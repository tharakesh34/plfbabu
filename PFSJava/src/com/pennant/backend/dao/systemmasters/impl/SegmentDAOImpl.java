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
 * FileName    		:  SegmentDAOImpl.java                                                   * 	  
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
import com.pennant.backend.dao.systemmasters.SegmentDAO;
import com.pennant.backend.model.ErrorDetails;
import com.pennant.backend.model.WorkFlowDetails;
import com.pennant.backend.model.systemmasters.Segment;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantJavaUtil;
import com.pennant.backend.util.WorkFlowUtil;

/**
 * DAO methods implementation for the <b>Segment model</b> class.<br>
 * 
 */
public class SegmentDAOImpl extends BasisCodeDAO<Segment> implements SegmentDAO {

	private static Logger logger = Logger.getLogger(SegmentDAOImpl.class);

	// Spring Named JDBC Template
	private NamedParameterJdbcTemplate namedParameterJdbcTemplate;

	/**
	 * This method set the Work Flow id based on the module name and return the
	 * new Segment
	 * 
	 * @return Segment
	 */
	@Override
	public Segment getSegment() {
		logger.debug("Entering");
		WorkFlowDetails workFlowDetails = WorkFlowUtil.getWorkFlowDetails("Segment");
		Segment segment = new Segment();
		if (workFlowDetails != null) {
			segment.setWorkflowId(workFlowDetails.getWorkFlowId());
		}
		logger.debug("Leaving");
		return segment;
	}

	/**
	 * This method get the module from method getSegment() and set the new
	 * record flag as true and return Segment()
	 * 
	 * @return Segment
	 */
	@Override
	public Segment getNewSegment() {
		logger.debug("Entering");
		Segment segment = getSegment();
		segment.setNewRecord(true);
		logger.debug("Leaving");
		return segment;
	}

