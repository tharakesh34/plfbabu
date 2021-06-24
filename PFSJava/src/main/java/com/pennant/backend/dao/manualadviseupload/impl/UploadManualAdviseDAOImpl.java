package com.pennant.backend.dao.manualadviseupload.impl;

import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;

import com.pennant.backend.dao.manualadviseupload.UploadManualAdviseDAO;
import com.pennant.backend.model.finance.UploadManualAdvise;
import com.pennant.backend.util.UploadConstants;
import com.pennanttech.pennapps.core.ConcurrencyException;
import com.pennanttech.pennapps.core.jdbc.SequenceDao;
import com.pennanttech.pennapps.core.resource.Literal;

public class UploadManualAdviseDAOImpl extends SequenceDao<UploadManualAdvise> implements UploadManualAdviseDAO {
	private static Logger logger = LogManager.getLogger(UploadManualAdviseDAOImpl.class);

	public UploadManualAdviseDAOImpl() {
		super();
	}

	public List<UploadManualAdvise> getAdviseUploadsByUploadId(long uploadId, String type) {
		logger.debug("Entering");
		UploadManualAdvise uploadManualAdvise = new UploadManualAdvise();
		uploadManualAdvise.setUploadId(uploadId);

		StringBuilder selectSql = new StringBuilder("Select AdviseId, UploadId, FinReference, AdviseType,");
		selectSql.append(" FeeTypeCode, ValueDate, AdviseAmount, Remarks, Status, Reason, RejectStage");
		if (type.contains("View")) {
			selectSql.append("FeeTypeId, ");
		}
		selectSql.append(" Version, LastMntBy, LastMntOn, RecordStatus,");
		selectSql.append(" RoleCode, NextRoleCode, TaskId, NextTaskId, RecordType, WorkflowId");

		selectSql.append(" FROM AdviseUploads");
		selectSql.append(StringUtils.trimToEmpty(type));
		selectSql.append(" Where UploadId = :UploadId");

		logger.debug("selectListSql: " + selectSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(uploadManualAdvise);
		RowMapper<UploadManualAdvise> typeRowMapper = BeanPropertyRowMapper.newInstance(UploadManualAdvise.class);

		logger.debug("Leaving");
		return this.jdbcTemplate.query(selectSql.toString(), beanParameters, typeRowMapper);
	}

	@Override
	public String save(UploadManualAdvise uploadManualAdvise, String type) {
		logger.debug("Entering ");

		if (uploadManualAdvise.getAdviseId() == Long.MIN_VALUE) {
			uploadManualAdvise.setAdviseId(getNextValue("SeqManualAdvise"));
			logger.debug("get NextID:" + uploadManualAdvise.getAdviseId());
		}

		StringBuilder insertSql = new StringBuilder("Insert Into AdviseUploads");
		insertSql.append(StringUtils.trimToEmpty(type));
		insertSql.append(
				" (AdviseId, UploadId, FinReference, AdviseType, FeeTypeCode, ValueDate, AdviseAmount, Remarks, Status, Reason,");
		insertSql.append(
				" Version, LastMntBy, LastMntOn, RecordStatus, RoleCode, NextRoleCode, TaskId, NextTaskId, RecordType,");
		insertSql.append(" WorkflowId, RejectStage, ManualAdviseId)");
		insertSql.append(
				" Values(:AdviseId, :UploadId, :FinReference, :AdviseType, :FeeTypeCode, :ValueDate, :AdviseAmount, :Remarks, :Status, :Reason,");
		insertSql.append(
				" :Version, :LastMntBy, :LastMntOn, :RecordStatus, :RoleCode, :NextRoleCode, :TaskId, :NextTaskId, :RecordType,");
		insertSql.append("  :WorkflowId, :RejectStage, :ManualAdviseId)");

		logger.debug("insertSql: " + insertSql.toString());

		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(uploadManualAdvise);
		this.jdbcTemplate.update(insertSql.toString(), beanParameters);

		logger.debug("Leaving ");

		return uploadManualAdvise.getFinReference();
	}

	@Override
	public void update(UploadManualAdvise uploadManualAdvise, String type) {
		int recordCount = 0;
		logger.debug("Entering ");

		StringBuilder updateSql = new StringBuilder("Update AdviseUploads");
		updateSql.append(StringUtils.trimToEmpty(type));
		updateSql.append(
				" Set FinReference = :FinReference, AdviseType = :AdviseType, FeeTypeCode = :FeeTypeCode, ValueDate = :ValueDate,");
		updateSql.append(" AdviseAmount = :AdviseAmount, Remarks = :Remarks, Status = :Status, Reason = :Reason,");
		updateSql.append(" Version = :Version, LastMntBy = :LastMntBy, LastMntOn = :LastMntOn, RoleCode = :RoleCode,");
		updateSql.append(" RecordStatus= :RecordStatus, NextRoleCode = :NextRoleCode, TaskId = :TaskId,");
		updateSql.append(" NextTaskId = :NextTaskId, RecordType = :RecordType, WorkflowId = :WorkflowId,");
		updateSql.append(" RejectStage = :RejectStage, ManualAdviseId = :ManualAdviseId");
		updateSql.append(" Where UploadId = :UploadId AND AdviseId = :AdviseId");

		if (!type.endsWith("_Temp")) {
			updateSql.append("  AND Version= :Version-1");
		}

		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(uploadManualAdvise);
		recordCount = this.jdbcTemplate.update(updateSql.toString(), beanParameters);

		if (recordCount == 0) {
			throw new ConcurrencyException();
		}

		logger.debug("Leaving ");
	}

	@Override
	public void deleteByUploadId(long uploadId, String type) {
		logger.debug("Entering");

		UploadManualAdvise uploadManualAdvise = new UploadManualAdvise();
		uploadManualAdvise.setUploadId(uploadId);
		StringBuilder deleteSql = new StringBuilder("Delete From AdviseUploads");
		deleteSql.append(StringUtils.trimToEmpty(type));
		deleteSql.append(" Where UploadId = :UploadId");
		logger.debug("deleteSql: " + deleteSql.toString());

		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(uploadManualAdvise);

		try {
			this.jdbcTemplate.update(deleteSql.toString(), beanParameters);
		} catch (DataAccessException e) {
			logger.error("Exception: ", e);
		}

		logger.debug("Leaving");
	}

	@Override
	public boolean getAdviseUploadsByFinReference(String finReference, long uploadId, String type) {
		logger.debug(Literal.ENTERING);

		StringBuilder sql = null;
		MapSqlParameterSource source = null;

		sql = new StringBuilder();
		sql.append(" Select Count(FinReference) from AdviseUploads");
		sql.append(type);
		sql.append(" Where FinReference = :FinReference And Status = :Status");
		if (uploadId > 0) {
			sql.append(" And UploadId != :UploadId");
		}
		logger.trace(Literal.SQL + sql.toString());

		source = new MapSqlParameterSource();
		source.addValue("FinReference", finReference);
		source.addValue("Status", UploadConstants.UPLOAD_STATUS_SUCCESS);

		try {
			if (this.jdbcTemplate.queryForObject(sql.toString(), source, Integer.class) > 0) {
				return true;
			}
		} catch (Exception e) {
			logger.error(e);
		} finally {
			source = null;
			sql = null;
			logger.debug("Leaving");
		}
		return false;
	}

	public List<UploadManualAdvise> getManualAdviseListByUploadId(long uploadId, String type) {
		logger.debug("Entering");
		UploadManualAdvise uploadManualAdvise = new UploadManualAdvise();
		uploadManualAdvise.setUploadId(uploadId);

		StringBuilder selectSql = new StringBuilder("Select AdviseId, UploadId, FinReference, AdviseType,");
		selectSql.append(" FeeTypeCode, ValueDate, AdviseAmount, Remarks, Status, Reason, RejectStage,");
		if (type.contains("View")) {
			selectSql.append("FeeTypeId, ");
		}
		selectSql.append(" Version, LastMntBy, LastMntOn, RecordStatus,");
		selectSql.append(" RoleCode, NextRoleCode, TaskId, NextTaskId, RecordType, WorkflowId");

		selectSql.append(" FROM AdviseUploads");
		selectSql.append(StringUtils.trimToEmpty(type));
		selectSql.append(" Where UploadId = :UploadId");

		logger.debug("selectListSql: " + selectSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(uploadManualAdvise);
		RowMapper<UploadManualAdvise> typeRowMapper = BeanPropertyRowMapper.newInstance(UploadManualAdvise.class);

		logger.debug("Leaving");
		return this.jdbcTemplate.query(selectSql.toString(), beanParameters, typeRowMapper);
	}
}