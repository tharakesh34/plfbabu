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
 * * FileName : FinanceMainDAOImpl.java * * Author : PENNANT TECHONOLOGIES * * Creation Date : 15-11-2011 * * Modified
 * Date : 15-11-2011 * * Description : * *
 ******************************************************************************************** 
 * Date Author Version Comments *
 ******************************************************************************************** 
 * 15-11-2011 Pennant 0.1 * * * * * * * * *
 ******************************************************************************************** 
 */
package com.pennant.backend.dao.finance.impl;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSourceUtils;
import org.springframework.jdbc.core.simple.ParameterizedBeanPropertyRowMapper;

import com.pennant.app.constants.ImplementationConstants;
import com.pennant.app.util.SysParamUtil;
import com.pennant.backend.dao.finance.FinanceMainDAO;
import com.pennant.backend.dao.impl.BasisCodeDAO;
import com.pennant.backend.model.WorkFlowDetails;
import com.pennant.backend.model.ddapayments.DDAPayments;
import com.pennant.backend.model.finance.BulkDefermentChange;
import com.pennant.backend.model.finance.BulkProcessDetails;
import com.pennant.backend.model.finance.FinanceEnquiry;
import com.pennant.backend.model.finance.FinanceMain;
import com.pennant.backend.model.finance.FinanceSummary;
import com.pennant.backend.model.finance.RolledoverFinanceDetail;
import com.pennant.backend.model.reports.AvailCommitment;
import com.pennant.backend.model.reports.AvailFinance;
import com.pennant.backend.util.FinanceConstants;
import com.pennant.backend.util.RepayConstants;
import com.pennant.backend.util.WorkFlowUtil;
import com.pennanttech.pennapps.core.App;
import com.pennanttech.pennapps.core.App.Database;
import com.pennanttech.pennapps.core.ConcurrencyException;
import com.pennanttech.pennapps.core.DependencyFoundException;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pff.core.TableType;
import com.pennanttech.pff.core.util.QueryUtil;

/**
 * DAO methods implementation for the <b>FinanceMain model</b> class.<br>
 */
public class FinanceMainDAOImpl extends BasisCodeDAO<FinanceMain> implements FinanceMainDAO {
	private static Logger				logger	= Logger.getLogger(FinanceMainDAOImpl.class);

	private NamedParameterJdbcTemplate	namedParameterJdbcTemplate;

	public FinanceMainDAOImpl() {
		super();
	}

	/**
	 * This method set the Work Flow id based on the module name and return the new FinanceMain
	 * 
	 * @return FinanceMain
	 */
	@Override
	public FinanceMain getFinanceMain(boolean isWIF) {
		logger.debug("Entering");

		String wfModuleName = "";

		if (isWIF) {
			wfModuleName = "WIFFinanceMain";
		} else {
			wfModuleName = "FinanceMain";
		}

		WorkFlowDetails workFlowDetails = WorkFlowUtil.getWorkFlowDetails(wfModuleName);
		FinanceMain financeMain = new FinanceMain();
		if (workFlowDetails != null) {
			financeMain.setWorkflowId(workFlowDetails.getWorkFlowId());
		}
		logger.debug("Leaving");
		return financeMain;
	}

	/**
	 * This method get the module from method getFinanceMain() and set the new record flag as true and return
	 * FinanceMain()
	 * 
	 * @return FinanceMain
	 */

	@Override
	public FinanceMain getNewFinanceMain(boolean isWIF) {
		logger.debug("Entering");
		FinanceMain financeMain = getFinanceMain(isWIF);
		financeMain.setNewRecord(true);
		logger.debug("Leaving");
		return financeMain;
	}

	/**
	 * Method to get all the first task owner roles of the work flow's which are used for finance.
	 */
	@Override
	public List<String> getFinanceWorlflowFirstTaskOwners(String event, String moduleName) {
		logger.debug("Entering");
		MapSqlParameterSource source = new MapSqlParameterSource();
		source.addValue("WorkFlowActive", 1);
		source.addValue("FinEvent", event);
		source.addValue("ModuleName", event);

		StringBuilder selectSql = new StringBuilder();
		selectSql.append("select FirstTaskOwner from WorkFlowDetails");
		selectSql.append(" where  WorkFlowType in ( select DISTINCT WorkFlowType from  LMTFinanceWorkFlowDef");
		if (StringUtils.isNotBlank(event)) {
			selectSql.append(" where FinEvent=:FinEvent )");
		}
		selectSql.append(" and WorkFlowActive= :WorkFlowActive ");
		logger.debug("selectSql: " + selectSql.toString());
		List<String> firstTaskOwnerList = new ArrayList<String>();

		try {
			firstTaskOwnerList = this.namedParameterJdbcTemplate.queryForList(selectSql.toString(), source,
					String.class);
		} catch (EmptyResultDataAccessException e) {
			logger.debug(e);
		}

		logger.debug("Leaving");
		return firstTaskOwnerList;
	}

	/**
	 * Fetch the Record Finance Main Detail details by key field
	 * 
	 * @param id
	 *            (String)
	 * @param type
	 *            (String) ""/_Temp/_View
	 * @return FinanceMain
	 */
	@Override
	public FinanceMain getFinanceMain(final String id, String nextRoleCode, String type) {
		logger.debug("Entering");

		MapSqlParameterSource mapSqlParameterSource = new MapSqlParameterSource();
		mapSqlParameterSource.addValue("FinReference", id);
		mapSqlParameterSource.addValue("NextRoleCode", nextRoleCode);

		StringBuilder selectSql = new StringBuilder(
				"SELECT FinReference, InvestmentRef, NumberOfTerms, GrcPeriodEndDate, AllowGrcPeriod, GraceBaseRate, ");
		selectSql.append(
				" GraceSpecialRate, GrcPftRate, GrcPftFrq, NextGrcPftDate, AllowGrcPftRvw, GrcPftRvwFrq, NextGrcPftRvwDate, AllowGrcCpz, ");
		selectSql.append(
				" GrcCpzFrq, NextGrcCpzDate, RepayBaseRate, RepaySpecialRate, RepayProfitRate, RepayFrq, NextRepayDate, RepayPftFrq, ");
		selectSql.append(
				" NextRepayPftDate, AllowRepayRvw, RepayRvwFrq, NextRepayRvwDate, AllowRepayCpz, RepayCpzFrq, NextRepayCpzDate, MaturityDate, ");
		selectSql.append(
				" CpzAtGraceEnd, DownPayment, GraceFlatAmount, ReqRepayAmount, TotalProfit, TotalCpz, TotalGrossPft, TotalGrossGrcPft, ");
		selectSql.append(
				" TotalGracePft, TotalGraceCpz, GrcRateBasis, RepayRateBasis, FinType, FinRemarks, FinCcy, ScheduleMethod, ProfitDaysBasis, ");
		selectSql.append(
				" ReqMaturity, CalTerms, CalMaturity, FirstRepay, LastRepay, FinStartDate, FinAmount,  FinRepaymentAmount, CustID, Defferments, ");
		selectSql.append(
				" PlanDeferCount, FinBranch, FinSourceID, AllowedDefRpyChange, AvailedDefRpyChange, AllowedDefFrqChange, AvailedDefFrqChange, ");
		selectSql.append(
				" RecalType, FinAssetValue, DisbAccountId, RepayAccountId, FinIsActive, LimitValid, OverrideLimit, SecurityDeposit, Version, ");
		selectSql.append(
				" LastMntBy, LastMntOn, RecordStatus, RoleCode, NextRoleCode, TaskId, NextTaskId, RecordType, WorkflowId,MinDownpayPerc,LastRepayDate, ");
		selectSql.append(
				" LastRepayPftDate, LastRepayRvwDate, LastRepayCpzDate, AllowGrcRepay, GrcSchdMthd, GrcMargin, RepayMargin, FinCurrAssetValue, ");
		selectSql.append(
				" FinCommitmentRef, FinLimitRef,DepreciationFrq, FinContractDate,  NextDepDate, LastDepDate, FinAccount, FinCustPftAccount, ");
		selectSql.append(
				" TotalRepayAmt,FinApprovedDate, FeeChargeAmt, Blacklisted, FinRepayPftOnFrq, AnualizedPercRate, EffectiveRateOfReturn, ");
		selectSql.append(
				" MigratedFinance, ScheduleMaintained, ScheduleRegenerated, FinPurpose,FinStatus, FinStsReason, InitiateUser, BankName, iban, ");
		selectSql.append(
				" AccountType, DdaReferenceNo, CustDSR,JointAccount, JointCustId, DownPayBank, DownPaySupl, DownPayAccount, SecurityCollateral,  Approved, ");
		selectSql.append(
				" Discrepancy, LimitApproved, GraceTerms, RcdMaintainSts, FinRepayMethod, GrcProfitDaysBasis, StepFinance,StepType, StepPolicy, ");
		selectSql.append(
				" AlwManualSteps, NoOfSteps, LinkedFinRef, NextUserId, Priority,  GrcMinRate, GrcMaxRate, RpyMinRate, RpyMaxRate, DeviationApproval, ");
		selectSql.append(
				" ManualSchedule, TakeOverFinance, GrcAdvBaseRate, GrcAdvMargin, GrcAdvPftRate, RpyAdvBaseRate, RpyAdvMargin, RpyAdvPftRate, ");
		selectSql.append(
				" SupplementRent, IncreasedCost, CreditInsAmt, RolloverFrq, NextRolloverDate,  ShariaStatus, InitiateDate, FinPreApprovedRef, MMAId, ");
		selectSql.append(
				" AccountsOfficer, FeeAccountId, FinCancelAc, DSACode, TDSApplicable, MandateID,DroplineFrq,FirstDroplineDate, PftServicingODLimit, ");
		selectSql.append(
				" InsuranceAmt,DeductInsDisb,AlwBPI , BpiTreatment , PlanEMIHAlw , PlanEMIHMethod , PlanEMIHMaxPerYear , PlanEMIHMax , ");
		selectSql.append(
				" PlanEMIHLockPeriod , PlanEMICpz , CalRoundingMode , RoundingTarget, AlwMultiDisb , ApplicationNo , ReferralId ,  DmaCode ,  SalesDepartment , ");
		selectSql.append(
				" QuickDisb , WifReference, UnPlanEMIHLockPeriod , UnPlanEMICpz, ReAgeCpz, MaxUnplannedEmi, MaxReAgeHolidays, AvailedUnPlanEmi, AvailedReAgeH, BpiAmount, DeductFeeDisb");
		selectSql
				.append(" , PromotionCode,RvwRateApplFor , SchCalOnRvw,PastduePftCalMthd,DroppingMethod,RateChgAnyDay,PastduePftMargin, ReAgeBucket, FinCategory, ProductCategory,EligibilityMethod ");

		if (StringUtils.trimToEmpty(type).contains("View")) {
			selectSql.append(
					" ,LovDescFinTypeName, LovDescFinMaxAmt, LovDescFinMinAmount, LovDescFinDivision, LovDescFinBranchName, ");
			selectSql.append(
					" LovDescStepPolicyName, LovDescAccountsOfficer, DSACodeDesc, ReferralIdDesc, DmaCodeDesc, SalesDepartmentDesc,lovdescEntityCode ");
		}
		selectSql.append(" From FinanceMain");
		selectSql.append(StringUtils.trimToEmpty(type));
		selectSql.append(" Where FinReference =:FinReference And NextRoleCode = :NextRoleCode ");

		logger.debug("selectSql: " + selectSql.toString());
		RowMapper<FinanceMain> typeRowMapper = ParameterizedBeanPropertyRowMapper.newInstance(FinanceMain.class);

		try {
			return this.namedParameterJdbcTemplate.queryForObject(selectSql.toString(), mapSqlParameterSource,
					typeRowMapper);
		} catch (EmptyResultDataAccessException e) {
			logger.warn("Exception: ", e);
		}
		logger.debug("Leaving");
		return null;
	}

	/**
	 * Fetch the Record Finance Main Detail details by key field
	 * 
	 * @param id
	 *            (String)
	 * @param type
	 *            (String) ""/_Temp/_View
	 * @return FinanceMain
	 */
	@Override
	public FinanceMain getFinanceMainById(final String finReference, String type, boolean isWIF) {
		logger.debug("Entering");

		MapSqlParameterSource source = new MapSqlParameterSource();

		StringBuilder selectSql = new StringBuilder(
				"SELECT FinReference,GraceTerms, NumberOfTerms, GrcPeriodEndDate, AllowGrcPeriod,");
		selectSql.append(" GraceBaseRate, GraceSpecialRate, GrcPftRate, GrcPftFrq,NextGrcPftDate, AllowGrcPftRvw,");
		selectSql.append(" GrcPftRvwFrq, NextGrcPftRvwDate, AllowGrcCpz, GrcCpzFrq, NextGrcCpzDate,RepayBaseRate,");
		selectSql.append(" RepaySpecialRate, RepayProfitRate, RepayFrq, NextRepayDate, RepayPftFrq, NextRepayPftDate,");
		selectSql.append(" AllowRepayRvw,RepayRvwFrq, NextRepayRvwDate, AllowRepayCpz, RepayCpzFrq, NextRepayCpzDate,");
		selectSql.append(
				" MaturityDate, CpzAtGraceEnd,DownPayment, ReqRepayAmount, TotalProfit, DownPayBank, DownPaySupl, ");
		selectSql.append(" TotalCpz,TotalGrossPft,TotalGracePft,TotalGraceCpz,TotalGrossGrcPft, TotalRepayAmt,");
		selectSql.append(" GrcRateBasis, RepayRateBasis, FinType,FinRemarks, FinCcy, ScheduleMethod,");
		selectSql.append(" ProfitDaysBasis, ReqMaturity, CalTerms, CalMaturity, FirstRepay, LastRepay,");
		selectSql.append(" FinStartDate, FinAmount, FinRepaymentAmount, CustID, Defferments, PlanDeferCount, ");
		selectSql.append(" FinBranch, FinSourceID, AllowedDefRpyChange, AvailedDefRpyChange, AllowedDefFrqChange,");
		selectSql
				.append(" AvailedDefFrqChange, RecalType, FinAssetValue, DisbAccountId, RepayAccountId, FinIsActive, ");
		selectSql.append(
				" LastRepayDate, LastRepayPftDate,LastRepayRvwDate, LastRepayCpzDate, AllowGrcRepay, GrcSchdMthd,");
		selectSql.append(
				" GrcMargin, RepayMargin, FinCommitmentRef, DepreciationFrq, FinCurrAssetValue,FinContractDate,");
		selectSql.append(" NextDepDate, LastDepDate, FinAccount, FinCustPftAccount,");
		selectSql.append(" ClosingStatus, FinApprovedDate, ");
		selectSql.append(
				" AnualizedPercRate , EffectiveRateOfReturn , FinRepayPftOnFrq , GrcProfitDaysBasis, StepFinance , StepPolicy, AlwManualSteps, NoOfSteps, StepType, ");
		selectSql.append(" LinkedFinRef,");
		selectSql.append(" GrcMinRate, GrcMaxRate , RpyMinRate, RpyMaxRate,");
		selectSql.append(
				" ManualSchedule , TakeOverFinance , GrcAdvBaseRate ,GrcAdvMargin ,GrcAdvPftRate ,RpyAdvBaseRate ,RpyAdvMargin ,RpyAdvPftRate ,");
		selectSql.append(
				" SupplementRent, IncreasedCost , feeAccountId, MinDownPayPerc, TDSApplicable, FeeChargeAmt, InsuranceAmt, AlwBPI , BpiTreatment , PlanEMIHAlw ,");
		selectSql.append(
				" PlanEMIHMethod , PlanEMIHMaxPerYear , PlanEMIHMax , PlanEMIHLockPeriod , PlanEMICpz , CalRoundingMode ,RoundingTarget, AlwMultiDisb, BpiAmount, ");
		selectSql.append(" DeductFeeDisb, RvwRateApplFor, SchCalOnRvw,PastduePftCalMthd,DroppingMethod,RateChgAnyDay,PastduePftMargin,  FinCategory, ProductCategory,");

		if (StringUtils.trimToEmpty(type).contains("View")) {
			selectSql.append(" lovDescFinTypeName, lovDescFinBranchName, ");
			if (!isWIF) {
				selectSql.append(" ReAgeBucket, FinLimitRef, ");
				selectSql.append(" lovDescAccruedTillLBD, lovDescFinScheduleOn,");
				selectSql.append(
						" lovDescFinDivision,LovDescStepPolicyName,CustStsDescription, lovDescAccountsOfficer, DsaCodeDesc,  ");
				selectSql.append("  ReferralIdDesc , DmaCodeDesc , SalesDepartmentDesc,lovDescEntityCode,LOVDESCSOURCECITY, ");
			}
		}
		selectSql.append(" Version , LastMntBy, LastMntOn, RecordStatus, RoleCode, NextRoleCode, TaskId, ");
		selectSql.append(" NextTaskId, RecordType, WorkflowId");

		if (isWIF) {
			selectSql.append(" From WIFFinanceMain");
		} else {
			selectSql.append(
					" , InvestmentRef , DownPayAccount,  SecurityDeposit, RcdMaintainSts, FinRepayMethod, FinCancelAc ,");
			selectSql.append(
					" MigratedFinance, ScheduleMaintained, ScheduleRegenerated, CustDSR,JointAccount,JointCustId,DeviationApproval,FinPreApprovedRef,MandateID,");
			selectSql.append(
					" Blacklisted, LimitValid, OverrideLimit, FinPurpose,FinStatus, FinStsReason, InitiateUser,");
			selectSql.append(" BankName, Iban, AccountType, DdaReferenceNo, NextUserId, Priority, ");
			selectSql.append(
					" RolloverFrq, NextRolloverDate,ShariaStatus,InitiateDate,MMAId,AccountsOfficer,DsaCode,DroplineFrq,FirstDroplineDate,PftServicingODLimit,  ");
			selectSql.append(
					" ReferralId, DmaCode, SalesDepartment, QuickDisb, WifReference, UnPlanEMIHLockPeriod , UnPlanEMICpz, ReAgeCpz, MaxUnplannedEmi, MaxReAgeHolidays, AvailedUnPlanEmi, AvailedReAgeH, PromotionCode, ApplicationNo,EligibilityMethod ");
			selectSql.append(" From FinanceMain");
		}
		selectSql.append(StringUtils.trimToEmpty(type));
		selectSql.append(" Where FinReference =:FinReference");

		logger.debug("selectSql: " + selectSql.toString());

		source.addValue("FinReference", finReference);

		RowMapper<FinanceMain> typeRowMapper = ParameterizedBeanPropertyRowMapper.newInstance(FinanceMain.class);
		try {
			return this.namedParameterJdbcTemplate.queryForObject(selectSql.toString(), source, typeRowMapper);
		} catch (EmptyResultDataAccessException e) {
			logger.warn("Exception: ", e);
		}
		logger.debug("Leaving");
		return null;
	}

	/**
	 * Fetch the Record Finance Main Detail details by key field
	 * 
	 * @param id
	 *            (String)
	 * @param type
	 *            (String) ""/_Temp/_View
	 * @return FinanceMain
	 */
	@Override
	public FinanceMain getDisbursmentFinMainById(final String finReference, TableType tableType) {
		logger.debug("Entering");

		MapSqlParameterSource source = new MapSqlParameterSource();

		StringBuilder selectSql = new StringBuilder("SELECT T1.FinCcy,T1.FinType,T1.CustID,T1.FinStartDate, T1.FinBranch,");
		selectSql.append(" T1.FinReference,T1.MaturityDate,T1.FeeChargeAmt,T1.DownPayment,T1.DeductFeeDisb, ");
		selectSql.append(" T1.BpiAmount,T1.DeductInsDisb,T1.FinisActive, T2.alwMultiPartyDisb,T2.FinTypeDesc,");
		selectSql.append(" T1.BpiTreatment,T3.CustCIF,T3.CustShrtName,T1.quickDisb ,T1.FinAssetValue,T1.FinCurrAssetValue From FinanceMain");
		selectSql.append(tableType.getSuffix());
		selectSql.append(" T1 INNER JOIN RMTFinanceTypes T2 ON T1.FinType=T2.FinType ");
		selectSql.append(" INNER JOIN Customers T3 ON T1.CustID = T3.CustID ");
		selectSql.append(" Where T1.FinReference =:FinReference");

		logger.debug("selectSql: " + selectSql.toString());

		source.addValue("FinReference", finReference);

		RowMapper<FinanceMain> typeRowMapper = ParameterizedBeanPropertyRowMapper.newInstance(FinanceMain.class);
		try {
			return this.namedParameterJdbcTemplate.queryForObject(selectSql.toString(), source, typeRowMapper);
		} catch (EmptyResultDataAccessException e) {
			logger.warn("Exception: ", e);
		}
		logger.debug("Leaving");
		return null;
	}

