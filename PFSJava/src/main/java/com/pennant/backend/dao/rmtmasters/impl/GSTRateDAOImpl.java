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
 * * FileName : GSTRateDAOImpl.java * * Author : PENNANT TECHONOLOGIES * * Creation Date : 20-05-2019 * * Modified Date
 * : 20-05-2019 * * Description : * *
 ********************************************************************************************
 * Date Author Version Comments *
 ********************************************************************************************
 * 20-05-2019 PENNANT 0.1 * * * * * * * * *
 ********************************************************************************************
 */
package com.pennant.backend.dao.rmtmasters.impl;

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

import com.pennant.backend.dao.rmtmasters.GSTRateDAO;
import com.pennant.backend.model.rmtmasters.GSTRate;
import com.pennanttech.pennapps.core.ConcurrencyException;
import com.pennanttech.pennapps.core.DependencyFoundException;
import com.pennanttech.pennapps.core.jdbc.SequenceDao;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pennapps.core.resource.Message;
import com.pennanttech.pff.core.TableType;
import com.pennanttech.pff.core.util.QueryUtil;

/**
 * Data access layer implementation for <code>GSTRate</code> with set of CRUD operations.
 */
public class GSTRateDAOImpl extends SequenceDao<GSTRate> implements GSTRateDAO {
	private static Logger logger = LogManager.getLogger(GSTRateDAOImpl.class);

	public GSTRateDAOImpl() {
		super();
	}

	@Override
	public GSTRate getGSTRate(long id, String type) {
		logger.debug(Literal.ENTERING);

		// Prepare the SQL.
		StringBuilder sql = new StringBuilder("SELECT ");
		sql.append(" id, fromState, toState, taxType, calcType, amount, ");
		sql.append(" percentage, calcOn, active, ");
		if (StringUtils.trimToEmpty(type).contains("View")) {
			sql.append("   fromStateName , toStateName ,");
		}
		sql.append(" Version, LastMntOn, LastMntBy,RecordStatus, RoleCode, NextRoleCode, TaskId, NextTaskId,");
		sql.append(" RecordType, WorkflowId From GST_RATES");
		sql.append(type);
		sql.append(" Where id = :id");

		// Execute the SQL, binding the arguments.
		logger.trace(Literal.SQL + sql.toString());

		GSTRate gSTRate = new GSTRate();
		gSTRate.setId(id);

		SqlParameterSource paramSource = new BeanPropertySqlParameterSource(gSTRate);
		RowMapper<GSTRate> rowMapper = BeanPropertyRowMapper.newInstance(GSTRate.class);

		try {
			return jdbcTemplate.queryForObject(sql.toString(), paramSource, rowMapper);
		} catch (EmptyResultDataAccessException e) {
			logger.warn(Message.NO_RECORD_FOUND);
			return null;
		}
	}

	@Override
	public boolean isDuplicateKey(long id, String fromState, String toState, String taxType, TableType tableType) {
		logger.debug(Literal.ENTERING);

		// Prepare the SQL.
		String sql;
		String whereClause = "fromState = :fromState AND toState = :toState AND taxType = :taxType AND id != :id";

		switch (tableType) {
		case MAIN_TAB:
			sql = QueryUtil.getCountQuery("GST_RATES", whereClause);
			break;
		case TEMP_TAB:
			sql = QueryUtil.getCountQuery("GST_RATES_Temp", whereClause);
			break;
		default:
			sql = QueryUtil.getCountQuery(new String[] { "GST_RATES_Temp", "GST_RATES" }, whereClause);
			break;
		}

		// Execute the SQL, binding the arguments.
		logger.trace(Literal.SQL + sql);
		MapSqlParameterSource paramSource = new MapSqlParameterSource();
		paramSource.addValue("id", id);
		paramSource.addValue("fromState", fromState);
		paramSource.addValue("toState", toState);
		paramSource.addValue("taxType", taxType);

		Integer count = jdbcTemplate.queryForObject(sql, paramSource, Integer.class);

		boolean exists = false;
		if (count > 0) {
			exists = true;
		}

		logger.debug(Literal.LEAVING);
		return exists;
	}

	@Override
	public String save(GSTRate gSTRate, TableType tableType) {
		logger.debug(Literal.ENTERING);

		// Prepare the SQL.
		StringBuilder sql = new StringBuilder(" insert into GST_RATES");
		sql.append(tableType.getSuffix());
		sql.append("(id, fromState, toState, taxType, calcType, amount, ");
		sql.append(" percentage, calcOn, active, ");
		sql.append(" Version , LastMntBy, LastMntOn, RecordStatus, RoleCode, NextRoleCode, TaskId, NextTaskId, ");
		sql.append(" RecordType, WorkflowId) values(");
		sql.append(" :id, :fromState, :toState, :taxType, :calcType, :amount, ");
		sql.append(" :percentage, :calcOn, :active, ");
		sql.append(" :Version , :LastMntBy, :LastMntOn, :RecordStatus, :RoleCode, :NextRoleCode, :TaskId,");
		sql.append(" :NextTaskId, :RecordType, :WorkflowId)");

		if (gSTRate.getId() == Long.MIN_VALUE) {
			gSTRate.setId(getNextValue("SeqGST_RATES"));
			logger.debug("get NextID:" + gSTRate.getId());
		}

		// Execute the SQL, binding the arguments.
		logger.trace(Literal.SQL + sql.toString());
		SqlParameterSource paramSource = new BeanPropertySqlParameterSource(gSTRate);

		try {
			jdbcTemplate.update(sql.toString(), paramSource);
		} catch (DuplicateKeyException e) {
			throw new ConcurrencyException(e);
		}

		logger.debug(Literal.LEAVING);
		return String.valueOf(gSTRate.getId());
	}

