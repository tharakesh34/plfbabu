package com.pennanttech.niyogin.legaldesk.model;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

@XmlType(propOrder = { "name", "email", "seqNumbOfSign" })
@XmlRootElement(name = "co_borrower")
@XmlAccessorType(XmlAccessType.NONE)
public class CoBorrower {

	@XmlElement(name = "name")
	private String	name;

	@XmlElement(name = "email")
	private String	email;

	@XmlElement(name = "sequence_of_signature")
	private int		seqNumbOfSign;

	private long	custID;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public int getSeqNumbOfSign() {
		return seqNumbOfSign;
	}

	public void setSeqNumbOfSign(int seqNumbOfSign) {
		this.seqNumbOfSign = seqNumbOfSign;
	}

	public long getCustID() {
		return custID;
	}

	public void setCustID(long custID) {
		this.custID = custID;
	}

}
