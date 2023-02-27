package com.pennanttech.pff.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class PmayDetails {
	private String RECORD_ID;

	private String APPLICATION_NO;

	private String TOWN_CODE;

	private String BORROWER_TYPE;

	private String NAME;

	private String AADHAAR_NO;

	private String GENDER;

	private String MOBILE_NO;

	private String ALTERNATE_NO;

	private String EMAIL_ID;

	private String ALTERNATE_ID;

	public void setRECORD_ID(String RECORD_ID) {
		this.RECORD_ID = RECORD_ID;
	}

	public String getRECORD_ID() {
		return this.RECORD_ID;
	}

	public void setAPPLICATION_NO(String APPLICATION_NO) {
		this.APPLICATION_NO = APPLICATION_NO;
	}

	public String getAPPLICATION_NO() {
		return this.APPLICATION_NO;
	}

	public void setTOWN_CODE(String TOWN_CODE) {
		this.TOWN_CODE = TOWN_CODE;
	}

	public String getTOWN_CODE() {
		return this.TOWN_CODE;
	}

	public void setBORROWER_TYPE(String BORROWER_TYPE) {
		this.BORROWER_TYPE = BORROWER_TYPE;
	}

	public String getBORROWER_TYPE() {
		return this.BORROWER_TYPE;
	}

	public void setNAME(String NAME) {
		this.NAME = NAME;
	}

	public String getNAME() {
		return this.NAME;
	}

	public void setAADHAAR_NO(String AADHAAR_NO) {
		this.AADHAAR_NO = AADHAAR_NO;
	}

	public String getAADHAAR_NO() {
		return this.AADHAAR_NO;
	}

	public void setGENDER(String GENDER) {
		this.GENDER = GENDER;
	}

	public String getGENDER() {
		return this.GENDER;
	}

	public void setMOBILE_NO(String MOBILE_NO) {
		this.MOBILE_NO = MOBILE_NO;
	}

	public String getMOBILE_NO() {
		return this.MOBILE_NO;
	}

	public void setALTERNATE_NO(String ALTERNATE_NO) {
		this.ALTERNATE_NO = ALTERNATE_NO;
	}

	public String getALTERNATE_NO() {
		return this.ALTERNATE_NO;
	}

	public void setEMAIL_ID(String EMAIL_ID) {
		this.EMAIL_ID = EMAIL_ID;
	}

	public String getEMAIL_ID() {
		return this.EMAIL_ID;
	}

	public void setALTERNATE_ID(String ALTERNATE_ID) {
		this.ALTERNATE_ID = ALTERNATE_ID;
	}

	public String getALTERNATE_ID() {
		return this.ALTERNATE_ID;
	}
}
