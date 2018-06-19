package com.pennanttech.pff.external;

import com.pennant.backend.model.audit.AuditHeader;

public interface BlacklistCheck {

	public AuditHeader checkHunterDetails(AuditHeader auditHeader);
}
