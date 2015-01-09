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

import java.util.List;

import javax.sql.DataSource;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.core.simple.ParameterizedBeanPropertyRowMapper;

import com.pennant.app.util.ErrorUtil;
import com.pennant.backend.dao.impl.BasisCodeDAO;
import com.pennant.backend.dao.rmtmasters.FinanceTypeDAO;
import com.pennant.backend.model.ErrorDetails;
import com.pennant.backend.model.rmtmasters.FinanceType;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantJavaUtil;

/**
 * DAO methods implementation for the <b>FinanceType model</b> class.<br>
 * 
 */
public class FinanceTypeDAOImpl extends BasisCodeDAO<FinanceType> implements FinanceTypeDAO {

	private static Logger logger = Logger.getLogger(FinanceTypeDAOImpl.class);

	// Spring Named JDBC Template
	private NamedParameterJdbcTemplate	namedParameterJdbcTemplate;

	/**
	 * This method set the Work Flow id based on the module name and return the new FinanceType
	 * 
	 * @return FinanceType
	 */
	@Override
	public FinanceType getFinanceType() {
		logger.debug("Entering");
		FinanceType financeType = new FinanceType("");
		logger.debug("Leaving");
		return financeType;
	}

	/**
	 * This method get the module from method getFinanceType() and set the new record flag as true and
	 * return FinanceType()
	 * 
	 * @return FinanceType
	 */
	@Override
	public FinanceType getNewFinanceType() {
		logger.debug("Entering");
		FinanceType financeType = getFinanceType();
		financeType.setFinCategory("");
		financeType.setNewRecord(true);
		logger.debug("Leaving");
		return financeType;
	}

	public FinanceType getCommodityFinanceType() {
		logger.debug("Entering");
		FinanceType financeType = new FinanceType("CF");
		financeType.setFinCategory("CF");
		logger.debug("Leaving");
		return financeType;
	}

	/**
	 * This method get the module from method getFinanceType() and set the new record flag as true and
	 * return FinanceType()
	 * 
	 * @return FinanceType
	 */

