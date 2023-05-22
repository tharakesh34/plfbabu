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
 * * FileName : DPDBucketDAOImpl.java * * Author : PENNANT TECHONOLOGIES * * Creation Date : 21-04-2017 * * Modified
 * Date : 21-04-2017 * * Description : * *
 ********************************************************************************************
 * Date Author Version Comments *
 ********************************************************************************************
 * 21-04-2017 PENNANT 0.1 * * * * * * * * *
 ********************************************************************************************
 */
package com.pennant.backend.dao.applicationmaster.impl;

import java.util.List;

import org.springframework.dao.DataAccessException;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;

import com.pennant.backend.dao.applicationmaster.DPDBucketDAO;
import com.pennant.backend.model.applicationmaster.DPDBucket;
import com.pennanttech.pennapps.core.ConcurrencyException;
import com.pennanttech.pennapps.core.DependencyFoundException;
import com.pennanttech.pennapps.core.jdbc.SequenceDao;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pennapps.core.resource.Message;
import com.pennanttech.pff.core.TableType;
import com.pennanttech.pff.core.util.QueryUtil;

/**
 * Data access layer implementation for <code>DPDBucket</code> with set of CRUD operations.
 */
public class DPDBucketDAOImpl extends SequenceDao<DPDBucket> implements DPDBucketDAO {
	public DPDBucketDAOImpl() {
		super();
	}

	@Override
	public DPDBucket getDPDBucket(long bucketID, String type) {
		logger.debug(Literal.ENTERING);

		// Prepare the SQL.
		StringBuilder sql = new StringBuilder("SELECT ");
		sql.append(" bucketID, bucketCode, bucketDesc, active, ");

		sql.append(
				" Version, LastMntOn, LastMntBy,RecordStatus, RoleCode, NextRoleCode, TaskId, NextTaskId, RecordType, WorkflowId");
		sql.append(" From DPDBUCKETS");
		sql.append(type);
		sql.append(" Where bucketID = :bucketID");

		// Execute the SQL, binding the arguments.
		logger.trace(Literal.SQL + sql.toString());

		DPDBucket dPDBucket = new DPDBucket();
		dPDBucket.setBucketID(bucketID);

		SqlParameterSource paramSource = new BeanPropertySqlParameterSource(dPDBucket);
		RowMapper<DPDBucket> rowMapper = BeanPropertyRowMapper.newInstance(DPDBucket.class);

		try {
			return jdbcTemplate.queryForObject(sql.toString(), paramSource, rowMapper);
		} catch (EmptyResultDataAccessException e) {
			logger.warn(Message.NO_RECORD_FOUND);
			return null;
		}
	}

	@Override
	public DPDBucket getDPDBucket(String bucketCode, String type) {
		logger.debug(Literal.ENTERING);

		// Prepare the SQL.
		StringBuilder sql = new StringBuilder("SELECT ");
		sql.append(" bucketID ");
		sql.append(" From DPDBUCKETS");
		sql.append(type);
		sql.append(" Where BucketCode = :BucketCode");

		// Execute the SQL, binding the arguments.
		logger.trace(Literal.SQL + sql.toString());

		DPDBucket dPDBucket = new DPDBucket();
		dPDBucket.setBucketCode(bucketCode);

		SqlParameterSource paramSource = new BeanPropertySqlParameterSource(dPDBucket);
		RowMapper<DPDBucket> rowMapper = BeanPropertyRowMapper.newInstance(DPDBucket.class);

		try {
			return jdbcTemplate.queryForObject(sql.toString(), paramSource, rowMapper);
		} catch (EmptyResultDataAccessException e) {
			logger.warn(Message.NO_RECORD_FOUND);
			return null;
		}
	}

	@Override
	public List<DPDBucket> getDPDBuckets() {
		logger.debug(Literal.ENTERING);

		// Prepare the SQL.
		StringBuilder sql = new StringBuilder("SELECT ");
		sql.append(" bucketID, bucketCode");
		sql.append(" From DPDBUCKETS");

		// Execute the SQL, binding the arguments.
		logger.trace(Literal.SQL + sql.toString());

		RowMapper<DPDBucket> rowMapper = BeanPropertyRowMapper.newInstance(DPDBucket.class);
		List<DPDBucket> dPDBucket = jdbcTemplate.query(sql.toString(), rowMapper);
		logger.debug(Literal.LEAVING);
		return dPDBucket;
	}

