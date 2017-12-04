package com.pennanttech.niyogin.cibil.consumer.model;

import java.io.Serializable;
import java.util.Date;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

@XmlType(propOrder = { "firstName", "middleName", "lastName", "dob", "gender", "mobile", "nomineeName",
		"nomineeRelation", "pan", "uid" })
@XmlRootElement(name = "personal")
@XmlAccessorType(XmlAccessType.FIELD)
public class CibilPersonalDetails implements Serializable {

	private static final long	serialVersionUID	= -5242331822367560368L;

	@XmlElement(name = "FIRST_NAME")
	private String				firstName;

	@XmlElement(name = "MIDDLE_NAME")
	private String				middleName;

	@XmlElement(name = "LAST_NAME")
	private String				lastName;

	@XmlElement(name = "DOB")
	private Date				dob;

	@XmlElement(name = "GENDER")
	private String				gender;

	@XmlElement(name = "MOBILE")
	private String				mobile;

	@XmlElement(name = "NOMINEE_NM")
	private String				nomineeName;

	@XmlElement(name = "NOMINEE_REL")
	private String				nomineeRelation;

	@XmlElement(name = "PAN")
	private String				pan;

	@XmlElement(name = "UID_")
	private String				uid;

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getMiddleName() {
		return middleName;
	}

	public void setMiddleName(String middleName) {
		this.middleName = middleName;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	public Date getDob() {
		return dob;
	}

	public void setDob(Date dob) {
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

	public String getNomineeName() {
		return nomineeName;
	}

	public void setNomineeName(String nomineeName) {
		this.nomineeName = nomineeName;
	}

	public String getNomineeRelation() {
		return nomineeRelation;
	}

	public void setNomineeRelation(String nomineeRelation) {
		this.nomineeRelation = nomineeRelation;
	}

	public String getPan() {
		return pan;
	}

	public void setPan(String pan) {
		this.pan = pan;
	}

	public String getUid() {
		return uid;
	}

	public void setUid(String uid) {
		this.uid = uid;
	}

	@Override
	public String toString() {
		return "CibilPersonalDetails [firstName=" + firstName + ", middleName=" + middleName + ", lastName=" + lastName
				+ ", dob=" + dob + ", gender=" + gender + ", mobile=" + mobile + ", nomineeName=" + nomineeName
				+ ", nomineeRelation=" + nomineeRelation + ", pan=" + pan + ", uid=" + uid + "]";
	}

}
