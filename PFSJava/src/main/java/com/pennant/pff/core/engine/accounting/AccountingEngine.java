package com.pennant.pff.core.engine.accounting;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.zkoss.util.resource.Labels;

import com.pennant.app.constants.ImplementationConstants;
import com.pennant.app.util.AccountEngineExecution;
import com.pennant.backend.dao.bmtmasters.AccountEngineEventDAO;
import com.pennant.backend.dao.finance.FinStageAccountingLogDAO;
import com.pennant.backend.dao.rulefactory.PostingsDAO;
import com.pennant.backend.model.bmtmasters.AccountEngineEvent;
import com.pennant.backend.model.finance.FinReceiptDetail;
import com.pennant.backend.model.finance.FinReceiptHeader;
import com.pennant.backend.model.finance.FinStageAccountingLog;
import com.pennant.backend.model.finance.FinanceMain;
import com.pennant.backend.model.rulefactory.AEEvent;
import com.pennant.backend.model.rulefactory.ReturnDataSet;
import com.pennant.backend.util.FinanceConstants;
import com.pennant.cache.util.AccountingConfigCache;
import com.pennant.pff.accounting.model.PostingDTO;
import com.pennant.pff.core.engine.accounting.event.PostingEvent;
import com.pennanttech.pennapps.core.AppException;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pff.constants.AccountingEvent;
import com.pennanttech.pff.receipt.constants.ReceiptMode;

public class AccountingEngine {
	private static final Logger logger = LogManager.getLogger(AccountingEngine.class);
	private static PostingEventFactory factory;
	private static PostingsDAO postingsDAO;
	private static AccountEngineExecution engineExecution;
	private static FinStageAccountingLogDAO finStageAccountingLogDAO;
	private static AccountEngineEventDAO accountEngineEventDAO;

	public static List<ReturnDataSet> execute(String accEvent, PostingDTO postingDTO) {
		List<ReturnDataSet> transactions = new ArrayList<>();

		List<AEEvent> events = executeEvent(accEvent, postingDTO);

		events.stream().forEach(aeEvent -> transactions.addAll(aeEvent.getReturnDataSet()));

		return transactions;
	}

	public static List<ReturnDataSet> post(String accEvent, PostingDTO postingDTO) {
		logger.info("Posting accounting tranactions for {} event .", accEvent);
		List<AEEvent> events = executeEvent(accEvent, postingDTO);

		List<ReturnDataSet> transactions = new ArrayList<>();

		if (events.isEmpty()) {
			return transactions;
		}

		events.stream().forEach(aeEvent -> {
			long linkedTranId = postingsDAO.getLinkedTransId();
			String acEvent = aeEvent.getAccountingEvent();
			logger.info("Posting accounting tranactions for {} event with LinkedTranId {}.", acEvent, linkedTranId);
			aeEvent.setLinkedTranId(linkedTranId);
			aeEvent.getReturnDataSet().stream().forEach(rds -> rds.setLinkedTranId(linkedTranId));
			transactions.addAll(aeEvent.getReturnDataSet());

		});

		if (AccountingEvent.STAGE.equals(accEvent)) {
			stageExecution(accEvent, postingDTO, events);
		}

		PostingEvent postingEvent = getPostingEvent(accEvent);
		postingEvent.setEventDetails(events, postingDTO);

		postingsDAO.saveBatch(transactions);

		logger.info("Posting accounting tranactions for {} event completed.", accEvent);
		return transactions;
	}

	public static List<ReturnDataSet> getReversalsByLinkedTranID(long linkedTranId) {
		long newLinkedTranID = postingsDAO.getLinkedTransId();

		List<ReturnDataSet> returnDataSets = postingsDAO.getPostingsByLinkTransId(linkedTranId);

		engineExecution.getReversePostings(returnDataSets, newLinkedTranID);

		return returnDataSets;
	}

