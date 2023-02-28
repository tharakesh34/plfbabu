package com.pennant.pff.upload.dao.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;

import com.pennant.backend.model.WorkFlowDetails;
import com.pennant.backend.util.WorkFlowUtil;
import com.pennant.pff.upload.dao.UploadDAO;
import com.pennant.pff.upload.model.FileUploadHeader;
import com.pennanttech.pennapps.core.AppException;
import com.pennanttech.pennapps.core.ConcurrencyException;
import com.pennanttech.pennapps.core.DependencyFoundException;
import com.pennanttech.pennapps.core.jdbc.JdbcUtil;
import com.pennanttech.pennapps.core.jdbc.SequenceDao;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pennapps.core.resource.Message;
import com.pennanttech.pennapps.core.util.DateUtil;
import com.pennanttech.pff.core.TableType;
import com.pennanttech.pff.file.UploadContants.Status;
import com.pennanttech.pff.file.UploadTypes;

public class UploadDAOImpl extends SequenceDao<FileUploadHeader> implements UploadDAO {

	@Override
	public WorkFlowDetails getWorkFlow(String moduleCode) {
		return WorkFlowUtil.getWorkFlowDetails(moduleCode);
	}

	@Override
	public long saveHeader(FileUploadHeader header) {
		StringBuilder sql = new StringBuilder("Insert into FILE_UPLOAD_HEADER");
		sql.append(" (EntityCode, Type, FileName, TotalRecords, SuccessRecords, FailureRecords");
		sql.append(", Progress, CreatedBy, CreatedOn, ApprovedBy, ApprovedOn, LastMntBy, LastMntOn");
		sql.append(", Version, RecordStatus, RoleCode, NextRoleCode, TaskId, NextTaskId");
		sql.append(", RecordType, WorkflowId");
		sql.append(") Values(");
		sql.append(" ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)");

		logger.debug(Literal.SQL.concat(sql.toString()));

		KeyHolder keyHolder = new GeneratedKeyHolder();

		try {
			this.jdbcOperations.update(new PreparedStatementCreator() {

				@Override
				public PreparedStatement createPreparedStatement(Connection con) throws SQLException {
					PreparedStatement ps = con.prepareStatement(sql.toString(), new String[] { "id" });

					int index = 0;

					ps.setString(++index, header.getEntityCode());
					ps.setString(++index, header.getType());
					ps.setString(++index, header.getFileName());
					ps.setInt(++index, header.getTotalRecords());
					ps.setInt(++index, header.getSuccessRecords());
					ps.setInt(++index, header.getFailureRecords());
					ps.setInt(++index, header.getProgress());
					ps.setLong(++index, header.getCreatedBy());
					ps.setTimestamp(++index, header.getCreatedOn());
					ps.setObject(++index, header.getApprovedBy());
					ps.setTimestamp(++index, header.getApprovedOn());
					ps.setLong(++index, header.getLastMntBy());
					ps.setTimestamp(++index, header.getLastMntOn());
					ps.setInt(++index, header.getVersion());
					ps.setString(++index, header.getRecordStatus());
					ps.setString(++index, header.getRoleCode());
					ps.setString(++index, header.getNextRoleCode());
					ps.setString(++index, header.getTaskId());
					ps.setString(++index, header.getNextTaskId());
					ps.setString(++index, header.getRecordType());
					ps.setLong(++index, header.getWorkflowId());

					return ps;
				}
			}, keyHolder);
		} catch (DuplicateKeyException e) {
			throw new AppException("The File Name is Already Processed");
		}

		Number key = keyHolder.getKey();

		if (key == null) {
			return 0;
		}

		return key.longValue();
	}

