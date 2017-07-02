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
 * FileName    		:  PinCodeDAOImpl.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  01-06-2017    														*
 *                                                                  						*
 * Modified Date    :  01-06-2017    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 01-06-2017       PENNANT	                 0.1                                            * 
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

import com.pennant.backend.dao.applicationmaster.PinCodeDAO;
import com.pennant.backend.dao.impl.BasisNextidDaoImpl;
import com.pennant.backend.model.applicationmaster.PinCode;
import com.pennanttech.pff.core.ConcurrencyException;
import com.pennanttech.pff.core.DependencyFoundException;
import com.pennanttech.pff.core.Literal;
import com.pennanttech.pff.core.TableType;
import com.pennanttech.pff.core.util.QueryUtil;

/**
 * Data access layer implementation for <code>PinCode</code> with set of CRUD operations.
 */
public class PinCodeDAOImpl extends BasisNextidDaoImpl<PinCode> implements PinCodeDAO {
	private static Logger				logger	= Logger.getLogger(PinCodeDAOImpl.class);

	private NamedParameterJdbcTemplate	namedParameterJdbcTemplate;

	public PinCodeDAOImpl() {
		super();
	}
	
	@Override
	public PinCode getPinCode(long pinCodeId,String type) {
		logger.debug(Literal.ENTERING);
		
		// Prepare the SQL.
		StringBuilder sql = new StringBuilder("SELECT ");
		sql.append(" pinCodeId, pinCode, city, areaName, active, ");
		
		sql.append(" Version, LastMntOn, LastMntBy,RecordStatus, RoleCode, NextRoleCode, TaskId, NextTaskId, RecordType, WorkflowId" );
		if (StringUtils.trimToEmpty(type).contains("View")) {
			sql.append(",pCCityName");
		}
		sql.append(" From PinCodes");
		sql.append(type);
		sql.append(" Where pinCodeId = :pinCodeId");
		
		// Execute the SQL, binding the arguments.
		logger.trace(Literal.SQL + sql.toString());

		PinCode pinCode = new PinCode();
		pinCode.setPinCodeId(pinCodeId);

		SqlParameterSource paramSource = new BeanPropertySqlParameterSource(pinCode);
		RowMapper<PinCode> rowMapper = ParameterizedBeanPropertyRowMapper.newInstance(PinCode.class);

		try {
			pinCode = namedParameterJdbcTemplate.queryForObject(sql.toString(), paramSource, rowMapper);
		} catch (EmptyResultDataAccessException e) {
			logger.error("Exception: ", e);
			pinCode = null;
		}

		logger.debug(Literal.LEAVING);
		return pinCode;
	}		
	
	@Override
	public boolean isDuplicateKey(long pinCodeId,String pinCode, TableType tableType) {
		logger.debug(Literal.ENTERING);

		// Prepare the SQL.
		String sql;
		String whereClause = "pinCode = :pinCode AND pinCodeId != :pinCodeId";

		switch (tableType) {
		case MAIN_TAB:
			sql = QueryUtil.getCountQuery("PinCodes", whereClause);
			break;
		case TEMP_TAB:
			sql = QueryUtil.getCountQuery("PinCodes_Temp", whereClause);
			break;
		default:
			sql = QueryUtil.getCountQuery(new String[] { "PinCodes_Temp", "PinCodes" }, whereClause);
			break;
		}

		// Execute the SQL, binding the arguments.
		logger.trace(Literal.SQL + sql);
		MapSqlParameterSource paramSource = new MapSqlParameterSource();
		paramSource.addValue("pinCodeId", pinCodeId);
		paramSource.addValue("pinCode", pinCode);
		
		Integer count = namedParameterJdbcTemplate.queryForObject(sql, paramSource, Integer.class);

		boolean exists = false;
		if (count > 0) {
			exists = true;
		}

		logger.debug(Literal.LEAVING);
		return exists;
	}
	
	@Override
	public String save(PinCode pinCode,TableType tableType) {
		logger.debug(Literal.ENTERING);
		
		// Prepare the SQL.
		StringBuilder sql =new StringBuilder(" insert into PinCodes");
		sql.append(tableType.getSuffix());
		sql.append(" (pinCodeId, pinCode, city, areaName, active, ");
		sql.append(" Version , LastMntBy, LastMntOn, RecordStatus, RoleCode, NextRoleCode, TaskId, NextTaskId, RecordType, WorkflowId)" );
		sql.append(" values(");
		sql.append(" :pinCodeId, :pinCode, :city, :areaName, :active, ");
		sql.append(" :Version , :LastMntBy, :LastMntOn, :RecordStatus, :RoleCode, :NextRoleCode, :TaskId, :NextTaskId, :RecordType, :WorkflowId)");
		
		if (pinCode.getPinCodeId() <= 0) {
			pinCode.setPinCodeId(getNextidviewDAO().getNextId("SeqPinCodes"));
		}
		
		// Execute the SQL, binding the arguments.
		logger.trace(Literal.SQL + sql.toString());
		SqlParameterSource paramSource = new BeanPropertySqlParameterSource(pinCode);

		try {
			namedParameterJdbcTemplate.update(sql.toString(), paramSource);
		} catch (DuplicateKeyException e) {
			throw new ConcurrencyException(e);
		}

		logger.debug(Literal.LEAVING);
		return String.valueOf(pinCode.getPinCodeId());
	}	

