package com.pennant.backend.model.loandetail;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import com.pennant.backend.model.WSReturnStatus;
import com.pennant.backend.model.emiholidays.EMIHolidays;
import com.pennant.backend.model.gracedetails.GraceDetails;
import com.pennant.backend.model.loanbranch.LoanBranch;
import com.pennant.backend.model.loanschedules.LoanSchedules;
import com.pennant.backend.model.loansummary.LoanSummary;

@XmlType(propOrder = { "loanName", "custDOB", "finReference", "finType", "finTypeDesc", "finCcy", "profitDaysBasis",
		"cif", "shortName", "finBranch", "finStartDate", "finAmount", "downPayBank", "downPaySupl", "finPurpose",
		"finIsActive", "accountsOfficerReference", "dsaCode", "tdsApplicable", "baseProduct", "custSegmentation",
		"existingLanRefNo", "rsa", "finRepayMethod", "verification", "finContractDate", "leadSource", "poSource",
		"sourcingBranch", "sourChannelCategory", "applicationNo", "reqRepayAmount", "repayRateBasis", "repayProfitRate",
		"repayBaseRate", "repaySpecialRate", "repayMargin", "scheduleMethod", "repayPftFrq", "nextRepayPftDate",
		"repayRvwFrq", "nextRepayRvwDate", "repayCpzFrq", "nextRepayCpzDate", "repayFrq", "nextRepayDate",
		"maturityDate", "noOfMonths", "finRepayPftOnFrq", "rpyMinRate", "rpyMaxRate", "alwBPI", "bpiTreatment",
		"bpiPftDaysBasis", "reqLoanAmt", "reqLoanTenor", "calMaturity", "finAssetValue", "finCurrAssetValue",
		"productCategory", "dmaCodeReference", "referralId", "employeeName", "quickDisb", "firstDisbDate",
		"lastDisbDate", "stage", "status", "connectorReference", "fixedRateTenor", "fixedTenorRate", "repayAmount",
		"advType", "advTerms", "closedDate", "manufacturerDealerId", "finOcrRequired", "tdsType", "escrow",
		"custBankId", "overdraftTxnChrgReq", "overdraftCalcChrg", "overdraftChrCalOn", "overdraftChrgAmtOrPerc",
		"overdraftTxnChrgFeeType", "accNumber", "netDisbursementAmount" })

@XmlRootElement(name = "loan")
@XmlAccessorType(XmlAccessType.NONE)
public class LoanDetail {

