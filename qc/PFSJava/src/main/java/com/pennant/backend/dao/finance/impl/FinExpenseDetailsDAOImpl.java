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
 * FileName    		:  FinExpenseDetailsDAOImpl.java                                        * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  17-12-2017    														*
 *                                                                  						*
 * Modified Date    :  17-12-2017    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 17-12-2017       Pennant	                 0.1                                            * 
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

import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.core.simple.ParameterizedBeanPropertyRowMapper;

import com.pennant.backend.dao.finance.FinExpenseDetailsDAO;
import com.pennant.backend.model.expenses.FinExpenseDetails;
import com.pennanttech.pennapps.core.ConcurrencyException;
import com.pennanttech.pennapps.core.jdbc.SequenceDao;
import com.pennanttech.pennapps.core.resource.Literal;

/**
 * DAO methods implementation for the <b>UploadHeader model</b> class.<br>
 * 
 */
public class FinExpenseDetailsDAOImpl extends SequenceDao<FinExpenseDetails> implements FinExpenseDetailsDAO {
	private static Logger logger = Logger.getLogger(FinExpenseDetailsDAOImpl.class);

	public FinExpenseDetailsDAOImpl() {
		super();
	}

	@Override
	public long saveFinExpenseDetails(FinExpenseDetails finExpenseDetails) {
		logger.debug("Entering");

		StringBuilder sql = new StringBuilder();

		if (finExpenseDetails.getFinExpenseId() == Long.MIN_VALUE) {
			finExpenseDetails.setFinExpenseId(getNextValue("SeqFinExpenseDetails"));
			logger.debug("get NextID:" + finExpenseDetails.getFinExpenseId());
		}

		sql.append(" Insert Into FinExpenseDetails");
		sql.append(" (FinExpenseId, FinReference, ExpenseTypeId, Amount,");
		sql.append(
				" Version, LastMntBy, LastMntOn, RecordStatus, RoleCode, NextRoleCode, TaskId, NextTaskId, RecordType, WorkflowId)");
		sql.append(" Values(:FinExpenseId, :FinReference, :ExpenseTypeId, :Amount,");
		sql.append(
				" :Version, :LastMntBy, :LastMntOn, :RecordStatus, :RoleCode, :NextRoleCode, :TaskId, :NextTaskId, :RecordType, :WorkflowId)");

		logger.debug("sql: " + sql.toString());

		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(finExpenseDetails);
		this.jdbcTemplate.update(sql.toString(), beanParameters);

		logger.debug("Leaving");

		return finExpenseDetails.getFinExpenseId();
	}

	@Override
	public FinExpenseDetails getFinExpenseDetailsByReference(String finReference, long expenseTypeId) {
		logger.debug("Entering");

		FinExpenseDetails finExpenseDetails = new FinExpenseDetails();
		finExpenseDetails.setFinReference(finReference);
		finExpenseDetails.setExpenseTypeId(expenseTypeId);

		StringBuilder selectSql = new StringBuilder();
		selectSql.append(" SELECT FinExpenseId, FinReference, ExpenseTypeId, Amount,");
		selectSql.append(
				" Version, LastMntBy, LastMntOn, RecordStatus, RoleCode,  NextRoleCode, TaskId, NextTaskId, RecordType, WorkflowId");
		selectSql.append(" From FinExpenseDetails");
		selectSql.append(" WHERE  FinReference = :FinReference And ExpenseTypeId = :ExpenseTypeId");

		logger.debug("selectSql: " + selectSql.toString());

		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(finExpenseDetails);
		RowMapper<FinExpenseDetails> typeRowMapper = ParameterizedBeanPropertyRowMapper
				.newInstance(FinExpenseDetails.class);

		try {
			finExpenseDetails = jdbcTemplate.queryForObject(selectSql.toString(), beanParameters, typeRowMapper);
		} catch (EmptyResultDataAccessException e) {
			logger.error("Exception: ", e);
			finExpenseDetails = null;
		}

		logger.debug(Literal.LEAVING);

		return finExpenseDetails;
	}

	@Override
	public void update(FinExpenseDetails finExpenseDetails) {
		logger.debug(Literal.ENTERING);

		// Prepare the SQL, ensure primary key will not be updated.
		StringBuilder sql = new StringBuilder("update FinExpenseDetails");
		sql.append(" set Amount = :Amount, LastMntOn = :LastMntOn");
		sql.append(" where FinExpenseId = :FinExpenseId");

		// Execute the SQL, binding the arguments.
		logger.trace(Literal.SQL + sql.toString());
		SqlParameterSource paramSource = new BeanPropertySqlParameterSource(finExpenseDetails);
		int recordCount = jdbcTemplate.update(sql.toString(), paramSource);

		// Check for the concurrency failure.
		if (recordCount == 0) {
			throw new ConcurrencyException();
		}

		logger.debug(Literal.LEAVING);
	}

	@Override
	public List<FinExpenseDetails> getFinExpenseDetailsById(String financeRef) {
		logger.debug("Entering");

		FinExpenseDetails finExpenseDetails = new FinExpenseDetails();
		finExpenseDetails.setFinReference(financeRef);

		StringBuilder selectSql = new StringBuilder();
		selectSql.append(" SELECT T1.FinexpenseID,T1.Amount,T2.expensetypecode,t2.expensetypedesc,T1.lastmnton");

		selectSql.append(" From finexpensedetails T1");
		selectSql.append(" Inner join expensetypes T2 on T2.expenseTypeid=T1.ExpenseTypeID");
		selectSql.append(" Where T1.FinReference = :FinReference");

		logger.debug("selectSql: " + selectSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(finExpenseDetails);
		RowMapper<FinExpenseDetails> typeRowMapper = ParameterizedBeanPropertyRowMapper
				.newInstance(FinExpenseDetails.class);
		logger.debug("Leaving");

		return this.jdbcTemplate.query(selectSql.toString(), beanParameters, typeRowMapper);
	}

	/**
	 * 
	 */
	public List<FinExpenseDetails> getAMZFinExpenseDetails(String finRef, String type) {
		logger.debug("Entering");

		FinExpenseDetails expenseDetail = new FinExpenseDetails();
		expenseDetail.setFinReference(finRef);

		StringBuilder selectSql = new StringBuilder();
		selectSql.append(" SELECT T1.FinExpenseId, T1.FinReference, T1.ExpenseTypeId, T1.Amount");
		selectSql.append(" From FinExpenseDetails T1");
		selectSql.append(" INNER JOIN ExpenseTypes T2 ON T2.ExpenseTypeId = T1.ExpenseTypeId AND T2.AmortReq = 1");
		selectSql.append(" Where T1.FinReference = :FinReference");

		logger.debug("selectSql: " + selectSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(expenseDetail);
		RowMapper<FinExpenseDetails> typeRowMapper = ParameterizedBeanPropertyRowMapper
				.newInstance(FinExpenseDetails.class);
		logger.debug("Leaving");

		return this.jdbcTemplate.query(selectSql.toString(), beanParameters, typeRowMapper);
	}
}