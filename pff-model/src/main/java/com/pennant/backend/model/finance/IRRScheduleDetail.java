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
 * * FileName : FinanceScheduleDetail.java * * Author : PENNANT TECHONOLOGIES * * Creation Date : 13-08-2012 * *
 * Modified Date : 13-08-2012 * * Description : * *
 ********************************************************************************************
 * Date Author Version Comments *
 ********************************************************************************************
 * 16-03-2012 Pennant 0.1 * * * * * * * * *
 ********************************************************************************************
 */
package com.pennant.backend.model.finance;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

public class IRRScheduleDetail implements Serializable {
	private static final long serialVersionUID = 1L;
	private long finID;
	private String finReference = null;
	private Date schDate;
	private BigDecimal profitCalc = BigDecimal.ZERO;
	private BigDecimal principalCalc = BigDecimal.ZERO;
	private BigDecimal repayAmount = BigDecimal.ZERO;
	private BigDecimal closingBalance = BigDecimal.ZERO;
	private BigDecimal gapInterst = BigDecimal.ZERO;
	private boolean nonSchdRcd = false;

	public IRRScheduleDetail copyEntity() {
		IRRScheduleDetail entity = new IRRScheduleDetail();
		entity.setFinID(this.finID);
		entity.setFinReference(this.finReference);
		entity.setSchDate(this.schDate);
		entity.setProfitCalc(this.profitCalc);
		entity.setPrincipalCalc(this.principalCalc);
		entity.setRepayAmount(this.repayAmount);
		entity.setClosingBalance(this.closingBalance);
		entity.setGapInterst(this.gapInterst);
		entity.setNonSchdRcd(this.nonSchdRcd);
		return entity;
	}

	public String getFinReference() {
		return finReference;
	}

	public void setFinReference(String finReference) {
		this.finReference = finReference;
	}

	public Date getSchDate() {
		return schDate;
	}

	public void setSchDate(Date schDate) {
		this.schDate = schDate;
	}

	public BigDecimal getProfitCalc() {
		return profitCalc;
	}

	public void setProfitCalc(BigDecimal profitCalc) {
		this.profitCalc = profitCalc;
	}

	public BigDecimal getPrincipalCalc() {
		return principalCalc;
	}

	public void setPrincipalCalc(BigDecimal principalCalc) {
		this.principalCalc = principalCalc;
	}

	public BigDecimal getRepayAmount() {
		return repayAmount;
	}

	public void setRepayAmount(BigDecimal repayAmount) {
		this.repayAmount = repayAmount;
	}

	public BigDecimal getClosingBalance() {
		return closingBalance;
	}

	public void setClosingBalance(BigDecimal closingBalance) {
		this.closingBalance = closingBalance;
	}

	public BigDecimal getGapInterst() {
		return gapInterst;
	}

	public void setGapInterst(BigDecimal gapInterst) {
		this.gapInterst = gapInterst;
	}

	public boolean isNonSchdRcd() {
		return nonSchdRcd;
	}

	public void setNonSchdRcd(boolean nonSchdRcd) {
		this.nonSchdRcd = nonSchdRcd;
	}

	public long getFinID() {
		return finID;
	}

	public void setFinID(long finID) {
		this.finID = finID;
	}
}
