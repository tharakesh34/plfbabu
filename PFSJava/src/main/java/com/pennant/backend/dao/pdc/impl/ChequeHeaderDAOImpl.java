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
 * FileName    		:  ChequeHeaderDAOImpl.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  27-11-2017    														*
 *                                                                  						*
 * Modified Date    :  27-11-2017    														*
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
package com.pennant.backend.dao.pdc.impl;

import javax.sql.DataSource;

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

import com.pennant.backend.dao.impl.BasisNextidDaoImpl;
import com.pennant.backend.dao.pdc.ChequeHeaderDAO;
import com.pennant.backend.model.finance.ChequeHeader;
import com.pennant.backend.model.mandate.Mandate;
import com.pennanttech.pennapps.core.ConcurrencyException;
import com.pennanttech.pennapps.core.DependencyFoundException;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pff.core.TableType;
import com.pennanttech.pff.core.util.QueryUtil;

/**
 * Data access layer implementation for <code>ChequeHeader</code> with set of CRUD operations.
 */
public class ChequeHeaderDAOImpl extends BasisNextidDaoImpl<Mandate> implements ChequeHeaderDAO {
	private static Logger				logger	= Logger.getLogger(ChequeHeaderDAOImpl.class);

	private NamedParameterJdbcTemplate	namedParameterJdbcTemplate;

	public ChequeHeaderDAOImpl() {
		super();
	}

	@Override
	public ChequeHeader getChequeHeader(long headerID, String type) {
		logger.debug(Literal.ENTERING);

		// Prepare the SQL.
		StringBuilder sql = new StringBuilder("SELECT ");
		sql.append(" headerID, finReference, chequeType, noOfCheques, totalAmount, active, ");

		sql.append(" Version, LastMntOn, LastMntBy,RecordStatus, RoleCode, NextRoleCode, TaskId, NextTaskId, RecordType, WorkflowId");
		sql.append(" From CHEQUEHEADER");
		sql.append(type);
		sql.append(" Where headerID = :headerID");

		// Execute the SQL, binding the arguments.
		logger.trace(Literal.SQL + sql.toString());

		ChequeHeader chequeHeader = new ChequeHeader();
		chequeHeader.setHeaderID(headerID);

		SqlParameterSource paramSource = new BeanPropertySqlParameterSource(chequeHeader);
		RowMapper<ChequeHeader> rowMapper = ParameterizedBeanPropertyRowMapper.newInstance(ChequeHeader.class);

		try {
			chequeHeader = namedParameterJdbcTemplate.queryForObject(sql.toString(), paramSource, rowMapper);
		} catch (EmptyResultDataAccessException e) {
			logger.error("Exception: ", e);
			chequeHeader = null;
		}

		logger.debug(Literal.LEAVING);
		return chequeHeader;
	}
	
	@Override
	public ChequeHeader getChequeHeader(String finReference,String type) {
		logger.debug(Literal.ENTERING);
		
		// Prepare the SQL.
		StringBuilder sql = new StringBuilder("SELECT ");
		sql.append(" headerID, finReference, chequeType, noOfCheques, totalAmount, active, ");
		
		sql.append(" Version, LastMntOn, LastMntBy,RecordStatus, RoleCode, NextRoleCode, TaskId, NextTaskId, RecordType, WorkflowId" );
		sql.append(" From CHEQUEHEADER");
		sql.append(type);
		sql.append(" Where finReference = :finReference");
		
		// Execute the SQL, binding the arguments.
		logger.trace(Literal.SQL + sql.toString());

		ChequeHeader chequeHeader = new ChequeHeader();
		chequeHeader.setFinReference(finReference);

		SqlParameterSource paramSource = new BeanPropertySqlParameterSource(chequeHeader);
		RowMapper<ChequeHeader> rowMapper = ParameterizedBeanPropertyRowMapper.newInstance(ChequeHeader.class);

		try {
			chequeHeader = namedParameterJdbcTemplate.queryForObject(sql.toString(), paramSource, rowMapper);
		} catch (EmptyResultDataAccessException e) {
			logger.error("Exception: ", e);
			chequeHeader = null;
		}

		logger.debug(Literal.LEAVING);
		return chequeHeader;
	}		
	
