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
 * * FileName : LegalExpensesDAOImpl.java * * Author : PENNANT TECHONOLOGIES * * Creation Date : 19-04-2016 * * Modified
 * Date : 19-04-2016 * * Description : * *
 ********************************************************************************************
 * Date Author Version Comments *
 ********************************************************************************************
 * 19-04-2016 Pennant 0.1 * * * * * * * * *
 ********************************************************************************************
 */

package com.pennant.backend.dao.expenses.impl;

import java.math.BigDecimal;

import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.EmptyResultDataAccessException;

import com.pennant.backend.dao.expenses.LegalExpensesDAO;
import com.pennant.backend.model.expenses.LegalExpenses;
import com.pennant.backend.util.PennantConstants;
import com.pennanttech.pennapps.core.ConcurrencyException;
import com.pennanttech.pennapps.core.DependencyFoundException;
import com.pennanttech.pennapps.core.jdbc.JdbcUtil;
import com.pennanttech.pennapps.core.jdbc.SequenceDao;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pennapps.core.resource.Message;

/**
 * DAO methods implementation for the <b>LegalExpenses model</b> class.<br>
 * 
 */

public class LegalExpensesDAOImpl extends SequenceDao<LegalExpenses> implements LegalExpensesDAO {
	private static Logger logger = LogManager.getLogger(LegalExpensesDAOImpl.class);

	public LegalExpensesDAOImpl() {
		super();
	}

	@Override
	public LegalExpenses getLegalExpensesById(String reference, String type) {
		StringBuilder sql = new StringBuilder("Select");
		sql.append(" ExpReference, CustomerId, BookingDate, Amount, FinID, FinReference, TransactionType");
		sql.append(", Remarks, RecoveredAmount, Amountdue, IsRecoverdFromMOPA, TotalCharges");
		sql.append(", Version ,LastMntBy, LastMntOn, RecordStatus, RoleCode");
		sql.append(", NextRoleCode, TaskId, NextTaskId, RecordType, WorkflowId");
		sql.append(" From FinLegalExpenses");
		sql.append(StringUtils.trimToEmpty(type));
		sql.append(" Where ExpReference = ?");

		logger.debug(Literal.SQL + sql.toString());

		try {
			return this.jdbcOperations.queryForObject(sql.toString(), (rs, num) -> {
				LegalExpenses legal = new LegalExpenses();

				legal.setExpReference(rs.getString("ExpReference"));
				legal.setCustomerId(rs.getString("CustomerId"));
				legal.setBookingDate(rs.getDate("BookingDate"));
				legal.setAmount(rs.getBigDecimal("Amount"));
				legal.setFinID(rs.getLong("FinID"));
				legal.setFinReference(rs.getString("FinReference"));
				legal.setTransactionType(rs.getString("TransactionType"));
				legal.setRemarks(rs.getString("Remarks"));
				legal.setRecoveredAmount(rs.getBigDecimal("RecoveredAmount"));
				legal.setAmountdue(rs.getBigDecimal("Amountdue"));
				legal.setIsRecoverdFromMOPA(rs.getBoolean("IsRecoverdFromMOPA"));
				legal.setTotalCharges(rs.getBigDecimal("TotalCharges"));
				legal.setVersion(rs.getInt("Version"));
				legal.setLastMntBy(rs.getLong("LastMntBy"));
				legal.setLastMntOn(rs.getTimestamp("LastMntOn"));
				legal.setRecordStatus(rs.getString("RecordStatus"));
				legal.setRoleCode(rs.getString("RoleCode"));
				legal.setNextRoleCode(rs.getString("NextRoleCode"));
				legal.setTaskId(rs.getString("TaskId"));
				legal.setNextTaskId(rs.getString("NextTaskId"));
				legal.setRecordType(rs.getString("RecordType"));
				legal.setWorkflowId(rs.getLong("WorkflowId"));

				return legal;
			}, reference);
		} catch (EmptyResultDataAccessException e) {
			logger.warn(Message.NO_RECORD_FOUND);
			return null;
		}
	}

	@Override
	public void delete(LegalExpenses le, String type) {
		StringBuilder sql = new StringBuilder("Delete From FinLegalExpenses");
		sql.append(StringUtils.trimToEmpty(type));
		sql.append(" Where ExpReference = ?");

		logger.debug(Literal.SQL + sql.toString());

		try {
			int recordCount = this.jdbcOperations.update(sql.toString(), ps -> ps.setString(1, le.getExpReference()));

			if (recordCount <= 0) {
				throw new ConcurrencyException();
			}
		} catch (DataAccessException e) {
			throw new DependencyFoundException(e);
		}
	}

