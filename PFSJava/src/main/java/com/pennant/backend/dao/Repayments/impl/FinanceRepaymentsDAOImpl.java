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
package com.pennant.backend.dao.Repayments.impl;

import java.math.BigDecimal;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.RowMapper;

import com.pennant.backend.dao.Repayments.FinanceRepaymentsDAO;
import com.pennant.backend.model.Repayments.FinanceRepayments;
import com.pennant.backend.model.finance.FinRepayHeader;
import com.pennant.backend.model.finance.RepayScheduleDetail;
import com.pennanttech.pennapps.core.ConcurrencyException;
import com.pennanttech.pennapps.core.jdbc.JdbcUtil;
import com.pennanttech.pennapps.core.jdbc.SequenceDao;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pennapps.core.resource.Message;
import com.pennanttech.pennapps.core.util.DateUtil;
import com.pennanttech.pff.core.TableType;

/**
 * DAO methods implementation for the <b>Finance Repayments</b> class.<br>
 * 
 */
public class FinanceRepaymentsDAOImpl extends SequenceDao<FinanceRepayments> implements FinanceRepaymentsDAO {
	private static Logger logger = LogManager.getLogger(FinanceRepaymentsDAOImpl.class);

	public FinanceRepaymentsDAOImpl() {
		super();
	}

	public long getFinancePaySeq(FinanceRepayments rpd) {
		String sql = "Select coalesce(max(FinPaySeq), 0) From FinRepayDetails Where FinID = ? and  FinSchdDate = ? and FinRpyFor = ?";

		logger.debug(Literal.SQL + sql);

		Object[] objects = new Object[] { rpd.getFinID(), rpd.getFinSchdDate(), rpd.getFinRpyFor() };

		long repaySeq = this.jdbcOperations.queryForObject(sql, Long.class, objects);

		return repaySeq + 1;
	}

	@Override
	public void save(List<FinanceRepayments> list, String type) {
		String sql = getInsertQuery(type);

		Map<String, Long> repaySeqMap = new HashMap<>();

		try {
			jdbcOperations.batchUpdate(sql, new BatchPreparedStatementSetter() {
				@Override
				public void setValues(PreparedStatement ps, int seq) throws SQLException {
					FinanceRepayments fr = list.get(seq);

					int index = 1;

					long finPaySeq = 0;
					if (fr.getId() == Long.MIN_VALUE || fr.getId() == 0) {
						String key = fr.getFinID() + DateUtil.formatToShortDate(fr.getFinSchdDate())
								+ fr.getFinRpyFor();
						repaySeqMap.computeIfAbsent(key, abc -> getFinancePaySeq(fr));

						finPaySeq = repaySeqMap.get(key);
						finPaySeq = finPaySeq + seq;
						fr.setFinPaySeq(finPaySeq);

					}

					ps.setLong(index++, fr.getFinID());
					ps.setString(index++, fr.getFinReference());
					ps.setDate(index++, JdbcUtil.getDate(fr.getFinSchdDate()));
					ps.setString(index++, fr.getFinRpyFor());
					ps.setLong(index++, fr.getFinPaySeq());
					ps.setLong(index++, fr.getLinkedTranId());
					ps.setBigDecimal(index++, fr.getFinRpyAmount());
					ps.setDate(index++, JdbcUtil.getDate(fr.getFinPostDate()));
					ps.setDate(index++, JdbcUtil.getDate(fr.getFinValueDate()));
					ps.setString(index++, fr.getFinBranch());
					ps.setString(index++, fr.getFinType());
					ps.setLong(index++, fr.getFinCustID());
					ps.setBigDecimal(index++, fr.getFinSchdPriPaid());
					ps.setBigDecimal(index++, fr.getFinSchdPftPaid());
					ps.setBigDecimal(index++, fr.getFinSchdTdsPaid());
					ps.setBigDecimal(index++, fr.getSchdFeePaid());
					ps.setBigDecimal(index++, fr.getFinTotSchdPaid());
					ps.setBigDecimal(index++, fr.getFinFee());
					ps.setBigDecimal(index++, fr.getFinWaiver());
					ps.setBigDecimal(index++, fr.getFinRefund());
					ps.setBigDecimal(index++, fr.getPenaltyPaid());
					ps.setBigDecimal(index++, fr.getPenaltyWaived());
					ps.setLong(index++, fr.getReceiptId());
					ps.setLong(index, fr.getWaiverId());

				}

				@Override
				public int getBatchSize() {
					return list.size();
				}
			});

		} catch (DuplicateKeyException e) {
			throw new ConcurrencyException(e);
		}
	}

	@Override
	public long save(FinanceRepayments fr, String type) {
		List<FinanceRepayments> list = new ArrayList<>();

		list.add(fr);

		save(list, type);

		return fr.getId();
	}

	@Override
	public List<FinanceRepayments> getByFinRefAndSchdDate(long finID, Date finSchdDate) {
		StringBuilder sql = new StringBuilder("Select");
		sql.append(" FinID, FinReference, FinPostDate, FinSchdDate, FinValueDate, FinSchdPriPaid, FinSchdPftPaid");
		sql.append(", FinSchdTdsPaid, FinTotSchdPaid, PenaltyPaid, PenaltyWaived");
		sql.append(" From FinRepayDetails");
		sql.append(" Where FinID = ? and FinSchdDate = ?");

		logger.debug(Literal.SQL + sql.toString());

		List<FinanceRepayments> repaymentList = this.jdbcOperations.query(sql.toString(), ps -> {
			int index = 1;
			ps.setLong(index++, finID);
			ps.setDate(index, JdbcUtil.getDate(finSchdDate));

		}, (rs, rowNum) -> {
			FinanceRepayments rpd = new FinanceRepayments();

			rpd.setFinID(rs.getLong("FinID"));
			rpd.setFinReference(rs.getString("FinReference"));
			rpd.setFinPostDate(rs.getTimestamp("FinPostDate"));
			rpd.setFinSchdDate(rs.getTimestamp("FinSchdDate"));
			rpd.setFinValueDate(rs.getTimestamp("FinValueDate"));
			rpd.setFinSchdPriPaid(rs.getBigDecimal("FinSchdPriPaid"));
			rpd.setFinSchdPftPaid(rs.getBigDecimal("FinSchdPftPaid"));
			rpd.setFinSchdTdsPaid(rs.getBigDecimal("FinSchdTdsPaid"));
			rpd.setFinTotSchdPaid(rs.getBigDecimal("FinTotSchdPaid"));
			rpd.setPenaltyPaid(rs.getBigDecimal("PenaltyPaid"));
			rpd.setPenaltyWaived(rs.getBigDecimal("PenaltyWaived"));

			return rpd;

		});

		return sortByFinValueDate(repaymentList);
	}

	@Override
	public void deleteRpyDetailbyLinkedTranId(long linkedTranId, long finID) {
		String sql = "Delete From FinRepayDetails Where FinID = ? and LinkedTranId = ?";

		logger.debug(Literal.SQL + sql);

		this.jdbcOperations.update(sql, ps -> {
			ps.setLong(1, finID);
			ps.setLong(2, linkedTranId);
		});
	}

