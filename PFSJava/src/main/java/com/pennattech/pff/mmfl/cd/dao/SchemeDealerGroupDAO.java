package com.pennattech.pff.mmfl.cd.dao;

import com.pennant.backend.dao.impl.BasicCrudDao;
import com.pennanttech.pff.core.TableType;
import com.pennanttech.pff.mmfl.cd.model.SchemeDealerGroup;

public interface SchemeDealerGroupDAO extends BasicCrudDao<SchemeDealerGroup> {

	SchemeDealerGroup getSchemeDealerGroup(long id, String type);

	boolean isDuplicateKey(SchemeDealerGroup schemeDealerGroup, TableType tableType);

}
