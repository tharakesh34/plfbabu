package com.pennant.backend.dao.systemmasters;

import com.pennant.backend.model.systemmasters.Qualification;
import com.pennanttech.pff.core.TableType;

public interface QualificationDAO {
	Qualification getQualificationById(String id, String type);

	boolean isDuplicateKey(String code, TableType tableType);

	String save(Qualification qualification, TableType tableType);

	void update(Qualification qualification, TableType tableType);

	void delete(Qualification qualification, TableType mainTab);
}
