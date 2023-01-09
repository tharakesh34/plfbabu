package com.pennant.pff.holdrefund.model;

import java.io.Serializable;

public class FinanceHoldDetail implements Serializable {
	private static final long serialVersionUID = 1L;

	private long finID;
	private String holdStatus;
	private String reason;
	private String remarks;

	public FinanceHoldDetail() {
		super();
	}

	public long getFinID() {
		return finID;
	}

	public void setFinID(long finID) {
		this.finID = finID;
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
