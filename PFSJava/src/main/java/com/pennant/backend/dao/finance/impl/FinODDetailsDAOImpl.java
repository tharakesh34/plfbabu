/**
 * \ * Copyright 2011 - Pennant Technologies
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
 * * FileName : FinODDetailsDAOImpl.java * * Author : PENNANT TECHONOLOGIES * * Creation Date : 08-05-2012 * * Modified
 * Date : 08-05-2012 * * Description : * *
 ********************************************************************************************
 * Date Author Version Comments *
 ********************************************************************************************
 * 08-05-2012 Pennant 0.1 * * * * * * * * *
 ********************************************************************************************
 */

package com.pennant.backend.dao.finance.impl;

import java.math.BigDecimal;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;

import com.pennant.backend.dao.finance.FinODDetailsDAO;
import com.pennant.backend.model.finance.FinODDetails;
import com.pennanttech.pennapps.core.jdbc.BasicDao;
import com.pennanttech.pennapps.core.jdbc.JdbcUtil;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pennapps.core.resource.Message;
import com.pennanttech.pennapps.core.util.DateUtil;

/**
 * DAO methods implementation for the <b>FinODDetails model</b> class.<br>
 * 
 */
public class FinODDetailsDAOImpl extends BasicDao<FinODDetails> implements FinODDetailsDAO {
	private static Logger logger = LogManager.getLogger(FinODDetailsDAOImpl.class);

	public FinODDetailsDAOImpl() {
		super();
	}

	@Override
	public FinODDetails getFinODDetailsForBatch(long finID, Date schdDate) {
		StringBuilder sql = new StringBuilder("Select");
		sql.append(" FinCurODAmt, FinCurODPri, FinCurODPft, FinODTillDate, FinCurODDays");
		sql.append(", FinLMdfDate, LpCpz, LpCpzAmount, LpCurCpzBal, PayableAmount");
		sql.append(" From FinODDetails_View");
		sql.append(" Where FinID = ? and FinODSchdDate = ?");

		logger.debug(Literal.SQL + sql.toString());

		try {
			return this.jdbcOperations.queryForObject(sql.toString(), (rs, rowNum) -> {
				FinODDetails fod = new FinODDetails();
				fod.setFinCurODAmt(rs.getBigDecimal("FinCurODAmt"));
				fod.setFinCurODPri(rs.getBigDecimal("FinCurODPri"));
				fod.setFinCurODPft(rs.getBigDecimal("FinCurODPft"));
				fod.setFinODTillDate(rs.getDate("FinODTillDate"));
				fod.setFinCurODDays(rs.getInt("FinCurODDays"));
				fod.setFinLMdfDate(rs.getDate("FinLMdfDate"));
				fod.setLpCpz(rs.getBoolean("LpCpz"));
				fod.setLpCpzAmount(rs.getBigDecimal("LpCpzAmount"));
				fod.setLpCurCpzBal(rs.getBigDecimal("LpCurCpzBal"));
				fod.setPayableAmount(rs.getBigDecimal("PayableAmount"));

				return fod;
			}, finID, schdDate);
		} catch (EmptyResultDataAccessException e) {
			logger.warn(Message.NO_RECORD_FOUND);
			return null;
		}
	}

	@Override
	public void update(FinODDetails od) {
		List<FinODDetails> odList = new ArrayList<>();

		odList.add(od);

		updateList(odList);
	}

	@Override
	public void updateList(List<FinODDetails> od) {
		StringBuilder sql = updateFODQuery();

		logger.debug(Literal.SQL + sql.toString());

		jdbcOperations.batchUpdate(sql.toString(), new BatchPreparedStatementSetter() {
			@Override
			public void setValues(PreparedStatement ps, int i) throws SQLException {
				FinODDetails fd = od.get(i);

				int index = 1;
				ps.setDate(index++, JdbcUtil.getDate(fd.getFinODTillDate()));
				ps.setBigDecimal(index++, fd.getFinCurODAmt());
				ps.setBigDecimal(index++, fd.getFinCurODPri());
				ps.setBigDecimal(index++, fd.getFinCurODPft());
				ps.setInt(index++, fd.getFinCurODDays());
				ps.setBigDecimal(index++, fd.getTotPenaltyAmt());
				ps.setBigDecimal(index++, fd.getTotWaived());
				ps.setBigDecimal(index++, fd.getTotPenaltyPaid());
				ps.setBigDecimal(index++, fd.getTotPenaltyBal());
				ps.setDate(index++, JdbcUtil.getDate(fd.getFinLMdfDate()));
				ps.setBigDecimal(index++, fd.getLPIPaid());
				ps.setBigDecimal(index++, fd.getLPIBal());
				ps.setBigDecimal(index++, fd.getLPIWaived());
				ps.setBoolean(index++, fd.isLpCpz());
				ps.setBigDecimal(index++, fd.getLpCpzAmount());
				ps.setBigDecimal(index++, fd.getLpCurCpzBal());
				ps.setBigDecimal(index++, fd.getCurOverdraftTxnChrg());
				ps.setBigDecimal(index++, fd.getPayableAmount());

				ps.setLong(index++, fd.getFinID());
				ps.setDate(index, JdbcUtil.getDate(fd.getFinODSchdDate()));
			}

			@Override
			public int getBatchSize() {
				return od.size();
			}
		});
	}

	@Override
	public int updateODDetailsBatch(List<FinODDetails> overdues) {
		StringBuilder sql = new StringBuilder("Update FinODDetails");
		sql.append(" Set FinODTillDate = ?, FinCurODAmt = ?, FinCurODPri = ?, FinCurODPft = ?, FinCurODDays = ?");
		sql.append(", TotPenaltyAmt = ?, TotWaived = ?, TotPenaltyPaid = ?, TotPenaltyBal = ?, FinLMdfDate = ?");
		sql.append(", LPIAmt = ?, LPIPaid = ?, LPIBal = ?, LPIWaived = ?, CurOverdraftTxnChrg = ?");
		sql.append(", FinMaxODAmt = ?, FinMaxODPri = ?, FinMaxODPft = ? ");
		sql.append(", LppDueAmt = ?, LppDueTillDate = ?, LpiDueAmt = ?, LpiDueTillDate = ?, PayableAmount = ?");
		sql.append(" Where FinID = ? and FinODSchdDate = ? and FinODFor = ?");

		logger.debug(Literal.SQL + sql.toString());

		return jdbcOperations.batchUpdate(sql.toString(), new BatchPreparedStatementSetter() {

			@Override
			public void setValues(PreparedStatement ps, int i) throws SQLException {
				FinODDetails od = overdues.get(i);

				int index = 1;

				ps.setDate(index++, JdbcUtil.getDate(od.getFinODTillDate()));
				ps.setBigDecimal(index++, od.getFinCurODAmt());
				ps.setBigDecimal(index++, od.getFinCurODPri());
				ps.setBigDecimal(index++, od.getFinCurODPft());
				ps.setInt(index++, od.getFinCurODDays());
				ps.setBigDecimal(index++, od.getTotPenaltyAmt());
				ps.setBigDecimal(index++, od.getTotWaived());
				ps.setBigDecimal(index++, od.getTotPenaltyPaid());
				ps.setBigDecimal(index++, od.getTotPenaltyBal());
				ps.setDate(index++, JdbcUtil.getDate(od.getFinLMdfDate()));
				ps.setBigDecimal(index++, od.getLPIAmt());
				ps.setBigDecimal(index++, od.getLPIPaid());
				ps.setBigDecimal(index++, od.getLPIBal());
				ps.setBigDecimal(index++, od.getLPIWaived());
				ps.setBigDecimal(index++, od.getCurOverdraftTxnChrg());
				ps.setBigDecimal(index++, od.getFinMaxODAmt());
				ps.setBigDecimal(index++, od.getFinMaxODPri());
				ps.setBigDecimal(index++, od.getFinMaxODPft());
				ps.setBigDecimal(index++, od.getLppDueAmt());
				ps.setDate(index++, JdbcUtil.getDate(od.getLppDueTillDate()));
				ps.setBigDecimal(index++, od.getLpiDueAmt());
				ps.setDate(index++, JdbcUtil.getDate(od.getLpiDueTillDate()));
				ps.setBigDecimal(index++, od.getPayableAmount());

				ps.setLong(index++, od.getFinID());
				ps.setDate(index++, JdbcUtil.getDate(od.getFinODSchdDate()));
				ps.setString(index, od.getFinODFor());
			}

			@Override
			public int getBatchSize() {
				return overdues.size();
			}
		}).length;

	}

