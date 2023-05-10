package com.pennant.backend.model.finance;

import java.math.BigDecimal;
import java.util.Date;

public class AlmExtract {
	private String almReportType;
	private Date almReportDate;
	private String accountNumber;
	private String accrualBasis;
	private BigDecimal accruedInterest;
	private String BankNumber;
	private String branch;
	private long compFreq;
	private long compFreqIncr;
	private String currencyCode;
	private BigDecimal currentBalance;
	private Date dueDate;
	private long initPaymentFreq;
	private BigDecimal initRate;
	private BigDecimal lifeCeiling;
	private BigDecimal lifeFloor;
	private String loanType;
	private Date maturity;
	private BigDecimal originalBalance;
	private int originalTerm;
	private Date originationDate;
	private BigDecimal instalment;
	private String paymentFreq;
	private long paymentType;
	private long pctOwned;
	private String rateFlag;
	private String rePriceIndex;
	private long dpd;
	private BigDecimal totalInterest;
	private BigDecimal billedInterest;
	private BigDecimal billedNotReceivedInterest;
	private BigDecimal billedNotReceivedPrincipal;
	private String customerName;
	private BigDecimal pretaxirr;
	private long schemeId;
	private String professionCode;
	private long brokerId;
	private long pslctgid;
	private String npaStageId;
	private String weakerSectionDesc;

	public String getAlmReportType() {
		return almReportType;
	}

	public void setAlmReportType(String almReportType) {
		this.almReportType = almReportType;
	}

	public Date getAlmReportDate() {
		return almReportDate;
	}

	public void setAlmReportDate(Date almReportDate) {
		this.almReportDate = almReportDate;
	}

	public String getAccountNumber() {
		return accountNumber;
	}

	public void setAccountNumber(String accountNumber) {
		this.accountNumber = accountNumber;
	}

	public String getAccrualBasis() {
		return accrualBasis;
	}

	public void setAccrualBasis(String accrualBasis) {
		this.accrualBasis = accrualBasis;
	}

	public BigDecimal getAccruedInterest() {
		return accruedInterest;
	}

	public void setAccruedInterest(BigDecimal accruedInterest) {
		this.accruedInterest = accruedInterest;
	}

	public String getBankNumber() {
		return BankNumber;
	}

	public void setBankNumber(String bankNumber) {
		BankNumber = bankNumber;
	}

	public String getBranch() {
		return branch;
	}

	public void setBranch(String branch) {
		this.branch = branch;
	}

	public long getCompFreq() {
		return compFreq;
	}

	public void setCompFreq(long compFreq) {
		this.compFreq = compFreq;
	}

	public long getCompFreqIncr() {
		return compFreqIncr;
	}

	public void setCompFreqIncr(long compFreqIncr) {
		this.compFreqIncr = compFreqIncr;
	}

	public String getCurrencyCode() {
		return currencyCode;
	}

	public void setCurrencyCode(String currencyCode) {
		this.currencyCode = currencyCode;
	}

	public BigDecimal getCurrentBalance() {
		return currentBalance;
	}

	public void setCurrentBalance(BigDecimal currentBalance) {
		this.currentBalance = currentBalance;
	}

	public Date getDueDate() {
		return dueDate;
	}

	public void setDueDate(Date dueDate) {
		this.dueDate = dueDate;
	}

	public long getInitPaymentFreq() {
		return initPaymentFreq;
	}

	public void setInitPaymentFreq(long initPaymentFreq) {
		this.initPaymentFreq = initPaymentFreq;
	}

	public BigDecimal getInitRate() {
		return initRate;
	}

	public void setInitRate(BigDecimal initRate) {
		this.initRate = initRate;
	}

	public BigDecimal getLifeCeiling() {
		return lifeCeiling;
	}

	public void setLifeCeiling(BigDecimal lifeCeiling) {
		this.lifeCeiling = lifeCeiling;
	}

	public BigDecimal getLifeFloor() {
		return lifeFloor;
	}

	public void setLifeFloor(BigDecimal lifeFloor) {
		this.lifeFloor = lifeFloor;
	}

	public String getLoanType() {
		return loanType;
	}