	/**
	 * Fetch the Record Finance Main Detail details by key field
	 * 
	 * @param id
	 *            (String)
	 * @param type
	 *            (String) ""/_Temp/_View
	 * @return FinanceMain
	 */
	@Override
	public FinanceMain getFinanceMainForPftCalc(final String finReference) {
		logger.debug("Entering");

		MapSqlParameterSource source = new MapSqlParameterSource();

		StringBuilder selectSql = new StringBuilder(
				"SELECT FinReference,FinType, CustId, FinAmount, DownPayment, FeeChargeAmt, GrcPeriodEndDate, NextRepayPftDate, NextRepayRvwDate, FinIsActive, ");
		selectSql.append(
				" ProfitDaysBasis, FinStartDate, FinAssetValue, LastRepayPftDate,LastRepayRvwDate,FinCurrAssetValue, MaturityDate, FinStatus, FinStsReason, ");
		selectSql.append(
				" InitiateUser, BankName, Iban, AccountType, DdaReferenceNo, ClosingStatus, LastRepayDate, NextRepayDate, PromotionCode, PastduePftCalMthd, PastduePftMargin ");
		selectSql.append(" From FinanceMain");
		selectSql.append(" Where FinReference =:FinReference");

		logger.debug("selectSql: " + selectSql.toString());
		source.addValue("FinReference", finReference);

		RowMapper<FinanceMain> typeRowMapper = ParameterizedBeanPropertyRowMapper.newInstance(FinanceMain.class);
		FinanceMain finMain = new FinanceMain();

		try {
			finMain = this.namedParameterJdbcTemplate.queryForObject(selectSql.toString(), source, typeRowMapper);
		} catch (EmptyResultDataAccessException e) {
			finMain = null;
		}
		logger.debug("Leaving");
		return finMain;
	}

