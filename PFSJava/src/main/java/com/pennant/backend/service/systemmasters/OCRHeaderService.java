package com.pennant.backend.service.systemmasters;

import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.model.ocrmaster.OCRHeader;

public interface OCRHeaderService {

	AuditHeader saveOrUpdate(AuditHeader auditHeader);

	AuditHeader delete(AuditHeader auditHeader);

	OCRHeader getOCRHeader(long headerId);

	OCRHeader getApprovedOCRHeader(long headerId);

	AuditHeader doApprove(AuditHeader auditHeader);

	AuditHeader doReject(AuditHeader auditHeader);

	OCRHeader getOCRHeaderByOCRId(String ocrID, String type);
}
