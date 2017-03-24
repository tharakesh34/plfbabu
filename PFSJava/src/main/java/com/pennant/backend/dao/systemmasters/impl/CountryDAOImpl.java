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
 * FileName    		:  CountryDAOImpl.java                                                   * 	  
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
import com.pennant.backend.dao.smtmasters.CountryDAO;
import com.pennant.backend.model.ErrorDetails;
import com.pennant.backend.model.systemmasters.Country;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantJavaUtil;

/**
 * DAO methods implementation for the <b>Country model</b> class.<br>
 * 
 */
public class CountryDAOImpl extends BasisCodeDAO<Country> implements CountryDAO {

	private static Logger logger = Logger.getLogger(CountryDAOImpl.class);

	// Spring Named JDBC Template
	private NamedParameterJdbcTemplate namedParameterJdbcTemplate;
	
	public CountryDAOImpl() {
		super();
	}
	
	/**
	 * Fetch the Record Country details by key field
	 * 
	 * @param id
	 *            (String)
	 * @param type
	 *            (String) ""/_Temp/_View
	 * @return Country
	 */
	@Override
	public Country getCountryById(final String id, String type) {
		logger.debug("Entering");
		Country country = new Country();
		country.setId(id);
		StringBuilder selectSql = new StringBuilder();
		
		selectSql.append("SELECT CountryCode, CountryDesc, CountryParentLimit, CountryResidenceLimit,CountryRiskLimit, CountryIsActive,SystemDefault," );
		selectSql.append(" Version, LastMntOn, LastMntBy,RecordStatus, RoleCode, NextRoleCode, TaskId, NextTaskId, RecordType, WorkflowId" );
		selectSql.append(" FROM  BMTCountries");
		selectSql.append(StringUtils.trimToEmpty(type));
		selectSql.append(" Where CountryCode =:CountryCode") ;

		logger.debug("selectSql: " + selectSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(country);
		RowMapper<Country> typeRowMapper = ParameterizedBeanPropertyRowMapper.newInstance(Country.class);

		try {
			country = this.namedParameterJdbcTemplate.queryForObject(selectSql.toString(), beanParameters, typeRowMapper);
		} catch (EmptyResultDataAccessException e) {
			logger.warn("Exception: ", e);
			country = null;
		}
		logger.debug("Leaving");
		return country;
	}

	/**
	 * @param dataSource
	 *            the dataSource to set
	 */
	public void setDataSource(DataSource dataSource) {
		this.namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
	}

	/**
	 * This method Deletes the Record from the BMTCountries or
	 * BMTCountries_Temp. if Record not deleted then throws DataAccessException
	 * with error 41003. delete Country by key CountryCode
	 * 
	 * @param Country
	 *            (country)
	 * @param type
	 *            (String) ""/_Temp/_View
	 * @return void
	 * @throws DataAccessException
	 * 
	 */
	@SuppressWarnings("serial")
	public void delete(Country country, String type) {
		logger.debug("Entering");
		int recordCount = 0;
		StringBuilder deleteSql = new StringBuilder();
		
		deleteSql.append("Delete From BMTCountries");
		deleteSql.append(StringUtils.trimToEmpty(type));
		deleteSql.append(" Where CountryCode =:CountryCode");
		
		logger.debug("deleteSql: "+ deleteSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(country);

		try {
			recordCount = this.namedParameterJdbcTemplate.update(deleteSql.toString(),beanParameters);

			if (recordCount <= 0) {
				ErrorDetails errorDetails= getError("41004", country.getCountryCode(), country.getUserDetails().getUsrLanguage());
				throw new DataAccessException(errorDetails.getError()) {
				};
			}
		} catch (DataAccessException e) {
			logger.debug("Error in delete Method");
			logger.error("Exception: ", e);
			ErrorDetails errorDetails= getError("41006", country.getCountryCode(), country.getUserDetails().getUsrLanguage());
			throw new DataAccessException(errorDetails.getError()) {
			};
		}
		logger.debug("Leaving");
	}

	/**
	 * This method insert new Records into BMTCountries or BMTCountries_Temp.
	 * 
	 * save Country
	 * 
	 * @param Country
	 *            (country)
	 * @param type
	 *            (String) ""/_Temp/_View
	 * @return void
	 * @throws DataAccessException
	 * 
	 */
	@Override
	public String save(Country country, String type) {
		logger.debug("Entering");
		StringBuilder insertSql = new StringBuilder();
		
		insertSql.append("Insert Into BMTCountries");
		insertSql.append(StringUtils.trimToEmpty(type));
		insertSql.append(" (CountryCode, CountryDesc, CountryParentLimit, CountryResidenceLimit,");
		insertSql.append(" CountryRiskLimit, CountryIsActive,SystemDefault,");
		insertSql.append(" Version , LastMntBy, LastMntOn, RecordStatus, RoleCode, NextRoleCode, TaskId, NextTaskId," );
		insertSql.append(" RecordType, WorkflowId)");
		insertSql.append(" Values(:CountryCode, :CountryDesc, :CountryParentLimit, :CountryResidenceLimit,");
		insertSql.append(" :CountryRiskLimit, :CountryIsActive,:SystemDefault,");
		insertSql.append(" :Version , :LastMntBy, :LastMntOn, :RecordStatus, :RoleCode, :NextRoleCode, :TaskId, :NextTaskId,");
		insertSql.append(" :RecordType, :WorkflowId)");

		logger.debug("insertSql: "+ insertSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(country);
		this.namedParameterJdbcTemplate.update(insertSql.toString(), beanParameters);

		logger.debug("Leaving");
		return country.getId();
	}

	/**
	 * This method updates the Record BMTCountries or BMTCountries_Temp. if
	 * Record not updated then throws DataAccessException with error 41004.
	 * update Country by key CountryCode and Version
	 * 
	 * @param Country
	 *            (country)
	 * @param type
	 *            (String) ""/_Temp/_View
	 * @return void
	 * @throws DataAccessException
	 * 
	 */
	@SuppressWarnings("serial")
	@Override
	public void update(Country country, String type) {
		int recordCount = 0;
		logger.debug("Entering");
		StringBuilder updateSql = new StringBuilder();
		
		updateSql.append("Update BMTCountries");
		updateSql.append(StringUtils.trimToEmpty(type));
		updateSql.append(" Set CountryCode = :CountryCode, CountryDesc = :CountryDesc, CountryParentLimit = :CountryParentLimit,");
		updateSql.append(" CountryResidenceLimit = :CountryResidenceLimit, CountryRiskLimit = :CountryRiskLimit,");
		updateSql.append(" CountryIsActive = :CountryIsActive, SystemDefault = :SystemDefault,");
		updateSql.append(" Version = :Version , LastMntBy = :LastMntBy, LastMntOn = :LastMntOn,");
		updateSql.append(" RecordStatus= :RecordStatus, RoleCode = :RoleCode,NextRoleCode = :NextRoleCode, TaskId = :TaskId,");
		updateSql.append(" NextTaskId = :NextTaskId, RecordType = :RecordType, WorkflowId = :WorkflowId" );
		updateSql.append(" Where CountryCode =:CountryCode ");
		if (!type.endsWith("_Temp")){
			updateSql.append(" AND Version= :Version-1");
		}

		logger.debug("updateSql: "+ updateSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(country);
		recordCount = this.namedParameterJdbcTemplate.update(updateSql.toString(),	beanParameters);

		if (recordCount <= 0) {
			logger.debug("Error in Update Method Count :" + recordCount);

			ErrorDetails errorDetails= getError("41003", country.getCountryCode(), country.getUserDetails().getUsrLanguage());
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
	public String getSystemDefaultCount(String countryCode) {
		logger.debug("Entering");
		Country country = new Country();
		country.setCountryCode(countryCode);
		country.setSystemDefault(true);

		StringBuilder selectSql = new StringBuilder();

		selectSql.append("SELECT CountryCode FROM  BMTCountries_View ");
		selectSql.append(" Where CountryCode != :CountryCode and SystemDefault = :SystemDefault");

		logger.debug("selectSql: " + selectSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(country);
		String dftCountryCode = "";
		try {
			dftCountryCode = this.namedParameterJdbcTemplate.queryForObject(selectSql.toString(), beanParameters, String.class);
        } catch (EmptyResultDataAccessException e) {
        	logger.warn("Exception: ", e);
        	dftCountryCode = "";
        }

		logger.debug("Leaving");
		return dftCountryCode;
	}
	
	/**
	 * This method for getting the error details
	 * @param errorId (String)
	 * @param Id (String)
	 * @param userLanguage (String)
	 * @return ErrorDetails
	 */
	private ErrorDetails  getError(String errorId, String countryCode,String userLanguage){
		String[][] parms= new String[2][2]; 
		parms[1][0] = countryCode;

		parms[0][0] = PennantJavaUtil.getLabel("label_CountryCode")+ ":" + parms[1][0];
		return ErrorUtil.getErrorDetail(new ErrorDetails(PennantConstants.KEY_FIELD, errorId, parms[0],parms[1]), userLanguage);
	}
}