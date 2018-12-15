package com.pennanttech.pff.model.cibil;

import java.io.Serializable;

public class CibilFileInfo implements Serializable {
	private static final long serialVersionUID = 1L;

	public CibilFileInfo() {
		super();
	}

	private long id;
	private String fileName;
	private String status;
	private String startTime;
	private long totalRecords;
	private long processedRecords;
	private long successCount;
	private long failedCount;
	private String remarks;
	private CibilMemberDetail cibilMemberDetail;

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getStartTime() {
		return startTime;
	}

	public void setStartTime(String startTime) {
		this.startTime = startTime;
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

	public long getSuccessCount() {
		return successCount;
	}

	public void setSuccessCount(long successCount) {
		this.successCount = successCount;
	}

	public long getFailedCount() {
		return failedCount;
	}

	public void setFailedCount(long failedCount) {
		this.failedCount = failedCount;
	}

	public String getRemarks() {
		return remarks;
	}

	public void setRemarks(String remarks) {
		this.remarks = remarks;
	}

	public CibilMemberDetail getCibilMemberDetail() {
		return cibilMemberDetail;
	}

	public void setCibilMemberDetail(CibilMemberDetail cibilMemberDetail) {
		this.cibilMemberDetail = cibilMemberDetail;
	}

}
