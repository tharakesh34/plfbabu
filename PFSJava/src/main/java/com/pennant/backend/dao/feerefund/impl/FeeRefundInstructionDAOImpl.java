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
package com.pennant.backend.dao.feerefund.impl;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.apache.commons.lang.StringUtils;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.RowMapper;

import com.pennant.backend.dao.feerefund.FeeRefundInstructionDAO;
import com.pennant.backend.model.feerefund.FeeRefundInstruction;
import com.pennanttech.pennapps.core.ConcurrencyException;
import com.pennanttech.pennapps.core.DependencyFoundException;
import com.pennanttech.pennapps.core.jdbc.JdbcUtil;
import com.pennanttech.pennapps.core.jdbc.SequenceDao;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pennapps.core.resource.Message;
import com.pennanttech.pff.core.TableType;

public class FeeRefundInstructionDAOImpl extends SequenceDao<FeeRefundInstruction> implements FeeRefundInstructionDAO {

	public FeeRefundInstructionDAOImpl() {
		super();
	}

	@Override
	public FeeRefundInstruction getFeeRefundInstructionDetails(long headerID, String type) {
		StringBuilder sql = getSqlQuery(type);
		sql.append(" Where HeaderID = ?");

		logger.debug(Literal.SQL.concat(sql.toString()));

		try {
			return this.jdbcOperations.queryForObject(sql.toString(), new FeeRefundInstructionRM(type), headerID);
		} catch (EmptyResultDataAccessException e) {
			logger.warn(Message.NO_RECORD_FOUND);
			return null;
		}
	}

	@Override
	public long save(FeeRefundInstruction fri, TableType tableType) {
		StringBuilder sql = new StringBuilder("insert into");
		sql.append(" Fee_Refund_Instructions");
		sql.append(tableType.getSuffix());
		sql.append(" (ID, HeaderID, PaymentType, PaymentAmount, Remarks, PartnerBankId");
		sql.append(", IssuingBank, FavourName, FavourNumber, PayableLoc, PrintingLoc, ValueDate, PostDate");
		sql.append(", BankBranchId, AcctHolderName, AccountNo, PhoneCountryCode, PhoneNumber, ClearingDate");
		sql.append(", Status, Active, PaymentCCy, Lei");
		sql.append(", Version, CreatedBy, CreatedOn, ApprovedBy, ApprovedOn, LastMntBy, LastMntOn");
		sql.append(", RecordStatus, RoleCode, NextRoleCode, TaskId, NextTaskId, RecordType, WorkflowId");
		sql.append(") values(");
		sql.append(" ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?");
		sql.append(", ?, ?, ?, ?, ?, ?, ?, ?, ?");
		sql.append(")");

		if (fri.getId() <= 0) {
			fri.setId(getNextValue("SeqFee_Refund_Instructions"));
		}

		logger.debug(Literal.SQL.concat(sql.toString()));

		try {
			jdbcOperations.update(sql.toString(), ps -> {
				int index = 1;

				ps.setLong(index++, fri.getId());
				ps.setLong(index++, fri.getHeaderID());
				ps.setString(index++, fri.getPaymentType());
				ps.setBigDecimal(index++, fri.getPaymentAmount());
				ps.setString(index++, fri.getRemarks());
				ps.setLong(index++, fri.getPartnerBankId());
				ps.setString(index++, fri.getIssuingBank());
				ps.setString(index++, fri.getFavourName());
				ps.setString(index++, fri.getFavourNumber());
				ps.setString(index++, fri.getPayableLoc());
				ps.setString(index++, fri.getPrintingLoc());
				ps.setDate(index++, JdbcUtil.getDate(fri.getValueDate()));
				ps.setDate(index++, JdbcUtil.getDate(fri.getPostDate()));
				ps.setLong(index++, fri.getBankBranchId());
				ps.setString(index++, fri.getAcctHolderName());
				ps.setString(index++, fri.getAccountNo());
				ps.setString(index++, fri.getPhoneCountryCode());
				ps.setString(index++, fri.getPhoneNumber());
				ps.setDate(index++, JdbcUtil.getDate(fri.getClearingDate()));
				ps.setString(index++, fri.getStatus());
				ps.setBoolean(index++, fri.getActive());
				ps.setString(index++, fri.getPaymentCCy());
				ps.setString(index++, fri.getLei());
				ps.setInt(index++, fri.getVersion());
				ps.setLong(index++, fri.getCreatedBy());
				ps.setTimestamp(index++, fri.getCreatedOn());
				ps.setObject(index++, fri.getApprovedBy());
				ps.setTimestamp(index++, fri.getApprovedOn());
				ps.setLong(index++, fri.getLastMntBy());
				ps.setTimestamp(index++, fri.getLastMntOn());
				ps.setString(index++, fri.getRecordStatus());
				ps.setString(index++, fri.getRoleCode());
				ps.setString(index++, fri.getNextRoleCode());
				ps.setString(index++, fri.getTaskId());
				ps.setString(index++, fri.getNextTaskId());
				ps.setString(index++, fri.getRecordType());
				ps.setLong(index++, fri.getWorkflowId());
			});
		} catch (DuplicateKeyException e) {
			throw new ConcurrencyException(e);
		}

		return fri.getId();
	}

