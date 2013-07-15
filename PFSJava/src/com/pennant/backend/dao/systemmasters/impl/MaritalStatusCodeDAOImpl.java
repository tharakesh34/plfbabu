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
 * FileName    		:  MaritalStatusCodeDAOImpl.java                                                   * 	  
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
import com.pennant.backend.dao.systemmasters.MaritalStatusCodeDAO;
import com.pennant.backend.model.ErrorDetails;
import com.pennant.backend.model.WorkFlowDetails;
import com.pennant.backend.model.systemmasters.MaritalStatusCode;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantJavaUtil;
import com.pennant.backend.util.WorkFlowUtil;

/**
 * DAO methods implementation for the <b>MaritalStatusCode model</b> class.<br>
 * 
 */
public class MaritalStatusCodeDAOImpl extends BasisCodeDAO<MaritalStatusCode>implements MaritalStatusCodeDAO {

	private static Logger logger = Logger.getLogger(MaritalStatusCodeDAOImpl.class);

	// Spring Named JDBC Template
	private NamedParameterJdbcTemplate namedParameterJdbcTemplate;

	/**
	 * This method set the Work Flow id based on the module name and return the
	 * new MaritalStatusCode
	 * 
	 * @return MaritalStatusCode
	 */
	@Override
	public MaritalStatusCode getMaritalStatusCode() {
		logger.debug("Entering");
		WorkFlowDetails workFlowDetails = WorkFlowUtil.getWorkFlowDetails("MaritalStatusCode");
		MaritalStatusCode maritalStatusCode = new MaritalStatusCode();
		if (workFlowDetails != null) {
			maritalStatusCode.setWorkflowId(workFlowDetails.getWorkFlowId());
		}
		logger.debug("Leaving");
		return maritalStatusCode;
	}

	/**
	 * This method get the module from method getMaritalStatusCode() and set the
	 * new record flag as true and return MaritalStatusCode()
	 * 
	 * @return MaritalStatusCode
	 */
	@Override
	public MaritalStatusCode getNewMaritalStatusCode() {
		logger.debug("Entering");
		MaritalStatusCode maritalStatusCode = getMaritalStatusCode();
		maritalStatusCode.setNewRecord(true);
		logger.debug("Leaving");
		return maritalStatusCode;
	}

