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
 * * FileName : FinExpenseDetailsDAOImpl.java * * Author : PENNANT TECHONOLOGIES * * Creation Date : 17-12-2017 * *
 * Modified Date : 17-12-2017 * * Description : * *
 ********************************************************************************************
 * Date Author Version Comments *
 ********************************************************************************************
 * 17-12-2017 Pennant 0.1 * * * * * * * * *
 ********************************************************************************************
 */

package com.pennant.backend.dao.finance.impl;

import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.dao.EmptyResultDataAccessException;

import com.pennant.backend.dao.finance.FinExpenseDetailsDAO;
import com.pennant.backend.model.expenses.FinExpenseDetails;
import com.pennanttech.pennapps.core.ConcurrencyException;
import com.pennanttech.pennapps.core.jdbc.JdbcUtil;
import com.pennanttech.pennapps.core.jdbc.SequenceDao;
import com.pennanttech.pennapps.core.resource.Literal;

/**
 * DAO methods implementation for the <b>UploadHeader model</b> class.<br>
 * 
 */
public class FinExpenseDetailsDAOImpl extends SequenceDao<FinExpenseDetails> implements FinExpenseDetailsDAO {
	private static Logger logger = LogManager.getLogger(FinExpenseDetailsDAOImpl.class);

	public FinExpenseDetailsDAOImpl() {
		super();
	}

	@Override
	public long saveFinExpenseDetails(FinExpenseDetails ed) {
		if (ed.getFinExpenseId() == Long.MIN_VALUE) {
			ed.setFinExpenseId(getNextValue("SeqFinExpenseDetails"));
		}

		StringBuilder sql = new StringBuilder("Insert Into FinExpenseDetails");
		sql.append("(FinExpenseId, FinID, FinReference, ExpenseTypeId, Amount");
		sql.append(", Version, LastMntBy, LastMntOn, RecordStatus, RoleCode");
		sql.append(", NextRoleCode, TaskId, NextTaskId, RecordType, WorkflowId)");
		sql.append(" Values(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)");

		logger.debug(Literal.SQL + sql.toString());

		this.jdbcOperations.update(sql.toString(), ps -> {
			int index = 1;

			ps.setLong(index++, ed.getFinExpenseId());
			ps.setLong(index++, ed.getFinID());
			ps.setString(index++, ed.getFinReference());
			ps.setLong(index++, ed.getExpenseTypeId());
			ps.setBigDecimal(index++, ed.getAmount());
			ps.setInt(index++, ed.getVersion());
			ps.setLong(index++, JdbcUtil.setLong(ed.getLastMntBy()));
			ps.setTimestamp(index++, ed.getLastMntOn());
			ps.setString(index++, ed.getRecordStatus());
			ps.setString(index++, ed.getRoleCode());
			ps.setString(index++, ed.getNextRoleCode());
			ps.setString(index++, ed.getTaskId());
			ps.setString(index++, ed.getNextTaskId());
			ps.setString(index++, ed.getRecordType());
			ps.setLong(index++, JdbcUtil.setLong(ed.getWorkflowId()));

		});

		return ed.getFinExpenseId();
	}

