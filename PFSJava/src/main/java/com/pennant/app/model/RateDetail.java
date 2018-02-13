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
 *
 * FileName    		:  RateDetail.java													*                           
 *                                                                    
 * Author      		:  PENNANT TECHONOLOGIES												*
 *                                                                  
 * Creation Date    :  26-04-2011															*
 *                                                                  
 * Modified Date    :  30-07-2011															*
 *                                                                  
 * Description 		:												 						*                                 
 *                                                                                          
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 26-04-2011       Pennant	                 0.1                                            * 
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
package com.pennant.app.model;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

import com.pennanttech.pennapps.core.model.ErrorDetail;

public class RateDetail implements Serializable {

	private static final long serialVersionUID = -4252917206750602498L;

	private String baseRateCode;
	private String currency;
	private String splRateCode;
	private Date valueDate;

	private BigDecimal baseRefRate = BigDecimal.ZERO;
	private BigDecimal splRefRate = BigDecimal.ZERO;
	private BigDecimal margin = BigDecimal.ZERO;
	private BigDecimal netRefRateDeposit = BigDecimal.ZERO;
	private BigDecimal netRefRateLoan = BigDecimal.ZERO;
	private ErrorDetail errorDetails = null;

	public RateDetail() {
		super();
	}

	public RateDetail(String baseRateCode,String currency, String splRateCode, BigDecimal margin, Date valueDate) {
		super();
		this.baseRateCode = baseRateCode;
		this.currency = currency;
		this.splRateCode = splRateCode;
		this.valueDate = valueDate;
		this.margin = margin;
	}

	// ******************************************************//
	// ****************** getter / setter *******************//
	// ******************************************************//
	
	public String getBaseRateCode() {
		return baseRateCode;
	}
	public void setBaseRateCode(String baseRateCode) {
		this.baseRateCode = baseRateCode;
	}

	public String getCurrency() {
		return currency;
	}
	public void setCurrency(String currency) {
		this.currency = currency;
	}

	public String getSplRateCode() {
		return splRateCode;
	}
	public void setSplRateCode(String splRateCode) {
		this.splRateCode = splRateCode;
	}

	public BigDecimal getMargin() {
		if (this.margin == null) {
			return BigDecimal.ZERO;
		}
		return margin;
	}
	public void setMargin(BigDecimal margin) {
		this.margin = margin;
	}

	public Date getValueDate() {
		return valueDate;
	}
	public void setValueDate(Date valueDate) {
		this.valueDate = valueDate;
	}

	public BigDecimal getBaseRefRate() {
		return baseRefRate;
	}
	public void setBaseRefRate(BigDecimal baseRefRate) {
		this.baseRefRate = baseRefRate;
	}

	public BigDecimal getSplRefRate() {
		return splRefRate;
	}
	public void setSplRefRate(BigDecimal splRefRate) {
		this.splRefRate = splRefRate;
	}

	public BigDecimal getNetRefRateDeposit() {
		return netRefRateDeposit;
	}
	public void setNetRefRateDeposit(BigDecimal netRefRateDeposit) {
		this.netRefRateDeposit = netRefRateDeposit;
	}

	public BigDecimal getNetRefRateLoan() {
		return netRefRateLoan;
	}
	public void setNetRefRateLoan(BigDecimal netRefRateLoan) {
		this.netRefRateLoan = netRefRateLoan;
	}

	public ErrorDetail getErrorDetails() {
		return errorDetails;
	}
	public void setErrorDetails(ErrorDetail errorDetails) {
		this.errorDetails = errorDetails;
	}
}