	@Override
	public void updateBatch(FinODDetails od) {
		StringBuilder sql = new StringBuilder("Update FinODDetails");
		sql.append(" Set FinCurODAmt = ?, FinCurODPri = ?, FinCurODPft = ?");
		sql.append(", FinODTillDate = ?, FinCurODDays = ?, FinLMdfDate = ?");
		sql.append(", LpCpz = ?, LpCpzAmount = ?, LpCurCpzBal = ?, CurOverdraftTxnChrg = ?");
		sql.append(" Where FinID = ? and FinODSchdDate = ?");

		logger.debug(Literal.SQL + sql.toString());

		this.jdbcOperations.update(sql.toString(), ps -> {
			int index = 1;
			ps.setBigDecimal(index++, od.getFinCurODAmt());
			ps.setBigDecimal(index++, od.getFinCurODPri());
			ps.setBigDecimal(index++, od.getFinCurODPft());
			ps.setDate(index++, JdbcUtil.getDate(od.getFinODTillDate()));
			ps.setInt(index++, od.getFinCurODDays());
			ps.setDate(index++, JdbcUtil.getDate(od.getFinLMdfDate()));
			ps.setBoolean(index++, od.isLpCpz());
			ps.setBigDecimal(index++, od.getLpCpzAmount());
			ps.setBigDecimal(index++, od.getLpCurCpzBal());
			ps.setBigDecimal(index++, od.getCurOverdraftTxnChrg());

			ps.setLong(index++, od.getFinID());
			ps.setDate(index, JdbcUtil.getDate(od.getFinODSchdDate()));
		});
	}

	private String updateTotalsQuery() {
		StringBuilder sql = new StringBuilder("Update FinODDetails");
		sql.append(" Set TotPenaltyAmt = (? + TotPenaltyAmt)");
		sql.append(", TotWaived = (? + TotWaived)");
		sql.append(", TotPenaltyPaid = (? + TotPenaltyPaid)");
		sql.append(", TotPenaltyBal = (? + TotPenaltyBal) , PayableAmount = (? + PayableAmount)");
		sql.append(" Where FinID = ? and FinODSchdDate = ?");
		return sql.toString();
	}

	private void updateTotals(FinODDetails od, PreparedStatement ps) throws SQLException {
		int index = 1;
		ps.setBigDecimal(index++, od.getTotPenaltyAmt());
		ps.setBigDecimal(index++, od.getTotWaived());
		ps.setBigDecimal(index++, od.getTotPenaltyPaid());
		ps.setBigDecimal(index++, od.getTotPenaltyBal());
		ps.setBigDecimal(index++, od.getPayableAmount());

		ps.setLong(index++, od.getFinID());
		ps.setDate(index, JdbcUtil.getDate(od.getFinODSchdDate()));
	}

	// FIXME: PV 09AUG19. Doubt. How come both paid and balance are setting with
	// addition.
	// Need to see impact on fields LpCurCpzBal
	@Override
	public void updateTotals(FinODDetails od) {
		String sql = updateTotalsQuery();

		logger.debug(Literal.SQL + sql);

		this.jdbcOperations.update(sql, ps -> updateTotals(od, ps));
	}

	@Override
	public void updateTotals(List<FinODDetails> list) {
		String sql = updateTotalsQuery();

		logger.debug(Literal.SQL + sql);

		this.jdbcOperations.batchUpdate(sql, new BatchPreparedStatementSetter() {

			@Override
			public void setValues(PreparedStatement ps, int index) throws SQLException {
				FinODDetails od = list.get(index);
				updateTotals(od, ps);
			}

			@Override
			public int getBatchSize() {
				return list.size();
			}

		});
	}

	// FIXME: PV 09AUG19. Need to see impact on fields LpCurCpzBal
	@Override
	public void resetTotals(FinODDetails od) {
		StringBuilder sql = new StringBuilder("Update FinODDetails");
		sql.append(" Set TotPenaltyAmt = ?, TotWaived = ?");
		sql.append(", TotPenaltyPaid = ?, TotPenaltyBal = ?");
		sql.append(", LpCpz = ?, LpCpzAmount = ?, LpCurCpzBal = ?");
		sql.append(" Where FinID = ? and FinODSchdDate = ?");

		logger.debug(Literal.SQL + sql.toString());

		this.jdbcOperations.update(sql.toString(), ps -> {
			int index = 1;

			ps.setBigDecimal(index++, od.getTotPenaltyAmt());
			ps.setBigDecimal(index++, od.getTotWaived());
			ps.setBigDecimal(index++, od.getTotPenaltyPaid());
			ps.setBigDecimal(index++, od.getTotPenaltyBal());
			ps.setBoolean(index++, od.isLpCpz());
			ps.setBigDecimal(index++, od.getLpCpzAmount());
			ps.setBigDecimal(index++, od.getLpCurCpzBal());

			ps.setLong(index++, od.getFinID());
			ps.setDate(index, JdbcUtil.getDate(od.getFinODSchdDate()));
		});
	}

	public int getPendingOverDuePayment(long finID) {
		String sql = "Select coalesce(max(FinCurODDays), 0) From FinODDetails Where FinID = ? and FinCurODAmt > ?";

		logger.debug(Literal.SQL + sql);

		return this.jdbcOperations.queryForObject(sql, Integer.class, finID, BigDecimal.ZERO);
	}

	@Override
	public int getFinODDays(long finID) {
		String sql = "Select coalesce(max(FinCurODDays), 0) From FinODDetails Where FinID = ? and FinCurODAmt <> ?";

		logger.debug(Literal.SQL + sql);

		return this.jdbcOperations.queryForObject(sql, Integer.class, finID, BigDecimal.ZERO);

	}

	@Override
	public FinODDetails getFinODSummary(long finID) {
		StringBuilder sql = new StringBuilder("Select");
		sql.append(" FinID, sum(TotPenaltyAmt) TotPenaltyAmt, sum(TotWaived) TotWaived");
		sql.append(", sum(TotPenaltyPaid) TotPenaltyPaid, sum(TotPenaltyBal) TotPenaltyBal");
		sql.append(", sum(FinCurODPri) FinCurODPri, sum(FinCurODPft) FinCurODPft, sum(LpCpzAmount) LpCpzAmount");
		sql.append(", sum(FinCurODAmt) FinCurODAmt");
		sql.append(", min(FinODSchdDate) FinODSchdDate, max(FinODSchdDate) FinODTillDate");
		sql.append(", max(FinCurODDays) FinCurODDays, sum(LpiAmt) LpiAmt, sum(LpiPaid) LpiPaid");
		sql.append(", sum(LpiBal) LpiBal, sum(LpiWaived) LpiWaived");
		sql.append(" From FinODDetails");
		sql.append(" Where FinID = ? group by FinID");

		logger.debug(Literal.SQL + sql.toString());

		try {
			return this.jdbcOperations.queryForObject(sql.toString(), (rs, rowNUm) -> {

				FinODDetails od = new FinODDetails();

				od.setTotPenaltyAmt(rs.getBigDecimal("TotPenaltyAmt"));
				od.setTotWaived(rs.getBigDecimal("TotWaived"));
				od.setTotPenaltyPaid(rs.getBigDecimal("TotPenaltyPaid"));
				od.setTotPenaltyBal(rs.getBigDecimal("TotPenaltyBal"));
				od.setFinCurODPri(rs.getBigDecimal("FinCurODPri"));
				od.setFinCurODPft(rs.getBigDecimal("FinCurODPft"));
				od.setLpCpzAmount(rs.getBigDecimal("LpCpzAmount"));
				od.setFinCurODAmt(rs.getBigDecimal("FinCurODAmt"));
				od.setFinODSchdDate(rs.getDate("FinODSchdDate"));
				od.setFinODTillDate(rs.getDate("FinODTillDate"));
				od.setFinCurODDays(rs.getInt("FinCurODDays"));
				od.setLPIAmt(rs.getBigDecimal("LpiAmt"));
				od.setLPIPaid(rs.getBigDecimal("LpiPaid"));
				od.setLPIBal(rs.getBigDecimal("LpiBal"));
				od.setLPIWaived(rs.getBigDecimal("LpiWaived"));

				return od;
			}, finID);
		} catch (EmptyResultDataAccessException e) {
			logger.warn(Message.NO_RECORD_FOUND);
			return null;
		}
	}

