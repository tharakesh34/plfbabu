package com.pennant.pff.batch.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;

import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;

import com.pennant.eod.constants.EodConstants;
import com.pennant.pff.batch.job.model.BatchJob;
import com.pennanttech.pennapps.core.jdbc.SequenceDao;
import com.pennanttech.pennapps.core.resource.Literal;

public class BatchJobDAOImpl extends SequenceDao<BatchJob> implements BatchJobDAO {

	@Override
	public long createBatch(String batchType, int totalRecords) {
		String sql = "Insert into BATCH_JOBS (Batch_Type, Start_Time, Total_Records) values (?, ?, ?)";

		logger.debug(Literal.SQL.concat(sql));

		KeyHolder keyHolder = new GeneratedKeyHolder();

		jdbcOperations.update(new PreparedStatementCreator() {

			@Override
			public PreparedStatement createPreparedStatement(Connection con) throws SQLException {
				PreparedStatement ps = con.prepareStatement(sql, new String[] { "id" });

				ps.setString(1, batchType);
				ps.setTimestamp(2, new Timestamp(System.currentTimeMillis()));
				ps.setInt(3, totalRecords);

				return ps;
			}
		}, keyHolder);

		Number key = keyHolder.getKey();

		if (key == null) {
			return 0;
		}

		return key.longValue();

	}

	@Override
	public void deleteBatch(long batchID) {
		String sql = "Delete From BATCH_JOBS Where ID = ?";

		logger.debug(Literal.SQL.concat(sql));

		this.jdbcOperations.update(sql, batchID);

	}

	@Override
	public BatchJob getBatch(long id) {

		StringBuilder sql = new StringBuilder();
		sql.append("Select Batch_Type, Total_Records, Process_Records, Success_Records, Failed_Records, Remarks");
		sql.append(" From BATCH_JOBS");
		sql.append(" Where ID = ?");

		logger.debug(Literal.SQL.concat(sql.toString()));

		return this.jdbcOperations.queryForObject(sql.toString(), (rs, rowNum) -> {
			BatchJob bjq = new BatchJob();

			bjq.setBatchType(rs.getString("Batch_Type"));
			bjq.setTotalRecords(rs.getInt("Total_Records"));
			bjq.setProcessRecords(rs.getInt("Process_Records"));
			bjq.setSuccessRecords(rs.getInt("Success_Records"));
			bjq.setFailedRecords(rs.getInt("Failed_Records"));
			bjq.setRemarks(rs.getString("Remarks"));

			return bjq;
		}, id);

	}

	@Override
	public void updateTotalRecords(int count, long batchID) {
		String sql = "Update BATCH_JOBS Set Total_Records = ? Where ID = ?";

		logger.debug(Literal.SQL.concat(sql));

		this.jdbcOperations.update(sql, ps -> {
			ps.setInt(1, count);
			ps.setLong(2, batchID);
		});

	}

	@Override
	public void updateBatch(BatchJob batchJob) {
		int process = batchJob.getProgress();

		String sql = null;

		if (process == EodConstants.PROGRESS_IN_PROCESS) {
			sql = "Update BATCH_JOBS Set Process_Records = ? Where ID = ?";

			this.jdbcOperations.update(sql, ps -> {
				ps.setInt(1, batchJob.getProcessedRecords());
				ps.setLong(2, batchJob.getId());
			});
		}
		if (process == EodConstants.PROGRESS_SUCCESS) {
			sql = "Update BATCH_JOBS Set Success_Records = ? Where ID = ?";

			this.jdbcOperations.update(sql, ps -> {
				ps.setInt(1, batchJob.getSuccessRecords());
				ps.setLong(2, batchJob.getId());
			});
		}

		if (process == EodConstants.PROGRESS_FAILED) {
			sql = "Update BATCH_JOBS Set Failed_Records = ? Where ID = ?";

			this.jdbcOperations.update(sql, ps -> {
				ps.setInt(1, batchJob.getFailedRecords());
				ps.setLong(2, batchJob.getId());
			});
		}

		logger.debug(Literal.SQL.concat(sql));

	}

	@Override
	public void updateEndTimeStatus(BatchJob batchJob) {
		String sql = "Update BATCH_JOBS Set End_Time = ?, Status = ? Where ID = ?";

		logger.debug(Literal.SQL.concat(sql));

		this.jdbcOperations.update(sql, ps -> {
			ps.setTimestamp(1, new Timestamp(System.currentTimeMillis()));
			ps.setString(2, batchJob.getStatus());
			ps.setLong(3, batchJob.getId());
		});
	}

}
