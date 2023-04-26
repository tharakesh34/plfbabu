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
 * * FileName : FinanceScheduleDetailDAOImpl.java * * Author : PENNANT TECHONOLOGIES * * Creation Date : 05-05-2011 * *
 * Modified Date : 05-05-2011 * * Description : * *
 ********************************************************************************************
 * Date Author Version Comments *
 ********************************************************************************************
 * 05-05-2011 Pennant 0.1 * * * * * * * * *
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
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;

import com.pennant.app.util.ScheduleCalculator;
import com.pennant.backend.dao.finance.FinanceScheduleDetailDAO;
import com.pennant.backend.model.finance.FinanceMain;
import com.pennant.backend.model.finance.FinanceProfitDetail;
import com.pennant.backend.model.finance.FinanceScheduleDetail;
import com.pennant.backend.model.finance.FinanceWriteoff;
import com.pennant.backend.model.finance.RestructureDetail;
import com.pennant.backend.model.finance.ScheduleDueTaxDetail;
import com.pennant.eod.constants.EodConstants;
import com.pennanttech.pennapps.core.App;
import com.pennanttech.pennapps.core.App.Database;
import com.pennanttech.pennapps.core.ConcurrencyException;
import com.pennanttech.pennapps.core.DependencyFoundException;
import com.pennanttech.pennapps.core.jdbc.BasicDao;
import com.pennanttech.pennapps.core.jdbc.JdbcUtil;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pennapps.core.resource.Message;
import com.pennanttech.pennapps.core.util.DateUtil;
import com.pennanttech.pff.core.TableType;

/**
 * DAO methods implementation for the <b>WIFFinanceScheduleDetail model</b> class.<br>
 */
public class FinanceScheduleDetailDAOImpl extends BasicDao<FinanceScheduleDetail> implements FinanceScheduleDetailDAO {
	private static Logger logger = LogManager.getLogger(FinanceScheduleDetailDAOImpl.class);

	public FinanceScheduleDetailDAOImpl() {
		super();
	}

	@Override
	public FinanceScheduleDetail getFinanceScheduleDetailById(final long finID, final Date schdDate, String type,
			boolean isWIF) {

		StringBuilder sql = getScheduleDetailQuery(type, isWIF);
		sql.append(" Where FinID = ? and SchDate = ?");

		logger.debug(Literal.SQL + sql.toString());

		RowMapper<FinanceScheduleDetail> rowMapper = new ScheduleDetailRowMapper(isWIF);

		try {
			return this.jdbcOperations.queryForObject(sql.toString(), rowMapper, finID, schdDate);
		} catch (EmptyResultDataAccessException e) {
			logger.warn(Message.NO_RECORD_FOUND);
			return null;
		}
	}

	public void deleteByFinReference(long finID, String type, boolean isWIF, long logKey) {
		StringBuilder sql = new StringBuilder("Delete From");

		if (isWIF) {
			sql.append(" WIFFinScheduleDetails");
		} else {
			sql.append(" FinScheduleDetails");
		}

		sql.append(StringUtils.trimToEmpty(type));
		sql.append(" Where FinID = ?");

		if (logKey != 0) {
			sql.append(" AND LogKey = ?");
		}

		logger.debug(Literal.SQL + sql.toString());

		this.jdbcOperations.update(sql.toString(), ps -> {
			ps.setLong(1, finID);

			if (logKey != 0) {
				ps.setLong(2, logKey);
			}

		});
	}

	@Override
	public void delete(FinanceScheduleDetail schdule, String type, boolean isWIF) {
		StringBuilder sql = new StringBuilder("Delete");

		if (isWIF) {
			sql.append(" From WIFFinScheduleDetails");
		} else {
			sql.append(" From FinScheduleDetails");
		}

		sql.append(StringUtils.trimToEmpty(type));
		sql.append(" Where FinID = ? and SchDate = ?");

		logger.debug(Literal.SQL + sql.toString());

		try {
			int recordCount = this.jdbcOperations.update(sql.toString(), ps -> {
				ps.setLong(1, schdule.getFinID());
				ps.setDate(2, JdbcUtil.getDate(schdule.getSchDate()));
			});
			if (recordCount <= 0) {
				throw new ConcurrencyException();
			}
		} catch (DataAccessException e) {
			throw new DependencyFoundException(e);
		}
	}

	@Override
	public void save(FinanceScheduleDetail schedule, String type, boolean isWIF) {
		List<FinanceScheduleDetail> schedules = new ArrayList<>();
		schedules.add(schedule);
		saveList(schedules, type, isWIF);
	}

