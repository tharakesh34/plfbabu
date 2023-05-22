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
 * * FileName : UploadHeaderDAOImpl.java * * Author : PENNANT TECHONOLOGIES * * Creation Date : 17-12-2017 * * Modified
 * Date : 17-12-2017 * * Description : * *
 ********************************************************************************************
 * Date Author Version Comments *
 ********************************************************************************************
 * 17-12-2017 Pennant 0.1 * * * * * * * * *
 ********************************************************************************************
 */

package com.pennant.backend.dao.finance.impl;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;

import com.pennant.backend.dao.finance.ReceiptUploadHeaderDAO;
import com.pennant.backend.model.receiptupload.ReceiptUploadHeader;
import com.pennant.backend.model.receiptupload.ReceiptUploadLog;
import com.pennant.backend.util.ReceiptUploadConstants;
import com.pennanttech.pennapps.core.ConcurrencyException;
import com.pennanttech.pennapps.core.DependencyFoundException;
import com.pennanttech.pennapps.core.jdbc.JdbcUtil;
import com.pennanttech.pennapps.core.jdbc.SequenceDao;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pennapps.core.resource.Message;
import com.pennanttech.pff.core.TableType;

/**
 * DAO methods implementation for the <b>UploadHeader model</b> class.<br>
 * 
 */
public class ReceiptUploadHeaderDAOImpl extends SequenceDao<ReceiptUploadHeader> implements ReceiptUploadHeaderDAO {

	private static Logger logger = LogManager.getLogger(ReceiptUploadHeaderDAOImpl.class);

	public ReceiptUploadHeaderDAOImpl() {
		super();
	}

	@Override
	public boolean isFileNameExist(String fileName) {
		StringBuilder sql = new StringBuilder();
		sql.append("Select UploadHeaderId From ReceiptUploadheader_view");
		sql.append(" Where  FileName = ?");

		logger.trace(Literal.SQL + sql.toString());

		try {
			return this.jdbcOperations.queryForObject(sql.toString(), Long.class, fileName) > 0;
		} catch (EmptyResultDataAccessException e) {
			logger.warn(Message.NO_RECORD_FOUND);
			return false;
		}
	}

	@Override
	public long save(ReceiptUploadHeader ruh, TableType tableType) {

		StringBuilder sql = new StringBuilder("Insert into");
		sql.append(" ReceiptUploadHeader");
		sql.append(tableType.getSuffix());
		sql.append(" (UploadHeaderId, FileName, Transactiondate, TotalRecords, SuccessCount, FailedCount");
		sql.append(", Version, LastMntBy, LastMntOn, RecordStatus, RoleCode, NextRoleCode, TaskId, NextTaskId");
		sql.append(", RecordType, WorkflowId, EntityCode, UploadProgress");
		sql.append(") values(");
		sql.append("?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?");
		sql.append(")");

		logger.trace(Literal.SQL + sql.toString());

		if (ruh.getUploadHeaderId() <= 0) {
			ruh.setUploadHeaderId(getNextValue("SeqReceiptUploadHeader"));
		}
		try {
			jdbcOperations.update(sql.toString(), ps -> {
				int index = 1;

				ps.setLong(index++, ruh.getUploadHeaderId());
				ps.setString(index++, ruh.getFileName());
				ps.setDate(index++, JdbcUtil.getDate(ruh.getTransactionDate()));
				ps.setInt(index++, ruh.getTotalRecords());
				ps.setInt(index++, ruh.getSuccessCount());
				ps.setInt(index++, ruh.getFailedCount());
				ps.setInt(index++, ruh.getVersion());
				ps.setLong(index++, ruh.getLastMntBy());
				ps.setTimestamp(index++, ruh.getLastMntOn());
				ps.setString(index++, ruh.getRecordStatus());
				ps.setString(index++, ruh.getRoleCode());
				ps.setString(index++, ruh.getNextRoleCode());
				ps.setString(index++, ruh.getTaskId());
				ps.setString(index++, ruh.getNextTaskId());
				ps.setString(index++, ruh.getRecordType());
				ps.setLong(index++, ruh.getWorkflowId());
				ps.setString(index++, ruh.getEntityCode());
				ps.setInt(index, ruh.getUploadProgress());
			});
		} catch (DuplicateKeyException e) {
			throw new ConcurrencyException(e);
		}

		return ruh.getUploadHeaderId();
	}

