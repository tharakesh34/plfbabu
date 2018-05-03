package com.pff.vo;

public class CustomerContactDetail {

	private String  phone;
	private String  countryCode;
	private String  areaCode;
	private String  subsidiaryNumber;
	public String getCountryCode() {
		
		return countryCode;
	}
	public void setCountryCode(String countryCode) {
		this.countryCode = countryCode;
	}
	public String getAreaCode() {
		return areaCode;
	}
	public void setAreaCode(String areaCode) {
		
		this.areaCode = areaCode;
	}
	public String getSubsidiaryNumber() {
		return subsidiaryNumber;
	}
	public void setSubsidiaryNumber(String subsidiaryNumber) {
		this.subsidiaryNumber = subsidiaryNumber;
	}
	public String getPhone() {
		return phone;
	}
	public void setPhone(String phone) {
		this.phone = phone;
	}

}