	@Override
	public void deleteRpyDetailbyMaxPostDate(Date finPostDate, long finID) {
		String sql = "Delete From FinRepayDetails Where FinID = ? and FinPostDate = ?";

		logger.debug(Literal.SQL + sql);

		this.jdbcOperations.update(sql, ps -> {
			ps.setLong(1, finID);
			ps.setDate(2, JdbcUtil.getDate(finPostDate));
		});
	}

	@Override
	public FinRepayHeader getFinRepayHeader(long finID, String type) {
		StringBuilder sql = getFinRepayheaderQuery(type);
		sql.append(" Where FinID = ?");

		logger.debug(Literal.SQL + sql.toString());

		FinRepayHeaderRowMapper rowMapper = new FinRepayHeaderRowMapper();
		try {
			return this.jdbcOperations.queryForObject(sql.toString(), rowMapper, finID);
		} catch (EmptyResultDataAccessException e) {
			logger.warn(Message.NO_RECORD_FOUND);
			return null;
		}
	}

	@Override
	public FinRepayHeader getFinRepayHeader(long finID, long linkedTranId, String type) {
		StringBuilder sql = new StringBuilder("Select");
		sql.append(" ValueDate, FinEvent");
		sql.append(" From FinRepayHeader");
		sql.append(StringUtils.trim(type));
		sql.append(" Where FinID = ? and LinkedTranId = ?");

		logger.debug(Literal.SQL + sql.toString());

		try {
			return this.jdbcOperations.queryForObject(sql.toString(), (rs, rowNum) -> {
				FinRepayHeader rh = new FinRepayHeader();

				rh.setValueDate(rs.getTimestamp("ValueDate"));
				rh.setFinEvent(rs.getString("FinEvent"));

				return rh;

			}, finID, linkedTranId);
		} catch (EmptyResultDataAccessException e) {
			logger.warn(Message.NO_RECORD_FOUND);
			return null;
		}
	}

	@Override
	public Long saveFinRepayHeader(FinRepayHeader rph, TableType tableType) {
		if (rph.getRepayID() == 0 || rph.getRepayID() == Long.MIN_VALUE) {
			rph.setRepayID(getNextValue("SeqFinRepayHeader"));
		}

		StringBuilder sql = new StringBuilder("Insert into");
		sql.append(" FinRepayHeader").append(StringUtils.trimToEmpty(tableType.getSuffix()));
		sql.append(" (RepayID, ReceiptSeqID, FinID, FinReference, ValueDate, FinEvent, RepayAmount, PriAmount");
		sql.append(", PftAmount, TotalRefund, TotalWaiver, EarlyPayEffMtd");
		sql.append(", EarlyPayDate, SchdRegenerated, LinkedTranId ");
		sql.append(", TotalSchdFee, PayApportionment, LatePftAmount, TotalPenalty, RealizeUnAmz, CpzChg");
		sql.append(", AdviseAmount, FeeAmount, ExcessAmount, RealizeUnLPI, PartialPaidAmount, FutPriAmount");
		sql.append(", FutPftAmount");
		sql.append(") values(");
		sql.append("?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?");
		sql.append(")");

		logger.debug(Literal.SQL + sql.toString());

		jdbcOperations.update(sql.toString(), ps -> {
			int index = 1;

			ps.setLong(index++, rph.getRepayID());
			ps.setLong(index++, rph.getReceiptSeqID());
			ps.setLong(index++, rph.getFinID());
			ps.setString(index++, rph.getFinReference());
			ps.setDate(index++, JdbcUtil.getDate(rph.getValueDate()));
			ps.setString(index++, rph.getFinEvent());
			ps.setBigDecimal(index++, rph.getRepayAmount());
			ps.setBigDecimal(index++, rph.getPriAmount());
			ps.setBigDecimal(index++, rph.getPftAmount());
			ps.setBigDecimal(index++, rph.getTotalRefund());
			ps.setBigDecimal(index++, rph.getTotalWaiver());
			ps.setString(index++, rph.getEarlyPayEffMtd());
			ps.setDate(index++, JdbcUtil.getDate(rph.getEarlyPayDate()));
			ps.setBoolean(index++, rph.isSchdRegenerated());
			ps.setLong(index++, rph.getLinkedTranId());
			ps.setBigDecimal(index++, rph.getTotalSchdFee());
			ps.setString(index++, rph.getPayApportionment());
			ps.setBigDecimal(index++, rph.getLatePftAmount());
			ps.setBigDecimal(index++, rph.getTotalPenalty());
			ps.setBigDecimal(index++, rph.getRealizeUnAmz());
			ps.setBigDecimal(index++, rph.getCpzChg());
			ps.setBigDecimal(index++, rph.getAdviseAmount());
			ps.setBigDecimal(index++, rph.getFeeAmount());
			ps.setBigDecimal(index++, rph.getExcessAmount());
			ps.setBigDecimal(index++, rph.getRealizeUnLPI());
			ps.setBigDecimal(index++, rph.getPartialPaidAmount());
			ps.setBigDecimal(index++, rph.getFutPriAmount());
			ps.setBigDecimal(index, rph.getFutPftAmount());

		});

		return rph.getRepayID();
	}

	@Override
	public void updateFinRepayHeader(FinRepayHeader rph, String type) {
		StringBuilder sql = new StringBuilder("Update FinRepayHeader");
		sql.append(StringUtils.trimToEmpty(type));
		sql.append(" Set ValueDate = ?, FinEvent = ?, RepayAmount = ?, PriAmount = ?, PftAmount = ?, TotalRefund = ?");
		sql.append(", TotalWaiver = ?, EarlyPayEffMtd = ?, EarlyPayDate = ?, SchdRegenerated = ?");
		sql.append(", LinkedTranId = ?, RealizeUnAmz = ?, CpzChg = ?, TotalSchdFee = ?");
		sql.append(", PayApportionment = ?, AdviseAmount = ?, FeeAmount = ?, ExcessAmount = ?");
		sql.append(" Where FinID = ?");

		logger.debug(Literal.SQL + sql.toString());

		int recordCount = this.jdbcOperations.update(sql.toString(), ps -> {
			int index = 1;
			ps.setDate(index++, JdbcUtil.getDate(rph.getValueDate()));
			ps.setString(index++, rph.getFinEvent());
			ps.setBigDecimal(index++, rph.getRepayAmount());
			ps.setBigDecimal(index++, rph.getPriAmount());
			ps.setBigDecimal(index++, rph.getPftAmount());
			ps.setBigDecimal(index++, rph.getTotalRefund());
			ps.setBigDecimal(index++, rph.getTotalWaiver());
			ps.setString(index++, rph.getEarlyPayEffMtd());
			ps.setDate(index++, JdbcUtil.getDate(rph.getEarlyPayDate()));
			ps.setBoolean(index++, rph.isSchdRegenerated());
			ps.setLong(index++, rph.getLinkedTranId());
			ps.setBigDecimal(index++, rph.getRealizeUnAmz());
			ps.setBigDecimal(index++, rph.getCpzChg());
			ps.setBigDecimal(index++, rph.getTotalSchdFee());
			ps.setString(index++, rph.getPayApportionment());
			ps.setBigDecimal(index++, rph.getAdviseAmount());
			ps.setBigDecimal(index++, rph.getFeeAmount());
			ps.setBigDecimal(index++, rph.getExcessAmount());

			ps.setLong(index, rph.getFinID());

		});

		if (recordCount <= 0) {
			throw new ConcurrencyException();
		}

		logger.debug("Leaving");
	}

