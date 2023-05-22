package com.pennant.pff.receipt.model;

import java.util.Date;

import com.pennant.pff.upload.model.UploadDetails;

public class ReceiptStatusUpload extends UploadDetails {
	private static final long serialVersionUID = 1L;

	private long receiptId;
	private String statusRM;
	private Date realizationDate;
	private Date bounceDate;
	private String bounceReason;
	private String bounceRemarks;

	public ReceiptStatusUpload() {
		super();
	}

	public long getReceiptId() {
		return receiptId;
	}

	public void setReceiptId(long receiptId) {
		this.receiptId = receiptId;
	}

	public String getStatusRM() {
		return statusRM;
	}

	public void setStatusRM(String statusRM) {
		this.statusRM = statusRM;
	}

	public Date getRealizationDate() {
		return realizationDate;
	}

	public void setRealizationDate(Date realizationDate) {
		this.realizationDate = realizationDate;
	}

	public Date getBounceDate() {
		return bounceDate;
	}

	public void setBounceDate(Date bounceDate) {
		this.bounceDate = bounceDate;
	}

	public String getBounceReason() {
		return bounceReason;
	}

	public void setBounceReason(String bounceReason) {
		this.bounceReason = bounceReason;
	}

	public String getBounceRemarks() {
		return bounceRemarks;
	}

	public void setBounceRemarks(String bounceRemarks) {
		this.bounceRemarks = bounceRemarks;
	}

}
