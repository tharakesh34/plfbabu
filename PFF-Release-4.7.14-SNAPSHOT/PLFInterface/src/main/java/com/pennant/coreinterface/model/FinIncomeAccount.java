package com.pennant.coreinterface.model;

import java.io.Serializable;
import java.util.Date;

public class FinIncomeAccount implements Serializable { 
	
	private static final long serialVersionUID = 5044381253061287258L;
	
	public FinIncomeAccount() {
		super();
	}
	
	private String finReference;
	private String incomeAccount;
	private long custId;
	private String finType;
	private String acCcy;
	private Date lastMntOn;

	
	
	// ******************************************************//
	// ****************** getter / setter *******************//
	// ******************************************************//
	
	
	public String getFinReference() {
		return finReference;
	}
	public void setFinReference(String finReference) {
		this.finReference = finReference;
	}
	
	public String getIncomeAccount() {
		return incomeAccount;
	}
	public void setIncomeAccount(String incomeAccount) {
		this.incomeAccount = incomeAccount;
	}
	
	public long getCustId() {
		return custId;
	}
	public void setCustId(long custId) {
		this.custId = custId;
	}
	
	public String getFinType() {
		return finType;
	}
	public void setFinType(String finType) {
		this.finType = finType;
	}
	
	public String getAcCcy() {
		return acCcy;
	}
	public void setAcCcy(String acCcy) {
		this.acCcy = acCcy;
	}
	
	public Date getLastMntOn() {
		return lastMntOn;
	}
	public void setLastMntOn(Date lastMntOn) {
		this.lastMntOn = lastMntOn;
	}
	
}
