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
import java.util.HashSet;
import java.util.Set;

/**
 * Model class for the <b>FinanceMain table</b>.<br>
 * 
 */
public class FinanceEnquiry implements java.io.Serializable {

	private static final long	serialVersionUID	= -7702107666101609103L;
	private String				finReference		= null;
	private String				finStatus;
	private boolean				finIsActive;
	private boolean				blacklisted;
	private String				finBranch			= null;
	private String				lovDescFinBranchName;
	private String				finType;
	private String				lovDescFinTypeName;
	private String				lovDescProductCodeName;
	private String				finCcy;
	private String				scheduleMethod;
	private String				profitDaysBasis;
	private Date				finStartDate;
	private int					numberOfTerms		= 0;
	private long				custID;
	private String				lovDescCustCIF;
	private String				lovDescCustShrtName;
	private BigDecimal			finAmount			= BigDecimal.ZERO;
	private BigDecimal			finCurrAssetValue	= BigDecimal.ZERO;
	private BigDecimal			feeChargeAmt		= BigDecimal.ZERO;
	private BigDecimal			insuranceAmt		= BigDecimal.ZERO;
	private BigDecimal			downPayment			= BigDecimal.ZERO;
	private BigDecimal			finRepaymentAmount	= BigDecimal.ZERO;
	private Date				grcPeriodEndDate;
	private Date				maturityDate;
	private String				closingStatus;
	private String				custTypeCtg;
	private BigDecimal			nextDueAmount		= BigDecimal.ZERO;
	private Date				nextDueDate;
	private long				mandateID;
	private String				finRepayMethod;
	private Date				latestRpyDate;
	private BigDecimal			currentBalance;
	private BigDecimal			amountOverdue;
	private BigDecimal			finAssetValue;
	private int					odDays;
	private BigDecimal			collateralValue;
	private String				collateralType;
	private BigDecimal			repayProfitRate;
	private BigDecimal			firstRepay			= BigDecimal.ZERO;
	private BigDecimal			writtenOffAmount	= BigDecimal.ZERO;
	private BigDecimal			writtenOffPrincipal	= BigDecimal.ZERO;
	private BigDecimal			settlementAmount	= BigDecimal.ZERO;
	private BigDecimal			paymentAmount	= BigDecimal.ZERO;
	private String				repayFrq;
	private String				ownership;
	private int					NOInst				= 0;

	public FinanceEnquiry() {

	}

