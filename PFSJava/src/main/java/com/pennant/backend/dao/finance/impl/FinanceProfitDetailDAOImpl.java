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
 * * FileName : FinanceProfitDetailDAOImpl.java * * Author : PENNANT TECHONOLOGIES * * Creation Date : 09-02-2012 * *
 * Modified Date : 09-02-2012 * * Description : * *
 ********************************************************************************************
 * Date Author Version Comments *
 ********************************************************************************************
 * 09-02-2012 Pennant 0.1 * * * * * * * * *
 ********************************************************************************************
 */
package com.pennant.backend.dao.finance.impl;

import java.math.BigDecimal;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;
import java.util.List;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.RowMapper;

import com.pennant.backend.dao.finance.FinanceProfitDetailDAO;
import com.pennant.backend.model.customermasters.Customer;
import com.pennant.backend.model.finance.FinanceProfitDetail;
import com.pennant.pff.extension.CustomerExtension;
import com.pennanttech.pennapps.core.ConcurrencyException;
import com.pennanttech.pennapps.core.jdbc.BasicDao;
import com.pennanttech.pennapps.core.jdbc.JdbcUtil;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pennapps.core.resource.Message;

/**
 * DAO methods implementation for the <b>FinanceProfitDetail model</b> class.<br>
 * 
 */
public class FinanceProfitDetailDAOImpl extends BasicDao<FinanceProfitDetail> implements FinanceProfitDetailDAO {
	private static Logger logger = LogManager.getLogger(FinanceProfitDetailDAOImpl.class);

	public FinanceProfitDetailDAOImpl() {
		super();
	}

	@Override
	public FinanceProfitDetail getFinProfitDetailsById(long finID) {
		StringBuilder sql = getProfitDetailQuery();
		sql.append(" Where FinID = ?");

		logger.debug(Literal.SQL + sql.toString());

		try {
			return this.jdbcOperations.queryForObject(sql.toString(), new ProfitDetailRowMapper(), finID);
		} catch (EmptyResultDataAccessException e) {
			logger.warn(Message.NO_RECORD_FOUND);
			return null;
		}
	}

	@Override
	public List<FinanceProfitDetail> getFinProfitDetailsByCustId(Customer customer) {
		long custID = customer.getCustID();
		String corBankID = customer.getCustCoreBank();

		StringBuilder sql = getProfitDetailQuery();

		if (CustomerExtension.CUST_CORE_BANK_ID) {
			sql.append(" Where c.CustCoreBank = ? and pd.FinIsActive = ?");
		} else {
			sql.append(" where c.CustId = ? and pd.FinIsActive = ?");
		}

		logger.debug(Literal.SQL.concat(sql.toString()));

		ProfitDetailRowMapper rowMapper = new ProfitDetailRowMapper();

		return this.jdbcOperations.query(sql.toString(), ps -> {
			if (CustomerExtension.CUST_CORE_BANK_ID) {
				ps.setString(1, corBankID);
			} else {
				ps.setLong(1, custID);
			}
			ps.setBoolean(2, true);
		}, rowMapper);

	}

	private StringBuilder getProfitDetailQuery() {
		StringBuilder sql = new StringBuilder("Select");
		sql.append(" FinID, pd.FinReference, pd.CustId, pd.FinBranch, pd.FinType, pd.FinCcy, pd.LastMdfDate");
		sql.append(", pd.FinIsActive, pd.TotalPftSchd, pd.TotalPftCpz, pd.TotalPftPaid, pd.TotalPftBal");
		sql.append(", pd.TotalPftPaidInAdv, pd.TotalPriPaid, pd.TotalPriBal, pd.TdSchdPft, pd.TdPftCpz");
		sql.append(", pd.TdSchdPftPaid, pd.TdSchdPftBal, pd.PftAccrued, pd.PftAccrueSusp, pd.PftAmz, pd.PftAmzSusp");
		sql.append(", pd.TdSchdPri, pd.TdSchdPriPaid, pd.TdSchdPriBal, pd.AcrTillLBD, pd.AmzTillLBD, pd.LpiTillLBD");
		sql.append(", pd.LppTillLBD, pd.GstLpiTillLBD, pd.GstLppTillLBD, pd.FinWorstStatus, pd.FinStatus");
		sql.append(", pd.FinStsReason, pd.ClosingStatus, pd.FinCategory, pd.PrvRpySchDate, pd.NSchdDate");
		sql.append(", pd.PrvRpySchPri, pd.PrvRpySchPft, pd.LatestRpyDate, pd.LatestRpyPri, pd.LatestRpyPft");
		sql.append(", pd.TotalWriteoff, pd.FirstODDate, pd.PrvODDate, pd.ODPrincipal, pd.ODProfit, pd.CurODDays");
		sql.append(", pd.ActualODDays, pd.FinStartDate, pd.FullPaidDate, pd.ExcessAmt, pd.EmiInAdvance");
		sql.append(", pd.PayableAdvise, pd.ExcessAmtResv, pd.EmiInAdvanceResv, pd.PayableAdviseResv");
		sql.append(", pd.AMZMethod, pd.GapIntAmz, pd.GapIntAmzLbd, pd.SvAmount, pd.CbAmount, pd.NOPaidInst");
		sql.append(", pd.NOAutoIncGrcEnd, pd.WriteoffLoan, pd.TotalPriSchd");
		sql.append(", pd.MaturityDate, pd.ProductCategory, pd.PrvMthAmz, pd.PenaltyPaid, pd.PenaltyDue");
		sql.append(", pd.PrvMthGapIntAmz, pd.FirstRepayDate, pd.PrvMthAcr");
		sql.append(" From FinPftDetails pd");
		sql.append(" Inner Join Customers c on c.CustID = pd.CustID");
		return sql;
	}

	@Override
	public FinanceProfitDetail getFinProfitDetailsByFinRef(long finID, boolean isActive) {
		logger.debug(Literal.ENTERING);

		StringBuilder sql = getProfitDetailQuery();
		sql.append(" where FinID = ?");

		if (isActive) {
			sql.append(" and FinIsActive = ?");
		}

		logger.debug(Literal.SQL + sql.toString());

		ProfitDetailRowMapper rowMapper = new ProfitDetailRowMapper();
		return this.jdbcTemplate.getJdbcOperations().queryForObject(sql.toString(), rowMapper, finID, isActive);
	}

	@Override
	public FinanceProfitDetail getFinProfitDetailsByFinRef(long finID) {
		StringBuilder sql = getProfitDetailQuery();
		sql.append(" Where FinID = ?");

		logger.debug(Literal.SQL + sql.toString());

		ProfitDetailRowMapper rowMapper = new ProfitDetailRowMapper();
		return this.jdbcOperations.queryForObject(sql.toString(), rowMapper, finID);
	}

	@Override
	public FinanceProfitDetail getPftDetailForEarlyStlReport(long finID) {
		StringBuilder sql = new StringBuilder("Select");
		sql.append(" FinID, FinReference, TotalPftPaid, TotalPftBal");
		sql.append(", TotalPriPaid, TotalPriBal, NOInst, NOPaidInst");
		sql.append(" from FinPftDetails");
		sql.append(" Where FinID = ?");

		logger.debug(Literal.SQL + sql.toString());

		try {
			return this.jdbcOperations.queryForObject(sql.toString(), (rs, rowNum) -> {
				FinanceProfitDetail fpd = new FinanceProfitDetail();

				fpd.setFinID(rs.getLong("FinID"));
				fpd.setFinReference(rs.getString("FinReference"));
				fpd.setTotalPftPaid(rs.getBigDecimal("TotalPftPaid"));
				fpd.setTotalPftBal(rs.getBigDecimal("TotalPftBal"));
				fpd.setTotalPriPaid(rs.getBigDecimal("TotalPriPaid"));
				fpd.setTotalPriBal(rs.getBigDecimal("TotalPriBal"));
				fpd.setNOInst(rs.getInt("NOInst"));
				fpd.setNOPaidInst(rs.getInt("NOPaidInst"));

				return fpd;
			}, finID);
		} catch (EmptyResultDataAccessException e) {
			logger.warn(Message.NO_RECORD_FOUND);
			return null;
		}
	}

