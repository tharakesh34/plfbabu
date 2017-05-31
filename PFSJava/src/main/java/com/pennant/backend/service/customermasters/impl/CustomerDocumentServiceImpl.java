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
 * FileName    		:  CustomerDocumentServiceImpl.java                                                   * 	  
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

package com.pennant.backend.service.customermasters.impl;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.BeanUtils;

import com.pennant.app.util.DateUtility;
import com.pennant.app.util.ErrorUtil;
import com.pennant.backend.dao.audit.AuditHeaderDAO;
import com.pennant.backend.dao.customermasters.CustomerDAO;
import com.pennant.backend.dao.customermasters.CustomerDocumentDAO;
import com.pennant.backend.dao.documentdetails.DocumentManagerDAO;
import com.pennant.backend.model.ErrorDetails;
import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.model.customermasters.Customer;
import com.pennant.backend.model.customermasters.CustomerDocument;
import com.pennant.backend.model.documentdetails.DocumentDetails;
import com.pennant.backend.model.documentdetails.DocumentManager;
import com.pennant.backend.model.systemmasters.DocumentType;
import com.pennant.backend.service.GenericService;
import com.pennant.backend.service.customermasters.CustomerDocumentService;
import com.pennant.backend.service.customermasters.validation.CustomerDocumentValidation;
import com.pennant.backend.service.systemmasters.DocumentTypeService;
import com.pennant.backend.util.PennantConstants;

/**
 * Service implementation for methods that depends on <b>CustomerDocument</b>.<br>
 * 
 */
