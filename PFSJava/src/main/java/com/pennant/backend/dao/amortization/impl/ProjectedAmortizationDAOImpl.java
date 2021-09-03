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
 * * FileName : ProjectedAmortizationDAOImpl.java * * Author : PENNANT TECHONOLOGIES * * Creation Date : 23-01-2018 * *
 * Modified Date : 23-01-2018 * * Description : * *
 ********************************************************************************************
 * Date Author Version Comments *
 ********************************************************************************************
 * 23-01-2018 Pennant 0.1 * * * * * * * * *
 ********************************************************************************************
 */
package com.pennant.backend.dao.amortization.impl;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.PreparedStatementSetter;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSourceUtils;

import com.pennant.app.util.DateUtility;
import com.pennant.backend.dao.amortization.ProjectedAmortizationDAO;
import com.pennant.backend.model.amortization.AmortizationQueuing;
import com.pennant.backend.model.amortization.ProjectedAmortization;
import com.pennant.backend.model.finance.ProjectedAccrual;
import com.pennant.backend.util.AmortizationConstants;
import com.pennant.eod.constants.EodConstants;
import com.pennanttech.pennapps.core.App;
import com.pennanttech.pennapps.core.App.Database;
import com.pennanttech.pennapps.core.jdbc.JdbcUtil;
import com.pennanttech.pennapps.core.jdbc.SequenceDao;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pennapps.core.util.DateUtil;

