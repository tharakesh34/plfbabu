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
 * * FileName : FinanceTypeDAOImpl.java * * Author : PENNANT TECHONOLOGIES * * Creation Date : 30-06-2011 * * Modified
 * Date : 30-06-2011 * * Description : * *
 ********************************************************************************************
 * Date Author Version Comments *
 ********************************************************************************************
 * 30-06-2011 Pennant 0.1 * * * * * * * * *
 ********************************************************************************************
 */

package com.pennant.backend.dao.rmtmasters.impl;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;

import com.pennant.backend.dao.rmtmasters.FinanceTypeDAO;
import com.pennant.backend.model.rmtmasters.FinanceType;
import com.pennant.backend.util.PennantJavaUtil;
import com.pennanttech.pennapps.core.ConcurrencyException;
import com.pennanttech.pennapps.core.DependencyFoundException;
import com.pennanttech.pennapps.core.jdbc.BasicDao;
import com.pennanttech.pennapps.core.jdbc.JdbcUtil;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pennapps.core.resource.Message;

/**
 * DAO methods implementation for the <b>FinanceType model</b> class.<br>
 * 
 */
public class FinanceTypeDAOImpl extends BasicDao<FinanceType> implements FinanceTypeDAO {
	private static Logger logger = LogManager.getLogger(FinanceTypeDAOImpl.class);

	public FinanceTypeDAOImpl() {
		super();
	}

