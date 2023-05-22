package com.pennanttech.pff.overdraft.dao.impl;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.lang.StringUtils;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;

import com.pennanttech.pennapps.core.ConcurrencyException;
import com.pennanttech.pennapps.core.jdbc.JdbcUtil;
import com.pennanttech.pennapps.core.jdbc.SequenceDao;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pennapps.core.resource.Message;
import com.pennanttech.pff.core.TableType;
import com.pennanttech.pff.overdraft.OverdraftConstants;
import com.pennanttech.pff.overdraft.dao.OverdraftLimitDAO;
import com.pennanttech.pff.overdraft.model.OverdraftLimit;
import com.pennanttech.pff.overdraft.model.OverdraftLimitTransation;

public class OverdraftLimitDAOImpl extends SequenceDao<OverdraftLimit> implements OverdraftLimitDAO {

	@Override
	public long createLimit(OverdraftLimit odl, TableType tableType) {
		StringBuilder sql = new StringBuilder("Insert Into Overdraft_Loan_Limits");
		sql.append(tableType.getSuffix());
		sql.append(" (Id, FinId, FinReference, ActualLimit, MonthlyLimit");
		sql.append(", ActualLimitBal, MonthlyLimitBal, BlockLimit, BlockType");
		sql.append(", Version, CreatedBy, CreatedOn, LastMntBy, LastMntOn, RecordStatus, RoleCode, NextRoleCode");
		sql.append(", TaskId, NextTaskId, RecordType, WorkflowId)");
		sql.append(" Values(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)");

		if (odl.getId() == 0 || odl.getId() <= Long.MIN_VALUE) {
			odl.setId(getNextValue("SEQ_OVERDRAFT_LOAN_LIMITS"));
		}

		logger.debug(Literal.SQL + sql.toString());

		jdbcOperations.update(sql.toString(), ps -> {
			int index = 1;
			ps.setLong(index++, odl.getId());
			ps.setLong(index++, odl.getFinID());
			ps.setString(index++, odl.getFinReference());
			ps.setBigDecimal(index++, odl.getActualLimit());
			ps.setBigDecimal(index++, odl.getMonthlyLimit());
			ps.setBigDecimal(index++, odl.getActualLimitBal());
			ps.setBigDecimal(index++, odl.getMonthlyLimitBal());
			ps.setBoolean(index++, odl.isBlockLimit());
			ps.setString(index++, odl.getBlockType());
			ps.setInt(index++, odl.getVersion());
			ps.setLong(index++, odl.getCreatedBy());
			ps.setTimestamp(index++, odl.getCreatedOn());
			ps.setLong(index++, odl.getLastMntBy());
			ps.setTimestamp(index++, odl.getLastMntOn());
			ps.setString(index++, odl.getRecordStatus());
			ps.setString(index++, odl.getRoleCode());
			ps.setString(index++, odl.getNextRoleCode());
			ps.setString(index++, odl.getTaskId());
			ps.setString(index++, odl.getNextTaskId());
			ps.setString(index++, odl.getRecordType());
			ps.setLong(index, odl.getWorkflowId());
		});

		return odl.getId();
	}

	@Override
	public void logLimt(long limitID) {
		StringBuilder sql = new StringBuilder("Insert into Overdraft_Loan_Limit_Log (");
		sql.append(" LimitId, FinId, FinReference, ActualLimit, MonthlyLimit");
		sql.append(", ActualLimitBal, MonthlyLimitBal, BlockLimit, BlockType");
		sql.append(", Version, CreatedBy, CreatedOn, LastMntBy, LastMntOn, RecordStatus, RoleCode, NextRoleCode");
		sql.append(", TaskId, NextTaskId, RecordType, WorkflowId");
		sql.append(")");
		sql.append(" Select Id, FinId, FinReference, ActualLimit, MonthlyLimit");
		sql.append(", ActualLimitBal, MonthlyLimitBal, BlockLimit, BlockType");
		sql.append(", Version, CreatedBy, CreatedOn, LastMntBy, LastMntOn, RecordStatus, RoleCode, NextRoleCode");
		sql.append(", TaskId, NextTaskId, RecordType, WorkflowId");
		sql.append(" From Overdraft_Loan_Limits Where ID = ?");

		logger.debug(Literal.SQL + sql.toString());

		this.jdbcOperations.update(sql.toString(), ps -> ps.setLong(1, limitID));
	}

