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
 * FileName    		:  PropertyTypeDAOImpl.java                                                   * 	  
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
import com.pennant.backend.dao.amtmasters.PropertyTypeDAO;
import com.pennant.backend.dao.impl.BasisNextidDaoImpl;
import com.pennant.backend.model.ErrorDetails;
import com.pennant.backend.model.WorkFlowDetails;
import com.pennant.backend.model.amtmasters.PropertyType;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantJavaUtil;
import com.pennant.backend.util.WorkFlowUtil;

/**
 * DAO methods implementation for the <b>PropertyType model</b> class.<br>
 */
public class PropertyTypeDAOImpl extends BasisNextidDaoImpl<PropertyType> implements PropertyTypeDAO {

	private static Logger logger = Logger.getLogger(PropertyTypeDAOImpl.class);

	// Spring Named JDBC Template
	private NamedParameterJdbcTemplate namedParameterJdbcTemplate;

	/**
	 * This method set the Work Flow id based on the module name and return the new PropertyType 
	 * @return PropertyType
	 */
	@Override
	public PropertyType getPropertyType() {
		logger.debug("Entering");
		WorkFlowDetails workFlowDetails=WorkFlowUtil.getWorkFlowDetails("PropertyType");
		PropertyType propertyType= new PropertyType();
		if (workFlowDetails!=null){
			propertyType.setWorkflowId(workFlowDetails.getWorkFlowId());
		}
		logger.debug("Leaving");
		return propertyType;
	}

	/**
	 * This method get the module from method getPropertyType() and 
	 * set the new record flag as true and return PropertyType()   
	 * @return PropertyType
	 */
	@Override
	public PropertyType getNewPropertyType() {
		logger.debug("Entering");
		PropertyType propertyType = getPropertyType();
		propertyType.setNewRecord(true);
		logger.debug("Leaving");
		return propertyType;
	}