	@Override
	public FinanceProfitDetail getFinProfitDetailsByRef(long finID) {
		StringBuilder sql = new StringBuilder("Select");
		sql.append(" AcrTillLBD, PftAmzSusp, AmzTillLBD, LpiTillLBD, LppTillLBD");
		sql.append(", GstLpiTillLBD, GstLppTillLBD, GapIntAmzLbd");
		sql.append(" From FinPftDetails");
		sql.append(" Where FinID = ?");

		logger.debug(Literal.SQL + sql.toString());

		try {
			return this.jdbcOperations.queryForObject(sql.toString(), (rs, rowNum) -> {
				FinanceProfitDetail pd = new FinanceProfitDetail();

				pd.setAcrTillLBD(rs.getBigDecimal("AcrTillLBD"));
				pd.setPftAmzSusp(rs.getBigDecimal("PftAmzSusp"));
				pd.setAmzTillLBD(rs.getBigDecimal("AmzTillLBD"));
				pd.setLpiTillLBD(rs.getBigDecimal("LpiTillLBD"));
				pd.setLppTillLBD(rs.getBigDecimal("LppTillLBD"));
				pd.setGstLpiTillLBD(rs.getBigDecimal("GstLpiTillLBD"));
				pd.setGstLppTillLBD(rs.getBigDecimal("GstLppTillLBD"));
				pd.setGapIntAmzLbd(rs.getBigDecimal("GapIntAmzLbd"));

				return pd;

			}, finID);
		} catch (EmptyResultDataAccessException e) {
			logger.warn(Message.NO_RECORD_FOUND);
			return null;
		}
	}

	@Override
	public FinanceProfitDetail getProfitDetailForWriteOff(long finID) {
		StringBuilder sql = new StringBuilder("Select");
		sql.append(" ODPrincipal, ODProfit, PenaltyDue, PftAccrued, MaturityDate");
		sql.append(" from FinPftDetails");
		sql.append(" Where FinID = ?");

		logger.debug(Literal.SQL + sql.toString());

		try {
			return this.jdbcOperations.queryForObject(sql.toString(), (rs, rowNum) -> {
				FinanceProfitDetail fpd = new FinanceProfitDetail();

				fpd.setODPrincipal(rs.getBigDecimal("ODPrincipal"));
				fpd.setODProfit(rs.getBigDecimal("ODProfit"));
				fpd.setPenaltyDue(rs.getBigDecimal("PenaltyDue"));
				fpd.setPftAccrued(rs.getBigDecimal("PftAccrued"));
				fpd.setMaturityDate(rs.getDate("MaturityDate"));

				return fpd;

			}, finID);
		} catch (EmptyResultDataAccessException e) {
			logger.warn(Message.NO_RECORD_FOUND);
			return null;
		}
	}

	@Override
	public FinanceProfitDetail getFinProfitDetailsForSummary(long finID) {
		StringBuilder sql = getProfitDetailQuery();
		sql.append(" Where FinID = ?");

		logger.debug(Literal.SQL + sql.toString());

		ProfitDetailRowMapper rowMapper = new ProfitDetailRowMapper();
		return this.jdbcOperations.queryForObject(sql.toString(), rowMapper, finID);

	}

	@Override
	public void update(FinanceProfitDetail pd, boolean isRpyProcess) {
		StringBuilder sql = new StringBuilder("Update FinPftDetails");
		sql.append(" Set");
		sql.append(" PftAccrued = ?, PftAccrueSusp = ?, PftAmz = ?, PftAmzSusp = ?, PftAmzNormal = ?");
		sql.append(", PftAmzPD = ?, PftInSusp = ?, CurFlatRate = ?, CurReducingRate = ?, TotalPftSchd = ?");
		sql.append(", TotalPftCpz = ?, TotalPftPaid = ?, TotalPftBal = ?, TdSchdPft = ?, TdPftCpz = ?");
		sql.append(", TdSchdPftPaid = ?, TdSchdPftBal = ?, TotalpriSchd = ?, TotalPriPaid = ?, TotalPriBal = ?");
		sql.append(", TdSchdPri = ?, TdSchdPriPaid = ?, TdSchdPriBal = ?, CalPftOnPD = ?, PftOnPDMethod = ?");
		sql.append(", PftOnPDMrg = ?, TotPftOnPD = ?, TotPftOnPDPaid = ?, TotPftOnPDWaived = ?, TotPftOnPDDue = ?");
		sql.append(", NOInst = ?, NOPaidInst = ?, NOODInst = ?, FutureInst = ?, RemainingTenor = ?");
		sql.append(", TotalTenor = ?, ODPrincipal = ?, ODProfit = ?, CurODDays = ?, MaxODDays = ?");
		sql.append(", FirstODDate = ?, PrvODDate = ?, PenaltyPaid = ?, PenaltyDue = ?, PenaltyWaived = ?");
		sql.append(", FirstRepayDate = ?, FirstRepayAmt = ?, FinalRepayAmt = ?, FirstDisbDate = ?");
		sql.append(", LatestDisbDate = ?, FullPaidDate = ?, PrvRpySchDate = ?, PrvRpySchPri = ?");
		sql.append(", PrvRpySchPft = ?, RepayFrq = ?, NSchdDate = ?, NSchdPri = ?, NSchdPft = ?");
		sql.append(", NSchdPriDue = ?, NSchdPftDue = ?, AccumulatedDepPri = ?, DepreciatePri = ?");
		sql.append(", TotalPriPaidInAdv = ?, TotalPftPaidInAdv = ?, LastMdfDate = ?, MaturityDate = ?");
		sql.append(", FinIsActive = ?, ClosingStatus = ?, FinStatus = ?, ActualODDays = ?, AmzTillLBD = ?");
		sql.append(", LpiTillLBD = ?, LppTillLBD = ?, GstLpiTillLBD = ?, GstLppTillLBD = ?");
		sql.append(", GapIntAmz = ?, GapIntAmzLbd = ?, PrvMthAmz = ?");

		if (isRpyProcess) {
			sql.append(", LatestRpyDate = ?, LatestRpyPri = ?, LatestRpyPft = ?");
		}

		sql.append(" Where FinID = ?");

		logger.debug(Literal.SQL + sql);

		jdbcOperations.update(sql.toString(), ps -> {
			int index = 1;

			ps.setBigDecimal(index++, pd.getPftAccrued());
			ps.setBigDecimal(index++, pd.getPftAccrueSusp());
			ps.setBigDecimal(index++, pd.getPftAmz());
			ps.setBigDecimal(index++, pd.getPftAmzSusp());
			ps.setBigDecimal(index++, pd.getPftAmzNormal());
			ps.setBigDecimal(index++, pd.getPftAmzPD());
			ps.setBoolean(index++, pd.isPftInSusp());
			ps.setBigDecimal(index++, pd.getCurFlatRate());
			ps.setBigDecimal(index++, pd.getCurReducingRate());
			ps.setBigDecimal(index++, pd.getTotalPftSchd());
			ps.setBigDecimal(index++, pd.getTotalPftCpz());
			ps.setBigDecimal(index++, pd.getTotalPftPaid());
			ps.setBigDecimal(index++, pd.getTotalPftBal());
			ps.setBigDecimal(index++, pd.getTdSchdPft());
			ps.setBigDecimal(index++, pd.getTdPftCpz());
			ps.setBigDecimal(index++, pd.getTdSchdPftPaid());
			ps.setBigDecimal(index++, pd.getTdSchdPftBal());
			ps.setBigDecimal(index++, pd.getTotalpriSchd());
			ps.setBigDecimal(index++, pd.getTotalPriPaid());
			ps.setBigDecimal(index++, pd.getTotalPriBal());
			ps.setBigDecimal(index++, pd.getTdSchdPri());
			ps.setBigDecimal(index++, pd.getTdSchdPriPaid());
			ps.setBigDecimal(index++, pd.getTdSchdPriBal());
			ps.setBoolean(index++, pd.isCalPftOnPD());
			ps.setString(index++, pd.getPftOnPDMethod());
			ps.setBigDecimal(index++, pd.getPftOnPDMrg());
			ps.setBigDecimal(index++, pd.getTotPftOnPD());
			ps.setBigDecimal(index++, pd.getTotPftOnPDPaid());
			ps.setBigDecimal(index++, pd.getTotPftOnPDWaived());
			ps.setBigDecimal(index++, pd.getTotPftOnPDDue());
			ps.setInt(index++, pd.getNOInst());
			ps.setInt(index++, pd.getNOPaidInst());
			ps.setInt(index++, pd.getNOODInst());
			ps.setInt(index++, pd.getFutureInst());
			ps.setInt(index++, pd.getRemainingTenor());
			ps.setInt(index++, pd.getTotalTenor());
			ps.setBigDecimal(index++, pd.getODPrincipal());
			ps.setBigDecimal(index++, pd.getODProfit());
			ps.setInt(index++, pd.getCurODDays());
			ps.setInt(index++, pd.getMaxODDays());
			ps.setDate(index++, JdbcUtil.getDate(pd.getFirstODDate()));
			ps.setDate(index++, JdbcUtil.getDate(pd.getPrvODDate()));
			ps.setBigDecimal(index++, pd.getPenaltyPaid());
			ps.setBigDecimal(index++, pd.getPenaltyDue());
			ps.setBigDecimal(index++, pd.getPenaltyWaived());
			ps.setDate(index++, JdbcUtil.getDate(pd.getFirstRepayDate()));
			ps.setBigDecimal(index++, pd.getFirstRepayAmt());
			ps.setBigDecimal(index++, pd.getFinalRepayAmt());
			ps.setDate(index++, JdbcUtil.getDate(pd.getFirstDisbDate()));
			ps.setDate(index++, JdbcUtil.getDate(pd.getLatestDisbDate()));
			ps.setDate(index++, JdbcUtil.getDate(pd.getFullPaidDate()));
			ps.setDate(index++, JdbcUtil.getDate(pd.getPrvRpySchDate()));
			ps.setBigDecimal(index++, pd.getPrvRpySchPri());
			ps.setBigDecimal(index++, pd.getPrvRpySchPft());
			ps.setString(index++, pd.getRepayFrq());
			ps.setDate(index++, JdbcUtil.getDate(pd.getNSchdDate()));
			ps.setBigDecimal(index++, pd.getNSchdPri());
			ps.setBigDecimal(index++, pd.getNSchdPft());
			ps.setBigDecimal(index++, pd.getNSchdPriDue());
			ps.setBigDecimal(index++, pd.getNSchdPftDue());
			ps.setBigDecimal(index++, pd.getAccumulatedDepPri());
			ps.setBigDecimal(index++, pd.getDepreciatePri());
			ps.setBigDecimal(index++, pd.getTotalPriPaidInAdv());
			ps.setBigDecimal(index++, pd.getTotalPftPaidInAdv());
			ps.setDate(index++, JdbcUtil.getDate(pd.getLastMdfDate()));
			ps.setDate(index++, JdbcUtil.getDate(pd.getMaturityDate()));
			ps.setBoolean(index++, pd.getFinIsActive());
			ps.setString(index++, pd.getClosingStatus());
			ps.setString(index++, pd.getFinStatus());
			ps.setInt(index++, pd.getActualODDays());
			ps.setBigDecimal(index++, pd.getAmzTillLBD());
			ps.setBigDecimal(index++, pd.getLpiTillLBD());
			ps.setBigDecimal(index++, pd.getLppTillLBD());
			ps.setBigDecimal(index++, pd.getGstLpiTillLBD());
			ps.setBigDecimal(index++, pd.getGstLppTillLBD());
			ps.setBigDecimal(index++, pd.getGapIntAmz());
			ps.setBigDecimal(index++, pd.getGapIntAmzLbd());
			ps.setBigDecimal(index++, pd.getPrvMthAmz());

			if (isRpyProcess) {
				ps.setDate(index++, JdbcUtil.getDate(pd.getLatestRpyDate()));
				ps.setBigDecimal(index++, pd.getLatestRpyPri());
				ps.setBigDecimal(index++, pd.getLatestRpyPft());
			}

			ps.setLong(index, pd.getFinID());
		});

	}