	/**
	 * Fetch the Record Finance Types details by key field
	 * 
	 * @param id   (String)
	 * @param type (String) ""/_Temp/_View
	 * @return FinanceType
	 */
	@Override
	public FinanceType getFinanceTypeByID(final String id, String type) {
		StringBuilder sql = new StringBuilder("Select");
		sql.append(" FinType, Product, FinCategory, FinTypeDesc, FinCcy, FinDaysCalType");
		sql.append(", FinIsGenRef, FinMaxAmount, FinMinAmount, FinDftStmtFrq, FinIsAlwMD");
		sql.append(", FinSchdMthd, FInIsAlwGrace, FinHistRetension, EqualRepayment, SchdOnPMTCal, FinRateType");
		sql.append(", FinBaseRate, FinSplRate, FinIntRate, FInMinRate, FinMaxRate, FinDftIntFrq, FinIsIntCpz");
		sql.append(", FinCpzFrq, FinIsRvwAlw, FinRvwFrq, FinGrcRateType, FinGrcBaseRate, FinGrcSplRate");
		sql.append(", FinGrcIntRate, FInGrcMinRate, FinGrcMaxRate, FinGrcDftIntFrq, FinGrcIsIntCpz");
		sql.append(", FinGrcCpzFrq, FinGrcIsRvwAlw, FinGrcRvwFrq, FinMinTerm, FinMaxTerm, FinDftTerms");
		sql.append(", FinRpyFrq, FinRepayMethod, FinIsAlwPartialRpy, FinIsAlwDifferment, FinMaxDifferment");
		sql.append(", FinIsAlwEarlyRpy, FinIsAlwEarlySettle, FinODRpyTries, AlwPlanDeferment, PlanDeferCount");
		sql.append(", FinIsDwPayRequired, FinRvwRateApplFor, FinAlwRateChangeAnyDate, FinIsIntCpzAtGrcEnd");
		sql.append(", FinIsRateRvwAtGrcEnd, FinSchCalCodeOnRvw, FinAssetType");
		sql.append(", FinIsActive ");
		sql.append(", FinGrcSchdMthd, FinIsAlwGrcRepay, FinMargin, FinGrcMargin, FinScheduleOn, FinGrcScheduleOn");
		sql.append(", FinCommitmentReq, FinCollateralReq");
		sql.append(", OverrideLimit, LimitRequired, FinCommitmentOvrride, FinCollateralOvrride, FinRepayPftOnFrq");
		sql.append(", FinPftUnChanged, ManualSchedule, ApplyODPenalty, ODIncGrcDays, ODChargeType");
		sql.append(", ODGraceDays, ODChargeCalOn, ODChargeAmtOrPerc, ODAllowWaiver, ODMaxWaiverPerc");
		sql.append(", ODMinCapAmount, ODMinCapAmount, FinDivision, StepFinance, SteppingMandatory");
		sql.append(", AlwManualSteps, AlwdStepPolicies, DftStepPolicy, StartDate, EndDate");
		sql.append(", Remarks, AlwEarlyPayMethods, PastduePftCalMthd, PastduePftMargin");
		sql.append(", DownPayRule, FinSuspTrigger, FinSuspRemarks, AlwMultiPartyDisb");
		sql.append(", TdsApplicable, CollateralType, ApplyGrcPricing, GrcPricingMethod, ApplyRpyPricing");
		sql.append(", RpyPricingMethod, RpyHierarchy, NpaRpyHierarchy, DroplineOD");
		sql.append(", DroppingMethod, RateChgAnyDay, AlwBPI");
		sql.append(", BpiTreatment, PftDueSchOn, PlanEMIHAlw, AlwPlannedEmiInGrc, PlanEMIHMethod, PlanEMIHMaxPerYear");
		sql.append(", PlanEMIHMax, PlanEMIHLockPeriod, PlanEMICpz, UnPlanEMIHLockPeriod, UnPlanEMICpz");
		sql.append(", ReAgeCpz, FddLockPeriod, AlwdRpyMethods, AlwReage, AlwUnPlanEmiHoliday, MaxUnplannedEmi");
		sql.append(", MaxReAgeHolidays, RoundingMode, RoundingTarget, FrequencyDays, AlwMaxDisbCheckReq");
		sql.append(", QuickDisb, AutoApprove, ProfitCenterID, ProductCategory, DeveloperFinance, CostOfFunds");
		sql.append(", ChequeCaptureReq, FinLTVCheck, PartiallySecured, BpiPftDaysBasis, AlwHybridRate");
		sql.append(", FixedRateTenor, EligibilityMethods, ODRuleCode, AlwZeroIntAcc, AutoRejectionDays");
		sql.append(", TaxNoMand, PutCallRequired, GrcAdvIntersetReq, GrcAdvType, GrcAdvMinTerms, GrcAdvMaxTerms");
		sql.append(", GrcAdvDefaultTerms, WriteOffRepayHry, MatureRepayHry, PresentmentRepayHry");
		sql.append(", AdvIntersetReq, AdvType, AdvMaxTerms, AdvMinTerms, AdvDefaultTerms");
		sql.append(", AdvStage, DsfReq, CashCollateralReq, TdsAllowToModify, TdsApplicableTo, AlwVan, SubventionReq");
		sql.append(", VanAllocationMethod, AllowDrawingPower, AllowRevolving, AlwSanctionAmt");
		sql.append(", AlwSanctionAmtOverride, SanBsdSchdle");
		sql.append(", OcrRequired, AllowedOCRS, DefaultOCR, AllowedLoanPurposes, SpecificLoanPurposes");
		sql.append(", GrcAdjReq, GrcPeriodAftrFullDisb, AutoIncrGrcEndDate, GrcAutoIncrMonths");
		sql.append(", MaxAutoIncrAllowed, AlwLoanSplit, SplitLoanType, TdsType, CalcOfSteps, StepsAppliedFor");
		sql.append(", IntProvRule, RegProvRule, OverdraftTxnChrgReq, OverdraftTxnChrgFeeType, OverDraftExtGraceDays");
		sql.append(", OverDraftColChrgFeeType, OverDraftColAmt");
		sql.append(", ClosureThresholdLimit");
		sql.append(", MaxFPPCalType, MaxFPPAmount, MaxFPPPer, MaxFPPCalOn");
		sql.append(", PpLockInPeriod, EsLockInPeriod, MinPPCalType, MinPPCalOn");
		sql.append(", MinPPAmount, MinPPPercentage, MaxPPCalType, MaxPPAmount, MaxPPPercentage, MaxPPCalOn");
		sql.append(", AllowAutoRefund, MaxAutoRefund, MinAutoRefund, AssetClassSetup, ODMinAmount, AllowCancelFin");

		if (StringUtils.trimToEmpty(type).contains("View")) {
			sql.append(", FinCategoryDesc, DownPayRuleCode, DownPayRuleDesc ");
			sql.append(", LovDescFinDivisionName");
			sql.append(", LovDescPromoFinTypeDesc, ProfitCenterCode, ProfitCenterDesc");
			sql.append(", LovDescEntityCode, AssetClassSetupCode, AssetClassSetupDesc");
		}

		sql.append(", Version, LastMntBy, LastMntOn, RecordStatus");
		sql.append(", RoleCode, NextRoleCode, TaskId, NextTaskId, RecordType, WorkflowId");
		sql.append(", ThrldtoMaintainGrcPrd, InstBasedSchd ");
		sql.append(" From RMTFinanceTypes");
		sql.append(StringUtils.trimToEmpty(type));
		sql.append(" Where FinType = ?");

		logger.trace(Literal.SQL.concat(sql.toString()));

		try {
			return this.jdbcOperations.queryForObject(sql.toString(), (rs, rowNum) -> {
				FinanceType ft = new FinanceType();

				ft.setFinType(rs.getString("FinType"));
				ft.setProduct(rs.getString("Product"));
				ft.setFinCategory(rs.getString("FinCategory"));
				ft.setFinTypeDesc(rs.getString("FinTypeDesc"));
				ft.setFinCcy(rs.getString("FinCcy"));
				ft.setFinDaysCalType(rs.getString("FinDaysCalType"));
				ft.setFinIsGenRef(rs.getBoolean("FinIsGenRef"));
				ft.setFinMaxAmount(rs.getBigDecimal("FinMaxAmount"));
				ft.setFinMinAmount(rs.getBigDecimal("FinMinAmount"));
				ft.setFinDftStmtFrq(rs.getString("FinDftStmtFrq"));
				ft.setFinIsAlwMD(rs.getBoolean("FinIsAlwMD"));
				ft.setFinSchdMthd(rs.getString("FinSchdMthd"));
				ft.setFInIsAlwGrace(rs.getBoolean("FInIsAlwGrace"));
				ft.setFinHistRetension(rs.getInt("FinHistRetension"));
				ft.setEqualRepayment(rs.getBoolean("EqualRepayment"));
				ft.setSchdOnPMTCal(rs.getBoolean("SchdOnPMTCal"));
				ft.setFinRateType(rs.getString("FinRateType"));
				ft.setFinBaseRate(rs.getString("FinBaseRate"));
				ft.setFinSplRate(rs.getString("FinSplRate"));
				ft.setFinIntRate(rs.getBigDecimal("FinIntRate"));
				ft.setFInMinRate(rs.getBigDecimal("FInMinRate"));
				ft.setFinMaxRate(rs.getBigDecimal("FinMaxRate"));
				ft.setFinDftIntFrq(rs.getString("FinDftIntFrq"));
				ft.setFinIsIntCpz(rs.getBoolean("FinIsIntCpz"));
				ft.setFinCpzFrq(rs.getString("FinCpzFrq"));
				ft.setFinIsRvwAlw(rs.getBoolean("FinIsRvwAlw"));
				ft.setFinRvwFrq(rs.getString("FinRvwFrq"));
				ft.setFinGrcRateType(rs.getString("FinGrcRateType"));
				ft.setFinGrcBaseRate(rs.getString("FinGrcBaseRate"));
				ft.setFinGrcSplRate(rs.getString("FinGrcSplRate"));
				ft.setFinGrcIntRate(rs.getBigDecimal("FinGrcIntRate"));
				ft.setFInGrcMinRate(rs.getBigDecimal("FInGrcMinRate"));
				ft.setFinGrcMaxRate(rs.getBigDecimal("FinGrcMaxRate"));
				ft.setFinGrcDftIntFrq(rs.getString("FinGrcDftIntFrq"));
				ft.setFinGrcIsIntCpz(rs.getBoolean("FinGrcIsIntCpz"));
				ft.setFinGrcCpzFrq(rs.getString("FinGrcCpzFrq"));
				ft.setFinGrcIsRvwAlw(rs.getBoolean("FinGrcIsRvwAlw"));
				ft.setFinGrcRvwFrq(rs.getString("FinGrcRvwFrq"));
				ft.setFinMinTerm(rs.getInt("FinMinTerm"));
				ft.setFinMaxTerm(rs.getInt("FinMaxTerm"));
				ft.setFinDftTerms(rs.getInt("FinDftTerms"));
				ft.setFinRpyFrq(rs.getString("FinRpyFrq"));
				ft.setFinRepayMethod(rs.getString("FinRepayMethod"));
				ft.setFinIsAlwPartialRpy(rs.getBoolean("FinIsAlwPartialRpy"));
				ft.setFinIsAlwDifferment(rs.getBoolean("FinIsAlwDifferment"));
				ft.setFinMaxDifferment(rs.getInt("FinMaxDifferment"));
				ft.setFinIsAlwEarlyRpy(rs.getBoolean("FinIsAlwEarlyRpy"));
				ft.setFinIsAlwEarlySettle(rs.getBoolean("FinIsAlwEarlySettle"));
				ft.setFinODRpyTries(rs.getInt("FinODRpyTries"));
				ft.setAlwPlanDeferment(rs.getBoolean("AlwPlanDeferment"));
				ft.setPlanDeferCount(rs.getInt("PlanDeferCount"));
				ft.setFinIsDwPayRequired(rs.getBoolean("FinIsDwPayRequired"));
				ft.setFinRvwRateApplFor(rs.getString("FinRvwRateApplFor"));
				ft.setFinAlwRateChangeAnyDate(rs.getBoolean("FinAlwRateChangeAnyDate"));
				ft.setFinIsIntCpzAtGrcEnd(rs.getBoolean("FinIsIntCpzAtGrcEnd"));
				ft.setFinIsRateRvwAtGrcEnd(rs.getBoolean("FinIsRateRvwAtGrcEnd"));
				ft.setFinSchCalCodeOnRvw(rs.getString("FinSchCalCodeOnRvw"));
				ft.setFinAssetType(rs.getString("FinAssetType"));
				ft.setFinIsActive(rs.getBoolean("FinIsActive"));
				ft.setFinGrcSchdMthd(rs.getString("FinGrcSchdMthd"));
				ft.setFinIsAlwGrcRepay(rs.getBoolean("FinIsAlwGrcRepay"));
				ft.setFinMargin(rs.getBigDecimal("FinMargin"));
				ft.setFinGrcMargin(rs.getBigDecimal("FinGrcMargin"));
				ft.setFinScheduleOn(rs.getString("FinScheduleOn"));
				ft.setFinGrcScheduleOn(rs.getString("FinGrcScheduleOn"));
				ft.setFinCommitmentReq(rs.getBoolean("FinCommitmentReq"));
				ft.setFinCollateralReq(rs.getBoolean("FinCollateralReq"));
				ft.setOverrideLimit(rs.getBoolean("OverrideLimit"));
				ft.setLimitRequired(rs.getBoolean("LimitRequired"));
				ft.setFinCommitmentOvrride(rs.getBoolean("FinCommitmentOvrride"));
				ft.setFinCollateralOvrride(rs.getBoolean("FinCollateralOvrride"));
				ft.setFinRepayPftOnFrq(rs.getBoolean("FinRepayPftOnFrq"));
				ft.setFinPftUnChanged(rs.getBoolean("FinPftUnChanged"));
				ft.setManualSchedule(rs.getBoolean("ManualSchedule"));
				ft.setApplyODPenalty(rs.getBoolean("ApplyODPenalty"));
				ft.setODIncGrcDays(rs.getBoolean("ODIncGrcDays"));
				ft.setODChargeType(rs.getString("ODChargeType"));
				ft.setODGraceDays(rs.getInt("ODGraceDays"));
				ft.setODChargeCalOn(rs.getString("ODChargeCalOn"));
				ft.setODChargeAmtOrPerc(rs.getBigDecimal("ODChargeAmtOrPerc"));
				ft.setODAllowWaiver(rs.getBoolean("ODAllowWaiver"));
				ft.setODMaxWaiverPerc(rs.getBigDecimal("ODMaxWaiverPerc"));
				ft.setODMinCapAmount(rs.getBigDecimal("ODMinCapAmount"));
				ft.setFinDivision(rs.getString("FinDivision"));
				ft.setStepFinance(rs.getBoolean("StepFinance"));
				ft.setSteppingMandatory(rs.getBoolean("SteppingMandatory"));
				ft.setAlwManualSteps(rs.getBoolean("AlwManualSteps"));
				ft.setAlwdStepPolicies(rs.getString("AlwdStepPolicies"));
				ft.setDftStepPolicy(rs.getString("DftStepPolicy"));
				ft.setStartDate(rs.getTimestamp("StartDate"));
				ft.setEndDate(rs.getTimestamp("EndDate"));
				ft.setRemarks(rs.getString("Remarks"));
				ft.setAlwEarlyPayMethods(rs.getString("AlwEarlyPayMethods"));
				ft.setPastduePftCalMthd(rs.getString("PastduePftCalMthd"));
				ft.setPastduePftMargin(rs.getBigDecimal("PastduePftMargin"));
				ft.setDownPayRule(rs.getLong("DownPayRule"));
				ft.setFinSuspTrigger(rs.getString("FinSuspTrigger"));
				ft.setFinSuspRemarks(rs.getString("FinSuspRemarks"));
				ft.setAlwMultiPartyDisb(rs.getBoolean("AlwMultiPartyDisb"));
				ft.setTdsApplicable(rs.getBoolean("TdsApplicable"));
				ft.setCollateralType(rs.getString("CollateralType"));
				ft.setApplyGrcPricing(rs.getBoolean("ApplyGrcPricing"));
				ft.setGrcPricingMethod(rs.getLong("GrcPricingMethod"));
				ft.setApplyRpyPricing(rs.getBoolean("ApplyRpyPricing"));
				ft.setRpyPricingMethod(rs.getLong("RpyPricingMethod"));
				ft.setRpyHierarchy(rs.getString("RpyHierarchy"));
				ft.setNpaRpyHierarchy(rs.getString("NpaRpyHierarchy"));
				ft.setDroplineOD(rs.getBoolean("DroplineOD"));
				ft.setDroppingMethod(rs.getString("DroppingMethod"));
				ft.setRateChgAnyDay(rs.getBoolean("RateChgAnyDay"));
				ft.setAlwBPI(rs.getBoolean("AlwBPI"));
				ft.setBpiTreatment(rs.getString("BpiTreatment"));
				ft.setPftDueSchOn(rs.getString("PftDueSchOn"));
				ft.setPlanEMIHAlw(rs.getBoolean("PlanEMIHAlw"));
				ft.setalwPlannedEmiInGrc(rs.getBoolean("AlwPlannedEmiInGrc"));
				ft.setPlanEMIHMethod(rs.getString("PlanEMIHMethod"));
				ft.setPlanEMIHMaxPerYear(rs.getInt("PlanEMIHMaxPerYear"));
				ft.setPlanEMIHMax(rs.getInt("PlanEMIHMax"));
				ft.setPlanEMIHLockPeriod(rs.getInt("PlanEMIHLockPeriod"));
				ft.setPlanEMICpz(rs.getBoolean("PlanEMICpz"));
				ft.setUnPlanEMIHLockPeriod(rs.getInt("UnPlanEMIHLockPeriod"));
				ft.setUnPlanEMICpz(rs.getBoolean("UnPlanEMICpz"));
				ft.setReAgeCpz(rs.getBoolean("ReAgeCpz"));
				ft.setFddLockPeriod(rs.getInt("FddLockPeriod"));
				ft.setAlwdRpyMethods(rs.getString("AlwdRpyMethods"));
				ft.setAlwReage(rs.getBoolean("AlwReage"));
				ft.setAlwUnPlanEmiHoliday(rs.getBoolean("AlwUnPlanEmiHoliday"));
				ft.setMaxUnplannedEmi(rs.getInt("MaxUnplannedEmi"));
				ft.setMaxReAgeHolidays(rs.getInt("MaxReAgeHolidays"));
				ft.setRoundingMode(rs.getString("RoundingMode"));
				ft.setRoundingTarget(rs.getInt("RoundingTarget"));
				ft.setFrequencyDays(rs.getString("FrequencyDays"));
				ft.setAlwMaxDisbCheckReq(rs.getBoolean("AlwMaxDisbCheckReq"));
				ft.setQuickDisb(rs.getBoolean("QuickDisb"));
				ft.setAutoApprove(rs.getBoolean("AutoApprove"));
				ft.setProfitCenterID(rs.getLong("ProfitCenterID"));
				ft.setProductCategory(rs.getString("ProductCategory"));
				ft.setDeveloperFinance(rs.getBoolean("DeveloperFinance"));
				ft.setCostOfFunds(rs.getString("CostOfFunds"));
				ft.setChequeCaptureReq(rs.getBoolean("ChequeCaptureReq"));
				ft.setFinLTVCheck(rs.getString("FinLTVCheck"));
				ft.setPartiallySecured(rs.getBoolean("PartiallySecured"));
				ft.setBpiPftDaysBasis(rs.getString("BpiPftDaysBasis"));
				ft.setAlwHybridRate(rs.getBoolean("AlwHybridRate"));
				ft.setFixedRateTenor(rs.getInt("FixedRateTenor"));
				ft.setEligibilityMethods(rs.getString("EligibilityMethods"));
				ft.setODRuleCode(rs.getString("ODRuleCode"));
				ft.setAlwZeroIntAcc(rs.getBoolean("AlwZeroIntAcc"));
				ft.setAutoRejectionDays(rs.getInt("AutoRejectionDays"));
				ft.setTaxNoMand(rs.getBoolean("TaxNoMand"));
				ft.setPutCallRequired(rs.getBoolean("PutCallRequired"));
				ft.setGrcAdvIntersetReq(rs.getBoolean("GrcAdvIntersetReq"));
				ft.setGrcAdvType(rs.getString("GrcAdvType"));
				ft.setGrcAdvMinTerms(rs.getInt("GrcAdvMinTerms"));
				ft.setGrcAdvMaxTerms(rs.getInt("GrcAdvMaxTerms"));
				ft.setGrcAdvDefaultTerms(rs.getInt("GrcAdvDefaultTerms"));
				ft.setWriteOffRepayHry(rs.getString("WriteOffRepayHry"));
				ft.setPresentmentRepayHry(rs.getString("PresentmentRepayHry"));
				ft.setMatureRepayHry(rs.getString("MatureRepayHry"));
				ft.setAdvIntersetReq(rs.getBoolean("AdvIntersetReq"));
				ft.setAdvType(rs.getString("AdvType"));
				ft.setAdvMaxTerms(rs.getInt("AdvMaxTerms"));
				ft.setAdvMinTerms(rs.getInt("AdvMinTerms"));
				ft.setAdvDefaultTerms(rs.getInt("AdvDefaultTerms"));
				ft.setAdvStage(rs.getString("AdvStage"));
				ft.setDsfReq(rs.getBoolean("DsfReq"));
				ft.setCashCollateralReq(rs.getBoolean("CashCollateralReq"));
				ft.setTdsAllowToModify(rs.getBoolean("TdsAllowToModify"));
				ft.setTdsApplicableTo(rs.getString("TdsApplicableTo"));
				ft.setAlwVan(rs.getBoolean("AlwVan"));
				ft.setSubventionReq(rs.getBoolean("SubventionReq"));
				ft.setVanAllocationMethod(rs.getString("VanAllocationMethod"));
				ft.setAllowDrawingPower(rs.getBoolean("AllowDrawingPower"));
				ft.setAllowRevolving(rs.getBoolean("AllowRevolving"));
				ft.setAlwSanctionAmt(rs.getBoolean("AlwSanctionAmt"));
				ft.setAlwSanctionAmtOverride(rs.getBoolean("AlwSanctionAmtOverride"));
				ft.setSanBsdSchdle(rs.getBoolean("SanBsdSchdle"));
				ft.setAllowedLoanPurposes(rs.getString("AllowedLoanPurposes"));
				ft.setSpecificLoanPurposes(rs.getString("SpecificLoanPurposes"));
				ft.setAlwLoanSplit(rs.getBoolean("AlwLoanSplit"));
				ft.setSplitLoanType(rs.getString("SplitLoanType"));
				ft.setInstBasedSchd(rs.getBoolean("InstBasedSchd"));
				ft.setOcrRequired(rs.getBoolean("OcrRequired"));
				ft.setAllowedOCRS(rs.getString("AllowedOCRS"));
				ft.setDefaultOCR(rs.getString("DefaultOCR"));
				ft.setGrcAdjReq(rs.getBoolean("GrcAdjReq"));
				ft.setGrcPeriodAftrFullDisb(rs.getBoolean("GrcPeriodAftrFullDisb"));
				ft.setAutoIncrGrcEndDate(rs.getBoolean("AutoIncrGrcEndDate"));
				ft.setGrcAutoIncrMonths(rs.getInt("GrcAutoIncrMonths"));
				ft.setMaxAutoIncrAllowed(rs.getInt("MaxAutoIncrAllowed"));
				ft.setThrldtoMaintainGrcPrd(rs.getInt("ThrldtoMaintainGrcPrd"));
				ft.setTdsType(rs.getString("TdsType"));
				ft.setCalcOfSteps(rs.getString("CalcOfSteps"));
				ft.setStepsAppliedFor(rs.getString("StepsAppliedFor"));
				ft.setIntProvRule(JdbcUtil.getLong(rs.getObject("IntProvRule")));
				ft.setRegProvRule(JdbcUtil.getLong(rs.getObject("RegProvRule")));
				ft.setOverdraftTxnChrgReq(rs.getBoolean("OverdraftTxnChrgReq"));
				ft.setOverdraftTxnChrgFeeType(rs.getLong("OverdraftTxnChrgFeeType"));
				ft.setOverDraftExtGraceDays(rs.getInt("OverDraftExtGraceDays"));
				ft.setOverDraftColChrgFeeType(rs.getLong("OverDraftColChrgFeeType"));
				ft.setOverDraftColAmt(rs.getBigDecimal("OverDraftColAmt"));
				ft.setClosureThresholdLimit(rs.getBigDecimal("ClosureThresholdLimit"));
				ft.setMaxFPPCalType(rs.getString("MaxFPPCalType"));
				ft.setMaxFPPAmount(rs.getBigDecimal("MaxFPPAmount"));
				ft.setMaxFPPPer(rs.getBigDecimal("MaxFPPPer"));
				ft.setMaxFPPCalOn(rs.getString("MaxFPPCalOn"));
				ft.setPpLockInPeriod(rs.getInt("PpLockInPeriod"));
				ft.setEsLockInPeriod(rs.getInt("EsLockInPeriod"));
				ft.setMinPPCalType(rs.getString("MinPPCalType"));
				ft.setMinPPCalOn(rs.getString("MinPPCalOn"));
				ft.setMinPPAmount(rs.getBigDecimal("MinPPAmount"));
				ft.setMinPPPercentage(rs.getBigDecimal("MinPPPercentage"));
				ft.setMaxPPCalType(rs.getString("MaxPPCalType"));
				ft.setMaxPPAmount(rs.getBigDecimal("MaxPPAmount"));
				ft.setMaxPPPercentage(rs.getBigDecimal("MaxPPPercentage"));
				ft.setMaxPPCalOn(rs.getString("MaxPPCalOn"));
				ft.setAllowAutoRefund(rs.getBoolean("AllowAutoRefund"));
				ft.setMaxAutoRefund(rs.getBigDecimal("MaxAutoRefund"));
				ft.setMinAutoRefund(rs.getBigDecimal("MinAutoRefund"));
				ft.setAssetClassSetup(rs.getLong("AssetClassSetup"));
				ft.setOdMinAmount(rs.getBigDecimal("ODMinAmount"));
				ft.setAllowCancelFin(rs.getBoolean("AllowCancelFin"));

				if (StringUtils.trimToEmpty(type).contains("View")) {
					ft.setFinCategoryDesc(rs.getString("FinCategoryDesc"));
					ft.setDownPayRuleCode(rs.getString("DownPayRuleCode"));
					ft.setDownPayRuleDesc(rs.getString("DownPayRuleDesc"));
					ft.setLovDescFinDivisionName(rs.getString("LovDescFinDivisionName"));
					ft.setLovDescPromoFinTypeDesc(rs.getString("LovDescPromoFinTypeDesc"));
					ft.setProfitCenterCode(rs.getString("ProfitcenterCode"));
					ft.setProfitCenterDesc(rs.getString("ProfitCenterDesc"));
					ft.setLovDescEntityCode(rs.getString("LovDescEntityCode"));
					ft.setAssetClassSetupCode(rs.getString("AssetClassSetupCode"));
					ft.setAssetClassSetupDesc(rs.getString("AssetClassSetupDesc"));
				}

				ft.setVersion(rs.getInt("Version"));
				ft.setLastMntBy(rs.getLong("LastMntBy"));
				ft.setLastMntOn(rs.getTimestamp("LastMntOn"));
				ft.setRecordStatus(rs.getString("RecordStatus"));
				ft.setRoleCode(rs.getString("RoleCode"));
				ft.setNextRoleCode(rs.getString("NextRoleCode"));
				ft.setTaskId(rs.getString("TaskId"));
				ft.setNextTaskId(rs.getString("NextTaskId"));
				ft.setRecordType(rs.getString("RecordType"));
				ft.setWorkflowId(rs.getLong("WorkflowId"));

				return ft;
			}, id);
		} catch (EmptyResultDataAccessException e) {
			logger.warn(Message.NO_RECORD_FOUND);
			return null;
		}
	}

