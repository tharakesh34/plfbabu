package com.pennant.backend.model.dedup.external;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;

@XmlAccessorType(XmlAccessType.NONE)
public class DedupeResponse {
	@JsonProperty("RESPCODE")
	private String responseCode;
	@JsonProperty("customerStatusResponse")
	private CustomerStatusResponse customerStatusResponse;
	@JsonProperty("appscore")
	private Appscore appscore;
	@JsonProperty("errorDescription")
	private ErrorDescription errorDescription;
	@JsonProperty("demographicDetails")
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
