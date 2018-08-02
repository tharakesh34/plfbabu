package com.pennanttech.pff.organization.service;

import com.pennant.backend.model.audit.AuditHeader;
import com.pennanttech.pff.organization.model.Organization;

public interface OrganizationService {
	
	AuditHeader saveOrUpdate(AuditHeader auditHeader);

	Organization getOrganization(long id, String type);

	Organization getApprovedOrganization(long id);

	AuditHeader delete(AuditHeader auditHeader);

	AuditHeader doApprove(AuditHeader auditHeader);

	AuditHeader doReject(AuditHeader auditHeader);
}
