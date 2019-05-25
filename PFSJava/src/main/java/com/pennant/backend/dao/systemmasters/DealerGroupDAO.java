package com.pennant.backend.dao.systemmasters;

import com.pennant.backend.dao.impl.BasicCrudDao;
import com.pennant.backend.model.systemmasters.DealerGroup;
import com.pennanttech.pff.core.TableType;

public interface DealerGroupDAO extends BasicCrudDao<DealerGroup> {
	DealerGroup getDealerGroup(long id, String type);

	boolean isDuplicateKey(long id, String name, TableType tableType);

	boolean isIdExists(long id);

}
