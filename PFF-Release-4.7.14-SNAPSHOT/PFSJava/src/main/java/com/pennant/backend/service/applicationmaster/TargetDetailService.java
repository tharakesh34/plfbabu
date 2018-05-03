package com.pennant.backend.service.applicationmaster;

import com.pennant.backend.model.applicationmaster.TargetDetail;
import com.pennant.backend.model.audit.AuditHeader;

public interface TargetDetailService {
	AuditHeader saveOrUpdate(AuditHeader auditHeader);
	TargetDetail getTargetDetailById(String id);
	TargetDetail getApprovedTargetDetailById(String id);
	AuditHeader delete(AuditHeader auditHeader);
	AuditHeader doApprove(AuditHeader auditHeader);
	AuditHeader doReject(AuditHeader auditHeader);
}
