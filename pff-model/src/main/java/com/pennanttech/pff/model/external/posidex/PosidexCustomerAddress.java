package com.pennanttech.pff.model.external.posidex;

import java.io.Serializable;

public class PosidexCustomerAddress extends Posidex implements Serializable {
	static final long serialVersionUID = 1L;

	private String addresstype;
	private String address1;
	private String address2;
	private String address3;
	private String state;
	private String city;
	private String pin;
	private String landline1;
	private String landline2;
	private String mobile;
	private String area;
	private String landmark;
	private String std;
	private String email;

	public PosidexCustomerAddress() {
	    super();
	}

	public String getAddresstype() {
		return addresstype;
	}

	public void setAddresstype(String addresstype) {
		this.addresstype = addresstype;
	}

	public String getAddress1() {
		return address1;
	}

	public void setAddress1(String address1) {
		this.address1 = address1;
	}

	public String getAddress2() {
		return address2;
	}

	public void setAddress2(String address2) {
		this.address2 = address2;
	}

	public String getAddress3() {
		return address3;
	}

	public void setAddress3(String address3) {
		this.address3 = address3;
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

	public String getPin() {
		return pin;
	}

	public void setPin(String pin) {
		this.pin = pin;
	}

	public String getLandline1() {
		return landline1;
	}

	public void setLandline1(String landline1) {
		this.landline1 = landline1;
	}

	public String getLandline2() {
		return landline2;
	}

	public void setLandline2(String landline2) {
		this.landline2 = landline2;
	}

	public String getMobile() {
		return mobile;
	}

	public void setMobile(String mobile) {
		this.mobile = mobile;
	}

	public String getArea() {
		return area;
	}

	public void setArea(String area) {
		this.area = area;
	}

	public String getLandmark() {
		return landmark;
	}

	public void setLandmark(String landmark) {
		this.landmark = landmark;
	}

	public String getStd() {
		return std;
	}

	public void setStd(String std) {
		this.std = std;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

}
