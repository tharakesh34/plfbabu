package com.pennant.backend.service.applicationmaster;

import com.pennant.backend.model.administration.ReportingManager;
import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.audit.AuditHeader;

public interface ReportingManagerService {

	AuditHeader saveOrUpdate(AuditHeader auditHeader);

	ReportingManager getReportingManager(long Id);

	ReportingManager getApprovedReportingManager(long Id);

	AuditHeader delete(AuditHeader auditHeader);

	AuditHeader doApprove(AuditHeader auditHeader);

	AuditHeader doReject(AuditHeader auditHeader);

	AuditDetail validation(AuditDetail auditDetail, String usrLanguage);

	AuditHeader businessValidation(AuditHeader auditHeader, String method);

}
