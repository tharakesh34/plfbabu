/**
 * Copyright 2011 - Pennant Technologies
 * 
 * This file is part of Pennant Java Application Framework and related Products. All
 * components/modules/functions/classes/logic in this software, unless otherwise stated, the property of Pennant
 * Technologies.
 * 
 * Copyright and other intellectual property laws protect these materials. Reproduction or retransmission of the
 * materials, in whole or in part, in any manner, without the prior written consent of the copyright holder, is a
 * violation of copyright law.
 */

/**
 ********************************************************************************************
 * FILE HEADER *
 ********************************************************************************************
 * * FileName : FinanceSummary.java * * Author : PENNANT TECHONOLOGIES * * Creation Date : 13-08-2012 * * Modified Date
 * : 13-08-2012 * * Description : * *
 ********************************************************************************************
 * Date Author Version Comments *
 ********************************************************************************************
 * 16-03-2012 Pennant 0.1 * * * * * * * * *
 ********************************************************************************************
 */
package com.pennant.backend.model.finance;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlType;

@XmlType(propOrder = { "effectiveRateOfReturn", "totalPriSchd", "totalGracePft", "totalGraceCpz", "totalGrossGrcPft",
		"totalCpz", "totalProfit", "totalRepayAmt", "feeChargeAmt", "numberOfTerms", "loanTenor", "maturityDate",
		"firstDisbDate", "lastDisbDate", "firstEmiAmount", "nextSchDate", "nextRepayAmount", "futureInst",
		"futureTenor", "firstInstDate", "paidTotal", "schdPriPaid", "schdPftPaid", "finLastRepayDate",
		"totalOutStanding", "outStandPrincipal", "outStandProfit", "totalOverDue", "overDuePrincipal", "overDueProfit",
		"overDueInstlments", "overDueCharges", "totalOverDueIncCharges", "finODDetail", "advPaymentAmount", "finStatus",
		"fullyDisb", "sanctionAmt", "utilizedAmt", "availableAmt", "finCurODDays", "foreClosureAmount", "installmentNo",
		"dueDate", "overDueEMI" })

@XmlAccessorType(XmlAccessType.NONE)
public class FinanceSummary implements Serializable {
	private static final long serialVersionUID = 1854976637601258760L;

	private long finID;
	private String finReference;
	private BigDecimal totalDisbursement = BigDecimal.ZERO;
	@XmlElement(name = "loanPrincipal")
	private BigDecimal totalPriSchd = BigDecimal.ZERO;
	private BigDecimal totalPftSchd = BigDecimal.ZERO;
	private BigDecimal principalSchd = BigDecimal.ZERO;
	private BigDecimal profitSchd = BigDecimal.ZERO;
	@XmlElement(name = "paidPft")
	private BigDecimal schdPftPaid = BigDecimal.ZERO;
	private BigDecimal currentFinanceAmount = BigDecimal.ZERO;
	@XmlElement(name = "paidPri")
	private BigDecimal schdPriPaid = BigDecimal.ZERO;
	private BigDecimal totalDownPayment = BigDecimal.ZERO;
	private BigDecimal downPaymentToBank = BigDecimal.ZERO;
	private BigDecimal downPaymentToSpplier = BigDecimal.ZERO;
	private BigDecimal profitSuspended = BigDecimal.ZERO;
	@XmlElement
	private BigDecimal totalCpz = BigDecimal.ZERO;

	@XmlElement(name = "DPD")
	private int finCurODDays = 0;
	private String assetCode;
	private BigDecimal finODTotPenaltyAmt = BigDecimal.ZERO;
	private BigDecimal finODTotWaived = BigDecimal.ZERO;
	private BigDecimal finODTotPenaltyPaid = BigDecimal.ZERO;
	private BigDecimal finODTotPenaltyBal = BigDecimal.ZERO;

	@XmlElement
	private Date nextSchDate;
	private Date schDate;