	@Override
	public void updateCpzDetail(List<FinanceProfitDetail> pdList) {
		String sql = "Update FinPftDetails Set TdPftCpz = ?, LastMdfDate = ? Where FinID = ?";

		logger.debug(Literal.SQL + sql);

		this.jdbcOperations.batchUpdate(sql, new BatchPreparedStatementSetter() {

			@Override
			public void setValues(PreparedStatement ps, int i) throws SQLException {
				FinanceProfitDetail pd = pdList.get(i);

				int index = 1;

				ps.setBigDecimal(index++, pd.getTdPftCpz());
				ps.setDate(index++, JdbcUtil.getDate(pd.getLastMdfDate()));
				ps.setLong(index, pd.getFinID());
			}

			@Override
			public int getBatchSize() {
				return pdList.size();
			}
		});
	}

	@Override
	public void save(FinanceProfitDetail pd) {
		StringBuilder sql = new StringBuilder("Insert Into FinPftDetails(");
		sql.append("FinID, FinReference, CustId, FinBranch, FinType, LastMdfDate, TotalPftSchd");
		sql.append(", TotalPftCpz, TotalPftPaid, TotalPftBal, TotalPftPaidInAdv, TotalPriPaid");
		sql.append(", TotalPriBal, TdSchdPft, TdPftCpz, TdSchdPftPaid, TdSchdPftBal, PftAccrued");
		sql.append(", PftAccrueSusp, PftAmz, PftAmzSusp, TdSchdPri, TdSchdPriPaid, TdSchdPriBal");
		sql.append(", AcrTillLBD, AmzTillLBD, LpiTillLBD, LppTillLBD, GstLpiTillLBD, GstLppTillLBD");
		sql.append(", RepayFrq, CustCIF, FinCcy, FinPurpose, FinContractDate");
		sql.append(", FinApprovedDate, FinStartDate, MaturityDate, FullPaidDate, FinAmount");
		sql.append(", DownPayment, CurReducingRate, CurFlatRate, TotalpriSchd, ODPrincipal, ODProfit");
		sql.append(", PenaltyPaid, PenaltyDue, PenaltyWaived, NSchdDate, NSchdPri, NSchdPft");
		sql.append(", NSchdPriDue, NSchdPftDue, PftInSusp, FinStatus, FinStsReason, FinWorstStatus");
		sql.append(", NOInst, NOPaidInst, NOODInst, FinCommitmentRef, FinIsActive, FirstRepayDate");
		sql.append(", FirstRepayAmt, FinalRepayAmt, CurODDays, ActualODDays, MaxODDays, FirstODDate, PrvODDate");
		sql.append(", ClosingStatus, FinCategory, PrvRpySchDate, PrvRpySchPri, PrvRpySchPft, LatestRpyDate");
		sql.append(", LatestRpyPri, LatestRpyPft, TotalWriteoff, AccumulatedDepPri, DepreciatePri");
		sql.append(", TotalPriPaidInAdv, PftAmzNormal, PftAmzPD");
		sql.append(", AmzTillLBDNormal, AmzTillLBDPD, AmzTillLBDPIS, CalPftOnPD, PftOnPDMethod");
		sql.append(", PftOnPDMrg, TotPftOnPD, TotPftOnPDPaid, TotPftOnPDWaived, TotPftOnPDDue");
		sql.append(", AcrSuspTillLBD, PrvMthAmz, PrvMthAmzNrm, PrvMthAmzPD, PrvMthAmzSusp, PrvMthAcr, PrvMthAcrSusp");
		sql.append(", FirstDisbDate, LatestDisbDate, FutureInst, RemainingTenor, TotalTenor, ProductCategory");
		sql.append(", ExcessAmt, EmiInAdvance, PayableAdvise, ExcessAmtResv, EmiInAdvanceResv, PayableAdviseResv");
		sql.append(", GapIntAmz, GapIntAmzLbd, PrvMthGapIntAmz, SvAmount, CbAmount , NOAutoIncGrcEnd");
		sql.append(" ) Values (");
		sql.append("?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?");
		sql.append(", ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?");
		sql.append(", ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?");
		sql.append(", ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?");
		sql.append(", ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?");
		sql.append(", ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?");
		sql.append(", ?, ?, ?, ?, ?, ?, ?, ?, ? , ?, ?, ?");
		sql.append(" )");

		logger.debug(Literal.SQL + sql);

		jdbcOperations.update(sql.toString(), ps -> {
			int index = 1;
			ps.setLong(index++, pd.getFinID());
			ps.setString(index++, pd.getFinReference());
			ps.setLong(index++, pd.getCustId());
			ps.setString(index++, pd.getFinBranch());
			ps.setString(index++, pd.getFinType());
			ps.setDate(index++, JdbcUtil.getDate(pd.getLastMdfDate()));
			ps.setBigDecimal(index++, pd.getTotalPftSchd());
			ps.setBigDecimal(index++, pd.getTotalPftCpz());
			ps.setBigDecimal(index++, pd.getTotalPftPaid());
			ps.setBigDecimal(index++, pd.getTotalPftBal());
			ps.setBigDecimal(index++, pd.getTotalPftPaidInAdv());
			ps.setBigDecimal(index++, pd.getTotalPriPaid());
			ps.setBigDecimal(index++, pd.getTotalPriBal());
			ps.setBigDecimal(index++, pd.getTdSchdPft());
			ps.setBigDecimal(index++, pd.getTdPftCpz());
			ps.setBigDecimal(index++, pd.getTdSchdPftPaid());
			ps.setBigDecimal(index++, pd.getTdSchdPftBal());
			ps.setBigDecimal(index++, pd.getPftAccrued());
			ps.setBigDecimal(index++, pd.getPftAccrueSusp());
			ps.setBigDecimal(index++, pd.getPftAmz());
			ps.setBigDecimal(index++, pd.getPftAmzSusp());
			ps.setBigDecimal(index++, pd.getTdSchdPri());
			ps.setBigDecimal(index++, pd.getTdSchdPriPaid());
			ps.setBigDecimal(index++, pd.getTdSchdPriBal());
			ps.setBigDecimal(index++, pd.getAcrTillLBD());
			ps.setBigDecimal(index++, pd.getAmzTillLBD());
			ps.setBigDecimal(index++, pd.getLpiTillLBD());
			ps.setBigDecimal(index++, pd.getLppTillLBD());
			ps.setBigDecimal(index++, pd.getGstLpiTillLBD());
			ps.setBigDecimal(index++, pd.getGstLppTillLBD());
			ps.setString(index++, pd.getRepayFrq());
			ps.setString(index++, pd.getCustCIF());
			ps.setString(index++, pd.getFinCcy());
			ps.setString(index++, pd.getFinPurpose());
			ps.setDate(index++, JdbcUtil.getDate(pd.getFinContractDate()));
			ps.setDate(index++, JdbcUtil.getDate(pd.getFinApprovedDate()));
			ps.setDate(index++, JdbcUtil.getDate(pd.getFinStartDate()));
			ps.setDate(index++, JdbcUtil.getDate(pd.getMaturityDate()));
			ps.setDate(index++, JdbcUtil.getDate(pd.getFullPaidDate()));
			ps.setBigDecimal(index++, pd.getFinAmount());
			ps.setBigDecimal(index++, pd.getDownPayment());
			ps.setBigDecimal(index++, pd.getCurReducingRate());
			ps.setBigDecimal(index++, pd.getCurFlatRate());
			ps.setBigDecimal(index++, pd.getTotalpriSchd());
			ps.setBigDecimal(index++, pd.getODPrincipal());
			ps.setBigDecimal(index++, pd.getODProfit());
			ps.setBigDecimal(index++, pd.getPenaltyPaid());
			ps.setBigDecimal(index++, pd.getPenaltyDue());
			ps.setBigDecimal(index++, pd.getPenaltyWaived());
			ps.setDate(index++, JdbcUtil.getDate(pd.getNSchdDate()));
			ps.setBigDecimal(index++, pd.getNSchdPri());
			ps.setBigDecimal(index++, pd.getNSchdPft());
			ps.setBigDecimal(index++, pd.getNSchdPriDue());
			ps.setBigDecimal(index++, pd.getNSchdPftDue());
			ps.setBoolean(index++, pd.isPftInSusp());
			ps.setString(index++, pd.getFinStatus());
			ps.setString(index++, pd.getFinStsReason());
			ps.setString(index++, pd.getFinWorstStatus());
			ps.setInt(index++, pd.getNOInst());
			ps.setInt(index++, pd.getNOPaidInst());
			ps.setInt(index++, pd.getNOODInst());
			ps.setString(index++, pd.getFinCommitmentRef());
			ps.setBoolean(index++, pd.getFinIsActive());
			ps.setDate(index++, JdbcUtil.getDate(pd.getFirstRepayDate()));
			ps.setBigDecimal(index++, pd.getFirstRepayAmt());
			ps.setBigDecimal(index++, pd.getFinalRepayAmt());
			ps.setInt(index++, pd.getCurODDays());
			ps.setInt(index++, pd.getActualODDays());
			ps.setInt(index++, pd.getMaxODDays());
			ps.setDate(index++, JdbcUtil.getDate(pd.getFirstODDate()));
			ps.setDate(index++, JdbcUtil.getDate(pd.getPrvODDate()));
			ps.setString(index++, pd.getClosingStatus());
			ps.setString(index++, pd.getFinCategory());
			ps.setDate(index++, JdbcUtil.getDate(pd.getPrvRpySchDate()));
			ps.setBigDecimal(index++, pd.getPrvRpySchPri());
			ps.setBigDecimal(index++, pd.getPrvRpySchPft());
			ps.setDate(index++, JdbcUtil.getDate(pd.getLatestRpyDate()));
			ps.setBigDecimal(index++, pd.getLatestRpyPri());
			ps.setBigDecimal(index++, pd.getLatestRpyPft());
			ps.setBigDecimal(index++, pd.getTotalWriteoff());
			ps.setBigDecimal(index++, pd.getAccumulatedDepPri());
			ps.setBigDecimal(index++, pd.getDepreciatePri());
			ps.setBigDecimal(index++, pd.getTotalPriPaidInAdv());
			ps.setBigDecimal(index++, pd.getPftAmzNormal());
			ps.setBigDecimal(index++, pd.getPftAmzPD());
			ps.setBigDecimal(index++, pd.getAmzTillLBDNormal());
			ps.setBigDecimal(index++, pd.getAmzTillLBDPD());
			ps.setBigDecimal(index++, pd.getAmzTillLBDPIS());
			ps.setBoolean(index++, pd.isCalPftOnPD());
			ps.setString(index++, pd.getPftOnPDMethod());
			ps.setBigDecimal(index++, pd.getPftOnPDMrg());
			ps.setBigDecimal(index++, pd.getTotPftOnPD());
			ps.setBigDecimal(index++, pd.getTotPftOnPDPaid());
			ps.setBigDecimal(index++, pd.getTotPftOnPDWaived());
			ps.setBigDecimal(index++, pd.getTotPftOnPDDue());
			ps.setBigDecimal(index++, pd.getAcrSuspTillLBD());
			ps.setBigDecimal(index++, pd.getPrvMthAmz());
			ps.setBigDecimal(index++, pd.getPrvMthAmzNrm());
			ps.setBigDecimal(index++, pd.getPrvMthAmzPD());
			ps.setBigDecimal(index++, pd.getPrvMthAmzSusp());
			ps.setBigDecimal(index++, pd.getPrvMthAcr());
			ps.setBigDecimal(index++, pd.getPrvMthAcrSusp());
			ps.setDate(index++, JdbcUtil.getDate(pd.getFirstDisbDate()));
			ps.setDate(index++, JdbcUtil.getDate(pd.getLatestDisbDate()));
			ps.setInt(index++, pd.getFutureInst());
			ps.setInt(index++, pd.getRemainingTenor());
			ps.setInt(index++, pd.getTotalTenor());
			ps.setString(index++, pd.getProductCategory());
			ps.setBigDecimal(index++, pd.getExcessAmt());
			ps.setBigDecimal(index++, pd.getEmiInAdvance());
			ps.setBigDecimal(index++, pd.getPayableAdvise());
			ps.setBigDecimal(index++, pd.getExcessAmtResv());
			ps.setBigDecimal(index++, pd.getEmiInAdvanceResv());
			ps.setBigDecimal(index++, pd.getPayableAdviseResv());
			ps.setBigDecimal(index++, pd.getGapIntAmz());
			ps.setBigDecimal(index++, pd.getGapIntAmzLbd());
			ps.setBigDecimal(index++, pd.getPrvMthGapIntAmz());
			ps.setBigDecimal(index++, pd.getSvAmount());
			ps.setBigDecimal(index++, pd.getCbAmount());
			ps.setInt(index, pd.getNOAutoIncGrcEnd());
		});
	}

