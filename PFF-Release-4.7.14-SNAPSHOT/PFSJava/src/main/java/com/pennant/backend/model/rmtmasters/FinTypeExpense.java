package com.pennant.backend.model.rmtmasters;

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
 * FileName    		:  FinTypeExpense.java                                                  * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  15-12-2017    														*
 *                                                                  						*
 * Modified Date    :  15-12-2017    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 15-12-2017       Pennant	                 0.1                                            * 
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

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Set;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;

import com.pennant.app.constants.AccountEventConstants;
import com.pennanttech.pennapps.core.model.AbstractWorkflowEntity;
import com.pennanttech.pennapps.core.model.LoggedInUser;

/**
 * Model class for the <b>FinTypeExpenses table</b>.<br>
 *
 */

@XmlAccessorType(XmlAccessType.NONE)
public class FinTypeExpense extends AbstractWorkflowEntity {
	private static final long	serialVersionUID	= 1L;
	private String				finType				= null;
	private long				finTypeExpenseID	= Long.MIN_VALUE;
	private long				expenseTypeID		= Long.MIN_VALUE;
	private String				expenseTypeCode;
	private String				expenseTypeDesc;
	private String				calculationType;
	private BigDecimal			amount				= BigDecimal.ZERO;
	private BigDecimal			percentage			= BigDecimal.ZERO;
	private String				calculateOn;
	private boolean				amortReq;
	private boolean				taxApplicable;
	private boolean				active;
	private String				finEvent			= AccountEventConstants.ACCEVENT_ADDDBSP;

	private boolean				newRecord			= false;
	private String				lovValue;
	private FinTypeExpense		befImage;

	private LoggedInUser		userDetails;

	public boolean isNew() {
		return isNewRecord();
	}

	public FinTypeExpense() {
		super();
	}

	public FinTypeExpense(String id) {
		super();
	}

	public Set<String> getExcludeFields() {
		Set<String> excludeFields = new HashSet<String>();
		excludeFields.add("expenseTypeCode");
		excludeFields.add("expenseTypeDesc");
		return excludeFields;
	}
	//Getter and Setter methods

	public String getFinType() {
		return finType;
	}

	public void setFinType(String finType) {
		this.finType = finType;
	}

	public long getFinTypeExpenseID() {
		return finTypeExpenseID;
	}

	public void setFinTypeExpenseID(long finTypeExpenseID) {
		this.finTypeExpenseID = finTypeExpenseID;
	}

	public long getExpenseTypeID() {
		return expenseTypeID;
	}

	public void setExpenseTypeID(long expenseTypeID) {
		this.expenseTypeID = expenseTypeID;
	}

	public String getExpenseTypeCode() {
		return expenseTypeCode;
	}

	public void setExpenseTypeCode(String expenseTypeCode) {
		this.expenseTypeCode = expenseTypeCode;
	}

	public String getExpenseTypeDesc() {
		return expenseTypeDesc;
	}

	public void setExpenseTypeDesc(String expenseTypeDesc) {
		this.expenseTypeDesc = expenseTypeDesc;
	}

	public String getCalculationType() {
		return calculationType;
	}

	public void setCalculationType(String calculationType) {
		this.calculationType = calculationType;
	}

	public BigDecimal getAmount() {
		return amount;
	}

	public void setAmount(BigDecimal amount) {
		this.amount = amount;
	}

	public BigDecimal getPercentage() {
		return percentage;
	}

	public void setPercentage(BigDecimal percentage) {
		this.percentage = percentage;
	}

	public String getCalculateOn() {
		return calculateOn;
	}

	public void setCalculateOn(String calculateOn) {
		this.calculateOn = calculateOn;
	}

	public boolean isAmortReq() {
		return amortReq;
	}

	public void setAmortReq(boolean amortReq) {
		this.amortReq = amortReq;
	}

	public boolean isTaxApplicable() {
		return taxApplicable;
	}

	public void setTaxApplicable(boolean taxApplicable) {
		this.taxApplicable = taxApplicable;
	}

	public boolean isActive() {
		return active;
	}

	public void setActive(boolean active) {
		this.active = active;
	}

	public boolean isNewRecord() {
		return newRecord;
	}

	public void setNewRecord(boolean newRecord) {
		this.newRecord = newRecord;
	}

	public String getLovValue() {
		return lovValue;
	}

	public void setLovValue(String lovValue) {
		this.lovValue = lovValue;
	}

	public FinTypeExpense getBefImage() {
		return this.befImage;
	}

	public void setBefImage(FinTypeExpense beforeImage) {
		this.befImage = beforeImage;
	}

	public LoggedInUser getUserDetails() {
		return userDetails;
	}

	public void setUserDetails(LoggedInUser userDetails) {
		this.userDetails = userDetails;
	}

	public long getId() {

		return finTypeExpenseID;
	}

	public void setId(long id) {
		this.finTypeExpenseID = id;

	}
	
	public String getFinEvent() {
		return finEvent;
	}

	public void setFinEvent(String finEvent) {
		this.finEvent = finEvent;
	}

}
