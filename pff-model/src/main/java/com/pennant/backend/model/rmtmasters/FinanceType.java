/**
 * 
 * 
 * Copyright 2011 - Pennant Technologies This file is part of Pennant Java Application Framework and related Products.
 * All components/modules/functions/classes/logic in this software, unless otherwise stated, the property of Pennant
 * Technologies. Copyright and other intellectual property laws protect these materials. Reproduction or retransmission
 * of the materials, in whole or in part, in any manner, without the prior written consent of the copyright holder, is a
 * violation of copyright law.
 */

/**
 ******************************************************************************************** 
 * FILE HEADER *
 ******************************************************************************************** 
 * * FileName : FinanceType.java * * Author : PENNANT TECHONOLOGIES * * Creation Date : 30-06-2011 * * Modified Date :
 * 30-06-2011 * * Description : * *
 ******************************************************************************************** 
 * Date Author Version Comments *
 ******************************************************************************************** 
 * 30-06-2011 Pennant 0.1 * * * * * * * * *
 ******************************************************************************************** 
 */

package com.pennant.backend.model.rmtmasters;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlTransient;

import com.pennant.backend.model.applicationmaster.IRRFinanceType;
import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.finance.FeeType;
import com.pennant.backend.model.financemanagement.FinTypeReceiptModes;
import com.pennant.backend.model.financemanagement.FinTypeVASProducts;
import com.pennanttech.pennapps.core.model.AbstractWorkflowEntity;
import com.pennanttech.pennapps.core.model.LoggedInUser;

/**
 * Model class for the <b>FinanceType table</b>.<br>
 */
@XmlAccessorType(XmlAccessType.FIELD)
public class FinanceType extends AbstractWorkflowEntity {

	private static final long serialVersionUID = -4098586745401583126L;

	// Ordered Same AS Table please don't break It
	private String product = "";
	private String finType;
	private String finCategory;
	private String finCategoryDesc;
	private String finTypeDesc;
	private String finCcy;
	private String finDaysCalType;
	private String finDivision;
	private boolean finIsGenRef;
	private BigDecimal finMaxAmount = BigDecimal.ZERO;
	private BigDecimal finMinAmount = BigDecimal.ZERO;
	private String finDftStmtFrq;
	private boolean finIsAlwMD;
	private int finHistRetension;
	private boolean equalRepayment = true;
	private String finAssetType;
	private boolean finIsDwPayRequired;
	private long downPayRule;
	private String downPayRuleCode;
	private String downPayRuleDesc;
	private boolean fInIsAlwGrace;
	private String finRateType;
	private boolean alwZeroIntAcc;
	private String finBaseRate;
	private String finSplRate;
	private BigDecimal finMargin = BigDecimal.ZERO;
	private BigDecimal finIntRate = BigDecimal.ZERO;
	private BigDecimal fInMinRate = BigDecimal.ZERO;
	private BigDecimal finMaxRate = BigDecimal.ZERO;
	private boolean alwHybridRate;
	private int fixedRateTenor;
	private String finDftIntFrq;
	private String finSchdMthd;
	private boolean finIsIntCpz;
	private String finCpzFrq;
	private boolean finIsRvwAlw;
	private String finRvwFrq;
	private String finRvwRateApplFor;
	private boolean finAlwRateChangeAnyDate;
	private String finSchCalCodeOnRvw;
	private String finGrcRateType;
	private String finGrcBaseRate;
	private String finGrcSplRate;
	private BigDecimal finGrcMargin = BigDecimal.ZERO;
	private BigDecimal finGrcIntRate = BigDecimal.ZERO;
	private BigDecimal fInGrcMinRate = BigDecimal.ZERO;
	private BigDecimal finGrcMaxRate = BigDecimal.ZERO;
	private String finGrcDftIntFrq;
	private boolean finIsAlwGrcRepay;
	private String finGrcSchdMthd;
	private boolean finGrcIsIntCpz;
	private String finGrcCpzFrq;
	private boolean finGrcIsRvwAlw;
	private String finGrcRvwFrq;
	private boolean finIsIntCpzAtGrcEnd;
	private int finMinTerm;
	private int finMaxTerm;
	private int finDftTerms;
	private boolean finRepayPftOnFrq;
	private String finRpyFrq;
	private String finRepayMethod;
	private String alwdRpyMethods;
	private boolean finIsAlwPartialRpy;
	private boolean finIsAlwDifferment;
	private int finMaxDifferment;
	private boolean alwPlanDeferment;
	private int planDeferCount;
	private boolean finIsAlwEarlyRpy;
	private boolean finIsAlwEarlySettle;
	private int finODRpyTries;
	private boolean finIsActive;
	private String finScheduleOn;
	private String alwEarlyPayMethods;
	private String finGrcScheduleOn;
	private boolean finCommitmentReq;
	private boolean finCollateralReq;
	private String collateralType;
	private boolean overrideLimit;
	private boolean limitRequired;
	private boolean finCommitmentOvrride;
	private boolean finCollateralOvrride;
	private boolean partiallySecured;

	private boolean finPftUnChanged;
	private Date startDate;
	private Date endDate;
	private boolean manualSchedule;
	private boolean applyGrcPricing;
	private long grcPricingMethod;
	private boolean applyRpyPricing;
	private long rpyPricingMethod;
	private String rpyHierarchy;
	private String grcPricingMethodDesc;
	private String rpyPricingMethodDesc;
	private boolean droplineOD;
	private String droppingMethod;
	private boolean rateChgAnyDay;
	private String frequencyDays;
	// EMI Holiday Fields & BPI & Freezing Period
	private boolean alwBPI;
	private String bpiTreatment;
	private String bpiPftDaysBasis;
	private String pftDueSchOn;
	private boolean planEMIHAlw;
	private boolean alwPlannedEmiInGrc;
	private String planEMIHMethod;
	private int planEMIHMaxPerYear;
	private int planEMIHMax;
	private int planEMIHLockPeriod;
	private boolean planEMICpz;
	private int unPlanEMIHLockPeriod;
	private boolean unPlanEMICpz;
	private boolean alwReage;
	private boolean alwUnPlanEmiHoliday;
	private boolean reAgeCpz;
	private int fddLockPeriod;
	private int maxUnplannedEmi;
	private int maxReAgeHolidays;

	private String roundingMode;
	private int roundingTarget = 0;
	private boolean developerFinance;

	private boolean alwMultiPartyDisb;
	private boolean tdsApplicable;
	private boolean tdsAllowToModify;
	private String tdsApplicableTo;
	private String addrLine1;

	// Profit on past Due
	private String pastduePftCalMthd;
	private BigDecimal pastduePftMargin = BigDecimal.ZERO;

	// Overdue Penalty Details
	private boolean applyODPenalty;
	private boolean oDIncGrcDays;
	private String oDChargeType;
	private int oDGraceDays;
	private String oDChargeCalOn;
	private BigDecimal oDChargeAmtOrPerc = BigDecimal.ZERO;
	private String oDRuleCode;
	private boolean oDAllowWaiver;
	private BigDecimal oDMaxWaiverPerc = BigDecimal.ZERO;
	private BigDecimal oDMinCapAmount = BigDecimal.ZERO;

	// Step In Finance Details
	private boolean stepFinance;
	private boolean steppingMandatory;
	private boolean alwManualSteps;
	private String alwdStepPolicies;
	private String dftStepPolicy;
	private String dftStepPolicyType;
	private String lovDescDftStepPolicyName;
	private String remarks;
	private List<FinTypeVASProducts> finTypeVASProductsList;
	private List<FinTypeReceiptModes> finTypeReceiptModesList = new ArrayList<>();

	// Suspend details
	private String finSuspTrigger;
	private String finSuspRemarks;
	private String lovValue;
	private FinanceType befImage;
	private boolean alwMaxDisbCheckReq;
	private boolean quickDisb;
	private boolean autoApprove;
	private String promotionCode;
	private String promotionDesc;

	private long profitCenterID;
	private String profitCenterCode;
	private String profitCenterDesc;

	private String lovDescEntityCode;
	private String lovDescEntityDesc;

	// cheque
	private boolean chequeCaptureReq;
	// autorejection No of Days
	private int autoRejectionDays;

	// Gst detial
	private boolean taxNoMand;

	private boolean alwVan;
	private String vanAllocationMethod;
	private boolean allowDrawingPower;
	private boolean allowRevolving;
	private boolean sanBsdSchdle;

	private boolean alwSanctionAmt;
	private boolean alwSanctionAmtOverride;
	private boolean finIsRateRvwAtGrcEnd;
	private boolean schdOnPMTCal;
	private Long regProvRule;
	private Long intProvRule;
	private long assetClassSetup;
	private String assetClassSetupCode;
	private String assetClassSetupDesc;

	private boolean overdraftTxnChrgReq;
	private long overdraftTxnChrgFeeType = Long.MIN_VALUE;
	private FeeType feetype;

	// OCR Check
	private boolean ocrRequired;
	private String allowedOCRS;
	private String defaultOCR;
	// Loan Purpose
	private String allowedLoanPurposes;
	private String specificLoanPurposes;
	private boolean allowCancelFin;
	@XmlTransient
	private LoggedInUser userDetails;

	@XmlTransient
	private Map<String, List<AuditDetail>> auditDetailMap = new HashMap<String, List<AuditDetail>>();

	// ==================Not the Table
	private String lovDescFinDivisionName;
	private String lovDescWorkFlowRolesName;
	private String lovDescWorkFlowTypeName;
	private String lovDescPromoFinTypeDesc;
	private String productCategory;

	// only used for API
	private boolean promotionType = false;

	private List<FinTypeAccounting> finTypeAccountingList = new ArrayList<FinTypeAccounting>();
	private Map<String, Long> finTypeAccountingMap = new HashMap<String, Long>();
	private List<FinTypeFees> finTypeFeesList = new ArrayList<FinTypeFees>();
	private List<FinTypePartnerBank> finTypePartnerBankList = new ArrayList<FinTypePartnerBank>();
	private List<FinTypeExpense> finTypeExpenseList = new ArrayList<FinTypeExpense>();

	// Cost of funds
	private String costOfFunds;
	private List<IRRFinanceType> irrFinanceTypeList = new ArrayList<IRRFinanceType>();

	// Collateral LTV Check Details
	private String finLTVCheck;

	// Eligibility Method
	private String eligibilityMethods;
	private boolean putCallRequired = false;

	private boolean grcAdvIntersetReq;
	private String grcAdvType;
	private int grcAdvMinTerms;
	private int grcAdvMaxTerms;
	private int grcAdvDefaultTerms;

	private boolean advIntersetReq;
	private String advType;
	private int advMinTerms;
	private int advMaxTerms;
	private int advDefaultTerms;
	private String advStage;

	private boolean dsfReq;
	private boolean cashCollateralReq;
	private boolean subventionReq;

	// FIXME MUR>>
	private int minGrcTerms;
	private int maxGrcTerms;
	private int defaultGrcTerms;
	private boolean alwChgGrcTerms;

