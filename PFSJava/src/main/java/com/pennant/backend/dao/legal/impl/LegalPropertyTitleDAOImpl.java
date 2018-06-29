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
 * FileName    		:  LegalPropertyTitleDAOImpl.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  18-06-2018    														*
 *                                                                  						*
 * Modified Date    :  18-06-2018    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 18-06-2018       PENNANT	                 0.1                                            * 
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
package com.pennant.backend.dao.legal.impl;

import java.util.List;

import javax.sql.DataSource;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.core.simple.ParameterizedBeanPropertyRowMapper;

import com.pennant.backend.dao.impl.BasisNextidDaoImpl;
import com.pennant.backend.dao.legal.LegalPropertyTitleDAO;
import com.pennant.backend.model.legal.LegalPropertyTitle;
import com.pennanttech.pennapps.core.ConcurrencyException;
import com.pennanttech.pennapps.core.DependencyFoundException;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pff.core.TableType;

/**
 * Data access layer implementation for <code>LegalPropertyTitle</code> with set
 * of CRUD operations.
 */
public class LegalPropertyTitleDAOImpl extends BasisNextidDaoImpl<LegalPropertyTitle> implements LegalPropertyTitleDAO {
	private static Logger logger = Logger.getLogger(LegalPropertyTitleDAOImpl.class);

	private NamedParameterJdbcTemplate namedParameterJdbcTemplate;

	public LegalPropertyTitleDAOImpl() {
		super();
	}

	@Override
	public LegalPropertyTitle getLegalPropertyTitle(long legalPropertyTitleId, String type) {
		logger.debug(Literal.ENTERING);

		// Prepare the SQL.
		StringBuilder sql = new StringBuilder("SELECT ");
		sql.append(" legalPropertyTitleId, legalId, title, ");

		sql.append(
				" Version, LastMntOn, LastMntBy,RecordStatus, RoleCode, NextRoleCode, TaskId, NextTaskId, RecordType, WorkflowId");
		sql.append(" From LegalPropertyTitle");
		sql.append(type);
		sql.append(" Where legalPropertyTitleId = :legalPropertyTitleId");

		// Execute the SQL, binding the arguments.
		logger.trace(Literal.SQL + sql.toString());

		LegalPropertyTitle legalPropertyTitle = new LegalPropertyTitle();
		legalPropertyTitle.setLegalPropertyTitleId(legalPropertyTitleId);

		SqlParameterSource paramSource = new BeanPropertySqlParameterSource(legalPropertyTitle);
		RowMapper<LegalPropertyTitle> rowMapper = ParameterizedBeanPropertyRowMapper
				.newInstance(LegalPropertyTitle.class);

		try {
			legalPropertyTitle = namedParameterJdbcTemplate.queryForObject(sql.toString(), paramSource, rowMapper);
		} catch (EmptyResultDataAccessException e) {
			legalPropertyTitle = null;
		}

		logger.debug(Literal.LEAVING);
		return legalPropertyTitle;
	}
	
	@Override
	public  List<LegalPropertyTitle>  getLegalPropertyTitleList(long legalId, String type) {
		logger.debug(Literal.ENTERING);
		
		// Prepare the SQL.
		StringBuilder sql = new StringBuilder("SELECT ");
		sql.append(" legalPropertyTitleId, legalId, title, ");
		sql.append(" Version, LastMntOn, LastMntBy,RecordStatus, RoleCode, NextRoleCode, TaskId, NextTaskId, RecordType, WorkflowId");
		sql.append(" From LegalPropertyTitle");
		sql.append(type);
		sql.append(" Where legalId = :legalId");
		
		// Execute the SQL, binding the arguments.
		logger.trace(Literal.SQL + sql.toString());
		
		LegalPropertyTitle legalPropertyTitle = new LegalPropertyTitle();
		legalPropertyTitle.setLegalId(legalId);;
		
		SqlParameterSource paramSource = new BeanPropertySqlParameterSource(legalPropertyTitle);
		RowMapper<LegalPropertyTitle> rowMapper = ParameterizedBeanPropertyRowMapper.newInstance(LegalPropertyTitle.class);
		try {
			return this.namedParameterJdbcTemplate.query(sql.toString(), paramSource, rowMapper);
		} catch (Exception e) {
			logger.error(Literal.ENTERING, e);
		} finally {
			sql = null;
		}
		return null;
	}

