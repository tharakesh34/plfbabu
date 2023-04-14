package com.pennant.pff.writeoffupload.model;

import com.pennant.pff.upload.model.UploadDetails;

public class WriteOffUploadDetail extends UploadDetails {
	private static final long serialVersionUID = 1L;

	private String remarks;
	private String event;
	private long receiptId = Long.MIN_VALUE;

	public WriteOffUploadDetail() {
		super();
	}

	public String getRemarks() {
		return remarks;
	}

	public void setRemarks(String remarks) {
		this.remarks = remarks;
	}

	public String getEvent() {
		return event;
	}

	public void setEvent(String event) {
		this.event = event;
	}

	public long getReceiptId() {
		return receiptId;
	}

	public void setReceiptId(long receiptId) {
		this.receiptId = receiptId;
	}

}
