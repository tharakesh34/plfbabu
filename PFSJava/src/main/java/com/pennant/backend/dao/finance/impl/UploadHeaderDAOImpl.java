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

import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.EmptyResultDataAccessException;

import com.pennant.backend.dao.finance.UploadHeaderDAO;
import com.pennant.backend.model.WorkFlowDetails;
import com.pennant.backend.model.expenses.UploadHeader;
import com.pennant.backend.model.receiptupload.UploadReceipt;
import com.pennant.backend.util.WorkFlowUtil;
import com.pennanttech.pennapps.core.ConcurrencyException;
import com.pennanttech.pennapps.core.DependencyFoundException;
import com.pennanttech.pennapps.core.jdbc.JdbcUtil;
import com.pennanttech.pennapps.core.jdbc.SequenceDao;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pennapps.core.resource.Message;
import com.pennanttech.pff.core.TableType;
import com.pennanttech.pff.core.util.QueryUtil;

/**
 * DAO methods implementation for the <b>UploadHeader model</b> class.<br>
 * 
 */
public class UploadHeaderDAOImpl extends SequenceDao<UploadHeader> implements UploadHeaderDAO {
	private static Logger logger = LogManager.getLogger(UploadHeaderDAOImpl.class);

	public UploadHeaderDAOImpl() {
		super();
	}

	@Override
	public UploadHeader getUploadHeader(long uploadId) {
		StringBuilder sql = new StringBuilder("Select");
		sql.append(" UploadId, FileLocation, FileName, TransactionDate, TotalRecords, SuccessCount");
		sql.append(", FailedCount, Module, FileDownload, ApprovedDate, MakerId, ApproverId");
		sql.append(", Version, LastMntBy, LastMntOn, RecordStatus, RoleCode");
		sql.append(", NextRoleCode, TaskId, NextTaskId, RecordType, WorkflowId");
		sql.append(" From UploadHeader");
		sql.append(" Where  UploadId = ?");

		logger.debug(Literal.SQL + sql.toString());

		try {
			return this.jdbcOperations.queryForObject(sql.toString(), (rs, rowNum) -> {
				UploadHeader uph = new UploadHeader();

				uph.setUploadId(rs.getLong("UploadId"));
				uph.setFileLocation(rs.getString("FileLocation"));
				uph.setFileName(rs.getString("FileName"));
				uph.setTransactionDate(rs.getDate("TransactionDate"));
				uph.setTotalRecords(rs.getInt("TotalRecords"));
				uph.setSuccessCount(rs.getInt("SuccessCount"));
				uph.setFailedCount(rs.getInt("FailedCount"));
				uph.setModule(rs.getString("Module"));
				uph.setFileDownload(rs.getBoolean("FileDownload"));
				uph.setApprovedDate(rs.getDate("ApprovedDate"));
				uph.setMakerId(JdbcUtil.getLong(rs.getObject("MakerId")));
				uph.setApproverId(JdbcUtil.getLong(rs.getObject("ApproverId")));
				uph.setVersion(rs.getInt("Version"));
				uph.setLastMntBy(rs.getLong("LastMntBy"));
				uph.setLastMntOn(rs.getTimestamp("LastMntOn"));
				uph.setRecordStatus(rs.getString("RecordStatus"));
				uph.setRoleCode(rs.getString("RoleCode"));
				uph.setNextRoleCode(rs.getString("NextRoleCode"));
				uph.setTaskId(rs.getString("TaskId"));
				uph.setNextTaskId(rs.getString("NextTaskId"));
				uph.setRecordType(rs.getString("RecordType"));
				uph.setWorkflowId(rs.getLong("WorkflowId"));

				return uph;
			}, uploadId);
		} catch (EmptyResultDataAccessException e) {
			logger.warn(Message.NO_RECORD_FOUND);
			return null;
		}
	}

