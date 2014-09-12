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
 * FileName    		:  RatingCodeDAOImpl.java                                                   * 	  
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

package com.pennant.backend.dao.bmtmasters.impl;

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
import com.pennant.backend.dao.bmtmasters.RatingCodeDAO;
import com.pennant.backend.dao.impl.BasisCodeDAO;
import com.pennant.backend.model.ErrorDetails;
import com.pennant.backend.model.WorkFlowDetails;
import com.pennant.backend.model.bmtmasters.RatingCode;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantJavaUtil;
import com.pennant.backend.util.WorkFlowUtil;

/**
 * DAO methods implementation for the <b>RatingCode model</b> class.<br>
 * 
 */
public class RatingCodeDAOImpl extends BasisCodeDAO<RatingCode> implements RatingCodeDAO {

	private static Logger logger = Logger.getLogger(RatingCodeDAOImpl.class);

	// Spring Named JDBC Template
	private NamedParameterJdbcTemplate namedParameterJdbcTemplate;

	/**
	 * This method set the Work Flow id based on the module name and return the
	 * new RatingCode
	 * 
	 * @return RatingCode
	 */
	@Override
	public RatingCode getRatingCode() {
		logger.debug("Entering");
		WorkFlowDetails workFlowDetails = WorkFlowUtil.getWorkFlowDetails("RatingCode");
		RatingCode ratingCode = new RatingCode();
		if (workFlowDetails != null) {
			ratingCode.setWorkflowId(workFlowDetails.getWorkFlowId());
		}
		logger.debug("Leaving");
		return ratingCode;
	}

	/**
	 * This method get the module from method getRatingCode() and set the new
	 * record flag as true and return RatingCode()
	 * 
	 * @return RatingCode
	 */
	@Override
	public RatingCode getNewRatingCode() {
		logger.debug("Entering");
		RatingCode ratingCode = getRatingCode();
		ratingCode.setNewRecord(true);
		logger.debug("Leaving");
		return ratingCode;
	}

