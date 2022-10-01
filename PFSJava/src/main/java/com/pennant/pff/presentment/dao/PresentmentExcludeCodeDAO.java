package com.pennant.pff.presentment.dao;

import java.util.List;
import java.util.Map;

import com.pennant.pff.presentment.model.PresentmentExcludeCode;
import com.pennanttech.pennapps.jdbc.search.ISearch;
import com.pennanttech.pff.core.TableType;

public interface PresentmentExcludeCodeDAO {

	PresentmentExcludeCode getCode(String code);

	String save(PresentmentExcludeCode code, TableType type);

	void update(PresentmentExcludeCode code, TableType type);

	void delete(PresentmentExcludeCode code, TableType type);

	boolean isDuplicateKey(long id, TableType type);

	List<PresentmentExcludeCode> getBounceCodeById(Long id);

	List<PresentmentExcludeCode> getResult(ISearch search);

	Map<Integer, String> getBounceForPD();
}
