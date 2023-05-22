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

package com.pennant.externalinput;

import java.math.BigDecimal;
import java.util.Date;

/**
 * Model class for the <b>FinanceMain table</b>.<br>
 * 
 */
public class ExtFinanceData implements java.io.Serializable {

	private static final long serialVersionUID = 1L;
	private String finReference = null;
	private String finBranch = null;
	private String finType;
	private String finRemarks;
	private String finCcy;
	private String scheduleMethod;
	private String profitDaysBasis;
	private Date finStartDate;
	private BigDecimal finAmount = BigDecimal.ZERO;
	private BigDecimal finAssetValue = BigDecimal.ZERO;
	private String finSourceID = null;
	private int numberOfTerms = 0;
	private String lovDescCustCIF;
	private int defferments;
	private int frqDefferments;
	private String finCommitmentRef;
	private Date finContractDate;
	private String grcRateBasis;
	private Date grcPeriodEndDate;
	private boolean allowGrcPeriod = false;
	private String graceBaseRate;
	private String graceSpecialRate;
	private BigDecimal grcMargin = BigDecimal.ZERO;
	private BigDecimal grcPftRate = BigDecimal.ZERO;
	private String grcPftFrq;
	private Date nextGrcPftDate;
	private boolean allowGrcPftRvw = false;
	private String grcPftRvwFrq;
	private Date nextGrcPftRvwDate;
	private boolean allowGrcCpz = false;
	private String grcCpzFrq;
	private Date nextGrcCpzDate;
	private boolean cpzAtGraceEnd = false;
	private boolean allowGrcRepay = false;
	private String grcSchdMthd;
	private String repayRateBasis;
	private String repayBaseRate;
	private String repaySpecialRate;
	private BigDecimal repayMargin = BigDecimal.ZERO;
	private BigDecimal repayProfitRate = BigDecimal.ZERO;
	private String repayFrq;
	private Date nextRepayDate;
	private String repayPftFrq;
	private Date nextRepayPftDate;
	private boolean allowRepayRvw = false;
	private String repayRvwFrq;
	private Date nextRepayRvwDate;
	private boolean allowRepayCpz = false;
	private String repayCpzFrq;
	private Date nextRepayCpzDate;
	private Date maturityDate;
	private BigDecimal dpToBank;
	private BigDecimal dpToSupplier;
	private BigDecimal downPayment;
	private BigDecimal reqRepayAmount = BigDecimal.ZERO;
	private boolean calculateRepay = false;
	private boolean equalRepay = true;
	private String recordStatus;
	private String errDesc;
	private int planDeferCount;
	private boolean stepFinance = false;

	private BigDecimal expGracePft = BigDecimal.ZERO;
	private BigDecimal expGraceCpz = BigDecimal.ZERO;
	private BigDecimal expGorssGracePft = BigDecimal.ZERO;
	private BigDecimal expRepayPft = BigDecimal.ZERO;
	private BigDecimal expTotalPft = BigDecimal.ZERO;
	private BigDecimal expFirstInst = BigDecimal.ZERO;
	private BigDecimal expLastInst = BigDecimal.ZERO;
	private BigDecimal expLastInstPft = BigDecimal.ZERO;
	private BigDecimal expRateAtStart = BigDecimal.ZERO;
	private BigDecimal expRateAtGrcEnd = BigDecimal.ZERO;

	public ExtFinanceData() {
	    super();
	}

	public String getFinReference() {
		return finReference;
	}

	public void setFinReference(String finReference) {
		this.finReference = finReference;
	}

	public String getFinBranch() {
		return finBranch;
	}

	public void setFinBranch(String finBranch) {
		this.finBranch = finBranch;
	}

	public String getFinType() {
		return finType;
	}

	public void setFinType(String finType) {
		this.finType = finType;
	}

	public String getFinRemarks() {
		return finRemarks;
	}

