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
 * * FileName : FinanceRepaymentsDAOImpl.java * * Author : PENNANT TECHONOLOGIES * * Creation Date : 05-05-2011 * *
 * Modified Date : 05-05-2011 * * Description : * *
 ********************************************************************************************
 * Date Author Version Comments *
 ********************************************************************************************
 * 05-05-2011 Pennant 0.1 * * * * * * * * *
 ********************************************************************************************
 */
package com.pennant.backend.dao.receipts.impl;

import java.math.BigDecimal;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.RowMapper;

import com.pennant.backend.dao.receipts.FinExcessAmountDAO;
import com.pennant.backend.model.finance.FinExcessAmount;
import com.pennant.backend.model.finance.FinExcessAmountReserve;
import com.pennant.backend.model.finance.FinExcessMovement;
import com.pennant.backend.util.RepayConstants;
import com.pennant.eod.constants.EodConstants;
import com.pennanttech.pennapps.core.App;
import com.pennanttech.pennapps.core.App.Database;
import com.pennanttech.pennapps.core.ConcurrencyException;
import com.pennanttech.pennapps.core.jdbc.JdbcUtil;
import com.pennanttech.pennapps.core.jdbc.SequenceDao;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pennapps.core.resource.Message;
import com.pennanttech.pff.presentment.model.PresentmentDetail;

/**
 * DAO methods implementation for the <b>Finance Repayments</b> class.<br>
 * 
 */
public class FinExcessAmountDAOImpl extends SequenceDao<FinExcessAmount> implements FinExcessAmountDAO {
	private static Logger logger = LogManager.getLogger(FinExcessAmountDAOImpl.class);

	public FinExcessAmountDAOImpl() {
		super();
	}

	@Override
	public List<FinExcessAmount> getExcessAmountsByRef(long finId) {
		StringBuilder sql = getExcessAmountSqlQuery();
		sql.append(" Where FinID = ?");

		logger.debug(Literal.SQL + sql.toString());

		ExcessAmountRowMapper rowMapper = new ExcessAmountRowMapper();

		return this.jdbcOperations.query(sql.toString(), ps -> {
			int index = 1;
			ps.setLong(index, finId);
		}, rowMapper);
	}

	@Override
	public void saveExcess(FinExcessAmount fe) {
		if (fe.getId() == 0 || fe.getId() == Long.MIN_VALUE) {
			fe.setId(getNextValue("SeqFinExcessAmount"));
		}

		StringBuilder sql = new StringBuilder("Insert into");
		sql.append(" FinExcessAmount ");
		sql.append("(ExcessID, FinID, FinReference, AmountType, Amount, UtilisedAmt, ReservedAmt, BalanceAmt");
		sql.append(") values(");
		sql.append("?, ?, ?, ?, ?, ?, ?, ?");
		sql.append(")");

		logger.debug(Literal.SQL + sql.toString());

		jdbcOperations.update(sql.toString(), ps -> {
			int index = 1;

			ps.setLong(index++, fe.getExcessID());
			ps.setLong(index++, fe.getFinID());
			ps.setString(index++, fe.getFinReference());
			ps.setString(index++, fe.getAmountType());
			ps.setBigDecimal(index++, fe.getAmount());
			ps.setBigDecimal(index++, fe.getUtilisedAmt());
			ps.setBigDecimal(index++, fe.getReservedAmt());
			ps.setBigDecimal(index, fe.getBalanceAmt());

		});
	}

	@Override
	public void updateExcess(FinExcessAmount excess) {
		String sql = "Update FinExcessAmount Set Amount = ?, UtilisedAmt = ?, ReservedAmt = ?, BalanceAmt = ? Where ExcessID = ?";

		logger.debug(Literal.SQL + sql);

		this.jdbcOperations.update(sql, ps -> {
			int index = 1;

			ps.setBigDecimal(index++, excess.getAmount());
			ps.setBigDecimal(index++, excess.getUtilisedAmt());
			ps.setBigDecimal(index++, excess.getReservedAmt());
			ps.setBigDecimal(index++, excess.getBalanceAmt());
			ps.setLong(index, excess.getExcessID());
		});
	}

