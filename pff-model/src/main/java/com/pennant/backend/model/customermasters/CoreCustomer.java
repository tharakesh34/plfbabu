package com.pennant.backend.model.customermasters;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

public class CoreCustomer implements Serializable {

	private static final long serialVersionUID = 7911548308889266685L;

	private String custCIF;
	private long custID;
	private String internalRating;
	private Date dateOfInternalRating;

	private String relationCode;
	private String relationShipCIF;

	private String faxIndemity;
	private String emailIndemity;

	private String kycRiskLevel;
	private String foreignPolicyExposed;
	private String pliticalyExposed;
	private BigDecimal monthlyTurnover = BigDecimal.ZERO;
	private String introducer;
	private String referenceName;
	private String purposeOfRelationShip;
	private String sourceOfIncome;
	private String expectedTypeOfTrans;
	private BigDecimal monthlyOutageVolume = BigDecimal.ZERO;
	private BigDecimal monthlyIncomeVolume = BigDecimal.ZERO;
	private BigDecimal maximumSingleDeposit = BigDecimal.ZERO;
	private BigDecimal maximumSingleWithdrawal = BigDecimal.ZERO;
	private BigDecimal annualIncome = BigDecimal.ZERO;
	private String countryOfOriginOfFunds;
	private String countryOfSourceOfIncome;
	private String sourceOfWealth;
	private String isKYCUptoDate;
	private String listedOnStockExchange;
	private String nameOfExchange;
	private String stockCodeOfCustomer;
	private String customerVisitReport;
	private BigDecimal initialDeposit = BigDecimal.ZERO;
	private BigDecimal futureDeposit = BigDecimal.ZERO;
	private BigDecimal annualTurnOver = BigDecimal.ZERO;
	private String parentCompanyDetails;
	private String nameOfParentCompany;
	private String parentCompanyPlaceOfIncorp;
	private String emirateOfIncop;
	private String nameOfApexCompany;
	private String noOfEmployees;
	private String noOfUAEBranches;
	private String noOfOverseasBranches;
	private String overSeasbranches;
	private String nameOfAuditors;
	private String financialHighlights;
	private String bankingRelationShip;
	private String pFFICertfication;

	private String pOAFlag;
	private String pOACIF;
	private String pOAHoldersname;
	private String passportNumber;
	private String emiratesIDNumber;
	private String nationality;
	private Date pOAIssuancedate;
	private Date pOAExpirydate;
	private Date passportExpiryDate;
	private Date emiratesIDExpiryDate;

	private String empName;
	private String issueCheque;

	private int totalNoOfPartners;
	private String modeOfOperation;
	private String powerOfAttorney;
	private String auditedFinancials;
	private String faxOfIndemity;
	private String indemityEmailAddress;
	private String chequeBookRequest;
	private String currencyOfFinancials;
	private BigDecimal grossProfit = BigDecimal.ZERO;
	private BigDecimal netProfit = BigDecimal.ZERO;
	private BigDecimal shareCapital = BigDecimal.ZERO;
	private BigDecimal throughputAmount = BigDecimal.ZERO;
	private String throughputFrequency;
	private String throughputAccount;
	private String haveBranchInUS;
	private String salaryCurrency;
	private BigDecimal salary = BigDecimal.ZERO;
	private Date salaryDateFreq;
	private String businessType;
	private String nameOfBusiness;

	private boolean newRecord;

