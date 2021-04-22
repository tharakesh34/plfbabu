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
 * FileName    		:  FinTypeExpenseDAOImpl.java                                           * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  20-12-2017    														*
 *                                                                  						*
 * Modified Date    :  			    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 20-12-2017       Pennant	                 0.1                                            * 
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

package com.pennant.backend.dao.rmtmasters.impl;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.PreparedStatementSetter;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;

import com.pennant.backend.dao.rmtmasters.FinTypeExpenseDAO;
import com.pennant.backend.model.rmtmasters.FinTypeExpense;
import com.pennanttech.pennapps.core.ConcurrencyException;
import com.pennanttech.pennapps.core.DependencyFoundException;
import com.pennanttech.pennapps.core.jdbc.SequenceDao;
import com.pennanttech.pennapps.core.resource.Literal;

/**
 * DAO methods implementation for the <b>FinTypeExpense model</b> class.<br>
 * 
 */
public class FinTypeExpenseDAOImpl extends SequenceDao<FinTypeExpense> implements FinTypeExpenseDAO {
	private static Logger logger = LogManager.getLogger(FinTypeExpenseDAOImpl.class);

	public FinTypeExpenseDAOImpl() {
		super();
	}

	/**
	 * This method set the Work Flow id based on the module name and return the new FinTypeExpense
	 * 
	 * @return FinTypeExpense
	 */
	@Override
	public FinTypeExpense getFinTypeExpense() {
		logger.debug("Entering");
		FinTypeExpense finTypeExpense = new FinTypeExpense("");
		logger.debug("Leaving");
		return finTypeExpense;
	}

	/**
	 * This method get the module from method getFinTypeExpense() and set the new record flag as true and return
	 * FinTypeExpense()
	 * 
	 * @return FinTypeExpense
	 */
	@Override
	public FinTypeExpense getNewFinTypeExpense() {
		logger.debug("Entering");
		FinTypeExpense finTypeExpense = getFinTypeExpense();
		finTypeExpense.setNewRecord(true);
		logger.debug("Leaving");
		return finTypeExpense;
	}

