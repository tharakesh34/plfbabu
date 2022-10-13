package com.pennant.backend.dao.cersai;

import com.pennant.backend.dao.impl.BasicCrudDao;
import com.pennant.backend.model.cersai.SecurityInterestType;
import com.pennanttech.pff.core.TableType;

public interface SecurityInterestTypeDAO extends BasicCrudDao<SecurityInterestType> {

	/**
	 * Fetch the Record SecurityInterestType by key field
	 * 
	 * @param assetCategoryId assetCategoryId of the SecurityInterestType.
	 * @param id              id of the SecurityInterestType.
	 * @param tableType       The type of the table.
	 * @return SecurityInterestType
	 */
	SecurityInterestType getSecurityInterestType(Long assetCategoryId, int id, String type);

	boolean isDuplicateKey(int id, TableType tableType);

}