	@Override
	public int getFinCurSchdODDays(long finID, Date finODSchdDate) {
		String sql = "Select coalesce(max(FinCurODDays), 0) From FinODDetails Where FinID = ? and FinODSchdDate = ?";

		logger.debug(Literal.SQL + sql);

		return this.jdbcOperations.queryForObject(sql, Integer.class, finID, finODSchdDate);
	}

	@Override
	public List<FinODDetails> getFinODDByFinRef(long finID, Date odSchdDate) {
		StringBuilder sql = new StringBuilder("Select");
		sql.append(" FinID, FinReference, FinODSchdDate, FinODFor, FinBranch, FinType, CustID, FinODTillDate");
		sql.append(", FinCurODAmt, FinCurODPri, FinCurODPft, FinMaxODAmt, FinMaxODPri, FinMaxODPft");
		sql.append(", GraceDays, IncGraceDays, FinCurODDays, TotPenaltyAmt, TotWaived, TotPenaltyPaid");
		sql.append(", TotPenaltyBal, LPIAmt, LPIPaid, LPIBal, LPIWaived, ApplyODPenalty, ODIncGrcDays");
		sql.append(", ODChargeType, ODGraceDays, ODChargeCalOn, ODChargeAmtOrPerc, ODAllowWaiver, ODMaxWaiverPerc");
		sql.append(", FinLMdfDate, ODRuleCode, LpCpz, LpCpzAmount, LpCurCpzBal");
		sql.append(", PresentmentId, CurOverdraftTxnChrg, MaxOverdraftTxnChrg");
		sql.append(", LppDueAmt, LppDueTillDate, LpiDueAmt, LpiDueTillDate, ODMinAmount, PayableAmount");
		sql.append(" From FinODDetails");
		sql.append(" Where FinID = ?");

		if (odSchdDate != null) {
			sql.append(" and FinODSchdDate >= ?");
		}

		logger.debug(Literal.SQL + sql.toString());

		List<FinODDetails> finODDetails = this.jdbcOperations.query(sql.toString(), ps -> {
			int index = 1;
			ps.setLong(index++, finID);
			if (odSchdDate != null) {
				ps.setDate(index, JdbcUtil.getDate(odSchdDate));
			}
		}, (rs, rowNum) -> {
			FinODDetails odd = new FinODDetails();

			odd.setFinID(rs.getLong("FinID"));
			odd.setFinReference(rs.getString("FinReference"));
			odd.setFinODSchdDate(rs.getTimestamp("FinODSchdDate"));
			odd.setFinODFor(rs.getString("FinODFor"));
			odd.setFinBranch(rs.getString("FinBranch"));
			odd.setFinType(rs.getString("FinType"));
			odd.setCustID(rs.getLong("CustID"));
			odd.setFinODTillDate(rs.getTimestamp("FinODTillDate"));
			odd.setFinCurODAmt(rs.getBigDecimal("FinCurODAmt"));
			odd.setFinCurODPri(rs.getBigDecimal("FinCurODPri"));
			odd.setFinCurODPft(rs.getBigDecimal("FinCurODPft"));
			odd.setFinMaxODAmt(rs.getBigDecimal("FinMaxODAmt"));
			odd.setFinMaxODPri(rs.getBigDecimal("FinMaxODPri"));
			odd.setFinMaxODPft(rs.getBigDecimal("FinMaxODPft"));
			odd.setGraceDays(rs.getInt("GraceDays"));
			odd.setIncGraceDays(rs.getBoolean("IncGraceDays"));
			odd.setFinCurODDays(rs.getInt("FinCurODDays"));
			odd.setTotPenaltyAmt(rs.getBigDecimal("TotPenaltyAmt"));
			odd.setTotWaived(rs.getBigDecimal("TotWaived"));
			odd.setTotPenaltyPaid(rs.getBigDecimal("TotPenaltyPaid"));
			odd.setTotPenaltyBal(rs.getBigDecimal("TotPenaltyBal"));
			odd.setLPIAmt(rs.getBigDecimal("LPIAmt"));
			odd.setLPIPaid(rs.getBigDecimal("LPIPaid"));
			odd.setLPIBal(rs.getBigDecimal("LPIBal"));
			odd.setLPIWaived(rs.getBigDecimal("LPIWaived"));
			odd.setApplyODPenalty(rs.getBoolean("ApplyODPenalty"));
			odd.setODIncGrcDays(rs.getBoolean("ODIncGrcDays"));
			odd.setODChargeType(rs.getString("ODChargeType"));
			odd.setODGraceDays(rs.getInt("ODGraceDays"));
			odd.setODChargeCalOn(rs.getString("ODChargeCalOn"));
			odd.setODChargeAmtOrPerc(rs.getBigDecimal("ODChargeAmtOrPerc"));
			odd.setODAllowWaiver(rs.getBoolean("ODAllowWaiver"));
			odd.setODMaxWaiverPerc(rs.getBigDecimal("ODMaxWaiverPerc"));
			odd.setFinLMdfDate(rs.getTimestamp("FinLMdfDate"));
			odd.setODRuleCode(rs.getString("ODRuleCode"));
			odd.setLpCpz(rs.getBoolean("LpCpz"));
			odd.setLpCpzAmount(rs.getBigDecimal("LpCpzAmount"));
			odd.setLpCurCpzBal(rs.getBigDecimal("LpCurCpzBal"));
			odd.setPresentmentID(rs.getLong("PresentmentId"));
			odd.setCurOverdraftTxnChrg(rs.getBigDecimal("CurOverdraftTxnChrg"));
			odd.setMaxOverdraftTxnChrg(rs.getBigDecimal("MaxOverdraftTxnChrg"));
			odd.setLppDueAmt(rs.getBigDecimal("LppDueAmt"));
			odd.setLppDueTillDate(rs.getDate("LppDueTillDate"));
			odd.setLpiDueAmt(rs.getBigDecimal("LpiDueAmt"));
			odd.setLpiDueTillDate(rs.getDate("LpiDueTillDate"));
			odd.setOdMinAmount(rs.getBigDecimal("ODMinAmount"));
			odd.setPayableAmount(rs.getBigDecimal("PayableAmount"));

			return odd;
		});

		return sort(finODDetails);
	}

	@Override
	public int getMaxODDaysOnDeferSchd(long finID, List<Date> pastSchDates) {
		StringBuilder sql = new StringBuilder("Select coalesce(max(FinCurODDays), 0) From FinODDetails");
		sql.append(" Where FinID = ?");

		if (pastSchDates != null) {
			sql.append(" and FinOdSchdDate in (");
			sql.append(JdbcUtil.getInCondition(pastSchDates));
			sql.append(")");
		} else {
			sql.append(" and FinCurODAmt > ?");
		}

		Object[] objects = null;

		if (pastSchDates != null) {
			objects = new Object[pastSchDates.size() + 1];
		} else {
			objects = new Object[2];
		}

		objects[0] = finID;

		if (pastSchDates != null) {
			int i = 1;
			for (Date pastSchDate : pastSchDates) {
				objects[i++] = pastSchDate;
			}

		} else {
			objects[1] = BigDecimal.ZERO;
		}

		logger.debug(Literal.SQL + sql.toString());

		return this.jdbcOperations.queryForObject(sql.toString(), Integer.class, objects);
	}

	@Override
	public FinODDetails getMaxDaysFinODDetails(long finID) {
		StringBuilder sql = new StringBuilder("Select");
		sql.append(" T1.FinODSchdDate, T1.FinODFor");
		sql.append(" From FinODDetails T1");
		sql.append(" Inner Join (Select FinID, max(FinCurODDays) MaxODDays From FinODDetails");
		sql.append(" Where FinID = ? and FinCurODAmt > 0 group by FinID) T2 ");
		sql.append(" ON T1.FinID = T2.FinID and T1.FinCurODDays = T2.MaxODDays and T1.FinCurODAmt > 0");

		logger.debug(Literal.SQL + sql.toString());

		try {
			return this.jdbcOperations.queryForObject(sql.toString(), (rs, rowNum) -> {
				FinODDetails od = new FinODDetails();

				od.setFinODSchdDate(rs.getDate("FinODSchdDate"));
				od.setFinODFor(rs.getString("FinODFor"));

				return od;

			}, finID);
		} catch (EmptyResultDataAccessException e) {
			logger.warn(Message.NO_RECORD_FOUND);
			return null;
		}
	}

