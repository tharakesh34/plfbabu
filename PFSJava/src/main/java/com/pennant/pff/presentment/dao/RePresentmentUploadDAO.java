package com.pennant.pff.presentment.dao;

import java.util.Date;
import java.util.List;

import com.pennant.pff.presentment.model.RePresentmentUploadDetail;

public interface RePresentmentUploadDAO {
	List<RePresentmentUploadDetail> getDetails(long headerID);

	List<String> isDuplicateExists(String reference, Date dueDate, long headerID);

	boolean isProcessed(String reference, Date dueDate);

	String getBounceCode(String reference, Date dueDate);

	void update(List<RePresentmentUploadDetail> detailsList);

	void update(List<Long> headerIds, String errorCode, String errorDesc);

	String getSqlQuery();

	String getPresentmenttype(String reference, Date dueDate);
}