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
 * * FileName : PaymentDetailDAOImpl.java * * Author : PENNANT TECHONOLOGIES * * Creation Date : 27-05-2017 * * Modified
 * Date : 27-05-2017 * * Description : * *
 ********************************************************************************************
 * Date Author Version Comments *
 ********************************************************************************************
 * 27-05-2017 PENNANT 0.1 * * * * * * * * *
 ********************************************************************************************
 */
package com.pennant.backend.dao.feerefund.impl;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.dao.DataAccessException;
import org.springframework.dao.EmptyResultDataAccessException;

import com.pennant.backend.dao.feerefund.FeeRefundDetailDAO;
import com.pennant.backend.model.feerefund.FeeRefundDetail;
import com.pennanttech.pennapps.core.ConcurrencyException;
import com.pennanttech.pennapps.core.DependencyFoundException;
import com.pennanttech.pennapps.core.jdbc.SequenceDao;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pennapps.core.resource.Message;
import com.pennanttech.pff.core.TableType;

public class FeeRefundDetailDAOImpl extends SequenceDao<FeeRefundDetail> implements FeeRefundDetailDAO {

	public FeeRefundDetailDAOImpl() {
		super();
	}

	@Override
	public long save(FeeRefundDetail frd, TableType tableType) {
		StringBuilder sql = new StringBuilder();
		sql.append("Insert Into FEE_REFUND_DETAILS");
		sql.append(tableType.getSuffix());
		sql.append(" (ID, HeaderID, ReceivableType, TotalAmount, PaidAmount, PrevRefundAmount");
		sql.append(", CurrRefundAmount, PayableFeeTypeCode, AvailableAmount, ReceivableRefId");
		sql.append(", FeeTypeCode, FeeTypeDesc, PayableFeeTypeDesc");
		sql.append(", Version, CreatedBy, CreatedOn, ApprovedBy, ApprovedOn, LastMntBy, LastMntOn");
		sql.append(", RecordStatus, RoleCode, NextRoleCode, TaskId, NextTaskId, RecordType, WorkflowId)");
		sql.append(" Values (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)");

		logger.debug(Literal.SQL.concat(sql.toString()));

		if (frd.getId() <= 0) {
			frd.setId(getNextValue("SeqFee_Refund_Detail"));
		}

		this.jdbcOperations.update(sql.toString(), ps -> {
			int index = 0;

			ps.setLong(++index, frd.getId());
			ps.setLong(++index, frd.getHeaderID());
			ps.setString(++index, frd.getReceivableType());
			ps.setBigDecimal(++index, frd.getTotalAmount());
			ps.setBigDecimal(++index, frd.getPaidAmount());
			ps.setBigDecimal(++index, frd.getPrevRefundAmount());
			ps.setBigDecimal(++index, frd.getCurrRefundAmount());
			ps.setBigDecimal(++index, frd.getAvailableAmount());
			ps.setLong(++index, frd.getReceivableRefId());
			ps.setString(++index, frd.getFeeTypeCode());
			ps.setString(++index, frd.getFeeTypeDesc());
			ps.setString(++index, frd.getPayableFeeTypeCode());
			ps.setString(++index, frd.getPayableFeeTypeDesc());
			ps.setInt(++index, frd.getVersion());
			ps.setLong(++index, frd.getCreatedBy());
			ps.setTimestamp(++index, frd.getCreatedOn());
			ps.setObject(++index, frd.getApprovedBy());
			ps.setTimestamp(++index, frd.getApprovedOn());
			ps.setLong(++index, frd.getLastMntBy());
			ps.setTimestamp(++index, frd.getLastMntOn());
			ps.setString(++index, frd.getRecordStatus());
			ps.setString(++index, frd.getRoleCode());
			ps.setString(++index, frd.getNextRoleCode());
			ps.setString(++index, frd.getTaskId());
			ps.setString(++index, frd.getNextTaskId());
			ps.setString(++index, frd.getRecordType());
			ps.setLong(++index, frd.getWorkflowId());
		});

		return frd.getId();
	}

