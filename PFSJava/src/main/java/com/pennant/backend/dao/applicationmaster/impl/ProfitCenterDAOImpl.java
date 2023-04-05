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
 * * FileName : ProfitCenterDAOImpl.java * * Author : PENNANT TECHONOLOGIES * * Creation Date : 22-04-2017 * * Modified
 * Date : 22-04-2017 * * Description : * *
 ********************************************************************************************
 * Date Author Version Comments *
 ********************************************************************************************
 * 22-04-2017 PENNANT 0.1 * * * * * * * * *
 ********************************************************************************************
 */
package com.pennant.backend.dao.applicationmaster.impl;

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

import com.pennant.backend.dao.applicationmaster.ProfitCenterDAO;
import com.pennant.backend.model.applicationmaster.ProfitCenter;
import com.pennanttech.pennapps.core.ConcurrencyException;
import com.pennanttech.pennapps.core.DependencyFoundException;
import com.pennanttech.pennapps.core.jdbc.SequenceDao;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pennapps.core.resource.Message;
import com.pennanttech.pff.core.TableType;
import com.pennanttech.pff.core.util.QueryUtil;

/**
 * Data access layer implementation for <code>ProfitCenter</code> with set of CRUD operations.
 */
public class ProfitCenterDAOImpl extends SequenceDao<ProfitCenter> implements ProfitCenterDAO {
	private static Logger logger = LogManager.getLogger(ProfitCenterDAOImpl.class);

	public ProfitCenterDAOImpl() {
		super();
	}

	@Override
	public ProfitCenter getProfitCenter(long profitCenterID, String type) {
		logger.debug(Literal.ENTERING);

		// Prepare the SQL.
		StringBuilder sql = new StringBuilder("SELECT ");
		sql.append(" profitCenterID, profitCenterCode, profitCenterDesc, active, ");

		sql.append(
				" Version, LastMntOn, LastMntBy,RecordStatus, RoleCode, NextRoleCode, TaskId, NextTaskId, RecordType, WorkflowId");
		sql.append(" From ProfitCenters");
		sql.append(type);
		sql.append(" Where profitCenterID = :profitCenterID");

		// Execute the SQL, binding the arguments.
		logger.trace(Literal.SQL + sql.toString());

		ProfitCenter profitCenter = new ProfitCenter();
		profitCenter.setProfitCenterID(profitCenterID);

		SqlParameterSource paramSource = new BeanPropertySqlParameterSource(profitCenter);
		RowMapper<ProfitCenter> rowMapper = BeanPropertyRowMapper.newInstance(ProfitCenter.class);

		try {
			return jdbcTemplate.queryForObject(sql.toString(), paramSource, rowMapper);
		} catch (EmptyResultDataAccessException e) {
			logger.warn(Message.NO_RECORD_FOUND);
			return null;
		}
	}

	@Override
	public boolean isDuplicateKey(long profitCenterID, String profitCenterCode, TableType tableType) {
		logger.debug(Literal.ENTERING);

		// Prepare the SQL.
		String sql;
		String whereClause = "profitCenterCode = :profitCenterCode AND profitCenterID != :profitCenterID";

		switch (tableType) {
		case MAIN_TAB:
			sql = QueryUtil.getCountQuery("ProfitCenters", whereClause);
			break;
		case TEMP_TAB:
			sql = QueryUtil.getCountQuery("ProfitCenters_Temp", whereClause);
			break;
		default:
			sql = QueryUtil.getCountQuery(new String[] { "ProfitCenters_Temp", "ProfitCenters" }, whereClause);
			break;
		}

		// Execute the SQL, binding the arguments.
		logger.trace(Literal.SQL + sql);
		MapSqlParameterSource paramSource = new MapSqlParameterSource();
		paramSource.addValue("profitCenterID", profitCenterID);
		paramSource.addValue("profitCenterCode", profitCenterCode);

		Integer count = jdbcTemplate.queryForObject(sql, paramSource, Integer.class);

		boolean exists = false;
		if (count > 0) {
			exists = true;
		}

		logger.debug(Literal.LEAVING);
		return exists;
	}

