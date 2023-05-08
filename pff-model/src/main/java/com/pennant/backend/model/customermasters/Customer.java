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
 * * FileName : Customer.java * * Author : PENNANT TECHONOLOGIES * * Creation Date : 27-05-2011 * * Modified Date :
 * 27-05-2011 * * Description : * *
 ********************************************************************************************
 * Date Author Version Comments *
 ********************************************************************************************
 * 27-05-2011 Pennant 0.1 * * * * * * * * *
 ********************************************************************************************
 */
package com.pennant.backend.model.customermasters;

import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

import org.apache.commons.lang.StringUtils;

import com.pennant.backend.model.WSReturnStatus;
import com.pennanttech.pennapps.core.model.AbstractWorkflowEntity;
import com.pennanttech.pennapps.core.model.LoggedInUser;

/**
 * Model class for the <b>Customer table</b>.<br>
 * 
 */
@XmlType(propOrder = { "custFName", "custMName", "custLName", "custSalutationCode", "custShrtName", "custMotherMaiden",
		"custFNameLclLng", "custLng", "custDOB", "custCOB", "custNationality", "custResidentialSts", "custGenderCode",
		"custMaritalSts", "noOfDependents", "custTypeCode", "custSector", "custSubSector", "custSegment",
		"custSubSegment", "custIndustry", "custGroupID", "custParentCountry", "custRiskCountry", "custIsStaff",
		"custStaffID", "custEmpSts", "custDSA", "custDSADept", "custAddlDec1", "subCategory", "casteId", "religionId",
		"custShrtNameLclLng", "returnStatus", "custCRCPR", "custResidentialSts" })
@XmlAccessorType(XmlAccessType.NONE)
public class Customer extends AbstractWorkflowEntity {
	private static final long serialVersionUID = 2198471029043076055L;

	private long custID;
	private String custCIF;

	private String custCoreBank;

	private String custCtgCode;

	private String lovDescCustCtgCodeName;
	private String lovDescCustCtgType;

	@XmlElement(name = "type")
	private String custTypeCode;
	private String lovDescCustTypeCodeName;

	@XmlElement(name = "salutation")
	private String custSalutationCode;
	private String lovDescCustSalutationCodeName;

	@XmlElement(name = "firstName")
	private String custFName;

	@XmlElement(name = "middleName")
	private String custMName;

	@XmlElement(name = "lastName")
	private String custLName;

	@XmlElement(name = "shortName")
	private String custShrtName;

	@XmlElement(name = "motherName")
	private String custFNameLclLng;
	private String custMNameLclLng;
	private String custLNameLclLng;
	@XmlElement(name = "othersName")
	private String custShrtNameLclLng;

	private String custDftBranch;

	private String lovDescCustDftBranchName;

	@XmlElement(name = "gender")
	private String custGenderCode;
	private String lovDescCustGenderCodeName;

	@XmlElement(name = "dateofBirth")
	private Date custDOB;
	private String custPOB;

	@XmlElement(name = "countryofBirth")
	private String custCOB;
	private String lovDescCustCOBName;
	private String custPassportNo;

	@XmlElement(name = "fatherName")
	private String custMotherMaiden;
	private boolean custIsMinor;
	private String custReferedBy;

	@XmlElement(name = "saleAgent")
	private String custDSA;

	@XmlElement(name = "saleAgentDept")
	private String custDSADept;
	private String lovDescCustDSADeptName;
	private long custRO1;
	private String lovDescCustRO1Name;
	private String lovDescCustRO1City;
	private String custRO2;
	private String lovDescCustRO2Name;

	@XmlElement(name = "groupID")
	private long custGroupID;
	private String lovDescCustGroupCode;
	private String lovDesccustGroupIDName;
	private String custTradeLicenceNum;
	private Date custTradeLicenceExpiry;
	private Date custPassportExpiry;
	private String custVisaNum;
	private Date custVisaExpiry;
	private String custSts;
	private String lovDescCustStsName;
	private Date custStsChgDate;
	private String custGroupSts;
	private String lovDescCustGroupStsName;
	private boolean custIsBlocked;
	private boolean custIsActive;
	private boolean custIsClosed;
	private String custInactiveReason;
	private boolean custIsDecease;
	private boolean custIsDormant;
	private boolean custIsDelinquent;
	private boolean custIsTradeFinCust;

	@XmlElement(name = "staff")
	private boolean custIsStaff;

	@XmlElement(name = "staffID")
	private String custStaffID;

	@XmlElement(name = "industry")
	private String custIndustry;
	private String lovDescCustIndustryName;

	@XmlElement(name = "sector")
	private String custSector;
	private String lovDescCustSectorName;

	@XmlElement(name = "subSector")
	private String custSubSector;
	private String lovDescCustSubSectorName;
	@XmlElement
	private String custProfession;
	private String lovDescCustProfessionName;
	private BigDecimal custTotalIncome = BigDecimal.ZERO;
	private BigDecimal custTotalExpense = BigDecimal.ZERO;

	@XmlElement(name = "maritalStatus")
	private String custMaritalSts;
	private String lovDescCustMaritalStsName;

	@XmlElement(name = "employmentStatus")
	private String custEmpSts;
	private String lovDescCustEmpStsName;

	@XmlElement(name = "segment")
	private String custSegment;
	private String lovDescCustSegmentName;

	@XmlElement(name = "subsegment")
	private String custSubSegment;
	private String lovDescCustSubSegmentName;
	private boolean custIsBlackListed;
	private Date custBlackListDate;

	@XmlElement(name = "numofDependents")
	private int noOfDependents;
	private String custBLRsnCode;
	private String lovDescCustBLRsnCodeName;
	private boolean custIsRejected;
	private String custRejectedRsn;
	private String lovDescCustRejectedRsnName;

	private String custBaseCcy;

	@XmlElement(name = "language")
	private String custLng;
	private String lovDescCustLngName;

	@XmlElement(name = "parentCountry")
	private String custParentCountry;
	private String lovDescCustParentCountryName;
	private String custResdCountry;
	private String lovDescCustResdCountryName;

	@XmlElement(name = "riskCountry")
	private String custRiskCountry;
	private String lovDescCustRiskCountryName;

