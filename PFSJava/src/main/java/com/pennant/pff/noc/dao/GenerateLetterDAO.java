package com.pennant.pff.noc.dao;

import java.util.List;

import com.pennant.backend.model.reports.ReportListDetail;
import com.pennant.pff.noc.model.GenerateLetter;
import com.pennanttech.pennapps.jdbc.search.ISearch;

public interface GenerateLetterDAO {

	List<GenerateLetter> getResult(ISearch searchFilters);

	List<ReportListDetail> getPrintLetters(List<String> workFlowRoles);

	GenerateLetter getLetter(long id);

	List<GenerateLetter> getGenerateLetters(List<String> getGenerateLetters);

	boolean isReferenceExist(String reference);
}