	public void setLoanType(String loanType) {
		this.loanType = loanType;
	}

	public Date getMaturity() {
		return maturity;
	}

	public void setMaturity(Date maturity) {
		this.maturity = maturity;
	}

	public BigDecimal getOriginalBalance() {
		return originalBalance;
	}

	public void setOriginalBalance(BigDecimal originalBalance) {
		this.originalBalance = originalBalance;
	}

	public int getOriginalTerm() {
		return originalTerm;
	}

	public void setOriginalTerm(int originalTerm) {
		this.originalTerm = originalTerm;
	}

	public Date getOriginationDate() {
		return originationDate;
	}

	public void setOriginationDate(Date originationDate) {
		this.originationDate = originationDate;
	}

	public BigDecimal getInstalment() {
		return instalment;
	}

	public void setInstalment(BigDecimal instalment) {
		this.instalment = instalment;
	}

	public String getPaymentFreq() {
		return paymentFreq;
	}

	public void setPaymentFreq(String paymentFreq) {
		this.paymentFreq = paymentFreq;
	}

	public long getPaymentType() {
		return paymentType;
	}

	public void setPaymentType(long paymentType) {
		this.paymentType = paymentType;
	}

	public long getPctOwned() {
		return pctOwned;
	}

	public void setPctOwned(long pctOwned) {
		this.pctOwned = pctOwned;
	}

	public String getRateFlag() {
		return rateFlag;
	}

	public void setRateFlag(String rateFlag) {
		this.rateFlag = rateFlag;
	}

	public String getRePriceIndex() {
		return rePriceIndex;
	}

	public void setRePriceIndex(String rePriceIndex) {
		this.rePriceIndex = rePriceIndex;
	}

	public long getDpd() {
		return dpd;
	}

	public void setDpd(long dpd) {
		this.dpd = dpd;
	}

	public BigDecimal getTotalInterest() {
		return totalInterest;
	}

	public void setTotalInterest(BigDecimal totalInterest) {
		this.totalInterest = totalInterest;
	}

	public BigDecimal getBilledInterest() {
		return billedInterest;
	}

	public void setBilledInterest(BigDecimal billedInterest) {
		this.billedInterest = billedInterest;
	}

	public BigDecimal getBilledNotReceivedInterest() {
		return billedNotReceivedInterest;
	}

	public void setBilledNotReceivedInterest(BigDecimal billedNotReceivedInterest) {
		this.billedNotReceivedInterest = billedNotReceivedInterest;
	}

	public BigDecimal getBilledNotReceivedPrincipal() {
		return billedNotReceivedPrincipal;
	}

	public void setBilledNotReceivedPrincipal(BigDecimal billedNotReceivedPrincipal) {
		this.billedNotReceivedPrincipal = billedNotReceivedPrincipal;
	}

	public String getCustomerName() {
		return customerName;
	}

	public void setCustomerName(String customerName) {
		this.customerName = customerName;
	}

	public BigDecimal getPretaxirr() {
		return pretaxirr;
	}

	public void setPretaxirr(BigDecimal pretaxirr) {
		this.pretaxirr = pretaxirr;
	}

	public long getSchemeId() {
		return schemeId;
	}

	public void setSchemeId(long schemeId) {
		this.schemeId = schemeId;
	}

	public String getProfessionCode() {
		return professionCode;
	}

	public void setProfessionCode(String professionCode) {
		this.professionCode = professionCode;
	}

	public long getBrokerId() {
		return brokerId;
	}

	public void setBrokerId(long brokerId) {
		this.brokerId = brokerId;
	}

	public long getPslctgid() {
		return pslctgid;
	}

	public void setPslctgid(long pslctgid) {
		this.pslctgid = pslctgid;
	}

	public String getNpaStageId() {
		return npaStageId;
	}

	public void setNpaStageId(String npaStageId) {
		this.npaStageId = npaStageId;
	}

	public String getWeakerSectionDesc() {
		return weakerSectionDesc;
	}

	public void setWeakerSectionDesc(String weakerSectionDesc) {
		this.weakerSectionDesc = weakerSectionDesc;
	}

}
