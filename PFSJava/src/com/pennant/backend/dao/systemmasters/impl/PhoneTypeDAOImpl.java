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
 * FileName    		:  PhoneTypeDAOImpl.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  06-05-2011    														*
 *                                                                  						*
 * Modified Date    :  06-05-2011    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 06-05-2011       Pennant	                 0.1                                            * 
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
import com.pennant.backend.dao.systemmasters.PhoneTypeDAO;
import com.pennant.backend.model.ErrorDetails;
import com.pennant.backend.model.WorkFlowDetails;
import com.pennant.backend.model.systemmasters.PhoneType;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantJavaUtil;
import com.pennant.backend.util.WorkFlowUtil;

/**
 * DAO methods implementation for the <b>PhoneType model</b> class.<br>
 * 
 */
public class PhoneTypeDAOImpl extends BasisCodeDAO<PhoneType> implements PhoneTypeDAO {

	private static Logger logger = Logger.getLogger(PhoneTypeDAOImpl.class);

	// Spring Named JDBC Template
	private NamedParameterJdbcTemplate namedParameterJdbcTemplate;

	/**
	 * This method set the Work Flow id based on the module name and return the
	 * new PhoneType
	 * 
	 * @return PhoneType
	 */
	@Override
	public PhoneType getPhoneType() {
		logger.debug("Entering");
		WorkFlowDetails workFlowDetails = WorkFlowUtil.getWorkFlowDetails("PhoneType");
		PhoneType phoneType = new PhoneType();
		if (workFlowDetails != null) {
			phoneType.setWorkflowId(workFlowDetails.getWorkFlowId());
		}
		logger.debug("Leaving");
		return phoneType;
	}

	/**
	 * This method get the module from method getPhoneType() and set the new
	 * record flag as true and return PhoneType()
	 * 
	 * @return PhoneType
	 */
	@Override
	public PhoneType getNewPhoneType() {
		logger.debug("Entering");
		PhoneType phoneType = getPhoneType();
		phoneType.setNewRecord(true);
		logger.debug("Leaving");
		return phoneType;
	}

