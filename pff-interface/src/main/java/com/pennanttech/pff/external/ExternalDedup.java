package com.pennanttech.pff.external;

import com.pennant.backend.model.audit.AuditHeader;

public interface ExternalDedup {
	
	public AuditHeader checkDedup(AuditHeader auditHeader);

}
