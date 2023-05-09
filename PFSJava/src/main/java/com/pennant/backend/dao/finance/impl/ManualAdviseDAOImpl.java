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
 * * FileName : ManualAdviseDAOImpl.java * * Author : PENNANT TECHONOLOGIES * * Creation Date : 22-04-2017 * * Modified
 * Date : 22-04-2017 * * Description : * *
 ********************************************************************************************
 * Date Author Version Comments *
 ********************************************************************************************
 * 22-04-2017 PENNANT 0.1 * * * * * * * * *
 ********************************************************************************************
 */
package com.pennant.backend.dao.finance.impl;

import java.math.BigDecimal;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.lang.StringUtils;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.RowMapper;
import org.zkoss.util.resource.Labels;

import com.pennant.app.constants.ImplementationConstants;
import com.pennant.app.util.SysParamUtil;
import com.pennant.backend.dao.finance.ManualAdviseDAO;
import com.pennant.backend.model.finance.AdviseDueTaxDetail;
import com.pennant.backend.model.finance.FinanceMain;
import com.pennant.backend.model.finance.ManualAdvise;
import com.pennant.backend.model.finance.ManualAdviseMovements;
import com.pennant.backend.model.finance.ManualAdviseReserve;
import com.pennant.backend.util.FinanceConstants;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.RepayConstants;
import com.pennant.backend.util.UploadConstants;
import com.pennant.pff.fee.AdviseType;
import com.pennanttech.pennapps.core.App;
import com.pennanttech.pennapps.core.ConcurrencyException;
import com.pennanttech.pennapps.core.DependencyFoundException;
import com.pennanttech.pennapps.core.jdbc.JdbcUtil;
import com.pennanttech.pennapps.core.jdbc.SequenceDao;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pennapps.core.resource.Message;
import com.pennanttech.pennapps.core.util.DateUtil;
import com.pennanttech.pff.advancepayment.AdvancePaymentUtil.AdvanceRuleCode;
import com.pennanttech.pff.core.TableType;
import com.pennanttech.pff.core.util.QueryUtil;
import com.pennanttech.pff.receipt.constants.Allocation;

/**
 * Data access layer implementation for <code>ManualAdvise</code> with set of CRUD operations.
 */
public class ManualAdviseDAOImpl extends SequenceDao<ManualAdvise> implements ManualAdviseDAO {

	public ManualAdviseDAOImpl() {
		super();
	}

	@Override
	public ManualAdvise getManualAdviseById(long adviseID, String type) {
		StringBuilder sql = getManualAdvicequery(type);
		sql.append(" Where adviseID = ?");

		logger.debug(Literal.SQL.concat(sql.toString()));

		try {
			return this.jdbcOperations.queryForObject(sql.toString(), new ManualAdviseRM(type), adviseID);
		} catch (EmptyResultDataAccessException e) {
			logger.warn(Message.NO_RECORD_FOUND);
			return null;
		}
	}

	@Override
	public ManualAdvise getManualAdviseByReceiptId(long receiptID, String type) {
		StringBuilder sql = getManualAdvicequery(type);
		sql.append(" Where ReceiptID = ?");

		logger.debug(Literal.SQL.concat(sql.toString()));

		try {
			return this.jdbcOperations.queryForObject(sql.toString(), new ManualAdviseRM(type), receiptID);
		} catch (EmptyResultDataAccessException e) {
			logger.warn(Message.NO_RECORD_FOUND);
			return null;
		}
	}

	@Override
	public String save(ManualAdvise ma, TableType tableType) {
		if (ma.getBalanceAmt().compareTo(BigDecimal.ZERO) == 0) {
			ma.setBalanceAmt(ma.getAdviseAmount().subtract(ma.getPaidAmount()).subtract(ma.getWaivedAmount())
					.subtract(ma.getReservedAmt()));// added
		}

		StringBuilder sql = new StringBuilder("Insert Into");
		sql.append(" ManualAdvise");
		sql.append(tableType.getSuffix());
		sql.append("(AdviseID, AdviseType, FinID, FinReference, FeeTypeID, sequence, AdviseAmount");
		sql.append(", BounceID, ReceiptID, PaidAmount, WaivedAmount, Remarks, ValueDate, PostDate, ReservedAmt");
		sql.append(", BalanceAmt, PaidCGST, PaidSGST, PaidUGST, PaidIGST, PaidCESS, WaivedCGST, WaivedSGST");
		sql.append(", WaivedUGST, WaivedIGST, WaivedCESS, FinSource, Version, LastMntBy, LastMntOn, RecordStatus");
		sql.append(", RoleCode, NextRoleCode, TaskId, NextTaskId, RecordType, WorkflowId, DueCreation, PresentmentId");
		sql.append(", LinkedTranId, HoldDue, DueDate, Status, Reason");
		sql.append(") Values(");
		sql.append("?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?");
		sql.append(", ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?");
		sql.append(")");

		if (ma.getAdviseID() <= 0) {
			ma.setAdviseID(getNewAdviseID());
		}

		logger.debug(Literal.SQL.concat(sql.toString()));

		try {
			jdbcOperations.update(sql.toString(), ps -> {
				int index = 1;

				ps.setLong(index++, ma.getAdviseID());
				ps.setInt(index++, ma.getAdviseType());
				ps.setLong(index++, ma.getFinID());
				ps.setString(index++, ma.getFinReference());
				ps.setLong(index++, ma.getFeeTypeID());
				ps.setInt(index++, ma.getSequence());
				ps.setBigDecimal(index++, ma.getAdviseAmount());
				ps.setLong(index++, ma.getBounceID());
				ps.setLong(index++, ma.getReceiptID());
				ps.setBigDecimal(index++, ma.getPaidAmount());
				ps.setBigDecimal(index++, ma.getWaivedAmount());
				ps.setString(index++, ma.getRemarks());
				ps.setDate(index++, JdbcUtil.getDate(ma.getValueDate()));
				ps.setDate(index++, JdbcUtil.getDate(ma.getPostDate()));
				ps.setBigDecimal(index++, ma.getReservedAmt());
				ps.setBigDecimal(index++, ma.getBalanceAmt());
				ps.setBigDecimal(index++, ma.getPaidCGST());
				ps.setBigDecimal(index++, ma.getPaidSGST());
				ps.setBigDecimal(index++, ma.getPaidUGST());
				ps.setBigDecimal(index++, ma.getPaidIGST());
				ps.setBigDecimal(index++, ma.getPaidCESS());
				ps.setBigDecimal(index++, ma.getWaivedCGST());
				ps.setBigDecimal(index++, ma.getWaivedSGST());
				ps.setBigDecimal(index++, ma.getWaivedUGST());
				ps.setBigDecimal(index++, ma.getWaivedIGST());
				ps.setBigDecimal(index++, ma.getWaivedCESS());
				ps.setString(index++, ma.getFinSource());
				ps.setInt(index++, ma.getVersion());
				ps.setLong(index++, ma.getLastMntBy());
				ps.setTimestamp(index++, ma.getLastMntOn());
				ps.setString(index++, ma.getRecordStatus());
				ps.setString(index++, ma.getRoleCode());
				ps.setString(index++, ma.getNextRoleCode());
				ps.setString(index++, ma.getTaskId());
				ps.setString(index++, ma.getNextTaskId());
				ps.setString(index++, ma.getRecordType());
				ps.setLong(index++, ma.getWorkflowId());
				ps.setBoolean(index++, ma.isDueCreation());
				ps.setObject(index++, ma.getPresentmentID());
				ps.setObject(index++, ma.getLinkedTranId());
				ps.setBoolean(index++, ma.isHoldDue());
				ps.setDate(index++, JdbcUtil.getDate(ma.getDueDate()));
				ps.setString(index++, ma.getStatus());
				ps.setString(index, ma.getReason());
			});
		} catch (DuplicateKeyException e) {
			throw new ConcurrencyException(e);
		}

		return String.valueOf(ma.getAdviseID());
	}

	@Override
	public void update(ManualAdvise ma, TableType tableType) {
		StringBuilder sql = new StringBuilder("Update");
		sql.append(" ManualAdvise");
		sql.append(tableType.getSuffix());
		sql.append(" Set AdviseType = ?, FinID = ?,  FinReference = ?, FeeTypeID = ?");
		sql.append(", Sequence = ?, AdviseAmount = ?, PaidAmount = ?");
		sql.append(", WaivedAmount = ?, Remarks = ?, BounceID = ?, ReceiptID = ?");
		sql.append(", ValueDate = ?, PostDate = ?, ReservedAmt = ?, BalanceAmt = ?");
		sql.append(", PaidCGST = ?, PaidSGST = ?, PaidUGST = ?, PaidIGST = ?, PaidCESS = ?");
		sql.append(", WaivedCGST = ?, WaivedSGST = ?, WaivedIGST = ?, WaivedUGST = ?, WaivedCESS = ?");
		sql.append(", FinSource = ?, DueCreation = ?, LinkedTranId = ?, Status = ?, Reason = ?");
		sql.append(", LastMntOn = ?, RecordStatus = ?, RoleCode = ? , NextRoleCode = ?, TaskId = ?, NextTaskId = ?");
		sql.append(", RecordType = ?, WorkflowId = ?");
		sql.append(" Where AdviseID = ?");

		logger.debug(Literal.SQL.concat(sql.toString()));

		int recordCount = jdbcOperations.update(sql.toString(), ps -> {
			int index = 1;

			ps.setInt(index++, ma.getAdviseType());
			ps.setLong(index++, ma.getFinID());
			ps.setString(index++, ma.getFinReference());
			ps.setLong(index++, ma.getFeeTypeID());
			ps.setInt(index++, ma.getSequence());
			ps.setBigDecimal(index++, ma.getAdviseAmount());
			ps.setBigDecimal(index++, ma.getPaidAmount());
			ps.setBigDecimal(index++, ma.getWaivedAmount());
			ps.setString(index++, ma.getRemarks());
			ps.setLong(index++, ma.getBounceID());
			ps.setLong(index++, ma.getReceiptID());
			ps.setDate(index++, JdbcUtil.getDate(ma.getValueDate()));
			ps.setDate(index++, JdbcUtil.getDate(ma.getPostDate()));
			ps.setBigDecimal(index++, ma.getReservedAmt());
			ps.setBigDecimal(index++, ma.getBalanceAmt());
			ps.setBigDecimal(index++, ma.getPaidCGST());
			ps.setBigDecimal(index++, ma.getPaidSGST());
			ps.setBigDecimal(index++, ma.getPaidUGST());
			ps.setBigDecimal(index++, ma.getPaidIGST());
			ps.setBigDecimal(index++, ma.getPaidCESS());
			ps.setBigDecimal(index++, ma.getWaivedCGST());
			ps.setBigDecimal(index++, ma.getWaivedSGST());
			ps.setBigDecimal(index++, ma.getWaivedIGST());
			ps.setBigDecimal(index++, ma.getWaivedUGST());
			ps.setBigDecimal(index++, ma.getWaivedCESS());
			ps.setString(index++, ma.getFinSource());
			ps.setBoolean(index++, ma.isDueCreation());
			ps.setLong(index++, ma.getLinkedTranId());
			ps.setString(index++, ma.getStatus());
			ps.setString(index++, ma.getReason());
			ps.setTimestamp(index++, ma.getLastMntOn());
			ps.setString(index++, ma.getRecordStatus());
			ps.setString(index++, ma.getRoleCode());
			ps.setString(index++, ma.getNextRoleCode());
			ps.setString(index++, ma.getTaskId());
			ps.setString(index++, ma.getNextTaskId());
			ps.setString(index++, ma.getRecordType());
			ps.setLong(index++, ma.getWorkflowId());

			ps.setLong(index, ma.getAdviseID());

		});

		if (recordCount == 0) {
			throw new ConcurrencyException();
		}
	}

	@Override
	public void delete(ManualAdvise ma, TableType tableType) {
		StringBuilder sql = new StringBuilder("Delete From ManualAdvise");
		sql.append(tableType.getSuffix());
		sql.append(" Where AdviseID = ?");
		sql.append(QueryUtil.getConcurrencyClause(tableType));

		logger.debug(Literal.SQL.concat(sql.toString()));

		try {
			int recordCount = jdbcOperations.update(sql.toString(), ps -> {
				int index = 1;

				ps.setLong(index++, ma.getAdviseID());
				if (tableType == TableType.TEMP_TAB) {
					ps.setTimestamp(index, ma.getPrevMntOn());
				} else {
					ps.setLong(index, ma.getVersion() - 1);
				}
			});

			if (recordCount == 0) {
				throw new ConcurrencyException();
			}

		} catch (DataAccessException e) {
			throw new DependencyFoundException(e);
		}

	}

	@Override
	public void deleteByAdviseId(ManualAdvise ma, TableType tableType) {
		StringBuilder sql = new StringBuilder("Delete From ManualAdvise");
		sql.append(tableType.getSuffix());
		sql.append(" Where AdviseID = ?");

		logger.debug(Literal.SQL.concat(sql.toString()));

		jdbcOperations.update(sql.toString(), ma.getAdviseID());
	}

	@Override
	public void updateAdvPayment(ManualAdvise ma, TableType tableType) {
		StringBuilder sql = new StringBuilder("Update ManualAdvise");
		sql.append(tableType.getSuffix());
		sql.append(" Set PaidAmount = PaidAmount + ?, WaivedAmount = WaivedAmount + ?, ReservedAmt = ReservedAmt + ?");
		sql.append(", BalanceAmt = BalanceAmt + ?, PaidCGST = PaidCGST + ?, PaidSGST = PaidSGST + ?");
		sql.append(", PaidUGST = PaidUGST + ?, PaidIGST = PaidIGST + ?, PaidCESS = PaidCESS + ?");
		sql.append(", WaivedCGST = WaivedCGST + ?, WaivedSGST = WaivedSGST + ?, WaivedUGST = WaivedUGST + ?");
		sql.append(", WaivedIGST = WaivedIGST + ?, WaivedCESS = WaivedCESS + ?, TdsPaid = TdsPaid + ?");
		sql.append(" Where AdviseID = ?");

		logger.debug(Literal.SQL.concat(sql.toString()));

		jdbcOperations.update(sql.toString(), ps -> {
			int index = 1;

			ps.setBigDecimal(index++, ma.getPaidAmount());
			ps.setBigDecimal(index++, ma.getWaivedAmount());
			ps.setBigDecimal(index++, ma.getReservedAmt());
			ps.setBigDecimal(index++, ma.getBalanceAmt());
			ps.setBigDecimal(index++, ma.getPaidCGST());
			ps.setBigDecimal(index++, ma.getPaidSGST());
			ps.setBigDecimal(index++, ma.getPaidUGST());
			ps.setBigDecimal(index++, ma.getPaidIGST());
			ps.setBigDecimal(index++, ma.getPaidCESS());
			ps.setBigDecimal(index++, ma.getWaivedCGST());
			ps.setBigDecimal(index++, ma.getWaivedSGST());
			ps.setBigDecimal(index++, ma.getWaivedUGST());
			ps.setBigDecimal(index++, ma.getWaivedIGST());
			ps.setBigDecimal(index++, ma.getWaivedCESS());
			ps.setBigDecimal(index++, ma.getTdsPaid());

			ps.setLong(index, ma.getAdviseID());
		});
	}

