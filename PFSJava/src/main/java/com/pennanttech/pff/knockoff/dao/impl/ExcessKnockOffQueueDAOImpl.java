package com.pennanttech.pff.knockoff.dao.impl;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

import org.springframework.dao.DataAccessException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;

import com.pennant.backend.model.finance.CustEODEvent;
import com.pennant.eod.constants.EodConstants;
import com.pennant.pff.batch.job.dao.BatchJobQueueDAO;
import com.pennant.pff.batch.job.model.BatchJobQueue;
import com.pennant.pff.extension.CustomerExtension;
import com.pennanttech.pennapps.core.App;
import com.pennanttech.pennapps.core.jdbc.JdbcUtil;
import com.pennanttech.pennapps.core.jdbc.SequenceDao;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pennapps.core.resource.Message;
import com.pennanttech.pennapps.core.util.DateUtil;

public class ExcessKnockOffQueueDAOImpl extends SequenceDao<CustEODEvent> implements BatchJobQueueDAO {
	private static final String SEQUENCE_NAME = "SEQ_CROSS_LOAN_KNOCKOFF_QUEUE";

	@Override
	public int prepareQueue(BatchJobQueue jobQueue) {
		String sql = "Insert Into Cross_Loan_KnockOff_Queue(ID, CustID) Select row_number() over(order by CustID) ID, CustID From (Select distinct CustID From Cross_Loan_KnockOff_Stage) T";

		if (CustomerExtension.CUST_CORE_BANK_ID) {
			sql = "Insert Into Cross_Loan_KnockOff_Queue(ID, CoreBankId) Select row_number() over(order by CoreBankId) ID, CoreBankId From (Select distinct CoreBankId From Cross_Loan_KnockOff_Stage) T";
		}

		logger.debug(Literal.SQL.concat(sql));

		return this.jdbcOperations.update(sql);

	}

	@Override
	public void handleFailures(BatchJobQueue jobQueue) {
		String sql = "Delete From Cross_Loan_KnockOff_Queue Where Progress = ? ";

		logger.debug(Literal.SQL.concat(sql));

		this.jdbcOperations.update(sql, ps -> {
			ps.setInt(1, EodConstants.PROGRESS_SUCCESS);

		});

		sql = "Update Cross_Loan_KnockOff_Queue Set Progress = ? Where Progress = ?";

		logger.debug(Literal.SQL.concat(sql));

		int count = this.jdbcOperations.update(sql, ps -> {
			ps.setInt(1, EodConstants.PROGRESS_WAIT);
			ps.setInt(2, EodConstants.PROGRESS_FAILED);
		});

		if (count > 0) {
			updateQueingRecords(getQueingRecords());
		}

	}

	@Override
	public int getCount() {
		return 0;
	}

	@Override
	public int getQueueCount() {
		String sql = "Select Coalesce(count(Id), 0) From Cross_Loan_KnockOff_Queue";

		logger.debug(Literal.SQL.concat(sql));

		return this.jdbcOperations.queryForObject(sql, Integer.class);
	}

	@Override
	public int getQueueCount(BatchJobQueue jobQueue) {
		String sql = "Select Coalesce(count(Id), 0) From Cross_Loan_KnockOff_Queue";

		logger.debug(Literal.SQL.concat(sql));

		return this.jdbcOperations.queryForObject(sql, Integer.class);

	}

	@Override
	public void updateProgress(BatchJobQueue jobQueue) {
		int process = jobQueue.getProgress();
		long queueId = jobQueue.getId();

		String sql = null;
		if (process == EodConstants.PROGRESS_IN_PROCESS) {
			sql = "Update Cross_Loan_KnockOff_Queue Set Progress = ?, StartTime = ?, ThreadId = ? Where Id = ?";

			logger.debug(Literal.SQL.concat(sql));

			this.jdbcOperations.update(sql, ps -> {
				ps.setInt(1, process);
				ps.setDate(2, JdbcUtil.getDate(DateUtil.getSysDate()));
				ps.setLong(3, jobQueue.getThreadId());
				ps.setLong(4, queueId);
			});
		} else if (process == EodConstants.PROGRESS_SUCCESS) {
			sql = "Update Cross_Loan_KnockOff_Queue Set EndTime = ?, Progress = ? Where Id = ?";

			logger.debug(Literal.SQL.concat(sql));

			this.jdbcOperations.update(sql, ps -> {
				ps.setDate(1, JdbcUtil.getDate(DateUtil.getSysDate()));
				ps.setInt(2, EodConstants.PROGRESS_SUCCESS);
				ps.setLong(3, queueId);
			});
		} else if (process == EodConstants.PROGRESS_FAILED) {
			sql = "Update Cross_Loan_KnockOff_Queue Set EndTime = ?, ThreadId = ?, Progress = ?, ErrorLog = ? Where Id = ?";

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
	public void clearQueue() {
		String sql = "Truncate table Cross_Loan_KnockOff_Queue";

		logger.debug(Literal.SQL.concat(sql));

		jdbcOperations.update(sql);
	}

	@Override
	public long getNextValue() {
		return getNextValue(SEQUENCE_NAME);
	}

	@Override
	public void resetSequence() {
		switch (App.DATABASE) {
		case ORACLE, MY_SQL:
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
		String sql = "Select CustId From Cross_Loan_KnockOff_Queue Where Id = ?";

		logger.debug(Literal.SQL.concat(sql));

		try {
			return jdbcOperations.queryForObject(sql, Long.class, sequence);
		} catch (EmptyResultDataAccessException e) {
			logger.warn(Message.NO_RECORD_FOUND);
			return null;
		}
	}

	@Override
	public String getRefBySequence(long sequence) {
		String sql = "Select CoreBankID From Cross_Loan_KnockOff_Queue Where Id = ?";

		logger.debug(Literal.SQL.concat(sql));

		try {
			return jdbcOperations.queryForObject(sql, String.class, sequence);
		} catch (EmptyResultDataAccessException e) {
			logger.warn(Message.NO_RECORD_FOUND);
			return null;
		}
	}

	private void updateQueingRecords(List<BatchJobQueue> jobQueue) {
		String sql = "Update Cross_Loan_KnockOff_Queue Set Id = ? Where ID = ?";

		logger.debug(Literal.SQL + sql);

		try {
			jdbcOperations.batchUpdate(sql, new BatchPreparedStatementSetter() {

				@Override
				public void setValues(PreparedStatement ps, int i) throws SQLException {
					BatchJobQueue jobQueuedetails = jobQueue.get(i);

					ps.setLong(1, jobQueuedetails.getResetCounterId());
					ps.setLong(2, jobQueuedetails.getId());
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

	private List<BatchJobQueue> getQueingRecords() {
		String sql = "Select row_number() over(order by id) ResetCounterId, ID from Cross_Loan_KnockOff_Queue";

		logger.debug(Literal.SQL + sql);

		return this.jdbcOperations.query(sql, (rs, rowNum) -> {
			BatchJobQueue jobQueue = new BatchJobQueue();
			jobQueue.setId(rs.getLong("ID"));
			jobQueue.setResetCounterId(rs.getLong("ResetCounterId"));
			return jobQueue;
		});
	}

}