	@Override
	public int update(FileUploadHeader header, TableType tableType) {
		StringBuilder sql = new StringBuilder("Update");
		sql.append(" FILE_UPLOAD_HEADER");
		sql.append(" Set FileName = ?, SuccessRecords = ?, FailureRecords = ?, TotalRecords = ?");
		sql.append(", EntityCode = ?, Progress = ?, CreatedBy = ?, CreatedOn = ?");
		sql.append(", ApprovedBy = ?, ApprovedOn = ?");
		sql.append(", Version = ?, LastMntBy = ?, LastMntOn = ?, RecordStatus = ?, RoleCode = ?");
		sql.append(", NextRoleCode = ?, TaskId = ?, NextTaskId = ?, RecordType = ?, WorkflowId = ?");
		sql.append(" Where ID = ?");

		logger.debug(Literal.SQL.concat(sql.toString()));

		int recordCount = jdbcOperations.update(sql.toString(), ps -> {
			int index = 0;

			ps.setString(++index, header.getFileName());
			ps.setInt(++index, header.getSuccessRecords());
			ps.setInt(++index, header.getFailureRecords());
			ps.setInt(++index, header.getTotalRecords());
			ps.setString(++index, header.getEntityCode());
			ps.setInt(++index, header.getProgress());
			ps.setLong(++index, header.getCreatedBy());
			ps.setTimestamp(++index, header.getCreatedOn());
			ps.setObject(++index, header.getApprovedBy());
			ps.setTimestamp(++index, header.getApprovedOn());
			ps.setInt(++index, header.getVersion());
			ps.setLong(++index, header.getLastMntBy());
			ps.setTimestamp(++index, header.getLastMntOn());
			ps.setString(++index, header.getRecordStatus());
			ps.setString(++index, header.getRoleCode());
			ps.setString(++index, header.getNextRoleCode());
			ps.setString(++index, header.getTaskId());
			ps.setString(++index, header.getNextTaskId());
			ps.setString(++index, header.getRecordType());
			ps.setLong(++index, header.getWorkflowId());

			ps.setLong(++index, header.getId());
		});

		if (recordCount == 0) {
			throw new ConcurrencyException();
		}

		return recordCount;
	}

	@Override
	public void updateProgress(long headerID, int status) {
		String sql = "Update FILE_UPLOAD_HEADER Set Progress = ? Where Id = ?";

		logger.debug(Literal.SQL.concat(sql));

		this.jdbcOperations.update(sql, ps -> {
			ps.setInt(1, status);
			ps.setLong(2, headerID);
		});
	}

	@Override
	public void uploadHeaderStatusCnt(long headerID, int success, int failure) {
		StringBuilder sql = new StringBuilder("Update FILE_UPLOAD_HEADER");
		sql.append(" Set SuccessRecords = ?, FailureRecords = ?, TotalRecords = ?");
		sql.append(" Where Id = ?");

		logger.debug(Literal.SQL.concat(sql.toString()));

		this.jdbcOperations.update(sql.toString(), ps -> {
			int index = 0;

			ps.setInt(++index, success);
			ps.setInt(++index, failure);
			ps.setInt(++index, failure + success);
			ps.setLong(++index, headerID);
		});
	}

	@Override
	public boolean isExists(String fileName) {
		StringBuilder sql = new StringBuilder("Select Id From (");
		sql.append(" Select Id From FILE_UPLOAD_HEADER Where FileName = ?");
		sql.append(" Union All");
		sql.append(" Select Id From FILE_UPLOAD_HEADER_TEMP Where FileName = ?)");

		logger.debug(Literal.SQL.concat(sql.toString()));

		try {
			return this.jdbcOperations.queryForObject(sql.toString(), Long.class, fileName, fileName) > 0;
		} catch (EmptyResultDataAccessException e) {
			logger.warn(Message.NO_RECORD_FOUND);
			return false;
		}
	}

