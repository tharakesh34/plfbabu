package com.pennant.backend.dao.finance.impl;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

import org.springframework.jdbc.core.BatchPreparedStatementSetter;

import com.pennant.backend.dao.finance.AutoRefundDAO;
import com.pennant.backend.model.finance.AutoRefundLoan;
import com.pennanttech.pennapps.core.ConcurrencyException;
import com.pennanttech.pennapps.core.jdbc.BasicDao;
import com.pennanttech.pennapps.core.jdbc.JdbcUtil;
import com.pennanttech.pennapps.core.resource.Literal;

public class AutoRefundDAOImpl extends BasicDao<AutoRefundLoan> implements AutoRefundDAO {

	@Override
	public void save(List<AutoRefundLoan> finalRefundList) {
		StringBuilder sql = new StringBuilder("Insert into Auto_Refund_Loans");
		sql.append("(FinID, RefundAmt, AppDate, ExecutionTime, Status, ErrorCode)");
		sql.append(" Values (?, ?, ?, ?, ?, ?)");

		logger.debug(Literal.SQL.concat(sql.toString()));

		int[] recordCount = jdbcOperations.batchUpdate(sql.toString(), new BatchPreparedStatementSetter() {
			@Override
			public void setValues(PreparedStatement ps, int i) throws SQLException {
				AutoRefundLoan arl = finalRefundList.get(i);

				int index = 0;

				ps.setLong(++index, arl.getFinID());
				ps.setBigDecimal(++index, arl.getRefundAmt());
				ps.setDate(++index, JdbcUtil.getDate(arl.getAppDate()));
				ps.setTimestamp(++index, JdbcUtil.getTimestamp(arl.getExecutionTime()));
				ps.setString(++index, arl.getStatus());
				ps.setString(++index, arl.getErrorCode());
			}

			@Override
			public int getBatchSize() {
				return finalRefundList.size();
			}
		});

		if (recordCount.length <= 0) {
			throw new ConcurrencyException();
		}
	}
}
