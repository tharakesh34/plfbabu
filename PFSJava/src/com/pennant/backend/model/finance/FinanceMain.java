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
import java.util.HashMap;
import java.util.List;

import com.pennant.app.util.FrequencyUtil;
import com.pennant.backend.model.ErrorDetails;
import com.pennant.backend.model.LoginUserDetails;

/**
 * Model class for the <b>FinanceMain table</b>.<br>
 * 
 */
public class FinanceMain implements java.io.Serializable {

    private static final long serialVersionUID = -3026443763391506067L;
    
    //===========================================
    //==========Finance Basic Details============
    //===========================================
    
	private String 		finReference;
	private String 		linkedFinRef;
	private String 		investmentRef = "";
	private String 		finType;
	private String 		lovDescFinTypeName;
	private String 		finCcy;
	private String 		lovDescFinCcyName;
	private String 		profitDaysBasis;
	private String 		lovDescProfitDaysBasisName;
	private long 		custID;
	private String 		lovDescCustCIF;
	private String 		lovDescCustShrtName;
	private String 		finBranch;
	private String 		lovDescFinBranchName;
	private Date 		finStartDate;
	private Date 		finContractDate;
	/*private BigDecimal disbCcyFinAmount;
	private BigDecimal 	ccyConversionRate;
	private String 		disbCcy;
	private int 		lovDescDisbCcyFormatter;
	private String 		lovDescDisbCcyName;
	private boolean 	diffDisbCcy;*/
	private BigDecimal 	finAmount = BigDecimal.ZERO;
	private String 		disbAccountId;
	private String 		repayAccountId;
	private BigDecimal 	downPayment = BigDecimal.ZERO;
	private BigDecimal 	downPayBank = BigDecimal.ZERO;
	private BigDecimal 	downPaySupl = BigDecimal.ZERO;
	private String 		downPayAccount;
	private int 		defferments;
	private int 		planDeferCount;
	private String 		finPurpose;
	private String  	lovDescFinPurposeName;
	private boolean 	allowGrcPeriod = false;
	private String 		finRepayMethod;
	private String 		lovDescFinRepayMethod;
	private String 		finCommitmentRef;
	private String 		lovDescCommitmentRefName;
	private String 		depreciationFrq;
	private boolean 	finIsActive = false;
	private String 		finRemarks;

	private String		mobileNumber;
	private String 		lovdescFinProduct;
	private String 		lovDescCustCRCPR;
	private String		lovDescCustPassportNo;
	private Date		lovDescCustDOB;
	private String 		requestStage;
	private String 		queuePriority;
	
	//Commercial Workflow Purpose
	private boolean 	securityCollateral;
	private String	 	approved;
	private boolean 	customerAcceptance;
	private boolean 	cbbApprovalRequired;
	private boolean 	cbbApproved;
	private BigDecimal 	securityDeposit;
	
	//Step Details
	private boolean 	stepFinance = false;
	private String 		stepPolicy;
	private String 		lovDescStepPolicyName;
	private boolean 	alwManualSteps = false;
	private int 		noOfSteps = 0;
	
	//Limit Status Details
	private String 		discrepancy;
	private String 		limitStatus;
	
	//===========================================
    //==========Grace Period Details=============
    //===========================================
	
	private int 		graceTerms = 0;
	private Date 		grcPeriodEndDate;
	private String 		grcRateBasis;
	private BigDecimal 	grcPftRate = BigDecimal.ZERO;
	private String 		graceBaseRate;
	private String 		lovDescGraceBaseRateName;
	private String 		graceSpecialRate;
	private String 		lovDescGraceSpecialRateName;
	private BigDecimal 	grcMargin = BigDecimal.ZERO;
	private String 		grcProfitDaysBasis;
	private boolean 	grcAlwIndRate = false;
	private String 		grcIndBaseRate;
	private String 		lovDescGrcIndBaseRateName;
	private String 		grcPftFrq;
	private Date 		nextGrcPftDate;
	private boolean 	allowGrcPftRvw = false;
	private String 		grcPftRvwFrq;
	private Date 		nextGrcPftRvwDate;
	private boolean 	allowGrcCpz = false;
	private String 		grcCpzFrq;
	private Date 		nextGrcCpzDate;
	private boolean 	allowGrcRepay = false;
	private String 		grcSchdMthd;
	
