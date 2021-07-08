package com.pennant.backend.model.dedup.external;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;

@XmlAccessorType(XmlAccessType.NONE)
public class CustContactDetail {

	@XmlElement
	private String phoneType;
	@XmlElement
	private String stdCode;
	@XmlElement
	private String phoneNumber;

	public String getPhoneType() {
		return phoneType;
	}

	public void setPhoneType(String phoneType) {
		this.phoneType = phoneType;
	}

	public String getStdCode() {
		return stdCode;
	}

	public void setStdCode(String stdCode) {
		this.stdCode = stdCode;
	}

	public String getPhoneNumber() {
		return phoneNumber;
	}

	public void setPhoneNumber(String phoneNumber) {
		this.phoneNumber = phoneNumber;
	}

	@Override
	public String toString() {
		return "CustContactDetail [phoneType=" + phoneType + ", stdCode=" + stdCode + ", phoneNumber=" + phoneNumber
				+ "]";
	}

}
