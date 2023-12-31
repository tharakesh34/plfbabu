package com.pennant.pff.presentment.dao;

import java.util.List;

import com.pennant.backend.model.reports.ReportListDetail;
import com.pennant.pff.presentment.model.PresentmentExcludeCode;
import com.pennanttech.pennapps.jdbc.search.ISearch;
import com.pennanttech.pff.core.TableType;

public interface PresentmentExcludeCodeDAO {

	PresentmentExcludeCode getExcludeCode(long id);

	List<PresentmentExcludeCode> getPresentmentExcludeCodes(List<String> roleCodes);

	List<PresentmentExcludeCode> getResult(ISearch search);

	List<ReportListDetail> getPrintCodes(List<String> roleCodes);

	String save(PresentmentExcludeCode code, TableType type);

	void update(PresentmentExcludeCode code, TableType type);

	void delete(PresentmentExcludeCode code, TableType type);

	boolean isDuplicateKey(String excludeCode, String instrumentType, TableType type);
}
