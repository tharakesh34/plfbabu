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
 * * FileName : WIFFinanceScheduleDetail.java * * Author : PENNANT TECHONOLOGIES * * Creation Date : 12-11-2011 * *
 * Modified Date : 12-11-2011 * * Description : * *
 ******************************************************************************************** 
 * Date Author Version Comments *
 ******************************************************************************************** 
 * 12-11-2011 Pennant 0.1 * * * * * * * * *
 ******************************************************************************************** 
 */
package com.pennant.backend.model.finance;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Date;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

import com.pennanttech.pennapps.core.model.ErrorDetail;
import com.pennanttech.pennapps.core.model.LoggedInUser;
import com.pennanttech.pff.core.model.AbstractWorkflowEntity;

/**
 * Model class for the <b>WIFFinanceScheduleDetail table</b>.<br>
 * 
 */
@XmlType(propOrder = { "schDate", "profitCalc", "profitSchd", "schdPftPaid", "principalSchd", "schdPriPaid", "feeSchd",
		"tDSAmount", "repayAmount", "closingBalance" })
@XmlAccessorType(XmlAccessType.NONE)
public class FinanceScheduleDetail extends AbstractWorkflowEntity {
	private static final long serialVersionUID = 1L;

	private String finReference = null;
	private int schSeq = 0;
	@XmlElement
	private Date schDate;
	private Date defSchdDate;
	private long logKey;
	private int instNumber = 0;
	private boolean pftOnSchDate = false;
	private boolean cpzOnSchDate = false;
	private boolean repayOnSchDate = false;
	private boolean rvwOnSchDate = false;
	private boolean disbOnSchDate = false;
	private boolean rolloverOnSchDate = false;
	private boolean downpaymentOnSchDate = false;
	private String bpiOrHoliday = "";
	private boolean frqDate = false;
	private BigDecimal balanceForPftCal = BigDecimal.ZERO;
	private String baseRate = null;
	private String splRate = "";
	private BigDecimal mrgRate = BigDecimal.ZERO;
	private BigDecimal actRate = BigDecimal.ZERO;
	private BigDecimal calculatedRate = BigDecimal.ZERO;
	private int noOfDays;
	private boolean calOnIndRate = false;
	private BigDecimal dayFactor = BigDecimal.ZERO;
	@XmlElement(name="pftAmount")
	private BigDecimal profitCalc = BigDecimal.ZERO;
	@XmlElement(name="schdPft")
	private BigDecimal profitSchd = BigDecimal.ZERO;
	@XmlElement(name="schdPri")
	private BigDecimal principalSchd = BigDecimal.ZERO;
	private BigDecimal rolloverAmount = BigDecimal.ZERO;
	@XmlElement(name="totalAmount")
	private BigDecimal repayAmount = BigDecimal.ZERO;
	private BigDecimal profitBalance = BigDecimal.ZERO;
	private BigDecimal disbAmount = BigDecimal.ZERO;
	private BigDecimal downPaymentAmount = BigDecimal.ZERO;
	private BigDecimal feeChargeAmt = BigDecimal.ZERO;
	private BigDecimal insuranceAmt = BigDecimal.ZERO;

	private BigDecimal refundOrWaiver = BigDecimal.ZERO;
	private BigDecimal cpzAmount = BigDecimal.ZERO;
	@XmlElement(name="endBal")
	private BigDecimal closingBalance = BigDecimal.ZERO;

	private BigDecimal profitFraction = BigDecimal.ZERO;
	private BigDecimal prvRepayAmount = BigDecimal.ZERO;
	@XmlElement
	private BigDecimal schdPftPaid = BigDecimal.ZERO;
	@XmlElement
	private BigDecimal schdPriPaid = BigDecimal.ZERO;
	private BigDecimal rolloverAmountPaid = BigDecimal.ZERO;
	private boolean schPftPaid = false;
	private boolean schPriPaid = false;
	private String schdMethod = null;
	private String specifier;
	private BigDecimal earlyPaid = BigDecimal.ZERO;
	private BigDecimal earlyPaidBal = BigDecimal.ZERO;
	private BigDecimal writeoffPrincipal = BigDecimal.ZERO;
	private BigDecimal writeoffProfit = BigDecimal.ZERO;

