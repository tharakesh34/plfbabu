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
 * FileName    		:  BuilderProjcetDAOImpl.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  22-05-2017    														*
 *                                                                  						*
 * Modified Date    :  22-05-2017    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 22-05-2017       PENNANT	                 0.1                                            * 
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
package com.pennant.backend.dao.systemmasters.impl;

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
import com.pennant.backend.dao.systemmasters.BuilderProjcetDAO;
import com.pennant.backend.model.systemmasters.BuilderProjcet;
import com.pennanttech.pennapps.core.ConcurrencyException;
import com.pennanttech.pennapps.core.DependencyFoundException;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pff.core.TableType;
import com.pennanttech.pff.core.util.QueryUtil;

/**
 * Data access layer implementation for <code>BuilderProjcet</code> with set of CRUD operations.
 */
public class BuilderProjcetDAOImpl extends BasisNextidDaoImpl<BuilderProjcet> implements BuilderProjcetDAO {
	private static Logger				logger	= Logger.getLogger(BuilderProjcetDAOImpl.class);

	private NamedParameterJdbcTemplate	namedParameterJdbcTemplate;

	public BuilderProjcetDAOImpl() {
		super();
	}
	
	@Override
	public BuilderProjcet getBuilderProjcet(long id,String type) {
		logger.debug(Literal.ENTERING);
		
		// Prepare the SQL.
		StringBuilder sql = new StringBuilder("SELECT ");
		sql.append(" id, name, builderId, apfNo, ");
		if(type.contains("View")){
			sql.append("builderIdName,");
		}	
		sql.append(" Version, LastMntOn, LastMntBy,RecordStatus, RoleCode, NextRoleCode, TaskId, NextTaskId, RecordType, WorkflowId" );
		sql.append(" From BuilderProjcet");
		sql.append(type);
		sql.append(" Where id = :id");
		
		
		// Execute the SQL, binding the arguments.
		logger.trace(Literal.SQL + sql.toString());

		BuilderProjcet builderProjcet = new BuilderProjcet();
		builderProjcet.setId(id);

		SqlParameterSource paramSource = new BeanPropertySqlParameterSource(builderProjcet);
		RowMapper<BuilderProjcet> rowMapper = ParameterizedBeanPropertyRowMapper.newInstance(BuilderProjcet.class);

		try {
			builderProjcet = namedParameterJdbcTemplate.queryForObject(sql.toString(), paramSource, rowMapper);
		} catch (EmptyResultDataAccessException e) {
			logger.error("Exception: ", e);
			builderProjcet = null;
		}

		logger.debug(Literal.LEAVING);
		return builderProjcet;
	}		
	
	@Override
	public String save(BuilderProjcet builderProjcet,TableType tableType) {
		logger.debug(Literal.ENTERING);
		
		if (builderProjcet.getId() == Long.MIN_VALUE) {
			builderProjcet.setId(getNextId("SeqBuilderProjcet"));
		}
		// Prepare the SQL.
		StringBuilder sql =new StringBuilder(" insert into BuilderProjcet");
		sql.append(tableType.getSuffix());
		sql.append(" (id, name, builderId, apfNo, ");
		sql.append(" Version , LastMntBy, LastMntOn, RecordStatus, RoleCode, NextRoleCode, TaskId, NextTaskId, RecordType, WorkflowId)" );
		sql.append(" values(");
		sql.append(" :id, :name, :builderId, :apfNo, ");
		sql.append(" :Version , :LastMntBy, :LastMntOn, :RecordStatus, :RoleCode, :NextRoleCode, :TaskId, :NextTaskId, :RecordType, :WorkflowId)");
		
		/*// Get the identity sequence number.
		if (builderProjcet.getId() <= 0) {
			builderProjcet.setId(getNextidviewDAO().getNextId("SeqBuilderProjcet"));
		}*/
		// Execute the SQL, binding the arguments.
		logger.trace(Literal.SQL + sql.toString());
		SqlParameterSource paramSource = new BeanPropertySqlParameterSource(builderProjcet);

		try {
			namedParameterJdbcTemplate.update(sql.toString(), paramSource);
		} catch (DuplicateKeyException e) {
			throw new ConcurrencyException(e);
		}

		logger.debug(Literal.LEAVING);
		return String.valueOf(builderProjcet.getId());
	}	

	@Override
	public void update(BuilderProjcet builderProjcet,TableType tableType) {
		logger.debug(Literal.ENTERING);
		
		// Prepare the SQL.
		StringBuilder	sql =new StringBuilder("update BuilderProjcet" );
		sql.append(tableType.getSuffix());
		sql.append("  set name = :name, builderId = :builderId, apfNo = :apfNo, ");
		sql.append(" LastMntOn = :LastMntOn, RecordStatus = :RecordStatus, RoleCode = :RoleCode,");
		sql.append(" NextRoleCode = :NextRoleCode, TaskId = :TaskId, NextTaskId = :NextTaskId,");
		sql.append(" RecordType = :RecordType, WorkflowId = :WorkflowId");
		sql.append(" where id = :id ");
		sql.append(QueryUtil.getConcurrencyCondition(tableType));
	
		// Execute the SQL, binding the arguments.
		logger.trace(Literal.SQL + sql.toString());
		
		SqlParameterSource paramSource = new BeanPropertySqlParameterSource(builderProjcet);
		int recordCount = namedParameterJdbcTemplate.update(sql.toString(), paramSource);

		// Check for the concurrency failure.
		if (recordCount == 0) {
			throw new ConcurrencyException();
		}
		
		logger.debug(Literal.LEAVING);
	}

	@Override
	public void delete(BuilderProjcet builderProjcet, TableType tableType) {
		logger.debug(Literal.ENTERING);

		// Prepare the SQL.
		StringBuilder sql = new StringBuilder("delete from BuilderProjcet");
		sql.append(tableType.getSuffix());
		sql.append(" where id = :id ");
		sql.append(QueryUtil.getConcurrencyCondition(tableType));

		// Execute the SQL, binding the arguments.
		logger.trace(Literal.SQL + sql.toString());
		SqlParameterSource paramSource = new BeanPropertySqlParameterSource(builderProjcet);
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

	@Override
	public boolean isDuplicateKey(long id, String name, long builderId, TableType tableType) {

		// Prepare the SQL.
		String sql;
		String whereClause = "name = :name AND builderId = :builderId AND id != :id";

		switch (tableType) {
		case MAIN_TAB:
			sql = QueryUtil.getCountQuery("BuilderProjcet", whereClause);
			break;
		case TEMP_TAB:
			sql = QueryUtil.getCountQuery("BuilderProjcet_Temp", whereClause);
			break;
		default:
			sql = QueryUtil.getCountQuery(new String[] { "BuilderProjcet_Temp", "BuilderProjcet" }, whereClause);
			break;
		}

		// Execute the SQL, binding the arguments.
		logger.trace(Literal.SQL + sql);
		MapSqlParameterSource paramSource = new MapSqlParameterSource();
		paramSource.addValue("id", id);
		paramSource.addValue("name", name);
		paramSource.addValue("builderId", builderId);
		
		Integer count = namedParameterJdbcTemplate.queryForObject(sql, paramSource, Integer.class);

		boolean exists = false;
		if (count > 0) {
			exists = true;
		}

		logger.debug(Literal.LEAVING);
		return exists;
	}
	
}	