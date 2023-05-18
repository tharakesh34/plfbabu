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
 * * FileName : FinanceScheduleDetail.java * * Author : PENNANT TECHONOLOGIES * * Creation Date : 13-08-2012 * *
 * Modified Date : 13-08-2012 * * Description : * *
 ********************************************************************************************
 * Date Author Version Comments *
 ********************************************************************************************
 * 16-03-2012 Pennant 0.1 * * * * * * * * *
 ********************************************************************************************
 */
package com.pennant.backend.model.finance;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

import com.pennanttech.pennapps.core.model.AbstractWorkflowEntity;
import com.pennanttech.pennapps.core.model.ErrorDetail;
import com.pennanttech.pennapps.core.model.LoggedInUser;

/**
 * Model class for the <b>WIFFinanceScheduleDetail table</b>.<br>
 * 
 */
@XmlType(propOrder = { "schDate", "profitCalc", "profitSchd", "schdPftPaid", "principalSchd", "schdPriPaid", "feeSchd",
		"tDSAmount", "repayAmount", "closingBalance", "limitDrop", "dropLineLimit", "availableLimit" })
@XmlAccessorType(XmlAccessType.NONE)
public class FinanceScheduleDetail extends AbstractWorkflowEntity {
	private static final long serialVersionUID = 1L;

	private long finID;
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
	private BigDecimal dayFactor = BigDecimal.ZERO;
	@XmlElement(name = "pftAmount")
	private BigDecimal profitCalc = BigDecimal.ZERO;
	@XmlElement(name = "schdPft")
	private BigDecimal profitSchd = BigDecimal.ZERO;
	@XmlElement(name = "schdPri")
	private BigDecimal principalSchd = BigDecimal.ZERO;
	@XmlElement(name = "totalAmount")
	private BigDecimal repayAmount = BigDecimal.ZERO;
	private BigDecimal profitBalance = BigDecimal.ZERO;
	private BigDecimal disbAmount = BigDecimal.ZERO;
	private BigDecimal downPaymentAmount = BigDecimal.ZERO;
	private BigDecimal feeChargeAmt = BigDecimal.ZERO;

	private BigDecimal refundOrWaiver = BigDecimal.ZERO;
	private BigDecimal cpzAmount = BigDecimal.ZERO;
	private BigDecimal cpzBalance = BigDecimal.ZERO;
	@XmlElement(name = "endBal")
	private BigDecimal closingBalance = BigDecimal.ZERO;

	private BigDecimal profitFraction = BigDecimal.ZERO;
	private BigDecimal prvRepayAmount = BigDecimal.ZERO;
	@XmlElement
	private BigDecimal schdPftPaid = BigDecimal.ZERO;
	@XmlElement
	private BigDecimal schdPriPaid = BigDecimal.ZERO;
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

	// Fee Details on Schedule Basis
	@XmlElement
	private BigDecimal feeSchd = BigDecimal.ZERO;
	private BigDecimal schdFeePaid = BigDecimal.ZERO;
	private BigDecimal schdFeeOS = BigDecimal.ZERO;

	@XmlElement
	private BigDecimal tDSAmount = BigDecimal.ZERO;
	private BigDecimal tDSPaid = BigDecimal.ZERO;
	private String pftDaysBasis;
	private BigDecimal writeoffSchFee = BigDecimal.ZERO;
	private String finCcy;
	private BigDecimal partialPaidAmt = BigDecimal.ZERO;
	private boolean recalLock = false;

	private long presentmentId;
	private String lovValue;
	private FinanceScheduleDetail befImage;

	private LoggedInUser userDetails;
	private boolean repayComplete = false;
	private List<ErrorDetail> errorDetails = new ArrayList<>();

	// GST
	private BigDecimal feeTax = BigDecimal.ZERO;
	private BigDecimal subventionAmount = BigDecimal.ZERO;

	// Is TDS Applicable
	private boolean tDSApplicable = false;

	// Profit waiver
	private BigDecimal schdPftWaiver = BigDecimal.ZERO;

