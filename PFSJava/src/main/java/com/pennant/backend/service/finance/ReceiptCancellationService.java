package com.pennant.backend.service.finance;

import java.lang.reflect.InvocationTargetException;
import java.util.List;

import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.model.finance.FinReceiptHeader;
import com.pennant.backend.model.financemanagement.PresentmentDetail;
import com.pennant.backend.model.rulefactory.ReturnDataSet;
import com.pennanttech.pennapps.core.InterfaceException;

public interface ReceiptCancellationService {

	FinReceiptHeader getFinReceiptHeaderById(long receiptID, boolean isFeePayment);
	AuditHeader saveOrUpdate(AuditHeader auditHeader) throws InterfaceException, IllegalAccessException, InvocationTargetException;
	AuditHeader doApprove(AuditHeader auditHeader) throws InterfaceException, IllegalAccessException, InvocationTargetException;
	AuditHeader doReject(AuditHeader auditHeader) throws InterfaceException;
	List<ReturnDataSet> getPostingsByTranIdList(List<Long> tranIdList);
	PresentmentDetail presentmentCancellation(PresentmentDetail presentmentDetail, String returnCode) throws Exception;
}
