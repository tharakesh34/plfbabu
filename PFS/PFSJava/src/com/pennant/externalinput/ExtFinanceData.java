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
	private BigDecimal finAmount = new BigDecimal(0);
	private BigDecimal finAssetValue = new BigDecimal(0);
	private String disbAccountId;
	private String repayAccountId;
	private String finAccount;
	private String finCustPftAccount;
	private String finSourceID = null;
	private int numberOfTerms = 0;
	private String lovDescCustCIF;
	private int defferments;
	private int frqDefferments;
	private String finCommitmentRef;
	private String depreciationFrq;
	private Date nextDepDate;
	private Date finContractDate;
	private String grcRateBasis;
	private Date grcPeriodEndDate;
	private boolean allowGrcPeriod = false;
	private String graceBaseRate;
	private String graceSpecialRate;
	private BigDecimal grcMargin = new BigDecimal(0);
	private BigDecimal grcPftRate = new BigDecimal(0);
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
	private BigDecimal repayMargin = new BigDecimal(0);
	private BigDecimal repayProfitRate = new BigDecimal(0);
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
	private BigDecimal downPayment;
	private BigDecimal reqRepayAmount = new BigDecimal(0);
	private boolean alwIndRate = false;
	private String indBaseRate;
	private boolean calculateRepay = false;
	private boolean equalRepay = true;
	private String recordStatus;
	private String errDesc;

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

	public String getDisbAccountId() {
		return disbAccountId;
	}

	public void setDisbAccountId(String disbAccountId) {
		this.disbAccountId = disbAccountId;
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

	public String getDepreciationFrq() {
		return depreciationFrq;
	}

	public void setDepreciationFrq(String depreciationFrq) {
		this.depreciationFrq = depreciationFrq;
	}

	public Date getNextDepDate() {
		return nextDepDate;
	}

	public void setNextDepDate(Date nextDepDate) {
		this.nextDepDate = nextDepDate;
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

	public boolean isAlwIndRate() {
		return alwIndRate;
	}

	public void setAlwIndRate(boolean alwIndRate) {
		this.alwIndRate = alwIndRate;
	}

	public String getIndBaseRate() {
		return indBaseRate;
	}

	public void setIndBaseRate(String indBaseRate) {
		this.indBaseRate = indBaseRate;
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

}
