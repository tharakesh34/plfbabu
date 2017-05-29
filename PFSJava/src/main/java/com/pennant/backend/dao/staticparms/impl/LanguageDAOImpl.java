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
 * FileName    		:  LanguageDAOImpl.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  27-06-2011    														*
 *                                                                  						*
 * Modified Date    :  27-06-2011    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 27-06-2011       Pennant	                 0.1                                            * 
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

package com.pennant.backend.dao.staticparms.impl;

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
import com.pennant.backend.dao.staticparms.LanguageDAO;
import com.pennant.backend.model.ErrorDetails;
import com.pennant.backend.model.staticparms.Language;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantJavaUtil;

/**
 * DAO methods implementation for the <b>Language model</b> class.<br>
 * 
 */
public class LanguageDAOImpl extends BasisCodeDAO<Language> implements LanguageDAO {

	private static Logger logger = Logger.getLogger(LanguageDAOImpl.class);

	// Spring Named JDBC Template
	private NamedParameterJdbcTemplate namedParameterJdbcTemplate;

	public LanguageDAOImpl() {
		super();
	}

	/**
	 * Fetch the Record Language Details details by key field
	 * 
	 * @param id
	 *            (String)
	 * @param type
	 *            (String) ""/_Temp/_View
	 * @return Language
	 */
	@Override
	public Language getLanguageById(final String id, String type) {
		logger.debug("Entering");
		Language language = new Language();
		language.setId(id);
		StringBuilder selectSql = new StringBuilder();
		
		selectSql.append("Select LngCode, LngDesc, LngNumber,");
		/*if(type.contains("View")){
			selectSql.append("");
		}*/
		selectSql.append(" Version , LastMntBy, LastMntOn, RecordStatus, RoleCode, NextRoleCode, TaskId, NextTaskId, RecordType, WorkflowId");
		selectSql.append(" From BMTLanguage");
		selectSql.append(StringUtils.trimToEmpty(type));
		selectSql.append(" Where LngCode =:LngCode");

		logger.debug("selectSql: " + selectSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(language);
		RowMapper<Language> typeRowMapper = ParameterizedBeanPropertyRowMapper.newInstance(Language.class);

		try {
			language = this.namedParameterJdbcTemplate.queryForObject(selectSql.toString(), beanParameters, typeRowMapper);
		} catch (EmptyResultDataAccessException e) {
			logger.error("Exception: ", e);
			language = null;
		}
		logger.debug("Leaving");
		return language;
	}

	/**
	 * @param dataSource
	 *            the dataSource to set
	 */
	public void setDataSource(DataSource dataSource) {
		this.namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
	}

	/**
	 * This method Deletes the Record from the BMTLanguage or BMTLanguage_Temp.
	 * if Record not deleted then throws DataAccessException with error 41003.
	 * delete Language Details by key LngCode
	 * 
	 * @param Language
	 *            Details (language)
	 * @param type
	 *            (String) ""/_Temp/_View
	 * @return void
	 * @throws DataAccessException
	 * 
	 */
	@SuppressWarnings("serial")
	public void delete(Language language, String type) {
		logger.debug("Entering");
		int recordCount = 0;
		StringBuilder deleteSql = new StringBuilder();
		
		deleteSql.append("Delete From BMTLanguage");
		deleteSql.append(StringUtils.trimToEmpty(type));
		deleteSql.append(" Where LngCode =:LngCode");
		
		logger.debug("deleteSql: "+ deleteSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(language);

		try {
			recordCount = this.namedParameterJdbcTemplate.update(deleteSql.toString(),beanParameters);

			if (recordCount <= 0) {
				ErrorDetails errorDetails = getError("41004",language.getLngCode(), language.getUserDetails().getUsrLanguage());
				throw new DataAccessException(errorDetails.getError()) {
				};
			}
		} catch (DataAccessException e) {
			logger.debug("Error in delete Method");
			logger.error("Exception: ", e);
			ErrorDetails errorDetails = getError("41006",language.getLngCode(), language.getUserDetails().getUsrLanguage());
			throw new DataAccessException(errorDetails.getError()) {
			};
		}
		logger.debug("Leaving");
	}

	/**
	 * This method insert new Records into BMTLanguage or BMTLanguage_Temp.
	 * 
	 * save Language Details
	 * 
	 * @param Language
	 *            Details (language)
	 * @param type
	 *            (String) ""/_Temp/_View
	 * @return void
	 * @throws DataAccessException
	 * 
	 */
	@Override
	public String save(Language language, String type) {
		logger.debug("Entering");
		StringBuilder insertSql = new StringBuilder();
		
		insertSql.append("Insert Into BMTLanguage");
		insertSql.append(StringUtils.trimToEmpty(type));
		insertSql.append(" (LngCode, LngDesc, LngNumber, ");
		insertSql.append(" Version , LastMntBy, LastMntOn, RecordStatus, RoleCode, NextRoleCode, TaskId, NextTaskId, RecordType, WorkflowId)");
		insertSql.append(" Values(:LngCode, :LngDesc, :LngNumber, ");
		insertSql.append(" :Version , :LastMntBy, :LastMntOn, :RecordStatus, :RoleCode, :NextRoleCode, :TaskId, ");
		insertSql.append(" :NextTaskId, :RecordType, :WorkflowId)");
		
		logger.debug("insertSql: "+ insertSql.toString());	
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(language);
		this.namedParameterJdbcTemplate.update(insertSql.toString(), beanParameters);

		logger.debug("Leaving");
		return language.getId();
	}

	/**
	 * This method updates the Record BMTLanguage or BMTLanguage_Temp. if Record
	 * not updated then throws DataAccessException with error 41004. update
	 * Language Details by key LngCode and Version
	 * 
	 * @param Language
	 *            Details (language)
	 * @param type
	 *            (String) ""/_Temp/_View
	 * @return void
	 * @throws DataAccessException
	 * 
	 */
	@SuppressWarnings("serial")
	@Override
	public void update(Language language, String type) {
		int recordCount = 0;
		logger.debug("Entering");
		StringBuilder updateSql = new StringBuilder();
		
		updateSql.append("Update BMTLanguage");
		updateSql.append(StringUtils.trimToEmpty(type));
		updateSql.append(" Set LngDesc = :LngDesc, LngNumber = :LngNumber,");
		updateSql.append(" Version = :Version , LastMntBy = :LastMntBy, LastMntOn = :LastMntOn,  RecordStatus= :RecordStatus, ");
		updateSql.append(" RoleCode = :RoleCode, NextRoleCode = :NextRoleCode, TaskId = :TaskId, NextTaskId = :NextTaskId, ");
		updateSql.append(" RecordType = :RecordType, WorkflowId = :WorkflowId ");
		updateSql.append(" Where LngCode =:LngCode");
		if (!type.endsWith("_Temp")) {
			updateSql.append("  AND Version= :Version-1");
		}
		
		logger.debug("updateSql: "+ updateSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(language);
		recordCount = this.namedParameterJdbcTemplate.update(updateSql.toString(),beanParameters);

		if (recordCount <= 0) {
			logger.debug("Error in Update Method Count :" + recordCount);

			ErrorDetails errorDetails = getError("41003",language.getLngCode(), language.getUserDetails().getUsrLanguage());
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
	private ErrorDetails  getError(String errorId, String lngCode, String userLanguage){
		String[][] parms= new String[2][1]; 
		parms[1][0] = lngCode;
		parms[0][0] = PennantJavaUtil.getLabel("label_LngCode")+ ":" + parms[1][0];
		return ErrorUtil.getErrorDetail(new ErrorDetails(PennantConstants.KEY_FIELD, errorId, parms[0],parms[1]), userLanguage);
	}

}