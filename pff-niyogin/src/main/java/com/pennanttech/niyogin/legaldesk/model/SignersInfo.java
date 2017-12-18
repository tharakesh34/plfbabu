package com.pennanttech.niyogin.legaldesk.model;

import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

@XmlType(propOrder = { "leanders", "borrowers" })
@XmlAccessorType(XmlAccessType.FIELD)
public class SignersInfo {

	@XmlElement(name = "leander")
	private List<SignerDetails>	leanders;

	@XmlElement(name = "borrower")
	private List<SignerDetails>	borrowers;

	public List<SignerDetails> getLeanders() {
		return leanders;
	}

	public void setLeanders(List<SignerDetails> leanders) {
		this.leanders = leanders;
	}

	public List<SignerDetails> getBorrowers() {
		return borrowers;
	}

	public void setBorrowers(List<SignerDetails> borrowers) {
		this.borrowers = borrowers;
	}

}