	//===========================================
    //==========Repay Period Details=============
    //===========================================
	
	private int 		numberOfTerms = 0;
	private BigDecimal 	reqRepayAmount = BigDecimal.ZERO;
	private String 		repayRateBasis;
	private BigDecimal 	repayProfitRate = BigDecimal.ZERO;
	private String 		repayBaseRate;
	private String 		lovDescRepayBaseRateName;
	private String 		repaySpecialRate;
	private String 		lovDescRepaySpecialRateName;
	private BigDecimal 	repayMargin = BigDecimal.ZERO;
	private String 		scheduleMethod;
	private String 		lovDescScheduleMethodName;
	private boolean 	alwIndRate = false;
	private String 		indBaseRate;
	private String 		lovDescIndBaseRateName;
	private String 		repayPftFrq;
	private Date 		nextRepayPftDate;
	private boolean 	allowRepayRvw = false;
	private String 		repayRvwFrq;
	private Date 		nextRepayRvwDate;
	private boolean 	allowRepayCpz = false;
	private String 		repayCpzFrq;
	private Date 		nextRepayCpzDate;
	private String 		repayFrq;
	private Date 		nextRepayDate;
	private Date 		maturityDate;
	private boolean 	finRepayPftOnFrq;
	
	//===========================================
    //=========Fee Details Usage  ============
    //===========================================
	
	private String 		remFeeSchdMethod;
	private int 		feeSchdTerms;
	private BigDecimal 	calSchdFeeAmt;
	
	//===========================================
    //=========Schedule Build Usage  ============
    //===========================================
	
	private BigDecimal 	feeChargeAmt;
	private BigDecimal 	curFeeChargeAmt;
	private BigDecimal 	curDisbursementAmt;
	private BigDecimal 	anualizedPercRate;
	private BigDecimal 	effectiveRateOfReturn;
	private boolean 	cpzAtGraceEnd = false;
	private BigDecimal 	totalGracePft = BigDecimal.ZERO;
	private BigDecimal 	totalGraceCpz = BigDecimal.ZERO;
	private BigDecimal 	totalGrossGrcPft = BigDecimal.ZERO;
	private BigDecimal 	totalProfit = BigDecimal.ZERO;
	private BigDecimal 	totalCpz = BigDecimal.ZERO;
	private BigDecimal 	totalGrossPft = BigDecimal.ZERO;
	private BigDecimal 	totalRepayAmt = BigDecimal.ZERO;
	private boolean 	calculateRepay = false;
	private boolean 	equalRepay = true;
	private int 		calTerms;
	private Date 		calMaturity;
	private BigDecimal 	firstRepay;
	private BigDecimal 	lastRepay;
	private BigDecimal 	finRepaymentAmount = BigDecimal.ZERO;
	private Date 		eventFromDate;
	private Date 		eventToDate;
	private Date 		recalFromDate;
	private Date 		recalToDate;
	private String 		recalType;
	private String 		finRvwRateApplFor;
	private String 		finGrcRvwRateApplFor;
	private int 		lovDescFinFormatter;
	private boolean 	lovDescAdjClosingBal;
	
	//Finance Maintenance Details
	private boolean 	excludeDeferedDates = false;
	private String 		addTermAfter;
	private int 		allowedDefRpyChange = 0;
	private int 		availedDefRpyChange = 0;
	private int 		allowedDefFrqChange = 0;
	private int 		availedDefFrqChange = 0;
	private Date 		lastRepayDate;
	private Date 		lastRepayPftDate;
	private Date 		lastRepayRvwDate;
	private Date 		lastRepayCpzDate;
	private boolean 	pftIntact = false;
	private int 		adjTerms = 0;
	
	//===========================================
    //=========External Usage Details============
    //===========================================
	
