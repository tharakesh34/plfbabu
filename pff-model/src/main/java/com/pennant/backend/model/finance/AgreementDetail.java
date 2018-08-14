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

/**
 ********************************************************************************************
 *                                 FILE HEADER                                              *
 ********************************************************************************************
 *
 * FileName    		:  AgreementDetail.java													*                           
 *                                                                    
 * Author      		:  PENNANT TECHONOLOGIES												*
 *                                                                  
 * Creation Date    :  27-04-2012															*
 *                                                                  
 * Modified Date    :  23-07-2018															*
 *                                                                  
 * Description 		:												 						*                                 
 *                                                                                          
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 10-05-2018       Pennant	                 1.0          Updated as part of Agreements     * 
 * 23-07-2018       Pennant                  1.1          Adding the Collateral Extended    * 
 *                                                        Details                           * 
 *                                                                                          * 
 *                                                                                          * 
 *                                                                                          * 
 *                                                                                          * 
 *                                                                                          * 
 *                                                                                          * 
 ********************************************************************************************
 */

package com.pennant.backend.model.finance;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;

import com.pennanttech.pennapps.pff.sampling.model.SamplingDetail;

public class AgreementDetail {

	//=============== Basic Details=============//
	private String 	userId = "";															// Login User ID
	private String 	userName = "";														// Login User Name
	private String 	appDate = "";														// Application Date
	private String 	appTime = "";														// Application Time
	private String 	userDeptName = "";													// User Department 
	private String 	usrEmailId = "";													// User EmailId 
	
	// =========== Customer ====================//
	
	private long   	custId = 0;
	private String 	custCIF = "";													// CIF Number
	private String 	custName = ""; 														// Full Name or Short Name
	private String 	custArabicName = "";													// Customer Arabic Name
	private String 	custPassport = "";												// Customer Passport Number
	private String 	custRO1 ="";													// Primary Relationship Officer
	private String 	custDOB="";														// Date of Birth for Retail & Date of Incorporation for Corporate
	private String 	custJointDOB="";														// Date of Birth for Retail & Date of Incorporation for Corporate
	private String 	custJointName="";														// Date of Birth for Retail & Date of Incorporation for Corporate
	private String 	custAge="";														// Customer Age based On Date Of Birth
	private String 	custEmpProf=""; 													// Profession
	private String 	phoneHome=""; 														// Phone Number with Phone Type : "HOME"
	private String 	custMobile=""; 													// Phone Number with Phone Type : "MOBILE"
	private String 	custFax=""; 														// Phone Number with Phone Type : "FAX"
	private String 	custEmail=""; 														// Email with Email Type : "PERSON1"
	private String 	custIdType="";														// EID for Retail & Trade License for Corporate
	private String 	custIdName="";														// ID Number : EID for Retail & Trade License for Corporate
	private String 	noOfDependents="";													// Number of Dependents
	private String  custSector="";														// Customer Sector
	private String  custSubSector="";													// Customer SubSector
	private String  custSegment="";													// Customer Segment
	private String  custIndustry="";													// Customer Industry
	private String  custNationality="";												// Customer Nationality
	private String  custCPRNo="";														// Customer CPR/EID/Trade License
	private String  custTotIncome="";													// Customer Total Income
	private String  custTotExpense="";													// Customer Total Expense
	private String  branchDesc="";
	private String panNumber ="";
	private String  custEmpStsCode="";													// Customer Employee Status Code
	private String  custEmpStsDesc="";													// Customer Employee Status Description
	private String  custEmpName="";													// Customer Employer Name
	private String  custYearsExp="";													// Customer Year of experience
	private String  custEmpStartDate="";												// Customer Employee Start Date
	private String  custOccupation="";												// Customer Employee Designation
	private String  custDocExpDate="";												// Customer Employee Designation
	private String  custDocIdNum="";												// Customer Employee Designation
	
	private String  custaddress="";													// Customer Address
	private String  custSalutation="";												//  customer salutation
	private String  custGender="";												    //  customer Gender
	private String  custCtgCode="";												    //  customer Gender
	
	private String otherBankName;
	private String otherBankAmt;
	private String custContribution;
	private String custConInWords;
	private String liabilityHoldName;
	private String sellerContribution;
	private String sellerContInWords;
	private String otherBankAmtInWords;
	private String otherBankAmtInArabic;
	private String llReferenceNo;
	private String llDate;
	private String  coAppTotIncome="0.00";													// Co Applicant Total Income
	private String  coAppTotExpense="0.00";													// Co Applicant Total Expense
	private String totalIncome="0.00";
	private String totalExpense="0.00";

	

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

	public String getCustPOBox() {
		return custPOBox;
	}

	public void setCustPOBox(String custPOBox) {
		this.custPOBox = custPOBox;
	}
	
	public String getBranchDesc() {
		return branchDesc;
	}

	public void setBranchDesc(String branchDesc) {
		this.branchDesc = branchDesc;
	}
	
	public String getCustAddrCountry() {
		return custAddrCountry;
	}

	public void setCustAddrCountry(String custAddrCountry) {
		this.custAddrCountry = custAddrCountry;
	}

	public String getLovDescCustAddrCountryName() {
		return lovDescCustAddrCountryName;
	}

	public void setLovDescCustAddrCountryName(String lovDescCustAddrCountryName) {
		this.lovDescCustAddrCountryName = lovDescCustAddrCountryName;
	}

	public String getCustAddrProvince() {
		return custAddrProvince;
	}

	public void setCustAddrProvince(String custAddrProvince) {
		this.custAddrProvince = custAddrProvince;
	}

	public String getLovDescCustAddrProvinceName() {
		return lovDescCustAddrProvinceName;
	}

	public void setLovDescCustAddrProvinceName(String lovDescCustAddrProvinceName) {
		this.lovDescCustAddrProvinceName = lovDescCustAddrProvinceName;
	}

	public String getCustAddrCity() {
		return custAddrCity;
	}

	public void setCustAddrCity(String custAddrCity) {
		this.custAddrCity = custAddrCity;
	}

	public String getLovDescCustAddrCityName() {
		return lovDescCustAddrCityName;
	}

	public void setLovDescCustAddrCityName(String lovDescCustAddrCityName) {
		this.lovDescCustAddrCityName = lovDescCustAddrCityName;
	}

	public String getCustAddrZIP() {
		return custAddrZIP;
	}

	public void setCustAddrZIP(String custAddrZIP) {
		this.custAddrZIP = custAddrZIP;
	}

	public String getCustAddrPhone() {
		return custAddrPhone;
	}

	public void setCustAddrPhone(String custAddrPhone) {
		this.custAddrPhone = custAddrPhone;
	}

	private String custAddrHNbr="";
	private String custFlatNbr="";
	private String custAddrStreet="";
	private String custAddrLine1="";
	private String custAddrLine2="";
	private String custPOBox="";
	private String custAddrCountry="";
	private String lovDescCustAddrCountryName="";
	private String custAddrProvince="";
	private String lovDescCustAddrProvinceName="";
	private String custAddrCity ="";
	private String lovDescCustAddrCityName ="";
	private String custAddrZIP ="";
	private String custAddrPhone ="";
	
	// ========== Arabic Fields Details ==========//
	
	private String finReferenceArabic;
	private String custCityArabic;
	private String propertyTypeArabic;
	private String sellerNameArabic;
	private String sellerAddrArabic;
	private String custNationalityArabic;
	private String plotUnitNumberArabic;
	private String otherbankNameArabic;
	private String sectorOrCommArabic;
	private String finAmountInArabic;
	private String proprtyDescArabic;
	

	private String propertyLocArabic;
	private String unitAreaInArabic;
	private String unitAreaInSqftArabic;
	private String buildUpAreaArabic;
	private String sellerInternalArabic;
	private String otherBankAmtArabic;
	private String custJointNameArabic;
	private String collateralArabic;
	private String collateralAuthArabic;
	private String propertyUseArabic;
	private String sellerNationalityArabic;
	private String sellerCntbAmtArabic;
	private String custCntAmtArabic;



	// ========== Customer Address Details ==========//
	
	
	
	// =========== Finance Basic================//
	
	// Finance Basic Details
	//===============================
	private String 	finType ="";														// Finance Type
	private String 	finTypeDesc ="";													// Finance Type Description
	private String 	finDivision ="";													// Division
	private String 	finRef ="";															// Finance Reference
	private String 	finCcy ="";															// Currency Code
	private String 	pftDaysBasis ="";													// Profit Days Basis
	private String 	finBranch ="";														// Finance Branch
	private String  finBranchName="";
	private String repayRateBasis;
	private String 	startDate ="";														// Finance Start Date
	private String 	contractDate ="";													// Contract Date
	private String 	finAmount ="";														// Finance Amount
	private String 	finAmountInWords ="";												// Finance Amount in Words
	private String 	feeChargeAmt ="";													// Fee Amount added to Finance Amount
	private String 	insuranceAmt ="";													// Insurance Amount added to Finance Amount
	private String 	calSchdFeeAmt ="";													// Fee Amount which is added to Schedule
	private String 	downPayment ="";													// Down Payment to Bank / Down Pay by Bank in case of AHB DPSP
	private String 	downPayBank ="";													// Down Payment to Bank / Down Pay by Bank in case of AHB DPSP
	private String 	downPaySupl ="";													// Down Payment to Supplier/Vendor
	private String 	disbAccount ="";													// Disbursement Account
	private String 	repayAccount ="";													// Customer Repayment Account
	private String 	downpayAc ="";														// Down Payment Account
	private String 	finPurpose ="";														// Finance Purpose / Asset Type
	private String 	finRpyMethod ="";													// Repayment Method
	private String 	facilityRef =""; 													// Commitment Or Limit Reference
	private String 	grcEndDate ="";														// Grace Period End Date
	private String 	lpoPrice ="";														// LPO Price
	private String 	lpoPriceInWords ="";												// LPO Price in Words
	private int   formatter;														    // Currency Formatter
	private String  initiatedBy ="";													// Finance Initiated User
	private String  initiatedDate ="";													// Finance Initiated Date
	private String   totalPriPaid ="";
	private String   totalRepayAmt ="";
	private String   secSixTermAmt ="";
	private String 	finAmtPertg ="";												    // Finance Amount * 125%
	private String 	purchasePrice  ="";												    // Finance Amount - DownPayment 
	private String 	secDeposit  ="";												    // DownPayment 
	private String 	facilityAmt  ="";												    // Facility Amount in Finance Basic Details
	private String 	VehicleCC  ="";												
	private String 	vehicleCategory  ="";												// totalExpAmt=(FeeAmount-WaiverAmount-PaidAmount)
	private String 	totalExpAmt  ="";													// totalExpAmt=(FeeAmount-WaiverAmount-PaidAmount)
	private String 	repayFrqDay  ="";													// Payment Frequency Day 
	private String 	repayFrqCode  ="";													// Payment Frequency Day 
	
	
	public String getFinBranchAddrHNbr() {
		return StringUtils.trimToEmpty(finBranchAddrHNbr);
	}

	public void setFinBranchAddrHNbr(String finBranchAddrHNbr) {
		this.finBranchAddrHNbr = finBranchAddrHNbr;
	}

	public String getFinBranchFlatNbr() {
		return StringUtils.trimToEmpty(finBranchFlatNbr);
	}

	public void setFinBranchFlatNbr(String finBranchFlatNbr) {
		this.finBranchFlatNbr = finBranchFlatNbr;
	}

	public String getFinBranchAddrStreet() {
		return StringUtils.trimToEmpty(finBranchAddrStreet);
	}

	public void setFinBranchAddrStreet(String finBranchAddrStreet) {
		this.finBranchAddrStreet = finBranchAddrStreet;
	}

	public String getFinBranchAddrLine1() {
		return StringUtils.trimToEmpty(finBranchAddrLine1);
	}

	public void setFinBranchAddrLine1(String finBranchAddrLine1) {
		this.finBranchAddrLine1 = finBranchAddrLine1;
	}

	public String getFinBranchAddrLine2() {
		return StringUtils.trimToEmpty(finBranchAddrLine2);
	}

	public void setFinBranchAddrLine2(String finBranchAddrLine2) {
		this.finBranchAddrLine2 = finBranchAddrLine2;
	}

	public String getFinBranchPOBox() {
		return StringUtils.trimToEmpty(finBranchPOBox);
	}

	public void setFinBranchPOBox(String finBranchPOBox) {
		this.finBranchPOBox = finBranchPOBox;
	}

	public String getFinBranchAddrCountry() {
		return StringUtils.trimToEmpty(finBranchAddrCountry);
	}

	public void setFinBranchAddrCountry(String finBranchAddrCountry) {
		this.finBranchAddrCountry = finBranchAddrCountry;
	}

	public String getFinBranchAddrCountryName() {
		return StringUtils.trimToEmpty(finBranchAddrCountryName);
	}

	public void setFinBranchAddrCountryName(String finBranchAddrCountryName) {
		this.finBranchAddrCountryName = finBranchAddrCountryName;
	}

	public String getFinBranchAddrProvince() {
		return StringUtils.trimToEmpty(finBranchAddrProvince);
	}

	public void setFinBranchAddrProvince(String finBranchAddrProvince) {
		this.finBranchAddrProvince = finBranchAddrProvince;
	}

	public String getFinBranchAddrProvinceName() {
		return StringUtils.trimToEmpty(finBranchAddrProvinceName);
	}

	public void setFinBranchAddrProvinceName(String finBranchAddrProvinceName) {
		this.finBranchAddrProvinceName = finBranchAddrProvinceName;
	}

	public String getFinBranchAddrCity() {
		return StringUtils.trimToEmpty(finBranchAddrCity);
	}

	public void setFinBranchAddrCity(String finBranchAddrCity) {
		this.finBranchAddrCity = finBranchAddrCity;
	}

	public String getFinBranchAddrCityName() {
		return StringUtils.trimToEmpty(finBranchAddrCityName);
	}

	public void setFinBranchAddrCityName(String finBranchAddrCityName) {
		this.finBranchAddrCityName = finBranchAddrCityName;
	}

	public String getFinBranchAddrZIP() {
		return StringUtils.trimToEmpty(finBranchAddrZIP);
	}

	public void setFinBranchAddrZIP(String finBranchAddrZIP) {
		this.finBranchAddrZIP = finBranchAddrZIP;
	}

	public String getFinBranchAddrPhone() {
		return StringUtils.trimToEmpty(finBranchAddrPhone);
	}

	public void setFinBranchAddrPhone(String finBranchAddrPhone) {
		this.finBranchAddrPhone = finBranchAddrPhone;
	}

	private String finBranchAddrHNbr="";
	private String finBranchFlatNbr="";
	private String finBranchAddrStreet="";
	private String finBranchAddrLine1="";
	private String finBranchAddrLine2="";
	private String finBranchPOBox="";
	private String finBranchAddrCountry="";
	private String finBranchAddrCountryName="";
	private String finBranchAddrProvince="";
	private String finBranchAddrProvinceName="";
	private String finBranchAddrCity ="";
	private String finBranchAddrCityName ="";
	private String finBranchAddrZIP ="";
	private String finBranchAddrPhone ="";

	//Repayment Details
	//===============================
	private String 	noOfPayments ="";													// Number of Installments
	private String 	repayFrq ="";														// Repayment Frequency
	private String 	rpyRateBasis ="";													// Repayment Rate Basis
	private String 	firstInstDate ="";													// Next Repay Date
	private String 	lastInstDate ="";													// Last Installment Date / Maturity Date
	private String 	curInstDate ="";													// Current Installment Date 
	private String 	nextInstDate ="";													// Next Installment Date 
	private String 	firstInstAmount ="";												// First Term Installment Amount
	private String 	lastInstAmount ="";													// Last Term Installment Amount
	private String 	schdMethod ="";														// Schedule Method
	private String 	profitRate ="";														// Actual Profit Rate of Repayment
	private String 	repayBaseRate ="";													// Actual repay Rate Base  of Repayment
	private String 	advPftRate ="";														// Advised Actual Profit Rate of Repayment
	private String 	advBaseRate ="";													// Advised Base Rate of Repayment
	private String 	advMargin ="";														// Advised Margin Rate of Repayment
	private String 	advPayment ="";														// Advance Payment
	private String 	effPftRate ="";														// Effective Rate of Return
	private String 	maturityDate ="";													// Maturity Date
	private String 	profit ="";															// Total Profit
	private String 	advProfit ="";														// Total Advised Profit
	private String 	latePayRate ="";													// Late Payment Penalty Rate
	private String 	nextInstlPft ="";													// Next Instl Pft
	private String 	noOfDays ="";														// Next Instl Pft
	private String 	repayAmount ="";													// repay Amount
	private String 	insAmt ="";													    	// Insurance Amount
	private String 	repayMargin ="";													//  Margin
	private String 	numOfPayGrace ="";											        //  NumberOfTerms-graceTerms
	private String finMinRate ="";
	private String finMaxRate ="";
	private String profitRateType ="";														//Profit Rate Type
	private String tenor ="";
	private String netRefRateLoan ="";
	private String repayBaseRateVal = "";
	
	//TODO::Added as part of Comments
	private String graceAvailable ="";
	private String totalTerms ="";
	private String firstDisbursementAmt ="";
	private String repaySplRate ="";
	
	//DDA Fields
	//===============================
	private String 	bankName ="";														// DDA Sponsoring Bank
	private String 	accountType ="";													// DDA Bank Account Type for selected Account
	private String 	iban =""; 															// Customer IBAN Number
	private String 	ddaPurposeCode ="";													// DDA Purpose Code
	private String 	ifscCode ="";													// DDA Purpose Code
	
	//Mandate
	private String				accNumberMandate ="";

	
	//External Fields
	//===============================
	private String 	mMADate ="";														// MMA Agreement Date
	private String 	custDSR ="";														// Customer DSR
	
	private List<Recommendation> recommendations;									// Recommendations Data
	private List<GroupRecommendation> groupRecommendations;							// Grouping Recommendations
	private List<ExceptionList> exceptionLists;										// Exceptional List(May be Deviations in Future)
	
	// =========== Vehicle Finance =============//

	/*
	private String 	quotationNo ="";													// Quotation Number
	private String 	quotationDate ="";													// Quotation Date
	private String 	paymentMode ="";													// Payment Mode
	private String 	purchaseDate ="";													// Purchase Date
	private String 	carDealer ="";														// Vendor/Seller ID
	private String 	dealerName ="";														// Vendor/Seller Name
	private String 	dealerPhone ="";													// Vendor/Seller Name
	private String 	dealerFax ="";													// Vendor/Seller Name
	private String 	dealerAddr ="";														// Vendor/Seller Address
	private String 	dealerCountry ="";													// Vendor/Seller Country
	private String 	dealerCity ="";														// Vendor/Seller City
	private String 	salesPersonName ="";												// Sales Person Name / DSA
	private String 	purchaseOrder ="";													// Purchase Order Number (FinReference)
	private String 	manfacName ="";														// Vehicle Brand
	private String 	model ="";															// Vehicle Model
	private String 	modelYear ="";														// Make Year
	private String 	carColor ="";														// Car Color
	private String 	carChasisNo ="";													// Chasis Number
	private String 	engineNumber ="";													// Engine Number
	private String 	trafficProfileNo ="";												// Traffic Profile Number / Traffic Code Number
	private String 	tpName ="";															// Third Party Name
	private String 	tpPassport ="";														// Third Party Passport Number
	private String 	carCapacity ="";													// Number of Cylinders
	private String 	carRegNo ="";														// Car Registration Number
	private String 	emiratesReg ="";													// Emirates of Registration / Province of Dealer
	private String 	vehicleType ="";													// Vehicle Type
	private String 	vehicleUsage ="";													// Vehicle Usage
	private String 	assetValue ="";														// Asset Value
	private String 	dealercompN ="";												    // Seller Company Name
	private String 	finAmtPerAssetValue ="";											// Financing Amount in perc of Asset Value
	private String 	sharePerFinAmtAssetValue ="";											//Share perc for Financing amount on Asset price
	
	private String 	dealerPOBox ="";													//dealer PO Box
	private String 	lpoDate ="";														//LOP Date
	private String 	totalPrice ="";														//total Price
	private String 	deliveryOfLoc ="";													//delivery Of Location
	private String 	bondNumFrom ="";													//bondNumFrom
	private String 	bondNumTo ="";														//bondNumTo	
	private String 	vehicleReg ="";													    // Emirates of Registration
	
	*/
	
	//=============Gurantee Details==============//
	
	private String guranteeNum;
	private String guranteeAmt;						//Gurantee % of Finance Amount
	private String guranteeDays;					// Days Between the Start Date and Maturity Date
	private String guranteeEndDate;                 // maturity Date
	private String guranteeAmtInWords;
	
	public String getGuranteeNum() {
		return guranteeNum;
	}
	public void setGuranteeNum(String guranteeNum) {
		this.guranteeNum = guranteeNum;
	}

	public String getGuranteeAmt() {
		return guranteeAmt;
	}
	public void setGuranteeAmt(String guranteeAmt) {
		this.guranteeAmt = guranteeAmt;
	}

	public String getGuranteeDays() {
		return guranteeDays;
	}
	public void setGuranteeDays(String guranteeDays) {
		this.guranteeDays = guranteeDays;
	}

	public String getGuranteeEndDate() {
		return guranteeEndDate;
	}
	public void setGuranteeEndDate(String guranteeEndDate) {
		this.guranteeEndDate = guranteeEndDate;
	}

	public String getGuranteeAmtInWords() {
		return guranteeAmtInWords;
	}
	public void setGuranteeAmtInWords(String guranteeAmtInWords) {
		this.guranteeAmtInWords = guranteeAmtInWords;
	}
	