	/**
	 * Fetch the Record MaritalStatus Codes details by key field
	 * 
	 * @param id
	 *            (String)
	 * @param type
	 *            (String) ""/_Temp/_View
	 * @return MaritalStatusCode
	 */
	@Override
	public MaritalStatusCode getMaritalStatusCodeById(final String id,String type) {
		logger.debug("Entering");
		MaritalStatusCode maritalStatusCode = getMaritalStatusCode();
		maritalStatusCode.setId(id);
		StringBuilder selectSql = new StringBuilder();
		
		selectSql.append("Select MaritalStsCode, MaritalStsDesc, MaritalStsIsActive,");
		/*if(type.contains("View")){
			selectSql.append("");
		}*/
		selectSql.append(" Version, LastMntBy, LastMntOn, RecordStatus, RoleCode, NextRoleCode, TaskId, NextTaskId, RecordType, WorkflowId");
		selectSql.append(" From BMTMaritalStatusCodes");
		selectSql.append(StringUtils.trimToEmpty(type));
		selectSql.append(" Where MaritalStsCode =:MaritalStsCode");

		logger.debug("selectSql: " + selectSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(maritalStatusCode);
		RowMapper<MaritalStatusCode> typeRowMapper = ParameterizedBeanPropertyRowMapper.newInstance(MaritalStatusCode.class);

		try {
			maritalStatusCode = this.namedParameterJdbcTemplate.queryForObject(selectSql.toString(), beanParameters, typeRowMapper);
		} catch (EmptyResultDataAccessException e) {
			logger.error(e);
			maritalStatusCode = null;
		}
		logger.debug("Leaving");
		return maritalStatusCode;
	}

	/**
	 * This method initialize the Record.
	 * 
	 * @param MaritalStatusCode
	 *            (maritalStatusCode)
	 * @return MaritalStatusCode
	 */
	@Override
	public void initialize(MaritalStatusCode maritalStatusCode) {
		super.initialize(maritalStatusCode);
	}

	/**
	 * This method refresh the Record.
	 * 
	 * @param MaritalStatusCode
	 *            (maritalStatusCode)
	 * @return void
	 */
	@Override
	public void refresh(MaritalStatusCode maritalStatusCode) {

	}

	/**
	 * @param dataSource
	 *            the dataSource to set
	 */
	public void setDataSource(DataSource dataSource) {
		this.namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
	}

	/**
	 * This method Deletes the Record from the BMTMaritalStatusCodes or
	 * BMTMaritalStatusCodes_Temp. if Record not deleted then throws
	 * DataAccessException with error 41003. delete MaritalStatus Codes by key
	 * MaritalStsCode
	 * 
	 * @param Marital
	 *            Status Codes (maritalStatusCode)
	 * @param type
	 *            (String) ""/_Temp/_View
	 * @return void
	 * @throws DataAccessException
	 * 
	 */
	@SuppressWarnings("serial")
	public void delete(MaritalStatusCode maritalStatusCode, String type) {
		logger.debug("Entering");
		int recordCount = 0;
		StringBuilder deleteSql = new StringBuilder();
		
		deleteSql.append("Delete From BMTMaritalStatusCodes");
		deleteSql.append(StringUtils.trimToEmpty(type));
		deleteSql.append(" Where MaritalStsCode =:MaritalStsCode");
		
		logger.debug("deleteSql: "+ deleteSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(maritalStatusCode);

		try {
			recordCount = this.namedParameterJdbcTemplate.update(deleteSql.toString(),beanParameters);

			if (recordCount <= 0) {
				ErrorDetails errorDetails = getError("41004",maritalStatusCode.getMaritalStsCode(), 
					maritalStatusCode.getUserDetails().getUsrLanguage());
				throw new DataAccessException(errorDetails.getError()) {
				};
			}
		} catch (DataAccessException e) {
			logger.error(e);
			ErrorDetails errorDetails = getError("41006",maritalStatusCode.getMaritalStsCode(), 
					maritalStatusCode.getUserDetails().getUsrLanguage());
			throw new DataAccessException(errorDetails.getError()) {
			};
		}
		logger.debug("Leaving");
	}

	/**
	 * This method insert new Records into BMTMaritalStatusCodes or
	 * BMTMaritalStatusCodes_Temp.
	 * 
	 * save MaritalStatus Codes
	 * 
	 * @param Marital
	 *            Status Codes (maritalStatusCode)
	 * @param type
	 *            (String) ""/_Temp/_View
	 * @return void
	 * @throws DataAccessException
	 * 
	 */
	@Override
	public String save(MaritalStatusCode maritalStatusCode, String type) {
		logger.debug("Entering");
		StringBuilder insertSql = new StringBuilder();
		
		insertSql.append("Insert Into BMTMaritalStatusCodes");
		insertSql.append(StringUtils.trimToEmpty(type));
		insertSql.append(" (MaritalStsCode, MaritalStsDesc, MaritalStsIsActive,");
		insertSql.append(" Version , LastMntBy, LastMntOn, RecordStatus, RoleCode, NextRoleCode, TaskId, NextTaskId,");
		insertSql.append(" RecordType, WorkflowId)");
		insertSql.append(" Values(:MaritalStsCode, :MaritalStsDesc, :MaritalStsIsActive,");
		insertSql.append(" :Version , :LastMntBy, :LastMntOn, :RecordStatus, :RoleCode, :NextRoleCode, :TaskId, :NextTaskId,");
		insertSql.append(" :RecordType, :WorkflowId)");
		
		logger.debug("insertSql: "+ insertSql.toString());		
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(maritalStatusCode);
		this.namedParameterJdbcTemplate.update(insertSql.toString(), beanParameters);

		logger.debug("Leaving");
		return maritalStatusCode.getId();
	}

	/**
	 * This method updates the Record BMTMaritalStatusCodes or
	 * BMTMaritalStatusCodes_Temp. if Record not updated then throws
	 * DataAccessException with error 41004. update MaritalStatus Codes by key
	 * MaritalStsCode and Version
	 * 
	 * @param Marital
	 *            Status Codes (maritalStatusCode)
	 * @param type
	 *            (String) ""/_Temp/_View
	 * @return void
	 * @throws DataAccessException
	 * 
	 */
	@SuppressWarnings("serial")
	@Override
	public void update(MaritalStatusCode maritalStatusCode, String type) {
		logger.debug("Entering");
		int recordCount = 0;
		StringBuilder updateSql = new StringBuilder();
		
		updateSql.append("Update BMTMaritalStatusCodes");
		updateSql.append(StringUtils.trimToEmpty(type));
		updateSql.append(" Set MaritalStsCode = :MaritalStsCode, MaritalStsDesc = :MaritalStsDesc, MaritalStsIsActive = :MaritalStsIsActive,");
		updateSql.append(" Version = :Version , LastMntBy = :LastMntBy, LastMntOn = :LastMntOn, RecordStatus= :RecordStatus,");
		updateSql.append(" RoleCode = :RoleCode, NextRoleCode = :NextRoleCode, TaskId = :TaskId, NextTaskId = :NextTaskId,");
		updateSql.append(" RecordType = :RecordType, WorkflowId = :WorkflowId ");
		updateSql.append(" Where MaritalStsCode =:MaritalStsCode");
		if (!type.endsWith("_TEMP")) {
			updateSql.append("  AND Version= :Version-1");
		}

		logger.debug("updateSql: "+ updateSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(maritalStatusCode);
		recordCount = this.namedParameterJdbcTemplate.update(updateSql.toString(),beanParameters);
		if (recordCount <= 0) {
			logger.debug("Error in Update Method Count :" + recordCount);

			ErrorDetails errorDetails = getError("41003",maritalStatusCode.getMaritalStsCode(), 
					maritalStatusCode.getUserDetails().getUsrLanguage());
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
	private ErrorDetails  getError(String errorId, String maritalStatusCode, String userLanguage){
		String[][] parms= new String[2][1]; 
		parms[1][0] = String.valueOf(maritalStatusCode);
		parms[0][0] = PennantJavaUtil.getLabel("label_MaritalStsCode")+ ":" + parms[1][0];
		return ErrorUtil.getErrorDetail(new ErrorDetails(PennantConstants.KEY_FIELD, errorId, parms[0],parms[1]), userLanguage);
	}
}