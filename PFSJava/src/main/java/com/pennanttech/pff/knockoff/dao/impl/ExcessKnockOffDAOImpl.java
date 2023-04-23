package com.pennanttech.pff.knockoff.dao.impl;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Date;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;

import com.pennant.backend.model.customermasters.CustomerCoreBank;
import com.pennant.backend.model.finance.AutoKnockOff;
import com.pennant.backend.model.finance.FinanceMain;
import com.pennant.eod.constants.EodConstants;
import com.pennant.pff.extension.CustomerExtension;
import com.pennanttech.pennapps.core.ConcurrencyException;
import com.pennanttech.pennapps.core.jdbc.JdbcUtil;
import com.pennanttech.pennapps.core.jdbc.SequenceDao;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pennapps.core.util.DateUtil;
import com.pennanttech.pff.knockoff.dao.ExcessKnockOffDAO;
import com.pennanttech.pff.knockoff.model.ExcessKnockOff;
import com.pennanttech.pff.knockoff.model.ExcessKnockOffDetails;
import com.pennanttech.pff.model.Queing;

public class ExcessKnockOffDAOImpl extends SequenceDao<AutoKnockOff> implements ExcessKnockOffDAO {

	private static Logger logger = LogManager.getLogger(ExcessKnockOffDAOImpl.class);

	@Override
	public void logExcessForCrossLoanKnockOff(Date valueDate, String day, String thresholdValue) {
		StringBuilder sql = new StringBuilder("Insert Into");
		sql.append(" CROSS_LOAN_KNOCKOFF_STAGE");
		sql.append(" (CustId, CoreBankId, FinId, ReferenceId, AmountType");
		sql.append(", BalanceAmt, ValueDate, ExecutionDay, ThresholdValue)");
		sql.append(" Select CustId, CustCoreBank, FinId, ReferenceId, AmountType");
		sql.append(", BalanceAmt, ? ValueDate, ? ExecutionDay, ? ThresholdValue From");
		sql.append(" (Select c.CustId, c.CustCoreBank, fm.FinId, ExcessId ReferenceId, AmountType");
		sql.append(", sum(BalanceAmt) BalanceAmt");
		sql.append(" From FinExcessAmount ea");
		sql.append(" Inner Join FinanceMain fm on fm.FinID = ea.FinID");
		sql.append(" Inner Join Customers c on c.CustId = fm.CustId");
		sql.append(" Where  AmountType = ? and BalanceAmt > ? and fm.FinIsActive = ? and");
		sql.append(" fm.WriteoffLoan = ? Group by c.CustId, c.CustCoreBank, fm.FinID, ea.ExcessId, ea.AmountType");
		sql.append(" Union All");
		sql.append(" Select CustId, CustCoreBank, FinId, ReferenceId, AmountType");
		sql.append(", sum(BalanceAmt) BalanceAmt from ");
		sql.append(" (Select c.CustId, c.CustCoreBank, fm.FinId, AdviseId ReferenceId, ? AmountType");
		sql.append(", BalanceAmt From ManualAdvise ma");
		sql.append(" Inner Join FinanceMain fm on fm.FinID = ma.FinID");
		sql.append(" Inner Join Customers c on c.CustId = fm.CustId");
		sql.append(" Where  ma.AdviseType = ? and BalanceAmt > ? and fm.FinIsActive = ? and");
		sql.append(" fm.WriteoffLoan = ?) it ");
		sql.append("Group by it.CustId, it.CustCoreBank, it.FinID, it.ReferenceId, it.AmountType) T");

		logger.debug(Literal.SQL.concat(sql.toString()));

		try {
			this.jdbcOperations.update(sql.toString(), ps -> {
				int index = 0;

				ps.setDate(++index, JdbcUtil.getDate(valueDate));
				ps.setString(++index, day);
				ps.setString(++index, thresholdValue);
				ps.setString(++index, "E");
				ps.setInt(++index, 0);
				ps.setInt(++index, 1);
				ps.setInt(++index, 0);
				ps.setString(++index, "P");
				ps.setInt(++index, 2);
				ps.setInt(++index, 0);
				ps.setInt(++index, 1);
				ps.setInt(++index, 0);
			});
		} catch (DuplicateKeyException e) {
			throw new ConcurrencyException(e);
		}
	}

