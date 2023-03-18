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
 * * FileName : TanAssignmentDAOImpl.java * * Author : PENNANT TECHONOLOGIES * * Creation Date : 08-09-2020 * * Modified
 * Date : 08-09-2020 * * Description : * *
 ********************************************************************************************
 * Date Author Version Comments *
 ********************************************************************************************
 * 08-09-2020 PENNANT 0.1 * * * * * * * * *
 ********************************************************************************************
 */
package com.pennant.backend.dao.tandetails.impl;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.RowMapper;

import com.pennant.backend.dao.tandetails.TanAssignmentDAO;
import com.pennant.backend.util.PennantConstants;
import com.pennanttech.finance.tds.cerificate.model.TanAssignment;
import com.pennanttech.finance.tds.cerificate.model.TanDetail;
import com.pennanttech.pennapps.core.ConcurrencyException;
import com.pennanttech.pennapps.core.jdbc.SequenceDao;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pff.core.TableType;
import com.pennanttech.pff.core.util.QueryUtil;

public class TanAssignmentDAOImpl extends SequenceDao<TanAssignment> implements TanAssignmentDAO {
	private static Logger logger = LogManager.getLogger(TanAssignmentDAOImpl.class);

	public TanAssignmentDAOImpl() {
		super();
	}

	private StringBuilder getSelectQuery(TableType tableType) {
		StringBuilder sql = new StringBuilder("Select");
		sql.append(" Id, CustID, FinReference, TanID, Version, LastMntBy, LastMntOn");
		sql.append(", RecordStatus, RoleCode, NextRoleCode, TaskId, NextTaskId, RecordType, WorkflowId");
		sql.append(" From TAN_ASSIGNMENTS");
		sql.append(tableType.getSuffix());
		return sql;
	}

	public class TanAssignmentRowMapper implements RowMapper<TanAssignment> {
		@Override
		public TanAssignment mapRow(ResultSet rs, int rowNum) throws SQLException {
			TanAssignment ta = new TanAssignment();

			ta.setId(rs.getLong("Id"));
			ta.setCustID(rs.getLong("CustID"));
			ta.setFinReference(rs.getString("FinReference"));
			ta.setTanID(rs.getLong("TanID"));
			ta.setVersion(rs.getInt("Version"));
			ta.setLastMntBy(rs.getLong("LastMntBy"));
			ta.setLastMntOn(rs.getTimestamp("LastMntOn"));
			ta.setRecordStatus(rs.getString("RecordStatus"));
			ta.setRoleCode(rs.getString("RoleCode"));
			ta.setNextRoleCode(rs.getString("NextRoleCode"));
			ta.setTaskId(rs.getString("TaskId"));
			ta.setNextTaskId(rs.getString("NextTaskId"));
			ta.setRecordType(rs.getString("RecordType"));
			ta.setWorkflowId(rs.getLong("WorkflowId"));

			return ta;
		}
	}

	@Override
	public String save(TanAssignment tanAssignment, TableType tableType) {
		StringBuilder sql = new StringBuilder("insert into TAN_ASSIGNMENTS");
		sql.append(tableType.getSuffix());
		sql.append(" (Id, CustId, FinReference, TanID");
		sql.append(", Version, LastMntBy, LastMntOn, RecordStatus");
		sql.append(", RoleCode, NextRoleCode, TaskId, NextTaskId, RecordType, WorkflowId)");
		sql.append(" Values (?, ?, ?, ?, ?");
		sql.append(", ?, ?, ?, ?, ?, ?, ?, ?, ?)");

		if ((tanAssignment.getId() == Long.MIN_VALUE || tanAssignment.getId() == 0) && tableType == TableType.TEMP_TAB
				&& PennantConstants.RECORD_TYPE_NEW.equals(tanAssignment.getRecordType())) {
			tanAssignment.setId(getNextValue("SEQTAN_ASSIGNMENTS"));
		}

		logger.debug(Literal.SQL + sql.toString());

		try {
			jdbcOperations.update(sql.toString(), ps -> {
				int index = 1;

				ps.setLong(index++, tanAssignment.getId());
				ps.setLong(index++, tanAssignment.getCustID());
				ps.setString(index++, tanAssignment.getFinReference());
				ps.setLong(index++, tanAssignment.getTanID());
				ps.setInt(index++, tanAssignment.getVersion());
				ps.setLong(index++, tanAssignment.getLastMntBy());
				ps.setTimestamp(index++, tanAssignment.getLastMntOn());
				ps.setString(index++, tanAssignment.getRecordStatus());
				ps.setString(index++, tanAssignment.getRoleCode());
				ps.setString(index++, tanAssignment.getNextRoleCode());
				ps.setString(index++, tanAssignment.getTaskId());
				ps.setString(index++, tanAssignment.getNextTaskId());
				ps.setString(index++, tanAssignment.getRecordType());
				ps.setLong(index, tanAssignment.getWorkflowId());
			});

		} catch (DuplicateKeyException e) {
			throw new ConcurrencyException(e);
		}

		return Long.toString(tanAssignment.getId());
	}

