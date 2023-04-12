package com.pennant.backend.dao.receipts.impl;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.RowMapper;

import com.pennant.backend.dao.receipts.CrossLoanTransferDAO;
import com.pennant.backend.model.finance.CrossLoanTransfer;
import com.pennant.backend.model.finance.FinExcessAmount;
import com.pennanttech.pennapps.core.ConcurrencyException;
import com.pennanttech.pennapps.core.DependencyFoundException;
import com.pennanttech.pennapps.core.jdbc.JdbcUtil;
import com.pennanttech.pennapps.core.jdbc.SequenceDao;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pennapps.core.resource.Message;

public class CrossLoanTransferDAOImpl extends SequenceDao<CrossLoanTransfer> implements CrossLoanTransferDAO {

	public CrossLoanTransferDAOImpl() {
		super();
	}

	@Override
	public long save(CrossLoanTransfer clt, String tableType) {
		StringBuilder sql = new StringBuilder("Insert into Cross_Loan_Transfer");
		sql.append(tableType);
		sql.append(" (ID, CustId, ReceiptId, FromFinID, ToFinID, FromFinReference, ToFinReference, TransferAmount");
		sql.append(", ExcessId, ToLinkedTranId, FromLinkedTranId, ExcessAmount, UtiliseAmount, ReserveAmount");
		sql.append(", AvailableAmount, ToExcessId, ExcessType, Source, Version");
		sql.append(", CreatedBy, CreatedOn, ApprovedBy, ApprovedOn, LastMntBy, LastMntOn");
		sql.append(", RecordStatus, RoleCode, NextRoleCode, TaskId, NextTaskId, RecordType, WorkflowId)");
		sql.append(" Values(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?");
		sql.append(", ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)");

		if (clt.getId() <= 0) {
			clt.setId(getNextValue("SeqCross_Loan_Transfer"));
		}

		logger.debug(Literal.SQL.concat(sql.toString()));

		this.jdbcOperations.update(sql.toString(), ps -> {
			int index = 0;

			ps.setLong(++index, clt.getId());
			ps.setLong(++index, clt.getCustId());
			ps.setLong(++index, clt.getReceiptId());
			ps.setLong(++index, clt.getFromFinID());
			ps.setLong(++index, clt.getToFinID());
			ps.setString(++index, clt.getFromFinReference());
			ps.setString(++index, clt.getToFinReference());
			ps.setBigDecimal(++index, clt.getTransferAmount());
			ps.setLong(++index, clt.getExcessId());
			ps.setLong(++index, clt.getToLinkedTranId());
			ps.setLong(++index, clt.getFromLinkedTranId());
			ps.setBigDecimal(++index, clt.getExcessAmount());
			ps.setBigDecimal(++index, clt.getUtiliseAmount());
			ps.setBigDecimal(++index, clt.getReserveAmount());
			ps.setBigDecimal(++index, clt.getAvailableAmount());
			ps.setLong(++index, clt.getToExcessId());
			ps.setString(++index, clt.getExcessType());
			ps.setString(++index, clt.getSource());
			ps.setInt(++index, clt.getVersion());
			ps.setLong(++index, clt.getCreatedBy());
			ps.setTimestamp(++index, clt.getCreatedOn());
			ps.setLong(++index, clt.getApprovedBy());
			ps.setTimestamp(++index, clt.getApprovedOn());
			ps.setLong(++index, clt.getLastMntBy());
			ps.setTimestamp(++index, clt.getLastMntOn());
			ps.setString(++index, clt.getRecordStatus());
			ps.setString(++index, clt.getRoleCode());
			ps.setString(++index, clt.getNextRoleCode());
			ps.setString(++index, clt.getTaskId());
			ps.setString(++index, clt.getNextTaskId());
			ps.setString(++index, clt.getRecordType());
			ps.setLong(++index, clt.getWorkflowId());
		});

		return clt.getId();
	}

