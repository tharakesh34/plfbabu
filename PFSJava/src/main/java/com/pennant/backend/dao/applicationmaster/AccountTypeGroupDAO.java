package com.pennant.backend.dao.applicationmaster;

import com.pennant.backend.dao.impl.BasicCrudDao;
import com.pennant.backend.model.applicationmaster.AccountTypeGroup;
import com.pennanttech.pff.core.TableType;

public interface AccountTypeGroupDAO extends BasicCrudDao<AccountTypeGroup> {
	
	AccountTypeGroup getAccountTypeGroupById(long id, String type);
	
	/**
	 * Checks whether another record exists with the key attributes in the specified table type.
	 * 
	 * @param groupCode
	 *            of AccountTypeGroup
	 * @param tableType
	 *            of PhoneType
	 * @return
	 */
	boolean isDuplicateKey(String groupCode, TableType tableType);

}