	private static List<AEEvent> executeEvent(String eventName, PostingDTO postingDTO) {
		logger.info("Executing accounting tranactions for {} event started.", eventName);

		List<AEEvent> events = getPostingEvent(eventName).prepareAEEvents(postingDTO);

		events.stream().forEach(aeEvent -> {
			engineExecution.getAccEngineExecResults(aeEvent);

			if (!aeEvent.isPostingSucess()) {
				logger.warn("Executing accounting tranactions for {} event failed.", aeEvent.getErrorMessage());
				throw new AppException("Executing accounting tranactions for {} event failed.");
			}
		});

		logger.info("Executing accounting tranactions for {} event completed.", eventName);
		return events;
	}

	private static void stageExecution(String accEvent, PostingDTO postingDTO, List<AEEvent> events) {
		long linkedTranID = postingDTO.getLinkedTranID();

		List<ReturnDataSet> transactions = new ArrayList<>();
		events.stream().forEach(aeEvent -> {
			engineExecution.getAccEngineExecResults(aeEvent);

			if (!getPostingEvent(accEvent).prvStageAccountingCheck(aeEvent.getReturnDataSet(), postingDTO)) {
				logger.debug(Labels.getLabel("label_Finance_Recal_StageAccountings"));
				logger.debug(Literal.LEAVING);
			}

			if (linkedTranID > 0) {
				getReversalsByLinkedTranID(linkedTranID);
			}

			transactions.addAll(aeEvent.getReturnDataSet());
		});

		PostingEvent postingEvent = getPostingEvent(accEvent);
		postingEvent.setEventDetails(events, postingDTO);

		postingsDAO.saveBatch(transactions);

		FinanceMain fm = postingDTO.getFinanceMain();

		String receiptNumber = null;

		String roleCode = "";
		if (postingDTO.getFinReceiptData() != null) {
			FinReceiptHeader rch = postingDTO.getFinReceiptData().getReceiptHeader();
			String receiptMode = rch.getReceiptMode();

			roleCode = rch.getRoleCode();
			for (FinReceiptDetail rcd : rch.getReceiptDetails()) {
				if (receiptMode.equals(rcd.getPaymentType()) && !ReceiptMode.EXCESS.equals(receiptMode)) {
					receiptNumber = rcd.getPaymentRef();
				}

				if (receiptMode.equals(rcd.getPaymentType())) {
					rcd.getDeclaredFieldValues(fm.getDeclaredFieldValues());
				}
			}

		}

		FinStageAccountingLog fsa = new FinStageAccountingLog();

		fsa.setFinID(fm.getFinID());
		fsa.setFinReference(fm.getFinReference());
		fsa.setFinEvent(fm.getRcdMaintainSts());
		fsa.setRoleCode(roleCode);
		fsa.setLinkedTranId(linkedTranID);
		fsa.setReceiptNo(receiptNumber);

		finStageAccountingLogDAO.saveStageAccountingLog(fsa);
	}

	public static void cancelStageAccounting(long finID, String finEvent) {
		List<Long> excdTranIdList = finStageAccountingLogDAO.getLinkedTranIdList(finID, finEvent);

		for (Long linkedTranId : excdTranIdList) {
			getReversalsByLinkedTranID(linkedTranId);
			logger.debug("Reverse Transaction Success for Transaction ID : " + linkedTranId);
		}
	}

	public static Long getAccountSetID(String finType, String eventCode, int moduleID) {
		FinanceMain fm = new FinanceMain();
		fm.setFinType(finType);

		return getAccountSetID(fm, eventCode, moduleID);
	}

	public static Long getAccountSetID(FinanceMain fm, String eventCode) {
		int moduleID = FinanceConstants.MODULEID_FINTYPE;

		if (StringUtils.isNotBlank(fm.getPromotionCode())) {
			moduleID = FinanceConstants.MODULEID_PROMOTION;
		}

		return getAccountSetID(fm, eventCode, moduleID);
	}

