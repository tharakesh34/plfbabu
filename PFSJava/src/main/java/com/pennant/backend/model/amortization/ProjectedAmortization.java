package com.pennant.backend.model.amortization;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

import com.pennant.backend.model.Entity;

public class ProjectedAmortization implements Serializable, Entity {
	private static final long serialVersionUID = 7690656031696834080L;

	private long incomeAmzID = Long.MIN_VALUE;
	private String finReference;
	private long custID;
	private String finType;
	private String incomeType;
	private long refenceID;
	private String aMZMethod;
	private Date lastMntOn;
	private BigDecimal amount = BigDecimal.ZERO;
	private BigDecimal calcFactor = BigDecimal.ZERO;
	private BigDecimal amortizedAmount = BigDecimal.ZERO;
	private BigDecimal unAmortizedAmount = BigDecimal.ZERO;
	private BigDecimal curMonthAmz = BigDecimal.ZERO;
	private BigDecimal prvMonthAmz = BigDecimal.ZERO;
	private boolean active;

	private long projIncomeAMZID = Long.MIN_VALUE;
	private BigDecimal cumulativeAmount = BigDecimal.ZERO;
	private Date monthEndDate;

	private boolean updProjAMZ = false;

	public ProjectedAmortization() {

	}

	// getters / setters

	@Override
	public long getId() {
		return incomeAmzID;
	}

	@Override
	public void setId(long id) {
		this.incomeAmzID = id;
	}

	public long getIncomeAmzID() {
		return incomeAmzID;
	}

	public void setIncomeAmzID(long incomeAmzID) {
		this.incomeAmzID = incomeAmzID;
	}

	public String getFinReference() {
		return finReference;
	}

	public void setFinReference(String finReference) {
		this.finReference = finReference;
	}

	public String getIncomeType() {
		return incomeType;
	}

	public void setIncomeType(String incomeType) {
		this.incomeType = incomeType;
	}

	public long getRefenceID() {
		return refenceID;
	}

	public void setRefenceID(long refenceID) {
		this.refenceID = refenceID;
	}

	public Date getLastMntOn() {
		return lastMntOn;
	}

	public void setLastMntOn(Date lastMntOn) {
		this.lastMntOn = lastMntOn;
	}

	public BigDecimal getAmount() {
		return amount;
	}

	public void setAmount(BigDecimal amount) {
		this.amount = amount;
	}

	public BigDecimal getCalcFactor() {
		return calcFactor;
	}

	public void setCalcFactor(BigDecimal calcFactor) {
		this.calcFactor = calcFactor;
	}

	public BigDecimal getAmortizedAmount() {
		return amortizedAmount;
	}

	public void setAmortizedAmount(BigDecimal amortizedAmount) {
		this.amortizedAmount = amortizedAmount;
	}

	public BigDecimal getUnAmortizedAmount() {
		return unAmortizedAmount;
	}

	public void setUnAmortizedAmount(BigDecimal unAmortizedAmount) {
		this.unAmortizedAmount = unAmortizedAmount;
	}

	public BigDecimal getCurMonthAmz() {
		return curMonthAmz;
	}

	public void setCurMonthAmz(BigDecimal curMonthAmz) {
		this.curMonthAmz = curMonthAmz;
	}

	public BigDecimal getPrvMonthAmz() {
		return prvMonthAmz;
	}

	public void setPrvMonthAmz(BigDecimal prvMonthAmz) {
		this.prvMonthAmz = prvMonthAmz;
	}

	public boolean isActive() {
		return active;
	}

	public void setActive(boolean active) {
		this.active = active;
	}

	public String getAMZMethod() {
		return aMZMethod;
	}

	public void setAMZMethod(String aMZMethod) {
		this.aMZMethod = aMZMethod;
	}

	public long getProjIncomeAMZID() {
		return projIncomeAMZID;
	}

	public void setProjIncomeAMZID(long projIncomeAMZID) {
		this.projIncomeAMZID = projIncomeAMZID;
	}

	public Date getMonthEndDate() {
		return monthEndDate;
	}

	public void setMonthEndDate(Date monthEndDate) {
		this.monthEndDate = monthEndDate;
	}

	public BigDecimal getCumulativeAmount() {
		return cumulativeAmount;
	}

	public void setCumulativeAmount(BigDecimal cumulativeAmount) {
		this.cumulativeAmount = cumulativeAmount;
	}

	@Override
	public boolean isNew() {
		return false;
	}

	public long getCustID() {
		return custID;
	}

	public void setCustID(long custID) {
		this.custID = custID;
	}

	public boolean isUpdProjAMZ() {
		return updProjAMZ;
	}

	public void setUpdProjAMZ(boolean updProjAMZ) {
		this.updProjAMZ = updProjAMZ;
	}

	public String getFinType() {
		return finType;
	}

	public void setFinType(String finType) {
		this.finType = finType;
	}
}
