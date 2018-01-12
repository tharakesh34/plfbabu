package com.pennanttech.niyogin.legaldesk.model;

import java.math.BigDecimal;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

@XmlType(propOrder = { "purposeOfLoan", "borrowerPan","sactionAmt", "tenure", "intrestType", "rateOfIntrest", "instalmentAmt",
		"instalmentStartdate", "instalmentSchedule", "processingFees", "penaltyCharges", "documentationCharges",
		"foreclosure", "chargesForDihorner", "defaultEmiCharges", "insuranceGstAmt", "disbursementOfLoan", "loanType" })
@XmlRootElement(name = "FormData")
@XmlAccessorType(XmlAccessType.FIELD)
public class FormData {

	@XmlElement(name = "purpose_of_loan")
	private String		purposeOfLoan;

	private int			tenure;

	@XmlElement(name = "intrest_type")
	private String		intrestType;
	
	@XmlElement(name = "borrower_pan")
	private String		borrowerPan;
	
	@XmlElement(name = "sancation_amount")
	private BigDecimal		sactionAmt		= BigDecimal.ZERO;

	@XmlElement(name = "rate_of_intrest")
	private String	rateOfIntrest;

	@XmlElement(name = "instalment_amount")
	private BigDecimal	instalmentAmt		= BigDecimal.ZERO;

	@XmlElement(name = "instalment_startdate")
	private String		instalmentStartdate;

	@XmlElement(name = "instalment_schedule")
	private int			instalmentSchedule;

	@XmlElement(name = "processing_fees")
	private String		processingFees;

	@XmlElement(name = "penal_charges")
	private String		penaltyCharges;

	@XmlElement(name = "documentation_charges")
	private String		documentationCharges;

	private String		foreclosure;

	@XmlElement(name = "charges_for_dihorner")
	private String		chargesForDihorner;

	@XmlElement(name = "default_emi_charges")
	private String		defaultEmiCharges;

	@XmlElement(name = "insurance_amount_gst")
	private String		insuranceGstAmt;

	@XmlElement(name = "disbursement_of_loan")
	private BigDecimal	disbursementOfLoan	= BigDecimal.ZERO;

	@XmlElement(name = "loan_type")
	private String		loanType;

	public String getPurposeOfLoan() {
		return purposeOfLoan;
	}

	public void setPurposeOfLoan(String purposeOfLoan) {
		this.purposeOfLoan = purposeOfLoan;
	}

	public int getTenure() {
		return tenure;
	}

	public void setTenure(int tenure) {
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

	public BigDecimal getInstalmentAmt() {
		return instalmentAmt;
	}

	public void setInstalmentAmt(BigDecimal instalmentAmt) {
		this.instalmentAmt = instalmentAmt;
	}

	public String getInstalmentStartdate() {
		return instalmentStartdate;
	}

	public void setInstalmentStartdate(String instalmentStartdate) {
		this.instalmentStartdate = instalmentStartdate;
	}

	public int getInstalmentSchedule() {
		return instalmentSchedule;
	}

	public void setInstalmentSchedule(int instalmentSchedule) {
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

	public BigDecimal getDisbursementOfLoan() {
		return disbursementOfLoan;
	}

	public void setDisbursementOfLoan(BigDecimal disbursementOfLoan) {
		this.disbursementOfLoan = disbursementOfLoan;
	}

	public String getLoanType() {
		return loanType;
	}

	public void setLoanType(String loanType) {
		this.loanType = loanType;
	}
	
	public String getBorrowerPan() {
		return borrowerPan;
	}

	public void setBorrowerPan(String borrowerPan) {
		this.borrowerPan = borrowerPan;
	}

	public BigDecimal getSactionAmt() {
		return sactionAmt;
	}

	public void setSactionAmt(BigDecimal sactionAmt) {
		this.sactionAmt = sactionAmt;
	}
}
