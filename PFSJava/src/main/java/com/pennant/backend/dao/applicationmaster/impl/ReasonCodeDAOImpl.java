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
 * FileName    		:  ReasonCodeDAOImpl.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  19-12-2017    														*
 *                                                                  						*
 * Modified Date    :  19-12-2017    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 19-12-2017       PENNANT	                 0.1                                            * 
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

import com.pennant.backend.dao.applicationmaster.ReasonCodeDAO;
import com.pennant.backend.model.applicationmaster.ReasonCode;
import com.pennanttech.pennapps.core.ConcurrencyException;
import com.pennanttech.pennapps.core.DependencyFoundException;
import com.pennanttech.pennapps.core.jdbc.SequenceDao;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pff.core.TableType;
import com.pennanttech.pff.core.util.QueryUtil;

/**
 * Data access layer implementation for <code>ReasonCode</code> with set of CRUD operations.
 */
public class ReasonCodeDAOImpl extends SequenceDao<ReasonCode> implements ReasonCodeDAO {
	private static Logger logger = LogManager.getLogger(ReasonCodeDAOImpl.class);

	public ReasonCodeDAOImpl() {
		super();
	}

	@Override
	public ReasonCode getReasonCode(long id, String type) {
		logger.debug(Literal.ENTERING);

		// Prepare the SQL.
		StringBuilder sql = new StringBuilder("SELECT ");
		sql.append(" id, reasonTypeID, reasonCategoryID, code, description, active,");

		sql.append(
				" Version, LastMntOn, LastMntBy,RecordStatus, RoleCode, NextRoleCode, TaskId, NextTaskId, RecordType, WorkflowId");

		if (StringUtils.trimToEmpty(type).contains("View")) {
			sql.append(",reasonTypeDesc,reasonCategoryDesc,reasonCategoryCode,reasonTypeCode");
		}
		sql.append(" From Reasons");
		sql.append(type);
		sql.append(" Where id = :id");

		// Execute the SQL, binding the arguments.
		logger.trace(Literal.SQL + sql.toString());

		ReasonCode reasonCode = new ReasonCode();
		reasonCode.setId(id);

		SqlParameterSource paramSource = new BeanPropertySqlParameterSource(reasonCode);
		RowMapper<ReasonCode> rowMapper = BeanPropertyRowMapper.newInstance(ReasonCode.class);

		try {
			reasonCode = jdbcTemplate.queryForObject(sql.toString(), paramSource, rowMapper);
		} catch (EmptyResultDataAccessException e) {
			logger.error("Exception: ", e);
			reasonCode = null;
		}

		logger.debug(Literal.LEAVING);
		return reasonCode;
	}

