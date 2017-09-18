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
 * FileName    		:  BounceReasonDAOImpl.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  22-04-2017    														*
 *                                                                  						*
 * Modified Date    :  22-04-2017    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 22-04-2017       PENNANT	                 0.1                                            * 
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

import com.pennant.backend.dao.applicationmaster.BounceReasonDAO;
import com.pennant.backend.dao.impl.BasisNextidDaoImpl;
import com.pennant.backend.model.applicationmaster.BounceReason;
import com.pennanttech.pennapps.core.ConcurrencyException;
import com.pennanttech.pennapps.core.DependencyFoundException;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pff.core.TableType;
import com.pennanttech.pff.core.util.QueryUtil;

/**
 * Data access layer implementation for <code>BounceReason</code> with set of CRUD operations.
 */
public class BounceReasonDAOImpl extends BasisNextidDaoImpl<BounceReason> implements BounceReasonDAO {
	private static Logger				logger	= Logger.getLogger(BounceReasonDAOImpl.class);

	private NamedParameterJdbcTemplate	namedParameterJdbcTemplate;

	public BounceReasonDAOImpl() {
		super();
	}
	
	@Override
	public BounceReason getBounceReason(long bounceID,String type) {
		logger.debug(Literal.ENTERING);
		
		// Prepare the SQL.
		StringBuilder sql = new StringBuilder("SELECT ");
		sql.append(" bounceID, bounceCode, reasonType, category, reason, action, ");
		sql.append(" ruleID, returnCode, active, ");
		if (type.contains("View")) {
			sql.append(" ruleCode, ruleCodeDesc,");
		}
		sql.append(" Version, LastMntOn, LastMntBy,RecordStatus, RoleCode, NextRoleCode, TaskId, NextTaskId, RecordType, WorkflowId" );
		sql.append(" From BounceReasons");
		sql.append(type);
		sql.append(" Where bounceID = :bounceID");
		
		// Execute the SQL, binding the arguments.
		logger.trace(Literal.SQL + sql.toString());

		BounceReason bounceReason = new BounceReason();
		bounceReason.setBounceID(bounceID);

		SqlParameterSource paramSource = new BeanPropertySqlParameterSource(bounceReason);
		RowMapper<BounceReason> rowMapper = ParameterizedBeanPropertyRowMapper.newInstance(BounceReason.class);

		try {
			bounceReason = namedParameterJdbcTemplate.queryForObject(sql.toString(), paramSource, rowMapper);
		} catch (EmptyResultDataAccessException e) {
			logger.error("Exception: ", e);
			bounceReason = null;
		}

		logger.debug(Literal.LEAVING);
		return bounceReason;
	}		
	
	@Override
	public boolean isDuplicateKey(long bounceID, String bounceCode, TableType tableType) {
		logger.debug(Literal.ENTERING);

		// Prepare the SQL.
		String sql;
		String whereClause = "bounceCode = :bounceCode AND bounceID != :bounceID";

		switch (tableType) {
		case MAIN_TAB:
			sql = QueryUtil.getCountQuery("BounceReasons", whereClause);
			break;
		case TEMP_TAB:
			sql = QueryUtil.getCountQuery("BounceReasons_Temp", whereClause);
			break;
		default:
			sql = QueryUtil.getCountQuery(new String[] { "BounceReasons_Temp", "BounceReasons" }, whereClause);
			break;
		}

		// Execute the SQL, binding the arguments.
		logger.trace(Literal.SQL + sql);
		MapSqlParameterSource paramSource = new MapSqlParameterSource();
		paramSource.addValue("bounceID", bounceID);
		paramSource.addValue("bounceCode", bounceCode);

		Integer count = namedParameterJdbcTemplate.queryForObject(sql, paramSource, Integer.class);

		boolean exists = false;
		if (count > 0) {
			exists = true;
		}

		logger.debug(Literal.LEAVING);

		return exists;
	}
	
	@Override
	public String save(BounceReason bounceReason,TableType tableType) {
		logger.debug(Literal.ENTERING);
		
		// Prepare the SQL.
		StringBuilder sql =new StringBuilder(" insert into BounceReasons");
		sql.append(tableType.getSuffix());
		sql.append("(bounceID, bounceCode, reasonType, category, reason, action, ");
		sql.append(" ruleID, returnCode, active, ");
		sql.append(" Version , LastMntBy, LastMntOn, RecordStatus, RoleCode, NextRoleCode, TaskId, NextTaskId, RecordType, WorkflowId)" );
		sql.append(" values(");
		sql.append(" :bounceID, :bounceCode, :reasonType, :category, :reason, :action, ");
		sql.append(" :ruleID, :returnCode, :active, ");
		sql.append(" :Version , :LastMntBy, :LastMntOn, :RecordStatus, :RoleCode, :NextRoleCode, :TaskId, :NextTaskId, :RecordType, :WorkflowId)");
		
		if (bounceReason.getBounceID() <= 0) {
			bounceReason.setBounceID(getNextidviewDAO().getNextId("SeqBounceReasons"));
		}
		
		// Execute the SQL, binding the arguments.
		logger.trace(Literal.SQL + sql.toString());
		SqlParameterSource paramSource = new BeanPropertySqlParameterSource(bounceReason);

		try {
			namedParameterJdbcTemplate.update(sql.toString(), paramSource);
		} catch (DuplicateKeyException e) {
			throw new ConcurrencyException(e);
		}

		logger.debug(Literal.LEAVING);
		return String.valueOf(bounceReason.getBounceID());
	}	

