package com.pennant.backend.model.bulkaddressupload;

import com.pennant.pff.upload.model.UploadDetails;
import com.pennanttech.pennapps.core.model.LoggedInUser;

public class CustomerKycDetail extends UploadDetails {
	private static final long serialVersionUID = 1L;

	private Long finID;
	private String finReference;
	private String custAddrType;
	private int custAddrPriority;
	private String custAddrLine3;
	private String custAddrHNbr;
	private String custFlatNbr;
	private String custAddrStreet;
	private String custAddrLine1;
	private String custAddrLine2;
	private String custAddrCity;
	private String custAddrLine4;
	private String custDistrict;
	private String custAddrProvince;
	private String custAddrCountry;
	private String custAddrZIP;
	private LoggedInUser userDetails;
	private Long pinCodeId;
	private String phoneTypeCode;
	private int phoneTypePriority;
	private String phoneNumber;
	private String custEMailTypeCode;
	private int custEMailPriority;
	private String custEMail;
	private String source;

	public CustomerKycDetail() {
		super();
	}

	public Long getFinID() {
		return finID;
	}

	public void setFinID(Long finID) {
		this.finID = finID;
	}

	public String getFinReference() {
		return finReference;
	}

	public void setFinReference(String finReference) {
		this.finReference = finReference;
	}

	public String getCustAddrType() {
		return custAddrType;
	}

	public void setCustAddrType(String custAddrType) {
		this.custAddrType = custAddrType;
	}

	public int getCustAddrPriority() {
		return custAddrPriority;
	}

	public void setCustAddrPriority(int custAddrPriority) {
		this.custAddrPriority = custAddrPriority;
	}

	public String getCustAddrLine3() {
		return custAddrLine3;
	}

	public void setCustAddrLine3(String custAddrLine3) {
		this.custAddrLine3 = custAddrLine3;
	}

	public String getCustAddrHNbr() {
		return custAddrHNbr;
	}

	public void setCustAddrHNbr(String custAddrHNbr) {
		this.custAddrHNbr = custAddrHNbr;
	}

	public String getCustFlatNbr() {
		return custFlatNbr;
	}

	public void setCustFlatNbr(String custFlatNbr) {
		this.custFlatNbr = custFlatNbr;
	}

	public String getCustAddrStreet() {
		return custAddrStreet;
	}

	public void setCustAddrStreet(String custAddrStreet) {
		this.custAddrStreet = custAddrStreet;
	}

	public String getCustAddrLine1() {
		return custAddrLine1;
	}

	public void setCustAddrLine1(String custAddrLine1) {
		this.custAddrLine1 = custAddrLine1;
	}

	public String getCustAddrLine2() {
		return custAddrLine2;
	}

	public void setCustAddrLine2(String custAddrLine2) {
		this.custAddrLine2 = custAddrLine2;
	}

	public String getCustAddrCity() {
		return custAddrCity;
	}

	public void setCustAddrCity(String custAddrCity) {
		this.custAddrCity = custAddrCity;
	}

	public String getCustAddrLine4() {
		return custAddrLine4;
	}

	public void setCustAddrLine4(String custAddrLine4) {
		this.custAddrLine4 = custAddrLine4;
	}

	public String getCustDistrict() {
		return custDistrict;
	}

	public void setCustDistrict(String custDistrict) {
		this.custDistrict = custDistrict;
	}

	public String getCustAddrProvince() {
		return custAddrProvince;
	}

	public void setCustAddrProvince(String custAddrProvince) {
		this.custAddrProvince = custAddrProvince;
	}

	public String getCustAddrCountry() {
		return custAddrCountry;
	}

	public void setCustAddrCountry(String custAddrCountry) {
		this.custAddrCountry = custAddrCountry;
	}

	public String getCustAddrZIP() {
		return custAddrZIP;
	}

	public void setCustAddrZIP(String custAddrZIP) {
		this.custAddrZIP = custAddrZIP;
	}

	public Long getPinCodeId() {
		return pinCodeId;
	}

	public void setPinCodeId(Long pinCodeId) {
		this.pinCodeId = pinCodeId;
	}

	public String getPhoneTypeCode() {
		return phoneTypeCode;
	}

	public void setPhoneTypeCode(String phoneTypeCode) {
		this.phoneTypeCode = phoneTypeCode;
	}

	public int getPhoneTypePriority() {
		return phoneTypePriority;
	}

	public void setPhoneTypePriority(int phoneTypePriority) {
		this.phoneTypePriority = phoneTypePriority;
	}

	public String getPhoneNumber() {
		return phoneNumber;
	}

	public void setPhoneNumber(String phoneNumber) {
		this.phoneNumber = phoneNumber;
	}

	public String getCustEMailTypeCode() {
		return custEMailTypeCode;
	}

	public void setCustEMailTypeCode(String custEMailTypeCode) {
		this.custEMailTypeCode = custEMailTypeCode;
	}

	public int getCustEMailPriority() {
		return custEMailPriority;
	}

	public void setCustEMailPriority(int custEMailPriority) {
		this.custEMailPriority = custEMailPriority;
	}

	public String getCustEMail() {
		return custEMail;
	}

	public void setCustEMail(String custEMail) {
		this.custEMail = custEMail;
	}

	public LoggedInUser getUserDetails() {
		return userDetails;
	}

	public void setUserDetails(LoggedInUser userDetails) {
		this.userDetails = userDetails;
	}

	public String getSource() {
		return source;
	}

	public void setSource(String source) {
		this.source = source;
	}

}