public class ProjectedAmortizationDAOImpl extends SequenceDao<ProjectedAmortization>
		implements ProjectedAmortizationDAO {
	private static Logger logger = LogManager.getLogger(ProjectedAmortizationDAOImpl.class);

	private static final String UPDATE_SQL = "UPDATE AmortizationQueuing set ThreadId = :ThreadId Where ThreadId = :AcThreadId";
	private static final String UPDATE_SQL_RC = "UPDATE Top(:RowCount) AmortizationQueuing set ThreadId = :ThreadId Where ThreadId = :AcThreadId";
	private static final String UPDATE_ORCL_RC = "UPDATE AmortizationQueuing set ThreadId = :ThreadId Where ROWNUM <= :RowCount AND ThreadId = :AcThreadId";

	private static final String START_FINREF_RC = "UPDATE AmortizationQueuing set Progress = :Progress, StartTime = :StartTime "
			+ " Where FinReference = :FinReference AND Progress = :ProgressWait";

	public ProjectedAmortizationDAOImpl() {
		super();
	}

	@Override
	public List<ProjectedAmortization> getIncomeAMZDetailsByRef(long finID) {
		StringBuilder sql = new StringBuilder("Select * from (");
		sql.append(" Select amz.FinID, amz.FinReference, amz.CustID, amz.FinType, fe.FeeTypeCode");
		sql.append(", ReferenceID, IncomeTypeID, IncomeType, CalculatedOn, CalcFactor, Amount, ActualAmount");
		sql.append(", AMZMethod, MonthEndDate, AmortizedAmount, UnAmortizedAmount, CurMonthAmz, PrvMonthAmz");
		sql.append(", amz.Active, e.EntityCode, fm.FinBranch, fm.FinCcy");
		sql.append(" from IncomeAmortization amz");
		sql.append(" Inner join FinanceMain fm on fm.FinID = amz.FinID");
		sql.append(" Inner join FeeTypes fe on fe.FeeTypeID = amz.IncomeTypeID and IncomeType = ?");
		sql.append(getIncomeAMZDetailsCommonJoins());
		sql.append(" union all");
		sql.append(" Select amz.FinID, amz.FinReference, amz.CustID, amz.FinType, fe.ExpenseTypeCode FeeTypeCode");
		sql.append(", ReferenceID, IncomeTypeID, IncomeType, CalculatedOn, CalcFactor, Amount, ActualAmount");
		sql.append(", AMZMethod, MonthEndDate, AmortizedAmount, UnAmortizedAmount, CurMonthAmz, PrvMonthAmz");
		sql.append(", amz.Active, e.EntityCode, fm.FinBranch, fm.FinCcy");
		sql.append(" from IncomeAmortization amz");
		sql.append(" Inner join FinanceMain fm on fm.FinID = amz.FinID");
		sql.append(" Inner join ExpenseTypes fe on fe.ExpenseTypeID = amz.IncomeTypeID and IncomeType = ?");
		sql.append(getIncomeAMZDetailsCommonJoins());
		sql.append(") amz Where amz.FinID = ?");

		logger.trace(Literal.SQL + sql.toString());

		return this.jdbcOperations.query(sql.toString(), ps -> {
			ps.setString(1, AmortizationConstants.AMZ_INCOMETYPE_FEE);
			ps.setString(2, AmortizationConstants.AMZ_INCOMETYPE_EXPENSE);
			ps.setLong(3, finID);
		}, (rs, rowNum) -> {
			ProjectedAmortization pamz = new ProjectedAmortization();

			pamz.setFinID(rs.getLong("FInID"));
			pamz.setFinReference(rs.getString("FinReference"));
			pamz.setCustID(rs.getLong("CustID"));
			pamz.setFinType(rs.getString("FinType"));
			pamz.setFeeTypeCode(rs.getString("FeeTypeCode"));
			pamz.setReferenceID(rs.getLong("ReferenceID"));
			pamz.setIncomeTypeID(rs.getLong("IncomeTypeID"));
			pamz.setIncomeType(rs.getString("IncomeType"));
			pamz.setCalculatedOn(rs.getTimestamp("CalculatedOn"));
			pamz.setCalcFactor(rs.getBigDecimal("CalcFactor"));
			pamz.setAmount(rs.getBigDecimal("Amount"));
			pamz.setActualAmount(rs.getBigDecimal("ActualAmount"));
			pamz.setaMZMethod(rs.getString("AMZMethod"));
			pamz.setMonthEndDate(rs.getTimestamp("MonthEndDate"));
			pamz.setAmortizedAmount(rs.getBigDecimal("AmortizedAmount"));
			pamz.setUnAmortizedAmount(rs.getBigDecimal("UnAmortizedAmount"));
			pamz.setCurMonthAmz(rs.getBigDecimal("CurMonthAmz"));
			pamz.setPrvMonthAmz(rs.getBigDecimal("PrvMonthAmz"));
			pamz.setActive(rs.getBoolean("Active"));
			pamz.setEntityCode(rs.getString("EntityCode"));
			pamz.setFinBranch(rs.getString("FinBranch"));
			pamz.setFinCcy(rs.getString("FinCcy"));

			return pamz;
		});
	}

	private String getIncomeAMZDetailsCommonJoins() {
		StringBuilder sql = new StringBuilder();
		sql.append(" Inner join RmtFinanceTypes ft on ft.Fintype = amz.Fintype");
		sql.append(" Inner join SmtDivisionDetail d on d.DivisionCode = ft.FinDivision");
		sql.append(" Inner join Entity e on e.EntityCode = d.EntityCode");

		return sql.toString();
	}

	@Override
	public String getAMZMethodByFinRef(long finID) {
		String sql = "Select distinct AMZMethod From IncomeAmortization Where FinID = ?";

		logger.trace(Literal.SQL + sql.toString());

		try {
			return this.jdbcOperations.queryForObject(sql.toString(), String.class, finID);
		} catch (EmptyResultDataAccessException e) {
			//
		}
		return null;
	}

	@Override
	public void saveBatchIncomeAMZ(List<ProjectedAmortization> amzList) {
		StringBuilder sql = new StringBuilder("Insert Into IncomeAmortization");
		sql.append(" (FinID, FinReference, CustID, FinType, ReferenceID, IncomeTypeID, IncomeType");
		sql.append(", LastMntOn, CalculatedOn, CalcFactor, Amount, ActualAmount");
		sql.append(", AMZMethod, MonthEndDate, AmortizedAmount");
		sql.append(", UnAmortizedAmount, CurMonthAmz, PrvMonthAmz, Active)");
		sql.append(" Values(?, ?, ?, ?, ?, ?, ? , ?, ?, ?, ?, ? , ?, ?, ? , ?, ?, ?, ?)");

		logger.trace(Literal.SQL + sql.toString());

		this.jdbcOperations.batchUpdate(sql.toString(), new BatchPreparedStatementSetter() {

			@Override
			public void setValues(PreparedStatement ps, int i) throws SQLException {
				ProjectedAmortization amz = amzList.get(i);

				int index = 1;

				ps.setLong(index++, amz.getFinID());
				ps.setString(index++, amz.getFinReference());
				ps.setLong(index++, amz.getCustID());
				ps.setString(index++, amz.getFinType());
				ps.setLong(index++, amz.getReferenceID());
				ps.setLong(index++, amz.getIncomeTypeID());
				ps.setString(index++, amz.getIncomeType());
				ps.setDate(index++, JdbcUtil.getDate(amz.getLastMntOn()));
				ps.setDate(index++, JdbcUtil.getDate(amz.getCalculatedOn()));
				ps.setBigDecimal(index++, amz.getCalcFactor());
				ps.setBigDecimal(index++, amz.getAmount());
				ps.setBigDecimal(index++, amz.getActualAmount());
				ps.setString(index++, amz.getaMZMethod());
				ps.setDate(index++, JdbcUtil.getDate(amz.getMonthEndDate()));
				ps.setBigDecimal(index++, amz.getAmortizedAmount());
				ps.setBigDecimal(index++, amz.getUnAmortizedAmount());
				ps.setBigDecimal(index++, amz.getCurMonthAmz());
				ps.setBigDecimal(index++, amz.getPrvMonthAmz());
				ps.setBoolean(index++, amz.isActive());
			}

			@Override
			public int getBatchSize() {
				return amzList.size();
			}
		});
	}

	/**
	 * PRESENT UPDATE NOT ALLOWED
	 * 
	 * FIXME : Validate / verify update fields when update come into picture
	 * 
	 * @param amortizationList
	 */
	@Override
	public void updateBatchIncomeAMZ(List<ProjectedAmortization> amzList) {
		StringBuilder sql = new StringBuilder("Update IncomeAmortization");
		sql.append(" Set LastMntOn = ?, CalculatedOn = ?, CalcFactor = ?");
		sql.append(", Amount = ?, ActualAmount = ?, AMZMethod = ?, Active = ?");
		sql.append(" Where FinID = ? and ReferenceID = ? and IncomeType = ?");

		logger.trace(Literal.SQL + sql.toString());

		this.jdbcOperations.batchUpdate(sql.toString(), new BatchPreparedStatementSetter() {

			@Override
			public void setValues(PreparedStatement ps, int i) throws SQLException {
				ProjectedAmortization amz = amzList.get(i);

				int index = 1;

				ps.setDate(index++, JdbcUtil.getDate(amz.getLastMntOn()));
				ps.setDate(index++, JdbcUtil.getDate(amz.getCalculatedOn()));
				ps.setBigDecimal(index++, amz.getCalcFactor());
				ps.setBigDecimal(index++, amz.getAmount());
				ps.setBigDecimal(index++, amz.getActualAmount());
				ps.setString(index++, amz.getaMZMethod());
				ps.setBoolean(index++, amz.isActive());

				ps.setLong(index++, amz.getFinID());
				ps.setLong(index++, amz.getReferenceID());
				ps.setString(index++, amz.getIncomeType());

			}

			@Override
			public int getBatchSize() {
				return amzList.size();
			}
		});
	}

	/**
	 * 
	 * @param amortizationList
	 */
	@Override
	public void updateBatchIncomeAMZAmounts(List<ProjectedAmortization> amzList) {
		StringBuilder sql = new StringBuilder("Update IncomeAmortization");
		sql.append(" Set MonthEndDate = ?e, CalculatedOn = ?, AmortizedAmount = ?, UnAmortizedAmount = ?");
		sql.append(", CurMonthAmz = ?, PrvMonthAmz = ?, Active = ?");
		sql.append(" Where FinID = ? and ReferenceID = ? AND IncomeType = ?");

		logger.trace(Literal.SQL + sql.toString());

		this.jdbcOperations.batchUpdate(sql.toString(), new BatchPreparedStatementSetter() {

			@Override
			public void setValues(PreparedStatement ps, int i) throws SQLException {
				ProjectedAmortization amz = amzList.get(i);

				int index = 1;

				ps.setDate(index++, JdbcUtil.getDate(amz.getMonthEndDate()));
				ps.setDate(index++, JdbcUtil.getDate(amz.getCalculatedOn()));
				ps.setBigDecimal(index++, amz.getAmortizedAmount());
				ps.setBigDecimal(index++, amz.getUnAmortizedAmount());
				ps.setBigDecimal(index++, amz.getCurMonthAmz());
				ps.setBigDecimal(index++, amz.getPrvMonthAmz());
				ps.setBoolean(index++, amz.isActive());

				ps.setLong(index++, amz.getFinID());
				ps.setLong(index++, amz.getReferenceID());
				ps.setString(index++, amz.getIncomeType());

			}

			@Override
			public int getBatchSize() {
				return amzList.size();
			}
		});
	}

	@Override
	public ProjectedAccrual getPrvProjectedAccrual(long finID, Date prvMonthEndDate, String type) {
		StringBuilder sql = new StringBuilder("Select");
		sql.append(" FinID, FinReference, AccruedOn, PftAccrued, CumulativeAccrued, POSAccrued, CumulativePOS");
		sql.append(", NoOfDays, CumulativeDays, AMZPercentage, PartialPaidAmt, PartialAMZPerc, MonthEnd");
		sql.append(", AvgPOS");
		sql.append(" From ProjectedAccruals");
		sql.append(StringUtils.trimToEmpty(type));
		sql.append(" Where FinID = ?");

		logger.trace(Literal.SQL + sql.toString());

		try {
			return this.jdbcOperations.queryForObject(sql.toString(), (rs, rowNum) -> {
				ProjectedAccrual pa = new ProjectedAccrual();

				pa.setFinID(rs.getLong("FinID"));
				pa.setFinReference(rs.getString("FinReference"));
				pa.setAccruedOn(rs.getTimestamp("AccruedOn"));
				pa.setPftAccrued(rs.getBigDecimal("PftAccrued"));
				pa.setCumulativeAccrued(rs.getBigDecimal("CumulativeAccrued"));
				pa.setPOSAccrued(rs.getBigDecimal("POSAccrued"));
				pa.setCumulativePOS(rs.getBigDecimal("CumulativePOS"));
				pa.setNoOfDays(rs.getInt("NoOfDays"));
				pa.setCumulativeDays(rs.getInt("CumulativeDays"));
				pa.setAMZPercentage(rs.getBigDecimal("AMZPercentage"));
				pa.setPartialPaidAmt(rs.getBigDecimal("PartialPaidAmt"));
				pa.setPartialAMZPerc(rs.getBigDecimal("PartialAMZPerc"));
				pa.setMonthEnd(rs.getBoolean("MonthEnd"));
				pa.setAvgPOS(rs.getBigDecimal("AvgPOS"));
				pa.setAccruedOn(prvMonthEndDate);

				return pa;
			}, finID);
		} catch (EmptyResultDataAccessException e) {
			//
		}

		return null;
	}

	@Override
	public void preparePrvProjectedAccruals(Date prvMonthEndDate) {
		logger.debug(Literal.ENTERING);

		StringBuilder sql = new StringBuilder("truncate table ProjectedAccruals_Work");

		logger.trace(Literal.SQL + sql.toString());
		jdbcOperations.update(sql.toString());

		sql = new StringBuilder("Insert into ProjectedAccruals_Work");
		sql.append(" (FinID, FinReference, AccruedOn, PftAccrued, CumulativeAccrued");
		sql.append(", POSAccrued, CumulativePOS, NoOfDays, CumulativeDays, AMZPercentage, PartialPaidAmt");
		sql.append(", PartialAMZPerc, MonthEnd, AvgPOS)");
		sql.append(" SELECT FinID, FinReference, AccruedOn, PftAccrued, CumulativeAccrued");
		sql.append(", POSAccrued, CumulativePOS, NoOfDays, CumulativeDays, AMZPercentage, PartialPaidAmt");
		sql.append(", PartialAMZPerc, MonthEnd, AvgPOS");
		sql.append(" FROM ProjectedAccruals");
		sql.append(" Where AccruedOn = ?");

		logger.trace(Literal.SQL + sql.toString());

		jdbcOperations.update(sql.toString(), prvMonthEndDate);
	}

	@Override
	public List<ProjectedAccrual> getProjectedAccrualsByFinRef(long finID) {
		StringBuilder sql = new StringBuilder("Select");
		sql.append(" FinID, FinReference, AccruedOn, AMZPercentage, PartialAMZPerc, MonthEnd");
		sql.append(" From ProjectedAccruals");
		sql.append(" Where FinID = ? order by AccruedOn");

		logger.trace(Literal.SQL + sql.toString());

		return this.jdbcOperations.query(sql.toString(), (rs, rowNum) -> {
			ProjectedAccrual pa = new ProjectedAccrual();

			pa.setFinID(rs.getLong("FinID"));
			pa.setFinReference(rs.getString("FinReference"));
			pa.setAccruedOn(rs.getDate("AccruedOn"));
			pa.setAMZPercentage(rs.getBigDecimal("AMZPercentage"));
			pa.setMonthEnd(rs.getBoolean("MonthEnd"));

			return pa;
		}, finID);
	}

	@Override
	public List<ProjectedAccrual> getFutureProjectedAccrualsByFinRef(long finID, Date curMonthStart) {
		logger.debug(Literal.ENTERING);

		StringBuilder sql = new StringBuilder("Select");
		sql.append(" FinID, FinReference, AccruedOn, AMZPercentage, PartialAMZPerc, MonthEnd");
		sql.append(" From ProjectedAccruals");
		sql.append(" Where FinID = ? and AccruedOn >= ? order by AccruedOn");

		logger.trace(Literal.SQL + sql.toString());

		return this.jdbcOperations.query(sql.toString(), new PreparedStatementSetter() {
			@Override
			public void setValues(PreparedStatement ps) throws SQLException {
				int index = 1;
				ps.setLong(index++, finID);
				ps.setDate(index++, JdbcUtil.getDate(curMonthStart));
			}
		}, (rs, rowNum) -> {
			ProjectedAccrual pamz = new ProjectedAccrual();

			pamz.setFinReference(rs.getString("FinReference"));
			pamz.setAccruedOn(rs.getTimestamp("AccruedOn"));
			pamz.setAMZPercentage(rs.getBigDecimal("AMZPercentage"));
			pamz.setPartialAMZPerc(rs.getBigDecimal("PartialAMZPerc"));
			pamz.setMonthEnd(rs.getBoolean("MonthEnd"));

			return pamz;
		});

	}

	@Override
	public int saveBatchProjAccruals(List<ProjectedAccrual> paList) {
		StringBuilder sql = new StringBuilder("Insert into ProjectedAccruals");
		sql.append(" (FinID, FinReference, AccruedOn, PftAccrued, CumulativeAccrued, POSAccrued, CumulativePOS");
		sql.append(", NoOfDays, CumulativeDays, AMZPercentage, PartialPaidAmt, PartialAMZPerc, MonthEnd");
		sql.append(", AvgPOS");
		sql.append(") values(");
		sql.append("?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?");
		sql.append(")");

		try {
			return this.jdbcOperations.batchUpdate(sql.toString(), new BatchPreparedStatementSetter() {

				@Override
				public void setValues(PreparedStatement ps, int i) throws SQLException {
					ProjectedAccrual pa = paList.get(i);

					int index = 1;

					ps.setLong(index++, pa.getFinID());
					ps.setString(index++, pa.getFinReference());
					ps.setDate(index++, JdbcUtil.getDate(pa.getAccruedOn()));
					ps.setBigDecimal(index++, pa.getPftAccrued());
					ps.setBigDecimal(index++, pa.getCumulativeAccrued());
					ps.setBigDecimal(index++, pa.getPOSAccrued());
					ps.setBigDecimal(index++, pa.getCumulativePOS());
					ps.setInt(index++, pa.getNoOfDays());
					ps.setInt(index++, pa.getCumulativeDays());
					ps.setBigDecimal(index++, pa.getAMZPercentage());
					ps.setBigDecimal(index++, pa.getPartialPaidAmt());
					ps.setBigDecimal(index++, pa.getPartialAMZPerc());
					ps.setBoolean(index++, pa.isMonthEnd());
					ps.setBigDecimal(index++, pa.getAvgPOS());
				}

				@Override
				public int getBatchSize() {
					return paList.size();
				}
			}).length;

		} catch (Exception e) {
			for (ProjectedAccrual projAccrls : paList) {
				logger.info("FinID {}", projAccrls.getFinID());
				logger.info("FinReference {}", projAccrls.getFinReference());
				logger.info("AccruedOn {}", JdbcUtil.getDate(projAccrls.getAccruedOn()));
				logger.info("PftAccrued {}", projAccrls.getPftAccrued());
				logger.info("CumulativeAccrued {}", projAccrls.getCumulativeAccrued());
				logger.info("POSAccrued {}", projAccrls.getPOSAccrued());
				logger.info("CumulativePOS {}", projAccrls.getCumulativePOS());
				logger.info("NoOfDays {}", projAccrls.getNoOfDays());
				logger.info("CumulativeDays {}", projAccrls.getCumulativeDays());
				logger.info("AMZPercentage {}", projAccrls.getAMZPercentage());
				logger.info("PartialPaidAmt {}", projAccrls.getPartialPaidAmt());
				logger.info("PartialAMZPerc {}", projAccrls.getPartialAMZPerc());
				logger.info("MonthEnd {}", projAccrls.isMonthEnd());
				logger.info("AvgPOS {}", projAccrls.getAvgPOS());
			}
			throw e;
		}
	}

	@Override
	public void deleteFutureProjAccrualsByFinRef(long finID, Date curMonthStart) {
		String sql = "Delete From ProjectedAccruals Where FinID = ? and AccruedOn >= ?";

		logger.trace(Literal.SQL + sql);

		this.jdbcOperations.update(sql, finID, curMonthStart);
	}

	@Override
	public void deleteAllProjAccrualsByFinRef(long finID) {
		String sql = "Delete From ProjectedAccruals Where FinID = ?";

		logger.trace(Literal.SQL + sql);

		this.jdbcOperations.update(sql, finID);
	}

	@Override
	public Date getPrvAMZMonthLog() {
		StringBuilder sql = new StringBuilder("Select");
		sql.append(" al.MonthendDate from AmortizationLog al");
		sql.append("  Inner Join (Select max(ID) ID From AmortizationLog Where Status = ?) temp ON al.ID = temp.ID");

		logger.trace(Literal.SQL + sql.toString());

		try {
			return this.jdbcOperations.queryForObject(sql.toString(), Date.class, 2);
		} catch (EmptyResultDataAccessException e) {
			//
		}

		return null;
	}

	@Override
	public long saveAmortizationLog(ProjectedAmortization pamz) {
		StringBuilder sql = new StringBuilder("Insert Into AmortizationLog");
		sql.append(" (Id, MonthEndDate, Status, StartTime, EndTime, LastMntBy)");
		sql.append(" Values(?, ?, ?, ?, ?, ?)");

		logger.trace(Literal.SQL + sql.toString());

		if (pamz.getAmzLogId() <= 0) {
			pamz.setAmzLogId(getNextValue("SeqAmortizationLog"));
		}

		this.jdbcOperations.update(sql.toString(), ps -> {

		});

		return pamz.getAmzLogId();
	}

	@Override
	public boolean isAmortizationLogExist() {
		String sql = "Select count(Id) from AmortizationLog where Status in (?, ?)";
		return this.jdbcOperations.queryForObject(sql, Integer.class, 0, 1) > 0;
	}

	@Override
	public ProjectedAmortization getAmortizationLog() {
		StringBuilder sql = new StringBuilder();
		if (App.DATABASE == Database.ORACLE) {
			sql.append(" Select Id, MonthEndDate, Status, StartTime, EndTime, LastMntBy  FROM AmortizationLog ");
			sql.append(" WHERE ( EndTime IS NULL OR Status = " + EodConstants.PROGRESS_FAILED
					+ " ) AND ROWNUM = 1 ORDER BY Id DESC");

		} else if (App.DATABASE == Database.SQL_SERVER) {
			sql.append(" Select TOP 1 Id, MonthEndDate, Status, StartTime, EndTime, LastMntBy  FROM AmortizationLog");
			sql.append(" WHERE ( EndTime IS NULL OR Status = " + EodConstants.PROGRESS_FAILED + " ) ORDER BY Id DESC");
		}

		logger.trace(Literal.SQL + sql.toString());

		ProjectedAmortization amzLog = new ProjectedAmortization();
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(amzLog);
		RowMapper<ProjectedAmortization> typeRowMapper = BeanPropertyRowMapper.newInstance(ProjectedAmortization.class);

		try {
			amzLog = jdbcTemplate.queryForObject(sql.toString(), beanParameters, typeRowMapper);
		} catch (EmptyResultDataAccessException e) {
			amzLog = null;
		}
		return amzLog;
	}

	@Override
	public void updateAmzStatus(long status, long amzId) {
		logger.debug(Literal.ENTERING);

		StringBuilder sql = new StringBuilder("Update AmortizationLog set");
		sql.append(" EndTime = :EndTime, Status = :Status");
		sql.append(" Where Id = :Id ");

		logger.trace(Literal.SQL + sql.toString());

		MapSqlParameterSource source = new MapSqlParameterSource();
		source.addValue("Status", status);
		source.addValue("Id", amzId);
		source.addValue("EndTime", new Timestamp(System.currentTimeMillis()));

		this.jdbcTemplate.update(sql.toString(), source);

		logger.debug(Literal.LEAVING);
	}

	@Override
	public void deleteFutureProjAccruals(Date curMonthStart) {
		StringBuilder sql = new StringBuilder("DELETE FROM ProjectedAccruals");
		sql.append(" WHERE AccruedOn >= :AccruedOn");

		logger.trace(Literal.SQL + sql.toString());

		MapSqlParameterSource source = new MapSqlParameterSource();
		source.addValue("AccruedOn", curMonthStart);

		this.jdbcTemplate.update(sql.toString(), source);
	}

	@Override
	public void deleteAllProjAccruals() {
		StringBuilder sql = new StringBuilder("DELETE FROM ProjectedAccruals");
		logger.trace(Literal.SQL + sql.toString());

		MapSqlParameterSource source = new MapSqlParameterSource();

		this.jdbcTemplate.update(sql.toString(), source);
	}

	// Calculate Average POS

	public ProjectedAmortization getCalAvgPOSLog() {
		StringBuilder sql = new StringBuilder();
		sql.append(" Select Id, MonthEndDate, Status, StartTime, EndTime, LastMntBy  FROM CalAvgPOSLog");
		sql.append(" WHERE (EndTime IS NULL OR Status = " + EodConstants.PROGRESS_FAILED
				+ ") AND ROWNUM = 1 ORDER BY Id DESC");

		logger.trace(Literal.SQL + sql.toString());

		ProjectedAmortization amzLog = new ProjectedAmortization();
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(amzLog);
		RowMapper<ProjectedAmortization> typeRowMapper = BeanPropertyRowMapper.newInstance(ProjectedAmortization.class);

		try {
			amzLog = jdbcTemplate.queryForObject(sql.toString(), beanParameters, typeRowMapper);
		} catch (EmptyResultDataAccessException e) {
			amzLog = null;
		}
		return amzLog;
	}

	@SuppressWarnings("deprecation")
	@Override
	public long saveCalAvgPOSLog(ProjectedAmortization proAmortization) {
		logger.debug(Literal.ENTERING);

		StringBuilder sql = new StringBuilder("Insert Into CalAvgPOSLog");
		sql.append(" (Id, MonthEndDate, Status, StartTime, EndTime, LastMntBy)");
		sql.append(" Values(:Id, :MonthEndDate, :Status, :StartTime, :EndTime, :LastMntBy)");

		// Get the identity sequence number.
		if (proAmortization.getAmzLogId() <= 0) {
			proAmortization.setAmzLogId(getNextValue("SeqCalAvgPOSLog"));
		}

		logger.trace(Literal.SQL + sql.toString());

		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(proAmortization);
		this.jdbcTemplate.update(sql.toString(), beanParameters);

		logger.debug(Literal.LEAVING);
		return proAmortization.getAmzLogId();
	}

	@Override
	public void updateCalAvgPOSStatus(long status, long amzId) {
		logger.debug(Literal.ENTERING);

		StringBuilder sql = new StringBuilder("Update CalAvgPOSLog set");
		sql.append(" EndTime = :EndTime, Status = :Status");
		sql.append(" Where Id = :Id ");

		logger.trace(Literal.SQL + sql.toString());

		MapSqlParameterSource source = new MapSqlParameterSource();
		source.addValue("Status", status);
		source.addValue("Id", amzId);
		source.addValue("EndTime", new Timestamp(System.currentTimeMillis()));

		this.jdbcTemplate.update(sql.toString(), source);

		logger.debug(Literal.LEAVING);
	}

	/**
	 * 
	 * @param projAccrualList
	 */
	@Override
	public void updateBatchCalAvgPOS(List<ProjectedAccrual> projAccrualList) {
		logger.debug(Literal.ENTERING);

		StringBuilder sql = new StringBuilder("Update ProjectedAccruals SET AvgPOS = :AvgPOS");
		sql.append(" Where FinReference = :FinReference AND AccruedOn = :AccruedOn AND MonthEnd = :MonthEnd");

		logger.trace(Literal.SQL + sql.toString());

		SqlParameterSource[] beanParameters = SqlParameterSourceUtils.createBatch(projAccrualList.toArray());
		this.jdbcTemplate.batchUpdate(sql.toString(), beanParameters);

		logger.debug(Literal.LEAVING);
	}

	// End Calculate Average POS

	// Amortization Queuing

	@Override
	public long getTotalCountByProgress() {
		logger.debug(Literal.ENTERING);

		AmortizationQueuing amortizationQueuing = new AmortizationQueuing();
		amortizationQueuing.setProgress(EodConstants.PROGRESS_WAIT);

		StringBuilder sql = new StringBuilder();
		sql.append("SELECT COUNT(FinReference) from IncomeAmortization");
		sql.append(" Where FinReference IN ");
		sql.append(" ( Select DISTINCT FinReference from AmortizationQueuing");
		sql.append(" where Progress = :Progress) AND ActualAmount > 0");

		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(amortizationQueuing);
		long progressCount = this.jdbcTemplate.queryForObject(sql.toString(), beanParameters, Long.class);

		logger.debug(Literal.LEAVING);
		return progressCount;
	}

	@Override
	public int updateThreadIDByRowNumber(Date date, long noOfRows, int threadId) {
		logger.debug(Literal.ENTERING);

		MapSqlParameterSource source = new MapSqlParameterSource();
		source.addValue("RowCount", noOfRows);
		source.addValue("ThreadId", threadId);
		source.addValue("EodDate", date);
		source.addValue("AcThreadId", "0");

		try {
			if (noOfRows == 0) {
				logger.trace(Literal.SQL + UPDATE_SQL);
				return this.jdbcTemplate.update(UPDATE_SQL, source);
			} else {
				if (App.DATABASE == Database.SQL_SERVER) {
					logger.trace(Literal.SQL + UPDATE_SQL_RC);
					return this.jdbcTemplate.update(UPDATE_SQL_RC, source);
				} else if (App.DATABASE == Database.ORACLE) {
					logger.trace(Literal.SQL + UPDATE_ORCL_RC);
					return this.jdbcTemplate.update(UPDATE_ORCL_RC, source);
				} else {
					logger.trace(Literal.SQL + UPDATE_SQL);
					return this.jdbcTemplate.update(UPDATE_SQL, source);
				}
			}

		} catch (EmptyResultDataAccessException dae) {
			logger.error(Literal.EXCEPTION, dae);
		}

		logger.debug(Literal.LEAVING);
		return 0;
	}

	/**
	 * 
	 * @param monthEndDate
	 * @return
	 */
	@Override
	public int prepareAMZFeeDetails(Date monthEndDate, Date appDate) {
		logger.debug(Literal.ENTERING);

		StringBuilder sql = new StringBuilder("Insert Into IncomeAmortization");
		sql.append(" (FinReference, CustID, FinType, ReferenceID, IncomeTypeID, IncomeType");
		sql.append(", LastMntOn, CalculatedOn, CalcFactor");
		sql.append(", Amount, ActualAmount, AMZMethod, MonthEndDate");
		sql.append(", AmortizedAmount, UnAmortizedAmount, CurMonthAmz, PrvMonthAmz, Active)");
		sql.append(" SELECT T3.FinReference, T3.CUSTID, T3.FINTYPE, T1.FeeID, T1.FeeTypeID, :IncomeType");
		sql.append(", :LastMntOn, :CalculatedOn, COALESCE(T1.TaxPercent, 0) CalcFactor");
		sql.append(", T1.ActualAmount Amount, 0 ActualAmount, T3.AMZMethod, :MonthEndDate");
		sql.append(", 0 AmortizedAmount, 0 UnAmortizedAmount, 0 CurMonthAmz, 0 PrvMonthAmz, :Active");
		sql.append(" From FinFeeDetail T1 ");
		sql.append(" INNER JOIN FeeTypes T2 ON T1.FeeTypeID = T2.FeeTypeID AND T2.AmortzReq = 1");
		sql.append(" INNER JOIN FINPFTDETAILS T3 ON T1.FinReference = T3.FinReference ");
		sql.append(" WHERE T1.ActualAmount - T1.WaivedAmount > 0");
		sql.append(" AND (T1.POSTDATE >= :MonthStartDate AND T1.POSTDATE <= :MonthEndDate) ");
		sql.append(" AND T1.FeeID NOT IN (Select ReferenceID From INCOMEAMORTIZATION WHERE IncomeType = :IncomeType)");

		logger.trace(Literal.SQL + sql.toString());

		MapSqlParameterSource source = new MapSqlParameterSource();
		source.addValue("Active", true);
		source.addValue("CalculatedOn", appDate);
		source.addValue("LastMntOn", DateUtility.getSysDate());
		source.addValue("MonthEndDate", monthEndDate);
		source.addValue("MonthStartDate", DateUtility.getMonthStart(monthEndDate));
		source.addValue("IncomeType", AmortizationConstants.AMZ_INCOMETYPE_FEE);

		logger.debug(Literal.LEAVING);
		return this.jdbcTemplate.update(sql.toString(), source);
	}

	@Override
	public int prepareAMZExpenseDetails(Date monthEndDate, Date appDate) {
		StringBuilder sql = new StringBuilder("Insert Into IncomeAmortization");
		sql.append(" (FinID, FinReference, CustID, FinType, ReferenceID, IncomeTypeID, IncomeType");
		sql.append(", LastMntOn, CalculatedOn, CalcFactor, Amount, ActualAmount, AMZMethod");
		sql.append(", MonthEndDate, AmortizedAmount, UnAmortizedAmount, CurMonthAmz, PrvMonthAmz, Active)");
		sql.append(" Select fm.FinID, fm.FinReference, fm.CustID, fm.FinType, ed.FinExpenseId, ed.ExpenseTypeId");
		sql.append(", ?, ?, ?, 0 CalcFactor, ed.Amount, ed.Amount actualamount, pd.AMZMethod, ?");
		sql.append(", 0 AmortizedAmount, ed.Amount UnAmortizedAmount, 0 CurMonthAmz, 0 PrvMonthAmz, ?");
		sql.append(" From FinExpenseDetails ed");
		sql.append(" Inner join ExpenseTypes et on et.ExpenseTypeId = ed.ExpenseTypeId and t2.AmortReq = 1");
		sql.append(" Inner Join FinanceMain fm on fm.FinID = ed.FinID");
		sql.append(" Inner Join FinPftDetails pd on pd.FinID = fm.FinID");
		sql.append(" Where ed.Amount > 0 and ed.FinExpenseId");
		sql.append(" not in (Select ReferenceID From IncomeAmortization Where IncomeType = ?)");

		logger.debug(Literal.SQL + sql.toString());

		return this.jdbcOperations.update(sql.toString(), ps -> {
			int index = 1;

			ps.setString(index++, AmortizationConstants.AMZ_INCOMETYPE_EXPENSE);
			ps.setDate(index++, JdbcUtil.getDate(DateUtil.getSysDate()));
			ps.setDate(index++, JdbcUtil.getDate(appDate));
			ps.setDate(index++, JdbcUtil.getDate(monthEndDate));
			ps.setBoolean(index++, true);
			ps.setString(index++, AmortizationConstants.AMZ_INCOMETYPE_EXPENSE);
		});

	}

	@Override
	public void updateActualAmount(Date appDate) {
		StringBuilder sql = new StringBuilder();

		if (App.DATABASE == Database.POSTGRES) {
			sql.append(" Update INCOMEAMORTIZATION T1 set ACTUALAMOUNT = T2.ACTUALAMOUNT");
			sql.append(", UnAmortizedAmount = T2.ACTUALAMOUNT");
			sql.append(" from (Select T1.REFERENCEID, INCOMETYPE");
			sql.append(", COALESCE(ROUND((Amount * 100)/(100 + CalcFactor)), 0) - T2.WaivedAmount ActualAmount");
			sql.append(" From INCOMEAMORTIZATION T1");
			sql.append(" INNER JOIN FinFeeDetail T2 ON T1.REFERENCEID = T2.FeeID");
			sql.append(" Where INCOMETYPE = :IncomeType AND CalculatedOn = :CalculatedOn) T2 ");
			sql.append(" Where T1.REFERENCEID = T2.REFERENCEID AND T1.INCOMETYPE = T2.INCOMETYPE");

		} else if (App.DATABASE == Database.SQL_SERVER) {
			sql.append(" UPDATE T1 SET T1.ACTUALAMOUNT = T2.ACTUALAMOUNT, T1.UnAmortizedAmount = T2.ACTUALAMOUNT");
			sql.append(" FROM INCOMEAMORTIZATION T1  ");
			sql.append(" INNER JOIN (Select T1.REFERENCEID, INCOMETYPE");
			sql.append(
					", (COALESCE((ROUND(((Amount/100) * 100)/(100 + CalcFactor), 0)* 100), 0) - T2.WaivedAmount) ActualAmount");
			sql.append(" From INCOMEAMORTIZATION T1");
			sql.append(" INNER JOIN FinFeeDetail T2 ON T1.ReferenceID = T2.FeeID");
			sql.append(" Where INCOMETYPE = :IncomeType AND CalculatedOn = :CalculatedOn) T2 ");
			sql.append(" ON T1.REFERENCEID = T2.REFERENCEID AND T1.INCOMETYPE = T2.INCOMETYPE");

		} else if (App.DATABASE == Database.ORACLE) {
			sql.append(" MERGE INTO INCOMEAMORTIZATION T1 ");
			sql.append(" USING (Select T1.REFERENCEID, INCOMETYPE");
			sql.append(", COALESCE(ROUND((Amount * 100)/(100 + CalcFactor)), 0) - T2.WaivedAmount ActualAmount");
			sql.append(" From INCOMEAMORTIZATION T1");
			sql.append(" INNER JOIN FinFeeDetail T2 ON T1.ReferenceID = T2.FeeID");
			sql.append(" Where INCOMETYPE = :IncomeType and CalculatedOn = :CalculatedOn) T2 ");
			sql.append(" ON (T1.REFERENCEID = T2.REFERENCEID AND T1.INCOMETYPE = T2.INCOMETYPE)");
			sql.append(" WHEN MATCHED THEN UPDATE SET T1.ACTUALAMOUNT = T2.ACTUALAMOUNT, ");
			sql.append(" T1.UnAmortizedAmount = T2.ACTUALAMOUNT");
		}

		logger.trace(Literal.SQL + sql.toString());

		MapSqlParameterSource source = new MapSqlParameterSource();
		source.addValue("CalculatedOn", appDate);
		source.addValue("IncomeType", AmortizationConstants.AMZ_INCOMETYPE_FEE);

		this.jdbcTemplate.update(sql.toString(), source);
	}

	/**
	 * Delete Future Amortizations
	 */
	@Override
	public void deleteFutureProjAMZByMonthEnd(Date amzMonth) {
		logger.debug(Literal.ENTERING);

		StringBuilder sql = new StringBuilder();
		sql.append("Delete From ProjectedIncomeAMZ");
		sql.append(" where FinReference IN (Select FinReference From AmortizationQueuing)");
		sql.append(" and MonthEndDate >= :MonthEndDate");

		logger.trace(Literal.SQL + sql.toString());

		MapSqlParameterSource source = new MapSqlParameterSource();
		source.addValue("MonthEndDate", amzMonth);

		this.jdbcTemplate.update(sql.toString(), source);

		logger.debug(Literal.LEAVING);
	}

	/**
	 * Delete and insert into working table previous Amortizations
	 */
	@Override
	public void truncateAndInsertProjAMZ(Date amzMonth) {
		logger.debug(Literal.ENTERING);

		MapSqlParameterSource source = new MapSqlParameterSource();
		source.addValue("MonthEndDate", amzMonth);

		// truncate working table
		StringBuilder sql = new StringBuilder("TRUNCATE TABLE ProjectedIncomeAMZ_WORK");
		logger.trace(Literal.SQL + sql.toString());

		this.jdbcTemplate.update(sql.toString(), source);

		// insert into working table
		sql = new StringBuilder("INSERT INTO ProjectedIncomeAMZ_WORK");
		sql.append(" SELECT * FROM PROJECTEDINCOMEAMZ WHERE MonthEndDate < :MonthEndDate");

		logger.trace(Literal.SQL + sql.toString());
		this.jdbcTemplate.update(sql.toString(), source);

		// Drop Index temporary
		try {
			if (App.DATABASE == Database.SQL_SERVER) {
				sql = new StringBuilder("DROP INDEX IDX_PROJINCAMZ_EOM ON PROJECTEDINCOMEAMZ");
			} else {
				sql = new StringBuilder("DROP INDEX IDX_PROJINCAMZ_EOM");
			}

			logger.trace(Literal.SQL + sql.toString());
			this.jdbcTemplate.update(sql.toString(), source);

		} catch (Exception e) {
			logger.warn(Literal.EXCEPTION, e);
		}

		// truncate main table
		sql = new StringBuilder("TRUNCATE TABLE PROJECTEDINCOMEAMZ");

		logger.trace(Literal.SQL + sql.toString());
		this.jdbcTemplate.update(sql.toString(), source);

		logger.debug(Literal.LEAVING);
	}

	/**
	 * Copy previous Amortizations from Working table to Main table
	 * 
	 */
	@Override
	public void copyPrvProjAMZ() {
		logger.debug(Literal.ENTERING);

		StringBuilder sql = new StringBuilder("INSERT INTO PROJECTEDINCOMEAMZ SELECT * FROM ProjectedIncomeAMZ_WORK");
		logger.debug("sql4 : " + sql.toString());
		this.jdbcTemplate.update(sql.toString(), new MapSqlParameterSource());

		logger.debug(Literal.LEAVING);
	}

	/**
	 * Create INDEX for MONTHENDDATE
	 * 
	 */
	@Override
	public void createIndexProjIncomeAMZ() {
		logger.debug(Literal.ENTERING);

		try {
			StringBuilder indexSql = new StringBuilder(
					" CREATE INDEX IDX_PROJINCAMZ_EOM ON PROJECTEDINCOMEAMZ (MONTHENDDATE) ");

			logger.debug("indexSql : " + indexSql.toString());
			this.jdbcTemplate.update(indexSql.toString(), new MapSqlParameterSource());

		} catch (Exception e) {
			logger.warn(Literal.EXCEPTION, e);
		}

		logger.debug(Literal.LEAVING);
	}

	@Override
	public void logAmortizationQueuing() {
		logger.debug(Literal.ENTERING);

		StringBuilder sql = new StringBuilder("INSERT INTO AmortizationQueuing_Log");
		sql.append(" SELECT * FROM AmortizationQueuing");

		logger.trace(Literal.SQL + sql.toString());

		this.jdbcTemplate.getJdbcOperations().update(sql.toString());

		logger.debug(Literal.LEAVING);
	}

	@Override
	public void delete() {
		logger.debug(Literal.ENTERING);

		StringBuilder deleteSql = new StringBuilder("TRUNCATE TABLE AmortizationQueuing");
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(new AmortizationQueuing());

		logger.debug("deleteSql: " + deleteSql.toString());
		this.jdbcTemplate.update(deleteSql.toString(), beanParameters);

		logger.debug(Literal.LEAVING);
	}

	@Override
	public int prepareAmortizationQueue(Date amzMonth, boolean isEOMProcess) {
		logger.debug(Literal.ENTERING);

		AmortizationQueuing amortizationQueuing = new AmortizationQueuing();
		amortizationQueuing.setThreadId(0);
		amortizationQueuing.setEodDate(amzMonth);
		amortizationQueuing.setEodProcess(isEOMProcess);
		amortizationQueuing.setProgress(EodConstants.PROGRESS_WAIT);

		amortizationQueuing.setStartTime(DateUtil.getSysDate());

		Date curMonthStart = DateUtil.getMonthStart(amzMonth);
		amortizationQueuing.setMonthEndDate(curMonthStart);

		StringBuilder sql = new StringBuilder();
		sql.append(
				"INSERT INTO AmortizationQueuing (FINREFERENCE, CUSTID, EODDATE, THREADID, PROGRESS, STARTTIME, EODPROCESS)");
		sql.append(
				" SELECT FinReference, CustID, :EodDate, :ThreadId, :Progress, :StartTime, :EodProcess From FinanceMain");
		sql.append(" WHERE (ClosedDate IS NULL OR ClosedDate >= :MonthEndDate)");
		sql.append(" AND FinReference IN (Select FinReference From IncomeAmortization) ");

		logger.trace(Literal.SQL + sql.toString());

		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(amortizationQueuing);
		int financeRecords = this.jdbcTemplate.update(sql.toString(), beanParameters);

		logger.debug(Literal.LEAVING);
		return financeRecords;
	}

	@Override
	public long getCountByProgress() {
		MapSqlParameterSource parameterSource = new MapSqlParameterSource();
		parameterSource.addValue("Progress", String.valueOf(EodConstants.PROGRESS_WAIT));

		StringBuilder sql = new StringBuilder();
		sql.append("SELECT COUNT(FinReference) from AmortizationQueuing where Progress = :Progress");

		long progressCount = this.jdbcTemplate.queryForObject(sql.toString(), parameterSource, Long.class);

		return progressCount;
	}

	@Override
	public void updateStatus(long finID, int progress) {
		String sql = "Update AmortizationQueuing Set EndTime = ?, Progress = ? Where FinID = ?";

		this.jdbcOperations.update(sql, ps -> {
			int index = 1;

			ps.setDate(index++, JdbcUtil.getDate(DateUtil.getSysDate()));
			ps.setInt(index++, progress);
			ps.setLong(index++, finID);

		});
	}

	@Override
	public void updateFailed(AmortizationQueuing amz) {
		String sql = "Update AmortizationQueuing Set EndTime = ?, ThreadId = ?, Progress = ? Where FinID = ?";

		this.jdbcOperations.update(sql, ps -> {
			int index = 1;

			ps.setDate(index++, JdbcUtil.getDate(amz.getEndTime()));
			ps.setInt(index++, amz.getThreadId());
			ps.setInt(index++, amz.getProgress());
			ps.setLong(index++, amz.getFinID());

		});
	}
}
