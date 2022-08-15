package com.pennant.backend.dao.batchProcessStatus.impl;

import java.util.Date;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.dao.EmptyResultDataAccessException;

import com.pennant.backend.dao.batchProcessStatus.BatchProcessStatusDAO;
import com.pennanttech.pennapps.core.ConcurrencyException;
import com.pennanttech.pennapps.core.jdbc.JdbcUtil;
import com.pennanttech.pennapps.core.jdbc.SequenceDao;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pennapps.core.resource.Message;

public class BatchProcessStatusDAOImpl extends SequenceDao<Object> implements BatchProcessStatusDAO {
	private static Logger logger = LogManager.getLogger(BatchProcessStatusDAOImpl.class);

	@Override
	public String getBatchStatus(String batchName) {
		String sql = "Select Status from Batch_Process_Status Where Name = ?";

		logger.debug(Literal.SQL + sql);

		try {
			return this.jdbcOperations.queryForObject(sql, String.class, batchName);
		} catch (EmptyResultDataAccessException e) {
			logger.warn(Message.NO_RECORD_FOUND);
			return null;
		}
	}

	@Override
	public void saveBatchStatus(String batchName, Date startTime, String status) {
		String sql = "Insert Into Batch_Process_Status (Name, Status, StartTime) Values(?, ?, ?)";

		logger.debug(Literal.SQL + sql);

		try {
			jdbcOperations.update(sql, ps -> {
				int index = 1;

				ps.setString(index++, batchName);
				ps.setString(index++, status);
				ps.setDate(index++, JdbcUtil.getDate(startTime));
			});
		} catch (DuplicateKeyException e) {
			throw new ConcurrencyException(e);
		}
	}

	@Override
	public void updateBatchStatus(String batchName, Date endTime, String status) {
		String sql = "Update Batch_Process_Status Set Status = ?, EndTime = ? Where Name = ?";

		jdbcOperations.update(sql, ps -> {
			int index = 1;

			ps.setString(index++, status);
			ps.setDate(index++, JdbcUtil.getDate(endTime));
			ps.setString(index++, batchName);
		});

	}
}
