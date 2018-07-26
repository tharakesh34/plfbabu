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
 * FileName    		:  SnapShotConditionsDAOImpl.java                                                   * 	  
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

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.core.simple.ParameterizedBeanPropertyRowMapper;

import com.pennant.backend.dao.eodsnapshot.SnapShotConditionsDAO;
import com.pennant.backend.model.eodsnapshot.SnapShotCondition;
import com.pennanttech.pennapps.core.ConcurrencyException;
import com.pennanttech.pennapps.core.DependencyFoundException;
import com.pennanttech.pennapps.core.jdbc.BasicDao;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pff.core.TableType;
import com.pennanttech.pff.core.util.QueryUtil;

/**
 * Data access layer implementation for <code>SnapShotConditions</code> with set of CRUD operations.
 */
public class SnapShotConditionsDAOImpl extends BasicDao<SnapShotCondition> implements SnapShotConditionsDAO {
	private static Logger				logger	= Logger.getLogger(SnapShotConditionsDAOImpl.class);

	

	public SnapShotConditionsDAOImpl() {
		super();
	}
	
	@Override
	public SnapShotCondition getSnapShotConditions(long id, int executionOrder,String type) {
		logger.debug(Literal.ENTERING);
		
		// Prepare the SQL.
		StringBuilder sql = new StringBuilder("SELECT ");
		sql.append(" id, executionOrder, condition, ");
		
		sql.append(" Version, LastMntOn, LastMntBy,RecordStatus, RoleCode, NextRoleCode, TaskId, NextTaskId, RecordType, WorkflowId" );
		sql.append(" From EODSnapShotConditions");
		sql.append(type);
		sql.append(" Where id = :id AND executionOrder = :executionOrder");
		
		// Execute the SQL, binding the arguments.
		logger.trace(Literal.SQL + sql.toString());

		SnapShotCondition snapShotConditions = new SnapShotCondition();
		snapShotConditions.setId(id);
		snapShotConditions.setExecutionOrder(executionOrder);

		SqlParameterSource paramSource = new BeanPropertySqlParameterSource(snapShotConditions);
		RowMapper<SnapShotCondition> rowMapper = ParameterizedBeanPropertyRowMapper.newInstance(SnapShotCondition.class);

		try {
			snapShotConditions = jdbcTemplate.queryForObject(sql.toString(), paramSource, rowMapper);
		} catch (EmptyResultDataAccessException e) {
			logger.error("Exception: ", e);
			snapShotConditions = null;
		}

		logger.debug(Literal.LEAVING);
		return snapShotConditions;
	}		
	
	@Override
	public String save(SnapShotCondition snapShotConditions,TableType tableType) {
		logger.debug(Literal.ENTERING);
		
		// Prepare the SQL.
		StringBuilder sql =new StringBuilder(" insert into EODSnapShotConditions");
		sql.append(tableType.getSuffix());
		sql.append(" (id, executionOrder, condition, ");
		sql.append(" Version , LastMntBy, LastMntOn, RecordStatus, RoleCode, NextRoleCode, TaskId, NextTaskId, RecordType, WorkflowId)" );
		sql.append(" values(");
		sql.append(" :id, :executionOrder, :condition, ");
		sql.append(" :Version , :LastMntBy, :LastMntOn, :RecordStatus, :RoleCode, :NextRoleCode, :TaskId, :NextTaskId, :RecordType, :WorkflowId)");
		
		// Execute the SQL, binding the arguments.
		logger.trace(Literal.SQL + sql.toString());
		SqlParameterSource paramSource = new BeanPropertySqlParameterSource(snapShotConditions);

		try {
			jdbcTemplate.update(sql.toString(), paramSource);
		} catch (DuplicateKeyException e) {
			throw new ConcurrencyException(e);
		}

		logger.debug(Literal.LEAVING);
		return String.valueOf(snapShotConditions.getId());
	}	

	@Override
	public void update(SnapShotCondition snapShotConditions,TableType tableType) {
		logger.debug(Literal.ENTERING);
		
		// Prepare the SQL.
		StringBuilder	sql =new StringBuilder("update EODSnapShotConditions" );
		sql.append(tableType.getSuffix());
		sql.append("  set condition = :condition, ");
		sql.append(" LastMntOn = :LastMntOn, RecordStatus = :RecordStatus, RoleCode = :RoleCode,");
		sql.append(" NextRoleCode = :NextRoleCode, TaskId = :TaskId, NextTaskId = :NextTaskId,");
		sql.append(" RecordType = :RecordType, WorkflowId = :WorkflowId");
		sql.append(" where id = :id AND executionOrder = :executionOrder ");
		sql.append(QueryUtil.getConcurrencyCondition(tableType));
	
		// Execute the SQL, binding the arguments.
		logger.trace(Literal.SQL + sql.toString());
		
		SqlParameterSource paramSource = new BeanPropertySqlParameterSource(snapShotConditions);
		int recordCount = jdbcTemplate.update(sql.toString(), paramSource);

		// Check for the concurrency failure.
		if (recordCount == 0) {
			throw new ConcurrencyException();
		}
		
		logger.debug(Literal.LEAVING);
	}

	@Override
	public void delete(SnapShotCondition snapShotConditions, TableType tableType) {
		logger.debug(Literal.ENTERING);

		// Prepare the SQL.
		StringBuilder sql = new StringBuilder("delete from EODSnapShotConditions");
		sql.append(tableType.getSuffix());
		sql.append(" where id = :id AND executionOrder = :executionOrder ");
		sql.append(QueryUtil.getConcurrencyCondition(tableType));

		// Execute the SQL, binding the arguments.
		logger.trace(Literal.SQL + sql.toString());
		SqlParameterSource paramSource = new BeanPropertySqlParameterSource(snapShotConditions);
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
	public List<SnapShotCondition> getApprovedTabelConditions(long id) {
		logger.debug(Literal.ENTERING);
		List<SnapShotCondition> conditions= new ArrayList<SnapShotCondition>();
		// Prepare the SQL.
		StringBuilder sql = new StringBuilder("SELECT ");
		sql.append(" id, executionOrder, condition ");
		sql.append(" From EODSnapShotConditions");
		sql.append(" Where id = :id ORDER BY executionOrder");
		
		// Execute the SQL, binding the arguments.
		logger.trace(Literal.SQL + sql.toString());

		SnapShotCondition snapShotConditions = new SnapShotCondition();
		snapShotConditions.setId(id);
		
		SqlParameterSource paramSource = new BeanPropertySqlParameterSource(snapShotConditions);
		RowMapper<SnapShotCondition> rowMapper = ParameterizedBeanPropertyRowMapper.newInstance(SnapShotCondition.class);

		try {
			conditions = jdbcTemplate.query(sql.toString(), paramSource, rowMapper);
		} catch (EmptyResultDataAccessException e) {
			logger.error("Exception: ", e);
			snapShotConditions = null;
		}

		logger.debug(Literal.LEAVING);
		return conditions;
	}		

	
}	
