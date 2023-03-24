package com.pennanttech.external.silien.dao;

import java.util.List;

import com.pennanttech.external.silien.model.LienMarkDetail;

public interface ExtLienMarkingDAO {

	long getSeqNumber(String tableName);

	public void processAllLoansWithSIAndSave(String lienMark, int isActive);

	public void updateLienMarkRecord(LienMarkDetail lienDetail);

	public List<LienMarkDetail> fetchUnprocessedLienMarkingRecords(int status);

	public List<LienMarkDetail> fetchRecordsForLienFileWriting(int status);

	public String fetchRepaymentMode(LienMarkDetail detail);

	public long verifyLoanWithLienMarking(String accNumber, String lienStatus);

	public String getRecordStatus(String accNumber);

	public void insertLienMarkStatusRecord(String accNumber, String lienMark, int fileStatus);

	public void insertOrUpdateLienMarkStatusRecord(String accNumber, String lienMark, int fileStatus);

	public void updateLienRecordStatus(String accNumber, int fileStatus, String errCode, String errMessage);

	public void updateLienInterfaceStatus(LienMarkDetail lienMarkDetail);

	public boolean isFileProcessed(String fileName);

	public void insertSILienResponseFileStatus(String fileName, int status);

	public void updateLienResponseFileStatus(long id, int status, String errorCode, String errorMessage);
}
