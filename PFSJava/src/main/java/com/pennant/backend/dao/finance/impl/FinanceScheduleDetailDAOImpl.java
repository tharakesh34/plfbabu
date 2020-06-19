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
 * FileName    		:  FinanceScheduleDetailDAOImpl.java                                    * 	  
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
package com.pennant.backend.dao.finance.impl;

import java.math.BigDecimal;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.PreparedStatementSetter;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSourceUtils;
import org.springframework.jdbc.core.simple.ParameterizedBeanPropertyRowMapper;

import com.pennant.app.constants.ImplementationConstants;
import com.pennant.app.util.SysParamUtil;
import com.pennant.backend.dao.finance.FinanceScheduleDetailDAO;
import com.pennant.backend.model.finance.AccountHoldStatus;
import com.pennant.backend.model.finance.FinanceMain;
import com.pennant.backend.model.finance.FinanceScheduleDetail;
import com.pennant.backend.model.finance.FinanceSummary;
import com.pennant.backend.model.finance.FinanceWriteoff;
import com.pennant.backend.model.finance.ScheduleMapDetails;
import com.pennant.eod.constants.EodConstants;
import com.pennanttech.pennapps.core.App;
import com.pennanttech.pennapps.core.App.Database;
import com.pennanttech.pennapps.core.ConcurrencyException;
import com.pennanttech.pennapps.core.DependencyFoundException;
import com.pennanttech.pennapps.core.jdbc.BasicDao;
import com.pennanttech.pennapps.core.resource.Literal;

/**
 * DAO methods implementation for the <b>WIFFinanceScheduleDetail model</b> class.<br>
 */
public class FinanceScheduleDetailDAOImpl extends BasicDao<FinanceScheduleDetail> implements FinanceScheduleDetailDAO {
	private static Logger logger = Logger.getLogger(FinanceScheduleDetailDAOImpl.class);

	public FinanceScheduleDetailDAOImpl() {
		super();
	}

