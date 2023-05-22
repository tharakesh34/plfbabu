package com.penanttech.pff.model.external.bre;

import javax.xml.bind.annotation.XmlElement;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
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

	@XmlElement(name = "APPLICANT_DETAILS")
	private ApplicantDetails applicantDetails;
	@XmlElement(name = "ADDRESSDETIALS")
	private AddressDetails addressdetials;
	@XmlElement(name = "EMPLOYMENTDETAILS")
	private EmploymentDetails employmentdetails;
	@XmlElement(name = "BANKING")
	private Banking banking;
	@XmlElement(name = "CONSOLIDATED_BANKING")
	private ConsolidatedBanking consolidatedBanking;
	@XmlElement(name = "INCOME")
	private Income income;
	@XmlElement(name = "BUSINESS")
	private Business business;
	@XmlElement(name = "ELIGIBILITY")
	private Eligibility eligibility;
	@XmlElement(name = "BUREAU")
	private Bureau bureau;
	@XmlElement(name = "LOANOBLIGATION")
	private LoanObligation loanobligation;
	@XmlElement(name = "EDUCATIONDETAIL")
	private EducationDetail educationdetail;
	@XmlElement(name = "POA")
	private Poa poa;

	@JsonCreator
	public ApplicantInElement() {
	    super();
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
