package com.pennanttech.external.collectionreceipt.model;

import java.util.Date;

public class CollReceiptHeader {

	private long id;
	private String requestFileName;
	private String requestFileLocation;
	private int extraction;
	private int status;
	private int writeResponse;
	private int respFileStatus;
	private String respFileName;
	private String respFileLocation;
	private Date createdDate;
	private String errorCode;
	private String errorMessage;

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getRequestFileName() {
		return requestFileName;
	}

	public void setRequestFileName(String requestFileName) {
		this.requestFileName = requestFileName;
	}

	public String getRequestFileLocation() {
		return requestFileLocation;
	}

	public void setRequestFileLocation(String requestFileLocation) {
		this.requestFileLocation = requestFileLocation;
	}

	public int getExtraction() {
		return extraction;
	}

	public void setExtraction(int extraction) {
		this.extraction = extraction;
	}

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	public int getWriteResponse() {
		return writeResponse;
	}

	public void setWriteResponse(int writeResponse) {
		this.writeResponse = writeResponse;
	}

	public int getRespFileStatus() {
		return respFileStatus;
	}

	public void setRespFileStatus(int respFileStatus) {
		this.respFileStatus = respFileStatus;
	}

	public String getRespFileName() {
		return respFileName;
	}

	public void setRespFileName(String respFileName) {
		this.respFileName = respFileName;
	}

	public String getRespFileLocation() {
		return respFileLocation;
	}

	public void setRespFileLocation(String respFileLocation) {
		this.respFileLocation = respFileLocation;
	}

	public Date getCreatedDate() {
		return createdDate;
	}

	public void setCreatedDate(Date createdDate) {
		this.createdDate = createdDate;
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

	public boolean isValid() {
		if (this.errorCode == null) {
			return true;
		}
		return false;
	}

}