	@Override
	public void updateBalances(OverdraftLimit odlh) {
		String sql = "Update Overdraft_Loan_Limits  Set ActualLimitBal = ?, MonthlyLimitBal = ?, Version = ? Where Id = ? and LastMntOn = ?";

		logger.debug(Literal.SQL + sql);

		int count = this.jdbcOperations.update(sql.toString(), ps -> {
			int index = 1;

			ps.setBigDecimal(index++, odlh.getActualLimitBal());
			ps.setBigDecimal(index++, odlh.getMonthlyLimitBal());
			ps.setInt(index++, odlh.getVersion() + 1);

			ps.setLong(index++, odlh.getId());
			ps.setTimestamp(index, odlh.getPrevMntOn());
		});

		if (count == 0) {
			throw new ConcurrencyException();
		}
	}

	@Override
	public void updateLimit(OverdraftLimit odlh, TableType tableType) {
		StringBuilder sql = new StringBuilder("Update Overdraft_Loan_Limits");
		sql.append(tableType.getSuffix());
		sql.append(" Set BlockLimit = ?, BlockType = ?");
		sql.append(", Version = ?, LastMntBy = ?, LastMntOn = ?, RecordStatus = ?, RoleCode = ?");
		sql.append(", NextRoleCode = ?, TaskId = ?, NextTaskId = ?, RecordType = ?, WorkflowId = ?");
		sql.append(" Where Id = ?");

		logger.debug(Literal.SQL + sql.toString());

		this.jdbcOperations.update(sql.toString(), ps -> {
			int index = 1;

			ps.setBoolean(index++, odlh.isBlockLimit());
			ps.setString(index++, odlh.getBlockType());
			ps.setInt(index++, odlh.getVersion());
			ps.setLong(index++, odlh.getLastMntBy());
			ps.setTimestamp(index++, odlh.getLastMntOn());
			ps.setString(index++, odlh.getRecordStatus());
			ps.setString(index++, odlh.getRoleCode());
			ps.setString(index++, odlh.getNextRoleCode());
			ps.setString(index++, odlh.getTaskId());
			ps.setString(index++, odlh.getNextTaskId());
			ps.setString(index++, odlh.getRecordType());
			ps.setLong(index++, odlh.getWorkflowId());

			ps.setLong(index, odlh.getId());
		});
	}

	@Override
	public void deleteLimit(long id, TableType tableType) {
		StringBuilder sql = new StringBuilder("Delete");
		sql.append(" From Overdraft_Loan_Limits");
		sql.append(tableType.getSuffix());
		sql.append(" Where Id = ?");

		logger.debug(Literal.SQL + sql.toString());

		jdbcOperations.update(sql.toString(), id);
	}

	@Override
	public OverdraftLimit getLimit(long finID) {
		String sql = "Select Id, FinId, FinReference, ActualLimit, MonthlyLimit, ActualLimitBal, MonthlyLimitBal, BlockLimit, BlockType, LastMntOn, CreatedOn From Overdraft_Loan_Limits Where FinID = ?";

		logger.debug(Literal.SQL + sql);

		try {
			return jdbcOperations.queryForObject(sql, (rs, rowNum) -> {
				OverdraftLimit limit = new OverdraftLimit();

				limit.setId(rs.getLong("Id"));
				limit.setFinID(rs.getLong("FinId"));
				limit.setFinReference(rs.getString("FinReference"));
				limit.setActualLimit(rs.getBigDecimal("ActualLimit"));
				limit.setMonthlyLimit(rs.getBigDecimal("MonthlyLimit"));
				limit.setActualLimitBal(rs.getBigDecimal("ActualLimitBal"));
				limit.setMonthlyLimitBal(rs.getBigDecimal("MonthlyLimitBal"));
				limit.setBlockLimit(rs.getBoolean("BlockLimit"));
				limit.setBlockType(rs.getString("BlockType"));
				limit.setLastMntOn(rs.getTimestamp("LastMntOn"));
				limit.setCreatedOn(rs.getTimestamp("CreatedOn"));
				limit.setPrevMntOn(limit.getLastMntOn());

				return limit;
			}, finID);
		} catch (EmptyResultDataAccessException e) {
			logger.warn(Message.NO_RECORD_FOUND);
		}

		return null;

	}

	@Override
	public long createTransaction(OverdraftLimitTransation txn) {
		List<OverdraftLimitTransation> transactions = new ArrayList<>();
		transactions.add(txn);

		createTransactions(transactions);

		return txn.getId();
	}

