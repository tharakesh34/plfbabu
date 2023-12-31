package com.pennant.pff.noc.dao;

import java.util.List;

import com.pennant.backend.model.finance.ReceiptAllocationDetail;
import com.pennant.backend.model.reports.ReportListDetail;
import com.pennant.pff.noc.model.GenerateLetter;
import com.pennanttech.pennapps.jdbc.search.ISearch;
import com.pennanttech.pff.core.TableType;

public interface GenerateLetterDAO {

	List<GenerateLetter> getResult(ISearch searchFilters, List<String> roleCodes);

	List<ReportListDetail> getPrintLetters(List<String> workFlowRoles);

	GenerateLetter getLetter(long id);

	List<GenerateLetter> getGenerateLetters(List<String> getGenerateLetters);

	boolean isReferenceExist(String reference);

	List<ReceiptAllocationDetail> getPrinAndPftWaiver(String finReference);

	long save(GenerateLetter pinCode, TableType tableType);

	void update(GenerateLetter pinCode, TableType tableType);

	void delete(GenerateLetter pinCode, TableType mainTab);

	List<GenerateLetter> getLetterInfo(long finID);

	long getInitiatedLoan(long finID, String letterType);

	boolean letterIsInQueu(long finID, String letterType);

	void deleteAutoLetterGeneration(long finID, String letterType);

	String getReasonCode(long finID);

	String getCancelReasons(String reference);

	List<GenerateLetter> getLoanLetterInfo(long finID, String letterType);

	void updateAutoLetterGeneration(long finID, String letterType);
}