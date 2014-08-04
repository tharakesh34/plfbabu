package com.pennant.backend.service.financemanagement;

import java.lang.reflect.InvocationTargetException;
import java.util.List;

import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.model.finance.FinanceSuspHead;
import com.pennant.coreinterface.exception.AccountNotFoundException;

public interface SuspenseService {
	
	FinanceSuspHead getFinanceSuspHead();
	FinanceSuspHead getNewFinanceSuspHead();
	FinanceSuspHead refresh(FinanceSuspHead suspHead);
	FinanceSuspHead getFinanceSuspHeadById(String finRef, boolean isEnquiry);
	List<String> getSuspFinanceList();
	AuditHeader saveOrUpdate(AuditHeader auditHeader) throws AccountNotFoundException, IllegalAccessException, InvocationTargetException;
	AuditHeader delete(AuditHeader auditHeader);
	AuditHeader doApprove(AuditHeader auditHeader) throws AccountNotFoundException, IllegalAccessException, InvocationTargetException;
	AuditHeader doReject(AuditHeader auditHeader);
	
}
