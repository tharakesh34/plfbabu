package com.penanttech.pff.model.external.bre;

import java.io.Serializable;

import org.codehaus.jackson.annotate.JsonCreator;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.annotate.JsonProperty;


@JsonIgnoreProperties(ignoreUnknown = true)
public class EligibilityApclOutElement implements Serializable{

	private static final long serialVersionUID = 1L;
	
	@JsonProperty("CORE_INC_SALARIED")
	private String coreIncSalaried;
	
	@JsonProperty("ELIGIBLE_INCOME")
	private String eligibleIncome;
	
	@JsonProperty("FOIR_NORM")
	private String foirNorm;
	
	@JsonProperty("INCOME_METHOD")
	private String incomeMethod;
	
	@JsonProperty("LOAN_ELIGIBILITY")
	private String loanEligibility;
	
	@JsonCreator
	public EligibilityApclOutElement(){}

	public String getCoreIncSalaried() {
		return coreIncSalaried;
	}

	public void setCoreIncSalaried(String coreIncSalaried) {
		this.coreIncSalaried = coreIncSalaried;
	}

	public String getEligibleIncome() {
		return eligibleIncome;
	}

	public void setEligibleIncome(String eligibleIncome) {
		this.eligibleIncome = eligibleIncome;
	}

	public String getFoirNorm() {
		return foirNorm;
	}

	public void setFoirNorm(String foirNorm) {
		this.foirNorm = foirNorm;
	}

	public String getIncomeMethod() {
		return incomeMethod;
	}

	public void setIncomeMethod(String incomeMethod) {
		this.incomeMethod = incomeMethod;
	}

	public String getLoanEligibility() {
		return loanEligibility;
	}

	public void setLoanEligibility(String loanEligibility) {
		this.loanEligibility = loanEligibility;
	}
}
