package com.pennanttech.niyogin.criff.model;

import java.io.Serializable;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

@XmlType(propOrder = { "address1", "address2", "address3", "city", "country", "district", "pin", "state" })
@XmlAccessorType(XmlAccessType.FIELD)
public class CompanyAddress implements Serializable {
	private static final long	serialVersionUID	= 2929025907999752246L;

	@XmlElement(name = "ADDRESS_LINE1")
	private String				address1;

	@XmlElement(name = "ADDRESS_LINE2")
	private String				address2;

	@XmlElement(name = "ADDRESS_LINE3")
	private String				address3;

	@XmlElement(name = "ADDRESS1_CITY")
	private String				city;

	@XmlElement(name = "ADDRESS1_COUNTRY")
	private String				country;

	@XmlElement(name = "ADDRESS1_DISTRICT")
	private String				district;

	@XmlElement(name = "ADDRESS1_PIN")
	private String				pin;

	@XmlElement(name = "ADDRESS1_STATE")
	private String				state;

	public String getAddress1() {
		return address1;
	}

	public void setAddress1(String address1) {
		this.address1 = address1;
	}

	public String getAddress2() {
		return address2;
	}

	public void setAddress2(String address2) {
		this.address2 = address2;
	}

	public String getAddress3() {
		return address3;
	}

	public void setAddress3(String address3) {
		this.address3 = address3;
	}

	public String getCity() {
		return city;
	}

	public void setCity(String city) {
		this.city = city;
	}

	public String getCountry() {
		return country;
	}

	public void setCountry(String country) {
		this.country = country;
	}

	public String getDistrict() {
		return district;
	}

	public void setDistrict(String district) {
		this.district = district;
	}

	public String getPin() {
		return pin;
	}

	public void setPin(String pin) {
		this.pin = pin;
	}

	public String getState() {
		return state;
	}

	public void setState(String state) {
		this.state = state;
	}
}
