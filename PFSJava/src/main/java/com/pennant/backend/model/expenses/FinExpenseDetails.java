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
 * FileName    		:  FinExpenseDetails.java	                                            * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  17-12-2017    														*
 *                                                                  						*
 * Modified Date    :  17-12-2017     														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 27-05-2011       Pennant	                 0.1                                            * 
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
package com.pennant.backend.model.expenses;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Set;

import com.pennanttech.pennapps.core.model.AbstractWorkflowEntity;

public class FinExpenseDetails extends AbstractWorkflowEntity {

	private static final long	serialVersionUID	= -7156982149543027619L;

	private long				finExpenseId		= Long.MIN_VALUE;
	private String				finReference		= null;
	private long				expenseTypeId		= 0;
	private BigDecimal			amount				= BigDecimal.ZERO;
	private String				expenseTypeCode;
	private String				expenseTypeDesc;

	public FinExpenseDetails() {
		super();
	}

	public Set<String> getExcludeFields() {

		Set<String> excludeFields = new HashSet<String>();
		excludeFields.add("expenseTypeCode");
		excludeFields.add("expenseTypeDesc");

		return excludeFields;
	}

	public long getFinExpenseId() {
		return finExpenseId;
	}

	public void setFinExpenseId(long finExpenseId) {
		this.finExpenseId = finExpenseId;
	}

	public String getFinReference() {
		return finReference;
	}

	public void setFinReference(String finReference) {
		this.finReference = finReference;
	}

	public BigDecimal getAmount() {
		return amount;
	}

	public void setAmount(BigDecimal amount) {
		this.amount = amount;
	}

	public boolean isNew() {
		return false;
	}

	public long getId() {
		return this.finExpenseId;
	}

	public void setId(long finExpenseId) {
		this.finExpenseId = finExpenseId;
	}

	public long getExpenseTypeId() {
		return expenseTypeId;
	}

	public void setExpenseTypeId(long expenseTypeId) {
		this.expenseTypeId = expenseTypeId;
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
}