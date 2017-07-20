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
 * FileName    		:  CollateralStructureDAOImpl.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  29-11-2016    														*
 *                                                                  						*
 * Modified Date    :  29-11-2016    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 29-11-2016       PENNANT	                 0.1                                            * 
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

package com.pennant.backend.dao.collateral.impl;

import javax.sql.DataSource;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.core.simple.ParameterizedBeanPropertyRowMapper;

import com.pennant.backend.dao.collateral.CollateralStructureDAO;
import com.pennant.backend.dao.impl.BasisCodeDAO;
import com.pennant.backend.model.collateral.CollateralStructure;
import com.pennanttech.pennapps.core.ConcurrencyException;
import com.pennanttech.pennapps.core.DependencyFoundException;

/**
 * DAO methods implementation for the <b>CollateralStructure model</b> class.<br>
 * 
 */

public class CollateralStructureDAOImpl extends BasisCodeDAO<CollateralStructure> implements CollateralStructureDAO {

	private static Logger logger = Logger.getLogger(CollateralStructureDAOImpl.class);

	// Spring Named JDBC Template
	private NamedParameterJdbcTemplate jdbcTemplate;

	public void setDataSource(DataSource dataSource) {
		this.jdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
	}
	
	public CollateralStructureDAOImpl() {
		super();
	}
	
	/**
	 * Fetch the Record CollateralStructure details by key field
	 * 
	 * @param id
	 *            (String)
	 * @param type
	 *            (String) ""/_Temp/_View
	 * @return CollateralStructure
	 */
	@Override
	public CollateralStructure getCollateralStructureByType(String collateralType, String type) {
		logger.debug("Entering");

		MapSqlParameterSource source = null;
		StringBuilder sql = new StringBuilder();

		sql.append(" SELECT  CollateralType, CollateralDesc, LtvType, LtvPercentage, MarketableSecurities, PreValidationReq,");
		sql.append(" PostValidationReq, CollateralLocReq, CollateralValuatorReq, Remarks, AllowLtvWaiver, MaxLtvWaiver,PostValidation,PreValidation, Active,");
		sql.append(" Fields, ActualBlock, SQLRule, ");
		if (type.contains("View")) {
			sql.append("");
		}
		sql.append(" Version, LastMntOn, LastMntBy,RecordStatus, RoleCode, NextRoleCode, TaskId, NextTaskId, RecordType, WorkflowId");
		sql.append(" From CollateralStructure");
		sql.append(StringUtils.trimToEmpty(type));
		sql.append(" Where CollateralType = :CollateralType");
		logger.debug("SelectSql: " + sql.toString());

		source = new MapSqlParameterSource();
		source.addValue("CollateralType", collateralType);

		RowMapper<CollateralStructure> typeRowMapper = ParameterizedBeanPropertyRowMapper.newInstance(CollateralStructure.class);
		CollateralStructure collateralStructure = null;
		try {
			collateralStructure = this.jdbcTemplate.queryForObject(sql.toString(), source, typeRowMapper);
		} catch (EmptyResultDataAccessException e) {
			logger.error("Exception :", e);
			collateralStructure = null;
		}
		logger.debug("Leaving");
		return collateralStructure;
	}

	/**
	 * This method Deletes the Record from the CollateralStructure or
	 * CollateralStructure_Temp. if Record not deleted then throws
	 * DataAccessException with error 41003. delete CollateralStructure by key
	 * CollateralType
	 * 
	 * @param CollateralStructure
	 *            (collateralStructure)
	 * @param type
	 *            (String) ""/_Temp/_View
	 * @return void
	 * @throws DataAccessException
	 * 
	 */
	@Override
	public void delete(CollateralStructure collateralStructure, String type) {
		logger.debug("Entering");

		StringBuilder sql = new StringBuilder("Delete From CollateralStructure");
		sql.append(StringUtils.trimToEmpty(type));
		sql.append(" Where CollateralType = :CollateralType");

		logger.debug("sql: " + sql.toString());

		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(collateralStructure);
		try {
			if (this.jdbcTemplate.update(sql.toString(), beanParameters) <= 0) {
				throw new ConcurrencyException();
			}
		} catch (DataAccessException e) {
			throw new DependencyFoundException(e);
		}
		logger.debug("Leaving");
	}

