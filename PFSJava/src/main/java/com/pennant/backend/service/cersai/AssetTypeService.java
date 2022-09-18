package com.pennant.backend.service.cersai;

import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.model.cersai.AssetTyp;

public interface AssetTypeService {

	AuditHeader saveOrUpdate(AuditHeader auditHeader);

	AssetTyp getAssetTyp(String assetCategoryId, int id);

	AssetTyp getApprovedAssetTyp(String assetCategoryId, int id);

	AuditHeader delete(AuditHeader auditHeader);

	AuditHeader doApprove(AuditHeader auditHeader);

	AuditHeader doReject(AuditHeader auditHeader);
}