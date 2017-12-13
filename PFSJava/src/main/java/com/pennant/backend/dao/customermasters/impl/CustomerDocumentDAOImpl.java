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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.core.simple.ParameterizedBeanPropertyRowMapper;

import com.pennant.backend.dao.customermasters.CustomerDocumentDAO;
import com.pennant.backend.dao.impl.BasisCodeDAO;
import com.pennant.backend.model.customermasters.CustomerDocument;
import com.pennant.backend.model.documentdetails.DocumentDetails;
import com.pennanttech.pennapps.core.ConcurrencyException;
import com.pennanttech.pennapps.core.DependencyFoundException;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pff.core.util.QueryUtil;

/**
 * DAO methods implementation for the <b>CustomerDocument model</b> class.<br>
 * 
 */
public class CustomerDocumentDAOImpl extends BasisCodeDAO<CustomerDocument>	implements CustomerDocumentDAO {
	private static Logger logger = Logger.getLogger(CustomerDocumentDAOImpl.class);

	private NamedParameterJdbcTemplate namedParameterJdbcTemplate;

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
		selectSql.append(" SELECT CustID, CustDocType, CustDocTitle, CustDocSysName, CustDocRcvdOn," );
		selectSql.append(" CustDocCategory, CustDocName, DocRefId," );
		selectSql.append(" CustDocExpDate, CustDocIssuedOn, CustDocIssuedCountry, CustDocIsVerified," );
		selectSql.append(" CustDocVerifiedBy, CustDocIsAcrive, DocPurpose, DocUri,");
		if(type.contains("View")){
			selectSql.append(" lovDescCustDocCategory, lovDescCustDocIssuedCountry, " );
			selectSql.append(" DocExpDateIsMand,DocIssueDateMand,DocIdNumMand,");
			selectSql.append(" DocIssuedAuthorityMand, DocIsPdfExtRequired, DocIsPasswordProtected, PdfMappingRef, pdfPassWord,");
		}
		selectSql.append(" Version, LastMntOn, LastMntBy, RecordStatus, RoleCode, NextRoleCode,");
		selectSql.append(" TaskId, NextTaskId, RecordType, WorkflowId ");
		selectSql.append(" FROM  CustomerDocuments");
		selectSql.append(StringUtils.trimToEmpty(type));
		selectSql.append(" Where CustID = :custID AND CustDocCategory = :CustDocCategory");
		
		logger.debug("selectSql: " + selectSql.toString());		
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(customerDocument);
		RowMapper<CustomerDocument> typeRowMapper = ParameterizedBeanPropertyRowMapper.newInstance(
				CustomerDocument.class);

