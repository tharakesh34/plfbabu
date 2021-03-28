package com.pennant.backend.dao.finance.impl;

import java.math.BigDecimal;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
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

import com.pennant.backend.dao.finance.FinServiceInstrutionDAO;
import com.pennant.backend.model.finance.FinServiceInstruction;
import com.pennant.backend.model.finance.LMSServiceLog;
import com.pennanttech.pennapps.core.jdbc.JdbcUtil;
import com.pennanttech.pennapps.core.jdbc.SequenceDao;
import com.pennanttech.pennapps.core.resource.Literal;

public class FinServiceInstrutionDAOImpl extends SequenceDao<FinServiceInstruction> implements FinServiceInstrutionDAO {
	private static Logger logger = LogManager.getLogger(FinServiceInstrutionDAOImpl.class);

	public FinServiceInstrutionDAOImpl() {
		super();
	}

	public int saveList(List<FinServiceInstruction> finServiceInstructionList, String type) {
		for (FinServiceInstruction finSerList : finServiceInstructionList) {
			if (finSerList.getServiceSeqId() == Long.MIN_VALUE) {
				finSerList.setServiceSeqId(getNextValue("SeqFinInstruction"));
			}
		}

		String sql = getInsertQuery(type);

		logger.trace(Literal.SQL + sql.toString());

		return this.jdbcOperations.batchUpdate(sql.toString(), new BatchPreparedStatementSetter() {

			@Override
			public void setValues(PreparedStatement ps, int i) throws SQLException {
				FinServiceInstruction fsd = finServiceInstructionList.get(i);

				int index = 1;
				ps.setLong(index++, fsd.getServiceSeqId());
				ps.setString(index++, fsd.getFinEvent());
				ps.setString(index++, fsd.getFinReference());
				ps.setDate(index++, JdbcUtil.getDate(fsd.getFromDate()));
				ps.setDate(index++, JdbcUtil.getDate(fsd.getToDate()));
				ps.setString(index++, fsd.getPftDaysBasis());
				ps.setString(index++, fsd.getSchdMethod());
				ps.setBigDecimal(index++, fsd.getActualRate());
				ps.setString(index++, fsd.getBaseRate());
				ps.setString(index++, fsd.getSplRate());
				ps.setBigDecimal(index++, fsd.getMargin());
				ps.setDate(index++, JdbcUtil.getDate(fsd.getGrcPeriodEndDate()));
				ps.setString(index++, fsd.getRepayPftFrq());
				ps.setString(index++, fsd.getRepayRvwFrq());
				ps.setString(index++, fsd.getRepayCpzFrq());
				ps.setString(index++, fsd.getGrcPftFrq());
				ps.setString(index++, fsd.getGrcRvwFrq());
				ps.setString(index++, fsd.getGrcCpzFrq());
				ps.setDate(index++, JdbcUtil.getDate(fsd.getNextGrcRepayDate()));
				ps.setString(index++, fsd.getRepayFrq());
				ps.setDate(index++, JdbcUtil.getDate(fsd.getNextRepayDate()));
				ps.setBigDecimal(index++, fsd.getAmount());
				ps.setString(index++, fsd.getRecalType());
				ps.setDate(index++, JdbcUtil.getDate(fsd.getRecalFromDate()));
				ps.setDate(index++, JdbcUtil.getDate(fsd.getRecalToDate()));
				ps.setBoolean(index++, fsd.isPftIntact());
				ps.setInt(index++, fsd.getTerms());
				ps.setString(index++, fsd.getServiceReqNo());
				ps.setString(index++, fsd.getRemarks());
				ps.setBigDecimal(index++, fsd.getPftChg());
				ps.setLong(index++, fsd.getInstructionUID());
				ps.setLong(index++, fsd.getLinkedTranID());
			}

			@Override
			public int getBatchSize() {
				return finServiceInstructionList.size();
			}
		}).length;
	}

