/**
 * Copyright 2011 - Pennant Technologies
 * 
 * This file is part of Pennant Java Application Framework and related Products. All
 * components/modules/functions/classes/logic in this software, unless otherwise stated, the property of Pennant
 * Technologies.
 * 
 * Copyright and other intellectual property laws protect these materials. Reproduction or retransmission of the
 * materials, in whole or in part, in any manner, without the prior written consent of the copyright holder, is a
 * violation of copyright law.
 */

/**
 ********************************************************************************************
 * FILE HEADER *
 ********************************************************************************************
 * * FileName : DepositChequesDAOImpl.java * * Author : PENNANT TECHONOLOGIES * * Creation Date : 18-07-2018 * *
 * Modified Date : 18-07-2018 * * Description : * *
 ********************************************************************************************
 * Date Author Version Comments *
 ********************************************************************************************
 * 18-07-2018 PENNANT 0.1 * * * * * * * * *
 ********************************************************************************************
 */

package com.pennant.backend.dao.receipts.impl;

import java.util.List;

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

import com.pennant.backend.dao.receipts.DepositChequesDAO;
import com.pennant.backend.model.finance.DepositCheques;
import com.pennant.backend.util.CashManagementConstants;
import com.pennanttech.pennapps.core.ConcurrencyException;
import com.pennanttech.pennapps.core.DependencyFoundException;
import com.pennanttech.pennapps.core.jdbc.JdbcUtil;
import com.pennanttech.pennapps.core.jdbc.SequenceDao;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pennapps.core.resource.Message;
import com.pennanttech.pff.core.TableType;
import com.pennanttech.pff.core.util.QueryUtil;

/**
 * Data access layer implementation for <code>DepositCheques</code> with set of CRUD operations.
 */
public class DepositChequesDAOImpl extends SequenceDao<DepositCheques> implements DepositChequesDAO {
	private static Logger logger = LogManager.getLogger(DepositChequesDAOImpl.class);

	public DepositChequesDAOImpl() {
		super();
	}

	@Override
	public String save(DepositCheques depositCheques, String type) {
		logger.debug(Literal.ENTERING);

		// Prepare the SQL.
		StringBuilder sql = new StringBuilder(" insert into DepositCheques");
		sql.append(StringUtils.trimToEmpty(type));
		sql.append(" (Id, MovementId, ReceiptId, ReceiptMode, Amount, Status, LinkedTranId,");
		sql.append(
				" Version, LastMntBy, LastMntOn, RecordStatus, RoleCode, NextRoleCode, TaskId, NextTaskId, RecordType, WorkflowId)");
		sql.append(" values(");
		sql.append(" :Id, :MovementId, :ReceiptId, :ReceiptMode, :Amount, :Status, :LinkedTranId,");
		sql.append(
				" :Version, :LastMntBy, :LastMntOn, :RecordStatus, :RoleCode, :NextRoleCode, :TaskId, :NextTaskId, :RecordType, :WorkflowId)");

		// Get the identity sequence number.
		if (depositCheques.getId() <= 0) {
			depositCheques.setId(getNextValue("SeqDepositCheques"));
		}

		// Execute the SQL, binding the arguments.
		logger.trace(Literal.SQL + sql.toString());
		SqlParameterSource paramSource = new BeanPropertySqlParameterSource(depositCheques);

		try {
			jdbcTemplate.update(sql.toString(), paramSource);
		} catch (DuplicateKeyException e) {
			throw new ConcurrencyException(e);
		}

		logger.debug(Literal.LEAVING);
		return String.valueOf(depositCheques.getMovementId());
	}

