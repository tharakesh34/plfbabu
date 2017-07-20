package com.pennant.backend.service.finance;

import java.lang.reflect.InvocationTargetException;

import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.model.finance.FinReceiptHeader;
import com.pennanttech.pennapps.core.InterfaceException;

public interface ReceiptRealizationService {

	FinReceiptHeader getFinReceiptHeaderById(long receiptID, String type);
	AuditHeader saveOrUpdate(AuditHeader auditHeader) throws InterfaceException, IllegalAccessException, InvocationTargetException;
	AuditHeader doApprove(AuditHeader auditHeader) throws InterfaceException, IllegalAccessException, InvocationTargetException;
	AuditHeader doReject(AuditHeader auditHeader) throws InterfaceException;

}
