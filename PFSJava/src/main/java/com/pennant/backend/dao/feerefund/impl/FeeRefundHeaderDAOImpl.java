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
 * * FileName : PaymentHeaderDAOImpl.java * * Author : PENNANT TECHONOLOGIES * * Creation Date : 27-05-2017 * * Modified
 * Date : 27-05-2017 * * Description : * *
 ********************************************************************************************
 * Date Author Version Comments *
 ********************************************************************************************
 * 27-05-2017 PENNANT 0.1 * * * * * * * * *
 ********************************************************************************************
 */
package com.pennant.backend.dao.feerefund.impl;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.lang.StringUtils;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.EmptyResultDataAccessException;

import com.pennant.backend.dao.feerefund.FeeRefundHeaderDAO;
import com.pennant.backend.model.feerefund.FeeRefundHeader;
import com.pennant.backend.model.finance.FinanceMain;
import com.pennant.backend.model.finance.ManualAdvise;
import com.pennant.backend.util.PennantConstants;
import com.pennanttech.pennapps.core.ConcurrencyException;
import com.pennanttech.pennapps.core.DependencyFoundException;
import com.pennanttech.pennapps.core.jdbc.JdbcUtil;
import com.pennanttech.pennapps.core.jdbc.SequenceDao;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pennapps.core.resource.Message;
import com.pennanttech.pff.core.TableType;

public class FeeRefundHeaderDAOImpl extends SequenceDao<FeeRefundHeader> implements FeeRefundHeaderDAO {

	public FeeRefundHeaderDAOImpl() {
		super();
	}

	@Override
	public FinanceMain getFinanceDetails(long finID) {
		StringBuilder sql = new StringBuilder("Select");
		sql.append(" fm.FinID, fm.FinReference, ft.FinType, ft.FinTypeDesc, ft.FinDivision");
		sql.append(", fm.CalRoundingMode, fm.RoundingTarget, fm.FinBranch, fm.CustID, cu.CustCif");
		sql.append(", cu.CustShrtName, curr.CcyCode, fm.FinStartDate, fm.MaturityDate, div.EntityCode");
		sql.append(", fm.ClosingStatus, fm.RcdMaintainSts, fm.WriteoffLoan");
		sql.append(" From FinanceMain fm");
		sql.append(" Inner Join Customers cu on cu.CustID = fm.CustID");
		sql.append(" Inner Join RMTFinanceTypes ft on ft.FinType = fm.FinType");
		sql.append(" Inner Join RMTCurrencies curr on curr.CcyCode = fm.FinCcy");
		sql.append(" Inner Join SMTDivisionDetail div on div.DivisionCode = ft.FinDivision");
		sql.append(" Where FinID = ?");

		logger.debug(Literal.SQL.concat(sql.toString()));

		try {
			return jdbcOperations.queryForObject(sql.toString(), (rs, rowNum) -> {
				FinanceMain fm = new FinanceMain();

				fm.setFinID(rs.getLong("FinID"));
				fm.setFinReference(rs.getString("FinReference"));
				fm.setFinType(rs.getString("FinType"));
				fm.setLovDescFinTypeName(rs.getString("FinTypeDesc"));
				fm.setFinPurpose(rs.getString("FinDivision"));
				fm.setCalRoundingMode(rs.getString("CalRoundingMode"));
				fm.setRoundingTarget(rs.getInt("RoundingTarget"));
				fm.setFinBranch(rs.getString("FinBranch"));
				fm.setCustID(rs.getLong("CustID"));
				fm.setLovDescCustCIF(rs.getString("CustCif"));
				fm.setLovDescCustShrtName(rs.getString("CustShrtName"));
				fm.setFinCcy(rs.getString("CcyCode"));
				fm.setFinStartDate(rs.getDate("FinStartDate"));
				fm.setMaturityDate(rs.getDate("MaturityDate"));
				fm.setEntityCode(rs.getString("EntityCode"));
				fm.setLovDescEntityCode(rs.getString("EntityCode"));
				fm.setClosingStatus(rs.getString("ClosingStatus"));
				fm.setRcdMaintainSts(rs.getString("RcdMaintainSts"));
				fm.setWriteoffLoan(rs.getBoolean("WriteoffLoan"));

				return fm;
			}, finID);
		} catch (EmptyResultDataAccessException e) {
			logger.warn(Message.NO_RECORD_FOUND);
			return null;
		}
	}