	private BigDecimal orgPft = BigDecimal.ZERO;
	private BigDecimal orgPri = BigDecimal.ZERO;
	private BigDecimal orgEndBal = BigDecimal.ZERO;
	private BigDecimal orgPlanPft = BigDecimal.ZERO;

	//Advised profit Rates
	private String advBaseRate;
	private BigDecimal advMargin = BigDecimal.ZERO;
	private BigDecimal advPftRate = BigDecimal.ZERO;
	private BigDecimal advCalRate = BigDecimal.ZERO;
	private BigDecimal advProfit = BigDecimal.ZERO;
	private BigDecimal advRepayAmount = BigDecimal.ZERO;

	// Ijarah External Charges
	private BigDecimal SuplRent = BigDecimal.ZERO;
	private BigDecimal IncrCost = BigDecimal.ZERO;
	private BigDecimal SuplRentPaid = BigDecimal.ZERO;
	private BigDecimal IncrCostPaid = BigDecimal.ZERO;

	//Fee Details on Schedule Basis
	@XmlElement
	private BigDecimal feeSchd = BigDecimal.ZERO;
	private BigDecimal schdFeePaid = BigDecimal.ZERO;
	private BigDecimal schdFeeOS = BigDecimal.ZERO;
	private BigDecimal insSchd = BigDecimal.ZERO;
	private BigDecimal schdInsPaid = BigDecimal.ZERO;

	@XmlElement
	private BigDecimal tDSAmount = BigDecimal.ZERO;
	private BigDecimal tDSPaid = BigDecimal.ZERO;
	private String pftDaysBasis;
	private BigDecimal writeoffIns = BigDecimal.ZERO;
	private BigDecimal writeoffCrIns = BigDecimal.ZERO;
	private BigDecimal writeoffIncrCost = BigDecimal.ZERO;
	private BigDecimal writeoffSuplRent = BigDecimal.ZERO;
	private BigDecimal writeoffSchFee = BigDecimal.ZERO;
	private BigDecimal rebate = BigDecimal.ZERO;
	private String 	   finCcy;
	private BigDecimal partialPaidAmt = BigDecimal.ZERO;
	
	private long presentmentId ;
	private boolean newRecord = false;
	private String lovValue;
	private FinanceScheduleDetail befImage;

	private LoggedInUser userDetails;
	private boolean repayComplete = false;
	private ArrayList<ErrorDetail> errorDetails = new ArrayList<ErrorDetail>();
	
	//GST
	private BigDecimal feeTax = BigDecimal.ZERO;
	private BigDecimal subventionAmount = BigDecimal.ZERO;
	
	public FinanceScheduleDetail(Date schDate, boolean repayOnSchDate, BigDecimal actRate) {
		super();
		this.schDate = schDate;
		this.repayOnSchDate = repayOnSchDate;
		this.actRate = actRate;
	}

	public boolean isNew() {
		return isNewRecord();
	}

	public FinanceScheduleDetail() {
		super(); 
	}

	public FinanceScheduleDetail(String id) {
		super(); 
		this.setId(id);
	}

	// Getter and Setter methods

	public String getId() {
		return finReference;
	}

	public void setId(String id) {
		this.finReference = id;
	}

	public String getFinReference() {
		return finReference;
	}

	public void setFinReference(String finReference) {
		this.finReference = finReference;
	}

	public Date getSchDate() {
		return schDate;
	}

	public void setSchDate(Date schDate) {
		this.schDate = schDate;
	}

	public int getSchSeq() {
		return schSeq;
	}

	public void setSchSeq(int schSeq) {
		this.schSeq = schSeq;
	}

	public int getInstNumber() {
		return instNumber;
	}

	public void setInstNumber(int instNumber) {
		this.instNumber = instNumber;
	}

	public boolean isPftOnSchDate() {
		return pftOnSchDate;
	}

	public void setPftOnSchDate(boolean pftOnSchDate) {
		this.pftOnSchDate = pftOnSchDate;
	}

	public boolean isCpzOnSchDate() {
		return cpzOnSchDate;
	}

	public void setCpzOnSchDate(boolean cpzOnSchDate) {
		this.cpzOnSchDate = cpzOnSchDate;
	}

