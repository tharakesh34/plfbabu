/**
 * Copyright 2011 - Pennant Technologies
 * 
 * This file is part of Pennant Java Application Framework and related Products. All
 * components/modules/functions/classes/logic in this software, unless otherwise stated, the property of Pennant
 * Technologies.
 * 
 * Copyright and other intellectual property laws protect these materials. Reproduction or retransmission of the
 * materials, in whole or in part, in any manner, without the prior written consent of the copyright holder, is a
 * violation of copyright law.
 */

/**
 ********************************************************************************************
 * FILE HEADER *
 ********************************************************************************************
 * * FileName : TdsReceivablesTxnDAOImpl.java * * Author : PENNANT TECHONOLOGIES * * Creation Date : 03-09-2020 * *
 * Modified Date : 03-09-2020 * * Description : * *
 ********************************************************************************************
 * Date Author Version Comments *
 ********************************************************************************************
 * 03-09-2020 PENNANT 0.1 * * * * * * * * *
 ********************************************************************************************
 */
package com.pennant.backend.dao.tds.receivables.impl;

import java.math.BigDecimal;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.PreparedStatementSetter;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;

import com.pennant.backend.dao.tds.receivables.TdsReceivablesTxnDAO;
import com.pennant.backend.dao.tds.receivables.TdsReceivablesTxnStatus;
import com.pennant.backend.model.rulefactory.ReturnDataSet;
import com.pennant.backend.model.tds.receivables.TdsReceivablesTxn;
import com.pennant.backend.util.PennantConstants;
import com.pennanttech.pennapps.core.ConcurrencyException;
import com.pennanttech.pennapps.core.DependencyFoundException;
import com.pennanttech.pennapps.core.jdbc.JdbcUtil;
import com.pennanttech.pennapps.core.jdbc.SequenceDao;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pennapps.core.resource.Message;
import com.pennanttech.pff.core.TableType;

/**
 * Data access layer implementation for <code>TdsReceivablesTxn</code> with set of CRUD operations.
 */
public class TdsReceivablesTxnDAOImpl extends SequenceDao<TdsReceivablesTxn> implements TdsReceivablesTxnDAO {
	private static Logger logger = LogManager.getLogger(TdsReceivablesTxnDAOImpl.class);

	public TdsReceivablesTxnDAOImpl() {
		super();
	}

	public long getAdjustmentTxnSeq() {
		return getNextValue("SEQADJ_TXN_ID");
	}

	@Override
	public String save(TdsReceivablesTxn tdsReceivablesTxn, TableType tableType) {
		StringBuilder sql = new StringBuilder("Insert Into");
		sql.append(" TDS_RECEIVABLES_TXN");
		sql.append(tableType.getSuffix());
		sql.append(" (Id, TxnID, ReceivableID, TranDate, ReceiptID, AdjustmentAmount, Status");
		sql.append(", Version , LastMntBy, LastMntOn, RecordStatus, RoleCode, NextRoleCode, FinTranYear");
		sql.append(", TaskId, NextTaskId, RecordType, WorkflowId, Module)");
		sql.append("  Values (?, ?, ?, ?, ?, ?, ?");
		sql.append(", ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)");

		if (tdsReceivablesTxn.getId() == Long.MIN_VALUE) {
			tdsReceivablesTxn.setId(getNextValue("SEQTDS_RECEIVABLES_TXN"));
		}

		logger.debug(Literal.SQL + sql.toString());

		try {
			jdbcOperations.update(sql.toString(), ps -> {
				int index = 1;

				ps.setLong(index++, tdsReceivablesTxn.getId());
				ps.setLong(index++, tdsReceivablesTxn.getTxnID());
				ps.setLong(index++, tdsReceivablesTxn.getReceivableID());
				ps.setDate(index++, JdbcUtil.getDate(tdsReceivablesTxn.getTranDate()));
				ps.setLong(index++, tdsReceivablesTxn.getReceiptID());
				ps.setBigDecimal(index++, tdsReceivablesTxn.getAdjustmentAmount());
				ps.setString(index++, tdsReceivablesTxn.getStatus());
				ps.setInt(index++, tdsReceivablesTxn.getVersion());
				ps.setLong(index++, tdsReceivablesTxn.getLastMntBy());
				ps.setTimestamp(index++, tdsReceivablesTxn.getLastMntOn());
				ps.setString(index++, tdsReceivablesTxn.getRecordStatus());
				ps.setString(index++, tdsReceivablesTxn.getRoleCode());
				ps.setString(index++, tdsReceivablesTxn.getNextRoleCode());
				ps.setString(index++, tdsReceivablesTxn.getFinTranYear());
				ps.setString(index++, tdsReceivablesTxn.getTaskId());
				ps.setString(index++, tdsReceivablesTxn.getNextTaskId());
				ps.setString(index++, tdsReceivablesTxn.getRecordType());
				ps.setLong(index++, tdsReceivablesTxn.getWorkflowId());
				ps.setString(index, tdsReceivablesTxn.getModule());

			});

		} catch (DuplicateKeyException e) {
			throw new ConcurrencyException(e);
		}

		return String.valueOf(tdsReceivablesTxn.getId());
	}

