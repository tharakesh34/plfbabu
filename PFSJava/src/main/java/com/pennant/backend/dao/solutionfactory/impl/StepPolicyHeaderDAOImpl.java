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
 * FileName    		:  StepPolicyHeaderDAOImpl.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  30-06-2011    														*
 *                                                                  						*
 * Modified Date    :  30-06-2011    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 30-06-2011       Pennant	                 0.1                                            * 
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

package com.pennant.backend.dao.solutionfactory.impl;


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

import com.pennant.backend.dao.impl.BasisCodeDAO;
import com.pennant.backend.dao.solutionfactory.StepPolicyHeaderDAO;
import com.pennant.backend.model.solutionfactory.StepPolicyHeader;
import com.pennanttech.pennapps.core.ConcurrencyException;
import com.pennanttech.pff.core.DependencyFoundException;

/**
 * DAO methods implementation for the <b>StepPolicyHeader model</b> class.<br>
 * 
 */
public class StepPolicyHeaderDAOImpl extends BasisCodeDAO<StepPolicyHeader> implements StepPolicyHeaderDAO {

	private static Logger logger = Logger.getLogger(StepPolicyHeaderDAOImpl.class);

	// Spring Named JDBC Template
	private NamedParameterJdbcTemplate	namedParameterJdbcTemplate;

	public StepPolicyHeaderDAOImpl() {
		super();
	}

	/**
	 * Fetch the Record Step Policy Header details by key field
	 * 
	 * @param id
	 *         (String)
	 * @param type
	 *         (String) ""/_Temp/_View
	 * @return StepPolicyHeader
	 */
	@Override
	public StepPolicyHeader getStepPolicyHeaderByID(final String id, String type) {
		logger.debug("Entering");
		StepPolicyHeader stepPolicyHeader = new StepPolicyHeader();
		stepPolicyHeader.setId(id);

		StringBuilder selectSql = new StringBuilder("SELECT PolicyCode,PolicyDesc,StepType,");
		selectSql.append(" Version, LastMntBy, LastMntOn, RecordStatus,");
		selectSql.append(" RoleCode, NextRoleCode, TaskId, NextTaskId, RecordType, WorkflowId");
		selectSql.append(" FROM StepPolicyHeader");
		selectSql.append(StringUtils.trimToEmpty(type));
		selectSql.append(" Where PolicyCode = :PolicyCode");

		logger.debug("selectListSql: " + selectSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(stepPolicyHeader);
		RowMapper<StepPolicyHeader> typeRowMapper = ParameterizedBeanPropertyRowMapper.newInstance(StepPolicyHeader.class);

		try {
			stepPolicyHeader = this.namedParameterJdbcTemplate.queryForObject(selectSql.toString(), beanParameters, typeRowMapper);
		} catch (EmptyResultDataAccessException e) {
			logger.warn("Exception: ", e);
			stepPolicyHeader = null;
		}
		logger.debug("Leaving");
		return stepPolicyHeader;
	}

	/**
	 * @param dataSource
	 *         the dataSource to set
	 */
	public void setDataSource(DataSource dataSource) {
		this.namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
	}

	/**
	 * This method Deletes the Record from the RMTStepPolicyHeaders or RMTStepPolicyHeaders_Temp. if Record not
	 * deleted then throws DataAccessException with error 41003. delete Finance Types by key FinType
	 * 
	 * @param Finance
	 *         Types (stepPolicyHeader)
	 * @param type
	 *         (String) ""/_Temp/_View
	 * @return void
	 * @throws DataAccessException
	 * 
	 */
	@Override
	public void delete(StepPolicyHeader stepPolicyHeader, String type) {
		logger.debug("Entering");
		int recordCount = 0;
		
		StringBuilder deleteSql = new StringBuilder("Delete From StepPolicyHeader");
		deleteSql.append(StringUtils.trimToEmpty(type));
		deleteSql.append(" Where PolicyCode =:PolicyCode");
		
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(stepPolicyHeader);

		try {
			recordCount = this.namedParameterJdbcTemplate.update(deleteSql.toString(), beanParameters);

			if (recordCount <= 0) {
				throw new ConcurrencyException();
			}
		} catch (DataAccessException e) {
			throw new DependencyFoundException(e);
		}
		logger.debug("Leaving");
	}

	/**
	 * This method insert new Records into RMTStepPolicyHeaders or RMTStepPolicyHeaders_Temp.
	 * 
	 * save Finance Types
	 * 
	 * @param Finance
	 *         Types (stepPolicyHeader)
	 * @param type
	 *         (String) ""/_Temp/_View
	 * @return void
	 * @throws DataAccessException
	 * 
	 */
	@Override
	public String save(StepPolicyHeader stepPolicyHeader, String type) {
		logger.debug("Entering");
		StringBuilder insertSql = new StringBuilder("Insert Into StepPolicyHeader");
		insertSql.append(StringUtils.trimToEmpty(type));
		insertSql.append("(PolicyCode, PolicyDesc,StepType,Version , LastMntBy, LastMntOn, RecordStatus,"); 
		insertSql.append(" RoleCode, NextRoleCode, TaskId, NextTaskId,RecordType, WorkflowId)");
		insertSql.append(" Values(:PolicyCode, :PolicyDesc, :StepType, :Version , :LastMntBy, :LastMntOn, :RecordStatus, ");
		insertSql.append(" :RoleCode, :NextRoleCode, :TaskId, :NextTaskId, :RecordType, :WorkflowId)");
		
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(stepPolicyHeader);
		this.namedParameterJdbcTemplate.update(insertSql.toString(), beanParameters);

		logger.debug("Leaving");
		return stepPolicyHeader.getId();
	}

	/**
	 * This method updates the Record RMTStepPolicyHeaders or RMTStepPolicyHeaders_Temp. if Record not updated
	 * then throws DataAccessException with error 41004. update Finance Types by key FinType and
	 * Version
	 * 
	 * @param Finance
	 *         Types (stepPolicyHeader)
	 * @param type
	 *         (String) ""/_Temp/_View
	 * @return void
	 * @throws DataAccessException
	 * 
	 */
	@Override
	public void update(StepPolicyHeader stepPolicyHeader, String type) {
		int recordCount = 0;
		logger.debug("Entering");
		StringBuilder updateSql = new StringBuilder("Update StepPolicyHeader");
		updateSql.append(StringUtils.trimToEmpty(type));
		updateSql.append(" Set PolicyDesc = :PolicyDesc, StepType = :StepType,");
		updateSql.append(" Version = :Version , LastMntBy = :LastMntBy, LastMntOn = :LastMntOn, " );
		updateSql.append(" RecordStatus= :RecordStatus, RoleCode = :RoleCode,NextRoleCode = :NextRoleCode, TaskId = :TaskId," );
		updateSql.append(" NextTaskId = :NextTaskId, RecordType = :RecordType, WorkflowId = :WorkflowId" );
		updateSql.append(" Where PolicyCode =:PolicyCode");
		if (!type.endsWith("_Temp")) {
			updateSql.append(" AND Version= :Version-1");
		}

		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(stepPolicyHeader);
		recordCount = this.namedParameterJdbcTemplate.update(updateSql.toString(), beanParameters);

		if (recordCount <= 0) {
			throw new ConcurrencyException();
		}
		logger.debug("Leaving");
	}
	
}