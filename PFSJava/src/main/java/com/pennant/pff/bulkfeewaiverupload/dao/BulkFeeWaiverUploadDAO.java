package com.pennant.pff.bulkfeewaiverupload.dao;

import java.util.List;

import com.pennanttech.model.bulkfeewaiverupload.BulkFeeWaiverUpload;

public interface BulkFeeWaiverUploadDAO {

	List<BulkFeeWaiverUpload> getDetails(long headerID);

	void update(List<BulkFeeWaiverUpload> detailsList);

	void update(List<Long> headerIds, String errorCode, String errorDesc, int progress);

	String getSqlQuery();
}
