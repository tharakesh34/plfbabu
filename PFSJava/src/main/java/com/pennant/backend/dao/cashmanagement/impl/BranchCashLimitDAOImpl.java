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
 * FileName    		:  BranchCashLimitDAOImpl.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  29-01-2018    														*
 *                                                                  						*
 * Modified Date    :  29-01-2018    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 29-01-2018       PENNANT	                 0.1                                            * 
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
package com.pennant.backend.dao.cashmanagement.impl;

import java.util.List;

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

import com.pennant.backend.dao.cashmanagement.BranchCashLimitDAO;
import com.pennant.backend.model.cashmanagement.BranchCashLimit;
import com.pennanttech.pennapps.core.ConcurrencyException;
import com.pennanttech.pennapps.core.DependencyFoundException;
import com.pennanttech.pennapps.core.jdbc.BasicDao;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pff.core.TableType;
import com.pennanttech.pff.core.util.QueryUtil;

/**
 * Data access layer implementation for <code>BranchCashLimit</code> with set of CRUD operations.
 */
public class BranchCashLimitDAOImpl extends BasicDao<BranchCashLimit> implements BranchCashLimitDAO {
	private static Logger logger = LogManager.getLogger(BranchCashLimitDAOImpl.class);

	public BranchCashLimitDAOImpl() {
		super();
	}

	@Override
	public BranchCashLimit getBranchCashLimit(String branchCode, String type) {
		logger.debug(Literal.ENTERING);

		// Prepare the SQL.
		StringBuilder sql = new StringBuilder("SELECT ");
		sql.append(
				" branchCode, curLimitSetDate, reOrderLimit, cashLimit, adHocCashLimit, remarks,previousDate,previousAmount, ");
		if (type.contains("View")) {
			sql.append(
					" BranchCodeName,branchCash, AdhocInitiationAmount, AdhocProcessingAmount, AdhocTransitAmount,AutoProcessingAmount, AutoTransitAmount, ReservedAmount, LastEODDate, ");
		}
		sql.append(
				" Version, LastMntOn, LastMntBy,RecordStatus, RoleCode, NextRoleCode, TaskId, NextTaskId, RecordType, WorkflowId");
		sql.append(" From BranchCashLimit");
		sql.append(type);
		sql.append(" Where branchCode = :branchCode");

		// Execute the SQL, binding the arguments.
		logger.trace(Literal.SQL + sql.toString());

		BranchCashLimit branchCashLimit = new BranchCashLimit();
		branchCashLimit.setBranchCode(branchCode);

		SqlParameterSource paramSource = new BeanPropertySqlParameterSource(branchCashLimit);
		RowMapper<BranchCashLimit> rowMapper = BeanPropertyRowMapper.newInstance(BranchCashLimit.class);

		try {
			branchCashLimit = jdbcTemplate.queryForObject(sql.toString(), paramSource, rowMapper);
		} catch (EmptyResultDataAccessException e) {
			logger.error("Exception: ", e);
			branchCashLimit = null;
		}

		logger.debug(Literal.LEAVING);
		return branchCashLimit;
	}

	@Override
	public String save(BranchCashLimit branchCashLimit, TableType tableType) {
		logger.debug(Literal.ENTERING);

		// Prepare the SQL.
		StringBuilder sql = new StringBuilder(" insert into BranchCashLimit");
		sql.append(tableType.getSuffix());
		sql.append(
				"(branchCode, curLimitSetDate, reOrderLimit, cashLimit, adHocCashLimit, remarks,previousDate,previousAmount, ");
		sql.append(
				" Version , LastMntBy, LastMntOn, RecordStatus, RoleCode, NextRoleCode, TaskId, NextTaskId, RecordType, WorkflowId)");
		sql.append(" values(");
		sql.append(
				" :branchCode, :curLimitSetDate, :reOrderLimit, :cashLimit, :adHocCashLimit, :remarks,:previousDate,:previousAmount, ");
		sql.append(
				" :Version , :LastMntBy, :LastMntOn, :RecordStatus, :RoleCode, :NextRoleCode, :TaskId, :NextTaskId, :RecordType, :WorkflowId)");

		// Execute the SQL, binding the arguments.
		logger.trace(Literal.SQL + sql.toString());
		SqlParameterSource paramSource = new BeanPropertySqlParameterSource(branchCashLimit);

		try {
			jdbcTemplate.update(sql.toString(), paramSource);
		} catch (DuplicateKeyException e) {
			throw new ConcurrencyException(e);
		}

		logger.debug(Literal.LEAVING);
		return String.valueOf(branchCashLimit.getBranchCode());
	}