	private int minPureTerms;
	private int maxPureTerms;
	private int defaultPureTerms;
	private boolean alwChgPureTerms;
	private boolean alwCloBefDUe;
	private String finTypeClassification;
	private boolean allowPftBal = true;
	private boolean grcAdjReq;
	private boolean grcPeriodAftrFullDisb;
	private boolean autoIncrGrcEndDate;
	private int grcAutoIncrMonths;
	private int maxAutoIncrAllowed;
	private int thrldtoMaintainGrcPrd;
	private boolean alwLoanSplit;
	private String splitLoanType;
	private boolean instBasedSchd;
	private String tdsType;
	private String calcOfSteps;
	private String stepsAppliedFor;
	private int overDraftExtGraceDays;
	private long overDraftColChrgFeeType;
	private BigDecimal overDraftColAmt = BigDecimal.ZERO;
	private BigDecimal closureThresholdLimit = BigDecimal.ZERO;
	private String maxFPPCalType;
	private BigDecimal maxFPPAmount = BigDecimal.ZERO;
	private BigDecimal maxFPPPer = BigDecimal.ZERO;
	private String maxFPPCalOn;
	private int ppLockInPeriod;
	private int esLockInPeriod;
	private String minPPCalType;
	private String minPPCalOn;
	private BigDecimal minPPAmount = BigDecimal.ZERO;
	private BigDecimal minPPPercentage = BigDecimal.ZERO;
	private String maxPPCalType;
	private BigDecimal maxPPAmount = BigDecimal.ZERO;
	private BigDecimal maxPPPercentage = BigDecimal.ZERO;
	private String maxPPCalOn;
	private boolean allowAutoRefund;
	private BigDecimal maxAutoRefund = BigDecimal.ZERO;
	private BigDecimal minAutoRefund = BigDecimal.ZERO;
	private String npaRpyHierarchy;
	private BigDecimal odMinAmount = BigDecimal.ZERO;

	public FinanceType() {
		super();
	}

	public FinanceType(String id) {
		super();
		this.setId(id);
	}

