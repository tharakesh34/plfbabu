package com.pennant.pff.noc.service.impl;

import java.util.List;

import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.model.reports.ReportListDetail;
import com.pennant.pff.noc.dao.GenerateLetterDAO;
import com.pennant.pff.noc.model.GenerateLetter;
import com.pennant.pff.noc.service.GenerateLetterService;
import com.pennanttech.pennapps.jdbc.search.ISearch;

public class GenerateLetterServiceImpl implements GenerateLetterService {

	public GenerateLetterServiceImpl() {
		super();
	}

	private GenerateLetterDAO generateLetterDAO;

	@Override
	public List<GenerateLetter> getResult(ISearch searchFilters) {
		return generateLetterDAO.getResult(searchFilters);
	}

	@Override
	public List<ReportListDetail> getPrintLetters(List<String> workFlowRoles) {
		return generateLetterDAO.getPrintLetters(workFlowRoles);
	}

	@Override
	public GenerateLetter getLetter(long id) {
		return generateLetterDAO.getLetter(id);
	}

	@Override
	public List<GenerateLetter> getGenerateLetters(List<String> getGenerateLetters) {
		return generateLetterDAO.getGenerateLetters(getGenerateLetters);
	}

	@Override
	public boolean isReferenceExist(String reference) {
		return generateLetterDAO.isReferenceExist(reference);
	}

	@Override
	public AuditHeader saveOrUpdate(AuditHeader ah) {

		return null;
	}

	@Override
	public AuditHeader delete(AuditHeader ah) {
		return null;
	}

	@Override
	public AuditHeader doApprove(AuditHeader ah) {
		return null;
	}

	@Override
	public AuditHeader doReject(AuditHeader ah) {
		return null;
	}
}