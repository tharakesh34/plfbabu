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
 * FileName    		:  FeeTypeDAOImpl.java                                                  * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  03-01-2017    														*
 *                                                                  						*
 * Modified Date    :  03-01-2017    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 03-01-2017       PENNANT	                 0.1                                            * 
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

package com.pennant.backend.dao.feetype.impl;

import javax.sql.DataSource;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.core.simple.ParameterizedBeanPropertyRowMapper;

import com.pennant.backend.dao.feetype.FeeTypeDAO;
import com.pennant.backend.dao.impl.BasisNextidDaoImpl;
import com.pennant.backend.model.feetype.FeeType;
import com.pennanttech.pennapps.core.ConcurrencyException;
import com.pennanttech.pennapps.core.DependencyFoundException;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pff.core.TableType;
import com.pennanttech.pff.core.util.QueryUtil;

/**
 * Data access layer implementation for <code>FeeType</code> with set of CRUD operations.
 */

public class FeeTypeDAOImpl extends BasisNextidDaoImpl<FeeType> implements FeeTypeDAO {

	private static Logger				logger	= Logger.getLogger(FeeTypeDAOImpl.class);

	// Spring Named JDBC Template
	private NamedParameterJdbcTemplate	namedParameterJdbcTemplate;

	public FeeTypeDAOImpl() {
		super();
	}

