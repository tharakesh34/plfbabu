package com.pennant.interfaces.model;

import java.math.BigDecimal;
import java.util.Date;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "Transaction")
public class Transaction {

	private Date transactionDate;
	private String accountNumber;
	private String currency;
	private String transactionRef;
	private BigDecimal debitAmount;
	private BigDecimal creditAmount;
	private String transactionNarration;
	private BigDecimal runningBalance;
	private BigDecimal outstandingBalance;
	private String drOrCr;
	private int ccyEditField;
	private BigDecimal txnAmount;

	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++ //
	// ++++++++++++++++++ getter / setter +++++++++++++++++++ //
	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++ //

	public void setTransactionDate(Date transactionDate) {
		this.transactionDate = transactionDate;
	}

	@XmlElement(name = "TransactionDate")
	public Date getTransactionDate() {
		return this.transactionDate;
	}

	public void setAccountNumber(String accountNumber) {
		this.accountNumber = accountNumber;
	}

	@XmlElement(name = "AccountNumber")
	public String getAccountNumber() {
		return this.accountNumber;
	}

	public void setCurrency(String currency) {
		this.currency = currency;
	}

	@XmlElement(name = "Currency")
	public String getCurrency() {
		return this.currency;
	}

	public void setTransactionRef(String transactionRef) {
		this.transactionRef = transactionRef;
	}

	@XmlElement(name = "TransactionRef")
	public String getTransactionRef() {
		return this.transactionRef;
	}

	public void setDebitAmount(BigDecimal debitAmount) {
		this.debitAmount = debitAmount;
	}

	@XmlElement(name = "DebitAmount")
	public BigDecimal getDebitAmount() {
		return this.debitAmount;
	}

	public void setCreditAmount(BigDecimal creditAmount) {
		this.creditAmount = creditAmount;
	}

	@XmlElement(name = "CreditAmount")
	public BigDecimal getCreditAmount() {
		return this.creditAmount;
	}

	public void setTransactionNarration(String transactionNarration) {
		this.transactionNarration = transactionNarration;
	}

	@XmlElement(name = "TransactionNarration")
	public String getTransactionNarration() {
		return this.transactionNarration;
	}

	public void setRunningBalance(BigDecimal runningBalance) {
		this.runningBalance = runningBalance;
	}

	@XmlElement(name = "RunningBalance")
	public BigDecimal getRunningBalance() {
		return this.runningBalance;
	}

	public void setOutstandingBalance(BigDecimal outstandingBalance) {
		this.outstandingBalance = outstandingBalance;
	}

	@XmlElement(name = "OutstandingBalance")
	public BigDecimal getOutstandingBalance() {
		return this.outstandingBalance;
	}

	public String getDrOrCr() {
		return drOrCr;
	}

	public void setDrOrCr(String drOrCr) {
		this.drOrCr = drOrCr;
	}

	public int getCcyEditField() {
		return ccyEditField;
	}

	public void setCcyEditField(int ccyEditField) {
		this.ccyEditField = ccyEditField;
	}

	public BigDecimal getTxnAmount() {
		return txnAmount;
	}

	public void setTxnAmount(BigDecimal txnAmount) {
		this.txnAmount = txnAmount;
	}

}
