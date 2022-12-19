package com.pennant.pff.upload.service;

import java.util.Date;
import java.util.List;

import com.pennant.backend.model.applicationmaster.Entity;
import com.pennant.pff.upload.model.FileUploadHeader;
import com.pennanttech.pff.core.TableType;

public interface UploadService<T> {

	FileUploadHeader getUploadHeader(String moduleCode);

	long saveHeader(FileUploadHeader header, TableType type);

	void validate(FileUploadHeader header, T detail);

	List<FileUploadHeader> getUploadHeaderById(List<String> roleCodes, String entityCode, Long id, Date fromDate,
			Date toDate);

	List<Entity> getEntities();

	void update(FileUploadHeader uploadHeader);

	void approve(List<FileUploadHeader> headers);

	void reject(List<FileUploadHeader> headers);
}
