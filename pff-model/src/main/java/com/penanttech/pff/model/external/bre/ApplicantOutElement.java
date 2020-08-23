package com.penanttech.pff.model.external.bre;

import java.io.Serializable;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.annotate.JsonProperty;

import com.penanttech.pff.model.external.bre.BREService.ApplicantDetails;

@JsonIgnoreProperties(ignoreUnknown = true)
public class ApplicantOutElement implements Serializable{
	
	private static final long serialVersionUID = 1L;
	@JsonProperty("APPLICANT_DETAILS")
	private ApplicantDetails applicantDetails;
	
	@JsonProperty("ELIGIBILITY_APCL_OUT")
	private EligibilityApclOut eligApclOut;
	
	public ApplicantOutElement(){}

	public ApplicantDetails getApplicantDetails() {
		return applicantDetails;
	}

	public void setApplicantDetails(ApplicantDetails applicantDetails) {
		this.applicantDetails = applicantDetails;
	}

	public EligibilityApclOut getEligApclOut() {
		return eligApclOut;
	}

	public void setEligApclOut(EligibilityApclOut eligApclOut) {
		this.eligApclOut = eligApclOut;
	}
	
}