	@Override
	public void update(PinCode pinCode,TableType tableType) {
		logger.debug(Literal.ENTERING);
		
		// Prepare the SQL.
		StringBuilder	sql =new StringBuilder("update PinCodes" );
		sql.append(tableType.getSuffix());
		sql.append("  set pinCode = :pinCode, city = :city, areaName= :areaName, active = :active, ");
		sql.append(" LastMntOn = :LastMntOn, RecordStatus = :RecordStatus, RoleCode = :RoleCode,");
		sql.append(" NextRoleCode = :NextRoleCode, TaskId = :TaskId, NextTaskId = :NextTaskId,");
		sql.append(" RecordType = :RecordType, WorkflowId = :WorkflowId");
		sql.append(" where pinCodeId = :pinCodeId ");
		sql.append(QueryUtil.getConcurrencyCondition(tableType));
	
		// Execute the SQL, binding the arguments.
		logger.trace(Literal.SQL + sql.toString());
		
		SqlParameterSource paramSource = new BeanPropertySqlParameterSource(pinCode);
		int recordCount = namedParameterJdbcTemplate.update(sql.toString(), paramSource);

		// Check for the concurrency failure.
		if (recordCount == 0) {
			throw new ConcurrencyException();
		}
		
		logger.debug(Literal.LEAVING);
	}

	@Override
	public void delete(PinCode pinCode, TableType tableType) {
		logger.debug(Literal.ENTERING);

		// Prepare the SQL.
		StringBuilder sql = new StringBuilder("delete from PinCodes");
		sql.append(tableType.getSuffix());
		sql.append(" where pinCodeId = :pinCodeId ");
		sql.append(QueryUtil.getConcurrencyCondition(tableType));

		// Execute the SQL, binding the arguments.
		logger.trace(Literal.SQL + sql.toString());
		SqlParameterSource paramSource = new BeanPropertySqlParameterSource(pinCode);
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
	public boolean isCityCodeExists(String pcCity) {
		logger.debug("Entering");
		MapSqlParameterSource source = new MapSqlParameterSource();
		source.addValue("City", pcCity);
		
		StringBuilder selectSql = new StringBuilder("SELECT COUNT(City)");
		selectSql.append(" From PinCodes_View ");
		selectSql.append(" Where City=:City");
		
		logger.debug("selectSql: " + selectSql.toString());
		int rcdCount =  this.namedParameterJdbcTemplate.queryForObject(selectSql.toString(), source, Integer.class);
		
		logger.debug("Leaving");
		return rcdCount > 0 ? true : false;
	}

	@Override
	public PinCode getPinCode(String code, String type) {

		logger.debug(Literal.ENTERING);

		// Prepare the SQL.
		StringBuilder sql = new StringBuilder("SELECT ");
		sql.append(" pinCodeId, pinCode, city, areaName, active, ");
		
		if (StringUtils.trimToEmpty(type).contains("View")) {
			sql.append("pCCountry,pCProvince,");
		}
		sql.append(" Version, LastMntOn, LastMntBy,RecordStatus, RoleCode, NextRoleCode, TaskId, NextTaskId, RecordType, WorkflowId");
		sql.append(" From PinCodes");
		sql.append(type);

		sql.append(" Where pinCode = :pinCode");

		// Execute the SQL, binding the arguments.
		logger.trace(Literal.SQL + sql.toString());

		PinCode pinCode = new PinCode();
		pinCode.setPinCode(code);

		SqlParameterSource paramSource = new BeanPropertySqlParameterSource(pinCode);
		RowMapper<PinCode> rowMapper = ParameterizedBeanPropertyRowMapper.newInstance(PinCode.class);

		try {
			pinCode = namedParameterJdbcTemplate.queryForObject(sql.toString(), paramSource, rowMapper);
		} catch (EmptyResultDataAccessException e) {
			logger.error("Exception: ", e);
			pinCode = null;
		}

		logger.debug(Literal.LEAVING);
		return pinCode;
	}
	
}	
