package com.pennant.backend.dao.others.external.reports.impl;

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
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.PreparedStatementSetter;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;

import com.pennant.backend.dao.finance.impl.FinanceScheduleDetailDAOImpl;
import com.pennant.backend.dao.others.external.reports.LoanMasterReportDAO;
import com.pennant.backend.model.collateral.CollateralAssignment;
import com.pennant.backend.model.configuration.VASRecording;
import com.pennant.backend.model.finance.FinAdvancePayments;
import com.pennant.backend.model.finance.FinFeeDetail;
import com.pennant.backend.model.finance.FinRepayHeader;
import com.pennant.backend.model.finance.FinanceScheduleDetail;
import com.pennant.backend.model.finance.VasMovementDetail;
import com.pennant.backend.model.others.external.reports.LoanReport;
import com.pennant.eod.constants.EodConstants;
import com.pennanttech.pennapps.core.App;
import com.pennanttech.pennapps.core.App.Database;
import com.pennanttech.pennapps.core.jdbc.BasicDao;
import com.pennanttech.pennapps.core.jdbc.JdbcUtil;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pennapps.core.resource.Message;
import com.pennanttech.pennapps.core.util.DateUtil;

public class LoanMasterReportDAOImpl extends BasicDao<FinanceScheduleDetail> implements LoanMasterReportDAO {
	private static Logger logger = LogManager.getLogger(LoanMasterReportDAOImpl.class);
	FinanceScheduleDetailDAOImpl daoImpl = new FinanceScheduleDetailDAOImpl();

