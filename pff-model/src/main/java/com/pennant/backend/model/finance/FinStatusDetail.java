package com.pennant.backend.model.finance;

import java.util.Date;

public class FinStatusDetail {

	private long finID;
	private String finReference;
	private Date valueDate;
	private long custId;
	private String finStatus;
	private int ODDays;

	public FinStatusDetail() {
	    super();
	}

	public long getFinID() {
		return finID;
	}

	public void setFinID(long finID) {
		this.finID = finID;
	}

	public String getFinReference() {
		return finReference;
	}

	public void setFinReference(String finReference) {
		this.finReference = finReference;
	}

	public Date getValueDate() {
		return valueDate;
	}

	public void setValueDate(Date valueDate) {
		this.valueDate = valueDate;
	}

	public long getCustId() {
		return custId;
	}

	public void setCustId(long custId) {
		this.custId = custId;
	}

	public String getFinStatus() {
		return finStatus;
	}

	public void setFinStatus(String finStatus) {
		this.finStatus = finStatus;
	}

	public void setODDays(int ODDays) {
		this.ODDays = ODDays;
	}

	public int getODDays() {
		return ODDays;
	}

}
