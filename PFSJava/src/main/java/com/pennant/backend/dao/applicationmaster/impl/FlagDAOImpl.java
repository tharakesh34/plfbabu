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
 * * FileName : FlagDAOImpl.java * * Author : PENNANT TECHONOLOGIES * * Creation Date : 14-07-2015 * * Modified Date :
 * 14-07-2015 * * Description : * *
 ********************************************************************************************
 * Date Author Version Comments *
 ********************************************************************************************
 * 14-07-2015 Pennant 0.1 * * * * * * * * *
 ********************************************************************************************
 */
package com.pennant.backend.dao.applicationmaster.impl;

import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.dao.EmptyResultDataAccessException;

import com.pennant.backend.dao.applicationmaster.FlagDAO;
import com.pennant.backend.model.applicationmasters.Flag;
import com.pennanttech.pennapps.core.ConcurrencyException;
import com.pennanttech.pennapps.core.DependencyFoundException;
import com.pennanttech.pennapps.core.jdbc.BasicDao;
import com.pennanttech.pennapps.core.jdbc.JdbcUtil;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pff.core.TableType;
import com.pennanttech.pff.core.util.QueryUtil;

/**
 * Data access layer implementation for <code>Flag</code> with set of CRUD operations.
 */

public class FlagDAOImpl extends BasicDao<Flag> implements FlagDAO {
	private static Logger logger = LogManager.getLogger(FlagDAOImpl.class);

	public FlagDAOImpl() {
		super();
	}

	@Override
	public Flag getFlagById(final String id, String type) {
		StringBuilder sql = new StringBuilder("Select");
		sql.append(" FlagCode, FlagDesc, Active");
		sql.append(", Version , LastMntBy, LastMntOn, RecordStatus, RoleCode");
		sql.append(", NextRoleCode, TaskId, NextTaskId, RecordType, WorkflowId");
		sql.append(" From Flags");
		sql.append(StringUtils.trimToEmpty(type));
		sql.append(" Where FlagCode = ?");

		logger.debug(Literal.SQL + sql.toString());

		try {
			return this.jdbcOperations.queryForObject(sql.toString(), (rs, rowNum) -> {
				Flag flag = new Flag();

				flag.setFlagCode(rs.getString("FlagCode"));
				flag.setFlagDesc(rs.getString("FlagDesc"));
				flag.setActive(rs.getBoolean("Active"));
				flag.setVersion(rs.getInt("Version"));
				flag.setLastMntBy(rs.getLong("LastMntBy"));
				flag.setLastMntOn(rs.getTimestamp("LastMntOn"));
				flag.setRecordStatus(rs.getString("RecordStatus"));
				flag.setRoleCode(rs.getString("RoleCode"));
				flag.setNextRoleCode(rs.getString("NextRoleCode"));
				flag.setTaskId(rs.getString("TaskId"));
				flag.setNextTaskId(rs.getString("NextTaskId"));
				flag.setRecordType(rs.getString("RecordType"));
				flag.setWorkflowId(rs.getLong("WorkflowId"));

				return flag;
			}, id);
		} catch (EmptyResultDataAccessException e) {
			//
		}

		return null;
	}

	@Override
	public boolean isDuplicateKey(String flagCode, TableType tableType) {
		String sql;
		String whereClause = "FlagCode = ?";

		Object[] obj = new Object[] { flagCode };

		switch (tableType) {
		case MAIN_TAB:
			sql = QueryUtil.getCountQuery("Flags", whereClause);
			break;
		case TEMP_TAB:
			sql = QueryUtil.getCountQuery("Flags_Temp", whereClause);
			break;
		default:
			sql = QueryUtil.getCountQuery(new String[] { "Flags_Temp", "Flags" }, whereClause);

			obj = new Object[] { flagCode };

			break;
		}

		logger.debug(Literal.SQL + sql);

		return jdbcOperations.queryForObject(sql, Integer.class, obj) > 0;
	}