	@Override
	public void update(TanAssignment tanAssignment, TableType tableType) {
		StringBuilder sql = new StringBuilder("update TAN_ASSIGNMENTS");
		sql.append(tableType.getSuffix());
		sql.append(" set CustID = ?, FinReference = ?, TanID = ?");
		sql.append(", LastMntOn = ?, RecordStatus = ?, RoleCode = ?");
		sql.append(", NextRoleCode = ?, TaskId = ?, NextTaskId = ?");
		sql.append(", RecordType = ?, WorkflowId = ?");
		sql.append(" Where Id = ?");

		logger.debug(Literal.SQL + sql.toString());

		this.jdbcOperations.update(sql.toString(), ps -> {
			int index = 1;

			ps.setLong(index++, tanAssignment.getCustID());
			ps.setString(index++, tanAssignment.getFinReference());
			ps.setLong(index++, tanAssignment.getTanID());
			ps.setTimestamp(index++, tanAssignment.getLastMntOn());
			ps.setString(index++, tanAssignment.getRecordStatus());
			ps.setString(index++, tanAssignment.getRoleCode());
			ps.setString(index++, tanAssignment.getNextRoleCode());
			ps.setString(index++, tanAssignment.getTaskId());
			ps.setString(index++, tanAssignment.getNextTaskId());
			ps.setString(index++, tanAssignment.getRecordType());
			ps.setLong(index++, tanAssignment.getWorkflowId());

			ps.setLong(index, tanAssignment.getTanID());

		});

	}

	@Override
	public void delete(TanAssignment tanAssignment, TableType tableType) {
		StringBuilder sql = new StringBuilder("Delete from TAN_ASSIGNMENTS");
		sql.append(tableType.getSuffix());
		sql.append(" Where Id = ? ");

		logger.debug(Literal.SQL + sql.toString());
		this.jdbcOperations.update(sql.toString(), ps -> {
			int index = 1;

			ps.setLong(index, tanAssignment.getId());

		});

	}

	@Override
	public boolean isDuplicateKey(long id, String finReference, long custID, long tanID, TableType tableType) {
		String sql;
		String whereClause = "FinReference = ? and CustID = ? and TanID = ? and Id != ?";

		Object[] obj = new Object[] { finReference, custID, tanID, id };

		switch (tableType) {
		case MAIN_TAB:
			sql = QueryUtil.getCountQuery("TAN_ASSIGNMENTS", whereClause);
			break;
		case TEMP_TAB:
			sql = QueryUtil.getCountQuery("TAN_ASSIGNMENTS_TEMP", whereClause);
			break;
		default:
			sql = QueryUtil.getCountQuery(new String[] { "TAN_ASSIGNMENTS_TEMP", "TAN_ASSIGNMENTS" }, whereClause);
			obj = new Object[] { finReference, custID, tanID, id, finReference, custID, tanID, id };

			break;
		}

		logger.debug(Literal.SQL + sql);

		return jdbcOperations.queryForObject(sql, Integer.class, obj) > 0;
	}