	@XmlElement(name = "nationality")
	private String custNationality;
	private String lovDescCustNationalityName;
	private Date custClosedOn;
	private String custStmtFrq;
	private boolean custIsStmtCombined;
	private Timestamp custStmtLastDate;
	private Timestamp custStmtNextDate;
	private String custStmtDispatchMode;
	private String lovDescDispatchModeDescName;
	private Date custFirstBusinessDate;
	private String custAddlVar81;
	private String custAddlVar82;
	private String lovDescTargetName;
	private String custAddlVar83; // custRelatedParty
	private String custAddlVar84;
	private String custAddlVar85;
	private String custAddlVar86;
	private String custAddlVar87;
	private String custAddlVar88;
	private String custAddlVar89;
	private Date custAddlDate1;
	private Date custAddlDate2;
	private Date custAddlDate3;
	private Date custAddlDate4;
	private Date custAddlDate5;
	private String custAddlVar1;
	private String custAddlVar2;
	private String custAddlVar3;
	private String custAddlVar4;
	private String custAddlVar5;
	private String custAddlVar6;
	private String custAddlVar7;
	private String custAddlVar8;
	private String custAddlVar9;
	private String custAddlVar10;
	private String custAddlVar11;
	private String contactPersonName;
	private String emailID;
	@XmlElement
	private String phoneNumber;
	private String phoneAreaCode;
	private String phoneCountryCode;
	@XmlElement(name = "emiCardEligibilityAmt")
	private BigDecimal custAddlDec1 = BigDecimal.ZERO;
	private double custAddlDec2;
	private double custAddlDec3;
	private double custAddlDec4;
	private double custAddlDec5;
	private int custAddlInt1;
	private int custAddlInt2;
	private int custAddlInt3;
	private int custAddlInt4;
	private int custAddlInt5;
	private boolean salariedCustomer;
	private String lovValue;
	private Customer befImage;
	private LoggedInUser userDetails;
	private CustomerQDE customerQDE;
	private boolean proceedToDedup = false;
	private boolean dedupFound = false;
	private boolean skipDedup = false;
	@XmlElement(name = "custCRCPR")
	private String custCRCPR;
	private boolean jointCust;
	private String jointCustName;
	private Date jointCustDob;
	private String custRelation;
	private boolean custSuspSts;
	private Date custSuspDate;
	private String custSuspTrigger;
	private String custSuspRemarks;
	private Timestamp custSuspEffDate;
	private Timestamp custSuspAprDate;
	private String ruleCode;
	private String custSuspMvtType;
	private String custSwiftBrnCode;
	private Date custAppDate;
	private String sourceSystem;
	private String branchRefno;
	private String lovDescRequestStage;
	private String custSourceID = null;

	@XmlElement
	private String subCategory;
	@XmlElement(name = "caste")
	private long casteId;
	@XmlElement(name = "religion")
	private long religionId;
	private String casteCode;
	private String religionCode;
	private String casteDesc;
	private String religionDesc;
	private String aadhaarNo;
	private boolean includeIncome;

	// API validation purpose only
	private Customer customer = this;
	@XmlElement
	private WSReturnStatus returnStatus;
	@XmlElement
	private boolean marginDeviation = false;
	private BigDecimal customerAge;
	@XmlElement
	private String applicationNo;
	private String branchProvince;
	private boolean dnd;
	private boolean vip;

	private String legalconstitution;
	private String businesscategory;
	private String custAddrProvince;
	private String customerFullName;
	private String custOffAddress;
	private String custResiAddress;

	private String otherReligion;
	private String otherCaste;
	private String ckycOrRefNo;
	private String natureOfBusiness;
	private String lovDescNatureOfBusiness;
	private String entityType;
	private String lovDescEntityType;
	@XmlElement(name = "residentialSts")
	private String custResidentialSts;
	private String lovDescCustResidentialSts;
	private String qualification;
	private String lovDescQualification;
	private String custFlag;
	private String lovDescCustFlag;
	private String primaryIdName;
	private BigDecimal industryMargin;
	private String subIndustry;
	private String residentialStatus;
	private String custAddrCity;
	private String custAddrCountry;
	private String lovDescCustAddrCountry;
	private String lovDescCustAddrCity;
	private String lovDescCustAddrProvince;
	private boolean prospectAsCIF = false;
	private String loanName;
	@XmlElement
	private BigDecimal loanInstalmentAmount;
	private String applicantType;
	private String relationWithCust;
	@XmlElement
	private String product;

	public Customer() {
		super();
	}

	public Customer(long id) {
		super();
		this.setId(id);
	}

	public Customer(long id, String custCoreBank) {
		super();
		this.setId(id);
		this.setCustCoreBank(custCoreBank);
	}

	public Set<String> getExcludeFields() {
		Set<String> excludeFields = new HashSet<String>();
		excludeFields.add("branchProvince");
		excludeFields.add("proceedToDedup");
		excludeFields.add("dedupFound");
		excludeFields.add("skipDedup");
		excludeFields.add("phoneAreaCode");
		excludeFields.add("phoneCountryCode");
		excludeFields.add("custSuspSts");
		excludeFields.add("custSuspDate");
		excludeFields.add("custSuspTrigger");
		excludeFields.add("custSuspRemarks");
		excludeFields.add("custSuspEffDate");
		excludeFields.add("custSuspAprDate");
		excludeFields.add("custSuspMvtType");
		excludeFields.add("ruleCode");
		excludeFields.add("customer");
		excludeFields.add("returnStatus");
		excludeFields.add("custSwiftBrnCode");
		excludeFields.add("sourceSystem");
		excludeFields.add("branchRefno");
		excludeFields.add("casteCode");
		excludeFields.add("religionCode");
		excludeFields.add("casteDesc");
		excludeFields.add("religionDesc");
		excludeFields.add("aadhaarNo");
		excludeFields.add("customerAge");
		excludeFields.add("includeIncome");
		excludeFields.add("legalconstitution");
		excludeFields.add("businesscategory");
		excludeFields.add("custAddrProvince");
		excludeFields.add("extendedFields");
		excludeFields.add("custOffAddress");
		excludeFields.add("custResiAddress");
		excludeFields.add("customerFullName");

		excludeFields.add("ckycOrRefNo");
		excludeFields.add("lovDescNatureOfBusiness");
		excludeFields.add("lovDescEntityType");
		excludeFields.add("lovDescCustResidentialSts");
		excludeFields.add("lovDescCustFlag");

		// This needs to be removed in HL MLOD
		excludeFields.add("otherReligion");
		excludeFields.add("otherCaste");
		excludeFields.add("ckycOrRefNo");
		excludeFields.add("natureOfBusiness");
		excludeFields.add("lovDescNatureOfBusiness");
		excludeFields.add("entityType");
		excludeFields.add("lovDescEntityType");
		excludeFields.add("custResidentialSts");
		excludeFields.add("lovDescCustResidentialSts");
		excludeFields.add("qualification");
		excludeFields.add("lovDescQualification");
		excludeFields.add("custFlag");
		excludeFields.add("lovDescCustFlag");
		excludeFields.add("primaryIdName");
		excludeFields.add("industryMargin");
		excludeFields.add("subIndustry");
		excludeFields.add("custAddrCountry");
		excludeFields.add("custAddrCity");
		excludeFields.add("lovDescCustAddrCountry");
		excludeFields.add("lovDescCustAddrCity");
		excludeFields.add("lovDescCustAddrProvince");
		excludeFields.add("prospectAsCIF");
		excludeFields.add("loanName");
		excludeFields.add("loanInstalmentAmount");
		excludeFields.add("applicantType");
		excludeFields.add("relationWithCust");
		excludeFields.add("product");

		return excludeFields;
	}

	// ******************************************************//
	// ****************** getter / setter *******************//
	// ******************************************************//

	public long getId() {
		return custID;
	}

	public void setId(long id) {
		this.custID = id;
	}

	public long getCustID() {
		return custID;
	}

	public void setCustID(long custID) {
		this.custID = custID;
	}

	public String getCustCIF() {
		return custCIF;
	}

	public void setCustCIF(String custCIF) {
		this.custCIF = custCIF;
	}

	public String getCustCoreBank() {
		return custCoreBank;
	}

	public void setCustCoreBank(String custCoreBank) {
		this.custCoreBank = custCoreBank;
	}

	public String getCustCtgCode() {
		return custCtgCode;
	}

	public void setCustCtgCode(String custCtgCode) {
		this.custCtgCode = custCtgCode;
	}

	public String getLovDescCustCtgCodeName() {
		return this.lovDescCustCtgCodeName;
	}

