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
 * * FileName : DocumentTypeDAOImpl.java * * Author : PENNANT TECHONOLOGIES * * Creation Date : 05-05-2011 * * Modified
 * Date : 05-05-2011 * * Description : * *
 ********************************************************************************************
 * Date Author Version Comments *
 ********************************************************************************************
 * 05-05-2011 Pennant 0.1 * * * * * * * * *
 ********************************************************************************************
 */
package com.pennant.backend.dao.systemmasters.impl;

import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;

import com.pennant.backend.dao.systemmasters.DocumentTypeDAO;
import com.pennant.backend.model.systemmasters.DocumentType;
import com.pennanttech.pennapps.core.ConcurrencyException;
import com.pennanttech.pennapps.core.DependencyFoundException;
import com.pennanttech.pennapps.core.jdbc.BasicDao;
import com.pennanttech.pennapps.core.jdbc.JdbcUtil;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pennapps.core.resource.Message;
import com.pennanttech.pff.core.TableType;
import com.pennanttech.pff.core.util.QueryUtil;

/**
 * DAO methods implementation for the <b>DocumentType model</b> class.<br>
 * 
 */
public class DocumentTypeDAOImpl extends BasicDao<DocumentType> implements DocumentTypeDAO {
	private static Logger logger = LogManager.getLogger(DocumentTypeDAOImpl.class);

	private static String selectAllQuery;

	public DocumentTypeDAOImpl() {
		super();
	}

