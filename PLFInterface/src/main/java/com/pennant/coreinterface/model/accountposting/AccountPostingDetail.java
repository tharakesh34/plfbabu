package com.pennant.coreinterface.model.accountposting;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "AccountPostings")
public class AccountPostingDetail implements Serializable {

	private static final long serialVersionUID = 29344053874751317L;

	private String referenceNum;
	private String debitAccountNumber;
	private String debitCcy;
	private String creditAccountNumber;
	private String creditCcy;
	private BigDecimal transactionAmount = BigDecimal.ZERO;
	private String transactionCcy;
	private String paymentMode;
	private String dealRefNum;
	private String transNarration;
	private String dealPurpose;
	private String dealType;
	private List<SecondaryDebitAccount> scndDebitAccountList;
	private String hostReferenceNum;
	private String returnCode;
	private String transRefNum;
	private String debitedCIFID;
	private String creditedCIFID;
	private String debitedCardNoFlag;
	private String creditedCardNoFlag;
	private long timeStamp;

	public AccountPostingDetail() {

	}

	@XmlElement(name = "ReferenceNum")
	public String getReferenceNum() {
		return referenceNum;
	}

	public void setReferenceNum(String referenceNum) {
		this.referenceNum = referenceNum;
	}

	@XmlElement(name = "DebitAccountNumber")
	public String getDebitAccountNumber() {
		return debitAccountNumber;
	}

	public void setDebitAccountNumber(String debitAccountNumber) {
		this.debitAccountNumber = debitAccountNumber;
	}

	@XmlElement(name = "DebitCurrency")
	public String getDebitCcy() {
		return debitCcy;
	}

	public void setDebitCcy(String debitCcy) {
		this.debitCcy = debitCcy;
	}

	@XmlElement(name = "CreditAccountNumber")
	public String getCreditAccountNumber() {
		return creditAccountNumber;
	}

	public void setCreditAccountNumber(String creditAccountNumber) {
		this.creditAccountNumber = creditAccountNumber;
	}

	@XmlElement(name = "CreditCurrency")
	public String getCreditCcy() {
		return creditCcy;
	}

	public void setCreditCcy(String creditCcy) {
		this.creditCcy = creditCcy;
	}

	@XmlElement(name = "DebitedCIFID")
	public String getDebitedCIFID() {
		return debitedCIFID;
	}

	public void setDebitedCIFID(String debitedCIFID) {
		this.debitedCIFID = debitedCIFID;
	}

	@XmlElement(name = "CreditedCIFID")
	public String getCreditedCIFID() {
		return creditedCIFID;
	}

	public void setCreditedCIFID(String creditedCIFID) {
		this.creditedCIFID = creditedCIFID;
	}

	@XmlElement(name = "DebitedCardNoFlag")
	public String getDebitedCardNoFlag() {
		return debitedCardNoFlag;
	}

	public void setDebitedCardNoFlag(String debitedCardNoFlag) {
		this.debitedCardNoFlag = debitedCardNoFlag;
	}

	@XmlElement(name = "CreditedCardNoFlag")
	public String getCreditedCardNoFlag() {
		return creditedCardNoFlag;
	}

	public void setCreditedCardNoFlag(String creditedCardNoFlag) {
		this.creditedCardNoFlag = creditedCardNoFlag;
	}

	@XmlElement(name = "TransactionAmount")
	public BigDecimal getTransactionAmount() {
		return transactionAmount;
	}

	public void setTransactionAmount(BigDecimal transactionAmount) {
		this.transactionAmount = transactionAmount;
	}

	@XmlElement(name = "TransactionCurrency")
	public String getTransactionCcy() {
		return transactionCcy;
	}

	public void setTransactionCcy(String transactionCcy) {
		this.transactionCcy = transactionCcy;
	}

	@XmlElement(name = "PaymentMode")
	public String getPaymentMode() {
		return paymentMode;
	}

	public void setPaymentMode(String paymentMode) {
		this.paymentMode = paymentMode;
	}

	@XmlElement(name = "HostReferenceNum")
	public String getHostReferenceNum() {
		return hostReferenceNum;
	}

	public void setHostReferenceNum(String hostReferenceNum) {
		this.hostReferenceNum = hostReferenceNum;
	}

	@XmlElement(name = "ReturnCode")
	public String getReturnCode() {
		return returnCode;
	}

	public void setReturnCode(String returnCode) {
		this.returnCode = returnCode;
	}

	@XmlElement(name = "TransactionReferenceNum")
	public String getTransRefNum() {
		return transRefNum;
	}

	public void setTransRefNum(String transRefNum) {
		this.transRefNum = transRefNum;
	}

	@XmlElement(name = "TimeStamp")
	public long getTimeStamp() {
		return timeStamp;
	}

	public void setTimeStamp(long timeStamp) {
		this.timeStamp = timeStamp;
	}

	public String getDealRefNum() {
		return dealRefNum;
	}

	public void setDealRefNum(String dealRefNum) {
		this.dealRefNum = dealRefNum;
	}

	public String getTransNarration() {
		return transNarration;
	}

	public void setTransNarration(String transNarration) {
		this.transNarration = transNarration;
	}

	public String getDealPurpose() {
		return dealPurpose;
	}

	public void setDealPurpose(String dealPurpose) {
		this.dealPurpose = dealPurpose;
	}

	public String getDealType() {
		return dealType;
	}

	public void setDealType(String dealType) {
		this.dealType = dealType;
	}

	public List<SecondaryDebitAccount> getScndDebitAccountList() {
		return scndDebitAccountList;
	}

	public void setScndDebitAccountList(List<SecondaryDebitAccount> scndDebitAccountList) {
		this.scndDebitAccountList = scndDebitAccountList;
	}
}
