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
 * FileName    		:  FinanceRateReview.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  02-12-2011    														*
 *                                                                  						*
 * Modified Date    :  02-12-2011    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 02-12-2011       Pennant	                 0.1                                            * 
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

package com.pennant.backend.model.finance;

import java.math.BigDecimal;
import java.util.Date;

import com.pennanttech.pennapps.core.model.AbstractWorkflowEntity;

/**
 * Model class for the <b>RepayInstruction table</b>.<br>
 * 
 */

public class FinanceRateReview extends AbstractWorkflowEntity {
	private static final long	serialVersionUID	= 1L;

	private String				finReference		= null;
	private String				rateType			= null;
	private String				currency			= null;
	private Date				valueDate			= null;
	private Date				effectiveDate		= null;
	private Date				eventFromDate		= null;
	private Date				eventToDate			= null;
	private Date				recalFromdate		= null;
	private Date				recalToDate			= null;
	private BigDecimal			eMIAmount			= BigDecimal.ZERO;

	public String getFinReference() {
		return finReference;
	}

	public void setFinReference(String finReference) {
		this.finReference = finReference;
	}

	public String getRateType() {
		return rateType;
	}

	public void setRateType(String rateType) {
		this.rateType = rateType;
	}

	public String getCurrency() {
		return currency;
	}

	public void setCurrency(String currency) {
		this.currency = currency;
	}

	public Date getEffectiveDate() {
		return effectiveDate;
	}

	public void setEffectiveDate(Date effectiveDate) {
		this.effectiveDate = effectiveDate;
	}

	public Date getEventFromDate() {
		return eventFromDate;
	}

	public void setEventFromDate(Date eventFromDate) {
		this.eventFromDate = eventFromDate;
	}

	public Date getEventToDate() {
		return eventToDate;
	}

	public void setEventToDate(Date eventToDate) {
		this.eventToDate = eventToDate;
	}

	public Date getRecalFromdate() {
		return recalFromdate;
	}

	public void setRecalFromdate(Date recalFromdate) {
		this.recalFromdate = recalFromdate;
	}

	public Date getRecalToDate() {
		return recalToDate;
	}

	public void setRecalToDate(Date recalToDate) {
		this.recalToDate = recalToDate;
	}

	public BigDecimal geteMIAmount() {
		return eMIAmount;
	}

	public void seteMIAmount(BigDecimal eMIAmount) {
		this.eMIAmount = eMIAmount;
	}


	public Date getValueDate() {
		return valueDate;
	}

	public void setValueDate(Date valueDate) {
		this.valueDate = valueDate;
	}

}
