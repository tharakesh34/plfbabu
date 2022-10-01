package com.pennant.backend.dao.applicationmaster;

import java.util.List;

import com.pennant.backend.dao.impl.BasicCrudDao;
import com.pennant.backend.model.applicationmaster.BounceCode;
import com.pennanttech.pennapps.jdbc.search.ISearch;
import com.pennanttech.pff.core.TableType;

public interface BounceCodeDao extends BasicCrudDao<BounceCode> {

	BounceCode getCode(String code);

	String save(BounceCode code, TableType type);

	void update(BounceCode code, TableType type);

	void delete(BounceCode code, TableType type);

	boolean isDuplicateKey(long id, TableType type);

	List<BounceCode> getBounceCodeById(Long Id);

	List<BounceCode> getResult(ISearch search);

}