	@Override
	public void deleteFinRepayHeader(FinRepayHeader rph, String type) {
		StringBuilder sql = new StringBuilder("Delete From FinRepayHeader");
		sql.append(StringUtils.trimToEmpty(type));
		sql.append(" Where FinID = ?");

		logger.debug(Literal.SQL + sql.toString());

		this.jdbcOperations.update(sql.toString(), rph.getFinID());
	}

	@Override
	public void deleteFinRepayHeaderByTranId(long finID, long linkedTranId, String type) {
		StringBuilder sql = new StringBuilder("Delete From FinRepayHeader");
		sql.append(StringUtils.trimToEmpty(type));
		sql.append(" Where FinID = ? and LinkedTranId = ?");

		this.jdbcOperations.update(sql.toString(), finID, linkedTranId);
	}

	@Override
	public List<RepayScheduleDetail> getRpySchdList(long finID, String type) {
		StringBuilder sql = getFinRepayScheduleQuery(type);
		sql.append(" Where FinID = ?");

		logger.debug(Literal.SQL + sql.toString());

		FinRepaySchdRowMapper rowMapper = new FinRepaySchdRowMapper();

		return this.jdbcOperations.query(sql.toString(), ps -> {
			int index = 1;
			ps.setLong(index, finID);
		}, rowMapper);
	}

	@Override
	public List<RepayScheduleDetail> getRpySchedulesForDate(long finID, Date schDate) {
		StringBuilder sql = new StringBuilder("Select");
		sql.append(" rsd.SchDate, rph.ValueDate, rsd.ProfitSchdPayNow, rsd.PrincipalSchdPayNow");
		sql.append(", rsd.WaivedAmt, rsd.PenaltyPayNow, rsd.LatePftSchdPayNow, rsd.PftSchdWaivedNow");
		sql.append(", rsd.LatePftSchdWaivedNow, rsd.PriSchdWaivedNow ");
		sql.append(" From FinRepayScheduleDetail rsd");
		sql.append(" Inner Join FinRepayHeader rph on rph.RepayID = rsd.RepayID");
		sql.append(" Inner Join FinReceiptDetail rcd on rcd.ReceiptSeqID = rph.ReceiptSeqID ");
		sql.append(" Where rsd.FinID = ? and rsd.SchDate = ? and rcd.Status not in (?, ?) ");

		logger.debug(Literal.SQL + sql.toString());

		return this.jdbcOperations.query(sql.toString(), ps -> {
			int index = 1;
			ps.setLong(index++, finID);
			ps.setDate(index++, JdbcUtil.getDate(schDate));
			ps.setString(index++, "C");
			ps.setString(index, "B");

		}, (rs, rowNum) -> {
			RepayScheduleDetail rsd = new RepayScheduleDetail();

			rsd.setSchDate(rs.getDate("SchDate"));
			rsd.setValueDate(rs.getDate("ValueDate"));
			rsd.setProfitSchdPayNow(rs.getBigDecimal("ProfitSchdPayNow"));
			rsd.setPrincipalSchdPayNow(rs.getBigDecimal("PrincipalSchdPayNow"));
			rsd.setWaivedAmt(rs.getBigDecimal("WaivedAmt"));
			rsd.setPenaltyPayNow(rs.getBigDecimal("PenaltyPayNow"));
			rsd.setLatePftSchdPayNow(rs.getBigDecimal("LatePftSchdPayNow"));
			rsd.setPftSchdWaivedNow(rs.getBigDecimal("PftSchdWaivedNow"));
			rsd.setLatePftSchdWaivedNow(rs.getBigDecimal("LatePftSchdWaivedNow"));
			rsd.setPriSchdWaivedNow(rs.getBigDecimal("PriSchdWaivedNow"));

			return rsd;
		});
	}

	@Override
	public void saveRpySchdList(List<RepayScheduleDetail> rsdList, TableType tableType) {
		StringBuilder sql = new StringBuilder("Insert Into FinRepayScheduleDetail");
		sql.append(StringUtils.trimToEmpty(tableType.getSuffix())).append("(");
		sql.append(" RepayID, RepaySchID, FinID, FinReference, SchDate, SchdFor, LinkedTranId, ProfitSchdBal");
		sql.append(", PrincipalSchdBal, ProfitSchdPayNow, TdsSchdPayNow, PrincipalSchdPayNow, PenaltyAmt");
		sql.append(", DaysLate, MaxWaiver, AllowRefund, AllowWaiver, ProfitSchd, ProfitSchdPaid");
		sql.append(", PrincipalSchd, PrincipalSchdPaid, RefundReq, WaivedAmt, RepayBalance, PenaltyPayNow");
		sql.append(", SchdFee, SchdFeePaid, SchdFeeBal, SchdFeePayNow, LatePftSchd, LatePftSchdPaid, LatePftSchdBal");
		sql.append(", LatePftSchdPayNow, PftSchdWaivedNow, LatePftSchdWaivedNow, PriSchdWaivedNow, SchdFeeWaivedNow");
		sql.append(", TaxHeaderId, WaiverId");
		sql.append(") values(");
		sql.append(" ?, ?, ?, ?, ?, ?, ?, ? , ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?");
		sql.append(", ?, ?,  ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?");
		sql.append(")");

		logger.debug(Literal.SQL + sql.toString());

		this.jdbcOperations.batchUpdate(sql.toString(), new BatchPreparedStatementSetter() {

			@Override
			public void setValues(PreparedStatement ps, int i) throws SQLException {
				RepayScheduleDetail rsd = rsdList.get(i);
				int index = 1;

				ps.setLong(index++, rsd.getRepayID());
				ps.setLong(index++, rsd.getRepaySchID());
				ps.setLong(index++, rsd.getFinID());
				ps.setString(index++, rsd.getFinReference());
				ps.setDate(index++, JdbcUtil.getDate(rsd.getSchDate()));
				ps.setString(index++, rsd.getSchdFor());
				ps.setLong(index++, rsd.getLinkedTranId());
				ps.setBigDecimal(index++, rsd.getProfitSchdBal());
				ps.setBigDecimal(index++, rsd.getPrincipalSchdBal());
				ps.setBigDecimal(index++, rsd.getProfitSchdPayNow());
				ps.setBigDecimal(index++, rsd.getTdsSchdPayNow());
				ps.setBigDecimal(index++, rsd.getPrincipalSchdPayNow());
				ps.setBigDecimal(index++, rsd.getPenaltyAmt());
				ps.setInt(index++, rsd.getDaysLate());
				ps.setBigDecimal(index++, rsd.getMaxWaiver());
				ps.setBoolean(index++, rsd.isAllowRefund());
				ps.setBoolean(index++, rsd.isAllowWaiver());
				ps.setBigDecimal(index++, rsd.getProfitSchd());
				ps.setBigDecimal(index++, rsd.getProfitSchdPaid());
				ps.setBigDecimal(index++, rsd.getPrincipalSchd());
				ps.setBigDecimal(index++, rsd.getPrincipalSchdPaid());
				ps.setBigDecimal(index++, rsd.getRefundReq());
				ps.setBigDecimal(index++, rsd.getWaivedAmt());
				ps.setBigDecimal(index++, rsd.getRepayBalance());
				ps.setBigDecimal(index++, rsd.getPenaltyPayNow());
				ps.setBigDecimal(index++, rsd.getSchdFee());
				ps.setBigDecimal(index++, rsd.getSchdFeePaid());
				ps.setBigDecimal(index++, rsd.getSchdFeeBal());
				ps.setBigDecimal(index++, rsd.getSchdFeePayNow());
				ps.setBigDecimal(index++, rsd.getLatePftSchd());
				ps.setBigDecimal(index++, rsd.getLatePftSchdPaid());
				ps.setBigDecimal(index++, rsd.getLatePftSchdBal());
				ps.setBigDecimal(index++, rsd.getLatePftSchdPayNow());
				ps.setBigDecimal(index++, rsd.getPftSchdWaivedNow());
				ps.setBigDecimal(index++, rsd.getLatePftSchdWaivedNow());
				ps.setBigDecimal(index++, rsd.getPriSchdWaivedNow());
				ps.setBigDecimal(index++, rsd.getSchdFeeWaivedNow());
				ps.setObject(index++, rsd.getTaxHeaderId());
				ps.setLong(index, rsd.getWaiverId());
			}

			@Override
			public int getBatchSize() {
				return rsdList.size();
			}
		});

	}