	@Override
	public boolean isFileNameExist(String fileName) {
		String sql = "Select UploadId From UploadHeader Where FileName = ?";

		logger.debug(Literal.SQL + sql);

		try {
			return this.jdbcOperations.queryForObject(sql, Long.class, fileName) > 0;
		} catch (EmptyResultDataAccessException e) {
			logger.warn(Message.NO_RECORD_FOUND);
			return false;
		}
	}

	@Override
	public long save(UploadHeader uh) {
		StringBuilder sql = new StringBuilder();
		sql.append("Insert Into UploadHeader");
		sql.append(" (UploadId, FileLocation, FileName, TransactionDate, TotalRecords");
		sql.append(", SuccessCount, FailedCount, Module, FileDownload");
		sql.append(", Version, LastMntBy, LastMntOn, RecordStatus, RoleCode");
		sql.append(", NextRoleCode, TaskId, NextTaskId, RecordType, WorkflowId)");
		sql.append(" Values (?, ?, ?, ?, ?, ?, ?, ?, ?");
		sql.append(", ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)");

		if (uh.getUploadId() == Long.MIN_VALUE) {
			uh.setUploadId(getNextValue("SeqUploadHeader"));
		}

		logger.debug(Literal.SQL + sql.toString());

		this.jdbcOperations.update(sql.toString(), ps -> {
			int index = 1;

			ps.setLong(index++, uh.getUploadId());
			ps.setString(index++, uh.getFileLocation());
			ps.setString(index++, uh.getFileName());
			ps.setDate(index++, JdbcUtil.getDate(uh.getTransactionDate()));
			ps.setInt(index++, uh.getTotalRecords());
			ps.setInt(index++, uh.getSuccessCount());
			ps.setInt(index++, uh.getFailedCount());
			ps.setString(index++, uh.getModule());
			ps.setBoolean(index++, uh.isFileDownload());
			ps.setInt(index++, uh.getVersion());
			ps.setLong(index++, uh.getLastMntBy());
			ps.setTimestamp(index++, uh.getLastMntOn());
			ps.setString(index++, uh.getRecordStatus());
			ps.setString(index++, uh.getRoleCode());
			ps.setString(index++, uh.getNextRoleCode());
			ps.setString(index++, uh.getTaskId());
			ps.setString(index++, uh.getNextTaskId());
			ps.setString(index++, uh.getRecordType());
			ps.setLong(index, uh.getWorkflowId());
		});

		return uh.getUploadId();
	}

	@Override
	public void updateRecordCounts(UploadHeader uh) {
		StringBuilder sql = new StringBuilder("Update UploadHeader Set");
		sql.append(" SuccessCount = (Select Count(UploadId) SuccessCount From UploadFinExpenses");
		sql.append(" Where Status = ? and UploadId = ?)");
		sql.append(", FailedCount = (Select Count(UploadId) FailedCount From UploadFinExpenses");
		sql.append(" Where Status = ? and UploadId = ?)");
		sql.append(", TotalRecords = (Select Count(UploadId) SuccessCount From UploadFinExpenses Where UploadId = ?)");
		sql.append(" Where UploadId = ?");

		logger.debug(Literal.SQL + sql.toString());

		this.jdbcOperations.update(sql.toString(), ps -> {
			int index = 1;

			ps.setString(index++, "SUCCESS");
			ps.setLong(index++, uh.getUploadId());
			ps.setString(index++, "FAILED");
			ps.setLong(index++, uh.getUploadId());
			ps.setLong(index++, uh.getUploadId());
			ps.setLong(index, uh.getUploadId());
		});
	}

	@Override
	public void updateRecord(UploadHeader uh) {
		String sql = "Update UploadHeader Set SuccessCount = ?, FailedCount = ?, TotalRecords = ? Where UploadId = ?";

		logger.debug(Literal.SQL + sql);

		this.jdbcOperations.update(sql, ps -> {
			int index = 1;

			ps.setInt(index++, uh.getSuccessCount());
			ps.setInt(index++, uh.getFailedCount());
			ps.setInt(index++, uh.getTotalRecords());
			ps.setLong(index, uh.getUploadId());
		});
	}

