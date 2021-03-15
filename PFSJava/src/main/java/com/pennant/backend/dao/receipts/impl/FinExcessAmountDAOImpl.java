/**
 * Copyright 2011 - Pennant Technologies
 * 
 * This file is part of Pennant Java Application Framework and related Products. 
 * All components/modules/functions/classes/logic in this software, unless 
 * otherwise stated, the property of Pennant Technologies. 
 * 
 * Copyright and other intellectual property laws protect these materials. 
 * Reproduction or retransmission of the materials, in whole or in part, in any manner, 
 * without the prior written consent of the copyright holder, is a violation of 
 * copyright law.
 */

/**
 ********************************************************************************************
 *                                 FILE HEADER                                              *
 ********************************************************************************************
 *																							*
 * FileName    		:  FinanceRepaymentsDAOImpl.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  05-05-2011    														*
 *                                                                  						*
 * Modified Date    :  05-05-2011    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 05-05-2011       Pennant	                 0.1                                            * 
 *                                                                                          * 
 *                                                                                          * 
 *                                                                                          * 
 *                                                                                          * 
 *                                                                                          * 
 *                                                                                          * 
 *                                                                                          * 
 *                                                                                          * 
 ********************************************************************************************
 */
package com.pennant.backend.dao.receipts.impl;

import java.math.BigDecimal;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.PreparedStatementSetter;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSourceUtils;
import org.springframework.jdbc.core.simple.ParameterizedBeanPropertyRowMapper;

import com.pennant.backend.dao.receipts.FinExcessAmountDAO;
import com.pennant.backend.model.finance.FinExcessAmount;
import com.pennant.backend.model.finance.FinExcessAmountReserve;
import com.pennant.backend.model.finance.FinExcessMovement;
import com.pennant.backend.model.financemanagement.PresentmentDetail;
import com.pennant.backend.util.RepayConstants;
import com.pennant.eod.constants.EodConstants;
import com.pennanttech.pennapps.core.App;
import com.pennanttech.pennapps.core.App.Database;
import com.pennanttech.pennapps.core.ConcurrencyException;
import com.pennanttech.pennapps.core.jdbc.JdbcUtil;
import com.pennanttech.pennapps.core.jdbc.SequenceDao;
import com.pennanttech.pennapps.core.resource.Literal;

/**
 * DAO methods implementation for the <b>Finance Repayments</b> class.<br>
 * 
 */
public class FinExcessAmountDAOImpl extends SequenceDao<FinExcessAmount> implements FinExcessAmountDAO {
	private static Logger logger = LogManager.getLogger(FinExcessAmountDAOImpl.class);

	public FinExcessAmountDAOImpl() {
		super();
	}

	/**
	 * Method for Fetching List of Excess amounts exist against Finance Reference
	 */
	@Override
	public List<FinExcessAmount> getExcessAmountsByRef(String finReference) {
		StringBuilder sql = getExcessAmountSqlQuery();
		sql.append(" Where FinReference = ?");

		logger.trace(Literal.SQL + sql.toString());

		ExcessAmountRowMapper rowMapper = new ExcessAmountRowMapper();

		try {
			return this.jdbcOperations.query(sql.toString(), new PreparedStatementSetter() {
				@Override
				public void setValues(PreparedStatement ps) throws SQLException {
					int index = 1;
					ps.setString(index++, finReference);
				}
			}, rowMapper);
		} catch (EmptyResultDataAccessException e) {
			logger.error(Literal.EXCEPTION, e);
		}

		return new ArrayList<>();
	}

	/**
	 * Method for Saving Excess Movements after Excess Utilization
	 */
	@Override
	public void saveExcess(FinExcessAmount fe) {
		if (fe.getId() == 0 || fe.getId() == Long.MIN_VALUE) {
			fe.setId(getNextValue("SeqFinExcessAmount"));
		}

		StringBuilder sql = new StringBuilder("Insert into");
		sql.append(" FinExcessAmount ");
		sql.append("(ExcessID, FinReference, AmountType, Amount, UtilisedAmt, ReservedAmt, BalanceAmt");
		sql.append(") values(");
		sql.append("?, ?, ?, ?, ?, ?, ?");
		sql.append(")");

		logger.trace(Literal.SQL + sql.toString());

		jdbcOperations.update(sql.toString(), ps -> {
			int index = 1;

			ps.setLong(index++, JdbcUtil.setLong(fe.getExcessID()));
			ps.setString(index++, fe.getFinReference());
			ps.setString(index++, fe.getAmountType());
			ps.setBigDecimal(index++, fe.getAmount());
			ps.setBigDecimal(index++, fe.getUtilisedAmt());
			ps.setBigDecimal(index++, fe.getReservedAmt());
			ps.setBigDecimal(index++, fe.getBalanceAmt());

		});
	}

