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
 * FileName    		:  Additional FieldsDAOImpl.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  22-12-2011    														*
 *                                                                  						*
 * Modified Date    :  22-12-2011    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 22-12-2011       Pennant	                 0.1                                            * 
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

package com.pennant.backend.dao.testing.impl;


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

import com.pennant.app.util.ErrorUtil;
import com.pennant.backend.dao.impl.BasisCodeDAO;
import com.pennant.backend.dao.testing.AdditionalFieldsDAO;
import com.pennant.backend.model.ErrorDetails;
import com.pennant.backend.model.WorkFlowDetails;
import com.pennant.backend.model.testing.AdditionalFields;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantJavaUtil;
import com.pennant.backend.util.WorkFlowUtil;

/**
 * DAO methods implementation for the <b>Additional Fields model</b> class.<br>
 * 
 */

public class AdditionalFieldsDAOImpl extends BasisCodeDAO<AdditionalFields> implements AdditionalFieldsDAO {

	private static Logger logger = Logger.getLogger(AdditionalFieldsDAOImpl.class);
	
	// Spring Named JDBC Template
	private NamedParameterJdbcTemplate namedParameterJdbcTemplate;
	
	/**
	 * This method set the Work Flow id based on the module name and return the new Additional Fields 
	 * @return Additional Fields
	 */

	@Override
	public AdditionalFields getAdditionalFields() {
		logger.debug("Entering");
		WorkFlowDetails workFlowDetails=WorkFlowUtil.getWorkFlowDetails("AdditionalFields");
		AdditionalFields additionalFields= new AdditionalFields();
		if (workFlowDetails!=null){
			additionalFields.setWorkflowId(workFlowDetails.getWorkFlowId());
		}
		logger.debug("Leaving");
		return additionalFields;
	}


	/**
	 * This method get the module from method getAdditional Fields() and set the new record flag as true and return Additional Fields()   
	 * @return Additional Fields
	 */


	@Override
	public AdditionalFields getNewAdditionalFields() {
		logger.debug("Entering");
		AdditionalFields additionalFields = getAdditionalFields();
		additionalFields.setNewRecord(true);
		logger.debug("Leaving");
		return additionalFields;
	}