	@Override
	public FinExcessAmount getFinExcessAmount(long finID, String amountType) {
		StringBuilder sql = new StringBuilder("Select");
		sql.append(" ExcessID, FinID, FinReference, AmountType, Amount, UtilisedAmt, ReservedAmt, BalanceAmt");
		sql.append(" From FinExcessAmount");
		sql.append(" Where FinID = ? and AmountType = ?");

		logger.debug(Literal.SQL + sql.toString());

		try {
			return this.jdbcOperations.queryForObject(sql.toString(), (rs, rowNum) -> {
				FinExcessAmount fea = new FinExcessAmount();

				fea.setExcessID(rs.getLong("ExcessID"));
				fea.setFinID(rs.getLong("FinID"));
				fea.setFinReference(rs.getString("FinReference"));
				fea.setAmountType(rs.getString("AmountType"));
				fea.setAmount(rs.getBigDecimal("Amount"));
				fea.setUtilisedAmt(rs.getBigDecimal("UtilisedAmt"));
				fea.setReservedAmt(rs.getBigDecimal("ReservedAmt"));
				fea.setBalanceAmt(rs.getBigDecimal("BalanceAmt"));

				return fea;
			}, finID, amountType);
		} catch (EmptyResultDataAccessException e) {
			logger.warn(Message.NO_RECORD_FOUND);
			return null;
		}
	}

	@Override
	public FinExcessAmount getFinExcessAmount(long finID, long receiptId) {
		StringBuilder sql = new StringBuilder("Select");
		sql.append(" fa.ExcessId, FinID, FinReference, AmountType");
		sql.append(", fa.Amount, fa.UtilisedAmt, fa.ReservedAmt, fa.BalanceAmt");
		sql.append(" From FinExcessAmount fa");
		sql.append(" Inner Join FinExcessMovement em on em.ExcessId = fa.ExcessId and MovementFrom = ?");
		sql.append(" Where fa.FinID = ? and fa.AmountType = ? and em.ReceiptID = ?");

		logger.debug(Literal.SQL + sql.toString());

		RowMapper<FinExcessAmount> rowMapper = new ExcessAmountRowMapper();
		try {
			return this.jdbcOperations.queryForObject(sql.toString(), rowMapper, "UPFRONT", finID,
					RepayConstants.EXAMOUNTTYPE_EXCESS, receiptId);
		} catch (EmptyResultDataAccessException e) {
			logger.warn(Message.NO_RECORD_FOUND);
			return null;
		}
	}

	@Override
	public void updateUtilise(long excessID, BigDecimal amount) {
		StringBuilder sql = new StringBuilder("Update FinExcessAmount");
		sql.append(" Set UtilisedAmt = UtilisedAmt + ?, ReservedAmt = ReservedAmt - ?");
		sql.append(" Where ExcessID = ? and ReservedAmt >= ?");

		logger.debug(Literal.SQL + sql.toString());
		int recordCount = this.jdbcOperations.update(sql.toString(), ps -> {
			int index = 1;

			ps.setBigDecimal(index++, amount);
			ps.setBigDecimal(index++, amount);
			ps.setLong(index++, excessID);
			ps.setBigDecimal(index, amount);
		});

		if (recordCount <= 0) {
			throw new ConcurrencyException();
		}
	}

	@Override
	public void updateUtiliseOnly(long excessID, BigDecimal paidNow) {
		StringBuilder sql = new StringBuilder("Update FinExcessAmount");
		sql.append(" Set UtilisedAmt = UtilisedAmt + ? ,  BalanceAmt = BalanceAmt - ?");
		sql.append(" Where ExcessID = ?");

		logger.debug(Literal.SQL + sql.toString());

		this.jdbcOperations.update(sql.toString(), ps -> {

			int index = 1;
			ps.setBigDecimal(index++, paidNow);
			ps.setBigDecimal(index++, paidNow);
			ps.setLong(index, excessID);

		});
	}

	@Override
	public void updateExcessBal(long excessID, BigDecimal amount) {
		String sql = "Update FinExcessAmount Set Amount = Amount + ?, BalanceAmt = BalanceAmt + ? Where ExcessID = ?";

		logger.debug(Literal.SQL + sql);

		int recordCount = this.jdbcOperations.update(sql, ps -> {
			int index = 1;

			ps.setBigDecimal(index++, amount);
			ps.setBigDecimal(index++, amount);

			ps.setLong(index, excessID);

		});

		if (recordCount <= 0) {
			throw new ConcurrencyException();
		}
	}

