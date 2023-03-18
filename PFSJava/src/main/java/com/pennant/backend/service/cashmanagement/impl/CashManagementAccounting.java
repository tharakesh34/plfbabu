package com.pennant.backend.service.cashmanagement.impl;

import java.math.BigDecimal;
import java.util.Date;
import java.util.Map;

import org.apache.commons.lang.StringUtils;

import com.pennant.app.constants.AccountConstants;
import com.pennant.app.util.PostingsPreparationUtil;
import com.pennant.app.util.SysParamUtil;
import com.pennant.backend.dao.partnerbank.PartnerBankDAO;
import com.pennant.backend.model.partnerbank.PartnerBank;
import com.pennant.backend.model.rulefactory.AEAmountCodes;
import com.pennant.backend.model.rulefactory.AEEvent;

public class CashManagementAccounting {
	PostingsPreparationUtil postingsPreparationUtil;
	private PartnerBankDAO partnerBankDAO;

	public PostingsPreparationUtil getPostingsPreparationUtil() {
		return postingsPreparationUtil;
	}

	public void setPostingsPreparationUtil(PostingsPreparationUtil postingsPreparationUtil) {
		this.postingsPreparationUtil = postingsPreparationUtil;
	}

	public PartnerBankDAO getPartnerBankDAO() {
		return partnerBankDAO;
	}

	public void setPartnerBankDAO(PartnerBankDAO partnerBankDAO) {
		this.partnerBankDAO = partnerBankDAO;
	}

	public AEEvent generateAccounting(String eventCode, String userBranch, String postingBranch,
			BigDecimal transactionAmount, long partnerBankId, long requestId, String finReference, long receiptId) {

		// Reference
		StringBuilder buffer = new StringBuilder("CM_");
		buffer.append(eventCode);
		buffer.append(postingBranch);
		buffer.append(StringUtils.leftPad(String.valueOf(requestId), 10, "0"));

		AEEvent aeEvent = new AEEvent();
		if (receiptId > 0) { // Avance Requirement
			// aeEvent.setPostingId(receiptId);
			aeEvent.setPostRefId(receiptId);
		}
		aeEvent.setAeAmountCodes(new AEAmountCodes());
		aeEvent.setBranch(postingBranch);
		aeEvent.setCcy(SysParamUtil.getAppCurrency());

		if (StringUtils.isEmpty(finReference)) {
			aeEvent.setFinReference(StringUtils.left(buffer.toString(), 20));
		} else {
			aeEvent.setFinReference(finReference);
		}

		Date appDate = SysParamUtil.getAppDate();

		aeEvent.setPostingUserBranch(postingBranch);
		aeEvent.setAccountingEvent(eventCode);
		aeEvent.setValueDate(appDate);
		aeEvent.setPostDate(appDate);
		aeEvent.setPostingType(AccountConstants.ACCOUNT_EVENT_POSTINGTYPE_BRANCH);

		AEAmountCodes amountCodes = aeEvent.getAeAmountCodes();
		amountCodes.setPostingType(AccountConstants.ACCOUNT_EVENT_POSTINGTYPE_BRANCH);
		amountCodes.setUserBranch(userBranch);
		if (partnerBankId > 0) {
			PartnerBank partnerBank = getPartnerBankDAO().getPartnerBankById(partnerBankId, "");
			amountCodes.setPartnerBankAcType(partnerBank.getAcType());
			amountCodes.setPartnerBankAc(partnerBank.getAccountNo());
		}

		amountCodes.setTransfer(transactionAmount);

		Map<String, Object> dataMap = amountCodes.getDeclaredFieldValues();
		aeEvent.setDataMap(dataMap);
		aeEvent.getAcSetIDList().clear();
		aeEvent.getAcSetIDList().add(getAccountingSetId(eventCode));

		// Posting details calling
		return getPostingsPreparationUtil().postAccounting(aeEvent);
	}

	private long getAccountingSetId(String event) {
		String parmCode = "GL_CM_GLHEAD_" + event;
		String id = SysParamUtil.getValueAsString(parmCode);
		if (StringUtils.isNotBlank(id)) {
			return Long.parseLong(id);
		}

		return 0;
	}
}
