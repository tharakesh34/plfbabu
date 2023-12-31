package com.pennant.pff.upload.service.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.CountDownLatch;

import javax.sql.DataSource;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.task.SimpleAsyncTaskExecutor;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;

import com.pennant.app.util.SysParamUtil;
import com.pennant.backend.dao.applicationmaster.EntityDAO;
import com.pennant.backend.model.WorkFlowDetails;
import com.pennant.backend.model.applicationmaster.Entity;
import com.pennant.backend.util.PennantConstants;
import com.pennant.eod.constants.EodConstants;
import com.pennant.pff.upload.dao.UploadDAO;
import com.pennant.pff.upload.job.UploadProcessJob;
import com.pennant.pff.upload.model.FileUploadHeader;
import com.pennant.pff.upload.model.UploadDetails;
import com.pennant.pff.upload.service.UploadService;
import com.pennanttech.dataengine.ProcessJobHandler;
import com.pennanttech.dataengine.ProcessRecord;
import com.pennanttech.dataengine.ValidateRecord;
import com.pennanttech.dataengine.config.DataEngineConfig;
import com.pennanttech.dataengine.model.DataEngineStatus;
import com.pennanttech.pennapps.core.engine.workflow.WorkflowEngine;
import com.pennanttech.pennapps.core.model.ErrorDetail;
import com.pennanttech.pennapps.core.model.LoggedInUser;
import com.pennanttech.pff.core.TableType;
import com.pennanttech.pff.file.UploadStatus;

public abstract class AUploadServiceImpl<T> implements UploadService, ValidateRecord {
	protected static final Logger logger = LogManager.getLogger(AUploadServiceImpl.class);

	private UploadDAO uploadDAO;
	private EntityDAO entityDAO;
	protected PlatformTransactionManager transactionManager;
	private DataEngineConfig dataEngineConfig;
	private DataSource dataSource;

	protected static final String INFO_LOG = "{} records to process the {}_PROCESS_JOB.";
	protected static final String IN_VALID_OBJECT = "Invalid Data transferred...";
	protected static final String REJECT_CODE = "890";
	protected static final String REJECT_DESC = "Rejected by the user.";

	protected abstract T getDetail(Object object);

	@Override
	public FileUploadHeader getUploadHeader(String moduleCode) {
		WorkFlowDetails workFlow = getWorkflow(moduleCode);

		FileUploadHeader header = new FileUploadHeader();

		if (workFlow != null) {
			header.setWorkflowId(workFlow.getWorkFlowId());
		}

		header.setRecordStatus(PennantConstants.RCD_STATUS_SAVED);
		header.setAppDate(SysParamUtil.getAppDate());

		return header;
	}

	@Override
	public long saveHeader(FileUploadHeader header, TableType type) {
		return uploadDAO.saveHeader(header);
	}

	@Override
	public void update(FileUploadHeader uploadHeader) {
		this.uploadDAO.update(uploadHeader);
	}