	@Override
	public void deleteRpySchdList(long finID, String type) {
		StringBuilder sql = new StringBuilder("Delete From FinRepayScheduleDetail");
		sql.append(StringUtils.trimToEmpty(type));
		sql.append(" Where FinID = ?");

		logger.debug(Literal.SQL + sql.toString());

		this.jdbcOperations.update(sql.toString(), finID);
	}

	@Override
	public void deleteFinRepaySchListByTranId(long finID, long linkedTranId, String type) {
		StringBuilder sql = new StringBuilder("Delete From FinRepayScheduleDetail");
		sql.append(StringUtils.trimToEmpty(type));
		sql.append(" Where FinID = ? and LinkedTranId = ?");

		logger.debug(Literal.SQL + sql.toString());

		this.jdbcOperations.update(sql.toString(), finID, linkedTranId);
	}

	@Override
	public BigDecimal getPaidPft(long finID, Date finPostDate) {
		String sql = "Select sum(FinSchdPftPaid) From FinRepayDetails Where FinID = ? and FinPostDate < ?";

		logger.debug(Literal.SQL + sql);
		return this.jdbcOperations.queryForObject(sql, BigDecimal.class, finID, finPostDate);
	}

	@Override
	public List<FinRepayHeader> getFinRepayHeadersByRef(long finID, String type) {
		StringBuilder sql = getFinRepayheaderQuery(type);
		sql.append(" Where FinID = ?");

		logger.debug(Literal.SQL + sql.toString());

		FinRepayHeaderRowMapper rowMapper = new FinRepayHeaderRowMapper();

		return this.jdbcOperations.query(sql.toString(), ps -> {
			int index = 1;
			ps.setLong(index, finID);
		}, rowMapper);
	}

	@Override
	public void deleteByRef(long finID, TableType tableType) {
		StringBuilder sql = new StringBuilder("Delete From FinRepayHeader");
		sql.append(tableType.getSuffix());
		sql.append(" Where FinID = ?");

		logger.debug(Literal.SQL + sql.toString());

		this.jdbcOperations.update(sql.toString(), finID);
	}

	@Override
	public void updateFinReference(String finReference, String extReference, String type) {
		StringBuilder sql = new StringBuilder("Update FinRepayHeader");
		sql.append(type);
		sql.append(" Set FinReference = ?");
		sql.append(" Where FinReference = ?");

		logger.debug(Literal.SQL + sql.toString());

		int recordCount = this.jdbcOperations.update(sql.toString(), finReference, extReference);

		if (recordCount <= 0) {
			throw new ConcurrencyException();
		}

	}

	@Override
	public FinRepayHeader getFinRepayHeadersByReceipt(long receiptId, String type) {
		StringBuilder sql = getFinRepayheaderQuery(type);
		sql.append(" Where ReceiptSeqID = ?");

		logger.debug(Literal.SQL + sql.toString());

		FinRepayHeaderRowMapper rowMapper = new FinRepayHeaderRowMapper();

		try {
			return this.jdbcOperations.queryForObject(sql.toString(), rowMapper, receiptId);
		} catch (EmptyResultDataAccessException e) {
			logger.warn(Message.NO_RECORD_FOUND);
			return null;
		}
	}

	@Override
	public List<RepayScheduleDetail> getRpySchdListByRepayID(long repayId, String type) {
		StringBuilder sql = getFinRepayScheduleQuery(type);
		sql.append(" Where RepayID = ?");

		logger.debug(Literal.SQL + sql.toString());

		FinRepaySchdRowMapper rowMapper = new FinRepaySchdRowMapper();

		return this.jdbcOperations.query(sql.toString(), ps -> {
			int index = 1;
			ps.setLong(index, repayId);
		}, rowMapper);
	}

	@Override
	public void deleteByReceiptId(long receiptId, TableType tableType) {
		StringBuilder sql = new StringBuilder("Delete From FinRepayHeader");
		sql.append(tableType.getSuffix());
		sql.append(" Where ReceiptSeqID = ?");

		logger.debug(Literal.SQL + sql.toString());

		this.jdbcOperations.update(sql.toString(), receiptId);
	}

	@Override
	public List<FinanceRepayments> getInProcessRepaymnets(long finID, List<Long> receiptList) {
		StringBuilder sql = new StringBuilder("Select FinSchdDate");
		sql.append(", sum(FinSchdPriPaid) FinSchdPriPaid");
		sql.append(", sum(FinSchdPftPaid) FinSchdPftPaid");
		sql.append(", sum(FinSchdTdsPaid) FinSchdTdsPaid");
		sql.append(", sum(FinTotSchdPaid) FinTotSchdPaid");
		sql.append(", sum(PenaltyPaid) PenaltyPaid");
		sql.append(" From FinRepayDetails");
		sql.append(" Where FinID = ? and ReceiptId In (");
		sql.append(JdbcUtil.getInCondition(receiptList));
		sql.append(")");
		sql.append(" group by Finschddate");

		logger.debug(Literal.SQL + sql.toString());
		return this.jdbcOperations.query(sql.toString(), ps -> {
			int index = 1;
			ps.setLong(index++, finID);

			for (Long receiptId : receiptList) {
				ps.setLong(index++, receiptId);
			}

		}, (rs, rowNum) -> {
			FinanceRepayments rpd = new FinanceRepayments();

			rpd.setFinSchdDate(rs.getDate("FinSchdDate"));
			rpd.setFinSchdPriPaid(rs.getBigDecimal("FinSchdPriPaid"));
			rpd.setFinSchdPftPaid(rs.getBigDecimal("FinSchdPftPaid"));
			rpd.setFinSchdTdsPaid(rs.getBigDecimal("FinSchdTdsPaid"));
			rpd.setFinTotSchdPaid(rs.getBigDecimal("FinTotSchdPaid"));
			rpd.setPenaltyPaid(rs.getBigDecimal("PenaltyPaid"));

			return rpd;

		});
	}