	@Override
	public void saveMovement(ManualAdviseMovements mam, String type) {
		if (mam.getMovementID() <= 0) {
			mam.setMovementID(getNextValue("SeqManualAdviseMovements"));
		}

		StringBuilder sql = new StringBuilder("Insert Into ManualAdviseMovements");
		sql.append(StringUtils.trimToEmpty(type));
		sql.append(" (MovementID, AdviseID, MovementDate, MovementAmount, PaidAmount, WaivedAmount, Status");
		sql.append(", ReceiptID, ReceiptSeqID, WaiverID, TaxHeaderId, TdsPaid");
		sql.append(", PaidCGST, PaidSGST, PaidIGST, PaidUGST, PaidCESS");
		sql.append(", WaivedCGST, WaivedSGST, WaivedIGST, WaivedUGST, WaivedCESS");
		sql.append(") Values(");
		sql.append(" ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?");
		sql.append(")");

		logger.debug(Literal.SQL.concat(sql.toString()));

		jdbcOperations.update(sql.toString(), ps -> {
			int index = 1;

			ps.setLong(index++, mam.getMovementID());
			ps.setLong(index++, mam.getAdviseID());
			ps.setDate(index++, JdbcUtil.getDate(mam.getMovementDate()));
			ps.setBigDecimal(index++, mam.getMovementAmount());
			ps.setBigDecimal(index++, mam.getPaidAmount());
			ps.setBigDecimal(index++, mam.getWaivedAmount());
			ps.setString(index++, mam.getStatus());
			ps.setLong(index++, mam.getReceiptID());
			ps.setLong(index++, mam.getReceiptSeqID());
			ps.setLong(index++, mam.getWaiverID());
			ps.setObject(index++, mam.getTaxHeaderId());
			ps.setBigDecimal(index++, mam.getTdsPaid());
			ps.setBigDecimal(index++, mam.getPaidCGST());
			ps.setBigDecimal(index++, mam.getPaidSGST());
			ps.setBigDecimal(index++, mam.getPaidIGST());
			ps.setBigDecimal(index++, mam.getPaidUGST());
			ps.setBigDecimal(index++, mam.getPaidCESS());
			ps.setBigDecimal(index++, mam.getWaivedCGST());
			ps.setBigDecimal(index++, mam.getWaivedSGST());
			ps.setBigDecimal(index++, mam.getWaivedIGST());
			ps.setBigDecimal(index++, mam.getWaivedUGST());
			ps.setBigDecimal(index, mam.getWaivedCESS());
		});
	}

	@Override
	public List<ManualAdviseMovements> getAdviseMovementsByReceipt(long receiptID, String type) {
		StringBuilder sql = new StringBuilder("Select");
		sql.append(" MovementID, AdviseID, MovementDate, MovementAmount, PaidAmount, WaivedAmount");
		sql.append(", Status, ReceiptID, ReceiptSeqID, TaxHeaderId");
		sql.append(" From ManualAdviseMovements");
		sql.append(StringUtils.trimToEmpty(type));
		sql.append(" Where ReceiptID = ?");

		logger.debug(Literal.SQL.concat(sql.toString()));

		return this.jdbcOperations.query(sql.toString(), (rs, rowNum) -> {
			ManualAdviseMovements ma = new ManualAdviseMovements();

			ma.setMovementID(rs.getLong("MovementID"));
			ma.setAdviseID(rs.getLong("AdviseID"));
			ma.setMovementDate(rs.getTimestamp("MovementDate"));
			ma.setMovementAmount(rs.getBigDecimal("MovementAmount"));
			ma.setPaidAmount(rs.getBigDecimal("PaidAmount"));
			ma.setWaivedAmount(rs.getBigDecimal("WaivedAmount"));
			ma.setStatus(rs.getString("Status"));
			ma.setReceiptID(rs.getLong("ReceiptID"));
			ma.setReceiptSeqID(rs.getLong("ReceiptSeqID"));
			ma.setTaxHeaderId(JdbcUtil.getLong(rs.getObject("TaxHeaderId")));

			return ma;
		}, receiptID);
	}

	@Override
	public List<ManualAdviseMovements> getAdviseMovements(long id) {
		StringBuilder sql = new StringBuilder("Select");
		sql.append(" mam.MovementID, mam.MovementDate, mam.MovementAmount");
		sql.append(", mam.PaidAmount, mam.WaivedAmount, mam.Status, rh.ReceiptMode, mam.TaxHeaderId");
		sql.append(", mam.PaidCGST, mam.PaidSGST, mam.PaidUGST, mam.PaidIGST, mam.PaidCESS");
		sql.append(", mam.WaivedCGST, mam.WaivedSGST, mam.WaivedUGST, mam.WaivedIGST, mam.WaivedCESS, ft.TaxComponent");
		sql.append(" From ManualAdviseMovements mam");
		sql.append(" Left Join FinReceiptHeader rh ON rh.ReceiptID = mam.ReceiptID");
		sql.append(" Left Join ManualAdvise ma ON ma.AdviseID = mam.AdviseID");
		sql.append(" Left Join FeeTypes ft ON ft.FeeTypeId = ma.FeeTypeId");
		sql.append(" Where mam.AdviseID = ?");

		logger.debug(Literal.SQL.concat(sql.toString()));

		return this.jdbcOperations.query(sql.toString(), ps -> {
			int index = 1;
			ps.setLong(index++, id);
		}, (rs, rowNum) -> {
			ManualAdviseMovements mam = new ManualAdviseMovements();

			mam.setMovementID(rs.getLong("MovementID"));
			mam.setMovementDate(rs.getTimestamp("MovementDate"));
			mam.setMovementAmount(rs.getBigDecimal("MovementAmount"));
			mam.setPaidAmount(rs.getBigDecimal("PaidAmount"));
			mam.setWaivedAmount(rs.getBigDecimal("WaivedAmount"));
			mam.setStatus(rs.getString("Status"));
			mam.setReceiptMode(rs.getString("ReceiptMode"));
			mam.setTaxHeaderId(JdbcUtil.getLong(rs.getObject("TaxHeaderId")));
			mam.setPaidCGST(rs.getBigDecimal("PaidCGST"));
			mam.setPaidSGST(rs.getBigDecimal("PaidSGST"));
			mam.setPaidUGST(rs.getBigDecimal("PaidUGST"));
			mam.setPaidIGST(rs.getBigDecimal("PaidIGST"));
			mam.setPaidCESS(rs.getBigDecimal("PaidCESS"));
			mam.setWaivedCGST(rs.getBigDecimal("WaivedCGST"));
			mam.setWaivedSGST(rs.getBigDecimal("WaivedSGST"));
			mam.setWaivedUGST(rs.getBigDecimal("WaivedUGST"));
			mam.setWaivedIGST(rs.getBigDecimal("WaivedIGST"));
			mam.setWaivedCESS(rs.getBigDecimal("WaivedCESS"));
			mam.setTaxComponent(rs.getString("TaxComponent"));

			return mam;
		});
	}

	@Override
	public void deleteMovementsByReceiptID(long receiptID, String type) {
		StringBuilder sql = new StringBuilder("Delete From ManualAdviseMovements");
		sql.append(StringUtils.trimToEmpty(type));
		sql.append(" Where ReceiptID = ? ");

		logger.debug(Literal.SQL.concat(sql.toString()));

		jdbcOperations.update(sql.toString(), receiptID);
	}

	@Override
	public List<ManualAdviseMovements> getAdvMovementsByReceiptSeq(long receiptID, long receiptSeqID, int adviseType,
			String type) {
		StringBuilder sql = new StringBuilder("Select");
		sql.append(" MovementID, AdviseID, MovementDate, MovementAmount, PaidAmount, WaivedAmount");
		sql.append(", Status, ReceiptID, ReceiptSeqID, TaxHeaderId");

		if (StringUtils.contains(type, "View")) {
			sql.append(", FeeTypeCode, FeeTypeDesc, TaxApplicable, TaxComponent");
		}

		sql.append(" From ManualAdviseMovements");
		sql.append(StringUtils.trimToEmpty(type));
		sql.append(" Where ReceiptID = ? and ReceiptSeqID = ?");

		if (StringUtils.contains(type, "View")) {
			sql.append(" and AdviseType = ?");
		}

		logger.debug(Literal.SQL.concat(sql.toString()));

		return this.jdbcOperations.query(sql.toString(), ps -> {
			int index = 1;
			ps.setLong(index++, receiptID);
			ps.setLong(index++, receiptSeqID);

			if (StringUtils.contains(type, "View")) {
				ps.setInt(index, adviseType);
			}
		}, (rs, rowNum) -> {
			ManualAdviseMovements mam = new ManualAdviseMovements();

			mam.setMovementID(rs.getLong("MovementID"));
			mam.setAdviseID(rs.getLong("AdviseID"));
			mam.setMovementDate(rs.getTimestamp("MovementDate"));
			mam.setMovementAmount(rs.getBigDecimal("MovementAmount"));
			mam.setPaidAmount(rs.getBigDecimal("PaidAmount"));
			mam.setWaivedAmount(rs.getBigDecimal("WaivedAmount"));
			mam.setStatus(rs.getString("Status"));
			mam.setReceiptID(rs.getLong("ReceiptID"));
			mam.setReceiptSeqID(rs.getLong("ReceiptSeqID"));
			mam.setTaxHeaderId(JdbcUtil.getLong(rs.getObject("TaxHeaderId")));

			if (StringUtils.contains(type, "View")) {
				mam.setFeeTypeCode(rs.getString("FeeTypeCode"));
				mam.setFeeTypeDesc(rs.getString("FeeTypeDesc"));
				mam.setTaxApplicable(rs.getBoolean("TaxApplicable"));
				mam.setTaxComponent(rs.getString("TaxComponent"));
			}

			return mam;
		});
	}

	@Override
	public void updateMovementStatus(long receiptID, long receiptSeqID, String status, String type) {
		StringBuilder sql = new StringBuilder("Update ManualAdviseMovements");
		sql.append(StringUtils.trimToEmpty(type));
		sql.append(" Set Status = ?");
		sql.append(" Where ReceiptID = ? and ReceiptSeqID = ?");

		logger.debug(Literal.SQL.concat(sql.toString()));

		jdbcOperations.update(sql.toString(), ps -> {
			int index = 1;

			ps.setString(index++, status);
			ps.setLong(index++, receiptID);
			ps.setLong(index, receiptSeqID);
		});
	}

	@Override
	public List<ManualAdviseReserve> getPayableReserveList(long receiptSeqID) {
		String sql = "Select ReceiptSeqID, AdviseID, ReservedAmt From ManualAdviseReserve Where ReceiptSeqID = ?";

		logger.debug(Literal.SQL.concat(sql));

		return this.jdbcOperations.query(sql, (rs, rowNum) -> {
			ManualAdviseReserve ma = new ManualAdviseReserve();

			ma.setReceiptSeqID(rs.getLong("ReceiptSeqID"));
			ma.setAdviseID(rs.getLong("AdviseID"));
			ma.setReservedAmt(rs.getBigDecimal("ReservedAmt"));

			return ma;
		}, receiptSeqID);
	}

	@Override
	public ManualAdviseReserve getPayableReserve(long receiptSeqID, long payAgainstID) {
		String sql = "Select ReceiptSeqID, AdviseID, ReservedAmt From ManualAdviseReserve Where ReceiptSeqID = ? and AdviseID= ?";

		logger.debug(Literal.SQL.concat(sql));

		try {
			return this.jdbcOperations.queryForObject(sql.toString(), (rs, rowNum) -> {
				ManualAdviseReserve mar = new ManualAdviseReserve();

				mar.setReceiptSeqID(rs.getLong("ReceiptSeqID"));
				mar.setAdviseID(rs.getLong("AdviseID"));
				mar.setReservedAmt(rs.getBigDecimal("ReservedAmt"));

				return mar;
			}, receiptSeqID, payAgainstID);
		} catch (EmptyResultDataAccessException e) {
			logger.warn(Message.NO_RECORD_FOUND);
			return null;
		}
	}

	@Override
	public void savePayableReserveLog(long receiptSeqID, long payAgainstID, BigDecimal reserveAmt) {
		String sql = "Insert Into ManualAdviseReserve (AdviseID, ReceiptSeqID, ReservedAmt) Values(?, ?, ?)";

		logger.debug(Literal.SQL.concat(sql));

		this.jdbcOperations.update(sql, ps -> {
			int index = 1;

			ps.setLong(index++, payAgainstID);
			ps.setLong(index++, receiptSeqID);
			ps.setBigDecimal(index, reserveAmt);
		});
	}

	@Override
	public void updatePayableReserveLog(long receiptID, long payAgainstID, BigDecimal diffInReserve) {
		String sql = "Update ManualAdviseReserve Set ReservedAmt = ReservedAmt + ? Where ReceiptSeqID = ? and AdviseID = ?";

		logger.debug(Literal.SQL.concat(sql));

		int recordCount = this.jdbcOperations.update(sql, ps -> {
			int index = 1;

			ps.setBigDecimal(index++, diffInReserve);
			ps.setLong(index++, receiptID);
			ps.setLong(index, payAgainstID);
		});

		if (recordCount <= 0) {
			throw new ConcurrencyException();
		}
	}

	@Override
	public void deletePayableReserve(long receiptID, long payAgainstID) {
		StringBuilder sql = new StringBuilder("Delete From ManualAdviseReserve");
		sql.append(" Where ReceiptSeqID = ?");
		if (payAgainstID != 0) {
			sql.append(" and AdviseID = ?");
		}

		logger.debug(Literal.SQL.concat(sql.toString()));

		this.jdbcOperations.update(sql.toString(), ps -> {
			int index = 1;

			ps.setLong(index++, receiptID);

			if (payAgainstID != 0) {
				ps.setLong(index, payAgainstID);
			}
		});
	}

	@Override
	public void updatePayableReserve(long payAgainstID, BigDecimal reserveAmt) {
		String sql = "Update ManualAdvise Set ReservedAmt = ReservedAmt + ?, BalanceAmt = BalanceAmt - ? Where AdviseID = ? and BalanceAmt >= ?";

		logger.debug(Literal.SQL.concat(sql));

		int recordCount = this.jdbcOperations.update(sql.toString(), ps -> {
			int index = 1;

			ps.setBigDecimal(index++, reserveAmt);
			ps.setBigDecimal(index++, reserveAmt);
			ps.setLong(index++, payAgainstID);
			ps.setBigDecimal(index, reserveAmt);
		});

		if (recordCount <= 0) {
			throw new ConcurrencyException();
		}
	}

	@Override
	public void updatePayableReserveAmount(long payAgainstID, BigDecimal reserveAmt) {
		String sql = "Update ManualAdvise Set ReservedAmt = ReservedAmt + ?, BalanceAmt = BalanceAmt - ? Where AdviseID = ? ";

		logger.debug(Literal.SQL.concat(sql));

		int recordCount = this.jdbcOperations.update(sql.toString(), ps -> {
			int index = 1;

			ps.setBigDecimal(index++, reserveAmt);
			ps.setBigDecimal(index++, reserveAmt);
			ps.setLong(index, payAgainstID);
		});

		if (recordCount <= 0) {
			throw new ConcurrencyException();
		}
	}

	@Override
	public void updateUtilise(long adviseID, BigDecimal amount, boolean noManualReserve) {
		StringBuilder sql = new StringBuilder("Update ManualAdvise");
		sql.append(" Set PaidAmount = PaidAmount + ?");

		if (!noManualReserve) {
			sql.append(", ReservedAmt = ReservedAmt - ?");
		}

		sql.append(" Where AdviseID = ? and ReservedAmt >= ?");

		logger.debug(Literal.SQL.concat(sql.toString()));

		int recordCount = this.jdbcOperations.update(sql.toString(), ps -> {
			int index = 1;

			ps.setBigDecimal(index++, amount);

			if (!noManualReserve) {
				ps.setBigDecimal(index++, amount);
			}

			ps.setLong(index++, adviseID);
			ps.setBigDecimal(index, amount);
		});

		if (recordCount <= 0) {
			throw new ConcurrencyException();
		}
	}

