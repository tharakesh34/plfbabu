package com.pennant.interfaceservice.model;

import java.math.BigDecimal;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "ReversalReply")
public class ReversalPostingReply {

	private String debitAccountNumber;
	private String debitCcy;
	private String creditAccountNumber;
	private String creditCcy;
	private String debitedCIFID;
	private String creditedCIFID;
	private String debitedCardNoFlag;
	private String creditedCardNoFlag;
	private BigDecimal transactionAmount;
	private String transactionCcy;
	private String paymentMode;
	private String debitedTransRemarks;
	private String creditedTransRemarks;
	private String orgRefNumber;
	private String referenceNum;
	private String hostReferenceNum;
	private String transRefNum;
	private String t24RefNum;
	private String hpsReference;
	private String returnCode;
	private String returnText;
	private long timeStamp;

	public ReversalPostingReply() {

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

	@XmlElement(name = "DebitedTransactionRemarks")
	public String getDebitedTransRemarks() {
		return debitedTransRemarks;
	}

	public void setDebitedTransRemarks(String debitedTransRemarks) {
		this.debitedTransRemarks = debitedTransRemarks;
	}

	@XmlElement(name = "CreditedTransactionRemarks")
	public String getCreditedTransRemarks() {
		return creditedTransRemarks;
	}

	public void setCreditedTransRemarks(String creditedTransRemarks) {
		this.creditedTransRemarks = creditedTransRemarks;
	}

	@XmlElement(name = "OrgRefNumber")
	public String getOrgRefNumber() {
		return orgRefNumber;
	}

	public void setOrgRefNumber(String orgRefNumber) {
		this.orgRefNumber = orgRefNumber;
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

	@XmlElement(name = "ReturnText")
	public String getReturnText() {
		return returnText;
	}

	public void setReturnText(String returnText) {
		this.returnText = returnText;
	}

	@XmlElement(name = "TransactionReferenceNum")
	public String getTransRefNum() {
		return transRefNum;
	}

	public void setTransRefNum(String transRefNum) {
		this.transRefNum = transRefNum;
	}

	@XmlElement(name = "T24ReferenceNum")
	public String getT24RefNum() {
		return t24RefNum;
	}

	public void setT24RefNum(String t24RefNum) {
		this.t24RefNum = t24RefNum;
	}

	@XmlElement(name = "HPSReference")
	public String getHpsReference() {
		return hpsReference;
	}

	public void setHpsReference(String hpsReference) {
		this.hpsReference = hpsReference;
	}

	@XmlElement(name = "TimeStamp")
	public long getTimeStamp() {
		return timeStamp;
	}

	public void setTimeStamp(long timeStamp) {
		this.timeStamp = timeStamp;
	}

}