	// Posting Details
	private BigDecimal totalFees = BigDecimal.ZERO;
	private BigDecimal totalWaiverFee = BigDecimal.ZERO;
	private BigDecimal totalPaidFee = BigDecimal.ZERO;

	// Finance Profit Enquiry
	private String finType;
	private String finBranch = null;
	private String finCcy;
	private long custID;
	private Date finStartDate;
	@XmlElement
	private Date maturityDate;
	@XmlElement(name = "finActiveStatus")
	private String finStatus;
	private BigDecimal finRate = BigDecimal.ZERO;
	@XmlElement(name = "lastRepayDate")
	private Date finLastRepayDate;
	@XmlElement(name = "outstandingPri")
	private BigDecimal outStandPrincipal = BigDecimal.ZERO;
	@XmlElement(name = "outstandingPft")
	private BigDecimal outStandProfit = BigDecimal.ZERO;
	@XmlElement(name = "outstandingTotal")
	private BigDecimal totalOutStanding = BigDecimal.ZERO;

	private BigDecimal principal = BigDecimal.ZERO;
	private BigDecimal futurePrincipal = BigDecimal.ZERO;
	private BigDecimal interest = BigDecimal.ZERO;
	private BigDecimal futureInterest = BigDecimal.ZERO;

	private BigDecimal totalOriginal = BigDecimal.ZERO;
	private BigDecimal totalPaid = BigDecimal.ZERO;

	private BigDecimal unPaidPrincipal = BigDecimal.ZERO;
	private BigDecimal unPaidProfit = BigDecimal.ZERO;
	private BigDecimal totalUnPaid = BigDecimal.ZERO;

	@XmlElement(name = "overduePri")
	private BigDecimal overDuePrincipal = BigDecimal.ZERO;
	@XmlElement(name = "overduePft")
	private BigDecimal overDueProfit = BigDecimal.ZERO;
	@XmlElement(name = "overdueTotal")
	private BigDecimal totalOverDue = BigDecimal.ZERO;
	@XmlElement
	private BigDecimal overDueCharges = BigDecimal.ZERO;
	@XmlElement(name = "overdueTotalIncludeCharges")
	private BigDecimal totalOverDueIncCharges = BigDecimal.ZERO;

	private BigDecimal earnedPrincipal = BigDecimal.ZERO;
	private BigDecimal earnedProfit = BigDecimal.ZERO;
	private BigDecimal totalEarned = BigDecimal.ZERO;

	private BigDecimal unEarnedPrincipal = BigDecimal.ZERO;
	private BigDecimal unEarnedProfit = BigDecimal.ZERO;
	private BigDecimal totalUnEarned = BigDecimal.ZERO;

	private BigDecimal payOffPrincipal = BigDecimal.ZERO;
	private BigDecimal payOffProfit = BigDecimal.ZERO;
	private BigDecimal totalPayOff = BigDecimal.ZERO;

	@XmlElement(name = "overdueInst")
	private long overDueInstlments;
	private BigDecimal overDueInstlementPft = BigDecimal.ZERO;
	private long paidInstlments;
	private BigDecimal paidInstlementPft = BigDecimal.ZERO;
	private long unPaidInstlments;
	private BigDecimal unPaidInstlementPft = BigDecimal.ZERO;
	@XmlElement
	private long numberOfTerms;
	private int NOInst;

	private String custCIF;

	@XmlElement
	private BigDecimal totalRepayAmt = BigDecimal.ZERO;

	private String finCommitmentRef;
	private String cmtTitle;
	private BigDecimal cmtAmount = BigDecimal.ZERO;
	private BigDecimal cmtAvailable = BigDecimal.ZERO;
	private Date CmtExpiryDate;
	private int utilizedDefCnt;

