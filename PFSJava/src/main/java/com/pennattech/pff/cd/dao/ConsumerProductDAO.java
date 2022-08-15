package com.pennattech.pff.cd.dao;

import com.pennant.backend.dao.impl.BasicCrudDao;
import com.pennanttech.pff.cd.model.ConsumerProduct;
import com.pennanttech.pff.core.TableType;

public interface ConsumerProductDAO extends BasicCrudDao<ConsumerProduct> {

	ConsumerProduct getConsumerProduct(long id, String type);

	boolean isDuplicateKey(ConsumerProduct commodityType, TableType tableType);

}
