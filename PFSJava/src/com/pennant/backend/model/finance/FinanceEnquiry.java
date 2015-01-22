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
 * FileName    		:  FinanceEnquiry.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  16-03-2012    														*
 *                                                                  						*
 * Modified Date    :  16-03-2012    														*
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

import java.math.BigDecimal;
import java.util.Date;

/**
 * Model class for the <b>FinanceMain table</b>.<br>
 * 
 */
public class FinanceEnquiry implements java.io.Serializable {

    private static final long serialVersionUID = -7702107666101609103L;
    
	private String finReference = null;
	private String finStatus;
	private boolean finIsActive;
	private boolean blacklisted;
	private String finBranch = null;
	private String lovDescFinBranchName;
	private String finType;
	private String lovDescFinTypeName;
	private String lovDescProductCodeName;
	private String finCcy;
	private String lovDescFinCcyName;
	private String scheduleMethod;
	private String lovDescScheduleMethodName;
	private String profitDaysBasis;
	private String lovDescProfitDaysBasisName;
	private Date finStartDate;
	private int numberOfTerms = 0;
	private long custID;
	private String lovDescCustCIF;
	private String lovDescCustShrtName;
	private BigDecimal finAmount =BigDecimal.ZERO;
	private BigDecimal feeChargeAmt =BigDecimal.ZERO;
	private BigDecimal downPayment =BigDecimal.ZERO;
	private BigDecimal finRepaymentAmount =BigDecimal.ZERO;
	private Date grcPeriodEndDate;
	private Date maturityDate;
	private int lovDescFinFormatter;
	private BigDecimal currentFinAmount =BigDecimal.ZERO;
	private int noOfDays;
	private Date nextDueDate;
	private BigDecimal nextDueAmount =BigDecimal.ZERO;
	private String closingStatus;
	private String custTypeCtg;
	private String assetCode;
	
	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//
	// ++++++++++++++++++ getter / setter +++++++++++++++++++//
	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//
	
	public String getFinReference() {
    	return finReference;
    }
	public void setFinReference(String finReference) {
    	this.finReference = finReference;
    }
	
	public String getFinStatus() {
    	return finStatus;
    }
	public void setFinStatus(String finStatus) {
    	this.finStatus = finStatus;
    }
	
	public void setFinIsActive(boolean finIsActive) {
	    this.finIsActive = finIsActive;
    }
	public boolean isFinIsActive() {
	    return finIsActive;
    }
	public void setBlacklisted(boolean blacklisted) {
	    this.blacklisted = blacklisted;
    }
	public boolean isBlacklisted() {
	    return blacklisted;
    }
	public String getFinBranch() {
    	return finBranch;
    }
	public void setFinBranch(String finBranch) {
    	this.finBranch = finBranch;
    }
	
	public String getLovDescFinBranchName() {
    	return lovDescFinBranchName;
    }
	public void setLovDescFinBranchName(String lovDescFinBranchName) {
    	this.lovDescFinBranchName = lovDescFinBranchName;
    }
	
	public String getFinType() {
    	return finType;
    }
	public void setFinType(String finType) {
    	this.finType = finType;
    }
	
	public String getLovDescFinTypeName() {
    	return lovDescFinTypeName;
    }
	public void setLovDescFinTypeName(String lovDescFinTypeName) {
    	this.lovDescFinTypeName = lovDescFinTypeName;
    }
	
	public String getLovDescProductCodeName() {
    	return lovDescProductCodeName;
    }
	public void setLovDescProductCodeName(String lovDescProductCodeName) {
    	this.lovDescProductCodeName = lovDescProductCodeName;
    }
	
	public String getFinCcy() {
    	return finCcy;
    }
	public void setFinCcy(String finCcy) {
    	this.finCcy = finCcy;
    }
	
	public String getLovDescFinCcyName() {
    	return lovDescFinCcyName;
    }
	public void setLovDescFinCcyName(String lovDescFinCcyName) {
    	this.lovDescFinCcyName = lovDescFinCcyName;
    }
	
	public String getScheduleMethod() {
    	return scheduleMethod;
    }
	public void setScheduleMethod(String scheduleMethod) {
    	this.scheduleMethod = scheduleMethod;
    }
	
	public String getLovDescScheduleMethodName() {
    	return lovDescScheduleMethodName;
    }
	public void setLovDescScheduleMethodName(String lovDescScheduleMethodName) {
    	this.lovDescScheduleMethodName = lovDescScheduleMethodName;
    }
	