	@Override
	public FinanceType getOrgFinanceTypeByID(final String finType, String type) {
		StringBuilder sql = new StringBuilder("Select");
		sql.append(" FinType, FinCategory, FinTypeDesc, FinCcy, FinDaysCalType");
		sql.append(", FinIsGenRef, FinMaxAmount, FinMinAmount, FinIsAlwMD, FinSchdMthd");
		sql.append(", FInIsAlwGrace, EqualRepayment, SchdOnPMTCal, FinRateType, FinBaseRate, FinSplRate");
		sql.append(", FinIntRate, FInMinRate, FinMaxRate, FinDftIntFrq, FinIsIntCpz, FinCpzFrq, FinIsRvwAlw");
		sql.append(", FinRvwFrq, FinGrcRateType, FinGrcBaseRate, FinGrcSplRate, FinGrcIntRate, FInGrcMinRate");
		sql.append(", FinGrcMaxRate, FinGrcDftIntFrq, FinGrcIsIntCpz, FinGrcCpzFrq, FinGrcIsRvwAlw");
		sql.append(", FinGrcRvwFrq, FinMinTerm, FinMaxTerm, FinDftTerms, FinRpyFrq, FinRepayMethod");
		sql.append(", FinIsAlwPartialRpy, FinIsAlwDifferment, FinMaxDifferment, FinIsActive, StepFinance");
		sql.append(", SteppingMandatory, AlwManualSteps, AlwdStepPolicies, DftStepPolicy, FinIsDwPayRequired");
		sql.append(", FinRvwRateApplFor, FinAlwRateChangeAnyDate, FinIsIntCpzAtGrcEnd, FinSchCalCodeOnRvw");
		sql.append(", AlwPlanDeferment, FinIsRateRvwAtGrcEnd, PlanDeferCount ");
		sql.append(", FinIsAlwGrcRepay, FinGrcSchdMthd, FinGrcMargin, FinMargin, FinCommitmentReq");
		sql.append(", FinCollateralReq ");
		sql.append(", OverrideLimit, LimitRequired, FinCommitmentOvrride");
		sql.append(", FinCollateralOvrride, FinRepayPftOnFrq, FinPftUnChanged, ApplyODPenalty, ODIncGrcDays");
		sql.append(", ODChargeType, ODGraceDays, ODChargeCalOn, ODChargeAmtOrPerc, ODAllowWaiver, ODMaxWaiverPerc");
		sql.append(", ODMinCapAmount, FinDivision, Product, StartDate, EndDate");
		sql.append(", PastduePftCalMthd, PastduePftMargin");
		sql.append(", DownPayRule, AlwMultiPartyDisb, CollateralType, TdsApplicable, ApplyGrcPricing");
		sql.append(", ApplyRpyPricing, DroplineOD, DroppingMethod, RateChgAnyDay, ManualSchedule, AlwBPI");
		sql.append(", BpiTreatment, PftDueSchOn, PlanEMIHAlw, AlwPlannedEmiInGrc, PlanEMIHMethod, PlanEMIHMaxPerYear");
		sql.append(", PlanEMIHMax, PlanEMIHLockPeriod, PlanEMICpz, UnPlanEMIHLockPeriod, UnPlanEMICpz, ReAgeCpz");
		sql.append(", FddLockPeriod, AlwdRpyMethods, MaxUnplannedEmi, MaxReAgeHolidays, RoundingMode");
		sql.append(", RoundingTarget, FrequencyDays, AlwReage, AlwUnPlanEmiHoliday, AlwMaxDisbCheckReq");
		sql.append(", QuickDisb, AutoApprove, ProductCategory, DeveloperFinance, CostOfFunds, ChequeCaptureReq");
		sql.append(", FinLTVCheck, PartiallySecured, BpiPftDaysBasis, AlwHybridRate, FixedRateTenor");
		sql.append(", EligibilityMethods, ODRuleCode, AlwZeroIntAcc, AutoRejectionDays, TaxNoMand");
		sql.append(", PutCallRequired, GrcAdvIntersetReq, GrcAdvType, GrcAdvMinTerms, GrcAdvMaxTerms");
		sql.append(", GrcAdvDefaultTerms, WriteOffRepayHry, MatureRepayHry, PresentmentRepayHry");
		sql.append(", AdvIntersetReq, AdvType, AdvMinTerms, AdvMaxTerms, AdvDefaultTerms");
		sql.append(", AdvStage, DsfReq, CashCollateralReq, TdsAllowToModify, TdsApplicableTo, AlwVan");
		sql.append(", VanAllocationMethod, AllowDrawingPower, AllowRevolving, AlwSanctionAmt");
		sql.append(", AlwSanctionAmtOverride, SanBsdSchdle");
		sql.append(", OcrRequired, AllowedOCRS, DefaultOCR, AllowedLoanPurposes, SpecificLoanPurposes"); // HL- merging
		sql.append(", GrcAdjReq, GrcPeriodAftrFullDisb, AutoIncrGrcEndDate, GrcAutoIncrMonths, MaxAutoIncrAllowed");
		sql.append(", ThrldtoMaintainGrcPrd, CalcOfSteps, StepsAppliedFor, AlwLoanSplit, SplitLoanType");
		sql.append(", InstBasedSchd, TdsType, SubventionReq, OverdraftTxnChrgReq, OverdraftTxnChrgFeeType");
		sql.append(", ClosureThresholdLimit");
		sql.append(", MaxFPPCalType, MaxFPPAmount, MaxFPPPer, MaxFPPCalOn");
		sql.append(", PpLockInPeriod, EsLockInPeriod, MinPPCalType, MinPPCalOn");
		sql.append(", MinPPAmount, MinPPPercentage, MaxPPCalType, MaxPPAmount, MaxPPPercentage, MaxPPCalOn");
		sql.append(", AllowAutoRefund, MaxAutoRefund, MinAutoRefund, OverDraftColChrgFeeType");
		sql.append(", OverDraftColAmt, AssetClassSetup, ODMinAmount, AllowCancelFin");

		if (StringUtils.trimToEmpty(type).contains("ORGView")) {
			sql.append(", DownPayRuleCode, DownPayRuleDesc, LovDescFinDivisionName, LovDescPromoFinTypeDesc");
			sql.append(", LovDescDftStepPolicyName, GrcPricingMethodDesc, RpyPricingMethodDesc, DftStepPolicyType");
			sql.append(", RpyHierarchy, NpaRpyHierarchy, LovDescEntityCode, LovDescEntityDesc");
			sql.append(", AlwEarlyPayMethods, FinScheduleOn, AssetClassSetupCode, AssetClassSetupDesc");
		}

		sql.append(" from RMTFinanceTypes");
		sql.append(StringUtils.trimToEmpty(type));
		sql.append(" Where FinType = ?");

		logger.debug(Literal.SQL + sql.toString());

		try {
			return this.jdbcOperations.queryForObject(sql.toString(), new RowMapper<FinanceType>() {
				@Override
				public FinanceType mapRow(ResultSet rs, int rowNum) throws SQLException {
					FinanceType ft = new FinanceType();

					ft.setFinType(rs.getString("FinType"));
					ft.setFinCategory(rs.getString("FinCategory"));
					ft.setFinTypeDesc(rs.getString("FinTypeDesc"));
					ft.setFinCcy(rs.getString("FinCcy"));
					ft.setFinDaysCalType(rs.getString("FinDaysCalType"));
					ft.setFinIsGenRef(rs.getBoolean("FinIsGenRef"));
					ft.setFinMaxAmount(rs.getBigDecimal("FinMaxAmount"));
					ft.setFinMinAmount(rs.getBigDecimal("FinMinAmount"));
					ft.setFinIsAlwMD(rs.getBoolean("FinIsAlwMD"));
					ft.setFinSchdMthd(rs.getString("FinSchdMthd"));
					ft.setFInIsAlwGrace(rs.getBoolean("FInIsAlwGrace"));
					ft.setEqualRepayment(rs.getBoolean("EqualRepayment"));
					ft.setSchdOnPMTCal(rs.getBoolean("SchdOnPMTCal"));
					ft.setFinRateType(rs.getString("FinRateType"));
					ft.setFinBaseRate(rs.getString("FinBaseRate"));
					ft.setFinSplRate(rs.getString("FinSplRate"));
					ft.setFinIntRate(rs.getBigDecimal("FinIntRate"));
					ft.setFInMinRate(rs.getBigDecimal("FInMinRate"));
					ft.setFinMaxRate(rs.getBigDecimal("FinMaxRate"));
					ft.setFinDftIntFrq(rs.getString("FinDftIntFrq"));
					ft.setFinIsIntCpz(rs.getBoolean("FinIsIntCpz"));
					ft.setFinCpzFrq(rs.getString("FinCpzFrq"));
					ft.setFinIsRvwAlw(rs.getBoolean("FinIsRvwAlw"));
					ft.setFinRvwFrq(rs.getString("FinRvwFrq"));
					ft.setFinGrcRateType(rs.getString("FinGrcRateType"));
					ft.setFinGrcBaseRate(rs.getString("FinGrcBaseRate"));
					ft.setFinGrcSplRate(rs.getString("FinGrcSplRate"));
					ft.setFinGrcIntRate(rs.getBigDecimal("FinGrcIntRate"));
					ft.setFInGrcMinRate(rs.getBigDecimal("FInGrcMinRate"));
					ft.setFinGrcMaxRate(rs.getBigDecimal("FinGrcMaxRate"));
					ft.setFinGrcDftIntFrq(rs.getString("FinGrcDftIntFrq"));
					ft.setFinGrcIsIntCpz(rs.getBoolean("FinGrcIsIntCpz"));
					ft.setFinGrcCpzFrq(rs.getString("FinGrcCpzFrq"));
					ft.setFinGrcIsRvwAlw(rs.getBoolean("FinGrcIsRvwAlw"));
					ft.setFinGrcRvwFrq(rs.getString("FinGrcRvwFrq"));
					ft.setFinMinTerm(rs.getInt("FinMinTerm"));
					ft.setFinMaxTerm(rs.getInt("FinMaxTerm"));
					ft.setFinDftTerms(rs.getInt("FinDftTerms"));
					ft.setFinRpyFrq(rs.getString("FinRpyFrq"));
					ft.setFinRepayMethod(rs.getString("FinRepayMethod"));
					ft.setFinIsAlwPartialRpy(rs.getBoolean("FinIsAlwPartialRpy"));
					ft.setFinIsAlwDifferment(rs.getBoolean("FinIsAlwDifferment"));
					ft.setFinMaxDifferment(rs.getInt("FinMaxDifferment"));
					ft.setFinIsActive(rs.getBoolean("FinIsActive"));
					ft.setStepFinance(rs.getBoolean("StepFinance"));
					ft.setSteppingMandatory(rs.getBoolean("SteppingMandatory"));
					ft.setAlwManualSteps(rs.getBoolean("AlwManualSteps"));
					ft.setAlwdStepPolicies(rs.getString("AlwdStepPolicies"));
					ft.setDftStepPolicy(rs.getString("DftStepPolicy"));
					ft.setFinIsDwPayRequired(rs.getBoolean("FinIsDwPayRequired"));
					ft.setFinRvwRateApplFor(rs.getString("FinRvwRateApplFor"));
					ft.setFinAlwRateChangeAnyDate(rs.getBoolean("FinAlwRateChangeAnyDate"));
					ft.setFinIsIntCpzAtGrcEnd(rs.getBoolean("FinIsIntCpzAtGrcEnd"));
					ft.setFinSchCalCodeOnRvw(rs.getString("FinSchCalCodeOnRvw"));
					ft.setAlwPlanDeferment(rs.getBoolean("AlwPlanDeferment"));
					ft.setFinIsRateRvwAtGrcEnd(rs.getBoolean("FinIsRateRvwAtGrcEnd"));
					ft.setPlanDeferCount(rs.getInt("PlanDeferCount"));
					ft.setFinIsAlwGrcRepay(rs.getBoolean("FinIsAlwGrcRepay"));
					ft.setFinGrcSchdMthd(rs.getString("FinGrcSchdMthd"));
					ft.setFinGrcMargin(rs.getBigDecimal("FinGrcMargin"));
					ft.setFinMargin(rs.getBigDecimal("FinMargin"));
					ft.setFinCommitmentReq(rs.getBoolean("FinCommitmentReq"));
					ft.setFinCollateralReq(rs.getBoolean("FinCollateralReq"));
					ft.setOverrideLimit(rs.getBoolean("OverrideLimit"));
					ft.setLimitRequired(rs.getBoolean("LimitRequired"));
					ft.setFinCommitmentOvrride(rs.getBoolean("FinCommitmentOvrride"));
					ft.setFinCollateralOvrride(rs.getBoolean("FinCollateralOvrride"));
					ft.setFinRepayPftOnFrq(rs.getBoolean("FinRepayPftOnFrq"));
					ft.setFinPftUnChanged(rs.getBoolean("FinPftUnChanged"));
					ft.setApplyODPenalty(rs.getBoolean("ApplyODPenalty"));
					ft.setODIncGrcDays(rs.getBoolean("ODIncGrcDays"));
					ft.setODChargeType(rs.getString("ODChargeType"));
					ft.setODGraceDays(rs.getInt("ODGraceDays"));
					ft.setODChargeCalOn(rs.getString("ODChargeCalOn"));
					ft.setODChargeAmtOrPerc(rs.getBigDecimal("ODChargeAmtOrPerc"));
					ft.setODAllowWaiver(rs.getBoolean("ODAllowWaiver"));
					ft.setODMaxWaiverPerc(rs.getBigDecimal("ODMaxWaiverPerc"));
					ft.setODMinCapAmount(rs.getBigDecimal("ODMinCapAmount"));
					ft.setFinDivision(rs.getString("FinDivision"));
					ft.setProduct(rs.getString("Product"));
					ft.setStartDate(rs.getTimestamp("StartDate"));
					ft.setEndDate(rs.getTimestamp("EndDate"));
					ft.setPastduePftCalMthd(rs.getString("PastduePftCalMthd"));
					ft.setPastduePftMargin(rs.getBigDecimal("PastduePftMargin"));
					ft.setDownPayRule(rs.getLong("DownPayRule"));
					ft.setAlwMultiPartyDisb(rs.getBoolean("AlwMultiPartyDisb"));
					ft.setCollateralType(rs.getString("CollateralType"));
					ft.setTdsApplicable(rs.getBoolean("TdsApplicable"));
					ft.setApplyGrcPricing(rs.getBoolean("ApplyGrcPricing"));
					ft.setApplyRpyPricing(rs.getBoolean("ApplyRpyPricing"));
					ft.setDroplineOD(rs.getBoolean("DroplineOD"));
					ft.setDroppingMethod(rs.getString("DroppingMethod"));
					ft.setRateChgAnyDay(rs.getBoolean("RateChgAnyDay"));
					ft.setManualSchedule(rs.getBoolean("ManualSchedule"));
					ft.setAlwBPI(rs.getBoolean("AlwBPI"));
					ft.setBpiTreatment(rs.getString("BpiTreatment"));
					ft.setPftDueSchOn(rs.getString("PftDueSchOn"));
					ft.setPlanEMIHAlw(rs.getBoolean("PlanEMIHAlw"));
					ft.setalwPlannedEmiInGrc(rs.getBoolean("AlwPlannedEmiInGrc"));
					ft.setPlanEMIHMethod(rs.getString("PlanEMIHMethod"));
					ft.setPlanEMIHMaxPerYear(rs.getInt("PlanEMIHMaxPerYear"));
					ft.setPlanEMIHMax(rs.getInt("PlanEMIHMax"));
					ft.setPlanEMIHLockPeriod(rs.getInt("PlanEMIHLockPeriod"));
					ft.setPlanEMICpz(rs.getBoolean("PlanEMICpz"));
					ft.setUnPlanEMIHLockPeriod(rs.getInt("UnPlanEMIHLockPeriod"));
					ft.setUnPlanEMICpz(rs.getBoolean("UnPlanEMICpz"));
					ft.setReAgeCpz(rs.getBoolean("ReAgeCpz"));
					ft.setFddLockPeriod(rs.getInt("FddLockPeriod"));
					ft.setAlwdRpyMethods(rs.getString("AlwdRpyMethods"));
					ft.setMaxUnplannedEmi(rs.getInt("MaxUnplannedEmi"));
					ft.setMaxReAgeHolidays(rs.getInt("MaxReAgeHolidays"));
					ft.setRoundingMode(rs.getString("RoundingMode"));
					ft.setRoundingTarget(rs.getInt("RoundingTarget"));
					ft.setFrequencyDays(rs.getString("FrequencyDays"));
					ft.setAlwReage(rs.getBoolean("AlwReage"));
					ft.setAlwUnPlanEmiHoliday(rs.getBoolean("AlwUnPlanEmiHoliday"));
					ft.setAlwMaxDisbCheckReq(rs.getBoolean("AlwMaxDisbCheckReq"));
					ft.setQuickDisb(rs.getBoolean("QuickDisb"));
					ft.setAutoApprove(rs.getBoolean("AutoApprove"));
					ft.setProductCategory(rs.getString("ProductCategory"));
					ft.setDeveloperFinance(rs.getBoolean("DeveloperFinance"));
					ft.setCostOfFunds(rs.getString("CostOfFunds"));
					ft.setChequeCaptureReq(rs.getBoolean("ChequeCaptureReq"));
					ft.setFinLTVCheck(rs.getString("FinLTVCheck"));
					ft.setPartiallySecured(rs.getBoolean("PartiallySecured"));
					ft.setBpiPftDaysBasis(rs.getString("BpiPftDaysBasis"));
					ft.setAlwHybridRate(rs.getBoolean("AlwHybridRate"));
					ft.setFixedRateTenor(rs.getInt("FixedRateTenor"));
					ft.setEligibilityMethods(rs.getString("EligibilityMethods"));
					ft.setODRuleCode(rs.getString("ODRuleCode"));
					ft.setAlwZeroIntAcc(rs.getBoolean("AlwZeroIntAcc"));
					ft.setAutoRejectionDays(rs.getInt("AutoRejectionDays"));
					ft.setTaxNoMand(rs.getBoolean("TaxNoMand"));
					ft.setPutCallRequired(rs.getBoolean("PutCallRequired"));
					ft.setGrcAdvIntersetReq(rs.getBoolean("GrcAdvIntersetReq"));
					ft.setGrcAdvType(rs.getString("GrcAdvType"));
					ft.setGrcAdvMinTerms(rs.getInt("GrcAdvMinTerms"));
					ft.setGrcAdvMaxTerms(rs.getInt("GrcAdvMaxTerms"));
					ft.setGrcAdvDefaultTerms(rs.getInt("GrcAdvDefaultTerms"));
					ft.setWriteOffRepayHry(rs.getString("WriteOffRepayHry"));
					ft.setPresentmentRepayHry(rs.getString("PresentmentRepayHry"));
					ft.setMatureRepayHry(rs.getString("MatureRepayHry"));
					ft.setAdvIntersetReq(rs.getBoolean("AdvIntersetReq"));
					ft.setAdvType(rs.getString("AdvType"));
					ft.setAdvMinTerms(rs.getInt("AdvMinTerms"));
					ft.setAdvMaxTerms(rs.getInt("AdvMaxTerms"));
					ft.setAdvDefaultTerms(rs.getInt("AdvDefaultTerms"));
					ft.setAdvStage(rs.getString("AdvStage"));
					ft.setDsfReq(rs.getBoolean("DsfReq"));
					ft.setCashCollateralReq(rs.getBoolean("CashCollateralReq"));
					ft.setTdsAllowToModify(rs.getBoolean("TdsAllowToModify"));
					ft.setTdsApplicableTo(rs.getString("TdsApplicableTo"));
					ft.setAlwVan(rs.getBoolean("AlwVan"));
					ft.setVanAllocationMethod(rs.getString("VanAllocationMethod"));
					ft.setAllowDrawingPower(rs.getBoolean("AllowDrawingPower"));
					ft.setAllowRevolving(rs.getBoolean("AllowRevolving"));
					ft.setAlwSanctionAmt(rs.getBoolean("AlwSanctionAmt"));
					ft.setAlwSanctionAmtOverride(rs.getBoolean("AlwSanctionAmtOverride"));
					ft.setSanBsdSchdle(rs.getBoolean("SanBsdSchdle"));
					ft.setDefaultOCR(rs.getString("DefaultOCR"));
					// HL Merging
					ft.setOcrRequired(rs.getBoolean("OcrRequired"));
					ft.setAllowedLoanPurposes(rs.getString("AllowedLoanPurposes"));
					ft.setAllowedOCRS(rs.getString("AllowedOCRS"));
					ft.setSpecificLoanPurposes(rs.getString("SpecificLoanPurposes"));
					ft.setAlwLoanSplit(rs.getBoolean("AlwLoanSplit"));
					ft.setSplitLoanType(rs.getString("SplitLoanType"));
					ft.setInstBasedSchd(rs.getBoolean("InstBasedSchd"));
					ft.setTdsType(rs.getString("TdsType"));
					ft.setGrcAdjReq(rs.getBoolean("GrcAdjReq"));
					ft.setGrcPeriodAftrFullDisb(rs.getBoolean("GrcPeriodAftrFullDisb"));
					ft.setAutoIncrGrcEndDate(rs.getBoolean("AutoIncrGrcEndDate"));
					ft.setGrcAutoIncrMonths(rs.getInt("GrcAutoIncrMonths"));
					ft.setMaxAutoIncrAllowed(rs.getInt("MaxAutoIncrAllowed"));
					ft.setThrldtoMaintainGrcPrd(rs.getInt("ThrldtoMaintainGrcPrd"));
					ft.setCalcOfSteps(rs.getString("CalcOfSteps"));
					ft.setStepsAppliedFor(rs.getString("StepsAppliedFor"));
					ft.setSubventionReq(rs.getBoolean("SubventionReq"));
					ft.setOverdraftTxnChrgReq(rs.getBoolean("OverdraftTxnChrgReq"));
					ft.setOverdraftTxnChrgFeeType(rs.getLong("OverdraftTxnChrgFeeType"));
					ft.setClosureThresholdLimit(rs.getBigDecimal("ClosureThresholdLimit"));
					ft.setMaxFPPCalType(rs.getString("MaxFPPCalType"));
					ft.setMaxFPPAmount(rs.getBigDecimal("MaxFPPAmount"));
					ft.setMaxFPPPer(rs.getBigDecimal("MaxFPPPer"));
					ft.setMaxFPPCalOn(rs.getString("MaxFPPCalOn"));
					ft.setPpLockInPeriod(rs.getInt("PpLockInPeriod"));
					ft.setEsLockInPeriod(rs.getInt("EsLockInPeriod"));
					ft.setMinPPCalType(rs.getString("MinPPCalType"));
					ft.setMinPPCalOn(rs.getString("MinPPCalOn"));
					ft.setMinPPAmount(rs.getBigDecimal("MinPPAmount"));
					ft.setMinPPPercentage(rs.getBigDecimal("MinPPPercentage"));
					ft.setMaxPPCalType(rs.getString("MaxPPCalType"));
					ft.setMaxPPAmount(rs.getBigDecimal("MaxPPAmount"));
					ft.setMaxPPPercentage(rs.getBigDecimal("MaxPPPercentage"));
					ft.setMaxPPCalOn(rs.getString("MaxPPCalOn"));
					ft.setAllowAutoRefund(rs.getBoolean("AllowAutoRefund"));
					ft.setMaxAutoRefund(rs.getBigDecimal("MaxAutoRefund"));
					ft.setMinAutoRefund(rs.getBigDecimal("MinAutoRefund"));
					ft.setOverDraftColChrgFeeType(rs.getLong("OverDraftColChrgFeeType"));
					ft.setOverDraftColAmt(rs.getBigDecimal("OverDraftColAmt"));
					ft.setAssetClassSetup(rs.getLong("AssetClassSetup"));
					ft.setOdMinAmount(rs.getBigDecimal("ODMinAmount"));
					ft.setAllowCancelFin(rs.getBoolean("AllowCancelFin"));

					if (StringUtils.trimToEmpty(type).contains("ORGView")) {
						ft.setDownPayRuleCode(rs.getString("DownPayRuleCode"));
						ft.setDownPayRuleDesc(rs.getString("DownPayRuleDesc"));
						ft.setLovDescFinDivisionName(rs.getString("LovDescFinDivisionName"));
						ft.setLovDescPromoFinTypeDesc(rs.getString("LovDescPromoFinTypeDesc"));
						ft.setLovDescDftStepPolicyName(rs.getString("LovDescDftStepPolicyName"));
						ft.setGrcPricingMethodDesc(rs.getString("GrcPricingMethodDesc"));
						ft.setRpyPricingMethodDesc(rs.getString("RpyPricingMethodDesc"));
						ft.setDftStepPolicyType(rs.getString("DftStepPolicyType"));
						ft.setRpyHierarchy(rs.getString("RpyHierarchy"));
						ft.setNpaRpyHierarchy(rs.getString("NpaRpyHierarchy"));
						ft.setWriteOffRepayHry(rs.getString("WriteOffRepayHry"));
						ft.setPresentmentRepayHry(rs.getString("PresentmentRepayHry"));
						ft.setMatureRepayHry(rs.getString("MatureRepayHry"));
						ft.setLovDescEntityCode(rs.getString("LovDescEntityCode"));
						ft.setLovDescEntityDesc(rs.getString("LovDescEntityDesc"));
						ft.setAlwEarlyPayMethods(rs.getString("AlwEarlyPayMethods"));
						ft.setFinScheduleOn(rs.getString("FinScheduleOn"));
						ft.setAssetClassSetupCode(rs.getString("AssetClassSetupCode"));
						ft.setAssetClassSetupDesc(rs.getString("AssetClassSetupDesc"));
					}

					return ft;
				}
			}, finType);
		} catch (EmptyResultDataAccessException e) {
			logger.warn(Message.NO_RECORD_FOUND);
			return null;
		}
	}

