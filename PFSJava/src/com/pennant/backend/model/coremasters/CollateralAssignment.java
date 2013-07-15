package com.pennant.backend.model.coremasters;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class CollateralAssignment implements Serializable {

    private static final long serialVersionUID = -292340612022755109L;
	
    private String dealType = "DBC";
    private String branch ="BAHR";
    private String accNumber = "";
    private String commitRef ="";
    
    private String custCIF = "300006";
	private String custShortName = "Cust MR1 TST";
	private String reference = "BLTCPZ01";
	private String faceValue = "1000000.00";
	private String currency ="QAR";
	private String actualValue = "963634.40";
	private String currentCover = "900000.00";
	private String shortFall ="63634.40";
	
	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//
	// ++++++++++++++++++ getter / setter +++++++++++++++++++//
	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//
	
	public void setDealType(String dealType) {
	    this.dealType = dealType;
    }
	public String getDealType() {
	    return dealType;
    }
	
	public void setBranch(String branch) {
	    this.branch = branch;
    }
	public String getBranch() {
	    return branch;
    }
	
	public void setAccNumber(String accNumber) {
	    this.accNumber = accNumber;
    }
	public String getAccNumber() {
	    return accNumber;
    }
	
	public void setCommitRef(String commitRef) {
	    this.commitRef = commitRef;
    }
	public String getCommitRef() {
	    return commitRef;
    }
	
	public String getCustCIF() {
    	return custCIF;
    }
	public void setCustCIF(String custCIF) {
    	this.custCIF = custCIF;
    }
	
	public String getCustShortName() {
    	return custShortName;
    }
	public void setCustShortName(String custShortName) {
    	this.custShortName = custShortName;
    }
	
	public String getReference() {
    	return reference;
    }
	public void setReference(String reference) {
    	this.reference = reference;
    }
	
	public String getFaceValue() {
    	return faceValue;
    }
	public void setFaceValue(String faceValue) {
    	this.faceValue = faceValue;
    }
	
	public String getCurrency() {
    	return currency;
    }
	public void setCurrency(String currency) {
    	this.currency = currency;
    }
	
	public String getActualValue() {
    	return actualValue;
    }
	public void setActualValue(String actualValue) {
    	this.actualValue = actualValue;
    }
	
	public String getCurrentCover() {
    	return currentCover;
    }
	public void setCurrentCover(String currentCover) {
    	this.currentCover = currentCover;
    }
	
	public String getShortFall() {
    	return shortFall;
    }
	public void setShortFall(String shortFall) {
    	this.shortFall = shortFall;
    }
	
	public List<CollateralAssignmentDetail> getAssignmentDetails() {
		List<CollateralAssignmentDetail> assignmentDetails = new ArrayList<CollateralAssignmentDetail>();
		assignmentDetails.add(new CollateralAssignmentDetail());		
	    return assignmentDetails;
    }
	
}
