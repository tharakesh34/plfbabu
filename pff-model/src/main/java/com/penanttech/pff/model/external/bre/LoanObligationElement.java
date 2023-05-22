package com.penanttech.pff.model.external.bre;

import java.io.Serializable;

import javax.xml.bind.annotation.XmlElement;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class LoanObligationElement implements Serializable {

	private static final long serialVersionUID = -7536537740033026351L;

	@XmlElement(name = "CUSTOMER_DECLARED_OBLIGATION")
	private String customerDeclaredObligation;
	@XmlElement(name = "LOAN_AMOUNT")
	private String loanAmount;
	@XmlElement(name = "TO_BE_REFINANCED")
	private String toBeRefinanced;
	@XmlElement(name = "TO_BE_CONSIDERED_FOR_OBLIGATION")
	private String toBeConsideredForObligation;
	@XmlElement(name = "TENURE")
	private String tenure;
	@XmlElement(name = "REMAINING_TENURE")
	private String remainingTenure;
	@XmlElement(name = "OBLIGATION_TYPE")
	private String obligationType;
	@XmlElement(name = "NO_OF_EMI_PAID")
	private String noOfEmiPaid;
	@XmlElement(name = "OUTSTANDING_LOAN_AMOUNT")
	private String outstandingLoanAmount;
	@XmlElement(name = "EMI")
	private String emi;
	@XmlElement(name = "EMI_BOUNCEL3")
	private String emiBouncel3;
	@XmlElement(name = "EMI_BOUNCEL12")
	private String emiBouncel12;
	@XmlElement(name = "DPD")
	private String dpd;
	@XmlElement(name = "BT_TOPUP_FLAG")
	private String btTopupFlag;
	@XmlElement(name = "BT_FINANCER")
	private String btFinancer;
	@XmlElement(name = "BT_MOB")
	private String btMob;
	@XmlElement(name = "BT_EMI")
	private String btEmi;
	@XmlElement(name = "CONSIDERED_RTR")
	private String consideredRtr;

	@JsonCreator
	public LoanObligationElement() {
	    super();
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
