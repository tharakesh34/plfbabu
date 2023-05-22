package com.pennant.pff.presentment.dao;

import java.util.Date;
import java.util.List;

import com.pennant.pff.upload.model.FileUploadHeader;
import com.pennanttech.model.presentment.PresentmentRespUpload;
import com.pennanttech.pff.presentment.model.PresentmentDetail;

public interface PresentmentRespUploadDAO {

	List<PresentmentRespUpload> getDetails(long id);

	void update(List<PresentmentRespUpload> details);

	long saveRespHeader(FileUploadHeader header);

	void saveRespDetails(long uploadID, long headerID);

	void updateProcessingFlag(long headerID);

	void update(List<Long> headerIds, String errorCode, String errorDesc);

	String getSqlQuery();

	boolean isProcessed(String reference, Date dueDate);

	PresentmentDetail getPresentmentDetail(String reference, Date dueDate);

	boolean isDuplicateKeyPresent(String hostReference, String clearingStatus, Date dueDate);
}
