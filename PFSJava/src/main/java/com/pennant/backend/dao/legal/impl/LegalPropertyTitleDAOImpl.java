/**
 * Copyright 2011 - Pennant Technologies
 * 
 * This file is part of Pennant Java Application Framework and related Products. 
 * All components/modules/functions/classes/logic in this software, unless 
 * otherwise stated, the property of Pennant Technologies. 
 * 
 * Copyright and other intellectual property laws protect these materials. 
 * Reproduction or retransmission of the materials, in whole or in part, in any manner, 
 * without the prior written consent of the copyright holder, is a violation of 
 * copyright law.
 */

/**
 ********************************************************************************************
 *                                 FILE HEADER                                              *
 ********************************************************************************************
 *																							*
 * FileName    		:  LegalPropertyTitleDAOImpl.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  18-06-2018    														*
 *                                                                  						*
 * Modified Date    :  18-06-2018    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 18-06-2018       PENNANT	                 0.1                                            * 
 *                                                                                          * 
 *                                                                                          * 
 *                                                                                          * 
 *                                                                                          * 
 *                                                                                          * 
 *                                                                                          * 
 *                                                                                          * 
 *                                                                                          * 
 ********************************************************************************************
*/
package com.pennant.backend.dao.legal.impl;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.PreparedStatementSetter;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.core.simple.ParameterizedBeanPropertyRowMapper;

import com.pennant.backend.dao.legal.LegalPropertyTitleDAO;
import com.pennant.backend.model.legal.LegalPropertyTitle;
import com.pennanttech.pennapps.core.ConcurrencyException;
import com.pennanttech.pennapps.core.DependencyFoundException;
import com.pennanttech.pennapps.core.jdbc.SequenceDao;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pff.core.TableType;

/**
 * Data access layer implementation for <code>LegalPropertyTitle</code> with set of CRUD operations.
 */
public class LegalPropertyTitleDAOImpl extends SequenceDao<LegalPropertyTitle> implements LegalPropertyTitleDAO {
	private static Logger logger = LogManager.getLogger(LegalPropertyTitleDAOImpl.class);

	public LegalPropertyTitleDAOImpl() {
		super();
	}

	@Override
	public LegalPropertyTitle getLegalPropertyTitle(long legalPropertyTitleId, String type) {
		logger.debug(Literal.ENTERING);

		// Prepare the SQL.
		StringBuilder sql = new StringBuilder("SELECT ");
		sql.append(" legalPropertyTitleId, legalId, title, ");

		sql.append(
				" Version, LastMntOn, LastMntBy,RecordStatus, RoleCode, NextRoleCode, TaskId, NextTaskId, RecordType, WorkflowId");
		sql.append(" From LegalPropertyTitle");
		sql.append(type);
		sql.append(" Where legalPropertyTitleId = :legalPropertyTitleId");

		// Execute the SQL, binding the arguments.
		logger.trace(Literal.SQL + sql.toString());

		LegalPropertyTitle legalPropertyTitle = new LegalPropertyTitle();
		legalPropertyTitle.setLegalPropertyTitleId(legalPropertyTitleId);

		SqlParameterSource paramSource = new BeanPropertySqlParameterSource(legalPropertyTitle);
		RowMapper<LegalPropertyTitle> rowMapper = ParameterizedBeanPropertyRowMapper
				.newInstance(LegalPropertyTitle.class);

		try {
			legalPropertyTitle = jdbcTemplate.queryForObject(sql.toString(), paramSource, rowMapper);
		} catch (EmptyResultDataAccessException e) {
			legalPropertyTitle = null;
		}

		logger.debug(Literal.LEAVING);
		return legalPropertyTitle;
	}

	@Override
	public List<LegalPropertyTitle> getLegalPropertyTitleList(long legalId, String type) {
		logger.debug(Literal.ENTERING);

		StringBuilder sql = new StringBuilder("Select");
		sql.append(" LegalPropertyTitleId, LegalId, Title, Version, LastMntOn, LastMntBy, RecordStatus");
		sql.append(", RoleCode, NextRoleCode, TaskId, NextTaskId, RecordType, WorkflowId");
		sql.append(" from LegalPropertyTitle");
		sql.append(StringUtils.trimToEmpty(type));
		sql.append(" Where legalId = ?");

		logger.trace(Literal.SQL + sql.toString());

		try {
			return this.jdbcOperations.query(sql.toString(), new PreparedStatementSetter() {
				@Override
				public void setValues(PreparedStatement ps) throws SQLException {
					int index = 1;
					ps.setLong(index++, legalId);
				}
			}, new RowMapper<LegalPropertyTitle>() {
				@Override
				public LegalPropertyTitle mapRow(ResultSet rs, int rowNum) throws SQLException {
					LegalPropertyTitle lpt = new LegalPropertyTitle();

					lpt.setLegalPropertyTitleId(rs.getLong("LegalPropertyTitleId"));
					lpt.setLegalId(rs.getLong("LegalId"));
					lpt.setTitle(rs.getString("Title"));
					lpt.setVersion(rs.getInt("Version"));
					lpt.setLastMntOn(rs.getTimestamp("LastMntOn"));
					lpt.setLastMntBy(rs.getLong("LastMntBy"));
					lpt.setRecordStatus(rs.getString("RecordStatus"));
					lpt.setRoleCode(rs.getString("RoleCode"));
					lpt.setNextRoleCode(rs.getString("NextRoleCode"));
					lpt.setTaskId(rs.getString("TaskId"));
					lpt.setNextTaskId(rs.getString("NextTaskId"));
					lpt.setRecordType(rs.getString("RecordType"));
					lpt.setWorkflowId(rs.getLong("WorkflowId"));

					return lpt;
				}
			});
		} catch (EmptyResultDataAccessException e) {
			logger.error(Literal.EXCEPTION, e);
		} finally {
			sql = null;
		}

		logger.debug(Literal.LEAVING);
		return new ArrayList<>();
	}

