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
 * * FileName : ManualDeviationDAOImpl.java * * Author : PENNANT TECHONOLOGIES * * Creation Date : 03-04-2018 * *
 * Modified Date : 03-04-2018 * * Description : * *
 ********************************************************************************************
 * Date Author Version Comments *
 ********************************************************************************************
 * 03-04-2018 PENNANT 0.1 * * * * * * * * *
 ********************************************************************************************
 */
package com.pennant.backend.dao.applicationmaster.impl;

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

import com.pennant.backend.dao.applicationmaster.ManualDeviationDAO;
import com.pennant.backend.model.applicationmaster.ManualDeviation;
import com.pennanttech.pennapps.core.ConcurrencyException;
import com.pennanttech.pennapps.core.DependencyFoundException;
import com.pennanttech.pennapps.core.jdbc.SequenceDao;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pennapps.core.resource.Message;
import com.pennanttech.pff.core.TableType;
import com.pennanttech.pff.core.util.QueryUtil;

/**
 * Data access layer implementation for <code>ManualDeviation</code> with set of CRUD operations.
 */
public class ManualDeviationDAOImpl extends SequenceDao<ManualDeviation> implements ManualDeviationDAO {
	private static Logger logger = LogManager.getLogger(ManualDeviationDAOImpl.class);

	public ManualDeviationDAOImpl() {
		super();
	}

	@Override
	public ManualDeviation getManualDeviation(long deviationID, String type) {
		logger.debug(Literal.ENTERING);

		// Prepare the SQL.
		StringBuilder sql = new StringBuilder("SELECT ");
		sql.append(" deviationID, code, description, module, categorization");
		sql.append(", severity,  active");
		if (StringUtils.trimToEmpty(type).contains("View")) {
			sql.append(", categorizationCode,categorizationName");
		}
		sql.append(", Version, LastMntOn, LastMntBy,RecordStatus, RoleCode");
		sql.append(", NextRoleCode, TaskId, NextTaskId, RecordType, WorkflowId");
		sql.append(" From ManualDeviations");
		sql.append(type);
		sql.append(" Where deviationID = :deviationID");

		// Execute the SQL, binding the arguments.
		logger.trace(Literal.SQL + sql.toString());

		ManualDeviation manualDeviation = new ManualDeviation();
		manualDeviation.setDeviationID(deviationID);

		SqlParameterSource paramSource = new BeanPropertySqlParameterSource(manualDeviation);
		RowMapper<ManualDeviation> rowMapper = BeanPropertyRowMapper.newInstance(ManualDeviation.class);

		try {
			return jdbcTemplate.queryForObject(sql.toString(), paramSource, rowMapper);
		} catch (EmptyResultDataAccessException e) {
			logger.warn(Message.NO_RECORD_FOUND);
			return null;
		}
	}

	private static final String DESC_QUERY = "select md.code,md.description,md.severity"
			+ " from productDeviations pd inner join manualdeviations md on pd.deviationID=md.deviationID"
			+ " Where productdevid = :Productdevid";

	@Override
	public ManualDeviation getManualDeviationDesc(long deviationID) {
		logger.debug(Literal.ENTERING);
		MapSqlParameterSource source = new MapSqlParameterSource();
		source.addValue("Productdevid", deviationID);

		// Execute the SQL, binding the arguments.
		logger.trace(Literal.SQL + DESC_QUERY);

		ManualDeviation manualDeviation = new ManualDeviation();
		manualDeviation.setDeviationID(deviationID);

		RowMapper<ManualDeviation> rowMapper = BeanPropertyRowMapper.newInstance(ManualDeviation.class);

		try {
			manualDeviation = jdbcTemplate.queryForObject(DESC_QUERY, source, rowMapper);
		} catch (EmptyResultDataAccessException e) {
			manualDeviation = null;
		}

		logger.debug(Literal.LEAVING);
		return manualDeviation;
	}

