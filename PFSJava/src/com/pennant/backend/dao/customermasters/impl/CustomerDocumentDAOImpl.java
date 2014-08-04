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
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.core.simple.ParameterizedBeanPropertyRowMapper;

import com.pennant.app.util.ErrorUtil;
import com.pennant.backend.dao.customermasters.CustomerDocumentDAO;
import com.pennant.backend.dao.impl.BasisCodeDAO;
import com.pennant.backend.model.ErrorDetails;
import com.pennant.backend.model.WorkFlowDetails;
import com.pennant.backend.model.customermasters.CustomerDocument;
import com.pennant.backend.model.documentdetails.DocumentDetails;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantJavaUtil;
import com.pennant.backend.util.WorkFlowUtil;

/**
 * DAO methods implementation for the <b>CustomerDocument model</b> class.<br>
 * 
 */
public class CustomerDocumentDAOImpl extends BasisCodeDAO<CustomerDocument>	implements CustomerDocumentDAO {

	private static Logger logger = Logger.getLogger(CustomerDocumentDAOImpl.class);

	// Spring Named JDBC Template
	private NamedParameterJdbcTemplate namedParameterJdbcTemplate;

	/**
	 * This method set the Work Flow id based on the module name and return the
	 * new CustomerDocument
	 * 
	 * @return CustomerDocument
	 */
	@Override
	public CustomerDocument getCustomerDocument() {
		logger.debug("Entering");
		WorkFlowDetails workFlowDetails = WorkFlowUtil.getWorkFlowDetails("CustomerDocument");
		CustomerDocument customerDocument = new CustomerDocument();
		if (workFlowDetails != null) {
			customerDocument.setWorkflowId(workFlowDetails.getWorkFlowId());
		}
		logger.debug("Leaving");
		return customerDocument;
	}

	/**
	 * This method get the module from method getCustomerDocument() and set the
	 * new record flag as true and return CustomerDocument()
	 * 
	 * @return CustomerDocument
	 */
	@Override
	public CustomerDocument getNewCustomerDocument() {
		logger.debug("Entering");
		CustomerDocument customerDocument = getCustomerDocument();
		customerDocument.setNewRecord(true);
		logger.debug("Leaving");
		return customerDocument;
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
		selectSql.append(" CustDocCategory, CustDocName, CustDocImage," );
		selectSql.append(" CustDocExpDate, CustDocIssuedOn, CustDocIssuedCountry, CustDocIsVerified," );
		selectSql.append(" CustDocVerifiedBy, CustDocIsAcrive,");
		if(type.contains("View")){
			selectSql.append(" lovDescCustDocCategory, lovDescCustDocIssuedCountry, lovDescCustRecordType," );
			selectSql.append(" lovDescCustCIF, lovDescCustShrtName,");
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
			logger.error(e);
			customerDocument = null;
		}
		logger.debug("Leaving");
		return customerDocument;
	}

