package com.pennanttech.ws.model.finance;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;

@XmlAccessorType(XmlAccessType.NONE)
public class LoanStatusDetails implements Serializable {
	private static final long serialVersionUID = 1L;

	@JsonProperty("finance")
	private List<LoanStatus> loanSatusDetails = new ArrayList<>();

	public LoanStatusDetails() {
		super();
	}

	public List<LoanStatus> getLoanSatusDetails() {
		return loanSatusDetails;
	}

	public void setLoanSatusDetails(List<LoanStatus> loanSatusDetails) {
		this.loanSatusDetails = loanSatusDetails;
	}

}
