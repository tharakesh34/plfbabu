package com.pennanttech.niyogin.dedup.model;

import java.io.Serializable;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;

@XmlAccessorType(XmlAccessType.NONE)
public class Phone implements Serializable {
	private static final long	serialVersionUID	= -1626565083533225611L;

	@XmlElement(name = "phone_number")
	private String				phoneNumber;
	@XmlElement(name = "phone_type")
	private String				phoneType;

	public Phone() {
		super();
	}

	public String getPhoneNumber() {
		return phoneNumber;
	}

	public void setPhoneNumber(String phoneNumber) {
		this.phoneNumber = phoneNumber;
	}

	public String getPhoneType() {
		return phoneType;
	}

	public void setPhoneType(String phoneType) {
		this.phoneType = phoneType;
	}

	@Override
	public String toString() {
		return "Phone [phoneNumber=" + phoneNumber + ", phoneType=" + phoneType + "]";
	}

}
