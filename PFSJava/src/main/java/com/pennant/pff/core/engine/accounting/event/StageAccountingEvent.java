package com.pennant.pff.core.engine.accounting.event;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;

import com.pennant.app.constants.AccountConstants;
import com.pennant.app.util.AEAmounts;
import com.pennant.app.util.GSTCalculator;
import com.pennant.app.util.RuleExecutionUtil;
import com.pennant.backend.dao.finance.FinStageAccountingLogDAO;
import com.pennant.backend.dao.finance.FinanceProfitDetailDAO;
import com.pennant.backend.dao.lmtmasters.FinanceReferenceDetailDAO;
import com.pennant.backend.dao.rulefactory.PostingsDAO;
import com.pennant.backend.dao.rulefactory.RuleDAO;
import com.pennant.backend.model.finance.FinReceiptData;
import com.pennant.backend.model.finance.FinReceiptDetail;
import com.pennant.backend.model.finance.FinReceiptHeader;
import com.pennant.backend.model.finance.FinScheduleData;
import com.pennant.backend.model.finance.FinanceDetail;
import com.pennant.backend.model.finance.FinanceMain;
import com.pennant.backend.model.finance.FinanceProfitDetail;
import com.pennant.backend.model.finance.FinanceScheduleDetail;
import com.pennant.backend.model.rmtmasters.FinanceType;
import com.pennant.backend.model.rulefactory.AEAmountCodes;
import com.pennant.backend.model.rulefactory.AEEvent;
import com.pennant.backend.model.rulefactory.ReturnDataSet;
import com.pennant.backend.model.rulefactory.Rule;
import com.pennant.backend.service.fees.FeeDetailService;
import com.pennant.backend.util.FinanceConstants;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.RuleReturnType;
import com.pennant.pff.accounting.model.PostingDTO;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pff.constants.AccountingEvent;
import com.pennanttech.pff.constants.FinServiceEvent;
import com.pennanttech.pff.receipt.constants.ReceiptMode;

public class StageAccountingEvent extends PostingEvent {
	private FeeDetailService feeDetailService;
	private FinanceReferenceDetailDAO financeReferenceDetailDAO;
	private FinanceProfitDetailDAO profitDetailsDAO;
	private RuleDAO ruleDAO;
	private FinStageAccountingLogDAO finStageAccountingLogDAO;
	private PostingsDAO postingsDAO;

