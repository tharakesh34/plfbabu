package com.pennanttech.niyogin.bre.model;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

@XmlType(propOrder = { "details", "financials", "business", "perfios" })
@XmlRootElement(name = "Applicant")
@XmlAccessorType(XmlAccessType.FIELD)
public class Applicant {

	@XmlElement(name = "Details")
	private ApplicantDetails	details;

	@XmlElement(name = "Financials")
	private ApplicantFinancials	financials;

	@XmlElement(name = "Business")
	private Business			business;

	@XmlElement(name = "Perfios")
	private Perfios				perfios;

	public ApplicantDetails getDetails() {
		return details;
	}

	public void setDetails(ApplicantDetails details) {
		this.details = details;
	}

	public ApplicantFinancials getFinancials() {
		return financials;
	}

	public void setFinancials(ApplicantFinancials financials) {
		this.financials = financials;
	}

	public Business getBusiness() {
		return business;
	}

	public void setBusiness(Business business) {
		this.business = business;
	}

	public Perfios getPerfios() {
		return perfios;
	}

	public void setPerfios(Perfios perfios) {
		this.perfios = perfios;
	}

}
