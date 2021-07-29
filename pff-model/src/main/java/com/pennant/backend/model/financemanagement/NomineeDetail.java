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
 * * FileName : NomineeDetail.java * * Author : PENNANT TECHONOLOGIES * * Creation Date : 30-01-2018 * * Modified Date :
 * 30-01-2018 * * Description : * *
 ********************************************************************************************
 * Date Author Version Comments *
 ********************************************************************************************
 * 30-01-2018 PENNANT 0.1 * * * * * * * * *
 ********************************************************************************************
 */
package com.pennant.backend.model.financemanagement;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import com.pennanttech.pennapps.core.model.AbstractWorkflowEntity;
import com.pennanttech.pennapps.core.model.LoggedInUser;

/**
 * Model class for the <b>NomineeDetail table</b>.<br>
 *
 */
public class NomineeDetail extends AbstractWorkflowEntity {
	private static final long serialVersionUID = 1L;

	private long nomineeId = Long.MIN_VALUE;
	private String finReference;
	private String finReferenceName;
	private String nomineeName;
	private String relation;
	private boolean minor;
	private String guardianName;
	private String guardianRelation;
	private Date dOB;
	private boolean sameAsApplicantAddr;
	private String address1;
	private String address2;
	private String address3;
	private String landMark;
	private String street;
	private long pinCode = 0;
	private String province;
	private String country;
	private String city;
	private long district = 0;
	private boolean newNominee = false;
	private String lovValue;
	private NomineeDetail befImage;
	private LoggedInUser userDetails;
	private String provinceName;
	private String cityName;
	private String pinCodeId;
	private String districtCode;
	private String districtName;
	private String relationDesc;
	private String countryName;
	private String finType;
	private String finBranch;
	private BigDecimal finAmount = BigDecimal.ZERO;
	private String currency;
	private Date maturityDate;
	private Date finStartDate;
	private String requestStage;
	private String custCIF;
	private String mobileNum;
	private String panNumber;
	private String custName;
	private long schemeId = 0;
	private String schemeCode;
	private String schemeDesc;
	private String branchDesc;
	private String finTypeDesc;
	private String areaName;
	private String profitDaysBasis;
	private String scheduleMethod;
	private long custID;
	private String rcdMaintainSts;

	public NomineeDetail() {
		super();
	}

	public NomineeDetail(long id) {
		super();
		this.setId(id);
	}

	public Set<String> getExcludeFields() {
		Set<String> excludeFields = new HashSet<String>();
		excludeFields.add("finReferenceName");
		excludeFields.add("provinceName");
		excludeFields.add("cityName");
		excludeFields.add("pinCodeId");
		excludeFields.add("districtCode");
		excludeFields.add("districtName");
		excludeFields.add("relationDesc");
		excludeFields.add("countryName");
		excludeFields.add("finType");
		excludeFields.add("finBranch");
		excludeFields.add("finAmount");
		excludeFields.add("maturityDate");
		excludeFields.add("finStartDate");
		excludeFields.add("requestStage");
		excludeFields.add("custCIF");
		excludeFields.add("mobileNum");
		excludeFields.add("panNumber");
		excludeFields.add("custName");
		excludeFields.add("schemeId");
		excludeFields.add("schemeCode");
		excludeFields.add("schemeDesc");
		excludeFields.add("branchDesc");
		excludeFields.add("finTypeDesc");
		excludeFields.add("areaName");
		excludeFields.add("scheduleMethod");
		excludeFields.add("profitDaysBasis");
		excludeFields.add("currency");
		excludeFields.add("custID");
		excludeFields.add("newNominee");
		excludeFields.add("finNomineeDetail");
		excludeFields.add("rcdMaintainSts");
		return excludeFields;
	}

	public long getId() {
		return nomineeId;
	}

	public void setId(long id) {
		this.nomineeId = id;
	}

	public long getNomineeId() {
		return nomineeId;
	}

	public void setNomineeId(long nomineeId) {
		this.nomineeId = nomineeId;
	}

	public String getFinReference() {
		return finReference;
	}

	public void setFinReference(String finReference) {
		this.finReference = finReference;
	}

	public String getFinReferenceName() {
		return this.finReferenceName;
	}

	public void setFinReferenceName(String finReferenceName) {
		this.finReferenceName = finReferenceName;
	}

	public String getNomineeName() {
		return nomineeName;
	}

	public void setNomineeName(String nomineeName) {
		this.nomineeName = nomineeName;
	}

	public String getRelation() {
		return relation;
	}

	public void setRelation(String relation) {
		this.relation = relation;
	}

	public boolean isMinor() {
		return minor;
	}

	public void setMinor(boolean minor) {
		this.minor = minor;
	}

	public String getGuardianName() {
		return guardianName;
	}

	public void setGuardianName(String guardianName) {
		this.guardianName = guardianName;
	}

	public String getGuardianRelation() {
		return guardianRelation;
	}

	public void setGuardianRelation(String guardianRelation) {
		this.guardianRelation = guardianRelation;
	}

	public Date getDOB() {
		return dOB;
	}

	public void setDOB(Date dOB) {
		this.dOB = dOB;
	}

	public boolean isSameAsApplicantAddr() {
		return sameAsApplicantAddr;
	}