	@Override
	public List<FinanceRepayments> getFinRepayments(long finID, List<Long> receiptList) {
		StringBuilder sql = new StringBuilder("Select");
		sql.append(" FinID, FinReference, FinPostDate, FinRpyFor, FinPaySeq, FinRpyAmount, FinSchdDate, FinValueDate");
		sql.append(", FinBranch, FinType, FinCustID, FinSchdPriPaid, FinSchdPftPaid, FinSchdTdsPaid");
		sql.append(", FinTotSchdPaid, FinFee, FinWaiver, FinRefund, SchdFeePaid ");
		sql.append(" From FinRepayDetails");
		sql.append(" Where FinID = ?");

		if (receiptList != null && receiptList.size() > 0) {
			sql.append(" and ReceiptId IN (");

			sql.append(JdbcUtil.getInCondition(receiptList));

			sql.append(" )");
		}

		logger.debug(Literal.SQL + sql.toString());

		return this.jdbcOperations.query(sql.toString(), ps -> {
			int index = 1;
			ps.setLong(index++, finID);
			if (receiptList != null && receiptList.size() > 0) {
				for (Long receiptId : receiptList) {
					ps.setLong(index++, receiptId);
				}
			}
		}, (rs, rowNum) -> {
			FinanceRepayments rd = new FinanceRepayments();

			rd.setFinID(rs.getLong("FinID"));
			rd.setFinReference(rs.getString("FinReference"));
			rd.setFinPostDate(rs.getTimestamp("FinPostDate"));
			rd.setFinRpyFor(rs.getString("FinRpyFor"));
			rd.setFinPaySeq(rs.getLong("FinPaySeq"));
			rd.setFinRpyAmount(rs.getBigDecimal("FinRpyAmount"));
			rd.setFinSchdDate(rs.getTimestamp("FinSchdDate"));
			rd.setFinValueDate(rs.getTimestamp("FinValueDate"));
			rd.setFinBranch(rs.getString("FinBranch"));
			rd.setFinType(rs.getString("FinType"));
			rd.setFinCustID(rs.getLong("FinCustID"));
			rd.setFinSchdPriPaid(rs.getBigDecimal("FinSchdPriPaid"));
			rd.setFinSchdPftPaid(rs.getBigDecimal("FinSchdPftPaid"));
			rd.setFinSchdTdsPaid(rs.getBigDecimal("FinSchdTdsPaid"));
			rd.setFinTotSchdPaid(rs.getBigDecimal("FinTotSchdPaid"));
			rd.setFinFee(rs.getBigDecimal("FinFee"));
			rd.setFinWaiver(rs.getBigDecimal("FinWaiver"));
			rd.setFinRefund(rs.getBigDecimal("FinRefund"));
			rd.setSchdFeePaid(rs.getBigDecimal("SchdFeePaid"));

			return rd;
		});
	}

	private StringBuilder getFinRepayScheduleQuery(String type) {
		StringBuilder sql = new StringBuilder("Select");
		sql.append(" RepayID, RepaySchID, FinID, FinReference, SchDate, SchdFor, ProfitSchdBal, PrincipalSchdBal");
		sql.append(", ProfitSchd, ProfitSchdPaid, PrincipalSchd, PrincipalSchdPaid, ProfitSchdPayNow");
		sql.append(", TdsSchdPayNow, PrincipalSchdPayNow, PenaltyAmt, DaysLate, MaxWaiver, AllowRefund");
		sql.append(", AllowWaiver, RefundReq, WaivedAmt, RepayBalance, PenaltyPayNow, SchdFee, SchdFeePaid");
		sql.append(", SchdFeeBal, SchdFeePayNow, LatePftSchd");
		sql.append(", LatePftSchdPaid, LatePftSchdBal, LatePftSchdPayNow");
		sql.append(", PftSchdWaivedNow, LatePftSchdWaivedNow, PriSchdWaivedNow");
		sql.append(", SchdFeeWaivedNow");
		sql.append(", PaidPenaltyCGST, PaidPenaltySGST, PaidPenaltyUGST, PaidPenaltyIGST, PenaltyWaiverCGST");
		sql.append(", PenaltyWaiverSGST, PenaltyWaiverUGST, PenaltyWaiverIGST, TaxHeaderId, WaiverId");
		sql.append(" From FinRepayScheduleDetail");
		sql.append(StringUtils.trimToEmpty(type));
		return sql;
	}

	/**
	 * Method for Fetching new RepayID
	 * 
	 * @param finRepayHeader
	 * @return
	 */
	@Override
	public long getNewRepayID() {
		return getNextValue("SeqFinRepayHeader");
	}

	public static List<FinanceRepayments> sortByFinValueDate(List<FinanceRepayments> finRepay) {
		if (finRepay != null && finRepay.size() > 0) {
			Collections.sort(finRepay, new Comparator<FinanceRepayments>() {
				@Override
				public int compare(FinanceRepayments detail1, FinanceRepayments detail2) {
					return DateUtil.compare(detail1.getFinValueDate(), detail2.getFinValueDate());
				}
			});
		}

		return finRepay;
	}

	@Override
	public List<Long> getLinkedTranIdByReceipt(long receiptId, String type) {
		String sql = "Select LinkedTranId from FinRepayDetails Where ReceiptID = ?";

		logger.debug(Literal.SQL + sql);

		return this.jdbcOperations.query(sql, ps -> ps.setLong(1, receiptId), (rs, i) -> {
			return rs.getLong(1);
		});
	}

	@Override
	public Date getMaxValueDate(long finID) {
		String sql = "Select max(FinValueDate) FinValueDate From FinRepayDetails Where FinID = ?";

		logger.debug(Literal.SQL + sql);

		return jdbcOperations.queryForObject(sql, Date.class, finID);
	}

	@Override
	public Date getFinSchdDateByReceiptId(long receiptid, String type) {
		String sql = "Select Min(FinSchdDate) from FinRepayDetails Where ReceiptId = ? and FinTotSchdPaid > ?";

		logger.debug(Literal.SQL + sql);
		return jdbcOperations.queryForObject(sql, Date.class, receiptid, 0);
	}