	@Override
	public void deleteQueue() {
		jdbcOperations.update("Truncate table Cross_Loan_KnockOff_Dtl_Stage");
		jdbcOperations.update("Truncate table Cross_Loan_KnockOff_Stage");
		jdbcOperations.update("Truncate table Cross_Loan_KnockOff_Queue");
	}

	@Override
	public long prepareQueue() {
		String sql = "Insert Into Cross_Loan_KnockOff_Queue(ID, CustID) Select row_number() over(order by CustID) ID, CustID From (Select distinct CustID From Cross_Loan_KnockOff_Stage) T";

		if (CustomerExtension.CUST_CORE_BANK_ID) {
			sql = "Insert Into Cross_Loan_KnockOff_Queue(ID, CoreBankId) Select row_number() over(order by CoreBankId) ID, CoreBankId From (Select distinct CoreBankId From Cross_Loan_KnockOff_Stage) T";
		}

		logger.debug(Literal.SQL.concat(sql));

		return this.jdbcOperations.update(sql);
	}

	@Override
	public void handleFailures() {
		String sql = "Delete From Cross_Loan_KnockOff_Queue Where Progress = ?";

		logger.debug(Literal.SQL.concat(sql));

		this.jdbcOperations.update(sql, ps -> {
			ps.setInt(1, EodConstants.PROGRESS_SUCCESS);
		});

		sql = "Update Cross_Loan_KnockOff_Queue Set Progress = ? Where Progress = ?";

		logger.debug(Literal.SQL.concat(sql));

		int count = this.jdbcOperations.update(sql, ps -> {
			ps.setInt(1, EodConstants.PROGRESS_WAIT);
			ps.setInt(2, EodConstants.PROGRESS_FAILED);
		});

		if (count > 0) {
			updateQueingRecords(getQueingRecords());
		}
	}

	@Override
	public long getQueueCount() {
		String sql = "Select Coalesce(count(ID), 0) From Cross_Loan_KnockOff_Queue where Progress = ?";

		logger.debug(Literal.SQL.concat(sql));

		return this.jdbcOperations.queryForObject(sql, Long.class, EodConstants.PROGRESS_WAIT);
	}

	@Override
	public int updateThreadID(long from, long to, int threadId) {
		String sql = "Update Cross_Loan_KnockOff_Queue Set ThreadId = ? Where Id > ? and Id <= ?  and ThreadId = ?";

		logger.debug(Literal.SQL.concat(sql));

		try {
			return this.jdbcOperations.update(sql, threadId, from, to, 0);
		} catch (DataAccessException dae) {
			logger.error(Literal.EXCEPTION, dae);
		}

		return 0;
	}

	private List<Queing> getQueingRecords() {
		String sql = "Select row_number() over(order by id) resteID, ID from Cross_Loan_KnockOff_Queue";

		logger.debug(Literal.SQL.concat(sql));

		return this.jdbcOperations.query(sql.toString(), ps -> {

		}, (rs, Num) -> {
			Queing queing = new Queing();
			queing.setId(rs.getLong("ID"));
			queing.setResetId(rs.getLong("resteID"));
			return queing;
		});

	}

	private void updateQueingRecords(List<Queing> queing) {
		String sql = "Update Cross_Loan_KnockOff_Queue Set Id = ? Where ID = ?";

		logger.debug(Literal.SQL.concat(sql));

		try {
			jdbcOperations.batchUpdate(sql, new BatchPreparedStatementSetter() {
				@Override
				public void setValues(PreparedStatement ps, int i) throws SQLException {
					Queing queingdetails = queing.get(i);
					int index = 1;

					ps.setLong(index++, queingdetails.getResetId());
					ps.setLong(index, queingdetails.getId());
				}

				@Override
				public int getBatchSize() {
					return queing.size();
				}
			});
		} catch (DataAccessException e) {
			logger.warn(Literal.EXCEPTION, e);
		}
	}

