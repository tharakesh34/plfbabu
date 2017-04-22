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
 * FileName    		:  ProfitCenterDAOImpl.java                                             * 	  
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

import com.pennant.backend.dao.impl.BasisNextidDaoImpl;
import com.pennant.backend.dao.applicationmaster.ProfitCenterDAO;
import com.pennant.backend.model.applicationmaster.ProfitCenter;
import com.pennanttech.pff.core.ConcurrencyException;
import com.pennanttech.pff.core.DependencyFoundException;
import com.pennanttech.pff.core.Literal;
import com.pennanttech.pff.core.TableType;
import com.pennanttech.pff.core.util.QueryUtil;

/**
 * Data access layer implementation for <code>ProfitCenter</code> with set of CRUD operations.
 */
public class ProfitCenterDAOImpl extends BasisNextidDaoImpl<ProfitCenter> implements ProfitCenterDAO {
	private static Logger				logger	= Logger.getLogger(ProfitCenterDAOImpl.class);

	private NamedParameterJdbcTemplate	namedParameterJdbcTemplate;

	public ProfitCenterDAOImpl() {
		super();
	}
	
	@Override
	public ProfitCenter getProfitCenter(long profitCenterID,String type) {
		logger.debug(Literal.ENTERING);
		
		// Prepare the SQL.
		StringBuilder sql = new StringBuilder("SELECT ");
		sql.append(" profitCenterID, profitCenterCode, profitCenterDesc, active, ");
		
		sql.append(" Version, LastMntOn, LastMntBy,RecordStatus, RoleCode, NextRoleCode, TaskId, NextTaskId, RecordType, WorkflowId" );
		sql.append(" From ProfitCenters");
		sql.append(type);
		sql.append(" Where profitCenterID = :profitCenterID");
		
		// Execute the SQL, binding the arguments.
		logger.trace(Literal.SQL + sql.toString());

		ProfitCenter profitCenter = new ProfitCenter();
		profitCenter.setProfitCenterID(profitCenterID);

		SqlParameterSource paramSource = new BeanPropertySqlParameterSource(profitCenter);
		RowMapper<ProfitCenter> rowMapper = ParameterizedBeanPropertyRowMapper.newInstance(ProfitCenter.class);

		try {
			profitCenter = namedParameterJdbcTemplate.queryForObject(sql.toString(), paramSource, rowMapper);
		} catch (EmptyResultDataAccessException e) {
			logger.error("Exception: ", e);
			profitCenter = null;
		}

		logger.debug(Literal.LEAVING);
		return profitCenter;
	}		
	
	@Override
	public boolean isDuplicateKey(long profitCenterID,String profitCenterCode, TableType tableType) {
		logger.debug(Literal.ENTERING);

		// Prepare the SQL.
		String sql;
		String whereClause = "profitCenterCode = :profitCenterCode AND profitCenterID != :profitCenterID";

		switch (tableType) {
		case MAIN_TAB:
			sql = QueryUtil.getCountQuery("ProfitCenters", whereClause);
			break;
		case TEMP_TAB:
			sql = QueryUtil.getCountQuery("ProfitCenters_Temp", whereClause);
			break;
		default:
			sql = QueryUtil.getCountQuery(new String[] { "ProfitCenters_Temp", "ProfitCenters" }, whereClause);
			break;
		}

		// Execute the SQL, binding the arguments.
		logger.trace(Literal.SQL + sql);
		MapSqlParameterSource paramSource = new MapSqlParameterSource();
		paramSource.addValue("profitCenterID", profitCenterID);
		paramSource.addValue("profitCenterCode", profitCenterCode);
		
		Integer count = namedParameterJdbcTemplate.queryForObject(sql, paramSource, Integer.class);

		boolean exists = false;
		if (count > 0) {
			exists = true;
		}

		logger.debug(Literal.LEAVING);
		return exists;
	}
	
	@Override
	public String save(ProfitCenter profitCenter,TableType tableType) {
		logger.debug(Literal.ENTERING);
		
		// Prepare the SQL.
		StringBuilder sql =new StringBuilder(" insert into ProfitCenters");
		sql.append(tableType.getSuffix());
		sql.append(" (profitCenterID, profitCenterCode, profitCenterDesc, active, ");
		sql.append(" Version , LastMntBy, LastMntOn, RecordStatus, RoleCode, NextRoleCode, TaskId, NextTaskId, RecordType, WorkflowId)" );
		sql.append(" values(");
		sql.append(" :profitCenterID, :profitCenterCode, :profitCenterDesc, :active, ");
		sql.append(" :Version , :LastMntBy, :LastMntOn, :RecordStatus, :RoleCode, :NextRoleCode, :TaskId, :NextTaskId, :RecordType, :WorkflowId)");
		
		// Execute the SQL, binding the arguments.
		logger.trace(Literal.SQL + sql.toString());
		SqlParameterSource paramSource = new BeanPropertySqlParameterSource(profitCenter);

		try {
			namedParameterJdbcTemplate.update(sql.toString(), paramSource);
		} catch (DuplicateKeyException e) {
			throw new ConcurrencyException(e);
		}

		logger.debug(Literal.LEAVING);
		return String.valueOf(profitCenter.getProfitCenterID());
	}	

	@Override
	public void update(ProfitCenter profitCenter,TableType tableType) {
		logger.debug(Literal.ENTERING);
		
		// Prepare the SQL.
		StringBuilder	sql =new StringBuilder("update ProfitCenters" );
		sql.append(tableType.getSuffix());
		sql.append("  set profitCenterCode = :profitCenterCode, profitCenterDesc = :profitCenterDesc, active = :active, ");
		sql.append(" LastMntOn = :LastMntOn, RecordStatus = :RecordStatus, RoleCode = :RoleCode,");
		sql.append(" NextRoleCode = :NextRoleCode, TaskId = :TaskId, NextTaskId = :NextTaskId,");
		sql.append(" RecordType = :RecordType, WorkflowId = :WorkflowId");
		sql.append(" where profitCenterID = :profitCenterID ");
		sql.append(QueryUtil.getConcurrencyCondition(tableType));
	
		// Execute the SQL, binding the arguments.
		logger.trace(Literal.SQL + sql.toString());
		
		SqlParameterSource paramSource = new BeanPropertySqlParameterSource(profitCenter);
		int recordCount = namedParameterJdbcTemplate.update(sql.toString(), paramSource);

		// Check for the concurrency failure.
		if (recordCount == 0) {
			throw new ConcurrencyException();
		}
		
		logger.debug(Literal.LEAVING);
	}

	@Override
	public void delete(ProfitCenter profitCenter, TableType tableType) {
		logger.debug(Literal.ENTERING);

		// Prepare the SQL.
		StringBuilder sql = new StringBuilder("delete from ProfitCenters");
		sql.append(tableType.getSuffix());
		sql.append(" where profitCenterID = :profitCenterID ");
		sql.append(QueryUtil.getConcurrencyCondition(tableType));

		// Execute the SQL, binding the arguments.
		logger.trace(Literal.SQL + sql.toString());
		SqlParameterSource paramSource = new BeanPropertySqlParameterSource(profitCenter);
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
