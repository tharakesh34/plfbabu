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
 * * FileName : LegalDocumentDAOImpl.java * * Author : PENNANT TECHONOLOGIES * * Creation Date : 18-06-2018 * * Modified
 * Date : 18-06-2018 * * Description : * *
 ********************************************************************************************
 * Date Author Version Comments *
 ********************************************************************************************
 * 18-06-2018 PENNANT 0.1 * * * * * * * * *
 ********************************************************************************************
 */
package com.pennant.backend.dao.legal.impl;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.PreparedStatementSetter;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;

import com.pennant.backend.dao.legal.LegalDocumentDAO;
import com.pennant.backend.model.legal.LegalDocument;
import com.pennanttech.pennapps.core.ConcurrencyException;
import com.pennanttech.pennapps.core.DependencyFoundException;
import com.pennanttech.pennapps.core.jdbc.JdbcUtil;
import com.pennanttech.pennapps.core.jdbc.SequenceDao;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pff.core.TableType;

/**
 * Data access layer implementation for <code>LegalDocument</code> with set of CRUD operations.
 */
public class LegalDocumentDAOImpl extends SequenceDao<LegalDocument> implements LegalDocumentDAO {
	private static Logger logger = LogManager.getLogger(LegalDocumentDAOImpl.class);

	public LegalDocumentDAOImpl() {
		super();
	}

	@Override
	public LegalDocument getLegalDocument(long legalId, long legalDocumentId, String type) {
		logger.debug(Literal.ENTERING);

		// Prepare the SQL.
		StringBuilder sql = new StringBuilder("SELECT ");
		sql.append(" legalId, legalDocumentId, documentDate, documentDetail, documentNo, surveyNo, ");
		sql.append(
				" documentType, documentCategory, scheduleType, documentName, documentTypeVerify, documentRemarks, documentReference, ");
		sql.append(" documentTypeApprove, documentAccepted, uploadDocumentType,");

		sql.append(" documentHolderProperty, documentPropertyAddress, documentBriefTracking, documentMortgage, ");
		sql.append(
				" Version, LastMntOn, LastMntBy,RecordStatus, RoleCode, NextRoleCode, TaskId, NextTaskId, RecordType, WorkflowId");
		sql.append(" From LegalDocuments");
		sql.append(type);
		sql.append(" Where legalDocumentId = :legalDocumentId And LegalId = :LegalId");

		// Execute the SQL, binding the arguments.
		logger.trace(Literal.SQL + sql.toString());

		LegalDocument legalDocument = new LegalDocument();
		legalDocument.setLegalDocumentId(legalDocumentId);
		legalDocument.setLegalId(legalId);

		SqlParameterSource paramSource = new BeanPropertySqlParameterSource(legalDocument);
		RowMapper<LegalDocument> rowMapper = BeanPropertyRowMapper.newInstance(LegalDocument.class);

		try {
			legalDocument = jdbcTemplate.queryForObject(sql.toString(), paramSource, rowMapper);
		} catch (EmptyResultDataAccessException e) {
			legalDocument = null;
		}

		logger.debug(Literal.LEAVING);
		return legalDocument;
	}

