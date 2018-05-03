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
 * FileName    		:  AuthorizationLimitDAOImpl.java                                       * 	  
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

import javax.sql.DataSource;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.core.simple.ParameterizedBeanPropertyRowMapper;

import com.pennant.backend.dao.authorization.AuthorizationLimitDAO;
import com.pennant.backend.dao.impl.BasisNextidDaoImpl;
import com.pennant.backend.model.authorization.AuthorizationLimit;
import com.pennanttech.pennapps.core.ConcurrencyException;
import com.pennanttech.pennapps.core.DependencyFoundException;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pff.core.TableType;
import com.pennanttech.pff.core.util.QueryUtil;

/**
 * Data access layer implementation for <code>AuthorizationLimit</code> with set of CRUD operations.
 */
public class AuthorizationLimitDAOImpl extends BasisNextidDaoImpl<AuthorizationLimit> implements AuthorizationLimitDAO {
	private static Logger				logger	= Logger.getLogger(AuthorizationLimitDAOImpl.class);

	private NamedParameterJdbcTemplate	namedParameterJdbcTemplate;

	public AuthorizationLimitDAOImpl() {
		super();
	}
	
	@Override
	public AuthorizationLimit getAuthorizationLimit(long id,String type) {
		logger.debug(Literal.ENTERING);
		
		// Prepare the SQL.
		StringBuilder sql = new StringBuilder("SELECT ");
		sql.append(" id, limitType, userID, roleId, module, limitAmount, ");
		sql.append(" startDate, expiryDate, holdStartDate, holdExpiryDate, active, ");
		
		if (StringUtils.trimToEmpty(type).contains("View")) {
			sql.append(" usrLogin,UsrFName,UsrMName,UsrLName,roleCd,roleName , " );
		}
	
		sql.append(" Version, LastMntOn, LastMntBy,RecordStatus, RoleCode, NextRoleCode, TaskId, NextTaskId, RecordType, WorkflowId" );
		sql.append(" From Auth_Limits");
		sql.append(type);
		sql.append(" Where id = :id");
		
		// Execute the SQL, binding the arguments.
		logger.trace(Literal.SQL + sql.toString());

		AuthorizationLimit authorizationLimit = new AuthorizationLimit();
		authorizationLimit.setId(id);

		SqlParameterSource paramSource = new BeanPropertySqlParameterSource(authorizationLimit);
		RowMapper<AuthorizationLimit> rowMapper = ParameterizedBeanPropertyRowMapper.newInstance(AuthorizationLimit.class);

		try {
			authorizationLimit = namedParameterJdbcTemplate.queryForObject(sql.toString(), paramSource, rowMapper);
		} catch (EmptyResultDataAccessException e) {
			logger.error("Exception: ", e);
			authorizationLimit = null;
		}

		logger.debug(Literal.LEAVING);
		return authorizationLimit;
	}	
	
	
	@Override
	public AuthorizationLimit getLimitForFinanceAuth(long id,String type, boolean active) {
		logger.debug(Literal.ENTERING);
		
		// Prepare the SQL.
		StringBuilder sql = new StringBuilder("SELECT ");
		sql.append(" id, limitType, userID, roleId, module, limitAmount, ");
		sql.append(" startDate, expiryDate, holdStartDate, holdExpiryDate, active, ");
		
		if (StringUtils.trimToEmpty(type).contains("View")) {
			sql.append(" usrLogin,UsrFName,UsrMName,UsrLName,roleCd,roleName , " );
		}
	
		sql.append(" Version, LastMntOn, LastMntBy,RecordStatus, RoleCode, NextRoleCode, TaskId, NextTaskId, RecordType, WorkflowId" );
		sql.append(" From Auth_Limits");
		sql.append(type);
		sql.append(" Where userID = :id and active = :active");
		
		// Execute the SQL, binding the arguments.
		logger.trace(Literal.SQL + sql.toString());

		AuthorizationLimit authorizationLimit = new AuthorizationLimit();
		authorizationLimit.setId(id);
		authorizationLimit.setActive(active);;

		SqlParameterSource paramSource = new BeanPropertySqlParameterSource(authorizationLimit);
		RowMapper<AuthorizationLimit> rowMapper = ParameterizedBeanPropertyRowMapper.newInstance(AuthorizationLimit.class);

		try {
			authorizationLimit = namedParameterJdbcTemplate.queryForObject(sql.toString(), paramSource, rowMapper);
		} catch (EmptyResultDataAccessException e) {
			logger.error("Exception: ", e);
			authorizationLimit = null;
		}

		logger.debug(Literal.LEAVING);
		return authorizationLimit;
	}	
	