	@XmlElement
	private String loanName;
	@XmlElement
	private Date custDOB;
	@XmlElement
	private String finReference;
	@XmlElement
	private String finType;
	@XmlElement
	private String finTypeDesc;
	@XmlElement
	private String finCcy;
	@XmlElement
	private String profitDaysBasis;
	@XmlElement
	private String cif;//
	@XmlElement
	private String shortName;//
	@XmlElement
	private String finBranch;
	@XmlElement
	private Date finStartDate;
	@XmlElement
	private BigDecimal finAmount;
	@XmlElement
	private BigDecimal downPayBank;
	@XmlElement
	private BigDecimal downPaySupl;
	@XmlElement
	private String finPurpose;
	@XmlElement
	private Boolean finIsActive;
	@XmlElement(name = "accountsOfficer")
	private String accountsOfficerReference;//
	@XmlElement
	private String dsaCode;
	@XmlElement
	private Boolean tdsApplicable;
	@XmlElement
	private String baseProduct;
	@XmlElement
	private String custSegmentation;
	@XmlElement
	private String existingLanRefNo;
	@XmlElement
	private Boolean rsa;
	@XmlElement
	private String finRepayMethod;
	@XmlElement
	private String verification;
	@XmlElement
	private Date finContractDate;
	@XmlElement
	private String leadSource;
	@XmlElement
	private String poSource;
	@XmlElement
	private String sourcingBranch;
	@XmlElement
	private String sourChannelCategory;
	@XmlElement
	private String applicationNo;
	@XmlElement
	private BigDecimal reqRepayAmount;
	@XmlElement
	private String repayRateBasis;
	@XmlElement(name = "repayPftRate")
	private BigDecimal repayProfitRate;
	@XmlElement
	private String repayBaseRate;
	@XmlElement
	private String repaySpecialRate;
	@XmlElement
	private BigDecimal repayMargin;
	@XmlElement
	private String scheduleMethod;
	@XmlElement
	private String repayPftFrq;
	@XmlElement
	private Date nextRepayPftDate;
	@XmlElement
	private String repayRvwFrq;
	@XmlElement
	private Date nextRepayRvwDate;
	@XmlElement
	private String repayCpzFrq;
	@XmlElement
	private Date nextRepayCpzDate;
	@XmlElement
	private String repayFrq;
	@XmlElement
	private Date nextRepayDate;
	@XmlElement
	private Date maturityDate;
	@XmlElement
	private Integer noOfMonths;
	@XmlElement
	private Boolean finRepayPftOnFrq;
	@XmlElement(name = "repayMinRate")
	private BigDecimal rpyMinRate;
	@XmlElement(name = "repayMaxRate")
	private BigDecimal rpyMaxRate;
	@XmlElement(name = "alwBpiTreatment")
	private Boolean alwBPI;
	@XmlElement(name = "dftBpiTreatment")
	private String bpiTreatment;
	@XmlElement(name = "bpiPftDaysBasis")
	private String bpiPftDaysBasis;
	@XmlElement
	private BigDecimal reqLoanAmt;
	@XmlElement
	private Integer reqLoanTenor;
	@XmlElement
	private Date calMaturity;
	@XmlElement
	private BigDecimal finAssetValue;
	@XmlElement
	private BigDecimal finCurrAssetValue;
	@XmlElement
	private String productCategory;
	@XmlElement(name = "dmaCode")
	private String dmaCodeReference;
	@XmlElement
	private String referralId;
	@XmlElement
	private String employeeName;
	@XmlElement
	private Boolean quickDisb;
	@XmlElement
	private Date firstDisbDate;
	@XmlElement
	private Date lastDisbDate;
	@XmlElement
	private String stage;
	@XmlElement
	private String status;
	@XmlElement(name = "connector")
	private String connectorReference;
	@XmlElement
	private Integer fixedRateTenor;
	@XmlElement
	private BigDecimal fixedTenorRate;
	@XmlElement
	private BigDecimal repayAmount;
	@XmlElement
	private String advType;
	@XmlElement(name = "advEMITerms")
	private Integer advTerms;
	@XmlElement
	private Date closedDate;
	@XmlElement
	private Long manufacturerDealerId;
	@XmlElement
	private Boolean finOcrRequired;
	@XmlElement
	private String tdsType;
	@XmlElement
	private Boolean escrow;
	@XmlElement
	private Long custBankId;
	@XmlElement(name = "txnChrgReq")
	private Boolean overdraftTxnChrgReq;
	@XmlElement(name = "oDCalculatedCharge")
	private String overdraftCalcChrg;
	@XmlElement(name = "oDChargeCalOn")
	private String overdraftChrCalOn;
	@XmlElement(name = "oDChargeAmtOrPerc")
	private BigDecimal overdraftChrgAmtOrPerc;
	@XmlElement(name = "txnChrgCode")
	private Long overdraftTxnChrgFeeType;
	@XmlElement
	private String accNumber;
	@XmlElement
	private LoanSummary loanSummary;
	@XmlElementWrapper(name = "schedules")
	@XmlElement
	private List<LoanSchedules> loanSchedules;
	@XmlElement(name = "branch")
	private LoanBranch loanBranch;
	@XmlElement(name = "grace")
	private GraceDetails graceDetails;
	@XmlElement(name = "EMIHolidays")
	private EMIHolidays emiHolidays;
	@XmlElement
	private WSReturnStatus returnStatus;
	@XmlElement
	private BigDecimal netDisbursementAmount;
	@XmlElement
	private Date fromDate;
	@XmlElement
	private Date toDate;

	public LoanDetail() {
		super();
	}

	public String getFinReference() {
		return finReference;
	}