	public void setLovDescCustCtgCodeName(String lovDescCustCtgCodeName) {
		this.lovDescCustCtgCodeName = lovDescCustCtgCodeName;
	}

	public void setLovDescCustCtgType(String lovDescCustCtgType) {
		this.lovDescCustCtgType = lovDescCustCtgType;
	}

	public String getLovDescCustCtgType() {
		return lovDescCustCtgType;
	}

	public String getCustTypeCode() {
		return custTypeCode;
	}

	public void setCustTypeCode(String custTypeCode) {
		this.custTypeCode = custTypeCode;
	}

	public String getLovDescCustTypeCodeName() {
		return this.lovDescCustTypeCodeName;
	}

	public void setLovDescCustTypeCodeName(String lovDescCustTypeCodeName) {
		this.lovDescCustTypeCodeName = lovDescCustTypeCodeName;
	}

	public String getCustSalutationCode() {
		return custSalutationCode;
	}

	public void setCustSalutationCode(String custSalutationCode) {
		this.custSalutationCode = custSalutationCode;
	}

	public String getLovDescCustSalutationCodeName() {
		return this.lovDescCustSalutationCodeName;
	}

	public void setLovDescCustSalutationCodeName(String lovDescCustSalutationCodeName) {
		this.lovDescCustSalutationCodeName = lovDescCustSalutationCodeName;
	}

	public String getCustFName() {
		return custFName;
	}

	public void setCustFName(String custFName) {
		this.custFName = custFName;
	}

	public String getCustMName() {
		return custMName;
	}

	public void setCustMName(String custMName) {
		this.custMName = custMName;
	}

	public String getCustLName() {
		return custLName;
	}

	public void setCustLName(String custLName) {
		this.custLName = custLName;
	}

	public String getCustShrtName() {
		return custShrtName;
	}

	public void setCustShrtName(String custShrtName) {
		this.custShrtName = custShrtName;
	}

	public String getCustFNameLclLng() {
		return custFNameLclLng;
	}

	public void setCustFNameLclLng(String custFNameLclLng) {
		this.custFNameLclLng = custFNameLclLng;
	}

	public String getCustMNameLclLng() {
		return custMNameLclLng;
	}

	public void setCustMNameLclLng(String custMNameLclLng) {
		this.custMNameLclLng = custMNameLclLng;
	}

	public String getCustLNameLclLng() {
		return custLNameLclLng;
	}

	public void setCustLNameLclLng(String custLNameLclLng) {
		this.custLNameLclLng = custLNameLclLng;
	}

	public String getCustShrtNameLclLng() {
		return custShrtNameLclLng;
	}

	public void setCustShrtNameLclLng(String custShrtNameLclLng) {
		this.custShrtNameLclLng = custShrtNameLclLng;
	}

	public String getCustDftBranch() {
		return custDftBranch;
	}

	public void setCustDftBranch(String custDftBranch) {
		this.custDftBranch = custDftBranch;
	}

	public String getLovDescCustDftBranchName() {
		return this.lovDescCustDftBranchName;
	}

	public void setLovDescCustDftBranchName(String lovDescCustDftBranchName) {
		this.lovDescCustDftBranchName = lovDescCustDftBranchName;
	}

	public String getCustGenderCode() {
		return custGenderCode;
	}

	public void setCustGenderCode(String custGenderCode) {
		this.custGenderCode = custGenderCode;
	}

	public String getLovDescCustGenderCodeName() {
		return this.lovDescCustGenderCodeName;
	}

	public void setLovDescCustGenderCodeName(String lovDescCustGenderCodeName) {
		this.lovDescCustGenderCodeName = lovDescCustGenderCodeName;
	}

	public Date getCustDOB() {
		return custDOB;
	}

	public void setCustDOB(Date custDOB) {
		this.custDOB = custDOB;
	}

	public String getCustPOB() {
		return custPOB;
	}

	public void setCustPOB(String custPOB) {
		this.custPOB = custPOB;
	}

	public String getCustCOB() {
		return custCOB;
	}

	public void setCustCOB(String custCOB) {
		this.custCOB = custCOB;
	}

	public String getLovDescCustCOBName() {
		return this.lovDescCustCOBName;
	}

	public void setLovDescCustCOBName(String lovDescCustCOBName) {
		this.lovDescCustCOBName = lovDescCustCOBName;
	}

	public String getCustPassportNo() {
		return custPassportNo;
	}

	public void setCustPassportNo(String custPassportNo) {
		this.custPassportNo = custPassportNo;
	}

	public String getCustMotherMaiden() {
		return custMotherMaiden;
	}

	public void setCustMotherMaiden(String custMotherMaiden) {
		this.custMotherMaiden = custMotherMaiden;
	}

	public boolean isCustIsMinor() {
		return custIsMinor;
	}

	public void setCustIsMinor(boolean custIsMinor) {
		this.custIsMinor = custIsMinor;
	}

	public String getCustReferedBy() {
		return custReferedBy;
	}

	public void setCustReferedBy(String custReferedBy) {
		this.custReferedBy = custReferedBy;
	}

	public String getCustDSA() {
		return custDSA;
	}

	public void setCustDSA(String custDSA) {
		this.custDSA = custDSA;
	}

	public String getCustDSADept() {
		return custDSADept;
	}

	public void setCustDSADept(String custDSADept) {
		this.custDSADept = custDSADept;
	}

	public String getLovDescCustDSADeptName() {
		return this.lovDescCustDSADeptName;
	}

	public void setLovDescCustDSADeptName(String lovDescCustDSADeptName) {
		this.lovDescCustDSADeptName = lovDescCustDSADeptName;
	}

	public long getCustRO1() {
		return custRO1;
	}

	public void setCustRO1(long custRO1) {
		this.custRO1 = custRO1;
	}

	public String getLovDescCustRO1Name() {
		return this.lovDescCustRO1Name;
	}

	public void setLovDescCustRO1Name(String lovDescCustRO1Name) {
		this.lovDescCustRO1Name = lovDescCustRO1Name;
	}

	public String getCustRO2() {
		return custRO2;
	}

	public void setCustRO2(String custRO2) {
		this.custRO2 = custRO2;
	}

	public String getLovDescCustRO2Name() {
		return this.lovDescCustRO2Name;
	}

	public void setLovDescCustRO2Name(String lovDescCustRO2Name) {
		this.lovDescCustRO2Name = lovDescCustRO2Name;
	}

	public long getCustGroupID() {
		return custGroupID;
	}

	public void setCustGroupID(long custGroupID) {
		this.custGroupID = custGroupID;
	}

	public String getCustSts() {
		return custSts;
	}

	public void setCustSts(String custSts) {
		this.custSts = custSts;
	}

	public String getLovDescCustStsName() {
		return this.lovDescCustStsName;
	}

	public void setLovDescCustStsName(String lovDescCustStsName) {
		this.lovDescCustStsName = lovDescCustStsName;
	}

	public Date getCustStsChgDate() {
		return custStsChgDate;
	}

	public void setCustStsChgDate(Date custStsChgDate) {
		this.custStsChgDate = custStsChgDate;
	}

	public String getCustGroupSts() {
		return custGroupSts;
	}

	public void setCustGroupSts(String custGroupSts) {
		this.custGroupSts = custGroupSts;
	}

	public String getLovDescCustGroupStsName() {
		return this.lovDescCustGroupStsName;
	}