	/**
	 * This method insert new Records into CollateralStructure or
	 * CollateralStructure_Temp.
	 * 
	 * save CollateralStructure
	 * 
	 * @param CollateralStructure
	 *            (collateralStructure)
	 * @param type
	 *            (String) ""/_Temp/_View
	 * @return void
	 * @throws DataAccessException
	 * 
	 */

	@Override
	public String save(CollateralStructure collateralStructure, String type) {
		logger.debug("Entering");

		StringBuilder sql = new StringBuilder();
		sql.append("Insert Into CollateralStructure");
		sql.append(StringUtils.trimToEmpty(type));
		sql.append(" (CollateralType, CollateralDesc, LtvType, LtvPercentage, MarketableSecurities, PreValidationReq,");
		sql.append(" PostValidationReq, CollateralLocReq, CollateralValuatorReq, Remarks, AllowLtvWaiver, MaxLtvWaiver, PostValidation, PreValidation, Active,");
		sql.append(" Fields, ActualBlock, SQLRule, ");
		sql.append(" Version , LastMntBy, LastMntOn, RecordStatus, RoleCode, NextRoleCode, TaskId, NextTaskId, RecordType, WorkflowId)");
		sql.append(" Values(");
		sql.append(" :CollateralType, :CollateralDesc, :LtvType, :LtvPercentage, :MarketableSecurities, :PreValidationReq,");
		sql.append(" :PostValidationReq, :CollateralLocReq, :CollateralValuatorReq, :Remarks, :AllowLtvWaiver, :MaxLtvWaiver, :PostValidation, :PreValidation, :Active,");
		sql.append(" :Fields, :ActualBlock, :SQLRule, ");
		sql.append(" :Version , :LastMntBy, :LastMntOn, :RecordStatus, :RoleCode, :NextRoleCode, :TaskId, :NextTaskId, :RecordType, :WorkflowId)");

		logger.debug("InsertSql: " + sql.toString());

		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(collateralStructure);
		this.jdbcTemplate.update(sql.toString(), beanParameters);
		logger.debug("Leaving");
		return collateralStructure.getCollateralType();
	}

	/**
	 * This method updates the Record CollateralStructure or
	 * CollateralStructure_Temp. if Record not updated then throws
	 * DataAccessException with error 41004. update CollateralStructure by key
	 * CollateralType and Version
	 * 
	 * @param CollateralStructure
	 *            (collateralStructure)
	 * @param type
	 *            (String) ""/_Temp/_View
	 * @return void
	 * @throws DataAccessException
	 * 
	 */
	@Override
	public void update(CollateralStructure collateralStructure, String type) {
		int recordCount = 0;
		logger.debug("Entering");
		
		StringBuilder sql = new StringBuilder("Update CollateralStructure");
		sql.append(StringUtils.trimToEmpty(type));
		sql.append(" Set collateralDesc = :collateralDesc, ltvType = :ltvType,");
		sql.append(" ltvPercentage = :ltvPercentage, marketableSecurities = :marketableSecurities, preValidationReq = :preValidationReq,");
		sql.append(" postValidationReq = :postValidationReq, collateralLocReq = :collateralLocReq, collateralValuatorReq = :collateralValuatorReq,");
		sql.append(" remarks = :remarks, allowLtvWaiver = :allowLtvWaiver, maxLtvWaiver = :maxLtvWaiver,PostValidation = :PostValidation,PreValidation = :PreValidation, Active = :Active, ");
		sql.append(" Fields = :Fields, ActualBlock = :ActualBlock, SQLRule = :SQLRule, ");
		sql.append(" Version = :Version , LastMntBy = :LastMntBy, LastMntOn = :LastMntOn, RecordStatus = :RecordStatus, RoleCode = :RoleCode,");
		sql.append(" NextRoleCode = :NextRoleCode, TaskId = :TaskId, NextTaskId = :NextTaskId, RecordType = :RecordType, WorkflowId = :WorkflowId");
		sql.append(" Where CollateralType = :CollateralType");

		logger.debug("Sql: " + sql.toString());

		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(collateralStructure);
		recordCount = this.jdbcTemplate.update(sql.toString(), beanParameters);

		if (recordCount <= 0) {
			throw new ConcurrencyException();
		}
		logger.debug("Leaving");
	}
}