	@Override
	public List<ManualAdvise> getManualAdvise(long finID) {
		StringBuilder sql = getSqlQuery();
		sql.append(" Where FinID = ? and ma.AdviseType = ? and HoldDue = ?");

		logger.debug(Literal.SQL.concat(sql.toString()));

		List<ManualAdvise> list = jdbcOperations.query(sql.toString(), ps -> {
			ps.setLong(1, finID);
			ps.setInt(2, 1);
			ps.setInt(3, 0);
		}, (rs, i) -> getRowMapper(rs));

		return list.stream().sorted((l1, l2) -> l1.getValueDate().compareTo(l2.getValueDate()))
				.collect(Collectors.toList());
	}

	private StringBuilder getSqlQuery() {
		StringBuilder sql = new StringBuilder("Select");
		sql.append(" AdviseID, FinID, FinReference, BalanceAmt, ma.AdviseType, AdviseAmount, ReservedAmt, ValueDate");
		sql.append(", PaidAmount, WaivedAmount, ma.FeeTypeId, FeeTypeCode, FeeTypeDesc ");
		sql.append(", ft.TaxApplicable, ft.TaxComponent, ma.BounceID");
		sql.append(", PaidCGST, PaidIGST, PaidSGST, PaidUGST, PaidCESS");
		sql.append(", WaivedCGST, WaivedIGST, WaivedSGST, WaivedUGST, WaivedCESS");
		sql.append(" From ManualAdvise ma");
		sql.append(" Inner Join FeeTypes ft on ft.FeeTypeId = ma.FeeTypeId");

		return sql;
	}

	private ManualAdvise getRowMapper(ResultSet rs) throws SQLException {
		ManualAdvise ma = new ManualAdvise();

		ma.setAdviseID(rs.getLong("AdviseID"));
		ma.setFinID(rs.getLong("FinID"));
		ma.setFinReference(rs.getString("FinReference"));
		ma.setBalanceAmt(rs.getBigDecimal("BalanceAmt"));
		ma.setAdviseType(rs.getInt("AdviseType"));
		ma.setAdviseAmount(rs.getBigDecimal("AdviseAmount"));
		ma.setReservedAmt(rs.getBigDecimal("ReservedAmt"));
		ma.setValueDate(JdbcUtil.getDate(rs.getDate("ValueDate")));
		ma.setPaidAmount(rs.getBigDecimal("PaidAmount"));
		ma.setWaivedAmount(rs.getBigDecimal("WaivedAmount"));
		ma.setFeeTypeID(rs.getLong("FeeTypeId"));
		ma.setFeeTypeCode(rs.getString("FeeTypeCode"));
		ma.setFeeTypeDesc(rs.getString("FeeTypeDesc"));
		ma.setTaxApplicable(rs.getBoolean("TaxApplicable"));
		ma.setTaxComponent(rs.getString("TaxComponent"));
		ma.setBounceID(rs.getLong("BounceID"));
		ma.setPaidCGST(rs.getBigDecimal("PaidCGST"));
		ma.setPaidSGST(rs.getBigDecimal("PaidSGST"));
		ma.setPaidIGST(rs.getBigDecimal("PaidIGST"));
		ma.setPaidUGST(rs.getBigDecimal("PaidUGST"));
		ma.setPaidCESS(rs.getBigDecimal("PaidCESS"));
		ma.setWaivedCGST(rs.getBigDecimal("WaivedCGST"));
		ma.setWaivedSGST(rs.getBigDecimal("WaivedSGST"));
		ma.setWaivedIGST(rs.getBigDecimal("WaivedIGST"));
		ma.setWaivedUGST(rs.getBigDecimal("WaivedUGST"));
		ma.setWaivedCESS(rs.getBigDecimal("WaivedCESS"));
		return ma;
	}

