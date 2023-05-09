package com.pennant.backend.dao.finance.manual.schedule.impl;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;

import com.pennant.backend.dao.finance.manual.schedule.ManualScheduleDAO;
import com.pennant.backend.model.finance.manual.schedule.ManualScheduleDetail;
import com.pennant.backend.model.finance.manual.schedule.ManualScheduleHeader;
import com.pennanttech.pennapps.core.ConcurrencyException;
import com.pennanttech.pennapps.core.DependencyFoundException;
import com.pennanttech.pennapps.core.jdbc.JdbcUtil;
import com.pennanttech.pennapps.core.jdbc.SequenceDao;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pennapps.core.resource.Message;
import com.pennanttech.pff.core.TableType;

public class ManualScheduleDAOImpl extends SequenceDao<ManualScheduleHeader> implements ManualScheduleDAO {
	private static Logger logger = LogManager.getLogger(ManualScheduleDAOImpl.class);

	public ManualScheduleDAOImpl() {
		super();
	}

	@Override
	public long saveHeaderDetails(ManualScheduleHeader schdHdr, String tableType) {
		if (schdHdr.getId() == Long.MIN_VALUE) {
			schdHdr.setId(getNextValue("SeqManualSchdHeader"));
		}

		StringBuilder sql = new StringBuilder();
		sql.append("Insert Into Manual_Schedule_Header");
		sql.append(tableType);
		sql.append(" (Id, FileName, TransactionDate, TotalSchedules, FinEvent, FinReference,");
		sql.append(" Version, LastMntBy, LastMntOn, RecordStatus, RoleCode, NextRoleCode, TaskId,");
		sql.append(" NextTaskId, RecordType, WorkflowId)");
		sql.append(" Values ( ?, ?, ?, ?, ?, ?,");
		sql.append(" ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)");

		logger.debug(Literal.SQL + sql.toString());

		try {
			jdbcOperations.update(sql.toString(), ps -> {
				int index = 1;

				ps.setLong(index++, schdHdr.getId());
				ps.setString(index++, schdHdr.getFileName());
				ps.setDate(index++, JdbcUtil.getDate(schdHdr.getTransactionDate()));
				ps.setInt(index++, schdHdr.getTotalSchedules());
				ps.setString(index++, schdHdr.getFinEvent());
				ps.setString(index++, schdHdr.getFinReference());
				ps.setInt(index++, schdHdr.getVersion());
				ps.setLong(index++, JdbcUtil.getLong(schdHdr.getLastMntBy()));
				ps.setTimestamp(index++, schdHdr.getLastMntOn());
				ps.setString(index++, schdHdr.getRecordStatus());
				ps.setString(index++, schdHdr.getRoleCode());
				ps.setString(index++, schdHdr.getNextRoleCode());
				ps.setString(index++, schdHdr.getTaskId());
				ps.setString(index++, schdHdr.getNextTaskId());
				ps.setString(index++, schdHdr.getRecordType());
				ps.setLong(index, JdbcUtil.getLong(schdHdr.getWorkflowId()));

			});
		} catch (DuplicateKeyException e) {
			throw new ConcurrencyException(e);
		}

		return schdHdr.getId();
	}

	public void saveManualSchdDetails(List<ManualScheduleDetail> details, String type) {
		StringBuilder sql = new StringBuilder();
		sql.append("Insert Into Manual_Schedule_Details");
		sql.append(type);
		sql.append(" (Id, Header_Id, SchDate, PrincipalSchd");
		sql.append(", PftOnSchDate, RvwOnSchDate, Status, Reason) ");
		sql.append(" Values( ?, ?, ?, ?, ?, ?, ?, ?)");

		logger.debug(Literal.SQL + sql.toString());

		try {
			this.jdbcOperations.batchUpdate(sql.toString(), new BatchPreparedStatementSetter() {

				@Override
				public void setValues(PreparedStatement ps, int i) throws SQLException {
					ManualScheduleDetail mnlSchedDtl = details.get(i);
					setInsertParameterizedFields(mnlSchedDtl, ps);
				}

				@Override
				public int getBatchSize() {
					return details.size();
				}
			});
		} catch (Exception e) {
			throw e;
		}
	}

