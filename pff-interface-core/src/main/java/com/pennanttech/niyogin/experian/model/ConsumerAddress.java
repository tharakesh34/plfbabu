package com.pennanttech.niyogin.experian.model;

import java.io.Serializable;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

@XmlType(propOrder = { "houseNo", "landmark", "careOf", "city", "country", "district", "pin", "state", "subDistrict" })
@XmlRootElement(name = "address")
@XmlAccessorType(XmlAccessType.FIELD)
public class ConsumerAddress implements Serializable {

	private static final long serialVersionUID = 2929025907999752246L;

	@XmlElement(name = "ADDRESS1_HOUSE_NO")
	private String houseNo;
	
	@XmlElement(name = "ADDRESS1_LANDMARK")
    private String landmark;
	
	@XmlElement(name = "ADDRESS1_CARE_OF")
    private String careOf;
	
	@XmlElement(name = "ADDRESS1_CITY")
    private String city;
	
	@XmlElement(name = "ADDRESS1_COUNTRY")
    private String country;
	
	@XmlElement(name = "ADDRESS1_DISTRICT")
    private String district;
	
	@XmlElement(name = "ADDRESS1_PIN")
    private String pin;
	
	@XmlElement(name = "ADDRESS1_STATE")
    private String state;
	
	@XmlElement(name = "ADDRESS1_SUB_DISTRICT")
    private String subDistrict;

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

	public String getCareOf() {
		return careOf;
	}

	public void setCareOf(String careOf) {
		this.careOf = careOf;
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

	public String getSubDistrict() {
		return subDistrict;
	}

	public void setSubDistrict(String subDistrict) {
		this.subDistrict = subDistrict;
	}

	@Override
	public String toString() {
		return "Address [houseNo=" + houseNo + ", landmark=" + landmark + ", careOf=" + careOf + ", city=" + city
				+ ", country=" + country + ", district=" + district + ", pin=" + pin + ", state=" + state
				+ ", subDistrict=" + subDistrict + "]";
	}

}