	@Override
	public void update(TdsReceivablesTxn tdsReceivablesTxn, TableType tableType) {
		StringBuilder sql = new StringBuilder("UPDATE TDS_RECEIVABLES_TXN");
		sql.append(tableType.getSuffix());
		sql.append(" SET TxnID = ?, ReceivableID = ?, Status = ?");
		sql.append(", TranDate = ?, ReceiptID = ?, AdjustmentAmount = ?");
		sql.append(", FinTranYear = ?, Version = ?, LastMntBy = ?");
		sql.append(", LastMntOn = ?, RecordStatus = ?, RoleCode = ?");
		sql.append(", NextRoleCode = ?, TaskId = ?, NextTaskId = ?");
		sql.append(", RecordType = ?, WorkflowId = ?, Module = ?");
		sql.append(" Where id = ?");

		logger.debug(Literal.SQL + sql.toString());

		this.jdbcOperations.update(sql.toString(), ps -> {
			int index = 1;

			ps.setLong(index++, tdsReceivablesTxn.getTxnID());
			ps.setLong(index++, tdsReceivablesTxn.getReceivableID());
			ps.setString(index++, tdsReceivablesTxn.getStatus());
			ps.setDate(index++, JdbcUtil.getDate(tdsReceivablesTxn.getTranDate()));
			ps.setLong(index++, tdsReceivablesTxn.getReceiptID());
			ps.setBigDecimal(index++, tdsReceivablesTxn.getAdjustmentAmount());
			ps.setString(index++, tdsReceivablesTxn.getFinTranYear());
			ps.setInt(index++, tdsReceivablesTxn.getVersion());
			ps.setLong(index++, tdsReceivablesTxn.getLastMntBy());
			ps.setTimestamp(index++, tdsReceivablesTxn.getLastMntOn());
			ps.setString(index++, tdsReceivablesTxn.getRecordStatus());
			ps.setString(index++, tdsReceivablesTxn.getRoleCode());
			ps.setString(index++, tdsReceivablesTxn.getNextRoleCode());
			ps.setString(index++, tdsReceivablesTxn.getTaskId());
			ps.setString(index++, tdsReceivablesTxn.getNextTaskId());
			ps.setString(index++, tdsReceivablesTxn.getRecordType());
			ps.setLong(index++, tdsReceivablesTxn.getWorkflowId());
			ps.setString(index++, tdsReceivablesTxn.getModule());

			ps.setLong(index, tdsReceivablesTxn.getId());

		});
	}

	@Override
	public void delete(TdsReceivablesTxn tdsReceivablesTxn, TableType tableType) {
		StringBuilder sql = new StringBuilder("Delete From TDS_RECEIVABLES_TXN");
		sql.append(tableType.getSuffix());
		sql.append(" Where id = :id");

		logger.trace(Literal.SQL + sql.toString());

		SqlParameterSource paramSource = new BeanPropertySqlParameterSource(tdsReceivablesTxn);
		int recordCount = 0;

		try {
			recordCount = jdbcTemplate.update(sql.toString(), paramSource);
		} catch (DataAccessException e) {
			throw new DependencyFoundException(e);
		}

		if (recordCount == 0) {
			throw new ConcurrencyException();
		}
	}

