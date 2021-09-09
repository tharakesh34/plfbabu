package com.pennant.backend.service.finance;

import com.pennant.backend.model.audit.AuditHeader;

public interface FinCollateralDelinkService {

	AuditHeader saveOrUpdate(AuditHeader auditHeader);

	AuditHeader delete(AuditHeader auditHeader);

	AuditHeader doApprove(AuditHeader auditHeader);

	AuditHeader doReject(AuditHeader auditHeader);

}