	private String getInsertQuery(String type) {
		StringBuilder sql = new StringBuilder("Insert into");
		sql.append(" FinServiceInstruction");
		sql.append(StringUtils.trimToEmpty(type));
		sql.append(" (ServiceSeqId, FinEvent, FinReference, FromDate, ToDate, PftDaysBasis");
		sql.append(", SchdMethod, ActualRate, BaseRate, SplRate, Margin, GrcPeriodEndDate");
		sql.append(", RepayPftFrq, RepayRvwFrq, RepayCpzFrq, GrcPftFrq, GrcRvwFrq, GrcCpzFrq");
		sql.append(", NextGrcRepayDate, RepayFrq, NextRepayDate, Amount, RecalType, RecalFromDate");
		sql.append(", RecalToDate, PftIntact, Terms, ServiceReqNo, Remarks, PftChg");
		sql.append(", InstructionUID, LinkedTranID)");
		sql.append(" values(");
		sql.append(" ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?");
		sql.append(", ?, ?, ?, ?, ?, ?");
		sql.append(")");

		return sql.toString();
	}

	/**
	 * 
	 * @param finServiceInstruction
	 * @param type
	 */
	public void save(FinServiceInstruction sd, String type) {
		if (sd.getServiceSeqId() == Long.MIN_VALUE) {
			sd.setServiceSeqId(getNextValue("SeqFinInstruction"));
		}

		String sql = getInsertQuery(type);

		logger.trace(Literal.SQL + sql);

		try {
			jdbcOperations.update(sql.toString(), ps -> {
				int index = 1;

				ps.setLong(index++, JdbcUtil.setLong(sd.getServiceSeqId()));
				ps.setString(index++, sd.getFinEvent());
				ps.setString(index++, sd.getFinReference());
				ps.setDate(index++, JdbcUtil.getDate(sd.getFromDate()));
				ps.setDate(index++, JdbcUtil.getDate(sd.getToDate()));
				ps.setString(index++, sd.getPftDaysBasis());
				ps.setString(index++, sd.getSchdMethod());
				ps.setBigDecimal(index++, sd.getActualRate());
				ps.setString(index++, sd.getBaseRate());
				ps.setString(index++, sd.getSplRate());
				ps.setBigDecimal(index++, sd.getMargin());
				ps.setDate(index++, JdbcUtil.getDate(sd.getGrcPeriodEndDate()));
				ps.setString(index++, sd.getRepayPftFrq());
				ps.setString(index++, sd.getRepayRvwFrq());
				ps.setString(index++, sd.getRepayCpzFrq());
				ps.setString(index++, sd.getGrcPftFrq());
				ps.setString(index++, sd.getGrcRvwFrq());
				ps.setString(index++, sd.getGrcCpzFrq());
				ps.setDate(index++, JdbcUtil.getDate(sd.getNextGrcRepayDate()));
				ps.setString(index++, sd.getRepayFrq());
				ps.setDate(index++, JdbcUtil.getDate(sd.getNextRepayDate()));
				ps.setBigDecimal(index++, sd.getAmount());
				ps.setString(index++, sd.getRecalType());
				ps.setDate(index++, JdbcUtil.getDate(sd.getRecalFromDate()));
				ps.setDate(index++, JdbcUtil.getDate(sd.getRecalToDate()));
				ps.setBoolean(index++, sd.isPftIntact());
				ps.setInt(index++, sd.getTerms());
				ps.setString(index++, sd.getServiceReqNo());
				ps.setString(index++, sd.getRemarks());
				ps.setBigDecimal(index++, sd.getPftChg());
				ps.setLong(index++, JdbcUtil.setLong(sd.getInstructionUID()));
				ps.setLong(index++, JdbcUtil.setLong(sd.getLinkedTranID()));
			});
		} catch (Exception e) {
			logger.warn("");
			throw e;
		}

	}