	@Override
	public int update(FeeRefundDetail frd, TableType tableType) {
		StringBuilder sql = new StringBuilder("Update");
		sql.append(" FEE_REFUND_DETAILS");
		sql.append(tableType.getSuffix());
		sql.append(" Set ID = ?, HeaderID = ?, ReceivableType = ?, TotalAmount = ?");
		sql.append(", PaidAmount = ?, PrevRefundAmount = ?, currRefundAmount = ?, AvailableAmount = ?");
		sql.append(", ReceivableRefId = ?, FeeTypeCode = ?, FeeTypeDesc = ?, PayableFeeTypeCode = ?");
		sql.append(", PayableFeeTypeDesc = ?");
		sql.append(", Version = ?, ApprovedBy = ?, ApprovedOn = ?, LastMntBy = ?, LastMntOn = ?");
		sql.append(", RecordStatus = ?, RoleCode = ?, NextRoleCode = ?, TaskId = ?");
		sql.append(", NextTaskId = ?, RecordType = ?, WorkflowId = ?");
		sql.append(" Where ID = ?");

		logger.debug(Literal.SQL.concat(sql.toString()));

		int recordCount = jdbcOperations.update(sql.toString(), ps -> {
			int index = 0;

			ps.setLong(++index, frd.getId());
			ps.setLong(++index, frd.getHeaderID());
			ps.setString(++index, frd.getReceivableType());
			ps.setBigDecimal(++index, frd.getTotalAmount());
			ps.setBigDecimal(++index, frd.getPaidAmount());
			ps.setBigDecimal(++index, frd.getPrevRefundAmount());
			ps.setBigDecimal(++index, frd.getCurrRefundAmount());
			ps.setBigDecimal(++index, frd.getAvailableAmount());
			ps.setLong(++index, frd.getReceivableRefId());
			ps.setString(++index, frd.getFeeTypeCode());
			ps.setString(++index, frd.getFeeTypeDesc());
			ps.setString(++index, frd.getPayableFeeTypeCode());
			ps.setString(++index, frd.getPayableFeeTypeDesc());
			ps.setString(++index, frd.getFeeTypeCode());
			ps.setInt(++index, frd.getVersion());
			ps.setObject(++index, frd.getApprovedBy());
			ps.setTimestamp(++index, frd.getApprovedOn());
			ps.setLong(++index, frd.getLastMntBy());
			ps.setTimestamp(++index, frd.getLastMntOn());
			ps.setString(++index, frd.getRecordStatus());
			ps.setString(++index, frd.getRoleCode());
			ps.setString(++index, frd.getNextRoleCode());
			ps.setString(++index, frd.getTaskId());
			ps.setString(++index, frd.getNextTaskId());
			ps.setString(++index, frd.getRecordType());
			ps.setLong(++index, frd.getWorkflowId());

			ps.setLong(++index, frd.getId());
		});

		if (recordCount == 0) {
			throw new ConcurrencyException();
		}

		return recordCount;
	}

	@Override
	public void delete(FeeRefundDetail header, TableType tableType) {
		String sql = "Delete From FEE_REFUND_DETAILS".concat(tableType.getSuffix()).concat(" Where HeaderID = ?");

		logger.debug(Literal.SQL.concat(sql));

		try {
			this.jdbcOperations.update(sql, header.getHeaderID());
		} catch (DataAccessException e) {
			throw new DependencyFoundException(e);
		}
	}