	@Override
	public void reverseUtilise(long adviseID, BigDecimal amount) {
		String sql = "Update ManualAdvise Set PaidAmount = PaidAmount - ?, BalanceAmt = BalanceAmt + ?, HoldDue = ? Where AdviseID = ?";

		logger.debug(Literal.SQL.concat(sql.toString()));

		int recordCount = this.jdbcOperations.update(sql, ps -> {
			int index = 1;

			ps.setBigDecimal(index++, amount);
			ps.setBigDecimal(index++, amount);
			ps.setInt(index++, 0);
			ps.setLong(index, adviseID);
		});

		if (recordCount <= 0) {
			throw new ConcurrencyException();
		}
	}

	@Override
	public Date getPresentmentBounceDueDate(long receiptId) {
		StringBuilder sql = new StringBuilder("Select schd.SchDate From ManualAdvise ma");
		sql.append(" Inner Join PresentmentDetails pd on pd.ReceiptId = ma.ReceiptId");
		sql.append(" Inner Join FinscheduleDetails schd on schd.FinID = pd.FinID and schd.Schdate = pd.Schdate");
		sql.append(" Where ma.AdviseType <> 2 and ma.AdviseAmount > 0 and ma.ReceiptId = ?");
		sql.append(" and FeeTypeId not in (Select FeeTypeId From FeeTypes)");

		logger.debug(Literal.SQL.concat(sql.toString()));

		try {
			return this.jdbcOperations.queryForObject(sql.toString(), Date.class, receiptId);
		} catch (EmptyResultDataAccessException ede) {
			logger.warn(Message.NO_RECORD_FOUND);
			return null;
		}
	}

	@Override
	public List<Long> getBounceAdvisesListByRef(long finID, int adviseType, String type) {
		StringBuilder sql = new StringBuilder("Select AdviseId");
		sql.append(" From ManualAdvise");
		sql.append(type);
		sql.append(" Where FinID = ? and AdviseType = ? and BounceId > 0");

		logger.debug(Literal.SQL.concat(sql.toString()));

		return jdbcOperations.queryForList(sql.toString(), Long.class, finID, adviseType);
	}

	@Override
	public FinanceMain getFinanceDetails(long finID) {
		StringBuilder sql = new StringBuilder("Select");
		sql.append(" fm.FinID, fm.FinReference, ft.FinType, ft.FinTypeDesc LovDescFinTypeName, fm.FinBranch");
		sql.append(", fm.CustId, cu.CustCIF LovDescCustCif, cu.CustShrtName LovDescCustShrtName, SD.EntityCode");
		sql.append(", fm.FinAssetValue, fm.FinStartDate, fm.MaturityDate, fm.FinCcy, fm.TDSApplicable, fm.TdsType");
		sql.append(", fm.FinIsActive, fm.ScheduleMethod, fm.ProfitDaysBasis");
		sql.append(" From FinanceMain fm");
		sql.append(" Inner Join Customers cu on fm.CustID = cu.CustID");
		sql.append(" Inner Join RmtFinanceTypes ft on ft.FinType = fm.FinType");
		sql.append(" Inner Join SMTDivisiondetail sd On sd.DivisionCode = ft.FinDivision");
		sql.append(" Where fm.FinID = ?");

		logger.debug(Literal.SQL.concat(sql.toString()));

		try {
			return this.jdbcOperations.queryForObject(sql.toString(), (rs, rowNum) -> {
				FinanceMain fm = new FinanceMain();

				fm.setFinID(rs.getLong("FinID"));
				fm.setFinReference(rs.getString("FinReference"));
				fm.setFinType(rs.getString("FinType"));
				fm.setLovDescFinTypeName(rs.getString("LovDescFinTypeName"));
				fm.setFinBranch(rs.getString("FinBranch"));
				fm.setCustID(rs.getLong("CustID"));
				fm.setLovDescCustCIF(rs.getString("LovDescCustCIF"));
				fm.setLovDescCustShrtName(rs.getString("LovDescCustShrtName"));
				fm.setEntityCode(rs.getString("EntityCode"));
				fm.setFinAssetValue(rs.getBigDecimal("FinAssetValue"));
				fm.setFinStartDate(rs.getTimestamp("FinStartDate"));
				fm.setMaturityDate(rs.getTimestamp("MaturityDate"));
				fm.setFinCcy(rs.getString("FinCcy"));
				fm.setTDSApplicable(rs.getBoolean("TDSApplicable"));
				fm.setTdsType(rs.getString("TdsType"));
				fm.setFinIsActive(rs.getBoolean("FinIsActive"));
				fm.setScheduleMethod(rs.getString("ScheduleMethod"));
				fm.setProfitDaysBasis(rs.getString("ProfitDaysBasis"));

				return fm;
			}, finID);
		} catch (EmptyResultDataAccessException e) {
			logger.warn(Message.NO_RECORD_FOUND);
			return null;
		}
	}

	public List<ManualAdvise> getAMZManualAdviseDetails(long finID, String type) {
		StringBuilder sql = new StringBuilder("Select");
		sql.append(" ma.AdviseID, ma.AdviseType, ma.FinID, ma.FinReference, ma.FeeTypeID, ma.Sequence");
		sql.append(", ma.AdviseAmount, ma.BounceID, ma.ReceiptID, ma.PaidAmount, ma.WaivedAmount");
		sql.append(", ma.ValueDate, ma.PostDate, ma.ReservedAmt, ma.BalanceAmt");
		sql.append(", ma.PaidCGST, ma.PaidSGST, ma.PaidUGST, ma.PaidIGST, ma.PaidCESS");
		sql.append(", ma.WaivedCGST, ma.WaivedSGST, ma.WaivedUGST, ma.WaivedIGST, ma.WaivedCESS");
		sql.append(", ma.FinSource, ma.DueCreation");
		sql.append(" From ManualAdvise ma");
		sql.append(" Inner Join FeeTypes ft on ft.FeeTypeID = ma.FeeTypeID and ft.AmortzReq = 1");
		sql.append(StringUtils.trimToEmpty(type));
		sql.append(" Where ma.AdviseType = ? and am.FinID = ?");

		logger.debug(Literal.SQL.concat(sql.toString()));

		return jdbcOperations.query(sql.toString(), ps -> {
			ps.setInt(1, 1);
			ps.setLong(2, finID);
		}, (rs, rowNum) -> {
			ManualAdvise ma = new ManualAdvise();

			ma.setAdviseID(rs.getLong("AdviseID"));
			ma.setAdviseType(rs.getInt("AdviseType"));
			ma.setFinID(rs.getLong("FinID"));
			ma.setFinReference(rs.getString("FinReference"));
			ma.setFeeTypeID(rs.getLong("FeeTypeID"));
			ma.setSequence(rs.getInt("Sequence"));
			ma.setAdviseAmount(rs.getBigDecimal("AdviseAmount"));
			ma.setReservedAmt(rs.getBigDecimal("ReservedAmt"));
			ma.setBalanceAmt(rs.getBigDecimal("BalanceAmt"));
			ma.setPaidCGST(rs.getBigDecimal("PaidCGST"));
			ma.setPaidSGST(rs.getBigDecimal("PaidSGST"));
			ma.setPaidUGST(rs.getBigDecimal("PaidUGST"));
			ma.setPaidIGST(rs.getBigDecimal("PaidIGST"));
			ma.setPaidCESS(rs.getBigDecimal("PaidCESS"));
			ma.setWaivedCGST(rs.getBigDecimal("WaivedCGST"));
			ma.setWaivedSGST(rs.getBigDecimal("WaivedSGST"));
			ma.setWaivedUGST(rs.getBigDecimal("WaivedUGST"));
			ma.setWaivedIGST(rs.getBigDecimal("WaivedIGST"));
			ma.setWaivedCESS(rs.getBigDecimal("WaivedCESS"));
			ma.setFinSource(rs.getString("FinSource"));
			ma.setDueCreation(rs.getBoolean("DueCreation"));

			return ma;
		});
	}

	@Override
	public BigDecimal getBalanceAmt(long finID, Date valueDate) {
		String sql = "Select sum(AdviseAmount - PaidAmount - WaivedAmount) From manualAdvise Where FinID = ? and AdviseType = ? and ValueDate <= ? and (Status is null OR Status = ?)";

		logger.debug(Literal.SQL.concat(sql));

		Object obj = new Object[] { finID, AdviseType.RECEIVABLE.id(), valueDate,
				PennantConstants.MANUALADVISE_MAINTAIN };

		try {
			return this.jdbcOperations.queryForObject(sql, BigDecimal.class, obj);
		} catch (Exception e) {
			logger.warn(Message.NO_RECORD_FOUND);
			return BigDecimal.ZERO;
		}
	}

	@Override
	public String getTaxComponent(long adviseID, String type) {
		StringBuilder sql = new StringBuilder("Select TaxComponent");
		sql.append(" From ManualAdvise");
		sql.append(type);
		sql.append(" Where adviseID = ?");

		logger.debug(Literal.SQL.concat(sql.toString()));

		try {
			return this.jdbcOperations.queryForObject(sql.toString(), String.class, adviseID);
		} catch (EmptyResultDataAccessException e) {
			logger.warn(Message.NO_RECORD_FOUND);
			return null;
		}
	}

	@Override
	public List<ManualAdviseMovements> getDMAdviseMovementsByFinRef(long finID, String type) {
		StringBuilder sql = new StringBuilder("Select");
		sql.append(" mam.MovementID, mam.AdviseID, mam.MovementDate");
		sql.append(", mam.MovementAmount, mam.PaidAmount, mam.WaivedAmount");
		sql.append(", mam.Status, mam.ReceiptID, mam.ReceiptSeqID");
		sql.append(", mam.PaidCGST, mam.PaidSGST, mam.PaidUGST, mam.PaidIGST, mam.PaidCESS");
		sql.append(", mam.WaivedCGST, mam.WaivedSGST, mam.WaivedUGST, mam.WaivedIGST, mam.WaivedCESS, mam.TaxHeaderId");
		sql.append(" From ManualAdvise");
		sql.append(StringUtils.trim(type)).append(" ma");
		sql.append(" Inner Join ManualAdviseMovements");
		sql.append(StringUtils.trim(type)).append(" mam on ma.AdviseId = mam.AdviseId");
		sql.append(" Where FinID = ?");

		logger.debug(Literal.SQL.concat(sql.toString()));

		return this.jdbcOperations.query(sql.toString(), ps -> {
			ps.setLong(1, finID);
		}, (rs, rowNum) -> {
			ManualAdviseMovements ma = new ManualAdviseMovements();

			ma.setMovementID(rs.getLong("MovementID"));
			ma.setAdviseID(rs.getLong("AdviseID"));
			ma.setMovementDate(rs.getDate("MovementDate"));
			ma.setMovementAmount(rs.getBigDecimal("MovementAmount"));
			ma.setPaidAmount(rs.getBigDecimal("PaidAmount"));
			ma.setWaivedAmount(rs.getBigDecimal("WaivedAmount"));
			ma.setStatus(rs.getString("Status"));
			ma.setReceiptID(rs.getLong("ReceiptID"));
			ma.setReceiptSeqID(rs.getLong("ReceiptSeqID"));
			ma.setPaidCGST(rs.getBigDecimal("PaidCGST"));
			ma.setPaidSGST(rs.getBigDecimal("PaidSGST"));
			ma.setPaidUGST(rs.getBigDecimal("PaidUGST"));
			ma.setPaidIGST(rs.getBigDecimal("PaidIGST"));
			ma.setPaidCESS(rs.getBigDecimal("PaidCESS"));
			ma.setWaivedCGST(rs.getBigDecimal("WaivedCGST"));
			ma.setWaivedSGST(rs.getBigDecimal("WaivedSGST"));
			ma.setWaivedUGST(rs.getBigDecimal("WaivedUGST"));
			ma.setWaivedIGST(rs.getBigDecimal("WaivedIGST"));
			ma.setWaivedCESS(rs.getBigDecimal("WaivedCESS"));
			ma.setTaxHeaderId(JdbcUtil.getLong(rs.getObject("TaxHeaderId")));

			return ma;
		});
	}

	@Override
	public List<ManualAdvise> getManualAdvise(long finID) {
		StringBuilder sql = new StringBuilder("Select");
		sql.append(" ma.AdviseID, ma.AdviseType, ma.FeeTypeID, ma.Sequence, ma.FinID, ma.FinRefeRence");
		sql.append(", (ma.AdviseAmount - ma.PaidAmount - ma.WaivedAmount) BalanceAmt, ma.AdviseAmount");
		sql.append(", ma.PaidAmount, ma.WaivedAmount, ma.ValueDate, ma.PostDate, ma.BounceID, ma.ReceiptID");
		sql.append(", ma.ReservedAmt, ft.TaxComponent");
		sql.append(", ft.FeeTypeCode, ft.FeeTypeDesc, coalesce(ft.TaxApplicable, 0) TaxApplicable, ma.DueCreation");
		sql.append(", ma.PaidCGST, ma.PaidSGST, ma.PaidIGST, ma.PaidUGST, ma.PaidCESS, ma.TdsPaid");
		sql.append(", ma.WaivedCGST, ma.WaivedSGST, ma.WaivedIGST,  ma.WaivedUGST, ma.WaivedCESS");
		sql.append(", ma.Remarks, ma.FinSource, ma.LinkedTranId, ma.TdsPaid");
		sql.append(", ma.Version, ma.LastMntOn, ma.LastMntBy, ma.RecordStatus");
		sql.append(", ma.RoleCode, ma.NextRoleCode, ma.TaskId, ma.NextTaskId, ma.RecordType, ma.WorkflowId");
		sql.append(" From ManualAdvise_Aview ma");
		sql.append(" Left Join FeeTypes ft on ma.FeeTypeId = ft.FeeTypeId");
		sql.append(" Where FinID = ?  and ma.Advisetype = ?");
		sql.append(" and ma.ValueDate <= ? and ma.Status is null");

		logger.debug(Literal.SQL.concat(sql.toString()));

		List<ManualAdvise> maList = jdbcOperations.query(sql.toString(), ps -> {
			int index = 1;

			ps.setLong(index++, finID);
			ps.setInt(index++, AdviseType.RECEIVABLE.id());
			ps.setDate(index, JdbcUtil.getDate(SysParamUtil.getAppDate()));

		}, (rs, rowNum) -> {
			ManualAdvise ma = new ManualAdvise();

			ma.setAdviseID(rs.getLong("AdviseID"));
			ma.setAdviseType(rs.getInt("AdviseType"));
			ma.setFeeTypeID(rs.getInt("FeeTypeID"));
			ma.setSequence(rs.getInt("Sequence"));
			ma.setFinID(rs.getLong("FinID"));
			ma.setFinReference(rs.getString("FinRefeRence"));
			ma.setBalanceAmt(rs.getBigDecimal("BalanceAmt"));
			ma.setAdviseAmount(rs.getBigDecimal("AdviseAmount"));
			ma.setPaidAmount(rs.getBigDecimal("PaidAmount"));
			ma.setWaivedAmount(rs.getBigDecimal("WaivedAmount"));
			ma.setValueDate(rs.getDate("ValueDate"));
			ma.setPostDate(rs.getDate("PostDate"));
			ma.setBounceID(JdbcUtil.getLong(rs.getLong("BounceID")));
			ma.setReceiptID(JdbcUtil.getLong(rs.getObject("ReceiptID")));
			ma.setReservedAmt(rs.getBigDecimal("ReservedAmt"));
			ma.setTaxComponent(rs.getString("TaxComponent"));
			ma.setFeeTypeCode(rs.getString("FeeTypeCode"));
			ma.setFeeTypeDesc(rs.getString("FeeTypeDesc"));
			ma.setTaxApplicable(rs.getBoolean("TaxApplicable"));
			ma.setDueCreation(rs.getBoolean("DueCreation"));
			ma.setPaidCGST(rs.getBigDecimal("PaidCGST"));
			ma.setPaidSGST(rs.getBigDecimal("PaidSGST"));
			ma.setPaidIGST(rs.getBigDecimal("PaidIGST"));
			ma.setPaidUGST(rs.getBigDecimal("PaidUGST"));
			ma.setPaidCESS(rs.getBigDecimal("PaidCESS"));
			ma.setTdsPaid(rs.getBigDecimal("TdsPaid"));
			ma.setWaivedCGST(rs.getBigDecimal("WaivedCGST"));
			ma.setWaivedSGST(rs.getBigDecimal("WaivedSGST"));
			ma.setWaivedIGST(rs.getBigDecimal("WaivedIGST"));
			ma.setWaivedCESS(rs.getBigDecimal("WaivedCESS"));
			ma.setRemarks(rs.getString("Remarks"));
			ma.setFinSource(rs.getString("FinSource"));
			ma.setLinkedTranId(JdbcUtil.getLong(rs.getObject("LinkedTranId")));
			ma.setVersion(rs.getInt("Version"));
			ma.setLastMntOn(rs.getTimestamp("LastMntOn"));
			ma.setLastMntBy(rs.getLong("LastMntBy"));
			ma.setRecordStatus(rs.getString("RecordStatus"));
			ma.setRoleCode(rs.getString("RoleCode"));
			ma.setNextRoleCode(rs.getString("NextRoleCode"));
			ma.setTaskId(rs.getString("TaskId"));
			ma.setNextTaskId(rs.getString("NextTaskId"));
			ma.setRecordType(rs.getString("RecordType"));
			ma.setWorkflowId(rs.getLong("WorkflowId"));

			return ma;

		});

		return maList.stream().sorted((l1, l2) -> Long.compare(l1.getAdviseID(), l2.getAdviseID()))
				.collect(Collectors.toList());
	}

