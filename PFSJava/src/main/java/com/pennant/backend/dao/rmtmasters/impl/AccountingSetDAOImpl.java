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
 * * FileName : AccountingSetDAOImpl.java * * Author : PENNANT TECHONOLOGIES * * Creation Date : 14-12-2011 * * Modified
 * Date : 14-12-2011 * * Description : * *
 ******************************************************************************************** 
 * Date Author Version Comments *
 ******************************************************************************************** 
 * 14-12-2011 Pennant 0.1 * * * * * * * * *
 ******************************************************************************************** 
 */

package com.pennant.backend.dao.rmtmasters.impl;

import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;

import com.pennant.backend.dao.rmtmasters.AccountingSetDAO;
import com.pennant.backend.model.rmtmasters.AccountType;
import com.pennant.backend.model.rmtmasters.AccountingSet;
import com.pennanttech.pennapps.core.ConcurrencyException;
import com.pennanttech.pennapps.core.DependencyFoundException;
import com.pennanttech.pennapps.core.jdbc.SequenceDao;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pennapps.core.resource.Message;

/**
 * DAO methods implementation for the <b>AccountingSet model</b> class.<br>
 */
public class AccountingSetDAOImpl extends SequenceDao<AccountingSet> implements AccountingSetDAO {
	private static Logger logger = LogManager.getLogger(AccountingSetDAOImpl.class);

	public AccountingSetDAOImpl() {
		super();
	}