	@Override
	public FinanceType getFinanceTypeByFinType(final String finType) {
		logger.debug(Literal.ENTERING);
		FinanceType financeType = new FinanceType();
		financeType.setFinType(finType);

		StringBuilder sql = new StringBuilder("Select");
		sql.append("  FinType, Product, FinCategory, FinDivision");
		sql.append(", FinIsAlwPartialRpy, FinSuspTrigger, FinSuspRemarks, PastduePftCalMthd, PastduePftMargin");
		sql.append(", AlwMultiPartyDisb, AlwMaxDisbCheckReq, CostOfFunds, FinLTVCheck, PartiallySecured, AlwVan");
		sql.append(", VanAllocationMethod, AlwSanctionAmt, AlwSanctionAmtOverride, AutoApprove, GrcAdjReq");
		sql.append(", GrcPeriodAftrFullDisb, AutoIncrGrcEndDate, GrcAutoIncrMonths, MaxAutoIncrAllowed, SubventionReq");
		sql.append(", ThrldtoMaintainGrcPrd, OverdraftTxnChrgReq, OverdraftTxnChrgFeeType, ProductCategory");
		sql.append(", TdsType, AutoApprove, GrcAdjReq, AllowAutoRefund");
		sql.append(", MaxAutoRefund, MinAutoRefund, ODMinAmount, AllowCancelFin");
		sql.append(" FROM RMTFinanceTypes");
		sql.append(" Where FinType = ?");

		logger.debug(Literal.SQL + sql.toString());

		try {
			return this.jdbcOperations.queryForObject(sql.toString(), (rs, num) -> {
				FinanceType ft = new FinanceType();

				ft.setFinType(rs.getString("FinType"));
				ft.setProduct(rs.getString("Product"));
				ft.setFinCategory(rs.getString("FinCategory"));
				ft.setFinDivision(rs.getString("FinDivision"));
				ft.setFinIsAlwPartialRpy(rs.getBoolean("FinIsAlwPartialRpy"));
				ft.setFinSuspTrigger(rs.getString("FinSuspTrigger"));
				ft.setFinSuspRemarks(rs.getString("FinSuspRemarks"));
				ft.setPastduePftCalMthd(rs.getString("PastduePftCalMthd"));
				ft.setPastduePftMargin(rs.getBigDecimal("PastduePftMargin"));
				ft.setAlwMultiPartyDisb(rs.getBoolean("AlwMultiPartyDisb"));
				ft.setAlwMaxDisbCheckReq(rs.getBoolean("AlwMaxDisbCheckReq"));
				ft.setCostOfFunds(rs.getString("CostOfFunds"));
				ft.setFinLTVCheck(rs.getString("FinLTVCheck"));
				ft.setPartiallySecured(rs.getBoolean("PartiallySecured"));
				ft.setAlwVan(rs.getBoolean("AlwVan"));
				ft.setVanAllocationMethod(rs.getString("VanAllocationMethod"));
				ft.setAlwSanctionAmt(rs.getBoolean("AlwSanctionAmt"));
				ft.setAlwSanctionAmtOverride(rs.getBoolean("AlwSanctionAmtOverride"));
				ft.setAutoApprove(rs.getBoolean("AutoApprove"));
				ft.setGrcAdjReq(rs.getBoolean("GrcAdjReq"));
				ft.setGrcPeriodAftrFullDisb(rs.getBoolean("GrcPeriodAftrFullDisb"));
				ft.setAutoIncrGrcEndDate(rs.getBoolean("AutoIncrGrcEndDate"));
				ft.setGrcAutoIncrMonths(rs.getInt("GrcAutoIncrMonths"));
				ft.setMaxAutoIncrAllowed(rs.getInt("MaxAutoIncrAllowed"));
				ft.setSubventionReq(rs.getBoolean("SubventionReq"));
				ft.setThrldtoMaintainGrcPrd(rs.getInt("ThrldtoMaintainGrcPrd"));
				ft.setOverdraftTxnChrgReq(rs.getBoolean("OverdraftTxnChrgReq"));
				ft.setOverdraftTxnChrgFeeType(rs.getLong("OverdraftTxnChrgFeeType"));
				ft.setProductCategory(rs.getString("ProductCategory"));
				ft.setAllowAutoRefund(rs.getBoolean("AllowAutoRefund"));
				ft.setMaxAutoRefund(rs.getBigDecimal("MaxAutoRefund"));
				ft.setMinAutoRefund(rs.getBigDecimal("MinAutoRefund"));
				ft.setOdMinAmount(rs.getBigDecimal("ODMinAmount"));
				ft.setAllowCancelFin(rs.getBoolean("AllowCancelFin"));

				return ft;
			}, finType);
		} catch (EmptyResultDataAccessException e) {
			logger.warn(Message.NO_RECORD_FOUND);
			return null;
		}
	}