	@Override
	public List<ManualAdvise> getManualAdviseByRefAndFeeId(int adviseType, long feeTypeID) {
		StringBuilder sql = new StringBuilder("Select");
		sql.append(" AdviseID, AdviseType, FinID, FinReference, FeeTypeID, Sequence, AdviseAmount, BounceID");
		sql.append(", ReceiptID, PaidAmount, WaivedAmount, Remarks, ValueDate, PostDate, ReservedAmt");
		sql.append(", BalanceAmt, Version, LastMntOn, LastMntBy, RecordStatus, RoleCode, NextRoleCode");
		sql.append(", TaskId, NextTaskId, RecordType, WorkflowId, DueCreation");
		sql.append(" From ManualAdvise");
		sql.append(" Where  AdviseType = ? and FeeTypeID = ?");

		logger.debug(Literal.SQL.concat(sql.toString()));

		return this.jdbcOperations.query(sql.toString(), (rs, rowNum) -> {
			ManualAdvise ma = new ManualAdvise();

			ma.setAdviseID(rs.getLong("AdviseID"));
			ma.setAdviseType(rs.getInt("AdviseType"));
			ma.setFinID(rs.getLong("FinID"));
			ma.setFinReference(rs.getString("FinReference"));
			ma.setFeeTypeID(rs.getLong("FeeTypeID"));
			ma.setSequence(rs.getInt("Sequence"));
			ma.setAdviseAmount(rs.getBigDecimal("AdviseAmount"));
			ma.setBounceID(rs.getLong("BounceID"));
			ma.setReceiptID(rs.getLong("ReceiptID"));
			ma.setPaidAmount(rs.getBigDecimal("PaidAmount"));
			ma.setWaivedAmount(rs.getBigDecimal("WaivedAmount"));
			ma.setRemarks(rs.getString("Remarks"));
			ma.setValueDate(rs.getTimestamp("ValueDate"));
			ma.setPostDate(rs.getTimestamp("PostDate"));
			ma.setReservedAmt(rs.getBigDecimal("ReservedAmt"));
			ma.setBalanceAmt(rs.getBigDecimal("BalanceAmt"));
			ma.setVersion(rs.getInt("Version"));
			ma.setLastMntOn(rs.getTimestamp("LastMntOn"));
			ma.setLastMntBy(rs.getLong("LastMntBy"));
			ma.setRecordStatus(rs.getString("RecordStatus"));
			ma.setRoleCode(rs.getString("RoleCode"));
			ma.setNextRoleCode(rs.getString("NextRoleCode"));
			ma.setTaskId(rs.getString("TaskId"));
			ma.setNextTaskId(rs.getString("NextTaskId"));
			ma.setRecordType(rs.getString("RecordType"));
			ma.setWorkflowId(rs.getLong("WorkflowId"));
			ma.setDueCreation(rs.getBoolean("DueCreation"));

			return ma;
		}, adviseType, feeTypeID);
	}

	@Override
	public void updatePaidAmountOnly(long adviseID, BigDecimal amount) {
		String sql = "Update ManualAdvise Set PaidAmount = PaidAmount + ?, BalanceAmt = BalanceAmt - ? Where AdviseID = ?";

		logger.debug(Literal.SQL.concat(sql));

		if (this.jdbcOperations.update(sql, amount, amount, adviseID) <= 0) {
			throw new ConcurrencyException();
		}
	}

	@Override
	public List<ManualAdvise> getManualAdviseByRefAndFeeCode(long finID, int adviseType, String feeTypeCode) {
		StringBuilder sql = new StringBuilder("Select");
		sql.append(" AdviseID, AdviseType, FinID, FinReference, FeeTypeID, Sequence, AdviseAmount, BounceID");
		sql.append(", ReceiptID, PaidAmount, WaivedAmount, Remarks, ValueDate, PostDate, ReservedAmt");
		sql.append(", BalanceAmt, FeeTypeCode, FeeTypeDesc, DueCreation, Version, LastMntOn, LastMntBy, RecordStatus");
		sql.append(", RoleCode, NextRoleCode, TaskId, NextTaskId, RecordType, WorkflowId, PresentmentId");
		sql.append(" From ManualAdvise_Aview");
		sql.append(" Where FinID = ? and AdviseType = ? and FeeTypeCode = ?");

		logger.debug(Literal.SQL.concat(sql.toString()));

		List<ManualAdvise> maList = this.jdbcOperations.query(sql.toString(), (rs, rowNum) -> {
			ManualAdvise ma = new ManualAdvise();

			ma.setAdviseID(rs.getLong("AdviseID"));
			ma.setAdviseType(rs.getInt("AdviseType"));
			ma.setFinID(rs.getLong("FinID"));
			ma.setFinReference(rs.getString("FinReference"));
			ma.setFeeTypeID(rs.getLong("FeeTypeID"));
			ma.setSequence(rs.getInt("Sequence"));
			ma.setAdviseAmount(rs.getBigDecimal("AdviseAmount"));
			ma.setBounceID(rs.getLong("BounceID"));
			ma.setReceiptID(rs.getLong("ReceiptID"));
			ma.setPaidAmount(rs.getBigDecimal("PaidAmount"));
			ma.setWaivedAmount(rs.getBigDecimal("WaivedAmount"));
			ma.setRemarks(rs.getString("Remarks"));
			ma.setValueDate(rs.getTimestamp("ValueDate"));
			ma.setPostDate(rs.getTimestamp("PostDate"));
			ma.setReservedAmt(rs.getBigDecimal("ReservedAmt"));
			ma.setBalanceAmt(rs.getBigDecimal("BalanceAmt"));
			ma.setFeeTypeCode(rs.getString("FeeTypeCode"));
			ma.setFeeTypeDesc(rs.getString("FeeTypeDesc"));
			ma.setDueCreation(rs.getBoolean("DueCreation"));
			ma.setVersion(rs.getInt("Version"));
			ma.setLastMntOn(rs.getTimestamp("LastMntOn"));
			ma.setLastMntBy(rs.getLong("LastMntBy"));
			ma.setRecordStatus(rs.getString("RecordStatus"));
			ma.setRoleCode(rs.getString("RoleCode"));
			ma.setNextRoleCode(rs.getString("NextRoleCode"));
			ma.setTaskId(rs.getString("TaskId"));
			ma.setNextTaskId(rs.getString("NextTaskId"));
			ma.setRecordType(rs.getString("RecordType"));
			ma.setWorkflowId(rs.getLong("WorkflowId"));
			ma.setPresentmentID(JdbcUtil.getLong(rs.getObject("PresentmentId")));

			return ma;
		}, finID, adviseType, feeTypeCode);

		return maList.stream().sorted((f1, f2) -> DateUtil.compare(f1.getValueDate(), f2.getValueDate()))
				.collect(Collectors.toList());
	}

	@Override
	public Date getManualAdviseDate(long finID, Date valueDate, String type, int adviseType) {
		StringBuilder sql = new StringBuilder("Select max(ValueDate)");
		sql.append(" From ManualAdvise");
		sql.append(type);
		sql.append(" Where  FinID = ? and AdviseType = ? and ValueDate > ?");
		sql.append(" and (AdviseAmount - PaidAmount - WaivedAmount) > 0");

		logger.debug(Literal.SQL.concat(sql.toString()));

		return jdbcOperations.queryForObject(sql.toString(), Date.class, finID, adviseType, valueDate);
	}

	@Override
	public List<ManualAdviseMovements> getInProcManualAdvMovmnts(List<Long> receiptList) {
		StringBuilder sql = new StringBuilder();
		sql.append(" Select AdviseID, sum(PaidAmount) PaidAmount, sum(WaivedAmount) WaivedAmount");
		sql.append(" From ManualAdviseMovements");
		sql.append(" Where ReceiptID in (");
		sql.append(JdbcUtil.getInCondition(receiptList));
		sql.append(") Group by AdviseID");

		logger.debug(Literal.SQL.concat(sql.toString()));

		List<ManualAdviseMovements> maList = this.jdbcOperations.query(sql.toString(), ps -> {
			int index = 1;

			for (Long receiptID : receiptList) {
				ps.setLong(index++, receiptID);
			}
		}, (rs, rowNum) -> {
			ManualAdviseMovements ma = new ManualAdviseMovements();

			ma.setAdviseID(rs.getLong("AdviseID"));
			ma.setPaidAmount(rs.getBigDecimal("PaidAmount"));
			ma.setWaivedAmount(rs.getBigDecimal("WaivedAmount"));

			return ma;
		});

		return maList.stream().sorted((l1, l2) -> Long.compare(l1.getAdviseID(), l2.getAdviseID()))
				.collect(Collectors.toList());
	}

	@Override
	public List<ManualAdviseMovements> getAdvMovementsByReceiptSeq(long receiptID, long receiptSeqID, String type) {
		StringBuilder sql = new StringBuilder("Select");
		sql.append(" MovementID, AdviseID, MovementDate, MovementAmount, PaidAmount, WaivedAmount");
		sql.append(", Status, ReceiptID, ReceiptSeqID, TaxHeaderId, TdsPaid");

		if (StringUtils.contains(type, "View")) {
			sql.append(", FeeTypeCode, FeeTypeDesc, TaxApplicable, TaxComponent");
		}

		sql.append(" From ManualAdviseMovements");
		sql.append(StringUtils.trimToEmpty(type));
		sql.append(" Where ReceiptID = ? and ReceiptSeqID = ?");

		logger.debug(Literal.SQL.concat(sql.toString()));

		return this.jdbcOperations.query(sql.toString(), (rs, rowNum) -> {
			ManualAdviseMovements mam = new ManualAdviseMovements();

			mam.setMovementID(rs.getLong("MovementID"));
			mam.setAdviseID(rs.getLong("AdviseID"));
			mam.setMovementDate(rs.getTimestamp("MovementDate"));
			mam.setMovementAmount(rs.getBigDecimal("MovementAmount"));
			mam.setPaidAmount(rs.getBigDecimal("PaidAmount"));
			mam.setWaivedAmount(rs.getBigDecimal("WaivedAmount"));
			mam.setStatus(rs.getString("Status"));
			mam.setReceiptID(rs.getLong("ReceiptID"));
			mam.setReceiptSeqID(rs.getLong("ReceiptSeqID"));
			mam.setTaxHeaderId(JdbcUtil.getLong(rs.getObject("TaxHeaderId")));
			mam.setTdsPaid(rs.getBigDecimal("TdsPaid"));

			if (StringUtils.contains(type, "View")) {
				mam.setFeeTypeCode(rs.getString("FeeTypeCode"));
				mam.setFeeTypeDesc(rs.getString("FeeTypeDesc"));
				mam.setTaxApplicable(rs.getBoolean("TaxApplicable"));
				mam.setTaxComponent(rs.getString("TaxComponent"));
			}

			return mam;
		}, receiptID, receiptSeqID);

	}

	@Override
	public ManualAdviseMovements getAdvMovByReceiptSeq(long receiptID, long receiptSeqID, String type) {
		StringBuilder sql = new StringBuilder("Select");
		sql.append(" MovementID, AdviseID, MovementDate, MovementAmount, PaidAmount, WaivedAmount");
		sql.append(", Status, ReceiptID, ReceiptSeqID, TaxHeaderId");

		if (StringUtils.contains(type, "View")) {
			sql.append(", FeeTypeCode, FeeTypeDesc, TaxApplicable, TaxComponent");
		}

		sql.append(" From ManualAdviseMovements");
		sql.append(StringUtils.trimToEmpty(type));
		sql.append(" Where ReceiptID = ? and ReceiptSeqID = ?");

		logger.debug(Literal.SQL.concat(sql.toString()));

		try {
			return this.jdbcOperations.queryForObject(sql.toString(), (rs, rowNum) -> {
				ManualAdviseMovements mam = new ManualAdviseMovements();

				mam.setMovementID(rs.getLong("MovementID"));
				mam.setAdviseID(rs.getLong("AdviseID"));
				mam.setMovementDate(rs.getTimestamp("MovementDate"));
				mam.setMovementAmount(rs.getBigDecimal("MovementAmount"));
				mam.setPaidAmount(rs.getBigDecimal("PaidAmount"));
				mam.setWaivedAmount(rs.getBigDecimal("WaivedAmount"));
				mam.setStatus(rs.getString("Status"));
				mam.setReceiptID(rs.getLong("ReceiptID"));
				mam.setReceiptSeqID(rs.getLong("ReceiptSeqID"));
				mam.setTaxHeaderId(JdbcUtil.getLong(rs.getObject("TaxHeaderId")));

				if (StringUtils.contains(type, "View")) {
					mam.setFeeTypeCode(rs.getString("FeeTypeCode"));
					mam.setFeeTypeDesc(rs.getString("FeeTypeDesc"));
					mam.setTaxApplicable(rs.getBoolean("TaxApplicable"));
					mam.setTaxComponent(rs.getString("TaxComponent"));
				}

				return mam;
			}, receiptID, receiptSeqID);
		} catch (EmptyResultDataAccessException e) {
			logger.warn(Message.NO_RECORD_FOUND);
			return null;
		}
	}

	@Override
	public List<ManualAdvise> getPreviousAdvPayments(long finID) {
		StringBuilder sql = new StringBuilder("Select");
		sql.append(" ma.FeeTypeID, ft.FeeTypeCode, ma.AdviseType, ma.AdviseAmount");
		sql.append(" From ManualAdvise ma");
		sql.append(" Inner Join FeeTypes ft on ft.FeeTypeID = ma.FeeTypeID");
		sql.append(" Where FinID = ? and FeeTypeCode in (?, ?)");

		logger.debug(Literal.SQL.concat(sql.toString()));

		return this.jdbcOperations.query(sql.toString(), ps -> {
			int index = 1;

			ps.setLong(index++, finID);
			ps.setString(index++, AdvanceRuleCode.ADVINT.name());
			ps.setString(index, AdvanceRuleCode.ADVEMI.name());

		}, (rs, rownum) -> {
			ManualAdvise ma = new ManualAdvise();

			ma.setFeeTypeID(rs.getLong("FeeTypeID"));
			ma.setFeeTypeCode(rs.getString("FeeTypeCode"));
			ma.setAdviseType(rs.getInt("AdviseType"));
			ma.setAdviseAmount(rs.getBigDecimal("AdviseAmount"));

			return ma;
		});
	}