		try {
			customerDocument = this.namedParameterJdbcTemplate.queryForObject(
					selectSql.toString(), beanParameters, typeRowMapper);
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
		logger.debug("Entering");
		CustomerDocument customerDocument = new CustomerDocument();
		customerDocument.setId(custId);

		StringBuilder selectSql = new StringBuilder();
		selectSql.append("SELECT CustID, CustDocType, CustDocTitle, CustDocSysName");
		selectSql.append(", CustDocRcvdOn, CustDocExpDate, CustDocIssuedOn, CustDocIssuedCountry");
		selectSql.append(", CustDocIsVerified, CustDocCategory, CustDocName, DocRefId, CustDocVerifiedBy, CustDocIsAcrive");
		selectSql.append(", DocPurpose, DocUri");
		if (type.contains("View")) {
			selectSql.append(", lovDescCustDocCategory, lovDescCustDocIssuedCountry");
			selectSql.append(", DocExpDateIsMand,DocIssueDateMand,DocIdNumMand,");
			selectSql.append(" DocIssuedAuthorityMand, DocIsPdfExtRequired, DocIsPasswordProtected, PdfMappingRef, pdfPassWord");
		}
		selectSql.append(", Version, LastMntOn, LastMntBy, RecordStatus");
		selectSql.append(", RoleCode, NextRoleCode, TaskId, NextTaskId");
		selectSql.append(", RecordType, WorkflowId");
		selectSql.append(" FROM CustomerDocuments");
		selectSql.append(StringUtils.trimToEmpty(type));
		selectSql.append(" where CustID = :CustID ");

		logger.debug("selectSql: " + selectSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(	customerDocument);
		RowMapper<CustomerDocument> typeRowMapper = ParameterizedBeanPropertyRowMapper.newInstance(
				CustomerDocument.class);
		
		List<CustomerDocument> customerDocuments =  this.namedParameterJdbcTemplate.query(selectSql.toString(),	beanParameters, typeRowMapper);
		logger.debug("Leaving");
		return customerDocuments;
	}
	
	public List<CustomerDocument> getCustomerDocumentByCustomerId(final long custId) {
		logger.debug("Entering");
		CustomerDocument customerDocument = new CustomerDocument();
		customerDocument.setId(custId);

		StringBuilder selectSql = new StringBuilder();
		selectSql.append(" SELECT CustID, CustDocType, CustDocTitle, CustDocSysName, CustDocRcvdOn," );
		selectSql.append(" CustDocExpDate, CustDocIssuedOn, CustDocIssuedCountry, CustDocIsVerified," );
		selectSql.append(" CustDocCategory, CustDocName, DocRefId, CustDocVerifiedBy, CustDocIsAcrive,");
		selectSql.append(" lovDescCustDocCategory, lovDescCustDocIssuedCountry, ");
		selectSql.append(" DocExpDateIsMand,DocIssueDateMand,DocIdNumMand, DocPurpose, DocUri,");
		selectSql.append(" DocIsPdfExtRequired, DocIsPasswordProtected, PdfMappingRef, pdfPassWord,");
		selectSql.append(" Version, LastMntOn, LastMntBy, RecordStatus, RoleCode, NextRoleCode,");
		selectSql.append(" TaskId, NextTaskId, RecordType, WorkflowId ");
		selectSql.append(" FROM  CustomerDocuments_AView");
		selectSql.append(" Where CustID = :custID ");
		
		logger.debug("selectSql: " + selectSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(	customerDocument);
		RowMapper<CustomerDocument> typeRowMapper = ParameterizedBeanPropertyRowMapper.newInstance(
				CustomerDocument.class);
		
		List<CustomerDocument> customerDocuments =  this.namedParameterJdbcTemplate.query(selectSql.toString(),	beanParameters, typeRowMapper);
		
		logger.debug("Leaving");
		return customerDocuments;
	}

	/**
	 * @param dataSource
	 *            the dataSource to set
	 */
	public void setDataSource(DataSource dataSource) {
		this.namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
	}

	/**
	 * This method Deletes the Record from the CustomerDocuments or
	 * CustomerDocuments_Temp. if Record not deleted then throws
	 * DataAccessException with error 41003. delete Customer Documents by key
	 * CustID
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
		logger.debug("deleteSql: "+ deleteSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(customerDocument);
		
		try {
			recordCount = this.namedParameterJdbcTemplate.update(deleteSql.toString(),beanParameters);

			if (recordCount <= 0) {
				throw new ConcurrencyException();
			}
		} catch (DataAccessException e) {
			throw new DependencyFoundException(e);
		}
		logger.debug("Leaving");
	}

	/**
	 * This method Deletes the Record from the CustomerDocuments or CustomerDocuments_Temp for the customer.
	 * delete Customer Documents by key CustID
	 * 
	 * @param long customerDocument 
	 * @param  type (String)
	 * 			""/_Temp/_View          
	 * @return void
	 * @throws DataAccessException
	 * 
	 */
	public void deleteByCustomer(long customerId,String type) {
		logger.debug("Entering");
		
		CustomerDocument customerDocument = new CustomerDocument();
		customerDocument.setCustID(customerId);
		
		StringBuilder deleteSql = new StringBuilder();
		deleteSql.append(" Delete From CustomerDocuments");
		deleteSql.append(StringUtils.trimToEmpty(type));
		deleteSql.append(" Where CustID =:CustID");
		logger.debug("deleteSql: "+ deleteSql.toString());
		
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(customerDocument);
		this.namedParameterJdbcTemplate.update(deleteSql.toString(), beanParameters);
		logger.debug("Leaving");
	}
	
	/**
	 * This method insert new Records into CustomerDocuments or
	 * CustomerDocuments_Temp.
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
		insertSql.append(" (CustID, CustDocType, CustDocTitle, CustDocSysName, CustDocRcvdOn," );
		insertSql.append(" CustDocExpDate, CustDocIssuedOn, CustDocIssuedCountry, CustDocIsVerified, CustDocVerifiedBy,");
		insertSql.append(" CustDocIsAcrive,CustDocCategory, CustDocName, DocRefId, DocPurpose, DocUri, PdfPassWord,");
		insertSql.append(" Version , LastMntBy, LastMntOn, RecordStatus, RoleCode, NextRoleCode," );
		insertSql.append(" TaskId, NextTaskId, RecordType, WorkflowId)");
		insertSql.append(" Values(:CustID, :CustDocType, :CustDocTitle, :CustDocSysName, :CustDocRcvdOn," );
		insertSql.append(" :CustDocExpDate, :CustDocIssuedOn, :CustDocIssuedCountry, :CustDocIsVerified, :CustDocVerifiedBy,");
		insertSql.append(" :CustDocIsAcrive,:CustDocCategory, :CustDocName, :DocRefId, :DocPurpose, :DocUri, :PdfPassWord,");
		insertSql.append(" :Version , :LastMntBy, :LastMntOn, :RecordStatus, :RoleCode, :NextRoleCode," );
		insertSql.append(" :TaskId, :NextTaskId, :RecordType, :WorkflowId)");
		
		logger.debug("insertSql: "+ insertSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(customerDocument);
		this.namedParameterJdbcTemplate.update(insertSql.toString(), beanParameters);

		logger.debug("Leaving");
		return customerDocument.getId();
	}

	/**
	 * This method updates the Record CustomerDocuments or
	 * CustomerDocuments_Temp. if Record not updated then throws
	 * DataAccessException with error 41004. update Customer Documents by key
	 * CustID and Version
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
		updateSql.append(" Set CustDocType = :CustDocType, CustDocTitle = :CustDocTitle," );
		updateSql.append(" CustDocSysName = :CustDocSysName, CustDocRcvdOn = :CustDocRcvdOn," );
		updateSql.append(" CustDocExpDate = :CustDocExpDate, CustDocIssuedOn = :CustDocIssuedOn," );
		updateSql.append(" CustDocName=:CustDocName," );
		updateSql.append(" CustDocIssuedCountry = :CustDocIssuedCountry, CustDocIsVerified = :CustDocIsVerified," );
		updateSql.append(" CustDocVerifiedBy = :CustDocVerifiedBy, CustDocIsAcrive = :CustDocIsAcrive, DocRefId = :DocRefId,");
		updateSql.append(" DocPurpose = :DocPurpose, DocUri = :DocUri, PdfPassWord = :PdfPassWord, Version = :Version , LastMntBy = :LastMntBy,");
		updateSql.append(" LastMntOn = :LastMntOn, RecordStatus= :RecordStatus, RoleCode = :RoleCode,");
		updateSql.append(" NextRoleCode = :NextRoleCode, TaskId = :TaskId, NextTaskId = :NextTaskId, RecordType = :RecordType,");
		updateSql.append(" WorkflowId = :WorkflowId ");
		updateSql.append(" Where CustID =:CustID AND CustDocCategory =:CustDocCategory");
		
		logger.debug("updateSql: "+ updateSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(customerDocument);
		recordCount = this.namedParameterJdbcTemplate.update(updateSql.toString(), beanParameters);

		if (recordCount <= 0) {
			throw new ConcurrencyException();
		}
		logger.debug("Leaving");
	}
	
	/**
	 * Get Customer document by customer and document type
	 * @param CustID
	 * @param DocType
	 * @return DocumentDetails
	 */
	public DocumentDetails getCustDocByCustAndDocType(final long custId, String docType, String type) {
		logger.debug("Entering");
		CustomerDocument customerDocument = new CustomerDocument();
		customerDocument.setCustID(custId);
		customerDocument.setCustDocCategory(docType);
		
		StringBuilder selectSql = new StringBuilder("Select CustID, CustDocCategory docCategory, 1 docIsCustDoc, ");
		selectSql.append(" CustDocType DocType, CustDocName DocName, DocRefId, DocPurpose, DocUri,");
		if(type.contains("View")){
			selectSql.append(" lovDescCustDocCategory lovDescDocCategoryName, CustDocTitle, CustDocSysName,");
			selectSql.append(" custDocRcvdOn, custDocExpDate, custDocIssuedOn, ");
			selectSql.append(" custDocIssuedCountry, lovDescCustDocIssuedCountry, custDocIsVerified, custDocVerifiedBy, custDocIsAcrive, ");
			selectSql.append(" DocExpDateIsMand,DocIssueDateMand,DocIdNumMand, ");
			selectSql.append(" DocIsPdfExtRequired, DocIsPasswordProtected, PdfMappingRef, pdfPassWord,");
		}
		selectSql.append(" Version, LastMntOn, LastMntBy, RecordStatus, RoleCode, NextRoleCode,");
		selectSql.append(" TaskId, NextTaskId, RecordType, WorkflowId ");
		selectSql.append(" From CustomerDocuments");
		selectSql.append(StringUtils.trimToEmpty(type));
		selectSql.append(" Where CustID = :CustID AND CustDocCategory =:CustDocCategory ");
		
		DocumentDetails documentDetails =null;
		logger.debug("selectSql: " + selectSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(customerDocument);
		RowMapper<DocumentDetails> typeRowMapper = ParameterizedBeanPropertyRowMapper.newInstance(DocumentDetails.class);
		
		try{
			documentDetails = this.namedParameterJdbcTemplate.queryForObject(selectSql.toString(), beanParameters, typeRowMapper);	
		}catch (EmptyResultDataAccessException e) {
			logger.warn("Exception: ", e);
			documentDetails = null;
		}
		logger.debug("Leaving");
		return documentDetails;
	}
	
	/**
	 * Get Customer documents by customer and document type(s)
	 * @param CustID
	 * @param DocType
	 * @return DocumentDetails
	 */
	@Override
	public List<DocumentDetails> getCustDocListByDocTypes(final long custId, List<String> docTypeList, String type) {
		logger.debug("Entering");
		
		StringBuilder selectSql = new StringBuilder("Select CustID, CustDocCategory docCategory, 1 docIsCustDoc, ");
		selectSql.append(" CustDocType DocType, DocPurpose, DocUri, CustDocName DocName, RecordStatus,");
		selectSql.append(" RecordType, WorkflowId" );
		selectSql.append(" From CustomerDocuments");
		selectSql.append(StringUtils.trimToEmpty(type));
		selectSql.append(" Where CustID =:CustID AND CustDocCategory IN(:DocTypeList) ");
		
		Map<String, List<String>> parameterMap=new HashMap<String,List<String>>();
		List<String> ids = new ArrayList<String>();
		ids.add(String.valueOf(custId));
		parameterMap.put("CustID", ids);
		parameterMap.put("DocTypeList", docTypeList);

		logger.debug("selectSql: " + selectSql.toString());
		RowMapper<DocumentDetails> typeRowMapper = ParameterizedBeanPropertyRowMapper.newInstance(DocumentDetails.class);
		
		List<DocumentDetails> documentDetails = this.namedParameterJdbcTemplate.query(selectSql.toString(),parameterMap, typeRowMapper);  
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
		
		StringBuilder selectSql = new StringBuilder("Select CustID, CustDocCategory docCategory, 1 docIsCustDoc, ");
		selectSql.append(" CustDocType DocType, CustDocName DocName, DocRefId, DocPurpose, DocUri,");
		if(type.contains("View")){
			selectSql.append(" lovDescCustDocCategory lovDescDocCategoryName, CustDocTitle, CustDocSysName,");
			selectSql.append(" custDocRcvdOn, custDocExpDate, custDocIssuedOn, lovDescCustCIF, lovDescCustShrtName,");
			selectSql.append(" custDocIssuedCountry, lovDescCustDocIssuedCountry, custDocIsVerified, custDocVerifiedBy, custDocIsAcrive,");
			selectSql.append(" DocExpDateIsMand,DocIssueDateMand,DocIdNumMand,");
		}
		selectSql.append(" Version, LastMntOn, LastMntBy, RecordStatus, RoleCode, NextRoleCode,");
		selectSql.append(" TaskId, NextTaskId, RecordType, WorkflowId ");
		selectSql.append(" From CustomerDocuments");
		selectSql.append(StringUtils.trimToEmpty(type));
		selectSql.append(" Where CustID = :CustID ");
		
		logger.debug("selectSql: " + selectSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(customerDocument);
		RowMapper<DocumentDetails> typeRowMapper = ParameterizedBeanPropertyRowMapper.newInstance(DocumentDetails.class);
		
		List<DocumentDetails> documentDetails = this.namedParameterJdbcTemplate.query(selectSql.toString(), beanParameters, typeRowMapper);	

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
		Integer count = namedParameterJdbcTemplate.queryForObject(sql, paramSource, Integer.class);

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
			recordCount = this.namedParameterJdbcTemplate.queryForObject(selectSql.toString(), source, Integer.class);
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
			recordCount = this.namedParameterJdbcTemplate.queryForObject(selectSql.toString(), source, Integer.class);
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
			recordCount = this.namedParameterJdbcTemplate.queryForObject(selectSql.toString(), source, Integer.class);
		} catch (EmptyResultDataAccessException dae) {
			logger.error(dae);
			recordCount = 0;
		}
		logger.debug("Leaving");
		return recordCount;
	}

}