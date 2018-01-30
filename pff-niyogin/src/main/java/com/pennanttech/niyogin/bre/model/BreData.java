package com.pennanttech.niyogin.bre.model;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

@XmlType(propOrder = { "cif", "application", "applicant", "coApplicant" })
@XmlRootElement(name = "BRE")
@XmlAccessorType(XmlAccessType.FIELD)
public class BreData {

	private String		cif;

	@XmlElement(name = "APPLICATION")
	private Application	application;

	@XmlElement(name = "APPLICANT")
	private Applicant	applicant;

	@XmlElement(name = "COAPPLICANT")
	private CoApplicant	coApplicant;

	public String getCif() {
		return cif;
	}

	public void setCif(String cif) {
		this.cif = cif;
	}

	public Application getApplication() {
		return application;
	}

	public void setApplication(Application application) {
		this.application = application;
	}

	public Applicant getApplicant() {
		return applicant;
	}

	public void setApplicant(Applicant applicant) {
		this.applicant = applicant;
	}

	public CoApplicant getCoApplicant() {
		return coApplicant;
	}

	public void setCoApplicant(CoApplicant coApplicant) {
		this.coApplicant = coApplicant;
	}

}
