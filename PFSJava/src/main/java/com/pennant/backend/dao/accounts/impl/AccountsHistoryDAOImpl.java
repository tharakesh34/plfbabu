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
 * FileName    		:  AccountsDAOImpl.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  02-01-2012    														*
 *                                                                  						*
 * Modified Date    :  02-01-2012    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 02-01-2012       Pennant	                 0.1                                            * 
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
package com.pennant.backend.dao.accounts.impl;

import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSourceUtils;
import org.springframework.jdbc.core.simple.ParameterizedBeanPropertyRowMapper;

import com.pennant.backend.dao.accounts.AccountsHistoryDAO;
import com.pennant.backend.dao.impl.BasisCodeDAO;
import com.pennant.backend.model.accounts.AccountsHistory;

public class AccountsHistoryDAOImpl extends BasisCodeDAO<AccountsHistory> implements AccountsHistoryDAO {
	private static Logger				logger	= Logger.getLogger(AccountsHistoryDAOImpl.class);

	// Spring Named JDBC Template
	private NamedParameterJdbcTemplate	namedParameterJdbcTemplate;

	public AccountsHistoryDAOImpl() {
		super();
	}

	@Override
	public AccountsHistory getAccountsHistoryById(final String id) {
		logger.debug("Entering");

		AccountsHistory accountsHist = new AccountsHistory();
		accountsHist.setAccountId(id);

		StringBuilder selectSql = new StringBuilder("Select AccountId, PostDate, TodayDebits, TodayCredits, ");
		selectSql.append("TodayNet, ShadowBal, AcBalance");
		selectSql.append(" From AccountsHistory");
		selectSql.append(" Where AccountId =:AccountId");

		logger.debug("selectSql: " + selectSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(accountsHist);
		RowMapper<AccountsHistory> typeRowMapper = ParameterizedBeanPropertyRowMapper
				.newInstance(AccountsHistory.class);

		try {
			accountsHist = this.namedParameterJdbcTemplate.queryForObject(selectSql.toString(), beanParameters,
					typeRowMapper);
		} catch (EmptyResultDataAccessException e) {
			logger.warn("Exception: ", e);
			accountsHist = null;
		}

		logger.debug("Leaving");
		return accountsHist;
	}

	@Override
	public boolean save(AccountsHistory accountsHist) {
		logger.debug("Entering");

		boolean isTxnSuccess = false;

		StringBuilder insertSql = new StringBuilder("Insert Into AccountsHistory");
		insertSql.append(" (AccountId, PostDate, TodayDebits, TodayCredits, ");
		insertSql.append(" TodayNet, ShadowBal, AcBalance ");
		insertSql.append(" Values(:AccountId, :PostDate, :TodayDebits, :TodayCredits, ");
		insertSql.append(" :TodayNet, :ShadowBal, :AcBalance)");

		logger.debug("insertSql: " + insertSql.toString());

		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(accountsHist);

		try {
			this.namedParameterJdbcTemplate.update(insertSql.toString(), beanParameters);
			isTxnSuccess = true;
		} catch (Exception e) {
		}

		logger.debug("Leaving");
		return isTxnSuccess;
	}

	@Override
	public boolean saveList(List<AccountsHistory> accountHistList) {
		logger.debug("Entering");

		boolean isTxnSuccess = false;

		StringBuilder insertSql = new StringBuilder("Insert Into AccountsHistory");
		insertSql.append(" (AccountId, PostDate, TodayDebits, TodayCredits, ");
		insertSql.append(" TodayNet, ShadowBal, AcBalance ");
		insertSql.append(" Values(:AccountId, :PostDate, :TodayDebits, :TodayCredits, ");
		insertSql.append(" :TodayNet, :ShadowBal, :AcBalance)");

		logger.debug("insertSql: " + insertSql.toString());

		SqlParameterSource[] beanParameters = SqlParameterSourceUtils.createBatch(accountHistList.toArray());

		try {
			this.namedParameterJdbcTemplate.batchUpdate(insertSql.toString(), beanParameters);
			isTxnSuccess = true;
		} catch (Exception e) {
		}

		logger.debug("Leaving");
		return isTxnSuccess;
	}

	@Override
	public boolean update(AccountsHistory accountsHist) {
		logger.debug("Entering");

		int recordCount = 0;
		boolean isTxnSuccess = false;

		StringBuilder updateSql = new StringBuilder("Update AccountsHistory");
		updateSql.append(" Set TodayDebits = :TodayDebits, TodayCredits = :TodayCredits");
		updateSql.append(", TodayNet = :TodayNet, ShadowBal = :ShadowBal,  AcBalance = :AcBalance");
		updateSql.append(" Where AccountId = :AccountId AND PostDate = :PostDate");

		logger.debug("updateSql: " + updateSql.toString());

		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(accountsHist);

		try {
			recordCount = this.namedParameterJdbcTemplate.update(updateSql.toString(), beanParameters);

			if (recordCount > 0) {
				isTxnSuccess = true;
			}

		} catch (Exception e) {
		}

		logger.debug("Leaving");
		return isTxnSuccess;
	}

	@Override
	public boolean updateList(List<AccountsHistory> accountHistList) {
		logger.debug("Entering");

		boolean isTxnSuccess = false;

		StringBuilder updateSql = new StringBuilder("Update AccountsHistory");
		updateSql.append(" Set TodayDebits = :TodayDebits, TodayCredits = :TodayCredits");
		updateSql.append(", TodayNet = :TodayNet, ShadowBal = :ShadowBal,  AcBalance = :AcBalance");
		updateSql.append(" Where AccountId = :AccountId AND PostDate = :PostDate");

		logger.debug("updateSql: " + updateSql.toString());
		SqlParameterSource[] beanParameters = SqlParameterSourceUtils.createBatch(accountHistList.toArray());

		try {
			this.namedParameterJdbcTemplate.batchUpdate(updateSql.toString(), beanParameters);
			isTxnSuccess = true;
		} catch (Exception e) {
		}

		logger.debug("Leaving");
		return isTxnSuccess;
	}

	@Override
	public boolean saveOrUpdate(AccountsHistory accountHist) {

		int recordCount = 0;

		//PREPARE BOTH UPDATE. and Insert Statements and make available for exception handling
		StringBuilder updateSql = new StringBuilder("Update AccountsHistory Set ");
		updateSql.append(" TodayDebits = (TodayDebits + :TodayDebits), ");
		updateSql.append(" TodayCredits = (TodayCredits + :TodayCredits), ");
		updateSql.append(" TodayNet = (TodayNet + :TodayNet), ");
		updateSql.append(" ShadowBal = (ShadowBal + :ShadowBal), ");
		updateSql.append(" AcBalance = (AcBalance + :AcBalance) ");
		updateSql.append(" Where AccountId = :AccountId AND PostDate = :PostDate");

		StringBuilder insertSql = new StringBuilder("Insert Into AccountsHistory");
		insertSql.append(" (AccountId, PostDate, TodayDebits, TodayCredits, ");
		insertSql.append(" TodayNet, ShadowBal, AcBalance ");
		insertSql.append(" Values(:AccountId, :PostDate, :TodayDebits, :TodayCredits, ");
		insertSql.append(" :TodayNet, :ShadowBal, :AcBalance)");

		//TRY UPDATE.
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(accountHist);
		recordCount = this.namedParameterJdbcTemplate.update(updateSql.toString(), beanParameters);

		if (recordCount > 0) {
			return true;
		}

		//UPDATE FAILS TRY INSERT
		try {
			this.namedParameterJdbcTemplate.update(insertSql.toString(), beanParameters);
			return true;
		} catch (DuplicateKeyException e) {
			//Due to huge transactions hit record j=has been created between update and insert statements. SO update now
			recordCount = this.namedParameterJdbcTemplate.update(updateSql.toString(), beanParameters);

			if (recordCount > 0) {
				return true;
			}

		}

		return false;

	}
}