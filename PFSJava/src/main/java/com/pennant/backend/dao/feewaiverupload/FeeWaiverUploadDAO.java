package com.pennant.backend.dao.feewaiverupload;

import java.util.List;

import com.pennant.backend.model.finance.FeeWaiverUpload;

public interface FeeWaiverUploadDAO {

	String save(FeeWaiverUpload waiverupload, String type);

	void update(FeeWaiverUpload waiverupload, String type);

	void deleteByUploadId(long uploadId, String type);

	List<FeeWaiverUpload> getFeeWaiverListByUploadId(long uploadId, String type);
}
