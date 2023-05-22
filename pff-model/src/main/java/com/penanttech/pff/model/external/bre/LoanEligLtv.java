package com.penanttech.pff.model.external.bre;

import java.io.Serializable;
import java.util.List;

import javax.xml.bind.annotation.XmlElement;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class LoanEligLtv implements Serializable {

	private static final long serialVersionUID = 1L;

	@XmlElement(name = "element")
	private List<LoanElgLtvElement> loanElgLtvElement;

	@JsonCreator
	public LoanEligLtv() {
	    super();
	}

	public List<LoanElgLtvElement> getLoanElgLtvElement() {
		return loanElgLtvElement;
	}

	public void setLoanElgLtvElement(List<LoanElgLtvElement> loanElgLtvElement) {
		this.loanElgLtvElement = loanElgLtvElement;
	}
}
