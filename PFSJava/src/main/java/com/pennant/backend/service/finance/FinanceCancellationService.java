package com.pennant.backend.service.finance;

import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.model.finance.FinanceDetail;


public interface FinanceCancellationService {

	FinanceDetail getFinanceDetailById(String finReference, String type, String userRole, String procEdtEvent);
	AuditHeader saveOrUpdate(AuditHeader aAuditHeader);
	AuditHeader doReject(AuditHeader auditHeader);
	AuditHeader doApprove(AuditHeader aAuditHeader);

}