	@Override
	public void update(BranchCashLimit branchCashLimit, TableType tableType) {
		logger.debug(Literal.ENTERING);

		// Prepare the SQL.
		StringBuilder sql = new StringBuilder("update BranchCashLimit");
		sql.append(tableType.getSuffix());
		sql.append("  set curLimitSetDate = :curLimitSetDate, reOrderLimit = :reOrderLimit, cashLimit = :cashLimit, ");
		sql.append(
				" adHocCashLimit = :adHocCashLimit, remarks = :remarks,previousDate=:previousDate,previousAmount=:previousAmount , ");
		sql.append(" LastMntOn = :LastMntOn, RecordStatus = :RecordStatus, RoleCode = :RoleCode,");
		sql.append(" NextRoleCode = :NextRoleCode, TaskId = :TaskId, NextTaskId = :NextTaskId,");
		sql.append(" RecordType = :RecordType, WorkflowId = :WorkflowId");
		sql.append(" where branchCode = :branchCode ");
		sql.append(QueryUtil.getConcurrencyCondition(tableType));

		// Execute the SQL, binding the arguments.
		logger.trace(Literal.SQL + sql.toString());

		SqlParameterSource paramSource = new BeanPropertySqlParameterSource(branchCashLimit);
		int recordCount = jdbcTemplate.update(sql.toString(), paramSource);

		// Check for the concurrency failure.
		if (recordCount == 0) {
			throw new ConcurrencyException();
		}

		logger.debug(Literal.LEAVING);
	}

	@Override
	public void delete(BranchCashLimit branchCashLimit, TableType tableType) {
		logger.debug(Literal.ENTERING);

		// Prepare the SQL.
		StringBuilder sql = new StringBuilder("delete from BranchCashLimit");
		sql.append(tableType.getSuffix());
		sql.append(" where branchCode = :branchCode ");
		sql.append(QueryUtil.getConcurrencyCondition(tableType));

		// Execute the SQL, binding the arguments.
		logger.trace(Literal.SQL + sql.toString());
		SqlParameterSource paramSource = new BeanPropertySqlParameterSource(branchCashLimit);
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
	public boolean isDuplicateKey(String branchCode, TableType tableType) {
		logger.debug(Literal.ENTERING);

		// Prepare the SQL.
		String sql;
		String whereClause = "branchCode = :branchCode ";

		switch (tableType) {
		case MAIN_TAB:
			sql = QueryUtil.getCountQuery("BranchCashLimit", whereClause);
			break;
		case TEMP_TAB:
			sql = QueryUtil.getCountQuery("BranchCashLimit_Temp", whereClause);
			break;
		default:
			sql = QueryUtil.getCountQuery(new String[] { "BranchCashLimit_Temp", "BranchCashLimit" }, whereClause);
			break;
		}

		// Execute the SQL, binding the arguments.
		logger.trace(Literal.SQL + sql);
		MapSqlParameterSource paramSource = new MapSqlParameterSource();
		paramSource.addValue("branchCode", branchCode);

		Integer count = jdbcTemplate.queryForObject(sql, paramSource, Integer.class);

		boolean exists = false;
		if (count > 0) {
			exists = true;
		}

		logger.debug(Literal.LEAVING);
		return exists;
	}

	@Override
	public List<BranchCashLimit> getAutoReplenishmentLimitList(String branchCode) {
		logger.debug(Literal.ENTERING);

		// Prepare the SQL.
		StringBuilder sql = new StringBuilder("SELECT ");
		sql.append(
				" branchCode, curLimitSetDate, reOrderLimit, cashLimit, adHocCashLimit, remarks,previousDate,previousAmount, ");
		sql.append(
				" BranchCodeName,branchCash, AdhocInitiationAmount, AdhocProcessingAmount, AdhocTransitAmount,AutoProcessingAmount, AutoTransitAmount, ");
		sql.append(" Version, LastMntOn, LastMntBy");
		sql.append(" From BranchCashLimit_Aview");
		if (StringUtils.isNotBlank(branchCode)) {
			sql.append(" Where branchCode=:BranchCode");
		}

		// Execute the SQL, binding the arguments.
		logger.trace(Literal.SQL + sql.toString());
		MapSqlParameterSource paramSource = new MapSqlParameterSource();
		paramSource.addValue("BranchCode", branchCode);

		RowMapper<BranchCashLimit> rowMapper = BeanPropertyRowMapper.newInstance(BranchCashLimit.class);
		logger.debug(Literal.LEAVING);

		return jdbcTemplate.query(sql.toString(), paramSource, rowMapper);
	}

}