	public FinanceType copyEntity() {
		FinanceType entity = new FinanceType();
		entity.setProduct(this.product);
		entity.setFinType(this.finType);
		entity.setFinCategory(this.finCategory);
		entity.setFinCategoryDesc(this.finCategoryDesc);
		entity.setFinTypeDesc(this.finTypeDesc);
		entity.setFinCcy(this.finCcy);
		entity.setFinDaysCalType(this.finDaysCalType);
		entity.setFinDivision(this.finDivision);
		entity.setFinIsGenRef(this.finIsGenRef);
		entity.setFinMaxAmount(this.finMaxAmount);
		entity.setFinMinAmount(this.finMinAmount);
		entity.setFinDftStmtFrq(this.finDftStmtFrq);
		entity.setFinIsAlwMD(this.finIsAlwMD);
		entity.setFinHistRetension(this.finHistRetension);
		entity.setEqualRepayment(this.equalRepayment);
		entity.setFinAssetType(this.finAssetType);
		entity.setFinIsDwPayRequired(this.finIsDwPayRequired);
		entity.setDownPayRule(this.downPayRule);
		entity.setDownPayRuleCode(this.downPayRuleCode);
		entity.setDownPayRuleDesc(this.downPayRuleDesc);
		entity.setFInIsAlwGrace(this.fInIsAlwGrace);
		entity.setFinRateType(this.finRateType);
		entity.setAlwZeroIntAcc(this.alwZeroIntAcc);
		entity.setFinBaseRate(this.finBaseRate);
		entity.setFinSplRate(this.finSplRate);
		entity.setFinMargin(this.finMargin);
		entity.setFinIntRate(this.finIntRate);
		entity.setFInMinRate(this.fInMinRate);
		entity.setFinMaxRate(this.finMaxRate);
		entity.setAlwHybridRate(this.alwHybridRate);
		entity.setFixedRateTenor(this.fixedRateTenor);
		entity.setFinDftIntFrq(this.finDftIntFrq);
		entity.setFinSchdMthd(this.finSchdMthd);
		entity.setFinIsIntCpz(this.finIsIntCpz);
		entity.setFinCpzFrq(this.finCpzFrq);
		entity.setFinIsRvwAlw(this.finIsRvwAlw);
		entity.setFinRvwFrq(this.finRvwFrq);
		entity.setFinRvwRateApplFor(this.finRvwRateApplFor);
		entity.setFinAlwRateChangeAnyDate(this.finAlwRateChangeAnyDate);
		entity.setFinSchCalCodeOnRvw(this.finSchCalCodeOnRvw);
		entity.setFinGrcRateType(this.finGrcRateType);
		entity.setFinGrcBaseRate(this.finGrcBaseRate);
		entity.setFinGrcSplRate(this.finGrcSplRate);
		entity.setFinGrcMargin(this.finGrcMargin);
		entity.setFinGrcIntRate(this.finGrcIntRate);
		entity.setFInGrcMinRate(this.fInGrcMinRate);
		entity.setFinGrcMaxRate(this.finGrcMaxRate);
		entity.setFinGrcDftIntFrq(this.finGrcDftIntFrq);
		entity.setFinIsAlwGrcRepay(this.finIsAlwGrcRepay);
		entity.setFinGrcSchdMthd(this.finGrcSchdMthd);
		entity.setFinGrcIsIntCpz(this.finGrcIsIntCpz);
		entity.setFinGrcCpzFrq(this.finGrcCpzFrq);
		entity.setFinGrcIsRvwAlw(this.finGrcIsRvwAlw);
		entity.setFinGrcRvwFrq(this.finGrcRvwFrq);
		entity.setFinIsIntCpzAtGrcEnd(this.finIsIntCpzAtGrcEnd);
		entity.setFinMinTerm(this.finMinTerm);
		entity.setFinMaxTerm(this.finMaxTerm);
		entity.setFinDftTerms(this.finDftTerms);
		entity.setFinRepayPftOnFrq(this.finRepayPftOnFrq);
		entity.setFinRpyFrq(this.finRpyFrq);
		entity.setFinRepayMethod(this.finRepayMethod);
		entity.setAlwdRpyMethods(this.alwdRpyMethods);
		entity.setFinIsAlwPartialRpy(this.finIsAlwPartialRpy);
		entity.setFinIsAlwDifferment(this.finIsAlwDifferment);
		entity.setFinMaxDifferment(this.finMaxDifferment);
		entity.setAlwPlanDeferment(this.alwPlanDeferment);
		entity.setPlanDeferCount(this.planDeferCount);
		entity.setFinIsAlwEarlyRpy(this.finIsAlwEarlyRpy);
		entity.setFinIsAlwEarlySettle(this.finIsAlwEarlySettle);
		entity.setFinODRpyTries(this.finODRpyTries);
		entity.setFinIsActive(this.finIsActive);
		entity.setFinScheduleOn(this.finScheduleOn);
		entity.setAlwEarlyPayMethods(this.alwEarlyPayMethods);
		entity.setFinGrcScheduleOn(this.finGrcScheduleOn);
		entity.setFinCommitmentReq(this.finCommitmentReq);
		entity.setFinCollateralReq(this.finCollateralReq);
		entity.setCollateralType(this.collateralType);
		entity.setOverrideLimit(this.overrideLimit);
		entity.setLimitRequired(this.limitRequired);
		entity.setFinCommitmentOvrride(this.finCommitmentOvrride);
		entity.setFinCollateralOvrride(this.finCollateralOvrride);
		entity.setPartiallySecured(this.partiallySecured);
		entity.setFinPftUnChanged(this.finPftUnChanged);
		entity.setStartDate(this.startDate);
		entity.setEndDate(this.endDate);
		entity.setManualSchedule(this.manualSchedule);
		entity.setApplyGrcPricing(this.applyGrcPricing);
		entity.setGrcPricingMethod(this.grcPricingMethod);
		entity.setApplyRpyPricing(this.applyRpyPricing);
		entity.setRpyPricingMethod(this.rpyPricingMethod);
		entity.setRpyHierarchy(this.rpyHierarchy);
		entity.setGrcPricingMethodDesc(this.grcPricingMethodDesc);
		entity.setRpyPricingMethodDesc(this.rpyPricingMethodDesc);
		entity.setDroplineOD(this.droplineOD);
		entity.setDroppingMethod(this.droppingMethod);
		entity.setRateChgAnyDay(this.rateChgAnyDay);
		entity.setFrequencyDays(this.frequencyDays);
		entity.setAlwBPI(this.alwBPI);
		entity.setBpiTreatment(this.bpiTreatment);
		entity.setBpiPftDaysBasis(this.bpiPftDaysBasis);
		entity.setPftDueSchOn(this.pftDueSchOn);
		entity.setPlanEMIHAlw(this.planEMIHAlw);
		entity.setalwPlannedEmiInGrc(this.alwPlannedEmiInGrc);
		entity.setPlanEMIHMethod(this.planEMIHMethod);
		entity.setPlanEMIHMaxPerYear(this.planEMIHMaxPerYear);
		entity.setPlanEMIHMax(this.planEMIHMax);
		entity.setPlanEMIHLockPeriod(this.planEMIHLockPeriod);
		entity.setPlanEMICpz(this.planEMICpz);
		entity.setUnPlanEMIHLockPeriod(this.unPlanEMIHLockPeriod);
		entity.setUnPlanEMICpz(this.unPlanEMICpz);
		entity.setAlwReage(this.alwReage);
		entity.setAlwUnPlanEmiHoliday(this.alwUnPlanEmiHoliday);
		entity.setReAgeCpz(this.reAgeCpz);
		entity.setFddLockPeriod(this.fddLockPeriod);
		entity.setMaxUnplannedEmi(this.maxUnplannedEmi);
		entity.setMaxReAgeHolidays(this.maxReAgeHolidays);
		entity.setRoundingMode(this.roundingMode);
		entity.setRoundingTarget(this.roundingTarget);
		entity.setDeveloperFinance(this.developerFinance);
		entity.setAlwMultiPartyDisb(this.alwMultiPartyDisb);
		entity.setTdsApplicable(this.tdsApplicable);
		entity.setTdsAllowToModify(this.tdsAllowToModify);
		entity.setTdsApplicableTo(this.tdsApplicableTo);
		entity.setAddrLine1(this.addrLine1);
		entity.setPastduePftCalMthd(this.pastduePftCalMthd);
		entity.setPastduePftMargin(this.pastduePftMargin);
		entity.setApplyODPenalty(this.applyODPenalty);
		entity.setODIncGrcDays(this.oDIncGrcDays);
		entity.setODChargeType(this.oDChargeType);
		entity.setODGraceDays(this.oDGraceDays);
		entity.setODChargeCalOn(this.oDChargeCalOn);
		entity.setODChargeAmtOrPerc(this.oDChargeAmtOrPerc);
		entity.setODRuleCode(this.oDRuleCode);
		entity.setODAllowWaiver(this.oDAllowWaiver);
		entity.setODMaxWaiverPerc(this.oDMaxWaiverPerc);
		entity.setODMinCapAmount(this.oDMinCapAmount);
		entity.setStepFinance(this.stepFinance);
		entity.setSteppingMandatory(this.steppingMandatory);
		entity.setAlwManualSteps(this.alwManualSteps);
		entity.setAlwdStepPolicies(this.alwdStepPolicies);
		entity.setDftStepPolicy(this.dftStepPolicy);
		entity.setDftStepPolicyType(this.dftStepPolicyType);
		entity.setLovDescDftStepPolicyName(this.lovDescDftStepPolicyName);
		entity.setRemarks(this.remarks);
		if (finTypeVASProductsList != null) {
			entity.setFinTypeVASProductsList(new ArrayList<FinTypeVASProducts>());
			this.finTypeVASProductsList.stream()
					.forEach(e -> entity.getFinTypeVASProductsList().add(e == null ? null : e.copyEntity()));
		}
		this.finTypeReceiptModesList.stream()
				.forEach(e -> entity.getFinTypeReceiptModesList().add(e == null ? null : e.copyEntity()));
		entity.setFinSuspTrigger(this.finSuspTrigger);
		entity.setFinSuspRemarks(this.finSuspRemarks);
		entity.setNewRecord(super.isNewRecord());
		entity.setLovValue(this.lovValue);
		entity.setBefImage(this.befImage == null ? null : this.befImage.copyEntity());
		entity.setAlwMaxDisbCheckReq(this.alwMaxDisbCheckReq);
		entity.setQuickDisb(this.quickDisb);
		entity.setAutoApprove(this.autoApprove);
		entity.setPromotionCode(this.promotionCode);
		entity.setPromotionDesc(this.promotionDesc);
		entity.setProfitCenterID(this.profitCenterID);
		entity.setProfitCenterCode(this.profitCenterCode);
		entity.setProfitCenterDesc(this.profitCenterDesc);
		entity.setLovDescEntityCode(this.lovDescEntityCode);
		entity.setLovDescEntityDesc(this.lovDescEntityDesc);
		entity.setChequeCaptureReq(this.chequeCaptureReq);
		entity.setAutoRejectionDays(this.autoRejectionDays);
		entity.setTaxNoMand(this.taxNoMand);
		entity.setAlwVan(this.alwVan);
		entity.setVanAllocationMethod(this.vanAllocationMethod);
		entity.setAllowDrawingPower(this.allowDrawingPower);
		entity.setAllowRevolving(this.allowRevolving);
		entity.setSanBsdSchdle(this.sanBsdSchdle);
		entity.setAlwSanctionAmt(this.alwSanctionAmt);
		entity.setAlwSanctionAmtOverride(this.alwSanctionAmtOverride);
		entity.setFinIsRateRvwAtGrcEnd(this.finIsRateRvwAtGrcEnd);
		entity.setSchdOnPMTCal(this.schdOnPMTCal);
		entity.setOcrRequired(this.ocrRequired);
		entity.setAllowedOCRS(this.allowedOCRS);
		entity.setDefaultOCR(this.defaultOCR);
		entity.setAllowedLoanPurposes(this.allowedLoanPurposes);
		entity.setSpecificLoanPurposes(this.specificLoanPurposes);
		entity.setAllowCancelFin(this.allowCancelFin);
		entity.setUserDetails(this.userDetails);
		this.auditDetailMap.entrySet().stream().forEach(e -> {
			List<AuditDetail> newList = new ArrayList<AuditDetail>();
			if (e.getValue() != null) {
				e.getValue().forEach(
						auditDetail -> newList.add(auditDetail == null ? null : auditDetail.getNewCopyInstance()));
				entity.getAuditDetailMap().put(e.getKey(), newList);
			} else
				entity.getAuditDetailMap().put(e.getKey(), null);
		});
		entity.setLovDescFinDivisionName(this.lovDescFinDivisionName);
		entity.setLovDescWorkFlowRolesName(this.lovDescWorkFlowRolesName);
		entity.setLovDescWorkFlowTypeName(this.lovDescWorkFlowTypeName);
		entity.setLovDescPromoFinTypeDesc(this.lovDescPromoFinTypeDesc);
		entity.setProductCategory(this.productCategory);
		entity.setPromotionType(this.promotionType);
		this.finTypeAccountingList.stream()
				.forEach(e -> entity.getFinTypeAccountingList().add(e == null ? null : e.copyEntity()));
		this.finTypeAccountingMap.entrySet().stream()
				.forEach(e -> entity.getFinTypeAccountingMap().put(e.getKey(), e.getValue()));
		this.finTypeFeesList.stream().forEach(e -> entity.getFinTypeFeesList().add(e == null ? null : e.copyEntity()));
		this.finTypeExpenseList.stream()
				.forEach(e -> entity.getFinTypeExpenseList().add(e == null ? null : e.copyEntity()));
		entity.setCostOfFunds(this.costOfFunds);
		this.irrFinanceTypeList.stream()
				.forEach(e -> entity.getIrrFinanceTypeList().add(e == null ? null : e.copyEntity()));
		entity.setFinLTVCheck(this.finLTVCheck);
		entity.setEligibilityMethods(this.eligibilityMethods);
		entity.setPutCallRequired(this.putCallRequired);
		entity.setGrcAdvIntersetReq(this.grcAdvIntersetReq);
		entity.setGrcAdvType(this.grcAdvType);
		entity.setGrcAdvMinTerms(this.grcAdvMinTerms);
		entity.setGrcAdvMaxTerms(this.grcAdvMaxTerms);
		entity.setGrcAdvDefaultTerms(this.grcAdvDefaultTerms);
		entity.setAdvIntersetReq(this.advIntersetReq);
		entity.setAdvType(this.advType);
		entity.setAdvMinTerms(this.advMinTerms);
		entity.setAdvMaxTerms(this.advMaxTerms);
		entity.setAdvDefaultTerms(this.advDefaultTerms);
		entity.setAdvStage(this.advStage);
		entity.setDsfReq(this.dsfReq);
		entity.setCashCollateralReq(this.cashCollateralReq);
		entity.setSubventionReq(this.subventionReq);
		entity.setMinGrcTerms(this.minGrcTerms);
		entity.setMaxGrcTerms(this.maxGrcTerms);
		entity.setDefaultGrcTerms(this.defaultGrcTerms);
		entity.setAlwChgGrcTerms(this.alwChgGrcTerms);
		entity.setMinPureTerms(this.minPureTerms);
		entity.setMaxPureTerms(this.maxPureTerms);
		entity.setDefaultPureTerms(this.defaultPureTerms);
		entity.setAlwChgPureTerms(this.alwChgPureTerms);
		entity.setAlwCloBefDUe(this.alwCloBefDUe);
		entity.setFinTypeClassification(this.finTypeClassification);
		entity.setAllowPftBal(this.allowPftBal);
		entity.setGrcAdjReq(this.grcAdjReq);
		entity.setGrcPeriodAftrFullDisb(this.grcPeriodAftrFullDisb);
		entity.setAutoIncrGrcEndDate(this.autoIncrGrcEndDate);
		entity.setGrcAutoIncrMonths(this.grcAutoIncrMonths);
		entity.setMaxAutoIncrAllowed(this.maxAutoIncrAllowed);
		entity.setThrldtoMaintainGrcPrd(this.thrldtoMaintainGrcPrd);
		entity.setAlwLoanSplit(this.alwLoanSplit);
		entity.setSplitLoanType(this.splitLoanType);
		entity.setInstBasedSchd(this.instBasedSchd);
		entity.setTdsType(this.tdsType);
		entity.setCalcOfSteps(this.calcOfSteps);
		entity.setStepsAppliedFor(this.stepsAppliedFor);
		entity.setOverdraftTxnChrgReq(this.overdraftTxnChrgReq);
		entity.setOverdraftTxnChrgFeeType(this.overdraftTxnChrgFeeType);
		entity.setOverDraftExtGraceDays(this.overDraftExtGraceDays);
		entity.setOverDraftColChrgFeeType(this.overDraftColChrgFeeType);
		entity.setOverDraftColAmt(this.overDraftColAmt);
		entity.setRegProvRule(this.regProvRule);
		entity.setIntProvRule(this.intProvRule);
		entity.setFeetype(this.feetype);
		entity.setClosureThresholdLimit(this.closureThresholdLimit);
		entity.setMaxFPPCalType(this.maxFPPCalType);
		entity.setMaxFPPAmount(this.maxFPPAmount);
		entity.setMaxFPPPer(this.maxFPPPer);
		entity.setMaxFPPCalOn(this.maxFPPCalOn);
		entity.setPpLockInPeriod(this.ppLockInPeriod);
		entity.setEsLockInPeriod(this.esLockInPeriod);
		entity.setMinPPCalType(this.minPPCalType);
		entity.setMinPPCalOn(this.minPPCalOn);
		entity.setMinPPAmount(this.minPPAmount);
		entity.setMinPPPercentage(this.minPPPercentage);
		entity.setMaxPPCalType(this.maxPPCalType);
		entity.setMaxPPAmount(this.maxPPAmount);
		entity.setMaxPPPercentage(this.maxPPPercentage);
		entity.setMaxPPCalOn(this.maxPPCalOn);
		entity.setAllowAutoRefund(this.allowAutoRefund);
		entity.setMaxAutoRefund(this.maxAutoRefund);
		entity.setMinAutoRefund(this.minAutoRefund);
		entity.setNpaRpyHierarchy(this.npaRpyHierarchy);
		entity.setAssetClassSetup(this.assetClassSetup);
		entity.setAssetClassSetupCode(this.assetClassSetupCode);
		entity.setAssetClassSetupDesc(this.assetClassSetupDesc);
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
		entity.setOdMinAmount(this.odMinAmount);

		return entity;
	}

	public Set<String> getExcludeFields() {
		Set<String> excludeFields = new HashSet<>();
		excludeFields.add("finTypeAccounts");
		excludeFields.add("finTypeAccountingList");
		excludeFields.add("downPayRuleCode");
		excludeFields.add("downPayRuleDesc");
		excludeFields.add("finSuspTrigger");
		excludeFields.add("finSuspRemarks");
		excludeFields.add("grcPricingMethodDesc");
		excludeFields.add("rpyPricingMethodDesc");
		excludeFields.add("productCategory");
		excludeFields.add("dftStepPolicyType");
		excludeFields.add("finTypeInsurances");
		excludeFields.add("finTypeAccountingMap");
		excludeFields.add("finTypeFeesList");
		excludeFields.add("finTypeVASProductsList");
		excludeFields.add("finTypeReceiptModesList");
		excludeFields.add("addrLine1");
		excludeFields.add("promotionCode");
		excludeFields.add("promotionDesc");
		excludeFields.add("profitCenterCode");
		excludeFields.add("profitCenterDesc");
		excludeFields.add("promotionType");
		excludeFields.add("finCategoryDesc");
		excludeFields.add("finOptionTypeCode");
		excludeFields.add("finOptionTypeDesc");

		// As part of Receipts
		excludeFields.add("minGrcTerms");
		excludeFields.add("maxGrcTerms");
		excludeFields.add("defaultGrcTerms");
		excludeFields.add("alwChgGrcTerms");

		excludeFields.add("minPureTerms");
		excludeFields.add("maxPureTerms");
		excludeFields.add("defaultPureTerms");
		excludeFields.add("alwChgPureTerms");
		excludeFields.add("alwCloBefDUe");
		excludeFields.add("finTypeClassification");
		excludeFields.add("allowPftBal");
		excludeFields.add("alwVan");
		excludeFields.add("vanAllocationMethod");
		excludeFields.add("feetype");
		excludeFields.add("assetClassSetupCode");
		excludeFields.add("assetClassSetupDesc");
		return excludeFields;
	}

