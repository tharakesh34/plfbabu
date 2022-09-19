package com.pennant.backend.dao.cersai;

import com.pennant.backend.dao.impl.BasicCrudDao;
import com.pennant.backend.model.cersai.ProvinceMapping;
import com.pennanttech.pff.core.TableType;

public interface ProvinceMappingDAO extends BasicCrudDao<ProvinceMapping> {

	/**
	 * Fetch the Record ProvinceMapping by key field
	 * 
	 * @param mappingType  mappingType of the ProvinceMapping.
	 * @param province     province of the ProvinceMapping.
	 * @param mappingValue mappingValue of the ProvinceMapping.
	 * @param tableType    The type of the table.
	 * @return ProvinceMapping
	 */
	ProvinceMapping getProvinceMapping(int mappingType, String province, String mappingValue, String type);

	String save(ProvinceMapping provinceMapping, TableType tableType);

	void update(ProvinceMapping provinceMapping, TableType tableType);

	void delete(ProvinceMapping provinceMapping, TableType tableType);

	boolean isDuplicateKey(int mappingType, String province, TableType tableType);

}