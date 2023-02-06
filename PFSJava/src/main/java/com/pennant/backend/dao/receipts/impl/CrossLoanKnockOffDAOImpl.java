package com.pennant.backend.dao.receipts.impl;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.apache.commons.lang.StringUtils;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.RowMapper;

import com.pennant.backend.dao.receipts.CrossLoanKnockOffDAO;
import com.pennant.backend.model.finance.CrossLoanKnockOff;
import com.pennant.backend.util.RepayConstants;
import com.pennanttech.pennapps.core.ConcurrencyException;
import com.pennanttech.pennapps.core.DependencyFoundException;
import com.pennanttech.pennapps.core.jdbc.JdbcUtil;
import com.pennanttech.pennapps.core.jdbc.SequenceDao;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pennapps.core.resource.Message;

public class CrossLoanKnockOffDAOImpl extends SequenceDao<CrossLoanKnockOff> implements CrossLoanKnockOffDAO {

	public CrossLoanKnockOffDAOImpl() {
		super();
	}

	@Override
	public long saveCrossLoanHeader(CrossLoanKnockOff cko, String tableType) {
		StringBuilder sql = new StringBuilder("Insert into Cross_Loan_KnockOff");
		sql.append(tableType);
		sql.append(" (Id, TransferID, KnockOffID, ValueDate, PostDate, Version");
		sql.append(", CreatedBy, CreatedOn, ApprovedBy, ApprovedOn, LastMntBy, LastMntOn");
		sql.append(", RecordStatus, RoleCode, NextRoleCode, TaskId, NextTaskId, RecordType, WorkflowId)");
		sql.append(" Values(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)");

		if (cko.getId() <= 0) {
			cko.setId(getNextValue("SeqCross_Loan_KnockOff"));
		}

		logger.debug(Literal.SQL.concat(sql.toString()));

		this.jdbcOperations.update(sql.toString(), ps -> {
			int index = 0;

			ps.setLong(++index, cko.getId());
			ps.setLong(++index, cko.getTransferID());
			ps.setLong(++index, cko.getKnockOffId());
			ps.setDate(++index, JdbcUtil.getDate(cko.getValueDate()));
			ps.setDate(++index, JdbcUtil.getDate(cko.getPostDate()));
			ps.setInt(++index, cko.getVersion());
			ps.setLong(++index, cko.getCreatedBy());
			ps.setTimestamp(++index, cko.getCreatedOn());
			ps.setLong(++index, cko.getApprovedBy());
			ps.setTimestamp(++index, cko.getApprovedOn());
			ps.setLong(++index, cko.getLastMntBy());
			ps.setTimestamp(++index, cko.getLastMntOn());
			ps.setString(++index, cko.getRecordStatus());
			ps.setString(++index, cko.getRoleCode());
			ps.setString(++index, cko.getNextRoleCode());
			ps.setString(++index, cko.getTaskId());
			ps.setString(++index, cko.getNextTaskId());
			ps.setString(++index, cko.getRecordType());
			ps.setLong(++index, cko.getWorkflowId());
		});

		return cko.getId();
	}

	@Override
	public void updateCrossLoanHeader(CrossLoanKnockOff crossLoanHeader, String tableType) {
		StringBuilder sql = new StringBuilder("Update Cross_Loan_KnockOff");
		sql.append(tableType);
		sql.append(" Set KnockOffID = ?, ValueDate = ?, PostDate = ?");
		sql.append(", Version = ?, LastMntBy = ?, LastMntOn = ?");
		sql.append(", RecordStatus= ?, RoleCode = ?, NextRoleCode = ?");
		sql.append(", TaskId = ?, NextTaskId = ?, RecordType = ?, WorkflowId = ?");
		sql.append(" Where  ID = ?");

		logger.debug(Literal.SQL.concat(sql.toString()));

		int recordCount = this.jdbcOperations.update(sql.toString(), ps -> {
			int index = 0;

			ps.setLong(++index, crossLoanHeader.getKnockOffId());
			ps.setDate(++index, JdbcUtil.getDate(crossLoanHeader.getValueDate()));
			ps.setDate(++index, JdbcUtil.getDate(crossLoanHeader.getPostDate()));
			ps.setInt(++index, crossLoanHeader.getVersion());
			ps.setLong(++index, crossLoanHeader.getLastMntBy());
			ps.setTimestamp(++index, crossLoanHeader.getLastMntOn());
			ps.setString(++index, crossLoanHeader.getRecordStatus());
			ps.setString(++index, crossLoanHeader.getRoleCode());
			ps.setString(++index, crossLoanHeader.getNextRoleCode());
			ps.setString(++index, crossLoanHeader.getTaskId());
			ps.setString(++index, crossLoanHeader.getNextTaskId());
			ps.setString(++index, crossLoanHeader.getRecordType());
			ps.setLong(++index, crossLoanHeader.getWorkflowId());

			ps.setLong(++index, crossLoanHeader.getId());
		});

		if (recordCount <= 0) {
			throw new ConcurrencyException();
		}
	}