	@Override
	public FinanceScheduleDetail getFinanceScheduleDetailById(final String id, final Date schdDate, String type,
			boolean isWIF) {

		StringBuilder sql = getScheduleDetailQuery(type, isWIF);
		sql.append(" Where FinReference = ? and SchDate = ?");
		logger.debug(Literal.SQL + sql.toString());

		RowMapper<FinanceScheduleDetail> rowMapper = new ScheduleDetailRowMapper(isWIF);

		try {
			return this.jdbcTemplate.getJdbcOperations().queryForObject(sql.toString(), new Object[] { id, schdDate },
					rowMapper);
		} catch (Exception e) {
			logger.warn(Literal.EXCEPTION, e);
		}

		return null;
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
		if (logKey != 0) {
			deleteSql.append(" AND LogKey =:LogKey");
		}
		logger.debug("deleteSql: " + deleteSql.toString());

		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(wIFFinanceScheduleDetail);
		this.jdbcTemplate.update(deleteSql.toString(), beanParameters);
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

	@Override
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

		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(wIFFinanceScheduleDetail);
		try {
			recordCount = this.jdbcTemplate.update(deleteSql.toString(), beanParameters);
			if (recordCount <= 0) {
				throw new ConcurrencyException();
			}
		} catch (DataAccessException e) {
			throw new DependencyFoundException(e);
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
		insertSql.append(" CpzOnSchDate, RepayOnSchDate, RvwOnSchDate, DisbOnSchDate,");
		insertSql.append(" DownpaymentOnSchDate, BalanceForPftCal, BaseRate, SplRate, MrgRate, ActRate, NoOfDays,");
		insertSql
				.append(" CalOnIndRate, DayFactor, ProfitCalc, ProfitSchd, PrincipalSchd, RepayAmount, ProfitBalance,");
		insertSql.append(" DisbAmount, DownPaymentAmount, CpzAmount, CpzBalance, OrgPft , OrgPri, OrgEndBal, ");
		insertSql.append(" ClosingBalance, ProfitFraction, PrvRepayAmount, OrgPlanPft,");
		insertSql.append(" SchdPriPaid, SchdPftPaid, SchPriPaid, SchPftPaid, Specifier,");
		insertSql.append(" CalculatedRate,FeeChargeAmt,InsuranceAmt,");
		insertSql.append(" InstNumber, BpiOrHoliday, FrqDate, RecalLock,");
		insertSql.append(" FeeSchd , SchdFeePaid , SchdFeeOS ,InsSchd, SchdInsPaid,");
		insertSql.append(" AdvBaseRate , AdvMargin , AdvPftRate , AdvCalRate , AdvProfit , AdvRepayAmount,");
		insertSql.append(" SuplRent , IncrCost ,SuplRentPaid , IncrCostPaid , TDSAmount, TDSPaid, PftDaysBasis,");
		if (!isWIF) {
			insertSql.append(" RefundOrWaiver, EarlyPaid, EarlyPaidBal , WriteoffPrincipal, WriteoffProfit,");
			insertSql.append(
					" WriteoffIns , WriteoffIncrCost, WriteoffSuplRent, WriteoffSchFee, PartialPaidAmt, SchdPftWaiver,");
		}
		insertSql.append(" DefSchdDate, SchdMethod,  ");
		insertSql.append(" RolloverOnSchDate , RolloverAmount, RolloverAmountPaid, InsuranceAmt)");
		insertSql.append(" Values(:FinReference, :SchDate, :SchSeq, :PftOnSchDate,");
		insertSql.append(" :CpzOnSchDate, :RepayOnSchDate, :RvwOnSchDate, :DisbOnSchDate, ");
		insertSql.append(
				" :DownpaymentOnSchDate, :BalanceForPftCal, :BaseRate, :SplRate,:MrgRate, :ActRate, :NoOfDays,");
		insertSql.append(
				" :CalOnIndRate,:DayFactor, :ProfitCalc, :ProfitSchd, :PrincipalSchd, :RepayAmount, :ProfitBalance,");
		insertSql.append(" :DisbAmount, :DownPaymentAmount, :CpzAmount, :CpzBalance, :OrgPft , :OrgPri, :OrgEndBal, ");
		insertSql.append(" :ClosingBalance, :ProfitFraction, :PrvRepayAmount, :OrgPlanPft,");
		insertSql.append(" :SchdPriPaid, :SchdPftPaid, :SchPriPaid, :SchPftPaid, :Specifier,");
		insertSql.append(" :CalculatedRate,:FeeChargeAmt,:InsuranceAmt, ");
		insertSql.append(" :InstNumber, :BpiOrHoliday, :FrqDate, :RecalLock,");
		insertSql.append(" :FeeSchd , :SchdFeePaid , :SchdFeeOS , :InsSchd, :SchdInsPaid,");
		insertSql.append(" :AdvBaseRate , :AdvMargin , :AdvPftRate , :AdvCalRate , :AdvProfit , :AdvRepayAmount,");
		insertSql.append(
				" :SuplRent , :IncrCost , :SuplRentPaid , :IncrCostPaid , :TDSAmount, :TDSPaid, :PftDaysBasis, ");
		if (!isWIF) {
			insertSql.append(" :RefundOrWaiver, :EarlyPaid, :EarlyPaidBal, :WriteoffPrincipal, :WriteoffProfit,");
			insertSql.append(
					" :WriteoffIns , :WriteoffIncrCost, :WriteoffSuplRent, :WriteoffSchFee, :PartialPaidAmt, :SchdPftWaiver,");
		}
		insertSql.append("  :DefSchdDate, :SchdMethod, ");
		insertSql.append(" :RolloverOnSchDate , :RolloverAmount, :RolloverAmountPaid, :InsuranceAmt)");

		logger.debug("insertSql: " + insertSql.toString());

		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(wIFFinanceScheduleDetail);
		this.jdbcTemplate.update(insertSql.toString(), beanParameters);
		logger.debug("Leaving");
		return wIFFinanceScheduleDetail.getId();
	}

	public void saveList(List<FinanceScheduleDetail> financeScheduleDetail, String type, boolean isWIF) {
		logger.debug("Entering");

		StringBuilder insertSql = new StringBuilder("Insert Into ");

		if (isWIF) {
			insertSql.append(" WIFFinScheduleDetails");
		} else {
			insertSql.append(" FinScheduleDetails");
		}

		insertSql.append(StringUtils.trimToEmpty(type));
		insertSql.append(" (FinReference, SchDate, SchSeq, PftOnSchDate,");
		insertSql.append(" CpzOnSchDate, RepayOnSchDate, RvwOnSchDate, DisbOnSchDate,");
		insertSql.append(" DownpaymentOnSchDate, BalanceForPftCal, BaseRate, SplRate, MrgRate, ActRate, NoOfDays,");
		insertSql
				.append(" CalOnIndRate, DayFactor, ProfitCalc, ProfitSchd, PrincipalSchd, RepayAmount, ProfitBalance,");
		insertSql.append(
				" DisbAmount, DownPaymentAmount, CpzAmount, CpzBalance, OrgPft , OrgPri, OrgEndBal,OrgPlanPft, ");

		insertSql.append(" ClosingBalance, ProfitFraction, PrvRepayAmount, CalculatedRate,FeeChargeAmt,InsuranceAmt,");
		insertSql.append(" FeeSchd , SchdFeePaid , SchdFeeOS ,InsSchd, SchdInsPaid,");
		insertSql.append(" AdvBaseRate , AdvMargin , AdvPftRate , AdvCalRate , AdvProfit , AdvRepayAmount,");
		insertSql.append(" SuplRent , IncrCost ,SuplRentPaid , IncrCostPaid , TDSAmount, TDSPaid, PftDaysBasis, ");
		if (!isWIF) {
			insertSql.append(" RefundOrWaiver, EarlyPaid, EarlyPaidBal,WriteoffPrincipal, WriteoffProfit, ");
			insertSql.append(
					" WriteoffIns , WriteoffIncrCost, WriteoffSuplRent, WriteoffSchFee, PartialPaidAmt, PresentmentId, TDSApplicable, SchdPftWaiver,");
			if (type.contains("Log")) {
				insertSql.append(" LogKey , ");
			}
		}
		insertSql.append(" SchdPriPaid, SchdPftPaid, SchPriPaid, SchPftPaid, Specifier,");
		insertSql.append(" DefSchdDate, SchdMethod, ");
		insertSql.append(" InstNumber, BpiOrHoliday, FrqDate, RecalLock,");
		insertSql.append(" RolloverOnSchDate , RolloverAmount, RolloverAmountPaid)");
		insertSql.append(" Values(:FinReference, :SchDate, :SchSeq, :PftOnSchDate,");
		insertSql.append(" :CpzOnSchDate, :RepayOnSchDate, :RvwOnSchDate, :DisbOnSchDate, ");
		insertSql.append(
				" :DownpaymentOnSchDate, :BalanceForPftCal, :BaseRate, :SplRate,:MrgRate, :ActRate, :NoOfDays,");
		insertSql.append(
				" :CalOnIndRate,:DayFactor, :ProfitCalc, :ProfitSchd, :PrincipalSchd, :RepayAmount, :ProfitBalance,");
		insertSql.append(
				" :DisbAmount, :DownPaymentAmount, :CpzAmount, :CpzBalance, :OrgPft , :OrgPri, :OrgEndBal,:OrgPlanPft, ");
		insertSql.append(
				" :ClosingBalance, :ProfitFraction, :PrvRepayAmount, :CalculatedRate,:FeeChargeAmt,:InsuranceAmt,");
		insertSql.append(" :FeeSchd , :SchdFeePaid , :SchdFeeOS , :InsSchd, :SchdInsPaid, ");
		insertSql.append(" :AdvBaseRate , :AdvMargin , :AdvPftRate , :AdvCalRate , :AdvProfit , :AdvRepayAmount,");
		insertSql.append(
				" :SuplRent , :IncrCost , :SuplRentPaid , :IncrCostPaid , :TDSAmount, :TDSPaid, :PftDaysBasis, ");
		if (!isWIF) {
			insertSql.append(" :RefundOrWaiver, :EarlyPaid, :EarlyPaidBal, :WriteoffPrincipal, :WriteoffProfit,");
			insertSql.append(
					" :WriteoffIns , :WriteoffIncrCost, :WriteoffSuplRent, :WriteoffSchFee, :PartialPaidAmt,:PresentmentId, :TDSApplicable, :SchdPftWaiver,");
			if (type.contains("Log")) {
				insertSql.append(" :LogKey , ");
			}
		}
		insertSql.append(" :SchdPriPaid, :SchdPftPaid, :SchPriPaid, :SchPftPaid, :Specifier,");
		insertSql.append("  :DefSchdDate, :SchdMethod, ");
		insertSql.append("  :InstNumber, :BpiOrHoliday, :FrqDate, :RecalLock, ");
		insertSql.append(" :RolloverOnSchDate , :RolloverAmount, :RolloverAmountPaid)");

		logger.debug("insertSql: " + insertSql.toString());

		SqlParameterSource[] beanParameters = SqlParameterSourceUtils.createBatch(financeScheduleDetail.toArray());
		try {
			this.jdbcTemplate.batchUpdate(insertSql.toString(), beanParameters);
		} catch (Exception e) {
			logger.error("Exception", e);
			throw e;
		}
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
		updateSql.append(
				" Set PftOnSchDate= :PftOnSchDate, CpzOnSchDate = :CpzOnSchDate, RepayOnSchDate= :RepayOnSchDate,");
		updateSql.append(" RvwOnSchDate= :RvwOnSchDate, DisbOnSchDate= :DisbOnSchDate, ");
		updateSql.append(" DownpaymentOnSchDate = :DownpaymentOnSchDate,");
		updateSql.append(
				" BalanceForPftCal= :BalanceForPftCal, BaseRate= :BaseRate, SplRate= :SplRate,MrgRate =:MrgRate,");
		updateSql.append(
				" ActRate= :ActRate, NoOfDays= :NoOfDays, CalOnIndRate=:CalOnIndRate,DayFactor =:DayFactor, ProfitCalc= :ProfitCalc,");
		updateSql.append(" ProfitSchd= :ProfitSchd, PrincipalSchd= :PrincipalSchd, RepayAmount= :RepayAmount,");
		updateSql.append(
				" ProfitBalance=:ProfitBalance, DisbAmount= :DisbAmount, DownPaymentAmount= :DownPaymentAmount,");
		updateSql.append(" CpzAmount= :CpzAmount, CpzBalance = :CpzBalance, ClosingBalance= :ClosingBalance,");
		updateSql.append(" OrgPft =:OrgPft , OrgPri=:OrgPri, OrgEndBal=:OrgEndBal,OrgPlanPft=:OrgPlanPft, ");
		updateSql.append(" ProfitFraction= :ProfitFraction, PrvRepayAmount= :PrvRepayAmount, ");
		updateSql.append(" SchdPriPaid= :SchdPriPaid, SchdPftPaid= :SchdPftPaid, SchPriPaid= :SchPriPaid,");

		updateSql.append(" SchPftPaid= :SchPftPaid,Specifier= :Specifier,");
		updateSql.append(" CalculatedRate =:CalculatedRate,FeeChargeAmt=:FeeChargeAmt,InsuranceAmt=:InsuranceAmt, ");
		updateSql.append(
				" FeeSchd=:FeeSchd , SchdFeePaid=:SchdFeePaid , SchdFeeOS=:SchdFeeOS , InsSchd=:InsSchd, SchdInsPaid=:SchdInsPaid,");
		updateSql.append(
				" AdvBaseRate=:AdvBaseRate , AdvMargin=:AdvMargin , AdvPftRate=:AdvPftRate , AdvCalRate=:AdvCalRate , AdvProfit=:AdvProfit , AdvRepayAmount=:AdvRepayAmount, ");
		updateSql.append(
				" SuplRent=:SuplRent , IncrCost=:IncrCost , SuplRentPaid=:SuplRentPaid , IncrCostPaid=:IncrCostPaid, ");
		updateSql.append(" TDSAmount=:TDSAmount, TDSPaid=:TDSPaid, PftDaysBasis=:PftDaysBasis, ");
		if (!isWIF) {
			updateSql.append(" RefundOrWaiver=:RefundOrWaiver, EarlyPaid =:EarlyPaid, EarlyPaidBal=:EarlyPaidBal ,");
			updateSql.append(" WriteoffPrincipal=:WriteoffPrincipal, WriteoffProfit=:WriteoffProfit ,");
			updateSql.append(" WriteoffIns=:WriteoffIns , PresentmentId=:PresentmentId, ");
			updateSql.append(
					" WriteoffIncrCost=:WriteoffIncrCost, WriteoffSuplRent=:WriteoffSuplRent, WriteoffSchFee=:WriteoffSchFee, PartialPaidAmt=:PartialPaidAmt,  ");
			updateSql.append(" tDSApplicable=:tDSApplicable, SchdPftWaiver = :SchdPftWaiver,");
		}
		updateSql.append(" DefSchdDate= :DefSchdDate, SchdMethod = :SchdMethod, ");
		updateSql.append(
				" InstNumber= :InstNumber, BpiOrHoliday= :BpiOrHoliday, FrqDate = :FrqDate,RecalLock=:RecalLock, ");
		updateSql.append(
				" RolloverOnSchDate=:RolloverOnSchDate , RolloverAmount=:RolloverAmount, RolloverAmountPaid=:RolloverAmountPaid ");
		updateSql.append(" Where FinReference =:FinReference AND SchDate = :SchDate AND SchSeq = :SchSeq");

		logger.debug("updateSql: " + updateSql.toString());

		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(financeScheduleDetail);
		recordCount = this.jdbcTemplate.update(updateSql.toString(), beanParameters);

		if (recordCount <= 0) {
			throw new ConcurrencyException();
		}
		logger.debug("Leaving");
	}

	@Override
	public void updateForRpy(FinanceScheduleDetail financeScheduleDetail) {
		int recordCount = 0;
		logger.debug("Entering");

		StringBuilder updateSql = new StringBuilder("Update FinScheduleDetails SET ");
		updateSql.append(
				" SchdPftPaid=:SchdPftPaid, SchdPriPaid=:SchdPriPaid, SchPftPaid=:SchPftPaid , SchPriPaid=:SchPriPaid , ");
		updateSql.append(
				" TDSPaid =:TDSPaid , SchdFeePaid=:SchdFeePaid,SchdInsPaid=:SchdInsPaid, SuplRentPaid=:SuplRentPaid, ");
		updateSql.append(" IncrCostPaid=:IncrCostPaid,  Rebate = :Rebate ");
		updateSql.append(" Where FinReference =:FinReference AND SchDate = :SchDate AND SchSeq = :SchSeq");

		logger.debug("updateSql: " + updateSql.toString());

		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(financeScheduleDetail);
		recordCount = this.jdbcTemplate.update(updateSql.toString(), beanParameters);

		if (recordCount <= 0) {
			throw new ConcurrencyException();
		}
		logger.debug("Leaving");
	}

	@Override
	public void updateListForRpy(List<FinanceScheduleDetail> schdList) {
		logger.debug("Entering");

		StringBuilder updateSql = new StringBuilder("Update FinScheduleDetails SET ");
		updateSql.append(
				" SchdPftPaid=:SchdPftPaid, SchdPriPaid=:SchdPriPaid, SchPftPaid=:SchPftPaid , SchPriPaid=:SchPriPaid , ");
		updateSql.append(
				" TDSPaid =:TDSPaid , SchdFeePaid=:SchdFeePaid,SchdInsPaid=:SchdInsPaid, SuplRentPaid=:SuplRentPaid, ");
		updateSql.append(" IncrCostPaid=:IncrCostPaid,  Rebate = :Rebate ");
		updateSql.append(" Where FinReference =:FinReference AND SchDate = :SchDate ");

		logger.debug("updateSql: " + updateSql.toString());
		SqlParameterSource[] beanParameters = SqlParameterSourceUtils.createBatch(schdList.toArray());
		this.jdbcTemplate.batchUpdate(updateSql.toString(), beanParameters);
		logger.debug("Leaving");
	}

	public void updateForRateReview(List<FinanceScheduleDetail> financeScheduleDetail) {
		logger.debug("Entering");

		// FIXME: PV: 15MAY17: Mechanism to find which records to be updated
		// need to be identified at the caller level
		StringBuilder updateSql = new StringBuilder("Update FinScheduleDetails Set");
		updateSql.append(" BalanceForPftCal= :BalanceForPftCal, BaseRate= :BaseRate, SplRate= :SplRate, ");
		updateSql.append(" MrgRate =:MrgRate, ActRate= :ActRate, CalculatedRate =:CalculatedRate, ");
		updateSql.append(" ProfitCalc= :ProfitCalc, ProfitSchd= :ProfitSchd, PrincipalSchd= :PrincipalSchd, ");
		updateSql.append(
				" RepayAmount= :RepayAmount, ProfitBalance=:ProfitBalance, CpzAmount= :CpzAmount, CpzBalance = :CpzBalance, ");
		updateSql.append(" ClosingBalance= :ClosingBalance, ProfitFraction= :ProfitFraction, ");
		updateSql.append(" PrvRepayAmount= :PrvRepayAmount, SchPriPaid= :SchPriPaid, SchPftPaid= :SchPftPaid, ");
		updateSql.append(" SchdMethod = :SchdMethod, TDSAmount=:TDSAmount, TDSPaid=:TDSPaid ");
		updateSql.append(" Where FinReference =:FinReference AND SchDate = :SchDate AND SchSeq = :SchSeq ");

		logger.debug("updateSql: " + updateSql.toString());

		SqlParameterSource[] beanParameters = SqlParameterSourceUtils.createBatch(financeScheduleDetail.toArray());
		this.jdbcTemplate.batchUpdate(updateSql.toString(), beanParameters);

		logger.debug("Leaving");
	}

	@Override
	public List<FinanceScheduleDetail> getFinScheduleDetails(String id, String type, boolean isWIF) {
		StringBuilder sql = getScheduleDetailQuery(type, isWIF);
		sql.append(" Where FinReference = ? order by SchDate asc");
		logger.debug(Literal.SQL + sql.toString());

		RowMapper<FinanceScheduleDetail> rowMapper = new ScheduleDetailRowMapper(isWIF);

		try {
			return this.jdbcTemplate.getJdbcOperations().query(sql.toString(), new PreparedStatementSetter() {
				@Override
				public void setValues(PreparedStatement ps) throws SQLException {
					ps.setString(1, id);
				}
			}, rowMapper);
		} catch (Exception e) {
			logger.warn(Literal.EXCEPTION, e);
		}

		return new ArrayList<>();
	}

	private StringBuilder getScheduleDetailQuery(String type, boolean isWIF) {
		StringBuilder sql = new StringBuilder("select");
		sql.append(" FinReference, SchDate, SchSeq, PftOnSchDate, CpzOnSchDate, RepayOnSchDate, RvwOnSchDate");
		sql.append(", DisbOnSchDate, DownpaymentOnSchDate, BalanceForPftCal, BaseRate, SplRate, MrgRate");
		sql.append(", ActRate, NoOfDays, CalOnIndRate, DayFactor, ProfitCalc, ProfitSchd, PrincipalSchd");
		sql.append(", RepayAmount, ProfitBalance, DisbAmount, DownPaymentAmount, CpzAmount, CpzBalance");
		sql.append(", OrgPft, OrgPri, OrgEndBal, OrgPlanPft, ClosingBalance, ProfitFraction, PrvRepayAmount");
		sql.append(", CalculatedRate, FeeChargeAmt, InsuranceAmt, FeeSchd, SchdFeePaid, SchdFeeOS");
		sql.append(", InsSchd, SchdInsPaid, TDSAmount, TDSPaid, PftDaysBasis, SchdPriPaid, SchdPftPaid");
		sql.append(", SchPriPaid, SchPftPaid, Specifier, DefSchdDate, SchdMethod, InstNumber, BpiOrHoliday");
		sql.append(", FrqDate, RecalLock");

		if (ImplementationConstants.IMPLEMENTATION_ISLAMIC) {
			sql.append(", AdvBaseRate, AdvMargin, AdvPftRate, AdvCalRate, AdvProfit, AdvRepayAmount, SuplRent");
			sql.append(", IncrCost, SuplRentPaid, IncrCostPaid, RolloverOnSchDate, RolloverAmount, RolloverAmountPaid");
		}

		if (!isWIF) {
			sql.append(", RefundOrWaiver, EarlyPaid, EarlyPaidBal, WriteoffPrincipal, WriteoffProfit, PresentmentId");
			sql.append(", WriteoffIns, WriteoffSchFee, PartialPaidAmt, TdsApplicable, SchdPftWaiver");

			if (ImplementationConstants.IMPLEMENTATION_ISLAMIC) {
				sql.append(" WriteoffIncrCost, WriteoffSuplRent");
			}

			sql.append(" From FinScheduleDetails");

		} else {
			sql.append(" From WIFFinScheduleDetails");
		}

		sql.append(StringUtils.trimToEmpty(type));

		if (App.DATABASE == Database.SQL_SERVER) {
			sql.append(EodConstants.SQL_NOLOCK);
		}

		return sql;
	}

	@Override
	public List<FinanceScheduleDetail> getFinScheduleDetails(long Custid, boolean isActive) {
		StringBuilder sql = getScheduleDetailQuery("", false);
		sql.append(" Where FinReference IN (Select FinReference from FinanceMain where CustID = ?");

		if (isActive) {
			sql.append(" AND FinIsActive = ?");
		}
		sql.append(" ) order by SchDate asc");

		logger.trace(Literal.SQL + sql.toString());

		RowMapper<FinanceScheduleDetail> rowMapper = new ScheduleDetailRowMapper(false);

		try {
			return this.jdbcTemplate.getJdbcOperations().query(sql.toString(), new PreparedStatementSetter() {
				@Override
				public void setValues(PreparedStatement ps) throws SQLException {
					ps.setLong(1, Custid);
					if (isActive) {
						ps.setLong(2, Custid);
					}
					ps.setBoolean(2, isActive);
				}
			}, rowMapper);
		} catch (Exception e) {
			logger.warn(Literal.EXCEPTION, e);
		}

		return new ArrayList<>();
	}

	@Override
	public List<FinanceScheduleDetail> getFinScheduleDetails(String id, String type, boolean isWIF, long logKey) {
		StringBuilder sql = getScheduleDetailQuery(type, isWIF);
		sql.append(" Where FinReference = ? and LogKey = ?");

		logger.trace(Literal.SQL + sql.toString());

		RowMapper<FinanceScheduleDetail> rowMapper = new ScheduleDetailRowMapper(isWIF);

		try {
			return this.jdbcTemplate.getJdbcOperations().query(sql.toString(), new PreparedStatementSetter() {
				@Override
				public void setValues(PreparedStatement ps) throws SQLException {
					ps.setString(1, id);
					ps.setLong(2, logKey);
				}
			}, rowMapper);
		} catch (Exception e) {
			logger.warn(Literal.EXCEPTION, e);
		}

		return new ArrayList<>();
	}

	@Override
	public List<FinanceScheduleDetail> getFinSchdDetailsForBatch(String finReference) {
		StringBuilder sql = new StringBuilder("select");
		sql.append(" SchDate, SchSeq, PftOnSchDate, CpzOnSchDate, RepayOnSchDate, RvwOnSchDate, BalanceForPftCal");
		sql.append(", ClosingBalance, CalculatedRate, NoOfDays, ProfitCalc, ProfitSchd, PrincipalSchd");
		sql.append(", DisbAmount, DownPaymentAmount, CpzAmount, CpzBalance, FeeChargeAmt, SchdPriPaid");
		sql.append(", SchdPftPaid, SchPftPaid, SchPriPaid, Specifier, SchdPftWaiver");
		sql.append(" from FinScheduleDetails");
		sql.append(" Where finReference = ?");

		try {
			return this.jdbcTemplate.getJdbcOperations().query(sql.toString(), new PreparedStatementSetter() {
				@Override
				public void setValues(PreparedStatement ps) throws SQLException {
					ps.setString(1, finReference);
				}
			}, new RowMapper<FinanceScheduleDetail>() {
				@Override
				public FinanceScheduleDetail mapRow(ResultSet rs, int rowNum) throws SQLException {
					FinanceScheduleDetail schd = new FinanceScheduleDetail();

					schd.setSchDate(rs.getTimestamp("SchDate"));
					schd.setSchSeq(rs.getInt("SchSeq"));
					schd.setPftOnSchDate(rs.getBoolean("PftOnSchDate"));
					schd.setCpzOnSchDate(rs.getBoolean("CpzOnSchDate"));
					schd.setRepayOnSchDate(rs.getBoolean("RepayOnSchDate"));
					schd.setRvwOnSchDate(rs.getBoolean("RvwOnSchDate"));
					schd.setBalanceForPftCal(rs.getBigDecimal("BalanceForPftCal"));
					schd.setClosingBalance(rs.getBigDecimal("ClosingBalance"));
					schd.setCalculatedRate(rs.getBigDecimal("CalculatedRate"));
					schd.setNoOfDays(rs.getInt("NoOfDays"));
					schd.setProfitCalc(rs.getBigDecimal("ProfitCalc"));
					schd.setProfitSchd(rs.getBigDecimal("ProfitSchd"));
					schd.setPrincipalSchd(rs.getBigDecimal("PrincipalSchd"));
					schd.setDisbAmount(rs.getBigDecimal("DisbAmount"));
					schd.setDownPaymentAmount(rs.getBigDecimal("DownPaymentAmount"));
					schd.setCpzAmount(rs.getBigDecimal("CpzAmount"));
					schd.setCpzBalance(rs.getBigDecimal("CpzBalance"));
					schd.setFeeChargeAmt(rs.getBigDecimal("FeeChargeAmt"));
					schd.setSchdPriPaid(rs.getBigDecimal("SchdPriPaid"));
					schd.setSchdPftPaid(rs.getBigDecimal("SchdPftPaid"));
					schd.setSchPftPaid(rs.getBoolean("SchPftPaid"));
					schd.setSchPriPaid(rs.getBoolean("SchPriPaid"));
					schd.setSpecifier(rs.getString("Specifier"));
					schd.setSchdPftWaiver(rs.getBigDecimal("SchdPftWaiver"));

					return schd;
				}
			});
		} catch (Exception e) {
			logger.warn(Literal.EXCEPTION, e);
		}
		return new ArrayList<>();
	}

	@Override
	public FinanceScheduleDetail getFinSchdDetailForRpy(String finReference, Date rpyDate, String finRpyFor) {
		logger.debug("Entering");

		FinanceScheduleDetail schdDetail = new FinanceScheduleDetail();
		schdDetail.setFinReference(finReference);
		schdDetail.setSchDate(rpyDate);

		StringBuilder selectSql = new StringBuilder(" Select SchSeq ");
		selectSql.append(" ,SchdPftPaid, SchdPriPaid, ProfitSchd, PrincipalSchd ");
		selectSql.append(" From FinScheduleDetails");
		selectSql.append(" Where FinReference =:FinReference AND SchDate=:SchDate ");

		logger.debug("selectSql: " + selectSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(schdDetail);
		RowMapper<FinanceScheduleDetail> typeRowMapper = ParameterizedBeanPropertyRowMapper
				.newInstance(FinanceScheduleDetail.class);

		try {
			schdDetail = this.jdbcTemplate.queryForObject(selectSql.toString(), beanParameters, typeRowMapper);
		} catch (EmptyResultDataAccessException e) {
			logger.warn("Exception: ", e);
			schdDetail = null;
		}
		logger.debug("Leaving");
		return schdDetail;
	}

	@Override
	public List<ScheduleMapDetails> getFinSchdDetailTermByDates(List<String> finReferences, Date schdFromdate,
			Date schdTodate) {
		logger.debug("Entering");

		MapSqlParameterSource source = new MapSqlParameterSource();
		source.addValue("FinReference", finReferences);
		source.addValue("FromDate", schdFromdate);
		source.addValue("ToDate", schdTodate);

		StringBuilder selectSql = new StringBuilder(
				"Select T1.FinReference, MIN(T1.SchDate) schdFromDate, MAX(T1.SchDate) schdToDate ");
		selectSql.append("FROM FinScheduleDetails T1 INNER JOIN  FinanceMain T2 ON T1.FinReference = T2.FinReference ");
		selectSql.append(" WHERE ");
		selectSql.append(" T1.FinReference IN( :FinReference ) AND ( T1.SchDate >= :FromDate");
		if (schdTodate != null) {
			selectSql.append(" AND T1.SchDate <=:ToDate ");
		}
		selectSql.append(" ) GROUP BY T1.FinReference");
		RowMapper<ScheduleMapDetails> typeRowMapper = ParameterizedBeanPropertyRowMapper
				.newInstance(ScheduleMapDetails.class);
		logger.debug("selectSql: " + selectSql.toString());

		logger.debug("Leaving");
		return this.jdbcTemplate.query(selectSql.toString(), source, typeRowMapper);
	}

	@Override
	public List<ScheduleMapDetails> getRecalCulateFinSchdDetailTermByDates(List<String> finReferences,
			Date schdFromdate, Date schdTodate) {
		logger.debug("Entering");
		Map<String, List<String>> map = new HashMap<String, List<String>>();
		map.put("FinReference", finReferences);
		StringBuilder selectSql = new StringBuilder(
				"Select T1.FinReference, MIN(T1.SchDate) schdRecalFromDate, MAX(T1.SchDate) schdRecalToDate ,T2.MaturityDate");
		selectSql.append("FROM FinScheduleDetails T1 INNER JOIN  FinanceMain T2 ON T1.FinReference = T2.FinReference ");
		selectSql.append(" WHERE ");
		selectSql.append(" T1.FinReference IN( :FinReference ) AND ( T1.SchDate >= ");
		selectSql.append("'" + schdFromdate + "'");
		if (schdTodate != null) {
			selectSql.append(" AND T1.SchDate <= ");
			selectSql.append("'" + schdTodate + "'");
		} else {
			selectSql.append(" AND T1.SchDate <= ");
			selectSql.append("T2.MaturityDate");
		}
		selectSql.append(" ) GROUP BY T1.FinReference and T2.MaturityDate");
		RowMapper<ScheduleMapDetails> typeRowMapper = ParameterizedBeanPropertyRowMapper
				.newInstance(ScheduleMapDetails.class);
		logger.debug("selectSql: " + selectSql.toString());

		logger.debug("Leaving");
		return this.jdbcTemplate.query(selectSql.toString(), map, typeRowMapper);
	}

	/**
	 * Method for get the count of FinScheduleDetails records depend on condition
	 * 
	 * @param finReference
	 * @param schdDate
	 * @return
	 */
	public int getFrqDfrCount(String finReference) {
		logger.debug("Entering");

		MapSqlParameterSource mapSqlParameterSource = new MapSqlParameterSource();
		mapSqlParameterSource.addValue("FinReference", finReference);

		StringBuilder selectQry = new StringBuilder(" Select Count(FinReference) From FinScheduleDetails ");
		selectQry.append(" Where FinReference = :FinReference ");
		logger.debug("selectSql: " + selectQry.toString());

		int recordCount = this.jdbcTemplate.queryForObject(selectQry.toString(), mapSqlParameterSource, Integer.class);
		logger.debug("Leaving");
		return recordCount;
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
		selectSql.append(" SUM(ProfitCalc - SchdPftPaid) ");
		selectSql.append(" FROM finscheduleDetails where FinReference = :FinReference");

		logger.debug("selectSql: " + selectSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(financeScheduleDetail);

		BigDecimal suspAmount = BigDecimal.ZERO;
		try {
			suspAmount = this.jdbcTemplate.queryForObject(selectSql.toString(), beanParameters, BigDecimal.class);
		} catch (EmptyResultDataAccessException e) {
			logger.warn("Exception: ", e);
			suspAmount = BigDecimal.ZERO;
		}

		selectSql = new StringBuilder(" SELECT SUM(CpzAmount) ");
		selectSql.append(" FROM finscheduleDetails ");
		selectSql.append(" WHERE FinReference = :FinReference AND SchDate <= :SchDate ");

		logger.debug("selectSql: " + selectSql.toString());
		BigDecimal cpzTillNow = BigDecimal.ZERO;
		try {
			cpzTillNow = this.jdbcTemplate.queryForObject(selectSql.toString(), beanParameters, BigDecimal.class);
		} catch (EmptyResultDataAccessException e) {
			logger.warn("Exception: ", e);
			cpzTillNow = BigDecimal.ZERO;
		}

		suspAmount = suspAmount.subtract(cpzTillNow);

		logger.debug("Leaving");
		return suspAmount;
	}

	/**
	 * Method for preparing Finance Summary Details for Inquiry
	 */
	@Override
	public FinanceSummary getFinanceSummaryDetails(FinanceSummary summary) {
		logger.debug("Entering");

		StringBuilder selectSql = new StringBuilder(
				" SELECT FPD.FinReference, FinAmount TotalDisbursement, TotalPriSchd,TotalFees,");
		selectSql.append(
				" TotalPftSchd, PrincipalSchd, ProfitSchd, SchdPftPaid, SchdPriPaid, Downpayment TotalDownPayment , ");
		selectSql.append(
				" TotalPftCpz TotalCpz, COALESCE(UtilizedDefCnt,0) UtilizedDefCnt FROM (SELECT FinReference, SUM(PrincipalSchd) PrincipalSchd, ");
		selectSql.append(" SUM(ProfitSchd) ProfitSchd, SUM(SchdPftPaid) SchdPftPaid, SUM(SchdPriPaid) SchdPriPaid ");
		selectSql.append(" FROM FinScheduleDetails WHERE FinReference = :FinReference ");
		selectSql.append(" AND SchDate <= :NextSchDate GROUP BY FinReference) T ");
		selectSql.append(" INNER JOIN FinPftDetails FPD ON FPD.FinReference=T.FinReference ");
		selectSql.append(
				" INNER JOIN (SELECT FinReference,Sum(ActualAmount) TotalFees from finfeedetail group by FinReference) FFD ON FFD.FinReference=T.FinReference");
		selectSql.append(
				" LEFT JOIN (SELECT FinReference,COUNT(*)UtilizedDefCnt FROM FinScheduleDetails t1 WHERE FinReference = :FinReference");
		selectSql.append(" GROUP BY FinReference)T2 on T2.FinReference = T.FinReference");

		logger.debug("selectSql: " + selectSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(summary);
		RowMapper<FinanceSummary> typeRowMapper = ParameterizedBeanPropertyRowMapper.newInstance(FinanceSummary.class);

		try {
			summary = this.jdbcTemplate.queryForObject(selectSql.toString(), beanParameters, typeRowMapper);
		} catch (Exception e) {
			logger.error("Exception: ", e);
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
		StringBuilder selectSql = new StringBuilder(
				" select sum(ProfitSchd - SchdPftPaid + PrincipalSchd - SchdPriPaid + ");
		selectSql.append(" SuplRent - SuplRentPaid +  ");
		selectSql.append(" IncrCost - IncrCostPaid + FeeSchd - SchdFeePaid + InsSchd - SchdInsPaid ) ");
		selectSql.append(" from FinScheduleDetails  where FinReference = :FinReference ");

		logger.debug("selectSql: " + selectSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(financeScheduleDetail);

		BigDecimal repayAmount = BigDecimal.ZERO;
		try {
			repayAmount = this.jdbcTemplate.queryForObject(selectSql.toString(), beanParameters, BigDecimal.class);
		} catch (EmptyResultDataAccessException e) {
			logger.warn("Exception: ", e);
			repayAmount = BigDecimal.ZERO;
		}

		logger.debug("Leaving");
		return repayAmount;

	}

	@Override
	public BigDecimal getTotalUnpaidPriAmount(String finReference) {
		logger.debug("Entering");

		FinanceScheduleDetail financeScheduleDetail = new FinanceScheduleDetail();
		financeScheduleDetail.setFinReference(finReference);

		// Get Sum of Total Payment Amount(profits and Principals)
		StringBuilder selectSql = new StringBuilder(" select sum(PrincipalSchd - SchdPriPaid ");
		selectSql.append(" - WriteoffPrincipal) ");
		selectSql.append(" from FinScheduleDetails  where FinReference = :FinReference ");

		logger.debug("selectSql: " + selectSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(financeScheduleDetail);

		BigDecimal priAmt = BigDecimal.ZERO;
		try {
			priAmt = this.jdbcTemplate.queryForObject(selectSql.toString(), beanParameters, BigDecimal.class);
		} catch (EmptyResultDataAccessException e) {
			logger.warn("Exception: ", e);
			priAmt = BigDecimal.ZERO;
		}

		logger.debug("Leaving");
		return priAmt;
	}

	@Override
	public BigDecimal getTotalUnpaidPftAmount(String finReference) {
		logger.debug("Entering");

		FinanceScheduleDetail financeScheduleDetail = new FinanceScheduleDetail();
		financeScheduleDetail.setFinReference(finReference);

		// Get Sum of Total Payment Amount(profits and Principals)
		StringBuilder selectSql = new StringBuilder(" select sum(ProfitSchd - SchdPftPaid  ");
		selectSql.append(" - WriteoffProfit ) ");
		selectSql.append(" from FinScheduleDetails  where FinReference = :FinReference ");

		logger.debug("selectSql: " + selectSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(financeScheduleDetail);

		BigDecimal pftAmt = BigDecimal.ZERO;
		try {
			pftAmt = this.jdbcTemplate.queryForObject(selectSql.toString(), beanParameters, BigDecimal.class);
		} catch (EmptyResultDataAccessException e) {
			logger.warn("Exception: ", e);
			pftAmt = BigDecimal.ZERO;
		}

		logger.debug("Leaving");
		return pftAmt;
	}

	@Override
	public FinanceWriteoff getWriteoffTotals(String finReference) {
		logger.debug("Entering");

		FinanceWriteoff financeWriteoff = new FinanceWriteoff();
		financeWriteoff.setFinReference(finReference);

		StringBuilder selectSql = new StringBuilder(
				" select sum(WriteoffPrincipal) WrittenoffPri, sum(WriteoffProfit) WrittenoffPft, ");
		selectSql.append(" sum(WriteoffIns) WrittenoffIns, ");
		selectSql.append(
				" sum(WriteoffIncrCost) WrittenoffIncrCost,sum(WriteoffSuplRent) WrittenoffSuplRent,sum(WriteoffSchFee) WrittenoffSchFee, ");
		selectSql.append(" sum(PrincipalSchd - SchdPriPaid - WriteoffPrincipal) UnPaidSchdPri, ");
		selectSql.append(" sum(ProfitSchd - SchdPftPaid - WriteoffProfit) UnPaidSchdPft,");
		selectSql.append(" sum(InsSchd - SchdInsPaid - WriteoffIns) UnpaidIns,");
		selectSql.append(" sum(IncrCost-IncrCostPaid-WriteoffIncrCost) UnpaidIncrCost,");
		selectSql.append(
				" sum(SuplRent - SuplRentPaid - WriteoffSuplRent) UnpaidSuplRent,sum(FeeSchd - SchdFeePaid - WriteoffSchFee) UnpaidSchFee ");
		selectSql.append(" from FinScheduleDetails  where FinReference = :FinReference ");

		logger.debug("selectSql: " + selectSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(financeWriteoff);
		RowMapper<FinanceWriteoff> typeRowMapper = ParameterizedBeanPropertyRowMapper
				.newInstance(FinanceWriteoff.class);

		try {
			financeWriteoff = this.jdbcTemplate.queryForObject(selectSql.toString(), beanParameters, typeRowMapper);
		} catch (EmptyResultDataAccessException e) {
			logger.warn("Exception: ", e);
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
		selectSql.append(" (RepayOnSchDate = 1 OR (PftOnSchDate = 1 AND RepayAmount > 0)) ");

		logger.debug("selectSql: " + selectSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(financeScheduleDetail);

		Date firstRepayDate = null;
		try {
			firstRepayDate = this.jdbcTemplate.queryForObject(selectSql.toString(), beanParameters, Date.class);
		} catch (EmptyResultDataAccessException e) {
			logger.warn("Exception: ", e);
			firstRepayDate = null;
		}

		if (firstRepayDate == null) {
			selectSql = new StringBuilder();
			selectSql.append("  select min(SchDate) from FinScheduleDetails_TEMP");
			selectSql.append(" where FinReference = :FinReference AND ");
			selectSql.append(" (RepayOnSchDate = 1 OR (PftOnSchDate = 1 AND RepayAmount > 0)) ");

			try {
				firstRepayDate = this.jdbcTemplate.queryForObject(selectSql.toString(), beanParameters, Date.class);
			} catch (EmptyResultDataAccessException e) {
				logger.warn("Exception: ", e);
				firstRepayDate = null;
			}
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
		selectSql.append(" SELECT F.RepayAccountId Account,  ");
		selectSql.append(
				" SUM(S.PrincipalSchd - S.SchdPriPaid + S.ProfitSchd - S.SchdPftPaid) CurODAmount FROM FinScheduleDetails S ");
		selectSql.append(" INNER JOIN FinanceMain F ON S.FinReference = F.FinReference ");
		selectSql.append(" INNER JOIN RMTFinanceTypes T ON F.FinType = T.FinType  ");
		selectSql.append(" WHERE S.SchDate > :ValueDate AND  S.SchDate <= :FutureDate  ");
		selectSql.append(" AND T.FinDivision = 'PBG' AND F.FinRepayMethod = 'AUTO' AND F.FinIsActive = 1 AND ");
		selectSql.append(
				"(S.PrincipalSchd - S.SchdPriPaid + S.ProfitSchd - S.SchdPftPaid) > 0 GROUP BY F.RepayAccountId ");

		logger.debug("selectSql: " + selectSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(holdStatus);
		RowMapper<AccountHoldStatus> typeRowMapper = ParameterizedBeanPropertyRowMapper
				.newInstance(AccountHoldStatus.class);

		logger.debug("Leaving");
		return this.jdbcTemplate.query(selectSql.toString(), beanParameters, typeRowMapper);
	}

	/**
	 * Get Schedule Details for Auto Hunting
	 * 
	 * @param accountId
	 */
	public List<FinanceScheduleDetail> getFinSchDetlsByPrimary(String accountId) {
		logger.debug("Entering");
		MapSqlParameterSource source = new MapSqlParameterSource();
		source.addValue("RepayAccountId", accountId);
		source.addValue("SchDate", SysParamUtil.getAppDate());

		StringBuilder selectSql = new StringBuilder();

		selectSql.append(" SELECT F.FinReference, F.FinBranch, F.FinType ,F.CustID ,F.LinkedFinRef,F.FinCcy,");
		selectSql.append(" S.SchDate, S.ProfitSchd, S.PrincipalSchd, ");
		selectSql.append(
				" S.SchdPftpaid, S.SchdPriPaid, (S.ProfitSchd - S.SchdPftPaid) As SchdPftBal, (S.PrincipalSchd - S.SchdPriPaid) As SchdPriBal,");
		selectSql.append(" S.SuplRent , S.SuplRentPaid,  S.IncrCost , S.IncrCostPaid , S.FeeSchd , S.SchdFeePaid , ");
		selectSql.append(" S.InsSchd , S.SchdInsPaid , S.AdvCalRate,S.AdvProfit,F.RepayAccountId ");
		selectSql.append(
				" FROM FinanceMain F , FinScheduleDetails S WHERE F.FinReference = S.FinReference  and  (S.ProfitSchd - S.SchdPftPaid + S.PrincipalSchd - S.SchdPriPaid ");
		selectSql.append(" + S.SuplRent - S.SuplRentPaid + ");
		selectSql.append(" S.IncrCost - S.IncrCostPaid + S.FeeSchd - S.SchdFeePaid + S.InsSchd - ");
		selectSql.append(" S.SchdInsPaid ) > 0   AND S.SchDate < :SchDate AND  ");
		selectSql
				.append(" (S.RepayOnSchDate = 1 OR (S.PftOnSchDate = 1 AND RepayAmount > 0))  AND F.FinIsActive = 1  ");
		selectSql.append("  and F.RepayAccountId=:RepayAccountId  ");

		logger.debug("selectSql: " + selectSql.toString());
		RowMapper<FinanceScheduleDetail> typeRowMapper = ParameterizedBeanPropertyRowMapper
				.newInstance(FinanceScheduleDetail.class);

		logger.debug("Leaving");
		return this.jdbcTemplate.query(selectSql.toString(), source, typeRowMapper);
	}

	/**
	 * Get Schedule Details for Auto Hunting
	 * 
	 * @param accountId
	 */
	@Override
	public List<FinanceScheduleDetail> getFinSchDetlsBySecondary(String accountId) {
		logger.debug("Entering");
		MapSqlParameterSource source = new MapSqlParameterSource();
		source.addValue("RepayAccountId", accountId);
		source.addValue("SchDate", SysParamUtil.getAppDate());

		StringBuilder selectSql = new StringBuilder();

		selectSql.append(" SELECT F.FinReference, F.FinBranch, F.FinType ,F.CustID ,F.LinkedFinRef,F.FinCcy,");
		selectSql.append(" S.SchDate, S.ProfitSchd, S.PrincipalSchd, ");
		selectSql.append(
				" S.SchdPftpaid, S.SchdPriPaid, (S.ProfitSchd - S.SchdPftPaid) As SchdPftBal, (S.PrincipalSchd - S.SchdPriPaid) As SchdPriBal,");
		selectSql.append(" S.SuplRent , S.SuplRentPaid,  S.IncrCost , S.IncrCostPaid , S.FeeSchd , S.SchdFeePaid , ");
		selectSql.append(" S.InsSchd , S.SchdInsPaid , S.AdvCalRate,S.AdvProfit,F.RepayAccountId ");
		selectSql.append(
				" FROM FinanceMain F , FinScheduleDetails S inner join SecondaryAccounts sa on s.FinReference=sa.FinReference WHERE F.FinReference = S.FinReference  and  (S.ProfitSchd - S.SchdPftPaid + S.PrincipalSchd - S.SchdPriPaid ");
		selectSql.append(" + S.SuplRent - S.SuplRentPaid + ");
		selectSql.append(" S.IncrCost - S.IncrCostPaid + S.FeeSchd - S.SchdFeePaid + S.InsSchd - ");
		selectSql.append(" S.SchdInsPaid ) > 0   AND S.SchDate < :SchDate AND  ");
		selectSql
				.append(" (S.RepayOnSchDate = 1 OR (S.PftOnSchDate = 1 AND RepayAmount > 0))  AND F.FinIsActive = 1  ");
		selectSql.append("  and sa.AccountNumber=:RepayAccountId  ");

		logger.debug("selectSql: " + selectSql.toString());
		RowMapper<FinanceScheduleDetail> typeRowMapper = ParameterizedBeanPropertyRowMapper
				.newInstance(FinanceScheduleDetail.class);

		logger.debug("Leaving");
		return this.jdbcTemplate.query(selectSql.toString(), source, typeRowMapper);
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
	public FinanceScheduleDetail getFinanceScheduleForRebate(String finreference, Date schdDate) {
		logger.debug("Entering");

		FinanceScheduleDetail wIFFinanceScheduleDetail = new FinanceScheduleDetail();
		wIFFinanceScheduleDetail.setFinReference(finreference);
		wIFFinanceScheduleDetail.setSchDate(schdDate);

		StringBuilder selectSql = new StringBuilder(" Select Sum(ProfitSchd) ProfitSchd , sum(AdvProfit) AdvProfit ");

		selectSql.append(" From FinScheduleDetails");
		selectSql.append(" Where FinReference =:FinReference");
		if (schdDate != null) {
			selectSql.append(" AND SchDate=:SchDate");
		}

		logger.debug("selectSql: " + selectSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(wIFFinanceScheduleDetail);
		RowMapper<FinanceScheduleDetail> typeRowMapper = ParameterizedBeanPropertyRowMapper
				.newInstance(FinanceScheduleDetail.class);

		try {
			return this.jdbcTemplate.queryForObject(selectSql.toString(), beanParameters, typeRowMapper);
		} catch (EmptyResultDataAccessException e) {
			logger.warn("Exception: ", e);
			wIFFinanceScheduleDetail = null;
		}
		logger.debug("Leaving");
		return null;
	}

	/**
	 * Method for fetch Finance Schedule details base on schdDate
	 * 
	 */
	@Override
	public FinanceScheduleDetail getFinSchduleDetails(String finReference, Date schdDate, boolean isWIF) {
		logger.debug("Entering");

		FinanceScheduleDetail financeScheduleDetail = new FinanceScheduleDetail();
		financeScheduleDetail.setSchDate(schdDate);
		financeScheduleDetail.setFinReference(finReference);

		StringBuilder selectSql = new StringBuilder(" Select FinReference, SchDate, SchSeq, PftOnSchDate,");
		selectSql.append(" CpzOnSchDate, RepayOnSchDate, RvwOnSchDate, DisbOnSchDate,");
		selectSql.append(" DownpaymentOnSchDate, BalanceForPftCal, BaseRate, SplRate, MrgRate, ActRate, NoOfDays,");
		selectSql
				.append(" CalOnIndRate, DayFactor, ProfitCalc, ProfitSchd, PrincipalSchd, RepayAmount, ProfitBalance,");
		selectSql.append(
				" DisbAmount, DownPaymentAmount, CpzAmount, CpzBalance, ClosingBalance, ProfitFraction, PrvRepayAmount, ");
		selectSql.append(" SchdPriPaid, SchdPftPaid, SchPriPaid, SchPftPaid,Specifier, OrgPlanPft,");
		selectSql.append(" DefSchdDate,SchdMethod, CalculatedRate,FeeChargeAmt,InsuranceAmt,");
		selectSql.append(
				" FeeSchd , SchdFeePaid , SchdFeeOS , InsSchd, SchdInsPaid,AdvBaseRate , AdvMargin , AdvPftRate , AdvCalRate , AdvProfit , AdvRepayAmount, ");
		selectSql.append(" SuplRent , IncrCost ,SuplRentPaid , IncrCostPaid , TDSAmount, TDSPaid, PftDaysBasis,  ");
		selectSql.append(" RolloverOnSchDate , RolloverAmount, RolloverAmountPaid, ");
		selectSql.append(" InstNumber, BpiOrHoliday, FrqDate, RecalLock ");

		selectSql.append(" , RefundOrWaiver ,EarlyPaid, EarlyPaidBal, WriteoffPrincipal, WriteoffProfit, ");
		selectSql.append(" WriteoffIns , WriteoffIncrCost,WriteoffSuplRent,WriteoffSchFee,PartialPaidAmt ");
		if (!isWIF) {
			selectSql.append(", SchdPftWaiver");
			selectSql.append(" From FinScheduleDetails");
		} else {
			selectSql.append(" From WIFFinScheduleDetails");
		}

		selectSql.append(" Where FinReference =:FinReference AND SchDate=:SchDate");

		logger.debug("selectSql: " + selectSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(financeScheduleDetail);
		RowMapper<FinanceScheduleDetail> typeRowMapper = ParameterizedBeanPropertyRowMapper
				.newInstance(FinanceScheduleDetail.class);

		try {
			financeScheduleDetail = this.jdbcTemplate.queryForObject(selectSql.toString(), beanParameters,
					typeRowMapper);
		} catch (EmptyResultDataAccessException e) {
			logger.warn("Exception: ", e);
			financeScheduleDetail = null;
		}

		logger.debug("Leaving");
		return financeScheduleDetail;
	}

	@Override
	public FinanceScheduleDetail getTotals(String finReference) {
		logger.debug("Entering");

		FinanceScheduleDetail financeScheduleDetail = new FinanceScheduleDetail();
		financeScheduleDetail.setFinReference(finReference);

		StringBuilder selectSql = new StringBuilder(
				" Select Sum(ProfitSchd) ProfitSchd, Sum(SchdPftPaid) SchdPftPaid,");
		selectSql.append(" Sum(PrincipalSchd) PrincipalSchd, Sum(SchdPriPaid) SchdPriPaid,");
		selectSql.append(" Sum(ProfitCalc) ProfitCalc, Sum(ClosingBalance) ClosingBalance");
		selectSql.append(" FROM FinScheduleDetails Where FinReference =:FinReference");

		logger.debug("selectSql: " + selectSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(financeScheduleDetail);
		RowMapper<FinanceScheduleDetail> typeRowMapper = ParameterizedBeanPropertyRowMapper
				.newInstance(FinanceScheduleDetail.class);

		try {
			financeScheduleDetail = this.jdbcTemplate.queryForObject(selectSql.toString(), beanParameters,
					typeRowMapper);
		} catch (EmptyResultDataAccessException e) {
			logger.warn("Exception: ", e);
			financeScheduleDetail = null;
		}

		logger.debug("Leaving");
		return financeScheduleDetail;
	}

	@Override
	public FinanceScheduleDetail getNextSchPayment(String finReference, Date curBussDate) {
		logger.debug("Entering");

		FinanceScheduleDetail financeScheduleDetail = new FinanceScheduleDetail();
		financeScheduleDetail.setFinReference(finReference);
		financeScheduleDetail.setSchDate(curBussDate);

		StringBuilder selectSql = new StringBuilder(
				" Select * from (select ROW_NUMBER() Over(order by Schdate) row_num, ");
		selectSql.append(" FinReference, SchDate, ProfitSchd, PrincipalSchd  FROM FinScheduleDetails Where ");
		selectSql.append(" FinReference =:FinReference AND SchDate>=:SchDate )T where row_num = 1 ");

		logger.debug("selectSql: " + selectSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(financeScheduleDetail);
		RowMapper<FinanceScheduleDetail> typeRowMapper = ParameterizedBeanPropertyRowMapper
				.newInstance(FinanceScheduleDetail.class);

		try {
			financeScheduleDetail = this.jdbcTemplate.queryForObject(selectSql.toString(), beanParameters,
					typeRowMapper);
		} catch (EmptyResultDataAccessException e) {
			logger.warn("Exception: ", e);
			financeScheduleDetail = null;
		}

		logger.debug("Leaving");
		return financeScheduleDetail;
	}

	/**
	 * Method for validate given date is valid schedule date or not.
	 * 
	 * @param finReference
	 * @param fromDate
	 * @param isWIF
	 * @return boolean
	 */
	@Override
	public boolean getFinScheduleCountByDate(String finReference, Date fromDate, boolean isWIF) {
		logger.debug("Entering");

		MapSqlParameterSource source = new MapSqlParameterSource();
		source.addValue("FinReference", finReference);
		source.addValue("SchDate", fromDate);

		StringBuilder selectSql = new StringBuilder(" Select COUNT(*) ");
		if (!isWIF) {
			selectSql.append(" From FinScheduleDetails");
		} else {
			selectSql.append(" From WIFFinScheduleDetails");
		}

		selectSql.append(" Where FinReference = :FinReference AND SchDate= :SchDate");

		logger.debug("selectSql: " + selectSql.toString());

		int recordCount = 0;
		try {
			recordCount = this.jdbcTemplate.queryForObject(selectSql.toString(), source, Integer.class);
		} catch (EmptyResultDataAccessException e) {
			logger.info(e);
			recordCount = 0;
		}

		if (recordCount <= 0) {
			return false;
		}

		logger.debug("Leaving");
		return true;
	}

	/**
	 * Method for fetch Finance Schedule details when Principal Payment greater than zero
	 * 
	 */
	@Override
	public BigDecimal getPriPaidAmount(String finReference) {
		logger.debug("Entering");

		MapSqlParameterSource detail = new MapSqlParameterSource();
		detail.addValue("FinReference", finReference);

		StringBuilder selectSql = new StringBuilder(" Select SUM(SchdPriPaid)  ");
		selectSql.append(" From FinScheduleDetails");
		selectSql.append(" Where FinReference = :FinReference");

		logger.debug("selectSql: " + selectSql.toString());

		BigDecimal schdPriPaid = this.jdbcTemplate.queryForObject(selectSql.toString(), detail, BigDecimal.class);
		if (schdPriPaid == null) {
			schdPriPaid = BigDecimal.ZERO;
		}

		logger.debug("Leaving");
		return schdPriPaid;
	}

	/**
	 * Method for fetch Finance Schedule details when Principal Payment greater than zero
	 * 
	 */
	@Override
	public BigDecimal getOutStandingBalFromFees(String finReference) {
		logger.debug("Entering");

		MapSqlParameterSource detail = new MapSqlParameterSource();
		detail.addValue("FinReference", finReference);

		StringBuilder selectSql = new StringBuilder(" Select SUM(FeeSchd) -  Sum(SchdFeePaid) ");
		selectSql.append(" From FinScheduleDetails");
		selectSql.append(" Where FinReference = :FinReference");

		logger.debug("selectSql: " + selectSql.toString());

		BigDecimal outStandingFeeBal = this.jdbcTemplate.queryForObject(selectSql.toString(), detail, BigDecimal.class);
		if (outStandingFeeBal == null) {
			outStandingFeeBal = BigDecimal.ZERO;
		}

		logger.debug("Leaving");
		return outStandingFeeBal;
	}

	@Override
	public List<FinanceScheduleDetail> getDMFinScheduleDetails(String id, String type) {
		FinanceScheduleDetail detail = new FinanceScheduleDetail();
		detail.setId(id);

		StringBuilder selectSql = new StringBuilder(" Select FinReference, SchDate, SchSeq, ");
		selectSql.append(" PftOnSchDate, RepayOnSchDate, DisbOnSchDate, ActRate, CalculatedRate, ");
		selectSql.append(" ProfitSchd, PrincipalSchd, RepayAmount, ");
		selectSql.append(" DisbAmount, TDSAmount, TDSPaid, PftDaysBasis,");
		selectSql.append(" SchdPriPaid, SchdPftPaid, InstNumber, BpiOrHoliday, PresentmentId ");
		selectSql.append(" From FinScheduleDetails");
		selectSql.append(StringUtils.trimToEmpty(type));
		selectSql.append(" Where FinReference =:FinReference  order by SchDate asc");

		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(detail);
		RowMapper<FinanceScheduleDetail> typeRowMapper = ParameterizedBeanPropertyRowMapper
				.newInstance(FinanceScheduleDetail.class);

		List<FinanceScheduleDetail> finSchdDetails = this.jdbcTemplate.query(selectSql.toString(), beanParameters,
				typeRowMapper);
		return finSchdDetails;

	}

	/**
	 * retrive last schdate be present appDate
	 * 
	 * @param finReference
	 * @param appDate
	 */
	@Override
	public Date getPrevSchdDate(String finReference, Date appDate) {
		logger.debug("Entering");

		StringBuilder selectSql = new StringBuilder();
		selectSql.append("  select max(SchDate) from FinScheduleDetails");
		selectSql.append(" where FinReference = :FinReference AND ");
		selectSql.append("  SchDate <= :schdate");

		logger.debug("selectSql: " + selectSql.toString());

		MapSqlParameterSource mapSqlParameterSource = new MapSqlParameterSource();

		mapSqlParameterSource.addValue("FinReference", finReference);
		mapSqlParameterSource.addValue("schdate", appDate);

		Date prevSchdDate = null;
		try {
			prevSchdDate = this.jdbcTemplate.queryForObject(selectSql.toString(), mapSqlParameterSource, Date.class);
		} catch (EmptyResultDataAccessException e) {
			logger.warn("Exception: ", e);
			prevSchdDate = null;
		}
		return prevSchdDate;
	}

	/**
	 * method return true if given date is Installment schedule or it will consider as schedule change date
	 */
	@Override
	public boolean isInstallSchd(String finReference, Date lastPrevDate) {
		logger.debug("Entering");

		MapSqlParameterSource detail = new MapSqlParameterSource();
		detail.addValue("FinReference", finReference);
		detail.addValue("schDate", lastPrevDate);

		StringBuilder selectSql = new StringBuilder(" Select (REPAYAMOUNT -PARTIALPAIDAMT) ");
		selectSql.append(" From FinScheduleDetails");
		selectSql.append(" Where FinReference = :FinReference AND SchDate = :schDate");

		logger.debug("selectSql: " + selectSql.toString());

		BigDecimal repayAmount = this.jdbcTemplate.queryForObject(selectSql.toString(), detail, BigDecimal.class);
		if (repayAmount == null) {
			repayAmount = BigDecimal.ZERO;
		}

		logger.debug("Leaving");

		if (repayAmount.compareTo(BigDecimal.ZERO) > 0) {
			return true;
		} else {
			return false;
		}

	}

	/**
	 * Ticket id:124998,receipt upload Retrieve closing balance for given schedule date
	 * 
	 * @param finReference
	 * @param valueDate
	 */
	@Override
	public BigDecimal getClosingBalance(String finReference, Date valueDate) {
		logger.debug("Entering");

		MapSqlParameterSource detail = new MapSqlParameterSource();
		detail.addValue("FinReference", finReference);
		detail.addValue("schDate", valueDate);

		StringBuilder selectSql = new StringBuilder("SELECT CLOSINGBALANCE FROM FINSCHEDULEDETAILS ");
		selectSql.append(
				" Where FinReference = :FinReference AND SCHDATE =(SELECT  MAX(SCHDATE) FROM FINSCHEDULEDETAILS ");
		selectSql.append("WHERE FINREFERENCE=:FinReference AND SCHDATE <=:schDate)");

		logger.debug("selectSql: " + selectSql.toString());

		BigDecimal closingBal = this.jdbcTemplate.queryForObject(selectSql.toString(), detail, BigDecimal.class);
		if (closingBal == null) {
			closingBal = BigDecimal.ZERO;
		}

		logger.debug("Leaving");
		return closingBal;
	}

	@Override
	public List<FinanceScheduleDetail> getFinScheduleDetails(String finReference, String type, long logKey) {
		StringBuilder sql = getScheduleDetailQuery("", false);
		sql.append(" Where FinReference = ?");
		if (logKey > 0) {
			sql.append(" And LogKey = ?");
		}
		sql.append(" Order by SchDate asc");
		logger.trace(Literal.SQL + sql.toString());

		RowMapper<FinanceScheduleDetail> rowMapper = new ScheduleDetailRowMapper(false);

		try {
			return this.jdbcTemplate.getJdbcOperations().query(sql.toString(), new PreparedStatementSetter() {
				@Override
				public void setValues(PreparedStatement ps) throws SQLException {
					ps.setString(1, finReference);
					ps.setLong(1, logKey);
				}
			}, rowMapper);
		} catch (Exception e) {
			//
		}

		return new ArrayList<>();

	}

	@Override
	public void updateTDS(List<FinanceScheduleDetail> schdList) {
		logger.debug("Entering");

		StringBuilder updateSql = new StringBuilder("Update FinScheduleDetails SET ");
		updateSql.append(" tDSApplicable=:tDSApplicable,TDSAmount=:TDSAmount ");
		updateSql.append(" Where FinReference =:FinReference AND SchDate = :SchDate ");

		logger.debug("updateSql: " + updateSql.toString());
		SqlParameterSource[] beanParameters = SqlParameterSourceUtils.createBatch(schdList.toArray());
		this.jdbcTemplate.batchUpdate(updateSql.toString(), beanParameters);
		logger.debug("Leaving");
	}

	@Override
	public List<FinanceScheduleDetail> getFirstRepayAmt(String finReference) {
		logger.debug("Entering");

		List<FinanceScheduleDetail> finSchdDetails = new ArrayList<FinanceScheduleDetail>();

		MapSqlParameterSource detail = new MapSqlParameterSource();
		detail.addValue("FinReference", finReference);

		StringBuilder selectSql = new StringBuilder(
				" Select SchDate, PrincipalSchd,ProfitSchd, RepayAmount,PartialPaidAmt, InstNumber");
		selectSql.append(" From FinScheduleDetails");
		selectSql.append(" Where FinReference = :FinReference order by SchDate ASC");

		logger.debug("selectSql: " + selectSql.toString());
		RowMapper<FinanceScheduleDetail> typeRowMapper = ParameterizedBeanPropertyRowMapper
				.newInstance(FinanceScheduleDetail.class);

		try {
			finSchdDetails = this.jdbcTemplate.query(selectSql.toString(), detail, typeRowMapper);
		} catch (EmptyResultDataAccessException dae) {
			return Collections.emptyList();
		}

		logger.debug("Leaving");
		return finSchdDetails;
	}

	@Override
	public FinanceScheduleDetail getFinScheduleDetailForCOf(String finReference, Date appDate) {

		logger.debug("Entering");

		MapSqlParameterSource detail = new MapSqlParameterSource();
		detail.addValue("FinReference", finReference);
		detail.addValue("SchDate", appDate);

		StringBuilder selectSql = new StringBuilder(" Select ClosingBalance,DisbAmount");
		selectSql.append(
				" FROM FinScheduleDetails Where  FinReference =:FinReference and SchDate<=:SchDate ORDER BY SchDate DESC FETCH FIRST 1 ROW ONLY ");

		logger.debug("selectSql: " + selectSql.toString());
		RowMapper<FinanceScheduleDetail> typeRowMapper = ParameterizedBeanPropertyRowMapper
				.newInstance(FinanceScheduleDetail.class);
		FinanceScheduleDetail financeScheduleDetail = null;
		try {
			financeScheduleDetail = this.jdbcTemplate.queryForObject(selectSql.toString(), detail, typeRowMapper);
		} catch (EmptyResultDataAccessException e) {
			logger.warn("Exception: ", e);
			financeScheduleDetail = null;
		}

		logger.debug("Leaving");
		return financeScheduleDetail;

	}

	@Override
	public FinanceScheduleDetail getPrvSchd(String finReference, Date curBussDate) {
		logger.debug("Entering");

		FinanceScheduleDetail financeScheduleDetail = new FinanceScheduleDetail();
		financeScheduleDetail.setFinReference(finReference);
		financeScheduleDetail.setSchDate(curBussDate);

		StringBuilder selectSql = new StringBuilder(" select SchDate, REPAYAMOUNT, PARTIALPAIDAMT ");
		selectSql.append("from FinScheduleDetails where FinReference = :FinReference AND ");
		selectSql.append(
				" SchDate = (select max(SchDate) from FinScheduleDetails where FinReference = :FinReference and schdate <= :SchDate ) ");

		logger.debug("selectSql: " + selectSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(financeScheduleDetail);
		RowMapper<FinanceScheduleDetail> typeRowMapper = ParameterizedBeanPropertyRowMapper
				.newInstance(FinanceScheduleDetail.class);

		try {
			financeScheduleDetail = this.jdbcTemplate.queryForObject(selectSql.toString(), beanParameters,
					typeRowMapper);
		} catch (EmptyResultDataAccessException e) {
			logger.warn("Exception: ", e);
			financeScheduleDetail = null;
		}

		logger.debug(Literal.LEAVING);
		return financeScheduleDetail;
	}

	@Override
	public void updateSchPaid(FinanceScheduleDetail curSchd) {
		logger.debug(Literal.ENTERING);

		StringBuilder sql = new StringBuilder("Update FinScheduleDetails set");
		sql.append(" SchdPftPaid = :SchdPftPaid");
		sql.append(", SchPftPaid = :SchPftPaid");

		sql.append(", SchdPriPaid = :SchdPriPaid");
		sql.append(", SchPriPaid = :SchPriPaid");

		sql.append(", TDSPaid = :TDSPaid");
		sql.append(" where FinReference =:FinReference And SchDate = :SchDate");

		logger.trace(Literal.SQL + sql.toString());

		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(curSchd);
		int recordCount = this.jdbcTemplate.update(sql.toString(), beanParameters);

		if (recordCount <= 0) {
			throw new ConcurrencyException();
		}

		logger.debug(Literal.LEAVING);
	}

	/**
	 * Getting fin schedule details for rate report
	 */
	@Override
	public List<FinanceScheduleDetail> getFinSchdDetailsForRateReport(String finReference) {
		logger.debug(Literal.ENTERING);

		MapSqlParameterSource source = new MapSqlParameterSource();
		source.addValue("FinReference", finReference);

		StringBuilder sql = new StringBuilder(" Select FinReference, SchDate, SchSeq, PftOnSchDate,");
		sql.append(" CpzOnSchDate, RepayOnSchDate, RvwOnSchDate, DisbOnSchDate,");
		sql.append(" DownpaymentOnSchDate, BalanceForPftCal, BaseRate, SplRate, MrgRate, ActRate, NoOfDays,");
		sql.append(" CalOnIndRate, DayFactor, ProfitCalc, ProfitSchd, PrincipalSchd, RepayAmount, ProfitBalance,");
		sql.append(" DisbAmount, DownPaymentAmount, CpzAmount, CpzBalance, OrgPft , OrgPri, OrgEndBal,OrgPlanPft, ");
		sql.append(" ClosingBalance, ProfitFraction, PrvRepayAmount, CalculatedRate,FeeChargeAmt,InsuranceAmt, ");
		sql.append(" FeeSchd , SchdFeePaid , SchdFeeOS, InsSchd, SchdInsPaid, ");
		sql.append(" AdvBaseRate , AdvMargin , AdvPftRate , AdvCalRate , AdvProfit , AdvRepayAmount, ");
		sql.append(" SuplRent , IncrCost , SuplRentPaid , IncrCostPaid , TDSAmount, TDSPaid, PftDaysBasis, ");
		sql.append(" RefundOrWaiver, EarlyPaid , EarlyPaidBal ,WriteoffPrincipal, WriteoffProfit,");
		sql.append(" WriteoffIns , WriteoffIncrCost,WriteoffSuplRent,WriteoffSchFee, PartialPaidAmt,PresentmentId, ");
		sql.append(" SchdPriPaid, SchdPftPaid, SchPriPaid, SchPftPaid,Specifier,");
		sql.append(" DefSchdDate, SchdMethod, ");
		sql.append(" InstNumber, BpiOrHoliday, FrqDate, RecalLock,");
		sql.append(" RolloverOnSchDate , RolloverAmount, RolloverAmountPaid, SchdPftWaiver");

		sql.append(" From FinScheduleDetails Where");
		sql.append(" FinReference = :FinReference ");

		logger.trace(Literal.SQL + sql.toString());

		RowMapper<FinanceScheduleDetail> typeRowMapper = ParameterizedBeanPropertyRowMapper
				.newInstance(FinanceScheduleDetail.class);

		logger.debug(Literal.LEAVING);
		return this.jdbcTemplate.query(sql.toString(), source, typeRowMapper);
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
	public FinanceMain getFinanceMainForRateReport(String finReference, String type) {
		logger.debug(Literal.ENTERING);

		MapSqlParameterSource source = new MapSqlParameterSource();

		StringBuilder sql = new StringBuilder(
				"SELECT FM.FinReference, FM.FinCcy, CU.CustCIF LovDescCustCIF,  CU.CustShrtName LovDescCustShrtName, ");
		sql.append(" FM.FinCurrAssetValue, FM.ProfitDaysBasis, FM.CalRoundingMode ,FM.RoundingTarget ");
		sql.append("From FinanceMain FM INNER JOIN CUSTOMERS CU ON FM.CUSTID = CU.CUSTID");
		sql.append(" Where FinReference =:FinReference");

		logger.debug("selectSql: " + sql.toString());

		source.addValue("FinReference", finReference);

		RowMapper<FinanceMain> typeRowMapper = ParameterizedBeanPropertyRowMapper.newInstance(FinanceMain.class);
		try {
			return this.jdbcTemplate.queryForObject(sql.toString(), source, typeRowMapper);
		} catch (EmptyResultDataAccessException e) {
		}
		logger.debug(Literal.LEAVING);
		return null;
	}

	@Override
	public boolean isScheduleInQueue(String finReference) {
		MapSqlParameterSource source = new MapSqlParameterSource();
		source.addValue("FinReference", finReference);

		int count = jdbcTemplate.queryForObject(
				"select count(FinReference) from FinScheduleDetails_Temp where FinReference = :FinReference", source,
				Integer.class);

		if (count > 0) {
			return true;
		}

		return false;
	}

	public class ScheduleDetailRowMapper implements RowMapper<FinanceScheduleDetail> {
		private boolean iswif;

		public ScheduleDetailRowMapper(boolean iswif) {
			this.iswif = iswif;
		}

		@Override
		public FinanceScheduleDetail mapRow(ResultSet rs, int rowNum) throws SQLException {
			FinanceScheduleDetail schd = new FinanceScheduleDetail();

			schd.setFinReference(rs.getString("FinReference"));
			schd.setSchDate(rs.getTimestamp("SchDate"));
			schd.setSchSeq(rs.getInt("SchSeq"));
			schd.setPftOnSchDate(rs.getBoolean("PftOnSchDate"));
			schd.setCpzOnSchDate(rs.getBoolean("CpzOnSchDate"));
			schd.setRepayOnSchDate(rs.getBoolean("RepayOnSchDate"));
			schd.setRvwOnSchDate(rs.getBoolean("RvwOnSchDate"));
			schd.setDisbOnSchDate(rs.getBoolean("DisbOnSchDate"));
			schd.setDownpaymentOnSchDate(rs.getBoolean("DownpaymentOnSchDate"));
			schd.setBalanceForPftCal(rs.getBigDecimal("BalanceForPftCal"));
			schd.setBaseRate(rs.getString("BaseRate"));
			schd.setSplRate(rs.getString("SplRate"));
			schd.setMrgRate(rs.getBigDecimal("MrgRate"));
			schd.setActRate(rs.getBigDecimal("ActRate"));
			schd.setNoOfDays(rs.getInt("NoOfDays"));
			schd.setCalOnIndRate(rs.getBoolean("CalOnIndRate"));
			schd.setDayFactor(rs.getBigDecimal("DayFactor"));
			schd.setProfitCalc(rs.getBigDecimal("ProfitCalc"));
			schd.setProfitSchd(rs.getBigDecimal("ProfitSchd"));
			schd.setPrincipalSchd(rs.getBigDecimal("PrincipalSchd"));
			schd.setRepayAmount(rs.getBigDecimal("RepayAmount"));
			schd.setProfitBalance(rs.getBigDecimal("ProfitBalance"));
			schd.setDisbAmount(rs.getBigDecimal("DisbAmount"));
			schd.setDownPaymentAmount(rs.getBigDecimal("DownPaymentAmount"));
			schd.setCpzAmount(rs.getBigDecimal("CpzAmount"));
			schd.setCpzBalance(rs.getBigDecimal("CpzBalance"));
			schd.setOrgPft(rs.getBigDecimal("OrgPft"));
			schd.setOrgPri(rs.getBigDecimal("OrgPri"));
			schd.setOrgEndBal(rs.getBigDecimal("OrgEndBal"));
			schd.setOrgPlanPft(rs.getBigDecimal("OrgPlanPft"));
			schd.setClosingBalance(rs.getBigDecimal("ClosingBalance"));
			schd.setProfitFraction(rs.getBigDecimal("ProfitFraction"));
			schd.setPrvRepayAmount(rs.getBigDecimal("PrvRepayAmount"));
			schd.setCalculatedRate(rs.getBigDecimal("CalculatedRate"));
			schd.setFeeChargeAmt(rs.getBigDecimal("FeeChargeAmt"));
			schd.setInsuranceAmt(rs.getBigDecimal("InsuranceAmt"));
			schd.setFeeSchd(rs.getBigDecimal("FeeSchd"));
			schd.setSchdFeePaid(rs.getBigDecimal("SchdFeePaid"));
			schd.setSchdFeeOS(rs.getBigDecimal("SchdFeeOS"));
			schd.setInsSchd(rs.getBigDecimal("InsSchd"));
			schd.setSchdInsPaid(rs.getBigDecimal("SchdInsPaid"));
			schd.setTDSAmount(rs.getBigDecimal("TDSAmount"));
			schd.setTDSPaid(rs.getBigDecimal("TDSPaid"));
			schd.setPftDaysBasis(rs.getString("PftDaysBasis"));
			schd.setSchdPriPaid(rs.getBigDecimal("SchdPriPaid"));
			schd.setSchdPftPaid(rs.getBigDecimal("SchdPftPaid"));
			schd.setSchPriPaid(rs.getBoolean("SchPriPaid"));
			schd.setSchPftPaid(rs.getBoolean("SchPftPaid"));
			schd.setSpecifier(rs.getString("Specifier"));
			schd.setDefSchdDate(rs.getTimestamp("DefSchdDate"));
			schd.setSchdMethod(rs.getString("SchdMethod"));
			schd.setInstNumber(rs.getInt("InstNumber"));
			schd.setBpiOrHoliday(rs.getString("BpiOrHoliday"));
			schd.setFrqDate(rs.getBoolean("FrqDate"));
			schd.setRecalLock(rs.getBoolean("RecalLock"));

			if (ImplementationConstants.IMPLEMENTATION_ISLAMIC) {
				schd.setAdvBaseRate(rs.getString("AdvBaseRate"));
				schd.setAdvMargin(rs.getBigDecimal("AdvMargin"));
				schd.setAdvPftRate(rs.getBigDecimal("AdvPftRate"));
				schd.setAdvCalRate(rs.getBigDecimal("AdvCalRate"));
				schd.setAdvProfit(rs.getBigDecimal("AdvProfit"));
				schd.setAdvRepayAmount(rs.getBigDecimal("AdvRepayAmount"));
				schd.setSuplRent(rs.getBigDecimal("SuplRent"));
				schd.setIncrCost(rs.getBigDecimal("IncrCost"));
				schd.setSuplRentPaid(rs.getBigDecimal("SuplRentPaid"));
				schd.setIncrCostPaid(rs.getBigDecimal("IncrCostPaid"));
				schd.setRolloverOnSchDate(rs.getBoolean("RolloverOnSchDate"));
				schd.setRolloverAmount(rs.getBigDecimal("RolloverAmount"));
				schd.setRolloverAmountPaid(rs.getBigDecimal("RolloverAmountPaid"));
			}

			if (!iswif) {
				schd.setRefundOrWaiver(rs.getBigDecimal("RefundOrWaiver"));
				schd.setEarlyPaid(rs.getBigDecimal("EarlyPaid"));
				schd.setEarlyPaidBal(rs.getBigDecimal("EarlyPaidBal"));
				schd.setWriteoffPrincipal(rs.getBigDecimal("WriteoffPrincipal"));
				schd.setWriteoffProfit(rs.getBigDecimal("WriteoffProfit"));
				schd.setPresentmentId(rs.getLong("PresentmentId"));
				schd.setWriteoffIns(rs.getBigDecimal("WriteoffIns"));
				schd.setWriteoffSchFee(rs.getBigDecimal("WriteoffSchFee"));
				schd.setPartialPaidAmt(rs.getBigDecimal("PartialPaidAmt"));
				schd.setTDSApplicable(rs.getBoolean("TdsApplicable"));
				schd.setSchdPftWaiver(rs.getBigDecimal("SchdPftWaiver"));

				if (ImplementationConstants.IMPLEMENTATION_ISLAMIC) {
					schd.setWriteoffIncrCost(rs.getBigDecimal("WriteoffIncrCost"));
					schd.setWriteoffSuplRent(rs.getBigDecimal("WriteoffSuplRent"));
				}

			}

			return schd;
		}

	}

	public int getDueBucket(String finReference) {
		MapSqlParameterSource mapSqlParameterSource = new MapSqlParameterSource();
		mapSqlParameterSource.addValue("FinReference", finReference);

		StringBuilder sql = new StringBuilder(" Select DueBucket From FinanceMain ");
		sql.append(" Where FinReference = :FinReference ");

		return this.jdbcTemplate.queryForObject(sql.toString(), mapSqlParameterSource, Integer.class);
	}

	/**
	 * Method for Fetch Due Schedule details against Loan Reference and Value Date
	 */
	@Override
	public List<FinanceScheduleDetail> getDueSchedulesByFacilityRef(String finReference, Date valueDate) {
		logger.debug(Literal.ENTERING);

		MapSqlParameterSource source = new MapSqlParameterSource();
		source.addValue("FinReference", finReference);
		source.addValue("ValueDate", valueDate);

		StringBuilder sql = new StringBuilder(" Select S.FinReference, S.SchDate, S.ProfitSchd, S.PrincipalSchd, ");
		sql.append(" S.SchdPriPaid, S.SchdPftPaid, S.TdsApplicable ");
		sql.append(" From FinScheduleDetails S INNER JOIN FinanceMain F ON S.FinReference = F.FinReference ");
		sql.append(
				" Where F.FinReference = :FinReference AND S.SchDate <=:ValueDate AND (ProfitSchd + PrincipalSchd - SchdPftPaid - SchdPriPaid) > 0  ");
		sql.append(" AND F.FinIsActive = 1 ORDER BY SchDate , FinReference  ");

		logger.trace(Literal.SQL + sql.toString());
		RowMapper<FinanceScheduleDetail> typeRowMapper = ParameterizedBeanPropertyRowMapper
				.newInstance(FinanceScheduleDetail.class);

		logger.debug(Literal.LEAVING);
		return this.jdbcTemplate.query(sql.toString(), source, typeRowMapper);
	}

	@Override
	public void updateTDSChange(List<FinanceScheduleDetail> schdList) {

		StringBuilder updateSql = new StringBuilder("Update FinScheduleDetails SET ");
		updateSql.append(" TDSAmount = :TDSAmount ");
		updateSql.append(" Where FinReference = :FinReference AND SchDate = :SchDate ");

		logger.debug("updateSql: " + updateSql.toString());
		SqlParameterSource[] beanParameters = SqlParameterSourceUtils.createBatch(schdList.toArray());
		this.jdbcTemplate.batchUpdate(updateSql.toString(), beanParameters);
	}
}