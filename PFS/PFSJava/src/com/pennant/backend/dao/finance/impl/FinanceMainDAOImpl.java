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
import java.util.Date;
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
import org.springframework.jdbc.core.namedparam.SqlParameterSourceUtils;
import org.springframework.jdbc.core.simple.ParameterizedBeanPropertyRowMapper;

import com.pennant.app.model.CustomerCalData;
import com.pennant.app.util.ErrorUtil;
import com.pennant.backend.dao.finance.FinanceMainDAO;
import com.pennant.backend.dao.impl.BasisCodeDAO;
import com.pennant.backend.model.ErrorDetails;
import com.pennant.backend.model.WorkFlowDetails;
import com.pennant.backend.model.finance.FinanceEnquiry;
import com.pennant.backend.model.finance.FinanceMain;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantJavaUtil;
import com.pennant.backend.util.WorkFlowUtil;

/**
 * DAO methods implementation for the <b>FinanceMain model</b> class.<br>
 * 
 */

public class FinanceMainDAOImpl extends BasisCodeDAO<FinanceMain> implements FinanceMainDAO {

	private static Logger logger = Logger.getLogger(FinanceMainDAOImpl.class);

	// Spring Named JDBC Template
	private NamedParameterJdbcTemplate namedParameterJdbcTemplate;

	/**
	 * This method set the Work Flow id based on the module name and return the new FinanceMain
	 * 
	 * @return FinanceMain
	 */