	@Override
	public void deleteList(String finReference, String finEvent, String type) {
		logger.debug("Entering");

		FinServiceInstruction finServiceInstruction = new FinServiceInstruction();
		finServiceInstruction.setFinReference(finReference);
		finServiceInstruction.setFinEvent(finEvent);

		StringBuilder deleteSql = new StringBuilder("Delete From ");
		deleteSql.append(" FinServiceInstruction");
		deleteSql.append(StringUtils.trimToEmpty(type));
		deleteSql.append("  Where  FinReference = :FinReference ");
		logger.debug("deleteSql: " + deleteSql.toString());

		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(finServiceInstruction);
		this.jdbcTemplate.update(deleteSql.toString(), beanParameters);
		logger.debug("Leaving");
	}

	/**
	 * Fetch the Record Repay Instruction Detail details by key field
	 * 
	 * @param id
	 *            (String)
	 * @param type
	 *            (String) ""/_Temp/_View
	 * @return RepayInstruction
	 */
	@Override
	public List<FinServiceInstruction> getFinServiceInstructions(final String finReference, String type,
			String finEvent) {
		logger.debug(Literal.ENTERING);

		StringBuilder sql = new StringBuilder("Select");
		sql.append(" ServiceSeqId, FinEvent, FinReference, FromDate, ToDate, PftDaysBasis, SchdMethod");
		sql.append(", ActualRate, BaseRate, SplRate, Margin, GrcPeriodEndDate, NextGrcRepayDate, RepayPftFrq");
		sql.append(", RepayRvwFrq, RepayCpzFrq, GrcPftFrq, GrcRvwFrq, GrcCpzFrq, RepayFrq, NextRepayDate");
		sql.append(", Amount, RecalType, RecalFromDate, RecalToDate, PftIntact, Terms, ServiceReqNo");
		sql.append(", Remarks, PftChg, InstructionUID, LinkedTranID");
		sql.append(" from FinServiceInstruction");
		sql.append(StringUtils.trimToEmpty(type));
		sql.append(" Where FinReference = ? and FinEvent = ?");

		logger.trace(Literal.SQL + sql.toString());

		try {
			return this.jdbcOperations.query(sql.toString(), new PreparedStatementSetter() {

				@Override
				public void setValues(PreparedStatement ps) throws SQLException {
					int index = 1;
					ps.setString(index++, finReference);
					ps.setString(index++, finEvent);
				}
			}, new RowMapper<FinServiceInstruction>() {
				@Override
				public FinServiceInstruction mapRow(ResultSet rs, int rowNum) throws SQLException {
					FinServiceInstruction fsi = new FinServiceInstruction();

					fsi.setServiceSeqId(rs.getLong("ServiceSeqId"));
					fsi.setFinEvent(rs.getString("FinEvent"));
					fsi.setFinReference(rs.getString("FinReference"));
					fsi.setFromDate(rs.getTimestamp("FromDate"));
					fsi.setToDate(rs.getTimestamp("ToDate"));
					fsi.setPftDaysBasis(rs.getString("PftDaysBasis"));
					fsi.setSchdMethod(rs.getString("SchdMethod"));
					fsi.setActualRate(rs.getBigDecimal("ActualRate"));
					fsi.setBaseRate(rs.getString("BaseRate"));
					fsi.setSplRate(rs.getString("SplRate"));
					fsi.setMargin(rs.getBigDecimal("Margin"));
					fsi.setGrcPeriodEndDate(rs.getTimestamp("GrcPeriodEndDate"));
					fsi.setNextGrcRepayDate(rs.getTimestamp("NextGrcRepayDate"));
					fsi.setRepayPftFrq(rs.getString("RepayPftFrq"));
					fsi.setRepayRvwFrq(rs.getString("RepayRvwFrq"));
					fsi.setRepayCpzFrq(rs.getString("RepayCpzFrq"));
					fsi.setGrcPftFrq(rs.getString("GrcPftFrq"));
					fsi.setGrcRvwFrq(rs.getString("GrcRvwFrq"));
					fsi.setGrcCpzFrq(rs.getString("GrcCpzFrq"));
					fsi.setRepayFrq(rs.getString("RepayFrq"));
					fsi.setNextRepayDate(rs.getTimestamp("NextRepayDate"));
					fsi.setAmount(rs.getBigDecimal("Amount"));
					fsi.setRecalType(rs.getString("RecalType"));
					fsi.setRecalFromDate(rs.getTimestamp("RecalFromDate"));
					fsi.setRecalToDate(rs.getTimestamp("RecalToDate"));
					fsi.setPftIntact(rs.getBoolean("PftIntact"));
					fsi.setTerms(rs.getInt("Terms"));
					fsi.setServiceReqNo(rs.getString("ServiceReqNo"));
					fsi.setRemarks(rs.getString("Remarks"));
					fsi.setPftChg(rs.getBigDecimal("PftChg"));
					fsi.setInstructionUID(rs.getLong("InstructionUID"));
					fsi.setLinkedTranID(rs.getLong("LinkedTranID"));

					return fsi;
				}
			});
		} catch (EmptyResultDataAccessException e) {
			logger.error(Literal.EXCEPTION, e);
		}

		logger.debug(Literal.LEAVING);
		return new ArrayList<>();
	}

