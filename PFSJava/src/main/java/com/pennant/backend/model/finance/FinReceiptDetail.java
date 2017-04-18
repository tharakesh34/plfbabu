package com.pennant.backend.model.finance;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class FinReceiptDetail {

	private int receiptID = 0;// Only setting from Receipt Header
	private long receiptSeqID = 0;// Auto Generated
	private String receiptType;
	private String paymentTo;
	private String paymentType;
	private int payAgainstID = 0;
	private BigDecimal amount = BigDecimal.ZERO;
	private String bankCode;
	private String favourName;
	private String favourNumber;
	private Date valueDate;
	private Date receivedDate;
	private long bankBranchID = 0;
	private String acHolderName;
	private String accountNo;
	private String phoneCountryCode;
	private String phoneAreaCode;
	private String phoneSubCode;
	private String status;
	private String remarks;
	
	private List<FinRepayHeader> repayHeaders = new ArrayList<FinRepayHeader>(1);
	
	public FinReceiptDetail() {
		
	}

	// ******************************************************//
	// ****************** getter / setter *******************//
	// ******************************************************//
	
	public int getReceiptID() {
		return receiptID;
	}
	public void setReceiptID(int receiptID) {
		this.receiptID = receiptID;
	}
	
	public long getReceiptSeqID() {
		return receiptSeqID;
	}
	public void setReceiptSeqID(long receiptSeqID) {
		this.receiptSeqID = receiptSeqID;
	}

	public String getReceiptType() {
		return receiptType;
	}
	public void setReceiptType(String receiptType) {
		this.receiptType = receiptType;
	}

	public String getPaymentTo() {
		return paymentTo;
	}
	public void setPaymentTo(String paymentTo) {
		this.paymentTo = paymentTo;
	}

	public String getPaymentType() {
		return paymentType;
	}
	public void setPaymentType(String paymentType) {
		this.paymentType = paymentType;
	}

	public int getPayAgainstID() {
		return payAgainstID;
	}
	public void setPayAgainstID(int payAgainstID) {
		this.payAgainstID = payAgainstID;
	}

	public BigDecimal getAmount() {
		return amount;
	}
	public void setAmount(BigDecimal amount) {
		this.amount = amount;
	}

	public String getBankCode() {
		return bankCode;
	}
	public void setBankCode(String bankCode) {
		this.bankCode = bankCode;
	}

	public String getFavourName() {
		return favourName;
	}
	public void setFavourName(String favourName) {
		this.favourName = favourName;
	}

	public String getFavourNumber() {
		return favourNumber;
	}
	public void setFavourNumber(String favourNumber) {
		this.favourNumber = favourNumber;
	}

	public Date getValueDate() {
		return valueDate;
	}
	public void setValueDate(Date valueDate) {
		this.valueDate = valueDate;
	}

	public Date getReceivedDate() {
		return receivedDate;
	}
	public void setReceivedDate(Date receivedDate) {
		this.receivedDate = receivedDate;
	}

	public long getBankBranchID() {
		return bankBranchID;
	}
	public void setBankBranchID(long bankBranchID) {
		this.bankBranchID = bankBranchID;
	}

	public String getAcHolderName() {
		return acHolderName;
	}
	public void setAcHolderName(String acHolderName) {
		this.acHolderName = acHolderName;
	}

	public String getAccountNo() {
		return accountNo;
	}
	public void setAccountNo(String accountNo) {
		this.accountNo = accountNo;
	}

	public String getPhoneCountryCode() {
		return phoneCountryCode;
	}
	public void setPhoneCountryCode(String phoneCountryCode) {
		this.phoneCountryCode = phoneCountryCode;
	}

	public String getPhoneAreaCode() {
		return phoneAreaCode;
	}
	public void setPhoneAreaCode(String phoneAreaCode) {
		this.phoneAreaCode = phoneAreaCode;
	}

	public String getPhoneSubCode() {
		return phoneSubCode;
	}
	public void setPhoneSubCode(String phoneSubCode) {
		this.phoneSubCode = phoneSubCode;
	}

	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}

	public String getRemarks() {
		return remarks;
	}
	public void setRemarks(String remarks) {
		this.remarks = remarks;
	}

	public List<FinRepayHeader> getRepayHeaders() {
		return repayHeaders;
	}

	public void setRepayHeaders(List<FinRepayHeader> repayHeaders) {
		this.repayHeaders = repayHeaders;
	}
	
}
