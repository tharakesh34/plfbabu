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
		sql.append(", sum(BalanceAmount) BalanceAmt");
		sql.append(" From FinExcessAmount ea");
		sql.append(" Inner Join FinanceMain fm on fm.FinID = ea.FinID");
		sql.append(" Inner Join Customers c on c.CustId = fm.CustId");
		sql.append(" Where  AmountType = ? and BalanceAmt > ? and fm.FinIsActive = ? and");
		sql.append(" fm.WriteoffLoan = ? Group by fm.FinID, fm.FinReference, AmountType, ExcessId");
		sql.append(" Union All");
		sql.append(" Select CustId, CustCoreBank, FinId, ReferenceId, AmountType");
		sql.append(", sum(BalanceAmt) BalanceAmount from ");
		sql.append(" (Select c.CustId, c.CustCoreBank, fm.FinId, AdviseId ReferenceId, ? AmountType");
		sql.append(", BalanceAmt From ManualAdvise ma");
		sql.append(" Inner Join FinanceMain fm on fm.FinID = ma.FinID");
		sql.append(" Inner Join Customers c on c.CustId = fm.CustId");
		sql.append(" Where  ma.AdviseType = ? and BalanceAmt > ? and fm.FinIsActive = ? and");
		sql.append(" fm.WriteoffLoan = ?) it Group by it.FinID, it.AmountType, it.ReferenceId) T");

		logger.debug(Literal.SQL + sql.toString());

		try {
			jdbcOperations.update(sql.toString(), ps -> {
				int index = 1;

				ps.setDate(index++, JdbcUtil.getDate(valueDate));
				ps.setString(index++, day);
				ps.setString(index++, thresholdValue);
				ps.setString(index++, "E");
				ps.setInt(index++, 0);
				ps.setInt(index++, 1);
				ps.setInt(index++, 0);
				ps.setString(index++, "P");
				ps.setInt(index++, 2);
				ps.setInt(index++, 0);
				ps.setInt(index++, 1);
				ps.setInt(index, 0);
			});
		} catch (DuplicateKeyException e) {
			throw new ConcurrencyException(e);
		}
	}

	@Override
	public void deleteQueue() {
		jdbcOperations.update("Delete From Cross_Loan_KnockOff_Queue");
	}

	@Override
	public long prepareQueue() {

		String sql = "Insert Into Cross_Loan_KnockOff_Queue(ID, CustID) Select row_number() over(order by CustID) ID, CustID From (Select distinct CustID From Cross_Loan_KnockOff_Stage) T";

		if (CustomerExtension.CUST_CORE_BANK_ID) {
			sql = "Insert Into Cross_Loan_KnockOff_Queue(ID, CoreBankId) Select row_number() over(order by CoreBankId) ID, CoreBankId From (Select distinct CoreBankId From Cross_Loan_KnockOff_Stage) T";
		}

		logger.debug(Literal.SQL + sql);

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

		logger.debug(Literal.SQL + sql);
		return this.jdbcOperations.queryForObject(sql, Long.class, EodConstants.PROGRESS_WAIT);
	}

	@Override
	public int updateThreadID(long from, long to, int threadId) {
		String sql = "Update Cross_Loan_KnockOff_Queue Set ThreadId = ? Where Id > ? and Id <= ?  and ThreadId = ?";

		logger.debug(Literal.SQL + sql);

		try {
			return this.jdbcOperations.update(sql, threadId, from, to, 0);
		} catch (DataAccessException dae) {
			logger.error(Literal.EXCEPTION, dae);
		}

		return 0;
	}

	private List<Queing> getQueingRecords() {
		String sql = "Select row_number() over(order by id) resteID, ID from Cross_Loan_KnockOff_Queue";

		logger.debug(Literal.SQL + sql);

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

		logger.debug(Literal.SQL + sql);

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

			logger.debug(Literal.SQL + sql);

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

			logger.debug(Literal.SQL + sql);

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

			logger.debug(Literal.SQL + sql);

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
		sql.append(" Inner Join FinanceMain fm fm.FinId = clk.FinId");

		if (CustomerExtension.CUST_CORE_BANK_ID) {
			sql.append(" Where CoreBankId = ?");
		} else {
			sql.append("Where CustId = ?");
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
		sql.append(" Where ake.valuedate = ? and ake.ProcessingFlag = ?");
		sql.append(" and ak.ExecutionDays like ?");
		sql.append(" Order by ExcessId, akl.KnockOffOrder, akf.FeeOrder");

		logger.debug(Literal.SQL + sql.toString());

		try {
			jdbcOperations.update(sql.toString(), ps -> {
				int index = 1;

				ps.setInt(index++, 1);
				ps.setDate(index++, JdbcUtil.getDate(valueDate));
				ps.setInt(index++, 0);
				ps.setString(index, "%" + day + "%");
			});
		} catch (DuplicateKeyException e) {
			throw new ConcurrencyException(e);
		}
	}

	@Override
	public List<ExcessKnockOffDetails> getStageDataByReference(long finID) {

		StringBuilder sql = new StringBuilder("Select");
		sql.append(
				" clk.ID, clk.KnockOffID, clk.ExcessID, clk.FinType, clk.Code, clk.ExecutionDays, clk.FinCcy, clk.FeeTypeCode");
		sql.append(", clk.KnockOffOrder, clk.FeeOrder");
		sql.append(" from CROSS_LOAN_KNOCKOFF_DTL_STAGE clkd");
		sql.append(" Inner Join CROSS_LOAN_KNOCKOFF_STAGE clk on clk.id = clkd.excessid ");
		sql.append(" Inner join Financemain fm fm.finid = clk.finid ");
		sql.append(" Where fm.finId = ?");

		logger.debug(Literal.SQL.concat(sql.toString()));

		return this.jdbcOperations.query(sql.toString(), ps -> {
			ps.setLong(1, finID);
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
		sql.append(" FinId, FinReference, FinType, AppDate");

		if (CustomerExtension.CUST_CORE_BANK_ID) {
			sql.append(" Where CoreBankId = ?");
		} else {
			sql.append("Where CustId = ?");
		}
		sql.append(" and finId = ?");

		logger.debug(Literal.SQL.concat(sql.toString()));

		return this.jdbcOperations.query(sql.toString(), ps -> {
			if (CustomerExtension.CUST_CORE_BANK_ID) {
				ps.setString(1, coreBankId);
			} else {
				ps.setLong(1, custId);
			}
			ps.setLong(2, finId);
		}, (rs, rowNum) -> {
			FinanceMain fm = new FinanceMain();

			fm.setFinID(rs.getLong("FinId"));
			fm.setFinReference(rs.getString("FinReference"));
			fm.setFinType(rs.getString("FinType"));
			fm.setAppDate(rs.getDate("AppDate"));

			return fm;
		});

	}

}
