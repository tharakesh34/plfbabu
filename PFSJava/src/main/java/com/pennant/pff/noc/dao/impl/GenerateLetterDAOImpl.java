package com.pennant.pff.noc.dao.impl;

import java.util.List;

import com.pennant.backend.model.reports.ReportListDetail;
import com.pennant.pff.noc.dao.GenerateLetterDAO;
import com.pennant.pff.noc.model.GenerateLetter;
import com.pennanttech.pennapps.core.jdbc.SequenceDao;
import com.pennanttech.pennapps.jdbc.search.ISearch;

public class GenerateLetterDAOImpl extends SequenceDao<GenerateLetter> implements GenerateLetterDAO {

	public GenerateLetterDAOImpl() {
		super();
	}

	@Override
	public List<GenerateLetter> getResult(ISearch searchFilters) {
		return null;
	}

	@Override
	public List<ReportListDetail> getPrintLetters(List<String> workFlowRoles) {
		return null;
	}

	@Override
	public GenerateLetter getLetter(long id) {
		return null;
	}

	@Override
	public List<GenerateLetter> getGenerateLetters(List<String> getGenerateLetters) {
		return null;
	}

	@Override
	public boolean isReferenceExist(String reference) {
		return false;
	}

}