	@Override
	public void update(DepositCheques depositCheques, String type) {
		logger.debug(Literal.ENTERING);

		// Prepare the SQL.
		StringBuilder sql = new StringBuilder("update DepositCheques");
		sql.append(StringUtils.trimToEmpty(type));
		sql.append(
				" Set ReceiptId = :ReceiptId, ReceiptMode = :ReceiptMode, Amount = :Amount, Status = :Status, LinkedTranId = :LinkedTranId,");
		sql.append(" LastMntOn = :LastMntOn, RecordStatus = :RecordStatus, RoleCode = :RoleCode,");
		sql.append(" NextRoleCode = :NextRoleCode, TaskId = :TaskId, NextTaskId = :NextTaskId,");
		sql.append(" RecordType = :RecordType, WorkflowId = :WorkflowId");
		sql.append(" where MovementId = :MovementId AND Id = :Id");
		// sql.append(QueryUtil.getConcurrencyCondition(tableType));

		// Execute the SQL, binding the arguments.
		logger.trace(Literal.SQL + sql.toString());

		SqlParameterSource paramSource = new BeanPropertySqlParameterSource(depositCheques);
		int recordCount = jdbcTemplate.update(sql.toString(), paramSource);

		// Check for the concurrency failure.
		if (recordCount == 0) {
			throw new ConcurrencyException();
		}

		logger.debug(Literal.LEAVING);
	}

