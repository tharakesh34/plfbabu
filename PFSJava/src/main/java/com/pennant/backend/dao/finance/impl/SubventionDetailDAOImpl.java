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
 * * FileName : SubventionDetailDAOImpl.java * * Author : PENNANT TECHONOLOGIES * * Creation Date : 12-09-2018 * *
 * Modified Date : 12-09-2018 * * Description : * *
 ********************************************************************************************
 * Date Author Version Comments *
 ********************************************************************************************
 * 12-09-2018 PENNANT 0.1 * * * * * * * * *
 ********************************************************************************************
 */
package com.pennant.backend.dao.finance.impl;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;

import com.pennant.backend.dao.finance.SubventionDetailDAO;
import com.pennant.backend.model.finance.SubventionDetail;
import com.pennant.backend.model.finance.SubventionScheduleDetail;
import com.pennanttech.pennapps.core.ConcurrencyException;
import com.pennanttech.pennapps.core.jdbc.BasicDao;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pff.core.TableType;

/**
 * Data access layer implementation for <code>SubventionDetail</code> with set of CRUD operations.
 */
public class SubventionDetailDAOImpl extends BasicDao<SubventionDetail> implements SubventionDetailDAO {
	private static Logger logger = LogManager.getLogger(SubventionDetailDAOImpl.class);

	public SubventionDetailDAOImpl() {
		super();
	}

	@Override
	public SubventionDetail getSubventionDetail(long finID, String type) {
		StringBuilder sql = new StringBuilder("Select");
		sql.append(" FinReference, Method, Type, Rate, PeriodRate, DiscountRate");
		sql.append(", Tenure, StartDate, EndDate");
		sql.append(", Version, LastMntOn, LastMntBy, RecordStatus");
		sql.append(", RoleCode, NextRoleCode, TaskId, NextTaskId, RecordType, WorkflowId");
		sql.append(" From SubventionDetails");
		sql.append(StringUtils.trim(type));
		sql.append(" Where FinID = ?");

		logger.trace(Literal.SQL + sql.toString());

		try {
			return jdbcOperations.queryForObject(sql.toString(), new RowMapper<SubventionDetail>() {
				@Override
				public SubventionDetail mapRow(ResultSet rs, int rowNum) throws SQLException {
					SubventionDetail sd = new SubventionDetail();

					sd.setFinReference(rs.getString("FinReference"));
					sd.setMethod(rs.getString("Method"));
					sd.setType(rs.getString("Type"));
					sd.setRate(rs.getBigDecimal("Rate"));
					sd.setPeriodRate(rs.getBigDecimal("PeriodRate"));
					sd.setDiscountRate(rs.getBigDecimal("DiscountRate"));
					sd.setTenure(rs.getInt("Tenure"));
					sd.setStartDate(rs.getTimestamp("StartDate"));
					sd.setEndDate(rs.getTimestamp("EndDate"));
					sd.setVersion(rs.getInt("Version"));
					sd.setLastMntOn(rs.getTimestamp("LastMntOn"));
					sd.setLastMntBy(rs.getLong("LastMntBy"));
					sd.setRecordStatus(rs.getString("RecordStatus"));
					sd.setRoleCode(rs.getString("RoleCode"));
					sd.setNextRoleCode(rs.getString("NextRoleCode"));
					sd.setTaskId(rs.getString("TaskId"));
					sd.setNextTaskId(rs.getString("NextTaskId"));
					sd.setRecordType(rs.getString("RecordType"));
					sd.setWorkflowId(rs.getLong("WorkflowId"));

					return sd;
				}
			}, finID);

		} catch (EmptyResultDataAccessException e) {
			logger.error(Literal.EXCEPTION, e);
		}

		return null;
	}

