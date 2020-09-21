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
 * FileName    		:  DocumentDetailsDAOImpl.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  21-06-2012    														*
 *                                                                  						*
 * Modified Date    :  21-06-2012    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 21-06-2012       Pennant	                 0.1                                            * 
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
package com.pennant.backend.dao.documentdetails.impl;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.PreparedStatementSetter;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSourceUtils;
import org.springframework.jdbc.core.simple.ParameterizedBeanPropertyRowMapper;

import com.pennant.backend.dao.documentdetails.DocumentDetailsDAO;
import com.pennant.backend.model.documentdetails.DocumentDetails;
import com.pennanttech.pennapps.core.ConcurrencyException;
import com.pennanttech.pennapps.core.DependencyFoundException;
import com.pennanttech.pennapps.core.jdbc.SequenceDao;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pff.core.TableType;

/**
 * DAO methods implementation for the <b>documentDetails model</b> class.<br>
 */
public class DocumentDetailsDAOImpl extends SequenceDao<DocumentDetails> implements DocumentDetailsDAO {
	private static Logger logger = Logger.getLogger(DocumentDetailsDAOImpl.class);

	public DocumentDetailsDAOImpl() {
		super();
	}

	/**
	 * Fetch the Record Channel Detail details by key field
	 * 
	 * @param id
	 *            (int)
	 * @param type
	 *            (String) ""/_Temp/_View
	 * @return documentDetails
	 */
	@Override
	public DocumentDetails getDocumentDetailsById(final long id, String type) {
		logger.debug("Entering");

		return getDocumentDetailsById(id, type, false);
	}

	/**
	 * This method Deletes the Record from the documentDetailss or documentDetailss_Temp. if Record not deleted then
	 * throws DataAccessException with error 41003. delete Channel Detail by key ChannelId
	 * 
	 * @param Channel
	 *            Detail (documentDetails)
	 * @param type
	 *            (String) ""/_Temp/_View
	 * @return void
	 * @throws DataAccessException
	 * 
	 */
	@Override
	public void delete(DocumentDetails documentDetails, String type) {
		logger.debug("Entering");

		int recordCount = 0;
		StringBuilder deleteSql = new StringBuilder("Delete From DocumentDetails");
		deleteSql.append(StringUtils.trimToEmpty(type));
		deleteSql.append(" Where DocId =:DocId ");
		logger.debug("deleteSql: " + deleteSql.toString());

		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(documentDetails);
		try {
			recordCount = this.jdbcTemplate.update(deleteSql.toString(), beanParameters);
			if (recordCount <= 0) {
				throw new ConcurrencyException();
			}
		} catch (DataAccessException e) {
			throw new DependencyFoundException(e);
		}
		logger.debug("Leaving");
	}

	/*
	 * Deleting the records based on the String referenceId, String docCategory, String docModule
	 */
	@Override
	public void deleteList(String referenceId, String docCategory, String docModule, String type) {
		logger.debug(Literal.ENTERING);

		DocumentDetails documentDetails = new DocumentDetails();
		documentDetails.setReferenceId(referenceId);
		documentDetails.setDocCategory(docCategory);
		documentDetails.setDocModule(docModule);

		StringBuilder deleteSql = new StringBuilder("Delete From DocumentDetails");
		deleteSql.append(StringUtils.trimToEmpty(type));
		deleteSql.append(" Where ReferenceId =:ReferenceId AND DocCategory =:DocCategory AND DocModule =:DocModule");
		logger.debug("deleteSql: " + deleteSql.toString());

		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(documentDetails);
		try {
			this.jdbcTemplate.update(deleteSql.toString(), beanParameters);
		} catch (Exception e) {
			logger.error(Literal.EXCEPTION, e);
		}
		logger.debug(Literal.LEAVING);
	}

	/**
	 * Method for Deletion List of Document Details in temp table
	 * 
	 * @param documentDetailList
	 * @param type
	 */
	public void deleteList(List<DocumentDetails> documentDetailList, String type) {
		logger.debug("Entering");

		StringBuilder deleteSql = new StringBuilder("Delete From DocumentDetails");
		deleteSql.append(StringUtils.trimToEmpty(type));
		deleteSql.append(" Where DocId =:DocId ");
		logger.debug("deleteSql: " + deleteSql.toString());

		SqlParameterSource[] beanParameters = SqlParameterSourceUtils.createBatch(documentDetailList.toArray());
		this.jdbcTemplate.batchUpdate(deleteSql.toString(), beanParameters);
		logger.debug("Leaving");
	}

