package com.pennanttech.pff.document.impl;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import com.pennant.app.util.DateUtility;
import com.pennant.app.util.ErrorUtil;
import com.pennant.backend.dao.audit.AuditHeaderDAO;
import com.pennant.backend.dao.documentdetails.DocumentDetailsDAO;
import com.pennant.backend.dao.documentdetails.DocumentManagerDAO;
import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.model.documentdetails.DocumentDetails;
import com.pennant.backend.model.documentdetails.DocumentManager;
import com.pennant.backend.model.finance.FinanceDetail;
import com.pennant.backend.model.systemmasters.DocumentType;
import com.pennant.backend.service.GenericService;
import com.pennant.backend.service.systemmasters.DocumentTypeService;
import com.pennant.backend.util.FinanceConstants;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantJavaUtil;
import com.pennanttech.pennapps.core.model.ErrorDetail;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pff.core.TableType;
import com.pennanttech.pff.document.DocumentService;

public class DocumentServiceImpl extends GenericService<DocumentDetails> implements DocumentService {
	private static Logger		logger	= Logger.getLogger(DocumentServiceImpl.class);

	private DocumentDetailsDAO	documentDetailsDAO;
	private AuditHeaderDAO		auditHeaderDAO;
	private DocumentManagerDAO	documentManagerDAO;
	
	// services
	private DocumentTypeService	documentTypeService;


	/**
	 * Method for validate and do below actions<br>.
	 *  - Save in case of new record<br>.
	 *  - Update if already exists.
	 */
	@Override
	public AuditHeader saveOrUpdate(AuditHeader auditHeader) {
		logger.debug("Entering");

		auditHeader = businessValidation(auditHeader, "saveOrUpdate");
		if (!auditHeader.isNextProcess()) {
			logger.debug("Leaving");
			return auditHeader;
		}

		String tableType = TableType.MAIN_TAB.getSuffix();
		DocumentDetails documentDetail = (DocumentDetails) auditHeader.getAuditDetail().getModelData();

		if (documentDetail.isWorkflow()) {
			tableType = TableType.TEMP_TAB.getSuffix();
		}

		if (documentDetail.isNew()) {
			/**
			 * Save the document (documentDetails object) into DocumentManagerTable 
			 * using documentManagerDAO.save(?) get the long Id<br>.
			 * 
			 * This will be used in the getDocumentDetailsDAO().save, Update & delete methods
			 */
			if (documentDetail.getDocRefId() <= 0 && documentDetail.getDocImage() != null) {
				DocumentManager documentManager = new DocumentManager();
				documentManager.setDocImage(documentDetail.getDocImage());
				documentDetail.setDocRefId(documentManagerDAO.save(documentManager));
			}
			// save
			documentDetail.setId(documentDetailsDAO.save(documentDetail, tableType));
		} else {
			if (documentDetail.getDocRefId() <= 0 && documentDetail.getDocImage() != null) {
				DocumentManager documentManager = new DocumentManager();
				documentManager.setDocImage(documentDetail.getDocImage());
				documentDetail.setDocRefId(documentManagerDAO.save(documentManager));
			}
			
			// update
			documentDetailsDAO.update(documentDetail, tableType);
		}
		
		auditHeader.getAuditDetail().setModelData(documentDetail);
		auditHeaderDAO.addAudit(auditHeader);

		logger.debug("Leaving");
		return auditHeader;
	}

	/**
	 * businessValidation method do the following steps. 1) get the details from the auditHeader. 2) fetch the details
	 * from the tables 3) Validate the Record based on the record details. 4) Validate for any business validation.
	 * 
	 * @param AuditHeader
	 *            (auditHeader)
	 * @return auditHeader
	 */
	private AuditHeader businessValidation(AuditHeader auditHeader, String method) {
		logger.debug("Entering");
		AuditDetail auditDetail = validate(auditHeader.getAuditDetail(), method);
		auditHeader.setAuditDetail(auditDetail);
		auditHeader = nextProcess(auditHeader);
		logger.debug("Leaving");
		return auditHeader;
	}

