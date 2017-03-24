package com.pennant.backend.service.finance;

import java.lang.reflect.InvocationTargetException;

import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.model.finance.FinanceDetail;
import com.pennant.exception.PFFInterfaceException;


public interface FinanceMaintenanceService {

	FinanceDetail getFinanceDetailById(String finReference, String type, String userRole, String procEdtEvent, String eventCode);

	AuditHeader saveOrUpdate(AuditHeader aAuditHeader) throws PFFInterfaceException;

	AuditHeader doReject(AuditHeader auditHeader) throws PFFInterfaceException;

	AuditHeader doApprove(AuditHeader aAuditHeader) throws PFFInterfaceException, IllegalAccessException, InvocationTargetException;

}
