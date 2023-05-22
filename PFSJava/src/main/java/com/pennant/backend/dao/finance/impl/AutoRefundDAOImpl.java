package com.pennant.backend.dao.finance.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Date;
import java.util.List;

import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;

import com.pennant.backend.dao.finance.AutoRefundDAO;
import com.pennant.backend.model.finance.AutoRefundLoan;
import com.pennant.backend.model.finance.CustEODEvent;
import com.pennant.pff.payment.model.PaymentDetail;
import com.pennanttech.pennapps.core.jdbc.JdbcUtil;
import com.pennanttech.pennapps.core.jdbc.SequenceDao;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pennapps.core.resource.Message;

public class AutoRefundDAOImpl extends SequenceDao<CustEODEvent> implements AutoRefundDAO {

	@Override
	public AutoRefundLoan getAutoRefund(long finID, Date appDate) {
		StringBuilder sql = new StringBuilder();
		sql.append(" Select fm.FinID, fm.FinReference, ft.MaxAutoRefund, ft.MinAutoRefund");
		sql.append(", fm.FinRepayMethod, fm.FinIsActive, fpd.CurOdDays, fm.FinCcy, fm.WriteOffLoan");
		sql.append(", h.HoldStatus, fm.FinType, e.EntityCode, fm.ClosedDate");
		sql.append(" From Financemain fm");
		sql.append(" Inner Join Customers c on c.CustID = fm.CustID");
		sql.append(" Inner Join RmtFinanceTypes ft on ft.FinType = fm.FinType");
		sql.append(" Inner Join SmtDivisionDetail d On d.DivisionCode = ft.FinDivision");
		sql.append(" Inner Join Entity e on e.EntityCode = d.EntityCode");
		sql.append(" Inner Join FinPftDetails fpd on fpd.FinID = fm.FinID");
		sql.append(" Left Join Fin_Hold_Details h on fm.FinID = h.FinID");
		sql.append(" Where fm.FinID = ? and ft.AllowAutoRefund = ?");

		logger.debug(Literal.SQL.concat(sql.toString()));

		try {
			return this.jdbcOperations.queryForObject(sql.toString(), (rs, rowNum) -> {
				AutoRefundLoan arl = new AutoRefundLoan();

				arl.setFinID(rs.getLong("FinID"));
				arl.setFinReference(rs.getString("FinReference"));
				arl.setMaxRefundAmt(rs.getBigDecimal("MaxAutoRefund"));
				arl.setMinRefundAmt(rs.getBigDecimal("MinAutoRefund"));
				arl.setFinRepayMethod(rs.getString("FinRepayMethod"));
				arl.setFinIsActive(rs.getBoolean("FinIsActive"));
				arl.setDpdDays(rs.getInt("CurOdDays"));
				arl.setFinCcy(rs.getString("FinCcy"));
				arl.setWriteOffLoan(rs.getBoolean("WriteOffLoan"));
				arl.setHoldStatus(rs.getString("HoldStatus"));
				arl.setFinType(rs.getString("FinType"));
				arl.setEntityCode(rs.getString("EntityCode"));
				arl.setClosedDate(rs.getDate("ClosedDate"));

				return arl;
			}, finID, true);
		} catch (EmptyResultDataAccessException e) {
			logger.warn(Message.NO_RECORD_FOUND);
			return null;
		}
	}

	@Override
	public long logRefund(AutoRefundLoan arl) {
		StringBuilder sql = new StringBuilder("Insert into Excess_Auto_Refunds");
		sql.append("(FinID, RefundAmt, BusinessDate, CreatedOn, Status, ErrorCode, ErrorDesc)");
		sql.append(" Values (?, ?, ?, ?, ?, ?, ?)");

		logger.debug(Literal.SQL.concat(sql.toString()));

		KeyHolder keyHolder = new GeneratedKeyHolder();

		this.jdbcOperations.update(new PreparedStatementCreator() {

			@Override
			public PreparedStatement createPreparedStatement(Connection con) throws SQLException {
				PreparedStatement ps = con.prepareStatement(sql.toString(), new String[] { "id" });
				int index = 0;
				ps.setLong(++index, arl.getFinID());
				ps.setBigDecimal(++index, arl.getRefundAmt());
				ps.setDate(++index, JdbcUtil.getDate(arl.getBusinessDate()));
				ps.setTimestamp(++index, JdbcUtil.getTimestamp(arl.getExecutionTime()));
				ps.setString(++index, arl.getStatus());
				ps.setString(++index, arl.getError().getCode());
				ps.setString(++index, arl.getError().getError());

				return ps;
			}
		}, keyHolder);

		return keyHolder.getKey().longValue();

	}

	@Override
	public void logPaymentDetails(List<PaymentDetail> pdList) {
		StringBuilder sql = new StringBuilder("Insert into Excess_Auto_Refunds_Payments");
		sql.append("(HeaderID, RefundAgainstID, RefundType, PaymentAmt, PaymentID)");
		sql.append(" Values (?, ?, ?, ?, ?)");

		logger.debug(Literal.SQL.concat(sql.toString()));

		this.jdbcOperations.batchUpdate(sql.toString(), new BatchPreparedStatementSetter() {
			@Override
			public void setValues(PreparedStatement ps, int i) throws SQLException {
				PaymentDetail pd = pdList.get(i);

				int index = 0;

				ps.setLong(++index, pd.getAutoRefundID());
				ps.setLong(++index, pd.getReferenceId());
				ps.setString(++index, pd.getAmountType());
				ps.setBigDecimal(++index, pd.getAmount());
				ps.setLong(++index, pd.getPaymentId());
			}

			@Override
			public int getBatchSize() {
				return pdList.size();
			}
		});
	}

}