	public CoreCustomer() {
	    super();
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

	public boolean isNewRecord() {
		return newRecord;
	}

	public void setNewRecord(boolean newRecord) {
		this.newRecord = newRecord;
	}

	public long getCustID() {
		return custID;
	}

	public void setCustID(long custID) {
		this.custID = custID;
	}

	public String getInternalRating() {
		return internalRating;
	}

	public void setInternalRating(String internalRating) {
		this.internalRating = internalRating;
	}

	public Date getDateOfInternalRating() {
		return dateOfInternalRating;
	}

	public void setDateOfInternalRating(Date dateOfInternalRating) {
		this.dateOfInternalRating = dateOfInternalRating;
	}

	public String getRelationCode() {
		return relationCode;
	}

	public void setRelationCode(String relationCode) {
		this.relationCode = relationCode;
	}

	public String getRelationShipCIF() {
		return relationShipCIF;
	}

	public void setRelationShipCIF(String relationShipCIF) {
		this.relationShipCIF = relationShipCIF;
	}

	public String getFaxIndemity() {
		return faxIndemity;
	}

	public void setFaxIndemity(String faxIndemity) {
		this.faxIndemity = faxIndemity;
	}

	public String getEmailIndemity() {
		return emailIndemity;
	}

	public void setEmailIndemity(String emailIndemity) {
		this.emailIndemity = emailIndemity;
	}

	public String getKycRiskLevel() {
		return kycRiskLevel;
	}

	public void setKycRiskLevel(String kycRiskLevel) {
		this.kycRiskLevel = kycRiskLevel;
	}

	public String getForeignPolicyExposed() {
		return foreignPolicyExposed;
	}

	public void setForeignPolicyExposed(String foreignPolicyExposed) {
		this.foreignPolicyExposed = foreignPolicyExposed;
	}

	public String getPliticalyExposed() {
		return pliticalyExposed;
	}

	public void setPliticalyExposed(String pliticalyExposed) {
		this.pliticalyExposed = pliticalyExposed;
	}

	public BigDecimal getMonthlyTurnover() {
		return monthlyTurnover;
	}

	public void setMonthlyTurnover(BigDecimal monthlyTurnover) {
		this.monthlyTurnover = monthlyTurnover;
	}

	public String getIntroducer() {
		return introducer;
	}

	public void setIntroducer(String introducer) {
		this.introducer = introducer;
	}

	public String getReferenceName() {
		return referenceName;
	}

	public void setReferenceName(String referenceName) {
		this.referenceName = referenceName;
	}

	public String getPurposeOfRelationShip() {
		return purposeOfRelationShip;
	}

	public void setPurposeOfRelationShip(String purposeOfRelationShip) {
		this.purposeOfRelationShip = purposeOfRelationShip;
	}

	public String getSourceOfIncome() {
		return sourceOfIncome;
	}

	public void setSourceOfIncome(String sourceOfIncome) {
		this.sourceOfIncome = sourceOfIncome;
	}

	public String getExpectedTypeOfTrans() {
		return expectedTypeOfTrans;
	}

	public void setExpectedTypeOfTrans(String expectedTypeOfTrans) {
		this.expectedTypeOfTrans = expectedTypeOfTrans;
	}

	public BigDecimal getMonthlyOutageVolume() {
		return monthlyOutageVolume;
	}

	public void setMonthlyOutageVolume(BigDecimal monthlyOutageVolume) {
		this.monthlyOutageVolume = monthlyOutageVolume;
	}

	public BigDecimal getMonthlyIncomeVolume() {
		return monthlyIncomeVolume;
	}

	public void setMonthlyIncomeVolume(BigDecimal monthlyIncomeVolume) {
		this.monthlyIncomeVolume = monthlyIncomeVolume;
	}

	public BigDecimal getMaximumSingleDeposit() {
		return maximumSingleDeposit;
	}

	public void setMaximumSingleDeposit(BigDecimal maximumSingleDeposit) {
		this.maximumSingleDeposit = maximumSingleDeposit;
	}

	public BigDecimal getMaximumSingleWithdrawal() {
		return maximumSingleWithdrawal;
	}

	public void setMaximumSingleWithdrawal(BigDecimal maximumSingleWithdrawal) {
		this.maximumSingleWithdrawal = maximumSingleWithdrawal;
	}

	public BigDecimal getAnnualIncome() {
		return annualIncome;
	}

	public void setAnnualIncome(BigDecimal annualIncome) {
		this.annualIncome = annualIncome;
	}

	public String getCountryOfOriginOfFunds() {
		return countryOfOriginOfFunds;
	}

	public void setCountryOfOriginOfFunds(String countryOfOriginOfFunds) {
		this.countryOfOriginOfFunds = countryOfOriginOfFunds;
	}

	public String getCountryOfSourceOfIncome() {
		return countryOfSourceOfIncome;
	}

	public void setCountryOfSourceOfIncome(String countryOfSourceOfIncome) {
		this.countryOfSourceOfIncome = countryOfSourceOfIncome;
	}

	public String getSourceOfWealth() {
		return sourceOfWealth;
	}

	public void setSourceOfWealth(String sourceOfWealth) {
		this.sourceOfWealth = sourceOfWealth;
	}

	public String getIsKYCUptoDate() {
		return isKYCUptoDate;
	}

	public void setIsKYCUptoDate(String isKYCUptoDate) {
		this.isKYCUptoDate = isKYCUptoDate;
	}

	public String getListedOnStockExchange() {
		return listedOnStockExchange;
	}

	public void setListedOnStockExchange(String listedOnStockExchange) {
		this.listedOnStockExchange = listedOnStockExchange;
	}

	public String getNameOfExchange() {
		return nameOfExchange;
	}

	public void setNameOfExchange(String nameOfExchange) {
		this.nameOfExchange = nameOfExchange;
	}

	public String getStockCodeOfCustomer() {
		return stockCodeOfCustomer;
	}

	public void setStockCodeOfCustomer(String stockCodeOfCustomer) {
		this.stockCodeOfCustomer = stockCodeOfCustomer;
	}

	public String getCustomerVisitReport() {
		return customerVisitReport;
	}

	public void setCustomerVisitReport(String customerVisitReport) {
		this.customerVisitReport = customerVisitReport;
	}

	public BigDecimal getInitialDeposit() {
		return initialDeposit;
	}

	public void setInitialDeposit(BigDecimal initialDeposit) {
		this.initialDeposit = initialDeposit;
	}

	public BigDecimal getFutureDeposit() {
		return futureDeposit;
	}

	public void setFutureDeposit(BigDecimal futureDeposit) {
		this.futureDeposit = futureDeposit;
	}

	public BigDecimal getAnnualTurnOver() {
		return annualTurnOver;
	}

	public void setAnnualTurnOver(BigDecimal annualTurnOver) {
		this.annualTurnOver = annualTurnOver;
	}

	public String getParentCompanyDetails() {
		return parentCompanyDetails;
	}

	public void setParentCompanyDetails(String parentCompanyDetails) {
		this.parentCompanyDetails = parentCompanyDetails;
	}

	public String getNameOfParentCompany() {
		return nameOfParentCompany;
	}

	public void setNameOfParentCompany(String nameOfParentCompany) {
		this.nameOfParentCompany = nameOfParentCompany;
	}

	public String getParentCompanyPlaceOfIncorp() {
		return parentCompanyPlaceOfIncorp;
	}

	public void setParentCompanyPlaceOfIncorp(String parentCompanyPlaceOfIncorp) {
		this.parentCompanyPlaceOfIncorp = parentCompanyPlaceOfIncorp;
	}

	public String getEmirateOfIncop() {
		return emirateOfIncop;
	}

	public void setEmirateOfIncop(String emirateOfIncop) {
		this.emirateOfIncop = emirateOfIncop;
	}

	public String getNameOfApexCompany() {
		return nameOfApexCompany;
	}

	public void setNameOfApexCompany(String nameOfApexCompany) {
		this.nameOfApexCompany = nameOfApexCompany;
	}

	public String getNoOfEmployees() {
		return noOfEmployees;
	}

	public void setNoOfEmployees(String noOfEmployees) {
		this.noOfEmployees = noOfEmployees;
	}

	public String getNoOfUAEBranches() {
		return noOfUAEBranches;
	}

	public void setNoOfUAEBranches(String noOfUAEBranches) {
		this.noOfUAEBranches = noOfUAEBranches;
	}

	public String getNoOfOverseasBranches() {
		return noOfOverseasBranches;
	}

	public void setNoOfOverseasBranches(String noOfOverseasBranches) {
		this.noOfOverseasBranches = noOfOverseasBranches;
	}

	public String getOverSeasbranches() {
		return overSeasbranches;
	}

	public void setOverSeasbranches(String overSeasbranches) {
		this.overSeasbranches = overSeasbranches;
	}

	public String getNameOfAuditors() {
		return nameOfAuditors;
	}

	public void setNameOfAuditors(String nameOfAuditors) {
		this.nameOfAuditors = nameOfAuditors;
	}

	public String getFinancialHighlights() {
		return financialHighlights;
	}

	public void setFinancialHighlights(String financialHighlights) {
		this.financialHighlights = financialHighlights;
	}

	public String getBankingRelationShip() {
		return bankingRelationShip;
	}

	public void setBankingRelationShip(String bankingRelationShip) {
		this.bankingRelationShip = bankingRelationShip;
	}

	public String getpFFICertfication() {
		return pFFICertfication;
	}

	public void setpFFICertfication(String pFFICertfication) {
		this.pFFICertfication = pFFICertfication;
	}

	public String getpOAFlag() {
		return pOAFlag;
	}

	public void setpOAFlag(String pOAFlag) {
		this.pOAFlag = pOAFlag;
	}

	public String getpOACIF() {
		return pOACIF;
	}

	public void setpOACIF(String pOACIF) {
		this.pOACIF = pOACIF;
	}

	public String getpOAHoldersname() {
		return pOAHoldersname;
	}

	public void setpOAHoldersname(String pOAHoldersname) {
		this.pOAHoldersname = pOAHoldersname;
	}

	public String getPassportNumber() {
		return passportNumber;
	}

	public void setPassportNumber(String passportNumber) {
		this.passportNumber = passportNumber;
	}

	public String getEmiratesIDNumber() {
		return emiratesIDNumber;
	}

	public void setEmiratesIDNumber(String emiratesIDNumber) {
		this.emiratesIDNumber = emiratesIDNumber;
	}

	public String getNationality() {
		return nationality;
	}

	public void setNationality(String nationality) {
		this.nationality = nationality;
	}

	public Date getpOAIssuancedate() {
		return pOAIssuancedate;
	}

	public void setpOAIssuancedate(Date pOAIssuancedate) {
		this.pOAIssuancedate = pOAIssuancedate;
	}

	public Date getpOAExpirydate() {
		return pOAExpirydate;
	}

	public void setpOAExpirydate(Date pOAExpirydate) {
		this.pOAExpirydate = pOAExpirydate;
	}

	public Date getPassportExpiryDate() {
		return passportExpiryDate;
	}

	public void setPassportExpiryDate(Date passportExpiryDate) {
		this.passportExpiryDate = passportExpiryDate;
	}

	public Date getEmiratesIDExpiryDate() {
		return emiratesIDExpiryDate;
	}

	public void setEmiratesIDExpiryDate(Date emiratesIDExpiryDate) {
		this.emiratesIDExpiryDate = emiratesIDExpiryDate;
	}

	public String getEmpName() {
		return empName;
	}

	public void setEmpName(String empName) {
		this.empName = empName;
	}

	public String getIssueCheque() {
		return issueCheque;
	}

	public void setIssueCheque(String issueCheque) {
		this.issueCheque = issueCheque;
	}

	public String getModeOfOperation() {
		return modeOfOperation;
	}

	public void setModeOfOperation(String modeOfOperation) {
		this.modeOfOperation = modeOfOperation;
	}

	public String getPowerOfAttorney() {
		return powerOfAttorney;
	}

	public void setPowerOfAttorney(String powerOfAttorney) {
		this.powerOfAttorney = powerOfAttorney;
	}

	public String getAuditedFinancials() {
		return auditedFinancials;
	}

	public void setAuditedFinancials(String auditedFinancials) {
		this.auditedFinancials = auditedFinancials;
	}

	public String getFaxOfIndemity() {
		return faxOfIndemity;
	}

	public void setFaxOfIndemity(String faxOfIndemity) {
		this.faxOfIndemity = faxOfIndemity;
	}

	public String getIndemityEmailAddress() {
		return indemityEmailAddress;
	}

	public void setIndemityEmailAddress(String indemityEmailAddress) {
		this.indemityEmailAddress = indemityEmailAddress;
	}

	public String getChequeBookRequest() {
		return chequeBookRequest;
	}

	public void setChequeBookRequest(String chequeBookRequest) {
		this.chequeBookRequest = chequeBookRequest;
	}

	public String getCurrencyOfFinancials() {
		return currencyOfFinancials;
	}

	public void setCurrencyOfFinancials(String currencyOfFinancials) {
		this.currencyOfFinancials = currencyOfFinancials;
	}

	public BigDecimal getGrossProfit() {
		return grossProfit;
	}

	public void setGrossProfit(BigDecimal grossProfit) {
		this.grossProfit = grossProfit;
	}

	public BigDecimal getNetProfit() {
		return netProfit;
	}

	public void setNetProfit(BigDecimal netProfit) {
		this.netProfit = netProfit;
	}

	public BigDecimal getShareCapital() {
		return shareCapital;
	}

	public void setShareCapital(BigDecimal shareCapital) {
		this.shareCapital = shareCapital;
	}

	public BigDecimal getThroughputAmount() {
		return throughputAmount;
	}

	public void setThroughputAmount(BigDecimal throughputAmount) {
		this.throughputAmount = throughputAmount;
	}

	public String getThroughputFrequency() {
		return throughputFrequency;
	}

	public void setThroughputFrequency(String throughputFrequency) {
		this.throughputFrequency = throughputFrequency;
	}

	public String getThroughputAccount() {
		return throughputAccount;
	}

	public void setThroughputAccount(String throughputAccount) {
		this.throughputAccount = throughputAccount;
	}

	public String getHaveBranchInUS() {
		return haveBranchInUS;
	}

	public void setHaveBranchInUS(String haveBranchInUS) {
		this.haveBranchInUS = haveBranchInUS;
	}

	public String getSalaryCurrency() {
		return salaryCurrency;
	}

	public void setSalaryCurrency(String salaryCurrency) {
		this.salaryCurrency = salaryCurrency;
	}

	public BigDecimal getSalary() {
		return salary;
	}

	public void setSalary(BigDecimal salary) {
		this.salary = salary;
	}

	public Date getSalaryDateFreq() {
		return salaryDateFreq;
	}

	public void setSalaryDateFreq(Date salaryDateFreq) {
		this.salaryDateFreq = salaryDateFreq;
	}

	public String getBusinessType() {
		return businessType;
	}

	public void setBusinessType(String businessType) {
		this.businessType = businessType;
	}

	public String getNameOfBusiness() {
		return nameOfBusiness;
	}

	public void setNameOfBusiness(String nameOfBusiness) {
		this.nameOfBusiness = nameOfBusiness;
	}

	public int getTotalNoOfPartners() {
		return totalNoOfPartners;
	}

	public void setTotalNoOfPartners(int totalNoOfPartners) {
		this.totalNoOfPartners = totalNoOfPartners;
	}
}