	@Override
	public List<FeeRefundDetail> getFeeRefundDetailList(long headerID, String type) {
		StringBuilder sql = new StringBuilder();
		sql.append("Select ID, HeaderID");
		sql.append(", ReceivableType, TotalAmount, PaidAmount, PrevRefundAmount, currRefundAmount");
		sql.append(", AvailableAmount, ReceivableRefId, FeeTypeCode, FeeTypeDesc, PayableFeeTypeCode");
		sql.append(", PayableFeeTypeDesc, Version");
		sql.append(", CreatedBy, CreatedOn, ApprovedBy, ApprovedOn, LastMntBy, LastMntOn");
		sql.append(", RecordStatus, RoleCode, NextRoleCode, TaskId, NextTaskId, RecordType, WorkflowId");
		sql.append(" From FEE_REFUND_DETAILS");
		sql.append(type);
		sql.append(" Where HeaderID = ?");

		logger.debug(Literal.SQL.concat(sql.toString()));

		return this.jdbcOperations.query(sql.toString(), ps -> ps.setLong(1, headerID), (rs, rowNum) -> {
			FeeRefundDetail frd = new FeeRefundDetail();

			frd.setId(rs.getLong("ID"));
			frd.setHeaderID(rs.getLong("HeaderID"));
			frd.setReceivableType(rs.getString("ReceivableType"));
			frd.setTotalAmount(rs.getBigDecimal("TotalAmount"));
			frd.setPaidAmount(rs.getBigDecimal("PaidAmount"));
			frd.setPrevRefundAmount(rs.getBigDecimal("PrevRefundAmount"));
			frd.setCurrRefundAmount(rs.getBigDecimal("currRefundAmount"));
			frd.setAvailableAmount(rs.getBigDecimal("AvailableAmount"));
			frd.setReceivableRefId(rs.getLong("ReceivableRefId"));
			frd.setFeeTypeCode(rs.getString("FeeTypeCode"));
			frd.setFeeTypeDesc(rs.getString("FeeTypeDesc"));
			frd.setPayableFeeTypeCode(rs.getString("PayableFeeTypeCode"));
			frd.setPayableFeeTypeDesc(rs.getString("PayableFeeTypeDesc"));
			frd.setVersion(rs.getInt("Version"));
			frd.setCreatedBy(rs.getLong("CreatedBy"));
			frd.setCreatedOn(rs.getTimestamp("CreatedOn"));
			frd.setApprovedBy(rs.getLong("ApprovedBy"));
			frd.setApprovedOn(rs.getTimestamp("ApprovedOn"));
			frd.setLastMntBy(rs.getLong("LastMntBy"));
			frd.setLastMntOn(rs.getTimestamp("LastMntOn"));
			frd.setRecordStatus(rs.getString("RecordStatus"));
			frd.setRoleCode(rs.getString("RoleCode"));
			frd.setNextRoleCode(rs.getString("NextRoleCode"));
			frd.setTaskId(rs.getString("TaskId"));
			frd.setNextTaskId(rs.getString("NextTaskId"));
			frd.setRecordType(rs.getString("RecordType"));
			frd.setWorkflowId(rs.getLong("WorkflowId"));

			return frd;
		});
	}

	@Override
	public void updatePayableRef(long adviseId, long id) {
		String sql = "Update FEE_REFUND_DETAILS Set PayableRefId = ? Where ID = ?";

		logger.debug(Literal.SQL.concat(sql));

		this.jdbcOperations.update(sql, ps -> {
			ps.setLong(1, adviseId);
			ps.setLong(2, id);
		});
	}