	@Override
	public List<FileUploadHeader> getUploadHeaderById(List<String> roleCodes, String entityCode, Long id, Date fromDate,
			Date toDate, String type, String stage, String usrLogin) {
		return uploadDAO.getHeaderData(roleCodes, entityCode, id, fromDate, toDate, type, stage, usrLogin);
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
	public void updateHeader(FileUploadHeader uploadHeader) {
		this.uploadDAO.updateHeader(uploadHeader);
	}

	@Override
	public void updateHeader(List<FileUploadHeader> uploadHeaders, boolean isApprove) {
		prepareHeader(uploadHeaders, isApprove);

		for (FileUploadHeader header : uploadHeaders) {
			header.getUploadDetails().forEach(detail -> updateProcess(header, detail, null));

			ProcessJobHandler handler = new ProcessJobHandler(header.getType().concat("_UPLOAD"), dataSource);
			handler.processJobFile(header);
		}
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
				header.setProgress(UploadStatus.APPROVED.status());
			} else {
				header.setRecordStatus(PennantConstants.RCD_STATUS_REJECTED);
				header.setApprovedBy(null);
				header.setApprovedOn(null);
				header.setProgress(UploadStatus.REJECTED.status());
				header.getUploadDetails().forEach(detail -> setRejectStatus(detail));
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
	public void uploadProcess(String type) {
		logger.debug("Respective job is not yet initialized....");
	}

	@Override
	public void uploadProcess(String type, ProcessRecord processRecord, UploadService uploadService,
			String moduleCode) {
		uploadProcess1(type, processRecord, uploadService, moduleCode);
	}

	@Override
	public void uploadProcess(String type, UploadService uploadService, String moduleCode) {
		uploadProcess1(type, null, uploadService, moduleCode);
	}

	private void uploadProcess1(String type, ProcessRecord processRecord, UploadService uploadService,
			String moduleCode) {
		logger.info("{} Upload job is started...", type);

		List<FileUploadHeader> loadData = uploadDAO.loadData(type);

		if (CollectionUtils.isEmpty(loadData)) {
			logger.debug("There is no records to process the {}.", type);
			return;
		}

		uploadDAO.updateProgress(loadData, UploadStatus.IN_PROCESS.status());

		setWorkflowDetails(loadData, moduleCode);

		List<FileUploadHeader> initaitedList = new ArrayList<>();
		List<FileUploadHeader> approvalList = new ArrayList<>();

		for (FileUploadHeader hdr : loadData) {
			if (UploadStatus.INITIATED.status() == hdr.getProgress()) {
				initaitedList.add(hdr);
			}

			if (UploadStatus.APPROVE.status() == hdr.getProgress()) {
				approvalList.add(hdr);
			}
		}

		Date appDate = SysParamUtil.getAppDate();

		if (CollectionUtils.isNotEmpty(initaitedList)) {
			logger.info(INFO_LOG, initaitedList.size(), type);
			new Thread(() -> {
				String jobName = type.toUpperCase().concat("_PROCESS_JOB");
				processJob(processRecord, jobName, initaitedList, appDate, uploadService);
			}).start();
		}

		if (CollectionUtils.isNotEmpty(approvalList)) {
			logger.info(INFO_LOG, approvalList.size(), type);
			new Thread(() -> {
				String jobName = type.toUpperCase().concat("_APPROVER_JOB");
				processJob(processRecord, jobName, approvalList, appDate, uploadService);
			}).start();

		}

		logger.info("{} is completed...", type);
	}

	private void processJob(ProcessRecord processRecord, String jobName, List<FileUploadHeader> loadData, Date appDate,
			UploadService uploadService) {

		SimpleAsyncTaskExecutor taskExecutor = new SimpleAsyncTaskExecutor(jobName);
		CountDownLatch latch = new CountDownLatch(1);

		UploadProcessJob threadProcess = new UploadProcessJob();
		threadProcess.setDataSource(dataSource);
		threadProcess.setLatch(latch);
		threadProcess.setHeaderList(loadData);
		threadProcess.setAppDate(appDate);
		if (processRecord == null) {
			threadProcess.setValidateRecord(this);
		}
		threadProcess.setProcessRecord(processRecord);
		threadProcess.setUploadService(uploadService);
		threadProcess.setJobName(jobName);
		taskExecutor.execute(threadProcess);

		try {
			latch.await();
		} catch (InterruptedException ie) {
			logger.warn("Interrupted!", ie);
			Thread.currentThread().interrupt();
		}
	}

	private void setWorkflowDetails(List<FileUploadHeader> loadData, String moduleCode) {
		WorkFlowDetails workflow = getWorkflow(moduleCode);

		WorkflowEngine engine = new WorkflowEngine(workflow.getWorkFlowXml());

		for (FileUploadHeader hdr : loadData) {
			hdr.setRecordStatus(PennantConstants.RCD_STATUS_SUBMITTED);
			String nextTaskId = hdr.getNextTaskId().replaceFirst(hdr.getTaskId() + ";", "");

			if ("".equals(nextTaskId)) {
				nextTaskId = getNextTaskIds(hdr.getTaskId(), hdr, engine);
				hdr.setNextTaskId(nextTaskId);
			}

			hdr.setNextRoleCode(getNextRoleCode(engine, nextTaskId, hdr.getNextRoleCode()));
		}

	}

	private String getNextRoleCode(WorkflowEngine workFlowEngine, String nextTaskId, String nextRoleCode) {
		if (StringUtils.isNotBlank(nextTaskId)) {
			String[] nextTasks = nextTaskId.split(";");

			if (nextTasks != null && nextTasks.length > 0) {
				for (int i = 0; i < nextTasks.length; i++) {
					nextRoleCode = workFlowEngine.getUserTask(nextTasks[i]).getActor();
				}
			} else {
				nextRoleCode = workFlowEngine.getUserTask(nextTaskId).getActor();
			}
		}

		return nextRoleCode;
	}

	private String getNextTaskIds(String taskId, FileUploadHeader hdr, WorkflowEngine workFlowEngine) {
		String result = workFlowEngine.getNextUserTaskIdsAsString(taskId, hdr);
		if (StringUtils.isNotBlank(result)) {
			result += ";";
		}

		return result;
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

	protected void setSuccesStatus(UploadDetails detail) {
		detail.setProgress(EodConstants.PROGRESS_SUCCESS);
		detail.setStatus("S");
		detail.setErrorCode(null);
		detail.setErrorDesc(null);
	}

	protected void setFailureStatus(UploadDetails detail) {
		setFailureStatus(detail, detail.getErrorCode(), detail.getErrorDesc());
	}

	protected void setFailureStatus(UploadDetails detail, String errorDesc) {
		setFailureStatus(detail, "9999", errorDesc);
	}

	protected void setFailureStatus(UploadDetails detail, ErrorDetail ed) {
		setFailureStatus(detail, ed.getCode(), ed.getError());
	}

	protected void setFailureStatus(UploadDetails detail, String errorCode, String errorDesc) {
		errorDesc = StringUtils.trimToEmpty(errorDesc);
		if (errorDesc.length() > 1999) {
			errorDesc = errorDesc.substring(0, 1999);
		}

		detail.setProgress(EodConstants.PROGRESS_FAILED);
		detail.setStatus("F");
		detail.setErrorCode(errorCode);
		detail.setErrorDesc(errorDesc);
	}

	protected void setRejectStatus(UploadDetails detail) {
		detail.setProgress(EodConstants.PROGRESS_FAILED);
		detail.setStatus("R");
		detail.setErrorCode("890");
		detail.setErrorDesc("Rejected by the user.");
	}

	@Override
	public String isValidateApprove(List<FileUploadHeader> selectedHeaders) {
		StringBuilder builder = new StringBuilder();

		for (FileUploadHeader header : selectedHeaders) {
			if (!header.isDownloadReq()) {
				continue;
			}

			if (!this.uploadDAO.isValidateApprove(header.getId(), UploadStatus.DOWNLOADED.status())) {
				if (builder.length() > 0) {
					builder.append(", ");
				}

				builder.append(header.getId());
			}
		}

		return builder.toString();
	}

	@Override
	public void updateProcess(FileUploadHeader header, UploadDetails detail, MapSqlParameterSource paramSource) {
		int progress = detail.getProgress();

		if (progress != EodConstants.PROGRESS_FAILED && progress != EodConstants.PROGRESS_SUCCESS) {
			if (paramSource != null) {
				paramSource.addValue("STATUS", detail.getStatus());
				paramSource.addValue("ERRORCODE", "");
				paramSource.addValue("ERRORDESC", "Record is not properly validated.");
			}
		}

		if (progress == EodConstants.PROGRESS_FAILED) {
			if (paramSource != null) {
				paramSource.addValue("ERRORCODE", detail.getErrorCode());
				paramSource.addValue("ERRORDESC", detail.getErrorDesc());
			}
		}

		if (paramSource != null) {
			paramSource.addValue("STATUS", detail.getStatus());
			paramSource.addValue("PROGRESS", progress);
		}
	}

	protected TransactionStatus getTransactionStatus() {
		return transactionManager
				.getTransaction(new DefaultTransactionDefinition(TransactionDefinition.PROPAGATION_REQUIRES_NEW));
	}

	protected void getRemarks(FileUploadHeader header, int sucessRecords, int failRecords) {
		StringBuilder remarks = new StringBuilder("Process Completed");

		if (failRecords > 0) {
			remarks.append(" with exceptions, ");
		}

		remarks.append(" Total Records : ").append(header.getTotalRecords());
		remarks.append(" Success Records : ").append(sucessRecords);
		remarks.append(" Failed Records : ").append(failRecords);
	}

	protected void prepareUserDetails(FileUploadHeader header, UploadDetails detail) {
		LoggedInUser userDetails = detail.getUserDetails();

		if (userDetails == null) {
			userDetails = new LoggedInUser();
			userDetails.setLoginUsrID(header.getApprovedBy());
			userDetails.setUserName(header.getApprovedByName());
		}

		detail.setUserDetails(userDetails);
	}

	private WorkFlowDetails getWorkflow(String moduleCode) {
		return uploadDAO.getWorkFlow(moduleCode);
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

	@Autowired
	public void setDataSource(DataSource dataSource) {
		this.dataSource = dataSource;
	}

}