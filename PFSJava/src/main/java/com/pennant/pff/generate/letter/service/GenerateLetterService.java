package com.pennant.pff.generate.letter.service;

import java.util.List;

import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.model.reports.ReportListDetail;
import com.pennant.pff.generate.letter.model.GenerateLetter;
import com.pennanttech.pennapps.jdbc.search.ISearch;

public interface GenerateLetterService {

	List<GenerateLetter> getResult(ISearch searchFilters);

	List<ReportListDetail> getPrintLetters(List<String> workFlowRoles);

	GenerateLetter getLetter(long id);

	List<GenerateLetter> getGenerateLetters(List<String> workFlowRoles);

	boolean isReferenceExist(String value);

	AuditHeader saveOrUpdate(AuditHeader ah);

	AuditHeader delete(AuditHeader ah);

	AuditHeader doApprove(AuditHeader ah);

	AuditHeader doReject(AuditHeader ah);
}