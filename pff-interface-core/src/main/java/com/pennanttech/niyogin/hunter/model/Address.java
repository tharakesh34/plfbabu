package com.pennanttech.niyogin.hunter.model;

import java.io.Serializable;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
@XmlType(propOrder = { "address", "city", "state", "country", "pin" })
@XmlRootElement(name = "address")
@XmlAccessorType(XmlAccessType.FIELD)
public class Address implements Serializable {

	private static final long serialVersionUID = 6131492561646131754L;

	private String				address;
	private String				city;
	private String				state;
	private String				country;
	private String				pin;

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

	public String getPin() {
		return pin;
	}

	public void setPin(String pin) {
		this.pin = pin;
	}

}
