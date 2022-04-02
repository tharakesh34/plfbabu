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

public class IRRCashFlow implements Serializable {
	private static final long serialVersionUID = 1L;
	private Date cfDate;
	private BigDecimal cfAmount = BigDecimal.ZERO;
	private BigDecimal cfFee = BigDecimal.ZERO;
	private boolean nonSchdRcd = false;

	public IRRCashFlow copyEntity() {
		IRRCashFlow entity = new IRRCashFlow();
		entity.setCfDate(this.cfDate);
		entity.setCfAmount(this.cfAmount);
		entity.setCfFee(this.cfFee);
		entity.setNonSchdRcd(this.nonSchdRcd);
		return entity;
	}

	public Date getCfDate() {
		return cfDate;
	}

	public void setCfDate(Date cfDate) {
		this.cfDate = cfDate;
	}

	public BigDecimal getCfAmount() {
		return cfAmount;
	}

	public void setCfAmount(BigDecimal cfAmount) {
		this.cfAmount = cfAmount;
	}

	public BigDecimal getCfFee() {
		return cfFee;
	}

	public void setCfFee(BigDecimal cfFee) {
		this.cfFee = cfFee;
	}

	public boolean isNonSchdRcd() {
		return nonSchdRcd;
	}

	public void setNonSchdRcd(boolean nonSchdRcd) {
		this.nonSchdRcd = nonSchdRcd;
	}
}
