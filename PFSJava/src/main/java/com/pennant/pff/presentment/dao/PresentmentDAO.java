package com.pennant.pff.presentment.dao;

import java.util.Date;
import java.util.List;

import com.pennant.pff.batch.job.model.BatchJobQueue;
import com.pennanttech.model.presentment.Presentment;
import com.pennanttech.pff.presentment.model.PresentmentDetail;
import com.pennanttech.pff.presentment.model.PresentmentHeader;

public interface PresentmentDAO {

	long createBatch(String batchName);

	BatchJobQueue getBatch(BatchJobQueue jobQueue);

	void updateTotalRecords(int count, long batchID);

	void updateBatch(BatchJobQueue jobQueue);

	void updateRemarks(BatchJobQueue jobQueue);

	void updateFailureError(BatchJobQueue jobQueue);

	void updateEndTimeStatus(BatchJobQueue jobQueue);
	
	int extarct(long batchID, Date dueDate);

	int extarct(long batchID, Date fromDate, Date toDate);

	int extarct(long batchID, String instrumentType, Date fromDate, Date toDate);

	int clearByNoDues(long batchID);

	int clearByInstrumentType(long batchID, String instrumentType);

	int clearByInstrumentType(long batchID, String instrumentType, String emnadateSource);

	int clearByLoanType(long batchID, String loanType);

	int clearByLoanBranch(long batchID, String loanBranch);

	int clearByEntityCode(long batchID, String entityCode);

	int clearSecurityCheque(long batchID);

	int updateToSecurityMandate(long batchID);

	void updateIPDC(long batchID);

	int clearByExistingRecord(long batchID);

	int clearByRepresentment(long batchID);

	void updatePartnerBankID(long batchID);

	int clearByManualExclude(long batchID);

	List<PresentmentDetail> getPresentmentDetails(long batchID);

	List<PresentmentDetail> getGroupByDefault(long batchID);

	List<PresentmentDetail> getGroupByPartnerBankAndBank(long batchID);

	List<PresentmentDetail> getGroupByBank(long batchID);

	List<PresentmentDetail> getGroupByPartnerBank(long batchID);

	void updateHeaderIdByDefault(long batchID, List<PresentmentDetail> list);

	void updateHeaderIdByPartnerBankAndBank(long batchID, List<PresentmentDetail> list);

	void updateHeaderIdByBank(long batchID, List<PresentmentDetail> list);

	void updateHeaderIdByPartnerBank(long batchID, List<PresentmentDetail> list);

	void clearQueue(long batchId);

	long getNextValue();

	long getSeqNumber(String tableName);

	long savePresentmentHeader(PresentmentHeader presentmentHeader);

	int updateSchdWithPresentmentId(List<PresentmentDetail> presenetments);

	List<PresentmentHeader> getPresentmentHeaders(long batchId);

	List<Long> getIncludeList(long id);

	boolean searchIncludeList(long id, int i);

	List<Long> getExcludeList(long id);

	Presentment getPartnerBankId(String loanType, String mandateType);

	int approveExludes(long id);

	void updatePartnerBankID(long id, long PartnerBankId);

	int updateHeader(long presentmentId);

	PresentmentDetail getPresentmentDetail(long extrationID);

	long save(PresentmentDetail pd);

	void updatePresentmentIdAsZero(long presentmentId);

	PresentmentDetail getPresentmenToPost(long PresentmentId);

	List<PresentmentDetail> getSendToPresentmentDetails(long presentmentId);

	void updateExcludeReason(long presentmentId, int manualExclude);

	int getRecordsByWaiting(String clearingStatus);

	PresentmentDetail getPresentmenForResponse(Long responseID);

	void logRespDetailError(long headerId, long detailId, String errorCode, String errorDesc);

}