	public void setFinRemarks(String finRemarks) {
		this.finRemarks = finRemarks;
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

	public BigDecimal getFinAmount() {
		return finAmount;
	}

	public void setFinAmount(BigDecimal finAmount) {
		this.finAmount = finAmount;
	}

	public BigDecimal getFinAssetValue() {
		return finAssetValue;
	}

	public void setFinAssetValue(BigDecimal finAssetValue) {
		this.finAssetValue = finAssetValue;
	}

	public String getFinSourceID() {
		return finSourceID;
	}

	public void setFinSourceID(String finSourceID) {
		this.finSourceID = finSourceID;
	}

	public int getNumberOfTerms() {
		return numberOfTerms;
	}

	public void setNumberOfTerms(int numberOfTerms) {
		this.numberOfTerms = numberOfTerms;
	}

	public String getLovDescCustCIF() {
		return lovDescCustCIF;
	}

	public void setLovDescCustCIF(String lovDescCustCIF) {
		this.lovDescCustCIF = lovDescCustCIF;
	}

	public int getDefferments() {
		return defferments;
	}

	public void setDefferments(int defferments) {
		this.defferments = defferments;
	}

	public int getFrqDefferments() {
		return frqDefferments;
	}

	public void setFrqDefferments(int frqDefferments) {
		this.frqDefferments = frqDefferments;
	}

	public String getFinCommitmentRef() {
		return finCommitmentRef;
	}

	public void setFinCommitmentRef(String finCommitmentRef) {
		this.finCommitmentRef = finCommitmentRef;
	}

	public Date getFinContractDate() {
		return finContractDate;
	}

	public void setFinContractDate(Date finContractDate) {
		this.finContractDate = finContractDate;
	}

	public String getGrcRateBasis() {
		return grcRateBasis;
	}

	public void setGrcRateBasis(String grcRateBasis) {
		this.grcRateBasis = grcRateBasis;
	}

	public Date getGrcPeriodEndDate() {
		return grcPeriodEndDate;
	}

	public void setGrcPeriodEndDate(Date grcPeriodEndDate) {
		this.grcPeriodEndDate = grcPeriodEndDate;
	}

	public boolean isAllowGrcPeriod() {
		return allowGrcPeriod;
	}

	public void setAllowGrcPeriod(boolean allowGrcPeriod) {
		this.allowGrcPeriod = allowGrcPeriod;
	}

	public String getGraceBaseRate() {
		return graceBaseRate;
	}

	public void setGraceBaseRate(String graceBaseRate) {
		this.graceBaseRate = graceBaseRate;
	}

	public String getGraceSpecialRate() {
		return graceSpecialRate;
	}

	public void setGraceSpecialRate(String graceSpecialRate) {
		this.graceSpecialRate = graceSpecialRate;
	}

	public BigDecimal getGrcMargin() {
		return grcMargin;
	}

	public void setGrcMargin(BigDecimal grcMargin) {
		this.grcMargin = grcMargin;
	}

	public BigDecimal getGrcPftRate() {
		return grcPftRate;
	}

	public void setGrcPftRate(BigDecimal grcPftRate) {
		this.grcPftRate = grcPftRate;
	}

	public String getGrcPftFrq() {
		return grcPftFrq;
	}

	public void setGrcPftFrq(String grcPftFrq) {
		this.grcPftFrq = grcPftFrq;
	}

	public Date getNextGrcPftDate() {
		return nextGrcPftDate;
	}

	public void setNextGrcPftDate(Date nextGrcPftDate) {
		this.nextGrcPftDate = nextGrcPftDate;
	}

	public boolean isAllowGrcPftRvw() {
		return allowGrcPftRvw;
	}

	public void setAllowGrcPftRvw(boolean allowGrcPftRvw) {
		this.allowGrcPftRvw = allowGrcPftRvw;
	}

	public String getGrcPftRvwFrq() {
		return grcPftRvwFrq;
	}

	public void setGrcPftRvwFrq(String grcPftRvwFrq) {
		this.grcPftRvwFrq = grcPftRvwFrq;
	}

	public Date getNextGrcPftRvwDate() {
		return nextGrcPftRvwDate;
	}

	public void setNextGrcPftRvwDate(Date nextGrcPftRvwDate) {
		this.nextGrcPftRvwDate = nextGrcPftRvwDate;
	}

	public boolean isAllowGrcCpz() {
		return allowGrcCpz;
	}

	public void setAllowGrcCpz(boolean allowGrcCpz) {
		this.allowGrcCpz = allowGrcCpz;
	}

	public String getGrcCpzFrq() {
		return grcCpzFrq;
	}

	public void setGrcCpzFrq(String grcCpzFrq) {
		this.grcCpzFrq = grcCpzFrq;
	}

	public Date getNextGrcCpzDate() {
		return nextGrcCpzDate;
	}

	public void setNextGrcCpzDate(Date nextGrcCpzDate) {
		this.nextGrcCpzDate = nextGrcCpzDate;
	}

	public boolean isCpzAtGraceEnd() {
		return cpzAtGraceEnd;
	}

	public void setCpzAtGraceEnd(boolean cpzAtGraceEnd) {
		this.cpzAtGraceEnd = cpzAtGraceEnd;
	}

	public boolean isAllowGrcRepay() {
		return allowGrcRepay;
	}

	public void setAllowGrcRepay(boolean allowGrcRepay) {
		this.allowGrcRepay = allowGrcRepay;
	}

	public String getGrcSchdMthd() {
		return grcSchdMthd;
	}

	public void setGrcSchdMthd(String grcSchdMthd) {
		this.grcSchdMthd = grcSchdMthd;
	}

	public String getRepayRateBasis() {
		return repayRateBasis;
	}

	public void setRepayRateBasis(String repayRateBasis) {
		this.repayRateBasis = repayRateBasis;
	}

	public String getRepayBaseRate() {
		return repayBaseRate;
	}

	public void setRepayBaseRate(String repayBaseRate) {
		this.repayBaseRate = repayBaseRate;
	}

	public String getRepaySpecialRate() {
		return repaySpecialRate;
	}

	public void setRepaySpecialRate(String repaySpecialRate) {
		this.repaySpecialRate = repaySpecialRate;
	}

	public BigDecimal getRepayMargin() {
		return repayMargin;
	}

	public void setRepayMargin(BigDecimal repayMargin) {
		this.repayMargin = repayMargin;
	}

	public BigDecimal getRepayProfitRate() {
		return repayProfitRate;
	}

	public void setRepayProfitRate(BigDecimal repayProfitRate) {
		this.repayProfitRate = repayProfitRate;
	}

	public String getRepayFrq() {
		return repayFrq;
	}

	public void setRepayFrq(String repayFrq) {
		this.repayFrq = repayFrq;
	}

	public Date getNextRepayDate() {
		return nextRepayDate;
	}

	public void setNextRepayDate(Date nextRepayDate) {
		this.nextRepayDate = nextRepayDate;
	}

	public String getRepayPftFrq() {
		return repayPftFrq;
	}

	public void setRepayPftFrq(String repayPftFrq) {
		this.repayPftFrq = repayPftFrq;
	}

	public Date getNextRepayPftDate() {
		return nextRepayPftDate;
	}

	public void setNextRepayPftDate(Date nextRepayPftDate) {
		this.nextRepayPftDate = nextRepayPftDate;
	}

	public boolean isAllowRepayRvw() {
		return allowRepayRvw;
	}

	public void setAllowRepayRvw(boolean allowRepayRvw) {
		this.allowRepayRvw = allowRepayRvw;
	}

	public String getRepayRvwFrq() {
		return repayRvwFrq;
	}

	public void setRepayRvwFrq(String repayRvwFrq) {
		this.repayRvwFrq = repayRvwFrq;
	}

	public Date getNextRepayRvwDate() {
		return nextRepayRvwDate;
	}

	public void setNextRepayRvwDate(Date nextRepayRvwDate) {
		this.nextRepayRvwDate = nextRepayRvwDate;
	}

	public boolean isAllowRepayCpz() {
		return allowRepayCpz;
	}

	public void setAllowRepayCpz(boolean allowRepayCpz) {
		this.allowRepayCpz = allowRepayCpz;
	}

	public String getRepayCpzFrq() {
		return repayCpzFrq;
	}

	public void setRepayCpzFrq(String repayCpzFrq) {
		this.repayCpzFrq = repayCpzFrq;
	}

	public Date getNextRepayCpzDate() {
		return nextRepayCpzDate;
	}

	public void setNextRepayCpzDate(Date nextRepayCpzDate) {
		this.nextRepayCpzDate = nextRepayCpzDate;
	}

	public Date getMaturityDate() {
		return maturityDate;
	}

	public void setMaturityDate(Date maturityDate) {
		this.maturityDate = maturityDate;
	}

	public BigDecimal getDownPayment() {
		return downPayment;
	}

	public void setDownPayment(BigDecimal downPayment) {
		this.downPayment = downPayment;
	}

	public BigDecimal getReqRepayAmount() {
		return reqRepayAmount;
	}

	public void setReqRepayAmount(BigDecimal reqRepayAmount) {
		this.reqRepayAmount = reqRepayAmount;
	}

	public boolean isCalculateRepay() {
		return calculateRepay;
	}

	public void setCalculateRepay(boolean calculateRepay) {
		this.calculateRepay = calculateRepay;
	}

	public boolean isEqualRepay() {
		return equalRepay;
	}

	public void setEqualRepay(boolean equalRepay) {
		this.equalRepay = equalRepay;
	}

	public String getRecordStatus() {
		return recordStatus;
	}

	public void setRecordStatus(String recordStatus) {
		this.recordStatus = recordStatus;
	}

	public String getErrDesc() {
		return errDesc;
	}

	public void setErrDesc(String errDesc) {
		this.errDesc = errDesc;
	}

	public BigDecimal getDpToBank() {
		return dpToBank;
	}

	public void setDpToBank(BigDecimal dpToBank) {
		this.dpToBank = dpToBank;
	}

	public BigDecimal getDpToSupplier() {
		return dpToSupplier;
	}

	public void setDpToSupplier(BigDecimal dpToSupplier) {
		this.dpToSupplier = dpToSupplier;
	}

	public int getPlanDeferCount() {
		return planDeferCount;
	}

	public void setPlanDeferCount(int planDeferCount) {
		this.planDeferCount = planDeferCount;
	}

	public boolean isStepFinance() {
		return stepFinance;
	}

	public void setStepFinance(boolean stepFinance) {
		this.stepFinance = stepFinance;
	}

	public BigDecimal getExpGracePft() {
		return expGracePft;
	}

	public void setExpGracePft(BigDecimal expGracePft) {
		this.expGracePft = expGracePft;
	}

	public BigDecimal getExpGraceCpz() {
		return expGraceCpz;
	}

	public void setExpGraceCpz(BigDecimal expGraceCpz) {
		this.expGraceCpz = expGraceCpz;
	}

	public BigDecimal getExpGorssGracePft() {
		return expGorssGracePft;
	}

	public void setExpGorssGracePft(BigDecimal expGorssGracePft) {
		this.expGorssGracePft = expGorssGracePft;
	}

	public BigDecimal getExpRepayPft() {
		return expRepayPft;
	}

	public void setExpRepayPft(BigDecimal expRepayPft) {
		this.expRepayPft = expRepayPft;
	}

	public BigDecimal getExpTotalPft() {
		return expTotalPft;
	}

	public void setExpTotalPft(BigDecimal expTotalPft) {
		this.expTotalPft = expTotalPft;
	}

	public BigDecimal getExpFirstInst() {
		return expFirstInst;
	}

	public void setExpFirstInst(BigDecimal expFirstInst) {
		this.expFirstInst = expFirstInst;
	}

	public BigDecimal getExpLastInst() {
		return expLastInst;
	}

	public void setExpLastInst(BigDecimal expLastInst) {
		this.expLastInst = expLastInst;
	}

	public BigDecimal getExpLastInstPft() {
		return expLastInstPft;
	}

	public void setExpLastInstPft(BigDecimal expLastInstPft) {
		this.expLastInstPft = expLastInstPft;
	}

	public BigDecimal getExpRateAtStart() {
		return expRateAtStart;
	}

	public void setExpRateAtStart(BigDecimal expRateAtStart) {
		this.expRateAtStart = expRateAtStart;
	}

	public BigDecimal getExpRateAtGrcEnd() {
		return expRateAtGrcEnd;
	}

	public void setExpRateAtGrcEnd(BigDecimal expRateAtGrcEnd) {
		this.expRateAtGrcEnd = expRateAtGrcEnd;
	}

}