	@Override
	public void updateExcess(FinExcessAmount excess) {
		StringBuilder sql = new StringBuilder();
		sql.append("update FinExcessAmount");
		sql.append(" set Amount = :Amount, UtilisedAmt = :UtilisedAmt");
		sql.append(", ReservedAmt = :ReservedAmt, BalanceAmt = :BalanceAmt");
		sql.append(" where ExcessID = :ExcessID");

		logger.trace(Literal.SQL + sql.toString());

		MapSqlParameterSource source = new MapSqlParameterSource();
		source.addValue("ExcessID", excess.getExcessID());
		source.addValue("Amount", excess.getAmount());
		source.addValue("UtilisedAmt", excess.getUtilisedAmt());
		source.addValue("ReservedAmt", excess.getReservedAmt());
		source.addValue("BalanceAmt", excess.getBalanceAmt());

		this.jdbcTemplate.update(sql.toString(), source);
	}

	@Override
	public FinExcessAmount getFinExcessAmount(String finreference, String amountType) {
		StringBuilder sql = new StringBuilder("Select");
		sql.append(" ExcessID, FinReference, AmountType, Amount, UtilisedAmt, ReservedAmt, BalanceAmt");
		sql.append(" From FinExcessAmount");
		sql.append(" where FinReference = ? and AmountType = ?");

		logger.trace(Literal.SQL + sql.toString());

		try {
			return this.jdbcOperations.queryForObject(sql.toString(), new Object[] { finreference, amountType },
					new RowMapper<FinExcessAmount>() {
						@Override
						public FinExcessAmount mapRow(ResultSet rs, int rowNum) throws SQLException {
							FinExcessAmount fea = new FinExcessAmount();

							fea.setExcessID(rs.getLong("ExcessID"));
							fea.setFinReference(rs.getString("FinReference"));
							fea.setAmountType(rs.getString("AmountType"));
							fea.setAmount(rs.getBigDecimal("Amount"));
							fea.setUtilisedAmt(rs.getBigDecimal("UtilisedAmt"));
							fea.setReservedAmt(rs.getBigDecimal("ReservedAmt"));
							fea.setBalanceAmt(rs.getBigDecimal("BalanceAmt"));

							return fea;
						}
					});
		} catch (EmptyResultDataAccessException e) {
			logger.warn("Excess Amount not exists for the specified FinReference {} and Amount Type {}", finreference,
					amountType);
		}

		return null;
	}

	@Override
	public FinExcessAmount getFinExcessAmount(String finreference, long receiptId) {
		StringBuilder sql = new StringBuilder();

		sql.append("select fa.ExcessId, FinReference, AmountType");
		sql.append(", fa.Amount, fa.UtilisedAmt, fa.ReservedAmt, fa.BalanceAmt");
		sql.append(" from FinExcessAmount fa");
		sql.append(" inner join FinExcessMovement em on em.ExcessId = fa.ExcessId and MovementFrom = :MovementFrom");
		sql.append(" where fa.FinReference = :FinReference");
		sql.append(" and fa.AmountType = :AmountType and em.ReceiptID = :ReceiptID");

		logger.trace(Literal.SQL + sql.toString());

		MapSqlParameterSource source = new MapSqlParameterSource();
		source.addValue("FinReference", finreference);
		source.addValue("MovementFrom", "UPFRONT");
		source.addValue("AmountType", RepayConstants.EXAMOUNTTYPE_EXCESS);
		source.addValue("ReceiptID", receiptId);

		RowMapper<FinExcessAmount> rowMapper = new ExcessAmountRowMapper();
		try {
			return this.jdbcTemplate.queryForObject(sql.toString(), source, rowMapper);
		} catch (EmptyResultDataAccessException e) {
			logger.warn("Excess Amount not exists for the specified FinReference {} and ReceiptId {}", finreference,
					receiptId);
		}

		return null;
	}

	/**
	 * Method for Update utilization amount after amounts Approval
	 */
	@Override
	public void updateUtilise(long excessID, BigDecimal amount) {
		StringBuilder sql = new StringBuilder("Update FinExcessAmount");
		sql.append(" Set UtilisedAmt = UtilisedAmt + ?, ReservedAmt = ReservedAmt - ?");
		sql.append(" Where ExcessID = ? and ReservedAmt >= ?");

		logger.trace(Literal.SQL + sql.toString());
		int recordCount = this.jdbcOperations.update(sql.toString(), ps -> {
			int index = 1;

			ps.setBigDecimal(index++, amount);
			ps.setBigDecimal(index++, amount);
			ps.setLong(index++, excessID);
			ps.setBigDecimal(index++, amount);
		});

		if (recordCount <= 0) {
			throw new ConcurrencyException();
		}
	}

