package com.pennanttech.pff.mmfl.cd.service;

import com.pennant.backend.model.audit.AuditHeader;
import com.pennanttech.pff.mmfl.cd.model.SchemeProductGroup;

public interface SchemeProductGroupService {

	AuditHeader saveOrUpdate(AuditHeader auditHeader);

	SchemeProductGroup getSchemeProductGroup(long id);

	SchemeProductGroup getApprovedSchemeProductGroup(long id);

	AuditHeader delete(AuditHeader auditHeader);

	AuditHeader doApprove(AuditHeader auditHeader);

	AuditHeader doReject(AuditHeader auditHeader);

}
