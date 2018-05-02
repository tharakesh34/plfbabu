package com.pennanttech.pennapps.pff.verification.service;

import com.pennant.backend.model.audit.AuditHeader;
import com.pennanttech.pennapps.pff.verification.model.LegalVerification;

public interface LegalVerificationService {
	
	AuditHeader saveOrUpdate(AuditHeader auditHeader);

	LegalVerification getLegalVerification(long id);

	LegalVerification getApprovedLegalVerification(long id);

	AuditHeader delete(AuditHeader auditHeader);

	AuditHeader doApprove(AuditHeader auditHeader);

	AuditHeader doReject(AuditHeader auditHeader);

}
