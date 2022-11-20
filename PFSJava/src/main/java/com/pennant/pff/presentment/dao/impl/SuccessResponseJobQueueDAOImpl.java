package com.pennant.pff.presentment.dao.impl;

import javax.sql.DataSource;

import org.springframework.dao.EmptyResultDataAccessException;

import com.pennant.eod.constants.EodConstants;
import com.pennant.pff.batch.job.dao.BatchJobQueueDAO;
import com.pennant.pff.batch.job.model.BatchJobQueue;
import com.pennanttech.pennapps.core.App;
import com.pennanttech.pennapps.core.jdbc.JdbcUtil;
import com.pennanttech.pennapps.core.jdbc.SequenceDao;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pennapps.core.resource.Message;
import com.pennanttech.pennapps.core.util.DateUtil;

public class SuccessResponseJobQueueDAOImpl extends SequenceDao<BatchJobQueue> implements BatchJobQueueDAO {
	private static final String SEQUENCE_NAME = "SEQ_PRESENTMENT_EXTX_QUEUE";

	public SuccessResponseJobQueueDAOImpl(DataSource dataSource) {
		super.setDataSource(dataSource);
	}

	@Override
	public void logQueue(BatchJobQueue jobQueue) {
		//
	}

	@Override
	public void deleteQueue(BatchJobQueue jobQueue) {
		String sql = "Delete from Presentment_Approve_Queue Where BatchID = ?";

		logger.debug(Literal.SQL.concat(sql));

		jdbcOperations.update(sql, jobQueue.getBatchId());
	}

	@Override
	public int prepareQueue(BatchJobQueue jobQueue) {
		StringBuilder sql = new StringBuilder("Insert into Presentment_Approve_Queue (Id, ReferenceId, BatchID)");
		sql.append(" Select row_number() over(order by pd.ID) ID, pd.ID as ReferenceId, ph.BatchID");
		sql.append(" From PresentmentHeader ph");
		sql.append(" Inner Join PresentmentDetails pd on pd.PresentmentID = ph.Id");
		sql.append(" Where BatchID = ?");

		logger.debug(Literal.SQL.concat(sql.toString()));

		return this.jdbcOperations.update(sql.toString(), jobQueue.getBatchId());
	}

	@Override
	public int handleFailures(BatchJobQueue jobQueue) {
		jdbcOperations.update("Delete from Presentment_Approve_Queue_Log Where BatchID = ?", jobQueue.getBatchId());

		StringBuilder sql = new StringBuilder("Insert Into Presentment_Approve_Queue_Log(Id, ReferenceId)");
		sql.append(" Select row_number() over(order by ID) ID, ID");
		sql.append(" From Presentment_Approve_Queue Where Progress = ? and BatchID = ?");

		logger.debug(Literal.SQL.concat(sql.toString()));

		this.jdbcOperations.update(sql.toString(), EodConstants.PROGRESS_WAIT, jobQueue.getBatchId());

		deleteQueue(jobQueue);

		sql = new StringBuilder("Insert Into Presentment_Approve_Queue (Id, ReferenceId, BatchID)");
		sql.append(" Select ID, ReferenceId, BatchID From Presentment_Approve_Queue_Log");

		logger.debug(Literal.SQL.concat(sql.toString()));

		return this.jdbcOperations.update(sql.toString());
	}

	@Override
	public int getQueueCount(BatchJobQueue jobQueue) {
		String sql = "Select Coalesce(count(Id), 0) From Presentment_Approve_Queue where BatchID = ? and Progress = ?";

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
			sql = "Update Presentment_Approve_Queue Set Progress = ?, StartTime = ?, ThreadId = ? Where Id = ?";

			logger.debug(Literal.SQL.concat(sql));

			this.jdbcOperations.update(sql, ps -> {
				ps.setInt(1, process);
				ps.setDate(2, JdbcUtil.getDate(DateUtil.getSysDate()));
				ps.setLong(3, jobQueue.getThreadId());
				ps.setLong(4, queueId);
			});
		} else if (process == EodConstants.PROGRESS_SUCCESS) {
			sql = "Update Presentment_Approve_Queue Set EndTime = ?, Progress = ? Where Id = ?";

			logger.debug(Literal.SQL.concat(sql));

			this.jdbcOperations.update(sql, ps -> {
				ps.setDate(1, JdbcUtil.getDate(DateUtil.getSysDate()));
				ps.setInt(2, EodConstants.PROGRESS_SUCCESS);
				ps.setLong(3, queueId);
			});
		} else if (process == EodConstants.PROGRESS_FAILED) {
			sql = "Update Presentment_Approve_Queue Set EndTime = ?, ThreadId = ?, Progress = ? Where Id = ?";

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
	public Long getJobId(String jobName) {
		StringBuilder sql = new StringBuilder("Select");
		sql.append(" COALESCE(MAX(JOB_EXECUTION_ID), 0) JobId");
		sql.append(" From BATCH_JOB_EXECUTION je");
		sql.append(" Inner Join BATCH_JOB_INSTANCE ji on ji.Job_Instance_Id = je.Job_Instance_Id");
		sql.append(" Where ji.Job_Name = ?");

		logger.debug(Literal.SQL.concat(sql.toString()));

		try {
			return this.jdbcOperations.queryForObject(sql.toString(), Long.class, jobName);
		} catch (EmptyResultDataAccessException dae) {
			logger.warn(Message.NO_RECORD_FOUND);
			return null;
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
		String sql = "Select ReferenceId From Presentment_Approve_Queue Where Id = ? and Progress = ?";

		logger.debug(Literal.SQL.concat(sql));

		try {
			return jdbcOperations.queryForObject(sql, Long.class, sequence, EodConstants.PROGRESS_WAIT);
		} catch (EmptyResultDataAccessException e) {
			logger.warn(Message.NO_RECORD_FOUND);
			return null;
		}
	}
}
