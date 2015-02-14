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
 * * FileName : WIFFinanceScheduleDetailDAOImpl.java * * Author : PENNANT TECHONOLOGIES * * Creation Date : 12-11-2011 *
 * * Modified Date : 12-11-2011 * * Description : * *
 ******************************************************************************************** 
 * Date Author Version Comments *
 ******************************************************************************************** 
 * 12-11-2011 Pennant 0.1 * * * * * * * * *
 ******************************************************************************************** 
 */

package com.pennant.backend.dao.finance.impl;

import java.math.BigDecimal;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

import com.pennant.app.util.ErrorUtil;
import com.pennant.backend.dao.finance.FinanceScheduleDetailDAO;
import com.pennant.backend.dao.impl.BasisCodeDAO;
import com.pennant.backend.model.ErrorDetails;
import com.pennant.backend.model.WorkFlowDetails;
import com.pennant.backend.model.finance.AccountHoldStatus;
import com.pennant.backend.model.finance.FinanceScheduleDetail;
import com.pennant.backend.model.finance.FinanceSummary;
import com.pennant.backend.model.finance.FinanceWriteoff;
import com.pennant.backend.model.finance.PaymentDetails;
import com.pennant.backend.model.finance.ScheduleMapDetails;
import com.pennant.backend.model.financemanagement.OverdueChargeRecovery;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantJavaUtil;
import com.pennant.backend.util.WorkFlowUtil;

/**
 * DAO methods implementation for the <b>WIFFinanceScheduleDetail model</b> class.<br>
 * 
 */

