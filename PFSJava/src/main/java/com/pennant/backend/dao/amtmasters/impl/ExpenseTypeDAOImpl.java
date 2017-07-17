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
 * FileName    		:  ExpenseTypeDAOImpl.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  29-09-2011    														*
 *                                                                  						*
 * Modified Date    :  29-09-2011    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 29-09-2011       Pennant	                 0.1                                            * 
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
package com.pennant.backend.dao.amtmasters.impl;

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

import com.pennant.backend.dao.amtmasters.ExpenseTypeDAO;
import com.pennant.backend.dao.impl.BasisNextidDaoImpl;
import com.pennant.backend.model.amtmasters.ExpenseType;
import com.pennanttech.pennapps.core.ConcurrencyException;
import com.pennanttech.pff.core.DependencyFoundException;

/**
 * DAO methods implementation for the <b>ExpenseType model</b> class.<br>
 * 
 */
public class ExpenseTypeDAOImpl extends BasisNextidDaoImpl<ExpenseType> implements ExpenseTypeDAO {
	private static Logger logger = Logger.getLogger(ExpenseTypeDAOImpl.class);

	// Spring Named JDBC Template
	private NamedParameterJdbcTemplate namedParameterJdbcTemplate;

	public ExpenseTypeDAOImpl() {
		super();
	}