	@Override
	public UploadHeader getUploadHeaderById(long uploadId, String type) {
		StringBuilder sql = new StringBuilder("Select");
		sql.append(" UploadId, FileLocation, FileName, TransactionDate, TotalRecords, SuccessCount");
		sql.append(", FailedCount, Module, EntityCode, FileDownload, ApprovedDate, MakerId, ApproverId");
		sql.append(", Version, LastMntBy, LastMntOn , RecordStatus, RoleCode");
		sql.append(", NextRoleCode, TaskId, NextTaskId, RecordType, WorkflowId");

		if (StringUtils.trimToEmpty(type).contains("View")) {
			sql.append(", UserName");
		}

		sql.append(" From UploadHeader");
		sql.append(type);
		sql.append(" Where UploadId = ?");

		logger.debug(Literal.SQL + sql.toString());

		try {
			return this.jdbcOperations.queryForObject(sql.toString(), (rs, rowNum) -> {
				UploadHeader uph = new UploadHeader();

				uph.setUploadId(rs.getLong("UploadId"));
				uph.setFileLocation(rs.getString("FileLocation"));
				uph.setFileName(rs.getString("FileName"));
				uph.setTransactionDate(rs.getDate("TransactionDate"));
				uph.setTotalRecords(rs.getInt("TotalRecords"));
				uph.setSuccessCount(rs.getInt("SuccessCount"));
				uph.setFailedCount(rs.getInt("FailedCount"));
				uph.setModule(rs.getString("Module"));
				uph.setEntityCode(rs.getString("EntityCode"));
				uph.setFileDownload(rs.getBoolean("FileDownload"));
				uph.setApprovedDate(rs.getDate("ApprovedDate"));
				uph.setMakerId(JdbcUtil.getLong(rs.getObject("MakerId")));
				uph.setApproverId(JdbcUtil.getLong(rs.getObject("ApproverId")));
				uph.setVersion(rs.getInt("Version"));
				uph.setLastMntBy(rs.getLong("LastMntBy"));
				uph.setLastMntOn(rs.getTimestamp("LastMntOn"));
				uph.setRecordStatus(rs.getString("RecordStatus"));
				uph.setRoleCode(rs.getString("RoleCode"));
				uph.setNextRoleCode(rs.getString("NextRoleCode"));
				uph.setTaskId(rs.getString("TaskId"));
				uph.setNextTaskId(rs.getString("NextTaskId"));
				uph.setRecordType(rs.getString("RecordType"));
				uph.setWorkflowId(rs.getLong("WorkflowId"));

				if (StringUtils.trimToEmpty(type).contains("View")) {
					uph.setUserName(rs.getString("UserName"));
				}

				return uph;
			}, uploadId);
		} catch (EmptyResultDataAccessException e) {
			logger.warn(Message.NO_RECORD_FOUND);
			return null;
		}
	}

	@Override
	public void updateFileDownload(long uploadId, boolean fileDownload, String type) {
		StringBuilder sql = new StringBuilder("Update UploadHeader");
		sql.append(StringUtils.trim(type));
		sql.append(" Set FileDownload = ?");
		sql.append(" Where UploadId = ?");

		logger.debug(Literal.SQL + sql.toString());

		try {
			this.jdbcOperations.update(sql.toString(), ps -> {
				int index = 1;

				ps.setBoolean(index++, fileDownload);
				ps.setLong(index, uploadId);
			});
		} catch (Exception e) {
			throw e;
		}
	}

