package com.pennant.coreinterface.vo;

import org.apache.commons.lang.StringUtils;

public class CorebankingAddres {

	private String CustomerMnemonic;
	private String addressType;
	private String accountNumber;
	private String accountBranch = "";
	private String accountBasic = "";
	private String accountSuffix = "";
	private String houseNo;
	private String flatNo;
	private String street;
	private String addressLine1;
	private String addressLine2;
	private String addressLine3;
	private String addressLine4;
	private String poBox;
	private String country;
	private String city;
	private String zipCode;
	private String phoneNo;

	public CorebankingAddres() {
		super();
	}

	public String getCustomerMnemonic() {
		return CustomerMnemonic;
	}

	public void setCustomerMnemonic(String customerMnemonic) {
		CustomerMnemonic = customerMnemonic;
	}

	public String getAddressType() {
		return addressType;
	}

	public void setAddressType(String addressType) {
		this.addressType = addressType;
	}

	public String getAccountNumber() {
		return accountNumber;
	}

	public void setAccountNumber(String accountNumber) {
		this.accountNumber = StringUtils.trimToEmpty(accountNumber);
		if (!this.accountNumber.equals("") && this.accountNumber.length() == 13) {
			this.accountBranch = this.accountNumber.substring(0, 4);
			this.accountBasic = this.accountNumber.substring(5, 10);
			this.accountSuffix = this.accountNumber.substring(11);
		}
	}

	public String getAccountBranch() {
		return accountBranch;
	}

	public void setAccountBranch(String accountBranch) {
		this.accountBranch = accountBranch;
	}

	public String getAccountBasic() {
		return accountBasic;
	}

	public void setAccountBasic(String accountBasic) {
		this.accountBasic = accountBasic;
	}

	public String getAccountSuffix() {
		return accountSuffix;
	}

	public void setAccountSuffix(String accountSuffix) {
		this.accountSuffix = accountSuffix;
	}

	public String getHouseNo() {
		return houseNo;
	}

	public void setHouseNo(String houseNo) {
		this.houseNo = houseNo;
	}

	public String getFlatNo() {
		return flatNo;
	}

	public void setFlatNo(String flatNo) {
		this.flatNo = flatNo;
	}

	public String getStreet() {
		return street;
	}

	public void setStreet(String street) {
		this.street = street;
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

	public String getAddressLine4() {
		return addressLine4;
	}

	public void setAddressLine4(String addressLine4) {
		this.addressLine4 = addressLine4;
	}

	public String getPoBox() {
		return poBox;
	}

	public void setPoBox(String poBox) {
		this.poBox = poBox;
	}

	public String getCountry() {
		return country;
	}

	public void setCountry(String country) {
		this.country = country;
	}

	public String getCity() {
		return city;
	}

	public void setCity(String city) {
		this.city = city;
	}

	public String getZipCode() {
		return zipCode;
	}

	public void setZipCode(String zipCode) {
		this.zipCode = zipCode;
	}

	public String getPhoneNo() {
		return phoneNo;
	}

	public void setPhoneNo(String phoneNo) {
		this.phoneNo = phoneNo;
	}

}
