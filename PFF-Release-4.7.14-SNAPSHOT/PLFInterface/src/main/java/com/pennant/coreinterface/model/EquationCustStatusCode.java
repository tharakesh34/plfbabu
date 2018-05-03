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
 * FileName    		:  Currency.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  17-06-2013    														*
 *                                                                  						*
 * Modified Date    :  17-06-2013    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 17-06-2013       Pennant	                 0.1                                            * 
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
package com.pennant.coreinterface.model;

import com.pennanttech.pennapps.core.model.AbstractWorkflowEntity;

/**
 * Model class for the <b>Currency table</b>.<br>
 *
 */
public class EquationCustStatusCode extends AbstractWorkflowEntity {
	private static final long serialVersionUID = -978175709084193919L;

	public EquationCustStatusCode() {
		super();
	}
	
	private String custStsCode;
	private String custStsDescription;
	private int dueDays;
	private boolean suspendProfit;
	private boolean custStsIsActive;
	
	// ******************************************************//
	// ****************** getter / setter *******************//
	// ******************************************************//
	
	
	public String getCustStsCode() {
		return custStsCode;
	}
	public void setCustStsCode(String custStsCode) {
		this.custStsCode = custStsCode;
	}

	public String getCustStsDescription() {
		return custStsDescription;
	}
	public void setCustStsDescription(String custStsDescription) {
		this.custStsDescription = custStsDescription;
	}
	
	public int getDueDays() {
		return dueDays;
	}
	public void setDueDays(int dueDays) {
		this.dueDays = dueDays;
	}
	
	public boolean isSuspendProfit() {
		return suspendProfit;
	}
	public void setSuspendProfit(boolean suspendProfit) {
		this.suspendProfit = suspendProfit;
	}
	
	public boolean isCustStsIsActive() {
		return custStsIsActive;
	}
	public void setCustStsIsActive(boolean custStsIsActive) {
		this.custStsIsActive = custStsIsActive;
	}
}
