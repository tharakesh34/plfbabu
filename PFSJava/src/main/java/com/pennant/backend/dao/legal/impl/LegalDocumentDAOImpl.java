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
 * FileName    		:  LegalDocumentDAOImpl.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  18-06-2018    														*
 *                                                                  						*
 * Modified Date    :  18-06-2018    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 18-06-2018       PENNANT	                 0.1                                            * 
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
package com.pennant.backend.dao.legal.impl;

import java.util.List;

import javax.sql.DataSource;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.core.simple.ParameterizedBeanPropertyRowMapper;

import com.pennant.backend.dao.impl.BasisNextidDaoImpl;
import com.pennant.backend.dao.legal.LegalDocumentDAO;
import com.pennant.backend.model.legal.LegalDocument;
import com.pennanttech.pennapps.core.ConcurrencyException;
import com.pennanttech.pennapps.core.DependencyFoundException;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pff.core.TableType;

/**
 * Data access layer implementation for <code>LegalDocument</code> with set of
 * CRUD operations.
 */
public class LegalDocumentDAOImpl extends BasisNextidDaoImpl<LegalDocument> implements LegalDocumentDAO {
	private static Logger logger = Logger.getLogger(LegalDocumentDAOImpl.class);

	private NamedParameterJdbcTemplate namedParameterJdbcTemplate;

	public LegalDocumentDAOImpl() {
		super();
	}

	@Override
	public LegalDocument getLegalDocument(long legalDocumentId, long legalId,String type) {
		logger.debug(Literal.ENTERING);

		// Prepare the SQL.
		StringBuilder sql = new StringBuilder("SELECT ");
		sql.append(" legalId, legalDocumentId, documentDate, documentDetail, documentNo, surveyNo, ");
		sql.append(" documentType, documentCategory, scheduleType, documentName, documentTypeVerify, documentRemarks, documentReference, ");
		sql.append(" documentTypeApprove, documentAccepted, uploadDocumentType,");
		sql.append(" Version, LastMntOn, LastMntBy,RecordStatus, RoleCode, NextRoleCode, TaskId, NextTaskId, RecordType, WorkflowId");
		sql.append(" From LegalDocuments");
		sql.append(type);
		sql.append(" Where legalDocumentId = :legalDocumentId And LegalId = :LegalId");

		// Execute the SQL, binding the arguments.
		logger.trace(Literal.SQL + sql.toString());

		LegalDocument legalDocument = new LegalDocument();
		legalDocument.setLegalDocumentId(legalDocumentId);

		SqlParameterSource paramSource = new BeanPropertySqlParameterSource(legalDocument);
		RowMapper<LegalDocument> rowMapper = ParameterizedBeanPropertyRowMapper.newInstance(LegalDocument.class);

		try {
			legalDocument = namedParameterJdbcTemplate.queryForObject(sql.toString(), paramSource, rowMapper);
		} catch (EmptyResultDataAccessException e) {
			legalDocument = null;
		}

		logger.debug(Literal.LEAVING);
		return legalDocument;
	}

	@Override
	public List<LegalDocument> getLegalDocumenttDetailsList(long legalId, String type) {
		logger.debug(Literal.ENTERING);

		// Prepare the SQL.
		StringBuilder sql = new StringBuilder("SELECT ");
		sql.append(" legalId, legalDocumentId, documentDate, documentDetail, documentNo, surveyNo, ");
		sql.append(" documentType, documentCategory, scheduleType, documentName, documentTypeVerify, documentRemarks, documentReference, ");
		sql.append(" documentTypeApprove, documentAccepted, uploadDocumentType, ");
		sql.append(" Version, LastMntOn, LastMntBy,RecordStatus, RoleCode, NextRoleCode, TaskId, NextTaskId, RecordType, WorkflowId");
		sql.append(" From LegalDocuments");
		sql.append(type);
		sql.append(" Where LegalId = :LegalId");

		// Execute the SQL, binding the arguments.
		logger.trace(Literal.SQL + sql.toString());

		MapSqlParameterSource source = new MapSqlParameterSource();
		source.addValue("LegalId", legalId);

		RowMapper<LegalDocument> typeRowMapper = ParameterizedBeanPropertyRowMapper.newInstance(LegalDocument.class);
		try {
			return this.namedParameterJdbcTemplate.query(sql.toString(), source, typeRowMapper);
		} catch (EmptyResultDataAccessException e) {
		} finally {
			source = null;
			sql = null;
		}
		return null;
	}
	
