package com.pennant.backend.service.systemmasters;

import java.util.List;

import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.model.systemmasters.ProjectUnits;

public interface ProjectUnitsService {
	AuditHeader saveOrUpdate(AuditHeader auditHeader);

	List<ProjectUnits> getProjectUnitsByProjectID(long projectID);

	ProjectUnits getApprovedProjectUnitsByID(long unitID);

	AuditHeader delete(AuditHeader auditHeader);

	AuditHeader doApprove(AuditHeader auditHeader);

	AuditHeader doReject(AuditHeader auditHeader);
}
