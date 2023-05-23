package com.pennant.pff.letter.dao.impl;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

import javax.sql.DataSource;

import org.springframework.dao.DataAccessException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;

import com.pennant.eod.constants.EodConstants;
import com.pennant.pff.batch.job.dao.BatchJobQueueDAO;
import com.pennant.pff.batch.job.model.BatchJobQueue;
import com.pennanttech.pennapps.core.App;
import com.pennanttech.pennapps.core.jdbc.JdbcUtil;
import com.pennanttech.pennapps.core.jdbc.SequenceDao;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pennapps.core.resource.Message;
import com.pennanttech.pennapps.core.util.DateUtil;

public class LetterGenerationJobQueueDAOImpl extends SequenceDao<BatchJobQueue> implements BatchJobQueueDAO {
	private static final String SEQUENCE_NAME = "SEQ_LETTER_GENERATION_QUEUE";

	public LetterGenerationJobQueueDAOImpl(DataSource dataSource) {
		super.setDataSource(dataSource);
	}

	@Override
	public void clearQueue() {
		String sql = "Truncate table LETTER_GENERATION_QUEUE";

		logger.debug(Literal.SQL.concat(sql));

		jdbcOperations.update(sql);
	}

	@Override
	public int prepareQueue(BatchJobQueue jobQueue) {
		StringBuilder sql = new StringBuilder("Insert into LETTER_GENERATION_QUEUE (Id, LetterID)");
		sql.append(" Select row_number() over(order by ID) ID, ID as LetterID");
		sql.append(" From Loan_Letters_Stage Where Generated = ? and RequestType = ?");

		logger.debug(Literal.SQL.concat(sql.toString()));

		return this.jdbcOperations.update(sql.toString(), ps -> {
			ps.setInt(1, 0);
			ps.setString(2, "A");
		});
	}

	@Override
	public void handleFailures(BatchJobQueue jobQueue) {
		String sql = "Delete From LETTER_GENERATION_QUEUE Where Progress = ?";

		logger.debug(Literal.SQL.concat(sql));

		this.jdbcOperations.update(sql, ps -> {
			ps.setInt(1, EodConstants.PROGRESS_SUCCESS);
		});

		sql = "Update LETTER_GENERATION_QUEUE Set Progress = ? Where Progress = ?";

		logger.debug(Literal.SQL.concat(sql));

		int count = this.jdbcOperations.update(sql, ps -> {
			ps.setInt(1, EodConstants.PROGRESS_WAIT);
			ps.setInt(2, EodConstants.PROGRESS_FAILED);
		});

		if (count > 0) {
			updateQueingRecords(getQueingRecords(jobQueue));
		}
	}

	@Override
	public int getQueueCount() {
		String sql = "Select Coalesce(count(Id), 0) From LETTER_GENERATION_QUEUE";

		logger.debug(Literal.SQL.concat(sql));

		return this.jdbcOperations.queryForObject(sql, Integer.class);
	}

	@Override
	public int getQueueCount(BatchJobQueue jobQueue) {
		String sql = "Select Coalesce(count(Id), 0) From LETTER_GENERATION_QUEUE Where Progress = ?";

		logger.debug(Literal.SQL.concat(sql));

		return this.jdbcOperations.queryForObject(sql, Integer.class, EodConstants.PROGRESS_WAIT);
	}

	@Override
	public void updateProgress(BatchJobQueue jobQueue) {
		int process = jobQueue.getProgress();
		long queueId = jobQueue.getId();

		String sql = null;
		if (process == EodConstants.PROGRESS_IN_PROCESS) {
			sql = "Update LETTER_GENERATION_QUEUE Set Progress = ?, StartTime = ?, ThreadId = ? Where Id = ?";

			logger.debug(Literal.SQL.concat(sql));

			this.jdbcOperations.update(sql, ps -> {
				ps.setInt(1, process);
				ps.setDate(2, JdbcUtil.getDate(DateUtil.getSysDate()));
				ps.setLong(3, jobQueue.getThreadId());
				ps.setLong(4, queueId);
			});
		} else if (process == EodConstants.PROGRESS_SUCCESS) {
			sql = "Update LETTER_GENERATION_QUEUE Set EndTime = ?, Progress = ? Where Id = ?";

			logger.debug(Literal.SQL.concat(sql));

			this.jdbcOperations.update(sql, ps -> {
				ps.setDate(1, JdbcUtil.getDate(DateUtil.getSysDate()));
				ps.setInt(2, EodConstants.PROGRESS_SUCCESS);
				ps.setLong(3, queueId);
			});
		} else if (process == EodConstants.PROGRESS_FAILED) {
			sql = "Update LETTER_GENERATION_QUEUE Set EndTime = ?, ThreadId = ?, Progress = ?, ErrorLog = ? Where Id = ?";

			logger.debug(Literal.SQL.concat(sql));

			this.jdbcOperations.update(sql, ps -> {
				ps.setDate(1, JdbcUtil.getDate(DateUtil.getSysDate()));
				ps.setInt(2, 0);
				ps.setInt(3, EodConstants.PROGRESS_FAILED);
				ps.setString(4, jobQueue.getError());
				ps.setLong(5, queueId);
			});
		}
	}

	@Override
	public long getNextValue() {
		return getNextValue(SEQUENCE_NAME);
	}

	@Override
	public void resetSequence() {
		switch (App.DATABASE) {
		case ORACLE:
		case MY_SQL:
			jdbcOperations.execute("ALTER SEQUENCE " + SEQUENCE_NAME + " RESTART START WITH " + 1);
			break;
		case POSTGRES:
			jdbcOperations.execute("ALTER SEQUENCE " + SEQUENCE_NAME + " RESTART WITH " + 1);
			break;
		default:
			break;
		}
	}

	@Override
	public Long getIdBySequence(long sequence) {
		String sql = "Select LetterID From LETTER_GENERATION_QUEUE Where Id = ?";

		logger.debug(Literal.SQL.concat(sql));

		try {
			return jdbcOperations.queryForObject(sql, Long.class, sequence);
		} catch (EmptyResultDataAccessException e) {
			logger.warn(Message.NO_RECORD_FOUND);
			return null;
		}
	}

	private List<BatchJobQueue> getQueingRecords(BatchJobQueue bJobQueue) {
		String sql = "Select row_number() over(order by id) ResetCounterId, ID from LETTER_GENERATION_QUEUE";

		logger.debug(Literal.SQL + sql);

		return this.jdbcOperations.query(sql.toString(), (rs, Num) -> {
			BatchJobQueue jobQueue = new BatchJobQueue();
			jobQueue.setId(rs.getLong("ID"));
			jobQueue.setResetCounterId(rs.getLong("ResetCounterId"));
			return jobQueue;
		});
	}

	private void updateQueingRecords(List<BatchJobQueue> jobQueue) {
		String sql = "Update LETTER_GENERATION_QUEUE Set Id = ? Where ID = ?";

		logger.debug(Literal.SQL + sql);

		try {
			jdbcOperations.batchUpdate(sql, new BatchPreparedStatementSetter() {

				@Override
				public void setValues(PreparedStatement ps, int i) throws SQLException {
					BatchJobQueue jobQueuedetails = jobQueue.get(i);
					int index = 1;

					ps.setLong(index++, jobQueuedetails.getResetCounterId());
					ps.setLong(index, jobQueuedetails.getId());
				}

				@Override
				public int getBatchSize() {
					return jobQueue.size();
				}
			});
		} catch (DataAccessException e) {
			logger.warn(Literal.EXCEPTION, e);
		}
	}

}
