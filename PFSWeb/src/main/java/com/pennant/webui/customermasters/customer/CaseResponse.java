package com.pennant.webui.customermasters.customer;

public class CaseResponse {

	private String caseNumber;
	private String status;
	private String message;

	// Getter Methods 

	public String getCaseNumber() {
		return caseNumber;
	}

	public String getStatus() {
		return status;
	}

	public String getMessage() {
		return message;
	}

	// Setter Methods 

	public void setCaseNumber(String caseNumber) {
		this.caseNumber = caseNumber;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public void setMessage(String message) {
		this.message = message;
	}

}
