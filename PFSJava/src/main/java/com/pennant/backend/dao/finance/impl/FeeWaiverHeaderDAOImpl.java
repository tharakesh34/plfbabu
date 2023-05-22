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
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.RowMapper;

import com.pennant.backend.dao.finance.FeeWaiverHeaderDAO;
import com.pennant.backend.model.finance.FeeWaiverHeader;
import com.pennant.backend.util.PennantConstants;
import com.pennanttech.pennapps.core.ConcurrencyException;
import com.pennanttech.pennapps.core.DependencyFoundException;
import com.pennanttech.pennapps.core.jdbc.JdbcUtil;
import com.pennanttech.pennapps.core.jdbc.SequenceDao;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pennapps.core.resource.Message;
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
			logger.warn(Message.NO_RECORD_FOUND);
			return null;
		}
	}

	@Override
	public FeeWaiverHeader getFeeWaiverHeaderByFinRef(long finID, String type) {
		StringBuilder sql = getSelectQuery(type);
		sql.append(" Where FinID = ?");

		logger.debug(Literal.SQL + sql.toString());

		try {
			return jdbcOperations.queryForObject(sql.toString(), new FeeWaiverHeaderRowMapper(), finID);
		} catch (EmptyResultDataAccessException e) {
			logger.warn(Message.NO_RECORD_FOUND);
			return null;
		}
	}

	@Override
	public Date getLastWaiverDate(long finID, Date appDate, Date receiptDate) {
		String sql = "Select max(ValueDate) From FeeWaiverHeader Where FinID = ? and ValueDate >= ? and ValueDate < ?";

		logger.debug(Literal.SQL + sql);
		return this.jdbcOperations.queryForObject(sql, Date.class, finID, receiptDate, appDate);
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
				ps.setLong(index, fwh.getWorkflowId());

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
			ps.setLong(index, fwh.getWaiverId());

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

	@Override
	public boolean isFeeWaiverInProcess(long finID) {
		String sql = "Select coalesce(count(FinID), 0) from FeeWaiverHeader_View where FinID = ? and RecordStatus in (?, ?)";

		logger.debug(Literal.SQL + sql);

		Object[] args = new Object[] { finID, PennantConstants.RCD_STATUS_SUBMITTED,
				PennantConstants.RCD_STATUS_RESUBMITTED };

		return this.jdbcOperations.queryForObject(sql, Integer.class, args) > 0;
	}

	private StringBuilder getSelectQuery(String type) {
		StringBuilder sql = new StringBuilder("Select");
		sql.append(" WaiverId, FinID, FinReference, Event, Remarks, PostingDate, ValueDate");
		sql.append(", Version, LastMntOn, LastMntBy, RecordStatus, RoleCode, NextRoleCode");
		sql.append(", TaskId, NextTaskId, RecordType, WorkflowId");
		sql.append(" From FeeWaiverHeader");
		sql.append(StringUtils.trimToEmpty(type));
		return sql;
	}

	private class FeeWaiverHeaderRowMapper implements RowMapper<FeeWaiverHeader> {

		private FeeWaiverHeaderRowMapper() {
		    super();
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

	@Override
	public List<FeeWaiverHeader> getFeeWaiverHeaderByFinReference(long finID, String type) {
		String sql = "Select FinID, FinReference, Status, WaiverId, WaiverFullFillAmount, WaiverFullFillDate, AlwCondWaiver From FeeWaiverHeader Where FinId = ?";

		logger.debug(Literal.SQL.concat(sql));

		return this.jdbcOperations.query(sql, new FeeWaiverHeaderRM(), finID);
	}

	@Override
	public List<FeeWaiverHeader> fetchPromisedFeeWaivers(Date promissedDate) {
		StringBuilder sql = new StringBuilder("Select");
		sql.append(" FinID, FinReference, WaiverId, WaiverFullFillAmount, WaiverFullFillDate, AlwCondWaiver");
		sql.append(" From FeeWaiverHeader");
		sql.append(" Where WaiverFullFillDate = ? and Status = ? and AlwCondWaiver = ?");

		logger.debug(Literal.SQL.concat(sql.toString()));

		return this.jdbcOperations.query(sql.toString(), new FeeWaiverHeaderRM(), JdbcUtil.getDate(promissedDate), "R",
				1);
	}

	@Override
	public void updateWaiverStatus(long waiverId, String status) {
		String sql = "Update FeeWaiverHeader Set Status = ? Where WaiverId = ?";

		logger.debug(Literal.SQL.concat(sql));

		int recordCount = jdbcOperations.update(sql, status, waiverId);

		if (recordCount == 0) {
			throw new ConcurrencyException();
		}
	}

	@Override
	public Date getMaxFullFillDate(long finId) {
		String sql = "Select max(WaiverFullFillDate) From FeeWaiverHeader Where FinID = ? and alwCondWaiver = ? and Status = ?";

		logger.debug(Literal.SQL.concat(sql));

		try {
			return this.jdbcOperations.queryForObject(sql, Date.class, finId, 1, "R");
		} catch (EmptyResultDataAccessException e) {
			logger.warn(Message.NO_RECORD_FOUND);
			return null;
		}
	}

	private class FeeWaiverHeaderRM implements RowMapper<FeeWaiverHeader> {

		private FeeWaiverHeaderRM() {
			super();
		}

		@Override
		public FeeWaiverHeader mapRow(ResultSet rs, int rowNum) throws SQLException {
			FeeWaiverHeader fwh = new FeeWaiverHeader();

			fwh.setFinID(rs.getLong("FinID"));
			fwh.setFinReference(rs.getString("FinReference"));
			fwh.setWaiverId(rs.getLong("WaiverId"));
			fwh.setWaiverFullFillAmount(rs.getBigDecimal("WaiverFullFillAmount"));
			fwh.setWaiverFullFillDate(rs.getTimestamp("WaiverFullFillDate"));
			fwh.setStatus(rs.getString("Status"));
			fwh.setAlwCondWaiver(rs.getBoolean("AlwCondWaiver"));

			return fwh;
		}
	}
}