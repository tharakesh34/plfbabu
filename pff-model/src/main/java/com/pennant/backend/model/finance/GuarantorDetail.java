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
 * * FileName : GuarantorDetail.java * * Author : PENNANT TECHONOLOGIES * * Creation Date : 10-09-2013 * * Modified Date
 * : 10-09-2013 * * Description : * *
 ******************************************************************************************** 
 * Date Author Version Comments *
 ******************************************************************************************** 
 * 10-09-2013 Pennant 0.1 * * * * * * * * *
 ******************************************************************************************** 
 */
package com.pennant.backend.model.finance;

import java.math.BigDecimal;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

import com.pennant.backend.model.customermasters.CustomerDetails;
import com.pennanttech.pennapps.core.model.AbstractWorkflowEntity;
import com.pennanttech.pennapps.core.model.LoggedInUser;

/**
 * Model class for the <b>GuarantorDetail table</b>.<br>
 * 
 */
@XmlType(propOrder = { "bankCustomer", "guarantorCIF", "guranteePercentage", "name", "mobileNo", "emailId",
		"guarantorIDType", "guarantorIDNumber", "guarantorProof", "addrHNbr", "flatNbr", "addrStreet", "addrLine1",
		"addrLine2", "POBox", "addrCountry", "addrProvince", "addrCity", "addrZIP" })
@XmlAccessorType(XmlAccessType.NONE)
public class GuarantorDetail extends AbstractWorkflowEntity {
	private static final long serialVersionUID = 1L;

	private long guarantorId = Long.MIN_VALUE;
	private long finID;
	private String finReference;

	@XmlElement
	private boolean bankCustomer;
	@XmlElement
	private String guarantorCIF;
	private String guarantorCIFName;
	@XmlElement
	private String guarantorIDType;
	private String guarantorIDTypeName;

	@XmlElement
	private String guarantorIDNumber;
	@XmlElement
	private String name;
	@XmlElement
	private BigDecimal guranteePercentage = BigDecimal.ZERO;
	@XmlElement
	private String mobileNo;
	@XmlElement
	private String emailId;
	@XmlElement(name = "gender")
	private String guarantorGenderCode;

	@XmlElement(name = "idDocContent")
	private byte[] guarantorProof = new byte[Byte.valueOf("0")];
	@XmlElement(name = "idDocName")
	private String guarantorProofName;
	@XmlElement
	private String remarks;
	private String primaryExposure;
	private String secondaryExposure;
	private String guarantorExposure;
	private String worstStatus;
	private String status;

	// Address Details
	@XmlElement
	private String addrHNbr;
	@XmlElement
	private String flatNbr;
	@XmlElement
	private String addrStreet;
	@XmlElement
	private String addrLine1;
	@XmlElement
	private String addrLine2;
	@XmlElement(name = "poBox")
	private String POBox;
	@XmlElement
	private String addrCountry;
	private String lovDescAddrCountryName;
	@XmlElement
	private String addrProvince;
	private String lovDescAddrProvinceName;
	@XmlElement
	private String addrCity;
	private String lovDescAddrCityName;
	@XmlElement
	private String addrZIP;
	private String lovDescAddrZip;

	private List<FinanceExposure> primaryList = null;
	private List<FinanceExposure> secoundaryList = null;
	private List<FinanceExposure> guarantorList = null;
	private FinanceExposure sumPrimaryDetails = null;
	private FinanceExposure sumSecondaryDetails = null;
	private FinanceExposure sumGurantorDetails = null;
	private String lovValue;
	private GuarantorDetail befImage;
	private LoggedInUser userDetails;

	// API validation purpose only
	@SuppressWarnings("unused")
	private GuarantorDetail validateGuarantor = this;
	private long custID;
	private String custShrtName;

	private CustomerDetails customerDetails;
	private Date lovCustDob;

	public GuarantorDetail() {
		super();
	}

