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

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.core.simple.ParameterizedBeanPropertyRowMapper;

import com.pennant.backend.dao.amtmasters.ExpenseTypeDAO;
import com.pennant.backend.model.amtmasters.ExpenseType;
import com.pennanttech.pennapps.core.ConcurrencyException;
import com.pennanttech.pennapps.core.DependencyFoundException;
import com.pennanttech.pennapps.core.jdbc.SequenceDao;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pff.core.TableType;
import com.pennanttech.pff.core.util.QueryUtil;

/**
 * DAO methods implementation for the <b>ExpenseType model</b> class.<br>
 * 
 */
public class ExpenseTypeDAOImpl extends SequenceDao<ExpenseType> implements ExpenseTypeDAO {
	private static Logger	logger	= Logger.getLogger(ExpenseTypeDAOImpl.class);

	
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
		selectSql.append("Select ExpenseTypeId, ExpenseTypeCode, ExpenseTypeDesc, AmortReq, TaxApplicable, Active,");
		selectSql.append(" Version , LastMntBy, LastMntOn, RecordStatus, RoleCode, ");
		selectSql.append(" NextRoleCode, TaskId, NextTaskId, RecordType, WorkflowId");
		selectSql.append(" From ExpenseTypes");
		selectSql.append(StringUtils.trimToEmpty(type));
		selectSql.append(" Where ExpenseTypeId =:ExpenseTypeId");
		logger.debug("selectSql: " + selectSql.toString());

		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(expenseType);
		RowMapper<ExpenseType> typeRowMapper = ParameterizedBeanPropertyRowMapper.newInstance(ExpenseType.class);

