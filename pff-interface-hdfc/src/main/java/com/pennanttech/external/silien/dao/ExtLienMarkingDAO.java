package com.pennanttech.external.silien.dao;

import java.util.List;

import com.pennanttech.external.silien.model.LienMarkDetail;

public interface ExtLienMarkingDAO {

	long getSeqNumber(String tableName);

	public List<LienMarkDetail> fetchRecordsForLienFileWriting(String status);

	public void updateLienRecordStatus(LienMarkDetail lienMarkDetail);

	public boolean isFileProcessed(String fileName);

	public void insertSILienResponseFileStatus(String fileName, int status);

	public void updateLienResponseFileStatus(long id, int status, String errorCode, String errorMessage);
}