	public Set<String> getExcludeFields() {
		Set<String> excludeFields = new HashSet<String>();
		excludeFields.add("guarantorCIFName");
		excludeFields.add("guarantorIDTypeName");
		excludeFields.add("primaryExposure");
		excludeFields.add("secondaryExposure");
		excludeFields.add("guarantorExposure");
		excludeFields.add("worstStatus");
		excludeFields.add("status");
		excludeFields.add("primaryList");
		excludeFields.add("secoundaryList");
		excludeFields.add("guarantorList");
		excludeFields.add("sumPrimaryDetails");
		excludeFields.add("sumSecondaryDetails");
		excludeFields.add("sumGurantorDetails");
		excludeFields.add("name");
		excludeFields.add("validateGuarantor");
		excludeFields.add("custID");
		excludeFields.add("custShrtName");
		excludeFields.add("customerDetails");
		excludeFields.add("lovCustDob");
		excludeFields.add("lovDescAddrZip");

		return excludeFields;
	}

	public GuarantorDetail(long id) {
		super();
		this.setId(id);
	}

	public long getId() {
		return guarantorId;
	}

	public void setId(long id) {
		this.guarantorId = id;
	}

	public long getGuarantorId() {
		return guarantorId;
	}

	public void setGuarantorId(long guarantorId) {
		this.guarantorId = guarantorId;
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

	public boolean isBankCustomer() {
		return bankCustomer;
	}

	public void setBankCustomer(boolean bankCustomer) {
		this.bankCustomer = bankCustomer;
	}

	public String getGuarantorCIF() {
		return guarantorCIF;
	}

	public void setGuarantorCIF(String guarantorCIF) {
		this.guarantorCIF = guarantorCIF;
	}

	public String getGuarantorCIFName() {
		return this.guarantorCIFName;
	}

	public void setGuarantorCIFName(String guarantorCIFName) {
		this.guarantorCIFName = guarantorCIFName;
	}

	public String getGuarantorIDType() {
		return guarantorIDType;
	}

	public void setGuarantorIDType(String guarantorIDType) {
		this.guarantorIDType = guarantorIDType;
	}

	public String getGuarantorIDTypeName() {
		return this.guarantorIDTypeName;
	}

	public void setGuarantorIDTypeName(String guarantorIDTypeName) {
		this.guarantorIDTypeName = guarantorIDTypeName;
	}

	public String getGuarantorIDNumber() {
		return guarantorIDNumber;
	}

	public void setGuarantorIDNumber(String guarantorIDNumber) {
		this.guarantorIDNumber = guarantorIDNumber;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public BigDecimal getGuranteePercentage() {
		return guranteePercentage;
	}

	public void setGuranteePercentage(BigDecimal guranteePercentage) {
		this.guranteePercentage = guranteePercentage;
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

	public String getGuarantorProofName() {
		return guarantorProofName;
	}

	public void setGuarantorProofName(String guarantorProofName) {
		this.guarantorProofName = guarantorProofName;
	}

	public String getLovValue() {
		return lovValue;
	}

	public void setLovValue(String lovValue) {
		this.lovValue = lovValue;
	}

	public GuarantorDetail getBefImage() {
		return this.befImage;
	}

	public void setBefImage(GuarantorDetail beforeImage) {
		this.befImage = beforeImage;
	}

	public LoggedInUser getUserDetails() {
		return userDetails;
	}

	public void setUserDetails(LoggedInUser userDetails) {
		this.userDetails = userDetails;
	}

	public String getRemarks() {
		return remarks;
	}

	public void setRemarks(String remarks) {
		this.remarks = remarks;
	}

	public byte[] getGuarantorProof() {
		return guarantorProof;
	}

	public void setGuarantorProof(byte[] guarantorProof) {
		this.guarantorProof = guarantorProof;
	}

	public String getPrimaryExposure() {
		return primaryExposure;
	}

	public void setPrimaryExposure(String primaryExposure) {
		this.primaryExposure = primaryExposure;
	}

	public String getSecondaryExposure() {
		return secondaryExposure;
	}

	public void setSecondaryExposure(String secondaryExposure) {
		this.secondaryExposure = secondaryExposure;
	}

	public String getGuarantorExposure() {
		return guarantorExposure;
	}

	public void setGuarantorExposure(String guarantorExposure) {
		this.guarantorExposure = guarantorExposure;
	}

	public String getWorstStatus() {
		return worstStatus;
	}

	public void setWorstStatus(String worstStatus) {
		this.worstStatus = worstStatus;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public List<FinanceExposure> getPrimaryList() {
		return primaryList;
	}

	public void setPrimaryList(List<FinanceExposure> primaryList) {
		this.primaryList = primaryList;
	}

	public List<FinanceExposure> getSecoundaryList() {
		return secoundaryList;
	}

	public void setSecoundaryList(List<FinanceExposure> secoundaryList) {
		this.secoundaryList = secoundaryList;
	}

	public List<FinanceExposure> getGuarantorList() {
		return guarantorList;
	}

	public void setGuarantorList(List<FinanceExposure> guarantorList) {
		this.guarantorList = guarantorList;
	}

	public FinanceExposure getSumPrimaryDetails() {
		return sumPrimaryDetails;
	}

	public void setSumPrimaryDetails(FinanceExposure sumPrimaryDetails) {
		this.sumPrimaryDetails = sumPrimaryDetails;
	}

	public FinanceExposure getSumSecondaryDetails() {
		return sumSecondaryDetails;
	}

	public void setSumSecondaryDetails(FinanceExposure sumSecondaryDetails) {
		this.sumSecondaryDetails = sumSecondaryDetails;
	}

	public FinanceExposure getSumGurantorDetails() {
		return sumGurantorDetails;
	}

	public void setSumGurantorDetails(FinanceExposure sumGurantorDetails) {
		this.sumGurantorDetails = sumGurantorDetails;
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
		this.POBox = pOBox;
	}

	public String getAddrCountry() {
		return addrCountry;
	}

	public void setAddrCountry(String addrCountry) {
		this.addrCountry = addrCountry;
	}

	public String getLovDescAddrCountryName() {
		return lovDescAddrCountryName;
	}

	public void setLovDescAddrCountryName(String lovDescAddrCountryName) {
		this.lovDescAddrCountryName = lovDescAddrCountryName;
	}

	public String getAddrProvince() {
		return addrProvince;
	}

	public void setAddrProvince(String addrProvince) {
		this.addrProvince = addrProvince;
	}

	public String getLovDescAddrProvinceName() {
		return lovDescAddrProvinceName;
	}

	public void setLovDescAddrProvinceName(String lovDescAddrProvinceName) {
		this.lovDescAddrProvinceName = lovDescAddrProvinceName;
	}

	public String getAddrCity() {
		return addrCity;
	}

	public void setAddrCity(String addrCity) {
		this.addrCity = addrCity;
	}

	public String getLovDescAddrCityName() {
		return lovDescAddrCityName;
	}

	public void setLovDescAddrCityName(String lovDescAddrCityName) {
		this.lovDescAddrCityName = lovDescAddrCityName;
	}

	public String getAddrZIP() {
		return addrZIP;
	}

	public void setAddrZIP(String addrZIP) {
		this.addrZIP = addrZIP;
	}

	public long getCustID() {
		return custID;
	}

	public void setCustID(long custID) {
		this.custID = custID;
	}

	public String getCustShrtName() {
		return custShrtName;
	}

	public void setCustShrtName(String custShrtName) {
		this.custShrtName = custShrtName;
	}

	public String getGuarantorGenderCode() {
		return guarantorGenderCode;
	}

	public void setGuarantorGenderCode(String guarantorGenderCode) {
		this.guarantorGenderCode = guarantorGenderCode;
	}

	public CustomerDetails getCustomerDetails() {
		return customerDetails;
	}

	public void setCustomerDetails(CustomerDetails customerDetails) {
		this.customerDetails = customerDetails;
	}

	public Date getLovCustDob() {
		return lovCustDob;
	}

	public void setLovCustDob(Date lovCustDob) {
		this.lovCustDob = lovCustDob;
	}

	public String getLovDescAddrZip() {
		return lovDescAddrZip;
	}

	public void setLovDescAddrZip(String lovDescAddrZip) {
		this.lovDescAddrZip = lovDescAddrZip;
	}

}