	@Override
	public long save(UploadHeader uh, TableType tableType) {
		StringBuilder sql = new StringBuilder();
		sql.append("Insert Into UploadHeader");
		sql.append(tableType.getSuffix());
		sql.append(" (UploadId, FileLocation, FileName, TransactionDate, TotalRecords, SuccessCount, FailedCount");
		sql.append(", Module, EntityCode, FileDownload, ApprovedDate, MakerId, ApproverId");
		sql.append(", Version, LastMntBy, LastMntOn, RecordStatus, RoleCode, NextRoleCode");
		sql.append(", TaskId, NextTaskId, RecordType, WorkflowId)");
		sql.append(" Values (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?");
		sql.append(", ?, ?, ?, ?, ?, ?, ?, ?)");

		if (uh.getUploadId() == Long.MIN_VALUE) {
			uh.setUploadId(getNextValue("SeqUploadHeader"));
		}

		logger.debug(Literal.SQL + sql.toString());

		this.jdbcOperations.update(sql.toString(), ps -> {
			int index = 1;

			ps.setLong(index++, uh.getUploadId());
			ps.setString(index++, uh.getFileLocation());
			ps.setString(index++, uh.getFileName());
			ps.setDate(index++, JdbcUtil.getDate(uh.getTransactionDate()));
			ps.setInt(index++, uh.getTotalRecords());
			ps.setInt(index++, uh.getSuccessCount());
			ps.setInt(index++, uh.getFailedCount());
			ps.setString(index++, uh.getModule());
			ps.setString(index++, uh.getEntityCode());
			ps.setBoolean(index++, uh.isFileDownload());
			ps.setDate(index++, JdbcUtil.getDate(uh.getApprovedDate()));
			ps.setObject(index++, uh.getMakerId());
			ps.setObject(index++, uh.getApproverId());
			ps.setInt(index++, uh.getVersion());
			ps.setLong(index++, uh.getLastMntBy());
			ps.setTimestamp(index++, uh.getLastMntOn());
			ps.setString(index++, uh.getRecordStatus());
			ps.setString(index++, uh.getRoleCode());
			ps.setString(index++, uh.getNextRoleCode());
			ps.setString(index++, uh.getTaskId());
			ps.setString(index++, uh.getNextTaskId());
			ps.setString(index++, uh.getRecordType());
			ps.setLong(index, uh.getWorkflowId());
		});

		return uh.getUploadId();
	}

	@Override
	public void update(UploadHeader uh, TableType tableType) {
		StringBuilder sql = new StringBuilder("Update UploadHeader");
		sql.append(tableType.getSuffix());
		sql.append(" Set FileLocation = ?, FileName = ?, TransactionDate = ?, TotalRecords = ?");
		sql.append(", SuccessCount = ?, FailedCount = ?, Module = ?, EntityCode = ?, FileDownload = ?");
		sql.append(", ApprovedDate = ?, MakerId = ?, ApproverId = ?");
		sql.append(", Version = ?, LastMntBy = ?, LastMntOn = ?, RecordStatus = ?, RoleCode = ?");
		sql.append(", NextRoleCode = ?, TaskId = ?, NextTaskId = ?, RecordType = ?, WorkflowId = ?");
		sql.append(" Where UploadId = ?");
		sql.append(QueryUtil.getConcurrencyClause(tableType));

		logger.debug(Literal.SQL + sql.toString());

		int recordCount = jdbcOperations.update(sql.toString(), ps -> {
			int index = 1;

			ps.setString(index++, uh.getFileLocation());
			ps.setString(index++, uh.getFileName());
			ps.setDate(index++, JdbcUtil.getDate(uh.getTransactionDate()));
			ps.setInt(index++, uh.getTotalRecords());
			ps.setInt(index++, uh.getSuccessCount());
			ps.setInt(index++, uh.getFailedCount());
			ps.setString(index++, uh.getModule());
			ps.setString(index++, uh.getEntityCode());
			ps.setBoolean(index++, uh.isFileDownload());
			ps.setDate(index++, JdbcUtil.getDate(uh.getApprovedDate()));
			ps.setObject(index++, uh.getMakerId());
			ps.setObject(index++, uh.getApproverId());
			ps.setInt(index++, uh.getVersion());
			ps.setLong(index++, uh.getLastMntBy());
			ps.setTimestamp(index++, uh.getLastMntOn());
			ps.setString(index++, uh.getRecordStatus());
			ps.setString(index++, uh.getRoleCode());
			ps.setString(index++, uh.getNextRoleCode());
			ps.setString(index++, uh.getTaskId());
			ps.setString(index++, uh.getNextTaskId());
			ps.setString(index++, uh.getRecordType());
			ps.setLong(index++, uh.getWorkflowId());

			ps.setLong(index++, uh.getUploadId());

			if (tableType == TableType.TEMP_TAB) {
				ps.setTimestamp(index, uh.getPrevMntOn());
			} else {
				ps.setInt(index, uh.getVersion() - 1);
			}

		});

		if (recordCount == 0) {
			throw new ConcurrencyException();
		}
	}