	/**
	 * Fetch the Record Rating Code details by key field
	 * 
	 * @param id
	 *            (String)
	 * @param type
	 *            (String) ""/_Temp/_View
	 * @return RatingCode
	 */
	@Override
	public RatingCode getRatingCodeById(final String ratingType, String ratingCode, String type) {
		logger.debug("Entering");
		RatingCode aRatingCode = new RatingCode();
		aRatingCode.setRatingType(ratingType);
		aRatingCode.setRatingCode(ratingCode);
		StringBuilder selectSql = new StringBuilder();
		
		selectSql.append("SELECT RatingType, RatingCode, RatingCodeDesc, RatingIsActive,");
		if(type.contains("View")){
			selectSql.append("LovDescRatingTypeName,");
		}
		selectSql.append(" Version, LastMntBy, LastMntOn, RecordStatus, RoleCode, NextRoleCode,");
		selectSql.append(" TaskId, NextTaskId, RecordType, WorkflowId");
		selectSql.append(" FROM BMTRatingCodes");
		selectSql.append(StringUtils.trimToEmpty(type));
		selectSql.append(" Where RatingType =:RatingType AND RatingCode=:RatingCode");

		logger.debug("selectSql: " + selectSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(aRatingCode);
		RowMapper<RatingCode> typeRowMapper = ParameterizedBeanPropertyRowMapper.newInstance(RatingCode.class);

		try {
			aRatingCode = this.namedParameterJdbcTemplate.queryForObject(selectSql.toString(), beanParameters, typeRowMapper);
		} catch (EmptyResultDataAccessException e) {
			aRatingCode = null;
		}
		logger.debug("Leaving");
		return aRatingCode;
	}

	/**
	 * This method initialize the Record.
	 * 
	 * @param RatingCode
	 *            (ratingCode)
	 * @return RatingCode
	 */
	@Override
	public void initialize(RatingCode ratingCode) {
		super.initialize(ratingCode);
	}

	/**
	 * This method refresh the Record.
	 * 
	 * @param RatingCode
	 *            (ratingCode)
	 * @return void
	 */
	@Override
	public void refresh(RatingCode ratingCode) {

	}

	/**
	 * @param dataSource
	 *            the dataSource to set
	 */
	public void setDataSource(DataSource dataSource) {
		this.namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
	}

	/**
	 * This method Deletes the Record from the BMTRatingCodes or
	 * BMTRatingCodes_Temp. if Record not deleted then throws
	 * DataAccessException with error 41003. delete Rating Code by key
	 * RatingType
	 * 
	 * @param Rating
	 *            Code (ratingCode)
	 * @param type
	 *            (String) ""/_Temp/_View
	 * @return void
	 * @throws DataAccessException
	 * 
	 */
	@SuppressWarnings("serial")
	public void delete(RatingCode ratingCode, String type) {
		logger.debug("Entering");
		int recordCount = 0;
		StringBuilder deleteSql = new StringBuilder();
		
		deleteSql.append("Delete From BMTRatingCodes");
		deleteSql.append(StringUtils.trimToEmpty(type));
		deleteSql.append(" Where RatingType =:RatingType AND RatingCode=:RatingCode");
		
		logger.debug("deleteSql: "+ deleteSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(ratingCode);

		try {
			recordCount = this.namedParameterJdbcTemplate.update(deleteSql.toString(),beanParameters);

			if (recordCount <= 0) {
				ErrorDetails errorDetails = getError("41004",ratingCode.getRatingType(),ratingCode.getRatingCode(),
					ratingCode.getUserDetails().getUsrLanguage());
				throw new DataAccessException(errorDetails.getError()) {
				};
			}
		} catch (DataAccessException e) {
			logger.debug("Error in delete Method");
			logger.error(e);
			ErrorDetails errorDetails = getError("41006",ratingCode.getRatingType(),ratingCode.getRatingCode(),
					ratingCode.getUserDetails().getUsrLanguage());
			throw new DataAccessException(errorDetails.getError()) {
			};
		}
		logger.debug("Leaving");
	}

	/**
	 * This method insert new Records into BMTRatingCodes or
	 * BMTRatingCodes_Temp.
	 * 
	 * save Rating Code
	 * 
	 * @param Rating
	 *            Code (ratingCode)
	 * @param type
	 *            (String) ""/_Temp/_View
	 * @return void
	 * @throws DataAccessException
	 * 
	 */
	@Override
	public String save(RatingCode ratingCode, String type) {
		logger.debug("Entering");
		StringBuilder insertSql = new StringBuilder();
		
		insertSql.append("Insert Into BMTRatingCodes");
		insertSql.append(StringUtils.trimToEmpty(type));
		insertSql.append(" (RatingType, RatingCode, RatingCodeDesc, RatingIsActive,");
		insertSql.append(" Version , LastMntBy, LastMntOn, RecordStatus, RoleCode, NextRoleCode, TaskId, NextTaskId,");
		insertSql.append(" RecordType, WorkflowId)");
		insertSql.append(" Values(:RatingType, :RatingCode, :RatingCodeDesc, :RatingIsActive,");
		insertSql.append(" :Version , :LastMntBy, :LastMntOn, :RecordStatus, :RoleCode, :NextRoleCode, :TaskId, :NextTaskId,");
		insertSql.append(" :RecordType, :WorkflowId)");
		
		logger.debug("insertSql: "+ insertSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(ratingCode);
		this.namedParameterJdbcTemplate.update(insertSql.toString(), beanParameters);

		logger.debug("Leaving");
		return ratingCode.getId();
	}

	/**
	 * This method updates the Record BMTRatingCodes or BMTRatingCodes_Temp. if
	 * Record not updated then throws DataAccessException with error 41004.
	 * update Rating Code by key RatingType and Version
	 * 
	 * @param Rating
	 *            Code (ratingCode)
	 * @param type
	 *            (String) ""/_Temp/_View
	 * @return void
	 * @throws DataAccessException
	 * 
	 */
	@SuppressWarnings("serial")
	@Override
	public void update(RatingCode ratingCode, String type) {
		int recordCount = 0;
		logger.debug("Entering");
		StringBuilder updateSql = new StringBuilder();
		
		updateSql.append("Update BMTRatingCodes");
		updateSql.append(StringUtils.trimToEmpty(type));
		updateSql.append(" Set RatingType = :RatingType, RatingCode = :RatingCode, RatingCodeDesc = :RatingCodeDesc,");
		updateSql.append(" RatingIsActive = :RatingIsActive,");
		updateSql.append(" Version = :Version , LastMntBy = :LastMntBy, LastMntOn = :LastMntOn, RecordStatus= :RecordStatus,");
		updateSql.append(" RoleCode = :RoleCode, NextRoleCode = :NextRoleCode, TaskId = :TaskId, NextTaskId = :NextTaskId,");
		updateSql.append(" RecordType = :RecordType, WorkflowId = :WorkflowId ");
		updateSql.append(" Where RatingType =:RatingType AND RatingCode=:RatingCode");
		if (!type.endsWith("_TEMP")) {
			updateSql.append("  AND Version= :Version-1");
		}

		logger.debug("updateSql: "+ updateSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(ratingCode);
		recordCount = this.namedParameterJdbcTemplate.update(updateSql.toString(),beanParameters);

		if (recordCount <= 0) {
			logger.debug("Error in Update Method Count :" + recordCount);

			ErrorDetails errorDetails = getError("41003",ratingCode.getRatingType(),ratingCode.getRatingCode(),
					ratingCode.getUserDetails().getUsrLanguage());
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
	private ErrorDetails  getError(String errorId, String ratingType, String ratingCode, String userLanguage){
		String[][] parms= new String[2][2]; 
		parms[1][0] = String.valueOf(ratingType);
		parms[1][1] = ratingCode;

		parms[0][0] = PennantJavaUtil.getLabel("label_RatingType")+ ":" + parms[1][0];
		parms[0][1] = PennantJavaUtil.getLabel("label_RatingCode")+ ":" + parms[1][1];
		return ErrorUtil.getErrorDetail(new ErrorDetails(PennantConstants.KEY_FIELD, errorId, parms[0],parms[1]), userLanguage);
	}

}