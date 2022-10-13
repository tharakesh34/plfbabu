package com.pennant.backend.service.cersai;

import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.model.cersai.SecurityInterestType;

public interface SecurityInterestTypeService {

	AuditHeader saveOrUpdate(AuditHeader auditHeader);

	SecurityInterestType getSecurityInterestType(Long assetCategoryId, int id);

	SecurityInterestType getApprovedSecurityInterestType(Long assetCategoryId, int id);

	AuditHeader delete(AuditHeader auditHeader);

	AuditHeader doApprove(AuditHeader auditHeader);

	AuditHeader doReject(AuditHeader auditHeader);
}