	@Override
	public BigDecimal getAccrueAmount(long finID) {
		String sql = "Select PftAccrued From FinPftDetails Where FinID = ?";

		logger.debug(Literal.SQL + sql);

		try {
			return this.jdbcOperations.queryForObject(sql, BigDecimal.class, finID);
		} catch (EmptyResultDataAccessException e) {
			logger.warn(Message.NO_RECORD_FOUND);
			return BigDecimal.ZERO;
		}
	}

	@Override
	public void updateLatestRpyDetails(FinanceProfitDetail pd) {
		String sql = "Update FinPftDetails Set LatestRpyDate = ?, LatestRpyPri = ?, LatestRpyPft = ?  Where FinID = ?";

		logger.debug(Literal.SQL + sql);

		this.jdbcOperations.update(sql, ps -> {
			int index = 1;

			ps.setDate(index++, JdbcUtil.getDate(pd.getLatestRpyDate()));
			ps.setBigDecimal(index++, pd.getLatestRpyPri());
			ps.setBigDecimal(index++, pd.getLatestRpyPft());

			ps.setLong(index, pd.getFinID());
		});
	}

	@Override
	public void UpdateActiveSts(long finID, boolean isActive) {
		String sql = "Update FinPftDetails Set FinIsActive = ? Where FinID = ?";

		logger.debug(Literal.SQL + sql);

		this.jdbcOperations.update(sql, ps -> {
			int index = 1;

			ps.setBoolean(index++, isActive);
			ps.setLong(index, finID);
		});
	}

