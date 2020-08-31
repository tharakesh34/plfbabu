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

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.core.simple.ParameterizedBeanPropertyRowMapper;

import com.pennant.backend.dao.applicationmaster.PinCodeDAO;
import com.pennant.backend.model.applicationmaster.PinCode;
import com.pennanttech.pennapps.core.ConcurrencyException;
import com.pennanttech.pennapps.core.DependencyFoundException;
import com.pennanttech.pennapps.core.jdbc.SequenceDao;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pff.core.TableType;
import com.pennanttech.pff.core.util.QueryUtil;

/**
 * Data access layer implementation for <code>PinCode</code> with set of CRUD operations.
 */
public class PinCodeDAOImpl extends SequenceDao<PinCode> implements PinCodeDAO {
	private static Logger logger = Logger.getLogger(PinCodeDAOImpl.class);

	public PinCodeDAOImpl() {
		super();
	}

	@Override
	public PinCode getPinCode(long pinCodeId, String type) {
		logger.debug(Literal.ENTERING);

		// Prepare the SQL.
		StringBuilder sql = new StringBuilder("SELECT ");
		sql.append(" pinCodeId, pinCode, city, areaName, active, groupId,serviceable,");

		sql.append(
				" Version, LastMntOn, LastMntBy,RecordStatus, RoleCode, NextRoleCode, TaskId, NextTaskId, RecordType, WorkflowId");
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
			pinCode = jdbcTemplate.queryForObject(sql.toString(), paramSource, rowMapper);
		} catch (EmptyResultDataAccessException e) {
			logger.error("Exception: ", e);
			pinCode = null;
		}

