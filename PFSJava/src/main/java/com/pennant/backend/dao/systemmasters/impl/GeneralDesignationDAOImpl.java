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
 * FileName    		:  GeneralDesignationDAOImpl.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  03-05-2011    														*
 *                                                                  						*
 * Modified Date    :  03-05-2011    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 03-05-2011       Pennant	                 0.1                                            * 
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
import com.pennant.backend.dao.systemmasters.GeneralDesignationDAO;
import com.pennant.backend.model.ErrorDetails;
import com.pennant.backend.model.systemmasters.GeneralDesignation;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantJavaUtil;

/**
 * DAO methods implementation for the <b>GeneralDesignation model</b> class.<br>
 * 
 */
public class GeneralDesignationDAOImpl extends BasisCodeDAO<GeneralDesignation>
		implements GeneralDesignationDAO {

	private static Logger logger = Logger.getLogger(GeneralDesignationDAOImpl.class);
	
	// Spring Named JDBC Template
	private NamedParameterJdbcTemplate namedParameterJdbcTemplate;

	public GeneralDesignationDAOImpl() {
		super();
	}

	/**
	 * Fetch the Record  General Designation details by key field
	 * 
	 * @param id (String)
	 * @param  type (String)
	 * 			""/_Temp/_View          
	 * @return GeneralDesignation
	 */
	@Override
	public GeneralDesignation getGeneralDesignationById(final String id, String type) {
		logger.debug("Entering");
		GeneralDesignation generalDesignation = new GeneralDesignation();
		generalDesignation.setId(id);
		
		StringBuilder selectSql = new StringBuilder("Select GenDesignation, GenDesgDesc," );
		selectSql.append(" Version , LastMntBy, LastMntOn, RecordStatus, RoleCode, NextRoleCode," );
		selectSql.append(" TaskId, NextTaskId, RecordType, WorkflowId" );
		selectSql.append(" From RMTGenDesignations");
		selectSql.append(StringUtils.trimToEmpty(type) );
		selectSql.append(" Where GenDesignation =:GenDesignation");
		
		logger.debug("selectSql: " + selectSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(generalDesignation);
		RowMapper<GeneralDesignation> typeRowMapper = ParameterizedBeanPropertyRowMapper
				.newInstance(GeneralDesignation.class);
		
		try{
			generalDesignation = this.namedParameterJdbcTemplate
					.queryForObject(selectSql.toString(), beanParameters,typeRowMapper);	
		}catch (EmptyResultDataAccessException e) {
			logger.error("Exception: ", e);
			generalDesignation = null;
		}
		logger.debug("Leaving");
		return generalDesignation;
	}
	
	/**
	 * @param dataSource the dataSource to set
	 */
	public void setDataSource(DataSource dataSource) {
		this.namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
	}
	
	/**
	 * This method Deletes the Record from the RMTGenDesignations or RMTGenDesignations_Temp.
	 * if Record not deleted then throws DataAccessException with  error  41003.
	 * delete General Designation by key GenDesignation
	 * 
	 * @param General Designation (generalDesignation)
	 * @param  type (String)
	 * 			""/_Temp/_View          
	 * @return void
	 * @throws DataAccessException
	 * 
	 */
	@SuppressWarnings("serial")
	public void delete(GeneralDesignation generalDesignation,String type) {
		logger.debug("Entering");
		int recordCount = 0;
		
		StringBuilder deleteSql = new StringBuilder(" Delete From RMTGenDesignations");
		deleteSql.append(StringUtils.trimToEmpty(type) );
		deleteSql.append(" Where GenDesignation =:GenDesignation");
		
		logger.debug("deleteSql: "+ deleteSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(generalDesignation);

		try{
			recordCount = this.namedParameterJdbcTemplate.update(deleteSql.toString(), beanParameters);
			
			if (recordCount <= 0) {
				ErrorDetails errorDetails=  getError("41003",generalDesignation.getGenDesignation(),
						generalDesignation.getUserDetails().getUsrLanguage());
				throw new DataAccessException(errorDetails.getError()) {};
			}
		}catch(DataAccessException e){
			logger.error("Exception: ", e);
			ErrorDetails errorDetails=  getError("41006",generalDesignation.getGenDesignation(),
					generalDesignation.getUserDetails().getUsrLanguage());
			throw new DataAccessException(errorDetails.getError()) {};
		}
		logger.debug("Leaving");
	}
	
	/**
	 * This method insert new Records into RMTGenDesignations or RMTGenDesignations_Temp.
	 *
	 * save General Designation 
	 * 
	 * @param General Designation (generalDesignation)
	 * @param  type (String)
	 * 			""/_Temp/_View          
	 * @return void
	 * @throws DataAccessException
	 * 
	 */
	@Override
	public String save(GeneralDesignation generalDesignation,String type) {
		logger.debug("Entering");
		
		StringBuilder insertSql = new StringBuilder("Insert Into RMTGenDesignations" );
		insertSql.append(StringUtils.trimToEmpty(type) );
		insertSql.append(" (GenDesignation, GenDesgDesc," );
		insertSql.append(" Version , LastMntBy, LastMntOn, RecordStatus, RoleCode, NextRoleCode," );
		insertSql.append(" TaskId, NextTaskId, RecordType, WorkflowId)" );
		insertSql.append(" Values(:GenDesignation, :GenDesgDesc,");
		insertSql.append(" :Version , :LastMntBy, :LastMntOn, :RecordStatus, :RoleCode," );
		insertSql.append(" :NextRoleCode, :TaskId, :NextTaskId, :RecordType, :WorkflowId)");
		
		logger.debug("insertSql: "+ insertSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(generalDesignation);
		this.namedParameterJdbcTemplate.update(insertSql.toString(), beanParameters);
		
		logger.debug("Leaving");
		return generalDesignation.getId();
	}
	
	/**
	 * This method updates the Record RMTGenDesignations or RMTGenDesignations_Temp.
	 * if Record not updated then throws DataAccessException with  error  41004.
	 * update General Designation by key GenDesignation and Version
	 * 
	 * @param General Designation (generalDesignation)
	 * @param  type (String)
	 * 			""/_Temp/_View          
	 * @return void
	 * @throws DataAccessException
	 * 
	 */
	@SuppressWarnings("serial")
	@Override
	public void update(GeneralDesignation generalDesignation,String type) {
		int recordCount = 0;
		logger.debug("Entering");
		
		StringBuilder updateSql = new StringBuilder("Update RMTGenDesignations" );
		updateSql.append(StringUtils.trimToEmpty(type) ); 
		updateSql.append(" Set GenDesgDesc = :GenDesgDesc," );
		updateSql.append(" Version = :Version , LastMntBy = :LastMntBy, LastMntOn = :LastMntOn,");
		updateSql.append(" RecordStatus= :RecordStatus, RoleCode = :RoleCode," );
		updateSql.append(" NextRoleCode = :NextRoleCode, TaskId = :TaskId, NextTaskId = :NextTaskId," );
		updateSql.append(" RecordType = :RecordType, WorkflowId = :WorkflowId" );
		updateSql.append(" Where GenDesignation =:GenDesignation");
		
		if (!type.endsWith("_Temp")){
			updateSql.append("  AND Version= :Version-1");
		}
		
		logger.debug("updateSql: "+ updateSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(generalDesignation);
		recordCount = this.namedParameterJdbcTemplate.update(updateSql.toString(), beanParameters);
		
		if (recordCount <= 0) {
			logger.debug("Error in Update Method Count :"+recordCount);
			ErrorDetails errorDetails= getError("41004",generalDesignation.getGenDesignation(),
					generalDesignation.getUserDetails().getUsrLanguage());
			throw new DataAccessException(errorDetails.getError()) {};
		}
		logger.debug("Leaving");
	}
	
	private ErrorDetails  getError(String errorId,String genDesignation, String userLanguage){
		String[][] parms= new String[2][1]; 
		
		parms[1][0] = genDesignation;
		parms[0][0] = PennantJavaUtil.getLabel("label_GenDesignation")+ ":" + parms[1][0];
		return ErrorUtil.getErrorDetail(new ErrorDetails(PennantConstants.KEY_FIELD,
				errorId, parms[0],parms[1]), userLanguage);
	}
}