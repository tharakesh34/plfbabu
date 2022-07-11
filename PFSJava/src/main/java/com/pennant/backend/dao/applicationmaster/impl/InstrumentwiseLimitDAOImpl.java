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
 * * FileName : InstrumentwiseLimitDAOImpl.java * * Author : PENNANT TECHONOLOGIES * * Creation Date : 18-01-2018 * *
 * Modified Date : 18-01-2018 * * Description : * *
 ********************************************************************************************
 * Date Author Version Comments *
 ********************************************************************************************
 * 18-01-2018 PENNANT 0.1 * * * * * * * * *
 ********************************************************************************************
 */
package com.pennant.backend.dao.applicationmaster.impl;

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

import com.pennant.backend.dao.applicationmaster.InstrumentwiseLimitDAO;
import com.pennant.backend.model.applicationmaster.InstrumentwiseLimit;
import com.pennanttech.pennapps.core.ConcurrencyException;
import com.pennanttech.pennapps.core.DependencyFoundException;
import com.pennanttech.pennapps.core.jdbc.SequenceDao;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pennapps.core.resource.Message;
import com.pennanttech.pff.core.TableType;
import com.pennanttech.pff.core.util.QueryUtil;

/**
 * Data access layer implementation for <code>InstrumentwiseLimit</code> with set of CRUD operations.
 */
public class InstrumentwiseLimitDAOImpl extends SequenceDao<InstrumentwiseLimit> implements InstrumentwiseLimitDAO {
	private static Logger logger = LogManager.getLogger(InstrumentwiseLimitDAOImpl.class);

	public InstrumentwiseLimitDAOImpl() {
		super();
	}

	@Override
	public InstrumentwiseLimit getInstrumentwiseLimit(long id, String type) {
		logger.debug(Literal.ENTERING);

		// Prepare the SQL.
		StringBuilder sql = new StringBuilder("SELECT ");
		sql.append(
				" id, instrumentMode, paymentMinAmtperTrans, paymentMaxAmtperTran, paymentMaxAmtperDay, receiptMinAmtperTran, ");
		sql.append(" receiptMaxAmtperTran, receiptMaxAmtperDay, MaxAmtPerInstruction,");
		sql.append(
				" Version, LastMntOn, LastMntBy,RecordStatus, RoleCode, NextRoleCode, TaskId, NextTaskId, RecordType, WorkflowId");
		sql.append(" From InstrumentwiseLimit");
		sql.append(type);
		sql.append(" Where id = :id");

		// Execute the SQL, binding the arguments.
		logger.trace(Literal.SQL + sql.toString());

		InstrumentwiseLimit instrumentwiseLimit = new InstrumentwiseLimit();
		instrumentwiseLimit.setId(id);

		SqlParameterSource paramSource = new BeanPropertySqlParameterSource(instrumentwiseLimit);
		RowMapper<InstrumentwiseLimit> rowMapper = BeanPropertyRowMapper.newInstance(InstrumentwiseLimit.class);

		try {
			return jdbcTemplate.queryForObject(sql.toString(), paramSource, rowMapper);
		} catch (EmptyResultDataAccessException e) {
			logger.warn(Message.NO_RECORD_FOUND);
			return null;
		}
	}

	@Override
	public String save(InstrumentwiseLimit instrumentwiseLimit, TableType tableType) {
		logger.debug(Literal.ENTERING);

		// Prepare the SQL.
		StringBuilder sql = new StringBuilder(" insert into InstrumentwiseLimit");
		sql.append(tableType.getSuffix());
		sql.append(
				"(id, instrumentMode, paymentMinAmtperTrans, paymentMaxAmtperTran, paymentMaxAmtperDay, receiptMinAmtperTran, ");
		sql.append(" receiptMaxAmtperTran, receiptMaxAmtperDay, MaxAmtPerInstruction,");
		sql.append(
				" Version , LastMntBy, LastMntOn, RecordStatus, RoleCode, NextRoleCode, TaskId, NextTaskId, RecordType, WorkflowId)");
		sql.append(" values(");
		sql.append(
				" :id, :instrumentMode, :paymentMinAmtperTrans, :paymentMaxAmtperTran, :paymentMaxAmtperDay, :receiptMinAmtperTran, ");
		sql.append(" :receiptMaxAmtperTran, :receiptMaxAmtperDay, :MaxAmtPerInstruction,");
		sql.append(
				" :Version , :LastMntBy, :LastMntOn, :RecordStatus, :RoleCode, :NextRoleCode, :TaskId, :NextTaskId, :RecordType, :WorkflowId)");

		// Get the identity sequence number.
		if (instrumentwiseLimit.getId() <= 0) {
			instrumentwiseLimit.setId(getNextValue("SeqInstrumentwiseLimit"));
		}

		// Execute the SQL, binding the arguments.
		logger.trace(Literal.SQL + sql.toString());
		SqlParameterSource paramSource = new BeanPropertySqlParameterSource(instrumentwiseLimit);

		try {
			jdbcTemplate.update(sql.toString(), paramSource);
		} catch (DuplicateKeyException e) {
			throw new ConcurrencyException(e);
		}

		logger.debug(Literal.LEAVING);
		return String.valueOf(instrumentwiseLimit.getId());
	}