	@Override
	public ChequeHeader getChequeHeaderByRef(String finReference, String type) {
		logger.debug(Literal.ENTERING);
		
		// Prepare the SQL.
		StringBuilder sql = new StringBuilder("SELECT ");
		sql.append(" headerID, finReference, chequeType, noOfCheques, totalAmount, active, ");
		
		sql.append(" Version, LastMntOn, LastMntBy,RecordStatus, RoleCode, NextRoleCode, TaskId, NextTaskId, RecordType, WorkflowId");
		sql.append(" From CHEQUEHEADER");
		sql.append(type);
		sql.append(" Where finReference = :finReference");
		
		// Execute the SQL, binding the arguments.
		logger.trace(Literal.SQL + sql.toString());
		
		ChequeHeader chequeHeader = new ChequeHeader();
		chequeHeader.setFinReference(finReference);
		
		SqlParameterSource paramSource = new BeanPropertySqlParameterSource(chequeHeader);
		RowMapper<ChequeHeader> rowMapper = ParameterizedBeanPropertyRowMapper.newInstance(ChequeHeader.class);
		
		try {
			chequeHeader = namedParameterJdbcTemplate.queryForObject(sql.toString(), paramSource, rowMapper);
		} catch (EmptyResultDataAccessException e) {
			logger.error("Exception: ", e);
			chequeHeader = null;
		}
		
		logger.debug(Literal.LEAVING);
		return chequeHeader;
	}

	@Override
	public String save(ChequeHeader chequeHeader, TableType tableType) {
		logger.debug(Literal.ENTERING);

		// Prepare the SQL.
		StringBuilder sql = new StringBuilder(" insert into CHEQUEHEADER");
		sql.append(tableType.getSuffix());
		sql.append("(headerID, finReference, chequeType, noOfCheques, totalAmount, active, ");
		sql.append(
				" Version , LastMntBy, LastMntOn, RecordStatus, RoleCode, NextRoleCode, TaskId, NextTaskId, RecordType, WorkflowId)");
		sql.append(" values(");
		sql.append(" :headerID, :finReference, :chequeType, :noOfCheques, :totalAmount, :active, ");
		sql.append(
				" :Version , :LastMntBy, :LastMntOn, :RecordStatus, :RoleCode, :NextRoleCode, :TaskId, :NextTaskId, :RecordType, :WorkflowId)");
		if (chequeHeader.getId() == Long.MIN_VALUE || chequeHeader.getId() == 0) {
			chequeHeader.setId(getNextidviewDAO().getNextId("SeqChequeHeader"));
			logger.debug("get NextID:" + chequeHeader.getId());
		}
		// Execute the SQL, binding the arguments.
		logger.trace(Literal.SQL + sql.toString());
		SqlParameterSource paramSource = new BeanPropertySqlParameterSource(chequeHeader);

		try {
			namedParameterJdbcTemplate.update(sql.toString(), paramSource);
		} catch (DuplicateKeyException e) {
			throw new ConcurrencyException(e);
		}

		logger.debug(Literal.LEAVING);
		return String.valueOf(chequeHeader.getHeaderID());
	}

	@Override
	public void update(ChequeHeader chequeHeader, TableType tableType) {
		logger.debug(Literal.ENTERING);

		// Prepare the SQL.
		StringBuilder sql = new StringBuilder("update CHEQUEHEADER");
		sql.append(tableType.getSuffix());
		sql.append("  set finReference = :finReference, chequeType = :chequeType, noOfCheques = :noOfCheques, ");
		sql.append(" totalAmount = :totalAmount, active = :active, ");
		sql.append(" LastMntOn = :LastMntOn, RecordStatus = :RecordStatus, RoleCode = :RoleCode,");
		sql.append(" NextRoleCode = :NextRoleCode, TaskId = :TaskId, NextTaskId = :NextTaskId,");
		sql.append(" RecordType = :RecordType, WorkflowId = :WorkflowId");
		sql.append(" where headerID = :headerID ");

		// Execute the SQL, binding the arguments.
		logger.trace(Literal.SQL + sql.toString());

		SqlParameterSource paramSource = new BeanPropertySqlParameterSource(chequeHeader);
		int recordCount = namedParameterJdbcTemplate.update(sql.toString(), paramSource);

		// Check for the concurrency failure.
		if (recordCount == 0) {
			throw new ConcurrencyException();
		}

		logger.debug(Literal.LEAVING);
	}