	public void setLovDescCustGroupStsName(String lovDescCustGroupStsName) {
		this.lovDescCustGroupStsName = lovDescCustGroupStsName;
	}

	public boolean isCustIsBlocked() {
		return custIsBlocked;
	}

	public void setCustIsBlocked(boolean custIsBlocked) {
		this.custIsBlocked = custIsBlocked;
	}

	public boolean isCustIsActive() {
		return custIsActive;
	}

	public void setCustIsActive(boolean custIsActive) {
		this.custIsActive = custIsActive;
	}

	public boolean isCustIsClosed() {
		return custIsClosed;
	}

	public void setCustIsClosed(boolean custIsClosed) {
		this.custIsClosed = custIsClosed;
	}

	public String getCustInactiveReason() {
		return custInactiveReason;
	}

	public void setCustInactiveReason(String custInactiveReason) {
		this.custInactiveReason = custInactiveReason;
	}

	public boolean isCustIsDecease() {
		return custIsDecease;
	}

	public void setCustIsDecease(boolean custIsDecease) {
		this.custIsDecease = custIsDecease;
	}

	public boolean isCustIsDormant() {
		return custIsDormant;
	}

	public void setCustIsDormant(boolean custIsDormant) {
		this.custIsDormant = custIsDormant;
	}

	public boolean isCustIsDelinquent() {
		return custIsDelinquent;
	}

	public void setCustIsDelinquent(boolean custIsDelinquent) {
		this.custIsDelinquent = custIsDelinquent;
	}

	public boolean isCustIsTradeFinCust() {
		return custIsTradeFinCust;
	}

	public void setCustIsTradeFinCust(boolean custIsTradeFinCust) {
		this.custIsTradeFinCust = custIsTradeFinCust;
	}

	public boolean isCustIsStaff() {
		return custIsStaff;
	}

	public void setCustIsStaff(boolean custIsStaff) {
		this.custIsStaff = custIsStaff;
	}

	public String getCustStaffID() {
		return custStaffID;
	}

	public void setCustStaffID(String custStaffID) {
		this.custStaffID = custStaffID;
	}

	public String getCustIndustry() {
		return custIndustry;
	}

	public void setCustIndustry(String custIndustry) {
		this.custIndustry = custIndustry;
	}

	public String getLovDescCustIndustryName() {
		return this.lovDescCustIndustryName;
	}

	public void setLovDescCustIndustryName(String lovDescCustIndustryName) {
		this.lovDescCustIndustryName = lovDescCustIndustryName;
	}

	public String getCustSector() {
		return custSector;
	}

	public void setCustSector(String custSector) {
		this.custSector = custSector;
	}

	public String getLovDescCustSectorName() {
		return this.lovDescCustSectorName;
	}

	public void setLovDescCustSectorName(String lovDescCustSectorName) {
		this.lovDescCustSectorName = lovDescCustSectorName;
	}

	public String getCustSubSector() {
		return custSubSector;
	}

	public void setCustSubSector(String custSubSector) {
		this.custSubSector = custSubSector;
	}

	public String getLovDescCustSubSectorName() {
		return this.lovDescCustSubSectorName;
	}

	public void setLovDescCustSubSectorName(String lovDescCustSubSectorName) {
		this.lovDescCustSubSectorName = lovDescCustSubSectorName;
	}

	public String getCustProfession() {
		return custProfession;
	}

	public void setCustProfession(String custProfession) {
		this.custProfession = custProfession;
	}

	public String getLovDescCustProfessionName() {
		return this.lovDescCustProfessionName;
	}

	public void setLovDescCustProfessionName(String lovDescCustProfessionName) {
		this.lovDescCustProfessionName = lovDescCustProfessionName;
	}

	public BigDecimal getCustTotalIncome() {
		return custTotalIncome;
	}

	public void setCustTotalIncome(BigDecimal custTotalIncome) {
		this.custTotalIncome = custTotalIncome;
	}

	public String getCustMaritalSts() {
		return custMaritalSts;
	}

	public void setCustMaritalSts(String custMaritalSts) {
		this.custMaritalSts = custMaritalSts;
	}

	public String getLovDescCustMaritalStsName() {
		return this.lovDescCustMaritalStsName;
	}

	public void setLovDescCustMaritalStsName(String lovDescCustMaritalStsName) {
		this.lovDescCustMaritalStsName = lovDescCustMaritalStsName;
	}

	public String getCustEmpSts() {
		return custEmpSts;
	}

	public void setCustEmpSts(String custEmpSts) {
		this.custEmpSts = custEmpSts;
	}

	public String getLovDescCustEmpStsName() {
		return this.lovDescCustEmpStsName;
	}

	public void setLovDescCustEmpStsName(String lovDescCustEmpStsName) {
		this.lovDescCustEmpStsName = lovDescCustEmpStsName;
	}

	public String getCustSegment() {
		return custSegment;
	}

	public void setCustSegment(String custSegment) {
		this.custSegment = custSegment;
	}

	public String getLovDescCustSegmentName() {
		return this.lovDescCustSegmentName;
	}

	public void setLovDescCustSegmentName(String lovDescCustSegmentName) {
		this.lovDescCustSegmentName = lovDescCustSegmentName;
	}

	public String getCustSubSegment() {
		return custSubSegment;
	}

	public void setCustSubSegment(String custSubSegment) {
		this.custSubSegment = custSubSegment;
	}

	public boolean isCustIsBlackListed() {
		return custIsBlackListed;
	}

	public void setCustIsBlackListed(boolean custIsBlackListed) {
		this.custIsBlackListed = custIsBlackListed;
	}

	public String getCustBLRsnCode() {
		return custBLRsnCode;
	}

	public void setCustBLRsnCode(String custBLRsnCode) {
		this.custBLRsnCode = custBLRsnCode;
	}

	public boolean isCustIsRejected() {
		return custIsRejected;
	}

	public void setCustIsRejected(boolean custIsRejected) {
		this.custIsRejected = custIsRejected;
	}

	public String getCustRejectedRsn() {
		return custRejectedRsn;
	}

	public void setCustRejectedRsn(String custRejectedRsn) {
		this.custRejectedRsn = custRejectedRsn;
	}

	public String getCustBaseCcy() {
		return custBaseCcy;
	}

	public void setCustBaseCcy(String custBaseCcy) {
		this.custBaseCcy = custBaseCcy;
	}

	public String getCustLng() {
		return custLng;
	}

	public void setCustLng(String custLng) {
		this.custLng = custLng;
	}

	public String getLovDescCustLngName() {
		return lovDescCustLngName;
	}

	public void setLovDescCustLngName(String lovDescCustLngName) {
		this.lovDescCustLngName = lovDescCustLngName;
	}

	public String getCustParentCountry() {
		return custParentCountry;
	}

	public void setCustParentCountry(String custParentCountry) {
		this.custParentCountry = custParentCountry;
	}

	public String getLovDescCustParentCountryName() {
		return this.lovDescCustParentCountryName;
	}

	public void setLovDescCustParentCountryName(String lovDescCustParentCountryName) {
		this.lovDescCustParentCountryName = lovDescCustParentCountryName;
	}

	public String getCustResdCountry() {
		return custResdCountry;
	}

	public void setCustResdCountry(String custResdCountry) {
		this.custResdCountry = custResdCountry;
	}

	public String getLovDescCustResdCountryName() {
		return this.lovDescCustResdCountryName;
	}

