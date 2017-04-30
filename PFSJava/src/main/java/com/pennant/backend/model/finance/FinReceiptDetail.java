package com.pennant.backend.model.finance;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.pennant.backend.model.Entity;

public class FinReceiptDetail implements Entity{

	private long receiptID = 0;// Only setting from Receipt Header
	private long receiptSeqID = 0;// Auto Generated
	private String receiptType;
	private String paymentTo;
	private String paymentType;
	private long payAgainstID = 0;
	private BigDecimal amount = BigDecimal.ZERO;
	private String favourNumber;
	private Date valueDate;
	private String bankCode;
	private String bankCodeDesc;
	private String favourName;
	private Date depositDate;
	private String depositNo;
	private String paymentRef;
	private String transactionRef;
	private String chequeAcNo;
	private long fundingAc = 0;
	private String fundingAcDesc;
	private Date receivedDate;
	private String status;
	private String remarks;
	
	private List<FinRepayHeader> repayHeaders = new ArrayList<FinRepayHeader>(1);
	
	public FinReceiptDetail() {
		
	}

	// ******************************************************//
	// ****************** getter / setter *******************//
	// ******************************************************//

	@Override
	public boolean isNew() {
		return false;
	}

	@Override
	public long getId() {
		return receiptSeqID;
	}

	@Override
	public void setId(long id) {
		this.receiptSeqID = id;
	}
	
	public long getReceiptID() {
		return receiptID;
	}
	public void setReceiptID(long receiptID) {
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

	public long getPayAgainstID() {
		return payAgainstID;
	}
	public void setPayAgainstID(long payAgainstID) {
		this.payAgainstID = payAgainstID;
	}

	public BigDecimal getAmount() {
		return amount;
	}
	public void setAmount(BigDecimal amount) {
		this.amount = amount;
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

	public Date getDepositDate() {
		return depositDate;
	}
	public void setDepositDate(Date depositDate) {
		this.depositDate = depositDate;
	}

	public String getDepositNo() {
		return depositNo;
	}
	public void setDepositNo(String depositNo) {
		this.depositNo = depositNo;
	}

	public String getPaymentRef() {
		return paymentRef;
	}
	public void setPaymentRef(String paymentRef) {
		this.paymentRef = paymentRef;
	}

	public String getTransactionRef() {
		return transactionRef;
	}
	public void setTransactionRef(String transactionRef) {
		this.transactionRef = transactionRef;
	}

	public String getChequeAcNo() {
		return chequeAcNo;
	}
	public void setChequeAcNo(String chequeAcNo) {
		this.chequeAcNo = chequeAcNo;
	}

	public long getFundingAc() {
		return fundingAc;
	}
	public void setFundingAc(long fundingAc) {
		this.fundingAc = fundingAc;
	}

	public Date getReceivedDate() {
		return receivedDate;
	}
	public void setReceivedDate(Date receivedDate) {
		this.receivedDate = receivedDate;
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

	public String getBankCodeDesc() {
		return bankCodeDesc;
	}

	public void setBankCodeDesc(String bankCodeDesc) {
		this.bankCodeDesc = bankCodeDesc;
	}

	public String getFundingAcDesc() {
		return fundingAcDesc;
	}

	public void setFundingAcDesc(String fundingAcDesc) {
		this.fundingAcDesc = fundingAcDesc;
	}

}
