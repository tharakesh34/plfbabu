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
 * * FileName : FeeTypeDAOImpl.java * * Author : PENNANT TECHONOLOGIES * * Creation Date : 03-01-2017 * * Modified Date
 * : 03-01-2017 * * Description : * *
 ********************************************************************************************
 * Date Author Version Comments *
 ********************************************************************************************
 * 03-01-2017 PENNANT 0.1 * * * * * * * * *
 ********************************************************************************************
 */

package com.pennant.backend.dao.finance.impl;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.dao.EmptyResultDataAccessException;

import com.pennant.backend.dao.finance.FinMaintainInstructionDAO;
import com.pennant.backend.model.finance.FinMaintainInstruction;
import com.pennanttech.pennapps.core.ConcurrencyException;
import com.pennanttech.pennapps.core.DependencyFoundException;
import com.pennanttech.pennapps.core.jdbc.JdbcUtil;
import com.pennanttech.pennapps.core.jdbc.SequenceDao;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pennapps.core.resource.Message;
import com.pennanttech.pff.core.TableType;
import com.pennanttech.pff.core.util.QueryUtil;

/**
 * Data access layer implementation for <code>FinMaintainInstruction</code> with set of CRUD operations.
 */

public class FinMaintainInstructionDAOImpl extends SequenceDao<FinMaintainInstruction>
		implements FinMaintainInstructionDAO {
	private static Logger logger = LogManager.getLogger(FinMaintainInstructionDAOImpl.class);

	public FinMaintainInstructionDAOImpl() {
		super();
	}

	@Override
	public FinMaintainInstruction getFinMaintainInstructionById(long finMaintainId, String type) {
		StringBuilder sql = getSqlQuery(type);
		sql.append(" Where FinMaintainId = ?");

		logger.debug(Literal.SQL + sql.toString());

		try {
			return this.jdbcOperations.queryForObject(sql.toString(), (rs, i) -> getRowMapper(rs), finMaintainId);
		} catch (EmptyResultDataAccessException e) {
			logger.warn(Message.NO_RECORD_FOUND);
			return null;
		}
	}

	@Override
	public FinMaintainInstruction getFinMaintainInstructionByFinRef(long finID, String event, String type) {
		StringBuilder sql = getSqlQuery(type);
		sql.append(" Where FinID = ? and Event = ?");

		logger.debug(Literal.SQL + sql.toString());

		try {
			return this.jdbcOperations.queryForObject(sql.toString(), (rs, i) -> getRowMapper(rs), finID, event);
		} catch (EmptyResultDataAccessException e) {
			logger.warn(Message.NO_RECORD_FOUND);
			return null;
		}
	}

	private StringBuilder getSqlQuery(String type) {
		StringBuilder sql = new StringBuilder("Select");
		sql.append(" FinMaintainId, FinID, FinReference, Event, TDSApplicable, TdsPercentage, TdsStartDate");
		sql.append(", TdsEndDate, TdsLimit, Version, LastMntOn, LastMntBy, RecordStatus");
		sql.append(", RoleCode, NextRoleCode, TaskId, NextTaskId, RecordType, WorkflowId");
		sql.append(" From FinMaintainInstructions");
		sql.append(StringUtils.trimToEmpty(type));

		return sql;
	}

	private FinMaintainInstruction getRowMapper(ResultSet rs) throws SQLException {
		FinMaintainInstruction fmi = new FinMaintainInstruction();

		fmi.setFinMaintainId(rs.getLong("FinMaintainId"));
		fmi.setFinID(rs.getLong("FinID"));
		fmi.setFinReference(rs.getString("FinReference"));
		fmi.setEvent(rs.getString("Event"));
		fmi.settDSApplicable(rs.getBoolean("TDSApplicable"));
		fmi.setTdsPercentage(rs.getBigDecimal("TdsPercentage"));
		fmi.setTdsStartDate(JdbcUtil.getDate(rs.getDate("TdsStartDate")));
		fmi.setTdsEndDate(JdbcUtil.getDate(rs.getDate("TdsEndDate")));
		fmi.setTdsLimit(rs.getBigDecimal("TdsLimit"));
		fmi.setVersion(rs.getInt("Version"));
		fmi.setLastMntOn(rs.getTimestamp("LastMntOn"));
		fmi.setLastMntBy(rs.getLong("LastMntBy"));
		fmi.setRecordStatus(rs.getString("RecordStatus"));
		fmi.setRoleCode(rs.getString("RoleCode"));
		fmi.setNextRoleCode(rs.getString("NextRoleCode"));
		fmi.setTaskId(rs.getString("TaskId"));
		fmi.setNextTaskId(rs.getString("NextTaskId"));
		fmi.setRecordType(rs.getString("RecordType"));
		fmi.setWorkflowId(rs.getLong("WorkflowId"));

		return fmi;
	}

	@Override
	public boolean isDuplicateKey(String event, long finID, TableType tableType) {
		String sql;
		String whereClause = "Event = ? and FinID = ?";

		Object[] args = new Object[] { event, finID };

		switch (tableType) {
		case MAIN_TAB:
			sql = QueryUtil.getCountQuery("FinMaintainInstructions", whereClause);
			break;
		case TEMP_TAB:
			sql = QueryUtil.getCountQuery("FinMaintainInstructions_Temp", whereClause);
			break;
		default:
			sql = QueryUtil.getCountQuery(new String[] { "FinMaintainInstructions_Temp", "FinMaintainInstructions" },
					whereClause);

			args = new Object[] { event, finID, event, finID };
			break;
		}

		logger.debug(Literal.SQL + sql);

		return jdbcOperations.queryForObject(sql, Integer.class, args) > 0;
	}

	@Override
	public String save(FinMaintainInstruction fmi, TableType tableType) {
		if (fmi.getFinMaintainId() == Long.MIN_VALUE) {
			fmi.setFinMaintainId(getNextValue("SeqFinMaintainInstructions"));
		}

		StringBuilder sql = new StringBuilder("Insert into FinMaintainInstructions");
		sql.append(tableType.getSuffix());
		sql.append(" (FinMaintainId, FinID, FinReference, Event, TDSApplicable, TdsPercentage, TdsStartDate");
		sql.append(", TdsEndDate, TdsLimit, Version , LastMntBy, LastMntOn, RecordStatus, RoleCode");
		sql.append(", NextRoleCode, TaskId, NextTaskId, RecordType, WorkflowId)");
		sql.append(" Values(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)");

		logger.debug(Literal.SQL + sql.toString());

		try {
			jdbcOperations.update(sql.toString(), ps -> {
				int index = 1;

				ps.setLong(index++, fmi.getFinMaintainId());
				ps.setLong(index++, fmi.getFinID());
				ps.setString(index++, fmi.getFinReference());
				ps.setString(index++, fmi.getEvent());
				ps.setBoolean(index++, fmi.istDSApplicable());
				ps.setBigDecimal(index++, fmi.getTdsPercentage());
				ps.setDate(index++, JdbcUtil.getDate(fmi.getTdsStartDate()));
				ps.setDate(index++, JdbcUtil.getDate(fmi.getTdsEndDate()));
				ps.setBigDecimal(index++, fmi.getTdsLimit());
				ps.setInt(index++, fmi.getVersion());
				ps.setLong(index++, fmi.getLastMntBy());
				ps.setTimestamp(index++, fmi.getLastMntOn());
				ps.setString(index++, fmi.getRecordStatus());
				ps.setString(index++, fmi.getRoleCode());
				ps.setString(index++, fmi.getNextRoleCode());
				ps.setString(index++, fmi.getTaskId());
				ps.setString(index++, fmi.getNextTaskId());
				ps.setString(index++, fmi.getRecordType());
				ps.setLong(index, fmi.getWorkflowId());

			});
		} catch (DuplicateKeyException e) {
			throw new ConcurrencyException(e);
		}

		return String.valueOf(fmi.getFinMaintainId());
	}

	@Override
	public void update(FinMaintainInstruction fmi, TableType tableType) {
		StringBuilder sql = new StringBuilder("Update FinMaintainInstructions");
		sql.append(tableType.getSuffix());
		sql.append(" Set FinID = ?, FinReference = ?, Event = ?, TDSApplicable = ?, TdsPercentage = ?");
		sql.append(", TdsStartDate = ?, TdsEndDate = ?, TdsLimit = ?, Version = ?");
		sql.append(", LastMntBy = ?, LastMntOn = ?, RecordStatus= ?, RoleCode = ?");
		sql.append(", NextRoleCode = ?, TaskId = ?, NextTaskId = ?, RecordType = ?, WorkflowId = ?");
		sql.append(" Where FinMaintainId = ?");

		logger.debug(Literal.SQL + sql.toString());

		int recordCount = jdbcOperations.update(sql.toString(), ps -> {
			int index = 1;

			ps.setLong(index++, fmi.getFinID());
			ps.setString(index++, fmi.getFinReference());
			ps.setString(index++, fmi.getEvent());
			ps.setBoolean(index++, fmi.istDSApplicable());
			ps.setBigDecimal(index++, fmi.getTdsPercentage());
			ps.setDate(index++, JdbcUtil.getDate(fmi.getTdsStartDate()));
			ps.setDate(index++, JdbcUtil.getDate(fmi.getTdsEndDate()));
			ps.setBigDecimal(index++, fmi.getTdsLimit());
			ps.setInt(index++, fmi.getVersion());
			ps.setLong(index++, fmi.getLastMntBy());
			ps.setTimestamp(index++, fmi.getLastMntOn());
			ps.setString(index++, fmi.getRecordStatus());
			ps.setString(index++, fmi.getRoleCode());
			ps.setString(index++, fmi.getNextRoleCode());
			ps.setString(index++, fmi.getTaskId());
			ps.setString(index++, fmi.getNextTaskId());
			ps.setString(index++, fmi.getRecordType());
			ps.setLong(index++, fmi.getWorkflowId());

			ps.setLong(index, fmi.getFinMaintainId());
		});

		if (recordCount == 0) {
			throw new ConcurrencyException();
		}
	}

	@Override
	public void delete(FinMaintainInstruction fmi, TableType tableType) {
		StringBuilder sql = new StringBuilder("Delete From FinMaintainInstructions");
		sql.append(tableType.getSuffix());
		sql.append(" Where FinMaintainId = ?");

		logger.debug(Literal.SQL + sql.toString());

		try {
			int recordCount = jdbcOperations.update(sql.toString(), ps -> ps.setLong(1, fmi.getFinMaintainId()));

			if (recordCount == 0) {
				throw new ConcurrencyException();
			}
		} catch (DataAccessException e) {
			throw new DependencyFoundException(e);
		}
	}

}