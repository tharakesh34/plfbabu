package com.pennant.pff.batch.job.model;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Date;

public class StepDetail implements Serializable {
	private static final long serialVersionUID = 1L;

	private long id;
	private String name;
	private String reference;
	private Date valueDate;
	private long totalRecords;
	private long processedRecords;
	private long successRecords;
	private long failedRecords;
	private LocalDateTime startTime;
	private LocalDateTime endTime;
	private long userId;
	private String remarks;
	private String status;
	private String exitCode;
	private String exitDescription;

	public StepDetail() {
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

	public LocalDateTime getStartTime() {
		return startTime;
	}

	public void setStartTime(LocalDateTime startTime) {
		this.startTime = startTime;
	}

	public LocalDateTime getEndTime() {
		return endTime;
	}

	public void setEndTime(LocalDateTime endTime) {
		this.endTime = endTime;
	}

	public long getUserId() {
		return userId;
	}

	public void setUserId(long userId) {
		this.userId = userId;
	}

	public String getRemarks() {
		return remarks;
	}

	public void setRemarks(String remarks) {
		this.remarks = remarks;
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

}
