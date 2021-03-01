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
 * FileName    		:  BranchCashDetailDAOImpl.java                                         * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  28-02-2018    														*
 *                                                                  						*
 * Modified Date    :  28-02-2018    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 28-02-2018       PENNANT	                 0.1                                            * 
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

import java.math.BigDecimal;
import java.util.Date;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.core.simple.ParameterizedBeanPropertyRowMapper;

import com.pennant.app.util.ErrorUtil;
import com.pennant.backend.dao.cashmanagement.BranchCashDetailDAO;
import com.pennant.backend.model.cashmanagement.BranchCashDetail;
import com.pennant.backend.util.CashManagementConstants;
import com.pennant.backend.util.PennantConstants;
import com.pennanttech.pennapps.core.AppException;
import com.pennanttech.pennapps.core.ConcurrencyException;
import com.pennanttech.pennapps.core.DependencyFoundException;
import com.pennanttech.pennapps.core.jdbc.BasicDao;
import com.pennanttech.pennapps.core.model.ErrorDetail;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pff.core.TableType;
import com.pennanttech.pff.core.util.QueryUtil;

/**
 * Data access layer implementation for <code>BranchCashDetail</code> with set of CRUD operations.
 */
public class BranchCashDetailDAOImpl extends BasicDao<BranchCashDetail> implements BranchCashDetailDAO {
	private static Logger logger = LogManager.getLogger(BranchCashDetailDAOImpl.class);

	public BranchCashDetailDAOImpl() {
		super();
	}

	@Override
	public BranchCashDetail getBranchCashDetail(String branchCode) {
		logger.debug(Literal.ENTERING);

		// Prepare the SQL.
		StringBuilder sql = new StringBuilder("SELECT ");
		sql.append(
				" branchCode, branchCash, AdhocInitiationAmount, AdhocProcessingAmount, AdhocTransitAmount,AutoProcessingAmount, AutoTransitAmount,ReservedAmount,LastEODDate ");
		sql.append(" From BranchCashDetails");
		sql.append(" Where branchCode = :BranchCode");

		// Execute the SQL, binding the arguments.
		logger.trace(Literal.SQL + sql.toString());

		BranchCashDetail branchCashDetail = new BranchCashDetail();
		branchCashDetail.setBranchCode(branchCode);

		SqlParameterSource paramSource = new BeanPropertySqlParameterSource(branchCashDetail);
		RowMapper<BranchCashDetail> rowMapper = ParameterizedBeanPropertyRowMapper.newInstance(BranchCashDetail.class);

		try {
			branchCashDetail = jdbcTemplate.queryForObject(sql.toString(), paramSource, rowMapper);
		} catch (EmptyResultDataAccessException e) {
			logger.error("Exception: ", e);
			branchCashDetail = null;
		}

		logger.debug(Literal.LEAVING);
		return branchCashDetail;
	}

	@Override
	public String save(BranchCashDetail branchCashDetail) {
		logger.debug(Literal.ENTERING);

		// Prepare the SQL.
		StringBuilder sql = new StringBuilder(" insert into BranchCashDetails");
		sql.append(
				" (branchCode, branchCash, AdhocInitiationAmount, AdhocProcessingAmount, AdhocTransitAmount,AutoProcessingAmount, AutoTransitAmount,ReservedAmount) ");
		sql.append(" values(");
		sql.append(
				" :branchCode, :BranchCash, :AdhocInitiationAmount, :AdhocProcessingAmount, :AdhocTransitAmount, :AutoProcessingAmount, :AutoTransitAmount,:ReservedAmount)");

		// Execute the SQL, binding the arguments.
		logger.trace(Literal.SQL + sql.toString());
		SqlParameterSource paramSource = new BeanPropertySqlParameterSource(branchCashDetail);

		try {
			jdbcTemplate.update(sql.toString(), paramSource);
		} catch (DuplicateKeyException e) {
			throw new ConcurrencyException(e);
		}

		logger.debug(Literal.LEAVING);
		return String.valueOf(branchCashDetail.getBranchCode());
	}

	/**
	 * This Method Updates Branch Details values based on the given bean . It added the new given Value to The existing
	 * value for the all fields Example If user send adhocInitiationAmount as 100000 then It add 100000 to
	 * adhocInitiationAmount.
	 * 
	 * @param branch
	 *            Code,transaction Amount and credit
	 */

	@Override
	public void updateBranchCashRequest(BranchCashDetail branchCashDetail) {
		logger.debug(Literal.ENTERING);

		StringBuilder sql = new StringBuilder("update BranchCashDetails");
		sql.append(" Set branchCash = branchCash + :BranchCash,");
		sql.append(" AdhocInitiationAmount = AdhocInitiationAmount + :AdhocInitiationAmount,");
		sql.append(" AdhocProcessingAmount = AdhocProcessingAmount + :AdhocProcessingAmount,");
		sql.append(" AdhocTransitAmount = AdhocTransitAmount + :AdhocTransitAmount,");
		sql.append(" AutoProcessingAmount = AutoProcessingAmount + :AutoProcessingAmount,");
		sql.append(" AutoTransitAmount = AutoTransitAmount + :AutoTransitAmount");
		sql.append(" Where branchCode = :BranchCode");

		// Execute the SQL, binding the arguments.
		logger.trace(Literal.SQL + sql.toString());

		SqlParameterSource paramSource = new BeanPropertySqlParameterSource(branchCashDetail);
		int recordCount = jdbcTemplate.update(sql.toString(), paramSource);

		// Check for the concurrency failure.
		if (recordCount == 0) {
			throw new ConcurrencyException();
		}

		logger.debug(Literal.LEAVING);
	}

