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
 * FileName    		:  SystemInternalAccountDefinitionDAOImpl.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  17-12-2011    														*
 *                                                                  						*
 * Modified Date    :  17-12-2011    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 17-12-2011       Pennant	                 0.1                                            * 
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

package com.pennant.backend.dao.masters.impl;


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
import com.pennant.backend.dao.masters.SystemInternalAccountDefinitionDAO;
import com.pennant.backend.model.ErrorDetails;
import com.pennant.backend.model.WorkFlowDetails;
import com.pennant.backend.model.masters.SystemInternalAccountDefinition;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantJavaUtil;
import com.pennant.backend.util.WorkFlowUtil;

/**
 * DAO methods implementation for the <b>SystemInternalAccountDefinition model</b> class.<br>
 * 
 */

public class SystemInternalAccountDefinitionDAOImpl extends BasisCodeDAO<SystemInternalAccountDefinition> implements SystemInternalAccountDefinitionDAO {

	private static Logger logger = Logger.getLogger(SystemInternalAccountDefinitionDAOImpl.class);
	
	// Spring Named JDBC Template
	private NamedParameterJdbcTemplate namedParameterJdbcTemplate;
	
	/**
	 * This method set the Work Flow id based on the module name and return the new SystemInternalAccountDefinition 
	 * @return SystemInternalAccountDefinition
	 */

	@Override
	public SystemInternalAccountDefinition getSystemInternalAccountDefinition() {
		logger.debug("Entering");
		WorkFlowDetails workFlowDetails=WorkFlowUtil.getWorkFlowDetails("SystemInternalAccountDefinition");
		SystemInternalAccountDefinition systemInternalAccountDefinition= new SystemInternalAccountDefinition();
		if (workFlowDetails!=null){
			systemInternalAccountDefinition.setWorkflowId(workFlowDetails.getWorkFlowId());
		}
		logger.debug("Leaving");
		return systemInternalAccountDefinition;
	}


	/**
	 * This method get the module from method getSystemInternalAccountDefinition() and set the new record flag as true and return SystemInternalAccountDefinition()   
	 * @return SystemInternalAccountDefinition
	 */


	@Override
	public SystemInternalAccountDefinition getNewSystemInternalAccountDefinition() {
		logger.debug("Entering");
		SystemInternalAccountDefinition systemInternalAccountDefinition = getSystemInternalAccountDefinition();
		systemInternalAccountDefinition.setNewRecord(true);
		logger.debug("Leaving");
		return systemInternalAccountDefinition;
	}

