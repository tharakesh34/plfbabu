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

import javax.sql.DataSource;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.core.simple.ParameterizedBeanPropertyRowMapper;

import com.pennant.app.util.ErrorUtil;
import com.pennant.backend.dao.impl.BasisNextidDaoImpl;
import com.pennant.backend.dao.rmtmasters.AccountingSetDAO;
import com.pennant.backend.model.ErrorDetails;
import com.pennant.backend.model.rmtmasters.AccountingSet;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantJavaUtil;

/**
 * DAO methods implementation for the <b>AccountingSet model</b> class.<br>
 */
public class AccountingSetDAOImpl extends BasisNextidDaoImpl<AccountingSet> implements
        AccountingSetDAO {

	private static Logger logger = Logger.getLogger(AccountingSetDAOImpl.class);

	// Spring Named JDBC Template
	private NamedParameterJdbcTemplate namedParameterJdbcTemplate;

	public AccountingSetDAOImpl() {
		super();
	}
	
	/**
	 * Fetch the Record Accounting Set details by key field
	 * 
	 * @param id
	 *            (int)
	 * @param type
	 *            (String) ""/_Temp/_View
	 * @return AccountingSet
	 */
	@Override
	public AccountingSet getAccountingSetById(final long id, String type) {
		logger.debug("Entering");
		AccountingSet accountingSet = new AccountingSet();

		accountingSet.setId(id);

		StringBuilder selectSql = new StringBuilder(
		        "Select AccountSetid, EventCode, AccountSetCode, ");
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
		RowMapper<AccountingSet> typeRowMapper = ParameterizedBeanPropertyRowMapper
		        .newInstance(AccountingSet.class);

		try {
			accountingSet = this.namedParameterJdbcTemplate.queryForObject(selectSql.toString(),
			        beanParameters, typeRowMapper);
		} catch (EmptyResultDataAccessException e) {
			logger.warn("Exception: ", e);
			accountingSet = null;
		}
		logger.debug("Leaving");
		return accountingSet;
	}

	/**
	 * To Set dataSource
	 * 
	 * @param dataSource
	 */
	public void setDataSource(DataSource dataSource) {
		this.namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
	}

	/**
	 * This method Deletes the Record from the RMTAccountingSet or RMTAccountingSet_Temp. if Record not deleted then
	 * throws DataAccessException with error 41003. delete Accounting Set by key AccountSetid
	 * 
	 * @param Accounting
	 *            Set (accountingSet)
	 * @param type
	 *            (String) ""/_Temp/_View
	 * @return void
	 * @throws DataAccessException
	 * 
	 */
	@SuppressWarnings("serial")
	public void delete(AccountingSet accountingSet, String type) {
		logger.debug("Entering");
		int recordCount = 0;

		StringBuilder deleteSql = new StringBuilder("Delete From RMTAccountingSet");
		deleteSql.append(StringUtils.trimToEmpty(type));
		deleteSql.append(" Where AccountSetid =:AccountSetid");
		logger.debug("deleteSql: " + deleteSql.toString());

		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(accountingSet);
		try {
			recordCount = this.namedParameterJdbcTemplate.update(deleteSql.toString(),
			        beanParameters);
			if (recordCount <= 0) {
				ErrorDetails errorDetails = getError("41003", accountingSet.getAccountSetCode(),
				        accountingSet.getUserDetails().getUsrLanguage());
				throw new DataAccessException(errorDetails.getError()) {
				};
			}
		} catch (DataAccessException e) {
			logger.error("Exception: ", e);
			ErrorDetails errorDetails = getError("41006", accountingSet.getAccountSetCode(),
			        accountingSet.getUserDetails().getUsrLanguage());
			throw new DataAccessException(errorDetails.getError()) {
			};
		}
		logger.debug("Leaving");
	}

	/**
	 * This method insert new Records into RMTAccountingSet or RMTAccountingSet_Temp. it fetches the available Sequence
	 * form SeqRMTAccountingSet by using getNextidviewDAO().getNextId() method.
	 * 
	 * save Accounting Set
	 * 
	 * @param Accounting
	 *            Set (accountingSet)
	 * @param type
	 *            (String) ""/_Temp/_View
	 * @return void
	 * @throws DataAccessException
	 * 
	 */

	@Override
	public long save(AccountingSet accountingSet, String type) {
		logger.debug("Entering");
		if (accountingSet.getId() == Long.MIN_VALUE) {
			accountingSet.setId(getNextidviewDAO().getNextId("SeqRMTAccountingSet"));
			logger.debug("get NextID:" + accountingSet.getId());
		}

		StringBuilder insertSql = new StringBuilder("Insert Into RMTAccountingSet");
		insertSql.append(StringUtils.trimToEmpty(type));
		insertSql
		        .append(" (AccountSetid, EventCode, AccountSetCode, AccountSetCodeName,EntryByInvestment ");
		insertSql
		        .append(",SystemDefault, Version , LastMntBy, LastMntOn, RecordStatus, RoleCode, ");
		insertSql.append(" NextRoleCode, TaskId, NextTaskId, RecordType, WorkflowId)");
		insertSql
		        .append(" Values(:AccountSetid, :EventCode, :AccountSetCode, :AccountSetCodeName, :EntryByInvestment");
		insertSql
		        .append(",:SystemDefault, :Version , :LastMntBy, :LastMntOn, :RecordStatus, :RoleCode, ");
		insertSql.append(" :NextRoleCode, :TaskId, :NextTaskId, :RecordType, :WorkflowId)");

		logger.debug("insertSql: " + insertSql.toString());

		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(accountingSet);
		this.namedParameterJdbcTemplate.update(insertSql.toString(), beanParameters);
		logger.debug("Leaving");
		return accountingSet.getId();
	}

	/**
	 * This method updates the Record RMTAccountingSet or RMTAccountingSet_Temp. if Record not updated then throws
	 * DataAccessException with error 41004. update Accounting Set by key AccountSetid and Version
	 * 
	 * @param Accounting
	 *            Set (accountingSet)
	 * @param type
	 *            (String) ""/_Temp/_View
	 * @return void
	 * @throws DataAccessException
	 * 
	 */
	@SuppressWarnings("serial")
	@Override
	public void update(AccountingSet accountingSet, String type) {
		int recordCount = 0;
		logger.debug("Entering");
		StringBuilder updateSql = new StringBuilder("Update RMTAccountingSet");
		updateSql.append(StringUtils.trimToEmpty(type));
		updateSql
		        .append(" Set AccountSetid = :AccountSetid, EventCode = :EventCode, AccountSetCode = :AccountSetCode, ");
		updateSql
		        .append(" AccountSetCodeName = :AccountSetCodeName ,SystemDefault=:SystemDefault,EntryByInvestment=:EntryByInvestment,  ");
		updateSql.append(" Version = :Version , LastMntBy = :LastMntBy, LastMntOn = :LastMntOn, ");
		updateSql
		        .append(" RecordStatus= :RecordStatus, RoleCode = :RoleCode, NextRoleCode = :NextRoleCode, ");
		updateSql
		        .append(" TaskId = :TaskId, NextTaskId = :NextTaskId, RecordType = :RecordType, WorkflowId = :WorkflowId");
		updateSql.append(" Where AccountSetid =:AccountSetid");

		if (!type.endsWith("_Temp")) {
			updateSql.append("  AND Version= :Version-1");
		}

		logger.debug("updateSql: " + updateSql.toString());

		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(accountingSet);
		recordCount = this.namedParameterJdbcTemplate.update(updateSql.toString(), beanParameters);

		if (recordCount <= 0) {
			logger.debug("Error Update Method Count :" + recordCount);
			ErrorDetails errorDetails = getError("41004", accountingSet.getAccountSetCode(),
			        accountingSet.getUserDetails().getUsrLanguage());
			throw new DataAccessException(errorDetails.getError()) {
			};
		}
		logger.debug("Leaving");
	}

	@Override
	public List<AccountingSet> getListAERuleBySysDflt(boolean isAllowedRIA, String type) {
		logger.debug("Entering");
		
		AccountingSet accountingSet = new AccountingSet();
		accountingSet.setEntryByInvestment(isAllowedRIA);
		accountingSet.setSystemDefault(true);
		
		StringBuilder selectSql = new StringBuilder("Select AccountSetid, EventCode, AccountSetCode,  AccountSetCodeName  ");
		selectSql.append(" From RMTAccountingSet");
		selectSql.append(StringUtils.trim(type));
		selectSql.append(" Where EventCode IN ('ADDDBSP','ADDDBSF','ADDDBSN','AMZ','AMZSUSP','AMZPD','CMTDISB','EMIDAY','REAGING',");
		selectSql.append(" 'NORM_PIS','PIS_NORM','RATCHG','REPAY', 'EARLYPAY','EARLYSTL','LATEPAY',");
		selectSql.append(" 'WRITEOFF','WRITEBK','GRACEEND','DEFFRQ','DEFRPY','PROVSN','SCDCHG','COMPOUND','DPRCIATE')");
		selectSql.append("  AND SystemDefault= :SystemDefault AND EntryByInvestment=:EntryByInvestment ");
		logger.debug("selectSql: " + selectSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(accountingSet);
		RowMapper<AccountingSet> typeRowMapper = ParameterizedBeanPropertyRowMapper.newInstance(AccountingSet.class);
		logger.debug("Leaving");
		return this.namedParameterJdbcTemplate.query(selectSql.toString(), beanParameters, typeRowMapper);
	}
	
	/**Method for Fetching Accounting Set ID by Using EventCode and AccSetCode
	 * 
	 * @param eventCode
	 * @param accSetCode
	 * @param type
	 * @return  Long
	 */
	@Override
	public Long getAccountingSetId(final String eventCode, final String accSetCode) {
		logger.debug("Entering");
		AccountingSet accountingSet = new AccountingSet();

		accountingSet.setEventCode(eventCode);
		accountingSet.setAccountSetCode(accSetCode);

		StringBuilder selectSql = new StringBuilder(" Select AccountSetid  ");
		selectSql.append(" From RMTAccountingSet");
		selectSql.append(" Where EventCode =:EventCode and AccountSetCode=:AccountSetCode");

		logger.debug("selectSql: " + selectSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(accountingSet);
		
		long accSetId = 0;
		
		try {
			accSetId =  this.namedParameterJdbcTemplate.queryForObject(selectSql.toString(),
		        beanParameters, Long.class);
		} catch (EmptyResultDataAccessException e) {
			logger.error("Exception: ", e);
			accSetId = 0;
		}
		logger.debug("Leaving");
		return accSetId;
	}

	@Override
	public AccountingSet getAccSetSysDflByEvent(String event, String type) {
		logger.debug("Entering");
		AccountingSet accountingSet = new AccountingSet();
		accountingSet.setEventCode(event);

		StringBuilder selectSql = new StringBuilder(
		        "Select AccountSetid, EventCode, AccountSetCode, AccountSetCodeName,EntryByInvestment");
		selectSql
		        .append(",SystemDefault, Version , LastMntBy, LastMntOn, RecordStatus, RoleCode, ");
		selectSql.append(" NextRoleCode, TaskId, NextTaskId, RecordType, WorkflowId");
		selectSql.append(" From RMTAccountingSet");
		selectSql.append(StringUtils.trimToEmpty(type));
		selectSql.append(" Where EventCode =:EventCode and SystemDefault=1 ");

		logger.debug("selectSql: " + selectSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(accountingSet);
		RowMapper<AccountingSet> typeRowMapper = ParameterizedBeanPropertyRowMapper
		        .newInstance(AccountingSet.class);

		try {
			accountingSet = this.namedParameterJdbcTemplate.queryForObject(selectSql.toString(),
			        beanParameters, typeRowMapper);
		} catch (EmptyResultDataAccessException e) {
			logger.warn("Exception: ", e);
			accountingSet = null;
		}
		logger.debug("Leaving");
		return accountingSet;
	}

	@Override
    public AccountingSet getAccountingSetbyEventCode(AccountingSet accountingset, String type) {
		logger.debug("Entering");
		StringBuilder selectSql = new StringBuilder("Select AccountSetid, EventCode, AccountSetCode,AccountSetCodeName ");
		selectSql.append(" From RMTAccountingSet");
		selectSql.append(StringUtils.trimToEmpty(type));
		selectSql.append(" Where  AccountSetid != :AccountSetid AND EventCode = :EventCode AND AccountSetCode = :AccountSetCode");
		
		logger.debug("selectSql: " + selectSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(accountingset);
		RowMapper<AccountingSet> typeRowMapper = ParameterizedBeanPropertyRowMapper.newInstance(AccountingSet.class);
		
		try{
			accountingset = this.namedParameterJdbcTemplate.queryForObject(selectSql.toString(), beanParameters, typeRowMapper);	
		}catch (EmptyResultDataAccessException e) {
			logger.warn("Exception: ", e);
			accountingset = null;
		}
		logger.debug("Leaving");
		return accountingset;
    }

	
	private ErrorDetails getError(String errorId, String accountSetid, String userLanguage) {
		String[][] parms = new String[2][1];
		parms[1][0] = accountSetid;
		parms[0][0] = PennantJavaUtil.getLabel("label_AccountSetid") + ":" + parms[1][0];
		return ErrorUtil.getErrorDetail(new ErrorDetails(PennantConstants.KEY_FIELD, errorId,
		        parms[0], parms[1]), userLanguage);
	}

	
}