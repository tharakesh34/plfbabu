package com.pennant.backend.dao.dealermapping;

import com.pennant.backend.dao.impl.BasicCrudDao;
import com.pennant.backend.model.dealermapping.DealerMapping;
import com.pennanttech.pff.core.TableType;

public interface DealerMappingDAO extends BasicCrudDao<DealerMapping> {

	DealerMapping getDealerMappingById(long id, String type);

	boolean isDuplicateKey(DealerMapping dealerMapping, TableType tableType);

	long getDealerCode();

}