	/**
	 * Fetch the Record Expense Type Details details by key field
	 * 
	 * @param id
	 *            (int)
	 * @param type
	 *            (String) ""/_Temp/_View
	 * @return ExpenseType
	 */
	@Override
	public ExpenseType getExpenseTypeById(final long id, String type) {
		logger.debug("Entering");
		ExpenseType expenseType = new ExpenseType();
		expenseType.setId(id);

		StringBuilder selectSql = new StringBuilder();
		selectSql.append("Select ExpenceTypeId, ExpenceTypeName, ExpenseFor, ");
		selectSql.append(" Version , LastMntBy, LastMntOn, RecordStatus, RoleCode, ");
		selectSql.append(" NextRoleCode, TaskId, NextTaskId, RecordType, WorkflowId");
		selectSql.append(" From AMTExpenseType");
		selectSql.append(StringUtils.trimToEmpty(type));
		selectSql.append(" Where ExpenceTypeId =:ExpenceTypeId");
		logger.debug("selectSql: " + selectSql.toString());

		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(expenseType);
		RowMapper<ExpenseType> typeRowMapper = ParameterizedBeanPropertyRowMapper.newInstance(ExpenseType.class);

		try {
			expenseType = this.namedParameterJdbcTemplate.queryForObject(selectSql.toString(), beanParameters,
					typeRowMapper);
		} catch (EmptyResultDataAccessException e) {
			logger.warn("Exception: ", e);
			expenseType = null;
		}
		logger.debug("Leaving");
		return expenseType;
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
	 * This method Deletes the Record from the AMTExpenseType or
	 * AMTExpenseType_Temp. if Record not deleted then throws
	 * DataAccessException with error 41003. delete Expense Type Details by key
	 * ExpenceTypeId
	 * 
	 * @param Expense
	 *            Type Details (expenseType)
	 * @param type
	 *            (String) ""/_Temp/_View
	 * @return void
	 * @throws DataAccessException
	 * 
	 */
	public void delete(ExpenseType expenseType, String type) {
		logger.debug("Entering");
		int recordCount = 0;
		StringBuilder deleteSql = new StringBuilder();
		deleteSql.append("Delete From AMTExpenseType");
		deleteSql.append(StringUtils.trimToEmpty(type));
		deleteSql.append(" Where ExpenceTypeId =:ExpenceTypeId ");
		logger.debug("deleteSql: " + deleteSql.toString());

		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(expenseType);

		try {
			recordCount = this.namedParameterJdbcTemplate.update(deleteSql.toString(), beanParameters);
			if (recordCount <= 0) {
				throw new ConcurrencyException();
			}
		} catch (DataAccessException e) {
			throw new DependencyFoundException(e);
		}
		logger.debug("Leaving");
	}

	/**
	 * This method insert new Records into AMTExpenseType or
	 * AMTExpenseType_Temp. it fetches the available Sequence form
	 * SeqAMTExpenseType by using getNextidviewDAO().getNextId() method.
	 * 
	 * save Expense Type Details
	 * 
	 * @param Expense
	 *            Type Details (expenseType)
	 * @param type
	 *            (String) ""/_Temp/_View
	 * @return void
	 * @throws DataAccessException
	 * 
	 */

	@Override
	public long save(ExpenseType expenseType, String type) {
		logger.debug("Entering");
		if (expenseType.getId() == Long.MIN_VALUE) {
			expenseType.setId(getNextidviewDAO().getNextId("SeqAMTExpenseType"));
			logger.debug("get NextID:" + expenseType.getId());
		}

		StringBuilder insertSql = new StringBuilder();
		insertSql.append("Insert Into AMTExpenseType");
		insertSql.append(StringUtils.trimToEmpty(type));
		insertSql.append(" (ExpenceTypeId, ExpenceTypeName, ExpenseFor, ");
		insertSql.append(" Version , LastMntBy, LastMntOn, RecordStatus, RoleCode, ");
		insertSql.append(" NextRoleCode, TaskId, NextTaskId, RecordType, WorkflowId)");
		insertSql.append(" Values(:ExpenceTypeId, :ExpenceTypeName, :ExpenseFor, ");
		insertSql.append(" :Version , :LastMntBy, :LastMntOn, :RecordStatus, :RoleCode, ");
		insertSql.append(" :NextRoleCode, :TaskId, :NextTaskId, :RecordType, :WorkflowId)");
		logger.debug("insertSql: " + insertSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(expenseType);
		this.namedParameterJdbcTemplate.update(insertSql.toString(), beanParameters);

		logger.debug("Leaving");
		return expenseType.getId();
	}

	/**
	 * This method updates the Record AMTExpenseType or AMTExpenseType_Temp. if
	 * Record not updated then throws DataAccessException with error 41004.
	 * update Expense Type Details by key ExpenceTypeId and Version
	 * 
	 * @param Expense
	 *            Type Details (expenseType)
	 * @param type
	 *            (String) ""/_Temp/_View
	 * @return void
	 * @throws DataAccessException
	 * 
	 */
	@Override
	public void update(ExpenseType expenseType, String type) {
		int recordCount = 0;
		logger.debug("Entering");
		StringBuilder updateSql = new StringBuilder();

		updateSql.append("Update AMTExpenseType");
		updateSql.append(StringUtils.trimToEmpty(type));
		updateSql
				.append(" Set ExpenceTypeId = :ExpenceTypeId, ExpenceTypeName = :ExpenceTypeName, ExpenseFor = :ExpenseFor, ");
		updateSql.append(" Version = :Version , LastMntBy = :LastMntBy, LastMntOn = :LastMntOn, ");
		updateSql.append(" RecordStatus= :RecordStatus, RoleCode = :RoleCode, NextRoleCode = :NextRoleCode, ");
		updateSql.append(" TaskId = :TaskId, NextTaskId = :NextTaskId, RecordType = :RecordType, ");
		updateSql.append(" WorkflowId = :WorkflowId");
		updateSql.append(" Where ExpenceTypeId =:ExpenceTypeId");

		if (!type.endsWith("_Temp")) {
			updateSql.append("  AND Version= :Version-1");
		}
		logger.debug("updateSql: " + updateSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(expenseType);
		recordCount = this.namedParameterJdbcTemplate.update(updateSql.toString(), beanParameters);

		if (recordCount <= 0) {
			throw new ConcurrencyException();
		}
		logger.debug("Leaving");
	}
}