	public void setFinReference(String finReference) {
		this.finReference = finReference;
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

	public String getFinCcy() {
		return finCcy;
	}

	public void setFinCcy(String finCcy) {
		this.finCcy = finCcy;
	}

	public String getProfitDaysBasis() {
		return profitDaysBasis;
	}

	public void setProfitDaysBasis(String profitDaysBasis) {
		this.profitDaysBasis = profitDaysBasis;
	}

	public String getCif() {
		return cif;
	}

	public void setCif(String cif) {
		this.cif = cif;
	}

	public String getShortName() {
		return shortName;
	}

	public void setShortName(String shortName) {
		this.shortName = shortName;
	}

	public String getFinBranch() {
		return finBranch;
	}

	public void setFinBranch(String finBranch) {
		this.finBranch = finBranch;
	}

	public Date getFinStartDate() {
		return finStartDate;
	}

	public void setFinStartDate(Date finStartDate) {
		this.finStartDate = finStartDate;
	}

	public BigDecimal getFinAmount() {
		return finAmount;
	}

	public void setFinAmount(BigDecimal finAmount) {
		this.finAmount = finAmount;
	}

	public BigDecimal getDownPayBank() {
		return downPayBank;
	}

	public void setDownPayBank(BigDecimal downPayBank) {
		this.downPayBank = downPayBank;
	}

	public BigDecimal getDownPaySupl() {
		return downPaySupl;
	}

	public void setDownPaySupl(BigDecimal downPaySupl) {
		this.downPaySupl = downPaySupl;
	}

	public String getFinPurpose() {
		return finPurpose;
	}

	public void setFinPurpose(String finPurpose) {
		this.finPurpose = finPurpose;
	}

	public Boolean getFinIsActive() {
		return finIsActive;
	}

	public void setFinIsActive(Boolean finIsActive) {
		this.finIsActive = finIsActive;
	}

	public String getAccountsOfficerReference() {
		return accountsOfficerReference;
	}

	public void setAccountsOfficerReference(String accountsOfficerReference) {
		this.accountsOfficerReference = accountsOfficerReference;
	}

	public String getDsaCode() {
		return dsaCode;
	}

	public void setDsaCode(String dsaCode) {
		this.dsaCode = dsaCode;
	}

	public Boolean getTdsApplicable() {
		return tdsApplicable;
	}

	public void setTdsApplicable(Boolean tdsApplicable) {
		this.tdsApplicable = tdsApplicable;
	}

	public String getBaseProduct() {
		return baseProduct;
	}

	public void setBaseProduct(String baseProduct) {
		this.baseProduct = baseProduct;
	}

	public String getCustSegmentation() {
		return custSegmentation;
	}

	public void setCustSegmentation(String custSegmentation) {
		this.custSegmentation = custSegmentation;
	}

	public String getExistingLanRefNo() {
		return existingLanRefNo;
	}

	public void setExistingLanRefNo(String existingLanRefNo) {
		this.existingLanRefNo = existingLanRefNo;
	}

	public Boolean getRsa() {
		return rsa;
	}

	public void setRsa(Boolean rsa) {
		this.rsa = rsa;
	}

	public String getFinRepayMethod() {
		return finRepayMethod;
	}

	public void setFinRepayMethod(String finRepayMethod) {
		this.finRepayMethod = finRepayMethod;
	}

	public String getVerification() {
		return verification;
	}

	public void setVerification(String verification) {
		this.verification = verification;
	}

	public Date getFinContractDate() {
		return finContractDate;
	}

	public void setFinContractDate(Date finContractDate) {
		this.finContractDate = finContractDate;
	}

	public String getLeadSource() {
		return leadSource;
	}

	public void setLeadSource(String leadSource) {
		this.leadSource = leadSource;
	}

	public String getPoSource() {
		return poSource;
	}

	public void setPoSource(String poSource) {
		this.poSource = poSource;
	}

	public String getSourcingBranch() {
		return sourcingBranch;
	}

	public void setSourcingBranch(String sourcingBranch) {
		this.sourcingBranch = sourcingBranch;
	}

	public String getSourChannelCategory() {
		return sourChannelCategory;
	}

	public void setSourChannelCategory(String sourChannelCategory) {
		this.sourChannelCategory = sourChannelCategory;
	}

	public String getApplicationNo() {
		return applicationNo;
	}

	public void setApplicationNo(String applicationNo) {
		this.applicationNo = applicationNo;
	}

	public BigDecimal getReqRepayAmount() {
		return reqRepayAmount;
	}

	public void setReqRepayAmount(BigDecimal reqRepayAmount) {
		this.reqRepayAmount = reqRepayAmount;
	}

	public String getRepayRateBasis() {
		return repayRateBasis;
	}

	public void setRepayRateBasis(String repayRateBasis) {
		this.repayRateBasis = repayRateBasis;
	}

	public BigDecimal getRepayProfitRate() {
		return repayProfitRate;
	}

	public void setRepayProfitRate(BigDecimal repayProfitRate) {
		this.repayProfitRate = repayProfitRate;
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

	public BigDecimal getRepayMargin() {
		return repayMargin;
	}

	public void setRepayMargin(BigDecimal repayMargin) {
		this.repayMargin = repayMargin;
	}

	public String getScheduleMethod() {
		return scheduleMethod;
	}

	public void setScheduleMethod(String scheduleMethod) {
		this.scheduleMethod = scheduleMethod;
	}

	public String getRepayPftFrq() {
		return repayPftFrq;
	}

	public void setRepayPftFrq(String repayPftFrq) {
		this.repayPftFrq = repayPftFrq;
	}

	public Date getNextRepayPftDate() {
		return nextRepayPftDate;
	}

	public void setNextRepayPftDate(Date nextRepayPftDate) {
		this.nextRepayPftDate = nextRepayPftDate;
	}

	public String getRepayRvwFrq() {
		return repayRvwFrq;
	}

	public void setRepayRvwFrq(String repayRvwFrq) {
		this.repayRvwFrq = repayRvwFrq;
	}

	public Date getNextRepayRvwDate() {
		return nextRepayRvwDate;
	}

	public void setNextRepayRvwDate(Date nextRepayRvwDate) {
		this.nextRepayRvwDate = nextRepayRvwDate;
	}

	public String getRepayCpzFrq() {
		return repayCpzFrq;
	}

	public void setRepayCpzFrq(String repayCpzFrq) {
		this.repayCpzFrq = repayCpzFrq;
	}

	public Date getNextRepayCpzDate() {
		return nextRepayCpzDate;
	}

	public void setNextRepayCpzDate(Date nextRepayCpzDate) {
		this.nextRepayCpzDate = nextRepayCpzDate;
	}

	public String getRepayFrq() {
		return repayFrq;
	}

	public void setRepayFrq(String repayFrq) {
		this.repayFrq = repayFrq;
	}

	public Date getNextRepayDate() {
		return nextRepayDate;
	}

	public void setNextRepayDate(Date nextRepayDate) {
		this.nextRepayDate = nextRepayDate;
	}

	public Date getMaturityDate() {
		return maturityDate;
	}

	public void setMaturityDate(Date maturityDate) {
		this.maturityDate = maturityDate;
	}

	public Boolean getFinRepayPftOnFrq() {
		return finRepayPftOnFrq;
	}

	public void setFinRepayPftOnFrq(Boolean finRepayPftOnFrq) {
		this.finRepayPftOnFrq = finRepayPftOnFrq;
	}

	public BigDecimal getRpyMinRate() {
		return rpyMinRate;
	}

	public void setRpyMinRate(BigDecimal rpyMinRate) {
		this.rpyMinRate = rpyMinRate;
	}

	public BigDecimal getRpyMaxRate() {
		return rpyMaxRate;
	}

	public void setRpyMaxRate(BigDecimal rpyMaxRate) {
		this.rpyMaxRate = rpyMaxRate;
	}

	public Boolean isAlwBPI() {
		return alwBPI;
	}

	public void setAlwBPI(Boolean alwBPI) {
		this.alwBPI = alwBPI;
	}

	public String getBpiTreatment() {
		return bpiTreatment;
	}

	public void setBpiTreatment(String bpiTreatment) {
		this.bpiTreatment = bpiTreatment;
	}

	public String getBpiPftDaysBasis() {
		return bpiPftDaysBasis;
	}

	public void setBpiPftDaysBasis(String bpiPftDaysBasis) {
		this.bpiPftDaysBasis = bpiPftDaysBasis;
	}

	public BigDecimal getReqLoanAmt() {
		return reqLoanAmt;
	}

	public void setReqLoanAmt(BigDecimal reqLoanAmt) {
		this.reqLoanAmt = reqLoanAmt;
	}

	public Integer getReqLoanTenor() {
		return reqLoanTenor;
	}

	public void setReqLoanTenor(Integer reqLoanTenor) {
		this.reqLoanTenor = reqLoanTenor;
	}

	public Date getCalMaturity() {
		return calMaturity;
	}

	public void setCalMaturity(Date calMaturity) {
		this.calMaturity = calMaturity;
	}

	public BigDecimal getFinAssetValue() {
		return finAssetValue;
	}

	public void setFinAssetValue(BigDecimal finAssetValue) {
		this.finAssetValue = finAssetValue;
	}

	public BigDecimal getFinCurrAssetValue() {
		return finCurrAssetValue;
	}

	public void setFinCurrAssetValue(BigDecimal finCurrAssetValue) {
		this.finCurrAssetValue = finCurrAssetValue;
	}

	public String getProductCategory() {
		return productCategory;
	}

	public void setProductCategory(String productCategory) {
		this.productCategory = productCategory;
	}

	public String getDmaCodeReference() {
		return dmaCodeReference;
	}

	public void setDmaCodeReference(String dmaCodeReference) {
		this.dmaCodeReference = dmaCodeReference;
	}

	public String getReferralId() {
		return referralId;
	}

	public void setReferralId(String referralId) {
		this.referralId = referralId;
	}

	public String getEmployeeName() {
		return employeeName;
	}

	public void setEmployeeName(String employeeName) {
		this.employeeName = employeeName;
	}

	public Boolean getQuickDisb() {
		return quickDisb;
	}

	public void setQuickDisb(Boolean quickDisb) {
		this.quickDisb = quickDisb;
	}

	public Date getFirstDisbDate() {
		return firstDisbDate;
	}

	public void setFirstDisbDate(Date firstDisbDate) {
		this.firstDisbDate = firstDisbDate;
	}

	public Date getLastDisbDate() {
		return lastDisbDate;
	}

	public void setLastDisbDate(Date lastDisbDate) {
		this.lastDisbDate = lastDisbDate;
	}

	public String getStage() {
		return stage;
	}

	public void setStage(String stage) {
		this.stage = stage;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getConnectorReference() {
		return connectorReference;
	}

	public void setConnectorReference(String connectorReference) {
		this.connectorReference = connectorReference;
	}

	public Integer getFixedRateTenor() {
		return fixedRateTenor;
	}

	public void setFixedRateTenor(Integer fixedRateTenor) {
		this.fixedRateTenor = fixedRateTenor;
	}

	public BigDecimal getFixedTenorRate() {
		return fixedTenorRate;
	}

	public void setFixedTenorRate(BigDecimal fixedTenorRate) {
		this.fixedTenorRate = fixedTenorRate;
	}

	public BigDecimal getRepayAmount() {
		return repayAmount;
	}

	public void setRepayAmount(BigDecimal repayAmount) {
		this.repayAmount = repayAmount;
	}

	public String getAdvType() {
		return advType;
	}

	public void setAdvType(String advType) {
		this.advType = advType;
	}

	public Integer getAdvTerms() {
		return advTerms;
	}

	public void setAdvTerms(Integer advTerms) {
		this.advTerms = advTerms;
	}

	public Date getClosedDate() {
		return closedDate;
	}

	public void setClosedDate(Date closedDate) {
		this.closedDate = closedDate;
	}

	public Long getManufacturerDealerId() {
		return manufacturerDealerId;
	}

	public void setManufacturerDealerId(Long manufacturerDealerId) {
		this.manufacturerDealerId = manufacturerDealerId;
	}

	public Boolean getFinOcrRequired() {
		return finOcrRequired;
	}

	public void setFinOcrRequired(Boolean finOcrRequired) {
		this.finOcrRequired = finOcrRequired;
	}

	public String getTdsType() {
		return tdsType;
	}

	public void setTdsType(String tdsType) {
		this.tdsType = tdsType;
	}

	public Boolean getEscrow() {
		return escrow;
	}

	public void setEscrow(Boolean escrow) {
		this.escrow = escrow;
	}

	public Long getCustBankId() {
		return custBankId;
	}

	public void setCustBankId(Long custBankId) {
		this.custBankId = custBankId;
	}

	public Boolean isOverdraftTxnChrgReq() {
		return overdraftTxnChrgReq;
	}

	public void setOverdraftTxnChrgReq(Boolean overdraftTxnChrgReq) {
		this.overdraftTxnChrgReq = overdraftTxnChrgReq;
	}

	public String getOverdraftCalcChrg() {
		return overdraftCalcChrg;
	}

	public void setOverdraftCalcChrg(String overdraftCalcChrg) {
		this.overdraftCalcChrg = overdraftCalcChrg;
	}

	public String getOverdraftChrCalOn() {
		return overdraftChrCalOn;
	}

	public void setOverdraftChrCalOn(String overdraftChrCalOn) {
		this.overdraftChrCalOn = overdraftChrCalOn;
	}

	public BigDecimal getOverdraftChrgAmtOrPerc() {
		return overdraftChrgAmtOrPerc;
	}

	public void setOverdraftChrgAmtOrPerc(BigDecimal overdraftChrgAmtOrPerc) {
		this.overdraftChrgAmtOrPerc = overdraftChrgAmtOrPerc;
	}

	public Long getOverdraftTxnChrgFeeType() {
		return overdraftTxnChrgFeeType;
	}

	public void setOverdraftTxnChrgFeeType(Long overdraftTxnChrgFeeType) {
		this.overdraftTxnChrgFeeType = overdraftTxnChrgFeeType;
	}

	public LoanSummary getLoanSummary() {
		return loanSummary;
	}

	public void setLoanSummary(LoanSummary loanSummary) {
		this.loanSummary = loanSummary;
	}

	public List<LoanSchedules> getLoanSchedules() {
		return loanSchedules;
	}

	public void setLoanSchedules(List<LoanSchedules> loanSchedules) {
		this.loanSchedules = loanSchedules;
	}

	public String getAccNumber() {
		return accNumber;
	}

	public void setAccNumber(String accNumber) {
		this.accNumber = accNumber;
	}

	public LoanBranch getLoanBranch() {
		return loanBranch;
	}

	public void setLoanBranch(LoanBranch loanBranch) {
		this.loanBranch = loanBranch;
	}

	public GraceDetails getGraceDetails() {
		return graceDetails;
	}

	public void setGraceDetails(GraceDetails graceDetails) {
		this.graceDetails = graceDetails;
	}

	public EMIHolidays getEmiHolidays() {
		return emiHolidays;
	}

	public void setEmiHolidays(EMIHolidays emiHolidays) {
		this.emiHolidays = emiHolidays;
	}

	public WSReturnStatus getReturnStatus() {
		return returnStatus;
	}

	public void setReturnStatus(WSReturnStatus returnStatus) {
		this.returnStatus = returnStatus;
	}

	public Integer getNoOfMonths() {
		return noOfMonths;
	}

	public void setNoOfMonths(Integer noOfMonths) {
		this.noOfMonths = noOfMonths;
	}

	public String getLoanName() {
		return loanName;
	}

	public void setLoanName(String loanName) {
		this.loanName = loanName;
	}

	public BigDecimal getNetDisbursementAmount() {
		return netDisbursementAmount;
	}

	public void setNetDisbursementAmount(BigDecimal netDisbursementAmount) {
		this.netDisbursementAmount = netDisbursementAmount;
	}

	public Date getCustDOB() {
		return custDOB;
	}

	public void setCustDOB(Date custDOB) {
		this.custDOB = custDOB;
	}

	public Date getFromDate() {
		return fromDate;
	}

	public void setFromDate(Date fromDate) {
		this.fromDate = fromDate;
	}

	public Date getToDate() {
		return toDate;
	}

	public void setToDate(Date toDate) {
		this.toDate = toDate;
	}
}
