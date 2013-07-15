package com.pennant.backend.model.coremasters;

import java.io.Serializable;

public class CollateralAssignmentDetail implements Serializable {

    private static final long serialVersionUID = -292340612022755109L;
	
	private String collateralReference = "AP2H0123";
	private String dealType = "";
	private String reference = "";
	private String branch = "";
	private String accNumber = "";
	private String custCIF = "300006";
	private String custShortName = "Cust MR1 TST";
	private String currency = "QAR"; 
	private String collateralType = "AAA";
	private String collateralTypeDesc = "Testing AAA";
	private String bankValuation = "900000.00";
	private String collateralAvail = "0.00";
	private String assignRule = "1";
	private String percentOfBank = "";
	private String maxAmount = "0";
	private String equivalent = "900000.00";
	private String assigned = "900000.00";
	
	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//
	// ++++++++++++++++++ getter / setter +++++++++++++++++++//
	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//
	
	public String getCollateralReference() {
    	return collateralReference;
    }
	public void setCollateralReference(String collateralReference) {
    	this.collateralReference = collateralReference;
    }
	
	public String getDealType() {
    	return dealType;
    }
	public void setDealType(String dealType) {
    	this.dealType = dealType;
    }
	
	public String getReference() {
    	return reference;
    }
	public void setReference(String reference) {
    	this.reference = reference;
    }
	
	public String getBranch() {
    	return branch;
    }
	public void setBranch(String branch) {
    	this.branch = branch;
    }
	
	public String getAccNumber() {
    	return accNumber;
    }
	public void setAccNumber(String accNumber) {
    	this.accNumber = accNumber;
    }
	
	public String getCustCIF() {
    	return custCIF;
    }
	public void setCustCIF(String custCIF) {
    	this.custCIF = custCIF;
    }
	
	public void setCurrency(String currency) {
	    this.currency = currency;
    }
	public String getCurrency() {
	    return currency;
    }
	public String getCustShortName() {
    	return custShortName;
    }
	public void setCustShortName(String custShortName) {
    	this.custShortName = custShortName;
    }
	
	public String getCollateralType() {
    	return collateralType;
    }
	public void setCollateralType(String collateralType) {
    	this.collateralType = collateralType;
    }
	
	public String getBankValuation() {
    	return bankValuation;
    }
	public void setBankValuation(String bankValuation) {
    	this.bankValuation = bankValuation;
    }
	
	public String getCollateralAvail() {
    	return collateralAvail;
    }
	public void setCollateralAvail(String collateralAvail) {
    	this.collateralAvail = collateralAvail;
    }
	
	public String getAssignRule() {
    	return assignRule;
    }
	public void setAssignRule(String assignRule) {
    	this.assignRule = assignRule;
    }
	
	public String getPercentOfBank() {
    	return percentOfBank;
    }
	public void setPercentOfBank(String percentOfBank) {
    	this.percentOfBank = percentOfBank;
    }
	
	public String getMaxAmount() {
    	return maxAmount;
    }
	public void setMaxAmount(String maxAmount) {
    	this.maxAmount = maxAmount;
    }
	
	public String getEquivalent() {
    	return equivalent;
    }
	public void setEquivalent(String equivalent) {
    	this.equivalent = equivalent;
    }
	
	public void setAssigned(String assigned) {
	    this.assigned = assigned;
    }
	public String getAssigned() {
	    return assigned;
    }
	public void setCollateralTypeDesc(String collateralTypeDesc) {
	    this.collateralTypeDesc = collateralTypeDesc;
    }
	public String getCollateralTypeDesc() {
	    return collateralTypeDesc;
    }
	
}
