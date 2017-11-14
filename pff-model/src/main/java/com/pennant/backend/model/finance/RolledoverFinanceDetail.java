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
import java.util.Date;

/**
 * Model class for the <b>Academic table</b>.<br>
 *
 */
public class RolledoverFinanceDetail implements java.io.Serializable  {

	private static final long serialVersionUID = -1472467289111692722L;
	
	private String finReference;
	private String newFinReference;
	private BigDecimal rolloverAmount = BigDecimal.ZERO;
	private BigDecimal custPayment = BigDecimal.ZERO;
	private Date rolloverDate;
	
	//External Fields for display purpose
	private Date startDate;
	private BigDecimal finAmount = BigDecimal.ZERO;
	private BigDecimal totalProfit = BigDecimal.ZERO;
	private BigDecimal profitRate = BigDecimal.ZERO;
	private BigDecimal  TotalPftBal = BigDecimal.ZERO;
	private BigDecimal TotalPriBal = BigDecimal.ZERO;
	private String finPurpose = "";

	public RolledoverFinanceDetail() {
		
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

	public String getNewFinReference() {
		return newFinReference;
	}
	public void setNewFinReference(String newFinReference) {
		this.newFinReference = newFinReference;
	}

	public BigDecimal getRolloverAmount() {
		return rolloverAmount;
	}
	public void setRolloverAmount(BigDecimal rolloverAmount) {
		this.rolloverAmount = rolloverAmount;
	}

	public BigDecimal getCustPayment() {
		return custPayment;
	}
	public void setCustPayment(BigDecimal custPayment) {
		this.custPayment = custPayment;
	}

	public Date getRolloverDate() {
		return rolloverDate;
	}
	public void setRolloverDate(Date rolloverDate) {
		this.rolloverDate = rolloverDate;
	}

	public Date getStartDate() {
		return startDate;
	}
	public void setStartDate(Date startDate) {
		this.startDate = startDate;
	}

	public BigDecimal getFinAmount() {
		return finAmount;
	}
	public void setFinAmount(BigDecimal finAmount) {
		this.finAmount = finAmount;
	}

	public BigDecimal getTotalProfit() {
		return totalProfit;
	}
	public void setTotalProfit(BigDecimal totalProfit) {
		this.totalProfit = totalProfit;
	}

	public BigDecimal getProfitRate() {
		return profitRate;
	}
	public void setProfitRate(BigDecimal profitRate) {
		this.profitRate = profitRate;
	}

	public BigDecimal getTotalPftBal() {
	    return TotalPftBal;
    }

	public void setTotalPftBal(BigDecimal totalPftBal) {
	    TotalPftBal = totalPftBal;
    }

	public BigDecimal getTotalPriBal() {
	    return TotalPriBal;
    }

	public void setTotalPriBal(BigDecimal totalPriBal) {
	    TotalPriBal = totalPriBal;
    }

	public String getFinPurpose() {
	    return finPurpose;
    }

	public void setFinPurpose(String finPurpose) {
	    this.finPurpose = finPurpose;
    }

}