	@Override
	public void saveDueTaxDetail(AdviseDueTaxDetail adt) {
		StringBuilder sql = new StringBuilder();
		sql.append("Insert Into AdviseDueTaxDetail");
		sql.append(" (AdviseID, Amount, TaxType , CGST , SGST , UGST , IGST , CESS, TotalGST, InvoiceID)");
		sql.append(" Values(?, ?, ?, ?, ?, ?, ?, ?, ?, ?)");

		logger.debug(Literal.SQL.concat(sql.toString()));

		this.jdbcOperations.update(sql.toString(), ps -> {
			int index = 1;

			ps.setLong(index++, adt.getAdviseID());
			ps.setBigDecimal(index++, adt.getAmount());
			ps.setString(index++, adt.getTaxType());
			ps.setBigDecimal(index++, adt.getCGST());
			ps.setBigDecimal(index++, adt.getSGST());
			ps.setBigDecimal(index++, adt.getUGST());
			ps.setBigDecimal(index++, adt.getIGST());
			ps.setBigDecimal(index++, adt.getCESS());
			ps.setBigDecimal(index++, adt.getTotalGST());
			ps.setObject(index, adt.getInvoiceID());
		});

	}

	@Override
	public boolean isAdviseDueCreated(long adviseID) {
		String sql = "Select count(AdviseID) From AdviseDueTaxDetail Where AdviseID = ?";

		logger.debug(Literal.SQL.concat(sql));

		return this.jdbcOperations.queryForObject(sql, Integer.class, adviseID) > 0;
	}

	@Override
	public Long getDebitInvoiceID(long adviseID) {
		String sql = "Select InvoiceID  From AdviseDueTaxDetail Where AdviseID = ? ";

		logger.debug(Literal.SQL.concat(sql));

		try {
			return this.jdbcOperations.queryForObject(sql, Long.class, adviseID);
		} catch (EmptyResultDataAccessException e) {
			logger.warn(Message.NO_RECORD_FOUND);
			return null;
		}
	}

	@Override
	public long getNewAdviseID() {
		return getNextValue("seqManualAdvise");
	}

	@Override
	public void updateUtiliseOnly(long adviseID, BigDecimal amount) {
		StringBuilder sql = new StringBuilder("Update ManualAdvise");
		sql.append(" Set PaidAmount = PaidAmount + ?, BalanceAmt = BalanceAmt - ?");
		sql.append(" Where AdviseID = ?");

		logger.debug(Literal.SQL.concat(sql.toString()));

		int recordCount = this.jdbcOperations.update(sql.toString(), ps -> {
			int index = 1;

			ps.setBigDecimal(index++, amount);
			ps.setBigDecimal(index++, amount);
			ps.setLong(index, adviseID);
		});

		if (recordCount <= 0) {
			throw new ConcurrencyException();
		}
	}

	@Override
	public ManualAdviseMovements getAdvMovByReceiptSeq(long receiptID, long receiptSeqID, long adviseId, String type) {
		StringBuilder sql = new StringBuilder("Select");
		sql.append(
				" mam.MovementID, mam.AdviseID, mam.MovementDate, mam.MovementAmount, mam.PaidAmount, mam.WaivedAmount");
		sql.append(", mam.Status, mam.ReceiptID, mam.ReceiptSeqID, mam.TaxHeaderId");
		sql.append(", ft.FeeTypeCode, ft.FeeTypeDesc, ft.TaxApplicable, ft.TaxComponent");
		sql.append(" from ManualAdviseMovements");
		sql.append(StringUtils.trimToEmpty(type));
		sql.append(" mam Inner Join ManualAdvise ma on mam.AdviseID = ma.AdviseID");
		sql.append(" Inner Join FeeTypes ft on ma.Feetypeid = ft.Feetypeid");
		sql.append(" Where mam.ReceiptID = ? and ReceiptSeqID = ? and mam.AdviseID = ?");

		logger.debug(Literal.SQL.concat(sql.toString()));

		try {
			return this.jdbcOperations.queryForObject(sql.toString(), (rs, rowNum) -> {
				ManualAdviseMovements mam = new ManualAdviseMovements();

				mam.setMovementID(rs.getLong("MovementID"));
				mam.setAdviseID(rs.getLong("AdviseID"));
				mam.setMovementDate(rs.getTimestamp("MovementDate"));
				mam.setMovementAmount(rs.getBigDecimal("MovementAmount"));
				mam.setPaidAmount(rs.getBigDecimal("PaidAmount"));
				mam.setWaivedAmount(rs.getBigDecimal("WaivedAmount"));
				mam.setStatus(rs.getString("Status"));
				mam.setReceiptID(rs.getLong("ReceiptID"));
				mam.setReceiptSeqID(rs.getLong("ReceiptSeqID"));
				mam.setTaxHeaderId(JdbcUtil.getLong(rs.getObject("TaxHeaderId")));
				mam.setFeeTypeCode(rs.getString("FeeTypeCode"));
				mam.setFeeTypeDesc(rs.getString("FeeTypeDesc"));
				mam.setTaxApplicable(rs.getBoolean("TaxApplicable"));
				mam.setTaxComponent(rs.getString("TaxComponent"));

				return mam;
			}, receiptID, receiptSeqID, adviseId);
		} catch (EmptyResultDataAccessException e) {
			logger.warn(Message.NO_RECORD_FOUND);
			return null;
		}
	}

	@Override
	public BigDecimal getReceivableAmt(long finID, boolean isBounce) {
		StringBuilder sql = new StringBuilder("Select");
		sql.append(" Coalesce(Sum(AdviseAmount - PaidAmount - WaivedAmount), 0) Amount");
		sql.append(" from ManualAdvise");
		sql.append(" Where FinID = ? and AdviseType = ?");

		if (isBounce) {
			sql.append(" and (BounceId > 0 or FeeTypeID IN (Select FeeTypeID from FeeTypes Where FeeTypeCode = ?))");
		} else {
			sql.append(" and FeeTypeID Not IN (Select FeeTypeID from FeeTypes Where FeeTypeCode = ?)");
		}

		logger.debug(Literal.SQL.concat(sql.toString()));

		return this.jdbcOperations.queryForObject(sql.toString(), BigDecimal.class, finID, 1,
				PennantConstants.FEETYPE_BOUNCE);
	}

	@Override
	public Date getMaxValueDateOfRcv(long finID) {
		String sql = "Select max(ValueDate) from ManualAdvise Where FinID = ? and AdviseType = ? and BounceId = ?";

		logger.debug(Literal.SQL.concat(sql));

		return this.jdbcOperations.queryForObject(sql, Date.class, finID, 1, 0);
	}

	@Override
	public List<ManualAdvise> getManualAdviseForLMSEvent(long finID) {
		StringBuilder sql = new StringBuilder("Select");
		sql.append(" ma.AdviseID, ma.AdviseType, ma.FinID, ma.FinReference, ma.FeeTypeID");
		sql.append(", ma.Sequence, ma.AdviseAmount, ma.BounceID, ma.LinkedTranId, ma.ReceiptID");
		sql.append(", ma.PaidAmount, ma.WaivedAmount, ma.Remarks, ma.ValueDate, ma.PostDate, ma.ReservedAmt");
		sql.append(", ma.BalanceAmt, ma.PaidCGST, ma.PaidSGST, ma.PaidUGST, ma.PaidIGST, ma.PaidCESS");
		sql.append(", ma.WaivedCGST, ma.WaivedSGST, ma.WaivedUGST, ma.WaivedIGST");
		sql.append(", ma.WaivedCESS, ma.FinSource, ma.DueCreation");
		sql.append(", ft.FeeTypeCode, ft.FeeTypeDesc, ft.TaxApplicable, ft.TaxComponent, ft.TDSReq, br.BounceCode");
		sql.append(" From ManualAdvise ma");
		sql.append(" Left Join FeeTypes ft on ft.FeeTypeID = ma.FeeTypeID");
		sql.append(" Left Join BounceReasons br on br.BounceID = ma.BounceID");
		sql.append(" Where ma.FinID = ? and ma.AdviseType = ?");
		sql.append(" and (ma.AdviseAmount - ma.PaidAmount - ma.WaivedAmount) > 0");

		logger.debug(Literal.SQL.concat(sql.toString()));

		List<ManualAdvise> list = this.jdbcOperations.query(sql.toString(), (rs, rowNum) -> {

			ManualAdvise ma = new ManualAdvise();

			ma.setAdviseID(rs.getLong("AdviseID"));
			ma.setAdviseType(rs.getInt("AdviseType"));
			ma.setFinID(rs.getLong("FinID"));
			ma.setFinReference(rs.getString("FinReference"));
			ma.setFeeTypeID(rs.getLong("FeeTypeID"));
			ma.setSequence(rs.getInt("sequence"));
			ma.setAdviseAmount(rs.getBigDecimal("adviseAmount"));
			ma.setBounceID(rs.getLong("BounceID"));
			ma.setReceiptID(rs.getLong("ReceiptID"));
			ma.setLinkedTranId(rs.getLong("LinkedTranId"));
			ma.setReceiptID(rs.getLong("ReceiptID"));
			ma.setPaidAmount(rs.getBigDecimal("paidAmount"));
			ma.setWaivedAmount(rs.getBigDecimal("waivedAmount"));
			ma.setRemarks(rs.getString("remarks"));
			ma.setValueDate(rs.getTimestamp("ValueDate"));
			ma.setPostDate(rs.getTimestamp("PostDate"));
			ma.setReservedAmt(rs.getBigDecimal("ReservedAmt"));
			ma.setBalanceAmt(rs.getBigDecimal("BalanceAmt"));
			ma.setPaidCGST(rs.getBigDecimal("PaidCGST"));
			ma.setPaidSGST(rs.getBigDecimal("PaidSGST"));
			ma.setPaidUGST(rs.getBigDecimal("PaidUGST"));
			ma.setPaidIGST(rs.getBigDecimal("PaidIGST"));
			ma.setPaidCESS(rs.getBigDecimal("PaidCESS"));
			ma.setWaivedCGST(rs.getBigDecimal("WaivedCGST"));
			ma.setWaivedSGST(rs.getBigDecimal("WaivedSGST"));
			ma.setWaivedUGST(rs.getBigDecimal("WaivedUGST"));
			ma.setWaivedIGST(rs.getBigDecimal("WaivedIGST"));
			ma.setWaivedCESS(rs.getBigDecimal("WaivedCESS"));
			ma.setFinSource(rs.getString("FinSource"));
			ma.setDueCreation(rs.getBoolean("DueCreation"));
			ma.setFeeTypeCode(rs.getString("FeeTypeCode"));
			ma.setFeeTypeDesc(rs.getString("FeeTypeDesc"));
			ma.setTaxApplicable(rs.getBoolean("taxApplicable"));
			ma.setTaxComponent(rs.getString("taxComponent"));
			ma.setTdsReq(rs.getBoolean("TDSReq"));
			ma.setBounceCode(rs.getString("BounceCode"));

			return ma;
		}, finID, AdviseType.PAYABLE.id());

		return list.stream().sorted((l1, l2) -> Long.compare(l2.getFeeTypeID(), l1.getFeeTypeID()))
				.collect(Collectors.toList());
	}

	@Override
	public List<ManualAdvise> getUnpaidBounces(long finID, String type) {
		StringBuilder sql = new StringBuilder("Select");
		sql.append(" AdviseID, AdviseAmount, PaidAmount, WaivedAmount, ReservedAmt, BalanceAmt, BounceId, ReceiptId");
		sql.append(", PaidCGST, PaidSGST, PaidUGST, PaidIGST, PaidCess");
		sql.append(", WaivedCGST, WaivedSGST, WaivedUGST, WaivedIGST, WaivedCess");

		if (StringUtils.trimToEmpty(type).contains("View")) {
			sql.append(", FeeTypeCode, FeeTypeDesc, BounceCode, BounceCodeDesc, TaxApplicable, TaxComponent");
		}

		sql.append(" From ManualAdvise");
		sql.append(type);
		sql.append(" Where FinID = ? AND AdviseType = ? AND BounceID > ?");
		sql.append(" AND (AdviseAmount - PaidAmount - WaivedAmount) > ?");

		logger.debug(Literal.SQL.concat(sql.toString()));

		return jdbcOperations.query(sql.toString(), ps -> {
			int index = 1;

			ps.setLong(index++, finID);
			ps.setInt(index++, AdviseType.RECEIVABLE.id());
			ps.setLong(index++, 0);
			ps.setBigDecimal(index++, BigDecimal.ZERO);
		}, (rs, rowNum) -> {
			ManualAdvise ma = new ManualAdvise();

			ma.setAdviseID(rs.getLong("AdviseID"));
			ma.setAdviseAmount(rs.getBigDecimal("AdviseAmount"));
			ma.setPaidAmount(rs.getBigDecimal("PaidAmount"));
			ma.setWaivedAmount(rs.getBigDecimal("WaivedAmount"));
			ma.setReservedAmt(rs.getBigDecimal("ReservedAmt"));
			ma.setBalanceAmt(rs.getBigDecimal("BalanceAmt"));
			ma.setBounceID(rs.getLong("BounceId"));
			ma.setReceiptID(rs.getLong("ReceiptId"));
			ma.setPaidCGST(rs.getBigDecimal("PaidCGST"));
			ma.setPaidSGST(rs.getBigDecimal("PaidSGST"));
			ma.setPaidUGST(rs.getBigDecimal("PaidUGST"));
			ma.setPaidIGST(rs.getBigDecimal("PaidIGST"));
			ma.setPaidCESS(rs.getBigDecimal("PaidCess"));
			ma.setWaivedCGST(rs.getBigDecimal("WaivedCGST"));
			ma.setWaivedSGST(rs.getBigDecimal("WaivedSGST"));
			ma.setWaivedUGST(rs.getBigDecimal("WaivedUGST"));
			ma.setWaivedIGST(rs.getBigDecimal("WaivedIGST"));
			ma.setWaivedCESS(rs.getBigDecimal("WaivedCess"));

			if (StringUtils.trimToEmpty(type).contains("View")) {
				ma.setFeeTypeCode(rs.getString("FeeTypeCode"));
				ma.setFeeTypeDesc(rs.getString("FeeTypeDesc"));
				ma.setBounceCode(rs.getString("BounceCode"));
				ma.setBounceCodeDesc(rs.getString("BounceCodeDesc"));
				ma.setTaxApplicable(rs.getBoolean("taxApplicable"));
				ma.setTaxComponent(rs.getString("taxComponent"));
			}

			return ma;
		});
	}

	@Override
	public List<ManualAdvise> getPaybleAdvises(long finID, String type) {
		return getPaybleAdvises(finID, SysParamUtil.getAppDate(), type);
	}

	@Override
	public List<ManualAdvise> getReceivableAdvises(long finID, String type) {
		return getReceivableAdvises(finID, SysParamUtil.getAppDate(), type);
	}

	@Override
	public List<ManualAdvise> getPaybleAdvises(long finID, Date valueDate, String type) {
		return getAdvises(finID, valueDate, AdviseType.PAYABLE.id(), type);
	}

	@Override
	public List<ManualAdvise> getReceivableAdvises(long finID, Date valueDate, String type) {
		return getAdvises(finID, valueDate, AdviseType.RECEIVABLE.id(), type);
	}

