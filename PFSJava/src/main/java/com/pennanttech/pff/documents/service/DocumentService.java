package com.pennanttech.pff.documents.service;

import com.pennant.backend.model.audit.AuditHeader;
import com.pennanttech.pff.documents.model.DocumentStatus;

public interface DocumentService {

	public DocumentStatus getDocumentStatus(String finReference);

	public AuditHeader delete(AuditHeader auditHeader);

	public AuditHeader saveOrUpdate(AuditHeader auditHeader);

	public AuditHeader doApprove(AuditHeader auditHeader);

	public AuditHeader doReject(AuditHeader auditHeader);

	int resetDocumentStatus(long docId);
}
