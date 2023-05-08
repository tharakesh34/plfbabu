package com.pennant.backend.dao.reports.impl;

import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.RowMapper;

import com.pennant.backend.dao.reports.ReschedulReportGenerationDAO;
import com.pennant.backend.model.finance.FinLogEntryDetail;
import com.pennant.backend.model.finance.FinServiceInstruction;
import com.pennant.backend.model.finance.FinanceProfitDetail;
import com.pennant.backend.model.finance.FinanceScheduleDetail;
import com.pennant.backend.model.finance.RescheduleLog;
import com.pennant.backend.model.finance.RescheduleLogHeader;
import com.pennanttech.pennapps.core.jdbc.BasicDao;
import com.pennanttech.pennapps.core.jdbc.JdbcUtil;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pennapps.core.resource.Message;
import com.pennanttech.pff.constants.AccountingEvent;

public class RescheduleReportGenerationDAOImpl extends BasicDao<RescheduleLog> implements ReschedulReportGenerationDAO {
	protected static final Logger logger = LogManager.getLogger(RescheduleReportGenerationDAOImpl.class);

	public List<FinLogEntryDetail> getFinLogEntryDetailList(long finID, Date fromDate, Date toDate) {
		FinLogEntryRowMapper rowMapper = new FinLogEntryRowMapper();
		StringBuilder sql = new StringBuilder("Select");
		sql.append(" FinID, FinReference, LogKey, EventAction, PostDate From FinLogEntryDetail");
		sql.append(" Where FinID = ? and PostDate >= ? and PostDate <= ? and EventAction in (?, ?)");

		logger.debug(Literal.SQL + sql.toString());

		return this.jdbcOperations.query(sql.toString(), ps -> {
			int index = 1;
			ps.setLong(index++, finID);
			ps.setDate(index++, fromDate);
			ps.setDate(index++, toDate);
			ps.setString(index++, AccountingEvent.SCDCHG);
			ps.setString(index, AccountingEvent.RATCHG);
		}, rowMapper);
	}

	private class FinLogEntryRowMapper implements RowMapper<FinLogEntryDetail> {
		public FinLogEntryRowMapper() {
		    super();
		}

		@Override
		public FinLogEntryDetail mapRow(ResultSet rs, int rowNum) throws SQLException {
			FinLogEntryDetail fle = new FinLogEntryDetail();

			fle.setFinID(rs.getLong("FinID"));
			fle.setFinReference(rs.getString("FinReference"));
			fle.setLogKey(rs.getLong("LogKey"));
			fle.setPostDate(rs.getDate("PostDate"));
			fle.setEventAction(rs.getString("EventAction"));

			return fle;
		}

	}

	@Override
	public RescheduleLog getFinBasicDetails(long finID) {
		StringBuilder sql = new StringBuilder("Select");
		sql.append(" r.Branchdesc, c.CustFName, c.CustShrtName, fm.FinBranch");
		sql.append(" From FinanceMain fm");
		sql.append(" Inner Join Customers c on fm.CustId = c.CustId");
		sql.append(" Inner Join RMTBranches r on fm.FinBranch = r.BranchCode");
		sql.append(" Where fm.FinID = ?");

		logger.debug(Literal.SQL + sql.toString());

		try {
			return this.jdbcOperations.queryForObject(sql.toString(), (rs, rowNum) -> {
				RescheduleLog rl = new RescheduleLog();
				rl.setCustName(rs.getString("CustFName") + "_" + rs.getString("CustShrtName"));
				rl.setFinBranch(rs.getString("FinBranch") + "_" + rs.getString("Branchdesc"));

				return rl;
			}, finID);
		} catch (EmptyResultDataAccessException e) {
			logger.warn(Message.NO_RECORD_FOUND);
			return new RescheduleLog();
		}
	}

