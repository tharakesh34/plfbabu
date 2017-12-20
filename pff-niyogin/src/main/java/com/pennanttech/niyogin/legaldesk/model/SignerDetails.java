package com.pennanttech.niyogin.legaldesk.model;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

@XmlType(propOrder = { "name", "seqNumbOfSign", "email" })
@XmlRootElement(name = "signers_info")
@XmlAccessorType(XmlAccessType.NONE)
public class SignerDetails {

	@XmlElement(name = "name")
	private String	name;

	@XmlElement(name = "sequence_of_signature")
	private int		seqNumbOfSign;

	@XmlElement(name = "email")
	private String	email;

	private long	custID;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getSeqNumbOfSign() {
		return seqNumbOfSign;
	}

	public void setSeqNumbOfSign(int seqNumbOfSign) {
		this.seqNumbOfSign = seqNumbOfSign;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public long getCustID() {
		return custID;
	}

	public void setCustID(long custID) {
		this.custID = custID;
	}

}
