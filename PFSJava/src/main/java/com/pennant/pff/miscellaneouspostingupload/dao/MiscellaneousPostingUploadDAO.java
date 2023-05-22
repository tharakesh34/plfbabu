package com.pennant.pff.miscellaneouspostingupload.dao;

import java.util.List;

import com.pennant.backend.model.miscellaneousposting.upload.MiscellaneousPostingUpload;

public interface MiscellaneousPostingUploadDAO {
	List<MiscellaneousPostingUpload> getDetails(long id);

	void update(List<MiscellaneousPostingUpload> details);

	void update(List<Long> headerIds, String errorCode, String errorDesc);

	String getSqlQuery();

	void updateBatchReference(List<MiscellaneousPostingUpload> details, long batchReference);
}