	@Override
	public List<FinServiceInstruction> getFinServiceInstructions(long finID) {
		StringBuilder sql = new StringBuilder("Select");
		sql.append(" ServiceSeqId, FinEvent, FinID, FinReference, FromDate, ToDate, PftDaysBasis, SchdMethod");
		sql.append(", ActualRate, BaseRate, SplRate, Margin, GrcPeriodEndDate, NextGrcRepayDate, RepayPftFrq");
		sql.append(", RepayRvwFrq, RepayCpzFrq, GrcPftFrq, GrcRvwFrq, GrcCpzFrq, RepayFrq, NextRepayDate");
		sql.append(", Amount, RecalType, RecalFromDate, RecalToDate, PftIntact, Terms, ServiceReqNo");
		sql.append(", Remarks, PftChg, InstructionUID, LinkedTranID, LogKey");
		sql.append(" From FinServiceInstruction");
		sql.append(" Where FinID = ?");
		sql.append(" Order By LogKey");

		logger.debug(Literal.SQL + sql.toString());

		return this.jdbcOperations.query(sql.toString(), ps -> {
			ps.setLong(1, finID);
		}, (rs, i) -> {
			FinServiceInstruction fsi = new FinServiceInstruction();

			fsi.setServiceSeqId(rs.getLong("ServiceSeqId"));
			fsi.setFinEvent(rs.getString("FinEvent"));
			fsi.setFinID(rs.getLong("FinID"));
			fsi.setFinReference(rs.getString("FinReference"));
			fsi.setFromDate(rs.getTimestamp("FromDate"));
			fsi.setToDate(rs.getTimestamp("ToDate"));
			fsi.setPftDaysBasis(rs.getString("PftDaysBasis"));
			fsi.setSchdMethod(rs.getString("SchdMethod"));
			fsi.setActualRate(rs.getBigDecimal("ActualRate"));
			fsi.setBaseRate(rs.getString("BaseRate"));
			fsi.setSplRate(rs.getString("SplRate"));
			fsi.setMargin(rs.getBigDecimal("Margin"));
			fsi.setGrcPeriodEndDate(rs.getTimestamp("GrcPeriodEndDate"));
			fsi.setNextGrcRepayDate(rs.getTimestamp("NextGrcRepayDate"));
			fsi.setRepayPftFrq(rs.getString("RepayPftFrq"));
			fsi.setRepayRvwFrq(rs.getString("RepayRvwFrq"));
			fsi.setRepayCpzFrq(rs.getString("RepayCpzFrq"));
			fsi.setGrcPftFrq(rs.getString("GrcPftFrq"));
			fsi.setGrcRvwFrq(rs.getString("GrcRvwFrq"));
			fsi.setGrcCpzFrq(rs.getString("GrcCpzFrq"));
			fsi.setRepayFrq(rs.getString("RepayFrq"));
			fsi.setNextRepayDate(rs.getTimestamp("NextRepayDate"));
			fsi.setAmount(rs.getBigDecimal("Amount"));
			fsi.setRecalType(rs.getString("RecalType"));
			fsi.setRecalFromDate(rs.getTimestamp("RecalFromDate"));
			fsi.setRecalToDate(rs.getTimestamp("RecalToDate"));
			fsi.setPftIntact(rs.getBoolean("PftIntact"));
			fsi.setTerms(rs.getInt("Terms"));
			fsi.setServiceReqNo(rs.getString("ServiceReqNo"));
			fsi.setRemarks(rs.getString("Remarks"));
			fsi.setPftChg(rs.getBigDecimal("PftChg"));
			fsi.setInstructionUID(rs.getLong("InstructionUID"));
			fsi.setLinkedTranID(rs.getLong("LinkedTranID"));
			fsi.setLogKey(JdbcUtil.getLong(rs.getLong("LogKey")));

			return fsi;
		});
	}

