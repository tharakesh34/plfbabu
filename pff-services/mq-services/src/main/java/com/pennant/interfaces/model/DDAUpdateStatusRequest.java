package com.pennant.interfaces.model;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "DDAUpdateStatusRequest")
public class DDAUpdateStatusRequest {

	private String referenceNum;
	private String finReference;
	private String responseType;
	private String responseCode;
	private String responseDesc;
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
	
	@XmlElement(name="FinanceRef")
	public String getFinReference() {
		return finReference;
	}
	
	public void setFinReference(String finReference) {
		this.finReference = finReference;
	}
	
	@XmlElement(name="ResponseType")
	public String getResponseType() {
		return responseType;
	}

	public void setResponseType(String responseType) {
		this.responseType = responseType;
	}

	@XmlElement(name="ResponseCode")
	public String getResponseCode() {
		return responseCode;
	}

	public void setResponseCode(String responseCode) {
		this.responseCode = responseCode;
	}

	@XmlElement(name="ResponseDescription")
	public String getResponseDesc() {
		return responseDesc;
	}

	public void setResponseDesc(String responseDesc) {
		this.responseDesc = responseDesc;
	}

	@XmlElement(name="Timestamp")
	public long getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(long timestamp) {
		this.timestamp = timestamp;
	}
}