	@Override
	public String save(LegalDocument legalDocument, TableType tableType) {
		logger.debug(Literal.ENTERING);

		// Prepare the SQL.
		StringBuilder sql = new StringBuilder(" insert into LegalDocuments");
		sql.append(tableType.getSuffix());
		sql.append("(legalDocumentId, legalId, documentDate, documentDetail, documentNo, surveyNo, ");
		sql.append("documentType, documentCategory, scheduleType, documentName, documentTypeVerify, documentRemarks, documentReference, ");
		sql.append(" documentTypeApprove, documentAccepted, uploadDocumentType,");
		sql.append(" Version , LastMntBy, LastMntOn, RecordStatus, RoleCode, NextRoleCode, TaskId, NextTaskId, RecordType, WorkflowId)");
		sql.append(" values(");
		sql.append(" :legalDocumentId, :legalId, :documentDate, :documentDetail, :documentNo, :surveyNo, ");
		sql.append( " :documentType, :documentCategory, :scheduleType, :documentName, :documentTypeVerify, :documentRemarks, :documentReference, ");
		sql.append(" :documentTypeApprove, :documentAccepted, :uploadDocumentType,");
		sql.append( " :Version , :LastMntBy, :LastMntOn, :RecordStatus, :RoleCode, :NextRoleCode, :TaskId, :NextTaskId, :RecordType, :WorkflowId)");

		if (legalDocument.getLegalDocumentId() == Long.MIN_VALUE) {
			legalDocument.setLegalDocumentId(getNextidviewDAO().getNextId("SeqLegalDocuments"));
			logger.debug("get NextID:" + legalDocument.getLegalDocumentId());
		}

		// Execute the SQL, binding the arguments.
		logger.trace(Literal.SQL + sql.toString());
		SqlParameterSource paramSource = new BeanPropertySqlParameterSource(legalDocument);

		try {
			namedParameterJdbcTemplate.update(sql.toString(), paramSource);
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
		sql.append(" documentNo = :documentNo, surveyNo = :surveyNo, documentType = :documentType, documentName = :documentName,");
		sql.append(" documentCategory = :documentCategory, scheduleType = :scheduleType, documentTypeVerify = :documentTypeVerify, ");
		sql.append(" documentRemarks = :documentRemarks, documentReference = :documentReference, documentTypeApprove = :documentTypeApprove, ");
		sql.append(" documentAccepted = :documentAccepted, uploadDocumentType = :uploadDocumentType, ");
		sql.append(" LastMntOn = :LastMntOn, RecordStatus = :RecordStatus, RoleCode = :RoleCode,");
		sql.append(" NextRoleCode = :NextRoleCode, TaskId = :TaskId, NextTaskId = :NextTaskId,");
		sql.append(" RecordType = :RecordType, WorkflowId = :WorkflowId");
		sql.append(" where legalDocumentId = :legalDocumentId AND legalId = :legalId");

		// Execute the SQL, binding the arguments.
		logger.trace(Literal.SQL + sql.toString());

		SqlParameterSource paramSource = new BeanPropertySqlParameterSource(legalDocument);
		int recordCount = namedParameterJdbcTemplate.update(sql.toString(), paramSource);

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
			recordCount = namedParameterJdbcTemplate.update(sql.toString(), paramSource);
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
		this.namedParameterJdbcTemplate.update(deleteSql.toString(), beanParameters);
		
		logger.debug(Literal.LEAVING);
	}
	
	/**
	 * Sets a new <code>JDBC Template</code> for the given data source.
	 * 
	 * @param dataSource
	 *            The JDBC data source to access.
	 */
	public void setDataSource(DataSource dataSource) {
		namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
	}

}
