package com.pennanttech.niyogin.dedup.model;

import java.io.Serializable;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;

@XmlAccessorType(XmlAccessType.NONE)
public class Address implements Serializable {
	private static final long	serialVersionUID	= 8647313455611629682L;

	@XmlElement(name = "address_line1")
	private String				addressLine1;
	@XmlElement(name = "address_line2")
	private String				addressLine2;
	@XmlElement(name = "address_line3")
	private String				addressLine3;
	private String				landmark;
	private String				city;
	private String				pin;
	private String				state;
	private String				country;
	@XmlElement(name = "address_type")
	private String				addressType;

	public Address() {
		super();
	}

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

	public String getLandmark() {
		return landmark;
	}

	public void setLandmark(String landmark) {
		this.landmark = landmark;
	}

	public String getCity() {
		return city;
	}

	public void setCity(String city) {
		this.city = city;
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

	public String getCountry() {
		return country;
	}

	public void setCountry(String country) {
		this.country = country;
	}

	public String getAddressType() {
		return addressType;
	}

	public void setAddressType(String addressType) {
		this.addressType = addressType;
	}

	@Override
	public String toString() {
		return "Address [addressLine1=" + addressLine1 + ", addressLine2=" + addressLine2 + ", addressLine3="
				+ addressLine3 + ", landmark=" + landmark + ", city=" + city + ", pin=" + pin + ", state=" + state
				+ ", country=" + country + ", addressType=" + addressType + "]";
	}
}