	@Override
	public String save(Flag flag, TableType tableType) {
		StringBuilder sql = new StringBuilder("Insert into Flags");
		sql.append(tableType.getSuffix());
		sql.append(" (FlagCode, FlagDesc, Active");
		sql.append(", Version , LastMntBy, LastMntOn, RecordStatus, RoleCode");
		sql.append(", NextRoleCode, TaskId, NextTaskId, RecordType, WorkflowId");
		sql.append(" Values(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)");

		logger.trace(Literal.SQL + sql.toString());

		try {
			jdbcOperations.update(sql.toString(), ps -> {
				int index = 1;

				ps.setString(index++, flag.getFlagCode());
				ps.setString(index++, flag.getFlagDesc());
				ps.setBoolean(index++, flag.isActive());
				ps.setInt(index++, flag.getVersion());
				ps.setLong(index++, JdbcUtil.setLong(flag.getLastMntBy()));
				ps.setTimestamp(index++, flag.getLastMntOn());
				ps.setString(index++, flag.getRecordStatus());
				ps.setString(index++, flag.getRoleCode());
				ps.setString(index++, flag.getNextRoleCode());
				ps.setString(index++, flag.getTaskId());
				ps.setString(index++, flag.getNextTaskId());
				ps.setString(index++, flag.getRecordType());
				ps.setLong(index++, JdbcUtil.setLong(flag.getWorkflowId()));
			});
		} catch (DuplicateKeyException e) {
			throw new ConcurrencyException(e);
		}

		return flag.getFlagCode();
	}

	@Override
	public void update(Flag flag, TableType tableType) {
		StringBuilder sql = new StringBuilder("Update Flags");
		sql.append(tableType.getSuffix());
		sql.append(" Set FlagDesc = ?, Active = ?");
		sql.append(", Version = ? , LastMntBy = ?, LastMntOn = ?, RecordStatus= ?, RoleCode = ?");
		sql.append(", NextRoleCode = ?, TaskId = ?, NextTaskId = ?, RecordType = ?, WorkflowId = ?");
		sql.append(" Where FlagCode = ?");
		sql.append(QueryUtil.getConcurrencyClause(tableType));

		logger.debug(Literal.SQL + sql.toString());

		int recordCount = jdbcOperations.update(sql.toString(), ps -> {
			int index = 1;

			ps.setString(index++, flag.getFlagDesc());
			ps.setBoolean(index++, flag.isActive());
			ps.setInt(index++, flag.getVersion());
			ps.setLong(index++, JdbcUtil.setLong(flag.getLastMntBy()));
			ps.setTimestamp(index++, flag.getLastMntOn());
			ps.setString(index++, flag.getRecordStatus());
			ps.setString(index++, flag.getRoleCode());
			ps.setString(index++, flag.getNextRoleCode());
			ps.setString(index++, flag.getTaskId());
			ps.setString(index++, flag.getNextTaskId());
			ps.setString(index++, flag.getRecordType());
			ps.setLong(index++, JdbcUtil.setLong(flag.getWorkflowId()));

			ps.setString(index++, flag.getFlagCode());
			if (tableType == TableType.TEMP_TAB) {
				ps.setTimestamp(index++, flag.getPrevMntOn());
			} else {
				ps.setInt(index++, flag.getVersion() - 1);
			}
		});

		if (recordCount == 0) {
			throw new ConcurrencyException();
		}
	}

	@Override
	public void delete(Flag flag, TableType tableType) {
		StringBuilder sql = new StringBuilder("Delete from Flags");
		sql.append(tableType.getSuffix());
		sql.append(" where FlagCode = ?");
		sql.append(QueryUtil.getConcurrencyClause(tableType));

		logger.debug(Literal.SQL + sql.toString());

		try {
			int recordCount = jdbcOperations.update(sql.toString(), ps -> {
				int index = 1;

				ps.setString(index++, flag.getFlagCode());
				if (tableType == TableType.TEMP_TAB) {
					ps.setTimestamp(index++, flag.getPrevMntOn());
				} else {
					ps.setInt(index++, flag.getVersion() - 1);
				}
			});

			if (recordCount == 0) {
				throw new ConcurrencyException();
			}
		} catch (DataAccessException e) {
			throw new DependencyFoundException(e);
		}
	}
}