package com.penanttech.pff.model.external.bre;

import org.codehaus.jackson.annotate.JsonCreator;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.annotate.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class AddressElement {

	//Address Details
	@JsonProperty("ADDRESS_TYPE")
	private String addressType;
	@JsonProperty("STATE")
	private String state;
	@JsonProperty("CITY")
	private String city;
	@JsonProperty("CITY_CATEGORY")
	private String cityCategory;
	@JsonProperty("PINCODE")
	private String pincode;
	@JsonProperty("COUNTRY")
	private String country;
	@JsonProperty("ADDRESS_LINE1")
	private String addressLine1;
	@JsonProperty("ADDRESS_LINE2")
	private String addressLine2;
	@JsonProperty("ADDRESS_LINE3")
	private String addressLine3;
	
	
	@JsonCreator
	public AddressElement() {
		
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
 