	@Override
	public CrossLoanTransfer getCrossLoanTransferById(long id, String type) {
		StringBuilder sql = new StringBuilder("Select");
		sql.append(" ID, CustId, ReceiptId, FromFinID, ToFinID, FromFinReference, ToFinReference, TransferAmount");
		sql.append(", ExcessId, ToLinkedTranId, FromLinkedTranId, ExcessAmount, UtiliseAmount, ReserveAmount");
		sql.append(", AvailableAmount, ToExcessId, ExcessType, Source, Version");
		sql.append(", CreatedBy, CreatedOn, ApprovedBy, ApprovedOn, LastMntBy, LastMntOn");
		sql.append(", RecordStatus, RoleCode, NextRoleCode, TaskId, NextTaskId, RecordType, WorkflowId");

		if (type.contains("View")) {
			sql.append(", CustCif, CustShrtName, ReceiptDate, ReceiptAmount, TransactionRef");
			sql.append(", ExcessAmount, ReserveAmount, UtiliseAmount, AvailableAmount");
		}

		sql.append(" From  Cross_Loan_Transfer");
		sql.append(StringUtils.trimToEmpty(type));
		sql.append(" Where ID = ?");

		logger.debug(Literal.SQL.concat(sql.toString()));

		return this.jdbcOperations.queryForObject(sql.toString(), new CrossLoanTrasferRM(type), id);
	}

	@Override
	public void update(CrossLoanTransfer clt, String tableType) {
		StringBuilder sql = new StringBuilder("Update Cross_Loan_Transfer");
		sql.append(tableType);
		sql.append(" Set CustId = ?, ReceiptId = ?, ExcessId = ?");
		sql.append(", AvailableAmount = ?, FromFinID = ?, ToFinID = ?, FromFinReference = ?");
		sql.append(", ToFinReference = ?, TransferAmount = ?");
		sql.append(", ExcessAmount = ?, UtiliseAmount = ?, ReserveAmount = ?");
		sql.append(", ToLinkedTranId = ?, FromLinkedTranId = ?, ToExcessId = ?");
		sql.append(", Version = ?, LastMntBy = ?, LastMntOn = ?");
		sql.append(", RecordStatus= ?, RoleCode = ?, NextRoleCode = ?");
		sql.append(", TaskId = ?, NextTaskId = ?, RecordType = ?, WorkflowId = ?");
		sql.append(" Where ID = ?");

		logger.debug(Literal.SQL.concat(sql.toString()));

		int recordCount = this.jdbcOperations.update(sql.toString(), ps -> {
			int index = 0;

			ps.setLong(++index, clt.getCustId());
			ps.setLong(++index, clt.getReceiptId());
			ps.setLong(++index, clt.getExcessId());
			ps.setBigDecimal(++index, clt.getAvailableAmount());
			ps.setLong(++index, clt.getFromFinID());
			ps.setLong(++index, clt.getToFinID());
			ps.setString(++index, clt.getFromFinReference());
			ps.setString(++index, clt.getToFinReference());
			ps.setBigDecimal(++index, clt.getTransferAmount());
			ps.setBigDecimal(++index, clt.getExcessAmount());
			ps.setBigDecimal(++index, clt.getUtiliseAmount());
			ps.setBigDecimal(++index, clt.getReserveAmount());
			ps.setLong(++index, clt.getToLinkedTranId());
			ps.setLong(++index, clt.getFromLinkedTranId());
			ps.setLong(++index, clt.getToExcessId());
			ps.setInt(++index, clt.getVersion());
			ps.setLong(++index, clt.getLastMntBy());
			ps.setTimestamp(++index, clt.getLastMntOn());
			ps.setString(++index, clt.getRecordStatus());
			ps.setString(++index, clt.getRoleCode());
			ps.setString(++index, clt.getNextRoleCode());
			ps.setString(++index, clt.getTaskId());
			ps.setString(++index, clt.getNextTaskId());
			ps.setString(++index, clt.getRecordType());
			ps.setLong(++index, clt.getWorkflowId());

			ps.setLong(++index, clt.getId());
		});

		if (recordCount <= 0) {
			throw new ConcurrencyException();
		}
	}

