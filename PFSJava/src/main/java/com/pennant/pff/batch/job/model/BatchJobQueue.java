package com.pennant.pff.batch.job.model;

import java.io.Serializable;
import java.util.Date;

public class BatchJobQueue implements Serializable {
	private static final long serialVersionUID = 1L;

	private long id;
	private Long batchId;
	private String batchType;
	private String jobName;
	private Date startTime;
	private Date endTime;
	private Date completedTime;
	private int threadId;
	private String batchStatus;
	private int totalRecords;
	private int processRecords;
	private int successRecords;
	private int failedRecords;
	private int processedRecords;
	private int totalThreads;
	private int progress;
	private Long resetCounterId;
	private String error;
	private String remarks;
	private String failedStep;

	public BatchJobQueue() {
		super();
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public Long getBatchId() {
		return batchId;
	}

	public void setBatchId(Long batchId) {
		this.batchId = batchId;
	}

	public String getBatchType() {
		return batchType;
	}

	public void setBatchType(String batchType) {
		this.batchType = batchType;
	}

	public String getJobName() {
		return jobName;
	}

	public void setJobName(String jobName) {
		this.jobName = jobName;
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

	public Date getCompletedTime() {
		return completedTime;
	}

	public void setCompletedTime(Date completedTime) {
		this.completedTime = completedTime;
	}

	public int getThreadId() {
		return threadId;
	}

	public void setThreadId(int threadId) {
		this.threadId = threadId;
	}

	public String getBatchStatus() {
		return batchStatus;
	}

	public void setBatchStatus(String batchStatus) {
		this.batchStatus = batchStatus;
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

	public int getProcessedRecords() {
		return processedRecords;
	}

	public void setProcessedRecords(int processedRecords) {
		this.processedRecords = processedRecords;
	}

	public int getTotalThreads() {
		return totalThreads;
	}

	public void setTotalThreads(int totalThreads) {
		this.totalThreads = totalThreads;
	}

	public int getProgress() {
		return progress;
	}

	public void setProgress(int progress) {
		this.progress = progress;
	}

	public Long getResetCounterId() {
		return resetCounterId;
	}

	public void setResetCounterId(Long resetCounterId) {
		this.resetCounterId = resetCounterId;
	}

	public String getError() {
		return error;
	}

	public void setError(String error) {
		this.error = error;
	}

	public String getRemarks() {
		return remarks;
	}

	public void setRemarks(String remarks) {
		this.remarks = remarks;
	}

	public String getFailedStep() {
		return failedStep;
	}

	public void setFailedStep(String failedStep) {
		this.failedStep = failedStep;
	}

}
