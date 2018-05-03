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
 * FileName    		:  SnapShotConfigurationDAOImpl.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  16-02-2018    														*
 *                                                                  						*
 * Modified Date    :  16-02-2018    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 16-02-2018       PENNANT	                 0.1                                            * 
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
package com.pennant.backend.dao.eodsnapshot.impl;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import javax.sql.DataSource;

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

import com.pennant.backend.dao.eodsnapshot.SnapShotConfigurationDAO;
import com.pennant.backend.dao.impl.BasisNextidDaoImpl;
import com.pennant.backend.model.eodsnapshot.SnapShotConfiguration;
import com.pennanttech.pennapps.core.ConcurrencyException;
import com.pennanttech.pennapps.core.DependencyFoundException;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pff.core.TableType;
import com.pennanttech.pff.core.util.QueryUtil;

/**
 * Data access layer implementation for <code>SnapShotConfiguration</code> with set of CRUD operations.
 */
public class SnapShotConfigurationDAOImpl extends BasisNextidDaoImpl<SnapShotConfiguration> implements SnapShotConfigurationDAO {
	private static Logger				logger	= Logger.getLogger(SnapShotConfigurationDAOImpl.class);

	private NamedParameterJdbcTemplate	namedParameterJdbcTemplate;

	public SnapShotConfigurationDAOImpl() {
		super();
	}
	
	@Override
	public SnapShotConfiguration getSnapShotConfiguration(long id,String type) {
		logger.debug(Literal.ENTERING);
		
		// Prepare the SQL.
		StringBuilder sql = new StringBuilder("SELECT ");
		sql.append(" id, type, fromSchema,fromTable, toTable, executionOrder, executionType, ");
		sql.append(" executionMethod, clearingType ,active, ");
		
		sql.append(" Version, LastMntOn, LastMntBy,RecordStatus, RoleCode, NextRoleCode, TaskId, NextTaskId, RecordType, WorkflowId" );
		sql.append(" From EODSnapShotConfiguration");
		sql.append(type);
		sql.append(" Where id = :id");
		
		// Execute the SQL, binding the arguments.
		logger.trace(Literal.SQL + sql.toString());

		SnapShotConfiguration snapShotConfiguration = new SnapShotConfiguration();
		snapShotConfiguration.setId(id);

		SqlParameterSource paramSource = new BeanPropertySqlParameterSource(snapShotConfiguration);
		RowMapper<SnapShotConfiguration> rowMapper = ParameterizedBeanPropertyRowMapper.newInstance(SnapShotConfiguration.class);

		try {
			snapShotConfiguration = namedParameterJdbcTemplate.queryForObject(sql.toString(), paramSource, rowMapper);
		} catch (EmptyResultDataAccessException e) {
			logger.error("Exception: ", e);
			snapShotConfiguration = null;
		}

		logger.debug(Literal.LEAVING);
		return snapShotConfiguration;
	}		
	
	@Override
	public boolean isDuplicateKey(long id,String fromTable, TableType tableType) {
		logger.debug(Literal.ENTERING);

		// Prepare the SQL.
		String sql;
		String whereClause = "fromTable = :fromTable AND id != :id";

		switch (tableType) {
		case MAIN_TAB:
			sql = QueryUtil.getCountQuery("EODSnapShotConfiguration", whereClause);
			break;
		case TEMP_TAB:
			sql = QueryUtil.getCountQuery("EODSnapShotConfiguration_Temp", whereClause);
			break;
		default:
			sql = QueryUtil.getCountQuery(new String[] { "EODSnapShotConfiguration_Temp", "EODSnapShotConfiguration" }, whereClause);
			break;
		}

		// Execute the SQL, binding the arguments.
		logger.trace(Literal.SQL + sql);
		MapSqlParameterSource paramSource = new MapSqlParameterSource();
		paramSource.addValue("id", id);
		paramSource.addValue("fromTable", fromTable);
		
		Integer count = namedParameterJdbcTemplate.queryForObject(sql, paramSource, Integer.class);

		boolean exists = false;
		if (count > 0) {
			exists = true;
		}

		logger.debug(Literal.LEAVING);
		return exists;
	}
	
	@Override
	public String save(SnapShotConfiguration snapShotConfiguration,TableType tableType) {
		logger.debug(Literal.ENTERING);
		
		// Prepare the SQL.
		StringBuilder sql =new StringBuilder(" insert into EODSnapShotConfiguration");
		sql.append(tableType.getSuffix());
		sql.append("(id, type, fromSchema,fromTable, toTable, executionOrder, executionType, ");
		sql.append(" executionMethod, clearingType, active, ");
		sql.append(" Version , LastMntBy, LastMntOn, RecordStatus, RoleCode, NextRoleCode, TaskId, NextTaskId, RecordType, WorkflowId)" );
		sql.append(" values(");
		sql.append(" :id, :type,:fromSchema, :fromTable, :toTable, :executionOrder, :executionType, ");
		sql.append(" :executionMethod, :clearingType, :active, ");
		sql.append(" :Version , :LastMntBy, :LastMntOn, :RecordStatus, :RoleCode, :NextRoleCode, :TaskId, :NextTaskId, :RecordType, :WorkflowId)");
		
		// Execute the SQL, binding the arguments.
		logger.trace(Literal.SQL + sql.toString());
		SqlParameterSource paramSource = new BeanPropertySqlParameterSource(snapShotConfiguration);

		try {
			namedParameterJdbcTemplate.update(sql.toString(), paramSource);
		} catch (DuplicateKeyException e) {
			throw new ConcurrencyException(e);
		}

		logger.debug(Literal.LEAVING);
		return String.valueOf(snapShotConfiguration.getId());
	}	

