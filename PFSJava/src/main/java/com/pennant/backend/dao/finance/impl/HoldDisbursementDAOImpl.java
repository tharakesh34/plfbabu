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
 * FileName    		:  HoldDisbursementDAOImpl.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  09-10-2018    														*
 *                                                                  						*
 * Modified Date    :  09-10-2018    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 09-10-2018       PENNANT	                 0.1                                            * 
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

import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;

import com.pennant.backend.dao.finance.HoldDisbursementDAO;
import com.pennant.backend.model.finance.HoldDisbursement;
import com.pennanttech.pennapps.core.ConcurrencyException;
import com.pennanttech.pennapps.core.DependencyFoundException;
import com.pennanttech.pennapps.core.jdbc.BasicDao;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pff.core.TableType;
import com.pennanttech.pff.core.util.QueryUtil;

/**
 * Data access layer implementation for <code>HoldDisbursement</code> with set of CRUD operations.
 */
public class HoldDisbursementDAOImpl extends BasicDao<HoldDisbursement> implements HoldDisbursementDAO {
	private static Logger logger = LogManager.getLogger(HoldDisbursementDAOImpl.class);

	public HoldDisbursementDAOImpl() {
		super();
	}

	@Override
	public HoldDisbursement getHoldDisbursement(String finReference, String type) {
		logger.debug(Literal.ENTERING);

		// Prepare the SQL.
		StringBuilder sql = new StringBuilder("SELECT ");
		sql.append(" finReference, hold, totalLoanAmt, disbursedAmount, holdLimitAmount, remarks, ");
		sql.append(
				" Version, LastMntOn, LastMntBy,RecordStatus, RoleCode, NextRoleCode, TaskId, NextTaskId, RecordType, WorkflowId");
		sql.append(" From HoldDisbursement");
		sql.append(type);
		sql.append("  where finReference = :finReference");

		// Execute the SQL, binding the arguments.
		logger.trace(Literal.SQL + sql.toString());

		HoldDisbursement holdDisbursement = new HoldDisbursement();
		holdDisbursement.setFinReference(finReference);

		SqlParameterSource paramSource = new BeanPropertySqlParameterSource(holdDisbursement);
		RowMapper<HoldDisbursement> rowMapper = BeanPropertyRowMapper.newInstance(HoldDisbursement.class);

		try {
			holdDisbursement = jdbcTemplate.queryForObject(sql.toString(), paramSource, rowMapper);
		} catch (EmptyResultDataAccessException e) {
			logger.error("Exception: ", e);
			holdDisbursement = null;
		}

		logger.debug(Literal.LEAVING);
		return holdDisbursement;
	}

	@Override
	public String save(HoldDisbursement holdDisbursement, TableType tableType) {
		logger.debug(Literal.ENTERING);

		// Prepare the SQL.
		StringBuilder sql = new StringBuilder(" insert into HoldDisbursement");
		sql.append(tableType.getSuffix());
		sql.append("(finReference, hold, totalLoanAmt, disbursedAmount, holdLimitAmount, remarks, ");
		sql.append(
				" Version , LastMntBy, LastMntOn, RecordStatus, RoleCode, NextRoleCode, TaskId, NextTaskId, RecordType, WorkflowId)");
		sql.append(" values(");
		sql.append(" :finReference, :hold, :totalLoanAmt, :disbursedAmount, :holdLimitAmount, :remarks, ");
		sql.append(
				" :Version , :LastMntBy, :LastMntOn, :RecordStatus, :RoleCode, :NextRoleCode, :TaskId, :NextTaskId, :RecordType, :WorkflowId)");

		// Execute the SQL, binding the arguments.
		logger.trace(Literal.SQL + sql.toString());
		SqlParameterSource paramSource = new BeanPropertySqlParameterSource(holdDisbursement);

		try {
			jdbcTemplate.update(sql.toString(), paramSource);
		} catch (DuplicateKeyException e) {
			throw new ConcurrencyException(e);
		}

		logger.debug(Literal.LEAVING);
		return String.valueOf(holdDisbursement.getFinReference());
	}