	@Override
	public String save(SubventionDetail subventionDetail, TableType tableType) {
		logger.debug(Literal.ENTERING);

		// Prepare the SQL.
		StringBuilder sql = new StringBuilder(" insert into SubventionDetails");
		sql.append(tableType.getSuffix());
		sql.append("(finReference, method, type, rate, periodRate, discountRate, ");
		sql.append(" tenure, startDate, endDate, ");
		sql.append(
				" Version , LastMntBy, LastMntOn, RecordStatus, RoleCode, NextRoleCode, TaskId, NextTaskId, RecordType, WorkflowId,SubVentionAmt)");
		sql.append(" values(");
		sql.append(" :finReference, :method, :type, :rate, :periodRate, :discountRate, ");
		sql.append(" :tenure, :startDate, :endDate, ");
		sql.append(
				" :Version , :LastMntBy, :LastMntOn, :RecordStatus, :RoleCode, :NextRoleCode, :TaskId, :NextTaskId, :RecordType, :WorkflowId,:SubVentionAmt)");

		// Execute the SQL, binding the arguments.
		logger.trace(Literal.SQL + sql.toString());
		SqlParameterSource paramSource = new BeanPropertySqlParameterSource(subventionDetail);

		try {
			jdbcTemplate.update(sql.toString(), paramSource);
		} catch (DuplicateKeyException e) {
			throw new ConcurrencyException(e);
		}

		logger.debug(Literal.LEAVING);
		return String.valueOf(subventionDetail.getFinReference());
	}

	@Override
	public void update(SubventionDetail subventionDetail, TableType tableType) {
		logger.debug(Literal.ENTERING);

		// Prepare the SQL.
		StringBuilder sql = new StringBuilder("update SubventionDetails");
		sql.append(tableType.getSuffix());
		sql.append("  set method = :method, type = :type, rate = :rate, ");
		sql.append(" periodRate = :periodRate, discountRate = :discountRate, tenure = :tenure, ");
		sql.append(" startDate = :startDate, endDate = :endDate, ");
		sql.append(" LastMntOn = :LastMntOn, RecordStatus = :RecordStatus, RoleCode = :RoleCode,");
		sql.append(" NextRoleCode = :NextRoleCode, TaskId = :TaskId, NextTaskId = :NextTaskId,");
		sql.append(" RecordType = :RecordType, WorkflowId = :WorkflowId,SubVentionAmt=:SubVentionAmt");
		sql.append(" where finReference = :finReference ");

		// Execute the SQL, binding the arguments.
		logger.trace(Literal.SQL + sql.toString());

		SqlParameterSource paramSource = new BeanPropertySqlParameterSource(subventionDetail);
		int recordCount = jdbcTemplate.update(sql.toString(), paramSource);

		// Check for the concurrency failure.
		if (recordCount == 0) {
			throw new ConcurrencyException();
		}
		logger.debug(Literal.LEAVING);
	}

	@Override
	public void delete(SubventionDetail subventionDetail, TableType tableType) {
		logger.debug(Literal.ENTERING);

		// Prepare the SQL.
		StringBuilder sql = new StringBuilder("delete from SubventionDetails");
		sql.append(tableType.getSuffix());
		sql.append(" where finReference = :finReference ");

		// Execute the SQL, binding the arguments.
		logger.trace(Literal.SQL + sql.toString());
		SqlParameterSource paramSource = new BeanPropertySqlParameterSource(subventionDetail);

		try {
			jdbcTemplate.update(sql.toString(), paramSource);
		} catch (DataAccessException e) {
			logger.debug(Literal.EXCEPTION, e);
		}
		logger.debug(Literal.LEAVING);
	}

	@Override
	public long save(SubventionScheduleDetail subVenscheduleDetail, String type) {

		StringBuilder sql = new StringBuilder(" insert into SubventionScheduleDetail");
		sql.append(type);
		sql.append(
				"(FinReference, DisbSeqID, SchDate, NoOfDays, DiscountedPft, PresentValue, FutureValue, ClosingBal)");
		sql.append(" values(");
		sql.append(
				":FinReference, :DisbSeqID, :SchDate, :NoOfDays, :DiscountedPft, :PresentValue, :FutureValue, :ClosingBal)");

		logger.debug("insertSql: " + sql.toString());

		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(subVenscheduleDetail);
		this.jdbcTemplate.update(sql.toString(), beanParameters);

		return subVenscheduleDetail.getDisbSeqID();
	}

	@Override
	public void deleteByFinReference(String finReference, String type) {
		SubventionScheduleDetail subventionScheduleDetail = new SubventionScheduleDetail();
		subventionScheduleDetail.setFinReference(finReference);

		StringBuilder deleteSql = new StringBuilder("Delete From SubventionScheduleDetail");
		deleteSql.append(StringUtils.trimToEmpty(type));
		deleteSql.append(" Where FinReference =:FinReference");

		logger.debug("deleteSql: " + deleteSql.toString());

		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(subventionScheduleDetail);

		this.jdbcTemplate.update(deleteSql.toString(), beanParameters);
	}