	@Override
	public List<TdsReceivablesTxn> getTdsReceivablesTxnsByTanId(long tanId, Date fromDate, Date toDate) {
		StringBuilder sql = new StringBuilder("Select");
		sql.append(" TDA.TanID, TD.TanNumber, TD.TanHolderName, RH.ReceiptId, RH.ReceiptPurpose, RH.ReceiptDate");
		sql.append(", RH.ReceiptAmount, RA.PaidAmount, RA.TdsPaid, TDA.FinReference");
		sql.append(", COALESCE(TRX.TDSAdjusted, 0) TDSAdjusted");
		sql.append(", (RA.TdsPaid - COALESCE(TRX.TDSAdjusted, 0)) BalanceAmount From Tan_Details TD");
		sql.append(" Inner Join Tan_Assignments TDA On TDA.TANID = TD.ID");
		sql.append(" Inner Join FinReceiptheader RH On RH.Reference = TDA.FinReference");
		sql.append(" AND RH.ReceiptModeStatus Not In ('C','B') AND RH.ReceiptDate >= ? AND RH.ReceiptDate <= ?");
		sql.append(" Inner Join ( Select ReceiptId, PaidAmount,  PaidAmount TdsPaid From ReceiptAllocationDetail");
		sql.append(" Where AllocationType = 'TDS' Union All Select ReceiptId, sum(PaidAmount) PaidAmount");
		sql.append(", sum(TdsPaid) TdsPaid From ReceiptAllocationDetail Where TdsPaid > 0 group by ReceiptId) RA");
		sql.append(" On RA.Receiptid = RH.Receiptid");
		sql.append(" Left Join (Select ReceiptId, Sum(AdjustmentAmount) TDSAdjusted From Tds_Receivables_Txn");
		sql.append(" Where Status IS NULL and Module= 'R' group by ReceiptId) TRX On TRX.ReceiptId = RH.ReceiptId");
		sql.append(" Where TD.ID = ?");

		logger.debug(Literal.SQL + sql.toString());

		return this.jdbcOperations.query(sql.toString(), new PreparedStatementSetter() {
			@Override
			public void setValues(PreparedStatement ps) throws SQLException {
				int index = 1;
				ps.setDate(index++, JdbcUtil.getDate(fromDate));
				ps.setDate(index++, JdbcUtil.getDate(toDate));
				ps.setLong(index, tanId);
			}
		}, (rs, rowNum) -> {
			TdsReceivablesTxn tdsRecDetl = new TdsReceivablesTxn();

			tdsRecDetl.setTanID(rs.getLong("TANID"));
			tdsRecDetl.setTanNumber(rs.getString("TANNumber"));
			tdsRecDetl.setTanHolderName(rs.getString("TANHolderName"));
			tdsRecDetl.setReceiptID(rs.getLong("ReceiptId"));
			tdsRecDetl.setReferenceType(rs.getString("ReceiptPurpose"));
			tdsRecDetl.setReceiptDate(rs.getTimestamp("ReceiptDate"));
			tdsRecDetl.setReceiptAmount(rs.getBigDecimal("ReceiptAmount"));
			tdsRecDetl.setTdsAdjusted(rs.getBigDecimal("TDSAdjusted"));
			tdsRecDetl.setBalanceAmount(rs.getBigDecimal("BalanceAmount"));
			tdsRecDetl.setAdjustmentAmount(new BigDecimal(0));
			tdsRecDetl.setTdsReceivable(rs.getBigDecimal("TdsPaid"));
			tdsRecDetl.setFinReference(rs.getString("FinReference"));
			tdsRecDetl.setModule("R");

			return tdsRecDetl;
		});
	}

	@Override
	public List<TdsReceivablesTxn> getTdsReceivablesPostTxnsByTanId(long tanId, Date fromDate, Date toDate) {
		StringBuilder sql = new StringBuilder("Select");
		sql.append(" TanId, TDA.FinREFERENCE, JVE.PostingDate, Jve.TXNAMOUNT TdsPaid");
		sql.append(", COALESCE(TRX.TDSAdjusted, 0) TDSAdjusted, JV.BATCHREFERENCE");
		sql.append(", (Jve.TXNAMOUNT - COALESCE(TRX.TDSAdjusted, 0)) BalanceAmount");
		sql.append(" From TAN_DETAILS TD");
		sql.append(" INNER JOIN TAN_ASSIGNMENTS TDA ON TDA.TANID = TD.ID");
		sql.append(" INNER JOIN JVPostings JV ON JV.REFERENCE  = TDA.finREFERENCE and POSTAGAINST = 'L'");
		sql.append(" INNER JOIN JVPostingEntry Jve ON JVE.BATCHREFERENCE  = JV.BATCHREFERENCE");
		sql.append(" AND Jve.PostingDate >= ? AND Jve.PostingDate <= ? AND TdsAdjReq = 1");
		sql.append(" LEFT JOIN (Select ReceiptId, Sum(AdjustmentAmount) TDSAdjusted");
		sql.append(" From TDS_RECEIVABLES_TXN Where Status IS NULL and Module = 'J' group by ReceiptId) TRX");
		sql.append(" ON TRX.ReceiptId = JV.BATCHREFERENCE Where TD.ID = ?");

		logger.debug(Literal.SQL + sql.toString());

		return this.jdbcOperations.query(sql.toString(), ps -> {
			int index = 1;
			ps.setDate(index++, JdbcUtil.getDate(fromDate));
			ps.setDate(index++, JdbcUtil.getDate(toDate));
			ps.setLong(index, tanId);
		}, (rs, rowNum) -> {
			TdsReceivablesTxn tdsRecDetl = new TdsReceivablesTxn();
			tdsRecDetl.setTanID(rs.getLong("TANID"));
			tdsRecDetl.setFinReference(rs.getString("FinReference"));
			tdsRecDetl.setReceiptDate(rs.getTimestamp("PostingDate"));
			tdsRecDetl.setTdsReceivable(rs.getBigDecimal("TdsPaid"));
			tdsRecDetl.setReceiptID(rs.getLong("BATCHREFERENCE"));
			tdsRecDetl.setTdsAdjusted(rs.getBigDecimal("TDSAdjusted"));
			tdsRecDetl.setBalanceAmount(rs.getBigDecimal("BalanceAmount"));
			tdsRecDetl.setAdjustmentAmount(new BigDecimal(0));
			tdsRecDetl.setModule("J");
			return tdsRecDetl;
		});
	}

