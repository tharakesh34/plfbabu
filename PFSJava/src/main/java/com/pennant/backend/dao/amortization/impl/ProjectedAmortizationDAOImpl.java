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
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;

import com.pennant.backend.dao.amortization.ProjectedAmortizationDAO;
import com.pennant.backend.model.amortization.AmortizationQueuing;
import com.pennant.backend.model.finance.ProjectedAccrual;
import com.pennant.backend.model.finance.ProjectedAmortization;
import com.pennant.backend.util.AmortizationConstants;
import com.pennant.eod.constants.EodConstants;
import com.pennanttech.pennapps.core.App;
import com.pennanttech.pennapps.core.App.Database;
import com.pennanttech.pennapps.core.jdbc.JdbcUtil;
import com.pennanttech.pennapps.core.jdbc.SequenceDao;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pennapps.core.resource.Message;
import com.pennanttech.pennapps.core.util.DateUtil;

public class ProjectedAmortizationDAOImpl extends SequenceDao<ProjectedAmortization>
		implements ProjectedAmortizationDAO {
	private static Logger logger = LogManager.getLogger(ProjectedAmortizationDAOImpl.class);

	private static final String UPDATE_SQL = "Update AmortizationQueuing Set ThreadId = ? Where ThreadId = ?";
	private static final String UPDATE_SQL_RC = "Update Top(?) AmortizationQueuing set ThreadId = ? Where ThreadId = ?";
	private static final String UPDATE_ORCL_RC = "Update AmortizationQueuing set ThreadId = ? Where ThreadId = ? and RowNum <= ?";

	public ProjectedAmortizationDAOImpl() {
		super();
	}

	@Override
	public List<ProjectedAmortization> getIncomeAMZDetailsByRef(long finID) {
		StringBuilder sql = new StringBuilder("Select * from (");
		sql.append(" Select fm.FinID, fm.FinReference, amz.CustID, amz.FinType, fe.FeeTypeCode");
		sql.append(", ReferenceID, IncomeTypeID, IncomeType, CalculatedOn, CalcFactor, Amount, ActualAmount");
		sql.append(", AMZMethod, MonthEndDate, AmortizedAmount, UnAmortizedAmount, CurMonthAmz, PrvMonthAmz");
		sql.append(", amz.Active, e.EntityCode, fm.FinBranch, fm.FinCcy");
		sql.append(" from IncomeAmortization amz");
		sql.append(" Inner join FinanceMain fm on fm.FinID = amz.FinID");
		sql.append(" Inner join FeeTypes fe on fe.FeeTypeID = amz.IncomeTypeID and IncomeType = ?");
		sql.append(getIncomeAMZDetailsCommonJoins());
		sql.append(" union all");
		sql.append(" Select fm.FinID, fm.FinReference, amz.CustID, amz.FinType, fe.ExpenseTypeCode FeeTypeCode");
		sql.append(", ReferenceID, IncomeTypeID, IncomeType, CalculatedOn, CalcFactor, Amount, ActualAmount");
		sql.append(", AMZMethod, MonthEndDate, AmortizedAmount, UnAmortizedAmount, CurMonthAmz, PrvMonthAmz");
		sql.append(", amz.Active, e.EntityCode, fm.FinBranch, fm.FinCcy");
		sql.append(" from IncomeAmortization amz");
		sql.append(" Inner join FinanceMain fm on fm.FinID = amz.FinID");
		sql.append(" Inner join ExpenseTypes fe on fe.ExpenseTypeID = amz.IncomeTypeID and IncomeType = ?");
		sql.append(getIncomeAMZDetailsCommonJoins());
		sql.append(" union all");
		sql.append(" Select fm.FinID, amz.FinReference, amz.CustID, amz.FinType, cbd.Type FeeTypeCode, ReferenceID");
		sql.append(", IncomeTypeID, IncomeType, CalculatedOn, CalcFactor, amz.Amount, ActualAmount, AMZMethod");
		sql.append(", MonthEndDate, AmortizedAmount, UnAmortizedAmount, CurMonthAmz, PrvMonthAmz");
		sql.append(", amz.Active, e.EntityCode, fm.FinBranch, fm.FinCcy");
		sql.append(" from IncomeAmortization amz");
		sql.append(" Inner join FinanceMain fm on fm.FinID = amz.FinID");
		sql.append(" Inner join CashBackDetails cbd on  fm.FinID = cbd.FinID");
		sql.append(" and cbd.FeeTypeID = amz.IncomeTypeID and IncomeType = ? and cbd.RetainedAmount != ?");
		sql.append(getIncomeAMZDetailsCommonJoins());
		sql.append(") amz Where amz.FinID = ?");

		logger.debug(Literal.SQL + sql.toString());

		return this.jdbcOperations.query(sql.toString(), ps -> {
			int index = 1;
			ps.setString(index++, AmortizationConstants.AMZ_INCOMETYPE_FEE);
			ps.setString(index++, AmortizationConstants.AMZ_INCOMETYPE_EXPENSE);
			ps.setString(index++, AmortizationConstants.AMZ_INCOMETYPE_SUBVENTIONAMOUNT);
			ps.setInt(index++, 0);
			ps.setLong(index, finID);
		}, (rs, rowNum) -> {
			ProjectedAmortization pamz = new ProjectedAmortization();

			pamz.setFinID(rs.getLong("FinID"));
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
		sql.append(" Inner Join RmtFinanceTypes ft on ft.Fintype = amz.Fintype");
		sql.append(" Inner Join SmtDivisionDetail d on d.DivisionCode = ft.FinDivision");
		sql.append(" Inner Join Entity e on e.EntityCode = d.EntityCode");

		return sql.toString();
	}

	@Override
	public String getAMZMethodByFinRef(long finID) {
		String sql = "Select distinct AMZMethod From IncomeAmortization Where FinID = ?";

		logger.debug(Literal.SQL + sql);

		try {
			return this.jdbcOperations.queryForObject(sql, String.class, finID);
		} catch (EmptyResultDataAccessException e) {
			logger.warn(Message.NO_RECORD_FOUND);
			return null;
		}
	}

	@Override
	public void saveBatchIncomeAMZ(List<ProjectedAmortization> amzList) {
		StringBuilder sql = new StringBuilder("Insert Into IncomeAmortization");
		sql.append(" (FinID, FinReference, CustID, FinType, ReferenceID, IncomeTypeID, IncomeType");
		sql.append(", LastMntOn, CalculatedOn, CalcFactor, Amount, ActualAmount");
		sql.append(", AMZMethod, MonthEndDate, AmortizedAmount");
		sql.append(", UnAmortizedAmount, CurMonthAmz, PrvMonthAmz, Active)");
		sql.append(" Values(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)");

		logger.debug(Literal.SQL + sql.toString());

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
				ps.setBoolean(index, amz.isActive());
			}

			@Override
			public int getBatchSize() {
				return amzList.size();
			}
		});
	}

	@Override
	public void updateBatchIncomeAMZ(List<ProjectedAmortization> amzList) {
		StringBuilder sql = new StringBuilder("Update IncomeAmortization");
		sql.append(" Set LastMntOn = ?, CalculatedOn = ?, CalcFactor = ?");
		sql.append(", Amount = ?, ActualAmount = ?, AMZMethod = ?, Active = ?");
		sql.append(" Where FinID = ? and ReferenceID = ? and IncomeType = ?");

		logger.debug(Literal.SQL + sql.toString());

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
				ps.setString(index, amz.getIncomeType());

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
		sql.append(" Set MonthEndDate = ?, CalculatedOn = ?, AmortizedAmount = ?, UnAmortizedAmount = ?");
		sql.append(", CurMonthAmz = ?, PrvMonthAmz = ?, Active = ?");
		sql.append(" Where FinID = ? and ReferenceID = ? and IncomeType = ?");

		logger.debug(Literal.SQL + sql.toString());

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
				ps.setString(index, amz.getIncomeType());
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
		sql.append(" FinID, FinReference, AccruedOn, PftAccrued, CumulativeAccrued, POSAccrued");
		sql.append(", CumulativePOS, NoOfDays, CumulativeDays, AMZPercentage, PartialPaidAmt");
		sql.append(", PartialAMZPerc, MonthEnd, AvgPOS");
		sql.append(" From ProjectedAccruals");
		sql.append(StringUtils.trimToEmpty(type));
		sql.append(" Where FinID = ?");

		logger.debug(Literal.SQL + sql.toString());

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
			logger.warn(Message.NO_RECORD_FOUND);
			return null;
		}
	}

	@Override
	public void preparePrvProjectedAccruals(Date prvMonthEndDate) {
		StringBuilder sql = new StringBuilder("Truncate Table ProjectedAccruals_Work");

		logger.debug(Literal.SQL + sql.toString());

		jdbcOperations.update(sql.toString());

		sql = new StringBuilder("Insert into ProjectedAccruals_Work");
		sql.append(" (FinID, FinReference, AccruedOn, PftAccrued, CumulativeAccrued");
		sql.append(", POSAccrued, CumulativePOS, NoOfDays, CumulativeDays, AMZPercentage, PartialPaidAmt");
		sql.append(", PartialAMZPerc, MonthEnd, AvgPOS)");
		sql.append(" Select FinID, FinReference, AccruedOn, PftAccrued, CumulativeAccrued");
		sql.append(", POSAccrued, CumulativePOS, NoOfDays, CumulativeDays, AMZPercentage, PartialPaidAmt");
		sql.append(", PartialAMZPerc, MonthEnd, AvgPOS");
		sql.append(" From ProjectedAccruals");
		sql.append(" Where AccruedOn = ?");

		logger.debug(Literal.SQL + sql.toString());

		jdbcOperations.update(sql.toString(), prvMonthEndDate);
	}

	@Override
	public List<ProjectedAccrual> getProjectedAccrualsByFinRef(long finID) {
		String sql = "Select FinID, FinReference, AccruedOn, AMZPercentage, PartialAMZPerc, MonthEnd From ProjectedAccruals Where FinID = ?";

		logger.debug(Literal.SQL + sql);

		List<ProjectedAccrual> list = this.jdbcOperations.query(sql, (rs, rowNum) -> setProjectedAccruals(rs), finID);

		return list.stream().sorted((l1, l2) -> DateUtil.compare(l1.getAccruedOn(), l2.getAccruedOn()))
				.collect(Collectors.toList());
	}

	@Override
	public List<ProjectedAccrual> getFutureProjectedAccrualsByFinRef(long finID, Date curMonthStart) {
		String sql = "Select FinID, FinReference, AccruedOn, AMZPercentage, PartialAMZPerc, MonthEnd From ProjectedAccruals Where FinID = ? and AccruedOn >= ?";

		logger.debug(Literal.SQL + sql);

		List<ProjectedAccrual> list = this.jdbcOperations.query(sql, (rs, rowNum) -> setProjectedAccruals(rs), finID,
				JdbcUtil.getDate(curMonthStart));

		return list.stream().sorted((l1, l2) -> DateUtil.compare(l1.getAccruedOn(), l2.getAccruedOn()))
				.collect(Collectors.toList());
	}

	private ProjectedAccrual setProjectedAccruals(ResultSet rs) throws SQLException {
		ProjectedAccrual pa = new ProjectedAccrual();

		pa.setFinID(rs.getLong("FinID"));
		pa.setFinReference(rs.getString("FinReference"));
		pa.setAccruedOn(rs.getTimestamp("AccruedOn"));
		pa.setAMZPercentage(rs.getBigDecimal("AMZPercentage"));
		pa.setPartialAMZPerc(rs.getBigDecimal("PartialAMZPerc"));
		pa.setMonthEnd(rs.getBoolean("MonthEnd"));

		return pa;
	}

	@Override
	public int saveBatchProjAccruals(List<ProjectedAccrual> paList) {
		StringBuilder sql = new StringBuilder("Insert into ProjectedAccruals");
		sql.append(" (FinID, FinReference, AccruedOn, PftAccrued, CumulativeAccrued, POSAccrued, CumulativePOS");
		sql.append(", NoOfDays, CumulativeDays, AMZPercentage, PartialPaidAmt, PartialAMZPerc, MonthEnd");
		sql.append(", AvgPOS");
		sql.append(") Values( ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)");

		logger.debug(Literal.SQL + sql.toString());

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
					ps.setBigDecimal(index, pa.getAvgPOS());
				}

				@Override
				public int getBatchSize() {
					return paList.size();
				}
			}).length;

		} catch (Exception e) {
			throw e;
		}
	}

	@Override
	public void deleteFutureProjAccrualsByFinRef(long finID, Date curMonthStart) {
		String sql = "Delete From ProjectedAccruals Where FinID = ? and AccruedOn >= ?";

		logger.debug(Literal.SQL + sql);

		this.jdbcOperations.update(sql, finID, curMonthStart);
	}

	@Override
	public void deleteAllProjAccrualsByFinRef(long finID) {
		String sql = "Delete From ProjectedAccruals Where FinID = ?";

		logger.debug(Literal.SQL + sql);

		this.jdbcOperations.update(sql, finID);
	}

	/**
	 * 
	 * @param projIncomeAMZ
	 */
	@Override
	public void saveBatchProjIncomeAMZ(List<ProjectedAmortization> projIncomeAMZ) {
		StringBuilder sql = new StringBuilder("Insert Into ProjectedIncomeAMZ");
		sql.append(" (FinID, FinReference, FinType, ReferenceID, IncomeTypeID, IncomeType");
		sql.append(", MonthEndDate, AmortizedAmount, CumulativeAmount, UnAmortizedAmount)");
		sql.append(" Values(?, ?, ?, ?, ?, ?, ?, ?, ?, ?)");

		logger.trace(Literal.SQL + sql.toString());

		this.jdbcOperations.batchUpdate(sql.toString(), new BatchPreparedStatementSetter() {

			@Override
			public void setValues(PreparedStatement ps, int i) throws SQLException {
				ProjectedAmortization pamz = projIncomeAMZ.get(i);

				int index = 1;

				ps.setLong(index++, pamz.getFinID());
				ps.setString(index++, pamz.getFinReference());
				ps.setString(index++, pamz.getFinType());
				ps.setLong(index++, pamz.getReferenceID());
				ps.setLong(index++, pamz.getIncomeTypeID());
				ps.setString(index++, pamz.getIncomeType());
				ps.setDate(index++, JdbcUtil.getDate(pamz.getMonthEndDate()));
				ps.setBigDecimal(index++, pamz.getAmortizedAmount());
				ps.setBigDecimal(index++, pamz.getCumulativeAmount());
				ps.setBigDecimal(index, pamz.getUnAmortizedAmount());
			}

			@Override
			public int getBatchSize() {
				return projIncomeAMZ.size();
			}
		});
	}

	@Override
	public Date getPrvAMZMonthLog() {
		StringBuilder sql = new StringBuilder("Select");
		sql.append(" al.MonthendDate from AmortizationLog al");
		sql.append(" Inner Join (Select max(ID) ID From AmortizationLog Where Status = ?) temp ON al.ID = temp.ID");

		logger.debug(Literal.SQL + sql.toString());

		try {
			return this.jdbcOperations.queryForObject(sql.toString(), Date.class, 2);
		} catch (EmptyResultDataAccessException e) {
			logger.warn(Message.NO_RECORD_FOUND);
			return null;
		}
	}

	@Override
	public long saveAmortizationLog(ProjectedAmortization pamz) {
		String sql = "Insert Into AmortizationLog (Id, MonthEndDate, Status, StartTime, EndTime, LastMntBy) Values(?, ?, ?, ?, ?, ?)";

		logger.debug(Literal.SQL + sql);

		if (pamz.getAmzLogId() <= 0) {
			pamz.setAmzLogId(getNextValue("SeqAmortizationLog"));
		}

		this.jdbcOperations.update(sql, ps -> {
			int index = 1;

			ps.setLong(index++, pamz.getAmzLogId());
			ps.setDate(index++, JdbcUtil.getDate(pamz.getMonthEndDate()));
			ps.setLong(index++, pamz.getStatus());
			ps.setTimestamp(index++, pamz.getStartTime());
			ps.setTimestamp(index++, pamz.getEndTime());
			ps.setLong(index, pamz.getLastMntBy());

		});

		return pamz.getAmzLogId();
	}

	@Override
	public boolean isAmortizationLogExist() {
		String sql = "Select count(Id) from AmortizationLog where Status in (?, ?)";

		logger.debug(Literal.SQL + sql);

		return this.jdbcOperations.queryForObject(sql, Integer.class, 0, 1) > 0;
	}

	@Override
	public ProjectedAmortization getAmortizationLog() {
		StringBuilder sql = new StringBuilder("");
		if (App.DATABASE == Database.ORACLE) {
			sql.append("Select");
			sql.append(" Id, MonthEndDate, Status, StartTime, EndTime, LastMntBy");
			sql.append(" From AmortizationLog");
			sql.append(" Where (EndTime IS NULL OR Status = ?) and RowNum = 1");
			sql.append(" ORDER BY Id DESC");
		} else if (App.DATABASE == Database.SQL_SERVER) {
			sql.append("Select");
			sql.append(" TOP 1 Id, MonthEndDate, Status, StartTime, EndTime, LastMntBy");
			sql.append(" From AmortizationLog");
			sql.append(" Where (EndTime IS NULL OR Status = ?)");
			sql.append(" ORDER BY Id DESC");
		}

		logger.debug(Literal.SQL + sql.toString());

		try {
			return jdbcOperations.queryForObject(sql.toString(), (rs, rowNum) -> {
				ProjectedAmortization pa = new ProjectedAmortization();

				pa.setAmzLogId(rs.getLong("Id"));
				pa.setMonthEndDate(JdbcUtil.getDate(rs.getDate("MonthEndDate")));
				pa.setStatus(rs.getLong("Status"));
				pa.setStartTime(rs.getTimestamp("StartTime"));
				pa.setEndTime(rs.getTimestamp("EndTime"));
				pa.setLastMntBy(rs.getLong("LastMntBy"));

				return pa;
			}, EodConstants.PROGRESS_FAILED);
		} catch (EmptyResultDataAccessException e) {
			logger.warn(Message.NO_RECORD_FOUND);
			return null;
		}
	}

	@Override
	public void updateAmzStatus(long status, long amzId) {
		String sql = "Update AmortizationLog set EndTime = ?, Status = ? Where Id =  ?";

		logger.debug(Literal.SQL + sql);

		this.jdbcOperations.update(sql, ps -> {
			int index = 1;

			ps.setTimestamp(index++, new Timestamp(System.currentTimeMillis()));
			ps.setLong(index++, status);

			ps.setLong(index, amzId);
		});
	}

	@Override
	public void deleteFutureProjAccruals(Date curMonthStart) {
		String sql = "Delete From ProjectedAccruals Where AccruedOn >= ?";

		logger.debug(Literal.SQL + sql);

		this.jdbcOperations.update(sql, ps -> ps.setDate(1, JdbcUtil.getDate(curMonthStart)));
	}

	@Override
	public void deleteAllProjAccruals() {
		String sql = "TRUNCATE TABLE ProjectedAccruals";

		logger.debug(Literal.SQL + sql);

		this.jdbcOperations.update(sql);
	}

	public ProjectedAmortization getCalAvgPOSLog() {
		StringBuilder sql = new StringBuilder("Select");
		sql.append(" Id, MonthEndDate, Status, StartTime, EndTime, LastMntBy");
		sql.append(" From CalAvgPOSLog");
		sql.append(" Where (EndTime is NULL or Status = ?) and RowNum = 1 Order By Id desc");

		logger.debug(Literal.SQL + sql.toString());

		try {
			return jdbcOperations.queryForObject(sql.toString(), (rs, rowNum) -> {
				ProjectedAmortization pa = new ProjectedAmortization();

				pa.setAmzLogId(rs.getLong("Id"));
				pa.setMonthEndDate(JdbcUtil.getDate(rs.getDate("MonthEndDate")));
				pa.setStatus(rs.getLong("Status"));
				pa.setStartTime(rs.getTimestamp("StartTime"));
				pa.setEndTime(rs.getTimestamp("EndTime"));
				pa.setLastMntBy(rs.getLong("LastMntBy"));

				return pa;
			}, EodConstants.PROGRESS_FAILED);
		} catch (EmptyResultDataAccessException e) {
			logger.warn(Message.NO_RECORD_FOUND);
			return null;
		}
	}

	@Override
	public long saveCalAvgPOSLog(ProjectedAmortization pa) {
		String sql = "Insert Into CalAvgPOSLog (Id, MonthEndDate, Status, StartTime, EndTime, LastMntBy) Values(?, ?, ?, ?, ?, ?)";

		if (pa.getAmzLogId() <= 0) {
			pa.setAmzLogId(getNextValue("SeqCalAvgPOSLog"));
		}

		logger.debug(Literal.SQL + sql);

		this.jdbcOperations.update(sql, ps -> {
			int index = 1;

			ps.setLong(index++, pa.getAmzLogId());
			ps.setDate(index++, JdbcUtil.getDate(pa.getMonthEndDate()));
			ps.setLong(index++, pa.getStatus());
			ps.setTimestamp(index++, pa.getStartTime());
			ps.setTimestamp(index++, pa.getEndTime());
			ps.setLong(index, pa.getLastMntBy());
		});

		return pa.getAmzLogId();
	}

	@Override
	public void updateCalAvgPOSStatus(long status, long amzId) {
		String sql = "Update CalAvgPOSLog Set EndTime = ?, Status = ? Where Id = ?";

		logger.debug(Literal.SQL + sql);

		this.jdbcOperations.update(sql, ps -> {
			int index = 1;

			ps.setTimestamp(index++, new Timestamp(System.currentTimeMillis()));
			ps.setLong(index++, status);
			ps.setLong(index, amzId);
		});
	}

	@Override
	public void updateBatchCalAvgPOS(List<ProjectedAccrual> projAccrualList) {
		String sql = "Update ProjectedAccruals Set AvgPOS = ? Where FinID = ? and AccruedOn = ? and MonthEnd = ?";

		logger.debug(Literal.SQL + sql);

		this.jdbcOperations.batchUpdate(sql, new BatchPreparedStatementSetter() {

			@Override
			public void setValues(PreparedStatement ps, int i) throws SQLException {
				ProjectedAccrual pa = projAccrualList.get(i);
				int index = 1;

				ps.setBigDecimal(index++, pa.getAvgPOS());

				ps.setLong(index++, pa.getFinID());
				ps.setDate(index++, JdbcUtil.getDate(pa.getAccruedOn()));
				ps.setBoolean(index, pa.isMonthEnd());
			}

			@Override
			public int getBatchSize() {
				return projAccrualList.size();
			}
		});
	}

	@Override
	public long getTotalCountByProgress() {
		StringBuilder sql = new StringBuilder("Select");
		sql.append(" Coalesce(Count(FinID), 0) from IncomeAmortization");
		sql.append(" Where FinID IN");
		sql.append("(Select Distinct FinID from AmortizationQueuing Where Progress = ?) and ActualAmount > ?");

		logger.debug(Literal.SQL + sql);

		return this.jdbcOperations.queryForObject(sql.toString(), Long.class, EodConstants.PROGRESS_WAIT, 0);
	}

	/**
	 * 
	 * @param monthEndDate
	 * @return
	 */
	@Override
	public int prepareAMZFeeDetails(Date monthEndDate, Date appDate) {
		StringBuilder sql = new StringBuilder("Insert Into IncomeAmortization");
		sql.append(" (FinID, FinReference, CustID, FinType, ReferenceID, IncomeTypeID, IncomeType");
		sql.append(", LastMntOn, CalculatedOn, CalcFactor");
		sql.append(", Amount, ActualAmount, AMZMethod, MonthEndDate");
		sql.append(", AmortizedAmount, UnAmortizedAmount, CurMonthAmz, PrvMonthAmz, Active)");
		sql.append(" Select T4.FinID, T4.FinReference, T4.CUSTID, T4.FINTYPE, T1.FeeID, T1.FeeTypeID");
		sql.append(", ?, ?, ?, Coalesce(T1.TaxPercent, 0) CalcFactor");
		sql.append(", T1.ActualAmount Amount, ? ActualAmount, T3.AMZMethod, ?");
		sql.append(", ? AmortizedAmount, ? UnAmortizedAmount, ? CurMonthAmz, ? PrvMonthAmz, ?");
		sql.append(" From FinFeeDetail T1");
		sql.append(" Inner Join FeeTypes T2 on T1.FeeTypeID = T2.FeeTypeID and T2.AmortzReq = ?");
		sql.append(" Inner Join FinPftDetails T3 on T1.FinID = T3.FinID");
		sql.append(" Inner Join FinanceMain T4 on T1.FinID = T3.FinID");
		sql.append(" Where T1.ActualAmount - T1.WaivedAmount > ?");
		sql.append(" and (T1.POSTDATE >= ? and T1.PostDate <= ?) ");
		sql.append(" and T1.FeeID not in(Select ReferenceID From IncomeAmortization Where IncomeType = ?)");

		logger.debug(Literal.SQL + sql.toString());

		return this.jdbcOperations.update(sql.toString(), ps -> {
			int index = 1;

			ps.setString(index++, AmortizationConstants.AMZ_INCOMETYPE_FEE);
			ps.setDate(index++, JdbcUtil.getDate(DateUtil.getSysDate()));
			ps.setDate(index++, JdbcUtil.getDate(appDate));
			ps.setInt(index++, 0);
			ps.setDate(index++, JdbcUtil.getDate(monthEndDate));
			ps.setInt(index++, 0);
			ps.setInt(index++, 0);
			ps.setInt(index++, 0);
			ps.setInt(index++, 0);
			ps.setBoolean(index++, true);
			ps.setInt(index++, 1);
			ps.setInt(index++, 0);
			ps.setDate(index++, JdbcUtil.getDate(DateUtil.getMonthStart(monthEndDate)));
			ps.setDate(index++, JdbcUtil.getDate(monthEndDate));
			ps.setString(index, AmortizationConstants.AMZ_INCOMETYPE_FEE);
		});
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
		sql.append(" Inner join ExpenseTypes et on et.ExpenseTypeId = ed.ExpenseTypeId and et.AmortReq = 1");
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
			ps.setString(index, AmortizationConstants.AMZ_INCOMETYPE_EXPENSE);
		});

	}

	@Override
	public void updateActualAmount(Date appDate) {
		StringBuilder sql = new StringBuilder();

		if (App.DATABASE == Database.POSTGRES) {
			sql.append("Update IncomeAmortization t1 set ActualAmount = t2.ActualAmount");
			sql.append(", UnAmortizedAmount = t2.ActualAmount");
			sql.append(" from (Select t1.ReferenceId, IncomeType");
			sql.append(", Coalesce(round((Amount * 100)/(100 + CalcFactor)), 0) - t2.WaivedAmount ActualAmount");
			sql.append(" From IncomeAmortization t1");
			sql.append(" Inner Join FinFeeDetail t2 on t1.ReferenceId = t2.FeeID");
			sql.append(" Where IncomeType = ? and CalculatedOn = ?) t2");
			sql.append(" Where T1.ReferenceId = t2.ReferenceId and t2.IncomeType = t2.IncomeType");
		} else if (App.DATABASE == Database.SQL_SERVER) {
			sql.append("Update t1 Set t1.ActualAmount = t2.ActualAmount, t1.UnAmortizedAmount = t2.ActualAmount");
			sql.append(" From IncomeAmortization t1");
			sql.append(" Inner Join (Select t1.ReferenceId, IncomeType");
			sql.append(", (Coalesce((Round(((Amount/100) * 100)/(100 + CalcFactor), 0)* 100), 0) - t2.WaivedAmount)");
			sql.append(" ActualAmount");
			sql.append(" From IncomeAmortization t1");
			sql.append(" Inner Join FinFeeDetail t2 on t1.ReferenceId = t2.FeeID");
			sql.append(" Where IncomeType = ? and CalculatedOn = ?) t2");
			sql.append(" ON t1.ReferenceId = t2.ReferenceId and t2.IncomeType = t2.IncomeType");
		} else if (App.DATABASE == Database.ORACLE) {
			sql.append("Merge Into IncomeAmortization t1");
			sql.append(" using (Select T1.ReferenceId, IncomeType");
			sql.append(", Coalesce(Round((Amount * 100)/(100 + CalcFactor)), 0) - T2.WaivedAmount ActualAmount");
			sql.append(" From incomeamortization t1");
			sql.append(" Inner Join FinFeeDetail t2 on t1.ReferenceId = t2.FeeID");
			sql.append(" Where IncomeType = ? and CalculatedOn = ?) t2");
			sql.append(" on (t1.ReferenceId = T2.ReferenceId and T1.IncomeType = T2.IncomeType)");
			sql.append(" When Matched Then Update Set t1.ActualAmount = t2.ActualAmount");
			sql.append(", T1.UnAmortizedAmount = T2.ActualAmount");
		}

		logger.debug(Literal.SQL + sql.toString());

		this.jdbcOperations.update(sql.toString(), ps -> {
			int index = 1;

			ps.setString(index++, AmortizationConstants.AMZ_INCOMETYPE_FEE);
			ps.setDate(index, JdbcUtil.getDate(appDate));
		});
	}

	@Override
	public void deleteFutureProjAMZByMonthEnd(Date amzMonth) {
		String sql = "Delete From ProjectedIncomeAMZ where FinID in (Select FinID From AmortizationQueuing) and MonthEndDate >= ?";

		logger.debug(Literal.SQL + sql);

		this.jdbcOperations.update(sql, ps -> ps.setDate(1, JdbcUtil.getDate(amzMonth)));
	}

	@Override
	public void truncateAndInsertProjAMZ(Date amzMonth) {
		String sql = "Truncate Table ProjectedIncomeAMZ_WORK";

		logger.debug(Literal.SQL + sql);

		this.jdbcOperations.update(sql);

		sql = "Insert Into ProjectedIncomeamz_Work Select * From ProjectedIncomeAmz WHERE MonthEndDate < ?";

		logger.debug(Literal.SQL + sql);

		this.jdbcOperations.update(sql, ps -> ps.setDate(1, JdbcUtil.getDate(amzMonth)));

		try {
			if (App.DATABASE == Database.SQL_SERVER) {
				sql = "Drop Index Idx_ProjIncAmz_Eom on ProjectedIncomeAmz";
			} else {
				sql = "Drop Index Idx_ProjIncAmz_Eom";
			}

			logger.debug(Literal.SQL + sql);

			this.jdbcOperations.update(sql);
		} catch (DataAccessException e) {
			logger.warn("Index (Idx_ProjIncAmz_Eom) not found.");
		}

		sql = "TRUNCATE TABLE PROJECTEDINCOMEAMZ";

		logger.debug(Literal.SQL + sql);

		this.jdbcOperations.update(sql);
	}

	@Override
	public void copyPrvProjAMZ() {
		String sql = "Insert Into ProjectedIncomeAmz Select * From ProjectedIncomeamz_Work";

		logger.debug(Literal.SQL + sql);

		this.jdbcOperations.update(sql);
	}

	@Override
	public void createIndexProjIncomeAMZ() {
		String sql = "CREATE INDEX IDX_PROJINCAMZ_EOM ON PROJECTEDINCOMEAMZ (MONTHENDDATE) ";

		logger.debug(Literal.SQL + sql);

		this.jdbcOperations.update(sql);
	}

	@Override
	public void logAmortizationQueuing() {
		String sql = "Insert Into AmortizationQueuing_Log Select * From AmortizationQueuing";

		logger.debug(Literal.SQL + sql);

		this.jdbcOperations.update(sql);
	}

	@Override
	public void delete() {
		String sql = "TRUNCATE TABLE AmortizationQueuing";

		logger.debug(Literal.SQL + sql);

		this.jdbcOperations.update(sql);
	}

	@Override
	public int prepareAmortizationQueue(Date amzMonth, boolean isEOMProcess) {
		StringBuilder sql = new StringBuilder("Insert Into AmortizationQueuing");
		sql.append(" (FinID, FinReference, CustId, EodDate, ThreadId, Progress, StartTime, EodProcess)");
		sql.append(" Select FinID, FinReference, CustID, ?, ?, ?, ?, ? From FinanceMain");
		sql.append(" Where (ClosedDate is NULL or ClosedDate >= ?)");
		sql.append(" and FinID IN (Select FinID From IncomeAmortization Where Active = 1) ");

		logger.trace(Literal.SQL + sql.toString());

		return this.jdbcOperations.update(sql.toString(), ps -> {
			int index = 1;

			ps.setDate(index++, JdbcUtil.getDate(amzMonth));
			ps.setInt(index++, 0);
			ps.setInt(index++, EodConstants.PROGRESS_WAIT);
			ps.setDate(index++, JdbcUtil.getDate(DateUtil.getSysDate()));
			ps.setBoolean(index++, isEOMProcess);
			ps.setDate(index, JdbcUtil.getDate(DateUtil.getMonthStart(amzMonth)));
		});
	}

	@Override
	public long getCountByProgress() {
		String sql = "Select Count(FinID) from AmortizationQueuing where Progress = ?";

		logger.debug(Literal.SQL + sql);

		return this.jdbcOperations.queryForObject(sql, Long.class, String.valueOf(EodConstants.PROGRESS_WAIT));
	}

	@Override
	public void updateStatus(long finID, int progress) {
		String sql = "Update AmortizationQueuing Set EndTime = ?, Progress = ? Where FinID = ?";

		logger.debug(Literal.SQL + sql);

		this.jdbcOperations.update(sql, ps -> {
			int index = 1;

			ps.setDate(index++, JdbcUtil.getDate(DateUtil.getSysDate()));
			ps.setInt(index++, progress);
			ps.setLong(index, finID);
		});
	}

	@Override
	public void updateFailed(AmortizationQueuing amz) {
		String sql = "Update AmortizationQueuing Set EndTime = ?, ThreadId = ?, Progress = ? Where FinID = ?";

		logger.debug(Literal.SQL + sql);

		this.jdbcOperations.update(sql, ps -> {
			int index = 1;

			ps.setDate(index++, JdbcUtil.getDate(amz.getEndTime()));
			ps.setInt(index++, amz.getThreadId());
			ps.setInt(index++, amz.getProgress());

			ps.setLong(index, amz.getFinID());
		});
	}

	@Override
	public int updateThreadIDByRowNumber(Date date, long noOfRows, int threadId) {
		if (noOfRows == 0) {
			return defaultUpdate(threadId);
		} else {
			if (App.DATABASE == Database.SQL_SERVER) {
				logger.trace(Literal.SQL + UPDATE_SQL_RC);

				return this.jdbcOperations.update(UPDATE_SQL_RC, ps -> {
					int index = 1;

					ps.setLong(index++, noOfRows);
					ps.setInt(index++, threadId);
					ps.setInt(index, 0);
				});
			} else if (App.DATABASE == Database.ORACLE) {
				logger.trace(Literal.SQL + UPDATE_ORCL_RC);

				return this.jdbcOperations.update(UPDATE_ORCL_RC, ps -> {
					int index = 1;

					ps.setInt(index++, threadId);
					ps.setInt(index++, 0);
					ps.setLong(index, noOfRows);
				});
			} else {
				return defaultUpdate(threadId);
			}
		}
	}

	private int defaultUpdate(int threadId) {
		logger.trace(Literal.SQL + UPDATE_SQL);
		return this.jdbcOperations.update(UPDATE_SQL, ps -> {
			int index = 1;

			ps.setString(index++, String.valueOf(threadId));
			ps.setString(index, String.valueOf(0));
		});
	}

	@Override
	public int prepareSVFeeDetails(Date monthEndDate, Date appDate) {
		StringBuilder sql = new StringBuilder("Insert Into IncomeAmortization");
		sql.append(" (FinID, FinReference, CustID, FinType, ReferenceID, IncomeTypeID");
		sql.append(", IncomeType, LastMntOn, CalculatedOn, CalcFactor");
		sql.append(", Amount, ActualAmount, AMZMethod, MonthEndDate");
		sql.append(", AmortizedAmount, UnAmortizedAmount, CurMonthAmz, PrvMonthAmz, Active)");
		sql.append(" SELECT T2.FinID, T2.FinReference, T2.CUSTID, T2.FINTYPE, T1.ID, T1.FEETYPEID, ?");
		sql.append(", ?, ?, 0 CalcFactor");
		sql.append(", T1.RetainedAmount, T1.RetainedAmount ActualAmount, T2.AMZMethod, ?");
		sql.append(", 0 AmortizedAmount, T1.Amount UnAmortizedAmount, 0 CurMonthAmz, 0 PrvMonthAmz, ?");
		sql.append(" From CashBackDetails T1");
		sql.append(" Inner Join FinPftDetails T2 ON T2.FinID = T1.FinID");
		sql.append(" Where T1.RetainedAmount > 0");
		sql.append(" AND T1.ID NOT IN (Select ReferenceID From INCOMEAMORTIZATION WHERE IncomeType = ?)");

		logger.debug(Literal.SQL + sql.toString());

		return this.jdbcOperations.update(sql.toString(), ps -> {
			int index = 1;

			ps.setString(index++, AmortizationConstants.AMZ_INCOMETYPE_SUBVENTIONAMOUNT);
			ps.setDate(index++, JdbcUtil.getDate(DateUtil.getSysDate()));
			ps.setDate(index++, JdbcUtil.getDate(appDate));
			ps.setDate(index++, JdbcUtil.getDate(monthEndDate));
			ps.setBoolean(index++, true);
			ps.setString(index, AmortizationConstants.AMZ_INCOMETYPE_SUBVENTIONAMOUNT);
		});
	}
}
