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
 * FileName    		:  MandateCheckDigitDAOImpl.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  11-12-2017    														*
 *                                                                  						*
 * Modified Date    :  11-12-2017    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 11-12-2017       PENNANT	                 0.1                                            * 
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

import com.pennant.backend.dao.applicationmaster.MandateCheckDigitDAO;
import com.pennant.backend.dao.impl.BasisCodeDAO;
import com.pennant.backend.model.applicationmaster.MandateCheckDigit;
import com.pennanttech.pennapps.core.ConcurrencyException;
import com.pennanttech.pennapps.core.DependencyFoundException;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pff.core.TableType;
import com.pennanttech.pff.core.util.QueryUtil;

/**
 * Data access layer implementation for <code>MandateCheckDigit</code> with set of CRUD operations.
 */
public class MandateCheckDigitDAOImpl extends BasisCodeDAO<MandateCheckDigit> implements MandateCheckDigitDAO {
	private static Logger				logger	= Logger.getLogger(MandateCheckDigitDAOImpl.class);

	private NamedParameterJdbcTemplate	namedParameterJdbcTemplate;

	public MandateCheckDigitDAOImpl() {
		super();
	}

	@Override
	public MandateCheckDigit getMandateCheckDigit(int checkDigitValue, String type) {
		logger.debug(Literal.ENTERING);

		// Prepare the SQL.
		StringBuilder sql = new StringBuilder("SELECT ");
		sql.append(" checkDigitValue, lookUpValue, active, ");

		sql.append(
				" Version, LastMntOn, LastMntBy,RecordStatus, RoleCode, NextRoleCode, TaskId, NextTaskId, RecordType, WorkflowId");
		sql.append(" From MandateCheckDigits");
		sql.append(StringUtils.trimToEmpty(type));
		sql.append(" Where checkDigitValue = :checkDigitValue ");
		if (!StringUtils.trimToEmpty(type).contains("View")) {
			sql.append("And Active = 1");
		}
		
		// Execute the SQL, binding the arguments.
		logger.trace(Literal.SQL + sql.toString());

		MandateCheckDigit mandateCheckDigit = new MandateCheckDigit();
		mandateCheckDigit.setCheckDigitValue(checkDigitValue);

		SqlParameterSource paramSource = new BeanPropertySqlParameterSource(mandateCheckDigit);
		RowMapper<MandateCheckDigit> rowMapper = ParameterizedBeanPropertyRowMapper
				.newInstance(MandateCheckDigit.class);

		try {
			mandateCheckDigit = namedParameterJdbcTemplate.queryForObject(sql.toString(), paramSource, rowMapper);
		} catch (EmptyResultDataAccessException e) {
			logger.error("Exception: ", e);
			mandateCheckDigit = null;
		}

		logger.debug(Literal.LEAVING);
		return mandateCheckDigit;
	}

	@Override
	public String save(MandateCheckDigit mandateCheckDigit, TableType tableType) {
		logger.debug(Literal.ENTERING);

		// Prepare the SQL.
		StringBuilder sql = new StringBuilder(" insert into MandateCheckDigits");
		sql.append(tableType.getSuffix());
		sql.append(" (checkDigitValue, lookUpValue, active, ");
		sql.append(
				" Version , LastMntBy, LastMntOn, RecordStatus, RoleCode, NextRoleCode, TaskId, NextTaskId, RecordType, WorkflowId)");
		sql.append(" values(");
		sql.append(" :checkDigitValue, :lookUpValue, :active, ");
		sql.append(
				" :Version , :LastMntBy, :LastMntOn, :RecordStatus, :RoleCode, :NextRoleCode, :TaskId, :NextTaskId, :RecordType, :WorkflowId)");

		// Execute the SQL, binding the arguments.
		logger.trace(Literal.SQL + sql.toString());
		SqlParameterSource paramSource = new BeanPropertySqlParameterSource(mandateCheckDigit);

		try {
			namedParameterJdbcTemplate.update(sql.toString(), paramSource);
		} catch (DuplicateKeyException e) {
			throw new ConcurrencyException(e);
		}

		logger.debug(Literal.LEAVING);
		return String.valueOf(mandateCheckDigit.getCheckDigitValue());
	}

