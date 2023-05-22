package com.pennant.pff.upload.dao;

import java.util.Date;
import java.util.List;

import com.pennant.backend.model.WorkFlowDetails;
import com.pennant.pff.upload.model.FileUploadHeader;
import com.pennanttech.pff.core.TableType;

public interface UploadDAO {

	WorkFlowDetails getWorkFlow(String moduleCode);

	long saveHeader(FileUploadHeader header);

	int update(FileUploadHeader header);

	void updateProgress(long headerID, int status);

	void uploadHeaderStatusCnt(long headerID, int success, int failure);

	boolean isExists(String fileName);

	boolean isFileDownlaoded(long id);

	List<FileUploadHeader> getHeaderData(List<String> roleCodes, String entityCode, Long id, Date fromDate, Date toDate,
			String type, String stage, String usrLogin);

	void deleteHeader(FileUploadHeader header, TableType tableType);

	List<Long> getHeaderStatusCnt(long uploadID, String tableName);

	void deleteDetail(long headerID, String tableName);

	void updateHeader(FileUploadHeader header);

	void updateHeader(List<FileUploadHeader> header);

	void updateDownloadStatus(long headerID, int status);

	boolean isValidateApprove(long id, int status);

	void updateFailRecords(int sucessRecords, int failedrecords, long headerId);

	List<FileUploadHeader> loadData(String type);

	void updateProgress(List<FileUploadHeader> headers, int status);
}
