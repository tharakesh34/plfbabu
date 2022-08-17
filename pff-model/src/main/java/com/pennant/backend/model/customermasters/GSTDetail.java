package com.pennant.backend.model.customermasters;

import java.util.HashSet;
import java.util.Set;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

import com.pennanttech.pennapps.core.model.AbstractWorkflowEntity;
import com.pennanttech.pennapps.core.model.LoggedInUser;

@XmlType(propOrder = { "gstNumber", "gstAddress", "gstAddress1", "gstAddress2", "gstAddress3", "gstAddress4", "gstCity",
		"gstState", "gstCountry", "gstPinCode", "gstDefault" })
@XmlAccessorType(XmlAccessType.NONE)
public class GSTDetail extends AbstractWorkflowEntity {
	private static final long serialVersionUID = -2825043112819872369L;

	@XmlElement
	private long id = Long.MIN_VALUE;
	private long custID;
	@XmlElement
	private String gstNumber;
	private String address;
	private String addressLine1;
	private String addressLine2;
	private String addressLine3;
	private String addressLine4;
	private String cityCode;
	private String stateCode;
	private String countryCode;
	private Long pinCodeId;
	private String pinCode;
	private boolean tin;
	private boolean tinName;
	private boolean tinAddress;
	private boolean defaultGST;
	private String custShrtName;
	private String custCIF;
	private String cityName;
	private String stateName;
	private String countryName;
	private String pinCodeName;
	private String sourceId;
	private GSTDetail befImage;
	private LoggedInUser userDetails;

	public GSTDetail() {
		super();
	}

	public Set<String> getExcludeFields() {
		Set<String> excludeFields = new HashSet<>();

		excludeFields.add("custCIF");
		excludeFields.add("custShrtName");
		excludeFields.add("cityName");
		excludeFields.add("stateName");
		excludeFields.add("countryName");
		excludeFields.add("pinCodeName");
		excludeFields.add("sourceId");

		return excludeFields;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public long getCustID() {
		return custID;
	}

	public void setCustID(long custID) {
		this.custID = custID;
	}

	public String getGstNumber() {
		return gstNumber;
	}

	public void setGstNumber(String gstNumber) {
		this.gstNumber = gstNumber;
	}

	public String getAddress() {
		return address;
	}

	@XmlElement(name = "gstAddress")
	public void setAddress(String address) {
		this.address = address;
	}

	public String getAddressLine1() {
		return addressLine1;
	}

	@XmlElement(name = "gstAddress1")
	public void setAddressLine1(String addressLine1) {
		this.addressLine1 = addressLine1;
	}

	public String getAddressLine2() {
		return addressLine2;
	}

	@XmlElement(name = "gstAddress2")
	public void setAddressLine2(String addressLine2) {
		this.addressLine2 = addressLine2;
	}

	public String getAddressLine3() {
		return addressLine3;
	}

	@XmlElement(name = "gstAddress3")
	public void setAddressLine3(String addressLine3) {
		this.addressLine3 = addressLine3;
	}

	public String getAddressLine4() {
		return addressLine4;
	}

	@XmlElement(name = "gstAddress4")
	public void setAddressLine4(String addressLine4) {
		this.addressLine4 = addressLine4;
	}

	public String getCityCode() {
		return cityCode;
	}

	@XmlElement(name = "gstCity")
	public void setCityCode(String cityCode) {
		this.cityCode = cityCode;
	}

	public String getStateCode() {
		return stateCode;
	}

	@XmlElement(name = "gstState")
	public void setStateCode(String stateCode) {
		this.stateCode = stateCode;
	}

	public String getCountryCode() {
		return countryCode;
	}

	@XmlElement(name = "gstCountry")
	public void setCountryCode(String countryCode) {
		this.countryCode = countryCode;
	}

	public Long getPinCodeId() {
		return pinCodeId;
	}

	public void setPinCodeId(Long pinCodeId) {
		this.pinCodeId = pinCodeId;
	}

	public String getPinCode() {
		return pinCode;
	}

	@XmlElement(name = "gstPinCode")
	public void setPinCode(String pinCode) {
		this.pinCode = pinCode;
	}

	public boolean isTin() {
		return tin;
	}

	public void setTin(boolean tin) {
		this.tin = tin;
	}

	public boolean isTinName() {
		return tinName;
	}

	public void setTinName(boolean tinName) {
		this.tinName = tinName;
	}

	public boolean isTinAddress() {
		return tinAddress;
	}

	public void setTinAddress(boolean tinAddress) {
		this.tinAddress = tinAddress;
	}

	public boolean isDefaultGST() {
		return defaultGST;
	}

	@XmlElement(name = "gstDefault")
	public void setDefaultGST(boolean defaultGST) {
		this.defaultGST = defaultGST;
	}

	public String getCustShrtName() {
		return custShrtName;
	}

	public void setCustShrtName(String custShrtName) {
		this.custShrtName = custShrtName;
	}

	public String getCustCIF() {
		return custCIF;
	}

	public void setCustCIF(String custCIF) {
		this.custCIF = custCIF;
	}

	public String getCityName() {
		return cityName;
	}

	public void setCityName(String cityName) {
		this.cityName = cityName;
	}

	public String getStateName() {
		return stateName;
	}

	public void setStateName(String stateName) {
		this.stateName = stateName;
	}

	public String getCountryName() {
		return countryName;
	}

	public void setCountryName(String countryName) {
		this.countryName = countryName;
	}

	public String getPinCodeName() {
		return pinCodeName;
	}

	public void setPinCodeName(String pinCodeName) {
		this.pinCodeName = pinCodeName;
	}

	public String getSourceId() {
		return sourceId;
	}

	public void setSourceId(String sourceId) {
		this.sourceId = sourceId;
	}

	public GSTDetail getBefImage() {
		return befImage;
	}

	public void setBefImage(GSTDetail befImage) {
		this.befImage = befImage;
	}

	public LoggedInUser getUserDetails() {
		return userDetails;
	}

	public void setUserDetails(LoggedInUser userDetails) {
		this.userDetails = userDetails;
	}

}