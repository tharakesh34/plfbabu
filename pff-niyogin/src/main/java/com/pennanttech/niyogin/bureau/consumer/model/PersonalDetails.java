package com.pennanttech.niyogin.bureau.consumer.model;

import java.io.Serializable;
import java.util.Date;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

@XmlType(propOrder = { "firstName", "lastName", "dob", "gender", "mobile", "pan", "uid_" })
@XmlRootElement(name = "personal")
@XmlAccessorType(XmlAccessType.FIELD)
public class PersonalDetails implements Serializable {

	private static final long serialVersionUID = -6544421138043452874L;
	
	@XmlElement(name = "FIRST_NAME")
	private String	firstName;
 
	@XmlElement(name = "LAST_NAME")
	private String	lastName;
	
	@XmlElement(name = "DOB")
	private Date	dob;
	
	@XmlElement(name = "GENDER")
	private String	gender;
	
	@XmlElement(name = "MOBILE")
	private String	mobile;
	
	@XmlElement(name = "PAN")
	private String	pan;
	
	@XmlElement(name = "UID_")
	private String	uid_;

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

	public String getPan() {
		return pan;
	}

	public void setPan(String pan) {
		this.pan = pan;
	}

	public String getUid_() {
		return uid_;
	}

	public void setUid_(String uid_) {
		this.uid_ = uid_;
	}

	@Override
	public String toString() {
		return "Personal [firstName=" + firstName + ", lastName=" + lastName + ", dob=" + dob + ", gender=" + gender
				+ ", mobile=" + mobile + ", pan=" + pan + ", uid_=" + uid_ + "]";
	}

}
