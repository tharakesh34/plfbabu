package com.pennant.backend.model.mail;

import java.util.HashMap;
import java.util.Map;

public class MailTemplateData {

	// Customer Details
	private String custShrtName = "";
	private String custCIF = "";
	private String custCoreBank = "";
	private String custPanNumber = "";
	private String custAadharNumber = "";
	private String custMobileNumber = "";
	private String custEmailId = "";
	private String custAddrLine1 = "";
	private String custAddrLine2 = "";
	private String custAddrHNbr = "";
	private String custAddrFlatNo = "";
	private String custAddrStreet = "";
	private String custAddrCountry = "";
	private String custAddrCity = "";
	private String custAddrProvince = "";
	private String custAddrDistrict = "";
	private String custAddrPincode = "";
	private String custSalutation = "";

	// User Details
	private String userName = "";
	private String userBranch = "";
	private String userDepartment = "";
	private String appDate = "";

	// User Branch Address Details
	private String userBranchAddrLine1 = "";
	private String userBranchAddrLine2 = "";
	private String userBranchAddrHNbr = "";
	private String userBranchAddrFlatNo = "";
	private String userBranchAddrStreet = "";
	private String userBranchAddrCountry = "";
	private String userBranchAddrCity = "";
	private String userBranchAddrProvince = "";
	private String userBranchAddrDistrict = "";
	private String userBranchAddrPincode = "";
	private String userBranchPhone = "";

	// Existing Beans
	private String usrName = "";
	private String usrRole = "";
	private String prevUsrName = "";
	private long prevUsrRole = 0;
	private String nextUsrName = "";
	private String nextUsrRole = "";
	private String nextUsrRoleCode = "";
	private String workFlowType = "";
	// ----------------------------

	// Loan Details
	private String finReference = "";
	private String finStartDate = "";
	private int priority = 0;
	private String finAmount = "";
	private String finAssetValue = "";
	private String finCurrAssetValue = "";
	private String repaymentFrequency = "";
	private String firstDueDate = "";
	private String maturityDate = "";
	private String gracePeriod = "";
	private String repayperiod = "";
	private String graceRate = "";
	private String repayRate = "";
	private String graceBaseRate = "";
	private String graceSpecialRate = "";
	private String graceMargin = "";
	private String repayBaseRate = "";
	private String repaySpecialRate = "";
	private String repayMargin = "";
	private String finBranch = "";
	private String finCcy = "";
	private String finDivision = "";
	private String accountsOfficerDesc = "";
	private String dsaCode = "";
	private String dsaDesc = "";
	private String dMACodeDesc = "";
	private String totalProfit = "";
	private String currReducingRate = "";
	private String firstRepay = "";
	private String lastRepay = "";
	private String totalPriPaid = "";
	private String totalPriBalance = "";
	private String totalProfitPaid = "";
	private String totalProfitBalance = "";
	private String nextRepayDate = "";
	private String repaymentDate = "";
	private String emiAmount = "";
	private String repayAmount = "";

	// User Branch Address Details
	private String finBranchAddrLine1 = "";
	private String finBranchAddrLine2 = "";
	private String finBranchAddrHNbr = "";
	private String finBranchAddrFlatNo = "";
	private String finBranchAddrStreet = "";
	private String finBranchAddrCountry = "";
	private String finBranchAddrCity = "";
	private String finBranchAddrProvince = "";
	private String finBranchAddrDistrict = "";
	private String finBranchAddrPincode = "";
	private String finBranchPhone = "";

	// existing beans
	private String numberOfTerms = "";
	private String graceTerms = "";
	private String effectiveRate = "";
	private String downPayment = "";
	private String feeAmount = "";
	private String finType = "";
	private String finTypeDesc = "";

	// Receipts
	private String amount = "";
	private String receiptAmount = "";
	private String valueDate = "";
	private String bounceDate = "";
	private String bounceReason = "";
	private String cancellationReason = "";
	private String limitAmount = "";
	// ---------------------------------

	// facility
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
	private String drawingPower;
	private String currentDate;
	// Including grace and installments
	private String totalTenor;

	private String qryDesc;

