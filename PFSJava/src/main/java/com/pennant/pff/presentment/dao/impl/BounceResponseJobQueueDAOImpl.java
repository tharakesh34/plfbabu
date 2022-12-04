package com.pennant.pff.presentment.dao.impl;

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

public class BounceResponseJobQueueDAOImpl extends SequenceDao<BatchJobQueue> implements BatchJobQueueDAO {
	private static final String SEQUENCE_NAME = "SEQ_PRMNT_RESP_BOUNCE_QUEUE";

	public BounceResponseJobQueueDAOImpl(DataSource dataSource) {
		super.setDataSource(dataSource);
	}

	@Override
	public void clearQueue() {
		String sql = "Truncate table PRMNT_RESP_BOUNCE_QUEUE";

		logger.debug(Literal.SQL.concat(sql));

		jdbcOperations.update(sql);
	}

	@Override
	public int prepareQueue(BatchJobQueue jobQueue) {
		StringBuilder sql = new StringBuilder("Insert into PRMNT_RESP_BOUNCE_QUEUE (Id, ReferenceId, BatchID)");
		sql.append(" Select row_number() over(order by prd.ID) ID, prd.ID as ReferenceId, ?");
		sql.append(" From PRESENTMENT_RESP_HEADER prh");
		sql.append(" Inner Join PRESENTMENT_RESP_DTLS prd on prd.Header_ID = prh.Id");
		sql.append(" and prh.Progress = ? and prh.Event = ?");
		sql.append(" Where prd.PROCESS_FLAG = ? and prd.CLEARING_STATUS = ? ");

		logger.debug(Literal.SQL.concat(sql.toString()));

		return this.jdbcOperations.update(sql.toString(), ps -> {
			ps.setLong(1, jobQueue.getBatchId());
			ps.setInt(2, 1);
			ps.setString(3, "IMPORT");
			ps.setInt(4, 0);
			ps.setString(5, "B");
		});
	}

	@Override
	public void handleFailures(BatchJobQueue jobQueue) {
		String sql = "Delete From PRMNT_RESP_BOUNCE_QUEUE Where Progress = ? and  BatchID = ?";

		logger.debug(Literal.SQL.concat(sql));

		this.jdbcOperations.update(sql, ps -> {
			ps.setInt(1, EodConstants.PROGRESS_SUCCESS);
			ps.setLong(2, jobQueue.getBatchId());
		});

		sql = "Update PRMNT_RESP_BOUNCE_QUEUE Set Progress = ? Where Progress = ? and  BatchID = ?";

		logger.debug(Literal.SQL.concat(sql));

		int count = this.jdbcOperations.update(sql, ps -> {
			ps.setInt(1, EodConstants.PROGRESS_WAIT);
			ps.setInt(2, EodConstants.PROGRESS_FAILED);
			ps.setLong(3, jobQueue.getBatchId());
		});

		if (count > 0) {
			updateQueingRecords(getQueingRecords(jobQueue));
		}
	}

	@Override
	public int getQueueCount() {
		String sql = "Select Coalesce(count(Id), 0) From PRMNT_RESP_BOUNCE_QUEUE";

		logger.debug(Literal.SQL.concat(sql));

		return this.jdbcOperations.queryForObject(sql, Integer.class);
	}

	@Override
	public int getQueueCount(BatchJobQueue jobQueue) {
		String sql = "Select Coalesce(count(Id), 0) From PRMNT_RESP_BOUNCE_QUEUE where BatchID = ? and Progress = ?";

		logger.debug(Literal.SQL.concat(sql));

		return this.jdbcOperations.queryForObject(sql, Integer.class, jobQueue.getBatchId(),
				EodConstants.PROGRESS_WAIT);
	}

	@Override
	public void updateProgress(BatchJobQueue jobQueue) {
		int process = jobQueue.getProgress();
		long queueId = jobQueue.getId();

		String sql = null;
		if (process == EodConstants.PROGRESS_IN_PROCESS) {
			sql = "Update PRMNT_RESP_BOUNCE_QUEUE Set Progress = ?, StartTime = ?, ThreadId = ? Where Id = ?";

			logger.debug(Literal.SQL.concat(sql));

			this.jdbcOperations.update(sql, ps -> {
				ps.setInt(1, process);
				ps.setDate(2, JdbcUtil.getDate(DateUtil.getSysDate()));
				ps.setLong(3, jobQueue.getThreadId());
				ps.setLong(4, queueId);
			});
		} else if (process == EodConstants.PROGRESS_SUCCESS) {
			sql = "Update PRMNT_RESP_BOUNCE_QUEUE Set EndTime = ?, Progress = ? Where Id = ?";

			logger.debug(Literal.SQL.concat(sql));

			this.jdbcOperations.update(sql, ps -> {
				ps.setDate(1, JdbcUtil.getDate(DateUtil.getSysDate()));
				ps.setInt(2, EodConstants.PROGRESS_SUCCESS);
				ps.setLong(3, queueId);
			});
		} else if (process == EodConstants.PROGRESS_FAILED) {
			sql = "Update PRMNT_RESP_BOUNCE_QUEUE Set EndTime = ?, ThreadId = ?, Progress = ? Where Id = ?";

			logger.debug(Literal.SQL.concat(sql));

			this.jdbcOperations.update(sql, ps -> {
				ps.setDate(1, JdbcUtil.getDate(DateUtil.getSysDate()));
				ps.setInt(2, 0);
				ps.setInt(3, EodConstants.PROGRESS_FAILED);
				ps.setLong(4, queueId);
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
		String sql = "Select ReferenceId From PRMNT_RESP_BOUNCE_QUEUE Where Id = ?";

		logger.debug(Literal.SQL.concat(sql));

		try {
			return jdbcOperations.queryForObject(sql, Long.class, sequence);
		} catch (EmptyResultDataAccessException e) {
			logger.warn(Message.NO_RECORD_FOUND);
			return null;
		}
	}

	private void updateQueingRecords(List<BatchJobQueue> jobQueue) {
		String sql = "Update PRMNT_EXTRACTION_QUEUE Set Id = ? Where ID = ? and BatchID = ?";

		logger.debug(Literal.SQL + sql);

		try {
			jdbcOperations.batchUpdate(sql, new BatchPreparedStatementSetter() {

				@Override
				public void setValues(PreparedStatement ps, int i) throws SQLException {
					BatchJobQueue jobQueuedetails = jobQueue.get(i);
					int index = 1;

					ps.setLong(index++, jobQueuedetails.getResetCounterId());
					ps.setLong(index, jobQueuedetails.getId());
					ps.setLong(index, jobQueuedetails.getBatchId());
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

	private List<BatchJobQueue> getQueingRecords(BatchJobQueue bJobQueue) {
		String sql = "Select row_number() over(order by id) resetCounterId, ID from PRMNT_EXTRACTION_QUEUE Where BatchID = ?";

		logger.debug(Literal.SQL + sql);

		return this.jdbcOperations.query(sql.toString(), ps -> {
			ps.setLong(1, bJobQueue.getBatchId());
		}, (rs, Num) -> {
			BatchJobQueue jobQueue = new BatchJobQueue();
			jobQueue.setId(rs.getLong("ID"));
			jobQueue.setResetCounterId(rs.getLong("resetCounterId"));
			return jobQueue;
		});
	}
}
