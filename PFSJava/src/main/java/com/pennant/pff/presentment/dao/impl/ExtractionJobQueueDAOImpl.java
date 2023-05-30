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

public class ExtractionJobQueueDAOImpl extends SequenceDao<BatchJobQueue> implements BatchJobQueueDAO {
	private static final String SEQUENCE_NAME = "SEQ_PRMNT_EXTRACTION_QUEUE";

	public ExtractionJobQueueDAOImpl(DataSource dataSource) {
		super.setDataSource(dataSource);
	}

	@Override
	public void clearQueue() {
		String sql = "Truncate table PRMNT_EXTRACTION_QUEUE";

		logger.debug(Literal.SQL.concat(sql));

		jdbcOperations.update(sql);
	}

	@Override
	public int prepareQueue(BatchJobQueue jobQueue) {
		if ("EXTRACTION".equals(jobQueue.getJobName())) {
			return prepareExtractionQueue(jobQueue);
		} else {
			return prepareApprovalQueue(jobQueue);
		}
	}

	@Override
	public void updateQueue(BatchJobQueue jobQueue) {
		//
	}

	private int prepareExtractionQueue(BatchJobQueue jobQueue) {
		StringBuilder sql = new StringBuilder("Insert into PRMNT_EXTRACTION_QUEUE (Id, ReferenceId, BatchID)");
		sql.append(" Select row_number() over(order by ID) ID, ID as ReferenceId, BatchID From");
		sql.append(" PRMNT_EXTRACTION_STAGE Where BatchID = ?");

		logger.debug(Literal.SQL.concat(sql.toString()));

		return this.jdbcOperations.update(sql.toString(), jobQueue.getBatchId());
	}

	private int prepareApprovalQueue(BatchJobQueue jobQueue) {
		StringBuilder sql = new StringBuilder("Insert into PRMNT_EXTRACTION_QUEUE (Id, ReferenceId, BatchID)");
		sql.append(" Select row_number() over(order by pd.ID) ID, pd.ID as ReferenceId, ph.BatchID");
		sql.append(" From PresentmentHeader ph");
		sql.append(" Inner Join PresentmentDetails pd on pd.PresentmentID = ph.Id");
		sql.append(" Where BatchID = ?");

		logger.debug(Literal.SQL.concat(sql.toString()));

		return this.jdbcOperations.update(sql.toString(), jobQueue.getBatchId());
	}

	@Override
	public void handleFailures(BatchJobQueue jobQueue) {
		String sql = "Delete From PRMNT_EXTRACTION_QUEUE Where Progress = ? and  BatchID = ?";

		logger.debug(Literal.SQL.concat(sql));

		this.jdbcOperations.update(sql, ps -> {
			ps.setInt(1, EodConstants.PROGRESS_SUCCESS);
			ps.setLong(2, jobQueue.getBatchId());
		});

		sql = "Update PRMNT_EXTRACTION_QUEUE Set Progress = ? Where Progress = ? and  BatchID = ?";

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

		return this.jdbcOperations.query(sql.toString(), ps -> ps.setLong(1, bJobQueue.getBatchId()), (rs, Num) -> {
			BatchJobQueue jobQueue = new BatchJobQueue();
			jobQueue.setId(rs.getLong("ID"));
			jobQueue.setResetCounterId(rs.getLong("resetCounterId"));
			return jobQueue;
		});
	}

	@Override
	public int getQueueCount() {
		String sql = "Select Coalesce(count(Id), 0) From PRMNT_EXTRACTION_QUEUE";

		logger.debug(Literal.SQL.concat(sql));

		return this.jdbcOperations.queryForObject(sql, Integer.class);
	}

	@Override
	public int getQueueCount(BatchJobQueue jobQueue) {
		String sql = "Select Coalesce(count(Id), 0) From PRMNT_EXTRACTION_QUEUE where BatchID = ? and Progress = ?";

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
			sql = "Update PRMNT_EXTRACTION_QUEUE Set Progress = ?, StartTime = ?, ThreadId = ? Where Id = ?";

			logger.debug(Literal.SQL.concat(sql));

			this.jdbcOperations.update(sql, ps -> {
				ps.setInt(1, process);
				ps.setDate(2, JdbcUtil.getDate(DateUtil.getSysDate()));
				ps.setLong(3, jobQueue.getThreadId());
				ps.setLong(4, queueId);
			});
		} else if (process == EodConstants.PROGRESS_SUCCESS) {
			sql = "Update PRMNT_EXTRACTION_QUEUE Set EndTime = ?, Progress = ? Where Id = ?";

			logger.debug(Literal.SQL.concat(sql));

			this.jdbcOperations.update(sql, ps -> {
				ps.setDate(1, JdbcUtil.getDate(DateUtil.getSysDate()));
				ps.setInt(2, EodConstants.PROGRESS_SUCCESS);
				ps.setLong(3, queueId);
			});
		} else if (process == EodConstants.PROGRESS_FAILED) {
			sql = "Update PRMNT_EXTRACTION_QUEUE Set EndTime = ?, ThreadId = ?, Progress = ? Where Id = ?";

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
		String sql = "Select ReferenceId From PRMNT_EXTRACTION_QUEUE Where Id = ?";

		logger.debug(Literal.SQL.concat(sql));

		try {
			return jdbcOperations.queryForObject(sql, Long.class, sequence);
		} catch (EmptyResultDataAccessException e) {
			logger.warn(Message.NO_RECORD_FOUND);
			return null;
		}

	}

	@Override
	public int updateThreadID(long from, long to, int i) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void logQueue() {
		// TODO Auto-generated method stub

	}

	@Override
	public void logQueue(int progress) {
		// TODO Auto-generated method stub

	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return 0;
	}
}
