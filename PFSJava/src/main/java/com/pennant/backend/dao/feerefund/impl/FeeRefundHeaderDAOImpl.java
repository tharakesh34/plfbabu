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

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
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

/**
 * Data access layer implementation for <code>PaymentHeader</code> with set of CRUD operations.
 */
public class FeeRefundHeaderDAOImpl extends SequenceDao<FeeRefundHeader> implements FeeRefundHeaderDAO {
	private static Logger logger = LogManager.getLogger(FeeRefundHeaderDAOImpl.class);

	public FeeRefundHeaderDAOImpl() {
		super();
	}

	@Override
	public FinanceMain getFinanceDetails(long finID) {
		StringBuilder sql = new StringBuilder("Select");
		sql.append(" fm.FinID, fm.FinReference, ft.FinType, ft.FinTypeDesc, ft.FinDivision");
		sql.append(", fm.CalRoundingMode, fm.RoundingTarget, fm.FinBranch, fm.CustID, cu.CustCif");
		sql.append(", cu.CustShrtName, curr.CcyCode, fm.FinStartDate, fm.MaturityDate, div.EntityCode");
		sql.append(", fm.ClosingStatus");
		sql.append(" From FinanceMainMaintenance_View fm");
		sql.append(" Inner Join Customers cu on cu.CustID = fm.CustID");
		sql.append(" Inner Join RMTFinanceTypes ft on ft.FinType = fm.FinType");
		sql.append(" Inner Join RMTCurrencies curr on curr.CcyCode = fm.FinCcy");
		sql.append(" Inner Join SMTDivisionDetail div on div.DivisionCode = ft.FinDivision");
		sql.append(" Where FinID = ?");

		logger.debug(Literal.SQL + sql.toString());

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

		logger.trace(Literal.SQL + sql.toString());

		List<ManualAdvise> list = jdbcOperations.query(sql.toString(), ps -> {
			ps.setLong(1, finID);
			ps.setInt(2, 1);
			ps.setInt(3, 0);
		}, (rs, i) -> {
			return getRowMapper(rs);
		});

		return list.stream().sorted((l1, l2) -> l1.getValueDate().compareTo(l2.getValueDate()))
				.collect(Collectors.toList());
	}

