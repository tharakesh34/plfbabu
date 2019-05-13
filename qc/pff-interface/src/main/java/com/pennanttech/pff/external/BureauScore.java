package com.pennanttech.pff.external;

import com.pennant.backend.model.audit.AuditHeader;

public interface BureauScore {

	public AuditHeader executeBureau(AuditHeader auditHeader);
}