	@Override
	public List<AEEvent> prepareAEEvents(PostingDTO postingDTO) {
		List<AEEvent> events = new ArrayList<>();

		FinanceDetail fd = postingDTO.getFinanceDetail();

		FinReceiptData frd = postingDTO.getFinReceiptData();

		String roleCode = "";
		String nextRoleCode = "";

		BigDecimal tranAmount = BigDecimal.ZERO;
		String receiptNumber = null;
		String paymentType = null;
		String partnerBankAc = null;
		long postRef = 0;
		Date valueDate = postingDTO.getValueDate();

		if (frd != null) {
			Map<String, Object> executeMap = fd.getFinScheduleData().getFinanceMain().getDeclaredFieldValues();

			FinReceiptHeader rch = frd.getReceiptHeader();
			roleCode = rch.getRoleCode();
			nextRoleCode = StringUtils.trimToEmpty(rch.getNextRoleCode());

			String receiptMode = rch.getReceiptMode();
			for (FinReceiptDetail rcd : rch.getReceiptDetails()) {
				if (receiptMode.equals(rcd.getPaymentType()) && !ReceiptMode.EXCESS.equals(receiptMode)) {
					valueDate = rcd.getReceivedDate();
					tranAmount = rcd.getAmount();
					receiptNumber = rcd.getPaymentRef();
					partnerBankAc = rcd.getPartnerBankAcType();
					postRef = rcd.getReceiptID();
				}

				if (receiptMode.equals(rcd.getPaymentType())) {
					rcd.getDeclaredFieldValues(executeMap);
				}
			}

			paymentType = receiptMode;
		}

		FinScheduleData schdData = fd.getFinScheduleData();
		FinanceMain fm = schdData.getFinanceMain();
		FinanceType ft = schdData.getFinanceType();
		List<FinanceScheduleDetail> schedules = schdData.getFinanceScheduleDetails();

		if (nextRoleCode.equals(roleCode) || PennantConstants.RCD_STATUS_RESUBMITTED.equals(fm.getRecordStatus())) {
			logger.debug(Literal.LEAVING);
			return events;
		}

		Map<String, Object> executeMap = fm.getDeclaredFieldValues();

		ft.getDeclaredFieldValues(executeMap);

		String financeType = fm.getFinType();
		if (StringUtils.isNotBlank(fm.getPromotionCode())) {
			financeType = fm.getPromotionCode();
		}

		List<Long> list = financeReferenceDetailDAO.getRefIdListByRefType(financeType, fm.getRcdMaintainSts(), roleCode,
				FinanceConstants.PROCEDT_STAGEACC);

		if (CollectionUtils.isEmpty(list)) {
			logger.debug(Literal.LEAVING);
			return events;
		}

		List<Long> accIDs = new ArrayList<>();
		for (Long ruleID : list) {
			Rule rule = ruleDAO.getRuleByID(ruleID, roleCode);
			if (rule == null) {
				continue;
			}

			Integer id = (Integer) RuleExecutionUtil.executeRule(rule.getSQLRule(), executeMap, fm.getFinCcy(),
					RuleReturnType.INTEGER);

			if (id > 0) {
				accIDs.add(Long.valueOf(id));
			}
		}

		if (CollectionUtils.isEmpty(accIDs)) {
			logger.debug(Literal.LEAVING);
			return events;
		}

		Map<String, Object> gstExecutionMap = GSTCalculator.getGSTDataMap(fm.getFinID());

		FinanceProfitDetail fpd = new FinanceProfitDetail();

		if (!FinServiceEvent.ORG.equals(fd.getModuleDefiner())) {
			fpd = profitDetailsDAO.getFinProfitDetailsById(fm.getFinID());
		}

		AEEvent aeEvent = AEAmounts.procAEAmounts(fm, schedules, fpd, AccountingEvent.STAGE, valueDate, valueDate);

		AEAmountCodes codes = aeEvent.getAeAmountCodes();
		codes.setTransfer(tranAmount);
		codes.setPaymentType(paymentType);
		codes.setPartnerBankAcType(partnerBankAc);
		codes.setUserBranch(postingDTO.getUserBranch());

		aeEvent.getAcSetIDList().addAll(accIDs);
		aeEvent.setModuleDefiner(fd.getModuleDefiner());
		aeEvent.setEntityCode(fm.getLovDescEntityCode());
		aeEvent.setPostingUserBranch(postingDTO.getUserBranch());
		aeEvent.setPostDate(postingDTO.getValueDate());
		aeEvent.setPostRefId(postRef > 0 ? postRef : aeEvent.getPostRefId());

		Map<String, Object> feeMap = feeDetailService.prepareFeeRulesMap(codes, aeEvent.getDataMap(), fd);

		Map<String, Object> dataMap = codes.getDeclaredFieldValues(feeMap);

		if (MapUtils.isNotEmpty(gstExecutionMap)) {
			for (String key : gstExecutionMap.keySet()) {
				if (StringUtils.isNotBlank(key)) {
					dataMap.put(key, gstExecutionMap.get(key));
				}
			}
		}

		aeEvent.setDataMap(dataMap);

		events.add(aeEvent);

		return events;
	}

