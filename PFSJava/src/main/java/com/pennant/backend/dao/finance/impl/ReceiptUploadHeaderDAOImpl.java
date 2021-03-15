/**
 * Copyright 2011 - Pennant Technologies
 * 
 * This file is part of Pennant Java Application Framework and related Products. 
 * All components/modules/functions/classes/logic in this software, unless 
 * otherwise stated, the property of Pennant Technologies. 
 * 
 * Copyright and other intellectual property laws protect these materials. 
 * Reproduction or retransmission of the materials, in whole or in part, in any manner, 
 * without the prior written consent of the copyright holder, is a violation of 
 * copyright law.
 */

/**
 ********************************************************************************************
 *                                 FILE HEADER                                              *
 ********************************************************************************************
 *																							*
 * FileName    		:  UploadHeaderDAOImpl.java                                             * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  17-12-2017    														*
 *                                                                  						*
 * Modified Date    :  17-12-2017    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 17-12-2017       Pennant	                 0.1                                            * 
 *                                                                                          * 
 *                                                                                          * 
 *                                                                                          * 
 *                                                                                          * 
 *                                                                                          * 
 *                                                                                          * 
 *                                                                                          * 
 *                                                                                          * 
 ********************************************************************************************
*/

package com.pennant.backend.dao.finance.impl;

import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.dao.EmptyResultDataAccessException;