	@Override
	public int updateExcessBalByRef(long finID, String amountType, BigDecimal amount) {
		String sql = "Update FinExcessAmount Set Amount = Amount + ?, BalanceAmt = BalanceAmt + ? Where FinID = ? And AmountType = ?";

		logger.debug(Literal.SQL + sql);

		return this.jdbcOperations.update(sql, ps -> {
			int index = 1;

			ps.setBigDecimal(index++, amount);
			ps.setBigDecimal(index++, amount);
			ps.setLong(index++, finID);
			ps.setString(index, amountType);
		});
	}

	@Override
	public long saveExcessMovements(List<FinExcessMovement> excessMovement) {
		StringBuilder sql = new StringBuilder("Insert into");
		sql.append(" FinExcessMovement");
		sql.append(" (ExcessID, ReceiptID, MovementType, TranType, Amount, MovementFrom, SchDate");
		sql.append(") values(");
		sql.append("?, ?, ?, ?, ?, ?, ?");
		sql.append(")");

		logger.debug(Literal.SQL + sql.toString());
		return jdbcOperations.batchUpdate(sql.toString(), new BatchPreparedStatementSetter() {

			@Override
			public void setValues(PreparedStatement ps, int index) throws SQLException {
				int i = 1;
				FinExcessMovement fe = excessMovement.get(index);

				ps.setLong(i++, fe.getExcessID());
				ps.setObject(i++, fe.getReceiptID());
				ps.setString(i++, fe.getMovementType());
				ps.setString(i++, fe.getTranType());
				ps.setBigDecimal(i++, fe.getAmount());
				ps.setString(i++, fe.getMovementFrom());
				ps.setDate(i, JdbcUtil.getDate(fe.getSchDate()));

			}

			@Override
			public int getBatchSize() {
				return excessMovement.size();
			}
		}).length;

	}

	@Override
	public void saveExcessMovement(FinExcessMovement fe) {
		StringBuilder sql = new StringBuilder("Insert into");
		sql.append(" FinExcessMovement");
		sql.append(" (ExcessID, ReceiptID, MovementType, TranType, Amount, MovementFrom, SchDate");
		sql.append(") values(");
		sql.append("?, ?, ?, ?, ?, ?, ?");
		sql.append(")");

		logger.debug(Literal.SQL + sql.toString());

		this.jdbcOperations.update(sql.toString(), ps -> {
			int index = 1;

			ps.setLong(index++, fe.getExcessID());
			ps.setObject(index++, fe.getReceiptID());
			ps.setString(index++, fe.getMovementType());
			ps.setString(index++, fe.getTranType());
			ps.setBigDecimal(index++, fe.getAmount());
			ps.setString(index++, fe.getMovementFrom());
			ps.setDate(index, JdbcUtil.getDate(fe.getSchDate()));
		});

	}

	@Override
	public void updateExcessReserve(long payAgainstID, BigDecimal reserveAmt) {
		StringBuilder sql = new StringBuilder("Update FinExcessAmount");
		sql.append(" Set ReservedAmt = ReservedAmt + ?, BalanceAmt = BalanceAmt - ?");
		sql.append(" Where ExcessID = ? and (BalanceAmt-ReservedAmt) >= ? ");

		logger.debug(Literal.SQL + sql.toString());

		int recordCount = this.jdbcOperations.update(sql.toString(), ps -> {
			int index = 1;

			ps.setBigDecimal(index++, reserveAmt);
			ps.setBigDecimal(index++, reserveAmt);
			ps.setLong(index++, payAgainstID);
			ps.setBigDecimal(index, reserveAmt);
		});

		if (recordCount <= 0) {
			throw new ConcurrencyException();
		}
	}