	@Override
	public List<TdsReceivablesTxn> getTdsReceivablesTxnsByTxnId(long txnId, TableType type, String module) {
		StringBuilder sql = new StringBuilder("Select");
		sql.append(" TXT.Id, TXT.AdjustmentAmount, TXT.TxnID, TXT.FinTranYear, TXT.Status");
		sql.append(", TXT.Version, TXT.LastMntBy, TXT.LastMntOn, TXT.RecordStatus, TXT.RoleCode, TXT.NextRoleCode");
		sql.append(", TXT.TaskId, TXT.NextTaskId, TXT.RecordType, TXT.WorkflowId");
		sql.append(", COALESCE(TX.AdjustmentAmount, 0) TDSAdjusted, TXT.ReceiptId, RA.TdsPaid, RA.PaidAmount");
		sql.append(", RH.ReceiptId, RH.ReceiptPurpose, RH.ReceiptDate, RH.ReceiptAmount");
		sql.append(", COALESCE((RA.TdsPaid - TX.AdjustmentAmount),0) BalanceAmount , RH.Reference, TXT.Module");
		sql.append(" From TDS_RECEIVABLES_TXN");
		sql.append(type.getSuffix());
		sql.append(" TXT");
		sql.append(" LEFT JOIN (Select ReceiptId, Sum(AdjustmentAmount) AdjustmentAmount From TDS_RECEIVABLES_TXN");
		sql.append(" Where Status is NULL and Module = 'R'group by ReceiptId) TX ON TX.ReceiptId = TXT.ReceiptId");
		sql.append(" INNER JOIN FinReceiptheader RH ON RH.ReceiptId = TXT.ReceiptId");

		if (StringUtils.equals(module, PennantConstants.RECEIVABLE_ADJUSTMENT_CNCL_MODULE)) {
			sql.append(" AND RH.ReceiptModeStatus  NOT IN ('C','B')");
		}

		sql.append(" Inner Join ( Select ReceiptId, PaidAmount,  PaidAmount TdsPaid From ReceiptAllocationDetail");
		sql.append(" Where AllocationType = 'TDS' Union All Select ReceiptId, sum(PaidAmount) PaidAmount");
		sql.append(", sum(TdsPaid) TdsPaid From ReceiptAllocationDetail Where TdsPaid > 0 group by ReceiptId) RA");
		sql.append(" ON RA.Receiptid = TXT.Receiptid Where TXT.TxnID = ? and TXT.Module = 'R'");

		logger.debug(Literal.SQL + sql.toString());

		return this.jdbcOperations.query(sql.toString(), ps -> ps.setLong(1, txnId), (rs, rowNum) -> {
			TdsReceivablesTxn tdsRecDetl = new TdsReceivablesTxn();

			tdsRecDetl.setId(rs.getLong("Id"));
			tdsRecDetl.setTxnID(rs.getLong("TxnID"));
			tdsRecDetl.setFinTranYear(rs.getString("FinTranYear"));
			tdsRecDetl.setStatus(rs.getString("Status"));
			tdsRecDetl.setVersion(rs.getInt("Version"));
			tdsRecDetl.setLastMntBy(rs.getLong("LastMntBy"));
			tdsRecDetl.setLastMntOn(rs.getTimestamp("LastMntOn"));
			tdsRecDetl.setRecordStatus(rs.getString("RecordStatus"));
			tdsRecDetl.setRoleCode(rs.getString("RoleCode"));
			tdsRecDetl.setNextRoleCode(rs.getString("NextRoleCode"));
			tdsRecDetl.setTaskId(rs.getString("TaskId"));
			tdsRecDetl.setNextTaskId(rs.getString("NextTaskId"));
			tdsRecDetl.setRecordType(rs.getString("RecordType"));
			tdsRecDetl.setWorkflowId(rs.getLong("WorkflowId"));
			tdsRecDetl.setReceiptID(rs.getLong("ReceiptId"));
			tdsRecDetl.setReferenceType(rs.getString("ReceiptPurpose"));
			tdsRecDetl.setReceiptDate(rs.getTimestamp("ReceiptDate"));
			tdsRecDetl.setReceiptAmount(rs.getBigDecimal("ReceiptAmount"));
			tdsRecDetl.setTdsAdjusted(rs.getBigDecimal("TDSAdjusted"));
			tdsRecDetl.setBalanceAmount(rs.getBigDecimal("BalanceAmount"));
			tdsRecDetl.setAdjustmentAmount(rs.getBigDecimal("AdjustmentAmount"));
			tdsRecDetl.setTdsReceivable(rs.getBigDecimal("TdsPaid"));
			tdsRecDetl.setFinReference(rs.getString("Reference"));
			tdsRecDetl.setModule(rs.getString("Module"));

			return tdsRecDetl;
		});
	}