	// PFF-API specific fields
	@XmlElement
	private BigDecimal effectiveRateOfReturn = BigDecimal.ZERO;
	@XmlElement
	private BigDecimal totalGracePft = BigDecimal.ZERO;
	@XmlElement
	private BigDecimal totalGraceCpz = BigDecimal.ZERO;
	@XmlElement
	private BigDecimal totalGrossGrcPft = BigDecimal.ZERO;
	@XmlElement
	private BigDecimal totalProfit = BigDecimal.ZERO;
	@XmlElement(name = "feeChargeAmount")
	private BigDecimal feeChargeAmt = BigDecimal.ZERO;
	@XmlElement
	private int loanTenor;
	@XmlElement
	private Date firstDisbDate;
	@XmlElement
	private Date lastDisbDate;
	@XmlElement
	private BigDecimal nextRepayAmount;
	@XmlElement
	private BigDecimal firstEmiAmount;
	@XmlElement(name = "loanBalanceTenure")
	private int futureInst;
	@XmlElement
	private int futureTenor;
	@XmlElement
	private Date firstInstDate;
	@XmlElement
	private BigDecimal paidTotal;
	@XmlElement
	private BigDecimal advPaymentAmount;
	@XmlElementWrapper(name = "overdueCharges")
	@XmlElement(name = "overdueCharge")
	private List<FinODDetails> finODDetail;
	@XmlElement
	private boolean fullyDisb;
	@XmlElement(name = "limitBalance")
	private BigDecimal sanctionAmt = BigDecimal.ZERO;
	@XmlElement(name = "billedAmount")
	private BigDecimal utilizedAmt = BigDecimal.ZERO;
	@XmlElement(name = "unbilledAmount")
	private BigDecimal availableAmt = BigDecimal.ZERO;
	@XmlElement
	private BigDecimal dueCharges = BigDecimal.ZERO;
	@XmlElement
	private BigDecimal overDueAmount = BigDecimal.ZERO;
	private BigDecimal totalDueAmt = BigDecimal.ZERO;
	private BigDecimal totalOutStandCharges = BigDecimal.ZERO;
	private BigDecimal OutStandIncludeCharges = BigDecimal.ZERO;
	private BigDecimal availableAmtExcludeCharges = BigDecimal.ZERO;
	private BigDecimal loanEMI = BigDecimal.ZERO;
	private BigDecimal foreClosureAmount = BigDecimal.ZERO;
	private int installmentNo;
	private Date dueDate;
	private BigDecimal loanTotPrincipal = BigDecimal.ZERO;
	private BigDecimal loanTotInterest = BigDecimal.ZERO;
	private BigDecimal overDueEMI;
	private String vehicleNo;
	private String migratedNo;
	private Date lastInstDate;

	public FinanceSummary() {
		super();
	}