	@Override
	public void update(GSTRate gSTRate, TableType tableType) {
		logger.debug(Literal.ENTERING);

		// Prepare the SQL.
		StringBuilder sql = new StringBuilder("update GST_RATES");
		sql.append(tableType.getSuffix());
		sql.append(" set fromState = :fromState, toState = :toState, taxType = :taxType, ");
		sql.append(" calcType = :calcType, amount = :amount, percentage = :percentage, ");
		sql.append(" calcOn = :calcOn, active = :active, ");
		sql.append(" LastMntOn = :LastMntOn, RecordStatus = :RecordStatus, RoleCode = :RoleCode,");
		sql.append(" NextRoleCode = :NextRoleCode, TaskId = :TaskId, NextTaskId = :NextTaskId,");
		sql.append(" RecordType = :RecordType, WorkflowId = :WorkflowId");
		sql.append(" where id = :id ");
		sql.append(QueryUtil.getConcurrencyCondition(tableType));

		// Execute the SQL, binding the arguments.
		logger.trace(Literal.SQL + sql.toString());

		SqlParameterSource paramSource = new BeanPropertySqlParameterSource(gSTRate);
		int recordCount = jdbcTemplate.update(sql.toString(), paramSource);

		// Check for the concurrency failure.
		if (recordCount == 0) {
			throw new ConcurrencyException();
		}
		logger.debug(Literal.LEAVING);
	}

	@Override
	public void delete(GSTRate gSTRate, TableType tableType) {
		logger.debug(Literal.ENTERING);

		// Prepare the SQL.
		StringBuilder sql = new StringBuilder("delete from GST_RATES");
		sql.append(tableType.getSuffix());
		sql.append(" where id = :id ");
		sql.append(QueryUtil.getConcurrencyCondition(tableType));

		// Execute the SQL, binding the arguments.
		logger.trace(Literal.SQL + sql.toString());
		SqlParameterSource paramSource = new BeanPropertySqlParameterSource(gSTRate);
		int recordCount = 0;

		try {
			recordCount = jdbcTemplate.update(sql.toString(), paramSource);
		} catch (DataAccessException e) {
			throw new DependencyFoundException(e);
		}

		// Check for the concurrency failure.
		if (recordCount == 0) {
			throw new ConcurrencyException();
		}

		logger.debug(Literal.LEAVING);
	}

	@Override
	public boolean isGSTExist(String fromState, String toState, String calcOn) {
		logger.debug(Literal.ENTERING);

		// Prepare the SQL.
		String sql;
		String whereClause = " FromState = :FromState AND ToState = :ToState AND TaxType = :CalcOn";

		sql = QueryUtil.getCountQuery("GST_RATES_AVIEW", whereClause);

		// Execute the SQL, binding the arguments.
		logger.trace(Literal.SQL + sql);
		MapSqlParameterSource paramSource = new MapSqlParameterSource();
		paramSource.addValue("FromState", fromState);
		paramSource.addValue("ToState", toState);
		paramSource.addValue("CalcOn", calcOn);

		Integer count = jdbcTemplate.queryForObject(sql, paramSource, Integer.class);

		boolean exists = false;
		if (count > 0) {
			exists = true;
		}

		logger.debug(Literal.LEAVING);

		return exists;
	}

	@Override
	public List<GSTRate> getGSTRateByStates(String fromState, String toState, String tableType) {
		logger.debug(Literal.ENTERING);
		MapSqlParameterSource source = new MapSqlParameterSource();
		source.addValue("fromState", fromState);
		source.addValue("toState", toState);
		source.addValue("active", true);

		// Prepare the SQL.
		StringBuilder sql = new StringBuilder("SELECT ");
		sql.append(" id, fromState, toState, taxType, calcType, amount,");
		sql.append(" percentage, calcOn, active, ");
		if (StringUtils.trimToEmpty(tableType).contains("View")) {
			sql.append(" fromStateName , toStateName,");
		}
		sql.append(" Version, LastMntOn, LastMntBy,RecordStatus, RoleCode, NextRoleCode, TaskId, NextTaskId,");
		sql.append(" RecordType, WorkflowId From GST_RATES");
		sql.append(tableType);
		sql.append(" Where fromState = :fromState and toState = :toState and active = :active");

		logger.debug("selectSql : " + sql.toString());
		RowMapper<GSTRate> typeRowMapper = BeanPropertyRowMapper.newInstance(GSTRate.class);

		logger.debug(Literal.LEAVING);
		return this.jdbcTemplate.query(sql.toString(), source, typeRowMapper);
	}
}
