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
 * FileName    		:  Accounts.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  02-01-2012    														*
 *                                                                  						*
 * Modified Date    :  02-01-2012    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 02-01-2012       Pennant	                 0.1                                            * 
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
package com.pennant.backend.model.accounts;

import java.math.BigDecimal;
import java.util.Date;

import com.pennanttech.pennapps.core.model.AbstractWorkflowEntity;

/**
 * Model class for the <b>Accounts table</b>.<br>
 * 
 */
public class AccountsHistory extends AbstractWorkflowEntity {

	private static final long	serialVersionUID	= 1750261249341373068L;
	private String accountId;
	private Date postDate;
	private BigDecimal todayDebits = BigDecimal.ZERO;
	private BigDecimal todayCredits = BigDecimal.ZERO;
	private BigDecimal todayNet = BigDecimal.ZERO;
	private BigDecimal shadowBal = BigDecimal.ZERO;
	private BigDecimal acBalance = BigDecimal.ZERO;
	private boolean newRecord = false;


	public AccountsHistory() {
		super();
	}

	public boolean isNew() {
		return isNewRecord();
	}

	public AccountsHistory(String accountId) {
		super();
		this.accountId = accountId;
	}

	public String getAccountId() {
		return accountId;
	}

	public void setAccountId(String accountId) {
		this.accountId = accountId;
	}

	public boolean isNewRecord() {
		return newRecord;
	}

	public void setNewRecord(boolean newRecord) {
		this.newRecord = newRecord;
	}

	public Date getPostDate() {
		return postDate;
	}

	public void setPostDate(Date postDate) {
		this.postDate = postDate;
	}

	public BigDecimal getTodayDebits() {
		return todayDebits;
	}

	public void setTodayDebits(BigDecimal todayDebits) {
		this.todayDebits = todayDebits;
	}

	public BigDecimal getTodayCredits() {
		return todayCredits;
	}

	public void setTodayCredits(BigDecimal todayCredits) {
		this.todayCredits = todayCredits;
	}

	public BigDecimal getTodayNet() {
		return todayNet;
	}

	public void setTodayNet(BigDecimal todayNet) {
		this.todayNet = todayNet;
	}

	public BigDecimal getShadowBal() {
		return shadowBal;
	}

	public void setShadowBal(BigDecimal shadowBal) {
		this.shadowBal = shadowBal;
	}

	public BigDecimal getAcBalance() {
		return acBalance;
	}

	public void setAcBalance(BigDecimal acBalance) {
		this.acBalance = acBalance;
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	}
	
}
