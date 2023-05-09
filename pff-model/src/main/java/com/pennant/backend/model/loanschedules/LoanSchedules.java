package com.pennant.backend.model.loanschedules;

import java.math.BigDecimal;
import java.util.Date;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "schedule")
@XmlAccessorType(XmlAccessType.NONE)
public class LoanSchedules {

	private long finID;
	private String finReference = null;
	@XmlElement
	private Date schDate;
	@XmlElement(name = "loanEMINumber")
	private int instNumber = 0;
	@XmlElement(name = "loanIntDays")
	private int noOfDays;
	@XmlElement(name = "pftAmount")
	private BigDecimal profitCalc = BigDecimal.ZERO;
	@XmlElement(name = "schdPft")
	private BigDecimal profitSchd = BigDecimal.ZERO;
	@XmlElement(name = "schdPri")
	private BigDecimal principalSchd = BigDecimal.ZERO;
	@XmlElement(name = "totalAmount")
	private BigDecimal repayAmount = BigDecimal.ZERO;
	@XmlElement(name = "endBal")
	private BigDecimal closingBalance = BigDecimal.ZERO;
	@XmlElement
	private BigDecimal schdPftPaid = BigDecimal.ZERO;
	@XmlElement
	private BigDecimal schdPriPaid = BigDecimal.ZERO;
	@XmlElement
	private BigDecimal feeSchd = BigDecimal.ZERO;
	@XmlElement
	private BigDecimal tDSAmount = BigDecimal.ZERO;
	@XmlElement(name = "limitDrop")
	private BigDecimal limitDrop = BigDecimal.ZERO;
	@XmlElement(name = "dropLineLimit")
	private BigDecimal oDLimit = BigDecimal.ZERO;
	@XmlElement(name = "availableLimit")
	private BigDecimal availableLimit = BigDecimal.ZERO;
	@XmlElement
	private String loanEMIStatus;
	@XmlElement
	private Long loanExcessInt = (long) 0;
	@XmlElement
	private Integer loanExcessIntDays = (int) 0;
	@XmlElement
	private BigDecimal actRate;
	@XmlElement
	private BigDecimal openBal;

	public LoanSchedules() {
		super();
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

	public Date getSchDate() {
		return schDate;
	}

	public void setSchDate(Date schDate) {
		this.schDate = schDate;
	}

	public int getInstNumber() {
		return instNumber;
	}

	public void setInstNumber(int instNumber) {
		this.instNumber = instNumber;
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

	public BigDecimal getRepayAmount() {
		return repayAmount;
	}

	public void setRepayAmount(BigDecimal repayAmount) {
		this.repayAmount = repayAmount;
	}

	public BigDecimal getClosingBalance() {
		return closingBalance;
	}

	public void setClosingBalance(BigDecimal closingBalance) {
		this.closingBalance = closingBalance;
	}

	public BigDecimal getSchdPftPaid() {
		return schdPftPaid;
	}

	public void setSchdPftPaid(BigDecimal schdPftPaid) {
		this.schdPftPaid = schdPftPaid;
	}

	public BigDecimal getSchdPriPaid() {
		return schdPriPaid;
	}

	public void setSchdPriPaid(BigDecimal schdPriPaid) {
		this.schdPriPaid = schdPriPaid;
	}

	public BigDecimal getFeeSchd() {
		return feeSchd;
	}

	public void setFeeSchd(BigDecimal feeSchd) {
		this.feeSchd = feeSchd;
	}

	public BigDecimal gettDSAmount() {
		return tDSAmount;
	}

	public void settDSAmount(BigDecimal tDSAmount) {
		this.tDSAmount = tDSAmount;
	}

	public BigDecimal getLimitDrop() {
		return limitDrop;
	}

	public void setLimitDrop(BigDecimal limitDrop) {
		this.limitDrop = limitDrop;
	}

	public BigDecimal getoDLimit() {
		return oDLimit;
	}

	public void setoDLimit(BigDecimal oDLimit) {
		this.oDLimit = oDLimit;
	}

	public BigDecimal getAvailableLimit() {
		return availableLimit;
	}

	public void setAvailableLimit(BigDecimal availableLimit) {
		this.availableLimit = availableLimit;
	}

	public String getLoanEMIStatus() {
		return loanEMIStatus;
	}

	public void setLoanEMIStatus(String loanEMIStatus) {
		this.loanEMIStatus = loanEMIStatus;
	}

	public Long getLoanExcessInt() {
		return loanExcessInt;
	}

	public void setLoanExcessInt(Long loanExcessInt) {
		this.loanExcessInt = loanExcessInt;
	}

	public Integer getLoanExcessIntDays() {
		return loanExcessIntDays;
	}

	public void setLoanExcessIntDays(Integer loanExcessIntDays) {
		this.loanExcessIntDays = loanExcessIntDays;
	}

	public BigDecimal getActRate() {
		return actRate;
	}

	public void setActRate(BigDecimal actRate) {
		this.actRate = actRate;
	}

	public BigDecimal getOpenBal() {
		return openBal;
	}

	public void setOpenBal(BigDecimal openBal) {
		this.openBal = openBal;
	}
}
