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
 * FileName    		:  DocumentTypeDAOImpl.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  05-05-2011    														*
 *                                                                  						*
 * Modified Date    :  05-05-2011    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 05-05-2011       Pennant	                 0.1                                            * 
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
package com.pennant.backend.dao.systemmasters.impl;

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

import com.pennant.backend.dao.impl.BasisCodeDAO;
import com.pennant.backend.dao.systemmasters.DocumentTypeDAO;
import com.pennant.backend.model.systemmasters.DocumentType;
import com.pennanttech.pennapps.core.ConcurrencyException;
import com.pennanttech.pennapps.core.DependencyFoundException;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pff.core.TableType;
import com.pennanttech.pff.core.util.QueryUtil;

/**
 * DAO methods implementation for the <b>DocumentType model</b> class.<br>
 * 
 */
public class DocumentTypeDAOImpl extends BasisCodeDAO<DocumentType> implements DocumentTypeDAO {

	private static Logger logger = Logger.getLogger(DocumentTypeDAOImpl.class);

	// Spring Named JDBC Template
	private NamedParameterJdbcTemplate namedParameterJdbcTemplate;

	public DocumentTypeDAOImpl() {
		super();
	}

	/**
	 * Fetch the Record Document Types details by key field
	 * 
	 * @param id
	 *            (String)
	 * @param type
	 *            (String) ""/_Temp/_View
	 * @return DocumentType
	 */
	@Override
	public DocumentType getDocumentTypeById(final String id, String type) {
		logger.debug("Entering");
		
		DocumentType documentType = new DocumentType();
		documentType.setId(id);
		StringBuilder selectSql = new StringBuilder();

		selectSql.append("SELECT DocTypeCode, DocTypeDesc, DocIsMandatory, DocTypeIsActive, DocIsCustDoc," );
		selectSql.append(" Version, LastMntOn, LastMntBy,RecordStatus, RoleCode, NextRoleCode, TaskId, NextTaskId, RecordType, WorkflowId," );
		selectSql.append(" DocExpDateIsMand, DocIssueDateMand, DocIdNumMand, DocIssuedAuthorityMand, DocIsPdfExtRequired, DocIsPasswordProtected, PdfMappingRef");
		selectSql.append(" FROM  BMTDocumentTypes");
		selectSql.append(StringUtils.trimToEmpty(type));
		selectSql.append(" Where DocTypeCode =:DocTypeCode") ;
				
		logger.debug("selectSql: " + selectSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(documentType);
		RowMapper<DocumentType> typeRowMapper = ParameterizedBeanPropertyRowMapper.newInstance(DocumentType.class);

		try {
			documentType = this.namedParameterJdbcTemplate.queryForObject(selectSql.toString(), beanParameters, typeRowMapper);
		} catch (EmptyResultDataAccessException e) {
			logger.error("Exception: ", e);
			documentType = null;
		}
		logger.debug("Leaving");
		return documentType;
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

		Integer count = namedParameterJdbcTemplate.queryForObject(sql, paramSource, Integer.class);

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
		sql.append(" (DocTypeCode, DocTypeDesc, DocIsMandatory, DocTypeIsActive, DocIsCustDoc," );
		sql.append(" Version , LastMntBy, LastMntOn, RecordStatus, RoleCode, NextRoleCode, TaskId, NextTaskId," );
		sql.append(" RecordType, WorkflowId, DocExpDateIsMand, DocIssueDateMand, DocIdNumMand, DocIssuedAuthorityMand, DocIsPdfExtRequired, DocIsPasswordProtected, PdfMappingRef)");
		sql.append(" values(:DocTypeCode, :DocTypeDesc, :DocIsMandatory, :DocTypeIsActive, :DocIsCustDoc," );
		sql.append(" :Version , :LastMntBy, :LastMntOn, :RecordStatus, :RoleCode, :NextRoleCode, :TaskId, :NextTaskId, ");
		sql.append(" :RecordType, :WorkflowId, :DocExpDateIsMand, :DocIssueDateMand, :DocIdNumMand, :DocIssuedAuthorityMand, :DocIsPdfExtRequired, :DocIsPasswordProtected, :PdfMappingRef)");
		
		// Execute the SQL, binding the arguments.
		logger.trace(Literal.SQL + sql.toString());
		SqlParameterSource paramSource = new BeanPropertySqlParameterSource(documentType);
		
		try {
			namedParameterJdbcTemplate.update(sql.toString(), paramSource);
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
		sql.append(" set DocTypeDesc = :DocTypeDesc, DocIsCustDoc = :DocIsCustDoc,");
		sql.append(" DocIsMandatory = :DocIsMandatory, DocTypeIsActive = :DocTypeIsActive,");
		sql.append(" Version = :Version , LastMntBy = :LastMntBy, LastMntOn = :LastMntOn, ");
		sql.append(" RecordStatus= :RecordStatus, RoleCode = :RoleCode,NextRoleCode = :NextRoleCode, TaskId = :TaskId,");
		sql.append(" NextTaskId = :NextTaskId, RecordType = :RecordType, WorkflowId = :WorkflowId, DocExpDateIsMand = :DocExpDateIsMand,");
		sql.append(" DocIssueDateMand= :DocIssueDateMand, DocIdNumMand = :DocIdNumMand, DocIssuedAuthorityMand = :DocIssuedAuthorityMand,");
		sql.append(" DocIsPdfExtRequired = :DocIsPdfExtRequired, DocIsPasswordProtected = :DocIsPasswordProtected, PdfMappingRef = :PdfMappingRef");
		sql.append(" where DocTypeCode =:DocTypeCode ");
		sql.append(QueryUtil.getConcurrencyCondition(tableType));

		// Execute the SQL, binding the arguments.
		logger.trace(Literal.SQL + sql.toString());

		SqlParameterSource paramSource = new BeanPropertySqlParameterSource(documentType);
		int recordCount = namedParameterJdbcTemplate.update(sql.toString(), paramSource);

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
			recordCount = namedParameterJdbcTemplate.update(sql.toString(), beanParameters);
		} catch (DataAccessException e) {
			throw new DependencyFoundException(e);
		}
		
		// Check for the concurrency failure.
		if (recordCount == 0) {
			throw new ConcurrencyException();
		}

		logger.debug(Literal.LEAVING);
	}

	/**
	 * @param dataSource
	 *            the dataSource to set
	 */
	public void setDataSource(DataSource dataSource) {
		this.namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
	}
}