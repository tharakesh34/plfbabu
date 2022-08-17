/**
 * Copyright 2011 - Pennant Technologies
 * 
 * This file is part of Pennant Java Application Framework and related Products. All
 * components/modules/functions/classes/logic in this software, unless otherwise stated, the property of Pennant
 * Technologies.
 * 
 * Copyright and other intellectual property laws protect these materials. Reproduction or retransmission of the
 * materials, in whole or in part, in any manner, without the prior written consent of the copyright holder, is a
 * violation of copyright law.
 */

/**
 ********************************************************************************************
 * FILE HEADER *
 ********************************************************************************************
 * * FileName : FinanceTaxDetail.java * * Author : PENNANT TECHONOLOGIES * * Creation Date : 17-06-2017 * * Modified
 * Date : 17-06-2017 * * Description : * *
 ********************************************************************************************
 * Date Author Version Comments *
 ********************************************************************************************
 * 17-06-2017 PENNANT 0.1 * * * * * * * * *
 ********************************************************************************************
 */
package com.pennant.backend.model.finance.financetaxdetail;

import java.sql.Timestamp;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;

import com.pennant.backend.model.WSReturnStatus;
import com.pennanttech.pennapps.core.model.AbstractWorkflowEntity;
import com.pennanttech.pennapps.core.model.LoggedInUser;

/**
 * Model class for the <b>FinanceTaxDetail table</b>.<br>
 *
 */
@XmlType(propOrder = { "finReference", "taxExempted", "applicableFor", "custCIF", "taxNumber", "addrLine1", "addrLine2",
		"addrLine3", "addrLine4", "country", "province", "city", "pinCode", "returnStatus" })
@XmlAccessorType(XmlAccessType.NONE)
public class FinanceTaxDetail extends AbstractWorkflowEntity {
	private static final long serialVersionUID = 1L;

	private long finID;
	@XmlElement
	private String finReference;
	@XmlElement(name = "applicableFor")
	private String applicableFor;
	private long taxCustId;
	@XmlElement(name = "gstExempted")
	private boolean taxExempted;
	@XmlElement(name = "gstNumber")
	private String taxNumber;
	@XmlElement(name = "addrLine1")
	private String addrLine1;
	@XmlElement(name = "addrLine2")
	private String addrLine2;
	@XmlElement(name = "addrLine3")
	private String addrLine3;
	@XmlElement(name = "addrLine4")
	private String addrLine4;
	@XmlElement(name = "country")
	private String country;
	private String countryName;
	@XmlElement(name = "province")
	private String province;
	private String provinceName;
	@XmlElement(name = "city")
	private String city;
	private String cityName;
	@XmlElement(name = "pinCode")
	private String pinCode;
	private String pinCodeName;
	@XmlTransient
	private String lovValue;
	@XmlTransient
	private FinanceTaxDetail befImage;
	@XmlTransient
	private LoggedInUser userDetails;
	@XmlElement
	private WSReturnStatus returnStatus;

	private String sourceId;

	@XmlElement(name = "cif")
	private String custCIF;
	private String custShrtName;

	private String sezCertificateNo;
	private Date sezValueDate;
	private String addressDetail;
	private boolean availCustTaxNum;
	@XmlElement
	private Long pinCodeId;
	private boolean rcdDelReq;

	public FinanceTaxDetail() {
		super();
	}

	public Set<String> getExcludeFields() {
		Set<String> excludeFields = new HashSet<String>();
		excludeFields.add("countryName");
		excludeFields.add("provinceName");
		excludeFields.add("cityName");
		excludeFields.add("pinCodeName");
		excludeFields.add("custCIF");
		excludeFields.add("sourceId");
		excludeFields.add("custShrtName");
		excludeFields.add("returnStatus");
		excludeFields.add("rcdDelReq");
		return excludeFields;
	}

	public long getFinID() {
		return finID;
	}

	public void setFinID(long finID) {
		this.finID = finID;
	}

	public String getFinReference() {
		return finReference;
	}

	public void setFinReference(String finReference) {
		this.finReference = finReference;
	}

	public String getApplicableFor() {
		return applicableFor;
	}

	public void setApplicableFor(String applicableFor) {
		this.applicableFor = applicableFor;
	}

