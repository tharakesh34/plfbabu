package com.pennant.backend.model.applicant;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import com.pennant.backend.model.WSReturnStatus;

@XmlRootElement(name = "applicantDetails")
@XmlAccessorType(XmlAccessType.NONE)
public class ApplicantDetails {

	@XmlElement
	private String finReference;
	@XmlElement
	private String applicantType;
	@XmlElement
	private String fullName;
	@XmlElement
	private String relation;
	@XmlElement
	private Long custID = (long) 0;
	@XmlElement
	private WSReturnStatus returnStatus;

	public ApplicantDetails() {
		super();
	}

	public String getFinReference() {
		return finReference;
	}

	public void setFinReference(String finReference) {
		this.finReference = finReference;
	}

	public String getApplicantType() {
		return applicantType;
	}

	public void setApplicantType(String applicantType) {
		this.applicantType = applicantType;
	}

	public String getFullName() {
		return fullName;
	}

	public void setFullName(String fullName) {
		this.fullName = fullName;
	}

	public String getRelation() {
		return relation;
	}

	public void setRelation(String relation) {
		this.relation = relation;
	}

	public Long getCustID() {
		return custID;
	}

	public void setCustID(Long custID) {
		this.custID = custID;
	}

	public WSReturnStatus getReturnStatus() {
		return returnStatus;
	}

	public void setReturnStatus(WSReturnStatus returnStatus) {
		this.returnStatus = returnStatus;
	}
}
