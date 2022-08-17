package com.pennanttech.pff.cd.model;

import java.sql.Timestamp;
import java.util.HashSet;
import java.util.Set;

import com.pennanttech.pennapps.core.model.AbstractWorkflowEntity;
import com.pennanttech.pennapps.core.model.LoggedInUser;

public class Manufacturer extends AbstractWorkflowEntity {
	private static final long serialVersionUID = 1L;

	private String name;
	private String oemId;
	private String description;
	private String channel;
	private boolean active;
	private String addressLine1;
	private String addressLine2;
	private String addressLine3;
	private String city;
	private String state;
	private String country;
	private String pinCode;
	private String pinAreaDesc;
	private Long pinCodeId;
	private String manufacPAN;
	private String gstInNumber;
	private String manfMobileNo;
	private String manfEmailId;
	private String manfacContactName;
	private String LovDescCityName;
	private String LovDescStateName;
	private String LovDescCountryName;
	private Manufacturer befImage;
	private LoggedInUser userDetails;
	private long manufacturerId = Long.MIN_VALUE;
	private String lovValue;
	private boolean newRecord = false;

	public Set<String> getExcludeFields() {
		Set<String> excludeFields = new HashSet<String>();

		excludeFields.add("pinCode");
		excludeFields.add("pinAreaDesc");
		excludeFields.add("LovDescCityName");
		excludeFields.add("LovDescStateName");
		excludeFields.add("LovDescCountryName");
		excludeFields.add("oemId");

		return excludeFields;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public boolean isActive() {
		return active;
	}

	public void setActive(boolean active) {
		this.active = active;
	}

	public Manufacturer getBefImage() {
		return befImage;
	}

	public void setBefImage(Manufacturer befImage) {
		this.befImage = befImage;
	}

	public LoggedInUser getUserDetails() {
		return userDetails;
	}

	public void setUserDetails(LoggedInUser userDetails) {
		this.userDetails = userDetails;
	}

	public long getManufacturerId() {
		return manufacturerId;
	}

	public void setManufacturerId(long manufacturerId) {
		this.manufacturerId = manufacturerId;
	}

	public String getLovValue() {
		return lovValue;
	}

	public void setLovValue(String lovValue) {
		this.lovValue = lovValue;
	}

	public boolean isNewRecord() {
		return newRecord;
	}

	public void setNewRecord(boolean newRecord) {
		this.newRecord = newRecord;
	}

	public boolean isNew() {
		return isNewRecord();
	}

	public String getChannel() {
		return channel;
	}

	public void setChannel(String channel) {
		this.channel = channel;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Timestamp getPrevMntOn() {
		return befImage == null ? null : befImage.getLastMntOn();
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

	public String getPinAreaDesc() {
		return pinAreaDesc;
	}

	public void setPinAreaDesc(String pinAreaDesc) {
		this.pinAreaDesc = pinAreaDesc;
	}

	public String getPinCode() {
		return pinCode;
	}

	public void setPinCode(String pinCode) {
		this.pinCode = pinCode;
	}

	public Long getPinCodeId() {
		return pinCodeId;
	}

	public void setPinCodeId(Long pinCodeId) {
		this.pinCodeId = pinCodeId;
	}

	public String getLovDescCityName() {
		return LovDescCityName;
	}

	public void setLovDescCityName(String lovDescCityName) {
		LovDescCityName = lovDescCityName;
	}

	public String getLovDescStateName() {
		return LovDescStateName;
	}

	public void setLovDescStateName(String lovDescStateName) {
		LovDescStateName = lovDescStateName;
	}

	public String getLovDescCountryName() {
		return LovDescCountryName;
	}

	public void setLovDescCountryName(String lovDescCountryName) {
		LovDescCountryName = lovDescCountryName;
	}

	public String getManufacPAN() {
		return manufacPAN;
	}

	public void setManufacPAN(String manufacPAN) {
		this.manufacPAN = manufacPAN;
	}

	public String getGstInNumber() {
		return gstInNumber;
	}

	public void setGstInNumber(String gstInNumber) {
		this.gstInNumber = gstInNumber;
	}

	public String getManfMobileNo() {
		return manfMobileNo;
	}

	public void setManfMobileNo(String manfMobileNo) {
		this.manfMobileNo = manfMobileNo;
	}

	public String getManfEmailId() {
		return manfEmailId;
	}

	public void setManfEmailId(String manfEmailId) {
		this.manfEmailId = manfEmailId;
	}

	public String getManfacContactName() {
		return manfacContactName;
	}

	public void setManfacContactName(String manfacContactName) {
		this.manfacContactName = manfacContactName;
	}

	public String getOemId() {
		return oemId;
	}

	public void setOemId(String oemId) {
		this.oemId = oemId;
	}

}