	/**
	 * Fetch the Record Segments details by key field
	 * 
	 * @param id
	 *            (String)
	 * @param type
	 *            (String) ""/_Temp/_View
	 * @return Segment
	 */
	@Override
	public Segment getSegmentById(final String id, String type) {
		logger.debug("Entering");
		Segment segment = new Segment();
		segment.setId(id);
		StringBuilder selectSql = new StringBuilder();
		
		selectSql.append("Select SegmentCode, SegmentDesc, SegmentIsActive,");
		/*if(type.contains("View")){
			selectSql.append("");
		}*/
		selectSql.append(" Version , LastMntBy, LastMntOn, RecordStatus, RoleCode, NextRoleCode, TaskId, NextTaskId, RecordType, WorkflowId");
		selectSql.append(" From BMTSegments");
		selectSql.append(StringUtils.trimToEmpty(type));
		selectSql.append(" Where SegmentCode =:SegmentCode");

		logger.debug("selectSql: " + selectSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(segment);
		RowMapper<Segment> typeRowMapper = ParameterizedBeanPropertyRowMapper.newInstance(Segment.class);
		try {
			segment = this.namedParameterJdbcTemplate.queryForObject(selectSql.toString(), beanParameters, typeRowMapper);
		} catch (EmptyResultDataAccessException e) {
			logger.error(e);
			segment = null;
		}
		logger.debug("Leaving");
		return segment;
	}

	/**
	 * This method initialize the Record.
	 * 
	 * @param Segment
	 *            (segment)
	 * @return Segment
	 */
	@Override
	public void initialize(Segment segment) {
		super.initialize(segment);
	}

	/**
	 * This method refresh the Record.
	 * 
	 * @param Segment
	 *            (segment)
	 * @return void
	 */
	@Override
	public void refresh(Segment segment) {

	}

	/**
	 * @param dataSource
	 *            the dataSource to set
	 */
	public void setDataSource(DataSource dataSource) {
		this.namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
	}

	/**
	 * This method Deletes the Record from the BMTSegments or BMTSegments_Temp.
	 * if Record not deleted then throws DataAccessException with error 41003.
	 * delete Segments by key SegmentCode
	 * 
	 * @param Segments
	 *            (segment)
	 * @param type
	 *            (String) ""/_Temp/_View
	 * @return void
	 * @throws DataAccessException
	 * 
	 */
	@SuppressWarnings("serial")
	public void delete(Segment segment, String type) {
		logger.debug("Entering");
		int recordCount = 0;
		StringBuilder deleteSql = new StringBuilder();
		
		deleteSql.append("Delete From BMTSegments");
		deleteSql.append(StringUtils.trimToEmpty(type));
		deleteSql.append(" Where SegmentCode =:SegmentCode");
		
		logger.debug("deleteSql: "+ deleteSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(segment);

		try {
			recordCount = this.namedParameterJdbcTemplate.update(deleteSql.toString(),beanParameters);

			if (recordCount <= 0) {
				ErrorDetails errorDetails = getError("41004",segment.getSegmentCode(), segment.getUserDetails().getUsrLanguage());
				throw new DataAccessException(errorDetails.getError()) {
				};
			}
		} catch (DataAccessException e) {
			logger.debug("Error in delete Method");
			logger.error(e);
			ErrorDetails errorDetails = getError("41006",segment.getSegmentCode(), segment.getUserDetails().getUsrLanguage());
			throw new DataAccessException(errorDetails.getError()) {
			};
		}
		logger.debug("Leaving");
	}

	/**
	 * This method insert new Records into BMTSegments or BMTSegments_Temp.
	 * 
	 * save Segments
	 * 
	 * @param Segments
	 *            (segment)
	 * @param type
	 *            (String) ""/_Temp/_View
	 * @return void
	 * @throws DataAccessException
	 * 
	 */
	@Override
	public String save(Segment segment, String type) {
		logger.debug("Entering");
		StringBuilder insertSql = new StringBuilder();
		
		insertSql.append("Insert Into BMTSegments");
		insertSql.append(StringUtils.trimToEmpty(type));
		insertSql.append(" (SegmentCode, SegmentDesc, SegmentIsActive,");
		insertSql.append(" Version , LastMntBy, LastMntOn, RecordStatus, RoleCode, NextRoleCode, TaskId, NextTaskId,");
		insertSql.append(" RecordType, WorkflowId)");
		insertSql.append(" Values(:SegmentCode, :SegmentDesc, :SegmentIsActive,");
		insertSql.append(" :Version , :LastMntBy, :LastMntOn, :RecordStatus, :RoleCode, :NextRoleCode, :TaskId, :NextTaskId,");
		insertSql.append(" :RecordType, :WorkflowId)");
		
		logger.debug("insertSql: "+ insertSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(segment);
		this.namedParameterJdbcTemplate.update(insertSql.toString(), beanParameters);

		logger.debug("Leaving");
		return segment.getId();
	}

	/**
	 * This method updates the Record BMTSegments or BMTSegments_Temp. if Record
	 * not updated then throws DataAccessException with error 41004. update
	 * Segments by key SegmentCode and Version
	 * 
	 * @param Segments
	 *            (segment)
	 * @param type
	 *            (String) ""/_Temp/_View
	 * @return void
	 * @throws DataAccessException
	 * 
	 */
	@SuppressWarnings("serial")
	@Override
	public void update(Segment segment, String type) {
		logger.debug("Entering");
		int recordCount = 0;
		StringBuilder updateSql = new StringBuilder();

		updateSql.append("Update BMTSegments");
		updateSql.append(StringUtils.trimToEmpty(type));
		updateSql.append(" Set SegmentCode = :SegmentCode, SegmentDesc = :SegmentDesc, SegmentIsActive = :SegmentIsActive,");
		updateSql.append(" Version = :Version, LastMntBy = :LastMntBy, LastMntOn = :LastMntOn, RecordStatus= :RecordStatus,");
		updateSql.append(" RoleCode = :RoleCode, NextRoleCode = :NextRoleCode, TaskId = :TaskId, NextTaskId = :NextTaskId,");
		updateSql.append(" RecordType = :RecordType, WorkflowId = :WorkflowId ");
		updateSql.append(" Where SegmentCode =:SegmentCode");
		if (!type.endsWith("_TEMP")) {
			updateSql.append("  AND Version= :Version-1");
		}

		logger.debug("updateSql: "+ updateSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(segment);
		recordCount = this.namedParameterJdbcTemplate.update(updateSql.toString(),beanParameters);

		if (recordCount <= 0) {
			logger.debug("Error in Update Method Count :" + recordCount);

			ErrorDetails errorDetails = getError("41003",segment.getSegmentCode(), segment.getUserDetails().getUsrLanguage());
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
	private ErrorDetails  getError(String errorId, String segmentCode, String userLanguage){
		String[][] parms= new String[2][1]; 
		parms[1][0] = String.valueOf(segmentCode);
		parms[0][0] = PennantJavaUtil.getLabel("label_Segment_Code")+ ":" + parms[1][0];
		return ErrorUtil.getErrorDetail(new ErrorDetails(PennantConstants.KEY_FIELD, errorId, parms[0],parms[1]), userLanguage);
	}

}