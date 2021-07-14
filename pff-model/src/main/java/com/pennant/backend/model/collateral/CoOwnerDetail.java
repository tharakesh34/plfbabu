package com.pennant.backend.model.collateral;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.pennant.backend.model.Entity;
import com.pennanttech.pennapps.core.model.AbstractWorkflowEntity;
import com.pennanttech.pennapps.core.model.LoggedInUser;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlType;

@XmlType(propOrder = { "bankCustomer", "coOwnerIDType", "coOwnerId", "coOwnerPercentage", "coOwnerCIF",
		"coOwnerCIFName", "mobileNo", "emailId", "coOwnerIDNumber", "addrHNbr", "flatNbr", "addrStreet", "addrLine1",
		"addrLine2", "POBox", "addrCountry", "addrProvince", "addrCity", "addrZIP", "remarks" })
@XmlAccessorType(XmlAccessType.NONE)
public class CoOwnerDetail extends AbstractWorkflowEntity implements Entity {
	private static final long serialVersionUID = 1L;

	private long customerId;

	@JsonProperty("coOwnerID")
	private int coOwnerId;
	private String collateralRef;

	@XmlElement
	private boolean bankCustomer;

	@JsonProperty("cif")
	private String coOwnerCIF;

	@JsonProperty("idType")
	private String coOwnerIDType;

	@JsonProperty("idNumber")
	private String coOwnerIDNumber;

	@JsonProperty("name")
	private String coOwnerCIFName;

	@JsonProperty("ownershipPerc")
	private BigDecimal coOwnerPercentage;
	@JsonProperty("phoneNumber")
	private String mobileNo;

	@JsonProperty("email")
	private String emailId;

	private byte[] coOwnerProof = new byte[Byte.MAX_VALUE];

	@XmlElement
	private String remarks;
	// Address Details

	@JsonProperty("houseNo")
	private String addrHNbr;

	@JsonProperty("flatNo")
	private String flatNbr;

	@JsonProperty("streetName")
	private String addrStreet;

	@XmlElement
	private String addrLine1;

	@XmlElement
	private String addrLine2;

	@XmlElement
	private String POBox;

	@JsonProperty("country")
	private String addrCountry;

	@JsonProperty("province")
	private String addrProvince;

	@JsonProperty("city")
	private String addrCity;

	@JsonProperty("zip")
	private String addrZIP;
	private boolean newRecord = false;
	private CoOwnerDetail befImage;
	private LoggedInUser userDetails;

	private String coOwnerIDTypeName;
	private String coOwnerProofName;
	private String lovDescAddrCountryName;
	private String lovDescAddrProvinceName;
	private String lovDescAddrCityName;

	// API validation purpose only
	@SuppressWarnings("unused")
	private CoOwnerDetail validateCoOwner = this;

	public CoOwnerDetail() {
		super();
	}

	public Set<String> getExcludeFields() {
		Set<String> excludeFields = new HashSet<String>();
		excludeFields.add("coOwnerIDTypeName");
		excludeFields.add("coOwnerProofName");
		excludeFields.add("lovDescAddrCountryName");
		excludeFields.add("lovDescAddrProvinceName");
		excludeFields.add("lovDescAddrCityName");
		excludeFields.add("coOwnerCIF");
		excludeFields.add("validateCoOwner");
		return excludeFields;
	}

	@Override
	public boolean isNew() {
		return isNewRecord();
	}

	public String getCollateralRef() {
		return collateralRef;
	}

	public void setCollateralRef(String collateralRef) {
		this.collateralRef = collateralRef;
	}

	public boolean isBankCustomer() {
		return bankCustomer;
	}

	public void setBankCustomer(boolean bankCustomer) {
		this.bankCustomer = bankCustomer;
	}

	public String getCoOwnerCIF() {
		return coOwnerCIF;
	}

	public void setCoOwnerCIF(String coOwnerCIF) {
		this.coOwnerCIF = coOwnerCIF;
	}

	public String getCoOwnerIDType() {
		return coOwnerIDType;
	}

	public void setCoOwnerIDType(String coOwnerIDType) {
		this.coOwnerIDType = coOwnerIDType;
	}

	public String getCoOwnerIDNumber() {
		return coOwnerIDNumber;
	}

	public void setCoOwnerIDNumber(String coOwnerIDNumber) {
		this.coOwnerIDNumber = coOwnerIDNumber;
	}

	public String getCoOwnerCIFName() {
		return coOwnerCIFName;
	}

	public void setCoOwnerCIFName(String coOwnerCIFName) {
		this.coOwnerCIFName = coOwnerCIFName;
	}

