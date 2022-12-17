package com.pennant.pff.upload.dao;

import java.util.Date;
import java.util.List;

import com.pennant.backend.model.WorkFlowDetails;
import com.pennant.pff.upload.model.FileUploadHeader;
import com.pennanttech.pff.core.TableType;

public interface UploadDAO {

	WorkFlowDetails getWorkFlow(String moduleCode);

	long saveHeader(FileUploadHeader header);

	int update(FileUploadHeader header, TableType tableType);

	void updateProgress(long headerID, int status, TableType tableType);

	void uploadHeaderStatusCnt(long headerID, int success, int failure);

	boolean isExists(String fileName);

	boolean isFileDownlaoded(long id);

	FileUploadHeader getHeaderData(long id, Date fromDate, Date toDate);

	void deleteHeader(FileUploadHeader header, TableType tableType);

	List<Long> getHeaderStatusCnt(long uploadID, String tableName);

	void deleteDetail(long headerID, String tableName);

	void updateHeader(FileUploadHeader header);
}
