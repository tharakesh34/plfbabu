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
 * FileName    		:  FeeWaiverHeaderDAOImpl.java                                          * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  27-11-2017    														*
 *                                                                  						*
 * Modified Date    :  			    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 27-11-2017       PENNANT	                 0.1                                            * 
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

package com.pennant.backend.dao.finance.impl;

import javax.sql.DataSource;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.core.simple.ParameterizedBeanPropertyRowMapper;

import com.pennant.backend.dao.finance.FeeWaiverHeaderDAO;
import com.pennant.backend.model.finance.FeeWaiverHeader;
import com.pennanttech.pennapps.core.ConcurrencyException;
import com.pennanttech.pennapps.core.DependencyFoundException;
import com.pennanttech.pennapps.core.jdbc.SequenceDao;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pff.core.TableType;

/**
 * Data access layer implementation for <code>FeeWaiverHeader</code> with set of CRUD operations.
 */

public class FeeWaiverHeaderDAOImpl extends SequenceDao<FeeWaiverHeader> implements FeeWaiverHeaderDAO {

	private static Logger				logger	= Logger.getLogger(FeeWaiverHeaderDAOImpl.class);

	public FeeWaiverHeaderDAOImpl() {
		super();
	}

	/**
	 * Fetch the Record FeeWaiverHeader details by key field
	 * 
	 * @param FinReference
	 * 
	 * @param id@Override
	 *            public FeeWaiverHeader getFeeWaiverHeaderByFinRef(String finreference, String type) { // TODO
	 *            Auto-generated method stub return null; } (long)
	 * 
	 * @param type
	 *            (String) ""/_Temp/_View
	 * @return FeeWaiverHeader
	 */
	@Override
	public FeeWaiverHeader getFeeWaiverHeaderById(long waiverId, String type) {
		logger.debug("Entering");

		FeeWaiverHeader feeWaiverHeader = new FeeWaiverHeader();
		feeWaiverHeader.setWaiverId(waiverId);

		StringBuilder selectSql = new StringBuilder();
		selectSql.append(" Select WaiverId, FinReference, Event, Remarks ");
		selectSql.append(
				" Version, LastMntOn, LastMntBy,RecordStatus, RoleCode, NextRoleCode, TaskId, NextTaskId, RecordType, WorkflowId");
		if (StringUtils.trimToEmpty(type).contains("View")) {
			selectSql.append("");
		}
		selectSql.append(" From FeeWaiverHeader");
		selectSql.append(StringUtils.trimToEmpty(type));
		selectSql.append(" Where WaiverId = :WaiverId");

		logger.debug("sql: " + selectSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(feeWaiverHeader);
		RowMapper<FeeWaiverHeader> typeRowMapper = ParameterizedBeanPropertyRowMapper
				.newInstance(FeeWaiverHeader.class);

		try {
			feeWaiverHeader = jdbcTemplate.queryForObject(selectSql.toString(), beanParameters,
					typeRowMapper);
		} catch (EmptyResultDataAccessException e) {
			logger.warn("Exception: ", e);
			feeWaiverHeader = null;
		}

		logger.debug("Leaving");
		return feeWaiverHeader;
	}

	/**
	 * Fetch the Record FeeWaiverHeader details by finReference and event
	 * 
	 * @param finreference
	 *            (String)
	 * @param type
	 *            (String)
	 * @return FeeWaiverHeader
	 */
	@Override
	public FeeWaiverHeader getFeeWaiverHeaderByFinRef(String finReference, String type) {
		logger.debug("Entering");

		FeeWaiverHeader feeWaiverHeader = new FeeWaiverHeader();
		feeWaiverHeader.setFinReference(finReference);

		StringBuilder selectSql = new StringBuilder();
		selectSql.append(" Select WaiverId, FinReference, Event, Remarks,PostingDate,ValueDate,");
		selectSql.append(
				" Version, LastMntOn, LastMntBy, RecordStatus, RoleCode, NextRoleCode, TaskId, NextTaskId, RecordType,WorkflowId");
		if (StringUtils.trimToEmpty(type).contains("View")) {
			selectSql.append("");
		}
		selectSql.append(" From FeeWaiverHeader");
		selectSql.append(StringUtils.trimToEmpty(type));
		selectSql.append(" Where FinReference = :FinReference");

		logger.debug("sql: " + selectSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(feeWaiverHeader);
		RowMapper<FeeWaiverHeader> typeRowMapper = ParameterizedBeanPropertyRowMapper
				.newInstance(FeeWaiverHeader.class);

		try {
			feeWaiverHeader = jdbcTemplate.queryForObject(selectSql.toString(), beanParameters,
					typeRowMapper);
		} catch (EmptyResultDataAccessException e) {
			logger.warn("Exception: ", e);
			feeWaiverHeader = null;
		}
		logger.debug("Leaving");
		return feeWaiverHeader;
	}

	@Override
	public String save(FeeWaiverHeader feeWaiverHeader, TableType tableType) {
		logger.debug(Literal.ENTERING);

		// Prepare the SQL.
		StringBuilder sql = new StringBuilder("Insert into FeeWaiverHeader");
		sql.append(tableType.getSuffix());
		sql.append(" (WaiverId, FinReference, Event,Remarks,PostingDate,ValueDate,");
		sql.append(
				" Version , LastMntBy, LastMntOn, RecordStatus, RoleCode, NextRoleCode, TaskId, NextTaskId, RecordType, WorkflowId)");
		sql.append(" values( :WaiverId, :FinReference, :Event,  :Remarks, :PostingDate, :ValueDate,");
		sql.append(
				" :Version, :LastMntBy, :LastMntOn, :RecordStatus, :RoleCode, :NextRoleCode, :TaskId, :NextTaskId, :RecordType, :WorkflowId)");

		// Get the identity sequence number.
		if (feeWaiverHeader.getWaiverId() == Long.MIN_VALUE) {
			feeWaiverHeader.setWaiverId(getNextValue("SeqFeeWaiverHeader"));
		}

		// Execute the SQL, binding the arguments.public FeeWaiverHeader getFeeWaiverHeaderById(String finreference, long finmaintainId, String type)
		logger.trace(Literal.SQL + sql.toString());
		SqlParameterSource paramSource = new BeanPropertySqlParameterSource(feeWaiverHeader);

		try {
			jdbcTemplate.update(sql.toString(), paramSource);
		} catch (DuplicateKeyException e) {
			throw new ConcurrencyException(e);
		}

		logger.debug("Leaving");
		return String.valueOf(feeWaiverHeader.getWaiverId());
	}

	@Override
	public void update(FeeWaiverHeader feeWaiverHeader, TableType tableType) {
		logger.debug(Literal.ENTERING);

		StringBuilder sql = new StringBuilder("update FeeWaiverHeader");
		sql.append(tableType.getSuffix());
		sql.append(" set FinReference =:FinReference, Event =:Event, Remarks =:Remarks,PostingDate =:PostingDate,ValueDate=:ValueDate,");
		sql.append(
				" Version= :Version , LastMntBy = :LastMntBy, LastMntOn = :LastMntOn, RecordStatus= :RecordStatus, RoleCode = :RoleCode, NextRoleCode = :NextRoleCode,");
		sql.append(" TaskId = :TaskId, NextTaskId = :NextTaskId, RecordType = :RecordType, WorkflowId = :WorkflowId");

		sql.append(" where WaiverId =:WaiverId");
		//sql.append(QueryUtil.getConcurrencyCondition(tableType));

		// Execute the SQL, binding the arguments.
		logger.trace(Literal.SQL + sql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(feeWaiverHeader);
		int recordCount = jdbcTemplate.update(sql.toString(), beanParameters);

		// Check for the concurrency failure.
		if (recordCount == 0) {
			throw new ConcurrencyException();
		}

		logger.debug(Literal.LEAVING);
	}

	/**
	 * 
	 */
	@Override
	public void delete(FeeWaiverHeader feeWaiverHeader, TableType tableType) {
		logger.debug(Literal.ENTERING);

		StringBuilder sql = new StringBuilder("delete From FeeWaiverHeader");
		sql.append(tableType.getSuffix());
		sql.append(" where WaiverId =:WaiverId");
		//sql.append(QueryUtil.getConcurrencyCondition(tableType));

		// Execute the SQL, binding the arguments.
		logger.trace(Literal.SQL + sql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(feeWaiverHeader);
		int recordCount = 0;

		try {
			recordCount = jdbcTemplate.update(sql.toString(), beanParameters);
		} catch (DataAccessException e) {
			throw new DependencyFoundException(e);
		}

		// Check for the concurrency failure.
		if (recordCount == 0) {
			throw new ConcurrencyException();
		}

		logger.debug(Literal.LEAVING);
	}

	
}