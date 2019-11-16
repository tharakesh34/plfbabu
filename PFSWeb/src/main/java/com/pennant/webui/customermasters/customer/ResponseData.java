package com.pennant.webui.customermasters.customer;

public class ResponseData {
	private ReturnStatus returnStatus;

	private CaseResponse caseResponse;

	public ReturnStatus getReturnStatus() {
		return returnStatus;
	}

	public void setReturnStatus(ReturnStatus returnStatus) {
		this.returnStatus = returnStatus;
	}

	public CaseResponse getCaseResponse() {
		return caseResponse;
	}

	public void setCaseResponse(CaseResponse caseResponse) {
		this.caseResponse = caseResponse;
	}

}
