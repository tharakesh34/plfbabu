package com.pennant.backend.dao.reports.impl;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.PreparedStatementSetter;
import org.springframework.jdbc.core.RowMapper;

import com.pennant.backend.dao.reports.CashFlowReportDAO;
import com.pennant.backend.model.finance.CashFlow;
import com.pennant.backend.model.finance.FinODDetails;
import com.pennant.backend.model.finance.FinRepayHeader;
import com.pennanttech.pennapps.core.jdbc.BasicDao;
import com.pennanttech.pennapps.core.resource.Literal;

public class CashFlowReportDAOImpl extends BasicDao<CashFlow> implements CashFlowReportDAO {
	private static Logger logger = Logger.getLogger(CashFlowReportDAOImpl.class);

	@Override
	public List<CashFlow> getCashFlowDetails() {
		logger.debug(Literal.ENTERING);
		List<CashFlow> cashFlows = null;
		StringBuilder selectSql = new StringBuilder();
		selectSql.append("Select FinReference from CashFlowReport_VIEW ");
		logger.trace(Literal.SQL + selectSql.toString());
		try {
			cashFlows = this.jdbcOperations.query(selectSql.toString(), new PreparedStatementSetter() {
				@Override
				public void setValues(PreparedStatement ps) throws SQLException {

				}
			}, new RowMapper<CashFlow>() {
				@Override
				public CashFlow mapRow(ResultSet rs, int rowNum) throws SQLException {
					CashFlow cashFlow = new CashFlow();
					cashFlow.setLan(rs.getString("FinReference"));
					return cashFlow;
				}
			});
		} catch (EmptyResultDataAccessException e) {
			logger.error(Literal.EXCEPTION, e);
		}
		logger.debug(Literal.LEAVING);
		return cashFlows;

	}