	@Override
	public void updatePenaltyTotals(FinODDetails od) {
		StringBuilder sql = new StringBuilder("Update FinODDetails");
		sql.append(" Set TotPenaltyAmt = ?, TotWaived = ?, TotPenaltyPaid = ?, TotPenaltyBal = ?");
		sql.append(", LPIAmt = ?, LPIPaid = ?, LPIBal = ?, LPIWaived = ?, LpCpz = ?, LpCpzAmount = ?, LpCurCpzBal = ?");
		sql.append(" Where FinID = ? and FinODSchdDate = ?");

		logger.debug(Literal.SQL + sql.toString());

		this.jdbcOperations.update(sql.toString(), ps -> {
			int index = 1;

			ps.setBigDecimal(index++, od.getTotPenaltyAmt());
			ps.setBigDecimal(index++, od.getTotWaived());
			ps.setBigDecimal(index++, od.getTotPenaltyPaid());
			ps.setBigDecimal(index++, od.getTotPenaltyBal());
			ps.setBigDecimal(index++, od.getLPIAmt());
			ps.setBigDecimal(index++, od.getLPIPaid());
			ps.setBigDecimal(index++, od.getLPIBal());
			ps.setBigDecimal(index++, od.getLPIWaived());
			ps.setBoolean(index++, od.isLpCpz());
			ps.setBigDecimal(index++, od.getLpCpzAmount());
			ps.setBigDecimal(index++, od.getLpCurCpzBal());

			ps.setLong(index++, od.getFinID());
			ps.setDate(index, JdbcUtil.getDate(od.getFinODSchdDate()));
		});

	}

	// FIXME: PV 09AUG19. Add fields related to LpCurCpzBal
	@Override
	public void updateLatePftTotals(long finID, Date finODSchdDate, BigDecimal paidNow, BigDecimal waivedNow) {
		List<FinODDetails> list = new ArrayList<>();
		FinODDetails od = new FinODDetails();

		od.setFinID(finID);
		od.setFinODSchdDate(finODSchdDate);
		od.setPaidNow(paidNow);
		od.setWaivedNow(waivedNow);

		list.add(od);

		updateLatePftTotals(list);

	}

	@Override
	public void updateLatePftTotals(List<FinODDetails> list) {
		StringBuilder sql = new StringBuilder("Update FinODDetails");
		sql.append(" Set LPIPaid = LPIPaid + ?, LPIBal = LPIBal - (? - ?), LPIWaived = LPIWaived + ?");
		sql.append(" Where FinID = ? and FinODSchdDate = ?");

		logger.debug(Literal.SQL + sql.toString());

		this.jdbcOperations.batchUpdate(sql.toString(), new BatchPreparedStatementSetter() {

			@Override
			public void setValues(PreparedStatement ps, int i) throws SQLException {
				FinODDetails od = list.get(i);
				int index = 1;
				ps.setBigDecimal(index++, od.getPaidNow());
				ps.setBigDecimal(index++, od.getPaidNow());
				ps.setBigDecimal(index++, od.getWaivedNow());
				ps.setBigDecimal(index++, od.getWaivedNow());

				ps.setLong(index++, od.getFinID());
				ps.setDate(index, JdbcUtil.getDate(od.getFinODSchdDate()));
			}

			@Override
			public int getBatchSize() {
				return list.size();
			}
		});
	}

	// FIXME: PV 09AUG19. Add fields related to LpCurCpzBal
	@Override
	public void updateReversals(long finID, Date odSchDate, BigDecimal penaltyPaid, BigDecimal latePftPaid) {
		StringBuilder sql = new StringBuilder("Update FinODDetails");
		sql.append(" Set TotPenaltyPaid = TotPenaltyPaid - ?, TotPenaltyBal = TotPenaltyBal + ?");
		sql.append(", LPIPaid = LPIPaid - ?, LPIBal = LPIBal + ?");
		sql.append(" Where FinID = ? and FinODSchdDate = ?");

		this.jdbcOperations.update(sql.toString(), ps -> {
			int index = 1;

			ps.setBigDecimal(index++, penaltyPaid);
			ps.setBigDecimal(index++, penaltyPaid);
			ps.setBigDecimal(index++, latePftPaid);
			ps.setBigDecimal(index++, latePftPaid);

			ps.setLong(index++, finID);
			ps.setDate(index, JdbcUtil.getDate(odSchDate));
		});
	}

	@Override
	public FinODDetails getTotals(long finID) {
		StringBuilder sql = new StringBuilder("Select");
		sql.append(" coalesce(sum(TotPenaltyAmt), 0) TotPenaltyAmt");
		sql.append(", coalesce(sum(TotPenaltyPaid), 0) TotPenaltyPaid");
		sql.append(", coalesce(Sum(TotWaived), 0) TotWaived");
		sql.append(", coalesce(sum(LPIAmt), 0) LPIAmt");
		sql.append(", coalesce(sum(LPIPaid), 0) LPIPaid");
		sql.append(", coalesce(sum(LPIWaived), 0) LPIWaived ");
		sql.append(" From FInODDetails Where FinID = ?");

		logger.debug(Literal.SQL + sql.toString());

		return this.jdbcOperations.queryForObject(sql.toString(), (rs, rowNum) -> {
			FinODDetails od = new FinODDetails();

			od.setTotPenaltyAmt(rs.getBigDecimal("TotPenaltyAmt"));
			od.setTotPenaltyPaid(rs.getBigDecimal("TotPenaltyPaid"));
			od.setTotWaived(rs.getBigDecimal("TotWaived"));
			od.setLPIAmt(rs.getBigDecimal("LPIAmt"));
			od.setLPIPaid(rs.getBigDecimal("LPIPaid"));
			od.setLPIWaived(rs.getBigDecimal("LPIWaived"));

			return od;

		}, finID);
	}

	@Override
	public BigDecimal getTotalPenaltyBal(long finID, List<Date> presentmentDates) {
		StringBuilder sql = new StringBuilder(" Select coalesce(sum(TotPenaltyBal), 0) TotPenaltyAmt");
		sql.append(" From FinODDetails Where FinID = ?");
		if (presentmentDates != null && !presentmentDates.isEmpty()) {
			sql.append(" and FinODSchdDate not in(");
			sql.append(JdbcUtil.getInCondition(presentmentDates));
			sql.append(")");
		}

		Object[] parameters = null;

		if (presentmentDates != null && !presentmentDates.isEmpty()) {
			parameters = new Object[1 + presentmentDates.size()];

			parameters[0] = finID;

			int i = 1;

			for (Date date : presentmentDates) {
				parameters[i++] = date;
			}

		} else {
			parameters = new Object[] { finID };
		}

		logger.debug(Literal.SQL + sql.toString());

		return this.jdbcOperations.queryForObject(sql.toString(), BigDecimal.class, parameters);
	}

