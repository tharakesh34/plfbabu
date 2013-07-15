package com.pennant.backend.model.customermasters;

import java.io.Serializable;
import java.util.Date;

public class CustomerLimit implements Serializable{

    private static final long serialVersionUID = -157358894725095946L;
    
    private String custCIF;
    private String custLocation;
    private String custShortName;
    private String limitCategory;
    private String currency;
    private Date earliestExpiryDate;
    private String branch;
    private boolean repeatThousands;
    private boolean checkLimit;
    private String seqNum;
    
	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//
	// ++++++++++++++++++ getter / setter +++++++++++++++++++//
	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//
    
	public String getCustCIF() {
    	return custCIF;
    }
	public void setCustCIF(String custCIF) {
    	this.custCIF = custCIF;
    }
	
	public String getCustLocation() {
    	return custLocation;
    }
	public void setCustLocation(String custLocation) {
    	this.custLocation = custLocation;
    }
	
	public String getCustShortName() {
    	return custShortName;
    }
	public void setCustShortName(String custShortName) {
    	this.custShortName = custShortName;
    }
	
	public String getLimitCategory() {
    	return limitCategory;
    }
	public void setLimitCategory(String limitCategory) {
    	this.limitCategory = limitCategory;
    }
	
	public String getCurrency() {
    	return currency;
    }
	public void setCurrency(String currency) {
    	this.currency = currency;
    }
	
	public Date getEarliestExpiryDate() {
    	return earliestExpiryDate;
    }
	public void setEarliestExpiryDate(Date earliestExpiryDate) {
    	this.earliestExpiryDate = earliestExpiryDate;
    }
	
	public String getBranch() {
    	return branch;
    }
	public void setBranch(String branch) {
    	this.branch = branch;
    }
	
	public boolean isRepeatThousands() {
    	return repeatThousands;
    }
	public void setRepeatThousands(boolean repeatThousands) {
    	this.repeatThousands = repeatThousands;
    }
	public boolean isCheckLimit() {
    	return checkLimit;
    }
	public void setCheckLimit(boolean checkLimit) {
    	this.checkLimit = checkLimit;
    }
	
	public String getSeqNum() {
    	return seqNum;
    }
	public void setSeqNum(String seqNum) {
    	this.seqNum = seqNum;
    }
	
}
