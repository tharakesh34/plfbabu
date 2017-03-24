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
 * FileName    		:  IndustryDAOImpl.java                                                   * 	  
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
import com.pennant.backend.dao.systemmasters.IndustryDAO;
import com.pennant.backend.model.ErrorDetails;
import com.pennant.backend.model.systemmasters.Industry;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantJavaUtil;

/**
 * DAO methods implementation for the <b>Industry model</b> class.<br>
 * 
 */
public class IndustryDAOImpl extends BasisCodeDAO<Industry> implements IndustryDAO {

	private static Logger logger = Logger.getLogger(IndustryDAOImpl.class);

	// Spring Named JDBC Template
	private NamedParameterJdbcTemplate namedParameterJdbcTemplate;

	public IndustryDAOImpl() {
		super();
	}

	/**
	 * Fetch the Record Industries details by key field
	 * 
	 * @param id
	 *            (String)
	 * @param type
	 *            (String) ""/_Temp/_View
	 * @return Industry
	 */
	@Override
	public Industry getIndustryById(final String id, String type) {
		logger.debug("Entering");
		Industry industry = new Industry();
		industry.setId(id);
		StringBuilder selectSql = new StringBuilder();
		
		selectSql.append("Select IndustryCode, SubSectorCode, IndustryDesc, IndustryIsActive,");
		if(type.contains("View")){
			selectSql.append("lovDescSubSectorCodeName,");
		}
		selectSql.append(" Version, LastMntBy, LastMntOn, RecordStatus, RoleCode, NextRoleCode, TaskId,");
		selectSql.append(" NextTaskId, RecordType, WorkflowId");
		selectSql.append(" From BMTIndustries");
		selectSql.append(StringUtils.trimToEmpty(type));
		selectSql.append(" Where IndustryCode =:IndustryCode");

		logger.debug("selectSql: " + selectSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(industry);
		RowMapper<Industry> typeRowMapper = ParameterizedBeanPropertyRowMapper.newInstance(Industry.class);

		try {
			industry = this.namedParameterJdbcTemplate.queryForObject(selectSql.toString(), beanParameters, typeRowMapper);
		} catch (EmptyResultDataAccessException e) {
			logger.warn("Exception: ", e);
			industry = null;
		}
		logger.debug("Leaving");
		return industry;
	}

	/**
	 * @param dataSource
	 *            the dataSource to set
	 */
	public void setDataSource(DataSource dataSource) {
		this.namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
	}

	/**
	 * This method Deletes the Record from the BMTIndustries or
	 * BMTIndustries_Temp. if Record not deleted then throws DataAccessException
	 * with error 41003. delete Industries by key IndustryCode
	 * 
	 * @param Industries
	 *            (industry)
	 * @param type
	 *            (String) ""/_Temp/_View
	 * @return void
	 * @throws DataAccessException
	 * 
	 */
	@SuppressWarnings("serial")
	public void delete(Industry industry, String type) {
		logger.debug("Entering");
		int recordCount = 0;
		StringBuilder deleteSql = new StringBuilder();
		
		deleteSql.append("Delete From BMTIndustries");
		deleteSql.append(StringUtils.trimToEmpty(type));
		deleteSql.append(" Where IndustryCode =:IndustryCode");
		
		logger.debug("deleteSql: "+ deleteSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(industry);

		try {
			recordCount = this.namedParameterJdbcTemplate.update(deleteSql.toString(),beanParameters);

			if (recordCount <= 0) {
				ErrorDetails errorDetails = getError("41004",industry.getIndustryCode(),industry.getSubSectorCode(),
					industry.getUserDetails().getUsrLanguage());
				throw new DataAccessException(errorDetails.getError()) {
				};
			}
		} catch (DataAccessException e) {
			logger.debug("Error in delete Method");
			logger.error("Exception: ", e);
			ErrorDetails errorDetails = getError("41006",industry.getIndustryCode(),industry.getSubSectorCode(),
					industry.getUserDetails().getUsrLanguage());
			throw new DataAccessException(errorDetails.getError()) {
			};
		}
		logger.debug("Leaving");
	}

	/**
	 * This method insert new Records into BMTIndustries or BMTIndustries_Temp.
	 * 
	 * save Industries
	 * 
	 * @param Industries
	 *            (industry)
	 * @param type
	 *            (String) ""/_Temp/_View
	 * @return void
	 * @throws DataAccessException
	 * 
	 */
	@Override
	public void save(Industry industry, String type) {
		logger.debug("Entering");
		StringBuilder insertSql = new StringBuilder();
		
		insertSql.append("Insert Into BMTIndustries");
		insertSql.append(StringUtils.trimToEmpty(type));
		insertSql.append(" (IndustryCode, SubSectorCode, IndustryDesc, IndustryIsActive,");
		insertSql.append("  Version , LastMntBy, LastMntOn, RecordStatus, RoleCode, NextRoleCode, TaskId, NextTaskId,");
		insertSql.append(" RecordType, WorkflowId)");
		insertSql.append(" Values(:IndustryCode, :SubSectorCode, :IndustryDesc, :IndustryIsActive,");
		insertSql.append(" :Version , :LastMntBy, :LastMntOn, :RecordStatus, :RoleCode, :NextRoleCode, :TaskId, :NextTaskId,");
		insertSql.append(" :RecordType, :WorkflowId)");
		
		logger.debug("insertSql: "+ insertSql.toString());		
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(industry);
		this.namedParameterJdbcTemplate.update(insertSql.toString(), beanParameters);
		logger.debug("Leaving");
	}

	/**
	 * This method updates the Record BMTIndustries or BMTIndustries_Temp. if
	 * Record not updated then throws DataAccessException with error 41004.
	 * update Industries by key IndustryCode and Version
	 * 
	 * @param Industries
	 *            (industry)
	 * @param type
	 *            (String) ""/_Temp/_View
	 * @return void
	 * @throws DataAccessException
	 * 
	 */
	@SuppressWarnings("serial")
	@Override
	public void update(Industry industry, String type) {
		logger.debug("Entering");
		int recordCount = 0;
		StringBuilder updateSql = new StringBuilder();
		
		updateSql.append("Update BMTIndustries");
		updateSql.append(StringUtils.trimToEmpty(type));
		updateSql.append(" Set IndustryCode = :IndustryCode, SubSectorCode = :SubSectorCode, IndustryDesc = :IndustryDesc,");
		updateSql.append(" IndustryIsActive = :IndustryIsActive,");
		updateSql.append(" Version = :Version , LastMntBy = :LastMntBy, LastMntOn = :LastMntOn, RecordStatus= :RecordStatus,");
		updateSql.append(" RoleCode = :RoleCode, NextRoleCode = :NextRoleCode, TaskId = :TaskId, NextTaskId = :NextTaskId,");
		updateSql.append(" RecordType = :RecordType, WorkflowId = :WorkflowId ");
		updateSql.append(" Where IndustryCode =:IndustryCode");
		if (!type.endsWith("_Temp")) {
			updateSql.append("  AND Version= :Version-1");
		}

		logger.debug("updateSql: "+ updateSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(industry);
		recordCount = this.namedParameterJdbcTemplate.update(updateSql.toString(),beanParameters);

		if (recordCount <= 0) {
			logger.debug("Error in Update Method Count :" + recordCount);
			ErrorDetails errorDetails = getError("41003",industry.getIndustryCode(),industry.getSubSectorCode(),
					industry.getUserDetails().getUsrLanguage());
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
	private ErrorDetails  getError(String errorId, String industryCode, String subSectorCode, String userLanguage){
		String[][] parms= new String[1][1]; 
		parms[1][0] = industryCode;
		//parms[1][1] = subSectorCode;

		parms[0][0] = PennantJavaUtil.getLabel("label_IndustryCode")+ ":" + parms[1][0];
		//parms[0][1] = PennantJavaUtil.getLabel("label_Industry_SubSectorCode")+ ":" + parms[1][1];
		return ErrorUtil.getErrorDetail(new ErrorDetails(PennantConstants.KEY_FIELD, errorId, parms[0],parms[1]), userLanguage);
	}

}