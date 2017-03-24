package com.pennant.coreinterface.model;

import java.io.Serializable;

public class CoreBankNewCustomer implements Serializable {
		
	private static final long serialVersionUID = -3381150946271855428L;
	
    public CoreBankNewCustomer() {
    	super();
    }
    
	private String operation;
	private String custCtgType;
	private String finReference;
	
	private String custCIF;
	private String custType;
	private String shortName;
	private String country;
	private String branch;
	private String currency;
	
	// ******************************************************//
	// ****************** getter / setter *******************//
	// ******************************************************//
	
	public String getOperation() {
		return operation;
	}
	public void setOperation(String operation) {
		this.operation = operation;
	}
	
	public String getCustCtgType() {
		return custCtgType;
	}
	public void setCustCtgType(String custCtgType) {
		this.custCtgType = custCtgType;
	}
	
	public String getFinReference() {
		return finReference;
	}
	public void setFinReference(String finReference) {
		this.finReference = finReference;
	}
	
	public String getCustCIF() {
		return custCIF;
	}
	public void setCustCIF(String custCIF) {
		this.custCIF = custCIF;
	}
	
	public String getCustType() {
		return custType;
	}
	public void setCustType(String custType) {
		this.custType = custType;
	}
	
	public String getShortName() {
		return shortName;
	}
	public void setShortName(String shortName) {
		this.shortName = shortName;
	}
	
	public String getCountry() {
		return country;
	}
	public void setCountry(String country) {
		this.country = country;
	}
	
	public String getBranch() {
		return branch;
	}
	public void setBranch(String branch) {
		this.branch = branch;
	}
	
	public String getCurrency() {
		return currency;
	}
	public void setCurrency(String currency) {
		this.currency = currency;
	}

}
