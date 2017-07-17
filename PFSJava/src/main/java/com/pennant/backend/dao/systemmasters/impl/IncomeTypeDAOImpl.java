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
 * FileName    		:  IncomeTypeDAOImpl.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  05-05-2011    														*
 *                                                                  						*
 * Modified Date    :  05-05-2011    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 05-05-2011       Pennant	                 0.1                                            * 
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
package com.pennant.backend.dao.systemmasters.impl;

import java.util.List;

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

import com.pennant.backend.dao.impl.BasisCodeDAO;
import com.pennant.backend.dao.systemmasters.IncomeTypeDAO;
import com.pennant.backend.model.systemmasters.IncomeType;
import com.pennanttech.pennapps.core.ConcurrencyException;
import com.pennanttech.pennapps.core.DependencyFoundException;
import com.pennanttech.pff.core.Literal;
import com.pennanttech.pff.core.TableType;
import com.pennanttech.pff.core.util.QueryUtil;

/**
 * DAO methods implementation for the <b>IncomeType model</b> class.<br>
 */
public class IncomeTypeDAOImpl extends BasisCodeDAO<IncomeType> implements IncomeTypeDAO {

	private static Logger logger = Logger.getLogger(IncomeTypeDAOImpl.class);

	// Spring Named JDBC Template
	private NamedParameterJdbcTemplate namedParameterJdbcTemplate;

	public IncomeTypeDAOImpl() {
		super();
	}
	
