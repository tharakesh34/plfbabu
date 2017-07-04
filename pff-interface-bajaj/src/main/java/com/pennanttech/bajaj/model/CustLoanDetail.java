package com.pennanttech.bajaj.model;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class CustLoanDetail {
	private String segment;
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
		return "CustLoanDetail [segment=" + segment + ", applicationNo="
				+ applicationNo + "]";
	}
	
	
}
