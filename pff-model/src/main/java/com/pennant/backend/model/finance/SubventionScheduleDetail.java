package com.pennant.backend.model.finance;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

public class SubventionScheduleDetail implements Serializable {

	public SubventionScheduleDetail() {
		super();
	}

	private static final long serialVersionUID = 1L;
	private long finID;
	private String finReference = null;
	private long disbSeqID = 0;
	private Date schDate;
	private int noOfDays;
	private BigDecimal discountedPft = BigDecimal.ZERO;
	private BigDecimal presentValue = BigDecimal.ZERO;
	private BigDecimal futureValue = BigDecimal.ZERO;
	private BigDecimal closingBal = BigDecimal.ZERO;

	private BigDecimal fvPftFraction = BigDecimal.ZERO;
	private BigDecimal cbPftFraction = BigDecimal.ZERO;

	public Set<String> getExcludeFields() {
		Set<String> excludeFields = new HashSet<String>();
		excludeFields.add("fvPftFraction");
		excludeFields.add("cbPftFraction");
		return excludeFields;
	}

	public SubventionScheduleDetail copyEntity() {
		SubventionScheduleDetail entity = new SubventionScheduleDetail();
		entity.setFinID(this.finID);
		entity.setFinReference(this.finReference);
		entity.setDisbSeqID(this.disbSeqID);
		entity.setSchDate(this.schDate);
		entity.setNoOfDays(this.noOfDays);
		entity.setDiscountedPft(this.discountedPft);
		entity.setPresentValue(this.presentValue);
		entity.setFutureValue(this.futureValue);
		entity.setClosingBal(this.closingBal);
		entity.setFvPftFraction(this.fvPftFraction);
		entity.setCbPftFraction(this.cbPftFraction);
		return entity;
	}

	public long getFinID() {
		return finID;
	}

	public void setFinID(long finID) {
		this.finID = finID;
	}

	public String getFinReference() {
		return finReference;
	}

	public void setFinReference(String finReference) {
		this.finReference = finReference;
	}

	public long getDisbSeqID() {
		return disbSeqID;
	}

	public void setDisbSeqID(long disbSeqID) {
		this.disbSeqID = disbSeqID;
	}

	public Date getSchDate() {
		return schDate;
	}

	public void setSchDate(Date schDate) {
		this.schDate = schDate;
	}

	public int getNoOfDays() {
		return noOfDays;
	}

	public void setNoOfDays(int noOfDays) {
		this.noOfDays = noOfDays;
	}

	public BigDecimal getDiscountedPft() {
		return discountedPft;
	}

	public void setDiscountedPft(BigDecimal discountedPft) {
		this.discountedPft = discountedPft;
	}

	public BigDecimal getPresentValue() {
		return presentValue;
	}

	public void setPresentValue(BigDecimal presentValue) {
		this.presentValue = presentValue;
	}

	public BigDecimal getFutureValue() {
		return futureValue;
	}

	public void setFutureValue(BigDecimal futureValue) {
		this.futureValue = futureValue;
	}

	public BigDecimal getClosingBal() {
		return closingBal;
	}

	public void setClosingBal(BigDecimal closingBal) {
		this.closingBal = closingBal;
	}

	public BigDecimal getCbPftFraction() {
		return cbPftFraction;
	}

	public void setCbPftFraction(BigDecimal cbPftFraction) {
		this.cbPftFraction = cbPftFraction;
	}

	public BigDecimal getFvPftFraction() {
		return fvPftFraction;
	}

	public void setFvPftFraction(BigDecimal fvPftFraction) {
		this.fvPftFraction = fvPftFraction;
	}

}
