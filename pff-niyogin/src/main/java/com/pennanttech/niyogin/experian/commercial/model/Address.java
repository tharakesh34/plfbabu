package com.pennanttech.niyogin.experian.commercial.model;

import java.io.Serializable;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

@XmlType(propOrder = { "houseNo", "landmark", "city", "country", "pin", "state" })
@XmlRootElement(name = "address")
@XmlAccessorType(XmlAccessType.FIELD)
public class Address implements Serializable {
	
	private static final long serialVersionUID = 3703871006940742948L;

	@XmlElement(name = "ADDRESS1_HOUSE_NO")
	private String	houseNo;

	@XmlElement(name = "ADDRESS1_LANDMARK")
	private String	landmark;

	@XmlElement(name = "ADDRESS1_CITY")
	private String	city;

	@XmlElement(name = "ADDRESS1_COUNTRY")
	private String	country;

	@XmlElement(name = "ADDRESS1_PIN")
	private String	pin;

	@XmlElement(name = "ADDRESS1_STATE")
	private String	state;

	public String getHouseNo() {
		return houseNo;
	}

	public void setHouseNo(String houseNo) {
		this.houseNo = houseNo;
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

	public String getState() {
		return state;
	}

	public void setState(String state) {
		this.state = state;
	}

	@Override
	public String toString() {
		return "Address [houseNo=" + houseNo + ", landmark=" + landmark + ", city=" + city + ", country=" + country
				+ ", pin=" + pin + ", state=" + state + "]";
	}

}