	/**
	 * This method insert new Records into documentDetailss or documentDetailss_Temp. it fetches the available Sequence
	 * form SeqdocumentDetailss by using getNextidviewDAO().getNextId() method.
	 * 
	 * save Channel Detail
	 * 
	 * @param Channel
	 *            Detail (documentDetails)
	 * @param type
	 *            (String) ""/_Temp/_View
	 * @return void
	 * @throws DataAccessException
	 * 
	 */
	@Override
	public long save(DocumentDetails documentDetails, String type) {
		logger.debug("Entering");

		if (documentDetails.getDocId() == Long.MIN_VALUE || documentDetails.getDocId() <= 0) {
			documentDetails.setDocId(getNextValue("SeqDocumentDetails"));
		}

		StringBuilder insertSql = new StringBuilder("Insert Into DocumentDetails");
		insertSql.append(StringUtils.trimToEmpty(type));
		insertSql.append(
				" ( DocId, DocModule, DocCategory, Doctype,DocName,ReferenceId, FinEvent, DocPurpose, DocUri,DocReceivedDate,DocReceived,DocOriginal, DocBarcode, Remarks");
		insertSql.append(", Version , LastMntBy, LastMntOn, RecordStatus, RoleCode, NextRoleCode,");
		insertSql.append(" TaskId, NextTaskId, RecordType, WorkflowId, docRefId, instructionUID)");
		insertSql.append(" Values(:DocId,:DocModule, :DocCategory, :Doctype, :DocName,:ReferenceId, :FinEvent,");
		insertSql.append(
				" :DocPurpose, :DocUri, :DocReceivedDate, :DocReceived , :docOriginal,:DocBarcode , :Remarks, :Version , :LastMntBy, :LastMntOn, :RecordStatus, :RoleCode, :NextRoleCode,");
		insertSql.append(" :TaskId, :NextTaskId, :RecordType, :WorkflowId, :docRefId, :instructionUID)");

		logger.debug("insertSql: " + insertSql.toString());

		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(documentDetails);
		this.jdbcTemplate.update(insertSql.toString(), beanParameters);
		logger.debug("Leaving");
		return documentDetails.getId();
	}

	/**
	 * Method for Generation of Sequence ID
	 */
	public long generateDocSeq() {
		logger.debug("Entering");
		long docId = getNextValue("SeqDocumentDetails");
		logger.debug("get NextID:" + docId);
		logger.debug("Leaving");
		return docId;
	}

	/**
	 * Method for Saving List Of Document Details
	 */
	public void saveList(ArrayList<DocumentDetails> docList, String type) {
		logger.debug("Entering");

		StringBuilder insertSql = new StringBuilder("Insert Into DocumentDetails");
		insertSql.append(StringUtils.trimToEmpty(type));
		insertSql.append(
				" ( DocId, DocModule, DocCategory, Doctype,DocName,ReferenceId,FinEvent, DocPurpose, DocUri,DocReceivedDate,DocReceived,DocOriginal,DocBarcode, Remarks");
		insertSql.append(", Version , LastMntBy, LastMntOn, RecordStatus, RoleCode, NextRoleCode, ");
		insertSql.append(" TaskId, NextTaskId, RecordType, WorkflowId, DocRefId, instructionUID)");
		insertSql.append(" Values(:DocId,:DocModule, :DocCategory, :Doctype, :DocName,:ReferenceId,:FinEvent");
		insertSql.append(
				", :DocPurpose, :DocUri, :DocReceivedDate, :DocReceived,  :docOriginal , :DocBarcode, :Remarks, :Version , :LastMntBy, :LastMntOn, :RecordStatus, :RoleCode, :NextRoleCode,");
		insertSql.append(" :TaskId, :NextTaskId, :RecordType, :WorkflowId, :docRefId, :instructionUID)");

		logger.debug("insertSql: " + insertSql.toString());

		SqlParameterSource[] beanParameters = SqlParameterSourceUtils.createBatch(docList.toArray());
		this.jdbcTemplate.batchUpdate(insertSql.toString(), beanParameters);
		logger.debug("Leaving");
	}

