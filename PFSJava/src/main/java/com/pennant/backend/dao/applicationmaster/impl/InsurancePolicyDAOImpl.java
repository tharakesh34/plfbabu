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
 * FileName    		:  InsurancePolicyDAOImpl.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  06-02-2017    														*
 *                                                                  						*
 * Modified Date    :  06-02-2017    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 06-02-2017       PENNANT	                 0.1                                            * 
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

import com.pennant.backend.dao.applicationmaster.InsurancePolicyDAO;
import com.pennant.backend.dao.impl.BasisCodeDAO;
import com.pennant.backend.model.applicationmaster.InsurancePolicy;
import com.pennanttech.pennapps.core.ConcurrencyException;
import com.pennanttech.pennapps.core.DependencyFoundException;

/**
 * DAO methods implementation for the <b>InsurancePolicy model</b> class.<br>
 * 
 */

public class InsurancePolicyDAOImpl extends BasisCodeDAO<InsurancePolicy> implements InsurancePolicyDAO {

	private static Logger				logger	= Logger.getLogger(InsurancePolicyDAOImpl.class);

	// Spring Named JDBC Template
	private NamedParameterJdbcTemplate	namedParameterJdbcTemplate;

	public InsurancePolicyDAOImpl() {
		super();
	}

	/**
	 * Fetch the Record InsurancePolicy details by key field
	 * 
	 * @param id
	 *            (String)
	 * @param type
	 *            (String) ""/_Temp/_View
	 * @return InsurancePolicy
	 */
	@Override
	public InsurancePolicy getInsurancePolicyById(final String id, String type) {
		logger.debug("Entering");
		InsurancePolicy insurancePolicy = new InsurancePolicy();
		insurancePolicy.setPolicyCode(id);
		StringBuilder sql = new StringBuilder();
		sql.append(" SELECT policyCode,policyDesc,insuranceType,insuranceProvider,policyRate,features,");
		sql.append(" active,");

		if (type.contains("View")) {
			sql.append("InsuranceTypeDesc,TakafulName,");
		}

		sql.append(" Version, LastMntOn, LastMntBy,RecordStatus, RoleCode, NextRoleCode, TaskId, NextTaskId, RecordType, WorkflowId");
		sql.append(" From InsurancePolicy");
		sql.append(StringUtils.trimToEmpty(type));
		sql.append(" Where PolicyCode =:PolicyCode");

		logger.debug("sql: " + sql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(insurancePolicy);
		RowMapper<InsurancePolicy> typeRowMapper = ParameterizedBeanPropertyRowMapper
				.newInstance(InsurancePolicy.class);

		try {
			insurancePolicy = this.namedParameterJdbcTemplate.queryForObject(sql.toString(), beanParameters,
					typeRowMapper);
		} catch (EmptyResultDataAccessException e) {
			logger.error("Exception: ", e);
			insurancePolicy = null;
		}
		logger.debug("Leaving");
		return insurancePolicy;
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
	 * This method Deletes the Record from the InsurancePolicy or InsurancePolicy_Temp. if Record not deleted then
	 * throws DataAccessException with error 41003. delete InsurancePolicy by key InsuranceProvider
	 * 
	 * @param InsurancePolicy
	 *            (insurancePolicy)
	 * @param type
	 *            (String) ""/_Temp/_View
	 * @return void
	 * @throws DataAccessException
	 * 
	 */
	public void delete(InsurancePolicy insurancePolicy, String type) {
		logger.debug("Entering");
		int recordCount = 0;

		StringBuilder sql = new StringBuilder();
		sql.append("Delete From InsurancePolicy");
		sql.append(StringUtils.trimToEmpty(type));
		sql.append(" Where InsuranceProvider =:InsuranceProvider");

		logger.debug("sql: " + sql.toString());

		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(insurancePolicy);
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
	 * This method insert new Records into InsurancePolicy or InsurancePolicy_Temp.
	 * 
	 * save InsurancePolicy
	 * 
	 * @param InsurancePolicy
	 *            (insurancePolicy)
	 * @param type
	 *            (String) ""/_Temp/_View
	 * @return void
	 * @throws DataAccessException
	 * 
	 */

	@Override
	public String save(InsurancePolicy insurancePolicy, String type) {
		logger.debug("Entering");

		StringBuilder sql = new StringBuilder();
		sql.append("Insert Into InsurancePolicy");
		sql.append(StringUtils.trimToEmpty(type));
		sql.append("(policyCode,policyDesc,insuranceType,insuranceProvider,policyRate,features,");
		sql.append(" active,");
		sql.append(" Version , LastMntBy, LastMntOn, RecordStatus, RoleCode, NextRoleCode, TaskId, NextTaskId, RecordType, WorkflowId)");
		sql.append(" Values(");
		sql.append(" :policyCode,:policyDesc,:insuranceType,:insuranceProvider,:policyRate,:features,");
		sql.append(" :active,");
		sql.append(" :Version , :LastMntBy, :LastMntOn, :RecordStatus, :RoleCode, :NextRoleCode, :TaskId, :NextTaskId, :RecordType, :WorkflowId)");

		logger.debug("sql: " + sql.toString());

		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(insurancePolicy);
		this.namedParameterJdbcTemplate.update(sql.toString(), beanParameters);
		logger.debug("Leaving");
		return insurancePolicy.getId();
	}

	/**
	 * This method updates the Record InsurancePolicy or InsurancePolicy_Temp. if Record not updated then throws
	 * DataAccessException with error 41004. update InsurancePolicy by key InsuranceProvider and Version
	 * 
	 * @param InsurancePolicy
	 *            (insurancePolicy)
	 * @param type
	 *            (String) ""/_Temp/_View
	 * @return void
	 * @throws DataAccessException
	 * 
	 */
	@Override
	public void update(InsurancePolicy insurancePolicy, String type) {
		int recordCount = 0;
		logger.debug("Entering");
		StringBuilder updateSql = new StringBuilder();
		updateSql.append("Update InsurancePolicy");
		updateSql.append(StringUtils.trimToEmpty(type));
		updateSql.append("  Set PolicyDesc=:PolicyDesc,InsuranceType=:InsuranceType,");
		updateSql.append(" InsuranceProvider=:InsuranceProvider,PolicyRate=:PolicyRate,Features=:Features, active=:active,");
		updateSql.append(" Version= :Version , LastMntBy=:LastMntBy,");
		updateSql.append(" LastMntOn= :LastMntOn, RecordStatus=:RecordStatus, RoleCode=:RoleCode,");
		updateSql.append(" NextRoleCode= :NextRoleCode, TaskId= :TaskId,");
		updateSql.append(" NextTaskId= :NextTaskId, RecordType= :RecordType, WorkflowId= :WorkflowId");
		updateSql.append(" Where PolicyCode =:PolicyCode");

		if (!type.endsWith("_TEMP")) {
			updateSql.append("  AND Version= :Version-1");
		}

		logger.debug("Sql: " + updateSql.toString());

		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(insurancePolicy);
		recordCount = this.namedParameterJdbcTemplate.update(updateSql.toString(), beanParameters);

		if (recordCount <= 0) {
			throw new ConcurrencyException();
		}
		logger.debug("Leaving");
	}
}