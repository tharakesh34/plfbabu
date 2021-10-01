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
 * * FileName : PaymentInstructionDAOImpl.java * * Author : PENNANT TECHONOLOGIES * * Creation Date : 27-05-2017 * *
 * Modified Date : 27-05-2017 * * Description : * *
 ********************************************************************************************
 * Date Author Version Comments *
 ********************************************************************************************
 * 27-05-2017 PENNANT 0.1 * * * * * * * * *
 ********************************************************************************************
 */
package com.pennant.backend.dao.payment.impl;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.RowMapper;

import com.pennant.backend.dao.payment.PaymentInstructionDAO;
import com.pennant.backend.model.finance.PaymentInstruction;
import com.pennant.backend.util.DisbursementConstants;
import com.pennanttech.pennapps.core.ConcurrencyException;
import com.pennanttech.pennapps.core.DependencyFoundException;
import com.pennanttech.pennapps.core.InterfaceException;
import com.pennanttech.pennapps.core.jdbc.JdbcUtil;
import com.pennanttech.pennapps.core.jdbc.SequenceDao;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pff.core.TableType;
import com.pennanttech.pff.core.util.QueryUtil;

/**
 * Data access layer implementation for <code>PaymentInstruction</code> with set of CRUD operations.
 */
public class PaymentInstructionDAOImpl extends SequenceDao<PaymentInstruction> implements PaymentInstructionDAO {
	private static Logger logger = LogManager.getLogger(PaymentInstructionDAOImpl.class);

	public PaymentInstructionDAOImpl() {
		super();
	}

	@Override
	public PaymentInstruction getPaymentInstruction(long id, String type) {
		StringBuilder sql = getSqlQuery(type);
		sql.append(" Where PaymentInstructionId = ?");

		logger.debug(Literal.SQL + sql.toString());

		try {
			return this.jdbcOperations.queryForObject(sql.toString(), new PaymentInstructionRM(type), id);
		} catch (EmptyResultDataAccessException e) {
			//
		}

		return null;
	}

	@Override
	public PaymentInstruction getPaymentInstructionDetails(long paymentId, String type) {
		StringBuilder sql = getSqlQuery(type);
		sql.append(" Where PaymentId = ?");

		logger.debug(Literal.SQL + sql.toString());

		try {
			return this.jdbcOperations.queryForObject(sql.toString(), new PaymentInstructionRM(type), paymentId);
		} catch (EmptyResultDataAccessException e) {
			//
		}

		return null;
	}

	@Override
	public String save(PaymentInstruction pi, TableType tableType) {
		StringBuilder sql = new StringBuilder("insert into");
		sql.append(" PaymentInstructions");
		sql.append(tableType.getSuffix());
		sql.append(" (PaymentInstructionId, PaymentId, PaymentType, PaymentAmount, Remarks, PartnerBankId");
		sql.append(", IssuingBank, FavourName, FavourNumber, PayableLoc, PrintingLoc, ValueDate, PostDate");
		sql.append(", BankBranchId, AcctHolderName, AccountNo, PhoneCountryCode, PhoneNumber, ClearingDate");
		sql.append(", Status, Active, PaymentCCy, Version, LastMntBy, LastMntOn, RecordStatus, RoleCode");
		sql.append(", NextRoleCode, TaskId, NextTaskId, RecordType, WorkflowId");
		sql.append(") values(");
		sql.append(" ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?");
		sql.append(", ?, ?, ?, ?");
		sql.append(")");

		if (pi.getPaymentInstructionId() <= 0) {
			pi.setPaymentInstructionId(getNextValue("SeqAdvpayment"));
		}

		logger.debug(Literal.SQL + sql.toString());

		try {
			jdbcOperations.update(sql.toString(), ps -> {
				int index = 1;

				ps.setLong(index++, pi.getPaymentInstructionId());
				ps.setLong(index++, pi.getPaymentId());
				ps.setString(index++, pi.getPaymentType());
				ps.setBigDecimal(index++, pi.getPaymentAmount());
				ps.setString(index++, pi.getRemarks());
				ps.setLong(index++, pi.getPartnerBankId());
				ps.setString(index++, pi.getIssuingBank());
				ps.setString(index++, pi.getFavourName());
				ps.setString(index++, pi.getFavourNumber());
				ps.setString(index++, pi.getPayableLoc());
				ps.setString(index++, pi.getPrintingLoc());
				ps.setDate(index++, JdbcUtil.getDate(pi.getValueDate()));
				ps.setDate(index++, JdbcUtil.getDate(pi.getPostDate()));
				ps.setLong(index++, pi.getBankBranchId());
				ps.setString(index++, pi.getAcctHolderName());
				ps.setString(index++, pi.getAccountNo());
				ps.setString(index++, pi.getPhoneCountryCode());
				ps.setString(index++, pi.getPhoneNumber());
				ps.setDate(index++, JdbcUtil.getDate(pi.getClearingDate()));
				ps.setString(index++, pi.getStatus());
				ps.setBoolean(index++, pi.getActive());
				ps.setString(index++, pi.getPaymentCCy());
				ps.setInt(index++, pi.getVersion());
				ps.setLong(index++, pi.getLastMntBy());
				ps.setTimestamp(index++, pi.getLastMntOn());
				ps.setString(index++, pi.getRecordStatus());
				ps.setString(index++, pi.getRoleCode());
				ps.setString(index++, pi.getNextRoleCode());
				ps.setString(index++, pi.getTaskId());
				ps.setString(index++, pi.getNextTaskId());
				ps.setString(index++, pi.getRecordType());
				ps.setLong(index++, pi.getWorkflowId());
			});
		} catch (DuplicateKeyException e) {
			throw new ConcurrencyException(e);
		}

		return String.valueOf(pi.getPaymentInstructionId());
	}

