package com.pennanttech.niyogin.hunter.model;

import java.io.Serializable;
import java.math.BigDecimal;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

@XmlType(propOrder = { "name", "address", "loanAmount", "phone", "emailId" })
@XmlRootElement(name = "org")
@XmlAccessorType(XmlAccessType.FIELD)
public class CustomerBasicDetail implements Serializable {

	private static final long	serialVersionUID	= 1407465347792589188L;
	
	private String name;
	private Address address;
	@XmlElement(name = "loan_amount")
	private BigDecimal loanAmount;
	private long phone;
	@XmlElement(name = "email_id")
	private String emailId;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public BigDecimal getLoanAmount() {
		return loanAmount;
	}

	public void setLoanAmount(BigDecimal loanAmount) {
		this.loanAmount = loanAmount;
	}

	public long getPhone() {
		return phone;
	}

	public void setPhone(long phone) {
		this.phone = phone;
	}

	public String getEmailId() {
		return emailId;
	}

	public void setEmailId(String emailId) {
		this.emailId = emailId;
	}

	public Address getAddress() {
		return address;
	}

	public void setAddress(Address address) {
		this.address = address;
	}

	@Override
	public String toString() {
		return "CustomerBasicDetail [name=" + name + ", loanAmount=" + loanAmount + ", phone=" + phone + ", emailId=" + emailId
				+ ", address=" + address + "]";
	}

}