	private List<ManualAdvise> getAdvises(long finID, Date valueDate, int adviseType, String type) {
		StringBuilder sql = getSqlQuery(type);
		sql.append(" Where FinID = ? and AdviseType = ? and (AdviseAmount - PaidAmount - WaivedAmount) > 0");
		if (!ImplementationConstants.MANUAL_ADVISE_FUTURE_DATE) {
			sql.append(" and ValueDate <= ?");
		}

		sql.append("  and (Status is null or status = ?)");
		sql.append(" order by valuedate, adviseid");

		logger.debug(Literal.SQL.concat(sql.toString()));

		return this.jdbcOperations.query(sql.toString(), ps -> {
			int index = 1;
			ps.setLong(index++, finID);
			ps.setInt(index++, adviseType);

			if (!ImplementationConstants.MANUAL_ADVISE_FUTURE_DATE) {
				ps.setDate(index++, JdbcUtil.getDate(valueDate));
			}
			ps.setString(index, PennantConstants.MANUALADVISE_MAINTAIN);
		}, new ManualAdviseRM(type));
	}

	@Override
	public List<ManualAdvise> getAdvisesList(long finID, int adviseType, String type) {
		StringBuilder sql = getSqlQuery(type);
		sql.append(" Where FinID = ? and AdviseType = ? ");

		if (!ImplementationConstants.MANUAL_ADVISE_FUTURE_DATE) {
			sql.append(" and ValueDate <= ?");
		}

		sql.append(" and (Status is null or status = ?)");
		sql.append(" order by valuedate, adviseid");

		logger.debug(Literal.SQL.concat(sql.toString()));

		return this.jdbcOperations.query(sql.toString(), ps -> {
			int index = 1;
			ps.setLong(index++, finID);
			ps.setInt(index++, adviseType);

			if (!ImplementationConstants.MANUAL_ADVISE_FUTURE_DATE) {
				ps.setDate(index++, JdbcUtil.getDate(SysParamUtil.getAppDate()));
			}

			ps.setString(index, PennantConstants.MANUALADVISE_MAINTAIN);
		}, new ManualAdviseRM(type));
	}

	@Override
	public List<ManualAdvise> getAdvisesByDueDate(long finID, Date dueDate, String type) {
		StringBuilder sql = getSqlQuery(type);
		sql.append(" Where FinID = ? and DueDate = ?");

		logger.debug(Literal.SQL.concat(sql.toString()));

		return this.jdbcOperations.query(sql.toString(), ps -> {
			ps.setLong(1, finID);

			if (DateUtil.compare(dueDate, null) != 0) {
				ps.setDate(2, JdbcUtil.getDate(dueDate));
			}
		}, new ManualAdviseRM(type));
	}

	@Override
	public void cancelFutureDatedAdvises(List<FinanceMain> fmList) {
		String sql = "Update ManualAdvise Set Status = ?, Reason = ? Where FinID = ? and ValueDate > ? and (Status is null OR status = ?)";

		logger.debug(Literal.SQL.concat(sql));

		this.jdbcOperations.batchUpdate(sql, new BatchPreparedStatementSetter() {
			@Override
			public void setValues(PreparedStatement ps, int i) throws SQLException {
				FinanceMain fm = fmList.get(i);
				int index = 1;

				ps.setString(index++, PennantConstants.MANUALADVISE_CANCEL);
				ps.setString(index++, Labels.getLabel("label_EOD_ManualAdvise_Cancel_Reason.Msg"));
				ps.setLong(index++, fm.getFinID());
				ps.setDate(index++, JdbcUtil.getDate(fm.getAppDate()));
				ps.setString(index, PennantConstants.MANUALADVISE_MAINTAIN);
			}

			@Override
			public int getBatchSize() {
				return fmList.size();
			}
		});
	}

	@Override
	public int getFutureDatedAdvises(long finID) {
		String sql = "Select count(finReference) from ManualAdvise where FinID = ? and valueDate > ?";

		logger.debug(Literal.SQL.concat(sql));

		java.sql.Date appDate = JdbcUtil.getDate(SysParamUtil.getAppDate());

		try {
			return this.jdbcOperations.queryForObject(sql, Integer.class, finID, appDate);
		} catch (EmptyResultDataAccessException dae) {
			logger.warn(Message.NO_RECORD_FOUND);
			return 0;
		}
	}

	@Override
	public List<ManualAdviseMovements> getAdviseMovements(long finID, Date dueDate, String type) {
		StringBuilder sql = new StringBuilder("Select");
		sql.append(" MovementID, AdviseID, MovementDate, MovementAmount, PaidAmount, WaivedAmount");
		sql.append(", Status, ReceiptID, ReceiptSeqID, TaxHeaderId, TdsPaid");
		sql.append(", PaidCGST, PaidSGST, PaidUGST, PaidIGST");
		sql.append(", WaivedCGST, WaivedSGST, WaivedUGST, WaivedIGST");

		if (StringUtils.contains(type, "View")) {
			sql.append(", FeeTypeCode, FeeTypeDesc, TaxApplicable, TaxComponent, DueDate");
		}

		sql.append(" from ManualAdviseMovements");
		sql.append(StringUtils.trimToEmpty(type));
		sql.append(" Where AdviseId in (Select AdviseId from ManualAdvise where DueDate = ? and FinID = ?)");

		logger.debug(Literal.SQL.concat(sql.toString()));

		List<ManualAdviseMovements> movements = this.jdbcOperations.query(sql.toString(), (rs, rowNum) -> {
			ManualAdviseMovements mam = new ManualAdviseMovements();

			mam.setMovementID(rs.getLong("MovementID"));
			mam.setAdviseID(rs.getLong("AdviseID"));
			mam.setMovementDate(rs.getTimestamp("MovementDate"));
			mam.setMovementAmount(rs.getBigDecimal("MovementAmount"));
			mam.setPaidAmount(rs.getBigDecimal("PaidAmount"));
			mam.setWaivedAmount(rs.getBigDecimal("WaivedAmount"));
			mam.setStatus(rs.getString("Status"));
			mam.setReceiptID(rs.getLong("ReceiptID"));
			mam.setReceiptSeqID(rs.getLong("ReceiptSeqID"));
			mam.setTaxHeaderId(JdbcUtil.getLong(rs.getLong("TaxHeaderId")));
			mam.setTdsPaid(rs.getBigDecimal("TdsPaid"));
			mam.setPaidCGST(rs.getBigDecimal("PaidCGST"));
			mam.setPaidSGST(rs.getBigDecimal("PaidSGST"));
			mam.setPaidUGST(rs.getBigDecimal("PaidUGST"));
			mam.setPaidIGST(rs.getBigDecimal("PaidIGST"));
			mam.setWaivedCGST(rs.getBigDecimal("WaivedCGST"));
			mam.setWaivedSGST(rs.getBigDecimal("WaivedSGST"));
			mam.setWaivedUGST(rs.getBigDecimal("WaivedUGST"));
			mam.setWaivedIGST(rs.getBigDecimal("WaivedIGST"));

			if (StringUtils.contains(type, "View")) {
				mam.setFeeTypeCode(rs.getString("FeeTypeCode"));
				mam.setFeeTypeDesc(rs.getString("FeeTypeDesc"));
				mam.setTaxApplicable(rs.getBoolean("TaxApplicable"));
				mam.setTaxComponent(rs.getString("TaxComponent"));
				mam.setDueDate(rs.getDate("DueDate"));
			}

			return mam;
		}, JdbcUtil.getDate(dueDate), finID);

		return movements.stream().sorted((l1, l2) -> DateUtil.compare(l1.getMovementDate(), l1.getMovementDate()))
				.collect(Collectors.toList());
	}

	@Override
	public long getFeeTypeId(long adviseID) {
		String sql = "Select FeeTypeID From ManualAdvise Where AdviseId = ?";

		logger.debug(Literal.SQL.concat(sql));

		try {
			return jdbcOperations.queryForObject(sql, Long.class, adviseID);
		} catch (EmptyResultDataAccessException e) {
			logger.warn(Message.NO_RECORD_FOUND);
			return Long.MIN_VALUE;
		}
	}

	@Override
	public List<ManualAdvise> getAdvisesByMaturityDate(long finID, Date valueDate) {
		StringBuilder sql = getSelectQuery("");
		sql.append(" Where FinID = ? and ValueDate > ? And (Status is null OR status = ?) And LinkedTranID = ?");

		logger.debug(Literal.SQL + sql.toString());

		return this.jdbcOperations.query(sql.toString(), ps -> {
			int index = 1;
			ps.setLong(index++, finID);
			ps.setDate(index++, JdbcUtil.getDate(valueDate));
			ps.setString(index++, PennantConstants.MANUALADVISE_MAINTAIN);
			ps.setInt(index, 0);
		}, new ManualAdviseRowMapper());
	}

	@Override
	public List<ManualAdvise> getAdvisesByValueDate(long finID, Date valueDate) {
		StringBuilder sql = new StringBuilder("Select");
		sql.append(" AdviseID, ma.AdviseType, FinID, FinReference, ma.FeeTypeID, Sequence, AdviseAmount, BounceID");
		sql.append(", ReceiptID, PaidAmount, WaivedAmount, Remarks, ValueDate, PostDate, ReservedAmt");
		sql.append(", BalanceAmt, PaidCGST, PaidSGST, PaidUGST, PaidCESS, PaidIGST, WaivedCGST, WaivedSGST");
		sql.append(", WaivedUGST, WaivedIGST, WaivedCESS, FinSource, ma.Version , ma.LastMntBy, ma.LastMntOn");
		sql.append(", ma.RecordStatus, ma.RoleCode, ma.NextRoleCode, ma.TaskId, ma.NextTaskId, ma.RecordType");
		sql.append(", ma.WorkflowId, DueCreation, LinkedTranId, HoldDue, Status, Reason, PresentmentId");
		sql.append(" From ManualAdvise ma");
		sql.append(" Inner Join FeeTypes ft on ft.FeeTypeID = ma.FeeTypeID");
		sql.append(" Where FinID = ? and ValueDate = ? and (Status is null OR status = ?) And LinkedTranID = ?");
		sql.append(" and ft.DueAccReq = ?");

		logger.debug(Literal.SQL.concat(sql.toString()));

		return this.jdbcOperations.query(sql.toString(), ps -> {
			int index = 1;
			ps.setLong(index++, finID);
			ps.setDate(index++, JdbcUtil.getDate(valueDate));
			ps.setString(index++, PennantConstants.MANUALADVISE_MAINTAIN);
			ps.setInt(index++, 0);
			ps.setInt(index, 1);
		}, new ManualAdviseRowMapper());
	}

	@Override
	public void updateLinkedTranId(ManualAdvise ma) {
		String sql = "Update ManualAdvise Set LinkedTranId = ?, DueCreation = ? Where FinID = ? and AdviseID = ? and (Status is null OR status = ?)";

		logger.debug(Literal.SQL.concat(sql));

		this.jdbcOperations.update(sql, ps -> {
			int index = 1;

			ps.setLong(index++, ma.getLinkedTranId());
			ps.setBoolean(index++, ma.isDueCreation());

			ps.setLong(index++, ma.getFinID());
			ps.setLong(index++, ma.getAdviseID());
			ps.setString(index, PennantConstants.MANUALADVISE_MAINTAIN);
		});
	}

	@Override
	public int cancelFutureDatedAdvises() {
		switch (App.DATABASE) {
		case ORACLE:
			return cancelManualAdvisesForOracle();
		case MY_SQL:
			return cancelManualAdvisesForSql();
		case POSTGRES:
			return cancelManualAdvisesForPostgres();
		default:
			return 0;
		}
	}

	private int cancelManualAdvisesForPostgres() {
		StringBuilder sql = new StringBuilder("UPDATE");
		sql.append(" ManualAdvise MA set Status = ?, Reason = ?");
		sql.append(" From FinanceMain FM");
		sql.append(" Where MA.FinID = FM.FinID and MA.ValueDate = FM.ClosedDate");
		sql.append(" and FM.FinIsActive = ? And FM.ClosingStatus in ( ?, ?, ?)");
		sql.append(" and (MA.Status is null or MA.Status = ?)");
		sql.append(" and (MA.AdviseAmount - MA.PaidAmount - MA.WaivedAmount) > ?");

		logger.debug(Literal.SQL.concat(sql.toString()));

		return jdbcOperations.update(sql.toString(), ps -> {
			int index = 1;

			ps.setString(index++, PennantConstants.MANUALADVISE_CANCEL);
			ps.setString(index++, Labels.getLabel("label_EOD_ManualAdvise_Cancel_Reason.Msg"));
			ps.setInt(index++, 0);
			ps.setString(index++, FinanceConstants.CLOSE_STATUS_EARLYSETTLE);
			ps.setString(index++, FinanceConstants.CLOSE_STATUS_CANCELLED);
			ps.setString(index++, FinanceConstants.CLOSE_STATUS_MATURED);
			ps.setString(index++, PennantConstants.MANUALADVISE_MAINTAIN);
			ps.setInt(index++, 0);
		});
	}

	private int cancelManualAdvisesForSql() {
		StringBuilder sql = new StringBuilder("UPDATE");
		sql.append(" MA set MA.Status = ?, Reason = ?");
		sql.append(" From ManualAdvise MA");
		sql.append(" INNER JOIN FinanceMain FM ON FM.FinID = MA.FinID");
		sql.append(" and MA.ValueDate = FM.ClosedDate");
		sql.append(" and FM.FinIsActive = ? and FM.ClosingStatus in ( ?, ?, ?)");
		sql.append(" Where (MA.Status is null or MA.Status = ?)");
		sql.append(" and (MA.AdviseAmount - MA.PaidAmount - MA.WaivedAmount) > ?");

		logger.debug(Literal.SQL.concat(sql.toString()));

		return jdbcOperations.update(sql.toString(), ps -> {
			int index = 1;

			ps.setString(index++, PennantConstants.MANUALADVISE_CANCEL);
			ps.setString(index++, Labels.getLabel("label_EOD_ManualAdvise_Cancel_Reason.Msg"));
			ps.setInt(index++, 0);
			ps.setString(index++, FinanceConstants.CLOSE_STATUS_EARLYSETTLE);
			ps.setString(index++, FinanceConstants.CLOSE_STATUS_CANCELLED);
			ps.setString(index++, FinanceConstants.CLOSE_STATUS_MATURED);
			ps.setString(index++, PennantConstants.MANUALADVISE_MAINTAIN);
			ps.setInt(index++, 0);
		});
	}

	private int cancelManualAdvisesForOracle() {
		StringBuilder sql = new StringBuilder("MERGE");
		sql.append(" INTO ManualAdvise MA");
		sql.append(" USING (SELECT FinID, FinReference, ClosedDate FROM FinanceMain");
		sql.append(" Where FinIsActive = ? and ClosingStatus in ( ?, ?, ?))");
		sql.append(" FM ON (FM.FinID = MA.FinID and MA.ValueDate = FM.ClosedDate");
		sql.append(" and (MA.Status is null or MA.Status = ?)");
		sql.append(" and (MA.AdviseAmount - MA.PaidAmount - MA.WaivedAmount) > ?)");
		sql.append(" WHEN MATCHED THEN UPDATE SET Status = ?, Reason = ?");

		logger.debug(Literal.SQL.concat(sql.toString()));

		return jdbcOperations.update(sql.toString(), ps -> {
			int index = 1;

			ps.setInt(index++, 0);
			ps.setString(index++, FinanceConstants.CLOSE_STATUS_EARLYSETTLE);
			ps.setString(index++, FinanceConstants.CLOSE_STATUS_CANCELLED);
			ps.setString(index++, FinanceConstants.CLOSE_STATUS_MATURED);
			ps.setString(index++, PennantConstants.MANUALADVISE_MAINTAIN);
			ps.setInt(index++, 0);
			ps.setString(index++, PennantConstants.MANUALADVISE_CANCEL);
			ps.setString(index, Labels.getLabel("label_EOD_ManualAdvise_Cancel_Reason.Msg"));
		});
	}