	@Override
	public void updateEOD(FinanceProfitDetail fpd, boolean posted, boolean monthend) {
		StringBuilder sql = new StringBuilder();
		sql.append("Update FinPftDetails set");
		sql.append(" PftAccrued = ?, PftAccrueSusp = ?, PftAmz = ?, PftAmzSusp = ?, PftAmzNormal = ?");
		sql.append(", PftAmzPD = ?, PftInSusp = ?, CurFlatRate = ?, CurReducingRate = ?, TotalPftSchd = ?");
		sql.append(", TotalPftCpz = ?, TotalPftPaid = ?, TotalPftBal = ?, TdSchdPft = ?, TdPftCpz = ?");
		sql.append(", TdSchdPftPaid = ?, TdSchdPftBal = ?, TotalpriSchd = ?, TotalPriPaid = ?, TotalPriBal = ?");
		sql.append(", TdSchdPri = ?, TdSchdPriPaid = ?, TdSchdPriBal = ?, CalPftOnPD = ?, PftOnPDMethod = ?");
		sql.append(", PftOnPDMrg = ?, TotPftOnPD = ?, TotPftOnPDPaid = ?, TotPftOnPDWaived = ?, TotPftOnPDDue = ?");
		sql.append(", NOInst = ?, NOPaidInst = ?, NOODInst = ?, FutureInst = ?, RemainingTenor = ?");
		sql.append(", TotalTenor = ?, ODPrincipal = ?, ODProfit = ?, CurODDays = ?, ActualODDays = ?");
		sql.append(", MaxODDays = ?, FirstODDate = ?, PrvODDate = ?, PenaltyPaid = ?, PenaltyDue = ?");
		sql.append(", PenaltyWaived = ?, FirstRepayDate = ?, FirstRepayAmt = ?, FinalRepayAmt = ?");
		sql.append(", FirstDisbDate = ?, LatestDisbDate = ?, FullPaidDate = ?, PrvRpySchDate = ?");
		sql.append(", PrvRpySchPri = ?, PrvRpySchPft = ?, NSchdDate = ?, NSchdPri = ?, NSchdPft = ?");
		sql.append(", NSchdPriDue = ?, NSchdPftDue = ?, AccumulatedDepPri = ?, DepreciatePri = ?");
		sql.append(", TotalPriPaidInAdv = ?");
		sql.append(", FinStatus = ?, FinStsReason = ?, FinWorstStatus = ?, TotalPftPaidInAdv = ?");
		sql.append(", LastMdfDate = ?, AMZMethod = ?, GapIntAmz = ?, NOAutoIncGrcEnd = ?");

		if (posted) {
			sql.append(", AmzTillLBD = ?, LpiTillLBD = ?, LppTillLBD = ?, GstLpiTillLBD = ?, GstLppTillLBD = ?");
			sql.append(", AmzTillLBDNormal = ?, AmzTillLBDPD = ?, AmzTillLBDPIS = ?, AcrTillLBD = ?");
			sql.append(", AcrSuspTillLBD = ?, GapIntAmzLbd = ?");
		}

		if (monthend) {
			sql.append(", PrvMthAmz = ?, PrvMthAmzNrm = ?, PrvMthAmzPD = ?, PrvMthAmzSusp = ?, PrvMthAcr = ?");
			sql.append(", PrvMthAcrSusp = ?, PrvMthGapIntAmz = ?");
		}
		sql.append(" Where FinID = ?");

		jdbcOperations.update(sql.toString(), ps -> {

			int index = 1;

			ps.setBigDecimal(index++, fpd.getPftAccrued());
			ps.setBigDecimal(index++, fpd.getPftAccrueSusp());
			ps.setBigDecimal(index++, fpd.getPftAmz());
			ps.setBigDecimal(index++, fpd.getPftAmzSusp());
			ps.setBigDecimal(index++, fpd.getPftAmzNormal());
			ps.setBigDecimal(index++, fpd.getPftAmzPD());
			ps.setBoolean(index++, fpd.isPftInSusp());
			ps.setBigDecimal(index++, fpd.getCurFlatRate());
			ps.setBigDecimal(index++, fpd.getCurReducingRate());
			ps.setBigDecimal(index++, fpd.getTotalPftSchd());
			ps.setBigDecimal(index++, fpd.getTotalPftCpz());
			ps.setBigDecimal(index++, fpd.getTotalPftPaid());
			ps.setBigDecimal(index++, fpd.getTotalPftBal());
			ps.setBigDecimal(index++, fpd.getTdSchdPft());
			ps.setBigDecimal(index++, fpd.getTdPftCpz());
			ps.setBigDecimal(index++, fpd.getTdSchdPftPaid());
			ps.setBigDecimal(index++, fpd.getTdSchdPftBal());
			ps.setBigDecimal(index++, fpd.getTotalpriSchd());
			ps.setBigDecimal(index++, fpd.getTotalPriPaid());
			ps.setBigDecimal(index++, fpd.getTotalPriBal());
			ps.setBigDecimal(index++, fpd.getTdSchdPri());
			ps.setBigDecimal(index++, fpd.getTdSchdPriPaid());
			ps.setBigDecimal(index++, fpd.getTdSchdPriBal());
			ps.setBoolean(index++, fpd.isCalPftOnPD());
			ps.setString(index++, fpd.getPftOnPDMethod());
			ps.setBigDecimal(index++, fpd.getPftOnPDMrg());
			ps.setBigDecimal(index++, fpd.getTotPftOnPD());
			ps.setBigDecimal(index++, fpd.getTotPftOnPDPaid());
			ps.setBigDecimal(index++, fpd.getTotPftOnPDWaived());
			ps.setBigDecimal(index++, fpd.getTotPftOnPDDue());
			ps.setInt(index++, fpd.getNOInst());
			ps.setInt(index++, fpd.getNOPaidInst());
			ps.setInt(index++, fpd.getNOODInst());
			ps.setInt(index++, fpd.getFutureInst());
			ps.setInt(index++, fpd.getRemainingTenor());
			ps.setInt(index++, fpd.getTotalTenor());
			ps.setBigDecimal(index++, fpd.getODPrincipal());
			ps.setBigDecimal(index++, fpd.getODProfit());
			ps.setInt(index++, fpd.getCurODDays());
			ps.setInt(index++, fpd.getActualODDays());
			ps.setInt(index++, fpd.getMaxODDays());
			ps.setDate(index++, JdbcUtil.getDate(fpd.getFirstODDate()));
			ps.setDate(index++, JdbcUtil.getDate(fpd.getPrvODDate()));
			ps.setBigDecimal(index++, fpd.getPenaltyPaid());
			ps.setBigDecimal(index++, fpd.getPenaltyDue());
			ps.setBigDecimal(index++, fpd.getPenaltyWaived());
			ps.setDate(index++, JdbcUtil.getDate(fpd.getFirstRepayDate()));
			ps.setBigDecimal(index++, fpd.getFirstRepayAmt());
			ps.setBigDecimal(index++, fpd.getFinalRepayAmt());
			ps.setDate(index++, JdbcUtil.getDate(fpd.getFirstDisbDate()));
			ps.setDate(index++, JdbcUtil.getDate(fpd.getLatestDisbDate()));
			ps.setDate(index++, JdbcUtil.getDate(fpd.getFullPaidDate()));
			ps.setDate(index++, JdbcUtil.getDate(fpd.getPrvRpySchDate()));
			ps.setBigDecimal(index++, fpd.getPrvRpySchPri());
			ps.setBigDecimal(index++, fpd.getPrvRpySchPft());
			ps.setDate(index++, JdbcUtil.getDate(fpd.getNSchdDate()));
			ps.setBigDecimal(index++, fpd.getNSchdPri());
			ps.setBigDecimal(index++, fpd.getNSchdPft());
			ps.setBigDecimal(index++, fpd.getNSchdPriDue());
			ps.setBigDecimal(index++, fpd.getNSchdPftDue());
			ps.setBigDecimal(index++, fpd.getAccumulatedDepPri());
			ps.setBigDecimal(index++, fpd.getDepreciatePri());
			ps.setBigDecimal(index++, fpd.getTotalPriPaidInAdv());
			ps.setString(index++, fpd.getFinStatus());
			ps.setString(index++, fpd.getFinStsReason());
			ps.setString(index++, fpd.getFinWorstStatus());
			ps.setBigDecimal(index++, fpd.getTotalPftPaidInAdv());
			ps.setDate(index++, JdbcUtil.getDate(fpd.getLastMdfDate()));
			ps.setString(index++, fpd.getAMZMethod());
			ps.setBigDecimal(index++, fpd.getGapIntAmz());
			ps.setInt(index++, fpd.getNOAutoIncGrcEnd());

			if (posted) {
				ps.setBigDecimal(index++, fpd.getAmzTillLBD());
				ps.setBigDecimal(index++, fpd.getLpiTillLBD());
				ps.setBigDecimal(index++, fpd.getLppTillLBD());
				ps.setBigDecimal(index++, fpd.getGstLpiTillLBD());
				ps.setBigDecimal(index++, fpd.getGstLppTillLBD());
				ps.setBigDecimal(index++, fpd.getAmzTillLBDNormal());
				ps.setBigDecimal(index++, fpd.getAmzTillLBDPD());
				ps.setBigDecimal(index++, fpd.getAmzTillLBDPIS());
				ps.setBigDecimal(index++, fpd.getAcrTillLBD());
				ps.setBigDecimal(index++, fpd.getAcrSuspTillLBD());
				ps.setBigDecimal(index++, fpd.getGapIntAmzLbd());
			}

			if (monthend) {
				ps.setBigDecimal(index++, fpd.getPrvMthAmz());
				ps.setBigDecimal(index++, fpd.getPrvMthAmzNrm());
				ps.setBigDecimal(index++, fpd.getPrvMthAmzPD());
				ps.setBigDecimal(index++, fpd.getPrvMthAmzSusp());
				ps.setBigDecimal(index++, fpd.getPrvMthAcr());
				ps.setBigDecimal(index++, fpd.getPrvMthAcrSusp());
				ps.setBigDecimal(index++, fpd.getPrvMthGapIntAmz());
			}

			ps.setLong(index, fpd.getFinID());

		});

	}