	public String getProfitDaysBasis() {
    	return profitDaysBasis;
    }
	public void setProfitDaysBasis(String profitDaysBasis) {
    	this.profitDaysBasis = profitDaysBasis;
    }
	
	public String getLovDescProfitDaysBasisName() {
    	return lovDescProfitDaysBasisName;
    }
	public void setLovDescProfitDaysBasisName(String lovDescProfitDaysBasisName) {
    	this.lovDescProfitDaysBasisName = lovDescProfitDaysBasisName;
    }
	
	public Date getFinStartDate() {
    	return finStartDate;
    }
	public void setFinStartDate(Date finStartDate) {
    	this.finStartDate = finStartDate;
    }
	
	public int getNumberOfTerms() {
    	return numberOfTerms;
    }
	public void setNumberOfTerms(int numberOfTerms) {
    	this.numberOfTerms = numberOfTerms;
    }
	
	public long getCustID() {
    	return custID;
    }
	public void setCustID(long custID) {
    	this.custID = custID;
    }
	
	public String getLovDescCustCIF() {
    	return lovDescCustCIF;
    }
	public void setLovDescCustCIF(String lovDescCustCIF) {
    	this.lovDescCustCIF = lovDescCustCIF;
    }
	
	public String getLovDescCustShrtName() {
    	return lovDescCustShrtName;
    }
	public void setLovDescCustShrtName(String lovDescCustShrtName) {
    	this.lovDescCustShrtName = lovDescCustShrtName;
    }
	
	public BigDecimal getFinAmount() {
    	return finAmount;
    }
	public void setFinAmount(BigDecimal finAmount) {
    	this.finAmount = finAmount;
    }
	
	public BigDecimal getFeeChargeAmt() {
    	return feeChargeAmt;
    }
	public void setFeeChargeAmt(BigDecimal feeChargeAmt) {
    	this.feeChargeAmt = feeChargeAmt;
    }
	
	public BigDecimal getFinRepaymentAmount() {
    	return finRepaymentAmount;
    }
	public void setFinRepaymentAmount(BigDecimal finRepaymentAmount) {
    	this.finRepaymentAmount = finRepaymentAmount;
    }
	
	public Date getGrcPeriodEndDate() {
    	return grcPeriodEndDate;
    }
	public void setGrcPeriodEndDate(Date grcPeriodEndDate) {
    	this.grcPeriodEndDate = grcPeriodEndDate;
    }
	
	public Date getMaturityDate() {
    	return maturityDate;
    }
	public void setMaturityDate(Date maturityDate) {
    	this.maturityDate = maturityDate;
    }
	
	public int getLovDescFinFormatter() {
    	return lovDescFinFormatter;
    }
	public void setLovDescFinFormatter(int lovDescFinFormatter) {
    	this.lovDescFinFormatter = lovDescFinFormatter;
    }
	
	public BigDecimal getCurrentFinAmount() {
    	return currentFinAmount;
    }
	public void setCurrentFinAmount(BigDecimal currentFinAmount) {
    	this.currentFinAmount = currentFinAmount;
    }
	
	public int getNoOfDays() {
    	return noOfDays;
    }
	public void setNoOfDays(int noOfDays) {
    	this.noOfDays = noOfDays;
    }
	
	public Date getNextDueDate() {
    	return nextDueDate;
    }
	public void setNextDueDate(Date nextDueDate) {
    	this.nextDueDate = nextDueDate;
    }
	
	public BigDecimal getNextDueAmount() {
    	return nextDueAmount;
    }
	public void setNextDueAmount(BigDecimal nextDueAmount) {
    	this.nextDueAmount = nextDueAmount;
    }
	
	public void setClosingStatus(String closingStatus) {
	    this.closingStatus = closingStatus;
    }
	public String getClosingStatus() {
	    return closingStatus;
    }
	
	public String getCustTypeCtg() {
    	return custTypeCtg;
    }
	public void setCustTypeCtg(String custTypeCtg) {
    	this.custTypeCtg = custTypeCtg;
    }
	public void setAssetCode(String assetCode) {
	    this.assetCode = assetCode;
    }
	public String getAssetCode() {
	    return assetCode;
    }
	public BigDecimal getDownPayment() {
	    return downPayment;
    }
	public void setDownPayment(BigDecimal downPayment) {
	    this.downPayment = downPayment;
    }
	
	
	
}
