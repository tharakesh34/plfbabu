package com.pennant.backend.dao.mandate;

import java.util.List;

import com.pennant.backend.model.mandate.MandateUpload;

public interface MandateUploadDAO {

	List<MandateUpload> loadRecordData(long id);

	void update(List<MandateUpload> details);

	void update(List<Long> headerIdList, String errorCode, String errorDesc);

	String getSqlQuery();
}
