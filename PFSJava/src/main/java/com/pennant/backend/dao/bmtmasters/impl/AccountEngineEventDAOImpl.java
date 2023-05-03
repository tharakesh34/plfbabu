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
 * * FileName : AccountEngineEventDAOImpl.java * * Author : PENNANT TECHONOLOGIES * * Creation Date : 27-06-2011 * *
 * Modified Date : 27-06-2011 * * Description : * *
 ********************************************************************************************
 * Date Author Version Comments *
 ********************************************************************************************
 * 27-06-2011 Pennant 0.1 * * * * * * * * *
 ********************************************************************************************
 */
package com.pennant.backend.dao.bmtmasters.impl;

import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.EmptyResultDataAccessException;

import com.pennant.backend.dao.bmtmasters.AccountEngineEventDAO;
import com.pennant.backend.model.WorkFlowDetails;
import com.pennant.backend.model.bmtmasters.AccountEngineEvent;
import com.pennant.backend.util.WorkFlowUtil;
import com.pennanttech.pennapps.core.ConcurrencyException;
import com.pennanttech.pennapps.core.DependencyFoundException;
import com.pennanttech.pennapps.core.jdbc.BasicDao;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pennapps.core.resource.Message;

/**
 * DAO methods implementation for the <b>AccountEngineEvent model</b> class.<br>
 * 
 */
public class AccountEngineEventDAOImpl extends BasicDao<AccountEngineEvent> implements AccountEngineEventDAO {
	private static Logger logger = LogManager.getLogger(AccountEngineEventDAOImpl.class);

	public AccountEngineEventDAOImpl() {
		super();
	}

	@Override
	public AccountEngineEvent getAccountEngineEvent() {
		WorkFlowDetails workFlowDetails = WorkFlowUtil.getWorkFlowDetails("AccountEngineEvent");
		AccountEngineEvent accountEngineEvent = new AccountEngineEvent();
		if (workFlowDetails != null) {
			accountEngineEvent.setWorkflowId(workFlowDetails.getWorkFlowId());
		}
		return accountEngineEvent;
	}

	@Override
	public AccountEngineEvent getNewAccountEngineEvent() {
		AccountEngineEvent accountEngineEvent = getAccountEngineEvent();
		accountEngineEvent.setNewRecord(true);
		return accountEngineEvent;
	}

	@Override
	public AccountEngineEvent getAccountEngineEventById(final String aeEventCode, String type) {
		StringBuilder sql = new StringBuilder();
		sql.append("Select AEEventCode, AEEventCodeDesc,");
		sql.append(" Version, LastMntOn, LastMntBy, RecordStatus, RoleCode, NextRoleCode");
		sql.append(", TaskId, NextTaskId, RecordType, WorkflowId");
		sql.append(" From  BMTAEEvents");
		sql.append(StringUtils.trimToEmpty(type));
		sql.append(" Where AEEventCode = ?");

		logger.debug(Literal.SQL.concat(sql.toString()));
		try {
			return this.jdbcOperations.queryForObject(sql.toString(), (rs, rowNum) -> {
				AccountEngineEvent aeEvent = new AccountEngineEvent();

				aeEvent.setAEEventCode(rs.getString("AEEventCode"));
				aeEvent.setAEEventCodeDesc(rs.getString("AEEventCodeDesc"));
				aeEvent.setVersion(rs.getInt("Version"));
				aeEvent.setLastMntOn(rs.getTimestamp("LastMntOn"));
				aeEvent.setLastMntBy(rs.getLong("LastMntBy"));
				aeEvent.setRecordStatus(rs.getString("RecordStatus"));
				aeEvent.setRoleCode(rs.getString("RoleCode"));
				aeEvent.setNextRoleCode(rs.getString("NextRoleCode"));
				aeEvent.setTaskId(rs.getString("TaskId"));
				aeEvent.setNextTaskId(rs.getString("NextTaskId"));
				aeEvent.setRecordType(rs.getString("RecordType"));
				aeEvent.setWorkflowId(rs.getLong("WorkflowId"));

				return aeEvent;
			}, aeEventCode);
		} catch (EmptyResultDataAccessException e) {
			logger.warn(Message.NO_RECORD_FOUND);
			return null;
		}
	}

