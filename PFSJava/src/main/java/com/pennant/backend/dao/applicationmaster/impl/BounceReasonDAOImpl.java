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
 * * FileName : BounceReasonDAOImpl.java * * Author : PENNANT TECHONOLOGIES * * Creation Date : 22-04-2017 * * Modified
 * Date : 22-04-2017 * * Description : * *
 ********************************************************************************************
 * Date Author Version Comments *
 ********************************************************************************************
 * 22-04-2017 PENNANT 0.1 * * * * * * * * *
 ********************************************************************************************
 */
package com.pennant.backend.dao.applicationmaster.impl;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;

import com.pennant.backend.dao.applicationmaster.BounceReasonDAO;
import com.pennant.backend.model.applicationmaster.BounceReason;
import com.pennanttech.pennapps.core.ConcurrencyException;
import com.pennanttech.pennapps.core.DependencyFoundException;
import com.pennanttech.pennapps.core.jdbc.JdbcUtil;
import com.pennanttech.pennapps.core.jdbc.SequenceDao;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pennapps.core.resource.Message;
import com.pennanttech.pff.core.TableType;
import com.pennanttech.pff.core.util.QueryUtil;

/**
 * Data access layer implementation for <code>BounceReason</code> with set of CRUD operations.
 */
public class BounceReasonDAOImpl extends SequenceDao<BounceReason> implements BounceReasonDAO {
	private static Logger logger = LogManager.getLogger(BounceReasonDAOImpl.class);

	public BounceReasonDAOImpl() {
		super();
	}

	@Override
	public BounceReason getBounceReason(long bounceID, String type) {
		StringBuilder sql = getSqlQuery(type);
		sql.append(" Where BounceID = ?");

		logger.debug(Literal.SQL + sql.toString());

		try {
			return this.jdbcOperations.queryForObject(sql.toString(), new BounceReasonRM(type), bounceID);
		} catch (EmptyResultDataAccessException e) {
			logger.warn(Message.NO_RECORD_FOUND);
			return null;
		}
	}

	@Override
	public boolean isDuplicateKey(long bounceID, String bounceCode, TableType tableType) {
		logger.debug(Literal.ENTERING);

		// Prepare the SQL.
		String sql;
		String whereClause = "bounceCode = :bounceCode AND bounceID != :bounceID";

		switch (tableType) {
		case MAIN_TAB:
			sql = QueryUtil.getCountQuery("BounceReasons", whereClause);
			break;
		case TEMP_TAB:
			sql = QueryUtil.getCountQuery("BounceReasons_Temp", whereClause);
			break;
		default:
			sql = QueryUtil.getCountQuery(new String[] { "BounceReasons_Temp", "BounceReasons" }, whereClause);
			break;
		}

		// Execute the SQL, binding the arguments.
		logger.trace(Literal.SQL + sql);
		MapSqlParameterSource paramSource = new MapSqlParameterSource();
		paramSource.addValue("bounceID", bounceID);
		paramSource.addValue("bounceCode", bounceCode);

		Integer count = jdbcTemplate.queryForObject(sql, paramSource, Integer.class);

		boolean exists = false;
		if (count > 0) {
			exists = true;
		}

		logger.debug(Literal.LEAVING);

		return exists;
	}

	@Override
	public String save(BounceReason br, TableType tableType) {
		StringBuilder sql = new StringBuilder("Insert into BounceReasons");
		sql.append(tableType.getSuffix());
		sql.append(" (BounceID, BounceCode, ReasonType, Category, Reason, Action");
		sql.append(", RuleID, ReturnCode, Active, InstrumentType, HoldMarkBounceCount");
		sql.append(", Version, LastMntOn, LastMntBy, RecordStatus, RoleCode, NextRoleCode");
		sql.append(", TaskId, NextTaskId, RecordType, WorkflowId)");
		sql.append(" Values(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?");
		sql.append(", ?, ?, ?, ?)");

		if (br.getBounceID() <= 0) {
			br.setBounceID(getNextValue("SeqBounceReasons"));
		}

		logger.debug(Literal.SQL + sql.toString());

		try {
			jdbcOperations.update(sql.toString(), ps -> {
				int index = 1;

				ps.setLong(index++, br.getBounceID());
				ps.setString(index++, br.getBounceCode());
				ps.setInt(index++, br.getReasonType());
				ps.setInt(index++, br.getCategory());
				ps.setString(index++, br.getReason());
				ps.setInt(index++, br.getAction());
				ps.setLong(index++, br.getRuleID());
				ps.setString(index++, br.getReturnCode());
				ps.setBoolean(index++, br.isActive());
				ps.setString(index++, br.getInstrumentType());
				ps.setInt(index++, br.getHoldMarkBounceCount());
				ps.setInt(index++, br.getVersion());
				ps.setTimestamp(index++, br.getLastMntOn());
				ps.setLong(index++, br.getLastMntBy());
				ps.setString(index++, br.getRecordStatus());
				ps.setString(index++, br.getRoleCode());
				ps.setString(index++, br.getNextRoleCode());
				ps.setString(index++, br.getTaskId());
				ps.setString(index++, br.getNextTaskId());
				ps.setString(index++, br.getRecordType());
				ps.setLong(index++, br.getWorkflowId());
			});
		} catch (DuplicateKeyException e) {
			throw new ConcurrencyException(e);
		}

		return Long.toString(br.getBounceID());
	}

