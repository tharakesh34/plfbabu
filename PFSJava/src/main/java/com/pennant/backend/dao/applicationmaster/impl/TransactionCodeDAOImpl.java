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
 * FileName    		:  TransactionCodeDAOImpl.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  10-11-2011    														*
 *                                                                  						*
 * Modified Date    :  10-11-2011    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 10-11-2011       Pennant	                 0.1                                            * 
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
package com.pennant.backend.dao.applicationmaster.impl;

import javax.sql.DataSource;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.core.simple.ParameterizedBeanPropertyRowMapper;

import com.pennant.backend.dao.applicationmaster.TransactionCodeDAO;
import com.pennant.backend.dao.impl.BasisCodeDAO;
import com.pennant.backend.model.applicationmaster.TransactionCode;
import com.pennanttech.pennapps.core.ConcurrencyException;
import com.pennanttech.pennapps.core.DependencyFoundException;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pff.core.TableType;
import com.pennanttech.pff.core.util.QueryUtil;

/**
 * DAO methods implementation for the <b>TransactionCode model</b> class.<br>
 * 
 */
public class TransactionCodeDAOImpl extends BasisCodeDAO<TransactionCode> implements TransactionCodeDAO {

	private static Logger logger = Logger.getLogger(TransactionCodeDAOImpl.class);

	// Spring Named JDBC Template
	private NamedParameterJdbcTemplate namedParameterJdbcTemplate;

	public TransactionCodeDAOImpl() {
		super();
	}

	/**
	 * Fetch the Record Transaction Code Detail details by key field
	 * 
	 * @param id
	 *            (String)
	 * @param type
	 *            (String) ""/_Temp/_View
	 * @return TransactionCode
	 */
	@Override
	public TransactionCode getTransactionCodeById(final String id, String type) {
		logger.debug("Entering");
		TransactionCode transactionCode = new TransactionCode();

		transactionCode.setId(id);

		StringBuilder selectSql = new StringBuilder("select TranCode, TranDesc, TranType, TranIsActive,");
		selectSql.append(" Version , LastMntBy, LastMntOn, RecordStatus, RoleCode, NextRoleCode,");
		selectSql.append(" TaskId, NextTaskId, RecordType, WorkflowId");
		selectSql.append(" From BMTTransactionCode");
		selectSql.append(StringUtils.trimToEmpty(type));
		selectSql.append(" where TranCode =:TranCode");

		logger.debug("selectSql: " + selectSql.toString());
		SqlParameterSource paramSource = new BeanPropertySqlParameterSource(transactionCode);
		RowMapper<TransactionCode> typeRowMapper = ParameterizedBeanPropertyRowMapper
				.newInstance(TransactionCode.class);

		try {
			transactionCode = this.namedParameterJdbcTemplate.queryForObject(selectSql.toString(), paramSource,
					typeRowMapper);
		} catch (EmptyResultDataAccessException e) {
			logger.warn("Exception: ", e);
			transactionCode = null;
		}
		logger.debug("Leaving");
		return transactionCode;
	}

	@Override
	public boolean isDuplicateKey(String tranCode, TableType tableType) {
		logger.debug(Literal.ENTERING);

		// Prepare the SQL.
		String sql;
		String whereClause = "TranCode = :TranCode";

		switch (tableType) {
		case MAIN_TAB:
			sql = QueryUtil.getCountQuery("BMTTransactionCode", whereClause);
			break;
		case TEMP_TAB:
			sql = QueryUtil.getCountQuery("BMTTransactionCode_Temp", whereClause);
			break;
		default:
			sql = QueryUtil
					.getCountQuery(new String[] { "BMTTransactionCode_Temp", "BMTTransactionCode" }, whereClause);
			break;
		}

		// Execute the SQL, binding the arguments.
		logger.trace(Literal.SQL + sql);
		MapSqlParameterSource paramSource = new MapSqlParameterSource();
		paramSource.addValue("TranCode", tranCode);

		Integer count = namedParameterJdbcTemplate.queryForObject(sql, paramSource, Integer.class);

		boolean exists = false;
		if (count > 0) {
			exists = true;
		}

		logger.debug(Literal.LEAVING);
		return exists;
	}

