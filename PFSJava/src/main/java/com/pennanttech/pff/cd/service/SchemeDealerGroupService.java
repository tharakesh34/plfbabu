package com.pennanttech.pff.cd.service;

import com.pennant.backend.model.audit.AuditHeader;
import com.pennanttech.pff.cd.model.SchemeDealerGroup;

public interface SchemeDealerGroupService {

	AuditHeader saveOrUpdate(AuditHeader auditHeader);

	SchemeDealerGroup getSchemeDealerGroup(long id);

	SchemeDealerGroup getApprovedSchemeDealerGroup(long id);

	AuditHeader delete(AuditHeader auditHeader);

	AuditHeader doApprove(AuditHeader auditHeader);

	AuditHeader doReject(AuditHeader auditHeader);

}
