package com.pennant.backend.dao.receipts.impl;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;

import com.pennant.backend.dao.receipts.CrossLoanTransferDAO;
import com.pennant.backend.model.finance.CrossLoanTransfer;
import com.pennanttech.pennapps.core.ConcurrencyException;
import com.pennanttech.pennapps.core.DependencyFoundException;
import com.pennanttech.pennapps.core.jdbc.SequenceDao;
import com.pennanttech.pennapps.core.resource.Literal;

public class CrossLoanTransferDAOImpl extends SequenceDao<CrossLoanTransfer> implements CrossLoanTransferDAO {
	private static Logger logger = LogManager.getLogger(CrossLoanTransferDAOImpl.class);

	@Override
	public long save(CrossLoanTransfer crossLoan, String tableType) {
		logger.debug(Literal.ENTERING);

		// Prepare the SQL.
		StringBuilder sql = new StringBuilder("insert into CrossLoanTransfer");
		sql.append(tableType);
		sql.append(" (CrossLoanId, CustId, ReceiptId, FromFinReference, ToFinReference, TransferAmount, ExcessId");
		sql.append(", ToLinkedTranId, FromLinkedTranId, ExcessAmount, UtiliseAmount, ReserveAmount, AvailableAmount");
		sql.append(", ToExcessId, ExcessType, Source, Version, LastMntBy, LastMntOn, RecordStatus, RoleCode");
		sql.append(", NextRoleCode, TaskId, NextTaskId, RecordType, WorkflowId)");
		sql.append(" values(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)");

		// Get the identity sequence number.
		if (crossLoan.getCrossLoanId() <= 0) {
			crossLoan.setCrossLoanId(getNextValue("SeqCrossLoanTransfer"));
		}

		// Execute the SQL, binding the arguments.
		logger.trace(Literal.SQL + sql.toString());
		this.jdbcOperations.update(sql.toString(), ps -> {
			int index = 1;
			ps.setLong(index++, crossLoan.getId());
			ps.setLong(index++, crossLoan.getCustId());
			ps.setLong(index++, crossLoan.getReceiptId());
			ps.setString(index++, crossLoan.getFromFinReference());
			ps.setString(index++, crossLoan.getToFinReference());
			ps.setBigDecimal(index++, crossLoan.getTransferAmount());
			ps.setLong(index++, crossLoan.getExcessId());
			ps.setLong(index++, crossLoan.getToLinkedTranId());
			ps.setLong(index++, crossLoan.getFromLinkedTranId());
			ps.setBigDecimal(index++, crossLoan.getExcessAmount());
			ps.setBigDecimal(index++, crossLoan.getUtiliseAmount());
			ps.setBigDecimal(index++, crossLoan.getReserveAmount());
			ps.setBigDecimal(index++, crossLoan.getAvailableAmount());
			ps.setLong(index++, crossLoan.getToExcessId());
			ps.setString(index++, crossLoan.getExcessType());
			ps.setString(index++, crossLoan.getSource());
			ps.setInt(index++, crossLoan.getVersion());
			ps.setLong(index++, crossLoan.getLastMntBy());
			ps.setTimestamp(index++, crossLoan.getLastMntOn());
			ps.setString(index++, crossLoan.getRecordStatus());
			ps.setString(index++, crossLoan.getRoleCode());
			ps.setString(index++, crossLoan.getNextRoleCode());
			ps.setString(index++, crossLoan.getTaskId());
			ps.setString(index++, crossLoan.getNextTaskId());
			ps.setString(index++, crossLoan.getRecordType());
			ps.setLong(index, crossLoan.getWorkflowId());
		});

		return crossLoan.getId();
	}

