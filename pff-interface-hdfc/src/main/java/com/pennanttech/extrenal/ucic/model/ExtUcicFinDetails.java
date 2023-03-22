package com.pennanttech.extrenal.ucic.model;

import java.util.Date;

public class ExtUcicFinDetails {
	private long custId;
	private long finId;
	private String finreference;
	private String closingStatus;
	private Date closedDate;
	private Date lastmntOn;

	public long getCustId() {
		return custId;
	}

	public void setCustId(long custId) {
		this.custId = custId;
	}

	public long getFinId() {
		return finId;
	}

	public void setFinId(long finId) {
		this.finId = finId;
	}

	public String getFinreference() {
		return finreference;
	}

	public void setFinreference(String finreference) {
		this.finreference = finreference;
	}

	public String getClosingStatus() {
		return closingStatus;
	}

	public void setClosingStatus(String closingStatus) {
		this.closingStatus = closingStatus;
	}

	public Date getClosedDate() {
		return closedDate;
	}

	public void setClosedDate(Date closedDate) {
		this.closedDate = closedDate;
	}

	public Date getLastmntOn() {
		return lastmntOn;
	}

	public void setLastmntOn(Date lastmntOn) {
		this.lastmntOn = lastmntOn;
	}

}
