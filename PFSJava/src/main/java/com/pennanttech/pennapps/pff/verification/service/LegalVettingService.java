package com.pennanttech.pennapps.pff.verification.service;

import java.util.List;

import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.model.documentdetails.DocumentManager;
import com.pennanttech.pennapps.pff.verification.DocumentType;
import com.pennanttech.pennapps.pff.verification.model.LVDocument;
import com.pennanttech.pennapps.pff.verification.model.LegalVetting;
import com.pennanttech.pennapps.pff.verification.model.Verification;
import com.pennanttech.pff.core.TableType;

public interface LegalVettingService {

	AuditHeader saveOrUpdate(AuditHeader auditHeader);

	LegalVetting getLegalVetting(LegalVetting lv, String type);

	LegalVetting getApprovedLegalVetting(long verificationId, String documentSubId);

	AuditHeader delete(AuditHeader auditHeader);

	AuditHeader doApprove(AuditHeader auditHeader);

	AuditHeader doReject(AuditHeader auditHeader);

	void save(Verification verification, TableType tableType);

	void saveDocuments(List<LVDocument> lvDocuments, TableType tableType);

	void deleteDocuments(long verificationId, TableType tableType);

	LegalVetting getLVFromStage(long verificationId);

	List<LVDocument> getLVDocumentsFromStage(long verificationId);

	List<Long> getLegalVettingIds(List<Verification> verifications, String keyRef);

	DocumentManager getDocumentById(long id);

	List<LVDocument> getLVDocuments(String keyReference, int docTypeKey);

	boolean isVettingExists(long id);

	List<LegalVetting> getList(String keyReference);

	List<LVDocument> getLVDocuments(long id);

	List<LVDocument> getDocuments(String keyReference, TableType tableType, DocumentType documentType);

	boolean isCollateralDocumentsChanged(String collateralRef);
}
