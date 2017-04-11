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


import java.util.ArrayList;
import java.util.List;

import javax.sql.DataSource;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.core.simple.ParameterizedBeanPropertyRowMapper;

import com.pennant.app.util.ErrorUtil;
import com.pennant.backend.dao.impl.BasisCodeDAO;
import com.pennant.backend.dao.masters.SystemInternalAccountDefinitionDAO;
import com.pennant.backend.model.ErrorDetails;
import com.pennant.backend.model.ValueLabel;
import com.pennant.backend.model.masters.SystemInternalAccountDefinition;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantJavaUtil;

/**
 * DAO methods implementation for the <b>SystemInternalAccountDefinition model</b> class.<br>
 * 
 */

public class SystemInternalAccountDefinitionDAOImpl extends BasisCodeDAO<SystemInternalAccountDefinition> implements SystemInternalAccountDefinitionDAO {

	private static Logger logger = Logger.getLogger(SystemInternalAccountDefinitionDAOImpl.class);
	
	// Spring Named JDBC Template
	private NamedParameterJdbcTemplate namedParameterJdbcTemplate;
	
	public SystemInternalAccountDefinitionDAOImpl() {
		super();
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
		SystemInternalAccountDefinition systemInternalAccountDefinition = new SystemInternalAccountDefinition();
		
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
			logger.error("Exception",e);
		}
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
	public String getSysIntAccNum(final String sIACode) {
		logger.debug("Entering");
		
		String sIANum = "";
		SystemInternalAccountDefinition systemInternalAccountDefinition = new SystemInternalAccountDefinition();
		systemInternalAccountDefinition.setId(sIACode);
		
		StringBuilder selectSql = new StringBuilder("Select SIANumber From SystemInternalAccountDef");
		selectSql.append(" Where SIACode =:SIACode");
		
		logger.debug("selectSql: " + selectSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(systemInternalAccountDefinition);
		
		try{
			sIANum = this.namedParameterJdbcTemplate.queryForObject(selectSql.toString(), beanParameters, String.class);	
		}catch (EmptyResultDataAccessException e) {
			sIANum = "";
			logger.error("Exception",e);
		}finally {
			systemInternalAccountDefinition = null;
			beanParameters = null;
		}
		logger.debug("Leaving");
		return sIANum;
	}
	
	/**
	 * Fetch the List of System Internal Account Definition details by key field
	 * 
	 * @param id (String)
	 * @param  type (String)
	 * 			""/_Temp/_View          
	 * @return SystemInternalAccountDefinition
	 */
	@Override
	public List<SystemInternalAccountDefinition> getSysIntAccNumList(List<String> sIACodeList) {
		logger.debug("Entering");
		
		if(sIACodeList.isEmpty()) {
			return new ArrayList<SystemInternalAccountDefinition>();
		}
		
		MapSqlParameterSource source=new MapSqlParameterSource();
		source.addValue("SIACode", sIACodeList);
		
		StringBuilder selectSql = new StringBuilder("Select SIACode, SIANumber From SystemInternalAccountDef");
		selectSql.append(" Where SIACode IN(:SIACode)");
		
		logger.debug("selectSql: " + selectSql.toString());
		RowMapper<SystemInternalAccountDefinition> typeRowMapper = ParameterizedBeanPropertyRowMapper.newInstance(SystemInternalAccountDefinition.class);
		
		List<SystemInternalAccountDefinition> sIANumList = null;
		try{
			sIANumList = this.namedParameterJdbcTemplate.query(selectSql.toString(), source, typeRowMapper);	
		}catch (EmptyResultDataAccessException e) {
			logger.warn("Exception: ", e);
			sIANumList = new ArrayList<SystemInternalAccountDefinition>();
		}
		logger.debug("Leaving");
		return sIANumList;
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
	public List<ValueLabel> getEntrySIANumDetails() {
		logger.debug("Entering");
		
		StringBuilder selectSql = new StringBuilder("Select SIACode Label , SIANumber Value From SystemInternalAccountDef");
		/*selectSql.append(" where SIACode in (select Distinct AccountType from RMTTransactionEntry where Account = '" );
		selectSql.append(PennantConstants.GLNPL);
		selectSql.append("')");*/
		
		logger.debug("selectSql: " + selectSql.toString());
		RowMapper<ValueLabel> typeRowMapper = ParameterizedBeanPropertyRowMapper.newInstance(ValueLabel.class);

		List<ValueLabel> valueLabels = this.namedParameterJdbcTemplate.getJdbcOperations().query(selectSql.toString(), typeRowMapper);	 
		logger.debug("Leaving");
		return valueLabels;
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
			logger.error("Exception: ", e);
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
		updateSql.append(" Set SIAName = :SIAName, SIAShortName = :SIAShortName, SIAAcType = :SIAAcType, SIANumber = :SIANumber");
		updateSql.append(", Version = :Version , LastMntBy = :LastMntBy, LastMntOn = :LastMntOn, RecordStatus= :RecordStatus, RoleCode = :RoleCode, NextRoleCode = :NextRoleCode, TaskId = :TaskId, NextTaskId = :NextTaskId, RecordType = :RecordType, WorkflowId = :WorkflowId");
		updateSql.append(" Where SIACode =:SIACode");
		
		if (!type.endsWith("_Temp")){
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
	
	private ErrorDetails  getError(String errorId, String sIACode, String userLanguage){
		String[][] parms= new String[2][1];
		parms[1][0] = sIACode;
		parms[0][0] = PennantJavaUtil.getLabel("label_SIACode")+ ":" + parms[1][0];
		return ErrorUtil.getErrorDetail(new ErrorDetails(PennantConstants.KEY_FIELD, errorId, parms[0],parms[1]), userLanguage);
	}

	
}