	@Override
	public void createTransactions(List<OverdraftLimitTransation> transactions) {
		StringBuilder sql = new StringBuilder("Insert Into Overdraft_Loan_Transactions");
		sql.append(" (LimitID, ActualLimit, MonthlyLimit, ActualLimitBal, MonthlyLimitBal");
		sql.append(", TxnType, TxnAmount, TxnCharge, TxnDate, ValueDate, Narration");
		sql.append(", Narration1, Narration2, Narration3)");
		sql.append(" Values (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)");

		logger.debug(Literal.SQL + sql.toString());

		jdbcOperations.batchUpdate(sql.toString(), new BatchPreparedStatementSetter() {

			@Override
			public void setValues(PreparedStatement ps, int i) throws SQLException {
				OverdraftLimitTransation odld = transactions.get(i);
				int index = 1;

				ps.setLong(index++, odld.getLimitID());
				ps.setBigDecimal(index++, odld.getActualLimit());
				ps.setBigDecimal(index++, odld.getMonthlyLimit());
				ps.setBigDecimal(index++, odld.getActualLimitBal());
				ps.setBigDecimal(index++, odld.getMonthlyLimitBal());
				ps.setString(index++, odld.getTxnType());
				ps.setBigDecimal(index++, odld.getTxnAmount());
				ps.setBigDecimal(index++, odld.getTxnCharge());
				ps.setTimestamp(index++, new Timestamp(System.currentTimeMillis()));
				ps.setDate(index++, JdbcUtil.getDate(odld.getValueDate()));
				ps.setString(index++, odld.getNarration());
				ps.setString(index++, odld.getNarration1());
				ps.setString(index++, odld.getNarration2());
				ps.setString(index, odld.getNarration3());
			}

			@Override
			public int getBatchSize() {
				return transactions.size();
			}
		});
	}

	@Override
	public void unBlockLimit(long finID) {
		String sql = "Update Overdraft_Loan_Limits set BlockLimit = ?, BlockType = ? Where FinID = ?";

		logger.debug(Literal.SQL + sql);

		this.jdbcOperations.update(sql, ps -> {
			int index = 1;

			ps.setBoolean(index++, false);
			ps.setString(index++, OverdraftConstants.AUTO_BLOCK_STATUS);

			ps.setLong(index, finID);
		});

	}

	@Override
	public void unBlockLimit(OverdraftLimit limit) {
		List<OverdraftLimit> limits = new ArrayList<>();
		limits.add(limit);

		blockLimit(limits, false);
	}

	@Override
	public boolean isAutoBlock(long finID) {
		String sql = "Select count(Id) From Overdraft_Loan_Limits Where FinID = ? and BlockType = ?";

		logger.debug(Literal.SQL + sql);

		return this.jdbcOperations.queryForObject(sql, Integer.class, finID, OverdraftConstants.AUTO_BLOCK_STATUS) > 0;
	}

	@Override
	public void blockLimit(List<OverdraftLimit> limits) {
		blockLimit(limits, true);
	}

	private void blockLimit(List<OverdraftLimit> limits, boolean blockLimit) {
		String sql = "Update Overdraft_Loan_Limits set BlockLimit = ?, BlockType = ? Where Id = ? and LastMntOn = ?";

		logger.debug(Literal.SQL + sql);

		this.jdbcOperations.batchUpdate(sql, new BatchPreparedStatementSetter() {

			@Override
			public void setValues(PreparedStatement ps, int i) throws SQLException {
				OverdraftLimit limit = limits.get(i);
				int index = 1;

				ps.setBoolean(index++, blockLimit);
				ps.setString(index++, OverdraftConstants.AUTO_BLOCK_STATUS);

				ps.setLong(index++, limit.getId());
				ps.setTimestamp(index, limit.getPrevMntOn());
			}

			@Override
			public int getBatchSize() {
				return limits.size();
			}
		});
	}

	@Override
	public boolean isLimitBlock(long finID) {
		String sql = "Select BlockLimit From OVERDRAFT_LOAN_LIMITS Where FinID = ?";

		logger.debug(Literal.SQL + sql);

		try {
			return this.jdbcOperations.queryForObject(sql, Boolean.class, finID);
		} catch (EmptyResultDataAccessException e) {
			logger.warn(Message.NO_RECORD_FOUND);
		}

		return false;
	}