	/**
	 * Method for Fetch Finance Type List
	 * 
	 * @return
	 */
	@Override
	public List<FinanceType> getFinTypeDetailForBatch() {
		logger.debug(Literal.ENTERING);

		StringBuilder selectSql = new StringBuilder("SELECT FinType, Product, FinCategory, FinDivision, ");
		selectSql.append(" FinIsAlwPartialRpy, FinSuspTrigger, FinSuspRemarks,  ");
		selectSql.append(" PastduePftCalMthd, PastduePftMargin,RpyHierarchy, NpaRpyHierarchy, CostOfFunds");
		selectSql.append(", FinLTVCheck, PartiallySecured, OverdraftTxnChrgReq, OverdraftTxnChrgFeeType, ODMinAmount");
		selectSql.append(" FROM RMTFinanceTypes");

		logger.debug("selectListSql: " + selectSql.toString());
		RowMapper<FinanceType> typeRowMapper = BeanPropertyRowMapper.newInstance(FinanceType.class);

		List<FinanceType> finTypes = this.jdbcTemplate.query(selectSql.toString(), typeRowMapper);
		logger.debug(Literal.LEAVING);
		return finTypes;
	}

	/**
	 * This method Deletes the Record from the RMTFinanceTypes or RMTFinanceTypes_Temp. if Record not deleted then
	 * throws DataAccessException with error 41003. delete Finance Types by key FinType
	 * 
	 * @param Finance Types (financeType)
	 * @param type    (String) ""/_Temp/_View
	 * @return void
	 * @throws DataAccessException
	 * 
	 */
	@Override
	public void delete(FinanceType financeType, String type) {
		logger.debug(Literal.ENTERING);
		int recordCount = 0;
		String deleteSql = "Delete From RMTFinanceTypes" + StringUtils.trimToEmpty(type) + " Where FinType =:FinType";
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(financeType);

		String[] valueParm = new String[2];
		String[] errParm = new String[2];

		valueParm[0] = financeType.getFinType();
		errParm[0] = PennantJavaUtil.getLabel("label_FinType") + ":" + valueParm[0];

		try {
			recordCount = this.jdbcTemplate.update(deleteSql, beanParameters);

			if (recordCount <= 0) {
				throw new ConcurrencyException();
			}
		} catch (DataAccessException e) {
			logger.debug("Error delete Method");
			throw new DependencyFoundException(e);
		}
		logger.debug(Literal.LEAVING);
	}

	/**
	 * This method insert new Records into RMTFinanceTypes or RMTFinanceTypes_Temp.
	 * 
	 * save Finance Types
	 * 
	 * @param Finance Types (financeType)
	 * @param type    (String) ""/_Temp/_View
	 * @return void
	 * @throws DataAccessException
	 * 
	 */

