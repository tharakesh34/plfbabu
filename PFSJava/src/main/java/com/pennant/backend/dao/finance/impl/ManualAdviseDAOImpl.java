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
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.RowMapper;

import com.pennant.backend.dao.finance.ManualAdviseDAO;
import com.pennant.backend.model.finance.AdviseDueTaxDetail;
import com.pennant.backend.model.finance.FinanceMain;
import com.pennant.backend.model.finance.ManualAdvise;
import com.pennant.backend.model.finance.ManualAdviseMovements;
import com.pennant.backend.model.finance.ManualAdviseReserve;
import com.pennanttech.pennapps.core.ConcurrencyException;
import com.pennanttech.pennapps.core.DependencyFoundException;
import com.pennanttech.pennapps.core.jdbc.JdbcUtil;
import com.pennanttech.pennapps.core.jdbc.SequenceDao;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pennapps.core.util.DateUtil;
import com.pennanttech.pff.advancepayment.AdvancePaymentUtil.AdvanceRuleCode;
import com.pennanttech.pff.core.TableType;
import com.pennanttech.pff.core.util.QueryUtil;

/**
 * Data access layer implementation for <code>ManualAdvise</code> with set of CRUD operations.
 */
public class ManualAdviseDAOImpl extends SequenceDao<ManualAdvise> implements ManualAdviseDAO {
	private static Logger logger = LogManager.getLogger(ManualAdviseDAOImpl.class);

	public ManualAdviseDAOImpl() {
		super();
	}

	@Override
	public ManualAdvise getManualAdviseById(long adviseID, String type) {
		StringBuilder sql = getManualAdvicequery(type);
		sql.append(" Where adviseID = ?");

		logger.debug(Literal.SQL + sql.toString());

		ManualAdviseRM rowMapper = new ManualAdviseRM(type);

		try {
			return this.jdbcOperations.queryForObject(sql.toString(), rowMapper, adviseID);

		} catch (EmptyResultDataAccessException e) {
			//
		}

		return null;
	}

	@Override
	public ManualAdvise getManualAdviseByReceiptId(long receiptID, String type) {
		StringBuilder sql = getManualAdvicequery(type);
		sql.append(" Where ReceiptID = ?");

		logger.debug(Literal.SQL + sql.toString());

		ManualAdviseRM rowMapper = new ManualAdviseRM(type);

		try {
			return this.jdbcOperations.queryForObject(sql.toString(), rowMapper, receiptID);

		} catch (EmptyResultDataAccessException e) {
			//
		}

		return null;
	}

