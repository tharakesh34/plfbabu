package com.pennant.backend.model.dedup.external;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;

@XmlAccessorType(XmlAccessType.NONE)
public class CustAddressDetail {

	@XmlElement
	private String addressType;
	@XmlElement
	private String address;
	@XmlElement
	private String city;
	@XmlElement
	private String pin;
	@XmlElement
	private String landmark;

	public String getAddressType() {
		return addressType;
	}

	public void setAddressType(String addressType) {
		this.addressType = addressType;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
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

	public String getLandmark() {
		return landmark;
	}

	public void setLandmark(String landmark) {
		this.landmark = landmark;
	}

	@Override
	public String toString() {
		return "CustAddressDetail [addressType=" + addressType + ", address=" + address + ", city=" + city + ", pin="
				+ pin + ", landmark=" + landmark + "]";
	}
}
