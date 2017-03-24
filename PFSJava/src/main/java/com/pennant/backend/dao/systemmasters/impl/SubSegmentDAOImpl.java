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
 * FileName    		:  SubSegmentDAOImpl.java                                                   * 	  
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
import com.pennant.backend.dao.systemmasters.SubSegmentDAO;
import com.pennant.backend.model.ErrorDetails;
import com.pennant.backend.model.systemmasters.SubSegment;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantJavaUtil;

/**
 * DAO methods implementation for the <b>SubSegment model</b> class.<br>
 * 
 */
public class SubSegmentDAOImpl extends BasisCodeDAO<SubSegment> implements SubSegmentDAO {

	private static Logger logger = Logger.getLogger(SubSegmentDAOImpl.class);

	// Spring Named JDBC Template
	private NamedParameterJdbcTemplate namedParameterJdbcTemplate;

	public SubSegmentDAOImpl() {
		super();
	}
	

	/**
	 * Fetch the Record Sub Segments details by key field
	 * 
	 * @param id
	 *            (String)
	 * @param type
	 *            (String) ""/_Temp/_View
	 * @return SubSegment
	 */
	@Override
	public SubSegment getSubSegmentById(final String id, String subSegmentCode,String type) {
		logger.debug("Entering");
		SubSegment subSegment = new SubSegment();
		subSegment.setId(id);
		subSegment.setSubSegmentCode(subSegmentCode);
		StringBuilder selectSql = new StringBuilder();
		
		selectSql.append("Select SegmentCode, SubSegmentCode, SubSegmentDesc, SubSegmentIsActive,");
		if(type.contains("View")){
			selectSql.append("lovDescSegmentCodeName,");
		}
		selectSql.append(" Version , LastMntBy, LastMntOn, RecordStatus, RoleCode, NextRoleCode,");
		selectSql.append(" TaskId, NextTaskId, RecordType, WorkflowId"); 
		selectSql.append(" From BMTSubSegments");
		selectSql.append(StringUtils.trimToEmpty(type));
		selectSql.append(" Where SubSegmentCode=:SubSegmentCode");

		logger.debug("selectSql: " + selectSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(subSegment);
		RowMapper<SubSegment> typeRowMapper = ParameterizedBeanPropertyRowMapper.newInstance(SubSegment.class);

		try {
			subSegment = this.namedParameterJdbcTemplate.queryForObject(selectSql.toString(), beanParameters, typeRowMapper);
		} catch (EmptyResultDataAccessException e) {
			logger.error("Exception: ", e);
			subSegment = null;
		}
		logger.debug("Leaving");
		return subSegment;
	}

	/**
	 * @param dataSource
	 *            the dataSource to set
	 */
	public void setDataSource(DataSource dataSource) {
		this.namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
	}

	/**
	 * This method Deletes the Record from the BMTSubSegments or
	 * BMTSubSegments_Temp. if Record not deleted then throws
	 * DataAccessException with error 41003. delete Sub Segments by key
	 * SegmentCode
	 * 
	 * @param Sub
	 *            Segments (subSegment)
	 * @param type
	 *            (String) ""/_Temp/_View
	 * @return void
	 * @throws DataAccessException
	 * 
	 */
	@SuppressWarnings("serial")
	public void delete(SubSegment subSegment, String type) {
		logger.debug("Entering");
		int recordCount = 0;
		StringBuilder deleteSql = new StringBuilder();
		
		deleteSql.append(" Delete From BMTSubSegments");
		deleteSql.append(StringUtils.trimToEmpty(type));
		deleteSql.append(" Where SegmentCode =:SegmentCode and SubSegmentCode = :SubSegmentCode");
		
		logger.debug("deleteSql: "+ deleteSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(subSegment);

		try {
			recordCount = this.namedParameterJdbcTemplate.update(deleteSql.toString(),beanParameters);

			if (recordCount <= 0) {
				ErrorDetails errorDetails = getError("41004",subSegment.getSegmentCode(),subSegment.getSubSegmentCode(),
					subSegment.getUserDetails().getUsrLanguage());
				throw new DataAccessException(errorDetails.getError()) {
				};
			}
		} catch (DataAccessException e) {
			logger.debug("Error in delete Method");
			logger.error("Exception: ", e);
			ErrorDetails errorDetails = getError("41006",subSegment.getSegmentCode(),subSegment.getSubSegmentCode(),
					subSegment.getUserDetails().getUsrLanguage());
			throw new DataAccessException(errorDetails.getError()) {
			};
		}
		logger.debug("Leaving");
	}

	/**
	 * This method insert new Records into BMTSubSegments or
	 * BMTSubSegments_Temp.
	 * 
	 * save Sub Segments
	 * 
	 * @param Sub
	 *            Segments (subSegment)
	 * @param type
	 *            (String) ""/_Temp/_View
	 * @return void
	 * @throws DataAccessException
	 * 
	 */
	@Override
	public void save(SubSegment subSegment, String type) {
		logger.debug("Entering");
		StringBuilder insertSql = new StringBuilder();

		insertSql.append("Insert Into BMTSubSegments");
		insertSql.append(StringUtils.trimToEmpty(type));
		insertSql.append(" (SegmentCode, SubSegmentCode, SubSegmentDesc, SubSegmentIsActive,");
		insertSql.append(" Version , LastMntBy, LastMntOn, RecordStatus, RoleCode, NextRoleCode, TaskId, NextTaskId,");
		insertSql.append(" RecordType, WorkflowId)");
		insertSql.append(" Values(:SegmentCode, :SubSegmentCode, :SubSegmentDesc, :SubSegmentIsActive,");
		insertSql.append(" :Version , :LastMntBy, :LastMntOn, :RecordStatus, :RoleCode, :NextRoleCode, :TaskId, :NextTaskId,");
		insertSql.append(" :RecordType, :WorkflowId)");
		
		logger.debug("insertSql: "+ insertSql.toString());		
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(subSegment);
		this.namedParameterJdbcTemplate.update(insertSql.toString(), beanParameters);
		logger.debug("Leaving");
	}

	/**
	 * This method updates the Record BMTSubSegments or BMTSubSegments_Temp. if
	 * Record not updated then throws DataAccessException with error 41004.
	 * update Sub Segments by key SegmentCode and Version
	 * 
	 * @param Sub
	 *            Segments (subSegment)
	 * @param type
	 *            (String) ""/_Temp/_View
	 * @return void
	 * @throws DataAccessException
	 * 
	 */
	@SuppressWarnings("serial")
	@Override
	public void update(SubSegment subSegment, String type) {
		logger.debug("Entering");
		int recordCount = 0;
		StringBuilder updateSql = new StringBuilder();

		updateSql.append("Update BMTSubSegments");
		updateSql.append(StringUtils.trimToEmpty(type));
		updateSql.append(" Set SegmentCode = :SegmentCode, SubSegmentCode = :SubSegmentCode, SubSegmentDesc = :SubSegmentDesc,");
		updateSql.append(" SubSegmentIsActive = :SubSegmentIsActive,");
		updateSql.append(" Version = :Version , LastMntBy = :LastMntBy, LastMntOn = :LastMntOn, RecordStatus= :RecordStatus,");
		updateSql.append(" RoleCode = :RoleCode, NextRoleCode = :NextRoleCode, TaskId = :TaskId, NextTaskId = :NextTaskId,");
		updateSql.append(" RecordType = :RecordType, WorkflowId = :WorkflowId ");
		updateSql.append(" Where  SubSegmentCode = :SubSegmentCode");
		if (!type.endsWith("_Temp")) {
			updateSql.append("  AND Version= :Version-1");
		}

		logger.debug("updateSql: "+ updateSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(subSegment);
		recordCount = this.namedParameterJdbcTemplate.update(updateSql.toString(),beanParameters);

		if (recordCount <= 0) {
			logger.debug("Error in Update Method Count :" + recordCount);
			ErrorDetails errorDetails = getError("41003", subSegment.getSegmentCode(),subSegment.getSubSegmentCode(),
					subSegment.getUserDetails().getUsrLanguage());
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
	private ErrorDetails  getError(String errorId, String segmentCode, String subSegmentCode, String userLanguage){
		String[][] parms= new String[2][2]; 
		parms[1][0] = segmentCode;
		parms[1][1] = subSegmentCode;

		parms[0][0] = PennantJavaUtil.getLabel("label_SegmentCode")+ ":" + parms[1][0];
		parms[0][1] = PennantJavaUtil.getLabel("label_SubSegmentCode")+ ":" + parms[1][1];
		return ErrorUtil.getErrorDetail(new ErrorDetails(PennantConstants.KEY_FIELD, errorId, parms[0],parms[1]), userLanguage);
	}
}