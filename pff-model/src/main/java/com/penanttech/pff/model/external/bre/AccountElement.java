package com.penanttech.pff.model.external.bre;

import java.io.Serializable;

import javax.xml.bind.annotation.XmlElement;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class AccountElement implements Serializable {

	private static final long serialVersionUID = 1L;
	@XmlElement(name = "BUREAU_TENURE")
	private String bureauTenure;
	@XmlElement(name = "ACCOUNT_TYPE")
	private String accountType;
	@XmlElement(name = "ACCOUNT_NUMBER")
	private String accountNumber;
	@XmlElement(name = "AMOUNT_OVERDUE")
	private String amountOverdue;
	@XmlElement(name = "CURRENT_BALANCE")
	private String currentBalance;
	@XmlElement(name = "DATE_CLOSED")
	private String dateClosed;
	@XmlElement(name = "DATE_OF_LAST_PAYMENT")
	private String dateOfLastPayment;
	@XmlElement(name = "DATE_OPENED_DISBURSED")
	private String dateOpenedDisbursed;
	@XmlElement(name = "DATE_REPORTED")
	private String dateReported;
	@XmlElement(name = "EMI_AMOUNT")
	private String emiAmount;
	@XmlElement(name = "HIGH_CREDIT_SANCTIONED_AMOUNT")
	private String highCreditSanctionedAmount;
	@XmlElement(name = "OWNERSHIP_INDICATOR")
	private String ownershipIndicator;
	@XmlElement(name = "PAYMENT_FREQUENCY")
	private String paymentFrequency;
	@XmlElement(name = "PAYMENT_HISTORY1")
	private String paymentHistory1;
	@XmlElement(name = "PAYMENT_HISTORY2")
	private String paymentHistory2;
	@XmlElement(name = "PAYMENT_HISTORY_END_DATE")
	private String paymentHistoryEndDate;
	@XmlElement(name = "PAYMENT_HISTORY_START_DATE")
	private String paymentHistoryStartDate;
	@XmlElement(name = "RATE_OF_INTEREST")
	private String rateOfInterest;
	@XmlElement(name = "REPAYMENT_TENURE")
	private String repaymentTenure;
	@XmlElement(name = "SETTLEMENT_AMOUNT")
	private String settlementAmount;
	@XmlElement(name = "SUIT_FILED_STATUS")
	private String suitFiledStatus;
	@XmlElement(name = "TYPE_COLLATERAL")
	private String typeCollateral;
	@XmlElement(name = "VALUE_COLLATERAL")
	private String valueCollateral;
	@XmlElement(name = "WOF_PRINCIPAL")
	private String wofPrincipal;
	@XmlElement(name = "WOF_SETTLED_STATUS")
	private String wofSettledStatus;
	@XmlElement(name = "CREDIT_LIMIT")
	private String creditLimit;
	@XmlElement(name = "CASH_LIMIT")
	private String cashLimit;
	@XmlElement(name = "WOF_TOTAL_AMOUNT")
	private String wofTotalAmount;

	@JsonCreator
	public AccountElement() {
	    super();
	}

	public String getBureauTenure() {
		return bureauTenure;
	}

	public void setBureauTenure(String bureauTenure) {
		this.bureauTenure = bureauTenure;
	}

	public String getAccountType() {
		return accountType;
	}

	public void setAccountType(String accountType) {
		this.accountType = accountType;
	}

	public String getAccountNumber() {
		return accountNumber;
	}

	public void setAccountNumber(String accountNumber) {
		this.accountNumber = accountNumber;
	}

	public String getAmountOverdue() {
		return amountOverdue;
	}

	public void setAmountOverdue(String amountOverdue) {
		this.amountOverdue = amountOverdue;
	}

	public String getCurrentBalance() {
		return currentBalance;
	}

	public void setCurrentBalance(String currentBalance) {
		this.currentBalance = currentBalance;
	}

	public String getDateClosed() {
		return dateClosed;
	}

	public void setDateClosed(String dateClosed) {
		this.dateClosed = dateClosed;
	}

	public String getDateOfLastPayment() {
		return dateOfLastPayment;
	}

	public void setDateOfLastPayment(String dateOfLastPayment) {
		this.dateOfLastPayment = dateOfLastPayment;
	}

	public String getDateOpenedDisbursed() {
		return dateOpenedDisbursed;
	}

	public void setDateOpenedDisbursed(String dateOpenedDisbursed) {
		this.dateOpenedDisbursed = dateOpenedDisbursed;
	}

	public String getDateReported() {
		return dateReported;
	}

	public void setDateReported(String dateReported) {
		this.dateReported = dateReported;
	}

	public String getEmiAmount() {
		return emiAmount;
	}

	public void setEmiAmount(String emiAmount) {
		this.emiAmount = emiAmount;
	}

	public String getHighCreditSanctionedAmount() {
		return highCreditSanctionedAmount;
	}

	public void setHighCreditSanctionedAmount(String highCreditSanctionedAmount) {
		this.highCreditSanctionedAmount = highCreditSanctionedAmount;
	}

	public String getOwnershipIndicator() {
		return ownershipIndicator;
	}

	public void setOwnershipIndicator(String ownershipIndicator) {
		this.ownershipIndicator = ownershipIndicator;
	}

	public String getPaymentFrequency() {
		return paymentFrequency;
	}

	public void setPaymentFrequency(String paymentFrequency) {
		this.paymentFrequency = paymentFrequency;
	}

	public String getPaymentHistory1() {
		return paymentHistory1;
	}

	public void setPaymentHistory1(String paymentHistory1) {
		this.paymentHistory1 = paymentHistory1;
	}

	public String getPaymentHistory2() {
		return paymentHistory2;
	}

	public void setPaymentHistory2(String paymentHistory2) {
		this.paymentHistory2 = paymentHistory2;
	}

	public String getPaymentHistoryEndDate() {
		return paymentHistoryEndDate;
	}

	public void setPaymentHistoryEndDate(String paymentHistoryEndDate) {
		this.paymentHistoryEndDate = paymentHistoryEndDate;
	}

	public String getPaymentHistoryStartDate() {
		return paymentHistoryStartDate;
	}

	public void setPaymentHistoryStartDate(String paymentHistoryStartDate) {
		this.paymentHistoryStartDate = paymentHistoryStartDate;
	}

	public String getRateOfInterest() {
		return rateOfInterest;
	}

	public void setRateOfInterest(String rateOfInterest) {
		this.rateOfInterest = rateOfInterest;
	}

	public String getRepaymentTenure() {
		return repaymentTenure;
	}

	public void setRepaymentTenure(String repaymentTenure) {
		this.repaymentTenure = repaymentTenure;
	}

	public String getSettlementAmount() {
		return settlementAmount;
	}

	public void setSettlementAmount(String settlementAmount) {
		this.settlementAmount = settlementAmount;
	}

	public String getSuitFiledStatus() {
		return suitFiledStatus;
	}

	public void setSuitFiledStatus(String suitFiledStatus) {
		this.suitFiledStatus = suitFiledStatus;
	}

	public String getTypeCollateral() {
		return typeCollateral;
	}

	public void setTypeCollateral(String typeCollateral) {
		this.typeCollateral = typeCollateral;
	}

	public String getValueCollateral() {
		return valueCollateral;
	}

	public void setValueCollateral(String valueCollateral) {
		this.valueCollateral = valueCollateral;
	}

	public String getWofPrincipal() {
		return wofPrincipal;
	}

	public void setWofPrincipal(String wofPrincipal) {
		this.wofPrincipal = wofPrincipal;
	}

	public String getWofSettledStatus() {
		return wofSettledStatus;
	}

	public void setWofSettledStatus(String wofSettledStatus) {
		this.wofSettledStatus = wofSettledStatus;
	}

	public String getCreditLimit() {
		return creditLimit;
	}

	public void setCreditLimit(String creditLimit) {
		this.creditLimit = creditLimit;
	}

	public String getCashLimit() {
		return cashLimit;
	}

	public void setCashLimit(String cashLimit) {
		this.cashLimit = cashLimit;
	}

	public String getWofTotalAmount() {
		return wofTotalAmount;
	}

	public void setWofTotalAmount(String wofTotalAmount) {
		this.wofTotalAmount = wofTotalAmount;
	}

}
