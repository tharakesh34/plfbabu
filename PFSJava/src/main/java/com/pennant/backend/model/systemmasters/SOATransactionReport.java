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
 * FileName    		:  TransactionReport.java                                               * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  05-05-2011    														*
 *                                                                  						*
 * Modified Date    :  05-05-2011    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 05-05-2011       Pennant	                 0.1                                            * 
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
package com.pennant.backend.model.systemmasters;

import java.math.BigDecimal;
import java.util.Date;

/**
 * Model class for the <b>Academic table</b>.<br>
 * 
 */
public class SOATransactionReport {

	private String finReference;
	private Date transactionDate;
	private String event;
	private BigDecimal transactionAmount;
	private String drOrCr;
	
	private BigDecimal ccyMinorCcyUnits;
	private Date fromDate;
	private Date toDate;
	private int ccyEditField;
	@SuppressWarnings("unused")
	private BigDecimal calTransactionAmount;

	public SOATransactionReport() {
		super();
	}
	
	public Date getTransactionDate() {
		return transactionDate;
	}
	public void setTransactionDate(Date transactionDate) {
		this.transactionDate = transactionDate;
	}

	public String getEvent() {
		return event;
	}
	public void setEvent(String event) {
		this.event = event;
	}

	public BigDecimal getTransactionAmount() {
		return transactionAmount;
	}
	public void setTransactionAmount(BigDecimal transactionAmount) {
		this.transactionAmount = transactionAmount;
	}

	public String getDrOrCr() {
		return drOrCr;
	}
	public void setDrOrCr(String drOrCr) {
		this.drOrCr = drOrCr;
	}

	public String getFinReference() {
		return finReference;
	}

	public void setFinReference(String finReference) {
		this.finReference = finReference;
	}

	public BigDecimal getCcyMinorCcyUnits() {
		return ccyMinorCcyUnits;
	}

	public void setCcyMinorCcyUnits(BigDecimal ccyMinorCcyUnits) {
		this.ccyMinorCcyUnits = ccyMinorCcyUnits;
	}

	public Date getFromDate() {
		return fromDate;
	}

	public void setFromDate(Date fromDate) {
		this.fromDate = fromDate;
	}

	public Date getToDate() {
		return toDate;
	}

	public void setToDate(Date toDate) {
		this.toDate = toDate;
	}

	public int getCcyEditField() {
		return ccyEditField;
	}

	public void setCcyEditField(int ccyEditField) {
		this.ccyEditField = ccyEditField;
	}

	public BigDecimal getCalTransactionAmount() {
		BigDecimal calTransactionAmount = BigDecimal.ZERO;

		if (this.ccyMinorCcyUnits != null && this.ccyMinorCcyUnits.compareTo(BigDecimal.ZERO) > 0 && this.transactionAmount != null) {
			calTransactionAmount = (this.transactionAmount).divide(this.ccyMinorCcyUnits);
		}
		
		return calTransactionAmount;
	}
}
