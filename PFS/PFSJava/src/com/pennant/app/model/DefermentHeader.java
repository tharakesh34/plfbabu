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
 * FileName    		:  DefermentHeader.java                                              * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  10-10-2011    														*
 *                                                                  						*
 * Modified Date    :  00-00-0000    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 12-08-2011       Pennant	                 0.1                                            * 
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

public class DefermentHeader implements Serializable {

    private static final long serialVersionUID = 7588427792693461577L;
    
	private Date deferedSchdDate;
	private BigDecimal defSchdProfit =  BigDecimal.ZERO;;
	private BigDecimal defSchdPrincipal =  BigDecimal.ZERO;;
	private String defRecalType = "";
	private Date defTillDate = null;
	
	public DefermentHeader() {
		super();
	}

	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//
	// ++++++++++++++++++ getter / setter +++++++++++++++++++//
	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//

	public Date getDeferedSchdDate() {
		return deferedSchdDate;
	}
	public void setDeferedSchdDate(Date deferedSchdDate) {
		this.deferedSchdDate = DateUtility.getDate(DateUtility.formatUtilDate(deferedSchdDate,
		        PennantConstants.dateFormat));
	}

	public BigDecimal getDefSchdProfit() {
		return defSchdProfit;
	}
	public void setDefSchdProfit(BigDecimal defSchdProfit) {
		this.defSchdProfit = defSchdProfit;
	}

	public BigDecimal getDefSchdPrincipal() {
		return defSchdPrincipal;
	}
	public void setDefSchdPrincipal(BigDecimal defSchdPrincipal) {
		this.defSchdPrincipal = defSchdPrincipal;
	}

	public String getDefRecalType() {
		return defRecalType;
	}
	public void setDefRecalType(String defRecalType) {
		this.defRecalType = defRecalType;
	}

	public Date getDefTillDate() {
		return defTillDate;
	}
	public void setDefTillDate(Date defTillDate) {
		this.defTillDate = defTillDate;
	}

}
