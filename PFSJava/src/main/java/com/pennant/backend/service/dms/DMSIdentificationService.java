package com.pennant.backend.service.dms;

import java.util.List;

import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.model.documentdetails.DocumentDetails;

public interface DMSIdentificationService {
	public void identifyExternalDocument(AuditHeader auditHeader);

	public List<DocumentDetails> getDmsDocumentDetails(long dmsId);
}
