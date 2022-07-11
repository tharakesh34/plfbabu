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
 * * FileName : BuilderGroupDAOImpl.java * * Author : PENNANT TECHONOLOGIES * * Creation Date : 17-05-2017 * * Modified
 * Date : 17-05-2017 * * Description : * *
 ********************************************************************************************
 * Date Author Version Comments *
 ********************************************************************************************
 * 17-05-2017 PENNANT 0.1 * * * * * * * * *
 ********************************************************************************************
 */
package com.pennant.backend.dao.systemmasters.impl;

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

import com.pennant.backend.dao.systemmasters.BuilderGroupDAO;
import com.pennant.backend.model.systemmasters.BuilderGroup;
import com.pennanttech.pennapps.core.ConcurrencyException;
import com.pennanttech.pennapps.core.DependencyFoundException;
import com.pennanttech.pennapps.core.jdbc.SequenceDao;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pennapps.core.resource.Message;
import com.pennanttech.pff.core.TableType;
import com.pennanttech.pff.core.util.QueryUtil;

/**
 * Data access layer implementation for <code>BuilderGroup</code> with set of CRUD operations.
 */
public class BuilderGroupDAOImpl extends SequenceDao<BuilderGroup> implements BuilderGroupDAO {
	private static Logger logger = LogManager.getLogger(BuilderGroupDAOImpl.class);

	public BuilderGroupDAOImpl() {
		super();
	}

	@Override
	public BuilderGroup getBuilderGroup(long id, String type) {
		logger.debug(Literal.ENTERING);

		// Prepare the SQL.
		StringBuilder sql = new StringBuilder("SELECT ");
		sql.append(
				" id, name, segmentation, pedeveloperid, city, province, pinCodeId, expLmtOnAmt, expLmtOnNoOfUnits, currExpUnits, currExpAmt,");
		if (type.contains("View")) {
			sql.append(" cityName, provinceName, poBox, areaName,");
		}

		sql.append(
				" Version, LastMntOn, LastMntBy,RecordStatus, RoleCode, NextRoleCode, TaskId, NextTaskId, RecordType, WorkflowId");
		sql.append(" From BuilderGroup");
		sql.append(type);
		sql.append(" Where id = :id");

		// Execute the SQL, binding the arguments.
		logger.trace(Literal.SQL + sql.toString());

		BuilderGroup builderGroup = new BuilderGroup();
		builderGroup.setId(id);

		SqlParameterSource paramSource = new BeanPropertySqlParameterSource(builderGroup);
		RowMapper<BuilderGroup> rowMapper = BeanPropertyRowMapper.newInstance(BuilderGroup.class);

		try {
			return jdbcTemplate.queryForObject(sql.toString(), paramSource, rowMapper);
		} catch (EmptyResultDataAccessException e) {
			logger.warn(Message.NO_RECORD_FOUND);
			return null;
		}
	}

	@Override
	public boolean isDuplicateKey(long id, String name, TableType tableType) {
		logger.debug(Literal.ENTERING);

		// Prepare the SQL.
		String sql;
		String whereClause = "name = :name AND id != :id";

		switch (tableType) {
		case MAIN_TAB:
			sql = QueryUtil.getCountQuery("BuilderGroup", whereClause);
			break;
		case TEMP_TAB:
			sql = QueryUtil.getCountQuery("BuilderGroup_Temp", whereClause);
			break;
		default:
			sql = QueryUtil.getCountQuery(new String[] { "BuilderGroup_Temp", "BuilderGroup" }, whereClause);
			break;
		}

		// Execute the SQL, binding the arguments.
		logger.trace(Literal.SQL + sql);
		MapSqlParameterSource paramSource = new MapSqlParameterSource();
		paramSource.addValue("id", id);
		paramSource.addValue("name", name);

		Integer count = jdbcTemplate.queryForObject(sql, paramSource, Integer.class);

		boolean exists = false;
		if (count > 0) {
			exists = true;
		}

		logger.debug(Literal.LEAVING);
		return exists;
	}

