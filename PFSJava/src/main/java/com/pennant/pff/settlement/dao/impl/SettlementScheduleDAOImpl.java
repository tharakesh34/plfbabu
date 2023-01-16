package com.pennant.pff.settlement.dao.impl;

import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DuplicateKeyException;

import com.pennant.pff.settlement.dao.SettlementScheduleDAO;
import com.pennant.pff.settlement.model.SettlementSchedule;
import com.pennanttech.pennapps.core.ConcurrencyException;
import com.pennanttech.pennapps.core.DependencyFoundException;
import com.pennanttech.pennapps.core.jdbc.JdbcUtil;
import com.pennanttech.pennapps.core.jdbc.SequenceDao;
import com.pennanttech.pennapps.core.resource.Literal;

public class SettlementScheduleDAOImpl extends SequenceDao<SettlementSchedule> implements SettlementScheduleDAO {

	public SettlementScheduleDAOImpl() {
		super();
	}

	@Override
	public String save(SettlementSchedule ss, String type) {
		if (ss.getSettlementDetailID() == 0 || ss.getSettlementDetailID() == Long.MIN_VALUE) {
			ss.setSettlementDetailID(getNextValue("SeqSettlementSchedule"));
		}

		StringBuilder sql = new StringBuilder("Insert into Settlement_Schedule");
		sql.append(StringUtils.trimToEmpty(type));
		sql.append("(HeaderID, Id, SettlementInstalDate, SettlementAmount");
		sql.append(", Version, LastMntBy, LastMntOn, RecordStatus, RoleCode, NextRoleCode");
		sql.append(", TaskId, NextTaskId, RecordType, WorkflowId)");
		sql.append(" Values (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?) ");

		logger.debug(Literal.SQL.concat(sql.toString()));

		try {
			jdbcOperations.update(sql.toString(), ps -> {
				int index = 0;

				ps.setLong(++index, ss.getSettlementHeaderID());
				ps.setLong(++index, ss.getSettlementDetailID());
				ps.setDate(++index, JdbcUtil.getDate(ss.getSettlementInstalDate()));
				ps.setBigDecimal(++index, ss.getSettlementAmount());
				ps.setInt(++index, ss.getVersion());
				ps.setLong(++index, ss.getLastMntBy());
				ps.setTimestamp(++index, ss.getLastMntOn());
				ps.setString(++index, ss.getRecordStatus());
				ps.setString(++index, ss.getRoleCode());
				ps.setString(++index, ss.getNextRoleCode());
				ps.setString(++index, ss.getTaskId());
				ps.setString(++index, ss.getNextTaskId());
				ps.setString(++index, ss.getRecordType());
				ps.setLong(++index, ss.getWorkflowId());
			});
		} catch (DuplicateKeyException e) {
			throw new ConcurrencyException(e);
		}

		return String.valueOf(ss.getSettlementDetailID());
	}

	@Override
	public void update(SettlementSchedule ss, String type) {
		StringBuilder sql = new StringBuilder("update Settlement_Schedule");
		sql.append(StringUtils.trimToEmpty(type));
		sql.append(" set SettlementInstalDate = ?, SettlementAmount = ?,");
		sql.append(" Version = ?, LastMntBy = ?, LastMntOn = ?, RecordStatus = ?, RoleCode = ?, NextRoleCode = ?,");
		sql.append(" TaskId = ?, NextTaskId = ?, RecordType = ?, WorkflowId = ?");
		sql.append(" Where HeaderID = ? and ID = ?");

		logger.debug(Literal.SQL.concat(sql.toString()));

		int recordCount = jdbcOperations.update(sql.toString(), ps -> {
			int index = 0;

			ps.setDate(++index, JdbcUtil.getDate(ss.getSettlementInstalDate()));
			ps.setBigDecimal(++index, ss.getSettlementAmount());
			ps.setInt(++index, ss.getVersion());
			ps.setLong(++index, ss.getLastMntBy());
			ps.setTimestamp(++index, ss.getLastMntOn());
			ps.setString(++index, ss.getRecordStatus());
			ps.setString(++index, ss.getRoleCode());
			ps.setString(++index, ss.getNextRoleCode());
			ps.setString(++index, ss.getTaskId());
			ps.setString(++index, ss.getNextTaskId());
			ps.setString(++index, ss.getRecordType());
			ps.setLong(++index, ss.getWorkflowId());
			ps.setLong(++index, ss.getSettlementHeaderID());
			ps.setLong(++index, ss.getSettlementDetailID());
		});

		if (recordCount == 0) {
			throw new ConcurrencyException();
		}
	}

	@Override
	public List<SettlementSchedule> getSettlementScheduleDetails(long id, String type) {
		StringBuilder sql = new StringBuilder("Select");
		sql.append(" HeaderID, ID, SettlementInstalDate, SettlementAmount");
		sql.append(", Version, LastMntBy, LastMntOn, RecordStatus, RoleCode, NextRoleCode");
		sql.append(", TaskId, NextTaskId, RecordType, WorkflowId ");
		sql.append(" From Settlement_Schedule");
		sql.append(type);
		sql.append(" Where HeaderID = ?");

		logger.debug(Literal.SQL.concat(sql.toString()));

		return this.jdbcOperations.query(sql.toString(), (rs, rowNum) -> {
			SettlementSchedule ss = new SettlementSchedule();

			ss.setSettlementHeaderID(rs.getLong("HeaderID"));
			ss.setSettlementDetailID(rs.getLong("ID"));
			ss.setSettlementInstalDate(JdbcUtil.getDate(rs.getDate("SettlementInstalDate")));
			ss.setSettlementAmount(rs.getBigDecimal("SettlementAmount"));
			ss.setVersion(rs.getInt("Version"));
			ss.setLastMntBy(rs.getLong("LastMntBy"));
			ss.setLastMntOn(rs.getTimestamp("LastMntOn"));
			ss.setRecordStatus(rs.getString("RecordStatus"));
			ss.setRoleCode(rs.getString("RoleCode"));
			ss.setNextRoleCode(rs.getString("NextRoleCode"));
			ss.setTaskId(rs.getString("TaskId"));
			ss.setNextTaskId(rs.getString("NextTaskId"));
			ss.setRecordType(rs.getString("RecordType"));
			ss.setWorkflowId(rs.getLong("WorkflowId"));

			return ss;
		}, id);
	}

	@Override
	public void delete(SettlementSchedule ss, String type) {
		StringBuilder sql = new StringBuilder("Delete from Settlement_Schedule");
		sql.append(StringUtils.trimToEmpty(type));
		sql.append(" Where HeaderID = ? and ID = ?");

		logger.debug(Literal.SQL.concat(sql.toString()));

		try {
			int recordCount = jdbcOperations.update(sql.toString(), ss.getSettlementHeaderID(),
					ss.getSettlementDetailID());

			if (recordCount == 0) {
				throw new ConcurrencyException();
			}

		} catch (DataAccessException e) {
			throw new DependencyFoundException(e);
		}
	}
}
