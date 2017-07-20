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
 * FileName    		:  InsuranceTypeDAOImpl.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  19-12-2016    														*
 *                                                                  						*
 * Modified Date    :  19-12-2016    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 19-12-2016       PENNANT	                 0.1                                            * 
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

import java.util.List;

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

import com.pennant.backend.dao.applicationmaster.InsuranceTypeDAO;
import com.pennant.backend.dao.impl.BasisCodeDAO;
import com.pennant.backend.model.applicationmaster.InsuranceType;
import com.pennant.backend.model.applicationmaster.InsuranceTypeProvider;
import com.pennanttech.pennapps.core.ConcurrencyException;
import com.pennanttech.pennapps.core.DependencyFoundException;

/**
 * DAO methods implementation for the <b>InsuranceType model</b> class.<br>
 * 
 */
public class InsuranceTypeDAOImpl extends BasisCodeDAO<InsuranceType> implements InsuranceTypeDAO {
	private static Logger				logger	= Logger.getLogger(InsuranceTypeDAOImpl.class);

	// Spring Named JDBC Template
	private NamedParameterJdbcTemplate	namedParameterJdbcTemplate;
	
	public InsuranceTypeDAOImpl() {
		super();
	}

	/**
	 * Fetch the Record InsuranceType details by key field
	 * 
	 * @param id
	 *            (String)
	 * @param type
	 *            (String) ""/_Temp/_View
	 * @return InsuranceType
	 */
	@Override
	public InsuranceType getInsuranceTypeById(final String id, String type) {
		logger.debug("Entering");
		InsuranceType insuranceType = new InsuranceType();
		insuranceType.setId(id);
		StringBuilder sql = new StringBuilder("SELECT ");
		sql.append(" insuranceType,insuranceTypeDesc,");

		if (type.contains("View")) {
			sql.append("");
		}

		sql.append(" Version, LastMntOn, LastMntBy,RecordStatus, RoleCode, NextRoleCode, TaskId, NextTaskId, RecordType, WorkflowId");
		sql.append(" From InsuranceType");
		sql.append(StringUtils.trimToEmpty(type));
		sql.append(" Where InsuranceType =:InsuranceType");

		logger.debug("sql: " + sql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(insuranceType);
		RowMapper<InsuranceType> typeRowMapper = ParameterizedBeanPropertyRowMapper.newInstance(InsuranceType.class);

		try {
			insuranceType = this.namedParameterJdbcTemplate.queryForObject(sql.toString(), beanParameters,
					typeRowMapper);
		} catch (EmptyResultDataAccessException e) {
			logger.warn("Exception: ", e);
			insuranceType = null;
		}
		logger.debug("Leaving");
		return insuranceType;
	}

	@Override
	public List<InsuranceTypeProvider> getProvidersByInstype(String insurancetype, String type) {
		logger.debug("Entering");

		InsuranceTypeProvider provider = new InsuranceTypeProvider();
		provider.setInsuranceType(insurancetype);

		StringBuilder selectSql = new StringBuilder(" Select InsuranceType,ProviderCode,ProviderName,InsuranceRate, ");
		selectSql.append(" Version , LastMntBy, LastMntOn, RecordStatus, RoleCode, NextRoleCode, TaskId,");
		selectSql.append(" NextTaskId, RecordType, WorkflowId");
		selectSql.append(" From InsuranceTypeProvider");

		selectSql.append(StringUtils.trimToEmpty(type));
		selectSql.append(" Where InsuranceType =:InsuranceType");

		logger.debug("selectSql: " + selectSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(provider);
		RowMapper<InsuranceTypeProvider> typeRowMapper = ParameterizedBeanPropertyRowMapper
				.newInstance(InsuranceTypeProvider.class);
		logger.debug("Leaving");
		return this.namedParameterJdbcTemplate.query(selectSql.toString(), beanParameters, typeRowMapper);
	}

	/**
	 * To Set dataSource
	 * 
	 * @param dataSource
	 */

	public void setDataSource(DataSource dataSource) {
		this.namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
	}

	/**
	 * This method Deletes the Record from the InsuranceType or InsuranceType_Temp. if Record not deleted then throws
	 * DataAccessException with error 41003. delete InsuranceType by key InsuranceType
	 * 
	 * @param InsuranceType
	 *            (insuranceType)
	 * @param type
	 *            (String) ""/_Temp/_View
	 * @return void
	 * @throws DataAccessException
	 * 
	 */
	public void delete(InsuranceType insuranceType, String type) {
		logger.debug("Entering");
		int recordCount = 0;

		StringBuilder sql = new StringBuilder("Delete From InsuranceType");
		sql.append(StringUtils.trimToEmpty(type));
		sql.append(" Where InsuranceType =:InsuranceType");

		logger.debug("sql: " + sql.toString());

		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(insuranceType);
		try {
			recordCount = this.namedParameterJdbcTemplate.update(sql.toString(), beanParameters);
			if (recordCount <= 0) {
				throw new ConcurrencyException();
			}
		} catch (DataAccessException e) {
			throw new DependencyFoundException(e);
		}
		logger.debug("Leaving");
	}

	/**
	 * This method insert new Records into InsuranceType or InsuranceType_Temp.
	 * 
	 * save InsuranceType
	 * 
	 * @param InsuranceType
	 *            (insuranceType)
	 * @param type
	 *            (String) ""/_Temp/_View
	 * @return void
	 * @throws DataAccessException
	 * 
	 */

	@Override
	public String save(InsuranceType insuranceType, String type) {
		logger.debug("Entering");

		StringBuilder sql = new StringBuilder("Insert Into InsuranceType");
		sql.append(StringUtils.trimToEmpty(type));
		sql.append(" (insuranceType,insuranceTypeDesc,");
		sql.append(" Version , LastMntBy, LastMntOn, RecordStatus, RoleCode, NextRoleCode, TaskId, NextTaskId, RecordType, WorkflowId)");
		sql.append(" Values(");
		sql.append(" :insuranceType,:insuranceTypeDesc,");
		sql.append(" :Version , :LastMntBy, :LastMntOn, :RecordStatus, :RoleCode, :NextRoleCode, :TaskId, :NextTaskId, :RecordType, :WorkflowId)");

		logger.debug("sql: " + sql.toString());

		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(insuranceType);
		this.namedParameterJdbcTemplate.update(sql.toString(), beanParameters);
		logger.debug("Leaving");
		return insuranceType.getId();
	}

	/**
	 * This method updates the Record InsuranceType or InsuranceType_Temp. if Record not updated then throws
	 * DataAccessException with error 41004. update InsuranceType by key InsuranceType and Version
	 * 
	 * @param InsuranceType
	 *            (insuranceType)
	 * @param type
	 *            (String) ""/_Temp/_View
	 * @return void
	 * @throws DataAccessException
	 * 
	 */
	@Override
	public void update(InsuranceType insuranceType, String type) {
		int recordCount = 0;
		logger.debug("Entering");
		StringBuilder updateSql = new StringBuilder("Update InsuranceType");
		updateSql.append(StringUtils.trimToEmpty(type));
		updateSql.append(" Set InsuranceTypeDesc = :InsuranceTypeDesc,");
		updateSql.append(" Version= :Version , LastMntBy=:LastMntBy,");
		updateSql.append(" LastMntOn= :LastMntOn, RecordStatus=:RecordStatus, RoleCode=:RoleCode,");
		updateSql.append(" NextRoleCode= :NextRoleCode, TaskId= :TaskId,");
		updateSql.append(" NextTaskId= :NextTaskId, RecordType= :RecordType, WorkflowId= :WorkflowId");
		updateSql.append(" Where InsuranceType =:InsuranceType");

		if (!type.endsWith("_Temp")) {
			updateSql.append(" AND Version= :Version-1");
		}

		logger.debug("updateSql: " + updateSql.toString());

		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(insuranceType);
		recordCount = this.namedParameterJdbcTemplate.update(updateSql.toString(), beanParameters);

		if (recordCount <= 0) {
			throw new ConcurrencyException();
		}
		logger.debug("Leaving");

	}

	@Override
	public InsuranceTypeProvider getInsTypeProvider(String insType, String providerCode, String type) {
		logger.debug("Entering");
		InsuranceTypeProvider provider = new InsuranceTypeProvider();
		provider.setInsuranceType(insType);
		provider.setProviderCode(providerCode);

		StringBuilder selectSql = new StringBuilder(" Select InsuranceType,ProviderCode, ");
		selectSql.append(" Version,LastMntBy, LastMntOn, RecordStatus, RoleCode, NextRoleCode, TaskId,");
		selectSql.append(" NextTaskId, RecordType, WorkflowId");
		selectSql.append(" From InsuranceTypeProvider");

		selectSql.append(StringUtils.trimToEmpty(type));
		selectSql.append(" Where InsuranceType =:InsuranceType");
		selectSql.append(" AND ProviderCode =:ProviderCode");

		logger.debug("selectSql: " + selectSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(provider);
		RowMapper<InsuranceTypeProvider> typeRowMapper = ParameterizedBeanPropertyRowMapper
				.newInstance(InsuranceTypeProvider.class);

		try {
			provider = this.namedParameterJdbcTemplate.queryForObject(selectSql.toString(), beanParameters,
					typeRowMapper);
		} catch (EmptyResultDataAccessException e) {
			logger.warn("Exception: ", e);
			provider = null;
		}
		logger.debug("Leaving");
		return provider;
	}

	@Override
	public void deleteList(String insuranceType, String type) {
		logger.debug("Entering");
		InsuranceTypeProvider insProvider = new InsuranceTypeProvider();
		insProvider.setInsuranceType(insuranceType);
		StringBuilder deleteSql = new StringBuilder("Delete From ");
		deleteSql.append(" InsuranceTypeProvider");
		deleteSql.append(StringUtils.trimToEmpty(type));
		deleteSql.append(" Where InsuranceType =:InsuranceType");
		logger.debug("deleteSql: " + deleteSql.toString());

		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(insProvider);
		this.namedParameterJdbcTemplate.update(deleteSql.toString(), beanParameters);
		logger.debug("Leaving");

	}

	@Override
	public void deleteByCode(String insType, String type) {
		logger.debug("Entering");
		InsuranceTypeProvider insuranceTypeProvider = new InsuranceTypeProvider();
		insuranceTypeProvider.setInsuranceType(insType);
		StringBuilder deleteSql = new StringBuilder("Delete From ");
		deleteSql.append(" InsuranceTypeProvider");
		deleteSql.append(StringUtils.trimToEmpty(type));
		deleteSql.append(" Where InsuranceType =:InsuranceType");
		logger.debug("deleteSql: " + deleteSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(insuranceTypeProvider);
		this.namedParameterJdbcTemplate.update(deleteSql.toString(), beanParameters);
		logger.debug("Leaving");

	}

	@Override
	public void saveList(InsuranceTypeProvider insTypeProvider, String type) {

		logger.debug("Entering");

		StringBuilder insertSql = new StringBuilder("Insert Into ");
		insertSql.append(" InsuranceTypeProvider");
		insertSql.append(StringUtils.trimToEmpty(type));
		insertSql.append(" (InsuranceType,ProviderCode,");
		insertSql
				.append(" Version,LastMntBy,LastMntOn,RecordStatus,RoleCode,NextRoleCode,TaskId,NextTaskId,RecordType,WorkflowId)");
		insertSql.append(" values (:InsuranceType,:ProviderCode, ");
		insertSql.append(" :Version,:LastMntBy,:LastMntOn,:RecordStatus,:RoleCode,:NextRoleCode,:TaskId,");

		insertSql.append(" :NextTaskId,:RecordType,:WorkflowId)");

		logger.debug("insertSql: " + insertSql.toString());

		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(insTypeProvider);
		this.namedParameterJdbcTemplate.update(insertSql.toString(), beanParameters);
		logger.debug("Leaving");

	}

	@Override
	public void delete(InsuranceTypeProvider insTypeProvider, String type) {

		logger.debug("Entering");
		int recordCount = 0;

		StringBuilder deleteSql = new StringBuilder("Delete From InsuranceTypeProvider");
		deleteSql.append(StringUtils.trimToEmpty(type));
		deleteSql.append("  Where  InsuranceType =:InsuranceType AND ProviderCode= :ProviderCode");
		logger.debug("deleteSql: " + deleteSql.toString());

		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(insTypeProvider);
		try {
			recordCount = this.namedParameterJdbcTemplate.update(deleteSql.toString(), beanParameters);
			if (recordCount < 0) {
				throw new ConcurrencyException();
			}
		} catch (DataAccessException e) {
			throw new DependencyFoundException(e);
		}
		logger.debug("Leaving");

	}
	
	@Override
	public List<InsuranceTypeProvider> getInsuranceType(String providerCode, String type) {
		logger.debug("Entering");
		InsuranceTypeProvider provider = new InsuranceTypeProvider();
		provider.setProviderCode(providerCode);

		StringBuilder selectSql = new StringBuilder(" Select InsuranceType,ProviderCode, ");
		selectSql.append(" Version,LastMntBy, LastMntOn, RecordStatus, RoleCode, NextRoleCode, TaskId,");
		selectSql.append(" NextTaskId, RecordType, WorkflowId");
		selectSql.append(" From InsuranceTypeProvider");

		selectSql.append(StringUtils.trimToEmpty(type));
		selectSql.append(" Where ProviderCode =:ProviderCode");

		logger.debug("selectSql: " + selectSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(provider);
		RowMapper<InsuranceTypeProvider> typeRowMapper = ParameterizedBeanPropertyRowMapper
				.newInstance(InsuranceTypeProvider.class);

		logger.debug("Leaving");
		return this.namedParameterJdbcTemplate.query(selectSql.toString(), beanParameters, typeRowMapper);
	}
}