	@Override
	public void delete(UploadHeader uh, TableType tableType) {
		StringBuilder sql = new StringBuilder("Delete From UploadHeader");
		sql.append(tableType.getSuffix());
		sql.append(" Where UploadId = ?");
		sql.append(QueryUtil.getConcurrencyClause(tableType));

		logger.debug(Literal.SQL + sql.toString());

		try {
			jdbcOperations.update(sql.toString(), ps -> {
				int index = 1;

				ps.setLong(index++, uh.getUploadId());

				if (tableType == TableType.TEMP_TAB) {
					ps.setTimestamp(index, uh.getPrevMntOn());
				} else {
					ps.setInt(index, uh.getVersion() - 1);
				}

			});
		} catch (DataAccessException e) {
			throw new DependencyFoundException(e);
		}
	}

	@Override
	public boolean isDuplicateKey(long uploadId, String fileName, TableType tableType) {
		String sql;
		String whereClause = "FileName = ? and UploadId != ?";

		Object[] obj = new Object[] { fileName, uploadId };

		switch (tableType) {
		case MAIN_TAB:
			sql = QueryUtil.getCountQuery("UploadHeader", whereClause);
			break;
		case TEMP_TAB:
			sql = QueryUtil.getCountQuery("UploadHeader_Temp", whereClause);
			break;
		default:
			sql = QueryUtil.getCountQuery(new String[] { "UploadHeader_Temp", "UploadHeader" }, whereClause);
			obj = new Object[] { fileName, uploadId, fileName, uploadId };
			break;
		}

		logger.debug(Literal.SQL + sql);

		return jdbcOperations.queryForObject(sql, Integer.class, obj) > 0;
	}

	@Override
	public UploadHeader getUploadHeader() {
		String moduleName = "UploadHeader";
		WorkFlowDetails wfd = WorkFlowUtil.getWorkFlowDetails(moduleName);
		UploadHeader uploadHeader = new UploadHeader();

		if (wfd != null) {
			uploadHeader.setWorkflowId(wfd.getWorkFlowId());
		}

		return uploadHeader;
	}

	@Override
	public boolean isFileDownload(long uploadID, String tableType) {
		StringBuilder sql = new StringBuilder("Select Filedownload From UploadHeader");
		sql.append(tableType);
		sql.append(" Where UploadID = ?");

		logger.debug(Literal.SQL + sql.toString());

		try {
			return jdbcOperations.queryForObject(sql.toString(), Boolean.class, uploadID);
		} catch (EmptyResultDataAccessException e) {
			logger.warn(Message.NO_RECORD_FOUND);
			return false;
		}
	}

	@Override
	public List<UploadReceipt> getSuccesFailedReceiptCount(long uploadId) {
		String sql = "Select Count(UploadId) UploadId, Status From UploadReceipt Where UploadId = ?";

		logger.debug(Literal.SQL + sql);

		List<UploadReceipt> urList = jdbcOperations.query(sql, ps -> {
			int index = 1;

			ps.setLong(index, uploadId);
		}, (rs, rowNum) -> {
			UploadReceipt ur = new UploadReceipt();

			ur.setUploadId(rs.getLong("UploadId"));
			ur.setStatus(rs.getString("Status"));
			return ur;
		});

		return urList.stream().sorted((s1, s2) -> StringUtils.compare(s1.getStatus(), s2.getStatus()))
				.collect(Collectors.toList());
	}
}