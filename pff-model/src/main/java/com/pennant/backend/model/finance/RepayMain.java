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
 * * FileName : FinanceMain.java * * Author : PENNANT TECHONOLOGIES * * Creation Date : 15-11-2011 * * Modified Date :
 * 15-11-2011 * * Description : * *
 ********************************************************************************************
 * Date Author Version Comments *
 ********************************************************************************************
 * 15-11-2011 Pennant 0.1 * * * * * * * * *
 ********************************************************************************************
 */

package com.pennant.backend.model.finance;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

public class RepayMain implements Serializable {

	private static final long serialVersionUID = 1L;
	private long finID;
	private String finReference = null;
	private String finCcy;
	private String profitDaysBais;
	private String finType;
	private String lovDescFinTypeName;
	private String finBranch = null;
	private String lovDescFinBranchName;
	private long custID;
	private String lovDescCustCIF;
	private String lovDescSalutationName;
	private String lovDescCustFName;
	private String lovDescCustLName;
	private String lovDescCustShrtName;
	private Date dateStart;
	private Date dateMatuirty;
	private BigDecimal finAmount = BigDecimal.ZERO;
	private BigDecimal curFinAmount = BigDecimal.ZERO;
	private BigDecimal profit = BigDecimal.ZERO;
	private BigDecimal profitBalance = BigDecimal.ZERO;
	private BigDecimal principal = BigDecimal.ZERO;
	private BigDecimal principalBalance = BigDecimal.ZERO;
	private BigDecimal totalCapitalize = BigDecimal.ZERO;
	private BigDecimal capitalizeBalance = BigDecimal.ZERO;
	private BigDecimal overduePrincipal = BigDecimal.ZERO;
	private BigDecimal overdueProfit = BigDecimal.ZERO;
	private BigDecimal totalFeeAmt = BigDecimal.ZERO;
	private Date dateLastFullyPaid;
	private Date dateNextPaymentDue;
	private BigDecimal accrued = BigDecimal.ZERO;
	private BigDecimal downpayment = BigDecimal.ZERO;
	private BigDecimal repayAmountNow = BigDecimal.ZERO;
	private BigDecimal principalPayNow = BigDecimal.ZERO;
	private BigDecimal profitPayNow = BigDecimal.ZERO;
	private BigDecimal refundNow = BigDecimal.ZERO;
	private BigDecimal pendindODCharges = BigDecimal.ZERO;
	private int lovDescFinFormatter;
	private BigDecimal repayAmountExcess = BigDecimal.ZERO;

	private String earlyPayEffectOn;
	private boolean earlyPay;
	private BigDecimal earlyPayAmount = BigDecimal.ZERO;
	private FinanceScheduleDetail earlyRepayNewSchd;
	private Date earlyPayOnSchDate;
	private Date earlyPayNextSchDate;
	private Date refundCalStartDate;
	private String payApportionment;

	private BigDecimal profitPaid = BigDecimal.ZERO;
	private BigDecimal principalPaid = BigDecimal.ZERO;

	public RepayMain() {
	    super();
	}