	/**
	 * Fetch the Record  Property Type details by key field
	 * 
	 * @param id (int)
	 * @param  type (String)
	 * 			""/_Temp/_View          
	 * @return PropertyType
	 */
	@Override
	public PropertyType getPropertyTypeById(final long id, String type) {
		logger.debug("Entering");
		PropertyType propertyType = getPropertyType();
		propertyType.setId(id);
		StringBuilder selectSql = new StringBuilder();

		selectSql.append("Select PropertyTypeId, PropertyTypeName,");
		selectSql.append(" Version , LastMntBy, LastMntOn, RecordStatus, RoleCode, " );
		selectSql.append(" NextRoleCode, TaskId, NextTaskId, RecordType, WorkflowId");
		selectSql.append(" From AMTPropertyType");
		selectSql.append(StringUtils.trimToEmpty(type));
		selectSql.append(" Where PropertyTypeId =:PropertyTypeId");

		logger.debug("selectSql: " + selectSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(propertyType);
		RowMapper<PropertyType> typeRowMapper = ParameterizedBeanPropertyRowMapper.newInstance(
				PropertyType.class);

		try{
			propertyType = this.namedParameterJdbcTemplate.queryForObject(selectSql.toString(), 
					beanParameters, typeRowMapper);	
		}catch (EmptyResultDataAccessException e) {
			propertyType = null;
		}
		logger.debug("Leaving getPropertyTypeByID()");
		return propertyType;
	}

	/**
	 * This method initialize the Record.
	 * @param PropertyType (propertyType)
	 * @return PropertyType
	 */
	@Override
	public void initialize(PropertyType propertyType) {
		super.initialize(propertyType);
	}
	
	/**
	 * This method refresh the Record.
	 * @param PropertyType (propertyType)
	 * @return void
	 */
	@Override
	public void refresh(PropertyType propertyType) {

	}

	/**
	 * To Set  dataSource
	 * @param dataSource
	 */
	public void setDataSource(DataSource dataSource) {
		this.namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
	}

	/**
	 * This method Deletes the Record from the AMTPropertyType or AMTPropertyType_Temp.
	 * if Record not deleted then throws DataAccessException with  error  41003.
	 * delete Property Type by key PropertyTypeId
	 * 
	 * @param Property Type (propertyType)
	 * @param  type (String)
	 * 			""/_Temp/_View          
	 * @return void
	 * @throws DataAccessException
	 * 
	 */
	@SuppressWarnings("serial")
	public void delete(PropertyType propertyType,String type) {
		logger.debug("Entering");
		int recordCount = 0;
		StringBuilder deleteSql =new StringBuilder();

		deleteSql.append("Delete From AMTPropertyType");
		deleteSql.append(StringUtils.trimToEmpty(type));
		deleteSql.append(" Where PropertyTypeId =:PropertyTypeId");

		logger.debug("deleteSql: " + deleteSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(propertyType);
		try{
			recordCount = this.namedParameterJdbcTemplate.update(deleteSql.toString(), beanParameters);
			if (recordCount <= 0) {
				ErrorDetails errorDetails= getError("41003",propertyType.getPropertyTypeId(),
						propertyType.getUserDetails().getUsrLanguage());
				throw new DataAccessException(errorDetails.getError()) {};
			}
		}catch(DataAccessException e){
			logger.error(e);
			ErrorDetails errorDetails= getError("41006",propertyType.getPropertyTypeId(), 
					propertyType.getUserDetails().getUsrLanguage());
			throw new DataAccessException(errorDetails.getError()) {};
		}
		logger.debug("Leaving");
	}

	/**
	 * This method insert new Records into AMTPropertyType or AMTPropertyType_Temp.
	 * it fetches the available Sequence form SeqAMTPropertyType by using 
	 * getNextidviewDAO().getNextId() method.  
	 *
	 * save Property Type 
	 * 
	 * @param Property Type (propertyType)
	 * @param  type (String)
	 * 			""/_Temp/_View          
	 * @return void
	 * @throws DataAccessException
	 * 
	 */

	@Override
	public long save(PropertyType propertyType,String type) {
		logger.debug("Entering");
		if (propertyType.getId()==Long.MIN_VALUE){
			propertyType.setId(getNextidviewDAO().getNextId("SeqAMTPropertyType"));
			logger.debug("get NextID:"+propertyType.getId());
		}
		StringBuilder insertSql = new StringBuilder();
		insertSql.append("Insert Into AMTPropertyType");
		insertSql.append(StringUtils.trimToEmpty(type));
		insertSql.append(" (PropertyTypeId, PropertyTypeName,");
		insertSql.append(" Version , LastMntBy, LastMntOn, RecordStatus, RoleCode, " );
		insertSql.append(" NextRoleCode, TaskId, NextTaskId, RecordType, WorkflowId)");
		insertSql.append(" Values(:PropertyTypeId, :PropertyTypeName,");
		insertSql.append(" :Version , :LastMntBy, :LastMntOn, :RecordStatus, :RoleCode, " );
		insertSql.append(" :NextRoleCode, :TaskId, :NextTaskId, :RecordType, :WorkflowId)");

		logger.debug("insertSql: " + insertSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(propertyType);
		this.namedParameterJdbcTemplate.update(insertSql.toString(), beanParameters);

		logger.debug("Leaving");
		return propertyType.getId();
	}

	/**
	 * This method updates the Record AMTPropertyType or AMTPropertyType_Temp.
	 * if Record not updated then throws DataAccessException with  error  41004.
	 * update Property Type by key PropertyTypeId and Version
	 * 
	 * @param Property Type (propertyType)
	 * @param  type (String)
	 * 			""/_Temp/_View          
	 * @return void
	 * @throws DataAccessException
	 * 
	 */

	@SuppressWarnings("serial")
	@Override
	public void update(PropertyType propertyType,String type) {
		int recordCount = 0;
		logger.debug("Entering");
		StringBuilder updateSql = new StringBuilder();

		updateSql.append("Update AMTPropertyType");
		updateSql.append(StringUtils.trimToEmpty(type)); 
		updateSql.append(" Set PropertyTypeId = :PropertyTypeId, PropertyTypeName = :PropertyTypeName,");
		updateSql.append(" Version = :Version , LastMntBy = :LastMntBy, LastMntOn = :LastMntOn, " );
		updateSql.append(" RecordStatus= :RecordStatus, RoleCode = :RoleCode, NextRoleCode = :NextRoleCode, " );
		updateSql.append(" TaskId = :TaskId, NextTaskId = :NextTaskId, RecordType = :RecordType, " );
		updateSql.append(" WorkflowId = :WorkflowId");
		updateSql.append(" Where PropertyTypeId =:PropertyTypeId");

		if (!type.endsWith("_TEMP")){
			updateSql.append("  AND Version= :Version-1");
		}
		logger.debug("updateSql: " + updateSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(propertyType);
		recordCount = this.namedParameterJdbcTemplate.update(updateSql.toString(), beanParameters);

		if (recordCount <= 0) {
			logger.debug("Error Update Method Count :"+recordCount);
			ErrorDetails errorDetails= getError("41004",propertyType.getPropertyTypeId(),
					propertyType.getUserDetails().getUsrLanguage());
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
	private ErrorDetails  getError(String errorId, long propertyTypeId, String userLanguage){
		String[][] parms= new String[2][1]; 
		parms[1][0] =String.valueOf(propertyTypeId);
		parms[0][0] = PennantJavaUtil.getLabel("label_PropertyTypeId")+":" +parms[1][0];
		return ErrorUtil.getErrorDetail(new ErrorDetails(PennantConstants.KEY_FIELD, 
				errorId, parms[0],parms[1]), userLanguage);
	}


}