	@Override
	public FinExcessAmountReserve getExcessReserve(long receiptSeqID, long payAgainstID) {
		String sql = "Select ReceiptSeqID, ExcessID, ReservedAmt From FinExcessAmountReserve Where ReceiptSeqID = ? and ExcessID = ?";

		logger.debug(Literal.SQL + sql);

		ExcessReserveRowMapper rowMapper = new ExcessReserveRowMapper();
		try {
			return this.jdbcOperations.queryForObject(sql, rowMapper, receiptSeqID, payAgainstID);
		} catch (EmptyResultDataAccessException e) {
			logger.warn(Message.NO_RECORD_FOUND);
			return null;
		}
	}

	@Override
	public List<FinExcessAmountReserve> getExcessReserveList(long receiptSeqID) {
		String sql = "Select ReceiptSeqID, ExcessID, ReservedAmt From FinExcessAmountReserve Where ReceiptSeqID = ?";

		logger.debug(Literal.SQL + sql);

		ExcessReserveRowMapper rowMapper = new ExcessReserveRowMapper();

		return this.jdbcOperations.query(sql, ps -> {
			int index = 1;
			ps.setLong(index, receiptSeqID);
		}, rowMapper);
	}

	@Override
	public void saveExcessReserveLog(long receiptSeqID, long payAgainstID, BigDecimal reserveAmt, String paymentType) {
		StringBuilder sql = new StringBuilder("Insert Into FinExcessAmountReserve");
		sql.append(" (ExcessID, ReceiptSeqID, ReservedAmt, PaymentType)");
		sql.append(" Values(?, ?, ?, ?)");

		logger.debug(Literal.SQL + sql.toString());

		jdbcOperations.update(sql.toString(), ps -> {
			int index = 1;

			ps.setLong(index++, payAgainstID);
			ps.setLong(index++, receiptSeqID);
			ps.setBigDecimal(index++, reserveAmt);
			ps.setString(index, paymentType);
		});
	}

	@Override
	public void updateExcessReserveLog(long receiptID, long payAgainstID, BigDecimal diffInReserve,
			String paymentType) {

		int recordCount = 0;

		StringBuilder sql = new StringBuilder("Update FinExcessAmountReserve");
		sql.append(" Set ReservedAmt = ReservedAmt + ? ");
		sql.append(" Where ReceiptSeqID = ? AND ExcessID = ? AND PaymentType = ?");

		logger.debug(Literal.SQL + sql);

		recordCount = this.jdbcOperations.update(sql.toString(), ps -> {
			int index = 1;
			ps.setBigDecimal(index++, diffInReserve);
			ps.setLong(index++, receiptID);
			ps.setLong(index++, payAgainstID);
			ps.setString(index, paymentType);
		});

		if (recordCount <= 0) {
			throw new ConcurrencyException();
		}
	}

	@Override
	public void deleteExcessReserve(long receiptID, long payAgainstID, String paymentType) {
		StringBuilder sql = new StringBuilder("Delete From FinExcessAmountReserve");
		sql.append(" Where ReceiptSeqID = ? and PaymentType = ?");
		if (payAgainstID != 0) {
			sql.append(" and ExcessID = ?");
		}

		logger.debug(Literal.SQL + sql.toString());

		this.jdbcOperations.update(sql.toString(), ps -> {
			int index = 1;
			ps.setLong(index++, receiptID);
			ps.setString(index++, paymentType);
			if (payAgainstID != 0) {
				ps.setLong(index, payAgainstID);
			}
		});
	}

	@Override
	public int updateExcessEMIAmount(List<FinExcessAmount> emiInAdvance, String amtType) {
		StringBuilder sql = new StringBuilder("Update FinExcessAmount");

		if ("R".equals(amtType)) {
			sql.append(" Set ReservedAmt = ReservedAmt + ?, BalanceAmt = BalanceAmt - ?");
		} else if ("U".equals(amtType)) {
			sql.append(" Set UtilisedAmt = UtilisedAmt + ?, BalanceAmt = BalanceAmt - ?");
		}
		sql.append(" Where ExcessID = ?");

		logger.debug(Literal.SQL + sql.toString());

		return jdbcOperations.batchUpdate(sql.toString(), new BatchPreparedStatementSetter() {

			@Override
			public void setValues(PreparedStatement ps, int i) throws SQLException {
				FinExcessAmount ex = emiInAdvance.get(i);

				int index = 1;

				ps.setBigDecimal(index++, ex.getAmount());
				ps.setBigDecimal(index++, ex.getAmount());
				ps.setLong(index, ex.getExcessID());
			}

			@Override
			public int getBatchSize() {
				return emiInAdvance.size();
			}
		}).length;
	}

