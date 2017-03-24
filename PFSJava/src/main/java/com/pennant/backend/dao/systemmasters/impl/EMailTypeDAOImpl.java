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
 * FileName    		:  EMailTypeDAOImpl.java                                                   * 	  
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
import com.pennant.backend.dao.systemmasters.EMailTypeDAO;
import com.pennant.backend.model.ErrorDetails;
import com.pennant.backend.model.systemmasters.EMailType;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantJavaUtil;

/**
 * DAO methods implementation for the <b>EMailType model</b> class.<br>
 * 
 */
public class EMailTypeDAOImpl extends BasisCodeDAO<EMailType> implements EMailTypeDAO {

	private static Logger logger = Logger.getLogger(EMailTypeDAOImpl.class);

	// Spring Named JDBC Template
	private NamedParameterJdbcTemplate namedParameterJdbcTemplate;

	public EMailTypeDAOImpl() {
		super();
	}

	/**
	 * Fetch the Record EMail Types details by key field
	 * 
	 * @param id
	 *            (String)
	 * @param type
	 *            (String) ""/_Temp/_View
	 * @return EMailType
	 */
	@Override
	public EMailType getEMailTypeById(final String id, String type) {
		logger.debug("Entering");
		EMailType eMailType = new EMailType();
		eMailType.setId(id);
		StringBuilder selectSql = new StringBuilder();

		selectSql.append("SELECT EmailTypeCode, EmailTypeDesc, EmailTypePriority, EmailTypeIsActive," );
		selectSql.append(" Version, LastMntOn, LastMntBy,RecordStatus, RoleCode, NextRoleCode, TaskId, NextTaskId, RecordType, WorkflowId" );
		selectSql.append(" FROM  BMTEMailTypes");
		selectSql.append(StringUtils.trimToEmpty(type));
		selectSql.append(" Where EmailTypeCode =:EmailTypeCode") ;
				
		logger.debug("selectSql: " + selectSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(eMailType);
		RowMapper<EMailType> typeRowMapper = ParameterizedBeanPropertyRowMapper.newInstance(EMailType.class);

		try {
			eMailType = this.namedParameterJdbcTemplate.queryForObject(selectSql.toString(), beanParameters, typeRowMapper);
		} catch (EmptyResultDataAccessException e) {
			logger.error("Exception: ", e);
			eMailType = null;
		}
		logger.debug("Leaving");
		return eMailType;
	}

	/**
	 * @param dataSource
	 *            the dataSource to set
	 */
	public void setDataSource(DataSource dataSource) {
		this.namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
	}

	/**
	 * This method Deletes the Record from the BMTEMailTypes or
	 * BMTEMailTypes_Temp. if Record not deleted then throws DataAccessException
	 * with error 41003. delete EMail Types by key EmailTypeCode
	 * 
	 * @param EMail
	 *            Types (eMailType)
	 * @param type
	 *            (String) ""/_Temp/_View
	 * @return void
	 * @throws DataAccessException
	 * 
	 */
	@SuppressWarnings("serial")
	public void delete(EMailType eMailType, String type) {
		logger.debug("Entering");
		int recordCount = 0;
		StringBuilder deleteSql = new StringBuilder();

		deleteSql.append("Delete From BMTEMailTypes");
		deleteSql.append(StringUtils.trimToEmpty(type));
		deleteSql.append(" Where EmailTypeCode =:EmailTypeCode");
		
		logger.debug("deleteSql: "+ deleteSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(eMailType);

		try {
			recordCount = this.namedParameterJdbcTemplate.update(deleteSql.toString(),	beanParameters);

			if (recordCount <= 0) {
				ErrorDetails errorDetails= getError("41004", eMailType.getEmailTypeCode(), eMailType.getUserDetails().getUsrLanguage());
				throw new DataAccessException(errorDetails.getError()) {
				};
			}
		} catch (DataAccessException e) {
			logger.debug("Error in delete Method");
			logger.error("Exception: ", e);
			ErrorDetails errorDetails= getError("41006", eMailType.getEmailTypeCode(), eMailType.getUserDetails().getUsrLanguage());
			throw new DataAccessException(errorDetails.getError()) {
			};
		}
		logger.debug("Leaving");
	}

	/**
	 * This method insert new Records into BMTEMailTypes or BMTEMailTypes_Temp.
	 * 
	 * save EMail Types
	 * 
	 * @param EMail
	 *            Types (eMailType)
	 * @param type
	 *            (String) ""/_Temp/_View
	 * @return void
	 * @throws DataAccessException
	 * 
	 */
	@Override
	public String save(EMailType eMailType, String type) {
		logger.debug("Entering");
		StringBuilder insertSql = new StringBuilder();

		insertSql.append("Insert Into BMTEMailTypes");
		insertSql.append(StringUtils.trimToEmpty(type));
		insertSql.append(" (EmailTypeCode, EmailTypeDesc, EmailTypePriority, EmailTypeIsActive," );
		insertSql.append(" Version , LastMntBy, LastMntOn, RecordStatus, RoleCode, NextRoleCode, TaskId, NextTaskId," );
		insertSql.append(" RecordType, WorkflowId)");
		insertSql.append(" Values(:EmailTypeCode, :EmailTypeDesc, :EmailTypePriority, :EmailTypeIsActive, " );
		insertSql.append(" :Version , :LastMntBy, :LastMntOn, :RecordStatus, :RoleCode, :NextRoleCode, :TaskId, :NextTaskId, ");
		insertSql.append(" :RecordType, :WorkflowId)");
		
		logger.debug("insertSql: "+ insertSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(eMailType);
		this.namedParameterJdbcTemplate.update(insertSql.toString(), beanParameters);

		logger.debug("Leaving");
		return eMailType.getId();
	}

	/**
	 * This method updates the Record BMTEMailTypes or BMTEMailTypes_Temp. if
	 * Record not updated then throws DataAccessException with error 41004.
	 * update EMail Types by key EmailTypeCode and Version
	 * 
	 * @param EMail
	 *            Types (eMailType)
	 * @param type
	 *            (String) ""/_Temp/_View
	 * @return void
	 * @throws DataAccessException
	 * 
	 */
	@SuppressWarnings("serial")
	@Override
	public void update(EMailType eMailType, String type) {
		logger.debug("Entering");
		int recordCount = 0;
		StringBuilder updateSql = new StringBuilder();

		updateSql.append("Update BMTEMailTypes");
		updateSql.append(StringUtils.trimToEmpty(type));
		updateSql.append(" Set EmailTypeCode = :EmailTypeCode, EmailTypeDesc = :EmailTypeDesc," );
		updateSql.append(" EmailTypePriority = :EmailTypePriority, EmailTypeIsActive = :EmailTypeIsActive ," );
		updateSql.append(" Version = :Version , LastMntBy = :LastMntBy, LastMntOn = :LastMntOn, " );
		updateSql.append(" RecordStatus= :RecordStatus, RoleCode = :RoleCode,NextRoleCode = :NextRoleCode, TaskId = :TaskId," );
		updateSql.append(" NextTaskId = :NextTaskId, RecordType = :RecordType, WorkflowId = :WorkflowId" );
		updateSql.append(" Where EmailTypeCode =:EmailTypeCode ");
		if (!type.endsWith("_Temp")){
			updateSql.append(" AND Version= :Version-1");
		}

		logger.debug("updateSql: "+ updateSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(eMailType);
		recordCount = this.namedParameterJdbcTemplate.update(updateSql.toString(),	beanParameters);

		if (recordCount <= 0) {
			logger.debug("Error in Update Method Count :" + recordCount);

			ErrorDetails errorDetails= getError("41003", eMailType.getEmailTypeCode(), eMailType.getUserDetails().getUsrLanguage());	
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
	private ErrorDetails  getError(String errorId, String emailTypeCode,String userLanguage){
		String[][] parms= new String[2][2]; 
		parms[1][0] = emailTypeCode;
		parms[0][0] = PennantJavaUtil.getLabel("label_EmailTypeCode")+ ":" + parms[1][0];
		return ErrorUtil.getErrorDetail(new ErrorDetails(PennantConstants.KEY_FIELD, errorId, parms[0],parms[1]), userLanguage);
	}
}