	@Override
	public void delete(DepositCheques depositCheques, String type) {
		logger.debug(Literal.ENTERING);

		// Prepare the SQL.
		StringBuilder sql = new StringBuilder("delete from DepositCheques");
		sql.append(StringUtils.trimToEmpty(type));
		sql.append(" where Id = :Id");
		// sql.append(QueryUtil.getConcurrencyCondition(tableType));

		// Execute the SQL, binding the arguments.
		logger.trace(Literal.SQL + sql.toString());
		SqlParameterSource paramSource = new BeanPropertySqlParameterSource(depositCheques);
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
	public void deleteByMovementId(long movementId, String type) {
		logger.debug(Literal.ENTERING);
		DepositCheques depositCheques = new DepositCheques();
		depositCheques.setMovementId(movementId);

		// Prepare the SQL.
		StringBuilder sql = new StringBuilder("delete from DepositCheques");
		sql.append(type);
		sql.append(" where MovementId = :MovementId");
		// sql.append(QueryUtil.getConcurrencyCondition(tableType));

		// Execute the SQL, binding the arguments.
		logger.trace(Literal.SQL + sql.toString());
		SqlParameterSource paramSource = new BeanPropertySqlParameterSource(depositCheques);

		try {
			this.jdbcTemplate.update(sql.toString(), paramSource);
		} catch (DataAccessException e) {
			throw new DependencyFoundException(e);
		}

		logger.debug(Literal.LEAVING);
	}

	@Override
	public List<DepositCheques> getDepositChequesList(long movementId, String type) {
		logger.debug(Literal.ENTERING);
		List<DepositCheques> list;

		// Prepare the SQL.
		StringBuilder sql = new StringBuilder(
				"SELECT Id, MovementId, ReceiptId, ReceiptMode, Amount, Status, LinkedTranId,");
		sql.append(
				" Version, LastMntOn, LastMntBy,RecordStatus, RoleCode, NextRoleCode, TaskId, NextTaskId, RecordType, WorkflowId");
		if (StringUtils.trimToEmpty(type).contains("View")) {
			sql.append(", Receiptpurpose, FavourNumber, ReceivedDate, FundingAc, Remarks, FinReference,  CustShrtName");
		}
		sql.append(" From DepositCheques");
		sql.append(StringUtils.trimToEmpty(type));
		sql.append(" Where MovementId = :MovementId");

		// Execute the SQL, binding the arguments.
		logger.trace(Literal.SQL + sql.toString());

		DepositCheques depositCheques = new DepositCheques();
		depositCheques.setMovementId(movementId);

		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(depositCheques);
		RowMapper<DepositCheques> rowMapper = BeanPropertyRowMapper.newInstance(DepositCheques.class);
		list = jdbcTemplate.query(sql.toString(), beanParameters, rowMapper);

		logger.debug(Literal.LEAVING);
		return list;
	}

	@Override
	public List<DepositCheques> getDepositChequesList(String branchCode) {
		StringBuilder sql = new StringBuilder("Select");
		sql.append(" ReceiptId, Receiptpurpose, ReceiptMode, Remarks, TransactionRef FavourNumber");
		sql.append(", ReceivedDate, FundingAc, Amount, FinReference, CustShrtName");
		sql.append(" From FinReceiptHeader_DView");
		sql.append(" Where DepositBranch = ?");

		logger.debug(Literal.SQL + sql.toString());

		return jdbcOperations.query(sql.toString(), ps -> ps.setString(1, branchCode), (rs, i) -> {
			DepositCheques dc = new DepositCheques();

			dc.setReceiptId(JdbcUtil.getLong(rs.getLong("ReceiptId")));
			dc.setReceiptpurpose(rs.getString("Receiptpurpose"));
			dc.setReceiptMode(rs.getString("ReceiptMode"));
			dc.setRemarks(rs.getString("Remarks"));
			dc.setFavourNumber(rs.getString("FavourNumber"));
			dc.setReceivedDate(JdbcUtil.getDate(rs.getDate("ReceivedDate")));
			dc.setFundingAc(JdbcUtil.getLong(rs.getLong("FundingAc")));
			dc.setAmount(rs.getBigDecimal("Amount"));
			dc.setFinReference(rs.getString("FinReference"));
			dc.setCustShrtName(rs.getString("CustShrtName"));

			return dc;
		});
	}

	@Override
	public boolean isDuplicateKey(long id, TableType tableType) {
		logger.debug(Literal.ENTERING);

		// Prepare the SQL.
		String sql;
		String whereClause = "Id = :Id";

		switch (tableType) {
		case MAIN_TAB:
			sql = QueryUtil.getCountQuery("DepositCheques", whereClause);
			break;
		case TEMP_TAB:
			sql = QueryUtil.getCountQuery("DepositCheques_Temp", whereClause);
			break;
		default:
			sql = QueryUtil.getCountQuery(new String[] { "DepositCheques_Temp", "DepositCheques" }, whereClause);
			break;
		}

		// Execute the SQL, binding the arguments.
		logger.trace(Literal.SQL + sql);
		MapSqlParameterSource paramSource = new MapSqlParameterSource();
		paramSource.addValue("Id", id);

		Integer count = jdbcTemplate.queryForObject(sql, paramSource, Integer.class);

		boolean exists = false;
		if (count > 0) {
			exists = true;
		}

		logger.debug(Literal.LEAVING);
		return exists;
	}

	@Override
	public DepositCheques getDepositChequeByReceiptID(long receiptID) {
		logger.debug(Literal.ENTERING);

		// Prepare the SQL.
		StringBuilder selectSql = new StringBuilder("SELECT MovementId, Amount, LinkedTranId ");
		selectSql.append(" From DepositCheques");
		selectSql.append(" Where ReceiptId = :ReceiptId ");

		// Execute the SQL, binding the arguments.
		logger.trace(Literal.SQL + selectSql.toString());

		DepositCheques depositCheques = new DepositCheques();
		depositCheques.setReceiptId(receiptID);

		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(depositCheques);
		RowMapper<DepositCheques> rowMapper = BeanPropertyRowMapper.newInstance(DepositCheques.class);

		try {
			return this.jdbcTemplate.queryForObject(selectSql.toString(), beanParameters, rowMapper);
		} catch (EmptyResultDataAccessException e) {
			logger.warn(Message.NO_RECORD_FOUND);
			return null;
		}
	}

	@Override
	public void reverseChequeStatus(long movementId, long receiptID, long linkedTranId) {
		logger.debug(Literal.ENTERING);

		DepositCheques depositCheques = new DepositCheques();
		depositCheques.setReceiptId(receiptID);
		depositCheques.setMovementId(movementId);
		depositCheques.setStatus(CashManagementConstants.DEPOSIT_CHEQUE_STATUS_REVERSE);

		// Prepare the SQL.
		StringBuilder sql = new StringBuilder("update DepositCheques");
		sql.append(" Set Status = :Status");
		sql.append(" Where MovementId = :MovementId AND ReceiptId = :ReceiptId ");

		// Execute the SQL, binding the arguments.
		logger.trace(Literal.SQL + sql.toString());

		SqlParameterSource paramSource = new BeanPropertySqlParameterSource(depositCheques);

		this.jdbcTemplate.update(sql.toString(), paramSource);

		logger.debug(Literal.LEAVING);
	}
}
