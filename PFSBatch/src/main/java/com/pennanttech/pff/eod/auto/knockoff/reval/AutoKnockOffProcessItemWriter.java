package com.pennanttech.pff.eod.auto.knockoff.reval;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.batch.item.ItemWriter;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.PreparedStatementSetter;

import com.pennant.backend.model.finance.AutoKnockOffExcess;
import com.pennanttech.pennapps.core.jdbc.BasicDao;
import com.pennanttech.pennapps.core.resource.Literal;

import AutoKnockOffExcess.AutoKnockOffExcessDetails;

public class AutoKnockOffProcessItemWriter extends BasicDao<AutoKnockOffExcess>
		implements ItemWriter<AutoKnockOffExcess> {
	private Logger logger = LogManager.getLogger(AutoKnockOffProcessItemWriter.class);

	@Override
	public void write(List<? extends AutoKnockOffExcess> autoKnockOffs) throws Exception {
		List<String> knockOffExcess = new ArrayList<>();
		String key = null;

		for (AutoKnockOffExcess autoKnockOffExcess : autoKnockOffs) {
			key = autoKnockOffExcess.getFinReference();

			if (knockOffExcess.contains(key)) {
				continue;
			} else {
				knockOffExcess.add(key);
				updateExcessData(autoKnockOffExcess);
				updateExcessDetails(autoKnockOffExcess.getExcessDetails());
			}
		}

	}

	private void updateExcessData(AutoKnockOffExcess koProcess) {
		logger.debug(Literal.ENTERING);

		StringBuilder sql = new StringBuilder("Update");
		sql.append(" AUTO_KNOCKOFF_EXCESS");
		sql.append(" set ProcessingFlag = ?, UtilizedAmount = ?");
		sql.append(" Where ID = ?");

		logger.trace(Literal.SQL + sql.toString());

		try {
			jdbcTemplate.getJdbcOperations().update(sql.toString(), new PreparedStatementSetter() {

				@Override
				public void setValues(PreparedStatement ps) throws SQLException {
					int index = 1;

					ps.setInt(index++, 1);
					ps.setBigDecimal(index++, koProcess.getTotalUtilizedAmnt());

					ps.setLong(index, koProcess.getID());
				}
			});
		} catch (DataAccessException e) {
			logger.warn(Literal.EXCEPTION, e);
		}

		logger.debug(Literal.LEAVING);

	}

	public void updateExcessDetails(List<? extends AutoKnockOffExcessDetails> processList) {
		logger.debug(Literal.ENTERING);

		StringBuilder sql = new StringBuilder("Update");
		sql.append(" AUTO_KNOCKOFF_EXCESS_DETAILS");
		sql.append(" Set ReceiptId = ?, Reason = ?, Status = ?, UtilizedAmnt = ?");
		sql.append(" Where ID = ?");

		logger.trace(Literal.SQL + sql.toString());

		try {
			jdbcTemplate.getJdbcOperations().batchUpdate(sql.toString(), new BatchPreparedStatementSetter() {

				@Override
				public void setValues(PreparedStatement ps, int i) throws SQLException {
					AutoKnockOffExcessDetails excessDetails = processList.get(i);

					int index = 1;
					ps.setLong(index++, excessDetails.getReceiptID());
					ps.setString(index++, excessDetails.getReason());
					ps.setString(index++, excessDetails.getStatus());
					ps.setBigDecimal(index++, excessDetails.getUtilizedAmnt());
					ps.setLong(index, excessDetails.getID());

				}

				@Override
				public int getBatchSize() {

					return processList.size();
				}
			});
		} catch (DataAccessException e) {
			logger.warn(Literal.EXCEPTION, e);
		}

		logger.debug(Literal.LEAVING);
	}

}
