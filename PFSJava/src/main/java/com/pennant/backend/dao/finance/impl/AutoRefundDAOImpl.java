package com.pennant.backend.dao.finance.impl;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;

import com.pennant.backend.dao.finance.AutoRefundDAO;
import com.pennant.backend.model.finance.AutoRefundLoan;
import com.pennanttech.pennapps.core.ConcurrencyException;
import com.pennanttech.pennapps.core.jdbc.BasicDao;
import com.pennanttech.pennapps.core.jdbc.JdbcUtil;
import com.pennanttech.pennapps.core.resource.Literal;

public class AutoRefundDAOImpl extends BasicDao<AutoRefundLoan> implements AutoRefundDAO {
	private static Logger logger = LogManager.getLogger(AutoRefundDAOImpl.class);

	@Override
	public void saveRefundlist(List<AutoRefundLoan> finalRefundList) {

		logger.debug(Literal.ENTERING);
		int[] recordCount = null;
		// Prepare the SQL
		StringBuilder sql = new StringBuilder(" insert into AutoRefundLoans");
		sql.append("( FINID,  REFUNDAMT , APPDATE,  EXECUTIONTIME, STATUS, ERRORCODE ");
		sql.append(") values(");
		sql.append("?, ?, ?, ?, ?, ? ");
		sql.append(")");

		recordCount = jdbcTemplate.getJdbcOperations().batchUpdate(sql.toString(), new BatchPreparedStatementSetter() {

			@Override
			public void setValues(PreparedStatement ps, int i) throws SQLException {
				AutoRefundLoan arl = finalRefundList.get(i);
				int index = 1;

				ps.setLong(index++, arl.getFinID());
				ps.setBigDecimal(index++, arl.getRefundAmt());
				ps.setDate(index++, JdbcUtil.getDate(arl.getAppDate()));
				ps.setTimestamp(index++, JdbcUtil.getTimestamp(arl.getExecutionTime()));
				ps.setString(index++, arl.getStatus());
				ps.setString(index++, arl.getErrorCode());
			}

			@Override
			public int getBatchSize() {
				return finalRefundList.size();
			}
		});
		if (recordCount == null || recordCount.length <= 0) {
			throw new ConcurrencyException();
		}

		logger.debug(Literal.LEAVING);
	}

}
