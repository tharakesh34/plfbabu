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
 * * FileName : ChequeHeaderDAOImpl.java * * Author : PENNANT TECHONOLOGIES * * Creation Date : 27-11-2017 * * Modified
 * Date : 27-11-2017 * * Description : * *
 ********************************************************************************************
 * Date Author Version Comments *
 ********************************************************************************************
 * 27-11-2017 PENNANT 0.1 * * * * * * * * *
 ********************************************************************************************
 */
package com.pennant.backend.dao.pdc.impl;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.dao.EmptyResultDataAccessException;

import com.pennant.backend.dao.pdc.ChequeHeaderDAO;
import com.pennant.backend.model.finance.ChequeHeader;
import com.pennant.backend.model.mandate.Mandate;
import com.pennanttech.pennapps.core.ConcurrencyException;
import com.pennanttech.pennapps.core.DependencyFoundException;
import com.pennanttech.pennapps.core.jdbc.SequenceDao;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pennapps.core.resource.Message;
import com.pennanttech.pff.core.TableType;
import com.pennanttech.pff.core.util.QueryUtil;

/**
 * Data access layer implementation for <code>ChequeHeader</code> with set of CRUD operations.
 */
public class ChequeHeaderDAOImpl extends SequenceDao<Mandate> implements ChequeHeaderDAO {
	private static Logger logger = LogManager.getLogger(ChequeHeaderDAOImpl.class);

	public ChequeHeaderDAOImpl() {
		super();
	}

	private StringBuilder getSqlQuery(String type) {
		StringBuilder sql = new StringBuilder("Select");
		sql.append(" HeaderID, FinID, FinReference, NoOfCheques, TotalAmount");
		sql.append(", Active, Version, LastMntOn, LastMntBy,RecordStatus");
		sql.append(", RoleCode, NextRoleCode, TaskId, NextTaskId, RecordType, WorkflowId");
		sql.append(" From ChequeHeader");
		sql.append(type);

		return sql;
	}

	private ChequeHeader getRowMapper(ResultSet rs) throws SQLException {
		ChequeHeader ch = new ChequeHeader();

		ch.setHeaderID(rs.getLong("HeaderID"));
		ch.setFinID(rs.getLong("FinID"));
		ch.setFinReference(rs.getString("FinReference"));
		ch.setNoOfCheques(rs.getInt("NoOfCheques"));
		ch.setTotalAmount(rs.getBigDecimal("TotalAmount"));
		ch.setActive(rs.getBoolean("Active"));
		ch.setVersion(rs.getInt("Version"));
		ch.setLastMntOn(rs.getTimestamp("LastMntOn"));
		ch.setLastMntBy(rs.getLong("LastMntBy"));
		ch.setRecordStatus(rs.getString("RecordStatus"));
		ch.setRoleCode(rs.getString("RoleCode"));
		ch.setNextRoleCode(rs.getString("NextRoleCode"));
		ch.setTaskId(rs.getString("TaskId"));
		ch.setNextTaskId(rs.getString("NextTaskId"));
		ch.setRecordType(rs.getString("RecordType"));
		ch.setWorkflowId(rs.getLong("WorkflowId"));

		return ch;
	}

	@Override
	public ChequeHeader getChequeHeader(long headerID, String type) {
		StringBuilder sql = getSqlQuery(type);
		sql.append(" Where HeaderID = ?");

		logger.debug(Literal.SQL + sql.toString());

		try {
			return jdbcOperations.queryForObject(sql.toString(), (rs, rowNum) -> {
				return getRowMapper(rs);
			}, headerID);
		} catch (EmptyResultDataAccessException e) {
			logger.warn(Message.NO_RECORD_FOUND);
			return null;
		}
	}

	@Override
	public ChequeHeader getChequeHeaderByRef(long finID, String type) {
		StringBuilder sql = getSqlQuery(type);
		sql.append(" Where FinID = ?");

		logger.debug(Literal.SQL + sql.toString());

		try {
			return jdbcOperations.queryForObject(sql.toString(), (rs, rowNum) -> {
				return getRowMapper(rs);
			}, finID);
		} catch (EmptyResultDataAccessException e) {
			logger.warn(Message.NO_RECORD_FOUND);
			return null;
		}
	}

