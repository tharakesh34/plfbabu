package com.penanttech.pff.model.external.bre;

import java.io.Serializable;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;


@JsonIgnoreProperties(ignoreUnknown = true)
public class LoanEligLtv implements Serializable{

	private static final long serialVersionUID = 1L;
	
	@JsonProperty("element")
	private List<LoanElgLtvElement> loanElgLtvElement;
	
	@JsonCreator
	public LoanEligLtv(){}

	public List<LoanElgLtvElement> getLoanElgLtvElement() {
		return loanElgLtvElement;
	}

	public void setLoanElgLtvElement(List<LoanElgLtvElement> loanElgLtvElement) {
		this.loanElgLtvElement = loanElgLtvElement;
	}
}