	@Override
	public FinExpenseDetails getFinExpenseDetailsByReference(String finReference, long expenseTypeId) {
		StringBuilder sql = new StringBuilder("Select");
		sql.append("(FinExpenseId, FinID, FinReference, ExpenseTypeId, Amount");
		sql.append(", Version, LastMntBy, LastMntOn, RecordStatus, RoleCode");
		sql.append(", NextRoleCode, TaskId, NextTaskId, RecordType, WorkflowId)");
		sql.append(" From FinExpenseDetails");
		sql.append(" Where FinReference = ? and ExpenseTypeId = ?");

		logger.debug(Literal.SQL + sql.toString());

		try {
			return jdbcOperations.queryForObject(sql.toString(), (rs, rowNum) -> {
				FinExpenseDetails ed = new FinExpenseDetails();

				ed.setFinExpenseId(rs.getLong("FinExpenseId"));
				ed.setFinID(rs.getLong("FinID"));
				ed.setFinReference(rs.getString("FinReference"));
				ed.setExpenseTypeId(rs.getLong("ExpenseTypeId"));
				ed.setAmount(rs.getBigDecimal("Amount"));
				ed.setVersion(rs.getInt("Version"));
				ed.setLastMntBy(rs.getLong("LastMntBy"));
				ed.setLastMntOn(rs.getTimestamp("LastMntOn"));
				ed.setRecordStatus(rs.getString("RecordStatus"));
				ed.setRoleCode(rs.getString("RoleCode"));
				ed.setNextRoleCode(rs.getString("NextRoleCode"));
				ed.setTaskId(rs.getString("TaskId"));
				ed.setNextTaskId(rs.getString("NextTaskId"));
				ed.setRecordType(rs.getString("RecordType"));
				ed.setWorkflowId(rs.getLong("WorkflowId"));

				return ed;
			}, finReference, expenseTypeId);
		} catch (EmptyResultDataAccessException e) {
			//
		}

		return null;
	}

	@Override
	public void update(FinExpenseDetails fed) {
		StringBuilder sql = new StringBuilder("Update FinExpenseDetails");
		sql.append(" set Amount = ?, LastMntOn = ?");
		sql.append(" where FinExpenseId = ?");

		logger.debug(Literal.SQL + sql.toString());

		int recordCount = jdbcOperations.update(sql.toString(), ps -> {
			int index = 1;

			ps.setBigDecimal(index++, fed.getAmount());
			ps.setTimestamp(index++, fed.getLastMntOn());

			ps.setLong(index++, fed.getFinExpenseId());
		});

		if (recordCount == 0) {
			throw new ConcurrencyException();
		}
	}

	@Override
	public List<FinExpenseDetails> getFinExpenseDetailsById(long finID) {
		StringBuilder sql = new StringBuilder("Select");
		sql.append(" ed.FinexpenseID, ed.Amount, et.ExpenseTypeCode, et.ExpenseTypeDesc, ed.LastMntOn");
		sql.append(" From FinExpenseDetails ed");
		sql.append(" Inner Join ExpenseTypes et on et.ExpenseTypeID = ed.ExpenseTypeID");
		sql.append(" Where ed.FinID = ?");

		logger.debug(Literal.SQL + sql.toString());

		return this.jdbcOperations.query(sql.toString(), (rs, rowNum) -> {
			FinExpenseDetails ed = new FinExpenseDetails();

			ed.setFinExpenseId(rs.getLong("FinExpenseId"));
			ed.setAmount(rs.getBigDecimal("Amount"));
			ed.setExpenseTypeCode(rs.getString("ExpenseTypeCode"));
			ed.setExpenseTypeDesc(rs.getString("ExpenseTypeDesc"));
			ed.setLastMntOn(rs.getTimestamp("LastMntOn"));

			return ed;
		}, finID);
	}

	public List<FinExpenseDetails> getAMZFinExpenseDetails(long finID, String type) {
		StringBuilder sql = new StringBuilder("Select");
		sql.append(" ed.FinExpenseId, ed.FinID, ed.FinReference, ed.ExpenseTypeId, ed.Amount");
		sql.append(" From FinExpenseDetails ed");
		sql.append(" Inner Join ExpenseTypes et on et.ExpenseTypeId = ed.ExpenseTypeId and et.AmortReq = ?");
		sql.append(" Where ed.FinID = ?");

		logger.debug(Literal.SQL + sql.toString());

		return this.jdbcOperations.query(sql.toString(), (rs, i) -> {
			FinExpenseDetails ed = new FinExpenseDetails();

			ed.setFinExpenseId(rs.getLong("FinExpenseId"));
			ed.setFinID(rs.getLong("FinID"));
			ed.setFinReference(rs.getString("FinReference"));
			ed.setExpenseTypeId(rs.getLong("ExpenseTypeId"));
			ed.setAmount(rs.getBigDecimal("Amount"));

			return ed;
		}, 1, finID);
	}
}