public class CustomerDocumentServiceImpl extends GenericService<CustomerDocument> 
							implements CustomerDocumentService {

	private static Logger logger = Logger.getLogger(CustomerDocumentServiceImpl.class);

	private AuditHeaderDAO auditHeaderDAO;
	private CustomerDAO customerDAO;
	private CustomerDocumentDAO customerDocumentDAO;
	private CustomerDocumentValidation customerDocumentValidation;
	private DocumentTypeService documentTypeService;
	private DocumentManagerDAO documentManagerDAO;

	public CustomerDocumentServiceImpl() {
		super();
	}
	
	// ******************************************************//
	// ****************** getter / setter *******************//
	// ******************************************************//

	public AuditHeaderDAO getAuditHeaderDAO() {
		return auditHeaderDAO;
	}
	public void setAuditHeaderDAO(AuditHeaderDAO auditHeaderDAO) {
		this.auditHeaderDAO = auditHeaderDAO;
	}

	public CustomerDAO getCustomerDAO() {
    	return customerDAO;
    }
	public void setCustomerDAO(CustomerDAO customerDAO) {
    	this.customerDAO = customerDAO;
    }
	
	public CustomerDocumentDAO getCustomerDocumentDAO() {
		return customerDocumentDAO;
	}
	public void setCustomerDocumentDAO(CustomerDocumentDAO customerDocumentDAO) {
		this.customerDocumentDAO = customerDocumentDAO;
	}

	public CustomerDocumentValidation getDocumentValidation(){
		
		if(customerDocumentValidation==null){
			this.customerDocumentValidation = new CustomerDocumentValidation(customerDocumentDAO);
		}
		return this.customerDocumentValidation;
	}
	
	
	/**
	 * saveOrUpdate method method do the following steps. 1) Do the Business
	 * validation by using businessValidation(auditHeader) method if there is
	 * any error or warning message then return the auditHeader. 2) Do Add or
	 * Update the Record a) Add new Record for the new record in the DB table
	 * CustomerDocuments/CustomerDocuments_Temp by using CustomerDocumentDAO's
	 * save method b) Update the Record in the table. based on the module
	 * workFlow Configuration. by using CustomerDocumentDAO's update method 3)
	 * Audit the record in to AuditHeader and AdtCustomerDocuments by using
	 * auditHeaderDAO.addAudit(auditHeader)
	 * 
	 * @param AuditHeader
	 *            (auditHeader)
	 * @return auditHeader
	 * @throws Exception
	 */
	@Override
	public AuditHeader saveOrUpdate(AuditHeader auditHeader){
		logger.debug("Entering");

		auditHeader = businessValidation(auditHeader, "saveOrUpdate");
		if (!auditHeader.isNextProcess()) {
			logger.debug("Leaving");
			return auditHeader;
		}

		String tableType = "";
		CustomerDocument customerDocument = (CustomerDocument) auditHeader
				.getAuditDetail().getModelData();

		if (customerDocument.isWorkflow()) {
			tableType = "_Temp";
		}

		if (customerDocument.isNew()) {
			customerDocument.setId(getCustomerDocumentDAO().save(customerDocument,tableType));
			auditHeader.getAuditDetail().setModelData(customerDocument);
		}else{
			getCustomerDocumentDAO().update(customerDocument,tableType);
		}
		getAuditHeaderDAO().addAudit(auditHeader);
		logger.debug("Leaving");
		return auditHeader;

	}

	/**
	 * delete method do the following steps. 1) Do the Business validation by
	 * using businessValidation(auditHeader) method if there is any error or
	 * warning message then return the auditHeader. 2) delete Record for the DB
	 * table CustomerDocuments by using CustomerDocumentDAO's delete method with
	 * type as Blank 3) Audit the record in to AuditHeader and
	 * AdtCustomerDocuments by using auditHeaderDAO.addAudit(auditHeader)
	 * 
	 * @param AuditHeader
	 *            (auditHeader)
	 * @return auditHeader
	 */
	@Override
	public AuditHeader delete(AuditHeader auditHeader) {
		logger.debug("Entering");

		auditHeader = businessValidation(auditHeader, "delete");
		if (!auditHeader.isNextProcess()) {
			logger.debug("Leaving");
			return auditHeader;
		}

		CustomerDocument customerDocument = (CustomerDocument) auditHeader
				.getAuditDetail().getModelData();

		getCustomerDocumentDAO().delete(customerDocument, "");
		getAuditHeaderDAO().addAudit(auditHeader);
		logger.debug("Leaving");
		return auditHeader;
	}

	/**
	 * getCustomerDocumentById fetch the details by using CustomerDocumentDAO's
	 * getCustomerDocumentById method.
	 * 
	 * @param id
	 *            (String)
	 * @param type
	 *            (String) ""/_Temp/_View
	 * @return CustomerDocument
	 */
	@Override
	public CustomerDocument getCustomerDocumentById(long id, String docCategory) {
		return getCustomerDocumentDAO().getCustomerDocumentById(id, docCategory, "_View");
	}

	/**
	 * getApprovedCustomerDocumentById fetch the details by using
	 * CustomerDocumentDAO's getCustomerDocumentById method . with parameter id
	 * and type as blank. it fetches the approved records from the
	 * CustomerDocuments.
	 * 
	 * @param id
	 *            (String)
	 * @return CustomerDocument
	 */
	public CustomerDocument getApprovedCustomerDocumentById(long id,
			String docType) {
		return getCustomerDocumentDAO().getCustomerDocumentById(id, docType, "_AView");
	}

	/**
	 * doApprove method do the following steps. 1) Do the Business validation by
	 * using businessValidation(auditHeader) method if there is any error or
	 * warning message then return the auditHeader. 2) based on the Record type
	 * do following actions a) DELETE Delete the record from the main table by
	 * using getCustomerDocumentDAO().delete with parameters customerDocument,""
	 * b) NEW Add new record in to main table by using
	 * getCustomerDocumentDAO().save with parameters customerDocument,"" c) EDIT
	 * Update record in the main table by using getCustomerDocumentDAO().update
	 * with parameters customerDocument,"" 3) Delete the record from the
	 * workFlow table by using getCustomerDocumentDAO().delete with parameters
	 * customerDocument,"_Temp" 4) Audit the record in to AuditHeader and
	 * AdtCustomerDocuments by using auditHeaderDAO.addAudit(auditHeader) for
	 * Work flow 5) Audit the record in to AuditHeader and AdtCustomerDocuments
	 * by using auditHeaderDAO.addAudit(auditHeader) based on the transaction
	 * Type.
	 * 
	 * @param AuditHeader
	 *            (auditHeader)
	 * @return auditHeader
	 */
	public AuditHeader doApprove(AuditHeader auditHeader) {
		logger.debug("Entering");

		String tranType = "";
		auditHeader = businessValidation(auditHeader, "doApprove");
		if (!auditHeader.isNextProcess()) {
			logger.debug("Leaving");
			return auditHeader;
				
			} 

		CustomerDocument customerDocument = new CustomerDocument();
		BeanUtils.copyProperties((CustomerDocument) auditHeader
				.getAuditDetail().getModelData(), customerDocument);

		if (customerDocument.getRecordType().equals(PennantConstants.RECORD_TYPE_DEL)) {
			tranType = PennantConstants.TRAN_DEL;
			getCustomerDocumentDAO().delete(customerDocument, "");
		} else {
			customerDocument.setRoleCode("");
			customerDocument.setNextRoleCode("");
			customerDocument.setTaskId("");
			customerDocument.setNextTaskId("");
			customerDocument.setWorkflowId(0);

			if (customerDocument.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)) {
				tranType = PennantConstants.TRAN_ADD;
				customerDocument.setRecordType("");
				DocumentManager documentManager = new DocumentManager();
				documentManager.setDocImage(customerDocument.getCustDocImage());
				customerDocument.setDocRefId(getDocumentManagerDAO().save(documentManager));
				getCustomerDocumentDAO().save(customerDocument, "");
			} else {
				tranType = PennantConstants.TRAN_UPD;
				customerDocument.setRecordType("");
				DocumentManager documentManager = new DocumentManager();
				documentManager.setDocImage(customerDocument.getCustDocImage());
				customerDocument.setDocRefId(getDocumentManagerDAO().save(documentManager));
				getCustomerDocumentDAO().update(customerDocument, "");
			}
		}
		if(!StringUtils.equals(customerDocument.getSourceId(), PennantConstants.FINSOURCE_ID_API)) {
		getCustomerDocumentDAO().delete(customerDocument, "_Temp");
		auditHeader.setAuditTranType(PennantConstants.TRAN_WF);
		getAuditHeaderDAO().addAudit(auditHeader);
		}

		auditHeader.setAuditTranType(tranType);
		auditHeader.getAuditDetail().setAuditTranType(tranType);
		auditHeader.getAuditDetail().setModelData(customerDocument);
		getAuditHeaderDAO().addAudit(auditHeader);
		logger.debug("Leaving");
		return auditHeader;
	}

	/**
	 * doReject method do the following steps. 1) Do the Business validation by
	 * using businessValidation(auditHeader) method if there is any error or
	 * warning message then return the auditHeader. 2) Delete the record from
	 * the workFlow table by using getCustomerDocumentDAO().delete with
	 * parameters customerDocument,"_Temp" 3) Audit the record in to AuditHeader
	 * and AdtCustomerDocuments by using auditHeaderDAO.addAudit(auditHeader)
	 * for Work flow
	 * 
	 * @param AuditHeader
	 *            (auditHeader)
	 * @return auditHeader
	 */
	public AuditHeader doReject(AuditHeader auditHeader) {
		logger.debug("Entering");

		auditHeader = businessValidation(auditHeader, "doReject");
		if (!auditHeader.isNextProcess()) {
			logger.debug("Leaving");
			return auditHeader;
		}

		CustomerDocument customerDocument = (CustomerDocument) auditHeader.getAuditDetail().getModelData();
		auditHeader.setAuditTranType(PennantConstants.TRAN_WF);
		getCustomerDocumentDAO().delete(customerDocument, "_Temp");

		getAuditHeaderDAO().addAudit(auditHeader);
		logger.debug("Leaving");
		return auditHeader;
	}
	
	public DocumentDetails getCustDocByCustAndDocType(final long custId, String docType){
		return getCustomerDocumentDAO().getCustDocByCustAndDocType(custId, docType, "_AView");
	}
	
	@Override
	public String getCustCRCPRById(long custId, String type){
		return getCustomerDAO().getCustCRCPRById(custId, type);
	}

	/**
	 * businessValidation method do the following steps. 1) get the details from
	 * the auditHeader. 2) fetch the details from the tables 3) Validate the
	 * Record based on the record details. 4) Validate for any business
	 * validation.
	 * 
	 * @param AuditHeader
	 *            (auditHeader)
	 * @return auditHeader
	 */
	private AuditHeader businessValidation(AuditHeader auditHeader, String method) {
		logger.debug("Entering");
		auditHeader = getDocumentValidation().documentValidation(auditHeader, method);
		auditHeader=nextProcess(auditHeader);
		logger.debug("Leaving");
		return auditHeader;
	}

	@Override
	public List<CustomerDocument> getApprovedCustomerDocumentById(long id) {
		return getCustomerDocumentDAO().getCustomerDocumentByCustomer(id, "_AView");
	}
	
	/**
	 * Method for validate customer documents and send respective error details
	 * 
	 * @param customerDocument
	 * @return AuditDetail
	 */
	@Override
	public AuditDetail validateCustomerDocuments(CustomerDocument customerDocument,Customer customer) {
		logger.debug("Entering");
		
		AuditDetail auditDetail = new AuditDetail();
		ErrorDetails errorDetail = new ErrorDetails();
		if(customerDocument !=null){
		if (customerDocument.getCustDocIssuedOn() != null && customerDocument.getCustDocExpDate() != null) {
			if (customerDocument.getCustDocIssuedOn().compareTo(customerDocument.getCustDocExpDate()) > 0) {
				String[] valueParm = new String[2];
				valueParm[0] = "custDocExpDate: " +DateUtility.formatDate(customerDocument.getCustDocExpDate(),
						PennantConstants.XMLDateFormat);
				valueParm[1] = "custDocIssuedOn: " +DateUtility.formatDate(customerDocument.getCustDocIssuedOn(),
						PennantConstants.XMLDateFormat);
				errorDetail = ErrorUtil.getErrorDetail(new ErrorDetails("90205", "", valueParm), "EN");
				auditDetail.setErrorDetail(errorDetail);
				return auditDetail;
			}
		}

		
		if(StringUtils.isBlank(customerDocument.getDocUri())) {
			if(customerDocument.getCustDocImage() ==null || customerDocument.getCustDocImage().length<=0){
				String[] valueParm = new String[2];
				valueParm[0] = "docContent";
				valueParm[1] = "docRefId";
				errorDetail = ErrorUtil.getErrorDetail(new ErrorDetails("90123", "", valueParm), "EN");
				auditDetail.setErrorDetail(errorDetail);
			} 
		}
		// validate custDocIssuedCountry
					if (StringUtils.isBlank(customerDocument.getCustDocIssuedCountry())) {
						String[] valueParm = new String[2];
						valueParm[0] = "CustDocIssuedCountry";
						errorDetail = ErrorUtil.getErrorDetail(new ErrorDetails("90502", "", valueParm), "EN");
						auditDetail.setErrorDetail(errorDetail);
						return auditDetail;
					}
		int count = getCustomerDocumentDAO().getCustCountryCount(customerDocument.getCustDocIssuedCountry());
		if (count <= 0) {
			String[] valueParm = new String[2];
			valueParm[0] = "custDocIssuedCountry";
			valueParm[1] = customerDocument.getCustDocIssuedCountry();
			errorDetail = ErrorUtil.getErrorDetail(new ErrorDetails("90701", "", valueParm), "EN");
			auditDetail.setErrorDetail(errorDetail);
		}

		DocumentType docType = documentTypeService.getDocumentTypeById(customerDocument.getCustDocCategory());
		if (docType == null) {
			String[] valueParm = new String[1];
			valueParm[0] = customerDocument.getCustDocCategory();
			errorDetail = ErrorUtil.getErrorDetail(new ErrorDetails("90401", "", valueParm), "EN");
			auditDetail.setErrorDetail(errorDetail);
			return auditDetail;
		}

		// validate Is Customer document?
		if (docType.isDocIsCustDoc()) {
			if (StringUtils.isBlank(customerDocument.getCustDocTitle())) {
				String[] valueParm = new String[2];
				valueParm[0] = "CustDocTitle";
				valueParm[1] = docType.getDocTypeCode();
				errorDetail = ErrorUtil.getErrorDetail(new ErrorDetails("90402", "", valueParm), "EN");
				auditDetail.setErrorDetail(errorDetail);
				return auditDetail;
			} else {
				customerDocument.setCustDocTitle(customerDocument.getCustDocTitle().toUpperCase());
			}
		}
		if (StringUtils.equals(customerDocument.getCustDocCategory(), "03")) {
			Pattern pattern = Pattern.compile("^[A-Za-z]{5}\\d{4}[A-Za-z]{1}");
			if(customerDocument.getCustDocTitle() !=null){
			Matcher matcher = pattern.matcher(customerDocument.getCustDocTitle());
			if(matcher.find() == false ){
				String[] valueParm = new String[0];
				errorDetail = ErrorUtil.getErrorDetail(new ErrorDetails("90251", "", valueParm), "EN");
				auditDetail.setErrorDetail(errorDetail);
				return auditDetail;
			}
			}
		}
			// validate DocIssuedAuthority
			if (docType.isDocIsCustDoc() && docType.isDocIssuedAuthorityMand()) {
			if (StringUtils.isBlank(customerDocument.getCustDocSysName())) {
				String[] valueParm = new String[2];
				valueParm[0] = "CustDocIssuedAuth";
				valueParm[1] = docType.getDocTypeCode();
				errorDetail = ErrorUtil.getErrorDetail(new ErrorDetails("90402", "", valueParm), "EN");
				auditDetail.setErrorDetail(errorDetail);
			}
		}

		// validate custDocIssuedOn
		if (docType.isDocIssueDateMand()) {
			if (customerDocument.getCustDocIssuedOn() == null) {
				String[] valueParm = new String[2];
				valueParm[0] = "CustDocIssuedOn";
				valueParm[1] = docType.getDocTypeCode();
				errorDetail = ErrorUtil.getErrorDetail(new ErrorDetails("90402", "", valueParm), "EN");
				auditDetail.setErrorDetail(errorDetail);
			}
		}

		// validate custDocExpDate
		if (docType.isDocExpDateIsMand()) {
			if (customerDocument.getCustDocExpDate() == null) {
				String[] valueParm = new String[2];
				valueParm[0] = "CustDocExpDate";
				valueParm[1] = docType.getDocTypeCode();
				errorDetail = ErrorUtil.getErrorDetail(new ErrorDetails("90402", "", valueParm), "EN");
				auditDetail.setErrorDetail(errorDetail);
			}
		}

		// validate Master code with PLF system masters
		count = getCustomerDocumentDAO().getCustCountryCount(customerDocument.getCustDocIssuedCountry());
		if (count <= 0) {
			String[] valueParm = new String[2];
			valueParm[0] = "custDocIssuedCountry";
			valueParm[1] = customerDocument.getCustDocIssuedCountry();
			errorDetail = ErrorUtil.getErrorDetail(new ErrorDetails("90701", "", valueParm), "EN");
			auditDetail.setErrorDetail(errorDetail);
			return auditDetail;
		}
		if(!(StringUtils.equals(customerDocument.getCustDocType(),PennantConstants.DOC_TYPE_PDF) 
				|| StringUtils.equals(customerDocument.getCustDocType(),PennantConstants.DOC_TYPE_DOC)
				|| StringUtils.equals(customerDocument.getCustDocType(),PennantConstants.DOC_TYPE_DOCX)
				|| StringUtils.equals(customerDocument.getCustDocType(),PennantConstants.DOC_TYPE_IMAGE))){
			String[] valueParm = new String[1];
			valueParm[0] = customerDocument.getCustDocType();
			errorDetail = ErrorUtil.getErrorDetail(new ErrorDetails("90122", "", valueParm), "EN");
			auditDetail.setErrorDetail(errorDetail);
		}
			String docFormate = customerDocument.getCustDocName()
					.substring(customerDocument.getCustDocName().lastIndexOf(".") + 1);
			if (StringUtils.equals(customerDocument.getCustDocName(), docFormate)) {
				String[] valueParm = new String[1];
				valueParm[0] = "docName: " + docFormate;
				errorDetail = ErrorUtil.getErrorDetail(new ErrorDetails("90291", "", valueParm), "EN");
				auditDetail.setErrorDetail(errorDetail);
			}
			boolean isImage = false;
			if (StringUtils.equals(customerDocument.getCustDocType(), PennantConstants.DOC_TYPE_IMAGE)) {
				if (StringUtils.equals(docFormate, "jpg") || StringUtils.equals(docFormate, "jpeg")
						|| StringUtils.equals(docFormate, "png")) {
					isImage = true;
				}
			}
			if (!isImage) {
				if (!StringUtils.equals(customerDocument.getCustDocType(), docFormate)) {
					String[] valueParm = new String[2];
					valueParm[0] = "document type: " + customerDocument.getCustDocName();
					valueParm[1] = customerDocument.getCustDocType();
					errorDetail = ErrorUtil.getErrorDetail(new ErrorDetails("90289", "", valueParm), "EN");
					auditDetail.setErrorDetail(errorDetail);
				}
			}
			if (customerDocument.getCustDocIssuedOn() != null && customer != null) {
				if (customerDocument.getCustDocIssuedOn().before(customer.getCustDOB())) {
					errorDetail = ErrorUtil.getErrorDetail(new ErrorDetails("90321", "", null), "EN");
					auditDetail.setErrorDetail(errorDetail);
					return auditDetail;
				}
			}
}	
		logger.debug("Leaving");
		return auditDetail;
		
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
		return getCustomerDocumentDAO().getVersion(custId,docType);
	}
	public void setDocumentTypeService(DocumentTypeService documentTypeService) {
		this.documentTypeService = documentTypeService;
	}
	public DocumentManagerDAO getDocumentManagerDAO() {
		return documentManagerDAO;
	}

	public void setDocumentManagerDAO(DocumentManagerDAO documentManagerDAO) {
		this.documentManagerDAO = documentManagerDAO;
	}
}