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
 * FileName    		:  CourseDAOImpl.java                                                   * 	  
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
import com.pennant.backend.dao.amtmasters.CourseDAO;
import com.pennant.backend.dao.impl.BasisCodeDAO;
import com.pennant.backend.model.ErrorDetails;
import com.pennant.backend.model.WorkFlowDetails;
import com.pennant.backend.model.amtmasters.Course;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantJavaUtil;
import com.pennant.backend.util.WorkFlowUtil;

/**
 * DAO methods implementation for the <b>Course model</b> class.<br>
 * 
 */

public class CourseDAOImpl extends BasisCodeDAO<Course> implements CourseDAO {

	private static Logger logger = Logger.getLogger(CourseDAOImpl.class);

	// Spring Named JDBC Template
	private NamedParameterJdbcTemplate namedParameterJdbcTemplate;

	/**
	 * This method set the Work Flow id based on the module name and return the new Course 
	 * @return Course
	 */

	@Override
	public Course getCourse() {
		logger.debug("Entering");
		WorkFlowDetails workFlowDetails=WorkFlowUtil.getWorkFlowDetails("Course");
		Course course= new Course();
		if (workFlowDetails!=null){
			course.setWorkflowId(workFlowDetails.getWorkFlowId());
		}
		logger.debug("Leaving");
		return course;
	}


	/**
	 * This method get the module from method getCourse() and
	 *  set the new record flag as true and return Course()   
	 * @return Course
	 */


	@Override
	public Course getNewCourse() {
		logger.debug("Entering");
		Course course = getCourse();
		course.setNewRecord(true);
		logger.debug("Leaving");
		return course;
	}

	/**
	 * Fetch the Record  Course Detail details by key field
	 * 
	 * @param id (String)
	 * @param  type (String)
	 * 			""/_Temp/_View          
	 * @return Course
	 */
	@Override
	public Course getCourseById(final String id, String type) {
		logger.debug("Entering");
		Course course = getCourse();
		course.setId(id);
		StringBuilder selectSql = new StringBuilder();

		selectSql.append("Select CourseName,CourseDesc, ");
		selectSql.append(" Version , LastMntBy, LastMntOn, RecordStatus, RoleCode, " );
		selectSql.append(" NextRoleCode, TaskId, NextTaskId, RecordType, WorkflowId  " );
		selectSql.append(" From AMTCourse");
		selectSql.append(StringUtils.trimToEmpty(type));
		selectSql.append(" Where CourseName =:CourseName ");
		logger.debug("selectSql: " + selectSql.toString());

		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(course);
		RowMapper<Course> typeRowMapper = ParameterizedBeanPropertyRowMapper.newInstance(Course.class);

		try{
			course = this.namedParameterJdbcTemplate.queryForObject(selectSql.toString(), 
					beanParameters, typeRowMapper);	
		}catch (EmptyResultDataAccessException e) {
			course = null;
		}
		logger.debug("Leaving");
		return course;
	}

	/**
	 * This method initialize the Record.
	 * @param Course (course)
	 * @return Course
	 */
	@Override
	public void initialize(Course course) {
		super.initialize(course);
	}
	
	/**
	 * This method refresh the Record.
	 * @param Course (course)
	 * @return void
	 */
	@Override
	public void refresh(Course course) {

	}

	/**
	 * To Set  dataSource
	 * @param dataSource
	 */
	public void setDataSource(DataSource dataSource) {
		this.namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
	}

	/**
	 * This method Deletes the Record from the AMTCourse or AMTCourse_Temp.
	 * if Record not deleted then throws DataAccessException with  error  41003.
	 * delete Course Detail by key CourseName
	 * 
	 * @param Course Detail (course)
	 * @param  type (String)
	 * 			""/_Temp/_View          
	 * @return void
	 * @throws DataAccessException
	 * 
	 */
	@SuppressWarnings("serial")
	public void delete(Course course,String type) {
		logger.debug("Entering");
		int recordCount = 0;
		StringBuilder deleteSql = 	new StringBuilder();

		deleteSql.append("Delete From AMTCourse");
		deleteSql.append(StringUtils.trimToEmpty(type)); 
		deleteSql.append(" Where CourseName =:CourseName");
		logger.debug("deleteSql: "+ deleteSql.toString());

		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(course);
		try{
			recordCount = this.namedParameterJdbcTemplate.update(deleteSql.toString(), beanParameters);
			if (recordCount <= 0) {
				ErrorDetails errorDetails= getError("41003",course.getCourseName(), 
						course.getUserDetails().getUsrLanguage());
				throw new DataAccessException(errorDetails.getError()) {};
			}
		}catch(DataAccessException e){
			logger.error(e);
			ErrorDetails errorDetails= getError("41006",course.getCourseName(), 
					course.getUserDetails().getUsrLanguage());
			throw new DataAccessException(errorDetails.getError()) {};
		}
		logger.debug("Leaving");
	}

