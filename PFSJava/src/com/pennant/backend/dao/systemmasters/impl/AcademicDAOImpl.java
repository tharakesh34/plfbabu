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
 * FileName    		:  AcademicDAOImpl.java                                                   * 	  
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
import com.pennant.backend.dao.impl.BasisNextidDaoImpl;
import com.pennant.backend.dao.systemmasters.AcademicDAO;
import com.pennant.backend.model.ErrorDetails;
import com.pennant.backend.model.WorkFlowDetails;
import com.pennant.backend.model.systemmasters.Academic;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantJavaUtil;
import com.pennant.backend.util.WorkFlowUtil;

/**
 * DAO methods implementation for the <b>Academic model</b> class.<br>
 * 
 */
public class AcademicDAOImpl extends BasisNextidDaoImpl<Academic> implements AcademicDAO {

	private static Logger logger = Logger.getLogger(AcademicDAOImpl.class);

	// Spring Named JDBC Template
	private NamedParameterJdbcTemplate namedParameterJdbcTemplate;

	/**
	 * This method set the Work Flow id based on the module name and return the
	 * new Academic
	 * 
	 * @return Academic
	 */
	@Override
	public Academic getAcademic() {
		logger.debug("Entering");
		WorkFlowDetails workFlowDetails = WorkFlowUtil.getWorkFlowDetails("Academic");
		Academic academic = new Academic();
		if (workFlowDetails != null) {
			academic.setWorkflowId(workFlowDetails.getWorkFlowId());
		}
		logger.debug("Leaving");
		return academic;
	}

	/**
	 * This method get the module from method getAcademic() and set the new
	 * record flag as true and return Academic()
	 * 
	 * @return Academic
	 */
	@Override
	public Academic getNewAcademic() {
		logger.debug("Entering");
		Academic academic = getAcademic();
		academic.setNewRecord(true);
		logger.debug("Leaving");
		return academic;
	}