	@Override
	public void delete(ChequeHeader chequeHeader, TableType tableType) {
		logger.debug(Literal.ENTERING);

		// Prepare the SQL.
		StringBuilder sql = new StringBuilder("delete from CHEQUEHEADER");
		sql.append(tableType.getSuffix());
		sql.append(" where headerID = :headerID ");

		// Execute the SQL, binding the arguments.
		logger.trace(Literal.SQL + sql.toString());
		SqlParameterSource paramSource = new BeanPropertySqlParameterSource(chequeHeader);
		int recordCount = 0;

		try {
			recordCount = namedParameterJdbcTemplate.update(sql.toString(), paramSource);
		} catch (DataAccessException e) {
			throw new DependencyFoundException(e);
		}

		// Check for the concurrency failure.
		if (recordCount == 0) {
			throw new ConcurrencyException();
		}

		logger.debug(Literal.LEAVING);
	}

	@Override
	public void deleteByFinRef(String finRef, TableType tableType) {
		logger.debug(Literal.ENTERING);
		ChequeHeader chequeHeader = new ChequeHeader();
		chequeHeader.setFinReference(finRef);
		// Prepare the SQL.
		StringBuilder sql = new StringBuilder("delete from CHEQUEHEADER");
		sql.append(tableType.getSuffix());
		sql.append(" where finReference = :finReference ");

		// Execute the SQL, binding the arguments.
		logger.trace(Literal.SQL + sql.toString());
		SqlParameterSource paramSource = new BeanPropertySqlParameterSource(chequeHeader);
		int recordCount = 0;

		try {
			recordCount = namedParameterJdbcTemplate.update(sql.toString(), paramSource);
		} catch (DataAccessException e) {
			throw new DependencyFoundException(e);
		}

		// Check for the concurrency failure.
		if (recordCount == 0) {
			throw new ConcurrencyException();
		}

		logger.debug(Literal.LEAVING);
	}

	@Override
	public boolean isDuplicateKey(long headerID, String finRef, TableType tableType) {
		logger.debug(Literal.ENTERING);

		// Prepare the SQL.
		String sql;
		String whereClause = "headerID != :headerID AND finReference = :finReference ";

		switch (tableType) {
		case MAIN_TAB:
			sql = QueryUtil.getCountQuery("ChequeHeader", whereClause);
			break;
		case TEMP_TAB:
			sql = QueryUtil.getCountQuery("ChequeHeader_Temp", whereClause);
			break;
		default:
			sql = QueryUtil.getCountQuery(new String[] { "ChequeHeader_Temp", "ChequeHeader" }, whereClause);
			break;
		}

		// Execute the SQL, binding the arguments.
		logger.trace(Literal.SQL + sql);
		MapSqlParameterSource paramSource = new MapSqlParameterSource();
		paramSource.addValue("headerID", headerID);
		paramSource.addValue("finReference", finRef);

		Integer count = namedParameterJdbcTemplate.queryForObject(sql, paramSource, Integer.class);

		boolean exists = false;
		if (count > 0) {
			exists = true;
		}

		logger.debug(Literal.LEAVING);
		return exists;
	}

	/**
	 * Sets a new <code>JDBC Template</code> for the given data source.
	 * 
	 * @param dataSource
	 *            The JDBC data source to access.
	 */
	public void setDataSource(DataSource dataSource) {
		namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
	}

}
