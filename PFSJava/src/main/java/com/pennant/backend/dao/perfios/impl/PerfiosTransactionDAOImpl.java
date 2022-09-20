package com.pennant.backend.dao.perfios.impl;

import java.sql.PreparedStatement;
import java.sql.SQLException;

import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.PreparedStatementSetter;

import com.pennant.backend.dao.perfios.PerfiosTransactionDAO;
import com.pennant.backend.model.perfios.PerfiosTransaction;
import com.pennanttech.pennapps.core.InterfaceException;
import com.pennanttech.pennapps.core.jdbc.BasicDao;
import com.pennanttech.pennapps.core.resource.Literal;

public class PerfiosTransactionDAOImpl extends BasicDao<PerfiosTransaction> implements PerfiosTransactionDAO {
	@Override
	public String updatePerfiosStatus(PerfiosTransaction perfios) {
		logger.debug(Literal.ENTERING);
		StringBuilder sql = new StringBuilder(
				"update PerfiosHeader set ProcessStage = ?, StatusCode = ?, StatusDesc = ? where TransactionId = ?");
		logger.trace(Literal.SQL + sql.toString());
		int result = 0;
		try {
			result = jdbcTemplate.getJdbcOperations().update(sql.toString(), new PreparedStatementSetter() {
				public void setValues(PreparedStatement preparedStatement) throws SQLException {
					int index = 1;
					preparedStatement.setString(index++, "C");
					preparedStatement.setString(index++, perfios.getStatus());
					preparedStatement.setString(index++, perfios.getStatusDesc());
					preparedStatement.setString(index, perfios.getTransationId());
				}
			});
		} catch (DataAccessException e) {
			throw new InterfaceException(Literal.EXCEPTION, e.getMessage(), e);
		}
		logger.debug(Literal.LEAVING);
		return result != 0 ? "Perfios Status Updated Successfully" : "Perfios Status Update Failed";
	}
}
