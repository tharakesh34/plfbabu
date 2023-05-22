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
 * * FileName : RepayInstructionDAOImpl.java * * Author : PENNANT TECHONOLOGIES * * Creation Date : 02-12-2011 * *
 * Modified Date : 02-12-2011 * * Description : * *
 ********************************************************************************************
 * Date Author Version Comments *
 ********************************************************************************************
 * 02-12-2011 Pennant 0.1 * * * * * * * * *
 ********************************************************************************************
 */

package com.pennant.backend.dao.finance.impl;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.RowMapper;

import com.pennant.backend.dao.finance.RepayInstructionDAO;
import com.pennant.backend.model.finance.RepayInstruction;
import com.pennanttech.pennapps.core.ConcurrencyException;
import com.pennanttech.pennapps.core.DependencyFoundException;
import com.pennanttech.pennapps.core.jdbc.BasicDao;
import com.pennanttech.pennapps.core.jdbc.JdbcUtil;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pennapps.core.resource.Message;

/**
 * DAO methods implementation for the <b>RepayInstruction model</b> class.<br>
 * 
 */

public class RepayInstructionDAOImpl extends BasicDao<RepayInstruction> implements RepayInstructionDAO {
	private static Logger logger = LogManager.getLogger(RepayInstructionDAOImpl.class);

	public RepayInstructionDAOImpl() {
		super();
	}

	@Override
	public RepayInstruction getRepayInstructionById(long finID, String type, boolean isWIF) {
		StringBuilder sql = getSqlQuery(type, isWIF);
		sql.append(" Where FinID = ?");

		logger.debug(Literal.SQL + sql.toString());

		RepayInsRowMapper rowMapper = new RepayInsRowMapper();

		try {
			return this.jdbcOperations.queryForObject(sql.toString(), rowMapper, finID);
		} catch (EmptyResultDataAccessException e) {
			logger.warn(Message.NO_RECORD_FOUND);
			return null;
		}
	}

	public void deleteByFinReference(long finID, String type, boolean isWIF, long logKey) {
		StringBuilder sql = new StringBuilder("Delete");
		if (isWIF) {
			sql.append(" From WIFFinRepayInstruction");
		} else {
			sql.append(" From FinRepayInstruction");
		}
		sql.append(StringUtils.trimToEmpty(type));
		sql.append(" Where FinID = ?");

		if (logKey != 0) {
			sql.append(" and LogKey = ?");
		}

		logger.debug(Literal.SQL + sql.toString());

		this.jdbcOperations.update(sql.toString(), ps -> {

			ps.setLong(1, finID);
			if (logKey != 0) {
				ps.setLong(2, logKey);
			}

		});
	}

	@Override
	public void delete(RepayInstruction ri, String type, boolean isWIF) {
		StringBuilder sql = new StringBuilder("Delete");
		if (isWIF) {
			sql.append(" From WIFFinRepayInstruction");
		} else {
			sql.append(" From FinRepayInstruction");
		}

		sql.append(StringUtils.trimToEmpty(type));
		sql.append(" Where FinID = ? and RepayDate = ?");

		logger.debug(Literal.SQL + sql.toString());

		try {
			int recordCount = this.jdbcOperations.update(sql.toString(), ps -> {
				int index = 1;

				ps.setLong(index, ri.getFinID());
			});

			if (recordCount <= 0) {
				throw new ConcurrencyException();
			}
		} catch (DataAccessException e) {
			throw new DependencyFoundException(e);
		}
	}

	@Override
	public String save(RepayInstruction ri, String type, boolean isWIF) {
		StringBuilder sql = getSaveQuery(type, isWIF);

		logger.debug(Literal.SQL + sql.toString());

		this.jdbcOperations.update(sql.toString(), ps -> {
			parameterizedSetter(ps, ri, type);
		});

		return ri.getId();
	}

	@Override
	public int saveList(List<RepayInstruction> repayInstruction, String type, boolean isWIF) {
		StringBuilder sql = getSaveQuery(type, isWIF);

		logger.debug(Literal.SQL + sql.toString());

		return this.jdbcOperations.batchUpdate(sql.toString(), new BatchPreparedStatementSetter() {
			@Override
			public void setValues(PreparedStatement ps, int i) throws SQLException {
				RepayInstruction ri = repayInstruction.get(i);
				parameterizedSetter(ps, ri, type);
			}

			@Override
			public int getBatchSize() {
				return repayInstruction.size();
			}
		}).length;
	}