	@Override
	public void update(FeeRefundInstruction fri, TableType tableType) {
		StringBuilder sql = new StringBuilder("update Fee_Refund_Instructions");
		sql.append(tableType.getSuffix());
		sql.append(" Set HeaderID = ?, PaymentType = ?, PaymentAmount = ?, IssuingBank = ?");
		sql.append(", Remarks = ?, PartnerBankId = ?, FavourName = ?, FavourNumber = ?");
		sql.append(", PayableLoc = ?, PrintingLoc = ?, ValueDate = ?, PostDate = ?, BankBranchId = ?");
		sql.append(", AcctHolderName = ?, AccountNo = ?, PhoneCountryCode = ?, PhoneNumber = ?, ClearingDate = ?");
		sql.append(", Active = ?, PaymentCCy = ?, Lei = ?, Status = ?");
		sql.append(", Version = ?, ApprovedBy = ?, ApprovedOn = ?, LastMntBy = ?, LastMntOn = ?");
		sql.append(", RecordStatus = ?, RoleCode = ?, NextRoleCode = ?, TaskId = ?");
		sql.append(", NextTaskId = ?, RecordType = ?, WorkflowId = ?");
		sql.append(" Where ID = ?");

		logger.debug(Literal.SQL.concat(sql.toString()));

		int recordCount = jdbcOperations.update(sql.toString(), ps -> {
			int index = 0;

			ps.setLong(++index, fri.getHeaderID());
			ps.setString(++index, fri.getPaymentType());
			ps.setBigDecimal(++index, fri.getPaymentAmount());
			ps.setString(++index, fri.getIssuingBank());
			ps.setString(++index, fri.getRemarks());
			ps.setLong(++index, fri.getPartnerBankId());
			ps.setString(++index, fri.getFavourName());
			ps.setString(++index, fri.getFavourNumber());
			ps.setString(++index, fri.getPayableLoc());
			ps.setString(++index, fri.getPrintingLoc());
			ps.setDate(++index, JdbcUtil.getDate(fri.getValueDate()));
			ps.setDate(++index, JdbcUtil.getDate(fri.getPostDate()));
			ps.setLong(++index, fri.getBankBranchId());
			ps.setString(++index, fri.getAcctHolderName());
			ps.setString(++index, fri.getAccountNo());
			ps.setString(++index, fri.getPhoneCountryCode());
			ps.setString(++index, fri.getPhoneNumber());
			ps.setDate(++index, JdbcUtil.getDate(fri.getClearingDate()));
			ps.setBoolean(++index, fri.getActive());
			ps.setString(++index, fri.getPaymentCCy());
			ps.setString(++index, fri.getLei());
			ps.setString(++index, fri.getStatus());
			ps.setInt(++index, fri.getVersion());
			ps.setObject(++index, fri.getApprovedBy());
			ps.setTimestamp(++index, fri.getApprovedOn());
			ps.setObject(++index, fri.getLastMntBy());
			ps.setTimestamp(++index, fri.getLastMntOn());
			ps.setString(++index, fri.getRecordStatus());
			ps.setString(++index, fri.getRoleCode());
			ps.setString(++index, fri.getNextRoleCode());
			ps.setString(++index, fri.getTaskId());
			ps.setString(++index, fri.getNextTaskId());
			ps.setString(++index, fri.getRecordType());
			ps.setLong(++index, fri.getWorkflowId());

			ps.setLong(++index, fri.getId());
		});

		if (recordCount == 0) {
			throw new ConcurrencyException();
		}
	}

