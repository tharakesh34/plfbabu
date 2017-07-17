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
 * FileName    		:  CollateralSetupDAOImpl.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  13-12-2016    														*
 *                                                                  						*
 * Modified Date    :  13-12-2016    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 13-12-2016       PENNANT	                 0.1                                            * 
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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

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

import com.pennant.backend.dao.collateral.CollateralSetupDAO;
import com.pennant.backend.dao.impl.BasisCodeDAO;
import com.pennant.backend.model.collateral.CollateralSetup;
import com.pennanttech.pennapps.core.ConcurrencyException;
import com.pennanttech.pennapps.core.DependencyFoundException;

/**
 * DAO methods implementation for the <b>CollateralSetup model</b> class.<br>
 * 
 */

public class CollateralSetupDAOImpl extends BasisCodeDAO<CollateralSetup> implements CollateralSetupDAO {

	private static Logger logger	= Logger.getLogger(CollateralSetupDAOImpl.class);

	private NamedParameterJdbcTemplate	jdbcTemplate;

	/**
	 * Fetch the Record CollateralSetup details by key field
	 * 
	 * @param id
	 *            (String)
	 * @param type
	 *            (String) ""/_Temp/_View
	 * @return CollateralSetup
	 */
	@Override
	public CollateralSetup getCollateralSetupByRef(String collateralRef, String type) {
		logger.debug("Entering");

		MapSqlParameterSource source = null;

		StringBuilder sql = new StringBuilder();
		sql.append(" SELECT CollateralRef, DepositorId, CollateralType, CollateralCcy, MaxCollateralValue, SpecialLTV,");
		sql.append(" CollateralLoc, Valuator, ExpiryDate, ReviewFrequency, NextReviewDate, MultiLoanAssignment,");
		sql.append(" ThirdPartyAssignment, Remarks, CollateralValue, BankLTV, BankValuation, ");
		if (StringUtils.containsIgnoreCase(type, "View")) {
			sql.append("CollateralType, DepositorCif, DepositorName, CollateralTypeName, ");
		}
		sql.append(" Version, LastMntOn, LastMntBy, RecordStatus, RoleCode, NextRoleCode, TaskId, NextTaskId, RecordType, WorkflowId");
		sql.append(" CreatedBy, CreatedOn  From CollateralSetup");
		sql.append(StringUtils.trimToEmpty(type));
		sql.append(" Where CollateralRef = :CollateralRef");
		logger.debug("sql: " + sql.toString());

		source = new MapSqlParameterSource();
		source.addValue("CollateralRef", collateralRef);

		RowMapper<CollateralSetup> typeRowMapper = ParameterizedBeanPropertyRowMapper.newInstance(CollateralSetup.class);
		try {
			return this.jdbcTemplate.queryForObject(sql.toString(), source, typeRowMapper);
		} catch (EmptyResultDataAccessException e) {
			logger.error("Exception: ", e);
		}
		logger.debug("Leaving");
		return null;
	}

	/**
	 * To Set dataSource
	 * 
	 * @param dataSource
	 */

	public void setDataSource(DataSource dataSource) {
		this.jdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
	}