	/**
	 * Fetch the Record  Additioanl Fields details by key field
	 * 
	 * @param id (String)
	 * @param  type (String)
	 * 			""/_Temp/_View          
	 * @return Additional Fields
	 */
	@Override
	public AdditionalFields getAdditionalFieldsById(final String id, String type) {
		logger.debug("Entering");
		AdditionalFields additionalFields = getAdditionalFields();
		
		additionalFields.setId(id);
		
		StringBuilder selectSql = new StringBuilder("Select Code, Description");
		selectSql.append(", Version , LastMntBy, LastMntOn, RecordStatus, RoleCode, NextRoleCode, TaskId, NextTaskId, RecordType, WorkflowId");

		if(StringUtils.trimToEmpty(type).contains("View")){
			
		}
		selectSql.append(" From AdditionalFields");
		selectSql.append(StringUtils.trimToEmpty(type));
		selectSql.append(" Where Code =:Code");
		
		logger.debug("selectSql: " + selectSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(additionalFields);
		RowMapper<AdditionalFields> typeRowMapper = ParameterizedBeanPropertyRowMapper.newInstance(AdditionalFields.class);
		
		try{
			additionalFields = this.namedParameterJdbcTemplate.queryForObject(selectSql.toString(), beanParameters, typeRowMapper);	
		}catch (EmptyResultDataAccessException e) {
			additionalFields = null;
		}
		logger.debug("Leaving");
		return additionalFields;
	}
	
	/**
	 * This method initialise the Record.
	 * @param Additional Fields (additional Fields)
 	 * @return Additional Fields
	 */
	@Override
	public void initialize(AdditionalFields additionalFields) {
		super.initialize(additionalFields);
	}
	/**
	 * This method refresh the Record.
	 * @param Additional Fields (additional Fields)
 	 * @return void
	 */
	@Override
	public void refresh(AdditionalFields additionalFields) {
		
	}
	
	/**
	 * To Set  dataSource
	 * @param dataSource
	 */
	
	public void setDataSource(DataSource dataSource) {
		this.namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
	}
	
	/**
	 * This method Deletes the Record from the AdditionalFields or AdditionalFields_Temp.
	 * if Record not deleted then throws DataAccessException with  error  41003.
	 * delete Additioanl Fields by key Code
	 * 
	 * @param Additioanl Fields (additional Fields)
	 * @param  type (String)
	 * 			""/_Temp/_View          
	 * @return void
	 * @throws DataAccessException
	 * 
	 */
	@SuppressWarnings("serial")
	public void delete(AdditionalFields additionalFields,String type) {
		logger.debug("Entering");
		int recordCount = 0;
		
		StringBuilder deleteSql = new StringBuilder("Delete From AdditionalFields");
		deleteSql.append(StringUtils.trimToEmpty(type));
		deleteSql.append(" Where Code =:Code");
		logger.debug("deleteSql: " + deleteSql.toString());

		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(additionalFields);
		try{
			recordCount = this.namedParameterJdbcTemplate.update(deleteSql.toString(), beanParameters);
			if (recordCount <= 0) {
				ErrorDetails errorDetails= getError("41003",additionalFields.getId() ,additionalFields.getUserDetails().getUsrLanguage());
				throw new DataAccessException(errorDetails.getError()) {};
			}
		}catch(DataAccessException e){
			logger.error(e);
			ErrorDetails errorDetails= getError("41006",additionalFields.getId() ,additionalFields.getUserDetails().getUsrLanguage());
			throw new DataAccessException(errorDetails.getError()) {};
		}
		logger.debug("Leaving");
	}
	
	/**
	 * This method insert new Records into AdditionalFields or AdditionalFields_Temp.
	 *
	 * save Additioanl Fields 
	 * 
	 * @param Additioanl Fields (additional Fields)
	 * @param  type (String)
	 * 			""/_Temp/_View          
	 * @return void
	 * @throws DataAccessException
	 * 
	 */
	
	@Override
	public String save(AdditionalFields additionalFields,String type) {
		logger.debug("Entering");
		
		StringBuilder insertSql =new StringBuilder("Insert Into AdditionalFields");
		insertSql.append(StringUtils.trimToEmpty(type));
		insertSql.append(" (Code, Description");
		insertSql.append(", Version , LastMntBy, LastMntOn, RecordStatus, RoleCode, NextRoleCode, TaskId, NextTaskId, RecordType, WorkflowId)");
		insertSql.append(" Values(:Code, :Description");
		insertSql.append(", :Version , :LastMntBy, :LastMntOn, :RecordStatus, :RoleCode, :NextRoleCode, :TaskId, :NextTaskId, :RecordType, :WorkflowId)");
		
		logger.debug("insertSql: " + insertSql.toString());
		
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(additionalFields);
		this.namedParameterJdbcTemplate.update(insertSql.toString(), beanParameters);
		logger.debug("Leaving");
		return additionalFields.getId();
	}
	
	/**
	 * This method updates the Record AdditionalFields or AdditionalFields_Temp.
	 * if Record not updated then throws DataAccessException with  error  41004.
	 * update Additioanl Fields by key Code and Version
	 * 
	 * @param Additioanl Fields (additional Fields)
	 * @param  type (String)
	 * 			""/_Temp/_View          
	 * @return void
	 * @throws DataAccessException
	 * 
	 */
	
	@SuppressWarnings("serial")
	@Override
	public void update(AdditionalFields additionalFields,String type) {
		int recordCount = 0;
		logger.debug("Entering");
		StringBuilder	updateSql =new StringBuilder("Update AdditionalFields");
		updateSql.append(StringUtils.trimToEmpty(type)); 
		updateSql.append(" Set Code = :Code, Description = :Description");
		updateSql.append(", Version = :Version , LastMntBy = :LastMntBy, LastMntOn = :LastMntOn, RecordStatus= :RecordStatus, RoleCode = :RoleCode, NextRoleCode = :NextRoleCode, TaskId = :TaskId, NextTaskId = :NextTaskId, RecordType = :RecordType, WorkflowId = :WorkflowId");
		updateSql.append(" Where Code =:Code");
		
		if (!type.endsWith("_TEMP")){
			updateSql.append("  AND Version= :Version-1");
		}
		
		logger.debug("updateSql: " + updateSql.toString());
		
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(additionalFields);
		recordCount = this.namedParameterJdbcTemplate.update(updateSql.toString(), beanParameters);
		
		if (recordCount <= 0) {
			logger.debug("Error Update Method Count :"+recordCount);
			ErrorDetails errorDetails= getError("41004",additionalFields.getId() ,additionalFields.getUserDetails().getUsrLanguage());
			throw new DataAccessException(errorDetails.getError()) {};
		}
		logger.debug("Leaving");
	}
	
	private ErrorDetails  getError(String errorId, String code, String userLanguage){
		String[][] parms= new String[2][1];
		parms[1][0] = code;
		parms[0][0] = PennantJavaUtil.getLabel("label_Code")+ ":" + parms[1][0];
		return ErrorUtil.getErrorDetail(new ErrorDetails(PennantConstants.KEY_FIELD, errorId, parms[0],parms[1]), userLanguage);
	}

	
}