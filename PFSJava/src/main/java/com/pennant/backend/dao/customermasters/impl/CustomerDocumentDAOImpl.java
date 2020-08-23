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
 * FileName    		:  CustomerDocumentDAOImpl.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  06-05-2011    														*
 *                                                                  						*
 * Modified Date    :  06-05-2011    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 06-05-2011       Pennant	                 0.1                                            * 
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
package com.pennant.backend.dao.customermasters.impl;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.PreparedStatementSetter;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.core.simple.ParameterizedBeanPropertyRowMapper;

import com.pennant.backend.dao.customermasters.CustomerDocumentDAO;
import com.pennant.backend.model.customermasters.CustomerDocument;
import com.pennant.backend.model.customermasters.ExternalDocument;
import com.pennant.backend.model.documentdetails.DocumentDetails;
import com.pennanttech.pennapps.core.ConcurrencyException;
import com.pennanttech.pennapps.core.DependencyFoundException;
import com.pennanttech.pennapps.core.jdbc.SequenceDao;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pff.core.TableType;
import com.pennanttech.pff.core.util.QueryUtil;

/**
 * DAO methods implementation for the <b>CustomerDocument model</b> class.<br>
 * 
 */
public class CustomerDocumentDAOImpl extends SequenceDao<CustomerDocument> implements CustomerDocumentDAO {
	private static Logger logger = Logger.getLogger(CustomerDocumentDAOImpl.class);

	public CustomerDocumentDAOImpl() {
		super();
	}

