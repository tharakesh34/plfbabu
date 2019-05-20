/**
 * Copyright 2011 - Pennant Technologies
 * 
 * This file is part of Pennant Java Application Framework and related Products. 
 * All components/modules/functions/classes/logic in this software, unless 
 * otherwise stated, the property of Pennant Technologies. 
 * 
 * Copyright and other intellectual property laws protect these materials. 
 * Reproduction or retransmission of the materials, in whole or in part, in any manner, 
 * without the prior written consent of the copyright holder, is a violation of 
 * copyright law.
 */

/**
 ********************************************************************************************
 *                                 FILE HEADER                                              *
 ********************************************************************************************
 *																							*
 * FileName    		:  ProjectedAmortization.java  			                                * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  22-01-2018    														*
 *                                                                  						*
 * Modified Date    :  22-01-2018    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 22-01-2018       Pennant	                 0.1                                            * 
 *                                                                                          * 
 *                                                                                          * 
 *                                                                                          * 
 *                                                                                          * 
 *                                                                                          * 
 *                                                                                          * 
 *                                                                                          * 
 *                                                                                          * 
 ********************************************************************************************
 */
package com.pennant.backend.model.amortization;

import java.io.Serializable;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.Date;

import com.pennant.app.util.DateUtility;
import com.pennant.backend.model.Entity;
import com.pennant.backend.util.PennantConstants;

public class ProjectedAmortization implements Serializable, Entity {
	private static final long serialVersionUID = 7690656031696834080L;

	private long amzLogId = Long.MIN_VALUE;
	private String finReference;
	private long custID;
	private String finType;
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

	// One time activity fields
	private long status;
	private Timestamp startTime;
	private Timestamp endTime;
	private long lastMntBy;

	// THREADS Implementation
	private Date startDate;

	public ProjectedAmortization() {
		super();
	}

	// getters / setters

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

	public long getReferenceID() {
		return referenceID;
	}

	public void setReferenceID(long referenceID) {
		this.referenceID = referenceID;
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

	public Date getMonthEndDate() {
		return monthEndDate;
	}

	public void setMonthEndDate(Date monthEndDate) {
		this.monthEndDate = DateUtility.getDate(DateUtility.format(monthEndDate,
				PennantConstants.dateFormat));

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

	public BigDecimal getActualAmount() {
		return actualAmount;
	}

	public void setActualAmount(BigDecimal actualAmount) {
		this.actualAmount = actualAmount;
	}

	public Date getCalculatedOn() {
		return calculatedOn;
	}

	public void setCalculatedOn(Date calculatedOn) {
		this.calculatedOn = DateUtility.getDate(DateUtility.format(calculatedOn,
				PennantConstants.dateFormat));
	}

	public boolean isSaveProjAMZ() {
		return saveProjAMZ;
	}

	public void setSaveProjAMZ(boolean saveProjAMZ) {
		this.saveProjAMZ = saveProjAMZ;
	}

	public long getIncomeTypeID() {
		return incomeTypeID;
	}

	public void setIncomeTypeID(long incomeTypeID) {
		this.incomeTypeID = incomeTypeID;
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

	public long getAmzLogId() {
		return amzLogId;
	}

	public void setAmzLogId(long amzLogId) {
		this.amzLogId = amzLogId;
	}

	@Override
	public long getId() {
		return amzLogId;
	}

	@Override
	public void setId(long id) {
		this.amzLogId = id;
	}
}
