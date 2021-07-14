package com.pennanttech.pff.model;

import java.io.Serializable;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;

import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;

@JsonIgnoreProperties(ignoreUnknown = true)
@XmlAccessorType(XmlAccessType.FIELD)
public class PMAYDetailsRespData implements Serializable {

	private static final long serialVersionUID = 1L;
	@JsonProperty("MESSAGE")
	public String MESSAGE;
	@JsonProperty("STATUS")
	public String STATUS;
	@JsonProperty("RECORD_ID")
	public String RECORD_ID;
	public String APPLICATION_ID;
	public String BENEFICIARY_CODE;
	public String REMARKS;
	public String COMPONENT;
	public String AADHAAR_NO;

	public String getMESSAGE() {
		return MESSAGE;
	}

	public void setMESSAGE(String mESSAGE) {
		MESSAGE = mESSAGE;
	}

	public String getSTATUS() {
		return STATUS;
	}

	public void setSTATUS(String sTATUS) {
		STATUS = sTATUS;
	}

	public String getRECORD_ID() {
		return RECORD_ID;
	}

	public void setRECORD_ID(String rECORD_ID) {
		RECORD_ID = rECORD_ID;
	}

	public String getAPPLICATION_ID() {
		return APPLICATION_ID;
	}

	public void setAPPLICATION_ID(String aPPLICATION_ID) {
		APPLICATION_ID = aPPLICATION_ID;
	}

	public String getBENEFICIARY_CODE() {
		return BENEFICIARY_CODE;
	}

	public void setBENEFICIARY_CODE(String bENEFICIARY_CODE) {
		BENEFICIARY_CODE = bENEFICIARY_CODE;
	}

	public String getREMARKS() {
		return REMARKS;
	}

	public void setREMARKS(String rEMARKS) {
		REMARKS = rEMARKS;
	}

	public String getCOMPONENT() {
		return COMPONENT;
	}

	public void setCOMPONENT(String cOMPONENT) {
		COMPONENT = cOMPONENT;
	}

	public String getAADHAAR_NO() {
		return AADHAAR_NO;
	}

	public void setAADHAAR_NO(String aADHAAR_NO) {
		AADHAAR_NO = aADHAAR_NO;
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	}

}