	@Override
	public void updateProgress(CustomerCoreBank ccb, int progressInProcess) {
		String sql = null;
		if (progressInProcess == EodConstants.PROGRESS_IN_PROCESS) {
			sql = "Update Cross_Loan_KnockOff_Queue Set Progress = ?, StartTime = ? Where CustID = ? and Progress = ?";

			if (CustomerExtension.CUST_CORE_BANK_ID) {
				sql = "Update Cross_Loan_KnockOff_Queue Set Progress = ?, StartTime = ? Where CoreBankId = ? and Progress = ?";
			}

			logger.debug(Literal.SQL.concat(sql));

			this.jdbcOperations.update(sql, ps -> {
				ps.setInt(1, progressInProcess);
				ps.setDate(2, JdbcUtil.getDate(DateUtil.getSysDate()));
				ps.setLong(3, ccb.getCustID());
				if (CustomerExtension.CUST_CORE_BANK_ID) {
					ps.setString(3, ccb.getCustCoreBank());
				}
				ps.setInt(4, EodConstants.PROGRESS_WAIT);
			});
		} else if (progressInProcess == EodConstants.PROGRESS_SUCCESS) {
			sql = "Update Cross_Loan_KnockOff_Queue Set EndTime = ?, Progress = ? where CustID = ?";

			if (CustomerExtension.CUST_CORE_BANK_ID) {
				sql = "Update Cross_Loan_KnockOff_Queue Set EndTime = ?, Progress = ? where CoreBankId = ?";
			}

			logger.debug(Literal.SQL.concat(sql));

			this.jdbcOperations.update(sql, ps -> {
				ps.setDate(1, JdbcUtil.getDate(DateUtil.getSysDate()));
				ps.setInt(2, EodConstants.PROGRESS_SUCCESS);
				ps.setLong(3, ccb.getCustID());
				if (CustomerExtension.CUST_CORE_BANK_ID) {
					ps.setString(3, ccb.getCustCoreBank());
				}
			});
		} else if (progressInProcess == EodConstants.PROGRESS_FAILED) {
			sql = "Update Cross_Loan_KnockOff_Queue Set EndTime = ?, ThreadId = ?, Progress = ? Where CustID = ?";

			if (CustomerExtension.CUST_CORE_BANK_ID) {
				sql = "Update Cross_Loan_KnockOff_Queue Set EndTime = ?, ThreadId = ?, Progress = ? Where CoreBankId = ?";
			}

			logger.debug(Literal.SQL.concat(sql));

			this.jdbcOperations.update(sql, ps -> {
				ps.setDate(1, JdbcUtil.getDate(DateUtil.getSysDate()));
				ps.setInt(2, 0);
				ps.setInt(3, EodConstants.PROGRESS_FAILED);
				ps.setLong(4, ccb.getCustID());
				if (CustomerExtension.CUST_CORE_BANK_ID) {
					ps.setString(5, ccb.getCustCoreBank());
				}
			});
		}
	}