	// ******************************************************//
	// ****************** getter / setter *******************//
	// ******************************************************//

	public String getId() {
		return finType;
	}

	public void setId(String id) {
		this.finType = id;
	}

	public String getFinType() {
		return finType;
	}

	public void setFinType(String finType) {
		this.finType = finType;
	}

	public String getProduct() {
		return product;
	}

	public void setProduct(String product) {
		this.product = product;
	}

	public String getFinCategory() {
		return finCategory;
	}

	public void setFinCategory(String finCategory) {
		this.finCategory = finCategory;
	}

	public String getFinTypeDesc() {
		return finTypeDesc;
	}

	public void setFinTypeDesc(String finTypeDesc) {
		this.finTypeDesc = finTypeDesc;
	}

	public String getFinCcy() {
		return finCcy;
	}

	public void setFinCcy(String finCcy) {
		this.finCcy = finCcy;
	}

	public String getFinDivision() {
		return finDivision;
	}

	public void setFinDivision(String finDivision) {
		this.finDivision = finDivision;
	}

	public String getLovDescFinDivisionName() {
		return lovDescFinDivisionName;
	}

	public void setLovDescFinDivisionName(String lovDescFinDivisionName) {
		this.lovDescFinDivisionName = lovDescFinDivisionName;
	}

	public String getFinDaysCalType() {
		return finDaysCalType;
	}

	public void setFinDaysCalType(String finDaysCalType) {
		this.finDaysCalType = finDaysCalType;
	}

	public boolean isFinIsGenRef() {
		return finIsGenRef;
	}

	public void setFinIsGenRef(boolean finIsGenRef) {
		this.finIsGenRef = finIsGenRef;
	}

	public BigDecimal getFinMaxAmount() {
		return finMaxAmount;
	}

	public void setFinMaxAmount(BigDecimal finMaxAmount) {
		this.finMaxAmount = finMaxAmount;
	}

	public BigDecimal getFinMinAmount() {
		return finMinAmount;
	}

	public void setFinMinAmount(BigDecimal finMinAmount) {
		this.finMinAmount = finMinAmount;
	}

	public String getFinDftStmtFrq() {
		return finDftStmtFrq;
	}

	public void setFinDftStmtFrq(String finDftStmtFrq) {
		this.finDftStmtFrq = finDftStmtFrq;
	}

	public boolean isFinIsAlwMD() {
		return finIsAlwMD;
	}

	public void setFinIsAlwMD(boolean finIsAlwMD) {
		this.finIsAlwMD = finIsAlwMD;
	}

	public String getFinSchdMthd() {
		return finSchdMthd;
	}

	public void setFinSchdMthd(String finSchdMthd) {
		this.finSchdMthd = finSchdMthd;
	}

	public boolean isFInIsAlwGrace() {
		return fInIsAlwGrace;
	}

	public void setFInIsAlwGrace(boolean fInIsAlwGrace) {
		this.fInIsAlwGrace = fInIsAlwGrace;
	}

	public int getFinHistRetension() {
		return finHistRetension;
	}

	public void setFinHistRetension(int finHistRetension) {
		this.finHistRetension = finHistRetension;
	}

	public String getFinRateType() {
		return finRateType;
	}

	public void setFinRateType(String finRateType) {
		this.finRateType = finRateType;
	}

	public String getFinBaseRate() {
		return finBaseRate;
	}

	public void setFinBaseRate(String finBaseRate) {
		this.finBaseRate = finBaseRate;
	}

	public String getFinSplRate() {
		return finSplRate;
	}

	public void setFinSplRate(String finSplRate) {
		this.finSplRate = finSplRate;
	}

	public BigDecimal getFinIntRate() {
		return finIntRate;
	}

	public void setFinIntRate(BigDecimal finIntRate) {
		this.finIntRate = finIntRate;
	}

	public BigDecimal getFInMinRate() {
		return fInMinRate;
	}

	public void setFInMinRate(BigDecimal fInMinRate) {
		this.fInMinRate = fInMinRate;
	}

	public BigDecimal getFinMaxRate() {
		return finMaxRate;
	}

	public void setFinMaxRate(BigDecimal finMaxRate) {
		this.finMaxRate = finMaxRate;
	}

	public boolean isAlwHybridRate() {
		return alwHybridRate;
	}

	public void setAlwHybridRate(boolean alwHybridRate) {
		this.alwHybridRate = alwHybridRate;
	}

	public int getFixedRateTenor() {
		return fixedRateTenor;
	}

	public void setFixedRateTenor(int fixedRateTenor) {
		this.fixedRateTenor = fixedRateTenor;
	}

	public String getFinDftIntFrq() {
		return finDftIntFrq;
	}

	public void setFinDftIntFrq(String finDftIntFrq) {
		this.finDftIntFrq = finDftIntFrq;
	}

	public boolean isFinIsIntCpz() {
		return finIsIntCpz;
	}

	public void setFinIsIntCpz(boolean finIsIntCpz) {
		this.finIsIntCpz = finIsIntCpz;
	}

	public String getFinCpzFrq() {
		return finCpzFrq;
	}

	public void setFinCpzFrq(String finCpzFrq) {
		this.finCpzFrq = finCpzFrq;
	}

	public boolean isFinIsRvwAlw() {
		return finIsRvwAlw;
	}

	public void setFinIsRvwAlw(boolean finIsRvwAlw) {
		this.finIsRvwAlw = finIsRvwAlw;
	}

	public String getFinRvwFrq() {
		return finRvwFrq;
	}

	public void setFinRvwFrq(String finRvwFrq) {
		this.finRvwFrq = finRvwFrq;
	}

	public String getFinGrcRateType() {
		return finGrcRateType;
	}

	public void setFinGrcRateType(String finGrcRateType) {
		this.finGrcRateType = finGrcRateType;
	}

	public String getFinGrcBaseRate() {
		return finGrcBaseRate;
	}

	public void setFinGrcBaseRate(String finGrcBaseRate) {
		this.finGrcBaseRate = finGrcBaseRate;
	}

	public String getFinGrcSplRate() {
		return finGrcSplRate;
	}

	public void setFinGrcSplRate(String finGrcSplRate) {
		this.finGrcSplRate = finGrcSplRate;
	}

	public BigDecimal getFinGrcIntRate() {
		return finGrcIntRate;
	}

	public void setFinGrcIntRate(BigDecimal finGrcIntRate) {
		this.finGrcIntRate = finGrcIntRate;
	}

	public BigDecimal getFInGrcMinRate() {
		return fInGrcMinRate;
	}

	public void setFInGrcMinRate(BigDecimal fInGrcMinRate) {
		this.fInGrcMinRate = fInGrcMinRate;
	}

	public BigDecimal getFinGrcMaxRate() {
		return finGrcMaxRate;
	}

	public void setFinGrcMaxRate(BigDecimal finGrcMaxRate) {
		this.finGrcMaxRate = finGrcMaxRate;
	}

	public String getFinGrcDftIntFrq() {
		return finGrcDftIntFrq;
	}

	public void setFinGrcDftIntFrq(String finGrcDftIntFrq) {
		this.finGrcDftIntFrq = finGrcDftIntFrq;
	}

	public boolean isFinGrcIsIntCpz() {
		return finGrcIsIntCpz;
	}

	public void setFinGrcIsIntCpz(boolean finGrcIsIntCpz) {
		this.finGrcIsIntCpz = finGrcIsIntCpz;
	}

	public String getFinGrcCpzFrq() {
		return finGrcCpzFrq;
	}

	public void setFinGrcCpzFrq(String finGrcCpzFrq) {
		this.finGrcCpzFrq = finGrcCpzFrq;
	}

	public boolean isFinGrcIsRvwAlw() {
		return finGrcIsRvwAlw;
	}

	public void setFinGrcIsRvwAlw(boolean finGrcIsRvwAlw) {
		this.finGrcIsRvwAlw = finGrcIsRvwAlw;
	}

	public String getFinGrcRvwFrq() {
		return finGrcRvwFrq;
	}

	public void setFinGrcRvwFrq(String finGrcRvwFrq) {
		this.finGrcRvwFrq = finGrcRvwFrq;
	}

	public int getFinMinTerm() {
		return finMinTerm;
	}

	public void setFinMinTerm(int finMinTerm) {
		this.finMinTerm = finMinTerm;
	}

	public int getFinMaxTerm() {
		return finMaxTerm;
	}

	public void setFinMaxTerm(int finMaxTerm) {
		this.finMaxTerm = finMaxTerm;
	}

	public int getFinDftTerms() {
		return finDftTerms;
	}

	public void setFinDftTerms(int finDftTerms) {
		this.finDftTerms = finDftTerms;
	}

	public String getFinRpyFrq() {
		return finRpyFrq;
	}

	public void setFinRpyFrq(String finRpyFrq) {
		this.finRpyFrq = finRpyFrq;
	}

	public String getFinRepayMethod() {
		return finRepayMethod;
	}

	public void setFinRepayMethod(String finRepayMethod) {
		this.finRepayMethod = finRepayMethod;
	}

	public boolean isFinIsAlwPartialRpy() {
		return finIsAlwPartialRpy;
	}

	public void setFinIsAlwPartialRpy(boolean finIsAlwPartialRpy) {
		this.finIsAlwPartialRpy = finIsAlwPartialRpy;
	}

	public boolean isFinIsAlwDifferment() {
		return finIsAlwDifferment;
	}

	public void setFinIsAlwDifferment(boolean finIsAlwDifferment) {
		this.finIsAlwDifferment = finIsAlwDifferment;
	}

	public boolean isFinIsAlwEarlyRpy() {
		return finIsAlwEarlyRpy;
	}

	public void setFinIsAlwEarlyRpy(boolean finIsAlwEarlyRpy) {
		this.finIsAlwEarlyRpy = finIsAlwEarlyRpy;
	}

	public boolean isFinIsAlwEarlySettle() {
		return finIsAlwEarlySettle;
	}

	public void setFinIsAlwEarlySettle(boolean finIsAlwEarlySettle) {
		this.finIsAlwEarlySettle = finIsAlwEarlySettle;
	}

	public int getFinODRpyTries() {
		return finODRpyTries;
	}

	public void setFinODRpyTries(int finODRpyTries) {
		this.finODRpyTries = finODRpyTries;
	}

	public boolean isFinIsActive() {
		return finIsActive;
	}

