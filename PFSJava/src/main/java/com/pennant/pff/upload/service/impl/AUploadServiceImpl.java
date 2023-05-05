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
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pff.core.TableType;
import com.pennanttech.pff.file.UploadStatus;
import com.pennanttech.pff.file.UploadTypes;

public abstract class AUploadServiceImpl implements UploadService, ValidateRecord {
	protected static final Logger logger = LogManager.getLogger(AUploadServiceImpl.class);

	private UploadDAO uploadDAO;
	private EntityDAO entityDAO;
	protected PlatformTransactionManager transactionManager;
	private DataEngineConfig dataEngineConfig;
	private DataSource dataSource;

	protected static final String INFO_LOG = "{} records to process the {}_PROCESS_JOB.";
	protected static final String ERR_CODE = "9999";
	protected static final String ERR_DESC = "User rejected the record";

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
				header.setFailureRecords(header.getTotalRecords());
				header.setSuccessRecords(0);
				header.setApprovedBy(null);
				header.setApprovedOn(null);
				header.setProgress(UploadStatus.REJECTED.status());
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
		logger.info("{} Upload job is started...", type.toUpperCase());

		List<FileUploadHeader> loadData = uploadDAO.loadData(type);

		if (CollectionUtils.isEmpty(loadData)) {
			logger.debug(String.format("There is no records to process the %s.", type.toUpperCase()));
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
			logger.info(INFO_LOG, initaitedList.size(), type.toUpperCase());
			new Thread(() -> {
				String jobName = type.toUpperCase().concat("_PROCESS_JOB");
				processJob(processRecord, jobName, initaitedList, appDate, uploadService, moduleCode);
			}).start();
		}

		if (CollectionUtils.isNotEmpty(approvalList)) {
			logger.info(INFO_LOG, approvalList.size(), type.toUpperCase());
			new Thread(() -> {
				String jobName = type.toUpperCase().concat("_APPROVER_JOB");
				processJob(processRecord, jobName, approvalList, appDate, uploadService, moduleCode);
			}).start();

		}

		logger.info(String.format("%s is completed...", type.toUpperCase()));
	}

	private void processJob(ProcessRecord processRecord, String jobName, List<FileUploadHeader> loadData, Date appDate,
			UploadService uploadService, String moduleCode) {

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

			logger.info("Thread Execution for the pool thread{} to thread{} ended");
		} catch (InterruptedException ie) {
			logger.error(Literal.EXCEPTION, ie);
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

					if (nextRoleCode.length() > 1) {
						nextRoleCode = nextRoleCode.concat(",");
					}
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
	public void updateProcess(FileUploadHeader header, UploadDetails detail, MapSqlParameterSource record,
			String successStatus, String failureStatus) {

		if (detail.getProgress() == EodConstants.PROGRESS_FAILED) {
			if (record != null) {
				record.addValue("ERRORCODE", detail.getErrorCode());
				record.addValue("ERRORDESC", detail.getErrorDesc());
			}

			header.setFailureRecords(header.getFailureRecords() + 1);
			detail.setStatus(failureStatus);
			detail.setProgress(EodConstants.PROGRESS_FAILED);

		} else {
			header.setSuccessRecords(header.getSuccessRecords() + 1);
			detail.setStatus(successStatus);
			detail.setProgress(EodConstants.PROGRESS_SUCCESS);
		}

		if (UploadTypes.EXCESS_TRANSFER.name().equals(header.getType())) {
			detail.setStatus(detail.getProgress() == EodConstants.PROGRESS_FAILED ? "R" : "C");
		}

		if (record != null) {
			record.addValue("STATUS", detail.getStatus());
			record.addValue("PROGRESS", detail.getProgress());
		}
	}

	@Override
	public void updateProcess(FileUploadHeader header, UploadDetails detail, MapSqlParameterSource record) {
		updateProcess(header, detail, record, "S", "F");
	}

	public abstract void doValidate(FileUploadHeader header, Object object);

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