	@Override
	public int getCurOddays(long finID) {
		String sql = "Select CurOdDays From FinPftDetails Where FinID = ?";

		logger.debug(Literal.SQL + sql);

		try {
			return this.jdbcOperations.queryForObject(sql, Integer.class, finID);
		} catch (EmptyResultDataAccessException e) {
			logger.warn(Message.NO_RECORD_FOUND);
			return 0;
		}
	}

	@Override
	public boolean isSuspenseFinance(long finID) {
		String sql = "Select PftInSusp From FinPftDetails Where FinID = ?";

		logger.debug(Literal.SQL + sql);

		try {
			return this.jdbcOperations.queryForObject(sql, Boolean.class, finID);
		} catch (EmptyResultDataAccessException e) {
			logger.warn(Message.NO_RECORD_FOUND);
			return false;
		}
	}

	@Override
	public BigDecimal getTotalCustomerExposre(long custId) {
		StringBuilder sql = new StringBuilder("Select");
		sql.append(" coalesce(sum((NSchdPri + NSchdPft)), 0)");
		sql.append(" From FinPftDetails");
		sql.append(" Where FinID in (Select FinID from FinanceMain Where CustID = ?)");

		logger.debug(Literal.SQL + sql.toString());

		return this.jdbcOperations.queryForObject(sql.toString(), BigDecimal.class, custId);
	}

	@Override
	public BigDecimal getTotalCoApplicantsExposre(String finReference) {
		StringBuilder sql = new StringBuilder("Select");
		sql.append(" coalesce(sum((NSchdPri + NSchdPft)), 0)");
		sql.append(" From FinPftDetails");
		sql.append(" Where FinID in (Select FinID from FinanceMain Where CustID in (");
		sql.append(" Select CustID from FinjointAccountDetails_View Where FinReference = ?))");

		logger.debug(Literal.SQL + sql.toString());

		return this.jdbcOperations.queryForObject(sql.toString(), BigDecimal.class, finReference);
	}

	@Override
	public void updateFinPftMaturity(long finID, String closingStatus, boolean finIsActive) {
		String sql = "Update finpftdetails Set FinIsActive = ?, ClosingStatus = ? Where FinID = ?";

		logger.debug(Literal.SQL + sql);

		this.jdbcOperations.update(sql, ps -> {
			int index = 1;
			ps.setBoolean(index++, finIsActive);
			ps.setString(index++, closingStatus);
			ps.setLong(index, finID);
		});
	}

	@Override
	public Date getFirstRePayDateByFinRef(long finID) {
		String sql = "Select FirstRepayDate From FinPftDetails Where FinID = ?";

		logger.debug(Literal.SQL + sql);

		try {
			return this.jdbcOperations.queryForObject(sql, Date.class, finID);
		} catch (EmptyResultDataAccessException e) {
			logger.warn(Message.NO_RECORD_FOUND);
			return null;
		}
	}

	@Override
	public BigDecimal getMaxRpyAmount(long finID) {
		String sql = "Select MaxRpyAmount From FinPftDetails Where FinID = ?";

		logger.debug(Literal.SQL + sql);

		try {
			return this.jdbcOperations.queryForObject(sql, BigDecimal.class, finID);
		} catch (EmptyResultDataAccessException e) {
			logger.warn(Message.NO_RECORD_FOUND);
			return BigDecimal.ZERO;
		}
	}

	@Override
	public List<FinanceProfitDetail> getFinProfitListByFinRefList(List<Long> finIDList) {
		StringBuilder sql = new StringBuilder("Select");
		sql.append(" FinID, FinReference, NSchdPri, NSchdPft, curODDays");
		sql.append(" from FinPftDetails");
		sql.append(" where FinID in (");

		int i = 0;

		if (CollectionUtils.isNotEmpty(finIDList)) {
			while (i < finIDList.size()) {
				sql.append(" ?,");
				i++;
			}
			sql.deleteCharAt(sql.length() - 1);
		}

		sql.append(")");

		logger.debug(Literal.SQL + sql.toString());

		return this.jdbcOperations.query(sql.toString(), ps -> {
			int index = 1;

			for (long finID : finIDList) {
				ps.setLong(index++, finID);
			}

		}, (rs, rowNum) -> {
			FinanceProfitDetail fpd = new FinanceProfitDetail();

			fpd.setFinID(rs.getLong("FinID"));
			fpd.setFinReference(rs.getString("FinReference"));
			fpd.setNSchdPri(rs.getBigDecimal("NSchdPri"));
			fpd.setNSchdPft(rs.getBigDecimal("NSchdPft"));
			fpd.setCurODDays(rs.getInt("curODDays"));

			return fpd;
		});
	}

	@Override
	public void updateAssignmentBPIAmounts(FinanceProfitDetail pd) {
		String sql = "Update FinPftDetails Set AssignBPI1 = ?, AssignBPI2 = ? Where FinID = ?";

		this.jdbcOperations.update(sql, ps -> {
			int index = 1;
			ps.setBigDecimal(index++, pd.getAssignBPI1());
			ps.setBigDecimal(index++, pd.getAssignBPI2());
			ps.setLong(index, pd.getFinID());
		});

	}