	@Override
	public FinanceType getNewCommodityFinanceType() {
		logger.debug("Entering");
		FinanceType financeType = getCommodityFinanceType();
		financeType.setNewRecord(true);
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
	public FinanceType getFinanceTypeByID(final String id, String type) {
		logger.debug("Entering");
		FinanceType financeType = new FinanceType();
		financeType.setId(id);

		StringBuilder selectSql = new StringBuilder("SELECT FinType,FinCategory, FinTypeDesc, FinCcy, FinDaysCalType,");
		selectSql.append(" FinAcType, FinContingentAcType, FinBankContingentAcType, FinProvisionAcType,FinSuspAcType,");
		selectSql.append(" FinIsGenRef, FinMaxAmount, FinMinAmount,");
		selectSql.append(" FinIsOpenNewFinAc, FinDftStmtFrq, FinIsAlwMD, FinSchdMthd, FInIsAlwGrace,");
		selectSql.append(" FinHistRetension, FinOrgPrfUnchanged, FinFrEqrepayment, FinRateType, FinBaseRate,");
		selectSql.append(" FinSplRate,FinIntRate, FInMinRate, FinMaxRate, FinDftIntFrq,  FinIsIntCpz,");
		selectSql.append(" FinCpzFrq,  FinIsRvwAlw, FinRvwFrq,  FinGrcRateType, FinGrcBaseRate,");
		selectSql.append(" FinGrcSplRate, FinGrcIntRate, FInGrcMinRate, FinGrcMaxRate,FinGrcDftIntFrq,");
		selectSql.append(" FinGrcIsIntCpz, FinGrcCpzFrq, FinGrcIsRvwAlw, FinGrcRvwFrq, FinMinTerm,");
		selectSql.append(" FinMaxTerm, FinDftTerms, FinRpyFrq,  FInRepayMethod, FinIsAlwPartialRpy,");
		selectSql.append(" FinIsAlwDifferment,FinMaxDifferment, FinIsAlwEarlyRpy, FinIsAlwEarlySettle,");
		selectSql.append(" FinODRpyTries, FinLatePayRule, AlwPlanDeferment,PlanDeferCount ,FinAEAddDsbOD,");
		selectSql.append(" FinAEAddDsbFD, FinAEAddDsbFDA, FinAEAmzNorm, FinAEAmzSusp, FinAEToNoAmz,");
		selectSql.append(" FinToAmz, FinAEMAmz, FinAERateChg, FinAERepay, FinAEEarlyPay, FinAEEarlySettle,");
		selectSql.append(" FinIsDwPayRequired, FinRvwRateApplFor, FinAlwRateChangeAnyDate, FinGrcRvwRateApplFor,");
		selectSql.append(" FinIsIntCpzAtGrcEnd, FinGrcAlwRateChgAnyDate, FinMinDownPayAmount,");
		selectSql.append(" FinAEWriteOff, FinAEWriteOffBK, FinAEGraceEnd,FinSchCalCodeOnRvw, FinAssetType ,");
		selectSql.append("	FinDepositRestrictedTo,FinAEBuyOrInception,FinAESellOrMaturity,");
		selectSql.append("	FinIsActive,PftPayAcType,FinIsOpenPftPayAcc,FinAEPlanDef,FinDefRepay,FinGrcSchdMthd,FinIsAlwGrcRepay,");
		selectSql.append("	FinMargin,FinGrcMargin,FinProvision,FinSchdChange,FinScheduleOn,FinGrcScheduleOn,");
		selectSql.append("	FinCommitmentReq,FinCollateralReq,FinDepreciationReq,FinDepreciationFrq,FinDepreciationRule,");
		selectSql.append("  FinGrcAlwIndRate, FinGrcIndBaseRate, FinAlwIndRate, FInIndBaseRate,FinAECapitalize,");
		selectSql.append("  AllowRIAInvestment , AllowParllelFinance , OverrideLimit , LimitRequired , " );
		selectSql.append(" FinCommitmentOvrride , FinCollateralOvrride ,FinInstDate ,FinRepayPftOnFrq, FinAEProgClaim , FinAEMaturity, FinPftUnChanged, ");
		selectSql.append(" ApplyODPenalty , ODIncGrcDays , ODChargeType , ODGraceDays , ODChargeCalOn , ODChargeAmtOrPerc , ODAllowWaiver , ODMaxWaiverPerc,FinDivision, ");
		selectSql.append(" StepFinance , SteppingMandatory , AlwManualSteps , AlwdStepPolicies, DftStepPolicy, ");

		if (type.contains("View")) {
			selectSql.append(" lovDescFinCcyName,lovDescFinDaysCalTypeName, lovDescFinContingentAcTypeName,");
			selectSql.append(" lovDescFinBankContingentAcTypeName, lovDescFinProvisionAcTypeName,lovDescFinSuspAcTypeName, lovDescFinAcTypeName,");
			selectSql.append(" lovDescFinSchdMthdName,lovDescFinRateTypeName,lovDescFinBaseRateName,lovDescFinSplRateName,");
			selectSql.append(" lovDescFinGrcRateTypeName,lovDescFinGrcBaseRateName,lovDescFinGrcSplRateName,");
			selectSql.append(" lovDescFinAEAddDsbODName, lovDescFinAECapitalizeName,lovDescEVFinAECapitalizeName, ");
			selectSql.append(" lovDescFinAEAddDsbFDName, lovDescFinAEAddDsbFDAName,lovDescFinAEAmzNormName, lovDescFinAEAmzSuspName,");
			selectSql.append(" lovDescFinAEToNoAmzName,lovDescFinToAmzName, lovDescFinMAmzName, lovDescFinAERateChgName, ");
			selectSql.append(" lovDescFinAERepayName,lovDescFinAEWriteOffName,lovDescFinAEWriteOffBKName,lovDescFinAEGraceEndName,");
			selectSql.append(" lovDescFInRepayMethodName,lovDescEVFinAEAddDsbODName,lovDescEVFinAEAddDsbFDName,lovDescEVFinAEAddDsbFDAName,");
			selectSql.append(" lovDescEVFinAEAmzNormName, lovDescEVFinAEAmzSuspName,lovDescEVFinAEToNoAmzName,lovDescEVFinToAmzName, lovDescEVFinMAmzName, ");
			selectSql.append(" lovDescEVFinAERateChgName, lovDescEVFinAERepayName, lovDescEVFinAEEarlyPayName,");
			selectSql.append(" lovDescEVFinAEEarlySettleName, lovDescEVFinAEWriteOffName,lovDescEVFinAEWriteOffBKName,lovDescEVFinAEGraceEndName,lovDescFinFormetter,");
			selectSql.append(" lovDescProductCodeName,lovDescProductCodeDesc,lovDescAssetCodeName,lovDescPftPayAcTypeName,lovDescFinLatePayRuleName,lovDescEVFinLatePayRuleName,");
			selectSql.append(" lovDescFinAEPlanDefName,lovDescEVFinAEPlanDefName,lovDescFinDefRepayName,lovDescEVFinDefRepayName,");
			selectSql.append(" lovDescEVFinProvisionName,lovDescFinProvisionName,lovDescFinSchdChangeName,lovDescEVFinSchdChangeName, " );
			selectSql.append(" lovDescFinAEProgClaimName,lovDescEVFinAEProgClaimName, lovDescFinAEMaturityName,lovDescEVFinAEMaturityName, ");
			selectSql.append(" lovDescFInIndBaseRateName, lovDescFinGrcIndBaseRateName, lovDescFinDepreciationRuleName,lovDescEVFinDepreciationRuleName ," );
			selectSql.append(" lovDescFinInstDateName,lovDescEVFinInstDateName,lovDescFinDivisionName,lovDescFinAEEarlySettleName,");
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

		StringBuilder selectSql = new StringBuilder("SELECT FinType, FinAcType, FinCategory,FinDivision, " );
		selectSql.append(" FinIsOpenNewFinAc, PftPayAcType,  FinSuspAcType, FinProvisionAcType , FinAEAddDsbFD, " );
		selectSql.append(" FinAEAddDsbFDA, FinAEAddDsbOD, FinAEAmzNorm, FinAEAmzSusp, FinDefRepay, FinAEPlanDef, " );
		selectSql.append(" FinAEEarlyPay, FinAEEarlySettle, FinLatePayRule, FinToAmz, FinAEToNoAmz, FinAERateChg, " );
		selectSql.append(" FinAERepay, FinAEWriteOff, FinSchdChange, FinAECapitalize, FinProvision, " );
		selectSql.append(" FinDepreciationRule, FinAEProgClaim, FinAEMaturity,FinAEMAmz, FinAEWriteOffBK, FinAEGraceEnd" );
		selectSql.append(" FROM RMTFinanceTypes");
		selectSql.append(" Where FinType = :FinType");

		logger.debug("selectListSql: " + selectSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(financeType);
		RowMapper<FinanceType> typeRowMapper = ParameterizedBeanPropertyRowMapper.newInstance(FinanceType.class);

		try {
			financeType = this.namedParameterJdbcTemplate.queryForObject(selectSql.toString(), beanParameters, typeRowMapper);
		} catch (EmptyResultDataAccessException e) {
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

		StringBuilder selectSql = new StringBuilder("SELECT FinType, FinAcType, FinCategory, FinDivision, " );
		selectSql.append(" FinIsOpenNewFinAc, PftPayAcType,  FinSuspAcType, FinProvisionAcType , FinAEAddDsbFD, " );
		selectSql.append(" FinAEAddDsbFDA, FinAEAddDsbOD, FinAEAmzNorm, FinAEAmzSusp, FinDefRepay, FinAEPlanDef, " );
		selectSql.append(" FinAEEarlyPay, FinAEEarlySettle, FinLatePayRule, FinToAmz, FinAEToNoAmz, FinAERateChg, " );
		selectSql.append(" FinAERepay, FinAEWriteOff, FinSchdChange, FinAECapitalize, FinProvision, " );
		selectSql.append(" FinDepreciationRule, FinAEProgClaim, FinAEMaturity,FinAEMAmz, FinAEWriteOffBK, FinAEGraceEnd," );
		selectSql.append(" AllowRIAInvestment, FinIsAlwPartialRpy" );
		selectSql.append(" FROM RMTFinanceTypes");

		logger.debug("selectListSql: " + selectSql.toString());
		RowMapper<FinanceType> typeRowMapper = ParameterizedBeanPropertyRowMapper.newInstance(FinanceType.class);

		logger.debug("Leaving");
		return this.namedParameterJdbcTemplate.getJdbcOperations().query(selectSql.toString(), typeRowMapper);
    }

	/**
	 * This method initialize the Record.
	 * 
	 * @param FinanceType
	 *         (financeType)
	 * @return FinanceType
	 */
	@Override
	public void initialize(FinanceType financeType) {
		super.initialize(financeType);
	}

	/**
	 * This method refresh the Record.
	 * 
	 * @param FinanceType
	 *         (financeType)
	 * @return void
	 */
	@Override
	public void refresh(FinanceType financeType) {

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
	@SuppressWarnings("serial")
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
				ErrorDetails errorDetails = ErrorUtil.getErrorDetail(new ErrorDetails(PennantConstants.KEY_FIELD,
						"41003", errParm, valueParm), financeType.getUserDetails().getUsrLanguage());
				throw new DataAccessException(errorDetails.getError()) { };
			}
		} catch (DataAccessException e) {
			logger.debug("Error delete Method");
			logger.error(e);
			ErrorDetails errorDetails = ErrorUtil.getErrorDetail(new ErrorDetails(PennantConstants.KEY_FIELD, 
					"41006", errParm, valueParm), financeType.getUserDetails().getUsrLanguage());
			throw new DataAccessException(errorDetails.getError()) { };
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
		insertSql.append("(FinType, FinCategory,FinTypeDesc, FinCcy,  FinDaysCalType, FinAcType, FinContingentAcType,"); 
		insertSql.append(" FinBankContingentAcType, FinProvisionAcType,FinSuspAcType, FinIsGenRef,");
		insertSql.append(" FinMaxAmount, FinMinAmount,  FinIsOpenNewFinAc, FinDftStmtFrq,  FinIsAlwMD,");
		insertSql.append(" FinSchdMthd, FInIsAlwGrace, FinHistRetension, FinOrgPrfUnchanged, FinFrEqrepayment, FinRateType,");
		insertSql.append(" FinBaseRate, FinSplRate, FinIntRate, FInMinRate, FinMaxRate,FinDftIntFrq,  FinIsIntCpz, FinCpzFrq,");
		insertSql.append(" FinIsRvwAlw, FinRvwFrq, FinGrcRateType, FinGrcBaseRate, FinGrcSplRate, FinGrcIntRate, FInGrcMinRate,");
		insertSql.append(" FinGrcMaxRate,FinGrcDftIntFrq,  FinGrcIsIntCpz, FinGrcCpzFrq,  FinGrcIsRvwAlw, FinGrcRvwFrq, FinMinTerm,");
		insertSql.append(" FinMaxTerm, FinDftTerms, FinRpyFrq,  FInRepayMethod,FinIsAlwPartialRpy, FinIsAlwDifferment, FinMaxDifferment,");
		insertSql.append(" AlwPlanDeferment, PlanDeferCount,FinIsAlwEarlyRpy, FinIsAlwEarlySettle, FinODRpyTries, FinLatePayRule, ");
		insertSql.append(" FinAEAddDsbOD, FinAEAddDsbFD,FinAEAddDsbFDA, FinAEAmzNorm, ");
		insertSql.append(" FinAEAmzSusp, FinAEToNoAmz, FinToAmz, FinAEMAmz, FinAERateChg, FinAERepay, FinAEEarlyPay, ");
		insertSql.append(" FinAEEarlySettle, FinAEWriteOff,FinAEWriteOffBK, FinAEGraceEnd, FinIsDwPayRequired, FinRvwRateApplFor,FinIsIntCpzAtGrcEnd, ");
		insertSql.append(" FinAlwRateChangeAnyDate, FinGrcRvwRateApplFor,FinGrcAlwRateChgAnyDate , FinMinDownPayAmount , ");
		insertSql.append(" FinSchCalCodeOnRvw,FinAssetType ,FinDepositRestrictedTo,FinAEBuyOrInception,FinAESellOrMaturity, ");
		insertSql.append(" FinIsActive, PftPayAcType,FinIsOpenPftPayAcc	,FinAEPlanDef,FinDefRepay,Version , LastMntBy, LastMntOn, RecordStatus, RoleCode, NextRoleCode, TaskId, ");
		insertSql.append(" NextTaskId, RecordType, WorkflowId ,FinGrcSchdMthd,FinIsAlwGrcRepay,");
		insertSql.append("	FinCommitmentReq,FinCollateralReq,FinDepreciationReq,FinDepreciationFrq,FinDepreciationRule,");
		insertSql.append(" FinMargin,FinGrcMargin,FinProvision,FinSchdChange,FinScheduleOn,FinGrcScheduleOn, ");
		insertSql.append(" FinAlwIndRate,FinIndBaseRate,FinGrcAlwIndRate,FinGrcIndBaseRate,FinAECapitalize, FinAEProgClaim , FinAEMaturity , FinPftUnChanged ,");
		insertSql.append("  AllowRIAInvestment , AllowParllelFinance , OverrideLimit, LimitRequired, FinCommitmentOvrride, FinCollateralOvrride, FinInstDate , FinRepayPftOnFrq, ");
		insertSql.append("  ApplyODPenalty , ODIncGrcDays , ODChargeType , ODGraceDays , ODChargeCalOn , ODChargeAmtOrPerc , ODAllowWaiver , ODMaxWaiverPerc, FinDivision, ");
		insertSql.append("  StepFinance , SteppingMandatory , AlwManualSteps , AlwdStepPolicies, DftStepPolicy) ");

		insertSql.append(" Values(:FinType, :FinCategory, :FinTypeDesc, :FinCcy, :FinDaysCalType, :FinAcType, ");
		insertSql.append(" :FinContingentAcType, :FinBankContingentAcType, :FinProvisionAcType, :FinSuspAcType,");
		insertSql.append(" :FinIsGenRef, :FinMaxAmount, :FinMinAmount, :FinIsOpenNewFinAc, :FinDftStmtFrq,  :FinIsAlwMD, ");
		insertSql.append(" :FinSchdMthd, :FInIsAlwGrace, :FinHistRetension, :FinOrgPrfUnchanged, :FinFrEqrepayment, ");
		insertSql.append(" :FinRateType, :FinBaseRate, :FinSplRate, :FinIntRate, :FInMinRate, :FinMaxRate, :FinDftIntFrq, ");
		insertSql.append(" :FinIsIntCpz, :FinCpzFrq,  :FinIsRvwAlw, :FinRvwFrq,  :FinGrcRateType, :FinGrcBaseRate, ");
		insertSql.append(" :FinGrcSplRate, :FinGrcIntRate, :FInGrcMinRate, :FinGrcMaxRate, :FinGrcDftIntFrq, :FinGrcIsIntCpz, ");
		insertSql.append(" :FinGrcCpzFrq,  :FinGrcIsRvwAlw, :FinGrcRvwFrq, :FinMinTerm, :FinMaxTerm, :FinDftTerms, :FinRpyFrq,");
		insertSql.append(" :FInRepayMethod, :FinIsAlwPartialRpy, :FinIsAlwDifferment, :FinMaxDifferment,:AlwPlanDeferment ,:PlanDeferCount ,:FinIsAlwEarlyRpy, ");
		insertSql.append(" :FinIsAlwEarlySettle, :FinODRpyTries, :FinLatePayRule, ");
		insertSql.append(" :FinAEAddDsbOD, :FinAEAddDsbFD, :FinAEAddDsbFDA, :FinAEAmzNorm, :FinAEAmzSusp, :FinAEToNoAmz, :FinToAmz, :FinAEMAmz, ");
		insertSql.append(" :FinAERateChg, :FinAERepay, :FinAEEarlyPay, :FinAEEarlySettle, :FinAEWriteOff,:FinAEWriteOffBK, :FinAEGraceEnd, ");
		insertSql.append(" :FinIsDwPayRequired, :FinRvwRateApplFor, :FinIsIntCpzAtGrcEnd, :FinAlwRateChangeAnyDate,");
		insertSql.append(" :FinGrcRvwRateApplFor, :FinGrcAlwRateChgAnyDate , :FinMinDownPayAmount, ");
		insertSql.append(" :FinSchCalCodeOnRvw,:FinAssetType,:FinDepositRestrictedTo,:FinAEBuyOrInception,:FinAESellOrMaturity,  ");
		insertSql.append(" :FinIsActive,:PftPayAcType,:FinIsOpenPftPayAcc,:FinAEPlanDef,:FinDefRepay,");
		insertSql.append(" :Version , :LastMntBy, :LastMntOn, :RecordStatus, :RoleCode, :NextRoleCode, :TaskId, :NextTaskId, ");
		insertSql.append(" :RecordType, :WorkflowId ,:FinGrcSchdMthd,:FinIsAlwGrcRepay,");
		insertSql.append(" :FinCommitmentReq,:FinCollateralReq,:FinDepreciationReq,:FinDepreciationFrq,:FinDepreciationRule,");
		insertSql.append(" :FinMargin,:FinGrcMargin,:FinProvision,:FinSchdChange,:FinScheduleOn,:FinGrcScheduleOn,");
		insertSql.append(" :FinAlwIndRate,:FinIndBaseRate,:FinGrcAlwIndRate,:FinGrcIndBaseRate, :FinAECapitalize , :FinAEProgClaim ,:FinAEMaturity , :FinPftUnChanged,");
		insertSql.append(" :AllowRIAInvestment , :AllowParllelFinance , :OverrideLimit, :LimitRequired, :FinCommitmentOvrride, :FinCollateralOvrride, :FinInstDate, :FinRepayPftOnFrq , ");
		insertSql.append(" :ApplyODPenalty , :ODIncGrcDays , :ODChargeType , :ODGraceDays , :ODChargeCalOn , :ODChargeAmtOrPerc , :ODAllowWaiver , :ODMaxWaiverPerc, :FinDivision , ");
		insertSql.append(" :StepFinance , :SteppingMandatory , :AlwManualSteps , :AlwdStepPolicies , :DftStepPolicy) ");
		
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

	@SuppressWarnings("serial")
	@Override
	public void update(FinanceType financeType, String type) {
		int recordCount = 0;
		logger.debug("Entering");
		StringBuilder updateSql = new StringBuilder("Update RMTFinanceTypes");
		updateSql.append(StringUtils.trimToEmpty(type));
		updateSql.append(" Set FinType = :FinType, FinTypeDesc = :FinTypeDesc, FinCategory =:FinCategory, FinCcy = :FinCcy,");
		updateSql.append(" FinDaysCalType = :FinDaysCalType,FinAcType = :FinAcType, FinContingentAcType = :FinContingentAcType,");
		updateSql.append(" FinBankContingentAcType= :FinBankContingentAcType, FinProvisionAcType= :FinProvisionAcType,FinSuspAcType=:FinSuspAcType,");
		updateSql.append(" FinIsGenRef = :FinIsGenRef, FinMaxAmount = :FinMaxAmount,FinMinAmount = :FinMinAmount,");
		updateSql.append(" FinIsOpenNewFinAc = :FinIsOpenNewFinAc, FinDftStmtFrq = :FinDftStmtFrq,FinIsAlwMD = :FinIsAlwMD,");
		updateSql.append(" FinSchdMthd = :FinSchdMthd, FInIsAlwGrace = :FInIsAlwGrace, FinHistRetension = :FinHistRetension,");
		updateSql.append(" FinOrgPrfUnchanged = :FinOrgPrfUnchanged, FinFrEqrepayment = :FinFrEqrepayment,");
		updateSql.append(" FinRateType = :FinRateType, FinBaseRate = :FinBaseRate, FinSplRate = :FinSplRate,");
		updateSql.append(" FinIntRate = :FinIntRate,FInMinRate = :FInMinRate, FinMaxRate = :FinMaxRate, FinDftIntFrq = :FinDftIntFrq,");
		updateSql.append(" FinIsIntCpz = :FinIsIntCpz, FinCpzFrq = :FinCpzFrq, FinIsRvwAlw = :FinIsRvwAlw,FinRvwFrq = :FinRvwFrq, ");
		updateSql.append(" FinGrcRateType = :FinGrcRateType, FinGrcBaseRate = :FinGrcBaseRate,FinGrcSplRate = :FinGrcSplRate,");
		updateSql.append(" FinGrcIntRate = :FinGrcIntRate, FInGrcMinRate = :FInGrcMinRate, FinGrcMaxRate = :FinGrcMaxRate,");
		updateSql.append(" FinGrcDftIntFrq = :FinGrcDftIntFrq,  FinGrcIsIntCpz = :FinGrcIsIntCpz, FinGrcCpzFrq = :FinGrcCpzFrq,");
		updateSql.append(" FinGrcIsRvwAlw = :FinGrcIsRvwAlw, FinGrcRvwFrq = :FinGrcRvwFrq,FinMinTerm = :FinMinTerm,");
		updateSql.append(" FinMaxTerm = :FinMaxTerm, FinDftTerms = :FinDftTerms, FinRpyFrq = :FinRpyFrq,");
		updateSql.append(" FInRepayMethod = :FInRepayMethod, FinIsAlwPartialRpy = :FinIsAlwPartialRpy,");
		updateSql.append(" FinIsAlwDifferment = :FinIsAlwDifferment, FinMaxDifferment= :FinMaxDifferment, AlwPlanDeferment=:AlwPlanDeferment,");
		updateSql.append(" PlanDeferCount=:PlanDeferCount, FinIsAlwEarlyRpy = :FinIsAlwEarlyRpy, FinIsAlwEarlySettle = :FinIsAlwEarlySettle,");
		updateSql.append(" FinODRpyTries = :FinODRpyTries, FinLatePayRule = :FinLatePayRule,FinAEAddDsbOD = :FinAEAddDsbOD,");
		updateSql.append(" FinAEAddDsbFD = :FinAEAddDsbFD, FinAEAddDsbFDA = :FinAEAddDsbFDA, FinAEAmzNorm = :FinAEAmzNorm,");
		updateSql.append(" FinAEAmzSusp = :FinAEAmzSusp, FinAEToNoAmz = :FinAEToNoAmz, FinToAmz = :FinToAmz, FinAEMAmz = :FinAEMAmz,");
		updateSql.append(" FinAERateChg = :FinAERateChg,FinAERepay = :FinAERepay,FinAEEarlyPay = :FinAEEarlyPay, ");
		updateSql.append(" FinAEEarlySettle = :FinAEEarlySettle,FinAEWriteOff = :FinAEWriteOff, FinAEWriteOffBK=:FinAEWriteOffBK, FinAEGraceEnd=:FinAEGraceEnd, FinIsDwPayRequired = :FinIsDwPayRequired,");
		updateSql.append(" FinRvwRateApplFor = :FinRvwRateApplFor,FinIsIntCpzAtGrcEnd = :FinIsIntCpzAtGrcEnd,FinAlwRateChangeAnyDate = :FinAlwRateChangeAnyDate,  ");
		updateSql.append(" FinGrcRvwRateApplFor = :FinGrcRvwRateApplFor,FinGrcAlwRateChgAnyDate = :FinGrcAlwRateChgAnyDate,FinMinDownPayAmount = :FinMinDownPayAmount,");
		updateSql.append(" FinSchCalCodeOnRvw = :FinSchCalCodeOnRvw,FinAssetType=:FinAssetType,FinDepositRestrictedTo=:FinDepositRestrictedTo,");
		updateSql.append(" FinAEBuyOrInception=:FinAEBuyOrInception,FinAESellOrMaturity=:FinAESellOrMaturity,FinIsActive = :FinIsActive,");
		updateSql.append(" PftPayAcType=:PftPayAcType,FinIsOpenPftPayAcc=:FinIsOpenPftPayAcc,Version = :Version ,LastMntBy = :LastMntBy,LastMntOn = :LastMntOn,");
		updateSql.append(" RecordStatus= :RecordStatus,FinAEPlanDef=:FinAEPlanDef,FinDefRepay=:FinDefRepay, RoleCode = :RoleCode, NextRoleCode = :NextRoleCode, TaskId = :TaskId,");
		updateSql.append(" NextTaskId = :NextTaskId, RecordType = :RecordType, WorkflowId = :WorkflowId,FinGrcSchdMthd=:FinGrcSchdMthd, ");
		updateSql.append(" FinIsAlwGrcRepay=:FinIsAlwGrcRepay,FinScheduleOn=:FinScheduleOn,FinGrcScheduleOn=:FinGrcScheduleOn,");
		updateSql.append(" FinMargin=:FinMargin,FinGrcMargin=:FinGrcMargin,");
		updateSql.append(" FinCommitmentReq=:FinCommitmentReq ,FinCollateralReq=:FinCollateralReq ,FinDepreciationReq=:FinDepreciationReq,");
		updateSql.append(" FinDepreciationFrq=:FinDepreciationFrq ,FinDepreciationRule=:FinDepreciationRule ,");
		updateSql.append(" FinProvision=:FinProvision,FinSchdChange=:FinSchdChange,FinAECapitalize =:FinAECapitalize,  FinAEProgClaim =:FinAEProgClaim , FinAEMaturity =:FinAEMaturity ,");
		updateSql.append(" FinAlwIndRate=:FinAlwIndRate, FinIndBaseRate=:FinIndBaseRate, FinPftUnChanged=:FinPftUnChanged ,");
		updateSql.append(" FinGrcAlwIndRate=:FinGrcAlwIndRate, FinGrcIndBaseRate=:FinGrcIndBaseRate, ");
		updateSql.append(" AllowRIAInvestment =:AllowRIAInvestment , AllowParllelFinance =:AllowParllelFinance ,OverrideLimit=:OverrideLimit, ");
		updateSql.append(" LimitRequired=:LimitRequired ,FinCommitmentOvrride=:FinCommitmentOvrride ,FinCollateralOvrride=:FinCollateralOvrride ,FinInstDate=:FinInstDate, FinRepayPftOnFrq =:FinRepayPftOnFrq, ");
		updateSql.append(" ApplyODPenalty =:ApplyODPenalty , ODIncGrcDays =:ODIncGrcDays, ODChargeType=:ODChargeType , ODGraceDays=:ODGraceDays , " );
		updateSql.append(" ODChargeCalOn=:ODChargeCalOn , ODChargeAmtOrPerc=:ODChargeAmtOrPerc , ODAllowWaiver=:ODAllowWaiver , ODMaxWaiverPerc=:ODMaxWaiverPerc, FinDivision=:FinDivision, ");
		updateSql.append(" StepFinance=:StepFinance , SteppingMandatory=:SteppingMandatory , AlwManualSteps=:AlwManualSteps , AlwdStepPolicies=:AlwdStepPolicies , DftStepPolicy=:DftStepPolicy");

		updateSql.append(" Where FinType =:FinType");

		if (!type.endsWith("_TEMP")) {
			updateSql.append(" AND Version= :Version-1");
		}

		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(financeType);
		recordCount = this.namedParameterJdbcTemplate.update(updateSql.toString(), beanParameters);

		if (recordCount <= 0) {
			logger.debug("Error Update Method Count :" + recordCount);
			String[] valueParm = new String[2];
			String[] errParm = new String[2];
			valueParm[0] = financeType.getFinAcType();
			errParm[0] = PennantJavaUtil.getLabel("label_FinType") + ":" + valueParm[0];

			ErrorDetails errorDetails = ErrorUtil.getErrorDetail(new ErrorDetails(PennantConstants.KEY_FIELD, "41004", errParm, valueParm), financeType.getUserDetails()
			        .getUsrLanguage());
			throw new DataAccessException(errorDetails.getError()) {
			};
		}
		logger.debug("Leaving");
	}

	@Override
    public boolean checkRIAFinance(String finType) {
		logger.debug("Entering");
		FinanceType financeType = new FinanceType();
		financeType.setId(finType);

		StringBuilder selectSql = new StringBuilder("SELECT AllowRIAInvestment ");
		selectSql.append(" FROM RMTFinanceTypes");
		selectSql.append(" Where FinType = :FinType");

		logger.debug("selectListSql: " + selectSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(financeType);

		boolean isAllowRIAFinacne = false;
		try {
			isAllowRIAFinacne = this.namedParameterJdbcTemplate.queryForObject(selectSql.toString(), beanParameters, Boolean.class);
		} catch (EmptyResultDataAccessException e) {
			isAllowRIAFinacne = false;
		}
		logger.debug("Leaving");
		return isAllowRIAFinacne;
    }

}