	@Override
	public List<FinODDetails> getFinODBalByFinRef(long finID) {
		StringBuilder sql = new StringBuilder("Select");
		sql.append(" FinID, FinReference, FinODSchdDate, FinODFor, FinBranch, FinType, CustID, FinODTillDate");
		sql.append(", FinCurODAmt, FinCurODPri, FinCurODPft, FinMaxODAmt, FinMaxODPri, FinMaxODPft");
		sql.append(", GraceDays, IncGraceDays, FinCurODDays, TotPenaltyAmt, TotWaived, TotPenaltyPaid");
		sql.append(", TotPenaltyBal, LPIAmt, LPIPaid, LPIBal, LPIWaived, ApplyODPenalty, ODIncGrcDays");
		sql.append(", ODChargeType, ODGraceDays, ODChargeCalOn, ODChargeAmtOrPerc, ODAllowWaiver, ODMaxWaiverPerc");
		sql.append(", FinLMdfDate, ODRuleCode, LpCpz, LpCpzAmount, LpCurCpzBal, LockODRecalCal, CurOverdraftTxnChrg");
		sql.append(
				", MaxOverdraftTxnChrg, LppDueAmt, LppDueTillDate, LpiDueAmt, LpiDueTillDate, ODMinAmount, PayableAmount");
		sql.append(" From FinODDetails");
		sql.append(" Where FinID = ?");

		logger.debug(Literal.SQL + sql.toString());

		List<FinODDetails> odList = this.jdbcOperations.query(sql.toString(), ps -> {
			int index = 1;
			ps.setLong(index, finID);
		}, (rs, rowNum) -> {
			FinODDetails fod = new FinODDetails();

			fod.setFinID(rs.getLong("FinID"));
			fod.setFinReference(rs.getString("FinReference"));
			fod.setFinODSchdDate(rs.getTimestamp("FinODSchdDate"));
			fod.setFinODFor(rs.getString("FinODFor"));
			fod.setFinBranch(rs.getString("FinBranch"));
			fod.setFinType(rs.getString("FinType"));
			fod.setCustID(rs.getLong("CustID"));
			fod.setFinODTillDate(rs.getTimestamp("FinODTillDate"));
			fod.setFinCurODAmt(rs.getBigDecimal("FinCurODAmt"));
			fod.setFinCurODPri(rs.getBigDecimal("FinCurODPri"));
			fod.setFinCurODPft(rs.getBigDecimal("FinCurODPft"));
			fod.setFinMaxODAmt(rs.getBigDecimal("FinMaxODAmt"));
			fod.setFinMaxODPri(rs.getBigDecimal("FinMaxODPri"));
			fod.setFinMaxODPft(rs.getBigDecimal("FinMaxODPft"));
			fod.setGraceDays(rs.getInt("GraceDays"));
			fod.setIncGraceDays(rs.getBoolean("IncGraceDays"));
			fod.setFinCurODDays(rs.getInt("FinCurODDays"));
			fod.setTotPenaltyAmt(rs.getBigDecimal("TotPenaltyAmt"));
			fod.setTotWaived(rs.getBigDecimal("TotWaived"));
			fod.setTotPenaltyPaid(rs.getBigDecimal("TotPenaltyPaid"));
			fod.setTotPenaltyBal(rs.getBigDecimal("TotPenaltyBal"));
			fod.setLPIAmt(rs.getBigDecimal("LPIAmt"));
			fod.setLPIPaid(rs.getBigDecimal("LPIPaid"));
			fod.setLPIBal(rs.getBigDecimal("LPIBal"));
			fod.setLPIWaived(rs.getBigDecimal("LPIWaived"));
			fod.setApplyODPenalty(rs.getBoolean("ApplyODPenalty"));
			fod.setODIncGrcDays(rs.getBoolean("ODIncGrcDays"));
			fod.setODChargeType(rs.getString("ODChargeType"));
			fod.setODGraceDays(rs.getInt("ODGraceDays"));
			fod.setODChargeCalOn(rs.getString("ODChargeCalOn"));
			fod.setODChargeAmtOrPerc(rs.getBigDecimal("ODChargeAmtOrPerc"));
			fod.setODAllowWaiver(rs.getBoolean("ODAllowWaiver"));
			fod.setODMaxWaiverPerc(rs.getBigDecimal("ODMaxWaiverPerc"));
			fod.setFinLMdfDate(rs.getTimestamp("FinLMdfDate"));
			fod.setODRuleCode(rs.getString("ODRuleCode"));
			fod.setLpCpz(rs.getBoolean("LpCpz"));
			fod.setLpCpzAmount(rs.getBigDecimal("LpCpzAmount"));
			fod.setLpCurCpzBal(rs.getBigDecimal("LpCurCpzBal"));
			fod.setLockODRecalCal(rs.getBoolean("LockODRecalCal"));
			fod.setCurOverdraftTxnChrg(rs.getBigDecimal("CurOverdraftTxnChrg"));
			fod.setMaxOverdraftTxnChrg(rs.getBigDecimal("MaxOverdraftTxnChrg"));
			fod.setLppDueAmt(rs.getBigDecimal("LppDueAmt"));
			fod.setLppDueTillDate(rs.getTimestamp("LppDueTillDate"));
			fod.setLpiDueAmt(rs.getBigDecimal("LpiDueAmt"));
			fod.setLpiDueTillDate(rs.getTimestamp("LpiDueTillDate"));
			fod.setOdMinAmount(rs.getBigDecimal("ODMinAmount"));
			fod.setPayableAmount(rs.getBigDecimal("PayableAmount"));

			return fod;
		});

		return odList.stream().sorted((od1, od2) -> od1.getFinODSchdDate().compareTo(od2.getFinODSchdDate()))
				.collect(Collectors.toList());
	}

	@Override
	public int saveList(List<FinODDetails> odList) {
		StringBuilder sql = new StringBuilder("Insert into");
		sql.append(" FinODDetails");
		sql.append(" (FinID, FinReference, FinODSchdDate, FinODFor, FinBranch, FinType, CustID, FinODTillDate");
		sql.append(", FinCurODAmt, FinCurODPri, FinCurODPft, FinMaxODAmt, FinMaxODPri, FinMaxODPft, GraceDays");
		sql.append(", IncGraceDays, FinCurODDays, TotPenaltyAmt, TotWaived, TotPenaltyPaid, TotPenaltyBal");
		sql.append(", FinLMdfDate, LPIAmt, LPIPaid, LPIBal, LPIWaived, ApplyODPenalty, ODIncGrcDays, ODChargeType");
		sql.append(", ODGraceDays, LpCpz, LpCpzAmount, LpCurCpzBal, ODChargeCalOn, ODChargeAmtOrPerc");
		sql.append(", ODAllowWaiver, ODMaxWaiverPerc, ODRuleCode, CurOverdraftTxnChrg, MaxOverdraftTxnChrg");
		sql.append(", LppDueAmt, LppDueTillDate, LpiDueAmt, LpiDueTillDate, ODMinAmount, PayableAmount");
		sql.append(") values(");
		sql.append("?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?");
		sql.append(", ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?");
		sql.append(")");

		logger.debug(Literal.SQL.concat(sql.toString()));

		return jdbcOperations.batchUpdate(sql.toString(), new BatchPreparedStatementSetter() {

			@Override
			public void setValues(PreparedStatement ps, int i) throws SQLException {
				FinODDetails odd = odList.get(i);
				int index = 0;

				ps.setLong(++index, odd.getFinID());
				ps.setString(++index, odd.getFinReference());
				ps.setDate(++index, JdbcUtil.getDate(odd.getFinODSchdDate()));
				ps.setString(++index, odd.getFinODFor());
				ps.setString(++index, odd.getFinBranch());
				ps.setString(++index, odd.getFinType());
				ps.setLong(++index, odd.getCustID());
				ps.setDate(++index, JdbcUtil.getDate(odd.getFinODTillDate()));
				ps.setBigDecimal(++index, odd.getFinCurODAmt());
				ps.setBigDecimal(++index, odd.getFinCurODPri());
				ps.setBigDecimal(++index, odd.getFinCurODPft());
				ps.setBigDecimal(++index, odd.getFinMaxODAmt());
				ps.setBigDecimal(++index, odd.getFinMaxODPri());
				ps.setBigDecimal(++index, odd.getFinMaxODPft());
				ps.setInt(++index, odd.getGraceDays());
				ps.setBoolean(++index, odd.isIncGraceDays());
				ps.setInt(++index, odd.getFinCurODDays());
				ps.setBigDecimal(++index, odd.getTotPenaltyAmt());
				ps.setBigDecimal(++index, odd.getTotWaived());
				ps.setBigDecimal(++index, odd.getTotPenaltyPaid());
				ps.setBigDecimal(++index, odd.getTotPenaltyBal());
				ps.setDate(++index, JdbcUtil.getDate(odd.getFinLMdfDate()));
				ps.setBigDecimal(++index, odd.getLPIAmt());
				ps.setBigDecimal(++index, odd.getLPIPaid());
				ps.setBigDecimal(++index, odd.getLPIBal());
				ps.setBigDecimal(++index, odd.getLPIWaived());
				ps.setBoolean(++index, odd.isApplyODPenalty());
				ps.setBoolean(++index, odd.isODIncGrcDays());
				ps.setString(++index, odd.getODChargeType());
				ps.setInt(++index, odd.getODGraceDays());
				ps.setBoolean(++index, odd.isLpCpz());
				ps.setBigDecimal(++index, odd.getLpCpzAmount());
				ps.setBigDecimal(++index, odd.getLpCurCpzBal());
				ps.setString(++index, odd.getODChargeCalOn());
				ps.setBigDecimal(++index, odd.getODChargeAmtOrPerc());
				ps.setBoolean(++index, odd.isODAllowWaiver());
				ps.setBigDecimal(++index, odd.getODMaxWaiverPerc());
				ps.setString(++index, odd.getODRuleCode());
				ps.setBigDecimal(++index, odd.getCurOverdraftTxnChrg());
				ps.setBigDecimal(++index, odd.getMaxOverdraftTxnChrg());
				ps.setBigDecimal(++index, odd.getLppDueAmt());
				ps.setDate(++index, JdbcUtil.getDate(odd.getLppDueTillDate()));
				ps.setBigDecimal(++index, odd.getLpiDueAmt());
				ps.setDate(++index, JdbcUtil.getDate(odd.getLpiDueTillDate()));
				ps.setBigDecimal(++index, odd.getOdMinAmount());
				ps.setBigDecimal(++index, odd.getPayableAmount());
			}

			@Override
			public int getBatchSize() {
				return odList.size();
			}
		}).length;
	}