	private String getInsertQuery(String type) {
		StringBuilder sql = new StringBuilder("insert into");
		sql.append(" FinRepayDetails");
		sql.append(StringUtils.trimToEmpty(type));
		sql.append(" (FinID, FinReference, FinSchdDate, FinRpyFor, FinPaySeq, LinkedTranId, FinRpyAmount, FinPostDate");
		sql.append(", FinValueDate, FinBranch, FinType, FinCustID, FinSchdPriPaid, FinSchdPftPaid, FinSchdTdsPaid");
		sql.append(", SchdFeePaid, FinTotSchdPaid, FinFee");
		sql.append(", FinWaiver, FinRefund, PenaltyPaid, PenaltyWaived, ReceiptId, WaiverId");
		sql.append(") values(");
		sql.append("?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?");
		sql.append(")");
		return sql.toString();
	}

	private StringBuilder getFinRepayheaderQuery(String type) {
		StringBuilder sql = new StringBuilder("Select");
		sql.append(" RepayID, ReceiptSeqID, FinID, FinReference, ValueDate, FinEvent, RepayAmount, PriAmount");
		sql.append(", PftAmount, LatePftAmount, TotalPenalty, TotalRefund, TotalWaiver ");
		sql.append(", EarlyPayEffMtd, EarlyPayDate, SchdRegenerated, LinkedTranId");
		sql.append(", TotalSchdFee, PayApportionment, RealizeUnAmz");
		sql.append(", CpzChg, RealizeUnLPI, RealizeUnLPP, RealizeUnLPIGst, RealizeUnLPPGst, CpzChg");
		sql.append(", AdviseAmount, FeeAmount, ExcessAmount, PartialPaidAmount, FutPriAmount, FutPftAmount");
		sql.append(" From FinRepayHeader");
		sql.append(StringUtils.trim(type));
		return sql;
	}

	private class FinRepayHeaderRowMapper implements RowMapper<FinRepayHeader> {

		@Override
		public FinRepayHeader mapRow(ResultSet rs, int rowNum) throws SQLException {
			FinRepayHeader rh = new FinRepayHeader();

			rh.setRepayID(rs.getLong("RepayID"));
			rh.setReceiptSeqID(rs.getLong("ReceiptSeqID"));
			rh.setFinID(rs.getLong("FinID"));
			rh.setFinReference(rs.getString("FinReference"));
			rh.setValueDate(rs.getTimestamp("ValueDate"));
			rh.setFinEvent(rs.getString("FinEvent"));
			rh.setRepayAmount(rs.getBigDecimal("RepayAmount"));
			rh.setPriAmount(rs.getBigDecimal("PriAmount"));
			rh.setPftAmount(rs.getBigDecimal("PftAmount"));
			rh.setLatePftAmount(rs.getBigDecimal("LatePftAmount"));
			rh.setTotalPenalty(rs.getBigDecimal("TotalPenalty"));
			rh.setTotalRefund(rs.getBigDecimal("TotalRefund"));
			rh.setTotalWaiver(rs.getBigDecimal("TotalWaiver"));
			rh.setEarlyPayEffMtd(rs.getString("EarlyPayEffMtd"));
			rh.setEarlyPayDate(rs.getTimestamp("EarlyPayDate"));
			rh.setSchdRegenerated(rs.getBoolean("SchdRegenerated"));
			rh.setLinkedTranId(rs.getLong("LinkedTranId"));
			rh.setTotalSchdFee(rs.getBigDecimal("TotalSchdFee"));
			rh.setPayApportionment(rs.getString("PayApportionment"));
			rh.setRealizeUnAmz(rs.getBigDecimal("RealizeUnAmz"));
			rh.setCpzChg(rs.getBigDecimal("CpzChg"));
			rh.setRealizeUnLPI(rs.getBigDecimal("RealizeUnLPI"));
			/*
			 * rh.setRealizeUnLPP(rs.getBigDecimal("RealizeUnLPP"));
			 * rh.setRealizeUnLPIGst(rs.getBigDecimal("RealizeUnLPIGst"));
			 * rh.setRealizeUnLPPGst(rs.getBigDecimal("RealizeUnLPPGst")); (these columns are not available in bean)
			 */
			rh.setAdviseAmount(rs.getBigDecimal("AdviseAmount"));
			rh.setFeeAmount(rs.getBigDecimal("FeeAmount"));
			rh.setExcessAmount(rs.getBigDecimal("ExcessAmount"));
			rh.setPartialPaidAmount(rs.getBigDecimal("PartialPaidAmount"));
			rh.setFutPriAmount(rs.getBigDecimal("FutPriAmount"));
			rh.setFutPftAmount(rs.getBigDecimal("FutPftAmount"));

			return rh;
		}
	}

	private class FinRepaySchdRowMapper implements RowMapper<RepayScheduleDetail> {

		@Override
		public RepayScheduleDetail mapRow(ResultSet rs, int rowNum) throws SQLException {
			RepayScheduleDetail frs = new RepayScheduleDetail();

			frs.setRepayID(rs.getLong("RepayID"));
			frs.setRepaySchID(rs.getInt("RepaySchID"));
			frs.setFinID(rs.getLong("FinID"));
			frs.setFinReference(rs.getString("FinReference"));
			frs.setSchDate(rs.getTimestamp("SchDate"));
			frs.setSchdFor(rs.getString("SchdFor"));
			frs.setProfitSchdBal(rs.getBigDecimal("ProfitSchdBal"));
			frs.setPrincipalSchdBal(rs.getBigDecimal("PrincipalSchdBal"));
			frs.setProfitSchd(rs.getBigDecimal("ProfitSchd"));
			frs.setProfitSchdPaid(rs.getBigDecimal("ProfitSchdPaid"));
			frs.setPrincipalSchd(rs.getBigDecimal("PrincipalSchd"));
			frs.setPrincipalSchdPaid(rs.getBigDecimal("PrincipalSchdPaid"));
			frs.setProfitSchdPayNow(rs.getBigDecimal("ProfitSchdPayNow"));
			frs.setTdsSchdPayNow(rs.getBigDecimal("TdsSchdPayNow"));
			frs.setPrincipalSchdPayNow(rs.getBigDecimal("PrincipalSchdPayNow"));
			frs.setPenaltyAmt(rs.getBigDecimal("PenaltyAmt"));
			frs.setDaysLate(rs.getInt("DaysLate"));
			frs.setMaxWaiver(rs.getBigDecimal("MaxWaiver"));
			frs.setAllowRefund(rs.getBoolean("AllowRefund"));
			frs.setAllowWaiver(rs.getBoolean("AllowWaiver"));
			frs.setRefundReq(rs.getBigDecimal("RefundReq"));
			frs.setWaivedAmt(rs.getBigDecimal("WaivedAmt"));
			frs.setRepayBalance(rs.getBigDecimal("RepayBalance"));
			frs.setPenaltyPayNow(rs.getBigDecimal("PenaltyPayNow"));
			frs.setSchdFee(rs.getBigDecimal("SchdFee"));
			frs.setSchdFeePaid(rs.getBigDecimal("SchdFeePaid"));
			frs.setSchdFeeBal(rs.getBigDecimal("SchdFeeBal"));
			frs.setSchdFeePayNow(rs.getBigDecimal("SchdFeePayNow"));
			frs.setLatePftSchd(rs.getBigDecimal("LatePftSchd"));
			frs.setLatePftSchdPaid(rs.getBigDecimal("LatePftSchdPaid"));
			frs.setLatePftSchdBal(rs.getBigDecimal("LatePftSchdBal"));
			frs.setLatePftSchdPayNow(rs.getBigDecimal("LatePftSchdPayNow"));
			frs.setPftSchdWaivedNow(rs.getBigDecimal("PftSchdWaivedNow"));
			frs.setLatePftSchdWaivedNow(rs.getBigDecimal("LatePftSchdWaivedNow"));
			frs.setPriSchdWaivedNow(rs.getBigDecimal("PriSchdWaivedNow"));
			frs.setSchdFeeWaivedNow(rs.getBigDecimal("SchdFeeWaivedNow"));
			frs.setTaxHeaderId(JdbcUtil.getLong(rs.getObject("TaxHeaderId")));
			frs.setWaiverId(JdbcUtil.getLong(rs.getObject("WaiverId")));

			return frs;
		}
	}