	/**
	 * This method Deletes the Record from the CollateralDetail or CollateralDetail_Temp. if Record not deleted then
	 * throws DataAccessException with error 41003. delete CollateralSetup by key CollateralRef
	 * 
	 * @param CollateralSetup
	 *            (collateralSetup)
	 * @param type
	 *            (String) ""/_Temp/_View
	 * @return void
	 * @throws DataAccessException
	 * 
	 */
	@Override
	public void delete(CollateralSetup collateralSetup, String type) {
		logger.debug("Entering");

		StringBuilder sql = new StringBuilder("Delete From CollateralSetup");
		sql.append(StringUtils.trimToEmpty(type));
		sql.append(" Where CollateralRef = :CollateralRef");
		logger.debug("sql: " + sql.toString());

		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(collateralSetup);
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
	 * This method insert new Records into CollateralDetail or CollateralDetail_Temp.
	 * 
	 * save CollateralSetup
	 * 
	 * @param CollateralSetup
	 *            (collateralSetup)
	 * @param type
	 *            (String) ""/_Temp/_View
	 * @return void
	 * @throws DataAccessException
	 * 
	 */

	@Override
	public String save(CollateralSetup collateralSetup, String type) {
		logger.debug("Entering");

		StringBuilder sql = new StringBuilder();
		sql.append("Insert Into CollateralSetup");
		sql.append(StringUtils.trimToEmpty(type));
		sql.append(" (collateralRef,depositorId,collateralType,collateralCcy,maxCollateralValue,specialLTV,");
		sql.append(" collateralLoc,valuator,expiryDate,reviewFrequency,nextReviewDate,multiLoanAssignment,");
		sql.append(" thirdPartyAssignment,remarks,CollateralValue, BankLTV, BankValuation, Version , LastMntBy, LastMntOn,");
		sql.append(" RecordStatus, RoleCode, NextRoleCode, TaskId, NextTaskId, RecordType, WorkflowId, CreatedBy, CreatedOn)");
		sql.append(" Values(");
		sql.append(" :collateralRef,:depositorId,:collateralType,:collateralCcy,:maxCollateralValue,:specialLTV,");
		sql.append(" :collateralLoc,:valuator,:expiryDate,:reviewFrequency,:nextReviewDate,:multiLoanAssignment,");
		sql.append(" :thirdPartyAssignment,:remarks,:CollateralValue, :BankLTV, :BankValuation, :Version , :LastMntBy, :LastMntOn,");
		sql.append(" :RecordStatus, :RoleCode, :NextRoleCode, :TaskId, :NextTaskId, :RecordType, :WorkflowId, :CreatedBy, :CreatedOn)");

		logger.debug("sql: " + sql.toString());

		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(collateralSetup);
		this.jdbcTemplate.update(sql.toString(), beanParameters);
		logger.debug("Leaving");
		return collateralSetup.getId();
	}

	/**
	 * This method updates the Record CollateralDetail or CollateralDetail_Temp. if Record not updated then throws
	 * DataAccessException with error 41004. update CollateralSetup by key CollateralRef and Version
	 * 
	 * @param CollateralSetup
	 *            (collateralSetup)
	 * @param type
	 *            (String) ""/_Temp/_View
	 * @return void
	 * @throws DataAccessException
	 * 
	 */
	@Override
	public void update(CollateralSetup collateralSetup, String type) {
		int recordCount = 0;
		logger.debug("Entering");
		StringBuilder sql = new StringBuilder("Update CollateralSetup");
		sql.append(StringUtils.trimToEmpty(type));
		sql.append(" Set depositorId=:depositorId, collateralType=:collateralType,");
		sql.append(" collateralCcy=:collateralCcy, maxCollateralValue=:maxCollateralValue, specialLTV=:specialLTV,");
		sql.append(" collateralLoc=:collateralLoc, valuator=:valuator, expiryDate=:expiryDate,");
		sql.append(" reviewFrequency=:reviewFrequency, nextReviewDate=:nextReviewDate, multiLoanAssignment=:multiLoanAssignment,");
		sql.append(" thirdPartyAssignment=:thirdPartyAssignment, remarks=:remarks,CollateralValue=:CollateralValue, BankLTV=:BankLTV, ");
		sql.append(" BankValuation=:BankValuation, Version=:Version, LastMntBy=:LastMntBy, LastMntOn=:LastMntOn, RecordStatus=:RecordStatus, ");
		sql.append(" RoleCode=:RoleCode, NextRoleCode=:NextRoleCode,TaskId=:TaskId, NextTaskId=:NextTaskId, RecordType=:RecordType, WorkflowId=:WorkflowId");
		sql.append(" Where CollateralRef = :CollateralRef");
		logger.debug("Sql: " + sql.toString());

		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(collateralSetup);
		recordCount = this.jdbcTemplate.update(sql.toString(), beanParameters);

		if (recordCount <= 0) {
			throw new ConcurrencyException();
		}
		logger.debug("Leaving");
	}
	
	@Override
	public boolean isCollReferenceExists(String generatedSeqNo, String type) {
		logger.debug("Entering");
		MapSqlParameterSource source = null;
		StringBuilder sql = null;

		sql = new StringBuilder();
		sql.append(" Select Count(*) from CollateralSetup");
		sql.append(StringUtils.trimToEmpty(type));
		sql.append(" Where CollateralRef = :CollateralRef");
		logger.debug("Sql: " + sql.toString());

		source = new MapSqlParameterSource();
		source.addValue("CollateralRef", generatedSeqNo);
		try {
			if (this.jdbcTemplate.queryForObject(sql.toString(), source, Integer.class) > 0) {
				return true;
			}
		} catch (Exception e) {
			logger.error(e);
		} finally {
			source = null;
			sql = null;
			logger.debug("Leaving");
		}
		return false;
	}

	@Override
	public boolean updateCollReferene(long oldReference, long newReference) {
		logger.debug("Entering");

		MapSqlParameterSource source = null;
		StringBuilder sql = null;

		sql = new StringBuilder();
		sql.append(" UPDATE  SeqCollateralSetup  SET Seqno = :newReference Where Seqno = :oldReference");
		logger.debug("Sql: " + sql.toString());

		source = new MapSqlParameterSource();
		source.addValue("newReference", newReference);
		source.addValue("oldReference", oldReference);

		try {
			if (this.jdbcTemplate.update(sql.toString(), source) == 1) {
				return true;
			}
		} catch (Exception e) {
			logger.error("Exception: ", e);
			source = null;
			sql = null;
		}
		logger.debug("Leaving");
		return false;
	}
	
	/**
	 * Get latest version of collateral setup.
	 * 
	 * @param collateralRef
	 * @param tableType
	 * @return Integer
	 */
	@Override
	public int getVersion(String collateralRef, String tableType) {
		logger.debug("Entering");

		MapSqlParameterSource source = new MapSqlParameterSource();
		source.addValue("CollateralRef", collateralRef);

		StringBuffer selectSql = new StringBuffer();
		selectSql.append("SELECT Version FROM CollateralSetup");
		selectSql.append(tableType);
		selectSql.append(" WHERE CollateralRef = :CollateralRef");

		logger.debug("selectSql: " + selectSql.toString());

		int recordCount = 0;
		try {
			recordCount = this.jdbcTemplate.queryForObject(selectSql.toString(), source, Integer.class);
		} catch (EmptyResultDataAccessException dae) {
			logger.info(dae);
			recordCount = 0;
		}
		logger.debug("Leaving");
		return recordCount;
	}

	/**
	 * Method for get collateral count by reference.
	 * 
	 * @param collateralRef
	 * @param tableType
	 * @return Integer
	 */
	@Override
	public int getCollateralCountByref(String collateralRef, String tableType) {
		logger.debug("Entering");
		
		MapSqlParameterSource source = new MapSqlParameterSource();
		source.addValue("CollateralRef", collateralRef);

		StringBuffer selectSql = new StringBuffer();
		selectSql.append("SELECT COUNT(*) FROM CollateralSetup");
		selectSql.append(tableType);
		selectSql.append(" WHERE CollateralRef = :CollateralRef");

		logger.debug("selectSql: " + selectSql.toString());

		int recordCount = 0;
		try {
			recordCount = this.jdbcTemplate.queryForObject(selectSql.toString(), source, Integer.class);
		} catch (EmptyResultDataAccessException dae) {
			logger.info(dae);
			recordCount = 0;
		}
		logger.debug("Leaving");
		return recordCount;
	}

	/**
	 * Fetch collateral setup details by collateral reference and depositorId.
	 * 
	 * @param collateralRef
	 * @param depositorId
	 * @return CollateralSetup
	 */
	@Override
	public CollateralSetup getCollateralSetup(String collateralRef, long depositorId, String type) {
		logger.debug("Entering");

		MapSqlParameterSource source = new MapSqlParameterSource();
		source.addValue("CollateralRef", collateralRef);
		source.addValue("DepositorId", depositorId);

		StringBuilder selectSql = new StringBuilder();
		selectSql.append(" SELECT collateralRef, depositorId, collateralType, collateralCcy, maxCollateralValue,");
		selectSql.append(" specialLTV, collateralLoc, valuator, expiryDate, reviewFrequency, nextReviewDate,");
		selectSql.append(" multiLoanAssignment, thirdPartyAssignment, remarks,CollateralValue, BankLTV, BankValuation,");
		if (StringUtils.containsIgnoreCase(type, "View")) {
			selectSql.append("collateralType, depositorCif, depositorName, CollateralTypeName, ");
		}
		selectSql.append(" Version, LastMntOn, LastMntBy,RecordStatus, RoleCode, NextRoleCode, TaskId, NextTaskId, RecordType, WorkflowId, CreatedBy, CreatedOn");
		selectSql.append(" From CollateralSetup");
		selectSql.append(StringUtils.trimToEmpty(type));
		selectSql.append(" Where CollateralRef = :CollateralRef AND DepositorId = :DepositorId");

		logger.debug("selectSql: " + selectSql.toString());
		RowMapper<CollateralSetup> typeRowMapper = ParameterizedBeanPropertyRowMapper.newInstance(CollateralSetup.class);

		CollateralSetup collateralSetup = null;
		try {
			collateralSetup = this.jdbcTemplate.queryForObject(selectSql.toString(), source, typeRowMapper);
		} catch (EmptyResultDataAccessException e) {
			logger.error("Exception: ", e);
			collateralSetup = null;
		}

		logger.debug("Leaving");
		return collateralSetup;
	}

	/**
	 * Fetch list of customer collateral setup details.
	 * 
	 * @param depositorId
	 * @param tableType
	 * @return List<CollateralSetup>
	 */
	@Override
	public List<CollateralSetup> getApprovedCollateralByCustId(long depositorId, String type) {
		logger.debug("Entering");

		MapSqlParameterSource source = new MapSqlParameterSource();
		source.addValue("DepositorId", depositorId);

		StringBuilder sql = new StringBuilder();
		sql.append(" SELECT collateralRef, depositorId, collateralType, collateralCcy, maxCollateralValue, specialLTV,");
		sql.append(" collateralLoc, valuator, expiryDate, reviewFrequency, nextReviewDate, multiLoanAssignment,");
		sql.append(" thirdPartyAssignment,remarks,CollateralValue, BankLTV, BankValuation,");
		if (StringUtils.containsIgnoreCase(type, "View")) {
			sql.append("collateralType, depositorCif, depositorName, CollateralTypeName, ");
		}
		sql.append(" Version, LastMntOn, LastMntBy, RecordStatus, RoleCode, NextRoleCode, TaskId, NextTaskId, RecordType, WorkflowId");
		sql.append(" CreatedBy, CreatedOn From CollateralSetup");
		sql.append(StringUtils.trimToEmpty(type));
		sql.append(" Where DepositorId = :DepositorId");
		logger.debug("sql: " + sql.toString());

		RowMapper<CollateralSetup> typeRowMapper = ParameterizedBeanPropertyRowMapper.newInstance(CollateralSetup.class);

		List<CollateralSetup> collaterals = new ArrayList<CollateralSetup>();
		try {
			collaterals = this.jdbcTemplate.query(sql.toString(), source, typeRowMapper);
		} catch (EmptyResultDataAccessException e) {
			logger.error("Exception: ", e);
			return Collections.emptyList();
		}
		logger.debug("Leaving");
		return collaterals;
	}
	/**
	 * Method for get collateral count by reference.
	 * 
	 * @param collateralRef
	 * @return Integer
	 */

	@Override
	public int getCountByCollateralRef(String collateralRef) {
		logger.debug("Entering");

		MapSqlParameterSource source = new MapSqlParameterSource();
		source.addValue("CollateralRef", collateralRef);

		StringBuffer selectSql = new StringBuffer();
		selectSql.append("SELECT Count(*) FROM CollateralSetup");
		selectSql.append(" WHERE CollateralRef = :CollateralRef");
		logger.debug("selectSql: " + selectSql.toString());

		int recordCount = 0;
		try {
			recordCount = this.jdbcTemplate.queryForObject(selectSql.toString(), source, Integer.class);
		} catch (EmptyResultDataAccessException dae) {
			logger.info(dae);
			recordCount = 0;
		}
		logger.debug("Leaving");
		return recordCount;
	}
}