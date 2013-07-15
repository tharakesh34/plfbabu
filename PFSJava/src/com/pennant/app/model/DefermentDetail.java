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
 * FileName    		:  DefermentDetail.java                                              * 	  
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

public class DefermentDetail implements Serializable {
	
    private static final long serialVersionUID = -7292596404155418877L;

	private Date deferedSchdDate;
	private BigDecimal defSchdProfit = BigDecimal.ZERO;
	private BigDecimal defSchdPrincipal = BigDecimal.ZERO;
	private Date deferedRpyDate;
	private BigDecimal defRpySchdPft = BigDecimal.ZERO;
	private BigDecimal defRpySchdPri = BigDecimal.ZERO;
	private BigDecimal defRpySchdPftBal = BigDecimal.ZERO;
	private BigDecimal defRpySchdPriBal = BigDecimal.ZERO;
	private BigDecimal defPaidPftTillDate = BigDecimal.ZERO;
	private BigDecimal defPaidPriTillDate = BigDecimal.ZERO;
	private BigDecimal defPftBalance = BigDecimal.ZERO;
	private BigDecimal defPriBalance = BigDecimal.ZERO;

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

	public BigDecimal getDefPaidPftTillDate() {
		return defPaidPftTillDate;
	}
	public void setDefPaidPftTillDate(BigDecimal defPaidPftTillDate) {
		this.defPaidPftTillDate = defPaidPftTillDate;
	}

	public BigDecimal getDefPaidPriTillDate() {
		return defPaidPriTillDate;
	}
	public void setDefPaidPriTillDate(BigDecimal defPaidPriTillDate) {
		this.defPaidPriTillDate = defPaidPriTillDate;
	}

	public Date getDeferedRpyDate() {
		return deferedRpyDate;
	}
	public void setDeferedRpyDate(Date deferedRpyDate) {
		this.deferedRpyDate = DateUtility.getDate(DateUtility.formatUtilDate(deferedRpyDate,
		        PennantConstants.dateFormat));
	}

	public BigDecimal getDefRpySchdPft() {
		return defRpySchdPft;
	}
	public void setDefRpySchdPft(BigDecimal defRpySchdPft) {
		this.defRpySchdPft = defRpySchdPft;
	}

	public BigDecimal getDefRpySchdPri() {
		return defRpySchdPri;
	}
	public void setDefRpySchdPri(BigDecimal defRpySchdPri) {
		this.defRpySchdPri = defRpySchdPri;
	}

	public BigDecimal getDefPftBalance() {
		return defPftBalance;
	}
	public void setDefPftBalance(BigDecimal defPftBalance) {
		this.defPftBalance = defPftBalance;
	}

	public BigDecimal getDefPriBalance() {
		return defPriBalance;
	}
	public void setDefPriBalance(BigDecimal defPriBalance) {
		this.defPriBalance = defPriBalance;
	}

	public BigDecimal getDefRpySchdPftBal() {
		return defRpySchdPftBal;
	}
	public void setDefRpySchdPftBal(BigDecimal defRpySchdPftBal) {
		this.defRpySchdPftBal = defRpySchdPftBal;
	}

	public BigDecimal getDefRpySchdPriBal() {
		return defRpySchdPriBal;
	}
	public void setDefRpySchdPriBal(BigDecimal defRpySchdPriBal) {
		this.defRpySchdPriBal = defRpySchdPriBal;
	}

}
