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
 * FileName    		:  FinanceMain.java                                                 	* 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  05-05-2011    														*
 *                                                                  						*
 * Modified Date    :  05-05-2011    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 05-05-2011       Pennant	                 0.1                                            * 
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
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;

import com.pennant.backend.model.ErrorDetails;
import com.pennant.backend.model.LoggedInUser;
import com.pennanttech.pff.core.model.AbstractWorkflowEntity;

/**
 * Model class for the <b>FinanceMain table</b>.<br>
 * 
 */


@XmlType(propOrder = { "applicationNo","lovDescCustCIF", "finType", "finCcy", "finBranch", "profitDaysBasis", "finAmount",
		"finAssetValue", "downPayBank", "downPaySupl", "finRepayMethod", "finStartDate", "allowGrcPeriod",
		"tDSApplicable", "manualSchedule", "planDeferCount", "stepFinance", "alwManualSteps", "stepPolicy", "stepType",
		"graceTerms", "grcPeriodEndDate", "grcRateBasis", "grcPftRate", "graceBaseRate", "graceSpecialRate",
		"grcMargin", "grcProfitDaysBasis", "grcPftFrq", "nextGrcPftDate", "grcPftRvwFrq", "nextGrcPftRvwDate",
		"grcCpzFrq", "nextGrcCpzDate", "allowGrcRepay", "grcSchdMthd", "grcMinRate", "grcMaxRate", "grcAdvPftRate",
		"grcAdvBaseRate", "grcAdvMargin", "numberOfTerms", "reqRepayAmount", "repayRateBasis", "repayProfitRate",
		"repayBaseRate", "repaySpecialRate", "repayMargin", "scheduleMethod", "repayFrq", "nextRepayDate",
		"repayPftFrq", "nextRepayPftDate", "repayRvwFrq", "nextRepayRvwDate", "repayCpzFrq", "nextRepayCpzDate",
		"maturityDate", "finRepayPftOnFrq", "rpyMinRate", "rpyMaxRate", "rpyAdvPftRate", "rpyAdvBaseRate",
		"rpyAdvMargin", "supplementRent", "increasedCost", "rolloverFrq", "nextRolloverDate", "finContractDate",
		"finPurpose", "finLimitRef", "finCommitmentRef", "repayAccountId", "depreciationFrq", "dsaCode",
		"accountsOfficer", "salesDepartment", "dmaCode", "referralId", "quickDisb", "unPlanEMIHLockPeriod",
		"unPlanEMICpz", "reAgeCpz", "maxUnplannedEmi", "maxReAgeHolidays", "alwBPI", "bpiTreatment", "planEMIHAlw",
		"planEMIHMethod", "planEMIHMaxPerYear", "planEMIHMax", "planEMIHLockPeriod", "planEMICpz","firstDisbDate","lastDisbDate" })
@XmlRootElement(name = "financeDetail")
@XmlAccessorType(XmlAccessType.NONE)
public class FinanceMain extends AbstractWorkflowEntity {
	private static final long serialVersionUID = -3026443763391506067L;

	// ===========================================
	// ==========Finance Basic Details============
	// ===========================================
	
	private String finReference;

	private String linkedFinRef;
	private String investmentRef = "";
	@XmlElement
	private String finType;
	private String lovDescFinTypeName;
	private String promotionCode;
	@XmlElement
	private String finCcy;
	private boolean rcu;
	@XmlElement
	private String profitDaysBasis;
	private long custID;
	@XmlElement(name = "cif")
	private String lovDescCustCIF;
	private String lovDescCustShrtName;
	@XmlElement
	private String finBranch;
	private String lovDescFinBranchName;
	@XmlElement
	private Date finStartDate;
	@XmlElement
	private Date finContractDate;
	@XmlElement
	private BigDecimal finAmount = BigDecimal.ZERO;
	private String disbAccountId;
	@XmlElement
	private String repayAccountId;

	private String finCancelAc;
	private String finWriteoffAc;
	private BigDecimal minDownPayPerc = BigDecimal.ZERO;
	private BigDecimal downPayment = BigDecimal.ZERO;
	@XmlElement
	private BigDecimal downPayBank = BigDecimal.ZERO;
	@XmlElement
	private BigDecimal downPaySupl = BigDecimal.ZERO;
	private String downPayAccount;
	private int defferments;
	@XmlElement
	private int planDeferCount;
	@XmlElement
	private String finPurpose;
	private String lovDescFinPurposeName;
	@XmlElement
	private boolean allowGrcPeriod;
	@XmlElement
	private String finRepayMethod;
	@XmlElement
	private String finCommitmentRef;
	private String lovDescCommitmentRefName;
	@XmlElement
	private String depreciationFrq;
	private boolean finIsActive;
	private String finRemarks;
	private long initiateUser;
	private Date initiateDate;
	private long mMAId;
	private String agreeName;
	private boolean finIsAlwMD;
	@XmlElement
	private String accountsOfficer;
	@XmlElement
	private String dsaCode;
	private String dsaCodeDesc;
	private String lovDescAccountsOfficer;
	private String lovDescMobileNumber;
	private String lovDescFinProduct;
	private String lovDescCustCRCPR;
	private String lovDescCustPassportNo;
	private Date lovDescCustDOB;
	private String lovDescRequestStage;
	private String lovDescQueuePriority;
	private String lovDescMMAReference;
	@XmlElement(name = "tdsApplicable")
	private boolean tDSApplicable;
	private String droplineFrq;
	private Date firstDroplineDate;
	private boolean pftServicingODLimit;

	// Commercial Workflow Purpose
	private String approved;
	private BigDecimal securityDeposit = BigDecimal.ZERO;
	private BigDecimal deductFeeDisb = BigDecimal.ZERO;
	private BigDecimal deductInsDisb = BigDecimal.ZERO;

	// Step Details
	@XmlElement
	private boolean stepFinance;
	@XmlElement
	private String stepType;
	@XmlElement
	private String stepPolicy;
	private String lovDescStepPolicyName;
	@XmlElement
	private boolean alwManualSteps;
	private int noOfSteps = 0;
	private boolean shariaApprovalReq;
	private String shariaStatus;
	private String rejectStatus;
	private String rejectReason;
	private boolean isPaymentToBank;
	private boolean isPayToDevSelCust;
	private boolean scheduleChange;

	// Deviation Process
	private boolean deviationApproval;
	// Finance Pre-approval process
	private String finPreApprovedRef;
	private boolean preApprovalFinance;
	private boolean preApprovalExpired;
	@XmlElement
	private String	applicationNo;
	private String 	swiftBranchCode;

	// ===========================================
	// ==========Grace Period Details=============
	// ===========================================

	@XmlElement(name = "grcTerms")
	private int graceTerms = 0;
	@XmlElement
	private Date grcPeriodEndDate;
	@XmlElement
	private String grcRateBasis;
	@XmlElement
	private BigDecimal grcPftRate = BigDecimal.ZERO;
	@XmlElement(name = "grcBaseRate")
	private String graceBaseRate;
	@XmlElement(name = "grcSpecialRate")
	private String graceSpecialRate;
	@XmlElement
	private BigDecimal grcMargin = BigDecimal.ZERO;
	@XmlElement
	private String grcProfitDaysBasis;
	@XmlElement
	private String grcPftFrq;
	@XmlElement
	private Date nextGrcPftDate;
	private boolean allowGrcPftRvw = false;
	@XmlElement
	private String grcPftRvwFrq;
	@XmlElement
	private Date nextGrcPftRvwDate;
	private boolean allowGrcCpz = false;
	@XmlElement
	private String grcCpzFrq;
	@XmlElement
	private Date nextGrcCpzDate;
	@XmlElement
	private boolean allowGrcRepay = false;
	@XmlElement
	private String grcSchdMthd;
	@XmlElement
	private BigDecimal grcMinRate = BigDecimal.ZERO;
	@XmlElement
	private BigDecimal grcMaxRate = BigDecimal.ZERO;