	/**
	 * Method for perform business validations
	 * 
	 * @param auditDetail
	 * @param method
	 * @return
	 */
	private AuditDetail validate(AuditDetail auditDetail, String method) {
		logger.debug(Literal.ENTERING);

		DocumentDetails documentDetail = (DocumentDetails) auditDetail.getModelData();
		DocumentDetails tempDocumentDetail = null;
		if (documentDetail.isWorkflow()) {
			tempDocumentDetail = documentDetailsDAO.getDocumentDetails(documentDetail.getReferenceId(),
					documentDetail.getDocCategory(), FinanceConstants.MODULE_NAME, TableType.TEMP_TAB.getSuffix());
		}

		DocumentDetails befCustomerDocument = documentDetailsDAO.getDocumentDetails(documentDetail.getReferenceId(),
				documentDetail.getDocCategory(), FinanceConstants.MODULE_NAME, TableType.MAIN_TAB.getSuffix());

		DocumentDetails oldDocumentDetail = documentDetail.getBefImage();

		String[] valueParm = new String[2];
		String[] errParm = new String[2];

		valueParm[0] = StringUtils.trimToEmpty(documentDetail.getReferenceId());
		valueParm[1] = documentDetail.getDocCategory();

		errParm[0] = PennantJavaUtil.getLabel("DocumentDetails") + " , " + PennantJavaUtil.getLabel("label_CustCIF")
				+ ":" + valueParm[0] + " and ";
		errParm[1] = PennantJavaUtil.getLabel("CustDocType_label") + "-" + valueParm[1];

		if (documentDetail.isNew()) { // for New record or new record into work flow
			if (!documentDetail.isWorkflow()) {// With out Work flow only new
				if (befCustomerDocument != null) { // Record Already Exists in the table then error
					auditDetail.setErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD, "41001", errParm, null));
				}
			} else { // with work flow
				if (documentDetail.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)) { // if records type is new
					if (befCustomerDocument != null || tempDocumentDetail != null) {
						//if records already exists in the main table
						auditDetail
								.setErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD, "41001", errParm, null));
					}
				} else { // if records not exists in the Main flow table
					if (befCustomerDocument == null || tempDocumentDetail != null) {
						auditDetail
								.setErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD, "41005", errParm, null));
					}
				}
			}
		} else {
			// for work flow process records or (Record to update or Delete with
			// out work flow)
			if (!documentDetail.isWorkflow()) { // With out Work flow for update and delete
				if (befCustomerDocument == null) { // if records not exists in the main table
					auditDetail.setErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD, "41002", errParm, null));
				}

				if (befCustomerDocument != null && oldDocumentDetail != null
						&& !oldDocumentDetail.getLastMntOn().equals(befCustomerDocument.getLastMntOn())) {
					if (StringUtils.trimToEmpty(auditDetail.getAuditTranType())
							.equalsIgnoreCase(PennantConstants.TRAN_DEL)) {
						auditDetail
								.setErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD, "41003", errParm, null));
					} else {
						auditDetail
								.setErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD, "41004", errParm, null));
					}
				}
			} else {
				if (tempDocumentDetail == null) { // if records not exists in the Work flow table
					auditDetail.setErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD, "41005", errParm, null));
				}
				if (tempDocumentDetail != null && oldDocumentDetail != null
						&& !oldDocumentDetail.getLastMntOn().equals(tempDocumentDetail.getLastMntOn())) {
					auditDetail.setErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD, "41005", errParm, null));
				}
			}
		}

		auditDetail.setErrorDetails(ErrorUtil.getErrorDetails(auditDetail.getErrorDetails(), "EN"));

		if ("doApprove".equals(StringUtils.trimToEmpty(method)) || !documentDetail.isWorkflow()) {
			documentDetail.setBefImage(befCustomerDocument);
		}

		logger.debug(Literal.LEAVING);
		return auditDetail;
	}
	
	/**
	 * Method for validate Finance document details
	 * 
	 * @param financeDetail
	 * @return List<ErrorDetail>
	 */
	@Override
	public List<ErrorDetail> validateFinanceDocuments(FinanceDetail financeDetail) {
		logger.debug(Literal.ENTERING);

		List<ErrorDetail> errorDetails = new ArrayList<ErrorDetail>();
		// validate document details
		List<DocumentDetails> documentDetails = financeDetail.getDocumentDetailsList();

		if (documentDetails != null) {
			for (DocumentDetails detail : documentDetails) {
				DocumentType docType = documentTypeService.getDocumentTypeById(detail.getDocCategory());
				if (docType == null) {
					String[] valueParm = new String[1];
					valueParm[0] = detail.getDocCategory();
					errorDetails.add(ErrorUtil.getErrorDetail(new ErrorDetail("90401", valueParm)));
					return errorDetails;
				} else {
					if(docType.isDocIsCustDoc()) {
						String[] valueParm = new String[1];
						valueParm[0] = detail.getDocCategory();
						errorDetails.add(ErrorUtil.getErrorDetail(new ErrorDetail("90408", valueParm)));
						return errorDetails;
					}
				}

				//validate Dates
				if (detail.getCustDocIssuedOn() != null && detail.getCustDocExpDate() != null) {
					if (detail.getCustDocIssuedOn().compareTo(detail.getCustDocExpDate()) > 0) {
						String[] valueParm = new String[2];
						valueParm[0] = "custDocExpDate: " +DateUtility.formatDate(detail.getCustDocExpDate(),
								PennantConstants.XMLDateFormat);
						valueParm[1] = "custDocIssuedOn: " +DateUtility.formatDate(detail.getCustDocIssuedOn(),
								PennantConstants.XMLDateFormat);
						errorDetails.add(ErrorUtil.getErrorDetail(new ErrorDetail("65030", valueParm)));
						return errorDetails;
					}
				}

				// validate finance documents
				if (!docType.isDocIsCustDoc() && docType.isDocIsMandatory()) {
					if (StringUtils.isBlank(detail.getDocUri())) {
						if (detail.getDocImage() == null || detail.getDocImage().length <= 0) {
							String[] valueParm = new String[2];
							valueParm[0] = "docContent";
							valueParm[1] = "docRefId";
							errorDetails.add(ErrorUtil.getErrorDetail(new ErrorDetail("90123", valueParm)));
						}
					}
					if (detail.getDocImage() != null || StringUtils.isNotBlank(detail.getDocUri())) {
						if( StringUtils.isBlank(detail.getDocName())) {
							String[] valueParm = new String[1];
							valueParm[0] = "docName";
							errorDetails.add(ErrorUtil.getErrorDetail(new ErrorDetail("90502", valueParm)));
						}
					}
					if (StringUtils.isBlank(detail.getDoctype())) {
						String[] valueParm = new String[1];
						valueParm[0] = "docFormat";
						errorDetails.add(ErrorUtil.getErrorDetail(new ErrorDetail("90502", valueParm)));
					} else if(!StringUtils.equalsIgnoreCase(detail.getDoctype(), "jpg") 
							&& !StringUtils.equalsIgnoreCase(detail.getDoctype(), "png")
							&& !StringUtils.equalsIgnoreCase(detail.getDoctype(), "pdf")) {
						String[] valueParm = new String[1];
						valueParm[0] = "docFormat, Available formats are jpg,png,PDF";
						errorDetails.add(ErrorUtil.getErrorDetail(new ErrorDetail("90122", valueParm)));
					}

					//TODO: Need to add password protected field in documentdetails
				}
			}
		}
		logger.debug(Literal.LEAVING);

		return errorDetails;
	}

	public void setDocumentDetailsDAO(DocumentDetailsDAO documentDetailsDAO) {
		this.documentDetailsDAO = documentDetailsDAO;
	}

	public void setAuditHeaderDAO(AuditHeaderDAO auditHeaderDAO) {
		this.auditHeaderDAO = auditHeaderDAO;
	}
	
	public void setDocumentManagerDAO(DocumentManagerDAO documentManagerDAO) {
		this.documentManagerDAO = documentManagerDAO;
	}

	public void setDocumentTypeService(DocumentTypeService documentTypeService) {
		this.documentTypeService = documentTypeService;
	}
}
