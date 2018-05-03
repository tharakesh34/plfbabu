package com.pennanttech.niyogin.bre.model;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

@XmlType(propOrder = { "codeMogs", "coAppBureau", "coAppBusiness" })
@XmlRootElement(name = "element")
@XmlAccessorType(XmlAccessType.FIELD)
public class CoAppElement {

	@XmlElement(name = "CODEMOGS")
	private CodeMogs		codeMogs;

	@XmlElement(name = "COBUREAU")
	private CoAppBureau		coAppBureau;

	@XmlElement(name = "COBUSINESS")
	private CoAppBusiness	coAppBusiness;

	public CodeMogs getCodeMogs() {
		return codeMogs;
	}

	public void setCodeMogs(CodeMogs codeMogs) {
		this.codeMogs = codeMogs;
	}

	public CoAppBureau getCoAppBureau() {
		return coAppBureau;
	}

	public void setCoAppBureau(CoAppBureau coAppBureau) {
		this.coAppBureau = coAppBureau;
	}

	public CoAppBusiness getCoAppBusiness() {
		return coAppBusiness;
	}

	public void setCoAppBusiness(CoAppBusiness coAppBusiness) {
		this.coAppBusiness = coAppBusiness;
	}

}