	@Override
	public List<TdsReceivablesTxn> getTdsReceivablesPostTxnsByTxnId(long txnId, TableType type, String module) {
		StringBuilder sql = new StringBuilder("Select");
		sql.append(" TXT.Id, TXT.AdjustmentAmount, TXT.TxnID, TXT.FinTranYear, TXT.Status, TXT.Version");
		sql.append(", TXT.LastMntBy, TXT.LastMntOn, TXT.RecordStatus, TXT.RoleCode, TXT.NextRoleCode");
		sql.append(", TXT.TaskId, TXT.NextTaskId, TXT.RecordType, TXT.WorkflowId");
		sql.append(", COALESCE(TX.AdjustmentAmount, 0) TDSAdjusted, TXT.ReceiptId");
		sql.append(", Jve.TXNAMOUNT, JV.BATCHREFERENCE, JVE.PostingDate, JV.REFERENCE");
		sql.append(", COALESCE((Jve.TXNAMOUNT - TX.AdjustmentAmount),0) BalanceAmount, TXT.Module");
		sql.append("  From TDS_RECEIVABLES_TXN");
		sql.append(type.getSuffix());
		sql.append(" TXT");
		sql.append(" LEFT JOIN (Select ReceiptId, Sum(AdjustmentAmount) AdjustmentAmount From TDS_RECEIVABLES_TXN");
		sql.append(" Where Status is NULL and Module = 'J' group by ReceiptId) TX ON TX.ReceiptId = TXT.ReceiptId");
		sql.append(" INNER JOIN JVPostings JV ON JV.BATCHREFERENCE  = TXT.ReceiptId and POSTAGAINST = 'L'");
		sql.append(" INNER JOIN JVPostingEntry Jve ON JVE.BATCHREFERENCE  = JV.BATCHREFERENCE");
		sql.append(" AND TdsAdjReq = 1");
		sql.append(" Where TXT.TxnID = ? and TXT.Module = 'J' ");

		logger.debug(Literal.SQL + sql.toString());

		return this.jdbcOperations.query(sql.toString(), ps -> ps.setLong(1, txnId), (rs, rowNum) -> {
			TdsReceivablesTxn tdsRecDetl = new TdsReceivablesTxn();

			tdsRecDetl.setId(rs.getLong("Id"));
			tdsRecDetl.setTxnID(rs.getLong("TxnID"));
			tdsRecDetl.setFinTranYear(rs.getString("FinTranYear"));
			tdsRecDetl.setStatus(rs.getString("Status"));
			tdsRecDetl.setReceiptID(rs.getLong("ReceiptId"));
			tdsRecDetl.setReceiptDate(rs.getTimestamp("PostingDate"));
			tdsRecDetl.setTdsAdjusted(rs.getBigDecimal("TDSAdjusted"));
			tdsRecDetl.setBalanceAmount(rs.getBigDecimal("BalanceAmount"));
			tdsRecDetl.setAdjustmentAmount(rs.getBigDecimal("AdjustmentAmount"));
			tdsRecDetl.setTdsReceivable(rs.getBigDecimal("TXNAMOUNT"));
			tdsRecDetl.setFinReference(rs.getString("Reference"));
			tdsRecDetl.setVersion(rs.getInt("Version"));
			tdsRecDetl.setLastMntBy(rs.getLong("LastMntBy"));
			tdsRecDetl.setLastMntOn(rs.getTimestamp("LastMntOn"));
			tdsRecDetl.setRecordStatus(rs.getString("RecordStatus"));
			tdsRecDetl.setRoleCode(rs.getString("RoleCode"));
			tdsRecDetl.setNextRoleCode(rs.getString("NextRoleCode"));
			tdsRecDetl.setTaskId(rs.getString("TaskId"));
			tdsRecDetl.setNextTaskId(rs.getString("NextTaskId"));
			tdsRecDetl.setRecordType(rs.getString("RecordType"));
			tdsRecDetl.setWorkflowId(rs.getLong("WorkflowId"));
			tdsRecDetl.setModule(rs.getString("Module"));

			return tdsRecDetl;
		});
	}

	@Override
	public Date getMinRcptFinancialDate(long tanId) {
		StringBuilder sql = new StringBuilder("Select");
		sql.append(" min(RH.ReceiptDate) ReceiptDate");
		sql.append(" From FinReceiptheader RH");
		sql.append(" Inner join TAN_ASSIGNMENTS TA ON TA.FinReference = RH.Reference");
		sql.append(" Where TA.TanId = ?");

		logger.debug(Literal.SQL + sql.toString());

		return this.jdbcOperations.queryForObject(sql.toString(), Date.class, tanId);
	}

