package com.pennanttech.niyogin.criff.model;

import java.io.Serializable;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

@XmlType(propOrder = { "firstName", "lastName", "dob", "gender", "pan", "mobile", "maritalStatus", "personalAddress" })
@XmlRootElement(name = "applicant")
@XmlAccessorType(XmlAccessType.FIELD)
public class Applicant implements Serializable {
	private static final long	serialVersionUID	= -1685092053724752809L;

	@XmlElement(name = "FIRST_NAME")
	private String				firstName;
	@XmlElement(name = "LAST_NAME")
	private String				lastName;
	@XmlElement(name = "DOB")
	private String				dob;
	@XmlElement(name = "GENDER")
	private String				gender;
	@XmlElement(name = "MOBILE")
	private String				mobile;
	@XmlElement(name = "PAN")
	private String				pan;
	@XmlElement(name = "MARITAL_STATUS")
	private String				maritalStatus;
	@XmlElement(name = "UID_")
	private String				uid;
	@XmlElement(name = "address")
	private PersonalAddress		personalAddress;

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

	public String getMobile() {
		return mobile;
	}

	public void setMobile(String mobile) {
		this.mobile = mobile;
	}

	public String getPan() {
		return pan;
	}

	public void setPan(String pan) {
		this.pan = pan;
	}

	public String getMaritalStatus() {
		return maritalStatus;
	}

	public void setMaritalStatus(String maritalStatus) {
		this.maritalStatus = maritalStatus;
	}

	public PersonalAddress getPersonalAddress() {
		return personalAddress;
	}

	public void setPersonalAddress(PersonalAddress personalAddress) {
		this.personalAddress = personalAddress;
	}

	public String getUid() {
		return uid;
	}

	public void setUid(String uid) {
		this.uid = uid;
	}
}
