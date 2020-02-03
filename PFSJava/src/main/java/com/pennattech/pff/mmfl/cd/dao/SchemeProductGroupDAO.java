package com.pennattech.pff.mmfl.cd.dao;

import java.util.List;

import com.pennant.backend.dao.impl.BasicCrudDao;
import com.pennanttech.pff.core.TableType;
import com.pennanttech.pff.mmfl.cd.model.SchemeProductGroup;

public interface SchemeProductGroupDAO extends BasicCrudDao<SchemeProductGroup> {

	SchemeProductGroup getSchemeProductGroup(long id, String type);

	boolean isDuplicateKey(SchemeProductGroup schemeProductGroup, TableType tableType);

	long getGrpIdSeq();

	void saveProductGrpBatch(List<SchemeProductGroup> schemeProductGroupList, TableType tempTab);

}
