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
import java.sql.Types;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.PreparedStatementSetter;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSourceUtils;
import org.springframework.jdbc.core.simple.ParameterizedBeanPropertyRowMapper;

import com.pennant.app.constants.ImplementationConstants;
import com.pennant.app.util.ScheduleCalculator;
import com.pennant.app.util.SysParamUtil;
import com.pennant.backend.dao.finance.FinanceScheduleDetailDAO;
import com.pennant.backend.model.finance.AccountHoldStatus;
import com.pennant.backend.model.finance.FinanceMain;
import com.pennant.backend.model.finance.FinanceScheduleDetail;
import com.pennant.backend.model.finance.FinanceSummary;
import com.pennant.backend.model.finance.FinanceWriteoff;
import com.pennant.backend.model.finance.ScheduleDueTaxDetail;
import com.pennant.backend.model.finance.ScheduleMapDetails;
import com.pennant.eod.constants.EodConstants;
import com.pennanttech.pennapps.core.App;
import com.pennanttech.pennapps.core.App.Database;
import com.pennanttech.pennapps.core.ConcurrencyException;
import com.pennanttech.pennapps.core.DependencyFoundException;
import com.pennanttech.pennapps.core.jdbc.BasicDao;
import com.pennanttech.pennapps.core.jdbc.JdbcUtil;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pennapps.core.util.DateUtil;

/**
 * DAO methods implementation for the <b>WIFFinanceScheduleDetail model</b> class.<br>
 */
public class FinanceScheduleDetailDAOImpl extends BasicDao<FinanceScheduleDetail> implements FinanceScheduleDetailDAO {
	private static Logger logger = LogManager.getLogger(FinanceScheduleDetailDAOImpl.class);

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