	@Override
	public void delete(long id, String tableType) {
		StringBuilder sql = new StringBuilder("Delete from Cross_Loan_Transfer");
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
	public List<CrossLoanTransfer> getKnockOfListByRef(String finReference, boolean fromReference) {
		StringBuilder sql = new StringBuilder("Select");
		sql.append(" clt.FromFinID, clt.ToFinID, clt.FromFinReference, clt.ToFinReference");
		sql.append(", clt.TransferAmount, clt.ExcessID, fea.ValueDate");
		sql.append(" From Cross_Loan_Transfer clt");
		sql.append(" Inner Join FinExcessAmount fea on fea.ExcessID = clt.ExcessID");

		if (fromReference) {
			sql.append(" Where FromFinReference = ?");
		} else {
			sql.append(" Where ToFinReference = ?");
		}

		sql.append(" and clt.RecordStatus = ?");

		logger.debug(Literal.SQL.concat(sql.toString()));

		return this.jdbcOperations.query(sql.toString(), (rs, rowNum) -> {
			CrossLoanTransfer clt = new CrossLoanTransfer();

			clt.setFromFinID(rs.getLong("FromFinID"));
			clt.setToFinID(rs.getLong("ToFinID"));
			clt.setFromFinReference(rs.getString("FromFinReference"));
			clt.setToFinReference(rs.getString("ToFinReference"));
			clt.setTransferAmount(rs.getBigDecimal("TransferAmount"));
			clt.setExcessId(rs.getLong("ExcessID"));
			clt.setValueDate(JdbcUtil.getDate(rs.getDate("ValueDate")));

			return clt;
		}, finReference, "APPROVED");
	}

	@Override
	public boolean isLoanExistInTemp(long finID, boolean fromLoan) {
		StringBuilder sql = new StringBuilder("Select");
		sql.append(" count(T1.ID) from Cross_Loan_Transfer_Temp T1");
		sql.append(" Inner Join Cross_Loan_KnockOff_Temp T2 on T1.ID = T2.TransferID");

		if (fromLoan) {
			sql.append(" Where T1.FromFinID = ?");
		} else {
			sql.append(" Where T1.ToFinID = ?");
		}

		logger.debug(Literal.SQL.concat(sql.toString()));

		return this.jdbcOperations.queryForObject(sql.toString(), Integer.class, finID) > 0;
	}

	private class CrossLoanTrasferRM implements RowMapper<CrossLoanTransfer> {
		private String type;

		private CrossLoanTrasferRM(String type) {
			this.type = type;
		}

		@Override
		public CrossLoanTransfer mapRow(ResultSet rs, int rowNum) throws SQLException {
			CrossLoanTransfer clt = new CrossLoanTransfer();

			clt.setId(rs.getLong("ID"));
			clt.setCustId(rs.getLong("CustId"));
			clt.setReceiptId(rs.getLong("ReceiptId"));
			clt.setFromFinID(rs.getLong("FromFinID"));
			clt.setToFinID(rs.getLong("ToFinID"));
			clt.setFromFinReference(rs.getString("FromFinReference"));
			clt.setToExcessId(rs.getLong("ToExcessId"));
			clt.setToFinReference(rs.getString("ToFinReference"));
			clt.setTransferAmount(rs.getBigDecimal("TransferAmount"));
			clt.setExcessId(rs.getLong("ExcessId"));
			clt.setToLinkedTranId(rs.getLong("ToLinkedTranId"));
			clt.setFromLinkedTranId(rs.getLong("FromLinkedTranId"));
			clt.setVersion(rs.getInt("Version"));
			clt.setLastMntBy(rs.getLong("LastMntBy"));
			clt.setLastMntOn(rs.getTimestamp("LastMntOn"));
			clt.setRecordStatus(rs.getString("RecordStatus"));
			clt.setRoleCode(rs.getString("RoleCode"));
			clt.setNextRoleCode(rs.getString("NextRoleCode"));
			clt.setTaskId(rs.getString("TaskId"));
			clt.setNextTaskId(rs.getString("NextTaskId"));
			clt.setRecordType(rs.getString("RecordType"));
			clt.setWorkflowId(rs.getLong("WorkflowId"));
			clt.setExcessType(rs.getString("ExcessType"));
			clt.setSource(rs.getString("Source"));

			if (type.contains("View")) {
				clt.setCustCif(rs.getString("CustCif"));
				clt.setCustShrtName(rs.getString("CustShrtName"));
				clt.setReceiptDate(rs.getDate("ReceiptDate"));
				clt.setReceiptAmount(rs.getBigDecimal("ReceiptAmount"));
				clt.setTransactionRef(rs.getString("TransactionRef"));
				clt.setExcessAmount(rs.getBigDecimal("ExcessAmount"));
				clt.setReserveAmount(rs.getBigDecimal("ReserveAmount"));
				clt.setUtiliseAmount(rs.getBigDecimal("UtiliseAmount"));
				clt.setAvailableAmount(rs.getBigDecimal("AvailableAmount"));
			}

			return clt;
		}

	}

	@Override
	public FinExcessAmount getCrossLoanExcess(long excessId) {
		StringBuilder sql = new StringBuilder("Select");
		sql.append(" ExcessID, FinID, FinReference, AmountType, Amount, UtilisedAmt");
		sql.append(", ReservedAmt, BalanceAmt, ReceiptID, ValueDate");
		sql.append(" From FinExcessAmount");
		sql.append(" Where ExcessId = ?");

		logger.debug(Literal.SQL.concat(sql.toString()));

		try {
			return jdbcOperations.queryForObject(sql.toString(), (rs, rowNum) -> {
				FinExcessAmount ea = new FinExcessAmount();

				ea.setExcessID(rs.getLong("ExcessID"));
				ea.setFinID(rs.getLong("FinID"));
				ea.setFinReference(rs.getString("FinReference"));
				ea.setAmountType(rs.getString("AmountType"));
				ea.setAmount(rs.getBigDecimal("Amount"));
				ea.setUtilisedAmt(rs.getBigDecimal("UtilisedAmt"));
				ea.setReservedAmt(rs.getBigDecimal("ReservedAmt"));
				ea.setBalanceAmt(rs.getBigDecimal("BalanceAmt"));
				ea.setReceiptID(rs.getLong("ReceiptID"));
				ea.setValueDate(JdbcUtil.getDate(rs.getDate("ValueDate")));

				return ea;
			}, excessId);
		} catch (EmptyResultDataAccessException e) {
			logger.warn(Message.NO_RECORD_FOUND);
			return null;
		}
	}

	@Override
	public List<CrossLoanTransfer> getCrossLoanTransferByFinId(long finId, String type) {
		StringBuilder sql = new StringBuilder("Select");
		sql.append(" FromFinID, ToFinID, FromFinReference, ToFinReference");
		sql.append(", TransferAmount From Cross_Loan_Transfer");
		sql.append(StringUtils.trimToEmpty(type));
		sql.append(" Where FromFinID = ? OR ToFinID = ?");

		logger.debug(Literal.SQL.concat(sql.toString()));

		return this.jdbcOperations.query(sql.toString(), (rs, rowNum) -> {
			CrossLoanTransfer clt = new CrossLoanTransfer();

			clt.setFromFinID(rs.getLong("FromFinID"));
			clt.setToFinID(rs.getLong("ToFinID"));
			clt.setFromFinReference(rs.getString("FromFinReference"));
			clt.setToFinReference(rs.getString("ToFinReference"));
			clt.setTransferAmount(rs.getBigDecimal("TransferAmount"));

			return clt;
		}, finId, finId);
	}

}