	@Override
	public long save(FeeRefundHeader frh, TableType tableType) {
		StringBuilder sql = new StringBuilder();
		sql.append("Insert Into Fee_Refund_Header");
		sql.append(tableType.getSuffix());
		sql.append(" (ID, FinID, RefundType, PaymentAmount, OverDueAgainstLoan, OverDueAgainstCustomer");
		sql.append(", Override, Status, ApprovalStatus");
		sql.append(", Version, CreatedBy, CreatedOn, ApprovedBy, ApprovedOn, LastMntBy, LastMntOn");
		sql.append(", RecordStatus, RoleCode, NextRoleCode, TaskId, NextTaskId, RecordType, WorkflowId)");
		sql.append(" Values (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)");

		logger.debug(Literal.SQL.concat(sql.toString()));

		if (frh.getId() <= 0) {
			frh.setId(getNextValue("SeqFee_Refund_Header"));
		}

		this.jdbcOperations.update(sql.toString(), ps -> {
			int index = 0;

			ps.setLong(++index, frh.getId());
			ps.setLong(++index, frh.getFinID());
			ps.setString(++index, frh.getRefundType());
			ps.setBigDecimal(++index, frh.getPaymentAmount());
			ps.setBigDecimal(++index, frh.getOverDueAgainstLoan());
			ps.setBigDecimal(++index, frh.getOverDueAgainstCustomer());
			ps.setBoolean(++index, frh.isOverride());
			ps.setString(++index, frh.getStatus());
			ps.setInt(++index, frh.getApprovalStatus());
			ps.setInt(++index, frh.getVersion());
			ps.setLong(++index, frh.getCreatedBy());
			ps.setTimestamp(++index, frh.getCreatedOn());
			ps.setObject(++index, frh.getApprovedBy());
			ps.setTimestamp(++index, frh.getApprovedOn());
			ps.setLong(++index, frh.getLastMntBy());
			ps.setTimestamp(++index, frh.getLastMntOn());
			ps.setString(++index, frh.getRecordStatus());
			ps.setString(++index, frh.getRoleCode());
			ps.setString(++index, frh.getNextRoleCode());
			ps.setString(++index, frh.getTaskId());
			ps.setString(++index, frh.getNextTaskId());
			ps.setString(++index, frh.getRecordType());
			ps.setLong(++index, frh.getWorkflowId());
		});
		return frh.getId();
	}

	@Override
	public int update(FeeRefundHeader frh, TableType tableType) {
		StringBuilder sql = new StringBuilder("Update Fee_Refund_Header");
		sql.append(tableType.getSuffix());
		sql.append(" Set PaymentAmount = ?, OverDueAgainstLoan = ?, OverDueAgainstCustomer = ?");
		sql.append(", Override = ?, Status = ?, ApprovalStatus = ?");
		sql.append(", ApprovedBy = ?, ApprovedOn = ?, LastMntBy = ?, LastMntOn = ?");
		sql.append(", Version = ?, RecordStatus = ?, RoleCode = ?, NextRoleCode = ?");
		sql.append(", TaskId = ?, NextTaskId = ?, RecordType = ?, WorkflowId = ?");
		sql.append(" Where ID = ?");

		logger.debug(Literal.SQL.concat(sql.toString()));

		int recordCount = jdbcOperations.update(sql.toString(), ps -> {
			int index = 0;

			ps.setBigDecimal(++index, frh.getPaymentAmount());
			ps.setBigDecimal(++index, frh.getOverDueAgainstLoan());
			ps.setBigDecimal(++index, frh.getOverDueAgainstCustomer());
			ps.setBoolean(++index, frh.isOverride());
			ps.setString(++index, frh.getStatus());
			ps.setInt(++index, frh.getApprovalStatus());
			ps.setObject(++index, frh.getApprovedBy());
			ps.setTimestamp(++index, frh.getApprovedOn());
			ps.setLong(++index, frh.getLastMntBy());
			ps.setTimestamp(++index, frh.getLastMntOn());
			ps.setInt(++index, frh.getVersion());
			ps.setString(++index, frh.getRecordStatus());
			ps.setString(++index, frh.getRoleCode());
			ps.setString(++index, frh.getNextRoleCode());
			ps.setString(++index, frh.getTaskId());
			ps.setString(++index, frh.getNextTaskId());
			ps.setString(++index, frh.getRecordType());
			ps.setLong(++index, frh.getWorkflowId());

			ps.setLong(++index, frh.getId());
		});

		if (recordCount == 0) {
			throw new ConcurrencyException();
		}

		return recordCount;
	}

