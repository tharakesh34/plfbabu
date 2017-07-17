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
 * FileName    		:  InterestRateTypeDAOImpl.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  06-05-2011    														*
 *                                                                  						*
 * Modified Date    :  06-05-2011    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 06-05-2011       Pennant	                 0.1                                            * 
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
package com.pennant.backend.dao.applicationmaster.impl;

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

import com.pennant.backend.dao.applicationmaster.InterestRateTypeDAO;
import com.pennant.backend.dao.impl.BasisCodeDAO;
import com.pennant.backend.model.applicationmaster.InterestRateType;
import com.pennanttech.pennapps.core.ConcurrencyException;
import com.pennanttech.pennapps.core.DependencyFoundException;

/**
 * DAO methods implementation for the <b>InterestRateType model</b> class.<br>
 * 
 */
public class InterestRateTypeDAOImpl extends BasisCodeDAO<InterestRateType> implements InterestRateTypeDAO {

	private static Logger logger = Logger.getLogger(InterestRateTypeDAOImpl.class);

	// Spring Named JDBC Template
	private NamedParameterJdbcTemplate namedParameterJdbcTemplate;

	public InterestRateTypeDAOImpl() {
		super();
	}


	/**
	 * Fetch the Record Interest Rate Types details by key field
	 * 
	 * @param id
	 *            (String)
	 * @param type
	 *            (String) ""/_Temp/_View
	 * @return InterestRateType
	 */
	@Override
	public InterestRateType getInterestRateTypeById(final String id, String type) {
		logger.debug("Entering");
		InterestRateType interestRateType = new InterestRateType();
		interestRateType.setId(id);
		StringBuilder selectSql = new StringBuilder();

		selectSql.append("Select IntRateTypeCode, IntRateTypeDesc, IntRateTypeIsActive,");
		selectSql.append(" Version , LastMntBy, LastMntOn, RecordStatus, RoleCode, NextRoleCode, TaskId, NextTaskId, RecordType, WorkflowId");
		selectSql.append(" From BMTInterestRateTypes");
		selectSql.append(StringUtils.trimToEmpty(type));
		selectSql.append(" Where IntRateTypeCode =:IntRateTypeCode");

		logger.debug("selectSql: " + selectSql);
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(interestRateType);
		RowMapper<InterestRateType> typeRowMapper = ParameterizedBeanPropertyRowMapper.newInstance(InterestRateType.class);

		try {
			interestRateType = this.namedParameterJdbcTemplate.queryForObject(selectSql.toString(), beanParameters, typeRowMapper);
		} catch (EmptyResultDataAccessException e) {
			logger.error("Exception: ", e);
			interestRateType = null;
		}
		logger.debug("Leaving");
		return interestRateType;
	}

	/**
	 * @param dataSource
	 *            the dataSource to set
	 */
	public void setDataSource(DataSource dataSource) {
		this.namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
	}

	/**
	 * This method Deletes the Record from the BMTInterestRateTypes or
	 * BMTInterestRateTypes_Temp. if Record not deleted then throws
	 * DataAccessException with error 41003. delete Interest Rate Types by key
	 * IntRateTypeCode
	 * 
	 * @param Interest
	 *            Rate Types (interestRateType)
	 * @param type
	 *            (String) ""/_Temp/_View
	 * @return void
	 * @throws DataAccessException
	 * 
	 */
	@Override
	public void delete(InterestRateType interestRateType, String type) {
		logger.debug("Entering");
		int recordCount = 0;
		StringBuilder deleteSql = new StringBuilder();

		deleteSql.append(" Delete From BMTInterestRateTypes");
		deleteSql.append(StringUtils.trimToEmpty(type));
		deleteSql.append(" Where IntRateTypeCode =:IntRateTypeCode");
		
		logger.debug("deleteSql: "+ deleteSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(interestRateType);

		try {
			recordCount = this.namedParameterJdbcTemplate.update(deleteSql.toString(),beanParameters);

			if (recordCount <= 0) {
				throw new ConcurrencyException();
			}
		} catch (DataAccessException e) {
			throw new DependencyFoundException(e);
		}
		logger.debug("Leaving");
	}

	/**
	 * This method insert new Records into BMTInterestRateTypes or
	 * BMTInterestRateTypes_Temp.
	 * 
	 * save Interest Rate Types
	 * 
	 * @param Interest
	 *            Rate Types (interestRateType)
	 * @param type
	 *            (String) ""/_Temp/_View
	 * @return void
	 * @throws DataAccessException
	 * 
	 */
	@Override
	public String save(InterestRateType interestRateType, String type) {
		logger.debug("Entering");
		StringBuilder insertSql = new StringBuilder();

		insertSql.append("Insert Into BMTInterestRateTypes");
		insertSql.append(StringUtils.trimToEmpty(type));
		insertSql.append(" (IntRateTypeCode, IntRateTypeDesc, IntRateTypeIsActive,");
		insertSql.append(" Version , LastMntBy, LastMntOn, RecordStatus, RoleCode, NextRoleCode, TaskId, NextTaskId," );
		insertSql.append(" RecordType, WorkflowId)");
		insertSql.append(" Values(:IntRateTypeCode, :IntRateTypeDesc, :IntRateTypeIsActive,");
		insertSql.append(" :Version , :LastMntBy, :LastMntOn, :RecordStatus, :RoleCode, :NextRoleCode, :TaskId, :NextTaskId, ");
		insertSql.append(" :RecordType, :WorkflowId)");
		
		logger.debug("insertSql: "+ insertSql.toString());		
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(interestRateType);
		this.namedParameterJdbcTemplate.update(insertSql.toString(), beanParameters);

		logger.debug("Leaving");
		return interestRateType.getId();
	}

	/**
	 * This method updates the Record BMTInterestRateTypes or
	 * BMTInterestRateTypes_Temp. if Record not updated then throws
	 * DataAccessException with error 41004. update Interest Rate Types by key
	 * IntRateTypeCode and Version
	 * 
	 * @param Interest
	 *            Rate Types (interestRateType)
	 * @param type
	 *            (String) ""/_Temp/_View
	 * @return void
	 * @throws DataAccessException
	 * 
	 */
	@Override
	public void update(InterestRateType interestRateType, String type) {
		logger.debug("Entering");
		int recordCount = 0;
		StringBuilder updateSql = new StringBuilder();

		updateSql.append("Update BMTInterestRateTypes");
		updateSql.append(StringUtils.trimToEmpty(type));
		updateSql.append(" Set IntRateTypeDesc = :IntRateTypeDesc,");
		updateSql.append(" IntRateTypeIsActive = :IntRateTypeIsActive,");
		updateSql.append(" Version = :Version , LastMntBy = :LastMntBy, LastMntOn = :LastMntOn, RecordStatus= :RecordStatus,");
		updateSql.append(" RoleCode = :RoleCode, NextRoleCode = :NextRoleCode, TaskId = :TaskId, NextTaskId = :NextTaskId,");
		updateSql.append(" RecordType = :RecordType, WorkflowId = :WorkflowId");
		updateSql.append(" Where IntRateTypeCode =:IntRateTypeCode");
		if (!type.endsWith("_Temp")) {
			updateSql.append("  AND Version= :Version-1");
		}

		logger.debug("updateSql: "+ updateSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(interestRateType);
		recordCount = this.namedParameterJdbcTemplate.update(updateSql.toString(),beanParameters);

		if (recordCount <= 0) {
			throw new ConcurrencyException();
		}
		logger.debug("Leaving");
	}
}