	public int saveList(List<FinanceScheduleDetail> schedules, String type, boolean isWIF) {
		StringBuilder sql = new StringBuilder("Insert Into ");

		if (isWIF) {
			sql.append(" WIFFinScheduleDetails");
		} else {
			sql.append(" FinScheduleDetails");
		}

		sql.append(StringUtils.trimToEmpty(type));
		sql.append(" (FinID, FinReference, SchDate, SchSeq, PftOnSchDate, CpzOnSchDate, RepayOnSchDate");
		sql.append(", RvwOnSchDate, DisbOnSchDate, DownpaymentOnSchDate, BalanceForPftCal, BaseRate");
		sql.append(", SplRate, MrgRate, ActRate, NoOfDays, DayFactor, ProfitCalc");
		sql.append(", ProfitSchd, PrincipalSchd, RepayAmount, ProfitBalance, DisbAmount, DownPaymentAmount");
		sql.append(", CpzAmount, CpzBalance, OrgPft, OrgPri, OrgEndBal, OrgPlanPft, ClosingBalance");
		sql.append(", ProfitFraction, PrvRepayAmount, CalculatedRate, FeeChargeAmt");
		sql.append(", FeeSchd, SchdFeePaid, SchdFeeOS, TDSAmount, TDSPaid, PftDaysBasis");
		if (!isWIF) {
			sql.append(", RefundOrWaiver, EarlyPaid, EarlyPaidBal, WriteoffPrincipal, WriteoffProfit");
			sql.append(", WriteoffSchFee, PartialPaidAmt");
			sql.append(", PresentmentId, TDSApplicable, SchdPftWaiver");
			if (type.contains("Log")) {
				sql.append(", LogKey");
			}
		}
		sql.append(", SchdPriPaid, SchdPftPaid, SchPriPaid, SchPftPaid, Specifier");
		sql.append(", DefSchdDate, SchdMethod, InstNumber, BpiOrHoliday, FrqDate, RecalLock)");

		sql.append(" Values(");
		sql.append("?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?");
		sql.append(", ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?");
		if (!isWIF) {
			sql.append(", ?, ?, ?, ?, ?, ?, ?, ?, ?, ?");
			if (type.contains("Log")) {
				sql.append(", ?");
			}
		}
		sql.append(", ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?");
		sql.append(")");

		logger.debug(Literal.SQL + sql.toString());

		return jdbcOperations.batchUpdate(sql.toString(), new BatchPreparedStatementSetter() {

			@Override
			public void setValues(PreparedStatement ps, int i) throws SQLException {

				FinanceScheduleDetail fsd = schedules.get(i);

				int index = 1;

				ps.setLong(index++, fsd.getFinID());
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
				ps.setBigDecimal(index++, fsd.getFeeSchd());
				ps.setBigDecimal(index++, fsd.getSchdFeePaid());
				ps.setBigDecimal(index++, fsd.getSchdFeeOS());
				ps.setBigDecimal(index++, fsd.getTDSAmount());
				ps.setBigDecimal(index++, fsd.getTDSPaid());
				ps.setString(index++, fsd.getPftDaysBasis());

				if (!isWIF) {
					ps.setBigDecimal(index++, fsd.getRefundOrWaiver());
					ps.setBigDecimal(index++, fsd.getEarlyPaid());
					ps.setBigDecimal(index++, fsd.getEarlyPaidBal());
					ps.setBigDecimal(index++, fsd.getWriteoffPrincipal());
					ps.setBigDecimal(index++, fsd.getWriteoffProfit());
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
				ps.setBoolean(index, fsd.isRecalLock());
			}

			@Override
			public int getBatchSize() {
				return schedules.size();
			}
		}).length;

	}

	@Override
	public void updateForRpy(FinanceScheduleDetail schd) {
		StringBuilder sql = getUpdateQuery();

		logger.debug(Literal.SQL + sql.toString());

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
	}

	private void setPrepareStatementSetter(FinanceScheduleDetail schd, PreparedStatement ps) throws SQLException {
		int index = 1;

		ps.setBigDecimal(index++, schd.getSchdPftPaid());
		ps.setBigDecimal(index++, schd.getSchdPriPaid());
		ps.setBoolean(index++, schd.isSchPftPaid());
		ps.setBoolean(index++, schd.isSchPriPaid());
		ps.setBigDecimal(index++, schd.getTDSPaid());
		ps.setBigDecimal(index++, schd.getSchdFeePaid());

		ps.setLong(index++, schd.getFinID());
		ps.setDate(index++, JdbcUtil.getDate(schd.getSchDate()));
		ps.setLong(index, schd.getSchSeq());
	}

	private StringBuilder getUpdateQuery() {
		StringBuilder sql = new StringBuilder("Update FinScheduleDetails Set");
		sql.append(" SchdPftPaid = ?, SchdPriPaid = ?, SchPftPaid = ?, SchPriPaid = ?, TDSPaid = ?, SchdFeePaid = ?");
		sql.append(" Where FinID = ? and SchDate = ? and SchSeq = ?");
		return sql;
	}

	public int updateForRateReview(List<FinanceScheduleDetail> schedules) {
		StringBuilder sql = new StringBuilder("Update FinScheduleDetails Set");
		sql.append(" BalanceForPftCal = ?, BaseRate = ?, SplRate = ?, MrgRate = ?, ActRate = ?, CalculatedRate = ?");
		sql.append(", ProfitCalc = ?, ProfitSchd = ?, PrincipalSchd = ?, RepayAmount = ?, ProfitBalance = ?");
		sql.append(", CpzAmount = ?, CpzBalance = ?, ClosingBalance = ?, ProfitFraction = ?, PrvRepayAmount = ?");
		sql.append(", SchPriPaid = ?, SchPftPaid = ?, SchdMethod = ?, TDSAmount = ?, TDSPaid = ?");
		sql.append(" Where FinID = ? and SchDate = ? and SchSeq = ?");

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

				ps.setLong(index++, schd.getFinID());
				ps.setDate(index++, JdbcUtil.getDate(schd.getSchDate()));
				ps.setInt(index, schd.getSchSeq());

			}

			@Override
			public int getBatchSize() {
				return schedules.size();
			}
		});

		return count.length;

	}

	@Override
	public List<FinanceScheduleDetail> getFinScheduleDetails(long finID, String type, boolean isWIF) {
		StringBuilder sql = getScheduleDetailQuery(type, isWIF);
		sql.append(" Where FinID = ?");

		logger.debug(Literal.SQL + sql.toString());

		RowMapper<FinanceScheduleDetail> rowMapper = new ScheduleDetailRowMapper(isWIF);

		List<FinanceScheduleDetail> schedules = this.jdbcOperations.query(sql.toString(), ps -> {
			ps.setLong(1, finID);
		}, rowMapper);

		return ScheduleCalculator.sortSchdDetails(schedules);
	}

	@Override
	public List<FinanceScheduleDetail> getFinSchedules(long finID, TableType tableType) {
		StringBuilder sql = getScheduleDetailQuery(tableType.getSuffix(), false);
		sql.append(" Where FinID = ?");

		logger.debug(Literal.SQL.concat(sql.toString()));

		RowMapper<FinanceScheduleDetail> rowMapper = new ScheduleDetailRowMapper(false);

		List<FinanceScheduleDetail> schedules = this.jdbcOperations.query(sql.toString(), ps -> {
			ps.setLong(1, finID);
		}, rowMapper);

		return ScheduleCalculator.sortSchdDetails(schedules);
	}

	private StringBuilder getScheduleDetailQuery(String type, boolean isWIF) {
		StringBuilder sql = new StringBuilder("Select");
		appendSchdColumns(isWIF, sql);

		if (!isWIF) {
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

	private void appendSchdColumns(boolean isWIF, StringBuilder sql) {
		sql.append(" FinID, FinReference, SchDate, SchSeq, PftOnSchDate, CpzOnSchDate, RepayOnSchDate, RvwOnSchDate");
		sql.append(", DisbOnSchDate, DownpaymentOnSchDate, BalanceForPftCal, BaseRate, SplRate, MrgRate");
		sql.append(", ActRate, NoOfDays, DayFactor, ProfitCalc, ProfitSchd, PrincipalSchd");
		sql.append(", RepayAmount, ProfitBalance, DisbAmount, DownPaymentAmount, CpzAmount, CpzBalance");
		sql.append(", OrgPft, OrgPri, OrgEndBal, OrgPlanPft, ClosingBalance, ProfitFraction, PrvRepayAmount");
		sql.append(", CalculatedRate, FeeChargeAmt, FeeSchd, SchdFeePaid, SchdFeeOS");
		sql.append(", TDSAmount, TDSPaid, PftDaysBasis, SchdPriPaid, SchdPftPaid");
		sql.append(", SchPriPaid, SchPftPaid, Specifier, DefSchdDate, SchdMethod, InstNumber, BpiOrHoliday");
		sql.append(", FrqDate, RecalLock");

		if (!isWIF) {
			sql.append(", RefundOrWaiver, EarlyPaid, EarlyPaidBal, WriteoffPrincipal, WriteoffProfit, PresentmentId");
			sql.append(", WriteoffSchFee, PartialPaidAmt, TdsApplicable, SchdPftWaiver");
		}
	}

	@Override
	public List<FinanceScheduleDetail> getFinScheduleDetails(long Custid, boolean isActive) {
		StringBuilder sql = getScheduleDetailQuery("", false);
		sql.append(" Where FinID IN (Select FinID from FinanceMain where CustID = ?");

		if (isActive) {
			sql.append(" AND FinIsActive = ?");
		}

		logger.debug(Literal.SQL + sql.toString());

		RowMapper<FinanceScheduleDetail> rowMapper = new ScheduleDetailRowMapper(false);

		List<FinanceScheduleDetail> finSchdDetails = this.jdbcOperations.query(sql.toString(), ps -> {
			ps.setLong(1, Custid);
			if (isActive) {
				ps.setBoolean(2, isActive);
			}

		}, rowMapper);

		return ScheduleCalculator.sortSchdDetails(finSchdDetails);
	}

	@Override
	public List<FinanceScheduleDetail> getFinScheduleDetails(long finID, String type, boolean isWIF, long logKey) {
		StringBuilder sql = getScheduleDetailQuery(type, isWIF);
		sql.append(" Where FinID = ? and LogKey = ?");

		logger.debug(Literal.SQL + sql.toString());

		RowMapper<FinanceScheduleDetail> rowMapper = new ScheduleDetailRowMapper(isWIF);

		return this.jdbcOperations.query(sql.toString(), ps -> {
			ps.setLong(1, finID);
			ps.setLong(2, logKey);
		}, rowMapper);
	}

	@Override
	public List<FinanceScheduleDetail> getFinSchdDetailsForBatch(long finID) {
		StringBuilder sql = new StringBuilder("Select");
		sql.append(" SchDate, SchSeq, PftOnSchDate, CpzOnSchDate, RepayOnSchDate, RvwOnSchDate, BalanceForPftCal");
		sql.append(", ClosingBalance, CalculatedRate, NoOfDays, ProfitCalc, ProfitSchd, PrincipalSchd");
		sql.append(", DisbAmount, DownPaymentAmount, CpzAmount, CpzBalance, FeeChargeAmt, SchdPriPaid");
		sql.append(", SchdPftPaid, SchPftPaid, SchPriPaid, Specifier, SchdPftWaiver");
		sql.append(" from FinScheduleDetails");
		sql.append(" Where FinID = ?");

		List<FinanceScheduleDetail> finSchdDetails = jdbcOperations.query(sql.toString(), ps -> {
			ps.setLong(1, finID);
		}, (rs, rowNum) -> {
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
		});

		return ScheduleCalculator.sortSchdDetails(finSchdDetails);
	}

	@Override
	public BigDecimal getSuspenseAmount(long finID, Date dateValueDate) {
		String sql = "Select sum(ProfitCalc - SchdPftPaid) From finscheduleDetails Where FinID = ?";

		logger.debug(Literal.SQL + sql);

		BigDecimal suspAmount = BigDecimal.ZERO;
		try {
			suspAmount = this.jdbcOperations.queryForObject(sql, BigDecimal.class, finID);
		} catch (EmptyResultDataAccessException e) {
			suspAmount = BigDecimal.ZERO;
		}

		sql = "Select SUM(CpzAmount) FROM finscheduleDetails Where FinID = ? AND SchDate <= ?";

		logger.debug(Literal.SQL + sql);

		BigDecimal cpzTillNow = BigDecimal.ZERO;
		try {
			cpzTillNow = this.jdbcOperations.queryForObject(sql, BigDecimal.class, finID,
					JdbcUtil.getDate(dateValueDate));
		} catch (EmptyResultDataAccessException e) {
			cpzTillNow = BigDecimal.ZERO;
		}

		suspAmount = suspAmount.subtract(cpzTillNow);

		return suspAmount;
	}

	@Override
	public BigDecimal getTotalRepayAmount(long finID) {
		String sql = "Select sum((ProfitSchd - SchdPftPaid) + (PrincipalSchd - SchdPriPaid) + (FeeSchd - SchdFeePaid)) From FinScheduleDetails Where FinID = ?";

		logger.debug(Literal.SQL + sql);
		return this.jdbcOperations.queryForObject(sql, BigDecimal.class, finID);
	}

	@Override
	public BigDecimal getTotalUnpaidPriAmount(long finID) {
		String sql = "Select sum((PrincipalSchd - SchdPriPaid) - WriteoffPrincipal) From FinScheduleDetails Where FinID = ?";

		logger.debug(Literal.SQL + sql);
		return this.jdbcOperations.queryForObject(sql, BigDecimal.class, finID);
	}

	@Override
	public BigDecimal getTotalUnpaidPftAmount(long finID) {
		String sql = "Select sum((ProfitSchd - SchdPftPaid) - WriteoffProfit) From FinScheduleDetails  Where FinID = ?";

		logger.debug(Literal.SQL + sql);
		return this.jdbcOperations.queryForObject(sql, BigDecimal.class, finID);
	}

	@Override
	public FinanceWriteoff getWriteoffTotals(long finID) {
		StringBuilder sql = new StringBuilder("Select");
		sql.append("  sum(WriteoffPrincipal) WrittenoffPri");
		sql.append(", sum(WriteoffProfit) WrittenoffPft");
		sql.append(", sum(WriteoffSchFee) WrittenoffSchFee");
		sql.append(", sum((PrincipalSchd - SchdPriPaid) - WriteoffPrincipal) UnPaidSchdPri");
		sql.append(", sum((ProfitSchd - SchdPftPaid) - WriteoffProfit) UnPaidSchdPft");
		sql.append(", sum((FeeSchd - SchdFeePaid) - WriteoffSchFee) UnpaidSchFee");
		sql.append(" From FinScheduleDetails");
		sql.append(" Where FinID = ?");

		logger.debug(Literal.SQL + sql);

		return this.jdbcOperations.queryForObject(sql.toString(), (rs, rowNum) -> {
			FinanceWriteoff fw = new FinanceWriteoff();

			fw.setWrittenoffPri(rs.getBigDecimal("WrittenoffPri"));
			fw.setWrittenoffPft(rs.getBigDecimal("WrittenoffPft"));
			fw.setWrittenoffSchFee(rs.getBigDecimal("WrittenoffSchFee"));
			fw.setUnPaidSchdPri(rs.getBigDecimal("UnPaidSchdPri"));
			fw.setUnPaidSchdPft(rs.getBigDecimal("UnPaidSchdPft"));
			fw.setUnpaidSchFee(rs.getBigDecimal("UnpaidSchFee"));

			return fw;

		}, finID);
	}

	@Override
	public Date getFirstRepayDate(long finID) {
		StringBuilder sql = new StringBuilder("Select min(SchDate) From (");
		sql.append(" Select FinID, SchDate, PftOnSchDate, RepayAmount From FinScheduleDetails");
		sql.append(" Union All");
		sql.append(" Select FinID, SchDate, PftOnSchDate, RepayAmount From FinScheduleDetails_Temp");
		sql.append(" ) T");
		sql.append(" Where FinID = ? and (RepayOnSchDate = 1 or (PftOnSchDate = 1 and RepayAmount > 0))");

		logger.debug(Literal.SQL + sql);
		return this.jdbcOperations.queryForObject(sql.toString(), Date.class, finID);
	}

	@Override
	public FinanceScheduleDetail getTotals(long finID) {
		StringBuilder sql = new StringBuilder("Select");
		sql.append("  sum(ProfitSchd) ProfitSchd");
		sql.append(", sum(SchdPftPaid) SchdPftPaid");
		sql.append(", sum(PrincipalSchd) PrincipalSchd");
		sql.append(", Sum(SchdPriPaid) SchdPriPaid");
		sql.append(", sum(ProfitCalc) ProfitCalc");
		sql.append(", sum(ClosingBalance) ClosingBalance");
		sql.append(" From FinScheduleDetails Where FinID = ?");

		logger.debug(Literal.SQL + sql);
		return this.jdbcOperations.queryForObject(sql.toString(), (rs, rowNum) -> {
			FinanceScheduleDetail schedule = new FinanceScheduleDetail();

			schedule.setProfitSchd(rs.getBigDecimal("ProfitSchd"));
			schedule.setSchdPftPaid(rs.getBigDecimal("SchdPftPaid"));
			schedule.setPrincipalSchd(rs.getBigDecimal("PrincipalSchd"));
			schedule.setSchdPriPaid(rs.getBigDecimal("SchdPriPaid"));
			schedule.setProfitCalc(rs.getBigDecimal("ProfitCalc"));
			schedule.setClosingBalance(rs.getBigDecimal("ClosingBalance"));

			return schedule;

		}, finID);
	}

	@Override
	public boolean getFinScheduleCountByDate(long finID, Date fromDate, boolean isWIF) {
		StringBuilder selectSql = new StringBuilder("Select count(FinID)");

		if (!isWIF) {
			selectSql.append(" From FinScheduleDetails");
		} else {
			selectSql.append(" From WIFFinScheduleDetails");
		}

		selectSql.append(" Where FinID = ? and SchDate = ?");

		logger.debug(Literal.SQL + selectSql.toString());
		return this.jdbcOperations.queryForObject(selectSql.toString(), Integer.class, finID, fromDate) > 0;
	}

	@Override
	public BigDecimal getPriPaidAmount(long finID) {
		String sql = "Select Coalesce(sum(SchdPriPaid), 0) From FinScheduleDetails Where FinID = ?";

		logger.debug(Literal.SQL + sql);
		return this.jdbcOperations.queryForObject(sql, BigDecimal.class, finID);
	}

	@Override
	public BigDecimal getOutStandingBalFromFees(long finID) {
		String sql = "Select sum(FeeSchd) -  sum(SchdFeePaid) From FinScheduleDetails Where FinID = ?";

		logger.debug(Literal.SQL.concat(sql));

		BigDecimal balFee = this.jdbcOperations.queryForObject(sql, BigDecimal.class, finID);

		if (balFee == null) {
			balFee = BigDecimal.ZERO;
		}

		return balFee;
	}

	@Override
	public Date getPrevSchdDate(long finID, Date appDate) {
		String sql = "Select max(SchDate) from FinScheduleDetails Where FinID = ? and SchDate <= ?";

		logger.debug(Literal.SQL + sql);
		return this.jdbcOperations.queryForObject(sql, Date.class, finID, appDate);
	}

	@Override
	public boolean isInstallSchd(long finID, Date lastPrevDate) {
		String sql = "Select (RepayAmount - PartialPaidAmt) From FinScheduleDetails Where FinID = ? and SchDate = ?";

		logger.debug(Literal.SQL + sql);

		try {
			return this.jdbcOperations.queryForObject(sql, BigDecimal.class, finID, lastPrevDate)
					.compareTo(BigDecimal.ZERO) > 0;
		} catch (EmptyResultDataAccessException e) {
			logger.warn(Message.NO_RECORD_FOUND);
			return false;
		}
	}

	@Override
	public BigDecimal getClosingBalance(long finID, Date valueDate) {
		StringBuilder sql = new StringBuilder("Select");
		sql.append(" ClosingBalance");
		sql.append(" From FinScheduleDetails");
		sql.append(" Where FinID = ? and SchDate = (");
		sql.append(" Select max(SchDate) From FinScheduleDetails Where FinID = ? and SchDate <= ?");
		sql.append(")");

		logger.debug(Literal.SQL + sql);

		try {
			return this.jdbcOperations.queryForObject(sql.toString(), BigDecimal.class, finID, finID, valueDate);
		} catch (EmptyResultDataAccessException e) {
			logger.warn(Message.NO_RECORD_FOUND);
			return BigDecimal.ZERO;
		}
	}

	@Override
	public List<FinanceScheduleDetail> getFinScheduleDetails(long finID, String type, long logKey) {
		StringBuilder sql = getScheduleDetailQuery("", false);
		sql.append(" Where FinID = ?");
		if (logKey > 0) {
			sql.append(" And LogKey = ?");
		}

		logger.debug(Literal.SQL + sql.toString());

		RowMapper<FinanceScheduleDetail> rowMapper = new ScheduleDetailRowMapper(false);

		List<FinanceScheduleDetail> finSchdDetails = null;

		try {
			finSchdDetails = this.jdbcTemplate.getJdbcOperations().query(sql.toString(), ps -> {
				ps.setLong(1, finID);

				if (logKey > 0) {
					ps.setLong(2, logKey);
				}
			}, rowMapper);
		} catch (EmptyResultDataAccessException e) {
			finSchdDetails = new ArrayList<>();
		}

		return ScheduleCalculator.sortSchdDetails(finSchdDetails);

	}

	@Override
	public void updateTDS(List<FinanceScheduleDetail> schedules) {
		StringBuilder sql = new StringBuilder("Update FinScheduleDetails");
		sql.append(" Set TDSApplicable = ?, TDSAmount= ?");
		sql.append(" Where FinID = ? and SchDate = ?");

		logger.debug(Literal.SQL + sql.toString());

		this.jdbcOperations.batchUpdate(sql.toString(), new BatchPreparedStatementSetter() {

			@Override
			public void setValues(PreparedStatement ps, int i) throws SQLException {
				FinanceScheduleDetail schedule = schedules.get(i);

				int index = 1;
				ps.setBoolean(index++, schedule.isTDSApplicable());
				ps.setBigDecimal(index++, schedule.getTDSAmount());
				ps.setLong(index++, schedule.getFinID());
				ps.setDate(index, JdbcUtil.getDate(schedule.getSchDate()));
			}

			@Override
			public int getBatchSize() {
				return schedules.size();
			}

		});
	}

	@Override
	public List<FinanceScheduleDetail> getFirstRepayAmt(long finID) {
		StringBuilder sql = new StringBuilder("Select");
		sql.append(" SchDate, PrincipalSchd, ProfitSchd, RepayAmount, PartialPaidAmt, InstNumber");
		sql.append(" From FinScheduleDetails");
		sql.append(" Where FinID = ?");

		List<FinanceScheduleDetail> schedules = this.jdbcOperations.query(sql.toString(), ps -> {
			ps.setLong(1, finID);
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
	public FinanceScheduleDetail getPrvSchd(long finID, Date curBussDate) {
		StringBuilder sql = new StringBuilder("Select");
		sql.append(" SchDate, RepayAmount, PartialPaidAmt");
		sql.append(" From FinScheduleDetails");
		sql.append(" Where FinID = ? and SchDate = (");
		sql.append(" Select max(SchDate) From FinScheduleDetails Where FinID = ? and SchDate <= ?");
		sql.append(")");

		logger.debug(Literal.SQL + sql.toString());

		try {
			return jdbcOperations.queryForObject(sql.toString(), (rs, rowNum) -> {
				FinanceScheduleDetail fsd = new FinanceScheduleDetail();

				fsd.setSchDate(rs.getDate("SchDate"));
				fsd.setRepayAmount(rs.getBigDecimal("RepayAmount"));
				fsd.setPartialPaidAmt(rs.getBigDecimal("PartialPaidAmt"));

				return fsd;

			}, finID, finID, curBussDate);
		} catch (EmptyResultDataAccessException e) {
			logger.warn(Message.NO_RECORD_FOUND);
			return null;
		}
	}

	@Override
	public void updateSchPaid(FinanceScheduleDetail curSchd) {
		StringBuilder sql = new StringBuilder("Update FinScheduleDetails");
		sql.append(" Set SchdPftPaid = ?, SchPftPaid = ?, SchdPriPaid = ?, SchPriPaid = ?, TDSPaid = ?");
		sql.append(" Where FinID = ? and SchDate = ?");

		logger.debug(Literal.SQL + sql.toString());

		int recordCount = this.jdbcOperations.update(sql.toString(), ps -> {

			int index = 1;
			ps.setBigDecimal(index++, curSchd.getSchdPftPaid());
			ps.setBoolean(index++, curSchd.isSchPftPaid());
			ps.setBigDecimal(index++, curSchd.getSchdPriPaid());
			ps.setBoolean(index++, curSchd.isSchPriPaid());
			ps.setBigDecimal(index++, curSchd.getTDSPaid());
			ps.setLong(index++, curSchd.getFinID());
			ps.setDate(index, JdbcUtil.getDate(curSchd.getSchDate()));

		});

		if (recordCount <= 0) {
			throw new ConcurrencyException();
		}

	}

	/**
	 * Getting fin schedule details for rate report
	 */
	@Override
	public List<FinanceScheduleDetail> getFinSchdDetailsForRateReport(long finID) {
		StringBuilder sql = getScheduleDetailQuery("", false);
		sql.append(" Where FinID = ?");

		logger.debug(Literal.SQL + sql.toString());

		RowMapper<FinanceScheduleDetail> rowMapper = new ScheduleDetailRowMapper(false);

		return this.jdbcOperations.query(sql.toString(), rowMapper, finID);
	}

	// FIXME Move to FinanceMainDAO
	@Override
	public FinanceMain getFinanceMainForRateReport(long finID, String type) {
		StringBuilder sql = new StringBuilder("Select");
		sql.append(" fm.FinID, fm.FinReference, fm.FinCcy, cu.CustCIF, cu.CustShrtName");
		sql.append(" ,fm.FinCurrAssetValue, fm.ProfitDaysBasis, fm.CalRoundingMode, fm.RoundingTarget");
		sql.append(" From FinanceMain fm");
		sql.append(" Inner Join Customers cu On cu.CustID = fm.CustID");
		sql.append(" Where FinID = ?");

		logger.debug(Literal.SQL + sql.toString());

		try {
			return this.jdbcOperations.queryForObject(sql.toString(), (rs, rowNum) -> {
				FinanceMain fm = new FinanceMain();

				fm.setFinID(rs.getLong("FinID"));
				fm.setFinReference(rs.getString("FinReference"));
				fm.setFinCcy(rs.getString("FinCcy"));
				fm.setLovDescCustCIF(rs.getString("CustCIF"));
				fm.setLovDescCustShrtName(rs.getString("CustShrtName"));
				fm.setFinCurrAssetValue(rs.getBigDecimal("FinCurrAssetValue"));
				fm.setProfitDaysBasis(rs.getString("ProfitDaysBasis"));
				fm.setCalRoundingMode(rs.getString("CalRoundingMode"));
				fm.setRoundingTarget(rs.getInt("RoundingTarget"));

				return fm;
			}, finID);
		} catch (EmptyResultDataAccessException e) {
			logger.warn(Message.NO_RECORD_FOUND);
			return null;
		}
	}

	@Override
	public boolean isScheduleInQueue(long finID) {
		String sql = "Select count(FinID) from FinScheduleDetails_Temp where FinID = ?";

		logger.debug(Literal.SQL + sql);

		return jdbcOperations.queryForObject(sql, Integer.class, finID) > 0;
	}

	public class ScheduleDetailRowMapper implements RowMapper<FinanceScheduleDetail> {
		private boolean iswif;

		public ScheduleDetailRowMapper(boolean iswif) {
			this.iswif = iswif;
		}

		@Override
		public FinanceScheduleDetail mapRow(ResultSet rs, int rowNum) throws SQLException {
			FinanceScheduleDetail schd = new FinanceScheduleDetail();

			schd.setFinID(rs.getLong("FinID"));
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
			schd.setFeeSchd(rs.getBigDecimal("FeeSchd"));
			schd.setSchdFeePaid(rs.getBigDecimal("SchdFeePaid"));
			schd.setSchdFeeOS(rs.getBigDecimal("SchdFeeOS"));
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
			if (!iswif) {
				schd.setRefundOrWaiver(rs.getBigDecimal("RefundOrWaiver"));
				schd.setEarlyPaid(rs.getBigDecimal("EarlyPaid"));
				schd.setEarlyPaidBal(rs.getBigDecimal("EarlyPaidBal"));
				schd.setWriteoffPrincipal(rs.getBigDecimal("WriteoffPrincipal"));
				schd.setWriteoffProfit(rs.getBigDecimal("WriteoffProfit"));
				schd.setPresentmentId(rs.getLong("PresentmentId"));
				schd.setWriteoffSchFee(rs.getBigDecimal("WriteoffSchFee"));
				schd.setPartialPaidAmt(rs.getBigDecimal("PartialPaidAmt"));
				schd.setTDSApplicable(rs.getBoolean("TdsApplicable"));
				schd.setSchdPftWaiver(rs.getBigDecimal("SchdPftWaiver"));
			}

			return schd;
		}

	}

	// FIXME Move to FinanceMainDAO
	@Override
	public int getDueBucket(long finID) {
		String sql = "Select DueBucket From FinanceMain Where FinID = ?";

		logger.debug(Literal.SQL + sql);

		return this.jdbcOperations.queryForObject(sql, Integer.class, finID);
	}

	/**
	 * Method for Fetch Due Schedule details against Loan Reference and Value Date
	 */
	@Override
	public List<FinanceScheduleDetail> getDueSchedulesByFacilityRef(long finID, Date valueDate) {
		StringBuilder sql = new StringBuilder("Select");
		sql.append(" schd.FinID, schd.FinReference, schd.SchDate, schd.ProfitSchd, schd.PrincipalSchd");
		sql.append(", schd.SchdPriPaid, schd.SchdPftPaid, schd.TdsApplicable");
		sql.append(" From FinScheduleDetails schd");
		sql.append(" Inner Join FinanceMain fm On fm.FinID = schd.FinID");
		sql.append(" Where fm.FinID = ? and schd.SchDate <= ?");
		sql.append(" and ((ProfitSchd + PrincipalSchd) - (SchdPftPaid - SchdPriPaid)) > 0");
		sql.append(" and fm.FinIsActive = 1");
		sql.append(" order by SchDate, FinID");

		logger.debug(Literal.SQL + sql.toString());

		return this.jdbcOperations.query(sql.toString(), ps -> {
			ps.setLong(1, finID);
			ps.setDate(2, JdbcUtil.getDate(valueDate));
		}, (rs, rowNum) -> {
			FinanceScheduleDetail schd = new FinanceScheduleDetail();

			schd.setFinID(rs.getLong("FinID"));
			schd.setFinReference(rs.getString("FinReference"));
			schd.setSchDate(rs.getDate("SchDate"));
			schd.setProfitSchd(rs.getBigDecimal("ProfitSchd"));
			schd.setPrincipalSchd(rs.getBigDecimal("PrincipalSchd"));
			schd.setSchdPriPaid(rs.getBigDecimal("SchdPriPaid"));
			schd.setSchdPftPaid(rs.getBigDecimal("SchdPftPaid"));
			schd.setTDSApplicable(rs.getBoolean("TdsApplicable"));

			return schd;
		});

	}

	@Override
	public void saveSchDueTaxDetail(ScheduleDueTaxDetail sdtd) {
		StringBuilder sql = new StringBuilder();
		sql.append("Insert Into ScheduleDueTaxDetails");
		sql.append(" (FinID, FinReference, SchDate, TaxType, TaxCalcOn, Amount, InvoiceID)");
		sql.append(" Values(?, ?, ?, ?, ?, ?, ?)");

		logger.debug(Literal.SQL + sql.toString());

		this.jdbcOperations.update(sql.toString(), (ps) -> {
			int index = 1;

			ps.setLong(index++, sdtd.getFinID());
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

	@Override
	public Long getSchdDueInvoiceID(long finID, Date schdate) {
		String sql = "Select InvoiceID From ScheduleDueTaxDetails Where FinID = ? and SchDate = ?";

		logger.debug(Literal.SQL + sql);
		try {
			return this.jdbcOperations.queryForObject(sql, Long.class, finID, schdate);
		} catch (EmptyResultDataAccessException e) {
			logger.warn(Message.NO_RECORD_FOUND);
			return null;
		}
	}

	@Override
	public void updateTDSChange(List<FinanceScheduleDetail> schedules) {
		String sql = "Update FinScheduleDetails Set TDSAmount = ? Where FinID = ? and SchDate = ?";

		logger.debug(Literal.SQL + sql);

		jdbcOperations.batchUpdate(sql, new BatchPreparedStatementSetter() {

			@Override
			public void setValues(PreparedStatement ps, int i) throws SQLException {
				FinanceScheduleDetail schedule = schedules.get(i);

				int index = 1;

				ps.setBigDecimal(index++, schedule.getTDSAmount());
				ps.setLong(index++, schedule.getFinID());
				ps.setDate(index, JdbcUtil.getDate(schedule.getSchDate()));
			}

			@Override
			public int getBatchSize() {
				return schedules.size();
			}
		});

	}

	@Override
	public List<FinanceScheduleDetail> getFinScheduleDetailsBySchPriPaid(long finID, String type, boolean isWIF) {
		StringBuilder sql = getScheduleDetailQuery(type, isWIF);
		sql.append(" Where FinID = ? and SchdPriPaid > 0 order by SchDate asc");

		logger.debug(Literal.SQL + sql.toString());

		RowMapper<FinanceScheduleDetail> rowMapper = new ScheduleDetailRowMapper(isWIF);

		return this.jdbcTemplate.getJdbcOperations().query(sql.toString(), ps -> {
			ps.setLong(1, finID);
		}, rowMapper);
	}

	@Override
	public FinanceScheduleDetail getNextUnpaidSchPayment(long finID, Date valueDate) {
		StringBuilder sql = new StringBuilder("Select * from (");
		sql.append(" Select row_number() Over(order by Schdate) row_num, ");
		appendSchdColumns(false, sql);
		sql.append(" From FinScheduleDetails");
		sql.append(" Where FinID = ? and  SchDate >= ? ");
		sql.append(" and (SchPftPaid=0 AND SchPriPaid = 0) and (RepayOnSchDate = 1 and PftOnSchDate = 1 )) T");
		sql.append(" Where row_num = ?");

		logger.debug(Literal.SQL + sql.toString());

		RowMapper<FinanceScheduleDetail> rowMapper = new ScheduleDetailRowMapper(false);

		try {
			return this.jdbcOperations.queryForObject(sql.toString(), rowMapper, finID, valueDate, 1);
		} catch (EmptyResultDataAccessException e) {
			logger.warn(Message.NO_RECORD_FOUND);
			return null;
		}
	}

	@Override
	public Date getSchdDateForDPD(long finID, Date appDate) {
		StringBuilder sql = new StringBuilder();
		sql.append(" Select Min(Schdate) From FinScheduleDetails");
		sql.append(" Where FinID = ? and SchDate <= ?");
		sql.append(" and RepayAmount > ? and SchdPriPaid = ? and SchdPftPaid = ?");

		logger.debug(Literal.SQL + sql.toString());
		return this.jdbcOperations.queryForObject(sql.toString(), Date.class, finID, appDate, 0, 0, 0);
	}

	@Override
	public List<Date> getScheduleDates(long finID, Date valueDate) {
		String sql = "Select SchDate From FinScheduleDetails Where FinID = ? and PftOnSchDate = ?";

		logger.debug(Literal.SQL + sql);

		List<Date> list = this.jdbcOperations.query(sql, ps -> {
			ps.setLong(1, finID);
			ps.setDate(2, JdbcUtil.getDate(valueDate));
		}, (rs, i) -> {
			return rs.getDate(1);
		});

		return list.stream().sorted((l1, l2) -> l1.compareTo(l2)).collect(Collectors.toList());
	}

	@Override
	public List<FinanceScheduleDetail> getSchedulesForLMSEvent(long finID) {
		return getFinScheduleDetails(finID, "", false);
	}

	@Override
	public BigDecimal getUnpaidTdsAmount(String finReference) {
		String sql = "Select coalesce(sum(TdsAmount), 0) From FinScheduleDetails Where FinReference = ? and SchPftPaid = ?";

		logger.debug(Literal.SQL + sql);

		return this.jdbcOperations.queryForObject(sql, BigDecimal.class, finReference, 0);
	}

	@Override
	public RestructureDetail getRestructureDetail(String finReference) {
		String sql = "Select RestructureDate, PriHldEndDate, EmiHldEndDate, RestructureType From Restructure_Details_Temp Where FinReference = ?";

		logger.debug(Literal.SQL + sql);

		try {
			return this.jdbcOperations.queryForObject(sql, (rs, rowNum) -> {
				RestructureDetail rd = new RestructureDetail();

				rd.setRestructureDate(JdbcUtil.getDate(rs.getDate("RestructureDate")));
				rd.setPriHldEndDate(JdbcUtil.getDate(rs.getDate("PriHldEndDate")));
				rd.setEmiHldEndDate(JdbcUtil.getDate(rs.getDate("EmiHldEndDate")));
				rd.setRestructureType(rs.getString("RestructureType"));

				return rd;
			}, finReference);
		} catch (EmptyResultDataAccessException e) {
			logger.warn(Message.NO_RECORD_FOUND);
			return null;
		}
	}

	@Override
	public FinanceProfitDetail getAmzTillLBD(String finReference) {
		String sql = "Select AmzTillLBD From FinPftDetails Where finReference = ?";

		try {
			return this.jdbcOperations.queryForObject(sql, (rs, rowNum) -> {
				FinanceProfitDetail fp = new FinanceProfitDetail();

				fp.setAmzTillLBD(rs.getBigDecimal("AmzTillLBD"));

				return fp;
			}, finReference);
		} catch (EmptyResultDataAccessException e) {
			logger.warn(Message.NO_RECORD_FOUND);
			return null;
		}
	}

	@Override
	public void updateDueTaxDetail(long oldInvoiceId) {
		String sql = "Update ScheduleDueTaxDetails set Reversal = ? Where InvoiceId = ?";

		logger.debug(Literal.SQL + sql);

		int recordCount = this.jdbcOperations.update(sql, ps -> {
			ps.setInt(1, 1);
			ps.setLong(2, oldInvoiceId);
		});

		if (recordCount <= 0) {
			throw new ConcurrencyException();
		}
	}

	@Override
	public List<FinanceScheduleDetail> getFinSchdDetailsBtwDates(String finReference, Date fromdate, Date toDate) {
		logger.debug(Literal.ENTERING);

		MapSqlParameterSource source = new MapSqlParameterSource();
		source.addValue("FinReference", finReference);
		source.addValue("FromDate", fromdate);
		source.addValue("ToDate", toDate);

		StringBuilder sql = new StringBuilder(" Select FinReference, SchDate, BalanceForPftCal,");
		sql.append(" PftDaysBasis, CalculatedRate, ProfitCalc");
		sql.append(" From FinScheduleDetails Where");
		sql.append(" FinReference = :FinReference and schdate between");
		sql.append(" (select max(schdate) from finscheduledetails where schdate < :FromDate and ");
		sql.append(" FinReference = :FinReference) and (select min(schdate) from finscheduledetails");
		sql.append(" where schdate > :ToDate and finreference = :FinReference)");

		logger.trace(Literal.SQL + sql.toString());

		RowMapper<FinanceScheduleDetail> typeRowMapper = BeanPropertyRowMapper.newInstance(FinanceScheduleDetail.class);

		logger.debug(Literal.LEAVING);
		return this.jdbcTemplate.query(sql.toString(), source, typeRowMapper);
	}

	@Override
	public void updateSchdTotals(List<FinanceScheduleDetail> schdDtls) {
		StringBuilder sql = new StringBuilder("Update FinScheduleDetails");
		sql.append(" Set SchdPftPaid = SchdPftPaid + ?, SchPftPaid = SchPftPaid + ?");
		sql.append(", SchdPriPaid = SchdPriPaid + ?, SchPriPaid = SchPriPaid + ?");
		sql.append(", TDSPaid = TDSPaid + ?,  SchdPftWaiver = SchdPftWaiver + ?");
		sql.append(" Where FinID = ? and SchDate = ?");

		logger.debug(Literal.SQL.concat(sql.toString()));

		this.jdbcOperations.batchUpdate(sql.toString(), new BatchPreparedStatementSetter() {

			@Override
			public void setValues(PreparedStatement ps, int i) throws SQLException {
				FinanceScheduleDetail curSchd = schdDtls.get(i);
				int index = 0;

				ps.setBigDecimal(++index, curSchd.getSchdPftPaid());
				ps.setBoolean(++index, curSchd.isSchPftPaid());
				ps.setBigDecimal(++index, curSchd.getSchdPriPaid());
				ps.setBoolean(++index, curSchd.isSchPriPaid());
				ps.setBigDecimal(++index, curSchd.getTDSPaid());
				ps.setBigDecimal(++index, curSchd.getSchdPftWaiver());
				ps.setLong(++index, curSchd.getFinID());
				ps.setDate(++index, JdbcUtil.getDate(curSchd.getSchDate()));
			}

			@Override
			public int getBatchSize() {
				return schdDtls.size();
			}

		});
	}

	@Override
	public Date getNextSchdDate(long finID, Date appDate) {
		String sql = "Select min(SchDate) from FinScheduleDetails Where FinID = ? and SchDate > ? and RepayOnSchDate = ?";

		logger.debug(Literal.SQL.concat(sql));

		return this.jdbcOperations.queryForObject(sql, Date.class, finID, appDate, 1);
	}

	@Override
	public Date getSchdDateForKnockOff(long finID, Date appDate) {
		String sql = "Select Max(Schdate) From FinScheduleDetails Where FinID = ? and SchDate <= ? and RepayAmount > ? and SchdPriPaid = ? and SchdPftPaid = ?";

		logger.debug(Literal.SQL.concat(sql));

		return this.jdbcOperations.queryForObject(sql, Date.class, finID, appDate, 0, 0, 0);
	}

	@Override
	public FinanceScheduleDetail getNextSchd(long finID, Date appDate, boolean businessDate) {
		StringBuilder sql = new StringBuilder();
		sql.append(" Select SchDate, RepayAmount from FinScheduleDetails");
		sql.append(" Where FinID = ? and SchDate = (Select min(SchDate) From FinScheduleDetails");
		if (businessDate) {
			sql.append(" Where FinID = ? and SchDate >= ? and RepayOnSchDate = ?)");
		} else {
			sql.append(" Where FinID = ? and SchDate > ? and RepayOnSchDate = ?)");
		}
		sql.append(" and RepayOnSchDate = ?");

		logger.debug(Literal.SQL.concat(sql.toString()));

		try {
			return this.jdbcOperations.queryForObject(sql.toString(), (rs, rowNum) -> {
				FinanceScheduleDetail schd = new FinanceScheduleDetail();

				schd.setSchDate(rs.getDate("SchDate"));
				schd.setRepayAmount(rs.getBigDecimal("RepayAmount"));

				return schd;
			}, finID, finID, appDate, 1, 1);
		} catch (EmptyResultDataAccessException e) {
			logger.warn(Message.NO_RECORD_FOUND);
			return null;
		}
	}

	@Override
	public void updateWriteOffDetail(long finID) {
		String sql = "Update FinScheduleDetails Set WriteOffPrincipal = ?, WriteOffProfit = ?, WriteOffSchFee = ? Where FinID = ?";

		logger.debug(Literal.SQL.concat(sql));

		int recordCount = this.jdbcOperations.update(sql, ps -> {
			ps.setBigDecimal(1, BigDecimal.ZERO);
			ps.setBigDecimal(2, BigDecimal.ZERO);
			ps.setBigDecimal(3, BigDecimal.ZERO);
			ps.setLong(4, finID);
		});

		if (recordCount <= 0) {
			throw new ConcurrencyException();
		}
	}
}