	/**
	 * Method for Update utilization amount after amounts Approval
	 */
	@Override
	public void updateUtiliseOnly(long excessID, BigDecimal paidNow) {
		StringBuilder sql = new StringBuilder("Update FinExcessAmount");
		sql.append(" Set UtilisedAmt = UtilisedAmt + ? ,  BalanceAmt = BalanceAmt - ?");
		sql.append(" Where ExcessID = ?");

		logger.trace(Literal.SQL + sql.toString());

		this.jdbcOperations.update(sql.toString(), ps -> {

			int index = 1;
			ps.setBigDecimal(index++, paidNow);
			ps.setBigDecimal(index++, paidNow);
			ps.setLong(index++, excessID);

		});

	}

	/**
	 * Method for Update Excess Balance amount after amounts Approval
	 */
	@Override
	public void updateExcessBal(long excessID, BigDecimal amount) {
		int recordCount = 0;
		MapSqlParameterSource source = new MapSqlParameterSource();
		source.addValue("ExcessID", excessID);
		source.addValue("PaidNow", amount);

		StringBuilder sql = new StringBuilder("Update FinExcessAmount");
		sql.append(" Set Amount = Amount + :PaidNow, BalanceAmt = BalanceAmt + :PaidNow ");
		sql.append(" Where ExcessID =:ExcessID");

		logger.trace(Literal.SQL + sql.toString());
		recordCount = this.jdbcTemplate.update(sql.toString(), source);

		if (recordCount <= 0) {
			throw new ConcurrencyException();
		}
	}

	/**
	 * Method for Update Excess Balance amount after amounts Approval
	 */
	@Override
	public int updateExcessBalByRef(String reference, String amountType, BigDecimal amount) {
		int recordCount = 0;
		MapSqlParameterSource source = new MapSqlParameterSource();
		source.addValue("FinReference", reference);
		source.addValue("AmountType", amountType);
		source.addValue("PaidNow", amount);

		StringBuilder sql = new StringBuilder("Update FinExcessAmount");
		sql.append(" Set Amount = Amount + :PaidNow, BalanceAmt = BalanceAmt + :PaidNow ");
		sql.append(" Where FinReference =:FinReference And AmountType=:AmountType");

		logger.trace(Literal.SQL + sql.toString());
		recordCount = this.jdbcTemplate.update(sql.toString(), source);

		return recordCount;
	}

	/**
	 * Method for Saving Excess Movements after Excess Utilization
	 */

	@Override
	public long saveExcessMovements(List<FinExcessMovement> excessMovement) {
		StringBuilder sql = new StringBuilder("Insert into");
		sql.append(" FinExcessMovement");
		sql.append(" (ExcessID, ReceiptID, MovementType, TranType, Amount, MovementFrom, SchDate");
		sql.append(") values(");
		sql.append("?, ?, ?, ?, ?, ?, ?");
		sql.append(")");

		logger.trace(Literal.SQL + sql.toString());
		return jdbcOperations.batchUpdate(sql.toString(), new BatchPreparedStatementSetter() {

			@Override
			public void setValues(PreparedStatement ps, int index) throws SQLException {
				int i = 1;
				FinExcessMovement fe = excessMovement.get(index);

				ps.setLong(i++, JdbcUtil.setLong(fe.getExcessID()));
				ps.setLong(i++, JdbcUtil.setLong(fe.getReceiptID()));
				ps.setString(i++, fe.getMovementType());
				ps.setString(i++, fe.getTranType());
				ps.setBigDecimal(i++, fe.getAmount());
				ps.setString(i++, fe.getMovementFrom());
				ps.setDate(i++, JdbcUtil.getDate(fe.getSchDate()));

			}

			@Override
			public int getBatchSize() {
				return excessMovement.size();
			}
		}).length;

	}

