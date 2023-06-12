package com.pennanttech.pff.batch.backend.dao.impl;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.PreparedStatementSetter;
import org.springframework.jdbc.core.RowMapper;

import com.pennanttech.pennapps.core.ConcurrencyException;
import com.pennanttech.pennapps.core.jdbc.BasicDao;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pff.batch.backend.dao.BatchProcessStatusDAO;
import com.pennanttech.pff.batch.backend.service.impl.BatchProcessStatusServiceImpl;
import com.pennanttech.pff.batch.model.BatchProcessStatus;

public class BatchProcessStatusDAOImpl extends BasicDao<BatchProcessStatus> implements BatchProcessStatusDAO {
	private static Logger logger = LogManager.getLogger(BatchProcessStatusServiceImpl.class);

	@Override
	public BatchProcessStatus getBatchStatus(BatchProcessStatus batchProcessStatus) {
		logger.debug(Literal.ENTERING);

		StringBuilder sql = new StringBuilder("Select");
		sql.append(" Name, Status, StartTime, EndTime");
		sql.append(" From Batch_Process_Status");
		sql.append(" Where Name = ?");

		logger.trace(Literal.SQL + sql.toString());

		try {
			List<BatchProcessStatus> list = this.jdbcOperations.query(sql.toString(), new PreparedStatementSetter() {

				@Override
				public void setValues(PreparedStatement ps) throws SQLException {
					int index = 1;
					ps.setString(index, batchProcessStatus.getName());
				}
			}, new RowMapper<BatchProcessStatus>() {

				@Override
				public BatchProcessStatus mapRow(ResultSet rs, int rowNum) throws SQLException {
					BatchProcessStatus bps = new BatchProcessStatus();

					bps.setName(rs.getString("Name"));
					bps.setStatus(rs.getString("Status"));
					bps.setStartTime(rs.getTimestamp("StartTime"));
					bps.setEndTime(rs.getTimestamp("EndTime"));

					return bps;
				}
			});

			if (list == null || list.size() == 0) {
				return null;
			}
			return list.get(0);
		} catch (EmptyResultDataAccessException e) {
			logger.warn(Literal.EXCEPTION, e);
		}

		logger.debug(Literal.LEAVING);
		return null;
	}

	@Override
	public void saveBatchStatus(BatchProcessStatus bps) {
		logger.debug(Literal.ENTERING);

		StringBuilder sql = new StringBuilder("Insert into");
		sql.append(" Batch_Process_Status");
		sql.append(" (Name, Status, RunningStatus, FileName, Reference");
		sql.append(", ValueDate, TotalRecords, StartTime");
		sql.append(") values(");
		sql.append("?, ?, ?, ?, ?, ?, ?, ?");
		sql.append(")");

		logger.trace(Literal.SQL + sql.toString());

		try {
			jdbcTemplate.getJdbcOperations().update(sql.toString(), new PreparedStatementSetter() {

				@Override
				public void setValues(PreparedStatement ps) throws SQLException {
					int index = 1;

					ps.setString(index++, bps.getName());
					ps.setString(index++, bps.getStatus());
					ps.setBoolean(index++, bps.isRunningStatus());
					ps.setString(index++, bps.getFileName());
					ps.setString(index++, bps.getReference());
					ps.setTimestamp(index++, new Timestamp(bps.getValueDate().getTime()));
					ps.setLong(index++, bps.getTotalRecords());
					ps.setTimestamp(index, new Timestamp(bps.getStartTime().getTime()));
				}
			});
		} catch (DuplicateKeyException e) {
			logger.warn(Literal.EXCEPTION, e);

			throw new ConcurrencyException(e);
		}

		logger.debug(Literal.LEAVING);
	}

	@Override
	public void updateBatchStatus(BatchProcessStatus bps) {
		logger.debug(Literal.ENTERING);

		StringBuilder sql = new StringBuilder("Update");
		sql.append(" Batch_Process_Status set");
		sql.append(" Status = ?, RunningStatus = ?, ProcessedRecords = ?");
		sql.append(", SuccessRecords = ?, FailedRecords = ?");
		sql.append(", EndTime = ?, Remarks = ?");
		sql.append(" Where Name = ?");

		logger.trace(Literal.SQL + sql.toString());

		try {
			int update = jdbcTemplate.getJdbcOperations().update(sql.toString(), new PreparedStatementSetter() {

				@Override
				public void setValues(PreparedStatement ps) throws SQLException {
					int index = 1;

					ps.setString(index++, bps.getStatus());
					ps.setBoolean(index++, bps.isRunningStatus());
					ps.setLong(index++, bps.getProcessedRecords());
					ps.setLong(index++, bps.getSuccessRecords());
					ps.setLong(index++, bps.getFailedRecords());
					ps.setTimestamp(index++, new Timestamp(bps.getEndTime().getTime()));
					ps.setString(index++, bps.getRemarks());

					ps.setString(index, bps.getName());
				}
			});

			if (update == 0) {
				saveBatchStatus(bps);
			}

		} catch (DuplicateKeyException e) {
			logger.warn(Literal.EXCEPTION, e);

			throw new ConcurrencyException(e);
		}

		logger.debug(Literal.LEAVING);
	}

}