	public static Long getAccountSetID(FinanceMain fm, String eventCode, int moduleID) {
		String derivedEventCode = null;

		if (fm.isUnderSettlement()) {
			derivedEventCode = eventCode + "_S";
		} else if (fm.isUnderNpa()) {
			derivedEventCode = eventCode + "_N";
		} else if (fm.isWifLoan()) {
			derivedEventCode = eventCode + "_W";
		}

		Long accountSetID = null;

		if (derivedEventCode != null) {
			accountSetID = AccountingConfigCache.getCacheAccountSetID(fm.getFinType(), derivedEventCode, moduleID);
		}

		if (accountSetID == null || accountSetID <= 0) {
			accountSetID = AccountingConfigCache.getCacheAccountSetID(fm.getFinType(), eventCode, moduleID);
		}

		return accountSetID;
	}

	public static List<AccountEngineEvent> getEvents() {
		List<AccountEngineEvent> list = accountEngineEventDAO.getAccountEngineEvents();

		List<AccountEngineEvent> tempList = list
				.stream().filter(aeEvent -> aeEvent.getAEEventCode().endsWith("_S")
						|| aeEvent.getAEEventCode().endsWith("_N") || aeEvent.getAEEventCode().endsWith("_W"))
				.collect(Collectors.toList());

		list.removeAll(tempList);

		return list;
	}

	public static List<AccountEngineEvent> getServicingEvents() {
		List<AccountEngineEvent> list = getEvents();

		List<AccountEngineEvent> tempList = list.stream()
				.filter(aeEvent -> aeEvent.getAEEventCode().equals(AccountingEvent.ADDDBSP))
				.collect(Collectors.toList());

		if (!ImplementationConstants.ALLOW_ADDDBSF) {
			tempList = list.stream().filter(aeEvent -> aeEvent.getAEEventCode().equals(AccountingEvent.ADDDBSF))
					.collect(Collectors.toList());
		}

		list.removeAll(tempList);

		return list;
	}

	public static List<AccountEngineEvent> getOrginationEvents() {
		List<AccountEngineEvent> list = new ArrayList<>();
		List<AccountEngineEvent> tempList = getEvents();

		for (AccountEngineEvent aevent : tempList) {
			if (aevent.getAEEventCode().equals(AccountingEvent.ADDDBSP)) {
				list.add(aevent);
			}

			if (ImplementationConstants.ALLOW_ADDDBSF) {
				if (aevent.getAEEventCode().equals(AccountingEvent.ADDDBSN) || aevent.equals(AccountingEvent.ADDDBSF)) {
					list.add(aevent);
				}
			}
		}

		return list;
	}

	public static List<AccountEngineEvent> getOverDraftEvents() {
		List<AccountEngineEvent> list = new ArrayList<>();
		List<AccountEngineEvent> tempList = getEvents();

		for (AccountEngineEvent aevent : tempList) {
			if (aevent.getAEEventCode().equals(AccountingEvent.CMTDISB)) {
				list.add(aevent);
			}
		}

		return list;
	}

	private static PostingEvent getPostingEvent(String eventName) {
		return factory.getAccountingEventEvent(eventName);
	}

	public static void setFactory(PostingEventFactory factory) {
		AccountingEngine.factory = factory;
	}

	public static void setPostingsDAO(PostingsDAO postingsDAO) {
		AccountingEngine.postingsDAO = postingsDAO;
	}

	public static void setEngineExecution(AccountEngineExecution engineExecution) {
		AccountingEngine.engineExecution = engineExecution;
	}

	public static void setFinStageAccountingLogDAO(FinStageAccountingLogDAO finStageAccountingLogDAO) {
		AccountingEngine.finStageAccountingLogDAO = finStageAccountingLogDAO;
	}

	public static void setAccountEngineEventDAO(AccountEngineEventDAO accountEngineEventDAO) {
		AccountingEngine.accountEngineEventDAO = accountEngineEventDAO;
	}

}