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
 * FileName    		:  GenderDAOImpl.java                                                   * 	  
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
import com.pennant.backend.dao.systemmasters.GenderDAO;
import com.pennant.backend.model.ErrorDetails;
import com.pennant.backend.model.systemmasters.Gender;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantJavaUtil;

/**
 * DAO methods implementation for the <b>Gender model</b> class.<br>
 * 
 */
public class GenderDAOImpl extends BasisCodeDAO<Gender> implements GenderDAO {

	private static Logger logger = Logger.getLogger(GenderDAOImpl.class);

	// Spring Named JDBC Template
	private NamedParameterJdbcTemplate namedParameterJdbcTemplate;

	public GenderDAOImpl() {
		super();
	}
	
	/**
	 * Fetch the Record Genders details by key field
	 * 
	 * @param id
	 *            (String)
	 * @param type
	 *            (String) ""/_Temp/_View
	 * @return Gender
	 */
	@Override
	public Gender getGenderById(final String id, String type) {
		logger.debug("Entering");
		Gender gender = new Gender();
		gender.setId(id);
		StringBuilder selectSql = new StringBuilder();
		
		selectSql.append("SELECT GenderCode, GenderDesc, GenderIsActive,SystemDefault," );
		selectSql.append(" Version, LastMntOn, LastMntBy,RecordStatus, RoleCode, NextRoleCode, TaskId, NextTaskId, RecordType, WorkflowId" );
		selectSql.append(" FROM  BMTGenders");
		selectSql.append(StringUtils.trimToEmpty(type));
		selectSql.append(" Where GenderCode =:GenderCode") ;
				
		logger.debug("selectSql: " + selectSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(gender);
		RowMapper<Gender> typeRowMapper = ParameterizedBeanPropertyRowMapper.newInstance(Gender.class);

		try {
			gender = this.namedParameterJdbcTemplate.queryForObject(selectSql.toString(), beanParameters, typeRowMapper);
		} catch (EmptyResultDataAccessException e) {
			logger.error("Exception: ", e);
			gender = null;
		}
		logger.debug("Leaving");
		return gender;
	}

	/**
	 * @param dataSource
	 *            the dataSource to set
	 */
	public void setDataSource(DataSource dataSource) {
		this.namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
	}

	/**
	 * This method Deletes the Record from the BMTGenders or BMTGenders_Temp. if
	 * Record not deleted then throws DataAccessException with error 41003.
	 * delete Genders by key GenderCode
	 * 
	 * @param Genders
	 *            (gender)
	 * @param type
	 *            (String) ""/_Temp/_View
	 * @return void
	 * @throws DataAccessException
	 * 
	 */
	@SuppressWarnings("serial")
	public void delete(Gender gender, String type) {
		logger.debug("Entering");
		int recordCount = 0;
		StringBuilder deleteSql = new StringBuilder();
		
		deleteSql.append("Delete From BMTGenders");
		deleteSql.append(StringUtils.trimToEmpty(type));
		deleteSql.append(" Where GenderCode =:GenderCode");
		
		logger.debug("deleteSql: "+ deleteSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(gender);

		try {
			recordCount = this.namedParameterJdbcTemplate.update(deleteSql.toString(), beanParameters);

			if (recordCount <= 0) {
				ErrorDetails errorDetails= getError("41004", gender.getGenderCode(), gender.getUserDetails().getUsrLanguage());
				throw new DataAccessException(errorDetails.getError()) {
				};
			}
		} catch (DataAccessException e) {
			logger.debug("Error in delete Method");
			logger.error("Exception: ", e);
			ErrorDetails errorDetails= getError("41006", gender.getGenderCode(), gender.getUserDetails().getUsrLanguage());
			throw new DataAccessException(errorDetails.getError()) {
			};
		}
		logger.debug("Leaving");
	}

	/**
	 * This method insert new Records into BMTGenders or BMTGenders_Temp.
	 * 
	 * save Genders
	 * 
	 * @param Genders
	 *            (gender)
	 * @param type
	 *            (String) ""/_Temp/_View
	 * @return void
	 * @throws DataAccessException
	 * 
	 */
	@Override
	public String save(Gender gender, String type) {
		logger.debug("Entering");
		StringBuilder insertSql = new StringBuilder();
		
		insertSql.append("Insert Into BMTGenders");
		insertSql.append(StringUtils.trimToEmpty(type));
		insertSql.append(" (GenderCode, GenderDesc, GenderIsActive,SystemDefault," );
		insertSql.append(" Version , LastMntBy, LastMntOn, RecordStatus, RoleCode, NextRoleCode, TaskId, NextTaskId,");
		insertSql.append(" RecordType, WorkflowId)");
		insertSql.append(" Values(:GenderCode, :GenderDesc, :GenderIsActive, :SystemDefault, " );
		insertSql.append(" :Version , :LastMntBy, :LastMntOn, :RecordStatus, :RoleCode, :NextRoleCode, :TaskId, :NextTaskId,");
		insertSql.append(" :RecordType, :WorkflowId)");
		
		logger.debug("insertSql: "+ insertSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(gender);
		this.namedParameterJdbcTemplate.update(insertSql.toString(), beanParameters);

		logger.debug("Leaving");
		return gender.getId();
	}

	/**
	 * This method updates the Record BMTGenders or BMTGenders_Temp. if Record
	 * not updated then throws DataAccessException with error 41004. update
	 * Genders by key GenderCode and Version
	 * 
	 * @param Genders
	 *            (gender)
	 * @param type
	 *            (String) ""/_Temp/_View
	 * @return void
	 * @throws DataAccessException
	 * 
	 */
	@SuppressWarnings("serial")
	@Override
	public void update(Gender gender, String type) {
		int recordCount = 0;
		logger.debug("Entering");
		StringBuilder updateSql = new StringBuilder();
		
		updateSql.append("Update BMTGenders");
		updateSql.append(StringUtils.trimToEmpty(type));
		updateSql.append(" Set GenderCode = :GenderCode, GenderDesc = :GenderDesc, GenderIsActive = :GenderIsActive, SystemDefault=:SystemDefault," );
		updateSql.append(" Version = :Version , LastMntBy = :LastMntBy, LastMntOn = :LastMntOn, " );
		updateSql.append(" RecordStatus= :RecordStatus, RoleCode = :RoleCode,NextRoleCode = :NextRoleCode, TaskId = :TaskId," );
		updateSql.append(" NextTaskId = :NextTaskId, RecordType = :RecordType, WorkflowId = :WorkflowId" );
		updateSql.append(" Where GenderCode =:GenderCode ");
		if (!type.endsWith("_Temp")){
			updateSql.append(" AND Version= :Version-1");
		}

		logger.debug("updateSql: "+ updateSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(gender);
		recordCount = this.namedParameterJdbcTemplate.update(updateSql.toString(),	beanParameters);

		if (recordCount <= 0) {
			logger.debug("Error in Update Method Count :" + recordCount);
			ErrorDetails errorDetails= getError("41003", gender.getGenderCode(), gender.getUserDetails().getUsrLanguage());
			throw new DataAccessException(errorDetails.getError()) {
			};
		}
		logger.debug("Leaving");
	}
	
	/**
	 * Fetch the count of system default values by key field
	 * 
	 * @param id
	 *            (String)
	 * @param type
	 *            (String) ""/_Temp/_View
	 * @return Gender
	 */
	@Override
	public String getSystemDefaultCount(String genderCode) {
		logger.debug("Entering");
		Gender gender = new Gender();
		gender.setGenderCode(genderCode);
		gender.setSystemDefault(true);

		StringBuilder selectSql = new StringBuilder();

		selectSql.append("SELECT GenderCode FROM  BMTGenders_View ");
		selectSql.append(" Where GenderCode != :GenderCode and SystemDefault = :SystemDefault");

		logger.debug("selectSql: " + selectSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(gender);
		String dftGenderCode = "";
		try {
			dftGenderCode = this.namedParameterJdbcTemplate.queryForObject(selectSql.toString(), beanParameters, String.class);
        } catch (EmptyResultDataAccessException e) {
        	logger.warn("Exception: ", e);
        	dftGenderCode = "";
        }
		logger.debug("Leaving");
		return dftGenderCode;
	}

	
	/**
	 * This method for getting the error details
	 * @param errorId (String)
	 * @param Id (String)
	 * @param userLanguage (String)
	 * @return ErrorDetails
	 */
	private ErrorDetails  getError(String errorId, String genderCode,String userLanguage){
		String[][] parms= new String[2][2]; 
		parms[1][0] = genderCode;

		parms[0][0] = PennantJavaUtil.getLabel("label_GenderCode")+ ":" + parms[1][0];
		return ErrorUtil.getErrorDetail(new ErrorDetails(PennantConstants.KEY_FIELD, errorId, parms[0],parms[1]), userLanguage);
	}
}