	@Override
	public List<FinServiceInstruction> getDMFinServiceInstructions(final String finReference, String type) {
		FinServiceInstruction finServiceInstruction = new FinServiceInstruction();
		finServiceInstruction.setFinReference(finReference);

		StringBuilder selectSql = new StringBuilder("Select ServiceSeqId, FinEvent, FinReference, FromDate, ToDate, ");
		selectSql.append("PftDaysBasis, SchdMethod, ActualRate, ");
		selectSql
				.append("RepayFrq, NextRepayDate, Amount, RecalType, RecalFromDate, RecalToDate, Terms ,ServiceReqNo ");
		selectSql.append(" From FinServiceInstruction");
		selectSql.append(StringUtils.trimToEmpty(type));
		selectSql.append(" Where FinReference =:FinReference order by ServiceSeqId");

		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(finServiceInstruction);
		RowMapper<FinServiceInstruction> typeRowMapper = BeanPropertyRowMapper.newInstance(FinServiceInstruction.class);

		return this.jdbcTemplate.query(selectSql.toString(), beanParameters, typeRowMapper);
	}

	/**
	 * Fetch FinServiceInstruction Detail by finReference,fromDate,finEvent
	 * 
	 * @param finReference
	 * 
	 * @param fromDate
	 * 
	 * @param finEvent
	 * 
	 * @return List<FinServiceInstruction>
	 */
	@Override
	public List<FinServiceInstruction> getFinServiceInstAddDisbDetail(final String finReference, Date fromDate,
			String finEvent) {
		logger.debug("Entering");

		FinServiceInstruction finServiceInstruction = new FinServiceInstruction();
		finServiceInstruction.setFinReference(finReference);
		finServiceInstruction.setFinEvent(finEvent);
		finServiceInstruction.setFromDate(fromDate);

		StringBuilder selectSql = new StringBuilder("Select ServiceSeqId, FinEvent, FinReference, FromDate,ToDate");
		selectSql.append(
				",PftDaysBasis,SchdMethod, ActualRate, BaseRate, SplRate, Margin, GrcPeriodEndDate,NextGrcRepayDate");
		selectSql.append(",RepayPftFrq, RepayRvwFrq, RepayCpzFrq, GrcPftFrq, GrcRvwFrq, GrcCpzFrq");
		selectSql.append(
				",RepayFrq, NextRepayDate, Amount, RecalType, RecalFromDate, RecalToDate, PftIntact, Terms ,ServiceReqNo,Remarks, PftChg, InstructionUID, LinkedTranID ");
		selectSql.append(" From FinServiceInstruction");
		selectSql.append(" Where FinReference =:FinReference AND FromDate =:FromDate AND FinEvent =:FinEvent ");

		logger.debug("selectSql: " + selectSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(finServiceInstruction);
		RowMapper<FinServiceInstruction> typeRowMapper = BeanPropertyRowMapper.newInstance(FinServiceInstruction.class);

		logger.debug("Leaving");
		return this.jdbcTemplate.query(selectSql.toString(), beanParameters, typeRowMapper);
	}

	/**
	 * Fetch List of FinServiceInstruction Detail by finEvent and serviceReqNo
	 * 
	 * @param finEvent
	 * 
	 * @param serviceReqNo
	 * 
	 * @return List<FinServiceInstruction>
	 */

	@Override
	public boolean getFinServInstDetails(String finEvent, String serviceReqNo) {
		logger.debug("Entering");
		int count = 0;
		boolean flag = false;
		FinServiceInstruction finServiceInstruction = new FinServiceInstruction();
		finServiceInstruction.setFinEvent(finEvent);
		finServiceInstruction.setServiceReqNo(serviceReqNo);

		StringBuilder selectSql = new StringBuilder("Select count(*)");
		selectSql.append(" From FinServiceInstruction");
		selectSql.append(" Where FinEvent =:FinEvent and ServiceReqNo=:ServiceReqNo");

		logger.debug("selectSql: " + selectSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(finServiceInstruction);
		count = this.jdbcTemplate.queryForObject(selectSql.toString(), beanParameters, Integer.class);

		if (count > 0) {
			flag = true;
		}
		logger.debug("Leaving");
		return flag;
	}

	/**
	 * Fetch the Record Repay Instruction Detail details by key field
	 * 
	 * @param id
	 *            (String)
	 * @param type
	 *            (String) ""/_Temp/_View
	 * @return RepayInstruction
	 */
	@Override
	public List<FinServiceInstruction> getFinServInstByServiceReqNo(final String finReference, Date fromDate,
			String serviceReqNo, String finEvent) {
		logger.debug("Entering");

		FinServiceInstruction finServiceInstruction = new FinServiceInstruction();
		finServiceInstruction.setFinReference(finReference);
		finServiceInstruction.setServiceReqNo(serviceReqNo);
		finServiceInstruction.setFinEvent(finEvent);
		finServiceInstruction.setFromDate(fromDate);

		StringBuilder selectSql = new StringBuilder("Select ServiceSeqId, FinEvent, FinReference, FromDate,ToDate");
		selectSql.append(
				",PftDaysBasis,SchdMethod, ActualRate, BaseRate, SplRate, Margin, GrcPeriodEndDate,NextGrcRepayDate");
		selectSql.append(",RepayPftFrq, RepayRvwFrq, RepayCpzFrq, GrcPftFrq, GrcRvwFrq, GrcCpzFrq");
		selectSql.append(
				",RepayFrq, NextRepayDate, Amount, RecalType, RecalFromDate, RecalToDate, PftIntact, Terms ,ServiceReqNo,Remarks, PftChg, InstructionUID, LinkedTranID ");
		selectSql.append(" From FinServiceInstruction Where FinReference =:FinReference AND");
		selectSql.append(" FromDate=:FromDate AND FinEvent =:FinEvent AND ServiceReqNo =:ServiceReqNo ");

		logger.debug("selectSql: " + selectSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(finServiceInstruction);
		RowMapper<FinServiceInstruction> typeRowMapper = BeanPropertyRowMapper.newInstance(FinServiceInstruction.class);

		logger.debug("Leaving");
		return this.jdbcTemplate.query(selectSql.toString(), beanParameters, typeRowMapper);
	}

	@Override
	public List<FinServiceInstruction> getFinServiceInstDetailsByServiceReqNo(String finReference,
			String serviceReqNo) {
		logger.debug("Entering");
		FinServiceInstruction finServiceInstruction = new FinServiceInstruction();
		finServiceInstruction.setFinReference(finReference);
		finServiceInstruction.setServiceReqNo(serviceReqNo);

		StringBuilder selectSql = new StringBuilder("Select ServiceSeqId, FinEvent, FinReference, FromDate,ToDate");
		selectSql.append(
				",PftDaysBasis,SchdMethod, ActualRate, BaseRate, SplRate, Margin, GrcPeriodEndDate,NextGrcRepayDate");
		selectSql.append(",RepayPftFrq, RepayRvwFrq, RepayCpzFrq, GrcPftFrq, GrcRvwFrq, GrcCpzFrq");
		selectSql.append(
				",RepayFrq, NextRepayDate, Amount, RecalType, RecalFromDate, RecalToDate, PftIntact, Terms ,ServiceReqNo,Remarks, PftChg, InstructionUID, LinkedTranID ");
		selectSql.append(
				" From FinServiceInstruction Where FinReference =:FinReference AND ServiceReqNo =:ServiceReqNo ");

		logger.debug("selectSql: " + selectSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(finServiceInstruction);
		RowMapper<FinServiceInstruction> typeRowMapper = BeanPropertyRowMapper.newInstance(FinServiceInstruction.class);

		logger.debug("Leaving");
		return this.jdbcTemplate.query(selectSql.toString(), beanParameters, typeRowMapper);
	}

	@Override
	public List<LMSServiceLog> getLMSServiceLogList(String notificationFlag) {
		logger.debug(Literal.ENTERING);

		MapSqlParameterSource source = new MapSqlParameterSource();

		StringBuilder sql = new StringBuilder("Select Id,Event, FinReference, OldRate, NewRate, EffectiveDate,");
		sql.append(" NotificationFlag From LMSServiceLog Where  NotificationFlag = :NotificationFlag ");
		logger.debug(Literal.SQL + sql.toString());

		source.addValue("NotificationFlag", notificationFlag);

		RowMapper<LMSServiceLog> typeRowMapper = BeanPropertyRowMapper.newInstance(LMSServiceLog.class);

		logger.debug(Literal.LEAVING);
		return this.jdbcTemplate.query(sql.toString(), source, typeRowMapper);
	}

	@Override
	public void updateNotificationFlag(String notificationFlag, long id) {
		logger.debug(Literal.ENTERING);

		MapSqlParameterSource source = new MapSqlParameterSource();

		StringBuilder updateSql = new StringBuilder("update LMSServiceLog set NotificationFlag = :NotificationFlag");
		updateSql.append(" Where id =:id");
		logger.debug(Literal.SQL + updateSql.toString());

		source.addValue("NotificationFlag", notificationFlag);
		source.addValue("id", id);

		try {
			this.jdbcTemplate.update(updateSql.toString(), source);
		} catch (Exception e) {
			logger.error(Literal.EXCEPTION, e);
			throw e;
		}
		logger.debug(Literal.LEAVING);
	}

	@Override
	public BigDecimal getOldRate(String finReference, Date schdate) {
		StringBuilder sql = new StringBuilder("Select");
		sql.append(" CalculatedRate");
		sql.append(" From FinScheduleDetails");
		sql.append(" Where Finreference = ?");
		sql.append(" and Schdate = (Select max(schdate) From FinScheduleDetails");
		sql.append(" Where Finreference = ? and Schdate <= ?) ");

		logger.trace(Literal.SQL + sql.toString());

		try {
			Object[] object = new Object[] { finReference, finReference, schdate };
			return this.jdbcOperations.queryForObject(sql.toString(), object, BigDecimal.class);
		} catch (EmptyResultDataAccessException e) {
			logger.warn(
					"Rate not found in FinScheduleDetails table for the specified FinReference >> {} and Schdate <= {}",
					finReference, schdate);
		}

		return BigDecimal.ZERO;
	}

	@Override
	public BigDecimal getNewRate(String finReference, Date schdate) {
		logger.debug(Literal.ENTERING);

		StringBuilder sql = new StringBuilder();
		sql.append("select calculatedrate from finscheduledetails_Temp where Finreference = :FinReference ");
		sql.append("and Schdate = (select max(schdate) from finscheduledetails_Temp ");
		sql.append("where Finreference = :FinReference and Schdate <= :Schdate) ");

		logger.trace(Literal.SQL + sql.toString());

		MapSqlParameterSource paramSource = new MapSqlParameterSource();
		paramSource.addValue("FinReference", finReference);
		paramSource.addValue("Schdate", schdate);

		BigDecimal result = BigDecimal.ZERO;
		try {
			result = this.jdbcTemplate.queryForObject(sql.toString(), paramSource, BigDecimal.class);
		} catch (EmptyResultDataAccessException e) {
		}
		logger.debug(Literal.LEAVING);
		return result;
	}

	@Override
	public void saveLMSServiceLOGList(List<LMSServiceLog> slList) {
		StringBuilder sql = new StringBuilder("Insert into");
		sql.append(" LMSServiceLog");
		sql.append("(Event, FinReference, OldRate, NewRate, EffectiveDate, NotificationFlag");
		sql.append(") values(");
		sql.append("?, ?, ?, ?, ?, ?");
		sql.append(")");

		jdbcOperations.batchUpdate(sql.toString(), new BatchPreparedStatementSetter() {

			@Override
			public void setValues(PreparedStatement ps, int i) throws SQLException {
				LMSServiceLog sl = slList.get(i);

				int index = 1;

				ps.setString(index++, sl.getEvent());
				ps.setString(index++, sl.getFinReference());
				ps.setBigDecimal(index++, sl.getOldRate());
				ps.setBigDecimal(index++, sl.getNewRate());
				ps.setDate(index++, JdbcUtil.getDate(sl.getEffectiveDate()));
				ps.setString(index++, sl.getNotificationFlag());
			}

			@Override
			public int getBatchSize() {
				return slList.size();
			}
		});

	}

	@Override
	public List<String> getFinEventByFinRef(String finReference, String type) {

		MapSqlParameterSource paramSource = new MapSqlParameterSource();
		paramSource.addValue("FinReference", finReference);

		StringBuilder sql = new StringBuilder("Select FinEvent");
		sql.append(" From FinServiceInstruction");
		sql.append(StringUtils.trimToEmpty(type));
		sql.append(" Where FinReference =:FinReference ");

		logger.trace(Literal.SQL + sql.toString());
		try {
			return this.jdbcTemplate.queryForList(sql.toString(), paramSource, String.class);
		} catch (Exception e) {
			//
		}

		return new ArrayList<>();

	}

	@Override
	public List<FinServiceInstruction> getOrgFinServiceInstructions(String finReference, String type) {
		logger.debug(Literal.ENTERING);

		StringBuilder sql = new StringBuilder("Select");
		sql.append(" ServiceSeqId, FinEvent, FinReference, FromDate, ToDate, PftDaysBasis, SchdMethod");
		sql.append(", ActualRate, BaseRate, SplRate, Margin, GrcPeriodEndDate, NextGrcRepayDate, RepayPftFrq");
		sql.append(", RepayRvwFrq, RepayCpzFrq, GrcPftFrq, GrcRvwFrq, GrcCpzFrq, RepayFrq, NextRepayDate");
		sql.append(", Amount, RecalType, RecalFromDate, RecalToDate, PftIntact, Terms, ServiceReqNo");
		sql.append(", Remarks, PftChg, InstructionUID, LinkedTranID");
		sql.append(" from FinServiceInstruction");
		sql.append(StringUtils.trimToEmpty(type));
		sql.append(" Where FinReference = ? ");

		logger.trace(Literal.SQL + sql.toString());

		try {
			return this.jdbcOperations.query(sql.toString(), new PreparedStatementSetter() {

				@Override
				public void setValues(PreparedStatement ps) throws SQLException {
					int index = 1;
					ps.setString(index++, finReference);
				}
			}, new RowMapper<FinServiceInstruction>() {
				@Override
				public FinServiceInstruction mapRow(ResultSet rs, int rowNum) throws SQLException {
					FinServiceInstruction fsi = new FinServiceInstruction();

					fsi.setServiceSeqId(rs.getLong("ServiceSeqId"));
					fsi.setFinEvent(rs.getString("FinEvent"));
					fsi.setFinReference(rs.getString("FinReference"));
					fsi.setFromDate(rs.getTimestamp("FromDate"));
					fsi.setToDate(rs.getTimestamp("ToDate"));
					fsi.setPftDaysBasis(rs.getString("PftDaysBasis"));
					fsi.setSchdMethod(rs.getString("SchdMethod"));
					fsi.setActualRate(rs.getBigDecimal("ActualRate"));
					fsi.setBaseRate(rs.getString("BaseRate"));
					fsi.setSplRate(rs.getString("SplRate"));
					fsi.setMargin(rs.getBigDecimal("Margin"));
					fsi.setGrcPeriodEndDate(rs.getTimestamp("GrcPeriodEndDate"));
					fsi.setNextGrcRepayDate(rs.getTimestamp("NextGrcRepayDate"));
					fsi.setRepayPftFrq(rs.getString("RepayPftFrq"));
					fsi.setRepayRvwFrq(rs.getString("RepayRvwFrq"));
					fsi.setRepayCpzFrq(rs.getString("RepayCpzFrq"));
					fsi.setGrcPftFrq(rs.getString("GrcPftFrq"));
					fsi.setGrcRvwFrq(rs.getString("GrcRvwFrq"));
					fsi.setGrcCpzFrq(rs.getString("GrcCpzFrq"));
					fsi.setRepayFrq(rs.getString("RepayFrq"));
					fsi.setNextRepayDate(rs.getTimestamp("NextRepayDate"));
					fsi.setAmount(rs.getBigDecimal("Amount"));
					fsi.setRecalType(rs.getString("RecalType"));
					fsi.setRecalFromDate(rs.getTimestamp("RecalFromDate"));
					fsi.setRecalToDate(rs.getTimestamp("RecalToDate"));
					fsi.setPftIntact(rs.getBoolean("PftIntact"));
					fsi.setTerms(rs.getInt("Terms"));
					fsi.setServiceReqNo(rs.getString("ServiceReqNo"));
					fsi.setRemarks(rs.getString("Remarks"));
					fsi.setPftChg(rs.getBigDecimal("PftChg"));
					fsi.setInstructionUID(rs.getLong("InstructionUID"));
					fsi.setLinkedTranID(rs.getLong("LinkedTranID"));

					return fsi;
				}
			});
		} catch (EmptyResultDataAccessException e) {
			logger.error(Literal.EXCEPTION, e);
		}

		logger.debug(Literal.LEAVING);
		return new ArrayList<>();
	}

	@Override
	public boolean isFinServiceInstExists(String finReference, String table) {
		StringBuilder sql = new StringBuilder(" Select Count(1) ");
		sql.append(" from FinServiceInstruction");
		sql.append(table);
		sql.append(" where Finreference = ?");

		try {
			Object[] object = new Object[] { finReference };
			return this.jdbcOperations.queryForObject(sql.toString(), object, Integer.class) == 0 ? false : true;
		} catch (EmptyResultDataAccessException e) {
			logger.warn("Record not found in FinServiceInstruction {} table for the specified FinReference >> {}",
					table, finReference);
		}
		return false;
	}

}