	@Override
	public void delete(AccountEngineEvent aeevent, String type) {
		StringBuilder sql = new StringBuilder();
		sql.append("Delete From BMTAEEvents");
		sql.append(StringUtils.trimToEmpty(type));
		sql.append(" Where AEEventCode = ?");

		logger.debug(Literal.SQL.concat(sql.toString()));

		try {
			if (this.jdbcOperations.update(sql.toString(), aeevent.getAEEventCode()) <= 0) {
				throw new ConcurrencyException();
			}
		} catch (DataAccessException e) {
			throw new DependencyFoundException(e);
		}
	}

	@Override
	public String save(AccountEngineEvent aeEvent, String type) {
		StringBuilder sql = new StringBuilder();
		sql.append("Insert Into BMTAEEvents");
		sql.append(StringUtils.trimToEmpty(type));
		sql.append(" (AEEventCode, AEEventCodeDesc,");
		sql.append(" Version , LastMntBy, LastMntOn, RecordStatus, RoleCode, NextRoleCode, TaskId, NextTaskId,");
		sql.append(" RecordType, WorkflowId)");
		sql.append(" Values(?, ?, ? , ?, ?, ?, ?, ?, ?, ?, ?, ?)");

		logger.debug(Literal.SQL.concat(sql.toString()));

		this.jdbcOperations.update(sql.toString(), ps -> {
			int index = 0;
			ps.setString(++index, aeEvent.getAEEventCode());
			ps.setString(++index, aeEvent.getAEEventCodeDesc());
			ps.setInt(++index, aeEvent.getVersion());
			ps.setLong(++index, aeEvent.getLastMntBy());
			ps.setTimestamp(++index, aeEvent.getLastMntOn());
			ps.setString(++index, aeEvent.getRecordStatus());
			ps.setString(++index, aeEvent.getRoleCode());
			ps.setString(++index, aeEvent.getNextRoleCode());
			ps.setString(++index, aeEvent.getTaskId());
			ps.setString(++index, aeEvent.getNextTaskId());
			ps.setString(++index, aeEvent.getRecordType());
			ps.setLong(++index, aeEvent.getWorkflowId());

		});

		return aeEvent.getId();
	}

	@Override
	public void update(AccountEngineEvent aeEvent, String type) {
		StringBuilder sql = new StringBuilder();

		sql.append("Update BMTAEEvents");
		sql.append(StringUtils.trimToEmpty(type));
		sql.append(" Set AEEventCodeDesc = ?");
		sql.append(", Version = ?, LastMntBy = ?, LastMntOn = ?");
		sql.append(", RecordStatus= ?, RoleCode = ?, NextRoleCode = ?, TaskId = ?");
		sql.append(", NextTaskId = ?, RecordType = ?, WorkflowId = ?");
		sql.append(" Where AEEventCode = ?");

		if (!type.endsWith("_Temp")) {
			sql.append(" AND Version= ? - 1");
		}

		logger.debug(Literal.SQL.concat(sql.toString()));

		this.jdbcOperations.update(sql.toString(), ps -> {
			int index = 0;

			ps.setString(++index, aeEvent.getAEEventCodeDesc());
			ps.setInt(++index, aeEvent.getVersion());
			ps.setLong(++index, aeEvent.getLastMntBy());
			ps.setTimestamp(++index, aeEvent.getLastMntOn());
			ps.setString(++index, aeEvent.getRecordStatus());
			ps.setString(++index, aeEvent.getRoleCode());
			ps.setString(++index, aeEvent.getNextRoleCode());
			ps.setString(++index, aeEvent.getTaskId());
			ps.setString(++index, aeEvent.getNextTaskId());
			ps.setString(++index, aeEvent.getRecordType());
			ps.setLong(++index, aeEvent.getWorkflowId());

			ps.setString(++index, aeEvent.getAEEventCode());

			if (!type.endsWith("_Temp")) {
				ps.setInt(++index, aeEvent.getVersion());
			}

		});
	}

	@Override
	public List<AccountEngineEvent> getAccountEngineEvents() {
		StringBuilder sql = new StringBuilder();
		sql.append("Select AEEventCode, AEEventCodeDesc");
		sql.append(" From BMTAEEvents Where Active = ?");

		logger.debug(Literal.SQL.concat(sql.toString()));
		return this.jdbcOperations.query(sql.toString(), (rs, rowNum) -> {
			AccountEngineEvent aeEvent = new AccountEngineEvent();

			aeEvent.setAEEventCode(rs.getString("AEEventCode"));
			aeEvent.setAEEventCodeDesc(rs.getString("AEEventCodeDesc"));

			return aeEvent;
		}, 1);

	}
}