	@Override
	public long save(LegalExpenses le, String type) {
		if (le.getExpReference() == null) {
			if (le.getId() == 0 || le.getId() == Long.MIN_VALUE) {
				le.setId(getNextValue("SeqFinLegalExpenses"));
			}

			switch (le.getTransactionType()) {
			case PennantConstants.LEGEL_FEES:
				le.setExpReference(le.getFinReference() + "L" + le.getId());
				break;
			case PennantConstants.FINES:
				le.setExpReference(le.getFinReference() + "F" + le.getId());
				break;
			case PennantConstants.OTHERS:
				le.setExpReference(le.getFinReference() + "O" + le.getId());
				break;
			}
		}

		StringBuilder sql = new StringBuilder("Insert Into FinLegalExpenses");
		sql.append(StringUtils.trimToEmpty(type));
		sql.append(" (ExpReference, CustomerId, BookingDate, Amount, FinID, FinReference, TransactionType");
		sql.append(", Remarks, RecoveredAmount, Amountdue, IsRecoverdFromMOPA, TotalCharges");
		sql.append(", Version, LastMntBy, LastMntOn, RecordStatus, RoleCode");
		sql.append(", NextRoleCode, TaskId, NextTaskId, RecordType, WorkflowId)");
		sql.append(" Values(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)");

		logger.debug(Literal.SQL + sql.toString());

		this.jdbcOperations.update(sql.toString(), ps -> {
			int index = 1;

			ps.setString(index++, (le.getExpReference()));
			ps.setString(index++, (le.getCustomerId()));
			ps.setDate(index++, JdbcUtil.getDate(le.getBookingDate()));
			ps.setBigDecimal(index++, le.getAmount());
			ps.setLong(index++, le.getFinID());
			ps.setString(index++, le.getFinReference());
			ps.setString(index++, le.getTransactionType());
			ps.setString(index++, le.getRemarks());
			ps.setBigDecimal(index++, le.getRecoveredAmount());
			ps.setBigDecimal(index++, le.getAmountdue());
			ps.setBoolean(index++, le.isIsRecoverdFromMOPA());
			ps.setBigDecimal(index++, le.getTotalCharges());
			ps.setInt(index++, le.getVersion());
			ps.setLong(index++, le.getLastMntBy());
			ps.setTimestamp(index++, le.getLastMntOn());
			ps.setString(index++, le.getRecordStatus());
			ps.setString(index++, le.getRoleCode());
			ps.setString(index++, le.getNextRoleCode());
			ps.setString(index++, le.getTaskId());
			ps.setString(index++, le.getNextTaskId());
			ps.setString(index++, le.getRecordType());
			ps.setLong(index, le.getWorkflowId());

		});

		return le.getId();
	}

	@Override
	public void update(LegalExpenses le, String type) {
		StringBuilder sql = new StringBuilder("Update FinLegalExpenses");
		sql.append(StringUtils.trimToEmpty(type));
		sql.append(" Set CustomerId = ?, BookingDate = ?, Amount = ?, FinID = ?, FinReference = ?");
		sql.append(", TransactionType = ?, Remarks = ?, RecoveredAmount = ?, Amountdue = ?, IsRecoverdFromMOPA = ?");
		sql.append(", TotalCharges = ?, Version = ?, LastMntBy = ?, LastMntOn = ?, RecordStatus = ?, RoleCode = ?");
		sql.append(", NextRoleCode = ?, TaskId = ?, NextTaskId = ?, RecordType = ?, WorkflowId = ?");
		sql.append(" Where ExpReference = ?");

		if (!type.endsWith("_Temp")) {
			sql.append("  and Version = ? - 1");
		}

		logger.debug(Literal.SQL + sql.toString());

		int recordCount = this.jdbcOperations.update(sql.toString(), ps -> {
			int index = 1;

			ps.setString(index++, (le.getCustomerId()));
			ps.setDate(index++, JdbcUtil.getDate(le.getBookingDate()));
			ps.setBigDecimal(index++, le.getAmount());
			ps.setLong(index++, le.getFinID());
			ps.setString(index++, le.getFinReference());
			ps.setString(index++, le.getTransactionType());
			ps.setString(index++, le.getRemarks());
			ps.setBigDecimal(index++, le.getRecoveredAmount());
			ps.setBigDecimal(index++, le.getAmountdue());
			ps.setBoolean(index++, le.isIsRecoverdFromMOPA());
			ps.setBigDecimal(index++, le.getTotalCharges());
			ps.setInt(index++, le.getVersion());
			ps.setLong(index++, le.getLastMntBy());
			ps.setTimestamp(index++, le.getLastMntOn());
			ps.setString(index++, le.getRecordStatus());
			ps.setString(index++, le.getRoleCode());
			ps.setString(index++, le.getNextRoleCode());
			ps.setString(index++, le.getTaskId());
			ps.setString(index++, le.getNextTaskId());
			ps.setString(index++, le.getRecordType());
			ps.setLong(index++, le.getWorkflowId());

			ps.setString(index++, (le.getExpReference()));

			if (!type.endsWith("_Temp")) {
				ps.setInt(index, le.getVersion() - 1);
			}
		});

		if (recordCount <= 0) {
			throw new ConcurrencyException();
		}

	}

	@Override
	public BigDecimal getTotalCharges(long finID) {
		String sql = "Select sum(Amount) From FinLegalExpenses_Aview where FinID = ?";

		logger.debug(Literal.SQL + sql);
		return this.jdbcOperations.queryForObject(sql, BigDecimal.class, finID);
	}
}