	@Override
	public boolean isDuplicateKey(long id,int limitType,long userID,long roleId,String module, TableType tableType) {
		logger.debug(Literal.ENTERING);

		// Prepare the SQL.
		String sql;
		String whereClause = "limitType = :limitType AND userID = :userID AND roleId = :roleId AND module = :module AND id != :id";

		switch (tableType) {
		case MAIN_TAB:
			sql = QueryUtil.getCountQuery("Auth_Limits", whereClause);
			break;
		case TEMP_TAB:
			sql = QueryUtil.getCountQuery("Auth_Limits_Temp", whereClause);
			break;
		default:
			sql = QueryUtil.getCountQuery(new String[] { "Auth_Limits_Temp", "Auth_Limits" }, whereClause);
			break;
		}

		// Execute the SQL, binding the arguments.
		logger.trace(Literal.SQL + sql);
		MapSqlParameterSource paramSource = new MapSqlParameterSource();
		paramSource.addValue("id", id);
		paramSource.addValue("limitType", limitType);
		paramSource.addValue("userID", userID);
		paramSource.addValue("roleId", roleId);
		paramSource.addValue("module", module);
		
		Integer count = namedParameterJdbcTemplate.queryForObject(sql, paramSource, Integer.class);

		boolean exists = false;
		if (count > 0) {
			exists = true;
		}

		logger.debug(Literal.LEAVING);
		return exists;
	}
	
	@Override
	public String save(AuthorizationLimit authorizationLimit,TableType tableType) {
		logger.debug(Literal.ENTERING);
		
		// Prepare the SQL.
		StringBuilder sql =new StringBuilder(" insert into Auth_Limits");
		sql.append(tableType.getSuffix());
		sql.append("(id, limitType, userID, roleId, module, limitAmount, ");
		sql.append(" startDate, expiryDate, holdStartDate, holdExpiryDate, active, ");
		sql.append(" Version , LastMntBy, LastMntOn, RecordStatus, RoleCode, NextRoleCode, TaskId, NextTaskId, RecordType, WorkflowId)" );
		sql.append(" values(");
		sql.append(" :id, :limitType, :userID, :roleId, :module, :limitAmount, ");
		sql.append(" :startDate, :expiryDate, :holdStartDate, :holdExpiryDate, :active, ");
		sql.append(" :Version , :LastMntBy, :LastMntOn, :RecordStatus, :RoleCode, :NextRoleCode, :TaskId, :NextTaskId, :RecordType, :WorkflowId)");

		if (authorizationLimit.getId()==Long.MIN_VALUE){
			authorizationLimit.setId(getNextidviewDAO().getNextId("SeqAuth_Limits"));
			logger.debug("get NextID:"+authorizationLimit.getId());
		}

		// Execute the SQL, binding the arguments.
		logger.trace(Literal.SQL + sql.toString());
		SqlParameterSource paramSource = new BeanPropertySqlParameterSource(authorizationLimit);

		try {
			namedParameterJdbcTemplate.update(sql.toString(), paramSource);
		} catch (DuplicateKeyException e) {
			throw new ConcurrencyException(e);
		}

		logger.debug(Literal.LEAVING);
		return String.valueOf(authorizationLimit.getId());
	}	

