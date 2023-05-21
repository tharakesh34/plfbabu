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
 * * FileName : FinanceMain.java * * Author : PENNANT TECHONOLOGIES * * Creation Date : 05-05-2011 * * Modified Date :
 * 05-05-2011 * * Description : * *
 ********************************************************************************************
 * Date Author Version Comments *
 ********************************************************************************************
 * 05-05-2011 Pennant 0.1 * * * * * * * * *
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
import java.util.Map.Entry;
import java.util.Set;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;

import org.apache.commons.lang.StringUtils;

import com.pennant.backend.model.eventproperties.EventProperties;
import com.pennant.backend.model.reason.details.ReasonDetails;
import com.pennant.backend.model.rulefactory.ReturnDataSet;
import com.pennanttech.pennapps.core.model.AbstractWorkflowEntity;
import com.pennanttech.pennapps.core.model.ErrorDetail;
import com.pennanttech.pennapps.core.model.LoggedInUser;

/**
 * Model class for the <b>FinanceMain table</b>.<br>
 * 
 */

@XmlType(propOrder = { "applicationNo", "lovDescCustCIF", "finType", "finCcy", "finBranch", "finReference",
		"profitDaysBasis", "finAmount", "finAssetValue", "downPayBank", "downPaySupl", "finRepayMethod", "finStartDate",
		"firstDroplineDate", "allowGrcPeriod", "tDSApplicable", "manualSchedule", "planDeferCount", "stepFinance",
		"alwManualSteps", "stepPolicy", "stepType", "graceTerms", "grcStartDate", "grcPeriodEndDate", "grcRateBasis",
		"grcPftRate", "graceBaseRate", "graceSpecialRate", "grcMargin", "grcProfitDaysBasis", "grcPftFrq",
		"nextGrcPftDate", "grcPftRvwFrq", "nextGrcPftRvwDate", "grcCpzFrq", "nextGrcCpzDate", "allowGrcRepay",
		"grcSchdMthd", "grcMinRate", "grcMaxRate", "grcMaxAmount", "numberOfTerms", "reqRepayAmount", "repayRateBasis",
		"repayProfitRate", "repayBaseRate", "repaySpecialRate", "repayMargin", "scheduleMethod", "repayFrq",
		"nextRepayDate", "repayPftFrq", "nextRepayPftDate", "repayRvwFrq", "nextRepayRvwDate", "repayCpzFrq",
		"nextRepayCpzDate", "maturityDate", "finRepayPftOnFrq", "rpyMinRate", "rpyMaxRate", "finContractDate",
		"finPurpose", "finLimitRef", "finCommitmentRef", "dsaCode", "dsaName", "dsaCodeDesc", "accountsOfficer",
		"salesDepartment", "dmaCode", "referralId", "employeeName", "quickDisb", "unPlanEMIHLockPeriod", "unPlanEMICpz",
		"reAgeCpz", "maxUnplannedEmi", "maxReAgeHolidays", "alwBPI", "bpiTreatment", "bpiPftDaysBasis", "planEMIHAlw",
		"planEMIHAlwInGrace", "planEMIHMethod", "planEMIHMaxPerYear", "planEMIHMax", "planEMIHLockPeriod", "planEMICpz",
		"firstDisbDate", "lastDisbDate", "stage", "status", "product", "advTerms", "closedDate", "fixedRateTenor",
		"fixedTenorRate", "eligibilityMethod", "connector", "legalRequired", "reqLoanAmt", "reqLoanTenor",
		"offerProduct", "offerAmount", "custSegmentation", "baseProduct", "processType", "bureauTimeSeries",
		"campaignName", "existingLanRefNo", "leadSource", "poSource", "rsa", "verification", "sourChannelCategory",
		"offerId", "endGrcPeriodAftrFullDisb", "autoIncGrcEndDate", "noOfSteps", "calcOfSteps", "stepsAppliedFor",
		"noOfGrcSteps", "mandateID", "finIsActive" })
@XmlRootElement(name = "financeDetail")
@XmlAccessorType(XmlAccessType.NONE)
public class FinanceMain extends AbstractWorkflowEntity {
	private static final long serialVersionUID = -3026443763391506067L;

	private long finID;
	@XmlElement
	private String finReference;

	private String linkedFinRef;
	private String investmentRef = "";
	@XmlElement
	private String finType;
	@XmlElement(name = "finTypeDesc")
	private String lovDescFinTypeName;
	@XmlElement
	private String promotionCode;
	@XmlElement
	private String finCcy;
	@XmlElement
	private String profitDaysBasis;
	private long custID;
	@XmlElement(name = "cif")
	private String lovDescCustCIF;
	@XmlElement(name = "shortName")
	private String lovDescCustShrtName;
	@XmlElement
	private String finBranch;
	private String lovDescFinBranchName;
	private String finBranchProvinceCode;
	@XmlElement
	private Date finStartDate;
	@XmlElement
	private Date finContractDate;
	@XmlElement
	private BigDecimal finAmount = BigDecimal.ZERO;
	private BigDecimal earlyPayAmount = BigDecimal.ZERO;
	private BigDecimal minDownPayPerc = BigDecimal.ZERO;
	private BigDecimal downPayment = BigDecimal.ZERO;
	private BigDecimal advanceEMI = BigDecimal.ZERO;
	@XmlElement
	private BigDecimal downPayBank = BigDecimal.ZERO;
	@XmlElement
	private BigDecimal downPaySupl = BigDecimal.ZERO;
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
	private boolean finIsActive;
	private String finRemarks;
	private long initiateUser;
	private Date initiateDate;
	private boolean finIsAlwMD;
	private long accountsOfficer;
	@XmlElement(name = "accountsOfficer")
	private String accountsOfficerReference;
	private String dsaCode;
	@XmlElement(name = "dsaCode")
	private String dsaCodeReference;
	@XmlElement
	private String dsaName;
	@XmlElement
	private String dsaCodeDesc;
	private String lovDescAccountsOfficer;
	private String lovDescSourceCity;
	private String lovDescMobileNumber;
	private String lovDescFinProduct;
	private String lovDescCustCRCPR;
	private String lovDescCustPassportNo;
	private Date lovDescCustDOB;
	private String lovDescRequestStage;
	private String lovDescQueuePriority;
	@XmlElement(name = "tdsApplicable")
	private boolean tDSApplicable;
	@XmlElement
	private String droplineFrq;
	@XmlElement
	private Date firstDroplineDate;
	private boolean pftServicingODLimit;

	// Offer Details Start

	@XmlElement
	private String offerProduct;
	@XmlElement
	private BigDecimal offerAmount = BigDecimal.ZERO;
	@XmlElement
	private String custSegmentation;
	@XmlElement
	private String baseProduct;
	@XmlElement
	private String processType;
	@XmlElement
	private String bureauTimeSeries;
	@XmlElement
	private String campaignName;
	@XmlElement
	private String existingLanRefNo;
	@XmlElement
	private boolean rsa;
	@XmlElement
	private String verification;
	@XmlElement
	private String leadSource;
	@XmlElement
	private String poSource;
	@XmlElement
	private String offerId;

	// Offer Details End

	// Sourcing Details
	@XmlElement
	private String sourcingBranch;
	private String lovDescSourcingBranch;
	@XmlElement
	private String sourChannelCategory;
	@XmlElement(name = "asmName")
	private Long asmName;
	private String lovDescAsmName;

	// Payment type check
	private boolean chequeOrDDAvailable;
	private boolean neftAvailable; // If NEFT/IMPS/RTGS Available

	// Commercial Workflow Purpose
	private String approved;
	private BigDecimal deductFeeDisb = BigDecimal.ZERO;

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
	@XmlElement
	private int noOfSteps = 0;
	private String rejectStatus;
	private String rejectReason;
	private boolean scheduleChange;

	private BigDecimal adjOrgBal = BigDecimal.ZERO;
	private BigDecimal remBalForAdj = BigDecimal.ZERO;
	private boolean devFinCalReq = false;
	private boolean stepRecalOnProrata = false;
	private boolean resetNxtRpyInstReq = false;
	private boolean resetOrgBal = true;
	private String lovDescEntityCode;
	// private String parentRef = "";
	private String parentRef = "";

	// Deviation Process
	private boolean deviationApproval;
	// Finance Pre-approval process
	private String finPreApprovedRef;
	private boolean preApprovalFinance;
	private boolean preApprovalExpired;
	@XmlElement
	private String applicationNo;
	private String swiftBranchCode;

	private boolean allowDrawingPower;
	private boolean allowRevolving;
	private boolean finIsRateRvwAtGrcEnd;
	private boolean rateChange;

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
	@XmlElement
	private BigDecimal grcMaxAmount = BigDecimal.ZERO;
	private boolean grcFrqEditable;

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
	private BigDecimal repayBaseRateVal = BigDecimal.ZERO;
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

	private boolean frqEditable = false;

	// PV: 10MAY17: remove from exlcuded fields
	private String calRoundingMode;
	private int roundingTarget;

	private boolean alwMultiDisb;

	private long postingId = Long.MIN_VALUE;

	// ===========================================
	// =========BPI Details ============
	// ===========================================
	@XmlElement(name = "alwBpiTreatment")
	private boolean alwBPI = false;
	@XmlElement(name = "dftBpiTreatment")
	private String bpiTreatment;
	@XmlElement(name = "bpiPftDaysBasis")
	private String bpiPftDaysBasis;

	// ===========================================
	// =========Planned EMI Holidays & Deferments
	// ===========================================
	@XmlElement
	private boolean planEMIHAlw = false;
	@XmlElement
	private boolean planEMIHAlwInGrace = false;
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
	private int maxUnplannedEmi;
	@XmlElement
	private int maxReAgeHolidays;
	@XmlElement
	private String product;
	@XmlElement
	private BigDecimal reqLoanAmt = BigDecimal.ZERO;
	@XmlElement
	private Integer reqLoanTenor = 0;

	// ===========================================
	// =========Schedule Build Usage ============
	// ===========================================
	private BigDecimal feeChargeAmt = BigDecimal.ZERO;
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
	@XmlElement
	private Date calMaturity;
	private BigDecimal firstRepay = BigDecimal.ZERO;
	private BigDecimal lastRepay = BigDecimal.ZERO;
	private BigDecimal finRepaymentAmount = BigDecimal.ZERO;
	@XmlElement
	private Date eventFromDate;
	private Date eventToDate;
	private Date recalFromDate;
	private Date recalToDate;
	private String recalType;
	private String recalSchdMethod;
	@XmlElement
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
	private BigDecimal pastduePftMargin = BigDecimal.ZERO;
	private BigDecimal pftCpzFromReset = BigDecimal.ZERO;
	private boolean recalSteps = false;

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
	private boolean isAdjustClosingBal = false;
	private BigDecimal osPriBal = BigDecimal.ZERO;

	// ===========================================
	// =========External Usage Details============
	// ===========================================
	@XmlElement
	private BigDecimal finAssetValue = BigDecimal.ZERO;
	@XmlElement
	private BigDecimal finCurrAssetValue = BigDecimal.ZERO;
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
	private String finStatus;
	private String finStsReason;
	private BigDecimal custDSR = BigDecimal.ZERO;
	private BigDecimal curFinAmount = BigDecimal.ZERO;
	private BigDecimal financingAmount = BigDecimal.ZERO;
	private BigDecimal bpiAmount = BigDecimal.ZERO;
	private Date appDate;

	// ManagerCheques
	private BigDecimal lovDescFinancingAmount = BigDecimal.ZERO;

	// ===========================================
	// ===========Validation Details==============
	// ===========================================

	private boolean lovDescIsSchdGenerated = false;
	private String lovDescProductCodeName;
	private long jointCustId;
	private String lovDescJointCustCIF;
	private String lovDescJointCustShrtName;
	private boolean jointAccount;

	// ===========================================
	// =========Enquiry Purpose Details===========
	// ===========================================

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
	private BigDecimal maturity = BigDecimal.ZERO;
	private boolean chequeFound = false;
	private boolean chequeOverride = false;
	private BigDecimal score = BigDecimal.ZERO;
	private boolean smecustomer = false;
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
	private FinanceMain befImage;

	@XmlTransient
	private LoggedInUser userDetails;
	private String nextUserId = null;
	private String nextUsrName;
	private int priority;
	private String lovDescAssignMthd;
	private Map<String, String> lovDescBaseRoleCodeMap = null;
	private String lovDescFirstTaskOwner;

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

	/* Limits */
	@XmlElement
	private String finLimitRef;
	private String lovDescLimitRefName;
	@XmlElement
	private String productCategory;

	/* Mandate */
	@XmlElement
	private Long mandateID;
	private Long securityMandateID;

	private BigDecimal refundAmount = BigDecimal.ZERO;
	private List<ErrorDetail> errorDetails = new ArrayList<>();
	private Map<String, String> lovDescNextUsersRolesMap = null;

