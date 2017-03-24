package com.pennant.backend.model.finance;

import java.util.Date;

public class FinStatusDetail {
	private String finReference;
	private Date valueDate;
	private long custId;
	private String finStatus;
	private String finStatusReason;
	
	public FinStatusDetail() {
		
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
	public void setFinStatusReason(String finStatusReason) {
	    this.finStatusReason = finStatusReason;
    }
	public String getFinStatusReason() {
	    return finStatusReason;
    }

}
