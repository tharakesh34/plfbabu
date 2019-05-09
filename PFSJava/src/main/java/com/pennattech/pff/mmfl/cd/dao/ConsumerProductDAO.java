package com.pennattech.pff.mmfl.cd.dao;

import com.pennant.backend.dao.impl.BasicCrudDao;
import com.pennanttech.pff.core.TableType;
import com.pennanttech.pff.mmfl.cd.model.ConsumerProduct;

public interface ConsumerProductDAO extends BasicCrudDao<ConsumerProduct> {

	ConsumerProduct getConsumerProduct(long id, String type);

	boolean isDuplicateKey(ConsumerProduct commodityType, TableType tableType);

}