	@Override
	public List<FinServiceInstruction> getFinServiceInstructions(long finID, long logkey) {
		StringBuilder sql = new StringBuilder("Select");
		sql.append(" ServiceSeqId, FinEvent, FinID, FinReference, FromDate, ToDate, PftDaysBasis, SchdMethod");
		sql.append(", ActualRate, BaseRate, SplRate, Margin, GrcPeriodEndDate, NextGrcRepayDate, RepayPftFrq");
		sql.append(", RepayRvwFrq, RepayCpzFrq, GrcPftFrq, GrcRvwFrq, GrcCpzFrq, RepayFrq, NextRepayDate");
		sql.append(", Amount, RecalType, RecalFromDate, RecalToDate, PftIntact, Terms, ServiceReqNo");
		sql.append(", Remarks, PftChg, InstructionUID, LinkedTranID, LogKey");
		sql.append(" from FinServiceInstruction");
		sql.append(" Where FinID = ? and LogKey = ?");

		logger.debug(Literal.SQL + sql.toString());

		return this.jdbcOperations.query(sql.toString(), ps -> {
			ps.setLong(1, finID);
			ps.setObject(2, logkey);
		}, (rs, i) -> {
			FinServiceInstruction fsi = new FinServiceInstruction();

			fsi.setServiceSeqId(rs.getLong("ServiceSeqId"));
			fsi.setFinEvent(rs.getString("FinEvent"));
			fsi.setFinID(rs.getLong("FinID"));
			fsi.setFinReference(rs.getString("FinReference"));
			fsi.setFromDate(rs.getTimestamp("FromDate"));
			fsi.setToDate(rs.getTimestamp("ToDate"));
			fsi.setPftDaysBasis(rs.getString("PftDaysBasis"));
			fsi.setSchdMethod(rs.getString("SchdMethod"));
			fsi.setActualRate(rs.getBigDecimal("ActualRate"));
			fsi.setBaseRate(rs.getString("BaseRate"));
			fsi.setSplRate(rs.getString("SplRate"));
			fsi.setMargin(rs.getBigDecimal("Margin"));
			fsi.setGrcPeriodEndDate(rs.getTimestamp("GrcPeriodEndDate"));
			fsi.setNextGrcRepayDate(rs.getTimestamp("NextGrcRepayDate"));
			fsi.setRepayPftFrq(rs.getString("RepayPftFrq"));
			fsi.setRepayRvwFrq(rs.getString("RepayRvwFrq"));
			fsi.setRepayCpzFrq(rs.getString("RepayCpzFrq"));
			fsi.setGrcPftFrq(rs.getString("GrcPftFrq"));
			fsi.setGrcRvwFrq(rs.getString("GrcRvwFrq"));
			fsi.setGrcCpzFrq(rs.getString("GrcCpzFrq"));
			fsi.setRepayFrq(rs.getString("RepayFrq"));
			fsi.setNextRepayDate(rs.getTimestamp("NextRepayDate"));
			fsi.setAmount(rs.getBigDecimal("Amount"));
			fsi.setRecalType(rs.getString("RecalType"));
			fsi.setRecalFromDate(rs.getTimestamp("RecalFromDate"));
			fsi.setRecalToDate(rs.getTimestamp("RecalToDate"));
			fsi.setPftIntact(rs.getBoolean("PftIntact"));
			fsi.setTerms(rs.getInt("Terms"));
			fsi.setServiceReqNo(rs.getString("ServiceReqNo"));
			fsi.setRemarks(rs.getString("Remarks"));
			fsi.setPftChg(rs.getBigDecimal("PftChg"));
			fsi.setInstructionUID(rs.getLong("InstructionUID"));
			fsi.setLinkedTranID(rs.getLong("LinkedTranID"));
			fsi.setLogKey(JdbcUtil.getLong(rs.getObject("LogKey")));

			return fsi;
		});
	}

