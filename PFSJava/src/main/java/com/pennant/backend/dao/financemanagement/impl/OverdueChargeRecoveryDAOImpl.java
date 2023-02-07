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
 * * FileName : OverdueChargeRecoveryDAOImpl.java * * Author : PENNANT TECHONOLOGIES * * Creation Date : 11-05-2012 * *
 * Modified Date : 11-05-2012 * * Description : * *
 ********************************************************************************************
 * Date Author Version Comments *
 ********************************************************************************************
 * 11-05-2012 Pennant 0.1 * * * * * * * * *
 ********************************************************************************************
 */
package com.pennant.backend.dao.financemanagement.impl;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.RowMapper;

import com.pennant.backend.dao.financemanagement.OverdueChargeRecoveryDAO;
import com.pennant.backend.model.WorkFlowDetails;
import com.pennant.backend.model.financemanagement.OverdueChargeRecovery;
import com.pennant.backend.util.WorkFlowUtil;
import com.pennanttech.pennapps.core.ConcurrencyException;
import com.pennanttech.pennapps.core.DependencyFoundException;
import com.pennanttech.pennapps.core.jdbc.BasicDao;
import com.pennanttech.pennapps.core.jdbc.JdbcUtil;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pennapps.core.resource.Message;

/**
 * DAO methods implementation for the <b>OverdueChargeRecovery model</b> class.<br>
 * 
 */
public class OverdueChargeRecoveryDAOImpl extends BasicDao<OverdueChargeRecovery> implements OverdueChargeRecoveryDAO {
	private static Logger logger = LogManager.getLogger(OverdueChargeRecoveryDAOImpl.class);

	public OverdueChargeRecoveryDAOImpl() {
		super();
	}

	@Override
	public OverdueChargeRecovery getOverdueChargeRecovery() {
		WorkFlowDetails workFlowDetails = WorkFlowUtil.getWorkFlowDetails("OverdueChargeRecovery");
		OverdueChargeRecovery overdueChargeRecovery = new OverdueChargeRecovery();

		if (workFlowDetails != null) {
			overdueChargeRecovery.setWorkflowId(workFlowDetails.getWorkFlowId());
		}
		return overdueChargeRecovery;
	}

	@Override
	public OverdueChargeRecovery getNewOverdueChargeRecovery() {
		OverdueChargeRecovery overdueChargeRecovery = getOverdueChargeRecovery();
		overdueChargeRecovery.setNewRecord(true);
		return overdueChargeRecovery;
	}

	@Override
	public OverdueChargeRecovery getOverdueChargeRecoveryById(long finID, Date finSchDate, String finOdFor,
			String type) {
		StringBuilder sql = new StringBuilder("Select * From (");
		sql.append(getSelectQuery(type).toString());
		sql.append(" Where FinID = ? and FinODSchdDate = ? AND FinODFor = ?) T  Where row_num <= 1");

		logger.debug(Literal.SQL + sql.toString());

		try {
			OverdueChargeRecoveryRowMapper rowMapper = new OverdueChargeRecoveryRowMapper(type);
			return this.jdbcOperations.queryForObject(sql.toString(), rowMapper, finID, finSchDate, finOdFor);
		} catch (EmptyResultDataAccessException e) {
			logger.warn(Message.NO_RECORD_FOUND);
			return null;
		}
	}

	@Override
	public List<OverdueChargeRecovery> getOverdueChargeRecoveryByRef(long finID, Date schdDate, String schdFor) {
		StringBuilder sql = getSelectQuery("");
		sql.append(" Where FinID = ? and FinODSchdDate = ? AND FinODFor = ?");

		logger.debug(Literal.SQL + sql.toString());

		OverdueChargeRecoveryRowMapper rowMapper = new OverdueChargeRecoveryRowMapper("");
		return this.jdbcOperations.query(sql.toString(), rowMapper, finID, schdDate, schdFor);
	}