	@Override
	public List<FinODDetails> getFinODPenalityByFinRef(long finID, boolean ispft, boolean isrender) {
		StringBuilder sql = new StringBuilder("Select");
		sql.append(" FinID, FinReference, FinODSchdDate, FinODFor, FinBranch, FinType, CustID, FinODTillDate");
		sql.append(", FinCurODAmt, FinCurODPri, FinCurODPft, FinMaxODAmt, FinMaxODPri, FinMaxODPft");
		sql.append(", GraceDays, IncGraceDays, FinCurODDays, TotPenaltyAmt, TotWaived, TotPenaltyPaid");
		sql.append(", TotPenaltyBal, LPIAmt, LPIPaid, LPIBal, LPIWaived, ApplyODPenalty, ODIncGrcDays");
		sql.append(", ODChargeType, ODGraceDays, ODChargeCalOn, ODChargeAmtOrPerc, ODAllowWaiver, ODMaxWaiverPerc");
		sql.append(", LpCpz, LpCpzAmount, LpCurCpzBal, FinLMdfDate, ODRuleCode");
		sql.append(", CurOverdraftTxnChrg, MaxOverdraftTxnChrg, ODMinAmount, PayableAmount");
		sql.append(" From FinODDetails");
		sql.append(" Where FinID = ?");

		if (!isrender) {
			if (ispft) {
				sql.append(" and LPIBal > ?");
			} else {
				sql.append(" and TotPenaltyBal > ?");
			}
			sql.append(" Order by FinODSchdDate");
		}

		logger.debug(Literal.SQL + sql.toString());

		return this.jdbcOperations.query(sql.toString(), ps -> {
			int index = 1;
			ps.setLong(index++, finID);
			if (!isrender) {
				if (ispft) {
					ps.setBigDecimal(index, BigDecimal.ZERO);
				} else {
					ps.setBigDecimal(index, BigDecimal.ZERO);
				}
			}
		}, (rs, rowNum) -> {
			FinODDetails fod = new FinODDetails();

			fod.setFinID(rs.getLong("FinID"));
			fod.setFinReference(rs.getString("FinReference"));
			fod.setFinODSchdDate(rs.getTimestamp("FinODSchdDate"));
			fod.setFinODFor(rs.getString("FinODFor"));
			fod.setFinBranch(rs.getString("FinBranch"));
			fod.setFinType(rs.getString("FinType"));
			fod.setCustID(rs.getLong("CustID"));
			fod.setFinODTillDate(rs.getTimestamp("FinODTillDate"));
			fod.setFinCurODAmt(rs.getBigDecimal("FinCurODAmt"));
			fod.setFinCurODPri(rs.getBigDecimal("FinCurODPri"));
			fod.setFinCurODPft(rs.getBigDecimal("FinCurODPft"));
			fod.setFinMaxODAmt(rs.getBigDecimal("FinMaxODAmt"));
			fod.setFinMaxODPri(rs.getBigDecimal("FinMaxODPri"));
			fod.setFinMaxODPft(rs.getBigDecimal("FinMaxODPft"));
			fod.setGraceDays(rs.getInt("GraceDays"));
			fod.setIncGraceDays(rs.getBoolean("IncGraceDays"));
			fod.setFinCurODDays(rs.getInt("FinCurODDays"));
			fod.setTotPenaltyAmt(rs.getBigDecimal("TotPenaltyAmt"));
			fod.setTotWaived(rs.getBigDecimal("TotWaived"));
			fod.setTotPenaltyPaid(rs.getBigDecimal("TotPenaltyPaid"));
			fod.setTotPenaltyBal(rs.getBigDecimal("TotPenaltyBal"));
			fod.setLPIAmt(rs.getBigDecimal("LPIAmt"));
			fod.setLPIPaid(rs.getBigDecimal("LPIPaid"));
			fod.setLPIBal(rs.getBigDecimal("LPIBal"));
			fod.setLPIWaived(rs.getBigDecimal("LPIWaived"));
			fod.setApplyODPenalty(rs.getBoolean("ApplyODPenalty"));
			fod.setODIncGrcDays(rs.getBoolean("ODIncGrcDays"));
			fod.setODChargeType(rs.getString("ODChargeType"));
			fod.setODGraceDays(rs.getInt("ODGraceDays"));
			fod.setODChargeCalOn(rs.getString("ODChargeCalOn"));
			fod.setODChargeAmtOrPerc(rs.getBigDecimal("ODChargeAmtOrPerc"));
			fod.setODAllowWaiver(rs.getBoolean("ODAllowWaiver"));
			fod.setODMaxWaiverPerc(rs.getBigDecimal("ODMaxWaiverPerc"));
			fod.setLpCpz(rs.getBoolean("LpCpz"));
			fod.setLpCpzAmount(rs.getBigDecimal("LpCpzAmount"));
			fod.setLpCurCpzBal(rs.getBigDecimal("LpCurCpzBal"));
			fod.setFinLMdfDate(rs.getTimestamp("FinLMdfDate"));
			fod.setODRuleCode(rs.getString("ODRuleCode"));
			fod.setCurOverdraftTxnChrg(rs.getBigDecimal("CurOverdraftTxnChrg"));
			fod.setMaxOverdraftTxnChrg(rs.getBigDecimal("MaxOverdraftTxnChrg"));
			fod.setOdMinAmount(rs.getBigDecimal("ODMinAmount"));
			fod.setPayableAmount(rs.getBigDecimal("PayableAmount"));

			return fod;
		});

	}

	@Override
	public void updateWaiverAmount(long finID, Date odDate, BigDecimal waivedAmount, BigDecimal penAmount) {
		StringBuilder sql = new StringBuilder("Update FinODDetails");
		sql.append(" Set TotWaived= TotWaived - ?, TotPenaltyBal= TotPenaltyBal + ?");
		sql.append(" Where FinID = ? and FinODSchdDate = ?");

		logger.debug(Literal.SQL + sql.toString());

		this.jdbcOperations.update(sql.toString(), ps -> {
			int index = 1;

			ps.setBigDecimal(index++, waivedAmount);
			ps.setBigDecimal(index++, penAmount.add(waivedAmount));

			ps.setLong(index++, finID);
			ps.setDate(index, JdbcUtil.getDate(odDate));
		});
	}

	@Override
	public List<FinODDetails> getCustomerDues(long custId) {
		String sql = "Select FinID, sum(TotPenaltyBal) TotPenaltyBal From FINODDETAILS Where CustID = ? group by FinID";

		logger.debug(Literal.SQL + sql);

		MapSqlParameterSource source = new MapSqlParameterSource();
		source.addValue("CustID", custId);

		return this.jdbcOperations.query(sql, (rs, rowNum) -> {
			FinODDetails od = new FinODDetails();

			od.setFinID(rs.getLong("FinID"));
			od.setFinID(rs.getLong("TotPenaltyBal"));

			return od;

		}, custId);
	}

