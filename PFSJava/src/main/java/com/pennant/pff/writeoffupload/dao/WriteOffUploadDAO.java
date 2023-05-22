package com.pennant.pff.writeoffupload.dao;

import java.util.List;

import com.pennant.pff.upload.model.FileUploadHeader;
import com.pennant.pff.writeoffupload.model.WriteOffUploadDetail;

public interface WriteOffUploadDAO {
	List<WriteOffUploadDetail> getDetails(long headerID);

	void update(List<WriteOffUploadDetail> detailsList);

	void update(List<Long> headerIds, String errorCode, String errorDesc);

	String getSqlQuery();

	long saveLog(WriteOffUploadDetail detail, FileUploadHeader header);

	boolean isInProgress(String finReference, long headerID);

	List<String> isDuplicateExists(String reference, long headerID);

	String getStatus(String finReference);

	void update(WriteOffUploadDetail detail);

}
