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
 * * FileName : AssignmentUploadDAOImpl.java * * Author : PENNANT TECHONOLOGIES * * Creation Date : 19-11-2018 * *
 * Modified Date : 19-11-2018 * * Description : * *
 ********************************************************************************************
 * Date Author Version Comments *
 ********************************************************************************************
 * 19-11-2018 Pennant 0.1 * * * * * * * * *
 ********************************************************************************************
 */

package com.pennant.backend.dao.assignmentupload.impl;

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

import com.pennant.backend.dao.assignmentupload.AssignmentUploadDAO;
import com.pennant.backend.model.assignmentupload.AssignmentUpload;
import com.pennant.backend.util.UploadConstants;
import com.pennanttech.pennapps.core.ConcurrencyException;
import com.pennanttech.pennapps.core.DependencyFoundException;
import com.pennanttech.pennapps.core.jdbc.SequenceDao;
import com.pennanttech.pennapps.core.resource.Literal;

/**
 * DAO methods implementation for the <b>FinanceType model</b> class.<br>
 * 
 */
public class AssignmentUploadDAOImpl extends SequenceDao<AssignmentUpload> implements AssignmentUploadDAO {
	private static Logger logger = LogManager.getLogger(AssignmentUploadDAOImpl.class);

	public AssignmentUploadDAOImpl() {
		super();
	}

