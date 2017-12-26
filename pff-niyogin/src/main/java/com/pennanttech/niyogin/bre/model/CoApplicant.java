package com.pennanttech.niyogin.bre.model;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

@XmlType(propOrder = { "dob", "salaryToPartnerOrDirector", "maxWorkExperience" })
@XmlRootElement(name = "CoApplicants")
@XmlAccessorType(XmlAccessType.FIELD)
public class CoApplicant {

	@XmlElement(name = "Dob")
	private String	dob;

	@XmlElement(name = "SalaryToPartnerOrDirector")
	private String	salaryToPartnerOrDirector;

	@XmlElement(name = "maxworkexperience")
	private long	maxWorkExperience;

	public String getDob() {
		return dob;
	}

	public void setDob(String dob) {
		this.dob = dob;
	}

	public String getSalaryToPartnerOrDirector() {
		return salaryToPartnerOrDirector;
	}

	public void setSalaryToPartnerOrDirector(String salaryToPartnerOrDirector) {
		this.salaryToPartnerOrDirector = salaryToPartnerOrDirector;
	}

	public long getMaxWorkExperience() {
		return maxWorkExperience;
	}

	public void setMaxWorkExperience(long maxWorkExperience) {
		this.maxWorkExperience = maxWorkExperience;
	}

}