	@Override
	public int update(ReceiptUploadHeader ruh, TableType tableType) {
		StringBuilder sql = new StringBuilder();
		sql.append("Update ReceiptUploadHeader");
		sql.append(tableType.getSuffix());
		sql.append(" Set FileName = ?, SuccessCount = ?, FailedCount = ?, TotalRecords = ?, Version = ?");
		sql.append(", LastMntBy = ?, LastMntOn = ?, RecordStatus = ?, RoleCode = ?, NextRoleCode = ?");
		sql.append(", TaskId = ?, NextTaskId = ?, RecordType = ?, WorkflowId = ?, entityCode = ?");
		sql.append(", UploadProgress = ?");
		sql.append(" Where UploadHeaderId = ?");

		logger.trace(Literal.SQL + sql.toString());

		int recordCount = jdbcTemplate.getJdbcOperations().update(sql.toString(), ps -> {
			int index = 1;

			ps.setString(index++, ruh.getFileName());
			ps.setInt(index++, ruh.getSuccessCount());
			ps.setInt(index++, ruh.getFailedCount());
			ps.setInt(index++, ruh.getTotalRecords());
			ps.setInt(index++, ruh.getVersion());
			ps.setLong(index++, ruh.getLastMntBy());
			ps.setTimestamp(index++, ruh.getLastMntOn());
			ps.setString(index++, ruh.getRecordStatus());
			ps.setString(index++, ruh.getRoleCode());
			ps.setString(index++, ruh.getNextRoleCode());
			ps.setString(index++, ruh.getTaskId());
			ps.setString(index++, ruh.getNextTaskId());
			ps.setString(index++, ruh.getRecordType());
			ps.setLong(index++, ruh.getWorkflowId());
			ps.setString(index++, ruh.getEntityCode());
			ps.setInt(index++, ruh.getUploadProgress());
			ps.setLong(index, ruh.getUploadHeaderId());

		});

		if (recordCount == 0) {
			throw new ConcurrencyException();
		}

		return recordCount;

	}

	@Override
	public void delete(ReceiptUploadHeader receiptUploadHeader, TableType tableType) {
		StringBuilder sql = new StringBuilder("Delete From ReceiptUploadHeader");
		sql.append(tableType.getSuffix());
		sql.append(" Where UploadHeaderId = ?");

		logger.trace(Literal.SQL + sql.toString());

		try {
			this.jdbcOperations.update(sql.toString(), new Object[] { receiptUploadHeader.getUploadHeaderId() });
		} catch (DataAccessException e) {
			throw new DependencyFoundException(e);
		}
	}

	@Override
	public ReceiptUploadHeader getReceiptHeaderById(long uploadHeaderId, String type) {

		StringBuilder sql = new StringBuilder("Select");
		sql.append(" UploadHeaderId, FileName, Transactiondate, TotalRecords, SuccessCount, FailedCount");
		sql.append(", EntityCode, UploadProgress, Version, LastMntOn, LastMntBy, RecordStatus, RoleCode");
		sql.append(", NextRoleCode, TaskId, NextTaskId, RecordType, WorkflowId");
		sql.append(" From ReceiptUploadHeader");
		sql.append(StringUtils.trimToEmpty(type));
		sql.append(" Where UploadHeaderId =?");

		logger.trace(Literal.SQL + sql.toString());

		try {
			return this.jdbcOperations.queryForObject(sql.toString(), (rs, rowNum) -> {
				ReceiptUploadHeader ruh = new ReceiptUploadHeader();

				ruh.setUploadHeaderId(rs.getLong("UploadHeaderId"));
				ruh.setFileName(rs.getString("FileName"));
				ruh.setTransactionDate(JdbcUtil.getDate(rs.getDate("Transactiondate")));
				ruh.setTotalRecords(rs.getInt("TotalRecords"));
				ruh.setSuccessCount(rs.getInt("SuccessCount"));
				ruh.setFailedCount(rs.getInt("FailedCount"));
				ruh.setEntityCode(rs.getString("EntityCode"));
				ruh.setUploadProgress(rs.getInt("UploadProgress"));
				ruh.setVersion(rs.getInt("Version"));
				ruh.setLastMntOn(rs.getTimestamp("LastMntOn"));
				ruh.setLastMntBy(rs.getLong("LastMntBy"));
				ruh.setRecordStatus(rs.getString("RecordStatus"));
				ruh.setRoleCode(rs.getString("RoleCode"));
				ruh.setNextRoleCode(rs.getString("NextRoleCode"));
				ruh.setTaskId(rs.getString("TaskId"));
				ruh.setNextTaskId(rs.getString("NextTaskId"));
				ruh.setRecordType(rs.getString("RecordType"));
				ruh.setWorkflowId(rs.getLong("WorkflowId"));

				return ruh;
			}, uploadHeaderId);
		} catch (EmptyResultDataAccessException e) {
			logger.warn(Message.NO_RECORD_FOUND);
			return null;
		}
	}

	@Override
	public void uploadHeaderStatusCnt(long uploadHeaderId, int sucessCount, int failedCount) {

		StringBuilder sql = new StringBuilder("Update ReceiptUploadHeader");
		sql.append(" Set SuccessCount = ?,  FailedCount = ?, TotalRecords = ?");
		sql.append(" Where UploadHeaderId = ?");

		logger.trace(Literal.SQL + sql.toString());

		this.jdbcOperations.update(sql.toString(), ps -> {
			int index = 1;
			ps.setInt(index++, sucessCount);
			ps.setInt(index++, failedCount);
			ps.setInt(index++, failedCount + sucessCount);
			ps.setLong(index, uploadHeaderId);
		});

	}

