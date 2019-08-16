package com.pennanttech.ws.model.finance;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;


@XmlAccessorType(XmlAccessType.NONE)
public class LoanStatusDetails {
	@XmlElement(name = "finance")
	private List<LoanStatus> loanSatusDetails=new ArrayList<>();;

	public List<LoanStatus> getLoanSatusDetails() {
		return loanSatusDetails;
	}

	public void setLoanSatusDetails(List<LoanStatus> loanSatusDetails) {
		this.loanSatusDetails = loanSatusDetails;
	}
	

}
