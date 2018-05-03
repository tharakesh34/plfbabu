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
 * FileName    		:  LegalExpenses.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  19-04-2016    														*
 *                                                                  						*
 * Modified Date    :  19-04-2016    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 19-04-2016       Pennant	                 0.1                                            * 
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
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import com.pennant.backend.model.Entity;
import com.pennant.backend.util.WorkFlowUtil;
import com.pennanttech.pennapps.core.model.AbstractWorkflowEntity;
import com.pennanttech.pennapps.core.model.LoggedInUser;

/**
 * Model class for the <b>LegalExpenses table</b>.<br>
 *
 */
public class LegalExpenses extends AbstractWorkflowEntity implements Entity {
	private static final long serialVersionUID = 1L;
	
	private long id = Long.MIN_VALUE;
	private String expReference;
	private String customerId;
	private Date bookingDate;
	private BigDecimal amount;
	private String finReference;
	private String transactionType;
	private String transactionTypeName;
	private String remarks;
	private BigDecimal recoveredAmount;
	private BigDecimal amountdue;
	private boolean isRecoverdFromMOPA;
	private BigDecimal totalCharges;
	private boolean newRecord;
	private String lovValue;
	private LegalExpenses befImage;
	private LoggedInUser userDetails;

	public boolean isNew() {
		return isNewRecord();
	}

	public LegalExpenses() {
		super();
		setWorkflowId(WorkFlowUtil.getWorkFlowID("LegalExpenses"));
	}

	
	public long getId() {
		return id;
	}
	public void setId (long id) {
		this.id = id;
	}
	public LegalExpenses(String id) {
		super();
		this.finReference=id;
	}

	public Set<String> getExcludeFields(){
		Set<String> excludeFields=new HashSet<String>();
			excludeFields.add("transactionTypeName");
			excludeFields.add("id");
	return excludeFields;
	}

	    // ******************************************************//
		// ****************** getter / setter  ******************//
		// ******************************************************//

	
	
	
	
		
	
	public Date getBookingDate() {
		return bookingDate;
	}
	public void setBookingDate(Date bookingDate) {
		this.bookingDate = bookingDate;
	}
	
	
		
	
	public BigDecimal getAmount() {
		return amount;
	}
	public void setAmount(BigDecimal amount) {
		this.amount = amount;
	}
	
	
		
	
	public String getFinReference() {
		return finReference;
	}
	public void setFinReference(String finReference) {
		this.finReference = finReference;
	}
	
	
		
	
	public String getTransactionType() {
		return transactionType;
	}
	public void setTransactionType(String transactionType) {
		this.transactionType = transactionType;
	}
	
	public String getTransactionTypeName() {
		return this.transactionTypeName;
	}

	public void setTransactionTypeName (String transactionTypeName) {
		this.transactionTypeName = transactionTypeName;
	}
	
		
	
	public String getRemarks() {
		return remarks;
	}
	public void setRemarks(String remarks) {
		this.remarks = remarks;
	}
	
	
		
	
	public BigDecimal getRecoveredAmount() {
		return recoveredAmount;
	}
	public void setRecoveredAmount(BigDecimal recoveredAmount) {
		this.recoveredAmount = recoveredAmount;
	}
	
	
		
	
	public BigDecimal getAmountdue() {
		return amountdue;
	}
	public void setAmountdue(BigDecimal amountdue) {
		this.amountdue = amountdue;
	}
	
	
		
	
	public boolean isIsRecoverdFromMOPA() {
		return isRecoverdFromMOPA;
	}
	public void setIsRecoverdFromMOPA(boolean isRecoverdFromMOPA) {
		this.isRecoverdFromMOPA = isRecoverdFromMOPA;
	}
	
	
		
	
	public BigDecimal getTotalCharges() {
		return totalCharges;
	}
	public void setTotalCharges(BigDecimal totalCharges) {
		this.totalCharges = totalCharges;
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

	public LegalExpenses getBefImage(){
		return this.befImage;
	}
	
	public void setBefImage(LegalExpenses beforeImage){
		this.befImage=beforeImage;
	}

	public LoggedInUser getUserDetails() {
		return userDetails;
	}

	public void setUserDetails(LoggedInUser userDetails) {
		this.userDetails = userDetails;
	}

	public String getExpReference() {
		return expReference;
	}

	public void setExpReference(String expReference) {
		this.expReference = expReference;
	}

	public String getCustomerId() {
		return customerId;
	}

	public void setCustomerId(String customerId) {
		this.customerId = customerId;
	}
}
