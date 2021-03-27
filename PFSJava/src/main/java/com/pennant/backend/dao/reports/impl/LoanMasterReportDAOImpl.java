package com.pennant.backend.dao.reports.impl;

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
import org.springframework.dao.DataAccessException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.PreparedStatementSetter;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.core.simple.ParameterizedBeanPropertyRowMapper;

import com.pennant.backend.dao.finance.impl.FinanceScheduleDetailDAOImpl;
import com.pennant.backend.dao.reports.LoanMasterReportDAO;
import com.pennant.backend.model.finance.FinRepayHeader;
import com.pennant.backend.model.finance.FinanceScheduleDetail;
import com.pennant.backend.model.finance.LoanReport;
import com.pennant.eod.constants.EodConstants;
import com.pennanttech.pennapps.core.App;
import com.pennanttech.pennapps.core.App.Database;
import com.pennanttech.pennapps.core.jdbc.BasicDao;
import com.pennanttech.pennapps.core.jdbc.JdbcUtil;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pennapps.core.util.DateUtil;

public class LoanMasterReportDAOImpl extends BasicDao<FinanceScheduleDetail> implements LoanMasterReportDAO {
	private static Logger logger = LogManager.getLogger(LoanMasterReportDAOImpl.class);
	FinanceScheduleDetailDAOImpl daoImpl = new FinanceScheduleDetailDAOImpl();

	@Override
	public List<LoanReport> getLoanReports(String finreference, Date fromDate, Date toDate) {
		StringBuilder sql = new StringBuilder("Select");
		sql.append(" FinReference, CustShrtName, CustCif, CustCtgDesc, CustCtgCode, LoanPurposeDesc");
		sql.append(", RepayProfitRate, CustCtgDesc, FinType, FinAssetValue, FinCurrAssetValue, FinIsActive");
		sql.append(", FinStartDate, CalMaturity, NumberOfTerms, AllowGrcPeriod, RepayRateBasis");
		sql.append(", RepayBaseRate, Finccy, RepaySpecialRate, RepayMargin, RpyMinRate, RpymaxRate");
		sql.append(", FinDivision, TotalPftCpz, NextRepayDate, RoundingMode, GraceTerms, CasteDesc");
		sql.append(", CpProvinceName, QuickDisb, FinAmount, RoundingTarget, MaturityDate");
		sql.append(" From LoanReport_View");

		if (StringUtils.isNotBlank(finreference)) {
			sql.append(" Where FinReference = ?");
		} else if (fromDate != null && toDate != null) {
			sql.append(" Where FinStartDate >= ? and FinStartDate <= ?");
		}

		logger.trace(Literal.SQL + sql.toString());

		return this.jdbcOperations.query(sql.toString(), new PreparedStatementSetter() {
			@Override
			public void setValues(PreparedStatement ps) throws SQLException {
				if (StringUtils.isNotBlank(finreference)) {
					ps.setString(1, finreference);
				} else if (fromDate != null && toDate != null) {
					ps.setDate(1, DateUtil.getSqlDate(fromDate));
					ps.setDate(2, DateUtil.getSqlDate(toDate));
				}
			}
		}, new RowMapper<LoanReport>() {
			@Override
			public LoanReport mapRow(ResultSet rs, int rowNum) throws SQLException {
				LoanReport loanReport = new LoanReport();
				loanReport.setFinReference(rs.getString("FinReference"));
				loanReport.setCustName(rs.getString("CustShrtName"));
				loanReport.setCustCIF(rs.getString("CustCif"));
				loanReport.setCustCategory(rs.getString("CustCtgDesc"));
				loanReport.setCustomerType(rs.getString("CustCtgCode"));
				loanReport.setProductDescription(rs.getString("LoanPurposeDesc"));
				loanReport.setOriginalROI(rs.getBigDecimal("RepayProfitRate"));
				loanReport.setCustCategory(rs.getString("CustCtgDesc"));
				loanReport.setFinType(rs.getString("FinType"));
				loanReport.setSanctioAmount(rs.getBigDecimal("FinAssetValue"));
				loanReport.setDisbursementAmount(rs.getBigDecimal("FinCurrAssetValue"));
				loanReport.setLoanStatus(rs.getBoolean("FinIsActive"));
				loanReport.setFinStartDate(rs.getDate("FinStartDate"));
				loanReport.setCalMaturity(rs.getDate("CalMaturity"));
				loanReport.setNumberOfTerms(rs.getInt("NumberOfTerms"));
				loanReport.setAlwGrcPeriod(rs.getBoolean("AllowGrcPeriod"));
				loanReport.setRepayRateBasis(rs.getString("RepayRateBasis"));
				loanReport.setRepayBaseRate(rs.getString("RepayBaseRate"));
				loanReport.setFinCcy(rs.getString("Finccy"));
				loanReport.setRepaySpecialRate(rs.getString("RepaySpecialRate"));
				loanReport.setRepayMargin(rs.getBigDecimal("RepayMargin"));
				loanReport.setRpyMinRate(rs.getBigDecimal("RpyMinRate"));
				loanReport.setRpyMaxRate(rs.getBigDecimal("RpymaxRate"));

				BigDecimal unDisbAmnt = BigDecimal.ZERO;
				if (loanReport.getSanctioAmount() != null) {
					unDisbAmnt = loanReport.getSanctioAmount().subtract(loanReport.getDisbursementAmount());
				}
				loanReport.setUnDisbursedAmount(unDisbAmnt);
				loanReport.setEntity(rs.getString("FinDivision"));
				loanReport.setCaptilizedIntrest(rs.getBigDecimal("TotalPftCpz"));
				loanReport.setNextRepayDate(rs.getDate("NextRepayDate"));
				loanReport.setRoundingMode(rs.getString("roundingMode"));
				loanReport.setGraceTerms(rs.getInt("GraceTerms"));
				loanReport.setCaste(rs.getString("CasteDesc"));
				loanReport.setBranchState(rs.getString("CpProvinceName"));
				loanReport.setQuickDisb(rs.getBoolean("QuickDisb"));
				loanReport.setFinAmount(rs.getBigDecimal("FinAmount"));
				loanReport.setRoundingTarget(rs.getInt("RoundingTarget"));
				loanReport.setMaturityDate(rs.getDate("MaturityDate"));
				return loanReport;
			}
		});
	}

