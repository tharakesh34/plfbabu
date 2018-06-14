package com.pennanttech.bajaj.model;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;

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
