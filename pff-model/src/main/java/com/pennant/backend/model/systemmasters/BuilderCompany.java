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
 * * FileName : BuilderCompany.java * * Author : PENNANT TECHONOLOGIES * * Creation Date : 22-05-2017 * * Modified Date
 * : 22-05-2017 * * Description : * *
 ********************************************************************************************
 * Date Author Version Comments *
 ********************************************************************************************
 * 22-05-2017 PENNANT 0.1 * * * * * * * * *
 ********************************************************************************************
 */
package com.pennant.backend.model.systemmasters;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;

import com.pennanttech.pennapps.core.model.AbstractWorkflowEntity;
import com.pennanttech.pennapps.core.model.LoggedInUser;

/**
 * Model class for the <b>BuilderCompany table</b>.<br>
 *
 */
@XmlType(propOrder = { "id", "name", "segmentation", "groupId", "active" })
@XmlAccessorType(XmlAccessType.FIELD)
public class BuilderCompany extends AbstractWorkflowEntity {
	private static final long serialVersionUID = 1L;

	private long id = Long.MIN_VALUE;
	private String name;
	private String segmentation;
	private long groupId;
	private String segmentationName;
	private String groupIdName;
	private String fieldCode;
	@XmlTransient
	private String lovValue;
	@XmlTransient
	private BuilderCompany befImage;
	@XmlTransient
	private LoggedInUser userDetails;
	private String apfType;
	private String peDevId;
	private String entityType;
	private String entyDesc;
	private String emailId;
	private String cityType;
	private String address1;
	private String address2;
	private String address3;
	private String city;
	private String cityName;
	private String state;
	private String code;
	private String codeName;
	private String areaName;
	private BigDecimal devavailablity = BigDecimal.ZERO;
	private BigDecimal magnitude = BigDecimal.ZERO;
	private BigDecimal absavailablity = BigDecimal.ZERO;
	private BigDecimal totalProj;
	private String approved;
	private String remarks;
	private String panDetails;
	private String benfName;
	private String accountNo;
	// private String bankNameDesc;
	private String bankName;
	private Long bankBranchId;
	private String BranDesc;
	private String ifsc;
	private BigDecimal limitOnAmt = BigDecimal.ZERO;
	private BigDecimal limitOnUnits = BigDecimal.ZERO;
	private BigDecimal currentExpUni;
	private BigDecimal currentExpAmt = BigDecimal.ZERO;
	private Date dateOfInCop;
	private BigDecimal noOfProj;
	private BigDecimal assHLPlayers;
	private BigDecimal onGoingProj;
	private BigDecimal expInBusiness;
	private String recommendation;
	private BigDecimal magintudeInLacs;
	private BigDecimal noOfProjCons;
	private String lovDescCIFName;
	private String custCIF;
	private boolean active;
	private Long custId;
	private Long pinCodeId;

	public BuilderCompany() {
		super();
	}

	public BuilderCompany(long id) {
		super();
		this.setId(id);
	}

