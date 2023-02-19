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
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import org.springframework.dao.DataAccessException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.RowMapper;

import com.pennant.backend.dao.feerefund.FeeRefundDetailDAO;
import com.pennant.backend.model.feerefund.FeeRefundDetail;
import com.pennanttech.pennapps.core.ConcurrencyException;
import com.pennanttech.pennapps.core.DependencyFoundException;
import com.pennanttech.pennapps.core.jdbc.JdbcUtil;
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
		sql.append("Insert Into Fee_Refund_Details");
		sql.append(tableType.getSuffix());
		sql.append(" (ID, HeaderID, ReceivableType, ReceivableFeeTypeID, PayableFeeTypeID");
		sql.append(", ReceivableID, PayableID, RefundAmount, TaxHeaderID");
		sql.append(", Version, CreatedBy, CreatedOn, ApprovedBy, ApprovedOn, LastMntBy, LastMntOn");
		sql.append(", RecordStatus, RoleCode, NextRoleCode, TaskId, NextTaskId, RecordType, WorkflowId)");
		sql.append(" Values (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)");

		logger.debug(Literal.SQL.concat(sql.toString()));

		if (frd.getId() <= 0) {
			frd.setId(getNextValue("SeqFee_Refund_Details"));
		}

		this.jdbcOperations.update(sql.toString(), ps -> {
			int index = 0;

			ps.setLong(++index, frd.getId());
			ps.setLong(++index, frd.getHeaderID());
			ps.setString(++index, frd.getReceivableType());
			ps.setObject(++index, frd.getReceivableFeeTypeID());
			ps.setObject(++index, frd.getPayableFeeTypeID());
			ps.setObject(++index, frd.getReceivableID());
			ps.setObject(++index, frd.getPayableID());
			ps.setBigDecimal(++index, frd.getRefundAmount());
			ps.setObject(++index, frd.getTaxHeaderID());
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
		sql.append(" Fee_Refund_Details");
		sql.append(tableType.getSuffix());
		sql.append(" Set ReceivableFeeTypeID = ?, PayableFeeTypeID = ?");
		sql.append(", ReceivableID = ?, PayableID = ?, RefundAmount = ?, TaxHeaderID = ?");
		sql.append(", Version = ?, ApprovedBy = ?, ApprovedOn = ?, LastMntBy = ?, LastMntOn = ?");
		sql.append(", RecordStatus = ?, RoleCode = ?, NextRoleCode = ?, TaskId = ?");
		sql.append(", NextTaskId = ?, RecordType = ?, WorkflowId = ?");
		sql.append(" Where ID = ?");

		logger.debug(Literal.SQL.concat(sql.toString()));

		int recordCount = jdbcOperations.update(sql.toString(), ps -> {
			int index = 0;

			ps.setObject(++index, frd.getReceivableFeeTypeID());
			ps.setObject(++index, frd.getPayableFeeTypeID());
			ps.setObject(++index, frd.getReceivableID());
			ps.setObject(++index, frd.getPayableID());
			ps.setBigDecimal(++index, frd.getRefundAmount());
			ps.setObject(++index, frd.getTaxHeaderID());
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
		String sql = "Delete From Fee_Refund_Details".concat(tableType.getSuffix()).concat(" Where HeaderID = ?");

		logger.debug(Literal.SQL.concat(sql));

		try {
			this.jdbcOperations.update(sql, header.getHeaderID());
		} catch (DataAccessException e) {
			throw new DependencyFoundException(e);
		}
	}

	@Override
	public List<FeeRefundDetail> getFeeRefundDetailList(long headerID, TableType tableType) {
		StringBuilder sql = getQuery(tableType);
		sql.append(" Where HeaderID = ?");

		logger.debug(Literal.SQL.concat(sql.toString()));

		return this.jdbcOperations.query(sql.toString(), new FeeRefundRM(), headerID);
	}

	@Override
	public void updatePayableId(long id, long adviseId) {
		String sql = "Update Fee_Refund_Details Set PayableID = ? Where ID = ?";

		logger.debug(Literal.SQL.concat(sql));

		this.jdbcOperations.update(sql, ps -> {
			ps.setLong(1, adviseId);
			ps.setLong(2, id);
		});
	}

	@Override
	public FeeRefundDetail getFeeRefundDetail(long id, TableType tableType) {
		StringBuilder sql = getQuery(tableType);
		sql.append(" Where ID = ?");

		logger.debug(Literal.SQL.concat(sql.toString()));

		try {
			return this.jdbcOperations.queryForObject(sql.toString(), new FeeRefundRM(), id);
		} catch (EmptyResultDataAccessException e) {
			logger.warn(Message.NO_RECORD_FOUND);
			return null;
		}
	}

	@Override
	public BigDecimal getPrvRefundAmt(long finID, long receivableID) {
		StringBuilder sql = new StringBuilder("Select Sum(RefundAmount)");
		sql.append(" From Fee_Refund_Details frd");
		sql.append(" Inner join Fee_Refund_Header frh on frh.ID = frd.HeaderID");
		sql.append(" Where frh.FinID = ? and ReceivableID = ? and frh.ApprovalStatus = ?");

		logger.debug(Literal.SQL.concat(sql.toString()));

		try {
			return this.jdbcOperations.queryForObject(sql.toString(), BigDecimal.class, finID, receivableID, 1);
		} catch (Exception e) {
			logger.warn(Message.NO_RECORD_FOUND);
			return BigDecimal.ZERO;
		}
	}

	@Override
	public void deleteList(FeeRefundDetail frd, TableType tableType) {
		StringBuilder sql = new StringBuilder("Delete from Fee_Refund_Details");
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

	private StringBuilder getQuery(TableType tableType) {
		StringBuilder sql = new StringBuilder();

		switch (tableType) {
		case MAIN_TAB:
		case AVIEW:
			sql.append(getQuery(""));
			break;
		case TEMP_TAB:
		case TVIEW:
			sql.append(getQuery("_Temp"));
			break;
		case BOTH_TAB:
		case VIEW:
			sql.append("Select * From (");
			sql.append(getQuery("_Temp"));
			sql.append(" Union All ");
			sql.append(getQuery(""));
			sql.append(" Where not exists (Select 1 From Fee_Refund_Details_Temp Where ID = frd.ID)");
			sql.append(" ) frd");
			break;
		default:
			sql.append(getQuery(tableType.getSuffix()));
			break;
		}

		return sql;
	}

	private StringBuilder getQuery(String type) {
		StringBuilder sql = new StringBuilder("Select");
		sql.append(" frd.ID, frd.HeaderID, frd.ReceivableType, frd.ReceivableFeeTypeID, frd.PayableFeeTypeID");
		sql.append(", frd.ReceivableID, frd.PayableID, frd.RefundAmount, frd.TaxHeaderID");
		sql.append(", frd.Version, frd.CreatedBy, frd.CreatedOn, frd.ApprovedBy");
		sql.append(", frd.ApprovedOn, frd.LastMntBy, frd.LastMntOn, frd.RecordStatus, frd.RoleCode");
		sql.append(", frd.NextRoleCode, frd.TaskId, frd.NextTaskId, frd.RecordType, frd.WorkflowId");
		sql.append(" From Fee_Refund_Details");
		sql.append(type);
		sql.append(" frd");

		return sql;
	}

	private class FeeRefundRM implements RowMapper<FeeRefundDetail> {

		@Override
		public FeeRefundDetail mapRow(ResultSet rs, int rowNum) throws SQLException {
			FeeRefundDetail frd = new FeeRefundDetail();

			frd.setId(rs.getLong("ID"));
			frd.setHeaderID(rs.getLong("HeaderID"));
			frd.setReceivableType(rs.getString("ReceivableType"));
			frd.setReceivableFeeTypeID(JdbcUtil.getLong(rs.getObject("ReceivableFeeTypeID")));
			frd.setPayableFeeTypeID(JdbcUtil.getLong(rs.getObject("PayableFeeTypeID")));
			frd.setReceivableID(JdbcUtil.getLong(rs.getObject("ReceivableID")));
			frd.setPayableID(JdbcUtil.getLong(rs.getObject("PayableID")));
			frd.setRefundAmount(rs.getBigDecimal("RefundAmount"));
			frd.setTaxHeaderID(JdbcUtil.getLong(rs.getObject("TaxHeaderID")));
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
		}

	}
}