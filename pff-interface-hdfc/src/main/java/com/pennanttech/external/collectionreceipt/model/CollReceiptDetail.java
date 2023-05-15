package com.pennanttech.external.collectionreceipt.model;

import java.util.Date;

public class CollReceiptDetail {

	private long id;
	private long headerId;
	private String recordData;
	private long receiptId;
	private Date receiptCreatedDate;
	private Date createdDate;
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

	public String getRecordData() {
		return recordData;
	}

	public void setRecordData(String recordData) {
		this.recordData = recordData;
	}

	public long getReceiptId() {
		return receiptId;
	}

	public void setReceiptId(long receiptId) {
		this.receiptId = receiptId;
	}

	public Date getReceiptCreatedDate() {
		return receiptCreatedDate;
	}

	public void setReceiptCreatedDate(Date receiptCreatedDate) {
		this.receiptCreatedDate = receiptCreatedDate;
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

}
