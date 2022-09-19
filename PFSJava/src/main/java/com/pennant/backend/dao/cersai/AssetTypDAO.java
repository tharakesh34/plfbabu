package com.pennant.backend.dao.cersai;

import com.pennant.backend.dao.impl.BasicCrudDao;
import com.pennant.backend.model.cersai.AssetTyp;
import com.pennanttech.pff.core.TableType;

public interface AssetTypDAO extends BasicCrudDao<AssetTyp> {

	/**
	 * Fetch the Record AssetType by key field
	 * 
	 * @param assetCategoryId assetCategoryId of the AssetType.
	 * @param id              id of the AssetType.
	 * @param tableType       The type of the table.
	 * @return AssetType
	 */
	AssetTyp getAssetTyp(String assetCategoryId, int id, String type);

	boolean isDuplicateKey(int id, TableType tableType);
}