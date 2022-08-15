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
 * * FileName : BranchCashDetail.java * * Author : PENNANT TECHONOLOGIES * * Creation Date : 28-02-2018 * * Modified
 * Date : 28-02-2018 * * Description : * *
 ********************************************************************************************
 * Date Author Version Comments *
 ********************************************************************************************
 * 28-02-2018 PENNANT 0.1 * * * * * * * * *
 ********************************************************************************************
 */
package com.pennant.backend.model.cashmanagement;

import java.math.BigDecimal;
import java.util.Date;

/**
 * Model class for the <b>BranchCashDetail table</b>.<br>
 *
 */
public class BranchCashDetail {

	private String branchCode;
	private BigDecimal branchCash = BigDecimal.ZERO;
	private BigDecimal adhocInitiationAmount = BigDecimal.ZERO;
	private BigDecimal adhocProcessingAmount = BigDecimal.ZERO;
	private BigDecimal adhocTransitAmount = BigDecimal.ZERO;
	private BigDecimal autoProcessingAmount = BigDecimal.ZERO;
	private BigDecimal autoTransitAmount = BigDecimal.ZERO;
	private BigDecimal reservedAmount = BigDecimal.ZERO;
	private Date lastEODDate;

	public BranchCashDetail() {
		super();
	}

	public String getBranchCode() {
		return branchCode;
	}

	public void setBranchCode(String branchCode) {
		this.branchCode = branchCode;
	}

	public BigDecimal getBranchCash() {
		return branchCash;
	}

	public void setBranchCash(BigDecimal branchCash) {
		this.branchCash = branchCash;
	}

	public BigDecimal getAdhocInitiationAmount() {
		return adhocInitiationAmount;
	}

	public void setAdhocInitiationAmount(BigDecimal adhocInitiationAmount) {
		this.adhocInitiationAmount = adhocInitiationAmount;
	}

	public BigDecimal getAdhocProcessingAmount() {
		return adhocProcessingAmount;
	}

	public void setAdhocProcessingAmount(BigDecimal adhocProcessingAmount) {
		this.adhocProcessingAmount = adhocProcessingAmount;
	}

	public BigDecimal getAdhocTransitAmount() {
		return adhocTransitAmount;
	}

	public void setAdhocTransitAmount(BigDecimal adhocTransitAmount) {
		this.adhocTransitAmount = adhocTransitAmount;
	}

	public BigDecimal getAutoProcessingAmount() {
		return autoProcessingAmount;
	}

	public void setAutoProcessingAmount(BigDecimal autoProcessingAmount) {
		this.autoProcessingAmount = autoProcessingAmount;
	}

	public BigDecimal getAutoTransitAmount() {
		return autoTransitAmount;
	}

	public void setAutoTransitAmount(BigDecimal autoTransitAmount) {
		this.autoTransitAmount = autoTransitAmount;
	}

	public BigDecimal getReservedAmount() {
		return reservedAmount;
	}

	public void setReservedAmount(BigDecimal reservedAmount) {
		this.reservedAmount = reservedAmount;
	}

	public Date getLastEODDate() {
		return lastEODDate;
	}

	public void setLastEODDate(Date lastEODDate) {
		this.lastEODDate = lastEODDate;
	}

}