	@Override
	public String save(TransactionCode transactionCode, TableType tableType) {
		logger.debug("Entering");

		StringBuilder sql = new StringBuilder("insert into BMTTransactionCode");
		sql.append(tableType.getSuffix());
		sql.append(" (TranCode, TranDesc, TranType, TranIsActive,");
		sql.append(" Version , LastMntBy, LastMntOn, RecordStatus, RoleCode, NextRoleCode,");
		sql.append(" TaskId, NextTaskId, RecordType, WorkflowId)");
		sql.append(" Values (:TranCode, :TranDesc, :TranType, :TranIsActive,");
		sql.append(" :Version , :LastMntBy, :LastMntOn, :RecordStatus, :RoleCode, :NextRoleCode,");
		sql.append(" :TaskId, :NextTaskId, :RecordType, :WorkflowId)");

		// Execute the SQL, binding the arguments.
		logger.debug("insertSql: " + sql.toString());
		SqlParameterSource paramSource = new BeanPropertySqlParameterSource(transactionCode);

		try {
			namedParameterJdbcTemplate.update(sql.toString(), paramSource);
		} catch (DuplicateKeyException e) {
			throw new ConcurrencyException(e);
		}

		logger.debug(Literal.LEAVING);
		return transactionCode.getId();
	}

	@Override
	public void update(TransactionCode transactionCode, TableType tableType) {
		logger.debug("Entering");
		StringBuilder sql = new StringBuilder("update BMTTransactionCode");
		sql.append(tableType.getSuffix());
		sql.append(" set TranDesc = :TranDesc, TranType = :TranType,");
		sql.append(" TranIsActive = :TranIsActive,");
		sql.append(" Version = :Version , LastMntBy = :LastMntBy, LastMntOn = :LastMntOn,");
		sql.append(" RecordStatus= :RecordStatus, RoleCode = :RoleCode,");
		sql.append(" NextRoleCode = :NextRoleCode, TaskId = :TaskId, NextTaskId = :NextTaskId,");
		sql.append(" RecordType = :RecordType, WorkflowId = :WorkflowId");
		sql.append(" where TranCode =:TranCode");
		sql.append(QueryUtil.getConcurrencyCondition(tableType));

		// Execute the SQL, binding the arguments.
		logger.trace(Literal.SQL + sql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(transactionCode);
		int recordCount = namedParameterJdbcTemplate.update(sql.toString(), beanParameters);

		// Check for the concurrency failure.
		if (recordCount == 0) {
			throw new ConcurrencyException();
		}

		logger.debug(Literal.LEAVING);
	}

	@Override
	public void delete(TransactionCode transactionCode, TableType tableType) {
		logger.debug("Entering");

		// Prepare the SQL.
		StringBuilder sql = new StringBuilder("delete from BMTTransactionCode");
		sql.append(tableType.getSuffix());
		sql.append(" where TranCode =:TranCode");
		sql.append(QueryUtil.getConcurrencyCondition(tableType));

		// Execute the SQL, binding the arguments.
		logger.trace(Literal.SQL + sql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(transactionCode);
		int recordCount = 0;

		try {
			recordCount = namedParameterJdbcTemplate.update(sql.toString(), beanParameters);
		} catch (DataAccessException e) {
			throw new DependencyFoundException(e);
		}

		// Check for the concurrency failure.
		if (recordCount == 0) {
			throw new ConcurrencyException();
		}

		logger.debug(Literal.LEAVING);
	}

	/**
	 * To Set dataSource
	 * 
	 * @param dataSource
	 */
	public void setDataSource(DataSource dataSource) {
		this.namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
	}

}