	@Override
	public void updateExcessAmount(long excessID, String amountType, BigDecimal amount) {
		StringBuilder sql = new StringBuilder("Update FinExcessAmount");

		if ("R".equals(amountType)) {
			sql.append(" Set ReservedAmt = ReservedAmt + ?, BalanceAmt = BalanceAmt - ?");
		} else if ("U".equals(amountType)) {
			sql.append(" Set UtilisedAmt = UtilisedAmt + ?, BalanceAmt = BalanceAmt - ?");
		}

		sql.append(" Where ExcessID = ?");

		logger.debug(Literal.SQL + sql.toString());

		int recordCount = this.jdbcOperations.update(sql.toString(), ps -> {
			int index = 1;

			ps.setBigDecimal(index++, amount);
			ps.setBigDecimal(index++, amount);
			ps.setLong(index, excessID);
		});

		if (recordCount <= 0) {
			throw new ConcurrencyException();
		}
	}

	@Override
	public int updateExcessAmtList(List<FinExcessAmount> excess) {
		String sql = "Update FinExcessAmount Set ReservedAmt = ReservedAmt - ?, BalanceAmt = BalanceAmt + ? Where ExcessID = ?";

		logger.debug(Literal.SQL + sql);
		return jdbcOperations.batchUpdate(sql, new BatchPreparedStatementSetter() {

			@Override
			public void setValues(PreparedStatement ps, int i) throws SQLException {
				FinExcessAmount ex = excess.get(i);

				int index = 1;

				ps.setBigDecimal(index++, ex.getAmount());
				ps.setBigDecimal(index++, ex.getAmount());
				ps.setLong(index, ex.getExcessID());
			}

			@Override
			public int getBatchSize() {
				return excess.size();
			}
		}).length;
	}

	@Override
	public void updateExcessAmount(long excessID, BigDecimal advanceAmount) {
		String sql = "Update FinExcessAmount Set ReservedAmt = ReservedAmt - ?, BalanceAmt = BalanceAmt + ? Where ExcessID = ?";

		logger.debug(Literal.SQL + sql);

		this.jdbcOperations.update(sql, ps -> {
			int index = 1;

			ps.setBigDecimal(index++, advanceAmount);
			ps.setBigDecimal(index++, advanceAmount);
			ps.setLong(index, excessID);

		});
	}

	@Override
	public void batchUpdateExcessAmount(List<PresentmentDetail> presements) {
		String sql = "Update FinExcessAmount Set ReservedAmt = ReservedAmt - ?, BalanceAmt = BalanceAmt + ? Where ExcessID = ?";

		logger.debug(Literal.SQL + sql);

		jdbcOperations.batchUpdate(sql, new BatchPreparedStatementSetter() {

			@Override
			public void setValues(PreparedStatement ps, int i) throws SQLException {
				int index = 1;

				PresentmentDetail pd = presements.get(i);
				ps.setBigDecimal(index++, pd.getAdvanceAmt());
				ps.setBigDecimal(index++, pd.getAdvanceAmt());
				ps.setLong(index, pd.getExcessID());
			}

			@Override
			public int getBatchSize() {
				return presements.size();
			}
		});
	}

	@Override
	public void updateUtilizedAndBalance(FinExcessAmount ea) {
		String sql = "Update FinExcessAmount Set UtilisedAmt = ?, BalanceAmt = ? Where ExcessID = ?";

		logger.debug(Literal.SQL + sql);

		this.jdbcOperations.update(sql, ps -> {
			int index = 1;

			ps.setBigDecimal(index++, ea.getUtilisedAmt());
			ps.setBigDecimal(index++, ea.getBalanceAmt());
			ps.setLong(index, ea.getExcessID());
		});
	}

	@Override
	public FinExcessAmount getExcessAmountsByRefAndType(long finID, String amountType) {
		StringBuilder sql = getExcessAmountSqlQuery();
		sql.append(" Where FinID = ? and AmountType = ?");

		logger.debug(Literal.SQL + sql.toString());

		ExcessAmountRowMapper rowMapper = new ExcessAmountRowMapper();

		try {
			return jdbcOperations.queryForObject(sql.toString(), rowMapper, finID, amountType);
		} catch (EmptyResultDataAccessException e) {
			logger.warn(Message.NO_RECORD_FOUND);
			return null;
		}
	}

