package com.pennant.backend.service.cersai;

import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.model.cersai.ProvinceMapping;

public interface ProvinceMappingService {

	AuditHeader saveOrUpdate(AuditHeader auditHeader);

	ProvinceMapping getProvinceMapping(int mappingType, String province, String mappingValue);

	ProvinceMapping getApprovedProvinceMapping(int mappingType, String province, String mappingValue);

	AuditHeader delete(AuditHeader auditHeader);

	AuditHeader doApprove(AuditHeader auditHeader);

	AuditHeader doReject(AuditHeader auditHeader);
}