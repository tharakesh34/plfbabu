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
 * * FileName : FinExpenseMovementsDAOImpl.java * * Author : PENNANT TECHONOLOGIES * * Creation Date : 17-12-2017 * *
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

import com.pennant.backend.dao.finance.FinExpenseMovementsDAO;
import com.pennant.backend.model.expenses.FinExpenseMovements;
import com.pennanttech.pennapps.core.jdbc.JdbcUtil;
import com.pennanttech.pennapps.core.jdbc.SequenceDao;
import com.pennanttech.pennapps.core.resource.Literal;

/**
 * DAO methods implementation for the <b>UploadHeader model</b> class.<br>
 * 
 */
public class FinExpenseMovementsDAOImpl extends SequenceDao<FinExpenseMovements> implements FinExpenseMovementsDAO {
	private static Logger logger = LogManager.getLogger(FinExpenseMovementsDAOImpl.class);

	public FinExpenseMovementsDAOImpl() {
		super();
	}

	@Override
	public long saveFinExpenseMovements(FinExpenseMovements fem) {
		if (fem.getFinExpenseMovemntId() == Long.MIN_VALUE) {
			fem.setFinExpenseMovemntId(getNextValue("SeqFinExpenseMovements"));
		}

		StringBuilder sql = new StringBuilder("Insert Into FinExpenseMovements");
		sql.append(" (FinExpenseMovemntId, FinExpenseId, FinID, FinReference, ModeType, UploadId");
		sql.append(", TransactionAmount, TransactionType, LastMntOn");
		sql.append(", TransactionDate, LinkedTranId, RevLinkedTranId)");
		sql.append(" Values(");
		sql.append(" ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?");
		sql.append(")");

		logger.debug(Literal.SQL + sql.toString());

		this.jdbcOperations.update(sql.toString(), ps -> {
			int index = 1;

			ps.setLong(index++, fem.getFinExpenseMovemntId());
			ps.setLong(index++, fem.getFinExpenseId());
			ps.setLong(index++, fem.getFinID());
			ps.setString(index++, fem.getFinReference());
			ps.setString(index++, fem.getModeType());
			ps.setLong(index++, fem.getUploadId());
			ps.setBigDecimal(index++, fem.getTransactionAmount());
			ps.setString(index++, fem.getTransactionType());
			ps.setTimestamp(index++, fem.getLastMntOn());
			ps.setDate(index++, JdbcUtil.getDate((fem.getTransactionDate())));
			ps.setLong(index++, fem.getLinkedTranId());
			ps.setLong(index++, fem.getRevLinkedTranId());
		});

		return fem.getFinExpenseMovemntId();
	}

	@Override
	public List<FinExpenseMovements> getFinExpenseMovementById(long finID, long finExpenseId) {
		StringBuilder sql = new StringBuilder("Select");
		sql.append(" m.ModeType, m.TransactionType, m.TransactionDate, m.TransactionAmount");
		sql.append(", h.FileName, h.LastMntBy");
		sql.append(" From FinExpenseMovements m");
		sql.append(" Inner Join UploadHeader h on h.uploadId = m.UploadId");
		sql.append(" Where m.FinID = ? and m.FinExpenseID = ?");

		logger.debug(Literal.SQL + sql.toString());

		return this.jdbcOperations.query(sql.toString(), (rs, rowNum) -> {
			FinExpenseMovements movement = new FinExpenseMovements();

			movement.setModeType(rs.getString("ModeType"));
			movement.setTransactionType(rs.getString("TransactionType"));
			movement.setTransactionDate(JdbcUtil.getDate(rs.getDate("TransactionDate")));
			movement.setTransactionAmount(rs.getBigDecimal("TransactionAmount"));
			movement.setFileName(rs.getString("FileName"));
			movement.setLastMntBy(rs.getLong("LastMntBy"));

			return movement;
		}, finID, finExpenseId);
	}

	@Override
	public List<FinExpenseMovements> getFinExpenseMovements(long finID, long expenseTypeID) {
		StringBuilder sql = new StringBuilder("Select");
		sql.append(" m.FinExpenseMovemntID, m.LinkedTranID");
		sql.append(" From FinExpenseDetails ed");
		sql.append(" inner join FinExpenseMovements m on m.FinExpenseId = ed.FinExpenseId");
		sql.append(" Where ed.FinID = ? and ed.ExpenseTypeID = ? and RevLinkedTranID is NULL");

		logger.debug(Literal.SQL + sql.toString());

		return this.jdbcOperations.query(sql.toString(), ps -> {
			int index = 1;

			ps.setLong(index++, finID);
			ps.setLong(index++, expenseTypeID);
		}, (rs, num) -> {
			FinExpenseMovements movement = new FinExpenseMovements();
			movement.setFinExpenseMovemntId(rs.getLong("FinExpenseMovemntID"));
			movement.setLinkedTranId(rs.getLong("LinkedTranID"));

			return movement;
		});
	}

	@Override
	public void updateRevLinkedTranID(long id, long revLinkedTranID) {
		String sql = "Update FinExpenseMovements set RevLinkedTranID = ? Where FinExpenseMovemntID = ?";

		logger.debug(Literal.SQL + sql);

		this.jdbcOperations.update(sql, ps -> {
			int index = 1;

			ps.setLong(index++, revLinkedTranID);
			ps.setLong(index++, id);
		});
	}
}