	@Override
	public boolean isDuplicateKey(long reasonTypeID, long reasonCategoryID, String code, TableType tableType) {
		logger.debug(Literal.ENTERING);

		// Prepare the SQL.
		String sql;
		String whereClause = "reasonTypeID = :reasonTypeID AND reasonCategoryID = :reasonCategoryID AND code = :code";

		switch (tableType) {
		case MAIN_TAB:
			sql = QueryUtil.getCountQuery("Reasons", whereClause);
			break;
		case TEMP_TAB:
			sql = QueryUtil.getCountQuery("Reasons_Temp", whereClause);
			break;
		default:
			sql = QueryUtil.getCountQuery(new String[] { "Reasons_Temp", "Reasons" }, whereClause);
			break;
		}

		// Execute the SQL, binding the arguments.
		logger.trace(Literal.SQL + sql);
		MapSqlParameterSource paramSource = new MapSqlParameterSource();
		paramSource.addValue("reasonTypeID", reasonTypeID);
		paramSource.addValue("reasonCategoryID", reasonCategoryID);
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
	public String save(ReasonCode reasonCode, TableType tableType) {
		logger.debug(Literal.ENTERING);

		// Prepare the SQL.
		StringBuilder sql = new StringBuilder(" insert into Reasons");
		sql.append(tableType.getSuffix());
		sql.append("(id, reasonTypeID, reasonCategoryID, code, description, active, ");
		sql.append(
				" Version , LastMntBy, LastMntOn, RecordStatus, RoleCode, NextRoleCode, TaskId, NextTaskId, RecordType, WorkflowId)");
		sql.append(" values(");
		sql.append(" :id, :reasonTypeID, :reasonCategoryID, :code, :description, :active, ");
		sql.append(
				" :Version , :LastMntBy, :LastMntOn, :RecordStatus, :RoleCode, :NextRoleCode, :TaskId, :NextTaskId, :RecordType, :WorkflowId)");

		// Get the identity sequence number.
		if (reasonCode.getId() <= 0) {
			reasonCode.setId(getNextValue("SeqReasons"));
		}

		// Execute the SQL, binding the arguments.
		logger.trace(Literal.SQL + sql.toString());
		SqlParameterSource paramSource = new BeanPropertySqlParameterSource(reasonCode);

		try {
			jdbcTemplate.update(sql.toString(), paramSource);
		} catch (DuplicateKeyException e) {
			throw new ConcurrencyException(e);
		}

		logger.debug(Literal.LEAVING);
		return String.valueOf(reasonCode.getId());
	}

	@Override
	public void update(ReasonCode reasonCode, TableType tableType) {
		logger.debug(Literal.ENTERING);

		// Prepare the SQL.
		StringBuilder sql = new StringBuilder("update Reasons");
		sql.append(tableType.getSuffix());
		sql.append("  set reasonTypeID = :reasonTypeID, reasonCategoryID = :reasonCategoryID, code = :code, ");
		sql.append(" description = :description, active = :active, ");
		sql.append(" LastMntBy = :LastMntBy, LastMntOn = :LastMntOn, ");
		sql.append(" RecordStatus = :RecordStatus, RoleCode = :RoleCode,");
		sql.append(" NextRoleCode = :NextRoleCode, TaskId = :TaskId, NextTaskId = :NextTaskId,");
		sql.append(" RecordType = :RecordType, WorkflowId = :WorkflowId");
		sql.append(" where id = :id ");
		sql.append(QueryUtil.getConcurrencyCondition(tableType));

		// Execute the SQL, binding the arguments.
		logger.trace(Literal.SQL + sql.toString());

		SqlParameterSource paramSource = new BeanPropertySqlParameterSource(reasonCode);
		int recordCount = jdbcTemplate.update(sql.toString(), paramSource);

		// Check for the concurrency failure.
		if (recordCount == 0) {
			throw new ConcurrencyException();
		}

		logger.debug(Literal.LEAVING);
	}

	@Override
	public void delete(ReasonCode reasonCode, TableType tableType) {
		logger.debug(Literal.ENTERING);

		// Prepare the SQL.
		StringBuilder sql = new StringBuilder("delete from Reasons");
		sql.append(tableType.getSuffix());
		sql.append(" where id = :id ");
		sql.append(QueryUtil.getConcurrencyCondition(tableType));

		// Execute the SQL, binding the arguments.
		logger.trace(Literal.SQL + sql.toString());
		SqlParameterSource paramSource = new BeanPropertySqlParameterSource(reasonCode);
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
	public boolean isreasonCategoryIDExists(long rCategoryCode) {
		logger.debug(Literal.ENTERING);
		MapSqlParameterSource source = new MapSqlParameterSource();
		source.addValue("reasonCategoryID", rCategoryCode);

		StringBuilder selectSql = new StringBuilder("SELECT COUNT(reasonCategoryID)");
		selectSql.append(" From Reasons_view ");
		selectSql.append(" Where reasonCategoryID=:reasonCategoryID");

		logger.debug("selectSql: " + selectSql.toString());
		int rcdCount = this.jdbcTemplate.queryForObject(selectSql.toString(), source, Integer.class);

		logger.debug(Literal.LEAVING);
		return rcdCount > 0 ? true : false;
	}

	@Override
	public boolean isreasonTypeIDExists(long rTypeCode) {
		logger.debug(Literal.ENTERING);
		MapSqlParameterSource source = new MapSqlParameterSource();
		source.addValue("reasonTypeID", rTypeCode);

		StringBuilder selectSql = new StringBuilder("SELECT COUNT(reasonTypeID)");
		selectSql.append(" From Reasons_view ");
		selectSql.append(" Where reasonTypeID=:reasonTypeID");

		logger.debug("selectSql: " + selectSql.toString());
		int rcdCount = this.jdbcTemplate.queryForObject(selectSql.toString(), source, Integer.class);

		logger.debug(Literal.LEAVING);
		return rcdCount > 0 ? true : false;
	}

	@Override
	public List<ReasonCode> getReasonDetails(String reasonTypeCode) {
		logger.debug(Literal.ENTERING);

		StringBuilder sql = new StringBuilder(
				"SELECT reasonTypeID, reasonTypeCode, reasonTypeDesc, reasonCategoryID, ");
		sql.append(" reasonCategoryCode, reasonCategoryDesc, code, description, active, version ");
		sql.append(" From Reasons_Aview");
		sql.append(" Where reasonTypeCode = :reasonTypeCode");

		logger.trace(Literal.SQL + sql.toString());

		MapSqlParameterSource sqlParameterSource = new MapSqlParameterSource();
		sqlParameterSource.addValue("reasonTypeCode", reasonTypeCode);
		RowMapper<ReasonCode> rowMapper = BeanPropertyRowMapper.newInstance(ReasonCode.class);

		try {
			return jdbcTemplate.query(sql.toString(), sqlParameterSource, rowMapper);
		} catch (EmptyResultDataAccessException e) {
			logger.error(Literal.EXCEPTION, e);
		}

		logger.debug(Literal.LEAVING);
		return null;
	}

}
