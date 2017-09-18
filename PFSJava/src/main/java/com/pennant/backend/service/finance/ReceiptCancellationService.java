package com.pennant.backend.service.finance;

import java.util.List;

import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.model.finance.FinReceiptHeader;
import com.pennant.backend.model.financemanagement.PresentmentDetail;
import com.pennant.backend.model.rulefactory.ReturnDataSet;

public interface ReceiptCancellationService {

	FinReceiptHeader getFinReceiptHeaderById(long receiptID, boolean isFeePayment);
	AuditHeader saveOrUpdate(AuditHeader auditHeader) ;
	AuditHeader doApprove(AuditHeader auditHeader) ;
	AuditHeader doReject(AuditHeader auditHeader) ;
	List<ReturnDataSet> getPostingsByTranIdList(List<Long> tranIdList);
	PresentmentDetail presentmentCancellation(PresentmentDetail presentmentDetail, String returnCode) ;
}