	@Override
	public OverdueChargeRecovery getMaxOverdueChargeRecoveryById(long finID, Date finSchDate, String finOdFor,
			String type) {

		StringBuilder sql = new StringBuilder("Select");
		sql.append(" FinID, FinReference, FinODSchdDate, FinODFor, MovementDate, SeqNo");
		sql.append(", Penalty, WaivedAmt, PenaltyPaid, PenaltyBal, RcdCanDel");
		sql.append(" From FinODCRecovery");
		sql.append(StringUtils.trimToEmpty(type));
		sql.append(" Where FinID = ? and FinODSchdDate = ? and FinODFor = ?");

		logger.debug(Literal.SQL + sql.toString());

		try {
			return this.jdbcOperations.queryForObject(sql.toString(), (rs, rowNum) -> {
				OverdueChargeRecovery ocr = new OverdueChargeRecovery();

				ocr.setFinID(rs.getLong("FinID"));
				ocr.setFinReference(rs.getString("FinReference"));
				ocr.setFinODSchdDate(rs.getDate("FinODSchdDate"));
				ocr.setFinODFor(rs.getString("FinODFor"));
				ocr.setMovementDate(rs.getDate("MovementDate"));
				ocr.setSeqNo(rs.getInt("SeqNo"));
				ocr.setPenalty(rs.getBigDecimal("Penalty"));
				ocr.setWaivedAmt(rs.getBigDecimal("WaivedAmt"));
				ocr.setPenaltyPaid(rs.getBigDecimal("PenaltyPaid"));
				ocr.setPenaltyBal(rs.getBigDecimal("PenaltyBal"));
				ocr.setRcdCanDel(rs.getBoolean("RcdCanDel"));

				return ocr;

			}, finID, finSchDate, finOdFor);
		} catch (EmptyResultDataAccessException e) {
			logger.warn(Message.NO_RECORD_FOUND);
			return null;
		}
	}

	@Override
	public void delete(OverdueChargeRecovery odcr, String type) {
		StringBuilder sql = new StringBuilder("Delete From FinODCRecovery");
		sql.append(StringUtils.trimToEmpty(type));
		sql.append(" Where FinID = ?");

		logger.debug(Literal.SQL + sql.toString());

		try {
			int recordCount = this.jdbcOperations.update(sql.toString(), odcr.getFinID());
			if (recordCount <= 0) {
				throw new ConcurrencyException();
			}
		} catch (DataAccessException e) {
			throw new DependencyFoundException(e);
		}
	}

	@Override
	public void deleteUnpaid(long finID, Date finODSchDate, String finODFor, String type) {
		StringBuilder sql = new StringBuilder("Delete From FinODCRecovery");
		sql.append(StringUtils.trimToEmpty(type));
		sql.append(" Where FinID = ? and FinODSchdDate= ? and FinODFor= ? and RcdCanDel = ?");

		logger.debug(Literal.SQL + sql.toString());

		this.jdbcOperations.update(sql.toString(), finID, finODSchDate, finODFor, 1);
	}

	@Override
	public String save(OverdueChargeRecovery odcr, String type) {
		StringBuilder sql = new StringBuilder("Insert Into FinODCRecovery");
		sql.append(" (FinID, FinReference, FinODSchdDate, FinODFor, MovementDate, SeqNo, ODDays, FinCurODAmt");
		sql.append(", FinCurODPri, FinCurODPft, PenaltyType, PenaltyCalOn, PenaltyAmtPerc, Penalty");
		sql.append(", MaxWaiver, WaivedAmt, PenaltyPaid, PenaltyBal, RcdCanDel)");
		sql.append(" Values(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)");

		logger.debug(Literal.SQL + sql.toString());

		this.jdbcOperations.update(sql.toString(), ps -> {
			int index = 1;

			ps.setLong(index++, odcr.getFinID());
			ps.setString(index++, odcr.getFinReference());
			ps.setDate(index++, JdbcUtil.getDate(odcr.getFinODSchdDate()));
			ps.setString(index++, odcr.getFinODFor());
			ps.setDate(index++, JdbcUtil.getDate(odcr.getMovementDate()));
			ps.setInt(index++, odcr.getSeqNo());
			ps.setInt(index++, odcr.getODDays());
			ps.setBigDecimal(index++, odcr.getFinCurODAmt());
			ps.setBigDecimal(index++, odcr.getFinCurODPri());
			ps.setBigDecimal(index++, odcr.getFinCurODPft());
			ps.setString(index++, odcr.getPenaltyType());
			ps.setString(index++, odcr.getPenaltyCalOn());
			ps.setBigDecimal(index++, odcr.getPenaltyAmtPerc());
			ps.setBigDecimal(index++, odcr.getPenalty());
			ps.setBigDecimal(index++, odcr.getMaxWaiver());
			ps.setBigDecimal(index++, odcr.getWaivedAmt());
			ps.setBigDecimal(index++, odcr.getPenaltyPaid());
			ps.setBigDecimal(index++, odcr.getPenaltyBal());
			ps.setBoolean(index, odcr.isRcdCanDel());
		});

		return odcr.getId();
	}