	// Advised profit Rates
	@XmlElement
	private String grcAdvBaseRate;
	@XmlElement
	private BigDecimal grcAdvMargin = BigDecimal.ZERO;
	@XmlElement
	private BigDecimal grcAdvPftRate = BigDecimal.ZERO;

	// ===========================================
	// ==========Repay Period Details=============
	// ===========================================

	@XmlElement
	private int numberOfTerms = 0;
	private int NOInst = 0;
	@XmlElement
	private BigDecimal reqRepayAmount = BigDecimal.ZERO;
	@XmlElement
	private String repayRateBasis;
	@XmlElement(name = "repayPftRate")
	private BigDecimal repayProfitRate = BigDecimal.ZERO;
	@XmlElement
	private String repayBaseRate;
	@XmlElement
	private String repaySpecialRate;
	@XmlElement
	private BigDecimal repayMargin = BigDecimal.ZERO;
	@XmlElement
	private String scheduleMethod;
	@XmlElement
	private String repayPftFrq;
	@XmlElement
	private Date nextRepayPftDate;
	private boolean allowRepayRvw = false;
	@XmlElement
	private String repayRvwFrq;
	@XmlElement
	private Date nextRepayRvwDate;
	private boolean allowRepayCpz = false;
	@XmlElement
	private String repayCpzFrq;
	@XmlElement
	private Date nextRepayCpzDate;
	@XmlElement
	private String repayFrq;
	@XmlElement
	private Date nextRepayDate;
	@XmlElement
	private Date maturityDate;
	@XmlElement
	private boolean finRepayPftOnFrq;
	@XmlElement(name = "repayMinRate")
	private BigDecimal rpyMinRate = BigDecimal.ZERO;
	@XmlElement(name = "repayMaxRate")
	private BigDecimal rpyMaxRate = BigDecimal.ZERO;

	@XmlElement(name = "repayAdvBaseRate")
	private String rpyAdvBaseRate;
	@XmlElement(name = "repayAdvMargin")
	private BigDecimal rpyAdvMargin = BigDecimal.ZERO;
	@XmlElement(name = "repayAdvPftRate")
	private BigDecimal rpyAdvPftRate = BigDecimal.ZERO;
	@XmlElement
	private BigDecimal supplementRent = BigDecimal.ZERO;
	@XmlElement
	private BigDecimal increasedCost = BigDecimal.ZERO;
	private BigDecimal curSuplRent = BigDecimal.ZERO;
	private BigDecimal curIncrCost = BigDecimal.ZERO;

	@XmlElement
	private String rolloverFrq;
	@XmlElement
	private Date nextRolloverDate;
	
	//PV: 10MAY17: remove from exlcuded fields
	private String	calRoundingMode;
	private int	roundingTarget;

	private boolean	alwMultiDisb;

	// ===========================================
	// =========BPI Details ============
	// ===========================================
	@XmlElement(name="alwBpiTreatment")
	private boolean alwBPI = false;
	@XmlElement(name="dftBpiTreatment")
	private String bpiTreatment;
	
	// ===========================================
	// =========Planned EMI Holidays & Deferments
	// ===========================================
	@XmlElement
	private boolean planEMIHAlw = false;
	@XmlElement
	private String planEMIHMethod = "";
	@XmlElement
	private int planEMIHMaxPerYear = 0;
	@XmlElement
	private int planEMIHMax = 0;
	@XmlElement
	private int planEMIHLockPeriod = 0;
	@XmlElement
	private boolean planEMICpz = false;
	@XmlElement
	private int unPlanEMIHLockPeriod = 0;
	@XmlElement
	private boolean unPlanEMICpz = false;
	@XmlElement
	private boolean reAgeCpz = false;
	@XmlElement
	private int	maxUnplannedEmi;
	@XmlElement
	private int	maxReAgeHolidays;

	// ===========================================
	// =========Schedule Build Usage ============
	// ===========================================
	private BigDecimal feeChargeAmt = BigDecimal.ZERO;
	private BigDecimal insuranceAmt = BigDecimal.ZERO;
	private BigDecimal curDisbursementAmt = BigDecimal.ZERO;
	private BigDecimal anualizedPercRate = BigDecimal.ZERO;
	private BigDecimal effectiveRateOfReturn = BigDecimal.ZERO;
	private boolean cpzAtGraceEnd = false;
	private BigDecimal totalGracePft = BigDecimal.ZERO;
	private BigDecimal totalGraceCpz = BigDecimal.ZERO;
	private BigDecimal totalGrossGrcPft = BigDecimal.ZERO;
	private BigDecimal totalProfit = BigDecimal.ZERO;
	private BigDecimal totalCpz = BigDecimal.ZERO;
	private BigDecimal totalGrossPft = BigDecimal.ZERO;
	private BigDecimal totalRepayAmt = BigDecimal.ZERO;
	private boolean calculateRepay = false;
	private boolean equalRepay = true;
	private int calGrcTerms;
	private Date calGrcEndDate;
	private int calTerms;
	private Date calMaturity;
	private BigDecimal firstRepay = BigDecimal.ZERO;
	private BigDecimal lastRepay = BigDecimal.ZERO;
	private BigDecimal finRepaymentAmount = BigDecimal.ZERO;
	private Date eventFromDate;
	private Date eventToDate;
	private Date recalFromDate;
	private Date recalToDate;
	private String recalType;
	private String recalSchdMethod;
	private BigDecimal desiredProfit = BigDecimal.ZERO;
	private String rvwRateApplFor;
	private boolean lovDescAdjClosingBal;
	private BigDecimal totalPriAmt = BigDecimal.ZERO;
	private BigDecimal schPriDue = BigDecimal.ZERO;
	private BigDecimal schPftDue = BigDecimal.ZERO;
	private String schCalOnRvw;
	private String pastduePftCalMthd;
	private String droppingMethod;
	private boolean rateChgAnyDay;
	private BigDecimal pastduePftMargin= BigDecimal.ZERO;
	
	// Finance Maintenance Details
	private int allowedDefRpyChange = 0;
	private int availedDefRpyChange = 0;
	private int allowedDefFrqChange = 0;
	private int availedDefFrqChange = 0;
	private Date lastRepayDate;
	private Date lastRepayPftDate;
	private Date lastRepayRvwDate;
	private Date lastRepayCpzDate;
	private boolean pftIntact = false;
	private int adjTerms = 0;

	// ===========================================
	// =========External Usage Details============
	// ===========================================
	@XmlElement
	private BigDecimal finAssetValue = BigDecimal.ZERO;
	private BigDecimal finCurrAssetValue = BigDecimal.ZERO;
	private Date nextDepDate;
	private Date lastDepDate;
	private String finSourceID = null;
	private String rcdMaintainSts = "";
	private String lovDescFinScheduleOn;
	private String closingStatus;
	private Date finApprovedDate;
	private String lovDescTenorName;
	private boolean migratedFinance;
	private boolean scheduleMaintained;
	private boolean scheduleRegenerated;
	@XmlElement
	private boolean manualSchedule;
	private boolean takeOverFinance;
	private String finStatus;
	private String finStsReason;
	private BigDecimal custDSR = BigDecimal.ZERO;
	private BigDecimal curFinAmount = BigDecimal.ZERO;
	private BigDecimal financingAmount = BigDecimal.ZERO;
	private BigDecimal bpiAmount = BigDecimal.ZERO;
	
