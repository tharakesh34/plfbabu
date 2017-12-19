package com.pennanttech.niyogin.legaldesk.model;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

@XmlType(propOrder = { "purposeOfLoan", "tenure", "intrestType", "rateOfIntrest", "instalmentAmt",
		"instalmentStartdate", "instalmentSchedule", "processingFees", "penaltyCharges", "documentationCharges",
		"foreclosure", "chargesForDihorner", "defaultEmiCharges", "insuranceGstAmt", "disbursementOfLoan", "loanType" })
@XmlRootElement(name = "FormData")
@XmlAccessorType(XmlAccessType.FIELD)
public class FormData {


	@XmlElement(name = "purpose_of_loan")
	private String	purposeOfLoan;

	private String	tenure;

	@XmlElement(name = "intrest_type")
	private String	intrestType;

	@XmlElement(name = "rate_of_intrest")
	private String	rateOfIntrest;

	@XmlElement(name = "instalment_amount")
	private String	instalmentAmt;

	@XmlElement(name = "instalment_startdate")
	private String	instalmentStartdate;

	@XmlElement(name = "instalment_schedule")
	private String	instalmentSchedule;

	@XmlElement(name = "processing_fees")
	private String	processingFees;

	@XmlElement(name = "penal_charges")
	private String	penaltyCharges;

	@XmlElement(name = "documentation_charges")
	private String	documentationCharges;

	private String	foreclosure;

	@XmlElement(name = "charges_for_dihorner")
	private String	chargesForDihorner;

	@XmlElement(name = "default_emi_charges")
	private String	defaultEmiCharges;

	@XmlElement(name = "insurance_amount_gst")
	private String	insuranceGstAmt;

	@XmlElement(name = "disbursement_of_loan")
	private String	disbursementOfLoan;

	@XmlElement(name = "loan_type")
	private String	loanType;

	public String getPurposeOfLoan() {
		return purposeOfLoan;
	}

	public void setPurposeOfLoan(String purposeOfLoan) {
		this.purposeOfLoan = purposeOfLoan;
	}

	public String getTenure() {
		return tenure;
	}

	public void setTenure(String tenure) {
		this.tenure = tenure;
	}

	public String getIntrestType() {
		return intrestType;
	}

	public void setIntrestType(String intrestType) {
		this.intrestType = intrestType;
	}

	public String getRateOfIntrest() {
		return rateOfIntrest;
	}

	public void setRateOfIntrest(String rateOfIntrest) {
		this.rateOfIntrest = rateOfIntrest;
	}

	public String getInstalmentAmt() {
		return instalmentAmt;
	}

	public void setInstalmentAmt(String instalmentAmt) {
		this.instalmentAmt = instalmentAmt;
	}

	public String getInstalmentStartdate() {
		return instalmentStartdate;
	}

	public void setInstalmentStartdate(String instalmentStartdate) {
		this.instalmentStartdate = instalmentStartdate;
	}

	public String getInstalmentSchedule() {
		return instalmentSchedule;
	}

	public void setInstalmentSchedule(String instalmentSchedule) {
		this.instalmentSchedule = instalmentSchedule;
	}

	public String getProcessingFees() {
		return processingFees;
	}

	public void setProcessingFees(String processingFees) {
		this.processingFees = processingFees;
	}

	public String getPenaltyCharges() {
		return penaltyCharges;
	}

	public void setPenaltyCharges(String penaltyCharges) {
		this.penaltyCharges = penaltyCharges;
	}

	public String getDocumentationCharges() {
		return documentationCharges;
	}

	public void setDocumentationCharges(String documentationCharges) {
		this.documentationCharges = documentationCharges;
	}

	public String getForeclosure() {
		return foreclosure;
	}

	public void setForeclosure(String foreclosure) {
		this.foreclosure = foreclosure;
	}

	public String getChargesForDihorner() {
		return chargesForDihorner;
	}

	public void setChargesForDihorner(String chargesForDihorner) {
		this.chargesForDihorner = chargesForDihorner;
	}

	public String getDefaultEmiCharges() {
		return defaultEmiCharges;
	}

	public void setDefaultEmiCharges(String defaultEmiCharges) {
		this.defaultEmiCharges = defaultEmiCharges;
	}

	public String getInsuranceGstAmt() {
		return insuranceGstAmt;
	}

	public void setInsuranceGstAmt(String insuranceGstAmt) {
		this.insuranceGstAmt = insuranceGstAmt;
	}

	public String getDisbursementOfLoan() {
		return disbursementOfLoan;
	}

	public void setDisbursementOfLoan(String disbursementOfLoan) {
		this.disbursementOfLoan = disbursementOfLoan;
	}

	public String getLoanType() {
		return loanType;
	}

	public void setLoanType(String loanType) {
		this.loanType = loanType;
	}

}