	@Override
	public void update(InstrumentwiseLimit instrumentwiseLimit, TableType tableType) {
		logger.debug(Literal.ENTERING);

		// Prepare the SQL.
		StringBuilder sql = new StringBuilder("update InstrumentwiseLimit");
		sql.append(tableType.getSuffix());
		sql.append(
				" Set instrumentMode = :instrumentMode, paymentMinAmtperTrans = :paymentMinAmtperTrans, paymentMaxAmtperTran = :paymentMaxAmtperTran, ");
		sql.append(
				" paymentMaxAmtperDay = :paymentMaxAmtperDay, receiptMinAmtperTran = :receiptMinAmtperTran, receiptMaxAmtperTran = :receiptMaxAmtperTran, ");
		sql.append(" receiptMaxAmtperDay = :receiptMaxAmtperDay, MaxAmtPerInstruction = :MaxAmtPerInstruction,");
		sql.append(" LastMntOn = :LastMntOn, RecordStatus = :RecordStatus, RoleCode = :RoleCode,");
		sql.append(" NextRoleCode = :NextRoleCode, TaskId = :TaskId, NextTaskId = :NextTaskId,");
		sql.append(" RecordType = :RecordType, WorkflowId = :WorkflowId");
		sql.append(" where id = :id ");
		sql.append(QueryUtil.getConcurrencyCondition(tableType));

		// Execute the SQL, binding the arguments.
		logger.trace(Literal.SQL + sql.toString());

		SqlParameterSource paramSource = new BeanPropertySqlParameterSource(instrumentwiseLimit);
		int recordCount = jdbcTemplate.update(sql.toString(), paramSource);

		// Check for the concurrency failure.
		if (recordCount == 0) {
			throw new ConcurrencyException();
		}

		logger.debug(Literal.LEAVING);
	}