	public void deleteByFinReference(String id, String type, boolean isWIF, long logKey) {
		StringBuilder sql = new StringBuilder("Delete From");

		if (isWIF) {
			sql.append(" WIFFinScheduleDetails");
		} else {
			sql.append(" FinScheduleDetails");
		}

		sql.append(StringUtils.trimToEmpty(type));
		sql.append(" Where FinReference = ?");

		if (logKey != 0) {
			sql.append(" AND LogKey = ?");
		}
		logger.trace(Literal.SQL + sql.toString());

		this.jdbcOperations.update(sql.toString(), ps -> {
			ps.setString(1, id);

			if (logKey != 0) {
				ps.setLong(2, logKey);
			}

		});
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

	public int saveList(List<FinanceScheduleDetail> financeScheduleDetail, String type, boolean isWIF) {
		StringBuilder sql = new StringBuilder("Insert Into ");

		if (isWIF) {
			sql.append(" WIFFinScheduleDetails");
		} else {
			sql.append(" FinScheduleDetails");
		}

		sql.append(StringUtils.trimToEmpty(type));
		sql.append(" (FinReference, SchDate, SchSeq, PftOnSchDate, CpzOnSchDate, RepayOnSchDate");
		sql.append(", RvwOnSchDate, DisbOnSchDate, DownpaymentOnSchDate, BalanceForPftCal, BaseRate");
		sql.append(", SplRate, MrgRate, ActRate, NoOfDays, CalOnIndRate, DayFactor, ProfitCalc");
		sql.append(", ProfitSchd, PrincipalSchd, RepayAmount, ProfitBalance, DisbAmount, DownPaymentAmount");
		sql.append(", CpzAmount, CpzBalance, OrgPft, OrgPri, OrgEndBal, OrgPlanPft, ClosingBalance");
		sql.append(", ProfitFraction, PrvRepayAmount, CalculatedRate, FeeChargeAmt, InsuranceAmt");
		sql.append(", FeeSchd, SchdFeePaid, SchdFeeOS, InsSchd, SchdInsPaid, AdvBaseRate, AdvMargin");
		sql.append(", AdvPftRate, AdvCalRate, AdvProfit, AdvRepayAmount, SuplRent, IncrCost");
		sql.append(", SuplRentPaid, IncrCostPaid, TDSAmount, TDSPaid, PftDaysBasis");
		if (!isWIF) {
			sql.append(", RefundOrWaiver, EarlyPaid, EarlyPaidBal, WriteoffPrincipal, WriteoffProfit");
			sql.append(", WriteoffIns, WriteoffIncrCost, WriteoffSuplRent, WriteoffSchFee, PartialPaidAmt");
			sql.append(", PresentmentId, TDSApplicable, SchdPftWaiver");
			if (type.contains("Log")) {
				sql.append(", LogKey");
			}
		}
		sql.append(", SchdPriPaid, SchdPftPaid, SchPriPaid, SchPftPaid, Specifier");
		sql.append(", DefSchdDate, SchdMethod, InstNumber, BpiOrHoliday, FrqDate");
		sql.append(", RolloverOnSchDate, RolloverAmount, RolloverAmountPaid, RecalLock)");

		sql.append(" Values(");
		sql.append("?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?");
		sql.append(", ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?");
		if (!isWIF) {
			sql.append(", ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?");
			if (type.contains("Log")) {
				sql.append(", ?");
			}
		}
		sql.append(", ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?");
		sql.append(")");

		logger.trace(Literal.SQL + sql.toString());

		try {
			return jdbcOperations.batchUpdate(sql.toString(), new BatchPreparedStatementSetter() {

				@Override
				public void setValues(PreparedStatement ps, int i) throws SQLException {

					FinanceScheduleDetail fsd = financeScheduleDetail.get(i);
					int index = 1;
					ps.setString(index++, fsd.getFinReference());
					ps.setDate(index++, JdbcUtil.getDate(fsd.getSchDate()));
					ps.setInt(index++, fsd.getSchSeq());
					ps.setBoolean(index++, fsd.isPftOnSchDate());
					ps.setBoolean(index++, fsd.isCpzOnSchDate());
					ps.setBoolean(index++, fsd.isRepayOnSchDate());
					ps.setBoolean(index++, fsd.isRvwOnSchDate());
					ps.setBoolean(index++, fsd.isDisbOnSchDate());
					ps.setBoolean(index++, fsd.isDownpaymentOnSchDate());
					ps.setBigDecimal(index++, fsd.getBalanceForPftCal());
					ps.setString(index++, fsd.getBaseRate());
					ps.setString(index++, fsd.getSplRate());
					ps.setBigDecimal(index++, fsd.getMrgRate());
					ps.setBigDecimal(index++, fsd.getActRate());
					ps.setInt(index++, fsd.getNoOfDays());
					ps.setBoolean(index++, fsd.isCalOnIndRate());
					ps.setBigDecimal(index++, fsd.getDayFactor());
					ps.setBigDecimal(index++, fsd.getProfitCalc());
					ps.setBigDecimal(index++, fsd.getProfitSchd());
					ps.setBigDecimal(index++, fsd.getPrincipalSchd());
					ps.setBigDecimal(index++, fsd.getRepayAmount());
					ps.setBigDecimal(index++, fsd.getProfitBalance());
					ps.setBigDecimal(index++, fsd.getDisbAmount());
					ps.setBigDecimal(index++, fsd.getDownPaymentAmount());
					ps.setBigDecimal(index++, fsd.getCpzAmount());
					ps.setBigDecimal(index++, fsd.getCpzBalance());
					ps.setBigDecimal(index++, fsd.getOrgPft());
					ps.setBigDecimal(index++, fsd.getOrgPri());
					ps.setBigDecimal(index++, fsd.getOrgEndBal());
					ps.setBigDecimal(index++, fsd.getOrgPlanPft());
					ps.setBigDecimal(index++, fsd.getClosingBalance());
					ps.setBigDecimal(index++, fsd.getProfitFraction());
					ps.setBigDecimal(index++, fsd.getPrvRepayAmount());
					ps.setBigDecimal(index++, fsd.getCalculatedRate());
					ps.setBigDecimal(index++, fsd.getFeeChargeAmt());
					ps.setBigDecimal(index++, fsd.getInsuranceAmt());
					ps.setBigDecimal(index++, fsd.getFeeSchd());
					ps.setBigDecimal(index++, fsd.getSchdFeePaid());
					ps.setBigDecimal(index++, fsd.getSchdFeeOS());
					ps.setBigDecimal(index++, fsd.getInsSchd());
					ps.setBigDecimal(index++, fsd.getSchdInsPaid());
					ps.setString(index++, fsd.getAdvBaseRate());
					ps.setBigDecimal(index++, fsd.getAdvMargin());
					ps.setBigDecimal(index++, fsd.getAdvPftRate());
					ps.setBigDecimal(index++, fsd.getAdvCalRate());
					ps.setBigDecimal(index++, fsd.getAdvProfit());
					ps.setBigDecimal(index++, fsd.getAdvRepayAmount());
					ps.setBigDecimal(index++, fsd.getSuplRent());
					ps.setBigDecimal(index++, fsd.getIncrCost());
					ps.setBigDecimal(index++, fsd.getSuplRentPaid());
					ps.setBigDecimal(index++, fsd.getIncrCostPaid());
					ps.setBigDecimal(index++, fsd.getTDSAmount());
					ps.setBigDecimal(index++, fsd.getTDSPaid());
					ps.setString(index++, fsd.getPftDaysBasis());

					if (!isWIF) {
						ps.setBigDecimal(index++, fsd.getRefundOrWaiver());
						ps.setBigDecimal(index++, fsd.getEarlyPaid());
						ps.setBigDecimal(index++, fsd.getEarlyPaidBal());
						ps.setBigDecimal(index++, fsd.getWriteoffPrincipal());
						ps.setBigDecimal(index++, fsd.getWriteoffProfit());
						ps.setBigDecimal(index++, fsd.getWriteoffIns());
						ps.setBigDecimal(index++, fsd.getWriteoffIncrCost());
						ps.setBigDecimal(index++, fsd.getWriteoffSuplRent());
						ps.setBigDecimal(index++, fsd.getWriteoffSchFee());
						ps.setBigDecimal(index++, fsd.getPartialPaidAmt());
						ps.setLong(index++, fsd.getPresentmentId());
						ps.setBoolean(index++, fsd.isTDSApplicable());
						ps.setBigDecimal(index++, fsd.getSchdPftWaiver());
						if (type.contains("Log")) {
							ps.setLong(index++, fsd.getLogKey());
						}
					}

					ps.setBigDecimal(index++, fsd.getSchdPriPaid());
					ps.setBigDecimal(index++, fsd.getSchdPftPaid());
					ps.setBoolean(index++, fsd.isSchPriPaid());
					ps.setBoolean(index++, fsd.isSchPftPaid());
					ps.setString(index++, fsd.getSpecifier());
					ps.setDate(index++, JdbcUtil.getDate(fsd.getDefSchdDate()));
					ps.setString(index++, fsd.getSchdMethod());
					ps.setInt(index++, fsd.getInstNumber());
					ps.setString(index++, fsd.getBpiOrHoliday());
					ps.setBoolean(index++, fsd.isFrqDate());
					ps.setBoolean(index++, fsd.isRolloverOnSchDate());
					ps.setBigDecimal(index++, fsd.getRolloverAmount());
					ps.setBigDecimal(index++, fsd.getRolloverAmountPaid());
					ps.setBoolean(index++, fsd.isRecalLock());
				}

				@Override
				public int getBatchSize() {
					return financeScheduleDetail.size();
				}
			}).length;

		} catch (Exception e) {
			logger.error(Literal.EXCEPTION, e);
			throw e;
		}
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
	public void updateForRpy(FinanceScheduleDetail schd) {
		StringBuilder sql = getUpdateQuery();

		logger.trace(Literal.SQL + sql.toString());

		int recordCount = jdbcOperations.update(sql.toString(), ps -> {
			setPrepareStatementSetter(schd, ps);
		});

		if (recordCount <= 0) {
			throw new ConcurrencyException();
		}
	}

	@Override
	public void updateListForRpy(List<FinanceScheduleDetail> schdList) {
		StringBuilder sql = getUpdateQuery();

		logger.debug(Literal.SQL + sql.toString());

		try {
			jdbcOperations.batchUpdate(sql.toString(), new BatchPreparedStatementSetter() {

				@Override
				public void setValues(PreparedStatement ps, int i) throws SQLException {
					FinanceScheduleDetail fsd = schdList.get(i);
					setPrepareStatementSetter(fsd, ps);
				}

				@Override
				public int getBatchSize() {
					return schdList.size();
				}
			});
		} catch (EmptyResultDataAccessException e) {
			logger.warn(Literal.EXCEPTION, e);
		}
	}

	private void setPrepareStatementSetter(FinanceScheduleDetail schd, PreparedStatement ps) throws SQLException {
		int index = 1;

		ps.setBigDecimal(index++, schd.getSchdPftPaid());
		ps.setBigDecimal(index++, schd.getSchdPriPaid());
		ps.setBoolean(index++, schd.isSchPftPaid());
		ps.setBoolean(index++, schd.isSchPriPaid());
		ps.setBigDecimal(index++, schd.getTDSPaid());
		ps.setBigDecimal(index++, schd.getSchdFeePaid());
		ps.setBigDecimal(index++, schd.getSchdInsPaid());
		ps.setBigDecimal(index++, schd.getSuplRentPaid());
		ps.setBigDecimal(index++, schd.getIncrCostPaid());
		ps.setBigDecimal(index++, schd.getRebate());

		ps.setString(index++, schd.getFinReference());
		ps.setDate(index++, JdbcUtil.getDate(schd.getSchDate()));
		ps.setLong(index++, schd.getSchSeq());
	}

	private StringBuilder getUpdateQuery() {
		StringBuilder sql = new StringBuilder();
		sql.append("Update FinScheduleDetails set");
		sql.append(" SchdPftPaid = ?, SchdPriPaid = ?, SchPftPaid = ?, SchPriPaid = ?, TDSPaid = ?, SchdFeePaid = ?");
		sql.append(", SchdInsPaid = ?, SuplRentPaid = ?, IncrCostPaid = ?, Rebate = ?");
		sql.append(" Where FinReference = ? and SchDate = ? and SchSeq = ?");
		return sql;
	}

	public int updateForRateReview(List<FinanceScheduleDetail> schedules) {
		StringBuilder sql = new StringBuilder();
		sql.append("Update FinScheduleDetails set");
		sql.append(" BalanceForPftCal = ?, BaseRate = ?, SplRate = ?, MrgRate = ?, ActRate = ?, CalculatedRate = ?");
		sql.append(", ProfitCalc = ?, ProfitSchd = ?, PrincipalSchd = ?, RepayAmount = ?, ProfitBalance = ?");
		sql.append(", CpzAmount = ?, CpzBalance = ?, ClosingBalance = ?, ProfitFraction = ?, PrvRepayAmount = ?");
		sql.append(", SchPriPaid = ?, SchPftPaid = ?, SchdMethod = ?, TDSAmount = ?, TDSPaid = ?");
		sql.append(" Where FinReference = ? and SchDate = ? and SchSeq = ?");

		int[] count = jdbcOperations.batchUpdate(sql.toString(), new BatchPreparedStatementSetter() {

			@Override
			public void setValues(PreparedStatement ps, int i) throws SQLException {
				FinanceScheduleDetail schd = schedules.get(i);

				int index = 1;

				ps.setBigDecimal(index++, schd.getBalanceForPftCal());
				ps.setString(index++, schd.getBaseRate());
				ps.setString(index++, schd.getSplRate());
				ps.setBigDecimal(index++, schd.getMrgRate());
				ps.setBigDecimal(index++, schd.getActRate());
				ps.setBigDecimal(index++, schd.getCalculatedRate());
				ps.setBigDecimal(index++, schd.getProfitCalc());
				ps.setBigDecimal(index++, schd.getProfitSchd());
				ps.setBigDecimal(index++, schd.getPrincipalSchd());
				ps.setBigDecimal(index++, schd.getRepayAmount());
				ps.setBigDecimal(index++, schd.getProfitBalance());
				ps.setBigDecimal(index++, schd.getCpzAmount());
				ps.setBigDecimal(index++, schd.getCpzBalance());
				ps.setBigDecimal(index++, schd.getClosingBalance());
				ps.setBigDecimal(index++, schd.getProfitFraction());
				ps.setBigDecimal(index++, schd.getPrvRepayAmount());
				ps.setBoolean(index++, schd.isSchPriPaid());
				ps.setBoolean(index++, schd.isSchPftPaid());
				ps.setString(index++, schd.getSchdMethod());
				ps.setBigDecimal(index++, schd.getTDSAmount());
				ps.setBigDecimal(index++, schd.getTDSPaid());

				ps.setString(index++, schd.getFinReference());
				ps.setDate(index++, JdbcUtil.getDate(schd.getSchDate()));
				ps.setInt(index++, schd.getSchSeq());

			}

			@Override
			public int getBatchSize() {
				return schedules.size();
			}
		});

		return count.length;

	}

	@Override
	public List<FinanceScheduleDetail> getFinScheduleDetails(String id, String type, boolean isWIF) {
		StringBuilder sql = getScheduleDetailQuery(type, isWIF);
		sql.append(" Where FinReference = ?");

		logger.trace(Literal.SQL + sql.toString());

		RowMapper<FinanceScheduleDetail> rowMapper = new ScheduleDetailRowMapper(isWIF);
		List<FinanceScheduleDetail> finSchdDetails = null;

		finSchdDetails = this.jdbcOperations.query(sql.toString(), ps -> {
			ps.setString(1, id);
		}, rowMapper);

		return ScheduleCalculator.sortSchdDetails(finSchdDetails);
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

		logger.trace(Literal.SQL + sql.toString());

		RowMapper<FinanceScheduleDetail> rowMapper = new ScheduleDetailRowMapper(false);

		List<FinanceScheduleDetail> finSchdDetails = null;

		try {
			finSchdDetails = this.jdbcTemplate.getJdbcOperations().query(sql.toString(), ps -> {
				ps.setLong(1, Custid);
				if (isActive) {
					ps.setBoolean(2, isActive);
				}

			}, rowMapper);
		} catch (Exception e) {
			logger.warn(Literal.EXCEPTION, e);
			finSchdDetails = new ArrayList<>();
		}
		return ScheduleCalculator.sortSchdDetails(finSchdDetails);
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

		logger.trace(Literal.SQL + sql.toString());

		RowMapper<FinanceScheduleDetail> rowMapper = new ScheduleDetailRowMapper(false);

		List<FinanceScheduleDetail> finSchdDetails = null;

		try {
			finSchdDetails = this.jdbcTemplate.getJdbcOperations().query(sql.toString(), new PreparedStatementSetter() {
				@Override
				public void setValues(PreparedStatement ps) throws SQLException {
					ps.setString(1, finReference);

					if (logKey > 0) {
						ps.setLong(2, logKey);
					}
				}
			}, rowMapper);
		} catch (Exception e) {
			finSchdDetails = new ArrayList<>();
		}

		return ScheduleCalculator.sortSchdDetails(finSchdDetails);

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
		StringBuilder sql = new StringBuilder("Select");
		sql.append(" SchDate, PrincipalSchd, ProfitSchd, RepayAmount, PartialPaidAmt, InstNumber");
		sql.append(" From FinScheduleDetails");
		sql.append(" Where FinReference = ?");

		List<FinanceScheduleDetail> schedules = this.jdbcOperations.query(sql.toString(), ps -> {
			ps.setString(1, finReference);
		}, (rs, rowNum) -> {
			FinanceScheduleDetail schd = new FinanceScheduleDetail();

			schd.setSchDate(JdbcUtil.getDate(rs.getDate("SchDate")));
			schd.setPrincipalSchd(rs.getBigDecimal("PrincipalSchd"));
			schd.setProfitSchd(rs.getBigDecimal("ProfitSchd"));
			schd.setRepayAmount(rs.getBigDecimal("RepayAmount"));
			schd.setInstNumber(rs.getInt("InstNumber"));

			return schd;
		});

		return schedules.stream().sorted((sch1, sch2) -> DateUtil.compare(sch1.getSchDate(), sch2.getSchDate()))
				.collect(Collectors.toList());
	}

	@Override
	public FinanceScheduleDetail getPrvSchd(String finRef, Date curBussDate) {
		StringBuilder sql = new StringBuilder("Select");
		sql.append(" SchDate, RepayAmount, PartialPaidAmt");
		sql.append(" From FinScheduleDetails");
		sql.append(" Where FinReference = ? and SchDate = ");
		sql.append("(select max(SchDate)");
		sql.append(" From FinScheduleDetails");
		sql.append(" Where FinReference = ? and SchDate <= ?)");

		logger.trace(Literal.SQL + sql.toString());

		try {
			return jdbcOperations.queryForObject(sql.toString(), new Object[] { finRef, finRef, curBussDate },
					new RowMapper<FinanceScheduleDetail>() {

						@Override
						public FinanceScheduleDetail mapRow(ResultSet rs, int arg1) throws SQLException {
							FinanceScheduleDetail fsd = new FinanceScheduleDetail();
							fsd.setSchDate(rs.getDate("SchDate"));
							fsd.setRepayAmount(rs.getBigDecimal("RepayAmount"));
							fsd.setPartialPaidAmt(rs.getBigDecimal("PartialPaidAmt"));

							return fsd;
						}
					});
		} catch (EmptyResultDataAccessException e) {
			logger.warn(
					"Records not found in FinScheduleDetails for the specified FinReference >> {} and SchDate [<=] >> {}",
					finRef, curBussDate);
		}

		return null;
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

	/**
	 * Method for Save Due Tax details against Loan Reference and Schedule Date
	 */
	@Override
	public void saveSchDueTaxDetail(ScheduleDueTaxDetail sdtd) {
		StringBuilder sql = new StringBuilder();
		sql.append(" Insert Into ScheduleDueTaxDetails ");
		sql.append(" (FinReference, SchDate, TaxType, TaxCalcOn, Amount, InvoiceID)");
		sql.append(" Values(?, ?, ?, ?, ?, ?)");

		logger.trace(Literal.SQL + sql.toString());

		this.jdbcOperations.update(sql.toString(), (ps) -> {
			int index = 1;

			ps.setString(index++, sdtd.getFinReference());
			ps.setDate(index++, JdbcUtil.getDate(sdtd.getSchDate()));
			ps.setString(index++, sdtd.getTaxType());
			ps.setString(index++, sdtd.getTaxCalcOn());
			ps.setBigDecimal(index++, sdtd.getAmount());

			if ((Object) sdtd.getInvoiceID() instanceof Long) {
				ps.setLong(index, JdbcUtil.getLong(sdtd.getInvoiceID()));
			} else {
				ps.setNull(index, Types.NULL);
			}

		});

	}

	/**
	 * Method for Fetching Invoice ID for the Due schedule
	 */
	@Override
	public Long getSchdDueInvoiceID(String finReference, Date schdate) {
		logger.debug(Literal.ENTERING);
		Long invoiceID = null;
		try {
			MapSqlParameterSource source = new MapSqlParameterSource();
			source.addValue("FinReference", finReference);
			source.addValue("SchDate", schdate);

			StringBuilder sql = new StringBuilder();
			sql.append(" Select InvoiceID From ScheduleDueTaxDetails");
			sql.append(" Where FinReference = :FinReference AND SchDate =:SchDate ");

			logger.trace(Literal.SQL + sql.toString());

			invoiceID = this.jdbcTemplate.queryForObject(sql.toString(), source, Long.class);
		} catch (Exception e) {
			logger.warn(Literal.EXCEPTION, e);
		}

		logger.debug(Literal.LEAVING);
		return invoiceID;

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

	@Override
	public List<FinanceScheduleDetail> getFinScheduleDetailsBySchPriPaid(String id, String type, boolean isWIF) {
		logger.debug(Literal.ENTERING);
		StringBuilder sql = getScheduleDetailQuery(type, isWIF);
		sql.append(" Where FinReference = ? and SchdPriPaid > 0 order by SchDate asc");
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
		logger.debug(Literal.LEAVING);
		return new ArrayList<>();
	}

	@Override
	public FinanceScheduleDetail getFinSchDetailOrderBySchDate(String selectQuery, String whereClause,
			String reference) {
		logger.debug(Literal.ENTERING);
		StringBuilder sql = new StringBuilder(selectQuery);
		sql.append(whereClause);
		logger.debug(Literal.SQL + sql.toString());
		RowMapper<FinanceScheduleDetail> rowMapper = ParameterizedBeanPropertyRowMapper
				.newInstance(FinanceScheduleDetail.class);
		try {
			return this.jdbcTemplate.getJdbcOperations().queryForObject(sql.toString(), new Object[] { reference },
					rowMapper);

		} catch (EmptyResultDataAccessException e) {
			logger.warn(Literal.EXCEPTION, e);
		}
		logger.debug(Literal.LEAVING);
		return null;
	}

	@Override
	public FinanceScheduleDetail getNextUnpaidSchPayment(String finReference, Date valueDate) {
		FinanceScheduleDetail financeScheduleDetail = new FinanceScheduleDetail();
		financeScheduleDetail.setFinReference(finReference);
		financeScheduleDetail.setSchDate(valueDate);

		StringBuilder selectSql = new StringBuilder();
		selectSql.append(" Select * from (select ROW_NUMBER() Over(order by Schdate) row_num, ");
		selectSql.append(" * FROM FinScheduleDetails Where FinReference =:FinReference ");
		selectSql.append(" AND SchDate>=:SchDate AND (SchPftPaid=0 AND SchPriPaid=0) ");
		selectSql.append(" AND (RepayOnSchDate = 1 AND PftOnSchDate = 1 ) )T where row_num = 1 ");

		logger.debug("selectSql: " + selectSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(financeScheduleDetail);
		RowMapper<FinanceScheduleDetail> typeRowMapper = ParameterizedBeanPropertyRowMapper
				.newInstance(FinanceScheduleDetail.class);

		try {
			financeScheduleDetail = this.jdbcTemplate.queryForObject(selectSql.toString(), beanParameters,
					typeRowMapper);
		} catch (EmptyResultDataAccessException e) {
			logger.warn(Literal.EXCEPTION, e);
			financeScheduleDetail = null;
		}
		return financeScheduleDetail;
	}
}