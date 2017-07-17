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
 * FileName    		:  CourseTypeDAOImpl.java                                                   * 	  
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

import com.pennant.backend.dao.amtmasters.CourseTypeDAO;
import com.pennant.backend.dao.impl.BasisCodeDAO;
import com.pennant.backend.model.amtmasters.CourseType;
import com.pennanttech.pennapps.core.ConcurrencyException;
import com.pennanttech.pff.core.DependencyFoundException;

/**
 * DAO methods implementation for the <b>CourseType model</b> class.<br>
 * 
 */
public class CourseTypeDAOImpl extends BasisCodeDAO<CourseType> implements CourseTypeDAO {
	private static Logger logger = Logger.getLogger(CourseTypeDAOImpl.class);

	// Spring Named JDBC Template
	private NamedParameterJdbcTemplate namedParameterJdbcTemplate;

	public CourseTypeDAOImpl() {
		super();
	}

	/**
	 * Fetch the Record  Course Type details by key field
	 * 
	 * @param id (String)
	 * @param  type (String)
	 * 			""/_Temp/_View          
	 * @return CourseType
	 */
	@Override
	public CourseType getCourseTypeById(final String id, String type) {
		logger.debug("Entering");
		CourseType courseType = new CourseType();
		courseType.setId(id);
		StringBuilder selectSql = new StringBuilder();

		selectSql.append(" Select CourseTypeCode, CourseTypeDesc, ");
		selectSql.append(" Version , LastMntBy, LastMntOn, RecordStatus, RoleCode, " );
		selectSql.append(" NextRoleCode, TaskId, NextTaskId, RecordType, WorkflowId");
		selectSql.append(" From AMTCourseType");
		selectSql.append(StringUtils.trimToEmpty(type));
		selectSql.append(" Where CourseTypeCode =:CourseTypeCode");

		logger.debug("selectSql: " + selectSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(courseType);
		RowMapper<CourseType> typeRowMapper = ParameterizedBeanPropertyRowMapper.newInstance(CourseType.class);

		try{
			courseType = this.namedParameterJdbcTemplate.queryForObject(selectSql.toString(), beanParameters, typeRowMapper);	
		}catch (EmptyResultDataAccessException e) {
			logger.warn("Exception: ", e);
			courseType = null;
		}
		logger.debug("Leaving");
		return courseType;
	}

	/**
	 * To Set  dataSource
	 * @param dataSource
	 */
	public void setDataSource(DataSource dataSource) {
		this.namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
	}

	/**
	 * This method Deletes the Record from the AMTCourseType or AMTCourseType_Temp.
	 * if Record not deleted then throws DataAccessException with  error  41003.
	 * delete Course Type by key CourseTypeCode
	 * 
	 * @param Course Type (courseType)
	 * @param  type (String)
	 * 			""/_Temp/_View          
	 * @return void
	 * @throws DataAccessException
	 * 
	 */
	public void delete(CourseType courseType, String type) {
		logger.debug("Entering");
		int recordCount = 0;
		StringBuilder deleteSql = new StringBuilder();

		deleteSql.append("Delete From AMTCourseType");
		deleteSql.append(StringUtils.trimToEmpty(type)); 
		deleteSql.append(" Where CourseTypeCode =:CourseTypeCode");

		logger.debug("deleteSql: " + deleteSql.toString()); 
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(courseType);

		try {
			recordCount = this.namedParameterJdbcTemplate.update(deleteSql.toString(), beanParameters);
			if (recordCount <= 0) {
				throw new ConcurrencyException();
			}
		} catch (DataAccessException e) {
			throw new DependencyFoundException(e);
		}
		logger.debug("Leaving ");
	}

	/**
	 * This method insert new Records into AMTCourseType or AMTCourseType_Temp.
	 *
	 * save Course Type 
	 * 
	 * @param Course Type (courseType)
	 * @param  type (String)
	 * 			""/_Temp/_View          
	 * @return void
	 * @throws DataAccessException
	 * 
	 */
	@Override
	public String save(CourseType courseType,String type) {
		logger.debug("Entering");

		StringBuilder insertSql = new StringBuilder("Insert Into AMTCourseType");
		insertSql.append(StringUtils.trimToEmpty(type));
		insertSql.append("(CourseTypeCode, CourseTypeDesc, ");
		insertSql.append(" Version , LastMntBy, LastMntOn, RecordStatus, RoleCode, " );
		insertSql.append(" NextRoleCode, TaskId, NextTaskId, RecordType, WorkflowId )");
		insertSql.append(" Values(:CourseTypeCode, :CourseTypeDesc, :Version , :LastMntBy, " );
		insertSql.append(" :LastMntOn, :RecordStatus, :RoleCode, :NextRoleCode,");
		insertSql.append(" :TaskId, :NextTaskId, :RecordType, :WorkflowId)");
		logger.debug("insertSql: " + insertSql.toString()); 

		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(courseType);
		this.namedParameterJdbcTemplate.update(insertSql.toString(), beanParameters);

		logger.debug("Leaving");
		return courseType.getId();
	}

	/**
	 * This method updates the Record AMTCourseType or AMTCourseType_Temp.
	 * if Record not updated then throws DataAccessException with  error  41004.
	 * update Course Type by key CourseTypeCode and Version
	 * 
	 * @param Course Type (courseType)
	 * @param  type (String)
	 * 			""/_Temp/_View          
	 * @return void
	 * @throws DataAccessException
	 * 
	 */
	@Override
	public void update(CourseType courseType, String type) {
		int recordCount = 0;
		logger.debug("Entering");

		StringBuilder updateSql = new StringBuilder("Update AMTCourseType");
		updateSql.append(StringUtils.trimToEmpty(type));
		updateSql.append(" Set CourseTypeDesc = :CourseTypeDesc, " );
		updateSql.append(" Version = :Version , LastMntBy = :LastMntBy, LastMntOn = :LastMntOn, " );
		updateSql.append(" RecordStatus= :RecordStatus, RoleCode = :RoleCode, NextRoleCode = :NextRoleCode," );
		updateSql.append(" TaskId = :TaskId, NextTaskId = :NextTaskId, RecordType = :RecordType, WorkflowId = :WorkflowId");
		updateSql.append(" Where CourseTypeCode =:CourseTypeCode");
		logger.debug("updateSql: " + updateSql.toString()); 

		if (!type.endsWith("_Temp")) {
			updateSql.append("  AND Version= :Version-1");
		}

		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(courseType);
		recordCount = this.namedParameterJdbcTemplate.update(updateSql.toString(), beanParameters);
		if (recordCount <= 0) {
			throw new ConcurrencyException();
		}
		logger.debug("Leaving Update Method");
	}
}