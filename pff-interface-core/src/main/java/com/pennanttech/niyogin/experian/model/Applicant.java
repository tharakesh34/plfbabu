package com.pennanttech.niyogin.experian.model;

import java.io.Serializable;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

@XmlType(propOrder = { "firstName", "lastName", "dob", "gender", "pan", "mobile", "maritalStatus", "address" })
@XmlRootElement(name = "applicant")
@XmlAccessorType(XmlAccessType.FIELD)
public class Applicant implements Serializable {

	private static final long serialVersionUID = 8019839188681477564L;

	@XmlElement(name = "FIRST_NAME")
	private String	firstName;

	@XmlElement(name = "LAST_NAME")
	private String	lastName;

	@XmlElement(name = "DOB")
	private String	dob;

	@XmlElement(name = "GENDER")
	private String	gender;

	@XmlElement(name = "PAN")
	private String	pan;

	@XmlElement(name = "MOBILE")
	private String	mobile;

	@XmlElement(name = "MARITAL_STATUS")
	private String	maritalStatus;

	private Address	address;

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	public String getDob() {
		return dob;
	}

	public void setDob(String dob) {
		this.dob = dob;
	}

	public String getGender() {
		return gender;
	}

	public void setGender(String gender) {
		this.gender = gender;
	}

	public String getPan() {
		return pan;
	}

	public void setPan(String pan) {
		this.pan = pan;
	}

	public String getMobile() {
		return mobile;
	}

	public void setMobile(String mobile) {
		this.mobile = mobile;
	}

	public String getMaritalStatus() {
		return maritalStatus;
	}

	public void setMaritalStatus(String maritalStatus) {
		this.maritalStatus = maritalStatus;
	}

	public Address getAddress() {
		return address;
	}

	public void setAddress(Address address) {
		this.address = address;
	}

	@Override
	public String toString() {
		return "Applicant [firstName=" + firstName + ", lastName=" + lastName + ", dob=" + dob + ", gender=" + gender
				+ ", pan=" + pan + ", mobile=" + mobile + ", maritalStatus=" + maritalStatus + ", address=" + address
				+ "]";
	}

}
