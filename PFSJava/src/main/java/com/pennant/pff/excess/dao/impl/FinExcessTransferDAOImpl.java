package com.pennant.pff.excess.dao.impl;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.RowMapper;

import com.pennant.pff.excess.dao.FinExcessTransferDAO;
import com.pennant.pff.excess.model.FinExcessTransfer;
import com.pennanttech.pennapps.core.ConcurrencyException;
import com.pennanttech.pennapps.core.DependencyFoundException;
import com.pennanttech.pennapps.core.jdbc.JdbcUtil;
import com.pennanttech.pennapps.core.jdbc.SequenceDao;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pennapps.core.resource.Message;
import com.pennanttech.pff.core.TableType;

public class FinExcessTransferDAOImpl extends SequenceDao<FinExcessTransfer> implements FinExcessTransferDAO {
	private static Logger logger = LogManager.getLogger(FinExcessTransferDAOImpl.class);

	public FinExcessTransferDAOImpl() {
		super();
	}

	@Override
	public FinExcessTransfer getExcessTransferByFinId(long finId, long trnasferId, String type) {
		StringBuilder sql = getSqlQuery(type);
		sql.append(" Where Id = ? and FinId = ?");

		logger.debug(Literal.SQL.concat(sql.toString()));

		try {
			return this.jdbcOperations.queryForObject(sql.toString(), new FinExcessTransferRM(type), trnasferId, finId);
		} catch (EmptyResultDataAccessException e) {
			logger.warn(Message.NO_RECORD_FOUND);
			return null;
		}
	}

	@Override
	public void delete(FinExcessTransfer finExcessTransfer, TableType type) {
		StringBuilder sql = new StringBuilder();
		sql.append(" Delete From Excess_Transfer_Details");
		sql.append(type.getSuffix());
		sql.append(" Where Id = ?");

		logger.debug(Literal.SQL.concat(sql.toString()));

		try {
			this.jdbcOperations.update(sql.toString(), finExcessTransfer.getId());
		} catch (DataAccessException e) {
			throw new DependencyFoundException(e);
		}
	}

	@Override
	public String save(FinExcessTransfer fet, TableType tableType) {
		if (fet.getId() <= 0) {
			fet.setId(getNextValue("SeqExcess_Transfer_Details"));
		}

		StringBuilder sql = new StringBuilder("Insert into Excess_Transfer_Details");
		sql.append(tableType.getSuffix());
		sql.append(" (Id, FinId, FinReference, TransferDate, TransferToType, TransferToId, TransferFromType");
		sql.append(", TransferFromId, LinkedTranId, Status, Version, CreatedBy, CreatedOn");
		sql.append(", LastMntBy, LastMntOn, ApprovedBy, ApprovedOn, RecordStatus, RoleCode");
		sql.append(", NextRoleCode, TaskId, NextTaskId, RecordType, WorkflowId, TransferAmount)");
		sql.append(" Values(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)");

		logger.debug(Literal.SQL.concat(sql.toString()));

		try {
			jdbcOperations.update(sql.toString(), ps -> {
				int index = 0;

				ps.setLong(++index, fet.getId());
				ps.setLong(++index, fet.getFinId());
				ps.setString(++index, fet.getFinReference());
				ps.setDate(++index, JdbcUtil.getDate(fet.getTransferDate()));
				ps.setString(++index, fet.getTransferToType());
				ps.setLong(++index, fet.getTransferToId());
				ps.setString(++index, fet.getTransferFromType());
				ps.setLong(++index, fet.getTransferFromId());
				ps.setLong(++index, fet.getLinkedTranId());
				ps.setString(++index, fet.getStatus());
				ps.setInt(index++, fet.getVersion());
				ps.setLong(index++, fet.getCreatedBy());
				ps.setTimestamp(index++, fet.getCreatedOn());
				ps.setLong(index++, fet.getLastMntBy());
				ps.setTimestamp(index++, fet.getLastMntOn());
				ps.setLong(index++, fet.getApprovedBy());
				ps.setTimestamp(index++, fet.getApprovedOn());
				ps.setString(index++, fet.getRecordStatus());
				ps.setString(index++, fet.getRoleCode());
				ps.setString(index++, fet.getNextRoleCode());
				ps.setString(index++, fet.getTaskId());
				ps.setString(index++, fet.getNextTaskId());
				ps.setString(index++, fet.getRecordType());
				ps.setLong(index, fet.getWorkflowId());
				ps.setBigDecimal(++index, fet.getTransferAmount());
			});
		} catch (DuplicateKeyException e) {
			throw new ConcurrencyException(e);
		}

		return String.valueOf(fet.getId());
	}