	/**
	 * Fetch the Record Phone Types details by key field
	 * 
	 * @param id
	 *            (String)
	 * @param type
	 *            (String) ""/_Temp/_View
	 * @return PhoneType
	 */
	@Override
	public PhoneType getPhoneTypeById(final String id, String type) {
		logger.debug("Entering");
		PhoneType phoneType = getPhoneType();
		phoneType.setId(id);
		StringBuilder selectSql = new StringBuilder();

		selectSql.append("Select PhoneTypeCode, PhoneTypeDesc, PhoneTypePriority, PhoneTypeIsActive,");
		/*if(type.contains("View")){
			selectSql.append("");
		}*/
		selectSql.append(" Version , LastMntBy, LastMntOn, RecordStatus, RoleCode, NextRoleCode, TaskId,");
		selectSql.append(" NextTaskId, RecordType, WorkflowId");
		selectSql.append(" From BMTPhoneTypes");
		selectSql.append(StringUtils.trimToEmpty(type));
		selectSql.append(" Where PhoneTypeCode =:PhoneTypeCode");

		logger.debug("selectListSql: " + selectSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(phoneType);
		RowMapper<PhoneType> typeRowMapper = ParameterizedBeanPropertyRowMapper.newInstance(PhoneType.class);

		try {
			phoneType = this.namedParameterJdbcTemplate.queryForObject(selectSql.toString(), beanParameters, typeRowMapper);
		} catch (EmptyResultDataAccessException e) {
			logger.error(e);
			phoneType = null;
		}
		logger.debug("Leaving");
		return phoneType;
	}

	/**
	 * This method initialize the Record.
	 * 
	 * @param PhoneType
	 *            (phoneType)
	 * @return PhoneType
	 */
	@Override
	public void initialize(PhoneType phoneType) {
		super.initialize(phoneType);
	}

	/**
	 * This method refresh the Record.
	 * 
	 * @param PhoneType
	 *            (phoneType)
	 * @return void
	 */
	@Override
	public void refresh(PhoneType phoneType) {

	}

	/**
	 * @param dataSource
	 *            the dataSource to set
	 */
	public void setDataSource(DataSource dataSource) {
		this.namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
	}

	/**
	 * This method Deletes the Record from the BMTPhoneTypes or
	 * BMTPhoneTypes_Temp. if Record not deleted then throws DataAccessException
	 * with error 41003. delete Phone Types by key PhoneTypeCode
	 * 
	 * @param Phone
	 *            Types (phoneType)
	 * @param type
	 *            (String) ""/_Temp/_View
	 * @return void
	 * @throws DataAccessException
	 * 
	 */
	@SuppressWarnings("serial")
	public void delete(PhoneType phoneType, String type) {
		logger.debug("Entering");
		int recordCount = 0;
		StringBuilder deleteSql = new StringBuilder();
		
		deleteSql.append(" Delete From BMTPhoneTypes");
		deleteSql.append(StringUtils.trimToEmpty(type));
		deleteSql.append(" Where PhoneTypeCode =:PhoneTypeCode");
		
		logger.debug("deleteSql: "+ deleteSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(phoneType);

		try {
			recordCount = this.namedParameterJdbcTemplate.update(deleteSql.toString(),beanParameters);

			if (recordCount <= 0) {
				ErrorDetails errorDetails = getError("41004",phoneType.getPhoneTypeCode(), phoneType.getUserDetails().getUsrLanguage());
				throw new DataAccessException(errorDetails.getError()) {
				};
			}
		} catch (DataAccessException e) {
			logger.debug("Error in delete Method");
			logger.error(e);
			ErrorDetails errorDetails = getError("41006",phoneType.getPhoneTypeCode(), phoneType.getUserDetails().getUsrLanguage());
			throw new DataAccessException(errorDetails.getError()) {
			};
		}
		logger.debug("Leaving");
	}

	/**
	 * This method insert new Records into BMTPhoneTypes or BMTPhoneTypes_Temp.
	 * 
	 * save Phone Types
	 * 
	 * @param Phone
	 *            Types (phoneType)
	 * @param type
	 *            (String) ""/_Temp/_View
	 * @return void
	 * @throws DataAccessException
	 * 
	 */
	@Override
	public String save(PhoneType phoneType, String type) {
		logger.debug("Entering");
		StringBuilder insertSql = new StringBuilder();

		insertSql.append("Insert Into BMTPhoneTypes");
		insertSql.append(StringUtils.trimToEmpty(type));
		insertSql.append(" (PhoneTypeCode, PhoneTypeDesc, PhoneTypePriority, PhoneTypeIsActive,");
		insertSql.append(" Version , LastMntBy, LastMntOn, RecordStatus, RoleCode, NextRoleCode, TaskId, NextTaskId," );
		insertSql.append(" RecordType, WorkflowId)");
		insertSql.append(" Values(:PhoneTypeCode, :PhoneTypeDesc, :PhoneTypePriority, :PhoneTypeIsActive,");
		insertSql.append(" :Version , :LastMntBy, :LastMntOn, :RecordStatus, :RoleCode, :NextRoleCode, :TaskId, :NextTaskId, ");
		insertSql.append(" :RecordType, :WorkflowId)");
		
		logger.debug("insertSql: "+ insertSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(phoneType);
		this.namedParameterJdbcTemplate.update(insertSql.toString(), beanParameters);

		logger.debug("Leaving");
		return phoneType.getId();
	}

	/**
	 * This method updates the Record BMTPhoneTypes or BMTPhoneTypes_Temp. if
	 * Record not updated then throws DataAccessException with error 41004.
	 * update Phone Types by key PhoneTypeCode and Version
	 * 
	 * @param Phone
	 *            Types (phoneType)
	 * @param type
	 *            (String) ""/_Temp/_View
	 * @return void
	 * @throws DataAccessException
	 * 
	 */
	@SuppressWarnings("serial")
	@Override
	public void update(PhoneType phoneType, String type) {
		logger.debug("Entering");
		int recordCount = 0;
		StringBuilder updateSql = new StringBuilder();

		updateSql.append("Update BMTPhoneTypes");
		updateSql.append(StringUtils.trimToEmpty(type));
		updateSql.append(" Set PhoneTypeCode = :PhoneTypeCode, PhoneTypeDesc = :PhoneTypeDesc,");
		updateSql.append(" PhoneTypePriority = :PhoneTypePriority, PhoneTypeIsActive = :PhoneTypeIsActive,");
		updateSql.append(" Version = :Version , LastMntBy = :LastMntBy, LastMntOn = :LastMntOn,");
		updateSql.append(" RecordStatus= :RecordStatus, RoleCode = :RoleCode, NextRoleCode = :NextRoleCode,");
		updateSql.append(" TaskId = :TaskId, NextTaskId = :NextTaskId, RecordType = :RecordType, WorkflowId = :WorkflowId");
		updateSql.append(" Where PhoneTypeCode =:PhoneTypeCode");
		if (!type.endsWith("_TEMP")) {
			updateSql.append("  AND Version= :Version-1");
		}

		logger.debug("updateSql: "+ updateSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(phoneType);
		recordCount = this.namedParameterJdbcTemplate.update(updateSql.toString(),beanParameters);

		if (recordCount <= 0) {
			logger.debug("Error in Update Method Count :" + recordCount);

			ErrorDetails errorDetails = getError("41003",phoneType.getPhoneTypeCode(), phoneType.getUserDetails().getUsrLanguage());
			throw new DataAccessException(errorDetails.getError()) {
			};
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
	private ErrorDetails  getError(String errorId, String phoneTypeCode, String userLanguage){
		String[][] parms= new String[2][1]; 
		parms[1][0] = String.valueOf(phoneTypeCode);
		parms[0][0] = PennantJavaUtil.getLabel("label_PhoneType_Code")+ ":" + parms[1][0];
		return ErrorUtil.getErrorDetail(new ErrorDetails(PennantConstants.KEY_FIELD, errorId, parms[0],parms[1]), userLanguage);
	}

}