	@Override
	public String save(LegalPropertyTitle legalPropertyTitle, TableType tableType) {
		logger.debug(Literal.ENTERING);

		// Prepare the SQL.
		StringBuilder sql = new StringBuilder(" insert into LegalPropertyTitle");
		sql.append(tableType.getSuffix());
		sql.append(" (legalPropertyTitleId, legalId, title, ");
		sql.append(
				" Version , LastMntBy, LastMntOn, RecordStatus, RoleCode, NextRoleCode, TaskId, NextTaskId, RecordType, WorkflowId)");
		sql.append(" values(");
		sql.append(" :legalPropertyTitleId, :legalId, :title, ");
		sql.append(
				" :Version , :LastMntBy, :LastMntOn, :RecordStatus, :RoleCode, :NextRoleCode, :TaskId, :NextTaskId, :RecordType, :WorkflowId)");

		if (legalPropertyTitle.getId() == Long.MIN_VALUE) {
			legalPropertyTitle.setId(getNextidviewDAO().getNextId("SeqLegalPropertyTitle"));
			logger.debug("get NextID:" + legalPropertyTitle.getId());
		}

		// Execute the SQL, binding the arguments.
		logger.trace(Literal.SQL + sql.toString());
		SqlParameterSource paramSource = new BeanPropertySqlParameterSource(legalPropertyTitle);

		try {
			namedParameterJdbcTemplate.update(sql.toString(), paramSource);
		} catch (DuplicateKeyException e) {
			throw new ConcurrencyException(e);
		}

		logger.debug(Literal.LEAVING);
		return String.valueOf(legalPropertyTitle.getLegalPropertyTitleId());
	}

	@Override
	public void update(LegalPropertyTitle legalPropertyTitle, TableType tableType) {
		logger.debug(Literal.ENTERING);
		// Prepare the SQL.
		StringBuilder sql = new StringBuilder("update LegalPropertyTitle");
		sql.append(tableType.getSuffix());
		sql.append(" set legalId = :legalId, title = :title, ");
		sql.append(" LastMntOn = :LastMntOn, RecordStatus = :RecordStatus, RoleCode = :RoleCode,");
		sql.append(" NextRoleCode = :NextRoleCode, TaskId = :TaskId, NextTaskId = :NextTaskId,");
		sql.append(" RecordType = :RecordType, WorkflowId = :WorkflowId");
		sql.append(" where legalPropertyTitleId = :legalPropertyTitleId ");

		// Execute the SQL, binding the arguments.
		logger.trace(Literal.SQL + sql.toString());

		SqlParameterSource paramSource = new BeanPropertySqlParameterSource(legalPropertyTitle);
		int recordCount = namedParameterJdbcTemplate.update(sql.toString(), paramSource);

		// Check for the concurrency failure.
		if (recordCount == 0) {
			throw new ConcurrencyException();
		}
		logger.debug(Literal.LEAVING);
	}

	@Override
	public void delete(LegalPropertyTitle legalPropertyTitle, TableType tableType) {
		logger.debug(Literal.ENTERING);

		// Prepare the SQL.
		StringBuilder sql = new StringBuilder("delete from LegalPropertyTitle");
		sql.append(tableType.getSuffix());
		sql.append(" where legalPropertyTitleId = :legalPropertyTitleId ");

		// Execute the SQL, binding the arguments.
		logger.trace(Literal.SQL + sql.toString());
		SqlParameterSource paramSource = new BeanPropertySqlParameterSource(legalPropertyTitle);
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
	public void deleteList(LegalPropertyTitle legalPropertyTitle, String tableType) {
	
		StringBuilder deleteSql = new StringBuilder("Delete From LegalPropertyTitle");
		deleteSql.append(StringUtils.trimToEmpty(tableType));
		deleteSql.append(" Where legalId = :legalId");
		logger.debug("deleteSql: " + deleteSql.toString());

		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(legalPropertyTitle);
		this.namedParameterJdbcTemplate.update(deleteSql.toString(), beanParameters);
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