	/**
	 * Method for Saving Excess Movements after Excess Utilization
	 */
	@Override
	public void saveExcessMovement(FinExcessMovement fe) {
		StringBuilder sql = new StringBuilder("Insert into");
		sql.append(" FinExcessMovement");
		sql.append(" (ExcessID, ReceiptID, MovementType, TranType, Amount, MovementFrom, SchDate");
		sql.append(") values(");
		sql.append("?, ?, ?, ?, ?, ?, ?");
		sql.append(")");

		logger.trace(Literal.SQL + sql.toString());

		this.jdbcOperations.update(sql.toString(), ps -> {
			int index = 1;

			ps.setLong(index++, JdbcUtil.setLong(fe.getExcessID()));
			ps.setLong(index++, JdbcUtil.setLong(fe.getReceiptID()));
			ps.setString(index++, fe.getMovementType());
			ps.setString(index++, fe.getTranType());
			ps.setBigDecimal(index++, fe.getAmount());
			ps.setString(index++, fe.getMovementFrom());
			ps.setDate(index++, JdbcUtil.getDate(fe.getSchDate()));
		});

	}

	/**
	 * Method for updating Reserved amount against Excess ID
	 */
	@Override
	public void updateExcessReserve(long payAgainstID, BigDecimal reserveAmt) {
		StringBuilder sql = new StringBuilder("Update FinExcessAmount");
		sql.append(" Set ReservedAmt = ReservedAmt + ?, BalanceAmt = BalanceAmt - ?");
		sql.append(" Where ExcessID = ? and (BalanceAmt-ReservedAmt) >= ? ");

		logger.trace(Literal.SQL + sql.toString());
		int recordCount = this.jdbcOperations.update(sql.toString(), ps -> {
			int index = 1;

			ps.setBigDecimal(index++, reserveAmt);
			ps.setBigDecimal(index++, reserveAmt);
			ps.setLong(index++, payAgainstID);
			ps.setBigDecimal(index++, reserveAmt);
		});

		if (recordCount <= 0) {
			throw new ConcurrencyException();
		}
	}

	/**
	 * Method for Fetch the Reserved Excess Amounts Log details
	 */
	@Override
	public FinExcessAmountReserve getExcessReserve(long receiptSeqID, long payAgainstID) {
		StringBuilder sql = getExcessReserveSqlQuery();
		sql.append(" Where ReceiptSeqID = ? and ExcessID = ?");

		logger.trace(Literal.SQL + sql.toString());

		ExcessReserveRowMapper rowMapper = new ExcessReserveRowMapper();
		try {
			return this.jdbcOperations.queryForObject(sql.toString(), new Object[] { receiptSeqID, payAgainstID },
					rowMapper);
		} catch (EmptyResultDataAccessException e) {
			logger.warn(
					"No Records found in FinExcessAmountReserve table for specified ReceiptSeqID >> {} and ExcessID >> {}",
					receiptSeqID, payAgainstID);
		}

		return null;
	}

	private StringBuilder getExcessReserveSqlQuery() {
		StringBuilder sql = new StringBuilder("Select");
		sql.append(" ReceiptSeqID, ExcessID, ReservedAmt");
		sql.append(" from FinExcessAmountReserve");
		return sql;
	}

	/**
	 * Method for Fetch the Reserved Excess Amounts Log details
	 */
	@Override
	public List<FinExcessAmountReserve> getExcessReserveList(long receiptSeqID) {
		logger.debug(Literal.ENTERING);

		StringBuilder sql = getExcessReserveSqlQuery();
		sql.append(" Where ReceiptSeqID = ?");

		logger.trace(Literal.SQL + sql.toString());

		ExcessReserveRowMapper rowMapper = new ExcessReserveRowMapper();

		try {
			return this.jdbcOperations.query(sql.toString(), new PreparedStatementSetter() {

				@Override
				public void setValues(PreparedStatement ps) throws SQLException {
					int index = 1;
					ps.setLong(index++, receiptSeqID);
				}
			}, rowMapper);
		} catch (EmptyResultDataAccessException e) {
			logger.error(Literal.EXCEPTION, e);
		}

		logger.debug(Literal.LEAVING);
		return new ArrayList<>();
	}

	/**
	 * Method for Save Reserved amount against Excess ID
	 */
	@Override
	public void saveExcessReserveLog(long receiptSeqID, long payAgainstID, BigDecimal reserveAmt, String paymentType) {
		FinExcessAmountReserve reserve = new FinExcessAmountReserve();
		reserve.setReceiptSeqID(receiptSeqID);
		reserve.setExcessID(payAgainstID);
		reserve.setReservedAmt(reserveAmt);
		reserve.setPaymentType(paymentType);

		StringBuilder sql = new StringBuilder("Insert Into FinExcessAmountReserve ");
		sql.append(" (ExcessID, ReceiptSeqID, ReservedAmt, PaymentType )");
		sql.append(" Values(:ExcessID, :ReceiptSeqID, :ReservedAmt, :PaymentType)");

		logger.trace(Literal.SQL + sql.toString());

		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(reserve);
		this.jdbcTemplate.update(sql.toString(), beanParameters);
	}