	@Override
	public String save(LegalPropertyTitle legalPropertyTitle, TableType tableType) {
		logger.debug(Literal.ENTERING);

		// Prepare the SQL.
		StringBuilder sql = new StringBuilder(" insert into LegalPropertyTitle");
		sql.append(tableType.getSuffix());
		sql.append(" (legalPropertyTitleId, legalId, title, ");
		sql.append(
				" Version , LastMntBy, LastMntOn, RecordStatus, RoleCode, NextRoleCode, TaskId, NextTaskId, RecordType, WorkflowId)");
		sql.append(" values(");
		sql.append(" :legalPropertyTitleId, :legalId, :title, ");
		sql.append(
				" :Version , :LastMntBy, :LastMntOn, :RecordStatus, :RoleCode, :NextRoleCode, :TaskId, :NextTaskId, :RecordType, :WorkflowId)");

		if (legalPropertyTitle.getId() == Long.MIN_VALUE) {
			legalPropertyTitle.setId(getNextValue("SeqLegalPropertyTitle"));
			logger.debug("get NextValue:" + legalPropertyTitle.getId());
		}

		// Execute the SQL, binding the arguments.
		logger.trace(Literal.SQL + sql.toString());
		SqlParameterSource paramSource = new BeanPropertySqlParameterSource(legalPropertyTitle);

		try {
			jdbcTemplate.update(sql.toString(), paramSource);
		} catch (DuplicateKeyException e) {
			throw new ConcurrencyException(e);
		}

		logger.debug(Literal.LEAVING);
		return String.valueOf(legalPropertyTitle.getLegalPropertyTitleId());
	}

	@Override
	public void update(LegalPropertyTitle legalPropertyTitle, TableType tableType) {
		logger.debug(Literal.ENTERING);
		// Prepare the SQL.
		StringBuilder sql = new StringBuilder("update LegalPropertyTitle");
		sql.append(tableType.getSuffix());
		sql.append(" set legalId = :legalId, title = :title, ");
		sql.append(" LastMntOn = :LastMntOn, RecordStatus = :RecordStatus, RoleCode = :RoleCode,");
		sql.append(" NextRoleCode = :NextRoleCode, TaskId = :TaskId, NextTaskId = :NextTaskId,");
		sql.append(" RecordType = :RecordType, WorkflowId = :WorkflowId");
		sql.append(" where legalPropertyTitleId = :legalPropertyTitleId ");

		// Execute the SQL, binding the arguments.
		logger.trace(Literal.SQL + sql.toString());

		SqlParameterSource paramSource = new BeanPropertySqlParameterSource(legalPropertyTitle);
		int recordCount = jdbcTemplate.update(sql.toString(), paramSource);

		// Check for the concurrency failure.
		if (recordCount == 0) {
			throw new ConcurrencyException();
		}
		logger.debug(Literal.LEAVING);
	}

	@Override
	public void delete(LegalPropertyTitle legalPropertyTitle, TableType tableType) {
		logger.debug(Literal.ENTERING);

		// Prepare the SQL.
		StringBuilder sql = new StringBuilder("delete from LegalPropertyTitle");
		sql.append(tableType.getSuffix());
		sql.append(" where legalPropertyTitleId = :legalPropertyTitleId ");

		// Execute the SQL, binding the arguments.
		logger.trace(Literal.SQL + sql.toString());
		SqlParameterSource paramSource = new BeanPropertySqlParameterSource(legalPropertyTitle);
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
	public void deleteList(LegalPropertyTitle legalPropertyTitle, String tableType) {

		StringBuilder deleteSql = new StringBuilder("Delete From LegalPropertyTitle");
		deleteSql.append(StringUtils.trimToEmpty(tableType));
		deleteSql.append(" Where legalId = :legalId");
		logger.debug("deleteSql: " + deleteSql.toString());

		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(legalPropertyTitle);
		this.jdbcTemplate.update(deleteSql.toString(), beanParameters);
	}

}
