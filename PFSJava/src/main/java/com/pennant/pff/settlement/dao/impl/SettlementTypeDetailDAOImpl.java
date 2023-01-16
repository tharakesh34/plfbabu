package com.pennant.pff.settlement.dao.impl;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.apache.commons.lang.StringUtils;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.RowMapper;

import com.pennant.pff.settlement.dao.SettlementTypeDetailDAO;
import com.pennant.pff.settlement.model.SettlementTypeDetail;
import com.pennanttech.pennapps.core.ConcurrencyException;
import com.pennanttech.pennapps.core.DependencyFoundException;
import com.pennanttech.pennapps.core.jdbc.SequenceDao;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pennapps.core.resource.Message;
import com.pennanttech.pff.core.TableType;
import com.pennanttech.pff.core.util.QueryUtil;

public class SettlementTypeDetailDAOImpl extends SequenceDao<SettlementTypeDetail> implements SettlementTypeDetailDAO {
	public SettlementTypeDetailDAOImpl() {
		super();
	}

	@Override
	public long save(SettlementTypeDetail std, TableType tableType) {
		if (std.getId() == 0 || std.getId() == Long.MIN_VALUE) {
			std.setId(getNextValue("SeqSettlementTypes"));
		}

		StringBuilder sql = new StringBuilder("Insert Into Settlement_Types");
		sql.append(tableType.getSuffix());
		sql.append(" (Id, SettlementCode, SettlementDesc, AlwGracePeriod, Active");
		sql.append(", Version, LastMntBy, LastMntOn, RecordStatus, RoleCode, NextRoleCode");
		sql.append(", TaskId, NextTaskId, RecordType, WorkflowId)");
		sql.append(" Values(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)");

		logger.debug(Literal.SQL.concat(sql.toString()));

		try {
			this.jdbcOperations.update(sql.toString(), ps -> {
				int index = 0;

				ps.setLong(++index, std.getId());
				ps.setString(++index, std.getSettlementCode());
				ps.setString(++index, std.getSettlementDesc());
				ps.setBoolean(++index, std.isAlwGracePeriod());
				ps.setBoolean(++index, std.isActive());
				ps.setInt(++index, std.getVersion());
				ps.setLong(++index, std.getLastMntBy());
				ps.setTimestamp(++index, std.getLastMntOn());
				ps.setString(++index, std.getRecordStatus());
				ps.setString(++index, std.getRoleCode());
				ps.setString(++index, std.getNextRoleCode());
				ps.setString(++index, std.getTaskId());
				ps.setString(++index, std.getNextTaskId());
				ps.setString(++index, std.getRecordType());
				ps.setLong(++index, std.getWorkflowId());
			});
		} catch (DuplicateKeyException e) {
			throw new ConcurrencyException(e);
		}

		return std.getId();
	}

	@Override
	public void update(SettlementTypeDetail std, TableType tableType) {
		StringBuilder sql = new StringBuilder("Update Settlement_Types");
		sql.append(tableType.getSuffix());
		sql.append(" set SettlementDesc = ?, AlwGracePeriod = ?, Active = ?");
		sql.append(", Version = ?, LastMntBy = ?, LastMntOn = ?");
		sql.append(", RecordStatus= ?, RoleCode = ?, NextRoleCode = ?, TaskId = ?");
		sql.append(", NextTaskId = ?, RecordType = ?, WorkflowId = ?");
		sql.append(" Where ID = ? and SettlementCode = ?");

		logger.debug(Literal.SQL.concat(sql.toString()));

		int recordCount = this.jdbcOperations.update(sql.toString(), ps -> {
			int index = 0;

			ps.setString(++index, std.getSettlementDesc());
			ps.setBoolean(++index, std.isAlwGracePeriod());
			ps.setBoolean(++index, std.isActive());
			ps.setInt(++index, std.getVersion());
			ps.setLong(++index, std.getLastMntBy());
			ps.setTimestamp(++index, std.getLastMntOn());
			ps.setString(++index, std.getRecordStatus());
			ps.setString(++index, std.getRoleCode());
			ps.setString(++index, std.getNextRoleCode());
			ps.setString(++index, std.getTaskId());
			ps.setString(++index, std.getNextTaskId());
			ps.setString(++index, std.getRecordType());
			ps.setLong(++index, std.getWorkflowId());

			ps.setLong(++index, std.getId());
			ps.setString(++index, std.getSettlementCode());
		});

		if (recordCount == 0) {
			throw new ConcurrencyException();
		}
	}