	/*
	 * Remove fields as not required comments from Raju
	//=============Home Loan Asset Details==============//
	private String projectName;
	private String propertyCategory;
	private String propertyType;
	private String developerName;
	private String sellerName;
	private String sellerPassport;
	private String areaInSqureFeet;
	private String areaInSqureFeetEng;
	private String houseNumber;
	private String flatNumber;
	private String homestreet;
	private String homePOBox;
	private String homeCountry;
	private String unitAreaInSqftEng;
	private String unitAreaInSqft;
	private String plotUnitNumber;
	private String sectorOrCommunity;
	private String buildUpArea;
	private String buildUpAreaEng;
	private String emiratesOfReg;
	private String mOUDate;
	private String marketValue;
	private String marketValueInWords;
	private String propertyUse;
	private String propertyloc;
	private String sellerInternal;
	private String numberOfUnits;
	private String numberOfLeasedUnits;
	

	public String getPlotUnitNumber() {
		return plotUnitNumber;
	}

	public void setPlotUnitNumber(String plotUnitNumber) {
		this.plotUnitNumber = plotUnitNumber;
	}

	public String getSectorOrCommunity() {
		return sectorOrCommunity;
	}

	public void setSectorOrCommunity(String sectorOrCommunity) {
		this.sectorOrCommunity = sectorOrCommunity;
	}

	public String getBuildUpArea() {
		return buildUpArea;
	}

	public void setBuildUpArea(String buildUpArea) {
		this.buildUpArea = buildUpArea;
	}

	public String getPropertyType() {
		return propertyType;
	}

	public void setPropertyType(String propertyType) {
		this.propertyType = propertyType;
	}

	public String getDeveloperName() {
		return developerName;
	}

	public void setDeveloperName(String developerName) {
		this.developerName = developerName;
	}

	public String getSellerName() {
		return sellerName;
	}

	public void setSellerName(String sellerName) {
		this.sellerName = sellerName;
	}

	public String getAreaInSqureFeet() {
		return areaInSqureFeet;
	}

	public void setAreaInSqureFeet(String areaInSqureFeet) {
		this.areaInSqureFeet = areaInSqureFeet;
	}

	public String getAreaInSqureFeetEng() {
		return areaInSqureFeetEng;
	}

	public void setAreaInSqureFeetEng(String areaInSqureFeetEng) {
		this.areaInSqureFeetEng = areaInSqureFeetEng;
	}

	public String getHouseNumber() {
		return houseNumber;
	}

	public void setHouseNumber(String houseNumber) {
		this.houseNumber = houseNumber;
	}

	public String getFlatNumber() {
		return flatNumber;
	}

	public void setFlatNumber(String flatNumber) {
		this.flatNumber = flatNumber;
	}

	public String getHomestreet() {
		return homestreet;
	}

	public void setHomestreet(String homestreet) {
		this.homestreet = homestreet;
	}

	public String getHomePOBox() {
		return homePOBox;
	}

	public void setHomePOBox(String homePOBox) {
		this.homePOBox = homePOBox;
	}

	public String getHomeCountry() {
		return homeCountry;
	}

	public void setHomeCountry(String homeCountry) {
		this.homeCountry = homeCountry;
	}*/

	public String getRepayBaseRateVal() {
		return repayBaseRateVal;
	}

	public void setRepayBaseRateVal(String repayBaseRateVal) {
		this.repayBaseRateVal = repayBaseRateVal;
	}

	public String getFinMinRate() {
		return finMinRate;
	}

	public void setFinMinRate(String finMinRate) {
		this.finMinRate = finMinRate;
	}
	
	//=============Customer Income==============//
	
	//private List<CustomerIncomeCategory> custincomeCategories;
	
	//====== Customer Existing Finances ========//
	
	private List<CustomerFinance> customerFinances;									// Existing Finances Under same Customer
	private String totCustFin ="";														// Total Outstanding Finance Amount
	
	//======Mortgage Finance ===================//
	
	private String assetType ="";														// Asset Type
	private String assetarea ="";														// Area of Asset
	private String assetRegistration ="";											
	private String deedno ="";
	private String assetStatus ="";
	private String assetareainSF ="";
	private String assetage ="";
	private String assetareainSM ="";
	private String assetMarketvle ="";
	private String assetPricePF ="";
	private String assetFinRatio ="";
	
	//====== Goods Finance =====================//
	
	private List<GoodLoanDetails> goodsLoanDetails;
	
	//====== Commodity Finance =================//
	
	private String holdCertificateNo ="";
	private String quantity ="";
	private String commodityType ="";
	private String commodityDesc ="";  //  Quantity and General Description of Commodities
	
	private String commodityUnit ="";
	private String brokerName ="";
	/*private int tenureDays;
	private String splInstruction;
	private List<CommidityLoanDetails> commidityLoanDetail;*/
	
	//======== Credit review====================//
	
	/*private String adtYear1 = "";
	private String adtYear2 = "";
	private String adtYear3 = "";
	private List<CustomerCreditReview> creditReviewsBalance;
	private List<CustomerCreditReview> creditReviewsRatio;*/
	
	//=============== Scoring Details===========//
	
	/*private List<ScoringHeader> finScoringHeaderDetails;
	private List<ScoringHeader> nonFinScoringHeaderDetails;
	private String totMaxScore = "";
	private String totMinScore = "";
	private String totCalcScore = "";
	private String overrideScore = "";
	private String isOverrideScore = "";*/
	
	//========== Schedule Details ==========//
	
	private String osPri =""; 						//Outstanding AHB Share Amount (AED)
	private String comName ="";						//Company Name 
	private String sharePerc ="";					//AHB Share (finAmount-downpayment)/finAmount
	private String perofPri ="";					//Perc of Pri in Schd on Total Pri
	private String fIDate ="";						// First Installment Date
	private String SchAdvRate ="";
	private String schdInst ="";					//Schd Installment
	private String osPriAmount ="";	
	private String SchPriAmtPerBank ="";			//Schd Pri Amount % for bank
	private String SchPriAmtPerMContr ="";			//Schd Pri Amount % for many contributors
	private String SchPriAmtPerSContr ="";			//Schd Pri Amount % for single contributors
	private String actualInstlpft ="";				//Schd Pri Amount % for many contributors
	private String PftDiffRates ="";				//Pft Diff b/w twp rates
	private String perPriPaidCustinstl ="";			//Percentage of pri paid by Cust on this instl
	private String priAmtCurtInst ="";				//Pri amount of cur Instl
	private String schPriFirstInstl ="";			//Schd Pri Amount on First Instl
	private String schdPftFirstInstl ="";			//Schd Pft Amount on First Instl
	private String schdAdvPftFirstInstl ="";		//Schd Adv Pft Amount on First Instl
	
	private String oDInsDate ="";					//OD Instl Date
	private String emiRate ="";						//EMI DATE 
	private String prvInstDate ="";					//PRV INSTL Date
	private String defPayDate ="";    				//Deferred Payment Date
	private String earlySettleAmt ="";    			//Early Settlement Amount
	private String earlySettleDate ="";    			//Early Settlement Date
	private String firstInstDays ="";    			//first Inst Days 
	private String secondInstDays ="";    			//second Inst Days
	private String reviewDate ="";    			    //First EIBOR Revision Date
	
	
	
	
	
	
	//========== Master Agreement ==========//
	
	private String facilityDate ="";
	private String mMAFacilityLimit ="";
	private String facilityPftRate ="";
	private String facilityBaseRate ="";
	private String facilityMargin ="";
	private String facilityMinRate ="";
	private String facilityPeriod ="";
	private String facilityMaturity ="";
	private String facilityMinAmount ="";
	private String facilityLatePayRate ="";
	private String facilityCcy ="";
	private String facilityAmount ="";
	private String folReference ="";
	private String facilityMaxCapRate ="";
	private String facilityMinCapRate ="";
	private String noOfContracts ="";

	
	private String mMAPurchRegOffice ="";
	private String mMAContractAmt = "";
	private String mMAPurchaddress ="";
	private String attention ="";
	private String mMAFax ="";
	private String mMARate = "";
	private String mMAFOLIssueDate ="";
	private String mMAFOLPeriod ="";
	private String mMAPftRate ="";
	private String baseRateCode ="";
	private String mMAMinRate = "";
	private String mMAMargin ="";
	private String mMAProfitPeriod ="";
	private String mMAMinAmount = "";
	private String mMANumberOfTerms ="";
	private String mMAMLatePayRate ="";
	private String mMALatePayRate ="";
	private String mMAMaturityDate ="";
	

	//==========Equipment Loan Detail==========//
	
	private String machineName ="";
	private String machinePurpose ="";
	private String machineAge ="";
	private String manufacturer ="";
	private String eqpmentLocation ="";
	
	

	
	public String getMachineName() {
		return machineName;
	}

	public void setMachineName(String machineName) {
		this.machineName = machineName;
	}

	public String getMachinePurpose() {
		return machinePurpose;
	}

	public void setMachinePurpose(String machinePurpose) {
		this.machinePurpose = machinePurpose;
	}

	public String getMachineAge() {
		return machineAge;
	}

	public void setMachineAge(String machineAge) {
		this.machineAge = machineAge;
	}

	public String getManufacturer() {
		return manufacturer;
	}

	public void setManufacturer(String manufacturer) {
		this.manufacturer = manufacturer;
	}

	public String getmMAContractAmt() {
		return mMAContractAmt;
	}

	public void setmMAContractAmt(String mMAContractAmt) {
		this.mMAContractAmt = mMAContractAmt;
	}

	public String getmMAPurchaddress() {
		return mMAPurchaddress;
	}

	public void setmMAPurchaddress(String mMAPurchaddress) {
		this.mMAPurchaddress = mMAPurchaddress;
	}

	public String getmMAFax() {
		return mMAFax;
	}

	public void setmMAFax(String mMAFax) {
		this.mMAFax = mMAFax;
	}

	public String getmMARate() {
		return mMARate;
	}

	public void setmMARate(String mMARate) {
		this.mMARate = mMARate;
	}

	public String getmMAPurchRegOffice() {
	    return mMAPurchRegOffice;
    }

	public void setmMAPurchRegOffice(String mMAPurchRegOffice) {
	    this.mMAPurchRegOffice = mMAPurchRegOffice;
    }

	public String getmMAFOLIssueDate() {
	    return mMAFOLIssueDate;
    }

	public void setmMAFOLIssueDate(String mMAFOLIssueDate) {
	    this.mMAFOLIssueDate = mMAFOLIssueDate;
    }

	public String getmMAFOLPeriod() {
	    return mMAFOLPeriod;
    }

	public void setmMAFOLPeriod(String mMAFOLPeriod) {
	    this.mMAFOLPeriod = mMAFOLPeriod;
    }
	public String getmMAPftRate() {
		return mMAPftRate;
	}

	public void setmMAPftRate(String mMAPftRate) {
		this.mMAPftRate = mMAPftRate;
	}

	public String getmMAMinRate() {
		return mMAMinRate;
	}

	public void setmMAMinRate(String mMAMinRate) {
		this.mMAMinRate = mMAMinRate;
	}

	public String getmMAMargin() {
		return mMAMargin;
	}

	public void setmMAMargin(String mMAMargin) {
		this.mMAMargin = mMAMargin;
	}

	public String getmMAProfitPeriod() {
		return mMAProfitPeriod;
	}

	public void setmMAProfitPeriod(String mMAProfitPeriod) {
		this.mMAProfitPeriod = mMAProfitPeriod;
	}

	public String getmMAMinAmount() {
		return mMAMinAmount;
	}

	public void setmMAMinAmount(String mMAMinAmount) {
		this.mMAMinAmount = mMAMinAmount;
	}

	public String getmMANumberOfTerms() {
		return mMANumberOfTerms;
	}

	public void setmMANumberOfTerms(String mMANumberOfTerms) {
		this.mMANumberOfTerms = mMANumberOfTerms;
	}

	public String getAttention() {
		return attention;
	}

	public void setAttention(String attention) {
		this.attention = attention;
	}

	public String getCustaddress() {
		return custaddress;
	}

	public void setCustaddress(String custaddress) {
		this.custaddress = custaddress;
	}

	public String getStartDate() {
		return startDate;
	}

	public void setStartDate(String startDate) {
		this.startDate = startDate;
	}

	public String getInitiatedDate() {
		return initiatedDate;
	}

	public void setInitiatedDate(String initiatedDate) {
		this.initiatedDate = initiatedDate;
	}

	public String getTotalPriPaid() {
		return totalPriPaid;
	}

	public void setTotalPriPaid(String totalPriPaid) {
		this.totalPriPaid = totalPriPaid;
	}

	public String getCurInstDate() {
		return curInstDate;
	}

	public void setCurInstDate(String curInstDate) {
		this.curInstDate = curInstDate;
	}

	public String getNextInstDate() {
		return nextInstDate;
	}

	public void setNextInstDate(String nextInstDate) {
		this.nextInstDate = nextInstDate;
	}

	public String getAdvPftRate() {
		return advPftRate;
	}

	public void setAdvPftRate(String advPftRate) {
		this.advPftRate = advPftRate;
	}

	public String getAdvBaseRate() {
		return advBaseRate;
	}

	public void setAdvBaseRate(String advBaseRate) {
		this.advBaseRate = advBaseRate;
	}

	public String getAdvMargin() {
		return advMargin;
	}

	public void setAdvMargin(String advMargin) {
		this.advMargin = advMargin;
	}

	public String getAdvProfit() {
		return advProfit;
	}

	public void setAdvProfit(String advProfit) {
		this.advProfit = advProfit;
	}

	public String getLatePayRate() {
		return latePayRate;
	}

	public void setLatePayRate(String latePayRate) {
		this.latePayRate = latePayRate;
	}

	public String getNextInstlPft() {
		return nextInstlPft;
	}

	public void setNextInstlPft(String nextInstlPft) {
		this.nextInstlPft = nextInstlPft;
	}

	public String getNoOfDays() {
		return noOfDays;
	}

	public void setNoOfDays(String noOfDays) {
		this.noOfDays = noOfDays;
	}

	/*
	 * Remove fields as not required comments from Raju
	 * 
	 * public String getDealerCountry() {
		return dealerCountry;
	}

	public void setDealerCountry(String dealerCountry) {
		this.dealerCountry = dealerCountry;
	}

	

	public String getAssetValue() {
		return assetValue;
	}

	public void setAssetValue(String assetValue) {
		this.assetValue = assetValue;
	}

	public String getDealercompN() {
		return dealercompN;
	}

	public void setDealercompN(String dealercompN) {
		this.dealercompN = dealercompN;
	}

	public String getFinAmtPerAssetValue() {
		return finAmtPerAssetValue;
	}

	public void setFinAmtPerAssetValue(String finAmtPerAssetValue) {
		this.finAmtPerAssetValue = finAmtPerAssetValue;
	}

	public String getSharePerFinAmtAssetValue() {
		return sharePerFinAmtAssetValue;
	}

	public void setSharePerFinAmtAssetValue(String sharePerFinAmtAssetValue) {
		this.sharePerFinAmtAssetValue = sharePerFinAmtAssetValue;
	}

	public String getDealerPOBox() {
		return dealerPOBox;
	}

	public void setDealerPOBox(String dealerPOBox) {
		this.dealerPOBox = dealerPOBox;
	}

	public String getLpoDate() {
		return lpoDate;
	}

	public void setLpoDate(String lpoDate) {
		this.lpoDate = lpoDate;
	}

	public String getTotalPrice() {
		return totalPrice;
	}

	public void setTotalPrice(String totalPrice) {
		this.totalPrice = totalPrice;
	}*/

	public String getCommodityDesc() {
		return commodityDesc;
	}

	public void setCommodityDesc(String commodityDesc) {
		this.commodityDesc = commodityDesc;
	}

	public String getBrokerName() {
		return brokerName;
	}

	public void setBrokerName(String brokerName) {
		this.brokerName = brokerName;
	}

	public String getOsPri() {
		return osPri;
	}

	public void setOsPri(String osPri) {
		this.osPri = osPri;
	}

	public String getComName() {
		return comName;
	}

	public void setComName(String comName) {
		this.comName = comName;
	}

	public String getSharePerc() {
		return sharePerc;
	}

	public void setSharePerc(String sharePerc) {
		this.sharePerc = sharePerc;
	}

	public String getPerofPri() {
		return perofPri;
	}

	public void setPerofPri(String perofPri) {
		this.perofPri = perofPri;
	}

	public String getfIDate() {
		return fIDate;
	}

	public void setfIDate(String fIDate) {
		this.fIDate = fIDate;
	}

	public String getSchAdvRate() {
		return SchAdvRate;
	}

	public void setSchAdvRate(String schAdvRate) {
		SchAdvRate = schAdvRate;
	}

	public String getSchdInst() {
		return schdInst;
	}

	public void setSchdInst(String schdInst) {
		this.schdInst = schdInst;
	}

	public String getOsPriAmount() {
		return osPriAmount;
	}

	public void setOsPriAmount(String osPriAmount) {
		this.osPriAmount = osPriAmount;
	}

	public String getSchPriAmtPerBank() {
		return SchPriAmtPerBank;
	}

	public void setSchPriAmtPerBank(String schPriAmtPerBank) {
		SchPriAmtPerBank = schPriAmtPerBank;
	}

	public String getSchPriAmtPerMContr() {
		return SchPriAmtPerMContr;
	}

	public void setSchPriAmtPerMContr(String schPriAmtPerMContr) {
		SchPriAmtPerMContr = schPriAmtPerMContr;
	}

	public String getSchPriAmtPerSContr() {
		return SchPriAmtPerSContr;
	}

	public void setSchPriAmtPerSContr(String schPriAmtPerSContr) {
		SchPriAmtPerSContr = schPriAmtPerSContr;
	}

	public String getActualInstlpft() {
		return actualInstlpft;
	}

	public void setActualInstlpft(String actualInstlpft) {
		this.actualInstlpft = actualInstlpft;
	}

	public String getPftDiffRates() {
		return PftDiffRates;
	}

	public void setPftDiffRates(String pftDiffRates) {
		PftDiffRates = pftDiffRates;
	}

	public String getPerPriPaidCustinstl() {
		return perPriPaidCustinstl;
	}

	public void setPerPriPaidCustinstl(String perPriPaidCustinstl) {
		this.perPriPaidCustinstl = perPriPaidCustinstl;
	}

	public String getPriAmtCurtInst() {
		return priAmtCurtInst;
	}

	public void setPriAmtCurtInst(String priAmtCurtInst) {
		this.priAmtCurtInst = priAmtCurtInst;
	}

	public String getoDInsDate() {
		return oDInsDate;
	}

	public void setoDInsDate(String oDInsDate) {
		this.oDInsDate = oDInsDate;
	}

	public String getEmiRate() {
		return emiRate;
	}

	public void setEmiRate(String emiRate) {
		this.emiRate = emiRate;
	}

	public String getPrvInstDate() {
		return prvInstDate;
	}

	public void setPrvInstDate(String prvInstDate) {
		this.prvInstDate = prvInstDate;
	}

	public String getDefPayDate() {
		return defPayDate;
	}

	public void setDefPayDate(String defPayDate) {
		this.defPayDate = defPayDate;
	}

	public String getFacilityPftRate() {
		return facilityPftRate;
	}

	public void setFacilityPftRate(String facilityPftRate) {
		this.facilityPftRate = facilityPftRate;
	}

	public String getFacilityBaseRate() {
		return facilityBaseRate;
	}

	public void setFacilityBaseRate(String facilityBaseRate) {
		this.facilityBaseRate = facilityBaseRate;
	}

	public String getFacilityMargin() {
		return facilityMargin;
	}

	public void setFacilityMargin(String facilityMargin) {
		this.facilityMargin = facilityMargin;
	}

	public String getFacilityMinRate() {
		return facilityMinRate;
	}

	public void setFacilityMinRate(String facilityMinRate) {
		this.facilityMinRate = facilityMinRate;
	}

	public String getFacilityPeriod() {
		return facilityPeriod;
	}

	public void setFacilityPeriod(String facilityPeriod) {
		this.facilityPeriod = facilityPeriod;
	}

	public String getFacilityMaturity() {
		return facilityMaturity;
	}

	public void setFacilityMaturity(String facilityMaturity) {
		this.facilityMaturity = facilityMaturity;
	}

	public String getFacilityMinAmount() {
		return facilityMinAmount;
	}

	public void setFacilityMinAmount(String facilityMinAmount) {
		this.facilityMinAmount = facilityMinAmount;
	}

	public String getFacilityLatePayRate() {
		return facilityLatePayRate;
	}

	public void setFacilityLatePayRate(String facilityLatePayRate) {
		this.facilityLatePayRate = facilityLatePayRate;
	}

	public String getFacilityCcy() {
		return facilityCcy;
	}

	public void setFacilityCcy(String facilityCcy) {
		this.facilityCcy = facilityCcy;
	}

	public String getFacilityAmount() {
		return facilityAmount;
	}

	public void setFacilityAmount(String facilityAmount) {
		this.facilityAmount = facilityAmount;
	}

	public String getFolReference() {
		return folReference;
	}

	public void setFolReference(String folReference) {
		this.folReference = folReference;
	}

	public String getFacilityMaxCapRate() {
		return facilityMaxCapRate;
	}

	public void setFacilityMaxCapRate(String facilityMaxCapRate) {
		this.facilityMaxCapRate = facilityMaxCapRate;
	}

	public String getFacilityMinCapRate() {
		return facilityMinCapRate;
	}

	public void setFacilityMinCapRate(String facilityMinCapRate) {
		this.facilityMinCapRate = facilityMinCapRate;
	}

	public String getNoOfContracts() {
		return noOfContracts;
	}

	public void setNoOfContracts(String noOfContracts) {
		this.noOfContracts = noOfContracts;
	}

	
	public String getBaseRateCode() {
		return baseRateCode;
	}

	public void setBaseRateCode(String baseRateCode) {
		this.baseRateCode = baseRateCode;
	}

	
	public String getAvlPerDays() {
		return avlPerDays;
	}

	public void setAvlPerDays(String avlPerDays) {
		this.avlPerDays = avlPerDays;
	}

	public String getMaxCapProfitRate() {
		return maxCapProfitRate;
	}

	public void setMaxCapProfitRate(String maxCapProfitRate) {
		this.maxCapProfitRate = maxCapProfitRate;
	}

	public String getMinCapRate() {
		return minCapRate;
	}

	public void setMinCapRate(String minCapRate) {
		this.minCapRate = minCapRate;
	}

	public String getFacOfferLetterDate() {
		return facOfferLetterDate;
	}

	public void setFacOfferLetterDate(String facOfferLetterDate) {
		this.facOfferLetterDate = facOfferLetterDate;
	}

	public String getNumOfContracts() {
		return numOfContracts;
	}

	public void setNumOfContracts(String numOfContracts) {
		this.numOfContracts = numOfContracts;
	}

	public String getPmaryRelOfficer() {
		return pmaryRelOfficer;
	}

	public void setPmaryRelOfficer(String pmaryRelOfficer) {
		this.pmaryRelOfficer = pmaryRelOfficer;
	}

	public String getCustAccount() {
		return custAccount;
	}

	public void setCustAccount(String custAccount) {
		this.custAccount = custAccount;
	}

	public String getAvlPeriodInWords() {
		return avlPeriodInWords;
	}

	public void setAvlPeriodInWords(String avlPeriodInWords) {
		this.avlPeriodInWords = avlPeriodInWords;
	}

	public String getFacLimitInWords() {
		return facLimitInWords;
	}

