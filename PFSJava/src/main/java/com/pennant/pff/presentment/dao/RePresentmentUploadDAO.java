package com.pennant.pff.presentment.dao;

import java.util.Date;
import java.util.List;

import com.pennant.pff.presentment.model.RePresentmentUploadDetail;

public interface RePresentmentUploadDAO {
	List<RePresentmentUploadDetail> loadRecordData(long id);

	void saveDetail(RePresentmentUploadDetail detail);

	List<String> isDuplicateExists(String reference, Date dueDate, long headerID);

	boolean isProcessed(String reference, Date dueDate);

	String getBounceCode(String reference, Date dueDate);
}