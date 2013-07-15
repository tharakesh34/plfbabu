package com.pennant.backend.dao.audit;

import com.pennant.backend.model.audit.AuditHeader;

public interface AuditHeaderCustomerDAO {

	public AuditHeader getNewAuditHeader();
	public long addAudit(AuditHeader auditHeader);
}
