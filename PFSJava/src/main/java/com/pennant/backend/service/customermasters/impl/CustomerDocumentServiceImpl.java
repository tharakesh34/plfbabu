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
 * * FileName : CustomerDocumentServiceImpl.java * * Author : PENNANT TECHONOLOGIES * * Creation Date : 06-05-2011 * *
 * Modified Date : 06-05-2011 * * Description : * *
 ********************************************************************************************
 * Date Author Version Comments *
 ********************************************************************************************
 * 06-05-2011 Pennant 0.1 * * * * * * * * *
 ********************************************************************************************
 */

package com.pennant.backend.service.customermasters.impl;

import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.BeanUtils;

import com.pennant.app.constants.ImplementationConstants;
import com.pennant.app.util.ErrorUtil;
import com.pennant.app.util.SysParamUtil;
import com.pennant.backend.dao.audit.AuditHeaderDAO;
import com.pennant.backend.dao.customermasters.CustomerDAO;
import com.pennant.backend.dao.customermasters.CustomerDocumentDAO;
import com.pennant.backend.dao.documentdetails.DocumentManagerDAO;
import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.model.customermasters.Customer;
import com.pennant.backend.model.customermasters.CustomerDocument;
import com.pennant.backend.model.documentdetails.DocumentDetails;
import com.pennant.backend.model.systemmasters.DocumentType;
import com.pennant.backend.service.GenericService;
import com.pennant.backend.service.customermasters.CustomerDocumentService;
import com.pennant.backend.service.customermasters.validation.CustomerDocumentValidation;
import com.pennant.backend.service.masters.MasterDefService;
import com.pennant.backend.service.systemmasters.DocumentTypeService;
import com.pennant.backend.util.PennantConstants;
import com.pennanttech.model.dms.DMSModule;
import com.pennanttech.pennapps.core.model.ErrorDetail;
import com.pennanttech.pennapps.core.util.DateUtil;
import com.pennanttech.pff.documents.service.DocumentService;

/**
 * Service implementation for methods that depends on <b>CustomerDocument</b>.<br>
 * 
 */
public class CustomerDocumentServiceImpl extends GenericService<CustomerDocument> implements CustomerDocumentService {

	private static Logger logger = LogManager.getLogger(CustomerDocumentServiceImpl.class);

	private AuditHeaderDAO auditHeaderDAO;
	private CustomerDAO customerDAO;
	private CustomerDocumentDAO customerDocumentDAO;
	private CustomerDocumentValidation customerDocumentValidation;
	private DocumentTypeService documentTypeService;
	private DocumentManagerDAO documentManagerDAO;
	private MasterDefService masterDefService;
	private DocumentService documentService2;

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

	public CustomerDocumentValidation getDocumentValidation() {

		if (customerDocumentValidation == null) {
			this.customerDocumentValidation = new CustomerDocumentValidation(customerDocumentDAO);
		}
		return this.customerDocumentValidation;
	}

	/**
	 * saveOrUpdate method method do the following steps. 1) Do the Business validation by using
	 * businessValidation(auditHeader) method if there is any error or warning message then return the auditHeader. 2)
	 * Do Add or Update the Record a) Add new Record for the new record in the DB table
	 * CustomerDocuments/CustomerDocuments_Temp by using CustomerDocumentDAO's save method b) Update the Record in the
	 * table. based on the module workFlow Configuration. by using CustomerDocumentDAO's update method 3) Audit the
	 * record in to AuditHeader and AdtCustomerDocuments by using auditHeaderDAO.addAudit(auditHeader)
	 * 
	 * @param AuditHeader (auditHeader)
	 * @return auditHeader
	 * @throws Exception
	 */
	@Override
	public AuditHeader saveOrUpdate(AuditHeader auditHeader) {
		logger.debug("Entering");

		auditHeader = businessValidation(auditHeader, "saveOrUpdate");
		if (!auditHeader.isNextProcess()) {
			logger.debug("Leaving");
			return auditHeader;
		}

		String tableType = "";
		CustomerDocument customerDocument = (CustomerDocument) auditHeader.getAuditDetail().getModelData();

		if (customerDocument.isWorkflow()) {
			tableType = "_Temp";
		}

		if (customerDocument.isNewRecord()) {
			customerDocument.setCustID(getCustomerDocumentDAO().save(customerDocument, tableType));
			auditHeader.getAuditDetail().setModelData(customerDocument);
		} else {
			getCustomerDocumentDAO().update(customerDocument, tableType);
		}
		getAuditHeaderDAO().addAudit(auditHeader);
		logger.debug("Leaving");
		return auditHeader;

	}