	public void setLovDescCustResdCountryName(String lovDescCustResdCountryName) {
		this.lovDescCustResdCountryName = lovDescCustResdCountryName;
	}

	public String getCustRiskCountry() {
		return custRiskCountry;
	}

	public void setCustRiskCountry(String custRiskCountry) {
		this.custRiskCountry = custRiskCountry;
	}

	public String getLovDescCustRiskCountryName() {
		return this.lovDescCustRiskCountryName;
	}

	public void setLovDescCustRiskCountryName(String lovDescCustRiskCountryName) {
		this.lovDescCustRiskCountryName = lovDescCustRiskCountryName;
	}

	public String getCustNationality() {
		return custNationality;
	}

	public void setCustNationality(String custNationality) {
		this.custNationality = custNationality;
	}

	public String getLovDescCustNationalityName() {
		return this.lovDescCustNationalityName;
	}

	public void setLovDescCustNationalityName(String lovDescCustNationalityName) {
		this.lovDescCustNationalityName = lovDescCustNationalityName;
	}

	public Date getCustClosedOn() {
		return custClosedOn;
	}

	public void setCustClosedOn(Date custClosedOn) {
		this.custClosedOn = custClosedOn;
	}

	public String getCustStmtFrq() {
		return custStmtFrq;
	}

	public void setCustStmtFrq(String custStmtFrq) {
		this.custStmtFrq = custStmtFrq;
	}

	public boolean isCustIsStmtCombined() {
		return custIsStmtCombined;
	}

	public void setCustIsStmtCombined(boolean custIsStmtCombined) {
		this.custIsStmtCombined = custIsStmtCombined;
	}

	public Timestamp getCustStmtLastDate() {
		return custStmtLastDate;
	}

	public void setCustStmtLastDate(Timestamp custStmtLastDate) {
		this.custStmtLastDate = custStmtLastDate;
	}

	public Timestamp getCustStmtNextDate() {
		return custStmtNextDate;
	}

	public void setCustStmtNextDate(Timestamp custStmtNextDate) {
		this.custStmtNextDate = custStmtNextDate;
	}

	public String getCustStmtDispatchMode() {
		return custStmtDispatchMode;
	}

	public void setCustStmtDispatchMode(String custStmtDispatchMode) {
		this.custStmtDispatchMode = custStmtDispatchMode;
	}

	public Date getCustFirstBusinessDate() {
		return custFirstBusinessDate;
	}

	public void setCustFirstBusinessDate(Date custFirstBusinessDate) {
		this.custFirstBusinessDate = custFirstBusinessDate;
	}

	public String getCustAddlVar81() {
		return custAddlVar81;
	}

	public void setCustAddlVar81(String custAddlVar81) {
		this.custAddlVar81 = custAddlVar81;
	}

	public String getCustAddlVar82() {
		return custAddlVar82;
	}

	public void setCustAddlVar82(String custAddlVar82) {
		this.custAddlVar82 = custAddlVar82;
	}

	public String getLovDescTargetName() {
		return lovDescTargetName;
	}

	public void setLovDescTargetName(String lovDescTargetName) {
		this.lovDescTargetName = lovDescTargetName;
	}

	public String getCustAddlVar83() {
		return custAddlVar83;
	}

	public void setCustAddlVar83(String custAddlVar83) {
		this.custAddlVar83 = custAddlVar83;
	}

	public String getCustAddlVar84() {
		return custAddlVar84;
	}

	public void setCustAddlVar84(String custAddlVar84) {
		this.custAddlVar84 = custAddlVar84;
	}

	public String getCustAddlVar85() {
		return custAddlVar85;
	}

	public void setCustAddlVar85(String custAddlVar85) {
		this.custAddlVar85 = custAddlVar85;
	}

	public String getCustAddlVar86() {
		return custAddlVar86;
	}

	public void setCustAddlVar86(String custAddlVar86) {
		this.custAddlVar86 = custAddlVar86;
	}

	public String getCustAddlVar87() {
		return custAddlVar87;
	}

	public void setCustAddlVar87(String custAddlVar87) {
		this.custAddlVar87 = custAddlVar87;
	}

	public String getCustAddlVar88() {
		return custAddlVar88;
	}

	public void setCustAddlVar88(String custAddlVar88) {
		this.custAddlVar88 = custAddlVar88;
	}

	public String getCustAddlVar89() {
		return custAddlVar89;
	}

	public void setCustAddlVar89(String custAddlVar89) {
		this.custAddlVar89 = custAddlVar89;
	}

	public Date getCustAddlDate1() {
		return custAddlDate1;
	}

	public void setCustAddlDate1(Date custAddlDate1) {
		this.custAddlDate1 = custAddlDate1;
	}

	public Date getCustAddlDate2() {
		return custAddlDate2;
	}

	public void setCustAddlDate2(Date custAddlDate2) {
		this.custAddlDate2 = custAddlDate2;
	}

	public Date getCustAddlDate3() {
		return custAddlDate3;
	}

	public void setCustAddlDate3(Date custAddlDate3) {
		this.custAddlDate3 = custAddlDate3;
	}

	public Date getCustAddlDate4() {
		return custAddlDate4;
	}

	public void setCustAddlDate4(Date custAddlDate4) {
		this.custAddlDate4 = custAddlDate4;
	}

	public Date getCustAddlDate5() {
		return custAddlDate5;
	}

	public void setCustAddlDate5(Date custAddlDate5) {
		this.custAddlDate5 = custAddlDate5;
	}

	public String getCustAddlVar1() {
		return custAddlVar1;
	}

	public void setCustAddlVar1(String custAddlVar1) {
		this.custAddlVar1 = custAddlVar1;
	}

	public String getCustAddlVar2() {
		return custAddlVar2;
	}

	public void setCustAddlVar2(String custAddlVar2) {
		this.custAddlVar2 = custAddlVar2;
	}

	public String getCustAddlVar3() {
		return custAddlVar3;
	}

	public void setCustAddlVar3(String custAddlVar3) {
		this.custAddlVar3 = custAddlVar3;
	}

	public String getCustAddlVar4() {
		return custAddlVar4;
	}

	public void setCustAddlVar4(String custAddlVar4) {
		this.custAddlVar4 = custAddlVar4;
	}

	public String getCustAddlVar5() {
		return custAddlVar5;
	}

	public void setCustAddlVar5(String custAddlVar5) {
		this.custAddlVar5 = custAddlVar5;
	}

	public String getCustAddlVar6() {
		return custAddlVar6;
	}

	public void setCustAddlVar6(String custAddlVar6) {
		this.custAddlVar6 = custAddlVar6;
	}

	public String getCustAddlVar7() {
		return custAddlVar7;
	}

	public void setCustAddlVar7(String custAddlVar7) {
		this.custAddlVar7 = custAddlVar7;
	}

	public String getCustAddlVar8() {
		return custAddlVar8;
	}

	public void setCustAddlVar8(String custAddlVar8) {
		this.custAddlVar8 = custAddlVar8;
	}

	public String getCustAddlVar9() {
		return custAddlVar9;
	}

	public void setCustAddlVar9(String custAddlVar9) {
		this.custAddlVar9 = custAddlVar9;
	}

	public String getCustAddlVar10() {
		return custAddlVar10;
	}

	public void setCustAddlVar10(String custAddlVar10) {
		this.custAddlVar10 = custAddlVar10;
	}

	public String getCustAddlVar11() {
		return custAddlVar11;
	}

