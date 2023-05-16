package com.pennant.pff.noc.upload.dao;

import java.util.List;

import com.pennant.pff.noc.upload.model.BlockAutoGenLetterUpload;

public interface BlockAutoGenLetterUploadDAO {

	List<BlockAutoGenLetterUpload> getDetails(long id);

	void update(List<BlockAutoGenLetterUpload> details);

	void update(List<Long> headerIds, String errorCode, String errorDesc);

	String getSqlQuery();

	boolean isValidateAction(long finid);

	void delete(long finid);

	void save(BlockAutoGenLetterUpload bagu);

	void savebyLog(BlockAutoGenLetterUpload bu);

}