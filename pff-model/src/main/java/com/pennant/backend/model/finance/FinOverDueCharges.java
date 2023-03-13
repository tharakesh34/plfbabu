package com.pennant.backend.model.finance;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

public class FinOverDueCharges implements Serializable {
	private static final long serialVersionUID = -3820386074856359720L;

	private long id;
	private String chargeType;
	private long finID;
	private Date schDate;
	private Date postDate;
	private Date valueDate;
	private BigDecimal amount = BigDecimal.ZERO;
	private BigDecimal paidAmount = BigDecimal.ZERO;
	private BigDecimal waivedAmount = BigDecimal.ZERO;
	private BigDecimal balanceAmt = BigDecimal.ZERO;
	private BigDecimal odPri = BigDecimal.ZERO;
	private BigDecimal odPft = BigDecimal.ZERO;
	private int dueDays;
	private Date finOdTillDate;
	private boolean newRecord;

	public FinOverDueCharges() {
		super();
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getChargeType() {
		return chargeType;
	}

	public void setChargeType(String chargeType) {
		this.chargeType = chargeType;
	}

	public long getFinID() {
		return finID;
	}

	public void setFinID(long finID) {
		this.finID = finID;
	}

	public Date getSchDate() {
		return schDate;
	}

	public void setSchDate(Date schDate) {
		this.schDate = schDate;
	}

	public Date getPostDate() {
		return postDate;
	}

	public void setPostDate(Date postDate) {
		this.postDate = postDate;
	}

	public Date getValueDate() {
		return valueDate;
	}

	public void setValueDate(Date valueDate) {
		this.valueDate = valueDate;
	}

	public BigDecimal getAmount() {
		return amount;
	}

	public void setAmount(BigDecimal amount) {
		this.amount = amount;
	}

	public BigDecimal getPaidAmount() {
		return paidAmount;
	}

	public void setPaidAmount(BigDecimal paidAmount) {
		this.paidAmount = paidAmount;
	}

	public BigDecimal getWaivedAmount() {
		return waivedAmount;
	}

	public void setWaivedAmount(BigDecimal waivedAmount) {
		this.waivedAmount = waivedAmount;
	}

	public BigDecimal getBalanceAmt() {
		return balanceAmt;
	}

	public void setBalanceAmt(BigDecimal balanceAmt) {
		this.balanceAmt = balanceAmt;
	}

	public BigDecimal getOdPri() {
		return odPri;
	}

	public void setOdPri(BigDecimal odPri) {
		this.odPri = odPri;
	}

	public BigDecimal getOdPft() {
		return odPft;
	}

	public void setOdPft(BigDecimal odPft) {
		this.odPft = odPft;
	}

	public int getDueDays() {
		return dueDays;
	}

	public void setDueDays(int dueDays) {
		this.dueDays = dueDays;
	}

	public Date getFinOdTillDate() {
		return finOdTillDate;
	}

	public void setFinOdTillDate(Date finOdTillDate) {
		this.finOdTillDate = finOdTillDate;
	}

	public boolean isNewRecord() {
		return newRecord;
	}

	public void setNewRecord(boolean newRecord) {
		this.newRecord = newRecord;
	}
}
