package com.pennant.pff.noc.upload.dao;

import java.util.List;

import com.pennant.backend.model.rmtmasters.FinTypeFees;
import com.pennant.pff.noc.upload.model.LoanLetterUpload;

public interface LoanLetterUploadDAO {

	List<LoanLetterUpload> getDetails(long id);

	void update(List<LoanLetterUpload> details);

	void update(List<Long> headerIds, String errorCode, String errorDesc, int progress);

	String getSqlQuery();

	int getFinTypeLtrMap(String finReference);

	String getCanceltype(String finReference);

	LoanLetterUpload getByReference(String reference);

	FinTypeFees getFeeWaiverAllowed(String finType, String finEvent);
}
