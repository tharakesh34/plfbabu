package com.pennant.backend.dao.transactionmapping;

import com.pennant.backend.dao.impl.BasicCrudDao;
import com.pennanttech.pff.cd.model.TransactionMapping;
import com.pennanttech.pff.core.TableType;

public interface TransactionMappingDAO extends BasicCrudDao<TransactionMapping> {

	boolean isDuplicateKey(TransactionMapping mapping, TableType tableType);

	TransactionMapping getTransactionMappingById(long id, String type);

	int getcountByMID(long mid, String tid);

	int getCountByPhoneAndStroeId(String mobileNumber, int storeId, int posId);

	int getCountByPhoneNumber(String mobileNumber);

	TransactionMapping getDealerDetails(long mId, String tId);
}