	@Override
	public void update(PaymentInstruction pi, TableType tableType) {
		StringBuilder sql = new StringBuilder("update PaymentInstructions");
		sql.append(tableType.getSuffix());
		sql.append(" Set PaymentId = ?, PaymentType = ?, PaymentAmount = ?, IssuingBank = ?");
		sql.append(", Remarks = ?, PartnerBankId = ?, FavourName = ?, FavourNumber = ?");
		sql.append(", PayableLoc = ?, PrintingLoc = ?, ValueDate = ?, PostDate = ?, BankBranchId = ?");
		sql.append(", AcctHolderName = ?, AccountNo = ?, PhoneCountryCode = ?, PhoneNumber = ?, ClearingDate = ?");
		sql.append(", Active = ?, PaymentCCy = ?, Status = ?, LastMntOn = ?, RecordStatus = ?, RoleCode = ?");
		sql.append(", NextRoleCode = ?, TaskId = ?, NextTaskId = ?, RecordType = ?, WorkflowId = ?");
		sql.append(" Where PaymentInstructionId = ?");

		logger.debug(Literal.SQL + sql.toString());

		int recordCount = jdbcOperations.update(sql.toString(), ps -> {
			int index = 1;

			ps.setLong(index++, pi.getPaymentId());
			ps.setString(index++, pi.getPaymentType());
			ps.setBigDecimal(index++, pi.getPaymentAmount());
			ps.setString(index++, pi.getIssuingBank());
			ps.setString(index++, pi.getRemarks());
			ps.setLong(index++, pi.getPartnerBankId());
			ps.setString(index++, pi.getFavourName());
			ps.setString(index++, pi.getFavourNumber());
			ps.setString(index++, pi.getPayableLoc());
			ps.setString(index++, pi.getPrintingLoc());
			ps.setDate(index++, JdbcUtil.getDate(pi.getValueDate()));
			ps.setDate(index++, JdbcUtil.getDate(pi.getPostDate()));
			ps.setLong(index++, pi.getBankBranchId());
			ps.setString(index++, pi.getAcctHolderName());
			ps.setString(index++, pi.getAccountNo());
			ps.setString(index++, pi.getPhoneCountryCode());
			ps.setString(index++, pi.getPhoneNumber());
			ps.setDate(index++, JdbcUtil.getDate(pi.getClearingDate()));
			ps.setBoolean(index++, pi.getActive());
			ps.setString(index++, pi.getPaymentCCy());
			ps.setString(index++, pi.getStatus());
			ps.setTimestamp(index++, pi.getLastMntOn());
			ps.setString(index++, pi.getRecordStatus());
			ps.setString(index++, pi.getRoleCode());
			ps.setString(index++, pi.getNextRoleCode());
			ps.setString(index++, pi.getTaskId());
			ps.setString(index++, pi.getNextTaskId());
			ps.setString(index++, pi.getRecordType());
			ps.setLong(index++, pi.getWorkflowId());

			ps.setLong(index++, pi.getPaymentInstructionId());
		});

		if (recordCount == 0) {
			throw new ConcurrencyException();
		}
	}

	@Override
	public void delete(PaymentInstruction pi, TableType tableType) {
		StringBuilder sql = new StringBuilder("delete from PaymentInstructions");
		sql.append(tableType.getSuffix());
		sql.append(" Where PaymentId = ?");

		logger.debug(Literal.SQL + sql.toString());

		try {
			int recordCount = jdbcOperations.update(sql.toString(), ps -> ps.setLong(1, pi.getPaymentId()));

			if (recordCount == 0) {
				throw new ConcurrencyException();
			}
		} catch (DataAccessException e) {
			throw new DependencyFoundException(e);
		}
	}

