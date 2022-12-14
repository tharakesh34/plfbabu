package com.pennant.pff.core.engine.accounting;

import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.zkoss.util.resource.Labels;

import com.pennant.app.util.AccountEngineExecution;
import com.pennant.backend.dao.finance.FinStageAccountingLogDAO;
import com.pennant.backend.dao.rulefactory.PostingsDAO;
import com.pennant.backend.model.finance.FinReceiptDetail;
import com.pennant.backend.model.finance.FinReceiptHeader;
import com.pennant.backend.model.finance.FinStageAccountingLog;
import com.pennant.backend.model.finance.FinanceMain;
import com.pennant.backend.model.rulefactory.AEEvent;
import com.pennant.backend.model.rulefactory.ReturnDataSet;
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

}