	@Override
	public void update(OverdueChargeRecovery odcr, String type) {
		StringBuilder sql = new StringBuilder("Update FinODCRecovery");
		sql.append(StringUtils.trimToEmpty(type));
		sql.append(" Set SeqNo = ?, ODDays = ?, FinCurODAmt = ?, FinCurODPri = ?");
		sql.append(", FinCurODPft = ?, PenaltyType = ?, PenaltyCalOn = ?, PenaltyAmtPerc = ?");
		sql.append(", Penalty= ?, MaxWaiver = ?, WaivedAmt = ?, PenaltyPaid= ?, PenaltyBal= ?, RcdCanDel = ?");
		sql.append(" Where FinID = ? and FinODSchdDate = ? and FinODFor = ? and MovementDate = ?");

		logger.debug(Literal.SQL + sql.toString());

		int recordCount = this.jdbcOperations.update(sql.toString(), ps -> {
			int index = 1;

			ps.setInt(index++, odcr.getSeqNo());
			ps.setInt(index++, odcr.getODDays());
			ps.setBigDecimal(index++, odcr.getFinCurODAmt());
			ps.setBigDecimal(index++, odcr.getFinCurODPri());
			ps.setBigDecimal(index++, odcr.getFinCurODPft());
			ps.setString(index++, odcr.getPenaltyType());
			ps.setString(index++, odcr.getPenaltyCalOn());
			ps.setBigDecimal(index++, odcr.getPenaltyAmtPerc());
			ps.setBigDecimal(index++, odcr.getPenalty());
			ps.setBigDecimal(index++, odcr.getMaxWaiver());
			ps.setBigDecimal(index++, odcr.getWaivedAmt());
			ps.setBigDecimal(index++, odcr.getPenaltyPaid());
			ps.setBigDecimal(index++, odcr.getPenaltyBal());
			ps.setBoolean(index++, odcr.isRcdCanDel());

			ps.setLong(index++, odcr.getFinID());
			ps.setDate(index++, JdbcUtil.getDate(odcr.getFinODSchdDate()));
			ps.setString(index++, odcr.getFinODFor());
			ps.setDate(index, JdbcUtil.getDate(odcr.getMovementDate()));

		});

		if (recordCount <= 0) {
			throw new ConcurrencyException();
		}
	}

	@Override
	public void updatePenaltyPaid(OverdueChargeRecovery odcr, boolean fullyPaidSchd, String type) {
		StringBuilder sql = new StringBuilder("Update FinODCRecovery");
		sql.append(StringUtils.trimToEmpty(type));
		sql.append(" Set WaivedAmt = ?, PenaltyPaid = (PenaltyPaid + ?)");
		sql.append(", PenaltyBal= (PenaltyBal - ?), RcdCanDel = ?");
		sql.append(" Where FinID = ? and FinODSchdDate = ? and FinODFor = ?");

		if (fullyPaidSchd) {
			sql.append(" and MovementDate = (Select MovementDate From FinODCRecovery_ATView");
			sql.append(" Where FinID = ? and FinODSchdDate = ? and FinODFor = ?)  and RcdCanDel = 0");
		} else {
			sql.append(" and MovementDate = ? and RcdCanDel = 1");
		}

		logger.debug(Literal.SQL + sql.toString());

		int recordCount = this.jdbcOperations.update(sql.toString(), ps -> {
			int index = 1;
			ps.setBigDecimal(index++, odcr.getWaivedAmt());
			ps.setBigDecimal(index++, odcr.getPenaltyPaid());
			ps.setBigDecimal(index++, odcr.getPenaltyBal());
			ps.setBoolean(index++, odcr.isRcdCanDel());

			ps.setLong(index++, odcr.getFinID());
			ps.setDate(index++, JdbcUtil.getDate(odcr.getFinODSchdDate()));
			ps.setString(index++, odcr.getFinODFor());

			if (fullyPaidSchd) {
				ps.setLong(index++, odcr.getFinID());
				ps.setDate(index++, JdbcUtil.getDate(odcr.getFinODSchdDate()));
				ps.setString(index++, odcr.getFinODFor());

				ps.setBoolean(index, false);
			} else {
				ps.setDate(index++, JdbcUtil.getDate(odcr.getMovementDate()));
				ps.setBoolean(index, true);
			}
		});

		if (recordCount <= 0) {
			throw new ConcurrencyException();
		}
	}

