package com.pennant.backend.dao.transactionmapping;

import com.pennant.backend.dao.impl.BasicCrudDao;
import com.pennanttech.pff.core.TableType;
import com.pennanttech.pff.mmfl.cd.model.TransactionMapping;

public interface TransactionMappingDAO extends BasicCrudDao<TransactionMapping> {

	boolean isDuplicateKey(TransactionMapping mapping, TableType tableType);

	TransactionMapping getTransactionMappingById(long id, String type);

	int getcountByMID(long mid, long tid);

	int getCountByPhoneAndStroeId(String mobileNumber, int storeId, int posId);

	int getCountByPhoneNumber(String mobileNumber);

}
