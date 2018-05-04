package com.pennanttech.pennapps.pff.verification.service;

import java.util.List;

import com.pennant.backend.model.audit.AuditHeader;
import com.pennanttech.pennapps.pff.verification.model.LVDocument;
import com.pennanttech.pennapps.pff.verification.model.LegalVerification;
import com.pennanttech.pennapps.pff.verification.model.Verification;
import com.pennanttech.pff.core.TableType;

public interface LegalVerificationService {

	AuditHeader saveOrUpdate(AuditHeader auditHeader);

	LegalVerification getLegalVerification(long id);

	LegalVerification getApprovedLegalVerification(long id);

	AuditHeader delete(AuditHeader auditHeader);

	AuditHeader doApprove(AuditHeader auditHeader);

	AuditHeader doReject(AuditHeader auditHeader);

	long save(Verification verification, TableType tableType);

	void saveDocuments(List<LVDocument> lvDocuments, TableType tableType);

	void deleteDocuments(String reference, TableType tableType);

	LegalVerification getLVFromStage(long verificationId);
	
	List<LVDocument> getLVDocumentsFromStage(long verificationId);

	List<Long> getLegalVerficationIds(List<Verification> verifications, String keyRef);
}
