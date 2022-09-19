package com.pennant.backend.dao.cersai;

import com.pennant.backend.dao.impl.BasicCrudDao;
import com.pennant.backend.model.cersai.AreaUnit;
import com.pennanttech.pff.core.TableType;

public interface AreaUnitDAO extends BasicCrudDao<AreaUnit> {

	/**
	 * Fetch the Record AreaUnit by key field
	 * 
	 * @param id        id of the AreaUnit.
	 * @param tableType The type of the table.
	 * @return AreaUnit
	 */
	AreaUnit getAreaUnit(Long id, String type);

	boolean isDuplicateKey(long id, TableType tableType);
}