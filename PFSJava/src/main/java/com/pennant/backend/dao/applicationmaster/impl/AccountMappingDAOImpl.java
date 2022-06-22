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
 * * FileName : AccountMappingDAOImpl.java * * Author : PENNANT TECHONOLOGIES * * Creation Date : 24-04-2017 * *
 * Modified Date : 24-04-2017 * * Description : * *
 ********************************************************************************************
 * Date Author Version Comments *
 ********************************************************************************************
 * 24-04-2017 PENNANT 0.1 * * * * * * * * *
 ********************************************************************************************
 */
package com.pennant.backend.dao.applicationmaster.impl;

import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;

import com.pennant.backend.dao.applicationmaster.AccountMappingDAO;
import com.pennant.backend.model.applicationmaster.AccountMapping;
import com.pennanttech.pennapps.core.ConcurrencyException;
import com.pennanttech.pennapps.core.DependencyFoundException;
import com.pennanttech.pennapps.core.jdbc.BasicDao;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pennapps.core.resource.Message;
import com.pennanttech.pff.core.TableType;
import com.pennanttech.pff.core.util.QueryUtil;

/**
 * Data access layer implementation for <code>AccountMapping</code> with set of CRUD operations.
 */
public class AccountMappingDAOImpl extends BasicDao<AccountMapping> implements AccountMappingDAO {
	private static Logger logger = LogManager.getLogger(AccountMappingDAOImpl.class);

	public AccountMappingDAOImpl() {
		super();
	}

	@Override
	public AccountMapping getAccountMapping(String account, String type) {
		// Prepare the SQL.
		StringBuilder sql = new StringBuilder();
		sql.append("select account, hostAccount, FinType, CostCenterID, ProfitCenterID, AccountType");
		sql.append(", Version, LastMntOn, LastMntBy,RecordStatus, RoleCode, NextRoleCode");
		sql.append(", TaskId, NextTaskId, RecordType, WorkflowId");

		if (type.contains("View")) {
			sql.append(", costCenterCode, costCenterDesc, profitCenterCode, profitCenterDesc");
			sql.append(", accountTypeDesc, finTypeDesc");
		}

		sql.append(" From AccountMapping");
		sql.append(type);
		sql.append(" Where Account = :Account");

		// Execute the SQL, binding the arguments.
		logger.trace(Literal.SQL + sql.toString());

		AccountMapping accountMapping = new AccountMapping();
		accountMapping.setAccount(account);

		SqlParameterSource paramSource = new BeanPropertySqlParameterSource(accountMapping);
		RowMapper<AccountMapping> rowMapper = BeanPropertyRowMapper.newInstance(AccountMapping.class);

		try {
			return jdbcTemplate.queryForObject(sql.toString(), paramSource, rowMapper);
		} catch (EmptyResultDataAccessException e) {
			logger.warn(Message.NO_RECORD_FOUND);
			return null;
		}
	}

	@Override
	public List<AccountMapping> getAccountMappingFinType(String finType, String type) {

		logger.debug(Literal.ENTERING);

		AccountMapping accountMapping = new AccountMapping();
		accountMapping.setFinType(finType);

		StringBuilder sql = new StringBuilder();
		sql.append("select account, hostAccount, FinType, CostCenterID, ProfitCenterID, AccountType");
		sql.append(", Version, LastMntOn, LastMntBy,RecordStatus, RoleCode, NextRoleCode");
		sql.append(", TaskId, NextTaskId, RecordType, WorkflowId");

		if (type.contains("View")) {
			sql.append(", costCenterCode, costCenterDesc, profitCenterCode, profitCenterDesc");
			sql.append(", accountTypeDesc, finTypeDesc");
		}

		sql.append(" From AccountMapping");
		sql.append(type);
		sql.append(" Where FinType = :FinType");

		// Execute the SQL, binding the arguments.
		logger.trace(Literal.SQL + sql.toString());

		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(accountMapping);
		RowMapper<AccountMapping> typeRowMapper = BeanPropertyRowMapper.newInstance(AccountMapping.class);

		logger.debug(Literal.LEAVING);

		return this.jdbcTemplate.query(sql.toString(), beanParameters, typeRowMapper);
	}

