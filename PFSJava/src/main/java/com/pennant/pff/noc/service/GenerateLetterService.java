package com.pennant.pff.noc.service;

import java.util.List;

import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.model.finance.FinExcessAmount;
import com.pennant.backend.model.finance.ManualAdvise;
import com.pennant.backend.model.finance.ReceiptAllocationDetail;
import com.pennant.backend.model.letter.LoanLetter;
import com.pennant.backend.model.reports.ReportListDetail;
import com.pennant.pff.noc.model.GenerateLetter;
import com.pennant.pff.noc.model.LoanTypeLetterMapping;
import com.pennanttech.pennapps.jdbc.search.ISearch;

public interface GenerateLetterService {

	List<GenerateLetter> getResult(ISearch searchFilters, List<String> roleCodes);

	List<ReportListDetail> getPrintLetters(List<String> workFlowRoles);

	GenerateLetter getLetter(long id);

	List<GenerateLetter> getGenerateLetters(List<String> workFlowRoles);

	boolean isReferenceExist(String value);

	AuditHeader saveOrUpdate(AuditHeader ah);

	AuditHeader delete(AuditHeader ah);

	AuditHeader doApprove(AuditHeader ah);

	AuditHeader doReject(AuditHeader ah);

	void getFinanceDetailById(GenerateLetter gl);

	List<ReceiptAllocationDetail> getPrinAndPftWaiver(String finReference);

	List<FinExcessAmount> getExcessAvailable(long finID);

	List<GenerateLetter> getLetterInfo(GenerateLetter gl);

	List<LoanTypeLetterMapping> getFinTypeMap(String finType);

	String getCanceltype(String finReference);

	long getInitiatedLoan(long finID, String letterType);

	LoanLetter generateLetter(GenerateLetter generateLetter);

	boolean letterIsInQueu(long finID, String letterType);

	List<ManualAdvise> getManualAdvises(long finID);

	void deleteAutoLetterGeneration(long finID, String letterType);
}