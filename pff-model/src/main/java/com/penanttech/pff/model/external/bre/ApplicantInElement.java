package com.penanttech.pff.model.external.bre;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.penanttech.pff.model.external.bre.BREService.ApplicantDetails;
import com.penanttech.pff.model.external.bre.BREService.Bureau;
import com.penanttech.pff.model.external.bre.BREService.Business;
import com.penanttech.pff.model.external.bre.BREService.ConsolidatedBanking;
import com.penanttech.pff.model.external.bre.BREService.EducationDetail;
import com.penanttech.pff.model.external.bre.BREService.Eligibility;
import com.penanttech.pff.model.external.bre.BREService.EmploymentDetails;
import com.penanttech.pff.model.external.bre.BREService.Income;
import com.penanttech.pff.model.external.bre.BREService.Poa;

@JsonIgnoreProperties(ignoreUnknown = true)
public class ApplicantInElement {

	@JsonProperty("APPLICANT_DETAILS")
	private ApplicantDetails applicantDetails;
	@JsonProperty("ADDRESSDETIALS")
	private AddressDetails addressdetials;
	@JsonProperty("EMPLOYMENTDETAILS")
	private EmploymentDetails employmentdetails;
	@JsonProperty("BANKING")
	private Banking banking;
	@JsonProperty("CONSOLIDATED_BANKING")
	private ConsolidatedBanking consolidatedBanking;
	@JsonProperty("INCOME")
	private Income income;
	@JsonProperty("BUSINESS")
	private Business business;
	@JsonProperty("ELIGIBILITY")
	private Eligibility eligibility;
	@JsonProperty("BUREAU")
	private Bureau bureau;
	@JsonProperty("LOANOBLIGATION")
	private LoanObligation loanobligation;
	@JsonProperty("EDUCATIONDETAIL")
	private EducationDetail educationdetail;
	@JsonProperty("POA")
	private Poa poa;

	@JsonCreator
	public ApplicantInElement() {

	}

	public ApplicantDetails getApplicantDetails() {
		return applicantDetails;
	}

	public void setApplicantDetails(ApplicantDetails applicantDetails) {
		this.applicantDetails = applicantDetails;
	}

	public AddressDetails getAddressdetials() {
		return addressdetials;
	}

	public void setAddressdetials(AddressDetails addressdetials) {
		this.addressdetials = addressdetials;
	}

	public EmploymentDetails getEmploymentdetails() {
		return employmentdetails;
	}

	public void setEmploymentdetails(EmploymentDetails employmentdetails) {
		this.employmentdetails = employmentdetails;
	}

	public Banking getBanking() {
		return banking;
	}

	public void setBanking(Banking banking) {
		this.banking = banking;
	}

	public ConsolidatedBanking getConsolidatedBanking() {
		return consolidatedBanking;
	}

	public void setConsolidatedBanking(ConsolidatedBanking consolidatedBanking) {
		this.consolidatedBanking = consolidatedBanking;
	}

	public Income getIncome() {
		return income;
	}

	public void setIncome(Income income) {
		this.income = income;
	}

	public Business getBusiness() {
		return business;
	}

	public void setBusiness(Business business) {
		this.business = business;
	}

	public Eligibility getEligibility() {
		return eligibility;
	}

	public void setEligibility(Eligibility eligibility) {
		this.eligibility = eligibility;
	}

	public Bureau getBureau() {
		return bureau;
	}

	public void setBureau(Bureau bureau) {
		this.bureau = bureau;
	}

	public LoanObligation getLoanobligation() {
		return loanobligation;
	}

	public void setLoanobligation(LoanObligation loanobligation) {
		this.loanobligation = loanobligation;
	}

	public EducationDetail getEducationdetail() {
		return educationdetail;
	}

	public void setEducationdetail(EducationDetail educationdetail) {
		this.educationdetail = educationdetail;
	}

	public Poa getPoa() {
		return poa;
	}

	public void setPoa(Poa poa) {
		this.poa = poa;
	}

}
