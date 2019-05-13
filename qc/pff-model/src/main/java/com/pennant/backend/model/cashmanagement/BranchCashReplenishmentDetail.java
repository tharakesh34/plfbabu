package com.pennant.backend.model.cashmanagement;

import java.math.BigDecimal;
import java.util.Date;

public class BranchCashReplenishmentDetail {

	private long processId;
	private String branchCode;
	private String branchDescription;
	private BigDecimal cashLimit = BigDecimal.ZERO;
	private BigDecimal reOrderLimit = BigDecimal.ZERO;
	private BigDecimal branchCash = BigDecimal.ZERO;
	private BigDecimal autoTransitAmount = BigDecimal.ZERO;
	private BigDecimal adhocTransitAmount = BigDecimal.ZERO;
	private BigDecimal adhocReplenishment = BigDecimal.ZERO;
	private BigDecimal autoReplenishment = BigDecimal.ZERO;
	private BigDecimal totalReplenishment = BigDecimal.ZERO;
	private String approved;
	private BigDecimal amountProcessed = BigDecimal.ZERO;
	private Date transactionDate;
	private long batchId;

	public long getProcessId() {
		return processId;
	}

	public void setProcessId(long processId) {
		this.processId = processId;
	}

	public String getBranchCode() {
		return branchCode;
	}

	public void setBranchCode(String branchCode) {
		this.branchCode = branchCode;
	}

	public String getBranchDescription() {
		return branchDescription;
	}

	public void setBranchDescription(String branchDescription) {
		this.branchDescription = branchDescription;
	}

	public BigDecimal getCashLimit() {
		return cashLimit;
	}

	public void setCashLimit(BigDecimal cashLimit) {
		this.cashLimit = cashLimit;
	}

	public BigDecimal getReOrderLimit() {
		return reOrderLimit;
	}

	public void setReOrderLimit(BigDecimal reOrderLimit) {
		this.reOrderLimit = reOrderLimit;
	}

	public BigDecimal getBranchCash() {
		return branchCash;
	}

	public void setBranchCash(BigDecimal branchCash) {
		this.branchCash = branchCash;
	}

	public BigDecimal getAutoTransitAmount() {
		return autoTransitAmount;
	}

	public void setAutoTransitAmount(BigDecimal autoTransitAmount) {
		this.autoTransitAmount = autoTransitAmount;
	}

	public BigDecimal getAdhocTransitAmount() {
		return adhocTransitAmount;
	}

	public void setAdhocTransitAmount(BigDecimal adhocTransitAmount) {
		this.adhocTransitAmount = adhocTransitAmount;
	}

	public BigDecimal getAdhocReplenishment() {
		return adhocReplenishment;
	}

	public void setAdhocReplenishment(BigDecimal adhocReplenishment) {
		this.adhocReplenishment = adhocReplenishment;
	}

	public BigDecimal getAutoReplenishment() {
		return autoReplenishment;
	}

	public void setAutoReplenishment(BigDecimal autoReplenishment) {
		this.autoReplenishment = autoReplenishment;
	}

	public BigDecimal getTotalReplenishment() {
		return totalReplenishment;
	}

	public void setTotalReplenishment(BigDecimal totalReplenishment) {
		this.totalReplenishment = totalReplenishment;
	}

	public String getApproved() {
		return approved;
	}

	public void setApproved(String approved) {
		this.approved = approved;
	}

	public BigDecimal getAmountProcessed() {
		return amountProcessed;
	}

	public void setAmountProcessed(BigDecimal amountProcessed) {
		this.amountProcessed = amountProcessed;
	}

	public Date getTransactionDate() {
		return transactionDate;
	}

	public void setTransactionDate(Date transactionDate) {
		this.transactionDate = transactionDate;
	}

	public long getBatchId() {
		return batchId;
	}

	public void setBatchId(long batchId) {
		this.batchId = batchId;
	}
}
