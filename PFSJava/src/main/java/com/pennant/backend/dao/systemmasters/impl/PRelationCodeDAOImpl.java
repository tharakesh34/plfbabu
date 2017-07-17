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
 * FileName    		:  PRelationCodeDAOImpl.java                                                   * 	  
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

import com.pennant.backend.dao.impl.BasisCodeDAO;
import com.pennant.backend.dao.systemmasters.PRelationCodeDAO;
import com.pennant.backend.model.systemmasters.PRelationCode;
import com.pennanttech.pennapps.core.ConcurrencyException;
import com.pennanttech.pff.core.DependencyFoundException;

/**
 * DAO methods implementation for the <b>PRelationCode model</b> class.<br>
 * 
 */
public class PRelationCodeDAOImpl extends BasisCodeDAO<PRelationCode> implements PRelationCodeDAO {

	private static Logger logger = Logger.getLogger(PRelationCodeDAOImpl.class);

	// Spring Named JDBC Template
	private NamedParameterJdbcTemplate namedParameterJdbcTemplate;

	public PRelationCodeDAOImpl() {
		super();
	}
	
	/**
	 * Fetch the Record Personal Relation Codes details by key field
	 * 
	 * @param id
	 *            (String)
	 * @param type
	 *            (String) ""/_Temp/_View
	 * @return PRelationCode
	 */
	@Override
	public PRelationCode getPRelationCodeById(final String id, String type) {
		logger.debug("Entering");
		PRelationCode pRelationCode = new PRelationCode();
		pRelationCode.setId(id);
		StringBuilder selectSql = new StringBuilder();

		selectSql.append("Select PRelationCode, PRelationDesc, RelationCodeIsActive,");
		selectSql.append(" Version , LastMntBy, LastMntOn, RecordStatus, RoleCode, NextRoleCode, TaskId, NextTaskId, RecordType, WorkflowId");
		selectSql.append(" From BMTPRelationCodes");
		selectSql.append(StringUtils.trimToEmpty(type));
		selectSql.append(" Where PRelationCode =:PRelationCode");

		logger.debug("selectSql: " + selectSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(pRelationCode);
		RowMapper<PRelationCode> typeRowMapper = ParameterizedBeanPropertyRowMapper.newInstance(PRelationCode.class);

		try {
			pRelationCode = this.namedParameterJdbcTemplate.queryForObject(selectSql.toString(), beanParameters, typeRowMapper);
		} catch (EmptyResultDataAccessException e) {
			logger.error("Exception: ", e);
			pRelationCode = null;
		}
		logger.debug("Leaving");
		return pRelationCode;
	}

	/**
	 * @param dataSource
	 *            the dataSource to set
	 */
	public void setDataSource(DataSource dataSource) {
		this.namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
	}

	/**
	 * This method Deletes the Record from the BMTPRelationCodes or
	 * BMTPRelationCodes_Temp. if Record not deleted then throws
	 * DataAccessException with error 41003. delete Personal Relation Codes by
	 * key PRelationCode
	 * 
	 * @param Persional
	 *            Relation Codes (pRelationCode)
	 * @param type
	 *            (String) ""/_Temp/_View
	 * @return void
	 * @throws DataAccessException
	 * 
	 */
	@Override
	public void delete(PRelationCode pRelationCode, String type) {
		logger.debug("Entering");
		int recordCount = 0;
		StringBuilder deleteSql = new StringBuilder();

		deleteSql.append(" Delete From BMTPRelationCodes");
		deleteSql.append(StringUtils.trimToEmpty(type));
		deleteSql.append(" Where PRelationCode =:PRelationCode");
		
		logger.debug("deleteSql: "+ deleteSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(pRelationCode);

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
	 * This method insert new Records into BMTPRelationCodes or
	 * BMTPRelationCodes_Temp.
	 * 
	 * save Personal Relation Codes
	 * 
	 * @param Persional
	 *            Relation Codes (pRelationCode)
	 * @param type
	 *            (String) ""/_Temp/_View
	 * @return void
	 * @throws DataAccessException
	 * 
	 */
	@Override
	public String save(PRelationCode pRelationCode, String type) {
		logger.debug("Entering");
		StringBuilder insertSql = new StringBuilder();

		insertSql.append("Insert Into BMTPRelationCodes");
		insertSql.append(StringUtils.trimToEmpty(type));
		insertSql.append(" (PRelationCode, PRelationDesc, RelationCodeIsActive,");
		insertSql.append(" Version , LastMntBy, LastMntOn, RecordStatus, RoleCode, NextRoleCode, TaskId, NextTaskId," );
		insertSql.append(" RecordType, WorkflowId)");
		insertSql.append(" Values(:PRelationCode, :PRelationDesc, :RelationCodeIsActive,");
		insertSql.append(" :Version , :LastMntBy, :LastMntOn, :RecordStatus, :RoleCode, :NextRoleCode, :TaskId, :NextTaskId, ");
		insertSql.append(" :RecordType, :WorkflowId)");
		
		logger.debug("insertSql: "+ insertSql.toString());		  
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(pRelationCode);
		this.namedParameterJdbcTemplate.update(insertSql.toString(), beanParameters);

		logger.debug("Leaving");
		return pRelationCode.getId();
	}

	/**
	 * This method updates the Record BMTPRelationCodes or
	 * BMTPRelationCodes_Temp. if Record not updated then throws
	 * DataAccessException with error 41004. update Personal Relation Codes by
	 * key PRelationCode and Version
	 * 
	 * @param Persional
	 *            Relation Codes (pRelationCode)
	 * @param type
	 *            (String) ""/_Temp/_View
	 * @return void
	 * @throws DataAccessException
	 * 
	 */
	@Override
	public void update(PRelationCode pRelationCode, String type) {
		logger.debug("Entering");
		int recordCount = 0;
		StringBuilder updateSql = new StringBuilder();

		updateSql.append("Update BMTPRelationCodes");
		updateSql.append(StringUtils.trimToEmpty(type));
		updateSql.append(" Set PRelationDesc = :PRelationDesc,");
		updateSql.append(" RelationCodeIsActive = :RelationCodeIsActive,");
		updateSql.append(" Version = :Version , LastMntBy = :LastMntBy, LastMntOn = :LastMntOn, RecordStatus= :RecordStatus,");
		updateSql.append(" RoleCode = :RoleCode, NextRoleCode = :NextRoleCode, TaskId = :TaskId, NextTaskId = :NextTaskId,");
		updateSql.append(" RecordType = :RecordType, WorkflowId = :WorkflowId");
		updateSql.append(" Where PRelationCode =:PRelationCode");
		if (!type.endsWith("_Temp")) {
			updateSql.append(" AND Version= :Version-1");
		}

		logger.debug("updateSql: "+ updateSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(pRelationCode);
		recordCount = this.namedParameterJdbcTemplate.update(updateSql.toString(),beanParameters);

		if (recordCount <= 0) {
			throw new ConcurrencyException();
		}
		logger.debug("Leaving");
	}

}