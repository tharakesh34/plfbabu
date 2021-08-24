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
 * * FileName : FeeWaiverHeaderDAOImpl.java * * Author : PENNANT TECHONOLOGIES * * Creation Date : 27-11-2017 * *
 * Modified Date : * * Description : * *
 ********************************************************************************************
 * Date Author Version Comments *
 ********************************************************************************************
 * 27-11-2017 PENNANT 0.1 * * * * * * * * *
 ********************************************************************************************
 */
package com.pennant.backend.dao.finance.impl;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;

import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.RowMapper;

import com.pennant.backend.dao.finance.FeeWaiverHeaderDAO;
import com.pennant.backend.model.finance.FeeWaiverHeader;
import com.pennanttech.pennapps.core.ConcurrencyException;
import com.pennanttech.pennapps.core.DependencyFoundException;
import com.pennanttech.pennapps.core.jdbc.JdbcUtil;
import com.pennanttech.pennapps.core.jdbc.SequenceDao;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pff.core.TableType;

/**
 * Data access layer implementation for <code>FeeWaiverHeader</code> with set of CRUD operations.
 */

public class FeeWaiverHeaderDAOImpl extends SequenceDao<FeeWaiverHeader> implements FeeWaiverHeaderDAO {
	private static Logger logger = LogManager.getLogger(FeeWaiverHeaderDAOImpl.class);

	public FeeWaiverHeaderDAOImpl() {
		super();
	}

	@Override
	public FeeWaiverHeader getFeeWaiverHeaderById(long waiverId, String type) {
		StringBuilder sql = getSelectQuery(type);
		sql.append(" Where WaiverId = ?");

		logger.debug(Literal.SQL + sql.toString());

		try {
			return jdbcOperations.queryForObject(sql.toString(), new FeeWaiverHeaderRowMapper(), waiverId);
		} catch (EmptyResultDataAccessException e) {
			//
		}

		return null;
	}

	@Override
	public FeeWaiverHeader getFeeWaiverHeaderByFinRef(long finID, String type) {
		StringBuilder sql = getSelectQuery(type);
		sql.append(" Where FinID = ?");

		logger.debug(Literal.SQL + sql.toString());

		try {
			return jdbcOperations.queryForObject(sql.toString(), new FeeWaiverHeaderRowMapper(), finID);
		} catch (EmptyResultDataAccessException e) {
			//
		}

		return null;
	}

	@Override
	public Date getLastWaiverDate(long finID, Date appDate, Date receiptDate) {
		String sql = "Select max(ValueDate) From FeeWaiverHeader Where FinID = ? and ValueDate >= ? and ValueDate < ?";

		logger.debug(Literal.SQL + sql);

		try {
			return this.jdbcOperations.queryForObject(sql, Date.class, finID, receiptDate, appDate);
		} catch (EmptyResultDataAccessException e) {
			//
		}

		return null;
	}

	@Override
	public String save(FeeWaiverHeader fwh, TableType tableType) {
		StringBuilder sql = new StringBuilder("Insert into FeeWaiverHeader");
		sql.append(tableType.getSuffix());
		sql.append(" (WaiverId, FinID, FinReference, Event, Remarks, PostingDate, ValueDate");
		sql.append(", Version , LastMntBy, LastMntOn, RecordStatus, RoleCode, NextRoleCode");
		sql.append(", TaskId, NextTaskId, RecordType, WorkflowId)");
		sql.append(" values(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)");

		if (fwh.getWaiverId() == Long.MIN_VALUE) {
			fwh.setWaiverId(getNextValue("SeqFeeWaiverHeader"));
		}

		logger.debug(Literal.SQL + sql.toString());

		try {
			jdbcOperations.update(sql.toString(), ps -> {
				int index = 1;
				ps.setLong(index++, fwh.getWaiverId());
				ps.setLong(index++, fwh.getFinID());
				ps.setString(index++, fwh.getFinReference());
				ps.setString(index++, fwh.getEvent());
				ps.setString(index++, fwh.getRemarks());
				ps.setDate(index++, JdbcUtil.getDate(fwh.getPostingDate()));
				ps.setDate(index++, JdbcUtil.getDate(fwh.getValueDate()));
				ps.setInt(index++, fwh.getVersion());
				ps.setLong(index++, fwh.getLastMntBy());
				ps.setTimestamp(index++, fwh.getLastMntOn());
				ps.setString(index++, fwh.getRecordStatus());
				ps.setString(index++, fwh.getRoleCode());
				ps.setString(index++, fwh.getNextRoleCode());
				ps.setString(index++, fwh.getTaskId());
				ps.setString(index++, fwh.getNextTaskId());
				ps.setString(index++, fwh.getRecordType());
				ps.setLong(index++, fwh.getWorkflowId());

			});
		} catch (DuplicateKeyException e) {
			throw new ConcurrencyException(e);
		}

		return String.valueOf(fwh.getWaiverId());
	}