	@Override
	public boolean isDuplicateKey(long id, TableType tableType) {
		String sql;
		String whereClause = "PaymentInstructionId = ?";

		Object[] obj = new Object[] { id };
		switch (tableType) {
		case MAIN_TAB:
			sql = QueryUtil.getCountQuery("PaymentInstructions", whereClause);
			break;
		case TEMP_TAB:
			sql = QueryUtil.getCountQuery("PaymentInstructions_Temp", whereClause);
			break;
		default:
			sql = QueryUtil.getCountQuery(new String[] { "PaymentInstructions_Temp", "PaymentInstructions" },
					whereClause);
			obj = new Object[] { id, id };
			break;
		}

		logger.debug(Literal.SQL + sql);

		return jdbcOperations.queryForObject(sql, Integer.class, obj) > 0;
	}

	@Override
	public int updatePaymentInstrucionStatus(PaymentInstruction pi, TableType mainTab) {
		String sql = "Update PaymentInstructions Set Status = ?, ClearingDate = ?, TransactionRef = ?, RejectReason = ? Where PaymentInstructionId = ? and Status = ?";

		logger.debug(Literal.SQL + sql);

		return this.jdbcOperations.update(sql, ps -> {
			int index = 1;

			ps.setString(index++, pi.getStatus());
			ps.setDate(index++, JdbcUtil.getDate(pi.getClearingDate()));
			ps.setString(index++, pi.getTransactionRef());
			ps.setString(index++, pi.getRejectReason());

			ps.setLong(index++, pi.getPaymentInstructionId());
			ps.setString(index++, DisbursementConstants.STATUS_AWAITCON);
		});
	}

	@Override
	public int getAssignedPartnerBankCount(long partnerBankId, String type) {
		StringBuilder sql = new StringBuilder("Select Count(PartnerBankId) From PaymentInstructions");
		sql.append(StringUtils.trimToEmpty(type));
		sql.append(" Where PartnerBankId = ?");

		logger.debug(Literal.SQL + sql.toString());

		try {
			return this.jdbcOperations.queryForObject(sql.toString(), Integer.class, partnerBankId);
		} catch (EmptyResultDataAccessException e) {
			//
		}

		return 0;
	}

	@Override
	public void updateStatus(PaymentInstruction instruction, String tableType) {
		StringBuilder sql = new StringBuilder("Update PaymentInstructions");
		sql.append(StringUtils.trimToEmpty(tableType));
		sql.append(" Set Status = ? Where PaymentInstructionId = ?");

		logger.debug(Literal.SQL + sql.toString());

		this.jdbcOperations.update(sql.toString(), ps -> {
			int index = 1;

			ps.setString(index++, instruction.getStatus());

			ps.setLong(index++, instruction.getPaymentInstructionId());
		});
	}

	private StringBuilder getSqlQuery(String type) {
		StringBuilder sql = new StringBuilder("Select");
		sql.append(" PaymentInstructionId, PaymentId, PaymentType, PaymentAmount, Remarks, PartnerBankId");
		sql.append(", IssuingBank, FavourName, FavourNumber, PayableLoc, PrintingLoc, ValueDate, PostDate");
		sql.append(", Status, RejectReason, TransactionRef, BankBranchId, AcctHolderName, AccountNo, PhoneCountryCode");
		sql.append(", PhoneNumber, ClearingDate, Active, PaymentCCy, RejectReason");
		sql.append(", Version, LastMntOn, LastMntBy, RecordStatus");
		sql.append(", RoleCode, NextRoleCode, TaskId, NextTaskId, RecordType, WorkflowId");

		if (StringUtils.trimToEmpty(type).contains("View")) {
			sql.append(", PartnerBankCode, PartnerBankName, BankBranchIFSC, BankBranchCode");
			sql.append(", IssuingBankName, PCCityName, BranchDesc, BankName, BranchBankCode");
			sql.append(", PartnerBankAc, PartnerBankAcType");
		}

		sql.append(" From PaymentInstructions");
		sql.append(StringUtils.trimToEmpty(type));

		return sql;
	}

	private class PaymentInstructionRM implements RowMapper<PaymentInstruction> {
		private String type;

		PaymentInstructionRM(String type) {
			this.type = type;
		}

