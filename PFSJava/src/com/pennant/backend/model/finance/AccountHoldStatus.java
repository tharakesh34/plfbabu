package com.pennant.backend.model.finance;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

public class AccountHoldStatus implements Serializable {

    private static final long serialVersionUID = -5722811453434523809L;
	
	private String account;
	private BigDecimal curODAmount;
	private Date valueDate;
	private String holdStatus ="";
	private String statusDesc ="";
	
	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//
	// ++++++++++++++++++ getter / setter +++++++++++++++++++//
	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//
	
	public String getAccount() {
    	return account;
    }
	public void setAccount(String account) {
    	this.account = account;
    }
	
	public BigDecimal getCurODAmount() {
    	return curODAmount;
    }
	public void setCurODAmount(BigDecimal curODAmount) {
    	this.curODAmount = curODAmount;
    }
	
	public Date getValueDate() {
    	return valueDate;
    }
	public void setValueDate(Date valueDate) {
    	this.valueDate = valueDate;
    }
	
	public String getHoldStatus() {
    	return holdStatus;
    }
	public void setHoldStatus(String holdStatus) {
    	this.holdStatus = holdStatus;
    }

	public String getStatusDesc() {
    	return statusDesc;
    }
	public void setStatusDesc(String statusDesc) {
    	this.statusDesc = statusDesc;
    }
	
}
