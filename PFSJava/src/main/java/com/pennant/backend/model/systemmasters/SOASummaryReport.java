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
 * FileName    		:  SOASummaryReport.java                                                * 	  
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

import com.pennant.app.util.DateUtility;

/**
 * Model class for the <b>Academic table</b>.<br>
 * 
 */
public class SOASummaryReport {

	private String finReference;
	private String component;
	private BigDecimal due;
	private BigDecimal receipt;
	private BigDecimal overDue;
	
	private BigDecimal ccyMinorCcyUnits;
	
	@SuppressWarnings("unused")
	private Date appDate;
	@SuppressWarnings("unused")
	private BigDecimal calDue;
	@SuppressWarnings("unused")
	private BigDecimal calReceipt;
	@SuppressWarnings("unused")
	private BigDecimal calOverDue;
	
	public SOASummaryReport() {
		super();
	}

	public String getComponent() {
		return component;
	}

	public void setComponent(String component) {
		this.component = component;
	}

	public BigDecimal getDue() {
		return due;
	}

	public void setDue(BigDecimal due) {
		this.due = due;
	}

	public BigDecimal getReceipt() {
		return receipt;
	}

	public void setReceipt(BigDecimal receipt) {
		this.receipt = receipt;
	}

	public BigDecimal getOverDue() {
		return overDue;
	}

	public void setOverDue(BigDecimal overDue) {
		this.overDue = overDue;
	}

	public String getFinReference() {
		return finReference;
	}

	public void setFinReference(String finReference) {
		this.finReference = finReference;
	}

	public Date getAppDate() {
		return DateUtility.getAppDate();
	}

	public BigDecimal getCalDue() {
		BigDecimal calDue = BigDecimal.ZERO;

		if (this.ccyMinorCcyUnits != null && this.ccyMinorCcyUnits.compareTo(BigDecimal.ZERO) > 0 && this.due != null) {
			calDue = (this.due).divide(this.ccyMinorCcyUnits);
		}

		return calDue;
	}

	public BigDecimal getCalReceipt() {
		BigDecimal calReceipt = BigDecimal.ZERO;

		if (this.ccyMinorCcyUnits != null && this.ccyMinorCcyUnits.compareTo(BigDecimal.ZERO) > 0 && this.receipt != null) {
			calReceipt = (this.receipt).divide(this.ccyMinorCcyUnits);
		}

		return calReceipt;
	}

	public BigDecimal getCalOverDue() {
		BigDecimal calOverDue = BigDecimal.ZERO;

		if (this.ccyMinorCcyUnits != null && this.ccyMinorCcyUnits.compareTo(BigDecimal.ZERO) > 0 && this.overDue != null) {
			calOverDue = (this.overDue).divide(this.ccyMinorCcyUnits);
		}

		return calOverDue;
	}

	public BigDecimal getCcyMinorCcyUnits() {
		return ccyMinorCcyUnits;
	}

	public void setCcyMinorCcyUnits(BigDecimal ccyMinorCcyUnits) {
		this.ccyMinorCcyUnits = ccyMinorCcyUnits;
	}
}