	/**
	 * This method insert new Records into AMTCourse or AMTCourse_Temp.
	 *
	 * save Course Detail 
	 * 
	 * @param Course Detail (course)
	 * @param  type (String)
	 * 			""/_Temp/_View          
	 * @return void
	 * @throws DataAccessException
	 * 
	 */

	@Override
	public String save(Course course,String type) {
		logger.debug("Entering");
		StringBuilder insertSql = 	new StringBuilder();

		insertSql.append(" Insert Into AMTCourse");
		insertSql.append(StringUtils.trimToEmpty(type));
		insertSql.append(" (CourseName , CourseDesc, " );
		insertSql.append(" Version , LastMntBy, LastMntOn, RecordStatus, RoleCode, NextRoleCode,");
		insertSql.append(" TaskId, NextTaskId, RecordType, WorkflowId)" );
		insertSql.append(" Values(:CourseName , :CourseDesc, " );
		insertSql.append(" :Version , :LastMntBy, :LastMntOn, :RecordStatus, :RoleCode,"); 
		insertSql.append(" :NextRoleCode, :TaskId, :NextTaskId, :RecordType, :WorkflowId)");
		logger.debug("selectSql: " + insertSql.toString());     

		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(course);
		this.namedParameterJdbcTemplate.update(insertSql.toString(), beanParameters);

		logger.debug("Leaving");
		return course.getId();
	}

	/**
	 * This method updates the Record AMTCourse or AMTCourse_Temp.
	 * if Record not updated then throws DataAccessException with  error  41004.
	 * update Course Detail by key CourseName and Version
	 * 
	 * @param Course Detail (course)
	 * @param  type (String)
	 * 			""/_Temp/_View          
	 * @return void
	 * @throws DataAccessException
	 * 
	 */
	@SuppressWarnings("serial")
	@Override
	public void update(Course course,String type) {
		int recordCount = 0;
		logger.debug("Entering Update Method");
		StringBuilder  updateSql = 	new StringBuilder();

		updateSql.append("Update AMTCourse");
		updateSql.append(StringUtils.trimToEmpty(type));  
		updateSql.append(" Set CourseName = :CourseName, CourseDesc = :CourseDesc,"); 
		updateSql.append(" Version = :Version , LastMntBy = :LastMntBy, LastMntOn = :LastMntOn, " );
		updateSql.append(" RecordStatus= :RecordStatus, RoleCode = :RoleCode, NextRoleCode = :NextRoleCode, " );
		updateSql.append(" TaskId = :TaskId, NextTaskId = :NextTaskId, RecordType = :RecordType, " );
		updateSql.append(" WorkflowId = :WorkflowId");
		updateSql.append(" Where CourseName =:CourseName");
		logger.debug("updateSql: " + updateSql.toString());    

		if (!type.endsWith("_TEMP")){
			updateSql.append("  AND Version= :Version-1");
		}

		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(course);
		recordCount = this.namedParameterJdbcTemplate.update(updateSql.toString(), beanParameters);

		if (recordCount <= 0) {
			logger.debug("Error Update Method Count :"+recordCount);
			ErrorDetails errorDetails= getError("41004",course.getCourseName(), 
					course.getUserDetails().getUsrLanguage());
			throw new DataAccessException(errorDetails.getError()) {};
		}
		logger.debug("Leaving Update Method");
	}

	/**
	 * This method for getting the error details
	 * @param errorId (String)
	 * @param Id (String)
	 * @param userLanguage (String)
	 * @return ErrorDetails
	 */
	private ErrorDetails  getError(String errorId, String courseName, String userLanguage){
		String[][] parms= new String[2][1]; 
		parms[1][0] = courseName;
		parms[0][0] = PennantJavaUtil.getLabel("label_CourseName")+ ":" + parms[1][0];
		return ErrorUtil.getErrorDetail(new ErrorDetails(PennantConstants.KEY_FIELD, 
				errorId, parms[0],parms[1]), userLanguage);
	}

}