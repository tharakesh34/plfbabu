package com.pennant.backend.service.applicationmaster;

import com.pennant.backend.model.applicationmaster.SettlementTypeDetail;
import com.pennant.backend.model.audit.AuditHeader;

public interface SettlementTypeDetailService {

	SettlementTypeDetail getSettlementById(long id);

	SettlementTypeDetail getSettlementByCode(String Code);

	AuditHeader delete(AuditHeader auditHeader);

	AuditHeader saveOrUpdate(AuditHeader auditHeader);

	AuditHeader doApprove(AuditHeader auditHeader);

	AuditHeader doReject(AuditHeader auditHeader);

}
