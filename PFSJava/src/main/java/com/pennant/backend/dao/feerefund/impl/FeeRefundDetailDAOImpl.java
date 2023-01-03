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

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;

import com.pennant.backend.dao.feerefund.FeeRefundDetailDAO;
import com.pennant.backend.model.feerefund.FeeRefundDetail;
import com.pennanttech.pennapps.core.ConcurrencyException;
import com.pennanttech.pennapps.core.DependencyFoundException;
import com.pennanttech.pennapps.core.jdbc.SequenceDao;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pennapps.core.resource.Message;
import com.pennanttech.pff.core.TableType;

/**
 * Data access layer implementation for <code>PaymentDetail</code> with set of CRUD operations.
 */
public class FeeRefundDetailDAOImpl extends SequenceDao<FeeRefundDetail> implements FeeRefundDetailDAO {
	private static Logger logger = LogManager.getLogger(FeeRefundDetailDAOImpl.class);

	public FeeRefundDetailDAOImpl() {
		super();
	}

	@Override
	public long save(FeeRefundDetail frd, TableType tableType) {
		StringBuilder sql = new StringBuilder();
		sql.append("Insert Into FEEREFUNDDETAIL");
		sql.append(tableType.getSuffix());
		sql.append(" (FeeRefundDetailId, FeeRefundId");
		sql.append(", ReceivableType, TotalAmount, PaidAmount, PrevRefundAmount, currRefundAmount");
		sql.append(", AvailableAmount, ReceivableRefId, FeeTypeCode, FeeTypeDesc, PayableFeeTypeCode");
		sql.append(", PayableFeeTypeDesc, Version, LastMntBy, LastMntOn, RecordStatus");
		sql.append(", RoleCode, NextRoleCode, TaskId, NextTaskId, RecordType, WorkflowId)");
		sql.append(" Values (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)");

		logger.debug(Literal.SQL.concat(sql.toString()));
		if (frd.getFeeRefundDetailId() <= 0) {
			frd.setFeeRefundDetailId(getNextValue("SeqFeeRefundDetail"));
		}

		this.jdbcOperations.update(sql.toString(), ps -> {
			int index = 0;

			ps.setLong(++index, frd.getFeeRefundDetailId());
			ps.setLong(++index, frd.getFeeRefundId());
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
		return frd.getFeeRefundDetailId();
	}

	@Override
	public int update(FeeRefundDetail frd, TableType tableType) {
		StringBuilder sql = new StringBuilder("Update");
		sql.append(" FEEREFUNDDETAIL");
		sql.append(tableType.getSuffix());
		sql.append(" Set FeeRefundDetailId = ?, FeeRefundId = ?, ReceivableType = ?, TotalAmount = ?");
		sql.append(", PaidAmount = ?, PrevRefundAmount = ?, currRefundAmount = ?, AvailableAmount = ?");
		sql.append(", ReceivableRefId = ?, FeeTypeCode = ?, FeeTypeDesc = ?, PayableFeeTypeCode = ?");
		sql.append(", PayableFeeTypeDesc = ?");
		sql.append(", Version = ?, LastMntBy = ?, LastMntOn = ?, RecordStatus = ?, RoleCode = ?");
		sql.append(", NextRoleCode = ?, TaskId = ?, NextTaskId = ?, RecordType = ?, WorkflowId = ?");
		sql.append(" Where ID = ?");

		logger.debug(Literal.SQL.concat(sql.toString()));

		int recordCount = jdbcOperations.update(sql.toString(), ps -> {
			int index = 0;

			ps.setLong(++index, frd.getFeeRefundDetailId());
			ps.setLong(++index, frd.getFeeRefundId());
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
			ps.setLong(++index, frd.getLastMntBy());
			ps.setTimestamp(++index, frd.getLastMntOn());
			ps.setString(++index, frd.getRecordStatus());
			ps.setString(++index, frd.getRoleCode());
			ps.setString(++index, frd.getNextRoleCode());
			ps.setString(++index, frd.getTaskId());
			ps.setString(++index, frd.getNextTaskId());
			ps.setString(++index, frd.getRecordType());
			ps.setLong(++index, frd.getWorkflowId());

			ps.setLong(++index, frd.getFeeRefundDetailId());
		});

		if (recordCount == 0) {
			throw new ConcurrencyException();
		}

		return recordCount;
	}

	@Override
	public void delete(FeeRefundDetail header, TableType tableType) {
		String sql = "Delete From FEEREFUNDDETAIL".concat(tableType.getSuffix()).concat(" Where FeeRefundId = ?");

		logger.debug(Literal.SQL.concat(sql));

		try {
			this.jdbcOperations.update(sql, header.getFeeRefundId());
		} catch (DataAccessException e) {
			throw new DependencyFoundException(e);
		}
	}

	@Override
	public List<FeeRefundDetail> getFeeRefundDetailList(long feeRefundId, String type) {
		StringBuilder sql = new StringBuilder();
		sql.append("Select FeeRefundDetailId, FeeRefundId");
		sql.append(", ReceivableType, TotalAmount, PaidAmount, PrevRefundAmount, currRefundAmount");
		sql.append(", AvailableAmount, ReceivableRefId, FeeTypeCode, FeeTypeDesc, PayableFeeTypeCode");
		sql.append(", PayableFeeTypeDesc, Version, LastMntBy, LastMntOn, RecordStatus");
		sql.append(", RoleCode, NextRoleCode, TaskId, NextTaskId, RecordType, WorkflowId");
		sql.append(" From FEEREFUNDDETAIL");
		sql.append(type);
		sql.append(" Where FeeRefundId = ?");

		logger.debug(Literal.SQL.concat(sql.toString()));

		return this.jdbcOperations.query(sql.toString(), ps -> ps.setLong(1, feeRefundId), (rs, Num) -> {
			FeeRefundDetail frd = new FeeRefundDetail();

			frd.setFeeRefundDetailId(rs.getLong("FeeRefundDetailId"));
			frd.setFeeRefundId(rs.getLong("FeeRefundId"));
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
	public void updatePayableRef(long adviseId, long feeRefundDetailId) {
		String sql = "Update FEEREFUNDDETAIL Set PayableRefId = ? Where FeeRefundDetailId = ?";

		logger.debug(Literal.SQL.concat(sql));

		this.jdbcOperations.update(sql, ps -> {
			ps.setLong(1, adviseId);
			ps.setLong(2, feeRefundDetailId);
		});
	}

	@Override
	public FeeRefundDetail getFeeRefundDetail(long feeRefundDetailId, String type) {
		StringBuilder sql = new StringBuilder();
		sql.append("Select FeeRefundDetailId, FeeRefundId");
		sql.append(", ReceivableType, TotalAmount, PaidAmount, PrevRefundAmount, currRefundAmount");
		sql.append(", AvailableAmount, ReceivableRefId, FeeTypeCode, FeeTypeDesc, PayableFeeTypeCode");
		sql.append(", PayableFeeTypeDesc, Version, LastMntBy, LastMntOn, RecordStatus");
		sql.append(", RoleCode, NextRoleCode, TaskId, NextTaskId, RecordType, WorkflowId");
		sql.append(" From FEEREFUNDDETAIL");
		sql.append(type);
		sql.append(" Where FeeRefundDetailId = ?");

		logger.debug(Literal.SQL + sql.toString());

		try {
			return this.jdbcOperations.queryForObject(sql.toString(), (rs, rowNum) -> {
				FeeRefundDetail frd = new FeeRefundDetail();

				frd.setFeeRefundDetailId(rs.getLong("FeeRefundDetailId"));
				frd.setFeeRefundId(rs.getLong("FeeRefundId"));
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
			}, feeRefundDetailId);
		} catch (EmptyResultDataAccessException e) {
			logger.warn(Message.NO_RECORD_FOUND);
			return null;
		}
	}

	@Override
	public BigDecimal getPrvRefundAmt(long adviseID, long finID) {
		StringBuilder sql = new StringBuilder();
		sql.append("Select sum(CurrRefundAmount) From FEEREFUNDDETAIL frd");
		sql.append(" Inner join FEEREFUNDHEADER frh on frh.FeeRefundId = frd.FeeRefundId");
		sql.append(" Where frh.FinID = ? and ReceivableRefId = ? and frh.approvalStatus = '1' group by frh.FinID");

		logger.debug(Literal.SQL + sql.toString());

		try {
			return this.jdbcOperations.queryForObject(sql.toString(), BigDecimal.class, finID, adviseID);
		} catch (Exception e) {
			logger.warn(Message.NO_RECORD_FOUND);
			return BigDecimal.ZERO;
		}
	}

	@Override
	public void deleteList(FeeRefundDetail frd, TableType tableType) {
		logger.debug(Literal.ENTERING);

		// Prepare the SQL.
		StringBuilder sql = new StringBuilder("delete from FEEREFUNDDETAIL");
		sql.append(tableType.getSuffix());
		sql.append(" where FeeRefundId = :FeeRefundId");

		// Execute the SQL, binding the arguments.
		logger.trace(Literal.SQL + sql.toString());
		SqlParameterSource paramSource = new BeanPropertySqlParameterSource(frd);
		int recordCount = 0;

		try {
			recordCount = jdbcTemplate.update(sql.toString(), paramSource);
		} catch (DataAccessException e) {
			throw new DependencyFoundException(e);
		}
		// Check for the concurrency failure.
		if (recordCount == 0) {
			throw new ConcurrencyException();
		}

		logger.debug(Literal.LEAVING);
	}

}
