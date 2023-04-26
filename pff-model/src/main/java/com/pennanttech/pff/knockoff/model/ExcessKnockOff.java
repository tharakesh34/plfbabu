package com.pennanttech.pff.knockoff.model;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.pennanttech.pennapps.core.model.AbstractWorkflowEntity;

public class ExcessKnockOff extends AbstractWorkflowEntity {
	private static final long serialVersionUID = 1L;

	private long id = Long.MIN_VALUE;
	private long custID;
	private String coreBankId;
	private long finID;
	private String finReference;
	private String finType;
	private long referenceID;
	private String amountType;
	private BigDecimal balanceAmt = BigDecimal.ZERO;
	private String executionDay;
	private String thresholdValue;
	private BigDecimal totalUtilizedAmnt = BigDecimal.ZERO;
	private Date valueDate;

	private List<ExcessKnockOffDetails> excessKnockOffDetails = new ArrayList<>();

	public ExcessKnockOff() {
		super();
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public long getCustID() {
		return custID;
	}

	public void setCustID(long custID) {
		this.custID = custID;
	}

	public String getCoreBankId() {
		return coreBankId;
	}

	public void setCoreBankId(String coreBankId) {
		this.coreBankId = coreBankId;
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

	public String getFinType() {
		return finType;
	}

	public void setFinType(String finType) {
		this.finType = finType;
	}

	public long getReferenceID() {
		return referenceID;
	}

	public void setReferenceID(long referenceID) {
		this.referenceID = referenceID;
	}

	public String getAmountType() {
		return amountType;
	}

	public void setAmountType(String amountType) {
		this.amountType = amountType;
	}

	public BigDecimal getBalanceAmt() {
		return balanceAmt;
	}

	public void setBalanceAmt(BigDecimal balanceAmt) {
		this.balanceAmt = balanceAmt;
	}

	public String getExecutionDay() {
		return executionDay;
	}

	public void setExecutionDay(String executionDay) {
		this.executionDay = executionDay;
	}

	public String getThresholdValue() {
		return thresholdValue;
	}

	public void setThresholdValue(String thresholdValue) {
		this.thresholdValue = thresholdValue;
	}

	public BigDecimal getTotalUtilizedAmnt() {
		return totalUtilizedAmnt;
	}

	public void setTotalUtilizedAmnt(BigDecimal totalUtilizedAmnt) {
		this.totalUtilizedAmnt = totalUtilizedAmnt;
	}

	public Date getValueDate() {
		return valueDate;
	}

	public void setValueDate(Date valueDate) {
		this.valueDate = valueDate;
	}

	public List<ExcessKnockOffDetails> getExcessKnockOffDetails() {
		return excessKnockOffDetails;
	}

	public void setExcessKnockOffDetails(List<ExcessKnockOffDetails> excessKnockOffDetails) {
		this.excessKnockOffDetails = excessKnockOffDetails;
	}

}