	@Override
	public FinanceMain getFinanceMain(boolean isWIF) {
		logger.debug("Entering");

		String wifName = "";

		if (isWIF) {
			wifName = "WIFFinanceMain";
		} else {
			wifName = "FinanceMain";
		}

		WorkFlowDetails workFlowDetails = WorkFlowUtil.getWorkFlowDetails(wifName);
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
	 * Fetch the Record Finance Main Detail details by key field
	 * 
	 * @param id
	 *            (String)
	 * @param type
	 *            (String) ""/_Temp/_View
	 * @return FinanceMain
	 */
	@Override
	public FinanceMain getFinanceMainById(final String id, String type, boolean isWIF) {
		logger.debug("Entering");
		FinanceMain financeMain = getFinanceMain(isWIF);

		financeMain.setId(id);

		StringBuilder selectSql = new StringBuilder(
		        "SELECT FinReference, NumberOfTerms, GrcPeriodEndDate, AllowGrcPeriod,");
		selectSql
		        .append(" GraceBaseRate, GraceSpecialRate, GrcPftRate, GrcPftFrq,NextGrcPftDate, AllowGrcPftRvw,");
		selectSql
		        .append(" GrcPftRvwFrq, NextGrcPftRvwDate, AllowGrcCpz, GrcCpzFrq, NextGrcCpzDate,RepayBaseRate,");
		selectSql
		        .append(" RepaySpecialRate, RepayProfitRate, RepayFrq, NextRepayDate, RepayPftFrq, NextRepayPftDate,");
		selectSql
		        .append(" AllowRepayRvw,RepayRvwFrq, NextRepayRvwDate, AllowRepayCpz, RepayCpzFrq, NextRepayCpzDate,");
		selectSql
		        .append(" MaturityDate, CpzAtGraceEnd,DownPayment, GraceFlatAmount, ReqRepayAmount, TotalProfit,");
		selectSql
		        .append(" TotalCpz,TotalGrossPft,TotalGracePft,TotalGraceCpz,TotalGrossGrcPft, TotalRepayAmt,");
		selectSql.append(" GrcRateBasis, RepayRateBasis, FinType,FinRemarks, FinCcy, ScheduleMethod,");
		selectSql
		        .append(" ProfitDaysBasis, ReqMaturity, CalTerms, CalMaturity, FirstRepay, LastRepay,");
		selectSql
		        .append(" FinStartDate, FinAmount, FinRepaymentAmount, CustID, Defferments, FrqDefferments, ");
		selectSql
		        .append(" FinBranch, FinSourceID, AllowedDefRpyChange, AvailedDefRpyChange, AllowedDefFrqChange,");
		selectSql
		        .append(" AvailedDefFrqChange, RecalType, FinAssetValue, disbAccountId, repayAccountId, FinIsActive, ");
		selectSql
		        .append(" LastRepayDate, LastRepayPftDate,LastRepayRvwDate, LastRepayCpzDate, AllowGrcRepay, GrcSchdMthd,");
		selectSql
		        .append(" GrcMargin, RepayMargin, FinCommitmentRef, DepreciationFrq, FinCurrAssetValue,FinContractDate,");
		selectSql.append(" NextDepDate, LastDepDate, FinAccount, FinCustPftAccount,");
		selectSql.append(" NextDepDate, LastDepDate, FinAccount, FinCustPftAccount,");
		selectSql.append(" AlwIndRate, IndBaseRate, GrcAlwIndRate, GrcIndBaseRate, ClosingStatus, FinApprovedDate, ");
		selectSql.append(" AnualizedPercRate , EffectiveRateOfReturn , FinRepayPftOnFrq , ");

		if (StringUtils.trimToEmpty(type).contains("View")) {
			selectSql
			        .append(" lovDescFinCcyName,lovDescScheduleMethodName, lovDescProfitDaysBasisName,");
			selectSql
			        .append(" lovDescGraceBaseRateName,lovDescGraceSpecialRateName,lovDescFinTypeName,");
			selectSql
			        .append(" lovDescRepayBaseRateName,lovDescRepaySpecialRateName,lovDescFinFormatter,");
			selectSql.append(" lovDescFinIsAlwMD, lovDescFinMaxAmt,lovDescFinMinAmount,");
			selectSql
			        .append(" lovDescMinDwnPayPercent,lovDescFinAlwDeferment, lovDescDwnPayReq, lovDescFinMaxDifferment,");
			selectSql
			        .append(" lovDescAssetCodeName,lovDescFinLatePayRule,lovDescFinAEEarlyPay,lovDescFinAEEarlySettle,");
			selectSql.append(" lovDescIndBaseRateName, lovDescGrcIndBaseRateName,");
			selectSql.append(" FinRvwRateApplFor, FinGrcRvwRateApplFor,");
			if (!isWIF) {
				selectSql
				        .append(" lovDescCustCIF, lovDescCustFName, lovDescCustLName, lovDescFinBranchName,");
				selectSql
				        .append(" lovDescSalutationName, lovDescCustShrtName, lovDescCustAddrLine1, lovDescCustAddrLine2,");
				selectSql
				        .append(" lovDescCustAddrCity, lovDescCustAddrCountry, lovDescCustCtgTypeName, lovDescAccruedTillLBD, lovDescCommitmentRefName,");
			}			
		}
		selectSql.append(" Version , LastMntBy, LastMntOn, RecordStatus, RoleCode, NextRoleCode, TaskId, ");
		selectSql.append(" NextTaskId, RecordType, WorkflowId");

		if (isWIF) {
			selectSql.append(" From WIFFinanceMain");
		} else {
			selectSql.append(" ,MigratedFinance, ScheduleMaintained, ScheduleRegenerated ");
			selectSql.append(" ,Blacklisted, FeeChargeAmt, LimitValid, OverrideLimit ");
			selectSql.append(" From FinanceMain");
		}
		selectSql.append(StringUtils.trimToEmpty(type));
		selectSql.append(" Where FinReference =:FinReference");

		logger.debug("selectSql: " + selectSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(financeMain);
		RowMapper<FinanceMain> typeRowMapper = ParameterizedBeanPropertyRowMapper
		        .newInstance(FinanceMain.class);

		try {
			financeMain = this.namedParameterJdbcTemplate.queryForObject(selectSql.toString(),
			        beanParameters, typeRowMapper);
		} catch (EmptyResultDataAccessException e) {
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
	 * @param type
	 *            (String) ""/_Temp/_View
	 * @return FinanceMain
	 */
	@Override
	public FinanceMain getFinanceMainForDataSet(final String finReference) {
		logger.debug("Entering");
		
		FinanceMain financeMain = getFinanceMain(false);
		financeMain.setId(finReference);
		
		StringBuilder selectSql = new StringBuilder("select  FinReference, GrcPeriodEndDate, FinRepaymentAmount," );
		selectSql.append(" DisbAccountid, RepayAccountid, FinAccount, FinCustPftAccount, " );
		selectSql.append(" FinCcy, FinBranch, CustId, FinAmount, DownPayment, FinType, " );
		selectSql.append(" FinStartDate, NumberOfTerms, NextGrcPftDate, nextRepayDate, LastRepayPftDate, NextRepayPftDate, " );
		selectSql.append(" LastRepayRvwDate, NextRepayRvwDate, FinAssetValue, FinCurrAssetValue," );
		selectSql.append(" RecordType, Version " );
		selectSql.append(" FROM Financemain");
		selectSql.append(" Where FinReference =:FinReference");
		
		logger.debug("selectSql: " + selectSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(financeMain);
		RowMapper<FinanceMain> typeRowMapper = ParameterizedBeanPropertyRowMapper.newInstance(FinanceMain.class);
		
		try {
			financeMain = this.namedParameterJdbcTemplate.queryForObject(selectSql.toString(), beanParameters, typeRowMapper);
		} catch (EmptyResultDataAccessException e) {
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
		return this.namedParameterJdbcTemplate.queryForList(selectSql.toString(), beanParameters,
		        String.class);

	}

	/**
	 * This method initialise the Record.
	 * 
	 * @param FinanceMain
	 *            (financeMain)
	 * @return FinanceMain
	 */
	@Override
	public void initialize(FinanceMain financeMain) {
		super.initialize(financeMain);
	}

	/**
	 * This method refresh the Record.
	 * 
	 * @param FinanceMain
	 *            (financeMain)
	 * @return void
	 */
	@Override
	public void refresh(FinanceMain financeMain) {

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
	 * This method Deletes the Record from the FinanceMain or FinanceMain_Temp. if Record not deleted then throws
	 * DataAccessException with error 41003. delete Finance Main Detail by key FinReference
	 * 
	 * @param Finance
	 *            Main Detail (financeMain)
	 * @param type
	 *            (String) ""/_Temp/_View
	 * @return void
	 * @throws DataAccessException
	 * 
	 */
	@SuppressWarnings("serial")
	public void delete(FinanceMain financeMain, String type, boolean isWIF) {
		logger.debug("Entering");
		int recordCount = 0;

		StringBuilder deleteSql = new StringBuilder("Delete From ");
		if (isWIF) {
			deleteSql.append(" WIFFinanceMain");
		} else {
			deleteSql.append(" FinanceMain");
		}
		deleteSql.append(StringUtils.trimToEmpty(type));
		deleteSql.append(" Where FinReference =:FinReference");
		logger.debug("deleteSql: " + deleteSql.toString());

		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(financeMain);
		try {
			recordCount = this.namedParameterJdbcTemplate.update(deleteSql.toString(),
			        beanParameters);
			if (recordCount <= 0) {
				ErrorDetails errorDetails = getError("41003", financeMain.getId(), financeMain
				        .getUserDetails().getUsrLanguage());
				throw new DataAccessException(errorDetails.getError()) {
				};
			}
		} catch (DataAccessException e) {
			logger.error(e);
			ErrorDetails errorDetails = getError("41006", financeMain.getId(), financeMain
			        .getUserDetails().getUsrLanguage());
			throw new DataAccessException(errorDetails.getError()) {
			};
		}
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

	@Override
	public String save(FinanceMain financeMain, String type, boolean isWIF) {
		logger.debug("Entering");

		StringBuilder insertSql = new StringBuilder("Insert Into ");
		if (isWIF) {
			insertSql.append(" WIFFinanceMain");
		} else {
			insertSql.append(" FinanceMain");
		}
		insertSql.append(StringUtils.trimToEmpty(type));
		insertSql.append(" (FinReference, NumberOfTerms, GrcPeriodEndDate, AllowGrcPeriod,");
		insertSql
		        .append(" GraceBaseRate, GraceSpecialRate, GrcPftRate, GrcPftFrq,NextGrcPftDate, AllowGrcPftRvw,");
		insertSql
		        .append(" GrcPftRvwFrq, NextGrcPftRvwDate, AllowGrcCpz, GrcCpzFrq, NextGrcCpzDate,RepayBaseRate,");
		insertSql
		        .append(" RepaySpecialRate, RepayProfitRate, RepayFrq, NextRepayDate, RepayPftFrq, NextRepayPftDate,");
		insertSql
		        .append(" AllowRepayRvw,RepayRvwFrq, NextRepayRvwDate, AllowRepayCpz, RepayCpzFrq, NextRepayCpzDate,");
		insertSql
		        .append(" MaturityDate, CpzAtGraceEnd,DownPayment, GraceFlatAmount, ReqRepayAmount, TotalProfit,");
		insertSql
		        .append(" TotalCpz,TotalGrossPft,TotalGracePft, TotalGraceCpz,TotalGrossGrcPft, TotalRepayAmt,");
		insertSql.append("  GrcRateBasis, RepayRateBasis,FinType,FinRemarks, FinCcy, ScheduleMethod,FinContractDate,");
		insertSql
		        .append(" ProfitDaysBasis, ReqMaturity, CalTerms, CalMaturity, FirstRepay, LastRepay,");
		insertSql
		        .append(" FinStartDate, FinAmount, FinRepaymentAmount, CustID, Defferments,FrqDefferments,");
		insertSql
		        .append(" FinBranch, FinSourceID, AllowedDefRpyChange, AvailedDefRpyChange, AllowedDefFrqChange,");
		insertSql
		        .append(" AvailedDefFrqChange, RecalType, FinIsActive,FinAssetValue, disbAccountId, repayAccountId, ");
		insertSql
		        .append(" LastRepayDate, LastRepayPftDate, LastRepayRvwDate, LastRepayCpzDate, AllowGrcRepay, GrcSchdMthd,");
		insertSql
		        .append(" GrcMargin, RepayMargin, FinCommitmentRef, DepreciationFrq, FinCurrAssetValue,");
		insertSql.append(" NextDepDate, LastDepDate, FinAccount, FinCustPftAccount, ClosingStatus, FinApprovedDate, ");
		insertSql
		        .append(" AlwIndRate, IndBaseRate, GrcAlwIndRate, GrcIndBaseRate,DedupFound,SkipDedup,Blacklisted,");
		insertSql.append(" AnualizedPercRate , EffectiveRateOfReturn , FinRepayPftOnFrq ,");
		if (!isWIF) {
			insertSql.append(" MigratedFinance, ScheduleMaintained, ScheduleRegenerated,");
			insertSql.append(" FeeChargeAmt,LimitValid, OverrideLimit,");
		}
		
		insertSql
		        .append(" Version , LastMntBy, LastMntOn, RecordStatus, RoleCode, NextRoleCode, TaskId,");
		insertSql.append(" NextTaskId, RecordType, WorkflowId)");
		insertSql
		        .append(" Values(:FinReference, :NumberOfTerms, :GrcPeriodEndDate, :AllowGrcPeriod,");
		insertSql
		        .append(" :GraceBaseRate, :GraceSpecialRate,:GrcPftRate,:GrcPftFrq,:NextGrcPftDate,:AllowGrcPftRvw,");
		insertSql
		        .append(" :GrcPftRvwFrq,:NextGrcPftRvwDate,:AllowGrcCpz,:GrcCpzFrq,:NextGrcCpzDate,:RepayBaseRate,");
		insertSql
		        .append(" :RepaySpecialRate,:RepayProfitRate,:RepayFrq,:NextRepayDate,:RepayPftFrq,:NextRepayPftDate,");
		insertSql
		        .append(" :AllowRepayRvw,:RepayRvwFrq,:NextRepayRvwDate,:AllowRepayCpz,:RepayCpzFrq,:NextRepayCpzDate,");
		insertSql
		        .append(" :MaturityDate,:CpzAtGraceEnd,:DownPayment,:GraceFlatAmount,:ReqRepayAmount,:TotalProfit,");
		insertSql
		        .append(" :TotalCpz,:TotalGrossPft,:TotalGracePft,:TotalGraceCpz,:TotalGrossGrcPft, :TotalRepayAmt,");
		insertSql.append(" :GrcRateBasis,:RepayRateBasis, :FinType,:FinRemarks,:FinCcy,:ScheduleMethod,:FinContractDate,");
		insertSql
		        .append(" :ProfitDaysBasis,:ReqMaturity,:CalTerms,:CalMaturity,:FirstRepay,:LastRepay,");
		insertSql
		        .append(" :FinStartDate,:FinAmount,:FinRepaymentAmount,:CustID,:Defferments,:FrqDefferments,");
		insertSql
		        .append(" :FinBranch, :FinSourceID, :AllowedDefRpyChange, :AvailedDefRpyChange, :AllowedDefFrqChange,");
		insertSql
		        .append(" :AvailedDefFrqChange, :RecalType, :FinIsActive,:FinAssetValue, :disbAccountId, :repayAccountId, ");
		insertSql
		        .append(" :LastRepayDate, :LastRepayPftDate, :LastRepayRvwDate, :LastRepayCpzDate,:AllowGrcRepay, :GrcSchdMthd,");
		insertSql
		        .append(" :GrcMargin, :RepayMargin, :FinCommitmentRef, :DepreciationFrq, :FinCurrAssetValue,");
		insertSql.append(" :NextDepDate, :LastDepDate, :FinAccount, :FinCustPftAccount, :ClosingStatus , :FinApprovedDate, ");
		insertSql
		        .append(" :AlwIndRate, :IndBaseRate, :GrcAlwIndRate, :GrcIndBaseRate, :DedupFound,:SkipDedup,:Blacklisted,");
		insertSql.append(" :AnualizedPercRate , :EffectiveRateOfReturn , :FinRepayPftOnFrq ,");
		if (!isWIF) {
			insertSql.append(" :MigratedFinance, :ScheduleMaintained, :ScheduleRegenerated,");
			insertSql.append(" :FeeChargeAmt, :LimitValid, :OverrideLimit,");
		}
		insertSql
		        .append(" :Version ,:LastMntBy,:LastMntOn,:RecordStatus,:RoleCode,:NextRoleCode,:TaskId,");
		insertSql.append(" :NextTaskId,:RecordType,:WorkflowId)");
		logger.debug("insertSql: " + insertSql.toString());

		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(financeMain);
		this.namedParameterJdbcTemplate.update(insertSql.toString(), beanParameters);
		logger.debug("Leaving");
		return financeMain.getId();
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

	@SuppressWarnings("serial")
	@Override
	public void update(FinanceMain financeMain, String type, boolean isWIF) {
		int recordCount = 0;
		logger.debug("Entering");
		StringBuilder updateSql = new StringBuilder("Update ");
		if (isWIF) {
			updateSql.append(" WIFFinanceMain");
		} else {
			updateSql.append(" FinanceMain");
		}
		updateSql.append(StringUtils.trimToEmpty(type));
		updateSql.append(" Set  NumberOfTerms = :NumberOfTerms,");
		updateSql
		        .append(" GrcPeriodEndDate = :GrcPeriodEndDate, AllowGrcPeriod = :AllowGrcPeriod,");
		updateSql.append(" GraceBaseRate = :GraceBaseRate, GraceSpecialRate = :GraceSpecialRate,");
		updateSql.append(" GrcPftRate = :GrcPftRate, GrcPftFrq = :GrcPftFrq,");
		updateSql.append(" NextGrcPftDate = :NextGrcPftDate, AllowGrcPftRvw = :AllowGrcPftRvw,");
		updateSql.append(" GrcPftRvwFrq = :GrcPftRvwFrq, NextGrcPftRvwDate = :NextGrcPftRvwDate,");
		updateSql
		        .append(" AllowGrcCpz = :AllowGrcCpz, GrcCpzFrq = :GrcCpzFrq, NextGrcCpzDate = :NextGrcCpzDate,");
		updateSql.append(" RepayBaseRate = :RepayBaseRate, RepaySpecialRate = :RepaySpecialRate,");
		updateSql
		        .append(" RepayProfitRate = :RepayProfitRate, RepayFrq = :RepayFrq, NextRepayDate = :NextRepayDate,");
		updateSql.append(" RepayPftFrq = :RepayPftFrq, NextRepayPftDate = :NextRepayPftDate,");
		updateSql.append(" AllowRepayRvw = :AllowRepayRvw, RepayRvwFrq = :RepayRvwFrq,");
		updateSql.append(" NextRepayRvwDate = :NextRepayRvwDate, AllowRepayCpz = :AllowRepayCpz,");
		updateSql.append(" RepayCpzFrq = :RepayCpzFrq, NextRepayCpzDate = :NextRepayCpzDate,");
		updateSql
		        .append(" MaturityDate = :MaturityDate, CpzAtGraceEnd = :CpzAtGraceEnd, DownPayment = :DownPayment,");
		updateSql.append(" GraceFlatAmount = :GraceFlatAmount, ReqRepayAmount = :ReqRepayAmount,");
		updateSql
		        .append(" TotalProfit = :TotalProfit,TotalCpz= :TotalCpz, TotalGrossPft = :TotalGrossPft,");
		updateSql
		        .append(" TotalGracePft= :TotalGracePft,TotalGraceCpz= :TotalGraceCpz,TotalGrossGrcPft= :TotalGrossGrcPft,");
		updateSql
		        .append(" TotalRepayAmt= :TotalRepayAmt, GrcRateBasis = :GrcRateBasis, RepayRateBasis = :RepayRateBasis, FinType = :FinType,");
		updateSql
		        .append(" FinRemarks = :FinRemarks, FinCcy = :FinCcy, ScheduleMethod = :ScheduleMethod,");
		updateSql
		        .append(" ProfitDaysBasis = :ProfitDaysBasis, ReqMaturity = :ReqMaturity, CalTerms = :CalTerms,");
		updateSql
		        .append(" CalMaturity = :CalMaturity, FirstRepay = :FirstRepay, LastRepay = :LastRepay,");
		updateSql
		        .append(" FinStartDate = :FinStartDate, FinAmount = :FinAmount,FinContractDate= :FinContractDate,");
		updateSql
		        .append(" FinRepaymentAmount = :FinRepaymentAmount, CustID = :CustID,Defferments = :Defferments,");
		updateSql
		        .append(" FrqDefferments= :FrqDefferments, FinBranch =:FinBranch, FinSourceID= :FinSourceID,");
		updateSql
		        .append(" AllowedDefRpyChange= :AllowedDefRpyChange, AvailedDefRpyChange= :AvailedDefRpyChange,");
		updateSql
		        .append(" AllowedDefFrqChange= :AllowedDefFrqChange, AvailedDefFrqChange= :AvailedDefFrqChange,");
		updateSql
		        .append(" RecalType=:RecalType, FinIsActive= :FinIsActive,FinAssetValue= :FinAssetValue,");
		updateSql.append(" disbAccountId= :disbAccountId, repayAccountId= :repayAccountId,");
		updateSql.append(" LastRepayDate= :LastRepayDate,LastRepayPftDate= :LastRepayPftDate,");
		updateSql
		        .append(" LastRepayRvwDate= :LastRepayRvwDate, LastRepayCpzDate= :LastRepayCpzDate,AllowGrcRepay= :AllowGrcRepay,");
		updateSql
		        .append(" GrcSchdMthd= :GrcSchdMthd, GrcMargin= :GrcMargin,RepayMargin= :RepayMargin,");
		updateSql
		        .append(" FinCommitmentRef= :FinCommitmentRef, DepreciationFrq= :DepreciationFrq, FinCurrAssetValue= :FinCurrAssetValue,");
		updateSql
		        .append(" NextDepDate= :NextDepDate, LastDepDate= :LastDepDate,FinAccount=:FinAccount,");
		updateSql
		        .append(" AlwIndRate = :AlwIndRate, IndBaseRate = :IndBaseRate, GrcAlwIndRate = :GrcAlwIndRate,");
		updateSql
		        .append(" GrcIndBaseRate = :GrcIndBaseRate, FinCustPftAccount=:FinCustPftAccount,ClosingStatus= :ClosingStatus, ");
		updateSql
        		.append(" FinApprovedDate= :FinApprovedDate,"); 
		updateSql.append(" AnualizedPercRate =:AnualizedPercRate , EffectiveRateOfReturn =:EffectiveRateOfReturn , FinRepayPftOnFrq =:FinRepayPftOnFrq ,");
		
		if (!isWIF) {
			updateSql.append(" MigratedFinance = :MigratedFinance,ScheduleMaintained = :ScheduleMaintained, ScheduleRegenerated = :ScheduleRegenerated,");
			updateSql.append(" FeeChargeAmt=:FeeChargeAmt, LimitValid= :LimitValid, OverrideLimit= :OverrideLimit,");
		}
		
		updateSql.append(" Version = :Version,LastMntBy = :LastMntBy, LastMntOn = :LastMntOn,");
		updateSql
		        .append(" RecordStatus= :RecordStatus, RoleCode = :RoleCode, NextRoleCode = :NextRoleCode,");
		updateSql
		        .append(" TaskId = :TaskId, NextTaskId = :NextTaskId, RecordType = :RecordType, WorkflowId = :WorkflowId");
		updateSql.append(" Where FinReference =:FinReference");
		if (!type.endsWith("_TEMP") && !isWIF) {
			updateSql.append("  AND Version= :Version-1");
		}

		logger.debug("updateSql: " + updateSql.toString());

		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(financeMain);
		recordCount = this.namedParameterJdbcTemplate.update(updateSql.toString(), beanParameters);

		if (recordCount <= 0) {
			logger.debug("Error Update Method Count :" + recordCount);
			ErrorDetails errorDetails = getError("41004", financeMain.getId(), financeMain
			        .getUserDetails().getUsrLanguage());
			throw new DataAccessException(errorDetails.getError()) {
			};
		}
		logger.debug("Leaving");
	}

	private ErrorDetails getError(String errorId, String FinReference, String userLanguage) {
		String[][] parms = new String[2][1];
		parms[1][0] = FinReference;
		parms[0][0] = PennantJavaUtil.getLabel("label_FinReference") + ":" + parms[1][0];
		return ErrorUtil.getErrorDetail(new ErrorDetails(PennantConstants.KEY_FIELD, errorId,
		        parms[0], parms[1]), userLanguage);
	}

	/**
	 * Method to return the customer details based on given customer id.
	 */
	public boolean isFinReferenceExists(final String id, String type, boolean isWIF) {
		logger.debug("Entering");

		FinanceMain financeMain = getFinanceMain(isWIF);
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
		RowMapper<FinanceMain> typeRowMapper = ParameterizedBeanPropertyRowMapper
		        .newInstance(FinanceMain.class);

		try {
			financeMain = this.namedParameterJdbcTemplate.queryForObject(selectSql.toString(),
			        beanParameters, typeRowMapper);
		} catch (EmptyResultDataAccessException e) {
			financeMain = null;
		}
		logger.debug("Leaving");
		return financeMain == null? false:true;
	}

	@Override
	/**
	 * 
	 *  <br> IN FinanceMainDAOImpl.java
	 * @param id
	 * @param type
	 *  @return  FinanceMain 
	 */
	public CustomerCalData calculateData(CustomerCalData calData, String type) {
		logger.debug("Entering calculateData()");
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(calData);
		RowMapper<CustomerCalData> typeRowMapper = ParameterizedBeanPropertyRowMapper
		        .newInstance(CustomerCalData.class);
		CustomerCalData custData = new CustomerCalData();
		try {
			String reqCust = "SELECT count(FinReference) as custLiveFinCount, Sum(FinAmount) as custLiveFinAmount "
			        + "From FinanceMain Where CustID =:CustID and  MaturityDate > getdate()";
			custData = this.namedParameterJdbcTemplate.queryForObject(reqCust, beanParameters,
			        typeRowMapper);
			calData.setCustLiveFinCount(custData.getCustLiveFinCount());
			calData.setCustLiveFinAmount(custData.getCustLiveFinAmount());
		} catch (EmptyResultDataAccessException e) {
			custData = null;
		}
		try {
			String reqFtp = "SELECT count(FinReference) as custReqFtpCount, Sum(FinAmount) as custReqFtpAmount "
			        + "From FinanceMain Where FinType =:FinType and  MaturityDate > getdate()";
			custData = this.namedParameterJdbcTemplate.queryForObject(reqFtp, beanParameters,
			        typeRowMapper);
			calData.setCustReqFtpCount(custData.getCustReqFtpCount());
			calData.setCustReqFtpAmount(custData.getCustReqFtpAmount());
		} catch (EmptyResultDataAccessException e) {
			custData = null;
		}
		try {
			String rpyBank = "SELECT Sum(FinRepaymentAmount) as custRepayBank From FinanceMain Where  CustID = :CustID";
			custData = this.namedParameterJdbcTemplate.queryForObject(rpyBank, beanParameters,
			        typeRowMapper);
			if (custData.getCustRepayBank() != null) {
				calData.setCustRepayBank(custData.getCustRepayBank());
			} else {
				calData.setCustRepayBank(new BigDecimal(0));
			}
			calData.setCustRepayOther(new BigDecimal(0));
		} catch (EmptyResultDataAccessException e) {
			custData = null;
		}
		try {
			String custpastDue = "SELECT Count(t1.FinReference) as custPastDueCount ,sum(FinCurODAmt) as custPastDueAmt "
			        + "FROM finoddetails as t1 inner join financemain as t2  on t1.FinReference=t2.FinReference "
			        + "where t1.CustID =:CustID and  t2.MaturityDate > getdate()";
			custData = this.namedParameterJdbcTemplate.queryForObject(custpastDue, beanParameters,
			        typeRowMapper);
			calData.setCustPastDueCount(custData.getCustPastDueCount());
			calData.setCustPastDueAmt(custData.getCustPastDueAmt());
		} catch (EmptyResultDataAccessException e) {
			custData = null;
		}

		try {
			calData.setCustPDHist30D(getMaturedOD(calData, "<=30"));
			calData.setCustPDHist60D(getMaturedOD(calData, "<=60"));
			calData.setCustPDHist90D(getMaturedOD(calData, "<=90"));
			calData.setCustPDHist120D(getMaturedOD(calData, "<=120"));
			calData.setCustPDHist180D(getMaturedOD(calData, "<=180"));
			calData.setCustPDHist180DP(getMaturedOD(calData, ">180"));
		} catch (EmptyResultDataAccessException e) {
			logger.debug(e);
		}
		try {
			calData.setCustPDLive30D(getLiveOD(calData, "<=30"));
			calData.setCustPDLive60D(getLiveOD(calData, "<=60"));
			calData.setCustPDLive90D(getLiveOD(calData, "<=90"));
			calData.setCustPDLive120D(getLiveOD(calData, "<=120"));
			calData.setCustPDLive180D(getLiveOD(calData, "<=180"));
			calData.setCustPDLive180DP(getLiveOD(calData, ">180"));
		} catch (EmptyResultDataAccessException e) {
			logger.debug(e);
		}
		logger.debug("Leaving calculateData()");
		return calData;
	}

	public BigDecimal getMaturedOD(CustomerCalData calData, String days) {
		logger.debug("Entering getMaturedOD()");
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(calData);
		StringBuilder selectsql = new StringBuilder(
		        "SELECT sum(FinMaxODAmt) FROM FinODDetails as t1 ");
		selectsql
		        .append("inner join FinanceMain as t2  on t1.FinReference=t2.FinReference where t1.CustID =:CustID  and  t2.MaturityDate ");
		selectsql.append("< getdate() and t1.FinCurODDays " + days);
		logger.debug("select sql:" + selectsql);
		logger.debug("Leaving getMaturedOD()");
		return this.namedParameterJdbcTemplate.queryForObject(selectsql.toString(), beanParameters,
		        BigDecimal.class);
	}

	public BigDecimal getLiveOD(CustomerCalData calData, String days) {
		logger.debug("Entering getLiveOD()");
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(calData);
		StringBuilder selectsql = new StringBuilder(
		        "SELECT sum(FinCurODAmt) FROM FinODDetails as t1 ");
		selectsql
		        .append("inner join FinanceMain as t2  on t1.FinReference=t2.FinReference where t1.CustID =:CustID  and  t2.MaturityDate ");
		selectsql.append("> getdate() and t1.FinCurODDays " + days);
		logger.debug("select sql:" + selectsql);
		logger.debug("Leaving getLiveOD()");
		return this.namedParameterJdbcTemplate.queryForObject(selectsql.toString(), beanParameters,
		        BigDecimal.class);
	}

	@Override
	public void listUpdate(ArrayList<FinanceMain> financeMain, String type) {
		logger.debug("Entering");
		StringBuilder updateSql = new StringBuilder("Update FinanceMain");
		updateSql.append(StringUtils.trimToEmpty(type));
		updateSql.append(" Set FinReference = :FinReference, NumberOfTerms = :NumberOfTerms,");
		updateSql
		        .append(" GrcPeriodEndDate = :GrcPeriodEndDate, AllowGrcPeriod = :AllowGrcPeriod,");
		updateSql.append(" GraceBaseRate = :GraceBaseRate, GraceSpecialRate = :GraceSpecialRate,");
		updateSql.append(" GrcPftRate = :GrcPftRate, GrcPftFrq = :GrcPftFrq,");
		updateSql.append(" NextGrcPftDate = :NextGrcPftDate, AllowGrcPftRvw = :AllowGrcPftRvw,");
		updateSql.append(" GrcPftRvwFrq = :GrcPftRvwFrq, NextGrcPftRvwDate = :NextGrcPftRvwDate,");
		updateSql
		        .append(" AllowGrcCpz = :AllowGrcCpz, GrcCpzFrq = :GrcCpzFrq, NextGrcCpzDate = :NextGrcCpzDate,");
		updateSql.append(" RepayBaseRate = :RepayBaseRate, RepaySpecialRate = :RepaySpecialRate,");
		updateSql
		        .append(" RepayProfitRate = :RepayProfitRate, RepayFrq = :RepayFrq, NextRepayDate = :NextRepayDate,");
		updateSql.append(" RepayPftFrq = :RepayPftFrq, NextRepayPftDate = :NextRepayPftDate,");
		updateSql.append(" AllowRepayRvw = :AllowRepayRvw, RepayRvwFrq = :RepayRvwFrq,");
		updateSql.append(" NextRepayRvwDate = :NextRepayRvwDate, AllowRepayCpz = :AllowRepayCpz,");
		updateSql.append(" RepayCpzFrq = :RepayCpzFrq, NextRepayCpzDate = :NextRepayCpzDate,");
		updateSql
		        .append(" MaturityDate = :MaturityDate, CpzAtGraceEnd = :CpzAtGraceEnd, DownPayment = :DownPayment,");
		updateSql.append(" GraceFlatAmount = :GraceFlatAmount, ReqRepayAmount = :ReqRepayAmount,");
		updateSql
		        .append(" TotalProfit = :TotalProfit,TotalCpz= :TotalCpz, TotalGrossPft = :TotalGrossPft,");
		updateSql
		        .append(" TotalGracePft= :TotalGracePft,TotalGraceCpz= :TotalGraceCpz,TotalGrossGrcPft= :TotalGrossGrcPft,");
		updateSql
		        .append(" TotalRepayAmt= :TotalRepayAmt, GrcRateBasis = :GrcRateBasis, RepayRateBasis = :RepayRateBasis, FinType = :FinType,");
		updateSql
		        .append(" FinRemarks = :FinRemarks, FinCcy = :FinCcy, ScheduleMethod = :ScheduleMethod,");
		updateSql
		        .append(" ProfitDaysBasis = :ProfitDaysBasis, ReqMaturity = :ReqMaturity, CalTerms = :CalTerms,");
		updateSql
		        .append(" CalMaturity = :CalMaturity, FirstRepay = :FirstRepay, LastRepay = :LastRepay,");
		updateSql
		        .append(" FinStartDate = :FinStartDate, FinAmount = :FinAmount,FinContractDate=:FinContractDate,");
		updateSql
		        .append(" FinRepaymentAmount = :FinRepaymentAmount, CustID = :CustID,Defferments = :Defferments, ");
		updateSql
		        .append(" FrqDefferments= :FrqDefferments, FinBranch =:FinBranch, FinSourceID= :FinSourceID,");
		updateSql
		        .append(" AllowedDefRpyChange= :AllowedDefRpyChange, AvailedDefRpyChange= :AvailedDefRpyChange,");
		updateSql
		        .append(" AllowedDefFrqChange= :AllowedDefFrqChange, AvailedDefFrqChange= :AvailedDefFrqChange,");
		updateSql
		        .append(" RecalType=:RecalType, FinIsActive= :FinIsActive,FinAssetValue= :FinAssetValue,");
		updateSql.append(" disbAccountId= :disbAccountId, repayAccountId= :repayAccountId,");
		updateSql.append(" LastRepayDate= :LastRepayDate,LastRepayPftDate= :LastRepayPftDate,");
		updateSql
		        .append(" LastRepayRvwDate= :LastRepayRvwDate, LastRepayCpzDate= :LastRepayCpzDate,AllowGrcRepay= :AllowGrcRepay,");
		updateSql
		        .append(" GrcSchdMthd= :GrcSchdMthd, GrcMargin= :GrcMargin, RepayMargin= :RepayMargin,");
		updateSql
		        .append(" FinCommitmentRef= :FinCommitmentRef, DepreciationFrq= :DepreciationFrq, FinCurrAssetValue= :FinCurrAssetValue,");
		updateSql
		        .append(" NextDepDate= :NextDepDate, LastDepDate= :LastDepDate,FinAccount=:FinAccount,");
		updateSql
		        .append(" AlwIndRate = :AlwIndRate, IndBaseRate = :IndBaseRate, GrcAlwIndRate = :GrcAlwIndRate,");
		updateSql
		        .append(" GrcIndBaseRate = :GrcIndBaseRate, FinCustPftAccount= :FinCustPftAccount, ClosingStatus= :ClosingStatus, ");
		updateSql
				.append(" FinApprovedDate= :FinApprovedDate, FeeChargeAmt=:FeeChargeAmt, LimitValid= :LimitValid, OverrideLimit= :OverrideLimit,");
		updateSql
				.append(" AnualizedPercRate =:AnualizedPercRate , EffectiveRateOfReturn =:EffectiveRateOfReturn , FinRepayPftOnFrq =:FinRepayPftOnFrq ,");
		updateSql.append(" Version = :Version,LastMntBy = :LastMntBy, LastMntOn = :LastMntOn,");
		updateSql
		        .append(" RecordStatus= :RecordStatus, RoleCode = :RoleCode, NextRoleCode = :NextRoleCode,");
		updateSql
		        .append(" TaskId = :TaskId, NextTaskId = :NextTaskId, RecordType = :RecordType, WorkflowId = :WorkflowId");
		updateSql.append(" Where FinReference =:FinReference");
		logger.debug("updateSql: " + updateSql.toString());

		SqlParameterSource[] beanParameters = SqlParameterSourceUtils.createBatch(financeMain
		        .toArray());
		this.namedParameterJdbcTemplate.batchUpdate(updateSql.toString(), beanParameters);

		logger.debug("Leaving");
	}

	@SuppressWarnings("serial")
	@Override
	public void updateFinAccounts(FinanceMain financeMain) {
		int recordCount = 0;
		logger.debug("Entering");
		StringBuilder updateSql = new StringBuilder("Update FinanceMain");
		updateSql.append(" Set FinAccount =:FinAccount , FinCustPftAccount =:FinCustPftAccount ");
		updateSql.append(" Where FinReference =:FinReference");

		logger.debug("updateSql: " + updateSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(financeMain);
		recordCount = this.namedParameterJdbcTemplate.update(updateSql.toString(), beanParameters);

		if (recordCount <= 0) {
			logger.debug("Error Update Method Count :" + recordCount);
			ErrorDetails errorDetails = getError("41004", financeMain.getId(), financeMain
			        .getUserDetails().getUsrLanguage());
			throw new DataAccessException(errorDetails.getError()) { };
		}
		logger.debug("Leaving");
	}

	/**
	 * Method for get the Actual Profit Amount
	 * @throws ClassNotFoundException 
	 * @throws DataAccessException 
	 */
    @Override
    public List<BigDecimal> getActualPftBal(String finReference, String type) {
		logger.debug("Entering");

		FinanceMain financeMain = getFinanceMain(false);
		financeMain.setFinReference(finReference);

		StringBuilder selectSql = new StringBuilder();
		selectSql.append(" SELECT TotalProfit, TotalCpz From FinanceMain");
		selectSql.append(StringUtils.trimToEmpty(type));
		selectSql.append(" Where FinReference ='" + finReference + "'");

		logger.debug("selectSql: " + selectSql.toString());
		RowMapper<FinanceMain> typeRowMapper = ParameterizedBeanPropertyRowMapper.newInstance(FinanceMain.class);
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(financeMain);
		logger.debug("Leaving");

		financeMain = this.namedParameterJdbcTemplate.queryForObject(selectSql.toString(),beanParameters,typeRowMapper);
		List<BigDecimal> list = new ArrayList<BigDecimal>();
		list.add(financeMain.getTotalProfit());
		list.add(financeMain.getTotalCpz());
		return list;
	}

	@SuppressWarnings("serial")
    @Override
    public void updateRepaymentAmount(String finReference, BigDecimal repaymentAmount) {
		logger.debug("Entering");
		
		int recordCount = 0;
		FinanceMain financeMain = getFinanceMain(false);
		financeMain.setFinReference(finReference);
		financeMain.setFinRepaymentAmount(repaymentAmount);
		
		StringBuilder updateSql = new StringBuilder("Update FinanceMain");
		updateSql.append(" Set FinRepaymentAmount =:FinRepaymentAmount ");
		updateSql.append(" Where FinReference =:FinReference");

		logger.debug("updateSql: " + updateSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(financeMain);
		recordCount = this.namedParameterJdbcTemplate.update(updateSql.toString(), beanParameters);

		if (recordCount <= 0) {
			logger.debug("Error Update Method Count :" + recordCount);
			ErrorDetails errorDetails = getError("41004", financeMain.getId(), financeMain
			        .getUserDetails().getUsrLanguage());
			throw new DataAccessException(errorDetails.getError()) { };
		}
    }
	
	/**
	 * Method for get the Finance Details and FinanceShedule Details
	 */
	@Override
	public List<FinanceEnquiry> getFinanceDetailsByCustId(long custId) {
		logger.debug("Entering");
		FinanceEnquiry finEnquiry =new FinanceEnquiry();
		StringBuilder selectSql = new StringBuilder();
		selectSql.append("SELECT fm.FinReference as FinReference, fm.FinType as FinType,fm.LovDescFinTypeName, fm.FinBranch as FinBranch, fm.FinStartDate as FinStartDate,");
		selectSql.append(" fm.GrcPeriodEndDate as GrcPeriodEndDate, fm.NumberOfTerms as NumberOfTerms, fm.MaturityDate as MaturityDate,");
		selectSql.append(" fm.FinCcy as FinCcy,fm.LovDescFinCcyName, fm.FinAmount as FinAmount, fm.FinAmount as CurrentFinAmount,");
		selectSql.append(" fm.NextRepayDate AS NextDueDate, (fsd.PrincipalSchd + fsd.ProfitSchd) as NextDueAmount, fm.lovDescFinFormatter");
		selectSql.append(" FROM FinanceMain_AView AS fm INNER JOIN FinScheduleDetails as fsd ON  fm.FinReference = fsd.FinReference AND fm.NextRepayDate = fsd.SchDate");
		selectSql.append(" Where fm.custId="+custId);
		selectSql.append("  order by fm.FinType, fm.FinCcy");
		
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(finEnquiry);
		RowMapper<FinanceEnquiry> typeRowMapper = ParameterizedBeanPropertyRowMapper
		        .newInstance(FinanceEnquiry.class);

		logger.debug("selectSql: " + selectSql.toString());
		logger.debug("Leaving");
		return this.namedParameterJdbcTemplate.query(selectSql.toString(), beanParameters,
		        typeRowMapper);
	}

	@SuppressWarnings("serial")
    @Override
    public void updateCustCIF(long custID, String finReference) {
		logger.debug("Entering");
		
		int recordCount = 0;
		FinanceMain financeMain = getFinanceMain(false);
		financeMain.setFinReference(finReference);
		financeMain.setCustID(custID);
		
		StringBuilder updateSql = new StringBuilder("Update FinanceMain_Temp");
		updateSql.append(" Set CustID =:CustID ");
		updateSql.append(" Where FinReference =:FinReference");

		logger.debug("updateSql: " + updateSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(financeMain);
		recordCount = this.namedParameterJdbcTemplate.update(updateSql.toString(), beanParameters);

		if (recordCount <= 0) {
			logger.debug("Error Update Method Count :" + recordCount);
			ErrorDetails errorDetails = getError("41004", financeMain.getId(), financeMain
			        .getUserDetails().getUsrLanguage());
			throw new DataAccessException(errorDetails.getError()) { };
		}
	    logger.debug("Leaving");
    }

	@SuppressWarnings("serial")
    @Override
	public void updateFinBlackListStatus(String finReference) {
		logger.debug("Entering");

		int recordCount = 0;
		FinanceMain financeMain = getFinanceMain(false);
		financeMain.setFinReference(finReference);
		financeMain.setBlacklisted(true);

		StringBuilder updateSql = new StringBuilder("Update FinanceMain");
		updateSql.append(" Set Blacklisted =:Blacklisted ");
		updateSql.append(" Where FinReference =:FinReference");

		logger.debug("updateSql: " + updateSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(financeMain);
		recordCount = this.namedParameterJdbcTemplate.update(updateSql.toString(), beanParameters);

		if (recordCount <= 0) {
			logger.debug("Error Update Method Count :" + recordCount);
			ErrorDetails errorDetails = getError("41004", financeMain.getId(), financeMain
					.getUserDetails().getUsrLanguage());
			throw new DataAccessException(errorDetails.getError()) { };
		}
		logger.debug("Leaving");
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

	@SuppressWarnings("serial")
	@Override
	public void updateCancelStatus(FinanceMain financeMain, String type) {
		int recordCount = 0;
		logger.debug("Entering");
		StringBuilder updateSql = new StringBuilder("Update FinanceMain");
		updateSql.append(StringUtils.trimToEmpty(type));
		updateSql.append(" Set  FinIsActive = :FinIsActive , ClosingStatus=:ClosingStatus");
		updateSql.append(" Where FinReference =:FinReference");
		logger.debug("updateSql: " + updateSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(financeMain);
		recordCount = this.namedParameterJdbcTemplate.update(updateSql.toString(), beanParameters);
		if (recordCount <= 0) {
			logger.debug("Error Update Method Count :" + recordCount);
			ErrorDetails errorDetails = getError("41004", financeMain.getId(), financeMain
			        .getUserDetails().getUsrLanguage());
			throw new DataAccessException(errorDetails.getError()) {
			};
		}
		logger.debug("Leaving");
	}
	
	
	
	
	
	
	
	
	
}