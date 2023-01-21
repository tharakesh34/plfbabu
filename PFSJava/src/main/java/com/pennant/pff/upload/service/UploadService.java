package com.pennant.pff.upload.service;

import java.util.Date;
import java.util.List;

import com.pennant.backend.model.applicationmaster.Entity;
import com.pennant.pff.upload.model.FileUploadHeader;
import com.pennanttech.pff.core.TableType;

public interface UploadService {

	FileUploadHeader getUploadHeader(String moduleCode);

	long saveHeader(FileUploadHeader header, TableType type);

	void doValidate(FileUploadHeader header, Object detail);

	List<FileUploadHeader> getUploadHeaderById(List<String> roleCodes, String entityCode, Long id, Date fromDate,
			Date toDate, String type, String stage);

	List<Entity> getEntities();

	void update(FileUploadHeader uploadHeader);

	void updateHeader(List<FileUploadHeader> uploadHeaders, boolean isApprove);

	void doApprove(List<FileUploadHeader> headers);

	void doReject(List<FileUploadHeader> headers);

	String getSqlQuery();
}
