package com.pennant.interfaceservice.model;

import java.sql.Timestamp;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "LimitDetailsRequest")

public class CustomerLimitDetailRequest {

	private String referenceNum;
	private String limitRef;
	private Timestamp timestamp;

	@XmlElement(name="ReferenceNum")
	public String getReferenceNum() {
		return referenceNum;
	}

	public void setReferenceNum(String referenceNum) {
		this.referenceNum = referenceNum;
	}

	@XmlElement(name="LimitRef")
	public String getLimitRef() {
		return limitRef;
	}

	public void setLimitRef(String limitRef) {
		this.limitRef = limitRef;
	}
                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                
	@XmlElement(name="Timestamp")
	public Timestamp getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(Timestamp timestamp) {
		this.timestamp = timestamp;
	}
}