	public void setFinIsActive(boolean finIsActive) {
		this.finIsActive = finIsActive;
	}

	public String getRemarks() {
		return remarks;
	}

	public void setRemarks(String remarks) {
		this.remarks = remarks;
	}

	public String getLovValue() {
		return lovValue;
	}

	public void setLovValue(String lovValue) {
		this.lovValue = lovValue;
	}

	public FinanceType getBefImage() {
		return this.befImage;
	}

	public void setBefImage(FinanceType beforeImage) {
		this.befImage = beforeImage;
	}

	public LoggedInUser getUserDetails() {
		return userDetails;
	}

	public void setUserDetails(LoggedInUser userDetails) {
		this.userDetails = userDetails;
	}

	/**
	 * @param finFrEqrepayment the finFrEqrepayment to set
	 */
	public void setEqualRepayment(boolean equalRepayment) {
		this.equalRepayment = equalRepayment;
	}

	/** @return the finFrEqrepayment */
	public boolean isEqualRepayment() {
		return equalRepayment;
	}

	/**
	 * @param finIsDwPayRequired the finIsDwPayRequired to set
	 */
	public void setFinIsDwPayRequired(boolean finIsDwPayRequired) {
		this.finIsDwPayRequired = finIsDwPayRequired;
	}

	/** @return the finIsDwPayRequired */
	public boolean isFinIsDwPayRequired() {
		return finIsDwPayRequired;
	}

	public String getFinRvwRateApplFor() {
		return finRvwRateApplFor;
	}

	public void setFinRvwRateApplFor(String finRvwRateApplFor) {
		this.finRvwRateApplFor = finRvwRateApplFor;
	}

	public boolean isFinAlwRateChangeAnyDate() {
		return finAlwRateChangeAnyDate;
	}

	public void setFinAlwRateChangeAnyDate(boolean finAlwRateChangeAnyDate) {
		this.finAlwRateChangeAnyDate = finAlwRateChangeAnyDate;
	}

	public long getDownPayRule() {
		return downPayRule;
	}

	public void setDownPayRule(long downPayRule) {
		this.downPayRule = downPayRule;
	}

	public String getDownPayRuleDesc() {
		return downPayRuleDesc;
	}

	public void setDownPayRuleDesc(String downPayRuleDesc) {
		this.downPayRuleDesc = downPayRuleDesc;
	}

	public void setFinIsIntCpzAtGrcEnd(boolean finIsIntCpzAtGrcEnd) {
		this.finIsIntCpzAtGrcEnd = finIsIntCpzAtGrcEnd;
	}

	public boolean isFinIsIntCpzAtGrcEnd() {
		return finIsIntCpzAtGrcEnd;
	}

	public String getFinSchCalCodeOnRvw() {
		return finSchCalCodeOnRvw;
	}

	public void setFinSchCalCodeOnRvw(String finSchCalCodeOnRvw) {
		this.finSchCalCodeOnRvw = finSchCalCodeOnRvw;
	}

	public void setFinMaxDifferment(int finMaxDifferment) {
		this.finMaxDifferment = finMaxDifferment;
	}

	public int getFinMaxDifferment() {
		return finMaxDifferment;
	}

	public boolean isAlwPlanDeferment() {
		return alwPlanDeferment;
	}

	public void setAlwPlanDeferment(boolean alwPlanDeferment) {
		this.alwPlanDeferment = alwPlanDeferment;
	}

	public int getPlanDeferCount() {
		return planDeferCount;
	}

	public void setPlanDeferCount(int planDeferCount) {
		this.planDeferCount = planDeferCount;
	}

	public String getLovDescWorkFlowRolesName() {
		return lovDescWorkFlowRolesName;
	}

	public void setLovDescWorkFlowRolesName(String lovDescWorkFlowRolesName) {
		this.lovDescWorkFlowRolesName = lovDescWorkFlowRolesName;
	}

	public String getLovDescWorkFlowTypeName() {
		return lovDescWorkFlowTypeName;
	}

	public void setLovDescWorkFlowTypeName(String lovDescWorkFlowTypeName) {
		this.lovDescWorkFlowTypeName = lovDescWorkFlowTypeName;
	}

	public Map<String, List<AuditDetail>> getAuditDetailMap() {
		return auditDetailMap;
	}

	public void setAuditDetailMap(Map<String, List<AuditDetail>> auditDetailMap) {
		this.auditDetailMap = auditDetailMap;
	}

	public boolean isFinIsAlwGrcRepay() {
		return finIsAlwGrcRepay;
	}

	public void setFinIsAlwGrcRepay(boolean finIsAlwGrcRepay) {
		this.finIsAlwGrcRepay = finIsAlwGrcRepay;
	}

	public String getFinGrcSchdMthd() {
		return finGrcSchdMthd;
	}

	public void setFinGrcSchdMthd(String finGrcSchdMthd) {
		this.finGrcSchdMthd = finGrcSchdMthd;
	}

	public BigDecimal getFinMargin() {
		return finMargin;
	}

	public void setFinMargin(BigDecimal finMargin) {
		this.finMargin = finMargin;
	}

	public BigDecimal getFinGrcMargin() {
		return finGrcMargin;
	}

	public void setFinGrcMargin(BigDecimal finGrcMargin) {
		this.finGrcMargin = finGrcMargin;
	}

	public void setFinGrcScheduleOn(String finGrcScheduleOn) {
		this.finGrcScheduleOn = finGrcScheduleOn;
	}

	public String getFinGrcScheduleOn() {
		return finGrcScheduleOn;
	}

	public void setFinScheduleOn(String finScheduleOn) {
		this.finScheduleOn = finScheduleOn;
	}

	public String getFinScheduleOn() {
		return finScheduleOn;
	}

	public String getAlwEarlyPayMethods() {
		return alwEarlyPayMethods;
	}

	public void setAlwEarlyPayMethods(String alwEarlyPayMethods) {
		this.alwEarlyPayMethods = alwEarlyPayMethods;
	}

	public void setFinCommitmentReq(boolean finCommitmentReq) {
		this.finCommitmentReq = finCommitmentReq;
	}

	public boolean isFinCommitmentReq() {
		return finCommitmentReq;
	}

	public void setFinCollateralReq(boolean finCollateralReq) {
		this.finCollateralReq = finCollateralReq;
	}

	public boolean isFinCollateralReq() {
		return finCollateralReq;
	}

	public String getCollateralType() {
		return collateralType;
	}

	public void setCollateralType(String collateralType) {
		this.collateralType = collateralType;
	}

	public void setFinCommitmentOvrride(boolean finCommitmentOvrride) {
		this.finCommitmentOvrride = finCommitmentOvrride;
	}

	public boolean isFinCommitmentOvrride() {
		return finCommitmentOvrride;
	}

	public void setFinCollateralOvrride(boolean finCollateralOvrride) {
		this.finCollateralOvrride = finCollateralOvrride;
	}

	public boolean isFinCollateralOvrride() {
		return finCollateralOvrride;
	}

	public void setOverrideLimit(boolean overrideLimit) {
		this.overrideLimit = overrideLimit;
	}

	public boolean isOverrideLimit() {
		return overrideLimit;
	}

	public void setLimitRequired(boolean limitRequired) {
		this.limitRequired = limitRequired;
	}

	public boolean isLimitRequired() {
		return limitRequired;
	}

	public void setFinRepayPftOnFrq(boolean finRepayPftOnFrq) {
		this.finRepayPftOnFrq = finRepayPftOnFrq;
	}

	public boolean isFinRepayPftOnFrq() {
		return finRepayPftOnFrq;
	}

	public void setFinPftUnChanged(boolean finPftUnChanged) {
		this.finPftUnChanged = finPftUnChanged;
	}

	public boolean isFinPftUnChanged() {
		return finPftUnChanged;
	}

	public boolean isApplyODPenalty() {
		return applyODPenalty;
	}

	public void setApplyODPenalty(boolean applyODPenalty) {
		this.applyODPenalty = applyODPenalty;
	}

	public boolean isODIncGrcDays() {
		return oDIncGrcDays;
	}

	public void setODIncGrcDays(boolean oDIncGrcDays) {
		this.oDIncGrcDays = oDIncGrcDays;
	}

	public String getODChargeType() {
		return oDChargeType;
	}

	public void setODChargeType(String oDChargeType) {
		this.oDChargeType = oDChargeType;
	}

	public int getODGraceDays() {
		return oDGraceDays;
	}

	public void setODGraceDays(int oDGraceDays) {
		this.oDGraceDays = oDGraceDays;
	}

	public String getODChargeCalOn() {
		return oDChargeCalOn;
	}

	public void setODChargeCalOn(String oDChargeCalOn) {
		this.oDChargeCalOn = oDChargeCalOn;
	}

	public BigDecimal getODChargeAmtOrPerc() {
		return oDChargeAmtOrPerc;
	}

	public void setODChargeAmtOrPerc(BigDecimal oDChargeAmtOrPerc) {
		this.oDChargeAmtOrPerc = oDChargeAmtOrPerc;
	}

	public boolean isODAllowWaiver() {
		return oDAllowWaiver;
	}

	public void setODAllowWaiver(boolean oDAllowWaiver) {
		this.oDAllowWaiver = oDAllowWaiver;
	}

	public BigDecimal getODMaxWaiverPerc() {
		return oDMaxWaiverPerc;
	}

	public void setODMaxWaiverPerc(BigDecimal oDMaxWaiverPerc) {
		this.oDMaxWaiverPerc = oDMaxWaiverPerc;
	}

	public BigDecimal getODMinCapAmount() {
		return oDMinCapAmount;
	}

	public void setODMinCapAmount(BigDecimal oDMinCapAmount) {
		this.oDMinCapAmount = oDMinCapAmount;
	}

	public List<FinTypeFees> getFinTypeFeesList() {
		return finTypeFeesList;
	}

	public void setFinTypeFeesList(List<FinTypeFees> finTypeFeesList) {
		this.finTypeFeesList = finTypeFeesList;
	}

	public boolean isStepFinance() {
		return stepFinance;
	}

	public void setStepFinance(boolean stepFinance) {
		this.stepFinance = stepFinance;
	}

	public boolean isSteppingMandatory() {
		return steppingMandatory;
	}

	public void setSteppingMandatory(boolean steppingMandatory) {
		this.steppingMandatory = steppingMandatory;
	}

	public boolean isAlwManualSteps() {
		return alwManualSteps;
	}

	public void setAlwManualSteps(boolean alwManualSteps) {
		this.alwManualSteps = alwManualSteps;
	}

	public String getAlwdStepPolicies() {
		return alwdStepPolicies;
	}

	public void setAlwdStepPolicies(String alwdStepPolicies) {
		this.alwdStepPolicies = alwdStepPolicies;
	}

	public String getDftStepPolicy() {
		return dftStepPolicy;
	}

	public void setDftStepPolicy(String dftStepPolicy) {
		this.dftStepPolicy = dftStepPolicy;
	}

	public String getLovDescDftStepPolicyName() {
		return lovDescDftStepPolicyName;
	}