	@Override
	public void delete(FeeRefundHeader frh, TableType tableType) {
		String sql = "Delete From Fee_Refund_Header".concat(tableType.getSuffix()).concat(" Where ID = ?");

		logger.debug(Literal.SQL.concat(sql));

		try {
			this.jdbcOperations.update(sql, frh.getId());
		} catch (DataAccessException e) {
			throw new DependencyFoundException(e);
		}
	}

	@Override
	public FeeRefundHeader getFeeRefundHeader(long id, String type) {
		StringBuilder sql = new StringBuilder("Select");
		sql.append(" ID, FinID, RefundType, PaymentAmount, Override, Status, ApprovalStatus");
		sql.append(", CreatedOn, ApprovedOn, Version, LastMntBy, LastMntOn, RecordStatus");
		sql.append(", RoleCode, NextRoleCode, TaskId, NextTaskId, RecordType, WorkflowId");

		if (StringUtils.trimToEmpty(type).contains("View")) {
			sql.append(", CustID, CustCif, CustShrtName, FinReference, FinType, BranchDesc");
		}

		sql.append(" From Fee_Refund_Header");
		sql.append(type);
		sql.append(" Where ID = ?");

		logger.debug(Literal.SQL.concat(sql.toString()));

		try {
			return this.jdbcOperations.queryForObject(sql.toString(), (rs, rowNum) -> {
				FeeRefundHeader frh = new FeeRefundHeader();

				frh.setId(rs.getLong("ID"));
				frh.setFinID(rs.getLong("FinID"));
				frh.setRefundType(rs.getString("RefundType"));
				frh.setPaymentAmount(rs.getBigDecimal("PaymentAmount"));
				frh.setOverride(rs.getBoolean("Override"));
				frh.setStatus(rs.getString("Status"));
				frh.setApprovalStatus(rs.getInt("ApprovalStatus"));
				frh.setCreatedOn(rs.getTimestamp("CreatedOn"));
				frh.setApprovedOn(rs.getTimestamp("ApprovedOn"));
				frh.setVersion(rs.getInt("Version"));
				frh.setLastMntBy(rs.getLong("LastMntBy"));
				frh.setLastMntOn(rs.getTimestamp("LastMntOn"));
				frh.setRecordStatus(rs.getString("RecordStatus"));
				frh.setRoleCode(rs.getString("RoleCode"));
				frh.setNextRoleCode(rs.getString("NextRoleCode"));
				frh.setTaskId(rs.getString("TaskId"));
				frh.setNextTaskId(rs.getString("NextTaskId"));
				frh.setRecordType(rs.getString("RecordType"));
				frh.setWorkflowId(rs.getLong("WorkflowId"));

				if (StringUtils.trimToEmpty(type).contains("View")) {
					frh.setCustId(rs.getLong("CustID"));
					frh.setCustCif(rs.getString("CustCif"));
					frh.setCustShrtName(rs.getString("CustShrtName"));
					frh.setFinReference(rs.getString("FinReference"));
					frh.setFinType(rs.getString("FinType"));
					frh.setBranchDesc(rs.getString("BranchDesc"));
				}

				return frh;
			}, id);
		} catch (EmptyResultDataAccessException e) {
			logger.warn(Message.NO_RECORD_FOUND);
			return null;
		}
	}

	@Override
	public void updateApprovalStatus(long id, int refundProgress) {
		String sql = "Update Fee_Refund_Header_Temp Set ApprovalStatus = ? Where ID = ?";

		logger.debug(Literal.SQL.concat(sql));

		this.jdbcOperations.update(sql, ps -> {
			ps.setInt(1, refundProgress);
			ps.setLong(2, id);
		});
	}

	@Override
	public boolean isFileDownloaded(long id, int isDownloaded) {
		String sql = "Select count(Id) From Fee_Refund_Header_Temp Where Id = ? and ApprovalStatus = ?";

		logger.debug(Literal.SQL.concat(sql));

		try {
			return this.jdbcOperations.queryForObject(sql, (rs, rowNum) -> rs.getInt(1), id,
					PennantConstants.FEE_REFUND_APPROVAL_DOWNLOADED) > 0;
		} catch (EmptyResultDataAccessException e) {
			logger.warn(Message.NO_RECORD_FOUND);
			return false;
		}
	}
}
