package com.pennant.backend.service.finance;

import java.lang.reflect.InvocationTargetException;

import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.model.finance.FinanceDetail;
import com.pennanttech.pennapps.core.InterfaceException;


public interface RepaymentCancellationService {

	FinanceDetail getFinanceDetailById(String finReference, String type);

	AuditHeader doApprove(AuditHeader aAuditHeader) throws InterfaceException, IllegalAccessException, InvocationTargetException;

	AuditHeader doReject(AuditHeader auditHeader);

	AuditHeader saveOrUpdate(AuditHeader aAuditHeader) throws InterfaceException, IllegalAccessException, InvocationTargetException;

}