	/**
	 * delete method do the following steps. 1) Do the Business validation by using businessValidation(auditHeader)
	 * method if there is any error or warning message then return the auditHeader. 2) delete Record for the DB table
	 * CustomerDocuments by using CustomerDocumentDAO's delete method with type as Blank 3) Audit the record in to
	 * AuditHeader and AdtCustomerDocuments by using auditHeaderDAO.addAudit(auditHeader)
	 * 
	 * @param AuditHeader (auditHeader)
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

		CustomerDocument customerDocument = (CustomerDocument) auditHeader.getAuditDetail().getModelData();

		getCustomerDocumentDAO().delete(customerDocument, "");
		getAuditHeaderDAO().addAudit(auditHeader);
		logger.debug("Leaving");
		return auditHeader;
	}

	/**
	 * getCustomerDocumentById fetch the details by using CustomerDocumentDAO's getCustomerDocumentById method.
	 * 
	 * @param id   (String)
	 * @param type (String) ""/_Temp/_View
	 * @return CustomerDocument
	 */
	@Override
	public CustomerDocument getCustomerDocumentById(long id, String docCategory) {
		return getCustomerDocumentDAO().getCustomerDocumentById(id, docCategory, "_View");
	}

	/**
	 * getApprovedCustomerDocumentById fetch the details by using CustomerDocumentDAO's getCustomerDocumentById method .
	 * with parameter id and type as blank. it fetches the approved records from the CustomerDocuments.
	 * 
	 * @param id (String)
	 * @return CustomerDocument
	 */
	public CustomerDocument getApprovedCustomerDocumentById(long id, String docType) {
		return getCustomerDocumentDAO().getCustomerDocumentById(id, docType, "_AView");
	}