	@Override
	public boolean isDuplicateKey(long bucketID, String bucketCode, TableType tableType) {
		logger.debug(Literal.ENTERING);

		// Prepare the SQL.
		String sql;
		String whereClause = "bucketCode = :bucketCode AND bucketID != :bucketID";

		switch (tableType) {
		case MAIN_TAB:
			sql = QueryUtil.getCountQuery("DPDBUCKETS", whereClause);
			break;
		case TEMP_TAB:
			sql = QueryUtil.getCountQuery("DPDBUCKETS_Temp", whereClause);
			break;
		default:
			sql = QueryUtil.getCountQuery(new String[] { "DPDBUCKETS_Temp", "DPDBUCKETS" }, whereClause);
			break;
		}

		// Execute the SQL, binding the arguments.
		logger.trace(Literal.SQL + sql);
		MapSqlParameterSource paramSource = new MapSqlParameterSource();
		paramSource.addValue("bucketID", bucketID);
		paramSource.addValue("bucketCode", bucketCode);

		Integer count = jdbcTemplate.queryForObject(sql, paramSource, Integer.class);

		boolean exists = false;
		if (count > 0) {
			exists = true;
		}

		logger.debug(Literal.LEAVING);
		return exists;
	}

	@Override
	public String save(DPDBucket dPDBucket, TableType tableType) {
		logger.debug(Literal.ENTERING);

		// Prepare the SQL.
		StringBuilder sql = new StringBuilder("insert into DPDBUCKETS");
		sql.append(tableType.getSuffix());
		sql.append(" (bucketID, bucketCode, bucketDesc, active, ");
		sql.append(
				" Version , LastMntBy, LastMntOn, RecordStatus, RoleCode, NextRoleCode, TaskId, NextTaskId, RecordType, WorkflowId)");
		sql.append(" values(");
		sql.append(" :bucketID, :bucketCode, :bucketDesc, :active, ");
		sql.append(
				" :Version , :LastMntBy, :LastMntOn, :RecordStatus, :RoleCode, :NextRoleCode, :TaskId, :NextTaskId, :RecordType, :WorkflowId)");

		// Get the identity sequence number.
		if (dPDBucket.getBucketID() <= 0) {
			dPDBucket.setBucketID(getNextValue("SeqDPDBUCKETS"));
		}
		// Execute the SQL, binding the arguments.
		logger.trace(Literal.SQL + sql.toString());
		SqlParameterSource paramSource = new BeanPropertySqlParameterSource(dPDBucket);

		try {
			jdbcTemplate.update(sql.toString(), paramSource);
		} catch (DuplicateKeyException e) {
			throw new ConcurrencyException(e);
		}

		logger.debug(Literal.LEAVING);
		return String.valueOf(dPDBucket.getBucketID());
	}

	@Override
	public void update(DPDBucket dPDBucket, TableType tableType) {
		logger.debug(Literal.ENTERING);

		// Prepare the SQL.
		StringBuilder sql = new StringBuilder("update DPDBUCKETS");
		sql.append(tableType.getSuffix());
		sql.append("  set bucketCode = :bucketCode, bucketDesc = :bucketDesc, active = :active, ");
		sql.append(" LastMntOn = :LastMntOn, RecordStatus = :RecordStatus, RoleCode = :RoleCode,");
		sql.append(" NextRoleCode = :NextRoleCode, TaskId = :TaskId, NextTaskId = :NextTaskId,");
		sql.append(" RecordType = :RecordType, WorkflowId = :WorkflowId");
		sql.append(" where bucketID = :bucketID ");
		// sql.append(QueryUtil.getConcurrencyCondition(tableType));

		// Execute the SQL, binding the arguments.
		logger.trace(Literal.SQL + sql.toString());

		SqlParameterSource paramSource = new BeanPropertySqlParameterSource(dPDBucket);
		int recordCount = jdbcTemplate.update(sql.toString(), paramSource);

		// Check for the concurrency failure.
		if (recordCount == 0) {
			throw new ConcurrencyException();
		}

		logger.debug(Literal.LEAVING);
	}

	@Override
	public void delete(DPDBucket dPDBucket, TableType tableType) {
		logger.debug(Literal.ENTERING);

		// Prepare the SQL.
		StringBuilder sql = new StringBuilder("delete from DPDBUCKETS");
		sql.append(tableType.getSuffix());
		sql.append(" where bucketID = :bucketID ");
		// sql.append(QueryUtil.getConcurrencyCondition(tableType));

		// Execute the SQL, binding the arguments.
		logger.trace(Literal.SQL + sql.toString());
		SqlParameterSource paramSource = new BeanPropertySqlParameterSource(dPDBucket);
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

}