	public boolean isTaxExempted() {
		return taxExempted;
	}

	public void setTaxExempted(boolean taxExempted) {
		this.taxExempted = taxExempted;
	}

	public String getTaxNumber() {
		return taxNumber;
	}

	public void setTaxNumber(String taxNumber) {
		this.taxNumber = taxNumber;
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

	public String getAddrLine3() {
		return addrLine3;
	}

	public void setAddrLine3(String addrLine3) {
		this.addrLine3 = addrLine3;
	}

	public String getAddrLine4() {
		return addrLine4;
	}

	public void setAddrLine4(String addrLine4) {
		this.addrLine4 = addrLine4;
	}

	public String getCountry() {
		return country;
	}

	public void setCountry(String country) {
		this.country = country;
	}

	public String getCountryName() {
		return this.countryName;
	}

	public void setCountryName(String countryName) {
		this.countryName = countryName;
	}

	public String getProvince() {
		return province;
	}

	public void setProvince(String province) {
		this.province = province;
	}

	public String getProvinceName() {
		return this.provinceName;
	}

	public void setProvinceName(String provinceName) {
		this.provinceName = provinceName;
	}

	public String getCity() {
		return city;
	}

	public void setCity(String city) {
		this.city = city;
	}

	public String getCityName() {
		return this.cityName;
	}

	public void setCityName(String cityName) {
		this.cityName = cityName;
	}

	public String getPinCode() {
		return pinCode;
	}

	public void setPinCode(String pinCode) {
		this.pinCode = pinCode;
	}

	public String getPinCodeName() {
		return this.pinCodeName;
	}

	public void setPinCodeName(String pinCodeName) {
		this.pinCodeName = pinCodeName;
	}

	public String getLovValue() {
		return lovValue;
	}

	public void setLovValue(String lovValue) {
		this.lovValue = lovValue;
	}

	public FinanceTaxDetail getBefImage() {
		return this.befImage;
	}

	public void setBefImage(FinanceTaxDetail beforeImage) {
		this.befImage = beforeImage;
	}

	public LoggedInUser getUserDetails() {
		return userDetails;
	}

	public void setUserDetails(LoggedInUser userDetails) {
		this.userDetails = userDetails;
	}

	public Timestamp getPrevMntOn() {
		return befImage == null ? null : befImage.getLastMntOn();
	}

	public String getCustCIF() {
		return custCIF;
	}

	public void setCustCIF(String custCIF) {
		this.custCIF = custCIF;
	}

	public String getCustShrtName() {
		return custShrtName;
	}

	public void setCustShrtName(String custShrtName) {
		this.custShrtName = custShrtName;
	}

	public long getTaxCustId() {
		return taxCustId;
	}

	public void setTaxCustId(long taxCustId) {
		this.taxCustId = taxCustId;
	}

	public String getSourceId() {
		return sourceId;
	}

	public void setSourceId(String sourceId) {
		this.sourceId = sourceId;
	}

	public WSReturnStatus getReturnStatus() {
		return returnStatus;
	}

	public void setReturnStatus(WSReturnStatus returnStatus) {
		this.returnStatus = returnStatus;
	}

	public String getSezCertificateNo() {
		return sezCertificateNo;
	}

	public void setSezCertificateNo(String sezCertificateNo) {
		this.sezCertificateNo = sezCertificateNo;
	}

	public Date getSezValueDate() {
		return sezValueDate;
	}

	public void setSezValueDate(Date sezValueDate) {
		this.sezValueDate = sezValueDate;
	}

	public String getAddressDetail() {
		return addressDetail;
	}

	public void setAddressDetail(String addressDetail) {
		this.addressDetail = addressDetail;
	}

	public Long getPinCodeId() {
		return pinCodeId;
	}

	public void setPinCodeId(Long pinCodeId) {
		this.pinCodeId = pinCodeId;
	}

	public boolean isAvailCustTaxNum() {
		return availCustTaxNum;
	}

	public void setAvailCustTaxNum(boolean availCustTaxNum) {
		this.availCustTaxNum = availCustTaxNum;
	}

	public boolean isRcdDelReq() {
		return rcdDelReq;
	}

	public void setRcdDelReq(boolean rcdDelReq) {
		this.rcdDelReq = rcdDelReq;
	}

}
