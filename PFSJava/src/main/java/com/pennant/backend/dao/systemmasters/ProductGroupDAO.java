package com.pennant.backend.dao.systemmasters;

import com.pennant.backend.dao.impl.BasicCrudDao;
import com.pennant.backend.model.systemmasters.ProductGroup;
import com.pennanttech.pff.core.TableType;

public interface ProductGroupDAO extends BasicCrudDao<ProductGroup> {
	ProductGroup getProductGroup(long id, String type);

	boolean isDuplicateKey(long id, String name, TableType tableType);

	boolean isIdExists(long id);

}
