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
 * FileName    		:  FinanceTypeDAOImpl.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  30-06-2011    														*
 *                                                                  						*
 * Modified Date    :  30-06-2011    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 30-06-2011       Pennant	                 0.1                                            * 
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

package com.pennant.backend.dao.rmtmasters.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.core.simple.ParameterizedBeanPropertyRowMapper;

import com.pennant.backend.dao.rmtmasters.FinanceTypeDAO;
import com.pennant.backend.model.rmtmasters.FinanceType;
import com.pennant.backend.util.PennantJavaUtil;
import com.pennanttech.pennapps.core.ConcurrencyException;
import com.pennanttech.pennapps.core.DependencyFoundException;
import com.pennanttech.pennapps.core.jdbc.BasicDao;
import com.pennanttech.pennapps.core.resource.Literal;

/**
 * DAO methods implementation for the <b>FinanceType model</b> class.<br>
 * 
 */
public class FinanceTypeDAOImpl extends BasicDao<FinanceType> implements FinanceTypeDAO {
	private static Logger logger = Logger.getLogger(FinanceTypeDAOImpl.class);

	public FinanceTypeDAOImpl() {
		super();
	}

	/**
	 * Fetch the Record Finance Types details by key field
	 * 
	 * @param id
	 *            (String)
	 * @param type
	 *            (String) ""/_Temp/_View
	 * @return FinanceType
	 */
	@Override
	public FinanceType getFinanceTypeByID(final String id, String type) {
		logger.debug("Entering");
		FinanceType financeType = new FinanceType();
		financeType.setId(id);

		StringBuilder sql = new StringBuilder(
				"SELECT FinType, Product,FinCategory, FinTypeDesc, FinCcy, FinDaysCalType,");
		sql.append(" FinAcType, FinContingentAcType, FinBankContingentAcType, FinProvisionAcType,FinSuspAcType,");
		sql.append(" FinIsGenRef, FinMaxAmount, FinMinAmount,FinIsOpenNewFinAc, FinDftStmtFrq,FinIsAlwMD, ");
		sql.append(" FinSchdMthd, FInIsAlwGrace,FinHistRetension, EqualRepayment,FinRateType, ");
		sql.append(" FinBaseRate,FinSplRate,FinIntRate, FInMinRate, FinMaxRate, FinDftIntFrq,  FinIsIntCpz,");
		sql.append(" FinCpzFrq,  FinIsRvwAlw, FinRvwFrq,  FinGrcRateType, FinGrcBaseRate,");
		sql.append(" FinGrcSplRate, FinGrcIntRate, FInGrcMinRate, FinGrcMaxRate,FinGrcDftIntFrq,");
		sql.append(" FinGrcIsIntCpz, FinGrcCpzFrq, FinGrcIsRvwAlw, FinGrcRvwFrq, FinMinTerm,");
		sql.append(" FinMaxTerm, FinDftTerms, FinRpyFrq,  finRepayMethod, FinIsAlwPartialRpy,");
		sql.append(" FinIsAlwDifferment,FinMaxDifferment, FinIsAlwEarlyRpy, FinIsAlwEarlySettle,");
		sql.append(" FinODRpyTries, AlwPlanDeferment,PlanDeferCount ,");
		sql.append(" FinIsDwPayRequired,  FinRvwRateApplFor, FinAlwRateChangeAnyDate, FinIsIntCpzAtGrcEnd,");
		sql.append(
				" FinSchCalCodeOnRvw, FinAssetType , FinDepositRestrictedTo,FinAEBuyOrInception,FinAESellOrMaturity,FinIsActive,PftPayAcType,");
		sql.append(" FinIsOpenPftPayAcc,FinGrcSchdMthd,FinIsAlwGrcRepay,FinMargin,FinGrcMargin,");
		sql.append(" FinScheduleOn,FinGrcScheduleOn,FinCommitmentReq,FinCollateralReq,");
		sql.append(" FinDepreciationReq,FinDepreciationFrq, ");
		sql.append(" AllowRIAInvestment , OverrideLimit , LimitRequired ,");
		sql.append(" FinCommitmentOvrride , FinCollateralOvrride ,FinRepayPftOnFrq, FinPftUnChanged,ManualSchedule, ");
		sql.append(
				" ApplyODPenalty , ODIncGrcDays , ODChargeType , ODGraceDays , ODChargeCalOn , ODChargeAmtOrPerc , ODAllowWaiver , ODMaxWaiverPerc, ODMinCapAmount, ODMinCapAmount, FinDivision, ");
		sql.append(
				" StepFinance , SteppingMandatory , AlwManualSteps , AlwdStepPolicies, DftStepPolicy, StartDate, EndDate,");
		sql.append(" AllowDownpayPgm,Remarks,AlwEarlyPayMethods,");
		sql.append(" PastduePftCalMthd,PastduePftMargin,AlwAdvanceRent,");
		sql.append(
				" GrcAdvBaseRate , GrcAdvMargin , GrcAdvPftRate, RpyAdvBaseRate , RpyAdvMargin , RpyAdvPftRate, RollOverFinance,RollOverFrq,");
		sql.append(" DownPayRule, FinSuspTrigger, FinSuspRemarks, AlwMultiPartyDisb, TDSApplicable, CollateralType, ");
		sql.append(
				" ApplyGrcPricing, GrcPricingMethod, ApplyRpyPricing, RpyPricingMethod, RpyHierarchy, DroplineOD, DroppingMethod ,RateChgAnyDay, ");
		sql.append(
				" AlwBPI , BpiTreatment , PftDueSchOn , PlanEMIHAlw , PlanEMIHMethod , PlanEMIHMaxPerYear , PlanEMIHMax , ");
		sql.append(
				" PlanEMIHLockPeriod , PlanEMICpz , UnPlanEMIHLockPeriod , UnPlanEMICpz , ReAgeCpz, FddLockPeriod, AlwdRpyMethods,AlwReage,AlwUnPlanEmiHoliday, ");
		sql.append(
				" MaxUnplannedEmi, MaxReAgeHolidays, RoundingMode, RoundingTarget, FrequencyDays,alwMaxDisbCheckReq,quickDisb, ProfitCenterID, ProductCategory, DeveloperFinance, CostOfFunds,");
		sql.append(
				" chequeCaptureReq, FinLTVCheck, PartiallySecured, bpiPftDaysBasis, alwHybridRate, fixedRateTenor, eligibilityMethods,ODRuleCode, AlwZeroIntAcc,");
		sql.append(" AutoRejectionDays, TaxNoMand, PutCallRequired");

		sql.append(", GrcAdvIntersetReq, GrcAdvType, GrcAdvMinTerms, GrcAdvMaxTerms, GrcAdvDefaultTerms");
		sql.append(", AdvIntersetReq, AdvType, AdvMaxTerms, AdvMinTerms, AdvDefaultTerms");
		sql.append(", AdvStage, DsfReq, CashCollateralReq");

		if (type.contains("View")) {
			sql.append(
					", FinCategoryDesc, DownPayRuleCode, DownPayRuleDesc, lovDescFinContingentAcTypeName,lovDescFinBankContAcTypeName,lovDescFinProvisionAcTypeName,lovDescFinAcTypeName");
			sql.append(
					", lovDescPftPayAcTypeName, lovDescFinSuspAcTypeName, lovDescFinDivisionName,lovDescPromoFinTypeDesc, ProfitCenterCode, ProfitCenterDesc, LovDescEntityCode ");
		}

		sql.append(" , Version, LastMntBy, LastMntOn, RecordStatus");
		sql.append(" , RoleCode, NextRoleCode, TaskId, NextTaskId, RecordType, WorkflowId");

		sql.append(" FROM RMTFinanceTypes");
		sql.append(StringUtils.trimToEmpty(type));
		sql.append(" Where FinType = :FinType");

		logger.debug("selectListSql: " + sql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(financeType);
		RowMapper<FinanceType> typeRowMapper = ParameterizedBeanPropertyRowMapper.newInstance(FinanceType.class);

		try {
			financeType = this.jdbcTemplate.queryForObject(sql.toString(), beanParameters, typeRowMapper);
		} catch (EmptyResultDataAccessException e) {
			logger.warn("Exception: ", e);
			financeType = null;
		}
		logger.debug("Leaving");
		return financeType;
	}

	/**
	 * Fetch the Record Finance Types details by key field
	 * 
	 * @param finType
	 *            (String)
	 * @param type
	 *            (String) ""/_Temp/_View
	 * @return FinanceType
	 */
	@Override
	public FinanceType getOrgFinanceTypeByID(final String finType, String type) {
		logger.debug("Entering");
		FinanceType financeType = new FinanceType();
		financeType.setId(finType);

		StringBuilder sql = new StringBuilder();
		sql.append(
				" Select FinType, FinCategory, FinTypeDesc, FinCcy, FinDaysCalType, FinAcType, FinContingentAcType, ");
		sql.append(
				" FinIsGenRef, FinMaxAmount, FinMinAmount, FinIsOpenNewFinAc, FinIsAlwMD, FinSchdMthd, FInIsAlwGrace, ");
		sql.append(" EqualRepayment, FinRateType, FinBaseRate, FinSplRate, FinIntRate, FInMinRate, FinMaxRate, ");
		sql.append(" FinDftIntFrq, FinIsIntCpz, FinCpzFrq, FinIsRvwAlw, FinRvwFrq, FinGrcRateType, FinGrcBaseRate, ");
		sql.append(" FinGrcSplRate, FinGrcIntRate, FInGrcMinRate, FinGrcMaxRate, FinGrcDftIntFrq, FinGrcIsIntCpz, ");
		sql.append(" FinGrcCpzFrq, FinGrcIsRvwAlw, FinGrcRvwFrq, FinMinTerm, FinMaxTerm, FinDftTerms, FinRpyFrq, ");
		sql.append(
				" FInRepayMethod, finIsalwpartialrpy, FinIsAlwDifferment, FinMaxDifferment, FinIsActive, StepFinance, SteppingMandatory, ");
		sql.append(" AlwManualSteps, AlwdStepPolicies, DftStepPolicy, FinIsDwPayRequired, FinRvwRateApplFor, ");
		sql.append(" FinAlwRateChangeAnyDate,  FinIsIntCpzAtGrcEnd, FinSchCalCodeOnRvw, AlwPlanDeferment, ");
		sql.append(
				" PlanDeferCount, PftPayAcType, FinIsOpenPftPayAcc, FinIsAlwGrcRepay, FinGrcSchdMthd, FinGrcMargin, ");
		sql.append(" FinMargin, FinCommitmentReq, FinCollateralReq, FinDepreciationReq, FinDepreciationFrq, ");
		sql.append(" FinBankContingentAcType, FinProvisionAcType, AllowRIAInvestment, OverrideLimit, LimitRequired, ");
		sql.append(" FinCommitmentOvrride, FinCollateralOvrride, FinRepayPftOnFrq, FinPftUnChanged, ApplyODPenalty, ");
		sql.append(" ODIncGrcDays, ODChargeType, ODGraceDays, ODChargeCalOn, ODChargeAmtOrPerc, ODAllowWaiver, ");
		sql.append(
				" ODMaxWaiverPerc, ODMinCapAmount, FinDivision, FinSuspAcType, Product, StartDate, EndDate, AllowDownpayPgm, ");
		sql.append(
				" PastduePftCalMthd,PastduePftMargin,  AlwAdvanceRent, GrcAdvBaseRate, GrcAdvMargin, GrcAdvPftRate, RpyAdvBaseRate, ");
		sql.append(" RpyAdvMargin, RpyAdvPftRate, RollOverFinance, RollOverFrq, DownPayRule, AlwMultiPartyDisb, ");
		sql.append(" CollateralType, TDSApplicable,ApplyGrcPricing, ApplyRpyPricing, DroplineOD, DroppingMethod, ");
		sql.append(" RateChgAnyDay, ManualSchedule, AlwBPI , BpiTreatment , PftDueSchOn , ");
		sql.append(
				" PlanEMIHAlw , PlanEMIHMethod , PlanEMIHMaxPerYear , PlanEMIHMax ,PlanEMIHLockPeriod , PlanEMICpz , ");
		sql.append(" UnPlanEMIHLockPeriod , UnPlanEMICpz , ReAgeCpz, FddLockPeriod, AlwdRpyMethods, MaxUnplannedEmi, ");
		sql.append(
				" MaxReAgeHolidays, RoundingMode, RoundingTarget, FrequencyDays,AlwReage,AlwUnPlanEmiHoliday,alwMaxDisbCheckReq,quickDisb,ProductCategory,DeveloperFinance, CostOfFunds, ");
		sql.append(
				" chequeCaptureReq, FinLTVCheck, PartiallySecured, bpiPftDaysBasis, alwHybridRate, fixedRateTenor, eligibilityMethods,ODRuleCode,AlwZeroIntAcc,");
		sql.append(" AutoRejectionDays, TaxNoMand , PutCallRequired");

		sql.append(", GrcAdvIntersetReq, GrcAdvType, GrcAdvMinTerms, GrcAdvMaxTerms, GrcAdvDefaultTerms");
		sql.append(", AdvIntersetReq, AdvType, AdvMinTerms, AdvMaxTerms, AdvDefaultTerms, AdvStage");
		sql.append(", DsfReq, CashCollateralReq");

		if (type.contains("ORGView")) {
			sql.append(
					" ,DownPayRuleCode, DownPayRuleDesc, LovDescFinDivisionName , lovDescPromoFinTypeDesc, lovDescDftStepPolicyName, ");
			sql.append(
					" GrcPricingMethodDesc, RpyPricingMethodDesc, DftStepPolicyType, RpyHierarchy, LovDescEntityCode, LovDescEntityDesc,alwEarlyPayMethods");
		}
		sql.append(" FROM RMTFinanceTypes");
		sql.append(StringUtils.trimToEmpty(type));
		sql.append(" Where FinType = :FinType");

		logger.debug("selectListSql: " + sql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(financeType);
		RowMapper<FinanceType> typeRowMapper = ParameterizedBeanPropertyRowMapper.newInstance(FinanceType.class);

		try {
			financeType = this.jdbcTemplate.queryForObject(sql.toString(), beanParameters, typeRowMapper);
		} catch (EmptyResultDataAccessException e) {
			logger.warn("Exception: ", e);
			financeType = null;
		}
		logger.debug("Leaving");
		return financeType;
	}

	/**
	 * Fetch the Record Finance Types details by key field
	 * 
	 * @param id
	 *            (String)
	 * @param type
	 *            (String) ""/_Temp/_View
	 * @return FinanceType
	 */
	@Override
	public FinanceType getFinanceTypeByFinType(final String finType) {
		logger.debug("Entering");
		FinanceType financeType = new FinanceType();
		financeType.setFinType(finType);

		StringBuilder selectSql = new StringBuilder("SELECT FinType, Product, FinAcType, FinCategory, FinDivision, ");
		selectSql.append(" FinIsOpenNewFinAc, PftPayAcType,  FinSuspAcType, FinProvisionAcType , ");
		selectSql.append(" AllowRIAInvestment, FinIsAlwPartialRpy, FinSuspTrigger, FinSuspRemarks,  ");
		selectSql.append(
				" PastduePftCalMthd, PastduePftMargin,alwMultiPartyDisb, alwMaxDisbCheckReq, CostOfFunds, FinLTVCheck, PartiallySecured ");
		selectSql.append(" FROM RMTFinanceTypes");
		selectSql.append(" Where FinType = :FinType");

		logger.debug("selectListSql: " + selectSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(financeType);
		RowMapper<FinanceType> typeRowMapper = ParameterizedBeanPropertyRowMapper.newInstance(FinanceType.class);

		try {
			financeType = this.jdbcTemplate.queryForObject(selectSql.toString(), beanParameters, typeRowMapper);
		} catch (EmptyResultDataAccessException e) {
			logger.warn("Exception: ", e);
			financeType = null;
		}
		logger.debug("Leaving");
		return financeType;
	}

	/**
	 * Method for Fetch Finance Type List
	 * 
	 * @return
	 */
	@Override
	public List<FinanceType> getFinTypeDetailForBatch() {
		logger.debug("Entering");

		StringBuilder selectSql = new StringBuilder("SELECT FinType, Product, FinAcType, FinCategory, FinDivision, ");
		selectSql.append(" FinIsOpenNewFinAc, PftPayAcType,  FinSuspAcType, FinProvisionAcType , ");
		selectSql.append(" AllowRIAInvestment, FinIsAlwPartialRpy, FinSuspTrigger, FinSuspRemarks,  ");
		selectSql.append(
				" PastduePftCalMthd, PastduePftMargin,RpyHierarchy, CostOfFunds, FinLTVCheck, PartiallySecured ");
		selectSql.append(" FROM RMTFinanceTypes");

		logger.debug("selectListSql: " + selectSql.toString());
		RowMapper<FinanceType> typeRowMapper = ParameterizedBeanPropertyRowMapper.newInstance(FinanceType.class);

		List<FinanceType> finTypes = this.jdbcTemplate.query(selectSql.toString(), typeRowMapper);
		logger.debug("Leaving");
		return finTypes;
	}

	/**
	 * This method Deletes the Record from the RMTFinanceTypes or
	 * RMTFinanceTypes_Temp. if Record not deleted then throws
	 * DataAccessException with error 41003. delete Finance Types by key FinType
	 * 
	 * @param Finance
	 *            Types (financeType)
	 * @param type
	 *            (String) ""/_Temp/_View
	 * @return void
	 * @throws DataAccessException
	 * 
	 */
	@Override
	public void delete(FinanceType financeType, String type) {
		logger.debug("Entering");
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
		logger.debug("Leaving");
	}

	/**
	 * This method insert new Records into RMTFinanceTypes or
	 * RMTFinanceTypes_Temp.
	 * 
	 * save Finance Types
	 * 
	 * @param Finance
	 *            Types (financeType)
	 * @param type
	 *            (String) ""/_Temp/_View
	 * @return void
	 * @throws DataAccessException
	 * 
	 */

	@Override
	public String save(FinanceType financeType, String type) {
		logger.debug("Entering");
		StringBuilder sql = new StringBuilder("Insert Into RMTFinanceTypes");
		sql.append(StringUtils.trimToEmpty(type));
		sql.append(
				"(FinType, Product, FinCategory,FinTypeDesc, FinCcy,  FinDaysCalType, FinAcType, FinContingentAcType,");
		sql.append(" FinBankContingentAcType, FinProvisionAcType,FinSuspAcType, FinIsGenRef,");
		sql.append(" FinMaxAmount, FinMinAmount,  FinIsOpenNewFinAc, FinDftStmtFrq,  FinIsAlwMD,");
		sql.append(" FinSchdMthd, FInIsAlwGrace, FinHistRetension, EqualRepayment, FinRateType,");
		sql.append(
				" FinBaseRate, FinSplRate, FinIntRate, FInMinRate, FinMaxRate,FinDftIntFrq,  FinIsIntCpz, FinCpzFrq,");
		sql.append(
				" FinIsRvwAlw, FinRvwFrq, FinGrcRateType, FinGrcBaseRate, FinGrcSplRate, FinGrcIntRate, FInGrcMinRate,");
		sql.append(
				" FinGrcMaxRate,FinGrcDftIntFrq,  FinGrcIsIntCpz, FinGrcCpzFrq,  FinGrcIsRvwAlw, FinGrcRvwFrq, FinMinTerm,");
		sql.append(
				" FinMaxTerm, FinDftTerms, FinRpyFrq,  finRepayMethod,FinIsAlwPartialRpy, FinIsAlwDifferment, FinMaxDifferment,");
		sql.append(" AlwPlanDeferment, PlanDeferCount,FinIsAlwEarlyRpy, FinIsAlwEarlySettle, FinODRpyTries, ");
		sql.append(" FinIsDwPayRequired, FinRvwRateApplFor,FinIsIntCpzAtGrcEnd, ");
		sql.append(" FinAlwRateChangeAnyDate, ");
		sql.append(
				" FinSchCalCodeOnRvw,FinAssetType ,FinDepositRestrictedTo,FinAEBuyOrInception,FinAESellOrMaturity, ");
		sql.append(
				" FinIsActive, PftPayAcType,FinIsOpenPftPayAcc	,Version , LastMntBy, LastMntOn, RecordStatus, RoleCode, NextRoleCode, TaskId, ");
		sql.append(" NextTaskId, RecordType, WorkflowId ,FinGrcSchdMthd,FinIsAlwGrcRepay,");
		sql.append("	FinCommitmentReq,FinCollateralReq,FinDepreciationReq,FinDepreciationFrq,");
		sql.append(" FinMargin,FinGrcMargin,FinScheduleOn,FinGrcScheduleOn, ");
		sql.append(" FinPftUnChanged ,ManualSchedule,");
		sql.append(
				"  AllowRIAInvestment , OverrideLimit, LimitRequired, FinCommitmentOvrride, FinCollateralOvrride, FinRepayPftOnFrq, ");
		sql.append(
				"  ApplyODPenalty , ODIncGrcDays , ODChargeType , ODGraceDays , ODChargeCalOn , ODChargeAmtOrPerc , ODAllowWaiver , ODMaxWaiverPerc, ODMinCapAmount, FinDivision, ");
		sql.append(
				"  StepFinance , SteppingMandatory , AlwManualSteps , AlwdStepPolicies, DftStepPolicy, StartDate, EndDate, ");
		sql.append(" AllowDownpayPgm, Remarks, AlwEarlyPayMethods ,ProductCategory, ");
		sql.append(" PastduePftCalMthd,PastduePftMargin,AlwAdvanceRent,");
		sql.append(
				" GrcAdvBaseRate , GrcAdvMargin , GrcAdvPftRate, RpyAdvBaseRate , RpyAdvMargin , RpyAdvPftRate , RollOverFinance, RollOverFrq,");
		sql.append(" DownPayRule, FinSuspTrigger, FinSuspRemarks, AlwMultiPartyDisb, TDSApplicable, CollateralType, ");
		sql.append(
				" ApplyGrcPricing, GrcPricingMethod, ApplyRpyPricing, RpyPricingMethod, RpyHierarchy, DroplineOD, DroppingMethod,RateChgAnyDay, ");
		sql.append(
				" AlwBPI , BpiTreatment , PftDueSchOn , PlanEMIHAlw , PlanEMIHMethod , PlanEMIHMaxPerYear , PlanEMIHMax ,AlwReage,AlwUnPlanEmiHoliday,QuickDisb, chequeCaptureReq, ");
		sql.append(
				" PlanEMIHLockPeriod , PlanEMICpz , UnPlanEMIHLockPeriod , UnPlanEMICpz , ReAgeCpz, FddLockPeriod, AlwdRpyMethods,MaxUnplannedEmi, MaxReAgeHolidays, ");
		sql.append(
				" RoundingMode,RoundingTarget, FrequencyDays,alwMaxDisbCheckReq, ProfitCenterID ,DeveloperFinance, CostOfFunds, FinLTVCheck, PartiallySecured, ");
		sql.append(" bpiPftDaysBasis, alwHybridRate, fixedRateTenor, eligibilityMethods,ODRuleCode,AlwZeroIntAcc, ");
		sql.append(" AutoRejectionDays, TaxNoMand , PutCallRequired  ");
		sql.append(", AdvIntersetReq, AdvType, AdvMinTerms, AdvMaxTerms, AdvDefaultTerms");
		sql.append(", GrcAdvIntersetReq, GrcAdvType, GrcAdvMinTerms, GrcAdvMaxTerms, GrcAdvDefaultTerms, AdvStage");
		sql.append(", DsfReq, CashCollateralReq ) ");

		sql.append(
				" Values(:FinType, :Product, :FinCategory,:FinTypeDesc, :FinCcy,  :FinDaysCalType, :FinAcType, :FinContingentAcType,");
		sql.append(" :FinBankContingentAcType, :FinProvisionAcType,:FinSuspAcType, :FinIsGenRef,");
		sql.append(" :FinMaxAmount, :FinMinAmount,  :FinIsOpenNewFinAc, :FinDftStmtFrq,  :FinIsAlwMD,");
		sql.append(" :FinSchdMthd, :FInIsAlwGrace, :FinHistRetension, :EqualRepayment, :FinRateType,");
		sql.append(
				" :FinBaseRate, :FinSplRate, :FinIntRate, :FInMinRate, :FinMaxRate,:FinDftIntFrq,  :FinIsIntCpz, :FinCpzFrq,");
		sql.append(
				" :FinIsRvwAlw, :FinRvwFrq, :FinGrcRateType, :FinGrcBaseRate, :FinGrcSplRate, :FinGrcIntRate, :FInGrcMinRate,");
		sql.append(
				" :FinGrcMaxRate,:FinGrcDftIntFrq,  :FinGrcIsIntCpz, :FinGrcCpzFrq,  :FinGrcIsRvwAlw, :FinGrcRvwFrq, :FinMinTerm,");
		sql.append(
				" :FinMaxTerm, :FinDftTerms, :FinRpyFrq,  :finRepayMethod,:FinIsAlwPartialRpy, :FinIsAlwDifferment, :FinMaxDifferment,");
		sql.append(" :AlwPlanDeferment, :PlanDeferCount,:FinIsAlwEarlyRpy, :FinIsAlwEarlySettle, :FinODRpyTries, ");
		sql.append(" :FinIsDwPayRequired, :FinRvwRateApplFor,:FinIsIntCpzAtGrcEnd, :FinAlwRateChangeAnyDate, ");
		sql.append(
				" :FinSchCalCodeOnRvw,:FinAssetType ,:FinDepositRestrictedTo,:FinAEBuyOrInception,:FinAESellOrMaturity, ");
		sql.append(
				" :FinIsActive, :PftPayAcType,:FinIsOpenPftPayAcc ,:Version , :LastMntBy, :LastMntOn, :RecordStatus, :RoleCode, :NextRoleCode, :TaskId, ");
		sql.append(" :NextTaskId, :RecordType, :WorkflowId ,:FinGrcSchdMthd,:FinIsAlwGrcRepay,");
		sql.append(" :FinCommitmentReq,:FinCollateralReq,:FinDepreciationReq,:FinDepreciationFrq,");
		sql.append(" :FinMargin,:FinGrcMargin,:FinScheduleOn,:FinGrcScheduleOn, ");
		sql.append(" :FinPftUnChanged ,:ManualSchedule,");
		sql.append(
				" :AllowRIAInvestment , :OverrideLimit, :LimitRequired, :FinCommitmentOvrride, :FinCollateralOvrride , :FinRepayPftOnFrq, ");
		sql.append(
				" :ApplyODPenalty , :ODIncGrcDays , :ODChargeType , :ODGraceDays , :ODChargeCalOn , :ODChargeAmtOrPerc , :ODAllowWaiver , :ODMaxWaiverPerc, :ODMinCapAmount, :FinDivision, ");
		sql.append(
				" :StepFinance , :SteppingMandatory , :AlwManualSteps , :AlwdStepPolicies, :DftStepPolicy, :StartDate, :EndDate, ");
		sql.append(" :AllowDownpayPgm, :Remarks, :AlwEarlyPayMethods ,:ProductCategory, ");
		sql.append(" :PastduePftCalMthd,:PastduePftMargin,:AlwAdvanceRent,");
		sql.append(
				" :GrcAdvBaseRate , :GrcAdvMargin , :GrcAdvPftRate, :RpyAdvBaseRate , :RpyAdvMargin , :RpyAdvPftRate , :RollOverFinance, :RollOverFrq,");
		sql.append(
				" :DownPayRule, :FinSuspTrigger, :FinSuspRemarks, :AlwMultiPartyDisb, :TDSApplicable, :CollateralType, ");
		sql.append(
				" :ApplyGrcPricing, :GrcPricingMethod, :ApplyRpyPricing, :RpyPricingMethod, :RpyHierarchy, :DroplineOD, :DroppingMethod, :RateChgAnyDay,");
		sql.append(
				" :AlwBPI , :BpiTreatment , :PftDueSchOn , :PlanEMIHAlw , :PlanEMIHMethod , :PlanEMIHMaxPerYear , :PlanEMIHMax , :AlwReage, :AlwUnPlanEmiHoliday, :QuickDisb, :chequeCaptureReq, ");
		sql.append(
				" :PlanEMIHLockPeriod , :PlanEMICpz , :UnPlanEMIHLockPeriod , :UnPlanEMICpz , :ReAgeCpz, :FddLockPeriod, :AlwdRpyMethods,:MaxUnplannedEmi, :MaxReAgeHolidays,  ");
		sql.append(
				" :RoundingMode,:RoundingTarget, :FrequencyDays,:AlwMaxDisbCheckReq, :ProfitCenterID, :DeveloperFinance, :CostOfFunds, :FinLTVCheck, :PartiallySecured, ");
		sql.append(
				" :bpiPftDaysBasis, :alwHybridRate, :fixedRateTenor, :eligibilityMethods, :ODRuleCode, :AlwZeroIntAcc,");
		sql.append(" :AutoRejectionDays, :TaxNoMand , :PutCallRequired");
		sql.append(", :AdvIntersetReq, :AdvType, :AdvMinTerms, :AdvMaxTerms, :AdvDefaultTerms");
		sql.append(
				", :GrcAdvIntersetReq, :GrcAdvType, :GrcAdvMinTerms, :GrcAdvMaxTerms, :GrcAdvDefaultTerms, :AdvStage");
		sql.append(", :DsfReq, :CashCollateralReq) ");

		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(financeType);
		financeType.getFinMaxAmount();
		this.jdbcTemplate.update(sql.toString(), beanParameters);

		logger.debug("Leaving");
		return financeType.getId();
	}

	/**
	 * This method updates the Record RMTFinanceTypes or RMTFinanceTypes_Temp.
	 * if Record not updated then throws DataAccessException with error 41004.
	 * update Finance Types by key FinType and Version
	 * 
	 * @param Finance
	 *            Types (financeType)
	 * @param type
	 *            (String) ""/_Temp/_View
	 * @return void
	 * @throws DataAccessException
	 * 
	 */

	@Override
	public void update(FinanceType financeType, String type) {
		int recordCount = 0;
		logger.debug("Entering");
		StringBuilder sql = new StringBuilder("Update RMTFinanceTypes");
		sql.append(StringUtils.trimToEmpty(type));
		sql.append(
				" Set Product = :Product,  FinTypeDesc = :FinTypeDesc, FinCategory =:FinCategory, FinCcy = :FinCcy,");
		sql.append(
				" FinDaysCalType = :FinDaysCalType,FinAcType = :FinAcType, FinContingentAcType = :FinContingentAcType,");
		sql.append(
				" FinBankContingentAcType= :FinBankContingentAcType, FinProvisionAcType= :FinProvisionAcType,FinSuspAcType=:FinSuspAcType,");
		sql.append(" FinIsGenRef = :FinIsGenRef, FinMaxAmount = :FinMaxAmount,FinMinAmount = :FinMinAmount,");
		sql.append(" FinIsOpenNewFinAc = :FinIsOpenNewFinAc, FinDftStmtFrq = :FinDftStmtFrq,FinIsAlwMD = :FinIsAlwMD,");
		sql.append(
				" FinSchdMthd = :FinSchdMthd, FInIsAlwGrace = :FInIsAlwGrace, FinHistRetension = :FinHistRetension,");
		sql.append(" EqualRepayment = :EqualRepayment,");
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
				" FinRvwRateApplFor = :FinRvwRateApplFor,FinIsIntCpzAtGrcEnd = :FinIsIntCpzAtGrcEnd,FinAlwRateChangeAnyDate = :FinAlwRateChangeAnyDate,  ");
		sql.append(
				" FinSchCalCodeOnRvw = :FinSchCalCodeOnRvw,FinAssetType=:FinAssetType,FinDepositRestrictedTo=:FinDepositRestrictedTo,");
		sql.append(
				" FinAEBuyOrInception=:FinAEBuyOrInception,FinAESellOrMaturity=:FinAESellOrMaturity,FinIsActive = :FinIsActive,");
		sql.append(
				" PftPayAcType=:PftPayAcType,FinIsOpenPftPayAcc=:FinIsOpenPftPayAcc,Version = :Version ,LastMntBy = :LastMntBy,LastMntOn = :LastMntOn,");
		sql.append(
				" RecordStatus= :RecordStatus, RoleCode = :RoleCode, NextRoleCode = :NextRoleCode, TaskId = :TaskId,");
		sql.append(
				" NextTaskId = :NextTaskId, RecordType = :RecordType, WorkflowId = :WorkflowId,FinGrcSchdMthd=:FinGrcSchdMthd, ");
		sql.append(
				" FinIsAlwGrcRepay=:FinIsAlwGrcRepay,FinScheduleOn=:FinScheduleOn,FinGrcScheduleOn=:FinGrcScheduleOn,");
		sql.append(
				" FinMargin=:FinMargin,FinGrcMargin=:FinGrcMargin,GrcAdvBaseRate=:GrcAdvBaseRate , GrcAdvMargin=:GrcAdvMargin , ");
		sql.append(
				" GrcAdvPftRate=:GrcAdvPftRate, RpyAdvBaseRate=:RpyAdvBaseRate , RpyAdvMargin=:RpyAdvMargin , RpyAdvPftRate=:RpyAdvPftRate,");
		sql.append(
				" FinCommitmentReq=:FinCommitmentReq ,FinCollateralReq=:FinCollateralReq ,FinDepreciationReq=:FinDepreciationReq,");
		sql.append(" FinDepreciationFrq=:FinDepreciationFrq ,");
		sql.append(" FinPftUnChanged=:FinPftUnChanged ,ManualSchedule = :ManualSchedule,");
		sql.append(" AllowRIAInvestment =:AllowRIAInvestment , OverrideLimit=:OverrideLimit, ");
		sql.append(
				" LimitRequired=:LimitRequired ,FinCommitmentOvrride=:FinCommitmentOvrride ,FinCollateralOvrride=:FinCollateralOvrride , FinRepayPftOnFrq =:FinRepayPftOnFrq, ");
		sql.append(
				" ApplyODPenalty =:ApplyODPenalty , ODIncGrcDays =:ODIncGrcDays, ODChargeType=:ODChargeType , ODGraceDays=:ODGraceDays , ");
		sql.append(
				" ODChargeCalOn=:ODChargeCalOn , ODChargeAmtOrPerc=:ODChargeAmtOrPerc , ODAllowWaiver=:ODAllowWaiver , ODMaxWaiverPerc=:ODMaxWaiverPerc, ODMinCapAmount=:ODMinCapAmount, FinDivision=:FinDivision, ");
		sql.append(
				" StepFinance=:StepFinance , SteppingMandatory=:SteppingMandatory , AlwManualSteps=:AlwManualSteps , AlwdStepPolicies=:AlwdStepPolicies , DftStepPolicy=:DftStepPolicy,");
		sql.append(" StartDate=:StartDate, EndDate=:EndDate, ");
		sql.append(" AllowDownpayPgm=:AllowDownpayPgm, Remarks=:Remarks, AlwEarlyPayMethods=:AlwEarlyPayMethods,");
		sql.append("  PastduePftCalMthd=:PastduePftCalMthd,PastduePftMargin=:PastduePftMargin,");
		sql.append(" AlwAdvanceRent=:AlwAdvanceRent, RollOverFinance=:RollOverFinance, RollOverFrq = :RollOverFrq, ");
		sql.append(" DownPayRule=:DownPayRule, ProductCategory=:ProductCategory,");
		sql.append(
				" FinSuspTrigger=:FinSuspTrigger, FinSuspRemarks=:FinSuspRemarks , AlwMultiPartyDisb = :AlwMultiPartyDisb , TDSApplicable=:TDSApplicable, CollateralType = :CollateralType, ");
		sql.append(
				" ApplyGrcPricing = :ApplyGrcPricing, GrcPricingMethod = :GrcPricingMethod, ApplyRpyPricing = :ApplyRpyPricing, RpyPricingMethod = :RpyPricingMethod, RpyHierarchy = :RpyHierarchy, ");
		sql.append(" DroplineOD = :DroplineOD, DroppingMethod = :DroppingMethod, RateChgAnyDay=:RateChgAnyDay,");
		sql.append(
				" AlwBPI=:AlwBPI , BpiTreatment=:BpiTreatment , PftDueSchOn=:PftDueSchOn , PlanEMIHAlw =:PlanEMIHAlw , PlanEMIHMethod =:PlanEMIHMethod , PlanEMIHMaxPerYear =:PlanEMIHMaxPerYear , PlanEMIHMax=:PlanEMIHMax , ");
		sql.append(
				" PlanEMIHLockPeriod=:PlanEMIHLockPeriod , PlanEMICpz=:PlanEMICpz , UnPlanEMIHLockPeriod=:UnPlanEMIHLockPeriod , UnPlanEMICpz=:UnPlanEMICpz ,AlwReage=:AlwReage,AlwUnPlanEmiHoliday=:AlwUnPlanEmiHoliday, ");
		sql.append(
				" ReAgeCpz=:ReAgeCpz, FddLockPeriod=:FddLockPeriod, AlwdRpyMethods=:AlwdRpyMethods, MaxUnplannedEmi=:MaxUnplannedEmi, MaxReAgeHolidays=:MaxReAgeHolidays, chequeCaptureReq = :chequeCaptureReq, ");
		sql.append(
				" RoundingMode=:RoundingMode ,RoundingTarget=:RoundingTarget, FrequencyDays=:FrequencyDays,AlwMaxDisbCheckReq=:AlwMaxDisbCheckReq,QuickDisb=:QuickDisb, ProfitCenterID = :ProfitCenterID, DeveloperFinance = :DeveloperFinance, CostOfFunds = :CostOfFunds,");
		sql.append(" FinLTVCheck = :FinLTVCheck, PartiallySecured = :PartiallySecured,");
		sql.append(" bpiPftDaysBasis = :bpiPftDaysBasis, eligibilityMethods = :eligibilityMethods,");
		sql.append(" alwHybridRate = :alwHybridRate, fixedRateTenor = :fixedRateTenor, ODRuleCode = :ODRuleCode, AlwZeroIntAcc = :AlwZeroIntAcc, ");
		sql.append(" AutoRejectionDays = :AutoRejectionDays, TaxNoMand = :TaxNoMand ,");
		sql.append(" PutCallRequired= :PutCallRequired ");

		sql.append(
				", GrcAdvIntersetReq= :GrcAdvIntersetReq, GrcAdvType= :GrcAdvType, GrcAdvMinTerms= :GrcAdvMinTerms, GrcAdvMaxTerms= :GrcAdvMaxTerms, GrcAdvDefaultTerms= :GrcAdvDefaultTerms");
		sql.append(
				", AdvIntersetReq= :AdvIntersetReq, AdvType= :AdvType, AdvMinTerms= :AdvMinTerms, AdvMaxTerms= :AdvMaxTerms, AdvDefaultTerms= :AdvDefaultTerms");
		sql.append(", AdvStage= :AdvStage, DsfReq= :DsfReq, CashCollateralReq= :CashCollateralReq");
		sql.append(" Where FinType =:FinType");

		if (!type.endsWith("_Temp")) {
			sql.append(" AND Version= :Version-1");
		}

		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(financeType);
		recordCount = this.jdbcTemplate.update(sql.toString(), beanParameters);

		if (recordCount <= 0) {
			throw new ConcurrencyException();
		}
		logger.debug("Leaving");
	}

	/**
	 * Method for get total number of records related to specific financeType
	 * 
	 * @param finType
	 */
	@Override
	public int getFinanceTypeCountById(String finType) {
		logger.debug("Entering");
		int productCount = 0;
		FinanceType financeType = new FinanceType();
		financeType.setFinType(finType);

		StringBuilder selectSql = new StringBuilder("SELECT COUNT(*) ");
		selectSql.append(" From RMTFinanceTypes ");
		selectSql.append(" Where FinType =:FinType AND (Product IS NULL OR Product = '')");

		logger.debug("selectSql: " + selectSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(financeType);

		try {
			productCount = this.jdbcTemplate.queryForObject(selectSql.toString(), beanParameters, Integer.class);
		} catch (EmptyResultDataAccessException dae) {
			logger.debug(dae);
			productCount = 0;
		}
		logger.debug("Leaving");
		return productCount;
	}

	/**
	 * Method for get total number of records related to specific
	 * financeType(Promotion)
	 * 
	 * @param finType
	 */
	@Override
	public int getPromotionTypeCountById(String finType) {
		logger.debug("Entering");
		int promotionCount = 0;
		FinanceType financeType = new FinanceType();
		financeType.setFinType(finType);

		StringBuilder selectSql = new StringBuilder("SELECT COUNT(*) ");
		selectSql.append(" From RMTFinanceTypes ");
		selectSql.append(" Where FinType =:FinType AND Product IS NOT NULL AND Product <> ' '");

		logger.debug("selectSql: " + selectSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(financeType);

		try {
			promotionCount = this.jdbcTemplate.queryForObject(selectSql.toString(), beanParameters, Integer.class);
		} catch (EmptyResultDataAccessException dae) {
			logger.debug(dae);
			promotionCount = 0;
		}

		logger.debug("Leaving");
		return promotionCount;
	}

	/**
	 * Fetch record count of product
	 * 
	 * @param productCode
	 * @return Integer
	 */
	@Override
	public int getProductCountById(String productCode) {
		logger.debug("Entering");

		int productCount = 0;
		FinanceType financeType = new FinanceType();
		financeType.setProduct(productCode);

		StringBuilder selectSql = new StringBuilder("SELECT COUNT(FinType) ");
		selectSql.append(" From RMTFinanceTypes Where Product =:Product");

		logger.debug("selectSql: " + selectSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(financeType);

		try {
			productCount = this.jdbcTemplate.queryForObject(selectSql.toString(), beanParameters, Integer.class);
		} catch (EmptyResultDataAccessException dae) {
			logger.debug(dae);
			productCount = 0;
		}
		logger.debug("Leaving");
		return productCount;
	}

	/**
	 * Method for Fetching Collateral Types based on Finance Type
	 */
	@Override
	public String getAllowedCollateralTypes(String finType) {
		logger.debug("Entering");

		String collateralTypes = "";
		FinanceType financeType = new FinanceType();
		financeType.setFinType(finType);

		StringBuilder selectSql = new StringBuilder("SELECT CollateralType ");
		selectSql.append(" From RMTFinanceTypes ");
		selectSql.append(" Where FinType =:FinType");

		logger.debug("selectSql: " + selectSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(financeType);

		try {
			collateralTypes = this.jdbcTemplate.queryForObject(selectSql.toString(), beanParameters, String.class);
		} catch (EmptyResultDataAccessException dae) {
			logger.debug(dae);
			collateralTypes = "";
		}
		logger.debug("Leaving");
		return collateralTypes;
	}

	/**
	 * Fetch the FinanceTypes Based on the Product Code
	 * 
	 * @param productCode
	 */
	@Override
	public List<FinanceType> getFinanceTypeByProduct(String productCode) {
		logger.debug("Entering");

		FinanceType financeType = new FinanceType();
		financeType.setFinCategory(productCode);
		StringBuilder selectSql = new StringBuilder();
		selectSql.append("SELECT FinType, FinTypeDesc");
		selectSql.append(" From RMTFinanceTypes ");
		selectSql.append(" Where FinCategory =:FinCategory");

		logger.debug("selectSql: " + selectSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(financeType);
		RowMapper<FinanceType> typeRowMapper = ParameterizedBeanPropertyRowMapper.newInstance(FinanceType.class);
		List<FinanceType> finacetypeList = new ArrayList<>();
		try {
			finacetypeList = this.jdbcTemplate.query(selectSql.toString(), beanParameters, typeRowMapper);
		} catch (EmptyResultDataAccessException dae) {
			logger.error(dae);
			return Collections.emptyList();
		}

		logger.debug("Leaving");
		return finacetypeList;
	}

	/**
	 * Method for fetch financeType description
	 * 
	 * @param productCode
	 * @return String
	 */
	@Override
	public String getFinanceTypeDesc(String productCode) {
		logger.debug("Entering");

		String finTypeDesc = "";
		FinanceType financeType = new FinanceType();
		financeType.setFinType(productCode);

		StringBuilder selectSql = new StringBuilder("SELECT FinTypeDesc ");
		selectSql.append(" From RMTFinanceTypes ");
		selectSql.append(" Where FinType =:FinType");

		logger.debug("selectSql: " + selectSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(financeType);

		try {
			finTypeDesc = this.jdbcTemplate.queryForObject(selectSql.toString(), beanParameters, String.class);
		} catch (EmptyResultDataAccessException dae) {
			logger.debug(dae);
			finTypeDesc = "";
		}
		logger.debug("Leaving");
		return finTypeDesc;
	}

	@Override
	public int getFinTypeCount(String finType, String type) {
		logger.debug("Entering");

		MapSqlParameterSource source = null;
		int count = 0;

		StringBuilder selectSql = new StringBuilder("Select Count(*) From RMTFinanceTypes");
		selectSql.append(StringUtils.trimToEmpty(type));
		selectSql.append(" Where FinType = :FinType");
		logger.debug("selectSql: " + selectSql.toString());

		source = new MapSqlParameterSource();
		source.addValue("FinType", finType);

		try {
			count = this.jdbcTemplate.queryForObject(selectSql.toString(), source, Integer.class);
		} catch (DataAccessException e) {
			logger.error(e);
		}

		logger.debug("Leaving");

		return count;
	}

	@Override
	public int getFinanceTypeByRuleCode(long ruleId, String type) {
		logger.debug("Entering");
		FinanceType financeType = new FinanceType();
		financeType.setDownPayRule(ruleId);

		StringBuilder selectSql = new StringBuilder("SELECT COUNT(*)");
		selectSql.append(" From RMTFinanceTypes");
		selectSql.append(StringUtils.trimToEmpty(type));
		selectSql.append(" Where DownPayRule =:DownPayRule");

		logger.debug("selectSql: " + selectSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(financeType);

		logger.debug("Leaving");
		return this.jdbcTemplate.queryForObject(selectSql.toString(), beanParameters, Integer.class);
	}

	/**
	 * Method for Checking Step Policy Code is already using in Existing FinType
	 * or not
	 */
	@Override
	public boolean isStepPolicyExists(String policyCode) {
		logger.debug("Entering");

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

		logger.debug("Leaving");
		return rcdCount > 0 ? true : false;
	}

	/**
	 * Method for get total number of records from RMtFianceTypes master
	 * table.<br>
	 * 
	 * @param divisionCode
	 * 
	 * @return Boolean
	 */
	@Override
	public boolean isDivisionCodeExistsInFinanceTypes(String divisionCode, String type) {
		logger.debug("Entering");

		MapSqlParameterSource source = new MapSqlParameterSource();
		source.addValue("FINDIVISION", divisionCode);

		StringBuffer selectSql = new StringBuffer();
		selectSql.append("SELECT COUNT(*) FROM RMTFINANCETYPES");
		selectSql.append(StringUtils.trimToEmpty(type));
		selectSql.append(" WHERE FINDIVISION= :FINDIVISION");

		logger.debug("insertSql: " + selectSql.toString());
		int recordCount = 0;
		try {
			recordCount = this.jdbcTemplate.queryForObject(selectSql.toString(), source, Integer.class);
		} catch (EmptyResultDataAccessException dae) {
			logger.debug("Exception: ", dae);
			recordCount = 0;
		}
		logger.debug("Leaving");

		return recordCount > 0 ? true : false;
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
		logger.debug("Entering");
		FinanceType financeType = new FinanceType();
		financeType.setFinType(finType);

		StringBuilder selectSql = new StringBuilder(
				"SELECT FinType, FinLTVCheck, FinCollateralReq, CollateralType, PartiallySecured, RecordType");
		selectSql.append(" FROM RMTFinanceTypes");
		selectSql.append(" Where FinType = :FinType");

		logger.debug("selectListSql: " + selectSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(financeType);
		RowMapper<FinanceType> typeRowMapper = ParameterizedBeanPropertyRowMapper.newInstance(FinanceType.class);

		try {
			financeType = this.jdbcTemplate.queryForObject(selectSql.toString(), beanParameters, typeRowMapper);
		} catch (EmptyResultDataAccessException e) {
			logger.warn("Exception: ", e);
			financeType = null;
		}
		logger.debug("Leaving");
		return financeType;
	}

	@Override
	public String getAllowedRepayMethods(String finType, String type) {

		logger.debug("Entering");

		String finTypeDesc = "";
		FinanceType financeType = new FinanceType();
		financeType.setFinType(finType);

		StringBuilder selectSql = new StringBuilder("SELECT AlwdRpyMethods ");
		selectSql.append(" From RMTFinanceTypes ");
		selectSql.append(" Where FinType =:FinType");

		logger.debug("selectSql: " + selectSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(financeType);

		try {
			finTypeDesc = this.jdbcTemplate.queryForObject(selectSql.toString(), beanParameters, String.class);
		} catch (EmptyResultDataAccessException dae) {
			logger.debug(dae);
			finTypeDesc = "";
		}
		logger.debug("Leaving");
		return finTypeDesc;

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
		RowMapper<FinanceType> typeRowMapper = ParameterizedBeanPropertyRowMapper.newInstance(FinanceType.class);
		List<FinanceType> finTypes = this.jdbcTemplate.query(sql.toString(), typeRowMapper);
		logger.debug(Literal.LEAVING);
		return finTypes;
	}
}