	@Override
	public List<FileUploadHeader> getHeaderData(List<String> roleCodes, String entityCode, Long id, Date fromDate,
			Date toDate, String type, String stage, String code) {
		StringBuilder sql = new StringBuilder("Select");
		sql.append(" uh.Id, uh.EntityCode, uh.Type, uh.FileName, uh.TotalRecords");
		sql.append(", uh.SuccessRecords, uh.FailureRecords, uh.ExecutionID, uh.Progress");
		sql.append(", uh.CreatedBy, uh.CreatedOn, uh.ApprovedBy, uh.ApprovedOn, uh.LastMntOn, uh.LastMntBy");
		sql.append(", uh.Version, uh.RecordStatus, uh.RoleCode, uh.NextRoleCode");
		sql.append(", uh.TaskId, uh.NextTaskId, uh.RecordType, uh.WorkflowId");
		sql.append(" From FILE_UPLOAD_HEADER uh");

		if ("A".equals(stage) && UploadTypes.PAYINS_REFUND.name().equals(type)) {
			sql.append(" Inner Join Clusters cl on cl.Entity = uh.EntityCode and cl.Code = ?");
		}

		sql.append(" Where uh.Type = ? and uh.Progress != ?");

		StringBuilder whereClause = prepareWhereClause(roleCodes, entityCode, id, fromDate, toDate, stage);

		if (whereClause.length() < 0) {
			return new ArrayList<>();
		}

		sql.append(whereClause.toString());

		logger.debug(Literal.SQL.concat(sql.toString()));

		return this.jdbcOperations.query(sql.toString(), ps -> {
			int index = 0;

			if ("A".equals(stage) && UploadTypes.PAYINS_REFUND.name().equals(type)) {
				ps.setString(++index, code);
			}

			ps.setString(++index, type);
			ps.setInt(++index, Status.IN_PROCESS.getValue());

			if (CollectionUtils.isNotEmpty(roleCodes)) {
				for (String roleCode : roleCodes) {
					ps.setString(++index, roleCode);
				}
			}

			if (StringUtils.isNotEmpty(entityCode)) {
				ps.setString(++index, entityCode);
			}

			if (id != null && id > 0) {
				ps.setLong(++index, id);
			}

			if (fromDate != null && toDate != null) {
				ps.setDate(++index, JdbcUtil.getDate(fromDate));
				ps.setDate(++index, JdbcUtil.getDate(DateUtil.addDays(toDate, 1)));
			}
		}, (rs, rowNum) -> {
			FileUploadHeader ruh = new FileUploadHeader();

			ruh.setId(rs.getLong("Id"));
			ruh.setEntityCode(rs.getString("EntityCode"));
			ruh.setType(rs.getString("Type"));
			ruh.setFileName(rs.getString("FileName"));
			ruh.setTotalRecords(rs.getInt("TotalRecords"));
			ruh.setSuccessRecords(rs.getInt("SuccessRecords"));
			ruh.setFailureRecords(rs.getInt("FailureRecords"));
			ruh.setExecutionID(rs.getLong("ExecutionID"));
			ruh.setProgress(rs.getInt("Progress"));
			ruh.setCreatedBy(rs.getLong("CreatedBy"));
			ruh.setCreatedOn(rs.getTimestamp("CreatedOn"));
			ruh.setApprovedBy(rs.getLong("ApprovedBy"));
			ruh.setApprovedOn(rs.getTimestamp("ApprovedOn"));
			ruh.setLastMntOn(rs.getTimestamp("LastMntOn"));
			ruh.setLastMntBy(rs.getLong("LastMntBy"));
			ruh.setVersion(rs.getInt("Version"));
			ruh.setRecordStatus(rs.getString("RecordStatus"));
			ruh.setRoleCode(rs.getString("RoleCode"));
			ruh.setNextRoleCode(rs.getString("NextRoleCode"));
			ruh.setTaskId(rs.getString("TaskId"));
			ruh.setNextTaskId(rs.getString("NextTaskId"));
			ruh.setRecordType(rs.getString("RecordType"));
			ruh.setWorkflowId(rs.getLong("WorkflowId"));

			return ruh;
		});
	}

	private StringBuilder prepareWhereClause(List<String> roleCodes, String entityCode, Long id, Date fromDate,
			Date toDate, String stage) {
		StringBuilder whereClause = new StringBuilder();

		if (CollectionUtils.isNotEmpty(roleCodes)) {
			whereClause.append(" and ");
			whereClause.append("(");
			if ("M".equals(stage)) {
				whereClause.append("uh.NextRoleCode is null or ");
			}
			whereClause.append("uh.NextRoleCode in (");
			whereClause.append(JdbcUtil.getInCondition(roleCodes));
			whereClause.append("))");
		}

		if (StringUtils.isNotEmpty(entityCode)) {
			whereClause.append(" and ");
			whereClause.append("uh.EntityCode = ?");
		}

		if (id != null && id > 0) {
			whereClause.append(" and ");
			whereClause.append(" uh.ID = ?");
		}

		if (fromDate != null && toDate != null) {
			whereClause.append(" and ");
			whereClause.append(" (uh.CreatedOn >= ? and uh.CreatedOn < ?)");
		}

		return whereClause;
	}

	@Override
	public void deleteHeader(FileUploadHeader header, TableType tableType) {
		String sql = "Delete From FILE_UPLOAD_HEADER".concat(tableType.getSuffix()).concat(" Where Id = ?");

		logger.debug(Literal.SQL.concat(sql));

		try {
			this.jdbcOperations.update(sql, header.getId());
		} catch (DataAccessException e) {
			throw new DependencyFoundException(e);
		}
	}

	@Override
	public boolean isFileDownlaoded(long id) {
		String sql = "Select count(Id) From FILE_UPLOAD_HEADER Where Id = ? and Progress = ?";
		logger.debug(Literal.SQL.concat(sql));

		try {
			return this.jdbcOperations.queryForObject(sql, (rs, rowNum) -> rs.getInt(1), id,
					Status.DOWNLOADED.getValue()) > 0;
		} catch (EmptyResultDataAccessException e) {
			logger.warn(Message.NO_RECORD_FOUND);
			return false;
		}
	}

	@Override
	public List<Long> getHeaderStatusCnt(long uploadID, String tableName) {
		String sql = "Select Id From ".concat(tableName).concat(" Where HeaderId = ?");

		logger.debug(Literal.SQL.concat(sql));

		return this.jdbcOperations.queryForList(sql, Long.class, uploadID);
	}

	@Override
	public void deleteDetail(long headerID, String tableName) {
		String sql = "Delete From ".concat(tableName).concat(" Where HeaderId = ?");

		logger.debug(Literal.SQL.concat(sql));

		jdbcOperations.update(sql, headerID);
	}

