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
 * FileName    		:  FinChangeCustomerDAOImpl.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  20-11-2019     														*
 *                                                                  						*
 * Modified Date    :  20-11-2019     														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 *20-11-2019        PENNANT	                 0.1                                            * 
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

import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;

import com.pennant.backend.dao.finance.FinChangeCustomerDAO;
import com.pennant.backend.model.finance.FinChangeCustomer;
import com.pennanttech.pennapps.core.ConcurrencyException;
import com.pennanttech.pennapps.core.DependencyFoundException;
import com.pennanttech.pennapps.core.jdbc.SequenceDao;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pff.core.TableType;
import com.pennanttech.pff.core.util.QueryUtil;

/**
 * Data access layer implementation for <code>FinChangeCustomer</code> with set of CRUD operations.
 */
public class FinChangeCustomerDAOImpl extends SequenceDao<FinChangeCustomer> implements FinChangeCustomerDAO {
	private static Logger logger = LogManager.getLogger(FinChangeCustomerDAOImpl.class);

	public FinChangeCustomerDAOImpl() {
		super();
	}

	@Override
	public FinChangeCustomer getFinChangeCustomerById(long id, String type) {
		logger.debug(Literal.ENTERING);

		// Prepare the SQL.
		StringBuilder sql = new StringBuilder("SELECT ");
		sql.append(" id,finReference ,OldCustId, CoApplicantId, ");
		sql.append(
				" Version, LastMntOn, LastMntBy,RecordStatus, RoleCode, NextRoleCode, TaskId, NextTaskId, RecordType, WorkflowId");
		if (type.contains("_View")) {
			sql.append(", custCategory , CustCif, jcustCif");
		}
		sql.append(" From FinChangeCustomer");
		sql.append(type);
		sql.append("  where Id = :Id");

		// Execute the SQL, binding the arguments.
		logger.trace(Literal.SQL + sql.toString());

		FinChangeCustomer FinChangeCustomer = new FinChangeCustomer();
		FinChangeCustomer.setId(id);

		SqlParameterSource paramSource = new BeanPropertySqlParameterSource(FinChangeCustomer);
		RowMapper<FinChangeCustomer> rowMapper = BeanPropertyRowMapper.newInstance(FinChangeCustomer.class);

		try {
			FinChangeCustomer = jdbcTemplate.queryForObject(sql.toString(), paramSource, rowMapper);
		} catch (EmptyResultDataAccessException e) {
			logger.error("Exception: ", e);
			FinChangeCustomer = null;
		}

		logger.debug(Literal.LEAVING);
		return FinChangeCustomer;
	}

	@Override
	public String save(FinChangeCustomer FinChangeCustomer, TableType tableType) {
		logger.debug(Literal.ENTERING);

		// Get the identity sequence number.
		if (FinChangeCustomer.getId() <= 0) {
			FinChangeCustomer.setId(getNextValue("SeqChangeCustomer"));
		}
		// Prepare the SQL.
		StringBuilder sql = new StringBuilder(" insert into FinChangeCustomer");
		sql.append(tableType.getSuffix());
		sql.append(" (id,finReference ,OldCustId, CoApplicantId, ");
		sql.append(
				" Version , LastMntBy, LastMntOn, RecordStatus, RoleCode, NextRoleCode, TaskId, NextTaskId, RecordType, WorkflowId)");
		sql.append(" values(");
		sql.append(" :id,:FinReference , :OldCustId, :CoApplicantId, ");
		sql.append(
				" :Version , :LastMntBy, :LastMntOn, :RecordStatus, :RoleCode, :NextRoleCode, :TaskId, :NextTaskId, :RecordType, :WorkflowId)");

		// Execute the SQL, binding the arguments.
		logger.trace(Literal.SQL + sql.toString());
		SqlParameterSource paramSource = new BeanPropertySqlParameterSource(FinChangeCustomer);

		try {
			jdbcTemplate.update(sql.toString(), paramSource);
		} catch (DuplicateKeyException e) {
			throw new ConcurrencyException(e);
		}

		logger.debug(Literal.LEAVING);
		return String.valueOf(FinChangeCustomer.getFinReference());
	}

