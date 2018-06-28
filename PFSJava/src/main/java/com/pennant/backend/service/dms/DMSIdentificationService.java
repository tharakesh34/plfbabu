package com.pennant.backend.service.dms;

import java.util.List;

import com.pennant.backend.model.audit.AuditHeader;
import com.pennanttech.model.dms.DMSDocumentDetails;

public interface DMSIdentificationService {
	public void identifyExternalDocument(AuditHeader auditHeader);
	public List<DMSDocumentDetails> getDmsDocumentDetails(long dmsId);
}