	@Override
	public void delete(FeeRefundInstruction fri, TableType tableType) {
		StringBuilder sql = new StringBuilder("Delete from Fee_Refund_Instructions");
		sql.append(tableType.getSuffix());
		sql.append(" Where HeaderID = ?");

		logger.debug(Literal.SQL.concat(sql.toString()));

		try {
			int recordCount = jdbcOperations.update(sql.toString(), ps -> ps.setLong(1, fri.getHeaderID()));

			if (recordCount == 0) {
				throw new ConcurrencyException();
			}
		} catch (DataAccessException e) {
			throw new DependencyFoundException(e);
		}
	}

	private StringBuilder getSqlQuery(String type) {
		StringBuilder sql = new StringBuilder("Select");
		sql.append(" ID, HeaderID, PaymentType, PaymentAmount, Remarks, PartnerBankId");
		sql.append(", IssuingBank, FavourName, FavourNumber, PayableLoc, PrintingLoc, ValueDate, PostDate");
		sql.append(", Status, BankBranchId, AcctHolderName, AccountNo, PhoneCountryCode");
		sql.append(", PhoneNumber, ClearingDate, Active, PaymentCCy, Lei");
		sql.append(", Version, CreatedBy, CreatedOn, ApprovedBy, ApprovedOn, LastMntBy, LastMntOn");
		sql.append(", RecordStatus, RoleCode, NextRoleCode, TaskId, NextTaskId, RecordType, WorkflowId");

		if (StringUtils.trimToEmpty(type).contains("View")) {
			sql.append(", PartnerBankCode, PartnerBankName, BankBranchIFSC, BankBranchCode");
			sql.append(", IssuingBankName, PCCityName, BranchDesc, BankName, BranchBankCode");
			sql.append(", PartnerBankAc, PartnerBankAcType");
		}

		sql.append(" From Fee_Refund_Instructions");
		sql.append(StringUtils.trimToEmpty(type));

		return sql;
	}

	private class FeeRefundInstructionRM implements RowMapper<FeeRefundInstruction> {
		private String type;

		FeeRefundInstructionRM(String type) {
			this.type = type;
		}

		@Override
		public FeeRefundInstruction mapRow(ResultSet rs, int rowNum) throws SQLException {
			FeeRefundInstruction fpd = new FeeRefundInstruction();

			fpd.setId(rs.getLong("ID"));
			fpd.setHeaderID(rs.getLong("HeaderID"));
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
			fpd.setBankBranchId(rs.getLong("BankBranchId"));
			fpd.setAcctHolderName(rs.getString("AcctHolderName"));
			fpd.setAccountNo(rs.getString("AccountNo"));
			fpd.setPhoneCountryCode(rs.getString("PhoneCountryCode"));
			fpd.setPhoneNumber(rs.getString("PhoneNumber"));
			fpd.setClearingDate(rs.getTimestamp("ClearingDate"));
			fpd.setActive(rs.getBoolean("Active"));
			fpd.setPaymentCCy(rs.getString("PaymentCCy"));
			fpd.setLei(rs.getString("Lei"));
			fpd.setVersion(rs.getInt("Version"));
			fpd.setCreatedBy(rs.getLong("CreatedBy"));
			fpd.setCreatedOn(rs.getTimestamp("CreatedOn"));
			fpd.setApprovedBy(rs.getLong("ApprovedBy"));
			fpd.setApprovedOn(rs.getTimestamp("ApprovedOn"));
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
	public boolean isInstructionInProgress(long finID) {
		StringBuilder sql = new StringBuilder("select count(fri.ID)");
		sql.append(" From Fee_Refund_Instructions_Temp fri");
		sql.append(" Left Join Fee_Refund_Header_Temp frh on frh.ID = fri.HeaderID");
		sql.append(" Left Join Fee_Refund_Details_Temp frd on frd.HeaderID = frh.ID");
		sql.append(" Where frh.FinID = ?");

		logger.debug(Literal.SQL.concat(sql.toString()));

		return jdbcOperations.queryForObject(sql.toString(), Integer.class, finID) > 0;
	}
}
