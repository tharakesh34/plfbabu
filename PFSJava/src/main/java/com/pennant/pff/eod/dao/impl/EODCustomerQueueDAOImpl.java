package com.pennant.pff.eod.dao.impl;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;

import com.pennant.app.util.SysParamUtil;
import com.pennant.eod.constants.EodConstants;
import com.pennant.pff.batch.job.dao.BatchJobQueueDAO;
import com.pennant.pff.batch.job.model.BatchJobQueue;
import com.pennanttech.pennapps.core.jdbc.JdbcUtil;
import com.pennanttech.pennapps.core.jdbc.SequenceDao;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pennapps.core.util.DateUtil;

public class EODCustomerQueueDAOImpl extends SequenceDao<BatchJobQueue> implements BatchJobQueueDAO {

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
			ps.setDate(1, JdbcUtil.getDate(SysParamUtil.getAppDate()));
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
	public int getQueueCount() {
		String sql = "Select Coalesce(count(Id), 0) From Eod_Customer_Queue";

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
			sql = "Update Eod_Customer_Queue Set EndTime = ?, ThreadId = ?, Progress = ? Where Id = ?";

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
	public void clearQueue() {
		// TODO Auto-generated method stub

	}

	@Override
	public long getNextValue() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void resetSequence() {
		// TODO Auto-generated method stub

	}

	@Override
	public Long getIdBySequence(long sequence) {
		// TODO Auto-generated method stub
		return null;
	}

	private List<BatchJobQueue> getQueingRecords(BatchJobQueue bJobQueue) {
		String sql = "Select row_number() over(order by id) ResetCounterId, ID From Eod_Customer_Queue";

		logger.debug(Literal.SQL + sql);

		return this.jdbcOperations.query(sql.toString(), ps -> {
			ps.setLong(1, bJobQueue.getBatchId());
		}, (rs, Num) -> {
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