	/**
	 * Fetch the Record Customer Documents details by key field
	 * 
	 * @param custId
	 *            (String)
	 * @param type
	 *            (String) ""/_Temp/_View
	 * @return CustomerDocument
	 */
	@Override
	public List<CustomerDocument> getCustomerDocumentByCustomer(final long custId, String type) {
		logger.debug("Entering");
		CustomerDocument customerDocument = new CustomerDocument();
		customerDocument.setId(custId);

		StringBuilder selectSql = new StringBuilder();
		selectSql.append(" SELECT CustID, CustDocType, CustDocTitle, CustDocSysName, CustDocRcvdOn," );
		selectSql.append(" CustDocExpDate, CustDocIssuedOn, CustDocIssuedCountry, CustDocIsVerified," );
		selectSql.append(" CustDocCategory, CustDocName, CustDocImage, " );
		selectSql.append(" CustDocVerifiedBy, CustDocIsAcrive,");
		if(type.contains("View")){
			selectSql.append(" lovDescCustDocCategory, lovDescCustDocIssuedCountry, lovDescCustRecordType," );
			selectSql.append(" lovDescCustCIF, lovDescCustShrtName,");
		}
		selectSql.append(" Version, LastMntOn, LastMntBy, RecordStatus, RoleCode, NextRoleCode,");
		selectSql.append(" TaskId, NextTaskId, RecordType, WorkflowId ");
		selectSql.append(" FROM  CustomerDocuments");
		selectSql.append(StringUtils.trimToEmpty(type));
		selectSql.append(" Where CustID = :custID ");
		
		logger.debug("selectSql: " + selectSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(	customerDocument);
		RowMapper<CustomerDocument> typeRowMapper = ParameterizedBeanPropertyRowMapper.newInstance(
				CustomerDocument.class);
		logger.debug("Leaving");
		return this.namedParameterJdbcTemplate.query(selectSql.toString(),	beanParameters, typeRowMapper);
	}
	
	
	
	
	public List<CustomerDocument> getCustomerDocumentByCustomerId(final long custId) {
		logger.debug("Entering");
		CustomerDocument customerDocument = new CustomerDocument();
		customerDocument.setId(custId);

		StringBuilder selectSql = new StringBuilder();
		selectSql.append(" SELECT CustID, CustDocType, CustDocTitle, CustDocSysName, CustDocRcvdOn," );
		selectSql.append(" CustDocExpDate, CustDocIssuedOn, CustDocIssuedCountry, CustDocIsVerified," );
		selectSql.append(" CustDocCategory, CustDocName, CustDocImage, " );
		selectSql.append(" CustDocVerifiedBy, CustDocIsAcrive,");
		selectSql.append(" lovDescCustDocCategory, lovDescCustDocIssuedCountry, lovDescCustRecordType," );
		selectSql.append(" lovDescCustCIF, lovDescCustShrtName,");
		selectSql.append(" Version, LastMntOn, LastMntBy, RecordStatus, RoleCode, NextRoleCode,");
		selectSql.append(" TaskId, NextTaskId, RecordType, WorkflowId ");
		selectSql.append(" FROM  CustomerDocuments_AView");
		selectSql.append(" Where CustID = :custID ");
		
		logger.debug("selectSql: " + selectSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(	customerDocument);
		RowMapper<CustomerDocument> typeRowMapper = ParameterizedBeanPropertyRowMapper.newInstance(
				CustomerDocument.class);
		logger.debug("Leaving");
		return this.namedParameterJdbcTemplate.query(selectSql.toString(),	beanParameters, typeRowMapper);
	}
	
	
	
	
	
	/**
	 * This method initialize the Record.
	 * 
	 * @param CustomerDocument
	 *            (customerDocument)
	 * @return CustomerDocument
	 */
	@Override
	public void initialize(CustomerDocument customerDocument) {
		super.initialize(customerDocument);
	}

	/**
	 * This method refresh the Record.
	 * 
	 * @param CustomerDocument
	 *            (customerDocument)
	 * @return void
	 */
	@Override
	public void refresh(CustomerDocument customerDocument) {

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
	@SuppressWarnings("serial")
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
				ErrorDetails errorDetails = getError("41004", customerDocument.getCustID(),
						customerDocument.getCustDocType(), customerDocument.getUserDetails().getUsrLanguage());
				throw new DataAccessException(errorDetails.getError()) { };
			}
		} catch (DataAccessException e) {
			logger.error(e);
			ErrorDetails errorDetails = getError("41006", customerDocument.getCustID(),
					customerDocument.getCustDocType(), customerDocument.getUserDetails().getUsrLanguage());
			throw new DataAccessException(errorDetails.getError()) {
			};
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
		insertSql.append(" CustDocExpDate, CustDocIssuedOn, CustDocIssuedCountry, CustDocIsVerified," );
		insertSql.append(" CustDocVerifiedBy, CustDocIsAcrive,CustDocCategory, CustDocName, CustDocImage,");
		insertSql.append(" Version , LastMntBy, LastMntOn, RecordStatus, RoleCode, NextRoleCode," );
		insertSql.append(" TaskId, NextTaskId, RecordType, WorkflowId)");
		insertSql.append(" Values(:CustID, :CustDocType, :CustDocTitle, :CustDocSysName, :CustDocRcvdOn," );
		insertSql.append(" :CustDocExpDate, :CustDocIssuedOn, :CustDocIssuedCountry, :CustDocIsVerified," );
		insertSql.append(" :CustDocVerifiedBy, :CustDocIsAcrive,:CustDocCategory, :CustDocName, :CustDocImage,");
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
	@SuppressWarnings("serial")
	@Override
	public void update(CustomerDocument customerDocument, String type) {
		logger.debug("Entering");
		int recordCount = 0;
		
		StringBuilder updateSql = new StringBuilder();
		updateSql.append("Update CustomerDocuments");
		updateSql.append(StringUtils.trimToEmpty(type));
		updateSql.append(" Set CustID = :CustID, CustDocType = :CustDocType, CustDocTitle = :CustDocTitle," );
		updateSql.append(" CustDocSysName = :CustDocSysName, CustDocRcvdOn = :CustDocRcvdOn," );
		updateSql.append(" CustDocExpDate = :CustDocExpDate, CustDocIssuedOn = :CustDocIssuedOn," );
		updateSql.append(" CustDocCategory = :CustDocCategory, CustDocName=:CustDocName, CustDocImage=:CustDocImage," );
		updateSql.append(" CustDocIssuedCountry = :CustDocIssuedCountry, CustDocIsVerified = :CustDocIsVerified," );
		updateSql.append(" CustDocVerifiedBy = :CustDocVerifiedBy, CustDocIsAcrive = :CustDocIsAcrive,");
		updateSql.append(" Version = :Version , LastMntBy = :LastMntBy, LastMntOn = :LastMntOn," );
		updateSql.append(" RecordStatus= :RecordStatus, RoleCode = :RoleCode, NextRoleCode = :NextRoleCode," );
		updateSql.append(" TaskId = :TaskId, NextTaskId = :NextTaskId, RecordType = :RecordType," );
		updateSql.append(" WorkflowId = :WorkflowId ");
		updateSql.append(" Where CustID =:CustID AND CustDocCategory =:CustDocCategory");
		if (!type.endsWith("_TEMP")) {
			//updateSql.append(" AND Version= :Version-1");
		}
		
		logger.debug("updateSql: "+ updateSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(customerDocument);
		recordCount = this.namedParameterJdbcTemplate.update(updateSql.toString(), beanParameters);

		if (recordCount <= 0) {
			logger.debug("Error Update Method Count :" + recordCount);
			ErrorDetails errorDetails =getError("41003", customerDocument.getCustID(),
					customerDocument.getCustDocType(), customerDocument.getUserDetails().getUsrLanguage());
			throw new DataAccessException(errorDetails.getError()) {};
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
		
		StringBuilder selectSql = new StringBuilder("Select CustID, CustDocCategory as docCategory, '1' as docIsCustDoc, ");
		selectSql.append(" CustDocType as DocType, CustDocName as DocName, RecordStatus,");
		if(type.contains("View")){
			selectSql.append(" lovDescCustDocCategory as lovDescDocCategoryName, CustDocImage as DocImage,  CustDocTitle, CustDocSysName,");
			selectSql.append(" custDocRcvdOn, custDocExpDate, custDocIssuedOn, lovDescCustCIF, lovDescCustShrtName,");
			selectSql.append(" custDocIssuedCountry, lovDescCustDocIssuedCountry, custDocIsVerified, custDocVerifiedBy, custDocIsAcrive, ");
		}
		selectSql.append(" RecordType, WorkflowId" );
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
		
		StringBuilder selectSql = new StringBuilder("Select CustID, CustDocCategory as docCategory, '1' as docIsCustDoc, ");
		selectSql.append(" CustDocType as DocType, CustDocName as DocName, RecordStatus,");
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
		logger.debug("Leaving");
		return this.namedParameterJdbcTemplate.query(selectSql.toString(),parameterMap, typeRowMapper);
	}
	
	/**
	 * Get Customer document List by customer 
	 */
	public List<DocumentDetails> getCustDocByCustId(final long custId, String type) {
		logger.debug("Entering");
		CustomerDocument customerDocument = new CustomerDocument();
		customerDocument.setCustID(custId);
		
		StringBuilder selectSql = new StringBuilder("Select CustID, CustDocCategory as docCategory, '1' as docIsCustDoc, ");
		selectSql.append(" CustDocType as DocType, CustDocName as DocName, RecordStatus,");
		if(type.contains("View")){
			selectSql.append(" lovDescCustDocCategory as lovDescDocCategoryName, CustDocImage as DocImage,  CustDocTitle, CustDocSysName,");
			selectSql.append(" custDocRcvdOn, custDocExpDate, custDocIssuedOn, lovDescCustCIF, lovDescCustShrtName,");
			selectSql.append(" custDocIssuedCountry, lovDescCustDocIssuedCountry, custDocIsVerified, custDocVerifiedBy, custDocIsAcrive, ");
		}
		selectSql.append(" RecordType, WorkflowId" );
		selectSql.append(" From CustomerDocuments");
		selectSql.append(StringUtils.trimToEmpty(type));
		selectSql.append(" Where CustID = :CustID ");
		
		logger.debug("selectSql: " + selectSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(customerDocument);
		RowMapper<DocumentDetails> typeRowMapper = ParameterizedBeanPropertyRowMapper.newInstance(DocumentDetails.class);
		logger.debug("Leaving");
		return this.namedParameterJdbcTemplate.query(selectSql.toString(), beanParameters, typeRowMapper);	
	}
	
	/**
	 * This method for getting the error details
	 * @param errorId (String)
	 * @param Id (String)
	 * @param userLanguage (String)
	 * @return ErrorDetails
	 */
	private ErrorDetails  getError(String errorId, long customerID,String docType, String userLanguage){
		String[][] parms= new String[2][2]; 
		parms[1][0] = String.valueOf(customerID);
		parms[1][1] = docType;

		parms[0][0] = PennantJavaUtil.getLabel("label_CustID")+ ":" + parms[1][0];
		parms[0][1] = PennantJavaUtil.getLabel("label_CustDocType")+ ":" + parms[1][1];
		return ErrorUtil.getErrorDetail(new ErrorDetails(PennantConstants.KEY_FIELD, 
				errorId, parms[0],parms[1]), userLanguage);
	}

}