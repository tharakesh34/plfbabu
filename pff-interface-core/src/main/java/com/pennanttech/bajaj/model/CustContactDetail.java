package com.pennanttech.bajaj.model;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;

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
		return "CustContactDetail [phoneType=" + phoneType + ", stdCode="
				+ stdCode + ", phoneNumber=" + phoneNumber + "]";
	}
	
	
}
