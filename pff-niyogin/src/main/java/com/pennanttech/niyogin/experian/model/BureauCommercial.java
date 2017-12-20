package com.pennanttech.niyogin.experian.model;

import java.io.Serializable;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

@XmlType(propOrder = { "stgUnqRefId", "applicationId", "applicant", "companyName", "companyAddress", "companyMobile",
		"companyPan", "legalEntity" })
@XmlRootElement(name = "bureaucommercial")
@XmlAccessorType(XmlAccessType.FIELD)
public class BureauCommercial implements Serializable {

	private static final long serialVersionUID = -1729491218914314327L;

	@XmlElement(name = "STG_UNQ_REF_ID")
	private long	stgUnqRefId;

	@XmlElement(name = "APPLICATION_ID")
	private long	applicationId;

	private Applicant applicant;

	@XmlElement(name = "company_name")
	private String	companyName;

	@XmlElement(name = "company_address")
	private CompanyAddress	companyAddress;

	@XmlElement(name = "COMPANY_MOBILE")
	private String	companyMobile;

	@XmlElement(name = "COMPANY_PAN")
	private String	companyPan;

	@XmlElement(name = "LEGAL_ENTITY")
	private String	legalEntity;

	
	public long getStgUnqRefId() {
		return stgUnqRefId;
	}

	public void setStgUnqRefId(long stgUnqRefId) {
		this.stgUnqRefId = stgUnqRefId;
	}

	public long getApplicationId() {
		return applicationId;
	}

	public void setApplicationId(long applicationId) {
		this.applicationId = applicationId;
	}

	public Applicant getApplicant() {
		return applicant;
	}

	public void setApplicant(Applicant applicant) {
		this.applicant = applicant;
	}

	public String getCompanyName() {
		return companyName;
	}

	public void setCompanyName(String companyName) {
		this.companyName = companyName;
	}

	public CompanyAddress getCompanyAddress() {
		return companyAddress;
	}

	public void setCompanyAddress(CompanyAddress companyAddress) {
		this.companyAddress = companyAddress;
	}

	public String getCompanyMobile() {
		return companyMobile;
	}

	public void setCompanyMobile(String companyMobile) {
		this.companyMobile = companyMobile;
	}

	public String getCompanyPan() {
		return companyPan;
	}

	public void setCompanyPan(String companyPan) {
		this.companyPan = companyPan;
	}

	public String getLegalEntity() {
		return legalEntity;
	}

	public void setLegalEntity(String legalEntity) {
		this.legalEntity = legalEntity;
	}

	@Override
	public String toString() {
		return "BureauCommercial [stgUnqRefId=" + stgUnqRefId + ", applicationId=" + applicationId + ", applicant="
				+ applicant + ", companyName=" + companyName + ", companyAddress=" + companyAddress + ", companyMobile="
				+ companyMobile + ", companyPan=" + companyPan + ", legalEntity=" + legalEntity + "]";
	}

}