	public void setFacLimitInWords(String facLimitInWords) {
		this.facLimitInWords = facLimitInWords;
	}

	public String getLeasePeriodInWords() {
		return leasePeriodInWords;
	}

	public void setLeasePeriodInWords(String leasePeriodInWords) {
		this.leasePeriodInWords = leasePeriodInWords;
	}

	public String getOfficePOBox() {
		return officePOBox;
	}

	public void setOfficePOBox(String officePOBox) {
		this.officePOBox = officePOBox;
	}

	public String getOfficeEmirate() {
		return OfficeEmirate;
	}

	public void setOfficeEmirate(String officeEmirate) {
		OfficeEmirate = officeEmirate;
	}

	public String getLovDescModelDesc() {
		return lovDescModelDesc;
	}

	public void setLovDescModelDesc(String lovDescModelDesc) {
		this.lovDescModelDesc = lovDescModelDesc;
	}

	public String getLovDescCarColorName() {
		return lovDescCarColorName;
	}

	public void setLovDescCarColorName(String lovDescCarColorName) {
		this.lovDescCarColorName = lovDescCarColorName;
	}

	public String getTotPriAmount() {
		return totPriAmount;
	}

	public void setTotPriAmount(String totPriAmount) {
		this.totPriAmount = totPriAmount;
	}

	public String getAssetDesc() {
		return assetDesc;
	}

	public void setAssetDesc(String assetDesc) {
		this.assetDesc = assetDesc;
	}

	public String getMiaDate() {
		return miaDate;
	}

	public void setMiaDate(String miaDate) {
		this.miaDate = miaDate;
	}

	public String getAssetDetail() {
		return AssetDetail;
	}

	public void setAssetDetail(String assetDetail) {
		AssetDetail = assetDetail;
	}

	public String getBankAddr() {
		return bankAddr;
	}

	public void setBankAddr(String bankAddr) {
		this.bankAddr = bankAddr;
	}

	public String getDD() {
		return DD;
	}

	public void setDD(String dD) {
		DD = dD;
	}

	public String getMM() {
		return MM;
	}

	public void setMM(String mM) {
		MM = mM;
	}

	public String getYY() {
		return YY;
	}

	public void setYY(String yY) {
		YY = yY;
	}

	public String getAvlPeriodText() {
		return avlPeriodText;
	}

	public void setAvlPeriodText(String avlPeriodText) {
		this.avlPeriodText = avlPeriodText;
	}

	public String getGoodsquanity() {
		return goodsquanity;
	}

	public void setGoodsquanity(String goodsquanity) {
		this.goodsquanity = goodsquanity;
	}

	public String getGoodsType() {
		return goodsType;
	}

	public void setGoodsType(String goodsType) {
		this.goodsType = goodsType;
	}

	public String getGoodsSpec() {
		return goodsSpec;
	}

	public void setGoodsSpec(String goodsSpec) {
		this.goodsSpec = goodsSpec;
	}

	private String avlPerDays ="";
	private String maxCapProfitRate ="";
	private String minCapRate = "";
	private String facOfferLetterDate ="";
	private String numOfContracts ="";
	private String pmaryRelOfficer = "";
private String custAccount ="";
	
	private String avlPeriodInWords ="";
	private String facLimitInWords ="";
	private String leasePeriodInWords ="";
	private String leasePerInDays ="";
	private String leaseTermsWords ="";

   // extra fields added by me 
	private String officePOBox ="";
	private String OfficeEmirate ="";
	private String lovDescModelDesc ="";
	private String lovDescCarColorName ="";
	private String totPriAmount ="";
	private String assetDesc ="";
	private String miaDate ="";
	private String AssetDetail ="";
	private String bankAddr ="";				//Bank Address
	private String DD ="";
	private String MM ="";
	private String YY ="";
	
	//Text
	private String avlPeriodText ="";
	
	
	
	
	
	// new Tabel creation 
	private String goodsquanity ="";
	private String goodsType ="";
	private String goodsSpec ="";
	private String unitPrice ="";
	
	
	
	//=============Check List Details===========//
	
	private List<CheckListDetails> checkListDetails;
	
	public AgreementDetail() {
		
	}
	
	public List<CheckListDetails> getCheckListDetails() {
	    return checkListDetails;
    }
	public void setCheckListDetails(List<CheckListDetails> checkListDetails) {
	    this.checkListDetails = checkListDetails;
    }
	
	public class CheckListDetails {
		
		private long questionId;
		private String question ="";
		private List<CheckListAnsDetails> listquestionAns;

		public CheckListDetails() {
			
		}
		
		public String getQuestion() {
			return StringUtils.trimToEmpty(question);
		}
		public void setQuestion(String question) {
			this.question = question;
		}

		public void setQuestionId(long questionId) {
			this.questionId = questionId;
		}
		public long getQuestionId() {
			return questionId;
		}

		public List<CheckListAnsDetails> getListquestionAns() {
			return listquestionAns;
		}
		public void setListquestionAns(List<CheckListAnsDetails> listquestionAns) {
			this.listquestionAns = listquestionAns;
		}

	}

	public class CheckListAnsDetails {
		
		private long questionId;
		private String questionAns ="";
		private String questionRem ="";

		public CheckListAnsDetails() {
			
		}
		
		public void setQuestionId(long questionId) {
			this.questionId = questionId;
		}
		public long getQuestionId() {
			return questionId;
		}

		public String getQuestionAns() {
			return StringUtils.trimToEmpty(questionAns);
		}
		public void setQuestionAns(String questionAns) {
			this.questionAns = questionAns;
		}

		public String getQuestionRem() {
			return StringUtils.trimToEmpty(questionRem);
		}
		public void setQuestionRem(String questionRem) {
			this.questionRem = questionRem;
		}
	}
	
	//======== Take over details====================//
	
	private String takeOverBankName ="";
	private String takeOverProduct ="";
	private String takeOverAmount ="";
	private String takeOverAmountWords ="";
	private String takeOverRate ="";
	private String takeOverStartDate ="";
	private String takeOverMaturityDate ="";
	
	
	//============== Guarantor Details =============//
	
	private String guarantName ="";
	private String guarantHouseNo ="";
	private String guarantStreet ="";
	private String guarantPO ="";
	private String guarantCountry ="";
	private String guarantProvince ="";
	
	//=============== End ======================//



	/*// Unused

	private String basicSalary;
	private String fixedAllowance;
	private String varAllowance;
	private String totalDeposits6;
	private String totalWD6;
	private String monthlyAvgdeposit6;
	private String monthlyAvgWD6;
	private String marginIncome;
	private String mrgInper;
	private String totalOtherIncome;
	private String mrgOper;
	private String marginOthIncome;
	private String grossBusinessinc;
	private String carSpecifications;
	private String dayofWeek;
	
	private String dayOfContarctDate = "";
	private String monthOfContarctDate = "";
	private String yearOfContarctDate = "";
	private String custSalutation = "";
	private String custFullName = "";

	private String agentName = "";
	private String agentAddr1 = "";
	private String agentAddr2 = "";
	private String agentCity = "";
	private String agentCountry = "";
	private String disbursementAmt;
	private String financeStartDate;
	private String disbursementAccount;	
*/
	//============== Finance Collaterals Details =============//
	private List<FinCollaterals> collateralData;
	public class FinCollaterals {
		private String collateralType ="";
		private String reference ="";
		private String depositorName="";
		private String collateralAmt ="";
		private String collateralBankAmt ="";
		private String colDesc ="";
		private String colAddrCity ="";
		private String colLtv ="";
		private List<ExtendedDetailCollateral> extendedDetailsList=null;
		public String getReference() {
			return reference;
		}
		public void setReference(String reference) {
			this.reference = reference;
		}
		
		public String getDepositorName() {
			return depositorName;
		}
		public void setDepositorName(String depositorName) {
			this.depositorName = depositorName;
		}
		public String getCollateralType() {
			return collateralType;
		}
		public void setCollateralType(String collateralType) {
			this.collateralType = collateralType;
		}
		public String getCollateralAmt() {
			return collateralAmt;
		}
		public void setCollateralAmt(String collateralAmt) {
			this.collateralAmt = collateralAmt;
		}
		public String getColDesc() {
			return colDesc;
		}
		public void setColDesc(String colDesc) {
			this.colDesc = colDesc;
		}
		public String getColAddrCity() {
			return colAddrCity;
		}
		public void setColAddrCity(String colAddrCity) {
			this.colAddrCity = colAddrCity;
		}
		public String getColLtv() {
			return colLtv;
		}
		public void setColLtv(String colLtv) {
			this.colLtv = colLtv;
		}
		public String getCollateralBankAmt() {
			return collateralBankAmt;
		}
		public void setCollateralBankAmt(String collateralBankAmt) {
			this.collateralBankAmt = collateralBankAmt;
		}
		public List<ExtendedDetailCollateral> getExtendedDetailsList() {
			return extendedDetailsList;
		}
		public void setExtendedDetailsList(List<ExtendedDetailCollateral> extendedDetailsList) {
			this.extendedDetailsList = extendedDetailsList;
		}
	}
	
	private ExtendedDetailCollateral edc;
	
	public ExtendedDetailCollateral getEdc() {
		return edc;
	}

	public void setEdc(ExtendedDetailCollateral edc) {
		this.edc = edc;
	}

	public class ExtendedDetailCollateral{
		private String id="";
		private String colType="";
		private List<ExtendedDetail> extDtls;
		public String getId() {
			return id;
		}
		public void setId(String id) {
			this.id = id;
		}
		public List<ExtendedDetail> getExtDtls() {
			return extDtls;
		}
		public void setExtDtls(List<ExtendedDetail> extDtls) {
			this.extDtls = extDtls;
		}
		public String getColType() {
			return colType;
		}
		public void setColType(String colType) {
			this.colType = colType;
		}
	}
	
	private List<FinanceScheduleDetail> scheduleData;
	
	public class FinanceScheduleDetail {
		private String schdPri ="";
		private String schdPft ="";
		private String curSchPriAmount ="";     		
		private String schDate ="";
		private String schTotalPriAmt ="";				//Payment Amount (AED)
		private String schdSeqNo ="";				
		private String closingBalance ="";				//closing Balance(AED)
		private String insSchd ="";			    	//Insurance Fee
		private String suplRent ="";
		private String schAdvPft ="";
		private String advPayment ="";					// Advance Payment
		
		
		public String getCurSchPriAmount() {
			return curSchPriAmount;
		}
		public void setCurSchPriAmount(String curSchPriAmount) {
			this.curSchPriAmount = curSchPriAmount;
		}
		
		public String getSchdSeqNo() {
			return schdSeqNo;
		}
		public void setSchdSeqNo(String schdSeqNo) {
			this.schdSeqNo = schdSeqNo;
		}
		public String getSchDate() {
			return schDate;
		}
		public void setSchDate(String schDate) {
			this.schDate = schDate;
		}
		public String getSchdPri() {
			return schdPri;
		}
		public void setSchdPri(String schdPri) {
			this.schdPri = schdPri;
		}
		public String getSchdPft() {
			return schdPft;
		}
		public void setSchdPft(String schdPft) {
			this.schdPft = schdPft;
		}
		public String getClosingBalance() {
			return closingBalance;
		}
		public void setClosingBalance(String closingBalance) {
			this.closingBalance = closingBalance;
		}
		public String getInsSchd() {
			return insSchd;
		}
		public void setInsSchd(String insSchd) {
			this.insSchd = insSchd;
		}
		public String getSuplRent() {
			return suplRent;
		}
		public void setSuplRent(String suplRent) {
			this.suplRent = suplRent;
		}
		public String getSchTotalPriAmt() {
			return schTotalPriAmt;
		}
		public void setSchTotalPriAmt(String schTotalPriAmt) {
			this.schTotalPriAmt = schTotalPriAmt;
		}
		public String getSchAdvPft() {
			return schAdvPft;
		}
		public void setSchAdvPft(String schAdvPft) {
			this.schAdvPft = schAdvPft;
		}
		public String getAdvPayment() {
			return advPayment;
		}
		public void setAdvPayment(String advPayment) {
			this.advPayment = advPayment;
		}
	}
	
	public class GoodLoanDetails {
		private String supplierName = "";
		private String supPOBox = "";
		private String itemType = "";
		private String itemNumber = "";
		private String itemDescription = "";
		private String unitPrice = "";
		private String quantity = "";
		private String placeofDelivery = "";
		private String quoationNbr = "";
		private String purchaseDate = "";
		private String quoationDate = "";
		private String totalCost = "";
		private String finCcy = "";

		public GoodLoanDetails() {

		}

		public String getSupplierName() {
			return StringUtils.trimToEmpty(supplierName);
		}
		public void setSupplierName(String supplierName) {
			this.supplierName = supplierName;
		}

		public String getItemType() {
			return StringUtils.trimToEmpty(itemType);
		}
		public void setItemType(String itemType) {
			this.itemType = itemType;
		}

		public String getItemNumber() {
			return StringUtils.trimToEmpty(itemNumber);
		}
		public void setItemNumber(String itemNumber) {
			this.itemNumber = itemNumber;
		}

		public String getItemDescription() {
			return StringUtils.trimToEmpty(itemDescription);
		}
		public void setItemDescription(String itemDescription) {
			this.itemDescription = itemDescription;
		}

		public String getUnitPrice() {
			return StringUtils.trimToEmpty(unitPrice);
		}
		public void setUnitPrice(String unitPrice) {
			this.unitPrice = unitPrice;
		}

		public String getQuantity() {
			return StringUtils.trimToEmpty(quantity);
		}
		public void setQuantity(String quantity) {
			this.quantity = quantity;
		}
		public String getPlaceofDelivery() {
	        return placeofDelivery;
        }
		public void setPlaceofDelivery(String placeofDelivery) {
	        this.placeofDelivery = placeofDelivery;
        }
		public String getSupPOBox() {
	        return supPOBox;
        }
		public void setSupPOBox(String supPOBox) {
	        this.supPOBox = supPOBox;
        }

		public String getQuoationNbr() {
			return quoationNbr;
		}

		public void setQuoationNbr(String quoationNbr) {
			this.quoationNbr = quoationNbr;
		}

		public String getPurchaseDate() {
			return purchaseDate;
		}

		public void setPurchaseDate(String purchaseDate) {
			this.purchaseDate = purchaseDate;
		}

		public String getQuoationDate() {
			return quoationDate;
		}

		public void setQuoationDate(String quoationDate) {
			this.quoationDate = quoationDate;
		}

		public String getTotalCost() {
			return totalCost;
		}

		public void setTotalCost(String totalCost) {
			this.totalCost = totalCost;
		}

		public String getFinCcy() {
			return finCcy;
		}

		public void setFinCcy(String finCcy) {
			this.finCcy = finCcy;
		}
	}
/*
	public class CommidityLoanDetails {
		private String itemType;
		private String quantity;
		private String unitBuyPrice;
		private String buyAmount;
		private String unitSellPrice;
		private String sellAmount;

		public String getItemType() {
			return StringUtils.trimToEmpty(itemType);
		}
		public void setItemType(String itemType) {
			this.itemType = itemType;
		}

		public String getQuantity() {
			return StringUtils.trimToEmpty(quantity);
		}
		public void setQuantity(String quantity) {
			this.quantity = quantity;
		}

		public String getUnitBuyPrice() {
			return StringUtils.trimToEmpty(unitBuyPrice);
		}
		public void setUnitBuyPrice(String unitBuyPrice) {
			this.unitBuyPrice = unitBuyPrice;
		}

		public String getBuyAmount() {
			return StringUtils.trimToEmpty(buyAmount);
		}
		public void setBuyAmount(String buyAmount) {
			this.buyAmount = buyAmount;
		}

		public String getUnitSellPrice() {
			return StringUtils.trimToEmpty(unitSellPrice);
		}
		public void setUnitSellPrice(String unitSellPrice) {
			this.unitSellPrice = unitSellPrice;
		}

		public String getSellAmount() {
			return StringUtils.trimToEmpty(sellAmount);
		}
		public void setSellAmount(String sellAmount) {
			this.sellAmount = sellAmount;
		}

	}

	public class CustomerIncomeCategory {
		private String incomeCategory;
		private List<CustomerIncomeDetails> customerIncomeDetails;

		public void setCustomerIncomeDetails(List<CustomerIncomeDetails> customerIncomeDetails) {
			this.customerIncomeDetails = customerIncomeDetails;
		}
		public List<CustomerIncomeDetails> getCustomerIncomeDetails() {
			return customerIncomeDetails;
		}

		public String getIncomeCategory() {
			return StringUtils.trimToEmpty(incomeCategory);
		}
		public void setIncomeCategory(String incomeCategory) {
			this.incomeCategory = incomeCategory;
		}

	}

	public class CustomerIncomeDetails {

		private String incomeType;
		private String income;
		private String jointCust;

		public String getIncomeType() {
			return StringUtils.trimToEmpty(incomeType);
		}
		public void setIncomeType(String incomeType) {
			this.incomeType = incomeType;
		}

		public String getIncome() {
			return StringUtils.trimToEmpty(income);
		}
		public void setIncome(String income) {
			this.income = income;
		}

		public String getJointCust() {
			return StringUtils.trimToEmpty(jointCust);
		}
		public void setJointCust(String jointCust) {
			this.jointCust = jointCust;
		}
	}

	public class CustomerCreditReview {
		private String categoryName;
		private List<CustomerCreditReviewDetails> customerCreditReviewDetails = new ArrayList<CustomerCreditReviewDetails>();

		public String getCategoryName() {
			return StringUtils.trimToEmpty(categoryName);
		}
		public void setCategoryName(String categoryName) {
			this.categoryName = categoryName;
		}

		public List<CustomerCreditReviewDetails> getCustomerCreditReviewDetails() {
			return customerCreditReviewDetails;
		}
		public void setCustomerCreditReviewDetails(
		        List<CustomerCreditReviewDetails> customerCreditReviewDetails) {
			this.customerCreditReviewDetails = customerCreditReviewDetails;
		}
	}

	public class CustomerCreditReviewDetails {
		private String subCategoryName;
		private String year1;
		private String year2;
		private String year3;

		public String getSubCategoryName() {
			return StringUtils.trimToEmpty(subCategoryName);
		}
		public void setSubCategoryName(String subCategoryName) {
			this.subCategoryName = subCategoryName;
		}

		public String getYear1() {
			return StringUtils.trimToEmpty(year1);
		}
		public void setYear1(String year1) {
			this.year1 = year1;
		}

		public String getYear2() {
			return StringUtils.trimToEmpty(year2);
		}
		public void setYear2(String year2) {
			this.year2 = year2;
		}

		public String getYear3() {
			return StringUtils.trimToEmpty(year3);
		}
		public void setYear3(String year3) {
			this.year3 = year3;
		}

	}

	public class ScoringHeader {

		public ScoringHeader() {
		}

		private String scoringGroup;
		private List<ScoringDetails> scoringDetails = new ArrayList<ScoringDetails>();

		public String getScoringGroup() {
			return StringUtils.trimToEmpty(scoringGroup);
		}
		public void setScoringGroup(String scoringGroup) {
			this.scoringGroup = scoringGroup;
		}

		public List<ScoringDetails> getScoringDetails() {
			return scoringDetails;
		}
		public void setScoringDetails(List<ScoringDetails> scoringDetails) {
			this.scoringDetails = scoringDetails;
		}
	}

	public class ScoringDetails {

		public ScoringDetails() {
		}

		private String scoringMetric;
		private String scoringDesc;
		private String metricMaxScore;
		private String calcScore;

		public String getScoringMetric() {
			return StringUtils.trimToEmpty(scoringMetric);
		}
		public void setScoringMetric(String scoringMetric) {
			this.scoringMetric = scoringMetric;
		}

		public String getScoringDesc() {
			return StringUtils.trimToEmpty(scoringDesc);
		}
		public void setScoringDesc(String scoringDesc) {
			this.scoringDesc = scoringDesc;
		}

		public String getMetricMaxScore() {
			return StringUtils.trimToEmpty(metricMaxScore);
		}
		public void setMetricMaxScore(String metricMaxScore) {
			this.metricMaxScore = metricMaxScore;
		}

		public String getCalcScore() {
			return StringUtils.trimToEmpty(calcScore);
		}
		public void setCalcScore(String calcScore) {
			this.calcScore = calcScore;
		}

	}*/
	public class GroupRecommendation {
		private String userRole ="";
		private List<Recommendation> recommendations;

		public GroupRecommendation() {
			
		}
		
		public void setUserRole(String userRole) {
			this.userRole = userRole;
		}
		public String getUserRole() {
			return StringUtils.trimToEmpty(userRole);
		}

		public void setRecommendations(List<Recommendation> recommendations) {
			this.recommendations = recommendations;
		}
		public List<Recommendation> getRecommendations() {
			return recommendations;
		}
	}

	public class Recommendation {
		private String userName ="";
		private String userRole ="";
		private String commentedDate ="";
		private String noteType ="";
		private String noteDesc ="";

		public Recommendation() {
			
		}
		
		public String getUserName() {
			return StringUtils.trimToEmpty(userName);
		}
		public void setUserName(String userName) {
			this.userName = userName;
		}

		public String getCommentedDate() {
			return StringUtils.trimToEmpty(commentedDate);
		}
		public void setCommentedDate(String commentedDate) {
			this.commentedDate = commentedDate;
		}

		public String getNoteType() {
			return StringUtils.trimToEmpty(noteType);
		}
		public void setNoteType(String noteType) {
			this.noteType = noteType;
		}

		public String getNoteDesc() {
			return StringUtils.trimToEmpty(noteDesc);
		}
		public String getHtmlNoteDesc() {
			return StringUtils.trimToEmpty(noteDesc);
		}
		public void setNoteDesc(String noteDesc) {
			this.noteDesc = noteDesc;
		}
		
		public void setUserRole(String userRole) {
			this.userRole = userRole;
		}
		public String getUserRole() {
			return StringUtils.trimToEmpty(userRole);
		}
	}

	public class ExceptionList {
		private String exceptionItem ="";
		private String exceptionDesc ="";

		public ExceptionList() {
			
		}
		
		public String getExceptionItem() {
			return StringUtils.trimToEmpty(exceptionItem);
		}
		public void setExceptionItem(String exceptionItem) {
			this.exceptionItem = exceptionItem;
		}

		public String getExceptionDesc() {
			return StringUtils.trimToEmpty(exceptionDesc);
		}
		public void setExceptionDesc(String exceptionDesc) {
			this.exceptionDesc = exceptionDesc;
		}
	}
	
	public class CustomerFinance {
		private String dealDate ="";
		private String dealType ="";
		private String originalAmount ="";
		private String monthlyInstalment ="";
		private String outstandingBalance ="";