	public boolean isRepayOnSchDate() {
		return repayOnSchDate;
	}

	public void setRepayOnSchDate(boolean repayOnSchDate) {
		this.repayOnSchDate = repayOnSchDate;
	}

	public boolean isRvwOnSchDate() {
		return rvwOnSchDate;
	}

	public void setRvwOnSchDate(boolean rvwOnSchDate) {
		this.rvwOnSchDate = rvwOnSchDate;
	}

	public boolean isDisbOnSchDate() {
		return disbOnSchDate;
	}

	public void setDisbOnSchDate(boolean disbOnSchDate) {
		this.disbOnSchDate = disbOnSchDate;
	}

	public boolean isDownpaymentOnSchDate() {
		return downpaymentOnSchDate;
	}

	public void setDownpaymentOnSchDate(boolean downpaymentOnSchDate) {
		this.downpaymentOnSchDate = downpaymentOnSchDate;
	}

	public String getBpiOrHoliday() {
		return bpiOrHoliday;
	}

	public void setBpiOrHoliday(String bpiOrHoliday) {
		this.bpiOrHoliday = bpiOrHoliday;
	}
	
	public boolean isFrqDate() {
		return frqDate;
	}

	public void setFrqDate(boolean frqDate) {
		this.frqDate = frqDate;
	}

	public BigDecimal getBalanceForPftCal() {
		return balanceForPftCal;
	}

	public void setBalanceForPftCal(BigDecimal balanceForPftCal) {
		this.balanceForPftCal = balanceForPftCal;
	}

	public String getBaseRate() {
		return baseRate;
	}

	public void setBaseRate(String baseRate) {
		this.baseRate = baseRate;
	}

	public String getSplRate() {
		return splRate;
	}

	public void setSplRate(String splRate) {
		this.splRate = splRate;
	}

	public BigDecimal getMrgRate() {
		return mrgRate;
	}

	public void setMrgRate(BigDecimal mrgRate) {
		this.mrgRate = mrgRate;
	}

	public BigDecimal getActRate() {
		return actRate;
	}

	public void setActRate(BigDecimal actRate) {
		this.actRate = actRate;
	}

	public BigDecimal getCalculatedRate() {
		return calculatedRate;
	}
	public void setCalculatedRate(BigDecimal calculatedRate) {
		this.calculatedRate = calculatedRate;
	}

	public int getNoOfDays() {
		return noOfDays;
	}

	public void setNoOfDays(int noOfDays) {
		this.noOfDays = noOfDays;
	}

	public boolean isCalOnIndRate() {
		return calOnIndRate;
	}

	public void setCalOnIndRate(boolean calOnIndRate) {
		this.calOnIndRate = calOnIndRate;
	}

	public BigDecimal getDayFactor() {
		return dayFactor;
	}

	public void setDayFactor(BigDecimal dayFactor) {
		this.dayFactor = dayFactor.setScale(9, RoundingMode.DOWN);
	}

	public BigDecimal getProfitCalc() {
		return profitCalc;
	}

	public void setProfitCalc(BigDecimal profitCalc) {
		this.profitCalc = profitCalc;
	}

	public BigDecimal getProfitSchd() {
		return profitSchd;
	}

	public void setProfitSchd(BigDecimal profitSchd) {
		this.profitSchd = profitSchd;
	}

	public BigDecimal getPrincipalSchd() {
		return principalSchd;
	}

	public void setPrincipalSchd(BigDecimal principalSchd) {
		this.principalSchd = principalSchd;
	}

	public BigDecimal getRepayAmount() {
		return repayAmount;
	}

	public void setRepayAmount(BigDecimal repayAmount) {
		this.repayAmount = repayAmount;
	}

	public BigDecimal getProfitBalance() {
		return profitBalance;
	}

	public void setProfitBalance(BigDecimal profitBalance) {
		this.profitBalance = profitBalance;
	}

	public BigDecimal getDisbAmount() {
		return disbAmount;
	}

	public void setDisbAmount(BigDecimal disbAmount) {
		this.disbAmount = disbAmount;
	}

	public BigDecimal getDownPaymentAmount() {
		return downPaymentAmount;
	}