	private BigDecimal 	finAssetValue = BigDecimal.ZERO;
	private BigDecimal 	finCurrAssetValue = BigDecimal.ZERO;
	private Date 		nextDepDate;
	private Date 		lastDepDate;
	private String 		finSourceID = null;
	private String 		rcdMaintainSts = "";
	private String		lovDescFinScheduleOn;
	private String 		closingStatus;
	private Date 		finApprovedDate;
	private String 		lovDescTenorName;
	private int 		lovDescNotes;
	private boolean 	migratedFinance;
	private boolean 	scheduleMaintained;
	private boolean 	scheduleRegenerated;	
	private String 		finStatus;
	private String 		finStsReason;
	private BigDecimal 	custDSR;
	
	//===========================================
    //===========Validation Details==============
    //===========================================
	
	private boolean 	lovDescIsSchdGenerated = false;
	private String 		lovDescAssetCodeName;
	private String 		lovDescProductCodeName;
	private BigDecimal 	availCommitAmount;
	private long 		jointCustId;
	private String 		lovDescJointCustCIF;
	private String 		lovDescJointCustShrtName;
	private boolean 	jointAccount;
	
	//===========================================
    //=========Enquiry Purpose Details===========
    //===========================================
	
	private String 		finAccount;
	private String 		finCustPftAccount;
	private BigDecimal 	lovDescAccruedTillLBD; 
	
	//===========================================
    //=============Workflow Details==============
    //===========================================
	
	private boolean 	dedupFound = false;
	private boolean 	skipDedup = false;
	private boolean 	proceedDedup = false;
	private boolean 	blacklisted = false;
	private boolean 	limitValid = false;
	private boolean 	overrideLimit = false;
	private BigDecimal 	amount;
	private boolean 	exception;
	private BigDecimal 	amountBD = BigDecimal.ZERO;
	private BigDecimal 	amountUSD = BigDecimal.ZERO;
	private BigDecimal  maturity = BigDecimal.ZERO;
	private boolean 	fundsAvailConfirmed;
	private boolean 	limitApproved;
	
	//===========================================
    //=========Purpose to Other Modules==========
    //===========================================
	
	private String 		lovDescCustCoreBank;
	private String 		lovDescCustFName;
	private String 		lovDescCustLName;
	private String 		lovDescSalutationName;
	private String 		lovDescCustCtgTypeName;

	private int 		version;
	private long 		lastMntBy;
	private Timestamp 	lastMntOn;
	private boolean 	newRecord = false;
	private FinanceMain befImage;
	private LoginUserDetails userDetails;
	private String 		recordStatus;
	private String 		roleCode = "";
	private String 		nextRoleCode = "";
	private String 		taskId = "";
	private String 		nextTaskId = "";
	private String 		recordType;
	private String 		userAction = "Save";
	private long 		workflowId = 0;
	
	//Can be Deleted -- TODO
	private int 		reqTerms;
	private Date 		reqMaturity;
	private boolean 	increaseTerms = false;
	private long 		authorization1;
	private String 		lovDescAuthorization1Name;
	private String 		lovDescAuth1DesigName;
	private long 		authorization2;
	private String 		lovDescAuthorization2Name;
	private String 		lovDescAuth2DesigName;
	private String 		lovDescFinDivision;
	
	private List<ErrorDetails> errorDetails = new ArrayList<ErrorDetails>();
	
	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//
	// ++++++++++++++++++ getter / setter +++++++++++++++++++//
	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//

	public FinanceMain() {
		super();
	}
	
	public FinanceMain(String finReference) {
		super();
		this.finReference = finReference;
	}

	public boolean isNew() {
		return isNewRecord();
	}

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

	public String getGrcCpzFrq() {
		return grcCpzFrq;
	}
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

	public BigDecimal getDownPayBank() {
    	return downPayBank;
    }
	public void setDownPayBank(BigDecimal downPayBank) {
    	this.downPayBank = downPayBank;
    }

	public BigDecimal getDownPaySupl() {
    	return downPaySupl;
    }
	public void setDownPaySupl(BigDecimal downPaySupl) {
    	this.downPaySupl = downPaySupl;
    }