	@Override
	public void updatePenaltyPaid(OverdueChargeRecovery odcr, String type) {
		StringBuilder sql = new StringBuilder("Update FinODCRecovery");
		sql.append(StringUtils.trimToEmpty(type));
		sql.append(" Set WaivedAmt = ?, PenaltyPaid = ?, PenaltyBal = (PenaltyBal - ?), RcdCanDel= ?");
		sql.append(" Where FinID = ? and FinODSchdDate = ? and FinODFor = ? and MovementDate = ? and RcdCanDel = ?");

		logger.debug(Literal.SQL + sql.toString());

		int recordCount = this.jdbcOperations.update(sql.toString(), ps -> {
			int index = 1;
			ps.setBigDecimal(index++, odcr.getWaivedAmt());
			ps.setBigDecimal(index++, odcr.getPenaltyPaid());
			ps.setBigDecimal(index++, odcr.getPenaltyBal());
			ps.setBoolean(index++, odcr.isRcdCanDel());

			ps.setLong(index++, odcr.getFinID());
			ps.setDate(index++, JdbcUtil.getDate(odcr.getFinODSchdDate()));
			ps.setString(index++, odcr.getFinODFor());
			ps.setDate(index++, JdbcUtil.getDate(odcr.getMovementDate()));
			ps.setBoolean(index, true);

		});

		if (recordCount <= 0) {
			throw new ConcurrencyException();
		}
	}

	public BigDecimal getPendingODCAmount(long finID) {
		String sql = "Select sum(TotPenaltyBal) PendingODC From FinODDetails Where FinID = ?";

		logger.debug(Literal.SQL + sql);

		return this.jdbcOperations.queryForObject(sql, BigDecimal.class, finID);
	}

	@Override
	public List<Long> getOverDueFinanceList() {
		String sql = "Select distinct FinID from FinODDetails Where FinCurODDays > GraceDays";

		logger.debug(Literal.SQL + sql);

		return this.jdbcOperations.queryForList(sql, Long.class);
	}

	@Override
	public List<OverdueChargeRecovery> getFinancePenaltysByFinRef(long finID, String type) {
		StringBuilder sql = new StringBuilder("Select");
		sql.append(" FinID, FinReference, FinODSchdDate, MovementDate, PenaltyPaid");
		sql.append(" From FinODCRecovery");
		sql.append(StringUtils.trimToEmpty(type));
		sql.append(" Where FinID = ? and PenaltyPaid > ?");
		sql.append(" order by FinODSchdDate, MovementDate, SeqNo");

		logger.debug(Literal.SQL + sql.toString());

		return this.jdbcOperations.query(sql.toString(), ps -> {
			int index = 1;
			ps.setLong(index++, finID);
			ps.setInt(index, 0);
		}, (rs, rowNum) -> {
			OverdueChargeRecovery odr = new OverdueChargeRecovery();

			odr.setFinID(rs.getLong("FinID"));
			odr.setFinReference(rs.getString("FinReference"));
			odr.setFinODSchdDate(rs.getTimestamp("FinODSchdDate"));
			odr.setMovementDate(rs.getTimestamp("MovementDate"));
			odr.setPenaltyPaid(rs.getBigDecimal("PenaltyPaid"));

			return odr;
		});
	}