	@Override
	public List<FinRepayHeader> getFinRepayHeader(String reference) {
		List<FinRepayHeader> finRepayHeaders = null;
		StringBuilder sql = new StringBuilder();
		sql.append("Select finrepay.finevent,finrecdt.ValueDate,finrecdt.Amount,finreceipt.reference");
		sql.append(", finrepay.PriAmount, finrepay.PftAmount");
		sql.append(" from finRepayHeader finrepay ");
		sql.append("join finreceiptdetail finrecdt on finrepay.receiptseqid=finrecdt.receiptseqid ");
		sql.append("join finreceiptheader finreceipt on finrecdt.receiptId=finreceipt.receiptId ");
		sql.append(" Where finrepay.FinReference = ? and finreceipt.ReceiptModeStatus in ('R','A','F')");

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
					finRepayHeader.setFinEvent(rs.getString("FinEvent"));
					finRepayHeader.setFinReference(rs.getString("Reference"));
					finRepayHeader.setRepayAmount(rs.getBigDecimal("Amount"));
					finRepayHeader.setValueDate(rs.getDate("ValueDate"));
					finRepayHeader.setPftAmount(rs.getBigDecimal("PftAmount"));
					finRepayHeader.setPriAmount(rs.getBigDecimal("PriAmount"));
					return finRepayHeader;
				}
			});
		} catch (Exception e) {
			logger.debug(Literal.EXCEPTION);
		}
		return finRepayHeaders;
	}

	@Override
	public List<CashFlow> getFinDisbDetails(String reference, String type) {
		List<CashFlow> cashFlows = null;
		StringBuilder sql = new StringBuilder();
		sql.append("Select DisbDate, FinReference, DisbAmount, 'DisbMade' as Type");
		sql.append(" From FinDisbursementDetails");
		sql.append(type);
		sql.append(" Where FinReference = ?");

		try {
			cashFlows = this.jdbcOperations.query(sql.toString(), new PreparedStatementSetter() {
				@Override
				public void setValues(PreparedStatement ps) throws SQLException {
					if (reference != null) {
						ps.setString(1, reference);
					}
				}
			}, new RowMapper<CashFlow>() {
				@Override
				public CashFlow mapRow(ResultSet rs, int rowNum) throws SQLException {
					CashFlow flow = new CashFlow();
					flow.setDate(rs.getDate("DisbDate"));
					flow.setLan(rs.getString("FinReference"));
					flow.setType(rs.getString("Type"));
					flow.setDisb(rs.getBigDecimal("DisbAmount"));
					return flow;
				}
			});
		} catch (Exception e) {
			logger.debug(Literal.EXCEPTION);
		}
		return cashFlows;
	}

	@Override
	public List<com.pennant.backend.model.finance.FinanceScheduleDetail> getFinScheduleDetails(String reference,
			String type) {
		List<com.pennant.backend.model.finance.FinanceScheduleDetail> fsds = null;
		StringBuilder sql = new StringBuilder("select");
		sql.append(" FinReference, SchDate, Specifier, PartialPaidAmt, ProfitSchd, SchdPftPaid, SchPftPaid");
		sql.append(", PrincipalSchd, SchdPriPaid, SchPriPaid");
		sql.append(" From FinScheduleDetails");
		sql.append(" Where FinReference = ? order by SchDate asc");

		try {
			fsds = this.jdbcOperations.query(sql.toString(), new PreparedStatementSetter() {
				@Override
				public void setValues(PreparedStatement ps) throws SQLException {
					if (reference != null) {
						ps.setString(1, reference);
					}
				}
			}, new RowMapper<com.pennant.backend.model.finance.FinanceScheduleDetail>() {
				@Override
				public com.pennant.backend.model.finance.FinanceScheduleDetail mapRow(ResultSet rs, int rowNum)
						throws SQLException {
					com.pennant.backend.model.finance.FinanceScheduleDetail fsd = new com.pennant.backend.model.finance.FinanceScheduleDetail();
					fsd.setFinReference(rs.getString("FinReference"));
					fsd.setSchDate(rs.getTimestamp("SchDate"));
					fsd.setSchdPriPaid(rs.getBigDecimal("SchdPriPaid"));
					fsd.setSchdPftPaid(rs.getBigDecimal("SchdPftPaid"));
					fsd.setSchPftPaid(rs.getBoolean("SchPftPaid"));
					fsd.setSchPriPaid(rs.getBoolean("SchPriPaid"));
					fsd.setSpecifier(rs.getString("Specifier"));
					fsd.setProfitSchd(rs.getBigDecimal("ProfitSchd"));
					fsd.setPrincipalSchd(rs.getBigDecimal("PrincipalSchd"));
					fsd.setPartialPaidAmt(rs.getBigDecimal("PartialPaidAmt"));
					return fsd;
				}
			});
		} catch (Exception e) {
			logger.debug(Literal.EXCEPTION);
		}
		return fsds;
	}

	@Override
	public List<FinODDetails> getFinODDetailsByFinRef(String finReference, String type) {
		List<FinODDetails> fods = null;
		StringBuilder sql = new StringBuilder();
		sql.append("Select FinODSchdDate, FinReference ");
		sql.append(" from FinODDetails");
		sql.append(type);
		sql.append(" Where FinReference = ?");

		try {
			fods = this.jdbcOperations.query(sql.toString(), new PreparedStatementSetter() {
				@Override
				public void setValues(PreparedStatement ps) throws SQLException {
					if (finReference != null) {
						ps.setString(1, finReference);
					}
				}
			}, new RowMapper<FinODDetails>() {
				@Override
				public FinODDetails mapRow(ResultSet rs, int rowNum) throws SQLException {
					FinODDetails fod = new FinODDetails();
					fod.setFinODSchdDate(rs.getDate("FinODSchdDate"));
					fod.setFinReference(rs.getString("FinReference"));
					return fod;
				}
			});
		} catch (Exception e) {
			logger.debug(Literal.EXCEPTION);
		}
		return fods;
	}
}
