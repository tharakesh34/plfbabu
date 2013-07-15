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

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.pennant.app.util.FrequencyUtil;
import com.pennant.backend.model.ErrorDetails;
import com.pennant.backend.model.LoginUserDetails;
import com.pennant.backend.model.rmtmasters.FinanceType;

/**
 * Model class for the <b>FinanceMain table</b>.<br>
 * 
 */
public class FinanceMain implements java.io.Serializable {

    private static final long serialVersionUID = -3026443763391506067L;
    
	private String finReference = null;
	private String finBranch = null;
	private String lovDescFinBranchName;
	private String finType;
	private String lovDescFinTypeName;
	private String finRemarks;
	private String finCcy;
	private String lovDescFinCcyName;
	private String scheduleMethod;
	private String lovDescScheduleMethodName;
	private String profitDaysBasis;
	private String lovDescProfitDaysBasisName;
	private Date finStartDate;
	private BigDecimal finAmount = new BigDecimal(0);
	private BigDecimal finAssetValue = new BigDecimal(0);
	private BigDecimal finCurrAssetValue = new BigDecimal(0);
	private String disbAccountId;
	private String repayAccountId;
	private String finAccount;
	private String finCustPftAccount;
	private String finSourceID = null;
	private int numberOfTerms = 0;
	
	private long custID;
	private String lovDescCustCIF;
	private String lovDescCustFName;
	private String lovDescCustLName;
	private String lovDescCustShrtName;
	private String lovDescCustAddrLine1;
	private String lovDescCustAddrLine2;
	private String lovDescCustAddrCity;
	private String lovDescCustAddrCountry;
	private String lovDescSalutationName;
	private String lovDescCustCtgTypeName;
	
	private int defferments;
	private int frqDefferments;
	private String finCommitmentRef;
	private String lovDescCommitmentRefName;
	private String depreciationFrq;
	private Date nextDepDate;
	private Date lastDepDate;
	private Date finContractDate;
	private BigDecimal feeChargeAmt;
	private BigDecimal curDisbursementAmt;
	
	private BigDecimal anualizedPercRate;
	private BigDecimal effectiveRateOfReturn;
	private boolean finRepayPftOnFrq;

	private String grcRateBasis;
	private Date grcPeriodEndDate;
	private boolean allowGrcPeriod = false;
	private String graceBaseRate;
	private String lovDescGraceBaseRateName;
	private String graceSpecialRate;
	private String lovDescGraceSpecialRateName;
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
	private boolean grcAlwIndRate = false;
	private String grcIndBaseRate;
	private String repayRateBasis;
	private String repayBaseRate;
	private String lovDescRepayBaseRateName;
	private String repaySpecialRate;
	private String lovDescRepaySpecialRateName;
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

	private BigDecimal graceFlatAmount = new BigDecimal(0);
	private BigDecimal totalGracePft = new BigDecimal(0);
	private BigDecimal totalGraceCpz = new BigDecimal(0);
	private BigDecimal totalGrossGrcPft = new BigDecimal(0);
	private BigDecimal totalProfit = new BigDecimal(0);
	private BigDecimal totalCpz = new BigDecimal(0);
	private BigDecimal totalGrossPft = new BigDecimal(0);
	private BigDecimal totalRepayAmt = new BigDecimal(0);
	private boolean calculateRepay = false;
	private boolean equalRepay = true;
	private int reqTerms;
	private Date reqMaturity;
	private int calTerms;
	private Date calMaturity;
	private BigDecimal firstRepay;
	private BigDecimal lastRepay;
	private boolean increaseTerms = false;
	private BigDecimal finRepaymentAmount;

	private Date eventFromDate;
	private Date eventToDate;
	private Date recalFromDate;
	private Date recalToDate;
	private String recalType;
	private boolean excludeDeferedDates = false;
	private String addTermAfter;
	private int allowedDefRpyChange = 0;
	private int availedDefRpyChange = 0;
	private int allowedDefFrqChange = 0;
	private int availedDefFrqChange = 0;
	private Date lastRepayDate;
	private Date lastRepayPftDate;
	private Date lastRepayRvwDate;
	private Date lastRepayCpzDate;
	private boolean finIsActive = false;
	private int version;
	private long lastMntBy;
	private Timestamp lastMntOn;
	private boolean newRecord = false;
	private String lovValue;
	private FinanceMain befImage;
	private LoginUserDetails userDetails;
	private String recordStatus;
	private String roleCode = "";
	private String nextRoleCode = "";
	private String taskId = "";
	private String nextTaskId = "";
	private String recordType;
	private String userAction = "Save";
	private long workflowId = 0;
	private int lovDescFinFormatter;
	private List<ErrorDetails> errorDetails = new ArrayList<ErrorDetails>();
	private BigDecimal lovDescFinMinAmt;
	private BigDecimal lovDescFinMaxAmt;
	private boolean lovDescFinAlwDeferment = false;
	private int lovDescFinMaxDifferment;
	private BigDecimal lovDescMinDwnPayPercent;
	private boolean lovDescDwnPayReq = false;
	private boolean lovDescIsSchdGenerated = false;
	private String lovDescAssetCodeName;
	private String lovDescProductCodeName;
	private boolean dedupFound = false;
	private boolean skipDedup = false;
	private boolean proceedDedup = false;
	private boolean blacklisted = false;
	private String lovDescGracePftFrq;
	private String lovDescGrcPftRevFrq;
	private String lovDescGrcCaptFrq;
	private String lovDescRepayFrq;
	private String lovDescRepayPftFrq;
	private String lovDescRepayRevFrq;
	private String lovDescRepayCaptFrq;
	private String lovDescFinLatePayRule;
	private String lovDescFinAEEarlyPay;
	private String lovDescFinAEEarlySettle;
	private BigDecimal lovDescFinOutStndAmt;
	private String lovDescGrcIndBaseRateName;
	private String lovDescIndBaseRateName;
	private BigDecimal lovDescAccruedTillLBD;
	private BigDecimal lovDescCreditAppMinLimit;
	private String finRvwRateApplFor;
	private String finGrcRvwRateApplFor;
	private String closingStatus;
	private Date finApprovedDate;
	private boolean limitValid = false;
	private boolean overrideLimit = false;
	private boolean lovDescAdjClosingBal;
	private String	lovDescFinScheduleOn;
	