	/**
	 * Fetch the Record Finance Main Detail details by key field
	 * 
	 * @param id
	 *            (String)
	 * @param type
	 *            (String) ""/_Temp/_View
	 * @return FinanceMain
	 */
	@Override
	public FinanceMain getFinanceMainForRpyCancel(final String id) {
		logger.debug("Entering");

		FinanceMain financeMain = new FinanceMain();
		financeMain.setId(id);

		StringBuilder selectSql = new StringBuilder("SELECT FinReference, CustId, GrcPeriodEndDate, NextRepayPftDate, NextRepayRvwDate,");
		selectSql.append(" FinStatus, FinAmount, FeeChargeAmt, FinRepaymentAmount, FinCcy,");
		selectSql.append(" ProfitDaysBasis, FinStartDate, FinAssetValue, LastRepayPftDate,LastRepayRvwDate,FinCurrAssetValue, MaturityDate, PromotionCode ");
		selectSql.append(" From FinanceMain");
		selectSql.append(" Where FinReference =:FinReference");

		logger.debug("selectSql: " + selectSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(financeMain);
		RowMapper<FinanceMain> typeRowMapper = ParameterizedBeanPropertyRowMapper.newInstance(FinanceMain.class);

		try {
			financeMain = this.namedParameterJdbcTemplate.queryForObject(selectSql.toString(), beanParameters, typeRowMapper);
		} catch (EmptyResultDataAccessException e) {
			logger.warn("Exception: ", e);
			financeMain = null;
		}
		
		logger.debug("Leaving");
		
		return financeMain;
	}

	/**
	 * Fetch the Record Finance Main Detail details by key field
	 * 
	 * @param finReference
	 *            (String)
	 * 
	 * @return FinanceMain
	 */
	@Override
	public FinanceMain getFinanceMainForBatch(final String finReference) {
		logger.debug("Entering");

		FinanceMain financeMain = new FinanceMain();
		financeMain.setId(finReference);

		StringBuilder selectSql = new StringBuilder("select  FinReference, GrcPeriodEndDate, FinRepaymentAmount,");
		selectSql.append(
				" DisbAccountid, RepayAccountid, FinAccount, FinCustPftAccount, FinCommitmentRef, FinLimitRef,");
		selectSql.append(
				" FinCcy, FinBranch, CustId, FinAmount, FeeChargeAmt, DownPayment, DownPayBank, DownPaySupl, DownPayAccount, SecurityDeposit, FinType, ");
		selectSql.append(
				" FinStartDate,GraceTerms, NumberOfTerms, NextGrcPftDate, NextRepayDate, LastRepayPftDate, NextRepayPftDate,ProductCategory, ");
		selectSql.append(" LastRepayRvwDate, NextRepayRvwDate, FinAssetValue, FinCurrAssetValue,FinRepayMethod, ");
		selectSql.append(" RecordType, Version, ProfitDaysBasis , FeeChargeAmt, FinStatus, FinStsReason,");
		selectSql.append(
				" InitiateUser, BankName, Iban, AccountType, DdaReferenceNo, SecurityDeposit, MaturityDate, feeAccountId, MinDownPayPerc, PromotionCode, FinIsActive ");
		selectSql.append(" FROM Financemain");
		selectSql.append(" Where FinReference =:FinReference");

		logger.debug("selectSql: " + selectSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(financeMain);
		RowMapper<FinanceMain> typeRowMapper = ParameterizedBeanPropertyRowMapper.newInstance(FinanceMain.class);

		try {
			financeMain = this.namedParameterJdbcTemplate.queryForObject(selectSql.toString(), beanParameters,
					typeRowMapper);
		} catch (EmptyResultDataAccessException e) {
			logger.warn("Exception: ", e);
			financeMain = null;
		}
		logger.debug("Leaving");
		return financeMain;
	}

	/**
	 * Fetch the Records Finance Main Detail details by key field
	 * 
	 * @param curBD
	 * @param nextBD
	 * @return
	 */
	@Override
	public List<String> getFinanceMainListByBatch(final Date curBD, final Date nextBD, String type) {
		logger.debug("Entering");

		FinanceMain financeMain = getNewFinanceMain(false);
		financeMain.setFinStartDate(curBD);
		financeMain.setMaturityDate(nextBD);

		StringBuilder selectSql = new StringBuilder("SELECT FinReference ");
		selectSql.append(" From FinanceMain");
		selectSql.append(StringUtils.trimToEmpty(type));
		selectSql.append(" Where FinStartDate >=:FinStartDate AND MaturityDate <:MaturityDate");

		logger.debug("selectSql: " + selectSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(financeMain);
		logger.debug("Leaving");
		return this.namedParameterJdbcTemplate.queryForList(selectSql.toString(), beanParameters, String.class);

	}

	/**
	 * To Set dataSource
	 * 
	 * @param dataSource
	 */

	public void setDataSource(DataSource dataSource) {
		this.namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
	}

	/**
	 * This method insert new Records into FinanceMain or FinanceMain_Temp.
	 * 
	 * save Finance Main Detail
	 * 
	 * @param Finance
	 *            Main Detail (financeMain)
	 * @param type
	 *            (String) ""/_Temp/_View
	 * @return void
	 * @throws DataAccessException
	 * 
	 */

	public String saveInvestmentFinance(FinanceMain financeMain, String type) {
		logger.debug("Entering");

		StringBuilder insertSql = new StringBuilder("Insert Into ");

		insertSql.append(" FinanceMain");
		insertSql.append(StringUtils.trimToEmpty(type));
		insertSql.append(" (FinReference, InvestmentRef , FinType, FinCcy, FinBranch, FinAmount, FinStartDate,");
		insertSql.append(" MaturityDate, CustID, RepayProfitRate , TotalRepayAmt ,");
		insertSql.append(" TotalProfit, ProfitDaysBasis, ScheduleMethod, disbAccountId, repayAccountId,");
		insertSql.append(" LastRepayDate, LastRepayPftDate, LastRepayRvwDate , LastRepayCpzDate ,");
		insertSql.append(" GraceTerms, NumberOfTerms, AllowGrcPeriod, AllowGrcPftRvw , AllowGrcCpz ,");
		insertSql.append(" AllowRepayRvw, AllowRepayCpz, CpzAtGraceEnd , CalTerms ,");
		insertSql.append(" Defferments, PlanDeferCount, AllowedDefRpyChange , AvailedDefRpyChange ,");
		insertSql.append(" AllowedDefFrqChange, AvailedDefFrqChange, FinIsActive , AllowGrcRepay ,");
		insertSql.append(" FinRepayPftOnFrq , ");
		insertSql.append(" MigratedFinance, ScheduleMaintained, ScheduleRegenerated , Blacklisted ,");
		insertSql
				.append(" GrcProfitDaysBasis, StepFinance , StepPolicy, StepType, AlwManualSteps, NoOfSteps,DsaCode, ");
		insertSql.append(" Version , LastMntBy, LastMntOn, RecordStatus, RoleCode, NextRoleCode, TaskId,");
		insertSql.append(
				" NextTaskId, NextUserId, Priority, RecordType, WorkflowId, feeAccountId, MinDownPayPerc, TDSApplicable,DroplineFrq,FirstDroplineDate,PftServicingODLimit, PromotionCode)");

		insertSql.append(
				" Values(:FinReference, :InvestmentRef, :FinType, :FinCcy, :FinBranch, :FinAmount, :FinStartDate,");
		insertSql.append(" :MaturityDate, :CustID, :RepayProfitRate , :TotalRepayAmt ,");
		insertSql.append(" :TotalProfit, :ProfitDaysBasis, :ScheduleMethod, :DisbAccountId, :RepayAccountId,");
		insertSql.append(" :LastRepayDate, :LastRepayPftDate, :LastRepayRvwDate, :LastRepayCpzDate,");
		insertSql.append(" :GraceTerms, :NumberOfTerms, :AllowGrcPeriod, :AllowGrcPftRvw, :AllowGrcCpz,");
		insertSql.append(" :AllowRepayRvw, :AllowRepayCpz, :CpzAtGraceEnd, :CalTerms,");
		insertSql.append(" :Defferments, :PlanDeferCount, :AllowedDefRpyChange, :AvailedDefRpyChange,");
		insertSql.append(" :AllowedDefFrqChange, :AvailedDefFrqChange, :FinIsActive , :AllowGrcRepay,");
		insertSql.append(" :FinRepayPftOnFrq , ");
		insertSql.append(" :MigratedFinance, :ScheduleMaintained, :ScheduleRegenerated, :Blacklisted,");
		insertSql.append(
				" :GrcProfitDaysBasis, :StepFinance, :StepPolicy, :StepType, :AlwManualSteps, :NoOfSteps,:DsaCode, ");
		insertSql.append(" :Version ,:LastMntBy,:LastMntOn,:RecordStatus,:RoleCode,:NextRoleCode,:TaskId,");
		insertSql.append(
				" :NextTaskId, :NextUserId, :Priority, :RecordType,:WorkflowId, :feeAccountId, :minDownPayPerc, :TDSApplicable,:DroplineFrq,:FirstDroplineDate,:PftServicingODLimit, :PromotionCode)");
		logger.debug("insertSql: " + insertSql.toString());

		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(financeMain);
		this.namedParameterJdbcTemplate.update(insertSql.toString(), beanParameters);
		logger.debug("Leaving");
		return financeMain.getId();
	}

	@Override
	public String save(FinanceMain financeMain, TableType tableType, boolean wif) {
		logger.debug(Literal.ENTERING);

		// Prepare the SQL.
		StringBuilder sql = new StringBuilder("insert into ");
		if (wif) {
			sql.append(" WIFFinanceMain");
		} else {
			sql.append(" FinanceMain");
		}
		sql.append(tableType.getSuffix());
		sql.append(" (FinReference, GraceTerms,  NumberOfTerms, GrcPeriodEndDate, AllowGrcPeriod,");
		sql.append(" GraceBaseRate, GraceSpecialRate, GrcPftRate, GrcPftFrq,NextGrcPftDate, AllowGrcPftRvw,");
		sql.append(" GrcPftRvwFrq, NextGrcPftRvwDate, AllowGrcCpz, GrcCpzFrq, NextGrcCpzDate,RepayBaseRate,");
		sql.append(" RepaySpecialRate, RepayProfitRate, RepayFrq, NextRepayDate, RepayPftFrq, NextRepayPftDate,");
		sql.append(" AllowRepayRvw,RepayRvwFrq, NextRepayRvwDate, AllowRepayCpz, RepayCpzFrq, NextRepayCpzDate,");
		sql.append(" MaturityDate, CpzAtGraceEnd,DownPayment, DownPayBank, DownPaySupl, ReqRepayAmount, TotalProfit,");
		sql.append(" TotalCpz,TotalGrossPft,TotalGracePft, TotalGraceCpz,TotalGrossGrcPft, TotalRepayAmt,");
		sql.append("  GrcRateBasis, RepayRateBasis,FinType,FinRemarks, FinCcy, ScheduleMethod,FinContractDate,");
		sql.append(" ProfitDaysBasis, ReqMaturity, CalTerms, CalMaturity, FirstRepay, LastRepay,");
		sql.append(" FinStartDate, FinAmount, FinRepaymentAmount, CustID, Defferments,PlanDeferCount,");
		sql.append(" FinBranch, FinSourceID, AllowedDefRpyChange, AvailedDefRpyChange, AllowedDefFrqChange,");
		sql.append(" AvailedDefFrqChange, RecalType, FinIsActive,FinAssetValue, disbAccountId, repayAccountId, ");
		sql.append(" LastRepayDate, LastRepayPftDate, LastRepayRvwDate, LastRepayCpzDate, AllowGrcRepay, GrcSchdMthd,");
		sql.append(" GrcMargin, RepayMargin, FinCommitmentRef, FinLimitRef, DepreciationFrq, FinCurrAssetValue,");
		sql.append(" NextDepDate, LastDepDate, FinAccount, FinCustPftAccount, ClosingStatus, FinApprovedDate, ");
		sql.append(" DedupFound,SkipDedup,Blacklisted,");
		sql.append(" GrcProfitDaysBasis, StepFinance , StepPolicy, AlwManualSteps, NoOfSteps, StepType, ");
		sql.append(" AnualizedPercRate , EffectiveRateOfReturn , FinRepayPftOnFrq, ");
		sql.append(" LinkedFinRef, ");
		sql.append(" GrcMinRate, GrcMaxRate , RpyMinRate, RpyMaxRate,  ");
		sql.append(
				" ManualSchedule , TakeOverFinance, GrcAdvBaseRate ,GrcAdvMargin ,GrcAdvPftRate ,RpyAdvBaseRate ,RpyAdvMargin ,RpyAdvPftRate ,");
		sql.append(
				" SupplementRent, IncreasedCost , feeAccountId, MinDownPayPerc,TDSApplicable,InsuranceAmt, AlwBPI , BpiTreatment , PlanEMIHAlw ,");
		sql.append(
				" PlanEMIHMethod , PlanEMIHMaxPerYear , PlanEMIHMax , PlanEMIHLockPeriod , PlanEMICpz , CalRoundingMode ,RoundingTarget, AlwMultiDisb,FinRepayMethod, ");
		sql.append(" FeeChargeAmt, BpiAmount, DeductFeeDisb, RvwRateApplFor, SchCalOnRvw,PastduePftCalMthd,DroppingMethod,RateChgAnyDay,PastduePftMargin, FinCategory, ProductCategory,");
		if (!wif) {
			sql.append(" InvestmentRef, MigratedFinance, ScheduleMaintained, ScheduleRegenerated,CustDSR,");
			sql.append(
					" LimitValid, OverrideLimit,FinPurpose,FinStatus, FinStsReason, InitiateUser, BankName, Iban, AccountType, DdaReferenceNo, DeviationApproval,FinPreApprovedRef,MandateID,");
			sql.append(" JointAccount,JointCustId,DownPayAccount, SecurityDeposit, RcdMaintainSts,FinCancelAc, ");
			sql.append(
					" NextUserId, Priority,RolloverFrq, NextRolloverDate,ShariaStatus, InitiateDate,MMAId, AccountsOfficer , ApplicationNo,");
			sql.append(
					" DsaCode, DroplineFrq,FirstDroplineDate,PftServicingODLimit, ReferralId, DmaCode, SalesDepartment, QuickDisb, WifReference,");
			sql.append(
					" UnPlanEMIHLockPeriod , UnPlanEMICpz, ReAgeCpz, MaxUnplannedEmi, MaxReAgeHolidays, AvailedUnPlanEmi, AvailedReAgeH, ReAgeBucket, DueBucket, EligibilityMethod,");
		}
		sql.append(" Version , LastMntBy, LastMntOn, RecordStatus, RoleCode, NextRoleCode, TaskId,");
		sql.append(" NextTaskId, RecordType, WorkflowId, PromotionCode)");
		sql.append(" values (:FinReference,:GraceTerms, :NumberOfTerms, :GrcPeriodEndDate, :AllowGrcPeriod,");
		sql.append(" :GraceBaseRate, :GraceSpecialRate,:GrcPftRate,:GrcPftFrq,:NextGrcPftDate,:AllowGrcPftRvw,");
		sql.append(" :GrcPftRvwFrq,:NextGrcPftRvwDate,:AllowGrcCpz,:GrcCpzFrq,:NextGrcCpzDate,:RepayBaseRate,");
		sql.append(" :RepaySpecialRate,:RepayProfitRate,:RepayFrq,:NextRepayDate,:RepayPftFrq,:NextRepayPftDate,");
		sql.append(" :AllowRepayRvw,:RepayRvwFrq,:NextRepayRvwDate,:AllowRepayCpz,:RepayCpzFrq,:NextRepayCpzDate,");
		sql.append(
				" :MaturityDate,:CpzAtGraceEnd,:DownPayment, :DownPayBank, :DownPaySupl, :ReqRepayAmount,:TotalProfit,");
		sql.append(" :TotalCpz,:TotalGrossPft,:TotalGracePft,:TotalGraceCpz,:TotalGrossGrcPft, :TotalRepayAmt,");
		sql.append(" :GrcRateBasis,:RepayRateBasis, :FinType,:FinRemarks,:FinCcy,:ScheduleMethod,:FinContractDate,");
		sql.append(" :ProfitDaysBasis,:ReqMaturity,:CalTerms,:CalMaturity,:FirstRepay,:LastRepay,");
		sql.append(" :FinStartDate,:FinAmount,:FinRepaymentAmount,:CustID,:Defferments,:PlanDeferCount,");
		sql.append(" :FinBranch, :FinSourceID, :AllowedDefRpyChange, :AvailedDefRpyChange, :AllowedDefFrqChange,");
		sql.append(" :AvailedDefFrqChange, :RecalType, :FinIsActive,:FinAssetValue, :disbAccountId, :repayAccountId, ");
		sql.append(
				" :LastRepayDate, :LastRepayPftDate, :LastRepayRvwDate, :LastRepayCpzDate,:AllowGrcRepay, :GrcSchdMthd,");
		sql.append(" :GrcMargin, :RepayMargin, :FinCommitmentRef, :FinLimitRef, :DepreciationFrq, :FinCurrAssetValue,");
		sql.append(" :NextDepDate, :LastDepDate, :FinAccount, :FinCustPftAccount, :ClosingStatus , :FinApprovedDate, ");
		sql.append(" :DedupFound,:SkipDedup,:Blacklisted,");
		sql.append(" :GrcProfitDaysBasis, :StepFinance , :StepPolicy, :AlwManualSteps, :NoOfSteps, :StepType, ");
		sql.append(" :AnualizedPercRate , :EffectiveRateOfReturn , :FinRepayPftOnFrq, ");
		sql.append(" :LinkedFinRef, ");
		sql.append(" :GrcMinRate, :GrcMaxRate , :RpyMinRate, :RpyMaxRate, ");
		sql.append(
				" :ManualSchedule , :TakeOverFinance, :GrcAdvBaseRate ,:GrcAdvMargin ,:GrcAdvPftRate ,:RpyAdvBaseRate ,:RpyAdvMargin ,:RpyAdvPftRate ,");
		sql.append(
				" :SupplementRent, :IncreasedCost , :feeAccountId, :MinDownPayPerc,:TDSApplicable,:InsuranceAmt, :AlwBPI , :BpiTreatment , :PlanEMIHAlw ,");
		sql.append(
				" :PlanEMIHMethod , :PlanEMIHMaxPerYear , :PlanEMIHMax , :PlanEMIHLockPeriod , :PlanEMICpz , :CalRoundingMode ,:RoundingTarget, :AlwMultiDisb,:FinRepayMethod, ");
		sql.append(" :FeeChargeAmt, :BpiAmount, :DeductFeeDisb,:RvwRateApplFor ,:SchCalOnRvw,:PastduePftCalMthd,:DroppingMethod,:RateChgAnyDay,:PastduePftMargin, :FinCategory, :ProductCategory,");
		if (!wif) {
			sql.append(" :InvestmentRef, :MigratedFinance, :ScheduleMaintained, :ScheduleRegenerated, :CustDSR,");
			sql.append(
					" :LimitValid, :OverrideLimit,:FinPurpose,:FinStatus, :FinStsReason, :InitiateUser, :BankName, :Iban, :AccountType, :DdaReferenceNo, :DeviationApproval, :FinPreApprovedRef,:MandateID,");
			sql.append(
					" :JointAccount,:JointCustId , :DownPayAccount,  :SecurityDeposit, :RcdMaintainSts,:FinCancelAc, ");
			sql.append(" :NextUserId, ");
			sql.append(
					" :Priority,:RolloverFrq, :NextRolloverDate,:ShariaStatus, :InitiateDate, :MMAId, :AccountsOfficer,:ApplicationNo,");
			sql.append(
					" :DsaCode,:DroplineFrq,:FirstDroplineDate,:PftServicingODLimit, :ReferralId, :DmaCode, :SalesDepartment, :QuickDisb, :WifReference,");
			sql.append(
					" :UnPlanEMIHLockPeriod , :UnPlanEMICpz, :ReAgeCpz, :MaxUnplannedEmi, :MaxReAgeHolidays, :AvailedUnPlanEmi, :AvailedReAgeH, :ReAgeBucket, :DueBucket, :EligibilityMethod,");
		}
		sql.append(" :Version ,:LastMntBy,:LastMntOn,:RecordStatus,:RoleCode,:NextRoleCode,:TaskId,");
		sql.append(" :NextTaskId,:RecordType,:WorkflowId, :PromotionCode)");

		// Execute the SQL, binding the arguments.
		logger.trace(Literal.SQL + sql.toString());
		SqlParameterSource paramSource = new BeanPropertySqlParameterSource(financeMain);

		try {
			namedParameterJdbcTemplate.update(sql.toString(), paramSource);
		} catch (DuplicateKeyException e) {
			throw new ConcurrencyException(e);
		}

		logger.debug(Literal.LEAVING);
		return financeMain.getId();
	}

	@Override
	public void update(FinanceMain financeMain, TableType tableType, boolean wif) {
		logger.debug(Literal.ENTERING);

		// Prepare the SQL, ensure primary key will not be updated.
		StringBuilder sql = new StringBuilder("update ");
		if (wif) {
			sql.append(" WIFFinanceMain");
		} else {
			sql.append(" FinanceMain");
		}
		sql.append(tableType.getSuffix());
		sql.append(" set NumberOfTerms = :NumberOfTerms,GraceTerms=:GraceTerms, ");
		sql.append(" GrcPeriodEndDate = :GrcPeriodEndDate, AllowGrcPeriod = :AllowGrcPeriod,");
		sql.append(" GraceBaseRate = :GraceBaseRate, GraceSpecialRate = :GraceSpecialRate,");
		sql.append(" GrcPftRate = :GrcPftRate, GrcPftFrq = :GrcPftFrq,");
		sql.append(" NextGrcPftDate = :NextGrcPftDate, AllowGrcPftRvw = :AllowGrcPftRvw,");
		sql.append(" GrcPftRvwFrq = :GrcPftRvwFrq, NextGrcPftRvwDate = :NextGrcPftRvwDate,");
		sql.append(" AllowGrcCpz = :AllowGrcCpz, GrcCpzFrq = :GrcCpzFrq, NextGrcCpzDate = :NextGrcCpzDate,");
		sql.append(" RepayBaseRate = :RepayBaseRate, RepaySpecialRate = :RepaySpecialRate,");
		sql.append(" RepayProfitRate = :RepayProfitRate, RepayFrq = :RepayFrq, NextRepayDate = :NextRepayDate,");
		sql.append(" RepayPftFrq = :RepayPftFrq, NextRepayPftDate = :NextRepayPftDate,");
		sql.append(" AllowRepayRvw = :AllowRepayRvw, RepayRvwFrq = :RepayRvwFrq,");
		sql.append(" NextRepayRvwDate = :NextRepayRvwDate, AllowRepayCpz = :AllowRepayCpz,");
		sql.append(" RepayCpzFrq = :RepayCpzFrq, NextRepayCpzDate = :NextRepayCpzDate,");
		sql.append(" MaturityDate = :MaturityDate, CpzAtGraceEnd = :CpzAtGraceEnd, DownPayment = :DownPayment,");
		sql.append(" DownPayBank=:DownPayBank, DownPaySupl=:DownPaySupl, ReqRepayAmount = :ReqRepayAmount,");
		sql.append(" TotalProfit = :TotalProfit,TotalCpz= :TotalCpz, TotalGrossPft = :TotalGrossPft,");
		sql.append(" TotalGracePft= :TotalGracePft,TotalGraceCpz= :TotalGraceCpz,TotalGrossGrcPft= :TotalGrossGrcPft,");
		sql.append(
				" TotalRepayAmt= :TotalRepayAmt, GrcRateBasis = :GrcRateBasis, RepayRateBasis = :RepayRateBasis, FinType = :FinType,");
		sql.append(" FinRemarks = :FinRemarks, FinCcy = :FinCcy, ScheduleMethod = :ScheduleMethod,");
		sql.append(" ProfitDaysBasis = :ProfitDaysBasis, ReqMaturity = :ReqMaturity, CalTerms = :CalTerms,");
		sql.append(" CalMaturity = :CalMaturity, FirstRepay = :FirstRepay, LastRepay = :LastRepay,");
		sql.append(" FinStartDate = :FinStartDate, FinAmount = :FinAmount,FinContractDate= :FinContractDate,");
		sql.append(" FinRepaymentAmount = :FinRepaymentAmount, CustID = :CustID,Defferments = :Defferments,");
		sql.append(" PlanDeferCount= :PlanDeferCount, FinBranch =:FinBranch, FinSourceID= :FinSourceID,");
		sql.append(" AllowedDefRpyChange= :AllowedDefRpyChange, AvailedDefRpyChange= :AvailedDefRpyChange,");
		sql.append(" AllowedDefFrqChange= :AllowedDefFrqChange, AvailedDefFrqChange= :AvailedDefFrqChange,");
		sql.append(" RecalType=:RecalType, FinIsActive= :FinIsActive,FinAssetValue= :FinAssetValue,");
		sql.append(" disbAccountId= :disbAccountId, repayAccountId= :repayAccountId,");
		sql.append(" LastRepayDate= :LastRepayDate,LastRepayPftDate= :LastRepayPftDate,");
		sql.append(
				" LastRepayRvwDate= :LastRepayRvwDate, LastRepayCpzDate= :LastRepayCpzDate,AllowGrcRepay= :AllowGrcRepay,");
		sql.append(" GrcSchdMthd= :GrcSchdMthd, GrcMargin= :GrcMargin,RepayMargin= :RepayMargin,");
		sql.append(
				" FinCommitmentRef= :FinCommitmentRef, FinLimitRef=:FinLimitRef, DepreciationFrq= :DepreciationFrq, FinCurrAssetValue= :FinCurrAssetValue,");
		sql.append(" NextDepDate= :NextDepDate, LastDepDate= :LastDepDate,FinAccount=:FinAccount, ");
		sql.append(
				" FinCustPftAccount=:FinCustPftAccount,ClosingStatus= :ClosingStatus, FinApprovedDate= :FinApprovedDate,");
		sql.append(
				" AnualizedPercRate =:AnualizedPercRate , EffectiveRateOfReturn =:EffectiveRateOfReturn , FinRepayPftOnFrq =:FinRepayPftOnFrq ,");
		sql.append(
				" GrcProfitDaysBasis = :GrcProfitDaysBasis, StepFinance = :StepFinance, StepPolicy = :StepPolicy, StepType = :StepType,");
		sql.append(
				" AlwManualSteps = :AlwManualSteps, NoOfSteps = :NoOfSteps, ManualSchedule=:ManualSchedule , TakeOverFinance=:TakeOverFinance ,LinkedFinRef=:LinkedFinRef, ");
		sql.append(
				" GrcMinRate=:GrcMinRate, GrcMaxRate=:GrcMaxRate , RpyMinRate=:RpyMinRate, RpyMaxRate=:RpyMaxRate, ");
		sql.append(" GrcAdvBaseRate=:GrcAdvBaseRate ,GrcAdvMargin=:GrcAdvMargin , ");
		sql.append(
				" GrcAdvPftRate=:GrcAdvPftRate ,RpyAdvBaseRate=:RpyAdvBaseRate ,RpyAdvMargin=:RpyAdvMargin ,RpyAdvPftRate=:RpyAdvPftRate ,");
		sql.append(" SupplementRent=:SupplementRent , IncreasedCost=:IncreasedCost , ");
		sql.append(
				" FeeAccountId=:FeeAccountId , MinDownPayPerc=:MinDownPayPerc, TDSApplicable=:TDSApplicable,InsuranceAmt=:InsuranceAmt, AlwBPI=:AlwBPI , ");
		sql.append(
				" BpiTreatment=:BpiTreatment , PlanEMIHAlw=:PlanEMIHAlw , PlanEMIHMethod=:PlanEMIHMethod , PlanEMIHMaxPerYear=:PlanEMIHMaxPerYear , ");
		sql.append(
				" PlanEMIHMax=:PlanEMIHMax , PlanEMIHLockPeriod=:PlanEMIHLockPeriod , PlanEMICpz=:PlanEMICpz , CalRoundingMode=:CalRoundingMode ,RoundingTarget=:RoundingTarget, AlwMultiDisb=:AlwMultiDisb, FeeChargeAmt=:FeeChargeAmt, BpiAmount=:BpiAmount, DeductFeeDisb=:DeductFeeDisb, ");
		sql.append(
				"RvwRateApplFor =:RvwRateApplFor, SchCalOnRvw =:SchCalOnRvw,PastduePftCalMthd=:PastduePftCalMthd,DroppingMethod=:DroppingMethod,RateChgAnyDay=:RateChgAnyDay,PastduePftMargin=:PastduePftMargin,  FinCategory=:FinCategory, ProductCategory=:ProductCategory, ");
		if (!wif) {
			sql.append(
					" DroplineFrq= :DroplineFrq,FirstDroplineDate = :FirstDroplineDate,PftServicingODLimit = :PftServicingODLimit,");
			sql.append(
					" MigratedFinance = :MigratedFinance,ScheduleMaintained = :ScheduleMaintained, ScheduleRegenerated = :ScheduleRegenerated,FinCancelAc=:FinCancelAc,");
			sql.append(
					" LimitValid= :LimitValid, OverrideLimit= :OverrideLimit,FinPurpose=:FinPurpose, DeviationApproval=:DeviationApproval,FinPreApprovedRef=:FinPreApprovedRef, MandateID=:MandateID, ");
			sql.append(
					" FinStatus=:FinStatus , FinStsReason=:FinStsReason, InitiateUser=:InitiateUser, BankName=:BankName, Iban=:Iban, AccountType=:AccountType,  DdaReferenceNo=:DdaReferenceNo,");
			sql.append(" CustDSR=:CustDSR, JointAccount=:JointAccount, JointCustId=:JointCustId, ");
			sql.append(
					" DownPayAccount=:DownPayAccount,  SecurityDeposit = :SecurityDeposit, RcdMaintainSts=:RcdMaintainSts, FinRepayMethod=:FinRepayMethod, ");
			sql.append(
					" NextUserId=:NextUserId, Priority=:Priority, RolloverFrq=:RolloverFrq, NextRolloverDate=:NextRolloverDate, ShariaStatus = :ShariaStatus,InitiateDate= :InitiateDate, ");
			sql.append(
					" MMAId =:MMAId,AccountsOfficer =:AccountsOfficer,DsaCode = :DsaCode, ApplicationNo=:ApplicationNo, ReferralId =:ReferralId , DmaCode =:DmaCode , SalesDepartment =:SalesDepartment , QuickDisb =:QuickDisb , WifReference =:WifReference ,");
			sql.append(
					" UnPlanEMIHLockPeriod=:UnPlanEMIHLockPeriod , UnPlanEMICpz=:UnPlanEMICpz, ReAgeCpz=:ReAgeCpz, MaxUnplannedEmi=:MaxUnplannedEmi, MaxReAgeHolidays=:MaxReAgeHolidays , AvailedUnPlanEmi=:AvailedUnPlanEmi, AvailedReAgeH=:AvailedReAgeH,ReAgeBucket=:ReAgeBucket,EligibilityMethod=:EligibilityMethod, ");
		}
		sql.append(" Version = :Version,LastMntBy = :LastMntBy, LastMntOn = :LastMntOn,");
		sql.append(" RecordStatus= :RecordStatus, RoleCode = :RoleCode, NextRoleCode = :NextRoleCode,");
		sql.append(
				" TaskId = :TaskId, NextTaskId = :NextTaskId, RecordType = :RecordType, WorkflowId = :WorkflowId, PromotionCode = :PromotionCode");
		sql.append(" where FinReference = :FinReference");
		sql.append(QueryUtil.getConcurrencyCondition(tableType));

		// Execute the SQL, binding the arguments.
		logger.trace(Literal.SQL + sql.toString());
		SqlParameterSource paramSource = new BeanPropertySqlParameterSource(financeMain);
		int recordCount = namedParameterJdbcTemplate.update(sql.toString(), paramSource);

		// Check for the concurrency failure.
		if (recordCount == 0) {
			throw new ConcurrencyException();
		}

		logger.debug(Literal.LEAVING);
	}

	@Override
	public void delete(FinanceMain financeMain, TableType tableType, boolean wif, boolean finalize) {
		logger.debug(Literal.ENTERING);

		// Prepare the SQL.
		StringBuilder sql = new StringBuilder("delete from");
		if (wif) {
			sql.append(" WIFFinanceMain");
		} else {
			sql.append(" FinanceMain");
		}
		sql.append(tableType.getSuffix());
		sql.append(" where FinReference = :FinReference");
		if (tableType == TableType.MAIN_TAB || !finalize) {
			sql.append(QueryUtil.getConcurrencyCondition(tableType));
		}

		// Execute the SQL, binding the arguments.
		logger.trace(Literal.SQL + sql.toString());
		SqlParameterSource paramSource = new BeanPropertySqlParameterSource(financeMain);
		int recordCount = 0;

		try {
			recordCount = namedParameterJdbcTemplate.update(sql.toString(), paramSource);
		} catch (DataAccessException e) {
			throw new DependencyFoundException(e);
		}

		// Check for the concurrency failure.
		if (recordCount == 0) {
			throw new ConcurrencyException();
		}

		logger.debug(Literal.LEAVING);
	}

	/**
	 * This method updates the Record FinanceMain or FinanceMain_Temp. if Record not updated then throws
	 * DataAccessException with error 41004. update Finance Main Detail by key FinReference and Version
	 * 
	 * @param Finance
	 *            Main Detail (financeMain)
	 * @param type
	 *            (String) ""/_Temp/_View
	 * @return void
	 * @throws DataAccessException
	 * 
	 */

	public void updateInvestmentFinance(FinanceMain financeMain, String type) {
		int recordCount = 0;
		logger.debug("Entering");
		StringBuilder updateSql = new StringBuilder("Update ");

		updateSql.append(" FinanceMain");
		updateSql.append(StringUtils.trimToEmpty(type));
		updateSql.append(" Set FinType = :FinType,  FinCcy = :FinCcy, FinBranch = :FinBranch,");
		updateSql.append(" FinAmount = :FinAmount, FinStartDate = :FinStartDate, ");
		updateSql.append(" MaturityDate = :MaturityDate, CustID = :CustID,");
		updateSql.append(" RepayProfitRate = :RepayProfitRate, TotalRepayAmt= :TotalRepayAmt, ");
		updateSql.append(" TotalProfit = :TotalProfit, ProfitDaysBasis= :ProfitDaysBasis, ");
		updateSql.append(" ScheduleMethod = :ScheduleMethod, ");
		updateSql.append(" DisbAccountId = :DisbAccountId, RepayAccountId= :RepayAccountId, ");
		updateSql.append(" LastRepayDate = :LastRepayDate, LastRepayPftDate = :LastRepayPftDate, ");
		updateSql.append(" LastRepayRvwDate = :LastRepayRvwDate, LastRepayCpzDate = :LastRepayCpzDate, ");
		updateSql.append(" NumberOfTerms = :NumberOfTerms, GraceTerms=:GraceTerms, AllowGrcPeriod = :AllowGrcPeriod, ");
		updateSql.append(" AllowGrcPftRvw = :AllowGrcPftRvw, AllowGrcCpz = :AllowGrcCpz, ");
		updateSql.append(" AllowRepayRvw = :AllowRepayRvw, AllowRepayCpz = :AllowRepayCpz, ");
		updateSql.append(" CpzAtGraceEnd = :CpzAtGraceEnd, CalTerms = :CalTerms, ");
		updateSql.append(" Defferments = :Defferments, PlanDeferCount = :PlanDeferCount, ");
		updateSql.append(" AllowedDefRpyChange = :AllowedDefRpyChange, AvailedDefRpyChange = :AvailedDefRpyChange, ");
		updateSql.append(" AllowedDefFrqChange = :AllowedDefFrqChange, AvailedDefFrqChange = :AvailedDefFrqChange, ");
		updateSql.append(" FinIsActive = :FinIsActive, AllowGrcRepay = :AllowGrcRepay, ");
		updateSql.append(" FinRepayPftOnFrq = :FinRepayPftOnFrq, ");
		updateSql.append(" MigratedFinance = :MigratedFinance, ScheduleMaintained = :ScheduleMaintained, ");
		updateSql.append(
				" ScheduleRegenerated = :ScheduleRegenerated, Blacklisted = :Blacklisted, GrcProfitDaysBasis = :GrcProfitDaysBasis,");
		updateSql.append(
				" StepFinance = :StepFinance, StepPolicy = :StepPolicy, AlwManualSteps = :AlwManualSteps, NoOfSteps = :NoOfSteps, StepType = :StepType, DsaCode = :DsaCode, ");
		updateSql.append(
				" DroplineFrq= :DroplineFrq,FirstDroplineDate = :FirstDroplineDate,PftServicingODLimit = :PftServicingODLimit,");
		updateSql.append(" Version = :Version,LastMntBy = :LastMntBy, LastMntOn = :LastMntOn,");
		updateSql.append(" RecordStatus= :RecordStatus, RoleCode = :RoleCode, NextRoleCode = :NextRoleCode,");
		updateSql.append(
				" TaskId = :TaskId, NextTaskId = :NextTaskId, RecordType = :RecordType, WorkflowId = :WorkflowId, NextUserId=:NextUserId, Priority=:Priority, MinDownPayPerc=:MinDownPayPerc");
		updateSql.append(" ,PromotionCode = :PromotionCode");
		updateSql.append(" Where FinReference =:FinReference");
		if (!type.endsWith("_Temp")) {
			updateSql.append("  AND Version= :Version-1");
		}

		logger.debug("updateSql: " + updateSql.toString());

		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(financeMain);
		recordCount = this.namedParameterJdbcTemplate.update(updateSql.toString(), beanParameters);

		if (recordCount <= 0) {
			throw new ConcurrencyException();
		}
		logger.debug("Leaving");
	}

	/**
	 * Method to return the customer details based on given customer id.
	 */
	public boolean isFinReferenceExists(final String id, String type, boolean isWIF) {
		logger.debug("Entering");

		FinanceMain financeMain = new FinanceMain();
		financeMain.setId(id);

		StringBuilder selectSql = new StringBuilder();
		selectSql.append("SELECT FinReference");

		if (isWIF) {
			selectSql.append(" From WIFFinanceMain");
		} else {
			selectSql.append(" From FinanceMain");
		}

		selectSql.append(StringUtils.trimToEmpty(type));
		selectSql.append(" Where FinReference =:FinReference");

		logger.debug("selectSql: " + selectSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(financeMain);

		String finReference = null;
		try {
			finReference = this.namedParameterJdbcTemplate.queryForObject(selectSql.toString(), beanParameters,
					String.class);
		} catch (EmptyResultDataAccessException e) {
			logger.warn("Exception: ", e);
			finReference = null;
		}
		financeMain = null;
		logger.debug("Leaving");
		return finReference == null ? false : true;
	}

	@Override
	public void listUpdate(ArrayList<FinanceMain> financeMain, String type) {
		logger.debug("Entering");
		StringBuilder updateSql = new StringBuilder("Update FinanceMain");
		updateSql.append(StringUtils.trimToEmpty(type));
		updateSql.append(" Set NumberOfTerms = :NumberOfTerms, GraceTerms=:GraceTerms, ");
		updateSql.append(" GrcPeriodEndDate = :GrcPeriodEndDate, AllowGrcPeriod = :AllowGrcPeriod,");
		updateSql.append(" GraceBaseRate = :GraceBaseRate, GraceSpecialRate = :GraceSpecialRate,");
		updateSql.append(" GrcPftRate = :GrcPftRate, GrcPftFrq = :GrcPftFrq,");
		updateSql.append(" NextGrcPftDate = :NextGrcPftDate, AllowGrcPftRvw = :AllowGrcPftRvw,");
		updateSql.append(" GrcPftRvwFrq = :GrcPftRvwFrq, NextGrcPftRvwDate = :NextGrcPftRvwDate,");
		updateSql.append(" AllowGrcCpz = :AllowGrcCpz, GrcCpzFrq = :GrcCpzFrq, NextGrcCpzDate = :NextGrcCpzDate,");
		updateSql.append(" RepayBaseRate = :RepayBaseRate, RepaySpecialRate = :RepaySpecialRate,");
		updateSql.append(" RepayProfitRate = :RepayProfitRate, RepayFrq = :RepayFrq, NextRepayDate = :NextRepayDate,");
		updateSql.append(" RepayPftFrq = :RepayPftFrq, NextRepayPftDate = :NextRepayPftDate,");
		updateSql.append(" AllowRepayRvw = :AllowRepayRvw, RepayRvwFrq = :RepayRvwFrq,");
		updateSql.append(" NextRepayRvwDate = :NextRepayRvwDate, AllowRepayCpz = :AllowRepayCpz,");
		updateSql.append(" RepayCpzFrq = :RepayCpzFrq, NextRepayCpzDate = :NextRepayCpzDate,");
		updateSql.append(" MaturityDate = :MaturityDate, CpzAtGraceEnd = :CpzAtGraceEnd, DownPayment = :DownPayment,");
		updateSql.append(
				" DownPayBank=:DownPayBank, DownPaySupl=:DownPaySupl, DownPayAccount=:DownPayAccount,  SecurityDeposit = :SecurityDeposit,");
		updateSql.append(
				" ReqRepayAmount = :ReqRepayAmount, TotalProfit = :TotalProfit,TotalCpz= :TotalCpz, TotalGrossPft = :TotalGrossPft,");
		updateSql.append(
				" TotalGracePft= :TotalGracePft,TotalGraceCpz= :TotalGraceCpz,TotalGrossGrcPft= :TotalGrossGrcPft,");
		updateSql.append(
				" TotalRepayAmt= :TotalRepayAmt, GrcRateBasis = :GrcRateBasis, RepayRateBasis = :RepayRateBasis, FinType = :FinType,");
		updateSql.append(" FinRemarks = :FinRemarks, FinCcy = :FinCcy, ScheduleMethod = :ScheduleMethod,");
		updateSql.append(" ProfitDaysBasis = :ProfitDaysBasis, ReqMaturity = :ReqMaturity, CalTerms = :CalTerms,");
		updateSql.append(" CalMaturity = :CalMaturity, FirstRepay = :FirstRepay, LastRepay = :LastRepay,");
		updateSql.append(" FinStartDate = :FinStartDate, FinAmount = :FinAmount,FinContractDate=:FinContractDate,");
		updateSql.append(" FinRepaymentAmount = :FinRepaymentAmount, CustID = :CustID,Defferments = :Defferments, ");
		updateSql.append(" PlanDeferCount= :PlanDeferCount, FinBranch =:FinBranch, FinSourceID= :FinSourceID,");
		updateSql.append(" AllowedDefRpyChange= :AllowedDefRpyChange, AvailedDefRpyChange= :AvailedDefRpyChange,");
		updateSql.append(" AllowedDefFrqChange= :AllowedDefFrqChange, AvailedDefFrqChange= :AvailedDefFrqChange,");
		updateSql.append(" RecalType=:RecalType, FinIsActive= :FinIsActive,FinAssetValue= :FinAssetValue,");
		updateSql.append(" disbAccountId= :disbAccountId, repayAccountId= :repayAccountId,FinPurpose=:FinPurpose,");
		updateSql.append(
				" LastRepayDate= :LastRepayDate,LastRepayPftDate= :LastRepayPftDate, FinStatus=:FinStatus, FinStsReason=:FinStsReason,");
		updateSql.append(
				" InitiateUser=:InitiateUser, LastRepayRvwDate= :LastRepayRvwDate, LastRepayCpzDate= :LastRepayCpzDate,AllowGrcRepay= :AllowGrcRepay,");
		updateSql.append(
				" BankName=:BankName, Iban=:Iban, AccountType=:AccountType, DdaReferenceNo=:DdaReferenceNo, GrcSchdMthd= :GrcSchdMthd, GrcMargin= :GrcMargin, RepayMargin= :RepayMargin,");
		updateSql.append(
				" FinCommitmentRef= :FinCommitmentRef, FinLimitRef=:FinLimitRef, DepreciationFrq= :DepreciationFrq, FinCurrAssetValue= :FinCurrAssetValue,");
		updateSql.append(
				" NextDepDate= :NextDepDate, LastDepDate= :LastDepDate,FinAccount=:FinAccount,FinCancelAc=:FinCancelAc,");
		updateSql.append(" FinCustPftAccount= :FinCustPftAccount, ClosingStatus= :ClosingStatus, ");
		updateSql.append(
				" FinApprovedDate= :FinApprovedDate, FeeChargeAmt=:FeeChargeAmt, BpiAmount=:BpiAmount, DeductFeeDisb=:DeductFeeDisb, LimitValid= :LimitValid, OverrideLimit= :OverrideLimit,");
		updateSql.append(" AnualizedPercRate =:AnualizedPercRate , EffectiveRateOfReturn =:EffectiveRateOfReturn , ");
		updateSql.append(" FinRepayPftOnFrq =:FinRepayPftOnFrq , CustDSR=:CustDSR, ");
		updateSql.append(
				" JointAccount=:JointAccount,JointCustId=:JointCustId, DownPayAccount=:DownPayAccount,  SecurityDeposit =:SecurityDeposit, RcdMaintainSts=:RcdMaintainSts,");
		updateSql.append(" FinRepayMethod=:FinRepayMethod, ");
		updateSql.append(
				" GrcProfitDaysBasis = :GrcProfitDaysBasis, StepFinance = :StepFinance, StepPolicy = :StepPolicy, StepType = :StepType,");
		updateSql.append(
				" AlwManualSteps = :AlwManualSteps, NoOfSteps = :NoOfSteps,  ManualSchedule=:ManualSchedule , TakeOverFinance=:TakeOverFinance ,");
		updateSql.append(" LinkedFinRef=:LinkedFinRef,");
		updateSql.append(
				" GrcMinRate=:GrcMinRate, GrcMaxRate=:GrcMaxRate , RpyMinRate=:RpyMinRate, RpyMaxRate=:RpyMaxRate, ");
		updateSql.append(" GrcAdvBaseRate=:GrcAdvBaseRate ,GrcAdvMargin=:GrcAdvMargin , ");
		updateSql.append(
				" GrcAdvPftRate=:GrcAdvPftRate ,RpyAdvBaseRate=:RpyAdvBaseRate ,RpyAdvMargin=:RpyAdvMargin ,RpyAdvPftRate=:RpyAdvPftRate ,");
		updateSql.append(" SupplementRent=:SupplementRent , IncreasedCost=:IncreasedCost , RolloverFrq=:RolloverFrq, ");
		updateSql.append(" NextRolloverDate=:NextRolloverDate, ShariaStatus = :ShariaStatus, DsaCode = :DsaCode, ");
		updateSql.append(
				" DroplineFrq= :DroplineFrq,FirstDroplineDate = :FirstDroplineDate,PftServicingODLimit = :PftServicingODLimit, AlwBPI=:AlwBPI , ");
		updateSql.append(
				" BpiTreatment=:BpiTreatment , PlanEMIHAlw=:PlanEMIHAlw , PlanEMIHMethod=:PlanEMIHMethod , PlanEMIHMaxPerYear=:PlanEMIHMaxPerYear , ");
		updateSql.append(
				" PlanEMIHMax=:PlanEMIHMax , PlanEMIHLockPeriod=:PlanEMIHLockPeriod , PlanEMICpz=:PlanEMICpz , CalRoundingMode=:CalRoundingMode ,RoundingTarget=:RoundingTarget, AlwMultiDisb=:AlwMultiDisb, ApplicationNo=:ApplicationNo,");
		updateSql.append(
				" ReferralId =:ReferralId , DmaCode =:DmaCode , SalesDepartment =:SalesDepartment , QuickDisb =:QuickDisb , WifReference =:WifReference ,");
		updateSql.append(
				" UnPlanEMIHLockPeriod=:UnPlanEMIHLockPeriod , UnPlanEMICpz=:UnPlanEMICpz, ReAgeCpz=:ReAgeCpz, MaxUnplannedEmi=:MaxUnplannedEmi, MaxReAgeHolidays=:MaxReAgeHolidays ,AvailedUnPlanEmi=:AvailedUnPlanEmi, AvailedReAgeH=:AvailedReAgeH, ReAgeBucket=:ReAgeBucket, FinCategory=:FinCategory, ProductCategory=:ProductCategory, ");
		updateSql.append(
				" Version = :Version,LastMntBy = :LastMntBy, LastMntOn = :LastMntOn, RecordStatus= :RecordStatus, RoleCode = :RoleCode, NextRoleCode = :NextRoleCode,");
		updateSql.append(
				" TaskId = :TaskId, NextTaskId = :NextTaskId, NextUserId=:NextUserId, Priority=:Priority, RecordType = :RecordType, WorkflowId = :WorkflowId, MinDownPayPerc=:MinDownPayPerc");
		updateSql.append(" PromotionCode = :PromotionCode");
		updateSql.append(" Where FinReference =:FinReference");
		logger.debug("updateSql: " + updateSql.toString());

		SqlParameterSource[] beanParameters = SqlParameterSourceUtils.createBatch(financeMain.toArray());
		this.namedParameterJdbcTemplate.batchUpdate(updateSql.toString(), beanParameters);

		logger.debug("Leaving");
	}

	@Override
	public void updateFinAccounts(String finReference, String finAccount) {
		int recordCount = 0;

		FinanceMain financeMain = new FinanceMain();
		financeMain.setFinReference(finReference);
		financeMain.setFinAccount(finAccount);

		logger.debug("Entering");
		StringBuilder updateSql = new StringBuilder("Update FinanceMain");
		updateSql.append(" Set FinAccount =:FinAccount ");
		updateSql.append(" Where FinReference =:FinReference");

		logger.debug("updateSql: " + updateSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(financeMain);
		recordCount = this.namedParameterJdbcTemplate.update(updateSql.toString(), beanParameters);

		if (recordCount <= 0) {
			throw new ConcurrencyException();
		}
		logger.debug("Leaving");
	}

	/**
	 * Method for get the Actual Profit Amount
	 * 
	 * @throws ClassNotFoundException
	 * @throws DataAccessException
	 */
	@Override
	public List<BigDecimal> getActualPftBal(String finReference, String type) {
		logger.debug("Entering");

		FinanceMain financeMain = new FinanceMain();
		financeMain.setFinReference(finReference);

		StringBuilder selectSql = new StringBuilder();
		selectSql.append(" SELECT TotalProfit, TotalCpz From FinanceMain");
		selectSql.append(StringUtils.trimToEmpty(type));
		selectSql.append(" Where FinReference =:FinReference");

		logger.debug("selectSql: " + selectSql.toString());
		RowMapper<FinanceMain> typeRowMapper = ParameterizedBeanPropertyRowMapper.newInstance(FinanceMain.class);
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(financeMain);
		logger.debug("Leaving");

		try {
			financeMain = this.namedParameterJdbcTemplate.queryForObject(selectSql.toString(), beanParameters,
					typeRowMapper);
		} catch (DataAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		List<BigDecimal> list = new ArrayList<BigDecimal>();
		if (financeMain!=null) {
			list.add(financeMain.getTotalProfit());
			list.add(financeMain.getTotalCpz());
		}else{
			list.add(BigDecimal.ZERO);
			list.add(BigDecimal.ZERO);
		}

		return list;
	}

	@Override
	public void updateRepaymentAmount(String finReference, BigDecimal finAmount, BigDecimal repaymentAmount,
			String finStatus, String finStsReason, boolean isCancelProc, boolean pftFullyPaid) {
		logger.debug("Entering");

		int recordCount = 0;
		FinanceMain financeMain = new FinanceMain();
		financeMain.setFinReference(finReference);
		financeMain.setFinRepaymentAmount(repaymentAmount);
		financeMain.setFinStatus(finStatus);
		financeMain.setFinStsReason(finStsReason);

		StringBuilder updateSql = new StringBuilder("Update FinanceMain");
		updateSql.append(" Set FinRepaymentAmount =:FinRepaymentAmount ");
		if (finAmount.subtract(repaymentAmount).compareTo(BigDecimal.ZERO) <= 0) {
			if (ImplementationConstants.REPAY_HIERARCHY_METHOD.equals(RepayConstants.REPAY_HIERARCHY_FCIP)) {
				financeMain.setFinIsActive(false);
				financeMain.setClosingStatus(FinanceConstants.CLOSE_STATUS_MATURED);
				updateSql.append(" , FinIsActive = :FinIsActive, ClosingStatus =:ClosingStatus ");
			} else if (pftFullyPaid
					&& ImplementationConstants.REPAY_HIERARCHY_METHOD.equals(RepayConstants.REPAY_HIERARCHY_FCPI)) {
				financeMain.setFinIsActive(false);
				financeMain.setClosingStatus(FinanceConstants.CLOSE_STATUS_MATURED);
				updateSql.append(" , FinIsActive = :FinIsActive, ClosingStatus =:ClosingStatus ");
			}
		} else if (isCancelProc) {
			financeMain.setFinIsActive(true);
			financeMain.setClosingStatus(null);
			updateSql.append(" , FinIsActive = :FinIsActive, ClosingStatus =:ClosingStatus ");
		}

		updateSql.append(" , FinStatus = :FinStatus , FinStsReason = :FinStsReason ");
		updateSql.append(" Where FinReference =:FinReference");

		logger.debug("updateSql: " + updateSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(financeMain);
		recordCount = this.namedParameterJdbcTemplate.update(updateSql.toString(), beanParameters);

		if (recordCount <= 0) {
			throw new ConcurrencyException();
		}
	}

	/**
	 * Method for get the Finance Details and FinanceShedule Details
	 */
	@Override
	public List<FinanceEnquiry> getFinanceDetailsByCustId(long custId) {
		logger.debug("Entering");

		MapSqlParameterSource source = new MapSqlParameterSource();

		StringBuilder selectSql = new StringBuilder();
		selectSql.append(
				" SELECT FinReference , FinBranch , FinType , FinCcy , ScheduleMethod , ProfitDaysBasis , FinStartDate , NumberOfTerms , ");
		selectSql.append(
				" CustID , FinAmount , GrcPeriodEndDate , MaturityDate , FinRepaymentAmount , FinIsActive , AllowGrcPeriod , ");
		selectSql.append(" LovDescFinTypeName, ");
		selectSql.append(
				" LovDescCustCIF , LovDescCustShrtName , LovDescFinBranchName , Blacklisted , LovDescFinScheduleOn , FeeChargeAmt , ");
		selectSql.append(
				" ClosingStatus , CustTypeCtg , GraceTerms , lovDescFinDivision , lovDescProductCodeName , Defferments ");
		selectSql.append(" FROM FinanceEnquiry_View ");
		selectSql.append(" Where CustID=:CustID and (ClosingStatus is null or ClosingStatus != 'C')");
		selectSql.append(" ORDER BY FinType, FinCcy ");

		source.addValue("CustID", custId);
		RowMapper<FinanceEnquiry> typeRowMapper = ParameterizedBeanPropertyRowMapper.newInstance(FinanceEnquiry.class);

		logger.debug("selectSql: " + selectSql.toString());
		logger.debug("Leaving");
		return this.namedParameterJdbcTemplate.query(selectSql.toString(), source, typeRowMapper);

	}

	@Override
	public void updateCustCIF(long custID, String finReference) {
		logger.debug("Entering");

		int recordCount = 0;
		FinanceMain financeMain = new FinanceMain();
		financeMain.setFinReference(finReference);
		financeMain.setCustID(custID);

		StringBuilder updateSql = new StringBuilder("Update FinanceMain_Temp");
		updateSql.append(" Set CustID =:CustID ");
		updateSql.append(" Where FinReference =:FinReference");

		logger.debug("updateSql: " + updateSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(financeMain);
		recordCount = this.namedParameterJdbcTemplate.update(updateSql.toString(), beanParameters);

		if (recordCount <= 0) {
			throw new ConcurrencyException();
		}
		logger.debug("Leaving");
	}

	@Override
	public void updateFinBlackListStatus(String finReference) {
		logger.debug("Entering");

		int recordCount = 0;
		FinanceMain financeMain = new FinanceMain();
		financeMain.setFinReference(finReference);
		financeMain.setBlacklisted(true);

		StringBuilder updateSql = new StringBuilder("Update FinanceMain");
		updateSql.append(" Set Blacklisted =:Blacklisted ");
		updateSql.append(" Where FinReference =:FinReference");

		logger.debug("updateSql: " + updateSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(financeMain);
		recordCount = this.namedParameterJdbcTemplate.update(updateSql.toString(), beanParameters);

		if (recordCount <= 0) {
			throw new ConcurrencyException();
		}
		logger.debug("Leaving");
	}

	@Override
	public List<String> getFinanceReferenceList() {
		logger.debug("Entering");

		StringBuilder selectSql = new StringBuilder("SELECT FinReference From FinanceMain");

		logger.debug("selectSql: " + selectSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource("");
		logger.debug("Leaving");
		return this.namedParameterJdbcTemplate.queryForList(selectSql.toString(), beanParameters, String.class);
	}

	@Override
	public FinanceSummary getFinanceProfitDetails(String finRef) {
		logger.debug(Literal.ENTERING);

		// Prepare the SQL.
		StringBuilder sql = new StringBuilder("select * from FinanceProfitEnquiry_View");
		sql.append(" where FinReference = :FinReference");

		// Execute the SQL, binding the arguments.
		logger.trace(Literal.SQL + sql);
		MapSqlParameterSource paramSource = new MapSqlParameterSource();
		paramSource.addValue("FinReference", finRef);

		RowMapper<FinanceSummary> rowMapper = ParameterizedBeanPropertyRowMapper.newInstance(FinanceSummary.class);

		FinanceSummary summary = new FinanceSummary();
		try {
			summary = this.namedParameterJdbcTemplate.queryForObject(sql.toString(), paramSource, rowMapper);
		} catch (EmptyResultDataAccessException e) {
			logger.warn(Literal.EXCEPTION, e);
		}

		logger.debug(Literal.LEAVING);
		return summary;
	}

	/**
	 * Method for fetching List Of IJARAH Finance for Bulk Rate Change
	 */
	@Override
	public List<BulkProcessDetails> getIjaraBulkRateFinList(Date fromDate, Date toDate) {
		logger.debug("Entering");

		BulkProcessDetails changeFinance = new BulkProcessDetails();
		changeFinance.setLovDescEventFromDate(fromDate);
		changeFinance.setLovDescEventToDate(toDate);

		StringBuilder selectSql = new StringBuilder(" SELECT FinReference, FinType, ");
		selectSql.append(" FinCcy, ScheduleMethod, ProfitDaysBasis, CustCIF, FinBranch, ");
		selectSql.append(" ProductCode,  MIN(SchDate) EventFromDate, MAX(SchDate) EventToDate ");
		selectSql.append(
				" FROM IjarahFinance_View WHERE SchDate BETWEEN :LovDescEventFromDate AND :LovDescEventToDate ");
		selectSql.append(
				" GROUP BY FinReference ,FinType ,FinCcy ,ScheduleMethod , ProfitDaysBasis ,CustCIF ,FinBranch ,ProductCode ");

		logger.debug("selectSql: " + selectSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(changeFinance);
		RowMapper<BulkProcessDetails> typeRowMapper = ParameterizedBeanPropertyRowMapper
				.newInstance(BulkProcessDetails.class);

		logger.debug("Leaving");
		return this.namedParameterJdbcTemplate.query(selectSql.toString(), beanParameters, typeRowMapper);
	}

	/**
	 * Method for Fetch List of Finance for Bulk Deferment Process
	 */
	@Override
	public List<BulkDefermentChange> getBulkDefermentFinList(Date fromDate, Date toDate) {
		logger.debug("Entering");

		BulkDefermentChange changeFinance = new BulkDefermentChange();
		changeFinance.setEventFromDate(fromDate);
		changeFinance.setEventToDate(toDate);

		StringBuilder selectSql = new StringBuilder(" SELECT FinReference, FinType, ");
		selectSql.append(" FinCcy, ScheduleMethod, ProfitDaysBasis, CustCIF, FinBranch, ");
		selectSql.append(" ProductCode, EventFromDate ");
		selectSql.append(
				" FROM BulkDefermentFinance_View WHERE EventFromDate BETWEEN :EventFromDate AND :EventToDate )");

		logger.debug("selectSql: " + selectSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(changeFinance);
		RowMapper<BulkDefermentChange> typeRowMapper = ParameterizedBeanPropertyRowMapper
				.newInstance(BulkDefermentChange.class);

		logger.debug("Leaving");
		return this.namedParameterJdbcTemplate.query(selectSql.toString(), beanParameters, typeRowMapper);
	}

	/**
	 * Reject Finance Details Saving For Reinstance of Finance Process
	 */
	public Boolean saveRejectFinanceDetails(FinanceMain financeMain) {
		logger.debug("Entering");

		saveRejectFinanace(financeMain);

		saveRejectedChildDetail(
				"INSERT INTO RejectDocumentDetails SELECT * FROM  DocumentDetails_Temp WHERE ReferenceId = :FinReference",
				financeMain);
		saveFinanceDetails("FinAgreementDetail_Temp", "RejectFinAgreementDetail", financeMain);
		saveFinanceDetails("FinanceEligibilityDetail", "RejectFinanceEligibilityDetail", financeMain);
		saveFinanceDetails("FinanceScoreHeader", "RejectFinanceScoreHeader", financeMain);
		saveFinanceDetails("FinContributorHeader_Temp", "RejectFinContributorHeader", financeMain);
		saveFinanceDetails("FinContributorDetail_Temp", "RejectFinContributorDetail", financeMain);
		saveFinanceDetails("FinDisbursementDetails_Temp", "RejectFinDisbursementdetails", financeMain);
		saveFinanceDetails("FinRepayinstruction_Temp", "RejectFinRepayinstruction", financeMain);
		saveFinanceDetails("FinScheduledetails_Temp", "RejectFinScheduledetails", financeMain);
		saveFinanceDetails("FinDedupDetail", "RejectFinDedupDetail", financeMain);
		saveFinanceDetails("FinBlackListDetail", "RejectFinBlackListDetail", financeMain);
		saveFinanceDetails("FinPoliceCaseDetail", "RejectFinPoliceCaseDetail", financeMain);
		saveFinanceDetails("FinODPenaltyRates_Temp", "RejectFinODPenaltyRates", financeMain);
		saveFinanceDetails("FinFeeCharges_Temp", "RejectFinFeeCharges", financeMain);

		StringBuilder insertSql = new StringBuilder();
		insertSql.append(
				" INSERT INTO RejectFinanceScoreDetail SELECT D.HeaderID,  D.SubGroupID, D.RuleId, D.MaxScore, D.ExecScore ");
		insertSql
				.append(" FROM FinanceScoreDetail D INNER JOIN RejectFinanceScoreHeader H ON D.HeaderID = H.HeaderId ");
		insertSql.append(" WHERE FinReference = :FinReference ");
		saveRejectedChildDetail(insertSql.toString(), financeMain);
		insertSql.delete(0, insertSql.length());

		return true;
	}

	private void saveFinanceDetails(String fromTable, String toTable, FinanceMain financeMain) {
		logger.debug("Entering");

		StringBuilder insertSql = new StringBuilder();
		insertSql.append(" INSERT INTO ");
		insertSql.append(toTable);
		insertSql.append(" SELECT * FROM ");
		insertSql.append(fromTable);
		insertSql.append(" WHERE FinReference = :FinReference ");

		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(financeMain);
		this.namedParameterJdbcTemplate.update(insertSql.toString(), beanParameters);
		logger.debug("Leaving");
	}

	private void saveRejectedChildDetail(String insertSql, FinanceMain financeMain) {
		logger.debug("Entering");
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(financeMain);
		this.namedParameterJdbcTemplate.update(insertSql, beanParameters);
		logger.debug("Leaving");
	}

	/***
	 * Method to save finance detail snap shot.
	 * 
	 * @param financeMain
	 */
	public void saveFinanceSnapshot(FinanceMain financeMain) {
		logger.debug("Entering");
		saveFinanceDetails("FinScheduledetails", "FinScheduleDetails_Log", financeMain);
		logger.debug("Leaving");
	}

	/**
	 * This method insert new Records into FinanceMain or FinanceMain_Temp.
	 * 
	 * save Finance Main Detail
	 * 
	 * @param Finance
	 *            Main Detail (financeMain)
	 * @param type
	 *            (String) ""/_Temp/_View
	 * @return void
	 * @throws DataAccessException
	 * 
	 */
	public String saveRejectFinanace(FinanceMain financeMain) {
		logger.debug("Entering");

		StringBuilder insertSql = new StringBuilder("Insert Into ");
		insertSql.append(" RejectFinanceMain ");
		insertSql.append(" (FinReference, GraceTerms,  NumberOfTerms, GrcPeriodEndDate, AllowGrcPeriod,");
		insertSql.append(" GraceBaseRate, GraceSpecialRate, GrcPftRate, GrcPftFrq,NextGrcPftDate, AllowGrcPftRvw,");
		insertSql.append(" GrcPftRvwFrq, NextGrcPftRvwDate, AllowGrcCpz, GrcCpzFrq, NextGrcCpzDate,RepayBaseRate,");
		insertSql.append(" RepaySpecialRate, RepayProfitRate, RepayFrq, NextRepayDate, RepayPftFrq, NextRepayPftDate,");
		insertSql.append(" AllowRepayRvw,RepayRvwFrq, NextRepayRvwDate, AllowRepayCpz, RepayCpzFrq, NextRepayCpzDate,");
		insertSql.append(
				" MaturityDate, CpzAtGraceEnd,DownPayment, DownPayBank, DownPaySupl, ReqRepayAmount, TotalProfit,");
		insertSql.append(" TotalCpz,TotalGrossPft,TotalGracePft, TotalGraceCpz,TotalGrossGrcPft, TotalRepayAmt,");
		insertSql.append("  GrcRateBasis, RepayRateBasis,FinType,FinRemarks, FinCcy, ScheduleMethod,FinContractDate,");
		insertSql.append(" ProfitDaysBasis, ReqMaturity, CalTerms, CalMaturity, FirstRepay, LastRepay,");
		insertSql.append(" FinStartDate, FinAmount, FinRepaymentAmount, CustID, Defferments,PlanDeferCount,");
		insertSql.append(" FinBranch, FinSourceID, AllowedDefRpyChange, AvailedDefRpyChange, AllowedDefFrqChange,");
		insertSql.append(
				" AvailedDefFrqChange, RecalType, FinIsActive,FinAssetValue, disbAccountId, repayAccountId, FinCancelAc, ");
		insertSql.append(
				" LastRepayDate, LastRepayPftDate, LastRepayRvwDate, LastRepayCpzDate, AllowGrcRepay, GrcSchdMthd,");
		insertSql.append(" GrcMargin, RepayMargin, FinCommitmentRef, FinLimitRef, DepreciationFrq, FinCurrAssetValue,");
		insertSql.append(" NextDepDate, LastDepDate, FinAccount, FinCustPftAccount, ClosingStatus, FinApprovedDate, ");
		insertSql.append(" DedupFound,SkipDedup,Blacklisted,");
		insertSql.append(
				" GrcProfitDaysBasis, StepFinance , StepPolicy, StepType, AlwManualSteps, NoOfSteps, ManualSchedule , TakeOverFinance ,");
		insertSql.append(" AnualizedPercRate , EffectiveRateOfReturn , FinRepayPftOnFrq, ");
		insertSql.append(" LinkedFinRef, ");
		insertSql.append(" GrcMinRate, GrcMaxRate, RpyMinRate, RpyMaxRate,  ");
		insertSql.append(
				" GrcAdvBaseRate ,GrcAdvMargin ,GrcAdvPftRate ,RpyAdvBaseRate ,RpyAdvMargin ,RpyAdvPftRate , SupplementRent, IncreasedCost, ");
		insertSql.append(" InvestmentRef, MigratedFinance, ScheduleMaintained, ScheduleRegenerated,CustDSR,");
		insertSql.append(
				" FeeChargeAmt, BpiAmount, DeductFeeDisb, LimitValid, OverrideLimit,FinPurpose,DeviationApproval,FinPreApprovedRef,MandateID,FinStatus, FinStsReason, initiateUser, BankName, Iban, AccountType, DdaReferenceNo, ");
		insertSql.append(
				" JointAccount,JointCustId,DownPayAccount, SecurityDeposit, RcdMaintainSts,FinRepayMethod, AlwBPI , BpiTreatment , PlanEMIHAlw , ");
		insertSql.append(
				" PlanEMIHMethod , PlanEMIHMaxPerYear , PlanEMIHMax , PlanEMIHLockPeriod , PlanEMICpz , CalRoundingMode ,RoundingTarget, AlwMultiDisb, ");
		insertSql.append(" NextUserId, ");
		insertSql.append(
				" Priority, RolloverFrq, NextRolloverDate, ShariaStatus, DsaCode, feeAccountId,MinDownPayPerc, MMAId, InitiateDate,TDSApplicable,AccountsOfficer,ApplicationNo,");
		insertSql.append(
				" ReferralId, DmaCode, SalesDepartment, QuickDisb, WifReference, UnPlanEMIHLockPeriod , UnPlanEMICpz, ReAgeCpz, MaxUnplannedEmi, MaxReAgeHolidays, AvailedUnPlanEmi, AvailedReAgeH, RvwRateApplFor ,SchCalOnRvw,PastduePftCalMthd,DroppingMethod,RateChgAnyDay,PastduePftMargin,  FinCategory, ProductCategory,");
		insertSql.append(" Version , LastMntBy, LastMntOn, RecordStatus, RoleCode, NextRoleCode, TaskId,");
		insertSql.append(" NextTaskId, RecordType, WorkflowId, RejectStatus, RejectReason, DueBucket)");
		insertSql.append(" Values(:FinReference,:GraceTerms, :NumberOfTerms, :GrcPeriodEndDate, :AllowGrcPeriod,");
		insertSql.append(" :GraceBaseRate, :GraceSpecialRate,:GrcPftRate,:GrcPftFrq,:NextGrcPftDate,:AllowGrcPftRvw,");
		insertSql.append(" :GrcPftRvwFrq,:NextGrcPftRvwDate,:AllowGrcCpz,:GrcCpzFrq,:NextGrcCpzDate,:RepayBaseRate,");
		insertSql
				.append(" :RepaySpecialRate,:RepayProfitRate,:RepayFrq,:NextRepayDate,:RepayPftFrq,:NextRepayPftDate,");
		insertSql.append(
				" :AllowRepayRvw,:RepayRvwFrq,:NextRepayRvwDate,:AllowRepayCpz,:RepayCpzFrq,:NextRepayCpzDate,");
		insertSql.append(
				" :MaturityDate,:CpzAtGraceEnd,:DownPayment, :DownPayBank, :DownPaySupl, :ReqRepayAmount,:TotalProfit,");
		insertSql.append(" :TotalCpz,:TotalGrossPft,:TotalGracePft,:TotalGraceCpz,:TotalGrossGrcPft, :TotalRepayAmt,");
		insertSql.append(
				" :GrcRateBasis,:RepayRateBasis, :FinType,:FinRemarks,:FinCcy,:ScheduleMethod,:FinContractDate,");
		insertSql.append(" :ProfitDaysBasis,:ReqMaturity,:CalTerms,:CalMaturity,:FirstRepay,:LastRepay,");
		insertSql.append(" :FinStartDate,:FinAmount,:FinRepaymentAmount,:CustID,:Defferments,:PlanDeferCount,");
		insertSql
				.append(" :FinBranch, :FinSourceID, :AllowedDefRpyChange, :AvailedDefRpyChange, :AllowedDefFrqChange,");
		insertSql.append(
				" :AvailedDefFrqChange, :RecalType, :FinIsActive,:FinAssetValue, :disbAccountId, :repayAccountId,:FinCancelAc, ");
		insertSql.append(
				" :LastRepayDate, :LastRepayPftDate, :LastRepayRvwDate, :LastRepayCpzDate,:AllowGrcRepay, :GrcSchdMthd,");
		insertSql.append(
				" :GrcMargin, :RepayMargin, :FinCommitmentRef, :FinLimitRef, :DepreciationFrq, :FinCurrAssetValue,");
		insertSql.append(
				" :NextDepDate, :LastDepDate, :FinAccount, :FinCustPftAccount, :ClosingStatus , :FinApprovedDate, ");
		insertSql.append(" :DedupFound,:SkipDedup,:Blacklisted,");
		insertSql.append(
				" :GrcProfitDaysBasis, :StepFinance , :StepPolicy, :StepType, :AlwManualSteps, :NoOfSteps, :ManualSchedule , :TakeOverFinance , ");
		insertSql.append(" :AnualizedPercRate , :EffectiveRateOfReturn , :FinRepayPftOnFrq, ");
		insertSql.append(" :LinkedFinRef, ");
		insertSql.append(" :GrcMinRate, :GrcMaxRate , :RpyMinRate, :RpyMaxRate, ");
		insertSql.append(
				" :GrcAdvBaseRate ,:GrcAdvMargin ,:GrcAdvPftRate ,:RpyAdvBaseRate ,:RpyAdvMargin ,:RpyAdvPftRate ,:SupplementRent, :IncreasedCost,  ");
		insertSql.append("  :InvestmentRef, :MigratedFinance, :ScheduleMaintained, :ScheduleRegenerated, :CustDSR,  ");
		insertSql.append(
				" :FeeChargeAmt, :BpiAmount, :DeductFeeDisb, :LimitValid, :OverrideLimit, :FinPurpose,:DeviationApproval,:FinPreApprovedRef,:MandateID,:FinStatus, :FinStsReason, :InitiateUser, :BankName, :Iban, :AccountType, :DdaReferenceNo,");
		insertSql.append(
				" :JointAccount,:JointCustId , :DownPayAccount,  :SecurityDeposit, :RcdMaintainSts,:FinRepayMethod, :AlwBPI , :BpiTreatment , :PlanEMIHAlw , ");
		insertSql.append(
				" :PlanEMIHMethod , :PlanEMIHMaxPerYear , :PlanEMIHMax , :PlanEMIHLockPeriod , :PlanEMICpz , :CalRoundingMode ,:RoundingTarget, :AlwMultiDisb, ");
		insertSql.append(" :NextUserId, ");
		insertSql.append(
				" :Priority,:RolloverFrq, :NextRolloverDate, :ShariaStatus, :DsaCode,:feeAccountId,:MinDownPayPerc,:MMAId,:InitiateDate,:TDSApplicable,:AccountsOfficer, :ApplicationNo,");
		insertSql.append(
				" :ReferralId, :DmaCode, :SalesDepartment, :QuickDisb, :WifReference, :UnPlanEMIHLockPeriod , :UnPlanEMICpz, :ReAgeCpz, :MaxUnplannedEmi, :MaxReAgeHolidays, :AvailedUnPlanEmi, :AvailedReAgeH,:RvwRateApplFor, :SchCalOnRvw,:PastduePftCalMthd,:DroppingMethod,:RateChgAnyDay,:PastduePftMargin, :FinCategory, :ProductCategory,");
		insertSql.append(" :Version ,:LastMntBy,:LastMntOn,:RecordStatus,:RoleCode,:NextRoleCode,:TaskId,");
		insertSql.append(" :NextTaskId,:RecordType,:WorkflowId, :RejectStatus, :RejectReason, :DueBucket)");
		logger.debug("insertSql: " + insertSql.toString());

		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(financeMain);
		this.namedParameterJdbcTemplate.update(insertSql.toString(), beanParameters);
		logger.debug("Leaving");
		return financeMain.getId();
	}

	/**
	 * Fetch the Records Finance Main Detail details by key field
	 * 
	 * @param curBD
	 * @param nextBD
	 * @return
	 */
	@Override
	public List<AvailFinance> getFinanceDetailByCmtRef(String cmtRef, long custId) {
		logger.debug("Entering");
		AvailCommitment commitment = new AvailCommitment();
		commitment.setCmtReference(cmtRef);
		commitment.setCustId(custId);

		StringBuilder selectSql = new StringBuilder(
				"SELECT FinReference , FinType,FinCcy , FinAmount,TotalPftSchd, DrawnPrinciple , OutStandingBal, ");
		selectSql.append(
				" CcySpotRate , LastRepay , MaturityDate , ProfitRate , RepayFrq , Status, CcyEditField, FinDivision , FinDivisionDesc");
		selectSql.append(" from AvailFinance_View ");
		selectSql.append(" Where FinCommitmentRef =:CmtReference AND OutStandingBal > 0 ");
		if (custId != 0) {
			selectSql.append(" AND CustId =:CustId ");
		}

		logger.debug("selectSql: " + selectSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(commitment);
		RowMapper<AvailFinance> typeRowMapper = ParameterizedBeanPropertyRowMapper.newInstance(AvailFinance.class);

		logger.debug("Leaving");
		return this.namedParameterJdbcTemplate.query(selectSql.toString(), beanParameters, typeRowMapper);
	}

	/**
	 * Fetch the Records/Existing Finance Main Detail details by customer ID
	 */
	@Override
	public List<FinanceSummary> getFinExposureByCustId(long custId) {
		logger.debug("Entering");
		FinanceSummary summary = new FinanceSummary();
		summary.setCustID(custId);

		StringBuilder selectSql = new StringBuilder("SELECT FinReference , FinCommitmentRef , CmtTitle , CustID , ");
		selectSql.append(" NumberOfTerms , FinStartDate , FinType , TotalOriginal , CmtAmount , CmtAvailable , ");
		selectSql.append(" CmtExpiryDate , TotalOutStanding , MaturityDate , TotalRepayAmt , FinStatus ");
		selectSql.append(" from CustFinanceExposure_View ");
		selectSql.append(" Where CustID =:CustID ");

		logger.debug("selectSql: " + selectSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(summary);
		RowMapper<FinanceSummary> typeRowMapper = ParameterizedBeanPropertyRowMapper.newInstance(FinanceSummary.class);

		logger.debug("Leaving");
		return this.namedParameterJdbcTemplate.query(selectSql.toString(), beanParameters, typeRowMapper);
	}

	/**
	 * Method for Updation of Effective Rate of Return for Finance
	 * 
	 * @param curBD
	 * @param nextBD
	 * @return
	 */
	@Override
	public void updateFinanceERR(String finReference, Date lastRepayDate, Date lastRepayPftDate,
			BigDecimal effectiveRate, String type) {
		logger.debug("Entering");

		FinanceMain financeMain = new FinanceMain();
		financeMain.setFinReference(finReference);
		financeMain.setEffectiveRateOfReturn(effectiveRate);
		financeMain.setLastRepayDate(lastRepayDate);
		financeMain.setLastRepayPftDate(lastRepayPftDate);

		int recordCount = 0;
		StringBuilder updateSql = new StringBuilder(" Update FinanceMain ");
		updateSql.append(
				" SET EffectiveRateOfReturn =:EffectiveRateOfReturn, LastRepayDate =:LastRepayDate, LastRepayPftDate=:LastRepayPftDate ");
		updateSql.append(" Where FinReference =:FinReference ");

		logger.debug("updateSql: " + updateSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(financeMain);
		recordCount = this.namedParameterJdbcTemplate.update(updateSql.toString(), beanParameters);

		if (recordCount <= 0) {
			throw new ConcurrencyException();
		}
		logger.debug("Leaving");
	}

	@Override
	public void updateFinancePriority() {
		logger.debug("Entering");

		StringBuilder sql = new StringBuilder("update FinanceMain_Temp");
		sql.append(" set Priority = Priority + 1");
		sql.append(" where Priority != 3");
		if (App.DATABASE == Database.ORACLE) {
			sql.append(" and (sysdate - lastmnton) * 24 * 60 > :TIMEINTERVAL");
		} else {
			sql.append(" and DATEDIFF(MINUTE, lastmnton, getdate()) > :TIMEINTERVAL");
		}
		logger.debug("updateSql: " + sql.toString());

		// Get the time interval system parameter for queue priority
		int timeInterval = SysParamUtil.getValueAsInt("QUEUEPRIORITY_TIMEINTERVAL");

		MapSqlParameterSource source = new MapSqlParameterSource();
		source.addValue("TIMEINTERVAL", timeInterval);

		this.namedParameterJdbcTemplate.update(sql.toString(), source);

		logger.debug("Leaving");
	}

	/**
	 * Method for Approval process for LPO Approval Agreement
	 */
	@Override
	public void updateApprovalStatus(String finReference, String approvalStatus) {
		logger.debug("Entering");

		MapSqlParameterSource source = new MapSqlParameterSource();
		source.addValue("FinReference", finReference);
		source.addValue("ApprovalStatus", approvalStatus);

		StringBuilder sql = new StringBuilder("UPDATE FinanceMain_Temp");
		sql.append(" set Approved = :ApprovalStatus");
		sql.append(" where FinReference = :FinReference");
		logger.debug("updateSql: " + sql.toString());

		this.namedParameterJdbcTemplate.update(sql.toString(), source);

		logger.debug("Leaving");
	}

	@Override
	public String getNextRoleCodeByRef(String finReference, String type) {
		logger.debug("Entering");

		String nextRoleCode = null;
		MapSqlParameterSource source = new MapSqlParameterSource();
		source.addValue("FinReference", finReference);

		StringBuilder sql = new StringBuilder("SELECT NextRoleCode From FinanceMain");
		sql.append(StringUtils.trimToEmpty(type));
		sql.append(" Where FinReference = :FinReference");
		logger.debug("selectSql: " + sql.toString());

		try {
			nextRoleCode = this.namedParameterJdbcTemplate.queryForObject(sql.toString(), source, String.class);
		} catch (EmptyResultDataAccessException e) {
			logger.warn("Exception: ", e);
			nextRoleCode = null;
		}

		logger.debug("Leaving");
		return nextRoleCode;
	}

	@Override
	public void updateNextUserId(List<String> finRefList, String oldUserId, String newUserId,
			boolean isManualAssignment) {

		logger.debug("Entering");

		Map<String, String> finRefMap = new HashMap<String, String>();
		for (String reference : finRefList) {
			finRefMap.put("FinReference", reference);
			finRefMap.put("USER_ID", oldUserId);
			finRefMap.put("NEW_USER_ID", newUserId);

			StringBuilder updateSql = new StringBuilder("Update FinanceMain_Temp");
			if (isManualAssignment) {
				updateSql.append(
						" SET NextUserId= (CASE WHEN NextRoleCode NOT like '%,%' AND COALESCE(NextUserId, ' ') LIKE (' ') THEN :NEW_USER_ID");
				updateSql.append(
						" WHEN NextRoleCode like '%,%' AND COALESCE(NextUserId, ' ') LIKE (' ') THEN :NEW_USER_ID ");
				if (App.DATABASE == Database.ORACLE || App.DATABASE == Database.POSTGRES) {
					updateSql.append(
							" WHEN NextRoleCode like '%,%' AND NOT COALESCE(NextUserId, ' ') LIKE (' ') THEN NextUserId||','||:NEW_USER_ID END) ");
				} else {
					updateSql.append(
							" WHEN NextRoleCode like '%,%' AND NOT COALESCE(NextUserId, ' ') LIKE (' ') THEN NextUserId+',"
									+ StringUtils.trimToEmpty(newUserId) + "' END) ");
				}
			} else {
				updateSql.append(" SET NextUserId = REPLACE(NextUserId, :USER_ID, :NEW_USER_ID)");
			}
			updateSql.append(" Where FinReference =:FinReference");
			logger.debug("updateSql: " + updateSql.toString());

			this.namedParameterJdbcTemplate.update(updateSql.toString(), finRefMap);
		}

		logger.debug("Leaving");
	}

	@Override
	public void updateDeviationApproval(FinanceMain financeMain, boolean rejected, String type) {
		logger.debug("Entering");

		StringBuilder updateSql = new StringBuilder("Update FinanceMain");
		updateSql.append(StringUtils.trimToEmpty(type));
		updateSql.append(" Set DeviationApproval =:DeviationApproval");
		if (rejected) {
			updateSql.append(" ,NextTaskId=:NextTaskId, NextRoleCode = :NextRoleCode, NextUserId = :NextUserId");
		}
		updateSql.append(" Where FinReference =:FinReference");

		logger.debug("updateSql: " + updateSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(financeMain);
		this.namedParameterJdbcTemplate.update(updateSql.toString(), beanParameters);

		logger.debug("Leaving");
	}


	/**
	 * Method for fetch finance details based on priority
	 */
	@Override
	public List<FinanceMain> getFinanceRefByPriority() {
		logger.debug("Entering");

		StringBuilder selectSql = new StringBuilder();
		selectSql.append(" SELECT FinReference,LastMntBy,NextUserId ,NextRoleCode, Priority FROM FinanceMain_Temp ");
		selectSql.append(" WHERE FinReference NOT IN (Select Reference from MailLog ) ");
		selectSql.append(" AND Priority != 0 ");

		logger.debug("selectSql: " + selectSql.toString());
		RowMapper<FinanceMain> typeRowMapper = ParameterizedBeanPropertyRowMapper.newInstance(FinanceMain.class);

		logger.debug("Leaving");

		return this.namedParameterJdbcTemplate.query(selectSql.toString(), typeRowMapper);
	}

	/**
	 * Method for fetch unprocessed finance details
	 */
	@Override
	public List<FinanceMain> getFinanceRefByValueDate(Date appDate, int maxAllowedDays) {
		logger.debug("Entering");

		MapSqlParameterSource source = new MapSqlParameterSource();
		source.addValue("AppDate", appDate);
		source.addValue("MaxAllowedDays", maxAllowedDays);

		StringBuilder selectSql = new StringBuilder();
		selectSql.append(" SELECT FinReference,LastMntBy,NextUserId ,NextRoleCode, Priority FROM FinanceMain_Temp ");
		selectSql.append(" where FinReference in(Select Reference from ( ");

		if (App.DATABASE == Database.SQL_SERVER) {
			selectSql.append(" Select Reference, DATEDIFF(dd, MAX(ValueDate), :AppDate)");
			selectSql.append("  maxValueDate from MailLog group by Reference) T where maxValueDate =:MaxAllowedDays)");
		} else if (App.DATABASE == Database.ORACLE) {
			selectSql.append(" Select Reference, TRUNC(:AppDate) - TO_DATE(MAX(ValueDate), 'dd-MM-yy') ");
			selectSql.append("  maxValueDate from MailLog group by Reference) T where maxValueDate =:MaxAllowedDays)");
		}

		logger.debug("selectSql: " + selectSql.toString());
		RowMapper<FinanceMain> typeRowMapper = ParameterizedBeanPropertyRowMapper.newInstance(FinanceMain.class);

		logger.debug("Leaving");

		return this.namedParameterJdbcTemplate.query(selectSql.toString(), source, typeRowMapper);
	}

	/**
	 * Method for fetch finance details based on GrcEnd Date
	 */
	@Override
	public List<FinanceMain> getFinGraceDetails(Date grcEnd, int allowedDays) {
		logger.debug("Entering");

		MapSqlParameterSource source = new MapSqlParameterSource();
		source.addValue("GraceEndDate", grcEnd);
		source.addValue("AllowedDays", allowedDays);

		StringBuilder selectSql = new StringBuilder();
		selectSql.append(
				"SELECT GrcPeriodEndDate, CustID,FinReference,LastMntBy,NextUserId ,NextRoleCode, Priority FROM FinanceMain WHERE ");

		if (App.DATABASE == Database.SQL_SERVER) {
			selectSql.append(" (DATEDIFF(dd, GrcPeriodEndDate, :GraceEndDate)) =:AllowedDays ");
		} else if (App.DATABASE == Database.ORACLE) {
			selectSql.append(
					" SELECT Reference, (TRUNC(:GraceEndDate) - TO_DATE(MAX(GrcPeriodEndDate), 'dd-MM-yy') =:AllowedDays ");
		}
		selectSql.append(" AND FinReference NOT IN(SELECT Reference FROM MailLog ) ");

		logger.debug("selectSql: " + selectSql.toString());
		RowMapper<FinanceMain> typeRowMapper = ParameterizedBeanPropertyRowMapper.newInstance(FinanceMain.class);

		logger.debug("Leaving");

		return this.namedParameterJdbcTemplate.query(selectSql.toString(), source, typeRowMapper);
	}

	/**
	 * Method for Fetching List of Limit Reference Details utilized for Finances in Rollover Functionality
	 */
	@Override
	public List<String> getRollOverLimitRefList() {
		logger.debug("Entering");

		MapSqlParameterSource source = new MapSqlParameterSource();

		StringBuilder selectSql = new StringBuilder();
		selectSql.append(" SELECT DISTINCT FinLimitRef FROM FinanceMain WHERE NextRolloverDate IS NOT NULL ");
		selectSql.append(" AND FinReference NOT IN (SELECT FinReference From RolledOverFinanceDetail_View)  ");

		logger.debug("selectSql: " + selectSql.toString());
		logger.debug("Leaving");

		return this.namedParameterJdbcTemplate.queryForList(selectSql.toString(), source, String.class);
	}

	/**
	 * Method for Fetching List of Limit Reference Details utilized for Finances in Rollover Functionality
	 */
	@Override
	public List<String> getRollOverFinTypeList(String limitRef) {
		logger.debug("Entering");

		MapSqlParameterSource source = new MapSqlParameterSource();
		source.addValue("FinLimitRef", limitRef);

		StringBuilder selectSql = new StringBuilder();
		selectSql.append(" SELECT DISTINCT FinType FROM FinanceMain WHERE NextRolloverDate IS NOT NULL ");
		selectSql.append(
				" AND FinReference NOT IN (SELECT FinReference From RolledOverFinanceDetail_View) AND FinLimitRef=:FinLimitRef  ");

		logger.debug("selectSql: " + selectSql.toString());
		logger.debug("Leaving");

		return this.namedParameterJdbcTemplate.queryForList(selectSql.toString(), source, String.class);
	}

	/**
	 * Method for Fetching List of Rollover Date Details utilized for Finances in Rollover Functionality
	 */
	@Override
	public List<Date> getRollOverDateList(String limitRef, String finType) {
		logger.debug("Entering");

		MapSqlParameterSource source = new MapSqlParameterSource();
		source.addValue("FinLimitRef", limitRef);
		source.addValue("FinType", finType);

		StringBuilder selectSql = new StringBuilder();
		selectSql.append(" SELECT DISTINCT MaturityDate FROM FinanceMain WHERE NextRolloverDate IS NOT NULL ");
		selectSql.append(" AND FinReference NOT IN (SELECT FinReference From RolledOverFinanceDetail_View) ");
		selectSql.append(" AND FinLimitRef=:FinLimitRef AND FinType=:FinType ");

		logger.debug("selectSql: " + selectSql.toString());
		logger.debug("Leaving");

		return this.namedParameterJdbcTemplate.queryForList(selectSql.toString(), source, Date.class);
	}

	/**
	 * Method for Fetching List of Finances on Same Rollover Date
	 */
	@Override
	public List<RolledoverFinanceDetail> getFinanceList(String limitRef, String finType, Date rolloverDate) {
		logger.debug("Entering");

		MapSqlParameterSource source = new MapSqlParameterSource();
		source.addValue("FinLimitRef", limitRef);
		source.addValue("FinType", finType);
		source.addValue("MaturityDate", rolloverDate);

		StringBuilder selectSql = new StringBuilder();
		selectSql.append(
				" SELECT F.FinReference, F.FinstartDate StartDate, F.FinAmount, F.EffectiveRateOfReturn ProfitRate, F.TotalProfit, ");
		selectSql.append(
				" S.RolloverAmount, F.NextRolloverDate RolloverDate, C.CcyEditField Formatter, P.TotalPriBal, P.TotalPftBal, F.FinPurpose  ");
		selectSql.append(
				" FROM FinanceMain F INNER JOIN FinScheduleDetails S ON F.FinReference = S.FinReference AND F.MaturityDate = S.SchDate ");
		selectSql.append(
				" INNER JOIN RMTCurrencies C ON C.CcyCode = F.FinCcy INNER JOIN FinPftDetails P ON P.FinReference = F.FinReference ");
		selectSql.append(
				" WHERE F.NextRolloverDate IS NOT NULL AND S.RolloverAmount > 0 AND F.FinReference NOT IN (SELECT FinReference From RolledOverFinanceDetail_View) ");
		selectSql.append(" AND F.FinLimitRef=:FinLimitRef AND F.FinType=:FinType AND F.MaturityDate=:MaturityDate ");

		logger.debug("selectSql: " + selectSql.toString());
		RowMapper<RolledoverFinanceDetail> typeRowMapper = ParameterizedBeanPropertyRowMapper
				.newInstance(RolledoverFinanceDetail.class);
		logger.debug("Leaving");

		return this.namedParameterJdbcTemplate.query(selectSql.toString(), source, typeRowMapper);
	}

	@Override
	public FinanceMain getFinanceMainByRef(String reference, String type, boolean isRejectFinance) {
		logger.debug("Entering");

		FinanceMain financeMain = new FinanceMain();
		financeMain.setId(reference);
		StringBuilder selectSql = new StringBuilder("select  *  FROM ");
		/*
		 * StringBuilder selectSql = new StringBuilder("select  FinReference, GrcPeriodEndDate, FinRepaymentAmount," );
		 * selectSql.
		 * append(" DisbAccountid, RepayAccountid, FinAccount, FinCustPftAccount, FinCommitmentRef, FinLimitRef," );
		 * selectSql.
		 * append(" FinCcy, FinBranch, CustId, FinAmount, FeeChargeAmt, DownPayment, DownPayBank, DownPaySupl, DownPayAccount, SecurityDeposit, FinType, "
		 * ); selectSql.
		 * append(" FinStartDate,GraceTerms, NumberOfTerms, NextGrcPftDate, NextRepayDate, LastRepayPftDate, NextRepayPftDate, "
		 * ); selectSql.append(" LastRepayRvwDate, NextRepayRvwDate, FinAssetValue, FinCurrAssetValue,FinRepayMethod, "
		 * ); selectSql.append(" RecordType, Version, ProfitDaysBasis , FeeChargeAmt, FinStatus, FinStsReason," );
		 * selectSql.append(" InitiateUser, BankName, Iban, AccountType, DdaReferenceNo, SecurityDeposit, MaturityDate "
		 * );
		 */
		if (isRejectFinance) {
			selectSql.append(" RejectFinancemain");
		} else {
			selectSql.append(" Financemain");
		}
		selectSql.append(StringUtils.trimToEmpty(type));
		selectSql.append(" Where FinReference =:FinReference");

		logger.debug("selectSql: " + selectSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(financeMain);
		RowMapper<FinanceMain> typeRowMapper = ParameterizedBeanPropertyRowMapper.newInstance(FinanceMain.class);

		try {
			financeMain = this.namedParameterJdbcTemplate.queryForObject(selectSql.toString(), beanParameters,
					typeRowMapper);
		} catch (EmptyResultDataAccessException e) {
			logger.warn("Exception: ", e);
			financeMain = null;
		}
		logger.debug("Leaving");
		return financeMain;
	}

	/**
	 * Method for Fetch DDA Payment Initiation details
	 * 
	 * @param repayMethod
	 * @param appDate
	 * @return List<FinanceMain>
	 */
	@Override
	public List<DDAPayments> getDDAPaymentsList(String repayMethod, Date appDate) {
		logger.debug("Entering");

		MapSqlParameterSource source = new MapSqlParameterSource();
		source.addValue("FinRepayMethod", repayMethod);
		source.addValue("SchDate", appDate);

		StringBuilder selectSql = new StringBuilder();
		selectSql.append(
				" SELECT T1.FinReference, T1.FinRepaymentAmount, T1.DDAReferenceNo, T1.RepayAccountId, T2.CustCIF, T3.SchDate");
		selectSql.append(" FROM FinanceMain T1 INNER JOIN Customers T2 ON T1.CustID = T2.CustID");
		selectSql.append(" INNER JOIN FinScheduleDetails T3 ON T1.FinReference = T3.FinReference");
		selectSql.append(" WHERE T1.FinRepayMethod =:FinRepayMethod AND T3.SchDate =:SchDate");

		logger.debug("selectSql: " + selectSql.toString());
		RowMapper<DDAPayments> typeRowMapper = ParameterizedBeanPropertyRowMapper.newInstance(DDAPayments.class);
		logger.debug("Leaving");
		try {
			return this.namedParameterJdbcTemplate.query(selectSql.toString(), source, typeRowMapper);
		} catch (EmptyResultDataAccessException dae) {
			logger.debug("Exception: ", dae);
			return null;
		}
	}

	/**
	 * Fetch the Record Finance Main Detail details by key field
	 * 
	 * @param finReference
	 *            (String)
	 * 
	 * @return FinanceMain
	 */
	@Override
	public FinanceMain getFinanceMainForManagerCheque(final String finReference, String type) {
		logger.debug("Entering");

		FinanceMain financeMain = new FinanceMain();
		financeMain.setId(finReference);

		StringBuilder selectSql = new StringBuilder("select  FinReference, GrcPeriodEndDate, FinRepaymentAmount,");
		selectSql.append(" GraceBaseRate, GraceSpecialRate, GrcPftRate, GrcPftFrq,NextGrcPftDate, AllowGrcPftRvw,");
		selectSql.append(" GrcPftRvwFrq, NextGrcPftRvwDate, AllowGrcCpz, GrcCpzFrq, NextGrcCpzDate,RepayBaseRate,");
		selectSql.append(" RepaySpecialRate, RepayProfitRate, RepayFrq, NextRepayDate, RepayPftFrq, NextRepayPftDate,");
		selectSql.append(" AllowRepayRvw,RepayRvwFrq, NextRepayRvwDate, AllowRepayCpz, RepayCpzFrq, NextRepayCpzDate,");
		selectSql.append(
				" MaturityDate, CpzAtGraceEnd,DownPayment, GraceFlatAmount, ReqRepayAmount, TotalProfit, DownPayBank, DownPaySupl, ");
		selectSql.append(" TotalCpz,TotalGrossPft,TotalGracePft,TotalGraceCpz,TotalGrossGrcPft, TotalRepayAmt,");
		selectSql.append(" GrcRateBasis, RepayRateBasis, FinType,FinRemarks, FinCcy, ScheduleMethod,");
		selectSql.append(" ProfitDaysBasis, ReqMaturity, CalTerms, CalMaturity, FirstRepay, LastRepay,");
		selectSql.append(" FinStartDate, FinAmount, FinRepaymentAmount, CustID, Defferments, ");
		selectSql.append(" FinBranch, FinSourceID, AllowedDefRpyChange, AvailedDefRpyChange, AllowedDefFrqChange,");
		selectSql
				.append(" AvailedDefFrqChange, RecalType, FinAssetValue, disbAccountId, repayAccountId, FinIsActive, ");
		selectSql.append(
				" LastRepayDate, LastRepayPftDate,LastRepayRvwDate, LastRepayCpzDate, AllowGrcRepay, GrcSchdMthd,");
		selectSql.append(
				" GrcMargin, RepayMargin, FinCommitmentRef, FinLimitRef, DepreciationFrq, FinCurrAssetValue,FinContractDate,");
		selectSql.append(" NextDepDate, LastDepDate, FinAccount, FinCustPftAccount,");
		selectSql.append(" NextDepDate, LastDepDate, FinAccount, FinCustPftAccount,");
		selectSql.append(" ClosingStatus, FinApprovedDate, ");
		selectSql.append(
				" AnualizedPercRate , EffectiveRateOfReturn , FinRepayPftOnFrq, FeeChargeAmt, BpiAmount, DeductFeeDisb, PromotionCode ");
		if (StringUtils.trimToEmpty(type).contains("View")) {
			selectSql.append(", lovDescFinTypeName, lovDescCustCIF, ");
			selectSql.append(
					" lovDescCustShrtName, LovDescCustFName, lovDescCustLName, lovDescFinBranchName, lovDescFinancingAmount ");
		}
		selectSql.append(" FROM Financemain");
		selectSql.append(StringUtils.trimToEmpty(type));
		selectSql.append(" Where FinReference = :FinReference");

		logger.debug("selectSql: " + selectSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(financeMain);
		RowMapper<FinanceMain> typeRowMapper = ParameterizedBeanPropertyRowMapper.newInstance(FinanceMain.class);

		try {
			financeMain = this.namedParameterJdbcTemplate.queryForObject(selectSql.toString(), beanParameters,
					typeRowMapper);
		} catch (EmptyResultDataAccessException e) {
			logger.warn("Exception: ", e);
			financeMain = null;
		}
		logger.debug("Leaving");
		return financeMain;
	}

	@Override
	public void updateRepaymentAmount(String finReference, BigDecimal repaymentAmount) {
		logger.debug("Entering");

		int recordCount = 0;
		FinanceMain financeMain = new FinanceMain();
		financeMain.setFinReference(finReference);
		financeMain.setFinRepaymentAmount(repaymentAmount);

		StringBuilder updateSql = new StringBuilder("Update FinanceMain");
		updateSql.append(" Set FinRepaymentAmount =:FinRepaymentAmount ");
		updateSql.append(" Where FinReference =:FinReference");

		logger.debug("updateSql: " + updateSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(financeMain);
		recordCount = this.namedParameterJdbcTemplate.update(updateSql.toString(), beanParameters);

		if (recordCount <= 0) {
			throw new ConcurrencyException();
		}
	}

	@Override
	public void updateStatus(String finReference, String status, String statusReason) {

		logger.debug("Entering");

		int recordCount = 0;
		FinanceMain financeMain = new FinanceMain();
		financeMain.setFinStatus(status);
		financeMain.setFinStsReason(statusReason);
		financeMain.setFinReference(finReference);
		StringBuilder updateSql = new StringBuilder("Update FinanceMain");
		updateSql.append(" Set FinStatus = :FinStatus, FinStsReason = :FinStsReason ");
		updateSql.append(" Where FinReference =:FinReference");

		logger.debug("updateSql: " + updateSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(financeMain);
		recordCount = this.namedParameterJdbcTemplate.update(updateSql.toString(), beanParameters);

		if (recordCount <= 0) {
			throw new ConcurrencyException();
		}
	}
	
	@Override
	public void updatePaymentInEOD(FinanceMain financeMain) {

		logger.debug("Entering");
		int recordCount = 0;
		StringBuilder updateSql = new StringBuilder("Update FinanceMain Set");
		updateSql.append(" FinStatus = :FinStatus, FinStsReason = :FinStsReason, ");
		updateSql.append("  FinIsActive = :FinIsActive, ClosingStatus = :ClosingStatus, ");
		updateSql.append("  FinRepaymentAmount = :FinRepaymentAmount ");
		updateSql.append(" Where FinReference =:FinReference");

		logger.debug("updateSql: " + updateSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(financeMain);
		recordCount = this.namedParameterJdbcTemplate.update(updateSql.toString(), beanParameters);

		if (recordCount <= 0) {
			throw new ConcurrencyException();
		}
	}

	/**
	 * Method for Fetching Approved Repayment method
	 */
	@Override
	public String getApprovedRepayMethod(String finReference, String type) {
		logger.debug("Entering");

		MapSqlParameterSource source = new MapSqlParameterSource();
		source.addValue("FinReference", finReference);

		StringBuilder selectSql = new StringBuilder();
		selectSql.append(" SELECT FinRepayMethod FROM FinanceMain");
		selectSql.append(StringUtils.trimToEmpty(type));
		selectSql.append(" WHERE FinReference=:FinReference ");

		logger.debug("selectSql: " + selectSql.toString());
		String repayMethod = this.namedParameterJdbcTemplate.queryForObject(selectSql.toString(), source, String.class);
		if (StringUtils.isBlank(repayMethod)) {
			repayMethod = null;
		}
		logger.debug("Leaving");
		return repayMethod;
	}

	/**
	 * @param AccountNo
	 * @return
	 */
	@Override
	public String getCurrencyByAccountNo(String accountNo) {
		logger.debug("Entering");
		FinanceMain financeMain = new FinanceMain();
		financeMain.setRepayAccountId(accountNo);

		StringBuilder selectSql = new StringBuilder();
		selectSql.append(" select FinCcy from FinanceMain fm inner join SecondaryAccounts sa on ");
		selectSql.append(" fm.FinReference=sa.FinReference where fm.RepayAccountId= :RepayAccountId ");
		selectSql.append(" or sa.AccountNumber = :RepayAccountId group by FinCcy ");

		logger.debug("selectSql: " + selectSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(financeMain);

		String finCcy = null;
		try {
			finCcy = this.namedParameterJdbcTemplate.queryForObject(selectSql.toString(), beanParameters, String.class);
		} catch (EmptyResultDataAccessException e) {
			logger.warn("Exception: ", e);
			finCcy = null;
		}
		financeMain = null;
		logger.debug("Leaving");
		return finCcy;
	}

	@Override
	public void updateMaturity(String finReference, String closingStatus, boolean finIsActive) {
		logger.debug("Entering");
		FinanceMain financeMain = new FinanceMain();
		financeMain.setFinReference(finReference);
		financeMain.setClosingStatus(closingStatus);
		financeMain.setFinIsActive(finIsActive);

		StringBuilder updateSql = new StringBuilder("Update FinanceMain");
		updateSql.append(" Set FinIsActive = :FinIsActive, ClosingStatus = :ClosingStatus ");
		updateSql.append(" Where FinReference = :FinReference ");
		logger.debug("updateSql: " + updateSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(financeMain);

		this.namedParameterJdbcTemplate.update(updateSql.toString(), beanParameters);
		logger.debug("Leaving");
	}

	@Override
	public List<String> getScheduleEffectModuleList(boolean schdChangeReq) {
		MapSqlParameterSource source = new MapSqlParameterSource();
		source.addValue("schdChangeReq", schdChangeReq);

		StringBuilder selectSql = new StringBuilder();
		selectSql.append(" SELECT ModuleName FROM ScheduleEffectModule WHERE SchdCanModify =:schdChangeReq ");

		logger.debug("selectSql: " + selectSql.toString());
		logger.debug("Leaving");

		return this.namedParameterJdbcTemplate.queryForList(selectSql.toString(), source, String.class);
	}

	/**
	 * updates finance sequence table
	 */
	@Override
	public boolean updateSeqNumber(long oldNumber, long newNumber) {
		logger.debug("Entering");
		boolean valUpdated = false;
		try {
			MapSqlParameterSource maSqlParameterSource = new MapSqlParameterSource();
			maSqlParameterSource.addValue("oldNumber", oldNumber);
			maSqlParameterSource.addValue("newNumber", newNumber);

			String updateSql = "UPDATE  SeqFinReference  SET Seqno = :newNumber Where Seqno = :oldNumber";
			int result = this.namedParameterJdbcTemplate.update(updateSql, maSqlParameterSource);
			if (result == 1) {
				valUpdated = true;
			}
		} catch (Exception e) {
			logger.error("Exception: ", e);
		}
		logger.debug("Leaving");
		return valUpdated;
	}

	/**
	 * Fetch the Record Finance Main Detail details by key field
	 * 
	 * @param id
	 *            (String)
	 * @param type
	 *            (String) ""/_Temp/_View
	 * @return FinanceMain
	 */
	@Override
	public List<FinanceMain> getFinanceMainbyCustId(final long id) {
		logger.debug("Entering");

		FinanceMain financeMain = new FinanceMain();
		financeMain.setCustID(id);

		StringBuilder selectSql = new StringBuilder("SELECT FinReference, CustId, FinAmount, FinType,FinCcy ");
		selectSql.append(" From FinanceMain");
		selectSql.append(" Where CustID =:CustID");

		logger.debug("selectSql: " + selectSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(financeMain);
		RowMapper<FinanceMain> typeRowMapper = ParameterizedBeanPropertyRowMapper.newInstance(FinanceMain.class);

		try {
			return this.namedParameterJdbcTemplate.query(selectSql.toString(), beanParameters, typeRowMapper);
		} catch (EmptyResultDataAccessException dae) {
			logger.debug("Exception: ", dae);
			return null;
		}

	}

	/**
	 * Method for fetch total number of records i.e count
	 * 
	 * @param finReference
	 * @param type
	 * @param isWIF
	 */
	@Override
	public int getFinanceCountById(String finReference, String type, boolean isWIF) {
		logger.debug("Entering");

		FinanceMain financeMain = new FinanceMain();
		financeMain.setFinReference(finReference);

		StringBuilder selectSql = new StringBuilder("SELECT COUNT(*) ");

		if (!isWIF) {
			selectSql.append(" From FinanceMain");
		} else {
			selectSql.append(" From WIFFinanceMain");
		}

		selectSql.append(StringUtils.trimToEmpty(type));
		selectSql.append(" Where FinReference =:FinReference AND FinIsActive = 1");

		logger.debug("selectSql: " + selectSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(financeMain);

		try {
			return this.namedParameterJdbcTemplate.queryForObject(selectSql.toString(), beanParameters, Integer.class);
		} catch (EmptyResultDataAccessException dae) {
			logger.debug("Exception: ", dae);
			return 0;
		}
	}
	
	@Override
	public int getFinCountByCustId(long custID) {
		logger.debug("Entering");
		
		FinanceMain financeMain = new FinanceMain();
		financeMain.setCustID(custID);
		
		StringBuilder selectSql = new StringBuilder("SELECT COUNT(*) ");
		
		selectSql.append(" From FinanceMain");
		
		selectSql.append(" Where CustID =:CustID AND FinIsActive = 1");
		
		logger.debug("selectSql: " + selectSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(financeMain);
		
		try {
			return this.namedParameterJdbcTemplate.queryForObject(selectSql.toString(), beanParameters, Integer.class);
		} catch (EmptyResultDataAccessException dae) {
			logger.debug("Exception: ", dae);
			return 0;
		}
	}

	@Override
	public int getFinanceCountByMandateId(long mandateID) {
		logger.debug("Entering");
		FinanceMain financeMain = new FinanceMain();
		financeMain.setMandateID(mandateID);
		financeMain.setFinIsActive(true);

		StringBuilder selectSql = new StringBuilder("SELECT COUNT(*)");
		selectSql.append(" From FinanceMain");
		selectSql.append(" Where MandateID =:MandateID AND FinIsActive=:FinIsActive");

		logger.debug("selectSql: " + selectSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(financeMain);

		try {
			return this.namedParameterJdbcTemplate.queryForObject(selectSql.toString(), beanParameters, Integer.class);
		} catch (EmptyResultDataAccessException dae) {
			logger.debug("Exception: ", dae);
			return 0;
		}
	}

	/**
	 * Method for getCount for mandate and FinRef in finance main
	 * 
	 * @param finReference
	 * @param mandateID
	 * 
	 */
	@Override
	public int getFinanceCountById(String finReference, long mandateID) {
		logger.debug("Entering");

		FinanceMain financeMain = new FinanceMain();
		financeMain.setFinReference(finReference);
		financeMain.setMandateID(mandateID);
		financeMain.setFinIsActive(true);

		StringBuilder selectSql = new StringBuilder("SELECT COUNT(*)");
		selectSql.append(" From FinanceMain");
		selectSql.append(" Where MandateID =:MandateID AND FinReference=:FinReference  AND FinIsActive=:FinIsActive");

		logger.debug("selectSql: " + selectSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(financeMain);

		try {
			return this.namedParameterJdbcTemplate.queryForObject(selectSql.toString(), beanParameters, Integer.class);
		} catch (EmptyResultDataAccessException dae) {
			logger.debug(dae);
			return 0;
		}
	}

	/**
	 * Method for Update old MandateId With New MandateId
	 * 
	 * @param finReference
	 * @param newMandateID
	 * 
	 */
	@Override
	public int loanMandateSwapping(String finReference, long newMandateID) {
		logger.debug("Entering");

		int recordCount = 0;
		FinanceMain financeMain = new FinanceMain();
		financeMain.setFinReference(finReference);
		financeMain.setMandateID(newMandateID);

		StringBuilder updateSql = new StringBuilder("Update FinanceMain");
		updateSql.append(" Set MandateID =:MandateID ");
		updateSql.append(" Where FinReference =:FinReference");

		logger.debug("updateSql: " + updateSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(financeMain);
		recordCount = this.namedParameterJdbcTemplate.update(updateSql.toString(), beanParameters);
		logger.debug("Leaving");

		return recordCount;

	}

	/**
	 * Method for fetch Finance details for Finance Maintenance module
	 * 
	 * @param finReference
	 * @param type
	 * @param isWIF
	 * @return FinanceMain
	 */
	@Override
	public FinanceMain getFinanceDetailsForService(String finReference, String type, boolean isWIF) {
		logger.debug("Entering");

		FinanceMain financeMain = new FinanceMain();
		financeMain.setId(finReference);

		StringBuilder selectSql = new StringBuilder(
				"SELECT FinReference, GrcPeriodEndDate, AllowGrcPeriod, MaturityDate, ");
		selectSql.append(" AllowGrcPeriod, RepayFrq, FinStartDate, CustID ");
		if (isWIF) {
			selectSql.append(" From WIFFinanceMain");
		} else {
			selectSql.append(" From FinanceMain");
		}
		selectSql.append(StringUtils.trimToEmpty(type));
		selectSql.append(" Where FinReference =:FinReference");

		logger.debug("selectSql: " + selectSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(financeMain);
		RowMapper<FinanceMain> typeRowMapper = ParameterizedBeanPropertyRowMapper.newInstance(FinanceMain.class);

		try {
			financeMain = this.namedParameterJdbcTemplate.queryForObject(selectSql.toString(), beanParameters,
					typeRowMapper);
		} catch (EmptyResultDataAccessException e) {
			logger.warn("Exception: ", e);
			financeMain = null;
		}
		logger.debug("Leaving");
		return financeMain;
	}

	/**
	 * Method for Update Basic Finance details
	 * 
	 * @param financeMain
	 * @param type
	 * @return Integer
	 */
	@Override
	public int updateFinanceBasicDetails(FinanceMain financeMain, String type) {
		logger.debug("Entering");

		int recordCount = 0;

		StringBuilder updateSql = new StringBuilder("Update  ");
		updateSql.append("FinanceMain");
		updateSql.append(StringUtils.trimToEmpty(type));
		updateSql.append(" Set  DsaCode = :DsaCode , AccountsOfficer = :AccountsOfficer, ReferralId = :ReferralId, ");
		updateSql.append(" SalesDepartment = :SalesDepartment , DmaCode = :DmaCode");
		updateSql.append(" Where FinReference =:FinReference");
		logger.debug("updateSql: " + updateSql.toString());

		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(financeMain);
		try {
			recordCount = this.namedParameterJdbcTemplate.update(updateSql.toString(), beanParameters);
		} catch (EmptyResultDataAccessException e) {
			logger.warn("Exception: ", e);
			recordCount = 0;
		}
		if (recordCount <= 0) {
			logger.debug("Error Update Method Count :" + recordCount);
		}
		logger.debug("Leaving");
		return recordCount;
	}

	@Override
	public List<String> getUsersLoginList(List<String> nextRoleCodes) {
		logger.debug("Entering");

		List<Long> userIds = new ArrayList<>();
		if(nextRoleCodes != null) {
			for(String id: nextRoleCodes) {
				userIds.add(Long.valueOf(id));
			}
		}
		
		MapSqlParameterSource mapSqlParameterSource = new MapSqlParameterSource();
		mapSqlParameterSource.addValue("usrid", userIds);

		StringBuilder selectSql = new StringBuilder("Select  usrlogin from secusers");
		selectSql.append(" Where usrid IN (:usrid) ");

		logger.debug("selectSql: " + selectSql.toString());

		return this.namedParameterJdbcTemplate.queryForList(selectSql.toString(), mapSqlParameterSource, String.class);
	}

	/**
	 * Fetch the Record Finance Main Detail details by key field
	 * 
	 * @param id
	 *            (String)
	 * @param type
	 *            (String) ""/_Temp/_View
	 * @return FinanceMain
	 */
	@Override
	public FinanceMain getFinanceMainParms(final String finReference) {
		logger.debug("Entering");

		MapSqlParameterSource source = new MapSqlParameterSource();

		StringBuilder selectSql = new StringBuilder("SELECT  Defferments, PlanDeferCount, ");
		selectSql.append("  AllowedDefRpyChange, AvailedDefRpyChange, AllowedDefFrqChange,");
		selectSql.append(" AvailedDefFrqChange, FinIsActive, ");
		selectSql.append(" AlwBPI , BpiTreatment , PlanEMIHAlw ,");
		selectSql.append(
				" PlanEMIHMethod , PlanEMIHMaxPerYear , PlanEMIHMax , PlanEMIHLockPeriod , PlanEMICpz , AlwMultiDisb, ");
		selectSql.append(
				"  UnPlanEMIHLockPeriod , UnPlanEMICpz, ReAgeCpz, MaxUnplannedEmi, MaxReAgeHolidays, AvailedUnPlanEmi, AvailedReAgeH, PromotionCode ");
		selectSql.append(" From FinanceMain_View");
		selectSql.append(" Where FinReference =:FinReference");

		logger.debug("selectSql: " + selectSql.toString());

		source.addValue("FinReference", finReference);

		RowMapper<FinanceMain> typeRowMapper = ParameterizedBeanPropertyRowMapper.newInstance(FinanceMain.class);
		try {
			return this.namedParameterJdbcTemplate.queryForObject(selectSql.toString(), source, typeRowMapper);
		} catch (EmptyResultDataAccessException e) {
			logger.warn("Exception: ", e);
		}
		logger.debug("Leaving");
		return null;
	}

	/**
	 * Method to get Finance related data.
	 * 
	 * @param custId
	 */
	@Override
	public List<FinanceMain> getFinanceByCustId(long custId) {
		logger.debug("Entering");

		FinanceMain financeMain = new FinanceMain();
		financeMain.setCustID(custId);

		StringBuilder selectSql = new StringBuilder("SELECT FM.FinReference,FM.FinAmount, FM.FinType, FM.FinCcy,");
		selectSql.append(" FM.FinAssetValue, FM.NumberOfTerms, FM.MaturityDate, FM.Finstatus,");
		selectSql.append(" FM.FinStartDate, FM.FirstRepay, FT.FinCategory lovDescFinProduct,FM.ClosingStatus");
		selectSql.append(" From FinanceMain FM INNER JOIN RMTFinanceTypes FT ON FM.FinType = FT.FinType ");
		selectSql.append(" Where CustID =:CustID");

		logger.debug("selectSql: " + selectSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(financeMain);
		RowMapper<FinanceMain> typeRowMapper = ParameterizedBeanPropertyRowMapper.newInstance(FinanceMain.class);
		List<FinanceMain> financeMainList = new ArrayList<FinanceMain>();
		try {
			financeMainList = this.namedParameterJdbcTemplate.query(selectSql.toString(), beanParameters,
					typeRowMapper);
		} catch (EmptyResultDataAccessException dae) {
			logger.error("Exception: ", dae);
			return Collections.emptyList();
		}
		logger.debug("Leaving");
		return financeMainList;
	}

	/**
	 * Method to get Finance related data.
	 * 
	 * @param collateralRef
	 */
	@Override
	public List<FinanceMain> getFinanceByCollateralRef(String collateralRef) {
		logger.debug("Entering");

		MapSqlParameterSource source = new MapSqlParameterSource();
		source.addValue("CollateralRef", collateralRef);

		StringBuilder selectSql = new StringBuilder("SELECT FM.FinReference, FM.FinAmount, FM.FinType, FM.FinCcy,FM.ClosingStatus,");
		selectSql.append(" FM.FinAssetValue, FM.NumberOfTerms, FM.MaturityDate, FM.Finstatus,FM.FinStartDate, FM.FirstRepay,");
		selectSql.append(" FT.FinCategory lovDescFinProduct, CA.CollateralRef From FinanceMain FM INNER JOIN ");
		selectSql.append(" CollateralAssignment CA On FM.FinReference = CA.Reference INNER JOIN ");
		selectSql.append(" RMTFinanceTypes FT ON FM.FinType = FT.FinType");
		selectSql.append(" Where CollateralRef =:CollateralRef");

		logger.debug("selectSql: " + selectSql.toString());
		RowMapper<FinanceMain> typeRowMapper = ParameterizedBeanPropertyRowMapper.newInstance(FinanceMain.class);
		List<FinanceMain> financeMainList = new ArrayList<FinanceMain>();
		try {
			financeMainList = this.namedParameterJdbcTemplate.query(selectSql.toString(), source, typeRowMapper);
		} catch (EmptyResultDataAccessException dae) {
			logger.error("Exception: ", dae);
			return Collections.emptyList();
		}
		logger.debug("Leaving");
		return financeMainList;
	}

	/**
	 * Method to get FinanceReferences by Given MandateId.
	 * 
	 * @param mandateId
	 */
	@Override
	public List<String> getFinReferencesByMandateId(long mandateId) {
		logger.debug("Entering");

		FinanceMain financeMain = new FinanceMain();
		financeMain.setMandateID(mandateId);

		StringBuilder selectSql = new StringBuilder("SELECT FinReference ");
		selectSql.append(" From FinanceMain");

		selectSql.append(" Where MandateID =:MandateID");

		logger.debug("selectSql: " + selectSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(financeMain);
		List<String> finReferencesList = new ArrayList<String>();
		try {
			finReferencesList = this.namedParameterJdbcTemplate.queryForList(selectSql.toString(), beanParameters,
					String.class);
		} catch (EmptyResultDataAccessException dae) {
			logger.error("Exception: ", dae);
			return Collections.emptyList();
		}
		logger.debug("Leaving");
		return finReferencesList;
	}

	/**
	 * Method to get FinanceReferences by Given custId.
	 * 
	 * @param custId
	 * @param finActiveStatus
	 */
	@Override
	public List<String> getFinReferencesByCustID(long custId, String finActiveStatus) {
		logger.debug("Entering");

		FinanceMain financeMain = new FinanceMain();
		financeMain.setCustID(custId);
		financeMain.setClosingStatus(finActiveStatus);

		StringBuilder selectSql = new StringBuilder("SELECT FinReference ");
		selectSql.append(" From FinanceMain");
		selectSql.append(" Where CustID =:CustID AND");
		if (StringUtils.isBlank(finActiveStatus)) {
			selectSql.append(" ClosingStatus is null");
		} else {
			selectSql.append(" ClosingStatus =:ClosingStatus");
		}

		logger.debug("selectSql: " + selectSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(financeMain);
		List<String> finReferencesList = new ArrayList<String>();
		try {
			finReferencesList = this.namedParameterJdbcTemplate.queryForList(selectSql.toString(), beanParameters,
					String.class);
		} catch (EmptyResultDataAccessException dae) {
			logger.error("Exception: ", dae);
			return Collections.emptyList();
		}
		logger.debug("Leaving");
		return finReferencesList;
	}

	/***
	 * Method to get the finassetValue for the comparison with teh current asset value in the odMaintenance
	 */
	@Override
	public BigDecimal getFinAssetValue(String finReference) {
		logger.debug("Entering");
		MapSqlParameterSource source = new MapSqlParameterSource();
		source.addValue("FinReference", finReference);

		StringBuilder selectSql = new StringBuilder("SELECT FinAssetValue ");
		selectSql.append(" From FinanceMain");
		selectSql.append(" Where FinReference =:FinReference");
		logger.debug("selectSql: " + selectSql.toString());
		return this.namedParameterJdbcTemplate.queryForObject(selectSql.toString(), source, BigDecimal.class);
	}

	@Override
	public BigDecimal getTotalMaxRepayAmount(long mandateId, String finReference) {
		logger.debug(Literal.ENTERING);
//FIXME Need to convert this sum of, max of logic to java .since it is not supported by Postgresql 
//		// Prepare the SQL.
//		StringBuilder sql = new StringBuilder("select coalesce(sum(max(RepayAmount)), 0) from FinScheduleDetails_View");
//		sql.append(" where FinReference in (select FinReference from FinanceMain_View ");
//		sql.append(" where MandateId = :MandateId and FinIsActive = 1 and FinReference != :FinReference)");
//		sql.append(" group by FinReference");
//
//		// Execute the SQL, binding the arguments.
//		logger.trace(Literal.SQL + sql);
//		MapSqlParameterSource paramSource = new MapSqlParameterSource();
//		paramSource.addValue("MandateId", mandateId);
//		paramSource.addValue("FinReference", finReference);
//
//		logger.debug(Literal.LEAVING);
//		return namedParameterJdbcTemplate.queryForObject(sql.toString(), paramSource, BigDecimal.class);
		return BigDecimal.ZERO;
	}
	
	@Override
	public void updateBucketStatus(String finReference, String status,int bucket, String statusReason) {
		
		logger.debug("Entering");
		
		FinanceMain financeMain = new FinanceMain();
		financeMain.setFinStatus(status);
		financeMain.setFinStsReason(statusReason);
		financeMain.setFinReference(finReference);
		financeMain.setDueBucket(bucket);
		StringBuilder updateSql = new StringBuilder("Update FinanceMain");
		updateSql.append(" Set FinStatus = :FinStatus, FinStsReason = :FinStsReason, DueBucket=:DueBucket ");
		updateSql.append(" Where FinReference =:FinReference");
		
		logger.debug("updateSql: " + updateSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(financeMain);
		this.namedParameterJdbcTemplate.update(updateSql.toString(), beanParameters);
		
		logger.debug("Leaving");
	}
	
	/**
	 * Method for get the Finance Details and FinanceShedule Details
	 */
	@Override
	public List<FinanceMain> getFinMainsForEODByCustId(long custId, boolean isActive) {
		logger.debug("Entering");

		MapSqlParameterSource source = new MapSqlParameterSource();

		StringBuilder selectSql = new StringBuilder();
		
		selectSql.append(" SELECT FinReference, GrcPeriodEndDate, AllowGrcPeriod,");
		selectSql.append(" GraceBaseRate, GraceSpecialRate, GrcPftRate, GrcPftFrq, NextGrcPftDate, AllowGrcPftRvw,");
		selectSql.append(" GrcPftRvwFrq, NextGrcPftRvwDate, AllowGrcCpz, GrcCpzFrq, NextGrcCpzDate, RepayBaseRate,");
		selectSql.append(" RepaySpecialRate, RepayProfitRate, RepayFrq, NextRepayDate, RepayPftFrq, NextRepayPftDate,");
		selectSql.append(" AllowRepayRvw,RepayRvwFrq, NextRepayRvwDate, AllowRepayCpz, RepayCpzFrq, NextRepayCpzDate,");
		selectSql.append(" MaturityDate, CpzAtGraceEnd, GrcRateBasis, RepayRateBasis, FinType, FinCcy, ");
		selectSql.append(" ProfitDaysBasis, FirstRepay, LastRepay, ScheduleMethod,");
		selectSql.append(" FinStartDate, FinAmount, CustID, FinBranch, FinSourceID, RecalType, FinIsActive, ");
		selectSql.append(" LastRepayDate, LastRepayPftDate, LastRepayRvwDate, LastRepayCpzDate, AllowGrcRepay, ");
		selectSql.append(" GrcSchdMthd, GrcMargin, RepayMargin, ClosingStatus, FinRepayPftOnFrq, GrcProfitDaysBasis,");
		selectSql.append(" GrcMinRate, GrcMaxRate , RpyMinRate, RpyMaxRate, ManualSchedule,");
		selectSql.append(" CalRoundingMode, RoundingTarget,RvwRateApplFor, SchCalOnRvw, ");
		selectSql.append(" PastduePftCalMthd, DroppingMethod, RateChgAnyDay, PastduePftMargin, FinRepayMethod, ");
		selectSql.append(" MigratedFinance, ScheduleMaintained, ScheduleRegenerated, MandateID, ");
		selectSql.append(" FinStatus, DueBucket, FinStsReason, BankName, Iban, AccountType, DdaReferenceNo, PromotionCode, ");
		selectSql.append(" FinCategory, ProductCategory, ReAgeBucket,TDSApplicable  ");
		selectSql.append(" FROM FinanceMain Where CustID=:CustID ");
		
		if (isActive) {
			selectSql.append(" and FinIsActive = :FinIsActive ");
		}

		source.addValue("CustID", custId);
		source.addValue("FinIsActive", isActive);
		RowMapper<FinanceMain> typeRowMapper = ParameterizedBeanPropertyRowMapper.newInstance(FinanceMain.class);

		logger.debug("selectSql: " + selectSql.toString());
		
		List<FinanceMain> finMains = this.namedParameterJdbcTemplate.query(selectSql.toString(), source, typeRowMapper); 
		
		logger.debug("Leaving");
		return finMains;
	}
	
	/**
	 * Method for get the Finance Details by Finance Reference
	 */
	@Override
	public FinanceMain getFinMainsForEODByFinRef(String finReference, boolean isActive) {
		logger.debug("Entering");

		FinanceMain financeMain = new FinanceMain(); 
		financeMain.setFinReference(finReference);
		financeMain.setFinIsActive(isActive);

		StringBuilder selectSql = new StringBuilder();

		selectSql.append(" SELECT FinReference, GrcPeriodEndDate, AllowGrcPeriod,");
		selectSql.append(" GraceBaseRate, GraceSpecialRate, GrcPftRate, GrcPftFrq, NextGrcPftDate, AllowGrcPftRvw,");
		selectSql.append(" GrcPftRvwFrq, NextGrcPftRvwDate, AllowGrcCpz, GrcCpzFrq, NextGrcCpzDate, RepayBaseRate,");
		selectSql.append(" RepaySpecialRate, RepayProfitRate, RepayFrq, NextRepayDate, RepayPftFrq, NextRepayPftDate,");
		selectSql.append(" AllowRepayRvw,RepayRvwFrq, NextRepayRvwDate, AllowRepayCpz, RepayCpzFrq, NextRepayCpzDate,");
		selectSql.append(" MaturityDate, CpzAtGraceEnd, GrcRateBasis, RepayRateBasis, FinType, FinCcy, ");
		selectSql.append(" ProfitDaysBasis, FirstRepay, LastRepay, ScheduleMethod,");
		selectSql.append(" FinStartDate, FinAmount, CustID, FinBranch, FinSourceID, RecalType, FinIsActive, ");
		selectSql.append(" LastRepayDate, LastRepayPftDate, LastRepayRvwDate, LastRepayCpzDate, AllowGrcRepay, ");
		selectSql.append(" GrcSchdMthd, GrcMargin, RepayMargin, ClosingStatus, FinRepayPftOnFrq, GrcProfitDaysBasis,");
		selectSql.append(" GrcMinRate, GrcMaxRate , RpyMinRate, RpyMaxRate, ManualSchedule,");
		selectSql.append(" CalRoundingMode, RoundingTarget,RvwRateApplFor, SchCalOnRvw, ");
		selectSql.append(" PastduePftCalMthd, DroppingMethod, RateChgAnyDay, PastduePftMargin, FinRepayMethod, ");
		selectSql.append(" MigratedFinance, ScheduleMaintained, ScheduleRegenerated, MandateID, ");
		selectSql.append(" FinStatus, DueBucket, FinStsReason, BankName, Iban, AccountType, DdaReferenceNo, PromotionCode, ");
		selectSql.append(" FinCategory, ProductCategory, ReAgeBucket,TDSApplicable  ");

		selectSql.append(" FROM FinanceMain Where FinReference = :FinReference ");
		selectSql.append(" AND FinIsActive = :FinIsActive ");

		logger.debug("selectSql: " + selectSql.toString());

		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(financeMain);
		RowMapper<FinanceMain> typeRowMapper = ParameterizedBeanPropertyRowMapper.newInstance(FinanceMain.class);

		logger.debug("Leaving");
		return this.namedParameterJdbcTemplate.queryForObject(selectSql.toString(), beanParameters, typeRowMapper);
	}
	
	@Override
	public int getFinanceMainByBank(String bankCode, String type) {
		logger.debug("Entering");

		FinanceMain financeMain = new FinanceMain();
		financeMain.setBankName(bankCode);

		StringBuilder selectSql = new StringBuilder("SELECT COUNT(*)");
		selectSql.append(" From FinanceMain");
		selectSql.append(StringUtils.trimToEmpty(type));
		selectSql.append(" Where BankName =:BankName");

		logger.debug("selectSql: " + selectSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(financeMain);

		logger.debug("Leaving");
		return this.namedParameterJdbcTemplate.queryForObject(selectSql.toString(), beanParameters, Integer.class);
	}
	
	@Override
	public void updateFinanceInEOD(FinanceMain financeMain, List<String> updateFields, boolean rateRvw) {
		logger.debug("Entering");
		StringBuilder updateSql = new StringBuilder("Update FinanceMain Set ");

		if (!updateFields.isEmpty()) {

			for (int i = 0; i < updateFields.size(); i++) {
				updateSql.append(updateFields.get(i));
				updateSql.append(" = :");
				updateSql.append(updateFields.get(i));
				if (i != updateFields.size()-1) {
					updateSql.append(" ,");
				}
			}

			if (rateRvw) {
				updateSql.append(" ,");
			}
		}
		
		if (rateRvw) {
			//profit related fields for rate review
			updateSql.append(" TotalGracePft = :TotalGracePft, TotalGraceCpz = :TotalGraceCpz ");
			updateSql.append(" ,TotalGrossGrcPft = :TotalGrossGrcPft, TotalProfit = :TotalProfit ");
			updateSql.append(" ,TotalCpz = :TotalCpz, TotalGrossPft = :TotalGrossPft ");
			updateSql.append(" ,TotalRepayAmt = :TotalRepayAmt, FinRepaymentAmount = :FinRepaymentAmount ");
		}

		updateSql.append(" Where FinReference =:FinReference");

		logger.debug("updateSql: " + updateSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(financeMain);
		this.namedParameterJdbcTemplate.update(updateSql.toString(), beanParameters);

		logger.debug("Leaving");

	}
	
	
	/**
	 * Fetch the Record Finance Main Detail details by key field
	 * 
	 * @param id
	 *            (String)
	 * @param type
	 *            (String) ""/_Temp/_View
	 * @return FinanceMain
	 */
	@Override
	public List<FinanceMain> getBYCustIdForLimitRebuild(final long id, boolean orgination) {
		logger.debug("Entering");

		FinanceMain financeMain = new FinanceMain();
		financeMain.setCustID(id);

		StringBuilder sql = new StringBuilder("");
		sql.append(" SELECT FinReference, GrcPeriodEndDate, AllowGrcPeriod,");
		sql.append(" GraceBaseRate, GraceSpecialRate, GrcPftRate, GrcPftFrq, NextGrcPftDate, AllowGrcPftRvw,");
		sql.append(" GrcPftRvwFrq, NextGrcPftRvwDate, AllowGrcCpz, GrcCpzFrq, NextGrcCpzDate, RepayBaseRate,");
		sql.append(" RepaySpecialRate, RepayProfitRate, RepayFrq, NextRepayDate, RepayPftFrq, NextRepayPftDate,");
		sql.append(" AllowRepayRvw,RepayRvwFrq, NextRepayRvwDate, AllowRepayCpz, RepayCpzFrq, NextRepayCpzDate,");
		sql.append(" MaturityDate, CpzAtGraceEnd, GrcRateBasis, RepayRateBasis, FinType, FinCcy, ");
		sql.append(" ProfitDaysBasis, FirstRepay, LastRepay, ScheduleMethod,DownPayment,");
		sql.append(" FinStartDate, FinAmount, CustID, FinBranch, FinSourceID, RecalType, FinIsActive, ");
		sql.append(" LastRepayDate, LastRepayPftDate, LastRepayRvwDate, LastRepayCpzDate, AllowGrcRepay, ");
		sql.append(" GrcSchdMthd, GrcMargin, RepayMargin, ClosingStatus, FinRepayPftOnFrq, GrcProfitDaysBasis,");
		sql.append(" GrcMinRate, GrcMaxRate , RpyMinRate, RpyMaxRate, ManualSchedule,");
		sql.append(" CalRoundingMode, RvwRateApplFor, SchCalOnRvw,FinAssetValue,FinCurrAssetValue, ");
		sql.append(" PastduePftCalMthd, DroppingMethod, RateChgAnyDay, PastduePftMargin, FinRepayMethod, ");
		sql.append(" MigratedFinance, ScheduleMaintained, ScheduleRegenerated, MandateID, ");
		sql.append(" FinStatus, FinStsReason, BankName, Iban, AccountType, DdaReferenceNo, PromotionCode, ");
		sql.append(" FinCategory, ProductCategory, ReAgeBucket,TDSApplicable  ");
		if (orgination) {
			sql.append(" , 1 LimitValid  ");
		}
		sql.append(" FROM FinanceMain");
		if (orgination) {
			sql.append(TableType.TEMP_TAB.getSuffix());
		}
		sql.append(" Where CustID=:CustID ");
		if (orgination) {
			if (App.DATABASE == Database.ORACLE) {
				sql.append(" AND RcdMaintainSts IS NULL ");
			} else {
				sql.append(" AND RcdMaintainSts = '' ");
			}
		}

		logger.debug("selectSql: " + sql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(financeMain);
		RowMapper<FinanceMain> typeRowMapper = ParameterizedBeanPropertyRowMapper.newInstance(FinanceMain.class);

		try {
			return this.namedParameterJdbcTemplate.query(sql.toString(), beanParameters, typeRowMapper);
		} catch (EmptyResultDataAccessException dae) {
			logger.debug("Exception: ", dae);
			return null;
		}

	}

	@Override
	public FinanceMain getFinanceBasicDetailByRef(String finReference, boolean isWIF) {
		logger.debug("Entering");

		MapSqlParameterSource source = new MapSqlParameterSource();
		source.addValue("FinReference", finReference);

		StringBuilder selectSql = new StringBuilder("SELECT FM.FinReference, FM.MaturityDate, FT.Ratechganyday ");

		if (isWIF) {
			selectSql.append(" From WIFFinanceMain FM");
		} else {
			selectSql.append(" From FinanceMain FM");
		}
		selectSql.append(" inner join RMTfinanceTypes FT on FM.FinType = FT.FinType ");
		selectSql.append(" Where FinReference =:FinReference");

		logger.debug("selectSql: " + selectSql.toString());

		RowMapper<FinanceMain> typeRowMapper = ParameterizedBeanPropertyRowMapper.newInstance(FinanceMain.class);
		FinanceMain financeMain = null;
		try {
			financeMain = this.namedParameterJdbcTemplate.queryForObject(selectSql.toString(), source, typeRowMapper);
		} catch (EmptyResultDataAccessException e) {
			logger.warn("Exception: ", e);
			financeMain = null;
		}
		logger.debug("Leaving");
		return financeMain;
	}

	/**
	 * 
	 * 
	 */
	@Override
	public void updateFinMandateId(long mandateId, String finReference, String type) {
		logger.debug("Entering");

		MapSqlParameterSource source = new MapSqlParameterSource();
		source.addValue("FinReference", finReference);
		source.addValue("MandateId", mandateId);
		
		StringBuilder sql = new StringBuilder("Update FinanceMain");
		sql.append(type);
		sql.append(" set MandateId =:MandateId");
		sql.append(" where FinReference =:FinReference");
		logger.debug("updateSql: " + sql.toString());

		this.namedParameterJdbcTemplate.update(sql.toString(), source);

		logger.debug("Leaving");
	}

	/**
	 * Method for fetch existing mandate id by reference.
	 * 
	 * @param finReference
	 * @param type
	 * @return mandateId
	 */
	@Override
	public long getMandateIdByRef(String finReference, String type) {
		logger.debug(Literal.ENTERING);

		MapSqlParameterSource source = new MapSqlParameterSource();
		source.addValue("FinReference", finReference);

		StringBuilder selectSql = new StringBuilder("SELECT MandateId From FinanceMain");
		selectSql.append(type);
		selectSql.append(" Where FinReference =:FinReference");

		logger.debug("selectSql: " + selectSql.toString());

		long mandateId = Long.MIN_VALUE;
		try {
			mandateId = this.namedParameterJdbcTemplate.queryForObject(selectSql.toString(), source, Long.class);
		} catch (EmptyResultDataAccessException e) {
			logger.warn("Exception: ", e);
			mandateId = Long.MIN_VALUE;
		}
		logger.debug(Literal.LEAVING);
		return mandateId;
	}

	/**
	 * Method for fetch total number of records i.e count
	 * 
	 * @param finReference
	 */
	@Override
	public int getFinanceCountById(String finReference) {
		logger.debug("Entering");

		FinanceMain financeMain = new FinanceMain();
		financeMain.setFinReference(finReference);

		StringBuilder selectSql = new StringBuilder("SELECT COUNT(*) ");
		selectSql.append(" From FinanceMain_AView");
		selectSql.append(" Where FinReference =:FinReference");

		logger.debug("selectSql: " + selectSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(financeMain);

		try {
			return this.namedParameterJdbcTemplate.queryForObject(selectSql.toString(), beanParameters, Integer.class);
		} catch (EmptyResultDataAccessException dae) {
			logger.debug("Exception: ", dae);
			return 0;
		}
	}
	/**
	 * Method for check Application number already Exists or not
	 * 
	 * @param applicationNo
	 * @param finType
	 */
	@Override
	public boolean isAppNoExists(String applicationNo, TableType tableType) {
		logger.debug("Entering");

		String selectSql = new String();
		String whereClause = " ApplicationNo =:ApplicationNo AND FinIsActive = :FinIsActive";
		switch (tableType) {
		case MAIN_TAB:
			selectSql = QueryUtil.getCountQuery("FinanceMain", whereClause);
			break;
		case TEMP_TAB:
			selectSql = QueryUtil.getCountQuery("FinanceMain_Temp", whereClause);
			break;
		default:
			selectSql = QueryUtil.getCountQuery(new String[] { "FinanceMain_Temp", "FinanceMain" }, whereClause);
			break;
		}

		logger.debug("selectSql: " + selectSql.toString());
		// Execute the SQL, binding the arguments.
		logger.trace(Literal.SQL + selectSql);
		MapSqlParameterSource paramSource = new MapSqlParameterSource();
		paramSource.addValue("ApplicationNo", applicationNo);
		paramSource.addValue("FinIsActive", 1);

		Integer count = namedParameterJdbcTemplate.queryForObject(selectSql, paramSource, Integer.class);

		boolean exists = false;
		if (count > 0) {
			exists = true;
		}
		return exists;
	}

	@Override
	public String getApplicationNoById(String finReference, String type) {
		logger.debug("Entering");

		String applicationNo = null;
		MapSqlParameterSource source = new MapSqlParameterSource();
		source.addValue("FinReference", finReference);
		StringBuilder sql = new StringBuilder("SELECT ApplicationNo From FinanceMain");
		sql.append(StringUtils.trimToEmpty(type));
		sql.append(" Where FinReference = :FinReference");
		logger.debug("selectSql: " + sql.toString());

		try {
			applicationNo = this.namedParameterJdbcTemplate.queryForObject(sql.toString(), source, String.class);
		} catch (EmptyResultDataAccessException e) {
			logger.warn("Exception: ", e);
			applicationNo = null;
		}

		logger.debug("Leaving");
		return applicationNo;
	}


	/**
	 * Method to get FinanceReferences by Given custId.
	 * 
	 * @param custId
	 */
	@Override
	public List<String> getFinReferencesByCustID(long custID) {
		logger.debug("Entering");

		FinanceMain financeMain = new FinanceMain();
		financeMain.setCustID(custID);
		financeMain.setFinIsActive(true);

		StringBuilder selectSql = new StringBuilder("SELECT FinReference ");
		selectSql.append(" From FinanceMain");
		selectSql.append(" Where CustID =:CustID AND FinIsActive = :FinIsActive");
		

		logger.debug("selectSql: " + selectSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(financeMain);
		List<String> finReferencesList = new ArrayList<String>();
		try {
			finReferencesList = this.namedParameterJdbcTemplate.queryForList(selectSql.toString(), beanParameters,
					String.class);
		} catch (EmptyResultDataAccessException dae) {
			logger.error("Exception: ", dae);
			return Collections.emptyList();
		}
		logger.debug("Leaving");
		return finReferencesList;
	}

	/**
	 * Method for get total number of records from FinanceMain master table.<br>
	 * 
	 * @param divisionCode
	 * 
	 * @return Boolean
	 */
	@Override
	public boolean isFinTypeExistsInFinanceMain(String finType, String type) {
		logger.debug("Entering");

		MapSqlParameterSource source = new MapSqlParameterSource();
		source.addValue("FINTYPE", finType);

		StringBuffer selectSql = new StringBuffer();
		selectSql.append("SELECT COUNT(*) FROM FINANCEMAIN");
		selectSql.append(StringUtils.trimToEmpty(type));
		selectSql.append(" WHERE FINTYPE= :FINTYPE");

		logger.debug("insertSql: " + selectSql.toString());
		int recordCount = 0;
		try {
			recordCount = this.namedParameterJdbcTemplate.queryForObject(selectSql.toString(), source, Integer.class);
		} catch (EmptyResultDataAccessException dae) {
			logger.debug("Exception: ", dae);
			recordCount = 0;
		}
		logger.debug("Leaving");

		return recordCount > 0 ? true : false;
	}
	
	@Override
	public List<FinanceMain> getFinancesByExpenseType (String finType, Date finApprovalStartDate, Date finApprovalEndDate) {
		logger.debug("Entering");
		
		List<FinanceMain> finMains = null;
		StringBuilder selectSql = new StringBuilder();
		selectSql.append(" SELECT FinReference, FinAssetValue, FinCurrAssetValue, FinCcy From FinanceMain");
		selectSql.append(" WHERE  FinType = :FinType And (ClosingStatus is null or ClosingStatus <> :ClosingStatus)");
		selectSql.append(" And FinApprovedDate >= :FinApprovalStartDate And FinApprovedDate <= :FinApprovalEndDate");
		
		logger.debug("selectSql: " + selectSql.toString());
		
		MapSqlParameterSource source = new MapSqlParameterSource();
		source.addValue("FinType", finType);
		source.addValue("ClosingStatus", "C");
		source.addValue("FinApprovalStartDate", finApprovalStartDate);
		source.addValue("FinApprovalEndDate", finApprovalEndDate);
		
		try {
			RowMapper<FinanceMain> typeRowMapper = ParameterizedBeanPropertyRowMapper.newInstance(FinanceMain.class);
			finMains = this.namedParameterJdbcTemplate.query(selectSql.toString(), source, typeRowMapper);
		} catch (EmptyResultDataAccessException e) {
			finMains = new ArrayList<FinanceMain>();
		}

		logger.debug("Leaving");
		
		return finMains;
	}
	
	/**
	 * Method for get total number of records from FinanceMain master table.<br>
	 * 
	 * @param loanPurposeCode
	 * 
	 * @return Boolean
	 */
	@Override
	public boolean isLoanPurposeExits(String loanPurposeCode, String type) {
		logger.debug("Entering");

		MapSqlParameterSource source = new MapSqlParameterSource();
		source.addValue("finpurpose", loanPurposeCode);

		StringBuffer selectSql = new StringBuffer();
		selectSql.append("SELECT COUNT(*) FROM FINANCEMAIN");
		selectSql.append(StringUtils.trimToEmpty(type));
		selectSql.append(" WHERE finpurpose= :finpurpose");

		logger.debug("insertSql: " + selectSql.toString());
		int recordCount = 0;
		try {
			recordCount = this.namedParameterJdbcTemplate.queryForObject(selectSql.toString(), source, Integer.class);
		} catch (EmptyResultDataAccessException dae) {
			logger.debug("Exception: ", dae);
			recordCount = 0;
		}
		logger.debug("Leaving");

		return recordCount > 0 ? true : false;
	}
}