	public RepayMain copyEntity() {
		RepayMain entity = new RepayMain();
		entity.setFinID(this.finID);
		entity.setFinReference(this.finReference);
		entity.setFinCcy(this.finCcy);
		entity.setProfitDaysBais(this.profitDaysBais);
		entity.setFinType(this.finType);
		entity.setLovDescFinTypeName(this.lovDescFinTypeName);
		entity.setFinBranch(this.finBranch);
		entity.setLovDescFinBranchName(this.lovDescFinBranchName);
		entity.setCustID(this.custID);
		entity.setLovDescCustCIF(this.lovDescCustCIF);
		entity.setLovDescSalutationName(this.lovDescSalutationName);
		entity.setLovDescCustFName(this.lovDescCustFName);
		entity.setLovDescCustLName(this.lovDescCustLName);
		entity.setLovDescCustShrtName(this.lovDescCustShrtName);
		entity.setDateStart(this.dateStart);
		entity.setDateMatuirty(this.dateMatuirty);
		entity.setFinAmount(this.finAmount);
		entity.setCurFinAmount(this.curFinAmount);
		entity.setProfit(this.profit);
		entity.setProfitBalance(this.profitBalance);
		entity.setPrincipal(this.principal);
		entity.setPrincipalBalance(this.principalBalance);
		entity.setTotalCapitalize(this.totalCapitalize);
		entity.setCapitalizeBalance(this.capitalizeBalance);
		entity.setOverduePrincipal(this.overduePrincipal);
		entity.setOverdueProfit(this.overdueProfit);
		entity.setTotalFeeAmt(this.totalFeeAmt);
		entity.setDateLastFullyPaid(this.dateLastFullyPaid);
		entity.setDateNextPaymentDue(this.dateNextPaymentDue);
		entity.setAccrued(this.accrued);
		entity.setDownpayment(this.downpayment);
		entity.setRepayAmountNow(this.repayAmountNow);
		entity.setPrincipalPayNow(this.principalPayNow);
		entity.setProfitPayNow(this.profitPayNow);
		entity.setRefundNow(this.refundNow);
		entity.setPendindODCharges(this.pendindODCharges);
		entity.setLovDescFinFormatter(this.lovDescFinFormatter);
		entity.setRepayAmountExcess(this.repayAmountExcess);
		entity.setEarlyPayEffectOn(this.earlyPayEffectOn);
		entity.setEarlyPay(this.earlyPay);
		entity.setEarlyPayAmount(this.earlyPayAmount);
		entity.setEarlyRepayNewSchd(this.earlyRepayNewSchd == null ? null : this.earlyRepayNewSchd.copyEntity());
		entity.setEarlyPayOnSchDate(this.earlyPayOnSchDate);
		entity.setEarlyPayNextSchDate(this.earlyPayNextSchDate);
		entity.setRefundCalStartDate(this.refundCalStartDate);
		entity.setPayApportionment(this.payApportionment);
		entity.setProfitPaid(this.profitPaid);
		entity.setPrincipalPaid(this.principalPaid);
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

	public String getFinCcy() {
		return finCcy;
	}

	public void setFinCcy(String finCcy) {
		this.finCcy = finCcy;
	}

	public String getProfitDaysBais() {
		return profitDaysBais;
	}

	public void setProfitDaysBais(String profitDaysBais) {
		this.profitDaysBais = profitDaysBais;
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

	public String getLovDescSalutationName() {
		return lovDescSalutationName;
	}

	public void setLovDescSalutationName(String lovDescSalutationName) {
		this.lovDescSalutationName = lovDescSalutationName;
	}

	public String getLovDescCustFName() {
		return lovDescCustFName;
	}

	public void setLovDescCustFName(String lovDescCustFName) {
		this.lovDescCustFName = lovDescCustFName;
	}

	public String getLovDescCustLName() {
		return lovDescCustLName;
	}

	public void setLovDescCustLName(String lovDescCustLName) {
		this.lovDescCustLName = lovDescCustLName;
	}

	public String getLovDescCustShrtName() {
		return lovDescCustShrtName;
	}

	public void setLovDescCustShrtName(String lovDescCustShrtName) {
		this.lovDescCustShrtName = lovDescCustShrtName;
	}

	public Date getDateStart() {
		return dateStart;
	}

	public void setDateStart(Date dateStart) {
		this.dateStart = dateStart;
	}

	public Date getDateMatuirty() {
		return dateMatuirty;
	}

	public void setDateMatuirty(Date dateMatuirty) {
		this.dateMatuirty = dateMatuirty;
	}

	public BigDecimal getFinAmount() {
		return finAmount;
	}

	public void setFinAmount(BigDecimal finAmount) {
		this.finAmount = finAmount;
	}

	public BigDecimal getCurFinAmount() {
		return curFinAmount;
	}

	public void setCurFinAmount(BigDecimal curFinAmount) {
		this.curFinAmount = curFinAmount;
	}

	public BigDecimal getProfit() {
		return profit;
	}

	public void setProfit(BigDecimal profit) {
		this.profit = profit;
	}

	public BigDecimal getProfitBalance() {
		return profitBalance;
	}

	public void setProfitBalance(BigDecimal profitBalance) {
		this.profitBalance = profitBalance;
	}

	public BigDecimal getPrincipal() {
		return principal;
	}

	public void setPrincipal(BigDecimal principal) {
		this.principal = principal;
	}

	public BigDecimal getPrincipalBalance() {
		return principalBalance;
	}

	public void setPrincipalBalance(BigDecimal principalBalance) {
		this.principalBalance = principalBalance;
	}

	public BigDecimal getTotalCapitalize() {
		return totalCapitalize;
	}

	public void setTotalCapitalize(BigDecimal totalCapitalize) {
		this.totalCapitalize = totalCapitalize;
	}

	public BigDecimal getCapitalizeBalance() {
		return capitalizeBalance;
	}

	public void setCapitalizeBalance(BigDecimal capitalizeBalance) {
		this.capitalizeBalance = capitalizeBalance;
	}

	public BigDecimal getOverduePrincipal() {
		return overduePrincipal;
	}

	public void setOverduePrincipal(BigDecimal overduePrincipal) {
		this.overduePrincipal = overduePrincipal;
	}

	public BigDecimal getOverdueProfit() {
		return overdueProfit;
	}

	public void setOverdueProfit(BigDecimal overdueProfit) {
		this.overdueProfit = overdueProfit;
	}

	public BigDecimal getTotalFeeAmt() {
		return totalFeeAmt;
	}

	public void setTotalFeeAmt(BigDecimal totalFeeAmt) {
		this.totalFeeAmt = totalFeeAmt;
	}

	public Date getDateLastFullyPaid() {
		return dateLastFullyPaid;
	}

	public void setDateLastFullyPaid(Date dateLastFullyPaid) {
		this.dateLastFullyPaid = dateLastFullyPaid;
	}

	public Date getDateNextPaymentDue() {
		return dateNextPaymentDue;
	}

	public void setDateNextPaymentDue(Date dateNextPaymentDue) {
		this.dateNextPaymentDue = dateNextPaymentDue;
	}

	public BigDecimal getAccrued() {
		return accrued;
	}

	public void setAccrued(BigDecimal accrued) {
		this.accrued = accrued;
	}

	public BigDecimal getDownpayment() {
		return downpayment;
	}

	public void setDownpayment(BigDecimal downpayment) {
		this.downpayment = downpayment;
	}

	public BigDecimal getRepayAmountNow() {
		return repayAmountNow;
	}

	public void setRepayAmountNow(BigDecimal repayAmountNow) {
		this.repayAmountNow = repayAmountNow;
	}

	public BigDecimal getPrincipalPayNow() {
		return principalPayNow;
	}

	public void setPrincipalPayNow(BigDecimal principalPayNow) {
		this.principalPayNow = principalPayNow;
	}

	public BigDecimal getProfitPayNow() {
		return profitPayNow;
	}

	public void setProfitPayNow(BigDecimal profitPayNow) {
		this.profitPayNow = profitPayNow;
	}

	public BigDecimal getRefundNow() {
		return refundNow;
	}

	public void setRefundNow(BigDecimal refundNow) {
		this.refundNow = refundNow;
	}

	public int getLovDescFinFormatter() {
		return lovDescFinFormatter;
	}

	public void setLovDescFinFormatter(int lovDescFinFormatter) {
		this.lovDescFinFormatter = lovDescFinFormatter;
	}

	public BigDecimal getPendindODCharges() {
		return pendindODCharges;
	}

	public void setPendindODCharges(BigDecimal pendindODCharges) {
		this.pendindODCharges = pendindODCharges;
	}

	public BigDecimal getRepayAmountExcess() {
		return repayAmountExcess;
	}

	public void setRepayAmountExcess(BigDecimal repayAmountExcess) {
		this.repayAmountExcess = repayAmountExcess;
	}

	public void setEarlyPay(boolean earlyPay) {
		this.earlyPay = earlyPay;
	}

	public boolean isEarlyPay() {
		return earlyPay;
	}

	public void setEarlyPayAmount(BigDecimal earlyPayAmount) {
		this.earlyPayAmount = earlyPayAmount;
	}

	public BigDecimal getEarlyPayAmount() {
		return earlyPayAmount;
	}

	public FinanceScheduleDetail getEarlyRepayNewSchd() {
		return earlyRepayNewSchd;
	}

	public void setEarlyRepayNewSchd(FinanceScheduleDetail earlyRepayNewSchd) {
		this.earlyRepayNewSchd = earlyRepayNewSchd;
	}

	public Date getEarlyPayOnSchDate() {
		return earlyPayOnSchDate;
	}

	public void setEarlyPayOnSchDate(Date earlyPayOnSchDate) {
		this.earlyPayOnSchDate = earlyPayOnSchDate;
	}

	public Date getEarlyPayNextSchDate() {
		return earlyPayNextSchDate;
	}

	public void setEarlyPayNextSchDate(Date earlyPayNextSchDate) {
		this.earlyPayNextSchDate = earlyPayNextSchDate;
	}

	public void setEarlyPayEffectOn(String earlyPayEffectOn) {
		this.earlyPayEffectOn = earlyPayEffectOn;
	}

	public String getEarlyPayEffectOn() {
		return earlyPayEffectOn;
	}

	public void setRefundCalStartDate(Date refundCalStartDate) {
		this.refundCalStartDate = refundCalStartDate;
	}

	public Date getRefundCalStartDate() {
		return refundCalStartDate;
	}

	public String getPayApportionment() {
		return payApportionment;
	}

	public void setPayApportionment(String payApportionment) {
		this.payApportionment = payApportionment;
	}

	public BigDecimal getProfitPaid() {
		return profitPaid;
	}

	public void setProfitPaid(BigDecimal profitPaid) {
		this.profitPaid = profitPaid;
	}

	public BigDecimal getPrincipalPaid() {
		return principalPaid;
	}

	public void setPrincipalPaid(BigDecimal principalPaid) {
		this.principalPaid = principalPaid;
	}

}
