package com.pennant.backend.service.applicationmaster;

import com.pennant.backend.model.applicationmaster.PresentmentReasonCode;
import com.pennant.backend.model.audit.AuditHeader;

public interface PresentmentReasonCodeService {
	AuditHeader saveOrUpdate(AuditHeader auditHeader);
	PresentmentReasonCode getPresentmentReasonCodeById(String id);
	PresentmentReasonCode getApprovedPresentmentReasonCodeById(String id);
	AuditHeader delete(AuditHeader auditHeader);
	AuditHeader doApprove(AuditHeader auditHeader);
	AuditHeader doReject(AuditHeader auditHeader);
}
