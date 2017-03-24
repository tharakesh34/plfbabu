package com.pennant.interfaces.model;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name="DDAUpdateRequest")
public class DDAUpdateRequest {
	
	private String referenceNum;
	private String type;// S = Sponsoring Bank, P= Paying Bank
	private String finReference;
	private String accNumber;
	private String dDAReferenceNo;
	private String action;
	private long timestamp;

	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++ //
	// ++++++++++++++++++ getter / setter +++++++++++++++++++ //
	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++ //
	
	@XmlElement(name="ReferenceNum")
	public String getReferenceNum() {
		return referenceNum;
	}
	public void setReferenceNum(String referenceNum) {
		this.referenceNum = referenceNum;
	}
	
	@XmlElement(name="Type")
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	
	@XmlElement(name="ISNumber")
	public String getFinReference() {
		return finReference;
	}
	public void setFinReference(String finReference) {
		this.finReference = finReference;
	}
	
	@XmlElement(name="AccountNumber")
	public String getAccNumber() {
		return accNumber;
	}
	public void setAccNumber(String accNumber) {
		this.accNumber = accNumber;
	}
	
	@XmlElement(name="DDAReferenceNo")
	public String getDDAReferenceNo() {
		return dDAReferenceNo;
	}
	public void setDDAReferenceNo(String dDAReferenceNo) {
		this.dDAReferenceNo = dDAReferenceNo;
	}
	
	@XmlElement(name="Action")
	public String getAction() {
		return action;
	}
	public void setAction(String action) {
		this.action = action;
	}
	
	@XmlElement(name="TimeStamp")
	public long getTimestamp() {
		return timestamp;
	}
	public void setTimestamp(long timestamp) {
		this.timestamp = timestamp;
	}
	
}
