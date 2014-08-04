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
	
	private int finCurODDays = 0;
	private BigDecimal finODTotPenaltyAmt = BigDecimal.ZERO;
	private BigDecimal finODTotWaived = BigDecimal.ZERO;
	private BigDecimal finODTotPenaltyPaid = BigDecimal.ZERO;
	private BigDecimal finODTotPenaltyBal = BigDecimal.ZERO;
	
	private Date nextSchDate;
	private Date schDate;
	
	//Posting Details
	private BigDecimal totalFees = BigDecimal.ZERO;
	private BigDecimal totalCharges = BigDecimal.ZERO;
	
	// Finance Profit Enquiry
	private String finType;
	private String finBranch = null;
	private String finCcy;
	private long custID;
	private Date finStartDate;
	private Date maturityDate;
	private String finStatus;
	private BigDecimal finRate = BigDecimal.ZERO;
	private Date finLastRepayDate;
	private BigDecimal outStandPrincipal = BigDecimal.ZERO;
	private BigDecimal outStandProfit = BigDecimal.ZERO;
	private BigDecimal totalOutStanding = BigDecimal.ZERO;
	
	private BigDecimal totalOriginal = BigDecimal.ZERO;
	private BigDecimal totalPaid = BigDecimal.ZERO;
	
	private BigDecimal unPaidPrincipal = BigDecimal.ZERO;
	private BigDecimal unPaidProfit = BigDecimal.ZERO;
	private BigDecimal totalUnPaid = BigDecimal.ZERO;
	
	private BigDecimal overDuePrincipal = BigDecimal.ZERO;
	private BigDecimal overDueProfit = BigDecimal.ZERO;
	private BigDecimal totalOverDue = BigDecimal.ZERO;
	
	private BigDecimal earnedPrincipal = BigDecimal.ZERO;
	private BigDecimal earnedProfit = BigDecimal.ZERO;
	private BigDecimal totalEarned = BigDecimal.ZERO;
	
	private BigDecimal unEarnedPrincipal = BigDecimal.ZERO;
	private BigDecimal unEarnedProfit = BigDecimal.ZERO;
	private BigDecimal totalUnEarned = BigDecimal.ZERO;
	
	private BigDecimal payOffPrincipal = BigDecimal.ZERO;
	private BigDecimal payOffProfit = BigDecimal.ZERO;
	private BigDecimal totalPayOff = BigDecimal.ZERO;
	
	private long overDueInstlments;
	private BigDecimal overDueInstlementPft = BigDecimal.ZERO;
	private long paidInstlments;
	private BigDecimal paidInstlementPft = BigDecimal.ZERO;
	private long unPaidInstlments;
	private BigDecimal unPaidInstlementPft = BigDecimal.ZERO;
	private long numberOfTerms;
	
	private int ccyEditField;
	
	private String lovDescFinBranchName;
	private String lovDescFinTypeName;
	private String lovDescFinCcyName;
	private String lovDescCustShrtName;
	private BigDecimal totalRepayAmt;
	
	//
	private String finCommitmentRef;
	private String cmtTitle;
	private BigDecimal cmtAmount = BigDecimal.ZERO;
	private BigDecimal cmtAvailable = BigDecimal.ZERO;
	private Date CmtExpiryDate;
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
	public void setFinCurODDays(int finCurODDays) {
	    this.finCurODDays = finCurODDays;
    }
	public int getFinCurODDays() {
	    return finCurODDays;
    }
	public void setFinODTotPenaltyAmt(BigDecimal finODTotPenaltyAmt) {
	    this.finODTotPenaltyAmt = finODTotPenaltyAmt;
    }
	public BigDecimal getFinODTotPenaltyAmt() {
	    return finODTotPenaltyAmt;
    }
	public void setFinODTotWaived(BigDecimal finODTotWaived) {
	    this.finODTotWaived = finODTotWaived;
    }
	public BigDecimal getFinODTotWaived() {
	    return finODTotWaived;
    }
	public void setFinODTotPenaltyPaid(BigDecimal finODTotPenaltyPaid) {
	    this.finODTotPenaltyPaid = finODTotPenaltyPaid;
    }
	public BigDecimal getFinODTotPenaltyPaid() {
	    return finODTotPenaltyPaid;
    }
	public void setFinODTotPenaltyBal(BigDecimal finODTotPenaltyBal) {
	    this.finODTotPenaltyBal = finODTotPenaltyBal;
    }
	public BigDecimal getFinODTotPenaltyBal() {
	    return finODTotPenaltyBal;
    }
	public String getFinType() {
    	return finType;
    }
	public void setFinType(String finType) {
    	this.finType = finType;
    }
	public String getFinBranch() {
    	return finBranch;
    }
	public void setFinBranch(String finBranch) {
    	this.finBranch = finBranch;
    }
	public String getFinCcy() {
    	return finCcy;
    }
	public void setFinCcy(String finCcy) {
    	this.finCcy = finCcy;
    }
	public long getCustID() {
    	return custID;
    }
	public void setCustID(long custID) {
    	this.custID = custID;
    }
	public Date getFinStartDate() {
    	return finStartDate;
    }
	public void setFinStartDate(Date finStartDate) {
    	this.finStartDate = finStartDate;
    }
	public Date getMaturityDate() {
    	return maturityDate;
    }
	public void setMaturityDate(Date maturityDate) {
    	this.maturityDate = maturityDate;
    }
	public String getFinStatus() {
    	return finStatus;
    }
	public void setFinStatus(String finStatus) {
    	this.finStatus = finStatus;
    }
	public BigDecimal getFinRate() {
    	return finRate;
    }
	public void setFinRate(BigDecimal finRate) {
    	this.finRate = finRate;
    }
	public Date getFinLastRepayDate() {
    	return finLastRepayDate;
    }
	public void setFinLastRepayDate(Date finLastRepayDate) {
    	this.finLastRepayDate = finLastRepayDate;
    }
	public BigDecimal getOutStandPrincipal() {
    	return outStandPrincipal;
    }
	public void setOutStandPrincipal(BigDecimal outStandPrincipal) {
    	this.outStandPrincipal = outStandPrincipal;
    }
	public BigDecimal getOutStandProfit() {
    	return outStandProfit;
    }
	public void setOutStandProfit(BigDecimal outStandProfit) {
    	this.outStandProfit = outStandProfit;
    }
	public BigDecimal getTotalOutStanding() {
    	return totalOutStanding;
    }
	public void setTotalOutStanding(BigDecimal totalOutStanding) {
    	this.totalOutStanding = totalOutStanding;
    }
	public BigDecimal getTotalOriginal() {
    	return totalOriginal;
    }
	public void setTotalOriginal(BigDecimal totalOriginal) {
    	this.totalOriginal = totalOriginal;
    }

	public BigDecimal getTotalPaid() {
    	return totalPaid;
    }
	public void setTotalPaid(BigDecimal totalPaid) {
    	this.totalPaid = totalPaid;
    }
	public BigDecimal getUnPaidPrincipal() {
    	return unPaidPrincipal;
    }
	public void setUnPaidPrincipal(BigDecimal unPaidPrincipal) {
    	this.unPaidPrincipal = unPaidPrincipal;
    }
	public BigDecimal getUnPaidProfit() {
    	return unPaidProfit;
    }
	public void setUnPaidProfit(BigDecimal unPaidProfit) {
    	this.unPaidProfit = unPaidProfit;
    }
	public BigDecimal getTotalUnPaid() {
    	return totalUnPaid;
    }
	public void setTotalUnPaid(BigDecimal totalUnPaid) {
    	this.totalUnPaid = totalUnPaid;
    }
	public BigDecimal getOverDuePrincipal() {
    	return overDuePrincipal;
    }
	public void setOverDuePrincipal(BigDecimal overDuePrincipal) {
    	this.overDuePrincipal = overDuePrincipal;
    }
	public BigDecimal getOverDueProfit() {
    	return overDueProfit;
    }
	public void setOverDueProfit(BigDecimal overDueProfit) {
    	this.overDueProfit = overDueProfit;
    }
	public BigDecimal getTotalOverDue() {
    	return totalOverDue;
    }
	public void setTotalOverDue(BigDecimal totalOverDue) {
    	this.totalOverDue = totalOverDue;
    }
	public BigDecimal getEarnedPrincipal() {
    	return earnedPrincipal;
    }
	public void setEarnedPrincipal(BigDecimal earnedPrincipal) {
    	this.earnedPrincipal = earnedPrincipal;
    }
	public BigDecimal getEarnedProfit() {
    	return earnedProfit;
    }
	public void setEarnedProfit(BigDecimal earnedProfit) {
    	this.earnedProfit = earnedProfit;
    }
	public BigDecimal getTotalEarned() {
    	return totalEarned;
    }
	public void setTotalEarned(BigDecimal totalEarned) {
    	this.totalEarned = totalEarned;
    }
	public BigDecimal getUnEarnedPrincipal() {
    	return unEarnedPrincipal;
    }
	public void setUnEarnedPrincipal(BigDecimal unEarnedPrincipal) {
    	this.unEarnedPrincipal = unEarnedPrincipal;
    }
	public BigDecimal getUnEarnedProfit() {
    	return unEarnedProfit;
    }
	public void setUnEarnedProfit(BigDecimal unEarnedProfit) {
    	this.unEarnedProfit = unEarnedProfit;
    }
	public BigDecimal getTotalUnEarned() {
    	return totalUnEarned;
    }
	public void setTotalUnEarned(BigDecimal totalUnEarned) {
    	this.totalUnEarned = totalUnEarned;
    }
	public BigDecimal getPayOffPrincipal() {
    	return payOffPrincipal;
    }
	public void setPayOffPrincipal(BigDecimal payOffPrincipal) {
    	this.payOffPrincipal = payOffPrincipal;
    }
	public BigDecimal getPayOffProfit() {
    	return payOffProfit;
    }
	public void setPayOffProfit(BigDecimal payOffProfit) {
    	this.payOffProfit = payOffProfit;
    }
	public BigDecimal getTotalPayOff() {
    	return totalPayOff;
    }
	public void setTotalPayOff(BigDecimal totalPayOff) {
    	this.totalPayOff = totalPayOff;
    }
	public long getOverDueInstlments() {
    	return overDueInstlments;
    }
	public void setOverDueInstlments(long overDueInstlments) {
    	this.overDueInstlments = overDueInstlments;
    }
	public BigDecimal getOverDueInstlementPft() {
    	return overDueInstlementPft;
    }
	public void setOverDueInstlementPft(BigDecimal overDueInstlementPft) {
    	this.overDueInstlementPft = overDueInstlementPft;
    }
	public long getPaidInstlments() {
    	return paidInstlments;
    }
	public void setPaidInstlments(long paidInstlments) {
    	this.paidInstlments = paidInstlments;
    }
	public BigDecimal getPaidInstlementPft() {
    	return paidInstlementPft;
    }
	public void setPaidInstlementPft(BigDecimal paidInstlementPft) {
    	this.paidInstlementPft = paidInstlementPft;
    }
	public long getUnPaidInstlments() {
    	return unPaidInstlments;
    }
	public void setUnPaidInstlments(long unPaidInstlments) {
    	this.unPaidInstlments = unPaidInstlments;
    }
	public BigDecimal getUnPaidInstlementPft() {
    	return unPaidInstlementPft;
    }
	public void setUnPaidInstlementPft(BigDecimal unPaidInstlementPft) {
    	this.unPaidInstlementPft = unPaidInstlementPft;
    }
	public int getCcyEditField() {
    	return ccyEditField;
    }
	public void setCcyEditField(int ccyEditField) {
    	this.ccyEditField = ccyEditField;
    }
	public String getLovDescFinBranchName() {
    	return lovDescFinBranchName;
    }
	public void setLovDescFinBranchName(String lovDescFinBranchName) {
    	this.lovDescFinBranchName = lovDescFinBranchName;
    }
	public String getLovDescFinTypeName() {
    	return lovDescFinTypeName;
    }
	public void setLovDescFinTypeName(String lovDescFinTypeName) {
    	this.lovDescFinTypeName = lovDescFinTypeName;
    }
	public String getLovDescFinCcyName() {
    	return lovDescFinCcyName;
    }
	public void setLovDescFinCcyName(String lovDescFinCcyName) {
    	this.lovDescFinCcyName = lovDescFinCcyName;
    }
	public String getLovDescCustShrtName() {
    	return lovDescCustShrtName;
    }
	public void setLovDescCustShrtName(String lovDescCustShrtName) {
    	this.lovDescCustShrtName = lovDescCustShrtName;
    }
	public void setNumberOfTerms(long numberOfTerms) {
	    this.numberOfTerms = numberOfTerms;
    }
	public long getNumberOfTerms() {
	    return numberOfTerms;
    }
	public void setTotalRepayAmt(BigDecimal totalRepayAmt) {
	    this.totalRepayAmt = totalRepayAmt;
    }
	public BigDecimal getTotalRepayAmt() {
	    return totalRepayAmt;
    }
	public String getFinCommitmentRef() {
    	return finCommitmentRef;
    }
	public void setFinCommitmentRef(String finCommitmentRef) {
    	this.finCommitmentRef = finCommitmentRef;
    }
	
	public String getCmtTitle() {
    	return cmtTitle;
    }
	public void setCmtTitle(String cmtTitle) {
    	this.cmtTitle = cmtTitle;
    }
	public BigDecimal getCmtAvailable() {
    	return cmtAvailable;
    }
	public void setCmtAvailable(BigDecimal cmtAvailable) {
    	this.cmtAvailable = cmtAvailable;
    }
	public BigDecimal getCmtAmount() {
    	return cmtAmount;
    }
	public void setCmtAmount(BigDecimal cmtAmount) {
    	this.cmtAmount = cmtAmount;
    }
	public Date getCmtExpiryDate() {
    	return CmtExpiryDate;
    }
	public void setCmtExpiryDate(Date cmtExpiryDate) {
    	CmtExpiryDate = cmtExpiryDate;
    }
}
