package com.pennant.pff.batch.job.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.springframework.batch.core.JobInstance;

import com.pennapps.core.ws.model.ReturnStatus;

public class BatchJob implements Serializable {
	private static final long serialVersionUID = 1L;

	private long id;
	private String jobName;
	private Long jobInstanceId;
	private Long jobExecutionId;
	private Date nextBusinessDate;
	private Long userId;
	private Date startTime;
	private Date endTime;
	private String status;
	private String exitCode;
	private String exitDescription;
	private List<StepDetail> steps = new ArrayList<>();
	private ReturnStatus returnStatus = new ReturnStatus();
	private JobInstance lastJobInstance;
	private String batchType;
	private int totalRecords;
	private int processRecords;
	private int successRecords;
	private int failedRecords;
	private String Remarks;
	private int progress;
	private int processedRecords;

	public BatchJob() {
		super();
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getJobName() {
		return jobName;
	}

	public void setJobName(String jobName) {
		this.jobName = jobName;
	}

	public Long getJobInstanceId() {
		return jobInstanceId;
	}

	public void setJobInstanceId(Long jobInstanceId) {
		this.jobInstanceId = jobInstanceId;
	}

	public Long getJobExecutionId() {
		return jobExecutionId;
	}

	public void setJobExecutionId(Long jobExecutionId) {
		this.jobExecutionId = jobExecutionId;
	}

	public Date getNextBusinessDate() {
		return nextBusinessDate;
	}

	public void setNextBusinessDate(Date nextBusinessDate) {
		this.nextBusinessDate = nextBusinessDate;
	}

	public Long getUserId() {
		return userId;
	}

	public void setUserId(Long userId) {
		this.userId = userId;
	}

	public Date getStartTime() {
		return startTime;
	}

	public void setStartTime(Date startTime) {
		this.startTime = startTime;
	}

	public Date getEndTime() {
		return endTime;
	}

	public void setEndTime(Date endTime) {
		this.endTime = endTime;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getExitCode() {
		return exitCode;
	}

	public void setExitCode(String exitCode) {
		this.exitCode = exitCode;
	}

	public String getExitDescription() {
		return exitDescription;
	}

	public void setExitDescription(String exitDescription) {
		this.exitDescription = exitDescription;
	}

	public List<StepDetail> getSteps() {
		return steps;
	}

	public void setSteps(List<StepDetail> steps) {
		this.steps = steps;
	}

	public ReturnStatus getReturnStatus() {
		return returnStatus;
	}

	public void setReturnStatus(ReturnStatus returnStatus) {
		this.returnStatus = returnStatus;
	}

	public JobInstance getLastJobInstance() {
		return lastJobInstance;
	}

	public void setLastJobInstance(JobInstance lastJobInstance) {
		this.lastJobInstance = lastJobInstance;
	}

	public String getBatchType() {
		return batchType;
	}

	public void setBatchType(String batchType) {
		this.batchType = batchType;
	}

	public int getTotalRecords() {
		return totalRecords;
	}

	public void setTotalRecords(int totalRecords) {
		this.totalRecords = totalRecords;
	}

	public int getProcessRecords() {
		return processRecords;
	}

	public void setProcessRecords(int processRecords) {
		this.processRecords = processRecords;
	}

	public int getSuccessRecords() {
		return successRecords;
	}

	public void setSuccessRecords(int successRecords) {
		this.successRecords = successRecords;
	}

	public int getFailedRecords() {
		return failedRecords;
	}

	public void setFailedRecords(int failedRecords) {
		this.failedRecords = failedRecords;
	}

	public String getRemarks() {
		return Remarks;
	}

	public void setRemarks(String remarks) {
		Remarks = remarks;
	}

	public int getProgress() {
		return progress;
	}

	public void setProgress(int progress) {
		this.progress = progress;
	}

	public int getProcessedRecords() {
		return processedRecords;
	}

	public void setProcessedRecords(int processedRecords) {
		this.processedRecords = processedRecords;
	}

}