	@Override
	public Date getMinPostFinancialDate(long tanId) {
		StringBuilder sql = new StringBuilder("Select");
		sql.append(" min(JVE.PostingDate) PostingDate  From JVPostings JV");
		sql.append(" INNER JOIN JVPostingEntry JVE ON JVE.BATCHREFERENCE = JV.BATCHREFERENCE");
		sql.append(" AND JVE.TDSAdjReq = 1");
		sql.append(" INNER JOIN TAN_ASSIGNMENTS TA ON TA.FinReference = JV.Reference");
		sql.append(" WHERE PostAgainst = 'L' and TA.TanId = ?");

		logger.debug(Literal.SQL + sql.toString());

		return this.jdbcOperations.queryForObject(sql.toString(), Date.class, tanId);
	}

	@Override
	public List<TdsReceivablesTxn> getTdsReceivablesTxnByReceivableId(long receivableID, TableType type) {
		StringBuilder sql = new StringBuilder("Select");
		sql.append(" TxnID, TranDate, Sum(AdjustmentAmount) AdjustmentAmount");
		sql.append(" From TDS_RECEIVABLES_TXN");
		sql.append(type.getSuffix());
		sql.append(" Where ReceivableID = ? and status is null ");
		sql.append(" group by TxnID, TranDate");

		logger.debug(Literal.SQL + sql.toString());

		return this.jdbcOperations.query(sql.toString(), (rs, rowNum) -> {
			TdsReceivablesTxn txn = new TdsReceivablesTxn();

			txn.setTxnID(rs.getLong("TxnID"));
			txn.setTranDate(JdbcUtil.getDate(rs.getDate("TranDate")));
			txn.setAdjustmentAmount(rs.getBigDecimal("AdjustmentAmount"));

			return txn;
		}, receivableID);
	}

	@Override
	public List<TdsReceivablesTxn> getTdsReceivablesTxnByReceiptId(long receiptId, TableType type) {
		StringBuilder sql = new StringBuilder("Select");
		sql.append(" Sum(AdjustmentAmount) AdjustmentAmount, ReceivableId ,ReceiptId");
		sql.append(" From TDS_RECEIVABLES_TXN");
		sql.append(type.getSuffix());
		sql.append(" Where ReceiptId = ? and Status IS NULL");
		sql.append(" group by ReceiptId, ReceivableId");

		logger.debug(Literal.SQL + sql.toString());

		return this.jdbcOperations.query(sql.toString(), ps -> ps.setLong(1, receiptId), (rs, rowNum) -> {
			TdsReceivablesTxn tdsReceivablesTxn = new TdsReceivablesTxn();

			tdsReceivablesTxn.setAdjustmentAmount(rs.getBigDecimal("AdjustmentAmount"));
			tdsReceivablesTxn.setReceivableID(rs.getLong("ReceivableId"));
			tdsReceivablesTxn.setReceiptID(rs.getLong("ReceiptId"));

			return tdsReceivablesTxn;
		});

	}

	@Override
	public int isDuplicateTransaction(long tANId, Date fromDate, Date toDate) {
		StringBuilder sql = new StringBuilder("Select");
		sql.append(" count(RH.ReceiptId) Count");
		sql.append(" From TAN_DETAILS TD");
		sql.append(" INNER JOIN TAN_ASSIGNMENTS TDA on TDA.TANID = TD.ID");
		sql.append(" INNER JOIN FinReceiptheader RH on RH.Reference = TDA.FinReference");
		sql.append(" and RH.ReceiptDate >= ? and RH.ReceiptDate <= ?");
		sql.append(" INNER JOIN TDS_RECEIVABLES_TXN_TEMP TX on RH.ReceiptId = TX.ReceiptId");
		sql.append(" Where TanId = ?");

		logger.debug(Literal.SQL + sql.toString());

		return this.jdbcOperations.queryForObject(sql.toString(), Integer.class, fromDate, toDate, tANId);
	}

	@Override
	public int getPendingTransactions(long receivableId) {
		String sql = "Select count(ReceivableId) Count From TDS_RECEIVABLES_TXN_TEMP Where ReceivableId = ?";

		logger.debug(Literal.SQL + sql);

		return this.jdbcOperations.queryForObject(sql, Integer.class, receivableId);
	}

	@Override
	public void updateReceivablesTxnStatus(long Id, TdsReceivablesTxnStatus Status) {
		StringBuilder sql = new StringBuilder("Update TDS_RECEIVABLES_Txn");
		sql.append(" Set Status = ?");
		sql.append(" Where ");
		sql.append(Status.getColumnName());
		sql.append(" = ?");
		sql.append(" and Status is NULL");

		logger.debug(Literal.SQL + sql.toString());

		this.jdbcOperations.update(sql.toString(), ps -> {
			int index = 1;
			ps.setString(index++, Status.getCode());
			ps.setLong(index, Id);
		});
	}

