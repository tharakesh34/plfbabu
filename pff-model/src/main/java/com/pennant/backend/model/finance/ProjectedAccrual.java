package com.pennant.backend.model.finance;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

public class ProjectedAccrual implements Serializable {
	private static final long serialVersionUID = 7690656031696834080L;

	private long finID;
	private String finReference;
	private String finType;
	private Date accruedOn;
	private Date schdDate;
	private BigDecimal schdPri = BigDecimal.ZERO;
	private BigDecimal schdPft = BigDecimal.ZERO;
	private BigDecimal schdTot = BigDecimal.ZERO;

	private int schSeq;
	private boolean pftOnSchDate;
	private boolean cpzOnSchDate;
	private boolean repayOnSchDate;
	private boolean rvwOnSchDate;
	private BigDecimal balanceForPftCal = BigDecimal.ZERO;
	private BigDecimal calculatedRate = BigDecimal.ZERO;
	private BigDecimal profitCalc = BigDecimal.ZERO;
	private BigDecimal profitSchd = BigDecimal.ZERO;
	private BigDecimal principalSchd = BigDecimal.ZERO;
	private BigDecimal disbAmount = BigDecimal.ZERO;
	private BigDecimal downPaymentAmount = BigDecimal.ZERO;
	private BigDecimal cpzAmount = BigDecimal.ZERO;
	private BigDecimal feeChargeAmt = BigDecimal.ZERO;
	private BigDecimal schdPriPaid = BigDecimal.ZERO;
	private BigDecimal schdPftPaid = BigDecimal.ZERO;
	private boolean schPftPaid;
	private boolean schPriPaid;
	private String specifier;
	private String closingStatus;
	private int emiAdv;
	private String advFlag;
	private int ccyEditField;
	private BigDecimal ccyMinorCcyUnits;
	private Date finStartDate;
	private Date maturityDate;

	private long projAccrualID = Long.MIN_VALUE;
	private BigDecimal pftAmz = BigDecimal.ZERO;
	private BigDecimal pftAccrued = BigDecimal.ZERO;
	private BigDecimal cumulativeAccrued = BigDecimal.ZERO;
	private BigDecimal pOSAccrued = BigDecimal.ZERO;
	private BigDecimal cumulativePOS = BigDecimal.ZERO;
	private int noOfDays = 0;
	private int cumulativeDays = 0;
	private BigDecimal aMZPercentage = BigDecimal.ZERO;

	// Partial Settlements
	private long partialPaymentID = Long.MIN_VALUE;
	private BigDecimal partialPaidAmt = BigDecimal.ZERO;
	private BigDecimal partialAMZPerc = BigDecimal.ZERO;
	private boolean monthEnd;

	// Customer Profitability RFT
	private BigDecimal avgPOS = BigDecimal.ZERO;

	public ProjectedAccrual() {
	    super();
	}

	public ProjectedAccrual copyEntity() {
		ProjectedAccrual entity = new ProjectedAccrual();
		entity.setFinID(this.finID);
		entity.setFinReference(this.finReference);
		entity.setFinType(this.finType);
		entity.setAccruedOn(this.accruedOn);
		entity.setSchdDate(this.schdDate);
		entity.setSchdPri(this.schdPri);
		entity.setSchdPft(this.schdPft);
		entity.setSchdTot(this.schdTot);
		entity.setSchSeq(this.schSeq);
		entity.setPftOnSchDate(this.pftOnSchDate);
		entity.setCpzOnSchDate(this.cpzOnSchDate);
		entity.setRepayOnSchDate(this.repayOnSchDate);
		entity.setRvwOnSchDate(this.rvwOnSchDate);
		entity.setBalanceForPftCal(this.balanceForPftCal);
		entity.setCalculatedRate(this.calculatedRate);
		entity.setProfitCalc(this.profitCalc);
		entity.setProfitSchd(this.profitSchd);
		entity.setPrincipalSchd(this.principalSchd);
		entity.setDisbAmount(this.disbAmount);
		entity.setDownPaymentAmount(this.downPaymentAmount);
		entity.setCpzAmount(this.cpzAmount);
		entity.setFeeChargeAmt(this.feeChargeAmt);
		entity.setSchdPriPaid(this.schdPriPaid);
		entity.setSchdPftPaid(this.schdPftPaid);
		entity.setSchPftPaid(this.schPftPaid);
		entity.setSchPriPaid(this.schPriPaid);
		entity.setSpecifier(this.specifier);
		entity.setClosingStatus(this.closingStatus);
		entity.setEmiAdv(this.emiAdv);
		entity.setAdvFlag(this.advFlag);
		entity.setCcyEditField(this.ccyEditField);
		entity.setCcyMinorCcyUnits(this.ccyMinorCcyUnits);
		entity.setFinStartDate(this.finStartDate);
		entity.setMaturityDate(this.maturityDate);
		entity.setProjAccrualID(this.projAccrualID);
		entity.setPftAmz(this.pftAmz);
		entity.setPftAccrued(this.pftAccrued);
		entity.setCumulativeAccrued(this.cumulativeAccrued);
		entity.setPOSAccrued(this.pOSAccrued);
		entity.setCumulativePOS(this.cumulativePOS);
		entity.setNoOfDays(this.noOfDays);
		entity.setCumulativeDays(this.cumulativeDays);
		entity.setAMZPercentage(this.aMZPercentage);
		entity.setPartialPaymentID(this.partialPaymentID);
		entity.setPartialPaidAmt(this.partialPaidAmt);
		entity.setPartialAMZPerc(this.partialAMZPerc);
		entity.setMonthEnd(this.monthEnd);
		entity.setAvgPOS(this.avgPOS);
		return entity;
	}