	@Override
	public ManualScheduleHeader getManualSchdHeader(long finID, String finEvent, String type) {
		StringBuilder sql = new StringBuilder("Select");
		sql.append(" Id, FileName, TransactionDate, TotalSchedules, FinEvent, FinID, FinReference");
		sql.append(", Version, LastMntBy, LastMntOn, RecordStatus, RoleCode, NextRoleCode, TaskId");
		sql.append(", NextTaskId, RecordType, WorkflowId");
		sql.append(" From Manual_Schedule_Header");
		sql.append(type);
		sql.append(" Where FinID = ? and FinEvent = ?");

		logger.debug(Literal.SQL + sql.toString());

		try {
			return this.jdbcOperations.queryForObject(sql.toString(), (rs, rowNum) -> {
				ManualScheduleHeader mSchd = new ManualScheduleHeader();

				mSchd.setId(rs.getLong("Id"));
				mSchd.setFileName(rs.getString("FileName"));
				mSchd.setTransactionDate(rs.getDate("TransactionDate"));
				mSchd.setTotalSchedules(rs.getInt("TotalSchedules"));
				mSchd.setFinEvent(rs.getString("FinEvent"));
				mSchd.setFinID(rs.getLong("FinID"));
				mSchd.setFinReference(rs.getString("FinReference"));
				mSchd.setVersion(rs.getInt("Version"));
				mSchd.setLastMntBy(rs.getLong("LastMntBy"));
				mSchd.setLastMntOn(rs.getTimestamp("LastMntOn"));
				mSchd.setRecordStatus(rs.getString("RecordStatus"));
				mSchd.setRoleCode(rs.getString("RoleCode"));
				mSchd.setNextRoleCode(rs.getString("NextRoleCode"));
				mSchd.setTaskId(rs.getString("TaskId"));
				mSchd.setNextTaskId(rs.getString("NextTaskId"));
				mSchd.setRecordType(rs.getString("RecordType"));
				mSchd.setWorkflowId(rs.getLong("WorkflowId"));

				return mSchd;
			}, finID, finEvent);
		} catch (EmptyResultDataAccessException e) {
			logger.warn(Message.NO_RECORD_FOUND);
			return null;
		}
	}

	@Override
	public List<ManualScheduleDetail> getManualSchdDetailsById(long headerId, String tableType) {
		StringBuilder sql = new StringBuilder("SELECT");
		sql.append(" Id, Header_Id, SchDate, PrincipalSchd, PftOnSchDate");
		sql.append(", RvwOnSchDate, Status, Reason");
		sql.append(" From Manual_Schedule_Details");
		sql.append(tableType);
		sql.append(" Where Header_Id = ?");

		logger.debug(Literal.SQL + sql.toString());

		return this.jdbcOperations.query(sql.toString(), (rs, rowNum) -> {
			ManualScheduleDetail msDtl = new ManualScheduleDetail();

			msDtl.setId(rs.getLong("Id"));
			msDtl.setHeaderId(rs.getLong("Header_Id"));
			msDtl.setSchDate(rs.getDate("SchDate"));
			msDtl.setPrincipalSchd(rs.getBigDecimal("PrincipalSchd"));
			msDtl.setPftOnSchDate(rs.getBoolean("PftOnSchDate"));
			msDtl.setRvwOnSchDate(rs.getBoolean("RvwOnSchDate"));
			msDtl.setStatus(rs.getString("Status"));
			msDtl.setReason(rs.getString("Reason"));

			return msDtl;
		}, headerId);
	}

	public void delete(ManualScheduleHeader uploadManualSchdHeader, TableType tableType) {
		StringBuilder sql = new StringBuilder("Delete From Manual_Schedule_Header");
		sql.append(tableType.getSuffix());
		sql.append(" Where Id = ?");

		logger.debug(Literal.SQL + sql.toString());

		try {
			jdbcOperations.update(sql.toString(), ps -> ps.setLong(1, uploadManualSchdHeader.getId()));
		} catch (DataAccessException e) {
			throw new DependencyFoundException(e);
		}
	}

	@Override
	public void deleteById(long headerId, TableType tableType) {
		ManualScheduleDetail details = new ManualScheduleDetail();
		details.setHeaderId(headerId);

		StringBuilder sql = new StringBuilder("Delete From Manual_Schedule_Details");
		sql.append(tableType.getSuffix());
		sql.append(" where Header_Id = ?");

		logger.debug(Literal.SQL + sql.toString());

		try {
			jdbcOperations.update(sql.toString(), ps -> ps.setLong(1, headerId));
		} catch (DataAccessException e) {
			throw new DependencyFoundException(e);
		}
	}

	private void setInsertParameterizedFields(ManualScheduleDetail schdDtl, PreparedStatement ps) throws SQLException {
		int index = 1;

		if (schdDtl.getId() == 0 || schdDtl.getId() == Long.MIN_VALUE) {
			schdDtl.setId(getNextValue("seqManualSchdDetails"));
		}

		ps.setLong(index++, schdDtl.getId());
		ps.setLong(index++, schdDtl.getHeaderId());
		ps.setDate(index++, JdbcUtil.getDate(schdDtl.getSchDate()));
		ps.setBigDecimal(index++, schdDtl.getPrincipalSchd());
		ps.setBoolean(index++, schdDtl.isPftOnSchDate());
		ps.setBoolean(index++, schdDtl.isRvwOnSchDate());
		ps.setString(index++, schdDtl.getStatus());
		ps.setString(index, schdDtl.getReason());
	}

	@Override
	public boolean isFileNameExist(String fileName, String type) {
		StringBuilder sql = new StringBuilder();
		sql.append("Select coalesce(Count(FileName), 0) From Manual_Schedule_Header");
		sql.append(type);
		sql.append(" Where FileName = ? ");

		logger.debug(Literal.SQL + sql.toString());

		return this.jdbcOperations.queryForObject(sql.toString(), (rs, rowNum) -> rs.getInt(1), fileName) > 0;

	}

}
