package com.pennanttech.external.presentment.model;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class ExtPresentment {
	private long id;
	private String module;
	private String fileName;
	private int status;
	private int extraction;
	private String fileLocation;
	private Date createdDate;
	private String errorCode;
	private String errorMessage;

	private List<ExtPresentmentData> extPresentmentDataList = new ArrayList<ExtPresentmentData>();

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getModule() {
		return module;
	}

	public void setModule(String module) {
		this.module = module;
	}

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	public String getFileLocation() {
		return fileLocation;
	}

	public void setFileLocation(String fileLocation) {
		this.fileLocation = fileLocation;
	}

	public Date getCreatedDate() {
		return createdDate;
	}

	public void setCreatedDate(Date createdDate) {
		this.createdDate = createdDate;
	}

	public int getExtraction() {
		return extraction;
	}

	public void setExtraction(int extraction) {
		this.extraction = extraction;
	}

	public String getErrorCode() {
		return errorCode;
	}

	public void setErrorCode(String errorCode) {
		this.errorCode = errorCode;
	}

	public String getErrorMessage() {
		return errorMessage;
	}

	public void setErrorMessage(String errorMessage) {
		this.errorMessage = errorMessage;
	}

	public List<ExtPresentmentData> getExtPresentmentDataList() {
		return extPresentmentDataList;
	}

	public void setExtPresentmentDataList(List<ExtPresentmentData> extPresentmentDataList) {
		this.extPresentmentDataList = extPresentmentDataList;
	}

	@Override
	public String toString() {
		return "ExtPresentment [id=" + id + ", module=" + module + ", fileName=" + fileName + ", status=" + status
				+ ", extraction=" + extraction + ", fileLocation=" + fileLocation + ", createdDate=" + createdDate
				+ ", extPresentmentDataList=" + extPresentmentDataList + "]";
	}

}