	/**
	 * This method updates the Record documentDetailss or documentDetailss_Temp. if Record not updated then throws
	 * DataAccessException with error 41004. update Channel Detail by key ChannelId and Version
	 * 
	 * @param Channel
	 *            Detail (documentDetails)
	 * @param type
	 *            (String) ""/_Temp/_View
	 * @return void
	 * @throws DataAccessException
	 * 
	 */
	@Override
	public void update(DocumentDetails documentDetails, String type) {
		logger.debug("Entering");

		int recordCount = 0;
		StringBuilder updateSql = new StringBuilder("Update DocumentDetails");
		updateSql.append(StringUtils.trimToEmpty(type));
		updateSql.append(" Set DocModule=:DocModule, DocCategory=:DocCategory, Doctype=:Doctype,DocName=:DocName, ");
		updateSql.append(
				" ReferenceId=:ReferenceId, FinEvent=:FinEvent, DocPurpose = :DocPurpose, DocUri = :DocUri, DocReceivedDate = :DocReceivedDate");
		updateSql.append(
				", DocReceived = :DocReceived, DocOriginal  =:docOriginal , DocBarcode = :DocBarcode, Remarks = :Remarks, Version = :Version , LastMntBy = :LastMntBy, LastMntOn = :LastMntOn, ");
		updateSql.append(" RecordStatus= :RecordStatus, RoleCode = :RoleCode, NextRoleCode = :NextRoleCode, ");
		updateSql.append(
				" TaskId = :TaskId, NextTaskId = :NextTaskId, RecordType = :RecordType, WorkflowId = :WorkflowId, docRefId = :docRefId,instructionUID = :instructionUID ");
		updateSql.append(" Where DocId =:DocId");

		logger.debug("updateSql: " + updateSql.toString());

		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(documentDetails);
		recordCount = this.jdbcTemplate.update(updateSql.toString(), beanParameters);

		if (recordCount <= 0) {
			throw new ConcurrencyException();
		}
		logger.debug("Leaving");
	}

