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
 * FileName    		:  RejectDetailDAOImpl.java                                                   * 	  
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

package com.pennant.backend.dao.applicationmaster.impl;

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
import com.pennant.backend.dao.applicationmaster.RejectDetailDAO;
import com.pennant.backend.dao.impl.BasisCodeDAO;
import com.pennant.backend.model.ErrorDetails;
import com.pennant.backend.model.WorkFlowDetails;
import com.pennant.backend.model.applicationmaster.RejectDetail;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantJavaUtil;
import com.pennant.backend.util.WorkFlowUtil;

/**
 * DAO methods implementation for the <b>RejectDetail model</b> class.<br>
 * 
 */
public class RejectDetailDAOImpl extends BasisCodeDAO<RejectDetail> implements RejectDetailDAO {

	private static Logger logger = Logger.getLogger(RejectDetailDAOImpl.class);

	// Spring Named JDBC Template
	private NamedParameterJdbcTemplate namedParameterJdbcTemplate;

	/**
	 * This method set the Work Flow id based on the module name and return the
	 * new RejectDetail
	 * 
	 * @return RejectDetail
	 */
	@Override
	public RejectDetail getRejectDetail() {
		logger.debug("Entering");
		WorkFlowDetails workFlowDetails = WorkFlowUtil.getWorkFlowDetails("RejectDetail");
		RejectDetail rejectDetail = new RejectDetail();
		if (workFlowDetails != null) {
			rejectDetail.setWorkflowId(workFlowDetails.getWorkFlowId());
		}
		logger.debug("Leaving");
		return rejectDetail;
	}

	/**
	 * This method get the module from method getRejectDetail() and set the new
	 * record flag as true and return RejectDetail()
	 * 
	 * @return RejectDetail
	 */
	@Override
	public RejectDetail getNewRejectDetail() {
		logger.debug("Entering");
		RejectDetail rejectDetail = getRejectDetail();
		rejectDetail.setNewRecord(true);
		logger.debug("Leaving");
		return rejectDetail;
	}

