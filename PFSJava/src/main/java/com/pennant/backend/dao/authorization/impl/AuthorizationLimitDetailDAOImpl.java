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
 * FileName    		:  AuthorizationLimitDetailDAOImpl.java                                 * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  06-04-2018    														*
 *                                                                  						*
 * Modified Date    :  06-04-2018    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 06-04-2018       PENNANT	                 0.1                                            * 
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
package com.pennant.backend.dao.authorization.impl;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.core.simple.ParameterizedBeanPropertyRowMapper;

import com.pennant.backend.dao.authorization.AuthorizationLimitDetailDAO;
import com.pennant.backend.model.authorization.AuthorizationLimitDetail;
import com.pennanttech.pennapps.core.ConcurrencyException;
import com.pennanttech.pennapps.core.DependencyFoundException;
import com.pennanttech.pennapps.core.jdbc.SequenceDao;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pff.core.TableType;
import com.pennanttech.pff.core.util.QueryUtil;

/**
 * Data access layer implementation for <code>AuthorizationLimitDetail</code>
 * with set of CRUD operations.
 */
public class AuthorizationLimitDetailDAOImpl extends SequenceDao<AuthorizationLimitDetail>
		implements AuthorizationLimitDetailDAO {
	private static Logger logger = Logger.getLogger(AuthorizationLimitDetailDAOImpl.class);

	public AuthorizationLimitDetailDAOImpl() {
		super();
	}

	@Override
	public AuthorizationLimitDetail getAuthorizationLimitDetail(long id, String type) {
		logger.debug(Literal.ENTERING);

		// Prepare the SQL.
		StringBuilder sql = new StringBuilder("SELECT ");
		sql.append(" id, authLimitId, code, limitAmount, ");

		if (StringUtils.trimToEmpty(type).contains("View")) {
			sql.append(" ProductDesc,CollateralDesc, ");
		}

		sql.append(
				" Version, LastMntOn, LastMntBy,RecordStatus, RoleCode, NextRoleCode, TaskId, NextTaskId, RecordType, WorkflowId");
		sql.append(" From Auth_Limit_Details");
		sql.append(type);
		sql.append(" Where id = :id");

		// Execute the SQL, binding the arguments.
		logger.trace(Literal.SQL + sql.toString());

		AuthorizationLimitDetail authorizationLimitDetail = new AuthorizationLimitDetail();
		authorizationLimitDetail.setId(id);

		SqlParameterSource paramSource = new BeanPropertySqlParameterSource(authorizationLimitDetail);
		RowMapper<AuthorizationLimitDetail> rowMapper = ParameterizedBeanPropertyRowMapper
				.newInstance(AuthorizationLimitDetail.class);

		try {
			authorizationLimitDetail = jdbcTemplate.queryForObject(sql.toString(), paramSource, rowMapper);
		} catch (EmptyResultDataAccessException e) {
			logger.error("Exception: ", e);
			authorizationLimitDetail = null;
		}

		logger.debug(Literal.LEAVING);
		return authorizationLimitDetail;
	}

	@Override
	public AuthorizationLimitDetail getAuthorizationLimitDetailByCode(long authLimitId, String code,
			TableType tableType) {
		logger.debug(Literal.ENTERING);

		// Prepare the SQL.
		StringBuilder sql = new StringBuilder("SELECT ");
		sql.append(" id, authLimitId, code, limitAmount, ");

		if (tableType.getSuffix().contains("View")) {
			sql.append(" ProductDesc,CollateralDesc, ");
		}

		sql.append(
				" Version, LastMntOn, LastMntBy,RecordStatus, RoleCode, NextRoleCode, TaskId, NextTaskId, RecordType, WorkflowId");
		sql.append(" From Auth_Limit_Details");
		sql.append(tableType.getSuffix());
		sql.append(" Where authLimitId=:authLimitId and code = :code");

		// Execute the SQL, binding the arguments.
		logger.trace(Literal.SQL + sql.toString());

		AuthorizationLimitDetail authorizationLimitDetail = new AuthorizationLimitDetail();
		authorizationLimitDetail.setAuthLimitId(authLimitId);
		authorizationLimitDetail.setCode(code);

		SqlParameterSource paramSource = new BeanPropertySqlParameterSource(authorizationLimitDetail);
		RowMapper<AuthorizationLimitDetail> rowMapper = ParameterizedBeanPropertyRowMapper
				.newInstance(AuthorizationLimitDetail.class);

		try {
			authorizationLimitDetail = jdbcTemplate.queryForObject(sql.toString(), paramSource, rowMapper);
		} catch (EmptyResultDataAccessException e) {
			logger.error("Exception: ", e);
			authorizationLimitDetail = null;
		}

		logger.debug(Literal.LEAVING);
		return authorizationLimitDetail;
	}

	@Override
	public boolean isDuplicateKey(long id, long authLimitId, String code, TableType tableType) {
		logger.debug(Literal.ENTERING);

		// Prepare the SQL.
		String sql;
		String whereClause = "authLimitId = :authLimitId AND code = :code AND id != :id";

		switch (tableType) {
		case MAIN_TAB:
			sql = QueryUtil.getCountQuery("Auth_Limit_Details", whereClause);
			break;
		case TEMP_TAB:
			sql = QueryUtil.getCountQuery("Auth_Limit_Details_Temp", whereClause);
			break;
		default:
			sql = QueryUtil.getCountQuery(new String[] { "Auth_Limit_Details_Temp", "Auth_Limit_Details" },
					whereClause);
			break;
		}

		// Execute the SQL, binding the arguments.
		logger.trace(Literal.SQL + sql);
		MapSqlParameterSource paramSource = new MapSqlParameterSource();
		paramSource.addValue("id", id);
		paramSource.addValue("authLimitId", authLimitId);
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
	public String save(AuthorizationLimitDetail authorizationLimitDetail, TableType tableType) {
		logger.debug(Literal.ENTERING);

		// Prepare the SQL.
		StringBuilder sql = new StringBuilder(" insert into Auth_Limit_Details");
		sql.append(tableType.getSuffix());
		sql.append(" (id, authLimitId, code, limitAmount, ");
		sql.append(
				" Version , LastMntBy, LastMntOn, RecordStatus, RoleCode, NextRoleCode, TaskId, NextTaskId, RecordType, WorkflowId)");
		sql.append(" values(");
		sql.append(" :id, :authLimitId, :code, :limitAmount, ");
		sql.append(
				" :Version , :LastMntBy, :LastMntOn, :RecordStatus, :RoleCode, :NextRoleCode, :TaskId, :NextTaskId, :RecordType, :WorkflowId)");

		if (authorizationLimitDetail.getId() == Long.MIN_VALUE) {
			authorizationLimitDetail.setId(getNextValue("SeqAuth_Limit_Details"));
			logger.debug("get NextID:" + authorizationLimitDetail.getId());
		}

		// Execute the SQL, binding the arguments.
		logger.trace(Literal.SQL + sql.toString());
		SqlParameterSource paramSource = new BeanPropertySqlParameterSource(authorizationLimitDetail);

		try {
			jdbcTemplate.update(sql.toString(), paramSource);
		} catch (DuplicateKeyException e) {
			throw new ConcurrencyException(e);
		}

		logger.debug(Literal.LEAVING);
		return String.valueOf(authorizationLimitDetail.getId());
	}

	@Override
	public void update(AuthorizationLimitDetail authorizationLimitDetail, TableType tableType) {
		logger.debug(Literal.ENTERING);

		// Prepare the SQL.
		StringBuilder sql = new StringBuilder("update Auth_Limit_Details");
		sql.append(tableType.getSuffix());
		sql.append("  set authLimitId = :authLimitId, code = :code, limitAmount = :limitAmount, ");
		sql.append(" LastMntOn = :LastMntOn, RecordStatus = :RecordStatus, RoleCode = :RoleCode,");
		sql.append(" NextRoleCode = :NextRoleCode, TaskId = :TaskId, NextTaskId = :NextTaskId,");
		sql.append(" RecordType = :RecordType, WorkflowId = :WorkflowId");
		sql.append(" where id = :id ");
		sql.append(QueryUtil.getConcurrencyCondition(tableType));

		// Execute the SQL, binding the arguments.
		logger.trace(Literal.SQL + sql.toString());

		SqlParameterSource paramSource = new BeanPropertySqlParameterSource(authorizationLimitDetail);
		int recordCount = jdbcTemplate.update(sql.toString(), paramSource);

		// Check for the concurrency failure.
		if (recordCount == 0) {
			throw new ConcurrencyException();
		}

		logger.debug(Literal.LEAVING);
	}

	@Override
	public void delete(AuthorizationLimitDetail authorizationLimitDetail, TableType tableType) {
		logger.debug(Literal.ENTERING);

		// Prepare the SQL.
		StringBuilder sql = new StringBuilder("delete from Auth_Limit_Details");
		sql.append(tableType.getSuffix());
		sql.append(" where id = :id ");
		sql.append(QueryUtil.getConcurrencyCondition(tableType));

		// Execute the SQL, binding the arguments.
		logger.trace(Literal.SQL + sql.toString());
		SqlParameterSource paramSource = new BeanPropertySqlParameterSource(authorizationLimitDetail);
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
	public List<AuthorizationLimitDetail> getListByAuthLimitId(long authLimitId, String type) {
		logger.debug(Literal.ENTERING);
		List<AuthorizationLimitDetail> limitDetails = new ArrayList<AuthorizationLimitDetail>();
		// Prepare the SQL.
		StringBuilder sql = new StringBuilder("SELECT ");
		sql.append(" id, authLimitId, code, limitAmount, ");

		if (StringUtils.trimToEmpty(type).contains("View")) {
			sql.append(" ProductDesc,CollateralDesc, ");
		}
		sql.append(
				" Version, LastMntOn, LastMntBy,RecordStatus, RoleCode, NextRoleCode, TaskId, NextTaskId, RecordType, WorkflowId");
		sql.append(" From Auth_Limit_Details");
		sql.append(type);
		sql.append(" Where authLimitId = :authLimitId");

		// Execute the SQL, binding the arguments.
		logger.trace(Literal.SQL + sql.toString());

		AuthorizationLimitDetail authorizationLimitDetail = new AuthorizationLimitDetail();
		authorizationLimitDetail.setAuthLimitId(authLimitId);

		SqlParameterSource paramSource = new BeanPropertySqlParameterSource(authorizationLimitDetail);
		RowMapper<AuthorizationLimitDetail> rowMapper = ParameterizedBeanPropertyRowMapper
				.newInstance(AuthorizationLimitDetail.class);

		try {
			limitDetails = jdbcTemplate.query(sql.toString(), paramSource, rowMapper);
		} catch (EmptyResultDataAccessException e) {
			logger.error("Exception: ", e);
			authorizationLimitDetail = null;
		}

		logger.debug(Literal.LEAVING);
		return limitDetails;
	}

	@Override
	public void deleteByAuthLimitId(long authLimitId, TableType tableType) {
		logger.debug(Literal.ENTERING);

		// Prepare the SQL.
		StringBuilder sql = new StringBuilder("delete from Auth_Limit_Details");
		sql.append(tableType.getSuffix());
		sql.append(" where authLimitId = :authLimitId ");
		AuthorizationLimitDetail authorizationLimitDetail = new AuthorizationLimitDetail();
		authorizationLimitDetail.setAuthLimitId(authLimitId);

		// Execute the SQL, binding the arguments.
		logger.trace(Literal.SQL + sql.toString());
		SqlParameterSource paramSource = new BeanPropertySqlParameterSource(authorizationLimitDetail);

		try {
			jdbcTemplate.update(sql.toString(), paramSource);
		} catch (DataAccessException e) {
			throw new DependencyFoundException(e);
		}

		logger.debug(Literal.LEAVING);
	}

}