	/**
	 * Fetch the Record Income Types details by key field
	 * 
	 * @param id
	 *            (String)
	 * @param type
	 *            (String) ""/_Temp/_View
	 * @return IncomeType
	 */
	@Override
	public IncomeType getIncomeTypeById(final String id,String incomeExpense,String category, String type) {
		logger.debug("Entering");
		
		IncomeType incomeType = new IncomeType();
		incomeType.setId(id);
		incomeType.setCategory(category);
		incomeType.setIncomeExpense(incomeExpense);
		StringBuilder selectSql = new StringBuilder();

		selectSql.append("SELECT IncomeExpense,Category,IncomeTypeCode, IncomeTypeDesc,Margin ,IncomeTypeIsActive, " );
		if(type.contains("View")){
			selectSql.append("lovDescCategoryName,");
		}
		selectSql.append(" Version, LastMntOn, LastMntBy,RecordStatus, RoleCode, NextRoleCode, TaskId, NextTaskId, RecordType, WorkflowId" );
		selectSql.append(" FROM  BMTIncomeTypes");
		selectSql.append(StringUtils.trimToEmpty(type));
		selectSql.append(" Where IncomeTypeCode =:IncomeTypeCode and IncomeExpense=:IncomeExpense and Category=:Category") ;
				
		logger.debug("selectSql: " + selectSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(incomeType);
		RowMapper<IncomeType> typeRowMapper = ParameterizedBeanPropertyRowMapper.newInstance(IncomeType.class);

		try {
			incomeType = this.namedParameterJdbcTemplate.queryForObject(selectSql.toString(), beanParameters, typeRowMapper);
		} catch (EmptyResultDataAccessException e) {
			logger.error("Exception: ", e);
			incomeType = null;
		}
		
		logger.debug("Leaving");
		return incomeType;
	}
	
	/**
	 * Fetch the Record Income Types details 
	 * 
	 * @param id
	 *            (String)
	 * @param type
	 *            (String) ""/_Temp/_View
	 * @return IncomeType
	 */
	@Override
	public List<IncomeType> getIncomeTypeList() {
		logger.debug("Entering");
		
		StringBuilder selectSql = new StringBuilder();
		selectSql.append("SELECT IncomeExpense,Category,IncomeTypeCode, IncomeTypeDesc,Margin ,IncomeTypeIsActive, lovDescCategoryName " );
		selectSql.append(" FROM  BMTIncomeTypes_AView");
				
		logger.debug("selectSql: " + selectSql.toString());
		RowMapper<IncomeType> typeRowMapper = ParameterizedBeanPropertyRowMapper.newInstance(IncomeType.class);

		logger.debug("Leaving");
		return this.namedParameterJdbcTemplate.getJdbcOperations().query(selectSql.toString(), typeRowMapper);
	}

	@Override
	public boolean isDuplicateKey(String incomeTypeCode, String incomeExpense,
			String category, TableType tableType) {
		logger.debug(Literal.ENTERING);

		// Prepare the SQL.
		String sql;
		String whereClause = "IncomeTypeCode = :incomeTypeCode and IncomeExpense = :incomeExpense and Category = :category";

		switch (tableType) {
		case MAIN_TAB:
			sql = QueryUtil.getCountQuery("BMTIncomeTypes", whereClause);
			break;
		case TEMP_TAB:
			sql = QueryUtil.getCountQuery("BMTIncomeTypes_Temp", whereClause);
			break;
		default:
			sql = QueryUtil.getCountQuery(new String[] { "BMTIncomeTypes_Temp", "BMTIncomeTypes" }, whereClause);
			break;
		}

		// Execute the SQL, binding the arguments.
		logger.trace(Literal.SQL + sql);
		MapSqlParameterSource paramSource = new MapSqlParameterSource();
		paramSource.addValue("incomeTypeCode", incomeTypeCode);
		paramSource.addValue("incomeExpense", incomeExpense);
		paramSource.addValue("category", category);
		
		Integer count = namedParameterJdbcTemplate.queryForObject(sql, paramSource, Integer.class);

		boolean exists = false;
		if (count > 0) {
			exists = true;
		}

		logger.debug(Literal.LEAVING);
		return exists;
	}

	@Override
	public String save(IncomeType incomeType, TableType tableType) {
		logger.debug(Literal.ENTERING);

		// Prepare the SQL.
		StringBuilder sql = new StringBuilder("insert into BMTIncomeTypes");
		sql.append(tableType.getSuffix());
		sql.append(" ( IncomeExpense, Category,IncomeTypeCode, IncomeTypeDesc,Margin, IncomeTypeIsActive,");
		sql.append(" Version , LastMntBy, LastMntOn, RecordStatus, RoleCode, NextRoleCode, TaskId, NextTaskId,");
		sql.append(" RecordType, WorkflowId)");
		sql.append(" values(:IncomeExpense,:Category,:IncomeTypeCode, :IncomeTypeDesc,:Margin, :IncomeTypeIsActive, ");
		sql.append(" :Version , :LastMntBy, :LastMntOn, :RecordStatus, :RoleCode, :NextRoleCode, :TaskId, :NextTaskId, ");
		sql.append(" :RecordType, :WorkflowId)");

		// Execute the SQL, binding the arguments.
		logger.trace(Literal.SQL + sql.toString());
		SqlParameterSource paramSource = new BeanPropertySqlParameterSource(incomeType);

		try {
			namedParameterJdbcTemplate.update(sql.toString(), paramSource);
		} catch (DuplicateKeyException e) {
			throw new ConcurrencyException(e);
		}

		logger.debug(Literal.LEAVING);
		return incomeType.getId();
	}
	
	@Override
	public void update(IncomeType incomeType, TableType tableType) {
		logger.debug(Literal.ENTERING);

		// Prepare the SQL, ensure primary key will not be updated.
		StringBuilder sql = new StringBuilder("update BMTIncomeTypes");
		sql.append(tableType.getSuffix());
		sql.append(" set  IncomeTypeDesc = :IncomeTypeDesc, IncomeTypeIsActive = :IncomeTypeIsActive, Margin=:Margin,");
		sql.append(" Version = :Version , LastMntBy = :LastMntBy, LastMntOn = :LastMntOn, " );
		sql.append(" RecordStatus= :RecordStatus, RoleCode = :RoleCode,NextRoleCode = :NextRoleCode, TaskId = :TaskId," );
		sql.append(" NextTaskId = :NextTaskId, RecordType = :RecordType, WorkflowId = :WorkflowId" );
		sql.append(" where IncomeTypeCode =:IncomeTypeCode and IncomeExpense=:IncomeExpense and Category=:Category");
		sql.append(QueryUtil.getConcurrencyCondition(tableType));
		
		// Execute the SQL, binding the arguments.
		logger.trace(Literal.SQL + sql.toString());
		SqlParameterSource paramSource = new BeanPropertySqlParameterSource(incomeType);
		int recordCount = namedParameterJdbcTemplate.update(sql.toString(), paramSource);

		// Check for the concurrency failure.
		if (recordCount == 0) {
			throw new ConcurrencyException();
		}
		
		logger.debug(Literal.LEAVING);
	}

	@Override
	public void delete(IncomeType incomeType, TableType tableType) {
		logger.debug(Literal.ENTERING);

		// Prepare the SQL.
		StringBuilder sql = new StringBuilder("delete from BMTIncomeTypes");
		sql.append(tableType.getSuffix());
		sql.append(" where IncomeTypeCode =:IncomeTypeCode");
		sql.append(QueryUtil.getConcurrencyCondition(tableType));
		
		// Execute the SQL, binding the arguments.
		logger.trace(Literal.SQL + sql.toString());
		SqlParameterSource paramSource = new BeanPropertySqlParameterSource(incomeType);
		int recordCount = 0;
		
		try {
			recordCount = namedParameterJdbcTemplate.update(sql.toString(),paramSource);
		}  catch (DataAccessException e) {
			throw new DependencyFoundException(e);
		}
		
		// Check for the concurrency failure.
		if (recordCount == 0) {
			throw new ConcurrencyException();
		}

		logger.debug(Literal.LEAVING);
	}

	/**
	 * Sets a new <code>JDBC Template</code> for the given data source.
	 * 
	 * @param dataSource
	 *            the dataSource to set
	 */
	public void setDataSource(DataSource dataSource) {
		this.namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
	}
}