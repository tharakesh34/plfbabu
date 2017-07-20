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
 * FileName    		:  ErrorDetailDAOImpl.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  05-05-2016    														*
 *                                                                  						*
 * Modified Date    :  05-05-2016    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 05-05-2016       Pennant	                 0.1                                            * 
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

package com.pennant.backend.dao.errordetail.impl;


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

import com.pennant.backend.dao.errordetail.ErrorDetailDAO;
import com.pennant.backend.dao.impl.BasisCodeDAO;
import com.pennant.backend.model.errordetail.ErrorDetail;
import com.pennanttech.pennapps.core.ConcurrencyException;
import com.pennanttech.pennapps.core.DependencyFoundException;

/**
 * DAO methods implementation for the <b>ErrorDetail model</b> class.<br>
 * 
 */

public class ErrorDetailDAOImpl extends BasisCodeDAO<ErrorDetail> implements ErrorDetailDAO {

	private static Logger logger = Logger.getLogger(ErrorDetailDAOImpl.class);
	
	// Spring Named JDBC Template
	private NamedParameterJdbcTemplate namedParameterJdbcTemplate;
	
	
	public ErrorDetailDAOImpl(){
		super();
	}
	
	/**
	 * Fetch the Record  Error Detail details by key field
	 * 
	 * @param id (String)
	 * @param  type (String)
	 * 			""/_Temp/_View          
	 * @return ErrorDetail
	 */
	@Override
	public ErrorDetail getErrorDetailById(final String id, String type) {
		logger.debug("Entering");
		ErrorDetail errorDetail = new ErrorDetail();
		
		errorDetail.setId(id);
		
		StringBuilder selectSql = new StringBuilder("Select ErrorCode, ErrorLanguage, ErrorSeverity, ErrorMessage, ErrorExtendedMessage");
		selectSql.append(", Version ,LastMntBy, LastMntOn, RecordStatus, RoleCode, NextRoleCode, TaskId, NextTaskId, RecordType, WorkflowId");
		
		selectSql.append(" From ErrorDetails");
		selectSql.append(StringUtils.trimToEmpty(type));
		selectSql.append(" Where ErrorCode =:ErrorCode");
		
		logger.debug("selectSql: " + selectSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(errorDetail);
		RowMapper<ErrorDetail> typeRowMapper = ParameterizedBeanPropertyRowMapper.newInstance(ErrorDetail.class);
		
		try{
			errorDetail = this.namedParameterJdbcTemplate.queryForObject(selectSql.toString(), beanParameters, typeRowMapper);	
		}catch (EmptyResultDataAccessException e) {
			logger.error(e);
			errorDetail = null;
		}
		logger.debug("Leaving");
		return errorDetail;
	}
	
	/**
	 * To Set  dataSource
	 * @param dataSource
	 */
	
	public void setDataSource(DataSource dataSource) {
		this.namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
	}
	
	/**
	 * This method Deletes the Record from the ErrorDetails or ErrorDetails_Temp.
	 * if Record not deleted then throws DataAccessException with  error  41003.
	 * delete Error Detail by key ErrorCode
	 * 
	 * @param Error Detail (errorDetail)
	 * @param  type (String)
	 * 			""/_Temp/_View          
	 * @return void
	 * @throws DataAccessException
	 * 
	 */
	@Override
	public void delete(ErrorDetail errorDetail,String type) {
		logger.debug("Entering");
		int recordCount = 0;
		
		StringBuilder deleteSql = new StringBuilder("Delete From ErrorDetails");
		deleteSql.append(StringUtils.trimToEmpty(type));
		deleteSql.append(" Where ErrorCode =:ErrorCode");
		logger.debug("deleteSql: " + deleteSql.toString());

		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(errorDetail);
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
	 * This method insert new Records into ErrorDetails or ErrorDetails_Temp.
	 *
	 * save Error Detail 
	 * 
	 * @param Error Detail (errorDetail)
	 * @param  type (String)
	 * 			""/_Temp/_View          
	 * @return void
	 * @throws DataAccessException
	 * 
	 */
	
	@Override
	public String save(ErrorDetail errorDetail,String type) {
		logger.debug("Entering");
		
		StringBuilder insertSql =new StringBuilder("Insert Into ErrorDetails");
		insertSql.append(StringUtils.trimToEmpty(type));
		insertSql.append(" (ErrorCode, ErrorLanguage, ErrorSeverity, ErrorMessage, ErrorExtendedMessage");
		insertSql.append(", Version , LastMntBy, LastMntOn, RecordStatus, RoleCode, NextRoleCode, TaskId, NextTaskId, RecordType, WorkflowId)");
		insertSql.append(" Values(:ErrorCode, :ErrorLanguage, :ErrorSeverity, :ErrorMessage, :ErrorExtendedMessage");
		insertSql.append(", :Version , :LastMntBy, :LastMntOn, :RecordStatus, :RoleCode, :NextRoleCode, :TaskId, :NextTaskId, :RecordType, :WorkflowId)");
		
		logger.debug("insertSql: " + insertSql.toString());
		
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(errorDetail);
		this.namedParameterJdbcTemplate.update(insertSql.toString(), beanParameters);
		logger.debug("Leaving");
		return errorDetail.getId();
	}
	
	/**
	 * This method updates the Record ErrorDetails or ErrorDetails_Temp.
	 * if Record not updated then throws DataAccessException with  error  41004.
	 * update Error Detail by key ErrorCode and Version
	 * 
	 * @param Error Detail (errorDetail)
	 * @param  type (String)
	 * 			""/_Temp/_View          
	 * @return void
	 * @throws DataAccessException
	 * 
	 */
	
	@Override
	public void update(ErrorDetail errorDetail,String type) {
		int recordCount = 0;
		logger.debug("Entering");
		StringBuilder	updateSql =new StringBuilder("Update ErrorDetails");
		updateSql.append(StringUtils.trimToEmpty(type)); 
		updateSql.append(" Set ErrorLanguage = :ErrorLanguage, ErrorSeverity = :ErrorSeverity, ErrorMessage = :ErrorMessage, ErrorExtendedMessage = :ErrorExtendedMessage");
		updateSql.append(", Version = :Version , LastMntBy = :LastMntBy, LastMntOn = :LastMntOn, RecordStatus= :RecordStatus, RoleCode = :RoleCode, NextRoleCode = :NextRoleCode, TaskId = :TaskId, NextTaskId = :NextTaskId, RecordType = :RecordType, WorkflowId = :WorkflowId");
		updateSql.append(" Where ErrorCode =:ErrorCode");
		
		if (!type.endsWith("_Temp")){
			updateSql.append("  AND Version= :Version-1");
		}
		
		logger.debug("updateSql: " + updateSql.toString());
		
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(errorDetail);
		recordCount = this.namedParameterJdbcTemplate.update(updateSql.toString(), beanParameters);
		
		if (recordCount <= 0) {
			throw new ConcurrencyException();
		}
		logger.debug("Leaving");
	}
	
}