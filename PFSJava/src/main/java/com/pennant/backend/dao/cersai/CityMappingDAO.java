package com.pennant.backend.dao.cersai;

import com.pennant.backend.dao.impl.BasicCrudDao;
import com.pennant.backend.model.cersai.CityMapping;
import com.pennanttech.pff.core.TableType;

public interface CityMappingDAO extends BasicCrudDao<CityMapping> {

	/**
	 * Fetch the Record CityMapping by key field
	 * 
	 * @param mappingType  mappingType of the CityMapping.
	 * @param cityCode     cityCode of the CityMapping.
	 * @param mappingValue mappingValue of the CityMapping.
	 * @param tableType    The type of the table.
	 * @return CityMapping
	 */
	CityMapping getCityMapping(int mappingType, String cityCode, String mappingValue, String type);

	boolean isDuplicateKey(int mappingType, String cityCode, TableType tableType);
}