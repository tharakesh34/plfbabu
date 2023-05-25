package com.pennanttech.refund.dao.impl;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

import org.springframework.dao.DataAccessException;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;

import com.pennant.backend.model.finance.CustEODEvent;
import com.pennant.backend.util.RepayConstants;
import com.pennant.eod.constants.EodConstants;
import com.pennant.pff.batch.job.dao.BatchJobQueueDAO;
import com.pennant.pff.batch.job.model.BatchJobQueue;
import com.pennant.pff.fee.AdviseType;
import com.pennanttech.pennapps.core.App;
import com.pennanttech.pennapps.core.ConcurrencyException;
import com.pennanttech.pennapps.core.jdbc.JdbcUtil;
import com.pennanttech.pennapps.core.jdbc.SequenceDao;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pennapps.core.resource.Message;
import com.pennanttech.pennapps.core.util.DateUtil;

public class AutoRefundQueueDAOImpl extends SequenceDao<CustEODEvent> implements BatchJobQueueDAO {
	private static final String SEQUENCE_NAME = "SEQ_AUTO_REFUND_QUEUE";

	@Override
	public int prepareQueue(BatchJobQueue jobQueue) {
		StringBuilder sql = new StringBuilder("Insert Into Auto_Refund_Queue (ID, FinID)");
		sql.append(" Select row_number() over(order by FinID) ID, FinID From (");
		sql.append(" Select distinct fm.FinID");
		sql.append(" From FinanceMain fm");
		sql.append(" Inner Join (");
		sql.append(" Select FinID From FinExcessAmount");
		sql.append(" Where AmountType = ? and BalanceAmt > ?");
		sql.append(" Union All");
		sql.append(" Select FinID From ManualAdvise");
		sql.append(" Where AdviseType = ? and (AdviseAmount - PaidAmount - WaivedAmount) > ?");
		sql.append(" ) excess on excess.FinID  = fm.FinID) T");

		logger.debug(Literal.SQL.concat(sql.toString()));

		try {
			return this.jdbcOperations.update(sql.toString(), ps -> {
				ps.setString(1, RepayConstants.EXAMOUNTTYPE_EXCESS);
				ps.setInt(2, 0);
				ps.setInt(3, AdviseType.PAYABLE.id());
				ps.setInt(4, 0);

			});
		} catch (DuplicateKeyException e) {
			throw new ConcurrencyException(e);
		}

	}

	@Override
	public void updateQueue(BatchJobQueue jobQueue) {
		// TODO Auto-generated method stub
	}

	@Override
	public void handleFailures(BatchJobQueue jobQueue) {
		String sql = "Delete From Auto_Refund_Queue Where Progress = ? ";

		logger.debug(Literal.SQL.concat(sql));

		this.jdbcOperations.update(sql, ps -> {
			ps.setInt(1, EodConstants.PROGRESS_SUCCESS);

		});

		sql = "Update Auto_Refund_Queue Set Progress = ? Where Progress = ?";

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
	public int getCount() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int getQueueCount() {
		String sql = "Select Coalesce(count(Id), 0) From Auto_Refund_Queue";

		logger.debug(Literal.SQL.concat(sql));

		return this.jdbcOperations.queryForObject(sql, Integer.class);
	}

	@Override
	public int getQueueCount(BatchJobQueue jobQueue) {
		String sql = "Select Coalesce(count(Id), 0) From Auto_Refund_Queue";

		logger.debug(Literal.SQL.concat(sql));

		return this.jdbcOperations.queryForObject(sql, Integer.class);

	}

	@Override
	public int updateThreadID(long from, long to, int i) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void updateProgress(BatchJobQueue jobQueue) {
		int process = jobQueue.getProgress();
		long queueId = jobQueue.getId();

		String sql = null;
		if (process == EodConstants.PROGRESS_IN_PROCESS) {
			sql = "Update Auto_Refund_Queue Set Progress = ?, StartTime = ?, ThreadId = ? Where Id = ?";

			logger.debug(Literal.SQL.concat(sql));

			this.jdbcOperations.update(sql, ps -> {
				ps.setInt(1, process);
				ps.setDate(2, JdbcUtil.getDate(DateUtil.getSysDate()));
				ps.setLong(3, jobQueue.getThreadId());
				ps.setLong(4, queueId);
			});
		} else if (process == EodConstants.PROGRESS_SUCCESS) {
			sql = "Update Auto_Refund_Queue Set EndTime = ?, Progress = ? Where Id = ?";

			logger.debug(Literal.SQL.concat(sql));

			this.jdbcOperations.update(sql, ps -> {
				ps.setDate(1, JdbcUtil.getDate(DateUtil.getSysDate()));
				ps.setInt(2, EodConstants.PROGRESS_SUCCESS);
				ps.setLong(3, queueId);
			});
		} else if (process == EodConstants.PROGRESS_FAILED) {
			sql = "Update Auto_Refund_Queue Set EndTime = ?, ThreadId = ?, Progress = ?, ErrorLog = ? Where Id = ?";

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
		String sql = "Truncate table Auto_Refund_Queue";

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
		String sql = "Select FinID From Auto_Refund_Queue Where Id = ?";

		logger.debug(Literal.SQL.concat(sql));

		try {
			return jdbcOperations.queryForObject(sql, Long.class, sequence);
		} catch (EmptyResultDataAccessException e) {
			logger.warn(Message.NO_RECORD_FOUND);
			return null;
		}
	}

	@Override
	public void logQueue() {
		// TODO Auto-generated method stub

	}

	@Override
	public void logQueue(int progress) {
		// TODO Auto-generated method stub

	}

	private void updateQueingRecords(List<BatchJobQueue> jobQueue) {
		String sql = "Update Auto_Refund_Queue Set Id = ? Where ID = ? and BatchID = ?";

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
		String sql = "Select row_number() over(order by id) ResetCounterId, ID from Auto_Refund_Queue";

		logger.debug(Literal.SQL + sql);

		return this.jdbcOperations.query(sql.toString(), (rs, Num) -> {
			BatchJobQueue jobQueue = new BatchJobQueue();
			jobQueue.setId(rs.getLong("ID"));
			jobQueue.setResetCounterId(rs.getLong("ResetCounterId"));
			return jobQueue;
		});
	}

}
