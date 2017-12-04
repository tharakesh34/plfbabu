package com.pennanttech.niyogin.cibil.consumer.model;

import java.io.Serializable;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

@XmlType(propOrder = { "houseNo", "societyName", "landmark", "careOf", "category", "city", "country", "district", "pin", "state" })
@XmlRootElement(name = "address")
@XmlAccessorType(XmlAccessType.FIELD)
public class CibilConsumerAddress implements Serializable{

	private static final long serialVersionUID = -7988120811703719720L;

	@XmlElement(name = "ADDRESS1_HOUSE_NO")
	private String	houseNo;

	@XmlElement(name = "ADDRESS1_SOCIETY_NAME")
	private String	societyName;

	@XmlElement(name = "ADDRESS1_LANDMARK")
	private String	landmark;

	@XmlElement(name = "ADDRESS1_CARE_OF")
	private String	careOf;

	@XmlElement(name = "ADDRESS1_CATEGORY")
	private String	category;

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


	public String getHouseNo() {
		return houseNo;
	}

	public void setHouseNo(String houseNo) {
		this.houseNo = houseNo;
	}

	public String getSocietyName() {
		return societyName;
	}

	public void setSocietyName(String societyName) {
		this.societyName = societyName;
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

	public String getCategory() {
		return category;
	}

	public void setCategory(String category) {
		this.category = category;
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

	public String getState() {
		return state;
	}

	public void setState(String state) {
		this.state = state;
	}

	public String getPin() {
		return pin;
	}

	public void setPin(String pin) {
		this.pin = pin;
	}

	@Override
	public String toString() {
		return "CibilConsumerAddress [houseNo=" + houseNo + ", societyName=" + societyName + ", landmark=" + landmark
				+ ", careOf=" + careOf + ", category=" + category + ", city=" + city + ", country=" + country
				+ ", district=" + district + ", state=" + state + ", pin=" + pin + "]";
	}

}
