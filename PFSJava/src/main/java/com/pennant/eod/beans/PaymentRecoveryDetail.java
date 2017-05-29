package com.pennant.eod.beans;

import java.math.BigDecimal;
import java.util.Date;

public class PaymentRecoveryDetail {

	private String		batchRefNumber;

	private String		transactionReference;
	private String		primaryDebitAccount;
	private String		secondaryDebitAccounts; // (with= ;delimited)
	private String		creditAccount;
	private Date		scheduleDate;
	private String		financeReference;
	private String		customerReference;
	private String		debitCurrency;
	private String		creditCurrency;
	private BigDecimal	paymentAmount;
	private String		transactionPurpose;
	private String		financeBranch;
	private String		financeType;
	private String		financePurpose;
	// response
	private String		sysTranRef;
	private BigDecimal	primaryAcDebitAmt;
	private String		secondaryAcDebitAmt;
	private String		paymentStatus;
	private int			priority;
	//others
	private String finEvent;
	private String finRpyFor;

	private String		recordIdentifier;
	
	public PaymentRecoveryDetail() {
		super();
	}

	public String getRecordIdentifier() {
		return recordIdentifier;
	}

	public void setRecordIdentifier(String recordIdentifier) {
		this.recordIdentifier = recordIdentifier;
	}

	public String getTransactionReference() {
		return transactionReference;
	}

	public void setTransactionReference(String transactionReference) {
		this.transactionReference = transactionReference;
	}

	public String getPrimaryDebitAccount() {
		return primaryDebitAccount;
	}

	public void setPrimaryDebitAccount(String primaryDebitAccount) {
		this.primaryDebitAccount = primaryDebitAccount;
	}

	public String getSecondaryDebitAccounts() {
		return secondaryDebitAccounts;
	}

	public void setSecondaryDebitAccounts(String secondaryDebitAccounts) {
		this.secondaryDebitAccounts = secondaryDebitAccounts;
	}

	public String getCreditAccount() {
		return creditAccount;
	}

	public void setCreditAccount(String creditAccount) {
		this.creditAccount = creditAccount;
	}

	public Date getScheduleDate() {
		return scheduleDate;
	}

	public void setScheduleDate(Date scheduleDate) {
		this.scheduleDate = scheduleDate;
	}

	public String getFinanceReference() {
		return financeReference;
	}

	public void setFinanceReference(String financeReference) {
		this.financeReference = financeReference;
	}

	public String getCustomerReference() {
		return customerReference;
	}

	public void setCustomerReference(String customerReference) {
		this.customerReference = customerReference;
	}

	public String getDebitCurrency() {
		return debitCurrency;
	}

	public void setDebitCurrency(String debitCurrency) {
		this.debitCurrency = debitCurrency;
	}

	public String getCreditCurrency() {
		return creditCurrency;
	}

	public void setCreditCurrency(String creditCurrency) {
		this.creditCurrency = creditCurrency;
	}

	public BigDecimal getPaymentAmount() {
		return paymentAmount;
	}

	public void setPaymentAmount(BigDecimal paymentAmount) {
		this.paymentAmount = paymentAmount;
	}

	public String getTransactionPurpose() {
		return transactionPurpose;
	}

	public void setTransactionPurpose(String transactionPurpose) {
		this.transactionPurpose = transactionPurpose;
	}

	public String getFinanceBranch() {
		return financeBranch;
	}

	public void setFinanceBranch(String financeBranch) {
		this.financeBranch = financeBranch;
	}

	public String getFinanceType() {
		return financeType;
	}

	public void setFinanceType(String financeType) {
		this.financeType = financeType;
	}

	public String getFinancePurpose() {
		return financePurpose;
	}

	public void setFinancePurpose(String financePurpose) {
		this.financePurpose = financePurpose;
	}

	public String getSysTranRef() {
		return sysTranRef;
	}

	public void setSysTranRef(String coreTranRef) {
		this.sysTranRef = coreTranRef;
	}

	public BigDecimal getPrimaryAcDebitAmt() {
		return primaryAcDebitAmt;
	}

	public void setPrimaryAcDebitAmt(BigDecimal primaryAcDebitAmt) {
		this.primaryAcDebitAmt = primaryAcDebitAmt;
	}

	public String getSecondaryAcDebitAmt() {
		return secondaryAcDebitAmt;
	}

	public void setSecondaryAcDebitAmt(String secondaryAcDebitAmt) {
		this.secondaryAcDebitAmt = secondaryAcDebitAmt;
	}

	public String getPaymentStatus() {
		return paymentStatus;
	}

	public void setPaymentStatus(String paymentStatus) {
		this.paymentStatus = paymentStatus;
	}

	public String getBatchRefNumber() {
		return batchRefNumber;
	}

	public void setBatchRefNumber(String batchRefNumber) {
		this.batchRefNumber = batchRefNumber;
	}

	public int getPriority() {
		return priority;
	}

	public void setPriority(int priority) {
		this.priority = priority;
	}

	public String getFinEvent() {
		return finEvent;
	}

	public void setFinEvent(String finEvent) {
		this.finEvent = finEvent;
	}

	public String getFinRpyFor() {
		return finRpyFor;
	}

	public void setFinRpyFor(String finRpyFor) {
		this.finRpyFor = finRpyFor;
	}

}
