package com.pennant.pff.settlement.service;

import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.pff.settlement.model.SettlementTypeDetail;

public interface SettlementTypeDetailService {

	SettlementTypeDetail getSettlementById(long id);

	SettlementTypeDetail getSettlementByCode(String code);

	AuditHeader delete(AuditHeader auditHeader);

	AuditHeader saveOrUpdate(AuditHeader auditHeader);

	AuditHeader doApprove(AuditHeader auditHeader);

	AuditHeader doReject(AuditHeader auditHeader);

}
