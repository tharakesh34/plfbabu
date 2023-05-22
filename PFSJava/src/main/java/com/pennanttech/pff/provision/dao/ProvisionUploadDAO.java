package com.pennanttech.pff.provision.dao;

import java.util.List;

import com.pennanttech.pff.provision.model.ProvisionUpload;

public interface ProvisionUploadDAO {
	List<ProvisionUpload> getDetails(long id);

	void update(List<ProvisionUpload> details);

	void update(List<Long> headerIds, String errorCode, String errorDesc);

	String getSqlQuery();

	Long getAssetClassId(String assetClassCode);

	Long getAssetSubClassId(long assetClassId, String assetSubClassCode);

}
