package com.pennant.pff.noc.upload.dao;

import java.util.Date;
import java.util.List;

import com.pennant.pff.noc.upload.model.CourierDetailUpload;

public interface CourierDetailUploadDAO {

	List<CourierDetailUpload> getDetails(long headerID);

	void update(List<CourierDetailUpload> detailsList);

	void update(List<Long> headerIds, String errorCode, String errorDesc, int progress);

	String getSqlQuery();

	Long isFileExist(String reference, String letterType, Date date);
}