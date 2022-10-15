package com.pennant.pff.presentment.dao;

import java.util.Date;
import java.util.List;

import com.pennanttech.model.presentment.Presentment;
import com.pennanttech.pff.presentment.model.PresentmentDetail;
import com.pennanttech.pff.presentment.model.PresentmentHeader;

public interface PresentmentDAO {
	int extarct(Date dueDate);

	int extarct(String instrumentType, Date dueDate);

	int extarct(String instrumentType, Date fromDate, Date toDate);

	int clearByNoDues();

	int clearByInstrumentType(String instrumentType);

	int clearByInstrumentType(String instrumentType, String emnadateSource);

	int clearByLoanType(String loanType);

	int clearByLoanBranch(String loanBranch);

	int clearByEntityCode(String entityCode);

	int clearByExistingRecord();

	int clearByRepresentment();

	void updatePartnerBankID();

	int clearByManualExclude();

	List<PresentmentDetail> getPresentmentDetails();

	List<PresentmentDetail> getGroupByDefault();

	List<PresentmentDetail> getGroupByPartnerBankAndBank();

	List<PresentmentDetail> getGroupByBank();

	List<PresentmentDetail> getGroupByPartnerBank();

	void updateHeaderIdByDefault(List<PresentmentDetail> list);

	void updateHeaderIdByPartnerBankAndBank(List<PresentmentDetail> list);

	void updateHeaderIdByBank(List<PresentmentDetail> list);

	void updateHeaderIdByPartnerBank(List<PresentmentDetail> list);

	void clearQueue();

	long saveList(List<PresentmentDetail> presentments);

	/*
	 * void orderByPartnerBankIdAndBankCode();
	 * 
	 * void orderByBankCode();
	 * 
	 * void orderByPartnerBankId();
	 * 
	 * void orderByData();
	 */

	long getNextValue();

	long getSeqNumber(String tableName);

	long savePresentmentHeader(PresentmentHeader presentmentHeader);

	int updateSchdWithPresentmentId(List<PresentmentDetail> presenetments);

	List<PresentmentHeader> getPresentmentHeaders(Date fromDate, Date toDate);

	List<Long> getIncludeList(long id);

	boolean searchIncludeList(long id, int i);

	List<Long> getExcludeList(long id);

	Presentment getPartnerBankId(String loanType, String mandateType);
}
