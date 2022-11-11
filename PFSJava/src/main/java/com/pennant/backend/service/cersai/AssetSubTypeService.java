package com.pennant.backend.service.cersai;

import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.model.cersai.AssetSubType;

public interface AssetSubTypeService {

	AuditHeader saveOrUpdate(AuditHeader auditHeader);

	AssetSubType getAssetSubType(Long assetTypeId, int id);

	AssetSubType getApprovedAssetSubType(Long assetTypeId, int id);

	AuditHeader delete(AuditHeader auditHeader);

	AuditHeader doApprove(AuditHeader auditHeader);

	AuditHeader doReject(AuditHeader auditHeader);

	/**
	 * getCERSAI_AssetSubType fetch the details by using CERSAI_AssetSubTypeDAO's getCERSAI_AssetSubTypeById method.
	 * 
	 * @param assetTypeId assetTypeId of the AssetSubType.
	 * @param id          id of the AssetSubType.
	 * @return CERSAI_AssetSubType
	 */

}