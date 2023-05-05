package com.pennant.pff.noc.upload.dao;

import java.util.List;

import com.pennant.backend.model.noc.upload.BulkGenerateLetterUpload;
import com.pennant.backend.model.rmtmasters.FinTypeFees;

public interface BulkGenerateLetterUploadDAO {

	List<BulkGenerateLetterUpload> getDetails(long id);

	void update(List<BulkGenerateLetterUpload> details);

	void update(List<Long> headerIds, String errorCode, String errorDesc, int progress);

	String getSqlQuery();

	int getFinTypeLtrMap(String finReference);

	String getCanceltype(String finReference);

	BulkGenerateLetterUpload getByReference(String reference);

	FinTypeFees getFeeWaiverAllowed(String finType, String finEvent);
}
