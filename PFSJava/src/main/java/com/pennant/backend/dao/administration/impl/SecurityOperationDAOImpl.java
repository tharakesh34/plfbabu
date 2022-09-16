/**
 * 
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
 * * FileName : SecurityOperationDAOImpl.java * * Author : PENNANT TECHONOLOGIES * * Creation Date : 10-03-2014 * *
 * Modified Date : 10-03-2014 * * Description : * *
 ********************************************************************************************
 * Date Author Version Comments *
 ********************************************************************************************
 * 10-03-2014 Pennant 0.1 * * * * * * * * *
 ********************************************************************************************
 */
package com.pennant.backend.dao.administration.impl;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.RowMapper;

import com.pennant.backend.dao.administration.SecurityOperationDAO;
import com.pennant.backend.model.administration.SecurityOperation;
import com.pennanttech.pennapps.core.ConcurrencyException;
import com.pennanttech.pennapps.core.DependencyFoundException;
import com.pennanttech.pennapps.core.jdbc.SequenceDao;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pennapps.core.resource.Message;

/**
 * DAO methods implementation for the <b>SecurityOperation model</b> class.<br>
 * 
 */
public class SecurityOperationDAOImpl extends SequenceDao<SecurityOperation> implements SecurityOperationDAO {

	public SecurityOperationDAOImpl() {
		super();
	}

	@Override
	public SecurityOperation getSecurityOperationById(final long id, String type) {
		StringBuilder sql = getSqlQuery(type);
		sql.append(" Where OprId = ?");

		logger.debug(Literal.SQL.concat(sql.toString()));

		try {
			return this.jdbcOperations.queryForObject(sql.toString(), new SecurityOperationsRM(), id);
		} catch (EmptyResultDataAccessException e) {
			logger.warn(Message.NO_RECORD_FOUND);
			return null;
		}
	}

	@Override
	public SecurityOperation getSecurityOperationByCode(String oprCode, String type) {
		StringBuilder sql = getSqlQuery(type);
		sql.append(" Where OprCode = ?");

		logger.debug(Literal.SQL.concat(sql.toString()));

		try {
			return this.jdbcOperations.queryForObject(sql.toString(), new SecurityOperationsRM(), oprCode);
		} catch (EmptyResultDataAccessException e) {
			logger.warn(Message.NO_RECORD_FOUND);
			return null;
		}
	}

	@Override
	public void delete(SecurityOperation so, String type) {
		StringBuilder sql = new StringBuilder("Delete From SecOperations");
		sql.append(StringUtils.trimToEmpty(type));
		sql.append(" Where OprID = ?");

		logger.debug(Literal.SQL.concat(sql.toString()));

		try {
			if (this.jdbcOperations.update(sql.toString(), ps -> ps.setLong(1, so.getOprID())) <= 0) {
				throw new ConcurrencyException();
			}
		} catch (DataAccessException e) {
			throw new DependencyFoundException(e);
		}
	}

	@Override
	public long save(SecurityOperation so, String type) {
		StringBuilder sql = new StringBuilder("Insert Into SecOperations");
		sql.append(StringUtils.trimToEmpty(type));
		sql.append(" (OprID, OprCode, OprDesc");
		sql.append(", Version , LastMntBy, LastMntOn, RecordStatus, RoleCode");
		sql.append(", NextRoleCode, TaskId, NextTaskId, RecordType, WorkflowId)");
		sql.append(" Values(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)");

		logger.debug(Literal.SQL.concat(sql.toString()));

		if (so.getId() == Long.MIN_VALUE) {
			so.setId(getNextValue("SeqSecOperations"));
		}

		this.jdbcOperations.update(sql.toString(), ps -> {
			int index = 1;

			ps.setLong(index++, so.getOprID());
			ps.setString(index++, so.getOprCode());
			ps.setString(index++, so.getOprDesc());
			ps.setInt(index++, so.getVersion());
			ps.setLong(index++, so.getLastMntBy());
			ps.setTimestamp(index++, so.getLastMntOn());
			ps.setString(index++, so.getRecordStatus());
			ps.setString(index++, so.getRoleCode());
			ps.setString(index++, so.getNextRoleCode());
			ps.setString(index++, so.getTaskId());
			ps.setString(index++, so.getNextTaskId());
			ps.setString(index++, so.getRecordType());
			ps.setLong(index++, so.getWorkflowId());
		});

		return so.getId();
	}

