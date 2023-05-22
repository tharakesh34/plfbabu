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
 * * FileName : DocumentDetailsDAOImpl.java * * Author : PENNANT TECHONOLOGIES * * Creation Date : 21-06-2012 * *
 * Modified Date : 21-06-2012 * * Description : * *
 ********************************************************************************************
 * Date Author Version Comments *
 ********************************************************************************************
 * 21-06-2012 Pennant 0.1 * * * * * * * * *
 ********************************************************************************************
 */
package com.pennant.backend.dao.documentdetails.impl;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.PreparedStatementSetter;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSourceUtils;

import com.pennant.backend.dao.documentdetails.DocumentDetailsDAO;
import com.pennant.backend.model.documentdetails.DocumentDetails;
import com.pennanttech.pennapps.core.ConcurrencyException;
import com.pennanttech.pennapps.core.DependencyFoundException;
import com.pennanttech.pennapps.core.jdbc.JdbcUtil;
import com.pennanttech.pennapps.core.jdbc.SequenceDao;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pennapps.core.resource.Message;
import com.pennanttech.pff.core.TableType;

/**
 * DAO methods implementation for the <b>documentDetails model</b> class.
 */
public class DocumentDetailsDAOImpl extends SequenceDao<DocumentDetails> implements DocumentDetailsDAO {
	private static Logger logger = LogManager.getLogger(DocumentDetailsDAOImpl.class);

	public DocumentDetailsDAOImpl() {
		super();
	}

	/**
	 * Fetch the Record Channel Detail details by key field
	 * 
	 * @param id   (int)
	 * @param type (String) ""/_Temp/_View
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
	 * @param Channel Detail (documentDetails)
	 * @param type    (String) ""/_Temp/_View
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
		} catch (DataAccessException e) {
			throw new DependencyFoundException(e);
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
	 * @param Channel Detail (documentDetails)
	 * @param type    (String) ""/_Temp/_View
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
		/*
		 * final loan approval stage record type is not changed to blank To address the both we are making here record
		 * type as empty(AS per our framework main table record type should be empty)
		 */
		if (StringUtils.isEmpty(type)) {
			documentDetails.setRecordType("");
		}

		StringBuilder insertSql = new StringBuilder("Insert Into DocumentDetails");
		insertSql.append(StringUtils.trimToEmpty(type));
		insertSql.append("(DocId, DocModule, DocCategory, Doctype,DocName,ReferenceId, FinEvent, DocImage");
		insertSql.append(", DocPurpose, DocUri, DocReceivedDate, DocReceived, DocOriginal, DocBarcode, Remarks");
		insertSql.append(", Version , LastMntBy, LastMntOn, RecordStatus, RoleCode, NextRoleCode,");
		insertSql.append(" TaskId, NextTaskId, RecordType, WorkflowId, docRefId, instructionUID)");
		insertSql.append(" Values(:DocId, :DocModule, :DocCategory, :Doctype, :DocName, :ReferenceId");
		insertSql.append(", :FinEvent, :DocImage, :DocPurpose, :DocUri, :DocReceivedDate, :DocReceived");
		insertSql.append(", :docOriginal, :DocBarcode , :Remarks");
		insertSql.append(", :Version , :LastMntBy, :LastMntOn, :RecordStatus, :RoleCode, :NextRoleCode,");
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
	 * @param Channel Detail (documentDetails)
	 * @param type    (String) ""/_Temp/_View
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
		updateSql.append(" Set DocModule= :DocModule, DocCategory= :DocCategory, Doctype= :Doctype,DocName= :DocName,");
		updateSql.append(" ReferenceId=:ReferenceId, FinEvent=:FinEvent, DocPurpose=:DocPurpose,");
		updateSql.append(" DocReceivedDate=:DocReceivedDate, DocReceived=:DocReceived, DocOriginal=:DocOriginal,");
		updateSql.append(" DocRefId=:DocRefId, InstructionUID=:InstructionUID, DocImage=:DocImage,");
		updateSql.append(" DocBarcode=:DocBarcode, Remarks=:Remarks, Version=:Version , LastMntBy=:LastMntBy,");
		updateSql.append(
				" LastMntOn=:LastMntOn, RecordStatus=:RecordStatus, RoleCode=:RoleCode, NextRoleCode=:NextRoleCode,");
		updateSql.append(" TaskId=:TaskId, NextTaskId=:NextTaskId, RecordType=:RecordType, WorkflowId=:WorkflowId");
		updateSql.append(" Where DocId = :DocId");

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
		StringBuilder sql = new StringBuilder("Select");
		sql.append(" DocId, DocModule, DocCategory, Doctype, DocName, ReferenceId, FinEvent, DocPurpose, DocImage");
		sql.append(", DocUri, DocReceivedDate, DocReceived, DocOriginal, DocBarcode, Remarks, Version, LastMntBy");
		sql.append(", LastMntOn, RecordStatus, RoleCode, NextRoleCode, TaskId, NextTaskId, RecordType");
		sql.append(", WorkflowId, DocRefId, InstructionUID");
		sql.append(" from DocumentDetails");
		sql.append(StringUtils.trimToEmpty(type));
		sql.append(" Where ReferenceId = ? and DocModule = ?");

