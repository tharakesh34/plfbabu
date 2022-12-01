package com.pennant.backend.dao.cersai;

import com.pennant.backend.dao.impl.BasicCrudDao;
import com.pennant.backend.model.cersai.AssetSubType;
import com.pennanttech.pff.core.TableType;

public interface AssetSubTypeDAO extends BasicCrudDao<AssetSubType> {

	/**
	 * Fetch the Record AssetSubType by key field
	 * 
	 * @param assetTypeId assetTypeId of the AssetSubType.
	 * @param id          id of the AssetSubType.
	 * @param tableType   The type of the table.
	 * @return AssetSubType
	 */
	AssetSubType getAssetSubType(Long assetTypeId, int id, String type);

	boolean isDuplicateKey(int id, TableType tableType);
}