	// HybridFlexi
	@XmlElement(name = "limitDrop")
	private BigDecimal limitDrop = BigDecimal.ZERO;
	@XmlElement(name = "dropLineLimit")
	private BigDecimal oDLimit = BigDecimal.ZERO;
	@XmlElement(name = "availableLimit")
	private BigDecimal availableLimit = BigDecimal.ZERO;
	private String loanEMIStatus;

	public FinanceScheduleDetail(Date schDate, boolean repayOnSchDate, BigDecimal actRate) {
		super();
		this.schDate = schDate;
		this.repayOnSchDate = repayOnSchDate;
		this.actRate = actRate;
	}

	public FinanceScheduleDetail() {
		super();
	}

	public FinanceScheduleDetail copyEntity() {
		FinanceScheduleDetail entity = new FinanceScheduleDetail();
		entity.setFinID(this.finID);
		entity.setFinReference(this.finReference);
		entity.setSchSeq(this.schSeq);
		entity.setSchDate(this.schDate);
		entity.setDefSchdDate(this.defSchdDate);
		entity.setLogKey(this.logKey);
		entity.setInstNumber(this.instNumber);
		entity.setPftOnSchDate(this.pftOnSchDate);
		entity.setCpzOnSchDate(this.cpzOnSchDate);
		entity.setRepayOnSchDate(this.repayOnSchDate);
		entity.setRvwOnSchDate(this.rvwOnSchDate);
		entity.setDisbOnSchDate(this.disbOnSchDate);
		entity.setDownpaymentOnSchDate(this.downpaymentOnSchDate);
		entity.setBpiOrHoliday(this.bpiOrHoliday);
		entity.setFrqDate(this.frqDate);
		entity.setBalanceForPftCal(this.balanceForPftCal);
		entity.setBaseRate(this.baseRate);
		entity.setSplRate(this.splRate);
		entity.setMrgRate(this.mrgRate);
		entity.setActRate(this.actRate);
		entity.setCalculatedRate(this.calculatedRate);
		entity.setNoOfDays(this.noOfDays);
		entity.setDayFactor(this.dayFactor);
		entity.setProfitCalc(this.profitCalc);
		entity.setProfitSchd(this.profitSchd);
		entity.setPrincipalSchd(this.principalSchd);
		entity.setRepayAmount(this.repayAmount);
		entity.setProfitBalance(this.profitBalance);
		entity.setDisbAmount(this.disbAmount);
		entity.setDownPaymentAmount(this.downPaymentAmount);
		entity.setFeeChargeAmt(this.feeChargeAmt);
		entity.setRefundOrWaiver(this.refundOrWaiver);
		entity.setCpzAmount(this.cpzAmount);
		entity.setCpzBalance(this.cpzBalance);
		entity.setClosingBalance(this.closingBalance);
		entity.setProfitFraction(this.profitFraction);
		entity.setPrvRepayAmount(this.prvRepayAmount);
		entity.setSchdPftPaid(this.schdPftPaid);
		entity.setSchdPriPaid(this.schdPriPaid);
		entity.setSchPftPaid(this.schPftPaid);
		entity.setSchPriPaid(this.schPriPaid);
		entity.setSchdMethod(this.schdMethod);
		entity.setSpecifier(this.specifier);
		entity.setEarlyPaid(this.earlyPaid);
		entity.setEarlyPaidBal(this.earlyPaidBal);
		entity.setWriteoffPrincipal(this.writeoffPrincipal);
		entity.setWriteoffProfit(this.writeoffProfit);
		entity.setOrgPft(this.orgPft);
		entity.setOrgPri(this.orgPri);
		entity.setOrgEndBal(this.orgEndBal);
		entity.setOrgPlanPft(this.orgPlanPft);
		entity.setFeeSchd(this.feeSchd);
		entity.setSchdFeePaid(this.schdFeePaid);
		entity.setSchdFeeOS(this.schdFeeOS);
		entity.setTDSAmount(this.tDSAmount);
		entity.setTDSPaid(this.tDSPaid);
		entity.setPftDaysBasis(this.pftDaysBasis);
		entity.setWriteoffSchFee(this.writeoffSchFee);
		entity.setFinCcy(this.finCcy);
		entity.setPartialPaidAmt(this.partialPaidAmt);
		entity.setRecalLock(this.recalLock);
		entity.setPresentmentId(this.presentmentId);
		entity.setNewRecord(super.isNewRecord());
		entity.setLovValue(this.lovValue);
		entity.setBefImage(this.befImage == null ? null : this.befImage.copyEntity());
		entity.setUserDetails(this.userDetails);
		entity.setRepayComplete(this.repayComplete);
		this.errorDetails.stream().forEach(e -> entity.getErrorDetails().add(e));
		entity.setFeeTax(this.feeTax);
		entity.setSubventionAmount(this.subventionAmount);
		entity.setTDSApplicable(this.tDSApplicable);
		entity.setSchdPftWaiver(this.schdPftWaiver);
		entity.setLimitDrop(this.limitDrop);
		entity.setODLimit(this.oDLimit);
		entity.setAvailableLimit(this.availableLimit);
		entity.setRecordStatus(super.getRecordStatus());
		entity.setRoleCode(super.getRoleCode());
		entity.setNextRoleCode(super.getNextRoleCode());
		entity.setTaskId(super.getTaskId());
		entity.setNextTaskId(super.getNextTaskId());
		entity.setRecordType(super.getRecordType());
		entity.setWorkflowId(super.getWorkflowId());
		entity.setUserAction(super.getUserAction());
		entity.setVersion(super.getVersion());
		entity.setLastMntBy(super.getLastMntBy());
		entity.setLastMntOn(super.getLastMntOn());
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
	 * @param defSchdDate the defSchdDate to set
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
	 * @param schdPftPaid the schdPftPaid to set
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

	public List<ErrorDetail> getErrorDetails() {
		return errorDetails;
	}

	public void setErrorDetails(List<ErrorDetail> errorDetails) {
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

	public BigDecimal getWriteoffSchFee() {
		return writeoffSchFee;
	}

	public void setWriteoffSchFee(BigDecimal writeoffSchFee) {
		this.writeoffSchFee = writeoffSchFee;
	}

	public String getFinCcy() {
		return finCcy;
	}

	public void setFinCcy(String finCcy) {
		this.finCcy = finCcy;
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

	public BigDecimal getLimitDrop() {
		return limitDrop;
	}

	public void setLimitDrop(BigDecimal limitDrop) {
		this.limitDrop = limitDrop;
	}

	// GST
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

	public boolean isRecalLock() {
		return recalLock;
	}

	public void setRecalLock(boolean recalLock) {
		this.recalLock = recalLock;
	}

	public BigDecimal getODLimit() {
		return oDLimit;
	}

	public void setODLimit(BigDecimal oDLimit) {
		this.oDLimit = oDLimit;
	}

	public BigDecimal getAvailableLimit() {
		return availableLimit;
	}

	public void setAvailableLimit(BigDecimal availableLimit) {
		this.availableLimit = availableLimit;
	}

	public BigDecimal getCpzBalance() {
		return cpzBalance;
	}

	public void setCpzBalance(BigDecimal cpzBalance) {
		this.cpzBalance = cpzBalance;
	}

	public boolean isTDSApplicable() {
		return tDSApplicable;
	}

	public void setTDSApplicable(boolean tDSApplicable) {
		this.tDSApplicable = tDSApplicable;
	}

	public BigDecimal getSchdPftWaiver() {
		return schdPftWaiver;
	}

	public void setSchdPftWaiver(BigDecimal schdPftWaiver) {
		this.schdPftWaiver = schdPftWaiver;
	}

	public String getLoanEMIStatus() {
		return loanEMIStatus;
	}

	public void setLoanEMIStatus(String loanEMIStatus) {
		this.loanEMIStatus = loanEMIStatus;
	}

}