		logger.trace(Literal.SQL + sql);

		return this.jdbcOperations.query(sql.toString(), ps -> {
			ps.setString(1, ref);
			ps.setString(2, module);

		}, (rs, rowNum) -> {
			DocumentDetails doc = new DocumentDetails();

			doc.setDocId(rs.getLong("DocId"));
			doc.setDocModule(rs.getString("DocModule"));
			doc.setDocCategory(rs.getString("DocCategory"));
			doc.setDoctype(rs.getString("Doctype"));
			doc.setDocName(rs.getString("DocName"));
			doc.setReferenceId(rs.getString("ReferenceId"));
			doc.setFinEvent(rs.getString("FinEvent"));
			doc.setDocPurpose(rs.getString("DocPurpose"));
			doc.setDocImage(rs.getBytes("DocImage"));
			doc.setDocUri(rs.getString("DocUri"));
			doc.setDocReceivedDate(rs.getTimestamp("DocReceivedDate"));
			doc.setDocReceived(rs.getBoolean("DocReceived"));
			doc.setDocOriginal(rs.getBoolean("DocOriginal"));
			doc.setDocBarcode(rs.getString("DocBarcode"));
			doc.setRemarks(rs.getString("Remarks"));
			doc.setVersion(rs.getInt("Version"));
			doc.setLastMntBy(rs.getLong("LastMntBy"));
			doc.setLastMntOn(rs.getTimestamp("LastMntOn"));
			doc.setRecordStatus(rs.getString("RecordStatus"));
			doc.setRoleCode(rs.getString("RoleCode"));
			doc.setNextRoleCode(rs.getString("NextRoleCode"));
			doc.setTaskId(rs.getString("TaskId"));
			doc.setNextTaskId(rs.getString("NextTaskId"));
			doc.setRecordType(rs.getString("RecordType"));
			doc.setWorkflowId(rs.getLong("WorkflowId"));
			doc.setDocRefId(JdbcUtil.getLong(rs.getObject("DocRefId")));
			doc.setInstructionUID(rs.getLong("InstructionUID"));

			return doc;
		});
	}

	@Override
	public List<DocumentDetails> getDocumentDetailsByRef(String ref, String module, String type) {
		StringBuilder sql = new StringBuilder("Select");
		sql.append(" DocId, DocModule, DocCategory, Doctype, DocName");
		sql.append(", ReferenceId, FinEvent, DocPurpose, DocUri, DocReceivedDate");
		sql.append(", DocReceived, DocOriginal, DocBarcode");
		sql.append(", Version, LastMntBy, LastMntOn, RecordStatus, RoleCode, NextRoleCode");
		sql.append(", TaskId, NextTaskId, RecordType, WorkflowId, docRefId, instructionUID");
		sql.append(" From DocumentDetails");
		sql.append(StringUtils.trimToEmpty(type));
		sql.append(" Where ReferenceId = ? and DocModule= ?");

		logger.trace(Literal.SQL + sql.toString());

		return this.jdbcOperations.query(sql.toString(), ps -> {
			int index = 1;

			ps.setString(index++, ref);
			ps.setString(index, module);
		}, (rs, rowNum) -> {
			DocumentDetails dd = new DocumentDetails();

			dd.setDocId(rs.getLong("DocId"));
			dd.setDocModule(rs.getString("DocModule"));
			dd.setDocCategory(rs.getString("DocCategory"));
			dd.setDoctype(rs.getString("Doctype"));
			dd.setDocName(rs.getString("DocName"));
			dd.setReferenceId(rs.getString("ReferenceId"));
			dd.setFinEvent(rs.getString("FinEvent"));
			dd.setDocPurpose(rs.getString("DocPurpose"));
			dd.setDocUri(rs.getString("DocUri"));
			dd.setDocReceivedDate(JdbcUtil.getDate(rs.getDate("DocReceivedDate")));
			dd.setDocReceived(rs.getBoolean("DocReceived"));
			dd.setDocOriginal(rs.getBoolean("DocOriginal"));
			dd.setDocBarcode(rs.getString("DocBarcode"));
			dd.setVersion(rs.getInt("Version"));
			dd.setLastMntBy(rs.getLong("LastMntBy"));
			dd.setLastMntOn(rs.getTimestamp("LastMntOn"));
			dd.setRecordStatus(rs.getString("RecordStatus"));
			dd.setRoleCode(rs.getString("RoleCode"));
			dd.setNextRoleCode(rs.getString("NextRoleCode"));
			dd.setTaskId(rs.getString("TaskId"));
			dd.setNextTaskId(rs.getString("NextTaskId"));
			dd.setRecordType(rs.getString("RecordType"));
			dd.setWorkflowId(rs.getLong("WorkflowId"));
			dd.setDocRefId(JdbcUtil.getLong(rs.getObject("docRefId")));
			dd.setInstructionUID(rs.getLong("instructionUID"));

			return dd;
		});

	}

	@Override
	public DocumentDetails getDocumentDetails(long id, String type) {
		StringBuilder sql = new StringBuilder("Select");
		sql.append(" DocId, DocModule, DocCategory, DocReceivedDate, DocReceived, DocOriginal, DocBarcode");
		sql.append(", Remarks, Doctype, DocName, DocRefId, ReferenceId ,FinEvent, DocUri");
		sql.append(", Version , LastMntBy, LastMntOn, RecordStatus, RoleCode, NextRoleCode");
		sql.append(", TaskId, NextTaskId, RecordType, WorkflowId, InstructionUID");
		sql.append(" From DocumentDetails");
		sql.append(StringUtils.trimToEmpty(type));
		sql.append(" Where DocId = ?");

		logger.trace(Literal.SQL + sql);

		try {
			return this.jdbcOperations.queryForObject(sql.toString(), (rs, i) -> {
				DocumentDetails dd = new DocumentDetails();

				dd.setDocId(rs.getLong("DocId"));
				dd.setDocModule(rs.getString("DocModule"));
				dd.setDocCategory(rs.getString("DocCategory"));
				dd.setDocReceivedDate(rs.getTimestamp("DocReceivedDate"));
				dd.setDocReceived(rs.getBoolean("DocReceived"));
				dd.setDocOriginal(rs.getBoolean("DocOriginal"));
				dd.setDocBarcode(rs.getString("DocBarcode"));
				dd.setRemarks(rs.getString("Remarks"));
				dd.setDoctype(rs.getString("Doctype"));
				dd.setDocName(rs.getString("DocName"));
				dd.setDocRefId(JdbcUtil.getLong(rs.getObject("DocRefId")));
				dd.setReferenceId(rs.getString("ReferenceId"));
				dd.setFinEvent(rs.getString("FinEvent"));
				dd.setDocUri(rs.getString("DocUri"));
				dd.setVersion(rs.getInt("Version"));
				dd.setLastMntBy(rs.getLong("LastMntBy"));
				dd.setLastMntOn(rs.getTimestamp("LastMntOn"));
				dd.setRecordStatus(rs.getString("RecordStatus"));
				dd.setRoleCode(rs.getString("RoleCode"));
				dd.setNextRoleCode(rs.getString("NextRoleCode"));
				dd.setTaskId(rs.getString("TaskId"));
				dd.setNextTaskId(rs.getString("NextTaskId"));
				dd.setRecordType(rs.getString("RecordType"));
				dd.setWorkflowId(rs.getLong("WorkflowId"));
				dd.setInstructionUID(rs.getLong("InstructionUID"));

				return dd;
			}, id);
		} catch (EmptyResultDataAccessException e) {
			logger.warn(Message.NO_RECORD_FOUND);
			return null;
		}
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
		RowMapper<DocumentDetails> typeRowMapper = BeanPropertyRowMapper.newInstance(DocumentDetails.class);

		try {
			return this.jdbcTemplate.queryForObject(selectSql.toString(), beanParameters, typeRowMapper);
		} catch (EmptyResultDataAccessException e) {
			logger.warn(Message.NO_RECORD_FOUND);
			return null;
		}
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
		StringBuilder sql = new StringBuilder("Select");
		sql.append(" DocId, DocModule, DocCategory, Doctype, DocName, ReferenceId, FinEvent, DocPurpose");
		sql.append(", DocUri, DocReceivedDate, DocReceived, DocOriginal, DocBarcode, Remarks, Version");
		sql.append(", LastMntBy, LastMntOn, RecordStatus, RoleCode, NextRoleCode, TaskId, NextTaskId");
		sql.append(", RecordType, WorkflowId, InstructionUID, DocRefId");
		sql.append(" From DocumentDetails");
		sql.append(StringUtils.trimToEmpty(type));
		sql.append(" Where ReferenceId = ? and DocCategory = ? and DocModule = ?");

		logger.trace(Literal.SQL + sql);

		try {
			return this.jdbcOperations.queryForObject(sql.toString(), (rs, i) -> {
				DocumentDetails dd = new DocumentDetails();
				dd.setDocId(rs.getLong("DocId"));
				dd.setDocModule(rs.getString("DocModule"));
				dd.setDocCategory(rs.getString("DocCategory"));
				dd.setDoctype(rs.getString("Doctype"));
				dd.setDocName(rs.getString("DocName"));
				dd.setReferenceId(rs.getString("ReferenceId"));
				dd.setFinEvent(rs.getString("FinEvent"));
				dd.setDocPurpose(rs.getString("DocPurpose"));
				dd.setDocUri(rs.getString("DocUri"));
				dd.setDocReceivedDate(rs.getTimestamp("DocReceivedDate"));
				dd.setDocReceived(rs.getBoolean("DocReceived"));
				dd.setDocOriginal(rs.getBoolean("DocOriginal"));
				dd.setDocBarcode(rs.getString("DocBarcode"));
				dd.setRemarks(rs.getString("Remarks"));
				dd.setVersion(rs.getInt("Version"));
				dd.setLastMntBy(rs.getLong("LastMntBy"));
				dd.setLastMntOn(rs.getTimestamp("LastMntOn"));
				dd.setRecordStatus(rs.getString("RecordStatus"));
				dd.setRoleCode(rs.getString("RoleCode"));
				dd.setNextRoleCode(rs.getString("NextRoleCode"));
				dd.setTaskId(rs.getString("TaskId"));
				dd.setNextTaskId(rs.getString("NextTaskId"));
				dd.setRecordType(rs.getString("RecordType"));
				dd.setWorkflowId(rs.getLong("WorkflowId"));
				dd.setInstructionUID(rs.getLong("InstructionUID"));
				dd.setDocRefId(JdbcUtil.getLong(rs.getObject("DocRefId")));

				return dd;
			}, referenceId, category, module);
		} catch (EmptyResultDataAccessException e) {
			logger.warn(Message.NO_RECORD_FOUND);
			return null;
		}
	}

	@Override
	public int updateDocURI(String uri, long id, TableType tableType) {
		logger.debug(Literal.ENTERING);
		StringBuilder sql = new StringBuilder("update DocumentDetails");
		sql.append(tableType.getSuffix());
		sql.append(" set DocURI = ? where docrefid = ?");

		return this.jdbcOperations.update(sql.toString(), new PreparedStatementSetter() {

			@Override
			public void setValues(PreparedStatement ps) throws SQLException {
				ps.setString(1, uri);
				ps.setLong(2, id);
			}
		});
	}

	@Override
	public List<String> getRegenerateAggDocTypes() {
		logger.debug(Literal.ENTERING);

		String sql = "select * from RegenerateAgreeement";
		logger.trace(Literal.SQL + sql);

		logger.debug(Literal.LEAVING);
		return this.jdbcOperations.queryForList(sql, String.class);
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
			return this.jdbcOperations.queryForObject(sql.toString(), Long.class, finReference, docCategory);
		} catch (EmptyResultDataAccessException e) {
			logger.warn(Message.NO_RECORD_FOUND);
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
		}

		logger.debug(Literal.LEAVING);
	}
}