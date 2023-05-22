package com.pennant.backend.service.finance;

import java.util.List;
import java.util.Map;

import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.model.finance.CustEODEvent;
import com.pennant.backend.model.finance.FinReceiptHeader;
import com.pennant.backend.model.finance.FinanceMain;
import com.pennant.backend.model.rulefactory.ReturnDataSet;
import com.pennanttech.pff.presentment.model.PresentmentDetail;

public interface ReceiptCancellationService {

	FinReceiptHeader getFinReceiptHeaderById(long receiptID, boolean isFeePayment);

	AuditHeader saveOrUpdate(AuditHeader auditHeader);

	AuditHeader doApprove(AuditHeader auditHeader);

	AuditHeader doReject(AuditHeader auditHeader);

	List<ReturnDataSet> getPostingsByTranIdList(List<Long> tranIdList);

	List<ReturnDataSet> getPostingsByPostRef(long postRef);

	PresentmentDetail presentmentCancellation(PresentmentDetail pd, String returnCode, String bounceRemarks);

	Map<String, Object> getGLSubHeadCodes(long finID);

	// ### 16-12-2020, ST#1627
	AuditHeader doApproveNonLanReceipt(AuditHeader auditHeader);

	PresentmentDetail presentmentCancellation(PresentmentDetail pd, CustEODEvent custEODEvent);

	FinanceMain getFinBasicDetails(String reference);
}