	@Override
	public String save(ProfitCenter profitCenter, TableType tableType) {
		logger.debug(Literal.ENTERING);

		// Prepare the SQL.
		StringBuilder sql = new StringBuilder(" insert into ProfitCenters");
		sql.append(tableType.getSuffix());
		sql.append(" (profitCenterID, profitCenterCode, profitCenterDesc, active, ");
		sql.append(
				" Version , LastMntBy, LastMntOn, RecordStatus, RoleCode, NextRoleCode, TaskId, NextTaskId, RecordType, WorkflowId)");
		sql.append(" values(");
		sql.append(" :profitCenterID, :profitCenterCode, :profitCenterDesc, :active, ");
		sql.append(
				" :Version , :LastMntBy, :LastMntOn, :RecordStatus, :RoleCode, :NextRoleCode, :TaskId, :NextTaskId, :RecordType, :WorkflowId)");

		if (profitCenter.getProfitCenterID() <= 0) {
			profitCenter.setProfitCenterID(getNextValue("SeqProfitCenters"));
		}
		// Execute the SQL, binding the arguments.
		logger.trace(Literal.SQL + sql.toString());
		SqlParameterSource paramSource = new BeanPropertySqlParameterSource(profitCenter);

		try {
			jdbcTemplate.update(sql.toString(), paramSource);
		} catch (DuplicateKeyException e) {
			throw new ConcurrencyException(e);
		}

		logger.debug(Literal.LEAVING);
		return String.valueOf(profitCenter.getProfitCenterID());
	}

	@Override
	public void update(ProfitCenter profitCenter, TableType tableType) {
		logger.debug(Literal.ENTERING);

		// Prepare the SQL.
		StringBuilder sql = new StringBuilder("update ProfitCenters");
		sql.append(tableType.getSuffix());
		sql.append(
				"  set profitCenterCode = :profitCenterCode, profitCenterDesc = :profitCenterDesc, active = :active, ");
		sql.append(" LastMntOn = :LastMntOn, RecordStatus = :RecordStatus, RoleCode = :RoleCode,");
		sql.append(" NextRoleCode = :NextRoleCode, TaskId = :TaskId, NextTaskId = :NextTaskId,");
		sql.append(" RecordType = :RecordType, WorkflowId = :WorkflowId");
		sql.append(" where profitCenterID = :profitCenterID ");
		sql.append(QueryUtil.getConcurrencyCondition(tableType));

		// Execute the SQL, binding the arguments.
		logger.trace(Literal.SQL + sql.toString());

		SqlParameterSource paramSource = new BeanPropertySqlParameterSource(profitCenter);
		int recordCount = jdbcTemplate.update(sql.toString(), paramSource);

		// Check for the concurrency failure.
		if (recordCount == 0) {
			throw new ConcurrencyException();
		}

		logger.debug(Literal.LEAVING);
	}

	@Override
	public void delete(ProfitCenter profitCenter, TableType tableType) {
		logger.debug(Literal.ENTERING);

		// Prepare the SQL.
		StringBuilder sql = new StringBuilder("delete from ProfitCenters");
		sql.append(tableType.getSuffix());
		sql.append(" where profitCenterID = :profitCenterID ");
		sql.append(QueryUtil.getConcurrencyCondition(tableType));

		// Execute the SQL, binding the arguments.
		logger.trace(Literal.SQL + sql.toString());
		SqlParameterSource paramSource = new BeanPropertySqlParameterSource(profitCenter);
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
	public Long getPftCenterIDByCode(String profitCenterCode) {
		String sql = "Select ProfitCenterID From ProfitCenters Where ProfitCenterCode = ?";

		logger.debug(Literal.SQL.concat(sql));

		try {
			return this.jdbcOperations.queryForObject(sql, Long.class, profitCenterCode);
		} catch (EmptyResultDataAccessException e) {
			logger.warn(Message.NO_RECORD_FOUND);
			return null;
		}
	}
}