	@Override
	public void saveHold(AuthorizationLimit authorizationLimit) {
		// Prepare the SQL.
		StringBuilder sql =new StringBuilder(" insert into Auth_Limits_HTemp ");
		sql.append("(id, limitType, userID, roleId, module, limitAmount, ");
		sql.append(" startDate, expiryDate, holdStartDate, holdExpiryDate, active, ");
		sql.append(" Version , LastMntBy, LastMntOn, RecordStatus, RoleCode, NextRoleCode, TaskId, NextTaskId, RecordType, WorkflowId)" );
		sql.append(" values(");
		sql.append(" :id, :limitType, :userID, :roleId, :module, :limitAmount, ");
		sql.append(" :startDate, :expiryDate, :holdStartDate, :holdExpiryDate, :active, ");
		sql.append(" :Version , :LastMntBy, :LastMntOn, :RecordStatus, :RoleCode, :NextRoleCode, :TaskId, :NextTaskId, :RecordType, :WorkflowId)");

		// Execute the SQL, binding the arguments.
		logger.trace(Literal.SQL + sql.toString());
		SqlParameterSource paramSource = new BeanPropertySqlParameterSource(authorizationLimit);

		try {
			namedParameterJdbcTemplate.update(sql.toString(), paramSource);
		} catch (DuplicateKeyException e) {
			throw new ConcurrencyException(e);
		}

		logger.debug(Literal.LEAVING);
	}

	
	@Override
	public void update(AuthorizationLimit authorizationLimit,TableType tableType) {
		logger.debug(Literal.ENTERING);
		
		// Prepare the SQL.
		StringBuilder	sql =new StringBuilder("update Auth_Limits" );
		sql.append(tableType.getSuffix());
		sql.append("  set limitType = :limitType, userID = :userID, roleId = :roleId, ");
		sql.append(" module = :module, limitAmount = :limitAmount, startDate = :startDate, ");
		sql.append(" expiryDate = :expiryDate, ");
		sql.append(" active = :active, ");
		sql.append(" LastMntOn = :LastMntOn, RecordStatus = :RecordStatus, RoleCode = :RoleCode,");
		sql.append(" NextRoleCode = :NextRoleCode, TaskId = :TaskId, NextTaskId = :NextTaskId,");
		sql.append(" RecordType = :RecordType, WorkflowId = :WorkflowId");
		sql.append(" where id = :id ");
		sql.append(QueryUtil.getConcurrencyCondition(tableType));
	
		// Execute the SQL, binding the arguments.
		logger.trace(Literal.SQL + sql.toString());
		
		SqlParameterSource paramSource = new BeanPropertySqlParameterSource(authorizationLimit);
		int recordCount = namedParameterJdbcTemplate.update(sql.toString(), paramSource);

		// Check for the concurrency failure.
		if (recordCount == 0) {
			throw new ConcurrencyException();
		}
		
		logger.debug(Literal.LEAVING);
	}

	@Override
	public void updateHold(AuthorizationLimit authorizationLimit,TableType tableType) {
		logger.debug(Literal.ENTERING);
		
		// Prepare the SQL.
		StringBuilder	sql =new StringBuilder("update Auth_Limits" );
		sql.append(tableType.getSuffix());
		sql.append("  set holdStartDate = :holdStartDate, holdExpiryDate = :holdExpiryDate ");
		sql.append(" module = :module, limitAmount = :limitAmount, startDate = :startDate, ");
		sql.append(" LastMntOn = :LastMntOn, RecordStatus = :RecordStatus, RoleCode = :RoleCode,");
		sql.append(" NextRoleCode = :NextRoleCode, TaskId = :TaskId, NextTaskId = :NextTaskId,");
		sql.append(" RecordType = :RecordType, WorkflowId = :WorkflowId");
		sql.append(" where id = :id ");
		sql.append(QueryUtil.getConcurrencyCondition(tableType));
	
		// Execute the SQL, binding the arguments.
		logger.trace(Literal.SQL + sql.toString());
		
		SqlParameterSource paramSource = new BeanPropertySqlParameterSource(authorizationLimit);
		int recordCount = namedParameterJdbcTemplate.update(sql.toString(), paramSource);

		// Check for the concurrency failure.
		if (recordCount == 0) {
			throw new ConcurrencyException();
		}
		
		logger.debug(Literal.LEAVING);
	}
	@Override
	public void delete(AuthorizationLimit authorizationLimit, TableType tableType) {
		logger.debug(Literal.ENTERING);

		// Prepare the SQL.
		StringBuilder sql = new StringBuilder("delete from Auth_Limits");
		sql.append(tableType.getSuffix());
		sql.append(" where id = :id ");
		sql.append(QueryUtil.getConcurrencyCondition(tableType));

		// Execute the SQL, binding the arguments.
		logger.trace(Literal.SQL + sql.toString());
		SqlParameterSource paramSource = new BeanPropertySqlParameterSource(authorizationLimit);
		int recordCount = 0;

		try {
			recordCount = namedParameterJdbcTemplate.update(sql.toString(), paramSource);
		} catch (DataAccessException e) {
			throw new DependencyFoundException(e);
		}

		// Check for the concurrency failure.
		if (recordCount == 0) {
			throw new ConcurrencyException();
		}

		logger.debug(Literal.LEAVING);
	}

	/**
	 * Sets a new <code>JDBC Template</code> for the given data source.
	 * 
	 * @param dataSource
	 *            The JDBC data source to access.
	 */
	public void setDataSource(DataSource dataSource) {
		namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
	}

	
}	
