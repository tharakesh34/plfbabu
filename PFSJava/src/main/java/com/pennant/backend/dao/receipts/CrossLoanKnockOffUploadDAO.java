package com.pennant.backend.dao.receipts;

import java.util.List;

import com.pennant.backend.model.crossloanknockoff.CrossLoanKnockoffUpload;

public interface CrossLoanKnockOffUploadDAO {

	List<CrossLoanKnockoffUpload> loadRecordData(long id);

	void update(List<CrossLoanKnockoffUpload> details);

	void update(List<Long> headerIdList, String errorCode, String errorDesc);

	String getSqlQuery();

	List<CrossLoanKnockoffUpload> getAllocations(long uploadId, long headerId);

	void saveAllocations(List<CrossLoanKnockoffUpload> details);

	long save(CrossLoanKnockoffUpload ck);

}
