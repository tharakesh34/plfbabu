package com.pennant.backend.dao.transactionmapping;

import com.pennant.backend.dao.impl.BasicCrudDao;
import com.pennant.backend.model.transactionmapping.TransactionMapping;
import com.pennanttech.pff.core.TableType;

public interface TransactionMappingDAO extends BasicCrudDao<TransactionMapping> {

	boolean isDuplicateKey(TransactionMapping mapping, TableType tableType);

	TransactionMapping getTransactionMappingById(long id, String type);

}
