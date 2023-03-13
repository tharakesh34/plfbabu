package com.pennant.pff.holdrefund.model;

import com.pennant.pff.upload.model.UploadDetails;

public class HoldRefundUploadDetail extends UploadDetails {
	private static final long serialVersionUID = 1L;

	private String holdStatus;
	private String reason;
	private String remarks;

	public HoldRefundUploadDetail() {
		super();
	}

	public String getHoldStatus() {
		return holdStatus;
	}

	public void setHoldStatus(String holdStatus) {
		this.holdStatus = holdStatus;
	}

	public String getReason() {
		return reason;
	}

	public void setReason(String reason) {
		this.reason = reason;
	}

	public String getRemarks() {
		return remarks;
	}

	public void setRemarks(String remarks) {
		this.remarks = remarks;
	}

}