	@Override
	public CrossLoanTransfer getCrossLoanTransferById(long crossLoanId, String type) {
		logger.debug("Entering");

		CrossLoanTransfer crossLoan = new CrossLoanTransfer();
		crossLoan.setId(crossLoanId);
		StringBuilder sql = new StringBuilder();

		sql.append(" SELECT CrossLoanId, CustId, ReceiptId, FromFinReference, ToExcessId,");
		sql.append(" ToFinReference, TransferAmount, ExcessId, ToLinkedTranId, FromLinkedTranId,");
		sql.append(" Version , LastMntBy, LastMntOn, RecordStatus, RoleCode, NextRoleCode, TaskId, NextTaskId,");
		sql.append(" RecordType, WorkflowId,ExcessType,Source ");
		if (type.contains("View")) {
			sql.append(" ,CustCif, CustShrtName, ReceiptDate, ReceiptAmount, TransactionRef ");
			sql.append(" ,ExcessAmount, ReserveAmount, UtiliseAmount, AvailableAmount ");
		}
		sql.append(" FROM  CrossLoanTransfer");
		sql.append(StringUtils.trimToEmpty(type));
		sql.append(" Where CrossLoanId =:CrossLoanId");

		CrossLoanTrasRowMapper rowMapper = new CrossLoanTrasRowMapper(type);

		logger.debug("Leaving");
		return this.jdbcOperations.queryForObject(sql.toString(), rowMapper, crossLoanId);
	}

	@Override
	public void update(CrossLoanTransfer crossLoan, String tableType) {
		logger.debug(Literal.ENTERING);

		// Prepare the SQL, ensure primary key will not be updated.
		StringBuilder sql = new StringBuilder("update CrossLoanTransfer");
		sql.append(tableType);
		sql.append(" set CustId = ?, ReceiptId = ?, ExcessId = ?");
		sql.append(", AvailableAmount = ?, FromFinReference = ?");
		sql.append(", ToFinReference = ?, TransferAmount = ?");
		sql.append(", ExcessAmount = ?, UtiliseAmount = ?, ReserveAmount = ?");
		sql.append(", ToLinkedTranId = ?, FromLinkedTranId = ?, ToExcessId = ?");
		sql.append(", Version = ?, LastMntBy = ?, LastMntOn = ?");
		sql.append(", RecordStatus= ?, RoleCode = ?, NextRoleCode = ?");
		sql.append(", TaskId = ?, NextTaskId = ?, RecordType = ?, WorkflowId = ?");
		sql.append(" where CrossLoanId = ?");

		// Execute the SQL, binding the arguments.
		logger.trace(Literal.SQL + sql.toString());
		int recordCount = this.jdbcOperations.update(sql.toString(), ps -> {
			int index = 1;

			ps.setLong(index++, crossLoan.getCustId());
			ps.setLong(index++, crossLoan.getReceiptId());
			ps.setLong(index++, crossLoan.getExcessId());
			ps.setBigDecimal(index++, crossLoan.getAvailableAmount());
			ps.setString(index++, crossLoan.getFromFinReference());
			ps.setString(index++, crossLoan.getToFinReference());
			ps.setBigDecimal(index++, crossLoan.getTransferAmount());
			ps.setBigDecimal(index++, crossLoan.getExcessAmount());
			ps.setBigDecimal(index++, crossLoan.getUtiliseAmount());
			ps.setBigDecimal(index++, crossLoan.getReserveAmount());
			ps.setLong(index++, crossLoan.getToLinkedTranId());
			ps.setLong(index++, crossLoan.getFromLinkedTranId());
			ps.setLong(index++, crossLoan.getToExcessId());
			ps.setInt(index++, crossLoan.getVersion());
			ps.setLong(index++, crossLoan.getLastMntBy());
			ps.setTimestamp(index++, crossLoan.getLastMntOn());
			ps.setString(index++, crossLoan.getRecordStatus());
			ps.setString(index++, crossLoan.getRoleCode());
			ps.setString(index++, crossLoan.getNextRoleCode());
			ps.setString(index++, crossLoan.getTaskId());
			ps.setString(index++, crossLoan.getNextTaskId());
			ps.setString(index++, crossLoan.getRecordType());
			ps.setLong(index++, crossLoan.getWorkflowId());

			ps.setLong(index++, crossLoan.getCrossLoanId());
		});

		if (recordCount <= 0) {
			throw new ConcurrencyException();
		}
		logger.debug(Literal.LEAVING);
	}

