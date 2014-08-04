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
 * FileName    		:  PropertyRelationTypeDAOImpl.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  29-09-2011    														*
 *                                                                  						*
 * Modified Date    :  29-09-2011    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 29-09-2011       Pennant	                 0.1                                            * 
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

package com.pennant.backend.dao.amtmasters.impl;


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
import com.pennant.backend.dao.amtmasters.PropertyRelationTypeDAO;
import com.pennant.backend.dao.impl.BasisNextidDaoImpl;
import com.pennant.backend.model.ErrorDetails;
import com.pennant.backend.model.WorkFlowDetails;
import com.pennant.backend.model.amtmasters.PropertyRelationType;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantJavaUtil;
import com.pennant.backend.util.WorkFlowUtil;

/**
 * DAO methods implementation for the <b>PropertyRelationType model</b> class.<br>
 * 
 */

public class PropertyRelationTypeDAOImpl extends BasisNextidDaoImpl<PropertyRelationType> implements PropertyRelationTypeDAO {

	private static Logger logger = Logger.getLogger(PropertyRelationTypeDAOImpl.class);

	// Spring Named JDBC Template
	private NamedParameterJdbcTemplate namedParameterJdbcTemplate;

	/**
	 * This method set the Work Flow id based on the module name and return the new PropertyRelationType 
	 * @return PropertyRelationType
	 */
	@Override
	public PropertyRelationType getPropertyRelationType() {
		logger.debug("Entering");
		WorkFlowDetails workFlowDetails=WorkFlowUtil.getWorkFlowDetails("PropertyRelationType");
		PropertyRelationType propertyRelationType= new PropertyRelationType();
		if (workFlowDetails!=null){
			propertyRelationType.setWorkflowId(workFlowDetails.getWorkFlowId());
		}
		logger.debug("Leaving");
		return propertyRelationType;
	}

	/**
	 * This method get the module from method getPropertyRelationType() and 
	 * set the new record flag as true and return PropertyRelationType()   
	 * @return PropertyRelationType
	 */
	@Override
	public PropertyRelationType getNewPropertyRelationType() {
		logger.debug("Entering");
		PropertyRelationType propertyRelationType = getPropertyRelationType();
		propertyRelationType.setNewRecord(true);
		logger.debug("Leaving");
		return propertyRelationType;
	}