	@Override
	public String save(BuilderGroup builderGroup, TableType tableType) {
		logger.debug(Literal.ENTERING);
		// Prepare the SQL.
		StringBuilder sql = new StringBuilder(" insert into BuilderGroup");
		sql.append(tableType.getSuffix());
		sql.append(
				" (id, name, segmentation, pedeveloperid, city, province, pincodeid, expLmtOnAmt, expLmtOnNoOfUnits, currExpUnits, currExpAmt, ");
		sql.append(
				" Version , LastMntBy, LastMntOn, RecordStatus, RoleCode, NextRoleCode, TaskId, NextTaskId, RecordType, WorkflowId)");
		sql.append(" values(");
		sql.append(
				" :id, :name, :segmentation, :peDeveloperId, :city, :province, :pinCodeId, :expLmtOnAmt, :expLmtOnNoOfUnits, :currExpUnits, :currExpAmt, ");
		sql.append(
				" :Version , :LastMntBy, :LastMntOn, :RecordStatus, :RoleCode, :NextRoleCode, :TaskId, :NextTaskId, :RecordType, :WorkflowId)");

		// Get the identity sequence number.
		if (builderGroup.getId() <= 0) {
			builderGroup.setId(getNextValue("SeqBuilderGroup"));
		}

		// Execute the SQL, binding the arguments.
		logger.trace(Literal.SQL + sql.toString());
		SqlParameterSource paramSource = new BeanPropertySqlParameterSource(builderGroup);

		try {
			jdbcTemplate.update(sql.toString(), paramSource);
		} catch (DuplicateKeyException e) {
			throw new ConcurrencyException(e);
		}

		logger.debug(Literal.LEAVING);
		return String.valueOf(builderGroup.getId());
	}

	@Override
	public void update(BuilderGroup builderGroup, TableType tableType) {
		logger.debug(Literal.ENTERING);

		// Prepare the SQL.
		StringBuilder sql = new StringBuilder("update BuilderGroup");
		sql.append(tableType.getSuffix());
		sql.append(
				"  set name = :name, segmentation = :segmentation, pedeveloperid = :peDeveloperId, city = :city, province = :province, pincodeid = :pinCodeId, ");
		sql.append(
				" expLmtOnAmt= :expLmtOnAmt, expLmtOnNoOfUnits = :expLmtOnNoOfUnits, currExpUnits = :currExpUnits, currExpAmt = :currExpAmt, ");
		sql.append(" Version = :Version, LastMntOn = :LastMntOn, RecordStatus = :RecordStatus, RoleCode = :RoleCode,");
		sql.append(" NextRoleCode = :NextRoleCode, TaskId = :TaskId, NextTaskId = :NextTaskId,");
		sql.append(" RecordType = :RecordType, WorkflowId = :WorkflowId");
		sql.append(" where id = :id ");
		sql.append(QueryUtil.getConcurrencyCondition(tableType));

		// Execute the SQL, binding the arguments.
		logger.trace(Literal.SQL + sql.toString());

		SqlParameterSource paramSource = new BeanPropertySqlParameterSource(builderGroup);
		int recordCount = jdbcTemplate.update(sql.toString(), paramSource);

		// Check for the concurrency failure.
		if (recordCount == 0) {
			throw new ConcurrencyException();
		}

		logger.debug(Literal.LEAVING);
	}

	@Override
	public void delete(BuilderGroup builderGroup, TableType tableType) {
		logger.debug(Literal.ENTERING);

		// Prepare the SQL.
		StringBuilder sql = new StringBuilder("delete from BuilderGroup");
		sql.append(tableType.getSuffix());
		sql.append(" where id = :id ");
		sql.append(QueryUtil.getConcurrencyCondition(tableType));

		// Execute the SQL, binding the arguments.
		logger.trace(Literal.SQL + sql.toString());
		SqlParameterSource paramSource = new BeanPropertySqlParameterSource(builderGroup);
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
	public boolean isIdExists(long id) {
		logger.debug("Entering");
		MapSqlParameterSource source = null;
		StringBuilder sql = null;

		sql = new StringBuilder();
		sql.append(" Select COUNT(*) from BuilderCompany ");
		sql.append(" Where GroupId = :GroupId ");
		logger.debug("Sql: " + sql.toString());

		source = new MapSqlParameterSource();
		source.addValue("GroupId", id);
		try {
			if (this.jdbcTemplate.queryForObject(sql.toString(), source, Integer.class) > 0) {
				return true;
			}
		} catch (Exception e) {
			logger.error(e);
		} finally {
			source = null;
			sql = null;
			logger.debug("Leaving");
		}
		return false;
	}

}
