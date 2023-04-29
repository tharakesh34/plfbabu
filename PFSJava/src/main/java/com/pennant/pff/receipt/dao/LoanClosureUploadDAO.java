package com.pennant.pff.receipt.dao;

import java.util.List;

import com.pennant.backend.model.LoanClosure;

public interface LoanClosureUploadDAO {

	List<LoanClosure> loadRecordData(long id);

	void update(List<LoanClosure> details);

	void update(List<Long> headerIdList, String errorCode, String errorDesc, int progressFailed);

	String getSqlQuery();

	void update(LoanClosure detail);

	boolean isInProgress(String reference, long headerID);

	boolean isInMaintanance(String reference);

	long save(LoanClosure cu);

}
