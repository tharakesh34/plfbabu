/**
 * Copyright 2011 - Pennant Technologies
 * 
 * This file is part of Pennant Java Application Framework and related Products. All
 * components/modules/functions/classes/logic in this software, unless otherwise stated, the property of Pennant
 * Technologies.
 * 
 * Copyright and other intellectual property laws protect these materials. Reproduction or retransmission of the
 * materials, in whole or in part, in any manner, without the prior written consent of the copyright holder, is a
 * violation of copyright law.
 */

/**
 ********************************************************************************************
 * FILE HEADER *
 ********************************************************************************************
 * * FileName : ProjectedAmortization.java * * Author : PENNANT TECHONOLOGIES * * Creation Date : 22-01-2018 * *
 * Modified Date : 22-01-2018 * * Description : * *
 ********************************************************************************************
 * Date Author Version Comments *
 ********************************************************************************************
 * 22-01-2018 Pennant 0.1 * * * * * * * * *
 ********************************************************************************************
 */
package com.pennant.backend.model.finance;

import java.io.Serializable;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.Date;

public class ProjectedAmortization implements Serializable {
	private static final long serialVersionUID = 7690656031696834080L;

	private long amzLogId = Long.MIN_VALUE;
	private long finID;
	private String finReference;
	private String finType;
	private String finBranch;
	private String finCcy;
	private String entityCode;
	private long custID;
	private String feeTypeCode;
	private String incomeType;
	private long referenceID;
	private long incomeTypeID;
	private String aMZMethod;
	private Date lastMntOn;
	private Date calculatedOn;
	private BigDecimal calcFactor = BigDecimal.ZERO;
	private BigDecimal amount = BigDecimal.ZERO;
	private BigDecimal actualAmount = BigDecimal.ZERO;
	private BigDecimal amortizedAmount = BigDecimal.ZERO;
	private BigDecimal unAmortizedAmount = BigDecimal.ZERO;
	private BigDecimal curMonthAmz = BigDecimal.ZERO;
	private BigDecimal prvMonthAmz = BigDecimal.ZERO;
	private boolean active;
	private BigDecimal cumulativeAmount = BigDecimal.ZERO;
	private Date monthEndDate;
	private boolean updProjAMZ = false;
	private boolean saveProjAMZ = false;
	private long status;
	private Timestamp startTime;
	private Timestamp endTime;
	private long lastMntBy;
	private Date startDate;

	public ProjectedAmortization() {
		super();
	}

	public ProjectedAmortization copyEntity() {
		ProjectedAmortization entity = new ProjectedAmortization();
		entity.setAmzLogId(this.amzLogId);
		entity.setFinID(this.finID);
		entity.setFinReference(this.finReference);
		entity.setFinType(this.finType);
		entity.setFinBranch(this.finBranch);
		entity.setFinCcy(this.finCcy);
		entity.setEntityCode(this.entityCode);
		entity.setCustID(this.custID);
		entity.setFeeTypeCode(this.feeTypeCode);
		entity.setIncomeType(this.incomeType);
		entity.setReferenceID(this.referenceID);
		entity.setIncomeTypeID(this.incomeTypeID);
		entity.setaMZMethod(this.aMZMethod);
		entity.setLastMntOn(this.lastMntOn);
		entity.setCalculatedOn(this.calculatedOn);
		entity.setCalcFactor(this.calcFactor);
		entity.setAmount(this.amount);
		entity.setActualAmount(this.actualAmount);
		entity.setAmortizedAmount(this.amortizedAmount);
		entity.setUnAmortizedAmount(this.unAmortizedAmount);
		entity.setCurMonthAmz(this.curMonthAmz);
		entity.setPrvMonthAmz(this.prvMonthAmz);
		entity.setActive(this.active);
		entity.setCumulativeAmount(this.cumulativeAmount);
		entity.setMonthEndDate(this.monthEndDate);
		entity.setUpdProjAMZ(this.updProjAMZ);
		entity.setSaveProjAMZ(this.saveProjAMZ);
		entity.setStatus(this.status);
		entity.setStartTime(this.startTime);
		entity.setEndTime(this.endTime);
		entity.setLastMntBy(this.lastMntBy);
		entity.setStartDate(this.startDate);
		return entity;
	}

