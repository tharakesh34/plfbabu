package com.pennant.backend.service.financemanagement;

import java.lang.reflect.InvocationTargetException;
import java.util.List;

import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.model.finance.FinanceSuspHead;
import com.pennanttech.pennapps.core.InterfaceException;

public interface SuspenseService {
	
	FinanceSuspHead getFinanceSuspHead();
	FinanceSuspHead getNewFinanceSuspHead();
	FinanceSuspHead getFinanceSuspHeadById(String finRef, boolean isEnquiry,String userRole, String procEdtEvent);
	List<String> getSuspFinanceList();
	AuditHeader saveOrUpdate(AuditHeader auditHeader) throws InterfaceException, IllegalAccessException, InvocationTargetException;
	AuditHeader delete(AuditHeader auditHeader);
	AuditHeader doApprove(AuditHeader auditHeader) throws InterfaceException, IllegalAccessException, InvocationTargetException;
	AuditHeader doReject(AuditHeader auditHeader) throws InterfaceException, IllegalAccessException, InvocationTargetException;
	
}
