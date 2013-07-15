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
 * FileName    		:  FinanceMain.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  15-11-2011    														*
 *                                                                  						*
 * Modified Date    :  15-11-2011    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 15-11-2011       Pennant	                 0.1                                            * 
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


public class RepayMain implements java.io.Serializable {

	private static final long serialVersionUID = 1L;
	private String finReference = null;
	private String finCcy;
	private String lovDescFinCcyName;
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
	private BigDecimal finAmount = new BigDecimal(0);
	private BigDecimal curFinAmount = new BigDecimal(0);
	private BigDecimal profit = new BigDecimal(0);
	private BigDecimal profitBalance = new BigDecimal(0);
	private BigDecimal principal = new BigDecimal(0);
	private BigDecimal principalBalance = new BigDecimal(0);
	private BigDecimal totalCapitalize = new BigDecimal(0);
	private BigDecimal capitalizeBalance = new BigDecimal(0);
	private BigDecimal overduePrincipal = new BigDecimal(0);
	private BigDecimal overdueProfit = new BigDecimal(0);
	private Date dateLastFullyPaid;
	private Date dateNextPaymentDue;
	private BigDecimal accrued = new BigDecimal(0);
	private BigDecimal downpayment = new BigDecimal(0);
	private String repayAccountId;
	private String finAccount;
	private String finCustPftAccount;
	private BigDecimal repayAmountNow = new BigDecimal(0);
	private BigDecimal principalPayNow = new BigDecimal(0);
	private BigDecimal profitPayNow = new BigDecimal(0);
	private BigDecimal refundNow = new BigDecimal(0);
	private BigDecimal pendindODCharges = new BigDecimal(0);
	private int lovDescFinFormatter;
	private BigDecimal repayAmountExcess = new BigDecimal(0);
	
	private String earlyPayEffectOn;
	private boolean earlyPay;
	private BigDecimal earlyPayAmount;
	private FinanceScheduleDetail earlyRepayNewSchd;
	private Date earlyPayOnSchDate;
	private Date earlyPayNextSchDate;

	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//
	// ++++++++++++++++++ getter / setter +++++++++++++++++++//
	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//
	
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

	public String getLovDescFinCcyName() {
		return lovDescFinCcyName;
	}
	public void setLovDescFinCcyName(String lovDescFinCcyName) {
		this.lovDescFinCcyName = lovDescFinCcyName;
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

	public String getRepayAccountId() {
		return repayAccountId;
	}
	public void setRepayAccountId(String repayAccountId) {
		this.repayAccountId = repayAccountId;
	}

	public String getFinAccount() {
		return finAccount;
	}
	public void setFinAccount(String finAccount) {
		this.finAccount = finAccount;
	}

	public String getFinCustPftAccount() {
		return finCustPftAccount;
	}
	public void setFinCustPftAccount(String finCustPftAccount) {
		this.finCustPftAccount = finCustPftAccount;
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
	
}
