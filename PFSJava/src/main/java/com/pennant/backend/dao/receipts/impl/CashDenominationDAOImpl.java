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

package com.pennant.backend.dao.receipts.impl;

import java.util.List;

import javax.sql.DataSource;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.core.simple.ParameterizedBeanPropertyRowMapper;

import com.pennant.backend.dao.impl.BasisCodeDAO;
import com.pennant.backend.dao.receipts.CashDenominationDAO;
import com.pennant.backend.model.finance.CashDenomination;
import com.pennanttech.pennapps.core.ConcurrencyException;
import com.pennanttech.pennapps.core.DependencyFoundException;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pff.core.TableType;
import com.pennanttech.pff.core.util.QueryUtil;

/**
 * Data access layer implementation for <code>CashDenomination</code> with set of CRUD operations.
 */
public class CashDenominationDAOImpl extends BasisCodeDAO<CashDenomination> implements CashDenominationDAO {
	private static Logger				logger	= Logger.getLogger(CashDenominationDAOImpl.class);

	private NamedParameterJdbcTemplate	namedParameterJdbcTemplate;

	public CashDenominationDAOImpl() {
		super();
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
	
	@Override
	public String save(CashDenomination cashDenomination,String type) {
		logger.debug(Literal.ENTERING);
		
		// Prepare the SQL.
		StringBuilder sql =new StringBuilder(" insert into CashDenominations");
		sql.append(StringUtils.trimToEmpty(type) );
		sql.append(" (MovementId, SeqNo, Denomination, Count, Amount,");
		sql.append(" Version, LastMntBy, LastMntOn, RecordStatus, RoleCode, NextRoleCode, TaskId, NextTaskId, RecordType, WorkflowId)" );
		sql.append(" values(");
		sql.append(" :MovementId, :SeqNo, :Denomination, :Count, :Amount,");
		sql.append(" :Version, :LastMntBy, :LastMntOn, :RecordStatus, :RoleCode, :NextRoleCode, :TaskId, :NextTaskId, :RecordType, :WorkflowId)");

		// Execute the SQL, binding the arguments.
		logger.trace(Literal.SQL + sql.toString());
		SqlParameterSource paramSource = new BeanPropertySqlParameterSource(cashDenomination);

		try {
			namedParameterJdbcTemplate.update(sql.toString(), paramSource);
		} catch (DuplicateKeyException e) {
			throw new ConcurrencyException(e);
		}

		logger.debug(Literal.LEAVING);
		return String.valueOf(cashDenomination.getMovementId());
	}	

	@Override
	public void update(CashDenomination cashDenomination, String  type) {
		logger.debug(Literal.ENTERING);
		
		// Prepare the SQL.
		StringBuilder	sql =new StringBuilder("update CashDenominations" );
		sql.append(StringUtils.trimToEmpty(type) );
		sql.append("  set count = :count, amount = :amount, ");
		sql.append(" LastMntOn = :LastMntOn, RecordStatus = :RecordStatus, RoleCode = :RoleCode,");
		sql.append(" NextRoleCode = :NextRoleCode, TaskId = :TaskId, NextTaskId = :NextTaskId,");
		sql.append(" RecordType = :RecordType, WorkflowId = :WorkflowId");
		sql.append(" where MovementId = :MovementId AND denomination = :denomination And SeqNo = :SeqNo");
		//sql.append(QueryUtil.getConcurrencyCondition(tableType));
	
		// Execute the SQL, binding the arguments.
		logger.trace(Literal.SQL + sql.toString());
		
		SqlParameterSource paramSource = new BeanPropertySqlParameterSource(cashDenomination);
		int recordCount = namedParameterJdbcTemplate.update(sql.toString(), paramSource);

		// Check for the concurrency failure.
		if (recordCount == 0) {
			throw new ConcurrencyException();
		}
		
		logger.debug(Literal.LEAVING);
	}

	@Override
	public void delete(CashDenomination cashDenomination, String type) {
		logger.debug(Literal.ENTERING);

		// Prepare the SQL.
		StringBuilder sql = new StringBuilder("delete from CashDenominations");
		sql.append(StringUtils.trimToEmpty(type) );
		sql.append(" where MovementId = :MovementId AND denomination = :denomination And SeqNo = :SeqNo");
		//sql.append(QueryUtil.getConcurrencyCondition(tableType));

		// Execute the SQL, binding the arguments.
		logger.trace(Literal.SQL + sql.toString());
		SqlParameterSource paramSource = new BeanPropertySqlParameterSource(cashDenomination);
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
	public void deleteByMovementId(long movementId, String type) {
		logger.debug(Literal.ENTERING);
		CashDenomination cashDenomination= new CashDenomination();
		cashDenomination.setMovementId(movementId);
		
		// Prepare the SQL.
		StringBuilder sql = new StringBuilder("delete from CashDenominations");
		sql.append(type);
		sql.append(" where MovementId = :MovementId");
		//sql.append(QueryUtil.getConcurrencyCondition(tableType));

		// Execute the SQL, binding the arguments.
		logger.trace(Literal.SQL + sql.toString());
		SqlParameterSource paramSource = new BeanPropertySqlParameterSource(cashDenomination);

		try {
			this.namedParameterJdbcTemplate.update(sql.toString(), paramSource);
		} catch (DataAccessException e) {
			throw new DependencyFoundException(e);
		}

		logger.debug(Literal.LEAVING);
	}

	@Override
	public List<CashDenomination> getCashDenominationList(long movementId, String type) {
		logger.debug(Literal.ENTERING);
		List<CashDenomination> list;
		
		// Prepare the SQL.
		StringBuilder sql = new StringBuilder("SELECT MovementId, SeqNo, Denomination, Count, Amount,");
		sql.append(" Version, LastMntOn, LastMntBy,RecordStatus, RoleCode, NextRoleCode, TaskId, NextTaskId, RecordType, WorkflowId" );
		sql.append(" From CashDenominations");
		sql.append(StringUtils.trimToEmpty(type));
		sql.append(" Where MovementId = :MovementId Order by SeqNo");
		
		// Execute the SQL, binding the arguments.
		logger.trace(Literal.SQL + sql.toString());

		CashDenomination cashDenomination = new CashDenomination();
		cashDenomination.setMovementId(movementId);
		
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(cashDenomination);
		RowMapper<CashDenomination> rowMapper = ParameterizedBeanPropertyRowMapper.newInstance(CashDenomination.class);
		list = namedParameterJdbcTemplate.query(sql.toString(), beanParameters, rowMapper);
		
		logger.debug(Literal.LEAVING);
		return list;
	}	

	@Override
	public boolean isDuplicateKey(long movementId, TableType tableType) {
		logger.debug(Literal.ENTERING);

		// Prepare the SQL.
		String sql;
		String whereClause = "MovementId = :MovementId";

		switch (tableType) {
		case MAIN_TAB:
			sql = QueryUtil.getCountQuery("CashDenominations", whereClause);
			break;
		case TEMP_TAB:
			sql = QueryUtil.getCountQuery("CashDenominations_Temp", whereClause);
			break;
		default:
			sql = QueryUtil.getCountQuery(new String[] { "CashDenominations_Temp", "CashDenominations" }, whereClause);
			break;
		}

		// Execute the SQL, binding the arguments.
		logger.trace(Literal.SQL + sql);
		MapSqlParameterSource paramSource = new MapSqlParameterSource();
		paramSource.addValue("MovementId", movementId);

		Integer count = namedParameterJdbcTemplate.queryForObject(sql, paramSource, Integer.class);

		boolean exists = false;
		if (count > 0) {
			exists = true;
		}

		logger.debug(Literal.LEAVING);
		return exists;
	}
}	