	@Override
	public List<OverdraftLimitTransation> getTransactions(long finID) {
		StringBuilder sql = new StringBuilder("Select");
		sql.append(" olt.ID, olt.LimitID, oll.FinID, oll.FinReference, olt.ActualLimit");
		sql.append(", olt.MonthlyLimit, olt.TxnAmount, olt.Narration, olt.TxnType");
		sql.append(", olt.ActualLimitBal, olt.MonthlyLimitBal, olt.TxnCharge, olt.TxnDate, olt.ValueDate");
		sql.append(" From OverDraft_Loan_Transactions olt");
		sql.append(" Inner Join OverDraft_Loan_Limits oll on oll.ID = olt.LimitID");
		sql.append(" Where oll.FinID = ?");

		logger.debug(Literal.SQL + sql.toString());

		List<OverdraftLimitTransation> list = this.jdbcOperations.query(sql.toString(), ps -> ps.setLong(1, finID),
				(rs, rowNum) -> {
					OverdraftLimitTransation odld = new OverdraftLimitTransation();

					odld.setId(rs.getLong("ID"));
					odld.setLimitID(rs.getLong("LimitID"));
					odld.setFinID(rs.getLong("FinID"));
					odld.setFinReference(rs.getString("FinReference"));
					odld.setActualLimit(rs.getBigDecimal("ActualLimit"));
					odld.setMonthlyLimit(rs.getBigDecimal("MonthlyLimit"));
					odld.setTxnAmount(rs.getBigDecimal("TxnAmount"));
					odld.setNarration(rs.getString("Narration"));
					odld.setTxnType(rs.getString("TxnType"));
					odld.setActualLimitBal(rs.getBigDecimal("ActualLimitBal"));
					odld.setMonthlyLimitBal(rs.getBigDecimal("MonthlyLimitBal"));
					odld.setTxnCharge(rs.getBigDecimal("TxnCharge"));
					odld.setTxnDate(JdbcUtil.getDate(rs.getDate("TxnDate")));
					odld.setValueDate(JdbcUtil.getDate(rs.getDate("ValueDate")));

					return odld;
				});

		return list.stream().sorted((l1, l2) -> Long.compare(l1.getId(), l2.getId())).collect(Collectors.toList());
	}

	@Override
	public OverdraftLimit getLimit(long finID, String type) {
		StringBuilder sql = new StringBuilder("Select");
		sql.append(" Id, FinId, FinReference, ActualLimit, MonthlyLimit, ActualLimitBal");
		sql.append(", MonthlyLimitBal, BlockLimit, BlockType, Version, LastMntBy");
		sql.append(", LastMntOn, RecordStatus, RoleCode, NextRoleCode, TaskId, NextTaskId");
		sql.append(", RecordType, WorkflowId, CreatedOn");

		if (StringUtils.trimToEmpty(type).contains("View")) {
			sql.append(", CustCif");
		}

		sql.append(" From Overdraft_Loan_Limits");
		sql.append(type);
		sql.append(" Where FinID = ?");

		logger.debug(Literal.SQL + sql.toString());

		try {
			return jdbcOperations.queryForObject(sql.toString(), (rs, rowNum) -> {
				OverdraftLimit limit = new OverdraftLimit();

				limit.setId(rs.getLong("Id"));
				limit.setFinID(rs.getLong("FinId"));
				limit.setFinReference(rs.getString("FinReference"));
				limit.setActualLimit(rs.getBigDecimal("ActualLimit"));
				limit.setMonthlyLimit(rs.getBigDecimal("MonthlyLimit"));
				limit.setActualLimitBal(rs.getBigDecimal("ActualLimitBal"));
				limit.setMonthlyLimitBal(rs.getBigDecimal("MonthlyLimitBal"));
				limit.setBlockLimit(rs.getBoolean("BlockLimit"));
				limit.setBlockType(rs.getString("BlockType"));
				limit.setVersion(rs.getInt("Version"));
				limit.setLastMntBy(rs.getLong("LastMntBy"));
				limit.setLastMntOn(rs.getTimestamp("LastMntOn"));
				limit.setRecordStatus(rs.getString("RecordStatus"));
				limit.setRoleCode(rs.getString("RoleCode"));
				limit.setNextRoleCode(rs.getString("NextRoleCode"));
				limit.setTaskId(rs.getString("TaskId"));
				limit.setNextTaskId(rs.getString("NextTaskId"));
				limit.setRecordType(rs.getString("RecordType"));
				limit.setWorkflowId(rs.getLong("WorkflowId"));
				limit.setCreatedOn(rs.getTimestamp("CreatedOn"));

				if (StringUtils.trimToEmpty(type).contains("View")) {
					limit.setCustCIF(rs.getString("CustCif"));
				}

				return limit;
			}, finID);
		} catch (EmptyResultDataAccessException e) {
			logger.warn(Message.NO_RECORD_FOUND);
		}

		return null;
	}
}
