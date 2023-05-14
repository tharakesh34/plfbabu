package com.pennant.pff.lpp.dao;

import java.util.List;

import com.pennant.backend.model.lpp.upload.LPPUpload;

public interface LPPUploadDAO {
	List<LPPUpload> getDetails(long id);

	void update(List<LPPUpload> details);

	void update(List<Long> headerIds, String errorCode, String errorDesc);

	String getSqlQuery();
}
