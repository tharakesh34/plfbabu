package com.pennant.backend.service.applicationmaster;

import com.pennant.backend.model.applicationmaster.PoliceCaseDetail;
import com.pennant.backend.model.audit.AuditHeader;

public interface PoliceCaseService {
	AuditHeader saveOrUpdate(AuditHeader auditHeader);
	PoliceCaseDetail getPoliceCaseDetailById(String id);
	PoliceCaseDetail getApprovedPoliceCaseDetailById(String id);
	AuditHeader delete(AuditHeader auditHeader);
	AuditHeader doApprove(AuditHeader auditHeader);
	AuditHeader doReject(AuditHeader auditHeader);
}
