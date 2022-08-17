package com.pennanttech.pff.overdraft.model;

import java.math.BigDecimal;
import java.util.Date;

public class OverdraftLimitTransation extends OverdraftLimit {
	private static final long serialVersionUID = 1L;

	private long limitID;
	private String txnType;
	private BigDecimal txnAmount = BigDecimal.ZERO;
	private BigDecimal txnCharge = BigDecimal.ZERO;
	private Date txnDate;
	private Date valueDate;
	private String narration;
	private String narration1;
	private String narration2;
	private String narration3;

	public OverdraftLimitTransation() {
		super();
	}

	public long getLimitID() {
		return limitID;
	}

	public void setLimitID(long limitID) {
		this.limitID = limitID;
	}

	public String getTxnType() {
		return txnType;
	}

	public void setTxnType(String txnType) {
		this.txnType = txnType;
	}

	public BigDecimal getTxnAmount() {
		return txnAmount;
	}

	public void setTxnAmount(BigDecimal txnAmt) {
		this.txnAmount = txnAmt;
	}

	public BigDecimal getTxnCharge() {
		return txnCharge;
	}

	public void setTxnCharge(BigDecimal txnCharge) {
		this.txnCharge = txnCharge;
	}

	public Date getTxnDate() {
		return txnDate;
	}

	public void setTxnDate(Date txnDate) {
		this.txnDate = txnDate;
	}

	public Date getValueDate() {
		return valueDate;
	}

	public void setValueDate(Date valueDate) {
		this.valueDate = valueDate;
	}

	public String getNarration() {
		return narration;
	}

	public void setNarration(String narration) {
		this.narration = narration;
	}

	public String getNarration1() {
		return narration1;
	}

	public void setNarration1(String narration1) {
		this.narration1 = narration1;
	}

	public String getNarration2() {
		return narration2;
	}

	public void setNarration2(String narration2) {
		this.narration2 = narration2;
	}

	public String getNarration3() {
		return narration3;
	}

	public void setNarration3(String narration3) {
		this.narration3 = narration3;
	}

}