	@Override
	public OverdueChargeRecovery getPastSchedulePenalty(long finID, Date schDate, boolean isCurSchedule,
			boolean befPriPftPay) {

		StringBuilder sql = new StringBuilder("Select");
		sql.append(" R.FinID, R.FinReference, R.FinODSchdDate, R.FinODFor, R.MovementDate");
		sql.append(", R.PenaltyBal, R.PenaltyPaid, R.WaivedAmt, R.PenaltyType, D.TotWaived");
		sql.append(" From FinODCRecovery_AMView R");
		sql.append(" Inner Join FinODDetails D ON R.FinID = D.FinID");
		sql.append(" Where R.FinID = ? and R.FinODSchdDate = D.FinODSchdDate and R.FinODFor = D.FinODFor");

		if (befPriPftPay) {
			sql.append(" and R.FinODSchdDate = ?");
		} else {
			if (isCurSchedule) {
				sql.append(" and R.FinODSchdDate = ? and R.PenaltyBal > 0 and R.RcdCanDel = 0 ");
			} else {
				sql.append(" and R.FinODSchdDate < ? AND R.PenaltyBal > 0 and R.RcdCanDel = 0 ");
			}
		}

		logger.debug(Literal.SQL + sql.toString());

		try {
			return this.jdbcOperations.queryForObject(sql.toString(), (rs, rowNum) -> {
				OverdueChargeRecovery odcr = new OverdueChargeRecovery();

				odcr.setFinID(rs.getLong("FinID"));
				odcr.setFinReference(rs.getString("FinReference"));
				odcr.setFinODSchdDate(rs.getDate("FinODSchdDate"));
				odcr.setFinODFor(rs.getString("FinODFor"));
				odcr.setMovementDate(rs.getDate("MovementDate"));
				odcr.setPenaltyBal(rs.getBigDecimal("PenaltyBal"));
				odcr.setPenaltyPaid(rs.getBigDecimal("PenaltyPaid"));
				odcr.setWaivedAmt(rs.getBigDecimal("WaivedAmt"));
				odcr.setPenaltyType(rs.getString("PenaltyType"));
				odcr.setTotWaived(rs.getBigDecimal("TotWaived"));

				return odcr;
			}, finID, schDate);
		} catch (EmptyResultDataAccessException e) {
			logger.warn(Message.NO_RECORD_FOUND);
			return null;
		}
	}

	@Override
	public List<OverdueChargeRecovery> getPastSchedulePenalties(long finID) {
		StringBuilder sql = new StringBuilder("Select");
		sql.append(" R.FinID, R.FinReference, R.FinODSchdDate, R.FinODFor, R.MovementDate");
		sql.append(", R.PenaltyBal, R.PenaltyPaid, R.WaivedAmt, R.PenaltyType, D.TotWaived");
		sql.append(" From FinODCRecovery_AMView R");
		sql.append(" Inner Join FinODDetails D ON R.FinID = D.FinID");
		sql.append(" Where R.FinID = ? and R.FinODSchdDate = D.FinODSchdDate and R.FinODFor = D.FinODFor");
		sql.append(" and R.PenaltyBal > 0 and R.RcdCanDel = 0");

		logger.debug(Literal.SQL + sql.toString());

		return this.jdbcOperations.query(sql.toString(), (rs, rowNum) -> {
			OverdueChargeRecovery odcr = new OverdueChargeRecovery();

			odcr.setFinID(rs.getLong("FinID"));
			odcr.setFinReference(rs.getString("FinReference"));
			odcr.setFinODSchdDate(rs.getDate("FinODSchdDate"));
			odcr.setFinODFor(rs.getString("FinODFor"));
			odcr.setMovementDate(rs.getDate("MovementDate"));
			odcr.setPenaltyBal(rs.getBigDecimal("PenaltyBal"));
			odcr.setPenaltyPaid(rs.getBigDecimal("PenaltyPaid"));
			odcr.setWaivedAmt(rs.getBigDecimal("WaivedAmt"));
			odcr.setPenaltyType(rs.getString("PenaltyType"));
			odcr.setTotWaived(rs.getBigDecimal("TotWaived"));

			return odcr;
		}, finID);

	}

	@Override
	public OverdueChargeRecovery getODCRecoveryDetails(OverdueChargeRecovery ocr) {
		StringBuilder sql = new StringBuilder("Select");
		sql.append(" (sum(PrincipalSchd) - sum(SchdPriPaid)) LovDescCurSchPriDue,");
		sql.append(" (sum(ProfitSchd) - sum(SchdPftPaid)) LovDescCurSchPftDue");
		sql.append(" From FinScheduleDetails");
		sql.append(" WHERE FinID = ?");

		logger.debug(Literal.SQL + sql.toString());

		try {
			return this.jdbcOperations.queryForObject(sql.toString(), (rs, rowNum) -> {
				OverdueChargeRecovery odcr = new OverdueChargeRecovery();
				odcr.setLovDescCurSchPriDue(rs.getBigDecimal("LovDescCurSchPriDue"));
				odcr.setLovDescCurSchPftDue(rs.getBigDecimal("LovDescCurSchPftDue"));

				return odcr;
			}, ocr.getFinID());
		} catch (EmptyResultDataAccessException e) {
			logger.warn(Message.NO_RECORD_FOUND);
			return ocr;
		}
	}