	@Override
	public String save(ChequeHeader ch, TableType tableType) {
		if (ch.getId() == Long.MIN_VALUE || ch.getId() == 0) {
			ch.setId(getNextValue("SeqChequeHeader"));
		}

		StringBuilder sql = new StringBuilder("Insert Into ChequeHeader");
		sql.append(tableType.getSuffix());
		sql.append("(HeaderID, FinID, FinReference, NoOfCheques, TotalAmount");
		sql.append(", Active, Version, LastMntOn, LastMntBy,RecordStatus");
		sql.append(", RoleCode, NextRoleCode, TaskId, NextTaskId, RecordType, WorkflowId)");
		sql.append(" Values(");
		sql.append(" ?, ?, ?, ?, ?, ?, ? , ?, ?, ?, ?, ?, ?, ?, ?, ?)");

		logger.debug(Literal.SQL + sql.toString());

		try {
			jdbcOperations.update(sql.toString(), ps -> {
				int index = 1;

				ps.setLong(index++, ch.getHeaderID());
				ps.setLong(index++, ch.getFinID());
				ps.setString(index++, ch.getFinReference());
				ps.setInt(index++, ch.getNoOfCheques());
				ps.setBigDecimal(index++, ch.getTotalAmount());
				ps.setBoolean(index++, ch.isActive());
				ps.setInt(index++, ch.getVersion());
				ps.setTimestamp(index++, ch.getLastMntOn());
				ps.setLong(index++, ch.getLastMntBy());
				ps.setString(index++, ch.getRecordStatus());
				ps.setString(index++, ch.getRoleCode());
				ps.setString(index++, ch.getNextRoleCode());
				ps.setString(index++, ch.getTaskId());
				ps.setString(index++, ch.getNextTaskId());
				ps.setString(index++, ch.getRecordType());
				ps.setLong(index++, ch.getWorkflowId());
			});
		} catch (DuplicateKeyException e) {
			throw new ConcurrencyException(e);
		}

		return String.valueOf(ch.getHeaderID());
	}

	@Override
	public void update(ChequeHeader ch, TableType tableType) {
		StringBuilder sql = new StringBuilder("Update ChequeHeader");
		sql.append(tableType.getSuffix());
		sql.append(" Set FinID = ?, FinReference = ?, NoOfCheques = ?, TotalAmount = ?");
		sql.append(", Active = ?, LastMntOn = ?, LastMntBy = ?, RecordStatus = ?, RoleCode = ?");
		sql.append(", NextRoleCode = ?, TaskId = ?, NextTaskId = ?, RecordType = ?, WorkflowId = ?");
		sql.append(" Where HeaderID = ?");

		logger.debug(Literal.SQL + sql.toString());

		int recordCount = jdbcOperations.update(sql.toString(), ps -> {
			int index = 1;

			ps.setLong(index++, ch.getFinID());
			ps.setString(index++, ch.getFinReference());
			ps.setInt(index++, ch.getNoOfCheques());
			ps.setBigDecimal(index++, ch.getTotalAmount());
			ps.setBoolean(index++, ch.isActive());
			ps.setTimestamp(index++, ch.getLastMntOn());
			ps.setLong(index++, ch.getLastMntBy());
			ps.setString(index++, ch.getRecordStatus());
			ps.setString(index++, ch.getRoleCode());
			ps.setString(index++, ch.getNextRoleCode());
			ps.setString(index++, ch.getTaskId());
			ps.setString(index++, ch.getNextTaskId());
			ps.setString(index++, ch.getRecordType());
			ps.setLong(index++, ch.getWorkflowId());

			ps.setLong(index++, ch.getHeaderID());
		});

		if (recordCount == 0) {
			throw new ConcurrencyException();
		}

	}

	@Override
	public void delete(ChequeHeader chequeHeader, TableType tableType) {
		StringBuilder sql = new StringBuilder("Delete from ChequeHeader");
		sql.append(tableType.getSuffix());
		sql.append(" Where HeaderID = ?");

		logger.debug(Literal.SQL + sql.toString());

		int recordCount = 0;
		try {
			recordCount = jdbcOperations.update(sql.toString(), ps -> {
				ps.setLong(1, chequeHeader.getHeaderID());
			});
		} catch (DataAccessException e) {
			throw new DependencyFoundException(e);
		}

		if (recordCount == 0) {
			throw new ConcurrencyException();
		}
	}

	@Override
	public void deleteByFinRef(long finID, TableType tableType) {
		StringBuilder sql = new StringBuilder("Delete from CHEQUEHEADER");
		sql.append(tableType.getSuffix());
		sql.append(" Where FinID = ?");

		logger.debug(Literal.SQL + sql.toString());

		int recordCount = 0;

		try {
			recordCount = jdbcOperations.update(sql.toString(), ps -> {
				ps.setLong(1, finID);
			});
		} catch (DataAccessException e) {
			throw new DependencyFoundException(e);
		}

		if (recordCount == 0) {
			throw new ConcurrencyException();
		}
	}

	@Override
	public boolean isDuplicateKey(long headerID, long finID, TableType tableType) {
		String sql;
		String whereClause = "HeaderID != ? and FinID = ?";
		Object[] object = new Object[] {};

		switch (tableType) {
		case MAIN_TAB:
			sql = QueryUtil.getCountQuery("ChequeHeader", whereClause);
			object = new Object[] { headerID, finID };
			break;
		case TEMP_TAB:
			sql = QueryUtil.getCountQuery("ChequeHeader_Temp", whereClause);
			object = new Object[] { headerID, finID };
			break;
		default:
			sql = QueryUtil.getCountQuery(new String[] { "ChequeHeader_Temp", "ChequeHeader" }, whereClause);
			object = new Object[] { headerID, finID, headerID, finID };
			break;
		}

		logger.debug(Literal.SQL + sql);
		return jdbcOperations.queryForObject(sql, Integer.class, object) > 0;
	}

	@Override
	public boolean isChequeDetilsExists(long finID) {
		String sql = "Select count(HeaderID) From ChequeHeader Where FinID = ?";

		logger.debug(Literal.SQL + sql);
		return this.jdbcOperations.queryForObject(sql, Integer.class, finID) > 0;
	}
}