	/**
	 * Method for updating Reserved excess Amount after modifications
	 */
	@Override
	public void updateExcessReserveLog(long receiptID, long payAgainstID, BigDecimal diffInReserve,
			String paymentType) {

		int recordCount = 0;
		MapSqlParameterSource source = new MapSqlParameterSource();
		source.addValue("ReceiptSeqID", receiptID);
		source.addValue("ExcessID", payAgainstID);
		source.addValue("PaidNow", diffInReserve);
		source.addValue("PaymentType", paymentType);

		StringBuilder sql = new StringBuilder("Update FinExcessAmountReserve ");
		sql.append(" Set ReservedAmt = ReservedAmt + :PaidNow ");
		sql.append(" Where ReceiptSeqID =:ReceiptSeqID AND ExcessID =:ExcessID AND PaymentType =:PaymentType ");

		logger.trace(Literal.SQL + sql.toString());
		recordCount = this.jdbcTemplate.update(sql.toString(), source);

		if (recordCount <= 0) {
			throw new ConcurrencyException();
		}
	}

	/**
	 * Method for Deleting Reserved Amounts against Excess ID Processed for Utilization
	 */
	@Override
	public void deleteExcessReserve(long receiptID, long payAgainstID, String paymentType) {
		StringBuilder sql = new StringBuilder("Delete From FinExcessAmountReserve");
		sql.append(" Where ReceiptSeqID = ? and PaymentType = ?");
		if (payAgainstID != 0) {
			sql.append(" and ExcessID = ?");
		}

		logger.trace(Literal.SQL + sql.toString());
		this.jdbcOperations.update(sql.toString(), ps -> {
			int index = 1;
			ps.setLong(index++, receiptID);
			ps.setString(index++, paymentType);
			if (payAgainstID != 0) {
				ps.setLong(index++, payAgainstID);
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

		logger.trace(Literal.SQL + sql.toString());

		return jdbcOperations.batchUpdate(sql.toString(), new BatchPreparedStatementSetter() {

			@Override
			public void setValues(PreparedStatement ps, int index) throws SQLException {
				FinExcessAmount ex = emiInAdvance.get(index);
				ps.setBigDecimal(1, ex.getAmount());
				ps.setBigDecimal(2, ex.getAmount());
				ps.setLong(3, ex.getId());
			}

			@Override
			public int getBatchSize() {
				return emiInAdvance.size();
			}
		}).length;
	}

	@Override
	public void updateExcessAmount(long excessID, String amountType, BigDecimal amount) {
		int recordCount = 0;
		FinExcessAmount finExcessAmount = new FinExcessAmount();
		finExcessAmount.setExcessID(excessID);
		finExcessAmount.setAmount(amount);

		StringBuilder sql = new StringBuilder("Update FinExcessAmount");
		if ("R".equals(amountType)) {
			sql.append(" Set ReservedAmt = ReservedAmt + :amount, BalanceAmt = BalanceAmt - :amount ");
		} else if ("U".equals(amountType)) {
			sql.append(" Set UtilisedAmt = UtilisedAmt + :amount, BalanceAmt = BalanceAmt - :amount ");
		}
		sql.append(" Where ExcessID =:ExcessID");

		logger.trace(Literal.SQL + sql.toString());

		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(finExcessAmount);
		recordCount = this.jdbcTemplate.update(sql.toString(), beanParameters);

		if (recordCount <= 0) {
			throw new ConcurrencyException();
		}
	}

	@Override
	public int updateExcessAmtList(List<FinExcessAmount> excess) {
		StringBuilder sql = new StringBuilder();
		sql.append(" Update FinExcessAmount Set ReservedAmt = ReservedAmt - ?,");
		sql.append(" BalanceAmt = BalanceAmt + ?");
		sql.append(" Where ExcessID = ?");

		logger.trace(Literal.SQL + sql.toString());
		return jdbcOperations.batchUpdate(sql.toString(), new BatchPreparedStatementSetter() {

			@Override
			public void setValues(PreparedStatement ps, int index) throws SQLException {
				FinExcessAmount ex = excess.get(index);
				ps.setBigDecimal(1, ex.getAmount());
				ps.setBigDecimal(2, ex.getAmount());
				ps.setLong(3, ex.getId());
			}

			@Override
			public int getBatchSize() {
				return excess.size();
			}
		}).length;
	}

	@Override
	public void updateExcessAmount(long excessID, BigDecimal advanceAmount) {
		StringBuilder sql = new StringBuilder();
		sql.append(" Update FinExcessAmount Set ReservedAmt = ReservedAmt - :AdvanceAmount,");
		sql.append(" BalanceAmt = BalanceAmt + :AdvanceAmount");
		sql.append(" Where ExcessID = :ExcessID");

		logger.trace(Literal.SQL + sql.toString());

		MapSqlParameterSource source = new MapSqlParameterSource();
		source.addValue("AdvanceAmount", advanceAmount);
		source.addValue("ExcessID", excessID);
		try {
			this.jdbcTemplate.update(sql.toString(), source);
		} catch (Exception e) {
			logger.error(Literal.EXCEPTION, e);
			throw e;
		}
	}

	@Override
	public void batchUpdateExcessAmount(List<PresentmentDetail> presements) {
		StringBuilder sql = new StringBuilder();
		sql.append(" Update FinExcessAmount Set ReservedAmt = ReservedAmt - ?,");
		sql.append(" BalanceAmt = BalanceAmt + ?");
		sql.append(" Where ExcessID = ?");

		logger.trace(Literal.SQL + sql.toString());

		jdbcOperations.batchUpdate(sql.toString(), new BatchPreparedStatementSetter() {

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
	public void updateUtilizedAndBalance(FinExcessAmount excessAmount) {
		StringBuilder sql = new StringBuilder();
		sql.append("Update FinExcessAmount");
		sql.append(" set UtilisedAmt = :UtilisedAmt, BalanceAmt = :BalanceAmt");
		sql.append(" Where ExcessID = :ExcessID");
		logger.debug(Literal.SQL + sql.toString());

		MapSqlParameterSource source = new MapSqlParameterSource();
		source.addValue("UtilisedAmt", excessAmount.getUtilisedAmt());
		source.addValue("BalanceAmt", excessAmount.getBalanceAmt());
		source.addValue("ExcessID", excessAmount.getExcessID());

		try {
			this.jdbcTemplate.update(sql.toString(), source);
		} catch (Exception e) {
			logger.error(Literal.EXCEPTION, e);
			throw e;
		}
	}

	@Override
	public FinExcessAmount getExcessAmountsByRefAndType(String finReference, String amountType) {
		StringBuilder sql = getExcessAmountSqlQuery();
		sql.append(" Where FinReference = ? and AmountType = ?");

		logger.trace(Literal.SQL + sql.toString());

		ExcessAmountRowMapper rowMapper = new ExcessAmountRowMapper();

		try {
			return jdbcOperations.queryForObject(sql.toString(), new Object[] { finReference, amountType }, rowMapper);
		} catch (EmptyResultDataAccessException e) {
			logger.warn(
					"Records not found in FinExcessAmount table for the specified FinReference >> {} and AmountType >> {}",
					finReference, amountType);
		}
		return null;
	}

	private StringBuilder getExcessAmountSqlQuery() {
		StringBuilder sql = new StringBuilder("Select");
		sql.append(" ExcessID, AmountType, Amount, UtilisedAmt, ReservedAmt, BalanceAmt");
		sql.append(" From FinExcessAmount");
		return sql;
	}

	@Override
	public List<FinExcessAmount> getAllExcessAmountsByRef(String finReference, String type) {
		StringBuilder sql = getExcessAmountSqlQuery();
		sql.append(StringUtils.trimToEmpty(type));

		if (App.DATABASE == Database.SQL_SERVER) {
			sql.append(EodConstants.SQL_NOLOCK);
		}

		sql.append(" Where FinReference = ?");

		logger.trace(Literal.SQL + sql.toString());

		ExcessAmountRowMapper rowMapper = new ExcessAmountRowMapper();

		return this.jdbcOperations.query(sql.toString(), new Object[] { finReference }, rowMapper);

	}

	@Override
	public void deductExcessReserve(long excessID, BigDecimal amount) {
		int recordCount = 0;
		MapSqlParameterSource source = new MapSqlParameterSource();
		source.addValue("ExcessID", excessID);
		source.addValue("PaidNow", amount);

		StringBuilder sql = new StringBuilder("Update FinExcessAmount");
		sql.append(" Set ReservedAmt = ReservedAmt - :PaidNow,Amount = Amount - :PaidNow ");
		sql.append(" Where ExcessID =:ExcessID");

		logger.debug(Literal.SQL + sql.toString());
		recordCount = this.jdbcTemplate.update(sql.toString(), source);

		if (recordCount <= 0) {
			throw new ConcurrencyException();
		}
	}

	@Override
	public int updateExcessReserveByRef(String reference, String amountType, BigDecimal amount) {

		int recordCount = 0;
		MapSqlParameterSource source = new MapSqlParameterSource();
		source.addValue("FinReference", reference);
		source.addValue("AmountType", amountType);
		source.addValue("PaidNow", amount);

		StringBuilder sql = new StringBuilder("Update FinExcessAmount");
		sql.append(" Set ReservedAmt = ReservedAmt + :PaidNow, Amount = Amount + :PaidNow");
		sql.append(" Where FinReference =:FinReference AND AmountType=:AmountType ");

		logger.trace(Literal.SQL + sql.toString());
		recordCount = this.jdbcTemplate.update(sql.toString(), source);

		return recordCount;
	}

	@Override
	public int updExcessAfterRealize(String reference, String amountType, BigDecimal amount) {
		int recordCount = 0;
		MapSqlParameterSource source = new MapSqlParameterSource();
		source.addValue("FinReference", reference);
		source.addValue("AmountType", amountType);
		source.addValue("PaidNow", amount);

		StringBuilder updateSql = new StringBuilder("Update FinExcessAmount");
		updateSql.append(" Set  BalanceAmt = BalanceAmt + :PaidNow, ReservedAmt = ReservedAmt - :PaidNow ");
		updateSql.append(" Where FinReference =:FinReference AND AmountType=:AmountType ");

		logger.trace(Literal.SQL + updateSql.toString());
		recordCount = this.jdbcTemplate.update(updateSql.toString(), source);

		return recordCount;
	}

	@Override
	public int updateExcessReserveList(List<FinExcessAmount> excessRevarsal) {
		StringBuilder sql = new StringBuilder("Update FinExcessAmount");
		sql.append(" Set BalanceAmt = ?, ReservedAmt = ?");
		sql.append(" Where ExcessID = ?");

		return jdbcOperations.batchUpdate(sql.toString(), new BatchPreparedStatementSetter() {

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
		StringBuilder sql = new StringBuilder("Update FinExcessAmount");
		sql.append(" Set BalanceAmt = ?, ReservedAmt = ?");
		sql.append(" Where ExcessID = ?");

		return this.jdbcOperations.update(sql.toString(), ps -> {
			ps.setBigDecimal(1, em.getBalanceAmt());
			ps.setBigDecimal(2, em.getReservedAmt());
			ps.setLong(3, em.getExcessID());
		});

	}

	@Override
	public int updateReserveUtilization(FinExcessAmount excessMovement) {
		int recordCount = 0;
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(excessMovement);

		StringBuilder sql = new StringBuilder("Update FinExcessAmount");
		sql.append(" Set  BalanceAmt = :BalanceAmt, ReservedAmt = :ReservedAmt, UtilisedAmt=:UtilisedAmt ");
		sql.append(" Where ExcessID =:ExcessID");

		logger.trace(Literal.SQL + sql.toString());
		recordCount = this.jdbcTemplate.update(sql.toString(), beanParameters);

		return recordCount;
	}

	@Override
	public FinExcessMovement getFinExcessMovement(long excessID, String movementFrom, Date schDate) {
		StringBuilder sql = new StringBuilder("Select");
		sql.append(" ExcessID, ReceiptID, MovementType, TranType, Amount, MovementFrom, SchDate");
		sql.append(" From FinExcessMovement");
		sql.append(" where ExcessID = ? and MovementFrom = ? and SchDate = ?");

		logger.trace(Literal.SQL + sql.toString());

		try {
			Object[] object = new Object[] { excessID, movementFrom, JdbcUtil.getDate(schDate) };
			return this.jdbcOperations.queryForObject(sql.toString(), object, (rs, rowNum) -> {
				FinExcessMovement fea = new FinExcessMovement();

				fea.setExcessID(rs.getLong("ExcessID"));
				fea.setReceiptID(rs.getLong("ReceiptID"));
				fea.setMovementType(rs.getString("MovementType"));
				fea.setTranType(rs.getString("TranType"));
				fea.setAmount(rs.getBigDecimal("Amount"));
				fea.setMovementFrom(rs.getString("MovementFrom"));
				fea.setSchDate(rs.getTimestamp("SchDate"));

				return fea;

			});
		} catch (EmptyResultDataAccessException e) {
			logger.warn(
					"Record is not found in FinExcessMovement table for the specified ExcessID >> {} and MovementFrom >> {} and SchDate >> {}",
					excessID, movementFrom, schDate);
		}

		return null;
	}

	@Override
	public List<FinExcessMovement> getFinExcessAmount(long presentmentid) {
		StringBuilder sql = new StringBuilder();
		sql.append("select * from FinExcessMovement");
		sql.append(" where ReceiptID = :ReceiptID and MovementType = :MovementType");
		sql.append(" and TranType = :TranType");

		logger.trace(Literal.SQL + sql.toString());

		MapSqlParameterSource source = new MapSqlParameterSource();
		source.addValue("ReceiptID", presentmentid);
		source.addValue("MovementType", "I");
		source.addValue("TranType", "I");

		RowMapper<FinExcessMovement> rowMapper = ParameterizedBeanPropertyRowMapper
				.newInstance(FinExcessMovement.class);
		return this.jdbcTemplate.query(sql.toString(), source, rowMapper);
	}

	@Override
	public FinExcessAmount getFinExcessByID(long excessID) {
		StringBuilder sql = new StringBuilder();
		sql.append("select * from FinExcessAmount");
		sql.append(" where ExcessID = :ExcessID");

		logger.trace(Literal.SQL + sql.toString());

		MapSqlParameterSource source = new MapSqlParameterSource();
		source.addValue("ExcessID", excessID);

		RowMapper<FinExcessAmount> rowMapper = ParameterizedBeanPropertyRowMapper.newInstance(FinExcessAmount.class);
		try {
			return this.jdbcTemplate.queryForObject(sql.toString(), source, rowMapper);
		} catch (EmptyResultDataAccessException e) {
			logger.warn(Literal.EXCEPTION, e);
		}

		return null;
	}

	@Override
	public int deleteMovemntByPrdID(long presentmentId) {
		StringBuilder sql = new StringBuilder();
		sql.append(" DELETE from FinExcessMovement");
		sql.append(" where ReceiptID = :ReceiptID and MovementType = :MovementType ");
		sql.append(" and TranType = :TranType");

		logger.trace(Literal.SQL + sql.toString());

		MapSqlParameterSource source = new MapSqlParameterSource();
		source.addValue("ReceiptID", presentmentId);
		source.addValue("MovementType", "I");
		source.addValue("TranType", "I");

		return this.jdbcTemplate.update(sql.toString(), source);
	}

	@Override
	public void saveExcessList(List<FinExcessAmount> feaList) {
		StringBuilder sql = saveExcessQuery();

		SqlParameterSource[] beanParameters = SqlParameterSourceUtils.createBatch(feaList.toArray());

		try {
			this.jdbcTemplate.batchUpdate(sql.toString(), beanParameters);
		} catch (Exception e) {
			logger.error("Exception", e);
			logger.debug("insertSql: " + sql.toString());
			throw e;
		}

	}

	private StringBuilder saveExcessQuery() {
		StringBuilder sql = new StringBuilder("Insert Into FinExcessAmount");
		sql.append(" (ExcessID, FinReference, AmountType, Amount, UtilisedAmt, ReservedAmt, BalanceAmt)");
		sql.append(" Values(:ExcessID, :FinReference, :AmountType, :Amount, :UtilisedAmt, :ReservedAmt, :BalanceAmt)");
		return sql;
	}

	@Override
	public void saveExcessMovementList(List<FinExcessMovement> movements) {
		StringBuilder sql = saveFEMQuery();

		SqlParameterSource[] beanParameters = SqlParameterSourceUtils.createBatch(movements.toArray());

		try {
			this.jdbcTemplate.batchUpdate(sql.toString(), beanParameters);
		} catch (Exception e) {
			logger.error(Literal.EXCEPTION, e);
			throw e;
		}

	}

	private StringBuilder saveFEMQuery() {
		StringBuilder sql = new StringBuilder("Insert Into FinExcessMovement");
		sql.append(" (ExcessID, ReceiptID, MovementType, TranType, Amount, MovementFrom, SchDate)");
		sql.append(" Values(:ExcessID, :ReceiptID, :MovementType, :TranType, :Amount, :MovementFrom, :SchDate)");

		return sql;
	}

	public class ExcessAmountRowMapper implements RowMapper<FinExcessAmount> {

		@Override
		public FinExcessAmount mapRow(ResultSet rs, int rowNum) throws SQLException {
			FinExcessAmount excessAmount = new FinExcessAmount();

			excessAmount.setExcessID(rs.getLong("ExcessID"));
			excessAmount.setAmountType(rs.getString("AmountType"));
			excessAmount.setAmount(rs.getBigDecimal("Amount"));
			excessAmount.setUtilisedAmt(rs.getBigDecimal("UtilisedAmt"));
			excessAmount.setReservedAmt(rs.getBigDecimal("ReservedAmt"));
			excessAmount.setBalanceAmt(rs.getBigDecimal("BalanceAmt"));

			return excessAmount;
		}

	}

	private class ExcessReserveRowMapper implements RowMapper<FinExcessAmountReserve> {
		public ExcessReserveRowMapper() {

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
}
