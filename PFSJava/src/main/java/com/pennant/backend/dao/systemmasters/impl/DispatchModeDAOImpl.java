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
 * FileName    		:  DispatchModeDAOImpl.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  18-08-2011    														*
 *                                                                  						*
 * Modified Date    :  18-08-2011    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 18-08-2011       Pennant	                 0.1                                            * 
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
import com.pennant.backend.dao.systemmasters.DispatchModeDAO;
import com.pennant.backend.model.ErrorDetails;
import com.pennant.backend.model.systemmasters.DispatchMode;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantJavaUtil;

/**
 * DAO methods implementation for the <b>DispatchMode model</b> class.<br>
 * 
 */
public class DispatchModeDAOImpl extends BasisCodeDAO<DispatchMode> implements DispatchModeDAO {

	private static Logger logger = Logger.getLogger(DispatchModeDAOImpl.class);

	// Spring Named JDBC Template
	private NamedParameterJdbcTemplate namedParameterJdbcTemplate;

	public DispatchModeDAOImpl() {
		super();
	}
	
	/**
	 * Fetch the Record Dispatch Mode Details details by key field
	 * 
	 * @param id
	 *            (String)
	 * @param type
	 *            (String) ""/_Temp/_View
	 * @return DispatchMode
	 */
	@Override
	public DispatchMode getDispatchModeById(final String id, String type) {
		logger.debug("Entering");
		DispatchMode dispatchMode = new DispatchMode();
		dispatchMode.setId(id);
		StringBuilder selectSql = new StringBuilder();

		selectSql.append("SELECT DispatchModeCode, DispatchModeDesc, DispatchModeIsActive," );
		selectSql.append(" Version, LastMntOn, LastMntBy,RecordStatus, RoleCode, NextRoleCode, TaskId, NextTaskId, RecordType, WorkflowId" );
		selectSql.append(" FROM  BMTDispatchModes");
		selectSql.append(StringUtils.trimToEmpty(type));
		selectSql.append(" Where DispatchModeCode =:DispatchModeCode") ;
				
		logger.debug("selectSql: " + selectSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(dispatchMode);
		RowMapper<DispatchMode> typeRowMapper = ParameterizedBeanPropertyRowMapper.newInstance(DispatchMode.class);

		try {
			dispatchMode = this.namedParameterJdbcTemplate.queryForObject(selectSql.toString(), beanParameters, typeRowMapper);
		} catch (EmptyResultDataAccessException e) {
			logger.warn("Exception: ", e);
			dispatchMode = null;
		}
		logger.debug("Leaving");
		return dispatchMode;
	}

	/**
	 * To Set dataSource
	 * 
	 * @param dataSource
	 */
	public void setDataSource(DataSource dataSource) {
		this.namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
	}

	/**
	 * This method Deletes the Record from the BMTDispatchModes or
	 * BMTDispatchModes_Temp. if Record not deleted then throws
	 * DataAccessException with error 41003. delete Dispatch Mode Details by key
	 * DispatchModeCode
	 * 
	 * @param Dispatch
	 *            Mode Details (dispatchMode)
	 * @param type
	 *            (String) ""/_Temp/_View
	 * @return void
	 * @throws DataAccessException
	 * 
	 */
	@SuppressWarnings("serial")
	public void delete(DispatchMode dispatchMode, String type) {
		logger.debug("Entering");
		int recordCount = 0;
		StringBuilder deleteSql = new StringBuilder();

		deleteSql.append("Delete From BMTDispatchModes");
		deleteSql.append(StringUtils.trimToEmpty(type));
		deleteSql.append(" Where DispatchModeCode =:DispatchModeCode");
		
		logger.debug("deleteSql: "+ deleteSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(dispatchMode);

		try {
			recordCount = this.namedParameterJdbcTemplate.update(deleteSql.toString(), beanParameters);

			if (recordCount <= 0) {
				ErrorDetails errorDetails= getError("41004", dispatchMode.getDispatchModeCode(), dispatchMode.getUserDetails().getUsrLanguage());
				throw new DataAccessException(errorDetails.getError()) {
				};
			}
		} catch (DataAccessException e) {
			logger.debug("Error in delete Method");
			logger.error("Exception: ", e);
			ErrorDetails errorDetails= getError("41006", dispatchMode.getDispatchModeCode(), dispatchMode.getUserDetails().getUsrLanguage());
			throw new DataAccessException(errorDetails.getError()) {
			};
		}
		logger.debug("Leaving");
	}

	/**
	 * This method insert new Records into BMTDispatchModes or
	 * BMTDispatchModes_Temp.
	 * 
	 * save Dispatch Mode Details
	 * 
	 * @param Dispatch
	 *            Mode Details (dispatchMode)
	 * @param type
	 *            (String) ""/_Temp/_View
	 * @return void
	 * @throws DataAccessException
	 * 
	 */
	@Override
	public String save(DispatchMode dispatchMode, String type) {
		logger.debug("Entering");
		StringBuilder insertSql = new StringBuilder();

		insertSql.append("Insert Into BMTDispatchModes");
		insertSql.append(StringUtils.trimToEmpty(type));
		insertSql.append(" (DispatchModeCode, DispatchModeDesc, DispatchModeIsActive," );
		insertSql.append(" Version , LastMntBy, LastMntOn, RecordStatus, RoleCode, NextRoleCode, TaskId, NextTaskId," );
		insertSql.append(" RecordType, WorkflowId)");
		insertSql.append(" Values(:DispatchModeCode, :DispatchModeDesc, :DispatchModeIsActive, " );
		insertSql.append(" :Version , :LastMntBy, :LastMntOn, :RecordStatus, :RoleCode, :NextRoleCode, :TaskId, :NextTaskId, ");
		insertSql.append(" :RecordType, :WorkflowId)");
		
		logger.debug("insertSql: "+ insertSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(dispatchMode);
		this.namedParameterJdbcTemplate.update(insertSql.toString(), beanParameters);

		logger.debug("Leaving");
		return dispatchMode.getId();
	}

	/**
	 * This method updates the Record BMTDispatchModes or BMTDispatchModes_Temp.
	 * if Record not updated then throws DataAccessException with error 41004.
	 * update Dispatch Mode Details by key DispatchModeCode and Version
	 * 
	 * @param Dispatch
	 *            Mode Details (dispatchMode)
	 * @param type
	 *            (String) ""/_Temp/_View
	 * @return void
	 * @throws DataAccessException
	 * 
	 */
	@SuppressWarnings("serial")
	@Override
	public void update(DispatchMode dispatchMode, String type) {
		logger.debug("Entering");
		int recordCount = 0;
		StringBuilder updateSql = new StringBuilder();

		updateSql.append("Update BMTDispatchModes");
		updateSql.append(StringUtils.trimToEmpty(type));
		updateSql.append(" Set DispatchModeCode = :DispatchModeCode, DispatchModeDesc = :DispatchModeDesc," );
		updateSql.append(" DispatchModeIsActive = :DispatchModeIsActive,");
		updateSql.append(" Version = :Version , LastMntBy = :LastMntBy, LastMntOn = :LastMntOn, " );
		updateSql.append(" RecordStatus= :RecordStatus, RoleCode = :RoleCode,NextRoleCode = :NextRoleCode, TaskId = :TaskId," );
		updateSql.append(" NextTaskId = :NextTaskId, RecordType = :RecordType, WorkflowId = :WorkflowId" );
		updateSql.append(" Where DispatchModeCode =:DispatchModeCode ");
		if (!type.endsWith("_Temp")){
			updateSql.append(" AND Version= :Version-1");
		}

		logger.debug("updateSql: "+ updateSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(dispatchMode);
		recordCount = this.namedParameterJdbcTemplate.update(updateSql.toString(),	beanParameters);

		if (recordCount <= 0) {
			logger.debug("Error in Update Method Count :" + recordCount);

			ErrorDetails errorDetails= getError("41003", dispatchMode.getDispatchModeCode(), dispatchMode.getUserDetails().getUsrLanguage());
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
	private ErrorDetails  getError(String errorId, String dispatchModeCode,String userLanguage){
		String[][] parms= new String[2][2]; 
		parms[1][0] = dispatchModeCode;
		parms[0][0] = PennantJavaUtil.getLabel("label_DispatchModeCode")+ ":" + parms[1][0];
		return ErrorUtil.getErrorDetail(new ErrorDetails(PennantConstants.KEY_FIELD, errorId, parms[0],parms[1]), userLanguage);
	}
}