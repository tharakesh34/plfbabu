package com.pennant.backend.service.cersai;

import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.model.cersai.DistrictMapping;

public interface DistrictMappingService {

	AuditHeader saveOrUpdate(AuditHeader auditHeader);

	DistrictMapping getDistrictMapping(int mappingType, String district, String mappingValue);

	DistrictMapping getApprovedDistrictMapping(int mappingType, String district, String mappingValue);

	AuditHeader delete(AuditHeader auditHeader);

	AuditHeader doApprove(AuditHeader auditHeader);

	AuditHeader doReject(AuditHeader auditHeader);
}