	/**
	 * Fetch the Record Reject Codes details by key field
	 * 
	 * @param id
	 *            (String)
	 * @param type
	 *            (String) ""/_Temp/_View
	 * @return RejectDetail
	 */
	@Override
	public RejectDetail getRejectDetailById(final String id, String type) {
		logger.debug("Entering");
		RejectDetail rejectDetail = getRejectDetail();
		rejectDetail.setId(id);
		StringBuilder selectSql = new StringBuilder();

		selectSql.append("Select RejectCode, RejectDesc, RejectIsActive,");
		/*if(type.contains("View")){
			selectSql.append("");
		}*/
		selectSql.append(" Version , LastMntBy, LastMntOn, RecordStatus, RoleCode, NextRoleCode, TaskId, NextTaskId, RecordType, WorkflowId");
		selectSql.append(" From BMTRejectCodes" + StringUtils.trimToEmpty(type));
		selectSql.append(" Where RejectCode =:RejectCode");

		logger.debug("selectSql: " + selectSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(rejectDetail);
		RowMapper<RejectDetail> typeRowMapper = ParameterizedBeanPropertyRowMapper.newInstance(RejectDetail.class);

		try {
			rejectDetail = this.namedParameterJdbcTemplate.queryForObject(selectSql.toString(), beanParameters, typeRowMapper);
		} catch (EmptyResultDataAccessException e) {
			logger.error(e);
			rejectDetail = null;
		}
		logger.debug("Leaving");
		return rejectDetail;
	}

	/**
	 * This method initialize the Record.
	 * 
	 * @param RejectDetail
	 *            (rejectDetail)
	 * @return RejectDetail
	 */
	@Override
	public void initialize(RejectDetail rejectDetail) {
		super.initialize(rejectDetail);
	}

	/**
	 * This method refresh the Record.
	 * 
	 * @param RejectDetail
	 *            (rejectDetail)
	 * @return void
	 */
	@Override
	public void refresh(RejectDetail rejectDetail) {

	}

	/**
	 * @param dataSource
	 *            the dataSource to set
	 */
	public void setDataSource(DataSource dataSource) {
		this.namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
	}

	/**
	 * This method Deletes the Record from the BMTRejectCodes or
	 * BMTRejectCodes_Temp. if Record not deleted then throws
	 * DataAccessException with error 41003. delete Reject Codes by key
	 * RejectCode
	 * 
	 * @param Reject
	 *            Codes (rejectDetail)
	 * @param type
	 *            (String) ""/_Temp/_View
	 * @return void
	 * @throws DataAccessException
	 * 
	 */
	@SuppressWarnings("serial")
	public void delete(RejectDetail rejectDetail, String type) {
		logger.debug("Entering");
		int recordCount = 0;
		StringBuilder deleteSql = new StringBuilder();
		
		deleteSql.append("Delete From BMTRejectCodes");
		deleteSql.append(StringUtils.trimToEmpty(type));
		deleteSql.append(" Where RejectCode =:RejectCode");
		
		logger.debug("deleteSql: "+ deleteSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(rejectDetail);

		try {
			recordCount = this.namedParameterJdbcTemplate.update(deleteSql.toString(),beanParameters);

			if (recordCount <= 0) {
				ErrorDetails errorDetails = getError("41004",rejectDetail.getRejectCode(), 
					rejectDetail.getUserDetails().getUsrLanguage());
				throw new DataAccessException(errorDetails.getError()) {
				};
			}
		} catch (DataAccessException e) {
			logger.debug("Error in delete Method");
			logger.error(e);
			ErrorDetails errorDetails = getError("41006",rejectDetail.getRejectCode(), 
					rejectDetail.getUserDetails().getUsrLanguage());
			throw new DataAccessException(errorDetails.getError()) {
			};
		}
		logger.debug("Leaving");
	}

	/**
	 * This method insert new Records into BMTRejectCodes or
	 * BMTRejectCodes_Temp.
	 * 
	 * save Reject Codes
	 * 
	 * @param Reject
	 *            Codes (rejectDetail)
	 * @param type
	 *            (String) ""/_Temp/_View
	 * @return void
	 * @throws DataAccessException
	 * 
	 */
	@Override
	public String save(RejectDetail rejectDetail, String type) {
		logger.debug("Entering");
		StringBuilder insertSql = new StringBuilder();

		insertSql.append("Insert Into BMTRejectCodes");
		insertSql.append(StringUtils.trimToEmpty(type));
		insertSql.append(" (RejectCode, RejectDesc, RejectIsActive,");
		insertSql.append(" Version , LastMntBy, LastMntOn, RecordStatus, RoleCode, NextRoleCode, TaskId, NextTaskId," );
		insertSql.append(" RecordType, WorkflowId)");
		insertSql.append(" Values (:RejectCode, :RejectDesc, :RejectIsActive,");
		insertSql.append(" :Version , :LastMntBy, :LastMntOn, :RecordStatus, :RoleCode, :NextRoleCode, :TaskId, :NextTaskId, ");
		insertSql.append(" :RecordType, :WorkflowId)");
		
		logger.debug("insertSql: "+ insertSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(rejectDetail);
		this.namedParameterJdbcTemplate.update(insertSql.toString(), beanParameters);

		logger.debug("Leaving");
		return rejectDetail.getId();
	}

	/**
	 * This method updates the Record BMTRejectCodes or BMTRejectCodes_Temp. if
	 * Record not updated then throws DataAccessException with error 41004.
	 * update Reject Codes by key RejectCode and Version
	 * 
	 * @param Reject
	 *            Codes (rejectDetail)
	 * @param type
	 *            (String) ""/_Temp/_View
	 * @return void
	 * @throws DataAccessException
	 * 
	 */
	@SuppressWarnings("serial")
	@Override
	public void update(RejectDetail rejectDetail, String type) {
		logger.debug("Entering");
		int recordCount = 0;
		StringBuilder updateSql = new StringBuilder();

		updateSql.append("Update BMTRejectCodes");
		updateSql.append(StringUtils.trimToEmpty(type));
		updateSql.append(" Set RejectCode = :RejectCode, RejectDesc = :RejectDesc, RejectIsActive = :RejectIsActive,");
		updateSql.append(" Version = :Version , LastMntBy = :LastMntBy, LastMntOn = :LastMntOn, RecordStatus= :RecordStatus,");
		updateSql.append(" RoleCode = :RoleCode, NextRoleCode = :NextRoleCode, TaskId = :TaskId,");
		updateSql.append(" NextTaskId = :NextTaskId, RecordType = :RecordType, WorkflowId = :WorkflowId");
		updateSql.append(" Where RejectCode =:RejectCode");
		if (!type.endsWith("_TEMP")) {
			updateSql.append("  AND Version= :Version-1");
		}

		logger.debug("updateSql: "+ updateSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(rejectDetail);
		recordCount = this.namedParameterJdbcTemplate.update(updateSql.toString(),beanParameters);

		if (recordCount <= 0) {
			logger.debug("Error in Update Method Count :" + recordCount);

			ErrorDetails errorDetails = getError("41003",rejectDetail.getRejectCode(), rejectDetail.getUserDetails().getUsrLanguage());
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
	private ErrorDetails  getError(String errorId, String rejectCode, String userLanguage){
		String[][] parms= new String[2][1]; 
		parms[1][0] = String.valueOf(rejectCode);
		parms[0][0] = PennantJavaUtil.getLabel("label_RejectCode")+ ":" + parms[1][0];
		return ErrorUtil.getErrorDetail(new ErrorDetails(PennantConstants.KEY_FIELD, errorId, parms[0],parms[1]), userLanguage);
	}

}