	@Override
	public List<DocumentDetails> getDocumentDetailsByRef(String ref, String module, String finEvent, String type) {
		logger.debug("Entering");

		StringBuilder sql = new StringBuilder("select DocId, DocModule, DocCategory,");
		sql.append(
				" Doctype, DocName, ReferenceId,FinEvent, DocPurpose, DocUri,DocReceivedDate,DocReceived,DocOriginal,DocBarcode, Remarks,");
		sql.append(" Version, LastMntBy, LastMntOn, RecordStatus, RoleCode, NextRoleCode,");
		sql.append(" TaskId, NextTaskId, RecordType, WorkflowId, docRefId, instructionUID ");
		sql.append(" from DocumentDetails");
		sql.append(StringUtils.trimToEmpty(type));
		sql.append(" where ReferenceId = :ReferenceId AND DocModule =:DocModule ");
		/*
		 * if(StringUtils.isNotBlank(finEvent)){ sql.append(" AND (FinEvent = :FinEvent OR FinEvent = '' )"); }
		 */
		logger.debug("selectSql: " + sql.toString());

		DocumentDetails documentDetails = new DocumentDetails();
		documentDetails.setReferenceId(ref);
		documentDetails.setDocModule(module);
		documentDetails.setFinEvent(finEvent);

		logger.debug("selectSql: " + sql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(documentDetails);
		RowMapper<DocumentDetails> typeRowMapper = ParameterizedBeanPropertyRowMapper
				.newInstance(DocumentDetails.class);

		List<DocumentDetails> documents = this.jdbcTemplate.query(sql.toString(), beanParameters, typeRowMapper);

		logger.debug("Leaving");
		return documents;
	}

	@Override
	public List<DocumentDetails> getDocumentDetailsByRef(String ref, String module, String type) {
		logger.debug("Entering");

		StringBuilder sql = new StringBuilder("select DocId, DocModule, DocCategory,");
		sql.append(
				" Doctype, DocName, ReferenceId,FinEvent, DocPurpose, DocUri,DocReceivedDate,DocReceived,DocOriginal,DocBarcode, Remarks,");
		sql.append(" Version, LastMntBy, LastMntOn, RecordStatus, RoleCode, NextRoleCode,");
		sql.append(" TaskId, NextTaskId, RecordType, WorkflowId, docRefId, instructionUID ");
		sql.append(" from DocumentDetails");
		sql.append(StringUtils.trimToEmpty(type));
		sql.append(" where ReferenceId = :ReferenceId AND DocModule=:DocModule ");
		logger.debug("selectSql: " + sql.toString());

		DocumentDetails documentDetails = new DocumentDetails();
		documentDetails.setReferenceId(ref);
		documentDetails.setDocModule(module);

		logger.debug("selectSql: " + sql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(documentDetails);
		RowMapper<DocumentDetails> typeRowMapper = ParameterizedBeanPropertyRowMapper
				.newInstance(DocumentDetails.class);

		logger.debug("Leaving");

		return this.jdbcTemplate.query(sql.toString(), beanParameters, typeRowMapper);

	}

	@Override
	public DocumentDetails getDocumentDetails(long id, String type) {
		logger.debug("Entering");
		DocumentDetails documentDetails = new DocumentDetails();

		documentDetails.setId(id);

		StringBuilder selectSql = new StringBuilder(
				"Select DocId, DocModule, DocCategory, DocReceivedDate, DocReceived, DocOriginal,DocBarcode, Remarks,");
		selectSql.append(" Doctype, DocName, DocRefId, ReferenceId ,FinEvent, DocUri,");
		selectSql.append(" Version , LastMntBy, LastMntOn, RecordStatus, RoleCode, NextRoleCode, ");
		selectSql.append(" TaskId, NextTaskId, RecordType, WorkflowId, instructionUID ");
		selectSql.append(" From DocumentDetails");
		selectSql.append(StringUtils.trimToEmpty(type));
		selectSql.append(" Where DocId =:DocId");

		logger.debug("selectSql: " + selectSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(documentDetails);
		RowMapper<DocumentDetails> typeRowMapper = ParameterizedBeanPropertyRowMapper
				.newInstance(DocumentDetails.class);

		try {
			documentDetails = this.jdbcTemplate.queryForObject(selectSql.toString(), beanParameters, typeRowMapper);
		} catch (EmptyResultDataAccessException e) {
			logger.warn("Exception: ", e);
			documentDetails = null;
		}

		logger.debug("Leaving");

		return documentDetails;
	}

	@Override
	public DocumentDetails getDocumentDetailsById(long id, String type, boolean readAttachment) {
		logger.debug(Literal.ENTERING);
		DocumentDetails documentDetails = new DocumentDetails();

		documentDetails.setId(id);

		StringBuilder selectSql = new StringBuilder(
				"Select DocId, DocModule, DocCategory,DocReceivedDate,DocReceived, DocOriginal,DocBarcode, Remarks,");

		if (readAttachment) {
			selectSql.append(" Doctype, DocName, DocRefId, ReferenceId ,FinEvent, T1.DocUri,  T2.DocImage,");
		} else {
			selectSql.append(" Doctype, DocName, ReferenceId, FinEvent, DocPurpose, DocUri,");
		}

		selectSql.append(" Version , LastMntBy, LastMntOn, RecordStatus, RoleCode, NextRoleCode, ");
		selectSql.append(" TaskId, NextTaskId, RecordType, WorkflowId, instructionUID ");
		selectSql.append(" From DocumentDetails");
		selectSql.append(StringUtils.trimToEmpty(type));
		if (readAttachment) {
			selectSql.append(" T1 Inner Join ");
			selectSql.append(" DocumentManager T2 ON T1.DocRefId = T2.Id");
		}
		selectSql.append(" Where DocId =:DocId");

		logger.debug("selectSql: " + selectSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(documentDetails);
		RowMapper<DocumentDetails> typeRowMapper = ParameterizedBeanPropertyRowMapper
				.newInstance(DocumentDetails.class);

		try {
			documentDetails = this.jdbcTemplate.queryForObject(selectSql.toString(), beanParameters, typeRowMapper);
		} catch (EmptyResultDataAccessException e) {
			logger.warn(Literal.EXCEPTION, e);
			documentDetails = null;
		}

		logger.debug(Literal.ENTERING);

		return documentDetails;
	}

	/**
	 * Method for fetch document details.
	 * 
	 * @param referenceId
	 * @param category
	 * @param module
	 * @param type
	 * @return DocumentDetails
	 */
	@Override
	public DocumentDetails getDocumentDetails(String referenceId, String category, String module, String type) {
		logger.debug("Entering");

		DocumentDetails documentDetails = new DocumentDetails();
		documentDetails.setReferenceId(referenceId);
		documentDetails.setDocCategory(category);
		documentDetails.setDocModule(module);

		StringBuilder selectSql = new StringBuilder("Select DocId, DocModule, DocCategory, ");
		selectSql.append(
				" Doctype, DocName, ReferenceId ,FinEvent, DocPurpose, DocUri,DocReceivedDate,DocReceived,DocOriginal,DocBarcode, Remarks,");
		selectSql.append(" Version , LastMntBy, LastMntOn, RecordStatus, RoleCode, NextRoleCode, ");
		selectSql.append(" TaskId, NextTaskId, RecordType, WorkflowId, instructionUID, DocRefId ");
		selectSql.append(" From DocumentDetails");
		selectSql.append(StringUtils.trimToEmpty(type));
		selectSql.append(" Where ReferenceId =:ReferenceId AND DocCategory =:DocCategory AND DocModule =:DocModule");

		logger.debug("selectSql: " + selectSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(documentDetails);
		RowMapper<DocumentDetails> typeRowMapper = ParameterizedBeanPropertyRowMapper
				.newInstance(DocumentDetails.class);

		try {
			documentDetails = this.jdbcTemplate.queryForObject(selectSql.toString(), beanParameters, typeRowMapper);
		} catch (EmptyResultDataAccessException e) {
			logger.warn("Exception: ", e);
			documentDetails = null;
		}
		logger.debug("Leaving");
		return documentDetails;
	}

	@Override
	public int updateDocURI(String uri, long id, TableType tableType) {
		logger.debug(Literal.ENTERING);
		StringBuilder sql = new StringBuilder("update DocumentDetails");
		sql.append(tableType.getSuffix());
		sql.append(" set DocURI = ? where docrefid = ?");

		try {
			return this.jdbcOperations.update(sql.toString(), new PreparedStatementSetter() {

				@Override
				public void setValues(PreparedStatement ps) throws SQLException {
					ps.setString(1, uri);
					ps.setLong(2, id);
				}
			});

		} catch (Exception e) {
			logger.error(Literal.EXCEPTION, e);
		}
		logger.debug(Literal.LEAVING);
		return 0;
	}

	@Override
	public List<String> getRegenerateAggDocTypes() {
		logger.debug(Literal.ENTERING);

		String sql = "select * from RegenerateAgreeement";
		logger.trace(Literal.SQL + sql.toString());

		logger.debug(Literal.LEAVING);
		return this.jdbcOperations.queryForList(sql.toString(), String.class);
	}

	@Override
	public long getDocIdByDocTypeAndFinRef(String finReference, String docCategory, String type) {
		logger.debug(Literal.ENTERING);

		StringBuilder sql = new StringBuilder("");
		sql.append("Select DocId From DocumentDetails");
		sql.append(StringUtils.trimToEmpty(type));
		sql.append(" where ReferenceId = ? and DocCategory = ?");

		logger.trace(Literal.SQL + sql.toString());

		logger.debug(Literal.LEAVING);
		try {
			return this.jdbcOperations.queryForObject(sql.toString(), new Object[] { finReference, docCategory },
					Long.class);
		} catch (Exception e) {
			logger.warn(Literal.EXCEPTION, e);
			return 0;
		}
	}

	@Override
	public void deleteDocumentByDocumentId(DocumentDetails documentDetails, String type) {

		logger.debug(Literal.ENTERING);

		StringBuilder sql = new StringBuilder("Delete from COVENANT_DOCUMENTS");
		sql.append(type);
		sql.append(" Where DocumentId = :DocumentId");

		MapSqlParameterSource source = new MapSqlParameterSource();
		source.addValue("DocumentId", documentDetails.getDocId());
		logger.trace(Literal.SQL + sql.toString());

		try {
			jdbcTemplate.update(sql.toString(), source);
		} catch (DataAccessException e) {
			throw new DependencyFoundException(e);
		} catch (Exception e) {
		}
		logger.debug(Literal.LEAVING);
	}
}