	@Override
	public void update(MandateCheckDigit mandateCheckDigit, TableType tableType) {
		logger.debug(Literal.ENTERING);

		// Prepare the SQL.
		StringBuilder sql = new StringBuilder("update MandateCheckDigits");
		sql.append(tableType.getSuffix());
		sql.append("  set lookUpValue = :lookUpValue, active = :active, ");
		sql.append(" LastMntOn = :LastMntOn, RecordStatus = :RecordStatus, RoleCode = :RoleCode,");
		sql.append(" NextRoleCode = :NextRoleCode, TaskId = :TaskId, NextTaskId = :NextTaskId,");
		sql.append(" RecordType = :RecordType, WorkflowId = :WorkflowId");
		sql.append(" where checkDigitValue = :checkDigitValue ");
		sql.append(QueryUtil.getConcurrencyCondition(tableType));

		// Execute the SQL, binding the arguments.
		logger.trace(Literal.SQL + sql.toString());

		SqlParameterSource paramSource = new BeanPropertySqlParameterSource(mandateCheckDigit);
		int recordCount = namedParameterJdbcTemplate.update(sql.toString(), paramSource);

		// Check for the concurrency failure.
		if (recordCount == 0) {
			throw new ConcurrencyException();
		}

		logger.debug(Literal.LEAVING);
	}

	@Override
	public void delete(MandateCheckDigit mandateCheckDigit, TableType tableType) {
		logger.debug(Literal.ENTERING);

		// Prepare the SQL.
		StringBuilder sql = new StringBuilder("delete from MandateCheckDigits");
		sql.append(tableType.getSuffix());
		sql.append(" where checkDigitValue = :checkDigitValue ");
		sql.append(QueryUtil.getConcurrencyCondition(tableType));

		// Execute the SQL, binding the arguments.
		logger.trace(Literal.SQL + sql.toString());
		SqlParameterSource paramSource = new BeanPropertySqlParameterSource(mandateCheckDigit);
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
	public boolean isDuplicateKey(long checkDigitValue, TableType tableType) {
		logger.debug("Entering");
		// Prepare the SQL.
		String sql;
		String whereClause = "CheckDigitValue = :CheckDigitValue";
		switch (tableType) {
		case MAIN_TAB:
			sql = QueryUtil.getCountQuery("MandateCheckDigits", whereClause);
			break;
		case TEMP_TAB:
			sql = QueryUtil.getCountQuery("MandateCheckDigits_Temp", whereClause);
			break;
		default:
			sql = QueryUtil.getCountQuery(new String[] { "MandateCheckDigits_Temp", "MandateCheckDigits" }, whereClause);
			break;
		}

		// Execute the SQL, binding the arguments.
		logger.trace(Literal.SQL + sql);
		MapSqlParameterSource paramSource = new MapSqlParameterSource();
		paramSource.addValue("CheckDigitValue", checkDigitValue);

		Integer count = namedParameterJdbcTemplate.queryForObject(sql, paramSource, Integer.class);

		boolean exists = false;
		if (count > 0) {
			exists = true;
		}

		logger.debug(Literal.LEAVING);
		return exists;
	}
	@Override
	public int getCheckDigit(int checkDigitValue, String lookUpValue, String type) {
		logger.debug("Entering");
		
		MandateCheckDigit mandateCheckDigit = new MandateCheckDigit();
		mandateCheckDigit.setCheckDigitValue(checkDigitValue);
		mandateCheckDigit.setLookUpValue(lookUpValue);

		StringBuilder selectSql = new StringBuilder("SELECT COUNT(*)");
		selectSql.append(" From MandateCheckDigits");
		selectSql.append(StringUtils.trimToEmpty(type));
		selectSql.append(" Where  CheckDigitValue != :CheckDigitValue AND LookUpValue = :LookUpValue " );

		logger.debug("selectSql: " + selectSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(mandateCheckDigit);

		logger.debug("Leaving");

		return this.namedParameterJdbcTemplate.queryForObject(selectSql.toString(), beanParameters, Integer.class);
	}


}
