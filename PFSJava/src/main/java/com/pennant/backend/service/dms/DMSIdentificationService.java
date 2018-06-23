package com.pennant.backend.service.dms;

import com.pennant.backend.model.audit.AuditHeader;

public interface DMSIdentificationService {
	public void identifyExternalDocument(AuditHeader auditHeader);
}

