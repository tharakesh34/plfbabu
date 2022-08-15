package com.pennattech.pff.cd.dao;

import java.util.List;

import com.pennant.backend.dao.impl.BasicCrudDao;
import com.pennanttech.pff.cd.model.SchemeProductGroup;
import com.pennanttech.pff.core.TableType;

public interface SchemeProductGroupDAO extends BasicCrudDao<SchemeProductGroup> {

	SchemeProductGroup getSchemeProductGroup(long id, String type);

	boolean isDuplicateKey(SchemeProductGroup schemeProductGroup, TableType tableType);

	long getGrpIdSeq();

	void saveProductGrpBatch(List<SchemeProductGroup> schemeProductGroupList, TableType tempTab);

}
