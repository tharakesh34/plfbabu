package com.pennant.backend.service.finance;

import java.lang.reflect.InvocationTargetException;

import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.model.finance.FinReceiptHeader;
import com.pennant.exception.PFFInterfaceException;

public interface ReceiptCancellationService {

	FinReceiptHeader getFinReceiptHeaderById(long receiptID, String type);
	AuditHeader saveOrUpdate(AuditHeader auditHeader) throws PFFInterfaceException, IllegalAccessException, InvocationTargetException;
	AuditHeader doApprove(AuditHeader auditHeader) throws PFFInterfaceException, IllegalAccessException, InvocationTargetException;
	AuditHeader doReject(AuditHeader auditHeader) throws PFFInterfaceException;

}