	public void setCustAddlVar11(String custAddlVar11) {
		this.custAddlVar11 = custAddlVar11;
	}

	public BigDecimal getCustAddlDec1() {
		return custAddlDec1;
	}

	public void setCustAddlDec1(BigDecimal custAddlDec1) {
		this.custAddlDec1 = custAddlDec1;
	}

	public double getCustAddlDec2() {
		return custAddlDec2;
	}

	public void setCustAddlDec2(double custAddlDec2) {
		this.custAddlDec2 = custAddlDec2;
	}

	public double getCustAddlDec3() {
		return custAddlDec3;
	}

	public void setCustAddlDec3(double custAddlDec3) {
		this.custAddlDec3 = custAddlDec3;
	}

	public double getCustAddlDec4() {
		return custAddlDec4;
	}

	public void setCustAddlDec4(double custAddlDec4) {
		this.custAddlDec4 = custAddlDec4;
	}

	public double getCustAddlDec5() {
		return custAddlDec5;
	}

	public void setCustAddlDec5(double custAddlDec5) {
		this.custAddlDec5 = custAddlDec5;
	}

	public int getCustAddlInt1() {
		return custAddlInt1;
	}

	public void setCustAddlInt1(int custAddlInt1) {
		this.custAddlInt1 = custAddlInt1;
	}

	public int getCustAddlInt2() {
		return custAddlInt2;
	}

	public void setCustAddlInt2(int custAddlInt2) {
		this.custAddlInt2 = custAddlInt2;
	}

	public int getCustAddlInt3() {
		return custAddlInt3;
	}

	public void setCustAddlInt3(int custAddlInt3) {
		this.custAddlInt3 = custAddlInt3;
	}

	public int getCustAddlInt4() {
		return custAddlInt4;
	}

	public void setCustAddlInt4(int custAddlInt4) {
		this.custAddlInt4 = custAddlInt4;
	}

	public int getCustAddlInt5() {
		return custAddlInt5;
	}

	public void setCustAddlInt5(int custAddlInt5) {
		this.custAddlInt5 = custAddlInt5;
	}

	public String getLovValue() {
		return lovValue;
	}

	public void setLovValue(String lovValue) {
		this.lovValue = lovValue;
	}

	public Customer getBefImage() {
		return this.befImage;
	}

	public void setBefImage(Customer beforeImage) {
		this.befImage = beforeImage;
	}

	public LoggedInUser getUserDetails() {
		return userDetails;
	}

	public void setUserDetails(LoggedInUser userDetails) {
		this.userDetails = userDetails;
	}

	public void setLovDescCustBLRsnCodeName(String lovDescCustBLRsnCodeName) {
		this.lovDescCustBLRsnCodeName = lovDescCustBLRsnCodeName;
	}

	public String getLovDescCustBLRsnCodeName() {
		return lovDescCustBLRsnCodeName;
	}

	public void setLovDescCustRejectedRsnName(String lovDescCustRejectedRsnName) {
		this.lovDescCustRejectedRsnName = lovDescCustRejectedRsnName;
	}

	public String getLovDescCustRejectedRsnName() {
		return lovDescCustRejectedRsnName;
	}

	public void setLovDesccustGroupIDName(String lovDesccustGroupIDName) {
		this.lovDesccustGroupIDName = lovDesccustGroupIDName;
	}

	public String getLovDesccustGroupIDName() {
		return lovDesccustGroupIDName;
	}

	public void setLovDescCustSubSegmentName(String lovDescCustSubSegmentName) {
		this.lovDescCustSubSegmentName = lovDescCustSubSegmentName;
	}

	public String getLovDescCustSubSegmentName() {
		return lovDescCustSubSegmentName;
	}

	public String getCustTradeLicenceNum() {
		return custTradeLicenceNum;
	}

	public void setCustTradeLicenceNum(String custTradeLicenceNum) {
		this.custTradeLicenceNum = custTradeLicenceNum;
	}

	public Date getCustTradeLicenceExpiry() {
		return custTradeLicenceExpiry;
	}

	public void setCustTradeLicenceExpiry(Date custTradeLicenceExpiry) {
		this.custTradeLicenceExpiry = custTradeLicenceExpiry;
	}

	public Date getCustPassportExpiry() {
		return custPassportExpiry;
	}

	public void setCustPassportExpiry(Date custPassportExpiry) {
		this.custPassportExpiry = custPassportExpiry;
	}

	public String getCustVisaNum() {
		return custVisaNum;
	}

	public void setCustVisaNum(String custVisaNum) {
		this.custVisaNum = custVisaNum;
	}

	public Date getCustVisaExpiry() {
		return custVisaExpiry;
	}

	public void setCustVisaExpiry(Date custVisaExpiry) {
		this.custVisaExpiry = custVisaExpiry;
	}

	public CustomerQDE getCustomerQDE() {
		if (customerQDE == null) {
			customerQDE = new CustomerQDE();
		}
		return customerQDE;
	}

	public void setLovDescDispatchModeDescName(String lovDescDispatchModeDescName) {
		this.lovDescDispatchModeDescName = lovDescDispatchModeDescName;
	}

	public String getLovDescDispatchModeDescName() {
		return lovDescDispatchModeDescName;
	}

	public void setCustomerQDE(CustomerQDE customerQDE) {
		this.customerQDE = customerQDE;
		if (customerQDE != null) {
			this.custCIF = customerQDE.getCustCIF();
			this.custCoreBank = customerQDE.getCustCoreBank();
			this.custCtgCode = customerQDE.getCustCtgCode();
			this.lovDescCustCtgCodeName = customerQDE.getLovDescCustCtgCodeName();
			this.custTypeCode = customerQDE.getCustTypeCode();
			this.lovDescCustTypeCodeName = customerQDE.getLovDescCustTypeCodeName();
			this.custSalutationCode = customerQDE.getCustSalutationCode();
			this.lovDescCustSalutationCodeName = customerQDE.getLovDescCustSalutationCodeName();
			this.custFName = customerQDE.getCustFName();
			this.custMName = customerQDE.getCustMName();
			this.custLName = customerQDE.getCustLName();
			this.custShrtName = customerQDE.getCustShrtName();
			this.custDOB = customerQDE.getCustDOB();
			this.custPassportNo = customerQDE.getCustPassportNo();
			this.custTradeLicenceNum = customerQDE.getCustTradeLicenceNum();
			this.custVisaNum = customerQDE.getCustVisaNum();
		}
	}

	public boolean isProceedToDedup() {
		return proceedToDedup;
	}

	public void setProceedToDedup(boolean proceedToDedup) {
		this.proceedToDedup = proceedToDedup;
	}

	public boolean isDedupFound() {
		return dedupFound;
	}

	public void setDedupFound(boolean dedupFound) {
		this.dedupFound = dedupFound;
	}

	public boolean isSkipDedup() {
		return skipDedup;
	}

	public void setSkipDedup(boolean skipDedup) {
		this.skipDedup = skipDedup;
	}

	public void setCustCRCPR(String custCRCPR) {
		this.custCRCPR = custCRCPR;
	}

	public String getCustCRCPR() {
		return custCRCPR;
	}

	public void setCustTotalExpense(BigDecimal custTotalExpense) {
		this.custTotalExpense = custTotalExpense;
	}

	public BigDecimal getCustTotalExpense() {
		return custTotalExpense;
	}

	public void setCustBlackListDate(Date custBlackListDate) {
		this.custBlackListDate = custBlackListDate;
	}