	@Override
	public FinODDetails getFinODByFinRef(long finID, Date odSchdDate) {
		StringBuilder sql = new StringBuilder("Select FinID, FinReference, FinODSchdDate, FinODFor, FinBranch");
		sql.append(", FinType, CustID, FinODTillDate, FinCurODAmt, FinCurODPri, FinCurODPft, FinMaxODAmt");
		sql.append(", FinMaxODPri, FinMaxODPft, GraceDays, IncGraceDays, FinCurODDays");
		sql.append(", TotPenaltyAmt, TotWaived, TotPenaltyPaid, TotPenaltyBal");
		sql.append(", LPIAmt, LPIPaid, LPIBal, LPIWaived, ApplyODPenalty, ODIncGrcDays, ODChargeType");
		sql.append(", ODGraceDays, ODChargeCalOn, ODChargeAmtOrPerc, ODAllowWaiver, ODMaxWaiverPerc");
		sql.append(", FinLMdfDate, ODRuleCode, CurOverdraftTxnChrg, MaxOverdraftTxnChrg, ODMinAmount, PayableAmount");
		sql.append(" From FinODDetails");
		sql.append(" Where FinID = ?");

		if (odSchdDate != null) {
			sql.append(" and FinODSchdDate = ?");
		}

		Object[] parameters = null;

		if (odSchdDate != null) {
			parameters = new Object[] { finID, odSchdDate };
		} else {
			parameters = new Object[] { finID };
		}

		logger.debug(Literal.SQL + sql.toString());

		try {
			return this.jdbcOperations.queryForObject(sql.toString(), (rs, rowNum) -> {
				FinODDetails od = new FinODDetails();

				od.setFinID(rs.getLong("FinID"));
				od.setFinReference(rs.getString("FinReference"));
				od.setFinODSchdDate(rs.getDate("FinODSchdDate"));
				od.setFinODFor(rs.getString("FinODFor"));
				od.setFinBranch(rs.getString("FinBranch"));
				od.setFinType(rs.getString("FinType"));
				od.setCustID(rs.getLong("CustID"));
				od.setFinODTillDate(rs.getDate("FinODTillDate"));
				od.setFinCurODAmt(rs.getBigDecimal("FinCurODAmt"));
				od.setFinCurODPri(rs.getBigDecimal("FinCurODPri"));
				od.setFinCurODPft(rs.getBigDecimal("FinCurODPft"));
				od.setFinMaxODAmt(rs.getBigDecimal("FinMaxODAmt"));
				od.setFinMaxODPri(rs.getBigDecimal("FinMaxODPri"));
				od.setFinMaxODPft(rs.getBigDecimal("FinMaxODPft"));
				od.setGraceDays(rs.getInt("GraceDays"));
				od.setIncGraceDays(rs.getBoolean("IncGraceDays"));
				od.setFinCurODDays(rs.getInt("FinCurODDays"));
				od.setTotPenaltyAmt(rs.getBigDecimal("TotPenaltyAmt"));
				od.setTotWaived(rs.getBigDecimal("TotWaived"));
				od.setTotPenaltyPaid(rs.getBigDecimal("TotPenaltyPaid"));
				od.setTotPenaltyBal(rs.getBigDecimal("TotPenaltyBal"));
				od.setLPIAmt(rs.getBigDecimal("LPIAmt"));
				od.setLPIPaid(rs.getBigDecimal("LPIPaid"));
				od.setLPIBal(rs.getBigDecimal("LPIBal"));
				od.setLPIWaived(rs.getBigDecimal("LPIWaived"));
				od.setApplyODPenalty(rs.getBoolean("ApplyODPenalty"));
				od.setODIncGrcDays(rs.getBoolean("ODIncGrcDays"));
				od.setODChargeType(rs.getString("ODChargeType"));
				od.setODGraceDays(rs.getInt("ODGraceDays"));
				od.setODChargeCalOn(rs.getString("ODChargeCalOn"));
				od.setODChargeAmtOrPerc(rs.getBigDecimal("ODChargeAmtOrPerc"));
				od.setODAllowWaiver(rs.getBoolean("ODAllowWaiver"));
				od.setODMaxWaiverPerc(rs.getBigDecimal("ODMaxWaiverPerc"));
				od.setFinLMdfDate(rs.getDate("FinLMdfDate"));
				od.setODRuleCode(rs.getString("ODRuleCode"));
				od.setCurOverdraftTxnChrg(rs.getBigDecimal("CurOverdraftTxnChrg"));
				od.setMaxOverdraftTxnChrg(rs.getBigDecimal("MaxOverdraftTxnChrg"));
				od.setOdMinAmount(rs.getBigDecimal("ODMinAmount"));
				od.setPayableAmount(rs.getBigDecimal("PayableAmount"));

				return od;
			}, parameters);
		} catch (EmptyResultDataAccessException e) {
			logger.warn(Message.NO_RECORD_FOUND);
			return null;
		}
	}

	public static List<FinODDetails> sort(List<FinODDetails> odDetails) {
		if (odDetails != null && odDetails.size() > 0) {
			Collections.sort(odDetails, new Comparator<FinODDetails>() {
				@Override
				public int compare(FinODDetails detail1, FinODDetails detail2) {
					return DateUtil.compare(detail1.getFinODSchdDate(), detail2.getFinODSchdDate());
				}
			});
		}

		return odDetails;
	}

	@Override
	public List<FinODDetails> getFinODDetailsByFinRef(long finID) {
		StringBuilder sql = new StringBuilder("Select");
		sql.append(" FinReference, FinODSchdDate, FinODFor, FinBranch, FinType, CustID, FinODTillDate");
		sql.append(", FinCurODAmt, FinCurODPri, FinCurODPft, FinMaxODAmt, FinMaxODPri, FinMaxODPft");
		sql.append(", GraceDays, IncGraceDays, FinCurODDays, TotPenaltyAmt, TotWaived, TotPenaltyPaid");
		sql.append(", TotPenaltyBal, LPIAmt, LPIPaid, LPIBal, LPIWaived, ApplyODPenalty, ODIncGrcDays");
		sql.append(", ODChargeType, ODGraceDays, ODChargeCalOn, ODChargeAmtOrPerc, ODAllowWaiver, ODMaxWaiverPerc");
		sql.append(", LpCpz, LpCpzAmount, LpCurCpzBal, FinLMdfDate, ODRuleCode");
		sql.append(", PresentmentId, CurOverdraftTxnChrg, MaxOverdraftTxnChrg, ODMinAmount, PayableAmount");
		sql.append(" from FinODDetails");
		sql.append(" Where FinID = ?");

		logger.debug(Literal.SQL + sql.toString());

		return sort(this.jdbcOperations.query(sql.toString(), ps -> ps.setLong(1, finID), (rs, rowNum) -> {
			FinODDetails od = new FinODDetails();

			od.setFinReference(rs.getString("FinReference"));
			od.setFinODSchdDate(rs.getTimestamp("FinODSchdDate"));
			od.setFinODFor(rs.getString("FinODFor"));
			od.setFinBranch(rs.getString("FinBranch"));
			od.setFinType(rs.getString("FinType"));
			od.setCustID(rs.getLong("CustID"));
			od.setFinODTillDate(rs.getTimestamp("FinODTillDate"));
			od.setFinCurODAmt(rs.getBigDecimal("FinCurODAmt"));
			od.setFinCurODPri(rs.getBigDecimal("FinCurODPri"));
			od.setFinCurODPft(rs.getBigDecimal("FinCurODPft"));
			od.setFinMaxODAmt(rs.getBigDecimal("FinMaxODAmt"));
			od.setFinMaxODPri(rs.getBigDecimal("FinMaxODPri"));
			od.setFinMaxODPft(rs.getBigDecimal("FinMaxODPft"));
			od.setGraceDays(rs.getInt("GraceDays"));
			od.setIncGraceDays(rs.getBoolean("IncGraceDays"));
			od.setFinCurODDays(rs.getInt("FinCurODDays"));
			od.setTotPenaltyAmt(rs.getBigDecimal("TotPenaltyAmt"));
			od.setTotWaived(rs.getBigDecimal("TotWaived"));
			od.setTotPenaltyPaid(rs.getBigDecimal("TotPenaltyPaid"));
			od.setTotPenaltyBal(rs.getBigDecimal("TotPenaltyBal"));
			od.setLPIAmt(rs.getBigDecimal("LPIAmt"));
			od.setLPIPaid(rs.getBigDecimal("LPIPaid"));
			od.setLPIBal(rs.getBigDecimal("LPIBal"));
			od.setLPIWaived(rs.getBigDecimal("LPIWaived"));
			od.setApplyODPenalty(rs.getBoolean("ApplyODPenalty"));
			od.setODIncGrcDays(rs.getBoolean("ODIncGrcDays"));
			od.setODChargeType(rs.getString("ODChargeType"));
			od.setODGraceDays(rs.getInt("ODGraceDays"));
			od.setODChargeCalOn(rs.getString("ODChargeCalOn"));
			od.setODChargeAmtOrPerc(rs.getBigDecimal("ODChargeAmtOrPerc"));
			od.setODAllowWaiver(rs.getBoolean("ODAllowWaiver"));
			od.setODMaxWaiverPerc(rs.getBigDecimal("ODMaxWaiverPerc"));
			od.setLpCpz(rs.getBoolean("LpCpz"));
			od.setLpCpzAmount(rs.getBigDecimal("LpCpzAmount"));
			od.setLpCurCpzBal(rs.getBigDecimal("LpCurCpzBal"));
			od.setFinLMdfDate(rs.getTimestamp("FinLMdfDate"));
			od.setODRuleCode(rs.getString("ODRuleCode"));
			od.setPresentmentID(rs.getLong("PresentmentId"));
			od.setCurOverdraftTxnChrg(rs.getBigDecimal("CurOverdraftTxnChrg"));
			od.setMaxOverdraftTxnChrg(rs.getBigDecimal("MaxOverdraftTxnChrg"));
			od.setOdMinAmount(rs.getBigDecimal("ODMinAmount"));
			od.setPayableAmount(rs.getBigDecimal("PayableAmount"));

			return od;
		}));
	}