	@Override
	public List<FinanceScheduleDetail> getFinScheduleDetails(String id, String type) {
		StringBuilder sql = getScheduleDetailQuery(type);
		sql.append(" Where FinReference = ? order by SchDate asc");
		logger.debug(Literal.SQL + sql.toString());
		FinanceScheduleDetailDAOImpl daoImpl = new FinanceScheduleDetailDAOImpl();
		RowMapper<FinanceScheduleDetail> rowMapper = daoImpl.new ScheduleDetailRowMapper(false);
		try {
			return this.jdbcTemplate.getJdbcOperations().query(sql.toString(), new PreparedStatementSetter() {
				@Override
				public void setValues(PreparedStatement ps) throws SQLException {
					ps.setString(1, id);
				}
			}, rowMapper);
		} catch (Exception e) {
			logger.warn(Literal.EXCEPTION, e);
		}

		return new ArrayList<>();
	}

	private StringBuilder getScheduleDetailQuery(String finReference) {
		StringBuilder sql = new StringBuilder("select");
		sql.append(" FinReference, SchDate, SchSeq, PftOnSchDate, CpzOnSchDate, RepayOnSchDate, RvwOnSchDate");
		sql.append(", DisbOnSchDate, DownpaymentOnSchDate, BalanceForPftCal, BaseRate, SplRate, MrgRate");
		sql.append(", ActRate, NoOfDays, CalOnIndRate, DayFactor, ProfitCalc, ProfitSchd, PrincipalSchd");
		sql.append(", RepayAmount, ProfitBalance, DisbAmount, DownPaymentAmount, CpzAmount, CpzBalance");
		sql.append(", OrgPft, OrgPri, OrgEndBal, OrgPlanPft, ClosingBalance, ProfitFraction, PrvRepayAmount");
		sql.append(", CalculatedRate, FeeChargeAmt, InsuranceAmt, FeeSchd, SchdFeePaid, SchdFeeOS");
		sql.append(", InsSchd, SchdInsPaid, TDSAmount, TDSPaid, PftDaysBasis, SchdPriPaid, SchdPftPaid");
		sql.append(", SchPriPaid, SchPftPaid, Specifier, DefSchdDate, SchdMethod, InstNumber, BpiOrHoliday");
		sql.append(", FrqDate, RecalLock");
		sql.append(", RefundOrWaiver, EarlyPaid, EarlyPaidBal, WriteoffPrincipal, WriteoffProfit, PresentmentId");
		sql.append(", WriteoffIns, WriteoffSchFee, PartialPaidAmt, TdsApplicable, SchdPftWaiver");
		sql.append(" From FinScheduleDetails");
		if (App.DATABASE == Database.SQL_SERVER) {
			sql.append(EodConstants.SQL_NOLOCK);
		}
		return sql;
	}

