/**
 * Copyright 2011 - Pennant Technologies
 * 
 * This file is part of Pennant Java Application Framework and related Products. 
 * All components/modules/functions/classes/logic in this software, unless 
 * otherwise stated, the property of Pennant Technologies. 
 * 
 * Copyright and other intellectual property laws protect these materials. 
 * Reproduction or retransmission of the materials, in whole or in part, in any manner, 
 * without the prior written consent of the copyright holder, is a violation of 
 * copyright law.
 */
package com.pennant.coreinterface.model.customer;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * Model class for the <b>Customer table</b>.<br>
 * 
 */
public class InterfaceCustomer implements Serializable {

	private static final long serialVersionUID = 8834741927485659871L;
	
	private long custID;
	private String custCIF;
	private String custCoreBank;
	private String custCtgCode;
	private String lovDescCustCtgCodeName;
	private String lovDescCustCtgType;
	private String custTypeCode;
	private String lovDescCustTypeCodeName;
	private String custSalutationCode;
	private String lovDescCustSalutationCodeName;
	private String custFName;
	private String custMName;
	private String custLName;
	private String custShrtName;
	private String custFNameLclLng;
	private String custMNameLclLng;
	private String custLNameLclLng;
	private String custShrtNameLclLng;
	private String custDftBranch;
	private String lovDescCustDftBranchName;
	private String custGenderCode;
	private String lovDescCustGenderCodeName;
	private Date custDOB;
	private String custPOB;
	private String custCOB;
	private String lovDescCustCOBName;
	private String custPassportNo;
	private String custMotherMaiden;
	private boolean custIsMinor;
	private String custReferedBy;
	private String custDSA;
	private String custDSADept;
	private String lovDescCustDSADeptName;
	private String custRO1;
	private String lovDescCustRO1Name;
	private String custRO2;
	private String lovDescCustRO2Name;
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
	private boolean custIsStaff;
	private String custStaffID;
	private String custIndustry;
	private String lovDescCustIndustryName;
	private String custSector;
	private String lovDescCustSectorName;
	private String custSubSector;
	private String lovDescCustSubSectorName;
	private String custProfession;
	private String lovDescCustProfessionName;
	private BigDecimal custTotalIncome = BigDecimal.ZERO;
	private BigDecimal custTotalExpense = BigDecimal.ZERO;
	private String custMaritalSts;
	private String lovDescCustMaritalStsName;
	private String custEmpSts;
	private String lovDescCustEmpStsName;
	private String custSegment;
	private String lovDescCustSegmentName;
	private String custSubSegment;
	private String lovDescCustSubSegmentName;
	private boolean custIsBlackListed;
	private Date custBlackListDate;
	private int noOfDependents;
	private String custBLRsnCode;
	private String lovDescCustBLRsnCodeName;
	private boolean custIsRejected;
	private String custRejectedRsn;
	private String lovDescCustRejectedRsnName;
	private String custBaseCcy;
	private String lovDescCustBaseCcyName;
	private int lovDescCcyFormatter;
	private String custLng;
	private String lovDescCustLngName;
	private String custParentCountry;
	private String lovDescCustParentCountryName;
	private String custResdCountry;
	private String lovDescCustResdCountryName;
	private String custRiskCountry;
	private String lovDescCustRiskCountryName;
	private String custNationality;
	private String lovDescCustNationalityName;
	private Date custClosedOn;
	private String custStmtFrq;
	private boolean custIsStmtCombined;
	private Date custStmtLastDate;
	private Date custStmtNextDate;
	private String custStmtDispatchMode;
	private String lovDescDispatchModeDescName;
	private Date custFirstBusinessDate;
	private String custAddlVar81;
	private String custAddlVar82;
	private String lovDescTargetName;
	private String custAddlVar83; //custRelatedParty
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
	private String phoneNumber;
	private String phoneAreaCode;
	private String phoneCountryCode;
	private double custAddlDec1;
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
	private boolean proceedToDedup = false;
	private boolean dedupFound = false;
	private boolean skipDedup = false;
	private String custCRCPR;
	private boolean jointCust;
	private String jointCustName;
	private Date jointCustDob;
	private String custRelation;