	public List<TanAssignment> getTanAssignmentsByFinReference(String finReference, TableType tableType) {
		StringBuilder sql = getSelectQuery(tableType);
		sql.append(" Where FinReference = ?");

		logger.trace(Literal.SQL + sql.toString());

		return this.jdbcOperations.query(sql.toString(), ps -> ps.setString(1, finReference),
				new TanAssignmentRowMapper());
	}

	@Override
	public long getIdByFinReferenceAndTanId(String finReference, long tanID, TableType view) {
		long id = 0;

		StringBuilder sql = new StringBuilder("SELECT ID FROM TAN_ASSIGNMENTS");
		sql.append(StringUtils.trimToEmpty(view.getSuffix()));
		sql.append(" Where FinReference = ? and TanID = ?");

		logger.debug(Literal.SQL + sql.toString());

		try {
			id = this.jdbcOperations.queryForObject(sql.toString(), Long.class, finReference, tanID);
		} catch (EmptyResultDataAccessException e) {
			logger.error(Literal.EXCEPTION, e);
			id = 0;
		}
		tanID = id + 1;
		return id;
	}

	public List<TanAssignment> getTanAssignments(long custId, String finReference, TableType tableType) {
		StringBuilder sql = new StringBuilder("Select");
		sql.append(" TA.ID, TA.CustId, TA.FinReference, TA.TanId, TD.TanNumber, TD.TanHolderName");
		sql.append(", TA.Version, TA.LastMntBy, TA.LastMntOn, TA.RecordStatus, TA.RoleCode");
		sql.append(", TA.NextRoleCode, TA.TaskId, TA.NextTaskId, TA.RecordType, TA.WorkflowId");
		sql.append(", TD.Version TDVersion, TD.LastMntBy TDLastMntBy, TD.LastMntOn TDLastMntOn");
		sql.append(", TD.RecordStatus TDRecordStatus, TD.RoleCode TDRoleCode, TD.NextRoleCode TDNextRoleCode");
		sql.append(", TD.TaskId TDTaskId, TD.NextTaskId TDNextTaskId, TD.RecordType TDRecordType");
		sql.append(", TD.WorkflowId TDWorkflowId From Tan_Assignments");
		sql.append(tableType.getSuffix());
		sql.append(" TA Inner Join Tan_Details");

		if (tableType.equals(TableType.TEMP_TAB)) {
			sql.append(TableType.VIEW.getSuffix());
		}

		sql.append(" TD on TD.Id = TA.TanId");
		sql.append(" Where TA.CustID = ?");

		if (StringUtils.isNotEmpty(finReference)) {
			sql.append(" AND TA.FinReference = ?");
		}

		logger.debug(Literal.SQL + sql.toString());

		return this.jdbcOperations.query(sql.toString(), ps -> {
			int index = 1;

			ps.setLong(index++, custId);
			if (StringUtils.isNotEmpty(finReference)) {
				ps.setString(index, finReference);
			}
		}, (rs, rowNum) -> {
			TanAssignment ta = new TanAssignment();
			TanDetail td = new TanDetail();

			ta.setId(rs.getLong("Id"));
			ta.setCustID(rs.getLong("CustID"));
			ta.setFinReference(rs.getString("FinReference"));
			ta.setTanID(rs.getLong("TanID"));
			ta.setVersion(rs.getInt("Version"));
			ta.setLastMntBy(rs.getLong("LastMntBy"));
			ta.setLastMntOn(rs.getTimestamp("LastMntOn"));
			ta.setRecordStatus(rs.getString("RecordStatus"));
			ta.setRoleCode(rs.getString("RoleCode"));
			ta.setNextRoleCode(rs.getString("NextRoleCode"));
			ta.setTaskId(rs.getString("TaskId"));
			ta.setNextTaskId(rs.getString("NextTaskId"));
			ta.setRecordType(rs.getString("RecordType"));
			ta.setWorkflowId(rs.getLong("WorkflowId"));

			td.setId(rs.getLong("TanID"));
			td.setTanNumber(rs.getString("TanNumber"));
			td.setTanHolderName(rs.getString("TanHolderName"));
			td.setFinReference(rs.getString("finReference"));
			td.setVersion(rs.getInt("TDVersion"));
			td.setLastMntBy(rs.getLong("TDLastMntBy"));
			td.setLastMntOn(rs.getTimestamp("TDLastMntOn"));
			td.setRecordStatus(rs.getString("TDRecordStatus"));
			td.setRoleCode(rs.getString("TDRoleCode"));
			td.setNextRoleCode(rs.getString("TDNextRoleCode"));
			td.setTaskId(rs.getString("TDTaskId"));
			td.setNextTaskId(rs.getString("TDNextTaskId"));
			td.setRecordType(rs.getString("TDRecordType"));
			td.setWorkflowId(rs.getLong("TDWorkflowId"));

			ta.setTanDetail(td);

			return ta;
		});
	}