	@XmlElement
	private String salesDepartment;
	private String salesDepartmentDesc;
	private String dmaCode;
	@XmlElement(name = "dmaCode")
	private String dmaCodeReference;
	@XmlElement
	private String dmaCodeDesc;
	@XmlElement
	private String dmaName;
	@XmlElement
	private String referralId;
	private String referralIdDesc;
	@XmlElement
	private String employeeName;
	private String employeeNameDesc;
	@XmlElement
	private boolean quickDisb;
	private String wifReference;

	private int availedUnPlanEmi = 0;
	private int availedReAgeH = 0;

	private String workFlowType;
	private String nextRoleCodeDesc;
	private String secUsrFullName = "";

	// API validation purpose only
	private FinanceMain validateMain = this;
	@XmlElement
	private Date firstDisbDate;
	@XmlElement
	private Date lastDisbDate;
	private int dueBucket = 0;
	private int reAgeBucket = 0;
	private String finCategory;
	@XmlElement
	private String stage;
	@XmlElement
	private String status;

	// For Fee Reca Calculation
	private BigDecimal recalFee = BigDecimal.ZERO;
	private int recalTerms = 0;

	// BPI Recalculation setting on Part Payment / Early settlement
	private boolean bpiResetReq = true;
	private boolean modifyBpi = false;

	// Service task specific implemented fields
	// FIXME: DDP: how to pass the below values from extended fields to
	// workflow.
	private boolean rcu;
	private boolean dedupMatch;
	private boolean hunterGo = true;
	private boolean bureau;

	// GST Columns Added
	private BigDecimal recalCGSTFee = BigDecimal.ZERO;
	private BigDecimal recalIGSTFee = BigDecimal.ZERO;
	private BigDecimal recalSGSTFee = BigDecimal.ZERO;
	private BigDecimal recalUGSTFee = BigDecimal.ZERO;
	@XmlElement
	private long eligibilityMethod;
	private String lovEligibilityMethod;
	private String lovDescEligibilityMethod;

	// Exposed For Workflow Rules
	private String collateralType = null;
	private BigDecimal marketValue = BigDecimal.ZERO;
	private BigDecimal guidedValue = BigDecimal.ZERO;
	private BigDecimal totalExposure = BigDecimal.ZERO;
	private boolean samplingRequired;
	@XmlElement
	private boolean legalRequired;

	private boolean depositProcess = false; // added for Cash Management

	private long connector;
	@XmlElement(name = "connector")
	private String connectorReference;
	private String connectorCode;
	private String connectorDesc;

	private boolean vanReq;
	private String vanCode;

	@XmlElement
	private int fixedRateTenor = 0;

	@XmlElement
	private BigDecimal fixedTenorRate = BigDecimal.ZERO;

	private Date fixedTenorEndDate;
	private String processAttributes;
	private String higherDeviationApprover;
	private Map<String, String> attributes = new HashMap<>();
	@XmlElement
	private BigDecimal repayAmount = BigDecimal.ZERO;
	private String entityCode;
	// ### 10-09-2018,Ticket id:124998
	private String entityDesc;

	// tasks #1152 Business Vertical Tagged with Loan
	private Long businessVertical;
	private String businessVerticalCode;
	private String businessVerticalDesc;
	@XmlElement
	private String grcAdvType;
	@XmlElement(name = "grcadvEMITerms")
	private int grcAdvTerms;
	@XmlElement
	private String advType;
	@XmlElement(name = "advEMITerms")
	private int advTerms;
	private String advStage;

	private boolean alwFlexi;
	private BigDecimal flexiAmount = BigDecimal.ZERO;
	private boolean chgDropLineSchd = false;
	private Long assignmentId;
	private long promotionSeqId = 0;
	@XmlElement
	private String loanCategory;
	private boolean allowSubvention;
	private Map<String, Object> glSubHeadCodes = new HashMap<>();
	@XmlElement
	private Date closedDate;
	private int recalIdx = -1;
	private boolean alwStrtPrdHday;
	private int maxStrtPrdHdays;
	private int strtPrdHdays;
	private String strtprdCpzMethod;
	@XmlElement(name = "hostReference")
	private String oldFinReference;
	@XmlElement
	private String coreBankId;
	private BigDecimal tdsPercentage;
	private Date tdsStartDate;
	private Date tdsEndDate;
	private BigDecimal tdsLimitAmt;
	private BigDecimal intTdsAdjusted = BigDecimal.ZERO;
	private String extReference;
	private String serviceName;
	private boolean sanBsdSchdle;
	private boolean applySanctionCheck;
	private BigDecimal svAmount = BigDecimal.ZERO;
	private BigDecimal cbAmount = BigDecimal.ZERO;
	private String bRRpyRvwFrq;
	private BigDecimal totalFinAmount = BigDecimal.ZERO;
	@XmlElement
	private BigDecimal appliedLoanAmt = BigDecimal.ZERO;
	@XmlElement
	private String subVentionFrom;
	@XmlElement
	private Long manufacturerDealerId;
	private String manufacturerDealerName;
	private String manufacturerDealerCode;
	private boolean skipRateReset = false;
	private List<ReasonDetails> detailsList = new ArrayList<ReasonDetails>();
	private String cancelRemarks;
	private String cancelType;
	private Map<String, Object> extendedFields = new HashMap<>();
	private String hunterStatus;
	private int autoRejectionDays;
	private long instructionUID = Long.MIN_VALUE;

	// QDP AutoApprove Changes.
	private boolean autoApprove = false;
	private Date eodValueDate;
	private boolean simulateAccounting;
	private List<ReturnDataSet> returnDataSet;

	private boolean pmay;
	// OCR changes
	@XmlElement
	private boolean finOcrRequired = false;
	// Check the Deviations are Avilable or not in Workflow
	private boolean deviationAvail;
	// Split Loan or Pricing Detail
	private boolean alwLoanSplit;
	private boolean loanSplitted = false;
	// Under Construction Details
	@XmlElement
	private boolean alwGrcAdj;
	@XmlElement
	private boolean endGrcPeriodAftrFullDisb;
	@XmlElement
	private boolean autoIncGrcEndDate;
	private int pendingCovntCount = 0;
	private String custEmpType = "";
	// Disb based schedule
	private boolean instBasedSchd;
	private boolean ocrDeviation = false;
	// Accounting related
	private String partnerBankAcType;
	private String partnerBankAc;
	private boolean writeoffLoan = false;
	// Restructure Loan
	private boolean restructure = false;
	private int schdVersion = 0;

	private EventProperties eventProperties = new EventProperties();
	@XmlElement
	private String tdsType;
	@XmlElement
	private String calcOfSteps;
	@XmlElement
	private String stepsAppliedFor;
	private boolean isRpyStps;
	private boolean isGrcStps;
	@XmlElement
	private int noOfGrcSteps = 0;
	private int noOfPrincipalHdays = 0;
	@XmlElement
	private boolean escrow = false;
	@XmlElement
	private Long custBankId;
	private String custAcctNumber;
	private String custAcctHolderName;
	private String manualSchdType;
	private boolean isra = false;

	@XmlElement(name = "txnChrgReq")
	private boolean overdraftTxnChrgReq;
	@XmlElement(name = "oDCalculatedCharge")
	private String overdraftCalcChrg;
	@XmlElement(name = "oDChargeCalOn")
	private String overdraftChrCalOn;
	@XmlElement(name = "oDChargeAmtOrPerc")
	private BigDecimal overdraftChrgAmtOrPerc = BigDecimal.ZERO;
	@XmlElement(name = "txnChrgCode")
	private long overdraftTxnChrgFeeType = Long.MIN_VALUE;
	private String receiptChannel;
	private List<FinanceScheduleDetail> oldSchedules = new ArrayList<>();
	private Date restructureDate;

	private boolean cpzPosIntact = false;
	private Map<String, BigDecimal> taxPercentages = new HashMap<>();
	private Map<String, Object> gstExecutionMap = new HashMap<>();

	private BigDecimal expectedEndBal = BigDecimal.ZERO;
	private String effSchdMethod = "";
	private BigDecimal sanBasedPft = BigDecimal.ZERO;
	private String moduleDefiner;
	private Date sanctionedDate;
	private List<FinODPenaltyRate> penaltyRates = new ArrayList<>();
	private boolean resetFromLastStep;
	private boolean wifLoan = false;

	private String holdStatus;
	private String reason;
	private boolean underSettlement;
	private boolean isEOD;
	private Date grcStartDate;
	private boolean underNpa;
	private String custCoreBank;
	private String closureType;
	private long createdBy;
	private Timestamp createdOn;
	private Long approvedBy;
	private Timestamp approvedOn;
	private String loanName;
	private Date custDOB;
	private boolean oldActiveState;

	public Set<String> getExcludeFields() {
		Set<String> excludeFields = new HashSet<>();
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
		excludeFields.add("product");
		excludeFields.add("exception");
		excludeFields.add("maturity");
		excludeFields.add("name");
		excludeFields.add("pftIntact");
		excludeFields.add("adjTerms");
		excludeFields.add("blacklistOverride");
		excludeFields.add("score");
		excludeFields.add("chequeFound");
		excludeFields.add("chequeOverride");
		excludeFields.add("curFinAmount");
		excludeFields.add("financingAmount");
		excludeFields.add("smecustomer");
		excludeFields.add("custStsDescription");
		excludeFields.add("rejectStatus");
		excludeFields.add("rejectReason");
		excludeFields.add("feeExists");
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
		excludeFields.add("finIsAlwMD");
		excludeFields.add("dsaCodeDesc");
		excludeFields.add("dsaCodeReference");
		excludeFields.add("dsaName");
		excludeFields.add("numOfMonths");
		excludeFields.add("ifscCode");
		excludeFields.add("refundAmount");
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
		excludeFields.add("dmaCodeReference");
		excludeFields.add("accountsOfficerReference");
		excludeFields.add("connectorReference");
		excludeFields.add("dmaCodeDesc");
		excludeFields.add("dmaName");
		excludeFields.add("referralIdDesc");
		excludeFields.add("calGrcTerms");
		excludeFields.add("calGrcEndDate");
		excludeFields.add("tDSAmount");
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
		excludeFields.add("rcu");
		excludeFields.add("dedupMatch");
		excludeFields.add("hunterGo");
		excludeFields.add("bureau");
		excludeFields.add("adjOrgBal");
		excludeFields.add("remBalForAdj");
		excludeFields.add("devFinCalReq");
		excludeFields.add("stepRecalOnProrata");
		excludeFields.add("resetOrgBal");
		excludeFields.add("resetNxtRpyInstReq");
		excludeFields.add("postingId");
		excludeFields.add("earlyPayAmount");

		// GST
		excludeFields.add("recalCGSTFee");
		excludeFields.add("recalIGSTFee");
		excludeFields.add("recalSGSTFee");
		excludeFields.add("recalUGSTFee");
		excludeFields.add("stage");
		excludeFields.add("status");

		// BPI Reset
		excludeFields.add("bpiResetReq");
		excludeFields.add("collateralType");
		excludeFields.add("marketValue");
		excludeFields.add("guidedValue");
		excludeFields.add("totalExposure");
		excludeFields.add("employeeNameDesc");

		// Repay Base Rate value for Agreements
		excludeFields.add("repayBaseRateVal");

		excludeFields.add("lovEligibilityMethod");
		excludeFields.add("lovDescEligibilityMethod");

		// Cash Management
		excludeFields.add("depositProcess");

		excludeFields.add("connectorCode");
		excludeFields.add("connectorDesc");

		excludeFields.add("isAdjustClosingBal");
		excludeFields.add("modifyBpi");

		excludeFields.add("fixedTenorEndDate");
		excludeFields.add("finBranchProvinceCode");
		excludeFields.add("higherDeviationApprover");
		excludeFields.add("attributes");
		excludeFields.add("repayAmount");
		excludeFields.add("entityDesc");
		excludeFields.add("entityCode");
		excludeFields.add("nextUsrName");

		// Payment type check
		excludeFields.add("chequeOrDDAvailable");
		excludeFields.add("neftAvailable");

		// tasks #1152 Business Vertical Tagged with Loan
		excludeFields.add("businessVerticalCode");
		excludeFields.add("businessVerticalDesc");

		// As part of receipt merging
		excludeFields.add("alwFlexi");
		excludeFields.add("flexiAmount");
		excludeFields.add("chgDropLineSchd");
		excludeFields.add("assignmentId");
		excludeFields.add("loanCategory");
		excludeFields.add("allowSubvention");
		excludeFields.add("glSubHeadCodes");
		excludeFields.add("recalIdx");
		excludeFields.add("alwStrtPrdHday");
		excludeFields.add("maxStrtPrdHdays");
		excludeFields.add("strtPrdHdays");
		excludeFields.add("strtprdCpzMethod");
		excludeFields.add("coreBankId");
		excludeFields.add("intTdsAdjusted");
		excludeFields.add("extReference");
		excludeFields.add("serviceName");

		excludeFields.add("pftCpzFromReset");
		excludeFields.add("applySanctionCheck");
		excludeFields.add("grcFrqEditable");
		excludeFields.add("frqEditable");
		excludeFields.add("bRRpyRvwFrq");
		excludeFields.add("totalFinAmount");
		excludeFields.add("skipRateReset");
		// cancelReason
		excludeFields.add("cancelReason");
		excludeFields.add("cancelRemarks");
		excludeFields.add("cancelType");
		excludeFields.add("extendedFields");
		excludeFields.add("autoApprove");
		// hunterStatus
		excludeFields.add("hunterStatus");
		excludeFields.add("autoRejectionDays");
		excludeFields.add("osPriBal");
		excludeFields.add("rateChange");
		excludeFields.add("appDate");
		excludeFields.add("eodValueDate");
		// Sourcing Details
		excludeFields.add("lovDescSourcingBranch");
		excludeFields.add("lovDescAsmName");
		excludeFields.add("deviationAvail");
		excludeFields.add("pendingCovntCount");
		excludeFields.add("appDate");
		excludeFields.add("parentRef");
		excludeFields.add("recalSteps");

		excludeFields.add("loanSplitted");
		excludeFields.add("custEmpType");
		excludeFields.add("planEMIHAlwInGrace");
		excludeFields.add("simulateAccounting");
		excludeFields.add("returnDataSet");
		excludeFields.add("ocrDeviation");
		excludeFields.add("eventProperties");
		// Accounting related
		excludeFields.add("partnerBankAcType");
		excludeFields.add("partnerBankAc");
		excludeFields.add("tdsType");
		excludeFields.add("isRpyStps");
		excludeFields.add("isGrcStps");
		excludeFields.add("cpzPosIntact");
		excludeFields.add("manufacturerDealerName");
		excludeFields.add("manufacturerDealerCode");
		excludeFields.add("recalSteps");
		excludeFields.add("instructionUID");
		excludeFields.add("custAcctNumber");
		excludeFields.add("custAcctHolderName");
		excludeFields.add("taxPercentages");
		excludeFields.add("gstExecutionMap");
		excludeFields.add("expectedEndBal");
		excludeFields.add("noOfPrincipalHdays");
		excludeFields.add("overdraftTxnChrgFeeType");
		excludeFields.add("receiptChannel");
		excludeFields.add("taxPercentages");
		excludeFields.add("penaltyRates");
		excludeFields.add("oldSchedules");
		excludeFields.add("restructureDate");
		excludeFields.add("effSchdMethod");
		excludeFields.add("sanBasedPft");
		excludeFields.add("cpzPosIntact");
		excludeFields.add("moduleDefiner");
		excludeFields.add("resetFromLastStep");
		excludeFields.add("wifLoan");
		excludeFields.add("holdStatus");
		excludeFields.add("reason");
		excludeFields.add("isEOD");
		excludeFields.add("grcStartDate");
		excludeFields.add("custCoreBank");
		excludeFields.add("closureType");
		excludeFields.add("loanName");
		excludeFields.add("custDOB");
		excludeFields.add("oldActiveState");

		return excludeFields;
	}