	private StringBuilder getSqlQuery() {
		StringBuilder sql = new StringBuilder("Select");
		sql.append(" AdviseID, FinID, FinReference, BalanceAmt, ma.AdviseType, AdviseAmount, ReservedAmt, ValueDate");
		sql.append(", PaidAmount, WaivedAmount, FeeTypeCode, FeeTypeDesc, ft.TaxApplicable, ft.TaxComponent");
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
		ma.setFeeTypeCode(rs.getString("FeeTypeCode"));
		ma.setFeeTypeDesc(rs.getString("FeeTypeDesc"));
		ma.setTaxApplicable(rs.getBoolean("TaxApplicable"));
		ma.setTaxComponent(rs.getString("TaxComponent"));
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
		sql.append("Insert Into FEEREFUNDHEADER");
		sql.append(tableType.getSuffix());
		sql.append(" (FeeRefundId, CustCif");
		sql.append(", FinID, PaymentType, FinType, BranchName");
		sql.append(", PaymentAmount, CreatedOn, ApprovedOn, Status, ApprovalStatus");
		sql.append(", Version, LastMntBy, LastMntOn, RecordStatus");
		sql.append(", RoleCode, NextRoleCode, TaskId, NextTaskId, RecordType, WorkflowId)");
		sql.append(" Values (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)");

		logger.debug(Literal.SQL.concat(sql.toString()));
		if (frh.getFeeRefundId() <= 0) {
			frh.setFeeRefundId(getNextValue("SeqFeeRefundHeader"));
		}

		this.jdbcOperations.update(sql.toString(), ps -> {
			int index = 0;

			ps.setLong(++index, frh.getFeeRefundId());
			ps.setString(++index, frh.getCustCif());
			ps.setLong(++index, frh.getFinID());
			ps.setString(++index, frh.getPaymentType());
			ps.setString(++index, frh.getFinType());
			ps.setString(++index, frh.getBranchName());
			ps.setBigDecimal(++index, frh.getPaymentAmount());
			ps.setDate(++index, (JdbcUtil.getDate(frh.getCreatedOn())));
			ps.setDate(++index, JdbcUtil.getDate(frh.getApprovedOn()));
			ps.setString(++index, frh.getStatus());
			ps.setString(++index, frh.getApprovalStatus());
			ps.setInt(++index, frh.getVersion());
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
		return frh.getFeeRefundId();
	}

	@Override
	public int update(FeeRefundHeader frh, TableType tableType) {
		StringBuilder sql = new StringBuilder("Update");
		sql.append(" FEEREFUNDHEADER");
		sql.append(tableType.getSuffix());
		sql.append(" Set FeeRefundId = ?, CustCif = ?, FinID = ?");
		sql.append(", PaymentType = ?, FinType = ?, BranchName = ?, PaymentAmount = ?");
		sql.append(", CreatedOn = ?, ApprovedOn = ?, Status = ?, ApprovalStatus = ?");
		sql.append(", Version = ?, LastMntBy = ?, LastMntOn = ?, RecordStatus = ?, RoleCode = ?");
		sql.append(", NextRoleCode = ?, TaskId = ?, NextTaskId = ?, RecordType = ?, WorkflowId = ?");
		sql.append(" Where FeeRefundId = ?");

		logger.debug(Literal.SQL.concat(sql.toString()));

		int recordCount = jdbcOperations.update(sql.toString(), ps -> {
			int index = 0;

			ps.setLong(++index, frh.getFeeRefundId());
			ps.setString(++index, frh.getCustCif());
			ps.setLong(++index, frh.getFinID());
			ps.setString(++index, frh.getPaymentType());
			ps.setString(++index, frh.getFinType());
			ps.setString(++index, frh.getBranchName());
			ps.setBigDecimal(++index, frh.getPaymentAmount());
			ps.setDate(++index, (JdbcUtil.getDate(frh.getCreatedOn())));
			ps.setDate(++index, JdbcUtil.getDate(frh.getApprovedOn()));
			ps.setString(++index, frh.getStatus());
			ps.setString(++index, frh.getApprovalStatus());
			ps.setInt(++index, frh.getVersion());
			ps.setLong(++index, frh.getLastMntBy());
			ps.setTimestamp(++index, frh.getLastMntOn());
			ps.setString(++index, frh.getRecordStatus());
			ps.setString(++index, frh.getRoleCode());
			ps.setString(++index, frh.getNextRoleCode());
			ps.setString(++index, frh.getTaskId());
			ps.setString(++index, frh.getNextTaskId());
			ps.setString(++index, frh.getRecordType());
			ps.setLong(++index, frh.getWorkflowId());

			ps.setLong(++index, frh.getFeeRefundId());
		});

		if (recordCount == 0) {
			throw new ConcurrencyException();
		}

		return recordCount;
	}

	@Override
	public void delete(FeeRefundHeader frh, TableType tableType) {
		String sql = "Delete From FEEREFUNDHEADER".concat(tableType.getSuffix()).concat(" Where FeeRefundId = ?");

		logger.debug(Literal.SQL.concat(sql));

		try {
			this.jdbcOperations.update(sql, frh.getFeeRefundId());
		} catch (DataAccessException e) {
			throw new DependencyFoundException(e);
		}
	}

	@Override
	public FeeRefundHeader getFeeRefundHeader(long feeRefundId, String type) {
		StringBuilder sql = new StringBuilder("Select");
		sql.append(" FeeRefundId, CustCif");
		sql.append(", FinID, PaymentType, FinType, BranchName");
		sql.append(", PaymentAmount, CreatedOn, ApprovedOn, Status, ApprovalStatus");
		sql.append(", Version, LastMntBy, LastMntOn, RecordStatus");
		sql.append(", RoleCode, NextRoleCode, TaskId, NextTaskId, RecordType, WorkflowId");
		if (StringUtils.trimToEmpty(type).contains("View")) {
			sql.append(", FinReference, CustName");
		}
		sql.append(" From FEEREFUNDHEADER");
		sql.append(type);
		sql.append(" Where FeeRefundId = ?");

		logger.debug(Literal.SQL + sql.toString());

		try {
			return this.jdbcOperations.queryForObject(sql.toString(), (rs, rowNum) -> {
				FeeRefundHeader frh = new FeeRefundHeader();

				frh.setFeeRefundId(rs.getLong("FeeRefundId"));
				frh.setCustCif(rs.getString("CustCif"));
				frh.setFinID(rs.getLong("FinID"));
				frh.setPaymentType(rs.getString("PaymentType"));
				frh.setFinType(rs.getString("FinType"));
				frh.setBranchName(rs.getString("BranchName"));
				frh.setPaymentAmount(rs.getBigDecimal("PaymentAmount"));
				frh.setCreatedOn(JdbcUtil.getDate(rs.getDate("CreatedOn")));
				frh.setApprovedOn(JdbcUtil.getDate(rs.getDate("ApprovedOn")));
				frh.setStatus(rs.getString("Status"));
				frh.setApprovalStatus(rs.getString("ApprovalStatus"));
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
					frh.setFinReference(rs.getString("FinReference"));
					frh.setCustName(rs.getString("CustName"));
				}

				return frh;
			}, feeRefundId);
		} catch (EmptyResultDataAccessException e) {
			logger.warn(Message.NO_RECORD_FOUND);
			return null;
		}
	}

	@Override
	public void updateApprovalStatus(long feeRefundId, String refundProgress) {
		String sql = "Update FEEREFUNDHEADER Set ApprovalStatus = ? Where FeeRefundId = ?";

		logger.debug(Literal.SQL.concat(sql));

		this.jdbcOperations.update(sql, ps -> {
			ps.setString(1, refundProgress);
			ps.setLong(2, feeRefundId);
		});
	}

	@Override
	public boolean isFileDownloaded(long id, String isDownloaded) {
		String sql = "Select count(Id) From FEEREFUNDHEADER_TEMP Where Id = ? and Progress = ?";
		logger.debug(Literal.SQL.concat(sql));

		try {
			return this.jdbcOperations.queryForObject(sql, (rs, rowNum) -> rs.getInt(1), id,
					PennantConstants.FEE_REFUND_APPROVAL_DOWNLOADED) > 0;
		} catch (EmptyResultDataAccessException e) {
			logger.warn(Message.NO_RECORD_FOUND);
			return false;
		}
	}

	@Override
	public BigDecimal getDueAgainstLoan(long finId) {
		StringBuilder sql = new StringBuilder("Select");
		sql.append(" Sum(ODPRINCIPAL + ODPROFIT + COALESCE(OD.LPPDUE,0) + COALESCE(OD.LPIDUE,0)");
		sql.append(" + COALESCE(MA.ADVDUE,0)) TotalDue FROM FINPFTDETAILS PFT ");
		sql.append(" LEFT JOIN (SELECT SUM(TOTPENALTYBAL) LPPDUE,SUM(LPIBAL)LPIDUE,FINID ");
		sql.append(" FROM FINODDETAILS GROUP BY FINID)OD ON OD.FINID = PFT.FINID ");
		sql.append(" LEFT JOIN (SELECT SUM(ADVISEAMOUNT - WAIVEDAMOUNT - PAIDAMOUNT) ADVDUE, FINID ");
		sql.append(" FROM MANUALADVISE WHERE ADVISETYPE = 2 GROUP BY FINID) MA ON MA.FINID = PFT.FINID ");
		sql.append(" WHERE PFT.FinId = ? Group by PFT.FINID");
		logger.debug(Literal.SQL + sql.toString());

		return this.jdbcOperations.queryForObject(sql.toString(), BigDecimal.class, finId);
	}

	@Override
	public BigDecimal getDueAgainstCustomer(long custId, String coreBankId) {
		StringBuilder sql = new StringBuilder("Select");
		sql.append(" Sum(ODPRINCIPAL + ODPROFIT + COALESCE(OD.LPPDUE,0) + COALESCE(OD.LPIDUE,0)");
		sql.append(" + COALESCE(MA.ADVDUE,0)) TotalDue FROM FINPFTDETAILS PFT ");
		sql.append(" LEFT JOIN (SELECT SUM(TOTPENALTYBAL) LPPDUE,SUM(LPIBAL)LPIDUE,FINID ");
		sql.append(" FROM FINODDETAILS GROUP BY FINID)OD ON OD.FINID = PFT.FINID ");
		sql.append(" LEFT JOIN (SELECT SUM(ADVISEAMOUNT - WAIVEDAMOUNT - PAIDAMOUNT) ADVDUE, FINID ");
		sql.append(" FROM MANUALADVISE WHERE ADVISETYPE = 2 GROUP BY FINID) MA ON MA.FINID = PFT.FINID ");
		sql.append(" INNER JOIN CUSTOMERS C ON C.CUSTID = PFT.CUSTID ");
		// if(corebank) {/* Need add based on implementation of custcorebank functionality
		// sql.append("WHERE pft.custid in");
		// sql.append(" (Select custid from customers where custcorebank = ?) group by c.custcorebank");
		// }else {
		sql.append(" WHERE  PFT.CUSTID = ? GROUP BY PFT.CUSTID ");
		// }

		logger.debug(Literal.SQL + sql.toString());

		// if(corebank) {/* Need add based on implementation of custcorebank functionality
		// return this.jdbcOperations.queryForObject(sql.toString(), BigDecimal.class, coreBankId);
		// }else {
		return this.jdbcOperations.queryForObject(sql.toString(), BigDecimal.class, custId);
		// }
	}
}