	@Override
	public List<FinanceRepayments> getFinRepayList(long finID) {
		StringBuilder sql = getRepayListQuery();
		sql.append(" Where FinID = ?");

		logger.debug(Literal.SQL + sql.toString());

		List<FinanceRepayments> repaymentList = this.jdbcOperations.query(sql.toString(), ps -> {
			int index = 1;
			ps.setLong(index, finID);

		}, new RepayListRM());

		return repaymentList.stream().sorted((rp1, rp2) -> DateUtil.compare(rp2.getFinSchdDate(), rp1.getFinSchdDate()))
				.collect(Collectors.toList());
	}

	@Override
	public List<FinanceRepayments> getFinRepayListByLinkedTranID(long finID) {
		long linkedTranID = getMaxLinkedTranID(finID);
		Date postDate = getMaxPostDate(finID);

		StringBuilder sql = getRepayListQuery();
		sql.append(" Where FinID = ?");

		if (linkedTranID > 0) {
			sql.append(" and LinkedTranId = ?");
		} else if (postDate != null) {
			sql.append(" and FinPostDate = ?");
		} else {
			return new ArrayList<>();
		}

		logger.debug(Literal.SQL + sql.toString());

		List<FinanceRepayments> repaymentList = this.jdbcOperations.query(sql.toString(), ps -> {
			int index = 1;
			ps.setLong(index++, finID);

			if (linkedTranID > 0) {
				ps.setLong(index, linkedTranID);
			} else if (postDate != null) {
				ps.setDate(index, JdbcUtil.getDate(postDate));
			}

		}, new RepayListRM());

		return repaymentList.stream().sorted((rp1, rp2) -> DateUtil.compare(rp2.getFinSchdDate(), rp1.getFinSchdDate()))
				.collect(Collectors.toList());
	}

	private StringBuilder getRepayListQuery() {
		StringBuilder sql = new StringBuilder("Select");
		sql.append(" FInID, FinReference, FinPostDate, FinRpyFor, FinPaySeq, FinRpyAmount");
		sql.append(", FinSchdDate, FinValueDate, FinBranch, FinType, FinCustID");
		sql.append(", FinSchdPriPaid, FinSchdPftPaid, FinSchdTdsPaid, FinTotSchdPaid");
		sql.append(", FinFee, FinWaiver, FinRefund, SchdFeePaid, PenaltyPaid, PenaltyWaived");
		sql.append(", LinkedTranId");
		sql.append(" From FinRepayDetails");

		return sql;
	}

	private class RepayListRM implements RowMapper<FinanceRepayments> {

		@Override
		public FinanceRepayments mapRow(ResultSet rs, int rowNum) throws SQLException {
			FinanceRepayments rd = new FinanceRepayments();

			rd.setFinID(rs.getLong("FinID"));
			rd.setFinReference(rs.getString("FinReference"));
			rd.setFinPostDate(rs.getTimestamp("FinPostDate"));
			rd.setFinRpyFor(rs.getString("FinRpyFor"));
			rd.setFinPaySeq(rs.getLong("FinPaySeq"));
			rd.setFinRpyAmount(rs.getBigDecimal("FinRpyAmount"));
			rd.setFinSchdDate(rs.getTimestamp("FinSchdDate"));
			rd.setFinValueDate(rs.getTimestamp("FinValueDate"));
			rd.setFinBranch(rs.getString("FinBranch"));
			rd.setFinType(rs.getString("FinType"));
			rd.setFinCustID(rs.getLong("FinCustID"));
			rd.setFinSchdPriPaid(rs.getBigDecimal("FinSchdPriPaid"));
			rd.setFinSchdPftPaid(rs.getBigDecimal("FinSchdPftPaid"));
			rd.setFinSchdTdsPaid(rs.getBigDecimal("FinSchdTdsPaid"));
			rd.setFinTotSchdPaid(rs.getBigDecimal("FinTotSchdPaid"));
			rd.setFinFee(rs.getBigDecimal("FinFee"));
			rd.setFinWaiver(rs.getBigDecimal("FinWaiver"));
			rd.setFinRefund(rs.getBigDecimal("FinRefund"));
			rd.setSchdFeePaid(rs.getBigDecimal("SchdFeePaid"));
			rd.setPenaltyPaid(rs.getBigDecimal("PenaltyPaid"));
			rd.setPenaltyWaived(rs.getBigDecimal("PenaltyWaived"));
			rd.setLinkedTranId(rs.getLong("LinkedTranId"));

			return rd;
		}

	}

	private long getMaxLinkedTranID(long finID) {
		String sql = "Select LinkedTranId From FinRepayDetails Where FinID = ?";

		logger.debug(Literal.SQL + sql);

		List<Long> tarnIds = this.jdbcOperations.query(sql, ps -> ps.setLong(1, finID), (rs, rowNum) -> {
			return rs.getLong(1);
		});
		if (tarnIds.isEmpty()) {
			return 0;
		}

		return tarnIds.stream().mapToLong(Long::longValue).max().orElse(0);
	}

	private Date getMaxPostDate(long finID) {
		String sql = "Select FinPostDate From FinRepayDetails Where FinID = ?";

		logger.debug(Literal.SQL + sql);

		List<Date> list = this.jdbcOperations.query(sql, ps -> ps.setLong(1, finID), (rs, rowNum) -> {
			return rs.getDate(1);
		});
		if (list.isEmpty()) {
			return null;
		}

		return list.stream().map(l1 -> l1).max(Date::compareTo).get();
	}

	@Override
	public void updateLinkedTranId(FinRepayHeader rph) {
		String sql = "Update FinRepayHeader set LinkedTranId = ? Where FinID = ? and RepayId = ?";

		logger.debug(Literal.SQL.concat(sql));

		if (this.jdbcOperations.update(sql, rph.getLinkedTranId(), rph.getFinID(), rph.getRepayID()) <= 0) {
			throw new ConcurrencyException();
		}
	}