	public List<FinanceScheduleDetail> getScheduleDetails(long finID, String type, long logkey) {
		StringBuilder sql = new StringBuilder("select");
		sql.append(" FinID, FinReference, SchDate, SchSeq, PftOnSchDate, CpzOnSchDate, RepayOnSchDate, RvwOnSchDate");
		sql.append(", DisbOnSchDate, DownpaymentOnSchDate, BalanceForPftCal, BaseRate, SplRate, MrgRate");
		sql.append(", ActRate, NoOfDays, DayFactor, ProfitCalc, ProfitSchd, PrincipalSchd");
		sql.append(", RepayAmount, ProfitBalance, DisbAmount, DownPaymentAmount, CpzAmount, CpzBalance");
		sql.append(", OrgPft, OrgPri, OrgEndBal, OrgPlanPft, ClosingBalance, ProfitFraction, PrvRepayAmount");
		sql.append(", CalculatedRate, FeeChargeAmt, FeeSchd, SchdFeePaid, SchdFeeOS");
		sql.append(", TDSAmount, TDSPaid, PftDaysBasis, SchdPriPaid, SchdPftPaid");
		sql.append(", SchPriPaid, SchPftPaid, Specifier, DefSchdDate, SchdMethod, InstNumber, BpiOrHoliday");
		sql.append(", FrqDate, RecalLock");
		sql.append(" From FinScheduleDetails");
		if (logkey != 0) {
			sql.append(type);
		}
		sql.append(" Where FinID= ?");
		if (logkey != 0) {
			sql.append(" and logkey = ?");
		}

		RowMapper<FinanceScheduleDetail> rowMapper = new ScheduleDetailRowMapper();

		return this.jdbcOperations.query(sql.toString(), ps -> {
			ps.setLong(1, finID);
			if (logkey != 0) {
				ps.setLong(2, logkey);
			}
		}, rowMapper);
	}

	public class ScheduleDetailRowMapper implements RowMapper<FinanceScheduleDetail> {