	/**
	 * Fetch the Record  System Internal Account Definition details by key field
	 * 
	 * @param id (String)
	 * @param  type (String)
	 * 			""/_Temp/_View          
	 * @return SystemInternalAccountDefinition
	 */
	@Override
	public SystemInternalAccountDefinition getSystemInternalAccountDefinitionById(final String id, String type) {
		logger.debug("Entering");
		SystemInternalAccountDefinition systemInternalAccountDefinition = getSystemInternalAccountDefinition();
		
		systemInternalAccountDefinition.setId(id);
		
		StringBuilder selectSql = new StringBuilder("Select SIACode, SIAName, SIAShortName, SIAAcType, SIANumber");
		selectSql.append(", Version , LastMntBy, LastMntOn, RecordStatus, RoleCode, NextRoleCode, TaskId, NextTaskId, RecordType, WorkflowId");

		if(StringUtils.trimToEmpty(type).contains("View")){
			selectSql.append(",lovDescSIAAcTypeName ");
		}
		selectSql.append(" From SystemInternalAccountDef");
		selectSql.append(StringUtils.trimToEmpty(type));
		selectSql.append(" Where SIACode =:SIACode");
		
		logger.debug("selectSql: " + selectSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(systemInternalAccountDefinition);
		RowMapper<SystemInternalAccountDefinition> typeRowMapper = ParameterizedBeanPropertyRowMapper.newInstance(SystemInternalAccountDefinition.class);
		
		try{
			systemInternalAccountDefinition = this.namedParameterJdbcTemplate.queryForObject(selectSql.toString(), beanParameters, typeRowMapper);	
		}catch (EmptyResultDataAccessException e) {
			systemInternalAccountDefinition = null;
		}
		logger.debug("Leaving");
		return systemInternalAccountDefinition;
	}
	
	/**
	 * This method initialise the Record.
	 * @param SystemInternalAccountDefinition (systemInternalAccountDefinition)
 	 * @return SystemInternalAccountDefinition
	 */
	@Override
	public void initialize(SystemInternalAccountDefinition systemInternalAccountDefinition) {
		super.initialize(systemInternalAccountDefinition);
	}
	/**
	 * This method refresh the Record.
	 * @param SystemInternalAccountDefinition (systemInternalAccountDefinition)
 	 * @return void
	 */
	@Override
	public void refresh(SystemInternalAccountDefinition systemInternalAccountDefinition) {
		
	}
	
	/**
	 * To Set  dataSource
	 * @param dataSource
	 */
	
	public void setDataSource(DataSource dataSource) {
		this.namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
	}
	
	/**
	 * This method Deletes the Record from the SystemInternalAccountDef or SystemInternalAccountDef_Temp.
	 * if Record not deleted then throws DataAccessException with  error  41003.
	 * delete System Internal Account Definition by key SIACode
	 * 
	 * @param System Internal Account Definition (systemInternalAccountDefinition)
	 * @param  type (String)
	 * 			""/_Temp/_View          
	 * @return void
	 * @throws DataAccessException
	 * 
	 */
	@SuppressWarnings("serial")
	public void delete(SystemInternalAccountDefinition systemInternalAccountDefinition,String type) {
		logger.debug("Entering");
		int recordCount = 0;
		
		StringBuilder deleteSql = new StringBuilder("Delete From SystemInternalAccountDef");
		deleteSql.append(StringUtils.trimToEmpty(type));
		deleteSql.append(" Where SIACode =:SIACode");
		logger.debug("deleteSql: " + deleteSql.toString());

		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(systemInternalAccountDefinition);
		try{
			recordCount = this.namedParameterJdbcTemplate.update(deleteSql.toString(), beanParameters);
			if (recordCount <= 0) {
				ErrorDetails errorDetails= getError("41003",systemInternalAccountDefinition.getId() ,systemInternalAccountDefinition.getUserDetails().getUsrLanguage());
				throw new DataAccessException(errorDetails.getError()) {};
			}
		}catch(DataAccessException e){
			logger.error(e);
			ErrorDetails errorDetails= getError("41006",systemInternalAccountDefinition.getId() ,systemInternalAccountDefinition.getUserDetails().getUsrLanguage());
			throw new DataAccessException(errorDetails.getError()) {};
		}
		logger.debug("Leaving");
	}
	
	/**
	 * This method insert new Records into SystemInternalAccountDef or SystemInternalAccountDef_Temp.
	 *
	 * save System Internal Account Definition 
	 * 
	 * @param System Internal Account Definition (systemInternalAccountDefinition)
	 * @param  type (String)
	 * 			""/_Temp/_View          
	 * @return void
	 * @throws DataAccessException
	 * 
	 */
	
	@Override
	public String save(SystemInternalAccountDefinition systemInternalAccountDefinition,String type) {
		logger.debug("Entering");
		
		StringBuilder insertSql =new StringBuilder("Insert Into SystemInternalAccountDef");
		insertSql.append(StringUtils.trimToEmpty(type));
		insertSql.append(" (SIACode, SIAName, SIAShortName, SIAAcType, SIANumber");
		insertSql.append(", Version , LastMntBy, LastMntOn, RecordStatus, RoleCode, NextRoleCode, TaskId, NextTaskId, RecordType, WorkflowId)");
		insertSql.append(" Values(:SIACode, :SIAName, :SIAShortName, :SIAAcType, :SIANumber");
		insertSql.append(", :Version , :LastMntBy, :LastMntOn, :RecordStatus, :RoleCode, :NextRoleCode, :TaskId, :NextTaskId, :RecordType, :WorkflowId)");
		
		logger.debug("insertSql: " + insertSql.toString());
		
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(systemInternalAccountDefinition);
		this.namedParameterJdbcTemplate.update(insertSql.toString(), beanParameters);
		logger.debug("Leaving");
		return systemInternalAccountDefinition.getId();
	}
	
	/**
	 * This method updates the Record SystemInternalAccountDef or SystemInternalAccountDef_Temp.
	 * if Record not updated then throws DataAccessException with  error  41004.
	 * update System Internal Account Definition by key SIACode and Version
	 * 
	 * @param System Internal Account Definition (systemInternalAccountDefinition)
	 * @param  type (String)
	 * 			""/_Temp/_View          
	 * @return void
	 * @throws DataAccessException
	 * 
	 */
	
	@SuppressWarnings("serial")
	@Override
	public void update(SystemInternalAccountDefinition systemInternalAccountDefinition,String type) {
		int recordCount = 0;
		logger.debug("Entering");
		StringBuilder	updateSql =new StringBuilder("Update SystemInternalAccountDef");
		updateSql.append(StringUtils.trimToEmpty(type)); 
		updateSql.append(" Set SIACode = :SIACode, SIAName = :SIAName, SIAShortName = :SIAShortName, SIAAcType = :SIAAcType, SIANumber = :SIANumber");
		updateSql.append(", Version = :Version , LastMntBy = :LastMntBy, LastMntOn = :LastMntOn, RecordStatus= :RecordStatus, RoleCode = :RoleCode, NextRoleCode = :NextRoleCode, TaskId = :TaskId, NextTaskId = :NextTaskId, RecordType = :RecordType, WorkflowId = :WorkflowId");
		updateSql.append(" Where SIACode =:SIACode");
		
		if (!type.endsWith("_TEMP")){
			updateSql.append("  AND Version= :Version-1");
		}
		
		logger.debug("updateSql: " + updateSql.toString());
		
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(systemInternalAccountDefinition);
		recordCount = this.namedParameterJdbcTemplate.update(updateSql.toString(), beanParameters);
		
		if (recordCount <= 0) {
			logger.debug("Error Update Method Count :"+recordCount);
			ErrorDetails errorDetails= getError("41004",systemInternalAccountDefinition.getId() ,systemInternalAccountDefinition.getUserDetails().getUsrLanguage());
			throw new DataAccessException(errorDetails.getError()) {};
		}
		logger.debug("Leaving");
	}
	
	private ErrorDetails  getError(String errorId, String SIACode, String userLanguage){
		String[][] parms= new String[2][1];
		parms[1][0] = SIACode;
		parms[0][0] = PennantJavaUtil.getLabel("label_SIACode")+ ":" + parms[1][0];
		return ErrorUtil.getErrorDetail(new ErrorDetails(PennantConstants.KEY_FIELD, errorId, parms[0],parms[1]), userLanguage);
	}

	
}