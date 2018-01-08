package com.pennant.backend.model.finance;

import java.util.List;

import org.apache.commons.lang.StringUtils;

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
	private String finMinRate;
	private String finMaxRate;
	private String profitRateType;														//Profit Rate Type
	private String tenor;
	private String netRefRateLoan;
	//DDA Fields
	//===============================
	private String 	bankName ="";														// DDA Sponsoring Bank
	private String 	accountType ="";													// DDA Bank Account Type for selected Account
	private String 	iban =""; 															// Customer IBAN Number
	private String 	ddaPurposeCode ="";													// DDA Purpose Code
	private String 	ifscCode ="";													// DDA Purpose Code
	
	//Mandate
	private String				accNumberMandate;

	
	//External Fields
	//===============================
	private String 	mMADate ="";														// MMA Agreement Date
	private String 	custDSR ="";														// Customer DSR
	
	private List<Recommendation> recommendations;									// Recommendations Data
	private List<GroupRecommendation> groupRecommendations;							// Grouping Recommendations
	private List<ExceptionList> exceptionLists;										// Exceptional List(May be Deviations in Future)
	
	// =========== Vehicle Finance =============//

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

	public String getDealerCountry() {
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
	}

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
		private String collateralType;
		private String reference;
		private String collateralAmt;
		public String getReference() {
			return reference;
		}
		public void setReference(String reference) {
			this.reference = reference;
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
	}
	
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
	
	public String getEmiratesReg() {
	    return emiratesReg;
    }
	public void setEmiratesReg(String emiratesReg) {
	    this.emiratesReg = emiratesReg;
    }
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
	
	public String getSalesPersonName() {
		return salesPersonName;
	}
	public void setSalesPersonName(String salesPersonName) {
		this.salesPersonName = salesPersonName;
	}
	
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
	
	public String getTrafficProfileNo() {
	    return trafficProfileNo;
    }
	public void setTrafficProfileNo(String trafficProfileNo) {
	    this.trafficProfileNo = trafficProfileNo;
    }

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
    }

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
    }

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

	public String getDealerCity() {
	    return dealerCity;
    }

	public void setDealerCity(String dealerCity) {
	    this.dealerCity = dealerCity;
    }

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
	}

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
	}

	public List<FinanceScheduleDetail> getScheduleData() {
		return scheduleData;
	}

	public void setScheduleData(List<FinanceScheduleDetail> scheduleData) {
		this.scheduleData = scheduleData;
	}

	public String getProjectName() {
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
	}

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
	}

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

	public String getUnitAreaInSqft() {
		return unitAreaInSqft;
	}

	public void setUnitAreaInSqft(String unitAreaInSqft) {
		this.unitAreaInSqft = unitAreaInSqft;
	}

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

	public String getEmiratesOfReg() {
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

	public String getSecDeposit() {
		return secDeposit;
	}

	public void setSecDeposit(String secDeposit) {
		this.secDeposit = secDeposit;
	}

	public String getSellerInternal() {
		return sellerInternal;
	}

	public void setSellerInternal(String sellerInternal) {
		this.sellerInternal = sellerInternal;
	}

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

	public String getNumberOfUnits() {
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
	}

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

	public String getVehicleReg() {
		return vehicleReg;
	}

	public void setVehicleReg(String vehicleReg) {
		this.vehicleReg = vehicleReg;
	}

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

	public String getNumberOfLeasedUnits() {
		return numberOfLeasedUnits;
	}
	public void setNumberOfLeasedUnits(String numberOfLeasedUnits) {
		this.numberOfLeasedUnits = numberOfLeasedUnits;
	}

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
		
		private String custName ="";
		private String panNumber ="";
		private String Address ="";
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


}
