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
 * * FileName : CustomerPhoneNumberDAOImpl.java * * Author : PENNANT TECHONOLOGIES * * Creation Date : 26-05-2011 * *
 * Modified Date : 26-05-2011 * * Description : * *
 ********************************************************************************************
 * Date Author Version Comments *
 ********************************************************************************************
 * 26-05-2011 Pennant 0.1 * * * * * * * * *
 ********************************************************************************************
 */
package com.pennant.backend.dao.finance.financialSummary.impl;

import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;

import com.pennant.backend.dao.finance.financialSummary.SanctionConditionsDAO;
import com.pennant.backend.model.finance.financialsummary.SanctionConditions;
import com.pennanttech.pennapps.core.ConcurrencyException;
import com.pennanttech.pennapps.core.DependencyFoundException;
import com.pennanttech.pennapps.core.jdbc.SequenceDao;
import com.pennanttech.pennapps.core.resource.Literal;

/**
 * DAO methods implementation for the <b>CustomerPhoneNumber model</b> class.<br>
 * 
 */
public class SanctionConditionsDAOImpl extends SequenceDao<SanctionConditions> implements SanctionConditionsDAO {
	private static Logger logger = LogManager.getLogger(SanctionConditionsDAOImpl.class);

	public SanctionConditionsDAOImpl() {
		super();
	}

	public List<SanctionConditions> getSanctionConditions(long finID) {
		StringBuilder sql = new StringBuilder("Select");
		sql.append(" t1.Id, t1.SeqNo, t1.SanctionCondition, t1.Status, t2.FinID, t2.FinReference, t1.Version");
		sql.append(", t1.Remarks, t1.LastMntBy, t1.LastMntOn, t1.RecordStatus, t1.RoleCode, t1.NextRoleCode");
		sql.append(", t1.TaskId, t1.NextTaskId, t1.RecordType, t1.WorkflowId");
		sql.append(" From Sanction_Conditions_Temp t1");
		sql.append(" Left Join FinanceMain_Temp t2 on t2.FinID =  t1.FinID");
		sql.append(" Where t2.FinID = ?");
		sql.append(" Union All");
		sql.append(" Select");
		sql.append(" t1.Id, t1.SeqNo, t1.SanctionCondition, t1.Status, t2.FinID, t2.FinReference, t1.Version");
		sql.append(", t1.Remarks, t1.LastMntBy, t1.LastMntOn, t1.RecordStatus, t1.RoleCode, t1.NextRoleCode");
		sql.append(", t1.TaskId, t1.NextTaskId, t1.RecordType, t1.WorkflowId");
		sql.append(" From Sanction_Conditions t1");
		sql.append(" Left Join FinanceMain t2 on t2.FinID =  t1.FinID");
		sql.append(" Where not Exists ( Select 1 From Sanction_conditions_Temp Where Id = t1.Id)");
		sql.append(" and t2.FinID = ?");

		logger.debug(Literal.SQL + sql.toString());

		return this.jdbcOperations.query(sql.toString(), ps -> {
			int index = 1;

			ps.setLong(index++, finID);
			ps.setLong(index, finID);
		}, (rs, rowNum) -> {
			SanctionConditions sc = new SanctionConditions();

			sc.setId(rs.getLong("Id"));
			sc.setSeqNo(rs.getLong("SeqNo"));
			sc.setSanctionCondition(rs.getString("SanctionCondition"));
			sc.setStatus(rs.getString("Status"));
			sc.setRemarks(rs.getString("Remarks"));
			sc.setFinID(rs.getLong("FinID"));
			sc.setFinReference(rs.getString("FinReference"));
			sc.setVersion(rs.getInt("Version"));
			sc.setLastMntBy(rs.getLong("LastMntBy"));
			sc.setLastMntOn(rs.getTimestamp("LastMntOn"));
			sc.setRecordStatus(rs.getString("RecordStatus"));
			sc.setRoleCode(rs.getString("RoleCode"));
			sc.setNextRoleCode(rs.getString("NextRoleCode"));
			sc.setTaskId(rs.getString("TaskId"));
			sc.setNextTaskId(rs.getString("NextTaskId"));
			sc.setRecordType(rs.getString("RecordType"));
			sc.setWorkflowId(rs.getLong("WorkflowId"));

			return sc;
		});
	}

	@Override
	public void delete(SanctionConditions sc, String type) {
		StringBuilder sql = new StringBuilder("Delete From Sanction_Conditions");
		sql.append(StringUtils.trimToEmpty(type));
		sql.append(" Where Id = ? and FinID = ?");

		logger.debug(Literal.SQL + sql.toString());

		try {
			int recordCount = this.jdbcOperations.update(sql.toString(), ps -> {
				int index = 1;

				ps.setLong(index++, sc.getId());
				ps.setLong(index, sc.getFinID());
			});

			if (recordCount <= 0) {
				throw new ConcurrencyException();
			}

		} catch (DataAccessException e) {
			throw new DependencyFoundException(e);
		}
	}

