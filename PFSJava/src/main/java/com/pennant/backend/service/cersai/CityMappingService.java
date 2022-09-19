package com.pennant.backend.service.cersai;

import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.model.cersai.CityMapping;

public interface CityMappingService {

	AuditHeader saveOrUpdate(AuditHeader auditHeader);

	CityMapping getCityMapping(int mappingType, String cityCode, String mappingValue);

	CityMapping getApprovedCityMapping(int mappingType, String cityCode, String mappingValue);

	AuditHeader delete(AuditHeader auditHeader);

	AuditHeader doApprove(AuditHeader auditHeader);

	AuditHeader doReject(AuditHeader auditHeader);
}