	/**
	 * Fetch the Record Finance Types details by key field
	 * 
	 * @param id
	 *            (String)
	 * @param type
	 *            (String) ""/_Temp/_View
	 * @return FinTypeExpense
	 */
	@Override
	public FinTypeExpense getFinTypeExpenseByID(FinTypeExpense finTypeExpense, String type) {
		logger.debug("Entering");

		StringBuilder selectSql = new StringBuilder("SELECT FinType,ExpenseTypeID, CalculationType, ");
		selectSql.append(" Amount, Percentage, CalculateOn, AmortReq, TaxApplicable, Active,");
		selectSql.append(" Version, LastMntBy, LastMntOn, RecordStatus,");
		selectSql.append(" RoleCode, NextRoleCode, TaskId, NextTaskId, RecordType, WorkflowId, FinEvent");
		if (type.contains("View")) {
			selectSql.append(" ,ExpenseTypeCode, ExpenseTypeDesc");
		}
		selectSql.append(" FROM FinTypeExpenses");
		selectSql.append(StringUtils.trimToEmpty(type));
		selectSql.append(" Where FinType = :FinType And FinTypeExpenseID = :FinTypeExpenseID");

		logger.debug("selectListSql: " + selectSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(finTypeExpense);
		RowMapper<FinTypeExpense> typeRowMapper = BeanPropertyRowMapper.newInstance(FinTypeExpense.class);

		try {
			finTypeExpense = this.jdbcTemplate.queryForObject(selectSql.toString(), beanParameters, typeRowMapper);
		} catch (EmptyResultDataAccessException e) {
			finTypeExpense = null;
		}
		logger.debug("Leaving");
		return finTypeExpense;
	}

	@Override
	public List<FinTypeExpense> getFinTypeExpenseListByFinType(String finType, String type) {
		logger.debug(Literal.ENTERING);

		StringBuilder sql = getSqlQuery(type);
		sql.append(" Where FinType = ?");

		logger.debug(Literal.SQL + sql.toString());

		FinTypeExpenseRowMapper rowMapper = new FinTypeExpenseRowMapper(type);

		try {
			return this.jdbcOperations.query(sql.toString(), new PreparedStatementSetter() {

				@Override
				public void setValues(PreparedStatement ps) throws SQLException {
					int index = 1;
					ps.setString(index++, finType);
				}
			}, rowMapper);
		} catch (EmptyResultDataAccessException e) {
			logger.warn(Literal.EXCEPTION, e);
		}

		logger.debug(Literal.LEAVING);
		return new ArrayList<>();
	}

	public List<FinTypeExpense> getLoanQueueExpenseListByFinType(String finType, String type) {
		logger.debug(Literal.ENTERING);

		StringBuilder sql = getSqlQuery(type);
		sql.append(" Where FinType = ? and Active = ?");

		logger.debug(Literal.SQL + sql.toString());

		FinTypeExpenseRowMapper rowMapper = new FinTypeExpenseRowMapper(type);

		try {
			return this.jdbcOperations.query(sql.toString(), new PreparedStatementSetter() {

				@Override
				public void setValues(PreparedStatement ps) throws SQLException {
					int index = 1;
					ps.setString(index++, finType);
					ps.setInt(index++, 1);
				}
			}, rowMapper);
		} catch (EmptyResultDataAccessException e) {
			logger.warn(Literal.EXCEPTION, e);
		}
		logger.debug(Literal.LEAVING);
		return new ArrayList<>();
	}

	private StringBuilder getSqlQuery(String type) {
		StringBuilder sql = new StringBuilder("Select");
		sql.append(" FinType, FinTypeExpenseID, ExpenseTypeID, CalculationType, Amount, Percentage");
		sql.append(", CalculateOn, AmortReq, TaxApplicable, Active, Version, LastMntBy, LastMntOn");
		sql.append(", RecordStatus, RoleCode, NextRoleCode, TaskId, NextTaskId, RecordType, WorkflowId");
		sql.append(", FinEvent");

		if (type.contains("View")) {
			sql.append(", ExpenseTypeCode, ExpenseTypeDesc");
		}

		sql.append(" From FinTypeExpenses");
		sql.append(StringUtils.trimToEmpty(type));

		return sql;
	}

	/**
	 * This method insert new Records into RMTFinTypeExpenses or RMTFinTypeExpenses_Temp.
	 * 
	 * save FinTypeExpense
	 * 
	 * @param FinTypeExpense
	 *            (FinTypeExpense)
	 * @param type
	 *            (String) ""/_Temp/_View
	 * @return void
	 * @throws DataAccessException
	 * 
	 */

	@Override
	public long save(FinTypeExpense finTypeExpense, String type) {
		logger.debug("Entering ");
		// Get the identity sequence number.
		if (finTypeExpense.getFinTypeExpenseID() <= 0) {
			finTypeExpense.setFinTypeExpenseID(getNextValue("SeqFinTypeExpense"));
		}

		StringBuilder insertSql = new StringBuilder("Insert Into FinTypeExpenses");
		insertSql.append(StringUtils.trimToEmpty(type));
		insertSql.append(" (FinType, FinTypeExpenseID, ExpenseTypeID, CalculationType, Amount,");
		insertSql.append("  Percentage, CalculateOn, AmortReq, TaxApplicable, Active,");
		insertSql.append(" Version , LastMntBy, LastMntOn, RecordStatus, RoleCode, NextRoleCode,");
		insertSql.append(" TaskId, NextTaskId, RecordType, WorkflowId, FinEvent)");
		insertSql.append(" Values(:FinType, :FinTypeExpenseID, :ExpenseTypeID, :CalculationType, :Amount,");
		insertSql.append(" :Percentage, :CalculateOn, :AmortReq, :TaxApplicable, :Active,");
		insertSql.append(" :Version , :LastMntBy, :LastMntOn, :RecordStatus, :RoleCode,");
		insertSql.append(" :NextRoleCode, :TaskId, :NextTaskId, :RecordType, :WorkflowId, :FinEvent)");

		logger.debug("insertSql: " + insertSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(finTypeExpense);
		this.jdbcTemplate.update(insertSql.toString(), beanParameters);
		logger.debug("Leaving ");
		return finTypeExpense.getId();
	}

	/**
	 * This method updates the Record RMTFinTypeExpenses or RMTFinTypeExpenses_Temp. if Record not updated then throws
	 * DataAccessException with error 41004. update Finance Types by key FinType and Version
	 * 
	 * @param FinTypeExpense
	 *            (FinTypeExpense)
	 * @param type
	 *            (String) ""/_Temp/_View
	 * @return void
	 * @throws DataAccessException
	 * 
	 */

	@Override
	public void update(FinTypeExpense finTypeExpense, String type) {
		int recordCount = 0;
		logger.debug("Entering ");

		StringBuilder updateSql = new StringBuilder("Update FinTypeExpenses");
		updateSql.append(StringUtils.trimToEmpty(type));
		updateSql.append(" Set CalculationType = :CalculationType, Amount = :Amount,Percentage = :Percentage, ");
		updateSql.append(
				" CalculateOn = :CalculateOn, AmortReq = :AmortReq, TaxApplicable = :TaxApplicable, Active = :Active,  ");
		updateSql.append(" Version = :Version , LastMntBy = :LastMntBy, LastMntOn = :LastMntOn,");
		updateSql.append(
				" RecordStatus= :RecordStatus, RoleCode = :RoleCode, NextRoleCode = :NextRoleCode, TaskId = :TaskId,");
		updateSql.append(" NextTaskId = :NextTaskId, RecordType = :RecordType, WorkflowId = :WorkflowId");
		updateSql.append(" Where FinType =:FinType And ExpenseTypeID = :ExpenseTypeID And  FinEvent = :FinEvent");

		/*
		 * if (!type.endsWith("_Temp")) { updateSql.append("  AND Version= :Version-1"); }
		 */
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(finTypeExpense);
		recordCount = this.jdbcTemplate.update(updateSql.toString(), beanParameters);

		if (recordCount <= 0) {
			throw new ConcurrencyException();
		}
		logger.debug("Leaving ");
	}

	/**
	 * This method Deletes the Record from the RMTFinTypeExpenses or RMTFinTypeExpenses_Temp. if Record not deleted then
	 * throws DataAccessException with error 41003. delete Finance Types by key FinType
	 * 
	 * @param FinTypeExpense
	 *            (FinTypeExpense)
	 * @param type
	 *            (String) ""/_Temp/_View
	 * @return void
	 * @throws DataAccessException
	 * 
	 */
	@Override
	public void delete(FinTypeExpense finTypeExpense, String type) {
		logger.debug("Entering");
		int recordCount = 0;

		StringBuilder deleteSql = new StringBuilder("Delete From FinTypeExpenses");
		deleteSql.append(StringUtils.trimToEmpty(type));
		deleteSql.append("  Where FinType =:FinType And FinTypeExpenseID = :FinTypeExpenseID ");
		logger.debug("deleteSql: " + deleteSql.toString());

		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(finTypeExpense);
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
	 * This method initialize the Record.
	 * 
	 * @param FinTypeExpense
	 * 
	 * 
	 *            (FinTypeExpense)
	 * @return FinTypeExpense
	 */

	@Override
	public void deleteByFinType(String finType, String type) {
		logger.debug("Entering");
		FinTypeExpense finTypeExpense = new FinTypeExpense();
		finTypeExpense.setFinType(finType);
		StringBuilder deleteSql = new StringBuilder("Delete From FinTypeExpenses");
		deleteSql.append(StringUtils.trimToEmpty(type));
		deleteSql.append(" Where FinType =:FinType");
		logger.debug("deleteSql: " + deleteSql.toString());

		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(finTypeExpense);

		try {
			this.jdbcTemplate.update(deleteSql.toString(), beanParameters);
		} catch (DataAccessException e) {
			logger.error("Exception: ", e);
		}
		logger.debug("Leaving");
	}

	@Override
	public FinTypeExpense getFinTypeExpenseByFinType(String finType, long expenseTypeId, String type) {
		logger.debug("Entering");
		FinTypeExpense finTypeExpense = null;
		MapSqlParameterSource mapSqlParameterSource = new MapSqlParameterSource();
		mapSqlParameterSource.addValue("FinType", finType);
		mapSqlParameterSource.addValue("ExpenseTypeID", expenseTypeId);
		StringBuilder selectSql = new StringBuilder("SELECT FinType,FinTypeExpenseID,ExpenseTypeID, CalculationType, ");
		selectSql.append(" Amount, Percentage, CalculateOn, AmortReq, TaxApplicable, Active,");
		selectSql.append(" Version, LastMntBy, LastMntOn, RecordStatus,");
		selectSql.append(" RoleCode, NextRoleCode, TaskId, NextTaskId, RecordType, WorkflowId,FinEvent");
		if (type.contains("View")) {
			selectSql.append(" ,ExpenseTypeCode, ExpenseTypeDesc");
		}
		selectSql.append(" FROM FinTypeExpenses");
		selectSql.append(StringUtils.trimToEmpty(type));
		selectSql.append(" Where FinType = :FinType And ExpenseTypeID = :ExpenseTypeID");

		logger.debug("selectListSql: " + selectSql.toString());
		RowMapper<FinTypeExpense> typeRowMapper = BeanPropertyRowMapper.newInstance(FinTypeExpense.class);

		try {
			finTypeExpense = this.jdbcTemplate.queryForObject(selectSql.toString(), mapSqlParameterSource,
					typeRowMapper);
		} catch (EmptyResultDataAccessException e) {
			finTypeExpense = null;
		}
		logger.debug("Leaving");
		return finTypeExpense;

	}

	/**
	 * Method for validating customers in Customer Group
	 * 
	 */
	@Override
	public boolean expenseExistingFinTypeExpense(long expenseId, String type) {
		logger.debug("Entering");

		int count = 0;
		MapSqlParameterSource mapSqlParameterSource = new MapSqlParameterSource();
		mapSqlParameterSource.addValue("ExpenseTypeID", expenseId);

		StringBuilder selectSql = new StringBuilder("SELECT  COUNT(*)  FROM  FinTypeExpenses");
		selectSql.append(StringUtils.trimToEmpty(type));
		selectSql.append(" Where ExpenseTypeID = :ExpenseTypeID");

		logger.debug("selectSql: " + selectSql.toString());
		try {
			count = this.jdbcTemplate.queryForObject(selectSql.toString(), mapSqlParameterSource, Integer.class);
		} catch (EmptyResultDataAccessException e) {
			logger.warn("Exception: ", e);
			count = 0;
		}
		logger.debug("Leaving");
		return count > 0 ? true : false;
	}

	public class FinTypeExpenseRowMapper implements RowMapper<FinTypeExpense> {
		private String type;

		public FinTypeExpenseRowMapper(String type) {
			this.type = type;
		}

		@Override
		public FinTypeExpense mapRow(ResultSet rs, int rowNum) throws SQLException {
			FinTypeExpense fte = new FinTypeExpense();

			fte.setFinType(rs.getString("FinType"));
			fte.setFinTypeExpenseID(rs.getLong("FinTypeExpenseID"));
			fte.setExpenseTypeID(rs.getLong("ExpenseTypeID"));
			fte.setCalculationType(rs.getString("CalculationType"));
			fte.setAmount(rs.getBigDecimal("Amount"));
			fte.setPercentage(rs.getBigDecimal("Percentage"));
			fte.setCalculateOn(rs.getString("CalculateOn"));
			fte.setAmortReq(rs.getBoolean("AmortReq"));
			fte.setTaxApplicable(rs.getBoolean("TaxApplicable"));
			fte.setActive(rs.getBoolean("Active"));
			fte.setVersion(rs.getInt("Version"));
			fte.setLastMntBy(rs.getLong("LastMntBy"));
			fte.setLastMntOn(rs.getTimestamp("LastMntOn"));
			fte.setRecordStatus(rs.getString("RecordStatus"));
			fte.setRoleCode(rs.getString("RoleCode"));
			fte.setNextRoleCode(rs.getString("NextRoleCode"));
			fte.setTaskId(rs.getString("TaskId"));
			fte.setNextTaskId(rs.getString("NextTaskId"));
			fte.setRecordType(rs.getString("RecordType"));
			fte.setWorkflowId(rs.getLong("WorkflowId"));
			fte.setFinEvent(rs.getString("FinEvent"));

			if (type.contains("View")) {
				fte.setExpenseTypeCode(rs.getString("ExpenseTypeCode"));
				fte.setExpenseTypeDesc(rs.getString("ExpenseTypeDesc"));
			}

			return fte;
		}

	}

}