	public Date getCustBlackListDate() {
		return custBlackListDate;
	}

	public void setNoOfDependents(int noOfDependents) {
		this.noOfDependents = noOfDependents;
	}

	public int getNoOfDependents() {
		return noOfDependents;
	}

	public boolean isJointCust() {
		return jointCust;
	}

	public void setJointCust(boolean jointCust) {
		this.jointCust = jointCust;
	}

	public void setJointCustName(String jointCustName) {
		this.jointCustName = jointCustName;
	}

	public String getJointCustName() {
		return jointCustName;
	}

	public void setJointCustDob(Date jointCustDob) {
		this.jointCustDob = jointCustDob;
	}

	public Date getJointCustDob() {
		return jointCustDob;
	}

	public void setLovDescCustGroupCode(String lovDesccustGroupCode) {
		this.lovDescCustGroupCode = lovDesccustGroupCode;
	}

	public String getLovDescCustGroupCode() {
		return lovDescCustGroupCode;
	}

	public String getCustRelation() {
		return custRelation;
	}

	public void setCustRelation(String custRelation) {
		this.custRelation = custRelation;
	}

	public String getContactPersonName() {
		return contactPersonName;
	}

	public void setContactPersonName(String contactPersonName) {
		this.contactPersonName = contactPersonName;
	}

	public String getEmailID() {
		return emailID;
	}

	public void setEmailID(String emailID) {
		this.emailID = emailID;
	}

	public String getPhoneNumber() {
		return phoneNumber;
	}

	public void setPhoneNumber(String phoneNumber) {
		this.phoneNumber = phoneNumber;
	}

	public boolean isSalariedCustomer() {
		return salariedCustomer;
	}

	public void setSalariedCustomer(boolean salariedCustomer) {
		this.salariedCustomer = salariedCustomer;
	}

	public String getPhoneAreaCode() {
		return phoneAreaCode;
	}

	public void setPhoneAreaCode(String phoneAreaCode) {
		this.phoneAreaCode = phoneAreaCode;
	}

	public String getPhoneCountryCode() {
		return phoneCountryCode;
	}

	public void setPhoneCountryCode(String phoneCountryCode) {
		this.phoneCountryCode = phoneCountryCode;
	}

	public boolean isCustSuspSts() {
		return custSuspSts;
	}

	public void setCustSuspSts(boolean custSuspSts) {
		this.custSuspSts = custSuspSts;
	}

	public Date getCustSuspDate() {
		return custSuspDate;
	}

	public void setCustSuspDate(Date custSuspDate) {
		this.custSuspDate = custSuspDate;
	}

	public String getCustSuspTrigger() {
		return custSuspTrigger;
	}

	public void setCustSuspTrigger(String custSuspTrigger) {
		this.custSuspTrigger = custSuspTrigger;
	}

	public String getCustSuspRemarks() {
		return custSuspRemarks;
	}

	public void setCustSuspRemarks(String custSuspRemarks) {
		this.custSuspRemarks = custSuspRemarks;
	}

	public String getCustSuspMvtType() {
		return custSuspMvtType;
	}

	public void setCustSuspMvtType(String custSuspMvtType) {
		this.custSuspMvtType = custSuspMvtType;
	}

	public Timestamp getCustSuspEffDate() {
		return custSuspEffDate;
	}

	public void setCustSuspEffDate(Timestamp custSuspEffDate) {
		this.custSuspEffDate = custSuspEffDate;
	}

	public Timestamp getCustSuspAprDate() {
		return custSuspAprDate;
	}

	public void setCustSuspAprDate(Timestamp custSuspAprDate) {
		this.custSuspAprDate = custSuspAprDate;
	}

	public boolean isProspectCustomer() {
		return StringUtils.isBlank(getCustCoreBank());
	}

	public Map<String, Object> getDeclaredFieldValues() {
		Map<String, Object> customerMap = new HashMap<>();

		return getDeclaredFieldValues(customerMap);
	}

	public Map<String, Object> getDeclaredFieldValues(Map<String, Object> dataMap) {
		Field[] declaredFields = this.getClass().getDeclaredFields();
		for (Field field : declaredFields) {
			try {
				dataMap.put("ct_" + field.getName(), field.get(this));
			} catch (SecurityException | IllegalArgumentException | IllegalAccessException e) {
				//
			}
		}

		return dataMap;
	}

	public String getRuleCode() {
		return ruleCode;
	}

	public void setRuleCode(String ruleCode) {
		this.ruleCode = ruleCode;
	}

	public WSReturnStatus getReturnStatus() {
		return returnStatus;
	}

	public void setReturnStatus(WSReturnStatus returnStatus) {
		this.returnStatus = returnStatus;
	}

	public String getCustSwiftBrnCode() {
		return custSwiftBrnCode;
	}

	public void setCustSwiftBrnCode(String custSwiftBrnCode) {
		this.custSwiftBrnCode = custSwiftBrnCode;
	}

	public Date getCustAppDate() {
		return custAppDate;
	}

	public void setCustAppDate(Date custAppDate) {
		this.custAppDate = custAppDate;
	}

	public String getSourceSystem() {
		return sourceSystem;
	}

	public void setSourceSystem(String sourceSystem) {
		this.sourceSystem = sourceSystem;
	}

	public String getBranchRefno() {
		return branchRefno;
	}

	public void setBranchRefno(String branchRefno) {
		this.branchRefno = branchRefno;
	}

	public String getLovDescRequestStage() {
		return lovDescRequestStage;
	}

	public void setLovDescRequestStage(String lovDescRequestStage) {
		this.lovDescRequestStage = lovDescRequestStage;
	}

	public String getCustSourceID() {
		return custSourceID;
	}

	public void setCustSourceID(String custSourceID) {
		this.custSourceID = custSourceID;
	}

	public String getLovDescCustRO1City() {
		return lovDescCustRO1City;
	}

	public void setLovDescCustRO1City(String lovDescCustRO1City) {
		this.lovDescCustRO1City = lovDescCustRO1City;
	}

	public String getSubCategory() {
		return subCategory;
	}

	public void setSubCategory(String subCategory) {
		this.subCategory = subCategory;
	}

	public long getCasteId() {
		return casteId;
	}

	public void setCasteId(long casteId) {
		this.casteId = casteId;
	}

	public long getReligionId() {
		return religionId;
	}

	public void setReligionId(long religionId) {
		this.religionId = religionId;
	}

	public String getCasteDesc() {
		return casteDesc;
	}

	public void setCasteDesc(String casteDesc) {
		this.casteDesc = casteDesc;
	}

	public String getReligionDesc() {
		return religionDesc;
	}

	public void setReligionDesc(String religionDesc) {
		this.religionDesc = religionDesc;
	}

	public String getCasteCode() {
		return casteCode;
	}

	public void setCasteCode(String casteCode) {
		this.casteCode = casteCode;
	}

	public String getReligionCode() {
		return religionCode;
	}

	public void setReligionCode(String religionCode) {
		this.religionCode = religionCode;
	}

	public String getAadhaarNo() {
		return aadhaarNo;
	}

	public void setAadhaarNo(String aadhaarNo) {
		this.aadhaarNo = aadhaarNo;
	}

	public boolean isIncludeIncome() {
		return includeIncome;
	}

	public void setIncludeIncome(boolean includeIncome) {
		this.includeIncome = includeIncome;
	}