	public long getAmzLogId() {
		return amzLogId;
	}

	public void setAmzLogId(long amzLogId) {
		this.amzLogId = amzLogId;
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

	public String getFinBranch() {
		return finBranch;
	}

	public void setFinBranch(String finBranch) {
		this.finBranch = finBranch;
	}

	public String getFinCcy() {
		return finCcy;
	}

	public void setFinCcy(String finCcy) {
		this.finCcy = finCcy;
	}

	public String getEntityCode() {
		return entityCode;
	}

	public void setEntityCode(String entityCode) {
		this.entityCode = entityCode;
	}

	public long getCustID() {
		return custID;
	}

	public void setCustID(long custID) {
		this.custID = custID;
	}

	public String getFeeTypeCode() {
		return feeTypeCode;
	}

	public void setFeeTypeCode(String feeTypeCode) {
		this.feeTypeCode = feeTypeCode;
	}

	public String getIncomeType() {
		return incomeType;
	}

	public void setIncomeType(String incomeType) {
		this.incomeType = incomeType;
	}

	public long getReferenceID() {
		return referenceID;
	}

	public void setReferenceID(long referenceID) {
		this.referenceID = referenceID;
	}

	public long getIncomeTypeID() {
		return incomeTypeID;
	}

	public void setIncomeTypeID(long incomeTypeID) {
		this.incomeTypeID = incomeTypeID;
	}

	public String getaMZMethod() {
		return aMZMethod;
	}

	public void setaMZMethod(String aMZMethod) {
		this.aMZMethod = aMZMethod;
	}

	public Date getLastMntOn() {
		return lastMntOn;
	}

	public void setLastMntOn(Date lastMntOn) {
		this.lastMntOn = lastMntOn;
	}

	public Date getCalculatedOn() {
		return calculatedOn;
	}

	public void setCalculatedOn(Date calculatedOn) {
		this.calculatedOn = calculatedOn;
	}

	public BigDecimal getCalcFactor() {
		return calcFactor;
	}

	public void setCalcFactor(BigDecimal calcFactor) {
		this.calcFactor = calcFactor;
	}

	public BigDecimal getAmount() {
		return amount;
	}

	public void setAmount(BigDecimal amount) {
		this.amount = amount;
	}

	public BigDecimal getActualAmount() {
		return actualAmount;
	}

	public void setActualAmount(BigDecimal actualAmount) {
		this.actualAmount = actualAmount;
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

	public BigDecimal getCumulativeAmount() {
		return cumulativeAmount;
	}

	public void setCumulativeAmount(BigDecimal cumulativeAmount) {
		this.cumulativeAmount = cumulativeAmount;
	}

	public Date getMonthEndDate() {
		return monthEndDate;
	}

	public void setMonthEndDate(Date monthEndDate) {
		this.monthEndDate = monthEndDate;
	}

	public boolean isUpdProjAMZ() {
		return updProjAMZ;
	}

	public void setUpdProjAMZ(boolean updProjAMZ) {
		this.updProjAMZ = updProjAMZ;
	}

	public boolean isSaveProjAMZ() {
		return saveProjAMZ;
	}

	public void setSaveProjAMZ(boolean saveProjAMZ) {
		this.saveProjAMZ = saveProjAMZ;
	}

	public long getStatus() {
		return status;
	}

	public void setStatus(long status) {
		this.status = status;
	}

	public Timestamp getStartTime() {
		return startTime;
	}

	public void setStartTime(Timestamp startTime) {
		this.startTime = startTime;
	}

	public Timestamp getEndTime() {
		return endTime;
	}

	public void setEndTime(Timestamp endTime) {
		this.endTime = endTime;
	}

	public long getLastMntBy() {
		return lastMntBy;
	}

	public void setLastMntBy(long lastMntBy) {
		this.lastMntBy = lastMntBy;
	}

	public Date getStartDate() {
		return startDate;
	}

	public void setStartDate(Date startDate) {
		this.startDate = startDate;
	}

}
