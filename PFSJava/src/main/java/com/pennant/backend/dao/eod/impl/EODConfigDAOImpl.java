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
 * FileName    		:  EODConfigDAOImpl.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  24-05-2017    														*
 *                                                                  						*
 * Modified Date    :  24-05-2017    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 24-05-2017       PENNANT	                 0.1                                            * 
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
package com.pennant.backend.dao.eod.impl;

import javax.sql.DataSource;

import org.apache.log4j.Logger;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.core.simple.ParameterizedBeanPropertyRowMapper;

import com.pennant.backend.dao.eod.EODConfigDAO;
import com.pennant.backend.dao.impl.BasisNextidDaoImpl;
import com.pennant.backend.model.eod.EODConfig;
import com.pennanttech.pff.core.ConcurrencyException;
import com.pennanttech.pff.core.DependencyFoundException;
import com.pennanttech.pff.core.Literal;
import com.pennanttech.pff.core.TableType;
import com.pennanttech.pff.core.util.QueryUtil;

/**
 * Data access layer implementation for <code>EODConfig</code> with set of CRUD operations.
 */
public class EODConfigDAOImpl extends BasisNextidDaoImpl<EODConfig> implements EODConfigDAO {
	private static Logger				logger	= Logger.getLogger(EODConfigDAOImpl.class);

	private NamedParameterJdbcTemplate	namedParameterJdbcTemplate;

	public EODConfigDAOImpl() {
		super();
	}
	
	@Override
	public EODConfig getEODConfig(long eodConfigId,String type) {
		logger.debug(Literal.ENTERING);
		
		// Prepare the SQL.
		StringBuilder sql = new StringBuilder("SELECT ");
		sql.append(" eodConfigId, extMnthRequired, mnthExtTo, active, ");
		
		sql.append(" Version, LastMntOn, LastMntBy,RecordStatus, RoleCode, NextRoleCode, TaskId, NextTaskId, RecordType, WorkflowId" );
		sql.append(" From EodConfig");
		sql.append(type);
		sql.append(" Where eodConfigId = :eodConfigId");
		
		// Execute the SQL, binding the arguments.
		logger.trace(Literal.SQL + sql.toString());

		EODConfig eODConfig = new EODConfig();
		eODConfig.setEodConfigId(eodConfigId);

		SqlParameterSource paramSource = new BeanPropertySqlParameterSource(eODConfig);
		RowMapper<EODConfig> rowMapper = ParameterizedBeanPropertyRowMapper.newInstance(EODConfig.class);

		try {
			eODConfig = namedParameterJdbcTemplate.queryForObject(sql.toString(), paramSource, rowMapper);
		} catch (EmptyResultDataAccessException e) {
			logger.error("Exception: ", e);
			eODConfig = null;
		}

		logger.debug(Literal.LEAVING);
		return eODConfig;
	}		
	
	@Override
	public String save(EODConfig eODConfig,TableType tableType) {
		logger.debug(Literal.ENTERING);
		
		// Prepare the SQL.
		StringBuilder sql =new StringBuilder(" insert into EodConfig");
		sql.append(tableType.getSuffix());
		sql.append(" (eodConfigId, extMnthRequired, mnthExtTo, active, ");
		sql.append(" Version , LastMntBy, LastMntOn, RecordStatus, RoleCode, NextRoleCode, TaskId, NextTaskId, RecordType, WorkflowId)" );
		sql.append(" values(");
		sql.append(" :eodConfigId, :extMnthRequired, :mnthExtTo, :active, ");
		sql.append(" :Version , :LastMntBy, :LastMntOn, :RecordStatus, :RoleCode, :NextRoleCode, :TaskId, :NextTaskId, :RecordType, :WorkflowId)");
		
		if (eODConfig.getEodConfigId() <= 0) {
			eODConfig.setEodConfigId(getNextidviewDAO().getNextId("SeqEodConfig"));
		}
		
		// Execute the SQL, binding the arguments.
		logger.trace(Literal.SQL + sql.toString());
		SqlParameterSource paramSource = new BeanPropertySqlParameterSource(eODConfig);

		try {
			namedParameterJdbcTemplate.update(sql.toString(), paramSource);
		} catch (DuplicateKeyException e) {
			throw new ConcurrencyException(e);
		}

		logger.debug(Literal.LEAVING);
		return String.valueOf(eODConfig.getEodConfigId());
	}	

	@Override
	public void update(EODConfig eODConfig,TableType tableType) {
		logger.debug(Literal.ENTERING);
		
		// Prepare the SQL.
		StringBuilder	sql =new StringBuilder("update EodConfig" );
		sql.append(tableType.getSuffix());
		sql.append("  set extMnthRequired = :extMnthRequired, mnthExtTo = :mnthExtTo, active = :active, ");
		sql.append(" LastMntOn = :LastMntOn, RecordStatus = :RecordStatus, RoleCode = :RoleCode,");
		sql.append(" NextRoleCode = :NextRoleCode, TaskId = :TaskId, NextTaskId = :NextTaskId,");
		sql.append(" RecordType = :RecordType, WorkflowId = :WorkflowId");
		sql.append(" where eodConfigId = :eodConfigId ");
		sql.append(QueryUtil.getConcurrencyCondition(tableType));
	
		// Execute the SQL, binding the arguments.
		logger.trace(Literal.SQL + sql.toString());
		
		SqlParameterSource paramSource = new BeanPropertySqlParameterSource(eODConfig);
		int recordCount = namedParameterJdbcTemplate.update(sql.toString(), paramSource);

		// Check for the concurrency failure.
		if (recordCount == 0) {
			throw new ConcurrencyException();
		}
		
		logger.debug(Literal.LEAVING);
	}

	@Override
	public void delete(EODConfig eODConfig, TableType tableType) {
		logger.debug(Literal.ENTERING);

		// Prepare the SQL.
		StringBuilder sql = new StringBuilder("delete from EodConfig");
		sql.append(tableType.getSuffix());
		sql.append(" where eodConfigId = :eodConfigId ");
		sql.append(QueryUtil.getConcurrencyCondition(tableType));

		// Execute the SQL, binding the arguments.
		logger.trace(Literal.SQL + sql.toString());
		SqlParameterSource paramSource = new BeanPropertySqlParameterSource(eODConfig);
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
