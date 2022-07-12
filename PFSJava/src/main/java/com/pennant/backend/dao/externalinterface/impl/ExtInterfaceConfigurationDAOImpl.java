/**
 * Copyright 2011 - Pennant Technologies
 * 
 * This file is part of Pennant Java Application Framework and related Products. All
 * components/modules/functions/classes/logic in this software, unless otherwise stated, the property of Pennant
 * Technologies.
 * 
 * Copyright and other intellectual property laws protect these materials. Reproduction or retransmission of the
 * materials, in whole or in part, in any manner, without the prior written consent of the copyright holder, is a
 * violation of copyright law.
 */

/**
 ********************************************************************************************
 * FILE HEADER *
 ********************************************************************************************
 * * FileName : ExtInterfaceConfigurationDAOImpl.java * * Author : PENNANT TECHONOLOGIES * * Creation Date : 10-08-2019
 * * * Modified Date : 10-08-2019 * * Description : * *
 ********************************************************************************************
 * Date Author Version Comments *
 ********************************************************************************************
 * 10-08-2019 PENNANT 0.1 * * * * * * * * *
 ********************************************************************************************
 */
package com.pennant.backend.dao.externalinterface.impl;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;

import com.pennant.backend.dao.externalinterface.ExtInterfaceConfigurationDAO;
import com.pennant.backend.model.externalinterface.InterfaceConfiguration;
import com.pennanttech.pennapps.core.ConcurrencyException;
import com.pennanttech.pennapps.core.DependencyFoundException;
import com.pennanttech.pennapps.core.jdbc.SequenceDao;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pff.core.TableType;
import com.pennanttech.pff.core.util.QueryUtil;

/**
 * Data access layer implementation for <code>InterfaceConfiguration</code> with set of CRUD operations.
 */