	@Override
	public long updateUploadProgress(long uploadHeaderId, int status) {

		StringBuilder sql = new StringBuilder("Update RECEIPTUPLOADHEADER_temp");
		sql.append(" Set UploadProgress = ? ");
		sql.append(" Where UploadHeaderId = ?");

		logger.debug(Literal.SQL + sql.toString());

		return this.jdbcOperations.update(sql.toString(), ps -> {
			int index = 1;
			ps.setInt(index++, status);
			ps.setLong(index, uploadHeaderId);

		});

	}

	@Override
	public boolean isFileDownlaoded(long uploadHeaderId, int receiptDownloaded) {
		StringBuilder sql = new StringBuilder("Select");
		sql.append(" count(*)");
		sql.append(" From ReceiptUploadheader_view");
		sql.append(" Where UploadHeaderId = ? and UploadProgress= ?");

		logger.debug(Literal.SQL + sql.toString());

		try {
			return this.jdbcOperations.queryForObject(sql.toString(), (rs, rowNum) -> rs.getInt(1), uploadHeaderId,
					receiptDownloaded) > 0;
		} catch (EmptyResultDataAccessException e) {
			logger.warn(Message.NO_RECORD_FOUND);
			return false;
		}
	}

	@Override
	public List<Long> getHeaderStatusCnt(long uploadHeaderId) {
		String sql = "Select ReceiptId From  ReceiptUploadDetails Where UploadHeaderId = ?";

		logger.trace(Literal.SQL + sql);

		return this.jdbcOperations.query(sql, ps -> ps.setLong(1, uploadHeaderId), (rs, rowNum) -> {
			if (rs.getLong("ReceiptId") == 0)
				return null;

			return rs.getLong("ReceiptId");
		});
	}

	@Override
	public long generateSeqId() {
		return getNextValue("SeqReceiptUploadHeader");
	}

	@Override
	public Map<Long, ReceiptUploadLog> createAttempLog(List<ReceiptUploadHeader> uploadHeaderList) {
		Map<Long, ReceiptUploadLog> map = new HashMap<Long, ReceiptUploadLog>();
		StringBuilder sql = new StringBuilder();
		sql.append("Insert into  RECEIPT_UPLOAD_LOG");
		sql.append("(Id, HeaderId, AttemptNo, AttemptStatus)");
		sql.append(" Values( ?, ?, ?, ?)");

		jdbcOperations.batchUpdate(sql.toString(), new BatchPreparedStatementSetter() {
			@Override
			public void setValues(PreparedStatement ps, int i) throws SQLException {
				ReceiptUploadHeader header = uploadHeaderList.get(i);
				long Id = header.getId();
				ReceiptUploadLog ua = new ReceiptUploadLog(Id);
				ua.setTotalcount(header.getSuccessCount());
				ua.setId(getNextValue("SeqReceipt_Upload_Log"));
				ua.setAttemptStatus(ReceiptUploadConstants.ATTEMPSTATUS_INPROCESS);
				ua.setAttemptNo(header.getAttemptNo());

				int index = 1;
				ps.setLong(index++, ua.getId());
				ps.setLong(index++, Id);
				ps.setLong(index++, header.getAttemptNo());
				ps.setInt(index, ReceiptUploadConstants.ATTEMPSTATUS_INPROCESS);
				map.put(Id, ua);
			}

			@Override
			public int getBatchSize() {
				return uploadHeaderList.size();
			}
		});

		return map;
	}

	@Override
	public void updateAttemptLog(ReceiptUploadLog apprLog) {
		StringBuilder sql = new StringBuilder();
		sql.append("Update RECEIPT_UPLOAD_LOG ");
		sql.append("Set HeaderId = ?, SuccessCount = ?, FailedCount = ?, ProcessedRecords = ?, AttemptStatus=? ");
		sql.append("Where Id = ?");
		jdbcOperations.update(sql.toString(), ps -> {
			int index = 1;
			ps.setLong(index++, apprLog.getHeaderId());
			ps.setInt(index++, apprLog.getSuccessRecords().get());
			ps.setInt(index++, apprLog.getFailRecords().get());
			ps.setInt(index++, apprLog.getProcessedRecords().get());
			ps.setInt(index++, ReceiptUploadConstants.ATTEMPSTATUS_DONE);
			ps.setLong(index, apprLog.getId());
		});
	}

	@Override
	public long setHeaderAttempNo(long id) {
		String sql = "Select count(HeaderId) From RECEIPT_UPLOAD_LOG Where HeaderId = ?";

		logger.debug(Literal.SQL + sql);

		return jdbcOperations.queryForObject(sql, (rs, rowNum) -> rs.getLong(1), id);
	}
}