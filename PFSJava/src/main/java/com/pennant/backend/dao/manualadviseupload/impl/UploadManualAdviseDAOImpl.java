package com.pennant.backend.dao.manualadviseupload.impl;

import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.dao.DataAccessException;

import com.pennant.backend.dao.manualadviseupload.UploadManualAdviseDAO;
import com.pennant.backend.model.finance.UploadManualAdvise;
import com.pennant.backend.util.UploadConstants;
import com.pennanttech.pennapps.core.ConcurrencyException;
import com.pennanttech.pennapps.core.DependencyFoundException;
import com.pennanttech.pennapps.core.jdbc.JdbcUtil;
import com.pennanttech.pennapps.core.jdbc.SequenceDao;
import com.pennanttech.pennapps.core.resource.Literal;

public class UploadManualAdviseDAOImpl extends SequenceDao<UploadManualAdvise> implements UploadManualAdviseDAO {
	private static Logger logger = LogManager.getLogger(UploadManualAdviseDAOImpl.class);

	public UploadManualAdviseDAOImpl() {
		super();
	}

	public List<UploadManualAdvise> getAdviseUploadsByUploadId(long uploadId, String type) {
		StringBuilder sql = new StringBuilder("Select");
		sql.append(" AdviseId, UploadId, FinID, FinReference, AdviseType, FeeTypeCode");
		sql.append(", ValueDate, AdviseAmount, Remarks, Status, Reason, RejectStage");
		if (type.contains("View")) {
			sql.append(", FeeTypeID");
		}
		sql.append(", Version, LastMntBy, LastMntOn, RecordStatus");
		sql.append(", RoleCode, NextRoleCode, TaskId, NextTaskId, RecordType, WorkflowId");

		sql.append(" From AdviseUploads");
		sql.append(StringUtils.trimToEmpty(type));
		sql.append(" Where UploadId = ?");

		logger.debug(Literal.SQL + sql.toString());

		return this.jdbcOperations.query(sql.toString(), (rs, i) -> {
			UploadManualAdvise uma = new UploadManualAdvise();

			uma.setAdviseId(rs.getLong("AdviseId"));
			uma.setUploadId(rs.getLong("UploadId"));
			uma.setFinID(rs.getLong("FinID"));
			uma.setFinReference(rs.getString("FinReference"));
			uma.setAdviseType(rs.getString("AdviseType"));
			uma.setFeeTypeCode(rs.getString("FeeTypeCode"));
			uma.setValueDate(rs.getTimestamp("ValueDate"));
			uma.setAdviseAmount(rs.getBigDecimal("AdviseAmount"));
			uma.setRemarks(rs.getString("Remarks"));
			uma.setStatus(rs.getString("Status"));
			uma.setReason(rs.getString("Reason"));
			uma.setRejectStage(rs.getString("RejectStage"));
			uma.setFeeTypeID(JdbcUtil.getLong(rs.getObject("FeeTypeID")));
			uma.setVersion(rs.getInt("Version"));
			uma.setLastMntBy(rs.getLong("LastMntBy"));
			uma.setLastMntOn(rs.getTimestamp("LastMntOn"));
			uma.setRecordStatus(rs.getString("RecordStatus"));
			uma.setRoleCode(rs.getString("RoleCode"));
			uma.setNextRoleCode(rs.getString("NextRoleCode"));
			uma.setTaskId(rs.getString("TaskId"));
			uma.setNextTaskId(rs.getString("NextTaskId"));
			uma.setRecordType(rs.getString("RecordType"));
			uma.setWorkflowId(rs.getLong("WorkflowId"));

			return uma;
		}, uploadId);
	}

	@Override
	public void save(UploadManualAdvise uma, String type) {
		if (uma.getAdviseId() == Long.MIN_VALUE) {
			uma.setAdviseId(getNextValue("SeqManualAdvise"));
		}

		StringBuilder sql = new StringBuilder("Insert Into AdviseUploads");
		sql.append(StringUtils.trimToEmpty(type));
		sql.append("(AdviseId, UploadId, FinID, FinReference, AdviseType, FeeTypeCode, ValueDate, AdviseAmount");
		sql.append(" , Remarks, Status, Reason, Version, LastMntBy, LastMntOn, RecordStatus, RoleCode, NextRoleCode");
		sql.append(", TaskId, NextTaskId, RecordType, WorkflowId, RejectStage, ManualAdviseId)");
		sql.append(" Values(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)");

		logger.debug(Literal.SQL + sql.toString());

		this.jdbcOperations.update(sql.toString(), ps -> {
			int index = 1;

			ps.setLong(index++, uma.getAdviseId());
			ps.setLong(index++, uma.getUploadId());
			ps.setLong(index++, uma.getFinID());
			ps.setString(index++, uma.getFinReference());
			ps.setString(index++, uma.getAdviseType());
			ps.setString(index++, uma.getFeeTypeCode());
			ps.setDate(index++, JdbcUtil.getDate(uma.getValueDate()));
			ps.setBigDecimal(index++, uma.getAdviseAmount());
			ps.setString(index++, uma.getRemarks());
			ps.setString(index++, uma.getStatus());
			ps.setString(index++, uma.getReason());
			ps.setInt(index++, uma.getVersion());
			ps.setLong(index++, uma.getLastMntBy());
			ps.setTimestamp(index++, uma.getLastMntOn());
			ps.setString(index++, uma.getRecordStatus());
			ps.setString(index++, uma.getRoleCode());
			ps.setString(index++, uma.getNextRoleCode());
			ps.setString(index++, uma.getTaskId());
			ps.setString(index++, uma.getNextTaskId());
			ps.setString(index++, uma.getRecordType());
			ps.setLong(index++, uma.getWorkflowId());
			ps.setString(index++, uma.getRejectStage());
			ps.setLong(index, uma.getManualAdviseId());
		});

	}

