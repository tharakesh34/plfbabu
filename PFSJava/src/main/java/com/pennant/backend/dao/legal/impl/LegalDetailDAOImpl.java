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
 * FileName    		:  LegalDetailDAOImpl.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  16-06-2018    														*
 *                                                                  						*
 * Modified Date    :  16-06-2018    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 16-06-2018       PENNANT	                 0.1                                            * 
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
package com.pennant.backend.dao.legal.impl;

import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.core.simple.ParameterizedBeanPropertyRowMapper;

import com.pennant.backend.dao.legal.LegalDetailDAO;
import com.pennant.backend.model.legal.LegalDetail;
import com.pennanttech.pennapps.core.ConcurrencyException;
import com.pennanttech.pennapps.core.DependencyFoundException;
import com.pennanttech.pennapps.core.jdbc.SequenceDao;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pff.core.TableType;
import com.pennanttech.pff.core.util.QueryUtil;

/**
 * Data access layer implementation for <code>LegalDetail</code> with set of
 * CRUD operations.
 */
public class LegalDetailDAOImpl extends SequenceDao<LegalDetail> implements LegalDetailDAO {
	private static Logger logger = Logger.getLogger(LegalDetailDAOImpl.class);



	public LegalDetailDAOImpl() {
		super();
	}

	@Override
	public LegalDetail getLegalDetail(long legalId, String type) {
		logger.debug(Literal.ENTERING);

		// Prepare the SQL.
		StringBuilder sql = new StringBuilder("SELECT ");
		sql.append(" legalId, legalReference, loanReference, collateralReference, branch, legalDate, schedulelevelArea, ");
		sql.append(" legalDecision, legalRemarks, propertyDetailModt, propertyDetailECDate, ecPropertyOwnerName, active,");
		sql.append(" Version, LastMntOn, LastMntBy,RecordStatus, RoleCode, NextRoleCode, TaskId, NextTaskId, RecordType, WorkflowId");
		if (type.contains("View")) {
			sql.append(" ,branchDesc");
		}
		sql.append(" From LegalDetails");
		sql.append(type);
		sql.append(" Where legalId = :legalId");

		// Execute the SQL, binding the arguments.
		logger.trace(Literal.SQL + sql.toString());
		LegalDetail legalDetail = new LegalDetail();
		legalDetail.setLegalId(legalId);

		SqlParameterSource paramSource = new BeanPropertySqlParameterSource(legalDetail);
		RowMapper<LegalDetail> rowMapper = ParameterizedBeanPropertyRowMapper.newInstance(LegalDetail.class);
		try {
			legalDetail = jdbcTemplate.queryForObject(sql.toString(), paramSource, rowMapper);
		} catch (EmptyResultDataAccessException e) {
			legalDetail = null;
		}
		logger.debug(Literal.LEAVING);
		return legalDetail;
	}

	@Override
	public boolean isDuplicateKey(long legalId, String loanReference, String collateralReference, TableType tableType) {
		logger.debug(Literal.ENTERING);

		// Prepare the SQL.
		String sql;
		String whereClause = "loanReference = :loanReference AND collateralReference = :collateralReference AND legalId != :legalId";

		switch (tableType) {
		case MAIN_TAB:
			sql = QueryUtil.getCountQuery("LegalDetails", whereClause);
			break;
		case TEMP_TAB:
			sql = QueryUtil.getCountQuery("LegalDetails_Temp", whereClause);
			break;
		default:
			sql = QueryUtil.getCountQuery(new String[] { "LegalDetails_Temp", "LegalDetails" }, whereClause);
			break;
		}

		// Execute the SQL, binding the arguments.
		logger.trace(Literal.SQL + sql);
		MapSqlParameterSource paramSource = new MapSqlParameterSource();
		paramSource.addValue("legalId", legalId);
		paramSource.addValue("loanReference", loanReference);
		paramSource.addValue("collateralReference", collateralReference);

		Integer count = jdbcTemplate.queryForObject(sql, paramSource, Integer.class);
		boolean exists = false;
		if (count > 0) {
			exists = true;
		}
		logger.debug(Literal.LEAVING);
		return exists;
	}

