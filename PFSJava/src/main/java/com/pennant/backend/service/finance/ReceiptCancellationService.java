package com.pennant.backend.service.finance;

import java.util.List;
import java.util.Map;

import com.pennant.app.core.CustEODEvent;
import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.model.finance.FinReceiptHeader;
import com.pennant.backend.model.financemanagement.PresentmentDetail;
import com.pennant.backend.model.rulefactory.ReturnDataSet;

public interface ReceiptCancellationService {

	FinReceiptHeader getFinReceiptHeaderById(long receiptID, boolean isFeePayment);

	AuditHeader saveOrUpdate(AuditHeader auditHeader);

	AuditHeader doApprove(AuditHeader auditHeader) throws Exception;

	AuditHeader doReject(AuditHeader auditHeader);

	List<ReturnDataSet> getPostingsByTranIdList(List<Long> tranIdList);

	List<ReturnDataSet> getPostingsByPostRef(long postRef);

	PresentmentDetail presentmentCancellation(PresentmentDetail pd, String returnCode, String bounceRemarks)
			throws Exception;

	Map<String, Object> getGLSubHeadCodes(long finID);

	// ### 16-12-2020, ST#1627
	AuditHeader doApproveNonLanReceipt(AuditHeader auditHeader) throws Exception;

	PresentmentDetail presentmentCancellation(PresentmentDetail pd, CustEODEvent custEODEvent) throws Exception;
}