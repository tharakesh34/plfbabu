package com.pennanttech.niyogin.model.dedup;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

@XmlType(propOrder = { "statusCode", "message", "applicantType", "firstName", "lastName", "emailId", "pan", "aadhaar",
		"exactMatch", "matchedFields" })
@XmlAccessorType(XmlAccessType.NONE)
public class ExperianDedup implements Serializable {
	private static final long	serialVersionUID	= -1568085405736640269L;

	@XmlElement(name = "applicant_type")
	private String				applicantType;
	@XmlElement(name = "first_name")
	private String				firstName;
	@XmlElement(name = "last_name")
	private String				lastName;
	private String				gender;
	private Date				dob;
	private String				emailId;
	private String				pan;
	private String				aadhaar;
	private String				passport;
	private String				linkedin;
	private String				facebook;
	private String				twitter;
	private String				statusCode;
	private String				message;
	@XmlElement(name = "exact_match")
	private String				exactMatch;
	private Address				address;
	private Phone				phone;
	@XmlElement(name = "matched_fields")
	private List<MatchedField>	matchedFields;

	public ExperianDedup() {
		super();
	}

	public String getApplicantType() {
		return applicantType;
	}

	public void setApplicantType(String applicantType) {
		this.applicantType = applicantType;
	}

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

	public String getGender() {
		return gender;
	}

	public void setGender(String gender) {
		this.gender = gender;
	}

	public Date getDob() {
		return dob;
	}

	public void setDob(Date dob) {
		this.dob = dob;
	}

	public String getEmailId() {
		return emailId;
	}

	public void setEmailId(String emailId) {
		this.emailId = emailId;
	}

	public String getPan() {
		return pan;
	}

	public void setPan(String pan) {
		this.pan = pan;
	}

	public String getAadhaar() {
		return aadhaar;
	}

	public void setAadhaar(String aadhaar) {
		this.aadhaar = aadhaar;
	}

	public String getPassport() {
		return passport;
	}

	public void setPassport(String passport) {
		this.passport = passport;
	}

	public String getLinkedin() {
		return linkedin;
	}

	public void setLinkedin(String linkedin) {
		this.linkedin = linkedin;
	}

	public String getFacebook() {
		return facebook;
	}

	public void setFacebook(String facebook) {
		this.facebook = facebook;
	}

	public String getTwitter() {
		return twitter;
	}

	public void setTwitter(String twitter) {
		this.twitter = twitter;
	}

	public String getStatusCode() {
		return statusCode;
	}

	public void setStatusCode(String statusCode) {
		this.statusCode = statusCode;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public String getExactMatch() {
		return exactMatch;
	}

	public void setExactMatch(String exactMatch) {
		this.exactMatch = exactMatch;
	}

	public Address getAddress() {
		return address;
	}

	public void setAddress(Address address) {
		this.address = address;
	}

	public Phone getPhone() {
		return phone;
	}

	public void setPhone(Phone phone) {
		this.phone = phone;
	}

	public List<MatchedField> getMatchedFields() {
		return matchedFields;
	}

	public void setMatchedFields(List<MatchedField> matchedFields) {
		this.matchedFields = matchedFields;
	}

	@Override
	public String toString() {
		return "ExperianDedup [applicantType=" + applicantType + ", firstName=" + firstName + ", lastName=" + lastName
				+ ", gender=" + gender + ", dob=" + dob + ", emailId=" + emailId + ", pan=" + pan + ", aadhaar="
				+ aadhaar + ", passport=" + passport + ", linkedin=" + linkedin + ", facebook=" + facebook
				+ ", twitter=" + twitter + ", statusCode=" + statusCode + ", message=" + message + ", exactMatch="
				+ exactMatch + ", address=" + address + ", phone=" + phone + ", matchedFields=" + matchedFields + "]";
	}

}