	@Override
	public void update(BounceReason br, TableType tableType) {
		StringBuilder sql = new StringBuilder("Update BounceReasons");
		sql.append(tableType.getSuffix());
		sql.append(" Set BounceCode = ?, ReasonType = ?, Category = ?");
		sql.append(", Reason = ?, Action = ?, RuleID = ?, ReturnCode = ?");
		sql.append(", Active = ?, InstrumentType = ?, HoldMarkBounceCount = ?");
		sql.append(", Version = ? , LastMntBy = ?, LastMntOn = ?, RecordStatus = ?, RoleCode = ?");
		sql.append(", NextRoleCode = ?, TaskId = ?, NextTaskId = ?");
		sql.append(", RecordType = ?, WorkflowId = ?");
		sql.append(" Where BounceID = ?");
		sql.append(QueryUtil.getConcurrencyClause(tableType));

		logger.debug(Literal.SQL + sql.toString());

		this.jdbcOperations.update(sql.toString(), ps -> {
			int index = 1;

			ps.setString(index++, br.getBounceCode());
			ps.setInt(index++, br.getReasonType());
			ps.setInt(index++, br.getCategory());
			ps.setString(index++, br.getReason());
			ps.setInt(index++, br.getAction());
			ps.setLong(index++, br.getRuleID());
			ps.setString(index++, br.getReturnCode());
			ps.setBoolean(index++, br.isActive());
			ps.setString(index++, br.getInstrumentType());
			ps.setInt(index++, br.getHoldMarkBounceCount());
			ps.setInt(index++, br.getVersion());
			ps.setLong(index++, br.getLastMntBy());
			ps.setTimestamp(index++, br.getLastMntOn());
			ps.setString(index++, br.getRecordStatus());
			ps.setString(index++, br.getRoleCode());
			ps.setString(index++, br.getNextRoleCode());
			ps.setString(index++, br.getTaskId());
			ps.setString(index++, br.getNextTaskId());
			ps.setString(index++, br.getRecordType());
			ps.setLong(index++, br.getWorkflowId());

			ps.setLong(index++, br.getBounceID());

			if (tableType == TableType.TEMP_TAB) {
				ps.setTimestamp(index++, br.getPrevMntOn());
			} else {
				ps.setInt(index++, br.getVersion() - 1);
			}
		});

	}

	@Override
	public void delete(BounceReason bounceReason, TableType tableType) {
		StringBuilder sql = new StringBuilder("Delete From BounceReasons");
		sql.append(tableType.getSuffix());
		sql.append(" Where BounceID = ?");

		logger.debug(Literal.SQL + sql.toString());

		try {
			int recordCount = this.jdbcOperations.update(sql.toString(),
					ps -> ps.setLong(1, bounceReason.getBounceID()));

			if (recordCount == 0) {
				throw new ConcurrencyException();
			}
		} catch (DataAccessException e) {
			throw new DependencyFoundException(e);
		}
	}

	@Override
	public BounceReason getBounceReasonByReturnCode(String returnCode, String type) {
		StringBuilder sql = getSqlQuery(type);
		sql.append(" Where ReturnCode = ?");

		logger.debug(Literal.SQL + sql.toString());

		try {
			return this.jdbcOperations.queryForObject(sql.toString(), new BounceReasonRM(type), returnCode);
		} catch (EmptyResultDataAccessException e) {
			logger.warn(Message.NO_RECORD_FOUND);
			return null;
		}
	}

	@Override
	public int getBounceReasonByRuleCode(long ruleId) {
		String sql = "Select Count(RuleID) From BounceReasons_View Where RuleID = ?";

		logger.debug(Literal.SQL.concat(sql));

		return this.jdbcOperations.queryForObject(sql, Integer.class, ruleId);
	}

