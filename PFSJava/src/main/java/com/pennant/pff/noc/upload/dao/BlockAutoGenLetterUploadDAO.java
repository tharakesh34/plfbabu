package com.pennant.pff.noc.upload.dao;

import java.util.List;

import com.pennant.pff.noc.upload.model.BlockAutoGenLetterUpload;

public interface BlockAutoGenLetterUploadDAO {

	List<BlockAutoGenLetterUpload> getDetails(long id);

	void update(List<BlockAutoGenLetterUpload> details);

	void update(List<Long> headerIds, String errorCode, String errorDesc, int progress);

	String getSqlQuery();

	boolean isValidateAction(String reference, String action, int progressSuccess);

	int getReference(String reference, int progressSuccess);

	void delete(String reference, int progressSuccess);

	long save(BlockAutoGenLetterUpload bagu);
}