import com.pennant.backend.dao.finance.ReceiptUploadHeaderDAO;
import com.pennant.backend.model.receiptupload.ReceiptUploadHeader;
import com.pennanttech.pennapps.core.ConcurrencyException;
import com.pennanttech.pennapps.core.jdbc.JdbcUtil;
import com.pennanttech.pennapps.core.jdbc.SequenceDao;
import com.pennanttech.pennapps.core.resource.Literal;
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
			return this.jdbcOperations.queryForObject(sql.toString(), new Object[] { fileName }, Long.class) > 0;
		} catch (EmptyResultDataAccessException e) {
			logger.warn("Record does not exist in receiptUploadheader_view with Filename>> {}", fileName);
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

				ps.setLong(index++, JdbcUtil.setLong(ruh.getUploadHeaderId()));
				ps.setString(index++, ruh.getFileName());
				ps.setDate(index++, JdbcUtil.getDate(ruh.getTransactionDate()));
				ps.setInt(index++, ruh.getTotalRecords());
				ps.setInt(index++, ruh.getSuccessCount());
				ps.setInt(index++, ruh.getFailedCount());
				ps.setInt(index++, ruh.getVersion());
				ps.setLong(index++, JdbcUtil.setLong(ruh.getLastMntBy()));
				ps.setTimestamp(index++, ruh.getLastMntOn());
				ps.setString(index++, ruh.getRecordStatus());
				ps.setString(index++, ruh.getRoleCode());
				ps.setString(index++, ruh.getNextRoleCode());
				ps.setString(index++, ruh.getTaskId());
				ps.setString(index++, ruh.getNextTaskId());
				ps.setString(index++, ruh.getRecordType());
				ps.setLong(index++, JdbcUtil.setLong(ruh.getWorkflowId()));
				ps.setString(index++, ruh.getEntityCode());
				ps.setInt(index++, ruh.getUploadProgress());
			});
		} catch (DuplicateKeyException e) {
			throw new ConcurrencyException(e);
		}

		return ruh.getUploadHeaderId();
	}

	@Override
	public void update(ReceiptUploadHeader ruh, TableType tableType) {
		StringBuilder sql = new StringBuilder();
		sql.append("Update ReceiptUploadHeader");
		sql.append(tableType.getSuffix());
		sql.append(" Set FileName = ?, SuccessCount = ?, FailedCount = ?, TotalRecords = ?, Version = ?");
		sql.append(", LastMntBy = ?, LastMntOn = ?, RecordStatus = ?, RoleCode = ?, NextRoleCode = ?");
		sql.append(", TaskId = ?, NextTaskId = ?, RecordType = ?, WorkflowId = ?, entityCode = ?");
		sql.append(", UploadProgress = ?");
		sql.append(" Where UploadHeaderId = ?");
		if (tableType == TableType.TEMP_TAB) {
			sql.append(" And LastMntOn = ?");
		} else {
			sql.append(" And Version = ?");
		}

		logger.trace(Literal.SQL, sql.toString());

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
			ps.setLong(index++, ruh.getUploadHeaderId());

			if (tableType == TableType.TEMP_TAB) {
				ps.setTimestamp(index++, ruh.getPrevMntOn());
			} else {
				ps.setInt(index++, ruh.getVersion() - 1);
			}

		});

		if (recordCount == 0) {
			throw new ConcurrencyException();
		}

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
			logger.error(e);
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

		logger.trace(Literal.SQL, sql.toString());

		try {
			return this.jdbcOperations.queryForObject(sql.toString(), new Object[] { uploadHeaderId }, (rs, rowNum) -> {
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
				ruh.setLastMntBy(JdbcUtil.setLong(rs.getLong("LastMntBy")));
				ruh.setRecordStatus(rs.getString("RecordStatus"));
				ruh.setRoleCode(rs.getString("RoleCode"));
				ruh.setNextRoleCode(rs.getString("NextRoleCode"));
				ruh.setTaskId(rs.getString("TaskId"));
				ruh.setNextTaskId(rs.getString("NextTaskId"));
				ruh.setRecordType(rs.getString("RecordType"));
				ruh.setWorkflowId(JdbcUtil.setLong(rs.getLong("WorkflowId")));

				return ruh;
			});
		} catch (EmptyResultDataAccessException e) {
			logger.info("Record not exists in ReceiptUploadHeader{}for the UploadHeaderId{}", type, uploadHeaderId);
		}
		return null;
	}

	@Override
	public void uploadHeaderStatusCnt(long uploadHeaderId, int sucessCount, int failedCount) {

		StringBuilder sql = new StringBuilder("Update ReceiptUploadHeader");
		sql.append(" Set SuccessCount = ?,  FailedCount = ?, TotalRecords = ?");
		sql.append(" Where UploadHeaderId = ?");

		logger.trace(Literal.SQL, sql.toString());

		this.jdbcOperations.update(sql.toString(), ps -> {
			int index = 1;
			ps.setInt(index++, sucessCount);
			ps.setInt(index++, failedCount);
			ps.setInt(index++, failedCount + sucessCount);
			ps.setLong(index++, uploadHeaderId);
		});

	}

	@Override
	public void updateUploadProgress(long uploadHeaderId, int receiptDownloaded) {

		StringBuilder sql = new StringBuilder("Update RECEIPTUPLOADHEADER_temp");
		sql.append(" Set UploadProgress = ? ");
		sql.append(" Where UploadHeaderId = ?");

		logger.trace(Literal.SQL, sql.toString());

		this.jdbcOperations.update(sql.toString(), ps -> {
			int index = 1;
			ps.setInt(index++, receiptDownloaded);
			ps.setLong(index++, uploadHeaderId);

		});

	}

	@Override
	public boolean isFileDownlaoded(long uploadHeaderId, int receiptDownloaded) {
		StringBuilder sql = new StringBuilder("Select");
		sql.append(" count(*)");
		sql.append(" From ReceiptUploadheader_view");
		sql.append(" Where UploadHeaderId = ? and UploadProgress= ?");

		logger.trace(Literal.SQL, sql);

		try {
			return this.jdbcOperations.queryForObject(sql.toString(),
					new Object[] { uploadHeaderId, receiptDownloaded }, (rs, rowNum) -> rs.getInt(1)) > 0;
		} catch (EmptyResultDataAccessException e) {
			logger.warn("Record is not found in receiptUploadheader_view for UploadHeaderId>>{} and UploadProgress>>{}",
					uploadHeaderId, receiptDownloaded);
		}

		return false;
	}

	@Override
	public List<Long> getHeaderStatusCnt(long uploadHeaderId) {
		String sql = "Select ReceiptId From  ReceiptUploadDetails Where UploadHeaderId = ?";

		logger.trace(Literal.SQL, sql.toString());

		return this.jdbcOperations.query(sql.toString(), ps -> {
			ps.setLong(1, uploadHeaderId);

		}, (rs, rowNum) -> {
			if (rs.getLong("ReceiptId") == 0)
				return null;

			return JdbcUtil.setLong(rs.getLong("ReceiptId"));
		});
	}

	@Override
	public long generateSeqId() {
		return getNextValue("SeqReceiptUploadHeader");
	}

}