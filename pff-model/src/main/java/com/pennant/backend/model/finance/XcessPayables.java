package com.pennant.backend.model.finance;

import java.math.BigDecimal;

import com.pennant.backend.model.Entity;

public class XcessPayables implements Entity {

	private long idx = 0;
	private long payableID = 0;
	private String payableType;
	private String payableDesc;
	private BigDecimal amount = BigDecimal.ZERO;
	private BigDecimal reserved = BigDecimal.ZERO;
	private BigDecimal gstAmount = BigDecimal.ZERO;
	private BigDecimal availableAmt = BigDecimal.ZERO;
	private BigDecimal totPaidNow = BigDecimal.ZERO;
	private BigDecimal paidNow = BigDecimal.ZERO;
	private BigDecimal balanceAmt = BigDecimal.ZERO;
	private BigDecimal paidGST = BigDecimal.ZERO;
	private BigDecimal paidCGST = BigDecimal.ZERO;
	private BigDecimal paidSGST = BigDecimal.ZERO;
	private BigDecimal paidIGST = BigDecimal.ZERO;
	private BigDecimal paidUGST = BigDecimal.ZERO;
	private String taxType = "";
	private String feeTypeCode = "";
	private boolean taxApplicable = false;
	private boolean tdsApplicable = false;
	private BigDecimal tdsAmount = BigDecimal.ZERO;
	private String finreference;

	public XcessPayables() {

	}

	public long getPayableID() {
		return payableID;
	}

	public void setPayableID(long payableID) {
		this.payableID = payableID;
	}

	public String getPayableType() {
		return payableType;
	}

	public void setPayableType(String payableType) {
		this.payableType = payableType;
	}

	public String getPayableDesc() {
		return payableDesc;
	}

	public void setPayableDesc(String payableDesc) {
		this.payableDesc = payableDesc;
	}

	public BigDecimal getAmount() {
		return amount;
	}

	public void setAmount(BigDecimal amount) {
		this.amount = amount;
	}

	public BigDecimal getGstAmount() {
		return gstAmount;
	}

	public void setGstAmount(BigDecimal gstAmount) {
		this.gstAmount = gstAmount;
	}

	public BigDecimal getTotPaidNow() {
		return totPaidNow;
	}

	public void setTotPaidNow(BigDecimal totPaidNow) {
		this.totPaidNow = totPaidNow;
	}

	public BigDecimal getBalanceAmt() {
		return balanceAmt;
	}

	public void setBalanceAmt(BigDecimal balanceAmt) {
		this.balanceAmt = balanceAmt;
	}

	@Override
	public boolean isNew() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public long getId() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void setId(long id) {
		// TODO Auto-generated method stub

	}

	public BigDecimal getReserved() {
		return reserved;
	}

	public void setReserved(BigDecimal reserved) {
		this.reserved = reserved;
	}

	public BigDecimal getAvailableAmt() {
		return availableAmt;
	}

	public void setAvailableAmt(BigDecimal availableAmt) {
		this.availableAmt = availableAmt;
	}

	public String getTaxType() {
		return taxType;
	}

	public void setTaxType(String taxType) {
		this.taxType = taxType;
	}

	public BigDecimal getPaidGST() {
		return paidGST;
	}

	public void setPaidGST(BigDecimal paidGST) {
		this.paidGST = paidGST;
	}

	public BigDecimal getPaidCGST() {
		return paidCGST;
	}

	public void setPaidCGST(BigDecimal paidCGST) {
		this.paidCGST = paidCGST;
	}

	public BigDecimal getPaidSGST() {
		return paidSGST;
	}

	public void setPaidSGST(BigDecimal paidSGST) {
		this.paidSGST = paidSGST;
	}

	public BigDecimal getPaidIGST() {
		return paidIGST;
	}

	public void setPaidIGST(BigDecimal paidIGST) {
		this.paidIGST = paidIGST;
	}

	public BigDecimal getPaidUGST() {
		return paidUGST;
	}

	public void setPaidUGST(BigDecimal paidUGST) {
		this.paidUGST = paidUGST;
	}

	public long getIdx() {
		return idx;
	}

	public void setIdx(long idx) {
		this.idx = idx;
	}

	public BigDecimal getPaidNow() {
		return paidNow;
	}

	public void setPaidNow(BigDecimal paidNow) {
		this.paidNow = paidNow;
	}

	public boolean isTaxApplicable() {
		return taxApplicable;
	}

	public void setTaxApplicable(boolean taxApplicable) {
		this.taxApplicable = taxApplicable;
	}

	public String getFeeTypeCode() {
		return feeTypeCode;
	}

	public void setFeeTypeCode(String feeTypeCode) {
		this.feeTypeCode = feeTypeCode;
	}

	public boolean isTdsApplicable() {
		return tdsApplicable;
	}

	public void setTdsApplicable(boolean tdsApplicable) {
		this.tdsApplicable = tdsApplicable;
	}

	public BigDecimal getTdsAmount() {
		return tdsAmount;
	}

	public void setTdsAmount(BigDecimal tdsAmount) {
		this.tdsAmount = tdsAmount;
	}

	public String getFinreference() {
		return finreference;
	}

	public void setFinreference(String finreference) {
		this.finreference = finreference;
	}

}
