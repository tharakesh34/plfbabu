package com.pennanttech.niyogin.criff.model;

import java.io.Serializable;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "bureauconsumer")
@XmlAccessorType(XmlAccessType.FIELD)
public class CriffBureauCommercial implements Serializable {
	private static final long	serialVersionUID	= -8306856974384474724L;

	@XmlElement(name = "APPLICATION_ID")
	private long				applicationId;
	@XmlElement(name = "STG_UNQ_REF_ID")
	private long				StgUnqRefId;
	@XmlElement(name = "company_name")
	private String				companyName;
	@XmlElement(name = "COMPANY_MOBILE")
	private String				companyMobile;
	@XmlElement(name = "COMPANY_PAN")
	private String				companyPAN;
	@XmlElement(name = "LEGAL_ENTITY")
	private String				legalEntity;
	
	@XmlElement(name = "applicant")
	private Applicant			applicant;
	@XmlElement(name = "company_address")
	private CompanyAddress		companyAddress;

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

	public Applicant getApplicant() {
		return applicant;
	}

	public void setApplicant(Applicant applicant) {
		this.applicant = applicant;
	}

	public CompanyAddress getCompanyAddress() {
		return companyAddress;
	}

	public void setCompanyAddress(CompanyAddress companyAddress) {
		this.companyAddress = companyAddress;
	}

	public String getCompanyName() {
		return companyName;
	}

	public void setCompanyName(String companyName) {
		this.companyName = companyName;
	}

	public String getCompanyMobile() {
		return companyMobile;
	}

	public void setCompanyMobile(String companyMobile) {
		this.companyMobile = companyMobile;
	}

	public String getCompanyPAN() {
		return companyPAN;
	}

	public void setCompanyPAN(String companyPAN) {
		this.companyPAN = companyPAN;
	}

	public String getLegalEntity() {
		return legalEntity;
	}

	public void setLegalEntity(String legalEntity) {
		this.legalEntity = legalEntity;
	}
}