	/**
	 * Fetch the Record Document Types details by key field
	 * 
	 * @param id   (String)
	 * @param type (String) ""/_Temp/_View
	 * @return DocumentType
	 */
	@Override
	public DocumentType getDocumentTypeById(final String id, String type) {
		logger.debug("Entering");

		DocumentType documentType = new DocumentType();
		documentType.setId(id);
		StringBuilder selectSql = new StringBuilder();

		selectSql.append(
				" SELECT DocTypeCode, DocTypeDesc, DocIsMandatory, DocTypeIsActive, CategoryId, Pdd, Otc, LvReq, RcuReq,");
		selectSql.append(
				" Version, LastMntOn, LastMntBy,RecordStatus, RoleCode, NextRoleCode, TaskId, NextTaskId, RecordType, WorkflowId,");
		selectSql.append(
				" DocExpDateIsMand, DocIssueDateMand, DocIdNumMand, DocIssuedAuthorityMand, DocIsPdfExtRequired, DocIsPasswordProtected, PdfMappingRef,docExternalRef");
		if (type.contains("View")) {
			selectSql.append(" ,categoryCode,categoryDesc ");
		}
		selectSql.append(" FROM  BMTDocumentTypes");
		selectSql.append(StringUtils.trimToEmpty(type));
		selectSql.append(" Where DocTypeCode =:DocTypeCode");

		logger.debug("selectSql: " + selectSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(documentType);
		RowMapper<DocumentType> typeRowMapper = BeanPropertyRowMapper.newInstance(DocumentType.class);

		try {
			return this.jdbcTemplate.queryForObject(selectSql.toString(), beanParameters, typeRowMapper);
		} catch (EmptyResultDataAccessException e) {
			logger.warn(Message.NO_RECORD_FOUND);
			return null;
		}
	}

	@Override
	public boolean isDuplicateKey(String docTypeCode, TableType tableType) {
		logger.debug(Literal.ENTERING);

		// Prepare the SQL.
		String sql;
		String whereClause = "DocTypeCode = :docTypeCode";

		switch (tableType) {
		case MAIN_TAB:
			sql = QueryUtil.getCountQuery("BMTDocumentTypes", whereClause);
			break;
		case TEMP_TAB:
			sql = QueryUtil.getCountQuery("BMTDocumentTypes_Temp", whereClause);
			break;
		default:
			sql = QueryUtil.getCountQuery(new String[] { "BMTDocumentTypes_Temp", "BMTDocumentTypes" }, whereClause);
			break;
		}

		// Execute the SQL, binding the arguments.
		logger.trace(Literal.SQL + sql);
		MapSqlParameterSource paramSource = new MapSqlParameterSource();
		paramSource.addValue("docTypeCode", docTypeCode);

		Integer count = jdbcTemplate.queryForObject(sql, paramSource, Integer.class);

		boolean exists = false;
		if (count > 0) {
			exists = true;
		}

		logger.debug(Literal.LEAVING);
		return exists;
	}

	@Override
	public String save(DocumentType documentType, TableType tableType) {
		logger.debug(Literal.ENTERING);

		// Prepare the SQL.
		StringBuilder sql = new StringBuilder("insert into BMTDocumentTypes");
		sql.append(tableType.getSuffix());
		sql.append(" (DocTypeCode, DocTypeDesc, DocIsMandatory, DocTypeIsActive, CategoryId, Pdd, Otc, LvReq, RcuReq,");
		sql.append(" Version , LastMntBy, LastMntOn, RecordStatus, RoleCode, NextRoleCode, TaskId, NextTaskId,");
		sql.append(
				" RecordType, WorkflowId, DocExpDateIsMand, DocIssueDateMand, DocIdNumMand, DocIssuedAuthorityMand, DocIsPdfExtRequired, DocIsPasswordProtected, PdfMappingRef,docExternalRef)");
		sql.append(
				" values(:DocTypeCode, :DocTypeDesc, :DocIsMandatory, :DocTypeIsActive, :CategoryId, :Pdd, :Otc, :LvReq, :RcuReq,");
		sql.append(
				" :Version , :LastMntBy, :LastMntOn, :RecordStatus, :RoleCode, :NextRoleCode, :TaskId, :NextTaskId, ");
		sql.append(
				" :RecordType, :WorkflowId, :DocExpDateIsMand, :DocIssueDateMand, :DocIdNumMand, :DocIssuedAuthorityMand, :DocIsPdfExtRequired, :DocIsPasswordProtected, :PdfMappingRef,:docExternalRef)");

		// Execute the SQL, binding the arguments.
		logger.trace(Literal.SQL + sql.toString());
		SqlParameterSource paramSource = new BeanPropertySqlParameterSource(documentType);

		try {
			jdbcTemplate.update(sql.toString(), paramSource);
		} catch (DuplicateKeyException e) {
			throw new ConcurrencyException(e);
		}

		logger.debug(Literal.LEAVING);
		return documentType.getId();
	}

	@Override
	public void update(DocumentType documentType, TableType tableType) {
		logger.debug(Literal.ENTERING);

		// Prepare the SQL, ensure primary key will not be updated.
		StringBuilder sql = new StringBuilder("update BMTDocumentTypes");
		sql.append(tableType.getSuffix());
		sql.append(" set DocTypeDesc = :DocTypeDesc,");
		sql.append(
				" DocIsMandatory = :DocIsMandatory, DocTypeIsActive = :DocTypeIsActive, CategoryId = :CategoryId, Pdd = :Pdd, ");
		sql.append(" Otc = :Otc, LvReq = :LvReq, RcuReq = :RcuReq, Version = :Version , LastMntBy = :LastMntBy, ");
		sql.append(
				"  LastMntOn = :LastMntOn, RecordStatus= :RecordStatus, RoleCode = :RoleCode,NextRoleCode = :NextRoleCode, TaskId = :TaskId,");
		sql.append(
				" NextTaskId = :NextTaskId, RecordType = :RecordType, WorkflowId = :WorkflowId, DocExpDateIsMand = :DocExpDateIsMand,");
		sql.append(
				" DocIssueDateMand= :DocIssueDateMand, DocIdNumMand = :DocIdNumMand, DocIssuedAuthorityMand = :DocIssuedAuthorityMand,");
		sql.append(
				" DocIsPdfExtRequired = :DocIsPdfExtRequired, DocIsPasswordProtected = :DocIsPasswordProtected, PdfMappingRef = :PdfMappingRef ,docExternalRef =:docExternalRef");
		sql.append(" where DocTypeCode =:DocTypeCode ");
		sql.append(QueryUtil.getConcurrencyCondition(tableType));

		// Execute the SQL, binding the arguments.
		logger.trace(Literal.SQL + sql.toString());

		SqlParameterSource paramSource = new BeanPropertySqlParameterSource(documentType);
		int recordCount = jdbcTemplate.update(sql.toString(), paramSource);

		// Check for the concurrency failure.
		if (recordCount == 0) {
			throw new ConcurrencyException();
		}

		logger.debug("Leaving");
	}

	public void delete(DocumentType documentType, TableType tableType) {
		logger.debug(Literal.ENTERING);

		// Prepare the SQL.
		StringBuilder sql = new StringBuilder("delete from BMTDocumentTypes");
		sql.append(tableType.getSuffix());
		sql.append(" where DocTypeCode =:DocTypeCode");
		sql.append(QueryUtil.getConcurrencyCondition(tableType));

		// Execute the SQL, binding the arguments.
		logger.trace(Literal.SQL + sql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(documentType);
		int recordCount = 0;

		try {
			recordCount = jdbcTemplate.update(sql.toString(), beanParameters);
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
	public List<DocumentType> getApprovedPdfExternalList(String type) {
		StringBuilder selectSql = new StringBuilder();

		selectSql.append(
				"SELECT DocTypeCode, DocTypeDesc, DocIsMandatory, DocTypeIsActive, CategoryId, Pdd, Otc, LvReq, RcuReq,");
		selectSql.append(
				" Version, LastMntOn, LastMntBy,RecordStatus, RoleCode, NextRoleCode, TaskId, NextTaskId, RecordType, WorkflowId,");
		selectSql.append(
				" DocExpDateIsMand, DocIssueDateMand, DocIdNumMand, DocIssuedAuthorityMand, DocIsPdfExtRequired, DocIsPasswordProtected, PdfMappingRef,docExternalRef");
		selectSql.append(" FROM  BMTDocumentTypes");
		selectSql.append(type);
		selectSql.append(" Where DocIsPdfExtRequired =1");

		logger.debug("selectSql: " + selectSql.toString());
		DocumentType documentType = new DocumentType();
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(documentType);
		RowMapper<DocumentType> typeRowMapper = BeanPropertyRowMapper.newInstance(DocumentType.class);
		logger.debug("Leaving");
		return this.jdbcTemplate.query(selectSql.toString(), beanParameters, typeRowMapper);
	}

	@Override
	public List<DocumentType> getDocumentTypes() {
		logger.trace(Literal.SQL + getSelectAllQuery());
		return this.jdbcTemplate.query(selectAllQuery, new MapSqlParameterSource(), new RowMapper<DocumentType>() {

			@Override
			public DocumentType mapRow(ResultSet rs, int rowNum) throws SQLException {
				DocumentType dt = new DocumentType();

				dt.setDocTypeCode(rs.getString("DocTypeCode"));
				dt.setDocTypeDesc(rs.getString("DocTypeDesc"));
				dt.setDocIsMandatory(rs.getBoolean("DocIsMandatory"));
				dt.setDocTypeIsActive(rs.getBoolean("DocTypeIsActive"));
				dt.setCategoryId(JdbcUtil.getLong(rs.getObject("CategoryId")));
				dt.setPdd(rs.getBoolean("Pdd"));
				dt.setOtc(rs.getBoolean("Otc"));
				dt.setLvReq(rs.getBoolean("LvReq"));
				dt.setRcuReq(rs.getBoolean("RcuReq"));
				dt.setVersion(rs.getInt("Version"));
				Date LastMntOn = rs.getDate("LastMntOn");
				if (LastMntOn != null) {
					dt.setLastMntOn(new Timestamp(LastMntOn.getTime()));
				}

				dt.setLastMntBy(rs.getLong("LastMntBy"));
				dt.setRecordStatus(rs.getString("RecordStatus"));
				dt.setRoleCode(rs.getString("RoleCode"));
				dt.setNextRoleCode(rs.getString("NextRoleCode"));
				dt.setTaskId(rs.getString("TaskId"));
				dt.setNextTaskId(rs.getString("NextTaskId"));
				dt.setRecordType(rs.getString("RecordType"));
				dt.setWorkflowId(rs.getLong("WorkflowId"));
				dt.setDocExpDateIsMand(rs.getBoolean("DocExpDateIsMand"));
				dt.setDocIssueDateMand(rs.getBoolean("DocIssueDateMand"));
				dt.setDocIdNumMand(rs.getBoolean("DocIdNumMand"));
				dt.setDocIssuedAuthorityMand(rs.getBoolean("DocIssuedAuthorityMand"));
				dt.setDocIsPdfExtRequired(rs.getBoolean("DocIsPdfExtRequired"));
				dt.setDocIsPasswordProtected(rs.getBoolean("DocIsPasswordProtected"));
				dt.setPdfMappingRef(JdbcUtil.getLong(rs.getObject("PdfMappingRef")));
				dt.setDocExternalRef(rs.getString("docExternalRef"));

				return dt;
			}
		});
	}

	private String getSelectAllQuery() {
		if (selectAllQuery != null) {

			return selectAllQuery;
		}

		StringBuilder sql = new StringBuilder();

		sql.append("Select DocTypeCode, DocTypeDesc, DocIsMandatory, DocTypeIsActive, CategoryId");
		sql.append(", Pdd, Otc, LvReq, RcuReq, DocExpDateIsMand, DocIssueDateMand, DocIdNumMand");
		sql.append(
				", DocIssuedAuthorityMand, DocIsPdfExtRequired, DocIsPasswordProtected, PdfMappingRef, DocExternalRef");
		sql.append(",  Version, LastMntOn, LastMntBy,RecordStatus, RoleCode");
		sql.append(", NextRoleCode, TaskId, NextTaskId, RecordType, WorkflowId");
		sql.append(" FROM  BMTDocumentTypes");

		selectAllQuery = sql.toString();

		return selectAllQuery;

	}

	public String getDocCategoryByDocType(String code) {
		String sql = "Select CategoryCode from BMTDocumentTypes_AView Where DocTypeCode = ?";

		logger.debug(Literal.SQL + sql);

		try {
			return jdbcOperations.queryForObject(sql, String.class, code);
		} catch (EmptyResultDataAccessException e) {
			logger.debug(Message.NO_RECORD_FOUND);
		}

		return null;
	}
}