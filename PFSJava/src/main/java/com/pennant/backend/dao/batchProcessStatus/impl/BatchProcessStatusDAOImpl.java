package com.pennant.backend.dao.batchProcessStatus.impl;

import java.util.Date;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;

import com.pennant.backend.dao.batchProcessStatus.BatchProcessStatusDAO;
import com.pennanttech.pennapps.core.ConcurrencyException;
import com.pennanttech.pennapps.core.jdbc.SequenceDao;
import com.pennanttech.pennapps.core.resource.Literal;

public class BatchProcessStatusDAOImpl extends SequenceDao<Object> implements BatchProcessStatusDAO {

	private static Logger logger = LogManager.getLogger(BatchProcessStatusDAOImpl.class);

	@Override
	public String getBatchStatus(String batchName) {
		logger.debug(Literal.ENTERING);
		MapSqlParameterSource parmSource = new MapSqlParameterSource();

		StringBuilder sql = new StringBuilder();
		sql.append("Select Status from Batch_Process_Status");
		sql.append(" Where BatchName =:BatchName");

		parmSource.addValue("BatchName", batchName);

		try {
			return this.jdbcTemplate.queryForObject(sql.toString(), parmSource, String.class);
		} catch (Exception e) {
			//
		}
		return null;
	}

	@Override
	public void saveBatchStatus(String batchName, Date startTime, String status) {
		MapSqlParameterSource parmSource = new MapSqlParameterSource();

		StringBuilder sql = new StringBuilder("Insert Into Batch_Process_Status");
		sql.append(" (BatchName, Status, StartTime)");
		sql.append(" Values(:BatchName, :Status, :StartTime)");

		parmSource.addValue("BatchName", batchName);
		parmSource.addValue("Status", status);
		parmSource.addValue("StartTime", startTime);

		try {
			jdbcTemplate.update(sql.toString(), parmSource);
		} catch (DuplicateKeyException e) {
			throw new ConcurrencyException(e);
		}
	}

	@Override
	public void updateBatchStatus(String batchName, Date endTime, String status) {
		MapSqlParameterSource parmSource = new MapSqlParameterSource();

		StringBuilder sql = new StringBuilder("Update  Batch_Process_Status");
		sql.append("  set Status = :Status, EndTime = :EndTime ");
		sql.append(" where BatchName = :BatchName ");

		parmSource.addValue("BatchName", batchName);
		parmSource.addValue("Status", status);
		parmSource.addValue("EndTime", endTime);

		jdbcTemplate.update(sql.toString(), parmSource);
	}
}
