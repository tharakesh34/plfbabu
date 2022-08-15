package com.pennattech.pff.cd.dao;

import java.util.List;

import com.pennant.backend.dao.impl.BasicCrudDao;
import com.pennanttech.pff.cd.model.SchemeDealerGroup;
import com.pennanttech.pff.core.TableType;

public interface SchemeDealerGroupDAO extends BasicCrudDao<SchemeDealerGroup> {

	SchemeDealerGroup getSchemeDealerGroup(long id, String type);

	boolean isDuplicateKey(SchemeDealerGroup schemeDealerGroup, TableType tableType);

	long getGrpIdSeq();

	void saveDealerGrpBatch(List<SchemeDealerGroup> sdgList, TableType tableType);

}