	@Override
	public List<TanAssignment> getTanDetailsByReference(String finReference) {
		StringBuilder sql = new StringBuilder("Select");
		sql.append(" TD.TanNumber, FinReference, TD.TanHolderName");
		sql.append(" From Tan_Assignments");
		sql.append(" inner join Tan_Details TD on TD.id = TANId");
		sql.append(" Where FinReference = ?");

		logger.debug(Literal.SQL + sql.toString());

		return this.jdbcOperations.query(sql.toString(), ps -> ps.setString(1, finReference), (rs, rowNum) -> {
			TanAssignment tda = new TanAssignment();

			TanDetail td = new TanDetail();
			td.setTanNumber(rs.getString("TanNumber"));
			td.setFinReference(rs.getString("FinReference"));
			td.setTanHolderName(rs.getString("TanHolderName"));
			tda.setTanDetail(td);

			return tda;
		});
	}

	@Override
	public List<TanAssignment> getTanNumberList(long custId) {
		StringBuilder sql = new StringBuilder("Select");
		sql.append(" Distinct TD.TanNumber, C.custCIF");
		sql.append(" From Tan_Assignments TA");
		sql.append(" LEFT JOIN TAN_DETAILS TD on TD.id = TA.TanId");
		sql.append(" LEFT JOIN CUSTOMERS C on C.custId = TA.custId");
		sql.append(" Where TA.custId != ?");

		logger.debug(Literal.SQL + sql.toString());

		return this.jdbcOperations.query(sql.toString(), ps -> ps.setLong(1, custId), (rs, rowNum) -> {
			TanAssignment tda = new TanAssignment();
			TanDetail td = new TanDetail();

			td.setTanNumber(rs.getString("TanNumber"));
			tda.setCustCIF(rs.getString("custCIF"));
			tda.setTanDetail(td);

			return tda;
		});
	}

	@Override
	public List<TanAssignment> getTanAssignmentsByCustId(long custId, String finReference, TableType mainTab) {
		StringBuilder sql = getSelectQuery(mainTab);
		sql.append(" Where CustID = ? and FinReference != ?");

		logger.debug(Literal.SQL + sql.toString());

		return this.jdbcOperations.query(sql.toString(), ps -> {
			int index = 1;
			ps.setLong(index++, custId);
			ps.setString(index, finReference);
		}, new TanAssignmentRowMapper());
	}

	@Override
	public int isTanNumberAvailable(long tanID) {
		String sql = "Select Count(FinReference) From Tan_Assignments Where TanID = ?";

		logger.debug(Literal.SQL + sql);

		return this.jdbcOperations.queryForObject(sql, Integer.class, tanID);
	}

	@Override
	public List<String> getFinReferenceByTanNumber(String finReference, String tanNumber, String type) {
		StringBuilder sql = new StringBuilder("Select TA.FinReference from Tan_details");
		sql.append(type);
		sql.append(" TD Inner Join Tan_Assignments");
		sql.append(type);
		sql.append(" TA On TA.TanId = TD.Id Where TD.TanNumber = ? And TA.FinReference != ?");

		logger.debug(Literal.SQL + sql.toString());

		return this.jdbcOperations.query(sql.toString(), ps -> {
			int index = 1;
			ps.setString(index++, tanNumber);
			ps.setString(index, finReference);
		}, (rs, rowNum) -> {
			return rs.getString("FinReference");
		});
	}
}
