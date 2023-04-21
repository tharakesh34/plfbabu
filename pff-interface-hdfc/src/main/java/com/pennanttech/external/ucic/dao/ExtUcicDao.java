package com.pennanttech.external.ucic.dao;

import com.pennanttech.external.ucic.model.ExtUcicCust;

public interface ExtUcicDao {

	long getSeqNumber(String tableName);

	public boolean isFileProcessed(String fileName);

	public void saveResponseFile(String fileName, String fileLocation, int fileStatus, String errorCode,
			String errorMessage);

	public void updateResponseFileProcessingFlag(long id, int status, String errorCode, String errorMessage);

	public String executeDataExtractionFromSP();

	public String executeUcicRequestFileSP(String fileName);

	public String executeUcicWeeklyRequestFileSP(String fileName);

	public String executeUcicResponseFileSP(String fileName);

	public String executeUcicAckFileSP(String fileName);

	public int updateAckForFile(String fileName, int ackStatus);

	void updateRecordProcessingFlagAndFileStatus(ExtUcicCust customer, int process_flag, int file_status);
}