	public void setDownPaymentAmount(BigDecimal downPaymentAmount) {
		this.downPaymentAmount = downPaymentAmount;
	}

	public BigDecimal getCpzAmount() {
		return cpzAmount;
	}

	public void setCpzAmount(BigDecimal cpzAmount) {
		this.cpzAmount = cpzAmount;
	}

	public BigDecimal getClosingBalance() {
		return closingBalance;
	}

	public void setClosingBalance(BigDecimal closingBalance) {
		this.closingBalance = closingBalance;
	}

	public BigDecimal getProfitFraction() {
		return profitFraction;
	}

	public void setProfitFraction(BigDecimal profitFraction) {
		this.profitFraction = profitFraction.setScale(9, RoundingMode.DOWN);
	}

	public BigDecimal getPrvRepayAmount() {
		return prvRepayAmount;
	}

	public void setPrvRepayAmount(BigDecimal prvRepayAmount) {
		this.prvRepayAmount = prvRepayAmount;
	}

	public BigDecimal getSchdPriPaid() {
		return schdPriPaid;
	}

	public void setSchdPriPaid(BigDecimal schdPriPaid) {
		this.schdPriPaid = schdPriPaid;
	}

	/**
	 * @return the defSchdDate
	 */
	public Date getDefSchdDate() {
		return defSchdDate;
	}

	/**
	 * @param defSchdDate
	 *            the defSchdDate to set
	 */
	public void setDefSchdDate(Date defSchdDate) {
		this.defSchdDate = defSchdDate;
	}

	/**
	 * @return the schdPftPaid
	 */
	public BigDecimal getSchdPftPaid() {
		return schdPftPaid;
	}

	/**
	 * @param schdPftPaid
	 *            the schdPftPaid to set
	 */
	public void setSchdPftPaid(BigDecimal schdPftPaid) {
		this.schdPftPaid = schdPftPaid;
	}

	public boolean isSchPftPaid() {
		return schPftPaid;
	}

	public void setSchPftPaid(boolean schPftPaid) {
		this.schPftPaid = schPftPaid;
	}

	public boolean isSchPriPaid() {
		return schPriPaid;
	}

	public void setSchPriPaid(boolean schPriPaid) {
		this.schPriPaid = schPriPaid;
	}

	public String getSchdMethod() {
		return schdMethod;
	}

	public void setSchdMethod(String schdMethod) {
		this.schdMethod = schdMethod;
	}

	public String getSpecifier() {
		return specifier;
	}
	public void setSpecifier(String specifier) {
		this.specifier = specifier;
	}

	public BigDecimal getEarlyPaid() {
		return earlyPaid;
	}
	public void setEarlyPaid(BigDecimal earlyPaid) {
		this.earlyPaid = earlyPaid;
	}

	public BigDecimal getEarlyPaidBal() {
		return earlyPaidBal;
	}
	public void setEarlyPaidBal(BigDecimal earlyPaidBal) {
		this.earlyPaidBal = earlyPaidBal;
	}

	public BigDecimal getWriteoffPrincipal() {
		return writeoffPrincipal;
	}
	public void setWriteoffPrincipal(BigDecimal writeoffPrincipal) {
		this.writeoffPrincipal = writeoffPrincipal;
	}

	public BigDecimal getWriteoffProfit() {
		return writeoffProfit;
	}
	public void setWriteoffProfit(BigDecimal writeoffProfit) {
		this.writeoffProfit = writeoffProfit;
	}

	public boolean isNewRecord() {
		return newRecord;
	}

	public void setNewRecord(boolean newRecord) {
		this.newRecord = newRecord;
	}

	public String getLovValue() {
		return lovValue;
	}

	public void setLovValue(String lovValue) {
		this.lovValue = lovValue;
	}

	public FinanceScheduleDetail getBefImage() {
		return this.befImage;
	}

	public void setBefImage(FinanceScheduleDetail beforeImage) {
		this.befImage = beforeImage;
	}

	public LoggedInUser getUserDetails() {
		return userDetails;
	}

	public void setUserDetails(LoggedInUser userDetails) {
		this.userDetails = userDetails;
	}

	public ArrayList<ErrorDetail> getErrorDetails() {
		return errorDetails;
	}

