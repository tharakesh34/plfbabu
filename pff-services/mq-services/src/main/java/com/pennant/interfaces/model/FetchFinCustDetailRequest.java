package com.pennant.interfaces.model;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name="FetchFinCustDetailRequest")
public class FetchFinCustDetailRequest {
	
	private String referenceNum;
	private String finReference;
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
	
	@XmlElement(name="ISNumber")
	public String getFinReference() {
		return finReference;
	}
	public void setFinReference(String finReference) {
		this.finReference = finReference;
	}
	
	@XmlElement(name="TimeStamp")
	public long getTimestamp() {
		return timestamp;
	}
	public void setTimestamp(long timestamp) {
		this.timestamp = timestamp;
	}
	
}