	@Override
	public List<FinRepayHeader> getFinRepayHeader(String reference) {
		logger.debug(Literal.ENTERING);
		List<FinRepayHeader> finRepayHeaders = null;
		StringBuilder sql = new StringBuilder();
		sql.append("Select finrepay.PriAmount,finrepay.finevent from finRepayHeader finrepay ");
		sql.append("join finreceiptdetail finrecdt on finrepay.receiptseqid=finrecdt.receiptseqid ");
		sql.append("join finreceiptheader finreceipt on finrecdt.receiptId=finreceipt.receiptId ");
		sql.append(" Where finrepay.FinReference = ? and finreceipt.ReceiptModeStatus in ('R','A')");

		logger.debug(Literal.SQL + sql.toString());
		try {
			finRepayHeaders = this.jdbcOperations.query(sql.toString(), new PreparedStatementSetter() {
				@Override
				public void setValues(PreparedStatement ps) throws SQLException {
					if (reference != null) {
						ps.setString(1, reference);
					}
				}
			}, new RowMapper<FinRepayHeader>() {
				@Override
				public FinRepayHeader mapRow(ResultSet rs, int rowNum) throws SQLException {
					FinRepayHeader finRepayHeader = new FinRepayHeader();
					finRepayHeader.setPriAmount(rs.getBigDecimal("PriAmount"));
					finRepayHeader.setFinEvent(rs.getString("FinEvent"));
					return finRepayHeader;
				}
			});
		} catch (Exception e) {
			logger.debug(Literal.EXCEPTION);
		}
		logger.debug(Literal.LEAVING);
		return finRepayHeaders;
	}

	@Override
	public List<FinanceScheduleDetail> getFinPftPaid(String id, Date appDate) {
		logger.debug(Literal.ENTERING);
		StringBuilder sql = getScheduleDetailQuery("");
		sql.append(" Where FinReference = ? and SchDate <=?");
		logger.debug(Literal.SQL + sql.toString());

		RowMapper<FinanceScheduleDetail> rowMapper = daoImpl.new ScheduleDetailRowMapper(false);
		try {
			return this.jdbcTemplate.getJdbcOperations().query(sql.toString(), new PreparedStatementSetter() {
				@Override
				public void setValues(PreparedStatement ps) throws SQLException {
					ps.setString(1, id);
					ps.setDate(2, JdbcUtil.getDate(appDate));
				}
			}, rowMapper);
		} catch (Exception e) {
			logger.warn(Literal.EXCEPTION, e);
		}

		return new ArrayList<>();
	}

	public int getMaxPendingOverDuePayment(String custCIF) {
		logger.debug(Literal.ENTERING);

		MapSqlParameterSource source = null;
		int maxPendingOverDueDays = 0;

		StringBuilder sql = new StringBuilder(" SELECT COALESCE(MAX(FinCurODDays),0) From FinODDetails od");
		sql.append(" join customers c on od.custid = c.custid ");
		sql.append(" Where c.custCIF =:CustCIF AND FinCurODAmt > 0 ");

		logger.debug("selectSql: " + sql.toString());

		source = new MapSqlParameterSource();
		source.addValue("CustCIF", custCIF);

		try {
			maxPendingOverDueDays = this.jdbcTemplate.queryForObject(sql.toString(), source, Integer.class);
		} catch (DataAccessException e) {
			logger.error(e);
		}
		logger.debug(Literal.LEAVING);
		return maxPendingOverDueDays;
	}

