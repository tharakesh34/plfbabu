package com.pennant.backend.dao.feewaiverupload.impl;

import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.dao.EmptyResultDataAccessException;

import com.pennant.backend.dao.feewaiverupload.FeeWaiverUploadDAO;
import com.pennant.backend.model.finance.FeeWaiverUpload;
import com.pennanttech.pennapps.core.ConcurrencyException;
import com.pennanttech.pennapps.core.jdbc.JdbcUtil;
import com.pennanttech.pennapps.core.jdbc.SequenceDao;
import com.pennanttech.pennapps.core.resource.Literal;

public class FeeWaiverUploadDAOImpl extends SequenceDao<FeeWaiverUpload> implements FeeWaiverUploadDAO {
	private static Logger logger = LogManager.getLogger(FeeWaiverUploadDAOImpl.class);

	public FeeWaiverUploadDAOImpl() {
		super();
	}

	@Override
	public String save(FeeWaiverUpload fwu, String type) {
		if (fwu.getWaiverId() == Long.MIN_VALUE) {
			fwu.setWaiverId(getNextValue("SeqFeeWaiverHeader"));
		}

		StringBuilder sql = new StringBuilder("Insert Into FeeWaiverUploads");
		sql.append(StringUtils.trimToEmpty(type));
		sql.append(" (WaiverId, UploadId, FinReference, FeeTypeCode, ValueDate, WaivedAmount");
		sql.append(", Remarks, Status, Reason, RejectStage");
		sql.append(", Version, LastMntBy, LastMntOn, RecordStatus, RoleCode, NextRoleCode");
		sql.append(", TaskId, NextTaskId, RecordType, WorkflowId)");
		sql.append(" Values(?, ?, ?, ?, ?, ?, ?, ?, ?");
		sql.append(", ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)");

		logger.debug(Literal.SQL + sql.toString());

		this.jdbcOperations.update(sql.toString(), ps -> {
			int index = 1;

			ps.setLong(index++, fwu.getWaiverId());
			ps.setLong(index++, fwu.getUploadId());
			ps.setString(index++, fwu.getFinReference());
			ps.setString(index++, fwu.getFeeTypeCode());
			ps.setDate(index++, JdbcUtil.getDate(fwu.getValueDate()));
			ps.setBigDecimal(index++, fwu.getWaivedAmount());
			ps.setString(index++, fwu.getRemarks());
			ps.setString(index++, fwu.getStatus());
			ps.setString(index++, fwu.getReason());
			ps.setString(index++, fwu.getRejectStage());
			ps.setInt(index++, fwu.getVersion());
			ps.setLong(index++, fwu.getLastMntBy());
			ps.setTimestamp(index++, fwu.getLastMntOn());
			ps.setString(index++, fwu.getRecordStatus());
			ps.setString(index++, fwu.getRoleCode());
			ps.setString(index++, fwu.getNextRoleCode());
			ps.setString(index++, fwu.getTaskId());
			ps.setString(index++, fwu.getNextTaskId());
			ps.setString(index++, fwu.getRecordType());
			ps.setLong(index, fwu.getWorkflowId());

		});

		return fwu.getFinReference();
	}

	@Override
	public void update(FeeWaiverUpload fwu, String type) {
		StringBuilder sql = new StringBuilder("Update FeeWaiverUploads set");
		sql.append("FinReference = ?, FeeTypeCode = ?, ValueDate = ?, WaivedAmount = ?, Remarks = ?");
		sql.append(", Status = ?, Reason = ?, Version = ?, LastMntBy = ?, LastMntOn = ?, RoleCode = ?");
		sql.append(", RecordStatus = ?, NextRoleCode = ?, TaskId = ?, NextTaskId = ?, RecordType = ?");

		if (!type.endsWith("_Temp")) {
			sql.append("  AND Version= ?");
		}

		logger.debug(Literal.SQL + sql.toString());

		int recordCount = jdbcOperations.update(sql.toString(), ps -> {
			int index = 1;

			ps.setString(index++, fwu.getFinReference());
			ps.setString(index++, fwu.getFeeTypeCode());
			ps.setDate(index++, JdbcUtil.getDate(fwu.getValueDate()));
			ps.setBigDecimal(index++, fwu.getWaivedAmount());
			ps.setString(index++, fwu.getRemarks());
			ps.setString(index++, fwu.getStatus());
			ps.setString(index++, fwu.getReason());
			ps.setInt(index++, fwu.getVersion());
			ps.setLong(index++, fwu.getLastMntBy());
			ps.setTimestamp(index++, fwu.getLastMntOn());
			ps.setString(index++, fwu.getRoleCode());
			ps.setString(index++, fwu.getRecordStatus());
			ps.setString(index++, fwu.getNextRoleCode());
			ps.setString(index++, fwu.getTaskId());
			ps.setString(index++, fwu.getNextTaskId());
			ps.setString(index++, fwu.getRecordType());
			ps.setLong(index++, fwu.getWorkflowId());
			ps.setString(index++, fwu.getRejectStage());

			if (!type.endsWith("_Temp")) {
				ps.setInt(index, fwu.getVersion() - 1);
			}
		});

		if (recordCount == 0) {
			throw new ConcurrencyException();
		}
	}

	@Override
	public void deleteByUploadId(long uploadId, String type) {
		StringBuilder sql = new StringBuilder("Delete From FeeWaiverUploads");
		sql.append(StringUtils.trimToEmpty(type));
		sql.append(" Where UploadId = ?");

		logger.debug(Literal.SQL + sql.toString());

		this.jdbcOperations.update(sql.toString(), ps -> ps.setLong(1, uploadId));
	}

	public List<FeeWaiverUpload> getFeeWaiverListByUploadId(long uploadId, String type) {
		StringBuilder sql = new StringBuilder("Select");
		sql.append(" WaiverId, UploadId, FinReference, FeeTypeCode, ValueDate, WaivedAmount, Remarks");
		sql.append(", Status, Reason, RejectStage");

		if (type.contains("View")) {
			sql.append(", FeeTypeId");
		}

		sql.append(", Version, LastMntBy, LastMntOn, RecordStatus, RoleCode");
		sql.append(", NextRoleCode, TaskId, NextTaskId, RecordType, WorkflowId");
		sql.append(" from FeeWaiverUploads");
		sql.append(StringUtils.trimToEmpty(type));
		sql.append(" Where UploadId = ?");

		logger.debug(Literal.SQL + sql.toString());

		try {
			return this.jdbcOperations.query(sql.toString(), ps -> ps.setLong(1, uploadId), (rs, rowNum) -> {
				FeeWaiverUpload fw = new FeeWaiverUpload();

				fw.setWaiverId(rs.getLong("WaiverId"));
				fw.setUploadId(rs.getLong("UploadId"));
				fw.setFinReference(rs.getString("FinReference"));
				fw.setFeeTypeCode(rs.getString("FeeTypeCode"));
				fw.setValueDate(rs.getTimestamp("ValueDate"));
				fw.setWaivedAmount(rs.getBigDecimal("WaivedAmount"));
				fw.setRemarks(rs.getString("Remarks"));
				fw.setStatus(rs.getString("Status"));
				fw.setReason(rs.getString("Reason"));
				fw.setRejectStage(rs.getString("RejectStage"));
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

				if (type.contains("View")) {
					fw.setFeeTypeID(rs.getLong("FeeTypeId"));
				}

				return fw;
			});

		} catch (EmptyResultDataAccessException e) {
			//
		}

		return null;
	}
}