	@Override
	public List<ManualAdvise> getAdvises(long finID, String type) {
		StringBuilder sql = getSelectQuery(type);
		sql.append(" Where FinID = ?");

		logger.debug(Literal.SQL.concat(sql.toString()));

		return this.jdbcOperations.query(sql.toString(), new ManualAdviseRowMapper(), finID);
	}

	private StringBuilder getSqlQuery(String type) {
		StringBuilder sql = new StringBuilder("Select");
		sql.append(" AdviseID, AdviseType, FinID, FinReference, FeeTypeID, Sequence, AdviseAmount, BounceID");
		sql.append(", LinkedTranId, ReceiptID, PaidAmount, WaivedAmount, Remarks, ValueDate, PostDate, ReservedAmt");
		sql.append(", BalanceAmt, PaidCGST, PaidSGST, PaidUGST, PaidIGST, PaidCESS, WaivedCGST, WaivedSGST");
		sql.append(", WaivedUGST, WaivedIGST, WaivedCESS, DueCreation, PresentmentId, DueDate, FinSource");
		sql.append(", Reason, Status");

		if (StringUtils.trimToEmpty(type).contains("View")) {
			sql.append(", FeeTypeCode, FeeTypeDesc, BounceCode, BounceCodeDesc");
			sql.append(", taxApplicable, taxComponent, TdsReq, LinkedTranId, PayableLinkTo");
		}

		sql.append(", Version, LastMntOn, LastMntBy, RecordStatus, RoleCode");
		sql.append(", NextRoleCode, TaskId, NextTaskId, RecordType, WorkflowId");
		sql.append(" From ManualAdvise");
		sql.append(StringUtils.trimToEmpty(type));

		return sql;
	}

	@Override
	public BigDecimal getPaidAmountsbyAllocation(long finID, String payableLinkTo, Date valueDate) {
		StringBuilder sql = new StringBuilder("Select coalesce(Sum(rad.PaidAmount), 0)");
		sql.append(" From FinReceiptHeader rh");
		sql.append(" Inner Join ReceiptAllocationDetail rad on rad.ReceiptID = rh.ReceiptID and rh.ValueDate <= ?");
		sql.append(" Where rh.FinID = ? and rh.ReceiptModeStatus not in (?, ?)");
		sql.append(" and Not Exists (Select 1 from FinReceiptHeader_Temp rht");
		sql.append(" Where rh.ReceiptID = rht.ReceiptID)");
		sql.append(" and rad.AllocationType = ?");

		logger.debug(Literal.SQL.concat(sql.toString()));

		return this.jdbcOperations.queryForObject(sql.toString(), BigDecimal.class, valueDate, finID,
				RepayConstants.PAYSTATUS_BOUNCE, RepayConstants.PAYSTATUS_CANCEL, payableLinkTo);
	}

	@Override
	public BigDecimal getPaidAmount(long finID, Long feeTypeId, Date valueDate) {
		StringBuilder sql = new StringBuilder("Select coalesce(sum(rad.PaidAmount - rad.TdsPaid), 0)");
		sql.append(" From ReceiptAllocationDetail rad");
		sql.append(" Inner Join FinReceiptHeader rh on rh.ReceiptID = rad.ReceiptID");
		sql.append(" Inner Join ManualAdvise ma on ma.AdviseID = rad.AllocationTO");
		sql.append(" Where rh.FinID = ?  and ma.FeeTypeID = ? and rh.ValueDate <= ?");
		sql.append(" and Not Exists (Select 1 from FinReceiptHeader_Temp rht");
		sql.append(" Where rh.ReceiptID = rht.ReceiptID)");
		sql.append(" and rh.ReceiptModeStatus not in (?, ?)");

		logger.debug(Literal.SQL.concat(sql.toString()));

		return this.jdbcOperations.queryForObject(sql.toString(), BigDecimal.class, finID, feeTypeId, valueDate,
				RepayConstants.PAYSTATUS_BOUNCE, RepayConstants.PAYSTATUS_CANCEL);
	}

	@Override
	public BigDecimal getFeePaidAmount(long finID, Long feeTypeId) {
		String sql = "Select PaidAmountOriginal, PaidAmountGST, PaidTDS from FinFeeDetail where FinID = ? and FeeTypeID = ?";

		logger.debug(Literal.SQL.concat(sql));

		try {
			return this.jdbcOperations.queryForObject(sql, (rs, rowNum) -> {
				BigDecimal eligibleAmount = rs.getBigDecimal("PaidAmountOriginal");
				eligibleAmount = eligibleAmount.add(rs.getBigDecimal("PaidAmountGST"));
				eligibleAmount = eligibleAmount.subtract(rs.getBigDecimal("PaidTDS"));

				return eligibleAmount;
			}, finID, feeTypeId);
		} catch (EmptyResultDataAccessException e) {
			logger.warn(Message.NO_RECORD_FOUND);
			return BigDecimal.ZERO;
		}
	}

	@Override
	public BigDecimal getExistingPayableAmount(long finID, long feeTypeId) {
		String sql = "Select coalesce(sum(AdviseAmount), 0) From ManualAdvise Where FinID = ? and FeeTypeID = ?";

		logger.debug(Literal.SQL.concat(sql));

		return this.jdbcOperations.queryForObject(sql, BigDecimal.class, finID, feeTypeId);
	}

	@Override
	public BigDecimal getRefundedAmount(long finID, long feeTypeId) {
		String sql = "Select coalesce(sum(AdviseAmount), 0) From ManualAdvise Where FinID = ? and FeeTypeID = ?";

		logger.debug(Literal.SQL.concat(sql));

		return this.jdbcOperations.queryForObject(sql, BigDecimal.class, finID, feeTypeId);
	}

	@Override
	public BigDecimal getRefundedAmt(long finID, long receivableID, long receivableFeeTypeID) {
		StringBuilder sql = new StringBuilder("Select");
		sql.append(" coalesce(sum(AdviseAmount), 0) AdviseAmount");
		sql.append(" From Fee_Refund_Header frh");
		sql.append(" Inner Join Fee_Refund_Details frd on frd.HeaderID = frh.ID");
		sql.append(" Inner Join ManualAdvise ma on ma.AdviseID = frd.PayableID");
		sql.append(" Where ma.FinID = ? and frd.ReceivableID = ? and frd.ReceivableFeeTypeID = ?");

		logger.debug(Literal.SQL.concat(sql.toString()));

		return this.jdbcOperations.queryForObject(sql.toString(), BigDecimal.class, finID, receivableID,
				receivableFeeTypeID);
	}

	@Override
	public boolean isDuplicatePayble(long finID, long feeTypeId, String payablelinkTo) {
		StringBuilder sql = new StringBuilder("Select");
		sql.append(" count(ma.FeeTypeId)");
		sql.append(" From ManualAdvise_Temp ma");
		sql.append(" Inner Join FeeTypes ft on ma.FeeTypeId = ft.FeeTypeId");
		sql.append(" Where ma.FeeTypeId = ? and ma.FinId = ? and ft.PayablelinkTo = ?");

		logger.debug(Literal.SQL.concat(sql.toString()));

		return this.jdbcOperations.queryForObject(sql.toString(), Integer.class, feeTypeId, finID, payablelinkTo) > 0;
	}

	@Override
	public boolean isPaybleExist(long finID, long feeTypeId, String payablelinkTo) {
		StringBuilder sql = new StringBuilder("Select count(FeeTypeId) From (");
		sql.append(" Select ma.FeeTypeId From ManualAdvise_Temp ma");
		sql.append(" Inner Join FeeTypes ft on ma.FeeTypeId = ft.FeeTypeId");
		sql.append(" Where ma.FeeTypeId != ? and ma.FinId = ? and ft.PayablelinkTo = ?");
		sql.append(" Union All");
		sql.append(" Select ma.FeeTypeId From ManualAdvise ma");
		sql.append(" Inner Join FeeTypes ft on ma.FeeTypeId = ft.FeeTypeId");
		sql.append(" Where ma.FeeTypeId != ? and ma.FinId = ? and ft.PayablelinkTo = ?");
		sql.append(" ) T");

		logger.debug(Literal.SQL.concat(sql.toString()));

		return this.jdbcOperations.queryForObject(sql.toString(), Integer.class, feeTypeId, finID, payablelinkTo,
				feeTypeId, finID, payablelinkTo) > 0;
	}

	private StringBuilder getManualAdvicequery(String type) {
		StringBuilder sql = new StringBuilder("Select");
		sql.append(" AdviseID, AdviseType, FinID, FinReference, FeeTypeID, Sequence, AdviseAmount, BounceID");
		sql.append(", LinkedTranId, ReceiptID, PaidAmount, WaivedAmount, Remarks, ValueDate, PostDate, ReservedAmt");
		sql.append(", BalanceAmt, PaidCGST, PaidSGST, PaidUGST, PaidIGST, PaidCESS");
		sql.append(", WaivedCGST, WaivedSGST, WaivedUGST, WaivedIGST, WaivedCESS");
		sql.append(", FinSource, DueCreation, PresentmentId, Reason, Status");

		if (StringUtils.trimToEmpty(type).contains("View")) {
			sql.append(", FeeTypeCode, FeeTypeDesc, TaxApplicable, TaxComponent, TDSReq, BounceCode, PayableLinkTo");
		}

		sql.append(", Version, LastMntOn, LastMntBy, RecordStatus, RoleCode, NextRoleCode");
		sql.append(", TaskId, NextTaskId, RecordType, WorkflowId");
		sql.append(" From ManualAdvise");
		sql.append(StringUtils.trimToEmpty(type));

		return sql;
	}

	private class ManualAdviseRM implements RowMapper<ManualAdvise> {
		private String type;

		public ManualAdviseRM(String type) {
			super();
			this.type = type;
		}

		@Override
		public ManualAdvise mapRow(ResultSet rs, int rowNum) throws SQLException {
			ManualAdvise ma = new ManualAdvise();

			ma.setAdviseID(rs.getLong("AdviseID"));
			ma.setAdviseType(rs.getInt("AdviseType"));
			ma.setFinID(rs.getLong("FinID"));
			ma.setFinReference(rs.getString("FinReference"));
			ma.setFeeTypeID(rs.getLong("FeeTypeID"));
			ma.setSequence(rs.getInt("sequence"));
			ma.setAdviseAmount(rs.getBigDecimal("AdviseAmount"));
			ma.setBounceID(rs.getLong("BounceID"));
			ma.setReceiptID(rs.getLong("ReceiptID"));
			ma.setLinkedTranId(rs.getLong("LinkedTranId"));
			ma.setReceiptID(rs.getLong("ReceiptID"));
			ma.setPaidAmount(rs.getBigDecimal("paidAmount"));
			ma.setWaivedAmount(rs.getBigDecimal("waivedAmount"));
			ma.setRemarks(rs.getString("remarks"));
			ma.setValueDate(rs.getTimestamp("ValueDate"));
			ma.setPostDate(rs.getTimestamp("PostDate"));
			ma.setReservedAmt(rs.getBigDecimal("ReservedAmt"));
			ma.setBalanceAmt(rs.getBigDecimal("BalanceAmt"));
			ma.setPaidCGST(rs.getBigDecimal("PaidCGST"));
			ma.setPaidSGST(rs.getBigDecimal("PaidSGST"));
			ma.setPaidUGST(rs.getBigDecimal("PaidUGST"));
			ma.setPaidIGST(rs.getBigDecimal("PaidIGST"));
			ma.setPaidCESS(rs.getBigDecimal("PaidCESS"));
			ma.setWaivedCGST(rs.getBigDecimal("WaivedCGST"));
			ma.setWaivedSGST(rs.getBigDecimal("WaivedSGST"));
			ma.setWaivedUGST(rs.getBigDecimal("WaivedUGST"));
			ma.setWaivedIGST(rs.getBigDecimal("WaivedIGST"));
			ma.setWaivedCESS(rs.getBigDecimal("WaivedCESS"));
			ma.setFinSource(rs.getString("FinSource"));
			ma.setDueCreation(rs.getBoolean("DueCreation"));
			ma.setPresentmentID(JdbcUtil.getLong(rs.getObject("PresentmentId")));

			if (type.contains("View")) {
				ma.setFeeTypeCode(rs.getString("FeeTypeCode"));
				ma.setFeeTypeDesc(rs.getString("FeeTypeDesc"));
				ma.setTaxApplicable(rs.getBoolean("taxApplicable"));
				ma.setTaxComponent(rs.getString("taxComponent"));
				ma.setTdsReq(rs.getBoolean("TDSReq"));
				ma.setBounceCode(rs.getString("BounceCode"));
				ma.setPayableLinkTo(rs.getString("PayableLinkTo"));
			}

			ma.setVersion(rs.getInt("Version"));
			ma.setLastMntOn(rs.getTimestamp("LastMntOn"));
			ma.setLastMntBy(rs.getLong("LastMntBy"));
			ma.setRecordStatus(rs.getString("RecordStatus"));
			ma.setRoleCode(rs.getString("RoleCode"));
			ma.setNextRoleCode(rs.getString("NextRoleCode"));
			ma.setTaskId(rs.getString("TaskId"));
			ma.setNextTaskId(rs.getString("NextTaskId"));
			ma.setRecordType(rs.getString("RecordType"));
			ma.setWorkflowId(rs.getLong("WorkflowId"));
			ma.setStatus(rs.getString("Status"));
			ma.setReason(rs.getString("Reason"));

			return ma;
		}
	}

	private StringBuilder getSelectQuery(String type) {
		StringBuilder sql = new StringBuilder("Select");
		sql.append(" AdviseID, AdviseType, FinID, FinReference, FeeTypeID, Sequence, AdviseAmount, BounceID");
		sql.append(", ReceiptID, PaidAmount, WaivedAmount, Remarks, ValueDate, PostDate, ReservedAmt");
		sql.append(", BalanceAmt, PaidCGST, PaidSGST, PaidUGST, PaidCESS, PaidIGST, WaivedCGST, WaivedSGST");
		sql.append(", WaivedUGST, WaivedIGST, WaivedCESS, FinSource, PresentmentId,Version , LastMntBy, LastMntOn");
		sql.append(", RecordStatus, RoleCode, NextRoleCode, TaskId, NextTaskId, RecordType, WorkflowId, DueCreation");
		sql.append(", LinkedTranId, HoldDue, Status, Reason");
		sql.append(" From ManualAdvise");
		sql.append(type);

		return sql;
	}

	public class ManualAdviseRowMapper implements RowMapper<ManualAdvise> {