	/**
	 * Fetch the Record Academic Details details by key field
	 * 
	 * @param id
	 *            (String)
	 * @param type
	 *            (String) ""/_Temp/_View
	 * @return Academic
	 */
	@Override
	public Academic getAcademicById(final long academicID, String type) {
		logger.debug("Entering");
		Academic academic = getAcademic();
		academic.setAcademicID(academicID);
		StringBuilder selectSql = new StringBuilder();
		
		selectSql.append(" Select AcademicID,AcademicLevel, AcademicDecipline, AcademicDesc," );
		/*if(type.contains("View")){
			selectSql.append("");
		}*/
		selectSql.append(" Version, LastMntOn, LastMntBy,RecordStatus, RoleCode, NextRoleCode, TaskId, NextTaskId, RecordType, WorkflowId" );
		selectSql.append(" FROM  BMTAcademics");
		selectSql.append(StringUtils.trimToEmpty(type));
		selectSql.append(" Where AcademicID =:AcademicID") ;

		logger.debug("selectSql: " + selectSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(academic);
		RowMapper<Academic> typeRowMapper = ParameterizedBeanPropertyRowMapper.newInstance(Academic.class);

		try {
			academic = this.namedParameterJdbcTemplate.queryForObject(selectSql.toString(), beanParameters, typeRowMapper);
		} catch (EmptyResultDataAccessException e) {
			logger.error(e);
			academic = null;
		}
		logger.debug("Leaving");
		return academic;
	}

	/**
	 * Fetch the Record Academic Details details by key field
	 * 
	 * @param id
	 *            (String)
	 * @param type
	 *            (String) ""/_Temp/_View
	 * @return Academic
	 */
	@Override
	public Academic getAcademic(String academicLevel, String academicDecipline, String type) {
		logger.debug("Entering");
		Academic academic = getAcademic();
		academic.setAcademicLevel(academicLevel);
		academic.setAcademicDecipline(academicDecipline);
		StringBuilder selectSql = new StringBuilder();

		selectSql.append(" Select AcademicID,AcademicLevel, AcademicDecipline, AcademicDesc," );
		/*if(type.contains("View")){
			selectSql.append("");
		}*/
		selectSql.append(" Version, LastMntOn, LastMntBy,RecordStatus, RoleCode, NextRoleCode, TaskId, NextTaskId, RecordType, WorkflowId" );
		selectSql.append(" FROM  BMTAcademics");
		selectSql.append(StringUtils.trimToEmpty(type));
		selectSql.append(" Where AcademicLevel =:AcademicLevel AND  AcademicDecipline=:AcademicDecipline") ;

		logger.debug("selectSql: " + selectSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(academic);
		RowMapper<Academic> typeRowMapper = ParameterizedBeanPropertyRowMapper.newInstance(Academic.class);

		try {
			academic = this.namedParameterJdbcTemplate.queryForObject(selectSql.toString(), beanParameters, typeRowMapper);
		} catch (EmptyResultDataAccessException e) {
			logger.error(e);
			academic = null;
		}
		logger.debug("Leaving");
		return academic;
	}

	/**
	 * This method initialize the Record.
	 * 
	 * @param Academic
	 *            (academic)
	 * @return Academic
	 */
	@Override
	public void initialize(Academic academic) {
		super.initialize(academic);
	}

	/**
	 * This method refresh the Record.
	 * 
	 * @param Academic
	 *            (academic)
	 * @return void
	 */
	@Override
	public void refresh(Academic academic) {

	}

	/**
	 * @param dataSource
	 *            the dataSource to set
	 */
	public void setDataSource(DataSource dataSource) {
		this.namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
	}

	/**
	 * This method Deletes the Record from the BMTAcademics or
	 * BMTAcademics_Temp. if Record not deleted then throws DataAccessException
	 * with error 41003. delete Academic Details by key AcademicLevel
	 * 
	 * @param Academic
	 *            Details (academic)
	 * @param type
	 *            (String) ""/_Temp/_View
	 * @return void
	 * @throws DataAccessException
	 * 
	 */
	@SuppressWarnings("serial")
	public void delete(Academic academic, String type) {
		logger.debug("Entering");
		int recordCount = 0;

		StringBuilder deleteSql =new StringBuilder();
		deleteSql.append("Delete From BMTAcademics");
		deleteSql.append(StringUtils.trimToEmpty(type));
		deleteSql.append(" Where  AcademicID =:AcademicID ");

		logger.debug("deleteSql: "+ deleteSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(academic);

		try {
			recordCount = this.namedParameterJdbcTemplate.update(deleteSql.toString(),	beanParameters);

			if (recordCount <= 0) {
				ErrorDetails errorDetails= getError("41004", academic.getAcademicLevel(), 
						academic.getAcademicDecipline(), academic.getUserDetails().getUsrLanguage());
				throw new DataAccessException(errorDetails.getError()) {
				};
			}
		} catch (DataAccessException e) {
			logger.debug("Error in delete Method");
			logger.error(e);
			ErrorDetails errorDetails= getError("41006", academic.getAcademicLevel(), 
					academic.getAcademicDecipline(), academic.getUserDetails().getUsrLanguage());
			throw new DataAccessException(errorDetails.getError()) {
			};
		}
		logger.debug("Leaving");
	}

	/**
	 * This method insert new Records into BMTAcademics or BMTAcademics_Temp.
	 * 
	 * save Academic Details
	 * 
	 * @param Academic
	 *            Details (academic)
	 * @param type
	 *            (String) ""/_Temp/_View
	 * @return void
	 * @throws DataAccessException
	 * 
	 */
	@Override
	public long save(Academic academic, String type) {
		logger.debug("Entering");
		if (academic.getId() == Long.MIN_VALUE) {
			academic.setAcademicID(getNextidviewDAO().getNextId("SeqBMTAcademics"));
		}
		StringBuilder insertSql = new StringBuilder();

		insertSql.append("Insert Into BMTAcademics");
		insertSql.append(StringUtils.trimToEmpty(type));
		insertSql.append(" (AcademicID,AcademicLevel, AcademicDecipline, AcademicDesc," );
		insertSql.append(" Version , LastMntBy, LastMntOn, RecordStatus, RoleCode, NextRoleCode, TaskId, NextTaskId," );
		insertSql.append(" RecordType, WorkflowId)");
		insertSql.append(" Values(:AcademicID,:AcademicLevel, :AcademicDecipline, :AcademicDesc, " );
		insertSql.append(" :Version , :LastMntBy, :LastMntOn, :RecordStatus, :RoleCode, :NextRoleCode, :TaskId, :NextTaskId, ");
		insertSql.append(" :RecordType, :WorkflowId)");

		logger.debug("insertSql: "+ insertSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(academic);
		this.namedParameterJdbcTemplate.update(insertSql.toString(), beanParameters);

		logger.debug("Leaving");
		return academic.getAcademicID();
	}

	/**
	 * This method updates the Record BMTAcademics or BMTAcademics_Temp. if
	 * Record not updated then throws DataAccessException with error 41004.
	 * update Academic Details by key AcademicLevel and Version
	 * 
	 * @param Academic
	 *            Details (academic)
	 * @param type
	 *            (String) ""/_Temp/_View
	 * @return void
	 * @throws DataAccessException
	 * 
	 */
	@SuppressWarnings("serial")
	@Override
	public void update(Academic academic, String type) {
		logger.debug("Entering");
		int recordCount = 0;
		StringBuilder updateSql = new StringBuilder();

		updateSql.append("Update BMTAcademics");
		updateSql.append(StringUtils.trimToEmpty(type));
		updateSql.append(" Set AcademicID = :AcademicID,AcademicLevel = :AcademicLevel," );
		updateSql.append(" AcademicDecipline = :AcademicDecipline, AcademicDesc = :AcademicDesc ," );
		updateSql.append(" Version = :Version , LastMntBy = :LastMntBy, LastMntOn = :LastMntOn, " );
		updateSql.append(" RecordStatus= :RecordStatus, RoleCode = :RoleCode,NextRoleCode = :NextRoleCode, TaskId = :TaskId," );
		updateSql.append(" NextTaskId = :NextTaskId, RecordType = :RecordType, WorkflowId = :WorkflowId" );
		updateSql.append("  Where AcademicID =:AcademicID ");
		if (!type.endsWith("_TEMP")){
			updateSql.append(" AND Version= :Version-1");
		}

		logger.debug("updateSql: "+ updateSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(academic);
		recordCount = this.namedParameterJdbcTemplate.update(updateSql.toString(), beanParameters);

		if (recordCount <= 0) {
			logger.debug("Error in Update Method Count :" + recordCount);
			ErrorDetails errorDetails= getError("41003", academic.getAcademicLevel(), 
					academic.getAcademicDecipline(), academic.getUserDetails().getUsrLanguage());
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
	private ErrorDetails  getError(String errorId, String academicLevel,String academicDecipline, String userLanguage){
		String[][] parms= new String[2][2]; 
		parms[1][0] = academicLevel;
		parms[1][1] = academicDecipline;

		parms[0][0] = PennantJavaUtil.getLabel("label_AcademicLevel")+ ":" + parms[1][0];
		parms[0][1] = PennantJavaUtil.getLabel("label_AcademicDecipline")+ ":" + parms[1][1];
		return ErrorUtil.getErrorDetail(new ErrorDetails(PennantConstants.KEY_FIELD, errorId, parms[0],parms[1]), userLanguage);
	}
}