	/**
	 * Fetch the Record Accounting Set details by key field
	 * 
	 * @param id   (int)
	 * @param type (String) ""/_Temp/_View
	 * @return AccountingSet
	 */
	@Override
	public AccountingSet getAccountingSetById(final long id, String type) {
		logger.debug("Entering");
		AccountingSet accountingSet = new AccountingSet();

		accountingSet.setId(id);

		StringBuilder selectSql = new StringBuilder("Select AccountSetid, EventCode, AccountSetCode, ");
		selectSql.append(" AccountSetCodeName,SystemDefault, EntryByInvestment, ");
		selectSql.append(" Version , LastMntBy, LastMntOn, RecordStatus, RoleCode, NextRoleCode,");
		selectSql.append(" TaskId, NextTaskId, RecordType, WorkflowId ");

		if (StringUtils.trimToEmpty(type).contains("View")) {
			selectSql.append(",lovDescEventCodeName ");
		}
		selectSql.append(" From RMTAccountingSet");
		selectSql.append(StringUtils.trimToEmpty(type));
		selectSql.append(" Where AccountSetid =:AccountSetid");

		logger.debug("selectSql: " + selectSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(accountingSet);
		RowMapper<AccountingSet> typeRowMapper = BeanPropertyRowMapper.newInstance(AccountingSet.class);

		try {
			return this.jdbcTemplate.queryForObject(selectSql.toString(), beanParameters, typeRowMapper);
		} catch (EmptyResultDataAccessException e) {
			logger.warn(Message.NO_RECORD_FOUND);
			return null;
		}
	}

	/**
	 * This method Deletes the Record from the RMTAccountingSet or RMTAccountingSet_Temp. if Record not deleted then
	 * throws DataAccessException with error 41003. delete Accounting Set by key AccountSetid
	 * 
	 * @param Accounting Set (accountingSet)
	 * @param type       (String) ""/_Temp/_View
	 * @return void
	 * @throws DataAccessException
	 * 
	 */
	@Override
	public void delete(AccountingSet accountingSet, String type) {
		logger.debug("Entering");
		int recordCount = 0;

		StringBuilder deleteSql = new StringBuilder("Delete From RMTAccountingSet");
		deleteSql.append(StringUtils.trimToEmpty(type));
		deleteSql.append(" Where AccountSetid =:AccountSetid");
		logger.debug("deleteSql: " + deleteSql.toString());

		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(accountingSet);
		try {
			recordCount = this.jdbcTemplate.update(deleteSql.toString(), beanParameters);
			if (recordCount <= 0) {
				throw new ConcurrencyException();
			}
		} catch (DataAccessException e) {
			throw new DependencyFoundException(e);
		}
		logger.debug("Leaving");
	}

	/**
	 * This method insert new Records into RMTAccountingSet or RMTAccountingSet_Temp. it fetches the available Sequence
	 * form SeqRMTAccountingSet by using getNextidviewDAO().getNextId() method.
	 * 
	 * save Accounting Set
	 * 
	 * @param Accounting Set (accountingSet)
	 * @param type       (String) ""/_Temp/_View
	 * @return void
	 * @throws DataAccessException
	 * 
	 */

	@Override
	public long save(AccountingSet accountingSet, String type) {
		logger.debug("Entering");
		if (accountingSet.getId() == Long.MIN_VALUE) {
			accountingSet.setId(getNextValue("SeqRMTAccountingSet"));
			logger.debug("get NextValue:" + accountingSet.getId());
		}

		StringBuilder insertSql = new StringBuilder("Insert Into RMTAccountingSet");
		insertSql.append(StringUtils.trimToEmpty(type));
		insertSql.append(" (AccountSetid, EventCode, AccountSetCode, AccountSetCodeName,EntryByInvestment ");
		insertSql.append(",SystemDefault, Version , LastMntBy, LastMntOn, RecordStatus, RoleCode, ");
		insertSql.append(" NextRoleCode, TaskId, NextTaskId, RecordType, WorkflowId)");
		insertSql.append(" Values(:AccountSetid, :EventCode, :AccountSetCode, :AccountSetCodeName, :EntryByInvestment");
		insertSql.append(",:SystemDefault, :Version , :LastMntBy, :LastMntOn, :RecordStatus, :RoleCode, ");
		insertSql.append(" :NextRoleCode, :TaskId, :NextTaskId, :RecordType, :WorkflowId)");

		logger.debug("insertSql: " + insertSql.toString());

		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(accountingSet);
		this.jdbcTemplate.update(insertSql.toString(), beanParameters);
		logger.debug("Leaving");
		return accountingSet.getId();
	}

	/**
	 * This method updates the Record RMTAccountingSet or RMTAccountingSet_Temp. if Record not updated then throws
	 * DataAccessException with error 41004. update Accounting Set by key AccountSetid and Version
	 * 
	 * @param Accounting Set (accountingSet)
	 * @param type       (String) ""/_Temp/_View
	 * @return void
	 * @throws DataAccessException
	 * 
	 */
	@Override
	public void update(AccountingSet accountingSet, String type) {
		int recordCount = 0;
		logger.debug("Entering");
		StringBuilder updateSql = new StringBuilder("Update RMTAccountingSet");
		updateSql.append(StringUtils.trimToEmpty(type));
		updateSql.append(" Set EventCode = :EventCode, AccountSetCode = :AccountSetCode, ");
		updateSql.append(
				" AccountSetCodeName = :AccountSetCodeName ,SystemDefault=:SystemDefault,EntryByInvestment=:EntryByInvestment,  ");
		updateSql.append(" Version = :Version , LastMntBy = :LastMntBy, LastMntOn = :LastMntOn, ");
		updateSql.append(" RecordStatus= :RecordStatus, RoleCode = :RoleCode, NextRoleCode = :NextRoleCode, ");
		updateSql.append(
				" TaskId = :TaskId, NextTaskId = :NextTaskId, RecordType = :RecordType, WorkflowId = :WorkflowId");
		updateSql.append(" Where AccountSetid =:AccountSetid");

		if (!type.endsWith("_Temp")) {
			updateSql.append("  AND Version= :Version-1");
		}

		logger.debug("updateSql: " + updateSql.toString());

		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(accountingSet);
		recordCount = this.jdbcTemplate.update(updateSql.toString(), beanParameters);

		if (recordCount <= 0) {
			throw new ConcurrencyException();
		}
		logger.debug("Leaving");
	}

	@Override
	public Long getAccountingSetId(final String eventCode, final String accSetCode) {
		String sql = "Select AccountSetid From RMTAccountingSet Where EventCode = ? and AccountSetCode = ?";

		logger.debug(Literal.SQL.concat(sql));

		try {
			return this.jdbcOperations.queryForObject(sql, Long.class, eventCode, accSetCode);
		} catch (EmptyResultDataAccessException e) {
			logger.warn(Message.NO_RECORD_FOUND);
			return 0L;
		}
	}

	@Override
	public Long getAccountingSetId(final String eventCode) {
		String sql = "Select AccountSetid From RMTAccountingSet Where EventCode = ?";

		logger.debug(Literal.SQL.concat(sql));

		try {
			return this.jdbcOperations.queryForObject(sql, Long.class, eventCode);
		} catch (EmptyResultDataAccessException e) {
			logger.warn(Message.NO_RECORD_FOUND);
			return 0L;
		}
	}

	@Override
	public AccountingSet getAccSetSysDflByEvent(String event, String setCode, String type) {
		logger.debug("Entering");
		AccountingSet accountingSet = new AccountingSet();
		accountingSet.setEventCode(event);
		accountingSet.setAccountSetCode(setCode);

		StringBuilder selectSql = new StringBuilder(
				"Select AccountSetid, EventCode, AccountSetCode, AccountSetCodeName,EntryByInvestment");
		selectSql.append(",SystemDefault, Version , LastMntBy, LastMntOn, RecordStatus, RoleCode, ");
		selectSql.append(" NextRoleCode, TaskId, NextTaskId, RecordType, WorkflowId");
		selectSql.append(" From RMTAccountingSet");
		selectSql.append(StringUtils.trimToEmpty(type));
		selectSql.append(" Where EventCode =:EventCode and AccountSetCode = :AccountSetCode and SystemDefault=1 ");

		logger.debug("selectSql: " + selectSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(accountingSet);
		RowMapper<AccountingSet> typeRowMapper = BeanPropertyRowMapper.newInstance(AccountingSet.class);

		try {
			accountingSet = this.jdbcTemplate.queryForObject(selectSql.toString(), beanParameters, typeRowMapper);
		} catch (EmptyResultDataAccessException e) {
			accountingSet = null;
		}
		logger.debug("Leaving");
		return accountingSet;
	}

	@Override
	public AccountingSet getAccountingSetbyEventCode(AccountingSet accountingset, String type) {
		logger.debug("Entering");
		StringBuilder selectSql = new StringBuilder(
				"Select AccountSetid, EventCode, AccountSetCode,AccountSetCodeName ");
		selectSql.append(" From RMTAccountingSet");
		selectSql.append(StringUtils.trimToEmpty(type));
		selectSql.append(
				" Where  AccountSetid != :AccountSetid AND EventCode = :EventCode AND AccountSetCode = :AccountSetCode");

		logger.debug("selectSql: " + selectSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(accountingset);
		RowMapper<AccountingSet> typeRowMapper = BeanPropertyRowMapper.newInstance(AccountingSet.class);

		try {
			return this.jdbcTemplate.queryForObject(selectSql.toString(), beanParameters, typeRowMapper);
		} catch (EmptyResultDataAccessException e) {
			logger.warn(Message.NO_RECORD_FOUND);
			return null;
		}
	}

	@Override
	public boolean isValidCategoryWiseEvents(String eventCode) {
		String sql = "Select count(AEEventCode) from CategoryWiseEvents where AEEventCode = ?";

		logger.debug(Literal.SQL.concat(sql));

		return jdbcOperations.queryForObject(sql, Integer.class, eventCode) > 0;
	}

	@Override
	public List<AccountType> getAccountTypes() {
		StringBuilder sql = new StringBuilder("Select GroupCode, AcType, AcTypeDesc");
		sql.append(" From RMTAccountTypes acct");
		sql.append(" Inner Join AccountTypeGroup atg on atg.GroupId = acct.AcTypeGrpId");
		sql.append(" Where atg.GroupCode in (?,?)");

		logger.debug(Literal.SQL.concat(sql.toString()));

		return this.jdbcOperations.query(sql.toString(), ps -> {

			ps.setString(1, "ASSET");
			ps.setString(2, "LIABILITY");

		}, (rs, rowNum) -> {
			AccountType at = new AccountType();

			at.setGroupCode(rs.getString("GroupCode"));
			at.setAcType(rs.getString("AcType"));
			at.setAcTypeDesc(rs.getString("AcTypeDesc"));

			return at;
		});
	}
}