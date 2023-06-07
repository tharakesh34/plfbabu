package com.pennanttech.pff.npa.dao;

import java.util.List;

import com.pennant.backend.dao.impl.BasicCrudDao;
import com.pennanttech.pff.core.TableType;
import com.pennanttech.pff.npa.model.AssetClassSetupDetail;
import com.pennanttech.pff.npa.model.AssetClassSetupHeader;

public interface AssetClassSetupDAO extends BasicCrudDao<AssetClassSetupHeader> {

	AssetClassSetupHeader getAssetClassSetupHeader(long id, String type);

	boolean isDuplicateKey(long id, String entityCode, TableType tableType);

	boolean isAssetEntityCodeExists(String entityCode, String code, TableType type);

	void updateDetail(AssetClassSetupDetail assetClassSetUpDetail, String type);

	long saveDetail(AssetClassSetupDetail assetClassSetUpDetail, String type);

	void deleteDetail(AssetClassSetupDetail assetClassSetUpDetail, String type);

	AssetClassSetupDetail getAssetClassSetupDetailByID(AssetClassSetupDetail assetClassSetUpDetail, String type);

	List<AssetClassSetupDetail> getAssetClassSetupDetailBySetupID(long id, String suffix);

	void deleteDetailBySetupID(long setupID, String type);

	List<AssetClassSetupHeader> getAssetClassSetups();

	void softDelete(long id, TableType tableType);

	boolean checkDependency(long assetClassSetupId);

	List<AssetClassSetupDetail> getAssetClassSetCodes(long finID);
}