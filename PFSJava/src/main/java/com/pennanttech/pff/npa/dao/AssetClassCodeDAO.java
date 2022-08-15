package com.pennanttech.pff.npa.dao;

import com.pennant.backend.dao.impl.BasicCrudDao;
import com.pennanttech.pff.core.TableType;
import com.pennanttech.pff.npa.model.AssetClassCode;

public interface AssetClassCodeDAO extends BasicCrudDao<AssetClassCode> {

	AssetClassCode getAssetClassCode(long id, String type);

	boolean isDuplicateKey(long id, String code, TableType tableType);

	boolean isAssetCodeExists(String code, TableType type);

	boolean checkDependency(String code);

}