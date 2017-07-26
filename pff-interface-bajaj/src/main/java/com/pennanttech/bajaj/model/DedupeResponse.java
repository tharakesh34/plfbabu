package com.pennanttech.bajaj.model;

import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;

@XmlAccessorType(XmlAccessType.NONE)
public class DedupeResponse {
	@XmlElement(name = "RESPCODE")
	private String responseCode;
	@XmlElement(name = "customerStatusResponse")
	private CustomerStatusResponse customerStatusResponse;
	@XmlElement(name = "appscore")
	private Appscore appscore;
	@XmlElement(name = "errorDescription")
	private ErrorDescription errorDescription;
	@XmlElement(name = "demographicDetails")
	private List<DemographicDetail> demographicDetails;

	public String getResponseCode() {
		return responseCode;
	}

	public void setResponseCode(String responseCode) {
		this.responseCode = responseCode;
	}

	public CustomerStatusResponse getCustomerStatusResponse() {
		return customerStatusResponse;
	}

	public void setCustomerStatusResponse(CustomerStatusResponse customerStatusResponse) {
		this.customerStatusResponse = customerStatusResponse;
	}

	public Appscore getAppscore() {
		return appscore;
	}

	public void setAppscore(Appscore appscore) {
		this.appscore = appscore;
	}

	public ErrorDescription getErrorDescription() {
		return errorDescription;
	}

	public void setErrorDescription(ErrorDescription errorDescription) {
		this.errorDescription = errorDescription;
	}

	public List<DemographicDetail> getDemographicDetails() {
		return demographicDetails;
	}

	public void setDemographicDetails(List<DemographicDetail> demographicDetails) {
		this.demographicDetails = demographicDetails;
	}

	@Override
	public String toString() {
		return "DedupeResponse [responseCode=" + responseCode + ", customerStatusResponse=" + customerStatusResponse
				+ ", appscore=" + appscore + ", errorDescription=" + errorDescription + ", demographicDetails="
				+ demographicDetails + "]";
	}

}