	@Override
	public String save(FinanceType financeType, String type) {
		logger.debug(Literal.ENTERING);
		StringBuilder sql = new StringBuilder("Insert Into RMTFinanceTypes");
		sql.append(StringUtils.trimToEmpty(type));
		sql.append("(FinType, Product, FinCategory,FinTypeDesc, FinCcy,  FinDaysCalType, ");
		sql.append(" FinIsGenRef,");
		sql.append(" FinMaxAmount, FinMinAmount,  FinDftStmtFrq,  FinIsAlwMD,");
		sql.append(" FinSchdMthd, FInIsAlwGrace, FinHistRetension, EqualRepayment, SchdOnPMTCal, FinRateType,");
		sql.append(
				" FinBaseRate, FinSplRate, FinIntRate, FInMinRate, FinMaxRate,FinDftIntFrq,  FinIsIntCpz, FinCpzFrq,");
		sql.append(
				" FinIsRvwAlw, FinRvwFrq, FinGrcRateType, FinGrcBaseRate, FinGrcSplRate, FinGrcIntRate, FInGrcMinRate,");
		sql.append(
				" FinGrcMaxRate,FinGrcDftIntFrq,  FinGrcIsIntCpz, FinGrcCpzFrq,  FinGrcIsRvwAlw, FinGrcRvwFrq, FinMinTerm,");
		sql.append(
				" FinMaxTerm, FinDftTerms, FinRpyFrq,  finRepayMethod,FinIsAlwPartialRpy, FinIsAlwDifferment, FinMaxDifferment,");
		sql.append(" AlwPlanDeferment, PlanDeferCount,FinIsAlwEarlyRpy, FinIsAlwEarlySettle, FinODRpyTries, ");
		sql.append(" FinIsDwPayRequired, FinRvwRateApplFor,FinIsIntCpzAtGrcEnd, FinIsRateRvwAtGrcEnd, ");
		sql.append(" FinAlwRateChangeAnyDate, ");
		sql.append(" FinSchCalCodeOnRvw,FinAssetType ,");
		sql.append(" FinIsActive, Version , LastMntBy, LastMntOn, RecordStatus, RoleCode, NextRoleCode, TaskId, ");
		sql.append(" NextTaskId, RecordType, WorkflowId ,FinGrcSchdMthd,FinIsAlwGrcRepay,");
		sql.append("	FinCommitmentReq,FinCollateralReq,");
		sql.append(" FinMargin,FinGrcMargin,FinScheduleOn,FinGrcScheduleOn, ");
		sql.append(" FinPftUnChanged ,ManualSchedule,");
		sql.append("  OverrideLimit, LimitRequired, FinCommitmentOvrride, FinCollateralOvrride, FinRepayPftOnFrq, ");
		sql.append(
				"  ApplyODPenalty , ODIncGrcDays , ODChargeType , ODGraceDays , ODChargeCalOn , ODChargeAmtOrPerc , ODAllowWaiver , ODMaxWaiverPerc, ODMinCapAmount, FinDivision, ");
		sql.append(
				"  StepFinance , SteppingMandatory , AlwManualSteps , AlwdStepPolicies, DftStepPolicy, StartDate, EndDate, ");
		sql.append(" Remarks, AlwEarlyPayMethods ,ProductCategory, ");
		sql.append(" PastduePftCalMthd,PastduePftMargin,");
		sql.append(" DownPayRule, FinSuspTrigger, FinSuspRemarks, AlwMultiPartyDisb, TdsApplicable, CollateralType, ");
		sql.append(
				" ApplyGrcPricing, GrcPricingMethod, ApplyRpyPricing, RpyPricingMethod, RpyHierarchy, NpaRpyHierarchy, DroplineOD, DroppingMethod,RateChgAnyDay, ");
		sql.append(
				" AlwBPI , BpiTreatment , PftDueSchOn , PlanEMIHAlw, AlwPlannedEmiInGrc, PlanEMIHMethod , PlanEMIHMaxPerYear , PlanEMIHMax ,AlwReage,AlwUnPlanEmiHoliday,QuickDisb, AutoApprove, chequeCaptureReq, ");
		sql.append(
				" PlanEMIHLockPeriod , PlanEMICpz , UnPlanEMIHLockPeriod , UnPlanEMICpz , ReAgeCpz, FddLockPeriod, AlwdRpyMethods,MaxUnplannedEmi, MaxReAgeHolidays, ");
		sql.append(
				" RoundingMode,RoundingTarget, FrequencyDays,alwMaxDisbCheckReq, ProfitCenterID ,DeveloperFinance, CostOfFunds, FinLTVCheck, PartiallySecured, ");
		sql.append(" bpiPftDaysBasis, alwHybridRate, fixedRateTenor, eligibilityMethods,ODRuleCode,AlwZeroIntAcc, ");
		sql.append(" AutoRejectionDays, TaxNoMand , PutCallRequired  ");
		sql.append(", WriteOffRepayHry, MatureRepayHry, PresentmentRepayHry");
		sql.append(", AdvIntersetReq, AdvType, AdvMinTerms, AdvMaxTerms, AdvDefaultTerms");
		sql.append(", GrcAdvIntersetReq, GrcAdvType, GrcAdvMinTerms, GrcAdvMaxTerms, GrcAdvDefaultTerms, AdvStage");
		sql.append(", DsfReq, CashCollateralReq , TdsAllowToModify, TdsApplicableTo, alwVan, vanAllocationMethod ");
		sql.append(", AllowDrawingPower, AllowRevolving, AlwSanctionAmt, AlwSanctionAmtOverride, SanBsdSchdle");
		sql.append(", OcrRequired, AllowedOCRS, DefaultOCR, AllowedLoanPurposes, SpecificLoanPurposes");
		sql.append(", GrcAdjReq, GrcPeriodAftrFullDisb, AutoIncrGrcEndDate, GrcAutoIncrMonths ");
		sql.append(
				", MaxAutoIncrAllowed, ThrldtoMaintainGrcPrd, CalcOfSteps, StepsAppliedFor, AlwLoanSplit, SplitLoanType,InstBasedSchd, TdsType, SubventionReq");
		sql.append(", RegProvRule, IntProvRule, OverDraftExtGraceDays");
		sql.append(", OverDraftColChrgFeeType, OverDraftColAmt, OverdraftTxnChrgReq, OverdraftTxnChrgFeeType");
		sql.append(", ClosureThresholdLimit");
		sql.append(", MaxFPPCalType, MaxFPPAmount, MaxFPPPer, MaxFPPCalOn");
		sql.append(", PpLockInPeriod, EsLockInPeriod, MinPPCalType, MinPPCalOn");
		sql.append(", MinPPAmount, MinPPPercentage, MaxPPCalType, MaxPPAmount, MaxPPPercentage, MaxPPCalOn");
		sql.append(", AllowAutoRefund, MaxAutoRefund, MinAutoRefund, AssetClassSetup, OdMinAmount, AllowCancelFin");
		sql.append(")");
		sql.append(" Values(:FinType, :Product, :FinCategory,:FinTypeDesc, :FinCcy,  :FinDaysCalType, ");
		sql.append(" :FinIsGenRef,");
		sql.append(" :FinMaxAmount, :FinMinAmount,  :FinDftStmtFrq,  :FinIsAlwMD,");
		sql.append(" :FinSchdMthd, :FInIsAlwGrace, :FinHistRetension, :EqualRepayment, :SchdOnPMTCal, :FinRateType,");
		sql.append(
				" :FinBaseRate, :FinSplRate, :FinIntRate, :FInMinRate, :FinMaxRate,:FinDftIntFrq,  :FinIsIntCpz, :FinCpzFrq,");
		sql.append(
				" :FinIsRvwAlw, :FinRvwFrq, :FinGrcRateType, :FinGrcBaseRate, :FinGrcSplRate, :FinGrcIntRate, :FInGrcMinRate,");
		sql.append(
				" :FinGrcMaxRate,:FinGrcDftIntFrq,  :FinGrcIsIntCpz, :FinGrcCpzFrq,  :FinGrcIsRvwAlw, :FinGrcRvwFrq, :FinMinTerm,");
		sql.append(
				" :FinMaxTerm, :FinDftTerms, :FinRpyFrq,  :finRepayMethod,:FinIsAlwPartialRpy, :FinIsAlwDifferment, :FinMaxDifferment,");
		sql.append(" :AlwPlanDeferment, :PlanDeferCount,:FinIsAlwEarlyRpy, :FinIsAlwEarlySettle, :FinODRpyTries, ");
		sql.append(
				" :FinIsDwPayRequired, :FinRvwRateApplFor,:FinIsIntCpzAtGrcEnd, :FinAlwRateChangeAnyDate, :FinIsRateRvwAtGrcEnd,");
		sql.append(" :FinSchCalCodeOnRvw,:FinAssetType, ");
		sql.append(
				" :FinIsActive, :Version , :LastMntBy, :LastMntOn, :RecordStatus, :RoleCode, :NextRoleCode, :TaskId, ");
		sql.append(" :NextTaskId, :RecordType, :WorkflowId ,:FinGrcSchdMthd,:FinIsAlwGrcRepay,");
		sql.append(" :FinCommitmentReq,:FinCollateralReq,");
		sql.append(" :FinMargin,:FinGrcMargin,:FinScheduleOn,:FinGrcScheduleOn, ");
		sql.append(" :FinPftUnChanged ,:ManualSchedule,");
		sql.append(
				" :OverrideLimit, :LimitRequired, :FinCommitmentOvrride, :FinCollateralOvrride , :FinRepayPftOnFrq, ");
		sql.append(
				" :ApplyODPenalty , :ODIncGrcDays , :ODChargeType , :ODGraceDays , :ODChargeCalOn , :ODChargeAmtOrPerc , :ODAllowWaiver , :ODMaxWaiverPerc, :ODMinCapAmount, :FinDivision, ");
		sql.append(
				" :StepFinance , :SteppingMandatory , :AlwManualSteps , :AlwdStepPolicies, :DftStepPolicy, :StartDate, :EndDate, ");
		sql.append(" :Remarks, :AlwEarlyPayMethods ,:ProductCategory, ");
		sql.append(" :PastduePftCalMthd,:PastduePftMargin,");
		sql.append(
				" :DownPayRule, :FinSuspTrigger, :FinSuspRemarks, :AlwMultiPartyDisb, :TdsApplicable, :CollateralType, ");
		sql.append(
				" :ApplyGrcPricing, :GrcPricingMethod, :ApplyRpyPricing, :RpyPricingMethod, :RpyHierarchy, :NpaRpyHierarchy, :DroplineOD, :DroppingMethod, :RateChgAnyDay,");
		sql.append(
				" :AlwBPI , :BpiTreatment , :PftDueSchOn , :PlanEMIHAlw , :AlwPlannedEmiInGrc , :PlanEMIHMethod , :PlanEMIHMaxPerYear , :PlanEMIHMax , :AlwReage, :AlwUnPlanEmiHoliday, :QuickDisb, :AutoApprove, :chequeCaptureReq, ");
		sql.append(
				" :PlanEMIHLockPeriod , :PlanEMICpz , :UnPlanEMIHLockPeriod , :UnPlanEMICpz , :ReAgeCpz, :FddLockPeriod, :AlwdRpyMethods,:MaxUnplannedEmi, :MaxReAgeHolidays,  ");
		sql.append(
				" :RoundingMode,:RoundingTarget, :FrequencyDays,:AlwMaxDisbCheckReq, :ProfitCenterID, :DeveloperFinance, :CostOfFunds, :FinLTVCheck, :PartiallySecured, ");
		sql.append(
				" :bpiPftDaysBasis, :alwHybridRate, :fixedRateTenor, :eligibilityMethods, :ODRuleCode, :AlwZeroIntAcc,");
		sql.append(" :AutoRejectionDays, :TaxNoMand , :PutCallRequired");
		sql.append(", :WriteOffRepayHry,  :MatureRepayHry, :PresentmentRepayHry");
		sql.append(", :AdvIntersetReq, :AdvType, :AdvMinTerms, :AdvMaxTerms, :AdvDefaultTerms");
		sql.append(
				", :GrcAdvIntersetReq, :GrcAdvType, :GrcAdvMinTerms, :GrcAdvMaxTerms, :GrcAdvDefaultTerms, :AdvStage");
		sql.append(
				", :DsfReq, :CashCollateralReq , :TdsAllowToModify , :TdsApplicableTo, :AlwVan, :VanAllocationMethod , :AllowDrawingPower, :AllowRevolving, :AlwSanctionAmt, :AlwSanctionAmtOverride, :SanBsdSchdle ");
		sql.append(", :OcrRequired, :AllowedOCRS, :DefaultOCR, :AllowedLoanPurposes, :SpecificLoanPurposes ");
		sql.append(", :GrcAdjReq, :GrcPeriodAftrFullDisb, :AutoIncrGrcEndDate, :GrcAutoIncrMonths ");
		sql.append(
				", :MaxAutoIncrAllowed, :ThrldtoMaintainGrcPrd, :CalcOfSteps, :StepsAppliedFor, :AlwLoanSplit, :SplitLoanType,:InstBasedSchd, :TdsType, :SubventionReq");
		sql.append(", :RegProvRule, :IntProvRule, :OverDraftExtGraceDays");
		sql.append(", :OverDraftColChrgFeeType, :OverDraftColAmt, :OverdraftTxnChrgReq, :OverdraftTxnChrgFeeType");
		sql.append(", :ClosureThresholdLimit");
		sql.append(", :MaxFPPCalType, :MaxFPPAmount, :MaxFPPPer, :MaxFPPCalOn");
		sql.append(", :PpLockInPeriod, :EsLockInPeriod, :MinPPCalType, :MinPPCalOn");
		sql.append(", :MinPPAmount, :MinPPPercentage, :MaxPPCalType, :MaxPPAmount, :MaxPPPercentage, :MaxPPCalOn");
		sql.append(
				", :AllowAutoRefund, :MaxAutoRefund, :MinAutoRefund, :AssetClassSetup, :OdMinAmount, :AllowCancelFin");
		sql.append(")");

		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(financeType);
		financeType.getFinMaxAmount();
		this.jdbcTemplate.update(sql.toString(), beanParameters);

		logger.debug(Literal.LEAVING);
		return financeType.getId();
	}

	/**
	 * This method updates the Record RMTFinanceTypes or RMTFinanceTypes_Temp. if Record not updated then throws
	 * DataAccessException with error 41004. update Finance Types by key FinType and Version
	 * 
	 * @param Finance Types (financeType)
	 * @param type    (String) ""/_Temp/_View
	 * @return void
	 * @throws DataAccessException
	 * 
	 */