	@Override
	public void update(SecurityOperation so, String type) {
		StringBuilder sql = new StringBuilder("Update SecOperations");
		sql.append(StringUtils.trimToEmpty(type));
		sql.append(" Set OprCode = ?, OprDesc = ?");
		sql.append(", Version = ?, LastMntBy = ?, LastMntOn = ?, RecordStatus= ?");
		sql.append(", RoleCode = ?, NextRoleCode = ?, TaskId = ?, NextTaskId = ?");
		sql.append(", RecordType = ?, WorkflowId = ?");
		sql.append(" Where OprID = ?");

		if (StringUtils.isBlank(type)) {
			sql.append(" and Version = ?");
		}

		logger.debug(Literal.SQL.concat(sql.toString()));

		int recordCount = this.jdbcOperations.update(sql.toString(), ps -> {
			int index = 1;

			ps.setString(index++, so.getOprCode());
			ps.setString(index++, so.getOprDesc());
			ps.setInt(index++, so.getVersion());
			ps.setLong(index++, so.getLastMntBy());
			ps.setTimestamp(index++, so.getLastMntOn());
			ps.setString(index++, so.getRecordStatus());
			ps.setString(index++, so.getRoleCode());
			ps.setString(index++, so.getNextRoleCode());
			ps.setString(index++, so.getTaskId());
			ps.setString(index++, so.getNextTaskId());
			ps.setString(index++, so.getRecordType());
			ps.setLong(index++, so.getWorkflowId());

			ps.setLong(index++, so.getOprID());

			if (StringUtils.isBlank(type)) {
				ps.setInt(index++, so.getVersion() - 1);
			}
		});

		if (recordCount <= 0) {
			throw new ConcurrencyException();
		}
	}

	@Override
	public List<SecurityOperation> getApprovedSecurityOperation() {
		StringBuilder sql = new StringBuilder("Select OprID, OprCode, OprDesc");
		sql.append(", Version , LastMntBy, LastMntOn, RecordStatus, RoleCode, NextRoleCode");
		sql.append(", TaskId, NextTaskId, RecordType, WorkflowId");
		sql.append(" From SecOperations_AView");

		logger.debug(Literal.SQL.concat(sql.toString()));

		return this.jdbcOperations.query(sql.toString(), ps -> {

		}, (rs, rowNum) -> {
			SecurityOperation so = new SecurityOperation();
			so.setOprID(rs.getLong("OprID"));
			so.setOprCode(rs.getString("OprCode"));
			so.setOprDesc(rs.getString("OprDesc"));
			so.setVersion(rs.getInt("Version"));
			so.setLastMntBy(rs.getLong("LastMntBy"));
			so.setLastMntOn(rs.getTimestamp("LastMntOn"));
			so.setRecordStatus(rs.getString("RecordStatus"));
			so.setRoleCode(rs.getString("RoleCode"));
			so.setNextRoleCode(rs.getString("NextRoleCode"));
			so.setTaskId(rs.getString("TaskId"));
			so.setNextTaskId(rs.getString("NextTaskId"));
			so.setRecordType(rs.getString("RecordType"));
			so.setWorkflowId(rs.getLong("WorkflowId"));

			return so;
		});
	}

	@Override
	public boolean isOperationExistByOprCode(String lovDescOprCd) {
		String sql = "Select count(OprCode) from SecOperations Where OprCode = ?";

		logger.debug(Literal.SQL.concat(sql));

		return jdbcOperations.queryForObject(sql, Integer.class, lovDescOprCd) > 0;
	}

	@Override
	public long getSecurityOperationByCode(String oprCode) {
		String sql = "Select OprID From SecOperations Where OprCode = ?";

		logger.debug(Literal.SQL.concat(sql));

		try {
			return this.jdbcOperations.queryForObject(sql, Long.class, oprCode);
		} catch (EmptyResultDataAccessException e) {
			logger.warn(Message.NO_RECORD_FOUND);
			return 0;
		}
	}

	private StringBuilder getSqlQuery(String type) {
		StringBuilder sql = new StringBuilder("Select");
		sql.append(" OprID, OprCode, OprDesc");
		sql.append(", Version, LastMntBy, LastMntOn, RecordStatus, RoleCode");
		sql.append(", NextRoleCode, TaskId, NextTaskId, RecordType, WorkflowId");
		sql.append(" From SecOperations");
		sql.append(StringUtils.trimToEmpty(type));
		return sql;
	}

	private class SecurityOperationsRM implements RowMapper<SecurityOperation> {

		private SecurityOperationsRM() {
			super();
		}

		@Override
		public SecurityOperation mapRow(ResultSet rs, int rowNum) throws SQLException {
			SecurityOperation so = new SecurityOperation();

			so.setOprID(rs.getLong("OprID"));
			so.setOprCode(rs.getString("OprCode"));
			so.setOprDesc(rs.getString("OprDesc"));
			so.setVersion(rs.getInt("Version"));
			so.setLastMntBy(rs.getLong("LastMntBy"));
			so.setLastMntOn(rs.getTimestamp("LastMntOn"));
			so.setRecordStatus(rs.getString("RecordStatus"));
			so.setRoleCode(rs.getString("RoleCode"));
			so.setNextRoleCode(rs.getString("NextRoleCode"));
			so.setTaskId(rs.getString("TaskId"));
			so.setNextTaskId(rs.getString("NextTaskId"));
			so.setRecordType(rs.getString("RecordType"));
			so.setWorkflowId(rs.getLong("WorkflowId"));

			return so;
		}
	}
}