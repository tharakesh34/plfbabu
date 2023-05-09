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
 * * FileName : AssignmentDAOImpl.java * * Author : PENNANT TECHONOLOGIES * * Creation Date : 12-09-2018 * * Modified
 * Date : 12-09-2018 * * Description : * *
 ********************************************************************************************
 * Date Author Version Comments *
 ********************************************************************************************
 * 12-09-2018 PENNANT 0.1 * * * * * * * * *
 ********************************************************************************************
 */

package com.pennant.backend.dao.applicationmaster.impl;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;

import com.pennant.backend.dao.applicationmaster.AssignmentDAO;
import com.pennant.backend.model.applicationmaster.Assignment;
import com.pennanttech.pennapps.core.ConcurrencyException;
import com.pennanttech.pennapps.core.DependencyFoundException;
import com.pennanttech.pennapps.core.jdbc.SequenceDao;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pennapps.core.resource.Message;
import com.pennanttech.pff.core.TableType;
import com.pennanttech.pff.core.util.QueryUtil;

/**
 * Data access layer implementation for <code>Assignment</code> with set of CRUD operations.
 */
public class AssignmentDAOImpl extends SequenceDao<Assignment> implements AssignmentDAO {
	private static Logger logger = LogManager.getLogger(AssignmentDAOImpl.class);

	public AssignmentDAOImpl() {
		super();
	}

	@Override
	public Assignment getAssignment(long id, String type) {
		logger.debug(Literal.ENTERING);

		StringBuilder sql = new StringBuilder("Select");
		sql.append(" Id, Description, DealId, LoanType, DisbDate, SharingPercentage, Gst, OpexFeeType");
		sql.append(", Active");

		if (StringUtils.trimToEmpty(type).contains("View")) {
			sql.append(", DealCode, DealCodeDesc, LoanTypeDesc, EntityCode");
		}

		sql.append(", Version, LastMntOn, LastMntBy, RecordStatus, RoleCode, NextRoleCode");
		sql.append(", TaskId, NextTaskId, RecordType, WorkflowId");
		sql.append(" from Assignment");
		sql.append(StringUtils.trimToEmpty(type));
		sql.append(" Where id = ?");

		logger.trace(Literal.SQL + sql.toString());

		try {
			return this.jdbcOperations.queryForObject(sql.toString(), new RowMapper<Assignment>() {
				@Override
				public Assignment mapRow(ResultSet rs, int rowNum) throws SQLException {
					Assignment a = new Assignment();

					a.setId(rs.getLong("Id"));
					a.setDescription(rs.getString("Description"));
					a.setDealId(rs.getLong("DealId"));
					a.setLoanType(rs.getString("LoanType"));
					a.setDisbDate(rs.getTimestamp("DisbDate"));
					a.setSharingPercentage(rs.getBigDecimal("SharingPercentage"));
					a.setGst(rs.getBoolean("Gst"));
					a.setOpexFeeType(rs.getString("OpexFeeType"));
					a.setActive(rs.getBoolean("Active"));
					a.setDealCode(rs.getString("DealCode"));
					a.setDealCodeDesc(rs.getString("DealCodeDesc"));
					a.setLoanTypeDesc(rs.getString("LoanTypeDesc"));
					a.setEntityCode(rs.getString("EntityCode"));
					a.setVersion(rs.getInt("Version"));
					a.setLastMntOn(rs.getTimestamp("LastMntOn"));
					a.setLastMntBy(rs.getLong("LastMntBy"));
					a.setRecordStatus(rs.getString("RecordStatus"));
					a.setRoleCode(rs.getString("RoleCode"));
					a.setNextRoleCode(rs.getString("NextRoleCode"));
					a.setTaskId(rs.getString("TaskId"));
					a.setNextTaskId(rs.getString("NextTaskId"));
					a.setRecordType(rs.getString("RecordType"));
					a.setWorkflowId(rs.getLong("WorkflowId"));

					return a;
				}
			}, id);
		} catch (EmptyResultDataAccessException e) {
			logger.warn(Message.NO_RECORD_FOUND);
			return null;
		}
	}

