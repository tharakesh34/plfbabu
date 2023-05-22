package com.pennanttech.pff.overdraft.dao.impl;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;

import com.pennanttech.pennapps.core.ConcurrencyException;
import com.pennanttech.pennapps.core.DependencyFoundException;
import com.pennanttech.pennapps.core.jdbc.JdbcUtil;
import com.pennanttech.pennapps.core.jdbc.SequenceDao;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pennapps.core.resource.Message;
import com.pennanttech.pff.core.TableType;
import com.pennanttech.pff.overdraft.dao.VariableOverdraftScheduleDAO;
import com.pennanttech.pff.overdraft.model.VariableOverdraftSchdDetail;
import com.pennanttech.pff.overdraft.model.VariableOverdraftSchdHeader;

public class VariableOverdraftScheduleDAOImpl extends SequenceDao<VariableOverdraftSchdHeader>
		implements VariableOverdraftScheduleDAO {
	private static final Logger logger = LogManager.getLogger(VariableOverdraftScheduleDAOImpl.class);

	@Override
	public boolean isFileNameExist(String fileName, String type) {
		logger.debug(Literal.ENTERING);

		StringBuilder sql = new StringBuilder();
		sql.append(" SELECT Count(*) From VARIABLE_OD_SCHD_HEADER");
		sql.append(type);
		sql.append(" Where FileName = ? ");

		logger.debug(Literal.SQL + sql.toString());

		logger.debug(Literal.LEAVING);
		return this.jdbcOperations.queryForObject(sql.toString(), (rs, rowNum) -> rs.getInt(1), fileName) > 0;
	}

	@Override
	public long saveHeader(VariableOverdraftSchdHeader scheduleHeader, String tableType) {
		logger.debug(Literal.ENTERING);

		StringBuilder sql = new StringBuilder();

		if (scheduleHeader.getId() == Long.MIN_VALUE) {
			scheduleHeader.setId(getNextValue("SEQ_VARIABLE_OD_SCHD_HEADER"));
		}

		sql.append(" Insert Into VARIABLE_OD_SCHD_HEADER");
		sql.append(tableType);
		sql.append(" (Id, FileName, TransactionDate, TotalSchedules, FinEvent, FinReference,");
		sql.append(" Version, LastMntBy, LastMntOn, RecordStatus, RoleCode, NextRoleCode, TaskId,");
		sql.append(" NextTaskId, RecordType, WorkflowId)");
		sql.append(" Values ( ?, ?, ?, ?, ?, ?");
		sql.append(", ?, ?, ?, ?, ?, ?, ?");
		sql.append(" , ?, ?, ?)");
		logger.debug(Literal.SQL + sql.toString());

		try {
			jdbcOperations.update(sql.toString(), ps -> {
				int index = 1;

				ps.setLong(index++, scheduleHeader.getId());
				ps.setString(index++, scheduleHeader.getFileName());
				ps.setDate(index++, JdbcUtil.getDate(scheduleHeader.getTransactionDate()));
				ps.setInt(index++, scheduleHeader.getTotalSchedules());
				ps.setString(index++, scheduleHeader.getFinEvent());
				ps.setString(index++, scheduleHeader.getFinReference());
				ps.setInt(index++, scheduleHeader.getVersion());
				ps.setLong(index++, JdbcUtil.getLong(scheduleHeader.getLastMntBy()));
				ps.setTimestamp(index++, scheduleHeader.getLastMntOn());
				ps.setString(index++, scheduleHeader.getRecordStatus());
				ps.setString(index++, scheduleHeader.getRoleCode());
				ps.setString(index++, scheduleHeader.getNextRoleCode());
				ps.setString(index++, scheduleHeader.getTaskId());
				ps.setString(index++, scheduleHeader.getNextTaskId());
				ps.setString(index++, scheduleHeader.getRecordType());
				ps.setLong(index, JdbcUtil.getLong(scheduleHeader.getWorkflowId()));

			});
		} catch (DuplicateKeyException e) {
			throw new ConcurrencyException(e);
		}

		logger.debug(Literal.LEAVING);
		return scheduleHeader.getId();
	}

	@Override
	public VariableOverdraftSchdHeader getHeader(String finReference, String finEvent, String tableType) {
		logger.debug(Literal.ENTERING);

		StringBuilder sql = new StringBuilder("SELECT");
		sql.append(" Id, FileName, TransactionDate, TotalSchedules, FinEvent, FinReference");
		sql.append(", Version, LastMntBy, LastMntOn, RecordStatus, RoleCode, NextRoleCode, TaskId");
		sql.append(", NextTaskId, RecordType, WorkflowId");
		sql.append(" From VARIABLE_OD_SCHD_HEADER");
		sql.append(tableType);
		sql.append(" Where FinReference = ? and FinEvent = ?");

		logger.debug(Literal.SQL + sql.toString());

		try {
			return this.jdbcOperations.queryForObject(sql.toString(), (rs, rowNum) -> {
				VariableOverdraftSchdHeader varODSchd = new VariableOverdraftSchdHeader();

				varODSchd.setId(rs.getLong("Id"));
				varODSchd.setFileName(rs.getString("FileName"));
				varODSchd.setTransactionDate(rs.getDate("TransactionDate"));
				varODSchd.setTotalSchedules(rs.getInt("TotalSchedules"));
				varODSchd.setFinEvent(rs.getString("FinEvent"));
				varODSchd.setFinReference(rs.getString("FinReference"));
				varODSchd.setVersion(rs.getInt("Version"));
				varODSchd.setLastMntBy(rs.getLong("LastMntBy"));
				varODSchd.setLastMntOn(rs.getTimestamp("LastMntOn"));
				varODSchd.setRecordStatus(rs.getString("RecordStatus"));
				varODSchd.setRoleCode(rs.getString("RoleCode"));
				varODSchd.setNextRoleCode(rs.getString("NextRoleCode"));
				varODSchd.setTaskId(rs.getString("TaskId"));
				varODSchd.setNextTaskId(rs.getString("NextTaskId"));
				varODSchd.setRecordType(rs.getString("RecordType"));
				varODSchd.setWorkflowId(rs.getLong("WorkflowId"));

				return varODSchd;
			}, finReference, finEvent);
		} catch (EmptyResultDataAccessException e) {
			logger.warn(Message.NO_RECORD_FOUND);
		}

		logger.debug(Literal.LEAVING);
		return null;
	}

	@Override
	public List<VariableOverdraftSchdDetail> getDetails(long headerId, String tableType) {
		logger.debug(Literal.ENTERING);

		StringBuilder sql = new StringBuilder("SELECT");
		sql.append(" Id, Header_Id, SchDate");
		sql.append(", DroplineAmount, Status, Reason");
		sql.append(" From VARIABLE_OD_SCHD_DETAILS");
		sql.append(tableType);
		sql.append(" Where Header_Id = ?");

		logger.trace(Literal.SQL + sql.toString());

		return this.jdbcOperations.query(sql.toString(), (rs, rowNum) -> {
			VariableOverdraftSchdDetail varODSchdDtl = new VariableOverdraftSchdDetail();

			varODSchdDtl.setId(rs.getLong("Id"));
			varODSchdDtl.setHeaderId(rs.getLong("Header_Id"));
			varODSchdDtl.setSchDate(rs.getDate("SchDate"));
			varODSchdDtl.setDroplineAmount(rs.getBigDecimal("DroplineAmount"));
			varODSchdDtl.setStatus(rs.getString("Status"));
			varODSchdDtl.setReason(rs.getString("Reason"));

			return varODSchdDtl;
		}, headerId);
	}

	@Override
	public void delete(VariableOverdraftSchdHeader uploadVariableODSchdGeader, TableType tableType) {
		logger.debug(Literal.ENTERING);

		StringBuilder sql = new StringBuilder("Delete From VARIABLE_OD_SCHD_HEADER");
		sql.append(tableType.getSuffix());
		sql.append(" Where Id = :Id ");

		logger.debug(Literal.SQL + sql.toString());
		SqlParameterSource paramSource = new BeanPropertySqlParameterSource(uploadVariableODSchdGeader);

		try {
			jdbcTemplate.update(sql.toString(), paramSource);
		} catch (DataAccessException e) {
			throw new DependencyFoundException(e);
		}

		logger.debug(Literal.LEAVING);
	}

	@Override
	public void deleteById(long headerId, TableType tableType) {
		logger.debug(Literal.ENTERING);

		VariableOverdraftSchdDetail details = new VariableOverdraftSchdDetail();
		details.setHeaderId(headerId);

		StringBuilder sql = new StringBuilder("Delete From VARIABLE_OD_SCHD_DETAILS");
		sql.append(tableType.getSuffix());
		sql.append(" Where Header_Id = :HeaderId ");

		logger.debug(Literal.SQL + sql.toString());
		SqlParameterSource paramSource = new BeanPropertySqlParameterSource(details);

		try {
			jdbcTemplate.update(sql.toString(), paramSource);
		} catch (DataAccessException e) {
			throw new DependencyFoundException(e);
		}

		logger.debug(Literal.LEAVING);
	}

	@Override
	public void saveDetails(List<VariableOverdraftSchdDetail> details, String tableType) {
		logger.debug(Literal.ENTERING);

		StringBuilder sql = new StringBuilder();
		sql.append(" INSERT Into VARIABLE_OD_SCHD_DETAILS");
		sql.append(tableType);
		sql.append(" (Id, Header_Id, SchDate, DroplineAmount");
		sql.append(", Status, Reason) ");
		sql.append(" VALUES( ?, ?, ?, ?, ?, ?)");

		logger.debug(Literal.SQL + sql.toString());

		try {
			this.jdbcOperations.batchUpdate(sql.toString(), new BatchPreparedStatementSetter() {

				@Override
				public void setValues(PreparedStatement ps, int i) throws SQLException {
					VariableOverdraftSchdDetail varODSchdDtl = details.get(i);
					setInsertParameterizedFields(varODSchdDtl, ps);
				}

				@Override
				public int getBatchSize() {
					return details.size();
				}
			});
		} catch (Exception e) {
			throw e;
		}
		logger.debug(Literal.LEAVING);
	}

	protected void setInsertParameterizedFields(VariableOverdraftSchdDetail varODSchdDtl, PreparedStatement ps)
			throws SQLException {
		int index = 1;

		if (varODSchdDtl.getId() == 0 || varODSchdDtl.getId() == Long.MIN_VALUE) {
			varODSchdDtl.setId(getNextValue("SEQ_VARIABLE_OD_SCHD_DETAILS"));
		}
		ps.setLong(index++, varODSchdDtl.getId());
		ps.setLong(index++, varODSchdDtl.getHeaderId());
		ps.setDate(index++, JdbcUtil.getDate(varODSchdDtl.getSchDate()));
		ps.setBigDecimal(index++, varODSchdDtl.getDroplineAmount());
		ps.setString(index++, varODSchdDtl.getStatus());
		ps.setString(index, varODSchdDtl.getReason());
	}
}