	@Override
	public int getRevisedTenure(String finReference) {
		logger.debug(Literal.ENTERING);

		MapSqlParameterSource source = null;
		int revisedTenure = 0;

		StringBuilder sql = new StringBuilder("SELECT count(*) from FinScheduleDetails ");
		sql.append("where FinReference=:FinReference and specifier not in ('G','E') and instnumber > 0  ");

		logger.debug("selectSql: " + sql.toString());

		source = new MapSqlParameterSource();
		source.addValue("FinReference", finReference);

		try {
			revisedTenure = this.jdbcTemplate.queryForObject(sql.toString(), source, Integer.class);
		} catch (DataAccessException e) {
			logger.error(e);
		}
		logger.debug(Literal.LEAVING);
		return revisedTenure;
	}

	@Override
	public List<FinanceScheduleDetail> getScheduleDetail(String finReference, Date curBussDate) {
		StringBuilder sql = new StringBuilder("Select");
		sql.append(" SchdPftPaid, SchdPriPaid, SchPftPaid, SchPriPaid");
		sql.append(", FinReference, SchDate, ProfitSchd, PrincipalSchd");
		sql.append(", ProfitCalc, ProfitFraction, NoOfDays, InstNumber");
		sql.append(" From FinScheduleDetails");
		sql.append(" Where FinReference = ?");

		logger.debug(Literal.SQL + sql.toString());

		return jdbcOperations.query(sql.toString(), ps -> {
			ps.setString(1, finReference);
		}, (rs, i) -> {
			FinanceScheduleDetail fsd = new FinanceScheduleDetail();

			fsd.setSchdPftPaid(rs.getBigDecimal("SchdPftPaid"));
			fsd.setSchdPriPaid(rs.getBigDecimal("SchdPriPaid"));
			fsd.setSchPftPaid(rs.getBoolean("SchPftPaid"));
			fsd.setSchPriPaid(rs.getBoolean("SchPriPaid"));
			fsd.setFinReference(rs.getString("FinReference"));
			fsd.setSchDate(JdbcUtil.getDate(rs.getDate("SchDate")));
			fsd.setProfitSchd(rs.getBigDecimal("ProfitSchd"));
			fsd.setPrincipalSchd(rs.getBigDecimal("PrincipalSchd"));
			fsd.setProfitCalc(rs.getBigDecimal("ProfitCalc"));
			fsd.setProfitFraction(rs.getBigDecimal("ProfitFraction"));
			fsd.setNoOfDays(rs.getInt("NoOfDays"));
			fsd.setInstNumber(rs.getInt("InstNumber"));

			return fsd;
		});
	}

	@Override
	public FinanceScheduleDetail getNextSchPayment(String finReference, Date curBussDate) {
		logger.debug(Literal.ENTERING);

		FinanceScheduleDetail financeScheduleDetail = new FinanceScheduleDetail();
		financeScheduleDetail.setFinReference(finReference);
		financeScheduleDetail.setSchDate(curBussDate);

		StringBuilder selectSql = new StringBuilder(
				"Select * from (select ROW_NUMBER() Over(order by Schdate) row_num, ");
		selectSql.append(" FinReference, SchDate, ProfitSchd, PrincipalSchd,ProfitCalc,ProfitFraction, ");
		selectSql.append("noofdays,schdpftpaid, schdpripaid  FROM FinScheduleDetails Where");
		selectSql.append(" FinReference =:FinReference AND SchDate>:SchDate )T where row_num = 1 ");

		logger.debug("selectSql: " + selectSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(financeScheduleDetail);
		RowMapper<FinanceScheduleDetail> typeRowMapper = ParameterizedBeanPropertyRowMapper
				.newInstance(FinanceScheduleDetail.class);

		try {
			financeScheduleDetail = this.jdbcTemplate.queryForObject(selectSql.toString(), beanParameters,
					typeRowMapper);
		} catch (EmptyResultDataAccessException e) {
			logger.warn(Literal.EXCEPTION, e);
			financeScheduleDetail = null;
		}

		logger.debug(Literal.LEAVING);
		return financeScheduleDetail;
	}
}
