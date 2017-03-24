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
 * FileName    		:  Academic.java                                                   * 	  
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

package com.pennant.backend.model.finance;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * Model class for the <b>Academic table</b>.<br>
 *
 */
public class RolledoverFinanceHeader implements java.io.Serializable  {

	private static final long serialVersionUID = -1472467289111692722L;
	
	private String finReference;
	private BigDecimal custPayment = BigDecimal.ZERO;
	private String paymentAccount;
	private BigDecimal latePayAmount = BigDecimal.ZERO;
	private BigDecimal latePayWaiverAmount = BigDecimal.ZERO;
	
	private List<RolledoverFinanceDetail> rolledoverFinanceDetails = new ArrayList<>(1);

	public RolledoverFinanceHeader() {
		
	}
	
	// ******************************************************//
	// ****************** getter / setter *******************//
	// ******************************************************//
		
	public String getFinReference() {
		return finReference;
	}
	public void setFinReference(String finReference) {
		this.finReference = finReference;
	}

	public BigDecimal getCustPayment() {
		return custPayment;
	}
	public void setCustPayment(BigDecimal custPayment) {
		this.custPayment = custPayment;
	}

	public String getPaymentAccount() {
		return paymentAccount;
	}
	public void setPaymentAccount(String paymentAccount) {
		this.paymentAccount = paymentAccount;
	}

	public List<RolledoverFinanceDetail> getRolledoverFinanceDetails() {
	    return rolledoverFinanceDetails;
    }
	public void setRolledoverFinanceDetails(List<RolledoverFinanceDetail> rolledoverFinanceDetails) {
	    this.rolledoverFinanceDetails = rolledoverFinanceDetails;
    }

	public BigDecimal getLatePayAmount() {
	    return latePayAmount;
    }
	public void setLatePayAmount(BigDecimal latePayAmount) {
	    this.latePayAmount = latePayAmount;
    }

	public BigDecimal getLatePayWaiverAmount() {
	    return latePayWaiverAmount;
    }
	public void setLatePayWaiverAmount(BigDecimal latePayWaiverAmount) {
	    this.latePayWaiverAmount = latePayWaiverAmount;
    }

}