	public void setLovDescDftStepPolicyName(String lovDescDftStepPolicyName) {
		this.lovDescDftStepPolicyName = lovDescDftStepPolicyName;
	}

	public Date getStartDate() {
		return startDate;
	}

	public void setStartDate(Date startDate) {
		this.startDate = startDate;
	}

	public Date getEndDate() {
		return endDate;
	}

	public void setEndDate(Date endDate) {
		this.endDate = endDate;
	}

	public String getPastduePftCalMthd() {
		return pastduePftCalMthd;
	}

	public void setPastduePftCalMthd(String pastduePftCalMthd) {
		this.pastduePftCalMthd = pastduePftCalMthd;
	}

	public BigDecimal getPastduePftMargin() {
		return pastduePftMargin;
	}

	public void setPastduePftMargin(BigDecimal pastduePftMargin) {
		this.pastduePftMargin = pastduePftMargin;
	}

	public String getLovDescPromoFinTypeDesc() {
		return lovDescPromoFinTypeDesc;
	}

	public void setLovDescPromoFinTypeDesc(String lovDescPromoFinTypeDesc) {
		this.lovDescPromoFinTypeDesc = lovDescPromoFinTypeDesc;
	}

	public String getFinAssetType() {
		return finAssetType;
	}

	public void setFinAssetType(String finAssetType) {
		this.finAssetType = finAssetType;
	}

	public boolean isAlwMultiPartyDisb() {
		return alwMultiPartyDisb;
	}

	public void setAlwMultiPartyDisb(boolean alwMultiPartyDisb) {
		this.alwMultiPartyDisb = alwMultiPartyDisb;
	}

	public Map<String, Object> getDeclaredFieldValues() {
		Map<String, Object> financeTypeMap = new HashMap<String, Object>();
		getDeclaredFieldValues(financeTypeMap);
		return financeTypeMap;
	}

	public void getDeclaredFieldValues(Map<String, Object> financeTypeMap) {
		for (int i = 0; i < this.getClass().getDeclaredFields().length; i++) {
			try {
				// "ft_" Should be in small case only, if we want to change the case we need to update the configuration
				// fields as well.
				financeTypeMap.put("ft_" + this.getClass().getDeclaredFields()[i].getName(),
						this.getClass().getDeclaredFields()[i].get(this));
			} catch (SecurityException | IllegalArgumentException | IllegalAccessException e) {
				// Nothing TO DO
			}
		}
	}

	public String getFinSuspTrigger() {
		return finSuspTrigger;
	}

	public void setFinSuspTrigger(String finSuspTrigger) {
		this.finSuspTrigger = finSuspTrigger;
	}

	public String getFinSuspRemarks() {
		return finSuspRemarks;
	}

	public void setFinSuspRemarks(String finSuspRemarks) {
		this.finSuspRemarks = finSuspRemarks;
	}

	public boolean isTdsApplicable() {
		return tdsApplicable;
	}

	public void setTdsApplicable(boolean tdsApplicable) {
		this.tdsApplicable = tdsApplicable;
	}

	public boolean isApplyGrcPricing() {
		return applyGrcPricing;
	}

	public void setApplyGrcPricing(boolean applyGrcPricing) {
		this.applyGrcPricing = applyGrcPricing;
	}

	public long getGrcPricingMethod() {
		return grcPricingMethod;
	}

	public void setGrcPricingMethod(long grcPricingMethod) {
		this.grcPricingMethod = grcPricingMethod;
	}

	public boolean isApplyRpyPricing() {
		return applyRpyPricing;
	}

	public void setApplyRpyPricing(boolean applyRpyPricing) {
		this.applyRpyPricing = applyRpyPricing;
	}

	public long getRpyPricingMethod() {
		return rpyPricingMethod;
	}

	public void setRpyPricingMethod(long rpyPricingMethod) {
		this.rpyPricingMethod = rpyPricingMethod;
	}

	public String getRpyHierarchy() {
		return rpyHierarchy;
	}

	public void setRpyHierarchy(String rpyHierarchy) {
		this.rpyHierarchy = rpyHierarchy;
	}

	public String getGrcPricingMethodDesc() {
		return grcPricingMethodDesc;
	}

	public void setGrcPricingMethodDesc(String grcPricingMethodDesc) {
		this.grcPricingMethodDesc = grcPricingMethodDesc;
	}

	public String getRpyPricingMethodDesc() {
		return rpyPricingMethodDesc;
	}

	public void setRpyPricingMethodDesc(String rpyPricingMethodDesc) {
		this.rpyPricingMethodDesc = rpyPricingMethodDesc;
	}

	public boolean isDroplineOD() {
		return droplineOD;
	}

