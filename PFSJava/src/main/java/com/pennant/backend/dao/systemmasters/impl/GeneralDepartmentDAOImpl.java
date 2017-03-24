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
 * FileName    		:  GeneralDepartmentDAOImpl.java                                                   * 	  
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
import com.pennant.backend.dao.systemmasters.GeneralDepartmentDAO;
import com.pennant.backend.model.ErrorDetails;
import com.pennant.backend.model.systemmasters.GeneralDepartment;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantJavaUtil;

/**
 * DAO methods implementation for the <b>GeneralDepartment model</b> class.<br>
 * 
 */
public class GeneralDepartmentDAOImpl extends BasisCodeDAO<GeneralDepartment>
		implements GeneralDepartmentDAO {

	private static Logger logger = Logger.getLogger(GeneralDepartmentDAOImpl.class);
	
	// Spring Named JDBC Template
	private NamedParameterJdbcTemplate namedParameterJdbcTemplate;

	public GeneralDepartmentDAOImpl() {
		super();
	}
	
	/**
	 * Fetch the Record  GeneralDepartment details by key field
	 * 
	 * @param id (String)
	 * @param  type (String)
	 * 			""/_Temp/_View          
	 * @return GeneralDepartment
	 */
	@Override
	public GeneralDepartment getGeneralDepartmentById(final String id, String type) {
		logger.debug("Entering");
		GeneralDepartment generalDepartment = new GeneralDepartment();
		generalDepartment.setId(id);
		
		StringBuilder selectSql = new StringBuilder("Select GenDepartment, GenDeptDesc," );
		selectSql.append(" Version , LastMntBy, LastMntOn, RecordStatus, RoleCode, NextRoleCode,");
		selectSql.append(" TaskId, NextTaskId, RecordType, WorkflowId" );
		selectSql.append(" From RMTGenDepartments");
		selectSql.append(StringUtils.trimToEmpty(type));
		selectSql.append(" Where GenDepartment =:GenDepartment");
		
		logger.debug("selectSql: " + selectSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(generalDepartment);
		RowMapper<GeneralDepartment> typeRowMapper = ParameterizedBeanPropertyRowMapper
				.newInstance(GeneralDepartment.class);
		
		try{
			generalDepartment = this.namedParameterJdbcTemplate.queryForObject(
					selectSql.toString(), beanParameters, typeRowMapper);	
		}catch (EmptyResultDataAccessException e) {
			logger.error("Exception: ", e);
			generalDepartment = null;
		}
		logger.debug("Leaving");
		return generalDepartment;
	}
	
	/**
	 * @param dataSource the dataSource to set
	 */
	public void setDataSource(DataSource dataSource) {
		this.namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
	}
	
	/**
	 * This method Deletes the Record from the RMTGenDepartments or RMTGenDepartments_Temp.
	 * if Record not deleted then throws DataAccessException with  error  41003.
	 * delete GeneralDepartment by key GenDepartment
	 * 
	 * @param GeneralDepartment (generalDepartment)
	 * @param  type (String)
	 * 			""/_Temp/_View          
	 * @return void
	 * @throws DataAccessException
	 * 
	 */
	@SuppressWarnings("serial")
	public void delete(GeneralDepartment generalDepartment,String type) {
		logger.debug("Entering");
		int recordCount = 0;
		
		StringBuilder deleteSql = new StringBuilder(" Delete From RMTGenDepartments" );
		deleteSql.append(StringUtils.trimToEmpty(type) );
		deleteSql.append(" Where GenDepartment =:GenDepartment");
		
		logger.debug("deleteSql: "+ deleteSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(generalDepartment);

		try{
			recordCount = this.namedParameterJdbcTemplate.update(deleteSql.toString(), beanParameters);
			
			if (recordCount <= 0) {
				ErrorDetails errorDetails= getError("41003",generalDepartment.getGenDepartment(),
						generalDepartment.getUserDetails().getUsrLanguage());
				throw new DataAccessException(errorDetails.getError()) {};
			}
		}catch(DataAccessException e){
			logger.error("Exception: ", e);
			ErrorDetails errorDetails=getError("41006",generalDepartment.getGenDepartment(),
					generalDepartment.getUserDetails().getUsrLanguage());
			throw new DataAccessException(errorDetails.getError()) {};
		}
		logger.debug("Leaving");
	}
	
	/**
	 * This method insert new Records into RMTGenDepartments or RMTGenDepartments_Temp.
	 *
	 * save GeneralDepartment 
	 * 
	 * @param GeneralDepartment (generalDepartment)
	 * @param  type (String)
	 * 			""/_Temp/_View          
	 * @return void
	 * @throws DataAccessException
	 * 
	 */
	@Override
	public String save(GeneralDepartment generalDepartment,String type) {
		logger.debug("Entering");
		
		StringBuilder insertSql = new StringBuilder("Insert Into RMTGenDepartments" );
		insertSql.append(StringUtils.trimToEmpty(type) );
		insertSql.append(" (GenDepartment, GenDeptDesc,");
		insertSql.append(" Version , LastMntBy, LastMntOn, RecordStatus, RoleCode, NextRoleCode,");
		insertSql.append(" TaskId, NextTaskId, RecordType, WorkflowId)" );
		insertSql.append(" Values(:GenDepartment, :GenDeptDesc," );
		insertSql.append(" :Version , :LastMntBy, :LastMntOn, :RecordStatus, :RoleCode, :NextRoleCode," );
		insertSql.append(" :TaskId, :NextTaskId, :RecordType, :WorkflowId)");
		
		logger.debug("insertSql: "+ insertSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(generalDepartment);
		this.namedParameterJdbcTemplate.update(insertSql.toString(), beanParameters);
		
		logger.debug("Leaving");
		return generalDepartment.getId();
	}
	
	/**
	 * This method updates the Record RMTGenDepartments or RMTGenDepartments_Temp.
	 * if Record not updated then throws DataAccessException with  error  41004.
	 * update GeneralDepartment by key GenDepartment and Version
	 * 
	 * @param GeneralDepartment (generalDepartment)
	 * @param  type (String)
	 * 			""/_Temp/_View          
	 * @return void
	 * @throws DataAccessException
	 * 
	 */
	@SuppressWarnings("serial")
	@Override
	public void update(GeneralDepartment generalDepartment,String type) {
		int recordCount = 0;
		logger.debug("Entering");
		
		StringBuilder updateSql = new StringBuilder("Update RMTGenDepartments");
		updateSql.append(StringUtils.trimToEmpty(type) ); 
		updateSql.append(" Set GenDepartment = :GenDepartment, GenDeptDesc = :GenDeptDesc," );
		updateSql.append(" Version = :Version , LastMntBy = :LastMntBy, LastMntOn = :LastMntOn,");
		updateSql.append(" RecordStatus= :RecordStatus, RoleCode = :RoleCode," );
		updateSql.append(" NextRoleCode = :NextRoleCode, TaskId = :TaskId, NextTaskId = :NextTaskId," );
		updateSql.append(" RecordType = :RecordType, WorkflowId = :WorkflowId" );
		updateSql.append(" Where GenDepartment =:GenDepartment");
		
		if (!type.endsWith("_Temp")){
			updateSql.append(" AND Version= :Version-1");
		}
		
		logger.debug("updateSql: "+ updateSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(generalDepartment);
		recordCount = this.namedParameterJdbcTemplate.update(updateSql.toString(), beanParameters);
		
		if (recordCount <= 0) {
			logger.debug("Error in Update Method Count :"+recordCount);
			ErrorDetails errorDetails= getError("41004",generalDepartment.getGenDepartment(),
					generalDepartment.getUserDetails().getUsrLanguage());
			throw new DataAccessException(errorDetails.getError()) {};
		}
		logger.debug("Leaving");
	}
	
	private ErrorDetails  getError(String errorId,String genDepartment, String userLanguage){
		String[][] parms= new String[2][1]; 
		
		parms[1][0] = genDepartment;
		parms[0][0] = PennantJavaUtil.getLabel("label_GenDepartment")+ ":" + parms[1][0];
		return ErrorUtil.getErrorDetail(new ErrorDetails(PennantConstants.KEY_FIELD,
				errorId, parms[0],parms[1]), userLanguage);
	}
	
}