	@Override
	public void delete(SettlementTypeDetail std, TableType tableType) {
		StringBuilder sql = new StringBuilder("Delete from Settlement_Types");
		sql.append(tableType.getSuffix());
		sql.append(" Where SettlementCode = ?");

		logger.debug(Literal.SQL.concat(sql.toString()));

		try {
			if (jdbcOperations.update(sql.toString(), std.getSettlementCode()) == 0) {
				throw new ConcurrencyException();
			}
		} catch (DataAccessException e) {
			throw new DependencyFoundException(e);
		}
	}

	@Override
	public SettlementTypeDetail getSettlementByCode(String code, String type) {
		StringBuilder sql = getSqlQuery(type);
		sql.append(" Where SettlementCode = ?");

		logger.debug(Literal.SQL.concat(sql.toString()));

		try {
			return this.jdbcOperations.queryForObject(sql.toString(), new SettlementTypeDetailRM(), code);
		} catch (EmptyResultDataAccessException e) {
			logger.warn(Message.NO_RECORD_FOUND);
			return null;
		}
	}

	@Override
	public SettlementTypeDetail getSettlementById(long id, String type) {
		StringBuilder sql = getSqlQuery(type);
		sql.append(" Where ID = ?");

		logger.debug(Literal.SQL.concat(sql.toString()));

		try {
			return this.jdbcOperations.queryForObject(sql.toString(), new SettlementTypeDetailRM(), id);
		} catch (EmptyResultDataAccessException e) {
			logger.warn(Message.NO_RECORD_FOUND);
			return null;
		}
	}

	@Override
	public boolean isDuplicateKey(String settlementCode, long id, TableType tableType) {
		String sql;
		String whereClause = "SettlementCode = ? and ID != ?";

		Object[] obj = new Object[] { settlementCode, id };

		switch (tableType) {
		case MAIN_TAB:
			sql = QueryUtil.getCountQuery("Settlement_Types", whereClause);
			break;
		case TEMP_TAB:
			sql = QueryUtil.getCountQuery("Settlement_Types_Temp", whereClause);
			break;
		default:
			sql = QueryUtil.getCountQuery(new String[] { "Settlement_Types_Temp", "Settlement_Types" }, whereClause);

			obj = new Object[] { settlementCode, id, settlementCode, id };
			break;
		}

		logger.debug(Literal.SQL.concat(sql));
		return jdbcOperations.queryForObject(sql, Integer.class, obj) > 0;
	}

	private StringBuilder getSqlQuery(String type) {
		StringBuilder sql = new StringBuilder("Select");
		sql.append(" ID, SettlementCode, SettlementDesc, AlwGracePeriod, Active");
		sql.append(", Version, LastMntOn, LastMntBy,RecordStatus, RoleCode");
		sql.append(", NextRoleCode, TaskId, NextTaskId, RecordType, WorkflowId");
		sql.append(" From Settlement_Types");
		sql.append(StringUtils.trimToEmpty(type));
		return sql;
	}

	private class SettlementTypeDetailRM implements RowMapper<SettlementTypeDetail> {

		private SettlementTypeDetailRM() {
			super();
		}

		@Override
		public SettlementTypeDetail mapRow(ResultSet rs, int rowNum) throws SQLException {
			SettlementTypeDetail std = new SettlementTypeDetail();

			std.setId(rs.getLong("ID"));
			std.setSettlementCode(rs.getString("SettlementCode"));
			std.setSettlementDesc(rs.getString("SettlementDesc"));
			std.setAlwGracePeriod(rs.getBoolean("AlwGracePeriod"));
			std.setActive(rs.getBoolean("Active"));
			std.setVersion(rs.getInt("Version"));
			std.setLastMntBy(rs.getLong("LastMntBy"));
			std.setLastMntOn(rs.getTimestamp("LastMntOn"));
			std.setRecordStatus(rs.getString("RecordStatus"));
			std.setRoleCode(rs.getString("RoleCode"));
			std.setNextRoleCode(rs.getString("NextRoleCode"));
			std.setTaskId(rs.getString("TaskId"));
			std.setNextTaskId(rs.getString("NextTaskId"));
			std.setRecordType(rs.getString("RecordType"));
			std.setWorkflowId(rs.getLong("WorkflowId"));

			return std;
		}

	}
}