		public CustomerFinance() {
			
		}
		
		public String getDealDate() {
			return StringUtils.trimToEmpty(dealDate);
		}
		public void setDealDate(String dealDate) {
			this.dealDate = dealDate;
		}

		public String getDealType() {
			return StringUtils.trimToEmpty(dealType);
		}
		public void setDealType(String dealType) {
			this.dealType = dealType;
		}

		public String getOriginalAmount() {
			return StringUtils.trimToEmpty(originalAmount);
		}
		public void setOriginalAmount(String originalAmount) {
			this.originalAmount = originalAmount;
		}

		public String getMonthlyInstalment() {
			return StringUtils.trimToEmpty(monthlyInstalment);
		}
		public void setMonthlyInstalment(String monthlyInstalment) {
			this.monthlyInstalment = monthlyInstalment;
		}

		public String getOutstandingBalance() {
			return StringUtils.trimToEmpty(outstandingBalance);
		}
		public void setOutstandingBalance(String outstandingBalance) {
			this.outstandingBalance = outstandingBalance;
		}
	}
	
	public String getUserId() {
		return userId;
	}
	public void setUserId(String userId) {
		this.userId = userId;
	}
	
	public String getUserName() {
		return userName;
	}
	public void setUserName(String userName) {
		this.userName = userName;
	}
	
	public String getAppDate() {
		return appDate;
	}
	public void setAppDate(String appDate) {
		this.appDate = appDate;
	}
	
	public String getCustName() {
		return custName;
	}
	public void setCustName(String custName) {
		this.custName = custName;
	}
	
	public String getCustCIF() {
		return custCIF;
	}
	public void setCustCIF(String custCIF) {
		this.custCIF = custCIF;
	}
	
	public String getCustPassport() {
		return custPassport;
	}
	public void setCustPassport(String custPassport) {
		this.custPassport = custPassport;
	}
	
	/*
	 * Remove fields as not required comments from Raju
	 * 
	public String getQuotationNo() {
		return quotationNo;
	}
	public void setQuotationNo(String quotationNo) {
		this.quotationNo = quotationNo;
	}
	
	public String getQuotationDate() {
		return quotationDate;
	}
	public void setQuotationDate(String quotationDate) {
		this.quotationDate = quotationDate;
	}
	
	public String getPaymentMode() {
		return paymentMode;
	}
	public void setPaymentMode(String paymentMode) {
		this.paymentMode = paymentMode;
	}
	
	public String getPurchaseDate() {
		return purchaseDate;
	}
	public void setPurchaseDate(String purchaseDate) {
		this.purchaseDate = purchaseDate;
	}
	
	public String getCarDealer() {
	    return carDealer;
    }
	public void setCarDealer(String carDealer) {
	    this.carDealer = carDealer;
    }
	
	
	public String getDealerName() {
		return dealerName;
	}
	public void setDealerName(String dealerName) {
		this.dealerName = dealerName;
	}
	
	public String getDealerAddr() {
		return dealerAddr;
	}
	public void setDealerAddr(String dealerAddr) {
		this.dealerAddr = dealerAddr;
	}
	
	public String getPurchaseOrder() {
		return purchaseOrder;
	}
	public void setPurchaseOrder(String purchaseOrder) {
		this.purchaseOrder = purchaseOrder;
	}
	
	public String getManfacName() {
		return manfacName;
	}
	public void setManfacName(String manfacName) {
		this.manfacName = manfacName;
	}
	
	public String getModel() {
		return model;
	}
	public void setModel(String model) {
		this.model = model;
	}
	
	public String getModelYear() {
		return modelYear;
	}
	public void setModelYear(String modelYear) {
		this.modelYear = modelYear;
	}
	
	public String getCarColor() {
		return carColor;
	}
	public void setCarColor(String carColor) {
		this.carColor = carColor;
	}
	
	public String getCarChasisNo() {
		return carChasisNo;
	}
	public void setCarChasisNo(String carChasisNo) {
		this.carChasisNo = carChasisNo;
	}
	
	
	
	public String getTpName() {
		return tpName;
	}
	public void setTpName(String tpName) {
		this.tpName = tpName;
	}
	
	public String getTpPassport() {
		return tpPassport;
	}
	public void setTpPassport(String tpPassport) {
		this.tpPassport = tpPassport;
	}
	
	public String getCarCapacity() {
		return carCapacity;
	}
	public void setCarCapacity(String carCapacity) {
		this.carCapacity = carCapacity;
	}
	
	public String getCarRegNo() {
		return carRegNo;
	}
	public void setCarRegNo(String carRegNo) {
		this.carRegNo = carRegNo;
	}*/
	
	public String getCustEmpProf() {
		return custEmpProf;
	}
	public void setCustEmpProf(String custEmpProf) {
		this.custEmpProf = custEmpProf;
	}
	
	public String getPhoneHome() {
		return phoneHome;
	}
	public void setPhoneHome(String phoneHome) {
		this.phoneHome = phoneHome;
	}
	
	public String getCustMobile() {
		return custMobile;
	}
	public void setCustMobile(String custMobile) {
		this.custMobile = custMobile;
	}
	
	public String getCustFax() {
		return custFax;
	}
	public void setCustFax(String custFax) {
		this.custFax = custFax;
	}
	
	public String getRepayAccount() {
		return repayAccount;
	}
	public void setRepayAccount(String repayAccount) {
		this.repayAccount = repayAccount;
	}
	
	public String getFinRef() {
		return finRef;
	}
	public void setFinRef(String finRef) {
		this.finRef = finRef;
	}
	
	public String getFinAmount() {
		return finAmount;
	}
	public void setFinAmount(String finAmount) {
		this.finAmount = finAmount;
	}

	public String getProfit() {
		return profit;
	}
	public void setProfit(String profit) {
		this.profit = profit;
	}
	
	public String getNoOfPayments() {
		return noOfPayments;
	}
	public void setNoOfPayments(String noOfPayments) {
		this.noOfPayments = noOfPayments;
	}
	
	public String getLpoPrice() {
		return lpoPrice;
	}
	public void setLpoPrice(String lpoPrice) {
		this.lpoPrice = lpoPrice;
	}
	
	public String getLpoPriceInWords() {
		return lpoPriceInWords;
	}
	public void setLpoPriceInWords(String lpoPriceInWords) {
		this.lpoPriceInWords = lpoPriceInWords;
	}
	
	public String getRepayFrq() {
		return repayFrq;
	}
	public void setRepayFrq(String repayFrq) {
		this.repayFrq = repayFrq;
	}
	
	public String getFirstInstDate() {
		return firstInstDate;
	}
	public void setFirstInstDate(String firstInstDate) {
		this.firstInstDate = firstInstDate;
	}
	
	public String getLastInstDate() {
		return lastInstDate;
	}
	public void setLastInstDate(String lastInstDate) {
		this.lastInstDate = lastInstDate;
	}
	
	public String getLastInstAmount() {
		return lastInstAmount;
	}
	public void setLastInstAmount(String lastInstAmount) {
		this.lastInstAmount = lastInstAmount;
	}
	
	public String getContractDate() {
		return contractDate;
	}
	public void setContractDate(String contractDate) {
		this.contractDate = contractDate;
	}
	
	public String getDownpayAc() {
		return downpayAc;
	}
	public void setDownpayAc(String downpayAc) {
		this.downpayAc = downpayAc;
	}
	
	/*
	 * Remove fields as not required comments from Raju
	 * 
	 * public String getEmiratesReg() {
	    return emiratesReg;
    }
	public void setEmiratesReg(String emiratesReg) {
	    this.emiratesReg = emiratesReg;
    }*/
	public String getDownPayBank() {
	    return downPayBank;
    }
	public void setDownPayBank(String downPayBank) {
	    this.downPayBank = downPayBank;
    }
	public String getCustDOB() {
	    return custDOB;
    }
	public void setCustDOB(String custDOB) {
	    this.custDOB = custDOB;
    }
	public List<Recommendation> getRecommendations() {
	    return recommendations;
    }
	public void setRecommendations(List<Recommendation> recommendations) {
	    this.recommendations = recommendations;
    }
	public List<GroupRecommendation> getGroupRecommendations() {
	    return groupRecommendations;
    }
	public void setGroupRecommendations(List<GroupRecommendation> groupRecommendations) {
	    this.groupRecommendations = groupRecommendations;
    }
	public List<ExceptionList> getExceptionLists() {
	    return exceptionLists;
    }
	public void setExceptionLists(List<ExceptionList> exceptionLists) {
	    this.exceptionLists = exceptionLists;
    }
	
	public String getFinPurpose() {
		return finPurpose;
	}
	public void setFinPurpose(String finPurpose) {
		this.finPurpose = finPurpose;
	}
	
	public String getCustDSR() {
		return custDSR;
	}
	public void setCustDSR(String custDSR) {
		this.custDSR = custDSR;
	}
	
	/*Remove fields as not required comments from Raju
	 * 
	 * 
	 * 
	public String getSalesPersonName() {
		return salesPersonName;
	}
	public void setSalesPersonName(String salesPersonName) {
		this.salesPersonName = salesPersonName;
	}*/
	
	public List<CustomerFinance> getCustomerFinances() {
		return customerFinances;
	}
	public void setCustomerFinances(List<CustomerFinance> customerFinances) {
		this.customerFinances = customerFinances;
	}
	
	public String getTotCustFin() {
		return totCustFin;
	}
	public void setTotCustFin(String totCustFin) {
		this.totCustFin = totCustFin;
	}
	
	public long getCustId() {
	    return custId;
    }
	public void setCustId(long custId) {
	    this.custId = custId;
    }
	
	
	/*Remove fields as not required comments from Raju
	 * 
	 * 
	public String getTrafficProfileNo() {
	    return trafficProfileNo;
    }
	public void setTrafficProfileNo(String trafficProfileNo) {
	    this.trafficProfileNo = trafficProfileNo;
    }*/

	public String getBankName() {
	    return bankName;
	}
	public void setBankName(String bankName) {
	    this.bankName = bankName;
    }

	public String getAccountType() {
	    return accountType;
    }
	public void setAccountType(String accountType) {
	    this.accountType = accountType;
    }

	public String getIban() {
	    return iban;
    }
	public void setIban(String iban) {
	    this.iban = iban;
    }

	public String getCustIdType() {
	    return custIdType;
    }
	public void setCustIdType(String custIdType) {
	    this.custIdType = custIdType;
    }

	public String getCustIdName() {
	    return custIdName;
    }
	public void setCustIdName(String custIdName) {
	    this.custIdName = custIdName;
    }

	public String getDdaPurposeCode() {
	    return ddaPurposeCode;
    }
	public void setDdaPurposeCode(String ddaPurposeCode) {
	    this.ddaPurposeCode = ddaPurposeCode;
    }

	public String getCustArabicName() {
	    return custArabicName;
    }
	public void setCustArabicName(String custArabicName) {
	    this.custArabicName = custArabicName;
    }

	public String getHoldCertificateNo() {
	    return holdCertificateNo;
    }
	public void setHoldCertificateNo(String holdCertificateNo) {
	    this.holdCertificateNo = holdCertificateNo;
    }

	public String getQuantity() {
	    return quantity;
    }
	public void setQuantity(String quantity) {
	    this.quantity = quantity;
    }

	

	public String getCustRO1() {
	    return custRO1;
    }
	public void setCustRO1(String custRO1) {
	    this.custRO1 = custRO1;
    }

	public String getMMADate() {
	    return mMADate;
    }
	public void setMMADate(String mMADate) {
	    this.mMADate = mMADate;
    }

	public String getCommodityType() {
	    return commodityType;
    }
	public void setCommodityType(String commodityType) {
	    this.commodityType = commodityType;
    }

	public String getDisbAccount() {
	    return disbAccount;
    }
	public void setDisbAccount(String disbAccount) {
	    this.disbAccount = disbAccount;
    }

	public String getNoOfDependents() {
		return noOfDependents;
	}
	public void setNoOfDependents(String noOfDependents) {
		this.noOfDependents = noOfDependents;
	}

	public String getCustSector() {
		return custSector;
	}
	public void setCustSector(String custSector) {
		this.custSector = custSector;
	}

	public String getCustSubSector() {
		return custSubSector;
	}
	public void setCustSubSector(String custSubSector) {
		this.custSubSector = custSubSector;
	}

	public String getCustSegment() {
		return custSegment;
	}
	public void setCustSegment(String custSegment) {
		this.custSegment = custSegment;
	}

	public String getCustIndustry() {
		return custIndustry;
	}
	public void setCustIndustry(String custIndustry) {
		this.custIndustry = custIndustry;
	}

	public String getFinType() {
		return finType;
	}
	public void setFinType(String finType) {
		this.finType = finType;
	}

	public String getFinTypeDesc() {
		return finTypeDesc;
	}
	public void setFinTypeDesc(String finTypeDesc) {
		this.finTypeDesc = finTypeDesc;
	}

	public String getFinDivision() {
		return finDivision;
	}
	public void setFinDivision(String finDivision) {
		this.finDivision = finDivision;
	}

	public String getFinCcy() {
		return finCcy;
	}
	public void setFinCcy(String finCcy) {
		this.finCcy = finCcy;
	}

	public String getPftDaysBasis() {
		return pftDaysBasis;
	}
	public void setPftDaysBasis(String pftDaysBasis) {
		this.pftDaysBasis = pftDaysBasis;
	}

	public String getFinBranch() {
		return finBranch;
	}
	public void setFinBranch(String finBranch) {
		this.finBranch = finBranch;
	}
	public String getFinBranchName() {
		return finBranchName;
	}

	public void setFinBranchName(String lovDescFinBranchName) {
		this.finBranchName = lovDescFinBranchName;
	}
	public String getRepayRateBasis() {
		return repayRateBasis;
	}

	public void setRepayRateBasis(String repayRateBasis) {
		this.repayRateBasis = repayRateBasis;
	}

	public String getFeeChargeAmt() {
		return feeChargeAmt;
	}
	public void setFeeChargeAmt(String feeChargeAmt) {
		this.feeChargeAmt = feeChargeAmt;
	}

	public String getDownPaySupl() {
		return downPaySupl;
	}
	public void setDownPaySupl(String downPaySupl) {
		this.downPaySupl = downPaySupl;
	}

	public String getFinRpyMethod() {
		return finRpyMethod;
	}
	public void setFinRpyMethod(String finRpyMethod) {
		this.finRpyMethod = finRpyMethod;
	}

	public String getFacilityRef() {
		return facilityRef;
	}
	public void setFacilityRef(String facilityRef) {
		this.facilityRef = facilityRef;
	}

	public String getGrcEndDate() {
		return grcEndDate;
	}
	public void setGrcEndDate(String grcEndDate) {
		this.grcEndDate = grcEndDate;
	}

	
	public String getRpyRateBasis() {
		return rpyRateBasis;
	}
	public void setRpyRateBasis(String rpyRateBasis) {
		this.rpyRateBasis = rpyRateBasis;
	}

	public String getFirstInstAmount() {
		return firstInstAmount;
	}
	public void setFirstInstAmount(String firstInstAmount) {
		this.firstInstAmount = firstInstAmount;
	}

	public String getSchdMethod() {
		return schdMethod;
	}
	public void setSchdMethod(String schdMethod) {
		this.schdMethod = schdMethod;
	}

	public String getMaturityDate() {
		return maturityDate;
	}
	public void setMaturityDate(String maturityDate) {
		this.maturityDate = maturityDate;
	}

	public String getAssetType() {
		return assetType;
	}
	public void setAssetType(String assetType) {
		this.assetType = assetType;
	}

	public String getAssetarea() {
		return assetarea;
	}
	public void setAssetarea(String assetarea) {
		this.assetarea = assetarea;
	}

	public String getAssetRegistration() {
		return assetRegistration;
	}
	public void setAssetRegistration(String assetRegistration) {
		this.assetRegistration = assetRegistration;
	}

	public String getDeedno() {
		return deedno;
	}
	public void setDeedno(String deedno) {
		this.deedno = deedno;
	}

	public String getAssetStatus() {
		return assetStatus;
	}
	public void setAssetStatus(String assetStatus) {
		this.assetStatus = assetStatus;
	}

	public String getAssetareainSF() {
		return assetareainSF;
	}
	public void setAssetareainSF(String assetareainSF) {
		this.assetareainSF = assetareainSF;
	}

	public String getAssetage() {
		return assetage;
	}
	public void setAssetage(String assetage) {
		this.assetage = assetage;
	}

	public String getAssetareainSM() {
		return assetareainSM;
	}
	public void setAssetareainSM(String assetareainSM) {
		this.assetareainSM = assetareainSM;
	}

	public String getAssetMarketvle() {
		return assetMarketvle;
	}
	public void setAssetMarketvle(String assetMarketvle) {
		this.assetMarketvle = assetMarketvle;
	}

	public String getAssetPricePF() {
		return assetPricePF;
	}
	public void setAssetPricePF(String assetPricePF) {
		this.assetPricePF = assetPricePF;
	}

	public String getAssetFinRatio() {
		return assetFinRatio;
	}
	public void setAssetFinRatio(String assetFinRatio) {
		this.assetFinRatio = assetFinRatio;
	}

	public String getCustNationality() {
	    return custNationality;
    }
	public void setCustNationality(String custNationality) {
	    this.custNationality = custNationality;
    }

	public String getCustCPRNo() {
	    return custCPRNo;
    }
	public void setCustCPRNo(String custCPRNo) {
	    this.custCPRNo = custCPRNo;
    }

	public String getCustAge() {
	    return custAge;
    }
	public void setCustAge(String custAge) {
	    this.custAge = custAge;
    }

	public String getCustTotIncome() {
	    return custTotIncome;
    }
	public void setCustTotIncome(String custTotIncome) {
	    this.custTotIncome = custTotIncome;
    }

	public String getCustTotExpense() {
	    return custTotExpense;
    }
	public void setCustTotExpense(String custTotExpense) {
	    this.custTotExpense = custTotExpense;
    }

	public String getCustEmpStsCode() {
	    return custEmpStsCode;
    }
	public void setCustEmpStsCode(String custEmpStsCode) {
	    this.custEmpStsCode = custEmpStsCode;
    }

	public String getCustEmpStsDesc() {
	    return custEmpStsDesc;
    }
	public void setCustEmpStsDesc(String custEmpStsDesc) {
	    this.custEmpStsDesc = custEmpStsDesc;
    }

	public String getCustEmpName() {
	    return custEmpName;
    }
	public void setCustEmpName(String custEmpName) {
	    this.custEmpName = custEmpName;
    }

	public String getCustYearsExp() {
	    return custYearsExp;
    }
	public void setCustYearsExp(String custYearsExp) {
	    this.custYearsExp = custYearsExp;
    }

	public String getCustEmpStartDate() {
	    return custEmpStartDate;
    }
	public void setCustEmpStartDate(String custEmpStartDate) {
	    this.custEmpStartDate = custEmpStartDate;
    }

	public String getCustEmail() {
	    return custEmail;
    }
	public void setCustEmail(String custEmail) {
	    this.custEmail = custEmail;
    }
	
	
	/*
	 * Remove fields as not required comments from Raju
	 * 
	 * 
	public String getVehicleType() {
	    return vehicleType;
    }
	public void setVehicleType(String vehicleType) {
	    this.vehicleType = vehicleType;
    }

	public String getVehicleUsage() {
	    return vehicleUsage;
    }
	public void setVehicleUsage(String vehicleUsage) {
	    this.vehicleUsage = vehicleUsage;
    }*/

	public String getCalSchdFeeAmt() {
	    return calSchdFeeAmt;
    }
	public void setCalSchdFeeAmt(String calSchdFeeAmt) {
	    this.calSchdFeeAmt = calSchdFeeAmt;
    }

	
	public String getEffPftRate() {
	    return effPftRate;
    }
	public void setEffPftRate(String effPftRate) {
	    this.effPftRate = effPftRate;
    }

	public String getTakeOverBankName() {
	    return takeOverBankName;
    }

	public void setTakeOverBankName(String takeOverBankName) {
	    this.takeOverBankName = takeOverBankName;
    }

	public String getTakeOverProduct() {
	    return takeOverProduct;
    }

	public void setTakeOverProduct(String takeOverProduct) {
	    this.takeOverProduct = takeOverProduct;
    }

	public String getTakeOverAmount() {
	    return takeOverAmount;
    }

	public void setTakeOverAmount(String takeOverAmount) {
	    this.takeOverAmount = takeOverAmount;
    }

	public String getTakeOverRate() {
	    return takeOverRate;
    }

	public void setTakeOverRate(String takeOverRate) {
	    this.takeOverRate = takeOverRate;
    }

	public String getTakeOverStartDate() {
	    return takeOverStartDate;
    }

	public void setTakeOverStartDate(String takeOverStartDate) {
	    this.takeOverStartDate = takeOverStartDate;
    }

	public String getTakeOverMaturityDate() {
	    return takeOverMaturityDate;
    }

	public void setTakeOverMaturityDate(String takeOverMaturityDate) {
	    this.takeOverMaturityDate = takeOverMaturityDate;
    }

	public String getTakeOverAmountWords() {
	    return takeOverAmountWords;
    }

	public void setTakeOverAmountWords(String takeOverAmountWords) {
	    this.takeOverAmountWords = takeOverAmountWords;
    }

	public String getGuarantName() {
	    return guarantName;
    }

	public void setGuarantName(String guarantName) {
	    this.guarantName = guarantName;
    }

	public String getGuarantHouseNo() {
	    return guarantHouseNo;
    }

	public void setGuarantHouseNo(String guarantHouseNo) {
	    this.guarantHouseNo = guarantHouseNo;
    }

	public String getGuarantStreet() {
	    return guarantStreet;
    }

	public void setGuarantStreet(String guarantStreet) {
	    this.guarantStreet = guarantStreet;
    }

	public String getGuarantPO() {
	    return guarantPO;
    }

	public void setGuarantPO(String guarantPO) {
	    this.guarantPO = guarantPO;
    }

	public String getGuarantCountry() {
	    return guarantCountry;
    }

	public void setGuarantCountry(String guarantCountry) {
	    this.guarantCountry = guarantCountry;
    }

	public String getGuarantProvince() {
	    return guarantProvince;
    }

	public void setGuarantProvince(String guarantProvince) {
	    this.guarantProvince = guarantProvince;
    }

	public String getmMADate() {
		return mMADate;
	}
	public void setmMADate(String mMADate) {
		this.mMADate = mMADate;
	}

	public String getCommodityUnit() {
	    return commodityUnit;
    }
	public void setCommodityUnit(String commodityUnit) {
	    this.commodityUnit = commodityUnit;
    }