		@Override
		public PaymentInstruction mapRow(ResultSet rs, int rowNum) throws SQLException {
			PaymentInstruction fpd = new PaymentInstruction();

			fpd.setPaymentInstructionId(rs.getLong("PaymentInstructionId"));
			fpd.setPaymentId(rs.getLong("PaymentId"));
			fpd.setPaymentType(rs.getString("PaymentType"));
			fpd.setPaymentAmount(rs.getBigDecimal("PaymentAmount"));
			fpd.setRemarks(rs.getString("Remarks"));
			fpd.setPartnerBankId(rs.getLong("PartnerBankId"));
			fpd.setIssuingBank(rs.getString("IssuingBank"));
			fpd.setFavourName(rs.getString("FavourName"));
			fpd.setFavourNumber(rs.getString("FavourNumber"));
			fpd.setPayableLoc(rs.getString("PayableLoc"));
			fpd.setPrintingLoc(rs.getString("PrintingLoc"));
			fpd.setValueDate(rs.getTimestamp("ValueDate"));
			fpd.setPostDate(rs.getTimestamp("PostDate"));
			fpd.setStatus(rs.getString("Status"));
			fpd.setRejectReason(rs.getString("RejectReason"));
			fpd.setTransactionRef(rs.getString("TransactionRef"));
			fpd.setBankBranchId(rs.getLong("BankBranchId"));
			fpd.setAcctHolderName(rs.getString("AcctHolderName"));
			fpd.setAccountNo(rs.getString("AccountNo"));
			fpd.setPhoneCountryCode(rs.getString("PhoneCountryCode"));
			fpd.setPhoneNumber(rs.getString("PhoneNumber"));
			fpd.setClearingDate(rs.getTimestamp("ClearingDate"));
			fpd.setActive(rs.getBoolean("Active"));
			fpd.setPaymentCCy(rs.getString("PaymentCCy"));
			fpd.setVersion(rs.getInt("Version"));
			fpd.setLastMntOn(rs.getTimestamp("LastMntOn"));
			fpd.setLastMntBy(rs.getLong("LastMntBy"));
			fpd.setRecordStatus(rs.getString("RecordStatus"));
			fpd.setRoleCode(rs.getString("RoleCode"));
			fpd.setNextRoleCode(rs.getString("NextRoleCode"));
			fpd.setTaskId(rs.getString("TaskId"));
			fpd.setNextTaskId(rs.getString("NextTaskId"));
			fpd.setRecordType(rs.getString("RecordType"));
			fpd.setWorkflowId(rs.getLong("WorkflowId"));

			if (StringUtils.trimToEmpty(type).contains("View")) {
				fpd.setPartnerBankCode(rs.getString("PartnerBankCode"));
				fpd.setPartnerBankName(rs.getString("PartnerBankName"));
				fpd.setBankBranchIFSC(rs.getString("BankBranchIFSC"));
				fpd.setBankBranchCode(rs.getString("BankBranchCode"));
				fpd.setIssuingBankName(rs.getString("IssuingBankName"));
				fpd.setpCCityName(rs.getString("PCCityName"));
				fpd.setBranchDesc(rs.getString("BranchDesc"));
				fpd.setBankName(rs.getString("BankName"));
				fpd.setPartnerBankAc(rs.getString("PartnerBankAc"));
				fpd.setPartnerBankAcType(rs.getString("PartnerBankAcType"));
				fpd.setBranchBankCode(rs.getString("BranchBankCode"));
			}

			return fpd;
		}

	}

	@Override
	public long getPymntsCustId(long paymentId) {
		StringBuilder sql = new StringBuilder("Select c.CustID From Customers c");
		sql.append(" Inner Join FinanceMain fm on fm.CustID = c.CustID");
		sql.append(" Inner Join PaymentHeader ph on ph.FinReference = fm.FinReference");
		sql.append(" Where ph.PaymentID = ?");

		logger.debug(Literal.SQL + sql.toString());

		try {
			return this.jdbcOperations.queryForObject(sql.toString(), Long.class, paymentId);
		} catch (Exception e) {
			throw new InterfaceException(Literal.EXCEPTION, e.getMessage());
		}
	}

	@Override
	public boolean isInstructionInProgress(String finReference) {
		StringBuilder sql = new StringBuilder("select count(*)");
		sql.append(" From PaymentInstructions_Temp pi");
		sql.append(" Left Join PaymentHeader_Temp ph on ph.PaymentID = pi.PaymentID");
		sql.append(" Left Join PaymentDetails_Temp pd on pd.PaymentID = ph.PaymentID");
		sql.append(" Where ph.FinReference = ?");

		logger.debug(Literal.SQL + sql.toString());

		return jdbcOperations.queryForObject(sql.toString(), Integer.class, finReference) > 0;
	}
}
