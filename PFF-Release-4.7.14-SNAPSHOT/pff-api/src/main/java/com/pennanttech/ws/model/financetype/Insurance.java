package com.pennanttech.ws.model.financetype;

import java.io.Serializable;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;

@XmlType(propOrder = { "takafulReq", "insTypeDesc", "takafulMandatory", "takafulProvider", "insuranceProvider" })
@XmlAccessorType(XmlAccessType.FIELD)
public class Insurance implements Serializable {

	private static final long serialVersionUID = -2273747187615277778L;

	public Insurance() {
		
	}
	
	private boolean takafulReq;
	private String insTypeDesc;
	private boolean takafulMandatory;
	private String takafulProvider;

	private InsuranceProvider insuranceProvider;

	public boolean isTakafulReq() {
		return takafulReq;
	}

	public void setTakafulReq(boolean takafulReq) {
		this.takafulReq = takafulReq;
	}

	public String getInsTypeDesc() {
		return insTypeDesc;
	}

	public void setInsTypeDesc(String insTypeDesc) {
		this.insTypeDesc = insTypeDesc;
	}

	public boolean isTakafulMandatory() {
		return takafulMandatory;
	}

	public void setTakafulMandatory(boolean takafulMandatory) {
		this.takafulMandatory = takafulMandatory;
	}

	public String getTakafulProvider() {
		return takafulProvider;
	}

	public void setTakafulProvider(String takafulProvider) {
		this.takafulProvider = takafulProvider;
	}

	public InsuranceProvider getInsuranceProvider() {
		return insuranceProvider;
	}

	public void setInsuranceProvider(InsuranceProvider insuranceProvider) {
		this.insuranceProvider = insuranceProvider;
	}
}
