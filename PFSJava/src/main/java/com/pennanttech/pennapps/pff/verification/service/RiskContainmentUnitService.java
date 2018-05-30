package com.pennanttech.pennapps.pff.verification.service;

import java.util.List;

import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.model.documentdetails.DocumentManager;
import com.pennanttech.pennapps.pff.verification.DocumentType;
import com.pennanttech.pennapps.pff.verification.model.RCUDocument;
import com.pennanttech.pennapps.pff.verification.model.RiskContainmentUnit;
import com.pennanttech.pennapps.pff.verification.model.Verification;
import com.pennanttech.pff.core.TableType;

public interface RiskContainmentUnitService {

	AuditHeader saveOrUpdate(AuditHeader auditHeader);

	RiskContainmentUnit getRiskContainmentUnit(RiskContainmentUnit rcu, String type);

	RiskContainmentUnit getApprovedRiskContainmentUnit(long verificationId);

	AuditHeader delete(AuditHeader auditHeader);

	AuditHeader doApprove(AuditHeader auditHeader);

	AuditHeader doReject(AuditHeader auditHeader);

	DocumentManager getDocumentById(Long docRefId);

	List<Long> getRCUVerificaationIds(List<Verification> verifications, String keyRef);

	void save(Verification verification, TableType tableType);

	void saveDocuments(List<RCUDocument> rcuDocuments, TableType tableType);

	void deleteDocuments(long verificationId, TableType tableType);

	List<RCUDocument> getDocuments(String keyReference, TableType tableType, DocumentType documentType);

	RiskContainmentUnit getRiskContainmentUnit(long verificationId);

	void updateRemarks(Verification item);

	void updateRCUDocuments(Verification item);

	RCUDocument getRCUDocument(long verificationId, RCUDocument rcuDocument);
	
	void deleteRCUDocuments(RCUDocument rcuDocument, String tableType);
	
	int getRCUDocumentsCount(long verificationId, int documentType);
	
	void delete(long id, TableType tableType);
}