	// ManagerCheques
	private BigDecimal lovDescFinancingAmount = BigDecimal.ZERO;

	// ===========================================
	// ===========Validation Details==============
	// ===========================================

	private boolean lovDescIsSchdGenerated = false;
	private String lovDescProductCodeName;
	private BigDecimal availCommitAmount = BigDecimal.ZERO;
	private long jointCustId;
	private String lovDescJointCustCIF;
	private String lovDescJointCustShrtName;
	private boolean jointAccount;

	// ===========================================
	// =========Enquiry Purpose Details===========
	// ===========================================

	private String finAccount;
	private String finCustPftAccount;
	private BigDecimal lovDescAccruedTillLBD = BigDecimal.ZERO;
	private String custStsDescription;

	// ===========================================
	// =============Workflow Details==============
	// ===========================================

	private boolean dedupFound = false;
	private boolean skipDedup = false;
	private boolean proceedDedup = false;
	private boolean blacklisted = false;
	private boolean blacklistOverride = false;
	private boolean limitValid = false;
	private boolean overrideLimit = false;
	private BigDecimal amount = BigDecimal.ZERO;
	private boolean exception;
	private BigDecimal amountBD = BigDecimal.ZERO;
	private BigDecimal amountUSD = BigDecimal.ZERO;
	private BigDecimal maturity = BigDecimal.ZERO;
	private boolean fundsAvailConfirmed;
	private boolean policeCaseFound = false;
	private boolean policeCaseOverride = false;
	private boolean chequeFound = false;
	private boolean chequeOverride = false;
	private BigDecimal score = BigDecimal.ZERO;
	private boolean smecustomer = false;
	private boolean cadrequired = false;
	private boolean feeExists = false;
	private String receiptMode;
	private String receiptModeStatus;
	private String receiptPurpose;
	private BigDecimal waivedAmt = BigDecimal.ZERO;

	// ===========================================
	// =========Purpose to Other Modules==========
	// ===========================================

	private String lovDescCustCoreBank;
	private String lovDescCustFName;
	private String lovDescCustLName;
	private String lovDescSalutationName;
	private String lovDescCustCtgCode;

	private boolean newRecord = false;
	private FinanceMain befImage;

	@XmlTransient
	private LoggedInUser userDetails;
	private String nextUserId = null;
	private int priority;
	private String lovDescAssignMthd;
	private Map<String, String> lovDescBaseRoleCodeMap = null;
	private String lovDescFirstTaskOwner;

	// ===========================================
	// =========DDA Registration Fields==========
	// ===========================================

	private String bankName;
	private String bankNameDesc;
	private String iban;
	private String accountType;
	private String ddaReferenceNo;

	// ===========================================
	// =========Schedule Calculator Purpose ======
	// ===========================================

	private BigDecimal pftForSelectedPeriod = BigDecimal.ZERO;
	private BigDecimal miscAmount = BigDecimal.ZERO;
	private int indexMisc = 0;

	private boolean compareToExpected = false;
	private BigDecimal compareExpectedResult = BigDecimal.ZERO;
	private int compareExpectIndex = 0;
	private String compareWith = "";
	
	private boolean protectSchdPft = false;

	private int schdIndex = 0;
	private int indexStart = 0;
	private int indexEnd = 0;
	private int newMaturityIndex = 0;
	private Date reqMaturity;

	private String procMethod = "";

	private String ifscCode;

	// Can be Deleted
	private int reqTerms;
	private boolean increaseTerms = false;
	private String lovDescFinDivision;
	private String lovValue;
	private String feeAccountId;

	/* Limits */
	@XmlElement
	private String finLimitRef;
	private String lovDescLimitRefName;
	private String productCategory;

	/* Mandate */
	private long mandateID = 0;

	private BigDecimal refundAmount = BigDecimal.ZERO;
	private List<ErrorDetails> errorDetails = new ArrayList<ErrorDetails>();
	private Map<String, String> lovDescNextUsersRolesMap = null;

	private List<SecondaryAccount> secondaryAccount = new ArrayList<SecondaryAccount>();
	
	@XmlElement
	private String salesDepartment;
	private	String salesDepartmentDesc;
	@XmlElement
	private String dmaCode;
	private String dmaCodeDesc;
	@XmlElement
	private String referralId;
	private String referralIdDesc;
	@XmlElement
	private boolean quickDisb;
	private String  wifReference;
	
	private int availedUnPlanEmi=0;
	private int availedReAgeH=0;
	
	private String workFlowType;
	private String nextRoleCodeDesc;
	private String secUsrFullName = "";
	
	// API validation purpose only
	private FinanceMain validateMain = this;
	@XmlElement
	private Date firstDisbDate;
	@XmlElement
	private Date lastDisbDate;
	private int dueBucket=0;
	private int reAgeBucket=0;
	private String finCategory;

	//For Fee Reca Calculation
	private BigDecimal recalFee = BigDecimal.ZERO;
	private int recalTerms = 0;

