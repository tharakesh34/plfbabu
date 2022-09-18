package com.pennant.backend.service.cersai;

import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.model.cersai.SecurityInterestType;

public interface SecurityInterestTypeService {

	AuditHeader saveOrUpdate(AuditHeader auditHeader);

	SecurityInterestType getSecurityInterestType(String assetCategoryId, int id);

	SecurityInterestType getApprovedSecurityInterestType(String assetCategoryId, int id);

	AuditHeader delete(AuditHeader auditHeader);

	AuditHeader doApprove(AuditHeader auditHeader);

	AuditHeader doReject(AuditHeader auditHeader);
}