	private String lovDescTenorName;

	private int lovDescNotes;
	
	private boolean migratedFinance;
	private boolean scheduleMaintained;
	private boolean scheduleRegenerated;	
	
	public void setLovDescNotes(int lovDescNotes) {
	    this.lovDescNotes = lovDescNotes;
    }

	public int getLovDescNotes() {
	    return lovDescNotes;
    }

	public boolean isNew() {
		return isNewRecord();
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

	public int getNumberOfTerms() {
		return numberOfTerms;
	}

	public void setNumberOfTerms(int numberOfTerms) {
		this.numberOfTerms = numberOfTerms;
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

	public String getLovDescGraceBaseRateName() {
		return this.lovDescGraceBaseRateName;
	}

	public void setLovDescGraceBaseRateName(String lovDescGraceBaseRateName) {
		this.lovDescGraceBaseRateName = lovDescGraceBaseRateName;
	}

	public String getGraceSpecialRate() {
		return graceSpecialRate;
	}

	public void setGraceSpecialRate(String graceSpecialRate) {
		this.graceSpecialRate = graceSpecialRate;
	}

	public String getLovDescGraceSpecialRateName() {
		return this.lovDescGraceSpecialRateName;
	}

	public void setLovDescGraceSpecialRateName(String lovDescGraceSpecialRateName) {
		this.lovDescGraceSpecialRateName = lovDescGraceSpecialRateName;
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

	/**
	 * @return the grcCpzFrq
	 */
	public String getGrcCpzFrq() {
		return grcCpzFrq;
	}

	/**
	 * @param grcCpzFrq
	 *            the grcCpzFrq to set
	 */
	public void setGrcCpzFrq(String grcCpzFrq) {
		this.grcCpzFrq = grcCpzFrq;
	}

	public String getGraceCpzFrqCode() {
		return FrequencyUtil.getFrequencyCode(grcCpzFrq);
	}

	public String getGraceCpzFrqMth() {
		return FrequencyUtil.getFrequencyMth(grcCpzFrq);
	}

	public String getGraceCpzFrqDay() {
		return FrequencyUtil.getFrequencyDay(grcCpzFrq);
	}

	public Date getNextGrcCpzDate() {
		return nextGrcCpzDate;
	}

	public void setNextGrcCpzDate(Date nextGrcCpzDate) {
		this.nextGrcCpzDate = nextGrcCpzDate;
	}

	public String getRepayBaseRate() {
		return repayBaseRate;
	}

	public void setRepayBaseRate(String repayBaseRate) {
		this.repayBaseRate = repayBaseRate;
	}

	public String getLovDescRepayBaseRateName() {
		return this.lovDescRepayBaseRateName;
	}

	public void setLovDescRepayBaseRateName(String lovDescRepayBaseRateName) {
		this.lovDescRepayBaseRateName = lovDescRepayBaseRateName;
	}

	public String getRepaySpecialRate() {
		return repaySpecialRate;
	}

	public void setRepaySpecialRate(String repaySpecialRate) {
		this.repaySpecialRate = repaySpecialRate;
	}

	public String getLovDescRepaySpecialRateName() {
		return this.lovDescRepaySpecialRateName;
	}

	public void setLovDescRepaySpecialRateName(String lovDescRepaySpecialRateName) {
		this.lovDescRepaySpecialRateName = lovDescRepaySpecialRateName;
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

	public String getRepayFrqCode() {
		return FrequencyUtil.getFrequencyCode(repayFrq);
	}

	public String getRepayFrqMth() {
		return FrequencyUtil.getFrequencyMth(repayFrq);
	}

	public String getRepayFrqDay() {
		return FrequencyUtil.getFrequencyDay(repayFrq);
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

	public String getRepayPftFrqCode() {
		return FrequencyUtil.getFrequencyCode(repayPftFrq);
	}

	public String getRepayPftFrqMth() {
		return FrequencyUtil.getFrequencyMth(repayPftFrq);
	}

	public String getRepayPftFrqDay() {
		return FrequencyUtil.getFrequencyDay(repayPftFrq);
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

	public String getRepayRvwFrqCode() {
		return FrequencyUtil.getFrequencyCode(repayRvwFrq);
	}

	public String getRepayRvwFrqMth() {
		return FrequencyUtil.getFrequencyMth(repayRvwFrq);
	}

	public String getRepayRvwFrqDay() {
		return FrequencyUtil.getFrequencyDay(repayRvwFrq);
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

	public String getRepayCpzFrqCode() {
		return FrequencyUtil.getFrequencyCode(repayCpzFrq);
	}

	public String getRepayCpzFrqMth() {
		return FrequencyUtil.getFrequencyMth(repayCpzFrq);
	}

	public String getRepayCpzFrqDay() {
		return FrequencyUtil.getFrequencyDay(repayCpzFrq);
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

	public void setFeeChargeAmt(BigDecimal feeChargeAmt) {
	    this.feeChargeAmt = feeChargeAmt;
    }

	public BigDecimal getFeeChargeAmt() {
	    return feeChargeAmt;
    }

	public boolean isCpzAtGraceEnd() {
		return cpzAtGraceEnd;
	}

	public void setCpzAtGraceEnd(boolean cpzAtGraceEnd) {
		this.cpzAtGraceEnd = cpzAtGraceEnd;
	}

	public BigDecimal getDownPayment() {
		return downPayment;
	}

	public void setDownPayment(BigDecimal downPayment) {
		this.downPayment = downPayment;
	}

	public BigDecimal getGraceFlatAmount() {
		return graceFlatAmount;
	}

	public void setGraceFlatAmount(BigDecimal graceFlatAmount) {
		this.graceFlatAmount = graceFlatAmount;
	}

	public BigDecimal getReqRepayAmount() {
		return reqRepayAmount;
	}

	public void setReqRepayAmount(BigDecimal reqRepayAmount) {
		this.reqRepayAmount = reqRepayAmount;
	}

	public BigDecimal getTotalProfit() {
		return totalProfit;
	}

	public void setTotalProfit(BigDecimal totalProfit) {
		this.totalProfit = totalProfit;
	}

	public BigDecimal getTotalGrossPft() {
		return totalGrossPft;
	}

	public void setTotalGrossPft(BigDecimal totalGrossPft) {
		this.totalGrossPft = totalGrossPft;
	}

	public BigDecimal getTotalRepayAmt() {
		return totalRepayAmt;
	}

	public void setTotalRepayAmt(BigDecimal totalRepayAmt) {
		this.totalRepayAmt = totalRepayAmt;
	}

	public String getRepayRateBasis() {
		return repayRateBasis;
	}

	public void setRepayRateBasis(String repayRateBasis) {
		this.repayRateBasis = repayRateBasis;
	}

	public String getFinType() {
		return finType;
	}

	public void setFinType(String finType) {
		this.finType = finType;
	}

	public String getLovDescFinTypeName() {
		return this.lovDescFinTypeName;
	}

	public void setLovDescFinTypeName(String lovDescFinTypeName) {
		this.lovDescFinTypeName = lovDescFinTypeName;
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

	public String getLovDescFinCcyName() {
		return this.lovDescFinCcyName;
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
		return this.lovDescScheduleMethodName;
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
		return this.lovDescProfitDaysBasisName;
	}

	public void setLovDescProfitDaysBasisName(String lovDescProfitDaysBasisName) {
		this.lovDescProfitDaysBasisName = lovDescProfitDaysBasisName;
	}

	public Date getReqMaturity() {
		return reqMaturity;
	}

	public void setReqMaturity(Date reqMaturity) {
		this.reqMaturity = reqMaturity;
	}

	public int getCalTerms() {
		return calTerms;
	}

	public void setCalTerms(int calTerms) {
		this.calTerms = calTerms;
	}

	public Date getCalMaturity() {
		return calMaturity;
	}

	public void setCalMaturity(Date calMaturity) {
		this.calMaturity = calMaturity;
	}

	public BigDecimal getFirstRepay() {
		return firstRepay;
	}

	public void setFirstRepay(BigDecimal firstRepay) {
		this.firstRepay = firstRepay;
	}

	public BigDecimal getLastRepay() {
		return lastRepay;
	}

	public void setLastRepay(BigDecimal lastRepay) {
		this.lastRepay = lastRepay;
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

	public BigDecimal getFinRepaymentAmount() {
		return finRepaymentAmount;
	}

	public void setFinRepaymentAmount(BigDecimal finRepaymentAmount) {
		this.finRepaymentAmount = finRepaymentAmount;
	}

	public long getCustID() {
		return custID;
	}

	public void setCustID(long custID) {
		this.custID = custID;
	}

	public int getDefferments() {
		return defferments;
	}

	public void setDefferments(int defferments) {
		this.defferments = defferments;
	}

	public boolean isFinIsActive() {
		return finIsActive;
	}

	public void setFinIsActive(boolean finIsActive) {
		this.finIsActive = finIsActive;
	}

	public int getVersion() {
		return version;
	}

	public void setVersion(int version) {
		this.version = version;
	}

	public long getLastMntBy() {
		return lastMntBy;
	}

	public void setLastMntBy(long lastMntBy) {
		this.lastMntBy = lastMntBy;
	}

	public Timestamp getLastMntOn() {
		return lastMntOn;
	}

	public void setLastMntOn(Timestamp lastMntON) {
		this.lastMntOn = lastMntON;
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

	public FinanceMain getBefImage() {
		return this.befImage;
	}

	public void setBefImage(FinanceMain beforeImage) {
		this.befImage = beforeImage;
	}

	public LoginUserDetails getUserDetails() {
		return userDetails;
	}

	public void setUserDetails(LoginUserDetails userDetails) {
		this.userDetails = userDetails;
	}

	public String getRecordStatus() {
		return recordStatus;
	}

	public void setRecordStatus(String recordStatus) {
		this.recordStatus = recordStatus;
	}

	public String getRoleCode() {
		return roleCode;
	}

	public void setRoleCode(String roleCode) {
		this.roleCode = roleCode;
	}

	public String getNextRoleCode() {
		return nextRoleCode;
	}

	public void setNextRoleCode(String nextRoleCode) {
		this.nextRoleCode = nextRoleCode;
	}

	public String getTaskId() {
		return taskId;
	}

	public void setTaskId(String taskId) {
		this.taskId = taskId;
	}

	public String getNextTaskId() {
		return nextTaskId;
	}

	public void setNextTaskId(String nextTaskId) {
		this.nextTaskId = nextTaskId;
	}

	public String getRecordType() {
		return recordType;
	}

	public void setRecordType(String recordType) {
		this.recordType = recordType;
	}

	public String getUserAction() {
		return userAction;
	}

	public void setUserAction(String userAction) {
		this.userAction = userAction;
	}

	public boolean isWorkflow() {
		if (this.workflowId == 0) {
			return false;
		}
		return true;
	}

	public long getWorkflowId() {
		return workflowId;
	}

	public void setWorkflowId(long workflowId) {
		this.workflowId = workflowId;
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

	public boolean isIncreaseTerms() {
		return increaseTerms;
	}

	public void setIncreaseTerms(boolean increaseTerms) {
		this.increaseTerms = increaseTerms;
	}

	public Date getEventFromDate() {
		return eventFromDate;
	}

	public void setEventFromDate(Date eventFromDate) {
		this.eventFromDate = eventFromDate;
	}

	public Date getEventToDate() {
		return eventToDate;
	}

	public void setEventToDate(Date eventToDate) {
		this.eventToDate = eventToDate;
	}

	public Date getRecalFromDate() {
		return recalFromDate;
	}

	public void setRecalFromDate(Date recalFromDate) {
		this.recalFromDate = recalFromDate;
	}

	public Date getRecalToDate() {
		return recalToDate;
	}

	public void setRecalToDate(Date recalToDate) {
		this.recalToDate = recalToDate;
	}

	public String getRecalType() {
		return recalType;
	}

	public void setRecalType(String recalType) {
		this.recalType = recalType;
	}

	public boolean isExcludeDeferedDates() {
		return excludeDeferedDates;
	}

	public void setExcludeDeferedDates(boolean excludeDeferedDates) {
		this.excludeDeferedDates = excludeDeferedDates;
	}

	/**
	 * @return the lovDescFinFormatter
	 */
	public int getLovDescFinFormatter() {
		return lovDescFinFormatter;
	}

	/**
	 * @param lovDescFinFormatter
	 *            the lovDescFinFormatter to set
	 */
	public void setLovDescFinFormatter(int lovDescFinFormatter) {
		this.lovDescFinFormatter = lovDescFinFormatter;
	}

	/**
	 * @return the grcPeriodEndDate
	 */
	public Date getGrcPeriodEndDate() {
		return grcPeriodEndDate;
	}

	/**
	 * @param grcPeriodEndDate
	 *            the grcPeriodEndDate to set
	 */
	public void setGrcPeriodEndDate(Date grcPeriodEndDate) {
		this.grcPeriodEndDate = grcPeriodEndDate;
	}

	/**
	 * @return the grcPftRate
	 */
	public BigDecimal getGrcPftRate() {
		return grcPftRate;
	}

	/**
	 * @param grcPftRate
	 *            the grcPftRate to set
	 */
	public void setGrcPftRate(BigDecimal grcPftRate) {
		this.grcPftRate = grcPftRate;
	}

	/**
	 * @return the grcPftFrq
	 */
	public String getGrcPftFrq() {
		return grcPftFrq;
	}

	/**
	 * @param grcPftFrq
	 *            the grcPftFrq to set
	 */
	public void setGrcPftFrq(String grcPftFrq) {
		this.grcPftFrq = grcPftFrq;
	}

	public String getGracePftFrqCode() {
		return FrequencyUtil.getFrequencyCode(grcPftFrq);
	}

	public String getGracePftFrqMth() {
		return FrequencyUtil.getFrequencyMth(grcPftFrq);
	}

	public String getGracePftFrqDay() {
		return FrequencyUtil.getFrequencyDay(grcPftFrq);
	}

	/**
	 * @return the grcPftRvwFrq
	 */
	public String getGrcPftRvwFrq() {
		return grcPftRvwFrq;
	}

	/**
	 * @param grcPftRvwFrq
	 *            the grcPftRvwFrq to set
	 */
	public void setGrcPftRvwFrq(String grcPftRvwFrq) {
		this.grcPftRvwFrq = grcPftRvwFrq;
	}

	public String getGracePftRvwFrqCode() {
		return FrequencyUtil.getFrequencyCode(grcPftRvwFrq);
	}

	public String getGracePftRvwFrqMth() {
		return FrequencyUtil.getFrequencyMth(grcPftRvwFrq);
	}

	public String getGracePftRvwFrqDay() {
		return FrequencyUtil.getFrequencyDay(grcPftRvwFrq);
	}

	/**
	 * @return the grcRateBasis
	 */
	public String getGrcRateBasis() {
		return grcRateBasis;
	}

	/**
	 * @param grcRateBasis
	 *            the grcRateBasis to set
	 */
	public void setGrcRateBasis(String grcRateBasis) {
		this.grcRateBasis = grcRateBasis;
	}

	/**
	 * @return the finBranch
	 */
	public String getFinBranch() {
		return finBranch;
	}

	/**
	 * @param finBranch
	 *            the finBranch to set
	 */
	public void setFinBranch(String finBranch) {
		this.finBranch = finBranch;
	}

	public String getLovDescFinBranchName() {
		return lovDescFinBranchName;
	}

	public void setLovDescFinBranchName(String lovDescFinBranchName) {
		this.lovDescFinBranchName = lovDescFinBranchName;
	}

	/**
	 * @return the finSourceID
	 */
	public String getFinSourceID() {
		return finSourceID;
	}

	/**
	 * @param finSourceID
	 *            the finSourceID to set
	 */
	public void setFinSourceID(String finSourceID) {
		this.finSourceID = finSourceID;
	}

	/**
	 * @return the allowedDefRpyChange
	 */
	public int getAllowedDefRpyChange() {
		return allowedDefRpyChange;
	}

	/**
	 * @param allowedDefRpyChange
	 *            the allowedDefRpyChange to set
	 */
	public void setAllowedDefRpyChange(int allowedDefRpyChange) {
		this.allowedDefRpyChange = allowedDefRpyChange;
	}

	/**
	 * @return the availedDefRpyChange
	 */
	public int getAvailedDefRpyChange() {
		return availedDefRpyChange;
	}

	/**
	 * @param availedDefRpyChange
	 *            the availedDefRpyChange to set
	 */
	public void setAvailedDefRpyChange(int availedDefRpyChange) {
		this.availedDefRpyChange = availedDefRpyChange;
	}

	/**
	 * @return the allowedDefFrqChange
	 */
	public int getAllowedDefFrqChange() {
		return allowedDefFrqChange;
	}

	/**
	 * @param allowedDefFrqChange
	 *            the allowedDefFrqChange to set
	 */
	public void setAllowedDefFrqChange(int allowedDefFrqChange) {
		this.allowedDefFrqChange = allowedDefFrqChange;
	}

	/**
	 * @return the availedDefFrqChange
	 */
	public int getAvailedDefFrqChange() {
		return availedDefFrqChange;
	}

	/**
	 * @param availedDefFrqChange
	 *            the availedDefFrqChange to set
	 */
	public void setAvailedDefFrqChange(int availedDefFrqChange) {
		this.availedDefFrqChange = availedDefFrqChange;
	}

	// Overidden Equals method to handle the comparision
	public boolean equals(FinanceMain financeMain) {
		return getId() == financeMain.getId();
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}

		if (obj instanceof FinanceMain) {
			FinanceMain financeMain = (FinanceMain) obj;
			return equals(financeMain);
		}
		return false;
	}

	/**
	 * @return the errorDetails
	 */
	public List<ErrorDetails> getErrorDetails() {
		return errorDetails;
	}

	/**
	 * @param errorDetails
	 *            the errorDetails to set
	 */
	public void setErrorDetails(ArrayList<ErrorDetails> errorDetails) {
		this.errorDetails = errorDetails;
	}

	public void setErrorDetail(ErrorDetails errorDetail) {

		if (errorDetails == null) {
			errorDetails = new ArrayList<ErrorDetails>();
		}

		this.errorDetails.add(errorDetail);
	}

	public int getReqTerms() {
		return reqTerms;
	}

	public void setReqTerms(int reqTerms) {
		this.reqTerms = reqTerms;
	}

	/**
	 * @return the lovDescFinMinAmt
	 */
	public BigDecimal getLovDescFinMinAmt() {
		return lovDescFinMinAmt;
	}

	/**
	 * @param lovDescFinMinAmt
	 *            the lovDescFinMinAmt to set
	 */
	public void setLovDescFinMinAmt(BigDecimal lovDescFinMinAmt) {
		this.lovDescFinMinAmt = lovDescFinMinAmt;
	}

	/**
	 * @return the lovDescFinMaxAmt
	 */
	public BigDecimal getLovDescFinMaxAmt() {
		return lovDescFinMaxAmt;
	}

	/**
	 * @param lovDescFinMaxAmt
	 *            the lovDescFinMaxAmt to set
	 */
	public void setLovDescFinMaxAmt(BigDecimal lovDescFinMaxAmt) {
		this.lovDescFinMaxAmt = lovDescFinMaxAmt;
	}

	/**
	 * @return the lovDescFinAlwDeferment
	 */
	public boolean isLovDescFinAlwDeferment() {
		return lovDescFinAlwDeferment;
	}

	/**
	 * @param lovDescFinAlwDeferment
	 *            the lovDescFinAlwDeferment to set
	 */
	public void setLovDescFinAlwDeferment(boolean lovDescFinAlwDeferment) {
		this.lovDescFinAlwDeferment = lovDescFinAlwDeferment;
	}

	/**
	 * @return the lovDescFinMaxDifferment
	 */
	public int getLovDescFinMaxDifferment() {
		return lovDescFinMaxDifferment;
	}

	/**
	 * @param lovDescFinMaxDifferment
	 *            the lovDescFinMaxDifferment to set
	 */
	public void setLovDescFinMaxDifferment(int lovDescFinMaxDifferment) {
		this.lovDescFinMaxDifferment = lovDescFinMaxDifferment;
	}

	/**
	 * @return the lovDescMinDwnPayPercent
	 */
	public BigDecimal getLovDescMinDwnPayPercent() {
		return lovDescMinDwnPayPercent;
	}

	/**
	 * @param lovDescMinDwnPayPercent
	 *            the lovDescMinDwnPayPercent to set
	 */
	public void setLovDescMinDwnPayPercent(BigDecimal lovDescMinDwnPayPercent) {
		this.lovDescMinDwnPayPercent = lovDescMinDwnPayPercent;
	}

	/**
	 * @return the lovDescDwnPayReq
	 */
	public boolean isLovDescDwnPayReq() {
		return lovDescDwnPayReq;
	}

	/**
	 * @param lovDescDwnPayReq
	 *            the lovDescDwnPayReq to set
	 */
	public void setLovDescDwnPayReq(boolean lovDescDwnPayReq) {
		this.lovDescDwnPayReq = lovDescDwnPayReq;
	}

	public FinanceMain() {
		super();
	}

	public FinanceMain(FinanceType financeType) {

		// Basic Details
		this.finType = financeType.getFinType();
		this.lovDescFinTypeName = financeType.getFinType() + "-" + financeType.getFinTypeDesc();
		this.finCcy = financeType.getFinCcy();
		this.lovDescFinCcyName = financeType.getFinCcy() + "-" + financeType.getLovDescFinCcyName();
		this.profitDaysBasis = financeType.getFinDaysCalType();
		this.lovDescProfitDaysBasisName = financeType.getFinDaysCalType() + "-"
		        + financeType.getLovDescFinDaysCalTypeName();
		this.scheduleMethod = financeType.getFinSchdMthd();
		this.lovDescScheduleMethodName = financeType.getFinSchdMthd() + "-"
		        + financeType.getLovDescFinSchdMthdName();
		this.lovDescFinFormatter = financeType.getLovDescFinFormetter();

		// Grace period details
		this.allowGrcPeriod = financeType.isFInIsAlwGrace();
		this.graceBaseRate = financeType.getFinGrcBaseRate();
		this.lovDescGraceBaseRateName = financeType.getFinGrcBaseRate() + "-"
		        + financeType.getLovDescFinGrcBaseRateName();
		this.graceSpecialRate = financeType.getFinGrcSplRate();
		this.lovDescGraceSpecialRateName = financeType.getFinGrcSplRate() + "-"
		        + financeType.getLovDescFinGrcSplRateName();
		this.grcPftRate = financeType.getFinGrcIntRate();
		this.grcPftFrq = financeType.getFinGrcDftIntFrq();
		this.allowGrcPftRvw = financeType.isFinGrcIsRvwAlw();
		this.grcPftRvwFrq = financeType.getFinGrcRvwFrq();
		this.allowGrcCpz = financeType.isFinGrcIsIntCpz();
		this.grcCpzFrq = financeType.getFinGrcCpzFrq();
		this.cpzAtGraceEnd = financeType.isFinIsIntCpzAtGrcEnd();
		this.grcRateBasis = financeType.getFinGrcRateType().substring(0, 1);

		// RepaymentDetails
		this.numberOfTerms = financeType.getFinDftTerms();
		this.repayBaseRate = financeType.getFinBaseRate();
		this.lovDescRepayBaseRateName = financeType.getFinBaseRate() + "-"
		        + financeType.getLovDescFinBaseRateName();
		this.repaySpecialRate = financeType.getFinSplRate();
		this.lovDescRepaySpecialRateName = financeType.getFinSplRate() + "-"
		        + financeType.getLovDescFinSplRateName();
		this.repayProfitRate = financeType.getFinIntRate();
		this.repayFrq = financeType.getFinRpyFrq();
		this.repayPftFrq = financeType.getFinDftIntFrq();
		this.allowRepayRvw = financeType.isFinIsRvwAlw();
		this.repayRvwFrq = financeType.getFinRvwFrq();
		this.allowRepayCpz = financeType.isFinIsIntCpz();
		this.repayCpzFrq = financeType.getFinCpzFrq();
		this.downPayment = financeType.getFinMinDownPayAmount();
		this.repayRateBasis = financeType.getFinRateType().substring(0, 1);
		this.recalType = financeType.getFinSchCalCodeOnRvw();
		this.equalRepay = financeType.isFinFrEqrepayment();
		this.newRecord = true;
		this.lovDescFinAlwDeferment = financeType.isFinIsAlwDifferment();
		this.lovDescFinMaxAmt = financeType.getFinMaxAmount();
		this.lovDescFinMinAmt = financeType.getFinMinAmount();
		this.lovDescMinDwnPayPercent = financeType.getFinMinDownPayAmount();
		this.lovDescDwnPayReq = financeType.isFinIsDwPayRequired();
		this.lovDescAssetCodeName = financeType.getLovDescAssetCodeName();

	}

	public void setLovDescCustCIF(String lovDescCustCIF) {
		this.lovDescCustCIF = lovDescCustCIF;
	}

	public String getLovDescCustCIF() {
		return lovDescCustCIF;
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

	public void setLovDescIsSchdGenerated(boolean lovDescIsSchdGenerated) {
		this.lovDescIsSchdGenerated = lovDescIsSchdGenerated;
	}

	public boolean getLovDescIsSchdGenerated() {
		return lovDescIsSchdGenerated;
	}

	/**
	 * @return the lovDescProductCodeName
	 */
	public String getLovDescAssetCodeName() {
		return lovDescAssetCodeName;
	}

	/**
	 * @param lovDescProductCodeName
	 *            the lovDescProductCodeName to set
	 */
	public void setLovDescAssetCodeName(String lovDescAssetCodeName) {
		this.lovDescAssetCodeName = lovDescAssetCodeName;
	}

	public boolean isDedupFound() {
		return dedupFound;
	}

	public void setDedupFound(boolean dedupFound) {
		this.dedupFound = dedupFound;
	}

	public boolean isSkipDedup() {
		return skipDedup;
	}

	public void setSkipDedup(boolean skipDedup) {
		this.skipDedup = skipDedup;
	}

	public boolean isProceedDedup() {
		return proceedDedup;
	}

	public void setProceedDedup(boolean proceedDedup) {
		this.proceedDedup = proceedDedup;
	}

	public boolean isBlacklisted() {
		return blacklisted;
	}

	public void setBlacklisted(boolean blacklisted) {
		this.blacklisted = blacklisted;
	}

	public BigDecimal getFinAssetValue() {
		return finAssetValue;
	}

	public void setFinAssetValue(BigDecimal finAssetValue) {
		this.finAssetValue = finAssetValue;
	}

	public Date getLastRepayDate() {
		return lastRepayDate;
	}

	public void setLastRepayDate(Date lastRepayDate) {
		this.lastRepayDate = lastRepayDate;
	}

	public Date getLastRepayPftDate() {
		return lastRepayPftDate;
	}

	public void setLastRepayPftDate(Date lastRepayPftDate) {
		this.lastRepayPftDate = lastRepayPftDate;
	}

	public Date getLastRepayRvwDate() {
		return lastRepayRvwDate;
	}

	public void setLastRepayRvwDate(Date lastRepayRvwDate) {
		this.lastRepayRvwDate = lastRepayRvwDate;
	}

	public Date getLastRepayCpzDate() {
		return lastRepayCpzDate;
	}

	public void setLastRepayCpzDate(Date lastRepayCpzDate) {
		this.lastRepayCpzDate = lastRepayCpzDate;
	}

	public int getFrqDefferments() {
		return frqDefferments;
	}

	public void setFrqDefferments(int frqDefferments) {
		this.frqDefferments = frqDefferments;
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

	public String getLovDescGracePftFrq() {
		return lovDescGracePftFrq;
	}

	public void setLovDescGracePftFrq(String lovDescGracePftFrq) {
		this.lovDescGracePftFrq = lovDescGracePftFrq;
	}

	public String getLovDescGrcPftRevFrq() {
		return lovDescGrcPftRevFrq;
	}

	public void setLovDescGrcPftRevFrq(String lovDescGrcPftRevFrq) {
		this.lovDescGrcPftRevFrq = lovDescGrcPftRevFrq;
	}

	public String getLovDescGrcCaptFrq() {
		return lovDescGrcCaptFrq;
	}

	public void setLovDescGrcCaptFrq(String lovDescGrcCaptFrq) {
		this.lovDescGrcCaptFrq = lovDescGrcCaptFrq;
	}

	public String getLovDescRepayFrq() {
		return lovDescRepayFrq;
	}

	public void setLovDescRepayFrq(String lovDescRepayFrq) {
		this.lovDescRepayFrq = lovDescRepayFrq;
	}

	public String getLovDescRepayPftFrq() {
		return lovDescRepayPftFrq;
	}

	public void setLovDescRepayPftFrq(String lovDescRepayPftFrq) {
		this.lovDescRepayPftFrq = lovDescRepayPftFrq;
	}

	public String getLovDescRepayRevFrq() {
		return lovDescRepayRevFrq;
	}

	public void setLovDescRepayRevFrq(String lovDescRepayRevFrq) {
		this.lovDescRepayRevFrq = lovDescRepayRevFrq;
	}

	public String getLovDescRepayCaptFrq() {
		return lovDescRepayCaptFrq;
	}

	public void setLovDescRepayCaptFrq(String lovDescRepayCaptFrq) {
		this.lovDescRepayCaptFrq = lovDescRepayCaptFrq;
	}

	public BigDecimal getTotalGrossGrcPft() {
		return totalGrossGrcPft;
	}

	public void setTotalGrossGrcPft(BigDecimal totalGrossGrcPft) {
		this.totalGrossGrcPft = totalGrossGrcPft;
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

	public BigDecimal getTotalCpz() {
		return totalCpz;
	}

	public void setTotalCpz(BigDecimal totalCpz) {
		this.totalCpz = totalCpz;
	}

	public String getLovDescFinLatePayRule() {
		return lovDescFinLatePayRule;
	}

	public void setLovDescFinLatePayRule(String lovDescFinLatePayRule) {
		this.lovDescFinLatePayRule = lovDescFinLatePayRule;
	}

	public String getLovDescFinAEEarlyPay() {
		return lovDescFinAEEarlyPay;
	}

	public void setLovDescFinAEEarlyPay(String lovDescFinAEEarlyPay) {
		this.lovDescFinAEEarlyPay = lovDescFinAEEarlyPay;
	}

	public String getLovDescFinAEEarlySettle() {
		return lovDescFinAEEarlySettle;
	}

	public void setLovDescFinAEEarlySettle(String lovDescFinAEEarlySettle) {
		this.lovDescFinAEEarlySettle = lovDescFinAEEarlySettle;
	}

	public BigDecimal getLovDescFinOutStndAmt() {
		return lovDescFinOutStndAmt;
	}

	public void setLovDescFinOutStndAmt(BigDecimal lovDescFinOutStndAmt) {
		this.lovDescFinOutStndAmt = lovDescFinOutStndAmt;
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

	public BigDecimal getGrcMargin() {
		return grcMargin;
	}

	public void setGrcMargin(BigDecimal grcMargin) {
		this.grcMargin = grcMargin;
	}

	public BigDecimal getRepayMargin() {
		return repayMargin;
	}

	public void setRepayMargin(BigDecimal repayMargin) {
		this.repayMargin = repayMargin;
	}

	public String getAddTermAfter() {
		return addTermAfter;
	}

	public void setAddTermAfter(String addTermAfter) {
		this.addTermAfter = addTermAfter;
	}

	public BigDecimal getFinCurrAssetValue() {
		return finCurrAssetValue;
	}

	public void setFinCurrAssetValue(BigDecimal finCurrAssetValue) {
		this.finCurrAssetValue = finCurrAssetValue;
	}

	public String getFinCommitmentRef() {
    	return finCommitmentRef;
    }
	public void setFinCommitmentRef(String finCommitmentRef) {
    	this.finCommitmentRef = finCommitmentRef;
    }
	
	public String getLovDescCommitmentRefName() {
    	return lovDescCommitmentRefName;
    }

	public void setLovDescCommitmentRefName(String lovDescCommitmentRefName) {
    	this.lovDescCommitmentRefName = lovDescCommitmentRefName;
    }

	public BigDecimal getAnualizedPercRate() {
    	return anualizedPercRate;
    }

	public void setAnualizedPercRate(BigDecimal anualizedPercRate) {
    	this.anualizedPercRate = anualizedPercRate;
    }

	public BigDecimal getEffectiveRateOfReturn() {
    	return effectiveRateOfReturn;
    }

	public void setEffectiveRateOfReturn(BigDecimal effectiveRateOfReturn) {
    	this.effectiveRateOfReturn = effectiveRateOfReturn;
    }

	public boolean isFinRepayPftOnFrq() {
    	return finRepayPftOnFrq;
    }

	public void setFinRepayPftOnFrq(boolean finRepayPftOnFrq) {
    	this.finRepayPftOnFrq = finRepayPftOnFrq;
    }

	public String getDepreciationFrq() {
		return depreciationFrq;
	}

	public void setDepreciationFrq(String depreciationFrq) {
		this.depreciationFrq = depreciationFrq;
	}

	public Date getFinContractDate() {
		return finContractDate;
	}

	public void setFinContractDate(Date finContractDate) {
		this.finContractDate = finContractDate;
	}

	public String getLovDescSalutationName() {
		return lovDescSalutationName;
	}

	public void setLovDescSalutationName(String lovDescSalutationName) {
		this.lovDescSalutationName = lovDescSalutationName;
	}

	public String getLovDescCustShrtName() {
		return lovDescCustShrtName;
	}

	public void setLovDescCustShrtName(String lovDescCustShrtName) {
		this.lovDescCustShrtName = lovDescCustShrtName;
	}

	public String getLovDescCustAddrLine1() {
		return lovDescCustAddrLine1;
	}

	public void setLovDescCustAddrLine1(String lovDescCustAddrLine1) {
		this.lovDescCustAddrLine1 = lovDescCustAddrLine1;
	}

	public String getLovDescCustAddrLine2() {
		return lovDescCustAddrLine2;
	}

	public void setLovDescCustAddrLine2(String lovDescCustAddrLine2) {
		this.lovDescCustAddrLine2 = lovDescCustAddrLine2;
	}

	public String getLovDescCustAddrCity() {
		return lovDescCustAddrCity;
	}

	public void setLovDescCustAddrCity(String lovDescCustAddrCity) {
		this.lovDescCustAddrCity = lovDescCustAddrCity;
	}

	public String getLovDescCustAddrCountry() {
		return lovDescCustAddrCountry;
	}

	public void setLovDescCustAddrCountry(String lovDescCustAddrCountry) {
		this.lovDescCustAddrCountry = lovDescCustAddrCountry;
	}
	
	public String getLovDescCustCtgTypeName() {
    	return lovDescCustCtgTypeName;
    }

	public void setLovDescCustCtgTypeName(String lovDescCustCtgTypeName) {
    	this.lovDescCustCtgTypeName = lovDescCustCtgTypeName;
    }

	public Date getNextDepDate() {
		return nextDepDate;
	}

	public void setNextDepDate(Date nextDepDate) {
		this.nextDepDate = nextDepDate;
	}

	public Date getLastDepDate() {
		return lastDepDate;
	}

	public void setLastDepDate(Date lastDepDate) {
		this.lastDepDate = lastDepDate;
	}

	public boolean isGrcAlwIndRate() {
		return grcAlwIndRate;
	}

	public void setGrcAlwIndRate(boolean grcAlwIndRate) {
		this.grcAlwIndRate = grcAlwIndRate;
	}

	public String getGrcIndBaseRate() {
		return grcIndBaseRate;
	}

	public void setGrcIndBaseRate(String grcIndBaseRate) {
		this.grcIndBaseRate = grcIndBaseRate;
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

	public String getLovDescGrcIndBaseRateName() {
		return lovDescGrcIndBaseRateName;
	}

	public void setLovDescGrcIndBaseRateName(String lovDescGrcIndBaseRateName) {
		this.lovDescGrcIndBaseRateName = lovDescGrcIndBaseRateName;
	}

	public String getLovDescIndBaseRateName() {
		return lovDescIndBaseRateName;
	}

	public void setLovDescIndBaseRateName(String lovDescIndBaseRateName) {
		this.lovDescIndBaseRateName = lovDescIndBaseRateName;
	}

	public BigDecimal getLovDescAccruedTillLBD() {
		return lovDescAccruedTillLBD;
	}

	public void setLovDescAccruedTillLBD(BigDecimal lovDescAccruedTillLBD) {
		this.lovDescAccruedTillLBD = lovDescAccruedTillLBD;
	}

	public BigDecimal getLovDescCreditAppMinLimit() {
		return lovDescCreditAppMinLimit;
	}

	public void setLovDescCreditAppMinLimit(BigDecimal lovDescCreditAppMinLimit) {
		this.lovDescCreditAppMinLimit = lovDescCreditAppMinLimit;
	}

	public String getFinRvwRateApplFor() {
		return finRvwRateApplFor;
	}

	public void setFinRvwRateApplFor(String finRvwRateApplFor) {
		this.finRvwRateApplFor = finRvwRateApplFor;
	}

	public String getFinGrcRvwRateApplFor() {
		return finGrcRvwRateApplFor;
	}

	public void setFinGrcRvwRateApplFor(String finGrcRvwRateApplFor) {
		this.finGrcRvwRateApplFor = finGrcRvwRateApplFor;
	}

	public FinanceMain(String finReference) {
		super();
		this.finReference = finReference;
	}

	public String getLovDescProductCodeName() {
    	return lovDescProductCodeName;
    }
	public void setLovDescProductCodeName(String lovDescProductCodeName) {
    	this.lovDescProductCodeName = lovDescProductCodeName;
    }

	public String getClosingStatus() {
    	return closingStatus;
    }
	public void setClosingStatus(String closingStatus) {
    	this.closingStatus = closingStatus;
    }

	public Date getFinApprovedDate() {
    	return finApprovedDate;
    }
	public void setFinApprovedDate(Date finApprovedDate) {
    	this.finApprovedDate = finApprovedDate;
    }

	public boolean isLimitValid() {
    	return limitValid;
    }

	public void setLimitValid(boolean limitValid) {
    	this.limitValid = limitValid;
    }

	public boolean isOverrideLimit() {
    	return overrideLimit;
    }

	public void setOverrideLimit(boolean overrideLimit) {
    	this.overrideLimit = overrideLimit;
    }

	public void setCurDisbursementAmt(BigDecimal curDisbursementAmt) {
	    this.curDisbursementAmt = curDisbursementAmt;
    }

	public BigDecimal getCurDisbursementAmt() {
	    return curDisbursementAmt;
    }

	public void setLovDescTenorName(String lovDescTenorName) {
	    this.lovDescTenorName = lovDescTenorName;
    }

	public String getLovDescTenorName() {
	    return lovDescTenorName;
    }

	public boolean isLovDescAdjClosingBal() {
    	return lovDescAdjClosingBal;
    }
	public void setLovDescAdjClosingBal(boolean lovDescAdjClosingBal) {
    	this.lovDescAdjClosingBal = lovDescAdjClosingBal;
    }

	public void setLovDescFinScheduleOn(String lovDescFinScheduleOn) {
	    this.lovDescFinScheduleOn = lovDescFinScheduleOn;
    }

	public String getLovDescFinScheduleOn() {
	    return lovDescFinScheduleOn;
    }

	public boolean isMigratedFinance() {
    	return migratedFinance;
    }
	public void setMigratedFinance(boolean migratedFinance) {
    	this.migratedFinance = migratedFinance;
    }

	public boolean isScheduleMaintained() {
    	return scheduleMaintained;
    }
	public void setScheduleMaintained(boolean scheduleMaintained) {
    	this.scheduleMaintained = scheduleMaintained;
    }

	public boolean isScheduleRegenerated() {
    	return scheduleRegenerated;
    }
	public void setScheduleRegenerated(boolean scheduleRegenerated) {
    	this.scheduleRegenerated = scheduleRegenerated;
    }
		
}