	@Override
	public List<LegalDocument> getLegalDocumenttDetailsList(long legalId, String type) {
		logger.debug(Literal.ENTERING);

		StringBuilder sql = new StringBuilder("Select");
		sql.append(" LegalId, LegalDocumentId, DocumentDate, DocumentDetail, DocumentNo, SurveyNo");
		sql.append(", DocumentType, DocumentCategory, ScheduleType, DocumentName, DocumentTypeVerify");
		sql.append(", DocumentRemarks, DocumentReference, DocumentTypeApprove, DocumentAccepted, UploadDocumentType");
		sql.append(", DocumentHolderProperty, DocumentPropertyAddress, DocumentBriefTracking, DocumentMortgage");
		sql.append(", Version, LastMntOn, LastMntBy, RecordStatus, RoleCode, NextRoleCode, TaskId");
		sql.append(", NextTaskId, RecordType, WorkflowId");
		sql.append(" from LegalDocuments");
		sql.append(StringUtils.trimToEmpty(type));
		sql.append(" Where LegalId = ?");

		logger.trace(Literal.SQL + sql.toString());

		return this.jdbcOperations.query(sql.toString(), new PreparedStatementSetter() {
			@Override
			public void setValues(PreparedStatement ps) throws SQLException {
				int index = 1;
				ps.setLong(index++, legalId);
			}
		}, new RowMapper<LegalDocument>() {
			@Override
			public LegalDocument mapRow(ResultSet rs, int rowNum) throws SQLException {
				LegalDocument ld = new LegalDocument();

				ld.setLegalId(rs.getLong("LegalId"));
				ld.setLegalDocumentId(rs.getLong("LegalDocumentId"));
				ld.setDocumentDate(rs.getTimestamp("DocumentDate"));
				ld.setDocumentDetail(rs.getString("DocumentDetail"));
				ld.setDocumentNo(rs.getString("DocumentNo"));
				ld.setSurveyNo(rs.getString("SurveyNo"));
				ld.setDocumentType(rs.getString("DocumentType"));
				ld.setDocumentCategory(rs.getString("DocumentCategory"));
				ld.setScheduleType(rs.getString("ScheduleType"));
				ld.setDocumentName(rs.getString("DocumentName"));
				ld.setDocumentTypeVerify(rs.getString("DocumentTypeVerify"));
				ld.setDocumentRemarks(rs.getString("DocumentRemarks"));
				ld.setDocumentReference(JdbcUtil.getLong(rs.getObject("DocumentReference")));
				ld.setDocumentTypeApprove(rs.getString("DocumentTypeApprove"));
				ld.setDocumentAccepted(rs.getString("DocumentAccepted"));
				ld.setUploadDocumentType(rs.getString("UploadDocumentType"));
				ld.setDocumentHolderProperty(rs.getString("DocumentHolderProperty"));
				ld.setDocumentPropertyAddress(rs.getString("DocumentPropertyAddress"));
				ld.setDocumentBriefTracking(rs.getString("DocumentBriefTracking"));
				ld.setDocumentMortgage(rs.getBoolean("DocumentMortgage"));
				ld.setVersion(rs.getInt("Version"));
				ld.setLastMntOn(rs.getTimestamp("LastMntOn"));
				ld.setLastMntBy(rs.getLong("LastMntBy"));
				ld.setRecordStatus(rs.getString("RecordStatus"));
				ld.setRoleCode(rs.getString("RoleCode"));
				ld.setNextRoleCode(rs.getString("NextRoleCode"));
				ld.setTaskId(rs.getString("TaskId"));
				ld.setNextTaskId(rs.getString("NextTaskId"));
				ld.setRecordType(rs.getString("RecordType"));
				ld.setWorkflowId(rs.getLong("WorkflowId"));

				return ld;
			}
		});
	}

	@Override
	public String save(LegalDocument legalDocument, TableType tableType) {
		logger.debug(Literal.ENTERING);

		// Prepare the SQL.
		StringBuilder sql = new StringBuilder(" insert into LegalDocuments");
		sql.append(tableType.getSuffix());
		sql.append("(legalDocumentId, legalId, documentDate, documentDetail, documentNo, surveyNo, ");
		sql.append(
				"documentType, documentCategory, scheduleType, documentName, documentTypeVerify, documentRemarks, documentReference, ");
		sql.append(" documentTypeApprove, documentAccepted, uploadDocumentType,");
		sql.append(" documentHolderProperty, documentPropertyAddress, documentBriefTracking, documentMortgage, ");
		sql.append(
				" Version , LastMntBy, LastMntOn, RecordStatus, RoleCode, NextRoleCode, TaskId, NextTaskId, RecordType, WorkflowId)");
		sql.append(" values(");
		sql.append(" :legalDocumentId, :legalId, :documentDate, :documentDetail, :documentNo, :surveyNo, ");
		sql.append(
				" :documentType, :documentCategory, :scheduleType, :documentName, :documentTypeVerify, :documentRemarks, :documentReference, ");
		sql.append(" :documentTypeApprove, :documentAccepted, :uploadDocumentType,");
		sql.append(" :documentHolderProperty, :documentPropertyAddress, :documentBriefTracking, :documentMortgage, ");
		sql.append(
				" :Version , :LastMntBy, :LastMntOn, :RecordStatus, :RoleCode, :NextRoleCode, :TaskId, :NextTaskId, :RecordType, :WorkflowId)");

		if (legalDocument.getLegalDocumentId() == Long.MIN_VALUE) {
			legalDocument.setLegalDocumentId(getNextValue("SeqLegalDocuments"));
			logger.debug("get NextValue:" + legalDocument.getLegalDocumentId());
		}

		// Execute the SQL, binding the arguments.
		logger.trace(Literal.SQL + sql.toString());
		SqlParameterSource paramSource = new BeanPropertySqlParameterSource(legalDocument);

		try {
			jdbcTemplate.update(sql.toString(), paramSource);
		} catch (DuplicateKeyException e) {
			throw new ConcurrencyException(e);
		}

		logger.debug(Literal.LEAVING);
		return String.valueOf(legalDocument.getLegalDocumentId());
	}

