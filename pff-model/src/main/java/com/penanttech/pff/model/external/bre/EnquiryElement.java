package com.penanttech.pff.model.external.bre;

import java.io.Serializable;

import org.codehaus.jackson.annotate.JsonCreator;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.annotate.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class EnquiryElement implements Serializable{

	private static final long serialVersionUID = 1L;
	@JsonProperty("date_of_enquiry")
	private String dateOfEnquiry;
	@JsonProperty("enquiry_purpose")
	private String enquiryPurpose;
	@JsonProperty("enqiury_amount")
	private String enqiuryAmount;
	@JsonProperty("member_name")
	private String memberName;

	@JsonCreator
	public EnquiryElement() {
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
