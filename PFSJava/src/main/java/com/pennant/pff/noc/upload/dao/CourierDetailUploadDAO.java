package com.pennant.pff.noc.upload.dao;

import java.util.Date;
import java.util.List;

import com.pennant.pff.noc.upload.model.CourierDetailUpload;

public interface CourierDetailUploadDAO {

	List<CourierDetailUpload> getDetails(long headerID);

	void update(List<CourierDetailUpload> detailsList);

	void update(List<Long> headerIds, String errorCode, String errorDesc);

	String getSqlQuery();

	Long isFileExist(String reference, String letterType, Date date);

	String isValidCourierMode(long finID, String letterType, Date letterDate);

	boolean isValidRecord(long finID, String letterType, Date letterDate);
}