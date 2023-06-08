package com.pennanttech.external.ucic.dao;

import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;

import com.pennanttech.external.ucic.model.ExtUcicCust;

public interface ExtUcicDao {

	String executeSP(String spName, MapSqlParameterSource in);

	String executeSP(String spName);

	long getSeqNumber(String tableName);

	public boolean isFileProcessed(String fileName);

	public void saveResponseFile(String fileName, String fileLocation, int fileStatus, String errorCode,
			String errorMessage);

	public void updateResponseFileProcessingFlag(long id, int status, String errorCode, String errorMessage);

	public int updateAckForFile(String fileName, int ackStatus);

	void updateRecordProcessingFlagAndFileStatus(ExtUcicCust customer, int process_flag, int file_status);

}
