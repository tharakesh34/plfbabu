package com.pennanttech.niyogin.legaldesk.model;

import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

@XmlType(propOrder = { "lenders", "borrowers" })
@XmlAccessorType(XmlAccessType.FIELD)
public class SignersInfo {

	@XmlElement(name = "leander")
	private List<SignerDetails>	lenders;

	@XmlElement(name = "borrower")
	private List<SignerDetails>	borrowers;

	public List<SignerDetails> getLenders() {
		return lenders;
	}

	public void setLenders(List<SignerDetails> lenders) {
		this.lenders = lenders;
	}

	public List<SignerDetails> getBorrowers() {
		return borrowers;
	}

	public void setBorrowers(List<SignerDetails> borrowers) {
		this.borrowers = borrowers;
	}
}