	public BigDecimal getCoOwnerPercentage() {
		return coOwnerPercentage;
	}

	public void setCoOwnerPercentage(BigDecimal coOwnerPercentage) {
		this.coOwnerPercentage = coOwnerPercentage;
	}

	public String getMobileNo() {
		return mobileNo;
	}

	public void setMobileNo(String mobileNo) {
		this.mobileNo = mobileNo;
	}

	public String getEmailId() {
		return emailId;
	}

	public void setEmailId(String emailId) {
		this.emailId = emailId;
	}

	public byte[] getCoOwnerProof() {
		return coOwnerProof;
	}

	public void setCoOwnerProof(byte[] coOwnerProof) {
		this.coOwnerProof = coOwnerProof;
	}

	public String getRemarks() {
		return remarks;
	}

	public void setRemarks(String remarks) {
		this.remarks = remarks;
	}

	public String getAddrHNbr() {
		return addrHNbr;
	}

	public void setAddrHNbr(String addrHNbr) {
		this.addrHNbr = addrHNbr;
	}

	public String getFlatNbr() {
		return flatNbr;
	}

	public void setFlatNbr(String flatNbr) {
		this.flatNbr = flatNbr;
	}

	public String getAddrStreet() {
		return addrStreet;
	}

	public void setAddrStreet(String addrStreet) {
		this.addrStreet = addrStreet;
	}

	public String getAddrLine1() {
		return addrLine1;
	}

	public void setAddrLine1(String addrLine1) {
		this.addrLine1 = addrLine1;
	}

	public String getAddrLine2() {
		return addrLine2;
	}

	public void setAddrLine2(String addrLine2) {
		this.addrLine2 = addrLine2;
	}

	public String getPOBox() {
		return POBox;
	}

	public void setPOBox(String pOBox) {
		POBox = pOBox;
	}

	public String getAddrCountry() {
		return addrCountry;
	}

	public void setAddrCountry(String addrCountry) {
		this.addrCountry = addrCountry;
	}

	public String getAddrProvince() {
		return addrProvince;
	}

	public void setAddrProvince(String addrProvince) {
		this.addrProvince = addrProvince;
	}

	public String getAddrCity() {
		return addrCity;
	}

	public void setAddrCity(String addrCity) {
		this.addrCity = addrCity;
	}

	public String getAddrZIP() {
		return addrZIP;
	}

	public void setAddrZIP(String addrZIP) {
		this.addrZIP = addrZIP;
	}

	public boolean isNewRecord() {
		return newRecord;
	}

	public void setNewRecord(boolean newRecord) {
		this.newRecord = newRecord;
	}

	public CoOwnerDetail getBefImage() {
		return befImage;
	}

	public void setBefImage(CoOwnerDetail befImage) {
		this.befImage = befImage;
	}

	public LoggedInUser getUserDetails() {
		return userDetails;
	}

	public void setUserDetails(LoggedInUser userDetails) {
		this.userDetails = userDetails;
	}

	public String getCoOwnerIDTypeName() {
		return coOwnerIDTypeName;
	}

	public void setCoOwnerIDTypeName(String coOwnerIDTypeName) {
		this.coOwnerIDTypeName = coOwnerIDTypeName;
	}

	public String getCoOwnerProofName() {
		return coOwnerProofName;
	}

	public void setCoOwnerProofName(String coOwnerProofName) {
		this.coOwnerProofName = coOwnerProofName;
	}

	public String getLovDescAddrCountryName() {
		return lovDescAddrCountryName;
	}

	public void setLovDescAddrCountryName(String lovDescAddrCountryName) {
		this.lovDescAddrCountryName = lovDescAddrCountryName;
	}

	public String getLovDescAddrProvinceName() {
		return lovDescAddrProvinceName;
	}

	public void setLovDescAddrProvinceName(String lovDescAddrProvinceName) {
		this.lovDescAddrProvinceName = lovDescAddrProvinceName;
	}

	public String getLovDescAddrCityName() {
		return lovDescAddrCityName;
	}

	public void setLovDescAddrCityName(String lovDescAddrCityName) {
		this.lovDescAddrCityName = lovDescAddrCityName;
	}

	@Override
	public long getId() {
		return coOwnerId;
	}

	@Override
	public void setId(long id) {
		// TODO Auto-generated method stub
	}

	public long getCustomerId() {
		return customerId;
	}

	public void setCustomerId(long customerId) {
		this.customerId = customerId;
	}

	public int getCoOwnerId() {
		return coOwnerId;
	}

	public void setCoOwnerId(int coOwnerId) {
		this.coOwnerId = coOwnerId;
	}

}
