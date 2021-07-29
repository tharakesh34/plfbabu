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
 * * FileName : HoldDisbursement.java * * Author : PENNANT TECHONOLOGIES * * Creation Date : 09-10-2018 * * Modified
 * Date : 09-10-2018 * * Description : * *
 ********************************************************************************************
 * Date Author Version Comments *
 ********************************************************************************************
 * 09-10-2018 PENNANT 0.1 * * * * * * * * *
 ********************************************************************************************
 */
package com.pennant.backend.model.finance;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.HashSet;
import java.util.Set;

import com.pennanttech.pennapps.core.model.AbstractWorkflowEntity;
import com.pennanttech.pennapps.core.model.LoggedInUser;

/**
 * Model class for the <b>HoldDisbursement table</b>.<br>
 *
 */
public class HoldDisbursement extends AbstractWorkflowEntity {
	private static final long serialVersionUID = 1L;

	private String finReference = "";
	private boolean hold = false;
	private BigDecimal totalLoanAmt = BigDecimal.ZERO;
	private BigDecimal disbursedAmount = BigDecimal.ZERO;
	private BigDecimal holdLimitAmount = BigDecimal.ZERO;
	private String remarks;
	// private String lovValue;
	private HoldDisbursement befImage;
	private LoggedInUser userDetails;

	public HoldDisbursement() {
		super();
	}

	public HoldDisbursement(String id) {
		super();
		this.setId(id);
	}

	public Set<String> getExcludeFields() {
		Set<String> excludeFields = new HashSet<String>();
		return excludeFields;
	}

	public String getId() {
		return finReference;
	}

	public void setId(String id) {
		this.finReference = id;
	}

	public String getFinReference() {
		return finReference;
	}

	public void setFinReference(String finReference) {
		this.finReference = finReference;
	}

	public boolean isHold() {
		return hold;
	}

	public void setHold(boolean hold) {
		this.hold = hold;
	}

	public BigDecimal getTotalLoanAmt() {
		return totalLoanAmt;
	}

	public void setTotalLoanAmt(BigDecimal totalLoanAmt) {
		this.totalLoanAmt = totalLoanAmt;
	}

	public BigDecimal getDisbursedAmount() {
		return disbursedAmount;
	}

	public void setDisbursedAmount(BigDecimal disbursedAmount) {
		this.disbursedAmount = disbursedAmount;
	}

	public BigDecimal getHoldLimitAmount() {
		return holdLimitAmount;
	}

	public void setHoldLimitAmount(BigDecimal holdLimitAmount) {
		this.holdLimitAmount = holdLimitAmount;
	}

	public String getRemarks() {
		return remarks;
	}

	public void setRemarks(String remarks) {
		this.remarks = remarks;
	}

	/*
	 * public String getLovValue() { return lovValue; }
	 * 
	 * public void setLovValue(String lovValue) { this.lovValue = lovValue; }
	 */
	public HoldDisbursement getBefImage() {
		return this.befImage;
	}

	public void setBefImage(HoldDisbursement beforeImage) {
		this.befImage = beforeImage;
	}

	public LoggedInUser getUserDetails() {
		return userDetails;
	}

	public void setUserDetails(LoggedInUser userDetails) {
		this.userDetails = userDetails;
	}

	public Timestamp getPrevMntOn() {
		return befImage == null ? null : befImage.getLastMntOn();
	}

}