	@Override
	public void update(BounceReason bounceReason,TableType tableType) {
		logger.debug(Literal.ENTERING);
		
		// Prepare the SQL.
		StringBuilder	sql =new StringBuilder("update BounceReasons" );
		sql.append(tableType.getSuffix());
		sql.append("  set bounceCode = :bounceCode, reasonType = :reasonType, category = :category, ");
		sql.append(" reason = :reason, action = :action, ruleID = :ruleID, ");
		sql.append(" returnCode = :returnCode, active = :active, ");
		sql.append(" LastMntOn = :LastMntOn, RecordStatus = :RecordStatus, RoleCode = :RoleCode,");
		sql.append(" NextRoleCode = :NextRoleCode, TaskId = :TaskId, NextTaskId = :NextTaskId,");
		sql.append(" RecordType = :RecordType, WorkflowId = :WorkflowId");
		sql.append(" where bounceID = :bounceID ");
		sql.append(QueryUtil.getConcurrencyCondition(tableType));
	
		// Execute the SQL, binding the arguments.
		logger.trace(Literal.SQL + sql.toString());
		
		SqlParameterSource paramSource = new BeanPropertySqlParameterSource(bounceReason);
		int recordCount = namedParameterJdbcTemplate.update(sql.toString(), paramSource);

		// Check for the concurrency failure.
		if (recordCount == 0) {
			throw new ConcurrencyException();
		}
		
		logger.debug(Literal.LEAVING);
	}

	@Override
	public void delete(BounceReason bounceReason, TableType tableType) {
		logger.debug(Literal.ENTERING);

		// Prepare the SQL.
		StringBuilder sql = new StringBuilder("delete from BounceReasons");
		sql.append(tableType.getSuffix());
		sql.append(" where bounceID = :bounceID ");
		sql.append(QueryUtil.getConcurrencyCondition(tableType));

		// Execute the SQL, binding the arguments.
		logger.trace(Literal.SQL + sql.toString());
		SqlParameterSource paramSource = new BeanPropertySqlParameterSource(bounceReason);
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
	
	@Override
	public BounceReason getBounceReasonByReturnCode(String returnCode, String type) {
		logger.debug(Literal.ENTERING);

		StringBuilder sql = null;
		MapSqlParameterSource source = null;

		sql = new StringBuilder();
		sql.append(" SELECT bounceID, bounceCode, reasonType, category, reason, action, ");
		sql.append(" ruleID, returnCode, active, ");
		if (type.contains("View")) {
			sql.append(" ruleCode, ruleCodeDesc,");
		}
		sql.append(" Version, LastMntOn, LastMntBy, RecordStatus, RoleCode, NextRoleCode, TaskId, ");
		sql.append(" NextTaskId, RecordType, WorkflowId From BounceReasons");
		sql.append(type);
		sql.append(" Where ReturnCode = :ReturnCode");
		logger.trace(Literal.SQL + sql.toString());

		source = new MapSqlParameterSource();
		source.addValue("ReturnCode", returnCode);

		RowMapper<BounceReason> rowMapper = ParameterizedBeanPropertyRowMapper.newInstance(BounceReason.class);
		try {
			return namedParameterJdbcTemplate.queryForObject(sql.toString(), source, rowMapper);
		} catch (EmptyResultDataAccessException e) {
			logger.error("Exception: ", e);
		} finally {
			source = null;
			sql = null;
		}
		logger.debug(Literal.LEAVING);
		return null;
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
	
	@Override
	public int getBounceReasonByRuleCode(long ruleId, String type) {
		logger.debug("Entering");
		BounceReason bounceReason = new BounceReason();
		bounceReason.setRuleID(ruleId);

		StringBuilder selectSql = new StringBuilder("SELECT COUNT(*)");
		selectSql.append(" From BounceReasons");
		selectSql.append(StringUtils.trimToEmpty(type));
		selectSql.append(" Where RuleID =:RuleID");

		logger.debug("selectSql: " + selectSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(bounceReason);

		logger.debug("Leaving");
		return this.namedParameterJdbcTemplate.queryForObject(selectSql.toString(), beanParameters, Integer.class);
	}

	@Override
	public boolean isDuplicateReturnCode(long bounceID, String returnCode, TableType tableType) {
		logger.debug(Literal.ENTERING);

		// Prepare the SQL.
		String sql;
		String whereClause = "returnCode = :returnCode AND bounceID != :bounceID";

		switch (tableType) {
		case MAIN_TAB:
			sql = QueryUtil.getCountQuery("BounceReasons", whereClause);
			break;
		case TEMP_TAB:
			sql = QueryUtil.getCountQuery("BounceReasons_Temp", whereClause);
			break;
		default:
			sql = QueryUtil.getCountQuery(new String[] { "BounceReasons_Temp", "BounceReasons" }, whereClause);
			break;
		}

		// Execute the SQL, binding the arguments.
		logger.trace(Literal.SQL + sql);
		MapSqlParameterSource paramSource = new MapSqlParameterSource();
		paramSource.addValue("bounceID", bounceID);
		paramSource.addValue("returnCode", returnCode);

		Integer count = namedParameterJdbcTemplate.queryForObject(sql, paramSource, Integer.class);

		boolean exists = false;
		if (count > 0) {
			exists = true;
		}

		logger.debug(Literal.LEAVING);

		return exists;
	}
}	
