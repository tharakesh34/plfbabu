package com.pennant.pff.revwriteoffupload.dao;

import java.util.List;

import com.pennant.backend.model.finance.FinanceWriteoff;
import com.pennant.pff.revwriteoffupload.model.RevWriteOffUploadDetail;
import com.pennant.pff.upload.model.FileUploadHeader;

public interface RevWriteOffUploadDAO {
	List<RevWriteOffUploadDetail> getDetails(long headerID);

	void update(List<RevWriteOffUploadDetail> detailsList);

	void update(List<Long> headerIds, String errorCode, String errorDesc);

	String getSqlQuery();

	long saveLog(RevWriteOffUploadDetail detail, FileUploadHeader header);

	boolean isInProgress(String finReference, long headerID);

	List<String> isDuplicateExists(String reference, long headerID);

	String getStatus(String finReference);

	void update(RevWriteOffUploadDetail detail);

	String save(FinanceWriteoff fwo, String type);

	long getReceiptIdByRef(String reference);

}