	@Override
	public void update(FinChangeCustomer FinChangeCustomer, TableType tableType) {
		logger.debug(Literal.ENTERING);

		// Prepare the SQL.
		StringBuilder sql = new StringBuilder("update FinChangeCustomer");
		sql.append(tableType.getSuffix());
		sql.append(" set finReference=:finReference, OldCustId = :OldCustId , CoApplicantId = :CoApplicantId, ");
		sql.append(" LastMntOn = :LastMntOn, RecordStatus = :RecordStatus, RoleCode = :RoleCode,");
		sql.append(" NextRoleCode = :NextRoleCode, TaskId = :TaskId, NextTaskId = :NextTaskId,");
		sql.append(" RecordType = :RecordType, WorkflowId = :WorkflowId");
		sql.append(" where id = :id ");
		sql.append(QueryUtil.getConcurrencyCondition(tableType));

		// Execute the SQL, binding the arguments.
		logger.trace(Literal.SQL + sql.toString());

		SqlParameterSource paramSource = new BeanPropertySqlParameterSource(FinChangeCustomer);
		int recordCount = jdbcTemplate.update(sql.toString(), paramSource);

		// Check for the concurrency failure.
		if (recordCount == 0) {
			throw new ConcurrencyException();
		}

		logger.debug(Literal.LEAVING);
	}

	@Override
	public void delete(FinChangeCustomer FinChangeCustomer, TableType tableType) {
		logger.debug(Literal.ENTERING);

		// Prepare the SQL.
		StringBuilder sql = new StringBuilder("delete from FinChangeCustomer");
		sql.append(tableType.getSuffix());
		sql.append(" where id = :id ");
		sql.append(QueryUtil.getConcurrencyCondition(tableType));

		// Execute the SQL, binding the arguments.
		logger.trace(Literal.SQL + sql.toString());
		SqlParameterSource paramSource = new BeanPropertySqlParameterSource(FinChangeCustomer);
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

	@Override
	public void deleteByReference(String finReference) {
		StringBuilder sql = new StringBuilder("Delete from FinChangeCustomer");
		sql.append(" where FinReference = ?");

		logger.trace(Literal.SQL + sql.toString());

		jdbcOperations.update(sql.toString(), ps -> {
			ps.setString(1, finReference);
		});
	}

	@Override
	public boolean isDuplicateKey(long id, String finReference, TableType tableType) {

		/*
		 * // Prepare the SQL. String sql; String whereClause = "finReference = :finReference and id != :id ";
		 * 
		 * switch (tableType) { case MAIN_TAB: sql = QueryUtil.getCountQuery("FinChangeCustomer", whereClause); break;
		 * case TEMP_TAB: sql = QueryUtil.getCountQuery("FinChangeCustomer_TEMP", whereClause); break; default: sql =
		 * QueryUtil.getCountQuery(new String[] { "FinChangeCustomer", "FinChangeCustomer_TEMP" }, whereClause); break;
		 * }
		 * 
		 * // Execute the SQL, binding the arguments. logger.trace(Literal.SQL + sql); MapSqlParameterSource paramSource
		 * = new MapSqlParameterSource(); paramSource.addValue("id", id); paramSource.addValue("finReference",
		 * finReference);
		 * 
		 * Integer count = jdbcTemplate.queryForObject(sql, paramSource, Integer.class);
		 * 
		 * boolean exists = false; if (count > 0) { exists = true; }
		 */
		logger.debug(Literal.LEAVING);
		return false;
	}

	@Override
	public boolean isFinReferenceProcess(String finReference, String type) {
		StringBuilder sql = new StringBuilder("SELECT COUNT(FinReference) FROM FinChangeCustomer");
		sql.append(StringUtils.trimToEmpty(type));
		sql.append(" Where FinReference = :FinReference ");

		logger.trace(Literal.SQL + sql.toString());

		MapSqlParameterSource source = new MapSqlParameterSource();
		source.addValue("FinReference", finReference);

		int count = 0;
		try {
			count = this.jdbcTemplate.queryForObject(sql.toString(), source, Integer.class);
		} catch (EmptyResultDataAccessException e) {
			//
		}

		return count > 0 ? true : false;
	}

	public void updateOldCustId(long OldCustId, long cooapplicantId) {
		logger.debug(Literal.ENTERING);

		StringBuilder sql = new StringBuilder("update FinChangeCustomer");
		sql.append(" set OldCustId = :OldCustId , CoApplicantId = :CoApplicantId");
		sql.append(" where FinReference = :FinReference ");
		logger.trace(Literal.SQL + sql.toString());

		MapSqlParameterSource source = new MapSqlParameterSource();
		source.addValue("OldCustId", cooapplicantId);
		source.addValue("CoApplicantId", OldCustId);

		int recordCount = jdbcTemplate.update(sql.toString(), source);

		if (recordCount == 0) {
			throw new ConcurrencyException();
		}
		logger.debug(Literal.LEAVING);
	}
}