	/**
	 * Fetch the Record Customer Documents details by key field
	 * 
	 * @param id
	 *            (String)
	 * @param type
	 *            (String) ""/_Temp/_View
	 * @return CustomerDocument
	 */
	@Override
	public CustomerDocument getCustomerDocumentById(final long id, String docCategory, String type) {
		logger.debug("Entering");
		CustomerDocument customerDocument = new CustomerDocument();
		customerDocument.setId(id);
		customerDocument.setCustDocCategory(docCategory);

		StringBuilder selectSql = new StringBuilder();
		selectSql.append(" SELECT CustID, CustDocType, CustDocTitle, CustDocSysName, CustDocRcvdOn,");
		selectSql.append(" CustDocCategory, CustDocName, DocRefId,");
		selectSql.append(" CustDocExpDate, CustDocIssuedOn, CustDocIssuedCountry, CustDocIsVerified,");
		selectSql.append(" CustDocVerifiedBy, CustDocIsAcrive, DocPurpose, DocUri, Remarks, ");
		if (type.contains("View")) {
			selectSql.append(" lovDescCustDocCategory, lovDescCustDocIssuedCountry, ");
			selectSql.append(" DocExpDateIsMand,DocIssueDateMand,DocIdNumMand,");
			selectSql.append(
					" DocIssuedAuthorityMand, DocIsPdfExtRequired, DocIsPasswordProtected, PdfMappingRef, pdfPassWord,");
		}
		selectSql.append(" Version, LastMntOn, LastMntBy, RecordStatus, RoleCode, NextRoleCode,");
		selectSql.append(" TaskId, NextTaskId, RecordType, WorkflowId ");
		selectSql.append(" FROM  CustomerDocuments");
		selectSql.append(StringUtils.trimToEmpty(type));
		selectSql.append(" Where CustID = :custID AND CustDocCategory = :CustDocCategory");

		logger.debug("selectSql: " + selectSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(customerDocument);
		RowMapper<CustomerDocument> typeRowMapper = ParameterizedBeanPropertyRowMapper
				.newInstance(CustomerDocument.class);

		try {
			customerDocument = this.jdbcTemplate.queryForObject(selectSql.toString(), beanParameters, typeRowMapper);
		} catch (EmptyResultDataAccessException e) {
			logger.info(e);
			customerDocument = null;
		}
		logger.debug("Leaving");
		return customerDocument;
	}

	/**
	 * Fetch the customer documents for the specified customer
	 * 
	 * @param custId
	 *            (String)
	 * @param type
	 *            (String) ""/_Temp/_View
	 * @return CustomerDocument
	 */
	@Override
	public List<CustomerDocument> getCustomerDocumentByCustomer(long custId, String type) {
		logger.debug(Literal.ENTERING);

		StringBuilder sql = new StringBuilder("Select");
		sql.append(" CustID, CustDocType, CustDocTitle, CustDocSysName, CustDocRcvdOn, CustDocExpDate");
		sql.append(", CustDocIssuedOn, CustDocIssuedCountry, CustDocIsVerified, CustDocCategory, CustDocName");
		sql.append(", DocRefId, CustDocVerifiedBy, CustDocIsAcrive, DocPurpose, DocUri, Version, LastMntOn");
		sql.append(", LastMntBy, RecordStatus, RoleCode, NextRoleCode, TaskId, NextTaskId, RecordType, WorkflowId");
		sql.append(", Remarks"); //HL-Merging
		if (StringUtils.trimToEmpty(type).contains("View")) {
			sql.append(", LovDescCustDocCategory, LovDescCustDocIssuedCountry, DocExpDateIsMand");
			sql.append(", DocIssueDateMand, DocIdNumMand, DocIssuedAuthorityMand, DocIsPdfExtRequired");
			sql.append(", DocIsPasswordProtected, PdfMappingRef, PdfPassWord");
		}

		sql.append(" from CustomerDocuments");
		sql.append(StringUtils.trimToEmpty(type));
		sql.append(" where CustID = ?");

		logger.trace(Literal.SQL + sql.toString());

		try {
			return this.jdbcOperations.query(sql.toString(), new PreparedStatementSetter() {
				@Override
				public void setValues(PreparedStatement ps) throws SQLException {
					int index = 1;
					ps.setLong(index++, custId);
				}
			}, new RowMapper<CustomerDocument>() {
				@Override
				public CustomerDocument mapRow(ResultSet rs, int rowNum) throws SQLException {
					CustomerDocument cd = new CustomerDocument();

					cd.setCustID(rs.getLong("CustID"));
					cd.setCustDocType(rs.getString("CustDocType"));
					cd.setCustDocTitle(rs.getString("CustDocTitle"));
					cd.setCustDocSysName(rs.getString("CustDocSysName"));
					cd.setCustDocRcvdOn(rs.getTimestamp("CustDocRcvdOn"));
					cd.setCustDocExpDate(rs.getTimestamp("CustDocExpDate"));
					cd.setCustDocIssuedOn(rs.getTimestamp("CustDocIssuedOn"));
					cd.setCustDocIssuedCountry(rs.getString("CustDocIssuedCountry"));
					cd.setCustDocIsVerified(rs.getBoolean("CustDocIsVerified"));
					cd.setCustDocCategory(rs.getString("CustDocCategory"));
					cd.setCustDocName(rs.getString("CustDocName"));
					cd.setDocRefId(rs.getLong("DocRefId"));
					cd.setCustDocVerifiedBy(rs.getLong("CustDocVerifiedBy"));
					cd.setCustDocIsAcrive(rs.getBoolean("CustDocIsAcrive"));
					cd.setDocPurpose(rs.getString("DocPurpose"));
					cd.setDocUri(rs.getString("DocUri"));
					cd.setVersion(rs.getInt("Version"));
					cd.setLastMntOn(rs.getTimestamp("LastMntOn"));
					cd.setLastMntBy(rs.getLong("LastMntBy"));
					cd.setRecordStatus(rs.getString("RecordStatus"));
					cd.setRoleCode(rs.getString("RoleCode"));
					cd.setNextRoleCode(rs.getString("NextRoleCode"));
					cd.setTaskId(rs.getString("TaskId"));
					cd.setNextTaskId(rs.getString("NextTaskId"));
					cd.setRecordType(rs.getString("RecordType"));
					cd.setWorkflowId(rs.getLong("WorkflowId"));
					cd.setRemarks(rs.getString("Remarks"));

					if (StringUtils.trimToEmpty(type).contains("View")) {
						cd.setLovDescCustDocCategory(rs.getString("LovDescCustDocCategory"));
						cd.setLovDescCustDocIssuedCountry(rs.getString("LovDescCustDocIssuedCountry"));
						cd.setLovDescdocExpDateIsMand(rs.getBoolean("DocExpDateIsMand"));
						cd.setDocIssueDateMand(rs.getBoolean("DocIssueDateMand"));
						cd.setDocIdNumMand(rs.getBoolean("DocIdNumMand"));
						cd.setDocIssuedAuthorityMand(rs.getBoolean("DocIssuedAuthorityMand"));
						cd.setDocIsPdfExtRequired(rs.getBoolean("DocIsPdfExtRequired"));
						cd.setDocIsPasswordProtected(rs.getBoolean("DocIsPasswordProtected"));
						cd.setPdfMappingRef(rs.getLong("PdfMappingRef"));
						cd.setPdfPassWord(rs.getString("PdfPassWord"));
					}

					return cd;
				}
			});
		} catch (EmptyResultDataAccessException e) {
			logger.error(Literal.EXCEPTION, e);
		}

		logger.debug(Literal.LEAVING);
		return new ArrayList<>();
	}

	public List<CustomerDocument> getCustomerDocumentByCustomerId(final long custId) {
		logger.debug("Entering");
		CustomerDocument customerDocument = new CustomerDocument();
		customerDocument.setId(custId);

		StringBuilder selectSql = new StringBuilder();
		selectSql.append(" SELECT CustID, CustDocType, CustDocTitle, CustDocSysName, CustDocRcvdOn,");
		selectSql.append(" CustDocExpDate, CustDocIssuedOn, CustDocIssuedCountry, CustDocIsVerified,");
		selectSql.append(" CustDocCategory, CustDocName, DocRefId, CustDocVerifiedBy, CustDocIsAcrive, Remarks,");
		selectSql.append(" lovDescCustDocCategory, lovDescCustDocIssuedCountry, ");
		selectSql.append(" DocExpDateIsMand,DocIssueDateMand,DocIdNumMand, DocPurpose, DocUri,");
		selectSql.append(" DocIsPdfExtRequired, DocIsPasswordProtected, PdfMappingRef, pdfPassWord,");
		selectSql.append(" Version, LastMntOn, LastMntBy, RecordStatus, RoleCode, NextRoleCode,");
		selectSql.append(" TaskId, NextTaskId, RecordType, WorkflowId ");
		selectSql.append(" FROM  CustomerDocuments_AView");
		selectSql.append(" Where CustID = :custID ");

		logger.debug("selectSql: " + selectSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(customerDocument);
		RowMapper<CustomerDocument> typeRowMapper = ParameterizedBeanPropertyRowMapper
				.newInstance(CustomerDocument.class);

		List<CustomerDocument> customerDocuments = this.jdbcTemplate.query(selectSql.toString(), beanParameters,
				typeRowMapper);

		logger.debug("Leaving");
		return customerDocuments;
	}

	/**
	 * This method Deletes the Record from the CustomerDocuments or CustomerDocuments_Temp. if Record not deleted then
	 * throws DataAccessException with error 41003. delete Customer Documents by key CustID
	 * 
	 * @param Customer
	 *            Documents (customerDocument)
	 * @param type
	 *            (String) ""/_Temp/_View
	 * @return void
	 * @throws DataAccessException
	 * 
	 */
	@Override
	public void delete(CustomerDocument customerDocument, String type) {
		logger.debug("Entering");
		int recordCount = 0;

		StringBuilder deleteSql = new StringBuilder();
		deleteSql.append(" Delete From CustomerDocuments");
		deleteSql.append(StringUtils.trimToEmpty(type));
		deleteSql.append(" Where CustID =:CustID AND CustDocCategory =:CustDocCategory");
		logger.debug("deleteSql: " + deleteSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(customerDocument);

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

	/**
	 * This method Deletes the Record from the CustomerDocuments or CustomerDocuments_Temp for the customer. delete
	 * Customer Documents by key CustID
	 * 
	 * @param long
	 *            customerDocument
	 * @param type
	 *            (String) ""/_Temp/_View
	 * @return void
	 * @throws DataAccessException
	 * 
	 */
	public void deleteByCustomer(long customerId, String type) {
		logger.debug("Entering");

		CustomerDocument customerDocument = new CustomerDocument();
		customerDocument.setCustID(customerId);

		StringBuilder deleteSql = new StringBuilder();
		deleteSql.append(" Delete From CustomerDocuments");
		deleteSql.append(StringUtils.trimToEmpty(type));
		deleteSql.append(" Where CustID =:CustID");
		logger.debug("deleteSql: " + deleteSql.toString());

		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(customerDocument);
		this.jdbcTemplate.update(deleteSql.toString(), beanParameters);
		logger.debug("Leaving");
	}

	/**
	 * This method insert new Records into CustomerDocuments or CustomerDocuments_Temp.
	 * 
	 * save Customer Documents
	 * 
	 * @param Customer
	 *            Documents (customerDocument)
	 * @param type
	 *            (String) ""/_Temp/_View
	 * @return void
	 * @throws DataAccessException
	 * 
	 */
	@Override
	public long save(CustomerDocument customerDocument, String type) {
		logger.debug("Entering");

		StringBuilder insertSql = new StringBuilder();
		insertSql.append("Insert Into CustomerDocuments");
		insertSql.append(StringUtils.trimToEmpty(type));
		insertSql.append(" (CustID, CustDocType, CustDocTitle, CustDocSysName, CustDocRcvdOn,");
		insertSql.append(
				" CustDocExpDate, CustDocIssuedOn, CustDocIssuedCountry, CustDocIsVerified, CustDocVerifiedBy,");
		insertSql.append(
				" CustDocIsAcrive,CustDocCategory, CustDocName, DocRefId, DocPurpose, DocUri, PdfPassWord,Remarks,");
		insertSql.append(" Version , LastMntBy, LastMntOn, RecordStatus, RoleCode, NextRoleCode,");
		insertSql.append(" TaskId, NextTaskId, RecordType, WorkflowId)");
		insertSql.append(" Values(:CustID, :CustDocType, :CustDocTitle, :CustDocSysName, :CustDocRcvdOn,");
		insertSql.append(
				" :CustDocExpDate, :CustDocIssuedOn, :CustDocIssuedCountry, :CustDocIsVerified, :CustDocVerifiedBy,");
		insertSql.append(
				" :CustDocIsAcrive,:CustDocCategory, :CustDocName, :DocRefId, :DocPurpose, :DocUri, :PdfPassWord, :Remarks,");
		insertSql.append(" :Version , :LastMntBy, :LastMntOn, :RecordStatus, :RoleCode, :NextRoleCode,");
		insertSql.append(" :TaskId, :NextTaskId, :RecordType, :WorkflowId)");

		logger.debug("insertSql: " + insertSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(customerDocument);
		this.jdbcTemplate.update(insertSql.toString(), beanParameters);

		logger.debug("Leaving");
		return customerDocument.getId();
	}

	@Override
	public long save(ExternalDocument externalDocument, String type) {
		logger.debug("Entering");

		if (externalDocument.getId() == Long.MIN_VALUE || externalDocument.getId() == 0) {
			externalDocument.setId(getNextValue("SeqDocumentDetails"));
		}

		StringBuilder insertSql = new StringBuilder("Insert Into ExternalDocuments");
		insertSql.append(StringUtils.trimToEmpty(type));
		insertSql.append(
				" ( Id, CustId, FinReference, BankId,DocName,DocType, FromDate, ToDate, Passwordprotected,Password,DocRefId, Docuri)");

		insertSql.append(" Values(:Id,:CustId, :FinReference, :BankId, :DocName,:DocType, :FromDate,");
		insertSql.append(" :ToDate, :PasswordProtected, :Password, :DocRefId , :DocUri)");

		logger.debug("insertSql: " + insertSql.toString());

		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(externalDocument);
		this.jdbcTemplate.update(insertSql.toString(), beanParameters);

		logger.debug("Leaving");
		return externalDocument.getId();
	}

	/**
	 * This method updates the Record CustomerDocuments or CustomerDocuments_Temp. if Record not updated then throws
	 * DataAccessException with error 41004. update Customer Documents by key CustID and Version
	 * 
	 * @param Customer
	 *            Documents (customerDocument)
	 * @param type
	 *            (String) ""/_Temp/_View
	 * @return void
	 * @throws DataAccessException
	 * 
	 */
	@Override
	public void update(CustomerDocument customerDocument, String type) {
		logger.debug("Entering");
		int recordCount = 0;

		StringBuilder updateSql = new StringBuilder();
		updateSql.append("Update CustomerDocuments");
		updateSql.append(StringUtils.trimToEmpty(type));
		updateSql.append(" Set CustDocType = :CustDocType, CustDocTitle = :CustDocTitle,");
		updateSql.append(" CustDocSysName = :CustDocSysName, CustDocRcvdOn = :CustDocRcvdOn,");
		updateSql.append(" CustDocExpDate = :CustDocExpDate, CustDocIssuedOn = :CustDocIssuedOn,");
		updateSql.append(" CustDocName=:CustDocName, Remarks= :Remarks, ");
		updateSql.append(" CustDocIssuedCountry = :CustDocIssuedCountry, CustDocIsVerified = :CustDocIsVerified,");
		updateSql.append(
				" CustDocVerifiedBy = :CustDocVerifiedBy, CustDocIsAcrive = :CustDocIsAcrive, DocRefId = :DocRefId,");
		updateSql.append(
				" DocPurpose = :DocPurpose, DocUri = :DocUri, PdfPassWord = :PdfPassWord, Version = :Version , LastMntBy = :LastMntBy,");
		updateSql.append(" LastMntOn = :LastMntOn, RecordStatus= :RecordStatus, RoleCode = :RoleCode,");
		updateSql.append(
				" NextRoleCode = :NextRoleCode, TaskId = :TaskId, NextTaskId = :NextTaskId, RecordType = :RecordType,");
		updateSql.append(" WorkflowId = :WorkflowId ");
		updateSql.append(" Where CustID =:CustID AND CustDocCategory =:CustDocCategory");

		logger.debug("updateSql: " + updateSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(customerDocument);
		recordCount = this.jdbcTemplate.update(updateSql.toString(), beanParameters);

		if (recordCount <= 0) {
			throw new ConcurrencyException();
		}
		logger.debug("Leaving");
	}

	/**
	 * Get Customer document by customer and document type
	 * 
	 * @param CustID
	 * @param DocType
	 * @return DocumentDetails
	 */
	public DocumentDetails getCustDocByCustAndDocType(final long custId, String docType, String type) {
		logger.debug("Entering");
		CustomerDocument customerDocument = new CustomerDocument();
		customerDocument.setCustID(custId);
		customerDocument.setCustDocCategory(docType);

		StringBuilder selectSql = new StringBuilder(
				"Select CustID, CustDocCategory docCategory, 'CUSTOMER' categoryCode, ");
		selectSql.append(" CustDocType DocType, CustDocName DocName, DocRefId, DocPurpose, DocUri,");
		if (type.contains("View")) {
			selectSql.append(" lovDescCustDocCategory lovDescDocCategoryName, CustDocTitle, CustDocSysName,");
			selectSql.append(" custDocRcvdOn, custDocExpDate, custDocIssuedOn, ");
			selectSql.append(
					" custDocIssuedCountry, lovDescCustDocIssuedCountry, custDocIsVerified, custDocVerifiedBy, custDocIsAcrive, ");
			selectSql.append(" DocExpDateIsMand, DocIssueDateMand, DocIdNumMand, ");
			selectSql.append(" DocIsPdfExtRequired, DocIsPasswordProtected, PdfMappingRef, pdfPassWord,");
		}
		selectSql.append(" Version, LastMntOn, LastMntBy, RecordStatus, RoleCode, NextRoleCode,");
		selectSql.append(" TaskId, NextTaskId, RecordType, WorkflowId, Remarks  ");
		selectSql.append(" From CustomerDocuments");
		selectSql.append(StringUtils.trimToEmpty(type));
		selectSql.append(" Where CustID = :CustID AND CustDocCategory =:CustDocCategory ");

		DocumentDetails documentDetails = null;
		logger.debug("selectSql: " + selectSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(customerDocument);
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

	/**
	 * Get Customer documents by customer and document type(s)
	 * 
	 * @param CustID
	 * @param DocType
	 * @return DocumentDetails
	 */
	@Override
	public List<DocumentDetails> getCustDocListByDocTypes(final long custId, List<String> docTypeList, String type) {
		logger.debug("Entering");

		StringBuilder selectSql = new StringBuilder(
				"Select CustID, CustDocCategory docCategory, 'CUSTOMER' categoryCode, ");
		selectSql.append(" CustDocType DocType, DocPurpose, DocUri, CustDocName DocName, RecordStatus,");
		selectSql.append(" RecordType, WorkflowId, Remarks");
		selectSql.append(" From CustomerDocuments");
		selectSql.append(StringUtils.trimToEmpty(type));
		selectSql.append(" Where CustID =:CustID AND CustDocCategory IN(:DocTypeList) ");

		Map<String, List<String>> parameterMap = new HashMap<String, List<String>>();
		List<String> ids = new ArrayList<String>();
		ids.add(String.valueOf(custId));
		parameterMap.put("CustID", ids);
		parameterMap.put("DocTypeList", docTypeList);

		logger.debug("selectSql: " + selectSql.toString());
		RowMapper<DocumentDetails> typeRowMapper = ParameterizedBeanPropertyRowMapper
				.newInstance(DocumentDetails.class);

		List<DocumentDetails> documentDetails = this.jdbcTemplate.query(selectSql.toString(), parameterMap,
				typeRowMapper);
		logger.debug("Leaving");
		return documentDetails;
	}

	/**
	 * Get Customer document List by customer
	 */
	public List<DocumentDetails> getCustDocByCustId(final long custId, String type) {
		logger.debug("Entering");
		CustomerDocument customerDocument = new CustomerDocument();
		customerDocument.setCustID(custId);

		StringBuilder selectSql = new StringBuilder(
				"Select CustID, CustDocCategory docCategory, 'CUSTOMER' categoryCode, ");
		selectSql.append(" CustDocType DocType, CustDocName DocName, DocRefId, DocPurpose, DocUri,");
		if (type.contains("View")) {
			selectSql.append(" lovDescCustDocCategory lovDescDocCategoryName, CustDocTitle, CustDocSysName,");
			selectSql.append(" custDocRcvdOn, custDocExpDate, custDocIssuedOn, lovDescCustCIF, lovDescCustShrtName,");
			selectSql.append(
					" custDocIssuedCountry, lovDescCustDocIssuedCountry, custDocIsVerified, custDocVerifiedBy, custDocIsAcrive,");
			selectSql.append(" DocExpDateIsMand,DocIssueDateMand,DocIdNumMand,");
		}
		selectSql.append(" Version, LastMntOn, LastMntBy, RecordStatus, RoleCode, NextRoleCode,");
		selectSql.append(" TaskId, NextTaskId, RecordType, WorkflowId ");
		selectSql.append(" From CustomerDocuments");
		selectSql.append(StringUtils.trimToEmpty(type));
		selectSql.append(" Where CustID = :CustID ");

		logger.debug("selectSql: " + selectSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(customerDocument);
		RowMapper<DocumentDetails> typeRowMapper = ParameterizedBeanPropertyRowMapper
				.newInstance(DocumentDetails.class);

		List<DocumentDetails> documentDetails = this.jdbcTemplate.query(selectSql.toString(), beanParameters,
				typeRowMapper);

		logger.debug("Leaving");
		return documentDetails;
	}

	@Override
	public boolean isDuplicateTitle(long custId, String custDocCategory, String custDocTitle) {
		logger.debug(Literal.ENTERING);

		boolean exists = false;

		// Prepare the parameter source.
		CustomerDocument document = new CustomerDocument();
		document.setCustID(custId);
		document.setCustDocCategory(custDocCategory);
		document.setCustDocTitle(custDocTitle);

		SqlParameterSource paramSource = new BeanPropertySqlParameterSource(document);

		// Check whether the document id exists for another customer.
		String sql = QueryUtil.getCountQuery(new String[] { "CustomerDocuments_Temp", "CustomerDocuments" },
				"CustID != :CustID and CustDocCategory = :CustDocCategory and CustDocTitle = :CustDocTitle");

		logger.trace(Literal.SQL + sql);
		Integer count = jdbcTemplate.queryForObject(sql, paramSource, Integer.class);

		if (count > 0) {
			exists = true;
		}

		logger.debug(Literal.LEAVING);
		return exists;
	}

	/**
	 * Method for get total number of records from BMTDocumentTypes master table.<br>
	 * 
	 * @param docType
	 * 
	 * @return Integer
	 */
	@Override
	public int getDocTypeCount(String docType) {
		logger.debug("Entering");

		MapSqlParameterSource source = new MapSqlParameterSource();
		source.addValue("DocTypeCode", docType);

		StringBuffer selectSql = new StringBuffer();
		selectSql.append("SELECT COUNT(*) FROM BMTDocumentTypes");
		selectSql.append(" WHERE ");
		selectSql.append("DocTypeCode= :DocTypeCode");

		logger.debug("insertSql: " + selectSql.toString());
		int recordCount = 0;
		try {
			recordCount = this.jdbcTemplate.queryForObject(selectSql.toString(), source, Integer.class);
		} catch (EmptyResultDataAccessException dae) {
			logger.error(dae);
			recordCount = 0;
		}
		logger.debug("Leaving");

		return recordCount;
	}

	/**
	 * Fetch current version of the record.
	 * 
	 * @param id
	 * @param typeCode
	 * @return Integer
	 */
	@Override
	public int getVersion(long custId, String docType) {
		logger.debug("Entering");

		MapSqlParameterSource source = new MapSqlParameterSource();
		source.addValue("CustId", custId);
		source.addValue("CustDocCategory", docType);

		StringBuffer selectSql = new StringBuffer();
		selectSql.append("SELECT Version FROM CustomerDocuments");

		selectSql.append(" WHERE CustId = :CustId AND CustDocCategory = :CustDocCategory");

		logger.debug("insertSql: " + selectSql.toString());

		int recordCount = 0;
		try {
			recordCount = this.jdbcTemplate.queryForObject(selectSql.toString(), source, Integer.class);
		} catch (EmptyResultDataAccessException dae) {
			logger.error(dae);
			recordCount = 0;
		}
		logger.debug("Leaving");
		return recordCount;
	}

	/**
	 * Method for get total number of records from BMTCountries master table.<br>
	 * 
	 * @param countryCode
	 * 
	 * @return Integer
	 */
	@Override
	public int getCustCountryCount(String countryCode) {
		logger.debug("Entering");

		MapSqlParameterSource source = new MapSqlParameterSource();
		source.addValue("CountryCode", countryCode);

		StringBuffer selectSql = new StringBuffer();
		selectSql.append("SELECT COUNT(*) FROM BMTCountries");
		selectSql.append(" WHERE ");
		selectSql.append("CountryCode= :CountryCode");

		logger.debug("insertSql: " + selectSql.toString());
		int recordCount = 0;
		try {
			recordCount = this.jdbcTemplate.queryForObject(selectSql.toString(), source, Integer.class);
		} catch (EmptyResultDataAccessException dae) {
			logger.error(dae);
			recordCount = 0;
		}
		logger.debug("Leaving");
		return recordCount;
	}

	/**
	 * Method for fetch duplicate CIF's for same document
	 * 
	 * @param custId
	 * @param docCategory
	 * @param docNumber
	 * @return List<String>
	 */
	@Override
	public List<String> getDuplicateDocByTitle(long custId, String docCategory, String docNumber) {
		logger.debug(Literal.ENTERING);

		// Prepare the parameter source.
		MapSqlParameterSource source = new MapSqlParameterSource();
		//source.addValue("CustId", custId);
		source.addValue("CustDocCategory", docCategory);
		source.addValue("CustDocTitle", docNumber);

		// Check whether the document id exists for another customer.
		StringBuffer selectSql = new StringBuffer();
		selectSql.append("SELECT T1.CustCIF FROM Customers T1 INNER JOIN CustomerDocuments_View T2 ");
		selectSql.append(" ON T1.CustId = T2.CustId WHERE T2.CustDocCategory = :CustDocCategory ");
		selectSql.append(" AND T2.CustDocTitle = :CustDocTitle");

		logger.trace(Literal.SQL + selectSql);
		List<String> duplicateCIFs = new ArrayList<>();
		try {
			duplicateCIFs = jdbcTemplate.queryForList(selectSql.toString(), source, String.class);
		} catch (EmptyResultDataAccessException dae) {
			Collections.emptyList();
		}

		logger.debug(Literal.LEAVING);
		return duplicateCIFs;
	}

	@Override
	public int updateDocURI(String docURI, long docrefid, TableType tableType) {
		logger.debug(Literal.ENTERING);
		StringBuilder sql = new StringBuilder("update CustomerDocuments");
		sql.append(tableType.getSuffix());
		sql.append(" set DocURI = ? where docRefId = ?");

		try {
			return this.jdbcOperations.update(sql.toString(), new PreparedStatementSetter() {

				@Override
				public void setValues(PreparedStatement ps) throws SQLException {
					ps.setString(1, docURI);
					ps.setLong(2, docrefid);
				}
			});

		} catch (Exception e) {
			logger.error(Literal.EXCEPTION, e);
		}
		logger.debug(Literal.LEAVING);
		return 0;
	}

	@Override
	public List<ExternalDocument> getExternalDocuments(long bankId, String type) {
		logger.debug(Literal.ENTERING);
		ExternalDocument externalDocument = new ExternalDocument();
		externalDocument.setBankId(bankId);

		StringBuilder selectSql = new StringBuilder();
		selectSql.append("SELECT Id, CustId, FinReference, BankId, DocName");
		selectSql.append(", DocType, FromDate, ToDate, PasswordProtected");
		selectSql.append(", Password, DocRefId, DocUri ");

		selectSql.append(" FROM ExternalDocuments");
		selectSql.append(StringUtils.trimToEmpty(type));
		selectSql.append(" where BankId = :BankId ");

		logger.debug("selectSql: " + selectSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(externalDocument);
		RowMapper<ExternalDocument> typeRowMapper = ParameterizedBeanPropertyRowMapper
				.newInstance(ExternalDocument.class);

		List<ExternalDocument> externalDocuments = null;
		try {
			externalDocuments = this.jdbcTemplate.query(selectSql.toString(), beanParameters, typeRowMapper);
		} catch (EmptyResultDataAccessException e) {
		} catch (Exception e) {
			logger.debug(Literal.EXCEPTION);
		}
		logger.debug(Literal.LEAVING);
		return externalDocuments == null ? Collections.emptyList() : externalDocuments;
	}

}