	@Override
	public void update(HoldDisbursement holdDisbursement, TableType tableType) {
		logger.debug(Literal.ENTERING);

		// Prepare the SQL.
		StringBuilder sql = new StringBuilder("update HoldDisbursement");
		sql.append(tableType.getSuffix());
		sql.append("  set hold = :hold, totalLoanAmt = :totalLoanAmt, disbursedAmount = :disbursedAmount, ");
		sql.append(" holdLimitAmount = :holdLimitAmount, remarks = :remarks, ");
		sql.append(" LastMntOn = :LastMntOn, RecordStatus = :RecordStatus, RoleCode = :RoleCode,");
		sql.append(" NextRoleCode = :NextRoleCode, TaskId = :TaskId, NextTaskId = :NextTaskId,");
		sql.append(" RecordType = :RecordType, WorkflowId = :WorkflowId");
		sql.append(" where finReference = :finReference ");
		sql.append(QueryUtil.getConcurrencyCondition(tableType));

		// Execute the SQL, binding the arguments.
		logger.trace(Literal.SQL + sql.toString());

		SqlParameterSource paramSource = new BeanPropertySqlParameterSource(holdDisbursement);
		int recordCount = jdbcTemplate.update(sql.toString(), paramSource);

		// Check for the concurrency failure.
		if (recordCount == 0) {
			throw new ConcurrencyException();
		}

		logger.debug(Literal.LEAVING);
	}

	@Override
	public void delete(HoldDisbursement holdDisbursement, TableType tableType) {
		logger.debug(Literal.ENTERING);

		// Prepare the SQL.
		StringBuilder sql = new StringBuilder("delete from HoldDisbursement");
		sql.append(tableType.getSuffix());
		sql.append(" where finReference = :finReference ");
		sql.append(QueryUtil.getConcurrencyCondition(tableType));

		// Execute the SQL, binding the arguments.
		logger.trace(Literal.SQL + sql.toString());
		SqlParameterSource paramSource = new BeanPropertySqlParameterSource(holdDisbursement);
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
	public boolean isDuplicateKey(String finReference, TableType tableType) {

		// Prepare the SQL.
		String sql;
		String whereClause = "finReference = :finReference";

		switch (tableType) {
		case MAIN_TAB:
			sql = QueryUtil.getCountQuery("HOLDDISBURSEMENT", whereClause);
			break;
		case TEMP_TAB:
			sql = QueryUtil.getCountQuery("HOLDDISBURSEMENT_TEMP", whereClause);
			break;
		default:
			sql = QueryUtil.getCountQuery(new String[] { "HOLDDISBURSEMENT", "HOLDDISBURSEMENT_TEMP" }, whereClause);
			break;
		}

		// Execute the SQL, binding the arguments.
		logger.trace(Literal.SQL + sql);
		MapSqlParameterSource paramSource = new MapSqlParameterSource();
		paramSource.addValue("finReference", finReference);

		Integer count = jdbcTemplate.queryForObject(sql, paramSource, Integer.class);

		boolean exists = false;
		if (count > 0) {
			exists = true;
		}

		logger.debug(Literal.LEAVING);
		return exists;
	}

	@Override
	public boolean isholdDisbursementProcess(String finReference, String type) {

		logger.debug("Entering");

		int count = 0;
		MapSqlParameterSource mapSqlParameterSource = new MapSqlParameterSource();
		mapSqlParameterSource.addValue("FinReference", finReference);
		mapSqlParameterSource.addValue("Hold", true);
		StringBuilder selectSql = new StringBuilder("SELECT COUNT(FinReference) FROM HOLDDISBURSEMENT");
		selectSql.append(StringUtils.trimToEmpty(type));
		selectSql.append(" Where FinReference = :FinReference and Hold =:Hold");

		logger.debug("selectSql: " + selectSql.toString());
		try {
			count = this.jdbcTemplate.queryForObject(selectSql.toString(), mapSqlParameterSource, Integer.class);
		} catch (EmptyResultDataAccessException e) {
			logger.warn("Exception: ", e);
			count = 0;
		}

		logger.debug("Leaving");

		return count > 0 ? true : false;
	}

}