public class ExtInterfaceConfigurationDAOImpl extends SequenceDao<InterfaceConfiguration>
		implements ExtInterfaceConfigurationDAO {
	private static Logger logger = LogManager.getLogger(ExtInterfaceConfigurationDAOImpl.class);

	public ExtInterfaceConfigurationDAOImpl() {
		super();
	}

	@Override
	public InterfaceConfiguration getExtInterfaceConfiguration(long id, String type) {
		logger.debug(Literal.ENTERING);

		// Prepare the SQL.
		StringBuilder sql = new StringBuilder("SELECT ");
		sql.append(" id, code, description, type, notificationType, errorCodes, ");
		sql.append(" active, contactsDetail, ");

		sql.append(
				" Version, LastMntOn, LastMntBy,RecordStatus, RoleCode, NextRoleCode, TaskId, NextTaskId, RecordType, WorkflowId,EodDate");
		sql.append(" From EXTINTERFACECONF");
		sql.append(type);
		sql.append(" Where id = :id");

		// Execute the SQL, binding the arguments.
		logger.trace(Literal.SQL + sql.toString());

		InterfaceConfiguration InterfaceConfiguration = new InterfaceConfiguration();
		InterfaceConfiguration.setId(id);

		SqlParameterSource paramSource = new BeanPropertySqlParameterSource(InterfaceConfiguration);
		RowMapper<InterfaceConfiguration> rowMapper = BeanPropertyRowMapper.newInstance(InterfaceConfiguration.class);

		try {
			InterfaceConfiguration = jdbcTemplate.queryForObject(sql.toString(), paramSource, rowMapper);
		} catch (Exception e) {
			logger.error("Exception: ", e);
			InterfaceConfiguration = null;
		}

		logger.debug(Literal.LEAVING);
		return InterfaceConfiguration;
	}

	@Override
	public boolean isDuplicateKey(long id, String code, TableType tableType) {
		logger.debug(Literal.ENTERING);

		// Prepare the SQL.
		String sql;
		String whereClause = "code = :code AND id != :id";

		switch (tableType) {
		case MAIN_TAB:
			sql = QueryUtil.getCountQuery("EXTINTERFACECONF", whereClause);
			break;
		case TEMP_TAB:
			sql = QueryUtil.getCountQuery("EXTINTERFACECONF_Temp", whereClause);
			break;
		default:
			sql = QueryUtil.getCountQuery(new String[] { "EXTINTERFACECONF_Temp", "EXTINTERFACECONF" }, whereClause);
			break;
		}

		// Execute the SQL, binding the arguments.
		logger.trace(Literal.SQL + sql);
		MapSqlParameterSource paramSource = new MapSqlParameterSource();
		paramSource.addValue("id", id);
		paramSource.addValue("code", code);

		Integer count = jdbcTemplate.queryForObject(sql, paramSource, Integer.class);

		boolean exists = false;
		if (count > 0) {
			exists = true;
		}

		logger.debug(Literal.LEAVING);
		return exists;
	}

	@Override
	public String save(InterfaceConfiguration interfaceConfiguration, TableType tableType) {
		logger.debug(Literal.ENTERING);

		// Prepare the SQL.
		StringBuilder sql = new StringBuilder(" Insert into EXTINTERFACECONF");
		sql.append(tableType.getSuffix());
		sql.append("(id, code, description, type, notificationType, errorCodes, ");
		sql.append(" active, contactsDetail, Version , LastMntBy, LastMntOn ");
		sql.append(" ,RecordStatus, RoleCode, NextRoleCode, TaskId, NextTaskId, RecordType, WorkflowId, EodDate)");
		sql.append(" values(");
		sql.append(" :id, :code, :description, :type, :notificationType, :errorCodes, ");
		sql.append(" :active, :contactsDetail, :Version , :LastMntBy, :LastMntOn,:RecordStatus, :RoleCode, ");
		sql.append(" :NextRoleCode, :TaskId, :NextTaskId, :RecordType, :WorkflowId, :EodDate)");

		if (interfaceConfiguration.getId() == Long.MIN_VALUE) {
			interfaceConfiguration.setId(getNextValue("SeqExtInterfaceConf"));
			logger.debug("get NextID:" + interfaceConfiguration.getId());
		}

		// Execute the SQL, binding the arguments.
		logger.trace(Literal.SQL + sql.toString());
		SqlParameterSource paramSource = new BeanPropertySqlParameterSource(interfaceConfiguration);

		try {
			jdbcTemplate.update(sql.toString(), paramSource);
		} catch (DuplicateKeyException e) {
			throw new ConcurrencyException(e);
		}

		logger.debug(Literal.LEAVING);
		return String.valueOf(interfaceConfiguration.getId());
	}

	@Override
	public void update(InterfaceConfiguration interfaceConfiguration, TableType tableType) {
		logger.debug(Literal.ENTERING);

		// Prepare the SQL.
		StringBuilder sql = new StringBuilder("update EXTINTERFACECONF");
		sql.append(tableType.getSuffix());
		sql.append(" set code = :code, description = :description, type = :type, ");
		sql.append(" notificationType = :notificationType, errorCodes = :errorCodes, active = :active,  ");
		sql.append(" contactsDetail = :contactsDetail, LastMntOn = :LastMntOn, RecordStatus = :RecordStatus, ");
		sql.append(" RoleCode = :RoleCode, NextRoleCode = :NextRoleCode, TaskId = :TaskId, NextTaskId = :NextTaskId,");
		sql.append(" RecordType = :RecordType, WorkflowId = :WorkflowId, EodDate =:EodDate");
		sql.append(" where id = :id ");
		sql.append(QueryUtil.getConcurrencyCondition(tableType));

		// Execute the SQL, binding the arguments.
		logger.trace(Literal.SQL + sql.toString());

		SqlParameterSource paramSource = new BeanPropertySqlParameterSource(interfaceConfiguration);
		int recordCount = jdbcTemplate.update(sql.toString(), paramSource);

		// Check for the concurrency failure.
		if (recordCount == 0) {
			throw new ConcurrencyException();
		}

		logger.debug(Literal.LEAVING);
	}

	@Override
	public void delete(InterfaceConfiguration InterfaceConfiguration, TableType tableType) {
		logger.debug(Literal.ENTERING);

		// Prepare the SQL.
		StringBuilder sql = new StringBuilder(" Delete from EXTINTERFACECONF");
		sql.append(tableType.getSuffix());
		sql.append(" where id = :id ");
		sql.append(QueryUtil.getConcurrencyCondition(tableType));

		// Execute the SQL, binding the arguments.
		logger.trace(Literal.SQL + sql.toString());
		SqlParameterSource paramSource = new BeanPropertySqlParameterSource(InterfaceConfiguration);
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

}
