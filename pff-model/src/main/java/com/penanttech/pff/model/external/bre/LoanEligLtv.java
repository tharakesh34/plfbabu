package com.penanttech.pff.model.external.bre;

import java.io.Serializable;
import java.util.List;

import org.codehaus.jackson.annotate.JsonCreator;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.annotate.JsonProperty;


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