	@Override
	public List<FinanceRepayments> getByFinRefAndWaiverId(long finID, long waiverId) {
		StringBuilder sql = new StringBuilder("Select");
		sql.append(" FinID, FinReference, FinPostDate, FinSchdDate, FinValueDate, FinSchdPriPaid, FinSchdPftPaid");
		sql.append(", FinSchdTdsPaid, FinTotSchdPaid, LpftWaived, PenaltyWaived");
		sql.append(" From FinRepayDetails");
		sql.append(" Where FinID = ? and WaiverId = ?");

		logger.debug(Literal.SQL.concat(sql.toString()));

		return this.jdbcOperations.query(sql.toString(), ps -> {
			int index = 1;
			ps.setLong(index++, finID);
			ps.setLong(index++, waiverId);

		}, (rs, rowNum) -> {
			FinanceRepayments rpd = new FinanceRepayments();

			rpd.setFinID(rs.getLong("FinID"));
			rpd.setFinReference(rs.getString("FinReference"));
			rpd.setFinPostDate(rs.getTimestamp("FinPostDate"));
			rpd.setFinSchdDate(rs.getTimestamp("FinSchdDate"));
			rpd.setFinValueDate(rs.getTimestamp("FinValueDate"));
			rpd.setFinSchdPriPaid(rs.getBigDecimal("FinSchdPriPaid"));
			rpd.setFinSchdPftPaid(rs.getBigDecimal("FinSchdPftPaid"));
			rpd.setFinSchdTdsPaid(rs.getBigDecimal("FinSchdTdsPaid"));
			rpd.setFinTotSchdPaid(rs.getBigDecimal("FinTotSchdPaid"));
			rpd.setLpftWaived(rs.getBigDecimal("LpftWaived"));
			rpd.setPenaltyWaived(rs.getBigDecimal("PenaltyWaived"));

			return rpd;

		}).stream().sorted((l1, l2) -> DateUtil.compare(l1.getFinValueDate(), l2.getFinValueDate()))
				.collect(Collectors.toList());
	}

	@Override
	public List<FinanceRepayments> getFinRepayListByFinRef(long finID, boolean isRpyCancelProc, String type) {
		StringBuilder sql = getRepayListQuery(isRpyCancelProc, type);
		sql.append(" Where FinID = ?");

		if (isRpyCancelProc) {
			sql.append(" and LinkedTranId = (Select max(LinkedTranId) From FinRepayDetails Where FinID = ?)");
			sql.append(" and LinkedTranId != ?");
		}

		logger.debug(Literal.SQL.concat(sql.toString()));

		List<FinanceRepayments> repaymentList = this.jdbcOperations.query(sql.toString(), ps -> {
			int index = 1;
			ps.setLong(index++, finID);

			if (isRpyCancelProc) {
				ps.setLong(index++, finID);
				ps.setInt(index++, 0);
			}

		}, new FinRepayListRowMapper(isRpyCancelProc));

		if (CollectionUtils.isEmpty(repaymentList)) {
			sql = getRepayListQuery(isRpyCancelProc, type);
			sql.append(" Where FinID = ?");
			if (isRpyCancelProc) {
				sql.append(" and FinPostDate = (Select max(FinPostDate) From FinRepayDetails Where FinID = ?)");
				sql.append(" and LinkedTranId = ?");
			}

			logger.debug(Literal.SQL.concat(sql.toString()));

			repaymentList = this.jdbcOperations.query(sql.toString(), ps -> {
				int index = 1;
				ps.setLong(index++, finID);
				if (isRpyCancelProc) {
					ps.setLong(index++, finID);
					ps.setInt(index++, 0);
				}

			}, new FinRepayListRowMapper(isRpyCancelProc));

		}

		return repaymentList.stream().sorted((rp1, rp2) -> DateUtil.compare(rp2.getFinSchdDate(), rp1.getFinSchdDate()))
				.collect(Collectors.toList());
	}

	private class FinRepayListRowMapper implements RowMapper<FinanceRepayments> {
		private boolean isRpyCancelProc;

		private FinRepayListRowMapper(boolean isRpyCancelProc) {
			this.isRpyCancelProc = isRpyCancelProc;
		}

		@Override
		public FinanceRepayments mapRow(ResultSet rs, int rowNum) throws SQLException {
			FinanceRepayments rd = new FinanceRepayments();

			rd.setFinID(rs.getLong("FinID"));
			rd.setFinReference(rs.getString("FinReference"));
			rd.setFinPostDate(rs.getTimestamp("FinPostDate"));
			rd.setFinRpyFor(rs.getString("FinRpyFor"));
			rd.setFinPaySeq(rs.getLong("FinPaySeq"));
			rd.setFinRpyAmount(rs.getBigDecimal("FinRpyAmount"));
			rd.setFinSchdDate(rs.getTimestamp("FinSchdDate"));
			rd.setFinValueDate(rs.getTimestamp("FinValueDate"));
			rd.setFinBranch(rs.getString("FinBranch"));
			rd.setFinType(rs.getString("FinType"));
			rd.setFinCustID(rs.getLong("FinCustID"));
			rd.setFinSchdPriPaid(rs.getBigDecimal("FinSchdPriPaid"));
			rd.setFinSchdPftPaid(rs.getBigDecimal("FinSchdPftPaid"));
			rd.setFinSchdTdsPaid(rs.getBigDecimal("FinSchdTdsPaid"));
			rd.setFinTotSchdPaid(rs.getBigDecimal("FinTotSchdPaid"));
			rd.setFinFee(rs.getBigDecimal("FinFee"));
			rd.setFinWaiver(rs.getBigDecimal("FinWaiver"));
			rd.setFinRefund(rs.getBigDecimal("FinRefund"));
			rd.setSchdFeePaid(rs.getBigDecimal("SchdFeePaid"));
			rd.setPenaltyPaid(rs.getBigDecimal("PenaltyPaid"));
			rd.setPenaltyWaived(rs.getBigDecimal("PenaltyWaived"));

			if (isRpyCancelProc) {
				rd.setLinkedTranId(rs.getLong("LinkedTranId"));
			}

			return rd;
		}
	}

	private StringBuilder getRepayListQuery(boolean isRpyCancelProc, String type) {
		StringBuilder sql = new StringBuilder("Select");
		sql.append(" FInID, FinReference, FinPostDate, FinRpyFor, FinPaySeq, FinRpyAmount");
		sql.append(", FinSchdDate, FinValueDate, FinBranch, FinType, FinCustID");
		sql.append(", FinSchdPriPaid, FinSchdPftPaid, FinSchdTdsPaid, FinTotSchdPaid");
		sql.append(", FinFee, FinWaiver, FinRefund, SchdFeePaid, PenaltyPaid");
		sql.append(", PenaltyWaived");

		if (isRpyCancelProc) {
			sql.append(", LinkedTranId");
		}

		sql.append(" From FinRepayDetails");
		sql.append(StringUtils.trimToEmpty(type));
		return sql;
	}

}