	/**
	 * doApprove method do the following steps. 1) Do the Business validation by using businessValidation(auditHeader)
	 * method if there is any error or warning message then return the auditHeader. 2) based on the Record type do
	 * following actions a) DELETE Delete the record from the main table by using getCustomerDocumentDAO().delete with
	 * parameters customerDocument,"" b) NEW Add new record in to main table by using getCustomerDocumentDAO().save with
	 * parameters customerDocument,"" c) EDIT Update record in the main table by using getCustomerDocumentDAO().update
	 * with parameters customerDocument,"" 3) Delete the record from the workFlow table by using
	 * getCustomerDocumentDAO().delete with parameters customerDocument,"_Temp" 4) Audit the record in to AuditHeader
	 * and AdtCustomerDocuments by using auditHeaderDAO.addAudit(auditHeader) for Work flow 5) Audit the record in to
	 * AuditHeader and AdtCustomerDocuments by using auditHeaderDAO.addAudit(auditHeader) based on the transaction Type.
	 * 
	 * @param AuditHeader (auditHeader)
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
		BeanUtils.copyProperties((CustomerDocument) auditHeader.getAuditDetail().getModelData(), customerDocument);

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
				if (customerDocument.getCustDocImage() != null && customerDocument.getCustDocImage().length > 0) {
					saveDocument(DMSModule.CUSTOMER, null, customerDocument);
				}
				getCustomerDocumentDAO().save(customerDocument, "");
			} else {
				tranType = PennantConstants.TRAN_UPD;
				customerDocument.setRecordType("");
				if (customerDocument.getCustDocImage() != null && customerDocument.getCustDocImage().length > 0) {
					saveDocument(DMSModule.CUSTOMER, null, customerDocument);
				}
				getCustomerDocumentDAO().update(customerDocument, "");
				documentService2.resetDocumentStatus(customerDocument.getID());
			}
		}
		if (!StringUtils.equals(customerDocument.getSourceId(), PennantConstants.FINSOURCE_ID_API)) {
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
	 * doReject method do the following steps. 1) Do the Business validation by using businessValidation(auditHeader)
	 * method if there is any error or warning message then return the auditHeader. 2) Delete the record from the
	 * workFlow table by using getCustomerDocumentDAO().delete with parameters customerDocument,"_Temp" 3) Audit the
	 * record in to AuditHeader and AdtCustomerDocuments by using auditHeaderDAO.addAudit(auditHeader) for Work flow
	 * 
	 * @param AuditHeader (auditHeader)
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

	public DocumentDetails getCustDocByCustAndDocType(final long custId, String docType) {
		return getCustomerDocumentDAO().getCustDocByCustAndDocType(custId, docType, "_AView");
	}

	@Override
	public String getCustCRCPRById(long custId, String type) {
		return getCustomerDAO().getCustCRCPRById(custId, type);
	}

	/**
	 * businessValidation method do the following steps. 1) get the details from the auditHeader. 2) fetch the details
	 * from the tables 3) Validate the Record based on the record details. 4) Validate for any business validation.
	 * 
	 * @param AuditHeader (auditHeader)
	 * @return auditHeader
	 */
	private AuditHeader businessValidation(AuditHeader auditHeader, String method) {
		logger.debug("Entering");
		auditHeader = getDocumentValidation().documentValidation(auditHeader, method);
		auditHeader = nextProcess(auditHeader);
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
	public AuditDetail validateCustomerDocuments(CustomerDocument customerDocument, Customer customer) {
		logger.debug("Entering");

		AuditDetail auditDetail = new AuditDetail();
		ErrorDetail errorDetail = new ErrorDetail();
		if (customerDocument != null) {
			if (customerDocument.getCustDocIssuedOn() != null && customerDocument.getCustDocExpDate() != null) {
				if (customerDocument.getCustDocIssuedOn().compareTo(customerDocument.getCustDocExpDate()) > 0) {
					String[] valueParm = new String[2];
					valueParm[0] = "custDocExpDate: "
							+ DateUtil.format(customerDocument.getCustDocExpDate(), PennantConstants.XMLDateFormat);
					valueParm[1] = "custDocIssuedOn: "
							+ DateUtil.format(customerDocument.getCustDocIssuedOn(), PennantConstants.XMLDateFormat);
					errorDetail = ErrorUtil.getErrorDetail(new ErrorDetail("65030", "", valueParm), "EN");
					auditDetail.setErrorDetail(errorDetail);
					return auditDetail;
				}
			}

			DocumentType docType = documentTypeService.getDocumentTypeById(customerDocument.getCustDocCategory());

			if (docType == null) {
				String[] valueParm = new String[1];
				valueParm[0] = customerDocument.getCustDocCategory();
				errorDetail = ErrorUtil.getErrorDetail(new ErrorDetail("90401", "", valueParm), "EN");
				auditDetail.setErrorDetail(errorDetail);
				return auditDetail;
			}

			if (PennantConstants.PFF_CUSTCTG_INDIV.equals(customer.getCustCtgCode())
					&& PennantConstants.FORM60.equals(customerDocument.getCustDocCategory())) {
				Date addMonths = DateUtil.addMonths(customerDocument.getCustDocIssuedOn(), 72);
				if (!ImplementationConstants.RETAIL_CUST_PAN_MANDATORY
						&& (DateUtil.compare(addMonths, customerDocument.getCustDocExpDate()) < 0)) {
					String[] valueParm = new String[1];
					valueParm[0] = "Difference Between Issued On & Expiry Date Sholud be Less Than 6 Years";
					auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetail("90505", "", valueParm)));
					return auditDetail;
				}
			}

			if (docType.isDocIsMandatory()) {
				if (StringUtils.isBlank(customerDocument.getDocUri())) {
					if (customerDocument.getCustDocImage() == null || customerDocument.getCustDocImage().length <= 0) {
						String[] valueParm = new String[2];
						valueParm[0] = "docContent";
						valueParm[1] = "docRefId";
						errorDetail = ErrorUtil.getErrorDetail(new ErrorDetail("90123", "", valueParm), "EN");
						auditDetail.setErrorDetail(errorDetail);
					}
				}
				if (StringUtils.isBlank(customerDocument.getCustDocName())) {
					String[] valueParm = new String[2];
					valueParm[0] = "docName";
					errorDetail = ErrorUtil.getErrorDetail(new ErrorDetail("90502", "", valueParm), "EN");
					auditDetail.setErrorDetail(errorDetail);
					return auditDetail;
				}
				if (StringUtils.isBlank(customerDocument.getCustDocType())) {
					String[] valueParm = new String[2];
					valueParm[0] = "docFormat";
					errorDetail = ErrorUtil.getErrorDetail(new ErrorDetail("90502", "", valueParm), "EN");
					auditDetail.setErrorDetail(errorDetail);
					return auditDetail;
				}
			}

			if ((StringUtils.isNotBlank(customerDocument.getDocUri()))
					|| (customerDocument.getCustDocImage() != null && customerDocument.getCustDocImage().length > 0)) {
				if (StringUtils.isBlank(customerDocument.getCustDocType())) {
					String[] valueParm = new String[2];
					valueParm[0] = "docFormat";
					errorDetail = ErrorUtil.getErrorDetail(new ErrorDetail("90502", "", valueParm), "EN");
					auditDetail.setErrorDetail(errorDetail);
					return auditDetail;
				}
			}

			// validate custDocIssuedCountry
			if (StringUtils.isBlank(customerDocument.getCustDocIssuedCountry())) {
				String[] valueParm = new String[2];
				valueParm[0] = "CustDocIssuedCountry";
				errorDetail = ErrorUtil.getErrorDetail(new ErrorDetail("90502", "", valueParm), "EN");
				auditDetail.setErrorDetail(errorDetail);
				return auditDetail;
			}
			int count = getCustomerDocumentDAO().getCustCountryCount(customerDocument.getCustDocIssuedCountry());
			if (count <= 0) {
				String[] valueParm = new String[2];
				valueParm[0] = "custDocIssuedCountry";
				valueParm[1] = customerDocument.getCustDocIssuedCountry();
				errorDetail = ErrorUtil.getErrorDetail(new ErrorDetail("90701", "", valueParm), "EN");
				auditDetail.setErrorDetail(errorDetail);
			}

			// validate Is Customer document?
			/*
			 * if (docType.isDocIsCustDoc()) { if (StringUtils.isBlank(customerDocument.getCustDocTitle())) { String[]
			 * valueParm = new String[2]; valueParm[0] = "CustDocTitle"; valueParm[1] = docType.getDocTypeCode();
			 * errorDetail = ErrorUtil.getErrorDetail(new ErrorDetail("90402", "", valueParm), "EN");
			 * auditDetail.setErrorDetail(errorDetail); return auditDetail; } else {
			 * customerDocument.setCustDocTitle(customerDocument.getCustDocTitle ().toUpperCase()); } }
			 */
			if (docType.isDocIdNumMand()) {
				if (StringUtils.isBlank(customerDocument.getCustDocTitle())) {
					String[] valueParm = new String[2];
					valueParm[0] = "CustDocTitle";
					valueParm[1] = docType.getDocTypeCode();
					errorDetail = ErrorUtil.getErrorDetail(new ErrorDetail("90402", "", valueParm), "EN");
					auditDetail.setErrorDetail(errorDetail);
					return auditDetail;
				} else {
					customerDocument.setCustDocTitle(customerDocument.getCustDocTitle().toUpperCase());
				}
			}

			if (StringUtils.equals(customerDocument.getCustDocCategory(), "03")) {
				Pattern pattern = Pattern.compile("^[A-Za-z]{5}\\d{4}[A-Za-z]{1}");
				if (customerDocument.getCustDocTitle() != null) {
					Matcher matcher = pattern.matcher(customerDocument.getCustDocTitle());
					if (!matcher.matches()) {
						String[] valueParm = new String[0];
						errorDetail = ErrorUtil.getErrorDetail(new ErrorDetail("90251", "", valueParm), "EN");
						auditDetail.setErrorDetail(errorDetail);
						return auditDetail;
					}
				}
			}
			// validate DocIssuedAuthority
			if (docType.isDocIssuedAuthorityMand()) {
				if (StringUtils.isBlank(customerDocument.getCustDocSysName())) {
					String[] valueParm = new String[2];
					valueParm[0] = "CustDocIssuedAuth";
					valueParm[1] = docType.getDocTypeCode();
					errorDetail = ErrorUtil.getErrorDetail(new ErrorDetail("90402", "", valueParm), "EN");
					auditDetail.setErrorDetail(errorDetail);
				}
			}

			// validate custDocIssuedOn
			if (docType.isDocIssueDateMand()) {
				if (customerDocument.getCustDocIssuedOn() == null) {
					String[] valueParm = new String[2];
					valueParm[0] = "CustDocIssuedOn";
					valueParm[1] = docType.getDocTypeCode();
					errorDetail = ErrorUtil.getErrorDetail(new ErrorDetail("90402", "", valueParm), "EN");
					auditDetail.setErrorDetail(errorDetail);
				}
			}

			// validate custDocExpDate
			if (docType.isDocExpDateIsMand()) {
				if (customerDocument.getCustDocExpDate() == null) {
					String[] valueParm = new String[2];
					valueParm[0] = "CustDocExpDate";
					valueParm[1] = docType.getDocTypeCode();
					errorDetail = ErrorUtil.getErrorDetail(new ErrorDetail("90402", "", valueParm), "EN");
					auditDetail.setErrorDetail(errorDetail);
				}
			}

			// validate Master code with PLF system masters
			count = getCustomerDocumentDAO().getCustCountryCount(customerDocument.getCustDocIssuedCountry());
			if (count <= 0) {
				String[] valueParm = new String[2];
				valueParm[0] = "custDocIssuedCountry";
				valueParm[1] = customerDocument.getCustDocIssuedCountry();
				errorDetail = ErrorUtil.getErrorDetail(new ErrorDetail("90701", "", valueParm), "EN");
				auditDetail.setErrorDetail(errorDetail);
				return auditDetail;
			}

			if (StringUtils.isBlank(customerDocument.getCustDocName())) {
				String[] valueParm = new String[2];
				valueParm[0] = "Document Name";
				errorDetail = ErrorUtil.getErrorDetail(new ErrorDetail("90502", "", valueParm), "EN");
				auditDetail.setErrorDetail(errorDetail);
				return auditDetail;
			}

			String docName = Objects.toString(customerDocument.getCustDocName(), "").toLowerCase();
			if (customerDocument.isDocIsMandatory() || StringUtils.isNotBlank(customerDocument.getCustDocType())) {
				String custDoc = customerDocument.getCustDocType();
				if (!(PennantConstants.DOC_TYPE_PDF.equals(custDoc) || PennantConstants.DOC_TYPE_DOC.equals(custDoc)
						|| PennantConstants.DOC_TYPE_DOCX.equals(custDoc)
						|| PennantConstants.DOC_TYPE_IMAGE.equals(custDoc)
						|| PennantConstants.DOC_TYPE_ZIP.equals(custDoc) || PennantConstants.DOC_TYPE_7Z.equals(custDoc)
						|| PennantConstants.DOC_TYPE_RAR.equals(custDoc)
						|| PennantConstants.DOC_TYPE_EXCEL.equals(custDoc)
						|| PennantConstants.DOC_TYPE_TXT.equals(custDoc))) {
					String[] valueParm = new String[1];
					valueParm[0] = customerDocument.getCustDocType();
					errorDetail = ErrorUtil.getErrorDetail(new ErrorDetail("90122", "", valueParm), "EN");
					auditDetail.setErrorDetail(errorDetail);
				}
			}

			if (StringUtils.isNotBlank(docName) || customerDocument.isDocIsMandatory()) {
				boolean isImage = false;
				if (StringUtils.equals(customerDocument.getCustDocType(), PennantConstants.DOC_TYPE_IMAGE)) {
					isImage = true;
					if (!docName.endsWith(".jpg") && !docName.endsWith(".jpeg") && !docName.endsWith(".png")) {
						String[] valueParm = new String[2];
						valueParm[0] = "document type: " + customerDocument.getCustDocName();
						valueParm[1] = customerDocument.getCustDocType();
						errorDetail = ErrorUtil.getErrorDetail(new ErrorDetail("90289", "", valueParm), "EN");
						auditDetail.setErrorDetail(errorDetail);
					}
				}

				// if docName has no extension.
				if (!docName.contains(".")) {
					String[] valueParm = new String[1];
					valueParm[0] = "docName: " + docName;
					errorDetail = ErrorUtil.getErrorDetail(new ErrorDetail("90291", "", valueParm), "EN");
					auditDetail.setErrorDetail(errorDetail);
				} else {
					// document name is only extension
					String docNameExtension = docName.substring(docName.lastIndexOf("."));
					if (StringUtils.equalsIgnoreCase(customerDocument.getCustDocName(), docNameExtension)) {
						String[] valueParm = new String[1];
						valueParm[0] = "docName: ";
						errorDetail = ErrorUtil.getErrorDetail(new ErrorDetail("90502", "", valueParm), "EN");
						auditDetail.setErrorDetail(errorDetail);
					}
				}
				String docExtension = docName.substring(docName.lastIndexOf(".") + 1);
				// if doc type and doc Extension are invalid
				if (!isImage) {
					if (StringUtils.equalsIgnoreCase(customerDocument.getCustDocType(),
							PennantConstants.DOC_TYPE_EXCEL)) {
						String docExtention = customerDocument.getCustDocName().toLowerCase();
						if (!docExtention.endsWith(".xls") && !docExtention.endsWith(".xlsx")) {
							String[] valueParm = new String[2];
							valueParm[0] = "document type: " + docName;
							valueParm[1] = customerDocument.getCustDocType();
							errorDetail = ErrorUtil.getErrorDetail(new ErrorDetail("90289", "", valueParm), "EN");
							auditDetail.setErrorDetail(errorDetail);
						}
					} else {
						if (!StringUtils.equalsIgnoreCase(customerDocument.getCustDocType(), docExtension)) {
							String[] valueParm = new String[2];
							valueParm[0] = "document type: " + customerDocument.getCustDocName();
							valueParm[1] = customerDocument.getCustDocType();
							errorDetail = ErrorUtil.getErrorDetail(new ErrorDetail("90289", "", valueParm), "EN");
							auditDetail.setErrorDetail(errorDetail);
						}
					}
				}
			}

			Date appStartDate = SysParamUtil.getAppDate();
			Date endDate = DateUtil.addDays(SysParamUtil.getValueAsDate("APP_DFT_END_DATE"), -1);
			Date startDate = null;
			if (customerDocument.getCustDocIssuedOn() != null && customer != null) {
				startDate = DateUtil.addDays(appStartDate, -1);
				if (customerDocument.getCustDocIssuedOn().before(customer.getCustDOB())
						|| customerDocument.getCustDocIssuedOn().after(startDate)) {
					String[] valueParm = new String[3];
					valueParm[0] = "custDocIssuedOn";
					valueParm[1] = DateUtil.format(customer.getCustDOB(), PennantConstants.XMLDateFormat);
					valueParm[2] = DateUtil.format(startDate, PennantConstants.XMLDateFormat);
					errorDetail = ErrorUtil.getErrorDetail(new ErrorDetail("90318", "", valueParm), "EN");
					auditDetail.setErrorDetail(errorDetail);
					return auditDetail;
				}
			}

			// {CustDocExpDate} should after {appStartDate} and before
			// {endDate}.
			if (customerDocument.getCustDocExpDate() != null && customer != null) {
				startDate = DateUtil.addDays(appStartDate, 1);
				if (customerDocument.getCustDocExpDate().before(startDate)
						|| customerDocument.getCustDocExpDate().after(endDate)) {
					String[] valueParm = new String[3];
					valueParm[0] = "custDocExpDate";
					valueParm[1] = DateUtil.format(startDate, PennantConstants.XMLDateFormat);
					valueParm[2] = DateUtil.format(endDate, PennantConstants.XMLDateFormat);
					errorDetail = ErrorUtil.getErrorDetail(new ErrorDetail("90318", "", valueParm), "EN");
					auditDetail.setErrorDetail(errorDetail);
					return auditDetail;
				}
			}

			if (docType.isDocIsPasswordProtected()) {
				if (StringUtils.isBlank(customerDocument.getPdfPassWord())) {
					String[] valueParm = new String[1];
					valueParm[0] = "docPassword";
					errorDetail = ErrorUtil.getErrorDetail(new ErrorDetail("90502", "", valueParm), "EN");
					auditDetail.setErrorDetail(errorDetail);
					return auditDetail;
				}
			}
		}
		logger.debug("Leaving");
		return auditDetail;
	}

	@Override
	public List<Customer> getCustIdByDocTitle(String custDocTitle) {
		return customerDocumentDAO.getCustIdByDocTitle(custDocTitle);
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
		return getCustomerDocumentDAO().getVersion(custId, docType);
	}

	@Override
	public boolean getCustomerDocExists(long custId, String docType) {
		return customerDocumentDAO.getCustomerDocExists(custId, docType);
	}

	@Override
	public String getDocTypeByMasterDefByCode(String masterType, String keyCode) {
		return masterDefService.getMasterKeyTypeByCode(masterType, keyCode);
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

	public void setMasterDefService(MasterDefService masterDefService) {
		this.masterDefService = masterDefService;
	}

	public void setDocumentService2(DocumentService documentService2) {
		this.documentService2 = documentService2;
	}

}