	public Set<String> getExcludeFields() {
		Set<String> excludeFields = new HashSet<String>();
		excludeFields.add("segmentationName");
		excludeFields.add("groupIdName");
		excludeFields.add("fieldCode");
		excludeFields.add("lovDescCIFName");
		excludeFields.add("custCIF");
		excludeFields.add("cityName");
		excludeFields.add("codeName");
		excludeFields.add("bnkName");
		excludeFields.add("BranDesc");
		excludeFields.add("entyDesc");
		excludeFields.add("ifsc");
		excludeFields.add("bankName");
		excludeFields.add("bankNameDesc");
		excludeFields.add("areaName");
		return excludeFields;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getSegmentation() {
		return segmentation;
	}

	public void setSegmentation(String segmentation) {
		this.segmentation = segmentation;
	}

	public long getGroupId() {
		return groupId;
	}

	public void setGroupId(long groupId) {
		this.groupId = groupId;
	}

	public String getLovValue() {
		return lovValue;
	}

	public void setLovValue(String lovValue) {
		this.lovValue = lovValue;
	}

	public BuilderCompany getBefImage() {
		return this.befImage;
	}

	public void setBefImage(BuilderCompany beforeImage) {
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

	public String getGroupIdName() {
		return groupIdName;
	}

	public void setGroupIdName(String groupIdName) {
		this.groupIdName = groupIdName;
	}

	public String getSegmentationName() {
		return segmentationName;
	}

	public void setSegmentationName(String segmentationName) {
		this.segmentationName = segmentationName;
	}

	public String getFieldCode() {
		return fieldCode;
	}

	public void setFieldCode(String fieldCode) {
		this.fieldCode = fieldCode;
	}

	public BigDecimal getMagnitude() {
		return magnitude;
	}

	public void setMagnitude(BigDecimal magnitude) {
		this.magnitude = magnitude;
	}

	public String getAreaName() {
		return areaName;
	}

	public void setAreaName(String areaName) {
		this.areaName = areaName;
	}

	public BigDecimal getCurrentExpUni() {
		return currentExpUni;
	}

	public void setCurrentExpUni(BigDecimal currentExpUni) {
		this.currentExpUni = currentExpUni;
	}

	public boolean isActive() {
		return active;
	}

	public void setActive(boolean active) {
		this.active = active;
	}

	public BigDecimal getDevavailablity() {
		return devavailablity;
	}

	public void setDevavailablity(BigDecimal devavailablity) {
		this.devavailablity = devavailablity;
	}

	public Long getCustId() {
		return custId;
	}

	public void setCustId(Long custId) {
		this.custId = custId;
	}

	public String getCityName() {
		return cityName;
	}

	public void setCityName(String cityName) {
		this.cityName = cityName;
	}

	public String getCodeName() {
		return codeName;
	}

	public void setCodeName(String codeName) {
		this.codeName = codeName;
	}

	public String getEntyDesc() {
		return entyDesc;
	}

	public void setEntyDesc(String entyDesc) {
		this.entyDesc = entyDesc;
	}

	public String getCustCIF() {
		return custCIF;
	}

	public void setCustCIF(String custCIF) {
		this.custCIF = custCIF;
	}

	public String getApfType() {
		return apfType;
	}

	public void setApfType(String apfType) {
		this.apfType = apfType;
	}

	public String getPeDevId() {
		return peDevId;
	}

	public void setPeDevId(String peDevId) {
		this.peDevId = peDevId;
	}

	public String getEntityType() {
		return entityType;
	}

	public void setEntityType(String entityType) {
		this.entityType = entityType;
	}

	public String getEmailId() {
		return emailId;
	}

	public void setEmailId(String emailId) {
		this.emailId = emailId;
	}

	public String getCityType() {
		return cityType;
	}

	public void setCityType(String cityType) {
		this.cityType = cityType;
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

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public BigDecimal getAbsavailablity() {
		return absavailablity;
	}

	public void setAbsavailablity(BigDecimal absavailablity) {
		this.absavailablity = absavailablity;
	}

	public BigDecimal getTotalProj() {
		return totalProj;
	}

	public void setTotalProj(BigDecimal totalProj) {
		this.totalProj = totalProj;
	}

	public String getApproved() {
		return approved;
	}

	public void setApproved(String approved) {
		this.approved = approved;
	}

	public String getRemarks() {
		return remarks;
	}

	public void setRemarks(String remarks) {
		this.remarks = remarks;
	}

	public String getPanDetails() {
		return panDetails;
	}

	public void setPanDetails(String panDetails) {
		this.panDetails = panDetails;
	}

	public String getBenfName() {
		return benfName;
	}

	public void setBenfName(String benfName) {
		this.benfName = benfName;
	}

	public String getAccountNo() {
		return accountNo;
	}

	public void setAccountNo(String accountNo) {
		this.accountNo = accountNo;
	}

	public String getBankName() {
		return bankName;
	}

	public void setBankName(String bankName) {
		this.bankName = bankName;
	}

	public Long getBankBranchId() {
		return bankBranchId;
	}

	public void setBankBranchId(Long bankBranchId) {
		this.bankBranchId = bankBranchId;
	}

	public String getIfsc() {
		return ifsc;
	}

	public void setIfsc(String ifsc) {
		this.ifsc = ifsc;
	}

	public BigDecimal getLimitOnAmt() {
		return limitOnAmt;
	}

	public void setLimitOnAmt(BigDecimal limitOnAmt) {
		this.limitOnAmt = limitOnAmt;
	}

	public BigDecimal getLimitOnUnits() {
		return limitOnUnits;
	}

	public void setLimitOnUnits(BigDecimal limitOnUnits) {
		this.limitOnUnits = limitOnUnits;
	}

	public BigDecimal getCurrentExpAmt() {
		return currentExpAmt;
	}

	public void setCurrentExpAmt(BigDecimal currentExpAmt) {
		this.currentExpAmt = currentExpAmt;
	}

	public Date getDateOfInCop() {
		return dateOfInCop;
	}

	public void setDateOfInCop(Date dateOfInCop) {
		this.dateOfInCop = dateOfInCop;
	}

	public BigDecimal getNoOfProj() {
		return noOfProj;
	}

	public void setNoOfProj(BigDecimal noOfProj) {
		this.noOfProj = noOfProj;
	}

	public BigDecimal getAssHLPlayers() {
		return assHLPlayers;
	}

	public void setAssHLPlayers(BigDecimal assHLPlayers) {
		this.assHLPlayers = assHLPlayers;
	}

	public BigDecimal getOnGoingProj() {
		return onGoingProj;
	}

	public void setOnGoingProj(BigDecimal onGoingProj) {
		this.onGoingProj = onGoingProj;
	}

	public BigDecimal getExpInBusiness() {
		return expInBusiness;
	}

	public void setExpInBusiness(BigDecimal expInBusiness) {
		this.expInBusiness = expInBusiness;
	}

	public String getRecommendation() {
		return recommendation;
	}

	public void setRecommendation(String recommendation) {
		this.recommendation = recommendation;
	}

	public BigDecimal getMagintudeInLacs() {
		return magintudeInLacs;
	}

	public void setMagintudeInLacs(BigDecimal magintudeInLacs) {
		this.magintudeInLacs = magintudeInLacs;
	}

	public BigDecimal getNoOfProjCons() {
		return noOfProjCons;
	}

	public void setNoOfProjCons(BigDecimal noOfProjCons) {
		this.noOfProjCons = noOfProjCons;
	}

	public String getLovDescCIFName() {
		return lovDescCIFName;
	}

	public void setLovDescCIFName(String lovDescCIFName) {
		this.lovDescCIFName = lovDescCIFName;
	}

	public String getBranDesc() {
		return BranDesc;
	}

	public void setBranDesc(String branDesc) {
		BranDesc = branDesc;
	}

	public Long getPinCodeId() {
		return pinCodeId;
	}

	public void setPinCodeId(Long pinCodeId) {
		this.pinCodeId = pinCodeId;
	}

}