	@Override
	public String save(ManualAdvise ma, TableType tableType) {
		StringBuilder sql = new StringBuilder("Insert Into");
		sql.append(" ManualAdvise");
		sql.append(tableType.getSuffix());
		sql.append("(AdviseID, AdviseType, FinID, FinReference, FeeTypeID, sequence, AdviseAmount, BounceID");
		sql.append(", ReceiptID, PaidAmount, WaivedAmount, Remarks, ValueDate, PostDate, ReservedAmt");
		sql.append(", BalanceAmt, PaidCGST, PaidSGST, PaidUGST, PaidIGST, PaidCESS, WaivedCGST, WaivedSGST");
		sql.append(", WaivedUGST, WaivedIGST, WaivedCESS, FinSource, Version, LastMntBy, LastMntOn, RecordStatus");
		sql.append(", RoleCode, NextRoleCode, TaskId, NextTaskId, RecordType, WorkflowId, DueCreation");
		sql.append(", LinkedTranId, HoldDue");
		sql.append(") Values(");
		sql.append("?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?");
		sql.append(", ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?");
		sql.append(")");

		if (ma.getAdviseID() <= 0) {
			ma.setAdviseID(getNewAdviseID());
		}

		logger.debug(Literal.SQL + sql.toString());

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
				ps.setLong(index++, ma.getLinkedTranId());
				ps.setBoolean(index++, ma.isHoldDue());
			});
		} catch (DuplicateKeyException e) {
			throw new ConcurrencyException(e);
		}
		return String.valueOf(ma.getAdviseID());
	}

	@Override
	public void update(ManualAdvise ma, TableType tableType) {
		StringBuilder sql = new StringBuilder("Update ManualAdvise");
		sql.append(tableType.getSuffix());
		sql.append(" Set AdviseType = ?, FinID = ?,  FinReference = ?, FeeTypeID = ?");
		sql.append(", Sequence = ?, AdviseAmount = ?, PaidAmount = ?");
		sql.append(", WaivedAmount = ?, Remarks = ?, BounceID = ?, ReceiptID = ?");
		sql.append(", ValueDate = ?, PostDate = ?, ReservedAmt = ?, BalanceAmt = ?");
		sql.append(", PaidCGST = ?, PaidSGST = ?, PaidUGST = ?, PaidIGST = ?, PaidCESS = ?");
		sql.append(", WaivedCGST = ?, WaivedSGST = ?, WaivedIGST = ?, WaivedUGST = ?, WaivedCESS = ?");
		sql.append(", FinSource = ?, DueCreation = ?, LinkedTranId = ?");
		sql.append(", LastMntOn = ?, RecordStatus = ?, RoleCode = ? , NextRoleCode = ?, TaskId = ?, NextTaskId = ?");
		sql.append(", RecordType = ?, WorkflowId = ?");
		sql.append(" Where AdviseID = ?");

		logger.debug(Literal.SQL + sql.toString());

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
			ps.setTimestamp(index++, ma.getLastMntOn());
			ps.setString(index++, ma.getRecordStatus());
			ps.setString(index++, ma.getRoleCode());
			ps.setString(index++, ma.getNextRoleCode());
			ps.setString(index++, ma.getTaskId());
			ps.setString(index++, ma.getNextTaskId());
			ps.setString(index++, ma.getRecordType());
			ps.setLong(index++, ma.getWorkflowId());

			ps.setLong(index++, ma.getAdviseID());

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

		logger.debug(Literal.SQL + sql.toString());

		int recordCount = 0;
		try {
			recordCount = jdbcOperations.update(sql.toString(), ps -> {
				int index = 1;

				ps.setLong(index++, ma.getAdviseID());
				if (tableType == TableType.TEMP_TAB) {
					ps.setTimestamp(index++, ma.getPrevMntOn());
				} else {
					ps.setLong(index++, ma.getVersion() - 1);
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

		jdbcOperations.update(sql.toString(), ma.getAdviseID());
	}

	@Override
	public List<ManualAdvise> getManualAdviseByRef(long finID, int adviseType, String type) {
		StringBuilder sql = getManualAdvicequery(type);
		sql.append(" Where FinID = ? and AdviseType = ?");
		sql.append(" and (AdviseAmount - PaidAmount - WaivedAmount) > 0 Order By FeeTypeID desc");

		logger.debug(Literal.SQL + sql.toString());

		ManualAdviseRM rowMapper = new ManualAdviseRM(type);

		return this.jdbcOperations.query(sql.toString(), ps -> {
			int index = 1;

			ps.setLong(index++, finID);
			ps.setInt(index++, adviseType);
		}, rowMapper);
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

		logger.debug(Literal.SQL + sql.toString());

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
			ps.setLong(index++, ma.getAdviseID());
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
			ps.setBigDecimal(index++, mam.getWaivedCESS());
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

		logger.debug(Literal.SQL + sql.toString());

		return this.jdbcOperations.query(sql.toString(), ps -> {
			int index = 1;
			ps.setLong(index++, receiptID);
		}, (rs, rowNum) -> {
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
		});
	}

	@Override
	public List<ManualAdviseMovements> getAdviseMovements(long id) {
		StringBuilder sql = new StringBuilder("Select");
		sql.append(" mam.MovementID, mam.MovementDate, mam.MovementAmount");
		sql.append(", mam.PaidAmount, mam.WaivedAmount, mam.Status, rh.ReceiptMode, mam.TaxHeaderId");
		sql.append(", mam.PaidCGST, mam.PaidSGST, mam.PaidUGST, mam.PaidIGST, mam.PaidCESS");
		sql.append(", mam.WaivedCGST, mam.WaivedSGST, mam.WaivedUGST, mam.WaivedIGST, mam.WaivedCESS");
		sql.append(" From ManualAdviseMovements mam");
		sql.append(" Left Join FinReceiptHeader rh ON rh.ReceiptID = mam.ReceiptID");
		sql.append(" Where AdviseID = ?");

		logger.debug(Literal.SQL + sql.toString());

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

			return mam;
		});
	}

	@Override
	public void deleteMovementsByReceiptID(long receiptID, String type) {
		StringBuilder sql = new StringBuilder("Delete From ManualAdviseMovements");
		sql.append(StringUtils.trimToEmpty(type));
		sql.append(" Where ReceiptID = ? ");

		logger.debug(Literal.SQL + sql.toString());

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

		logger.debug(Literal.SQL + sql.toString());

		return this.jdbcOperations.query(sql.toString(), ps -> {
			int index = 1;
			ps.setLong(index++, receiptID);
			ps.setLong(index++, receiptSeqID);

			if (StringUtils.contains(type, "View")) {
				ps.setInt(index++, adviseType);
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

		logger.debug(Literal.SQL + sql.toString());

		jdbcOperations.update(sql.toString(), ps -> {
			int index = 1;

			ps.setString(index++, status);
			ps.setLong(index++, receiptID);
			ps.setLong(index++, receiptSeqID);
		});
	}

	@Override
	public List<ManualAdviseReserve> getPayableReserveList(long receiptSeqID) {
		String sql = "Select ReceiptSeqID, AdviseID, ReservedAmt From ManualAdviseReserve Where ReceiptSeqID = ?";

		logger.debug(Literal.SQL + sql.toString());

		return this.jdbcOperations.query(sql.toString(), ps -> {
			int index = 1;
			ps.setLong(index++, receiptSeqID);
		}, (rs, rowNum) -> {
			ManualAdviseReserve ma = new ManualAdviseReserve();

			ma.setReceiptSeqID(rs.getLong("ReceiptSeqID"));
			ma.setAdviseID(rs.getLong("AdviseID"));
			ma.setReservedAmt(rs.getBigDecimal("ReservedAmt"));

			return ma;
		});
	}

	@Override
	public ManualAdviseReserve getPayableReserve(long receiptSeqID, long payAgainstID) {
		String sql = "Select ReceiptSeqID, AdviseID, ReservedAmt From ManualAdviseReserve Where ReceiptSeqID = ? and AdviseID= ?";

		logger.debug(Literal.SQL + sql);

		try {
			return this.jdbcOperations.queryForObject(sql.toString(), (rs, rowNum) -> {
				ManualAdviseReserve mar = new ManualAdviseReserve();

				mar.setReceiptSeqID(rs.getLong("ReceiptSeqID"));
				mar.setAdviseID(rs.getLong("AdviseID"));
				mar.setReservedAmt(rs.getBigDecimal("ReservedAmt"));

				return mar;
			}, receiptSeqID, payAgainstID);
		} catch (EmptyResultDataAccessException e) {
			//
		}

		return null;

	}

	@Override
	public void savePayableReserveLog(long receiptSeqID, long payAgainstID, BigDecimal reserveAmt) {
		String sql = "Insert Into ManualAdviseReserve (AdviseID, ReceiptSeqID, ReservedAmt) Values(?, ?, ?)";

		logger.debug(Literal.SQL + sql);

		this.jdbcOperations.update(sql, ps -> {
			int index = 1;

			ps.setLong(index++, payAgainstID);
			ps.setLong(index++, receiptSeqID);
			ps.setBigDecimal(index++, reserveAmt);
		});
	}

	@Override
	public void updatePayableReserveLog(long receiptID, long payAgainstID, BigDecimal diffInReserve) {
		String sql = "Update ManualAdviseReserve Set ReservedAmt = ReservedAmt + ? Where ReceiptSeqID = ? and AdviseID = ?";

		logger.debug(Literal.SQL + sql);

		int recordCount = this.jdbcOperations.update(sql, ps -> {
			int index = 1;

			ps.setBigDecimal(index++, diffInReserve);
			ps.setLong(index++, receiptID);
			ps.setLong(index++, payAgainstID);
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

		logger.debug(Literal.SQL + sql.toString());

		this.jdbcOperations.update(sql.toString(), ps -> {
			int index = 1;

			ps.setLong(index++, receiptID);

			if (payAgainstID != 0) {
				ps.setLong(index++, payAgainstID);
			}
		});
	}

	@Override
	public void updatePayableReserve(long payAgainstID, BigDecimal reserveAmt) {
		String sql = "Update ManualAdvise Set ReservedAmt = ReservedAmt + ?, BalanceAmt = BalanceAmt - ? Where AdviseID = ? and BalanceAmt >= ?";

		logger.debug(Literal.SQL + sql);

		int recordCount = this.jdbcOperations.update(sql.toString(), ps -> {
			int index = 1;

			ps.setBigDecimal(index++, reserveAmt);
			ps.setBigDecimal(index++, reserveAmt);
			ps.setLong(index++, payAgainstID);
			ps.setBigDecimal(index++, reserveAmt);
		});

		if (recordCount <= 0) {
			throw new ConcurrencyException();
		}
	}

	@Override
	public void updatePayableReserveAmount(long payAgainstID, BigDecimal reserveAmt) {
		String sql = "Update ManualAdvise Set ReservedAmt = ReservedAmt + ?, BalanceAmt = BalanceAmt - ? Where AdviseID = ? ";

		logger.debug(Literal.SQL);

		int recordCount = this.jdbcOperations.update(sql.toString(), ps -> {
			int index = 1;

			ps.setBigDecimal(index++, reserveAmt);
			ps.setBigDecimal(index++, reserveAmt);
			ps.setLong(index++, payAgainstID);
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

		logger.debug(Literal.SQL + sql.toString());

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

		logger.debug(Literal.SQL);

		int recordCount = this.jdbcOperations.update(sql, ps -> {
			int index = 1;

			ps.setBigDecimal(index++, amount);
			ps.setBigDecimal(index++, amount);
			ps.setInt(index++, 0);
			ps.setLong(index++, adviseID);
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
		sql.append(" Where M.AdviseType <> 2 and m.AdviseAmount > 0 and m.ReceiptId = ?");
		sql.append(" and FeeTypeId not in (Select FeeTypeId From FeeTypes)");

		logger.debug(Literal.SQL + sql.toString());

		try {
			return this.jdbcOperations.queryForObject(sql.toString(), Date.class, receiptId);
		} catch (EmptyResultDataAccessException ede) {
			//
		}

		return null;
	}

	@Override
	public List<Long> getBounceAdvisesListByRef(long finID, int adviseType, String type) {
		StringBuilder sql = new StringBuilder("Select AdviseId");
		sql.append(" From ManualAdvise");
		sql.append(type);
		sql.append(" Where FinID = ? and AdviseType = ? and BounceId > 0");

		logger.debug(Literal.SQL + sql.toString());

		return jdbcOperations.queryForList(sql.toString(), Long.class, finID, adviseType);
	}

	@Override
	public FinanceMain getFinanceDetails(long finID) {
		StringBuilder sql = new StringBuilder("Select");
		sql.append(" fm.FinID, fm.FinReference, ft.FinType, ft.FinTypeDesc LovDescFinTypeName, fm.FinBranch");
		sql.append(", fm.CustId, cu.CustCIF LovDescCustCif, cu.CustShrtName LovDescCustShrtName, SD.EntityCode");
		sql.append(", fm.FinAssetValue, fm.FinStartDate, fm.MaturityDate, fm.FinCcy, fm.TDSApplicable, fm.TdsType");
		sql.append(" From FinanceMain fm");
		sql.append(" Inner Join Customers cu on fm.CustID = cu.CustID");
		sql.append(" Inner Join RmtFinanceTypes ft on ft.FinType = fm.FinType");
		sql.append(" Inner Join SMTDivisiondetail sd On sd.DivisionCode = ft.FinDivision");
		sql.append(" Where fm.FinID = ?");

		logger.debug(Literal.SQL + sql.toString());

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

				return fm;
			}, finID);
		} catch (EmptyResultDataAccessException e) {
			//
		}

		return null;
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

		logger.debug(Literal.SQL + sql.toString());

		return jdbcOperations.query(sql.toString(), ps -> {
			int index = 1;

			ps.setInt(index++, 1);
			ps.setLong(index++, finID);
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
	public BigDecimal getBalanceAmt(long finID) {
		String sql = "Select sum(AdviseAmount - PaidAmount - WaivedAmount) From manualAdvise Where FinID = ? and AdviseType = ?";

		logger.debug(Literal.SQL + sql);

		try {
			return this.jdbcOperations.queryForObject(sql, BigDecimal.class, finID, 1);
		} catch (EmptyResultDataAccessException e) {
			//
		}

		return BigDecimal.ZERO;
	}

	@Override
	public String getTaxComponent(long adviseID, String type) {
		StringBuilder sql = new StringBuilder("Select TaxComponent");
		sql.append(" From ManualAdvise");
		sql.append(type);
		sql.append(" Where adviseID = ?");

		logger.debug(Literal.SQL + sql.toString());

		try {
			return this.jdbcOperations.queryForObject(sql.toString(), String.class, adviseID);
		} catch (EmptyResultDataAccessException e) {
			//
		}

		return null;
	}

	@Override
	public List<ManualAdvise> getManualAdvisesByFinRef(long finID, String type) {
		StringBuilder sql = getManualAdvicequery(type);
		sql.append(" Where FinID = ?");

		logger.debug(Literal.SQL + sql.toString());

		ManualAdviseRM rowMapper = new ManualAdviseRM(type);

		return this.jdbcOperations.query(sql.toString(), ps -> {
			ps.setLong(1, finID);
		}, rowMapper);

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

		logger.debug(Literal.SQL + sql.toString());

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
		sql.append(", ma.PaidCGST, ma.PaidSGST, ma.PaidIGST, ma.PaidUGST, ma.PaidCESS");
		sql.append(", ma.WaivedCGST, ma.WaivedSGST, ma.WaivedIGST,  ma.WaivedUGST, ma.WaivedCESS");
		sql.append(", ma.Remarks, ma.FinSource, ma.LinkedTranId");
		sql.append(", ma.Version, ma.LastMntOn, ma.LastMntBy, ma.RecordStatus");
		sql.append(", ma.RoleCode, ma.NextRoleCode, ma.TaskId, ma.NextTaskId, ma.RecordType, ma.WorkflowId");
		sql.append(" From ManualAdvise_Aview ma");
		sql.append(" Left Join FeeTypes ft on ma.FeeTypeId = ft.FeeTypeId");
		sql.append(" Where FinID = ?");

		logger.debug(Literal.SQL + sql.toString());

		List<ManualAdvise> maList = jdbcOperations.query(sql.toString(), ps -> {
			int index = 1;

			ps.setLong(index++, finID);

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
	public List<ManualAdvise> getManualAdviseByRef(long finID, String feeTypeCode, String type) {
		StringBuilder sql = new StringBuilder("Select");
		sql.append(" AdviseID, AdviseAmount, PaidAmount, WaivedAmount, ReservedAmt, BalanceAmt");
		sql.append(", BounceID, ReceiptID, DueCreation");

		if (StringUtils.trimToEmpty(type).contains("View")) {
			sql.append(", FeeTypeCode, FeeTypeDesc, BounceCode, BounceCodeDesc");
		}

		sql.append(" From ManualAdvise");
		sql.append(StringUtils.trimToEmpty(type));
		sql.append(" Where FinID = ? and FeeTypeCode = ?");

		logger.debug(Literal.SQL + sql.toString());

		List<ManualAdvise> maList = this.jdbcOperations.query(sql.toString(), ps -> {
			int index = 1;
			ps.setLong(index++, finID);
			ps.setString(index++, feeTypeCode);
		}, (rs, rowNum) -> {
			ManualAdvise ma = new ManualAdvise();

			ma.setAdviseID(rs.getLong("AdviseID"));
			ma.setAdviseAmount(rs.getBigDecimal("AdviseAmount"));
			ma.setPaidAmount(rs.getBigDecimal("PaidAmount"));
			ma.setWaivedAmount(rs.getBigDecimal("WaivedAmount"));
			ma.setReservedAmt(rs.getBigDecimal("ReservedAmt"));
			ma.setBalanceAmt(rs.getBigDecimal("BalanceAmt"));
			ma.setBounceID(rs.getLong("BounceID"));
			ma.setReceiptID(rs.getLong("ReceiptID"));
			ma.setDueCreation(rs.getBoolean("DueCreation"));

			if (StringUtils.trimToEmpty(type).contains("View")) {
				ma.setFeeTypeCode(rs.getString("FeeTypeCode"));
				ma.setFeeTypeDesc(rs.getString("FeeTypeDesc"));
				ma.setBounceCode(rs.getString("BounceCode"));
				ma.setBounceCodeDesc(rs.getString("BounceCodeDesc"));
			}

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

		logger.debug(Literal.SQL + sql.toString());

		return this.jdbcOperations.query(sql.toString(), ps -> {
			int index = 1;

			ps.setInt(index++, adviseType);
			ps.setLong(index++, feeTypeID);
		}, (rs, rowNum) -> {
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
		});

	}

	@Override
	public void updatePaidAmountOnly(long adviseID, BigDecimal amount) {
		String sql = "Update ManualAdvise Set PaidAmount = PaidAmount + ?, BalanceAmt = BalanceAmt - ? Where AdviseID = ?";

		logger.debug(Literal.SQL + sql);

		int recordCount = this.jdbcOperations.update(sql, ps -> {
			int index = 1;

			ps.setBigDecimal(index++, amount);
			ps.setBigDecimal(index++, amount);
			ps.setLong(index++, adviseID);
		});

		if (recordCount <= 0) {
			throw new ConcurrencyException();
		}
	}

	@Override
	public List<ManualAdvise> getManualAdviseByRefAndFeeCode(long finID, int adviseType, String feeTypeCode) {
		StringBuilder sql = new StringBuilder("Select");
		sql.append(" AdviseID, AdviseType, FinID, FinReference, FeeTypeID, Sequence, AdviseAmount, BounceID");
		sql.append(", ReceiptID, PaidAmount, WaivedAmount, Remarks, ValueDate, PostDate, ReservedAmt");
		sql.append(", BalanceAmt, FeeTypeCode, FeeTypeDesc, DueCreation, Version, LastMntOn, LastMntBy, RecordStatus");
		sql.append(", RoleCode, NextRoleCode, TaskId, NextTaskId, RecordType, WorkflowId");
		sql.append(" From ManualAdvise_Aview");
		sql.append(" Where FinID = ? and AdviseType = ? and FeeTypeCode = ?");

		logger.debug(Literal.SQL + sql.toString());

		List<ManualAdvise> maList = this.jdbcOperations.query(sql.toString(), ps -> {
			int index = 1;
			ps.setLong(index++, finID);
			ps.setInt(index++, adviseType);
			ps.setString(index++, feeTypeCode);
		}, (rs, rowNum) -> {
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

			return ma;
		});

		return maList.stream().sorted((f1, f2) -> DateUtil.compare(f1.getValueDate(), f2.getValueDate()))
				.collect(Collectors.toList());
	}

	@Override
	public List<ManualAdvise> getManualAdviseByRef(long finID, int adviseType, String type, Date valuDate) {
		StringBuilder sql = new StringBuilder("Select");
		sql.append(" AdviseID, AdviseAmount, PaidAmount, WaivedAmount, ReservedAmt, BalanceAmt");
		sql.append(", BounceID, ReceiptID, PaidCGST, PaidSGST, PaidUGST, PaidIGST, PaidCESS, WaivedCGST, WaivedSGST");
		sql.append(", WaivedUGST, WaivedIGST, WaivedCESS, DueCreation");

		if (StringUtils.trimToEmpty(type).contains("View")) {
			sql.append(", FeeTypeCode, FeeTypeDesc, BounceCode, BounceCodeDesc, TaxApplicable, TaxComponent, TdsReq");
		}

		sql.append(" From ManualAdvise");
		sql.append(StringUtils.trimToEmpty(type));
		sql.append(" Where FinID = ? and AdviseType = ?");

		logger.debug(Literal.SQL + sql.toString());

		return this.jdbcOperations.query(sql.toString(), ps -> {
			int index = 1;

			ps.setLong(index++, finID);
			ps.setInt(index++, adviseType);
		}, (rs, rowNum) -> {
			ManualAdvise ma = new ManualAdvise();

			ma.setAdviseID(rs.getLong("AdviseID"));
			ma.setAdviseAmount(rs.getBigDecimal("AdviseAmount"));
			ma.setPaidAmount(rs.getBigDecimal("PaidAmount"));
			ma.setWaivedAmount(rs.getBigDecimal("WaivedAmount"));
			ma.setReservedAmt(rs.getBigDecimal("ReservedAmt"));
			ma.setBalanceAmt(rs.getBigDecimal("BalanceAmt"));
			ma.setBounceID(rs.getLong("BounceID"));
			ma.setReceiptID(rs.getLong("ReceiptID"));
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
			ma.setDueCreation(rs.getBoolean("DueCreation"));

			if (StringUtils.trimToEmpty(type).contains("View")) {
				ma.setFeeTypeCode(rs.getString("FeeTypeCode"));
				ma.setFeeTypeDesc(rs.getString("FeeTypeDesc"));
				ma.setBounceCode(rs.getString("BounceCode"));
				ma.setBounceCodeDesc(rs.getString("BounceCodeDesc"));
				ma.setTaxApplicable(rs.getBoolean("TaxApplicable"));
				ma.setTaxComponent(rs.getString("TaxComponent"));
				ma.setTdsReq(rs.getBoolean("TdsReq"));
			}

			return ma;
		});

	}

	@Override
	public Date getManualAdviseDate(long finID, Date valueDate, String type, int adviseType) {
		StringBuilder sql = new StringBuilder("Select max(ValueDate)");
		sql.append(" From ManualAdvise");
		sql.append(type);
		sql.append(" Where  FinID = ? and AdviseType = ? and ValueDate > ?");
		sql.append(" and (AdviseAmount - PaidAmount - WaivedAmount) > 0");

		try {
			return jdbcOperations.queryForObject(sql.toString(), Date.class, finID, adviseType, valueDate);
		} catch (EmptyResultDataAccessException e) {
			//
		}

		return null;
	}

	@Override
	public List<ManualAdviseMovements> getInProcManualAdvMovmnts(List<Long> receiptList) {
		StringBuilder sql = new StringBuilder();
		sql.append(" Select AdviseID, sum(PaidAmount) PaidAmount, sum(WaivedAmount) WaivedAmount");
		sql.append(" From ManualAdviseMovements");
		sql.append(" Where ReceiptID in (");
		sql.append(commaJoin(receiptList));
		sql.append(")");

		logger.debug(Literal.SQL + sql.toString());

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

		logger.debug(Literal.SQL + sql.toString());

		return this.jdbcOperations.query(sql.toString(), ps -> {
			int index = 1;

			ps.setLong(index++, receiptID);
			ps.setLong(index++, receiptSeqID);
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
			mam.setTdsPaid(rs.getBigDecimal("TdsPaid"));

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

		logger.debug(Literal.SQL + sql.toString());

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
			//
		}

		return null;
	}

	@Override
	public List<ManualAdvise> getPreviousAdvPayments(long finID) {
		StringBuilder sql = new StringBuilder("Select");
		sql.append(" ma.FeeTypeID, ft.FeeTypeCode, ma.AdviseType, ma.AdviseAmount");
		sql.append(" From ManualAdvise ma");
		sql.append(" Inner Join FeeTypes ft on ft.FeeTypeID = ma.FeeTypeID");
		sql.append(" Where FinID = ? and FeeTypeCode in (?, ?)");

		logger.debug(Literal.SQL + sql.toString());

		return this.jdbcOperations.query(sql.toString(), ps -> {
			int index = 1;

			ps.setLong(index++, finID);
			ps.setString(index++, AdvanceRuleCode.ADVINT.name());
			ps.setString(index++, AdvanceRuleCode.ADVEMI.name());

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

		logger.debug(Literal.SQL + sql.toString());

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
			ps.setObject(index, JdbcUtil.getLong(adt.getInvoiceID()));
		});

	}

	@Override
	public boolean isAdviseDueCreated(long adviseID) {
		String sql = "Select count(AdviseID) From AdviseDueTaxDetail Where AdviseID = ?";

		logger.debug(Literal.SQL + sql);

		try {
			return this.jdbcOperations.queryForObject(sql, Integer.class, adviseID) > 0;
		} catch (EmptyResultDataAccessException e) {
			//
		}

		return false;
	}

	@Override
	public Long getDebitInvoiceID(long adviseID) {
		String sql = "Select InvoiceID  From AdviseDueTaxDetail Where AdviseID = ? ";

		logger.debug(Literal.SQL + sql);

		try {
			return this.jdbcOperations.queryForObject(sql.toString(), Long.class, adviseID);
		} catch (EmptyResultDataAccessException e) {
			//
		}

		return null;
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

		logger.debug(Literal.SQL + sql.toString());

		int recordCount = this.jdbcOperations.update(sql.toString(), ps -> {
			int index = 1;

			ps.setBigDecimal(index++, amount);
			ps.setBigDecimal(index++, amount);
			ps.setLong(index++, adviseID);
		});

		if (recordCount <= 0) {
			throw new ConcurrencyException();
		}

	}

	@Override
	public ManualAdviseMovements getAdvMovByReceiptSeq(long receiptID, long receiptSeqID, long adviseId, String type) {
		StringBuilder sql = new StringBuilder("Select");
		sql.append(" MovementID, AdviseID, MovementDate, MovementAmount, PaidAmount, WaivedAmount");
		sql.append(", Status, ReceiptID, ReceiptSeqID, TaxHeaderId");

		if (StringUtils.contains(type, "View")) {
			sql.append(", FeeTypeCode, FeeTypeDesc, TaxApplicable, TaxComponent");
		}

		sql.append(" From ManualAdviseMovements");
		sql.append(StringUtils.trimToEmpty(type));
		sql.append(" Where ReceiptID = ? and ReceiptSeqID = ? and AdviseID = ?");

		logger.debug(Literal.SQL + sql.toString());

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
			}, receiptID, receiptSeqID, adviseId);
		} catch (EmptyResultDataAccessException e) {
			//
		}
		return null;
	}

	@Override
	public BigDecimal getReceivableAmt(long finID, boolean isBounce) {
		StringBuilder sql = new StringBuilder("Select");
		sql.append(" Coalesce(Sum(AdviseAmount - PaidAmount - WaivedAmount), 0) Amount");
		sql.append(" from ManualAdvise");
		sql.append(" Where FinID = ? and AdviseType = ? ");

		if (isBounce) {
			sql.append(" and (BounceId > 0 or FeeTypeID IN (Select FeeTypeID from FeeTypes Where FeeTypeCode = ?))");
		} else {
			sql.append(" and FeeTypeID Not IN (Select FeeTypeID from FeeTypes Where FeeTypeCode = ?)");
		}

		logger.debug(Literal.SQL + sql.toString());

		try {
			return this.jdbcOperations.queryForObject(sql.toString(), BigDecimal.class, finID, 1, "BOUNCE");
		} catch (EmptyResultDataAccessException e) {
			//
		}

		return BigDecimal.ZERO;
	}

	private StringBuilder getManualAdvicequery(String type) {
		StringBuilder sql = new StringBuilder("Select");
		sql.append(" AdviseID, AdviseType, FinID, FinReference, FeeTypeID, Sequence, AdviseAmount, BounceID");
		sql.append(", LinkedTranId, ReceiptID, PaidAmount, WaivedAmount, Remarks, ValueDate, PostDate, ReservedAmt");
		sql.append(", BalanceAmt, PaidCGST, PaidSGST, PaidUGST, PaidIGST, PaidCESS");
		sql.append(", WaivedCGST, WaivedSGST, WaivedUGST, WaivedIGST, WaivedCESS, FinSource, DueCreation");

		if (StringUtils.trimToEmpty(type).contains("View")) {
			sql.append(", FeeTypeCode, FeeTypeDesc, TaxApplicable, TaxComponent, TDSReq, BounceCode");
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

			if (type.contains("View")) {
				ma.setFeeTypeCode(rs.getString("FeeTypeCode"));
				ma.setFeeTypeDesc(rs.getString("FeeTypeDesc"));
				ma.setTaxApplicable(rs.getBoolean("taxApplicable"));
				ma.setTaxComponent(rs.getString("taxComponent"));
				ma.setTdsReq(rs.getBoolean("TDSReq"));
				ma.setBounceCode(rs.getString("BounceCode"));
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

			return ma;

		}
	}

	private String commaJoin(List<Long> headerIdList) {
		return headerIdList.stream().map(e -> "?").collect(Collectors.joining(", "));
	}

	@Override
	public Date getMaxValueDateOfRcv(long finID) {
		String sql = "Select max(ValueDate) from ManualAdvise Where FinID = ? and AdviseType = ?";

		logger.debug(Literal.SQL + sql);

		try {
			return this.jdbcOperations.queryForObject(sql, Date.class, finID, 1);
		} catch (EmptyResultDataAccessException e) {
			//
		}

		return null;
	}

}