	public FinanceMain copyEntity() {
		FinanceMain entity = new FinanceMain();
		entity.setFinID(this.finID);
		entity.setFinReference(this.finReference);
		entity.setLinkedFinRef(this.linkedFinRef);
		entity.setInvestmentRef(this.investmentRef);
		entity.setFinType(this.finType);
		entity.setLovDescFinTypeName(this.lovDescFinTypeName);
		entity.setPromotionCode(this.promotionCode);
		entity.setFinCcy(this.finCcy);
		entity.setProfitDaysBasis(this.profitDaysBasis);
		entity.setCustID(this.custID);
		entity.setLovDescCustCIF(this.lovDescCustCIF);
		entity.setLovDescCustShrtName(this.lovDescCustShrtName);
		entity.setFinBranch(this.finBranch);
		entity.setLovDescFinBranchName(this.lovDescFinBranchName);
		entity.setFinBranchProvinceCode(this.finBranchProvinceCode);
		entity.setFinStartDate(this.finStartDate);
		entity.setFinContractDate(this.finContractDate);
		entity.setFinAmount(this.finAmount);
		entity.setMinDownPayPerc(this.minDownPayPerc);
		entity.setDownPayment(this.downPayment);
		entity.setAdvanceEMI(this.advanceEMI);
		entity.setDownPayBank(this.downPayBank);
		entity.setDownPaySupl(this.downPaySupl);
		entity.setDefferments(this.defferments);
		entity.setPlanDeferCount(this.planDeferCount);
		entity.setFinPurpose(this.finPurpose);
		entity.setLovDescFinPurposeName(this.lovDescFinPurposeName);
		entity.setAllowGrcPeriod(this.allowGrcPeriod);
		entity.setFinRepayMethod(this.finRepayMethod);
		entity.setFinCommitmentRef(this.finCommitmentRef);
		entity.setLovDescCommitmentRefName(this.lovDescCommitmentRefName);
		entity.setFinIsActive(this.finIsActive);
		entity.setFinRemarks(this.finRemarks);
		entity.setInitiateUser(this.initiateUser);
		entity.setInitiateDate(this.initiateDate);
		entity.setFinIsAlwMD(this.finIsAlwMD);
		entity.setAccountsOfficer(this.accountsOfficer);
		entity.setAccountsOfficerReference(this.accountsOfficerReference);
		entity.setDsaCode(this.dsaCode);
		entity.setDsaCodeReference(this.dsaCodeReference);
		entity.setDsaName(this.dsaName);
		entity.setDsaCodeDesc(this.dsaCodeDesc);
		entity.setLovDescAccountsOfficer(this.lovDescAccountsOfficer);
		entity.setLovDescSourceCity(this.lovDescSourceCity);
		entity.setLovDescMobileNumber(this.lovDescMobileNumber);
		entity.setLovDescFinProduct(this.lovDescFinProduct);
		entity.setLovDescCustCRCPR(this.lovDescCustCRCPR);
		entity.setLovDescCustPassportNo(this.lovDescCustPassportNo);
		entity.setLovDescCustDOB(this.lovDescCustDOB);
		entity.setLovDescRequestStage(this.lovDescRequestStage);
		entity.setLovDescQueuePriority(this.lovDescQueuePriority);
		entity.setTDSApplicable(this.tDSApplicable);
		entity.setDroplineFrq(this.droplineFrq);
		entity.setFirstDroplineDate(this.firstDroplineDate);
		entity.setPftServicingODLimit(this.pftServicingODLimit);
		entity.setOfferProduct(this.offerProduct);
		entity.setOfferAmount(this.offerAmount);
		entity.setCustSegmentation(this.custSegmentation);
		entity.setBaseProduct(this.baseProduct);
		entity.setProcessType(this.processType);
		entity.setBureauTimeSeries(this.bureauTimeSeries);
		entity.setCampaignName(this.campaignName);
		entity.setExistingLanRefNo(this.existingLanRefNo);
		entity.setRsa(this.rsa);
		entity.setVerification(this.verification);
		entity.setLeadSource(this.leadSource);
		entity.setPoSource(this.poSource);
		entity.setOfferId(this.offerId);
		entity.setSourcingBranch(this.sourcingBranch);
		entity.setLovDescSourcingBranch(this.lovDescSourcingBranch);
		entity.setSourChannelCategory(this.sourChannelCategory);
		entity.setAsmName(this.asmName);
		entity.setLovDescAsmName(this.lovDescAsmName);
		entity.setChequeOrDDAvailable(this.chequeOrDDAvailable);
		entity.setNeftAvailable(this.neftAvailable);
		entity.setApproved(this.approved);
		entity.setDeductFeeDisb(this.deductFeeDisb);
		entity.setStepFinance(this.stepFinance);
		entity.setStepType(this.stepType);
		entity.setStepPolicy(this.stepPolicy);
		entity.setLovDescStepPolicyName(this.lovDescStepPolicyName);
		entity.setAlwManualSteps(this.alwManualSteps);
		entity.setNoOfSteps(this.noOfSteps);
		entity.setRejectStatus(this.rejectStatus);
		entity.setRejectReason(this.rejectReason);
		entity.setScheduleChange(this.scheduleChange);
		entity.setAdjOrgBal(this.adjOrgBal);
		entity.setRemBalForAdj(this.remBalForAdj);
		entity.setDevFinCalReq(this.devFinCalReq);
		entity.setStepRecalOnProrata(this.stepRecalOnProrata);
		entity.setResetNxtRpyInstReq(this.resetNxtRpyInstReq);
		entity.setResetOrgBal(this.resetOrgBal);
		entity.setLovDescEntityCode(this.lovDescEntityCode);
		entity.setParentRef(this.parentRef);
		entity.setDeviationApproval(this.deviationApproval);
		entity.setFinPreApprovedRef(this.finPreApprovedRef);
		entity.setPreApprovalFinance(this.preApprovalFinance);
		entity.setPreApprovalExpired(this.preApprovalExpired);
		entity.setApplicationNo(this.applicationNo);
		entity.setSwiftBranchCode(this.swiftBranchCode);
		entity.setAllowDrawingPower(this.allowDrawingPower);
		entity.setAllowRevolving(this.allowRevolving);
		entity.setFinIsRateRvwAtGrcEnd(this.finIsRateRvwAtGrcEnd);
		entity.setRateChange(this.rateChange);
		entity.setGraceTerms(this.graceTerms);
		entity.setGrcPeriodEndDate(this.grcPeriodEndDate);
		entity.setGrcRateBasis(this.grcRateBasis);
		entity.setGrcPftRate(this.grcPftRate);
		entity.setGraceBaseRate(this.graceBaseRate);
		entity.setGraceSpecialRate(this.graceSpecialRate);
		entity.setGrcMargin(this.grcMargin);
		entity.setGrcProfitDaysBasis(this.grcProfitDaysBasis);
		entity.setGrcPftFrq(this.grcPftFrq);
		entity.setNextGrcPftDate(this.nextGrcPftDate);
		entity.setAllowGrcPftRvw(this.allowGrcPftRvw);
		entity.setGrcPftRvwFrq(this.grcPftRvwFrq);
		entity.setNextGrcPftRvwDate(this.nextGrcPftRvwDate);
		entity.setAllowGrcCpz(this.allowGrcCpz);
		entity.setGrcCpzFrq(this.grcCpzFrq);
		entity.setNextGrcCpzDate(this.nextGrcCpzDate);
		entity.setAllowGrcRepay(this.allowGrcRepay);
		entity.setGrcSchdMthd(this.grcSchdMthd);
		entity.setGrcMinRate(this.grcMinRate);
		entity.setGrcMaxRate(this.grcMaxRate);
		entity.setGrcMaxAmount(this.grcMaxAmount);
		entity.setGrcFrqEditable(this.grcFrqEditable);
		entity.setNumberOfTerms(this.numberOfTerms);
		entity.setNOInst(this.NOInst);
		entity.setReqRepayAmount(this.reqRepayAmount);
		entity.setRepayRateBasis(this.repayRateBasis);
		entity.setRepayProfitRate(this.repayProfitRate);
		entity.setRepayBaseRate(this.repayBaseRate);
		entity.setRepayBaseRateVal(this.repayBaseRateVal);
		entity.setRepaySpecialRate(this.repaySpecialRate);
		entity.setRepayMargin(this.repayMargin);
		entity.setScheduleMethod(this.scheduleMethod);
		entity.setRepayPftFrq(this.repayPftFrq);
		entity.setNextRepayPftDate(this.nextRepayPftDate);
		entity.setAllowRepayRvw(this.allowRepayRvw);
		entity.setRepayRvwFrq(this.repayRvwFrq);
		entity.setNextRepayRvwDate(this.nextRepayRvwDate);
		entity.setAllowRepayCpz(this.allowRepayCpz);
		entity.setRepayCpzFrq(this.repayCpzFrq);
		entity.setNextRepayCpzDate(this.nextRepayCpzDate);
		entity.setRepayFrq(this.repayFrq);
		entity.setNextRepayDate(this.nextRepayDate);
		entity.setMaturityDate(this.maturityDate);
		entity.setFinRepayPftOnFrq(this.finRepayPftOnFrq);
		entity.setRpyMinRate(this.rpyMinRate);
		entity.setRpyMaxRate(this.rpyMaxRate);
		entity.setFrqEditable(this.frqEditable);
		entity.setCalRoundingMode(this.calRoundingMode);
		entity.setRoundingTarget(this.roundingTarget);
		entity.setAlwMultiDisb(this.alwMultiDisb);
		entity.setPostingId(this.postingId);
		entity.setAlwBPI(this.alwBPI);
		entity.setBpiTreatment(this.bpiTreatment);
		entity.setBpiPftDaysBasis(this.bpiPftDaysBasis);
		entity.setPlanEMIHAlw(this.planEMIHAlw);
		entity.setPlanEMIHAlwInGrace(this.planEMIHAlwInGrace);
		entity.setPlanEMIHMethod(this.planEMIHMethod);
		entity.setPlanEMIHMaxPerYear(this.planEMIHMaxPerYear);
		entity.setPlanEMIHMax(this.planEMIHMax);
		entity.setPlanEMIHLockPeriod(this.planEMIHLockPeriod);
		entity.setPlanEMICpz(this.planEMICpz);
		entity.setUnPlanEMIHLockPeriod(this.unPlanEMIHLockPeriod);
		entity.setUnPlanEMICpz(this.unPlanEMICpz);
		entity.setReAgeCpz(this.reAgeCpz);
		entity.setMaxUnplannedEmi(this.maxUnplannedEmi);
		entity.setMaxReAgeHolidays(this.maxReAgeHolidays);
		entity.setProduct(this.product);
		entity.setReqLoanAmt(this.reqLoanAmt);
		entity.setReqLoanTenor(this.reqLoanTenor);
		entity.setFeeChargeAmt(this.feeChargeAmt);
		entity.setCurDisbursementAmt(this.curDisbursementAmt);
		entity.setAnualizedPercRate(this.anualizedPercRate);
		entity.setEffectiveRateOfReturn(this.effectiveRateOfReturn);
		entity.setCpzAtGraceEnd(this.cpzAtGraceEnd);
		entity.setTotalGracePft(this.totalGracePft);
		entity.setTotalGraceCpz(this.totalGraceCpz);
		entity.setTotalGrossGrcPft(this.totalGrossGrcPft);
		entity.setTotalProfit(this.totalProfit);
		entity.setTotalCpz(this.totalCpz);
		entity.setTotalGrossPft(this.totalGrossPft);
		entity.setTotalRepayAmt(this.totalRepayAmt);
		entity.setCalculateRepay(this.calculateRepay);
		entity.setEqualRepay(this.equalRepay);
		entity.setCalGrcTerms(this.calGrcTerms);
		entity.setCalGrcEndDate(this.calGrcEndDate);
		entity.setCalTerms(this.calTerms);
		entity.setCalMaturity(this.calMaturity);
		entity.setFirstRepay(this.firstRepay);
		entity.setLastRepay(this.lastRepay);
		entity.setFinRepaymentAmount(this.finRepaymentAmount);
		entity.setEventFromDate(this.eventFromDate);
		entity.setEventToDate(this.eventToDate);
		entity.setRecalFromDate(this.recalFromDate);
		entity.setRecalToDate(this.recalToDate);
		entity.setRecalType(this.recalType);
		entity.setRecalSchdMethod(this.recalSchdMethod);
		entity.setDesiredProfit(this.desiredProfit);
		entity.setRvwRateApplFor(this.rvwRateApplFor);
		entity.setLovDescAdjClosingBal(this.lovDescAdjClosingBal);
		entity.setTotalPriAmt(this.totalPriAmt);
		entity.setSchPriDue(this.schPriDue);
		entity.setSchPftDue(this.schPftDue);
		entity.setSchCalOnRvw(this.schCalOnRvw);
		entity.setPastduePftCalMthd(this.pastduePftCalMthd);
		entity.setDroppingMethod(this.droppingMethod);
		entity.setRateChgAnyDay(this.rateChgAnyDay);
		entity.setPastduePftMargin(this.pastduePftMargin);
		entity.setPftCpzFromReset(this.pftCpzFromReset);
		entity.setRecalSteps(this.recalSteps);
		entity.setAllowedDefRpyChange(this.allowedDefRpyChange);
		entity.setAvailedDefRpyChange(this.availedDefRpyChange);
		entity.setAllowedDefFrqChange(this.allowedDefFrqChange);
		entity.setAvailedDefFrqChange(this.availedDefFrqChange);
		entity.setLastRepayDate(this.lastRepayDate);
		entity.setLastRepayPftDate(this.lastRepayPftDate);
		entity.setLastRepayRvwDate(this.lastRepayRvwDate);
		entity.setLastRepayCpzDate(this.lastRepayCpzDate);
		entity.setPftIntact(this.pftIntact);
		entity.setAdjTerms(this.adjTerms);
		entity.setAdjustClosingBal(this.isAdjustClosingBal);
		entity.setOsPriBal(this.osPriBal);
		entity.setFinAssetValue(this.finAssetValue);
		entity.setFinCurrAssetValue(this.finCurrAssetValue);
		entity.setFinSourceID(this.finSourceID);
		entity.setRcdMaintainSts(this.rcdMaintainSts);
		entity.setLovDescFinScheduleOn(this.lovDescFinScheduleOn);
		entity.setClosingStatus(this.closingStatus);
		entity.setFinApprovedDate(this.finApprovedDate);
		entity.setLovDescTenorName(this.lovDescTenorName);
		entity.setMigratedFinance(this.migratedFinance);
		entity.setScheduleMaintained(this.scheduleMaintained);
		entity.setScheduleRegenerated(this.scheduleRegenerated);
		entity.setManualSchedule(this.manualSchedule);
		entity.setFinStatus(this.finStatus);
		entity.setFinStsReason(this.finStsReason);
		entity.setCustDSR(this.custDSR);
		entity.setCurFinAmount(this.curFinAmount);
		entity.setFinancingAmount(this.financingAmount);
		entity.setBpiAmount(this.bpiAmount);
		entity.setAppDate(this.appDate);
		entity.setLovDescFinancingAmount(this.lovDescFinancingAmount);
		entity.setLovDescIsSchdGenerated(this.lovDescIsSchdGenerated);
		entity.setLovDescProductCodeName(this.lovDescProductCodeName);
		entity.setJointCustId(this.jointCustId);
		entity.setLovDescJointCustCIF(this.lovDescJointCustCIF);
		entity.setLovDescJointCustShrtName(this.lovDescJointCustShrtName);
		entity.setJointAccount(this.jointAccount);
		entity.setLovDescAccruedTillLBD(this.lovDescAccruedTillLBD);
		entity.setCustStsDescription(this.custStsDescription);
		entity.setDedupFound(this.dedupFound);
		entity.setSkipDedup(this.skipDedup);
		entity.setProceedDedup(this.proceedDedup);
		entity.setBlacklisted(this.blacklisted);
		entity.setBlacklistOverride(this.blacklistOverride);
		entity.setLimitValid(this.limitValid);
		entity.setOverrideLimit(this.overrideLimit);
		entity.setAmount(this.amount);
		entity.setException(this.exception);
		entity.setMaturity(this.maturity);
		entity.setChequeFound(this.chequeFound);
		entity.setChequeOverride(this.chequeOverride);
		entity.setScore(this.score);
		entity.setSmecustomer(this.smecustomer);
		entity.setFeeExists(this.feeExists);
		entity.setReceiptMode(this.receiptMode);
		entity.setReceiptModeStatus(this.receiptModeStatus);
		entity.setReceiptPurpose(this.receiptPurpose);
		entity.setWaivedAmt(this.waivedAmt);
		entity.setLovDescCustCoreBank(this.lovDescCustCoreBank);
		entity.setLovDescCustFName(this.lovDescCustFName);
		entity.setLovDescCustLName(this.lovDescCustLName);
		entity.setLovDescSalutationName(this.lovDescSalutationName);
		entity.setLovDescCustCtgCode(this.lovDescCustCtgCode);
		entity.setNewRecord(super.isNewRecord());
		entity.setBefImage(this.befImage == null ? null : this.befImage.copyEntity());
		entity.setUserDetails(this.userDetails);
		entity.setNextUserId(this.nextUserId);
		entity.setNextUsrName(this.nextUsrName);
		entity.setPriority(this.priority);
		entity.setLovDescAssignMthd(this.lovDescAssignMthd);
		if (lovDescBaseRoleCodeMap != null) {
			entity.setLovDescBaseRoleCodeMap(new HashMap<String, String>());
			this.lovDescBaseRoleCodeMap.entrySet().stream()
					.forEach(e -> entity.getLovDescBaseRoleCodeMap().put(e.getKey(), e.getValue()));
		}
		entity.setLovDescFirstTaskOwner(this.lovDescFirstTaskOwner);
		entity.setPftForSelectedPeriod(this.pftForSelectedPeriod);
		entity.setMiscAmount(this.miscAmount);
		entity.setIndexMisc(this.indexMisc);
		entity.setCompareToExpected(this.compareToExpected);
		entity.setCompareExpectedResult(this.compareExpectedResult);
		entity.setCompareExpectIndex(this.compareExpectIndex);
		entity.setCompareWith(this.compareWith);
		entity.setProtectSchdPft(this.protectSchdPft);
		entity.setSchdIndex(this.schdIndex);
		entity.setIndexStart(this.indexStart);
		entity.setIndexEnd(this.indexEnd);
		entity.setNewMaturityIndex(this.newMaturityIndex);
		entity.setReqMaturity(this.reqMaturity);
		entity.setProcMethod(this.procMethod);
		entity.setIfscCode(this.ifscCode);
		entity.setReqTerms(this.reqTerms);
		entity.setIncreaseTerms(this.increaseTerms);
		entity.setLovDescFinDivision(this.lovDescFinDivision);
		entity.setLovValue(this.lovValue);
		entity.setFinLimitRef(this.finLimitRef);
		entity.setLovDescLimitRefName(this.lovDescLimitRefName);
		entity.setProductCategory(this.productCategory);
		entity.setMandateID(this.mandateID);
		entity.setSecurityMandateID(this.securityMandateID);
		entity.setWifLoan(this.wifLoan);
		entity.setRefundAmount(this.refundAmount);
		this.errorDetails.stream().forEach(e -> entity.getErrorDetails().add(e));
		if (lovDescNextUsersRolesMap != null) {
			entity.setLovDescNextUsersRolesMap(new HashMap<>());
			this.lovDescNextUsersRolesMap.entrySet().stream()
					.forEach(e -> entity.getLovDescNextUsersRolesMap().put(e.getKey(), e.getValue()));
		}

		entity.setSalesDepartment(this.salesDepartment);
		entity.setSalesDepartmentDesc(this.salesDepartmentDesc);
		entity.setDmaCode(this.dmaCode);
		entity.setDmaCodeReference(this.dmaCodeReference);
		entity.setDmaCodeDesc(this.dmaCodeDesc);
		entity.setDmaName(this.dmaName);
		entity.setReferralId(this.referralId);
		entity.setReferralIdDesc(this.referralIdDesc);
		entity.setEmployeeName(this.employeeName);
		entity.setEmployeeNameDesc(this.employeeNameDesc);
		entity.setQuickDisb(this.quickDisb);
		entity.setWifReference(this.wifReference);
		entity.setAvailedUnPlanEmi(this.availedUnPlanEmi);
		entity.setAvailedReAgeH(this.availedReAgeH);
		entity.setWorkFlowType(this.workFlowType);
		entity.setNextRoleCodeDesc(this.nextRoleCodeDesc);
		entity.setSecUsrFullName(this.secUsrFullName);
		entity.setValidateMain(this.validateMain);
		entity.setFirstDisbDate(this.firstDisbDate);
		entity.setLastDisbDate(this.lastDisbDate);
		entity.setDueBucket(this.dueBucket);
		entity.setReAgeBucket(this.reAgeBucket);
		entity.setFinCategory(this.finCategory);
		entity.setStage(this.stage);
		entity.setStatus(this.status);
		entity.setRecalFee(this.recalFee);
		entity.setRecalTerms(this.recalTerms);
		entity.setBpiResetReq(this.bpiResetReq);
		entity.setModifyBpi(this.modifyBpi);
		entity.setRcu(this.rcu);
		entity.setDedupMatch(this.dedupMatch);
		entity.setHunterGo(this.hunterGo);
		entity.setBureau(this.bureau);
		entity.setRecalCGSTFee(this.recalCGSTFee);
		entity.setRecalIGSTFee(this.recalIGSTFee);
		entity.setRecalSGSTFee(this.recalSGSTFee);
		entity.setRecalUGSTFee(this.recalUGSTFee);
		entity.setEligibilityMethod(this.eligibilityMethod);
		entity.setLovEligibilityMethod(this.lovEligibilityMethod);
		entity.setLovDescEligibilityMethod(this.lovDescEligibilityMethod);
		entity.setCollateralType(this.collateralType);
		entity.setMarketValue(this.marketValue);
		entity.setGuidedValue(this.guidedValue);
		entity.setTotalExposure(this.totalExposure);
		entity.setSamplingRequired(this.samplingRequired);
		entity.setLegalRequired(this.legalRequired);
		entity.setDepositProcess(this.depositProcess);
		entity.setConnector(this.connector);
		entity.setConnectorReference(this.connectorReference);
		entity.setConnectorCode(this.connectorCode);
		entity.setConnectorDesc(this.connectorDesc);
		entity.setVanReq(this.vanReq);
		entity.setVanCode(this.vanCode);
		entity.setFixedRateTenor(this.fixedRateTenor);
		entity.setFixedTenorRate(this.fixedTenorRate);
		entity.setFixedTenorEndDate(this.fixedTenorEndDate);
		entity.setProcessAttributes(this.processAttributes);
		entity.setHigherDeviationApprover(this.higherDeviationApprover);
		this.attributes.entrySet().stream().forEach(e -> entity.getAttributes().put(e.getKey(), e.getValue()));
		entity.setRepayAmount(this.repayAmount);
		entity.setEntityCode(this.entityCode);
		entity.setEntityDesc(this.entityDesc);
		entity.setBusinessVertical(this.businessVertical);
		entity.setBusinessVerticalCode(this.businessVerticalCode);
		entity.setBusinessVerticalDesc(this.businessVerticalDesc);
		entity.setGrcAdvType(this.grcAdvType);
		entity.setGrcAdvTerms(this.grcAdvTerms);
		entity.setAdvType(this.advType);
		entity.setAdvTerms(this.advTerms);
		entity.setAdvStage(this.advStage);
		entity.setAlwFlexi(this.alwFlexi);
		entity.setFlexiAmount(this.flexiAmount);
		entity.setChgDropLineSchd(this.chgDropLineSchd);
		entity.setAssignmentId(this.assignmentId);
		entity.setPromotionSeqId(this.promotionSeqId);
		entity.setLoanCategory(this.loanCategory);
		entity.setAllowSubvention(this.allowSubvention);
		this.glSubHeadCodes.entrySet().stream().forEach(e -> entity.getGlSubHeadCodes().put(e.getKey(), e.getValue()));
		entity.setClosedDate(this.closedDate);
		entity.setRecalIdx(this.recalIdx);
		entity.setAlwStrtPrdHday(this.alwStrtPrdHday);
		entity.setMaxStrtPrdHdays(this.maxStrtPrdHdays);
		entity.setStrtPrdHdays(this.strtPrdHdays);
		entity.setStrtprdCpzMethod(this.strtprdCpzMethod);
		entity.setOldFinReference(this.oldFinReference);
		entity.setCoreBankId(this.coreBankId);
		entity.setTdsPercentage(this.tdsPercentage);
		entity.setTdsStartDate(this.tdsStartDate);
		entity.setTdsEndDate(this.tdsEndDate);
		entity.setTdsLimitAmt(this.tdsLimitAmt);
		entity.setIntTdsAdjusted(this.intTdsAdjusted);
		entity.setExtReference(this.extReference);
		entity.setServiceName(this.serviceName);
		entity.setSanBsdSchdle(this.sanBsdSchdle);
		entity.setApplySanctionCheck(this.applySanctionCheck);
		entity.setSvAmount(this.svAmount);
		entity.setCbAmount(this.cbAmount);
		entity.setbRRpyRvwFrq(this.bRRpyRvwFrq);
		entity.setTotalFinAmount(this.totalFinAmount);
		entity.setAppliedLoanAmt(this.appliedLoanAmt);
		entity.setSkipRateReset(this.skipRateReset);
		this.detailsList.stream().forEach(e -> entity.getDetailsList().add(e == null ? null : e.copyEntity()));
		entity.setCancelRemarks(this.cancelRemarks);
		entity.setCancelType(this.cancelType);
		entity.setExtendedFields(this.extendedFields);
		entity.setHunterStatus(this.hunterStatus);
		entity.setAutoRejectionDays(this.autoRejectionDays);
		entity.setAutoApprove(this.autoApprove);
		entity.setEodValueDate(this.eodValueDate);
		entity.setSimulateAccounting(this.simulateAccounting);
		if (returnDataSet != null) {
			entity.setReturnDataSet(new ArrayList<ReturnDataSet>());
			this.returnDataSet.stream().forEach(e -> entity.getReturnDataSet().add(e == null ? null : e.copyEntity()));
		}
		entity.setPmay(this.pmay);
		entity.setFinOcrRequired(this.finOcrRequired);
		entity.setDeviationAvail(this.deviationAvail);
		entity.setAlwLoanSplit(this.alwLoanSplit);
		entity.setLoanSplitted(this.loanSplitted);
		entity.setAlwGrcAdj(this.alwGrcAdj);
		entity.setEndGrcPeriodAftrFullDisb(this.endGrcPeriodAftrFullDisb);
		entity.setAutoIncGrcEndDate(this.autoIncGrcEndDate);
		entity.setPendingCovntCount(this.pendingCovntCount);
		entity.setCustEmpType(this.custEmpType);
		entity.setInstBasedSchd(this.instBasedSchd);
		entity.setOcrDeviation(this.ocrDeviation);
		entity.setPartnerBankAcType(this.partnerBankAcType);
		entity.setPartnerBankAc(this.partnerBankAc);
		entity.setWriteoffLoan(this.writeoffLoan);
		entity.setRestructure(this.restructure);
		entity.setTdsType(this.tdsType);
		entity.setCalcOfSteps(this.calcOfSteps);
		entity.setStepsAppliedFor(this.stepsAppliedFor);
		entity.setRpyStps(this.isRpyStps);
		entity.setGrcStps(this.isGrcStps);
		entity.setNoOfGrcSteps(this.noOfGrcSteps);
		entity.setCpzPosIntact(this.cpzPosIntact);
		entity.setWriteoffLoan(this.writeoffLoan);
		entity.setRestructure(this.restructure);
		entity.setSchdVersion(this.schdVersion);
		entity.setSubVentionFrom(this.subVentionFrom);
		entity.setManufacturerDealerId(this.manufacturerDealerId);
		entity.setManufacturerDealerName(this.manufacturerDealerName);
		entity.setManufacturerDealerCode(this.manufacturerDealerCode);
		entity.setEscrow(this.escrow);
		entity.setCustBankId(this.custBankId);
		entity.setCustAcctNumber(this.custAcctNumber);
		entity.setCustAcctHolderName(this.custAcctHolderName);
		entity.setTaxPercentages(this.taxPercentages);
		entity.setGstExecutionMap(this.gstExecutionMap);
		entity.setExpectedEndBal(this.expectedEndBal);
		entity.setNoOfPrincipalHdays(this.noOfPrincipalHdays);
		entity.setManualSchdType(this.manualSchdType);
		entity.setEarlyPayAmount(this.earlyPayAmount);
		entity.setIsra(this.isra);
		entity.setOverdraftTxnChrgReq(this.overdraftTxnChrgReq);
		entity.setOverdraftCalcChrg(this.overdraftCalcChrg);
		entity.setOverdraftChrgAmtOrPerc(this.overdraftChrgAmtOrPerc);
		entity.setOverdraftChrCalOn(this.overdraftChrCalOn);
		entity.setOverdraftTxnChrgFeeType(this.overdraftTxnChrgFeeType);
		entity.setReceiptChannel(this.receiptChannel);
		entity.setSanctionedDate(this.sanctionedDate);
		entity.setTaxPercentages(this.taxPercentages);
		this.penaltyRates.stream().forEach(e -> entity.getPenaltyRates().add(e));
		entity.setOldSchedules(this.oldSchedules);
		entity.setRestructureDate(this.restructureDate);
		entity.setEffSchdMethod(this.effSchdMethod);
		entity.setSanBasedPft(this.sanBasedPft);
		entity.setCpzPosIntact(this.cpzPosIntact);
		entity.setModuleDefiner(this.moduleDefiner);
		entity.setUnderNpa(this.underNpa);
		entity.setUnderSettlement(this.underSettlement);
		entity.setCreatedBy(this.createdBy);
		entity.setCreatedOn(this.createdOn);
		entity.setApprovedBy(this.approvedBy);
		entity.setApprovedOn(this.approvedOn);
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

	public FinanceMain() {
		super();
	}

	public long getFinID() {
		return finID;
	}

	public void setFinID(long finID) {
		this.finID = finID;
	}

	public String getLovDescAsmName() {
		return lovDescAsmName;
	}

	public void setLovDescAsmName(String lovDescAsmName) {
		this.lovDescAsmName = lovDescAsmName;
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

	public String getApproved() {
		return approved;
	}

	public void setApproved(String approved) {
		this.approved = approved;
	}

	public BigDecimal getDeductFeeDisb() {
		return deductFeeDisb;
	}

	public void setDeductFeeDisb(BigDecimal deductFeeDisb) {
		this.deductFeeDisb = deductFeeDisb;
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

	@XmlTransient
	public Map<String, Object> getDeclaredFieldValues() {
		Map<String, Object> fieldsAndValuesMap = new HashMap<String, Object>();

		getDeclaredFieldValues(fieldsAndValuesMap);

		return fieldsAndValuesMap;
	}

	public void getDeclaredFieldValues(Map<String, Object> fieldsAndValuesMap) {
		for (int i = 0; i < this.getClass().getDeclaredFields().length; i++) {
			try {
				// "fm_" Should be in small case only, if we want to change the
				// case we need to update the configuration fields as well.
				fieldsAndValuesMap.put("fm_" + this.getClass().getDeclaredFields()[i].getName(),
						this.getClass().getDeclaredFields()[i].get(this));
				if ("extendedFields".equals(this.getClass().getDeclaredFields()[i].getName())) {
					fieldsAndValuesMap.putAll(extendedFields);
				}
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
	public List<ErrorDetail> getErrorDetails() {
		return errorDetails;
	}

	public void setErrorDetail(ErrorDetail errorDetail) {

		if (errorDetails == null) {
			errorDetails = new ArrayList<ErrorDetail>();
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

	public long getInitiateUser() {
		return initiateUser;
	}

	public void setInitiateUser(long initiateUser) {
		this.initiateUser = initiateUser;
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

	public long getAccountsOfficer() {
		return accountsOfficer;
	}

	public void setAccountsOfficer(long accountsOfficer) {
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

	public String getLovDescAccountsOfficer() {
		return lovDescAccountsOfficer;
	}

	public void setLovDescAccountsOfficer(String lovDescAccountsOfficer) {
		this.lovDescAccountsOfficer = lovDescAccountsOfficer;
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

	public Long getMandateID() {
		return mandateID;
	}

	public void setMandateID(Long mandateID) {
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

	public boolean isChequeOrDDAvailable() {
		return chequeOrDDAvailable;
	}

	public void setChequeOrDDAvailable(boolean chequeOrDDAvailable) {
		this.chequeOrDDAvailable = chequeOrDDAvailable;
	}

	public boolean isNeftAvailable() {
		return neftAvailable;
	}

	public void setNeftAvailable(boolean neftAvailable) {
		this.neftAvailable = neftAvailable;
	}

	public Date getFirstDroplineDate() {
		return firstDroplineDate;
	}

	public void setFirstDroplineDate(Date firstDroplineDate) {
		this.firstDroplineDate = firstDroplineDate;
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

	public boolean isPlanEMIHAlwInGrace() {
		return planEMIHAlwInGrace;
	}

	public void setPlanEMIHAlwInGrace(boolean planEMIHAlwInGrace) {
		this.planEMIHAlwInGrace = planEMIHAlwInGrace;
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

	public boolean isFinIsAlwMD() {
		return finIsAlwMD;
	}

	public void setFinIsAlwMD(boolean finIsAlwMD) {
		this.finIsAlwMD = finIsAlwMD;
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

	public void setErrorDetails(List<ErrorDetail> errorDetails) {
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

	public void setNextUsrName(String nextUsrName) {
		this.nextUsrName = nextUsrName;
	}

	public String getNextUsrName() {
		return nextUsrName;
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
		this.resetNxtRpyInstReq = false;
		this.procMethod = null;
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

	public boolean isDedupMatch() {
		return dedupMatch;
	}

	public void setDedupMatch(boolean dedupMatch) {
		this.dedupMatch = dedupMatch;
	}

	public boolean isHunterGo() {
		return hunterGo;
	}

	public void setHunterGo(boolean hunterGo) {
		this.hunterGo = hunterGo;
	}

	public boolean isBureau() {
		return bureau;
	}

	public void setBureau(boolean bureau) {
		this.bureau = bureau;
	}

	public String getLovDescSourceCity() {
		return lovDescSourceCity;
	}

	public void setLovDescSourceCity(String lovDescSourceCity) {
		this.lovDescSourceCity = lovDescSourceCity;
	}

	// GST

	public BigDecimal getRecalIGSTFee() {
		return recalIGSTFee;
	}

	public void setRecalIGSTFee(BigDecimal recalIGSTFee) {
		this.recalIGSTFee = recalIGSTFee;
	}

	public BigDecimal getRecalCGSTFee() {
		return recalCGSTFee;
	}

	public void setRecalCGSTFee(BigDecimal recalCGSTFee) {
		this.recalCGSTFee = recalCGSTFee;
	}

	public BigDecimal getRecalSGSTFee() {
		return recalSGSTFee;
	}

	public void setRecalSGSTFee(BigDecimal recalSGSTFee) {
		this.recalSGSTFee = recalSGSTFee;
	}

	public BigDecimal getRecalUGSTFee() {
		return recalUGSTFee;
	}

	public void setRecalUGSTFee(BigDecimal recalUGSTFee) {
		this.recalUGSTFee = recalUGSTFee;
	}

	public boolean isResetOrgBal() {
		return resetOrgBal;
	}

	public void setResetOrgBal(boolean resetOrgBal) {
		this.resetOrgBal = resetOrgBal;
	}

	public boolean isResetNxtRpyInstReq() {
		return resetNxtRpyInstReq;
	}

	public void setResetNxtRpyInstReq(boolean resetNxtRpyInstReq) {
		this.resetNxtRpyInstReq = resetNxtRpyInstReq;
	}

	public boolean isDevFinCalReq() {
		return devFinCalReq;
	}

	public void setDevFinCalReq(boolean devFinCalReq) {
		this.devFinCalReq = devFinCalReq;
	}

	public BigDecimal getAdjOrgBal() {
		return adjOrgBal;
	}

	public void setAdjOrgBal(BigDecimal adjOrgBal) {
		this.adjOrgBal = adjOrgBal;
	}

	public BigDecimal getRemBalForAdj() {
		return remBalForAdj;
	}

	public void setRemBalForAdj(BigDecimal remBalForAdj) {
		this.remBalForAdj = remBalForAdj;
	}

	public String getLovDescEntityCode() {
		return lovDescEntityCode;
	}

	public void setLovDescEntityCode(String lovDescEntityCode) {
		this.lovDescEntityCode = lovDescEntityCode;
	}

	public String getStage() {
		return stage;
	}

	public void setStage(String stage) {
		this.stage = stage;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public long getEligibilityMethod() {
		return eligibilityMethod;
	}

	public void setEligibilityMethod(long eligibilityMethod) {
		this.eligibilityMethod = eligibilityMethod;
	}

	public String getLovEligibilityMethod() {
		return lovEligibilityMethod;
	}

	public void setLovEligibilityMethod(String lovEligibilityMethod) {
		this.lovEligibilityMethod = lovEligibilityMethod;
	}

	public String getLovDescEligibilityMethod() {
		return lovDescEligibilityMethod;
	}

	public void setLovDescEligibilityMethod(String lovDescEligibilityMethod) {
		this.lovDescEligibilityMethod = lovDescEligibilityMethod;
	}

	public boolean isBpiResetReq() {
		return bpiResetReq;
	}

	public void setBpiResetReq(boolean bpiResetReq) {
		this.bpiResetReq = bpiResetReq;
	}

	public String getDsaName() {
		return dsaName;
	}

	public void setDsaName(String dsaName) {
		this.dsaName = dsaName;
	}

	public String getDmaName() {
		return dmaName;
	}

	public void setDmaName(String dmaName) {
		this.dmaName = dmaName;
	}

	public long getPostingId() {
		return postingId;
	}

	public void setPostingId(long postingId) {
		this.postingId = postingId;
	}

	public String getCollateralType() {
		return collateralType;
	}

	public void setCollateralType(String collateralType) {
		this.collateralType = collateralType;
	}

	public BigDecimal getMarketValue() {
		return marketValue;
	}

	public void setMarketValue(BigDecimal marketValue) {
		this.marketValue = marketValue;
	}

	public BigDecimal getGuidedValue() {
		return guidedValue;
	}

	public void setGuidedValue(BigDecimal guidedValue) {
		this.guidedValue = guidedValue;
	}

	public BigDecimal getTotalExposure() {
		return totalExposure;
	}

	public void setTotalExposure(BigDecimal totalExposure) {
		this.totalExposure = totalExposure;
	}

	public boolean isSamplingRequired() {
		return samplingRequired;
	}

	public void setSamplingRequired(boolean samplingRequired) {
		this.samplingRequired = samplingRequired;
	}

	public boolean isLegalRequired() {
		return legalRequired;
	}

	public void setLegalRequired(boolean legalRequired) {
		this.legalRequired = legalRequired;
	}

	public String getEmployeeName() {
		return employeeName;
	}

	public void setEmployeeName(String employeeName) {
		this.employeeName = employeeName;
	}

	public String getEmployeeNameDesc() {
		return employeeNameDesc;
	}

	public void setEmployeeNameDesc(String employeeNameDesc) {
		this.employeeNameDesc = employeeNameDesc;
	}

	public BigDecimal getGrcMaxAmount() {
		return grcMaxAmount;
	}

	public void setGrcMaxAmount(BigDecimal grcMaxAmount) {
		this.grcMaxAmount = grcMaxAmount;
	}

	public BigDecimal getRepayBaseRateVal() {
		return repayBaseRateVal;
	}

	public void setRepayBaseRateVal(BigDecimal repayBaseRateVal) {
		this.repayBaseRateVal = repayBaseRateVal;
	}

	public boolean isDepositProcess() {
		return depositProcess;
	}

	public void setDepositProcess(boolean depositProcess) {
		this.depositProcess = depositProcess;
	}

	public long getConnector() {
		return connector;
	}

	public void setConnector(long connector) {
		this.connector = connector;
	}

	public String getConnectorCode() {
		return connectorCode;
	}

	public void setConnectorCode(String connectorCode) {
		this.connectorCode = connectorCode;
	}

	public String getConnectorDesc() {
		return connectorDesc;
	}

	public void setConnectorDesc(String connectorDesc) {
		this.connectorDesc = connectorDesc;
	}

	public BigDecimal getAdvanceEMI() {
		return advanceEMI;
	}

	public void setAdvanceEMI(BigDecimal advanceEMI) {
		this.advanceEMI = advanceEMI;
	}

	public boolean isAdjustClosingBal() {
		return isAdjustClosingBal;
	}

	public void setAdjustClosingBal(boolean isAdjustClosingBal) {
		this.isAdjustClosingBal = isAdjustClosingBal;
	}

	public String getBpiPftDaysBasis() {
		return bpiPftDaysBasis;
	}

	public void setBpiPftDaysBasis(String bpiPftDaysBasis) {
		this.bpiPftDaysBasis = bpiPftDaysBasis;
	}

	public int getFixedRateTenor() {
		return fixedRateTenor;
	}

	public void setFixedRateTenor(int fixedRateTenor) {
		this.fixedRateTenor = fixedRateTenor;
	}

	public BigDecimal getFixedTenorRate() {
		return fixedTenorRate;
	}

	public void setFixedTenorRate(BigDecimal fixedTenorRate) {
		this.fixedTenorRate = fixedTenorRate;
	}

	public Date getFixedTenorEndDate() {
		return fixedTenorEndDate;
	}

	public void setFixedTenorEndDate(Date fixedTenorEndDate) {
		this.fixedTenorEndDate = fixedTenorEndDate;
	}

	public String getFinBranchProvinceCode() {
		return finBranchProvinceCode;
	}

	public void setFinBranchProvinceCode(String finBranchProvinceCode) {
		this.finBranchProvinceCode = finBranchProvinceCode;
	}

	public String getProcessAttributes() {
		return processAttributes;
	}

	public void setProcessAttributes(String processAttributes) {
		Map<String, String> result = new HashMap<>();

		if (StringUtils.isNotBlank(processAttributes)) {
			processAttributes = StringUtils.trimToEmpty(processAttributes);
			String[] params = processAttributes.split(",");

			for (String param : params) {
				if (StringUtils.isNotBlank(param)) {
					param = StringUtils.trimToEmpty(param);
					String[] attr = param.split("=");

					if (attr.length >= 1 && StringUtils.isNotBlank(attr[0])) {
						if (attr.length == 1) {
							result.put(StringUtils.trimToEmpty(attr[0]), "");
						} else {
							result.put(StringUtils.trimToEmpty(attr[0]), StringUtils.trimToEmpty(attr[1]));
						}
					}
				}
			}
		}

		addAttributes(result);
	}

	public String getHigherDeviationApprover() {
		return higherDeviationApprover;
	}

	public void setHigherDeviationApprover(String higherDeviationApprover) {
		this.higherDeviationApprover = higherDeviationApprover;
	}

	public Map<String, String> getAttributes() {
		return attributes;
	}

	public void addAttributes(Map<String, String> attributes) {
		this.attributes.putAll(attributes);

		// Update process attributes.
		StringBuilder result = new StringBuilder();

		for (Entry<String, String> entry : this.attributes.entrySet()) {
			if (result.length() > 0) {
				result.append(',');
			}

			result.append(entry.getKey()).append('=').append(entry.getValue());
		}

		this.processAttributes = result.toString();
	}

	public BigDecimal getRepayAmount() {
		return repayAmount;
	}

	public void setRepayAmount(BigDecimal repayAmount) {
		this.repayAmount = repayAmount;
	}

	public String getEntityCode() {
		return entityCode;
	}

	public void setEntityCode(String entityCode) {
		this.entityCode = entityCode;
	}

	public String getEntityDesc() {
		return entityDesc;
	}

	public void setEntityDesc(String entityDesc) {
		this.entityDesc = entityDesc;
	}

	public Long getBusinessVertical() {
		return businessVertical;
	}

	public void setBusinessVertical(Long businessVertical) {
		this.businessVertical = businessVertical;
	}

	public String getBusinessVerticalCode() {
		return businessVerticalCode;
	}

	public void setBusinessVerticalCode(String businessVerticalCode) {
		this.businessVerticalCode = businessVerticalCode;
	}

	public String getBusinessVerticalDesc() {
		return businessVerticalDesc;
	}

	public void setBusinessVerticalDesc(String businessVerticalDesc) {
		this.businessVerticalDesc = businessVerticalDesc;
	}

	public String getGrcAdvType() {
		return grcAdvType;
	}

	public void setGrcAdvType(String grcAdvType) {
		this.grcAdvType = grcAdvType;
	}

	public int getGrcAdvTerms() {
		return grcAdvTerms;
	}

	public void setGrcAdvTerms(int grcAdvTerms) {
		this.grcAdvTerms = grcAdvTerms;
	}

	public String getAdvType() {
		return advType;
	}

	public void setAdvType(String advType) {
		this.advType = advType;
	}

	public int getAdvTerms() {
		return advTerms;
	}

	public void setAdvTerms(int advTerms) {
		this.advTerms = advTerms;
	}

	public String getAdvStage() {
		return advStage;
	}

	public void setAdvStage(String advStage) {
		this.advStage = advStage;
	}

	public boolean isAlwFlexi() {
		return alwFlexi;
	}

	public void setAlwFlexi(boolean alwFlexi) {
		this.alwFlexi = alwFlexi;
	}

	public BigDecimal getFlexiAmount() {
		return flexiAmount;
	}

	public void setFlexiAmount(BigDecimal flexiAmount) {
		this.flexiAmount = flexiAmount;
	}

	public boolean isChgDropLineSchd() {
		return chgDropLineSchd;
	}

	public void setChgDropLineSchd(boolean chgDropLineSchd) {
		this.chgDropLineSchd = chgDropLineSchd;
	}

	public Long getAssignmentId() {
		return assignmentId == null ? 0 : assignmentId;
	}

	public void setAssignmentId(Long assignmentId) {
		this.assignmentId = assignmentId;
	}

	public Long getPromotionSeqId() {
		return promotionSeqId;
	}

	public void setPromotionSeqId(Long promotionSeqId) {
		this.promotionSeqId = promotionSeqId;
	}

	public String getLoanCategory() {
		return loanCategory;
	}

	public void setLoanCategory(String loanCategory) {
		this.loanCategory = loanCategory;
	}

	public boolean isAllowSubvention() {
		return allowSubvention;
	}

	public void setAllowSubvention(boolean allowSubvention) {
		this.allowSubvention = allowSubvention;
	}

	public Map<String, Object> getGlSubHeadCodes() {
		return glSubHeadCodes;
	}

	public void setGlSubHeadCodes(Map<String, Object> glSubHeadCodes) {
		this.glSubHeadCodes = glSubHeadCodes;
	}

	public Date getClosedDate() {
		return closedDate;
	}

	public void setClosedDate(Date closedDate) {
		this.closedDate = closedDate;
	}

	public int getRecalIdx() {
		return recalIdx;
	}

	public void setRecalIdx(int recalIdx) {
		this.recalIdx = recalIdx;
	}

	public boolean isAlwStrtPrdHday() {
		return alwStrtPrdHday;
	}

	public void setAlwStrtPrdHday(boolean alwStrtPrdHday) {
		this.alwStrtPrdHday = alwStrtPrdHday;
	}

	public int getMaxStrtPrdHdays() {
		return maxStrtPrdHdays;
	}

	public void setMaxStrtPrdHdays(int maxStrtPrdHdays) {
		this.maxStrtPrdHdays = maxStrtPrdHdays;
	}

	public int getStrtPrdHdays() {
		return strtPrdHdays;
	}

	public void setStrtPrdHdays(int strtPrdHdays) {
		this.strtPrdHdays = strtPrdHdays;
	}

	public String getStrtprdCpzMethod() {
		return strtprdCpzMethod;
	}

	public void setStrtprdCpzMethod(String strtprdCpzMethod) {
		this.strtprdCpzMethod = strtprdCpzMethod;
	}

	public String getOldFinReference() {
		return oldFinReference;
	}

	public void setOldFinReference(String oldFinReference) {
		this.oldFinReference = oldFinReference;
	}

	public BigDecimal getTdsPercentage() {
		return tdsPercentage;
	}

	public void setTdsPercentage(BigDecimal tdsPercentage) {
		this.tdsPercentage = tdsPercentage;
	}

	public Date getTdsStartDate() {
		return tdsStartDate;
	}

	public void setTdsStartDate(Date tdsStartDate) {
		this.tdsStartDate = tdsStartDate;
	}

	public Date getTdsEndDate() {
		return tdsEndDate;
	}

	public void setTdsEndDate(Date tdsEndDate) {
		this.tdsEndDate = tdsEndDate;
	}

	public BigDecimal getTdsLimitAmt() {
		return tdsLimitAmt;
	}

	public void setTdsLimitAmt(BigDecimal tdsLimitAmt) {
		this.tdsLimitAmt = tdsLimitAmt;
	}

	public String getCoreBankId() {
		return coreBankId;
	}

	public void setCoreBankId(String coreBankId) {
		this.coreBankId = coreBankId;
	}

	public BigDecimal getIntTdsAdjusted() {
		return intTdsAdjusted;
	}

	public void setIntTdsAdjusted(BigDecimal intTdsAdjusted) {
		this.intTdsAdjusted = intTdsAdjusted;
	}

	public String getVanCode() {
		return vanCode;
	}

	public void setVanCode(String vanCode) {
		this.vanCode = vanCode;
	}

	public boolean isVanReq() {
		return vanReq;
	}

	public void setVanReq(boolean vanReq) {
		this.vanReq = vanReq;
	}

	public String getExtReference() {
		return extReference;
	}

	public void setExtReference(String extReference) {
		this.extReference = extReference;
	}

	public boolean isAllowDrawingPower() {
		return allowDrawingPower;
	}

	public void setAllowDrawingPower(boolean allowDrawingPower) {
		this.allowDrawingPower = allowDrawingPower;
	}

	public boolean isAllowRevolving() {
		return allowRevolving;
	}

	public void setAllowRevolving(boolean allowRevolving) {
		this.allowRevolving = allowRevolving;
	}

	public String getServiceName() {
		return serviceName;
	}

	public void setServiceName(String serviceName) {
		this.serviceName = serviceName;
	}

	public boolean isModifyBpi() {
		return modifyBpi;
	}

	public void setModifyBpi(boolean modifyBpi) {
		this.modifyBpi = modifyBpi;
	}

	public BigDecimal getPftCpzFromReset() {
		return pftCpzFromReset;
	}

	public void setPftCpzFromReset(BigDecimal pftCpzFromReset) {
		this.pftCpzFromReset = pftCpzFromReset;
	}

	public boolean isSanBsdSchdle() {
		return sanBsdSchdle;
	}

	public void setSanBsdSchdle(boolean sanBsdSchdle) {
		this.sanBsdSchdle = sanBsdSchdle;
	}

	public boolean isApplySanctionCheck() {
		return applySanctionCheck;
	}

	public void setApplySanctionCheck(boolean applySanctionCheck) {
		this.applySanctionCheck = applySanctionCheck;
	}

	public BigDecimal getSvAmount() {
		return svAmount;
	}

	public void setSvAmount(BigDecimal svAmount) {
		this.svAmount = svAmount;
	}

	public BigDecimal getCbAmount() {
		return cbAmount;
	}

	public void setCbAmount(BigDecimal cbAmount) {
		this.cbAmount = cbAmount;
	}

	public boolean isGrcFrqEditable() {
		return grcFrqEditable;
	}

	public void setGrcFrqEditable(boolean grcFrqEditable) {
		this.grcFrqEditable = grcFrqEditable;
	}

	public boolean isFrqEditable() {
		return frqEditable;
	}

	public void setFrqEditable(boolean frqEditable) {
		this.frqEditable = frqEditable;
	}

	public String getbRRpyRvwFrq() {
		return bRRpyRvwFrq;
	}

	public void setbRRpyRvwFrq(String bRRpyRvwFrq) {
		this.bRRpyRvwFrq = bRRpyRvwFrq;
	}

	public BigDecimal getTotalFinAmount() {
		return totalFinAmount;
	}

	public void setTotalFinAmount(BigDecimal totalFinAmount) {
		this.totalFinAmount = totalFinAmount;
	}

	public BigDecimal getAppliedLoanAmt() {
		return appliedLoanAmt;
	}

	public void setAppliedLoanAmt(BigDecimal appliedLoanAmt) {
		this.appliedLoanAmt = appliedLoanAmt;
	}

	public boolean isFinIsRateRvwAtGrcEnd() {
		return finIsRateRvwAtGrcEnd;
	}

	public void setFinIsRateRvwAtGrcEnd(boolean finIsRateRvwAtGrcEnd) {
		this.finIsRateRvwAtGrcEnd = finIsRateRvwAtGrcEnd;
	}

	public boolean isSkipRateReset() {
		return skipRateReset;
	}

	public void setSkipRateReset(boolean skipRateReset) {
		this.skipRateReset = skipRateReset;
	}

	public String getCancelRemarks() {
		return cancelRemarks;
	}

	public void setCancelRemarks(String cancelRemarks) {
		this.cancelRemarks = cancelRemarks;
	}

	public String getCancelType() {
		return cancelType;
	}

	public void setCancelType(String cancelType) {
		this.cancelType = cancelType;
	}

	public List<ReasonDetails> getDetailsList() {
		return detailsList;
	}

	public void setDetailsList(List<ReasonDetails> detailsList) {
		this.detailsList = detailsList;
	}

	public void addExtendedField(String fieldName, Object value) {
		this.extendedFields.put(fieldName, value);
	}

	public void setExtendedFields(Map<String, Object> ruleMap) {
		this.extendedFields.putAll(ruleMap);
	}

	public Object getExtendedValue(String fieldName) {
		return this.extendedFields.get(fieldName);
	}

	public String getProduct() {
		return product;
	}

	public void setProduct(String product) {
		this.product = product;
	}

	public String getHunterStatus() {
		return hunterStatus;
	}

	public boolean isAutoApprove() {
		return autoApprove;
	}

	public void setAutoApprove(boolean autoApprove) {
		this.autoApprove = autoApprove;
	}

	public void setHunterStatus(String hunterStatus) {
		this.hunterStatus = hunterStatus;
	}

	public int getAutoRejectionDays() {
		return autoRejectionDays;
	}

	public void setAutoRejectionDays(int autoRejectionDays) {
		this.autoRejectionDays = autoRejectionDays;
	}

	/*
	 * public String getParentRef() { return parentRef; }
	 * 
	 * public void setParentRef(String parentRef) { this.parentRef = parentRef; }
	 */

	public BigDecimal getOsPriBal() {
		return osPriBal;
	}

	public void setOsPriBal(BigDecimal osPriBal) {
		this.osPriBal = osPriBal;
	}

	public boolean isRateChange() {
		return rateChange;
	}

	public void setRateChange(boolean rateChange) {
		this.rateChange = rateChange;
	}

	public Date getAppDate() {
		return appDate;
	}

	public void setAppDate(Date appDate) {
		this.appDate = appDate;
	}

	public Date getEodValueDate() {
		return eodValueDate;
	}

	public void setEodValueDate(Date eodValueDate) {
		this.eodValueDate = eodValueDate;
	}

	/*
	 * public String getParentRef() { return parentRef; }
	 * 
	 * public void setParentRef(String parentRef) { this.parentRef = parentRef; }
	 */

	public BigDecimal getReqLoanAmt() {
		return reqLoanAmt;
	}

	public void setReqLoanAmt(BigDecimal reqLoanAmt) {
		this.reqLoanAmt = reqLoanAmt;
	}

	public Integer getReqLoanTenor() {
		return reqLoanTenor;
	}

	public void setReqLoanTenor(Integer reqLoanTenor) {
		this.reqLoanTenor = reqLoanTenor;
	}

	public boolean isFinOcrRequired() {
		return finOcrRequired;
	}

	public void setFinOcrRequired(boolean finOcrRequired) {
		this.finOcrRequired = finOcrRequired;
	}

	public String getOfferProduct() {
		return offerProduct;
	}

	public void setOfferProduct(String offerProduct) {
		this.offerProduct = offerProduct;
	}

	public BigDecimal getOfferAmount() {
		return offerAmount;
	}

	public void setOfferAmount(BigDecimal offerAmount) {
		this.offerAmount = offerAmount;
	}

	public String getCustSegmentation() {
		return custSegmentation;
	}

	public void setCustSegmentation(String custSegmentation) {
		this.custSegmentation = custSegmentation;
	}

	public String getBaseProduct() {
		return baseProduct;
	}

	public void setBaseProduct(String baseProduct) {
		this.baseProduct = baseProduct;
	}

	public String getProcessType() {
		return processType;
	}

	public void setProcessType(String processType) {
		this.processType = processType;
	}

	public String getBureauTimeSeries() {
		return bureauTimeSeries;
	}

	public void setBureauTimeSeries(String bureauTimeSeries) {
		this.bureauTimeSeries = bureauTimeSeries;
	}

	public String getCampaignName() {
		return campaignName;
	}

	public void setCampaignName(String campaignName) {
		this.campaignName = campaignName;
	}

	public String getExistingLanRefNo() {
		return existingLanRefNo;
	}

	public void setExistingLanRefNo(String existingLanRefNo) {
		this.existingLanRefNo = existingLanRefNo;
	}

	public boolean isRsa() {
		return rsa;
	}

	public void setRsa(boolean rsa) {
		this.rsa = rsa;
	}

	public String getVerification() {
		return verification;
	}

	public void setVerification(String verification) {
		this.verification = verification;
	}

	public String getLeadSource() {
		return leadSource;
	}

	public void setLeadSource(String leadSource) {
		this.leadSource = leadSource;
	}

	public String getPoSource() {
		return poSource;
	}

	public void setPoSource(String poSource) {
		this.poSource = poSource;
	}

	public String getSourcingBranch() {
		return sourcingBranch;
	}

	public void setSourcingBranch(String sourcingBranch) {
		this.sourcingBranch = sourcingBranch;
	}

	public String getLovDescSourcingBranch() {
		return lovDescSourcingBranch;
	}

	public void setLovDescSourcingBranch(String lovDescSourcingBranch) {
		this.lovDescSourcingBranch = lovDescSourcingBranch;
	}

	public String getSourChannelCategory() {
		return sourChannelCategory;
	}

	public void setSourChannelCategory(String sourChannelCategory) {
		this.sourChannelCategory = sourChannelCategory;
	}

	public Long getAsmName() {
		return asmName;
	}

	public void setAsmName(Long asmName) {
		this.asmName = asmName;
	}

	public String getOfferId() {
		return offerId;
	}

	public void setOfferId(String offerId) {
		this.offerId = offerId;
	}

	public boolean isDeviationAvail() {
		return deviationAvail;
	}

	public void setDeviationAvail(boolean deviationAvail) {
		this.deviationAvail = deviationAvail;
	}

	public boolean isPmay() {
		return pmay;
	}

	public void setPmay(boolean pmay) {
		this.pmay = pmay;
	}

	/*
	 * public String getParentRef() { return parentRef; }
	 * 
	 * public void setParentRef(String parentRef) { this.parentRef = parentRef; }
	 */

	public boolean isAlwGrcAdj() {
		return alwGrcAdj;
	}

	public void setAlwGrcAdj(boolean alwGrcAdj) {
		this.alwGrcAdj = alwGrcAdj;
	}

	public boolean isEndGrcPeriodAftrFullDisb() {
		return endGrcPeriodAftrFullDisb;
	}

	public void setEndGrcPeriodAftrFullDisb(boolean endGrcPeriodAftrFullDisb) {
		this.endGrcPeriodAftrFullDisb = endGrcPeriodAftrFullDisb;
	}

	public boolean isAutoIncGrcEndDate() {
		return autoIncGrcEndDate;
	}

	public void setAutoIncGrcEndDate(boolean autoIncGrcEndDate) {
		this.autoIncGrcEndDate = autoIncGrcEndDate;
	}

	public int getPendingCovntCount() {
		return pendingCovntCount;
	}

	public boolean isRecalSteps() {
		return recalSteps;
	}

	public void setRecalSteps(boolean recalSteps) {
		this.recalSteps = recalSteps;
	}

	public void setPendingCovntCount(int pendingCovntCount) {
		this.pendingCovntCount = pendingCovntCount;
	}

	public String getParentRef() {
		return parentRef;
	}

	public void setParentRef(String parentRef) {
		this.parentRef = parentRef;
	}

	public boolean isAlwLoanSplit() {
		return alwLoanSplit;
	}

	public void setAlwLoanSplit(boolean alwLoanSplit) {
		this.alwLoanSplit = alwLoanSplit;
	}

	public boolean isInstBasedSchd() {
		return instBasedSchd;
	}

	public void setInstBasedSchd(boolean instBasedSchd) {
		this.instBasedSchd = instBasedSchd;
	}

	public boolean isLoanSplitted() {
		return loanSplitted;
	}

	public void setLoanSplitted(boolean loanSplitted) {
		this.loanSplitted = loanSplitted;
	}

	public String getCustEmpType() {
		return custEmpType;
	}

	public void setCustEmpType(String custEmpType) {
		this.custEmpType = custEmpType;
	}

	public String getConnectorReference() {
		return connectorReference;
	}

	public void setConnectorReference(String connectorReference) {
		this.connectorReference = connectorReference;
	}

	public String getDsaCodeReference() {
		return dsaCodeReference;
	}

	public void setDsaCodeReference(String dsaCodeReference) {
		this.dsaCodeReference = dsaCodeReference;
	}

	public String getAccountsOfficerReference() {
		return accountsOfficerReference;
	}

	public void setAccountsOfficerReference(String accountsOfficerReference) {
		this.accountsOfficerReference = accountsOfficerReference;
	}

	public String getDmaCodeReference() {
		return dmaCodeReference;
	}

	public void setDmaCodeReference(String dmaCodeReference) {
		this.dmaCodeReference = dmaCodeReference;
	}

	public boolean isSimulateAccounting() {
		return simulateAccounting;
	}

	public void setSimulateAccounting(boolean simulateAccounting) {
		this.simulateAccounting = simulateAccounting;
	}

	public List<ReturnDataSet> getReturnDataSet() {
		return returnDataSet;
	}

	public void setReturnDataSet(List<ReturnDataSet> returnDataSet) {
		this.returnDataSet = returnDataSet;
	}

	public boolean isOcrDeviation() {
		return ocrDeviation;
	}

	public void setOcrDeviation(boolean ocrDeviation) {
		this.ocrDeviation = ocrDeviation;
	}

	public EventProperties getEventProperties() {
		return eventProperties;
	}

	public void setEventProperties(EventProperties eventProperties) {
		this.eventProperties = eventProperties;
	}

	public String getPartnerBankAcType() {
		return partnerBankAcType;
	}

	public void setPartnerBankAcType(String partnerBankAcType) {
		this.partnerBankAcType = partnerBankAcType;
	}

	public String getPartnerBankAc() {
		return partnerBankAc;
	}

	public void setPartnerBankAc(String partnerBankAc) {
		this.partnerBankAc = partnerBankAc;
	}

	public String getTdsType() {
		return tdsType;
	}

	public void setTdsType(String tdsType) {
		this.tdsType = tdsType;
	}

	public boolean isWriteoffLoan() {
		return writeoffLoan;
	}

	public void setWriteoffLoan(boolean writeoffLoan) {
		this.writeoffLoan = writeoffLoan;
	}

	public boolean isRestructure() {
		return restructure;
	}

	public void setRestructure(boolean restructure) {
		this.restructure = restructure;
	}

	public String getCalcOfSteps() {
		return calcOfSteps;
	}

	public void setCalcOfSteps(String calcOfSteps) {
		this.calcOfSteps = calcOfSteps;
	}

	public String getStepsAppliedFor() {
		return stepsAppliedFor;
	}

	public void setStepsAppliedFor(String stepsAppliedFor) {
		this.stepsAppliedFor = stepsAppliedFor;
	}

	public boolean isRpyStps() {
		return isRpyStps;
	}

	public void setRpyStps(boolean isRpyStps) {
		this.isRpyStps = isRpyStps;
	}

	public boolean isGrcStps() {
		return isGrcStps;
	}

	public void setGrcStps(boolean isGrcStps) {
		this.isGrcStps = isGrcStps;
	}

	public boolean isCpzPosIntact() {
		return cpzPosIntact;
	}

	public void setCpzPosIntact(boolean cpzPosIntact) {
		this.cpzPosIntact = cpzPosIntact;
	}

	public int getNoOfGrcSteps() {
		return noOfGrcSteps;
	}

	public void setNoOfGrcSteps(int noOfGrcSteps) {
		this.noOfGrcSteps = noOfGrcSteps;
	}

	public int getSchdVersion() {
		return schdVersion;
	}

	public void setSchdVersion(int schdVersion) {
		this.schdVersion = schdVersion;
	}

	public String getSubVentionFrom() {
		return subVentionFrom;
	}

	public void setSubVentionFrom(String subVentionFrom) {
		this.subVentionFrom = subVentionFrom;
	}

	public Long getManufacturerDealerId() {
		return manufacturerDealerId;
	}

	public void setManufacturerDealerId(Long manufacturerDealerId) {
		this.manufacturerDealerId = manufacturerDealerId;
	}

	public String getManufacturerDealerName() {
		return manufacturerDealerName;
	}

	public void setManufacturerDealerName(String manufacturerDealerName) {
		this.manufacturerDealerName = manufacturerDealerName;
	}

	public String getManufacturerDealerCode() {
		return manufacturerDealerCode;
	}

	public void setManufacturerDealerCode(String manufacturerDealerCode) {
		this.manufacturerDealerCode = manufacturerDealerCode;
	}

	public long getInstructionUID() {
		return instructionUID;
	}

	public void setInstructionUID(long instructionUID) {
		this.instructionUID = instructionUID;
	}

	public boolean isEscrow() {
		return escrow;
	}

	public void setEscrow(boolean escrow) {
		this.escrow = escrow;
	}

	public Long getCustBankId() {
		return custBankId;
	}

	public void setCustBankId(Long custBankId) {
		this.custBankId = custBankId;
	}

	public String getCustAcctNumber() {
		return custAcctNumber;
	}

	public void setCustAcctNumber(String custAcctNumber) {
		this.custAcctNumber = custAcctNumber;
	}

	public String getCustAcctHolderName() {
		return custAcctHolderName;
	}

	public void setCustAcctHolderName(String custAcctHolderName) {
		this.custAcctHolderName = custAcctHolderName;
	}

	public Map<String, BigDecimal> getTaxPercentages() {
		return taxPercentages;
	}

	public void setTaxPercentages(Map<String, BigDecimal> taxPercentages) {
		this.taxPercentages = taxPercentages;
	}

	public Map<String, Object> getGstExecutionMap() {
		return gstExecutionMap;
	}

	public void setGstExecutionMap(Map<String, Object> gstExecutionMap) {
		this.gstExecutionMap = gstExecutionMap;
	}

	public BigDecimal getExpectedEndBal() {
		return expectedEndBal;
	}

	public void setExpectedEndBal(BigDecimal expectedEndBal) {
		this.expectedEndBal = expectedEndBal;
	}

	public int getNoOfPrincipalHdays() {
		return noOfPrincipalHdays;
	}

	public void setNoOfPrincipalHdays(int noOfPrincipalHdays) {
		this.noOfPrincipalHdays = noOfPrincipalHdays;
	}

	public String getManualSchdType() {
		return manualSchdType;
	}

	public void setManualSchdType(String manualSchdType) {
		this.manualSchdType = manualSchdType;
	}

	public BigDecimal getEarlyPayAmount() {
		return earlyPayAmount;
	}

	public void setEarlyPayAmount(BigDecimal earlyPayAmount) {
		this.earlyPayAmount = earlyPayAmount;
	}

	public boolean isIsra() {
		return isra;
	}

	public void setIsra(boolean isra) {
		this.isra = isra;
	}

	public boolean isOverdraftTxnChrgReq() {
		return overdraftTxnChrgReq;
	}

	public void setOverdraftTxnChrgReq(boolean overdraftTxnChrgReq) {
		this.overdraftTxnChrgReq = overdraftTxnChrgReq;
	}

	public long getOverdraftTxnChrgFeeType() {
		return overdraftTxnChrgFeeType;
	}

	public void setOverdraftTxnChrgFeeType(long overdraftTxnChrgFeeType) {
		this.overdraftTxnChrgFeeType = overdraftTxnChrgFeeType;
	}

	public String getOverdraftCalcChrg() {
		return overdraftCalcChrg;
	}

	public void setOverdraftCalcChrg(String overdraftCalcChrg) {
		this.overdraftCalcChrg = overdraftCalcChrg;
	}

	public String getOverdraftChrCalOn() {
		return overdraftChrCalOn;
	}

	public void setOverdraftChrCalOn(String overdraftChrCalOn) {
		this.overdraftChrCalOn = overdraftChrCalOn;
	}

	public BigDecimal getOverdraftChrgAmtOrPerc() {
		return overdraftChrgAmtOrPerc;
	}

	public void setOverdraftChrgAmtOrPerc(BigDecimal overdraftChrgAmtOrPerc) {
		this.overdraftChrgAmtOrPerc = overdraftChrgAmtOrPerc;
	}

	public String getReceiptChannel() {
		return receiptChannel;
	}

	public void setReceiptChannel(String receiptChannel) {
		this.receiptChannel = receiptChannel;
	}

	public Date getSanctionedDate() {
		return sanctionedDate;
	}

	public void setSanctionedDate(Date sanctionedDate) {
		this.sanctionedDate = sanctionedDate;
	}

	public List<FinODPenaltyRate> getPenaltyRates() {
		return penaltyRates;
	}

	public void setPenaltyRates(List<FinODPenaltyRate> penaltyRates) {
		this.penaltyRates = penaltyRates;
	}

	public List<FinanceScheduleDetail> getOldSchedules() {
		return oldSchedules;
	}

	public void setOldSchedules(List<FinanceScheduleDetail> oldSchedules) {
		this.oldSchedules = oldSchedules;
	}

	public Date getRestructureDate() {
		return restructureDate;
	}

	public void setRestructureDate(Date restructureDate) {
		this.restructureDate = restructureDate;
	}

	public String getEffSchdMethod() {
		return effSchdMethod;
	}

	public void setEffSchdMethod(String effSchdMethod) {
		this.effSchdMethod = effSchdMethod;
	}

	public BigDecimal getSanBasedPft() {
		return sanBasedPft;
	}

	public void setSanBasedPft(BigDecimal sanBasedPft) {
		this.sanBasedPft = sanBasedPft;
	}

	public String getModuleDefiner() {
		return moduleDefiner;
	}

	public void setModuleDefiner(String moduleDefiner) {
		this.moduleDefiner = moduleDefiner;
	}

	public boolean isStepRecalOnProrata() {
		return stepRecalOnProrata;
	}

	public void setStepRecalOnProrata(boolean stepRecalOnProrata) {
		this.stepRecalOnProrata = stepRecalOnProrata;
	}

	public boolean isResetFromLastStep() {
		return resetFromLastStep;
	}

	public void setResetFromLastStep(boolean resetFromLastStep) {
		this.resetFromLastStep = resetFromLastStep;
	}

	public Long getSecurityMandateID() {
		return securityMandateID;
	}

	public void setSecurityMandateID(Long securityMandateID) {
		this.securityMandateID = securityMandateID;
	}

	public boolean isWifLoan() {
		return wifLoan;
	}

	public void setWifLoan(boolean wifLoan) {
		this.wifLoan = wifLoan;
	}

	public String getHoldStatus() {
		return holdStatus;
	}

	public void setHoldStatus(String holdStatus) {
		this.holdStatus = holdStatus;
	}

	public String getReason() {
		return reason;
	}

	public void setReason(String reason) {
		this.reason = reason;
	}

	public boolean isUnderSettlement() {
		return underSettlement;
	}

	public void setUnderSettlement(boolean underSettlement) {
		this.underSettlement = underSettlement;
	}

	public boolean isEOD() {
		return isEOD;
	}

	public void setEOD(boolean isEOD) {
		this.isEOD = isEOD;
	}

	public Date getGrcStartDate() {
		return grcStartDate;
	}

	public void setGrcStartDate(Date grcStartDate) {
		this.grcStartDate = grcStartDate;
	}

	public boolean isUnderNpa() {
		return underNpa;
	}

	public void setUnderNpa(boolean underNpa) {
		this.underNpa = underNpa;
	}

	public String getCustCoreBank() {
		return custCoreBank;
	}

	public void setCustCoreBank(String custCoreBank) {
		this.custCoreBank = custCoreBank;
	}

	public String getClosureType() {
		return closureType;
	}

	public void setClosureType(String closureType) {
		this.closureType = closureType;
	}

	public long getCreatedBy() {
		return createdBy;
	}

	public void setCreatedBy(long createdBy) {
		this.createdBy = createdBy;
	}

	public Timestamp getCreatedOn() {
		return createdOn;
	}

	public void setCreatedOn(Timestamp createdOn) {
		this.createdOn = createdOn;
	}

	public Long getApprovedBy() {
		return approvedBy;
	}

	public void setApprovedBy(Long approvedBy) {
		this.approvedBy = approvedBy;
	}

	public Timestamp getApprovedOn() {
		return approvedOn;
	}

	public void setApprovedOn(Timestamp approvedOn) {
		this.approvedOn = approvedOn;
	}

	public String getLoanName() {
		return loanName;
	}

	public void setLoanName(String loanName) {
		this.loanName = loanName;
	}

	public Date getCustDOB() {
		return custDOB;
	}

	public void setCustDOB(Date custDOB) {
		this.custDOB = custDOB;
	}

	public boolean isOldActiveState() {
		return oldActiveState;
	}

	public void setOldActiveState(boolean oldActiveState) {
		this.oldActiveState = oldActiveState;
	}

}