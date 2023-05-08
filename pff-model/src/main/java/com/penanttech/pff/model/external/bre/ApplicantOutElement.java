package com.penanttech.pff.model.external.bre;

import java.io.Serializable;

import javax.xml.bind.annotation.XmlElement;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.penanttech.pff.model.external.bre.BREService.ApplicantDetails;

@JsonIgnoreProperties(ignoreUnknown = true)
public class ApplicantOutElement implements Serializable {

	private static final long serialVersionUID = 1L;
	@XmlElement(name = "APPLICANT_DETAILS")
	private ApplicantDetails applicantDetails;

	@XmlElement(name = "ELIGIBILITY_APCL_OUT")
	private EligibilityApclOut eligApclOut;

	public ApplicantOutElement() {
	    super();
	}

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