	@Override
	public String save(LegalDetail legalDetail, TableType tableType) {
		logger.debug(Literal.ENTERING);

		// Prepare the SQL.
		StringBuilder sql = new StringBuilder(" insert into LegalDetails");
		sql.append(tableType.getSuffix());
		sql.append("( legalId, legalReference, loanReference, collateralReference, branch, legalDate, schedulelevelArea, ");
		sql.append(" legalDecision, legalRemarks, propertyDetailModt, propertyDetailECDate, ecPropertyOwnerName, active,");
		sql.append(" Version , LastMntBy, LastMntOn, RecordStatus, RoleCode, NextRoleCode, TaskId, NextTaskId, RecordType, WorkflowId)");
		sql.append(" values(");
		sql.append(" :legalId, :legalReference, :loanReference, :collateralReference, :branch, :legalDate, :schedulelevelArea, ");
		sql.append(" :legalDecision, :legalRemarks, :propertyDetailModt, :propertyDetailECDate, :ecPropertyOwnerName, :active,");
		sql.append(" :Version , :LastMntBy, :LastMntOn, :RecordStatus, :RoleCode, :NextRoleCode, :TaskId, :NextTaskId, :RecordType, :WorkflowId)");

		if (legalDetail.getId() == Long.MIN_VALUE) {
			legalDetail.setId(getNextId("SeqLegalDetails"));
			legalDetail.setLegalReference(getLegalReference(legalDetail.getId()));
			logger.debug("get NextID:" + legalDetail.getId());
		}

		// Execute the SQL, binding the arguments.
		logger.trace(Literal.SQL + sql.toString());
		SqlParameterSource paramSource = new BeanPropertySqlParameterSource(legalDetail);

		try {
			jdbcTemplate.update(sql.toString(), paramSource);
		} catch (DuplicateKeyException e) {
			throw new ConcurrencyException(e);
		}
		logger.debug(Literal.LEAVING);
		return String.valueOf(legalDetail.getId());
	}

	@Override
	public void update(LegalDetail legalDetail, TableType tableType) {
		logger.debug(Literal.ENTERING);

		// Prepare the SQL.
		StringBuilder sql = new StringBuilder("update LegalDetails");
		sql.append(tableType.getSuffix());
		sql.append(" set legalId = :legalId, loanReference = :loanReference, collateralReference = :collateralReference, branch = :branch, ");
		sql.append(" legalDate = :legalDate, schedulelevelArea = :schedulelevelArea, legalDecision = :legalDecision, active = :active, ");
		sql.append(" legalRemarks = :legalRemarks, propertyDetailModt = :propertyDetailModt, propertyDetailECDate = :propertyDetailECDate, ecPropertyOwnerName = :ecPropertyOwnerName,");
		sql.append(" LastMntOn = :LastMntOn, RecordStatus = :RecordStatus, RoleCode = :RoleCode,");
		sql.append(" NextRoleCode = :NextRoleCode, TaskId = :TaskId, NextTaskId = :NextTaskId,");
		sql.append(" RecordType = :RecordType, WorkflowId = :WorkflowId");
		sql.append(" where legalId = :legalId ");

		// Execute the SQL, binding the arguments.
		logger.trace(Literal.SQL + sql.toString());

		SqlParameterSource paramSource = new BeanPropertySqlParameterSource(legalDetail);
		int recordCount = jdbcTemplate.update(sql.toString(), paramSource);

		// Check for the concurrency failure.
		if (recordCount == 0) {
			throw new ConcurrencyException();
		}
		logger.debug(Literal.LEAVING);
	}

	@Override
	public void delete(LegalDetail legalDetail, TableType tableType) {
		logger.debug(Literal.ENTERING);

		// Prepare the SQL.
		StringBuilder sql = new StringBuilder("delete from LegalDetails");
		sql.append(tableType.getSuffix());
		sql.append(" where legalId = :legalId ");

		// Execute the SQL, binding the arguments.
		logger.trace(Literal.SQL + sql.toString());
		SqlParameterSource paramSource = new BeanPropertySqlParameterSource(legalDetail);
		int recordCount = 0;
		try {
			recordCount = jdbcTemplate.update(sql.toString(), paramSource);
		} catch (DataAccessException e) {
			throw new DependencyFoundException(e);
		}
		// Check for the concurrency failure.
		if (recordCount == 0) {
			throw new ConcurrencyException();
		}
		logger.debug(Literal.LEAVING);
	}

