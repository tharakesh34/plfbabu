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
 * FileName    		:  OtherBankFinanceTypeDAOImpl.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  03-04-2015    														*
 *                                                                  						*
 * Modified Date    :  03-04-2015    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 03-04-2015       Pennant	                 0.1                                            * 
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

import com.pennant.backend.dao.applicationmaster.OtherBankFinanceTypeDAO;
import com.pennant.backend.dao.impl.BasisCodeDAO;
import com.pennant.backend.model.applicationmaster.OtherBankFinanceType;
import com.pennanttech.pennapps.core.ConcurrencyException;
import com.pennanttech.pff.core.DependencyFoundException;
import com.pennanttech.pff.core.Literal;
import com.pennanttech.pff.core.TableType;
import com.pennanttech.pff.core.util.QueryUtil;

/**
 * DAO methods implementation for the <b>OtherBankFinanceType model</b> class.<br>
 * 
 */
public class OtherBankFinanceTypeDAOImpl extends BasisCodeDAO<OtherBankFinanceType> implements OtherBankFinanceTypeDAO {
	private static Logger logger = Logger.getLogger(OtherBankFinanceTypeDAOImpl.class);
	
	// Spring Named JDBC Template
	private NamedParameterJdbcTemplate namedParameterJdbcTemplate;
	
	public OtherBankFinanceTypeDAOImpl(){
		super();
	}
	

