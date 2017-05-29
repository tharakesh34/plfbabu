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

import javax.sql.DataSource;

import org.springframework.dao.DuplicateKeyException;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;

import com.pennant.backend.dao.accounts.AccountsHistoryDAO;
import com.pennant.backend.dao.impl.BasisCodeDAO;
import com.pennant.backend.model.accounts.AccountsHistory;

public class AccountsHistoryDAOImpl extends BasisCodeDAO<AccountsHistory> implements AccountsHistoryDAO {

	// Spring Named JDBC Template
	private NamedParameterJdbcTemplate	namedParameterJdbcTemplate;

	public AccountsHistoryDAOImpl() {
		super();
	}
	
	
	/**
	 * To Set  dataSource
	 * @param dataSource
	 */
	
	public void setDataSource(DataSource dataSource) {
		this.namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
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
		insertSql.append(" TodayNet, ShadowBal, AcBalance) ");
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