	@Override
	public void update(UploadManualAdvise uma, String type) {
		StringBuilder sql = new StringBuilder("Update AdviseUploads");
		sql.append(StringUtils.trimToEmpty(type));
		sql.append(" Set");
		sql.append(" FinID = ?, FinReference = ?, AdviseType = ?, FeeTypeCode = ?, ValueDate = ?, AdviseAmount = ?");
		sql.append(", Remarks = ?, Status = ?, Reason = ?, Version = ?, LastMntBy = ?, LastMntOn = ?");
		sql.append(", RoleCode = ?, RecordStatus = ?, NextRoleCode = ?, TaskId = ?, NextTaskId = ?");
		sql.append(", RecordType = ?, WorkflowId = ?, RejectStage = ?, ManualAdviseId = ?");
		sql.append(" Where UploadId = ? and AdviseId = ?");

		if (!type.endsWith("_Temp")) {
			sql.append(" and Version = ? - 1");
		}

		logger.debug(Literal.SQL + sql.toString());

		int recordCount = this.jdbcOperations.update(sql.toString(), ps -> {
			int index = 1;

			ps.setLong(index++, uma.getFinID());
			ps.setString(index++, uma.getFinReference());
			ps.setString(index++, uma.getAdviseType());
			ps.setString(index++, uma.getFeeTypeCode());
			ps.setDate(index++, JdbcUtil.getDate(uma.getValueDate()));
			ps.setBigDecimal(index++, uma.getAdviseAmount());
			ps.setString(index++, uma.getRemarks());
			ps.setString(index++, uma.getStatus());
			ps.setString(index++, uma.getReason());
			ps.setInt(index++, uma.getVersion());
			ps.setLong(index++, uma.getLastMntBy());
			ps.setTimestamp(index++, uma.getLastMntOn());
			ps.setString(index++, uma.getRoleCode());
			ps.setString(index++, uma.getRecordStatus());
			ps.setString(index++, uma.getNextRoleCode());
			ps.setString(index++, uma.getTaskId());
			ps.setString(index++, uma.getNextTaskId());
			ps.setString(index++, uma.getRecordType());
			ps.setLong(index++, uma.getWorkflowId());
			ps.setString(index++, uma.getRejectStage());
			ps.setLong(index++, uma.getManualAdviseId());
			ps.setString(index++, uma.getRecordType());
			ps.setLong(index++, uma.getWorkflowId());
			ps.setString(index++, uma.getRejectStage());
			ps.setLong(index++, uma.getManualAdviseId());

			ps.setLong(index++, uma.getUploadId());
			ps.setLong(index++, uma.getAdviseId());

			if (!type.endsWith("_Temp")) {
				ps.setInt(index, uma.getVersion());
			}
		});

		if (recordCount == 0) {
			throw new ConcurrencyException();
		}
	}

	@Override
	public void deleteByUploadId(long uploadId, String type) {
		StringBuilder sql = new StringBuilder("Delete From AdviseUploads");
		sql.append(StringUtils.trimToEmpty(type));
		sql.append(" Where UploadId = ?");

		logger.debug(Literal.SQL + sql.toString());

		try {
			this.jdbcOperations.update(sql.toString(), ps -> ps.setLong(1, uploadId));
		} catch (DataAccessException e) {
			throw new DependencyFoundException(e);
		}
	}

	@Override
	public boolean getAdviseUploadsByFinReference(long finID, long uploadId, String type) {
		StringBuilder sql = new StringBuilder("Select Count(FinID)");
		sql.append(" From AdviseUploads");
		sql.append(type);
		sql.append(" Where FinID = ? and Status = ?");

		Object[] args = new Object[] { finID, UploadConstants.UPLOAD_STATUS_SUCCESS };

		if (uploadId > 0) {
			sql.append(" and UploadId != ?");
			args = new Object[] { finID, UploadConstants.UPLOAD_STATUS_SUCCESS, uploadId };
		}

		logger.debug(Literal.SQL + sql.toString());

		return jdbcOperations.queryForObject(sql.toString(), Integer.class, args) > 0;
	}

}