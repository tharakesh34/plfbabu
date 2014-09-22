package com.pennant.backend.dao.audit;

import com.pennant.backend.model.audit.AuditHeader;

public interface AuditHeaderCustomerDAO {

	AuditHeader getNewAuditHeader();
	long addAudit(AuditHeader auditHeader);
}