	private StringBuilder updateFODQuery() {
		StringBuilder sql = new StringBuilder("Update FinODDetails");
		sql.append(" Set");
		sql.append(" FinODTillDate = ?, FinCurODAmt = ?, FinCurODPri = ?, FinCurODPft = ?, FinCurODDays = ?");
		sql.append(", TotPenaltyAmt = ?, TotWaived = ?, TotPenaltyPaid = ?, TotPenaltyBal = ?, FinLMdfDate = ?");
		sql.append(", LPIPaid = ?, LPIBal = ?, LPIWaived = ?, LpCpz = ?, LpCpzAmount = ?, LpCurCpzBal = ?");
		sql.append(", CurOverdraftTxnChrg = ?, PayableAmount = ?");
		sql.append(" Where FinID = ? and FinODSchdDate = ?");
		return sql;
	}

	@Override
	public void updatePaidPenalties(List<FinODDetails> overdues) {
		StringBuilder sql = new StringBuilder("Update");
		sql.append(" FinODDetails Set");
		sql.append(" FinCurODAmt = ?, FinCurODPri = ?, FinCurODPft = ?, FinCurODDays = ?");
		sql.append(", TotPenaltyAmt = ?, TotWaived = ?, TotPenaltyPaid = ?, TotPenaltyBal = ?, FinLMdfDate = ?");
		sql.append(
				", LPIPaid = ?, LPIBal = ?, LPIWaived = ?, LpCpz = ?, LpCpzAmount = ?, LpCurCpzBal = ?, PayableAmount = ?");
		sql.append(" Where FinID = ? and FinODSchdDate = ?");

		logger.debug(Literal.SQL + sql.toString());

		jdbcOperations.batchUpdate(sql.toString(), new BatchPreparedStatementSetter() {
			@Override
			public void setValues(PreparedStatement ps, int i) throws SQLException {
				FinODDetails fd = overdues.get(i);

				setPrepareStatement(ps, fd);
			}

			@Override
			public int getBatchSize() {
				return overdues.size();
			}
		});
	}

	private void setPrepareStatement(PreparedStatement ps, FinODDetails fd) throws SQLException {
		int index = 1;

		ps.setBigDecimal(index++, fd.getFinCurODAmt());
		ps.setBigDecimal(index++, fd.getFinCurODPri());
		ps.setBigDecimal(index++, fd.getFinCurODPft());
		ps.setInt(index++, fd.getFinCurODDays());
		ps.setBigDecimal(index++, fd.getTotPenaltyAmt());
		ps.setBigDecimal(index++, fd.getTotWaived());
		ps.setBigDecimal(index++, fd.getTotPenaltyPaid());
		ps.setBigDecimal(index++, fd.getTotPenaltyBal());
		ps.setDate(index++, JdbcUtil.getDate(fd.getFinLMdfDate()));
		ps.setBigDecimal(index++, fd.getLPIPaid());
		ps.setBigDecimal(index++, fd.getLPIBal());
		ps.setBigDecimal(index++, fd.getLPIWaived());
		ps.setBoolean(index++, fd.isLpCpz());
		ps.setBigDecimal(index++, fd.getLpCpzAmount());
		ps.setBigDecimal(index++, fd.getLpCurCpzBal());
		ps.setBigDecimal(index++, fd.getPayableAmount());

		ps.setLong(index++, fd.getFinID());
		ps.setDate(index, JdbcUtil.getDate(fd.getFinODSchdDate()));
	}

	@Override
	public void updateFinODTotals(List<FinODDetails> list) {
		StringBuilder sql = new StringBuilder("Update FinODDetails");
		sql.append(" Set LPIBal = LPIBal + ?, LPIWaived = LPIWaived + ?");
		sql.append(", TotWaived = TotWaived + ?, TotPenltyBal = TotPenltyBal + ?");
		sql.append(", PriPenaltyWaived = PriPenaltyWaived + ?, PriPenaltyBal = PriPenaltyBal + ?");
		sql.append(", PftPenaltyWaived = PftPenaltyWaived + ?, PftPenaltyBal = PftPenaltyBal + ?");
		sql.append(" Where FinID = ? and FinODSchdDate = ?");

		logger.debug(Literal.SQL.concat(sql.toString()));

		this.jdbcOperations.batchUpdate(sql.toString(), new BatchPreparedStatementSetter() {

			@Override
			public void setValues(PreparedStatement ps, int i) throws SQLException {
				FinODDetails od = list.get(i);
				int index = 0;

				ps.setBigDecimal(++index, od.getLPIBal());
				ps.setBigDecimal(++index, od.getLPIWaived());
				ps.setBigDecimal(++index, od.getTotWaived());
				ps.setBigDecimal(++index, od.getTotPenaltyBal());
				ps.setBigDecimal(++index, od.getPriPenaltyWaived());
				ps.setBigDecimal(++index, od.getPriPenaltyBal());
				ps.setBigDecimal(++index, od.getPftPenaltyWaived());
				ps.setBigDecimal(++index, od.getPftPenaltyBal());
				ps.setLong(++index, od.getFinID());
				ps.setDate(++index, JdbcUtil.getDate(od.getFinODSchdDate()));
			}

			@Override
			public int getBatchSize() {
				return list.size();
			}
		});
	}

	@Override
	public BigDecimal getOverDueAmount(long finID) {
		String sql = "Select coalesce(Sum(coalesce(TotPenaltyBal,0) + coalesce(LpiBal, 0)),0) TotalDue From FinODDetails Where FinID = ? ";

		logger.debug(Literal.SQL.concat(sql));

		return this.jdbcOperations.queryForObject(sql, BigDecimal.class, finID);
	}

	@Override
	public void delete(long finID) {
		String sql = "Delete from FinODDetails Where FinID = ?";

		logger.debug(Literal.SQL.concat(sql));

		this.jdbcOperations.update(sql.toString(), finID);
	}

	@Override
	public List<FinODDetails> getLPPDueAmount(long finID) {
		String sql = "Select LppDueAmt, LppDueTillDate From FinODDetails Where FinID = ?";

		logger.debug(Literal.SQL.concat(sql));

		return this.jdbcOperations.query(sql.toString(), (rs, rowNum) -> {
			FinODDetails od = new FinODDetails();

			od.setLppDueAmt(rs.getBigDecimal("LppDueAmt"));
			od.setLppDueTillDate(rs.getDate("LppDueTillDate"));

			return od;
		}, finID);
	}
}