	private StringBuilder getSelectQuery(String type) {
		StringBuilder sql = new StringBuilder("Select");
		sql.append(" FinID, FinReference, FinODSchdDate, FinODFor, MovementDate, SeqNo, ODDays");
		sql.append(", FinCurODAmt, FinCurODPri, FinCurODPft, PenaltyType");
		sql.append(", PenaltyCalOn, PenaltyAmtPerc, Penalty, MaxWaiver, WaivedAmt, PenaltyPaid, PenaltyBal, RcdCanDel");

		if (StringUtils.trimToEmpty(type).contains("View")) {
			sql.append(", FinCcy, lovDescCustCIF, lovDescCustShrtName, lovDescFinStartDate, LovDescMaturityDate");
			sql.append(", LovDescFinAmount, LovDescCurFinAmt, LovDescCurSchPriDue, LovDescCurSchPftDue");
			sql.append(", LovDescTotOvrDueChrg, LovDescTotOvrDueChrgWaived, LovDescTotOvrDueChrgPaid");
		}

		sql.append(" From FinODCRecovery");
		sql.append(StringUtils.trimToEmpty(type));

		return sql;
	}

	private class OverdueChargeRecoveryRowMapper implements RowMapper<OverdueChargeRecovery> {

		private String type = null;

		private OverdueChargeRecoveryRowMapper(String type) {
			this.type = type;
		}

		@Override
		public OverdueChargeRecovery mapRow(ResultSet rs, int rowNum) throws SQLException {
			OverdueChargeRecovery ocr = new OverdueChargeRecovery();

			ocr.setFinID(rs.getLong("FinID"));
			ocr.setFinReference(rs.getString("FinReference"));
			ocr.setFinODSchdDate(rs.getDate("FinODSchdDate"));
			ocr.setFinODFor(rs.getString("FinODFor"));
			ocr.setMovementDate(rs.getDate("MovementDate"));
			ocr.setSeqNo(rs.getInt("SeqNo"));
			ocr.setODDays(rs.getInt("ODDays"));
			ocr.setFinCurODAmt(rs.getBigDecimal("FinCurODAmt"));
			ocr.setFinCurODPri(rs.getBigDecimal("FinCurODPri"));
			ocr.setFinCurODPft(rs.getBigDecimal("FinCurODPft"));
			ocr.setPenaltyType(rs.getString("PenaltyType"));
			ocr.setPenaltyCalOn(rs.getString("PenaltyCalOn"));
			ocr.setPenaltyAmtPerc(rs.getBigDecimal("PenaltyAmtPerc"));
			ocr.setPenalty(rs.getBigDecimal("Penalty"));
			ocr.setMaxWaiver(rs.getBigDecimal("MaxWaiver"));
			ocr.setWaivedAmt(rs.getBigDecimal("WaivedAmt"));
			ocr.setPenaltyPaid(rs.getBigDecimal("PenaltyPaid"));
			ocr.setPenaltyBal(rs.getBigDecimal("PenaltyBal"));
			ocr.setRcdCanDel(rs.getBoolean("RcdCanDel"));

			if (StringUtils.trimToEmpty(type).contains("View")) {
				ocr.setFinCcy(rs.getString("FinCcy"));
				ocr.setLovDescCustCIF(rs.getString("LovDescCustCIF"));
				ocr.setLovDescCustShrtName(rs.getString("LovDescCustShrtName"));
				ocr.setLovDescFinStartDate(rs.getDate("LovDescFinStartDate"));
				ocr.setLovDescMaturityDate(rs.getDate("LovDescMaturityDate"));
				ocr.setLovDescFinAmount(rs.getBigDecimal("LovDescFinAmount"));
				ocr.setLovDescCurFinAmt(rs.getBigDecimal("LovDescCurFinAmt"));
				ocr.setLovDescCurSchPriDue(rs.getBigDecimal("LovDescCurSchPriDue"));
				ocr.setLovDescCurSchPftDue(rs.getBigDecimal("LovDescCurSchPftDue"));
				ocr.setLovDescTotOvrDueChrg(rs.getBigDecimal("LovDescTotOvrDueChrg"));
				ocr.setLovDescTotOvrDueChrgWaived(rs.getBigDecimal("LovDescTotOvrDueChrgWaived"));
				ocr.setLovDescTotOvrDueChrgPaid(rs.getBigDecimal("LovDescTotOvrDueChrgPaid"));
			}

			return ocr;
		}
	}

}