	@Override
	public List<ExcessKnockOff> loadData(CustomerCoreBank customerCoreBank) {
		long custID = customerCoreBank.getCustID();
		String corBankID = customerCoreBank.getCustCoreBank();

		StringBuilder sql = new StringBuilder("Select");
		sql.append(" clk.Id, fm.CustId, clk.CoreBankId, fm.FinId, fm.FinReference");
		sql.append(", fm.FinType, clk.ReferenceID, clk.AmountType, clk.BalanceAmt");
		sql.append(" From Cross_Loan_KnockOff_Stage clk");
		sql.append(" Inner Join FinanceMain fm on fm.FinId = clk.FinId");

		if (CustomerExtension.CUST_CORE_BANK_ID) {
			sql.append(" Where clk.CoreBankId = ?");
		} else {
			sql.append("Where clk.CustId = ?");
		}

		logger.debug(Literal.SQL.concat(sql.toString()));

		return this.jdbcOperations.query(sql.toString(), ps -> {

			if (CustomerExtension.CUST_CORE_BANK_ID) {
				ps.setString(1, corBankID);
			} else {
				ps.setLong(1, custID);
			}
		}, (rs, rowNum) -> {
			ExcessKnockOff eko = new ExcessKnockOff();

			eko.setId(rs.getLong("Id"));
			eko.setCustID(JdbcUtil.getLong(rs.getObject("CustId")));
			eko.setCoreBankId(rs.getString("CoreBankId"));
			eko.setFinID(JdbcUtil.getLong(rs.getObject("FinID")));
			eko.setFinReference(rs.getString("FinReference"));
			eko.setFinType(rs.getString("FinType"));
			eko.setReferenceID(JdbcUtil.getLong(rs.getObject("CustId")));
			eko.setAmountType(rs.getString("AmountType"));
			eko.setBalanceAmt(rs.getBigDecimal("BalanceAmt"));

			return eko;
		});

	}

	@Override
	public void logExcessForCrossLoanDetails(Date valueDate, String day) {
		StringBuilder sql = new StringBuilder("Insert Into");
		sql.append(" CROSS_LOAN_KNOCKOFF_DTL_STAGE");
		sql.append(" (KnockOffId, ExcessID, FinType");
		sql.append(", Code, ExecutionDays, FinCcy, FeeTypeCode");
		sql.append(", KnockOffOrder, FeeOrder)");
		sql.append(" Select");
		sql.append(" ak.Id KnockOffId, ake.Id ExcessId, ft.FinType");
		sql.append(", ak.Code, ak.ExecutionDays, fm.FinCcy");
		sql.append(", fe.FeeTypeCode, akl.KnockOffOrder, akf.FeeOrder");
		sql.append(" From CROSS_LOAN_KNOCKOFF_STAGE ake");
		sql.append(" Inner Join FinanceMain fm on fm.FinID = ake.FinID");
		sql.append(" Inner Join RMTFinanceTypes ft on ft.Fintype = fm.Fintype");
		sql.append(" Inner Join Auto_KnockOff_LoanTypes akl on akl.Loantype = ft.Fintype");
		sql.append(" Inner Join Auto_knockOff ak on ak.Id = akl.Knockoffid and ak.Active = ?");
		sql.append(" Inner Join AUTO_KNOCKOFF_FEE_TYPES akf on akf.KnockOffId = ak.Id");
		sql.append(" Inner Join FeeTypes fe on fe.FeeTypeId = akf.FeeTypeId");
		sql.append(" Where ake.valuedate = ? ");
		sql.append(" and ak.ExecutionDays like ?");
		sql.append(" Order by ExcessId, akl.KnockOffOrder, akf.FeeOrder");

		logger.debug(Literal.SQL.concat(sql.toString()));

		try {
			jdbcOperations.update(sql.toString(), ps -> {
				int index = 1;

				ps.setInt(index++, 1);
				ps.setDate(index++, JdbcUtil.getDate(valueDate));
				ps.setString(index, "%" + day + "%");
			});
		} catch (DuplicateKeyException e) {
			throw new ConcurrencyException(e);
		}
	}