		@Override
		public ManualAdvise mapRow(ResultSet rs, int rowNum) throws SQLException {
			ManualAdvise ma = new ManualAdvise();

			ma.setAdviseID(rs.getLong("AdviseID"));
			ma.setAdviseType(rs.getInt("AdviseType"));
			ma.setFinReference(rs.getString("FinReference"));
			ma.setFeeTypeID(rs.getLong("FeeTypeID"));
			ma.setSequence(rs.getInt("Sequence"));
			ma.setAdviseAmount(rs.getBigDecimal("AdviseAmount"));
			ma.setBounceID(rs.getLong("BounceID"));
			ma.setReceiptID(rs.getLong("ReceiptID"));
			ma.setPaidAmount(rs.getBigDecimal("PaidAmount"));
			ma.setWaivedAmount(rs.getBigDecimal("WaivedAmount"));
			ma.setRemarks(rs.getString("Remarks"));
			ma.setValueDate(rs.getDate("ValueDate"));
			ma.setPostDate(rs.getDate("PostDate"));
			ma.setReservedAmt(rs.getBigDecimal("ReservedAmt"));
			ma.setBalanceAmt(rs.getBigDecimal("BalanceAmt"));
			ma.setPaidCGST(rs.getBigDecimal("PaidCGST"));
			ma.setPaidSGST(rs.getBigDecimal("PaidSGST"));
			ma.setPaidUGST(rs.getBigDecimal("PaidUGST"));
			ma.setPaidIGST(rs.getBigDecimal("PaidIGST"));
			ma.setWaivedCGST(rs.getBigDecimal("WaivedCGST"));
			ma.setWaivedSGST(rs.getBigDecimal("WaivedSGST"));
			ma.setWaivedUGST(rs.getBigDecimal("WaivedUGST"));
			ma.setWaivedIGST(rs.getBigDecimal("WaivedIGST"));
			ma.setWaivedCESS(rs.getBigDecimal("WaivedCESS"));
			ma.setFinSource(rs.getString("FinSource"));
			ma.setVersion(rs.getInt("Version"));
			ma.setLastMntBy(rs.getLong("LastMntBy"));
			ma.setLastMntOn(rs.getTimestamp("LastMntOn"));
			ma.setRecordStatus(rs.getString("RecordStatus"));
			ma.setRoleCode(rs.getString("RoleCode"));
			ma.setNextRoleCode(rs.getString("NextRoleCode"));
			ma.setTaskId(rs.getString("TaskId"));
			ma.setNextTaskId(rs.getString("NextTaskId"));
			ma.setRecordType(rs.getString("RecordType"));
			ma.setWorkflowId(rs.getLong("WorkflowId"));
			ma.setDueCreation(rs.getBoolean("DueCreation"));
			ma.setPresentmentID(JdbcUtil.getLong(rs.getObject("PresentmentId")));
			ma.setLinkedTranId(rs.getLong("LinkedTranId"));
			ma.setHoldDue(rs.getBoolean("HoldDue"));
			ma.setStatus(rs.getString("Status"));
			ma.setReason(rs.getString("Reason"));

			return ma;
		}
	}

	@Override
	public List<ManualAdvise> getAdviseStatus(long finID) {
		String sql = "Select Status, ValueDate, AdviseId, FinReference From ManualAdvise Where FinID = ? and Status != ?";

		logger.debug(Literal.SQL.concat(sql.toString()));

		return jdbcOperations.query(sql.toString(), (rs, rowNum) -> {
			ManualAdvise ma = new ManualAdvise();

			ma.setStatus(rs.getString("Status"));
			ma.setValueDate(rs.getTimestamp("ValueDate"));
			ma.setAdviseID(rs.getLong("AdviseId"));
			ma.setFinReference(rs.getString("Finreference"));

			return ma;
		}, finID, PennantConstants.MANUALADVISE_CANCEL);
	}

	@Override
	public void updateStatus(List<ManualAdvise> list, String type) {
		String sql = "Update ManualAdvise Set Status  = ? where AdviseId = ?";

		logger.debug(Literal.SQL.concat(sql));

		jdbcOperations.batchUpdate(sql, new BatchPreparedStatementSetter() {

			@Override
			public void setValues(PreparedStatement ps, int i) throws SQLException {
				ManualAdvise ma = list.get(i);

				int index = 1;

				ps.setString(index++, ma.getStatus());
				ps.setLong(index, ma.getAdviseID());
			}

			@Override
			public int getBatchSize() {
				return list.size();
			}
		});
	}

	@Override
	public List<ManualAdvise> getPayableAdviseList(long finID, Date maxValueDate) {
		StringBuilder sql = new StringBuilder("Select");
		sql.append(" ma.AdviseID, ma.AdviseType, FinID, FinReference, ma.FeeTypeID, AdviseAmount");
		sql.append(", PaidAmount, WaivedAmount, ValueDate, BalanceAmt, Status, ft.TaxComponent, ft.TdsReq");
		sql.append(", PaidCGST, PaidSGST, PaidUGST, PaidIGST, PaidCESS");
		sql.append(", WaivedCGST, WaivedSGST, WaivedUGST, WaivedIGST, WaivedCESS");
		sql.append(" From ManualAdvise ma");
		sql.append(" Inner Join FeeTypes ft on ft.FeeTypeID = ma.FeeTypeID");
		sql.append(" Where FinId = ? and ma.AdviseType = ? and ft.Refundable = ? and ft.AllowAutoRefund = ?");
		sql.append(" and (Status is null or status = ?) and ValueDate <= ?");
		sql.append(" and (AdviseAmount - PaidAmount - WaivedAmount) > ?");

		logger.debug(Literal.SQL.concat(sql.toString()));

		List<ManualAdvise> maList = this.jdbcOperations.query(sql.toString(), ps -> {
			int index = 0;

			ps.setLong(++index, finID);
			ps.setInt(++index, AdviseType.PAYABLE.id());
			ps.setInt(++index, 1);
			ps.setInt(++index, 1);
			ps.setString(++index, PennantConstants.MANUALADVISE_MAINTAIN);
			ps.setDate(++index, JdbcUtil.getDate(maxValueDate));
			ps.setBigDecimal(++index, BigDecimal.ZERO);
		}, (rs, rowNum) -> {
			ManualAdvise ma = new ManualAdvise();

			ma.setAdviseID(rs.getLong("AdviseID"));
			ma.setAdviseType(rs.getInt("AdviseType"));
			ma.setFinID(rs.getLong("FinID"));
			ma.setFinReference(rs.getString("FinReference"));
			ma.setFeeTypeID(rs.getLong("FeeTypeID"));
			ma.setAdviseAmount(rs.getBigDecimal("AdviseAmount"));
			ma.setPaidAmount(rs.getBigDecimal("PaidAmount"));
			ma.setWaivedAmount(rs.getBigDecimal("WaivedAmount"));
			ma.setValueDate(rs.getTimestamp("ValueDate"));
			ma.setBalanceAmt(rs.getBigDecimal("BalanceAmt"));
			ma.setStatus(rs.getString("Status"));
			ma.setTaxComponent(rs.getString("TaxComponent"));
			ma.setTdsReq(rs.getBoolean("TdsReq"));
			ma.setPaidCGST(rs.getBigDecimal("PaidCGST"));
			ma.setPaidSGST(rs.getBigDecimal("PaidSGST"));
			ma.setPaidUGST(rs.getBigDecimal("PaidUGST"));
			ma.setPaidIGST(rs.getBigDecimal("PaidIGST"));
			ma.setPaidCESS(rs.getBigDecimal("PaidCESS"));
			ma.setWaivedCGST(rs.getBigDecimal("WaivedCGST"));
			ma.setWaivedSGST(rs.getBigDecimal("WaivedSGST"));
			ma.setWaivedUGST(rs.getBigDecimal("WaivedUGST"));
			ma.setWaivedIGST(rs.getBigDecimal("WaivedIGST"));
			ma.setWaivedCESS(rs.getBigDecimal("WaivedCESS"));

			return ma;
		});

		return maList.stream().sorted((l1, l2) -> Long.compare(l1.getAdviseID(), l2.getAdviseID()))
				.collect(Collectors.toList());
	}

	@Override
	public List<ManualAdviseMovements> getAdviseMovementsByWaiver(long waiverId, String type) {
		StringBuilder sql = new StringBuilder("Select");
		sql.append(" MovementID, AdviseID, MovementDate, MovementAmount, PaidAmount, WaivedAmount");
		sql.append(", Status, WaiverId, ReceiptSeqID, TaxHeaderId");
		sql.append(" From ManualAdviseMovements");
		sql.append(StringUtils.trimToEmpty(type));
		sql.append(" Where WaiverId = ?");

		logger.debug(Literal.SQL.concat(sql.toString()));

		return this.jdbcOperations.query(sql.toString(), (rs, rowNum) -> {
			ManualAdviseMovements ma = new ManualAdviseMovements();

			ma.setMovementID(rs.getLong("MovementID"));
			ma.setAdviseID(rs.getLong("AdviseID"));
			ma.setMovementDate(rs.getTimestamp("MovementDate"));
			ma.setMovementAmount(rs.getBigDecimal("MovementAmount"));
			ma.setPaidAmount(rs.getBigDecimal("PaidAmount"));
			ma.setWaivedAmount(rs.getBigDecimal("WaivedAmount"));
			ma.setStatus(rs.getString("Status"));
			ma.setReceiptID(rs.getLong("ReceiptID"));
			ma.setReceiptSeqID(rs.getLong("ReceiptSeqID"));
			ma.setTaxHeaderId(JdbcUtil.getLong(rs.getObject("TaxHeaderId")));

			return ma;
		}, waiverId);
	}

	@Override
	public ManualAdvise getBounceChargesByReceiptID(Long bcReceiptID) {
		StringBuilder sql = new StringBuilder("Select");
		sql.append(" AdviseID, Adviseamount, PaidAmount, WaivedAmount");
		sql.append(" From ManualAdvise ma");
		sql.append(" Inner Join FeeTypes ft on ft.FeeTypeID = ma.FeeTypeID and ft.FeeTypeCode = ?");
		sql.append(" Where ma.receiptid = ?");

		logger.debug(Literal.SQL.concat(sql.toString()));

		try {
			return this.jdbcOperations.queryForObject(sql.toString(), (rs, rowNum) -> {
				ManualAdvise ma = new ManualAdvise();

				ma.setAdviseID(rs.getLong("AdviseID"));
				ma.setAdviseAmount(rs.getBigDecimal("Adviseamount"));
				ma.setPaidAmount(rs.getBigDecimal("PaidAmount"));
				ma.setWaivedAmount(rs.getBigDecimal("WaivedAmount"));

				return ma;
			}, Allocation.BOUNCE, bcReceiptID);
		} catch (EmptyResultDataAccessException e) {
			logger.warn(Message.NO_RECORD_FOUND);
			return null;
		}
	}

	@Override
	public void revertBounceCharges(long adviseID, BigDecimal remainingAmount) {
		String sql = "Update ManualAdvise set Adviseamount = Adviseamount - ?, BalanceAmt = BalanceAmt - ? Where AdviseId = ?";

		logger.debug(Literal.SQL.concat(sql));

		this.jdbcOperations.update(sql, ps -> {

			ps.setBigDecimal(1, remainingAmount);
			ps.setBigDecimal(2, remainingAmount);
			ps.setLong(3, adviseID);
		});
	}

	@Override
	public boolean isManualAdviseExist(long finID) {
		String sql = "Select Count(FinID) From ManualAdvise_Temp Where FinID = ?";

		logger.debug(Literal.SQL + sql);

		return this.jdbcOperations.queryForObject(sql, Integer.class, finID) > 0;
	}

	@Override
	public boolean isAdviseUploadExist(long finID) {
		String sql = "Select Count(FinID) From AdviseUploads_Temp Where FinID = ? and Status = ?";

		logger.debug(Literal.SQL.concat(sql));

		return this.jdbcOperations.queryForObject(sql, Integer.class, finID, UploadConstants.UPLOAD_STATUS_SUCCESS) > 0;
	}

	@Override
	public boolean isunAdjustablePayables(long finID) {
		String sql = "Select Count(AdviseId) From ManualAdvise Where FinId = ? and AdviseType = ? and (Adviseamount - PaidAmount - WaivedAmount) > ?";

		logger.debug(Literal.SQL.concat(sql));

		return this.jdbcOperations.queryForObject(sql, Integer.class, finID, AdviseType.PAYABLE.id(),
				BigDecimal.ZERO) > 0;
	}

	@Override
	public BigDecimal getOverDueAmount(long finID) {
		String sql = "Select coalesce(sum(AdviseAmount - WaivedAmount - PaidAmount), 0) AdvDue From ManualAdvise Where  FinID = ? and AdviseType = ?";

		logger.debug(Literal.SQL.concat(sql));

		return this.jdbcOperations.queryForObject(sql, BigDecimal.class, finID, AdviseType.RECEIVABLE.id());
	}

	@Override
	public BigDecimal getPayableBalance(long finID, long feeTypeID) {
		String sql = "Select Sum(BalanceAmt) From ManualAdvise Where FinID = ? and AdviseType = ? and FeeTypeID = ?";

		logger.debug(Literal.SQL.concat(sql));

		try {
			return jdbcOperations.queryForObject(sql, BigDecimal.class, finID, AdviseType.PAYABLE.id(), feeTypeID);
		} catch (EmptyResultDataAccessException e) {
			logger.warn(Message.NO_RECORD_FOUND);
			return BigDecimal.ZERO;
		}
	}

	@Override
	public List<ManualAdvise> getReceivableAdvises(long finID) {
		StringBuilder sql = new StringBuilder("Select");
		sql.append(" AdviseID, ma.AdviseType, FinID, FinReference, ma.FeeTypeID,  AdviseAmount");
		sql.append(", PaidAmount, WaivedAmount, ValueDate, BalanceAmt, Status, ft.TaxComponent, ft.TdsReq");
		sql.append(", PaidCGST, PaidSGST, PaidUGST, PaidIGST, PaidCESS");
		sql.append(", WaivedCGST, WaivedSGST, WaivedUGST, WaivedIGST, WaivedCESS");
		sql.append(" From ManualAdvise ma ");
		sql.append(" Inner Join FeeTypes ft on ft.FeeTypeID = ma.FeeTypeID");
		sql.append(" Where ma.FinId = ? and ma.AdviseType = ?");

		if (!ImplementationConstants.MANUAL_ADVISE_FUTURE_DATE) {
			sql.append(" and ValueDate <= ?");
		}

		logger.debug(Literal.SQL.concat(sql.toString()));

		return this.jdbcOperations.query(sql.toString(), ps -> {
			int index = 0;

			ps.setLong(++index, finID);
			ps.setInt(++index, AdviseType.RECEIVABLE.id());

			if (!ImplementationConstants.MANUAL_ADVISE_FUTURE_DATE) {
				ps.setDate(++index, JdbcUtil.getDate(SysParamUtil.getAppDate()));
			}
		}, (rs, rowNum) -> {
			ManualAdvise ma = new ManualAdvise();

			ma.setAdviseID(rs.getLong("AdviseID"));
			ma.setAdviseType(rs.getInt("AdviseType"));
			ma.setFinID(rs.getLong("FinID"));
			ma.setFinReference(rs.getString("FinReference"));
			ma.setFeeTypeID(rs.getLong("FeeTypeID"));
			ma.setAdviseAmount(rs.getBigDecimal("AdviseAmount"));
			ma.setPaidAmount(rs.getBigDecimal("PaidAmount"));
			ma.setWaivedAmount(rs.getBigDecimal("WaivedAmount"));
			ma.setValueDate(rs.getTimestamp("ValueDate"));
			ma.setBalanceAmt(rs.getBigDecimal("BalanceAmt"));
			ma.setStatus(rs.getString("Status"));
			ma.setTaxComponent(rs.getString("TaxComponent"));
			ma.setTdsReq(rs.getBoolean("TdsReq"));
			ma.setPaidCGST(rs.getBigDecimal("PaidCGST"));
			ma.setPaidSGST(rs.getBigDecimal("PaidSGST"));
			ma.setPaidUGST(rs.getBigDecimal("PaidUGST"));
			ma.setPaidIGST(rs.getBigDecimal("PaidIGST"));
			ma.setPaidCESS(rs.getBigDecimal("PaidCESS"));
			ma.setWaivedCGST(rs.getBigDecimal("WaivedCGST"));
			ma.setWaivedSGST(rs.getBigDecimal("WaivedSGST"));
			ma.setWaivedUGST(rs.getBigDecimal("WaivedUGST"));
			ma.setWaivedIGST(rs.getBigDecimal("WaivedIGST"));
			ma.setWaivedCESS(rs.getBigDecimal("WaivedCESS"));

			return ma;
		});
	}
}