	@Override
	public List<LoanReport> getLoanReports(String finreference, Date fromDate, Date toDate) {
		logger.debug(Literal.ENTERING);

		StringBuilder selectSql = new StringBuilder();
		selectSql.append("Select * from loanReport_view ");
		if (StringUtils.isNotBlank(finreference)) {
			selectSql.append("where finreference= ?");
		} else if (fromDate != null && toDate != null) {
			selectSql.append("where finstartdate>=? and finstartdate<=? ");
		}
		logger.trace(Literal.SQL + selectSql.toString());

		return this.jdbcOperations.query(selectSql.toString(), new PreparedStatementSetter() {
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
				loanReport.setEntity(rs.getString("FinDivision"));
				loanReport.setCaptilizedIntrest(rs.getBigDecimal("TotalPftCpz"));
				loanReport.setNextRepayDate(rs.getDate("nextRepayDate"));
				loanReport.setRoundingMode(rs.getString("roundingMode"));
				loanReport.setGraceTerms(rs.getInt("GraceTerms"));
				loanReport.setCaste(rs.getString("castedesc"));
				loanReport.setBranchState(rs.getString("cpprovincename"));
				loanReport.setQuickDisb(rs.getBoolean("quickDisb"));
				loanReport.setFinAmount(rs.getBigDecimal("finAmount"));
				loanReport.setRoundingTarget(rs.getInt("RoundingTarget"));
				loanReport.setMaturityDate(rs.getDate("maturitydate"));
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

		return this.jdbcTemplate.getJdbcOperations().query(sql.toString(), new PreparedStatementSetter() {
			@Override
			public void setValues(PreparedStatement ps) throws SQLException {
				ps.setString(1, id);
			}
		}, rowMapper);
	}

	private StringBuilder getScheduleDetailQuery(String finReference) {
		StringBuilder sql = new StringBuilder("select");
		sql.append(" FinReference, SchDate, SchSeq, PftOnSchDate, CpzOnSchDate, RepayOnSchDate, RvwOnSchDate");
		sql.append(", DisbOnSchDate, DownpaymentOnSchDate, BalanceForPftCal, BaseRate, SplRate, MrgRate");
		sql.append(", ActRate, NoOfDays, DayFactor, ProfitCalc, ProfitSchd, PrincipalSchd");
		sql.append(", RepayAmount, ProfitBalance, DisbAmount, DownPaymentAmount, CpzAmount, CpzBalance");
		sql.append(", OrgPft, OrgPri, OrgEndBal, OrgPlanPft, ClosingBalance, ProfitFraction, PrvRepayAmount");
		sql.append(", CalculatedRate, FeeChargeAmt, FeeSchd, SchdFeePaid, SchdFeeOS");
		sql.append(", TDSAmount, TDSPaid, PftDaysBasis, SchdPriPaid, SchdPftPaid");
		sql.append(", SchPriPaid, SchPftPaid, Specifier, DefSchdDate, SchdMethod, InstNumber, BpiOrHoliday");
		sql.append(", FrqDate, RecalLock");
		sql.append(", RefundOrWaiver, EarlyPaid, EarlyPaidBal, WriteoffPrincipal, WriteoffProfit, PresentmentId");
		sql.append(", WriteoffSchFee, PartialPaidAmt, TdsApplicable, SchdPftWaiver");
		sql.append(" From FinScheduleDetails");
		if (App.DATABASE == Database.SQL_SERVER) {
			sql.append(EodConstants.SQL_NOLOCK);
		}
		return sql;
	}

	@Override
	public List<FinRepayHeader> getFinRepayHeader(String reference) {
		logger.debug(Literal.ENTERING);

		StringBuilder sql = new StringBuilder();
		sql.append("Select finrepay.PriAmount,finrepay.finevent from finRepayHeader finrepay ");
		sql.append("join finreceiptdetail finrecdt on finrepay.receiptseqid=finrecdt.receiptseqid ");
		sql.append("join finreceiptheader finreceipt on finrecdt.receiptId=finreceipt.receiptId ");
		sql.append(" Where finrepay.FinReference = ? and finreceipt.ReceiptModeStatus in ('R','A')");

		logger.debug(Literal.SQL + sql.toString());

		return this.jdbcOperations.query(sql.toString(), new PreparedStatementSetter() {
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
	}

	@Override
	public List<FinanceScheduleDetail> getFinPftPaid(String id, Date appDate) {
		logger.debug(Literal.ENTERING);
		StringBuilder sql = getScheduleDetailQuery("");
		sql.append(" Where FinReference = ? and SchDate <=?");
		logger.debug(Literal.SQL + sql.toString());

		RowMapper<FinanceScheduleDetail> rowMapper = daoImpl.new ScheduleDetailRowMapper(false);

		return this.jdbcTemplate.getJdbcOperations().query(sql.toString(), new PreparedStatementSetter() {
			@Override
			public void setValues(PreparedStatement ps) throws SQLException {
				ps.setString(1, id);
				ps.setDate(2, JdbcUtil.getDate(appDate));
			}
		}, rowMapper);
	}

	public int getMaxPendingOverDuePayment(String custCIF) {
		logger.debug(Literal.ENTERING);

		StringBuilder sql = new StringBuilder(" SELECT COALESCE(MAX(FinCurODDays),0) From FinODDetails od");
		sql.append(" join customers c on od.custid = c.custid ");
		sql.append(" Where c.custCIF =:CustCIF AND FinCurODAmt > 0 ");

		logger.debug("selectSql: " + sql.toString());

		MapSqlParameterSource source = new MapSqlParameterSource();
		source.addValue("CustCIF", custCIF);

		return this.jdbcTemplate.queryForObject(sql.toString(), source, Integer.class);
	}

	@Override
	public int getRevisedTenure(String finReference) {
		logger.debug(Literal.ENTERING);

		StringBuilder sql = new StringBuilder("SELECT count(*) from FinScheduleDetails ");
		sql.append("where FinReference=:FinReference and specifier not in ('G','E') and instnumber > 0  ");

		logger.debug("selectSql: " + sql.toString());

		MapSqlParameterSource source = new MapSqlParameterSource();
		source.addValue("FinReference", finReference);

		return this.jdbcTemplate.queryForObject(sql.toString(), source, Integer.class);
	}

	@Override
	public List<FinanceScheduleDetail> getScheduleDetail(String finReference, Date curBussDate) {
		logger.debug(Literal.ENTERING);
		FinanceScheduleDetail financeScheduleDetail = new FinanceScheduleDetail();
		financeScheduleDetail.setFinReference(finReference);
		StringBuilder selectSql = new StringBuilder("Select schdpftpaid, schdpripaid,schpftpaid, schpripaid,  ");
		selectSql.append(" FinReference, SchDate, ProfitSchd, PrincipalSchd,ProfitCalc,ProfitFraction,noofdays, ");
		selectSql.append(" InstNumber FROM FinScheduleDetails Where FinReference =:FinReference ");
		logger.debug(Literal.SQL + selectSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(financeScheduleDetail);
		RowMapper<FinanceScheduleDetail> typeRowMapper = BeanPropertyRowMapper.newInstance(FinanceScheduleDetail.class);

		return this.jdbcTemplate.query(selectSql.toString(), beanParameters, typeRowMapper);
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
		RowMapper<FinanceScheduleDetail> typeRowMapper = BeanPropertyRowMapper.newInstance(FinanceScheduleDetail.class);

		try {
			return this.jdbcTemplate.queryForObject(selectSql.toString(), beanParameters, typeRowMapper);
		} catch (EmptyResultDataAccessException e) {
			logger.warn(Message.NO_RECORD_FOUND);
			return null;
		}
	}

	/**
	 * Fetch the List of Finance Disbursement Detail Records by key field
	 * 
	 * @param id   (String)
	 * @param type (String) ""/_Temp/_View
	 * @return WIFFinanceDisbursement
	 */
	@Override
	public LoanReport getFinanceDisbursementDetails(final String id, String type, boolean isWIF) {
		logger.debug(Literal.ENTERING);
		StringBuilder sql = new StringBuilder("Select");
		sql.append(" MAX(DisbDate) as LastDisbDate, MIN(DisbDate) as FirstDisbDate");

		if (isWIF) {
			sql.append(" From WIFFinDisbursementDetails");
		} else {
			sql.append(" From FinDisbursementDetails");
		}

		sql.append(StringUtils.trimToEmpty(type));
		sql.append("  Where FinReference = ?");

		return this.jdbcOperations.queryForObject(sql.toString(), new RowMapper<LoanReport>() {
			@Override
			public LoanReport mapRow(ResultSet rs, int rowNum) throws SQLException {
				LoanReport loanReport = new LoanReport();
				loanReport.setFirstDisbDate(rs.getDate("FirstDisbDate"));
				loanReport.setLastDisbDate(rs.getDate("LastDisbDate"));
				return loanReport;
			}
		}, id);
	}

	/**
	 * Method for Fetching List of Assigned Collateral to the Reference based on Module
	 */
	@Override
	public List<CollateralAssignment> getCollateralAssignmentByFinRef(String reference, String moduleName,
			String type) {
		logger.debug(Literal.ENTERING);
		StringBuilder sql = new StringBuilder("Select");
		sql.append(" CollateralRef");

		if (StringUtils.trimToEmpty(type).contains("View")) {
			sql.append(", CollateralValue, CollateralType");
		}

		sql.append(" from CollateralAssignment");
		sql.append(StringUtils.trimToEmpty(type));
		sql.append(" Where Reference = ? and Module = ?");

		logger.trace(Literal.SQL + sql.toString());

		return this.jdbcOperations.query(sql.toString(), new PreparedStatementSetter() {
			@Override
			public void setValues(PreparedStatement ps) throws SQLException {
				ps.setString(1, reference);
				ps.setString(2, moduleName);
			}
		}, new RowMapper<CollateralAssignment>() {
			@Override
			public CollateralAssignment mapRow(ResultSet rs, int rowNum) throws SQLException {
				CollateralAssignment collateralAssignment = new CollateralAssignment();

				collateralAssignment.setCollateralValue(rs.getBigDecimal("CollateralValue"));
				collateralAssignment.setCollateralType(rs.getString("CollateralType"));
				collateralAssignment.setCollateralRef(rs.getString("CollateralRef"));

				return collateralAssignment;
			}
		});
	}

	@Override
	public List<VASRecording> getLoanReportVasRecordingByRef(String finReference) {
		MapSqlParameterSource source = null;
		StringBuilder sql = null;
		List<VASRecording> vasRecordingList = new ArrayList<>();
		sql = new StringBuilder("Select vr.fee,vs.modeofPayment,vr.productcode ");
		sql.append("from vasrecording vr JOIN VasStructure vs ON vr.productcode = vs.productcode ");
		sql.append("Where PrimaryLinkRef=:PrimaryLinkRef");
		logger.debug("selectSql: " + sql.toString());

		RowMapper<VASRecording> typeRowMapper = BeanPropertyRowMapper.newInstance(VASRecording.class);

		source = new MapSqlParameterSource();
		source.addValue("PrimaryLinkRef", finReference);
		vasRecordingList = this.jdbcTemplate.query(sql.toString(), source, typeRowMapper);
		logger.debug(Literal.LEAVING);
		return vasRecordingList;
	}

	/**
	 * Method for get the FinODDetails Object by Key finReference
	 */
	@Override
	public LoanReport getFinODBalByFinRef(String finReference) {
		logger.debug(Literal.ENTERING);

		StringBuilder sql = new StringBuilder("Select");
		sql.append(" SUM(FinCurODPri) as LoanDebtors_Principal, SUM(FinCurODPft) as LoanDebtors_Interest ");
		sql.append(" from FinODDetails");
		sql.append("  Where FinReference = ?");

		logger.trace(Literal.SQL + sql.toString());

		return this.jdbcOperations.queryForObject(sql.toString(), new RowMapper<LoanReport>() {
			@Override
			public LoanReport mapRow(ResultSet rs, int rowNum) throws SQLException {
				LoanReport loanReport = new LoanReport();
				loanReport.setLoanDebtors_Interest(rs.getBigDecimal("LoanDebtors_Interest"));
				loanReport.setLoanDebtors_Principal(rs.getBigDecimal("LoanDebtors_Principal"));
				return loanReport;
			}
		}, finReference);
	}

	@Override
	public List<FinAdvancePayments> getFinAdvancePaymentsByFinRef(final String id, String type) {
		logger.debug(Literal.ENTERING);

		StringBuilder sql = new StringBuilder("Select");
		sql.append(" AmtToBeReleased , Status ");
		sql.append(" from FinAdvancePayments");
		sql.append(StringUtils.trimToEmpty(type));
		sql.append("  Where FinReference = ?");

		logger.trace(Literal.SQL + sql.toString());

		return this.jdbcOperations.query(sql.toString(), new PreparedStatementSetter() {
			@Override
			public void setValues(PreparedStatement ps) throws SQLException {
				int index = 1;
				ps.setString(index, id);
			}
		}, new RowMapper<FinAdvancePayments>() {
			@Override
			public FinAdvancePayments mapRow(ResultSet rs, int rowNum) throws SQLException {
				FinAdvancePayments finAdvancePayments = new FinAdvancePayments();

				finAdvancePayments.setAmtToBeReleased(rs.getBigDecimal("AmtToBeReleased"));
				finAdvancePayments.setStatus(rs.getString("Status"));

				return finAdvancePayments;
			}
		});
	}

	@Override
	public List<VasMovementDetail> getVasMovementDetailByRef(String finReference, String type) {
		logger.debug(Literal.ENTERING);
		VasMovementDetail vasMovementDetail = new VasMovementDetail();
		vasMovementDetail.setFinReference(finReference);

		StringBuilder selectSql = new StringBuilder("Select");
		selectSql.append(" VasMovementId,VasMovementDetailId,FinReference,VasReference,");
		selectSql.append(" MovementDate, MovementAmt,VasProvider,VasProduct,VasAmount");
		selectSql.append(" From VasMovementDetails");
		selectSql.append(StringUtils.trimToEmpty(type));
		selectSql.append(" Where FinReference =:FinReference");

		logger.trace(Literal.SQL + selectSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(vasMovementDetail);
		RowMapper<VasMovementDetail> typeRowMapper = BeanPropertyRowMapper.newInstance(VasMovementDetail.class);

		return this.jdbcTemplate.query(selectSql.toString(), beanParameters, typeRowMapper);
	}

	@Override
	public List<FinFeeDetail> getFinFeeDetailByFinRef(final String reference, boolean isWIF, String type) {
		logger.debug(Literal.ENTERING);

		StringBuilder sql = new StringBuilder("Select");
		sql.append(" FeeScheduleMethod, RemainingFee ");

		if (isWIF) {
			sql.append(" From WIFFinFeeDetail");
		} else {
			sql.append(" From FinFeeDetail");
		}

		sql.append(StringUtils.trimToEmpty(type));
		sql.append(" Where FinReference = ?");

		logger.trace(Literal.SQL + sql.toString());

		return this.jdbcOperations.query(sql.toString(), new PreparedStatementSetter() {
			@Override
			public void setValues(PreparedStatement ps) throws SQLException {
				int index = 1;
				ps.setString(index, reference);
			}
		}, new RowMapper<FinFeeDetail>() {
			@Override
			public FinFeeDetail mapRow(ResultSet rs, int rowNum) throws SQLException {
				FinFeeDetail finFeeDetail = new FinFeeDetail();

				finFeeDetail.setFeeScheduleMethod(rs.getString("FeeScheduleMethod"));
				finFeeDetail.setRemainingFee(rs.getBigDecimal("RemainingFee"));

				return finFeeDetail;
			}
		});
	}
}