	@Override
	public SubventionScheduleDetail getSubvenScheduleDetail(SubventionScheduleDetail subVenschedule, String type) {

		// Prepare the SQL.
		StringBuilder sql = new StringBuilder("SELECT ");
		sql.append(" FinReference, DisbSeqID, SchDate, NoOfDays, DiscountedPft, PresentValue, FutureValue, ClosingBal");
		sql.append(" From SubventionScheduleDetail");
		sql.append(type);
		sql.append(" Where finReference = :finReference And DisbSeqID = :DisbSeqID And SchDate = :SchDate");

		// Execute the SQL, binding the arguments.
		logger.trace(Literal.SQL + sql.toString());

		SqlParameterSource paramSource = new BeanPropertySqlParameterSource(subVenschedule);
		RowMapper<SubventionScheduleDetail> rowMapper = BeanPropertyRowMapper
				.newInstance(SubventionScheduleDetail.class);

		try {
			subVenschedule = jdbcTemplate.queryForObject(sql.toString(), paramSource, rowMapper);
		} catch (EmptyResultDataAccessException e) {
			logger.error(Literal.EXCEPTION, e);
			subVenschedule = null;
		}

		return subVenschedule;
	}

	@Override
	public List<SubventionScheduleDetail> getSubventionScheduleDetails(final String finReference, long disbSeqID,
			String type) {
		SubventionScheduleDetail subventionScheduleDetail = new SubventionScheduleDetail();
		subventionScheduleDetail.setFinReference(finReference);
		subventionScheduleDetail.setDisbSeqID(disbSeqID);

		StringBuilder selectSql = new StringBuilder(
				"Select FinReference, DisbSeqID, SchDate, NoOfDays, DiscountedPft, PresentValue, FutureValue, ClosingBal");

		if (StringUtils.trimToEmpty(type).contains("View")) {
			selectSql.append("");
		}
		selectSql.append(" From SubventionScheduleDetail");
		selectSql.append(StringUtils.trimToEmpty(type));
		selectSql.append(" Where FinReference = :FinReference ");
		if (disbSeqID != 0) {
			selectSql.append(" And DisbSeqID = :DisbSeqID ");
		}
		selectSql.append(" order by SchDate asc");

		logger.debug("selectSql: " + selectSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(subventionScheduleDetail);
		RowMapper<SubventionScheduleDetail> typeRowMapper = BeanPropertyRowMapper
				.newInstance(SubventionScheduleDetail.class);

		return this.jdbcTemplate.query(selectSql.toString(), beanParameters, typeRowMapper);
	}

	public void updateSubVebtionAmt(String finrefence, BigDecimal totalSubVentionAmt) {
		logger.debug(Literal.ENTERING);

		// Prepare the SQL.
		StringBuilder sql = new StringBuilder("update SubventionDetails");
		sql.append("  set SubVentionAmt=:SubVentionAmt");
		sql.append(" where finReference = :finReference ");

		// Execute the SQL, binding the arguments.
		logger.trace(Literal.SQL + sql.toString());

		SubventionDetail subVentionDetail = new SubventionDetail();
		subVentionDetail.setFinReference(finrefence);
		subVentionDetail.setSubVentionAmt(totalSubVentionAmt);

		SqlParameterSource paramSource = new BeanPropertySqlParameterSource(subVentionDetail);

		try {
			jdbcTemplate.update(sql.toString(), paramSource);
		} catch (DataAccessException e) {
			logger.debug("Expection:" + e);
		}

		logger.debug(Literal.LEAVING);
	}

	/***
	 * Method to get the Total SubVention Amount
	 */
	@Override
	public BigDecimal getTotalSubVentionAmt(String finReference) {
		logger.debug("Entering");

		BigDecimal subVentionAmt = BigDecimal.ZERO;
		MapSqlParameterSource source = new MapSqlParameterSource();
		source.addValue("FinReference", finReference);

		StringBuilder selectSql = new StringBuilder(" SELECT SubVentionAmt From SubventionDetails");
		selectSql.append(" Where FinReference = :FinReference");
		logger.debug("selectSql: " + selectSql.toString());

		try {
			subVentionAmt = this.jdbcTemplate.queryForObject(selectSql.toString(), source, BigDecimal.class);
		} catch (EmptyResultDataAccessException e) {
			subVentionAmt = BigDecimal.ZERO;
		}
		return subVentionAmt;
	}
}
