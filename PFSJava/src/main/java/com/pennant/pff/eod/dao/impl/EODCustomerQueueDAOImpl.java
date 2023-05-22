package com.pennant.pff.eod.dao.impl;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Date;
import java.util.List;

import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;

import com.pennant.app.util.SysParamUtil;
import com.pennant.eod.constants.EodConstants;
import com.pennant.pff.batch.job.dao.BatchJobQueueDAO;
import com.pennant.pff.batch.job.model.BatchJobQueue;
import com.pennanttech.pennapps.core.App;
import com.pennanttech.pennapps.core.jdbc.JdbcUtil;
import com.pennanttech.pennapps.core.jdbc.SequenceDao;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pennapps.core.util.DateUtil;

public class EODCustomerQueueDAOImpl extends SequenceDao<BatchJobQueue> implements BatchJobQueueDAO {
	private static final String SEQUENCE_NAME = "SEQ_EOD_CUSTOMER_QUEUE";

	@Override
	public int prepareQueue(BatchJobQueue jobQueue) {
		StringBuilder sql = new StringBuilder();
		sql.append("Insert Into Eod_Customer_Queue (ID, AppDate, CustId, CoreBankID, LoanExist)");
		sql.append(" Select row_number() over(order by CustCoreBank) ID, ?, CustID, CustCoreBank, LoanExist From (");
		sql.append(" Select distinct c.CustID, c.CustCoreBank, 1 LoanExist");
		sql.append(" From  FinanceMain fm");
		sql.append(" Inner Join Customers c on c.CustID = fm.CustID");
		sql.append(" Where fm.FinIsActive = ?");
		sql.append(" Union all");
		sql.append(" Select distinct c.CustID, c.CustCoreBank, 0 LoanExist");
		sql.append(" From LimitHeader lh");
		sql.append(" Inner Join Customers c on c.CustID = lh.CustomerID");
		sql.append(" Inner Join LIMITSTRUCTURE ls on ls.StructureCode = lh.LimitStructureCode and ls.Rebuild = ?");
		sql.append(" Where c.CustCoreBank not in (Select distinct c.CustCoreBank");
		sql.append(" From  FinanceMain fm");
		sql.append(" Inner Join Customers c on c.CustID = fm.CustID");
		sql.append(" Where fm.FinIsActive = ?)");
		sql.append(") T");

		logger.debug(Literal.SQL.concat(sql.toString()));

		return this.jdbcOperations.update(sql.toString(), ps -> {
			Date appDate = SysParamUtil.getAppDate();

			ps.setDate(1, JdbcUtil.getDate(appDate));
			ps.setBoolean(2, true);
			ps.setBoolean(3, true);
			ps.setBoolean(4, true);

		});
	}

	@Override
	public void updateQueue(BatchJobQueue jobQueue) {
		StringBuilder sql = new StringBuilder("Update Eod_Customer_Queue set LimitRebuild = ?");
		sql.append(" Where CustId in (Select lh.CustomerId From LimitHeader lh");
		sql.append(" Inner Join LimitStructure ls on ls.StructureCode = lh.LimitStructureCode and ls.Rebuild = ?)");

		logger.debug(Literal.SQL.concat(sql.toString()));

		jdbcOperations.update(sql.toString(), ps -> {
			ps.setInt(1, 1);
			ps.setInt(2, 1);
		});

	}

	@Override
	public void handleFailures(BatchJobQueue jobQueue) {
		String sql = "Delete From Eod_Customer_Queue Where Progress = ?";

		logger.debug(Literal.SQL.concat(sql));

		this.jdbcOperations.update(sql, ps -> {
			ps.setInt(1, EodConstants.PROGRESS_SUCCESS);
		});

		sql = "Update Eod_Customer_Queue Set Progress = ? Where Progress = ?";

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
		String sql = "Select count(Id) From Eod_Customer_Queue ecq Inner Join FinanceMain fm on fm.CustId = ecq.CustID Where fm.FinIsActive = 1";

		logger.debug(Literal.SQL.concat(sql));

		return this.jdbcOperations.queryForObject(sql, Integer.class);
	}

	@Override
	public int getQueueCount() {
		String sql = "Select count(Id) From Eod_Customer_Queue";

		logger.debug(Literal.SQL.concat(sql));

		return this.jdbcOperations.queryForObject(sql, Integer.class);
	}

	@Override
	public int getQueueCount(BatchJobQueue jobQueue) {
		return 0;
	}

	@Override
	public int updateThreadID(long from, long to, int threadId) {
		String sql = "Update Eod_Customer_Queue Set ThreadId = ? Where Id > ? and Id <= ?  and ThreadId = ?";

		logger.debug(Literal.SQL + sql);

		try {
			return this.jdbcOperations.update(sql, threadId, from, to, 0);
		} catch (DataAccessException dae) {
			logger.error(Literal.EXCEPTION, dae);
		}

		return 0;
	}

