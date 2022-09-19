package com.pennant.backend.dao.cersai;

import com.pennant.backend.dao.impl.BasicCrudDao;
import com.pennant.backend.model.cersai.DistrictMapping;
import com.pennanttech.pff.core.TableType;

public interface DistrictMappingDAO extends BasicCrudDao<DistrictMapping> {

	/**
	 * Fetch the Record DistrictMapping by key field
	 * 
	 * @param mappingType  mappingType of the DistrictMapping.
	 * @param district     district of the DistrictMapping.
	 * @param mappingValue mappingValue of the DistrictMapping.
	 * @param tableType    The type of the table.
	 * @return DistrictMapping
	 */
	DistrictMapping getDistrictMapping(int mappingType, String district, String mappingValue, String type);

	boolean isDuplicateKey(int mappingType, String district, TableType tableType);

}