	public String getAppTime() {
	    return appTime;
    }

	public void setAppTime(String appTime) {
	    this.appTime = appTime;
    }

	public String getInitiatedBy() {
	    return initiatedBy;
    }

	public void setInitiatedBy(String initiatedBy) {
	    this.initiatedBy = initiatedBy;
    }


	public String getSchPriFirstInstl() {
	    return schPriFirstInstl;
    }

	public void setSchPriFirstInstl(String schPriFirstInstl) {
	    this.schPriFirstInstl = schPriFirstInstl;
    }

	public String getSchdPftFirstInstl() {
	    return schdPftFirstInstl;
    }

	public void setSchdPftFirstInstl(String schdPftFirstInstl) {
	    this.schdPftFirstInstl = schdPftFirstInstl;
    }

	public int getFormatter() {
	    return formatter;
    }

	public void setFormatter(int formatter) {
	    this.formatter = formatter;
    }

	public String getProfitRate() {
	    return profitRate;
    }

	public void setProfitRate(String profitRate) {
	    this.profitRate = profitRate;
    }

	public void setUnitPrice(String unitPrice) {
	    this.unitPrice = unitPrice;
    }
	public String getUnitPrice() {
	    return unitPrice;
    }

	public String getEqpmentLocation() {
	    return eqpmentLocation;
    }

	public void setEqpmentLocation(String eqpmentLocation) {
	    this.eqpmentLocation = eqpmentLocation;
    }

	public String getmMAMLatePayRate() {
	    return mMAMLatePayRate;
    }

	public void setmMAMLatePayRate(String mMAMLatePayRate) {
	    this.mMAMLatePayRate = mMAMLatePayRate;
    }

	/*Remove fields as not required comments from Raju
	 * 
	 * 
	public String getDeliveryOfLoc() {
	    return deliveryOfLoc;
    }
	public void setDeliveryOfLoc(String deliveryOfLoc) {
	    this.deliveryOfLoc = deliveryOfLoc;
    }

	public String getEngineNumber() {
	    return engineNumber;
    }
	public void setEngineNumber(String engineNumber) {
	    this.engineNumber = engineNumber;
    }*/

	public String getmMAFacilityLimit() {
	    return mMAFacilityLimit;
    }

	public void setmMAFacilityLimit(String mMAFacilityLimit) {
	    this.mMAFacilityLimit = mMAFacilityLimit;
    }

	public String getmMAMaturityDate() {
	    return mMAMaturityDate;
    }

	public void setmMAMaturityDate(String mMAMaturityDate) {
	    this.mMAMaturityDate = mMAMaturityDate;
    }

	public String getmMALatePayRate() {
	    return mMALatePayRate;
    }

	public void setmMALatePayRate(String mMALatePayRate) {
	    this.mMALatePayRate = mMALatePayRate;
    }

	public String getLeasePerInDays() {
	    return leasePerInDays;
    }

	public void setLeasePerInDays(String leasePerInDays) {
	    this.leasePerInDays = leasePerInDays;
    }

	public String getLeaseTermsWords() {
	    return leaseTermsWords;
    }

	public void setLeaseTermsWords(String leaseTermsWords) {
	    this.leaseTermsWords = leaseTermsWords;
    }

	/*Remove fields as not required comments from Raju
	 * 
	public String getDealerCity() {
	    return dealerCity;
    }

	public void setDealerCity(String dealerCity) {
	    this.dealerCity = dealerCity;
    }*/

	public List<GoodLoanDetails> getGoodsLoanDetails() {
		return goodsLoanDetails;
	}

	public void setGoodsLoanDetails(List<GoodLoanDetails> goodsLoanDetails) {
		this.goodsLoanDetails = goodsLoanDetails;
	}

	public String getCustOccupation() {
		return custOccupation;
	}

	public void setCustOccupation(String custOccupation) {
		this.custOccupation = custOccupation;
	}

	public String getAdvPayment() {
		return advPayment;
	}

	public void setAdvPayment(String advPayment) {
		this.advPayment = advPayment;
	}
	
	/*Remove fields as not required comments from Raju
	 * 
	 * 
	public String getBondNumFrom() {
		return bondNumFrom;
	}

	public void setBondNumFrom(String bondNumFrom) {
		this.bondNumFrom = bondNumFrom;
	}

	public String getBondNumTo() {
		return bondNumTo;
	}

	public void setBondNumTo(String bondNumTo) {
		this.bondNumTo = bondNumTo;
	}*/

	public String getRepayAmount() {
		return repayAmount;
	}

	public void setRepayAmount(String repayAmount) {
		this.repayAmount = repayAmount;
	}

	public String getInsAmt() {
		return insAmt;
	}

	public void setInsAmt(String insAmt) {
		this.insAmt = insAmt;
	}

	public String getIfscCode() {
		return ifscCode;
	}

	public void setIfscCode(String ifscCode) {
		this.ifscCode = ifscCode;
	}

	public String getDownPayment() {
		return downPayment;
	}

	public void setDownPayment(String downPayment) {
		this.downPayment = downPayment;
	}

	public String getCustJointDOB() {
		return custJointDOB;
	}

	public void setCustJointDOB(String custJointDOB) {
		this.custJointDOB = custJointDOB;
	}

	public String getFinAmountInWords() {
		return finAmountInWords;
	}

	public void setFinAmountInWords(String finAmountInWords) {
		this.finAmountInWords = finAmountInWords;
	}

	/*Remove fields as not required comments from Raju
	 * 
	 * 
	public String getDealerPhone() {
		return dealerPhone;
	}

	public void setDealerPhone(String dealerPhone) {
		this.dealerPhone = dealerPhone;
	}

	public String getDealerFax() {
		return dealerFax;
	}

	public void setDealerFax(String dealerFax) {
		this.dealerFax = dealerFax;
	}*/

	public List<FinanceScheduleDetail> getScheduleData() {
		return scheduleData;
	}

	public void setScheduleData(List<FinanceScheduleDetail> scheduleData) {
		this.scheduleData = scheduleData;
	}

	/*Remove fields as not required comments from Raju
	 * 
	 * 
	 * 
	 * public String getProjectName() {
		return projectName;
	}

	public void setProjectName(String projectName) {
		this.projectName = projectName;
	}

	public String getPropertyCategory() {
		return propertyCategory;
	}

	public void setPropertyCategory(String propertyCategory) {
		this.propertyCategory = propertyCategory;
	}

	public String getUnitAreaInSqftEng() {
		return unitAreaInSqftEng;
	}

	public void setUnitAreaInSqftEng(String unitAreaInSqftEng) {
		this.unitAreaInSqftEng = unitAreaInSqftEng;
	}

	public String getBuildUpAreaEng() {
		return buildUpAreaEng;
	}

	public void setBuildUpAreaEng(String buildUpAreaEng) {
		this.buildUpAreaEng = buildUpAreaEng;
	}*/

	public String getCustJointName() {
		return custJointName;
	}

	public void setCustJointName(String custJointName) {
		this.custJointName = custJointName;
	}

	public List<FinCollaterals> getCollateralData() {
		return collateralData;
	}

	public void setCollateralData(List<FinCollaterals> collateralData) {
		this.collateralData = collateralData;
	}

	/*Remove fields as not required comments from Raju
	 * 
	 * 
	public String getmOUDate() {
		return mOUDate;
	}

	public void setmOUDate(String mOUDate) {
		this.mOUDate = mOUDate;
	}

	public String getMarketValue() {
		return marketValue;
	}

	public void setMarketValue(String marketValue) {
		this.marketValue = marketValue;
	}

	public String getSellerPassport() {
		return sellerPassport;
	}

	public void setSellerPassport(String sellerPassport) {
		this.sellerPassport = sellerPassport;
	}

	public String getPropertyUse() {
		return propertyUse;
	}

	public void setPropertyUse(String propertyUse) {
		this.propertyUse = propertyUse;
	}*/

	public String getTotalRepayAmt() {
		return totalRepayAmt;
	}

	public void setTotalRepayAmt(String totalRepayAmt) {
		this.totalRepayAmt = totalRepayAmt;
	}

	public String getSecSixTermAmt() {
		return secSixTermAmt;
	}

	public void setSecSixTermAmt(String secSixTermAmt) {
		this.secSixTermAmt = secSixTermAmt;
	}

	/*Remove fields as not required comments from Raju
	 * 
	 * 
	 * 
	 * public String getUnitAreaInSqft() {
		return unitAreaInSqft;
	}

	public void setUnitAreaInSqft(String unitAreaInSqft) {
		this.unitAreaInSqft = unitAreaInSqft;
	}*/

	public String getRepayBaseRate() {
		return repayBaseRate;
	}

	public void setRepayBaseRate(String repayBaseRate) {
		this.repayBaseRate = repayBaseRate;
	}

	public String getOtherBankName() {
		return otherBankName;
	}

	public void setOtherBankName(String otherBankName) {
		this.otherBankName = otherBankName;
	}

	public String getOtherBankAmt() {
		return otherBankAmt;
	}

	public void setOtherBankAmt(String otherBankAmt) {
		this.otherBankAmt = otherBankAmt;
	}

	public String getCustContribution() {
		return custContribution;
	}

	public void setCustContribution(String custContribution) {
		this.custContribution = custContribution;
	}

	public String getLiabilityHoldName() {
		return liabilityHoldName;
	}

	public void setLiabilityHoldName(String liabilityHoldName) {
		this.liabilityHoldName = liabilityHoldName;
	}

	public String getSellerContribution() {
		return sellerContribution;
	}

	public void setSellerContribution(String sellerContribution) {
		this.sellerContribution = sellerContribution;
	}

	public String getSellerContInWords() {
		return sellerContInWords;
	}

	public void setSellerContInWords(String sellerContInWords) {
		this.sellerContInWords = sellerContInWords;
	}

	public String getOtherBankAmtInWords() {
		return otherBankAmtInWords;
	}

	public void setOtherBankAmtInWords(String otherBankAmtInWords) {
		this.otherBankAmtInWords = otherBankAmtInWords;
	}

	public String getOtherBankAmtInArabic() {
		return otherBankAmtInArabic;
	}

	public void setOtherBankAmtInArabic(String otherBankAmtInArabic) {
		this.otherBankAmtInArabic = otherBankAmtInArabic;
	}

	public String getLlReferenceNo() {
		return llReferenceNo;
	}

	public void setLlReferenceNo(String llReferenceNo) {
		this.llReferenceNo = llReferenceNo;
	}

	public String getLlDate() {
		return llDate;
	}

	public void setLlDate(String llDate) {
		this.llDate = llDate;
	}

	/*Remove fields as not required comments from Raju
	 * 
	 * 
	 * public String getEmiratesOfReg() {
		return emiratesOfReg;
	}

	public void setEmiratesOfReg(String emiratesOfReg) {
		this.emiratesOfReg = emiratesOfReg;
	}

	public String getPropertyloc() {
		return propertyloc;
	}

	public void setPropertyloc(String propertyloc) {
		this.propertyloc = propertyloc;
	}*/

	public String getCoAppTotIncome() {
		return coAppTotIncome;
	}

	public void setCoAppTotIncome(String coAppTotIncome) {
		this.coAppTotIncome = coAppTotIncome;
	}

	public String getCoAppTotExpense() {
		return coAppTotExpense;
	}

	public void setCoAppTotExpense(String coAppTotExpense) {
		this.coAppTotExpense = coAppTotExpense;
	}

	public String getTotalIncome() {
		return totalIncome;
	}

	public void setTotalIncome(String totalIncome) {
		this.totalIncome = totalIncome;
	}

	public String getTotalExpense() {
		return totalExpense;
	}

	public void setTotalExpense(String totalExpense) {
		this.totalExpense = totalExpense;
	}

	public String getFinAmtPertg() {
		return finAmtPertg;
	}

	public void setFinAmtPertg(String finAmtPertg) {
		this.finAmtPertg = finAmtPertg;
	}

	public String getPurchasePrice() {
		return purchasePrice;
	}

	public void setPurchasePrice(String purchasePrice) {
		this.purchasePrice = purchasePrice;
	}

	public String getRepayMargin() {
		return repayMargin;
	}

	public void setRepayMargin(String repayMargin) {
		this.repayMargin = repayMargin;
	}

	public String getFinReferenceArabic() {
		return finReferenceArabic;
	}

	public void setFinReferenceArabic(String finReferenceArabic) {
		this.finReferenceArabic = finReferenceArabic;
	}

	public String getCustCityArabic() {
		return custCityArabic;
	}

	public void setCustCityArabic(String custCityArabic) {
		this.custCityArabic = custCityArabic;
	}

	public String getPropertyTypeArabic() {
		return propertyTypeArabic;
	}

	public void setPropertyTypeArabic(String propertyTypeArabic) {
		this.propertyTypeArabic = propertyTypeArabic;
	}

	public String getSellerNameArabic() {
		return sellerNameArabic;
	}

	public void setSellerNameArabic(String sellerNameArabic) {
		this.sellerNameArabic = sellerNameArabic;
	}

	public String getCustNationalityArabic() {
		return custNationalityArabic;
	}

	public void setCustNationalityArabic(String custNationalityArabic) {
		this.custNationalityArabic = custNationalityArabic;
	}

	public String getPlotUnitNumberArabic() {
		return plotUnitNumberArabic;
	}

	public void setPlotUnitNumberArabic(String plotUnitNumberArabic) {
		this.plotUnitNumberArabic = plotUnitNumberArabic;
	}

	public String getOtherbankNameArabic() {
		return otherbankNameArabic;
	}

	public void setOtherbankNameArabic(String otherbankNameArabic) {
		this.otherbankNameArabic = otherbankNameArabic;
	}

	public String getSectorOrCommArabic() {
		return sectorOrCommArabic;
	}

	public void setSectorOrCommArabic(String sectorOrCommArabic) {
		this.sectorOrCommArabic = sectorOrCommArabic;
	}

	public String getFinAmountInArabic() {
		return finAmountInArabic;
	}

	public void setFinAmountInArabic(String finAmountInArabic) {
		this.finAmountInArabic = finAmountInArabic;
	}

	public String getProprtyDescArabic() {
		return proprtyDescArabic;
	}

	public void setProprtyDescArabic(String proprtyDescArabic) {
		this.proprtyDescArabic = proprtyDescArabic;
	}

	public String getPropertyLocArabic() {
		return propertyLocArabic;
	}

	public void setPropertyLocArabic(String propertyLocArabic) {
		this.propertyLocArabic = propertyLocArabic;
	}

	public String getUnitAreaInArabic() {
		return unitAreaInArabic;
	}

	public void setUnitAreaInArabic(String unitAreaInArabic) {
		this.unitAreaInArabic = unitAreaInArabic;
	}

	public String getNumOfPayGrace() {
		return numOfPayGrace;
	}

	public void setNumOfPayGrace(String numOfPayGrace) {
		this.numOfPayGrace = numOfPayGrace;
	}

	public String getGraceAvailable() {
		return graceAvailable;
	}

	public void setGraceAvailable(String graceAvailable) {
		this.graceAvailable = graceAvailable;
	}

	public String getTotalTerms() {
		return totalTerms;
	}

	public void setTotalTerms(String totalTerms) {
		this.totalTerms = totalTerms;
	}

	public String getFirstDisbursementAmt() {
		return firstDisbursementAmt;
	}

	public void setFirstDisbursementAmt(String firstDisbursementAmt) {
		this.firstDisbursementAmt = firstDisbursementAmt;
	}

	public String getRepaySplRate() {
		return repaySplRate;
	}

	public void setRepaySplRate(String repaySplRate) {
		this.repaySplRate = repaySplRate;
	}

	public String getSecDeposit() {
		return secDeposit;
	}

	public void setSecDeposit(String secDeposit) {
		this.secDeposit = secDeposit;
	}

	/*Remove fields as not required comments from Raju
	 * 
	 * 
	 * public String getSellerInternal() {
		return sellerInternal;
	}

	public void setSellerInternal(String sellerInternal) {
		this.sellerInternal = sellerInternal;
	}*/

	public String getCustConInWords() {
		return custConInWords;
	}

	public void setCustConInWords(String custConInWords) {
		this.custConInWords = custConInWords;
	}

	public String getUnitAreaInSqftArabic() {
		return unitAreaInSqftArabic;
	}

	public void setUnitAreaInSqftArabic(String unitAreaInSqftArabic) {
		this.unitAreaInSqftArabic = unitAreaInSqftArabic;
	}

	public String getBuildUpAreaArabic() {
		return buildUpAreaArabic;
	}

	public void setBuildUpAreaArabic(String buildUpAreaArabic) {
		this.buildUpAreaArabic = buildUpAreaArabic;
	}

	/*Remove fields as not required comments from Raju
	 * 
	 * 
	 * public String getNumberOfUnits() {
		return numberOfUnits;
	}

	public void setNumberOfUnits(String numberOfUnits) {
		this.numberOfUnits = numberOfUnits;
	}

	public String getMarketValueInWords() {
		return marketValueInWords;
	}
	public void setMarketValueInWords(String marketValueInWords) {
		this.marketValueInWords = marketValueInWords;
	}*/

	public String getSchdAdvPftFirstInstl() {
		return schdAdvPftFirstInstl;
	}
	public void setSchdAdvPftFirstInstl(String schdAdvPftFirstInstl) {
		this.schdAdvPftFirstInstl = schdAdvPftFirstInstl;
	}



	public String getEarlySettleAmt() {
		return earlySettleAmt;
	}
	public void setEarlySettleAmt(String earlySettleAmt) {
		this.earlySettleAmt = earlySettleAmt;
	}

	public String getEarlySettleDate() {
		return earlySettleDate;
	}
	public void setEarlySettleDate(String earlySettleDate) {
		this.earlySettleDate = earlySettleDate;
	}

	public String getCustDocExpDate() {
		return custDocExpDate;
	}

	public void setCustDocExpDate(String custDocExpDate) {
		this.custDocExpDate = custDocExpDate;
	}

	public String getCustDocIdNum() {
		return custDocIdNum;
	}

	public void setCustDocIdNum(String custDocIdNum) {
		this.custDocIdNum = custDocIdNum;
	}

	public String getFacilityAmt() {
		return facilityAmt;
	}

	public void setFacilityAmt(String facilityAmt) {
		this.facilityAmt = facilityAmt;
	}

	public String getSellerInternalArabic() {
		return sellerInternalArabic;
	}

	public void setSellerInternalArabic(String sellerInternalArabic) {
		this.sellerInternalArabic = sellerInternalArabic;
	}

	public String getOtherBankAmtArabic() {
		return otherBankAmtArabic;
	}

	public void setOtherBankAmtArabic(String otherBankAmtArabic) {
		this.otherBankAmtArabic = otherBankAmtArabic;
	}

	public String getCustJointNameArabic() {
		return custJointNameArabic;
	}

	public void setCustJointNameArabic(String custJointNameArabic) {
		this.custJointNameArabic = custJointNameArabic;
	}

	public String getCollateralArabic() {
		return collateralArabic;
	}

	public void setCollateralArabic(String collateralArabic) {
		this.collateralArabic = collateralArabic;
	}

	public String getCollateralAuthArabic() {
		return collateralAuthArabic;
	}

	public void setCollateralAuthArabic(String collateralAuthArabic) {
		this.collateralAuthArabic = collateralAuthArabic;
	}

	public String getPropertyUseArabic() {
		return propertyUseArabic;
	}

	public void setPropertyUseArabic(String propertyUseArabic) {
		this.propertyUseArabic = propertyUseArabic;
	}

	public String getSellerNationalityArabic() {
		return sellerNationalityArabic;
	}

	public void setSellerNationalityArabic(String sellerNationalityArabic) {
		this.sellerNationalityArabic = sellerNationalityArabic;
	}

	public String getSellerCntbAmtArabic() {
		return sellerCntbAmtArabic;
	}

	public void setSellerCntbAmtArabic(String sellerCntbAmtArabic) {
		this.sellerCntbAmtArabic = sellerCntbAmtArabic;
	}

	public String getCustCntAmtArabic() {
		return custCntAmtArabic;
	}

	public void setCustCntAmtArabic(String custCntAmtArabic) {
		this.custCntAmtArabic = custCntAmtArabic;
	}

	public String getFinMaxRate() {
		return finMaxRate;
	}

	public void setFinMaxRate(String finMaxRate) {
		this.finMaxRate = finMaxRate;
	}

	public String getUserDeptName() {
		return userDeptName;
	}

	public void setUserDeptName(String userDeptName) {
		this.userDeptName = userDeptName;
	}

	public String getSellerAddrArabic() {
		return sellerAddrArabic;
	}

	public void setSellerAddrArabic(String sellerAddrArabic) {
		this.sellerAddrArabic = sellerAddrArabic;
	}

	public String getVehicleCC() {
		return VehicleCC;
	}

	public void setVehicleCC(String vehicleCC) {
		VehicleCC = vehicleCC;
	}

	public String getVehicleCategory() {
		return vehicleCategory;
	}

	public void setVehicleCategory(String vehicleCategory) {
		this.vehicleCategory = vehicleCategory;
	}

	public String getTotalExpAmt() {
		return totalExpAmt;
	}

	public void setTotalExpAmt(String totalExpAmt) {
		this.totalExpAmt = totalExpAmt;
	}

	public String getFacilityDate() {
		return facilityDate;
	}

	public void setFacilityDate(String facilityDate) {
		this.facilityDate = facilityDate;
	}

	public String getCustSalutation() {
		return custSalutation;
	}

	public void setCustSalutation(String custSalutation) {
		this.custSalutation = custSalutation;
	}

	public String getFirstInstDays() {
		return firstInstDays;
	}

	public void setFirstInstDays(String firstInstDays) {
		this.firstInstDays = firstInstDays;
	}

	public String getSecondInstDays() {
		return secondInstDays;
	}

	public void setSecondInstDays(String secondInstDays) {
		this.secondInstDays = secondInstDays;
	}

	public String getReviewDate() {
		return reviewDate;
	}

	public void setReviewDate(String reviewDate) {
		this.reviewDate = reviewDate;
	}

	public String getCustGender() {
		return custGender;
	}
	public void setCustGender(String custGender) {
		this.custGender = custGender;
	}

	public String getRepayFrqDay() {
		return repayFrqDay;
	}
	public void setRepayFrqDay(String repayFrqDay) {
		this.repayFrqDay = repayFrqDay;
	}

	/*Remove fields as not required comments from Raju
	 * 
	 * 
	 * public String getVehicleReg() {
		return vehicleReg;
	}

	public void setVehicleReg(String vehicleReg) {
		this.vehicleReg = vehicleReg;
	}*/

