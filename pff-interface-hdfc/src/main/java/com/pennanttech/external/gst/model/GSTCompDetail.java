package com.pennanttech.external.gst.model;

public class GSTCompDetail {
	private long id;
	private long headerId;
	private String record;
	private int status;
	private long gstVoucherId;
	private String errorCode;
	private String errorMessage;

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public long getHeaderId() {
		return headerId;
	}

	public void setHeaderId(long headerId) {
		this.headerId = headerId;
	}

	public String getRecord() {
		return record;
	}

	public void setRecord(String record) {
		this.record = record;
	}

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	public long getGstVoucherId() {
		return gstVoucherId;
	}

	public void setGstVoucherId(long gstVoucherId) {
		this.gstVoucherId = gstVoucherId;
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

}
