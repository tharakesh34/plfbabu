package com.pennant.pff.receipt.dao;

import java.util.List;

import com.pennant.backend.model.loanclosure.LoanClosureUpload;

public interface LoanClosureUploadDAO {

	List<LoanClosureUpload> loadRecordData(long id);

	long save(LoanClosureUpload lc);

	List<LoanClosureUpload> getAllocations(long uploadId, long headerID);

	void saveAllocations(List<LoanClosureUpload> details);

	void update(List<LoanClosureUpload> details);

	void update(List<Long> headerIdList, String errorCode, String errorDesc, int progressFailed);

	String getSqlQuery();

	boolean getReason(String code);

	boolean isInProgress(String reference, long headerID);
}