	private StringBuilder getExcessAmountSqlQuery() {
		StringBuilder sql = new StringBuilder("Select");
		sql.append(" ExcessID, FinID, FinReference, AmountType, Amount, UtilisedAmt, ReservedAmt, BalanceAmt");
		sql.append(" From FinExcessAmount");
		return sql;
	}

	@Override
	public List<FinExcessAmount> getAllExcessAmountsByRef(long finID, String type) {
		StringBuilder sql = getExcessAmountSqlQuery();
		sql.append(StringUtils.trimToEmpty(type));

		if (App.DATABASE == Database.SQL_SERVER) {
			sql.append(EodConstants.SQL_NOLOCK);
		}

		sql.append(" Where FinID = ?");

		logger.debug(Literal.SQL + sql.toString());

		ExcessAmountRowMapper rowMapper = new ExcessAmountRowMapper();

		return this.jdbcOperations.query(sql.toString(), rowMapper, finID);

	}

	@Override
	public void deductExcessReserve(long excessID, BigDecimal amount) {
		String sql = "Update FinExcessAmount Set ReservedAmt = ReservedAmt - ?, Amount = Amount - ? Where ExcessID = ?";

		logger.debug(Literal.SQL + sql);
		int recordCount = this.jdbcOperations.update(sql, ps -> {
			int index = 1;

			ps.setBigDecimal(index++, amount);
			ps.setBigDecimal(index++, amount);
			ps.setLong(index, excessID);

		});

		if (recordCount <= 0) {
			throw new ConcurrencyException();
		}
	}

	@Override
	public int updateExcessReserveByRef(long finID, String amountType, BigDecimal amount) {
		String sql = "Update FinExcessAmount Set ReservedAmt = ReservedAmt + ?, Amount = Amount + ? Where FinID = ? and AmountType = ?";
		logger.debug(Literal.SQL + sql);

		return this.jdbcOperations.update(sql, ps -> {
			int index = 1;

			ps.setBigDecimal(index++, amount);
			ps.setBigDecimal(index++, amount);
			ps.setLong(index++, finID);
			ps.setString(index, amountType);
		});
	}

	@Override
	public int updExcessAfterRealize(long finID, String amountType, BigDecimal amount) {
		String sql = "Update FinExcessAmount Set BalanceAmt = BalanceAmt + ?, ReservedAmt = ReservedAmt - ? Where FinID = ? and AmountType = ?";

		logger.debug(Literal.SQL + sql);

		return this.jdbcOperations.update(sql, ps -> {
			int index = 1;

			ps.setBigDecimal(index++, amount);
			ps.setBigDecimal(index++, amount);
			ps.setLong(index++, finID);
			ps.setString(index, amountType);

		});
	}

	@Override
	public int updateExcessReserveList(List<FinExcessAmount> excessRevarsal) {
		String sql = "Update FinExcessAmount Set BalanceAmt = ?, ReservedAmt = ? Where ExcessID = ?";

		return jdbcOperations.batchUpdate(sql, new BatchPreparedStatementSetter() {

			@Override
			public void setValues(PreparedStatement ps, int index) throws SQLException {
				FinExcessAmount ex = excessRevarsal.get(index);

				ps.setBigDecimal(1, ex.getBalanceAmt());
				ps.setBigDecimal(2, ex.getReservedAmt());
				ps.setLong(3, ex.getExcessID());
			}

			@Override
			public int getBatchSize() {
				return excessRevarsal.size();
			}
		}).length;

	}

	@Override
	public int updateExcessReserve(FinExcessAmount em) {
		String sql = "Update FinExcessAmount Set BalanceAmt = ?, ReservedAmt = ? Where ExcessID = ?";

		return this.jdbcOperations.update(sql, ps -> {
			int index = 1;

			ps.setBigDecimal(index++, em.getBalanceAmt());
			ps.setBigDecimal(index++, em.getReservedAmt());
			ps.setLong(index, em.getExcessID());
		});

	}

