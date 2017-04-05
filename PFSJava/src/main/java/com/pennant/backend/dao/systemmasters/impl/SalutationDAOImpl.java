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
 * FileName    		:  SalutationDAOImpl.java                                                   * 	  
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
import com.pennant.backend.dao.systemmasters.SalutationDAO;
import com.pennant.backend.model.ErrorDetails;
import com.pennant.backend.model.systemmasters.Salutation;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantJavaUtil;

/**
 * DAO methods implementation for the <b>Salutation model</b> class.<br>
 * 
 */
public class SalutationDAOImpl extends BasisCodeDAO<Salutation> implements SalutationDAO {

	private static Logger logger = Logger.getLogger(SalutationDAOImpl.class);

	// Spring Named JDBC Template
	private NamedParameterJdbcTemplate namedParameterJdbcTemplate;

	public SalutationDAOImpl() {
		super();
	}
	
	/**
	 * Fetch the Record Salutations details by key field
	 * 
	 * @param id
	 *            (String)
	 * @param type
	 *            (String) ""/_Temp/_View
	 * @return Salutation
	 */
	@Override
	public Salutation getSalutationById(final String id, String type) {
		logger.debug("Entering");
		Salutation salutation = new Salutation();
		salutation.setId(id);
		StringBuilder selectSql = new StringBuilder();
		
		selectSql.append("Select SalutationCode, SaluationDesc, SalutationIsActive,SalutationGenderCode,SystemDefault,");
		selectSql.append(" Version , LastMntBy, LastMntOn, RecordStatus, RoleCode, NextRoleCode, TaskId, NextTaskId, RecordType, WorkflowId");
		selectSql.append(" From BMTSalutations");
		selectSql.append(StringUtils.trimToEmpty(type));
		selectSql.append(" Where SalutationCode =:SalutationCode");

		logger.debug("selectSql: " + selectSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(salutation);
		RowMapper<Salutation> typeRowMapper = ParameterizedBeanPropertyRowMapper.newInstance(Salutation.class);

		try {
			salutation = this.namedParameterJdbcTemplate.queryForObject(selectSql.toString(), beanParameters, typeRowMapper);
		} catch (EmptyResultDataAccessException e) {
			logger.error("Exception: ", e);
			salutation = null;
		}
		logger.debug("Leaving");
		return salutation;
	}

	/**
	 * @param dataSource
	 *            the dataSource to set
	 */
	public void setDataSource(DataSource dataSource) {
		this.namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
	}

	/**
	 * This method Deletes the Record from the BMTSalutations or
	 * BMTSalutations_Temp. if Record not deleted then throws
	 * DataAccessException with error 41003. delete Salutations by key
	 * SalutationCode
	 * 
	 * @param Salutations
	 *            (salutation)
	 * @param type
	 *            (String) ""/_Temp/_View
	 * @return void
	 * @throws DataAccessException
	 * 
	 */
	@SuppressWarnings("serial")
	public void delete(Salutation salutation, String type) {
		logger.debug("Entering");
		int recordCount = 0;
		StringBuilder deleteSql = new StringBuilder();
		
		deleteSql.append(" Delete From BMTSalutations");
		deleteSql.append(StringUtils.trimToEmpty(type));
		deleteSql.append(" Where SalutationCode =:SalutationCode");
		
		logger.debug("deleteSql: "+ deleteSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(salutation);
		try {
			recordCount = this.namedParameterJdbcTemplate.update(deleteSql.toString(),beanParameters);

			if (recordCount <= 0) {
				ErrorDetails errorDetails = getError("41004",salutation.getSalutationCode(), salutation.getUserDetails().getUsrLanguage());
				throw new DataAccessException(errorDetails.getError()) {
				};
			}
		} catch (DataAccessException e) {
			logger.debug("Error in delete Method");
			logger.error("Exception: ", e);
			ErrorDetails errorDetails = getError("41006",salutation.getSalutationCode(), salutation.getUserDetails().getUsrLanguage());
			throw new DataAccessException(errorDetails.getError()) {
			};
		}
		logger.debug("Leaving");
	}

	/**
	 * This method insert new Records into BMTSalutations or
	 * BMTSalutations_Temp.
	 * 
	 * save Salutations
	 * 
	 * @param Salutations
	 *            (salutation)
	 * @param type
	 *            (String) ""/_Temp/_View
	 * @return void
	 * @throws DataAccessException
	 * 
	 */
	@Override
	public String save(Salutation salutation, String type) {
		logger.debug("Entering");
		StringBuilder insertSql = new StringBuilder();
		
		insertSql.append("Insert Into BMTSalutations");
		insertSql.append(StringUtils.trimToEmpty(type));
		insertSql.append(" (SalutationCode, SaluationDesc, SalutationIsActive,SalutationGenderCode,SystemDefault,");
		insertSql.append(" Version , LastMntBy, LastMntOn, RecordStatus, RoleCode, NextRoleCode, TaskId, NextTaskId,");
		insertSql.append(" RecordType, WorkflowId)");
		insertSql.append(" Values(:SalutationCode, :SaluationDesc, :SalutationIsActive, :SalutationGenderCode,:SystemDefault,");
		insertSql.append(" :Version , :LastMntBy, :LastMntOn, :RecordStatus, :RoleCode, :NextRoleCode, :TaskId, :NextTaskId,");
		insertSql.append(" :RecordType, :WorkflowId)");
		
		logger.debug("insertSql: "+ insertSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(salutation);
		this.namedParameterJdbcTemplate.update(insertSql.toString(), beanParameters);
		logger.debug("Leaving");
		return salutation.getId();
	}

	/**
	 * This method updates the Record BMTSalutations or BMTSalutations_Temp. if
	 * Record not updated then throws DataAccessException with error 41004.
	 * update Salutations by key SalutationCode and Version
	 * 
	 * @param Salutations
	 *            (salutation)
	 * @param type
	 *            (String) ""/_Temp/_View
	 * @return void
	 * @throws DataAccessException
	 * 
	 */
	@SuppressWarnings("serial")
	@Override
	public void update(Salutation salutation, String type) {
		logger.debug("Entering");
		int recordCount = 0;
		StringBuilder updateSql = new StringBuilder();
		
		updateSql.append("Update BMTSalutations");
		updateSql.append(StringUtils.trimToEmpty(type));
		updateSql.append(" Set SaluationDesc = :SaluationDesc,");
		updateSql.append(" SalutationIsActive = :SalutationIsActive, SalutationGenderCode = :SalutationGenderCode,SystemDefault=:SystemDefault,");
		updateSql.append(" Version = :Version , LastMntBy = :LastMntBy, LastMntOn = :LastMntOn, RecordStatus= :RecordStatus, ");
		updateSql.append(" RoleCode = :RoleCode, NextRoleCode = :NextRoleCode, TaskId = :TaskId, NextTaskId = :NextTaskId,");
		updateSql.append(" RecordType = :RecordType, WorkflowId = :WorkflowId ");
		updateSql.append(" Where SalutationCode =:SalutationCode");
		if (!type.endsWith("_Temp")) {
			updateSql.append("  AND Version = :Version-1");
		}

		logger.debug("updateSql: "+ updateSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(salutation);
		recordCount = this.namedParameterJdbcTemplate.update(updateSql.toString(),beanParameters);
		if (recordCount <= 0) {
			logger.debug("Error in Update Method Count :" + recordCount);
			ErrorDetails errorDetails = getError("41003",salutation.getSalutationCode(), salutation.getUserDetails().getUsrLanguage());
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
	public String getSystemDefaultCount(String salutationCode) {
		logger.debug("Entering");
		Salutation salutation = new Salutation();
		salutation.setSalutationCode(salutationCode);
		salutation.setSystemDefault(true);

		StringBuilder selectSql = new StringBuilder();

		selectSql.append("SELECT SalutationCode FROM  BMTSalutations_View ");
		selectSql.append(" Where SalutationCode != :SalutationCode and SystemDefault = :SystemDefault");

		logger.debug("selectSql: " + selectSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(salutation);
		String dftSalutationCode = "";
		try {
			dftSalutationCode = this.namedParameterJdbcTemplate.queryForObject(selectSql.toString(), beanParameters, String.class);
        } catch (Exception e) {
        	logger.warn("Exception: ", e);
        	dftSalutationCode = "";
        }
		logger.debug("Leaving");
		return dftSalutationCode;

	}
	

	@Override
    public void updateSytemDefaultByGender(String genderCode, boolean systemDefault) {
		logger.debug("Entering");
		StringBuilder updateSql = new StringBuilder();		
		updateSql.append("Update BMTSalutations  set SystemDefault=:SystemDefault ");
		updateSql.append(" Where SalutationGenderCode = :SalutationGenderCode");

		logger.debug("updateSql: "+ updateSql.toString());
		
		Salutation salutation = new Salutation();
		salutation.setSalutationGenderCode(genderCode);
		salutation.setSystemDefault(systemDefault);
		
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(salutation);
		this.namedParameterJdbcTemplate.update(updateSql.toString(),beanParameters);
		logger.debug("Leaving");
	    
    }

	/**
	 * This method for getting the error details
	 * @param errorId (String)
	 * @param Id (String)
	 * @param userLanguage (String)
	 * @return ErrorDetails
	 */
	private ErrorDetails  getError(String errorId, String salutationCode, String userLanguage){
		String[][] parms= new String[2][1]; 
		parms[1][0] = salutationCode;
		parms[0][0] = PennantJavaUtil.getLabel("label_SalutationCode")+ ":" + parms[1][0];
		return ErrorUtil.getErrorDetail(new ErrorDetails(PennantConstants.KEY_FIELD, errorId, parms[0],parms[1]), userLanguage);
	}


}