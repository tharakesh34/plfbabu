package com.pennant.backend.model.cersai;

import java.io.Serializable;
import java.util.Date;

public class CersaiHeader implements Serializable {

	private static final long serialVersionUID = 1L;

	private Long batchId = Long.MIN_VALUE;
	private String fileHeader;
	private String fileType;
	private long totalRecords;
	private Date fileDate;

	public Long getBatchId() {
		return batchId;
	}

	public void setBatchId(Long batchId) {
		this.batchId = batchId;
	}

	public String getFileHeader() {
		return fileHeader;
	}

	public void setFileHeader(String fileHeader) {
		this.fileHeader = fileHeader;
	}

	public String getFileType() {
		return fileType;
	}

	public void setFileType(String fileType) {
		this.fileType = fileType;
	}

	public long getTotalRecords() {
		return totalRecords;
	}

	public void setTotalRecords(long totalRecords) {
		this.totalRecords = totalRecords;
	}

	public Date getFileDate() {
		return fileDate;
	}

	public void setFileDate(Date fileDate) {
		this.fileDate = fileDate;
	}
}
