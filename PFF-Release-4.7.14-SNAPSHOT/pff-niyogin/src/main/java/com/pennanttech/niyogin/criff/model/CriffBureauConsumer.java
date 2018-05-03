package com.pennanttech.niyogin.criff.model;

import java.io.Serializable;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

@XmlType(propOrder = { "applicationId", "StgUnqRefId", "custCIF", "finReference", "address", "personal" })
@XmlRootElement(name = "bureauconsumer")
@XmlAccessorType(XmlAccessType.FIELD)
public class CriffBureauConsumer implements Serializable {

	private static final long	serialVersionUID	= -8306856974384474724L;
	@XmlElement(name = "APPLICATION_ID")
	private long				applicationId;
	@XmlElement(name = "STG_UNQ_REF_ID")
	private long				StgUnqRefId;
	@XmlElement(name = "CIF")
	private String				custCIF;
	private String				finReference;
	@XmlElement(name = "address")
	private PersonalAddress		address;

	@XmlElement(name = "personal")
	private Applicant			applicant;

	public long getApplicationId() {
		return applicationId;
	}

	public void setApplicationId(long applicationId) {
		this.applicationId = applicationId;
	}

	public long getStgUnqRefId() {
		return StgUnqRefId;
	}

	public void setStgUnqRefId(long stgUnqRefId) {
		StgUnqRefId = stgUnqRefId;
	}

	public PersonalAddress getAddress() {
		return address;
	}

	public void setAddress(PersonalAddress address) {
		this.address = address;
	}

	public Applicant getApplicant() {
		return applicant;
	}

	public void setApplicant(Applicant applicant) {
		this.applicant = applicant;
	}

	public String getCustCIF() {
		return custCIF;
	}

	public void setCustCIF(String custCIF) {
		this.custCIF = custCIF;
	}

	public String getFinReference() {
		return finReference;
	}

	public void setFinReference(String finReference) {
		this.finReference = finReference;
	}

}