	@Override
	public void updateEODDate(String branchCode, Date lastEODDate) {
		logger.debug(Literal.ENTERING);
		BranchCashDetail branchCashDetail = new BranchCashDetail();
		branchCashDetail.setBranchCode(branchCode);
		branchCashDetail.setLastEODDate(lastEODDate);

		StringBuilder sql = new StringBuilder("update BranchCashDetails");
		sql.append(" Set LastEODDate =  :LastEODDate ");
		sql.append(" Where branchCode = :BranchCode ");

		// Execute the SQL, binding the arguments.
		logger.trace(Literal.SQL + sql.toString());

		SqlParameterSource paramSource = new BeanPropertySqlParameterSource(branchCashDetail);
		int recordCount = jdbcTemplate.update(sql.toString(), paramSource);

		// Check for the concurrency failure.
		if (recordCount == 0) {
			throw new ConcurrencyException();
		}

		logger.debug(Literal.LEAVING);
	}

	/**
	 * This Method Updates Only branch Cash for the given branch. If user Set credit as true then, it add the
	 * transactionAmount to Existing bank cash If user Set credit as false then, it subtract transactionAmount from
	 * Existing bank cash
	 * 
	 * @param branch
	 *            Code,transaction Amount and credit
	 */

	@Override
	public void updateBranchCashDetail(String branchCode, BigDecimal transactionAmount, String paymentType) {
		logger.debug(Literal.ENTERING);

		BranchCashDetail branchCashDetail = new BranchCashDetail();
		branchCashDetail.setBranchCode(branchCode);
		StringBuilder sql = new StringBuilder("update BranchCashDetails");

		switch (paymentType) {
		case CashManagementConstants.Add_Disb_Reserve:
			// Add Reserve Amount
			sql.append(" set ReservedAmount = ReservedAmount + :ReservedAmount ");
			branchCashDetail.setReservedAmount(transactionAmount);
			break;
		case CashManagementConstants.Cancel_Disb_Reserve:
			// Add Reserve Amount
			sql.append(" set ReservedAmount = ReservedAmount - :ReservedAmount ");
			branchCashDetail.setReservedAmount(transactionAmount);
			break;
		case CashManagementConstants.Add_CashierPayment:
			// Reduce Branch Cash
			// Resuce Reserve Amount
			sql.append(" set branchCash = branchCash - :branchCash");
			sql.append(" , ReservedAmount = ReservedAmount - :ReservedAmount ");
			branchCashDetail.setReservedAmount(transactionAmount);
			branchCashDetail.setBranchCash(transactionAmount);
			break;

		case CashManagementConstants.Cancel_CashierPayment_AddReserv:
			// Cancel Pay Cash to Customer 
			// Add Reserve Amount
			sql.append(" set branchCash = branchCash + :branchCash");
			sql.append(" , ReservedAmount = ReservedAmount + :ReservedAmount ");
			branchCashDetail.setReservedAmount(transactionAmount);
			branchCashDetail.setBranchCash(transactionAmount);
			break;
		case CashManagementConstants.Add_Receipt_Amount:
			// Add Branch Cash
			sql.append(" set branchCash = branchCash + :branchCash");
			branchCashDetail.setBranchCash(transactionAmount);
			break;
		case CashManagementConstants.Cancel_Receipt_Amount:
			// Reduce Branch Cash
			sql.append(" set branchCash = branchCash - :branchCash");
			branchCashDetail.setBranchCash(transactionAmount);
			break;
		case CashManagementConstants.Add_Cash_To_Pennant:
			// Add Branch Cash
			sql.append(" set branchCash = branchCash + :branchCash");
			branchCashDetail.setBranchCash(transactionAmount);
			break;

		default:
			String[] errorParameters = new String[] { paymentType };
			ErrorDetail errorDetails = new ErrorDetail("CM0102", errorParameters);
			errorDetails = ErrorUtil.getErrorDetail(errorDetails);
			throw new AppException(errorDetails.getCode(), errorDetails.getMessage());
		}

		sql.append(" Where branchCode = :branchCode");

		// Execute the SQL, binding the arguments.
		logger.trace(Literal.SQL + sql.toString());

		SqlParameterSource paramSource = new BeanPropertySqlParameterSource(branchCashDetail);
		int recordCount = jdbcTemplate.update(sql.toString(), paramSource);

		// Check for the concurrency failure.
		if (recordCount == 0) {
			throw new AppException(ErrorUtil
					.getErrorDetail(new ErrorDetail("CM0104", null), PennantConstants.default_Language).getMessage());
		}

		logger.debug(Literal.LEAVING);
	}

	@Override
	public void delete(BranchCashDetail branchCashDetail) {
		logger.debug(Literal.ENTERING);

		// Prepare the SQL.
		StringBuilder sql = new StringBuilder("delete from BranchCashDetails");
		sql.append(" where branchCode = :branchCode ");
		sql.append(QueryUtil.getConcurrencyCondition(TableType.MAIN_TAB));

		// Execute the SQL, binding the arguments.
		logger.trace(Literal.SQL + sql.toString());
		SqlParameterSource paramSource = new BeanPropertySqlParameterSource(branchCashDetail);
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