	public String getUsrEmailId() {
		return usrEmailId;
	}
	public void setUsrEmailId(String usrEmailId) {
		this.usrEmailId = usrEmailId;
	}

	public String getProfitRateType() {
		return profitRateType;
	}
	public void setProfitRateType(String profitRateType) {
		this.profitRateType = profitRateType;
	}

	public String getCustCtgCode() {
		return custCtgCode;
	}

	public void setCustCtgCode(String custCtgCode) {
		this.custCtgCode = custCtgCode;
	}

	/*Remove fields as not required comments from Raju
	 * 
	 * 
	 * 
	 * public String getNumberOfLeasedUnits() {
		return numberOfLeasedUnits;
	}
	public void setNumberOfLeasedUnits(String numberOfLeasedUnits) {
		this.numberOfLeasedUnits = numberOfLeasedUnits;
	}*/

	public String getRepayFrqCode() {
		return repayFrqCode;
	}
	public void setRepayFrqCode(String repayFrqCode) {
		this.repayFrqCode = repayFrqCode;
	}

	public String getInsuranceAmt() {
		return insuranceAmt;
	}

	public void setInsuranceAmt(String insuranceAmt) {
		this.insuranceAmt = insuranceAmt;
	}
	
	List<CoApplicant> coApplicants;
	
	public List<CoApplicant> getCoApplicants() {
		return coApplicants;
	}

	public void setCoApplicants(List<CoApplicant> coApplicants) {
		this.coApplicants = coApplicants;
	}

	public String getPanNumber() {
		return panNumber;
	}

	public void setPanNumber(String panNumber) {
		this.panNumber = panNumber;
	}


	public String getAccNumberMandate() {
		return StringUtils.trimToEmpty(accNumberMandate);
	}

	public void setAccNumberMandate(String accNumberMandate) {
		this.accNumberMandate = accNumberMandate;
	}

	public String getTenor() {
		return StringUtils.trimToEmpty(tenor);
	}

	public void setTenor(String tenor) {
		this.tenor = tenor;
	}

	public String getNetRefRateLoan() {
		return StringUtils.trimToEmpty(netRefRateLoan);
	}

	public void setNetRefRateLoan(String netRefRateLoan) {
		this.netRefRateLoan = netRefRateLoan;
	}

	public List<CustomerBankInfo> getCustomerBankInfos() {
		return customerBankInfos;
	}

	public void setCustomerBankInfos(List<CustomerBankInfo> customerBankInfos) {
		this.customerBankInfos = customerBankInfos;
	}

	public class CoApplicant {
		private String custCIF = "";
		private String custName ="";
		private String panNumber ="";
		private String Address ="";
		private String custAddrHNbr="";
		private String custFlatNbr="";
		private String custAddrStreet="";
		private String  custRelation="";
		private String custAddrLine1="";
		private String custAddrLine2="";
		private String custPOBox="";
		private String custAddrCountry="";
		private String lovDescCustAddrCountryName="";
		private String custAddrProvince="";
		private String lovDescCustAddrProvinceName="";
		private String custAddrCity ="";
		private String lovDescCustAddrCityName ="";
		private String custAddrZIP ="";
		private String custAddrPhone ="";
		private String custEmail="";
		private String gstNo="";
		private String applicantType="";

		public CoApplicant() {
			
		}

		public String getCustName() {
			return StringUtils.trimToEmpty(custName);
		}

		public void setCustName(String custName) {
			this.custName = custName;
		}

		public String getPanNumber() {
			return StringUtils.trimToEmpty(panNumber);
		}

		public void setPanNumber(String panNumber) {
			this.panNumber = panNumber;
		}

		public String getAddress() {
			return StringUtils.trimToEmpty(Address);
		}

		public void setAddress(String address) {
			Address = address;
		}
		public String getCustRelation() {
			return custRelation;
		}

		public void setCustRelation(String custRelation) {
			this.custRelation = custRelation;
		}
		public String getCustAddrHNbr() {
			return StringUtils.trimToEmpty(custAddrHNbr);
		}

		public void setCustAddrHNbr(String custAddrHNbr) {
			this.custAddrHNbr = custAddrHNbr;
		}

		public String getCustFlatNbr() {
			return StringUtils.trimToEmpty(custFlatNbr);
		}

		public void setCustFlatNbr(String custFlatNbr) {
			this.custFlatNbr = custFlatNbr;
		}

		public String getCustAddrStreet() {
			return StringUtils.trimToEmpty(custAddrStreet);
		}

		public void setCustAddrStreet(String custAddrStreet) {
			this.custAddrStreet = custAddrStreet;
		}

		public String getCustAddrLine1() {
			return StringUtils.trimToEmpty(custAddrLine1);
		}

		public void setCustAddrLine1(String custAddrLine1) {
			this.custAddrLine1 = custAddrLine1;
		}

		public String getCustAddrLine2() {
			return StringUtils.trimToEmpty(custAddrLine2);
		}

		public void setCustAddrLine2(String custAddrLine2) {
			this.custAddrLine2 = custAddrLine2;
		}

		public String getCustPOBox() {
			return StringUtils.trimToEmpty(custPOBox);
		}

		public void setCustPOBox(String custPOBox) {
			this.custPOBox = custPOBox;
		}

		public String getCustAddrCountry() {
			return StringUtils.trimToEmpty(custAddrCountry);
		}

		public void setCustAddrCountry(String custAddrCountry) {
			this.custAddrCountry = custAddrCountry;
		}

		public String getLovDescCustAddrCountryName() {
			return StringUtils.trimToEmpty(lovDescCustAddrCountryName);
		}

		public void setLovDescCustAddrCountryName(String lovDescCustAddrCountryName) {
			this.lovDescCustAddrCountryName = lovDescCustAddrCountryName;
		}

		public String getCustAddrProvince() {
			return StringUtils.trimToEmpty(custAddrProvince);
		}

		public void setCustAddrProvince(String custAddrProvince) {
			this.custAddrProvince = custAddrProvince;
		}

		public String getLovDescCustAddrProvinceName() {
			return StringUtils.trimToEmpty(lovDescCustAddrProvinceName);
		}

		public void setLovDescCustAddrProvinceName(String lovDescCustAddrProvinceName) {
			this.lovDescCustAddrProvinceName = lovDescCustAddrProvinceName;
		}

		public String getCustAddrCity() {
			return StringUtils.trimToEmpty(custAddrCity);
		}

		public void setCustAddrCity(String custAddrCity) {
			this.custAddrCity = custAddrCity;
		}

		public String getLovDescCustAddrCityName() {
			return StringUtils.trimToEmpty(lovDescCustAddrCityName);
		}

		public void setLovDescCustAddrCityName(String lovDescCustAddrCityName) {
			this.lovDescCustAddrCityName = lovDescCustAddrCityName;
		}

		public String getCustAddrZIP() {
			return StringUtils.trimToEmpty(custAddrZIP);
		}

		public void setCustAddrZIP(String custAddrZIP) {
			this.custAddrZIP = custAddrZIP;
		}

		public String getCustAddrPhone() {
			return StringUtils.trimToEmpty(custAddrPhone);
		}

		public void setCustAddrPhone(String custAddrPhone) {
			this.custAddrPhone = custAddrPhone;
		}

		public String getCustEmail() {
			return custEmail;
		}

		public void setCustEmail(String custEmail) {
			this.custEmail = custEmail;
		}

		public String getGstNo() {
			return gstNo;
		}

		public void setGstNo(String gstNo) {
			this.gstNo = gstNo;
		}

		public String getApplicantType() {
			return applicantType;
		}

		public void setApplicantType(String applicantType) {
			this.applicantType = applicantType;
		}

		public String getCustCIF() {
			return custCIF;
		}

		public void setCustCIF(String custCIF) {
			this.custCIF = custCIF;
		}
		
	}
	private List<CustomerBankInfo> customerBankInfos;
 	
	public class CustomerBankInfo {
		private String	bankCode;
		private String	bankName;
		private String	accountNumber;
		private String	accountType;

		public String getBankCode() {
			return bankCode;
		}

		public void setBankCode(String bankCode) {
			this.bankCode = bankCode;
		}

		public String getBankName() {
			return StringUtils.trimToEmpty(bankName);
		}

		public void setBankName(String bankName) {
			this.bankName = bankName;
		}

		public String getAccountNumber() {
			return StringUtils.trimToEmpty(accountNumber);
		}

		public void setAccountNumber(String accountNumber) {
			this.accountNumber = accountNumber;
		}

		public String getAccountType() {
			return StringUtils.trimToEmpty(accountType);
		}

		public void setAccountType(String accountType) {
			this.accountType = accountType;
		}

	}


	//----- New Details Added ---------//
	
	private String effDateFltRate ="";
	
	public String getEffDateFltRate() {
		return effDateFltRate;
	}

	public void setEffDateFltRate(String effDateFltRate) {
		this.effDateFltRate = effDateFltRate;
	}

	//----- Loan Details ---------//
	private String gstNo ="";
	
	public String getGstNo() {
		return gstNo;
	}

	public void setGstNo(String gstNo) {
		this.gstNo = gstNo;
	}
	
	//-------- CAM DEtails Added -----------//
	private String custCategory ="";
	
	private String applicationNo ="";
	
	private String custType ="";
	
	private String custFatherName ="";
	
	private String custEmpType="";
	
	private String custEmpDesignation="";
	
	private String existingCustomer="";
	
	public String getExistingCustomer() {
		return existingCustomer;
	}

	public void setExistingCustomer(String existingCustomer) {
		this.existingCustomer = existingCustomer;
	}

	public String getCustEmpDesignation() {
		return custEmpDesignation;
	}

	public void setCustEmpDesignation(String custEmpDesignation) {
		this.custEmpDesignation = custEmpDesignation;
	}

	public String getCustEmpType() {
		return custEmpType;
	}

	public void setCustEmpType(String custEmpType) {
		this.custEmpType = custEmpType;
	}

	public String getCustFatherName() {
		return custFatherName;
	}

	public void setCustFatherName(String custFatherName) {
		this.custFatherName = custFatherName;
	}

	private String custMaritalStatus ="";
	
	public String getCustMaritalStatus() {
		return custMaritalStatus;
	}

	public void setCustMaritalStatus(String custMaritalStatus) {
		this.custMaritalStatus = custMaritalStatus;
	}

	public String getCustType() {
		return custType;
	}

	public void setCustType(String custType) {
		this.custType = custType;
	}

	public String getCustCategory() {
		return custCategory;
	}

	public void setCustCategory(String custCategory) {
		this.custCategory = custCategory;
	}

	public String getApplicationNo() {
		return applicationNo;
	}

	public void setApplicationNo(String applicationNo) {
		this.applicationNo = applicationNo;
	}

	//----- Customer Repayment Bank Details ---------//
	private String branchName ="";
	private String repayAcct ="";
	private String repayCustName ="";
	private String repayMode ="";
	private String repayBankName ="";
	private String repayAcctIfscCode ="";

	public String getBranchName() {
		return branchName;
	}

	public void setBranchName(String branchName) {
		this.branchName = branchName;
	}

	public String getRepayAcct() {
		return repayAcct;
	}

	public void setRepayAcct(String repayAcct) {
		this.repayAcct = repayAcct;
	}

	public String getRepayCustName() {
		return repayCustName;
	}

	public void setRepayCustName(String repayCustName) {
		this.repayCustName = repayCustName;
	}

	public String getRepayMode() {
		return repayMode;
	}

	public void setRepayMode(String repayMode) {
		this.repayMode = repayMode;
	}
	
    public String getRepayBankName() {
		return repayBankName;
	}

	public void setRepayBankName(String repayBankName) {
		this.repayBankName = repayBankName;
	}

	public String getRepayAcctIfscCode() {
		return repayAcctIfscCode;
	}

	public void setRepayAcctIfscCode(String repayAcctIfscCode) {
		this.repayAcctIfscCode = repayAcctIfscCode;
	}

	//------- Customer Charges -------//
	private List<CusCharge> cusCharges;
	
	public List<CusCharge> getCusCharges() {
		return cusCharges;
	}

	public void setCusCharges(List<CusCharge> cusCharges) {
		this.cusCharges = cusCharges;
	}

	public class CusCharge{
		private String feeChargeDesc ="";
		private String chargeAmt ="";
		private String chargeWaver ="";
		private String chargePaid ="";
		private String remainingAmount ="";
		private String feeTreatment ="";
		
		public CusCharge() {
		}

		public String getFeeChargeDesc() {
			return feeChargeDesc;
		}

		public void setFeeChargeDesc(String feeChargeDesc) {
			this.feeChargeDesc = feeChargeDesc;
		}

		public String getChargeAmt() {
			return chargeAmt;
		}

		public void setChargeAmt(String chargeAmt) {
			this.chargeAmt = chargeAmt;
		}

		public String getChargeWaver() {
			return chargeWaver;
		}

		public void setChargeWaver(String chargeWaver) {
			this.chargeWaver = chargeWaver;
		}

		public String getChargePaid() {
			return chargePaid;
		}

		public void setChargePaid(String chargePaid) {
			this.chargePaid = chargePaid;
		}

		public String getRemainingAmount() {
			return remainingAmount;
		}

		public void setRemainingAmount(String remainingAmount) {
			this.remainingAmount = remainingAmount;
		}

		public String getFeeTreatment() {
			return feeTreatment;
		}

		public void setFeeTreatment(String feeTreatment) {
			this.feeTreatment = feeTreatment;
		}
	}
	
	
	//------------- Disbursement Details ------------// 
	private List<Disbursement> disbursements;
	
	public List<Disbursement> getDisbursements() {
		return disbursements;
	}

	public void setDisbursements(List<Disbursement> disbursements) {
		this.disbursements = disbursements;
	}

	public class Disbursement{
		private String disbursementAmt ="";
		private String accountHolderName ="";
		private String disbursementDate ="";
		private String bankName ="";
		private String disbursementAcct ="";
		private String ifscCode ="";
		private String paymentMode ="";
		private String favoringName = "";
		private String issueBankName = "";
		private String iirReferenceNo = "";
		private String paymentModeRef = "";
		
		private String paymentId = "";
		private String paymentSeq = "";
		private String disbSeq = "";
		private String paymentDetail = "";
		private String custContribution = "";
		private String sellerContribution = "";
		private String remarks = "";
		private String bankCode = "";
		private String branchBankCode = "";
		private String branchCode = "";
		private String branchDesc = "";
		private String city = "";
		private String payableLoc = "";
		private String printingLoc = "";
		private String valueDate = "";
		private String bankBranchID = "";
		private String phoneCountryCode = "";
		private String phoneAreaCode = "";
		private String phoneNumber = "";
		private String clearingDate = "";
		private String status = "";
		private String active = "";
		private String inputDate = "";
		private String disbCCy = "";
		private String pOIssued = "";
		private String lovValue = "";
		private String partnerBankID = "";
		private String partnerbankCode = "";
		private String partnerBankName = "";
		private String finType = "";
		private String custShrtName = "";
		private String linkedTranId = "";
		private String partnerBankAcType = "";
		private String rejectReason = "";
		private String partnerBankAc = "";
		private String alwFileDownload = "";
		private String fileNamePrefix = "";
		private String channel = "";
		private String entityCode = "";
		
		public Disbursement() {
		}

		public String getDisbursementAmt() {
			return disbursementAmt;
		}

		public void setDisbursementAmt(String disbursementAmt) {
			this.disbursementAmt = disbursementAmt;
		}

		public String getAccountHolderName() {
			return accountHolderName;
		}

		public void setAccountHolderName(String accountHolderName) {
			this.accountHolderName = accountHolderName;
		}

		public String getDisbursementDate() {
			return disbursementDate;
		}

		public void setDisbursementDate(String disbursementDate) {
			this.disbursementDate = disbursementDate;
		}

		public String getBankName() {
			return bankName;
		}

		public void setBankName(String bankName) {
			this.bankName = bankName;
		}

		public String getDisbursementAcct() {
			return disbursementAcct;
		}

		public void setDisbursementAcct(String disbursementAcct) {
			this.disbursementAcct = disbursementAcct;
		}

		public String getIfscCode() {
			return ifscCode;
		}

		public void setIfscCode(String ifscCode) {
			this.ifscCode = ifscCode;
		}

		public String getPaymentMode() {
			return paymentMode;
		}

		public void setPaymentMode(String paymentMode) {
			this.paymentMode = paymentMode;
		}

		public String getFavoringName() {
			return favoringName;
		}

		public void setFavoringName(String favoringName) {
			this.favoringName = favoringName;
		}

		public String getIssueBankName() {
			return issueBankName;
		}

		public void setIssueBankName(String issueBankName) {
			this.issueBankName = issueBankName;
		}

		public String getIirReferenceNo() {
			return iirReferenceNo;
		}

		public void setIirReferenceNo(String iirReferenceNo) {
			this.iirReferenceNo = iirReferenceNo;
		}

		public String getPaymentModeRef() {
			return paymentModeRef;
		}

		public void setPaymentModeRef(String paymentModeRef) {
			this.paymentModeRef = paymentModeRef;
		}

		public String getPaymentId() {
			return paymentId;
		}

		public void setPaymentId(String paymentId) {
			this.paymentId = paymentId;
		}

		public String getPaymentSeq() {
			return paymentSeq;
		}

		public void setPaymentSeq(String paymentSeq) {
			this.paymentSeq = paymentSeq;
		}

		public String getDisbSeq() {
			return disbSeq;
		}

		public void setDisbSeq(String disbSeq) {
			this.disbSeq = disbSeq;
		}

		public String getPaymentDetail() {
			return paymentDetail;
		}

		public void setPaymentDetail(String paymentDetail) {
			this.paymentDetail = paymentDetail;
		}

		public String getCustContribution() {
			return custContribution;
		}

		public void setCustContribution(String custContribution) {
			this.custContribution = custContribution;
		}

		public String getSellerContribution() {
			return sellerContribution;
		}

		public void setSellerContribution(String sellerContribution) {
			this.sellerContribution = sellerContribution;
		}

		public String getRemarks() {
			return remarks;
		}

		public void setRemarks(String remarks) {
			this.remarks = remarks;
		}

		public String getBankCode() {
			return bankCode;
		}

		public void setBankCode(String bankCode) {
			this.bankCode = bankCode;
		}

		public String getBranchBankCode() {
			return branchBankCode;
		}

		public void setBranchBankCode(String branchBankCode) {
			this.branchBankCode = branchBankCode;
		}

		public String getBranchCode() {
			return branchCode;
		}

		public void setBranchCode(String branchCode) {
			this.branchCode = branchCode;
		}

		public String getBranchDesc() {
			return branchDesc;
		}

		public void setBranchDesc(String branchDesc) {
			this.branchDesc = branchDesc;
		}

		public String getCity() {
			return city;
		}

		public void setCity(String city) {
			this.city = city;
		}

		public String getPayableLoc() {
			return payableLoc;
		}

		public void setPayableLoc(String payableLoc) {
			this.payableLoc = payableLoc;
		}

		public String getPrintingLoc() {
			return printingLoc;
		}

		public void setPrintingLoc(String printingLoc) {
			this.printingLoc = printingLoc;
		}

		public String getValueDate() {
			return valueDate;
		}

		public void setValueDate(String valueDate) {
			this.valueDate = valueDate;
		}

		public String getBankBranchID() {
			return bankBranchID;
		}

		public void setBankBranchID(String bankBranchID) {
			this.bankBranchID = bankBranchID;
		}

		public String getPhoneCountryCode() {
			return phoneCountryCode;
		}

		public void setPhoneCountryCode(String phoneCountryCode) {
			this.phoneCountryCode = phoneCountryCode;
		}

		public String getPhoneAreaCode() {
			return phoneAreaCode;
		}

		public void setPhoneAreaCode(String phoneAreaCode) {
			this.phoneAreaCode = phoneAreaCode;
		}

		public String getPhoneNumber() {
			return phoneNumber;
		}

		public void setPhoneNumber(String phoneNumber) {
			this.phoneNumber = phoneNumber;
		}

		public String getClearingDate() {
			return clearingDate;
		}

		public void setClearingDate(String clearingDate) {
			this.clearingDate = clearingDate;
		}

		public String getStatus() {
			return status;
		}

		public void setStatus(String status) {
			this.status = status;
		}

		public String getActive() {
			return active;
		}

		public void setActive(String active) {
			this.active = active;
		}

		public String getInputDate() {
			return inputDate;
		}

		public void setInputDate(String inputDate) {
			this.inputDate = inputDate;
		}

		public String getDisbCCy() {
			return disbCCy;
		}

		public void setDisbCCy(String disbCCy) {
			this.disbCCy = disbCCy;
		}

		public String getpOIssued() {
			return pOIssued;
		}

		public void setpOIssued(String pOIssued) {
			this.pOIssued = pOIssued;
		}

		public String getLovValue() {
			return lovValue;
		}

		public void setLovValue(String lovValue) {
			this.lovValue = lovValue;
		}

		public String getPartnerBankID() {
			return partnerBankID;
		}

		public void setPartnerBankID(String partnerBankID) {
			this.partnerBankID = partnerBankID;
		}

		public String getPartnerbankCode() {
			return partnerbankCode;
		}

		public void setPartnerbankCode(String partnerbankCode) {
			this.partnerbankCode = partnerbankCode;
		}

		public String getPartnerBankName() {
			return partnerBankName;
		}

		public void setPartnerBankName(String partnerBankName) {
			this.partnerBankName = partnerBankName;
		}

		public String getFinType() {
			return finType;
		}

		public void setFinType(String finType) {
			this.finType = finType;
		}

		public String getCustShrtName() {
			return custShrtName;
		}

		public void setCustShrtName(String custShrtName) {
			this.custShrtName = custShrtName;
		}

		public String getLinkedTranId() {
			return linkedTranId;
		}

		public void setLinkedTranId(String linkedTranId) {
			this.linkedTranId = linkedTranId;
		}

		public String getPartnerBankAcType() {
			return partnerBankAcType;
		}

		public void setPartnerBankAcType(String partnerBankAcType) {
			this.partnerBankAcType = partnerBankAcType;
		}

		public String getRejectReason() {
			return rejectReason;
		}

		public void setRejectReason(String rejectReason) {
			this.rejectReason = rejectReason;
		}

		public String getPartnerBankAc() {
			return partnerBankAc;
		}

		public void setPartnerBankAc(String partnerBankAc) {
			this.partnerBankAc = partnerBankAc;
		}

		public String getAlwFileDownload() {
			return alwFileDownload;
		}

		public void setAlwFileDownload(String alwFileDownload) {
			this.alwFileDownload = alwFileDownload;
		}

