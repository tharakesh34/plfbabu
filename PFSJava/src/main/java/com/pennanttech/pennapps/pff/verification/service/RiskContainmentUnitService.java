package com.pennanttech.pennapps.pff.verification.service;

import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.model.documentdetails.DocumentManager;
import com.pennanttech.pennapps.pff.verification.model.RiskContainmentUnit;

public interface RiskContainmentUnitService {

	AuditHeader saveOrUpdate(AuditHeader auditHeader);

	RiskContainmentUnit getRiskContainmentUnit(RiskContainmentUnit rcu);

	RiskContainmentUnit getApprovedRiskContainmentUnit(long id, long documetId, String documentSubId);

	AuditHeader delete(AuditHeader auditHeader);

	AuditHeader doApprove(AuditHeader auditHeader);

	AuditHeader doReject(AuditHeader auditHeader);

	DocumentManager getDocumentById(Long docRefId);
	
}