	public void setErrorDetails(ArrayList<ErrorDetail> errorDetails) {
		this.errorDetails = errorDetails;
	}

	public void setFeeChargeAmt(BigDecimal feeChargeAmt) {
		this.feeChargeAmt = feeChargeAmt;
	}
	public BigDecimal getFeeChargeAmt() {
		return feeChargeAmt;
	}

	public BigDecimal getRefundOrWaiver() {
		return refundOrWaiver;
	}
	public void setRefundOrWaiver(BigDecimal refundOrWaiver) {
		this.refundOrWaiver = refundOrWaiver;
	}

	public boolean isRepayComplete() {
		return repayComplete;
	}
	public void setRepayComplete(boolean repayComplete) {
		this.repayComplete = repayComplete;
	}

	public void setLogKey(long logKey) {
		this.logKey = logKey;
	}
	public long getLogKey() {
		return logKey;
	}

	public BigDecimal getOrgPft() {
		return orgPft;
	}
	public void setOrgPft(BigDecimal orgPft) {
		this.orgPft = orgPft;
	}

	public BigDecimal getOrgPri() {
		return orgPri;
	}
	public void setOrgPri(BigDecimal orgPri) {
		this.orgPri = orgPri;
	}

	public BigDecimal getOrgEndBal() {
		return orgEndBal;
	}
	public void setOrgEndBal(BigDecimal orgEndBal) {
		this.orgEndBal = orgEndBal;
	}

	public BigDecimal getOrgPlanPft() {
		return orgPlanPft;
	}
	public void setOrgPlanPft(BigDecimal orgPlanPft) {
		this.orgPlanPft = orgPlanPft;
	}

	public BigDecimal getFeeSchd() {
		return feeSchd;
	}
	public void setFeeSchd(BigDecimal feeSchd) {
		this.feeSchd = feeSchd;
	}

	public BigDecimal getSchdFeePaid() {
		return schdFeePaid;
	}
	public void setSchdFeePaid(BigDecimal schdFeePaid) {
		this.schdFeePaid = schdFeePaid;
	}

	public BigDecimal getSchdFeeOS() {
		return schdFeeOS;
	}
	public void setSchdFeeOS(BigDecimal schdFeeOS) {
		this.schdFeeOS = schdFeeOS;
	}

	public String getAdvBaseRate() {
		return advBaseRate;
	}
	public void setAdvBaseRate(String advBaseRate) {
		this.advBaseRate = advBaseRate;
	}

	public BigDecimal getAdvMargin() {
		return advMargin;
	}
	public void setAdvMargin(BigDecimal advMargin) {
		this.advMargin = advMargin;
	}

	public BigDecimal getAdvPftRate() {
		return advPftRate;
	}
	public void setAdvPftRate(BigDecimal advPftRate) {
		this.advPftRate = advPftRate;
	}

	public BigDecimal getAdvCalRate() {
		return advCalRate;
	}
	public void setAdvCalRate(BigDecimal advCalRate) {
		this.advCalRate = advCalRate;
	}

	public BigDecimal getAdvProfit() {
		return advProfit;
	}
	public void setAdvProfit(BigDecimal advProfit) {
		this.advProfit = advProfit;
	}

	public BigDecimal getAdvRepayAmount() {
		return advRepayAmount;
	}
	public void setAdvRepayAmount(BigDecimal advRepayAmount) {
		this.advRepayAmount = advRepayAmount;
	}

	public BigDecimal getSuplRent() {
		return SuplRent;
	}
	public void setSuplRent(BigDecimal suplRent) {
		SuplRent = suplRent;
	}

	public BigDecimal getIncrCost() {
		return IncrCost;
	}
	public void setIncrCost(BigDecimal incrCost) {
		IncrCost = incrCost;
	}

	public BigDecimal getSuplRentPaid() {
		return SuplRentPaid;
	}
	public void setSuplRentPaid(BigDecimal suplRentPaid) {
		SuplRentPaid = suplRentPaid;
	}

	public BigDecimal getIncrCostPaid() {
		return IncrCostPaid;
	}
	public void setIncrCostPaid(BigDecimal incrCostPaid) {
		IncrCostPaid = incrCostPaid;
	}