		public String getFileNamePrefix() {
			return fileNamePrefix;
		}

		public void setFileNamePrefix(String fileNamePrefix) {
			this.fileNamePrefix = fileNamePrefix;
		}

		public String getChannel() {
			return channel;
		}

		public void setChannel(String channel) {
			this.channel = channel;
		}

		public String getEntityCode() {
			return entityCode;
		}

		public void setEntityCode(String entityCode) {
			this.entityCode = entityCode;
		}
	}
	
	//---------- Dcument details ---------//
	private List<Document> documents;

	public List<Document> getDocuments() {
		return documents;
	}

	public void setDocuments(List<Document> documents) {
		this.documents = documents;
	}

	public class Document{
		private String cusDocName ="";
		private String receiveDate ="";
		private String docType ="";
		private String userName ="";

		public Document(){}

		public String getCusDocName() {
			return cusDocName;
		}

		public void setCusDocName(String cusDocName) {
			this.cusDocName = cusDocName;
		}

		public String getReceiveDate() {
			return receiveDate;
		}

		public void setReceiveDate(String receiveDate) {
			this.receiveDate = receiveDate;
		}

		public String getDocType() {
			return docType;
		}

		public void setDocType(String docType) {
			this.docType = docType;
		}

		public String getUserName() {
			return userName;
		}

		public void setUserName(String userName) {
			this.userName = userName;
		}
	}
	
	//---------- Convenant details ---------//
	private List<Covenant> covenants;
	
	public List<Covenant> getCovenants() {
		return covenants;
	}

	public void setCovenants(List<Covenant> covenants) {
		this.covenants = covenants;
	}

	public class Covenant{
		private String userName ="";
		private String raisedDate ="";
		private String cusDocName ="";
		private String remarks ="";
		private String targetDate ="";
		private String status ="";
		private String internalUse = "";
		
		public Covenant() {
		}

		public String getUserName() {
			return userName;
		}

		public void setUserName(String userName) {
			this.userName = userName;
		}

		public String getRaisedDate() {
			return raisedDate;
		}

		public void setRaisedDate(String raisedDate) {
			this.raisedDate = raisedDate;
		}

		public String getCusDocName() {
			return cusDocName;
		}

		public void setCusDocName(String cusDocName) {
			this.cusDocName = cusDocName;
		}

		public String getStatus() {
			return status;
		}

		public void setStatus(String status) {
			this.status = status;
		}

		public String getRemarks() {
			return remarks;
		}

		public void setRemarks(String remarks) {
			this.remarks = remarks;
		}

		public String getTargetDate() {
			return targetDate;
		}

		public void setTargetDate(String targetDate) {
			this.targetDate = targetDate;
		}

		public String getInternalUse() {
			return internalUse;
		}

		public void setInternalUse(String internalUse) {
			this.internalUse = internalUse;
		}
	}
	
	private List<ContactDetail> contactDetails;
	
	public List<ContactDetail> getContactDetails() {
		return contactDetails;
	}

	public void setContactDetails(List<ContactDetail> contactDetails) {
		this.contactDetails = contactDetails;
	}

	public class ContactDetail{
		private String contactType ="";
		private String contactValue ="";
		
		public ContactDetail() {
		}

		public String getContactType() {
			return contactType;
		}

		public void setContactType(String contactType) {
			this.contactType = contactType;
		}

		public String getContactValue() {
			return contactValue;
		}

		public void setContactValue(String contactValue) {
			this.contactValue = contactValue;
		}
	} 
	
	private List<EmailDetail> emailDetails;
	
	public List<EmailDetail> getEmailDetails() {
		return emailDetails;
	}

	public void setEmailDetails(List<EmailDetail> emailDetails) {
		this.emailDetails = emailDetails;
	}

	public class EmailDetail{

		public EmailDetail() {
		}
		
		private String emailType ="";
		
		private String emailValue ="";

		public String getEmailType() {
			return emailType;
		}

		public void setEmailType(String emailType) {
			this.emailType = emailType;
		}

		public String getEmailValue() {
			return emailValue;
		}

		public void setEmailValue(String emailValue) {
			this.emailValue = emailValue;
		}
	}
	
	private List<AppIncDetail> appIncDetails;
	
	public List<AppIncDetail> getAppIncDetails() {
		return appIncDetails;
	}

	public void setAppIncDetails(List<AppIncDetail> appIncDetails) {
		this.appIncDetails = appIncDetails;
	}

	public class AppIncDetail{
		private String applicantType="";
		private String custCIF="";
		private String custName="";
		private String incomeCategory="";
		private String incomeType="";
		private String amt="";
		private String amtPrctConsidered="";
		private String amtConsidered="";
		private String margin;
		
		public AppIncDetail() {
		}

		public String getApplicantType() {
			return applicantType;
		}

		public void setApplicantType(String applicantType) {
			this.applicantType = applicantType;
		}

		public String getCustCIF() {
			return custCIF;
		}

		public void setCustCIF(String custCIF) {
			this.custCIF = custCIF;
		}

		public String getCustName() {
			return custName;
		}

		public void setCustName(String custName) {
			this.custName = custName;
		}

		public String getIncomeCategory() {
			return incomeCategory;
		}

		public void setIncomeCategory(String incomeCategory) {
			this.incomeCategory = incomeCategory;
		}

		public String getIncomeType() {
			return incomeType;
		}

		public void setIncomeType(String incomeType) {
			this.incomeType = incomeType;
		}

		public String getAmt() {
			return amt;
		}

		public void setAmt(String amt) {
			this.amt = amt;
		}

		public String getAmtPrctConsidered() {
			return amtPrctConsidered;
		}

		public void setAmtPrctConsidered(String amtPrctConsidered) {
			this.amtPrctConsidered = amtPrctConsidered;
		}

		public String getAmtConsidered() {
			return amtConsidered;
		}

		public void setAmtConsidered(String amtConsidered) {
			this.amtConsidered = amtConsidered;
		}

		public String getMargin() {
			return margin;
		}

		public void setMargin(String margin) {
			this.margin = margin;
		}
		
	}
	
	private List<AppExpDetail> appExpDetails;
	
	public List<AppExpDetail> getAppExpDetails() {
		return appExpDetails;
	}

	public void setAppExpDetails(List<AppExpDetail> appExpDetails) {
		this.appExpDetails = appExpDetails;
	}

	public class AppExpDetail{
		private String applicantType="";
		private String custName="";
		private String expenseCategory="";
		private String expenseType="";
		private String amt="";
		private String amtPrctConsidered="";
		private String amtConsidered="";
		private String margin;
		
		public AppExpDetail() {
		}

		public String getApplicantType() {
			return applicantType;
		}

		public void setApplicantType(String applicantType) {
			this.applicantType = applicantType;
		}

		public String getCustName() {
			return custName;
		}

		public void setCustName(String custName) {
			this.custName = custName;
		}

		public String getExpenseCategory() {
			return expenseCategory;
		}

		public void setExpenseCategory(String expenseCategory) {
			this.expenseCategory = expenseCategory;
		}

		public String getExpenseType() {
			return expenseType;
		}

		public void setExpenseType(String expenseType) {
			this.expenseType = expenseType;
		}

		public String getAmt() {
			return amt;
		}

		public void setAmt(String amt) {
			this.amt = amt;
		}

		public String getAmtPrctConsidered() {
			return amtPrctConsidered;
		}

		public void setAmtPrctConsidered(String amtPrctConsidered) {
			this.amtPrctConsidered = amtPrctConsidered;
		}

		public String getAmtConsidered() {
			return amtConsidered;
		}

		public void setAmtConsidered(String amtConsidered) {
			this.amtConsidered = amtConsidered;
		}

		public String getMargin() {
			return margin;
		}

		public void setMargin(String margin) {
			this.margin = margin;
		}
	}
	
	private List<ExternalLiabilityDetail> externalLiabilityDetails;
	
	public List<ExternalLiabilityDetail> getExternalLiabilityDetails() {
		return externalLiabilityDetails;
	}

	public void setExternalLiabilityDetails(List<ExternalLiabilityDetail> externalLiabilityDetails) {
		this.externalLiabilityDetails = externalLiabilityDetails;
	}

	public class ExternalLiabilityDetail{
		private String custCIF="";
		private String custName="";
		private String emiAmt="";
		private String finInstName="";
		private String amt="";
		private String status="";
		private String loanDate="";
		private String appType="";
		private String outStandingAmt="";
		private String finBranchName="";
		
		

		public String getFinBranchName() {
			return finBranchName;
		}

		public void setFinBranchName(String finBranchName) {
			this.finBranchName = finBranchName;
		}

		public ExternalLiabilityDetail() {
		}

		public String getAppType() {
			return appType;
		}

		public void setAppType(String appType) {
			this.appType = appType;
		}

		public String getCustCIF() {
			return custCIF;
		}

		public void setCustCIF(String custCIF) {
			this.custCIF = custCIF;
		}

		public String getCustName() {
			return custName;
		}

		public void setCustName(String custName) {
			this.custName = custName;
		}

		public String getEmiAmt() {
			return emiAmt;
		}

		public void setEmiAmt(String emiAmt) {
			this.emiAmt = emiAmt;
		}

		public String getFinInstName() {
			return finInstName;
		}

		public void setFinInstName(String finInstName) {
			this.finInstName = finInstName;
		}

		public String getAmt() {
			return amt;
		}

		public void setAmt(String amt) {
			this.amt = amt;
		}

		public String getStatus() {
			return status;
		}

		public void setStatus(String status) {
			this.status = status;
		}

		public String getLoanDate() {
			return loanDate;
		}

		public void setLoanDate(String loanDate) {
			this.loanDate = loanDate;
		}

		public String getOutStandingAmt() {
			return outStandingAmt;
		}

		public void setOutStandingAmt(String outStandingAmt) {
			this.outStandingAmt = outStandingAmt;
		}
		
	}
	
	private List<InternalLiabilityDetail> internalLiabilityDetails;
	
	public List<InternalLiabilityDetail> getInternalLiabilityDetails() {
		return internalLiabilityDetails;
	}

	public void setInternalLiabilityDetails(List<InternalLiabilityDetail> internalLiabilityDetails) {
		this.internalLiabilityDetails = internalLiabilityDetails;
	}

	public class InternalLiabilityDetail{
		private String custCIF="";
		private String custName="";
		private String emiAmt="";
		private String lanNumber="";
		private String amt="";
		private String status="";
		private String balTerms="";
		private String appType="";
		
		public InternalLiabilityDetail() {
		}

		public String getCustCIF() {
			return custCIF;
		}

		public void setCustCIF(String custCIF) {
			this.custCIF = custCIF;
		}

		public String getCustName() {
			return custName;
		}

		public void setCustName(String custName) {
			this.custName = custName;
		}

		public String getEmiAmt() {
			return emiAmt;
		}

		public void setEmiAmt(String emiAmt) {
			this.emiAmt = emiAmt;
		}

		public String getLanNumber() {
			return lanNumber;
		}

		public void setLanNumber(String lanNumber) {
			this.lanNumber = lanNumber;
		}

		public String getAmt() {
			return amt;
		}

		public void setAmt(String amt) {
			this.amt = amt;
		}

		public String getStatus() {
			return status;
		}

		public void setStatus(String status) {
			this.status = status;
		}

		public String getBalTerms() {
			return balTerms;
		}

		public void setBalTerms(String balTerms) {
			this.balTerms = balTerms;
		}

		public String getAppType() {
			return appType;
		}

		public void setAppType(String appType) {
			this.appType = appType;
		}
	}
	
	private List<BankingDetail> bankingDetails;
	
	public List<BankingDetail> getBankingDetails() {
		return bankingDetails;
	}

	public void setBankingDetails(List<BankingDetail> bankingDetails) {
		this.bankingDetails = bankingDetails;
	}

	public class BankingDetail{
		private String applicantType="";
		private String custCIF="";
		private String custName="";
		private String accType="";
		private String accNo="";
		private String bankName="";
		private String totCreditAmt="";
		private String totDebitAmt="";
		private String noCheqBounce="";
		private String avgEODBalance="";
		private String totNoCredTrans = "";
		private String avgCreditAmt = "";
		private String totNoDebitTrans = "";
		private String noCashDeposit = "";
		private String cashDepositAmt = "";
		private String noCashWithDra = "";
		private String cashWithDraAmt = "";
		private String noCheqDeposit="";
		private String amtCheqDeposit="";
		private String noCheqIssue="";
		private String amtCheqIssue="";
		private String noInwardCheq="";
		private String noOutwardCheq="";
		private String minEodBalance="";
		private String maxEodBalance="";
		private String avgEodBalance="";
		
		public BankingDetail() {
		}

		public String getApplicantType() {
			return applicantType;
		}

		public void setApplicantType(String applicantType) {
			this.applicantType = applicantType;
		}

		public String getCustCIF() {
			return custCIF;
		}

		public void setCustCIF(String custCIF) {
			this.custCIF = custCIF;
		}

		public String getCustName() {
			return custName;
		}

		public void setCustName(String custName) {
			this.custName = custName;
		}

		public String getAccType() {
			return accType;
		}

		public void setAccType(String accType) {
			this.accType = accType;
		}

		public String getAccNo() {
			return accNo;
		}

		public void setAccNo(String accNo) {
			this.accNo = accNo;
		}

		public String getBankName() {
			return bankName;
		}

		public void setBankName(String bankName) {
			this.bankName = bankName;
		}

		public String getTotCreditAmt() {
			return totCreditAmt;
		}

		public void setTotCreditAmt(String totCreditAmt) {
			this.totCreditAmt = totCreditAmt;
		}

		public String getTotDebitAmt() {
			return totDebitAmt;
		}

		public void setTotDebitAmt(String totDebitAmt) {
			this.totDebitAmt = totDebitAmt;
		}

		public String getNoCheqBounce() {
			return noCheqBounce;
		}

		public void setNoCheqBounce(String noCheqBounce) {
			this.noCheqBounce = noCheqBounce;
		}

		public String getAvgEODBalance() {
			return avgEODBalance;
		}

		public void setAvgEODBalance(String avgEODBalance) {
			this.avgEODBalance = avgEODBalance;
		}

		public String getTotNoCredTrans() {
			return totNoCredTrans;
		}

		public void setTotNoCredTrans(String totNoCredTrans) {
			this.totNoCredTrans = totNoCredTrans;
		}

		public String getAvgCreditAmt() {
			return avgCreditAmt;
		}

		public void setAvgCreditAmt(String avgCreditAmt) {
			this.avgCreditAmt = avgCreditAmt;
		}

		public String getTotNoDebitTrans() {
			return totNoDebitTrans;
		}

		public void setTotNoDebitTrans(String totNoDebitTrans) {
			this.totNoDebitTrans = totNoDebitTrans;
		}

		public String getNoCashDeposit() {
			return noCashDeposit;
		}

		public void setNoCashDeposit(String noCashDeposit) {
			this.noCashDeposit = noCashDeposit;
		}

		public String getCashDepositAmt() {
			return cashDepositAmt;
		}

		public void setCashDepositAmt(String cashDepositAmt) {
			this.cashDepositAmt = cashDepositAmt;
		}

		public String getNoCashWithDra() {
			return noCashWithDra;
		}

		public void setNoCashWithDra(String noCashWithDra) {
			this.noCashWithDra = noCashWithDra;
		}

		public String getCashWithDraAmt() {
			return cashWithDraAmt;
		}

		public void setCashWithDraAmt(String cashWithDraAmt) {
			this.cashWithDraAmt = cashWithDraAmt;
		}

		public String getNoCheqDeposit() {
			return noCheqDeposit;
		}

		public void setNoCheqDeposit(String noCheqDeposit) {
			this.noCheqDeposit = noCheqDeposit;
		}

		public String getAmtCheqDeposit() {
			return amtCheqDeposit;
		}

		public void setAmtCheqDeposit(String amtCheqDeposit) {
			this.amtCheqDeposit = amtCheqDeposit;
		}

		public String getNoCheqIssue() {
			return noCheqIssue;
		}

		public void setNoCheqIssue(String noCheqIssue) {
			this.noCheqIssue = noCheqIssue;
		}

		public String getAmtCheqIssue() {
			return amtCheqIssue;
		}

		public void setAmtCheqIssue(String amtCheqIssue) {
			this.amtCheqIssue = amtCheqIssue;
		}

		public String getNoInwardCheq() {
			return noInwardCheq;
		}

		public void setNoInwardCheq(String noInwardCheq) {
			this.noInwardCheq = noInwardCheq;
		}

		public String getNoOutwardCheq() {
			return noOutwardCheq;
		}

		public void setNoOutwardCheq(String noOutwardCheq) {
			this.noOutwardCheq = noOutwardCheq;
		}

		public String getMinEodBalance() {
			return minEodBalance;
		}

		public void setMinEodBalance(String minEodBalance) {
			this.minEodBalance = minEodBalance;
		}

		public String getMaxEodBalance() {
			return maxEodBalance;
		}

		public void setMaxEodBalance(String maxEodBalance) {
			this.maxEodBalance = maxEodBalance;
		}

		public String getAvgEodBalance() {
			return avgEodBalance;
		}

		public void setAvgEodBalance(String avgEodBalance) {
			this.avgEodBalance = avgEodBalance;
		}
	}
	
	private List<IrrDetail> irrDetails;
	
	public List<IrrDetail> getIrrDetails() {
		return irrDetails;
	}

	public void setIrrDetails(List<IrrDetail> irrDetails) {
		this.irrDetails = irrDetails;
	}

	public class IrrDetail{
		private String irrCode="";
		private String irrDesc="";
		private String irrPercentage="";
		
		public IrrDetail() {
		}

		public String getIrrCode() {
			return irrCode;
		}

		public void setIrrCode(String irrCode) {
			this.irrCode = irrCode;
		}

		public String getIrrDesc() {
			return irrDesc;
		}

		public void setIrrDesc(String irrDesc) {
			this.irrDesc = irrDesc;
		}

		public String getIrrPercentage() {
			return irrPercentage;
		}

		public void setIrrPercentage(String irrPercentage) {
			this.irrPercentage = irrPercentage;
		}
		
	}
	
	private List<ActivityDetail> activityDetails;
	
	public List<ActivityDetail> getActivityDetails() {
		return activityDetails;
	}

	public void setActivityDetails(List<ActivityDetail> activityDetails) {
		this.activityDetails = activityDetails;
	}

	public class ActivityDetail{
		private String workflow="";
		private String role="";
		private String submitDate="";
		private String activityDate="";
		private String activity="";
		private String activityUser="";
		
		public ActivityDetail() {
		}

		public String getRole() {
			return role;
		}

		public void setRole(String role) {
			this.role = role;
		}

		public String getWorkflow() {
			return workflow;
		}

		public void setWorkflow(String workflow) {
			this.workflow = workflow;
		}

		public String getActivityDate() {
			return activityDate;
		}

		public void setActivityDate(String activityDate) {
			this.activityDate = activityDate;
		}

		public String getSubmitDate() {
			return submitDate;
		}

		public void setSubmitDate(String submitDate) {
			this.submitDate = submitDate;
		}

		public String getActivity() {
			return activity;
		}

		public void setActivity(String activity) {
			this.activity = activity;
		}

		public String getActivityUser() {
			return activityUser;
		}

		public void setActivityUser(String activityUser) {
			this.activityUser = activityUser;
		}
	}
	
	private List<LoanDeviation> loanDeviations;
	
	public List<LoanDeviation> getLoanDeviations() {
		return loanDeviations;
	}

	public void setLoanDeviations(List<LoanDeviation> loanDeviations) {
		this.loanDeviations = loanDeviations;
	}

	public class LoanDeviation{
		private String deviationCode="";
		private String deviationDescription="";
		private String deviationRaisedDate="";
		private String deviationApprovedBy="";
		private String remarks="";
		private String deviationType="";
		private String severity="";
		
		public LoanDeviation() {
		}

		public String getDeviationCode() {
			return deviationCode;
		}

		public void setDeviationCode(String deviationCode) {
			this.deviationCode = deviationCode;
		}

		public String getDeviationDescription() {
			return deviationDescription;
		}

		public void setDeviationDescription(String deviationDescription) {
			this.deviationDescription = deviationDescription;
		}

		public String getDeviationRaisedDate() {
			return deviationRaisedDate;
		}

		public void setDeviationRaisedDate(String deviationRaisedDate) {
			this.deviationRaisedDate = deviationRaisedDate;
		}

		public String getDeviationApprovedBy() {
			return deviationApprovedBy;
		}

		public void setDeviationApprovedBy(String deviationApprovedBy) {
			this.deviationApprovedBy = deviationApprovedBy;
		}

		public String getRemarks() {
			return remarks;
		}

		public void setRemarks(String remarks) {
			this.remarks = remarks;
		}

		public String getDeviationType() {
			return deviationType;
		}

		public void setDeviationType(String deviationType) {
			this.deviationType = deviationType;
		}

		public String getSeverity() {
			return severity;
		}

		public void setSeverity(String severity) {
			this.severity = severity;
		}
	}
	
	private List<SourcingDetail> sourcingDetails;
	
	public List<SourcingDetail> getSourcingDetails() {
		return sourcingDetails;
	}

	public void setSourcingDetails(List<SourcingDetail> sourcingDetails) {
		this.sourcingDetails = sourcingDetails;
	}

	public class SourcingDetail{
		private String sourceChannel="";
		private String salesPerson="";
		private String dsaName="";
		private String dsaNameDesc="";
		private String salesManager ="";
		private String salesManagerDesc ="";
		private String dmaCodeDesc = "";
		
		public SourcingDetail() {
		}
		
		public String getDsaNameDesc() {
			return dsaNameDesc;
		}

		public void setDsaNameDesc(String dsaNameDesc) {
			this.dsaNameDesc = dsaNameDesc;
		}

		public String getSalesManagerDesc() {
			return salesManagerDesc;
		}

		public void setSalesManagerDesc(String salesManagerDesc) {
			this.salesManagerDesc = salesManagerDesc;
		}

		public String getDmaCodeDesc() {
			return dmaCodeDesc;
		}

		public void setDmaCodeDesc(String dmaCodeDesc) {
			this.dmaCodeDesc = dmaCodeDesc;
		}

		public String getSalesManager() {
			return salesManager;
		}

		public void setSalesManager(String salesManager) {
			this.salesManager = salesManager;
		}

		public String getSourceChannel() {
			return sourceChannel;
		}
		public void setSourceChannel(String sourceChannel) {
			this.sourceChannel = sourceChannel;
		}
		public String getSalesPerson() {
			return salesPerson;
		}
		public void setSalesPerson(String salesPerson) {
			this.salesPerson = salesPerson;
		}
		public String getDsaName() {
			return dsaName;
		}
		public void setDsaName(String dsaName) {
			this.dsaName = dsaName;
		}
		
	}
	
