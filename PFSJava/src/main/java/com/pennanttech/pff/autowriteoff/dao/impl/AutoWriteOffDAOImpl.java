package com.pennanttech.pff.autowriteoff.dao.impl;

import org.springframework.dao.DataAccessException;

import com.pennant.eod.constants.EodConstants;
import com.pennanttech.pennapps.core.jdbc.JdbcUtil;
import com.pennanttech.pennapps.core.jdbc.SequenceDao;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pennapps.core.util.DateUtil;
import com.pennanttech.pff.autowriteoff.dao.AutoWriteOffDAO;
import com.pennanttech.pff.autowriteoff.model.AutoWriteOffLoan;

public class AutoWriteOffDAOImpl extends SequenceDao<AutoWriteOffLoan> implements AutoWriteOffDAO {

	@Override
	public void deleteQueue() {
		jdbcOperations.update("Truncate table Auto_Write_Off_Calc_Queue");
	}

	@Override
	public long prepareQueueForEOM() {

		StringBuilder sql = new StringBuilder("Insert Into Auto_Write_Off_Calc_Queue(ID, FinID)");
		sql.append(" Select row_number() over(order by FinID) ID, FinID  From FinanceMain");
		sql.append(" Where FinIsActive = ? AND  WriteoffLoan = ?");

		logger.debug(Literal.SQL.concat(sql.toString()));

		return this.jdbcOperations.update(sql.toString(), 1, 0);
	}

	@Override
	public long getQueueCount() {
		String sql = "Select count(ID) From Auto_Write_Off_Calc_Queue where Progress = ?";

		logger.debug(Literal.SQL.concat(sql));

		return this.jdbcOperations.queryForObject(sql, Long.class, EodConstants.PROGRESS_WAIT);
	}

	@Override
	public int updateThreadID(long from, long to, int threadId) {
		String sql = "Update Auto_Write_Off_Calc_Queue Set ThreadId = ? Where Id > ? and Id <= ?  and ThreadId = ?";

		logger.debug(Literal.SQL.concat(sql));

		try {
			return this.jdbcOperations.update(sql, threadId, from, to, 0);
		} catch (DataAccessException dae) {
			logger.error(Literal.EXCEPTION, dae);
			return 0;
		}
	}

	@Override
	public void updateProgress(long finID, int progress) {
		String sql = null;
		if (progress == EodConstants.PROGRESS_IN_PROCESS) {
			sql = "Update Auto_Write_Off_Calc_Queue Set Progress = ?, StartTime = ? Where FinID = ? and Progress = ?";

			logger.debug(Literal.SQL.concat(sql));

			this.jdbcOperations.update(sql, ps -> {
				ps.setInt(1, progress);
				ps.setDate(2, JdbcUtil.getDate(DateUtil.getSysDate()));
				ps.setLong(3, finID);
				ps.setInt(4, EodConstants.PROGRESS_WAIT);
			});
		} else if (progress == EodConstants.PROGRESS_SUCCESS) {
			sql = "Update Auto_Write_Off_Calc_Queue Set EndTime = ?, Progress = ? where FinID = ?";

			logger.debug(Literal.SQL.concat(sql));

			this.jdbcOperations.update(sql, ps -> {
				ps.setDate(1, JdbcUtil.getDate(DateUtil.getSysDate()));
				ps.setInt(2, EodConstants.PROGRESS_SUCCESS);
				ps.setLong(3, finID);
			});
		} else if (progress == EodConstants.PROGRESS_FAILED) {
			sql = "Update Auto_Write_Off_Calc_Queue Set EndTime = ?, ThreadId = ?, Progress = ? Where FinID = ?";

			logger.debug(Literal.SQL.concat(sql));

			this.jdbcOperations.update(sql, ps -> {
				ps.setDate(1, JdbcUtil.getDate(DateUtil.getSysDate()));
				ps.setInt(2, 0);
				ps.setInt(3, EodConstants.PROGRESS_WAIT);
				ps.setLong(4, finID);
			});
		}
	}

	@Override
	public void insertlog(AutoWriteOffLoan awl) {
		StringBuilder sql = new StringBuilder("Insert Into Auto_Write_Off_Loans_Log");
		sql.append(" (FinID, FinReference, Code , ErrorMsg , executionDate , Status)");
		sql.append(" Values ( ?, ? ,? ,? ,? ,?)");

		logger.debug(Literal.SQL.concat(sql.toString()));

		this.jdbcOperations.update(sql.toString(), ps -> {
			int index = 1;

			ps.setLong(index++, awl.getFinID());
			ps.setString(index++, awl.getFinReference());
			ps.setString(index++, awl.getCode());
			ps.setString(index++, awl.getErrorMsg());
			ps.setDate(index++, JdbcUtil.getDate(awl.getExecutionDate()));
			ps.setString(index, awl.getStatus());
		});
	}
}