		logger.debug(Literal.LEAVING);
		return pinCode;
	}

	@Override
	public boolean isDuplicateKey(long pinCodeId, String city, String areaName, TableType tableType) {
		logger.debug(Literal.ENTERING);

		// Prepare the SQL.
		String sql;
		String whereClause = "City = :City and AreaName = :AreaName AND pinCodeId != :pinCodeId";

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
		paramSource.addValue("City", city);
		paramSource.addValue("AreaName", areaName);

		Integer count = jdbcTemplate.queryForObject(sql, paramSource, Integer.class);

		boolean exists = false;
		if (count > 0) {
			exists = true;
		}

		logger.debug(Literal.LEAVING);
		return exists;
	}

	@Override
	public String save(PinCode pinCode, TableType tableType) {
		logger.debug(Literal.ENTERING);

		// Prepare the SQL.
		StringBuilder sql = new StringBuilder(" insert into PinCodes");
		sql.append(tableType.getSuffix());
		sql.append(" (pinCodeId, pinCode, city, areaName, active,groupId,serviceable, ");
		sql.append(
				" Version , LastMntBy, LastMntOn, RecordStatus, RoleCode, NextRoleCode, TaskId, NextTaskId, RecordType, WorkflowId)");
		sql.append(" values(");
		sql.append(" :pinCodeId, :pinCode, :city, :areaName, :active, :groupId,:serviceable,");
		sql.append(
				" :Version , :LastMntBy, :LastMntOn, :RecordStatus, :RoleCode, :NextRoleCode, :TaskId, :NextTaskId, :RecordType, :WorkflowId)");

		if (pinCode.getPinCodeId() <= 0) {
			pinCode.setPinCodeId(getNextId("SeqPinCodes"));
		}

		// Execute the SQL, binding the arguments.
		logger.trace(Literal.SQL + sql.toString());
		SqlParameterSource paramSource = new BeanPropertySqlParameterSource(pinCode);

		try {
			jdbcTemplate.update(sql.toString(), paramSource);
		} catch (DuplicateKeyException e) {
			throw new ConcurrencyException(e);
		}

		logger.debug(Literal.LEAVING);
		return String.valueOf(pinCode.getPinCodeId());
	}

	@Override
	public void update(PinCode pinCode, TableType tableType) {
		logger.debug(Literal.ENTERING);

		// Prepare the SQL.
		StringBuilder sql = new StringBuilder("update PinCodes");
		sql.append(tableType.getSuffix());
		sql.append(
				"  set pinCode = :pinCode, city = :city, areaName= :areaName, active = :active,groupId=:groupId,serviceable=:serviceable, ");
		sql.append(" LastMntOn = :LastMntOn, RecordStatus = :RecordStatus, RoleCode = :RoleCode,");
		sql.append(" NextRoleCode = :NextRoleCode, TaskId = :TaskId, NextTaskId = :NextTaskId,");
		sql.append(" RecordType = :RecordType, WorkflowId = :WorkflowId");
		sql.append(" where pinCodeId = :pinCodeId ");
		sql.append(QueryUtil.getConcurrencyCondition(tableType));

		// Execute the SQL, binding the arguments.
		logger.trace(Literal.SQL + sql.toString());

		SqlParameterSource paramSource = new BeanPropertySqlParameterSource(pinCode);
		int recordCount = jdbcTemplate.update(sql.toString(), paramSource);

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
	public boolean isCityCodeExists(String pcCity) {
		logger.debug("Entering");
		MapSqlParameterSource source = new MapSqlParameterSource();
		source.addValue("City", pcCity);

		StringBuilder selectSql = new StringBuilder("SELECT COUNT(City)");
		selectSql.append(" From PinCodes_View ");
		selectSql.append(" Where City=:City");

		logger.debug("selectSql: " + selectSql.toString());
		int rcdCount = this.jdbcTemplate.queryForObject(selectSql.toString(), source, Integer.class);

		logger.debug("Leaving");
		return rcdCount > 0 ? true : false;
	}

	@Override
	public PinCode getPinCode(String code, String type) {

		logger.debug(Literal.ENTERING);

		// Prepare the SQL.
		StringBuilder sql = new StringBuilder("SELECT ");
		sql.append(" pinCodeId, pinCode, city, areaName, active,groupId, serviceable,");

		if (StringUtils.trimToEmpty(type).contains("View")) {
			sql.append("pCCountry,pCProvince,");
		}
		sql.append(
				" Version, LastMntOn, LastMntBy,RecordStatus, RoleCode, NextRoleCode, TaskId, NextTaskId, RecordType, WorkflowId");
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
			pinCode = jdbcTemplate.queryForObject(sql.toString(), paramSource, rowMapper);
		} catch (EmptyResultDataAccessException e) {
			logger.error("Exception: ", e);
			pinCode = null;
		}

		logger.debug(Literal.LEAVING);
		return pinCode;
	}

	@Override
	public int getPinCodeCount(String pinCode, String type) {
		logger.debug(Literal.ENTERING);

		StringBuilder sql = new StringBuilder("Select");
		sql.append(" count(pinCode)");
		sql.append(" From PinCodes");
		sql.append(StringUtils.trimToEmpty(type));
		sql.append(" Where PinCode = :PinCode");

		logger.trace(Literal.SQL + sql.toString());

		MapSqlParameterSource source = new MapSqlParameterSource();
		source.addValue("PinCode", pinCode);

		logger.debug(Literal.LEAVING);

		return this.jdbcTemplate.queryForObject(sql.toString(), source, Integer.class);
	}

	@Override
	public PinCode getPinCodeById(long pinCodeId, String type) {
		logger.debug(Literal.ENTERING);

		StringBuilder sql = new StringBuilder("Select");
		sql.append(" PinCodeId, PinCode, City, AreaName");
		sql.append(", Active, GroupId, Serviceable");

		if (StringUtils.trimToEmpty(type).contains("View")) {
			sql.append(", PCCountry, PCProvince");
		}

		sql.append(", Version, LastMntOn, LastMntBy,RecordStatus, RoleCode");
		sql.append(", NextRoleCode, TaskId, NextTaskId, RecordType, WorkflowId");
		sql.append(" From PinCodes");
		sql.append(StringUtils.trimToEmpty(type));
		sql.append(" Where PinCodeId = :PinCodeId");

		logger.trace(Literal.SQL + sql.toString());

		PinCode pinCode = new PinCode();
		pinCode.setPinCodeId(pinCodeId);

		SqlParameterSource paramSource = new BeanPropertySqlParameterSource(pinCode);
		RowMapper<PinCode> rowMapper = ParameterizedBeanPropertyRowMapper.newInstance(PinCode.class);

		try {
			return jdbcTemplate.queryForObject(sql.toString(), paramSource, rowMapper);
		} catch (EmptyResultDataAccessException e) {
			logger.error(Literal.EXCEPTION, e);
		}

		logger.debug(Literal.LEAVING);
		return null;
	}

}
