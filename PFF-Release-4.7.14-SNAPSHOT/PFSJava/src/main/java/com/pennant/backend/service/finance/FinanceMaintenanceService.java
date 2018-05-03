package com.pennant.backend.service.finance;

import java.lang.reflect.InvocationTargetException;

import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.model.finance.FinanceDetail;
import com.pennanttech.pennapps.core.InterfaceException;


public interface FinanceMaintenanceService {

	FinanceDetail getFinanceDetailById(String finReference, String type, String userRole, String procEdtEvent, String eventCode);

	AuditHeader saveOrUpdate(AuditHeader aAuditHeader) throws InterfaceException, IllegalAccessException, InvocationTargetException;

	AuditHeader doReject(AuditHeader auditHeader) throws InterfaceException, IllegalAccessException, InvocationTargetException;

	AuditHeader doApprove(AuditHeader aAuditHeader) throws InterfaceException, IllegalAccessException, InvocationTargetException;

}