		@Override
		public FinanceScheduleDetail mapRow(ResultSet rs, int rowNum) throws SQLException {
			FinanceScheduleDetail schd = new FinanceScheduleDetail();

			schd.setFinID(rs.getLong("FinID"));
			schd.setFinReference(rs.getString("FinReference"));
			schd.setSchDate(rs.getTimestamp("SchDate"));
			schd.setSchSeq(rs.getInt("SchSeq"));
			schd.setPftOnSchDate(rs.getBoolean("PftOnSchDate"));
			schd.setCpzOnSchDate(rs.getBoolean("CpzOnSchDate"));
			schd.setRepayOnSchDate(rs.getBoolean("RepayOnSchDate"));
			schd.setRvwOnSchDate(rs.getBoolean("RvwOnSchDate"));
			schd.setDisbOnSchDate(rs.getBoolean("DisbOnSchDate"));
			schd.setDownpaymentOnSchDate(rs.getBoolean("DownpaymentOnSchDate"));
			schd.setBalanceForPftCal(rs.getBigDecimal("BalanceForPftCal"));
			schd.setBaseRate(rs.getString("BaseRate"));
			schd.setSplRate(rs.getString("SplRate"));
			schd.setMrgRate(rs.getBigDecimal("MrgRate"));
			schd.setActRate(rs.getBigDecimal("ActRate"));
			schd.setNoOfDays(rs.getInt("NoOfDays"));
			schd.setDayFactor(rs.getBigDecimal("DayFactor"));
			schd.setProfitCalc(rs.getBigDecimal("ProfitCalc"));
			schd.setProfitSchd(rs.getBigDecimal("ProfitSchd"));
			schd.setPrincipalSchd(rs.getBigDecimal("PrincipalSchd"));
			schd.setRepayAmount(rs.getBigDecimal("RepayAmount"));
			schd.setProfitBalance(rs.getBigDecimal("ProfitBalance"));
			schd.setDisbAmount(rs.getBigDecimal("DisbAmount"));
			schd.setDownPaymentAmount(rs.getBigDecimal("DownPaymentAmount"));
			schd.setCpzAmount(rs.getBigDecimal("CpzAmount"));
			schd.setCpzBalance(rs.getBigDecimal("CpzBalance"));
			schd.setOrgPft(rs.getBigDecimal("OrgPft"));
			schd.setOrgPri(rs.getBigDecimal("OrgPri"));
			schd.setOrgEndBal(rs.getBigDecimal("OrgEndBal"));
			schd.setOrgPlanPft(rs.getBigDecimal("OrgPlanPft"));
			schd.setClosingBalance(rs.getBigDecimal("ClosingBalance"));
			schd.setProfitFraction(rs.getBigDecimal("ProfitFraction"));
			schd.setPrvRepayAmount(rs.getBigDecimal("PrvRepayAmount"));
			schd.setCalculatedRate(rs.getBigDecimal("CalculatedRate"));
			schd.setFeeChargeAmt(rs.getBigDecimal("FeeChargeAmt"));
			schd.setFeeSchd(rs.getBigDecimal("FeeSchd"));
			schd.setSchdFeePaid(rs.getBigDecimal("SchdFeePaid"));
			schd.setSchdFeeOS(rs.getBigDecimal("SchdFeeOS"));
			schd.setTDSAmount(rs.getBigDecimal("TDSAmount"));
			schd.setTDSPaid(rs.getBigDecimal("TDSPaid"));
			schd.setPftDaysBasis(rs.getString("PftDaysBasis"));
			schd.setSchdPriPaid(rs.getBigDecimal("SchdPriPaid"));
			schd.setSchdPftPaid(rs.getBigDecimal("SchdPftPaid"));
			schd.setSchPriPaid(rs.getBoolean("SchPriPaid"));
			schd.setSchPftPaid(rs.getBoolean("SchPftPaid"));
			schd.setSpecifier(rs.getString("Specifier"));
			schd.setDefSchdDate(rs.getTimestamp("DefSchdDate"));
			schd.setSchdMethod(rs.getString("SchdMethod"));
			schd.setInstNumber(rs.getInt("InstNumber"));
			schd.setBpiOrHoliday(rs.getString("BpiOrHoliday"));
			schd.setFrqDate(rs.getBoolean("FrqDate"));
			schd.setRecalLock(rs.getBoolean("RecalLock"));

			return schd;
		}

	}

	public FinanceProfitDetail getProfitDetail(long finID) {
		String sql = "Select MaturityDate from FinPftDetails Where FinID = ?";

		logger.debug(Literal.SQL + sql);

		try {
			return this.jdbcOperations.queryForObject(sql, (rs, i) -> {
				FinanceProfitDetail fpd = new FinanceProfitDetail();
				fpd.setMaturityDate(rs.getDate("MaturityDate"));

				return fpd;
			}, finID);
		} catch (EmptyResultDataAccessException e) {
			logger.warn(Message.NO_RECORD_FOUND);
			return null;
		}
	}

	@Override
	public List<RescheduleLogHeader> getFinBasicDetails() {
		StringBuilder sql = new StringBuilder("Select");
		sql.append(" c.CustFName, c.CustShrtName, fm.FinID, fm.FinReference");
		sql.append(" From FinanceMain fm");
		sql.append(" Inner Join Customers c on fm.custid = c.custid ");
		sql.append(" Where fm.FinID In");
		sql.append(" (Select distinct FinID From FinServiceInstruction");
		sql.append(" Where FinEvent = ?)");

		logger.debug(Literal.SQL + sql.toString());

		return this.jdbcOperations.query(sql.toString(), ps -> {
			ps.setString(1, "ReSchedule");
		}, (rs, i) -> {
			RescheduleLogHeader rlh = new RescheduleLogHeader();

			rlh.setFinID(rs.getLong("FinID"));
			rlh.setFinReference(rs.getString("FinReference"));
			rlh.setCustName(rs.getString("CustFName") + "_" + rs.getString("CustShrtName"));

			return rlh;
		});
	}

}
