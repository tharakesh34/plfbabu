package com.pennant.backend.service.cersai;

import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.model.cersai.AssetSubType;

public interface AssetSubTypeService {

	AuditHeader saveOrUpdate(AuditHeader auditHeader);

	AssetSubType getAssetSubType(String assetTypeId, int id);

	AssetSubType getApprovedAssetSubType(String assetTypeId, int id);

	AuditHeader delete(AuditHeader auditHeader);

	AuditHeader doApprove(AuditHeader auditHeader);

	AuditHeader doReject(AuditHeader auditHeader);
}