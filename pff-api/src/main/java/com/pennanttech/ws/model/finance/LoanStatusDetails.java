package com.pennanttech.ws.model.finance;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;

@XmlAccessorType(XmlAccessType.NONE)
public class LoanStatusDetails implements Serializable {
	private static final long serialVersionUID = 1L;

	@XmlElement(name = "finance")
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
