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
 * * FileName : AccountsDAOImpl.java * * Author : PENNANT TECHONOLOGIES * * Creation Date : 02-01-2012 * * Modified Date
 * : 02-01-2012 * * Description : * *
 ********************************************************************************************
 * Date Author Version Comments *
 ********************************************************************************************
 * 02-01-2012 Pennant 0.1 * * * * * * * * *
 ********************************************************************************************
 */
package com.pennant.backend.dao.accounts.impl;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import org.springframework.dao.DuplicateKeyException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSourceUtils;

import com.pennant.app.util.SysParamUtil;
import com.pennant.backend.dao.accounts.AccountsHistoryDAO;
import com.pennant.backend.model.accounts.AccountHistoryDetail;
import com.pennant.backend.model.accounts.AccountsHistory;
import com.pennanttech.pennapps.core.jdbc.BasicDao;
import com.pennanttech.pennapps.core.resource.Message;

public class AccountsHistoryDAOImpl extends BasicDao<AccountsHistory> implements AccountsHistoryDAO {

	public AccountsHistoryDAOImpl() {
		super();
	}

	@Override
	public boolean saveOrUpdate(AccountsHistory accountHist) {

		int recordCount = 0;

		// PREPARE BOTH UPDATE. and Insert Statements and make available for exception handling
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

		// TRY UPDATE.
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(accountHist);
		recordCount = this.jdbcTemplate.update(updateSql.toString(), beanParameters);

		if (recordCount > 0) {
			return true;
		}

		// UPDATE FAILS TRY INSERT
		try {
			this.jdbcTemplate.update(insertSql.toString(), beanParameters);
			return true;
		} catch (DuplicateKeyException e) {
			// Due to huge transactions hit record j=has been created between update and insert statements. SO update
			// now
			recordCount = this.jdbcTemplate.update(updateSql.toString(), beanParameters);

			if (recordCount > 0) {
				return true;
			}

		}

		return false;

	}

	/***
	 * Gets the account's closing balance for the specified date.
	 * 
	 * @param accountId The account id.
	 * @param postDate  The posting date.
	 * @return The account's closing balance for the specified date.
	 */
	@Override
	public BigDecimal getClosingBalance(String accountId, Date postDate) {
		StringBuilder sql = new StringBuilder("select AcBalance from AccountsHistory");
		sql.append(" where AccountId = :AccountId and PostDate = (");
		sql.append(" select max(PostDate) from AccountsHistory where AccountId = :AccountId and PostDate < :PostDate");
		sql.append(" )");

		MapSqlParameterSource paramSource = new MapSqlParameterSource();
		paramSource.addValue("AccountId", accountId);
		paramSource.addValue("PostDate", postDate);

		try {
			return jdbcTemplate.queryForObject(sql.toString(), paramSource, BigDecimal.class);
		} catch (EmptyResultDataAccessException e) {
			logger.warn(Message.NO_RECORD_FOUND);
			return BigDecimal.ZERO;
		}
	}

	/***
	 * Gets the account's closing balance for the specified date.
	 * 
	 * @param accountId The account id.
	 * @param postDate  The posting date.
	 * @return The account's closing balance for the specified date.
	 */
	@Override
	public BigDecimal getPrvClosingBalance(String accountId, Date postDate) {
		BigDecimal acBalance = BigDecimal.ZERO;

		StringBuilder sql = new StringBuilder("select AcBalance from AccountsHistory");
		sql.append(" where AccountId = :AccountId and PostDate = :PostDate ");

		MapSqlParameterSource paramSource = new MapSqlParameterSource();
		paramSource.addValue("AccountId", accountId);
		paramSource.addValue("PostDate", postDate);

		try {
			acBalance = jdbcTemplate.queryForObject(sql.toString(), paramSource, BigDecimal.class);
		} catch (EmptyResultDataAccessException e) {
			acBalance = null;
		}

		return acBalance;
	}

	@Override
	public void save(List<AccountHistoryDetail> accountHistDetails) {

		StringBuilder sql = new StringBuilder("Insert into AccountHistoryDetails ");
		sql.append(" (AccountId, PostDate, TodayDebits, TodayCredits, TodayNet, ShadowBal, AcBalance, ");
		sql.append(" OpeningBal, EntityCode, PostBranch, BranchProvince) ");
		sql.append(" Values( :AccountId, :PostDate, :TodayDebits, :TodayCredits, :TodayNet, :ShadowBal,");
		sql.append(" :AcBalance, :OpeningBal, :EntityCode, :PostBranch, :BranchProvince)");

		SqlParameterSource[] beanParameters = SqlParameterSourceUtils.createBatch(accountHistDetails.toArray());
		this.jdbcTemplate.batchUpdate(sql.toString(), beanParameters);

	}

	@Override
	public void update(List<AccountHistoryDetail> accountHistDetails) {

		StringBuilder updateSql = new StringBuilder(
				" UPDATE AccountHistoryDetails T3  SET  OPENINGBAL = T4.AcBalance  ");
		updateSql.append(" FROM ( Select distinct T1.ACCOUNTID, T1.ACBALANCE, T1.POSTDATE, T1.PostBranch, ");
		updateSql.append(" T1.BranchProvince, T1.ENTITYCODE, T1.TodayNet from AccountHistoryDetails T1  ");
		updateSql.append(" INNER JOIN (Select T2.Accountid, BranchProvince, max(T2.postdate) Postdate, ");
		updateSql.append(" PostBranch, Entitycode  from AccountHistoryDetails T2 where T2.postdate < ");
		updateSql.append("'" + SysParamUtil.getAppDate() + "'");
		updateSql.append(" Group by T2.accountid, BranchProvince, EntityCode, PostBranch ) T2   ");
		updateSql.append(" ON T1.accountid = T2.accountid and T1.postdate = T2.postdate And  ");
		updateSql.append(
				" T2.BranchProvince = T1.BranchProvince And T1.PostBranch = T2.PostBranch And T1.ENTITYCODE = T2.EntityCode )T4  ");
		updateSql.append(" WHERE (T4.ACCOUNTID = T3.ACCOUNTID  And T4.BranchProvince = T3.BranchProvince And   ");
		updateSql.append(" T4.PostBranch = T3.PostBranch and T4.EntityCode = T3. Entitycode  ");
		updateSql.append(" And T3.PostDate = '" + SysParamUtil.getAppDate() + "')");

		SqlParameterSource[] beanParameters = SqlParameterSourceUtils.createBatch(accountHistDetails.toArray());
		this.jdbcTemplate.batchUpdate(updateSql.toString(), beanParameters);
	}

	@Override
	public void updateCurrAccHstyDetails(List<AccountHistoryDetail> accountHistDetails) {

		StringBuilder updSql = new StringBuilder("Update ACCOUNTHISTORYDETAILS set ");
		updSql.append(" AcBalance = ( OpeningBal + TodayCredits + TodayDebits ) ,");
		updSql.append(" TodayNet = ( TodayCredits + TodayDebits ) ");
		updSql.append(" where PostDate = '" + SysParamUtil.getAppDate() + "'");

		SqlParameterSource beanParametrs = new BeanPropertySqlParameterSource(accountHistDetails);
		this.jdbcTemplate.update(updSql.toString(), beanParametrs);
	}

}