	@Override
	public int updateReserveUtilization(FinExcessAmount ea) {
		String sql = "Update FinExcessAmount Set BalanceAmt = ?, ReservedAmt = ?, UtilisedAmt = ? Where ExcessID = ?";
		logger.debug(Literal.SQL + sql);

		return this.jdbcOperations.update(sql, ps -> {
			int index = 1;

			ps.setBigDecimal(index++, ea.getBalanceAmt());
			ps.setBigDecimal(index++, ea.getReservedAmt());
			ps.setBigDecimal(index++, ea.getUtilisedAmt());
			ps.setLong(index, ea.getExcessID());
		});
	}

	@Override
	public FinExcessMovement getFinExcessMovement(long excessID, String movementFrom, Date schDate) {
		StringBuilder sql = new StringBuilder("Select");
		sql.append(" ExcessID, ReceiptID, MovementType, TranType, Amount, MovementFrom, SchDate");
		sql.append(" From FinExcessMovement");
		sql.append(" Where ExcessID = ? and MovementFrom = ? and SchDate = ?");

		logger.debug(Literal.SQL + sql.toString());

		Object[] object = new Object[] { excessID, movementFrom, JdbcUtil.getDate(schDate) };

		try {
			return this.jdbcOperations.queryForObject(sql.toString(), (rs, rowNum) -> {
				FinExcessMovement fea = new FinExcessMovement();

				fea.setExcessID(rs.getLong("ExcessID"));
				fea.setReceiptID(JdbcUtil.getLong(rs.getObject("ReceiptID")));
				fea.setMovementType(rs.getString("MovementType"));
				fea.setTranType(rs.getString("TranType"));
				fea.setAmount(rs.getBigDecimal("Amount"));
				fea.setMovementFrom(rs.getString("MovementFrom"));
				fea.setSchDate(rs.getTimestamp("SchDate"));

				return fea;

			}, object);
		} catch (EmptyResultDataAccessException e) {
			logger.warn(Message.NO_RECORD_FOUND);
			return null;
		}
	}

	@Override
	public List<FinExcessMovement> getFinExcessAmount(long presentmentid) {
		StringBuilder sql = new StringBuilder("Select");
		sql.append(" ExcessID, ReceiptID, MovementType, TranType, Amount, MovementFrom, SchDate");
		sql.append(" From FinExcessMovement");
		sql.append(" Where ReceiptID = ? and MovementType = ? and TranType = ?");

		logger.debug(Literal.SQL + sql.toString());

		Object[] object = new Object[] { presentmentid, "I", "I" };

		return this.jdbcOperations.query(sql.toString(), (rs, rowNum) -> {
			FinExcessMovement fea = new FinExcessMovement();

			fea.setExcessID(rs.getLong("ExcessID"));
			fea.setReceiptID(JdbcUtil.getLong(rs.getObject("ReceiptID")));
			fea.setMovementType(rs.getString("MovementType"));
			fea.setTranType(rs.getString("TranType"));
			fea.setAmount(rs.getBigDecimal("Amount"));
			fea.setMovementFrom(rs.getString("MovementFrom"));
			fea.setSchDate(rs.getTimestamp("SchDate"));

			return fea;

		}, object);
	}

	@Override
	public FinExcessAmount getFinExcessByID(long excessID) {
		StringBuilder sql = getExcessAmountSqlQuery();
		sql.append(" Where ExcessID = ?");

		logger.debug(Literal.SQL + sql.toString());

		ExcessAmountRowMapper rowMapper = new ExcessAmountRowMapper();

		return this.jdbcOperations.queryForObject(sql.toString(), rowMapper, excessID);
	}

	@Override
	public int deleteMovemntByPrdID(long presentmentId) {
		String sql = "Delete From FinExcessMovement Where ReceiptID = ? and MovementType = ? and TranType = ?";

		logger.debug(Literal.SQL + sql);

		return this.jdbcOperations.update(sql, ps -> {
			int index = 1;

			ps.setLong(index++, presentmentId);
			ps.setString(index++, "I");
			ps.setString(index, "I");
		});
	}

