package com.pennant.backend.service.finance;

import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.model.finance.DepositDetails;

public interface DepositDetailsService {

	AuditHeader saveOrUpdate(AuditHeader auditHeader);
	DepositDetails getDepositDetailsById(long id);
	DepositDetails getApprovedDepositDetailsById(long id);
	AuditHeader delete(AuditHeader auditHeader);
	AuditHeader doApprove(AuditHeader auditHeader);
	AuditHeader doReject(AuditHeader auditHeader);
	
}
