package com.pennanttech.pennapps.pff.verification.service;

import java.util.List;

import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.model.documentdetails.DocumentManager;
import com.pennanttech.pennapps.pff.verification.model.RCUDocument;
import com.pennanttech.pennapps.pff.verification.model.RiskContainmentUnit;
import com.pennanttech.pennapps.pff.verification.model.Verification;
import com.pennanttech.pff.core.TableType;

public interface RiskContainmentUnitService {

	AuditHeader saveOrUpdate(AuditHeader auditHeader);

	RiskContainmentUnit getRiskContainmentUnit(RiskContainmentUnit rcu);

	RiskContainmentUnit getApprovedRiskContainmentUnit(long id, long documetId, String documentSubId);

	AuditHeader delete(AuditHeader auditHeader);

	AuditHeader doApprove(AuditHeader auditHeader);

	AuditHeader doReject(AuditHeader auditHeader);

	DocumentManager getDocumentById(Long docRefId);

	List<Long> getRCUVerificaationIds(List<Verification> verifications, String keyRef);

	void save(Verification verification, TableType tableType);

	void saveDocuments(List<RCUDocument> rcuDocuments, TableType tableType);
}
