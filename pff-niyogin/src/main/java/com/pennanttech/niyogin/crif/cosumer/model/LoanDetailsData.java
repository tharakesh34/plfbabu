package com.pennanttech.niyogin.crif.cosumer.model;

import java.io.Serializable;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

@XmlType(propOrder = { "loanDetailsResponse" })
@XmlAccessorType(XmlAccessType.FIELD)
public class LoanDetailsData implements Serializable {

	private static final long	serialVersionUID	= 7174788511506344048L;

	@XmlElement(name = "LOAN-DETAILS")
	private LoanDetail			loanDetail;

	public LoanDetail getLoanDetail() {
		return loanDetail;
	}

	public void setLoanDetail(LoanDetail loanDetail) {
		this.loanDetail = loanDetail;
	}

	@Override
	public String toString() {
		return "LoanDetailsData [loanDetail=" + loanDetail + "]";
	}

}