		try {
			expenseType = this.jdbcTemplate.queryForObject(selectSql.toString(), beanParameters,
					typeRowMapper);
		} catch (EmptyResultDataAccessException e) {
			logger.warn("Exception: ", e);
			expenseType = null;
		}
		logger.debug("Leaving");
		return expenseType;
	}

	

	/**
	 * This method Deletes the Record from the AMTExpenseType or AMTExpenseType_Temp. if Record not deleted then throws
	 * DataAccessException with error 41003. delete Expense Type Details by key ExpenceTypeId
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
		deleteSql.append("Delete From ExpenseTypes");
		deleteSql.append(StringUtils.trimToEmpty(type));
		deleteSql.append(" Where ExpenseTypeId =:ExpenseTypeId ");
		logger.debug("deleteSql: " + deleteSql.toString());

		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(expenseType);

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
	 * This method insert new Records into AMTExpenseType or AMTExpenseType_Temp. it fetches the available Sequence form
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
			expenseType.setId(getNextId("SeqAMTExpenseType"));
			logger.debug("get NextID:" + expenseType.getId());
		}

		StringBuilder insertSql = new StringBuilder();
		insertSql.append("Insert Into ExpenseTypes");
		insertSql.append(StringUtils.trimToEmpty(type));
		insertSql.append(" (ExpenseTypeId, ExpenseTypeCode, ExpenseTypeDesc , AmortReq, TaxApplicable, Active, ");
		insertSql.append(" Version , LastMntBy, LastMntOn, RecordStatus, RoleCode, ");
		insertSql.append(" NextRoleCode, TaskId, NextTaskId, RecordType, WorkflowId)");
		insertSql.append(" Values(:ExpenseTypeId, :ExpenseTypeCode, :ExpenseTypeDesc, :AmortReq, :TaxApplicable, :Active,");
		insertSql.append(" :Version , :LastMntBy, :LastMntOn, :RecordStatus, :RoleCode, ");
		insertSql.append(" :NextRoleCode, :TaskId, :NextTaskId, :RecordType, :WorkflowId)");
		logger.debug("insertSql: " + insertSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(expenseType);
		this.jdbcTemplate.update(insertSql.toString(), beanParameters);

		logger.debug("Leaving");
		return expenseType.getId();
	}

	/**
	 * This method updates the Record AMTExpenseType or AMTExpenseType_Temp. if Record not updated then throws
	 * DataAccessException with error 41004. update Expense Type Details by key ExpenceTypeId and Version
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

		updateSql.append("Update ExpenseTypes");
		updateSql.append(StringUtils.trimToEmpty(type));
		updateSql.append(" Set ExpenseTypeId = :ExpenseTypeId, ExpenseTypeCode = :ExpenseTypeCode,");
		updateSql.append(
				"  ExpenseTypeDesc = :ExpenseTypeDesc, AmortReq = :AmortReq, TaxApplicable = :TaxApplicable, Active = :Active, ");
		updateSql.append(" Version = :Version , LastMntBy = :LastMntBy, LastMntOn = :LastMntOn, ");
		updateSql.append(" RecordStatus= :RecordStatus, RoleCode = :RoleCode, NextRoleCode = :NextRoleCode, ");
		updateSql.append(" TaskId = :TaskId, NextTaskId = :NextTaskId, RecordType = :RecordType, ");
		updateSql.append(" WorkflowId = :WorkflowId");
		updateSql.append(" Where ExpenseTypeId =:ExpenseTypeId");

		if (!type.endsWith("_Temp")) {
			updateSql.append("  AND Version= :Version-1");
		}
		logger.debug("updateSql: " + updateSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(expenseType);
		recordCount = this.jdbcTemplate.update(updateSql.toString(), beanParameters);

		if (recordCount <= 0) {
			throw new ConcurrencyException();
		}
		logger.debug("Leaving");
	}

	@Override
	public boolean isDuplicateKey(long expenseTypeId, String expenseTypeCode, TableType tableType) {
		logger.debug(Literal.ENTERING);

		// Prepare the SQL.
		String sql;
		String whereClause = "ExpenseTypeCode = :ExpenseTypeCode and ExpenseTypeId != :ExpenseTypeId";
		switch (tableType) {
		case MAIN_TAB:
			sql = QueryUtil.getCountQuery("ExpenseTypes", whereClause);
			break;
		case TEMP_TAB:
			sql = QueryUtil.getCountQuery("ExpenseTypes_Temp", whereClause);
			break;
		default:
			sql = QueryUtil.getCountQuery(new String[] { "ExpenseTypes_Temp", "ExpenseTypes" }, whereClause);
			break;
		}

		// Execute the SQL, binding the arguments.
		logger.trace(Literal.SQL + sql);
		MapSqlParameterSource paramSource = new MapSqlParameterSource();
		paramSource.addValue("ExpenseTypeId", expenseTypeId);
		paramSource.addValue("ExpenseTypeCode", expenseTypeCode);

		Integer count = jdbcTemplate.queryForObject(sql, paramSource, Integer.class);

		boolean exists = false;
		if (count > 0) {
			exists = true;
		}

		logger.debug(Literal.LEAVING);
		return exists;
	}
	
	@Override
	public long getFinExpenseIdByExpType(String expTypeCode, String type) {
		logger.debug("Entering");
		
		long finExpenseId = Long.MIN_VALUE;
		StringBuilder selectSql = new StringBuilder();
		selectSql.append(" SELECT ExpenseTypeId From ExpenseTypes");
		selectSql.append(StringUtils.trimToEmpty(type));
		selectSql.append(" WHERE  ExpenseTypeCode = :ExpenseTypeCode");
		
		logger.debug("selectSql: " + selectSql.toString());
		MapSqlParameterSource source = new MapSqlParameterSource();
		source.addValue("ExpenseTypeCode", expTypeCode);
		
		try {
			finExpenseId = this.jdbcTemplate.queryForObject(selectSql.toString(), source, Long.class);
		} catch (EmptyResultDataAccessException e) {
			finExpenseId = Long.MIN_VALUE;
		}

		logger.debug("Leaving");
		
		return finExpenseId;
	}
}