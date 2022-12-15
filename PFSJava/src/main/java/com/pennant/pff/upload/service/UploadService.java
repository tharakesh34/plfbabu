package com.pennant.pff.upload.service;

import java.util.List;

import com.pennant.backend.model.applicationmaster.Entity;
import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.pff.upload.model.FileUploadHeader;
import com.pennanttech.pff.core.TableType;

public interface UploadService<T> {

	FileUploadHeader getUploadHeader(String moduleCode);

	long saveHeader(FileUploadHeader header, TableType type);

	void importFile(FileUploadHeader header);

	void read(FileUploadHeader header);

	void validate(FileUploadHeader header, T detail);

	void saveDetail(T detail);

	void updateheader(FileUploadHeader header);

	boolean isExists(String fileName);

	boolean isDownloaded(long fileID);

	void updateProgress(long headerID, int status);

	void updateStatus(List<Long> headerIds);

	FileUploadHeader getUploadHeaderById(long id);

	List<T> getUploadDetailById(long headerID);

	List<Entity> getEntities();

	AuditHeader saveOrUpdate(AuditHeader ah);

	AuditHeader doApprove(AuditHeader ah);

	AuditHeader doReject(AuditHeader ah);

	AuditHeader delete(AuditHeader ah);

	void downloadReport(Long fileID, String type);
}
