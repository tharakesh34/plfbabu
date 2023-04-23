package com.pennant.backend.model.finance;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.pennant.backend.model.autoknockoff.AutoKnockOffExcessDetails;

public class AutoKnockOffExcess implements Serializable {
	private static final long serialVersionUID = 2800538447276766022L;

	private long iD;
	private long finID;
	private String finReference;
	private String amountType;
	private BigDecimal balanceAmount = BigDecimal.ZERO;
	private Date valueDate;
	private boolean processingFlag;
	private BigDecimal totalUtilizedAmnt = BigDecimal.ZERO;
	private long payableID;
	private String executionDay;
	private String thresholdValue;
	private boolean crossLoanAutoKnockOff;

	private List<AutoKnockOffExcessDetails> excessDetails = new ArrayList<>();

	public AutoKnockOffExcess() {
		super();
	}

	public long getID() {
		return iD;
	}

	public void setID(long iD) {
		this.iD = iD;
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

	public String getAmountType() {
		return amountType;
	}

	public void setAmountType(String amountType) {
		this.amountType = amountType;
	}

	public BigDecimal getBalanceAmount() {
		return balanceAmount;
	}

	public void setBalanceAmount(BigDecimal balanceAmount) {
		this.balanceAmount = balanceAmount;
	}

	public Date getValueDate() {
		return valueDate;
	}

	public void setValueDate(Date valueDate) {
		this.valueDate = valueDate;
	}

	public boolean isProcessingFlag() {
		return processingFlag;
	}

	public void setProcessingFlag(boolean processingFlag) {
		this.processingFlag = processingFlag;
	}

	public BigDecimal getTotalUtilizedAmnt() {
		return totalUtilizedAmnt;
	}

	public void setTotalUtilizedAmnt(BigDecimal totalUtilizedAmnt) {
		this.totalUtilizedAmnt = totalUtilizedAmnt;
	}

	public List<AutoKnockOffExcessDetails> getExcessDetails() {
		return excessDetails;
	}

	public void setExcessDetails(List<AutoKnockOffExcessDetails> excessDetails) {
		this.excessDetails = excessDetails;
	}

	public long getPayableID() {
		return payableID;
	}

	public void setPayableID(long payableID) {
		this.payableID = payableID;
	}

	public String getExecutionDay() {
		return executionDay;
	}

	public void setExecutionDay(String executionDay) {
		this.executionDay = executionDay;
	}

	public String getThresholdValue() {
		return thresholdValue;
	}

	public void setThresholdValue(String thresholdValue) {
		this.thresholdValue = thresholdValue;
	}

	public boolean isCrossLoanAutoKnockOff() {
		return crossLoanAutoKnockOff;
	}

	public void setCrossLoanAutoKnockOff(boolean crossLoanAutoKnockOff) {
		this.crossLoanAutoKnockOff = crossLoanAutoKnockOff;
	}
}