	@Override
	public void update(FeeWaiverHeader fwh, TableType tableType) {
		StringBuilder sql = new StringBuilder("Update FeeWaiverHeader");
		sql.append(tableType.getSuffix());
		sql.append(" Set FinID = ?, FinReference = ?, Event = ?, Remarks = ?, PostingDate = ?, ValueDate = ?");
		sql.append(", Version= ?, LastMntBy = ?, LastMntOn = ?, RecordStatus= ?, RoleCode = ?, NextRoleCode = ?");
		sql.append(", TaskId = ?, NextTaskId = ?, RecordType = ?, WorkflowId = ?");
		sql.append(" Where WaiverId = ?");

		logger.debug(Literal.SQL + sql.toString());

		int recordCount = jdbcOperations.update(sql.toString(), ps -> {
			int index = 1;

			ps.setLong(index++, fwh.getFinID());
			ps.setString(index++, fwh.getFinReference());
			ps.setString(index++, fwh.getEvent());
			ps.setString(index++, fwh.getRemarks());
			ps.setDate(index++, JdbcUtil.getDate(fwh.getPostingDate()));
			ps.setDate(index++, JdbcUtil.getDate(fwh.getValueDate()));
			ps.setInt(index++, fwh.getVersion());
			ps.setLong(index++, fwh.getLastMntBy());
			ps.setTimestamp(index++, fwh.getLastMntOn());
			ps.setString(index++, fwh.getRecordStatus());
			ps.setString(index++, fwh.getRoleCode());
			ps.setString(index++, fwh.getNextRoleCode());
			ps.setString(index++, fwh.getTaskId());
			ps.setString(index++, fwh.getNextTaskId());
			ps.setString(index++, fwh.getRecordType());
			ps.setLong(index++, fwh.getWorkflowId());
			ps.setLong(index++, fwh.getWaiverId());

		});

		if (recordCount == 0) {
			throw new ConcurrencyException();
		}
	}

	@Override
	public void delete(FeeWaiverHeader fwh, TableType tableType) {
		StringBuilder sql = new StringBuilder("Delete From FeeWaiverHeader");
		sql.append(tableType.getSuffix());
		sql.append(" Where WaiverId = ?");

		logger.debug(Literal.SQL + sql.toString());

		int recordCount = 0;

		try {
			recordCount = jdbcOperations.update(sql.toString(), fwh.getWaiverId());
		} catch (DataAccessException e) {
			throw new DependencyFoundException(e);
		}

		if (recordCount == 0) {
			throw new ConcurrencyException();
		}
	}

	private StringBuilder getSelectQuery(String type) {
		StringBuilder sql = new StringBuilder("Select");
		sql.append(" WaiverId, FinID, FinReference, Event, Remarks, PostingDate, ValueDate");
		sql.append(" Version, LastMntOn, LastMntBy, RecordStatus, RoleCode, NextRoleCode");
		sql.append(", TaskId, NextTaskId, RecordType, WorkflowId");
		sql.append(" From FeeWaiverHeader");
		sql.append(StringUtils.trimToEmpty(type));
		return sql;
	}

	private class FeeWaiverHeaderRowMapper implements RowMapper<FeeWaiverHeader> {

		private FeeWaiverHeaderRowMapper() {

		}

		@Override
		public FeeWaiverHeader mapRow(ResultSet rs, int rowNum) throws SQLException {
			FeeWaiverHeader fwh = new FeeWaiverHeader();

			fwh.setWaiverId(rs.getLong("WaiverId"));
			fwh.setFinID(rs.getLong("FinID"));
			fwh.setFinReference(rs.getString("FinReference"));
			fwh.setEvent(rs.getString("Event"));
			fwh.setRemarks(rs.getString("Remarks"));
			fwh.setPostingDate(rs.getDate("PostingDate"));
			fwh.setValueDate(rs.getDate("ValueDate"));
			fwh.setVersion(rs.getInt("Version"));
			fwh.setLastMntBy(rs.getLong("LastMntBy"));
			fwh.setLastMntOn(rs.getTimestamp("LastMntOn"));
			fwh.setRecordStatus(rs.getString("RecordStatus"));
			fwh.setRoleCode(rs.getString("RoleCode"));
			fwh.setNextRoleCode(rs.getString("NextRoleCode"));
			fwh.setTaskId(rs.getString("TaskId"));
			fwh.setNextTaskId(rs.getString("NextTaskId"));
			fwh.setRecordType(rs.getString("RecordType"));
			fwh.setWorkflowId(rs.getLong("WorkflowId"));

			return fwh;
		}

	}

}