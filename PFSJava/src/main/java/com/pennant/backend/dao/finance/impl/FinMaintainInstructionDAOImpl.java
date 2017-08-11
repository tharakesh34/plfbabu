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
 * FileName    		:  FeeTypeDAOImpl.java                                                  * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  03-01-2017    														*
 *                                                                  						*
 * Modified Date    :  03-01-2017    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 03-01-2017       PENNANT	                 0.1                                            * 
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

package com.pennant.backend.dao.finance.impl;

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

import com.pennant.backend.dao.finance.FinMaintainInstructionDAO;
import com.pennant.backend.dao.impl.BasisNextidDaoImpl;
import com.pennant.backend.model.finance.FinMaintainInstruction;
import com.pennanttech.pennapps.core.ConcurrencyException;
import com.pennanttech.pennapps.core.DependencyFoundException;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pff.core.TableType;
import com.pennanttech.pff.core.util.QueryUtil;

/**
 * Data access layer implementation for <code>FinMaintainInstruction</code> with
 * set of CRUD operations.
 */

public class FinMaintainInstructionDAOImpl extends BasisNextidDaoImpl<FinMaintainInstruction>
		implements FinMaintainInstructionDAO {

	private static Logger logger = Logger.getLogger(FinMaintainInstructionDAOImpl.class);

	// Spring Named JDBC Template
	private NamedParameterJdbcTemplate namedParameterJdbcTemplate;

	public FinMaintainInstructionDAOImpl() {
		super();
	}

	/**
	 * Fetch the Record FinMaintainInstruction details by key field
	 * 
	 * @param id
	 *            (int)
	 * @param type
	 *            (String) ""/_Temp/_View
	 * @return FinMaintainInstruction
	 */
	@Override
	public FinMaintainInstruction getFinMaintainInstructionById(long finMaintainId, String type) {
		logger.debug("Entering");

		FinMaintainInstruction finMaintainInstruction = new FinMaintainInstruction();
		finMaintainInstruction.setFinMaintainId(finMaintainId);

		StringBuilder selectSql = new StringBuilder();
		selectSql.append(" Select FinMaintainId, FinReference, Event, ");
		selectSql.append(" Version, LastMntOn, LastMntBy,RecordStatus, RoleCode, NextRoleCode, TaskId, NextTaskId, RecordType, WorkflowId");
		if (StringUtils.trimToEmpty(type).contains("View")) {
			selectSql.append("");
		}
		selectSql.append(" From FinMaintainInstructions");
		selectSql.append(StringUtils.trimToEmpty(type));
		selectSql.append(" Where FinMaintainId = :FinMaintainId");

		logger.debug("sql: " + selectSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(finMaintainInstruction);
		RowMapper<FinMaintainInstruction> typeRowMapper = ParameterizedBeanPropertyRowMapper
				.newInstance(FinMaintainInstruction.class);

		try {
			finMaintainInstruction = this.namedParameterJdbcTemplate.queryForObject(selectSql.toString(),
					beanParameters, typeRowMapper);
		} catch (EmptyResultDataAccessException e) {
			logger.warn("Exception: ", e);
			finMaintainInstruction = null;
		}

		logger.debug("Leaving");
		return finMaintainInstruction;
	}

	/**
	 * Fetch the Record FinMaintainInstruction details by finReference and event
	 * 
	 * @param feeTypeCode
	 *            (String)
	 * @return FinMaintainInstruction
	 */
	@Override
	public FinMaintainInstruction getFinMaintainInstructionByFinRef(String finReference, String event, String type) {
		logger.debug("Entering");

		FinMaintainInstruction finMaintainInstruction = new FinMaintainInstruction();
		finMaintainInstruction.setFinReference(finReference);
		finMaintainInstruction.setEvent(event);

		StringBuilder selectSql = new StringBuilder();
		selectSql.append(" Select FinMaintainId, FinReference, Event, ");
		selectSql.append(" Version, LastMntOn, LastMntBy,RecordStatus, RoleCode, NextRoleCode, TaskId, NextTaskId, RecordType, WorkflowId");
		if (StringUtils.trimToEmpty(type).contains("View")) {
			selectSql.append("");
		}
		selectSql.append(" From FinMaintainInstructions");
		selectSql.append(StringUtils.trimToEmpty(type));
		selectSql.append(" Where FinReference = :FinReference AND Event = :Event ");

		logger.debug("sql: " + selectSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(finMaintainInstruction);
		RowMapper<FinMaintainInstruction> typeRowMapper = ParameterizedBeanPropertyRowMapper
				.newInstance(FinMaintainInstruction.class);

		try {
			finMaintainInstruction = this.namedParameterJdbcTemplate.queryForObject(selectSql.toString(),
					beanParameters, typeRowMapper);
		} catch (EmptyResultDataAccessException e) {
			logger.warn("Exception: ", e);
			finMaintainInstruction = null;
		}
		logger.debug("Leaving");
		return finMaintainInstruction;
	}

	@Override
	public boolean isDuplicateKey(String event, String finReference, TableType tableType) {
		logger.debug(Literal.ENTERING);

		// Prepare the SQL.
		String sql;
		String whereClause = "Event = :Event and FinReference = :FinReference";
		switch (tableType) {
		case MAIN_TAB:
			sql = QueryUtil.getCountQuery("FinMaintainInstructions", whereClause);
			break;
		case TEMP_TAB:
			sql = QueryUtil.getCountQuery("FinMaintainInstructions_Temp", whereClause);
			break;
		default:
			sql = QueryUtil.getCountQuery(new String[] { "FinMaintainInstructions_Temp", "FinMaintainInstructions" },
					whereClause);
			break;
		}

		// Execute the SQL, binding the arguments.
		logger.trace(Literal.SQL + sql);
		MapSqlParameterSource paramSource = new MapSqlParameterSource();
		paramSource.addValue("Event", event);
		paramSource.addValue("FinReference", finReference);

		Integer count = namedParameterJdbcTemplate.queryForObject(sql, paramSource, Integer.class);

		boolean exists = false;
		if (count > 0) {
			exists = true;
		}

		logger.debug(Literal.LEAVING);
		return exists;
	}

	@Override
	public String save(FinMaintainInstruction finMaintainInstruction, TableType tableType) {
		logger.debug(Literal.ENTERING);

		// Prepare the SQL.
		StringBuilder sql = new StringBuilder("Insert into FinMaintainInstructions");
		sql.append(tableType.getSuffix());
		sql.append(" (FinMaintainId, FinReference, Event,");
		sql.append(" Version , LastMntBy, LastMntOn, RecordStatus, RoleCode, NextRoleCode, TaskId, NextTaskId, RecordType, WorkflowId)");
		sql.append(" values(:FinMaintainId, :FinReference, :Event,");
		sql.append(" :Version, :LastMntBy, :LastMntOn, :RecordStatus, :RoleCode, :NextRoleCode, :TaskId, :NextTaskId, :RecordType, :WorkflowId)");

		// Get the identity sequence number.
		if (finMaintainInstruction.getFinMaintainId() == Long.MIN_VALUE) {
			finMaintainInstruction.setFinMaintainId(getNextidviewDAO().getNextId("SeqFinMaintainInstructions"));
		}

		// Execute the SQL, binding the arguments.
		logger.trace(Literal.SQL + sql.toString());
		SqlParameterSource paramSource = new BeanPropertySqlParameterSource(finMaintainInstruction);

		try {
			namedParameterJdbcTemplate.update(sql.toString(), paramSource);
		} catch (DuplicateKeyException e) {
			throw new ConcurrencyException(e);
		}

		logger.debug("Leaving");
		return String.valueOf(finMaintainInstruction.getFinMaintainId());
	}

	/**
	 * 
	 */
	@Override
	public void update(FinMaintainInstruction finMaintainInstruction, TableType tableType) {
		logger.debug(Literal.ENTERING);

		StringBuilder sql = new StringBuilder("update FinMaintainInstructions");
		sql.append(tableType.getSuffix());
		sql.append(" set FinMaintainId = :FinMaintainId, FinReference = :FinReference, Event = :Event,");
		sql.append(	" Version= :Version , LastMntBy = :LastMntBy, LastMntOn = :LastMntOn, RecordStatus= :RecordStatus, RoleCode = :RoleCode, NextRoleCode = :NextRoleCode,");
		sql.append(" TaskId = :TaskId, NextTaskId = :NextTaskId, RecordType = :RecordType, WorkflowId = :WorkflowId");

		sql.append(" where FinMaintainId = :FinMaintainId");
		sql.append(QueryUtil.getConcurrencyCondition(tableType));

		// Execute the SQL, binding the arguments.
		logger.trace(Literal.SQL + sql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(finMaintainInstruction);
		int recordCount = namedParameterJdbcTemplate.update(sql.toString(), beanParameters);

		// Check for the concurrency failure.
		if (recordCount == 0) {
			throw new ConcurrencyException();
		}

		logger.debug(Literal.LEAVING);
	}

	/**
	 * 
	 */
	@Override
	public void delete(FinMaintainInstruction finMaintainInstruction, TableType tableType) {
		logger.debug(Literal.ENTERING);

		StringBuilder sql = new StringBuilder("delete From FinMaintainInstructions");
		sql.append(tableType.getSuffix());
		sql.append(" where FinMaintainId = :FinMaintainId");
		sql.append(QueryUtil.getConcurrencyCondition(tableType));

		// Execute the SQL, binding the arguments.
		logger.trace(Literal.SQL + sql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(finMaintainInstruction);
		int recordCount = 0;

		try {
			recordCount = namedParameterJdbcTemplate.update(sql.toString(), beanParameters);
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
	 * To Set dataSource
	 * 
	 * @param dataSource
	 */

	public void setDataSource(DataSource dataSource) {
		this.namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
	}
}