	@Override
	public void update(LegalDocument legalDocument, TableType tableType) {
		logger.debug(Literal.ENTERING);

		// Prepare the SQL.
		StringBuilder sql = new StringBuilder("update LegalDocuments");
		sql.append(tableType.getSuffix());
		sql.append("  set legalId = :legalId, documentDate = :documentDate, documentDetail = :documentDetail, ");
		sql.append(
				" documentNo = :documentNo, surveyNo = :surveyNo, documentType = :documentType, documentName = :documentName,");
		sql.append(
				" documentCategory = :documentCategory, scheduleType = :scheduleType, documentTypeVerify = :documentTypeVerify, ");
		sql.append(
				" documentRemarks = :documentRemarks, documentReference = :documentReference, documentTypeApprove = :documentTypeApprove, ");
		sql.append(" documentAccepted = :documentAccepted, uploadDocumentType = :uploadDocumentType, ");

		sql.append(
				" documentHolderProperty = :documentHolderProperty, documentPropertyAddress= :documentPropertyAddress, "
						+ " documentBriefTracking = :documentBriefTracking, documentMortgage = :documentMortgage, ");

		sql.append(" LastMntOn = :LastMntOn, RecordStatus = :RecordStatus, RoleCode = :RoleCode,");
		sql.append(" NextRoleCode = :NextRoleCode, TaskId = :TaskId, NextTaskId = :NextTaskId,");
		sql.append(" RecordType = :RecordType, WorkflowId = :WorkflowId");
		sql.append(" where legalDocumentId = :legalDocumentId AND legalId = :legalId");

		// Execute the SQL, binding the arguments.
		logger.trace(Literal.SQL + sql.toString());

		SqlParameterSource paramSource = new BeanPropertySqlParameterSource(legalDocument);
		int recordCount = jdbcTemplate.update(sql.toString(), paramSource);

		// Check for the concurrency failure.
		if (recordCount == 0) {
			throw new ConcurrencyException();
		}

		logger.debug(Literal.LEAVING);
	}

	@Override
	public void delete(LegalDocument legalDocument, TableType tableType) {
		logger.debug(Literal.ENTERING);

		// Prepare the SQL.
		StringBuilder sql = new StringBuilder("delete from LegalDocuments");
		sql.append(tableType.getSuffix());
		sql.append(" where legalDocumentId = :legalDocumentId AND legalId=:legalId");

		// Execute the SQL, binding the arguments.
		logger.trace(Literal.SQL + sql.toString());
		SqlParameterSource paramSource = new BeanPropertySqlParameterSource(legalDocument);
		int recordCount = 0;

		try {
			recordCount = jdbcTemplate.update(sql.toString(), paramSource);
		} catch (DataAccessException e) {
			throw new DependencyFoundException(e);
		}

		// Check for the concurrency failure.
		if (recordCount == 0) {
			throw new ConcurrencyException();
		}

		logger.debug(Literal.LEAVING);
	}

	@Override
	public void deleteList(LegalDocument documentDetail, String tableType) {
		logger.debug(Literal.ENTERING);

		StringBuilder deleteSql = new StringBuilder("Delete From LegalDocuments");
		deleteSql.append(StringUtils.trimToEmpty(tableType));
		deleteSql.append(" Where legalId = :legalId");
		logger.debug("deleteSql: " + deleteSql.toString());

		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(documentDetail);
		this.jdbcTemplate.update(deleteSql.toString(), beanParameters);

		logger.debug(Literal.LEAVING);
	}

}