	@Override
	public void delete(InstrumentwiseLimit instrumentwiseLimit, TableType tableType) {
		logger.debug(Literal.ENTERING);

		// Prepare the SQL.
		StringBuilder sql = new StringBuilder("delete from InstrumentwiseLimit");
		sql.append(tableType.getSuffix());
		sql.append(" where id = :id ");
		sql.append(QueryUtil.getConcurrencyCondition(tableType));

		// Execute the SQL, binding the arguments.
		logger.trace(Literal.SQL + sql.toString());
		SqlParameterSource paramSource = new BeanPropertySqlParameterSource(instrumentwiseLimit);
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
	public boolean isDuplicateKey(long id, String instrumentMode, TableType tableType) {
		logger.debug(Literal.ENTERING);

		// Prepare the SQL.
		String sql;
		String whereClause = "instrumentMode = :instrumentMode AND id != :id";

		switch (tableType) {
		case MAIN_TAB:
			sql = QueryUtil.getCountQuery("InstrumentwiseLimit", whereClause);
			break;
		case TEMP_TAB:
			sql = QueryUtil.getCountQuery("InstrumentwiseLimit_Temp", whereClause);
			break;
		default:
			sql = QueryUtil.getCountQuery(new String[] { "InstrumentwiseLimit_Temp", "InstrumentwiseLimit" },
					whereClause);
			break;
		}
		// Execute the SQL, binding the arguments.
		logger.trace(Literal.SQL + sql);
		MapSqlParameterSource paramSource = new MapSqlParameterSource();
		paramSource.addValue("id", id);
		paramSource.addValue("instrumentMode", instrumentMode);

		Integer count = jdbcTemplate.queryForObject(sql, paramSource, Integer.class);
		boolean exists = false;
		if (count > 0) {
			exists = true;
		}

		logger.debug(Literal.LEAVING);
		return exists;
	}

	@Override
	public InstrumentwiseLimit getInstrumentWiseModeLimit(String instrumentMode, String type) {
		StringBuilder sql = new StringBuilder("Select");
		sql.append(" Id, InstrumentMode, PaymentMinAmtperTrans, PaymentMaxAmtperTran, PaymentMaxAmtperDay");
		sql.append(", ReceiptMinAmtperTran, ReceiptMaxAmtperTran, ReceiptMaxAmtperDay, MaxAmtPerInstruction");
		sql.append(", Version, LastMntOn, LastMntBy, RecordStatus, RoleCode, NextRoleCode, TaskId");
		sql.append(", NextTaskId, RecordType, WorkflowId");
		sql.append(" from InstrumentwiseLimit");
		sql.append(StringUtils.trimToEmpty(type));
		sql.append(" Where InstrumentMode = ?");

		logger.trace(Literal.SQL + sql.toString());

		try {
			return this.jdbcOperations.queryForObject(sql.toString(), (rs, rowNum) -> {
				InstrumentwiseLimit iwl = new InstrumentwiseLimit();

				iwl.setId(rs.getLong("Id"));
				iwl.setInstrumentMode(rs.getString("InstrumentMode"));
				iwl.setPaymentMinAmtperTrans(rs.getBigDecimal("PaymentMinAmtperTrans"));
				iwl.setPaymentMaxAmtperTran(rs.getBigDecimal("PaymentMaxAmtperTran"));
				iwl.setPaymentMaxAmtperDay(rs.getBigDecimal("PaymentMaxAmtperDay"));
				iwl.setReceiptMinAmtperTran(rs.getBigDecimal("ReceiptMinAmtperTran"));
				iwl.setReceiptMaxAmtperTran(rs.getBigDecimal("ReceiptMaxAmtperTran"));
				iwl.setReceiptMaxAmtperDay(rs.getBigDecimal("ReceiptMaxAmtperDay"));
				iwl.setMaxAmtPerInstruction(rs.getBigDecimal("MaxAmtPerInstruction"));
				iwl.setVersion(rs.getInt("Version"));
				iwl.setLastMntOn(rs.getTimestamp("LastMntOn"));
				iwl.setLastMntBy(rs.getLong("LastMntBy"));
				iwl.setRecordStatus(rs.getString("RecordStatus"));
				iwl.setRoleCode(rs.getString("RoleCode"));
				iwl.setNextRoleCode(rs.getString("NextRoleCode"));
				iwl.setTaskId(rs.getString("TaskId"));
				iwl.setNextTaskId(rs.getString("NextTaskId"));
				iwl.setRecordType(rs.getString("RecordType"));
				iwl.setWorkflowId(rs.getLong("WorkflowId"));

				return iwl;
			}, instrumentMode);
		} catch (EmptyResultDataAccessException e) {
			logger.warn(Message.NO_RECORD_FOUND);
			return null;
		}
	}

	@Override
	public InstrumentwiseLimit getInstrumentForLMSEvent(String instrumentMode) {
		String sql = "Select ReceiptMinAmtperTran, ReceiptMaxAmtperTran, ReceiptMaxAmtperDay From InstrumentwiseLimit Where InstrumentMode = ?";

		logger.debug(Literal.SQL + sql);

		try {
			return this.jdbcOperations.queryForObject(sql, (rs, rowNum) -> {
				InstrumentwiseLimit iwl = new InstrumentwiseLimit();

				iwl.setReceiptMinAmtperTran(rs.getBigDecimal("ReceiptMinAmtperTran"));
				iwl.setReceiptMaxAmtperTran(rs.getBigDecimal("ReceiptMaxAmtperTran"));
				iwl.setReceiptMaxAmtperDay(rs.getBigDecimal("ReceiptMaxAmtperDay"));

				return iwl;
			}, instrumentMode);
		} catch (EmptyResultDataAccessException e) {
			logger.warn(Message.NO_RECORD_FOUND);
			return null;
		}
	}
}