	private List<ExtendedDetail> extendedDetails;
	
	/**
	 * Get the all the extended details that are configured.
	 * 
	 * @return
	 * 		all the extended detail vlaues
	 */
	public List<ExtendedDetail> getExtendedDetails() {
		return extendedDetails;
	}

	public void setExtendedDetails(List<ExtendedDetail> extendedDetails) {
		this.extendedDetails = extendedDetails;
	}

	/**
	 * ExtendedDetail contains the extended details field with name and value. 
	 *
	 */
	public class ExtendedDetail{
		private String key="";
		private String value="";
		private String fieldType="";
		private String fieldLabel="";
		public ExtendedDetail() {
		}
		/**
		 * Get the name of the extended field
		 * 
		 * @return
		 */
		public String getKey() {
			return key;
		}
		public void setKey(String key) {
			this.key = key;
		}
		/**
		 * Get the value of the extended field
		 * 
		 * @return
		 */
		public String getValue() {
			return value;
		}
		public void setValue(String value) {
			this.value = value;
		}
		public String getFieldType() {
			return fieldType;
		}
		public void setFieldType(String fieldType) {
			this.fieldType = fieldType;
		}
		public String getFieldLabel() {
			return fieldLabel;
		}
		public void setFieldLabel(String fieldLabel) {
			this.fieldLabel = fieldLabel;
		}
		
	}
	
	//Required Fields
	private String carDealer ="";														// Vendor/Seller ID
	private String assetValue ="";														// Asset Value
	private String dealerCity ="";														// Vendor/Seller City
	private String dealerCountry ="";													// Vendor/Seller Country
	private String dealerName ="";														// Vendor/Seller Name
	private String lpoDate ="";														//LOP Date
	private String marketValue="";
	private String marketValueInWords="";
	private String totalPrice ="";														//total Price
	
	public String getCarDealer() {
		return carDealer;
	}

	public void setCarDealer(String carDealer) {
		this.carDealer = carDealer;
	}

	public String getAssetValue() {
		return assetValue;
	}

	public void setAssetValue(String assetValue) {
		this.assetValue = assetValue;
	}

	public String getDealerCity() {
		return dealerCity;
	}

	public void setDealerCity(String dealerCity) {
		this.dealerCity = dealerCity;
	}

	public String getDealerCountry() {
		return dealerCountry;
	}

	public void setDealerCountry(String dealerCountry) {
		this.dealerCountry = dealerCountry;
	}

	public String getDealerName() {
		return dealerName;
	}

	public void setDealerName(String dealerName) {
		this.dealerName = dealerName;
	}

	public String getLpoDate() {
		return lpoDate;
	}

	public void setLpoDate(String lpoDate) {
		this.lpoDate = lpoDate;
	}

	public String getMarketValue() {
		return marketValue;
	}

	public void setMarketValue(String marketValue) {
		this.marketValue = marketValue;
	}

	public String getMarketValueInWords() {
		return marketValueInWords;
	}

	public void setMarketValueInWords(String marketValueInWords) {
		this.marketValueInWords = marketValueInWords;
	}

	public String getTotalPrice() {
		return totalPrice;
	}

	public void setTotalPrice(String totalPrice) {
		this.totalPrice = totalPrice;
	}
	
	private List<Score> scoringDetails;
	
	public List<Score> getScoringDetails() {
		return scoringDetails;
	}

	public void setScoringDetails(List<Score> scoringDetails) {
		this.scoringDetails = scoringDetails;
	}
	
	private String creditWorth="";
	
	private String maxScore="";
	
	private String totScore="";
	
	public String getCreditWorth() {
		return creditWorth;
	}

	public void setCreditWorth(String creditWorth) {
		this.creditWorth = creditWorth;
	}
	
	public String getMaxScore() {
		return maxScore;
	}

	public void setMaxScore(String maxScore) {
		this.maxScore = maxScore;
	}

	public String getTotScore() {
		return totScore;
	}

	public void setTotScore(String totScore) {
		this.totScore = totScore;
	}

	public class Score{
		private String scoringMetrics="";
		private String description="";
		private String maximumScore="";
		private String actualScore="";
		
		public Score() {
		}

		public String getScoringMetrics() {
			return scoringMetrics;
		}

		public void setScoringMetrics(String scoringMetrics) {
			this.scoringMetrics = scoringMetrics;
		}

		public String getDescription() {
			return description;
		}

		public void setDescription(String description) {
			this.description = description;
		}

		public String getMaximumScore() {
			return maximumScore;
		}

		public void setMaximumScore(String maximumScore) {
			this.maximumScore = maximumScore;
		}

		public String getActualScore() {
			return actualScore;
		}

		public void setActualScore(String actualScore) {
			this.actualScore = actualScore;
		}
	}
	
	List<Eligibility> eligibilityList;
	
	public List<Eligibility> getEligibilityList() {
    	return eligibilityList;
    }
	public void setEligibilityList(List<Eligibility> eligibilityList) {
    	this.eligibilityList = eligibilityList;
    }

	public class Eligibility{
		
		private String ruleCode = "";
		private String description = "";
		private String eligibilityLimit = "";
		
		public Eligibility() {
		}
		
		public String getRuleCode() {
        	return ruleCode;
        }
		public void setRuleCode(String ruleCode) {
        	this.ruleCode = ruleCode;
        }
		
		public String getDescription() {
        	return description;
        }
		public void setDescription(String description) {
        	this.description = description;
        }
		
		public String getEligibilityLimit() {
        	return eligibilityLimit;
        }
		public void setEligibilityLimit(String eligibilityLimit) {
        	this.eligibilityLimit = eligibilityLimit;
        }
	}
	
	private List<VerificationDetail> fiVerification;
	private List<VerificationDetail> technicalVerification ;
	private List<VerificationDetail> rcuVerification;
	private List<VerificationDetail> legalVerification ;
	
	public List<VerificationDetail> getFiVerification() {
		return fiVerification;
	}

	public void setFiVerification(List<VerificationDetail> fiVerification) {
		this.fiVerification = fiVerification;
	}

	public List<VerificationDetail> getTechnicalVerification() {
		return technicalVerification;
	}

	public void setTechnicalVerification(List<VerificationDetail> technicalVerification) {
		this.technicalVerification = technicalVerification;
	}

	public List<VerificationDetail> getRcuVerification() {
		return rcuVerification;
	}

	public void setRcuVerification(List<VerificationDetail> rcuVerification) {
		this.rcuVerification = rcuVerification;
	}

	public List<VerificationDetail> getLegalVerification() {
		return legalVerification;
	}

	public void setLegalVerification(List<VerificationDetail> legalVerification) {
		this.legalVerification = legalVerification;
	}

	public class VerificationDetail{
		private String applicantName= "";
		private String initiationDate= "";
		private String completionDate= "";
		private String initialStatus= "";
		private String remarks= "";
		private String doneBy= "";
		private String agencyName= "";
		private String addressType= "";
		private String collateralType= "";
		private String collateralReference= "";
		private String verificationType= "";
		private String documentName= "";
		private String documentStatus= "";
		private String recommanditionStatus = "";
		private String finalDecision = "";
		
		public VerificationDetail() {
		}

		public String getApplicantName() {
			return applicantName;
		}

		public void setApplicantName(String applicantName) {
			this.applicantName = applicantName;
		}

		public String getInitiationDate() {
			return initiationDate;
		}

		public void setInitiationDate(String initiationDate) {
			this.initiationDate = initiationDate;
		}

		public String getCompletionDate() {
			return completionDate;
		}

		public void setCompletionDate(String completionDate) {
			this.completionDate = completionDate;
		}

		public String getRemarks() {
			return remarks;
		}

		public void setRemarks(String remarks) {
			this.remarks = remarks;
		}

		public String getDoneBy() {
			return doneBy;
		}

		public void setDoneBy(String doneBy) {
			this.doneBy = doneBy;
		}

		public String getAgencyName() {
			return agencyName;
		}

		public void setAgencyName(String agencyName) {
			this.agencyName = agencyName;
		}

		public String getAddressType() {
			return addressType;
		}

		public void setAddressType(String addressType) {
			this.addressType = addressType;
		}

		public String getCollateralType() {
			return collateralType;
		}

		public void setCollateralType(String collateralType) {
			this.collateralType = collateralType;
		}

		public String getCollateralReference() {
			return collateralReference;
		}

		public void setCollateralReference(String collateralReference) {
			this.collateralReference = collateralReference;
		}

		public String getVerificationType() {
			return verificationType;
		}

		public void setVerificationType(String verificationType) {
			this.verificationType = verificationType;
		}

		public String getDocumentName() {
			return documentName;
		}

		public void setDocumentName(String documentName) {
			this.documentName = documentName;
		}

		public String getDocumentStatus() {
			return documentStatus;
		}

		public void setDocumentStatus(String documentStatus) {
			this.documentStatus = documentStatus;
		}

		public String getRecommanditionStatus() {
			return recommanditionStatus;
		}

		public void setRecommanditionStatus(String recommanditionStatus) {
			this.recommanditionStatus = recommanditionStatus;
		}

		public String getInitialStatus() {
			return initialStatus;
		}

		public void setInitialStatus(String initialStatus) {
			this.initialStatus = initialStatus;
		}

		public String getFinalDecision() {
			return finalDecision;
		}

		public void setFinalDecision(String finalDecision) {
			this.finalDecision = finalDecision;
		}
		
	}
	
	private List<LoanQryDetails> queryDetails;
	
	public List<LoanQryDetails> getQueryDetails() {
		return queryDetails;
	}

	public void setQueryDetails(List<LoanQryDetails> queryDetails) {
		this.queryDetails = queryDetails;
	}

	public class LoanQryDetails{
		private String raisedBy = "";
		private String raisedOn = "";
		private String category = "";
		private String description = "";
		private String status = "";

		public LoanQryDetails() {
		}

		public String getRaisedBy() {
			return raisedBy;
		}

		public void setRaisedBy(String raisedBy) {
			this.raisedBy = raisedBy;
		}

		public String getRaisedOn() {
			return raisedOn;
		}

		public void setRaisedOn(String raisedOn) {
			this.raisedOn = raisedOn;
		}

		public String getCategory() {
			return category;
		}

		public void setCategory(String category) {
			this.category = category;
		}

		public String getDescription() {
			return description;
		}

		public void setDescription(String description) {
			this.description = description;
		}

		public String getStatus() {
			return status;
		}

		public void setStatus(String status) {
			this.status = status;
		}
	}
	
	private String eligibilityMethod="";
	private String ConnectorCode="";
	
    public String getConnectorCode() {
		return ConnectorCode;
	}

	public void setConnectorCode(String connectorCode) {
		ConnectorCode = connectorCode;
	}

	public String getEligibilityMethod() {
		return eligibilityMethod;
	}

	public void setEligibilityMethod(String eligibilityMethod) {
		this.eligibilityMethod = eligibilityMethod;
	}

	private List<DirectorDetail> directorDetails;
	
	public List<DirectorDetail> getDirectorDetails() {
		return directorDetails;
	}

	public void setDirectorDetails(List<DirectorDetail> directorDetails) {
		this.directorDetails = directorDetails;
	}

	public class DirectorDetail{
		private String firstName = "";
		private String middleName  = "";
		private String lastName  = "";
		private String gender   = "";
		private String salutationCodeName  = "";
		private String cityName  = ""; 
		private String provinceName  = "";
		private String countryName  = "";
		private String shortName  = "";
		private String shareholder   = "No";
		private String director  = "No";
		private String sharePerc = "";
		private String docCategoryName ="";
		private String designationName="";
		private String nationalityName="";

		public String getNationalityName() {
			return nationalityName;
		}
		public void setNationalityName(String nationalityName) {
			this.nationalityName = nationalityName;
		}
		public String getDesignationName() {
			return designationName;
		}
		public void setDesignationName(String designationName) {
			this.designationName = designationName;
		}
		public String getDocCategoryName() {
			return docCategoryName;
		}
		public void setDocCategoryName(String docCategoryName) {
			this.docCategoryName = docCategoryName;
		}
		public String getFirstName() {
			return firstName;
		}
		public void setFirstName(String firstName) {
			this.firstName = firstName;
		}
		public String getMiddleName() {
			return middleName;
		}
		public void setMiddleName(String middleName) {
			this.middleName = middleName;
		}
		public String getLastName() {
			return lastName;
		}
		public void setLastName(String lastName) {
			this.lastName = lastName;
		}
		public String getGender() {
			return gender;
		}
		public void setGender(String gender) {
			this.gender = gender;
		}
		public String getSalutationCodeName() {
			return salutationCodeName;
		}
		public void setSalutationCodeName(String salutationCodeName) {
			this.salutationCodeName = salutationCodeName;
		}
		public String getCityName() {
			return cityName;
		}
		public void setCityName(String cityName) {
			this.cityName = cityName;
		}
		public String getProvinceName() {
			return provinceName;
		}
		public void setProvinceName(String provinceName) {
			this.provinceName = provinceName;
		}
		public String getCountryName() {
			return countryName;
		}
		public void setCountryName(String countryName) {
			this.countryName = countryName;
		}
		public String getShortName() {
			return shortName;
		}
		public void setShortName(String shortName) {
			this.shortName = shortName;
		}
		public String getShareholder() {
			return shareholder;
		}
		public void setShareholder(String shareholder) {
			this.shareholder = shareholder;
		}
		public String getDirector() {
			return director;
		}
		public void setDirector(String director) {
			this.director = director;
		}
		public String getSharePerc() {
			return sharePerc;
		}
		public void setSharePerc(String sharePerc) {
			this.sharePerc = sharePerc;
		}
		
		
	}
	
	private List<VasDetails> vasData;
	
	public List<VasDetails> getVasData() {
		return vasData;
	}

	public void setVasData(List<VasDetails> vasData) {
		this.vasData = vasData;
	}

	public class VasDetails{
		private String productCode = "";
		private String productDesc = "";
		private String postingAgainst = "";
		private String vasReference = "";
		private String primaryLinkRef = "";
		private String fee = "";
		private String renewalFee = "";
		private String feePaymentMode = "";
		private String valueDate = "";
		private String productType = "";
		private String productTypeDesc = "";
		private String productCtg = "";
		private String productCtgDesc = "";
		private String manufacturerDesc = "";
		private String vasStatus = "";
		private String feeAccounting = "";
		public String getProductCode() {
			return productCode;
		}
		public void setProductCode(String productCode) {
			this.productCode = productCode;
		}
		public String getProductDesc() {
			return productDesc;
		}
		public void setProductDesc(String productDesc) {
			this.productDesc = productDesc;
		}
		public String getPostingAgainst() {
			return postingAgainst;
		}
		public void setPostingAgainst(String postingAgainst) {
			this.postingAgainst = postingAgainst;
		}
		public String getVasReference() {
			return vasReference;
		}
		public void setVasReference(String vasReference) {
			this.vasReference = vasReference;
		}
		public String getPrimaryLinkRef() {
			return primaryLinkRef;
		}
		public void setPrimaryLinkRef(String primaryLinkRef) {
			this.primaryLinkRef = primaryLinkRef;
		}
		public String getFee() {
			return fee;
		}
		public void setFee(String fee) {
			this.fee = fee;
		}
		public String getRenewalFee() {
			return renewalFee;
		}
		public void setRenewalFee(String renewalFee) {
			this.renewalFee = renewalFee;
		}
		public String getFeePaymentMode() {
			return feePaymentMode;
		}
		public void setFeePaymentMode(String feePaymentMode) {
			this.feePaymentMode = feePaymentMode;
		}
		public String getValueDate() {
			return valueDate;
		}
		public void setValueDate(String valueDate) {
			this.valueDate = valueDate;
		}
		public String getProductType() {
			return productType;
		}
		public void setProductType(String productType) {
			this.productType = productType;
		}
		public String getProductTypeDesc() {
			return productTypeDesc;
		}
		public void setProductTypeDesc(String productTypeDesc) {
			this.productTypeDesc = productTypeDesc;
		}
		public String getProductCtg() {
			return productCtg;
		}
		public void setProductCtg(String productCtg) {
			this.productCtg = productCtg;
		}
		public String getProductCtgDesc() {
			return productCtgDesc;
		}
		public void setProductCtgDesc(String productCtgDesc) {
			this.productCtgDesc = productCtgDesc;
		}
		public String getManufacturerDesc() {
			return manufacturerDesc;
		}
		public void setManufacturerDesc(String manufacturerDesc) {
			this.manufacturerDesc = manufacturerDesc;
		}
		public String getVasStatus() {
			return vasStatus;
		}
		public void setVasStatus(String vasStatus) {
			this.vasStatus = vasStatus;
		}
		public String getFeeAccounting() {
			return feeAccounting;
		}
		public void setFeeAccounting(String feeAccounting) {
			this.feeAccounting = feeAccounting;
		}
	}
	
	private Map<String,String> otherMap;


	public Map<String, String> getOtherMap() {
		return otherMap;
	}

	public void setOtherMap(Map<String, String> otherMap) {
		this.otherMap = otherMap;
	}

	//----------------------------- PSL Details
	private String pslCategoryCodeName = "";
	private String pslWeakerSectionName = "";
	private String pslLandHoldingName = "";
	private String pslLandAreaName = "";
	private String pslSectorName = "";
	private String pslAmount = "";
	private String pslSubCategoryName = "";
	private String pslPurposeName = "";
	private String pslEndUseName = "";
	private String pslLoanPurposeName = "";
	private String pslEligibleAmount = "";

	public String getPslCategoryCodeName() {
		return pslCategoryCodeName;
	}

	public void setPslCategoryCodeName(String pslCategoryCodeName) {
		this.pslCategoryCodeName = pslCategoryCodeName;
	}

	public String getPslWeakerSectionName() {
		return pslWeakerSectionName;
	}

	public void setPslWeakerSectionName(String pslWeakerSectionName) {
		this.pslWeakerSectionName = pslWeakerSectionName;
	}

	public String getPslLandHoldingName() {
		return pslLandHoldingName;
	}

	public void setPslLandHoldingName(String pslLandHoldingName) {
		this.pslLandHoldingName = pslLandHoldingName;
	}

	public String getPslLandAreaName() {
		return pslLandAreaName;
	}

	public void setPslLandAreaName(String pslLandAreaName) {
		this.pslLandAreaName = pslLandAreaName;
	}

	public String getPslSectorName() {
		return pslSectorName;
	}

	public void setPslSectorName(String pslSectorName) {
		this.pslSectorName = pslSectorName;
	}

	public String getPslAmount() {
		return pslAmount;
	}

	public void setPslAmount(String pslAmount) {
		this.pslAmount = pslAmount;
	}

	public String getPslSubCategoryName() {
		return pslSubCategoryName;
	}

	public void setPslSubCategoryName(String pslSubCategoryName) {
		this.pslSubCategoryName = pslSubCategoryName;
	}

	public String getPslPurposeName() {
		return pslPurposeName;
	}

	public void setPslPurposeName(String pslPurposeName) {
		this.pslPurposeName = pslPurposeName;
	}

	public String getPslEndUseName() {
		return pslEndUseName;
	}

	public void setPslEndUseName(String pslEndUseName) {
		this.pslEndUseName = pslEndUseName;
	}

	public String getPslLoanPurposeName() {
		return pslLoanPurposeName;
	}

	public void setPslLoanPurposeName(String pslLoanPurposeName) {
		this.pslLoanPurposeName = pslLoanPurposeName;
	}

	public String getPslEligibleAmount() {
		return pslEligibleAmount;
	}

	public void setPslEligibleAmount(String pslEligibleAmount) {
		this.pslEligibleAmount = pslEligibleAmount;
	}
	
	private String smplTolerance;
	private String smplDecision;
	private String smplResubmitReasonDesc;
	private String smplRecommendedAmount;
	private String smplRemarks;
	List<SamplingDetail> smplDetails = new ArrayList<>();

	public String getSmplTolerance() {
		return smplTolerance;
	}

	public void setSmplTolerance(String smplTolerance) {
		this.smplTolerance = smplTolerance;
	}

	public String getSmplDecision() {
		return smplDecision;
	}

	public void setSmplDecision(String smplDecision) {
		this.smplDecision = smplDecision;
	}

	public String getSmplResubmitReasonDesc() {
		return smplResubmitReasonDesc;
	}

	public void setSmplResubmitReasonDesc(String smplResubmitReasonDesc) {
		this.smplResubmitReasonDesc = smplResubmitReasonDesc;
	}

	public String getSmplRecommendedAmount() {
		return smplRecommendedAmount;
	}

	public void setSmplRecommendedAmount(String smplRecommendedAmount) {
		this.smplRecommendedAmount = smplRecommendedAmount;
	}

	public String getSmplRemarks() {
		return smplRemarks;
	}

	public void setSmplRemarks(String smplRemarks) {
		this.smplRemarks = smplRemarks;
	}

	public List<SamplingDetail> getSmplDetails() {
		return smplDetails;
	}

	public void setSmplDetails(List<SamplingDetail> smplDetails) {
		this.smplDetails = smplDetails;
	}
	
	private List<CreditReviewEligibilitySummary> crdRevElgSummaries;
	
	public List<CreditReviewEligibilitySummary> getCrdRevElgSummaries() {
		return crdRevElgSummaries;
	}

	public void setCrdRevElgSummaries(List<CreditReviewEligibilitySummary> crdRevElgSummaries) {
		this.crdRevElgSummaries = crdRevElgSummaries;
	}

	public class CreditReviewEligibilitySummary {
		private String subCategoryCode="";
		private String subCategoryDesc="";
		private String y0Amount="";
		private String y1Amount="";
		private String y2Amount="";

		public String getSubCategoryCode() {
			return subCategoryCode;
		}

		public void setSubCategoryCode(String subCategoryCode) {
			this.subCategoryCode = subCategoryCode;
		}

		public String getSubCategoryDesc() {
			return subCategoryDesc;
		}

		public void setSubCategoryDesc(String subCategoryDesc) {
			this.subCategoryDesc = subCategoryDesc;
		}

		public String getY0Amount() {
			return y0Amount;
		}

		public void setY0Amount(String y0Amount) {
			this.y0Amount = y0Amount;
		}

		public String getY1Amount() {
			return y1Amount;
		}

		public void setY1Amount(String y1Amount) {
			this.y1Amount = y1Amount;
		}

		public String getY2Amount() {
			return y2Amount;
		}

		public void setY2Amount(String y2Amount) {
			this.y2Amount = y2Amount;
		}

	}
}
