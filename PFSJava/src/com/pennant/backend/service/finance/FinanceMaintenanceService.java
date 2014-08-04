package com.pennant.backend.service.finance;

import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.model.finance.FinanceDetail;
import com.pennant.coreinterface.exception.AccountNotFoundException;


public interface FinanceMaintenanceService {

	FinanceDetail getFinanceDetailById(String finReference, String type);

	AuditHeader saveOrUpdate(AuditHeader aAuditHeader) throws AccountNotFoundException;

	AuditHeader doReject(AuditHeader auditHeader);

	AuditHeader doApprove(AuditHeader aAuditHeader) throws AccountNotFoundException;

	
}