	@Override
	public void saveExcessList(List<FinExcessAmount> feaList) {
		StringBuilder sql = saveExcessQuery();

		this.jdbcOperations.batchUpdate(sql.toString(), new BatchPreparedStatementSetter() {

			@Override
			public void setValues(PreparedStatement ps, int i) throws SQLException {
				FinExcessAmount ea = feaList.get(i);

				int index = 1;

				ps.setLong(index++, ea.getExcessID());
				ps.setLong(index++, ea.getFinID());
				ps.setString(index++, ea.getFinReference());
				ps.setString(index++, ea.getAmountType());
				ps.setBigDecimal(index++, ea.getAmount());
				ps.setBigDecimal(index++, ea.getUtilisedAmt());
				ps.setBigDecimal(index++, ea.getReservedAmt());
				ps.setBigDecimal(index, ea.getBalanceAmt());

			}

			@Override
			public int getBatchSize() {
				return feaList.size();
			}

		});
	}

	private StringBuilder saveExcessQuery() {
		StringBuilder sql = new StringBuilder("Insert Into FinExcessAmount");
		sql.append(" (ExcessID, FinID, FinReference, AmountType, Amount, UtilisedAmt, ReservedAmt, BalanceAmt)");
		sql.append(" Values(?, ?, ?, ?, ?, ?, ?, ?)");
		return sql;
	}

	@Override
	public void saveExcessMovementList(List<FinExcessMovement> movements) {
		StringBuilder sql = saveFEMQuery();

		this.jdbcOperations.batchUpdate(sql.toString(), new BatchPreparedStatementSetter() {

			@Override
			public void setValues(PreparedStatement ps, int i) throws SQLException {
				FinExcessMovement em = movements.get(i);

				int index = 1;

				ps.setLong(index++, em.getExcessID());
				ps.setObject(index++, em.getReceiptID());
				ps.setString(index++, em.getMovementType());
				ps.setString(index++, em.getTranType());
				ps.setBigDecimal(index++, em.getAmount());
				ps.setString(index++, em.getMovementFrom());
				ps.setDate(index, JdbcUtil.getDate(em.getSchDate()));

			}

			@Override
			public int getBatchSize() {
				return movements.size();
			}
		});

	}

	private StringBuilder saveFEMQuery() {
		StringBuilder sql = new StringBuilder("Insert Into FinExcessMovement");
		sql.append(" (ExcessID, ReceiptID, MovementType, TranType, Amount, MovementFrom, SchDate)");
		sql.append(" Values(?, ?, ?, ?, ?, ?, ?)");

		return sql;
	}

	public class ExcessAmountRowMapper implements RowMapper<FinExcessAmount> {

		@Override
		public FinExcessAmount mapRow(ResultSet rs, int rowNum) throws SQLException {
			FinExcessAmount ea = new FinExcessAmount();

			ea.setExcessID(rs.getLong("ExcessID"));
			ea.setFinID(rs.getLong("FinID"));
			ea.setFinReference(rs.getString("FinReference"));
			ea.setAmountType(rs.getString("AmountType"));
			ea.setAmount(rs.getBigDecimal("Amount"));
			ea.setUtilisedAmt(rs.getBigDecimal("UtilisedAmt"));
			ea.setReservedAmt(rs.getBigDecimal("ReservedAmt"));
			ea.setBalanceAmt(rs.getBigDecimal("BalanceAmt"));

			return ea;
		}

	}

	private class ExcessReserveRowMapper implements RowMapper<FinExcessAmountReserve> {
		public ExcessReserveRowMapper() {
			super();
		}

		@Override
		public FinExcessAmountReserve mapRow(ResultSet rs, int rowNum) throws SQLException {
			FinExcessAmountReserve fear = new FinExcessAmountReserve();

			fear.setReceiptSeqID(rs.getLong("ReceiptSeqID"));
			fear.setExcessID(rs.getLong("ExcessID"));
			fear.setReservedAmt(rs.getBigDecimal("ReservedAmt"));

			return fear;
		}

	}

	@Override
	public boolean isFinExcessAmtExists(long finID) {
		String sql = "Select count(FinID) From FinExcessAmount Where FinID = ? and (BalanceAmt > 0 or ReservedAmt > 0)";

		logger.debug(Literal.SQL + sql);
		return this.jdbcOperations.queryForObject(sql, Integer.class, finID) > 0;
	}
}