	/**
	 * Checking the record Exists or Not
	 */
	@Override
	public boolean isExists(String reference, String collateralRef, String type) {
		logger.debug(Literal.ENTERING);

		int count = 0;
		MapSqlParameterSource source = new MapSqlParameterSource();
		
		StringBuilder selectSql = new StringBuilder("SELECT  COUNT(*)  FROM  LegalDetails");
		selectSql.append(StringUtils.trimToEmpty(type));
		selectSql.append(" Where loanReference = :loanReference and collateralReference = :collateralReference");
		logger.debug("selectSql: " + selectSql.toString());
		
		source.addValue("loanReference", reference);
		source.addValue("collateralReference", collateralRef);
		try {
			count = this.jdbcTemplate.queryForObject(selectSql.toString(), source, Integer.class);
		} catch (EmptyResultDataAccessException e) {
			count = 0;
		}
		logger.debug(Literal.LEAVING);
		return count > 0 ? true : false;
	}
	
	/**
	 * Updating the legal details
	 */
	@Override
	public void updateLegalDeatils(String reference, String collateralRef, boolean active) {
		logger.debug(Literal.ENTERING);

		StringBuilder mainSql = new StringBuilder();
		mainSql.append(" update LegalDetails set active = :active ");
		mainSql.append(" Where loanReference = :loanReference and collateralReference = :collateralReference ");

		StringBuilder tempSql = new StringBuilder();
		tempSql.append(" update LegalDetails_Temp set active = :active ");
		tempSql.append(" Where loanReference = :loanReference and collateralReference = :collateralReference ");

		MapSqlParameterSource source = new MapSqlParameterSource();

		source.addValue("loanReference", reference);
		source.addValue("collateralReference", collateralRef);
		source.addValue("active", active);

		try {
			jdbcTemplate.update(mainSql.toString(), source);
			jdbcTemplate.update(tempSql.toString(), source);
		} catch (Exception e) {
			logger.debug(Literal.EXCEPTION, e);
			throw e;
		} finally {
			source = null;
			tempSql = null;
			mainSql = null;
		}
		logger.debug(Literal.LEAVING);
	}
	
	private String getLegalReference(long legalId) {
		String reference  = new String();
		reference  = reference.concat("LG");
		reference = reference.concat(StringUtils.leftPad(String.valueOf(legalId), 18, "0"));
		return reference;
	}
	

	@Override
	public boolean isExists(String loanReference, TableType tableType) {
		logger.debug(Literal.ENTERING);

		int count = 0;
		MapSqlParameterSource source = new MapSqlParameterSource();
		
		StringBuilder selectSql = new StringBuilder("SELECT  COUNT(*)  FROM  LegalDetails");
		selectSql.append(StringUtils.trimToEmpty(tableType.getSuffix()));
		selectSql.append(" Where loanReference = :loanReference");
		logger.debug("selectSql: " + selectSql.toString());
		
		source.addValue("loanReference", loanReference);
		try {
			count = this.jdbcTemplate.queryForObject(selectSql.toString(), source, Integer.class);
		} catch (EmptyResultDataAccessException e) {
			count = 0;
		}
		logger.debug(Literal.LEAVING);
		return count > 0 ? true : false;
	}

	

	@Override
	public List<Long> getLegalIdListByFinRef(String loanReference) {
		logger.debug("Entering");

		MapSqlParameterSource source = new MapSqlParameterSource();
		StringBuilder selectSql = new StringBuilder("Select LegalId from LegalDetails");
		selectSql.append(" Where loanReference = :loanReference ");
		source.addValue("loanReference", loanReference);
		logger.debug("selectSql: " + selectSql.toString());

		logger.debug("Leaving");
		return this.jdbcTemplate.queryForList(selectSql.toString(), source, Long.class);
	}
	

}