	@Override
	public List<ExcessKnockOffDetails> getStageDataByID(long id) {
		StringBuilder sql = new StringBuilder("Select");
		sql.append(" clkd.ID, clkd.KnockOffID, clkd.ExcessID, clkd.FinType, clkd.Code, clkd.ExecutionDays");
		sql.append(", clkd.FinCcy, clkd.FeeTypeCode, clkd.KnockOffOrder, clkd.FeeOrder");
		sql.append(" from CROSS_LOAN_KNOCKOFF_DTL_STAGE clkd");
		sql.append(" Inner Join CROSS_LOAN_KNOCKOFF_STAGE clk on clk.id = clkd.excessid");
		sql.append(" Inner join Financemain fm on fm.finid = clk.finid ");
		sql.append(" Where clk.Id = ?");

		logger.debug(Literal.SQL.concat(sql.toString()));

		return this.jdbcOperations.query(sql.toString(), ps -> {
			ps.setLong(1, id);
		}, (rs, rowNum) -> {
			ExcessKnockOffDetails eko = new ExcessKnockOffDetails();

			eko.setId(rs.getLong("ID"));
			eko.setKnockOffID(rs.getLong("KnockOffID"));
			eko.setExcessID(rs.getLong("ExcessID"));
			eko.setFinType(rs.getString("FinType"));
			eko.setCode(rs.getString("Code"));
			eko.setExecutionDays(rs.getString("ExecutionDays"));
			eko.setFinCcy(rs.getString("FinCcy"));
			eko.setFeeTypeCode(rs.getString("FeeTypeCode"));
			eko.setKnockOffOrder(rs.getString("KnockOffOrder"));
			eko.setFeeOrder(rs.getInt("FeeOrder"));

			return eko;
		});

	}