	/**
	 * Fetch the Record FeeType details by key field
	 * 
	 * @param id
	 *            (int)
	 * @param type
	 *            (String) ""/_Temp/_View
	 * @return FeeType
	 */
	@Override
	public FeeType getFeeTypeById(final long id, String type) {
		logger.debug("Entering");
		
		FeeType feeType = new FeeType();
		feeType.setId(id);
		StringBuilder selectSql = new StringBuilder();

		selectSql.append(" Select feeTypeID, feeTypeCode, feeTypeDesc, active, manualAdvice, AdviseType, AccountSetId, TaxComponent, TaxApplicable,");
		if(type.contains("View")){
			selectSql.append(" AccountSetCode, AccountSetCodeName," );
		}
		selectSql.append(" Version, LastMntOn, LastMntBy,RecordStatus, RoleCode, NextRoleCode, TaskId, NextTaskId, RecordType, WorkflowId,HostFeeTypeCode, AmortzReq");
		selectSql.append(" From FeeTypes");
		selectSql.append(StringUtils.trimToEmpty(type));
		selectSql.append(" Where FeeTypeID =:FeeTypeID");

		logger.debug("sql: " + selectSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(feeType);
		RowMapper<FeeType> typeRowMapper = ParameterizedBeanPropertyRowMapper.newInstance(FeeType.class);

		try {
			feeType = this.namedParameterJdbcTemplate.queryForObject(selectSql.toString(), beanParameters,
					typeRowMapper);
		} catch (EmptyResultDataAccessException e) {
			logger.warn("Exception: ", e);
			feeType = null;
		}
		
		logger.debug("Leaving");
		return feeType;
	}
	
	@Override
	public boolean isDuplicateKey(long feeTypeID, String feeTypeCode, TableType tableType) {
		logger.debug(Literal.ENTERING);

		// Prepare the SQL.
		String sql;
		String whereClause ="FeeTypeCode = :feeTypeCode and FeeTypeID != :feeTypeID";
		switch (tableType) {
		case MAIN_TAB:
			sql = QueryUtil.getCountQuery("FeeTypes", whereClause);
			break;
		case TEMP_TAB:
			sql = QueryUtil.getCountQuery("FeeTypes_Temp", whereClause);
			break;
		default:
			sql = QueryUtil.getCountQuery(new String[] { "FeeTypes_Temp", "FeeTypes" }, whereClause);
			break;
		}

		// Execute the SQL, binding the arguments.
		logger.trace(Literal.SQL + sql);
		MapSqlParameterSource paramSource = new MapSqlParameterSource();
		paramSource.addValue("feeTypeID", feeTypeID);
		paramSource.addValue("feeTypeCode", feeTypeCode);
		
		Integer count = namedParameterJdbcTemplate.queryForObject(sql, paramSource, Integer.class);

		boolean exists = false;
		if (count > 0) {
			exists = true;
		}

		logger.debug(Literal.LEAVING);
		return exists;
	}
	
	@Override
	public String save(FeeType feeType, TableType tableType) {
		logger.debug(Literal.ENTERING);
		
		// Prepare the SQL.
		
		StringBuilder sql = new StringBuilder("insert into FeeTypes");
		sql.append(tableType.getSuffix());
		sql.append(" (feeTypeID, feeTypeCode, feeTypeDesc, manualAdvice, AdviseType, AccountSetId, active, TaxComponent,");
		sql.append(" Version , LastMntBy, LastMntOn, RecordStatus, RoleCode, NextRoleCode, TaskId, NextTaskId, RecordType, WorkflowId,HostFeeTypeCode,  AmortzReq)");
		sql.append(" values(");
		sql.append(" :feeTypeID, :feeTypeCode, :feeTypeDesc, :manualAdvice, :AdviseType, :AccountSetId, :active, :TaxComponent,");
		sql.append(" :Version , :LastMntBy, :LastMntOn, :RecordStatus, :RoleCode, :NextRoleCode, :TaskId, :NextTaskId, :RecordType, :WorkflowId,:HostFeeTypeCode, :AmortzReq)");
		
		// Get the identity sequence number.
		if (feeType.getId() == Long.MIN_VALUE) {
			feeType.setId(getNextidviewDAO().getNextId("SeqFeeTypes"));
		}
		
		// Execute the SQL, binding the arguments.
		logger.trace(Literal.SQL + sql.toString());
		SqlParameterSource paramSource = new BeanPropertySqlParameterSource(feeType);
		
		try {
			namedParameterJdbcTemplate.update(sql.toString(), paramSource);
		} catch (DuplicateKeyException e) {
			throw new ConcurrencyException(e);
		}
		
		logger.debug("Leaving");
		return String.valueOf(feeType.getFeeTypeID());
	}
	
	@Override
	public void update(FeeType feeType, TableType tableType) {
		logger.debug(Literal.ENTERING);
		
		StringBuilder sql = new StringBuilder("update FeeTypes");
		sql.append(tableType.getSuffix());
		sql.append(" set feeTypeCode=:feeTypeCode,feeTypeDesc=:feeTypeDesc,");
		sql.append(" active=:active,");
		sql.append(" manualAdvice = :manualAdvice, AdviseType = :AdviseType, AccountSetId = :AccountSetId, TaxComponent = :TaxComponent ,");
		sql.append(" Version= :Version , LastMntBy = :LastMntBy, LastMntOn = :LastMntOn, RecordStatus= :RecordStatus, RoleCode = :RoleCode,");
		sql.append(" NextRoleCode = :NextRoleCode, TaskId = :TaskId, NextTaskId = :NextTaskId, RecordType = :RecordType,");
		sql.append(" WorkflowId = :WorkflowId,HostFeeTypeCode=:HostFeeTypeCode, AmortzReq = :AmortzReq ");
		sql.append(" where FeeTypeID =:FeeTypeID");
		sql.append(QueryUtil.getConcurrencyCondition(tableType));

		// Execute the SQL, binding the arguments.
		logger.trace(Literal.SQL + sql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(feeType);
		int recordCount = namedParameterJdbcTemplate.update(sql.toString(), beanParameters);
		
		// Check for the concurrency failure.
		if (recordCount == 0) {
			throw new ConcurrencyException();
		}
		
		logger.debug(Literal.LEAVING);
	}
	
	@Override
	public void delete(FeeType feeType, TableType tableType) {
		logger.debug(Literal.ENTERING);
		

		StringBuilder sql = new StringBuilder("delete From FeeTypes");
		sql.append(tableType.getSuffix());
		sql.append(" where FeeTypeID =:FeeTypeID");
		sql.append(QueryUtil.getConcurrencyCondition(tableType));

		// Execute the SQL, binding the arguments.
		logger.trace(Literal.SQL + sql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(feeType);
		int recordCount = 0;
		
		try {
			recordCount = namedParameterJdbcTemplate.update(sql.toString(), beanParameters);
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
	 * To Set dataSource
	 * 
	 * @param dataSource
	 */

	public void setDataSource(DataSource dataSource) {
		this.namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
	}
	/**
	 * Fetch the Record FeeType details by Fee code
	 * 
	 * @param feeTypeCode
	 *            (String)
	 * @return FeeType
	 */
	@Override
	public FeeType getApprovedFeeTypeByFeeCode(String feeTypeCode) {
		logger.debug("Entering");

		FeeType feeType = new FeeType();
		feeType.setFeeTypeCode(feeTypeCode);
		StringBuilder selectSql = new StringBuilder();

		selectSql.append(" Select feeTypeID, feeTypeCode, feeTypeDesc, active, manualAdvice, AdviseType, AccountSetId,HostFeeTypeCode,AmortzReq,TaxApplicable");
		selectSql.append(" From FeeTypes");
		selectSql.append(" Where FeeTypeCode =:FeeTypeCode");

		logger.debug("sql: " + selectSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(feeType);
		RowMapper<FeeType> typeRowMapper = ParameterizedBeanPropertyRowMapper.newInstance(FeeType.class);

		try {
			feeType = this.namedParameterJdbcTemplate.queryForObject(selectSql.toString(), beanParameters,
					typeRowMapper);
		} catch (EmptyResultDataAccessException e) {
			logger.warn("Exception: ", e);
			feeType = null;
		}

		logger.debug("Leaving");

		return feeType;
	}
	
	@Override
	public int getAccountingSetIdCount(long accountSetId, String type) {
		logger.debug("Entering");

		MapSqlParameterSource source = null;
		int count = 0;

		StringBuilder selectSql = new StringBuilder("Select Count(*) From FeeTypes");
		selectSql.append(StringUtils.trimToEmpty(type));
		selectSql.append(" Where AccountSetId = :AccountSetId");
		logger.debug("selectSql: " + selectSql.toString());

		source = new MapSqlParameterSource();
		source.addValue("AccountSetId", accountSetId);

		try {
			count = this.namedParameterJdbcTemplate.queryForObject(selectSql.toString(), source, Integer.class);
		} catch (DataAccessException e) {
			logger.warn("Exception: ", e);
			count = 0;
		}

		logger.debug("Leaving");

		return count;
	}
	
	@Override
	public long getFinFeeTypeIdByFeeType(String feeTypeCode, String type) {
		logger.debug("Entering");

		long feeTypeId = Long.MIN_VALUE;
		StringBuilder selectSql = new StringBuilder();
		selectSql.append(" SELECT feeTypeID From FeeTypes");
		selectSql.append(StringUtils.trimToEmpty(type));
		selectSql.append(" WHERE FeeTypeCode = :FeeTypeCode ");

		logger.debug("selectSql: " + selectSql.toString());
		MapSqlParameterSource source = new MapSqlParameterSource();
		source.addValue("FeeTypeCode", feeTypeCode);

		try {
			feeTypeId = this.namedParameterJdbcTemplate.queryForObject(selectSql.toString(), source, Long.class);
		} catch (EmptyResultDataAccessException e) {
			feeTypeId = Long.MIN_VALUE;
		}

		logger.debug("Leaving");

		return feeTypeId;
	}
	
}