	@Override
	public void update(FinanceType financeType, String type) {
		int recordCount = 0;
		logger.debug(Literal.ENTERING);
		StringBuilder sql = new StringBuilder("Update RMTFinanceTypes");
		sql.append(StringUtils.trimToEmpty(type));
		sql.append(
				" Set Product = :Product,  FinTypeDesc = :FinTypeDesc, FinCategory =:FinCategory, FinCcy = :FinCcy,");
		sql.append(" FinDaysCalType = :FinDaysCalType,");
		sql.append(" FinIsGenRef = :FinIsGenRef, FinMaxAmount = :FinMaxAmount,FinMinAmount = :FinMinAmount,");
		sql.append(" FinDftStmtFrq = :FinDftStmtFrq,FinIsAlwMD = :FinIsAlwMD,");
		sql.append(
				" FinSchdMthd = :FinSchdMthd, FInIsAlwGrace = :FInIsAlwGrace, FinHistRetension = :FinHistRetension,");
		sql.append(" EqualRepayment = :EqualRepayment, SchdOnPMTCal = :SchdOnPMTCal,");
		sql.append(" FinRateType = :FinRateType, FinBaseRate = :FinBaseRate, FinSplRate = :FinSplRate,");
		sql.append(
				" FinIntRate = :FinIntRate,FInMinRate = :FInMinRate, FinMaxRate = :FinMaxRate, FinDftIntFrq = :FinDftIntFrq,");
		sql.append(
				" FinIsIntCpz = :FinIsIntCpz, FinCpzFrq = :FinCpzFrq, FinIsRvwAlw = :FinIsRvwAlw,FinRvwFrq = :FinRvwFrq, ");
		sql.append(
				" FinGrcRateType = :FinGrcRateType, FinGrcBaseRate = :FinGrcBaseRate,FinGrcSplRate = :FinGrcSplRate,");
		sql.append(" FinGrcIntRate = :FinGrcIntRate, FInGrcMinRate = :FInGrcMinRate, FinGrcMaxRate = :FinGrcMaxRate,");
		sql.append(
				" FinGrcDftIntFrq = :FinGrcDftIntFrq,  FinGrcIsIntCpz = :FinGrcIsIntCpz, FinGrcCpzFrq = :FinGrcCpzFrq,");
		sql.append(" FinGrcIsRvwAlw = :FinGrcIsRvwAlw, FinGrcRvwFrq = :FinGrcRvwFrq,FinMinTerm = :FinMinTerm,");
		sql.append(" FinMaxTerm = :FinMaxTerm, FinDftTerms = :FinDftTerms, FinRpyFrq = :FinRpyFrq,");
		sql.append(" finRepayMethod = :finRepayMethod, FinIsAlwPartialRpy = :FinIsAlwPartialRpy,");
		sql.append(
				" FinIsAlwDifferment = :FinIsAlwDifferment, FinMaxDifferment= :FinMaxDifferment, AlwPlanDeferment=:AlwPlanDeferment,");
		sql.append(
				" PlanDeferCount=:PlanDeferCount, FinIsAlwEarlyRpy = :FinIsAlwEarlyRpy, FinIsAlwEarlySettle = :FinIsAlwEarlySettle,");
		sql.append(" FinODRpyTries = :FinODRpyTries,");
		sql.append(" FinIsDwPayRequired = :FinIsDwPayRequired,");
		sql.append(
				" FinRvwRateApplFor = :FinRvwRateApplFor,FinIsIntCpzAtGrcEnd = :FinIsIntCpzAtGrcEnd,FinAlwRateChangeAnyDate = :FinAlwRateChangeAnyDate, FinIsRateRvwAtGrcEnd = :FinIsRateRvwAtGrcEnd, ");
		sql.append(" FinSchCalCodeOnRvw = :FinSchCalCodeOnRvw,FinAssetType=:FinAssetType,");
		sql.append(" FinIsActive = :FinIsActive,");
		sql.append(" Version = :Version ,LastMntBy = :LastMntBy,LastMntOn = :LastMntOn,");
		sql.append(
				" RecordStatus= :RecordStatus, RoleCode = :RoleCode, NextRoleCode = :NextRoleCode, TaskId = :TaskId,");
		sql.append(
				" NextTaskId = :NextTaskId, RecordType = :RecordType, WorkflowId = :WorkflowId,FinGrcSchdMthd=:FinGrcSchdMthd, ");
		sql.append(
				" FinIsAlwGrcRepay=:FinIsAlwGrcRepay,FinScheduleOn=:FinScheduleOn,FinGrcScheduleOn=:FinGrcScheduleOn,");
		sql.append(" FinMargin=:FinMargin,FinGrcMargin=:FinGrcMargin,");
		sql.append(" FinCommitmentReq=:FinCommitmentReq ,FinCollateralReq=:FinCollateralReq ,");
		sql.append(" FinPftUnChanged=:FinPftUnChanged ,ManualSchedule = :ManualSchedule,");
		sql.append(" OverrideLimit=:OverrideLimit, ");
		sql.append(
				" LimitRequired=:LimitRequired ,FinCommitmentOvrride=:FinCommitmentOvrride ,FinCollateralOvrride=:FinCollateralOvrride , FinRepayPftOnFrq =:FinRepayPftOnFrq, ");
		sql.append(
				" ApplyODPenalty =:ApplyODPenalty , ODIncGrcDays =:ODIncGrcDays, ODChargeType=:ODChargeType , ODGraceDays=:ODGraceDays , ");
		sql.append(
				" ODChargeCalOn=:ODChargeCalOn , ODChargeAmtOrPerc=:ODChargeAmtOrPerc , ODAllowWaiver=:ODAllowWaiver , ODMaxWaiverPerc=:ODMaxWaiverPerc, ODMinCapAmount=:ODMinCapAmount, FinDivision=:FinDivision, ");
		sql.append(
				" StepFinance=:StepFinance , SteppingMandatory=:SteppingMandatory , AlwManualSteps=:AlwManualSteps , AlwdStepPolicies=:AlwdStepPolicies , DftStepPolicy=:DftStepPolicy,");
		sql.append(" StartDate=:StartDate, EndDate=:EndDate, ");
		sql.append(" Remarks=:Remarks, AlwEarlyPayMethods=:AlwEarlyPayMethods,");
		sql.append("  PastduePftCalMthd=:PastduePftCalMthd,PastduePftMargin=:PastduePftMargin,");
		sql.append(" DownPayRule=:DownPayRule, ProductCategory=:ProductCategory,");
		sql.append(
				" FinSuspTrigger=:FinSuspTrigger, FinSuspRemarks=:FinSuspRemarks , AlwMultiPartyDisb = :AlwMultiPartyDisb , TdsApplicable=:TdsApplicable, CollateralType = :CollateralType, ");
		sql.append(
				" ApplyGrcPricing = :ApplyGrcPricing, GrcPricingMethod = :GrcPricingMethod, ApplyRpyPricing = :ApplyRpyPricing, RpyPricingMethod = :RpyPricingMethod, RpyHierarchy = :RpyHierarchy, ");
		sql.append(" DroplineOD = :DroplineOD, DroppingMethod = :DroppingMethod, RateChgAnyDay=:RateChgAnyDay,");
		sql.append(
				" AlwBPI=:AlwBPI , BpiTreatment=:BpiTreatment , PftDueSchOn=:PftDueSchOn , PlanEMIHAlw =:PlanEMIHAlw , AlwPlannedEmiInGrc = :AlwPlannedEmiInGrc, PlanEMIHMethod =:PlanEMIHMethod , PlanEMIHMaxPerYear =:PlanEMIHMaxPerYear , PlanEMIHMax=:PlanEMIHMax , ");
		sql.append(
				" PlanEMIHLockPeriod=:PlanEMIHLockPeriod , PlanEMICpz=:PlanEMICpz , UnPlanEMIHLockPeriod=:UnPlanEMIHLockPeriod , UnPlanEMICpz=:UnPlanEMICpz ,AlwReage=:AlwReage,AlwUnPlanEmiHoliday=:AlwUnPlanEmiHoliday, ");
		sql.append(
				" ReAgeCpz=:ReAgeCpz, FddLockPeriod=:FddLockPeriod, AlwdRpyMethods=:AlwdRpyMethods, MaxUnplannedEmi=:MaxUnplannedEmi, MaxReAgeHolidays=:MaxReAgeHolidays, chequeCaptureReq = :chequeCaptureReq, ");
		sql.append(
				" RoundingMode=:RoundingMode ,RoundingTarget=:RoundingTarget, FrequencyDays=:FrequencyDays,AlwMaxDisbCheckReq=:AlwMaxDisbCheckReq,QuickDisb=:QuickDisb, AutoApprove = :AutoApprove, ProfitCenterID = :ProfitCenterID, DeveloperFinance = :DeveloperFinance, CostOfFunds = :CostOfFunds,");
		sql.append(" FinLTVCheck = :FinLTVCheck, PartiallySecured = :PartiallySecured,");
		sql.append(" bpiPftDaysBasis = :bpiPftDaysBasis, eligibilityMethods = :eligibilityMethods,");
		sql.append(
				" alwHybridRate = :alwHybridRate, fixedRateTenor = :fixedRateTenor, ODRuleCode = :ODRuleCode, AlwZeroIntAcc = :AlwZeroIntAcc, ");
		sql.append(" AutoRejectionDays = :AutoRejectionDays, TaxNoMand = :TaxNoMand ,");
		sql.append(" PutCallRequired= :PutCallRequired ");

		sql.append(
				", GrcAdvIntersetReq= :GrcAdvIntersetReq, GrcAdvType= :GrcAdvType, GrcAdvMinTerms= :GrcAdvMinTerms, GrcAdvMaxTerms= :GrcAdvMaxTerms, GrcAdvDefaultTerms= :GrcAdvDefaultTerms");
		sql.append(
				", WriteOffRepayHry= :WriteOffRepayHry, MatureRepayHry= :MatureRepayHry, PresentmentRepayHry= :PresentmentRepayHry");
		sql.append(
				", AdvIntersetReq= :AdvIntersetReq, AdvType= :AdvType, AdvMinTerms= :AdvMinTerms, AdvMaxTerms= :AdvMaxTerms, AdvDefaultTerms= :AdvDefaultTerms");
		sql.append(
				", AdvStage= :AdvStage, DsfReq= :DsfReq, CashCollateralReq= :CashCollateralReq  , TdsAllowToModify =:TdsAllowToModify, TdsApplicableTo =:TdsApplicableTo");
		sql.append(
				", AlwVan =:AlwVan, VanAllocationMethod =:VanAllocationMethod , AllowDrawingPower =:AllowDrawingPower, AllowRevolving =:AllowRevolving, AlwSanctionAmt =:AlwSanctionAmt, AlwSanctionAmtOverride =:AlwSanctionAmtOverride, SanBsdSchdle =:SanBsdSchdle");
		sql.append(", OcrRequired =:OcrRequired, AllowedOCRS =:AllowedOCRS, DefaultOCR =:DefaultOCR ");
		sql.append(", AllowedLoanPurposes =:AllowedLoanPurposes, SpecificLoanPurposes =:SpecificLoanPurposes");
		sql.append(
				", GrcAdjReq = :GrcAdjReq, GrcPeriodAftrFullDisb = :GrcPeriodAftrFullDisb, AutoIncrGrcEndDate = :AutoIncrGrcEndDate");
		sql.append(", GrcAutoIncrMonths = :GrcAutoIncrMonths, MaxAutoIncrAllowed = :MaxAutoIncrAllowed");
		sql.append(", ThrldtoMaintainGrcPrd = :ThrldtoMaintainGrcPrd, CalcOfSteps = :CalcOfSteps");
		sql.append(", StepsAppliedFor = :StepsAppliedFor");
		sql.append(", RegProvRule = :RegProvRule, IntProvRule = :IntProvRule");
		sql.append(", OverDraftExtGraceDays = :OverDraftExtGraceDays");
		sql.append(", OverDraftColChrgFeeType = :OverDraftColChrgFeeType, OverDraftColAmt = :OverDraftColAmt");
		sql.append(", OverdraftTxnChrgReq= :OverdraftTxnChrgReq, OverdraftTxnChrgFeeType = :OverdraftTxnChrgFeeType");
		sql.append(", AlwLoanSplit = :AlwLoanSplit, SplitLoanType = :SplitLoanType,InstBasedSchd=:InstBasedSchd");
		sql.append(", TdsType = :TdsType, SubventionReq =:SubventionReq");
		sql.append(", ClosureThresholdLimit = :ClosureThresholdLimit, MaxFPPCalType = :MaxFPPCalType");
		sql.append(", MaxFPPAmount = :MaxFPPAmount, MaxFPPPer = :MaxFPPPer, MaxFPPCalOn = :MaxFPPCalOn");
		sql.append(", PpLockInPeriod = :PpLockInPeriod, EsLockInPeriod = :EsLockInPeriod");
		sql.append(", MinPPCalType = :MinPPCalType, MinPPCalOn = :MinPPCalOn, MinPPAmount = :MinPPAmount");
		sql.append(", MinPPPercentage = :MinPPPercentage, MaxPPCalType = :MaxPPCalType, MaxPPAmount = :MaxPPAmount");
		sql.append(", MaxPPPercentage = :MaxPPPercentage, MaxPPCalOn = :MaxPPCalOn");
		sql.append(", AllowAutoRefund = :AllowAutoRefund, MaxAutoRefund =:MaxAutoRefund");
		sql.append(", MinAutoRefund = :MinAutoRefund, NpaRpyHierarchy = :NpaRpyHierarchy");
		sql.append(", AssetClassSetup= :AssetClassSetup, OdMinAmount = :OdMinAmount, AllowCancelFin = :AllowCancelFin");
		sql.append(" Where FinType =:FinType");

		if (!type.endsWith("_Temp")) {
			sql.append(" AND Version= :Version-1");
		}

		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(financeType);
		recordCount = this.jdbcTemplate.update(sql.toString(), beanParameters);

		if (recordCount <= 0) {
			throw new ConcurrencyException();
		}
		logger.debug(Literal.LEAVING);
	}

