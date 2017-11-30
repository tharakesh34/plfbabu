package com.pennanttech.niyogin.experian.commercial.model;

import java.io.Serializable;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

@XmlType(propOrder = { "addressLine1", "addressLine2", "addressLine3", "city", "country", "district", "pin", "state" })
@XmlRootElement(name = "companyaddress")
@XmlAccessorType(XmlAccessType.FIELD)
public class CompanyAddress implements Serializable {

	private static final long serialVersionUID = -4978755658518155497L;

	@XmlElement(name = "ADDRESS_LINE1")
	private String	addressLine1;

	@XmlElement(name = "ADDRESS_LINE2")
	private String	addressLine2;

	@XmlElement(name = "ADDRESS_LINE3")
	private String	addressLine3;

	@XmlElement(name = "ADDRESS1_CITY")
	private String	city;

	@XmlElement(name = "ADDRESS1_COUNTRY")
	private String	country;

	@XmlElement(name = "ADDRESS1_DISTRICT")
	private String	district;

	@XmlElement(name = "ADDRESS1_PIN")
	private String	pin;

	@XmlElement(name = "ADDRESS1_STATE")
	private String	state;

	public String getAddressLine1() {
		return addressLine1;
	}

	public void setAddressLine1(String addressLine1) {
		this.addressLine1 = addressLine1;
	}

	public String getAddressLine2() {
		return addressLine2;
	}

	public void setAddressLine2(String addressLine2) {
		this.addressLine2 = addressLine2;
	}

	public String getAddressLine3() {
		return addressLine3;
	}

	public void setAddressLine3(String addressLine3) {
		this.addressLine3 = addressLine3;
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

	@Override
	public String toString() {
		return "CompanyAddress [addressLine1=" + addressLine1 + ", addressLine2=" + addressLine2 + ", addressLine3="
				+ addressLine3 + ", city=" + city + ", country=" + country + ", district=" + district + ", pin=" + pin
				+ ", state=" + state + "]";
	}

}