	public String getDownPayAccount() {
    	return downPayAccount;
    }
	public void setDownPayAccount(String downPayAccount) {
    	this.downPayAccount = downPayAccount;
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

	public int getLovDescFinFormatter() {
		return lovDescFinFormatter;
	}
	public void setLovDescFinFormatter(int lovDescFinFormatter) {
		this.lovDescFinFormatter = lovDescFinFormatter;
	}

	public Date getGrcPeriodEndDate() {
		return grcPeriodEndDate;
	}
	public void setGrcPeriodEndDate(Date grcPeriodEndDate) {
		this.grcPeriodEndDate = grcPeriodEndDate;
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

	public String getGracePftFrqCode() {
		return FrequencyUtil.getFrequencyCode(grcPftFrq);
	}

	public String getGracePftFrqMth() {
		return FrequencyUtil.getFrequencyMth(grcPftFrq);
	}

	public String getGracePftFrqDay() {
		return FrequencyUtil.getFrequencyDay(grcPftFrq);
	}

	public String getGrcPftRvwFrq() {
		return grcPftRvwFrq;
	}
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

	public String getGrcRateBasis() {
		return grcRateBasis;
	}
	public void setGrcRateBasis(String grcRateBasis) {
		this.grcRateBasis = grcRateBasis;
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

	public String getFinSourceID() {
		return finSourceID;
	}
	public void setFinSourceID(String finSourceID) {
		this.finSourceID = finSourceID;
	}

	public int getAllowedDefRpyChange() {
		return allowedDefRpyChange;
	}
	public void setAllowedDefRpyChange(int allowedDefRpyChange) {
		this.allowedDefRpyChange = allowedDefRpyChange;
	}

	public int getAvailedDefRpyChange() {
		return availedDefRpyChange;
	}
	public void setAvailedDefRpyChange(int availedDefRpyChange) {
		this.availedDefRpyChange = availedDefRpyChange;
	}

	public int getAllowedDefFrqChange() {
		return allowedDefFrqChange;
	}
	public void setAllowedDefFrqChange(int allowedDefFrqChange) {
		this.allowedDefFrqChange = allowedDefFrqChange;
	}

	public int getAvailedDefFrqChange() {
		return availedDefFrqChange;
	}
	public void setAvailedDefFrqChange(int availedDefFrqChange) {
		this.availedDefFrqChange = availedDefFrqChange;
	}
	
	public int getReqTerms() {
		return reqTerms;
	}
	public void setReqTerms(int reqTerms) {
		this.reqTerms = reqTerms;
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
	public boolean isLovDescIsSchdGenerated() {
		return lovDescIsSchdGenerated;
	}

	public String getLovDescAssetCodeName() {
		return lovDescAssetCodeName;
	}
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

	public int getPlanDeferCount() {
		return planDeferCount;
	}

	public void setPlanDeferCount(int planDeferCount) {
		this.planDeferCount = planDeferCount;
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

	public String getFinPurpose() {
    	return finPurpose;
    }
	public void setFinPurpose(String finPurpose) {
    	this.finPurpose = finPurpose;
    }

	public String getLovDescFinPurposeName() {
    	return lovDescFinPurposeName;
    }
	public void setLovDescFinPurposeName(String lovDescFinPurposeName) {
    	this.lovDescFinPurposeName = lovDescFinPurposeName;
    }

	public void setAmount(BigDecimal amount) {
	    this.amount = amount;
    }
	public BigDecimal getAmount() {
	    return amount;
    }

	public void setException(boolean exception) {
	    this.exception = exception;
    }
	public boolean isException() {
	    return exception;
    }

	public String getFinStatus() {
    	return finStatus;
    }
	public void setFinStatus(String finStatus) {
    	this.finStatus = finStatus;
    }

	public String getFinStsReason() {
    	return finStsReason;
    }
	public void setFinStsReason(String finStsReason) {
    	this.finStsReason = finStsReason;
    }

	public void setCustDSR(BigDecimal custDSR) {
	    this.custDSR = custDSR;
    }
	public BigDecimal getCustDSR() {
	    return custDSR;
    }

	public void setAuthorization1(long authorization1) {
	    this.authorization1 = authorization1;
    }
	public long getAuthorization1() {
	    return authorization1;
    }
	
	public String getLovDescAuthorization1Name() {
    	return lovDescAuthorization1Name;
    }
	public void setLovDescAuthorization1Name(String lovDescAuthorization1Name) {
    	this.lovDescAuthorization1Name = lovDescAuthorization1Name;
    }

	public String getLovDescAuth1DesigName() {
    	return lovDescAuth1DesigName;
    }
	public void setLovDescAuth1DesigName(String lovDescAuth1DesigName) {
    	this.lovDescAuth1DesigName = lovDescAuth1DesigName;
    }

	public void setAuthorization2(long authorization2) {
	    this.authorization2 = authorization2;
    }
	public long getAuthorization2() {
	    return authorization2;
    }

	public String getLovDescAuthorization2Name() {
    	return lovDescAuthorization2Name;
    }
	public void setLovDescAuthorization2Name(String lovDescAuthorization2Name) {
    	this.lovDescAuthorization2Name = lovDescAuthorization2Name;
    }

	public String getLovDescAuth2DesigName() {
    	return lovDescAuth2DesigName;
    }
	public void setLovDescAuth2DesigName(String lovDescAuth2DesigName) {
    	this.lovDescAuth2DesigName = lovDescAuth2DesigName;
    }

	public long getJointCustId() {
    	return jointCustId;
    }
	public void setJointCustId(long jointCustId) {
    	this.jointCustId = jointCustId;
    }

	public String getLovDescJointCustCIF() {
    	return lovDescJointCustCIF;
    }
	public void setLovDescJointCustCIF(String lovDescJointCustCIF) {
    	this.lovDescJointCustCIF = lovDescJointCustCIF;
    }

	public String getLovDescJointCustShrtName() {
    	return lovDescJointCustShrtName;
    }
	public void setLovDescJointCustShrtName(String lovDescJointCustShrtName) {
    	this.lovDescJointCustShrtName = lovDescJointCustShrtName;
    }

	public boolean isJointAccount() {
    	return jointAccount;
    }
	public void setJointAccount(boolean jointAccount) {
    	this.jointAccount = jointAccount;
    }

	public boolean isSecurityCollateral() {
    	return securityCollateral;
    }
	public void setSecurityCollateral(boolean securityCollateral) {
    	this.securityCollateral = securityCollateral;
    }

	public BigDecimal getMaturity() {
    	return maturity;
    }
	public void setMaturity(BigDecimal maturity) {
    	this.maturity = maturity;
    }

	public BigDecimal getAmountBD() {
    	return amountBD;
    }
	public void setAmountBD(BigDecimal amountBD) {
    	this.amountBD = amountBD;
    }

	public BigDecimal getAmountUSD() {
    	return amountUSD;
    }
	public void setAmountUSD(BigDecimal amountUSD) {
    	this.amountUSD = amountUSD;
    }

	public String getApproved() {
    	return approved;
    }
	public void setApproved(String approved) {
    	this.approved = approved;
    }

	public boolean isCustomerAcceptance() {
    	return customerAcceptance;
    }
	public void setCustomerAcceptance(boolean customerAcceptance) {
    	this.customerAcceptance = customerAcceptance;
    }

	public void setAvailCommitAmount(BigDecimal availCommitAmount) {
	    this.availCommitAmount = availCommitAmount;
    }

	public BigDecimal getAvailCommitAmount() {
	    return availCommitAmount;
    }

	public BigDecimal getSecurityDeposit() {
    	return securityDeposit;
    }
	public void setSecurityDeposit(BigDecimal securityDeposit) {
    	this.securityDeposit = securityDeposit;
    }

	public boolean isCbbApprovalRequired() {
    	return cbbApprovalRequired;
    }
	public void setCbbApprovalRequired(boolean cbbApprovalRequired) {
    	this.cbbApprovalRequired = cbbApprovalRequired;
    }

	public boolean isCbbApproved() {
    	return cbbApproved;
    }
	public void setCbbApproved(boolean cbbApproved) {
    	this.cbbApproved = cbbApproved;
    }

	public String getDiscrepancy() {
    	return discrepancy;
    }
	public void setDiscrepancy(String discrepancy) {
    	this.discrepancy = discrepancy;
    }
	
	public String getLimitStatus() {
    	return limitStatus;
    }
	public void setLimitStatus(String limitStatus) {
    	this.limitStatus = limitStatus;
    }

	public boolean isFundsAvailConfirmed() {
    	return fundsAvailConfirmed;
    }
	public void setFundsAvailConfirmed(boolean fundsAvailConfirmed) {
    	this.fundsAvailConfirmed = fundsAvailConfirmed;
    }

	public boolean isLimitApproved() {
    	return limitApproved;
    }
	public void setLimitApproved(boolean limitApproved) {
    	this.limitApproved = limitApproved;
    }

	public void setCurFeeChargeAmt(BigDecimal curFeeChargeAmt) {
	    this.curFeeChargeAmt = curFeeChargeAmt;
    }
	public BigDecimal getCurFeeChargeAmt() {
	    return curFeeChargeAmt;
    }
	
	public void setLovDescCustCoreBank(String lovDescCustCoreBank) {
	    this.lovDescCustCoreBank = lovDescCustCoreBank;
    }
	public String getLovDescCustCoreBank() {
	    return lovDescCustCoreBank;
    }

	public String getInvestmentRef() {
    	return investmentRef;
    }
	public void setInvestmentRef(String investmentRef) {
    	this.investmentRef = investmentRef;
    }

	public int getGraceTerms() {
    	return graceTerms;
    }
	public void setGraceTerms(int graceTerms) {
    	this.graceTerms = graceTerms;
    }

	public String getLovDescFinDivision() {
    	return lovDescFinDivision;
    }
	public void setLovDescFinDivision(String lovDescFinDivision) {
    	this.lovDescFinDivision = lovDescFinDivision;
    }

	public String getRcdMaintainSts() {
	    return rcdMaintainSts;
    }
	public void setRcdMaintainSts(String rcdMaintainSts) {
	    this.rcdMaintainSts = rcdMaintainSts;
    }

	public String getFinRepayMethod() {
	    return finRepayMethod;
    }
	public void setFinRepayMethod(String finRepayMethod) {
	    this.finRepayMethod = finRepayMethod;
    }

	public String getLovDescFinRepayMethod() {
	    return lovDescFinRepayMethod;
    }
	public void setLovDescFinRepayMethod(String lovDescFinRepayMethod) {
	    this.lovDescFinRepayMethod = lovDescFinRepayMethod;
    }
	
	public String getGrcProfitDaysBasis() {
	    return grcProfitDaysBasis;
    }
	public void setGrcProfitDaysBasis(String grcProfitDaysBasis) {
	    this.grcProfitDaysBasis = grcProfitDaysBasis;
    }
	
	public boolean isStepFinance() {
		return stepFinance;
	}
	public void setStepFinance(boolean stepFinance) {
		this.stepFinance = stepFinance;
	}

	public String getStepPolicy() {
		return stepPolicy;
	}
	public void setStepPolicy(String stepPolicy) {
		this.stepPolicy = stepPolicy;
	}
	
	public String getLovDescStepPolicyName() {
	    return lovDescStepPolicyName;
    }
	public void setLovDescStepPolicyName(String lovDescStepPolicyName) {
	    this.lovDescStepPolicyName = lovDescStepPolicyName;
    }

	public boolean isAlwManualSteps() {
		return alwManualSteps;
	}
	public void setAlwManualSteps(boolean alwManualSteps) {
		this.alwManualSteps = alwManualSteps;
	}

	public int getNoOfSteps() {
		return noOfSteps;
	}
	public void setNoOfSteps(int noOfSteps) {
		this.noOfSteps = noOfSteps;
	}
	
	public boolean isPftIntact() {
	    return pftIntact;
    }
	public void setPftIntact(boolean pftIntact) {
	    this.pftIntact = pftIntact;
    }

	public int getAdjTerms() {
	    return adjTerms;
    }
	public void setAdjTerms(int adjTerms) {
	    this.adjTerms = adjTerms;
    }
	
	public void setLovDescNotes(int lovDescNotes) {
	    this.lovDescNotes = lovDescNotes;
    }
	public int getLovDescNotes() {
	    return lovDescNotes;
    }
	
	public String getRemFeeSchdMethod() {
	    return remFeeSchdMethod;
    }
	public void setRemFeeSchdMethod(String remFeeSchdMethod) {
	    this.remFeeSchdMethod = remFeeSchdMethod;
    }

	public int getFeeSchdTerms() {
	    return feeSchdTerms;
    }
	public void setFeeSchdTerms(int feeSchdTerms) {
	    this.feeSchdTerms = feeSchdTerms;
    }

	public BigDecimal getCalSchdFeeAmt() {
	    return calSchdFeeAmt;
    }
	public void setCalSchdFeeAmt(BigDecimal calSchdFeeAmt) {
	    this.calSchdFeeAmt = calSchdFeeAmt;
    }

	public HashMap<String, Object> getDeclaredFieldValues() {
		HashMap<String, Object> customerScoringMap = new HashMap<String, Object>();	
		try {
			for (int i = 0; i < this.getClass().getDeclaredFields().length; i++) {
				customerScoringMap.put(this.getClass().getDeclaredFields()[i].getName(),
						this.getClass().getDeclaredFields()[i].get(this));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return customerScoringMap;
	}
	
	/*	public BigDecimal getDisbCcyFinAmount() {
    	return disbCcyFinAmount;
    }
	public void setDisbCcyFinAmount(BigDecimal disbCcyFinAmount) {
    	this.disbCcyFinAmount = disbCcyFinAmount;
    }

	public BigDecimal getCcyConversionRate() {
    	return ccyConversionRate;
    }
	public void setCcyConversionRate(BigDecimal ccyConversionRate) {
    	this.ccyConversionRate = ccyConversionRate;
    }

	public String getDisbCcy() {
    	return disbCcy;
    }
	public void setDisbCcy(String disbCcy) {
    	this.disbCcy = disbCcy;
    }

	public boolean isDiffDisbCcy() {
    	return diffDisbCcy;
    }
	public void setDiffDisbCcy(boolean diffDisbCcy) {
    	this.diffDisbCcy = diffDisbCcy;
    }

	public String getLovDescDisbCcyName() {
	    return lovDescDisbCcyName;
    }
	public void setLovDescDisbCcyName(String lovDescDisbCcyName) {
	    this.lovDescDisbCcyName = lovDescDisbCcyName;
    }

	public int getLovDescDisbCcyFormatter() {
	    return lovDescDisbCcyFormatter;
    }

	public void setLovDescDisbCcyFormatter(int lovDescDisbCcyFormatter) {
	    this.lovDescDisbCcyFormatter = lovDescDisbCcyFormatter;
    }*/
	
	public String getLovDescCustCRCPR() {
		return lovDescCustCRCPR;
	}

	public void setLovDescCustCRCPR(String lovDescCustCRCPR) {
		this.lovDescCustCRCPR = lovDescCustCRCPR;
	}

	public String getLovDescCustPassportNo() {
		return lovDescCustPassportNo;
	}

	public void setLovDescCustPassportNo(String lovDescCustPassportNo) {
		this.lovDescCustPassportNo = lovDescCustPassportNo;
	}

	public Date getLovDescCustDOB() {
		return lovDescCustDOB;
	}

	public void setLovDescCustDOB(Date lovDescCustDOB) {
		this.lovDescCustDOB = lovDescCustDOB;
	}
	
	public String getMobileNumber() {
		return mobileNumber;
	}

	public void setMobileNumber(String mobileNumber) {
		this.mobileNumber = mobileNumber;
	}

	public String getRequestStage() {
		return requestStage;
	}

	public void setRequestStage(String requestStage) {
		this.requestStage = requestStage;
	}

	public String getQueuePriority() {
		return queuePriority;
	}

	public void setQueuePriority(String queuePriority) {
		this.queuePriority = queuePriority;
	}
	public String getLovdescFinProduct() {
	    return lovdescFinProduct;
    }

	public void setLovdescFinProduct(String lovdescFinProduct) {
	    this.lovdescFinProduct = lovdescFinProduct;
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

	public String getLinkedFinRef() {
	    return linkedFinRef;
    }

	public void setLinkedFinRef(String linkedFinRef) {
	    this.linkedFinRef = linkedFinRef;
    }

}
