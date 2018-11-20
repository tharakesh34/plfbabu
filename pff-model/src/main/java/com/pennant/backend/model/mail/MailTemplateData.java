package com.pennant.backend.model.mail;

import java.util.HashMap;

public class MailTemplateData {

	// Customer Details
	private String custShrtName;
	private String custCIF;
	private String custCoreBank;
	private String custPanNumber;
	private String custAadharNumber;
	private String custMobileNumber;
	private String custEmailId;
	private String custAddrLine1;
	private String custAddrLine2;
	private String custAddrHNbr;
	private String custAddrFlatNo;
	private String custAddrStreet;
	private String custAddrCountry;
	private String custAddrCity;
	private String custAddrProvince;
	private String custAddrDistrict;
	private String custAddrPincode;

	// User Details
	private String userName;
	private String userBranch;
	private String userBranchDetails;
	private String userDepartment;
	private String appDate;

	// Existing Beans
	private String usrName;
	private String usrRole;
	private String prevUsrName;
	private long prevUsrRole = 0;
	private String nextUsrName;
	private String nextUsrRole;
	private String nextUsrRoleCode;
	private String workFlowType;
	// ----------------------------

	// Loan Details
	private String finReference;
	private String finStartDate;
	private int priority;
	private String finAmount;
	private String finAssetValue;
	private String finCurrAssetValue;
	private String repaymentFrequency;
	private String firstDueDate;
	private String maturityDate;
	private String gracePeriod;
	private String repayperiod;
	private String graceRate;
	private String repayRate;
	private String graceBaseRate;
	private String graceSpecialRate;
	private String graceMargin;
	private String repayBaseRate;
	private String repaySpecialRate;
	private String repayMargin;
	private String finBranch;
	private String finBranchContact;
	private String finCcy;
	private String finDivision;
	private String accountsOfficerDesc;
	private String dsaCode;
	private String dsaDesc;
	private String dMACodeDesc;
	private String totalProfit;
	private String currReducingRate;
	private String firstRepay;
	private String lastRepay;
	private String totalPriPaid;
	private String totalPriBalance;
	private String totalProfitPaid;
	private String totalProfitBalance;
	private String nextRepayDate;

	// existing beans
	private String numberOfTerms;
	private String graceTerms;
	private String effectiveRate;
	private String downPayment;
	private String feeAmount;
	private String insAmount;
	private String finType;

	// Receipts
	private String paidBy;
	private String paidAmount;
	private String paidDetails;
	private String paidDate;
	// ---------------------------------

	// facility
	private String totAmountBD;
	private String totAmountUSD;
	private String cafReference;
	private String countryOfDomicileName;
	private String countryOfRiskName;
	private String countryManagerName;
	private String customerGroupName;
	private String natureOfBusinessName;

	private String recommendations;
	private String finPurpose;
	private String finCommitmentRef;

	// Credit Review
	private String auditors;
	private String location;
	private String auditType;
	private String auditedDate;
	private String auditYear;
	private int auditPeriod;

	// Treasury Investment
	private String investmentRef;
	private String totPrincipalAmt;
	private String startDate;
	private String principalInvested;
	private String principalMaturity;
	private String principalDueToInvest;
	private String avgPftRate;

	// Provision
	private String principalDue;
	private String profitDue;
	private String totalDue;
	private String dueFromDate;
	private String nonFormulaProv;
	private String provisionedAmt;
	private String provisionedAmtCal;

	// Manual Suspense
	private String manualSusp;
	private String finSuspDate;
	private String finSuspAmt;
	private String finCurSuspAmt;

	// PO Authorization
	private String bankName;
	private String product;
	private String takeoverAmount;
	private String rate;
	private String custPortion;

	// External Usage
	private String roleCode = "";
	private long custId = 0;
	private String rcdMaintainSts;
	private String recieveMail;
	private String facilityType;
	private String finCurODAmt;
	private int finCurODDays;
	private String recordStatus;
	private String receiptPurpose;

	private String amount;
	private String receiptAmount;
	private String valueDate;

	private String bounceDate;
	private String bounceReason;
	private String cancellationReason;

	public MailTemplateData() {

	}

	// ******************************************************//
	// ****************** getter / setter *******************//
	// ******************************************************//

	public String getCustCoreBank() {
		return custCoreBank;
	}

	public void setCustCoreBank(String custCoreBank) {
		this.custCoreBank = custCoreBank;
	}

	public String getCustPanNumber() {
		return custPanNumber;
	}