	@Override
	public void update(RepayInstruction ri, String type, boolean isWIF) {
		StringBuilder sql = new StringBuilder("Update");
		if (isWIF) {
			sql.append(" WIFFinRepayInstruction");
		} else {
			sql.append(" FinRepayInstruction");
		}

		sql.append(StringUtils.trimToEmpty(type));
		sql.append(" Set RepayDate = ?, RepayAmount = ?, RepaySchdMethod = ?");
		sql.append(", Version = ?, LastMntBy = ?, LastMntOn = ?, RecordStatus = ?, RoleCode = ?");
		sql.append(", NextRoleCode = ?, TaskId = ?, NextTaskId = ?, RecordType = ?, WorkflowId = ?");
		sql.append(" Where FinID = ?");

		if (!type.endsWith("_Temp")) {
			sql.append(" and Version = ? - 1");
		}

		logger.debug(Literal.SQL + sql.toString());

		int recordCount = this.jdbcOperations.update(sql.toString(), ps -> {
			int index = 1;

			ps.setDate(index++, JdbcUtil.getDate(ri.getRepayDate()));
			ps.setBigDecimal(index++, ri.getRepayAmount());
			ps.setString(index++, ri.getRepaySchdMethod());
			ps.setInt(index++, ri.getVersion());
			ps.setLong(index++, ri.getLastMntBy());
			ps.setTimestamp(index++, ri.getLastMntOn());
			ps.setString(index++, ri.getRecordStatus());
			ps.setString(index++, ri.getRoleCode());
			ps.setString(index++, ri.getNextRoleCode());
			ps.setString(index++, ri.getTaskId());
			ps.setString(index++, ri.getNextTaskId());
			ps.setString(index++, ri.getRecordType());
			ps.setLong(index++, ri.getWorkflowId());
			ps.setLong(index++, ri.getFinID());

			if (!type.endsWith("_Temp")) {
				ps.setInt(index, ri.getVersion() - 1);
			}

		});

		if (recordCount <= 0) {
			throw new ConcurrencyException();
		}
	}

	@Override
	public List<RepayInstruction> getRepayInstructions(long finID, String type, boolean isWIF) {
		StringBuilder sql = getSqlQuery(type, isWIF);
		sql.append(" Where FinID = ?");

		logger.debug(Literal.SQL + sql.toString());

		RepayInsRowMapper rowMapper = new RepayInsRowMapper();

		return this.jdbcOperations.query(sql.toString(), ps -> {
			int index = 1;
			ps.setLong(index, finID);
		}, rowMapper);
	}

	@Override
	public List<RepayInstruction> getRepayInstructions(long finID, String type, boolean isWIF, long logKey) {
		StringBuilder sql = getSqlQuery(type, isWIF);
		sql.append(" Where FinID = ? and LogKey = ?");

		logger.debug(Literal.SQL + sql.toString());

		RepayInsRowMapper rowMapper = new RepayInsRowMapper();

		return this.jdbcOperations.query(sql.toString(), ps -> {
			int index = 1;
			ps.setLong(index++, finID);
			ps.setLong(index, logKey);
		}, rowMapper);
	}

	@Override
	public List<RepayInstruction> getRepayInstrEOD(long finID) {
		StringBuilder sql = new StringBuilder("Select");
		sql.append(" FinID, FinReference, RepayDate, RepayAmount, RepaySchdMethod");
		sql.append(" From FinRepayInstruction");
		sql.append(" Where FinID = ?");

		logger.debug(Literal.SQL + sql.toString());

		return this.jdbcOperations.query(sql.toString(), ps -> {
			int index = 1;
			ps.setLong(index, finID);
		}, (rs, rowNum) -> {
			RepayInstruction ri = new RepayInstruction();

			ri.setFinID(rs.getLong("FinID"));
			ri.setFinReference(rs.getString("FinReference"));
			ri.setRepayDate(JdbcUtil.getDate(rs.getDate("RepayDate")));
			ri.setRepayAmount(rs.getBigDecimal("RepayAmount"));
			ri.setRepaySchdMethod(rs.getString("RepaySchdMethod"));

			return ri;
		});
	}

	@Override
	public int deleteInEOD(long finID) {
		String sql = "Delete From FinRepayInstruction Where FinID = ?";

		logger.debug(Literal.SQL + sql);

		return this.jdbcOperations.update(sql, ps -> {
			int index = 1;
			ps.setLong(index, finID);
		});
	}

	@Override
	public int saveListInEOD(List<RepayInstruction> rpiList) {
		StringBuilder sql = new StringBuilder("Insert Into");
		sql.append(" FinRepayInstruction");
		sql.append(" (FinID, FinReference, RepayDate, RepayAmount, RepaySchdMethod, Version, LastMntBy, LastMntOn");
		sql.append(", RecordStatus, RoleCode, NextRoleCode, TaskId, NextTaskId, RecordType, WorkflowId");
		sql.append(") values(");
		sql.append(" ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?");
		sql.append(")");

		return jdbcOperations.batchUpdate(sql.toString(), new BatchPreparedStatementSetter() {
			@Override
			public void setValues(PreparedStatement ps, int i) throws SQLException {
				RepayInstruction rpi = rpiList.get(i);

				int index = 1;

				ps.setLong(index++, rpi.getFinID());
				ps.setString(index++, rpi.getFinReference());
				ps.setDate(index++, JdbcUtil.getDate(rpi.getRepayDate()));
				ps.setBigDecimal(index++, rpi.getRepayAmount());
				ps.setString(index++, rpi.getRepaySchdMethod());
				ps.setInt(index++, rpi.getVersion());
				ps.setLong(index++, rpi.getLastMntBy());
				ps.setTimestamp(index++, rpi.getLastMntOn());
				ps.setString(index++, rpi.getRecordStatus());
				ps.setString(index++, rpi.getRoleCode());
				ps.setString(index++, rpi.getNextRoleCode());
				ps.setString(index++, rpi.getTaskId());
				ps.setString(index++, rpi.getNextTaskId());
				ps.setString(index++, rpi.getRecordType());
				ps.setLong(index, rpi.getWorkflowId());
			}

			@Override
			public int getBatchSize() {
				return rpiList.size();
			}
		}).length;

	}

