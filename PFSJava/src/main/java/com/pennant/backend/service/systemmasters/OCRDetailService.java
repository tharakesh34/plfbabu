package com.pennant.backend.service.systemmasters;

import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.model.ocrmaster.OCRDetail;

public interface OCRDetailService {

	AuditHeader saveOrUpdate(AuditHeader auditHeader);

	OCRDetail getOCRDetailById(long detailID, int stepSequence, long headerID);

	OCRDetail getApprovedOCRDetailById(long detailID, int stepSequence, long headerID);

	AuditHeader delete(AuditHeader auditHeader);

	AuditHeader doApprove(AuditHeader auditHeader);

	AuditHeader doReject(AuditHeader auditHeader);

}
