package com.pennant.coreinterface.model;

public class FinanceCancellation {
	private String	dsReqFinRef;//fin reference
	private String	dsReqLnkTID;//Linked trans id

	private String	dsRspCount;
	private String	dsRspFinRef;// fin reference
	private String	dsRspErr;
	private String	dsRspErrD;

	private String	dsRspFinEvent;//
	private String	dsRspLnkTID;//Linked trans id
	private String	dsRspPOD;//post date 
	private String	dsRspAB;//branch
	private String	dsRspAN;//account
	private String	dsRspAS;//Sub head
	private String	dsRspPostRef;
	private String	dsRspStatus;//
	private String	dsRspOrder;

	public FinanceCancellation(String errorMessage) {
	   this.dsRspErrD=errorMessage;
    }
	
	public FinanceCancellation() {
    }

	public String getDsReqFinRef() {
    	return dsReqFinRef;
    }

	public String getDsReqLnkTID() {
    	return dsReqLnkTID;
    }

	public String getDsRspCount() {
    	return dsRspCount;
    }

	public String getDsRspFinRef() {
    	return dsRspFinRef;
    }

	public String getDsRspErr() {
    	return dsRspErr;
    }

	public String getDsRspErrD() {
    	return dsRspErrD;
    }

	public String getDsRspFinEvent() {
    	return dsRspFinEvent;
    }

	public String getDsRspLnkTID() {
    	return dsRspLnkTID;
    }

	public String getDsRspPOD() {
    	return dsRspPOD;
    }

	public String getDsRspAB() {
    	return dsRspAB;
    }

	public String getDsRspAN() {
    	return dsRspAN;
    }

	public String getDsRspAS() {
    	return dsRspAS;
    }

	public String getDsRspPostRef() {
    	return dsRspPostRef;
    }

	public String getDsRspStatus() {
    	return dsRspStatus;
    }

	public String getDsRspOrder() {
    	return dsRspOrder;
    }

	public void setDsReqFinRef(String dsReqFinRef) {
    	this.dsReqFinRef = dsReqFinRef;
    }

	public void setDsReqLnkTID(String dsReqLnkTID) {
    	this.dsReqLnkTID = dsReqLnkTID;
    }

	public void setDsRspCount(String dsRspCount) {
    	this.dsRspCount = dsRspCount;
    }

	public void setDsRspFinRef(String dsRspFinRef) {
    	this.dsRspFinRef = dsRspFinRef;
    }

	public void setDsRspErr(String dsRspErr) {
    	this.dsRspErr = dsRspErr;
    }

	public void setDsRspErrD(String dsRspErrD) {
    	this.dsRspErrD = dsRspErrD;
    }

	public void setDsRspFinEvent(String dsRspFinEvent) {
    	this.dsRspFinEvent = dsRspFinEvent;
    }

	public void setDsRspLnkTID(String dsRspLnkTID) {
    	this.dsRspLnkTID = dsRspLnkTID;
    }

	public void setDsRspPOD(String dsRspPOD) {
    	this.dsRspPOD = dsRspPOD;
    }

	public void setDsRspAB(String dsRspAB) {
    	this.dsRspAB = dsRspAB;
    }

	public void setDsRspAN(String dsRspAN) {
    	this.dsRspAN = dsRspAN;
    }

	public void setDsRspAS(String dsRspAS) {
    	this.dsRspAS = dsRspAS;
    }

	public void setDsRspPostRef(String dsRspPostRef) {
    	this.dsRspPostRef = dsRspPostRef;
    }

	public void setDsRspStatus(String dsRspStatus) {
    	this.dsRspStatus = dsRspStatus;
    }

	public void setDsRspOrder(String dsRspOrder) {
    	this.dsRspOrder = dsRspOrder;
    }

	
}