	public Set<String> getExcludeFields() {
		Set<String> excludeFields = new HashSet<String>();
		excludeFields.add("calculateRepay");
		excludeFields.add("equalRepay");
		excludeFields.add("eventFromDate");
		excludeFields.add("eventToDate");
		excludeFields.add("increaseTerms");
		excludeFields.add("allowedDefRpyChange");
		excludeFields.add("availedDefRpyChange");
		excludeFields.add("allowedDefFrqChange");
		excludeFields.add("availedDefFrqChange");
		excludeFields.add("recalFromDate");
		excludeFields.add("recalToDate");
		excludeFields.add("recalSchdMethod");
		excludeFields.add("reqTerms");
		excludeFields.add("errorDetails");
		excludeFields.add("proceedDedup");
		excludeFields.add("curDisbursementAmt");
		excludeFields.add("amount");
		excludeFields.add("exception");
		excludeFields.add("amountBD");
		excludeFields.add("maturity");
		excludeFields.add("amountUSD");
		excludeFields.add("availCommitAmount");
		excludeFields.add("name");
		excludeFields.add("fundsAvailConfirmed");
		excludeFields.add("pftIntact");
		excludeFields.add("adjTerms");
		excludeFields.add("blacklistOverride");
		excludeFields.add("policeCaseFound");
		excludeFields.add("policeCaseOverride");
		excludeFields.add("score");
		excludeFields.add("chequeFound");
		excludeFields.add("chequeOverride");
		excludeFields.add("curFinAmount");
		excludeFields.add("financingAmount");
		excludeFields.add("smecustomer");
		excludeFields.add("cadrequired");
		excludeFields.add("grcAdvBaseRateDesc");
		excludeFields.add("rpyAdvBaseRateDesc");
		excludeFields.add("bankNameDesc");
		excludeFields.add("mMADate");
		excludeFields.add("custStsDescription");
		excludeFields.add("shariaApprovalReq");
		excludeFields.add("rejectStatus");
		excludeFields.add("rejectReason");
		excludeFields.add("feeExists");
		excludeFields.add("isPaymentToBank");
		excludeFields.add("isPayToDevSelCust");
		excludeFields.add("totalPriAmt");
		excludeFields.add("desiredProfit");
		excludeFields.add("pftForSelectedPeriod");
		excludeFields.add("miscAmount");
		excludeFields.add("indexMisc");
		excludeFields.add("totalEarlyPaidBal");
		excludeFields.add("firstAdjSet");
		excludeFields.add("lastAdjSet");
		excludeFields.add("compareToExpected");
		excludeFields.add("compareExpectedResult");
		excludeFields.add("compareExpectIndex");
		excludeFields.add("compareWith");
		excludeFields.add("protectSchdPft");
		excludeFields.add("schdIndex");
		excludeFields.add("indexStart");
		excludeFields.add("indexEnd");
		excludeFields.add("newMaturityIndex");
		excludeFields.add("recalStartDate");
		excludeFields.add("recalEndDate");
		excludeFields.add("procMethod");
		excludeFields.add("secondaryAccount");
		excludeFields.add("agreeName");
		excludeFields.add("finIsAlwMD");
		excludeFields.add("dsaCodeDesc");
		excludeFields.add("finWriteoffAc");
		excludeFields.add("lovDecMMAReference");
		excludeFields.add("numOfMonths");
		excludeFields.add("ifscCode");
		excludeFields.add("refundAmount");
		excludeFields.add("curSuplRent");
		excludeFields.add("curIncrCost");
		excludeFields.add("schPriDue");
		excludeFields.add("schPftDue");
		excludeFields.add("preApprovalFinance");
		excludeFields.add("preApprovalExpired");
		excludeFields.add("scheduleChange");
		excludeFields.add("stepDetailList");
		excludeFields.add("feeRuleList");
		excludeFields.add("insuranceList");
		excludeFields.add("repayInstList");
		excludeFields.add("rateInstList");
		excludeFields.add("disbInstList");
		excludeFields.add("validateMain");
		excludeFields.add("salesDepartmentDesc");
		excludeFields.add("dmaCodeDesc");
		excludeFields.add("referralIdDesc");
		excludeFields.add("calGrcTerms");
		excludeFields.add("calGrcEndDate");
		excludeFields.add("tDSAmount");
		excludeFields.add("rcu");

		excludeFields.add("nextRoleCodeDesc");
		excludeFields.add("secUsrFullName");
		excludeFields.add("workFlowType");
		excludeFields.add("firstDisbDate");
		excludeFields.add("lastDisbDate");
		excludeFields.add("swiftBranchCode");
		excludeFields.add("receiptMode");
		excludeFields.add("receiptModeStatus");
		excludeFields.add("receiptPurpose");
		excludeFields.add("waivedAmt");
		excludeFields.add("recalFee");
		excludeFields.add("recalTerms");
		excludeFields.add("NOInst");

		return excludeFields;
	}

	// ******************************************************//
	// ****************** getter / setter *******************//
	// ******************************************************//

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

	public String getGraceSpecialRate() {
		return graceSpecialRate;
	}

