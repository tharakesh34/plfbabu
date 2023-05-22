package com.penanttech.pff.model.external.bre;

import javax.xml.bind.annotation.XmlElement;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class AddressElement {

	// Address Details
	@XmlElement(name = "ADDRESS_TYPE")
	private String addressType;
	@XmlElement(name = "STATE")
	private String state;
	@XmlElement(name = "CITY")
	private String city;
	@XmlElement(name = "CITY_CATEGORY")
	private String cityCategory;
	@XmlElement(name = "PINCODE")
	private String pincode;
	@XmlElement(name = "COUNTRY")
	private String country;
	@XmlElement(name = "ADDRESS_LINE1")
	private String addressLine1;
	@XmlElement(name = "ADDRESS_LINE2")
	private String addressLine2;
	@XmlElement(name = "ADDRESS_LINE3")
	private String addressLine3;

	@JsonCreator
	public AddressElement() {
	    super();
	}

	public String getAddressType() {
		return addressType;
	}

	public void setAddressType(String addressType) {
		this.addressType = addressType;
	}

	public String getState() {
		return state;
	}

	public void setState(String state) {
		this.state = state;
	}

	public String getCity() {
		return city;
	}

	public void setCity(String city) {
		this.city = city;
	}

	public String getCityCategory() {
		return cityCategory;
	}

	public void setCityCategory(String cityCategory) {
		this.cityCategory = cityCategory;
	}

	public String getPincode() {
		return pincode;
	}

	public void setPincode(String pincode) {
		this.pincode = pincode;
	}

	public String getCountry() {
		return country;
	}

	public void setCountry(String country) {
		this.country = country;
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

}