	@Override
	public String save(Assignment assignment, TableType tableType) {
		logger.debug(Literal.ENTERING);

		// Prepare the SQL.
		StringBuilder sql = new StringBuilder(" insert into Assignment");
		sql.append(tableType.getSuffix());
		sql.append("(id, description, dealid, loanType, disbDate, sharingPercentage, ");
		sql.append(" gST, opexFeeType, active,");
		sql.append(
				" Version , LastMntBy, LastMntOn, RecordStatus, RoleCode, NextRoleCode, TaskId, NextTaskId, RecordType, WorkflowId)");
		sql.append(" values(");
		sql.append(" :id, :description, :dealId, :loanType, :disbDate, :sharingPercentage, ");
		sql.append(" :gst, :opexFeeType, ");
		sql.append(" :active, ");
		sql.append(
				" :Version , :LastMntBy, :LastMntOn, :RecordStatus, :RoleCode, :NextRoleCode, :TaskId, :NextTaskId, :RecordType, :WorkflowId)");

		if (assignment.getId() == Long.MIN_VALUE) {
			assignment.setId(getNextValue("SeqAssignment"));
			logger.debug("get NextID:" + assignment.getId());
		}

		// Execute the SQL, binding the arguments.
		logger.trace(Literal.SQL + sql.toString());
		SqlParameterSource paramSource = new BeanPropertySqlParameterSource(assignment);

		try {
			jdbcTemplate.update(sql.toString(), paramSource);
		} catch (DuplicateKeyException e) {
			throw new ConcurrencyException(e);
		}

		logger.debug(Literal.LEAVING);
		return String.valueOf(assignment.getId());
	}

	@Override
	public void update(Assignment assignment, TableType tableType) {
		logger.debug(Literal.ENTERING);

		// Prepare the SQL.
		StringBuilder sql = new StringBuilder("update Assignment");
		sql.append(tableType.getSuffix());
		sql.append("  set description = :description, dealId = :dealId, loanType = :loanType, ");
		sql.append(" disbDate = :disbDate, sharingPercentage = :sharingPercentage, ");
		sql.append(" gst = :gst, ");
		sql.append(" opexFeeType = :opexFeeType, active = :active, ");
		sql.append(" LastMntOn = :LastMntOn, RecordStatus = :RecordStatus, RoleCode = :RoleCode,");
		sql.append(" NextRoleCode = :NextRoleCode, TaskId = :TaskId, NextTaskId = :NextTaskId,");
		sql.append(" RecordType = :RecordType, WorkflowId = :WorkflowId");
		sql.append(" where id = :id ");
		sql.append(QueryUtil.getConcurrencyCondition(tableType));

		// Execute the SQL, binding the arguments.
		logger.trace(Literal.SQL + sql.toString());

		SqlParameterSource paramSource = new BeanPropertySqlParameterSource(assignment);
		int recordCount = jdbcTemplate.update(sql.toString(), paramSource);

		// Check for the concurrency failure.
		if (recordCount == 0) {
			throw new ConcurrencyException();
		}

		logger.debug(Literal.LEAVING);
	}

	@Override
	public void delete(Assignment assignment, TableType tableType) {
		logger.debug(Literal.ENTERING);

		// Prepare the SQL.
		StringBuilder sql = new StringBuilder("delete from Assignment");
		sql.append(tableType.getSuffix());
		sql.append(" where id = :id ");
		sql.append(QueryUtil.getConcurrencyCondition(tableType));

		logger.trace(Literal.SQL + sql.toString());
		SqlParameterSource paramSource = new BeanPropertySqlParameterSource(assignment);
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

	public List<String> getFinTypes(long dealId) {
		logger.debug(Literal.ENTERING);

		// Prepare the SQL.
		StringBuilder sql = new StringBuilder("select loantypecode from ASSIGNMENTDEALLOANTYPE");
		sql.append(" where dealid = :dealId ");

		// Execute the SQL, binding the arguments.
		logger.trace(Literal.SQL + sql.toString());
		MapSqlParameterSource paramSource = new MapSqlParameterSource();
		paramSource.addValue("dealId", dealId);
		try {
			logger.debug(Literal.LEAVING);
			return jdbcTemplate.queryForList(sql.toString(), paramSource, String.class);

		} catch (DataAccessException e) {
			throw new DependencyFoundException(e);
		}

	}

	@Override
	public int getMappedAssignments(long dealId) {
		logger.debug(Literal.ENTERING);

		// Prepare the SQL.
		StringBuilder sql = new StringBuilder("select COUNT(*) from Assignment");
		sql.append(" where dealid = :dealId ");

		// Execute the SQL, binding the arguments.
		logger.trace(Literal.SQL + sql.toString());
		MapSqlParameterSource paramSource = new MapSqlParameterSource();
		paramSource.addValue("dealId", dealId);

		logger.debug(Literal.LEAVING);
		return this.jdbcTemplate.queryForObject(sql.toString(), paramSource, Integer.class);
	}
}