	public FinanceSummary copyEntity() {
		FinanceSummary entity = new FinanceSummary();
		entity.setFinID(this.finID);
		entity.setFinReference(this.finReference);
		entity.setTotalDisbursement(this.totalDisbursement);
		entity.setTotalPriSchd(this.totalPriSchd);
		entity.setTotalPftSchd(this.totalPftSchd);
		entity.setPrincipalSchd(this.principalSchd);
		entity.setProfitSchd(this.profitSchd);
		entity.setSchdPftPaid(this.schdPftPaid);
		entity.setCurrentFinanceAmount(this.currentFinanceAmount);
		entity.setSchdPriPaid(this.schdPriPaid);
		entity.setTotalDownPayment(this.totalDownPayment);
		entity.setDownPaymentToBank(this.downPaymentToBank);
		entity.setDownPaymentToSpplier(this.downPaymentToSpplier);
		entity.setProfitSuspended(this.profitSuspended);
		entity.setTotalCpz(this.totalCpz);
		entity.setFinCurODDays(this.finCurODDays);
		entity.setAssetCode(this.assetCode);
		entity.setFinODTotPenaltyAmt(this.finODTotPenaltyAmt);
		entity.setFinODTotWaived(this.finODTotWaived);
		entity.setFinODTotPenaltyPaid(this.finODTotPenaltyPaid);
		entity.setFinODTotPenaltyBal(this.finODTotPenaltyBal);
		entity.setNextSchDate(this.nextSchDate);
		entity.setSchDate(this.schDate);
		entity.setTotalFees(this.totalFees);
		entity.setTotalWaiverFee(this.totalWaiverFee);
		entity.setTotalPaidFee(this.totalPaidFee);
		entity.setFinType(this.finType);
		entity.setFinBranch(this.finBranch);
		entity.setFinCcy(this.finCcy);
		entity.setCustID(this.custID);
		entity.setFinStartDate(this.finStartDate);
		entity.setMaturityDate(this.maturityDate);
		entity.setFinStatus(this.finStatus);
		entity.setFinRate(this.finRate);
		entity.setFinLastRepayDate(this.finLastRepayDate);
		entity.setOutStandPrincipal(this.outStandPrincipal);
		entity.setOutStandProfit(this.outStandProfit);
		entity.setTotalOutStanding(this.totalOutStanding);
		entity.setPrincipal(this.principal);
		entity.setFuturePrincipal(this.futurePrincipal);
		entity.setInterest(this.interest);
		entity.setFutureInterest(this.futureInterest);
		entity.setTotalOriginal(this.totalOriginal);
		entity.setTotalPaid(this.totalPaid);
		entity.setUnPaidPrincipal(this.unPaidPrincipal);
		entity.setUnPaidProfit(this.unPaidProfit);
		entity.setTotalUnPaid(this.totalUnPaid);
		entity.setOverDuePrincipal(this.overDuePrincipal);
		entity.setOverDueProfit(this.overDueProfit);
		entity.setTotalOverDue(this.totalOverDue);
		entity.setOverDueCharges(this.overDueCharges);
		entity.setTotalOverDueIncCharges(this.totalOverDueIncCharges);
		entity.setEarnedPrincipal(this.earnedPrincipal);
		entity.setEarnedProfit(this.earnedProfit);
		entity.setTotalEarned(this.totalEarned);
		entity.setUnEarnedPrincipal(this.unEarnedPrincipal);
		entity.setUnEarnedProfit(this.unEarnedProfit);
		entity.setTotalUnEarned(this.totalUnEarned);
		entity.setPayOffPrincipal(this.payOffPrincipal);
		entity.setPayOffProfit(this.payOffProfit);
		entity.setTotalPayOff(this.totalPayOff);
		entity.setOverDueInstlments(this.overDueInstlments);
		entity.setOverDueInstlementPft(this.overDueInstlementPft);
		entity.setPaidInstlments(this.paidInstlments);
		entity.setPaidInstlementPft(this.paidInstlementPft);
		entity.setUnPaidInstlments(this.unPaidInstlments);
		entity.setUnPaidInstlementPft(this.unPaidInstlementPft);
		entity.setNumberOfTerms(this.numberOfTerms);
		entity.setNOInst(this.NOInst);
		entity.setCustCIF(this.custCIF);
		entity.setTotalRepayAmt(this.totalRepayAmt);
		entity.setFinCommitmentRef(this.finCommitmentRef);
		entity.setCmtTitle(this.cmtTitle);
		entity.setCmtAmount(this.cmtAmount);
		entity.setCmtAvailable(this.cmtAvailable);
		entity.setCmtExpiryDate(this.CmtExpiryDate);
		entity.setUtilizedDefCnt(this.utilizedDefCnt);
		entity.setEffectiveRateOfReturn(this.effectiveRateOfReturn);
		entity.setTotalGracePft(this.totalGracePft);
		entity.setTotalGraceCpz(this.totalGraceCpz);
		entity.setTotalGrossGrcPft(this.totalGrossGrcPft);
		entity.setTotalProfit(this.totalProfit);
		entity.setFeeChargeAmt(this.feeChargeAmt);
		entity.setLoanTenor(this.loanTenor);
		entity.setFirstDisbDate(this.firstDisbDate);
		entity.setLastDisbDate(this.lastDisbDate);
		entity.setNextRepayAmount(this.nextRepayAmount);
		entity.setFirstEmiAmount(this.firstEmiAmount);
		entity.setFutureInst(this.futureInst);
		entity.setFutureTenor(this.futureTenor);
		entity.setFirstInstDate(this.firstInstDate);
		entity.setPaidTotal(this.paidTotal);
		entity.setAdvPaymentAmount(this.advPaymentAmount);
		if (finODDetail != null) {
			entity.setFinODDetail(new ArrayList<>());
			this.finODDetail.stream().forEach(e -> entity.getFinODDetail().add(e == null ? null : e.copyEntity()));
		}
		entity.setFullyDisb(this.fullyDisb);
		entity.setSanctionAmt(this.sanctionAmt);
		entity.setUtilizedAmt(this.utilizedAmt);
		entity.setAvailableAmt(this.availableAmt);
		entity.setDueCharges(this.dueCharges);
		entity.setOverDueAmount(this.overDueAmount);
		entity.setTotalDueAmt(this.totalDueAmt);
		entity.setTotalOutStandCharges(this.totalOutStandCharges);
		entity.setOutStandIncludeCharges(this.OutStandIncludeCharges);
		entity.setAvailableAmtExcludeCharges(this.availableAmtExcludeCharges);

		return entity;
	}