	@Override
	public void update(SnapShotConfiguration snapShotConfiguration,TableType tableType) {
		logger.debug(Literal.ENTERING);
		
		// Prepare the SQL.
		StringBuilder	sql =new StringBuilder("update EODSnapShotConfiguration" );
		sql.append(tableType.getSuffix());
		sql.append("  set type = :type, fromSchema=:fromSchema, fromTable = :fromTable, toTable = :toTable, ");
		sql.append(" executionOrder = :executionOrder, executionType = :executionType, executionMethod = :executionMethod, ");
		sql.append(" clearingType=:clearingType, active = :active, ");
		sql.append(" LastMntOn = :LastMntOn, RecordStatus = :RecordStatus, RoleCode = :RoleCode,");
		sql.append(" NextRoleCode = :NextRoleCode, TaskId = :TaskId, NextTaskId = :NextTaskId,");
		sql.append(" RecordType = :RecordType, WorkflowId = :WorkflowId");
		sql.append(" where id = :id ");
		sql.append(QueryUtil.getConcurrencyCondition(tableType));
	
		// Execute the SQL, binding the arguments.
		logger.trace(Literal.SQL + sql.toString());
		
		SqlParameterSource paramSource = new BeanPropertySqlParameterSource(snapShotConfiguration);
		int recordCount = namedParameterJdbcTemplate.update(sql.toString(), paramSource);

		// Check for the concurrency failure.
		if (recordCount == 0) {
			throw new ConcurrencyException();
		}
		
		logger.debug(Literal.LEAVING);
	}

	@Override
	public void delete(SnapShotConfiguration snapShotConfiguration, TableType tableType) {
		logger.debug(Literal.ENTERING);

		// Prepare the SQL.
		StringBuilder sql = new StringBuilder("delete from EODSnapShotConfiguration");
		sql.append(tableType.getSuffix());
		sql.append(" where id = :id ");
		sql.append(QueryUtil.getConcurrencyCondition(tableType));

		// Execute the SQL, binding the arguments.
		logger.trace(Literal.SQL + sql.toString());
		SqlParameterSource paramSource = new BeanPropertySqlParameterSource(snapShotConfiguration);
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
	public List<SnapShotConfiguration> getActiveConfigurationList() {
		logger.debug(Literal.ENTERING);
		
		List<SnapShotConfiguration> configurations= new ArrayList<SnapShotConfiguration>();
		// Prepare the SQL.
		StringBuilder sql = new StringBuilder("SELECT ");
		sql.append(" id, type, fromSchema,fromTable, toTable, executionOrder, executionType, ");
		sql.append(" executionMethod, clearingType, active, ");
		
		sql.append(" Version, LastMntOn, LastMntBy,RecordStatus, RoleCode, NextRoleCode, TaskId, NextTaskId, RecordType, WorkflowId" );
		sql.append(" From EODSnapShotConfiguration");
		sql.append(" Where active=:Active");
		
		// Execute the SQL, binding the arguments.
		logger.trace(Literal.SQL + sql.toString());

		SnapShotConfiguration snapShotConfiguration = new SnapShotConfiguration();
		snapShotConfiguration.setActive(true);

		SqlParameterSource paramSource = new BeanPropertySqlParameterSource(snapShotConfiguration);
		RowMapper<SnapShotConfiguration> rowMapper = ParameterizedBeanPropertyRowMapper.newInstance(SnapShotConfiguration.class);
		try {
			configurations = namedParameterJdbcTemplate.query(sql.toString(), paramSource, rowMapper);
		} catch (EmptyResultDataAccessException e) {
			logger.error("Exception: ", e);
			snapShotConfiguration = null;
		}
		
		logger.debug(Literal.LEAVING);
		return configurations;
	}

	@Override
	public void updateLastRunDate(long id,Timestamp lastRunDate) {
		logger.debug(Literal.ENTERING);
		
		SnapShotConfiguration snapShotConfiguration = new SnapShotConfiguration();
		snapShotConfiguration.setId(id);
		snapShotConfiguration.setLastRunDate(lastRunDate);
		
		// Prepare the SQL.
		StringBuilder	sql =new StringBuilder("update EODSnapShotConfiguration" );
		sql.append("  set lastRunDate = :LastRunDate, ");
		sql.append(" where id = :id ");
		
		// Execute the SQL, binding the arguments.
		logger.trace(Literal.SQL + sql.toString());
		
		SqlParameterSource paramSource = new BeanPropertySqlParameterSource(snapShotConfiguration);
		int recordCount = namedParameterJdbcTemplate.update(sql.toString(), paramSource);

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
