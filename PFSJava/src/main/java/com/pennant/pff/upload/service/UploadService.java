package com.pennant.pff.upload.service;

import java.util.Date;
import java.util.List;

import com.pennant.backend.model.applicationmaster.Entity;
import com.pennant.pff.upload.model.FileUploadHeader;
import com.pennanttech.dataengine.ProcessRecord;
import com.pennanttech.dataengine.ValidateRecord;
import com.pennanttech.dataengine.model.DataEngineStatus;
import com.pennanttech.pff.core.TableType;

public interface UploadService {

	FileUploadHeader getUploadHeader(String moduleCode);

	long saveHeader(FileUploadHeader header, TableType type);

	void doValidate(FileUploadHeader header, Object detail);

	List<FileUploadHeader> getUploadHeaderById(List<String> roleCodes, String entityCode, Long id, Date fromDate,
			Date toDate, String type, String stage, String usrLogin);

	List<Entity> getEntities();

	void update(FileUploadHeader uploadHeader);

	void updateHeader(List<FileUploadHeader> uploadHeaders, boolean isApprove);

	void doApprove(List<FileUploadHeader> headers);

	void doReject(List<FileUploadHeader> headers);

	String getSqlQuery();

	ProcessRecord getProcessRecord();

	ValidateRecord getValidateRecord();

	DataEngineStatus getDEStatus(long executionID);

	void updateDownloadStatus(long headerID, int status);

	void updateInProcessStatus(long headerID, int status);

	String isValidateApprove(List<FileUploadHeader> selectedHeaders);

	void updateFailRecords(int sucessRecords, int faildrecords, long headerId);

	boolean isInProgress(Long headerID, Object... args);
}