	@Override
	public List<FinanceProfitDetail> getFinPftListForIncomeAMZ(Date curMonthStart) {
		StringBuilder sql = new StringBuilder("Select");
		sql.append(" FinID, FinReference, CustId, FinBranch, FinType, FinCcy, LastMdfDate, FinIsActive");
		sql.append(", TotalpriSchd, TotalPftSchd, TotalPftCpz, TotalPftPaid, TotalPftBal, TotalPriPaid");
		sql.append(", TotalPriBal, TdSchdPft, TdPftCpz, TdSchdPftPaid, TdSchdPftBal, PftAccrued, PftAccrueSusp");
		sql.append(", PftAmz, PftAmzSusp, TdSchdPri, TdSchdPriPaid, TdSchdPriBal, PrvMthAmz, ClosingStatus");
		sql.append(", FinCategory, TotalWriteoff, ODPrincipal, ODProfit, CurODDays, ActualODDays, FinStartDate");
		sql.append(", MaturityDate, LatestRpyDate, GapIntAmz, GapIntAmzLbd, PrvMthGapIntAmz, WriteoffLoan");
		sql.append(" from FinPftDetails");
		sql.append(" Where MaturityDate >= ?");

		logger.debug(Literal.SQL + sql.toString());

		return this.jdbcOperations.query(sql.toString(), ps -> {
			int index = 1;
			ps.setDate(index, JdbcUtil.getDate(curMonthStart));
		}, (rs, rowNum) -> {
			FinanceProfitDetail fpd = new FinanceProfitDetail();

			fpd.setFinID(rs.getLong("FinID"));
			fpd.setFinReference(rs.getString("FinReference"));
			fpd.setCustId(rs.getLong("CustId"));
			fpd.setFinBranch(rs.getString("FinBranch"));
			fpd.setFinType(rs.getString("FinType"));
			fpd.setFinCcy(rs.getString("FinCcy"));
			fpd.setLastMdfDate(rs.getTimestamp("LastMdfDate"));
			fpd.setFinIsActive(rs.getBoolean("FinIsActive"));
			fpd.setTotalpriSchd(rs.getBigDecimal("totalpriSchd"));
			fpd.setTotalPftSchd(rs.getBigDecimal("TotalPftSchd"));
			fpd.setTotalPftCpz(rs.getBigDecimal("TotalPftCpz"));
			fpd.setTotalPftPaid(rs.getBigDecimal("TotalPftPaid"));
			fpd.setTotalPftBal(rs.getBigDecimal("TotalPftBal"));
			fpd.setTotalPriPaid(rs.getBigDecimal("TotalPriPaid"));
			fpd.setTotalPriBal(rs.getBigDecimal("TotalPriBal"));
			fpd.setTdSchdPft(rs.getBigDecimal("TdSchdPft"));
			fpd.setTdPftCpz(rs.getBigDecimal("TdPftCpz"));
			fpd.setTdSchdPftPaid(rs.getBigDecimal("TdSchdPftPaid"));
			fpd.setTdSchdPftBal(rs.getBigDecimal("TdSchdPftBal"));
			fpd.setPftAccrued(rs.getBigDecimal("PftAccrued"));
			fpd.setPftAccrueSusp(rs.getBigDecimal("PftAccrueSusp"));
			fpd.setPftAmz(rs.getBigDecimal("PftAmz"));
			fpd.setPftAmzSusp(rs.getBigDecimal("PftAmzSusp"));
			fpd.setTdSchdPri(rs.getBigDecimal("TdSchdPri"));
			fpd.setTdSchdPriPaid(rs.getBigDecimal("TdSchdPriPaid"));
			fpd.setTdSchdPriBal(rs.getBigDecimal("TdSchdPriBal"));
			fpd.setPrvMthAmz(rs.getBigDecimal("PrvMthAmz"));
			fpd.setClosingStatus(rs.getString("ClosingStatus"));
			fpd.setFinCategory(rs.getString("FinCategory"));
			fpd.setTotalWriteoff(rs.getBigDecimal("TotalWriteoff"));
			fpd.setODPrincipal(rs.getBigDecimal("ODPrincipal"));
			fpd.setODProfit(rs.getBigDecimal("ODProfit"));
			fpd.setCurODDays(rs.getInt("CurODDays"));
			fpd.setActualODDays(rs.getInt("ActualODDays"));
			fpd.setFinStartDate(rs.getTimestamp("FinStartDate"));
			fpd.setMaturityDate(rs.getTimestamp("MaturityDate"));
			fpd.setLatestRpyDate(rs.getTimestamp("LatestRpyDate"));
			fpd.setGapIntAmz(rs.getBigDecimal("GapIntAmz"));
			fpd.setGapIntAmzLbd(rs.getBigDecimal("GapIntAmzLbd"));
			fpd.setPrvMthGapIntAmz(rs.getBigDecimal("PrvMthGapIntAmz"));
			fpd.setWriteoffLoan(rs.getBoolean("WriteoffLoan"));

			return fpd;
		});
	}

	@Override
	public FinanceProfitDetail getFinProfitForAMZ(long finID) {
		logger.debug(Literal.ENTERING);

		StringBuilder sql = new StringBuilder("Select");
		sql.append(" FinID, FinReference, CustId, FinBranch, FinType, FinCcy, LastMdfDate, FinIsActive");
		sql.append(", TotalpriSchd, TotalPftSchd, TotalPftCpz, TotalPftPaid, TotalPftBal, TotalPriPaid");
		sql.append(", TotalPriBal, TdSchdPft, TdPftCpz, TdSchdPftPaid, TdSchdPftBal, PftAccrued, PftAccrueSusp");
		sql.append(", PftAmz, PftAmzSusp, TdSchdPri, TdSchdPriPaid, TdSchdPriBal, PrvMthAmz, ClosingStatus");
		sql.append(", FinCategory, TotalWriteoff, ODPrincipal, ODProfit, CurODDays, ActualODDays, FinStartDate");
		sql.append(", MaturityDate, LatestRpyDate, GapIntAmz, GapIntAmzLbd, PrvMthGapIntAmz, WriteoffLoan");
		sql.append(" from FinPftDetails");
		sql.append(" Where FinID = ?");

		logger.debug(Literal.SQL + sql.toString());

		try {
			return this.jdbcOperations.queryForObject(sql.toString(), (rs, rowNum) -> {
				FinanceProfitDetail fpd = new FinanceProfitDetail();

				fpd.setFinID(rs.getLong("FinID"));
				fpd.setFinReference(rs.getString("FinReference"));
				fpd.setCustId(rs.getLong("CustId"));
				fpd.setFinBranch(rs.getString("FinBranch"));
				fpd.setFinType(rs.getString("FinType"));
				fpd.setFinCcy(rs.getString("FinCcy"));
				fpd.setLastMdfDate(rs.getTimestamp("LastMdfDate"));
				fpd.setFinIsActive(rs.getBoolean("FinIsActive"));
				fpd.setTotalpriSchd(rs.getBigDecimal("TotalpriSchd"));
				fpd.setTotalPftSchd(rs.getBigDecimal("TotalPftSchd"));
				fpd.setTotalPftCpz(rs.getBigDecimal("TotalPftCpz"));
				fpd.setTotalPftPaid(rs.getBigDecimal("TotalPftPaid"));
				fpd.setTotalPftBal(rs.getBigDecimal("TotalPftBal"));
				fpd.setTotalPriPaid(rs.getBigDecimal("TotalPriPaid"));
				fpd.setTotalPriBal(rs.getBigDecimal("TotalPriBal"));
				fpd.setTdSchdPft(rs.getBigDecimal("TdSchdPft"));
				fpd.setTdPftCpz(rs.getBigDecimal("TdPftCpz"));
				fpd.setTdSchdPftPaid(rs.getBigDecimal("TdSchdPftPaid"));
				fpd.setTdSchdPftBal(rs.getBigDecimal("TdSchdPftBal"));
				fpd.setPftAccrued(rs.getBigDecimal("PftAccrued"));
				fpd.setPftAccrueSusp(rs.getBigDecimal("PftAccrueSusp"));
				fpd.setPftAmz(rs.getBigDecimal("PftAmz"));
				fpd.setPftAmzSusp(rs.getBigDecimal("PftAmzSusp"));
				fpd.setTdSchdPri(rs.getBigDecimal("TdSchdPri"));
				fpd.setTdSchdPriPaid(rs.getBigDecimal("TdSchdPriPaid"));
				fpd.setTdSchdPriBal(rs.getBigDecimal("TdSchdPriBal"));
				fpd.setPrvMthAmz(rs.getBigDecimal("PrvMthAmz"));
				fpd.setClosingStatus(rs.getString("ClosingStatus"));
				fpd.setFinCategory(rs.getString("FinCategory"));
				fpd.setTotalWriteoff(rs.getBigDecimal("TotalWriteoff"));
				fpd.setODPrincipal(rs.getBigDecimal("ODPrincipal"));
				fpd.setODProfit(rs.getBigDecimal("ODProfit"));
				fpd.setCurODDays(rs.getInt("CurODDays"));
				fpd.setActualODDays(rs.getInt("ActualODDays"));
				fpd.setFinStartDate(rs.getTimestamp("FinStartDate"));
				fpd.setMaturityDate(rs.getTimestamp("MaturityDate"));
				fpd.setLatestRpyDate(rs.getTimestamp("LatestRpyDate"));
				fpd.setGapIntAmz(rs.getBigDecimal("GapIntAmz"));
				fpd.setGapIntAmzLbd(rs.getBigDecimal("GapIntAmzLbd"));
				fpd.setPrvMthGapIntAmz(rs.getBigDecimal("PrvMthGapIntAmz"));
				fpd.setWriteoffLoan(rs.getBoolean("WriteoffLoan"));

				return fpd;

			}, finID);
		} catch (EmptyResultDataAccessException e) {
			logger.warn(Message.NO_RECORD_FOUND);
			return null;
		}
	}

