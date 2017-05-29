package com.pennanttech.model;

import java.util.List;

public class DedupCustomerResponse {
	
	private String response;
	private String errorCode;
	private String errorDesc;
	private List<DedupCustomerDetail> dedupCustomerDetails;
	public String getResponse() {
		return response;
	}
	public void setResponse(String response) {
		this.response = response;
	}
	public String getErrorCode() {
		return errorCode;
	}
	public void setErrorCode(String errorCode) {
		this.errorCode = errorCode;
	}
	public String getErrorDesc() {
		return errorDesc;
	}
	public void setErrorDesc(String errorDesc) {
		this.errorDesc = errorDesc;
	}
	public List<DedupCustomerDetail> getDedupCustomerDetails() {
		return dedupCustomerDetails;
	}
	public void setDedupCustomerDetails(
			List<DedupCustomerDetail> dedupCustomerDetails) {
		this.dedupCustomerDetails = dedupCustomerDetails;
	}
	@Override
	public String toString() {
		return "DedupCustomerResponse [response=" + response + ", errorCode="
				+ errorCode + ", errorDesc=" + errorDesc
				+ ", dedupCustomerDetails=" + dedupCustomerDetails + "]";
	}

	
}