	@Override
	public List<ReturnDataSet> getPostingsByLinkTransId(long linkedTranId, TableType type, boolean showZeroBal) {

		StringBuilder sql = new StringBuilder("Select");
		sql.append(" LinkedTranId, Postref, PostingId, finReference, FinEvent");
		sql.append(", PostDate, ValueDate, TranCode, TranDesc, RevTranCode, DrOrCr, AcCcy");
		sql.append(", Account, PostAmount, TranOrderId, LovDescEventCodeName FROM Postings");
		sql.append(type.getSuffix());
		sql.append(" Where LinkedTranId = ?");

		if (!showZeroBal) {
			sql.append(" AND PostAmount != 0");
		}
		sql.append(" ORDER BY LinkedTranId, TranOrderId");

		logger.trace(Literal.SQL + sql.toString());

		return this.jdbcOperations.query(sql.toString(), ps -> ps.setLong(1, linkedTranId), (rs, rowNum) -> {
			ReturnDataSet rds = new ReturnDataSet();

			rds.setLinkedTranId(rs.getLong("LinkedTranId"));
			rds.setPostref(rs.getString("Postref"));
			rds.setPostingId(rs.getString("PostingId"));
			rds.setFinReference(rs.getString("FinReference"));
			rds.setFinEvent(rs.getString("FinEvent"));
			rds.setPostDate(rs.getDate("PostDate"));
			rds.setValueDate(rs.getDate("ValueDate"));
			rds.setTranCode(rs.getString("TranCode"));
			rds.setTranDesc(rs.getString("TranDesc"));
			rds.setRevTranCode(rs.getString("RevTranCode"));
			rds.setDrOrCr(rs.getString("DrOrCr"));
			rds.setAccount(rs.getString("Account"));
			rds.setPostAmount(rs.getBigDecimal("PostAmount"));
			rds.setAcCcy(rs.getString("AcCcy"));

			rds.setTranOrderId(rs.getString("TranOrderId"));
			rds.setLovDescEventCodeName(rs.getString("LovDescEventCodeName"));

			return rds;
		});
	}

	@Override
	public List<TdsReceivablesTxn> getTdsReceiptTxnsByFinRef(String finReference, TableType type) {
		StringBuilder sql = new StringBuilder("Select");
		sql.append("  RH.ReceiptId, RH.Reference, TX.ReceivableID, TX.TxnId, TX.Trandate, RH.ReceiptDate");
		sql.append(", RH.ReceiptPurpose, RH.ReceiptAmount, RA.TdsPaid TDSReceivable");
		sql.append(", COALESCE(TX.TDSAdjusted, 0) TDSAdjusted, TR.CertificateNumber,TR.CertificateDate");
		sql.append(", TR.CertificateAmount, TR.Balanceamount CertificateBalance, TX.Status, TX.RecordStatus");
		sql.append(", TX.Module From (Select TxnId, TranDate, ReceiptId, ReceivableID, Status, RecordStatus, Module");
		sql.append(", Sum(AdjustmentAmount) TDSAdjusted From TDS_RECEIVABLES_TXN");
		sql.append(type.getSuffix());
		sql.append(" where Module = 'R'");
		sql.append(" group by TxnID, TranDate, ReceiptID, ReceivableID, Status, RecordStatus, Module) TX");
		sql.append(" Inner Join FINRECEIPTHEADER RH on RH.ReceiptId = TX.ReceiptId");
		sql.append(" Inner Join TDS_Receivables TR ON TR.id = TX.ReceivableID");
		sql.append(" Inner Join ( Select ReceiptId, PaidAmount,  PaidAmount TdsPaid From ReceiptAllocationDetail");
		sql.append(" Where AllocationType = 'TDS' Union All Select ReceiptId, sum(PaidAmount) PaidAmount");
		sql.append(", sum(TdsPaid) TdsPaid From ReceiptAllocationDetail Where TdsPaid > 0 group by ReceiptId) RA");
		sql.append(" On RA.Receiptid = RH.Receiptid Where RH.Reference = ? order by  RH.ReceiptId, TX.TxnId");

		logger.debug(Literal.SQL + sql.toString());

		return this.jdbcOperations.query(sql.toString(), ps -> ps.setString(1, finReference), (rs, rowNum) -> {
			TdsReceivablesTxn tdsRecDetl = new TdsReceivablesTxn();

			tdsRecDetl.setReceiptID(rs.getLong("ReceiptId"));
			tdsRecDetl.setFinReference(rs.getString("Reference"));
			tdsRecDetl.setReceivableID(rs.getLong("ReceivableID"));
			tdsRecDetl.setTxnID(rs.getLong("TxnID"));
			tdsRecDetl.setTranDate(rs.getTimestamp("TranDate"));
			tdsRecDetl.setReceiptDate(rs.getTimestamp("ReceiptDate"));
			tdsRecDetl.setReceiptPurpose(rs.getString("ReceiptPurpose"));
			tdsRecDetl.setReceiptAmount(rs.getBigDecimal("ReceiptAmount"));
			tdsRecDetl.setTdsReceivable(rs.getBigDecimal("TdsReceivable"));
			tdsRecDetl.setTdsAdjusted(rs.getBigDecimal("TDSAdjusted"));
			tdsRecDetl.setCertificateNumber(rs.getString("CertificateNumber"));
			tdsRecDetl.setCertificateDate(rs.getTimestamp("CertificateDate"));
			tdsRecDetl.setCertificateAmount(rs.getBigDecimal("CertificateAmount"));
			tdsRecDetl.setCertificateBalance(rs.getBigDecimal("CertificateBalance"));
			tdsRecDetl.setStatus(rs.getString("Status"));
			tdsRecDetl.setRecordStatus(rs.getString("RecordStatus"));
			tdsRecDetl.setModule(rs.getString("Module"));

			return tdsRecDetl;
		});
	}

