package com.pennanttech.pff.external;

import com.pennant.backend.model.audit.AuditHeader;

public interface DMSIntegrationService {
	public AuditHeader insertExternalDocument(AuditHeader auditHeader) throws Exception;
}
