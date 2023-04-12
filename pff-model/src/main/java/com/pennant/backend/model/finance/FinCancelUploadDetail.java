package com.pennant.backend.model.finance;

import com.pennant.pff.upload.model.UploadDetails;

public class FinCancelUploadDetail extends UploadDetails {

	private static final long serialVersionUID = 1896787294691046554L;

	private String cancelType;
	private String reason;
	private String remarks;
	private FinanceMain fm;

	public FinCancelUploadDetail() {
		super();
	}

	public String getCancelType() {
		return cancelType;
	}

	public void setCancelType(String cancelType) {
		this.cancelType = cancelType;
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

	public FinanceMain getFm() {
		return fm;
	}

	public void setFm(FinanceMain fm) {
		this.fm = fm;
	}

}