	@Override
	public List<TdsReceivablesTxn> getTdsJvPostingsTxnsByFinRef(String finReference, TableType type) {
		StringBuilder sql = new StringBuilder("Select");
		sql.append(" TX.ReceiptId, TX.TxnId, TX.Trandate, JVE.PostingDate, JVE.TXNAmount TDSReceivable");
		sql.append(", COALESCE(TX.TDSAdjusted, 0) TDSAdjusted, TR.CertificateNumber, TR.CertificateDate");
		sql.append(", TR.CertificateAmount, TR.Balanceamount CertificateBalance, TX.Status, TX.RecordStatus");
		sql.append(", TX.Module From (Select TxnId, TranDate, ReceiptId, ReceivableID, Status, RecordStatus, Module");
		sql.append(", Sum(AdjustmentAmount) TDSAdjusted From TDS_RECEIVABLES_TXN");
		sql.append(type.getSuffix());
		sql.append(" where Module = 'J' group by TxnID");
		sql.append(", TranDate, ReceiptID, ReceivableID, Status, RecordStatus, Module) TX");
		sql.append(" INNER JOIN JVPostings JV ON JV.BATCHREFERENCE  = TX.ReceiptId and JV.POSTAGAINST = 'L'");
		sql.append(" INNER JOIN JVPostingEntry JVE ON JVE.BATCHREFERENCE  = JV.BATCHREFERENCE  AND JVE.TdsAdjReq = 1");
		sql.append(" INNER JOIN TDS_Receivables TR ON TR.id = TX.ReceivableID");
		sql.append(" Where JV.Reference = ?  order by  TX.ReceiptId, TX.TxnId");

		logger.debug(Literal.SQL + sql.toString());

		return this.jdbcOperations.query(sql.toString(), ps -> ps.setString(1, finReference), (rs, rowNum) -> {
			TdsReceivablesTxn tdsRecDetl = new TdsReceivablesTxn();

			tdsRecDetl.setReceiptID(rs.getLong("ReceiptId"));
			tdsRecDetl.setTxnID(rs.getLong("TxnID"));
			tdsRecDetl.setTranDate(rs.getTimestamp("TranDate"));
			tdsRecDetl.setReceiptDate(rs.getTimestamp("PostingDate"));
			tdsRecDetl.setTdsReceivable(rs.getBigDecimal("TdsReceivable"));
			tdsRecDetl.setTdsAdjusted(rs.getBigDecimal("TDSAdjusted"));
			tdsRecDetl.setCertificateNumber(rs.getString("CertificateNumber"));
			tdsRecDetl.setCertificateDate(rs.getTimestamp("CertificateDate"));
			tdsRecDetl.setCertificateAmount(rs.getBigDecimal("CertificateAmount"));
			tdsRecDetl.setCertificateBalance(rs.getBigDecimal("CertificateBalance"));
			tdsRecDetl.setStatus(rs.getString("Status"));
			tdsRecDetl.setRecordStatus(rs.getString("RecordStatus"));
			tdsRecDetl.setModule(rs.getString("Module"));

			return tdsRecDetl;
		});
	}

	@Override
	public long getPendingReceipt(long receiptId, TableType type) {
		StringBuilder sql = new StringBuilder("Select ");
		sql.append("ReceiptID From TDS_RECEIVABLES_TXN");
		sql.append(type.getSuffix());
		sql.append(" Where ReceiptID = ? ");

		logger.debug(Literal.SQL + sql.toString());

		try {
			return this.jdbcOperations.queryForObject(sql.toString(), Integer.class, receiptId);
		} catch (EmptyResultDataAccessException e) {
			logger.warn(Message.NO_RECORD_FOUND);
		}

		return 0;
	}

	@Override
	public void deleteTxnByReceiptId(long receiptId) {
		TdsReceivablesTxn tdsReceivableTxn = new TdsReceivablesTxn();
		StringBuilder sql = new StringBuilder("Delete From TDS_RECEIVABLES_TXN_TEMP");
		sql.append(" Where ReceiptId = ?");
		logger.debug(Literal.SQL + sql.toString());

		try {
			jdbcOperations.update(sql.toString(), ps -> ps.setLong(1, tdsReceivableTxn.getId()));
		} catch (DataAccessException e) {
			throw new DependencyFoundException(e);
		}
	}

}