	/**
	 * Method for get total number of records related to specific financeType
	 * 
	 * @param finType
	 */
	@Override
	public int getFinanceTypeCountById(String finType) {
		logger.debug(Literal.ENTERING);

		FinanceType financeType = new FinanceType();
		financeType.setFinType(finType);

		StringBuilder selectSql = new StringBuilder("SELECT COUNT(*) ");
		selectSql.append(" From RMTFinanceTypes ");
		selectSql.append(" Where FinType =:FinType AND (Product IS NULL OR Product = '')");

		logger.debug("selectSql: " + selectSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(financeType);

		return this.jdbcTemplate.queryForObject(selectSql.toString(), beanParameters, Integer.class);
	}

	/**
	 * Method for get total number of records related to specific financeType(Promotion)
	 * 
	 * @param finType
	 */
	@Override
	public int getPromotionTypeCountById(String finType) {
		logger.debug(Literal.ENTERING);

		FinanceType financeType = new FinanceType();
		financeType.setFinType(finType);

		StringBuilder selectSql = new StringBuilder("SELECT COUNT(*) ");
		selectSql.append(" From RMTFinanceTypes ");
		selectSql.append(" Where FinType =:FinType AND Product IS NOT NULL AND Product <> ' '");

		logger.debug("selectSql: " + selectSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(financeType);

		return this.jdbcTemplate.queryForObject(selectSql.toString(), beanParameters, Integer.class);
	}

	/**
	 * Fetch record count of product
	 * 
	 * @param productCode
	 * @return Integer
	 */
	@Override
	public int getProductCountById(String productCode) {
		logger.debug(Literal.ENTERING);

		FinanceType financeType = new FinanceType();
		financeType.setProduct(productCode);

		StringBuilder selectSql = new StringBuilder("SELECT COUNT(FinType) ");
		selectSql.append(" From RMTFinanceTypes Where Product =:Product");

		logger.debug("selectSql: " + selectSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(financeType);

		return this.jdbcTemplate.queryForObject(selectSql.toString(), beanParameters, Integer.class);
	}

	@Override
	public String getAllowedCollateralTypes(String finType) {
		String sql = "Select CollateralType From RMTFinanceTypes Where FinType = ?";

		logger.debug(Literal.SQL + sql);

		try {
			return this.jdbcOperations.queryForObject(sql, String.class, finType);
		} catch (EmptyResultDataAccessException dae) {
			logger.warn(Message.NO_RECORD_FOUND);
			return null;
		}
	}

	/**
	 * Fetch the FinanceTypes Based on the Product Code
	 * 
	 * @param productCode
	 */
	@Override
	public List<FinanceType> getFinanceTypeByProduct(String productCode) {
		logger.debug(Literal.ENTERING);

		FinanceType financeType = new FinanceType();
		financeType.setFinCategory(productCode);
		StringBuilder selectSql = new StringBuilder();
		selectSql.append("SELECT FinType, FinTypeDesc");
		selectSql.append(" From RMTFinanceTypes ");
		selectSql.append(" Where FinCategory =:FinCategory");

		logger.debug("selectSql: " + selectSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(financeType);
		RowMapper<FinanceType> typeRowMapper = BeanPropertyRowMapper.newInstance(FinanceType.class);

		return this.jdbcTemplate.query(selectSql.toString(), beanParameters, typeRowMapper);
	}

	/**
	 * Method for fetch financeType description
	 * 
	 * @param productCode
	 * @return String
	 */
	@Override
	public String getFinanceTypeDesc(String productCode) {
		logger.debug(Literal.ENTERING);

		FinanceType financeType = new FinanceType();
		financeType.setFinType(productCode);

		StringBuilder selectSql = new StringBuilder("SELECT FinTypeDesc ");
		selectSql.append(" From RMTFinanceTypes ");
		selectSql.append(" Where FinType =:FinType");

		logger.debug("selectSql: " + selectSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(financeType);

		try {
			return this.jdbcTemplate.queryForObject(selectSql.toString(), beanParameters, String.class);
		} catch (EmptyResultDataAccessException e) {
			logger.warn(Message.NO_RECORD_FOUND);
			return "";
		}
	}

	@Override
	public int getFinTypeCount(String finType, String type) {
		logger.debug(Literal.ENTERING);

		StringBuilder selectSql = new StringBuilder("Select Count(*) From RMTFinanceTypes");
		selectSql.append(StringUtils.trimToEmpty(type));
		selectSql.append(" Where FinType = :FinType");
		logger.debug("selectSql: " + selectSql.toString());

		MapSqlParameterSource source = new MapSqlParameterSource();
		source.addValue("FinType", finType);

		return this.jdbcTemplate.queryForObject(selectSql.toString(), source, Integer.class);
	}

	@Override
	public int getFinanceTypeByRuleCode(long ruleId, String type) {
		logger.debug(Literal.ENTERING);
		FinanceType financeType = new FinanceType();
		financeType.setDownPayRule(ruleId);

		StringBuilder selectSql = new StringBuilder("SELECT COUNT(*)");
		selectSql.append(" From RMTFinanceTypes");
		selectSql.append(StringUtils.trimToEmpty(type));
		selectSql.append(" Where DownPayRule =:DownPayRule");

		logger.debug("selectSql: " + selectSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(financeType);

		logger.debug(Literal.LEAVING);
		return this.jdbcTemplate.queryForObject(selectSql.toString(), beanParameters, Integer.class);
	}

	/**
	 * Method for Checking Step Policy Code is already using in Existing FinType or not
	 */
	@Override
	public boolean isStepPolicyExists(String policyCode) {
		logger.debug(Literal.ENTERING);

		StringBuilder selectSql = new StringBuilder("SELECT COUNT(FinType)");
		selectSql.append(" From RMTFinanceTypes_View ");
		selectSql.append(" Where ");
		selectSql.append("','");
		selectSql.append("||AlwdStepPolicies||");
		selectSql.append("','");
		selectSql.append(" LIKE ");
		selectSql.append("'%," + policyCode + ",%'");

		logger.debug("selectSql: " + selectSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(new FinanceType());
		int rcdCount = this.jdbcTemplate.queryForObject(selectSql.toString(), beanParameters, Integer.class);

		logger.debug(Literal.LEAVING);
		return rcdCount > 0 ? true : false;
	}

	/**
	 * Method for get total number of records from RMtFianceTypes master table.<br>
	 * 
	 * @param divisionCode
	 * 
	 * @return Boolean
	 */
	@Override
	public boolean isDivisionCodeExistsInFinanceTypes(String divisionCode, String type) {
		logger.debug(Literal.ENTERING);

		MapSqlParameterSource source = new MapSqlParameterSource();
		source.addValue("FINDIVISION", divisionCode);

		StringBuilder selectSql = new StringBuilder();
		selectSql.append("SELECT COUNT(*) FROM RMTFINANCETYPES");
		selectSql.append(StringUtils.trimToEmpty(type));
		selectSql.append(" WHERE FINDIVISION= :FINDIVISION");

		logger.debug(Literal.SQL + selectSql.toString());
		return this.jdbcTemplate.queryForObject(selectSql.toString(), source, Integer.class) > 0;
	}

	/**
	 * Method for validating customers in Caste
	 * 
	 */
	@Override
	public boolean isCostOfFundsExist(String costOfFunds, String type) {
		int count = 0;
		MapSqlParameterSource mapSqlParameterSource = new MapSqlParameterSource();
		mapSqlParameterSource.addValue("CostOfFunds", costOfFunds);

		StringBuilder selectSql = new StringBuilder("SELECT COUNT(CostOfFunds)  FROM  RMTFinanceTypes");
		selectSql.append(StringUtils.trimToEmpty(type));
		selectSql.append(" Where CostOfFunds = :CostOfFunds");

		logger.debug("selectSql: " + selectSql.toString());
		try {
			count = this.jdbcTemplate.queryForObject(selectSql.toString(), mapSqlParameterSource, Integer.class);
		} catch (EmptyResultDataAccessException e) {
			count = 0;
		}
		return count > 0 ? true : false;
	}

	@Override
	public FinanceType getFinLtvCheckByFinType(String finType) {
		logger.debug(Literal.ENTERING);
		FinanceType financeType = new FinanceType();
		financeType.setFinType(finType);

		StringBuilder selectSql = new StringBuilder(
				"SELECT FinType, FinLTVCheck, FinCollateralReq, CollateralType, PartiallySecured, RecordType");
		selectSql.append(" FROM RMTFinanceTypes");
		selectSql.append(" Where FinType = :FinType");

		logger.debug("selectListSql: " + selectSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(financeType);
		RowMapper<FinanceType> typeRowMapper = BeanPropertyRowMapper.newInstance(FinanceType.class);

		try {
			return this.jdbcTemplate.queryForObject(selectSql.toString(), beanParameters, typeRowMapper);
		} catch (EmptyResultDataAccessException e) {
			logger.warn(Message.NO_RECORD_FOUND);
			return null;
		}
	}

	@Override
	public String getAllowedRepayMethods(String finType, String type) {
		logger.debug(Literal.ENTERING);

		FinanceType financeType = new FinanceType();
		financeType.setFinType(finType);

		StringBuilder selectSql = new StringBuilder("SELECT AlwdRpyMethods ");
		selectSql.append(" From RMTFinanceTypes ");
		selectSql.append(" Where FinType =:FinType");

		logger.debug(Literal.SQL + selectSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(financeType);

		try {
			return this.jdbcTemplate.queryForObject(selectSql.toString(), beanParameters, String.class);
		} catch (EmptyResultDataAccessException e) {
			logger.warn(Message.NO_RECORD_FOUND);
			return "";
		}
	}

	/**
	 * Method for get the AutoRejectionDays for all LoanTypes.
	 * 
	 */
	public List<FinanceType> getAutoRejectionDays() {
		logger.debug(Literal.ENTERING);
		StringBuilder sql = new StringBuilder();
		sql.append("  SELECT FinType , AutoRejectionDays FROM RMTFinanceTypes Where AutoRejectionDays > 0");
		logger.debug(Literal.SQL + sql.toString());
		RowMapper<FinanceType> typeRowMapper = BeanPropertyRowMapper.newInstance(FinanceType.class);
		List<FinanceType> finTypes = this.jdbcTemplate.query(sql.toString(), typeRowMapper);
		logger.debug(Literal.LEAVING);
		return finTypes;
	}

	@Override
	public String getFinTypeByReference(String finref) {
		String sql = "Select FinType From financemain_view Where finreference = ?";

		logger.debug(Literal.SQL + sql);

		try {
			return this.jdbcOperations.queryForObject(sql, String.class, finref);
		} catch (EmptyResultDataAccessException dae) {
			logger.warn(Message.NO_RECORD_FOUND);
			return null;
		}
	}

	@Override
	public String getRepayHierarchy(String finType) {
		String sql = "Select RpyHierarchy From RMTFinanceTypes Where FinType = ?";

		logger.debug(Literal.SQL + sql);

		try {
			return jdbcOperations.queryForObject(sql, String.class, finType);
		} catch (EmptyResultDataAccessException e) {
			logger.warn(Message.NO_RECORD_FOUND);
			return null;
		}
	}

	@Override
	public FinanceType getFinanceType(final String finType) {
		logger.debug("Entering");
		FinanceType financeType = new FinanceType();
		financeType.setFinType(finType);

		StringBuilder selectSql = new StringBuilder();

		selectSql.append(" Select * FROM RMTFinanceTypes_View");
		selectSql.append(" Where FinType = :FinType");

		logger.debug("selectListSql: " + selectSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(financeType);
		RowMapper<FinanceType> typeRowMapper = BeanPropertyRowMapper.newInstance(FinanceType.class);

		try {
			return this.jdbcTemplate.queryForObject(selectSql.toString(), beanParameters, typeRowMapper);
		} catch (EmptyResultDataAccessException e) {
			logger.warn(Message.NO_RECORD_FOUND);
			return null;
		}
	}

	public List<String> getAllowedOCRList() {
		String sql = "Select AlloweDocrs FROM RMTFinanceTypes";

		logger.debug(Literal.SQL + sql);

		return this.jdbcOperations.query(sql, (rs, i) -> {
			return rs.getString("AlloweDocrs");
		});

	}

	@Override
	public String getFinDivsion(String finType) {
		String sql = "Select FinDivision From RMTFinanceTypes Where FinType = ?";

		logger.debug(Literal.SQL + sql);

		try {
			return this.jdbcOperations.queryForObject(sql, String.class, finType);
		} catch (EmptyResultDataAccessException dae) {
			logger.warn(Message.NO_RECORD_FOUND);
			return null;
		}
	}

	@Override
	public List<String> getFinanceTypeList() {
		logger.debug(Literal.ENTERING);
		List<String> ft = null;
		StringBuilder sql = new StringBuilder("Select");
		sql.append(" FinType");
		sql.append(" From RmtFinancetypes");
		logger.trace(Literal.SQL + sql.toString());
		try {
			ft = this.jdbcOperations.query(sql.toString(), new RowMapper<String>() {
				@Override
				public String mapRow(ResultSet rs, int rowNum) throws SQLException {

					return rs.getString("FinType");
				}
			});
		} catch (EmptyResultDataAccessException e) {
			logger.error(Literal.EXCEPTION, e);
		}
		logger.debug(Literal.LEAVING);
		return ft;

	}

	@Override
	public boolean isAllowCancelFin(String finType) {
		String sql = "Select AllowCancelFin From RmtFinancetypes Where FinType = ?";

		logger.debug(Literal.SQL.concat(sql));

		try {
			return this.jdbcOperations.queryForObject(sql, Boolean.class, finType);
		} catch (EmptyResultDataAccessException e) {
			logger.warn(Message.NO_RECORD_FOUND);
			return false;
		}
	}
}