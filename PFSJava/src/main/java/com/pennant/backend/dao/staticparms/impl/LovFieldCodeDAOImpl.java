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
 * FileName    		:  LovFieldCodeDAOImpl.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  04-10-2011    														*
 *                                                                  						*
 * Modified Date    :  04-10-2011    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 04-10-2011       Pennant	                 0.1                                            * 
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

package com.pennant.backend.dao.staticparms.impl;


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

import com.pennant.backend.dao.impl.BasisCodeDAO;
import com.pennant.backend.dao.staticparms.LovFieldCodeDAO;
import com.pennant.backend.model.staticparms.LovFieldCode;
import com.pennanttech.pennapps.core.ConcurrencyException;
import com.pennanttech.pennapps.core.DependencyFoundException;

/**
 * DAO methods implementation for the <b>LovFieldCode model</b> class.<br>
 * 
 */
public class LovFieldCodeDAOImpl extends BasisCodeDAO<LovFieldCode> implements LovFieldCodeDAO {

	private static Logger logger = Logger.getLogger(LovFieldCodeDAOImpl.class);

	// Spring Named JDBC Template
	private NamedParameterJdbcTemplate namedParameterJdbcTemplate;

	public LovFieldCodeDAOImpl() {
		super();
	}

	/**
	 * Fetch the Record  Field Code details by key field
	 * 
	 * @param id (String)
	 * @param  type (String)
	 * 			""/_Temp/_View          
	 * @return LovFieldCode
	 */
	@Override
	public LovFieldCode getLovFieldCodeById(final String id, String type) {
		logger.debug("Entering");
		LovFieldCode lovFieldCode = new LovFieldCode();
		lovFieldCode.setId(id);
		StringBuilder selectSql = new StringBuilder();

		selectSql.append("Select FieldCode, FieldCodeDesc, FieldCodeType, FieldEdit,isActive,");
		/*if(type.contains("View")){
			selectSql.append("");
		}*/
		selectSql.append(" Version , LastMntBy, LastMntOn, RecordStatus, RoleCode, NextRoleCode, TaskId, NextTaskId, RecordType, WorkflowId");
		selectSql.append(" From BMTLovFieldCode");
		selectSql.append(StringUtils.trimToEmpty(type));
		selectSql.append(" Where FieldCode =:FieldCode");

		logger.debug("selectSql: " + selectSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(lovFieldCode);
		RowMapper<LovFieldCode> typeRowMapper = ParameterizedBeanPropertyRowMapper.newInstance(LovFieldCode.class);

		try{
			lovFieldCode = this.namedParameterJdbcTemplate.queryForObject(selectSql.toString(), beanParameters, typeRowMapper);	
		}catch (EmptyResultDataAccessException e) {
			logger.warn("Exception: ", e);
			lovFieldCode = null;
		}
		logger.debug("Leaving");
		return lovFieldCode;
	}

	/**
	 * To Set  dataSource
	 * @param dataSource
	 */
	public void setDataSource(DataSource dataSource) {
		this.namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
	}

	/**
	 * This method Deletes the Record from the BMTLovFieldCode or BMTLovFieldCode_Temp.
	 * if Record not deleted then throws DataAccessException with  error  41003.
	 * delete Field Code by key FieldCode
	 * 
	 * @param Field Code (lovFieldCode)
	 * @param  type (String)
	 * 			""/_Temp/_View          
	 * @return void
	 * @throws DataAccessException
	 * 
	 */
	@Override
	public void delete(LovFieldCode lovFieldCode,String type) {
		logger.debug("Entering");
		int recordCount = 0;
		StringBuilder deleteSql = new StringBuilder();

		deleteSql.append("Delete From BMTLovFieldCode");
		deleteSql.append(StringUtils.trimToEmpty(type));
		deleteSql.append(" Where FieldCode =:FieldCode");
		
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(lovFieldCode);

		try{
			recordCount = this.namedParameterJdbcTemplate.update(deleteSql.toString(), beanParameters);
			if (recordCount <= 0) {
				throw new ConcurrencyException();
			}
		}catch(DataAccessException e){
			throw new DependencyFoundException(e);
		}
		logger.debug("Leaving");
	}

	/**
	 * This method insert new Records into BMTLovFieldCode or BMTLovFieldCode_Temp.
	 *
	 * save Field Code 
	 * 
	 * @param Field Code (lovFieldCode)
	 * @param  type (String)
	 * 			""/_Temp/_View          
	 * @return void
	 * @throws DataAccessException
	 * 
	 */
	@Override
	public String save(LovFieldCode lovFieldCode,String type) {
		logger.debug("Entering");
		StringBuilder insertSql = new StringBuilder();

		insertSql.append("Insert Into BMTLovFieldCode");
		insertSql.append(StringUtils.trimToEmpty(type));
		insertSql.append(" (FieldCode, FieldCodeDesc, FieldCodeType, fieldEdit,isActive,");
		insertSql.append(" Version , LastMntBy, LastMntOn, RecordStatus, RoleCode, NextRoleCode, TaskId, NextTaskId," );
		insertSql.append(" RecordType, WorkflowId)");
		insertSql.append(" Values(:FieldCode, :FieldCodeDesc, :FieldCodeType,:FieldEdit,:isActive,"); 
		insertSql.append(" :Version , :LastMntBy, :LastMntOn, :RecordStatus, :RoleCode, :NextRoleCode, :TaskId, :NextTaskId, ");
		insertSql.append(" :RecordType, :WorkflowId)");
		
		logger.debug("insertSql: "+ insertSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(lovFieldCode);
		this.namedParameterJdbcTemplate.update(insertSql.toString(), beanParameters);

		logger.debug("Leaving");
		return lovFieldCode.getId();
	}

	/**
	 * This method updates the Record BMTLovFieldCode or BMTLovFieldCode_Temp.
	 * if Record not updated then throws DataAccessException with  error  41004.
	 * update Field Code by key FieldCode and Version
	 * 
	 * @param Field Code (lovFieldCode)
	 * @param  type (String)
	 * 			""/_Temp/_View          
	 * @return void
	 * @throws DataAccessException
	 * 
	 */

	@Override
	public void update(LovFieldCode lovFieldCode,String type) {
		logger.debug("Entering");
		int recordCount = 0;
		StringBuilder updateSql = new StringBuilder();

		updateSql.append("Update BMTLovFieldCode");
		updateSql.append(StringUtils.trimToEmpty(type)); 
		updateSql.append(" Set FieldCodeDesc = :FieldCodeDesc, FieldCodeType = :FieldCodeType,");
		updateSql.append(" FieldEdit=:FieldEdit, isActive = :isActive,");
		updateSql.append(" Version = :Version , LastMntBy = :LastMntBy, LastMntOn = :LastMntOn, RecordStatus= :RecordStatus,");
		updateSql.append(" RoleCode = :RoleCode, NextRoleCode = :NextRoleCode, TaskId = :TaskId, NextTaskId = :NextTaskId,");
		updateSql.append(" RecordType = :RecordType, WorkflowId = :WorkflowId");
		updateSql.append(" Where FieldCode =:FieldCode");
		if (!type.endsWith("_Temp")) {
			updateSql.append("  AND Version= :Version-1");
		}

		logger.debug("updateSql: "+ updateSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(lovFieldCode);
		recordCount = this.namedParameterJdbcTemplate.update(updateSql.toString(), beanParameters);

		if (recordCount <= 0) {
			throw new ConcurrencyException();
		}
		logger.debug("Leaving");
	}

}