package com.pennant.backend.service.finance;

import java.lang.reflect.InvocationTargetException;
import java.util.List;

import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.model.finance.FinReceiptHeader;
import com.pennant.backend.model.financemanagement.PresentmentDetail;
import com.pennant.backend.model.rulefactory.ReturnDataSet;
import com.pennant.exception.PFFInterfaceException;

public interface ReceiptCancellationService {

	FinReceiptHeader getFinReceiptHeaderById(long receiptID, boolean isFeePayment);
	AuditHeader saveOrUpdate(AuditHeader auditHeader) throws PFFInterfaceException, IllegalAccessException, InvocationTargetException;
	AuditHeader doApprove(AuditHeader auditHeader) throws PFFInterfaceException, IllegalAccessException, InvocationTargetException;
	AuditHeader doReject(AuditHeader auditHeader) throws PFFInterfaceException;
	List<ReturnDataSet> getPostingsByTranIdList(List<Long> tranIdList);
	PresentmentDetail presentmentCancellation(PresentmentDetail presentmentDetail, String returnCode) throws Exception;
}
