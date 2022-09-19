package com.pennant.backend.dao.cersai;

import com.pennant.backend.dao.impl.BasicCrudDao;
import com.pennant.backend.model.cersai.AssetCategory;
import com.pennanttech.pff.core.TableType;

public interface AssetCategoryDAO extends BasicCrudDao<AssetCategory> {

	/**
	 * Fetch the Record AssetCategory by key field
	 * 
	 * @param id        id of the AssetCategory.
	 * @param tableType The type of the table.
	 * @return AssetCategory
	 */
	AssetCategory getAssetCategory(int id, String type);

	boolean isDuplicateKey(int id, TableType tableType);
}