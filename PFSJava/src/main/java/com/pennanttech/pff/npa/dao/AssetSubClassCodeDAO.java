package com.pennanttech.pff.npa.dao;

import java.util.List;

import com.pennant.backend.dao.impl.BasicCrudDao;
import com.pennanttech.pff.core.TableType;
import com.pennanttech.pff.npa.model.AssetSubClassCode;

public interface AssetSubClassCodeDAO extends BasicCrudDao<AssetSubClassCode> {

	AssetSubClassCode getAssetClassCode(long id, String type);

	boolean isDuplicateKey(long id, String code, TableType tableType);

	boolean checkUniqueKey(String code, long assetClassId, TableType type);

	boolean checkDependency(String code);

	boolean checkUniqueKey(String code, TableType type);

	List<String> getAssetClassCodes();

}