	@Override
	public void updateHeader(FileUploadHeader header) {
		StringBuilder sql = new StringBuilder("Update FILE_UPLOAD_HEADER");
		sql.append(" Set ExecutionID = ?, TotalRecords = ?, SuccessRecords = ?, FailureRecords = ?");
		sql.append(", Progress = ?, Remarks = Remarks + ?");
		sql.append(" Where ID = ?");

		logger.debug(Literal.SQL.concat(sql.toString()));

		jdbcOperations.update(sql.toString(), ps -> {
			int index = 0;

			ps.setLong(++index, header.getExecutionID());
			ps.setLong(++index, header.getTotalRecords());
			ps.setLong(++index, header.getSuccessRecords());
			ps.setLong(++index, header.getFailureRecords());
			ps.setInt(++index, header.getProgress());
			ps.setString(++index, header.getRemarks());

			ps.setLong(++index, header.getId());
		});
	}

	@Override
	public void updateHeader(List<FileUploadHeader> headerList) {
		StringBuilder sql = new StringBuilder("Update FILE_UPLOAD_HEADER");
		sql.append(" Set SuccessRecords = ?, FailureRecords = FailureRecords + ?");
		sql.append(", Progress = ?, Remarks = Remarks + ?");
		sql.append(", ApprovedBy = ?, ApprovedOn = ?");
		sql.append(", Version = Version + ?, LastMntBy = ?, LastMntOn = ?, RecordStatus = ?, RoleCode = ?");
		sql.append(", NextRoleCode = ?, TaskId = ?, NextTaskId = ?, RecordType = ?, WorkflowId = ?");
		sql.append(" Where ID = ?");

		logger.debug(Literal.SQL.concat(sql.toString()));

		jdbcOperations.batchUpdate(sql.toString(), new BatchPreparedStatementSetter() {

			@Override
			public void setValues(PreparedStatement ps, int i) throws SQLException {
				int index = 0;

				FileUploadHeader header = headerList.get(i);

				ps.setLong(++index, header.getSuccessRecords());
				ps.setLong(++index, header.getFailureRecords());
				ps.setInt(++index, header.getProgress());
				ps.setString(++index, header.getRemarks());
				ps.setObject(++index, header.getApprovedBy());
				ps.setTimestamp(++index, header.getApprovedOn());
				ps.setInt(++index, 1);
				ps.setLong(++index, header.getLastMntBy());
				ps.setTimestamp(++index, header.getLastMntOn());
				ps.setString(++index, header.getRecordStatus());
				ps.setString(++index, header.getRoleCode());
				ps.setString(++index, header.getNextRoleCode());
				ps.setString(++index, header.getTaskId());
				ps.setString(++index, header.getNextTaskId());
				ps.setString(++index, header.getRecordType());
				ps.setLong(++index, header.getWorkflowId());

				ps.setLong(++index, header.getId());
			}

			@Override
			public int getBatchSize() {
				return headerList.size();
			}
		});
	}

	@Override
	public void updateDownloadStatus(long headerID, int status) {
		String sql = "Update FILE_UPLOAD_HEADER Set Progress = ? Where Id = ?";

		logger.debug(Literal.SQL.concat(sql));

		this.jdbcOperations.update(sql, st -> {
			st.setInt(1, status);
			st.setLong(2, headerID);
		});
	}

	@Override
	public int isValidateApprove(List<FileUploadHeader> selectedHeaders, int status) {
		StringBuilder sql = new StringBuilder();
		sql.append("Select count(ID) From FILE_UPLOAD_HEADER");
		sql.append(" Where ID in (");
		sql.append(JdbcUtil.getInCondition(selectedHeaders));
		sql.append(") and Progress = ?");

		logger.debug(Literal.SQL.concat(sql.toString()));

		Object[] obj = new Object[selectedHeaders.size() + 1];

		int i = 0;
		for (FileUploadHeader hdr : selectedHeaders) {
			obj[i++] = hdr.getId();
		}

		obj[i] = status;

		return this.jdbcOperations.queryForObject(sql.toString(), Integer.class, obj);
	}

	@Override
	public void updateFailRecords(int sucessRecords, int faildrecords, long headerId) {
		String sql = "Update FILE_UPLOAD_HEADER Set SuccessRecords = SuccessRecords - ?, FailureRecords = FailureRecords + ? Where Id = ?";

		logger.debug(Literal.SQL.concat(sql));

		this.jdbcOperations.update(sql, st -> {
			int index = 0;
			
			st.setInt(++index, sucessRecords);
			st.setInt(++index, faildrecords);
			st.setLong(++index, headerId);
		});
	}
}
