package com.pennant.backend.model.finance;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

public class ProjectedAccrual implements Serializable {
	private static final long serialVersionUID = 7690656031696834080L;

	private String finReference;
	private String finType;
	private Date accruedOn;
	private Date schdDate;
	private BigDecimal schdPri = BigDecimal.ZERO;
	private BigDecimal schdPft = BigDecimal.ZERO;
	private BigDecimal schdTot = BigDecimal.ZERO;
	private BigDecimal pftAmz = BigDecimal.ZERO;
	private BigDecimal pftAccrued = BigDecimal.ZERO;
	private BigDecimal cumulativeAccrued = BigDecimal.ZERO;

	private int schSeq;
	private boolean pftOnSchDate;
	private boolean cpzOnSchDate;
	private boolean repayOnSchDate;
	private boolean rvwOnSchDate;
	private BigDecimal balanceForPftCal = BigDecimal.ZERO;
	private BigDecimal calculatedRate = BigDecimal.ZERO;
	private int noOfDays;
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
	public ProjectedAccrual() {

	}

	// ******************************************************//
	// ****************** getter / setter *******************//
	// ******************************************************//

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

}
