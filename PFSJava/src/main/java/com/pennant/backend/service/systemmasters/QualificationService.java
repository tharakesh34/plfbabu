package com.pennant.backend.service.systemmasters;

import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.model.systemmasters.Qualification;

public interface QualificationService {
	AuditHeader saveOrUpdate(AuditHeader auditHeader);

	Qualification getQualificationById(String id);

	Qualification getApprovedQualificationById(String id);

	AuditHeader delete(AuditHeader auditHeader);

	AuditHeader doApprove(AuditHeader auditHeader);

	AuditHeader doReject(AuditHeader auditHeader);

}
