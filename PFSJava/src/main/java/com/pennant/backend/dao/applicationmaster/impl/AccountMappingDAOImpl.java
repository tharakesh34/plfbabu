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

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.RowMapper;

import com.pennant.backend.dao.applicationmaster.AccountMappingDAO;
import com.pennant.backend.model.applicationmaster.AccountMapping;
import com.pennanttech.pennapps.core.ConcurrencyException;
import com.pennanttech.pennapps.core.DependencyFoundException;
import com.pennanttech.pennapps.core.jdbc.BasicDao;
import com.pennanttech.pennapps.core.jdbc.JdbcUtil;
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
		StringBuilder sql = getSqlQuery(type);
		sql.append(" Where Account = ?");

		log(sql.toString());

		try {
			return this.jdbcOperations.queryForObject(sql.toString(), new AccountMappingRM(type), account);
		} catch (EmptyResultDataAccessException e) {
			logger.warn(Message.NO_RECORD_FOUND);
			return null;
		}
	}

	@Override
	public List<AccountMapping> getAccountMappingFinType(String finType, String type) {
		StringBuilder sql = getSqlQuery(type);
		sql.append(" Where FinType = ?");

		log(sql.toString());

		return this.jdbcOperations.query(sql.toString(), ps -> ps.setString(1, finType), new AccountMappingRM(type));
	}

	@Override
	public String save(AccountMapping ac, TableType tableType) {
		StringBuilder sql = new StringBuilder("Insert into AccountMapping");
		sql.append(tableType.getSuffix());
		sql.append(" (Account, HostAccount, Version, LastMntBy, LastMntOn");
		sql.append(", RecordStatus, RoleCode, NextRoleCode, TaskId, NextTaskId");
		sql.append(", RecordType, WorkflowId, FinType, CostCenterID, ProfitCenterID, AccountType");
		sql.append(", OpenedDate, ClosedDate, Status, AllowedManualEntry, GLDescription, AccountTypeGroup");
		sql.append(", CreatedBy, CreatedOn, ApprovedBy, ApprovedOn)");
		sql.append(" Values (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ? , ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)");

		log(sql.toString());

		try {
			jdbcOperations.update(sql.toString(), ps -> {
				int index = 1;

				ps.setString(index++, ac.getAccount());
				ps.setString(index++, ac.getHostAccount());
				ps.setLong(index++, ac.getVersion());
				ps.setLong(index++, ac.getLastMntBy());
				ps.setTimestamp(index++, ac.getLastMntOn());
				ps.setString(index++, ac.getRecordStatus());
				ps.setString(index++, ac.getRoleCode());
				ps.setString(index++, ac.getNextRoleCode());
				ps.setString(index++, ac.getTaskId());
				ps.setString(index++, ac.getNextTaskId());
				ps.setString(index++, ac.getRecordType());
				ps.setLong(index++, ac.getWorkflowId());
				ps.setString(index++, ac.getFinType());
				ps.setObject(index++, ac.getCostCenterID());
				ps.setObject(index++, ac.getProfitCenterID());
				ps.setString(index++, ac.getAccountType());
				ps.setDate(index++, JdbcUtil.getDate(ac.getOpenedDate()));
				ps.setDate(index++, JdbcUtil.getDate(ac.getClosedDate()));
				ps.setString(index++, ac.getStatus());
				ps.setString(index++, ac.getAllowedManualEntry());
				ps.setString(index++, ac.getGLDescription());
				ps.setString(index++, ac.getAccountTypeGroup());
				ps.setLong(index++, ac.getCreatedBy());
				ps.setTimestamp(index++, ac.getCreatedOn());
				ps.setObject(index++, ac.getApprovedBy());
				ps.setTimestamp(index++, ac.getApprovedOn());

			});

		} catch (DuplicateKeyException e) {
			throw new ConcurrencyException(e);
		}

		return String.valueOf(ac.getAccount());
	}

	@Override
	public void update(AccountMapping ac, TableType tableType) {
		StringBuilder sql = new StringBuilder("Update AccountMapping");
		sql.append(tableType.getSuffix());
		sql.append(" Set HostAccount = ?, LastMntOn = ?, RecordStatus = ?, RoleCode = ?");
		sql.append(", NextRoleCode = ?, TaskId = ?, NextTaskId = ?, RecordType = ?, WorkflowId = ?");
		sql.append(", FinType = ?, CostCenterID = ?, ProfitCenterID = ?, AccountType = ?");
		sql.append(", OpenedDate = ?, ClosedDate = ?, Status = ?, AllowedManualEntry = ?, GLDescription = ?");
		sql.append(", AccountTypeGroup = ?, CreatedBy = ?, CreatedOn = ?, ApprovedBy = ?, ApprovedOn = ?");
		sql.append(" Where Account = ?");

		if (tableType == TableType.MAIN_TAB) {
			sql.append(" and Version = ?");
		}

		log(sql.toString());

		int recordCount = jdbcOperations.update(sql.toString(), ps -> {
			int index = 1;

			ps.setString(index++, ac.getHostAccount());
			ps.setTimestamp(index++, ac.getLastMntOn());
			ps.setString(index++, ac.getRecordStatus());
			ps.setString(index++, ac.getRoleCode());
			ps.setString(index++, ac.getNextRoleCode());
			ps.setString(index++, ac.getTaskId());
			ps.setString(index++, ac.getNextTaskId());
			ps.setString(index++, ac.getRecordType());
			ps.setLong(index++, ac.getWorkflowId());
			ps.setString(index++, ac.getFinType());
			ps.setObject(index++, ac.getCostCenterID());
			ps.setObject(index++, ac.getProfitCenterID());
			ps.setString(index++, ac.getAccountType());
			ps.setDate(index++, JdbcUtil.getDate(ac.getOpenedDate()));
			ps.setDate(index++, JdbcUtil.getDate(ac.getClosedDate()));
			ps.setString(index++, ac.getStatus());
			ps.setString(index++, ac.getAllowedManualEntry());
			ps.setString(index++, ac.getGLDescription());
			ps.setString(index++, ac.getAccountTypeGroup());
			ps.setLong(index++, ac.getCreatedBy());
			ps.setTimestamp(index++, ac.getCreatedOn());
			ps.setObject(index++, ac.getApprovedBy());
			ps.setTimestamp(index++, ac.getApprovedOn());

			ps.setString(index++, ac.getAccount());

			if (tableType == TableType.MAIN_TAB) {
				ps.setInt(index++, ac.getVersion() - 1);
			}
		});

		if (recordCount == 0) {
			throw new ConcurrencyException();
		}
	}

	@Override
	public void delete(AccountMapping ac, TableType tableType) {
		StringBuilder sql = new StringBuilder("Delete from AccountMapping");
		sql.append(tableType.getSuffix());
		sql.append(" Where Account = ? ");
		sql.append(QueryUtil.getConcurrencyClause(tableType));

		log(sql.toString());

		try {
			int recordCount = jdbcOperations.update(sql.toString(), ps -> {
				int index = 1;

				ps.setString(index++, ac.getAccount());

				if (tableType == TableType.TEMP_TAB) {
					ps.setTimestamp(index++, ac.getPrevMntOn());
				} else {
					ps.setInt(index++, ac.getVersion() - 1);
				}
			});

			if (recordCount == 0) {
				throw new ConcurrencyException();
			}
		} catch (DataAccessException e) {
			throw new DependencyFoundException(e);
		}
	}

	@Override
	public void delete(String finType, TableType tableType) {
		StringBuilder sql = new StringBuilder("Delete from AccountMapping");
		sql.append(tableType.getSuffix());
		sql.append(" Where FinType = ?");

		log(sql.toString());

		try {
			if (jdbcOperations.update(sql.toString(), ps -> ps.setString(1, finType)) == 0) {
				throw new ConcurrencyException();
			}
		} catch (DataAccessException e) {
			throw new DependencyFoundException(e);
		}
	}

	private StringBuilder getSqlQuery(String type) {
		StringBuilder sql = new StringBuilder("Select");
		sql.append(" Account, HostAccount, FinType, CostCenterID, ProfitCenterID, AccountType");
		sql.append(", OpenedDate, ClosedDate, Status, AllowedManualEntry, GLDescription, AccountTypeGroup");
		sql.append(", Version, LastMntOn, LastMntBy,RecordStatus, RoleCode, NextRoleCode");
		sql.append(", TaskId, NextTaskId, RecordType, WorkflowId");
		sql.append(", CreatedBy, CreatedOn, ApprovedBy, ApprovedOn");

		if (type.contains("View")) {
			sql.append(", CostCenterCode, CostCenterDesc, ProfitCenterCode, ProfitCenterDesc");
			sql.append(", AccountTypeDesc, FinTypeDesc");
		}

		sql.append(" From AccountMapping");
		sql.append(type);

		return sql;
	}

	private class AccountMappingRM implements RowMapper<AccountMapping> {
		private String type;

		public AccountMappingRM(String type) {
			this.type = type;
		}

		@Override
		public AccountMapping mapRow(ResultSet rs, int rowNum) throws SQLException {

			AccountMapping ac = new AccountMapping();

			ac.setAccount(rs.getString("Account"));
			ac.setHostAccount(rs.getString("HostAccount"));
			ac.setFinType(rs.getString("FinType"));
			ac.setCostCenterID(JdbcUtil.getLong(rs.getObject("CostCenterID")));
			ac.setProfitCenterID(JdbcUtil.getLong(rs.getObject("ProfitCenterID")));
			ac.setAccountType(rs.getString("AccountType"));
			ac.setOpenedDate(rs.getDate("OpenedDate"));
			ac.setClosedDate(rs.getDate("ClosedDate"));
			ac.setStatus(rs.getString("Status"));
			ac.setAllowedManualEntry(rs.getString("AllowedManualEntry"));
			ac.setGLDescription(rs.getString("GLDescription"));
			ac.setAccountTypeGroup(rs.getString("AccountTypeGroup"));
			ac.setVersion(rs.getInt("Version"));
			ac.setLastMntOn(rs.getTimestamp("LastMntOn"));
			ac.setLastMntBy(rs.getLong("LastMntBy"));
			ac.setRecordStatus(rs.getString("RecordStatus"));
			ac.setRoleCode(rs.getString("RoleCode"));
			ac.setNextRoleCode(rs.getString("NextRoleCode"));
			ac.setTaskId(rs.getString("TaskID"));
			ac.setNextTaskId(rs.getString("NextTaskId"));
			ac.setRecordType(rs.getString("RecordType"));
			ac.setWorkflowId(rs.getLong("WorkFlowId"));
			ac.setCreatedBy(rs.getLong("CreatedBy"));
			ac.setCreatedOn(rs.getTimestamp("CreatedOn"));
			ac.setApprovedBy(JdbcUtil.getLong(rs.getObject("ApprovedBy")));
			ac.setApprovedOn(rs.getTimestamp("ApprovedOn"));

			if (type.contains("View")) {
				ac.setCostCenterCode(rs.getString("CostCenterCode"));
				ac.setCostCenterDesc(rs.getString("CostCenterDesc"));
				ac.setProfitCenterCode(rs.getString("ProfitCenterCode"));
				ac.setProfitCenterDesc(rs.getString("ProfitCenterDesc"));
				ac.setAccountTypeDesc(rs.getString("AccountTypeDesc"));
				ac.setFinTypeDesc(rs.getString("FinTypeDesc"));
			}

			return ac;
		}
	}

	private void log(String sql) {
		logger.debug(Literal.SQL.concat(sql));
	}

	@Override
	public String getAccountMappingByAccount(String account) {
		String sql = "Select HostAccount From AccountMapping Where Account = ?";

		logger.debug(Literal.SQL.concat(sql));

		try {
			return jdbcOperations.queryForObject(sql, String.class, account);
		} catch (EmptyResultDataAccessException e) {
			logger.warn(Message.NO_RECORD_FOUND);
			return null;
		}
	}

	@Override
	public boolean isValidAccount(String account, String trantypeBoth, String trantypeDebit, String status) {
		String sql = "Select count(Account) From AccountMapping Where Account = ? and AllowedManualEntry in(?,?) and status = ?";

		logger.debug(Literal.SQL.concat(sql));

		try {
			return jdbcOperations.queryForObject(sql, Integer.class, account, trantypeBoth, trantypeDebit, status) > 0;
		} catch (EmptyResultDataAccessException e) {
			logger.warn(Message.NO_RECORD_FOUND);
			return false;
		}

	}

	@Override
	public boolean isExistingHostAccount(String hostAccount, String type) {
		StringBuilder sql = new StringBuilder("Select count(HostAccount) From AccountMapping");
		sql.append(type);
		sql.append(" Where HostAccount = ?");

		logger.debug(sql.toString());

		return jdbcOperations.queryForObject(sql.toString(), Integer.class, hostAccount) > 0;

	}

	@Override
	public AccountMapping getAccountMappingForAcounting(String account) {
		String sql = "Select AccountTypeDesc, AccountType From AccountMapping_view Where Account = ?";

		logger.debug(Literal.SQL.concat(sql));

		logger.debug(Literal.SQL.concat(sql.toString()));

		try {
			return this.jdbcOperations.queryForObject(sql.toString(), (rs, rowNum) -> {
				AccountMapping am = new AccountMapping();

				am.setAccountTypeDesc(rs.getString("AccountTypeDesc"));
				am.setAccountType(rs.getString("AccountType"));

				return am;

			}, account);
		} catch (EmptyResultDataAccessException e) {
			logger.warn(Message.NO_RECORD_FOUND);
			return null;
		}
	}
}