	@Override
	public List<FinanceMain> getLoansbyCustId(long custId, String coreBankId, long finId) {
		StringBuilder sql = new StringBuilder("Select");
		sql.append(" fm.FinID, fm.FinReference, fm.GrcPeriodEndDate, fm.AllowGrcPeriod, fm.GraceBaseRate");
		sql.append(", fm.GraceSpecialRate, fm.GrcPftRate, fm.GrcPftFrq, fm.NextGrcPftDate, fm.AllowGrcPftRvw");
		sql.append(", fm.GrcPftRvwFrq, fm.NextGrcPftRvwDate, fm.AllowGrcCpz, fm.GrcCpzFrq, fm.NextGrcCpzDate");
		sql.append(", fm.RepayBaseRate, fm.RepaySpecialRate, fm.RepayProfitRate, fm.RepayFrq, fm.NextRepayDate");
		sql.append(", fm.RepayPftFrq, fm.NextRepayPftDate, fm.AllowRepayRvw, fm.RepayRvwFrq, fm.RepayRvwFrq");
		sql.append(", fm.NextRepayRvwDate, fm.AllowRepayCpz, fm.RepayCpzFrq, fm.NextRepayCpzDate, fm.MaturityDate");
		sql.append(", fm.CpzAtGraceEnd, fm.GrcRateBasis, fm.RepayRateBasis, fm.FinType, fm.FinCcy, fm.ProfitDaysBasis");
		sql.append(", fm.FirstRepay, fm.LastRepay, fm.ScheduleMethod, fm.FinStartDate, fm.FinAmount, fm.CustID");
		sql.append(", fm.FinBranch, fm.FinSourceID, fm.RecalType, fm.FinIsActive, fm.LastRepayDate");
		sql.append(", fm.LastRepayPftDate, fm.LastRepayRvwDate, fm.LastRepayCpzDate, fm.AllowGrcRepay, fm.GrcSchdMthd");
		sql.append(", fm.GrcMargin, fm.RepayMargin, fm.ClosingStatus, fm.FinRepayPftOnFrq, fm.GrcProfitDaysBasis");
		sql.append(", fm.GrcMinRate, fm.GrcMaxRate, fm.GrcMaxAmount, fm.RpyMinRate, fm.RpyMaxRate, fm.ManualSchedule");
		sql.append(", fm.CalRoundingMode, fm.RoundingTarget, fm.RvwRateApplFor, fm.SchCalOnRvw, fm.PastduePftCalMthd");
		sql.append(", fm.DroppingMethod, fm.RateChgAnyDay, fm.PastduePftMargin, fm.FinRepayMethod, fm.MigratedFinance");
		sql.append(", fm.ScheduleMaintained, fm.ScheduleRegenerated, fm.MandateID, fm.FinStatus, fm.DueBucket");
		sql.append(", fm.FinStsReason");
		sql.append(", fm.PromotionCode, fm.FinCategory, fm.ProductCategory, fm.ReAgeBucket");
		sql.append(", fm.TDSApplicable, fm.TdsType");
		sql.append(", fm.BpiTreatment, fm.FinRepaymentAmount, fm.GrcAdvType, fm.AdvType, fm.SanBsdSchdle");
		sql.append(", fm.AutoIncGrcEndDate, fm.Version, fm.LastMntOn, fm.ReferralId, fm.GraceTerms, fm.AlwMultiDisb");
		sql.append(", fm.NumberOfTerms, fm.PromotionSeqId, fm.SvAmount, fm.CbAmount, fm.EmployeeName");
		sql.append(", fm.FinAssetValue, fm.FinCurrAssetValue, fm.AlwGrcAdj, fm.EndGrcPeriodAftrFullDisb");
		sql.append(", fm.WriteoffLoan, fm.SchdVersion, fm.NumberOfTerms, fm.ManualSchdType");
		sql.append(", fm.OverdraftTxnChrgReq, fm.OverdraftCalcChrg, fm.OverdraftChrgAmtOrPerc, fm.OverdraftChrCalOn");
		sql.append(", fm.StepFinance, fm.AlwManualSteps, fm.CalcOfSteps, fm.NoOfGrcSteps, sdd.EntityCode");
		sql.append(", fm.UnderSettlement, fm.UnderNpa");
		sql.append(" From FinanceMain fm");
		sql.append(" Inner Join Customers c on c.CustID = fm.CustID");
		sql.append(" Inner Join RmtFinanceTypes ft on ft.FinType = fm.FinType");
		sql.append(" Inner Join SmtDivisionDetail sdd on sdd.DivisionCode = ft.FinDivision");

		if (CustomerExtension.CUST_CORE_BANK_ID) {
			sql.append(" Where c.CustCoreBank = ?");
		} else {
			sql.append("Where c.CustId = ?");
		}
		sql.append(" and fm.FinID != ? and fm.FinIsActive = ?");

		logger.debug(Literal.SQL.concat(sql.toString()));

		return this.jdbcOperations.query(sql.toString(), ps -> {
			if (CustomerExtension.CUST_CORE_BANK_ID) {
				ps.setString(1, coreBankId);
			} else {
				ps.setLong(1, custId);
			}
			ps.setLong(2, finId);
			ps.setBoolean(3, true);
		}, (rs, rowNum) -> {
			FinanceMain fm = new FinanceMain();

			fm.setFinID(rs.getLong("FinID"));
			fm.setFinReference(rs.getString("FinReference"));
			fm.setGrcPeriodEndDate(rs.getDate("GrcPeriodEndDate"));
			fm.setAllowGrcPeriod(rs.getBoolean("AllowGrcPeriod"));
			fm.setGraceBaseRate(rs.getString("GraceBaseRate"));
			fm.setGraceSpecialRate(rs.getString("GraceSpecialRate"));
			fm.setGrcPftRate(rs.getBigDecimal("GrcPftRate"));
			fm.setGrcPftFrq(rs.getString("GrcPftFrq"));
			fm.setNextGrcPftDate(rs.getDate("NextGrcPftDate"));
			fm.setAllowGrcPftRvw(rs.getBoolean("AllowGrcPftRvw"));
			fm.setGrcPftRvwFrq(rs.getString("GrcPftRvwFrq"));
			fm.setNextGrcPftRvwDate(rs.getDate("NextGrcPftRvwDate"));
			fm.setAllowGrcCpz(rs.getBoolean("AllowGrcCpz"));
			fm.setGrcCpzFrq(rs.getString("GrcCpzFrq"));
			fm.setNextGrcCpzDate(rs.getDate("NextGrcCpzDate"));
			fm.setRepayBaseRate(rs.getString("RepayBaseRate"));
			fm.setRepaySpecialRate(rs.getString("RepaySpecialRate"));
			fm.setRepayProfitRate(rs.getBigDecimal("RepayProfitRate"));
			fm.setRepayFrq(rs.getString("RepayFrq"));
			fm.setNextRepayDate(rs.getDate("NextRepayDate"));
			fm.setRepayPftFrq(rs.getString("RepayPftFrq"));
			fm.setNextRepayPftDate(rs.getDate("NextRepayPftDate"));
			fm.setAllowRepayRvw(rs.getBoolean("AllowRepayRvw"));
			fm.setRepayRvwFrq(rs.getString("RepayRvwFrq"));
			fm.setNextRepayRvwDate(rs.getDate("NextRepayRvwDate"));
			fm.setAllowRepayCpz(rs.getBoolean("AllowRepayCpz"));
			fm.setRepayCpzFrq(rs.getString("RepayCpzFrq"));
			fm.setNextRepayCpzDate(rs.getDate("NextRepayCpzDate"));
			fm.setMaturityDate(rs.getDate("MaturityDate"));
			fm.setCpzAtGraceEnd(rs.getBoolean("CpzAtGraceEnd"));
			fm.setGrcRateBasis(rs.getString("GrcRateBasis"));
			fm.setRepayRateBasis(rs.getString("RepayRateBasis"));
			fm.setFinType(rs.getString("FinType"));
			fm.setFinCcy(rs.getString("FinCcy"));
			fm.setProfitDaysBasis(rs.getString("ProfitDaysBasis"));
			fm.setFirstRepay(rs.getBigDecimal("FirstRepay"));
			fm.setLastRepay(rs.getBigDecimal("LastRepay"));
			fm.setScheduleMethod(rs.getString("ScheduleMethod"));
			fm.setFinStartDate(rs.getDate("FinStartDate"));
			fm.setFinAmount(rs.getBigDecimal("FinAmount"));
			fm.setCustID(rs.getLong("CustID"));
			fm.setFinBranch(rs.getString("FinBranch"));
			fm.setFinSourceID(rs.getString("FinSourceID"));
			fm.setRecalType(rs.getString("RecalType"));
			fm.setFinIsActive(rs.getBoolean("FinIsActive"));
			fm.setLastRepayDate(rs.getDate("LastRepayDate"));
			fm.setLastRepayPftDate(rs.getDate("LastRepayPftDate"));
			fm.setLastRepayRvwDate(rs.getDate("LastRepayRvwDate"));
			fm.setLastRepayCpzDate(rs.getDate("LastRepayCpzDate"));
			fm.setAllowGrcRepay(rs.getBoolean("AllowGrcRepay"));
			fm.setGrcSchdMthd(rs.getString("GrcSchdMthd"));
			fm.setGrcMargin(rs.getBigDecimal("GrcMargin"));
			fm.setRepayMargin(rs.getBigDecimal("RepayMargin"));
			fm.setClosingStatus(rs.getString("ClosingStatus"));
			fm.setFinRepayPftOnFrq(rs.getBoolean("FinRepayPftOnFrq"));
			fm.setGrcProfitDaysBasis(rs.getString("GrcProfitDaysBasis"));
			fm.setGrcMinRate(rs.getBigDecimal("GrcMinRate"));
			fm.setGrcMaxRate(rs.getBigDecimal("GrcMaxRate"));
			fm.setGrcMaxAmount(rs.getBigDecimal("GrcMaxAmount"));
			fm.setRpyMinRate(rs.getBigDecimal("RpyMinRate"));
			fm.setRpyMaxRate(rs.getBigDecimal("RpyMaxRate"));
			fm.setManualSchedule(rs.getBoolean("ManualSchedule"));
			fm.setCalRoundingMode(rs.getString("CalRoundingMode"));
			fm.setRoundingTarget(rs.getInt("RoundingTarget"));
			fm.setRvwRateApplFor(rs.getString("RvwRateApplFor"));
			fm.setSchCalOnRvw(rs.getString("SchCalOnRvw"));
			fm.setPastduePftCalMthd(rs.getString("PastduePftCalMthd"));
			fm.setDroppingMethod(rs.getString("DroppingMethod"));
			fm.setRateChgAnyDay(rs.getBoolean("RateChgAnyDay"));
			fm.setPastduePftMargin(rs.getBigDecimal("PastduePftMargin"));
			fm.setFinRepayMethod(rs.getString("FinRepayMethod"));
			fm.setMigratedFinance(rs.getBoolean("MigratedFinance"));
			fm.setScheduleMaintained(rs.getBoolean("ScheduleMaintained"));
			fm.setScheduleRegenerated(rs.getBoolean("ScheduleRegenerated"));
			fm.setMandateID(JdbcUtil.getLong(rs.getObject("MandateID")));
			fm.setFinStatus(rs.getString("FinStatus"));
			fm.setDueBucket(rs.getInt("DueBucket"));
			fm.setFinStsReason(rs.getString("FinStsReason"));
			fm.setPromotionCode(rs.getString("PromotionCode"));
			fm.setFinCategory(rs.getString("FinCategory"));
			fm.setProductCategory(rs.getString("ProductCategory"));
			fm.setReAgeBucket(rs.getInt("ReAgeBucket"));
			fm.setTDSApplicable(rs.getBoolean("TDSApplicable"));
			fm.setTdsType(rs.getString("TdsType"));
			fm.setBpiTreatment(rs.getString("BpiTreatment"));
			fm.setFinRepaymentAmount(rs.getBigDecimal("FinRepaymentAmount"));
			fm.setGrcAdvType(rs.getString("GrcAdvType"));
			fm.setAdvType(rs.getString("AdvType"));
			fm.setSanBsdSchdle(rs.getBoolean("SanBsdSchdle"));
			fm.setPromotionSeqId(JdbcUtil.getLong(rs.getObject("PromotionSeqId")));
			fm.setSvAmount(rs.getBigDecimal("SvAmount"));
			fm.setCbAmount(rs.getBigDecimal("CbAmount"));
			fm.setEmployeeName(rs.getString("EmployeeName"));
			fm.setFinCurrAssetValue(rs.getBigDecimal("FinCurrAssetValue"));
			fm.setAlwGrcAdj(rs.getBoolean("AlwGrcAdj"));
			fm.setEndGrcPeriodAftrFullDisb(rs.getBoolean("EndGrcPeriodAftrFullDisb"));
			fm.setAutoIncGrcEndDate(rs.getBoolean("AutoIncGrcEndDate"));
			fm.setFinAssetValue(rs.getBigDecimal("FinAssetValue"));
			fm.setVersion(rs.getInt("Version"));
			fm.setLastMntOn(rs.getTimestamp("LastMntOn"));
			fm.setReferralId(rs.getString("ReferralId"));
			fm.setGraceTerms(rs.getInt("GraceTerms"));
			fm.setAlwMultiDisb(rs.getBoolean("Alwmultidisb"));
			fm.setWriteoffLoan(rs.getBoolean("WriteoffLoan"));
			fm.setSchdVersion(rs.getInt("SchdVersion"));
			fm.setNumberOfTerms(rs.getInt("NumberOfTerms"));
			fm.setManualSchdType(rs.getString("ManualSchdType"));
			fm.setOverdraftTxnChrgReq(rs.getBoolean("OverdraftTxnChrgReq"));
			fm.setOverdraftCalcChrg(rs.getString("OverdraftCalcChrg"));
			fm.setOverdraftChrgAmtOrPerc(rs.getBigDecimal("OverdraftChrgAmtOrPerc"));
			fm.setOverdraftChrCalOn(rs.getString("OverdraftChrCalOn"));
			fm.setStepFinance(rs.getBoolean("StepFinance"));
			fm.setAlwManualSteps(rs.getBoolean("AlwManualSteps"));
			fm.setCalcOfSteps(rs.getString("CalcOfSteps"));
			fm.setNoOfGrcSteps(rs.getInt("NoOfGrcSteps"));
			fm.setEntityCode(rs.getString("EntityCode"));
			fm.setUnderSettlement(rs.getBoolean("UnderSettlement"));
			fm.setUnderNpa(rs.getBoolean("UnderNpa"));

			return fm;
		});
	}
}