	public Set<String> getExcludeFields() {
		Set<String> excludeFields = new HashSet<String>();
		excludeFields.add("latestRpyDate");
		excludeFields.add("currentBalance");
		excludeFields.add("amountOverdue");
		excludeFields.add("finAssetValue");
		excludeFields.add("odDays");
		excludeFields.add("collateralValue");
		excludeFields.add("collateralType");
		excludeFields.add("repayProfitRate");
		excludeFields.add("firstRepay");
		excludeFields.add("writtenOffAmount");
		excludeFields.add("writtenOffPrincipal");
		excludeFields.add("settlementAmount");
		excludeFields.add("paymentAmount");
		excludeFields.add("repayFrq");
		excludeFields.add("ownership");
		excludeFields.add("NOInst");
		return excludeFields;
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

	public String getScheduleMethod() {
		return scheduleMethod;
	}

	public void setScheduleMethod(String scheduleMethod) {
		this.scheduleMethod = scheduleMethod;
	}

	public String getProfitDaysBasis() {
		return profitDaysBasis;
	}

	public void setProfitDaysBasis(String profitDaysBasis) {
		this.profitDaysBasis = profitDaysBasis;
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

	public BigDecimal getDownPayment() {
		return downPayment;
	}

	public void setDownPayment(BigDecimal downPayment) {
		this.downPayment = downPayment;
	}

	public BigDecimal getNextDueAmount() {
		return nextDueAmount;
	}

	public void setNextDueAmount(BigDecimal nextDueAmount) {
		this.nextDueAmount = nextDueAmount;
	}

	public Date getNextDueDate() {
		return nextDueDate;
	}

	public void setNextDueDate(Date nextDueDate) {
		this.nextDueDate = nextDueDate;
	}

	public long getMandateID() {
		return mandateID;
	}

	public void setMandateID(long mandateID) {
		this.mandateID = mandateID;
	}

	public String getFinRepayMethod() {
		return finRepayMethod;
	}

	public void setFinRepayMethod(String finRepayMethod) {
		this.finRepayMethod = finRepayMethod;
	}

	public BigDecimal getInsuranceAmt() {
		return insuranceAmt;
	}

	public void setInsuranceAmt(BigDecimal insuranceAmt) {
		this.insuranceAmt = insuranceAmt;
	}

	public Date getLatestRpyDate() {
		return latestRpyDate;
	}

	public void setLatestRpyDate(Date latestRpyDate) {
		this.latestRpyDate = latestRpyDate;
	}

	public BigDecimal getCurrentBalance() {
		return currentBalance;
	}

	public void setCurrentBalance(BigDecimal currentBalance) {
		this.currentBalance = currentBalance;
	}

	public BigDecimal getAmountOverdue() {
		return amountOverdue;
	}

	public void setAmountOverdue(BigDecimal amountOverdue) {
		this.amountOverdue = amountOverdue;
	}

	public BigDecimal getFinAssetValue() {
		return finAssetValue;
	}

	public void setFinAssetValue(BigDecimal finAssetValue) {
		this.finAssetValue = finAssetValue;
	}

	public int getOdDays() {
		return odDays;
	}

	public void setOdDays(int odDays) {
		this.odDays = odDays;
	}

	public BigDecimal getCollateralValue() {
		return collateralValue;
	}

	public void setCollateralValue(BigDecimal collateralValue) {
		this.collateralValue = collateralValue;
	}

	public String getCollateralType() {
		return collateralType;
	}

	public void setCollateralType(String collateralType) {
		this.collateralType = collateralType;
	}

	public BigDecimal getRepayProfitRate() {
		return repayProfitRate;
	}

	public void setRepayProfitRate(BigDecimal repayProfitRate) {
		this.repayProfitRate = repayProfitRate;
	}

	public BigDecimal getFirstRepay() {
		return firstRepay;
	}

	public void setFirstRepay(BigDecimal firstRepay) {
		this.firstRepay = firstRepay;
	}

	public BigDecimal getWrittenOffAmount() {
		return writtenOffAmount;
	}

	public void setWrittenOffAmount(BigDecimal writtenOffAmount) {
		this.writtenOffAmount = writtenOffAmount;
	}

	public BigDecimal getWrittenOffPrincipal() {
		return writtenOffPrincipal;
	}

	public void setWrittenOffPrincipal(BigDecimal writtenOffPrincipal) {
		this.writtenOffPrincipal = writtenOffPrincipal;
	}

	public BigDecimal getSettlementAmount() {
		return settlementAmount;
	}

	public void setSettlementAmount(BigDecimal settlementAmount) {
		this.settlementAmount = settlementAmount;
	}
	
	public BigDecimal getPaymentAmount() {
		return paymentAmount;
	}

	public void setPaymentAmount(BigDecimal paymentAmount) {
		this.paymentAmount = paymentAmount;
	}

	public String getRepayFrq() {
		return repayFrq;
	}

	public void setRepayFrq(String repayFrq) {
		this.repayFrq = repayFrq;
	}

	public String getOwnership() {
		return ownership;
	}

	public void setOwnership(String ownership) {
		this.ownership = ownership;
	}

	public BigDecimal getFinCurrAssetValue() {
		return finCurrAssetValue;
	}

	public void setFinCurrAssetValue(BigDecimal finCurrAssetValue) {
		this.finCurrAssetValue = finCurrAssetValue;
	}

	public int getNOInst() {
		return NOInst;
	}

	public void setNOInst(int nOInst) {
		NOInst = nOInst;
	}
}
