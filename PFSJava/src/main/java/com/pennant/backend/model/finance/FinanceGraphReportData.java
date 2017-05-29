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
 * FileName    		:  FinanceScheduleReportData.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  31-01-2012    														*
 *                                                                  						*
 * Modified Date    :  31-01-2012    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 31-01-2012       Pennant	                 0.1                                            * 
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

import java.io.Serializable;
import java.math.BigDecimal;

public class FinanceGraphReportData implements Serializable {
	
    private static final long serialVersionUID = -2686011251580058466L;
    
	private int recordNo;
	private String schDate;
	private BigDecimal profitBal;
	private BigDecimal principalBal;
	private BigDecimal financeBal;

	public FinanceGraphReportData() {
		super();
	}
	
	// ******************************************************//
	// ****************** getter / setter *******************//
	// ******************************************************//

	public int getRecordNo() {
    	return recordNo;
    }
	public void setRecordNo(int recordNo) {
    	this.recordNo = recordNo;
    }
	
	public String getSchDate() {
		return schDate;
	}
	public void setSchDate(String schDate) {
		this.schDate = schDate;
	}
	
	public BigDecimal getProfitBal() {
    	return profitBal;
    }
	public void setProfitBal(BigDecimal profitBal) {
    	this.profitBal = profitBal;
    }
	
	public BigDecimal getPrincipalBal() {
    	return principalBal;
    }
	public void setPrincipalBal(BigDecimal principalBal) {
    	this.principalBal = principalBal;
    }
	
	public BigDecimal getFinanceBal() {
    	return financeBal;
    }
	public void setFinanceBal(BigDecimal financeBal) {
    	this.financeBal = financeBal;
    }

	
}