	@Override
	public boolean isDuplicateReturnCode(long bounceID, String returnCode, TableType tableType) {
		logger.debug(Literal.ENTERING);

		// Prepare the SQL.
		String sql;
		String whereClause = "returnCode = :returnCode AND bounceID != :bounceID";

		switch (tableType) {
		case MAIN_TAB:
			sql = QueryUtil.getCountQuery("BounceReasons", whereClause);
			break;
		case TEMP_TAB:
			sql = QueryUtil.getCountQuery("BounceReasons_Temp", whereClause);
			break;
		default:
			sql = QueryUtil.getCountQuery(new String[] { "BounceReasons_Temp", "BounceReasons" }, whereClause);
			break;
		}

		// Execute the SQL, binding the arguments.
		logger.trace(Literal.SQL + sql);
		MapSqlParameterSource paramSource = new MapSqlParameterSource();
		paramSource.addValue("bounceID", bounceID);
		paramSource.addValue("returnCode", returnCode);

		Integer count = jdbcTemplate.queryForObject(sql, paramSource, Integer.class);

		boolean exists = false;
		if (count > 0) {
			exists = true;
		}

		logger.debug(Literal.LEAVING);

		return exists;
	}

	@Override
	public Long getBounceIDByCode(String returnCode) {
		String sql = "Select BounceID From BounceReasons Where ReturnCode = ?";

		logger.debug(Literal.SQL + sql);

		try {
			return jdbcOperations.queryForObject(sql, (rs, rowNum) -> {
				return JdbcUtil.getLong(rs.getObject(1));
			}, returnCode);
		} catch (EmptyResultDataAccessException e) {
			logger.warn(Message.NO_RECORD_FOUND);
			return null;
		}
	}

	private StringBuilder getSqlQuery(String type) {
		StringBuilder sql = new StringBuilder("Select");
		sql.append(" BounceID, BounceCode, ReasonType, Category, Reason, Action");
		sql.append(", RuleID, ReturnCode, Active, InstrumentType, HoldMarkBounceCount");

		if (type.contains("View")) {
			sql.append(", RuleCode, RuleCodeDesc");
		}

		sql.append(", Version, LastMntOn, LastMntBy, RecordStatus, RoleCode, NextRoleCode");
		sql.append(", TaskId, NextTaskId, RecordType, WorkflowId");
		sql.append(" From BounceReasons");
		sql.append(type);

		return sql;
	}

	private class BounceReasonRM implements RowMapper<BounceReason> {

		private String type;

		public BounceReasonRM(String type) {
			this.type = type;
		}

		@Override
		public BounceReason mapRow(ResultSet rs, int rowNum) throws SQLException {

			BounceReason br = new BounceReason();

			br.setBounceID(rs.getLong("BounceID"));
			br.setBounceCode(rs.getString("BounceCode"));
			br.setReasonType(rs.getInt("ReasonType"));
			br.setCategory(rs.getInt("Category"));
			br.setReason(rs.getString("Reason"));
			br.setAction(rs.getInt("Action"));
			br.setRuleID(rs.getInt("RuleID"));
			br.setReturnCode(rs.getString("ReturnCode"));
			br.setActive(rs.getBoolean("Active"));
			br.setInstrumentType(rs.getString("InstrumentType"));
			br.setHoldMarkBounceCount(rs.getInt("HoldMarkBounceCount"));

			if (type.contains("View")) {
				br.setRuleCode(rs.getString("RuleCode"));
				br.setRuleCodeDesc(rs.getString("RuleCodeDesc"));
			}

			br.setVersion(rs.getInt("Version"));
			br.setLastMntBy(rs.getLong("LastMntBy"));
			br.setLastMntOn(rs.getTimestamp("LastMntOn"));
			br.setRecordStatus(rs.getString("RecordStatus"));
			br.setRoleCode(rs.getString("RoleCode"));
			br.setNextRoleCode(rs.getString("NextRoleCode"));
			br.setTaskId(rs.getString("TaskId"));
			br.setNextTaskId(rs.getString("NextTaskId"));
			br.setRecordType(rs.getString("RecordType"));
			br.setWorkflowId(rs.getLong("WorkflowId"));

			return br;
		}
	}

	@Override
	public String getReasonByReceiptId(long receiptId) {
		StringBuilder sql = new StringBuilder("Select");
		sql.append(" br.Reason From BounceReasons br");
		sql.append(" Inner Join ManualAdvise ma On ma.BounceID = br.BounceID");
		sql.append(" Inner Join FinReceiptHeader frh On frh.ReceiptID = ma.ReceiptID");
		sql.append(" Where ma.ReceiptID = ?");

		logger.debug(Literal.SQL + sql.toString());

		try {
			return this.jdbcOperations.queryForObject(sql.toString(), String.class, receiptId);
		} catch (EmptyResultDataAccessException e) {
			logger.warn(Message.NO_RECORD_FOUND);
			return null;
		}
	}
}