public class FinanceScheduleDetailDAOImpl extends BasisCodeDAO<FinanceScheduleDetail> implements
        FinanceScheduleDetailDAO {

	private static Logger logger = Logger.getLogger(FinanceScheduleDetailDAOImpl.class);

	// Spring Named JDBC Template
	private NamedParameterJdbcTemplate namedParameterJdbcTemplate;

	/**
	 * This method set the Work Flow id based on the module name and return the new WIFFinanceScheduleDetail
	 * 
	 * @return WIFFinanceScheduleDetail
	 */

	@Override
	public FinanceScheduleDetail getFinanceScheduleDetail(boolean isWIF) {

		String wifName = "";

		if (isWIF) {
			wifName = "WIFFinScheduleDetails";
		} else {
			wifName = "FinScheduleDetails";
		}

		WorkFlowDetails workFlowDetails = WorkFlowUtil.getWorkFlowDetails(wifName);

		FinanceScheduleDetail wIFFinanceScheduleDetail = new FinanceScheduleDetail();
		if (workFlowDetails != null) {
			wIFFinanceScheduleDetail.setWorkflowId(workFlowDetails.getWorkFlowId());
		}
		logger.debug("Leaving");
		return wIFFinanceScheduleDetail;

	}

	/**
	 * This method get the module from method getWIFFinanceScheduleDetail() and set the new record flag as true and
	 * return FinanceScheduleDetail
	 * 
	 * @return FinanceScheduleDetail
	 */

	@Override
	public FinanceScheduleDetail getNewFinanceScheduleDetail(boolean isWIF) {
		logger.debug("Entering");
		FinanceScheduleDetail wIFFinanceScheduleDetail = getFinanceScheduleDetail(isWIF);
		wIFFinanceScheduleDetail.setNewRecord(true);
		logger.debug("Leaving");
		return wIFFinanceScheduleDetail;
	}

	/**
	 * Fetch the Record Finance Schedule Detail details by key field
	 * 
	 * @param id
	 *            (String)
	 * @param type
	 *            (String) ""/_Temp/_View
	 * @return FinanceScheduleDetail
	 */

	@Override
	public FinanceScheduleDetail getFinanceScheduleDetailById(final String id, final Date schdDate,
	        String type, boolean isWIF) {
		logger.debug("Entering");
		
		FinanceScheduleDetail wIFFinanceScheduleDetail = new FinanceScheduleDetail();
		wIFFinanceScheduleDetail.setId(id);
		wIFFinanceScheduleDetail.setSchDate(schdDate);

		StringBuilder selectSql = new StringBuilder(" Select FinReference, SchDate, SchSeq, PftOnSchDate,");
		selectSql.append(" CpzOnSchDate, RepayOnSchDate, RvwOnSchDate, DisbOnSchDate,Defered,DeferedPay,");
		selectSql.append(" DownpaymentOnSchDate, BalanceForPftCal, BaseRate, SplRate, MrgRate, ActRate, NoOfDays,");
		selectSql.append(" CalOnIndRate, DayFactor, ProfitCalc, ProfitSchd, PrincipalSchd, RepayAmount, ProfitBalance,");
		selectSql.append(" DisbAmount, DownPaymentAmount, CpzAmount,");
		selectSql.append(" ClosingBalance, ProfitFraction, PrvRepayAmount, DefPrincipalBal,");
		selectSql.append(" SchdPriPaid, SchdPftPaid, SchPriPaid, SchPftPaid,Specifier,");
		selectSql.append(" DefProfit, DefPrincipal, DefProfitBal, DefPrincipalSchd,OrgPlanPft,");
		selectSql.append(" DefProfitSchd, DefRepaySchd, DefSchdDate,SchdMethod, DefSchdPftPaid, DefSchdPriPaid,CalculatedRate,FeeChargeAmt,");
		selectSql.append(" FeeSchd , SchdFeePaid , SchdFeeOS , TakafulFeeSchd, SchdTakafulFeePaid, ");

		/*if (StringUtils.trimToEmpty(type).contains("View")) {
			selectSql.append("lovDescBaseRateName,lovDescSplRateName,");
		}*/
		selectSql.append(" Version , LastMntBy, LastMntOn, RecordStatus, RoleCode, NextRoleCode, TaskId,");
		selectSql.append(" NextTaskId, RecordType, WorkflowId");

		if (isWIF) {
			selectSql.append(" From WIFFinScheduleDetails");
		} else {
			selectSql.append(" , RefundOrWaiver ,EarlyPaid, EarlyPaidBal, WriteoffPrincipal, WriteoffProfit ");	
			selectSql.append(" From FinScheduleDetails");
		}

		selectSql.append(StringUtils.trimToEmpty(type));
		selectSql.append(" Where FinReference =:FinReference AND SchDate=:SchDate");

		logger.debug("selectSql: " + selectSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(
		        wIFFinanceScheduleDetail);
		RowMapper<FinanceScheduleDetail> typeRowMapper = ParameterizedBeanPropertyRowMapper
		        .newInstance(FinanceScheduleDetail.class);

		try {
			wIFFinanceScheduleDetail = this.namedParameterJdbcTemplate.queryForObject(
			        selectSql.toString(), beanParameters, typeRowMapper);
		} catch (EmptyResultDataAccessException e) {
			wIFFinanceScheduleDetail = null;
		}
		logger.debug("Leaving");
		return wIFFinanceScheduleDetail;
	}

	/**
	 * This method initialise the Record.
	 * 
	 * @param FinanceScheduleDetail
	 *            (wIFFinanceScheduleDetail)
	 * @return WIFFinanceScheduleDetail
	 */
	@Override
	public void initialize(FinanceScheduleDetail financeScheduleDetail) {
		super.initialize(financeScheduleDetail);
	}

	/**
	 * This method refresh the Record.
	 * 
	 * @param FinanceScheduleDetail
	 *            (wIFFinanceScheduleDetail)
	 * @return void
	 */
	@Override
	public void refresh(FinanceScheduleDetail financeScheduleDetail) {

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
	 * This method Deletes the Record from the WIFFinScheduleDetails or WIFFinScheduleDetails_Temp. if Record not
	 * deleted then throws DataAccessException with error 41003. delete Finance Schedule Detail by key FinReference
	 * 
	 * @param Finance
	 *            Schedule Detail (wIFFinanceScheduleDetail)
	 * @param type
	 *            (String) ""/_Temp/_View
	 * @return void
	 * @throws DataAccessException
	 * 
	 */

	public void deleteByFinReference(String id, String type, boolean isWIF, long logKey) {
		logger.debug("Entering");
		FinanceScheduleDetail wIFFinanceScheduleDetail = new FinanceScheduleDetail();
		wIFFinanceScheduleDetail.setId(id);

		StringBuilder deleteSql = new StringBuilder("Delete From ");

		if (isWIF) {
			deleteSql.append(" WIFFinScheduleDetails");
		} else {
			deleteSql.append(" FinScheduleDetails");
		}

		deleteSql.append(StringUtils.trimToEmpty(type));
		deleteSql.append(" Where FinReference =:FinReference");
		if(logKey != 0){
			deleteSql.append(" AND LogKey =:LogKey");
		}
		logger.debug("deleteSql: " + deleteSql.toString());

		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(
		        wIFFinanceScheduleDetail);
		this.namedParameterJdbcTemplate.update(deleteSql.toString(), beanParameters);
		logger.debug("Leaving");
	}

	/**
	 * This method Deletes the Record from the WIFFinScheduleDetails or WIFFinScheduleDetails_Temp. if Record not
	 * deleted then throws DataAccessException with error 41003. delete Finance Schedule Detail by key FinReference
	 * 
	 * @param Finance
	 *            Schedule Detail (wIFFinanceScheduleDetail)
	 * @param type
	 *            (String) ""/_Temp/_View
	 * @return void
	 * @throws DataAccessException
	 * 
	 */
	public void deleteFromWork(String finReference, long userId) {
		logger.debug("Entering");
		FinanceScheduleDetail financeScheduleDetail = new FinanceScheduleDetail();
		financeScheduleDetail.setFinReference(finReference);
		financeScheduleDetail.setLastMntBy(userId);

		StringBuilder deleteSql = new StringBuilder("Delete From ");
		deleteSql.append(" FinScheduleDetails_Work");
		deleteSql.append(" Where FinReference =:FinReference");

		logger.debug("deleteSql: " + deleteSql.toString());

		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(
		        financeScheduleDetail);
		this.namedParameterJdbcTemplate.update(deleteSql.toString(), beanParameters);
		logger.debug("Leaving");
	}

	/**
	 * This method Deletes the Record from the WIFFinScheduleDetails or WIFFinScheduleDetails_Temp. if Record not
	 * deleted then throws DataAccessException with error 41003. delete Finance Schedule Detail by key FinReference
	 * 
	 * @param Finance
	 *            Schedule Detail (wIFFinanceScheduleDetail)
	 * @param type
	 *            (String) ""/_Temp/_View
	 * @return void
	 * @throws DataAccessException
	 * 
	 */

	@SuppressWarnings("serial")
	public void delete(FinanceScheduleDetail wIFFinanceScheduleDetail, String type, boolean isWIF) {
		logger.debug("Entering");
		int recordCount = 0;

		StringBuilder deleteSql = new StringBuilder("Delete From ");

		if (isWIF) {
			deleteSql.append(" WIFFinScheduleDetails");
		} else {
			deleteSql.append(" FinScheduleDetails");
		}

		deleteSql.append(StringUtils.trimToEmpty(type));
		deleteSql.append(" Where FinReference =:FinReference and SchDate = :SchDate");
		logger.debug("deleteSql: " + deleteSql.toString());

		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(
		        wIFFinanceScheduleDetail);
		try {
			recordCount = this.namedParameterJdbcTemplate.update(deleteSql.toString(),
			        beanParameters);
			if (recordCount <= 0) {
				ErrorDetails errorDetails = getError("41003", wIFFinanceScheduleDetail.getId(),
				        wIFFinanceScheduleDetail.getUserDetails().getUsrLanguage());
				throw new DataAccessException(errorDetails.getError()) {
				};
			}
		} catch (DataAccessException e) {
			logger.error(e);
			ErrorDetails errorDetails = getError("41006", wIFFinanceScheduleDetail.getId(),
			        wIFFinanceScheduleDetail.getUserDetails().getUsrLanguage());
			throw new DataAccessException(errorDetails.getError()) {
			};
		}
		logger.debug("Leaving");
	}

	/**
	 * This method insert new Records into WIFFinScheduleDetails or WIFFinScheduleDetails_Temp.
	 * 
	 * save Finance Schedule Detail
	 * 
	 * @param Finance
	 *            Schedule Detail (wIFFinanceScheduleDetail)
	 * @param type
	 *            (String) ""/_Temp/_View
	 * @return void
	 * @throws DataAccessException
	 * 
	 */

	@Override
	public String save(FinanceScheduleDetail wIFFinanceScheduleDetail, String type, boolean isWIF) {
		logger.debug("Entering");

		StringBuilder insertSql = new StringBuilder("Insert Into ");

		if (isWIF) {
			insertSql.append(" WIFFinScheduleDetails");
		} else {
			insertSql.append(" FinScheduleDetails");
		}

		insertSql.append(StringUtils.trimToEmpty(type));
		insertSql.append(" (FinReference, SchDate, SchSeq, PftOnSchDate,");
		insertSql.append(" CpzOnSchDate, RepayOnSchDate, RvwOnSchDate, DisbOnSchDate,Defered,DeferedPay,");
		insertSql.append(" DownpaymentOnSchDate, BalanceForPftCal, BaseRate, SplRate, MrgRate, ActRate, NoOfDays,");
		insertSql.append(" CalOnIndRate, DayFactor, ProfitCalc, ProfitSchd, PrincipalSchd, RepayAmount, ProfitBalance,");
		insertSql.append(" DisbAmount, DownPaymentAmount, CpzAmount, OrgPft , OrgPri, OrgEndBal, ");
		insertSql.append(" ClosingBalance, ProfitFraction, PrvRepayAmount, DefPrincipalBal,OrgPlanPft,");
		insertSql.append(" SchdPriPaid, SchdPftPaid, SchPriPaid, SchPftPaid, Specifier,");
		insertSql.append(" DefProfit, DefPrincipal, DefProfitBal, DefPrincipalSchd,CalculatedRate,FeeChargeAmt,");
		insertSql.append(" FeeSchd , SchdFeePaid , SchdFeeOS ,TakafulFeeSchd, SchdTakafulFeePaid,");
		if(!isWIF){
			insertSql.append(" RefundOrWaiver, EarlyPaid, EarlyPaidBal , WriteoffPrincipal, WriteoffProfit,");	
		}
		insertSql.append(" DefProfitSchd, DefRepaySchd, DefSchdDate, SchdMethod, DefSchdPftPaid, DefSchdPriPaid,");
		insertSql.append(" Version , LastMntBy, LastMntOn, RecordStatus, RoleCode, NextRoleCode, TaskId,");
		insertSql.append(" NextTaskId, RecordType, WorkflowId)");
		insertSql.append(" Values(:FinReference, :SchDate, :SchSeq, :PftOnSchDate,");
		insertSql.append(" :CpzOnSchDate, :RepayOnSchDate, :RvwOnSchDate, :DisbOnSchDate, :Defered, :DeferedPay,");
		insertSql.append(" :DownpaymentOnSchDate, :BalanceForPftCal, :BaseRate, :SplRate,:MrgRate, :ActRate, :NoOfDays,");
		insertSql.append(" :CalOnIndRate,:DayFactor, :ProfitCalc, :ProfitSchd, :PrincipalSchd, :RepayAmount, :ProfitBalance,");
		insertSql.append(" :DisbAmount, :DownPaymentAmount, :CpzAmount, :OrgPft , :OrgPri, :OrgEndBal, ");
		insertSql.append(" :ClosingBalance, :ProfitFraction, :PrvRepayAmount, :DefPrincipalBal,:OrgPlanPft,");
		insertSql.append(" :SchdPriPaid, :SchdPftPaid, :SchPriPaid, :SchPftPaid, :Specifier,");
		insertSql.append(" :DefProfit, :DefPrincipal, :DefProfitBal, :DefPrincipalSchd,:CalculatedRate,:FeeChargeAmt,");
		insertSql.append(" :FeeSchd , :SchdFeePaid , :SchdFeeOS , :TakafulFeeSchd, :SchdTakafulFeePaid,");
		if(!isWIF){
			insertSql.append(" :RefundOrWaiver, :EarlyPaid, :EarlyPaidBal, :WriteoffPrincipal, :WriteoffProfit,");	
		}
		insertSql.append(" :DefProfitSchd, :DefRepaySchd, :DefSchdDate, :SchdMethod, :DefSchdPftPaid, :DefSchdPriPaid,");
		insertSql.append(" :Version , :LastMntBy, :LastMntOn, :RecordStatus, :RoleCode, :NextRoleCode, :TaskId,");
		insertSql.append(" :NextTaskId, :RecordType, :WorkflowId)");

		logger.debug("insertSql: " + insertSql.toString());

		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(
		        wIFFinanceScheduleDetail);
		this.namedParameterJdbcTemplate.update(insertSql.toString(), beanParameters);
		logger.debug("Leaving");
		return wIFFinanceScheduleDetail.getId();
	}

	public void saveList(List<FinanceScheduleDetail> financeScheduleDetail, String type,
	        boolean isWIF) {
		logger.debug("Entering");

		StringBuilder insertSql = new StringBuilder("Insert Into ");

		if (isWIF) {
			insertSql.append(" WIFFinScheduleDetails");
		} else {
			insertSql.append(" FinScheduleDetails");
		}

		insertSql.append(StringUtils.trimToEmpty(type));
		insertSql.append(" (FinReference, SchDate, SchSeq, PftOnSchDate,");
		insertSql.append(" CpzOnSchDate, RepayOnSchDate, RvwOnSchDate, DisbOnSchDate,Defered,DeferedPay,");
		insertSql.append(" DownpaymentOnSchDate, BalanceForPftCal, BaseRate, SplRate, MrgRate, ActRate, NoOfDays,");
		insertSql.append(" CalOnIndRate, DayFactor, ProfitCalc, ProfitSchd, PrincipalSchd, RepayAmount, ProfitBalance,");
		insertSql.append(" DisbAmount, DownPaymentAmount, CpzAmount, OrgPft , OrgPri, OrgEndBal,OrgPlanPft, ");
		insertSql.append(" ClosingBalance, ProfitFraction, PrvRepayAmount, DefPrincipalBal,CalculatedRate,FeeChargeAmt,");
		insertSql.append(" FeeSchd , SchdFeePaid , SchdFeeOS ,TakafulFeeSchd, SchdTakafulFeePaid,");
		if(!isWIF){
			insertSql.append(" RefundOrWaiver, EarlyPaid, EarlyPaidBal,WriteoffPrincipal, WriteoffProfit, ");	
			if(type.contains("Log")){
				insertSql.append(" LogKey , ");
			}
		}
		insertSql.append(" SchdPriPaid, SchdPftPaid, SchPriPaid, SchPftPaid, Specifier,");
		insertSql.append(" DefProfit, DefPrincipal, DefProfitBal, DefPrincipalSchd,");
		insertSql.append(" DefProfitSchd, DefRepaySchd, DefSchdDate, SchdMethod, DefSchdPftPaid, DefSchdPriPaid");
		insertSql.append(", Version , LastMntBy, LastMntOn, RecordStatus, RoleCode, NextRoleCode, TaskId,");
		insertSql.append(" NextTaskId, RecordType, WorkflowId)");
		insertSql.append(" Values(:FinReference, :SchDate, :SchSeq, :PftOnSchDate,");
		insertSql.append(" :CpzOnSchDate, :RepayOnSchDate, :RvwOnSchDate, :DisbOnSchDate, :Defered, :DeferedPay,");
		insertSql.append(" :DownpaymentOnSchDate, :BalanceForPftCal, :BaseRate, :SplRate,:MrgRate, :ActRate, :NoOfDays,");
		insertSql.append(" :CalOnIndRate,:DayFactor, :ProfitCalc, :ProfitSchd, :PrincipalSchd, :RepayAmount, :ProfitBalance,");
		insertSql.append(" :DisbAmount, :DownPaymentAmount, :CpzAmount, :OrgPft , :OrgPri, :OrgEndBal,:OrgPlanPft, ");
		insertSql.append(" :ClosingBalance, :ProfitFraction, :PrvRepayAmount, :DefPrincipalBal, :CalculatedRate,:FeeChargeAmt,");
		insertSql.append(" :FeeSchd , :SchdFeePaid , :SchdFeeOS , :TakafulFeeSchd, :SchdTakafulFeePaid,");
		if(!isWIF){
			insertSql.append(" :RefundOrWaiver, :EarlyPaid, :EarlyPaidBal, :WriteoffPrincipal, :WriteoffProfit,");
			if(type.contains("Log")){
				insertSql.append(" :LogKey , ");
			}
		}
		insertSql.append(" :SchdPriPaid, :SchdPftPaid, :SchPriPaid, :SchPftPaid, :Specifier,");
		insertSql.append(" :DefProfit, :DefPrincipal, :DefProfitBal, :DefPrincipalSchd,");
		insertSql.append(" :DefProfitSchd, :DefRepaySchd, :DefSchdDate, :SchdMethod, :DefSchdPftPaid, :DefSchdPriPaid");
		insertSql.append(", :Version , :LastMntBy, :LastMntOn, :RecordStatus, :RoleCode, :NextRoleCode, :TaskId,");
		insertSql.append(" :NextTaskId, :RecordType, :WorkflowId)");

		logger.debug("insertSql: " + insertSql.toString());

		SqlParameterSource[] beanParameters = SqlParameterSourceUtils
		        .createBatch(financeScheduleDetail.toArray());
		this.namedParameterJdbcTemplate.batchUpdate(insertSql.toString(), beanParameters);
		logger.debug("Leaving");
	}

	/**
	 * This method updates the Record WIFFinScheduleDetails or WIFFinScheduleDetails_Temp. if Record not updated then
	 * throws DataAccessException with error 41004. update Finance Schedule Detail by key FinReference and Version
	 * 
	 * @param Finance
	 *            Schedule Detail (wIFFinanceScheduleDetail)
	 * @param type
	 *            (String) ""/_Temp/_View
	 * @return void
	 * @throws DataAccessException
	 * 
	 */

	@SuppressWarnings("serial")
	@Override
	public void update(FinanceScheduleDetail financeScheduleDetail, String type, boolean isWIF) {
		int recordCount = 0;
		logger.debug("Entering");
		StringBuilder updateSql = new StringBuilder("Update ");

		if (isWIF) {
			updateSql.append("WIFFinScheduleDetails");
		} else {
			updateSql.append(" FinScheduleDetails");
		}

		updateSql.append(StringUtils.trimToEmpty(type));
		updateSql.append(" Set FinReference = :FinReference, SchDate = :SchDate, SchSeq = :SchSeq,");
		updateSql.append(" PftOnSchDate= :PftOnSchDate, CpzOnSchDate = :CpzOnSchDate, RepayOnSchDate= :RepayOnSchDate,");
		updateSql.append(" RvwOnSchDate= :RvwOnSchDate, DisbOnSchDate= :DisbOnSchDate, Defered= :Defered,");
		updateSql.append(" DeferedPay= :DeferedPay, DownpaymentOnSchDate = :DownpaymentOnSchDate,");
		updateSql.append(" BalanceForPftCal= :BalanceForPftCal, BaseRate= :BaseRate, SplRate= :SplRate,MrgRate =:MrgRate,");
		updateSql.append(" ActRate= :ActRate, NoOfDays= :NoOfDays, CalOnIndRate=:CalOnIndRate,DayFactor =:DayFactor, ProfitCalc= :ProfitCalc,");
		updateSql.append(" ProfitSchd= :ProfitSchd, PrincipalSchd= :PrincipalSchd, RepayAmount= :RepayAmount,");
		updateSql.append(" ProfitBalance=:ProfitBalance, DisbAmount= :DisbAmount, DownPaymentAmount= :DownPaymentAmount,");
		updateSql.append(" CpzAmount= :CpzAmount, ClosingBalance= :ClosingBalance,");
		updateSql.append(" OrgPft =:OrgPft , OrgPri=:OrgPri, OrgEndBal=:OrgEndBal,OrgPlanPft=:OrgPlanPft, ");
		updateSql.append(" ProfitFraction= :ProfitFraction, PrvRepayAmount= :PrvRepayAmount, DefPrincipalBal=:DefPrincipalBal,");
		updateSql.append(" SchdPriPaid= :SchdPriPaid, SchdPftPaid= :SchdPftPaid, SchPriPaid= :SchPriPaid,");
		updateSql.append(" SchPftPaid= :SchPftPaid,Specifier= :Specifier,DefProfit = :DefProfit,");
		updateSql.append(" DefPrincipal= :DefPrincipal, DefProfitBal =:DefProfitBal,");
		updateSql.append(" DefPrincipalSchd = :DefPrincipalSchd, DefProfitSchd =:DefProfitSchd,CalculatedRate =:CalculatedRate,FeeChargeAmt=:FeeChargeAmt, ");
		updateSql.append(" FeeSchd=:FeeSchd , SchdFeePaid=:SchdFeePaid , SchdFeeOS=:SchdFeeOS , TakafulFeeSchd=:TakafulFeeSchd, SchdTakafulFeePaid=:SchdTakafulFeePaid,");
		if(!isWIF){
			updateSql.append(" RefundOrWaiver=:RefundOrWaiver, EarlyPaid =:EarlyPaid, EarlyPaidBal=:EarlyPaidBal ,");	
			updateSql.append(" WriteoffPrincipal=:WriteoffPrincipal, WriteoffProfit=:WriteoffProfit ,");	
		}
		updateSql.append(" DefRepaySchd= :DefRepaySchd, DefSchdDate= :DefSchdDate, SchdMethod = :SchdMethod,");
		updateSql.append(" DefSchdPftPaid = :DefSchdPftPaid, DefSchdPriPaid = :DefSchdPriPaid, Version= :Version , LastMntBy=:LastMntBy,");
		updateSql.append(" LastMntOn= :LastMntOn, RecordStatus=:RecordStatus, RoleCode=:RoleCode,");
		updateSql.append(" NextRoleCode= :NextRoleCode, TaskId= :TaskId,");
		updateSql.append(" NextTaskId= :NextTaskId, RecordType= :RecordType, WorkflowId= :WorkflowId");
		updateSql.append(" Where FinReference =:FinReference AND SchDate = :SchDate AND SchSeq = :SchSeq");

		logger.debug("updateSql: " + updateSql.toString());

		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(
		        financeScheduleDetail);
		recordCount = this.namedParameterJdbcTemplate.update(updateSql.toString(), beanParameters);

		if (recordCount <= 0) {
			logger.debug("Error Update Method Count :" + recordCount);
			ErrorDetails errorDetails = getError("41004", financeScheduleDetail.getId(),
			        financeScheduleDetail.getUserDetails().getUsrLanguage());
			throw new DataAccessException(errorDetails.getError()) {
			};
		}
		logger.debug("Leaving");
	}
	
	@SuppressWarnings("serial")
	@Override
	public void updateForRpy(FinanceScheduleDetail financeScheduleDetail, String rpyFor) {
		int recordCount = 0;
		logger.debug("Entering");
		
		StringBuilder updateSql = new StringBuilder("Update FinScheduleDetails SET ");
		if(rpyFor.equals(PennantConstants.DEFERED)){
			updateSql.append(" DefSchdPftPaid=:DefSchdPftPaid , DefSchdPriPaid=:DefSchdPriPaid , ");
		}else if(rpyFor.equals(PennantConstants.SCHEDULE)){
			updateSql.append(" SchdPftPaid=:SchdPftPaid, SchdPriPaid=:SchdPriPaid, SchPftPaid=:SchPftPaid , SchPriPaid=:SchPriPaid , ");
		}
		updateSql.append("  DefSchPftPaid=:DefSchPftPaid, DefSchPriPaid=:DefSchPriPaid ");
		updateSql.append(" Where FinReference =:FinReference AND SchDate = :SchDate AND SchSeq = :SchSeq");

		logger.debug("updateSql: " + updateSql.toString());

		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(
		        financeScheduleDetail);
		recordCount = this.namedParameterJdbcTemplate.update(updateSql.toString(), beanParameters);

		if (recordCount <= 0) {
			logger.debug("Error Update Method Count :" + recordCount);
			ErrorDetails errorDetails = getError("41004", financeScheduleDetail.getId(),
			        financeScheduleDetail.getUserDetails().getUsrLanguage());
			throw new DataAccessException(errorDetails.getError()) {
			};
		}
		logger.debug("Leaving");
	}

	public void updateList(List<FinanceScheduleDetail> financeScheduleDetail, String type) {
		logger.debug("Entering");
		
		StringBuilder updateSql = new StringBuilder("Update FinScheduleDetails");
		updateSql.append(StringUtils.trimToEmpty(type));
		updateSql.append(" Set FinReference = :FinReference, SchDate = :SchDate, SchSeq = :SchSeq,");
		updateSql.append(" PftOnSchDate= :PftOnSchDate, CpzOnSchDate = :CpzOnSchDate, RepayOnSchDate= :RepayOnSchDate,");
		updateSql.append(" RvwOnSchDate= :RvwOnSchDate, DisbOnSchDate= :DisbOnSchDate, Defered= :Defered,");
		updateSql.append(" DeferedPay= :DeferedPay, DownpaymentOnSchDate = :DownpaymentOnSchDate,");
		updateSql.append(" BalanceForPftCal= :BalanceForPftCal, BaseRate= :BaseRate, SplRate= :SplRate,MrgRate =:MrgRate,");
		updateSql.append(" ActRate= :ActRate, NoOfDays= :NoOfDays,CalOnIndRate = :CalOnIndRate, DayFactor =:DayFactor, ProfitCalc= :ProfitCalc,");
		updateSql.append(" ProfitSchd= :ProfitSchd, PrincipalSchd= :PrincipalSchd, RepayAmount= :RepayAmount,");
		updateSql.append(" ProfitBalance=:ProfitBalance, DisbAmount= :DisbAmount, DownPaymentAmount= :DownPaymentAmount,");
		updateSql.append(" CpzAmount= :CpzAmount, FeeChargeAmt=:FeeChargeAmt, RefundOrWaiver=:RefundOrWaiver, EarlyPaid =:EarlyPaid, EarlyPaidBal=:EarlyPaidBal ,");	
		updateSql.append(" ClosingBalance= :ClosingBalance,WriteoffPrincipal=:WriteoffPrincipal, WriteoffProfit=:WriteoffProfit ,");
		updateSql.append(" ProfitFraction= :ProfitFraction, PrvRepayAmount= :PrvRepayAmount,");
		updateSql.append(" DefPrincipalBal=:DefPrincipalBal,CalculatedRate =:CalculatedRate, ");
		updateSql.append(" SchdPriPaid= :SchdPriPaid, SchdPftPaid= :SchdPftPaid, SchPriPaid= :SchPriPaid,");
		updateSql.append(" SchPftPaid= :SchPftPaid,Specifier= :Specifier,DefProfit = :DefProfit,");
		updateSql.append(" DefPrincipal= :DefPrincipal, DefProfitBal =:DefProfitBal,");
		updateSql.append(" DefPrincipalSchd = :DefPrincipalSchd, DefProfitSchd =:DefProfitSchd,");
		updateSql.append(" DefRepaySchd= :DefRepaySchd, DefSchdDate= :DefSchdDate, SchdMethod = :SchdMethod, ");
		updateSql.append(" OrgPft =:OrgPft , OrgPri=:OrgPri, OrgEndBal=:OrgEndBal, OrgPlanPft=:OrgPlanPft, ");
		updateSql.append(" FeeSchd=:FeeSchd , SchdFeePaid=:SchdFeePaid , SchdFeeOS=:SchdFeeOS, TakafulFeeSchd=:TakafulFeeSchd, SchdTakafulFeePaid=:SchdTakafulFeePaid ");
		updateSql.append(" Where FinReference =:FinReference AND SchDate = :SchDate AND SchSeq = :SchSeq ");

		logger.debug("updateSql: " + updateSql.toString());

		SqlParameterSource[] beanParameters = SqlParameterSourceUtils
		        .createBatch(financeScheduleDetail.toArray());
		this.namedParameterJdbcTemplate.batchUpdate(updateSql.toString(), beanParameters);

		logger.debug("Leaving");
	}

	private ErrorDetails getError(String errorId, String finReference, String userLanguage) {
		String[][] parms = new String[2][1];
		parms[1][0] = finReference;
		parms[0][0] = PennantJavaUtil.getLabel("label_FinReference") + ":" + parms[1][0];
		return ErrorUtil.getErrorDetail(new ErrorDetails(PennantConstants.KEY_FIELD, errorId,
		        parms[0], parms[1]), userLanguage);
	}

	@Override
	public List<FinanceScheduleDetail> getFinScheduleDetails(String id, String type, boolean isWIF) {

		FinanceScheduleDetail detail = new FinanceScheduleDetail();
		detail.setId(id);

		StringBuilder selectSql = new StringBuilder(" Select FinReference, SchDate, SchSeq, PftOnSchDate,");
		selectSql.append(" CpzOnSchDate, RepayOnSchDate, RvwOnSchDate, DisbOnSchDate,Defered,DeferedPay,");
		selectSql.append(" DownpaymentOnSchDate, BalanceForPftCal, BaseRate, SplRate, MrgRate, ActRate, NoOfDays,");
		selectSql.append(" CalOnIndRate, DayFactor, ProfitCalc, ProfitSchd, PrincipalSchd, RepayAmount, ProfitBalance,");
		selectSql.append(" DisbAmount, DownPaymentAmount, CpzAmount, OrgPft , OrgPri, OrgEndBal,OrgPlanPft,");
		selectSql.append(" ClosingBalance, ProfitFraction, PrvRepayAmount, DefPrincipalBal,CalculatedRate,FeeChargeAmt, ");
		selectSql.append(" FeeSchd , SchdFeePaid , SchdFeeOS,  TakafulFeeSchd, SchdTakafulFeePaid, ");
		if(!isWIF){
			selectSql.append(" RefundOrWaiver, EarlyPaid , EarlyPaidBal ,WriteoffPrincipal, WriteoffProfit,");
		}
		selectSql.append(" SchdPriPaid, SchdPftPaid, SchPriPaid, SchPftPaid,Specifier,");
		selectSql.append(" DefProfit, DefPrincipal, DefProfitBal, DefPrincipalSchd,");
		selectSql.append(" DefProfitSchd, DefRepaySchd, DefSchdDate, SchdMethod, DefSchdPftPaid, DefSchdPriPaid,");

		/*if (StringUtils.trimToEmpty(type).contains("View")) {
			selectSql.append(" lovDescBaseRateName,lovDescSplRateName,");
		}*/
		selectSql.append(" Version , LastMntBy, LastMntOn, RecordStatus, RoleCode, NextRoleCode, TaskId,");
		selectSql.append(" NextTaskId, RecordType, WorkflowId");

		if (isWIF) {
			selectSql.append(" From WIFFinScheduleDetails");
		} else {
			selectSql.append(" From FinScheduleDetails");
		}

		selectSql.append(StringUtils.trimToEmpty(type));
		selectSql.append(" Where FinReference =:FinReference");

		logger.debug("selectSql: " + selectSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(
		        detail);
		RowMapper<FinanceScheduleDetail> typeRowMapper = ParameterizedBeanPropertyRowMapper
		        .newInstance(FinanceScheduleDetail.class);

		logger.debug("Leaving");
		return this.namedParameterJdbcTemplate.query(selectSql.toString(), beanParameters,
		        typeRowMapper);
	}
	
	@Override
	public List<FinanceScheduleDetail> getFinScheduleDetails(String id, String type, boolean isWIF, long logKey) {

		FinanceScheduleDetail detail = new FinanceScheduleDetail();
		detail.setId(id);
		detail.setLogKey(logKey);

		StringBuilder selectSql = new StringBuilder(" Select FinReference, SchDate, SchSeq, PftOnSchDate,");
		selectSql.append(" CpzOnSchDate, RepayOnSchDate, RvwOnSchDate, DisbOnSchDate,Defered,DeferedPay,");
		selectSql.append(" DownpaymentOnSchDate, BalanceForPftCal, BaseRate, SplRate, MrgRate, ActRate, NoOfDays,");
		selectSql.append(" CalOnIndRate, DayFactor, ProfitCalc, ProfitSchd, PrincipalSchd, RepayAmount, ProfitBalance,");
		selectSql.append(" DisbAmount, DownPaymentAmount, CpzAmount, OrgPft , OrgPri, OrgEndBal,OrgPlanPft, ");
		selectSql.append(" ClosingBalance, ProfitFraction, PrvRepayAmount, DefPrincipalBal,CalculatedRate,FeeChargeAmt, ");
		selectSql.append(" FeeSchd , SchdFeePaid , SchdFeeOS, TakafulFeeSchd, SchdTakafulFeePaid,  ");
		if(!isWIF){
			selectSql.append(" RefundOrWaiver, EarlyPaid , EarlyPaidBal ,WriteoffPrincipal, WriteoffProfit,");
		}
		selectSql.append(" SchdPriPaid, SchdPftPaid, SchPriPaid, SchPftPaid,Specifier,");
		selectSql.append(" DefProfit, DefPrincipal, DefProfitBal, DefPrincipalSchd,");
		selectSql.append(" DefProfitSchd, DefRepaySchd, DefSchdDate, SchdMethod, DefSchdPftPaid, DefSchdPriPaid,");

		/*if (StringUtils.trimToEmpty(type).contains("View")) {
			selectSql.append(" lovDescBaseRateName,lovDescSplRateName,");
		}*/
		selectSql.append(" Version , LastMntBy, LastMntOn, RecordStatus, RoleCode, NextRoleCode, TaskId,");
		selectSql.append(" NextTaskId, RecordType, WorkflowId");

		if (isWIF) {
			selectSql.append(" From WIFFinScheduleDetails");
		} else {
			selectSql.append(" From FinScheduleDetails");
		}

		selectSql.append(StringUtils.trimToEmpty(type));
		selectSql.append(" Where FinReference =:FinReference AND LogKey =:LogKey");

		logger.debug("selectSql: " + selectSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(
		        detail);
		RowMapper<FinanceScheduleDetail> typeRowMapper = ParameterizedBeanPropertyRowMapper
		        .newInstance(FinanceScheduleDetail.class);

		logger.debug("Leaving");
		return this.namedParameterJdbcTemplate.query(selectSql.toString(), beanParameters,
		        typeRowMapper);
	}
	
	@Override
	public List<FinanceScheduleDetail> getFinSchdDetailsForBatch(String finReference) {
		logger.debug("Entering");
		
		FinanceScheduleDetail schdDetail = new FinanceScheduleDetail();
		schdDetail.setFinReference(finReference);

		StringBuilder selectSql = new StringBuilder(" Select SchDate, CpzOnSchDate, RepayOnSchDate, BalanceForPftCal, ");
		selectSql.append(" ProfitCalc, ProfitSchd, PrincipalSchd, DisbAmount, DownPaymentAmount, CpzAmount, CalculatedRate, " );
		selectSql.append(" SchdPriPaid, SchdPftPaid, DefPrincipalSchd, DefProfitSchd, DefSchdPftPaid, DefSchdPriPaid ");
		selectSql.append(" From FinScheduleDetails");
		selectSql.append(" Where FinReference =:FinReference");

		logger.debug("selectSql: " + selectSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(schdDetail);
		RowMapper<FinanceScheduleDetail> typeRowMapper = ParameterizedBeanPropertyRowMapper.newInstance(FinanceScheduleDetail.class);

		logger.debug("Leaving");
		return this.namedParameterJdbcTemplate.query(selectSql.toString(), beanParameters, typeRowMapper);
	}

	@Override
    public FinanceScheduleDetail getFinSchdDetailForRpy(String finReference, Date rpyDate, String finRpyFor) {
		logger.debug("Entering");
		
		FinanceScheduleDetail schdDetail = new FinanceScheduleDetail();
		schdDetail.setFinReference(finReference);
		schdDetail.setSchDate(rpyDate);

		StringBuilder selectSql = new StringBuilder(" Select SchSeq ");
		if(finRpyFor.equals(PennantConstants.DEFERED)){
			selectSql.append(" ,DefPrincipalSchd, DefProfitSchd, DefSchdPftPaid, DefSchdPriPaid ");
		}else if(finRpyFor.equals(PennantConstants.SCHEDULE)){
			selectSql.append(" ,SchdPftPaid, SchdPriPaid, ProfitSchd, PrincipalSchd ");
		}
		selectSql.append(" From FinScheduleDetails");
		selectSql.append(" Where FinReference =:FinReference AND SchDate=:SchDate ");

		logger.debug("selectSql: " + selectSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(schdDetail);
		RowMapper<FinanceScheduleDetail> typeRowMapper = ParameterizedBeanPropertyRowMapper.newInstance(FinanceScheduleDetail.class);

		try {
			schdDetail = this.namedParameterJdbcTemplate.queryForObject(selectSql.toString(), beanParameters, typeRowMapper);
		} catch (EmptyResultDataAccessException e) {
			schdDetail = null;
		}
		logger.debug("Leaving");
		return schdDetail;
    }
	
	@Override
	public List<ScheduleMapDetails> getFinSchdDetailTermByDates(List<String> finReferences, Date schdFromdate, Date schdTodate) {
		logger.debug("Entering");
		Map<String,List<String>> map=new HashMap<String, List<String>>();
		map.put("FinReference", finReferences);
		
		StringBuilder selectSql = new StringBuilder("Select T1.FinReference, MIN(T1.SchDate) AS schdFromDate, MAX(T1.SchDate) AS schdToDate ");
		selectSql.append("FROM FinScheduleDetails T1 INNER JOIN  FinanceMain T2 ON T1.FinReference = T2.FinReference ");
		selectSql.append(" WHERE ");
		selectSql.append(" T1.FinReference IN( :FinReference ) AND ( T1.SchDate >= " );
		selectSql.append("'"+schdFromdate+"'");
		if(schdTodate != null){
		selectSql.append(" AND T1.SchDate <= " );
		selectSql.append("'"+schdTodate+"'");
		}
		selectSql.append(" ) GROUP BY T1.FinReference");
		RowMapper<ScheduleMapDetails> typeRowMapper = ParameterizedBeanPropertyRowMapper.newInstance(ScheduleMapDetails.class);
		logger.debug("selectSql: " + selectSql.toString());
		
		logger.debug("Leaving");
		return this.namedParameterJdbcTemplate.query(selectSql.toString(), map, typeRowMapper);		
		
	}

	/**
	 * Method for Executing list of Queries and build data of object with Amount Codes
	 * 
	 * @throws InvocationTargetException
	 * @throws IllegalAccessException
	 */
	/*
	 * @Override public AmountCodeDetail getAmountCodeDetails(AmountCodeDetail amountCodeDetail) {
	 * 
	 * SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(amountCodeDetail);
	 * 
	 * //Execution QueryCode #1--Current Period StringBuilder selectSql = new StringBuilder();
	 * selectSql.append(" SELECT SUM(NoOfDays) AS CPNoOfDays, SUM(ProfitCalc) AS CPProfitCalc,");
	 * selectSql.append(" SUM(ProfitSchd) AS CPProfitSchd, SUM(PrincipalSchd) AS CPPrincipalSchd,");
	 * selectSql.append(" SUM(RepayAmount) AS CPRepayAmount, SUM(DisbAmount) AS CPDisbAmount, ");
	 * selectSql.append(" SUM(DownPaymentAmount) AS CPDownPaymentAmount, SUM(CpzAmount) AS CPCpzAmount,");
	 * selectSql.append(" SUM(DefRepaySchd) AS CPDefRepaySchd, SUM(DefProfitSchd) AS CPDefProfitSchd,");
	 * selectSql.append(" SUM(DefPrincipalSchd) AS CPDefPrincipalSchd, SUM(SchdPftPaid) AS CPSchdPftPaid,");
	 * selectSql.append(
	 * " SUM(SchdPriPaid) AS CPSchdPriPaid, SUM(DefSchdPftPaid) AS CPDefSchdPftPaid, SUM(DefSchdPriPaid) AS CPDefSchdPriPaid "
	 * ); selectSql.append(" FROM FinScheduleDetails_Work ");
	 * selectSql.append(" WHERE FinReference = :FinReference AND SchDate > :LastRepayPftDate");
	 * selectSql.append(" AND SchDate <= :NextRepayPftDate ");
	 * 
	 * logger.debug("selectSql: " + selectSql.toString()); RowMapper<CPDetail> typeRowMapper1 =
	 * ParameterizedBeanPropertyRowMapper.newInstance(CPDetail.class); CPDetail cpDetail; try { cpDetail =
	 * this.namedParameterJdbcTemplate.queryForObject(selectSql.toString(), beanParameters, typeRowMapper1); } catch
	 * (Exception e) { cpDetail = new CPDetail(); } BeanUtils.copyProperties(cpDetail, amountCodeDetail);
	 * 
	 * //Execution QueryCode #2--Till End of Previous period selectSql = new StringBuilder();
	 * selectSql.append(" SELECT SUM(NoOfDays) AS TPPNoOfDays, SUM(ProfitCalc) AS TPPProfitCalc,");
	 * selectSql.append(" SUM(ProfitSchd) AS TPPProfitSchd, SUM(PrincipalSchd) AS TPPPrincipalSchd,");
	 * selectSql.append(" SUM(RepayAmount) AS TPPRepayAmount, SUM(DisbAmount) AS TPPDisbAmount, ");
	 * selectSql.append(" SUM(DownPaymentAmount) AS TPPDownPaymentAmount, SUM(CpzAmount) AS TPPCpzAmount,");
	 * selectSql.append(" SUM(DefRepaySchd) AS TPPDefRepaySchd, SUM(DefProfitSchd) AS TPPDefProfitSchd,");
	 * selectSql.append(" SUM(DefPrincipalSchd) AS TPPDefPrincipalSchd, SUM(SchdPftPaid) AS TPPSchdPftPaid,");
	 * selectSql.append(
	 * " SUM(SchdPriPaid) AS TPPSchdPriPaid, SUM(DefSchdPftPaid) AS TPDefSchdPftPaid, SUM(DefSchdPriPaid) AS TPDefSchdPriPaid "
	 * ); selectSql.append(" FROM FinScheduleDetails_Work ");
	 * selectSql.append(" WHERE FinReference = :FinReference AND SchDate <= :LastRepayPftDate");
	 * 
	 * logger.debug("selectSql: " + selectSql.toString()); RowMapper<TEPPDetail> typeRowMapper2 =
	 * ParameterizedBeanPropertyRowMapper.newInstance(TEPPDetail.class); TEPPDetail teppDetail; try { teppDetail =
	 * this.namedParameterJdbcTemplate.queryForObject(selectSql.toString(), beanParameters, typeRowMapper2); } catch
	 * (Exception e) { teppDetail = new TEPPDetail(); } BeanUtils.copyProperties(teppDetail, amountCodeDetail);
	 * 
	 * //Execution QueryCode #3--Till End selectSql = new StringBuilder();
	 * selectSql.append(" SELECT SUM(NoOfDays) AS TENoOfDays, SUM(ProfitCalc) AS TEProfitCalc,");
	 * selectSql.append(" SUM(ProfitSchd) AS TEProfitSchd, SUM(PrincipalSchd) AS TEPrincipalSchd,");
	 * selectSql.append(" SUM(RepayAmount) AS TERepayAmount, SUM(DisbAmount) AS TEDisbAmount, ");
	 * selectSql.append(" SUM(DownPaymentAmount) AS TEDownPaymentAmount, SUM(CpzAmount) AS TECpzAmount,");
	 * selectSql.append(" SUM(DefRepaySchd) AS TEDefRepaySchd, SUM(DefProfitSchd) AS TEDefProfitSchd,");
	 * selectSql.append(" SUM(DefPrincipalSchd) AS TEDefPrincipalSchd, SUM(SchdPftPaid) AS TESchdPftPaid,");
	 * selectSql.append(
	 * " SUM(SchdPriPaid) AS TESchdPriPaid, SUM(DefSchdPftPaid) AS TEDefSchdPftPaid, SUM(DefSchdPriPaid) AS TEDefSchdPriPaid "
	 * ); selectSql.append(" FROM FinScheduleDetails_Work "); selectSql.append(" WHERE FinReference = :FinReference ");
	 * 
	 * logger.debug("selectSql: " + selectSql.toString()); RowMapper<TEDetail> typeRowMapper3 =
	 * ParameterizedBeanPropertyRowMapper.newInstance(TEDetail.class); TEDetail teDetail; try { teDetail =
	 * this.namedParameterJdbcTemplate.queryForObject(selectSql.toString(), beanParameters, typeRowMapper3); } catch
	 * (Exception e) { teDetail = new TEDetail(); } BeanUtils.copyProperties(teDetail, amountCodeDetail);
	 * 
	 * //Execution QueryCode #4--Review Period selectSql = new StringBuilder();
	 * selectSql.append(" SELECT SUM(ProfitCalc) AS RPProfitCalc, SUM(ProfitSchd) AS RPProfitSchd,");
	 * selectSql.append(" SUM(SchdPftPaid) AS RPSchdPftPaid, SUM(DefSchdPftPaid) AS RPDefSchdPftPaid ");
	 * selectSql.append(" FROM FinScheduleDetails_Work ");
	 * selectSql.append(" WHERE FinReference = :FinReference AND SchDate >  :LastRepayRvwDate ");
	 * selectSql.append(" AND SchDate <= :NextRepayRvwDate ");
	 * 
	 * logger.debug("selectSql: " + selectSql.toString()); RowMapper<RPDetail> typeRowMapper4 =
	 * ParameterizedBeanPropertyRowMapper.newInstance(RPDetail.class); RPDetail rpDetail; try { rpDetail =
	 * this.namedParameterJdbcTemplate.queryForObject(selectSql.toString(), beanParameters, typeRowMapper4); } catch
	 * (Exception e) { rpDetail = new RPDetail(); } BeanUtils.copyProperties(rpDetail, amountCodeDetail);
	 * 
	 * //Execution QueryCode #5--Review Period Till Date selectSql = new StringBuilder();
	 * selectSql.append(" SELECT SUM(ProfitCalc) AS RPPProfitCalc, SUM(ProfitSchd) AS RPPProfitSchd,");
	 * selectSql.append(" SUM(SchdPftPaid) AS RPPSchdPftPaid, SUM(DefSchdPftPaid) AS RPPDefSchdPftPaid  ");
	 * selectSql.append(" FROM FinScheduleDetails_Work ");
	 * selectSql.append(" WHERE FinReference = :FinReference AND SchDate > :LastRepayRvwDate ");
	 * selectSql.append(" AND SchDate <= :NextRepayPftDate ");
	 * 
	 * logger.debug("selectSql: " + selectSql.toString()); RowMapper<RPTDDetail> typeRowMapper5 =
	 * ParameterizedBeanPropertyRowMapper.newInstance(RPTDDetail.class); RPTDDetail rptdDetail; try { rptdDetail =
	 * this.namedParameterJdbcTemplate.queryForObject(selectSql.toString(), beanParameters, typeRowMapper5); } catch
	 * (Exception e) { rptdDetail = new RPTDDetail(); } BeanUtils.copyProperties(rptdDetail, amountCodeDetail);
	 * 
	 * return amountCodeDetail; }
	 */

	/**
	 * Method for get the count of FinScheduleDetails records depend on condition
	 * 
	 * @param finReference
	 * @param schdDate
	 * @return
	 */
	public int getFrqDfrCount(String finReference, String schdDate) {
		logger.debug("Entering");
		StringBuilder selectQry = new StringBuilder(" Select Count(FinReference) ");
		selectQry.append(" From FinScheduleDetails ");
		selectQry.append(" Where FinReference = '");
		selectQry.append(finReference);
		selectQry.append("' AND Defered = '1'");
		logger.debug("selectSql: " + selectQry.toString());

		int recordCount = this.namedParameterJdbcTemplate.getJdbcOperations().queryForInt(
		        selectQry.toString());
		logger.debug("Leaving");
		return recordCount;
	}

	@Override
	public PaymentDetails getPaymentDetails(String finReference, Date date, String type) {
		logger.debug("Entering");
		PaymentDetails paymentDetails = new PaymentDetails();
		paymentDetails.setFinReference(finReference);
		PaymentDetails details = new PaymentDetails();
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(paymentDetails);

		StringBuilder selectSql = new StringBuilder();
		selectSql.append("Select sum(DisbAmount) as DisbAmount, sum(DownPaymentAmount) as DownPaymentAmount, ");
		selectSql.append("sum(CpzAmount) as CpzAmount from FinScheduleDetails");
		selectSql.append(StringUtils.trimToEmpty(type));
		selectSql.append(" where SchDate <= '" + date + "' and FinReference = :FinReference");

		logger.debug("selectSql: " + selectSql.toString());
		RowMapper<PaymentDetails> typeRowMapper = ParameterizedBeanPropertyRowMapper
		        .newInstance(PaymentDetails.class);

		try {
			details = this.namedParameterJdbcTemplate.queryForObject(selectSql.toString(),
			        beanParameters, typeRowMapper);
			paymentDetails.setDisbAmount(details.getDisbAmount());
			paymentDetails.setDownPaymentAmount(details.getDownPaymentAmount());
			paymentDetails.setCpzAmount(details.getCpzAmount());
		} catch (Exception e) {
			e.printStackTrace();
		}
		StringBuilder selectSql2 = new StringBuilder();
		selectSql2.append("select sum(SchdPriPaid) as SchdPriPaid, sum(DefSchdPriPaid) as DefSchdPriPaid, ");
		selectSql2.append(" sum(SchdPftPaid) as SchdPftPaid, sum(DefSchdPftPaid) AS DefSchdPftPaid " );
		selectSql2.append(" from FinScheduleDetails_View where  FinReference = :FinReference");
		logger.debug("selectSql: " + selectSql2.toString());
		RowMapper<PaymentDetails> typeRowMapper1 = ParameterizedBeanPropertyRowMapper
		        .newInstance(PaymentDetails.class);
		try {
			details = this.namedParameterJdbcTemplate.queryForObject(selectSql2.toString(),
			        beanParameters, typeRowMapper1);
			paymentDetails.setSchdPriPaid(details.getSchdPriPaid());
			paymentDetails.setDefPrincipal(details.getDefPrincipal());
			paymentDetails.setSchdPftPaid(details.getSchdPftPaid());

		} catch (Exception e) {
			e.printStackTrace();
		}
		logger.debug("Leaving");
		return paymentDetails;
	}

	/**
	 * Method for Maintaining List of Schedule Details in Work table by User
	 * 
	 * @param financeScheduleDetail
	 * @param financeScheduleDetails
	 */
	@Override
	public void maintainWorkSchedules(String finReference, long userId,
	        List<FinanceScheduleDetail> financeScheduleDetails) {
		logger.debug("Entering");
		deleteFromWork(finReference, userId);
		for (int i = 0; i < financeScheduleDetails.size(); i++) {
			FinanceScheduleDetail detail = financeScheduleDetails.get(i);
			detail.setFinReference(finReference);
			detail.setLastMntBy(userId);
			save(detail, "_Work", false);
		}
		logger.debug("Leaving");
	}

	/**
	 * Method to get Schedule Details based on finrefernce and valuedate for OverDueRecovery
	 * 
	 * @param finreference
	 *            (String)
	 * @param financeScheduleDetails
	 */
	@Override
	public OverdueChargeRecovery getODCRecoveryDetails(OverdueChargeRecovery ocr) {
		logger.debug("Entering");
		OverdueChargeRecovery recovery = new OverdueChargeRecovery();
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(ocr);
		//Date dateValueDate = DateUtility.getDBDate(SystemParameterDetails.getSystemParameterValue(PennantConstants.APP_DATE_VALUE).toString());
		StringBuilder selectSql = new StringBuilder("Select ");
		selectSql.append(" (SUM(PrincipalSchd) + SUM(DefPrincipalSchd) - SUM (SchdPriPaid) - SUM(DefSchdPriPaid)) as lovDescCurSchPriDue,");
		selectSql.append(" (SUM(ProfitSchd) + SUM(DefProfitSchd) - SUM (SchdPftPaid) - SUM(DefSchdPftPaid)) as lovDescCurSchPftDue");
		selectSql.append(" From FinScheduleDetails");
		//selectSql.append(" WHERE SCHDATE <= '" + dateValueDate + "' AND FinReference ='"+ ocr.getFinReference() + "'");
		selectSql.append(" WHERE FinReference ='"+ ocr.getFinReference() + "'");
		logger.debug("selectSql: " + selectSql.toString());

		RowMapper<OverdueChargeRecovery> typeRowMapper = ParameterizedBeanPropertyRowMapper
		        .newInstance(OverdueChargeRecovery.class);
		try {
			recovery = this.namedParameterJdbcTemplate.queryForObject(selectSql.toString(),
			        beanParameters, typeRowMapper);
			ocr.setLovDescCurSchPriDue(recovery.getLovDescCurSchPriDue());
			ocr.setLovDescCurSchPftDue(recovery.getLovDescCurSchPftDue());
		} catch (Exception e) {
			logger.error(e);
			e.printStackTrace();
		}
		logger.debug("Leaving");
		return ocr;
	}

	/**
	 * Method for fetch Suspense Amount for Particular Finance
	 */
	@Override
	public BigDecimal getSuspenseAmount(String finReference, Date dateValueDate) {
		logger.debug("Entering");

		FinanceScheduleDetail financeScheduleDetail = new FinanceScheduleDetail();
		financeScheduleDetail.setFinReference(finReference);
		financeScheduleDetail.setSchDate(dateValueDate);

		// Get Profit calculated - Paid Profits
		StringBuilder selectSql = new StringBuilder(" SELECT ");
		selectSql.append(" SUM(ProfitCalc - SchdPftPaid - DefSchdPftPaid) ");
		selectSql.append(" FROM finscheduleDetails where FinReference = :FinReference");

		logger.debug("selectSql: " + selectSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(
		        financeScheduleDetail);

		BigDecimal suspAmount = BigDecimal.ZERO;
		suspAmount = this.namedParameterJdbcTemplate.queryForObject(selectSql.toString(),
		        beanParameters, BigDecimal.class);

		selectSql = new StringBuilder(" SELECT SUM(CpzAmount) ");
		selectSql.append(" FROM finscheduleDetails ");
		selectSql.append(" WHERE FinReference = :FinReference AND SchDate <= :SchDate ");

		logger.debug("selectSql: " + selectSql.toString());
		BigDecimal cpzTillNow = BigDecimal.ZERO;
		cpzTillNow = this.namedParameterJdbcTemplate.queryForObject(selectSql.toString(),
		        beanParameters, BigDecimal.class);

		suspAmount = suspAmount.subtract(cpzTillNow);

		logger.debug("Leaving");
		return suspAmount;
	}

	/**
	 * Method for preparing Finance Summary Details for Enquiry
	 */
	@Override
	public FinanceSummary getFinanceSummaryDetails(FinanceSummary summary) {
		logger.debug("Entering");

		StringBuilder selectSql = new StringBuilder(" SELECT TOP 1 fm.FinReference , ");
		selectSql.append(" (SELECT SUM(disbAmount) FROM FinDisbursementDetails WHERE FinReference = :FinReference) as TotalDisbursement, " );
		selectSql.append(" (SELECT SUM(PrincipalSchd) FROM FinScheduleDetails WHERE FinReference = :FinReference ) as TotalPriSchd, " );
		selectSql.append(" (SELECT SUM(ProfitSchd) FROM FinScheduleDetails WHERE FinReference = :FinReference )  as TotalPftSchd, " );
		selectSql.append(" (SELECT SUM(PrincipalSchd) FROM FinScheduleDetails WHERE FinReference = :FinReference " );
		selectSql.append(" AND SchDate <= :NextSchDate ) as PrincipalSchd, " );
		selectSql.append(" (SELECT SUM(ProfitSchd) FROM FinScheduleDetails WHERE FinReference = :FinReference " );
		selectSql.append(" AND SchDate <= :NextSchDate ) as ProfitSchd, " );
		selectSql.append(" (SELECT SUM(SchdPftPaid) FROM FinScheduleDetails WHERE FinReference = :FinReference " );
		selectSql.append(" AND SchDate <= :NextSchDate ) as SchdPftPaid, " );
		selectSql.append(" (SELECT SUM(SchdPriPaid) FROM FinScheduleDetails WHERE FinReference = :FinReference " );
		selectSql.append(" AND SchDate <= :NextSchDate ) as SchdPriPaid, " );
		selectSql.append(" fm.downpayment as totalDownPayment, fm.totalcpz as totalCpz " );
		selectSql.append(" FROM financemain as fm, FinScheduleDetails AS fsd " );
		selectSql.append(" WHERE fm.finreference = :FinReference AND fm.finreference = fsd.finreference " );
		//selectSql.append(" and SchDate < :SchDate " );

		logger.debug("selectSql: " + selectSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(summary);
		RowMapper<FinanceSummary> typeRowMapper = ParameterizedBeanPropertyRowMapper.newInstance(FinanceSummary.class);

		try {
			summary = this.namedParameterJdbcTemplate.queryForObject(selectSql.toString(), beanParameters, typeRowMapper);
		} catch (Exception e) {
			logger.error(e);
			//summary = null;
		}
		logger.debug("Leaving");
		return summary;
	}

	@Override
    public BigDecimal getTotalRepayAmount(String finReference) {
		logger.debug("Entering");

		FinanceScheduleDetail financeScheduleDetail = new FinanceScheduleDetail();
		financeScheduleDetail.setFinReference(finReference);

		// Get Sum of Total Payment Amount(profits and Principals)
		StringBuilder selectSql = new StringBuilder(" select sum(ProfitSchd - SchdPftPaid + PrincipalSchd - SchdPriPaid + " );
		selectSql.append(" DefProfitSchd-DefSchdPftPaid + DefPrincipalSchd - DefSchdPriPaid) " );
		selectSql.append(" from FinScheduleDetails  where FinReference = :FinReference ");

		logger.debug("selectSql: " + selectSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(
		        financeScheduleDetail);

		BigDecimal repayAmount = BigDecimal.ZERO;
		repayAmount = this.namedParameterJdbcTemplate.queryForObject(selectSql.toString(),
		        beanParameters, BigDecimal.class);

		logger.debug("Leaving");
		return repayAmount;
	
    }

	@Override
    public BigDecimal getTotalUnpaidPriAmount(String finReference) {
		logger.debug("Entering");

		FinanceScheduleDetail financeScheduleDetail = new FinanceScheduleDetail();
		financeScheduleDetail.setFinReference(finReference);

		// Get Sum of Total Payment Amount(profits and Principals)
		StringBuilder selectSql = new StringBuilder(" select sum(PrincipalSchd - SchdPriPaid + " );
		selectSql.append(" DefPrincipalSchd - DefSchdPriPaid - WriteoffPrincipal) " );
		selectSql.append(" from FinScheduleDetails  where FinReference = :FinReference ");

		logger.debug("selectSql: " + selectSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(
		        financeScheduleDetail);

		BigDecimal priAmt = BigDecimal.ZERO;
		priAmt = this.namedParameterJdbcTemplate.queryForObject(selectSql.toString(),
		        beanParameters, BigDecimal.class);

		logger.debug("Leaving");
		return priAmt;
    }

	@Override
    public BigDecimal getTotalUnpaidPftAmount(String finReference) {
		logger.debug("Entering");

		FinanceScheduleDetail financeScheduleDetail = new FinanceScheduleDetail();
		financeScheduleDetail.setFinReference(finReference);

		// Get Sum of Total Payment Amount(profits and Principals)
		StringBuilder selectSql = new StringBuilder(" select sum(ProfitSchd - SchdPftPaid +  " );
		selectSql.append(" DefProfitSchd-DefSchdPftPaid - WriteoffProfit ) " );
		selectSql.append(" from FinScheduleDetails  where FinReference = :FinReference ");

		logger.debug("selectSql: " + selectSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(
		        financeScheduleDetail);

		BigDecimal pftAmt = BigDecimal.ZERO;
		pftAmt = this.namedParameterJdbcTemplate.queryForObject(selectSql.toString(),
		        beanParameters, BigDecimal.class);

		logger.debug("Leaving");
		return pftAmt;
    }
	
	@Override
    public FinanceWriteoff getWriteoffTotals(String finReference) {
		logger.debug("Entering");

		FinanceWriteoff financeWriteoff = new FinanceWriteoff();
		financeWriteoff.setFinReference(finReference);

		// Get Sum of Total Payment Amount(profits and Principals)
		StringBuilder selectSql = new StringBuilder(" select sum(WriteoffPrincipal) AS WrittenoffPri, sum(WriteoffProfit) AS WrittenoffPft,  " );
		selectSql.append(" sum(PrincipalSchd - SchdPriPaid + DefPrincipalSchd - DefSchdPriPaid - WriteoffPrincipal) AS UnPaidSchdPri, " );
		selectSql.append(" sum(ProfitSchd - SchdPftPaid + DefProfitSchd-DefSchdPftPaid - WriteoffProfit) AS UnPaidSchdPft " );
		selectSql.append(" from FinScheduleDetails  where FinReference = :FinReference ");

		logger.debug("selectSql: " + selectSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(financeWriteoff);
		RowMapper<FinanceWriteoff> typeRowMapper = ParameterizedBeanPropertyRowMapper.newInstance(FinanceWriteoff.class);

		try{
			financeWriteoff = this.namedParameterJdbcTemplate.queryForObject(selectSql.toString(),
					beanParameters, typeRowMapper);
		} catch (EmptyResultDataAccessException e) {
			financeWriteoff = null;
		}

		logger.debug("Leaving");
		return financeWriteoff;
    }

	@Override
	public Date getFirstRepayDate(String finReference) {
		logger.debug("Entering");

		FinanceScheduleDetail financeScheduleDetail = new FinanceScheduleDetail();
		financeScheduleDetail.setFinReference(finReference);


		StringBuilder selectSql = new StringBuilder();		
		selectSql.append("  select min(SchDate) from FinScheduleDetails");
		selectSql.append(" where FinReference = :FinReference AND ");
		selectSql.append(" (RepayOnSchDate = '1' OR DeferedPay = '1' OR (PftOnSchDate = '1' AND RepayAmount > 0)) ");

		logger.debug("selectSql: " + selectSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(financeScheduleDetail);

		Date firstRepayDate = null;
		firstRepayDate = this.namedParameterJdbcTemplate.queryForObject(selectSql.toString(),beanParameters, Date.class);

		if(firstRepayDate == null) {
			selectSql = new StringBuilder();		
			selectSql.append("  select min(SchDate) from FinScheduleDetails_TEMP");
			selectSql.append(" where FinReference = :FinReference AND ");
			selectSql.append(" (RepayOnSchDate = '1' OR DeferedPay = '1' OR (PftOnSchDate = '1' AND RepayAmount > 0)) ");

			firstRepayDate = this.namedParameterJdbcTemplate.queryForObject(selectSql.toString(),beanParameters, Date.class);
		}

		logger.debug("Leaving");
		return firstRepayDate;
	}
	
	/**
	 * Method for Fetching Account hold Details on Future installment Amounts grouping by Repayments Account
	 */
	@Override
    public List<AccountHoldStatus> getFutureInstAmtByRepayAc(Date dateValueDate, Date futureDate) {
		logger.debug("Entering");
		
		AccountHoldStatus holdStatus = new AccountHoldStatus();
		holdStatus.setValueDate(dateValueDate);
		holdStatus.setFutureDate(futureDate);

		StringBuilder selectSql = new StringBuilder();
		selectSql.append(" SELECT F.RepayAccountId AS Account,  ");
		selectSql.append(" SUM(S.PrincipalSchd - S.SchdPriPaid + S.ProfitSchd - S.SchdPftPaid + S.DefPrincipalSchd - S.DefSchdPriPaid + S.DefProfitSchd  -S.DefSchdPftPaid) AS CurODAmount FROM FinScheduleDetails S ");
		selectSql.append(" INNER JOIN FinanceMain F ON S.FinReference = F.FinReference ");
		selectSql.append(" INNER JOIN RMTFinanceTypes T ON F.FinType = T.FinType  ");
		selectSql.append(" WHERE S.SchDate > :ValueDate AND  S.SchDate <= :FutureDate  ");
		selectSql.append(" AND T.FinDivision = 'RETAIL' AND F.FinRepayMethod = 'AUTO' AND F.FinIsActive = '1' AND ");
		selectSql.append("(S.PrincipalSchd - S.SchdPriPaid + S.ProfitSchd - S.SchdPftPaid + S.DefPrincipalSchd - S.DefSchdPriPaid + S.DefProfitSchd  -S.DefSchdPftPaid) > 0 GROUP BY F.RepayAccountId " );

		logger.debug("selectSql: " + selectSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(holdStatus);
		RowMapper<AccountHoldStatus> typeRowMapper = ParameterizedBeanPropertyRowMapper.newInstance(AccountHoldStatus.class);

		logger.debug("Leaving");
		return this.namedParameterJdbcTemplate.query(selectSql.toString(), beanParameters, typeRowMapper);
    }


}