	public MailTemplateData() {
	    super();
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

	public String getCustSalutation() {
		return custSalutation;
	}

	public void setCustSalutation(String custSalutation) {
		this.custSalutation = custSalutation;
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

	public Map<String, Object> getDeclaredFieldValues() {
		Map<String, Object> customerScoringMap = new HashMap<>();
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

	public String getRepaymentDate() {
		return repaymentDate;
	}

	public void setRepaymentDate(String repaymentDate) {
		this.repaymentDate = repaymentDate;
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

	public String getFinBranchAddrLine1() {
		return finBranchAddrLine1;
	}

	public void setFinBranchAddrLine1(String finBranchAddrLine1) {
		this.finBranchAddrLine1 = finBranchAddrLine1;
	}

	public String getFinBranchAddrLine2() {
		return finBranchAddrLine2;
	}

	public void setFinBranchAddrLine2(String finBranchAddrLine2) {
		this.finBranchAddrLine2 = finBranchAddrLine2;
	}

	public String getFinBranchAddrHNbr() {
		return finBranchAddrHNbr;
	}

	public void setFinBranchAddrHNbr(String finBranchAddrHNbr) {
		this.finBranchAddrHNbr = finBranchAddrHNbr;
	}

	public String getFinBranchAddrFlatNo() {
		return finBranchAddrFlatNo;
	}

	public void setFinBranchAddrFlatNo(String finBranchAddrFlatNo) {
		this.finBranchAddrFlatNo = finBranchAddrFlatNo;
	}

	public String getFinBranchAddrStreet() {
		return finBranchAddrStreet;
	}

	public void setFinBranchAddrStreet(String finBranchAddrStreet) {
		this.finBranchAddrStreet = finBranchAddrStreet;
	}

	public String getFinBranchAddrCountry() {
		return finBranchAddrCountry;
	}

	public void setFinBranchAddrCountry(String finBranchAddrCountry) {
		this.finBranchAddrCountry = finBranchAddrCountry;
	}

	public String getFinBranchAddrCity() {
		return finBranchAddrCity;
	}

	public void setFinBranchAddrCity(String finBranchAddrCity) {
		this.finBranchAddrCity = finBranchAddrCity;
	}

	public String getFinBranchAddrProvince() {
		return finBranchAddrProvince;
	}

	public void setFinBranchAddrProvince(String finBranchAddrProvince) {
		this.finBranchAddrProvince = finBranchAddrProvince;
	}

	public String getFinBranchAddrDistrict() {
		return finBranchAddrDistrict;
	}

	public void setFinBranchAddrDistrict(String finBranchAddrDistrict) {
		this.finBranchAddrDistrict = finBranchAddrDistrict;
	}

	public String getFinBranchAddrPincode() {
		return finBranchAddrPincode;
	}

	public void setFinBranchAddrPincode(String finBranchAddrPincode) {
		this.finBranchAddrPincode = finBranchAddrPincode;
	}

	public String getFinBranchPhone() {
		return finBranchPhone;
	}

	public void setFinBranchPhone(String finBranchPhone) {
		this.finBranchPhone = finBranchPhone;
	}

	public String getUserBranchAddrLine1() {
		return userBranchAddrLine1;
	}

	public void setUserBranchAddrLine1(String userBranchAddrLine1) {
		this.userBranchAddrLine1 = userBranchAddrLine1;
	}

	public String getUserBranchAddrLine2() {
		return userBranchAddrLine2;
	}

	public void setUserBranchAddrLine2(String userBranchAddrLine2) {
		this.userBranchAddrLine2 = userBranchAddrLine2;
	}

	public String getUserBranchAddrHNbr() {
		return userBranchAddrHNbr;
	}

	public void setUserBranchAddrHNbr(String userBranchAddrHNbr) {
		this.userBranchAddrHNbr = userBranchAddrHNbr;
	}

	public String getUserBranchAddrFlatNo() {
		return userBranchAddrFlatNo;
	}

	public void setUserBranchAddrFlatNo(String userBranchAddrFlatNo) {
		this.userBranchAddrFlatNo = userBranchAddrFlatNo;
	}

	public String getUserBranchAddrStreet() {
		return userBranchAddrStreet;
	}

	public void setUserBranchAddrStreet(String userBranchAddrStreet) {
		this.userBranchAddrStreet = userBranchAddrStreet;
	}

	public String getUserBranchAddrCountry() {
		return userBranchAddrCountry;
	}

	public void setUserBranchAddrCountry(String userBranchAddrCountry) {
		this.userBranchAddrCountry = userBranchAddrCountry;
	}

	public String getUserBranchAddrCity() {
		return userBranchAddrCity;
	}

	public void setUserBranchAddrCity(String userBranchAddrCity) {
		this.userBranchAddrCity = userBranchAddrCity;
	}

	public String getUserBranchAddrProvince() {
		return userBranchAddrProvince;
	}

	public void setUserBranchAddrProvince(String userBranchAddrProvince) {
		this.userBranchAddrProvince = userBranchAddrProvince;
	}

	public String getUserBranchAddrDistrict() {
		return userBranchAddrDistrict;
	}

	public void setUserBranchAddrDistrict(String userBranchAddrDistrict) {
		this.userBranchAddrDistrict = userBranchAddrDistrict;
	}

	public String getUserBranchAddrPincode() {
		return userBranchAddrPincode;
	}

	public void setUserBranchAddrPincode(String userBranchAddrPincode) {
		this.userBranchAddrPincode = userBranchAddrPincode;
	}

	public String getUserBranchPhone() {
		return userBranchPhone;
	}

	public void setUserBranchPhone(String userBranchPhone) {
		this.userBranchPhone = userBranchPhone;
	}

	public String getDrawingPower() {
		return drawingPower;
	}

	public void setDrawingPower(String drawingPower) {
		this.drawingPower = drawingPower;
	}

	public String getCurrentDate() {
		return currentDate;
	}

	public void setCurrentDate(String currentDate) {
		this.currentDate = currentDate;
	}

	public String getLimitAmount() {
		return limitAmount;
	}

	public void setLimitAmount(String limitAmount) {
		this.limitAmount = limitAmount;
	}

	public String getEmiAmount() {
		return emiAmount;
	}

	public void setEmiAmount(String emiAmount) {
		this.emiAmount = emiAmount;
	}

	public String getRepayAmount() {
		return repayAmount;
	}

	public void setRepayAmount(String repayAmount) {
		this.repayAmount = repayAmount;
	}

	public String getTotalTenor() {
		return totalTenor;
	}

	public void setTotalTenor(String totalTenor) {
		this.totalTenor = totalTenor;
	}

	public String getFinTypeDesc() {
		return finTypeDesc;
	}

	public void setFinTypeDesc(String finTypeDesc) {
		this.finTypeDesc = finTypeDesc;
	}

	public String getQryDesc() {
		return qryDesc;
	}

	public void setQryDesc(String qryDesc) {
		this.qryDesc = qryDesc;
	}

}
