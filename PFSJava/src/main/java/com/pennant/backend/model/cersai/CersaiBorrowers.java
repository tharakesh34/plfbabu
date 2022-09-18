package com.pennant.backend.model.cersai;

import java.io.Serializable;
import java.util.Date;

public class CersaiBorrowers implements Serializable {

	private static final long serialVersionUID = 1L;

	private long batchId;
	private long custId;
	private String custCif;
	private String rowType;
	private long serialNumber;
	private String borrowerType;
	private boolean assetOwner = false;
	private long borrowerUidType;
	private String borrowerUidValue;
	private String borrowerPAN;
	private long borrowerCKYC;
	private String borrowerName;
	private Date borrowerRegDate;
	private String borrowerRegNumber;
	private String individualPan;
	private long individualCKYC;
	private String gender;
	private String individualName;
	private String fatherMotherName;
	private Date dob;
	private long mobileNo;
	private String email;
	private String addressLine1;
	private String addressLine2;
	private String addressLine3;
	private String city;
	private String district;
	private String state;
	private long pincode;
	private String country;
	private String custCtgCode;

	public long getBatchId() {
		return batchId;
	}

	public void setBatchId(long batchId) {
		this.batchId = batchId;
	}

	public long getCustId() {
		return custId;
	}

	public void setCustId(long custId) {
		this.custId = custId;
	}

	public String getCustCif() {
		return custCif;
	}

	public void setCustCif(String custCif) {
		this.custCif = custCif;
	}

	public String getRowType() {
		return rowType;
	}

	public void setRowType(String rowType) {
		this.rowType = rowType;
	}

	public long getSerialNumber() {
		return serialNumber;
	}

	public void setSerialNumber(long serialNumber) {
		this.serialNumber = serialNumber;
	}

	public String getBorrowerType() {
		return borrowerType;
	}

	public void setBorrowerType(String borrowerType) {
		this.borrowerType = borrowerType;
	}

	public boolean isAssetOwner() {
		return assetOwner;
	}

	public void setAssetOwner(boolean assetOwner) {
		this.assetOwner = assetOwner;
	}

	public long getBorrowerUidType() {
		return borrowerUidType;
	}

	public void setBorrowerUidType(long borrowerUidType) {
		this.borrowerUidType = borrowerUidType;
	}

	public String getBorrowerUidValue() {
		return borrowerUidValue;
	}

	public void setBorrowerUidValue(String borrowerUidValue) {
		this.borrowerUidValue = borrowerUidValue;
	}

	public String getBorrowerPAN() {
		return borrowerPAN;
	}

	public void setBorrowerPAN(String borrowerPAN) {
		this.borrowerPAN = borrowerPAN;
	}

	public long getBorrowerCKYC() {
		return borrowerCKYC;
	}

	public void setBorrowerCKYC(long borrowerCKYC) {
		this.borrowerCKYC = borrowerCKYC;
	}

	public String getBorrowerName() {
		return borrowerName;
	}

	public void setBorrowerName(String borrowerName) {
		this.borrowerName = borrowerName;
	}

	public Date getBorrowerRegDate() {
		return borrowerRegDate;
	}

	public void setBorrowerRegDate(Date borrowerRegDate) {
		this.borrowerRegDate = borrowerRegDate;
	}

	public String getBorrowerRegNumber() {
		return borrowerRegNumber;
	}

	public void setBorrowerRegNumber(String borrowerRegNumber) {
		this.borrowerRegNumber = borrowerRegNumber;
	}

	public String getIndividualPan() {
		return individualPan;
	}

	public void setIndividualPan(String individualPan) {
		this.individualPan = individualPan;
	}

	public long getIndividualCKYC() {
		return individualCKYC;
	}

	public void setIndividualCKYC(long individualCKYC) {
		this.individualCKYC = individualCKYC;
	}

	public String getGender() {
		return gender;
	}

	public void setGender(String gender) {
		this.gender = gender;
	}

	public String getIndividualName() {
		return individualName;
	}

	public void setIndividualName(String individualName) {
		this.individualName = individualName;
	}

	public String getFatherMotherName() {
		return fatherMotherName;
	}

	public void setFatherMotherName(String fatherMotherName) {
		this.fatherMotherName = fatherMotherName;
	}

	public Date getDob() {
		return dob;
	}

	public void setDob(Date dob) {
		this.dob = dob;
	}

	public long getMobileNo() {
		return mobileNo;
	}

	public void setMobileNo(long mobileNo) {
		this.mobileNo = mobileNo;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
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

	public String getCity() {
		return city;
	}

	public void setCity(String city) {
		this.city = city;
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

	public long getPincode() {
		return pincode;
	}

	public void setPincode(long pincode) {
		this.pincode = pincode;
	}

	public String getCountry() {
		return country;
	}

	public void setCountry(String country) {
		this.country = country;
	}

	public String getCustCtgCode() {
		return custCtgCode;
	}

	public void setCustCtgCode(String custCtgCode) {
		this.custCtgCode = custCtgCode;
	}

}
