package com.pennant.backend.dao.receipts.impl;

import java.math.BigDecimal;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

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
import com.pennanttech.pennapps.core.util.DateUtil;
import com.pennanttech.pff.advancepayment.AdvancePaymentUtil.AdvanceRuleCode;
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
		sql.append(" Where FinID = ? and Amount > ?");

		logger.debug(Literal.SQL + sql.toString());

		ExcessAmountRowMapper rowMapper = new ExcessAmountRowMapper();

		List<FinExcessAmount> list = this.jdbcOperations.query(sql.toString(), ps -> {
			ps.setLong(1, finId);
			ps.setInt(2, 0);
		}, rowMapper);

		return list.stream().sorted((l1, l2) -> DateUtil.compare(l1.getValueDate(), l2.getValueDate()))
				.collect(Collectors.toList());
	}

	@Override
	public void saveExcess(FinExcessAmount fe) {
		List<FinExcessAmount> list = new ArrayList<>();
		list.add(fe);

		saveExcessList(list);
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

		logger.debug(Literal.SQL.concat(sql));

		return this.jdbcOperations.update(sql, ps -> {
			int index = 1;

			ps.setBigDecimal(index++, amount);
			ps.setBigDecimal(index++, amount);
			ps.setLong(index++, finID);
			ps.setString(index, amountType);
		});
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
		sql.append(" Where ExcessID = ? and BalanceAmt >= ? ");

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
	public FinExcessAmountReserve getExcessReserve(long receiptSeqID, long payAgainstID, String paymentType) {
		String sql = "Select ReceiptSeqID, ExcessID, ReservedAmt From FinExcessAmountReserve Where ReceiptSeqID = ? and ExcessID = ? and PaymentType=?";

		logger.debug(Literal.SQL + sql);

		ExcessReserveRowMapper rowMapper = new ExcessReserveRowMapper();
		try {
			return this.jdbcOperations.queryForObject(sql, rowMapper, receiptSeqID, payAgainstID, paymentType);
		} catch (EmptyResultDataAccessException e) {
			logger.warn(Message.NO_RECORD_FOUND);
			return null;
		}
	}

	@Override
	public List<FinExcessAmountReserve> getExcessReserveList(long receiptSeqID) {
		String sql = "Select ReceiptSeqID, ExcessID, ReservedAmt From FinExcessAmountReserve Where ReceiptSeqID = ?";

		logger.debug(Literal.SQL.concat(sql));

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
	public FinExcessAmount getExcessAmountsByRefAndType1(long finID, String amountType) {
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

	@Override
	public List<FinExcessAmount> getExcessAmountsByRefAndType(long finID, String amountType) {
		StringBuilder sql = getExcessAmountSqlQuery();
		sql.append(" Where FinID = ? and AmountType = ?");

		logger.debug(Literal.SQL + sql.toString());

		ExcessAmountRowMapper rowMapper = new ExcessAmountRowMapper();

		return jdbcOperations.query(sql.toString(), rowMapper, finID, amountType);
	}

	@Override
	public List<FinExcessAmount> getExcessAmountsByRefAndType(long finID, Date valueDate, String amountType) {
		StringBuilder sql = getExcessAmountSqlQuery();
		sql.append(" Where FinID = ? and ValueDate <= ? and AmountType = ?");

		logger.debug(Literal.SQL + sql.toString());

		ExcessAmountRowMapper rowMapper = new ExcessAmountRowMapper();

		return jdbcOperations.query(sql.toString(), rowMapper, finID, valueDate, amountType);
	}

	@Override
	public List<FinExcessAmount> getExcessAmountsByRefAndType(long finID) {
		StringBuilder sql = getExcessAmountSqlQuery();
		sql.append(" Where FinID = ?");

		logger.debug(Literal.SQL + sql.toString());

		ExcessAmountRowMapper rowMapper = new ExcessAmountRowMapper();

		return jdbcOperations.query(sql.toString(), rowMapper, finID);
	}

	@Override
	public void deductExcessReserve(long excessID, BigDecimal amount) {
		String sql = "Update FinExcessAmount Set ReservedAmt = ReservedAmt - ?, Amount = Amount - ? Where ExcessID = ?";

		logger.debug(Literal.SQL.concat(sql));

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
	public int updExcessAfterRealize(long finID, String amountType, BigDecimal amount, long receiptID) {
		String sql = "Update FinExcessAmount Set BalanceAmt = BalanceAmt + ?, ReservedAmt = ReservedAmt - ? Where FinID = ? and ReceiptId = ? and AmountType = ? ";

		logger.debug(Literal.SQL + sql);

		return this.jdbcOperations.update(sql, ps -> {
			int index = 0;

			ps.setBigDecimal(++index, amount);
			ps.setBigDecimal(++index, amount);
			ps.setLong(++index, finID);
			ps.setLong(++index, receiptID);
			ps.setString(++index, amountType);

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

	private StringBuilder getExcessAmountSqlQuery() {
		StringBuilder sql = new StringBuilder("Select");
		sql.append(" ExcessID, FinID, FinReference, AmountType, Amount, UtilisedAmt, ReservedAmt");
		sql.append(", BalanceAmt, ReceiptID, ValueDate");
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
	public int updateExcessReserveByRef(long finID, String amountType, BigDecimal amount) {
		String sql = "Update FinExcessAmount Set ReservedAmt = ReservedAmt + ?, Amount = Amount + ? Where FinID = ? and AmountType = ?";

		logger.debug(Literal.SQL + sql.toString());

		logger.debug(Literal.SQL + sql.toString());
		return this.jdbcOperations.update(sql.toString(), ps -> {
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

		logger.debug(Literal.SQL.concat(sql));

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
			ea.setReceiptID(rs.getLong("ReceiptID"));
			ea.setValueDate(JdbcUtil.getDate(rs.getDate("ValueDate")));

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

		logger.debug(Literal.SQL.concat(sql));
		return this.jdbcOperations.queryForObject(sql, Integer.class, finID) > 0;
	}

	@Override
	public List<FinExcessAmount> getFinExcessByRefForAutoRefund(long finID) {
		StringBuilder sql = new StringBuilder("Select");
		sql.append(" FE.ExcessID, FE.AmountType, FE.Amount, FE.UtilisedAmt, FE.ReservedAmt, FE.BalanceAmt");
		sql.append(" From FinExcessAmount FE");
		sql.append(" Inner join FinReceiptHeader FRH on FRH.ReceiptID = FE.ReceiptID");
		sql.append(" Where FE.FinID = ? and FE.BalanceAmt > ? and FRH.ActFinReceipt = ?");
		sql.append(" order by FE.Valuedate");

		logger.debug(Literal.SQL + sql.toString());

		return this.jdbcOperations.query(sql.toString(), (rs, rowNum) -> {
			FinExcessAmount fea = new FinExcessAmount();

			fea.setExcessID(rs.getLong("ExcessID"));
			fea.setAmountType(rs.getString("AmountType"));
			fea.setAmount(rs.getBigDecimal("Amount"));
			fea.setUtilisedAmt(rs.getBigDecimal("UtilisedAmt"));
			fea.setBalanceAmt(rs.getBigDecimal("BalanceAmt"));

			return fea;
		}, finID, 0, 1);
	}

	@Override
	public FinExcessAmount getExcessAmountsByReceiptId(long receiptId) {
		StringBuilder sql = new StringBuilder("Select");
		sql.append(" ExcessID, FinID, FinReference, AmountType, Amount, UtilisedAmt");
		sql.append(", ReservedAmt, BalanceAmt, ReceiptID, ValueDate");
		sql.append(" From FinExcessAmount");
		sql.append(" Where ReceiptId = ?");

		logger.debug(Literal.SQL.concat(sql.toString()));

		try {
			return jdbcOperations.queryForObject(sql.toString(), (rs, rowNum) -> {
				FinExcessAmount ea = new FinExcessAmount();

				ea.setExcessID(rs.getLong("ExcessID"));
				ea.setFinID(rs.getLong("FinID"));
				ea.setFinReference(rs.getString("FinReference"));
				ea.setAmountType(rs.getString("AmountType"));
				ea.setAmount(rs.getBigDecimal("Amount"));
				ea.setUtilisedAmt(rs.getBigDecimal("UtilisedAmt"));
				ea.setReservedAmt(rs.getBigDecimal("ReservedAmt"));
				ea.setBalanceAmt(rs.getBigDecimal("BalanceAmt"));
				ea.setReceiptID(rs.getLong("ReceiptID"));
				ea.setValueDate(JdbcUtil.getDate(rs.getDate("ValueDate")));

				return ea;
			}, receiptId);
		} catch (EmptyResultDataAccessException e) {
			logger.warn(Message.NO_RECORD_FOUND);
			return null;
		}
	}

	@Override
	public FinExcessAmount getExcessAmountsByReceiptId(long finID, String amountType, long receiptId) {
		StringBuilder sql = new StringBuilder("Select");
		sql.append(" ExcessID, FinID, FinReference, AmountType, Amount, UtilisedAmt");
		sql.append(", ReservedAmt, BalanceAmt, ReceiptID, ValueDate");
		sql.append(" From FinExcessAmount");
		sql.append(" Where FinID = ? and AmountType = ? and ReceiptId = ?");

		logger.debug(Literal.SQL + sql.toString());

		try {
			return jdbcOperations.queryForObject(sql.toString(), (rs, rowNum) -> {
				FinExcessAmount ea = new FinExcessAmount();

				ea.setExcessID(rs.getLong("ExcessID"));
				ea.setFinID(rs.getLong("FinID"));
				ea.setFinReference(rs.getString("FinReference"));
				ea.setAmountType(rs.getString("AmountType"));
				ea.setAmount(rs.getBigDecimal("Amount"));
				ea.setUtilisedAmt(rs.getBigDecimal("UtilisedAmt"));
				ea.setReservedAmt(rs.getBigDecimal("ReservedAmt"));
				ea.setBalanceAmt(rs.getBigDecimal("BalanceAmt"));
				ea.setReceiptID(rs.getLong("ReceiptID"));
				ea.setValueDate(JdbcUtil.getDate(rs.getDate("ValueDate")));

				return ea;
			}, finID, amountType, receiptId);
		} catch (EmptyResultDataAccessException e) {
			logger.warn(Message.NO_RECORD_FOUND);
			return null;
		}
	}

	@Override
	public List<FinExcessMovement> getExcessMovementList(long id, String movementType) {
		StringBuilder sql = new StringBuilder("Select");
		sql.append(" ExcessId, MovementType, Amount, ReceiptId, TranType");
		sql.append(" From FinExcessMovement");
		sql.append(" Where ReceiptId = ? and MovementType = ?");
		sql.append(" Order by ExcessID");

		logger.debug("selectSql: " + sql.toString());

		return this.jdbcOperations.query(sql.toString(), (rs, rowNum) -> {
			FinExcessMovement fem = new FinExcessMovement();

			fem.setExcessID(rs.getLong("ExcessId"));
			fem.setMovementType(rs.getString("MovementType"));
			fem.setAmount(rs.getBigDecimal("Amount"));
			fem.setReceiptID(rs.getLong("ReceiptId"));
			fem.setTranType(rs.getString("TranType"));

			return fem;
		}, id, movementType);
	}

	@Override
	public int updateTerminationExcess(long excessID, BigDecimal amount, BigDecimal balns, BigDecimal reserved) {
		String sql = "Update FinExcessAmount Set BalanceAmt = ?, UtilisedAmt = ?, ReservedAmt = ReservedAmt - ? Where ExcessID = ?";

		return this.jdbcOperations.update(sql, ps -> {
			int index = 1;

			ps.setBigDecimal(index++, balns);
			ps.setBigDecimal(index++, amount);
			ps.setBigDecimal(index++, reserved);
			ps.setLong(index++, excessID);
		});
	}

	@Override
	public BigDecimal getExcessBalance(long finID) {
		String sql = "Select coalesce(Sum(BalanceAmt), 0) Amount From FinExcessAmount Where FinID = ? and AmountType = ?";

		logger.debug(Literal.SQL.concat(sql));

		try {
			return jdbcOperations.queryForObject(sql, BigDecimal.class, finID, RepayConstants.EXAMOUNTTYPE_EXCESS);
		} catch (EmptyResultDataAccessException e) {
			logger.warn(Message.NO_RECORD_FOUND);
			return BigDecimal.ZERO;
		}
	}

	@Override
	public List<FinExcessAmount> getExcessRcdList(long finID, Date maxValueDate) {
		StringBuilder sql = getExcessAmountSqlQuery();
		sql.append(" Where FinID = ? and AmountType = ? and BalanceAmt > ? and ValueDate <= ?");

		logger.debug(Literal.SQL.concat(sql.toString()));

		return this.jdbcOperations.query(sql.toString(), ps -> {
			int index = 0;

			ps.setLong(++index, finID);
			ps.setString(++index, RepayConstants.EXAMOUNTTYPE_EXCESS);
			ps.setBigDecimal(++index, BigDecimal.ZERO);
			ps.setDate(++index, JdbcUtil.getDate(maxValueDate));
		}, new ExcessAmountRowMapper());
	}

	@Override
	public List<FinExcessAmount> getExcessList(long finID) {
		StringBuilder sql = getExcessAmountSqlQuery();
		sql.append(" Where FinID = ?  and BalanceAmt > ?");

		logger.debug(Literal.SQL.concat(sql.toString()));

		return this.jdbcOperations.query(sql.toString(), ps -> {
			int index = 0;

			ps.setLong(++index, finID);
			ps.setBigDecimal(++index, BigDecimal.ZERO);
		}, new ExcessAmountRowMapper());
	}

	@Override
	public BigDecimal getSettlementAmountReceived(long finId) {
		String sql = "Select coalesce(sum(BalanceAmt), 0) From FinExcessAmount Where FinID = ? and AmountType= ?";

		logger.debug(Literal.SQL.concat(sql));
		try {
			return this.jdbcOperations.queryForObject(sql, BigDecimal.class, finId, "S");
		} catch (EmptyResultDataAccessException e) {
			logger.warn(Message.NO_RECORD_FOUND);
			return BigDecimal.ZERO;
		}
	}

	public FinExcessAmount getFinExcessAmountById(long excessID, String type) {
		StringBuilder sql = new StringBuilder("Select");
		sql.append(" ExcessID, FinID, FinReference, AmountType, Amount, UtilisedAmt, ReservedAmt, BalanceAmt");
		sql.append(" From FinExcessAmount");
		sql.append(StringUtils.trimToEmpty(type));
		sql.append(" Where ExcessID = ?");

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
			}, excessID);
		} catch (EmptyResultDataAccessException e) {
			//
		}

		return null;
	}

	@Override
	public void saveExcessList(List<FinExcessAmount> list) {
		for (FinExcessAmount fe : list) {
			if (fe.getId() == 0 || fe.getId() == Long.MIN_VALUE) {
				fe.setId(getNextValue("SeqFinExcessAmount"));
			}
		}

		StringBuilder sql = new StringBuilder("Insert into");
		sql.append(" FinExcessAmount ");
		sql.append("(ExcessID, FinID, FinReference, AmountType, Amount, UtilisedAmt, ReservedAmt, BalanceAmt");
		sql.append(" , ReceiptId, PostDate, ValueDate");
		sql.append(") values(");
		sql.append("?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?");
		sql.append(")");

		logger.debug(Literal.SQL + sql.toString());

		jdbcOperations.batchUpdate(sql.toString(), new BatchPreparedStatementSetter() {

			@Override
			public void setValues(PreparedStatement ps, int i) throws SQLException {
				FinExcessAmount fe = list.get(i);

				int index = 0;

				ps.setLong(++index, fe.getExcessID());
				ps.setLong(++index, fe.getFinID());
				ps.setString(++index, fe.getFinReference());
				ps.setString(++index, fe.getAmountType());
				ps.setBigDecimal(++index, fe.getAmount());
				ps.setBigDecimal(++index, fe.getUtilisedAmt());
				ps.setBigDecimal(++index, fe.getReservedAmt());
				ps.setBigDecimal(++index, fe.getBalanceAmt());
				ps.setLong(++index, fe.getReceiptID());
				ps.setDate(++index, JdbcUtil.getDate(fe.getPostDate()));
				ps.setDate(++index, JdbcUtil.getDate(fe.getValueDate()));
			}

			@Override
			public int getBatchSize() {
				return list.size();
			}
		});
	}

	@Override
	public void updateExcessreserved(long receiptID, BigDecimal excessAmt) {
		StringBuilder sql = new StringBuilder("Update FinExcessAmount");
		sql.append(" Set ReservedAmt = ReservedAmt - ?, BalanceAmt = BalanceAmt + ?");
		sql.append(" Where receiptID = ? ");

		logger.debug(Literal.SQL.concat(sql.toString()));

		int recordCount = this.jdbcOperations.update(sql.toString(), ps -> {
			int index = 0;

			ps.setBigDecimal(++index, excessAmt);
			ps.setBigDecimal(++index, excessAmt);
			ps.setLong(++index, receiptID);
		});

		if (recordCount <= 0) {
			throw new ConcurrencyException();
		}
	}

	@Override
	public BigDecimal getBalAdvIntAmt(String finReference) {
		String sql = "Select BalanceAmt From FinExcessAmount Where FinReference = ? and AmountType = ?";

		try {
			return this.jdbcOperations.queryForObject(sql, BigDecimal.class, finReference,
					AdvanceRuleCode.ADVINT.name());
		} catch (EmptyResultDataAccessException e) {
			return BigDecimal.ZERO;
		}
	}
}