	@Override
	public boolean prvStageAccountingCheck(List<ReturnDataSet> list, PostingDTO postingDTO) {
		FinanceMain fm = postingDTO.getFinanceMain();

		long finID = fm.getFinID();
		String finEvent = fm.getRcdMaintainSts();
		String roleCode = "";

		long linkedTranId = finStageAccountingLogDAO.getLinkedTranId(finID, finEvent, roleCode);
		postingDTO.setLinkedTranID(linkedTranId);

		if (linkedTranId == 0) {
			return true;
		}

		List<ReturnDataSet> excdStageAccList = postingsDAO.getPostingsByLinkTransId(linkedTranId);

		for (ReturnDataSet returnDataSet : list) {
			returnDataSet.setLinkedTranId(linkedTranId);

			if (returnDataSet.getPostAmount().compareTo(BigDecimal.ZERO) < 0) {
				String tranCode = returnDataSet.getTranCode();
				String revTranCode = returnDataSet.getRevTranCode();
				String debitOrCredit = returnDataSet.getDrOrCr();

				returnDataSet.setTranCode(revTranCode);
				returnDataSet.setRevTranCode(tranCode);

				returnDataSet.setPostAmount(returnDataSet.getPostAmount().negate());

				if (debitOrCredit.equals(AccountConstants.TRANTYPE_CREDIT)) {
					returnDataSet.setDrOrCr(AccountConstants.TRANTYPE_DEBIT);
				} else {
					returnDataSet.setDrOrCr(AccountConstants.TRANTYPE_CREDIT);
				}
			}
		}

		Map<String, ReturnDataSet> excdMapDataSet = new HashMap<>();
		for (ReturnDataSet returnDataSet : excdStageAccList) {
			if (excdMapDataSet.containsKey(returnDataSet.getTranOrderId())) {
				excdMapDataSet.remove(returnDataSet.getTranOrderId());
			}
			excdMapDataSet.put(returnDataSet.getTranOrderId(), returnDataSet);
		}

		boolean reExecuteStgAc = false;
		for (ReturnDataSet set : list) {
			if (!excdMapDataSet.containsKey(set.getTranOrderId())) {
				reExecuteStgAc = true;
				break;
			}

			ReturnDataSet excdSet = excdMapDataSet.get(set.getTranOrderId());

			if (!StringUtils.equals(excdSet.getAccount(), set.getAccount())
					|| !StringUtils.equals(excdSet.getTranCode(), set.getTranCode())
					|| !StringUtils.equals(excdSet.getRevTranCode(), set.getRevTranCode())
					|| !StringUtils.equals(excdSet.getAcCcy(), set.getAcCcy())
					|| !StringUtils.equals(excdSet.getDrOrCr(), set.getDrOrCr())
					|| excdSet.getPostAmount().compareTo(set.getPostAmount()) != 0) {
				reExecuteStgAc = true;
				break;
			}
		}

		// Delete Entry log after Finance Stage Accounting Reversal on particular Stage/Role
		if (reExecuteStgAc) {
			finStageAccountingLogDAO.deleteByRefandRole(finID, finEvent, roleCode);
		}

		excdMapDataSet = null;
		return reExecuteStgAc;
	}

	@Autowired
	public void setFeeDetailService(FeeDetailService feeDetailService) {
		this.feeDetailService = feeDetailService;
	}

	@Autowired
	public void setFinanceReferenceDetailDAO(FinanceReferenceDetailDAO financeReferenceDetailDAO) {
		this.financeReferenceDetailDAO = financeReferenceDetailDAO;
	}

	@Autowired
	public void setProfitDetailsDAO(FinanceProfitDetailDAO profitDetailsDAO) {
		this.profitDetailsDAO = profitDetailsDAO;
	}

	@Autowired
	public void setRuleDAO(RuleDAO ruleDAO) {
		this.ruleDAO = ruleDAO;
	}

	@Autowired
	public void setFinStageAccountingLogDAO(FinStageAccountingLogDAO finStageAccountingLogDAO) {
		this.finStageAccountingLogDAO = finStageAccountingLogDAO;
	}

	@Autowired
	public void setPostingsDAO(PostingsDAO postingsDAO) {
		this.postingsDAO = postingsDAO;
	}

}