	@Override
	public void deleteHeader(long id, String tableType) {
		StringBuilder sql = new StringBuilder("Delete from Cross_Loan_KnockOff");
		sql.append(tableType);
		sql.append(" Where ID = ?");

		logger.debug(Literal.SQL.concat(sql.toString()));

		try {
			jdbcOperations.update(sql.toString(), id);
		} catch (DataAccessException e) {
			throw new DependencyFoundException(e);
		}
	}

	@Override
	public CrossLoanKnockOff getCrossLoanHeaderById(long id, String type) {
		StringBuilder sql = new StringBuilder("Select");
		sql.append(" Id, TransferID, KnockOffID, ValueDate, PostDate, Version");
		sql.append(", CreatedBy, CreatedOn, ApprovedBy, ApprovedOn, LastMntBy, LastMntOn");
		sql.append(", RecordStatus, RoleCode, NextRoleCode, TaskId, NextTaskId, RecordType, WorkflowId");
		sql.append(" From Cross_Loan_KnockOff");
		sql.append(StringUtils.trimToEmpty(type));
		sql.append(" Where ID = ?");

		logger.debug(Literal.SQL.concat(sql.toString()));

		try {
			return this.jdbcOperations.queryForObject(sql.toString(), new CrossLoanKnockOffRM(), id);
		} catch (EmptyResultDataAccessException e) {
			logger.warn(Message.NO_RECORD_FOUND);
			return null;
		}
	}

	private class CrossLoanKnockOffRM implements RowMapper<CrossLoanKnockOff> {

		private CrossLoanKnockOffRM() {
			super();
		}

		@Override
		public CrossLoanKnockOff mapRow(ResultSet rs, int rowNum) throws SQLException {
			CrossLoanKnockOff clkh = new CrossLoanKnockOff();

			clkh.setId(rs.getLong("Id"));
			clkh.setTransferID(rs.getLong("TransferID"));
			clkh.setKnockOffId(rs.getLong("KnockOffID"));
			clkh.setValueDate(rs.getDate("Valuedate"));
			clkh.setPostDate(rs.getDate("Postdate"));
			clkh.setVersion(rs.getInt("Version"));
			clkh.setCreatedBy(rs.getLong("CreatedBy"));
			clkh.setCreatedOn(rs.getTimestamp("CreatedOn"));
			clkh.setApprovedBy(rs.getLong("ApprovedBy"));
			clkh.setApprovedOn(rs.getTimestamp("ApprovedOn"));
			clkh.setLastMntBy(rs.getLong("LastMntBy"));
			clkh.setLastMntOn(rs.getTimestamp("LastMntOn"));
			clkh.setRecordStatus(rs.getString("RecordStatus"));
			clkh.setRoleCode(rs.getString("RoleCode"));
			clkh.setNextRoleCode(rs.getString("NextRoleCode"));
			clkh.setTaskId(rs.getString("TaskId"));
			clkh.setNextTaskId(rs.getString("NextTaskId"));
			clkh.setRecordType(rs.getString("RecordType"));
			clkh.setWorkflowId(rs.getLong("WorkflowId"));

			return clkh;
		}
	}

	@Override
	public boolean cancelReferenceID(long receiptID) {
		String sql = "Select Count(ReceiptID) From FinReceiptHeader Where ReceiptModeStatus = ? and ReceiptID = ?";

		logger.debug(Literal.SQL.concat(sql));

		return this.jdbcOperations.queryForObject(sql, Integer.class, RepayConstants.PAYSTATUS_CANCEL, receiptID) > 0;
	}
}