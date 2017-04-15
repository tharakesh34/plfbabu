package com.pennant.backend.dao.applicationmaster;

import com.pennant.backend.model.applicationmaster.AccountTypeGroup;

public interface AccountTypeGroupDAO {
	
	AccountTypeGroup getAccountTypeGroupById(long id, String type);

	void update(AccountTypeGroup accountTypeGroup, String type);

	void delete(AccountTypeGroup accountTypeGroup, String type);

	long save(AccountTypeGroup accountTypeGroup, String type);
}