	/**
	 * Fetch the Record Finance Types details by key field
	 * 
	 * @param id   (String)
	 * @param type (String) ""/_Temp/_View
	 * @return FinanceType
	 */
	@Override
	public List<AssignmentUpload> getAssignmentUploadsByUploadId(long uploadId, String type) {
		logger.debug("Entering");
		AssignmentUpload assignmentUpload = new AssignmentUpload();
		assignmentUpload.setUploadId(uploadId);

		StringBuilder selectSql = new StringBuilder(
				"Select Id, UploadId, FinReference, AssignmentId, AssignmentDate, EffectiveDate, Status, RejectReason,");
		if (type.contains("View")) {
			selectSql.append("");
		}
		selectSql.append(" Version, LastMntBy, LastMntOn, RecordStatus,");
		selectSql.append(" RoleCode, NextRoleCode, TaskId, NextTaskId, RecordType, WorkflowId");

		selectSql.append(" FROM AssignmentUploads");
		selectSql.append(StringUtils.trimToEmpty(type));
		selectSql.append(" Where UploadId = :UploadId");

		logger.debug("selectListSql: " + selectSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(assignmentUpload);
		RowMapper<AssignmentUpload> typeRowMapper = BeanPropertyRowMapper.newInstance(AssignmentUpload.class);

		logger.debug("Leaving");
		return this.jdbcTemplate.query(selectSql.toString(), beanParameters, typeRowMapper);
	}

	/**
	 * This method insert new Records into RMTFinanceTypes or RMTFinanceTypes_Temp.
	 * 
	 * save Finance Types
	 * 
	 * @param Finance Types (financeType)
	 * @param type    (String) ""/_Temp/_View
	 * @return void
	 * @throws DataAccessException
	 * 
	 */
	@Override
	public String save(AssignmentUpload assignmentUpload, String type) {
		logger.debug("Entering ");

		if (assignmentUpload.getId() == Long.MIN_VALUE) {
			assignmentUpload.setId(getNextValue("SeqAssignmentUploads"));
			logger.debug("get NextID:" + assignmentUpload.getId());
		}

		StringBuilder insertSql = new StringBuilder("Insert Into AssignmentUploads");
		insertSql.append(StringUtils.trimToEmpty(type));
		insertSql.append(
				" (Id, UploadId, FinReference, AssignmentId, AssignmentDate, EffectiveDate, Status, RejectReason,");
		insertSql.append(
				" Version, LastMntBy, LastMntOn, RecordStatus, RoleCode, NextRoleCode, TaskId, NextTaskId, RecordType, WorkflowId)");
		insertSql.append(
				" Values(:Id, :UploadId, :FinReference, :AssignmentId, :AssignmentDate, :EffectiveDate, :Status, :RejectReason,");
		insertSql.append(
				" :Version, :LastMntBy, :LastMntOn, :RecordStatus, :RoleCode, :NextRoleCode, :TaskId, :NextTaskId, :RecordType, :WorkflowId)");

		logger.debug("insertSql: " + insertSql.toString());

		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(assignmentUpload);
		this.jdbcTemplate.update(insertSql.toString(), beanParameters);

		logger.debug("Leaving ");

		return assignmentUpload.getFinReference();
	}

	/**
	 * This method updates the Record RMTFinanceTypes or RMTFinanceTypes_Temp. if Record not updated then throws
	 * DataAccessException with error 41004. update Finance Types by key FinType and Version
	 * 
	 * @param Finance Types (financeType)
	 * @param type    (String) ""/_Temp/_View
	 * @return void
	 * @throws DataAccessException
	 * 
	 */

	@Override
	public void update(AssignmentUpload assignmentUpload, String type) {
		int recordCount = 0;
		logger.debug("Entering ");

		StringBuilder updateSql = new StringBuilder("Update AssignmentUploads");
		updateSql.append(StringUtils.trimToEmpty(type));
		updateSql.append(
				" Set FinReference = :FinReference, AssignmentId = :AssignmentId, AssignmentDate = :AssignmentDate,");
		updateSql.append(" EffectiveDate = :EffectiveDate, Status = :Status, RejectReason = :RejectReason,");
		updateSql.append(" Version = :Version , LastMntBy = :LastMntBy, LastMntOn = :LastMntOn,");
		updateSql.append(" RecordStatus= :RecordStatus, RoleCode = :RoleCode, NextRoleCode = :NextRoleCode,");
		updateSql.append(" TaskId = :TaskId,  NextTaskId = :NextTaskId,");
		updateSql.append(" RecordType = :RecordType, WorkflowId = :WorkflowId");
		updateSql.append(" Where UploadId = :UploadId And Id = :Id");

		if (!type.endsWith("_Temp")) {
			updateSql.append("  AND Version= :Version-1");
		}

		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(assignmentUpload);
		recordCount = this.jdbcTemplate.update(updateSql.toString(), beanParameters);

		if (recordCount <= 0) {
			throw new ConcurrencyException();
		}

		logger.debug("Leaving ");
	}

	/**
	 * This method initialize the Record.
	 * 
	 * @param FinanceType (financeType)
	 * @return FinanceType
	 */

	@Override
	public void deleteByUploadId(long uploadId, String type) {
		logger.debug(Literal.ENTERING);

		AssignmentUpload assignmentUpload = new AssignmentUpload();
		assignmentUpload.setUploadId(uploadId);
		StringBuilder deleteSql = new StringBuilder("Delete From AssignmentUploads");
		deleteSql.append(StringUtils.trimToEmpty(type));
		deleteSql.append(" Where UploadId = :UploadId");
		logger.debug(Literal.SQL + deleteSql.toString());

		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(assignmentUpload);

		try {
			this.jdbcTemplate.update(deleteSql.toString(), beanParameters);
		} catch (DataAccessException e) {
			throw new DependencyFoundException(e);
		}

		logger.debug(Literal.LEAVING);
	}

	@Override
	public boolean getAssignmentUploadsByFinReference(String finReference, long uploadId, String type) {
		logger.debug(Literal.ENTERING);

		StringBuilder sql = new StringBuilder();
		sql.append(" Select Count(FinReference) from AssignmentUploads");
		sql.append(type);
		sql.append(" Where FinReference = :FinReference And Status = :Status");
		if (uploadId > 0) {
			sql.append(" And UploadId != :UploadId");
		}
		logger.trace(Literal.SQL + sql.toString());

		MapSqlParameterSource source = new MapSqlParameterSource();
		source.addValue("FinReference", finReference);
		source.addValue("Status", UploadConstants.REFUND_UPLOAD_STATUS_SUCCESS);

		return this.jdbcTemplate.queryForObject(sql.toString(), source, Integer.class) > 0;
	}
}