	public void setCustPanNumber(String custPanNumber) {
		this.custPanNumber = custPanNumber;
	}

	public String getCustAadharNumber() {
		return custAadharNumber;
	}

	public void setCustAadharNumber(String custAadharNumber) {
		this.custAadharNumber = custAadharNumber;
	}

	public String getCustMobileNumber() {
		return custMobileNumber;
	}

	public void setCustMobileNumber(String custMobileNumber) {
		this.custMobileNumber = custMobileNumber;
	}

	public String getCustEmailId() {
		return custEmailId;
	}

	public void setCustEmailId(String custEmailId) {
		this.custEmailId = custEmailId;
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

	public String getCustAddrHNbr() {
		return custAddrHNbr;
	}

	public void setCustAddrHNo(String custAddrHNbr) {
		this.custAddrHNbr = custAddrHNbr;
	}

	public String getCustAddrFlatNo() {
		return custAddrFlatNo;
	}

	public void setCustAddrFlatNo(String custAddrFlatNo) {
		this.custAddrFlatNo = custAddrFlatNo;
	}

	public String getCustAddrStreet() {
		return custAddrStreet;
	}

	public void setCustAddrStreet(String custAddrStreet) {
		this.custAddrStreet = custAddrStreet;
	}

	public String getCustAddrCountry() {
		return custAddrCountry;
	}

	public void setCustAddrCountry(String custAddrCountry) {
		this.custAddrCountry = custAddrCountry;
	}

	public String getCustAddrProvince() {
		return custAddrProvince;
	}

	public void setCustAddrProvince(String custAddrProvince) {
		this.custAddrProvince = custAddrProvince;
	}

	public String getCustAddrDistrict() {
		return custAddrDistrict;
	}

	public void setCustAddrDistrict(String custAddrDistrict) {
		this.custAddrDistrict = custAddrDistrict;
	}

	public String getCustAddrPincode() {
		return custAddrPincode;
	}

	public void setCustAddrPincode(String custAddrPincode) {
		this.custAddrPincode = custAddrPincode;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getUserBranch() {
		return userBranch;
	}

	public void setUserBranch(String userBranch) {
		this.userBranch = userBranch;
	}

	public String getUserBranchDetails() {
		return userBranchDetails;
	}

	public void setUserBranchDetails(String userBranchDetails) {
		this.userBranchDetails = userBranchDetails;
	}

	public String getUserDepartment() {
		return userDepartment;
	}

	public void setUserDepartment(String userDepartment) {
		this.userDepartment = userDepartment;
	}

	public String getAppDate() {
		return appDate;
	}

	public void setAppDate(String appDate) {
		this.appDate = appDate;
	}

	public String getFinCurAssetValue() {
		return finCurrAssetValue;
	}

	public void setFinCurAssetValue(String finCurrAssetValue) {
		this.finCurrAssetValue = finCurrAssetValue;
	}

	public String getFinAssetValue() {
		return finAssetValue;
	}

	public void setFinAssetValue(String finAssetValue) {
		this.finAssetValue = finAssetValue;
	}

	public String getRepaymentFrequency() {
		return repaymentFrequency;
	}

	public void setRepaymentFrequency(String repaymentFrequency) {
		this.repaymentFrequency = repaymentFrequency;
	}

	public String getFirstDueDate() {
		return firstDueDate;
	}

	public void setFirstDueDate(String firstDueDate) {
		this.firstDueDate = firstDueDate;
	}

	public String getGracePeriod() {
		return gracePeriod;
	}

	public void setGracePeriod(String gracePeriod) {
		this.gracePeriod = gracePeriod;
	}

	public String getRepayperiod() {
		return repayperiod;
	}

	public void setRepayperiod(String repayperiod) {
		this.repayperiod = repayperiod;
	}

	public String getGraceRate() {
		return graceRate;
	}

	public void setGraceRate(String graceRate) {
		this.graceRate = graceRate;
	}

	public String getRepayRate() {
		return repayRate;
	}

	public void setRepayRate(String repayRate) {
		this.repayRate = repayRate;
	}

	public String getGraceBaseRate() {
		return graceBaseRate;
	}

	public void setGraceBaseRate(String graceBaseRate) {
		this.graceBaseRate = graceBaseRate;
	}

	public String getGraceSpecialRate() {
		return graceSpecialRate;
	}

	public void setGraceSpecialRate(String graceSpecialRate) {
		this.graceSpecialRate = graceSpecialRate;
	}

	public String getGraceMargin() {
		return graceMargin;
	}

	public void setGraceMargin(String graceMargin) {
		this.graceMargin = graceMargin;
	}

	public String getRepayBaseRate() {
		return repayBaseRate;
	}

	public void setRepayBaseRate(String repayBaseRate) {
		this.repayBaseRate = repayBaseRate;
	}

	public String getRepaySpecialRate() {
		return repaySpecialRate;
	}

	public void setRepaySpecialRate(String repaySpecialRate) {
		this.repaySpecialRate = repaySpecialRate;
	}

	public String getRepayMargin() {
		return repayMargin;
	}

	public void setRepayMargin(String repayMargin) {
		this.repayMargin = repayMargin;
	}

	public String getFinDivision() {
		return finDivision;
	}

	public void setFinDivision(String finDivision) {
		this.finDivision = finDivision;
	}

	public String getAccountsOfficerDesc() {
		return accountsOfficerDesc;
	}

	public void setAccountsOfficerDesc(String accountsOfficerDesc) {
		this.accountsOfficerDesc = accountsOfficerDesc;
	}

	public String getDsaCode() {
		return dsaCode;
	}

	public void setDsaCode(String dsaCode) {
		this.dsaCode = dsaCode;
	}

	public String getDsaDesc() {
		return dsaDesc;
	}

	public void setDsaDesc(String dsaDesc) {
		this.dsaDesc = dsaDesc;
	}

	public String getdMACodeDesc() {
		return dMACodeDesc;
	}

	public void setdMACodeDesc(String dMACodeDesc) {
		this.dMACodeDesc = dMACodeDesc;
	}

	public String getTotalProfit() {
		return totalProfit;
	}

	public void setTotalProfit(String totalProfit) {
		this.totalProfit = totalProfit;
	}

	public String getCurrReducingRate() {
		return currReducingRate;
	}

	public void setCurrReducingRate(String currReducingRate) {
		this.currReducingRate = currReducingRate;
	}

	public String getFirstRepay() {
		return firstRepay;
	}

	public void setFirstRepay(String firstRepay) {
		this.firstRepay = firstRepay;
	}

	public String getLastRepay() {
		return lastRepay;
	}

	public void setLastRepay(String lastRepay) {
		this.lastRepay = lastRepay;
	}

	public String getTotalPriPaid() {
		return totalPriPaid;
	}

	public void setTotalPriPaid(String totalPriPaid) {
		this.totalPriPaid = totalPriPaid;
	}

	public String getTotalPriBalance() {
		return totalPriBalance;
	}

	public void setTotalPriBalance(String totalPriBalance) {
		this.totalPriBalance = totalPriBalance;
	}

	public String getTotalProfitPaid() {
		return totalProfitPaid;
	}

	public void setTotalProfitPaid(String totalProfitPaid) {
		this.totalProfitPaid = totalProfitPaid;
	}

	public String getTotalProfitBalance() {
		return totalProfitBalance;
	}

	public void setTotalProfitBalance(String totalProfitBalance) {
		this.totalProfitBalance = totalProfitBalance;
	}

	public String getPaidBy() {
		return paidBy;
	}

	public void setPaidBy(String paidBy) {
		this.paidBy = paidBy;
	}

	public String getPaidAmount() {
		return paidAmount;
	}

	public void setPaidAmount(String paidAmount) {
		this.paidAmount = paidAmount;
	}

	public String getPaidDetails() {
		return paidDetails;
	}

	public void setPaidDetails(String paidDetails) {
		this.paidDetails = paidDetails;
	}

	public String getPaidDate() {
		return paidDate;
	}

	public void setPaidDate(String paidDate) {
		this.paidDate = paidDate;
	}

	public String getFinReference() {
		return finReference;
	}

	public void setFinReference(String finReference) {
		this.finReference = finReference;
	}

	public String getFinAmount() {
		return finAmount;
	}

	public void setFinAmount(String finAmount) {
		this.finAmount = finAmount;
	}

	public String getFinCcy() {
		return finCcy;
	}

	public void setFinCcy(String finCcy) {
		this.finCcy = finCcy;
	}

	public String getFinStartDate() {
		return finStartDate;
	}

	public void setFinStartDate(String finStartDate) {
		this.finStartDate = finStartDate;
	}

	public String getMaturityDate() {
		return maturityDate;
	}

	public void setMaturityDate(String maturityDate) {
		this.maturityDate = maturityDate;
	}

	public String getNumberOfTerms() {
		return numberOfTerms;
	}

	public void setNumberOfTerms(String numberOfTerms) {
		this.numberOfTerms = numberOfTerms;
	}

	public String getEffectiveRate() {
		return effectiveRate;
	}

	public void setEffectiveRate(String effectiveRate) {
		this.effectiveRate = effectiveRate;
	}

	public String getCustShrtName() {
		return custShrtName;
	}

	public void setCustShrtName(String custShrtName) {
		this.custShrtName = custShrtName;
	}

	public String getDownPayment() {
		return downPayment;
	}

	public void setDownPayment(String downPayment) {
		this.downPayment = downPayment;
	}

	public String getFeeAmount() {
		return feeAmount;
	}

	public void setFeeAmount(String feeAmount) {
		this.feeAmount = feeAmount;
	}

	public void setCustId(long custId) {
		this.custId = custId;
	}

	public long getCustId() {
		return custId;
	}

	public void setRoleCode(String roleCode) {
		this.roleCode = roleCode;
	}

	public String getRoleCode() {
		return roleCode;
	}

	public String getUsrName() {
		return usrName;
	}

	public void setUsrName(String usrName) {
		this.usrName = usrName;
	}

	public String getUsrRole() {
		return usrRole;
	}

	public void setUsrRole(String usrRole) {
		this.usrRole = usrRole;
	}

	public String getPrevUsrName() {
		return prevUsrName;
	}

	public void setPrevUsrName(String prevUsrName) {
		this.prevUsrName = prevUsrName;
	}

	public long getPrevUsrRole() {
		return prevUsrRole;
	}

	public void setPrevUsrRole(long prevUsrRole) {
		this.prevUsrRole = prevUsrRole;
	}

	public String getNextUsrName() {
		return nextUsrName;
	}

	public void setNextUsrName(String nextUsrName) {
		this.nextUsrName = nextUsrName;
	}

	public String getNextUsrRole() {
		return nextUsrRole;
	}

	public void setNextUsrRole(String nextUsrRole) {
		this.nextUsrRole = nextUsrRole;
	}

	public String getTotAmountBD() {
		return totAmountBD;
	}

	public void setTotAmountBD(String totAmountBD) {
		this.totAmountBD = totAmountBD;
	}

	public String getTotAmountUSD() {
		return totAmountUSD;
	}

	public void setTotAmountUSD(String totAmountUSD) {
		this.totAmountUSD = totAmountUSD;
	}

	public void setCafReference(String cAFReference) {
		this.cafReference = cAFReference;
	}

	public String getCafReference() {
		return cafReference;
	}

	public String getWorkflowType() {
		return workFlowType;
	}

	public void setWorkflowType(String workflowType) {
		this.workFlowType = workflowType;
	}

	public String getNextUsrRoleCode() {
		return nextUsrRoleCode;
	}

	public void setNextUsrRoleCode(String nextUsrRoleCode) {
		this.nextUsrRoleCode = nextUsrRoleCode;
	}

	public String getFacilityType() {
		return facilityType;
	}

	public void setFacilityType(String facilityType) {
		this.facilityType = facilityType;
	}

	public String getCountryOfDomicileName() {
		return countryOfDomicileName;
	}

	public void setCountryOfDomicileName(String countryOfDomicileName) {
		this.countryOfDomicileName = countryOfDomicileName;
	}

	public String getCountryOfRiskName() {
		return countryOfRiskName;
	}

	public void setCountryOfRiskName(String countryOfRiskName) {
		this.countryOfRiskName = countryOfRiskName;
	}

	public String getCountryManagerName() {
		return countryManagerName;
	}

	public void setCountryManagerName(String countryManagerName) {
		this.countryManagerName = countryManagerName;
	}

	public String getCustomerGroupName() {
		return customerGroupName;
	}

	public void setCustomerGroupName(String customerGroupName) {
		this.customerGroupName = customerGroupName;
	}

	public String getNatureOfBusinessName() {
		return natureOfBusinessName;
	}

	public void setNatureOfBusinessName(String natureOfBusinessName) {
		this.natureOfBusinessName = natureOfBusinessName;
	}

	public String getRecommendations() {
		return recommendations;
	}

	public void setRecommendations(String recommendations) {
		this.recommendations = recommendations;
	}

	public String getFinPurpose() {
		return finPurpose;
	}

	public void setFinPurpose(String finPurpose) {
		this.finPurpose = finPurpose;
	}

	public String getFinCommitmentRef() {
		return finCommitmentRef;
	}

	public void setFinCommitmentRef(String finCommitmentRef) {
		this.finCommitmentRef = finCommitmentRef;
	}

	public String getFinBranch() {
		return finBranch;
	}

	public void setFinBranch(String finBranch) {
		this.finBranch = finBranch;
	}

	public String getCustCIF() {
		return custCIF;
	}

	public void setCustCIF(String custCIF) {
		this.custCIF = custCIF;
	}

	public String getAuditors() {
		return auditors;
	}

	public void setAuditors(String auditors) {
		this.auditors = auditors;
	}

	public String getLocation() {
		return location;
	}

	public void setLocation(String location) {
		this.location = location;
	}

	public String getAuditType() {
		return auditType;
	}

	public void setAuditType(String auditType) {
		this.auditType = auditType;
	}

	public String getAuditedDate() {
		return auditedDate;
	}

	public void setAuditedDate(String auditedDate) {
		this.auditedDate = auditedDate;
	}

	public String getAuditYear() {
		return auditYear;
	}

	public void setAuditYear(String auditYear) {
		this.auditYear = auditYear;
	}

	public int getAuditPeriod() {
		return auditPeriod;
	}

	public void setAuditPeriod(int auditPeriod) {
		this.auditPeriod = auditPeriod;
	}

	public String getInvestmentRef() {
		return investmentRef;
	}

	public void setInvestmentRef(String investmentRef) {
		this.investmentRef = investmentRef;
	}

	public String getTotPrincipalAmt() {
		return totPrincipalAmt;
	}

	public void setTotPrincipalAmt(String totPrincipalAmt) {
		this.totPrincipalAmt = totPrincipalAmt;
	}

	public String getStartDate() {
		return startDate;
	}

	public void setStartDate(String startDate) {
		this.startDate = startDate;
	}

	public String getPrincipalInvested() {
		return principalInvested;
	}

	public void setPrincipalInvested(String principalInvested) {
		this.principalInvested = principalInvested;
	}

	public String getPrincipalMaturity() {
		return principalMaturity;
	}

	public void setPrincipalMaturity(String principalMaturity) {
		this.principalMaturity = principalMaturity;
	}

	public String getPrincipalDueToInvest() {
		return principalDueToInvest;
	}

	public void setPrincipalDueToInvest(String principalDueToInvest) {
		this.principalDueToInvest = principalDueToInvest;
	}

	public String getAvgPftRate() {
		return avgPftRate;
	}

	public void setAvgPftRate(String avgPftRate) {
		this.avgPftRate = avgPftRate;
	}

	public String getGraceTerms() {
		return graceTerms;
	}

	public void setGraceTerms(String graceTerms) {
		this.graceTerms = graceTerms;
	}

	public String getRcdMaintainSts() {
		return rcdMaintainSts;
	}

	public void setRcdMaintainSts(String rcdMaintainSts) {
		this.rcdMaintainSts = rcdMaintainSts;
	}

	public String getPrincipalDue() {
		return principalDue;
	}

	public void setPrincipalDue(String principalDue) {
		this.principalDue = principalDue;
	}

	public String getProfitDue() {
		return profitDue;
	}

	public void setProfitDue(String profitDue) {
		this.profitDue = profitDue;
	}

	public String getTotalDue() {
		return totalDue;
	}

	public void setTotalDue(String totalDue) {
		this.totalDue = totalDue;
	}

	public String getDueFromDate() {
		return dueFromDate;
	}

	public void setDueFromDate(String dueFromDate) {
		this.dueFromDate = dueFromDate;
	}

	public String getNonFormulaProv() {
		return nonFormulaProv;
	}

	public void setNonFormulaProv(String nonFormulaProv) {
		this.nonFormulaProv = nonFormulaProv;
	}

	public String getProvisionedAmt() {
		return provisionedAmt;
	}

	public void setProvisionedAmt(String provisionedAmt) {
		this.provisionedAmt = provisionedAmt;
	}

	public String getProvisionedAmtCal() {
		return provisionedAmtCal;
	}

	public void setProvisionedAmtCal(String provisionedAmtCal) {
		this.provisionedAmtCal = provisionedAmtCal;
	}

	public String getManualSusp() {
		return manualSusp;
	}

	public void setManualSusp(String manualSusp) {
		this.manualSusp = manualSusp;
	}

	public String getFinSuspDate() {
		return finSuspDate;
	}

	public void setFinSuspDate(String finSuspDate) {
		this.finSuspDate = finSuspDate;
	}

	public String getFinSuspAmt() {
		return finSuspAmt;
	}

	public void setFinSuspAmt(String finSuspAmt) {
		this.finSuspAmt = finSuspAmt;
	}

	public String getFinCurSuspAmt() {
		return finCurSuspAmt;
	}

	public void setFinCurSuspAmt(String finCurSuspAmt) {
		this.finCurSuspAmt = finCurSuspAmt;
	}

	public String getBankName() {
		return bankName;
	}

	public void setBankName(String bankName) {
		this.bankName = bankName;
	}

	public String getProduct() {
		return product;
	}

	public void setProduct(String product) {
		this.product = product;
	}

	public String getTakeoverAmount() {
		return takeoverAmount;
	}

	public void setTakeoverAmount(String takeoverAmount) {
		this.takeoverAmount = takeoverAmount;
	}

	public String getRate() {
		return rate;
	}

	public void setRate(String rate) {
		this.rate = rate;
	}

	public String getCustPortion() {
		return custPortion;
	}

	public void setCustPortion(String custPortion) {
		this.custPortion = custPortion;
	}

	public String getRecieveMail() {
		return recieveMail;
	}

	public void setRecieveMail(String recieveMail) {
		this.recieveMail = recieveMail;
	}

	public HashMap<String, Object> getDeclaredFieldValues() {
		HashMap<String, Object> customerScoringMap = new HashMap<String, Object>();
		for (int i = 0; i < this.getClass().getDeclaredFields().length; i++) {
			try {
				customerScoringMap.put(this.getClass().getDeclaredFields()[i].getName(),
						this.getClass().getDeclaredFields()[i].get(this));
			} catch (SecurityException | IllegalArgumentException | IllegalAccessException e) {
				// Nothing TO DO
			}
		}
		return customerScoringMap;
	}

	public int getPriority() {
		return priority;
	}

	public void setPriority(int priority) {
		this.priority = priority;
	}

	public String getFinCurODAmt() {
		return finCurODAmt;
	}

	public void setFinCurODAmt(String finCurODAmt) {
		this.finCurODAmt = finCurODAmt;
	}

	public int getFinCurODDays() {
		return finCurODDays;
	}

	public void setFinCurODDays(int finCurODDays) {
		this.finCurODDays = finCurODDays;
	}

	public String getFinType() {
		return finType;
	}

	public void setFinType(String finType) {
		this.finType = finType;
	}

	public String getNextRepayDate() {
		return nextRepayDate;
	}

	public void setNextRepayDate(String nextRepayDate) {
		this.nextRepayDate = nextRepayDate;
	}

	public String getInsAmount() {
		return insAmount;
	}

	public void setInsAmount(String insAmount) {
		this.insAmount = insAmount;
	}

	public String getRecordStatus() {
		return recordStatus;
	}

	public void setRecordStatus(String recordStatus) {
		this.recordStatus = recordStatus;
	}

	public String getReceiptPurpose() {
		return receiptPurpose;
	}

	public void setReceiptPurpose(String receiptPurpose) {
		this.receiptPurpose = receiptPurpose;
	}

	public String getFinBranchContact() {
		return finBranchContact;
	}

	public void setFinBranchContact(String finBranchContact) {
		this.finBranchContact = finBranchContact;
	}

	public String getAmount() {
		return amount;
	}

	public void setAmount(String amount) {
		this.amount = amount;
	}

	public String getValueDate() {
		return valueDate;
	}

	public void setValueDate(String valueDate) {
		this.valueDate = valueDate;
	}

	public String getReceiptAmount() {
		return receiptAmount;
	}

	public void setReceiptAmount(String receiptAmount) {
		this.receiptAmount = receiptAmount;
	}

	public String getCustAddrCity() {
		return custAddrCity;
	}

	public void setCustAddrCity(String custAddrCity) {
		this.custAddrCity = custAddrCity;
	}

	public String getFinCurrAssetValue() {
		return finCurrAssetValue;
	}

	public void setFinCurrAssetValue(String finCurrAssetValue) {
		this.finCurrAssetValue = finCurrAssetValue;
	}

	public String getBounceDate() {
		return bounceDate;
	}

	public void setBounceDate(String bounceDate) {
		this.bounceDate = bounceDate;
	}

	public String getBounceReason() {
		return bounceReason;
	}

	public void setBounceReason(String bounceReason) {
		this.bounceReason = bounceReason;
	}

	public String getCancellationReason() {
		return cancellationReason;
	}

	public void setCancellationReason(String cancellationReason) {
		this.cancellationReason = cancellationReason;
	}

}
