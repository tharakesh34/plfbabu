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
 * FileName    		:  FinanceSummary.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  13-08-2012    														*
 *                                                                  						*
 * Modified Date    :  13-08-2012    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 16-03-2012       Pennant	                 0.1                                            * 
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
import java.util.Date;

public class FinanceSummary implements Serializable {
	
    private static final long serialVersionUID = 1854976637601258760L;
    
	private String finReference;
	private BigDecimal totalDisbursement = BigDecimal.ZERO;
	private BigDecimal totalPriSchd = BigDecimal.ZERO;
	private BigDecimal totalPftSchd = BigDecimal.ZERO;
	private BigDecimal principalSchd = BigDecimal.ZERO;
	private BigDecimal profitSchd = BigDecimal.ZERO;
	private BigDecimal schdPftPaid = BigDecimal.ZERO;
	private BigDecimal schdPriPaid = BigDecimal.ZERO;
	private BigDecimal totalDownPayment = BigDecimal.ZERO;
	private BigDecimal totalCpz = BigDecimal.ZERO;
	private Date nextSchDate;
	private Date schDate;
	
	//Posting Details
	private BigDecimal totalFees = BigDecimal.ZERO;
	private BigDecimal totalCharges = BigDecimal.ZERO;
	
	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//
	// ++++++++++++++++++ getter / setter +++++++++++++++++++//
	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//
	
	public String getFinReference() {
    	return finReference;
    }
	public void setFinReference(String finReference) {
    	this.finReference = finReference;
    }
	
	public BigDecimal getTotalDisbursement() {
    	return totalDisbursement;
    }
	public void setTotalDisbursement(BigDecimal totalDisbursement) {
    	this.totalDisbursement = totalDisbursement;
    }
	
	public BigDecimal getTotalPriSchd() {
    	return totalPriSchd;
    }
	public void setTotalPriSchd(BigDecimal totalPriSchd) {
    	this.totalPriSchd = totalPriSchd;
    }
	
	public BigDecimal getTotalPftSchd() {
    	return totalPftSchd;
    }
	public void setTotalPftSchd(BigDecimal totalPftSchd) {
    	this.totalPftSchd = totalPftSchd;
    }
	
	public BigDecimal getPrincipalSchd() {
    	return principalSchd;
    }
	public void setPrincipalSchd(BigDecimal principalSchd) {
    	this.principalSchd = principalSchd;
    }
	
	public BigDecimal getProfitSchd() {
    	return profitSchd;
    }
	public void setProfitSchd(BigDecimal profitSchd) {
    	this.profitSchd = profitSchd;
    }
	
	public BigDecimal getSchdPftPaid() {
    	return schdPftPaid;
    }
	public void setSchdPftPaid(BigDecimal schdPftPaid) {
    	this.schdPftPaid = schdPftPaid;
    }
	
	public BigDecimal getSchdPriPaid() {
    	return schdPriPaid;
    }
	public void setSchdPriPaid(BigDecimal schdPriPaid) {
    	this.schdPriPaid = schdPriPaid;
    }
	
	public BigDecimal getTotalDownPayment() {
    	return totalDownPayment;
    }
	public void setTotalDownPayment(BigDecimal totalDownPayment) {
    	this.totalDownPayment = totalDownPayment;
    }
	
	public BigDecimal getTotalCpz() {
    	return totalCpz;
    }
	public void setTotalCpz(BigDecimal totalCpz) {
    	this.totalCpz = totalCpz;
    }
	
	public Date getNextSchDate() {
    	return nextSchDate;
    }
	public void setNextSchDate(Date nextSchDate) {
    	this.nextSchDate = nextSchDate;
    }
	
	public Date getSchDate() {
    	return schDate;
    }
	public void setSchDate(Date schDate) {
    	this.schDate = schDate;
    }
	
	public BigDecimal getTotalFees() {
    	return totalFees;
    }
	public void setTotalFees(BigDecimal totalFees) {
    	this.totalFees = totalFees;
    }
	
	public BigDecimal getTotalCharges() {
    	return totalCharges;
    }
	public void setTotalCharges(BigDecimal totalCharges) {
    	this.totalCharges = totalCharges;
    }

}