	/**
	 * Fetch the Record  Property Relation Type details by key field
	 * 
	 * @param id (int)
	 * @param  type (String)
	 * 			""/_Temp/_View          
	 * @return PropertyRelationType
	 */
	@Override
	public PropertyRelationType getPropertyRelationTypeById(final long id, String type) {
		logger.debug("Entering");
		PropertyRelationType propertyRelationType = new PropertyRelationType();
		propertyRelationType.setId(id);
		StringBuilder selectSql = new StringBuilder();

		selectSql.append("Select PropertyRelationTypeId, PropertyRelationTypeName,");
		selectSql.append(" Version , LastMntBy, LastMntOn, RecordStatus, RoleCode, " );
		selectSql.append(" NextRoleCode, TaskId, NextTaskId, RecordType, WorkflowId");
		selectSql.append(" From AMTPropertyRelationType");
		selectSql.append(StringUtils.trimToEmpty(type));
		selectSql.append(" Where PropertyRelationTypeId =:PropertyRelationTypeId");

		logger.debug("selectSql: " + selectSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(propertyRelationType);
		RowMapper<PropertyRelationType> typeRowMapper = ParameterizedBeanPropertyRowMapper.newInstance(
				PropertyRelationType.class);

		try{
			propertyRelationType = this.namedParameterJdbcTemplate.queryForObject(selectSql.toString(), 
					beanParameters, typeRowMapper);	
		}catch (EmptyResultDataAccessException e) {
			propertyRelationType = null;
		}
		logger.debug("Leaving");
		return propertyRelationType;
	}

	/**
	 * This method initialize the Record.
	 * @param PropertyRelationType (propertyRelationType)
	 * @return PropertyRelationType
	 */
	@Override
	public void initialize(PropertyRelationType propertyRelationType) {
		super.initialize(propertyRelationType);
	}
	
	/**
	 * This method refresh the Record.
	 * @param PropertyRelationType (propertyRelationType)
	 * @return void
	 */
	@Override
	public void refresh(PropertyRelationType propertyRelationType) {

	}

	/**
	 * To Set  dataSource
	 * @param dataSource
	 */
	public void setDataSource(DataSource dataSource) {
		this.namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
	}

	/**
	 * This method Deletes the Record from the AMTPropertyRelationType or AMTPropertyRelationType_Temp.
	 * if Record not deleted then throws DataAccessException with  error  41003.
	 * delete Property Relation Type by key PropertyRelationTypeId
	 * 
	 * @param Property Relation Type (propertyRelationType)
	 * @param  type (String)
	 * 			""/_Temp/_View          
	 * @return void
	 * @throws DataAccessException
	 * 
	 */
	@SuppressWarnings("serial")
	public void delete(PropertyRelationType propertyRelationType,String type) {
		logger.debug("Entering delete Method");
		int recordCount = 0;
		StringBuilder deleteSql = new StringBuilder();

		deleteSql.append("Delete From AMTPropertyRelationType");
		deleteSql.append(StringUtils.trimToEmpty(type));
		deleteSql.append(" Where PropertyRelationTypeId =:PropertyRelationTypeId");

		logger.debug("deleteSql: " + deleteSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(propertyRelationType);

		try{
			recordCount = this.namedParameterJdbcTemplate.update(deleteSql.toString(), beanParameters);
			if (recordCount <= 0) {
				ErrorDetails errorDetails= getError("41003", 
						propertyRelationType.getPropertyRelationTypeId(), 
						propertyRelationType.getUserDetails().getUsrLanguage());
				throw new DataAccessException(errorDetails.getError()) {};
			}
		}catch(DataAccessException e){
			logger.error(e);
			ErrorDetails errorDetails= getError("41006", 
					propertyRelationType.getPropertyRelationTypeId(), 
					propertyRelationType.getUserDetails().getUsrLanguage());
			throw new DataAccessException(errorDetails.getError()) {};
		}
		logger.debug("Leaving");
	}

	/**
	 * This method insert new Records into AMTPropertyRelationType or AMTPropertyRelationType_Temp.
	 * it fetches the available Sequence form SeqAMTPropertyRelationType by using 
	 * getNextidviewDAO().getNextId() method.  
	 *
	 * save Property Relation Type 
	 * 
	 * @param Property Relation Type (propertyRelationType)
	 * @param  type (String)
	 * 			""/_Temp/_View          
	 * @return void
	 * @throws DataAccessException
	 * 
	 */

	@Override
	public long save(PropertyRelationType propertyRelationType,String type) {
		logger.debug("Entering");
		if (propertyRelationType.getId()==Long.MIN_VALUE){
			propertyRelationType.setId(getNextidviewDAO().getNextId("SeqAMTPropertyRelationType"));
			logger.debug("get NextID:"+propertyRelationType.getId());
		}
		StringBuilder insertSql = new StringBuilder();
		insertSql.append("Insert Into AMTPropertyRelationType");
		insertSql.append(StringUtils.trimToEmpty(type));
		insertSql.append(" (PropertyRelationTypeId, PropertyRelationTypeName,"); 
		insertSql.append(" Version , LastMntBy, LastMntOn, RecordStatus, RoleCode, " );
		insertSql.append(" NextRoleCode, TaskId, NextTaskId, RecordType, WorkflowId)");
		insertSql.append(" Values(:PropertyRelationTypeId, :PropertyRelationTypeName,");
		insertSql.append(" :Version , :LastMntBy, :LastMntOn, :RecordStatus, :RoleCode, " );
		insertSql.append(" :NextRoleCode, :TaskId, :NextTaskId, :RecordType, :WorkflowId)");
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(propertyRelationType);
		this.namedParameterJdbcTemplate.update(insertSql.toString(), beanParameters);

		logger.debug("Leaving");
		return propertyRelationType.getId();
	}

	/**
	 * This method updates the Record AMTPropertyRelationType or AMTPropertyRelationType_Temp.
	 * if Record not updated then throws DataAccessException with  error  41004.
	 * update Property Relation Type by key PropertyRelationTypeId and Version
	 * 
	 * @param Property Relation Type (propertyRelationType)
	 * @param  type (String)
	 * 			""/_Temp/_View          
	 * @return void
	 * @throws DataAccessException
	 * 
	 */

	@SuppressWarnings("serial")
	@Override
	public void update(PropertyRelationType propertyRelationType,String type) {
		int recordCount = 0;
		logger.debug("Entering");
		StringBuilder updateSql = new StringBuilder();
		updateSql.append("Update AMTPropertyRelationType");
		updateSql.append(StringUtils.trimToEmpty(type)); 
		updateSql.append(" Set PropertyRelationTypeId = :PropertyRelationTypeId, " );
		updateSql.append(" PropertyRelationTypeName = :PropertyRelationTypeName,");
		updateSql.append(" Version = :Version , LastMntBy = :LastMntBy, LastMntOn = :LastMntOn, " );
		updateSql.append(" RecordStatus= :RecordStatus, RoleCode = :RoleCode, NextRoleCode = :NextRoleCode, " );
		updateSql.append(" TaskId = :TaskId, NextTaskId = :NextTaskId, RecordType = :RecordType, " );
		updateSql.append(" WorkflowId = :WorkflowId ");
		updateSql.append(" Where PropertyRelationTypeId =:PropertyRelationTypeId");


		if (!type.endsWith("_TEMP")){
			updateSql.append("  AND Version= :Version-1");
		}
		logger.debug("updateSql: " + updateSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(propertyRelationType);
		recordCount = this.namedParameterJdbcTemplate.update(updateSql.toString(), beanParameters);

		if (recordCount <= 0) {
			logger.debug("Error Update Method Count :"+recordCount);
			ErrorDetails errorDetails= getError("41004", 
					propertyRelationType.getPropertyRelationTypeId(), 
					propertyRelationType.getUserDetails().getUsrLanguage());
			throw new DataAccessException(errorDetails.getError()) {};
		}
		logger.debug("Leaving");
	}

	/**
	 * This method for getting the error details
	 * @param errorId (String)
	 * @param Id (String)
	 * @param userLanguage (String)
	 * @return ErrorDetails
	 */
	private ErrorDetails  getError(String errorId, long propertyRelationTypeId, String userLanguage){
		String[][] parms= new String[2][1]; 
		parms[1][0] =String.valueOf(propertyRelationTypeId);
		parms[0][0] = PennantJavaUtil.getLabel("label_PropertyRelationTypeId")+":" +parms[1][0];
		return ErrorUtil.getErrorDetail(new ErrorDetails(PennantConstants.KEY_FIELD, 
				errorId, parms[0],parms[1]), userLanguage);
	}

}