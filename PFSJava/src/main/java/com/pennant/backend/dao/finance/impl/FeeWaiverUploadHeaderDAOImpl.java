package com.pennant.backend.dao.finance.impl;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.EmptyResultDataAccessException;

import com.pennant.backend.dao.finance.FeeWaiverUploadHeaderDAO;
import com.pennant.backend.model.WorkFlowDetails;
import com.pennant.backend.model.expenses.FeeWaiverUploadHeader;
import com.pennant.backend.util.WorkFlowUtil;
import com.pennanttech.pennapps.core.ConcurrencyException;
import com.pennanttech.pennapps.core.jdbc.JdbcUtil;
import com.pennanttech.pennapps.core.jdbc.SequenceDao;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pennapps.core.resource.Message;
import com.pennanttech.pff.core.TableType;
import com.pennanttech.pff.core.util.QueryUtil;

public class FeeWaiverUploadHeaderDAOImpl extends SequenceDao<FeeWaiverUploadHeader>
		implements FeeWaiverUploadHeaderDAO {
	private static Logger logger = LogManager.getLogger(FeeWaiverUploadHeaderDAOImpl.class);

	public FeeWaiverUploadHeaderDAOImpl() {
		super();
	}

	@Override
	public long save(FeeWaiverUploadHeader uh, TableType tableType) {
		if (uh.getUploadId() == Long.MIN_VALUE) {
			uh.setUploadId(getNextValue("SeqFeeWaiverUploadHeader"));
		}

		StringBuilder sql = new StringBuilder();
		sql.append(" Insert Into FeeWaiverUploadHeader");
		sql.append(tableType.getSuffix());
		sql.append(" (UploadId, FileLocation, FileName, TransactionDate, TotalRecords, SuccessCount, FailedCount");
		sql.append(", Module, FileDownload, Version, LastMntBy, LastMntOn, RecordStatus, RoleCode, NextRoleCode");
		sql.append(", TaskId, NextTaskId, RecordType, WorkflowId, ApprovedDate, MakerId, ApproverId)");
		sql.append(" Values(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?");
		sql.append(", ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)");

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
			ps.setLong(index++, uh.getWorkflowId());
			ps.setDate(index++, JdbcUtil.getDate(uh.getApprovedDate()));
			ps.setLong(index++, uh.getMakerId());
			ps.setObject(index, uh.getApproverId());

		});

		return uh.getUploadId();
	}

	@Override
	public void update(FeeWaiverUploadHeader uh, TableType tableType) {
		StringBuilder sql = new StringBuilder("update FeeWaiverUploadHeader");
		sql.append(tableType.getSuffix());
		sql.append(" Set FileLocation = ?, FileName = ?, TransactionDate = ?,");
		sql.append(" TotalRecords = ?, SuccessCount = ?, FailedCount = ?,");
		sql.append(" Module = ?, FileDownload = ?, Version = ?, LastMntBy = ?,");
		sql.append(" LastMntOn = ?, RecordStatus= ?, RoleCode = ?,");
		sql.append(" NextRoleCode = ?, TaskId = ?, NextTaskId = ?, RecordType = ?,");
		sql.append(" WorkflowId = ? ,ApprovedDate = ?, MakerId = ?, ApproverId= ?");
		sql.append(" where UploadId = ?");
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
			ps.setLong(index++, uh.getWorkflowId());
			ps.setDate(index++, JdbcUtil.getDate(uh.getApprovedDate()));
			ps.setLong(index++, uh.getMakerId());
			ps.setObject(index++, uh.getApproverId());

			if (tableType == TableType.TEMP_TAB) {
				ps.setTimestamp(index++, uh.getPrevMntOn());
			} else {
				ps.setInt(index++, uh.getVersion() - 1);
			}

			ps.setLong(index, uh.getUploadId());
		});

		if (recordCount == 0) {
			throw new ConcurrencyException();
		}
	}

	@Override
	public void delete(FeeWaiverUploadHeader uh, TableType tableType) {
		StringBuilder sql = new StringBuilder("Delete from FeeWaiverUploadHeader");
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
			logger.error(Literal.EXCEPTION, e);
		}
	}

	@Override
	public FeeWaiverUploadHeader getUploadHeader() {
		WorkFlowDetails workFlowDetails = WorkFlowUtil.getWorkFlowDetails("FeeWaiverUploadHeader");
		FeeWaiverUploadHeader uploadHeader = new FeeWaiverUploadHeader();
		if (workFlowDetails != null) {
			uploadHeader.setWorkflowId(workFlowDetails.getWorkFlowId());
		}

		return uploadHeader;
	}

	@Override
	public boolean isFileNameExist(String fileName) {
		String sql = "Select UploadId From FeeWaiverUploadHeader Where FileName = ?";

		logger.debug(Literal.SQL + sql);

		try {
			return this.jdbcOperations.queryForObject(sql, Long.class, fileName) > 0;
		} catch (EmptyResultDataAccessException e) {
			logger.warn(Message.NO_RECORD_FOUND);
			return false;
		}
	}

	@Override
	public FeeWaiverUploadHeader getUploadHeaderById(long uploadId, String type) {
		StringBuilder sql = new StringBuilder("Select");
		sql.append(" UploadId, FileLocation, FileName, TransactionDate, TotalRecords, SuccessCount");
		sql.append(", FailedCount, Module, FileDownload, Version, LastMntBy, LastMntOn, RecordStatus, RoleCode");
		sql.append(", NextRoleCode, TaskId, NextTaskId, RecordType, WorkflowId , ApprovedDate, MakerId, ApproverId");
		if (StringUtils.trimToEmpty(type).contains("View")) {
			sql.append(", UserName");
		}
		sql.append(" From FeeWaiverUploadHeader");
		sql.append(type);
		sql.append(" Where UploadId = ?");

		logger.debug(Literal.SQL + sql.toString());

		try {
			return this.jdbcOperations.queryForObject(sql.toString(), (rs, rowNum) -> {
				FeeWaiverUploadHeader fw = new FeeWaiverUploadHeader();

				fw.setUploadId(rs.getLong("UploadId"));
				fw.setFileLocation(rs.getString("FileLocation"));
				fw.setFileName(rs.getString("FileName"));
				fw.setTransactionDate(rs.getTimestamp("TransactionDate"));
				fw.setTotalRecords(rs.getInt("TotalRecords"));
				fw.setSuccessCount(rs.getInt("SuccessCount"));
				fw.setFailedCount(rs.getInt("FailedCount"));
				fw.setModule(rs.getString("Module"));
				fw.setFileDownload(rs.getBoolean("FileDownload"));
				fw.setVersion(rs.getInt("Version"));
				fw.setLastMntBy(rs.getLong("LastMntBy"));
				fw.setLastMntOn(rs.getTimestamp("LastMntOn"));
				fw.setRecordStatus(rs.getString("RecordStatus"));
				fw.setRoleCode(rs.getString("RoleCode"));
				fw.setNextRoleCode(rs.getString("NextRoleCode"));
				fw.setTaskId(rs.getString("TaskId"));
				fw.setNextTaskId(rs.getString("NextTaskId"));
				fw.setRecordType(rs.getString("RecordType"));
				fw.setWorkflowId(rs.getLong("WorkflowId"));
				fw.setApprovedDate(rs.getTimestamp("ApprovedDate"));
				fw.setMakerId(rs.getLong("MakerId"));
				fw.setApproverId(rs.getLong("ApproverId"));
				if (StringUtils.trimToEmpty(type).contains("View")) {
					fw.setUserName(rs.getString("UserName"));
				}
				return fw;
			}, uploadId);
		} catch (EmptyResultDataAccessException e) {
			logger.warn(Message.NO_RECORD_FOUND);
			return null;
		}
	}

	@Override
	public boolean isDuplicateKey(long uploadId, String fileName, TableType tableType) {
		String whereClause = "UploadId != ? and FileName = ?";

		Object[] obj = new Object[] { uploadId, fileName };

		String sql;
		switch (tableType) {
		case MAIN_TAB:
			sql = QueryUtil.getCountQuery("FeeWaiverUploadHeader", whereClause);
			break;
		case TEMP_TAB:
			sql = QueryUtil.getCountQuery("FeeWaiverUploadHeader_Temp", whereClause);
			break;
		default:
			sql = QueryUtil.getCountQuery(new String[] { "FeeWaiverUploadHeader_Temp", "FeeWaiverUploadHeader" },
					whereClause);

			obj = new Object[] { uploadId, fileName, uploadId, fileName };
			break;
		}

		logger.debug(Literal.SQL + sql);

		return jdbcOperations.queryForObject(sql, Integer.class, obj) > 0;
	}

	@Override
	public boolean isFileDownload(long uploadID, String tableType) {
		StringBuilder sql = new StringBuilder();
		sql.append(" Select Filedownload From FeeWaiverUploadHeader");
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
	public void updateFileDownload(long uploadId, boolean fileDownload, String type) {
		StringBuilder sql = new StringBuilder();
		sql.append("Update FeeWaiverUploadHeader");
		sql.append(StringUtils.trim(type));
		sql.append(" Set FileDownload = ?");
		sql.append(" Where UploadId = ?");

		logger.debug(Literal.SQL + sql.toString());

		try {
			this.jdbcOperations.update(sql.toString(), ps -> {
				ps.setBoolean(1, fileDownload);
				ps.setLong(2, uploadId);
			});
		} catch (Exception e) {
			logger.error(Literal.EXCEPTION, e);
			throw e;
		}
	}
}
