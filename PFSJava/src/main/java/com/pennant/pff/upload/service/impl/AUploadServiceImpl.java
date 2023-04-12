package com.pennant.pff.upload.service.impl;

import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.PlatformTransactionManager;

import com.pennant.app.util.SysParamUtil;
import com.pennant.backend.dao.applicationmaster.EntityDAO;
import com.pennant.backend.model.WorkFlowDetails;
import com.pennant.backend.model.applicationmaster.Entity;
import com.pennant.backend.util.PennantConstants;
import com.pennant.pff.upload.dao.UploadDAO;
import com.pennant.pff.upload.model.FileUploadHeader;
import com.pennant.pff.upload.service.UploadService;
import com.pennanttech.dataengine.ProcessRecord;
import com.pennanttech.dataengine.ValidateRecord;
import com.pennanttech.dataengine.config.DataEngineConfig;
import com.pennanttech.dataengine.model.DataEngineStatus;
import com.pennanttech.pff.core.TableType;
import com.pennanttech.pff.file.UploadContants.Status;

public abstract class AUploadServiceImpl implements UploadService {
	private UploadDAO uploadDAO;
	private EntityDAO entityDAO;
	protected PlatformTransactionManager transactionManager;
	private DataEngineConfig dataEngineConfig;

	protected static final String ERROR_LOG = "Cause {}\nMessage {}\n LocalizedMessage {}\nStackTrace {}";

	protected static final String ERR_CODE = "9999";
	protected static final String ERR_DESC = "User rejected the record";

	@Override
	public FileUploadHeader getUploadHeader(String moduleCode) {
		WorkFlowDetails workFlow = uploadDAO.getWorkFlow(moduleCode);

		FileUploadHeader header = new FileUploadHeader();

		if (workFlow != null) {
			header.setWorkflowId(workFlow.getWorkFlowId());
		}

		header.setRecordStatus(PennantConstants.RCD_STATUS_SUBMITTED);
		header.setAppDate(SysParamUtil.getAppDate());

		return header;
	}

	@Override
	public long saveHeader(FileUploadHeader header, TableType type) {
		return uploadDAO.saveHeader(header);
	}

	@Override
	public List<FileUploadHeader> getUploadHeaderById(List<String> roleCodes, String entityCode, Long id, Date fromDate,
			Date toDate, String type, String stage, String usrLogin) {
		List<FileUploadHeader> headerList = uploadDAO.getHeaderData(roleCodes, entityCode, id, fromDate, toDate, type,
				stage, usrLogin);

		for (FileUploadHeader header : headerList) {
			if (header.getFailureRecords() > 0) {
				header.setDataEngineLog(dataEngineConfig.getExceptions(header.getExecutionID()));
			}
		}

		return headerList;
	}

	@Override
	public DataEngineStatus getDEStatus(long executionID) {
		return dataEngineConfig.getDataEngineStatus(executionID);
	}

	@Override
	public List<Entity> getEntities() {
		return this.entityDAO.getEntites();
	}

	@Override
	public void update(FileUploadHeader uploadHeader) {
		this.uploadDAO.updateHeader(uploadHeader);
	}

	@Override
	public void updateHeader(List<FileUploadHeader> uploadHeaders, boolean isApprove) {
		prepareHeader(uploadHeaders, isApprove);

		this.uploadDAO.updateHeader(uploadHeaders);
	}

	private void prepareHeader(List<FileUploadHeader> uploadHeaders, boolean isApprove) {
		for (FileUploadHeader header : uploadHeaders) {
			header.setRecordType("");
			header.setWorkflowId(0);
			header.setTaskId(null);
			header.setNextTaskId(null);
			header.setRoleCode(null);
			header.setNextRoleCode(null);

			if (isApprove) {
				header.setRecordStatus(PennantConstants.RCD_STATUS_APPROVED);
				header.setApprovedBy(header.getLastMntBy());
				header.setApprovedOn(header.getLastMntOn());
				header.setProgress(Status.APPROVED.getValue());
			} else {
				header.setRecordStatus(PennantConstants.RCD_STATUS_REJECTED);
				header.setFailureRecords(header.getTotalRecords() - header.getFailureRecords());
				header.setSuccessRecords(0);
				header.setApprovedBy(null);
				header.setApprovedOn(null);
				header.setProgress(Status.REJECTED.getValue());
			}
		}
	}

	@Override
	public ProcessRecord getProcessRecord() {
		return null;
	}

	@Override
	public ValidateRecord getValidateRecord() {
		return null;
	}

	@Override
	public boolean isInProgress(Long headerID, Object... args) {
		return false;
	}

	@Override
	public void updateDownloadStatus(long headerID, int status) {
		this.uploadDAO.updateDownloadStatus(headerID, status);
	}

	@Override
	public void updateInProcessStatus(long headerID, int status) {
		this.uploadDAO.updateProgress(headerID, status);
	}

	@Override
	public void updateFailRecords(int sucessRecords, int faildrecords, long headerId) {
		this.uploadDAO.updateFailRecords(sucessRecords, faildrecords, headerId);
	}

	protected String getErrorMessage(Exception e) {
		String message = e.getMessage();

		if (message != null && message.length() > 1999) {
			message = message.substring(1999);
		}
		return message;
	}

	@Override
	public String isValidateApprove(List<FileUploadHeader> selectedHeaders) {
		StringBuilder builder = new StringBuilder();

		for (FileUploadHeader header : selectedHeaders) {
			if (!header.isDownloadReq()) {
				continue;
			}

			if (!this.uploadDAO.isValidateApprove(header.getId(), Status.DOWNLOADED.getValue())) {
				if (builder.length() > 0) {
					builder.append(", ");
				}

				builder.append(header.getId());
			}
		}

		return builder.toString();
	}

	@Autowired
	public void setUploadDAO(UploadDAO uploadDAO) {
		this.uploadDAO = uploadDAO;
	}

	@Autowired
	public void setEntityDAO(EntityDAO entityDAO) {
		this.entityDAO = entityDAO;
	}

	@Autowired
	public void setTransactionManager(PlatformTransactionManager transactionManager) {
		this.transactionManager = transactionManager;
	}

	@Autowired
	public void setDataEngineConfig(DataEngineConfig dataEngineConfig) {
		this.dataEngineConfig = dataEngineConfig;
	}

}