	private StringBuilder getSaveQuery(String type, boolean isWIF) {
		StringBuilder sql = new StringBuilder("Insert Into");
		if (isWIF) {
			sql.append(" WIFFinRepayInstruction");
		} else {
			sql.append(" FinRepayInstruction");
		}
		sql.append(StringUtils.trimToEmpty(type));
		sql.append(" (FinID, FinReference, RepayDate, RepayAmount, RepaySchdMethod");

		if (type.contains("Log")) {
			sql.append(", LogKey");
		}

		sql.append(", Version, LastMntBy, LastMntOn, RecordStatus");
		sql.append(", RoleCode, NextRoleCode, TaskId, NextTaskId, RecordType, WorkflowId");
		sql.append(") values(");
		sql.append("?, ?, ?, ?, ?");

		if (type.contains("Log")) {
			sql.append(", ?");
		}

		sql.append(", ?, ?, ?, ?, ?, ?, ?, ?, ?, ?");
		sql.append(")");

		return sql;
	}

	private void parameterizedSetter(PreparedStatement ps, RepayInstruction ri, String type) throws SQLException {
		int index = 1;

		ps.setLong(index++, ri.getFinID());
		ps.setString(index++, ri.getFinReference());
		ps.setDate(index++, JdbcUtil.getDate(ri.getRepayDate()));
		ps.setBigDecimal(index++, ri.getRepayAmount());
		ps.setString(index++, ri.getRepaySchdMethod());

		if (type.contains("Log")) {
			ps.setLong(index++, ri.getLogKey());
		}

		ps.setInt(index++, ri.getVersion());
		ps.setLong(index++, ri.getLastMntBy());
		ps.setTimestamp(index++, ri.getLastMntOn());
		ps.setString(index++, ri.getRecordStatus());
		ps.setString(index++, ri.getRoleCode());
		ps.setString(index++, ri.getNextRoleCode());
		ps.setString(index++, ri.getTaskId());
		ps.setString(index++, ri.getNextTaskId());
		ps.setString(index++, ri.getRecordType());
		ps.setLong(index, ri.getWorkflowId());
	}

	private StringBuilder getSqlQuery(String type, boolean isWIF) {
		StringBuilder sql = new StringBuilder("Select");
		sql.append(" FinID, FinReference, RepayDate, RepayAmount, RepaySchdMethod, Version, LastMntBy, LastMntOn");
		sql.append(", RecordStatus, RoleCode, NextRoleCode, TaskId, NextTaskId, RecordType, WorkflowId");

		if (isWIF) {
			sql.append(" From WIFFinRepayInstruction");
		} else {
			sql.append(" From FinRepayInstruction");
		}

		sql.append(StringUtils.trimToEmpty(type));

		return sql;
	}

	private class RepayInsRowMapper implements RowMapper<RepayInstruction> {
		@Override
		public RepayInstruction mapRow(ResultSet rs, int rowNum) throws SQLException {
			RepayInstruction ri = new RepayInstruction();

			ri.setFinID(rs.getLong("FinID"));
			ri.setFinReference(rs.getString("FinReference"));
			ri.setRepayDate(rs.getTimestamp("RepayDate"));
			ri.setRepayAmount(rs.getBigDecimal("RepayAmount"));
			ri.setRepaySchdMethod(rs.getString("RepaySchdMethod"));
			ri.setVersion(rs.getInt("Version"));
			ri.setLastMntBy(rs.getLong("LastMntBy"));
			ri.setLastMntOn(rs.getTimestamp("LastMntOn"));
			ri.setRecordStatus(rs.getString("RecordStatus"));
			ri.setRoleCode(rs.getString("RoleCode"));
			ri.setNextRoleCode(rs.getString("NextRoleCode"));
			ri.setTaskId(rs.getString("TaskId"));
			ri.setNextTaskId(rs.getString("NextTaskId"));
			ri.setRecordType(rs.getString("RecordType"));
			ri.setWorkflowId(rs.getLong("WorkflowId"));

			return ri;
		}
	}

	@Override
	public List<RepayInstruction> getRepayInstructionsForLMSEvent(long finID) {
		return getRepayInstructions(finID, "", false);
	}
}