	@Override
	public void updateProgress(BatchJobQueue jobQueue) {
		int process = jobQueue.getProgress();
		long queueId = jobQueue.getId();

		String sql = null;
		if (process == EodConstants.PROGRESS_IN_PROCESS) {
			sql = "Update Eod_Customer_Queue Set Progress = ?, StartTime = ?, ThreadId = ? Where Id = ?";

			logger.debug(Literal.SQL.concat(sql));

			this.jdbcOperations.update(sql, ps -> {
				ps.setInt(1, process);
				ps.setDate(2, JdbcUtil.getDate(DateUtil.getSysDate()));
				ps.setLong(3, jobQueue.getThreadId());

				ps.setLong(4, queueId);
			});
		} else if (process == EodConstants.PROGRESS_SUCCESS) {
			sql = "Update Eod_Customer_Queue Set EndTime = ?, Progress = ? Where Id = ?";

			logger.debug(Literal.SQL.concat(sql));

			this.jdbcOperations.update(sql, ps -> {
				ps.setDate(1, JdbcUtil.getDate(DateUtil.getSysDate()));
				ps.setInt(2, EodConstants.PROGRESS_SUCCESS);

				ps.setLong(3, queueId);
			});
		} else if (process == EodConstants.PROGRESS_FAILED) {
			sql = "Update Eod_Customer_Queue Set EndTime = ?, ThreadId = ?, Progress = ?, ErrorLog = ? Where Id = ?";

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
	public void logQueue() {
		StringBuilder sql = new StringBuilder("INSERT INTO Eod_Customer_Queue_log (");
		sql.append("SeqId, AppDate, CustId, CoreBankID, LoanExist, LimitRebuild, WorkerHost, ThreadId");
		sql.append(", StartTime, EndTime, Progress, ErrorLog");
		sql.append(") Select ");
		sql.append(" Id, AppDate, CustId, CoreBankID, LoanExist, LimitRebuild, WorkerHost, ThreadId");
		sql.append(", StartTime, EndTime, Progress, ErrorLog");
		sql.append(" From Eod_Customer_Queue");

		logger.debug(Literal.SQL.concat(sql.toString()));

		this.jdbcOperations.update(sql.toString());
	}

	@Override
	public void logQueue(int progress) {
		StringBuilder sql = new StringBuilder("INSERT INTO Eod_Customer_Queue_log (");
		sql.append("SeqId, AppDate, CustId, CoreBankID, LoanExist, LimitRebuild, WorkerHost, ThreadId");
		sql.append(", StartTime, EndTime, Progress, ErrorLog");
		sql.append(") Select ");
		sql.append(" SeqId, AppDate, CustId, CoreBankID, LoanExist, LimitRebuild, WorkerHost, ThreadId");
		sql.append(", StartTime, EndTime, Progress, ErrorLog");
		sql.append(" From Eod_Customer_Queue Where Progress = ?");

		logger.debug(Literal.SQL.concat(sql.toString()));

		this.jdbcOperations.update(sql.toString(), EodConstants.PROGRESS_SUCCESS);
	}

	@Override
	public void clearQueue() {
		String sql = "Truncate table Eod_Customer_Queue";

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
		// TODO Auto-generated method stub
		return null;
	}

	private List<BatchJobQueue> getQueingRecords(BatchJobQueue bJobQueue) {
		String sql = "Select row_number() over(order by id) ResetCounterId, ID From Eod_Customer_Queue";

		logger.debug(Literal.SQL.concat(sql));

		return this.jdbcOperations.query(sql, (rs, rowNum) -> {
			BatchJobQueue jobQueue = new BatchJobQueue();
			jobQueue.setId(rs.getLong("ID"));
			jobQueue.setResetCounterId(rs.getLong("ResetCounterId"));
			return jobQueue;
		});
	}

	private void updateQueingRecords(List<BatchJobQueue> list) {
		String sql = "Update Eod_Customer_Queue Set Id = ? Where ID = ?";

		logger.debug(Literal.SQL + sql);

		try {
			jdbcOperations.batchUpdate(sql, new BatchPreparedStatementSetter() {

				@Override
				public void setValues(PreparedStatement ps, int i) throws SQLException {
					BatchJobQueue jobQueue = list.get(i);
					int index = 0;

					ps.setLong(++index, jobQueue.getResetCounterId());
					ps.setLong(++index, jobQueue.getId());
				}

				@Override
				public int getBatchSize() {
					return list.size();
				}
			});
		} catch (DataAccessException e) {
			logger.warn(Literal.EXCEPTION, e);
		}
	}
}