	/**
	 * Fetch the Record  Other Bank Finance Type details by key field
	 * 
	 * @param id (String)
	 * @param  type (String)
	 * 			""/_Temp/_View          
	 * @return OtherBankFinanceType
	 */
	@Override
	public OtherBankFinanceType getOtherBankFinanceTypeById(final String id, String type) {
		logger.debug(Literal.ENTERING);
		OtherBankFinanceType otherBankFinanceType = new OtherBankFinanceType();
		
		otherBankFinanceType.setId(id);
		
		StringBuilder selectSql = new StringBuilder("Select FinType, FinTypeDesc, Active");
		selectSql.append(", Version , LastMntBy, LastMntOn, RecordStatus, RoleCode, NextRoleCode, TaskId, NextTaskId, RecordType, WorkflowId");
		if(StringUtils.trimToEmpty(type).contains("View")){
			selectSql.append("");
		}
		selectSql.append(" From OtherBankFinanceType");
		selectSql.append(StringUtils.trimToEmpty(type));
		selectSql.append(" Where FinType =:FinType");
		
		logger.debug("selectSql: " + selectSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(otherBankFinanceType);
		RowMapper<OtherBankFinanceType> typeRowMapper = ParameterizedBeanPropertyRowMapper.newInstance(OtherBankFinanceType.class);
		
		try{
			otherBankFinanceType = this.namedParameterJdbcTemplate.queryForObject(selectSql.toString(), beanParameters, typeRowMapper);	
		}catch (EmptyResultDataAccessException e) {
			logger.warn("Exception: ", e);
			otherBankFinanceType = null;
		}
		logger.debug("Leaving");
		return otherBankFinanceType;
	}
	
	/**
	 * To Set  dataSource
	 * @param dataSource
	 */
	
	public void setDataSource(DataSource dataSource) {
		this.namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
	}
	
	/**
	 * This method Deletes the Record from the OtherBankFinanceTypes or OtherBankFinanceTypes_Temp.
	 * if Record not deleted then throws DataAccessException with  error  41003.
	 * delete Other Bank Finance Type by key FinType
	 * 
	 * @param Other Bank Finance Type (otherBankFinanceType)
	 * @param  type (String)
	 * 			""/_Temp/_View          
	 * @return void
	 * @throws DataAccessException
	 * 
	 */
	public void delete(OtherBankFinanceType otherBankFinanceType, TableType tableType) {
		logger.debug(Literal.ENTERING);
		
		int recordCount = 0;
		
		StringBuilder deleteSql = new StringBuilder("Delete From OtherBankFinanceType");
		deleteSql.append(tableType.getSuffix());
		deleteSql.append(" Where FinType =:FinType");
		deleteSql.append(QueryUtil.getConcurrencyCondition(tableType));
		
		logger.trace(Literal.SQL + deleteSql.toString());

		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(otherBankFinanceType);
		try {
			recordCount = this.namedParameterJdbcTemplate.update(deleteSql.toString(), beanParameters);
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
	 * This method insert new Records into OtherBankFinanceTypes or OtherBankFinanceTypes_Temp.
	 *
	 * save Other Bank Finance Type 
	 * 
	 * @param Other Bank Finance Type (otherBankFinanceType)
	 * @param  type (String)
	 * 			""/_Temp/_View          
	 * @return void
	 * @throws DataAccessException
	 * 
	 */
	
	@Override
	public String save(OtherBankFinanceType otherBankFinanceType, TableType tableType) {
		logger.debug(Literal.ENTERING);
		
		StringBuilder insertSql =new StringBuilder("Insert Into OtherBankFinanceType");
		insertSql.append(tableType.getSuffix());
		insertSql.append(" (FinType, FinTypeDesc, Active");
		insertSql.append(", Version , LastMntBy, LastMntOn, RecordStatus, RoleCode, NextRoleCode, TaskId, NextTaskId, RecordType, WorkflowId)");
		insertSql.append(" Values(:FinType, :FinTypeDesc, :Active");
		insertSql.append(", :Version , :LastMntBy, :LastMntOn, :RecordStatus, :RoleCode, :NextRoleCode, :TaskId, :NextTaskId, :RecordType, :WorkflowId)");
		
		logger.trace(Literal.SQL +  insertSql.toString());
		
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(otherBankFinanceType);
		try{
		this.namedParameterJdbcTemplate.update(insertSql.toString(), beanParameters);
		} catch (DuplicateKeyException e) {
			throw new ConcurrencyException(e);
		}
		logger.debug(Literal.LEAVING);
		return otherBankFinanceType.getId();
	}
	
	/**
	 * This method updates the Record OtherBankFinanceTypes or OtherBankFinanceTypes_Temp.
	 * if Record not updated then throws DataAccessException with  error  41004.
	 * update Other Bank Finance Type by key FinType and Version
	 * 
	 * @param Other Bank Finance Type (otherBankFinanceType)
	 * @param  type (String)
	 * 			""/_Temp/_View          
	 * @return void
	 * @throws DataAccessException
	 * 
	 */
	@Override
	public void update(OtherBankFinanceType otherBankFinanceType, TableType tableType) {
		logger.debug(Literal.ENTERING);
		
		int recordCount = 0;
		
		StringBuilder	updateSql =new StringBuilder("Update OtherBankFinanceType");
		updateSql.append(tableType.getSuffix());
		updateSql.append(" Set FinTypeDesc = :FinTypeDesc, Active = :Active");
		updateSql.append(", Version = :Version , LastMntBy = :LastMntBy, LastMntOn = :LastMntOn, RecordStatus= :RecordStatus, RoleCode = :RoleCode, NextRoleCode = :NextRoleCode, TaskId = :TaskId, NextTaskId = :NextTaskId, RecordType = :RecordType, WorkflowId = :WorkflowId");
		updateSql.append(" Where FinType =:FinType");
		updateSql.append(QueryUtil.getConcurrencyCondition(tableType));
		
		logger.trace(Literal.SQL + updateSql.toString());
		
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(otherBankFinanceType);
		recordCount = this.namedParameterJdbcTemplate.update(updateSql.toString(), beanParameters);
		if (recordCount == 0) {
			throw new ConcurrencyException();
		}

		logger.debug(Literal.LEAVING);
	}


	@Override
	public boolean isDuplicateKey(String finType, TableType tableType) {
		logger.debug(Literal.ENTERING);
		// Prepare the SQL.
		String sql;
		String whereClause = "FinType =:FinType";

		switch (tableType) {
		case MAIN_TAB:
			sql = QueryUtil.getCountQuery("OtherBankFinanceType", whereClause);
			break;
		case TEMP_TAB:
			sql = QueryUtil.getCountQuery("OtherBankFinanceType_Temp", whereClause);
			break;
		default:
			sql = QueryUtil.getCountQuery(new String[] { "OtherBankFinanceType_Temp", "OtherBankFinanceType" }, whereClause);
			break;
		}

		// Execute the SQL, binding the arguments.
		logger.trace(Literal.SQL + sql);
		MapSqlParameterSource paramSource = new MapSqlParameterSource();
		paramSource.addValue("FinType", finType);

		Integer count = namedParameterJdbcTemplate.queryForObject(sql, paramSource, Integer.class);

		boolean exists = false;
		if (count > 0) {
			exists = true;
		}

		logger.debug(Literal.LEAVING);
		return exists;
	}
	
}