	public long getFinID() {
		return finID;
	}

	public void setFinID(long finID) {
		this.finID = finID;
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

	public Date getAccruedOn() {
		return accruedOn;
	}

	public void setAccruedOn(Date accruedOn) {
		this.accruedOn = accruedOn;
	}

	public Date getSchdDate() {
		return schdDate;
	}

	public void setSchdDate(Date schdDate) {
		this.schdDate = schdDate;
	}

	public BigDecimal getSchdPri() {
		return schdPri;
	}

	public void setSchdPri(BigDecimal schdPri) {
		this.schdPri = schdPri;
	}

	public BigDecimal getSchdPft() {
		return schdPft;
	}

	public void setSchdPft(BigDecimal schdPft) {
		this.schdPft = schdPft;
	}

	public BigDecimal getSchdTot() {
		return schdTot;
	}

	public void setSchdTot(BigDecimal schdTot) {
		this.schdTot = schdTot;
	}

	public BigDecimal getPftAmz() {
		return pftAmz;
	}

	public void setPftAmz(BigDecimal pftAmz) {
		this.pftAmz = pftAmz;
	}

	public BigDecimal getPftAccrued() {
		return pftAccrued;
	}

	public void setPftAccrued(BigDecimal pftAccrued) {
		this.pftAccrued = pftAccrued;
	}

	public BigDecimal getCumulativeAccrued() {
		return cumulativeAccrued;
	}

	public void setCumulativeAccrued(BigDecimal cumulativeAccrued) {
		this.cumulativeAccrued = cumulativeAccrued;
	}

	public int getSchSeq() {
		return schSeq;
	}

	public void setSchSeq(int schSeq) {
		this.schSeq = schSeq;
	}

	public boolean isPftOnSchDate() {
		return pftOnSchDate;
	}

	public void setPftOnSchDate(boolean pftOnSchDate) {
		this.pftOnSchDate = pftOnSchDate;
	}

	public boolean isCpzOnSchDate() {
		return cpzOnSchDate;
	}

	public void setCpzOnSchDate(boolean cpzOnSchDate) {
		this.cpzOnSchDate = cpzOnSchDate;
	}

	public boolean isRepayOnSchDate() {
		return repayOnSchDate;
	}

	public void setRepayOnSchDate(boolean repayOnSchDate) {
		this.repayOnSchDate = repayOnSchDate;
	}

	public boolean isRvwOnSchDate() {
		return rvwOnSchDate;
	}

	public void setRvwOnSchDate(boolean rvwOnSchDate) {
		this.rvwOnSchDate = rvwOnSchDate;
	}

	public BigDecimal getBalanceForPftCal() {
		return balanceForPftCal;
	}

	public void setBalanceForPftCal(BigDecimal balanceForPftCal) {
		this.balanceForPftCal = balanceForPftCal;
	}

	public BigDecimal getCalculatedRate() {
		return calculatedRate;
	}

	public void setCalculatedRate(BigDecimal calculatedRate) {
		this.calculatedRate = calculatedRate;
	}

	public int getNoOfDays() {
		return noOfDays;
	}

	public void setNoOfDays(int noOfDays) {
		this.noOfDays = noOfDays;
	}

	public BigDecimal getProfitCalc() {
		return profitCalc;
	}

	public void setProfitCalc(BigDecimal profitCalc) {
		this.profitCalc = profitCalc;
	}

	public BigDecimal getProfitSchd() {
		return profitSchd;
	}

	public void setProfitSchd(BigDecimal profitSchd) {
		this.profitSchd = profitSchd;
	}

	public BigDecimal getPrincipalSchd() {
		return principalSchd;
	}

	public void setPrincipalSchd(BigDecimal principalSchd) {
		this.principalSchd = principalSchd;
	}

	public BigDecimal getDisbAmount() {
		return disbAmount;
	}

	public void setDisbAmount(BigDecimal disbAmount) {
		this.disbAmount = disbAmount;
	}

	public BigDecimal getDownPaymentAmount() {
		return downPaymentAmount;
	}

	public void setDownPaymentAmount(BigDecimal downPaymentAmount) {
		this.downPaymentAmount = downPaymentAmount;
	}

	public BigDecimal getCpzAmount() {
		return cpzAmount;
	}

	public void setCpzAmount(BigDecimal cpzAmount) {
		this.cpzAmount = cpzAmount;
	}

	public BigDecimal getFeeChargeAmt() {
		return feeChargeAmt;
	}

	public void setFeeChargeAmt(BigDecimal feeChargeAmt) {
		this.feeChargeAmt = feeChargeAmt;
	}

	public BigDecimal getSchdPriPaid() {
		return schdPriPaid;
	}

	public void setSchdPriPaid(BigDecimal schdPriPaid) {
		this.schdPriPaid = schdPriPaid;
	}

	public boolean isSchPftPaid() {
		return schPftPaid;
	}

	public void setSchPftPaid(boolean schPftPaid) {
		this.schPftPaid = schPftPaid;
	}

	public boolean isSchPriPaid() {
		return schPriPaid;
	}

	public void setSchPriPaid(boolean schPriPaid) {
		this.schPriPaid = schPriPaid;
	}

	public String getSpecifier() {
		return specifier;
	}

	public void setSpecifier(String specifier) {
		this.specifier = specifier;
	}

	public String getClosingStatus() {
		return closingStatus;
	}

	public void setClosingStatus(String closingStatus) {
		this.closingStatus = closingStatus;
	}

	public int getEmiAdv() {
		return emiAdv;
	}

	public void setEmiAdv(int emiAdv) {
		this.emiAdv = emiAdv;
	}

	public String getAdvFlag() {
		return advFlag;
	}

	public void setAdvFlag(String advFlag) {
		this.advFlag = advFlag;
	}

	public int getCcyEditField() {
		return ccyEditField;
	}

	public void setCcyEditField(int ccyEditField) {
		this.ccyEditField = ccyEditField;
	}

	public BigDecimal getCcyMinorCcyUnits() {
		return ccyMinorCcyUnits;
	}

	public void setCcyMinorCcyUnits(BigDecimal ccyMinorCcyUnits) {
		this.ccyMinorCcyUnits = ccyMinorCcyUnits;
	}

	public Date getFinStartDate() {
		return finStartDate;
	}

	public void setFinStartDate(Date finStartDate) {
		this.finStartDate = finStartDate;
	}

	public Date getMaturityDate() {
		return maturityDate;
	}

	public void setMaturityDate(Date maturityDate) {
		this.maturityDate = maturityDate;
	}

	public BigDecimal getSchdPftPaid() {
		return schdPftPaid;
	}

	public void setSchdPftPaid(BigDecimal schdPftPaid) {
		this.schdPftPaid = schdPftPaid;
	}

	public int getCumulativeDays() {
		return cumulativeDays;
	}

	public void setCumulativeDays(int cumulativeDays) {
		this.cumulativeDays = cumulativeDays;
	}

	public BigDecimal getAMZPercentage() {
		return aMZPercentage;
	}

	public void setAMZPercentage(BigDecimal aMZPercentage) {
		this.aMZPercentage = aMZPercentage;
	}

	public long getProjAccrualID() {
		return projAccrualID;
	}

	public void setProjAccrualID(long projAccrualID) {
		this.projAccrualID = projAccrualID;
	}

	public BigDecimal getPOSAccrued() {
		return pOSAccrued;
	}

	public void setPOSAccrued(BigDecimal pOSAccrued) {
		this.pOSAccrued = pOSAccrued;
	}

	public BigDecimal getCumulativePOS() {
		return cumulativePOS;
	}

	public void setCumulativePOS(BigDecimal cumulativePOS) {
		this.cumulativePOS = cumulativePOS;
	}

	public BigDecimal getPartialPaidAmt() {
		return partialPaidAmt;
	}

	public void setPartialPaidAmt(BigDecimal partialPaidAmt) {
		this.partialPaidAmt = partialPaidAmt;
	}

	public long getPartialPaymentID() {
		return partialPaymentID;
	}

	public void setPartialPaymentID(long partialPaymentID) {
		this.partialPaymentID = partialPaymentID;
	}

	public BigDecimal getPartialAMZPerc() {
		return partialAMZPerc;
	}

	public void setPartialAMZPerc(BigDecimal partialAMZPerc) {
		this.partialAMZPerc = partialAMZPerc;
	}

	public boolean isMonthEnd() {
		return monthEnd;
	}

	public void setMonthEnd(boolean monthEnd) {
		this.monthEnd = monthEnd;
	}

	public BigDecimal getAvgPOS() {
		return avgPOS;
	}

	public void setAvgPOS(BigDecimal avgPOS) {
		this.avgPOS = avgPOS;
	}

}