	@Override
	public boolean isDuplicateKey(long deviationID, String code, TableType tableType) {
		logger.debug(Literal.ENTERING);

		// Prepare the SQL.
		String sql;
		String whereClause = "code = :code AND deviationID != :deviationID";

		switch (tableType) {
		case MAIN_TAB:
			sql = QueryUtil.getCountQuery("ManualDeviations", whereClause);
			break;
		case TEMP_TAB:
			sql = QueryUtil.getCountQuery("ManualDeviations_Temp", whereClause);
			break;
		default:
			sql = QueryUtil.getCountQuery(new String[] { "ManualDeviations_Temp", "ManualDeviations" }, whereClause);
			break;
		}

		// Execute the SQL, binding the arguments.
		logger.trace(Literal.SQL + sql);
		MapSqlParameterSource paramSource = new MapSqlParameterSource();
		paramSource.addValue("deviationID", deviationID);
		paramSource.addValue("code", code);

		Integer count = jdbcTemplate.queryForObject(sql, paramSource, Integer.class);

		boolean exists = false;
		if (count > 0) {
			exists = true;
		}

		logger.debug(Literal.LEAVING);
		return exists;
	}

	@Override
	public String save(ManualDeviation manualDeviation, TableType tableType) {
		logger.debug(Literal.ENTERING);

		// Prepare the SQL.
		StringBuilder sql = new StringBuilder(" insert into ManualDeviations");
		sql.append(tableType.getSuffix());
		sql.append("(deviationID, code, description, module, categorization, severity");
		sql.append(", active, Version , LastMntBy, LastMntOn, RecordStatus, RoleCode ");
		sql.append(", NextRoleCode, TaskId, NextTaskId, RecordType, WorkflowId)");
		sql.append(" values(");
		sql.append("  :deviationID, :code, :description, :module, :categorization, :severity");
		sql.append(", :active, :Version , :LastMntBy, :LastMntOn, :RecordStatus, :RoleCode");
		sql.append(", :NextRoleCode, :TaskId, :NextTaskId, :RecordType, :WorkflowId)");

		if (manualDeviation.getId() == Long.MIN_VALUE) {
			manualDeviation.setId(getNextValue("SeqManualDeviations"));
			logger.debug("get NextID:" + manualDeviation.getId());
		}

		// Execute the SQL, binding the arguments.
		logger.trace(Literal.SQL + sql.toString());
		SqlParameterSource paramSource = new BeanPropertySqlParameterSource(manualDeviation);

		try {
			jdbcTemplate.update(sql.toString(), paramSource);
		} catch (DuplicateKeyException e) {
			throw new ConcurrencyException(e);
		}

		logger.debug(Literal.LEAVING);
		return String.valueOf(manualDeviation.getDeviationID());
	}

	@Override
	public void update(ManualDeviation manualDeviation, TableType tableType) {
		logger.debug(Literal.ENTERING);

		// Prepare the SQL.
		StringBuilder sql = new StringBuilder("update ManualDeviations");
		sql.append(tableType.getSuffix());
		sql.append("  set code = :code, description = :description, module = :module, ");
		sql.append(" categorization = :categorization, severity = :severity, active = :active, ");
		sql.append(" LastMntOn = :LastMntOn, RecordStatus = :RecordStatus, RoleCode = :RoleCode,");
		sql.append(" NextRoleCode = :NextRoleCode, TaskId = :TaskId, NextTaskId = :NextTaskId,");
		sql.append(" RecordType = :RecordType, WorkflowId = :WorkflowId");
		sql.append(" where deviationID = :deviationID ");
		sql.append(QueryUtil.getConcurrencyCondition(tableType));

		// Execute the SQL, binding the arguments.
		logger.trace(Literal.SQL + sql.toString());

		SqlParameterSource paramSource = new BeanPropertySqlParameterSource(manualDeviation);
		int recordCount = jdbcTemplate.update(sql.toString(), paramSource);

		// Check for the concurrency failure.
		if (recordCount == 0) {
			throw new ConcurrencyException();
		}

		logger.debug(Literal.LEAVING);
	}