	public void setDroplineOD(boolean droplineOD) {
		this.droplineOD = droplineOD;
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

	public String getProductCategory() {
		return productCategory;
	}

	public void setProductCategory(String productCategory) {
		this.productCategory = productCategory;
	}

	public String getDftStepPolicyType() {
		return dftStepPolicyType;
	}

	public void setDftStepPolicyType(String dftStepPolicyType) {
		this.dftStepPolicyType = dftStepPolicyType;
	}

	public String getPftDueSchOn() {
		return pftDueSchOn;
	}

	public void setPftDueSchOn(String pftDueSchOn) {
		this.pftDueSchOn = pftDueSchOn;
	}

	public boolean isManualSchedule() {
		return manualSchedule;
	}

	public void setManualSchedule(boolean manualSchedule) {
		this.manualSchedule = manualSchedule;
	}

	public List<FinTypeAccounting> getFinTypeAccountingList() {
		return finTypeAccountingList;
	}

	public void setFinTypeAccountingList(List<FinTypeAccounting> finTypeAccountingList) {
		this.finTypeAccountingList = finTypeAccountingList;
		finTypeAccountingMap.clear();
		if (finTypeAccountingList != null) {
			for (FinTypeAccounting finTypeAcc : finTypeAccountingList) {
				finTypeAccountingMap.put(finTypeAcc.getEvent(), finTypeAcc.getAccountSetID());
			}
		}
	}

	public Long getAccEventValue(String eventCode) {
		if (finTypeAccountingMap.get(eventCode) == null) {
			return Long.MIN_VALUE;
		} else {
			return finTypeAccountingMap.get(eventCode);
		}
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

	public int getPlanEMIHLockPeriod() {
		return planEMIHLockPeriod;
	}

	public void setPlanEMIHLockPeriod(int planEMIHLockPeriod) {
		this.planEMIHLockPeriod = planEMIHLockPeriod;
	}

	public boolean isPlanEMICpz() {
		return planEMICpz;
	}

	public void setPlanEMICpz(boolean planEMICpz) {
		this.planEMICpz = planEMICpz;
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

	public int getFddLockPeriod() {
		return fddLockPeriod;
	}

	public void setFddLockPeriod(int fddLockPeriod) {
		this.fddLockPeriod = fddLockPeriod;
	}

	public String getAlwdRpyMethods() {
		return alwdRpyMethods;
	}

	public void setAlwdRpyMethods(String alwdRpyMethods) {
		this.alwdRpyMethods = alwdRpyMethods;
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

	public String getRoundingMode() {
		return roundingMode;
	}

	public void setRoundingMode(String roundingMode) {
		this.roundingMode = roundingMode;
	}

	public String getFrequencyDays() {
		return frequencyDays;
	}

	public void setFrequencyDays(String frequencyDays) {
		this.frequencyDays = frequencyDays;
	}

	public List<FinTypeVASProducts> getFinTypeVASProductsList() {
		return finTypeVASProductsList;
	}

	public void setFinTypeVASProductsList(List<FinTypeVASProducts> finTypeVASProductsList) {
		this.finTypeVASProductsList = finTypeVASProductsList;
	}

	public boolean isAlwReage() {
		return alwReage;
	}

	public void setAlwReage(boolean alwReage) {
		this.alwReage = alwReage;
	}

	public boolean isAlwUnPlanEmiHoliday() {
		return alwUnPlanEmiHoliday;
	}

	public void setAlwUnPlanEmiHoliday(boolean alwUnPlanEmiHoliday) {
		this.alwUnPlanEmiHoliday = alwUnPlanEmiHoliday;
	}

	public String getAddrLine1() {
		return addrLine1;
	}

	public void setAddrLine1(String addrLine1) {
		this.addrLine1 = addrLine1;
	}

	public boolean isAlwMaxDisbCheckReq() {
		return alwMaxDisbCheckReq;
	}

	public void setAlwMaxDisbCheckReq(boolean alwMaxDisbCheckReq) {
		this.alwMaxDisbCheckReq = alwMaxDisbCheckReq;
	}

	public boolean isQuickDisb() {
		return quickDisb;
	}

	public void setQuickDisb(boolean quickDisb) {
		this.quickDisb = quickDisb;
	}

	public boolean isAutoApprove() {
		return autoApprove;
	}

	public void setAutoApprove(boolean autoApprove) {
		this.autoApprove = autoApprove;
	}

	public String getPromotionCode() {
		return promotionCode;
	}

	public void setPromotionCode(String promotionCode) {
		this.promotionCode = promotionCode;
	}

	/**
	 * Copy the Promotion Details to Finance Type
	 * 
	 * @param promotion
	 */
	public void setFInTypeFromPromotiion(Promotion promotion) {
		setPromotionCode(promotion.getPromotionCode());
		setPromotionDesc(promotion.getPromotionDesc());
		setFinIsDwPayRequired(promotion.isFinIsDwPayRequired());
		setDownPayRule(promotion.getDownPayRule());
		if (promotion.getActualInterestRate() != null
				&& promotion.getActualInterestRate().compareTo(BigDecimal.ZERO) != 0) {
			setFinIntRate(promotion.getActualInterestRate());
		} else if (promotion.getFinBaseRate() != null) {
			setFinBaseRate(promotion.getFinBaseRate());
			setFinSplRate(promotion.getFinSplRate());
			setFinMargin(promotion.getFinMargin());
		}

		setApplyRpyPricing(promotion.isApplyRpyPricing());
		setRpyPricingMethod(promotion.getRpyPricingMethod());

		setFinMinTerm(promotion.getFinMinTerm());
		setFinMaxTerm(promotion.getFinMaxTerm());
		setFinMinAmount(promotion.getFinMinAmount());
		setFinMaxAmount(promotion.getFinMaxAmount());
		setFInMinRate(promotion.getFinMinRate());
		setFinMaxRate(promotion.getFinMaxRate());
		setFinCcy(promotion.getFinCcy());
	}

	public String getPromotionDesc() {
		return promotionDesc;
	}

	public void setPromotionDesc(String promotionDesc) {
		this.promotionDesc = promotionDesc;
	}

	public List<FinTypePartnerBank> getFinTypePartnerBankList() {
		return finTypePartnerBankList;
	}

	public void setFinTypePartnerBankList(List<FinTypePartnerBank> finTypePartnerBankList) {
		this.finTypePartnerBankList = finTypePartnerBankList;
	}

	public String getProfitCenterCode() {
		return profitCenterCode;
	}

	public void setProfitCenterCode(String profitcenterCode) {
		this.profitCenterCode = profitcenterCode;
	}

	public String getProfitCenterDesc() {
		return profitCenterDesc;
	}

	public void setProfitCenterDesc(String profitCenterDesc) {
		this.profitCenterDesc = profitCenterDesc;
	}

	public long getProfitCenterID() {
		return profitCenterID;
	}

	public void setProfitCenterID(long profitCenterID) {
		this.profitCenterID = profitCenterID;
	}

	public boolean isPromotionType() {
		return promotionType;
	}

	public void setPromotionType(boolean promotionType) {
		this.promotionType = promotionType;
	}

	public int getRoundingTarget() {
		return roundingTarget;
	}

	public void setRoundingTarget(int roundingTarget) {
		this.roundingTarget = roundingTarget;
	}

	public String getFinCategoryDesc() {
		return finCategoryDesc;
	}

	public void setFinCategoryDesc(String finCategoryDesc) {
		this.finCategoryDesc = finCategoryDesc;
	}

	public boolean isDeveloperFinance() {
		return developerFinance;
	}

	public void setDeveloperFinance(boolean developerFinance) {
		this.developerFinance = developerFinance;
	}

	public List<IRRFinanceType> getIrrFinanceTypeList() {
		return irrFinanceTypeList;
	}

	public void setIrrFinanceTypeList(List<IRRFinanceType> irrFinanceTypeList) {
		this.irrFinanceTypeList = irrFinanceTypeList;
	}

	public List<FinTypeExpense> getFinTypeExpenseList() {
		return finTypeExpenseList;
	}

	public void setFinTypeExpenseList(List<FinTypeExpense> finTypeExpenseList) {
		this.finTypeExpenseList = finTypeExpenseList;
	}

	public String getLovDescEntityCode() {
		return lovDescEntityCode;
	}

	public void setLovDescEntityCode(String lovDescEntityCode) {
		this.lovDescEntityCode = lovDescEntityCode;
	}

	public String getLovDescEntityDesc() {
		return lovDescEntityDesc;
	}

	public void setLovDescEntityDesc(String lovDescEntityDesc) {
		this.lovDescEntityDesc = lovDescEntityDesc;
	}

	public String getCostOfFunds() {
		return costOfFunds;
	}

	public void setCostOfFunds(String costOfFunds) {
		this.costOfFunds = costOfFunds;
	}

	public boolean isChequeCaptureReq() {
		return chequeCaptureReq;
	}

	public void setChequeCaptureReq(boolean chequeCaptureReq) {
		this.chequeCaptureReq = chequeCaptureReq;
	}

	public String getFinLTVCheck() {
		return finLTVCheck;
	}

	public void setFinLTVCheck(String finLTVCheck) {
		this.finLTVCheck = finLTVCheck;
	}

	public boolean isPartiallySecured() {
		return partiallySecured;
	}

	public void setPartiallySecured(boolean partiallySecured) {
		this.partiallySecured = partiallySecured;
	}

	public String getEligibilityMethods() {
		return eligibilityMethods;
	}

	public void setEligibilityMethods(String eligibilityMethods) {
		this.eligibilityMethods = eligibilityMethods;
	}

	public String getBpiPftDaysBasis() {
		return bpiPftDaysBasis;
	}

	public void setBpiPftDaysBasis(String bpiPftDaysBasis) {
		this.bpiPftDaysBasis = bpiPftDaysBasis;
	}

	public String getODRuleCode() {
		return oDRuleCode;
	}

	public void setODRuleCode(String ODRuleCode) {
		this.oDRuleCode = ODRuleCode;
	}

	public boolean isAlwZeroIntAcc() {
		return alwZeroIntAcc;
	}

	public void setAlwZeroIntAcc(boolean alwZeroIntAcc) {
		this.alwZeroIntAcc = alwZeroIntAcc;
	}

	public List<FinTypeReceiptModes> getFinTypeReceiptModesList() {
		return finTypeReceiptModesList;
	}

	public void setFinTypeReceiptModesList(List<FinTypeReceiptModes> finTypeReceiptModesList) {
		this.finTypeReceiptModesList = finTypeReceiptModesList;
	}

	public int getAutoRejectionDays() {
		return autoRejectionDays;
	}

	public void setAutoRejectionDays(int autoRejectionDays) {
		this.autoRejectionDays = autoRejectionDays;
	}

	public boolean isTaxNoMand() {
		return this.taxNoMand;
	}

	public void setTaxNoMand(boolean taxNoMand) {
		this.taxNoMand = taxNoMand;
	}

	public boolean isPutCallRequired() {
		return putCallRequired;
	}

	public void setPutCallRequired(boolean putCallRequired) {
		this.putCallRequired = putCallRequired;
	}

	public boolean isAdvIntersetReq() {
		return advIntersetReq;
	}

	public void setAdvIntersetReq(boolean advIntersetReq) {
		this.advIntersetReq = advIntersetReq;
	}

	public String getAdvType() {
		return advType;
	}

	public void setAdvType(String advType) {
		this.advType = advType;
	}

	public int getAdvMinTerms() {
		return advMinTerms;
	}

	public void setAdvMinTerms(int advMinTerms) {
		this.advMinTerms = advMinTerms;
	}

	public int getAdvMaxTerms() {
		return advMaxTerms;
	}

	public void setAdvMaxTerms(int advMaxTerms) {
		this.advMaxTerms = advMaxTerms;
	}

	public int getAdvDefaultTerms() {
		return advDefaultTerms;
	}

	public void setAdvDefaultTerms(int advDefaultTerms) {
		this.advDefaultTerms = advDefaultTerms;
	}

	public boolean isGrcAdvIntersetReq() {
		return grcAdvIntersetReq;
	}

	public void setGrcAdvIntersetReq(boolean grcAdvIntersetReq) {
		this.grcAdvIntersetReq = grcAdvIntersetReq;
	}

	public String getGrcAdvType() {
		return grcAdvType;
	}

	public void setGrcAdvType(String grcAdvType) {
		this.grcAdvType = grcAdvType;
	}

	public int getGrcAdvMaxTerms() {
		return grcAdvMaxTerms;
	}

	public void setGrcAdvMaxTerms(int grcAdvMaxTerms) {
		this.grcAdvMaxTerms = grcAdvMaxTerms;
	}

	public int getGrcAdvDefaultTerms() {
		return grcAdvDefaultTerms;
	}

	public void setGrcAdvDefaultTerms(int grcAdvDefaultTerms) {
		this.grcAdvDefaultTerms = grcAdvDefaultTerms;
	}

	public String getAdvStage() {
		return advStage;
	}

	public void setAdvStage(String advStage) {
		this.advStage = advStage;
	}

	public boolean isDsfReq() {
		return dsfReq;
	}

	public void setDsfReq(boolean dsfReq) {
		this.dsfReq = dsfReq;
	}

	public boolean isCashCollateralReq() {
		return cashCollateralReq;
	}

	public void setCashCollateralReq(boolean cashCollateralReq) {
		this.cashCollateralReq = cashCollateralReq;
	}

	public boolean isSubventionReq() {
		return subventionReq;
	}

	public void setSubventionReq(boolean subventionReq) {
		this.subventionReq = subventionReq;
	}

	public int getGrcAdvMinTerms() {
		return grcAdvMinTerms;
	}

	public void setGrcAdvMinTerms(int grcAdvMinTerms) {
		this.grcAdvMinTerms = grcAdvMinTerms;
	}

	public int getMinGrcTerms() {
		return minGrcTerms;
	}

	public void setMinGrcTerms(int minGrcTerms) {
		this.minGrcTerms = minGrcTerms;
	}

	public int getMaxGrcTerms() {
		return maxGrcTerms;
	}

	public void setMaxGrcTerms(int maxGrcTerms) {
		this.maxGrcTerms = maxGrcTerms;
	}

	public int getDefaultGrcTerms() {
		return defaultGrcTerms;
	}

	public void setDefaultGrcTerms(int defaultGrcTerms) {
		this.defaultGrcTerms = defaultGrcTerms;
	}

	public boolean isAlwChgGrcTerms() {
		return alwChgGrcTerms;
	}

	public void setAlwChgGrcTerms(boolean alwChgGrcTerms) {
		this.alwChgGrcTerms = alwChgGrcTerms;
	}

	public int getMinPureTerms() {
		return minPureTerms;
	}

	public void setMinPureTerms(int minPureTerms) {
		this.minPureTerms = minPureTerms;
	}

	public int getMaxPureTerms() {
		return maxPureTerms;
	}

	public void setMaxPureTerms(int maxPureTerms) {
		this.maxPureTerms = maxPureTerms;
	}

	public int getDefaultPureTerms() {
		return defaultPureTerms;
	}

	public void setDefaultPureTerms(int defaultPureTerms) {
		this.defaultPureTerms = defaultPureTerms;
	}

	public boolean isAlwChgPureTerms() {
		return alwChgPureTerms;
	}

	public void setAlwChgPureTerms(boolean alwChgPureTerms) {
		this.alwChgPureTerms = alwChgPureTerms;
	}

	public boolean isAlwCloBefDUe() {
		return alwCloBefDUe;
	}

	public void setAlwCloBefDUe(boolean alwCloBefDUe) {
		this.alwCloBefDUe = alwCloBefDUe;
	}

	public String getFinTypeClassification() {
		return finTypeClassification;
	}

	public void setFinTypeClassification(String finTypeClassification) {
		this.finTypeClassification = finTypeClassification;
	}

	public boolean isAllowPftBal() {
		return allowPftBal;
	}

	public void setAllowPftBal(boolean allowPftBal) {
		this.allowPftBal = allowPftBal;
	}

	public String getDownPayRuleCode() {
		return downPayRuleCode;
	}

	public void setDownPayRuleCode(String downPayRuleCode) {
		this.downPayRuleCode = downPayRuleCode;
	}

	public String getTdsApplicableTo() {
		return tdsApplicableTo;
	}

	public void setTdsApplicableTo(String tdsApplicableTo) {
		this.tdsApplicableTo = tdsApplicableTo;
	}

	public boolean isTdsAllowToModify() {
		return tdsAllowToModify;
	}

	public void setTdsAllowToModify(boolean tdsAllowToModify) {
		this.tdsAllowToModify = tdsAllowToModify;
	}

	public boolean isAlwVan() {
		return alwVan;
	}

	public void setAlwVan(boolean alwVan) {
		this.alwVan = alwVan;
	}

	public String getVanAllocationMethod() {
		return vanAllocationMethod;
	}

	public void setVanAllocationMethod(String vanAllocationMethod) {
		this.vanAllocationMethod = vanAllocationMethod;
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

	public boolean isAlwSanctionAmt() {
		return alwSanctionAmt;
	}

	public void setAlwSanctionAmt(boolean alwSanctionAmt) {
		this.alwSanctionAmt = alwSanctionAmt;
	}

	public boolean isAlwSanctionAmtOverride() {
		return alwSanctionAmtOverride;
	}

	public void setAlwSanctionAmtOverride(boolean alwSanctionAmtOverride) {
		this.alwSanctionAmtOverride = alwSanctionAmtOverride;
	}

	public boolean isSanBsdSchdle() {
		return sanBsdSchdle;
	}

	public void setSanBsdSchdle(boolean sanBsdSchdle) {
		this.sanBsdSchdle = sanBsdSchdle;
	}

	public boolean isFinIsRateRvwAtGrcEnd() {
		return finIsRateRvwAtGrcEnd;
	}

	public void setFinIsRateRvwAtGrcEnd(boolean finIsRateRvwAtGrcEnd) {
		this.finIsRateRvwAtGrcEnd = finIsRateRvwAtGrcEnd;
	}

	public boolean isSchdOnPMTCal() {
		return schdOnPMTCal;
	}

	public void setSchdOnPMTCal(boolean schdOnPMTCal) {
		this.schdOnPMTCal = schdOnPMTCal;
	}

	public boolean isOcrRequired() {
		return ocrRequired;
	}

	public void setOcrRequired(boolean ocrRequired) {
		this.ocrRequired = ocrRequired;
	}

	public String getAllowedOCRS() {
		return allowedOCRS;
	}

	public void setAllowedOCRS(String allowedOCRS) {
		this.allowedOCRS = allowedOCRS;
	}

	public String getDefaultOCR() {
		return defaultOCR;
	}

	public void setDefaultOCR(String defaultOCR) {
		this.defaultOCR = defaultOCR;
	}

	public String getAllowedLoanPurposes() {
		return allowedLoanPurposes;
	}

	public void setAllowedLoanPurposes(String allowedLoanPurposes) {
		this.allowedLoanPurposes = allowedLoanPurposes;
	}

	public String getSpecificLoanPurposes() {
		return specificLoanPurposes;
	}

	public void setSpecificLoanPurposes(String specificLoanPurposes) {
		this.specificLoanPurposes = specificLoanPurposes;
	}

	public boolean isGrcAdjReq() {
		return grcAdjReq;
	}

	public void setGrcAdjReq(boolean grcAdjReq) {
		this.grcAdjReq = grcAdjReq;
	}

	public boolean isGrcPeriodAftrFullDisb() {
		return grcPeriodAftrFullDisb;
	}

	public void setGrcPeriodAftrFullDisb(boolean grcPeriodAftrFullDisb) {
		this.grcPeriodAftrFullDisb = grcPeriodAftrFullDisb;
	}

	public boolean isAutoIncrGrcEndDate() {
		return autoIncrGrcEndDate;
	}

	public void setAutoIncrGrcEndDate(boolean autoIncrGrcEndDate) {
		this.autoIncrGrcEndDate = autoIncrGrcEndDate;
	}

	public Map<String, Long> getFinTypeAccountingMap() {
		return finTypeAccountingMap;
	}

	public void setFinTypeAccountingMap(Map<String, Long> finTypeAccountingMap) {
		this.finTypeAccountingMap = finTypeAccountingMap;
	}

	public int getGrcAutoIncrMonths() {
		return grcAutoIncrMonths;
	}

	public void setGrcAutoIncrMonths(int grcAutoIncrMonths) {
		this.grcAutoIncrMonths = grcAutoIncrMonths;
	}

	public int getMaxAutoIncrAllowed() {
		return maxAutoIncrAllowed;
	}

	public void setMaxAutoIncrAllowed(int maxAutoIncrAllowed) {
		this.maxAutoIncrAllowed = maxAutoIncrAllowed;
	}

	public int getThrldtoMaintainGrcPrd() {
		return thrldtoMaintainGrcPrd;
	}

	public void setThrldtoMaintainGrcPrd(int thrldtoMaintainGrcPrd) {
		this.thrldtoMaintainGrcPrd = thrldtoMaintainGrcPrd;
	}

	public boolean isalwPlannedEmiInGrc() {
		return alwPlannedEmiInGrc;
	}

	public void setalwPlannedEmiInGrc(boolean alwPlannedEmiInGrc) {
		this.alwPlannedEmiInGrc = alwPlannedEmiInGrc;
	}

	public boolean isAlwLoanSplit() {
		return alwLoanSplit;
	}

	public void setAlwLoanSplit(boolean alwLoanSplit) {
		this.alwLoanSplit = alwLoanSplit;
	}

	public String getSplitLoanType() {
		return splitLoanType;
	}

	public void setSplitLoanType(String splitLoanType) {
		this.splitLoanType = splitLoanType;
	}

	public boolean isInstBasedSchd() {
		return instBasedSchd;
	}

	public void setInstBasedSchd(boolean instBasedSchd) {
		this.instBasedSchd = instBasedSchd;
	}

	public String getTdsType() {
		return tdsType;
	}

	public void setTdsType(String tdsType) {
		this.tdsType = tdsType;
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

	public FeeType getFeetype() {
		return feetype;
	}

	public void setFeetype(FeeType feetype) {
		this.feetype = feetype;
	}

	public BigDecimal getOverDraftColAmt() {
		return overDraftColAmt;
	}

	public void setOverDraftColAmt(BigDecimal overDraftColAmt) {
		this.overDraftColAmt = overDraftColAmt;
	}

	public int getOverDraftExtGraceDays() {
		return overDraftExtGraceDays;
	}

	public void setOverDraftExtGraceDays(int overDraftExtGraceDays) {
		this.overDraftExtGraceDays = overDraftExtGraceDays;
	}

	public long getOverDraftColChrgFeeType() {
		return overDraftColChrgFeeType;
	}

	public void setOverDraftColChrgFeeType(long overDraftColChrgFeeType) {
		this.overDraftColChrgFeeType = overDraftColChrgFeeType;
	}

	public Long getRegProvRule() {
		return regProvRule;
	}

	public void setRegProvRule(Long regProvRule) {
		this.regProvRule = regProvRule;
	}

	public Long getIntProvRule() {
		return intProvRule;
	}

	public void setIntProvRule(Long intProvRule) {
		this.intProvRule = intProvRule;
	}

	public BigDecimal getClosureThresholdLimit() {
		return closureThresholdLimit;
	}

	public void setClosureThresholdLimit(BigDecimal closureThresholdLimit) {
		this.closureThresholdLimit = closureThresholdLimit;
	}

	public String getMaxFPPCalType() {
		return maxFPPCalType;
	}

	public BigDecimal getMaxFPPAmount() {
		return maxFPPAmount;
	}

	public BigDecimal getMaxFPPPer() {
		return maxFPPPer;
	}

	public String getMaxFPPCalOn() {
		return maxFPPCalOn;
	}

	public int getPpLockInPeriod() {
		return ppLockInPeriod;
	}

	public int getEsLockInPeriod() {
		return esLockInPeriod;
	}

	public String getMinPPCalType() {
		return minPPCalType;
	}

	public String getMinPPCalOn() {
		return minPPCalOn;
	}

	public BigDecimal getMinPPAmount() {
		return minPPAmount;
	}

	public BigDecimal getMinPPPercentage() {
		return minPPPercentage;
	}

	public String getMaxPPCalType() {
		return maxPPCalType;
	}

	public BigDecimal getMaxPPAmount() {
		return maxPPAmount;
	}

	public BigDecimal getMaxPPPercentage() {
		return maxPPPercentage;
	}

	public String getMaxPPCalOn() {
		return maxPPCalOn;
	}

	public void setMaxFPPCalType(String maxFPPCalType) {
		this.maxFPPCalType = maxFPPCalType;
	}

	public void setMaxFPPAmount(BigDecimal maxFPPAmount) {
		this.maxFPPAmount = maxFPPAmount;
	}

	public void setMaxFPPPer(BigDecimal maxFPPPer) {
		this.maxFPPPer = maxFPPPer;
	}

	public void setMaxFPPCalOn(String maxFPPCalOn) {
		this.maxFPPCalOn = maxFPPCalOn;
	}

	public void setPpLockInPeriod(int ppLockInPeriod) {
		this.ppLockInPeriod = ppLockInPeriod;
	}

	public void setEsLockInPeriod(int esLockInPeriod) {
		this.esLockInPeriod = esLockInPeriod;
	}

	public void setMinPPCalType(String minPPCalType) {
		this.minPPCalType = minPPCalType;
	}

	public void setMinPPCalOn(String minPPCalOn) {
		this.minPPCalOn = minPPCalOn;
	}

	public void setMinPPAmount(BigDecimal minPPAmount) {
		this.minPPAmount = minPPAmount;
	}

	public void setMinPPPercentage(BigDecimal minPPPercentage) {
		this.minPPPercentage = minPPPercentage;
	}

	public void setMaxPPCalType(String maxPPCalType) {
		this.maxPPCalType = maxPPCalType;
	}

	public void setMaxPPAmount(BigDecimal maxPPAmount) {
		this.maxPPAmount = maxPPAmount;
	}

	public void setMaxPPPercentage(BigDecimal maxPPPercentage) {
		this.maxPPPercentage = maxPPPercentage;
	}

	public void setMaxPPCalOn(String maxPPCalOn) {
		this.maxPPCalOn = maxPPCalOn;
	}

	public boolean isAllowAutoRefund() {
		return allowAutoRefund;
	}

	public void setAllowAutoRefund(boolean AllowAutoRefund) {
		this.allowAutoRefund = AllowAutoRefund;
	}

	public BigDecimal getMaxAutoRefund() {
		return maxAutoRefund;
	}

	public void setMaxAutoRefund(BigDecimal maxAutoRefund) {
		this.maxAutoRefund = maxAutoRefund;
	}

	public BigDecimal getMinAutoRefund() {
		return minAutoRefund;
	}

	public void setMinAutoRefund(BigDecimal minAutoRefund) {
		this.minAutoRefund = minAutoRefund;
	}

	public String getNpaRpyHierarchy() {
		return npaRpyHierarchy;
	}

	public void setNpaRpyHierarchy(String npaRpyHierarchy) {
		this.npaRpyHierarchy = npaRpyHierarchy;
	}

	public long getAssetClassSetup() {
		return assetClassSetup;
	}

	public void setAssetClassSetup(long assetClassSetup) {
		this.assetClassSetup = assetClassSetup;
	}

	public String getAssetClassSetupCode() {
		return assetClassSetupCode;
	}

	public void setAssetClassSetupCode(String assetClassSetupCode) {
		this.assetClassSetupCode = assetClassSetupCode;
	}

	public String getAssetClassSetupDesc() {
		return assetClassSetupDesc;
	}

	public void setAssetClassSetupDesc(String assetClassSetupDesc) {
		this.assetClassSetupDesc = assetClassSetupDesc;
	}

	public BigDecimal getOdMinAmount() {
		return odMinAmount;
	}

	public void setOdMinAmount(BigDecimal odMinAmount) {
		this.odMinAmount = odMinAmount;
	}

	public boolean isAllowCancelFin() {
		return allowCancelFin;
	}

	public void setAllowCancelFin(boolean allowCancelFin) {
		this.allowCancelFin = allowCancelFin;
	}
}