	public InterfaceCustomer() {
		
	}

	// ******************************************************//
	// ****************** getter / setter *******************//
	// ******************************************************//

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
		return lovDescCustCtgCodeName;
	}

	public void setLovDescCustCtgCodeName(String lovDescCustCtgCodeName) {
		this.lovDescCustCtgCodeName = lovDescCustCtgCodeName;
	}

	public String getLovDescCustCtgType() {
		return lovDescCustCtgType;
	}

	public void setLovDescCustCtgType(String lovDescCustCtgType) {
		this.lovDescCustCtgType = lovDescCustCtgType;
	}

	public String getCustTypeCode() {
		return custTypeCode;
	}

	public void setCustTypeCode(String custTypeCode) {
		this.custTypeCode = custTypeCode;
	}

	public String getLovDescCustTypeCodeName() {
		return lovDescCustTypeCodeName;
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
		return lovDescCustSalutationCodeName;
	}

	public void setLovDescCustSalutationCodeName(
			String lovDescCustSalutationCodeName) {
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
		return lovDescCustDftBranchName;
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
		return lovDescCustGenderCodeName;
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
		return lovDescCustCOBName;
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
		return lovDescCustDSADeptName;
	}

	public void setLovDescCustDSADeptName(String lovDescCustDSADeptName) {
		this.lovDescCustDSADeptName = lovDescCustDSADeptName;
	}

	public String getCustRO1() {
		return custRO1;
	}

	public void setCustRO1(String custRO1) {
		this.custRO1 = custRO1;
	}

	public String getLovDescCustRO1Name() {
		return lovDescCustRO1Name;
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
		return lovDescCustRO2Name;
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

	public String getLovDescCustGroupCode() {
		return lovDescCustGroupCode;
	}

	public void setLovDescCustGroupCode(String lovDescCustGroupCode) {
		this.lovDescCustGroupCode = lovDescCustGroupCode;
	}

	public String getLovDesccustGroupIDName() {
		return lovDesccustGroupIDName;
	}

	public void setLovDesccustGroupIDName(String lovDesccustGroupIDName) {
		this.lovDesccustGroupIDName = lovDesccustGroupIDName;
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

	public String getCustSts() {
		return custSts;
	}

	public void setCustSts(String custSts) {
		this.custSts = custSts;
	}

	public String getLovDescCustStsName() {
		return lovDescCustStsName;
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
		return lovDescCustGroupStsName;
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
		return lovDescCustIndustryName;
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
		return lovDescCustSectorName;
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
		return lovDescCustSubSectorName;
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
		return lovDescCustProfessionName;
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

	public BigDecimal getCustTotalExpense() {
		return custTotalExpense;
	}

	public void setCustTotalExpense(BigDecimal custTotalExpense) {
		this.custTotalExpense = custTotalExpense;
	}

	public String getCustMaritalSts() {
		return custMaritalSts;
	}

	public void setCustMaritalSts(String custMaritalSts) {
		this.custMaritalSts = custMaritalSts;
	}

	public String getLovDescCustMaritalStsName() {
		return lovDescCustMaritalStsName;
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
		return lovDescCustEmpStsName;
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
		return lovDescCustSegmentName;
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

	public String getLovDescCustSubSegmentName() {
		return lovDescCustSubSegmentName;
	}

	public void setLovDescCustSubSegmentName(String lovDescCustSubSegmentName) {
		this.lovDescCustSubSegmentName = lovDescCustSubSegmentName;
	}

	public boolean isCustIsBlackListed() {
		return custIsBlackListed;
	}

	public void setCustIsBlackListed(boolean custIsBlackListed) {
		this.custIsBlackListed = custIsBlackListed;
	}

	public Date getCustBlackListDate() {
		return custBlackListDate;
	}

	public void setCustBlackListDate(Date custBlackListDate) {
		this.custBlackListDate = custBlackListDate;
	}

	public int getNoOfDependents() {
		return noOfDependents;
	}

	public void setNoOfDependents(int noOfDependents) {
		this.noOfDependents = noOfDependents;
	}

	public String getCustBLRsnCode() {
		return custBLRsnCode;
	}

	public void setCustBLRsnCode(String custBLRsnCode) {
		this.custBLRsnCode = custBLRsnCode;
	}

	public String getLovDescCustBLRsnCodeName() {
		return lovDescCustBLRsnCodeName;
	}

	public void setLovDescCustBLRsnCodeName(String lovDescCustBLRsnCodeName) {
		this.lovDescCustBLRsnCodeName = lovDescCustBLRsnCodeName;
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

	public String getLovDescCustRejectedRsnName() {
		return lovDescCustRejectedRsnName;
	}

	public void setLovDescCustRejectedRsnName(String lovDescCustRejectedRsnName) {
		this.lovDescCustRejectedRsnName = lovDescCustRejectedRsnName;
	}

	public String getCustBaseCcy() {
		return custBaseCcy;
	}

	public void setCustBaseCcy(String custBaseCcy) {
		this.custBaseCcy = custBaseCcy;
	}

	public String getLovDescCustBaseCcyName() {
		return lovDescCustBaseCcyName;
	}

	public void setLovDescCustBaseCcyName(String lovDescCustBaseCcyName) {
		this.lovDescCustBaseCcyName = lovDescCustBaseCcyName;
	}

	public int getLovDescCcyFormatter() {
		return lovDescCcyFormatter;
	}

	public void setLovDescCcyFormatter(int lovDescCcyFormatter) {
		this.lovDescCcyFormatter = lovDescCcyFormatter;
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
		return lovDescCustParentCountryName;
	}

	public void setLovDescCustParentCountryName(
			String lovDescCustParentCountryName) {
		this.lovDescCustParentCountryName = lovDescCustParentCountryName;
	}

	public String getCustResdCountry() {
		return custResdCountry;
	}

	public void setCustResdCountry(String custResdCountry) {
		this.custResdCountry = custResdCountry;
	}

	public String getLovDescCustResdCountryName() {
		return lovDescCustResdCountryName;
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
		return lovDescCustRiskCountryName;
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
		return lovDescCustNationalityName;
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

	public Date getCustStmtLastDate() {
		return custStmtLastDate;
	}

	public void setCustStmtLastDate(Date custStmtLastDate) {
		this.custStmtLastDate = custStmtLastDate;
	}

	public Date getCustStmtNextDate() {
		return custStmtNextDate;
	}

	public void setCustStmtNextDate(Date custStmtNextDate) {
		this.custStmtNextDate = custStmtNextDate;
	}

	public String getCustStmtDispatchMode() {
		return custStmtDispatchMode;
	}

	public void setCustStmtDispatchMode(String custStmtDispatchMode) {
		this.custStmtDispatchMode = custStmtDispatchMode;
	}

	public String getLovDescDispatchModeDescName() {
		return lovDescDispatchModeDescName;
	}

	public void setLovDescDispatchModeDescName(
			String lovDescDispatchModeDescName) {
		this.lovDescDispatchModeDescName = lovDescDispatchModeDescName;
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

	public double getCustAddlDec1() {
		return custAddlDec1;
	}

	public void setCustAddlDec1(double custAddlDec1) {
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

	public boolean isSalariedCustomer() {
		return salariedCustomer;
	}

	public void setSalariedCustomer(boolean salariedCustomer) {
		this.salariedCustomer = salariedCustomer;
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

	public String getCustCRCPR() {
		return custCRCPR;
	}

	public void setCustCRCPR(String custCRCPR) {
		this.custCRCPR = custCRCPR;
	}

	public boolean isJointCust() {
		return jointCust;
	}

	public void setJointCust(boolean jointCust) {
		this.jointCust = jointCust;
	}

	public String getJointCustName() {
		return jointCustName;
	}

	public void setJointCustName(String jointCustName) {
		this.jointCustName = jointCustName;
	}

	public Date getJointCustDob() {
		return jointCustDob;
	}

	public void setJointCustDob(Date jointCustDob) {
		this.jointCustDob = jointCustDob;
	}

	public String getCustRelation() {
		return custRelation;
	}

	public void setCustRelation(String custRelation) {
		this.custRelation = custRelation;
	}

	public long getCustID() {
		return custID;
	}

	public void setCustID(long custID) {
		this.custID = custID;
	}
}
