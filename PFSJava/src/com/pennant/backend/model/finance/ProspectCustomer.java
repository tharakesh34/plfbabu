package com.pennant.backend.model.finance;

import java.io.Serializable;

public class ProspectCustomer implements Serializable {

    private static final long serialVersionUID = 5631543033048266883L;

    private long custId = Long.MIN_VALUE;
    private String custCIF;
    private String custShrtName;
    private String custTypeCtg;
    private String finReference;
    private String custDftBranch;
    
	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//
	// ++++++++++++++++++ getter / setter +++++++++++++++++++//
	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//
    
	public long getCustId() {
    	return custId;
    }
	public void setCustId(long custId) {
    	this.custId = custId;
    }
	
	public String getCustCIF() {
    	return custCIF;
    }
	public void setCustCIF(String custCIF) {
    	this.custCIF = custCIF;
    }
	
	public String getCustShrtName() {
    	return custShrtName;
    }
	public void setCustShrtName(String custShrtName) {
    	this.custShrtName = custShrtName;
    }
	
	public void setFinReference(String finReference) {
	    this.finReference = finReference;
    }
	public String getFinReference() {
	    return finReference;
    }
	
	public void setCustTypeCtg(String custTypeCtg) {
	    this.custTypeCtg = custTypeCtg;
    }
	public String getCustTypeCtg() {
	    return custTypeCtg;
    }
	
	public void setCustDftBranch(String custDftBranch) {
	    this.custDftBranch = custDftBranch;
    }
	public String getCustDftBranch() {
	    return custDftBranch;
    }
    
}