	@Override
	public void update(FinExcessTransfer fet, TableType tableType) {
		StringBuilder sql = new StringBuilder("update Excess_Transfer_Details");
		sql.append(tableType.getSuffix());
		sql.append(" Set TransferDate = ?,");
		sql.append(" TransferToType = ?, TransferToId = ?, TransferFromType = ?, TransferFromId = ?");
		sql.append(", LinkedTranId = ?, Status = ?, Version = ?, LastMntBy = ?, LastMntOn = ?");
		sql.append(", ApprovedBy = ?, ApprovedOn = ?, RecordStatus = ?, RoleCode = ?, NextRoleCode = ?");
		sql.append(", TaskId = ?, NextTaskId = ?, RecordType = ?, WorkflowId = :WorkflowId, TransferAmount = ?");
		sql.append(" Where ID = ?");

		logger.debug(Literal.SQL.concat(sql.toString()));

		int recordCount = jdbcOperations.update(sql.toString(), ps -> {
			int index = 0;

			ps.setDate(++index, JdbcUtil.getDate(fet.getTransferDate()));
			ps.setString(++index, fet.getTransferToType());
			ps.setLong(++index, fet.getTransferToId());
			ps.setString(++index, fet.getTransferFromType());
			ps.setLong(++index, fet.getTransferFromId());
			ps.setLong(++index, fet.getLinkedTranId());
			ps.setString(++index, fet.getStatus());
			ps.setInt(index++, fet.getVersion());
			ps.setLong(index++, fet.getLastMntBy());
			ps.setTimestamp(index++, fet.getLastMntOn());
			ps.setLong(index++, fet.getApprovedBy());
			ps.setTimestamp(index++, fet.getApprovedOn());
			ps.setString(index++, fet.getRecordStatus());
			ps.setString(index++, fet.getRoleCode());
			ps.setString(index++, fet.getNextRoleCode());
			ps.setString(index++, fet.getTaskId());
			ps.setString(index++, fet.getNextTaskId());
			ps.setString(index++, fet.getRecordType());
			ps.setLong(index, fet.getWorkflowId());
			ps.setBigDecimal(++index, fet.getTransferAmount());

			ps.setLong(++index, fet.getId());
		});

		if (recordCount == 0) {
			throw new ConcurrencyException();
		}
	}

	@Override
	public FinExcessTransfer getExcessTransferByTransferId(long transferId, String type) {
		StringBuilder sql = getSqlQuery(type);
		sql.append(" Where Id = ?");

		logger.debug(Literal.SQL.concat(sql.toString()));

		try {
			return this.jdbcOperations.queryForObject(sql.toString(), new FinExcessTransferRM(type), transferId);
		} catch (EmptyResultDataAccessException e) {
			logger.warn(Message.NO_RECORD_FOUND);
			return null;
		}
	}

	@Override
	public boolean isIdExists(long transferId) {
		String sql = "Select count(Id) from Excess_Transfer_Details Where Id = ?";

		logger.debug(Literal.SQL.concat(sql));

		return this.jdbcOperations.queryForObject(sql, Integer.class, transferId) > 0;
	}

	@Override
	public boolean isFinReceferenceExist(String finReference, String type) {
		StringBuilder sql = new StringBuilder();
		sql.append(" Select count(ID) from Excess_Transfer_Details");
		sql.append(StringUtils.trimToEmpty(type));
		sql.append(" Where FinReference = ?");

		logger.debug(Literal.SQL.concat(sql.toString()));

		return this.jdbcOperations.queryForObject(sql.toString(), Integer.class, finReference) > 0;
	}

	private StringBuilder getSqlQuery(String type) {
		StringBuilder sql = new StringBuilder("Select");
		sql.append(" Id, FinId, FinReference, TransferDate, TransferToType, TransferToId, TransferFromType");
		sql.append(", TransferFromId, LinkedTranId, Status");
		sql.append(", Version, CreatedBy, CreatedOn, LastMntBy, LastMntOn, ApprovedBy, ApprovedOn, RecordStatus");
		sql.append(", RoleCode, NextRoleCode, TaskId, NextTaskId, RecordType, WorkflowId");
		sql.append(", TransferAmount");

		if (StringUtils.trimToEmpty(type).contains("View")) {
			sql.append(", CustCIF");
		}

		sql.append(" From Excess_Transfer_Details");
		sql.append(StringUtils.trimToEmpty(type));

		return sql;
	}

	private class FinExcessTransferRM implements RowMapper<FinExcessTransfer> {
		private String type;

		private FinExcessTransferRM(String type) {
			this.type = type;
		}

		@Override
		public FinExcessTransfer mapRow(ResultSet rs, int rowNum) throws SQLException {
			FinExcessTransfer fet = new FinExcessTransfer();

			fet.setId(rs.getLong("Id"));
			fet.setFinId(rs.getLong("FinId"));
			fet.setFinReference(rs.getString("FinReference"));
			fet.setTransferDate(JdbcUtil.getDate(rs.getDate("TransferDate")));
			fet.setTransferToType(rs.getString("TransferToType"));
			fet.setTransferToId(rs.getLong("TransferToId"));
			fet.setTransferFromType(rs.getString("TransferFromType"));
			fet.setTransferFromId(rs.getLong("TransferFromId"));
			fet.setLinkedTranId(rs.getLong("LinkedTranId"));
			fet.setStatus(rs.getString("Status"));
			fet.setVersion(rs.getInt("Version"));
			fet.setCreatedBy(rs.getLong("CreatedBy"));
			fet.setCreatedOn(rs.getTimestamp("CreatedOn"));
			fet.setLastMntBy(rs.getLong("LastMntBy"));
			fet.setLastMntOn(rs.getTimestamp("LastMntOn"));
			fet.setApprovedBy(rs.getLong("ApprovedBy"));
			fet.setApprovedOn(rs.getTimestamp("ApprovedOn"));
			fet.setRecordStatus(rs.getString("RecordStatus"));
			fet.setRoleCode(rs.getString("RoleCode"));
			fet.setNextRoleCode(rs.getString("NextRoleCode"));
			fet.setTaskId(rs.getString("TaskId"));
			fet.setNextTaskId(rs.getString("NextTaskId"));
			fet.setRecordType(rs.getString("RecordType"));
			fet.setWorkflowId(rs.getLong("WorkflowId"));
			fet.setTransferAmount(rs.getBigDecimal("TransferAmount"));

			if (StringUtils.trimToEmpty(type).contains("View")) {
				fet.setCustCIF(rs.getString("CustCIF"));
			}

			return fet;
		}

	}

}
