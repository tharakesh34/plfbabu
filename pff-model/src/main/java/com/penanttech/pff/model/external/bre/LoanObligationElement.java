package com.penanttech.pff.model.external.bre;

import java.io.Serializable;

import org.codehaus.jackson.annotate.JsonCreator;
import org.codehaus.jackson.annotate.JsonProperty;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class LoanObligationElement implements Serializable{

	private static final long serialVersionUID = -7536537740033026351L;
	
	@JsonProperty("CUSTOMER_DECLARED_OBLIGATION")
	private String customerDeclaredObligation;
	@JsonProperty("LOAN_AMOUNT")
	private String loanAmount;
	@JsonProperty("TO_BE_REFINANCED")
	private String toBeRefinanced;
	@JsonProperty("TO_BE_CONSIDERED_FOR_OBLIGATION")
	private String toBeConsideredForObligation;
	@JsonProperty("TENURE")
	private String tenure;
	@JsonProperty("REMAINING_TENURE")
	private String remainingTenure;
	@JsonProperty("OBLIGATION_TYPE")
	private String obligationType;
	@JsonProperty("NO_OF_EMI_PAID")
	private String noOfEmiPaid;
	@JsonProperty("OUTSTANDING_LOAN_AMOUNT")
	private String outstandingLoanAmount;
	@JsonProperty("EMI")
	private String emi;
	@JsonProperty("EMI_BOUNCEL3")
	private String emiBouncel3;
	@JsonProperty("EMI_BOUNCEL12")
	private String emiBouncel12;
	@JsonProperty("DPD")
	private String dpd;
	@JsonProperty("BT_TOPUP_FLAG")
	private String btTopupFlag;
	@JsonProperty("BT_FINANCER")
	private String btFinancer;
	@JsonProperty("BT_MOB")
	private String btMob;
	@JsonProperty("BT_EMI")
	private String btEmi;
	@JsonProperty("CONSIDERED_RTR")
	private String consideredRtr;
	

	@JsonCreator
	public LoanObligationElement() {
	}

	public String getCustomerDeclaredObligation() {
		return customerDeclaredObligation;
	}

	public void setCustomerDeclaredObligation(String customerDeclaredObligation) {
		this.customerDeclaredObligation = customerDeclaredObligation;
	}

	public String getLoanAmount() {
		return loanAmount;
	}

	public void setLoanAmount(String loanAmount) {
		this.loanAmount = loanAmount;
	}

	public String getToBeRefinanced() {
		return toBeRefinanced;
	}

	public void setToBeRefinanced(String toBeRefinanced) {
		this.toBeRefinanced = toBeRefinanced;
	}

	public String getToBeConsideredForObligation() {
		return toBeConsideredForObligation;
	}

	public void setToBeConsideredForObligation(String toBeConsideredForObligation) {
		this.toBeConsideredForObligation = toBeConsideredForObligation;
	}

	public String getTenure() {
		return tenure;
	}

	public void setTenure(String tenure) {
		this.tenure = tenure;
	}

	public String getRemainingTenure() {
		return remainingTenure;
	}

	public void setRemainingTenure(String remainingTenure) {
		this.remainingTenure = remainingTenure;
	}

	public String getObligationType() {
		return obligationType;
	}

	public void setObligationType(String obligationType) {
		this.obligationType = obligationType;
	}

	public String getNoOfEmiPaid() {
		return noOfEmiPaid;
	}

	public void setNoOfEmiPaid(String noOfEmiPaid) {
		this.noOfEmiPaid = noOfEmiPaid;
	}

	public String getOutstandingLoanAmount() {
		return outstandingLoanAmount;
	}

	public void setOutstandingLoanAmount(String outstandingLoanAmount) {
		this.outstandingLoanAmount = outstandingLoanAmount;
	}

	public String getEmi() {
		return emi;
	}

	public void setEmi(String emi) {
		this.emi = emi;
	}

	public String getEmiBouncel3() {
		return emiBouncel3;
	}

	public void setEmiBouncel3(String emiBouncel3) {
		this.emiBouncel3 = emiBouncel3;
	}

	public String getEmiBouncel12() {
		return emiBouncel12;
	}

	public void setEmiBouncel12(String emiBouncel12) {
		this.emiBouncel12 = emiBouncel12;
	}

	public String getDpd() {
		return dpd;
	}

	public void setDpd(String dpd) {
		this.dpd = dpd;
	}

	public String getBtTopupFlag() {
		return btTopupFlag;
	}

	public void setBtTopupFlag(String btTopupFlag) {
		this.btTopupFlag = btTopupFlag;
	}

	public String getBtFinancer() {
		return btFinancer;
	}

	public void setBtFinancer(String btFinancer) {
		this.btFinancer = btFinancer;
	}

	public String getBtMob() {
		return btMob;
	}

	public void setBtMob(String btMob) {
		this.btMob = btMob;
	}

	public String getBtEmi() {
		return btEmi;
	}

	public void setBtEmi(String btEmi) {
		this.btEmi = btEmi;
	}

	public String getConsideredRtr() {
		return consideredRtr;
	}

	public void setConsideredRtr(String consideredRtr) {
		this.consideredRtr = consideredRtr;
	}
}
