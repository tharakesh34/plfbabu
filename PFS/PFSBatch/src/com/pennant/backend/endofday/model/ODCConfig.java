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
 * FileName    		:  ODCConfig.java													*                           
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
package com.pennant.backend.endofday.model;

import java.io.Serializable;
import java.math.BigDecimal;

public class ODCConfig implements Serializable{

	private static final long serialVersionUID = 7118064594415311302L;
	
	private String FinType;
	private long accountSetID;
	private String accountSetCode;
	private String oDCPLAccount;
	private String oDCPLSubHead;
	private String oDCCharityAccount;
	private String oDCCharitySubHead;
	private BigDecimal oDCPLShare = BigDecimal.ZERO;
	private String oDCCustCtg;
	private String oDCType;
	private String oDCOn;
	private BigDecimal oDCAmount = BigDecimal.ZERO;
	private int oDCGraceDays;
	private boolean oDCAllowWaiver;
	private BigDecimal oDCMaxWaiver = BigDecimal.ZERO;
	private boolean oDCSweepCharges;
	
	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//
	// ++++++++++++++++++ getter / setter +++++++++++++++++++//
	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//
	
	public String getFinType() {
		return FinType;
	}
	public void setFinType(String finType) {
		FinType = finType;
	}
	
	public long getAccountSetID() {
		return accountSetID;
	}
	public void setAccountSetID(long accountSetID) {
		this.accountSetID = accountSetID;
	}
	
	public String getAccountSetCode() {
		return accountSetCode;
	}
	public void setAccountSetCode(String accountSetCode) {
		this.accountSetCode = accountSetCode;
	}
	
	public String getODCPLAccount() {
		return oDCPLAccount;
	}
	public void setODCPLAccount(String oDCPLAccount) {
		this.oDCPLAccount = oDCPLAccount;
	}
	
	public String getODCPLSubHead() {
		return oDCPLSubHead;
	}
	public void setODCPLSubHead(String oDCPLSubHead) {
		this.oDCPLSubHead = oDCPLSubHead;
	}
	
	public String getODCCharityAccount() {
		return oDCCharityAccount;
	}
	public void setODCCharityAccount(String oDCCharityAccount) {
		this.oDCCharityAccount = oDCCharityAccount;
	}
	
	public String getODCCharitySubHead() {
		return oDCCharitySubHead;
	}
	public void setODCCharitySubHead(String oDCCharitySubHead) {
		this.oDCCharitySubHead = oDCCharitySubHead;
	}
	
	public BigDecimal getODCPLShare() {
		return oDCPLShare;
	}
	public void setODCPLShare(BigDecimal oDCPLShare) {
		this.oDCPLShare = oDCPLShare;
	}
	
	public String getODCCustCtg() {
		return oDCCustCtg;
	}
	public void setODCCustCtg(String oDCCustCtg) {
		this.oDCCustCtg = oDCCustCtg;
	}
	
	public String getODCType() {
		return oDCType;
	}
	public void setODCType(String oDCType) {
		this.oDCType = oDCType;
	}
	
	public String getODCOn() {
		return oDCOn;
	}
	public void setODCOn(String oDCOn) {
		this.oDCOn = oDCOn;
	}
	
	public BigDecimal getODCAmount() {
		return oDCAmount;
	}
	public void setODCAmount(BigDecimal oDCAmount) {
		this.oDCAmount = oDCAmount;
	}
	
	public int getODCGraceDays() {
		return oDCGraceDays;
	}
	public void setODCGraceDays(int oDCGraceDays) {
		this.oDCGraceDays = oDCGraceDays;
	}
	
	public boolean isODCAllowWaiver() {
		return oDCAllowWaiver;
	}
	public void setODCAllowWaiver(boolean oDCAllowWaiver) {
		this.oDCAllowWaiver = oDCAllowWaiver;
	}
	
	public BigDecimal getODCMaxWaiver() {
		return oDCMaxWaiver;
	}
	public void setODCMaxWaiver(BigDecimal oDCMaxWaiver) {
		this.oDCMaxWaiver = oDCMaxWaiver;
	}
	
	public boolean isODCSweepCharges() {
		return oDCSweepCharges;
	}
	public void setODCSweepCharges(boolean oDCSweepCharges) {
		this.oDCSweepCharges = oDCSweepCharges;
	}
	
}
