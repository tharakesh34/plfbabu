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

import javax.sql.DataSource;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.core.simple.ParameterizedBeanPropertyRowMapper;

import com.pennant.backend.dao.impl.BasisCodeDAO;
import com.pennant.backend.dao.rmtmasters.FinanceTypeDAO;
import com.pennant.backend.model.rmtmasters.FinanceType;
import com.pennant.backend.util.PennantJavaUtil;
import com.pennanttech.pennapps.core.ConcurrencyException;
import com.pennanttech.pff.core.DependencyFoundException;

/**
 * DAO methods implementation for the <b>FinanceType model</b> class.<br>
 * 
 */
public class FinanceTypeDAOImpl extends BasisCodeDAO<FinanceType> implements FinanceTypeDAO {

	private static Logger logger = Logger.getLogger(FinanceTypeDAOImpl.class);

	// Spring Named JDBC Template
	private NamedParameterJdbcTemplate	namedParameterJdbcTemplate;

	public FinanceTypeDAOImpl() {
		super();
	}
	
	/**
	 * Fetch the Record Finance Types details by key field
	 * 
	 * @param id
	 *         (String)
	 * @param type
	 *         (String) ""/_Temp/_View
	 * @return FinanceType
	 */
	@Override
	public FinanceType getFinanceTypeByID(final String id, String type) {
		logger.debug("Entering");
		FinanceType financeType = new FinanceType();
		financeType.setId(id);

		StringBuilder selectSql = new StringBuilder("SELECT FinType, Product,FinCategory, FinTypeDesc, FinCcy, FinDaysCalType,");
		selectSql.append(" FinAcType, FinContingentAcType, FinBankContingentAcType, FinProvisionAcType,FinSuspAcType,");
		selectSql.append(" FinIsGenRef, FinMaxAmount, FinMinAmount,FinIsOpenNewFinAc, FinDftStmtFrq,FinIsAlwMD, ");
		selectSql.append(" FinSchdMthd, FInIsAlwGrace,FinHistRetension, EqualRepayment,FinRateType, ");
		selectSql.append(" FinBaseRate,FinSplRate,FinIntRate, FInMinRate, FinMaxRate, FinDftIntFrq,  FinIsIntCpz,");
		selectSql.append(" FinCpzFrq,  FinIsRvwAlw, FinRvwFrq,  FinGrcRateType, FinGrcBaseRate,");
		selectSql.append(" FinGrcSplRate, FinGrcIntRate, FInGrcMinRate, FinGrcMaxRate,FinGrcDftIntFrq,");
		selectSql.append(" FinGrcIsIntCpz, FinGrcCpzFrq, FinGrcIsRvwAlw, FinGrcRvwFrq, FinMinTerm,");
		selectSql.append(" FinMaxTerm, FinDftTerms, FinRpyFrq,  finRepayMethod, FinIsAlwPartialRpy,");
		selectSql.append(" FinIsAlwDifferment,FinMaxDifferment, FinIsAlwEarlyRpy, FinIsAlwEarlySettle,");
		selectSql.append(" FinODRpyTries, AlwPlanDeferment,PlanDeferCount ,");
		selectSql.append(" FinIsDwPayRequired,  FinRvwRateApplFor, FinAlwRateChangeAnyDate, FinIsIntCpzAtGrcEnd,");
		selectSql.append(" FinSchCalCodeOnRvw, FinAssetType , FinDepositRestrictedTo,FinAEBuyOrInception,FinAESellOrMaturity,FinIsActive,PftPayAcType,");
		selectSql.append(" FinIsOpenPftPayAcc,FinGrcSchdMthd,FinIsAlwGrcRepay,FinMargin,FinGrcMargin,");
		selectSql.append(" FinScheduleOn,FinGrcScheduleOn,FinCommitmentReq,FinCollateralReq,");
		selectSql.append(" FinDepreciationReq,FinDepreciationFrq, ");
		selectSql.append(" AllowRIAInvestment , OverrideLimit , LimitRequired ,");
		selectSql.append(" FinCommitmentOvrride , FinCollateralOvrride ,FinRepayPftOnFrq, FinPftUnChanged,ManualSchedule, ");
		selectSql.append(" ApplyODPenalty , ODIncGrcDays , ODChargeType , ODGraceDays , ODChargeCalOn , ODChargeAmtOrPerc , ODAllowWaiver , ODMaxWaiverPerc,FinDivision, ");
		selectSql.append(" StepFinance , SteppingMandatory , AlwManualSteps , AlwdStepPolicies, DftStepPolicy, StartDate, EndDate,");
		selectSql.append(" AllowDownpayPgm,Remarks,AlwEarlyPayMethods,");
		selectSql.append(" PastduePftCalMthd,PastduePftMargin,AlwAdvanceRent,");
		selectSql.append(" GrcAdvBaseRate , GrcAdvMargin , GrcAdvPftRate, RpyAdvBaseRate , RpyAdvMargin , RpyAdvPftRate, RollOverFinance,RollOverFrq,");
		selectSql.append(" DownPayRule, FinSuspTrigger, FinSuspRemarks, AlwMultiPartyDisb, TDSApplicable, CollateralType, ");
		selectSql.append(" ApplyGrcPricing, GrcPricingMethod, ApplyRpyPricing, RpyPricingMethod, RpyHierarchy, DroplineOD, DroppingMethod ,RateChgAnyDay, ");
		selectSql.append(" AlwBPI , BpiTreatment , PftDueSchOn , PlanEMIHAlw , PlanEMIHMethod , PlanEMIHMaxPerYear , PlanEMIHMax , ");
		selectSql.append(" PlanEMIHLockPeriod , PlanEMICpz , UnPlanEMIHLockPeriod , UnPlanEMICpz , ReAgeCpz, FddLockPeriod, AlwdRpyMethods,AlwReage,AlwUnPlanEmiHoliday, ");
		selectSql.append(" MaxUnplannedEmi, MaxReAgeHolidays, RoundingMode, RoundingTarget, FrequencyDays,alwMaxDisbCheckReq,quickDisb, ProfitCenterID, ProductCategory,");
		
		if (type.contains("View")) {
			selectSql.append(" FinCategoryDesc, DownPayRuleDesc, lovDescFinContingentAcTypeName,lovDescFinBankContAcTypeName,lovDescFinProvisionAcTypeName,lovDescFinAcTypeName,");
			selectSql.append(" lovDescPftPayAcTypeName,lovDescFinSuspAcTypeName, lovDescFinDivisionName,lovDescPromoFinTypeDesc, ProfitCenterCode, ProfitCenterDesc, ");
		}

		selectSql.append(" Version, LastMntBy, LastMntOn, RecordStatus,");
		selectSql.append(" RoleCode, NextRoleCode, TaskId, NextTaskId, RecordType, WorkflowId");

		selectSql.append(" FROM RMTFinanceTypes");
		selectSql.append(StringUtils.trimToEmpty(type));
		selectSql.append(" Where FinType = :FinType");

		logger.debug("selectListSql: " + selectSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(financeType);
		RowMapper<FinanceType> typeRowMapper = ParameterizedBeanPropertyRowMapper.newInstance(FinanceType.class);

		try {
			financeType = this.namedParameterJdbcTemplate.queryForObject(selectSql.toString(), beanParameters, typeRowMapper);
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
	 *         (String)
	 * @param type
	 *         (String) ""/_Temp/_View
	 * @return FinanceType
	 */
	@Override
	public FinanceType getOrgFinanceTypeByID(final String finType, String type) {
		logger.debug("Entering");
		FinanceType financeType = new FinanceType();
		financeType.setId(finType);

		StringBuilder selectSql = new StringBuilder();
		selectSql.append(" Select FinType, FinCategory, FinTypeDesc, FinCcy, FinDaysCalType, FinAcType, FinContingentAcType, ");
		selectSql.append(" FinIsGenRef, FinMaxAmount, FinMinAmount, FinIsOpenNewFinAc, FinIsAlwMD, FinSchdMthd, FInIsAlwGrace, ");
		selectSql.append(" EqualRepayment, FinRateType, FinBaseRate, FinSplRate, FinIntRate, FInMinRate, FinMaxRate, ");
		selectSql.append(" FinDftIntFrq, FinIsIntCpz, FinCpzFrq, FinIsRvwAlw, FinRvwFrq, FinGrcRateType, FinGrcBaseRate, ");
		selectSql.append(" FinGrcSplRate, FinGrcIntRate, FInGrcMinRate, FinGrcMaxRate, FinGrcDftIntFrq, FinGrcIsIntCpz, ");
		selectSql.append(" FinGrcCpzFrq, FinGrcIsRvwAlw, FinGrcRvwFrq, FinMinTerm, FinMaxTerm, FinDftTerms, FinRpyFrq, ");
		selectSql.append(" FInRepayMethod, FinIsAlwDifferment, FinMaxDifferment, FinIsActive, StepFinance, SteppingMandatory, ");
		selectSql.append(" AlwManualSteps, AlwdStepPolicies, DftStepPolicy, FinIsDwPayRequired, FinRvwRateApplFor, ");
		selectSql.append(" FinAlwRateChangeAnyDate,  FinIsIntCpzAtGrcEnd, FinSchCalCodeOnRvw, AlwPlanDeferment, ");
		selectSql.append(" PlanDeferCount, PftPayAcType, FinIsOpenPftPayAcc, FinIsAlwGrcRepay, FinGrcSchdMthd, FinGrcMargin, ");
		selectSql.append(" FinMargin, FinCommitmentReq, FinCollateralReq, FinDepreciationReq, FinDepreciationFrq, ");
		selectSql.append(" FinBankContingentAcType, FinProvisionAcType, AllowRIAInvestment, OverrideLimit, LimitRequired, ");
		selectSql.append(" FinCommitmentOvrride, FinCollateralOvrride, FinRepayPftOnFrq, FinPftUnChanged, ApplyODPenalty, ");
		selectSql.append(" ODIncGrcDays, ODChargeType, ODGraceDays, ODChargeCalOn, ODChargeAmtOrPerc, ODAllowWaiver, ");
		selectSql.append(" ODMaxWaiverPerc, FinDivision, FinSuspAcType, Product, StartDate, EndDate, AllowDownpayPgm, ");
		selectSql.append(" PastduePftCalMthd,  AlwAdvanceRent, GrcAdvBaseRate, GrcAdvMargin, GrcAdvPftRate, RpyAdvBaseRate, ");
		selectSql.append(" RpyAdvMargin, RpyAdvPftRate, RollOverFinance, RollOverFrq, DownPayRule, AlwMultiPartyDisb, ");
		selectSql.append(" CollateralType, TDSApplicable,ApplyGrcPricing, ApplyRpyPricing, DroplineOD, DroppingMethod, ");
		selectSql.append(" RateChgAnyDay, ManualSchedule, AlwBPI , BpiTreatment , PftDueSchOn , ");
		selectSql.append(" PlanEMIHAlw , PlanEMIHMethod , PlanEMIHMaxPerYear , PlanEMIHMax ,PlanEMIHLockPeriod , PlanEMICpz , ");
		selectSql.append(" UnPlanEMIHLockPeriod , UnPlanEMICpz , ReAgeCpz, FddLockPeriod, AlwdRpyMethods, MaxUnplannedEmi, ");
		selectSql.append(" MaxReAgeHolidays, RoundingMode, RoundingTarget, FrequencyDays,AlwReage,AlwUnPlanEmiHoliday,alwMaxDisbCheckReq,quickDisb,ProductCategory ");
		if(type.contains("ORGView")){
			selectSql.append(" ,DownPayRuleDesc, LovDescFinDivisionName , lovDescPromoFinTypeDesc, lovDescDftStepPolicyName, ");
			selectSql.append(" GrcPricingMethodDesc, RpyPricingMethodDesc, DftStepPolicyType, RpyHierarchy");
		}
		selectSql.append(" FROM RMTFinanceTypes");
		selectSql.append(StringUtils.trimToEmpty(type));
		selectSql.append(" Where FinType = :FinType");

		logger.debug("selectListSql: " + selectSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(financeType);
		RowMapper<FinanceType> typeRowMapper = ParameterizedBeanPropertyRowMapper.newInstance(FinanceType.class);

		try {
			financeType = this.namedParameterJdbcTemplate.queryForObject(selectSql.toString(), beanParameters, typeRowMapper);
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
	 *         (String)
	 * @param type
	 *         (String) ""/_Temp/_View
	 * @return FinanceType
	 */
	@Override
	public FinanceType getFinanceTypeByFinType(final String finType) {
		logger.debug("Entering");
		FinanceType financeType = new FinanceType();
		financeType.setFinType(finType);

		StringBuilder selectSql = new StringBuilder("SELECT FinType, Product, FinAcType, FinCategory, FinDivision, " );
		selectSql.append(" FinIsOpenNewFinAc, PftPayAcType,  FinSuspAcType, FinProvisionAcType , " );
		selectSql.append(" AllowRIAInvestment, FinIsAlwPartialRpy, FinSuspTrigger, FinSuspRemarks,  " );
		selectSql.append(" PastduePftCalMthd, PastduePftMargin,alwMultiPartyDisb, alwMaxDisbCheckReq " );
		selectSql.append(" FROM RMTFinanceTypes");
		selectSql.append(" Where FinType = :FinType");

		logger.debug("selectListSql: " + selectSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(financeType);
		RowMapper<FinanceType> typeRowMapper = ParameterizedBeanPropertyRowMapper.newInstance(FinanceType.class);

		try {
			financeType = this.namedParameterJdbcTemplate.queryForObject(selectSql.toString(), beanParameters, typeRowMapper);
		} catch (EmptyResultDataAccessException e) {
			logger.warn("Exception: ", e);
			financeType = null;
		}
		logger.debug("Leaving");
		return financeType;
	}
	
	/**
	 * Method for Fetch Finance Type List
	 * @return 
	 */
	@Override
    public List<FinanceType> getFinTypeDetailForBatch() {
		logger.debug("Entering");

		StringBuilder selectSql = new StringBuilder("SELECT FinType, Product, FinAcType, FinCategory, FinDivision, " );
		selectSql.append(" FinIsOpenNewFinAc, PftPayAcType,  FinSuspAcType, FinProvisionAcType , " );
		selectSql.append(" AllowRIAInvestment, FinIsAlwPartialRpy, FinSuspTrigger, FinSuspRemarks,  " );
		selectSql.append(" PastduePftCalMthd, PastduePftMargin,RpyHierarchy " );
		selectSql.append(" FROM RMTFinanceTypes");

		logger.debug("selectListSql: " + selectSql.toString());
		RowMapper<FinanceType> typeRowMapper = ParameterizedBeanPropertyRowMapper.newInstance(FinanceType.class);

		List<FinanceType> finTypes = this.namedParameterJdbcTemplate.query(selectSql.toString(), typeRowMapper); 
		logger.debug("Leaving");
		return finTypes;
    }

	/**
	 * @param dataSource
	 *         the dataSource to set
	 */

	public void setDataSource(DataSource dataSource) {
		this.namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
	}

	/**
	 * This method Deletes the Record from the RMTFinanceTypes or RMTFinanceTypes_Temp. if Record not
	 * deleted then throws DataAccessException with error 41003. delete Finance Types by key FinType
	 * 
	 * @param Finance
	 *         Types (financeType)
	 * @param type
	 *         (String) ""/_Temp/_View
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
			recordCount = this.namedParameterJdbcTemplate.update(deleteSql, beanParameters);

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
	 * This method insert new Records into RMTFinanceTypes or RMTFinanceTypes_Temp.
	 * 
	 * save Finance Types
	 * 
	 * @param Finance
	 *         Types (financeType)
	 * @param type
	 *         (String) ""/_Temp/_View
	 * @return void
	 * @throws DataAccessException
	 * 
	 */

	@Override
	public String save(FinanceType financeType, String type) {
		logger.debug("Entering");
		StringBuilder insertSql = new StringBuilder("Insert Into RMTFinanceTypes");
		insertSql.append(StringUtils.trimToEmpty(type));
		insertSql.append("(FinType, Product, FinCategory,FinTypeDesc, FinCcy,  FinDaysCalType, FinAcType, FinContingentAcType,"); 
		insertSql.append(" FinBankContingentAcType, FinProvisionAcType,FinSuspAcType, FinIsGenRef,");
		insertSql.append(" FinMaxAmount, FinMinAmount,  FinIsOpenNewFinAc, FinDftStmtFrq,  FinIsAlwMD,");
		insertSql.append(" FinSchdMthd, FInIsAlwGrace, FinHistRetension, EqualRepayment, FinRateType,");
		insertSql.append(" FinBaseRate, FinSplRate, FinIntRate, FInMinRate, FinMaxRate,FinDftIntFrq,  FinIsIntCpz, FinCpzFrq,");
		insertSql.append(" FinIsRvwAlw, FinRvwFrq, FinGrcRateType, FinGrcBaseRate, FinGrcSplRate, FinGrcIntRate, FInGrcMinRate,");
		insertSql.append(" FinGrcMaxRate,FinGrcDftIntFrq,  FinGrcIsIntCpz, FinGrcCpzFrq,  FinGrcIsRvwAlw, FinGrcRvwFrq, FinMinTerm,");
		insertSql.append(" FinMaxTerm, FinDftTerms, FinRpyFrq,  finRepayMethod,FinIsAlwPartialRpy, FinIsAlwDifferment, FinMaxDifferment,");
		insertSql.append(" AlwPlanDeferment, PlanDeferCount,FinIsAlwEarlyRpy, FinIsAlwEarlySettle, FinODRpyTries, ");
		insertSql.append(" FinIsDwPayRequired, FinRvwRateApplFor,FinIsIntCpzAtGrcEnd, ");
		insertSql.append(" FinAlwRateChangeAnyDate, ");
		insertSql.append(" FinSchCalCodeOnRvw,FinAssetType ,FinDepositRestrictedTo,FinAEBuyOrInception,FinAESellOrMaturity, ");
		insertSql.append(" FinIsActive, PftPayAcType,FinIsOpenPftPayAcc	,Version , LastMntBy, LastMntOn, RecordStatus, RoleCode, NextRoleCode, TaskId, ");
		insertSql.append(" NextTaskId, RecordType, WorkflowId ,FinGrcSchdMthd,FinIsAlwGrcRepay,");
		insertSql.append("	FinCommitmentReq,FinCollateralReq,FinDepreciationReq,FinDepreciationFrq,");
		insertSql.append(" FinMargin,FinGrcMargin,FinScheduleOn,FinGrcScheduleOn, ");
		insertSql.append(" FinPftUnChanged ,ManualSchedule,");
		insertSql.append("  AllowRIAInvestment , OverrideLimit, LimitRequired, FinCommitmentOvrride, FinCollateralOvrride, FinRepayPftOnFrq, ");
		insertSql.append("  ApplyODPenalty , ODIncGrcDays , ODChargeType , ODGraceDays , ODChargeCalOn , ODChargeAmtOrPerc , ODAllowWaiver , ODMaxWaiverPerc, FinDivision, ");
		insertSql.append("  StepFinance , SteppingMandatory , AlwManualSteps , AlwdStepPolicies, DftStepPolicy, StartDate, EndDate, ");
		insertSql.append(" AllowDownpayPgm, Remarks, AlwEarlyPayMethods ,ProductCategory, ");
		insertSql.append(" PastduePftCalMthd,PastduePftMargin,AlwAdvanceRent,");
		insertSql.append(" GrcAdvBaseRate , GrcAdvMargin , GrcAdvPftRate, RpyAdvBaseRate , RpyAdvMargin , RpyAdvPftRate , RollOverFinance, RollOverFrq,");
		insertSql.append(" DownPayRule, FinSuspTrigger, FinSuspRemarks, AlwMultiPartyDisb, TDSApplicable, CollateralType, ");
		insertSql.append(" ApplyGrcPricing, GrcPricingMethod, ApplyRpyPricing, RpyPricingMethod, RpyHierarchy, DroplineOD, DroppingMethod,RateChgAnyDay, ");
		insertSql.append(" AlwBPI , BpiTreatment , PftDueSchOn , PlanEMIHAlw , PlanEMIHMethod , PlanEMIHMaxPerYear , PlanEMIHMax ,AlwReage,AlwUnPlanEmiHoliday,QuickDisb, ");
		insertSql.append(" PlanEMIHLockPeriod , PlanEMICpz , UnPlanEMIHLockPeriod , UnPlanEMICpz , ReAgeCpz, FddLockPeriod, AlwdRpyMethods,MaxUnplannedEmi, MaxReAgeHolidays, RoundingMode,RoundingTarget, FrequencyDays,alwMaxDisbCheckReq, ProfitCenterID) ");
		insertSql.append(" Values(:FinType, :Product, :FinCategory,:FinTypeDesc, :FinCcy,  :FinDaysCalType, :FinAcType, :FinContingentAcType,"); 
		insertSql.append(" :FinBankContingentAcType, :FinProvisionAcType,:FinSuspAcType, :FinIsGenRef,");
		insertSql.append(" :FinMaxAmount, :FinMinAmount,  :FinIsOpenNewFinAc, :FinDftStmtFrq,  :FinIsAlwMD,");
		insertSql.append(" :FinSchdMthd, :FInIsAlwGrace, :FinHistRetension, :EqualRepayment, :FinRateType,");
		insertSql.append(" :FinBaseRate, :FinSplRate, :FinIntRate, :FInMinRate, :FinMaxRate,:FinDftIntFrq,  :FinIsIntCpz, :FinCpzFrq,");
		insertSql.append(" :FinIsRvwAlw, :FinRvwFrq, :FinGrcRateType, :FinGrcBaseRate, :FinGrcSplRate, :FinGrcIntRate, :FInGrcMinRate,");
		insertSql.append(" :FinGrcMaxRate,:FinGrcDftIntFrq,  :FinGrcIsIntCpz, :FinGrcCpzFrq,  :FinGrcIsRvwAlw, :FinGrcRvwFrq, :FinMinTerm,");
		insertSql.append(" :FinMaxTerm, :FinDftTerms, :FinRpyFrq,  :finRepayMethod,:FinIsAlwPartialRpy, :FinIsAlwDifferment, :FinMaxDifferment,");
		insertSql.append(" :AlwPlanDeferment, :PlanDeferCount,:FinIsAlwEarlyRpy, :FinIsAlwEarlySettle, :FinODRpyTries, ");
		insertSql.append(" :FinIsDwPayRequired, :FinRvwRateApplFor,:FinIsIntCpzAtGrcEnd, :FinAlwRateChangeAnyDate, ");
		insertSql.append(" :FinSchCalCodeOnRvw,:FinAssetType ,:FinDepositRestrictedTo,:FinAEBuyOrInception,:FinAESellOrMaturity, ");
		insertSql.append(" :FinIsActive, :PftPayAcType,:FinIsOpenPftPayAcc ,:Version , :LastMntBy, :LastMntOn, :RecordStatus, :RoleCode, :NextRoleCode, :TaskId, ");
		insertSql.append(" :NextTaskId, :RecordType, :WorkflowId ,:FinGrcSchdMthd,:FinIsAlwGrcRepay,");
		insertSql.append(" :FinCommitmentReq,:FinCollateralReq,:FinDepreciationReq,:FinDepreciationFrq,");
		insertSql.append(" :FinMargin,:FinGrcMargin,:FinScheduleOn,:FinGrcScheduleOn, ");
		insertSql.append(" :FinPftUnChanged ,:ManualSchedule,");
		insertSql.append(" :AllowRIAInvestment , :OverrideLimit, :LimitRequired, :FinCommitmentOvrride, :FinCollateralOvrride , :FinRepayPftOnFrq, ");
		insertSql.append(" :ApplyODPenalty , :ODIncGrcDays , :ODChargeType , :ODGraceDays , :ODChargeCalOn , :ODChargeAmtOrPerc , :ODAllowWaiver , :ODMaxWaiverPerc, :FinDivision, ");
		insertSql.append(" :StepFinance , :SteppingMandatory , :AlwManualSteps , :AlwdStepPolicies, :DftStepPolicy, :StartDate, :EndDate, ");
		insertSql.append(" :AllowDownpayPgm, :Remarks, :AlwEarlyPayMethods ,:ProductCategory, ");
		insertSql.append(" :PastduePftCalMthd,:PastduePftMargin,:AlwAdvanceRent,");
		insertSql.append(" :GrcAdvBaseRate , :GrcAdvMargin , :GrcAdvPftRate, :RpyAdvBaseRate , :RpyAdvMargin , :RpyAdvPftRate , :RollOverFinance, :RollOverFrq,");
		insertSql.append(" :DownPayRule, :FinSuspTrigger, :FinSuspRemarks, :AlwMultiPartyDisb, :TDSApplicable, :CollateralType, ");
		insertSql.append(" :ApplyGrcPricing, :GrcPricingMethod, :ApplyRpyPricing, :RpyPricingMethod, :RpyHierarchy, :DroplineOD, :DroppingMethod, :RateChgAnyDay,");
		insertSql.append(" :AlwBPI , :BpiTreatment , :PftDueSchOn , :PlanEMIHAlw , :PlanEMIHMethod , :PlanEMIHMaxPerYear , :PlanEMIHMax , :AlwReage, :AlwUnPlanEmiHoliday, :QuickDisb, ");
		insertSql.append(" :PlanEMIHLockPeriod , :PlanEMICpz , :UnPlanEMIHLockPeriod , :UnPlanEMICpz , :ReAgeCpz, :FddLockPeriod, :AlwdRpyMethods,:MaxUnplannedEmi, :MaxReAgeHolidays, :RoundingMode,:RoundingTarget, :FrequencyDays,:AlwMaxDisbCheckReq, :ProfitCenterID) ");
		
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(financeType);
		financeType.getFinMaxAmount();
		this.namedParameterJdbcTemplate.update(insertSql.toString(), beanParameters);

		logger.debug("Leaving");
		return financeType.getId();
	}

	/**
	 * This method updates the Record RMTFinanceTypes or RMTFinanceTypes_Temp. if Record not updated
	 * then throws DataAccessException with error 41004. update Finance Types by key FinType and
	 * Version
	 * 
	 * @param Finance
	 *         Types (financeType)
	 * @param type
	 *         (String) ""/_Temp/_View
	 * @return void
	 * @throws DataAccessException
	 * 
	 */

	@Override
	public void update(FinanceType financeType, String type) {
		int recordCount = 0;
		logger.debug("Entering");
		StringBuilder updateSql = new StringBuilder("Update RMTFinanceTypes");
		updateSql.append(StringUtils.trimToEmpty(type));
		updateSql.append(" Set Product = :Product,  FinTypeDesc = :FinTypeDesc, FinCategory =:FinCategory, FinCcy = :FinCcy,");
		updateSql.append(" FinDaysCalType = :FinDaysCalType,FinAcType = :FinAcType, FinContingentAcType = :FinContingentAcType,");
		updateSql.append(" FinBankContingentAcType= :FinBankContingentAcType, FinProvisionAcType= :FinProvisionAcType,FinSuspAcType=:FinSuspAcType,");
		updateSql.append(" FinIsGenRef = :FinIsGenRef, FinMaxAmount = :FinMaxAmount,FinMinAmount = :FinMinAmount,");
		updateSql.append(" FinIsOpenNewFinAc = :FinIsOpenNewFinAc, FinDftStmtFrq = :FinDftStmtFrq,FinIsAlwMD = :FinIsAlwMD,");
		updateSql.append(" FinSchdMthd = :FinSchdMthd, FInIsAlwGrace = :FInIsAlwGrace, FinHistRetension = :FinHistRetension,");
		updateSql.append(" EqualRepayment = :EqualRepayment,");
		updateSql.append(" FinRateType = :FinRateType, FinBaseRate = :FinBaseRate, FinSplRate = :FinSplRate,");
		updateSql.append(" FinIntRate = :FinIntRate,FInMinRate = :FInMinRate, FinMaxRate = :FinMaxRate, FinDftIntFrq = :FinDftIntFrq,");
		updateSql.append(" FinIsIntCpz = :FinIsIntCpz, FinCpzFrq = :FinCpzFrq, FinIsRvwAlw = :FinIsRvwAlw,FinRvwFrq = :FinRvwFrq, ");
		updateSql.append(" FinGrcRateType = :FinGrcRateType, FinGrcBaseRate = :FinGrcBaseRate,FinGrcSplRate = :FinGrcSplRate,");
		updateSql.append(" FinGrcIntRate = :FinGrcIntRate, FInGrcMinRate = :FInGrcMinRate, FinGrcMaxRate = :FinGrcMaxRate,");
		updateSql.append(" FinGrcDftIntFrq = :FinGrcDftIntFrq,  FinGrcIsIntCpz = :FinGrcIsIntCpz, FinGrcCpzFrq = :FinGrcCpzFrq,");
		updateSql.append(" FinGrcIsRvwAlw = :FinGrcIsRvwAlw, FinGrcRvwFrq = :FinGrcRvwFrq,FinMinTerm = :FinMinTerm,");
		updateSql.append(" FinMaxTerm = :FinMaxTerm, FinDftTerms = :FinDftTerms, FinRpyFrq = :FinRpyFrq,");
		updateSql.append(" finRepayMethod = :finRepayMethod, FinIsAlwPartialRpy = :FinIsAlwPartialRpy,");
		updateSql.append(" FinIsAlwDifferment = :FinIsAlwDifferment, FinMaxDifferment= :FinMaxDifferment, AlwPlanDeferment=:AlwPlanDeferment,");
		updateSql.append(" PlanDeferCount=:PlanDeferCount, FinIsAlwEarlyRpy = :FinIsAlwEarlyRpy, FinIsAlwEarlySettle = :FinIsAlwEarlySettle,");
		updateSql.append(" FinODRpyTries = :FinODRpyTries,");
		updateSql.append(" FinIsDwPayRequired = :FinIsDwPayRequired,");
		updateSql.append(" FinRvwRateApplFor = :FinRvwRateApplFor,FinIsIntCpzAtGrcEnd = :FinIsIntCpzAtGrcEnd,FinAlwRateChangeAnyDate = :FinAlwRateChangeAnyDate,  ");
		updateSql.append(" FinSchCalCodeOnRvw = :FinSchCalCodeOnRvw,FinAssetType=:FinAssetType,FinDepositRestrictedTo=:FinDepositRestrictedTo,");
		updateSql.append(" FinAEBuyOrInception=:FinAEBuyOrInception,FinAESellOrMaturity=:FinAESellOrMaturity,FinIsActive = :FinIsActive,");
		updateSql.append(" PftPayAcType=:PftPayAcType,FinIsOpenPftPayAcc=:FinIsOpenPftPayAcc,Version = :Version ,LastMntBy = :LastMntBy,LastMntOn = :LastMntOn,");
		updateSql.append(" RecordStatus= :RecordStatus, RoleCode = :RoleCode, NextRoleCode = :NextRoleCode, TaskId = :TaskId,");
		updateSql.append(" NextTaskId = :NextTaskId, RecordType = :RecordType, WorkflowId = :WorkflowId,FinGrcSchdMthd=:FinGrcSchdMthd, ");
		updateSql.append(" FinIsAlwGrcRepay=:FinIsAlwGrcRepay,FinScheduleOn=:FinScheduleOn,FinGrcScheduleOn=:FinGrcScheduleOn,");
		updateSql.append(" FinMargin=:FinMargin,FinGrcMargin=:FinGrcMargin,GrcAdvBaseRate=:GrcAdvBaseRate , GrcAdvMargin=:GrcAdvMargin , ");
		updateSql.append(" GrcAdvPftRate=:GrcAdvPftRate, RpyAdvBaseRate=:RpyAdvBaseRate , RpyAdvMargin=:RpyAdvMargin , RpyAdvPftRate=:RpyAdvPftRate,");
		updateSql.append(" FinCommitmentReq=:FinCommitmentReq ,FinCollateralReq=:FinCollateralReq ,FinDepreciationReq=:FinDepreciationReq,");
		updateSql.append(" FinDepreciationFrq=:FinDepreciationFrq ,");
		updateSql.append(" FinPftUnChanged=:FinPftUnChanged ,ManualSchedule = :ManualSchedule,");
		updateSql.append(" AllowRIAInvestment =:AllowRIAInvestment , OverrideLimit=:OverrideLimit, ");
		updateSql.append(" LimitRequired=:LimitRequired ,FinCommitmentOvrride=:FinCommitmentOvrride ,FinCollateralOvrride=:FinCollateralOvrride , FinRepayPftOnFrq =:FinRepayPftOnFrq, ");
		updateSql.append(" ApplyODPenalty =:ApplyODPenalty , ODIncGrcDays =:ODIncGrcDays, ODChargeType=:ODChargeType , ODGraceDays=:ODGraceDays , " );
		updateSql.append(" ODChargeCalOn=:ODChargeCalOn , ODChargeAmtOrPerc=:ODChargeAmtOrPerc , ODAllowWaiver=:ODAllowWaiver , ODMaxWaiverPerc=:ODMaxWaiverPerc, FinDivision=:FinDivision, ");
		updateSql.append(" StepFinance=:StepFinance , SteppingMandatory=:SteppingMandatory , AlwManualSteps=:AlwManualSteps , AlwdStepPolicies=:AlwdStepPolicies , DftStepPolicy=:DftStepPolicy,");
		updateSql.append(" StartDate=:StartDate, EndDate=:EndDate, ");
		updateSql.append(" AllowDownpayPgm=:AllowDownpayPgm, Remarks=:Remarks, AlwEarlyPayMethods=:AlwEarlyPayMethods,");
		updateSql.append("  PastduePftCalMthd=:PastduePftCalMthd,PastduePftMargin=:PastduePftMargin,");
		updateSql.append(" AlwAdvanceRent=:AlwAdvanceRent, RollOverFinance=:RollOverFinance, RollOverFrq = :RollOverFrq, ");
		updateSql.append(" DownPayRule=:DownPayRule, ProductCategory=:ProductCategory,");
		updateSql.append(" FinSuspTrigger=:FinSuspTrigger, FinSuspRemarks=:FinSuspRemarks , AlwMultiPartyDisb = :AlwMultiPartyDisb , TDSApplicable=:TDSApplicable, CollateralType = :CollateralType, ");
		updateSql.append(" ApplyGrcPricing = :ApplyGrcPricing, GrcPricingMethod = :GrcPricingMethod, ApplyRpyPricing = :ApplyRpyPricing, RpyPricingMethod = :RpyPricingMethod, RpyHierarchy = :RpyHierarchy, ");
		updateSql.append(" DroplineOD = :DroplineOD, DroppingMethod = :DroppingMethod, RateChgAnyDay=:RateChgAnyDay,");
		updateSql.append(" AlwBPI=:AlwBPI , BpiTreatment=:BpiTreatment , PftDueSchOn=:PftDueSchOn , PlanEMIHAlw =:PlanEMIHAlw , PlanEMIHMethod =:PlanEMIHMethod , PlanEMIHMaxPerYear =:PlanEMIHMaxPerYear , PlanEMIHMax=:PlanEMIHMax , ");
		updateSql.append(" PlanEMIHLockPeriod=:PlanEMIHLockPeriod , PlanEMICpz=:PlanEMICpz , UnPlanEMIHLockPeriod=:UnPlanEMIHLockPeriod , UnPlanEMICpz=:UnPlanEMICpz ,AlwReage=:AlwReage,AlwUnPlanEmiHoliday=:AlwUnPlanEmiHoliday, ");
		updateSql.append(" ReAgeCpz=:ReAgeCpz, FddLockPeriod=:FddLockPeriod, AlwdRpyMethods=:AlwdRpyMethods, MaxUnplannedEmi=:MaxUnplannedEmi, MaxReAgeHolidays=:MaxReAgeHolidays,");
		updateSql.append(" RoundingMode=:RoundingMode ,RoundingTarget=:RoundingTarget, FrequencyDays=:FrequencyDays,AlwMaxDisbCheckReq=:AlwMaxDisbCheckReq,QuickDisb=:QuickDisb, ProfitCenterID = :ProfitCenterID");
		updateSql.append(" Where FinType =:FinType");

		if (!type.endsWith("_Temp")) {
			updateSql.append(" AND Version= :Version-1");
		}

		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(financeType);
		recordCount = this.namedParameterJdbcTemplate.update(updateSql.toString(), beanParameters);

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
		selectSql.append(" Where FinType =:FinType AND (Product IS NULL OR Product = ' ')");

		logger.debug("selectSql: " + selectSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(financeType);

		try {
			productCount = this.namedParameterJdbcTemplate.queryForObject(selectSql.toString(), beanParameters, Integer.class);
		} catch (EmptyResultDataAccessException dae) {
			logger.debug(dae);
			productCount = 0;
		}
		logger.debug("Leaving");
		return productCount;
	}
	
	/**
	 * Method for get total number of records related to specific financeType(Promotion)
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
			promotionCount = this.namedParameterJdbcTemplate.queryForObject(selectSql.toString(), beanParameters, Integer.class);
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
			productCount = this.namedParameterJdbcTemplate.queryForObject(selectSql.toString(), beanParameters, Integer.class);
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
			collateralTypes = this.namedParameterJdbcTemplate.queryForObject(selectSql.toString(), beanParameters, String.class);
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
			finacetypeList = this.namedParameterJdbcTemplate.query(selectSql.toString(), beanParameters, typeRowMapper);
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
			finTypeDesc = this.namedParameterJdbcTemplate.queryForObject(selectSql.toString(), beanParameters, String.class);
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
			count = this.namedParameterJdbcTemplate.queryForObject(selectSql.toString(), source, Integer.class);
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
		return this.namedParameterJdbcTemplate.queryForObject(selectSql.toString(), beanParameters, Integer.class);
	}

	/**
	 * Method for Checking Step Policy Code is already using in Existing FinType or not
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
		selectSql.append("'%,"+policyCode+",%'");

		logger.debug("selectSql: " + selectSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(new FinanceType());
		int rcdCount =  this.namedParameterJdbcTemplate.queryForObject(selectSql.toString(), beanParameters, Integer.class);
		
		logger.debug("Leaving");
		return rcdCount > 0 ? true : false;
	}
	
}