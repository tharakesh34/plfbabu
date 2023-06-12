package com.pennanttech.pff.batch.model;

import java.io.Serializable;
import java.util.Date;

public class BatchProcessStatus implements Serializable {
	private static final long serialVersionUID = -285217507865291453L;

	private long id;
	private String name;
	private String status;
	private boolean runningStatus;
	private String fileName;
	private String reference;
	private Date valueDate;
	private long totalRecords;
	private long processedRecords;
	private long successRecords;
	private long failedRecords;
	private Date startTime;
	private Date endTime;
	private String remarks;

	public BatchProcessStatus() {
	    super();
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public boolean isRunningStatus() {
		return runningStatus;
	}

	public void setRunningStatus(boolean runningStatus) {
		this.runningStatus = runningStatus;
	}

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	public String getReference() {
		return reference;
	}

	public void setReference(String reference) {
		this.reference = reference;
	}

	public Date getValueDate() {
		return valueDate;
	}

	public void setValueDate(Date valueDate) {
		this.valueDate = valueDate;
	}

	public long getTotalRecords() {
		return totalRecords;
	}

	public void setTotalRecords(long totalRecords) {
		this.totalRecords = totalRecords;
	}

	public long getProcessedRecords() {
		return processedRecords;
	}

	public void setProcessedRecords(long processedRecords) {
		this.processedRecords = processedRecords;
	}

	public long getSuccessRecords() {
		return successRecords;
	}

	public void setSuccessRecords(long successRecords) {
		this.successRecords = successRecords;
	}

	public long getFailedRecords() {
		return failedRecords;
	}

	public void setFailedRecords(long failedRecords) {
		this.failedRecords = failedRecords;
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

	public String getRemarks() {
		return remarks;
	}

	public void setRemarks(String remarks) {
		this.remarks = remarks;
	}

}