	@Override
	public long save(SanctionConditions sc, String type) {

		if (sc.getId() == Long.MIN_VALUE) {
			sc.setId(getNextValue("SEQ_SANCTION_CONDITIONS"));
		}

		StringBuilder sql = new StringBuilder("Insert Into SANCTION_CONDITIONS");
		sql.append(StringUtils.trimToEmpty(type));
		sql.append(" (Id, SeqNo, SanctionCondition, Status, FinID, FinReference, Remarks");
		sql.append(", Version, LastMntBy, LastMntOn, RecordStatus, RoleCode, NextRoleCode");
		sql.append(", TaskId, NextTaskId, RecordType, WorkflowId)");
		sql.append(" Values(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)");

		logger.debug(Literal.SQL + sql.toString());

		try {
			this.jdbcOperations.update(sql.toString(), ps -> {
				int index = 1;

				ps.setLong(index++, sc.getId());
				ps.setLong(index++, sc.getSeqNo());
				ps.setString(index++, sc.getSanctionCondition());
				ps.setString(index++, sc.getStatus());
				ps.setLong(index++, sc.getFinID());
				ps.setString(index++, sc.getFinReference());
				ps.setString(index++, sc.getRemarks());
				ps.setInt(index++, sc.getVersion());
				ps.setLong(index++, sc.getLastMntBy());
				ps.setTimestamp(index++, sc.getLastMntOn());
				ps.setString(index++, sc.getRecordStatus());
				ps.setString(index++, sc.getRoleCode());
				ps.setString(index++, sc.getNextRoleCode());
				ps.setString(index++, sc.getTaskId());
				ps.setString(index++, sc.getNextTaskId());
				ps.setString(index++, sc.getRecordType());
				ps.setLong(index, sc.getWorkflowId());
			});
		} catch (DuplicateKeyException e) {
			throw new ConcurrencyException(e);
		}

		return sc.getId();
	}

	@Override
	public void update(SanctionConditions sc, String type) {
		StringBuilder sql = new StringBuilder("Update Sanction_Conditions");
		sql.append(StringUtils.trimToEmpty(type));
		sql.append(" Set SanctionCondition = ?, Status = ?, Remarks=?");
		sql.append(", Version = ?, LastMntBy = ?, LastMntOn = ?, RecordStatus = ?, RoleCode = ?");
		sql.append(", NextRoleCode = ?, TaskId = ?, NextTaskId = ?, RecordType = ?, WorkflowId = ?");
		sql.append(" Where Id = ? and FinID = ?");

		if (!type.endsWith("_Temp")) {
			sql.append("  and Version = ?");
		}

		logger.debug(Literal.SQL + sql.toString());

		int recordCount = this.jdbcOperations.update(sql.toString(), ps -> {
			int index = 1;

			ps.setString(index++, sc.getSanctionCondition());
			ps.setString(index++, sc.getStatus());
			ps.setString(index++, sc.getRemarks());
			ps.setInt(index++, sc.getVersion());
			ps.setLong(index++, sc.getLastMntBy());
			ps.setTimestamp(index++, sc.getLastMntOn());
			ps.setString(index++, sc.getRecordStatus());
			ps.setString(index++, sc.getRoleCode());
			ps.setString(index++, sc.getNextRoleCode());
			ps.setString(index++, sc.getTaskId());
			ps.setString(index++, sc.getNextTaskId());
			ps.setString(index++, sc.getRecordType());
			ps.setLong(index++, sc.getWorkflowId());
			ps.setLong(index++, sc.getId());
			ps.setLong(index++, sc.getFinID());

			if (!type.endsWith("_Temp")) {
				ps.setInt(index, sc.getVersion() - 1);
			}

		});

		if (recordCount <= 0) {
			throw new ConcurrencyException();
		}
	}

	// FIXME:FinID seems method not using
	@Override
	public int getVersion(long id, String sanctionCondition) {
		logger.debug("Entering");

		MapSqlParameterSource source = new MapSqlParameterSource();
		source.addValue("id", id);
		source.addValue("sanctionCondition", sanctionCondition);

		StringBuilder selectSql = new StringBuilder();
		selectSql.append("SELECT Version FROM SANCTION_CONDITIONS");
		selectSql.append(" WHERE id = :id AND finReference = :finReference");

		logger.debug("insertSql: " + selectSql.toString());

		logger.debug("Leaving");

		return this.jdbcTemplate.queryForObject(selectSql.toString(), source, Integer.class);
	}

}