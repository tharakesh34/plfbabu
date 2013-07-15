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
 *
 * FileName    		:  RepayChanges.java													*                           
 *                                                                    
 * Author      		:  PENNANT TECHONOLOGIES												*
 *                                                                  
 * Creation Date    :  26-04-2011															*
 *                                                                  
 * Modified Date    :  30-07-2011															*
 *                                                                  
 * Description 		:												 						*                                 
 *                                                                                          
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 26-04-2011       Pennant	                 0.1                                            * 
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
package com.pennant.app.model;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

import com.pennant.app.util.DateUtility;
import com.pennant.backend.util.PennantConstants;

public class RepayChanges implements Serializable {

    private static final long serialVersionUID = -5377714898969654899L;
    
	private Date RepayDate;
	private BigDecimal RepayAmount = BigDecimal.ZERO;
	
	public RepayChanges(Date repayDate, BigDecimal repayAmount) {
		super();
		RepayDate = repayDate;
		RepayAmount = repayAmount;
	}
	
	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//
	// ++++++++++++++++++ getter / setter +++++++++++++++++++//
	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//
	
	public Date getRepayDate() {
		return RepayDate;
	}
	public void setRepayDate(Date repayDate) {
		RepayDate = DateUtility.getDate(DateUtility.formatUtilDate(repayDate,
				PennantConstants.dateFormat));
	}

	public BigDecimal getRepayAmount() {
		return RepayAmount;
	}
	public void setRepayAmount(BigDecimal repayAmount) {
		RepayAmount = repayAmount;
	}
	
}
