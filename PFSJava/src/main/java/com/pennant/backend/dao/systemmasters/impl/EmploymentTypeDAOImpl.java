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
 * FileName    		:  EmploymentTypeDAOImpl.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  03-05-2011    														*
 *                                                                  						*
 * Modified Date    :  03-05-2011    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 03-05-2011       Pennant	                 0.1                                            * 
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

import com.pennant.backend.dao.impl.BasisCodeDAO;
import com.pennant.backend.dao.systemmasters.EmploymentTypeDAO;
import com.pennant.backend.model.systemmasters.EmploymentType;
import com.pennanttech.pennapps.core.ConcurrencyException;
import com.pennanttech.pff.core.DependencyFoundException;
import com.pennanttech.pff.core.Literal;
import com.pennanttech.pff.core.TableType;
import com.pennanttech.pff.core.util.QueryUtil;

/**
 * DAO methods implementation for the <b>EmploymentType model</b> class.<br>
 * 
 */
public class EmploymentTypeDAOImpl extends BasisCodeDAO<EmploymentType>
		implements EmploymentTypeDAO {

	private static Logger logger = Logger.getLogger(EmploymentTypeDAOImpl.class);

	// Spring Named JDBC Template
	private NamedParameterJdbcTemplate namedParameterJdbcTemplate;

	public EmploymentTypeDAOImpl() {
		super();
	}
	
	/**
	 * Fetch the Record Employment Type details by key field
	 * 
	 * @param id
	 *            (String)
	 * @param type
	 *            (String) ""/_Temp/_View
	 * @return EmploymentType
	 */
	@Override
	public EmploymentType getEmploymentTypeById(final String id, String type) {
		logger.debug(Literal.ENTERING);
		
		EmploymentType employmentType = new EmploymentType();
		employmentType.setId(id);

		StringBuilder selectSql = new StringBuilder("Select EmpType, EmpTypeDesc, EmpTypeIsActive," );
		selectSql.append(" Version , LastMntBy, LastMntOn, RecordStatus, RoleCode, NextRoleCode,");
		selectSql.append(" TaskId, NextTaskId, RecordType, WorkflowId");
		selectSql.append(" From RMTEmpTypes" );
		selectSql.append(StringUtils.trimToEmpty(type));
		selectSql.append(" Where EmpType =:EmpType");

		logger.debug("selectSql: " + selectSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(
				employmentType);
		RowMapper<EmploymentType> typeRowMapper = ParameterizedBeanPropertyRowMapper
				.newInstance(EmploymentType.class);

		try {
			employmentType = this.namedParameterJdbcTemplate.queryForObject(
					selectSql.toString(), beanParameters, typeRowMapper);
		} catch (EmptyResultDataAccessException e) {
			logger.error("Exception: ", e);
			employmentType = null;
		}
		
		logger.debug(Literal.LEAVING);
		return employmentType;
	}
	
	@Override
	public boolean isDuplicateKey(String empType, TableType tableType) {
		logger.debug(Literal.ENTERING);

		// Prepare the SQL.
		String sql;
		String whereClause = "EmpType = :empType";

		switch (tableType) {
		case MAIN_TAB:
			sql = QueryUtil.getCountQuery("RMTEmpTypes", whereClause);
			break;
		case TEMP_TAB:
			sql = QueryUtil.getCountQuery("RMTEmpTypes_Temp", whereClause);
			break;
		default:
			sql = QueryUtil.getCountQuery(new String[] { "RMTEmpTypes_Temp", "RMTEmpTypes" }, whereClause);
			break;
		}

		// Execute the SQL, binding the arguments.
		logger.trace(Literal.SQL + sql);
		MapSqlParameterSource paramSource = new MapSqlParameterSource();
		paramSource.addValue("empType", empType);

		Integer count = namedParameterJdbcTemplate.queryForObject(sql, paramSource, Integer.class);

		boolean exists = false;
		if (count > 0) {
			exists = true;
		}

		logger.debug(Literal.LEAVING);
		return exists;
	}
	
	@Override
	public String save(EmploymentType employmentType, TableType tableType) {
		logger.debug(Literal.ENTERING);

		// Prepare the SQL.
		StringBuilder sql = new StringBuilder("insert into RMTEmpTypes");
		sql.append(tableType.getSuffix());
		sql.append(" (EmpType, EmpTypeDesc, EmpTypeIsActive,");
		sql.append(" Version , LastMntBy, LastMntOn, RecordStatus, RoleCode, NextRoleCode,");
		sql.append(" TaskId, NextTaskId, RecordType, WorkflowId)");
		sql.append(" values(:EmpType, :EmpTypeDesc, :EmpTypeIsActive,");
		sql.append(" :Version , :LastMntBy, :LastMntOn, :RecordStatus, :RoleCode,");
		sql.append(" :NextRoleCode, :TaskId, :NextTaskId, :RecordType, :WorkflowId)");

		// Execute the SQL, binding the arguments.
		logger.trace(Literal.SQL + sql.toString());
		SqlParameterSource paramSource = new BeanPropertySqlParameterSource(employmentType);

		try {
			namedParameterJdbcTemplate.update(sql.toString(), paramSource);
		} catch (DuplicateKeyException e) {
			throw new ConcurrencyException(e);
		}
		
		logger.debug(Literal.LEAVING);
		return employmentType.getId();
	}
	
	@Override
	public void update(EmploymentType employmentType, TableType tableType) {
		logger.debug(Literal.ENTERING);

		// Prepare the SQL, ensure primary key will not be updated.
		StringBuilder sql = new StringBuilder("update RMTEmpTypes");
		sql.append(tableType.getSuffix());
		sql.append(" set EmpTypeDesc = :EmpTypeDesc, EmpTypeIsActive = :EmpTypeIsActive,");
		sql.append(" Version = :Version , LastMntBy = :LastMntBy, LastMntOn = :LastMntOn,");
		sql.append(" RecordStatus= :RecordStatus, RoleCode = :RoleCode,");
		sql.append(" NextRoleCode = :NextRoleCode, TaskId = :TaskId, NextTaskId = :NextTaskId,");
		sql.append(" RecordType = :RecordType, WorkflowId = :WorkflowId");
		sql.append(" where EmpType =:EmpType");
		sql.append(QueryUtil.getConcurrencyCondition(tableType));

		// Execute the SQL, binding the arguments.
		logger.trace(Literal.SQL + sql.toString());
		SqlParameterSource paramSource = new BeanPropertySqlParameterSource(employmentType);
		int recordCount = namedParameterJdbcTemplate.update(sql.toString(), paramSource);
		
		// Check for the concurrency failure.
		if (recordCount == 0) {
			throw new ConcurrencyException();
		}
		
		logger.debug(Literal.LEAVING);
	}
	
	@Override
	public void delete(EmploymentType employmentType, TableType tableType) {
		logger.debug(Literal.ENTERING);
		
		// Prepare the SQL.
		StringBuilder sql = new StringBuilder(" delete from RMTEmpTypes");
		sql.append(tableType.getSuffix()); 
		sql.append(" where EmpType =:EmpType");
		sql.append(QueryUtil.getConcurrencyCondition(tableType));
		
		// Execute the SQL, binding the arguments.
		logger.trace(Literal.SQL +  sql.toString());
		SqlParameterSource paramSource = new BeanPropertySqlParameterSource(employmentType);
		int recordCount = 0;
		
		try {
			recordCount = namedParameterJdbcTemplate.update(sql.toString(),paramSource);
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
	 * @param dataSource
	 *            the dataSource to set
	 */
	public void setDataSource(DataSource dataSource) {
		this.namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
	}
}