	public void setSameAsApplicantAddr(boolean sameAsApplicantAddr) {
		this.sameAsApplicantAddr = sameAsApplicantAddr;
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

	public String getLandMark() {
		return landMark;
	}

	public void setLandMark(String landMark) {
		this.landMark = landMark;
	}

	public String getStreet() {
		return street;
	}

	public void setStreet(String street) {
		this.street = street;
	}

	public long getPinCode() {
		return pinCode;
	}

	public void setPinCode(long pinCode) {
		this.pinCode = pinCode;
	}

	public String getProvince() {
		return province;
	}

	public void setProvince(String province) {
		this.province = province;
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

	public String getLovValue() {
		return lovValue;
	}

	public void setLovValue(String lovValue) {
		this.lovValue = lovValue;
	}

	public NomineeDetail getBefImage() {
		return this.befImage;
	}

	public void setBefImage(NomineeDetail beforeImage) {
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

	public Date getdOB() {
		return dOB;
	}

	public void setdOB(Date dOB) {
		this.dOB = dOB;
	}

	public String getProvinceName() {
		return provinceName;
	}

	public void setProvinceName(String provinceName) {
		this.provinceName = provinceName;
	}

	public String getCityName() {
		return cityName;
	}

	public void setCityName(String cityName) {
		this.cityName = cityName;
	}

	public String getPinCodeId() {
		return pinCodeId;
	}

	public void setPinCodeId(String pinCodeId) {
		this.pinCodeId = pinCodeId;
	}

	public String getDistrictCode() {
		return districtCode;
	}

	public void setDistrictCode(String districtCode) {
		this.districtCode = districtCode;
	}

	public String getDistrictName() {
		return districtName;
	}

	public void setDistrictName(String districtName) {
		this.districtName = districtName;
	}

	public String getRelationDesc() {
		return relationDesc;
	}

	public void setRelationDesc(String relationDesc) {
		this.relationDesc = relationDesc;
	}

	public String getCountryName() {
		return countryName;
	}

	public void setCountryName(String countryName) {
		this.countryName = countryName;
	}

	public String getFinType() {
		return finType;
	}

	public void setFinType(String finType) {
		this.finType = finType;
	}

	public String getFinBranch() {
		return finBranch;
	}

	public void setFinBranch(String finBranch) {
		this.finBranch = finBranch;
	}

	public BigDecimal getFinAmount() {
		return finAmount;
	}

	public void setFinAmount(BigDecimal finAmount) {
		this.finAmount = finAmount;
	}

	public Date getMaturityDate() {
		return maturityDate;
	}

	public void setMaturityDate(Date maturityDate) {
		this.maturityDate = maturityDate;
	}

	public Date getFinStartDate() {
		return finStartDate;
	}

	public void setFinStartDate(Date finStartDate) {
		this.finStartDate = finStartDate;
	}

	public String getRequestStage() {
		return requestStage;
	}

	public void setRequestStage(String requestStage) {
		this.requestStage = requestStage;
	}

	public String getCustCIF() {
		return custCIF;
	}

	public void setCustCIF(String custCIF) {
		this.custCIF = custCIF;
	}

	public String getMobileNum() {
		return mobileNum;
	}

	public void setMobileNum(String mobileNum) {
		this.mobileNum = mobileNum;
	}

	public String getPanNumber() {
		return panNumber;
	}

	public void setPanNumber(String panNumber) {
		this.panNumber = panNumber;
	}

	public String getCustName() {
		return custName;
	}

	public void setCustName(String custName) {
		this.custName = custName;
	}

	public long getSchemeId() {
		return schemeId;
	}

	public void setSchemeId(long schemeId) {
		this.schemeId = schemeId;
	}

	public String getSchemeCode() {
		return schemeCode;
	}

	public void setSchemeCode(String schemeCode) {
		this.schemeCode = schemeCode;
	}

	public String getSchemeDesc() {
		return schemeDesc;
	}

	public void setSchemeDesc(String schemeDesc) {
		this.schemeDesc = schemeDesc;
	}

	public String getCurrency() {
		return currency;
	}

	public void setCurrency(String currency) {
		this.currency = currency;
	}

	public String getBranchDesc() {
		return branchDesc;
	}

	public void setBranchDesc(String branchDesc) {
		this.branchDesc = branchDesc;
	}

	public String getFinTypeDesc() {
		return finTypeDesc;
	}

	public void setFinTypeDesc(String finTypeDesc) {
		this.finTypeDesc = finTypeDesc;
	}

	public String getAreaName() {
		return areaName;
	}

	public void setAreaName(String areaName) {
		this.areaName = areaName;
	}

	public String getProfitDaysBasis() {
		return profitDaysBasis;
	}

	public void setProfitDaysBasis(String profitDaysBasis) {
		this.profitDaysBasis = profitDaysBasis;
	}

	public String getScheduleMethod() {
		return scheduleMethod;
	}

	public void setScheduleMethod(String scheduleMethod) {
		this.scheduleMethod = scheduleMethod;
	}

	public long getCustID() {
		return custID;
	}

	public void setCustID(long custID) {
		this.custID = custID;
	}

	public boolean isNewNominee() {
		return newNominee;
	}

	public void setNewNominee(boolean newNominee) {
		this.newNominee = newNominee;
	}

	public String getRcdMaintainSts() {
		return rcdMaintainSts;
	}

	public void setRcdMaintainSts(String rcdMaintainSts) {
		this.rcdMaintainSts = rcdMaintainSts;
	}

	public long getDistrict() {
		return district;
	}

	public void setDistrict(long district) {
		this.district = district;
	}

}
