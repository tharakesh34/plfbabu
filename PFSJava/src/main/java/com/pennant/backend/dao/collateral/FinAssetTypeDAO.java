package com.pennant.backend.dao.collateral;

import java.util.List;

import com.pennant.backend.model.finance.FinAssetTypes;

public interface FinAssetTypeDAO {

	void save(FinAssetTypes finAssetTypes, String type);

	void update(FinAssetTypes finAssetTypes, String type);

	List<FinAssetTypes> getFinAssetTypesByFinRef(String reference, String type);

	FinAssetTypes getFinAssetTypesbyID(FinAssetTypes finAssetTypes, String type);

	void delete(FinAssetTypes finAssetTypes, String type);
	
	void deleteByReference(String reference, String type);

}