	@Override
	public FeeRefundDetail getFeeRefundDetail(long id, String type) {
		StringBuilder sql = new StringBuilder("Select");
		sql.append(" ID, HeaderID");
		sql.append(", ReceivableType, TotalAmount, PaidAmount, PrevRefundAmount, currRefundAmount");
		sql.append(", AvailableAmount, ReceivableRefId, FeeTypeCode, FeeTypeDesc, PayableFeeTypeCode");
		sql.append(", PayableFeeTypeDesc, Version");
		sql.append(", CreatedBy, CreatedOn, ApprovedBy, ApprovedOn, LastMntBy, LastMntOn");
		sql.append(", RecordStatus, RoleCode, NextRoleCode, TaskId, NextTaskId, RecordType, WorkflowId");
		sql.append(" From FEE_REFUND_DETAILS");
		sql.append(type);
		sql.append(" Where ID = ?");

		logger.debug(Literal.SQL.concat(sql.toString()));

		try {
			return this.jdbcOperations.queryForObject(sql.toString(), (rs, rowNum) -> {
				FeeRefundDetail frd = new FeeRefundDetail();

				frd.setId(rs.getLong("ID"));
				frd.setHeaderID(rs.getLong("HeaderID"));
				frd.setReceivableType(rs.getString("ReceivableType"));
				frd.setTotalAmount(rs.getBigDecimal("TotalAmount"));
				frd.setPaidAmount(rs.getBigDecimal("PaidAmount"));
				frd.setPrevRefundAmount(rs.getBigDecimal("PrevRefundAmount"));
				frd.setCurrRefundAmount(rs.getBigDecimal("currRefundAmount"));
				frd.setAvailableAmount(rs.getBigDecimal("AvailableAmount"));
				frd.setReceivableRefId(rs.getLong("ReceivableRefId"));
				frd.setFeeTypeCode(rs.getString("FeeTypeCode"));
				frd.setFeeTypeDesc(rs.getString("FeeTypeDesc"));
				frd.setPayableFeeTypeCode(rs.getString("PayableFeeTypeCode"));
				frd.setPayableFeeTypeDesc(rs.getString("PayableFeeTypeDesc"));
				frd.setVersion(rs.getInt("Version"));
				frd.setCreatedBy(rs.getLong("CreatedBy"));
				frd.setCreatedOn(rs.getTimestamp("CreatedOn"));
				frd.setApprovedBy(rs.getLong("ApprovedBy"));
				frd.setApprovedOn(rs.getTimestamp("ApprovedOn"));
				frd.setLastMntBy(rs.getLong("LastMntBy"));
				frd.setLastMntOn(rs.getTimestamp("LastMntOn"));
				frd.setRecordStatus(rs.getString("RecordStatus"));
				frd.setRoleCode(rs.getString("RoleCode"));
				frd.setNextRoleCode(rs.getString("NextRoleCode"));
				frd.setTaskId(rs.getString("TaskId"));
				frd.setNextTaskId(rs.getString("NextTaskId"));
				frd.setRecordType(rs.getString("RecordType"));
				frd.setWorkflowId(rs.getLong("WorkflowId"));

				return frd;
			}, id);
		} catch (EmptyResultDataAccessException e) {
			logger.warn(Message.NO_RECORD_FOUND);
			return null;
		}
	}

	@Override
	public BigDecimal getPrvRefundAmt(long adviseID, long finID) {
		StringBuilder sql = new StringBuilder("Select");
		sql.append(" sum(CurrRefundAmount) From FEE_REFUND_DETAILS frd");
		sql.append(" Inner join FEE_REFUND_HEADER frh on frh.ID = frd.HeaderID");
		sql.append(" Where frh.FinID = ? and ReceivableRefId = ? and frh.approvalStatus = '1' group by frh.FinID");

		logger.debug(Literal.SQL.concat(sql.toString()));

		try {
			return this.jdbcOperations.queryForObject(sql.toString(), BigDecimal.class, finID, adviseID);
		} catch (Exception e) {
			logger.warn(Message.NO_RECORD_FOUND);
			return BigDecimal.ZERO;
		}
	}

	@Override
	public void deleteList(FeeRefundDetail frd, TableType tableType) {
		StringBuilder sql = new StringBuilder("Delete from FEE_REFUND_DETAILS");
		sql.append(tableType.getSuffix());
		sql.append(" where HeaderID = ?");

		logger.debug(Literal.SQL.concat(sql.toString()));

		try {
			int recordCount = jdbcOperations.update(sql.toString(), frd.getHeaderID());

			if (recordCount == 0) {
				throw new ConcurrencyException();
			}
		} catch (DataAccessException e) {
			throw new DependencyFoundException(e);
		}
	}
}