	@Override
	public void delete(ManualDeviation manualDeviation, TableType tableType) {
		logger.debug(Literal.ENTERING);

		// Prepare the SQL.
		StringBuilder sql = new StringBuilder("delete from ManualDeviations");
		sql.append(tableType.getSuffix());
		sql.append(" where deviationID = :deviationID ");
		sql.append(QueryUtil.getConcurrencyCondition(tableType));

		// Execute the SQL, binding the arguments.
		logger.trace(Literal.SQL + sql.toString());
		SqlParameterSource paramSource = new BeanPropertySqlParameterSource(manualDeviation);
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
	public boolean isExistsFieldCodeID(long fieldCodeID, String type) {
		logger.debug(Literal.ENTERING);

		MapSqlParameterSource mapSqlParameterSource = new MapSqlParameterSource();
		mapSqlParameterSource.addValue("FieldCodeID", fieldCodeID);

		StringBuilder sql = new StringBuilder("SELECT  COUNT(*)  FROM  ManualDeviations");
		sql.append(StringUtils.trimToEmpty(type));
		sql.append(" Where Categorization = :FieldCodeID OR Severity = :FieldCodeID");

		logger.debug("selectSql: " + sql.toString());

		return this.jdbcTemplate.queryForObject(sql.toString(), mapSqlParameterSource, Integer.class) > 0;
	}

	@Override
	public long getDeviationIdByCode(String deviationCode) {
		logger.debug(Literal.ENTERING);

		MapSqlParameterSource mapSqlParameterSource = new MapSqlParameterSource();
		mapSqlParameterSource.addValue("DeviationCode", deviationCode);
		StringBuilder sql = new StringBuilder();

		sql.append("Select DeviationID From ManualDeviations");
		sql.append(" Where Code = :DeviationCode");
		logger.debug(Literal.SQL + sql.toString());
		logger.debug(Literal.ENTERING);
		return this.jdbcTemplate.queryForObject(sql.toString(), mapSqlParameterSource, Long.class);
	}

	@Override
	public List<ManualDeviation> getManualDeviation(String categorizationCode, String type) {
		logger.debug(Literal.ENTERING);

		// Prepare the SQL.
		StringBuilder sql = new StringBuilder("Select");
		sql.append("  deviationID, code, description, module, categorization, severity");
		sql.append(", active, Version, LastMntOn, LastMntBy, RecordStatus, RoleCode");
		sql.append(", NextRoleCode, TaskId, NextTaskId, RecordType, WorkflowId");

		if (StringUtils.trimToEmpty(type).contains("View")) {
			sql.append(", categorizationCode, categorizationName");
		}

		sql.append(" From ManualDeviations");
		sql.append(type);
		sql.append(" Where categorizationCode = :categorizationCode");

		// Execute the SQL, binding the arguments.
		logger.trace(Literal.SQL + sql.toString());

		MapSqlParameterSource parameterSource = new MapSqlParameterSource();
		parameterSource.addValue("categorizationCode", categorizationCode);

		RowMapper<ManualDeviation> rowMapper = BeanPropertyRowMapper.newInstance(ManualDeviation.class);

		return jdbcTemplate.query(sql.toString(), parameterSource, rowMapper);
	}

	@Override
	public ManualDeviation getManualDeviationByCode(String code, String type) {
		logger.debug(Literal.ENTERING);

		// Prepare the SQL.
		StringBuilder sql = new StringBuilder("Select");
		sql.append("  deviationID, code, description, module, categorization, severity");
		sql.append(", active, Version, LastMntOn, LastMntBy, RecordStatus, RoleCode");
		sql.append(", NextRoleCode, TaskId, NextTaskId, RecordType, WorkflowId");
		if (StringUtils.trimToEmpty(type).contains("View")) {
			sql.append(", categorizationCode, categorizationName");
		}
		sql.append(" From ManualDeviations");
		sql.append(type);
		sql.append(" Where code = :code");

		// Execute the SQL, binding the arguments.
		logger.trace(Literal.SQL + sql.toString());
		MapSqlParameterSource parameterSource = new MapSqlParameterSource();
		parameterSource.addValue("code", code);

		RowMapper<ManualDeviation> rowMapper = BeanPropertyRowMapper.newInstance(ManualDeviation.class);

		try {
			return jdbcTemplate.queryForObject(sql.toString(), parameterSource, rowMapper);
		} catch (EmptyResultDataAccessException e) {
			logger.warn(Message.NO_RECORD_FOUND);
			return null;
		}
	}
}
