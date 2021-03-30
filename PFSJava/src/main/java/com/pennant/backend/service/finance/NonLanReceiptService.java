package com.pennant.backend.service.finance;

import java.util.List;

import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.model.finance.FinReceiptData;
import com.pennant.backend.model.finance.FinReceiptHeader;
import com.pennant.backend.model.finance.FinScheduleData;
import com.pennant.backend.model.finance.FinanceDetail;
import com.pennant.backend.model.rulefactory.ReturnDataSet;

public interface NonLanReceiptService {
	FinReceiptHeader getNonLanFinReceiptHeaderById(long receiptID, boolean isFeePayment, String type);

	AuditHeader saveOrUpdate(AuditHeader aAuditHeader);

	AuditHeader doReject(AuditHeader auditHeader);

	AuditHeader doReversal(AuditHeader auditHeader);

	AuditHeader doApprove(AuditHeader aAuditHeader) throws Exception;

	void saveMultiReceipt(AuditHeader auditHeader) throws Exception;

	FinReceiptData doReceiptValidations(FinanceDetail financeDetail, String method);

	FinReceiptData doBasicValidations(FinReceiptData receiptData);

	FinReceiptData doDataValidations(FinReceiptData receiptData);

	FinReceiptData doFunctionalValidations(FinReceiptData receiptData);

	FinReceiptData doBusinessValidations(FinReceiptData receiptData);

	FinScheduleData setErrorToFSD(FinScheduleData finScheduleData, String errorCode, String parm0);

	FinScheduleData setErrorToFSD(FinScheduleData finScheduleData, String errorCode, String parm0, String parm1);

	FinScheduleData setErrorToFSD(FinScheduleData finScheduleData, String errorCode, String parm0, String parm1,
			String parm2);

	FinScheduleData setErrorToFSD(FinScheduleData finScheduleData, String errorCode, String parm0, String parm1,
			String parm2, String parm3);

	FinReceiptData setReceiptDetail(FinReceiptData receiptData);

	FinReceiptData setReceiptData(FinReceiptData receiptData);

	List<ReturnDataSet> getPostingsByPostRefAndFinEvent(String postRef, String finEvent);

	public void processCollectionAPILog() throws Exception;
}