	public boolean isRolloverOnSchDate() {
		return rolloverOnSchDate;
	}
	public void setRolloverOnSchDate(boolean rolloverOnSchDate) {
		this.rolloverOnSchDate = rolloverOnSchDate;
	}

	public BigDecimal getRolloverAmount() {
		return rolloverAmount;
	}
	public void setRolloverAmount(BigDecimal rolloverAmount) {
		this.rolloverAmount = rolloverAmount;
	}

	public BigDecimal getRolloverAmountPaid() {
		return rolloverAmountPaid;
	}
	public void setRolloverAmountPaid(BigDecimal rolloverAmountPaid) {
		this.rolloverAmountPaid = rolloverAmountPaid;
	}

	public BigDecimal getTDSAmount() {
		return tDSAmount;
	}
	public void setTDSAmount(BigDecimal tDSAmount) {
		this.tDSAmount = tDSAmount;
	}

	public String getPftDaysBasis() {
		return pftDaysBasis;
	}
	public void setPftDaysBasis(String pftDaysBasis) {
		this.pftDaysBasis = pftDaysBasis;
	}

	public BigDecimal getWriteoffCrIns() {
		return writeoffCrIns;
	}

	public void setWriteoffCrIns(BigDecimal writeoffCrIns) {
		this.writeoffCrIns = writeoffCrIns;
	}

	public BigDecimal getWriteoffIncrCost() {
		return writeoffIncrCost;
	}

	public void setWriteoffIncrCost(BigDecimal writeoffIncrCost) {
		this.writeoffIncrCost = writeoffIncrCost;
	}

	public BigDecimal getWriteoffSuplRent() {
		return writeoffSuplRent;
	}

	public void setWriteoffSuplRent(BigDecimal writeoffSuplRent) {
		this.writeoffSuplRent = writeoffSuplRent;
	}

	public BigDecimal getWriteoffSchFee() {
		return writeoffSchFee;
	}

	public void setWriteoffSchFee(BigDecimal writeoffSchFee) {
		this.writeoffSchFee = writeoffSchFee;
	}

	public BigDecimal getRebate() {
		return rebate;
	}

	public void setRebate(BigDecimal rebate) {
		this.rebate = rebate;
	}

	public String getFinCcy() {
		return finCcy;
	}

	public void setFinCcy(String finCcy) {
		this.finCcy = finCcy;
	}

	public BigDecimal getInsuranceAmt() {
		return insuranceAmt;
	}
	public void setInsuranceAmt(BigDecimal insuranceAmt) {
		this.insuranceAmt = insuranceAmt;
	}

	public BigDecimal getInsSchd() {
		return insSchd;
	}
	public void setInsSchd(BigDecimal insFeeSchd) {
		this.insSchd = insFeeSchd;
	}

	public BigDecimal getSchdInsPaid() {
		return schdInsPaid;
	}
	public void setSchdInsPaid(BigDecimal schdInsPaid) {
		this.schdInsPaid = schdInsPaid;
	}

	public BigDecimal getWriteoffIns() {
		return writeoffIns;
	}
	public void setWriteoffIns(BigDecimal writeoffIns) {
		this.writeoffIns = writeoffIns;
	}

	public long getPresentmentId() {
		return presentmentId;
	}

	public void setPresentmentId(long presentmentId) {
		this.presentmentId = presentmentId;
	}

	public BigDecimal getTDSPaid() {
		return tDSPaid;
	}

	public void setTDSPaid(BigDecimal tDSPaid) {
		this.tDSPaid = tDSPaid;
	}

	public BigDecimal getPartialPaidAmt() {
		return partialPaidAmt;
	}

	public void setPartialPaidAmt(BigDecimal partialPaidAmt) {
		this.partialPaidAmt = partialPaidAmt;
	}
	
	//GST
	public BigDecimal getFeeTax() {
		return feeTax;
	}

	public void setFeeTax(BigDecimal feeTax) {
		this.feeTax = feeTax;
	}

	public BigDecimal getSubventionAmount() {
		return subventionAmount;
	}

	public void setSubventionAmount(BigDecimal subventionAmount) {
		this.subventionAmount = subventionAmount;
	}



}
