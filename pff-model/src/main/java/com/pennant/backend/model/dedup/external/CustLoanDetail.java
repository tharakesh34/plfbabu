package com.pennant.backend.model.dedup.external;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;

@XmlAccessorType(XmlAccessType.NONE)
public class CustLoanDetail {

	@XmlElement
	private String segment;
	@XmlElement
	private String applicationNo;

	public String getSegment() {
		return segment;
	}

	public void setSegment(String segment) {
		this.segment = segment;
	}

	public String getApplicationNo() {
		return applicationNo;
	}

	public void setApplicationNo(String applicationNo) {
		this.applicationNo = applicationNo;
	}

	@Override
	public String toString() {
		return "CustLoanDetail [segment=" + segment + ", applicationNo=" + applicationNo + "]";
	}

}
