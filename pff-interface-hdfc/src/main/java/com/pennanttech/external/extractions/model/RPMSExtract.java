package com.pennanttech.external.extractions.model;

import java.math.BigDecimal;
import java.util.Date;

public class RPMSExtract {

	private long agreementId;
	private long customerId;
	private String status;
	private String multiLinkLoanFlag;
	private long parentLoanNo;
	private String customerSegment;
	private long groupId;
	private String groupDesc;
	private String groupCode;
	private String repoSettledFlag;
	private String closureReason;
	private Date closureDate;
	private BigDecimal posOnClosure;
	private String pdcFlag;
	private BigDecimal totPrinWaiveOff;
	private BigDecimal totIntWaiveOff;
	private BigDecimal woffChqBounceCharges;
	private BigDecimal woffOverDueCharge;
	private BigDecimal woffOthers;
	private String ramId;
	private String turnOverInYearOne;
	private BigDecimal turnOverAmtYearOne;
	private String turnOverInYearTwo;
	private BigDecimal turnOverAmtYearTwo;
	private String turnOverInYearThree;
	private BigDecimal turnOverAmtYearThree;

	public long getAgreementId() {
		return agreementId;
	}

	public void setAgreementId(long agreementId) {
		this.agreementId = agreementId;
	}

	public long getCustomerId() {
		return customerId;
	}

	public void setCustomerId(long customerId) {
		this.customerId = customerId;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getMultiLinkLoanFlag() {
		return multiLinkLoanFlag;
	}

	public void setMultiLinkLoanFlag(String multiLinkLoanFlag) {
		this.multiLinkLoanFlag = multiLinkLoanFlag;
	}

	public long getParentLoanNo() {
		return parentLoanNo;
	}

	public void setParentLoanNo(long parentLoanNo) {
		this.parentLoanNo = parentLoanNo;
	}

	public String getCustomerSegment() {
		return customerSegment;
	}

	public void setCustomerSegment(String customerSegment) {
		this.customerSegment = customerSegment;
	}

	public long getGroupId() {
		return groupId;
	}

	public void setGroupId(long groupId) {
		this.groupId = groupId;
	}

	public String getGroupDesc() {
		return groupDesc;
	}

	public void setGroupDesc(String groupDesc) {
		this.groupDesc = groupDesc;
	}

	public String getGroupCode() {
		return groupCode;
	}

	public void setGroupCode(String groupCode) {
		this.groupCode = groupCode;
	}

	public String getRepoSettledFlag() {
		return repoSettledFlag;
	}

	public void setRepoSettledFlag(String repoSettledFlag) {
		this.repoSettledFlag = repoSettledFlag;
	}

	public String getClosureReason() {
		return closureReason;
	}

	public void setClosureReason(String closureReason) {
		this.closureReason = closureReason;
	}

	public Date getClosureDate() {
		return closureDate;
	}

	public void setClosureDate(Date closureDate) {
		this.closureDate = closureDate;
	}

	public BigDecimal getPosOnClosure() {
		return posOnClosure;
	}

	public void setPosOnClosure(BigDecimal posOnClosure) {
		this.posOnClosure = posOnClosure;
	}

	public String getPdcFlag() {
		return pdcFlag;
	}

	public void setPdcFlag(String pdcFlag) {
		this.pdcFlag = pdcFlag;
	}

	public BigDecimal getTotPrinWaiveOff() {
		return totPrinWaiveOff;
	}

	public void setTotPrinWaiveOff(BigDecimal totPrinWaiveOff) {
		this.totPrinWaiveOff = totPrinWaiveOff;
	}

	public BigDecimal getTotIntWaiveOff() {
		return totIntWaiveOff;
	}

	public void setTotIntWaiveOff(BigDecimal totIntWaiveOff) {
		this.totIntWaiveOff = totIntWaiveOff;
	}

	public BigDecimal getWoffChqBounceCharges() {
		return woffChqBounceCharges;
	}

	public void setWoffChqBounceCharges(BigDecimal woffChqBounceCharges) {
		this.woffChqBounceCharges = woffChqBounceCharges;
	}

	public BigDecimal getWoffOverDueCharge() {
		return woffOverDueCharge;
	}

	public void setWoffOverDueCharge(BigDecimal woffOverDueCharge) {
		this.woffOverDueCharge = woffOverDueCharge;
	}

	public BigDecimal getWoffOthers() {
		return woffOthers;
	}

	public void setWoffOthers(BigDecimal woffOthers) {
		this.woffOthers = woffOthers;
	}

	public String getRamId() {
		return ramId;
	}

	public void setRamId(String ramId) {
		this.ramId = ramId;
	}

	public String getTurnOverInYearOne() {
		return turnOverInYearOne;
	}

	public void setTurnOverInYearOne(String turnOverInYearOne) {
		this.turnOverInYearOne = turnOverInYearOne;
	}

	public BigDecimal getTurnOverAmtYearOne() {
		return turnOverAmtYearOne;
	}

	public void setTurnOverAmtYearOne(BigDecimal turnOverAmtYearOne) {
		this.turnOverAmtYearOne = turnOverAmtYearOne;
	}

	public String getTurnOverInYearTwo() {
		return turnOverInYearTwo;
	}

	public void setTurnOverInYearTwo(String turnOverInYearTwo) {
		this.turnOverInYearTwo = turnOverInYearTwo;
	}

	public BigDecimal getTurnOverAmtYearTwo() {
		return turnOverAmtYearTwo;
	}

	public void setTurnOverAmtYearTwo(BigDecimal turnOverAmtYearTwo) {
		this.turnOverAmtYearTwo = turnOverAmtYearTwo;
	}

	public String getTurnOverInYearThree() {
		return turnOverInYearThree;
	}

	public void setTurnOverInYearThree(String turnOverInYearThree) {
		this.turnOverInYearThree = turnOverInYearThree;
	}

	public BigDecimal getTurnOverAmtYearThree() {
		return turnOverAmtYearThree;
	}

	public void setTurnOverAmtYearThree(BigDecimal turnOverAmtYearThree) {
		this.turnOverAmtYearThree = turnOverAmtYearThree;
	}

}