	@Override
	public void updateAMZMethod(long finID, String amzMethod) {
		String sql = "Update FinPftDetails Set AMZMethod = ? Where FinID = ?";

		logger.debug(Literal.SQL + sql);

		this.jdbcOperations.update(sql, ps -> {
			int index = 1;

			ps.setString(index++, amzMethod);
			ps.setLong(index, finID);

		});
	}

	@Override
	public void updateSchPaid(FinanceProfitDetail pd) {
		String sql = "Update FinPftDetails Set TotalPftPaid = ?, TotalPriPaid = ? Where FinID = ?";

		logger.debug(Literal.SQL + sql);

		int recordCount = this.jdbcOperations.update(sql, ps -> {
			int index = 1;

			ps.setBigDecimal(index++, pd.getTotalPftPaid());
			ps.setBigDecimal(index++, pd.getTotalPriPaid());
			ps.setLong(index, pd.getFinID());
		});

		if (recordCount <= 0) {
			throw new ConcurrencyException();
		}

	}

	public class ProfitDetailRowMapper implements RowMapper<FinanceProfitDetail> {

		@Override
		public FinanceProfitDetail mapRow(ResultSet rs, int rowNum) throws SQLException {
			FinanceProfitDetail pftd = new FinanceProfitDetail();

			pftd.setFinID(rs.getLong("FinID"));
			pftd.setFinReference(rs.getString("FinReference"));
			pftd.setCustId(rs.getLong("CustId"));
			pftd.setFinBranch(rs.getString("FinBranch"));
			pftd.setFinType(rs.getString("FinType"));
			pftd.setFinCcy(rs.getString("FinCcy"));
			pftd.setLastMdfDate(rs.getTimestamp("LastMdfDate"));
			pftd.setFinIsActive(rs.getBoolean("FinIsActive"));
			pftd.setTotalPftSchd(rs.getBigDecimal("TotalPftSchd"));
			pftd.setTotalPftCpz(rs.getBigDecimal("TotalPftCpz"));
			pftd.setTotalPftPaid(rs.getBigDecimal("TotalPftPaid"));
			pftd.setTotalPftBal(rs.getBigDecimal("TotalPftBal"));
			pftd.setTotalPftPaidInAdv(rs.getBigDecimal("TotalPftPaidInAdv"));
			pftd.setTotalPriPaid(rs.getBigDecimal("TotalPriPaid"));
			pftd.setTotalPriBal(rs.getBigDecimal("TotalPriBal"));
			pftd.setTdSchdPft(rs.getBigDecimal("TdSchdPft"));
			pftd.setTdPftCpz(rs.getBigDecimal("TdPftCpz"));
			pftd.setTdSchdPftPaid(rs.getBigDecimal("TdSchdPftPaid"));
			pftd.setTdSchdPftBal(rs.getBigDecimal("TdSchdPftBal"));
			pftd.setPftAccrued(rs.getBigDecimal("PftAccrued"));
			pftd.setPftAccrueSusp(rs.getBigDecimal("PftAccrueSusp"));
			pftd.setPftAmz(rs.getBigDecimal("PftAmz"));
			pftd.setPftAmzSusp(rs.getBigDecimal("PftAmzSusp"));
			pftd.setTdSchdPri(rs.getBigDecimal("TdSchdPri"));
			pftd.setTdSchdPriPaid(rs.getBigDecimal("TdSchdPriPaid"));
			pftd.setTdSchdPriBal(rs.getBigDecimal("TdSchdPriBal"));
			pftd.setAcrTillLBD(rs.getBigDecimal("AcrTillLBD"));
			pftd.setAmzTillLBD(rs.getBigDecimal("AmzTillLBD"));
			pftd.setLpiTillLBD(rs.getBigDecimal("LpiTillLBD"));
			pftd.setLppTillLBD(rs.getBigDecimal("LppTillLBD"));
			pftd.setGstLpiTillLBD(rs.getBigDecimal("GstLpiTillLBD"));
			pftd.setGstLppTillLBD(rs.getBigDecimal("GstLppTillLBD"));
			pftd.setFinWorstStatus(rs.getString("FinWorstStatus"));
			pftd.setFinStatus(rs.getString("FinStatus"));
			pftd.setFinStsReason(rs.getString("FinStsReason"));
			pftd.setClosingStatus(rs.getString("ClosingStatus"));
			pftd.setFinCategory(rs.getString("FinCategory"));
			pftd.setPrvRpySchDate(rs.getTimestamp("PrvRpySchDate"));
			pftd.setNSchdDate(rs.getTimestamp("NSchdDate"));
			pftd.setPrvRpySchPri(rs.getBigDecimal("PrvRpySchPri"));
			pftd.setPrvRpySchPft(rs.getBigDecimal("PrvRpySchPft"));
			pftd.setLatestRpyDate(rs.getTimestamp("LatestRpyDate"));
			pftd.setLatestRpyPri(rs.getBigDecimal("LatestRpyPri"));
			pftd.setLatestRpyPft(rs.getBigDecimal("LatestRpyPft"));
			pftd.setTotalWriteoff(rs.getBigDecimal("TotalWriteoff"));
			pftd.setFirstODDate(rs.getTimestamp("FirstODDate"));
			pftd.setPrvODDate(rs.getTimestamp("PrvODDate"));
			pftd.setODPrincipal(rs.getBigDecimal("ODPrincipal"));
			pftd.setODProfit(rs.getBigDecimal("ODProfit"));
			pftd.setCurODDays(rs.getInt("CurODDays"));
			pftd.setActualODDays(rs.getInt("ActualODDays"));
			pftd.setFinStartDate(rs.getTimestamp("FinStartDate"));
			pftd.setFullPaidDate(rs.getTimestamp("FullPaidDate"));
			pftd.setExcessAmt(rs.getBigDecimal("ExcessAmt"));
			pftd.setEmiInAdvance(rs.getBigDecimal("EmiInAdvance"));
			pftd.setPayableAdvise(rs.getBigDecimal("PayableAdvise"));
			pftd.setExcessAmtResv(rs.getBigDecimal("ExcessAmtResv"));
			pftd.setEmiInAdvanceResv(rs.getBigDecimal("EmiInAdvanceResv"));
			pftd.setPayableAdviseResv(rs.getBigDecimal("PayableAdviseResv"));
			pftd.setAMZMethod(rs.getString("AMZMethod"));
			pftd.setGapIntAmz(rs.getBigDecimal("GapIntAmz"));
			pftd.setGapIntAmzLbd(rs.getBigDecimal("GapIntAmzLbd"));
			pftd.setSvAmount(rs.getBigDecimal("SvAmount"));
			pftd.setCbAmount(rs.getBigDecimal("CbAmount"));
			pftd.setNOPaidInst(rs.getInt("NOPaidInst"));
			pftd.setNOAutoIncGrcEnd(rs.getInt("NOAutoIncGrcEnd"));
			pftd.setWriteoffLoan(rs.getBoolean("WriteoffLoan"));
			pftd.setTotalpriSchd(rs.getBigDecimal("TotalpriSchd"));
			pftd.setMaturityDate(rs.getTimestamp("MaturityDate"));
			pftd.setProductCategory(rs.getString("ProductCategory"));
			pftd.setPrvMthAmz(rs.getBigDecimal("PrvMthAmz"));
			pftd.setPenaltyPaid(rs.getBigDecimal("PenaltyPaid"));
			pftd.setPenaltyDue(rs.getBigDecimal("PenaltyDue"));
			pftd.setPrvMthGapIntAmz(rs.getBigDecimal("PrvMthGapIntAmz"));
			pftd.setFirstRepayDate(rs.getTimestamp("FirstRepayDate"));
			pftd.setPrvMthAcr(rs.getBigDecimal("PrvMthAcr"));

			return pftd;
		}
	}

	@Override
	public void updateClosingSts(long finID, boolean writeoffLoan) {
		String sql = "Update FinPftDetails Set WriteoffLoan = ? Where FinID = ?";

		logger.debug(Literal.SQL + sql);

		this.jdbcOperations.update(sql, ps -> {
			ps.setBoolean(1, writeoffLoan);
			ps.setLong(2, finID);
		});
	}

	@Override
	public BigDecimal getOverDueAmount(long finID) {
		String sql = "Select (ODPrincipal + ODProfit) TotalDue From FinPftDetails Where FinID = ?";

		logger.debug(Literal.SQL.concat(sql));

		return this.jdbcOperations.queryForObject(sql, BigDecimal.class, finID);
	}

}
