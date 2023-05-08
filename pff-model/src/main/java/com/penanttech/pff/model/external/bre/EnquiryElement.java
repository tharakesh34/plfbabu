package com.penanttech.pff.model.external.bre;

import java.io.Serializable;

import javax.xml.bind.annotation.XmlElement;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class EnquiryElement implements Serializable {

	private static final long serialVersionUID = 1L;
	@XmlElement(name = "date_of_enquiry")
	private String dateOfEnquiry;
	@XmlElement(name = "enquiry_purpose")
	private String enquiryPurpose;
	@XmlElement(name = "enqiury_amount")
	private String enqiuryAmount;
	@XmlElement(name = "member_name")
	private String memberName;

	@JsonCreator
	public EnquiryElement() {
	    super();
	}

	public String getDateOfEnquiry() {
		return dateOfEnquiry;
	}

	public void setDateOfEnquiry(String dateOfEnquiry) {
		this.dateOfEnquiry = dateOfEnquiry;
	}

	public String getEnquiryPurpose() {
		return enquiryPurpose;
	}

	public void setEnquiryPurpose(String enquiryPurpose) {
		this.enquiryPurpose = enquiryPurpose;
	}

	public String getEnqiuryAmount() {
		return enqiuryAmount;
	}

	public void setEnqiuryAmount(String enqiuryAmount) {
		this.enqiuryAmount = enqiuryAmount;
	}

	public String getMemberName() {
		return memberName;
	}

	public void setMemberName(String memberName) {
		this.memberName = memberName;
	}

}