	@Override
	public String save(AccountMapping accountMapping, TableType tableType) {
		logger.debug(Literal.ENTERING);

		// Prepare the SQL.
		StringBuilder sql = new StringBuilder(" insert into AccountMapping");
		sql.append(tableType.getSuffix());
		sql.append(" (account, hostAccount, ");
		sql.append(
				" Version , LastMntBy, LastMntOn, RecordStatus, RoleCode, NextRoleCode, TaskId, NextTaskId, RecordType, WorkflowId, FinType, CostCenterID, ProfitCenterID, AccountType)");
		sql.append(" values(");
		sql.append(" :account, :hostAccount, ");
		sql.append(
				" :Version , :LastMntBy, :LastMntOn, :RecordStatus, :RoleCode, :NextRoleCode, :TaskId, :NextTaskId, :RecordType, :WorkflowId, :FinType, :CostCenterID, :ProfitCenterID , :AccountType)");

		// Execute the SQL, binding the arguments.
		logger.trace(Literal.SQL + sql.toString());
		SqlParameterSource paramSource = new BeanPropertySqlParameterSource(accountMapping);

		try {
			jdbcTemplate.update(sql.toString(), paramSource);
		} catch (DuplicateKeyException e) {
			throw new ConcurrencyException(e);
		}

		logger.debug(Literal.LEAVING);
		return String.valueOf(accountMapping.getAccount());
	}

	@Override
	public void update(AccountMapping accountMapping, TableType tableType) {
		logger.debug(Literal.ENTERING);

		// Prepare the SQL.
		StringBuilder sql = new StringBuilder("update AccountMapping");
		sql.append(tableType.getSuffix());
		sql.append("  set hostAccount = :hostAccount, ");
		sql.append(" LastMntOn = :LastMntOn, RecordStatus = :RecordStatus, RoleCode = :RoleCode,");
		sql.append(" NextRoleCode = :NextRoleCode, TaskId = :TaskId, NextTaskId = :NextTaskId,");
		sql.append(" RecordType = :RecordType, WorkflowId = :WorkflowId, FinType = :FinType,");
		sql.append(" CostCenterID = :CostCenterID, ProfitCenterID = :ProfitCenterID, AccountType = :AccountType");
		sql.append(" where account = :account ");
		if (tableType == TableType.MAIN_TAB) {
			sql.append(" and Version = :Version - 1");
		}

		// Execute the SQL, binding the arguments.
		logger.trace(Literal.SQL + sql.toString());

		SqlParameterSource paramSource = new BeanPropertySqlParameterSource(accountMapping);
		int recordCount = jdbcTemplate.update(sql.toString(), paramSource);

		// Check for the concurrency failure.
		if (recordCount == 0) {
			throw new ConcurrencyException();
		}

		logger.debug(Literal.LEAVING);
	}

	@Override
	public void delete(AccountMapping accountMapping, TableType tableType) {
		logger.debug(Literal.ENTERING);

		// Prepare the SQL.
		StringBuilder sql = new StringBuilder("delete from AccountMapping");
		sql.append(tableType.getSuffix());
		sql.append(" where account = :account ");
		sql.append(QueryUtil.getConcurrencyCondition(tableType));

		// Execute the SQL, binding the arguments.
		logger.trace(Literal.SQL + sql.toString());
		SqlParameterSource paramSource = new BeanPropertySqlParameterSource(accountMapping);
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
	public void delete(String finType, TableType tableType) {
		logger.debug(Literal.ENTERING);

		AccountMapping accountMapping = new AccountMapping();
		accountMapping.setFinType(finType);
		StringBuilder sql = new StringBuilder("delete from AccountMapping");
		sql.append(tableType.getSuffix());
		sql.append(" where FinType = :FinType ");

		// Execute the SQL, binding the arguments.
		logger.trace(Literal.SQL + sql.toString());
		SqlParameterSource paramSource = new BeanPropertySqlParameterSource(accountMapping);
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
