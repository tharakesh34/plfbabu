package com.pennanttech.niyogin.bre.model;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

@XmlType(propOrder = { "demogs", "financials", "business", "perfios" })
@XmlRootElement(name = "APPLICANT")
@XmlAccessorType(XmlAccessType.FIELD)
public class Applicant {

	@XmlElement(name = "DEMOGS")
	private DeMogs		demogs;

	@XmlElement(name = "FINANCIALS")
	private Financials	financials;

	@XmlElement(name = "BUSINESS")
	private Business	business;

	@XmlElement(name = "PERFIOS")
	private Perfios		perfios;

	public DeMogs getDemogs() {
		return demogs;
	}

	public void setDemogs(DeMogs demogs) {
		this.demogs = demogs;
	}

	public Financials getFinancials() {
		return financials;
	}

	public void setFinancials(Financials financials) {
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
