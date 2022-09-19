package com.pennant.backend.service.cersai;

import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.model.cersai.AssetCategory;

public interface AssetCategoryService {

	AuditHeader saveOrUpdate(AuditHeader auditHeader);

	AssetCategory getAssetCategory(int id);

	AssetCategory getApprovedAssetCategory(int id);

	AuditHeader delete(AuditHeader auditHeader);

	AuditHeader doApprove(AuditHeader auditHeader);

	AuditHeader doReject(AuditHeader auditHeader);
}