	@Override
	public void delete(long crossLoanId, String tableType) {
		logger.debug(Literal.ENTERING);

		// Prepare the SQL.
		StringBuilder sql = new StringBuilder("delete from CrossLoanTransfer");
		sql.append(tableType);
		sql.append(" where CrossLoanId = ? ");

		logger.debug(Literal.SQL + sql.toString());

		try {
			jdbcOperations.update(sql.toString(), ps -> ps.setLong(1, crossLoanId));
		} catch (DataAccessException e) {
			throw new DependencyFoundException(e);
		}

		logger.debug(Literal.LEAVING);
	}

	@Override
	public List<CrossLoanTransfer> getKnockOfListByRef(String finReference, boolean whichReference) {
		logger.debug("Entering");

		List<CrossLoanTransfer> excessList;
		MapSqlParameterSource source = new MapSqlParameterSource();
		if (whichReference) {
			source.addValue("FromFinReference", finReference);
		} else {
			source.addValue("ToFinReference", finReference);
		}

		StringBuilder sql = new StringBuilder("");
		sql.append(" select T1.FROMFINREFERENCE,T1.TOFINREFERENCE,T1.TRANSFERAMOUNT,T1.EXCESSID,T2.VALUEDATE");
		sql.append(" from CROSSLOANTransfer T1 inner join FINEXCESSAMOUNT T2 on T2.EXCESSID= T1.EXCESSID");
		if (whichReference) {
			sql.append(" Where FromFinReference = :FromFinReference");
		} else {
			sql.append(" Where ToFinReference = :ToFinReference");
		}
		sql.append(" And T1.RecordStatus ='APPROVED'");

		RowMapper<CrossLoanTransfer> typeRowMapper = BeanPropertyRowMapper.newInstance(CrossLoanTransfer.class);

		try {
			excessList = this.jdbcTemplate.query(sql.toString(), source, typeRowMapper);
		} catch (Exception e) {
			logger.warn(e);
			excessList = new ArrayList<>();
		}

		logger.debug("Leaving");
		return excessList;
	}

	@Override
	public boolean isLoanExistInTemp(String fromFinReference, boolean fromLoan) {
		logger.debug("Entering");

		boolean loanExists = false;

		StringBuilder sql = new StringBuilder(" ");
		sql.append(" Select count(T1.CROSSLOANID) from CrossLoanTransfer_temp T1 ");
		sql.append(" Inner join CROSSLOANKNOCKOFFHEADER_temp T2 on T1.CROSSLOANID=T2.CROSSLOANID ");

		if (fromLoan) {
			sql.append(" Where T1.FROMFINREFERENCE = :FinReference");
		} else {
			sql.append(" Where T1.TOFINREFERENCE = :FinReference");
		}
		logger.debug("selectSql: " + sql.toString());

		try {
			loanExists = this.jdbcOperations.queryForObject(sql.toString(), Boolean.class, fromFinReference);
		} catch (EmptyResultDataAccessException e) {
			logger.warn("Exception: ", e);
			loanExists = false;
		}

		logger.debug("Leaving");
		return loanExists;

	}

	private class CrossLoanTrasRowMapper implements RowMapper<CrossLoanTransfer> {
		private String type;

		private CrossLoanTrasRowMapper(String type) {
			this.type = type;
		}

		@Override
		public CrossLoanTransfer mapRow(ResultSet rs, int rowNum) throws SQLException {
			CrossLoanTransfer clt = new CrossLoanTransfer();

			clt.setCrossLoanId(rs.getLong("CrossLoanId"));
			clt.setCustId(rs.getLong("CustId"));
			clt.setReceiptId(rs.getLong("ReceiptId"));
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

}