	public boolean isMarginDeviation() {
		return marginDeviation;
	}

	public void setMarginDeviation(boolean marginDeviation) {
		this.marginDeviation = marginDeviation;
	}

	public BigDecimal getCustomerAge() {
		return customerAge;
	}

	public void setCustomerAge(BigDecimal customerAge) {
		this.customerAge = customerAge;
	}

	public String getApplicationNo() {
		return applicationNo;
	}

	public void setApplicationNo(String applicationNo) {
		this.applicationNo = applicationNo;
	}

	public String getBranchProvince() {
		return branchProvince;
	}

	public void setBranchProvince(String branchProvince) {
		this.branchProvince = branchProvince;
	}

	public String getLegalconstitution() {
		return legalconstitution;
	}

	public void setLegalconstitution(String legalconstitution) {
		this.legalconstitution = legalconstitution;
	}

	public String getBusinesscategory() {
		return businesscategory;
	}

	public void setBusinesscategory(String businesscategory) {
		this.businesscategory = businesscategory;
	}

	public Customer getCustomer() {
		return customer;
	}

	public void setCustomer(Customer customer) {
		this.customer = customer;
	}

	public boolean isDnd() {
		return dnd;
	}

	public void setDnd(boolean dnd) {
		this.dnd = dnd;
	}

	public String getCustAddrProvince() {
		return custAddrProvince;
	}

	public void setCustAddrProvince(String custAddrProvince) {
		this.custAddrProvince = custAddrProvince;
	}

	public String getCustomerFullName() {
		return customerFullName;
	}

	public void setCustomerFullName(String customerFullName) {
		this.customerFullName = customerFullName;
	}

	public String getCustOffAddress() {
		return custOffAddress;
	}

	public void setCustOffAddress(String custOffAddress) {
		this.custOffAddress = custOffAddress;
	}

	public String getCustResiAddress() {
		return custResiAddress;
	}

	public void setCustResiAddress(String custResiAddress) {
		this.custResiAddress = custResiAddress;
	}

	public String getOtherReligion() {
		return otherReligion;
	}

	public void setOtherReligion(String otherReligion) {
		this.otherReligion = otherReligion;
	}

	public String getOtherCaste() {
		return otherCaste;
	}

	public void setOtherCaste(String otherCaste) {
		this.otherCaste = otherCaste;
	}

	public String getCkycOrRefNo() {
		return ckycOrRefNo;
	}

	public void setCkycOrRefNo(String ckycOrRefNo) {
		this.ckycOrRefNo = ckycOrRefNo;
	}

	public String getNatureOfBusiness() {
		return natureOfBusiness;
	}

	public void setNatureOfBusiness(String natureOfBusiness) {
		this.natureOfBusiness = natureOfBusiness;
	}

	public String getCustResidentialSts() {
		return custResidentialSts;
	}

	public void setCustResidentialSts(String custResidentialSts) {
		this.custResidentialSts = custResidentialSts;
	}

	public String getLovDescCustResidentialSts() {
		return lovDescCustResidentialSts;
	}

	public void setLovDescCustResidentialSts(String lovDescCustResidentialSts) {
		this.lovDescCustResidentialSts = lovDescCustResidentialSts;
	}

	public String getEntityType() {
		return entityType;
	}

	public void setEntityType(String entityType) {
		this.entityType = entityType;
	}

	public String getCustFlag() {
		return custFlag;
	}

	public void setCustFlag(String custFlag) {
		this.custFlag = custFlag;
	}

	public String getQualification() {
		return qualification;
	}

	public void setQualification(String qualification) {
		this.qualification = qualification;
	}

	public String getLovDescNatureOfBusiness() {
		return lovDescNatureOfBusiness;
	}

	public void setLovDescNatureOfBusiness(String lovDescNatureOfBusiness) {
		this.lovDescNatureOfBusiness = lovDescNatureOfBusiness;
	}

	public String getLovDescEntityType() {
		return lovDescEntityType;
	}

	public void setLovDescEntityType(String lovDescEntityType) {
		this.lovDescEntityType = lovDescEntityType;
	}

	public String getLovDescCustFlag() {
		return lovDescCustFlag;
	}

	public void setLovDescCustFlag(String lovDescCustFlag) {
		this.lovDescCustFlag = lovDescCustFlag;
	}

	public String getLovDescQualification() {
		return lovDescQualification;
	}

	public void setLovDescQualification(String lovDescQualification) {
		this.lovDescQualification = lovDescQualification;
	}

	public String getPrimaryIdName() {
		return primaryIdName;
	}

	public void setPrimaryIdName(String primaryIdName) {
		this.primaryIdName = primaryIdName;
	}

	public BigDecimal getIndustryMargin() {
		return industryMargin;
	}

	public void setIndustryMargin(BigDecimal industryMargin) {
		this.industryMargin = industryMargin;
	}

	public String getSubIndustry() {
		return subIndustry;
	}

	public void setSubIndustry(String subIndustry) {
		this.subIndustry = subIndustry;
	}

	public String getResidentialStatus() {
		return residentialStatus;
	}

	public void setResidentialStatus(String residentialStatus) {
		this.residentialStatus = residentialStatus;
	}

	public boolean isVip() {
		return vip;
	}

	public void setVip(boolean vip) {
		this.vip = vip;
	}

	public String getCustAddrCity() {
		return custAddrCity;
	}

	public void setCustAddrCity(String custAddrCity) {
		this.custAddrCity = custAddrCity;
	}

	public String getCustAddrCountry() {
		return custAddrCountry;
	}

	public void setCustAddrCountry(String custAddrCountry) {
		this.custAddrCountry = custAddrCountry;
	}

	public String getLovDescCustAddrCountry() {
		return lovDescCustAddrCountry;
	}

	public void setLovDescCustAddrCountry(String lovDescCustAddrCountry) {
		this.lovDescCustAddrCountry = lovDescCustAddrCountry;
	}

	public String getLovDescCustAddrCity() {
		return lovDescCustAddrCity;
	}

	public void setLovDescCustAddrCity(String lovDescCustAddrCity) {
		this.lovDescCustAddrCity = lovDescCustAddrCity;
	}

	public String getLovDescCustAddrProvince() {
		return lovDescCustAddrProvince;
	}

	public void setLovDescCustAddrProvince(String lovDescCustAddrProvince) {
		this.lovDescCustAddrProvince = lovDescCustAddrProvince;
	}

	public boolean isProspectAsCIF() {
		return prospectAsCIF;
	}

	public void setprospectAsCIF(boolean prospectAsCIF) {
		this.prospectAsCIF = prospectAsCIF;
	}

	public String getLoanName() {
		return loanName;
	}

	public void setLoanName(String loanName) {
		this.loanName = loanName;
	}

	public BigDecimal getLoanInstalmentAmount() {
		return loanInstalmentAmount;
	}

	public void setLoanInstalmentAmount(BigDecimal loanInstalmentAmount) {
		this.loanInstalmentAmount = loanInstalmentAmount;
	}

	public String getApplicantType() {
		return applicantType;
	}

	public void setApplicantType(String applicantType) {
		this.applicantType = applicantType;
	}

	public String getRelationWithCust() {
		return relationWithCust;
	}

	public void setRelationWithCust(String relationWithCust) {
		this.relationWithCust = relationWithCust;
	}

	public String getProduct() {
		return product;
	}

	public void setProduct(String product) {
		this.product = product;
	}
}
