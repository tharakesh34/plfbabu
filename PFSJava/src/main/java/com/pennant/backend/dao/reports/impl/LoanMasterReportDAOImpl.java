package com.pennant.backend.dao.reports.impl;

import java.math.BigDecimal;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
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

public class LoanMasterReportDAOImpl extends BasicDao<FinanceScheduleDetail> implements LoanMasterReportDAO {
	private static Logger logger = Logger.getLogger(LoanMasterReportDAOImpl.class);
	FinanceScheduleDetailDAOImpl daoImpl = new FinanceScheduleDetailDAOImpl();

	@Override
	public List<LoanReport> getLoanReports(String finreference) {
		logger.debug(Literal.ENTERING);
		List<LoanReport> loanReport = null;
		StringBuilder selectSql = new StringBuilder();
		selectSql.append("Select lr.* from loanReport_view lr INNER JOIN ");
		selectSql.append("LoanMasterReport_MVIEW lm on (lr.finreference = lm.finreference) ");
		if (StringUtils.isNotBlank(finreference)) {
			selectSql.append("where lr.finreference= ?");
		}
		logger.trace(Literal.SQL + selectSql.toString());
		try {
			loanReport = this.jdbcOperations.query(selectSql.toString(), new PreparedStatementSetter() {
				@Override
				public void setValues(PreparedStatement ps) throws SQLException {
					if (StringUtils.isNotBlank(finreference)) {
						ps.setString(1, finreference);
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
					loanReport.setCustomerType(rs.getString("custctgcode"));
					loanReport.setProductDescription(rs.getString("loanpurposedesc"));
					loanReport.setOriginalROI(rs.getBigDecimal("RepayProfitRate"));
					loanReport.setCustCategory(rs.getString("CustCtgDesc"));
					loanReport.setFinType(rs.getString("FinType"));
					loanReport.setSanctioAmount(rs.getBigDecimal("FinAssetValue"));
					loanReport.setDisbursementAmount(rs.getBigDecimal("FinCurrAssetValue"));
					loanReport.setLoanStatus(rs.getBoolean("FinIsActive"));
					loanReport.setFinStartDate(rs.getDate("FinStartDate"));
					loanReport.setCalMaturity(rs.getDate("CalMaturity"));
					loanReport.setNumberOfTerms(rs.getInt("numberOfTerms"));
					loanReport.setAlwGrcPeriod(rs.getBoolean("allowGrcPeriod"));
					loanReport.setRepayRateBasis(rs.getString("repayRateBasis"));
					loanReport.setRepayBaseRate(rs.getString("repayBaseRate"));
					loanReport.setFinCcy(rs.getString("finccy"));
					loanReport.setRepaySpecialRate(rs.getString("repaySpecialRate"));
					loanReport.setRepayMargin(rs.getBigDecimal("repaymargin"));
					loanReport.setRpyMinRate(rs.getBigDecimal("rpyMinRate"));
					loanReport.setRpyMaxRate(rs.getBigDecimal("rpymaxRate"));

					BigDecimal unDisbAmnt = BigDecimal.ZERO;
					if (loanReport.getSanctioAmount() != null) {
						unDisbAmnt = loanReport.getSanctioAmount().subtract(loanReport.getDisbursementAmount());
					}
					loanReport.setUnDisbursedAmount(unDisbAmnt);
					loanReport.setEntity(rs.getString("FinDivision"));
					loanReport.setCaptilizedIntrest(rs.getBigDecimal("TotalPftCpz"));
					loanReport.setNextRepayDate(rs.getDate("nextRepayDate"));
					loanReport.setRoundingMode(rs.getString("roundingMode"));
					loanReport.setPrvMthAmz(rs.getBigDecimal("PrvMthAmz"));
					loanReport.setPftAmz(rs.getBigDecimal("pftamz"));
					loanReport.setGraceTerms(rs.getInt("GraceTerms"));
					loanReport.setCaste(rs.getString("castedesc"));
					loanReport.setBranchState(rs.getString("cpprovincename"));
					loanReport.setQuickDisb(rs.getBoolean("quickDisb"));
					loanReport.setFinAmount(rs.getBigDecimal("finAmount"));
					loanReport.setRoundingTarget(rs.getInt("RoundingTarget"));
					return loanReport;
				}
			});
		} catch (EmptyResultDataAccessException e) {
			logger.error(Literal.EXCEPTION, e);
		}
		logger.debug(Literal.LEAVING);
		return loanReport;

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
		logger.debug(Literal.ENTERING);
		FinanceScheduleDetail financeScheduleDetail = new FinanceScheduleDetail();
		financeScheduleDetail.setFinReference(finReference);
		financeScheduleDetail.setSchDate(curBussDate);

		StringBuilder selectSql = new StringBuilder("Select schdpftpaid, schdpripaid,schpftpaid, schpripaid  ");
		selectSql.append(" FinReference, SchDate, ProfitSchd, PrincipalSchd,ProfitCalc,ProfitFraction,noofdays");
		selectSql.append(" FROM FinScheduleDetails Where FinReference =:FinReference ");
		selectSql.append(" AND SchDate<=:SchDate order by SchDate desc");
		logger.debug(Literal.SQL + selectSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(financeScheduleDetail);
		RowMapper<FinanceScheduleDetail> typeRowMapper = ParameterizedBeanPropertyRowMapper
				.newInstance(FinanceScheduleDetail.class);
		try {
			return this.jdbcTemplate.query(selectSql.toString(), beanParameters, typeRowMapper);
		} catch (Exception e) {
			logger.warn(Literal.EXCEPTION, e);
		}

		logger.debug(Literal.LEAVING);
		return null;
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