	public void setGraceSpecialRate(String graceSpecialRate) {
		this.graceSpecialRate = graceSpecialRate;
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

	public String getRepaySpecialRate() {
		return repaySpecialRate;
	}

	public void setRepaySpecialRate(String repaySpecialRate) {
		this.repaySpecialRate = repaySpecialRate;
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

	public BigDecimal getMinDownPayPerc() {
		return minDownPayPerc;
	}

	public void setMinDownPayPerc(BigDecimal minDownPayPerc) {
		this.minDownPayPerc = minDownPayPerc;
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
	
	public int getCalGrcTerms() {
		return calGrcTerms;
	}

	public void setCalGrcTerms(int calGrcTerms) {
		this.calGrcTerms = calGrcTerms;
	}

	public Date getCalGrcEndDate() {
		return calGrcEndDate;
	}

	public void setCalGrcEndDate(Date calGrcEndDate) {
		this.calGrcEndDate = calGrcEndDate;
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

	public LoggedInUser getUserDetails() {
		return userDetails;
	}

	public void setUserDetails(LoggedInUser userDetails) {
		this.userDetails = userDetails;
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

	public BigDecimal getDesiredProfit() {
		return desiredProfit;
	}

	public void setDesiredProfit(BigDecimal desiredProfit) {
		this.desiredProfit = desiredProfit;
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

	public String getGrcPftRvwFrq() {
		return grcPftRvwFrq;
	}

	public void setGrcPftRvwFrq(String grcPftRvwFrq) {
		this.grcPftRvwFrq = grcPftRvwFrq;
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

	public String getLovDescCustCtgCode() {
		return lovDescCustCtgCode;
	}

	public void setLovDescCustCtgCode(String lovDescCustCtgCode) {
		this.lovDescCustCtgCode = lovDescCustCtgCode;
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

	public BigDecimal getLovDescAccruedTillLBD() {
		return lovDescAccruedTillLBD;
	}

	public void setLovDescAccruedTillLBD(BigDecimal lovDescAccruedTillLBD) {
		this.lovDescAccruedTillLBD = lovDescAccruedTillLBD;
	}

	public String getRvwRateApplFor() {
		return rvwRateApplFor;
	}

	public void setRvwRateApplFor(String finRvwRateApplFor) {
		this.rvwRateApplFor = finRvwRateApplFor;
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

	public boolean isManualSchedule() {
		return manualSchedule;
	}

	public void setManualSchedule(boolean manualSchedule) {
		this.manualSchedule = manualSchedule;
	}

	public boolean isTakeOverFinance() {
		return takeOverFinance;
	}

	public void setTakeOverFinance(boolean takeOverFinance) {
		this.takeOverFinance = takeOverFinance;
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

	public BigDecimal getDeductFeeDisb() {
		return deductFeeDisb;
	}

	public void setDeductFeeDisb(BigDecimal deductFeeDisb) {
		this.deductFeeDisb = deductFeeDisb;
	}

	public boolean isFundsAvailConfirmed() {
		return fundsAvailConfirmed;
	}

	public void setFundsAvailConfirmed(boolean fundsAvailConfirmed) {
		this.fundsAvailConfirmed = fundsAvailConfirmed;
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

	public boolean isShariaApprovalReq() {
		return shariaApprovalReq;
	}

	public void setShariaApprovalReq(boolean shariaApprovalReq) {
		this.shariaApprovalReq = shariaApprovalReq;
	}

	public String getShariaStatus() {
		return shariaStatus;
	}

	public void setShariaStatus(String shariaStatus) {
		this.shariaStatus = shariaStatus;
	}

	public boolean isPftIntact() {
		return pftIntact;
	}

	public void setPftIntact(boolean pftIntact) {
		this.pftIntact = pftIntact;
	}

	public boolean isPaymentToBank() {
		return isPaymentToBank;
	}

	public void setPaymentToBank(boolean isPaymentToBank) {
		this.isPaymentToBank = isPaymentToBank;
	}

	public boolean isPayToDevSelCust() {
		return isPayToDevSelCust;
	}

	public void setPayToDevSelCust(boolean isPayToDevSelCust) {
		this.isPayToDevSelCust = isPayToDevSelCust;
	}

	public int getAdjTerms() {
		return adjTerms;
	}

	public void setAdjTerms(int adjTerms) {
		this.adjTerms = adjTerms;
	}

	@XmlTransient
	public HashMap<String, Object> getDeclaredFieldValues() {
		HashMap<String, Object> fieldsAndValuesMap = new HashMap<String, Object>();
		
		getDeclaredFieldValues(fieldsAndValuesMap);
		
		return fieldsAndValuesMap;
	}

	public void getDeclaredFieldValues(HashMap<String, Object> fieldsAndValuesMap) {
		for (int i = 0; i < this.getClass().getDeclaredFields().length; i++) {
			try {
				//"fm_" Should be in small case only, if we want to change the case we need to update the configuration fields as well.
				fieldsAndValuesMap.put("fm_" + this.getClass().getDeclaredFields()[i].getName(), this.getClass()
						.getDeclaredFields()[i].get(this));
			} catch (SecurityException | IllegalArgumentException | IllegalAccessException e) {
				// Nothing TO DO
			}
		}
	}
	
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

	public String getLovDescFinProduct() {
		return lovDescFinProduct;
	}

	public void setLovDescFinProduct(String lovdescFinProduct) {
		this.lovDescFinProduct = lovdescFinProduct;
	}

	public String getLovDescMobileNumber() {
		return lovDescMobileNumber;
	}

	public void setLovDescMobileNumber(String lovdescMobileNumber) {
		this.lovDescMobileNumber = lovdescMobileNumber;
	}

	public String getLovDescRequestStage() {
		return lovDescRequestStage;
	}

	public void setLovDescRequestStage(String lovdescRequestStage) {
		this.lovDescRequestStage = lovdescRequestStage;
	}

	public String getLovDescQueuePriority() {
		return lovDescQueuePriority;
	}

	public void setLovDescQueuePriority(String lovDescQueuePriority) {
		this.lovDescQueuePriority = lovDescQueuePriority;
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

	public boolean isBlacklistOverride() {
		return blacklistOverride;
	}

	public void setBlacklistOverride(boolean blacklistOverride) {
		this.blacklistOverride = blacklistOverride;
	}

	public String getNextUserId() {
		return nextUserId;
	}

	public void setNextUserId(String nextUserId) {
		this.nextUserId = nextUserId;
	}

	public int getPriority() {
		return priority;
	}

	public void setPriority(int priority) {
		this.priority = priority;
	}

	public Map<String, String> getLovDescNextUsersRolesMap() {
		return lovDescNextUsersRolesMap;
	}

	public void setLovDescNextUsersRolesMap(Map<String, String> lovDescNextUsersRolesMap) {
		this.lovDescNextUsersRolesMap = lovDescNextUsersRolesMap;
	}

	public boolean isPoliceCaseFound() {
		return policeCaseFound;
	}

	public void setPoliceCaseFound(boolean policeCaseFound) {
		this.policeCaseFound = policeCaseFound;
	}

	public boolean isPoliceCaseOverride() {
		return policeCaseOverride;
	}

	public void setPoliceCaseOverride(boolean policeCaseOverride) {
		this.policeCaseOverride = policeCaseOverride;
	}

	public BigDecimal getScore() {
		return score;
	}

	public void setScore(BigDecimal score) {
		this.score = score;
	}

	public boolean isSmecustomer() {
		return smecustomer;
	}

	public void setSmecustomer(boolean smecustomer) {
		this.smecustomer = smecustomer;
	}

	public boolean isCadrequired() {
		return cadrequired;
	}

	public void setCadrequired(boolean cadrequired) {
		this.cadrequired = cadrequired;
	}

	public String getLovDescAssignMthd() {
		return lovDescAssignMthd;
	}

	public void setLovDescAssignMthd(String lovDescAssignMthd) {
		this.lovDescAssignMthd = lovDescAssignMthd;
	}

	public Map<String, String> getLovDescBaseRoleCodeMap() {
		return lovDescBaseRoleCodeMap;
	}

	public void setLovDescBaseRoleCodeMap(Map<String, String> lovDescBaseRoleCodeMap) {
		this.lovDescBaseRoleCodeMap = lovDescBaseRoleCodeMap;
	}

	public boolean isChequeFound() {
		return chequeFound;
	}

	public void setChequeFound(boolean chequeFound) {
		this.chequeFound = chequeFound;
	}

	public boolean isChequeOverride() {
		return chequeOverride;
	}

	public void setChequeOverride(boolean chequeOverride) {
		this.chequeOverride = chequeOverride;
	}

	public String getLovDescFirstTaskOwner() {
		return lovDescFirstTaskOwner;
	}

	public void setLovDescFirstTaskOwner(String lovDescFirstTaskOwner) {
		this.lovDescFirstTaskOwner = lovDescFirstTaskOwner;
	}

	public BigDecimal getGrcMinRate() {
		return grcMinRate;
	}

	public void setGrcMinRate(BigDecimal grcMinRate) {
		this.grcMinRate = grcMinRate;
	}

	public BigDecimal getGrcMaxRate() {
		return grcMaxRate;
	}

	public void setGrcMaxRate(BigDecimal grcMaxRate) {
		this.grcMaxRate = grcMaxRate;
	}

	public BigDecimal getRpyMinRate() {
		return rpyMinRate;
	}

	public void setRpyMinRate(BigDecimal rpyMinRate) {
		this.rpyMinRate = rpyMinRate;
	}

	public BigDecimal getRpyMaxRate() {
		return rpyMaxRate;
	}

	public void setRpyMaxRate(BigDecimal rpyMaxRate) {
		this.rpyMaxRate = rpyMaxRate;
	}

	public boolean isDeviationApproval() {
		return deviationApproval;
	}

	public void setDeviationApproval(boolean deviationApproval) {
		this.deviationApproval = deviationApproval;
	}

	public BigDecimal getCurFinAmount() {
		return curFinAmount;
	}

	public void setCurFinAmount(BigDecimal curFinAmount) {
		this.curFinAmount = curFinAmount;
	}

	public BigDecimal getFinancingAmount() {
		return financingAmount;
	}

	public void setFinancingAmount(BigDecimal financingAmount) {
		this.financingAmount = financingAmount;
	}

	public String getGrcAdvBaseRate() {
		return grcAdvBaseRate;
	}

	public void setGrcAdvBaseRate(String grcAdvBaseRate) {
		this.grcAdvBaseRate = grcAdvBaseRate;
	}

	public BigDecimal getGrcAdvMargin() {
		return grcAdvMargin;
	}

	public void setGrcAdvMargin(BigDecimal grcAdvMargin) {
		this.grcAdvMargin = grcAdvMargin;
	}

	public BigDecimal getGrcAdvPftRate() {
		return grcAdvPftRate;
	}

	public void setGrcAdvPftRate(BigDecimal grcAdvPftRate) {
		this.grcAdvPftRate = grcAdvPftRate;
	}

	public String getRpyAdvBaseRate() {
		return rpyAdvBaseRate;
	}

	public void setRpyAdvBaseRate(String rpyAdvBaseRate) {
		this.rpyAdvBaseRate = rpyAdvBaseRate;
	}

	public BigDecimal getRpyAdvMargin() {
		return rpyAdvMargin;
	}

	public void setRpyAdvMargin(BigDecimal rpyAdvMargin) {
		this.rpyAdvMargin = rpyAdvMargin;
	}

	public BigDecimal getRpyAdvPftRate() {
		return rpyAdvPftRate;
	}

	public void setRpyAdvPftRate(BigDecimal rpyAdvPftRate) {
		this.rpyAdvPftRate = rpyAdvPftRate;
	}

	public BigDecimal getSupplementRent() {
		return supplementRent;
	}

	public void setSupplementRent(BigDecimal supplementRent) {
		this.supplementRent = supplementRent;
	}

	public BigDecimal getIncreasedCost() {
		return increasedCost;
	}

	public void setIncreasedCost(BigDecimal increasedCost) {
		this.increasedCost = increasedCost;
	}

	public String getRolloverFrq() {
		return rolloverFrq;
	}

	public void setRolloverFrq(String rolloverFrq) {
		this.rolloverFrq = rolloverFrq;
	}

	public Date getNextRolloverDate() {
		return nextRolloverDate;
	}

	public void setNextRolloverDate(Date nextRolloverDate) {
		this.nextRolloverDate = nextRolloverDate;
	}

	public long getInitiateUser() {
		return initiateUser;
	}

	public void setInitiateUser(long initiateUser) {
		this.initiateUser = initiateUser;
	}

	public String getBankName() {
		return bankName;
	}

	public void setBankName(String bankName) {
		this.bankName = bankName;
	}

	public String getIban() {
		return iban;
	}

	public void setIban(String iban) {
		this.iban = iban;
	}

	public String getAccountType() {
		return accountType;
	}

	public void setAccountType(String accountType) {
		this.accountType = accountType;
	}

	public String getDdaReferenceNo() {
		return ddaReferenceNo;
	}

	public void setDdaReferenceNo(String ddaReferenceNo) {
		this.ddaReferenceNo = ddaReferenceNo;
	}

	public String getBankNameDesc() {
		return bankNameDesc;
	}

	public void setBankNameDesc(String bankNameDesc) {
		this.bankNameDesc = bankNameDesc;
	}

	public String getCustStsDescription() {
		return custStsDescription;
	}

	public void setCustStsDescription(String custStsDescription) {
		this.custStsDescription = custStsDescription;
	}

	public Date getInitiateDate() {
		return initiateDate;
	}

	public void setInitiateDate(Date initiateDate) {
		this.initiateDate = initiateDate;
	}

	public String getRejectStatus() {
		return rejectStatus;
	}

	public void setRejectStatus(String rejectStatus) {
		this.rejectStatus = rejectStatus;
	}

	public String getRejectReason() {
		return rejectReason;
	}

	public void setRejectReason(String rejectReason) {
		this.rejectReason = rejectReason;
	}

	public boolean isFeeExists() {
		return feeExists;
	}

	public void setFeeExists(boolean feeExists) {
		this.feeExists = feeExists;
	}

	public BigDecimal getTotalPriAmt() {
		return totalPriAmt;
	}

	public void setTotalPriAmt(BigDecimal totalPriAmt) {
		this.totalPriAmt = totalPriAmt;
	}

	public String getLovValue() {
		return lovValue;
	}

	public void setLovValue(String lovValue) {
		this.lovValue = lovValue;
	}

	public String getAccountsOfficer() {
		return accountsOfficer;
	}

	public void setAccountsOfficer(String accountsOfficer) {
		this.accountsOfficer = accountsOfficer;
	}

	public String getDsaCode() {
		return dsaCode;
	}

	public void setDsaCode(String dsaCode) {
		this.dsaCode = dsaCode;
	}

	public String getDsaCodeDesc() {
		return dsaCodeDesc;
	}

	public void setDsaCodeDesc(String dsaCodeDesc) {
		this.dsaCodeDesc = dsaCodeDesc;
	}

	public String getFinPreApprovedRef() {
		return finPreApprovedRef;
	}

	public void setFinPreApprovedRef(String finPreApprovedRef) {
		this.finPreApprovedRef = finPreApprovedRef;
	}

	public boolean isPreApprovalFinance() {
		return preApprovalFinance;
	}

	public void setPreApprovalFinance(boolean preApprovalFinance) {
		this.preApprovalFinance = preApprovalFinance;
	}

	public boolean isPreApprovalExpired() {
		return preApprovalExpired;
	}

	public void setPreApprovalExpired(boolean preApprovalExpired) {
		this.preApprovalExpired = preApprovalExpired;
	}

	public String getFeeAccountId() {
		return feeAccountId;
	}

	public void setFeeAccountId(String feeAccountId) {
		this.feeAccountId = feeAccountId;
	}

	public BigDecimal getPftForSelectedPeriod() {
		return pftForSelectedPeriod;
	}

	public void setPftForSelectedPeriod(BigDecimal pftForSelectedPeriod) {
		this.pftForSelectedPeriod = pftForSelectedPeriod;
	}


	public boolean isCompareToExpected() {
		return compareToExpected;
	}

	public void setCompareToExpected(boolean compareToExpected) {
		this.compareToExpected = compareToExpected;
	}

	public boolean isProtectSchdPft() {
		return protectSchdPft;
	}

	public void setProtectSchdPft(boolean protectSchdPft) {
		this.protectSchdPft = protectSchdPft;
	}

	public int getSchdIndex() {
		return schdIndex;
	}

	public void setSchdIndex(int schdIndex) {
		this.schdIndex = schdIndex;
	}

	public int getIndexStart() {
		return indexStart;
	}

	public void setIndexStart(int indexStart) {
		this.indexStart = indexStart;
	}

	public int getIndexEnd() {
		return indexEnd;
	}

	public void setIndexEnd(int indexEnd) {
		this.indexEnd = indexEnd;
	}

	public int getNewMaturityIndex() {
		return newMaturityIndex;
	}

	public void setNewMaturityIndex(int newMaturityIndex) {
		this.newMaturityIndex = newMaturityIndex;
	}

	public String getProcMethod() {
		return procMethod;
	}

	public void setProcMethod(String procMethod) {
		this.procMethod = procMethod;
	}

	public List<SecondaryAccount> getSecondaryAccount() {
		return secondaryAccount;
	}

	public void setSecondaryAccount(List<SecondaryAccount> secondaryAccount) {
		this.secondaryAccount = secondaryAccount;
	}

	public String getAgreeName() {
		return agreeName;
	}

	public void setAgreeName(String agreeName) {
		this.agreeName = agreeName;
	}

	public String getFinCancelAc() {
		return finCancelAc;
	}

	public void setFinCancelAc(String finCancelAc) {
		this.finCancelAc = finCancelAc;
	}

	public String getLovDescAccountsOfficer() {
		return lovDescAccountsOfficer;
	}

	public void setLovDescAccountsOfficer(String lovDescAccountsOfficer) {
		this.lovDescAccountsOfficer = lovDescAccountsOfficer;
	}

	public long getMMAId() {
		return mMAId;
	}

	public void setMMAId(long mMAId) {
		this.mMAId = mMAId;
	}

	public String getFinWriteoffAc() {
		return finWriteoffAc;
	}

	public void setFinWriteoffAc(String finWriteoffAc) {
		this.finWriteoffAc = finWriteoffAc;
	}

	public String getLovDescMMAReference() {
		return lovDescMMAReference;
	}

	public void setLovDescMMAReference(String lovDescMMAReference) {
		this.lovDescMMAReference = lovDescMMAReference;
	}

	public String getIfscCode() {
		return ifscCode;
	}

	public void setIfscCode(String ifscCode) {
		this.ifscCode = ifscCode;
	}

	public BigDecimal getLovDescFinancingAmount() {
		return lovDescFinancingAmount;
	}

	public void setLovDescFinancingAmount(BigDecimal lovDescFinancingAmount) {
		this.lovDescFinancingAmount = lovDescFinancingAmount;
	}

	public boolean isTDSApplicable() {
		return tDSApplicable;
	}

	public void setTDSApplicable(boolean tDSApplicable) {
		this.tDSApplicable = tDSApplicable;
	}

	public String getFinLimitRef() {
		return finLimitRef;
	}

	public void setFinLimitRef(String finLimitRef) {
		this.finLimitRef = finLimitRef;
	}

	public String getLovDescLimitRefName() {
		return lovDescLimitRefName;
	}

	public void setLovDescLimitRefName(String lovDescLimitRefName) {
		this.lovDescLimitRefName = lovDescLimitRefName;
	}

	public BigDecimal getRefundAmount() {
		return refundAmount;
	}
	public void setRefundAmount(BigDecimal refundAmount) {
		this.refundAmount = refundAmount;
	}

	public BigDecimal getCurSuplRent() {
		return curSuplRent;
	}
	public void setCurSuplRent(BigDecimal curSuplRent) {
		this.curSuplRent = curSuplRent;
	}

	public BigDecimal getCurIncrCost() {
		return curIncrCost;
	}
	public void setCurIncrCost(BigDecimal curIncrCost) {
		this.curIncrCost = curIncrCost;
	}

	public BigDecimal getSchPriDue() {
		return schPriDue;
	}
	public void setSchPriDue(BigDecimal schPriDue) {
		this.schPriDue = schPriDue;
	}

	public BigDecimal getSchPftDue() {
		return schPftDue;
	}
	public void setSchPftDue(BigDecimal schPftDue) {
		this.schPftDue = schPftDue;
	}

	public boolean isScheduleChange() {
		return scheduleChange;
	}
	public void setScheduleChange(boolean scheduleChange) {
		this.scheduleChange = scheduleChange;
	}

	public String getProductCategory() {
		return productCategory;
	}
	public void setProductCategory(String productCategory) {
		this.productCategory = productCategory;
	}

	public long getMandateID() {
		return mandateID;
	}
	public void setMandateID(long mandateID) {
		this.mandateID = mandateID;
	}

	public String getStepType() {
		return stepType;
	}
	public void setStepType(String stepType) {
		this.stepType = stepType;
	}

	public String getDroplineFrq() {
		return droplineFrq;
	}
	public void setDroplineFrq(String droplineFrq) {
		this.droplineFrq = droplineFrq;
	}

	public boolean isPftServicingODLimit() {
		return pftServicingODLimit;
	}
	public void setPftServicingODLimit(boolean pftServicingODLimit) {
		this.pftServicingODLimit = pftServicingODLimit;
	}

	public Date getFirstDroplineDate() {
		return firstDroplineDate;
	}
	public void setFirstDroplineDate(Date firstDroplineDate) {
		this.firstDroplineDate = firstDroplineDate;
	}

	public BigDecimal getInsuranceAmt() {
		return insuranceAmt;
	}
	public void setInsuranceAmt(BigDecimal insuranceAmt) {
		this.insuranceAmt = insuranceAmt;
	}

	public BigDecimal getDeductInsDisb() {
		return deductInsDisb;
	}
	public void setDeductInsDisb(BigDecimal deductInsDisb) {
		this.deductInsDisb = deductInsDisb;
	}

	public boolean isAlwBPI() {
		return alwBPI;
	}
	public void setAlwBPI(boolean alwBPI) {
		this.alwBPI = alwBPI;
	}

	public String getBpiTreatment() {
		return bpiTreatment;
	}
	public void setBpiTreatment(String bpiTreatment) {
		this.bpiTreatment = bpiTreatment;
	}

	public boolean isPlanEMIHAlw() {
		return planEMIHAlw;
	}
	public void setPlanEMIHAlw(boolean planEMIHAlw) {
		this.planEMIHAlw = planEMIHAlw;
	}

	public String getPlanEMIHMethod() {
		return planEMIHMethod;
	}
	public void setPlanEMIHMethod(String planEMIHMethod) {
		this.planEMIHMethod = planEMIHMethod;
	}

	public int getPlanEMIHMaxPerYear() {
		return planEMIHMaxPerYear;
	}
	public void setPlanEMIHMaxPerYear(int planEMIHMaxPerYear) {
		this.planEMIHMaxPerYear = planEMIHMaxPerYear;
	}

	public int getPlanEMIHMax() {
		return planEMIHMax;
	}
	public void setPlanEMIHMax(int planEMIHMax) {
		this.planEMIHMax = planEMIHMax;
	}

	public boolean isPlanEMICpz() {
		return planEMICpz;
	}
	public void setPlanEMICpz(boolean planEMICpz) {
		this.planEMICpz = planEMICpz;
	}

	public String getCalRoundingMode() {
		return calRoundingMode;
	}
	public void setCalRoundingMode(String calRoundingMode) {
		this.calRoundingMode = calRoundingMode;
	}

	public boolean isAlwMultiDisb() {
		return alwMultiDisb;
	}
	public void setAlwMultiDisb(boolean alwMultiDisb) {
		this.alwMultiDisb = alwMultiDisb;
	}
	public String getSalesDepartment() {
		return salesDepartment;
	}

	public void setSalesDepartment(String salesDepartment) {
		this.salesDepartment = salesDepartment;
	}
	public String getDmaCode() {
		return dmaCode;
	}

	public void setDmaCode(String dmaCode) {
		this.dmaCode = dmaCode;
	}
	public String getReferralId() {
		return referralId;
	}

	public void setReferralId(String referralId) {
		this.referralId = referralId;
	}

	public String getApplicationNo() {
		return applicationNo;
	}

	public void setApplicationNo(String applicationNo) {
		this.applicationNo = applicationNo;
	}

	public long getmMAId() {
		return mMAId;
	}

	public void setmMAId(long mMAId) {
		this.mMAId = mMAId;
	}

	public boolean isFinIsAlwMD() {
		return finIsAlwMD;
	}

	public void setFinIsAlwMD(boolean finIsAlwMD) {
		this.finIsAlwMD = finIsAlwMD;
	}

	public boolean istDSApplicable() {
		return tDSApplicable;
	}

	public void settDSApplicable(boolean tDSApplicable) {
		this.tDSApplicable = tDSApplicable;
	}

	public int getPlanEMIHLockPeriod() {
		return planEMIHLockPeriod;
	}

	public void setPlanEMIHLockPeriod(int planEMIHLockPeriod) {
		this.planEMIHLockPeriod = planEMIHLockPeriod;
	}

	public int getUnPlanEMIHLockPeriod() {
		return unPlanEMIHLockPeriod;
	}

	public void setUnPlanEMIHLockPeriod(int unPlanEMIHLockPeriod) {
		this.unPlanEMIHLockPeriod = unPlanEMIHLockPeriod;
	}

	public boolean isUnPlanEMICpz() {
		return unPlanEMICpz;
	}

	public void setUnPlanEMICpz(boolean unPlanEMICpz) {
		this.unPlanEMICpz = unPlanEMICpz;
	}

	public boolean isReAgeCpz() {
		return reAgeCpz;
	}

	public void setReAgeCpz(boolean reAgeCpz) {
		this.reAgeCpz = reAgeCpz;
	}

	public String getRecalSchdMethod() {
		return recalSchdMethod;
	}

	public void setRecalSchdMethod(String recalSchdMethod) {
		this.recalSchdMethod = recalSchdMethod;
	}

	public int getIndexMisc() {
		return indexMisc;
	}

	public void setIndexMisc(int indexMisc) {
		this.indexMisc = indexMisc;
	}

	public BigDecimal getCompareExpectedResult() {
		return compareExpectedResult;
	}

	public void setCompareExpectedResult(BigDecimal compareExpectedResult) {
		this.compareExpectedResult = compareExpectedResult;
	}

	public int getCompareExpectIndex() {
		return compareExpectIndex;
	}

	public void setCompareExpectIndex(int compareExpectIndex) {
		this.compareExpectIndex = compareExpectIndex;
	}

	public String getCompareWith() {
		return compareWith;
	}

	public void setCompareWith(String compareWith) {
		this.compareWith = compareWith;
	}

	public FinanceMain getValidateMain() {
		return validateMain;
	}

	public void setValidateMain(FinanceMain validateMain) {
		this.validateMain = validateMain;
	}

	public void setErrorDetails(List<ErrorDetails> errorDetails) {
		this.errorDetails = errorDetails;
	}

	public BigDecimal getMiscAmount() {
		return miscAmount;
	}

	public void setMiscAmount(BigDecimal miscAmount) {
		this.miscAmount = miscAmount;
	}
	
	public boolean isQuickDisb() {
		return quickDisb;
	}

	public void setQuickDisb(boolean quickDisb) {
		this.quickDisb = quickDisb;
	}

	public String getSalesDepartmentDesc() {
		return salesDepartmentDesc;
	}

	public void setSalesDepartmentDesc(String salesDepartmentDesc) {
		this.salesDepartmentDesc = salesDepartmentDesc;
	}

	public String getDmaCodeDesc() {
		return dmaCodeDesc;
	}

	public void setDmaCodeDesc(String dmaCodeDesc) {
		this.dmaCodeDesc = dmaCodeDesc;
	}

	public String getReferralIdDesc() {
		return referralIdDesc;
	}

	public void setReferralIdDesc(String referralIdDesc) {
		this.referralIdDesc = referralIdDesc;
	}

	public String getWifReference() {
		return wifReference;
	}

	public void setWifReference(String wifReference) {
		this.wifReference = wifReference;
	}

	public int getMaxUnplannedEmi() {
		return maxUnplannedEmi;
	}

	public void setMaxUnplannedEmi(int maxUnplannedEmi) {
		this.maxUnplannedEmi = maxUnplannedEmi;
	}

	public int getMaxReAgeHolidays() {
		return maxReAgeHolidays;
	}

	public void setMaxReAgeHolidays(int maxReAgeHolidays) {
		this.maxReAgeHolidays = maxReAgeHolidays;
	}

	public int getAvailedUnPlanEmi() {
		return availedUnPlanEmi;
	}

	public void setAvailedUnPlanEmi(int availedUnPlanEmi) {
		this.availedUnPlanEmi = availedUnPlanEmi;
	}

	public int getAvailedReAgeH() {
		return availedReAgeH;
	}

	public void setAvailedReAgeH(int availedReAgeH) {
		this.availedReAgeH = availedReAgeH;
	}

	public String getNextUsrName() {
		return "";
	}

	public String getWorkFlowType() {
		return workFlowType;
	}

	public void setWorkFlowType(String workFlowType) {
		this.workFlowType = workFlowType;
	}

	public String getCustShrtName() {
		return this.getLovDescCustShrtName();
	}

	public String getCustCIF() {
		return this.getLovDescCustCIF();
	}

	public String getNextRoleCodeDesc() {
		return nextRoleCodeDesc;
	}

	public void setNextRoleCodeDesc(String nextRoleCodeDesc) {
		this.nextRoleCodeDesc = nextRoleCodeDesc;
	}

	public String getSecUsrFullName() {
		return secUsrFullName;
	}

	public void setSecUsrFullName(String secUsrFullName) {
		this.secUsrFullName = secUsrFullName;
	}
	
	public void resetRecalculationFields() {
		this.recalFromDate = null;
		this.recalToDate = null;
		this.recalSchdMethod = null;
		this.recalType = null;
		this.adjTerms = 0;
		this.eventFromDate = null;
		this.eventToDate = null;
		this.pftIntact = false;
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

	public String getSwiftBranchCode() {
		return swiftBranchCode;
	}

	public void setSwiftBranchCode(String swiftBranchCode) {
		this.swiftBranchCode = swiftBranchCode;
	}

	public BigDecimal getBpiAmount() {
		return bpiAmount;
	}

	public void setBpiAmount(BigDecimal bpiAmount) {
		this.bpiAmount = bpiAmount;
	}

	public Timestamp getPrevMntOn() {
		return befImage == null ? null : befImage.getLastMntOn();
	}

	public String getPromotionCode() {
		return promotionCode;
	}

	public void setPromotionCode(String promotionCode) {
		this.promotionCode = promotionCode;
	}

	public String getSchCalOnRvw() {
		return schCalOnRvw;
	}

	public void setSchCalOnRvw(String schCalOnRvw) {
		this.schCalOnRvw = schCalOnRvw;
	}

	public String getPastduePftCalMthd() {
		return pastduePftCalMthd;
	}

	public void setPastduePftCalMthd(String pastduePftCalMthd) {
		this.pastduePftCalMthd = pastduePftCalMthd;
	}

	public String getDroppingMethod() {
		return droppingMethod;
	}

	public void setDroppingMethod(String droppingMethod) {
		this.droppingMethod = droppingMethod;
	}

	public boolean isRateChgAnyDay() {
		return rateChgAnyDay;
	}

	public void setRateChgAnyDay(boolean rateChgAnyDay) {
		this.rateChgAnyDay = rateChgAnyDay;
	}

	public BigDecimal getPastduePftMargin() {
		return pastduePftMargin;
	}

	public void setPastduePftMargin(BigDecimal pastduePftMargin) {
		this.pastduePftMargin = pastduePftMargin;
	}

	public String getReceiptMode() {
		return receiptMode;
	}

	public void setReceiptMode(String receiptMode) {
		this.receiptMode = receiptMode;
	}

	public int getDueBucket() {
		return dueBucket;
	}

	public void setDueBucket(int dueBucket) {
		this.dueBucket = dueBucket;
	}

	public int getRoundingTarget() {
		return roundingTarget;
	}

	public void setRoundingTarget(int roundingTarget) {
		this.roundingTarget = roundingTarget;
	}

	public BigDecimal getRecalFee() {
		return recalFee;
	}

	public void setRecalFee(BigDecimal recalFee) {
		this.recalFee = recalFee;
	}

	public int getRecalTerms() {
		return recalTerms;
	}

	public void setRecalTerms(int recalTerms) {
		this.recalTerms = recalTerms;
	}

	public int getReAgeBucket() {
		return reAgeBucket;
	}

	public void setReAgeBucket(int reAgeBucket) {
		this.reAgeBucket = reAgeBucket;
	}

	public String getFinCategory() {
		return finCategory;
	}

	public void setFinCategory(String finCategory) {
		this.finCategory = finCategory;
	}

	public String getReceiptPurpose() {
		return receiptPurpose;
	}

	public void setReceiptPurpose(String receiptPurpose) {
		this.receiptPurpose = receiptPurpose;
	}

	public BigDecimal getWaivedAmt() {
		return waivedAmt;
	}

	public void setWaivedAmt(BigDecimal waivedAmt) {
		this.waivedAmt = waivedAmt;
	}
	
	public int getNOInst() {
		return NOInst;
	}

	public void setNOInst(int nOInst) {
		NOInst = nOInst;
	}

	public String getReceiptModeStatus() {
		return receiptModeStatus;
	}

	public void setReceiptModeStatus(String receiptModeStatus) {
		this.receiptModeStatus = receiptModeStatus;
	}
	
	public boolean isRcu() {
		return rcu;
	}

	public void setRcu(boolean rcu) {
		this.rcu = rcu;
	}
}
