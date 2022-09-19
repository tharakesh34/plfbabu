package com.pennant.backend.service.cersai;

import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.model.cersai.AreaUnit;

public interface AreaUnitService {

	AuditHeader saveOrUpdate(AuditHeader auditHeader);

	AreaUnit getAreaUnit(Long id);

	AreaUnit getApprovedAreaUnit(Long id);

	AuditHeader delete(AuditHeader auditHeader);

	AuditHeader doApprove(AuditHeader auditHeader);

	AuditHeader doReject(AuditHeader auditHeader);
}