	public long getFinID() {
		return finID;
	}

	public void setFinID(long finID) {
		this.finID = finID;
	}

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

	public BigDecimal getCurrentFinanceAmount() {
		return currentFinanceAmount;
	}

	public void setCurrentFinanceAmount(BigDecimal currentFinanceAmount) {
		this.currentFinanceAmount = currentFinanceAmount;
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

	public BigDecimal getDownPaymentToBank() {
		return downPaymentToBank;
	}

	public void setDownPaymentToBank(BigDecimal downPaymentToBank) {
		this.downPaymentToBank = downPaymentToBank;
	}

	public BigDecimal getDownPaymentToSpplier() {
		return downPaymentToSpplier;
	}

	public void setDownPaymentToSpplier(BigDecimal downPaymentToSpplier) {
		this.downPaymentToSpplier = downPaymentToSpplier;
	}

	public BigDecimal getProfitSuspended() {
		return profitSuspended;
	}

	public void setProfitSuspended(BigDecimal profitSuspended) {
		this.profitSuspended = profitSuspended;
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

	public int getUtilizedDefCnt() {
		return utilizedDefCnt;
	}

	public void setUtilizedDefCnt(int utilizedDefCnt) {
		this.utilizedDefCnt = utilizedDefCnt;
	}

	public BigDecimal getEffectiveRateOfReturn() {
		return effectiveRateOfReturn;
	}

	public void setEffectiveRateOfReturn(BigDecimal effectiveRateOfReturn) {
		this.effectiveRateOfReturn = effectiveRateOfReturn;
	}

	public BigDecimal getTotalGracePft() {
		return totalGracePft;
	}

	public void setTotalGracePft(BigDecimal totalGracePft) {
		this.totalGracePft = totalGracePft;
	}

	public BigDecimal getTotalGraceCpz() {
		return totalGraceCpz;
	}

	public void setTotalGraceCpz(BigDecimal totalGraceCpz) {
		this.totalGraceCpz = totalGraceCpz;
	}

	public BigDecimal getTotalGrossGrcPft() {
		return totalGrossGrcPft;
	}

	public void setTotalGrossGrcPft(BigDecimal totalGrossGrcPft) {
		this.totalGrossGrcPft = totalGrossGrcPft;
	}

	public BigDecimal getTotalProfit() {
		return totalProfit;
	}

	public void setTotalProfit(BigDecimal totalProfit) {
		this.totalProfit = totalProfit;
	}

	public BigDecimal getFeeChargeAmt() {
		return feeChargeAmt;
	}

	public void setFeeChargeAmt(BigDecimal feeChargeAmt) {
		this.feeChargeAmt = feeChargeAmt;
	}

	public String getCustCIF() {
		return custCIF;
	}

	public void setCustCIF(String custCIF) {
		this.custCIF = custCIF;
	}

	public int getLoanTenor() {
		return loanTenor;
	}

	public void setLoanTenor(int loanTenor) {
		this.loanTenor = loanTenor;
	}

	public Date getFirstDisbDate() {
		return firstDisbDate;
	}

	public void setFirstDisbDate(Date firstDisbDate) {
		this.firstDisbDate = firstDisbDate;
	}

	public Date getLastDisbDate() {
		return lastDisbDate;
	}

	public void setLastDisbDate(Date lastDisbDate) {
		this.lastDisbDate = lastDisbDate;
	}

	public BigDecimal getNextRepayAmount() {
		return nextRepayAmount;
	}

	public void setNextRepayAmount(BigDecimal nextRepayAmount) {
		this.nextRepayAmount = nextRepayAmount;
	}

	public BigDecimal getFirstEmiAmount() {
		return firstEmiAmount;
	}

	public void setFirstEmiAmount(BigDecimal firstEmiAmount) {
		this.firstEmiAmount = firstEmiAmount;
	}

	public int getFutureInst() {
		return futureInst;
	}

	public void setFutureInst(int futureInst) {
		this.futureInst = futureInst;
	}

	public int getFutureTenor() {
		return futureTenor;
	}

	public void setFutureTenor(int futureTenor) {
		this.futureTenor = futureTenor;
	}

	public Date getFirstInstDate() {
		return firstInstDate;
	}

	public void setFirstInstDate(Date firstInstDate) {
		this.firstInstDate = firstInstDate;
	}

	public BigDecimal getPaidTotal() {
		return paidTotal;
	}

	public void setPaidTotal(BigDecimal paidTotal) {
		this.paidTotal = paidTotal;
	}

	public List<FinODDetails> getFinODDetail() {
		return finODDetail;
	}

	public void setFinODDetail(List<FinODDetails> finODDetail) {
		this.finODDetail = finODDetail;
	}

	public BigDecimal getAdvPaymentAmount() {
		return advPaymentAmount;
	}

	public void setAdvPaymentAmount(BigDecimal advPaymentAmount) {
		this.advPaymentAmount = advPaymentAmount;
	}

	public boolean isFullyDisb() {
		return fullyDisb;
	}

	public void setFullyDisb(boolean fullyDisb) {
		this.fullyDisb = fullyDisb;
	}

	public BigDecimal getTotalWaiverFee() {
		return totalWaiverFee;
	}

	public void setTotalWaiverFee(BigDecimal totalWaiverFee) {
		this.totalWaiverFee = totalWaiverFee;
	}

	public BigDecimal getTotalPaidFee() {
		return totalPaidFee;
	}

	public void setTotalPaidFee(BigDecimal totalPaidFee) {
		this.totalPaidFee = totalPaidFee;
	}

	public int getNOInst() {
		return NOInst;
	}

	public void setNOInst(int nOInst) {
		NOInst = nOInst;
	}

	public BigDecimal getSanctionAmt() {
		return sanctionAmt;
	}

	public void setSanctionAmt(BigDecimal sanctionAmt) {
		this.sanctionAmt = sanctionAmt;
	}

	public BigDecimal getUtilizedAmt() {
		return utilizedAmt;
	}

	public void setUtilizedAmt(BigDecimal utilizedAmt) {
		this.utilizedAmt = utilizedAmt;
	}

	public BigDecimal getAvailableAmt() {
		return availableAmt;
	}

	public void setAvailableAmt(BigDecimal availableAmt) {
		this.availableAmt = availableAmt;
	}

	public BigDecimal getTotalOverDueIncCharges() {
		return totalOverDueIncCharges;
	}

	public void setTotalOverDueIncCharges(BigDecimal totalOverDueIncCharges) {
		this.totalOverDueIncCharges = totalOverDueIncCharges;
	}

	public BigDecimal getOverDueCharges() {
		return overDueCharges;
	}

	public void setOverDueCharges(BigDecimal overDueCharges) {
		this.overDueCharges = overDueCharges;
	}

	public BigDecimal getPrincipal() {
		return principal;
	}

	public void setPrincipal(BigDecimal principal) {
		this.principal = principal;
	}

	public BigDecimal getFuturePrincipal() {
		return futurePrincipal;
	}

	public void setFuturePrincipal(BigDecimal futurePrincipal) {
		this.futurePrincipal = futurePrincipal;
	}

	public BigDecimal getInterest() {
		return interest;
	}

	public void setInterest(BigDecimal interest) {
		this.interest = interest;
	}

	public BigDecimal getFutureInterest() {
		return futureInterest;
	}

	public void setFutureInterest(BigDecimal futureInterest) {
		this.futureInterest = futureInterest;
	}

	public BigDecimal getDueCharges() {
		return dueCharges;
	}

	public void setDueCharges(BigDecimal dueCharges) {
		this.dueCharges = dueCharges;
	}

	public BigDecimal getOverDueAmount() {
		return overDueAmount;
	}

	public void setOverDueAmount(BigDecimal overDueAmount) {
		this.overDueAmount = overDueAmount;
	}

	public String getAssetCode() {
		return assetCode;
	}

	public void setAssetCode(String assetCode) {
		this.assetCode = assetCode;
	}

	public BigDecimal getTotalDueAmt() {
		return totalDueAmt;
	}

	public void setTotalDueAmt(BigDecimal totalDueAmt) {
		this.totalDueAmt = totalDueAmt;
	}

	public BigDecimal getTotalOutStandCharges() {
		return totalOutStandCharges;
	}

	public void setTotalOutStandCharges(BigDecimal totalOutStandCharges) {
		this.totalOutStandCharges = totalOutStandCharges;
	}

	public BigDecimal getOutStandIncludeCharges() {
		return OutStandIncludeCharges;
	}

	public void setOutStandIncludeCharges(BigDecimal outStandIncludeCharges) {
		OutStandIncludeCharges = outStandIncludeCharges;
	}

	public BigDecimal getAvailableAmtExcludeCharges() {
		return availableAmtExcludeCharges;
	}

	public void setAvailableAmtExcludeCharges(BigDecimal availableAmtExcludeCharges) {
		this.availableAmtExcludeCharges = availableAmtExcludeCharges;
	}

	public BigDecimal getLoanEMI() {
		return loanEMI;
	}

	public void setLoanEMI(BigDecimal loanEMI) {
		this.loanEMI = loanEMI;
	}

	public BigDecimal getForeClosureAmount() {
		return foreClosureAmount;
	}

	public void setForeClosureAmount(BigDecimal foreClosureAmount) {
		this.foreClosureAmount = foreClosureAmount;
	}

	public Date getDueDate() {
		return dueDate;
	}

	public void setDueDate(Date dueDate) {
		this.dueDate = dueDate;
	}

	public BigDecimal getLoanTotPrincipal() {
		return loanTotPrincipal;
	}

	public void setLoanTotPrincipal(BigDecimal loanTotPrincipal) {
		this.loanTotPrincipal = loanTotPrincipal;
	}

	public BigDecimal getLoanTotInterest() {
		return loanTotInterest;
	}

	public void setLoanTotInterest(BigDecimal loanPft) {
		this.loanTotInterest = loanPft;
	}

	public int getInstallmentNo() {
		return installmentNo;
	}

	public void setInstallmentNo(int installmentNo) {
		this.installmentNo = installmentNo;
	}

	public BigDecimal getOverDueEMI() {
		return overDueEMI;
	}

	public void setOverDueEMI(BigDecimal overDueEMI) {
		this.overDueEMI = overDueEMI;
	}

	public String getVehicleNo() {
		return vehicleNo;
	}

	public void setVehicleNo(String vehicleNo) {
		this.vehicleNo = vehicleNo;
	}

	public String getMigratedNo() {
		return migratedNo;
	}

	public void setMigratedNo(String migratedNo) {
		this.migratedNo = migratedNo;
	}

	public Date getLastInstDate() {
		return lastInstDate;
	}

	public void setLastInstDate(Date lastInstDate) {
		this.lastInstDate = lastInstDate;
	}

}
