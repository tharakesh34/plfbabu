package com.pennanttech.pff.document.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.BeanUtils;

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
import com.pennanttech.pennapps.pff.document.DocumentCategories;
import com.pennanttech.pff.core.TableType;
import com.pennanttech.pff.document.DocumentService;
import com.pennanttech.pff.documents.dao.DocumentDao;
import com.pennanttech.pff.documents.model.DocumentStatusDetail;

public class DocumentServiceImpl extends GenericService<DocumentDetails> implements DocumentService {
	private static Logger logger = LogManager.getLogger(DocumentServiceImpl.class);

	private DocumentDetailsDAO documentDetailsDAO;
	private AuditHeaderDAO auditHeaderDAO;
	private DocumentManagerDAO documentManagerDAO;
	private DocumentDao documentDao;
	// services
	private DocumentTypeService documentTypeService;

	/**
	 * Method for validate and do below actions<br>
	 * . - Save in case of new record<br>
	 * . - Update if already exists.
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

		if (documentDetail.isNewRecord()) {
			/**
			 * Save the document (documentDetails object) into DocumentManagerTable using documentManagerDAO.save(?) get
			 * the long Id<br>
			 * .
			 * 
			 * This will be used in the getDocumentDetailsDAO().save, Update & delete methods
			 */

			if (documentDetail.getDocRefId() != null && documentDetail.getDocImage() != null) {
				DocumentManager documentManager = new DocumentManager();
				documentManager.setDocImage(documentDetail.getDocImage());
				documentDetail.setDocRefId(documentManagerDAO.save(documentManager));
			}
			// save
			documentDetail.setId(documentDetailsDAO.save(documentDetail, tableType));
		} else {

			if (documentDetail.getDocRefId() != null && documentDetail.getDocImage() != null) {
				DocumentManager documentManager = new DocumentManager();
				documentManager.setDocImage(documentDetail.getDocImage());
				documentDetail.setDocRefId(documentManagerDAO.save(documentManager));
			}

			// update
			documentDetailsDAO.update(documentDetail, tableType);

			if (PennantConstants.RECORD_TYPE_UPD.equals(documentDetail.getRecordType())) {
				resetDocumentStatus(documentDetail.getDocId());
			}
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
	 * @param AuditHeader (auditHeader)
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

		if (documentDetail.isNewRecord()) { // for New record or new record into work flow
			if (!documentDetail.isWorkflow()) {// With out Work flow only new
				if (befCustomerDocument != null) { // Record Already Exists in the table then error
					auditDetail.setErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD, "41001", errParm, null));
				}
			} else { // with work flow
				if (documentDetail.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)) { // if records type is new
					if (befCustomerDocument != null || tempDocumentDetail != null) {
						// if records already exists in the main table
						auditDetail.setErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD, "41001", errParm, null));
					}
				} else { // if records not exists in the Main flow table
					if (befCustomerDocument == null || tempDocumentDetail != null) {
						auditDetail.setErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD, "41005", errParm, null));
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
						auditDetail.setErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD, "41003", errParm, null));
					} else {
						auditDetail.setErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD, "41004", errParm, null));
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
					if (DocumentCategories.CUSTOMER.getKey().equals(docType.getCategoryCode())) {
						String[] valueParm = new String[1];
						valueParm[0] = detail.getDocCategory();
						errorDetails.add(ErrorUtil.getErrorDetail(new ErrorDetail("90408", valueParm)));
						return errorDetails;
					}
				}

				// validate Dates
				if (detail.getCustDocIssuedOn() != null && detail.getCustDocExpDate() != null) {
					if (detail.getCustDocIssuedOn().compareTo(detail.getCustDocExpDate()) > 0) {
						String[] valueParm = new String[2];
						valueParm[0] = "custDocExpDate: "
								+ DateUtility.format(detail.getCustDocExpDate(), PennantConstants.XMLDateFormat);
						valueParm[1] = "custDocIssuedOn: "
								+ DateUtility.format(detail.getCustDocIssuedOn(), PennantConstants.XMLDateFormat);
						errorDetails.add(ErrorUtil.getErrorDetail(new ErrorDetail("65030", valueParm)));
						return errorDetails;
					}
				}

				// docType is mandatory in XML Level
				if (StringUtils.isNotBlank(detail.getDoctype())) {
					String documentType = detail.getDoctype();
					if (!(PennantConstants.DOC_TYPE_PDF.equals(documentType)
							|| PennantConstants.DOC_TYPE_DOC.equals(documentType)
							|| PennantConstants.DOC_TYPE_DOCX.equals(documentType)
							|| PennantConstants.DOC_TYPE_IMAGE.equals(documentType)
							|| PennantConstants.DOC_TYPE_ZIP.equals(documentType)
							|| PennantConstants.DOC_TYPE_7Z.equals(documentType)
							|| PennantConstants.DOC_TYPE_RAR.equals(documentType)
							|| PennantConstants.DOC_TYPE_EXCEL.equals(documentType)
							|| PennantConstants.DOC_TYPE_TXT.equals(documentType))) {
						String[] valueParm = new String[1];
						valueParm[0] = detail.getDoctype();
						errorDetails.add(ErrorUtil.getErrorDetail(new ErrorDetail("90122", "", valueParm), "EN"));
					}
				}

				// docName is mandatory in XML Level
				if (StringUtils.isNotBlank(detail.getDocName()) && StringUtils.isNotBlank(detail.getDoctype())) {
					String docName = detail.getDocName().toLowerCase();
					boolean isImage = false;
					if (StringUtils.equals(detail.getDoctype(), PennantConstants.DOC_TYPE_IMAGE)) {
						isImage = true;
						if (!docName.endsWith(".jpg") && !docName.endsWith(".jpeg") && !docName.endsWith(".png")) {
							String[] valueParm = new String[2];
							valueParm[0] = "document type: " + docName;
							valueParm[1] = detail.getDoctype();
							errorDetails.add(ErrorUtil.getErrorDetail(new ErrorDetail("90289", "", valueParm), "EN"));
						}
					}

					// if docName has no extension.
					if (!docName.contains(".")) {
						String[] valueParm = new String[1];
						valueParm[0] = "docName: " + docName;
						errorDetails.add(ErrorUtil.getErrorDetail(new ErrorDetail("90291", "", valueParm), "EN"));

					} else {
						// document name is only extension
						String docNameExtension = docName.substring(docName.lastIndexOf("."));
						if (StringUtils.equalsIgnoreCase(docName, docNameExtension)) {
							String[] valueParm = new String[1];
							valueParm[0] = "docName: ";
							errorDetails.add(ErrorUtil.getErrorDetail(new ErrorDetail("90502", "", valueParm), "EN"));

						}
					}
					String docExtension = docName.substring(docName.lastIndexOf(".") + 1);
					// if doc type and doc Extension are invalid
					if (!isImage) {
						if (StringUtils.equalsIgnoreCase(detail.getDoctype(), PennantConstants.DOC_TYPE_EXCEL)) {
							String docExtention = detail.getDocName().toLowerCase();
							if (!docExtention.endsWith(".xls") && !docExtention.endsWith(".xlsx")) {
								String[] valueParm = new String[2];
								valueParm[0] = "document type: " + docName;
								valueParm[1] = detail.getDoctype();
								errorDetails
										.add(ErrorUtil.getErrorDetail(new ErrorDetail("90289", "", valueParm), "EN"));
							}
						} else {
							if (!StringUtils.equalsIgnoreCase(detail.getDoctype(), docExtension)) {
								String[] valueParm = new String[2];
								valueParm[0] = "document type: " + docName;
								valueParm[1] = detail.getDoctype();
								errorDetails
										.add(ErrorUtil.getErrorDetail(new ErrorDetail("90289", "", valueParm), "EN"));
							}
						}
					}
				}

				// validate finance documents
				if (!(DocumentCategories.CUSTOMER.getKey().equals(docType.getCategoryCode()))
						&& docType.isDocIsMandatory()) {
					if (StringUtils.isBlank(detail.getDocUri())) {
						if (detail.getDocImage() == null || detail.getDocImage().length <= 0) {
							String[] valueParm = new String[2];
							valueParm[0] = "docContent";
							valueParm[1] = "docRefId";
							errorDetails.add(ErrorUtil.getErrorDetail(new ErrorDetail("90123", valueParm)));
						}
					}
					if (detail.getDocImage() != null || StringUtils.isNotBlank(detail.getDocUri())) {
						if (StringUtils.isBlank(detail.getDocName())) {
							String[] valueParm = new String[1];
							valueParm[0] = "docName";
							errorDetails.add(ErrorUtil.getErrorDetail(new ErrorDetail("90502", valueParm)));
						}
					}
					if (StringUtils.isBlank(detail.getDoctype())) {
						String[] valueParm = new String[1];
						valueParm[0] = "docFormat";
						errorDetails.add(ErrorUtil.getErrorDetail(new ErrorDetail("90502", valueParm)));
					}
				}
			}
		}
		logger.debug(Literal.LEAVING);

		return errorDetails;
	}

	@Override
	public AuditDetail doDocumentValidation(DocumentDetails detail) {

		logger.debug(Literal.ENTERING);

		AuditDetail auditDetail = new AuditDetail();

		// validate document details
		DocumentType docType = documentTypeService.getDocumentTypeById(detail.getDocCategory());
		if (docType == null) {
			String[] valueParm = new String[1];
			valueParm[0] = detail.getDocCategory();
			auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetail("90401", valueParm)));
			return auditDetail;
		}

		// validate Dates
		if (detail.getCustDocIssuedOn() != null && detail.getCustDocExpDate() != null) {
			if (detail.getCustDocIssuedOn().compareTo(detail.getCustDocExpDate()) > 0) {
				String[] valueParm = new String[2];
				valueParm[0] = "custDocExpDate: "
						+ DateUtility.format(detail.getCustDocExpDate(), PennantConstants.XMLDateFormat);
				valueParm[1] = "custDocIssuedOn: "
						+ DateUtility.format(detail.getCustDocIssuedOn(), PennantConstants.XMLDateFormat);
				auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetail("65030", valueParm)));
				return auditDetail;
			}
		}

		// docType is mandatory in XML Level
		if (StringUtils.isNotBlank(detail.getDoctype())) {
			if (!(StringUtils.equals(detail.getDoctype(), PennantConstants.DOC_TYPE_PDF)
					|| StringUtils.equals(detail.getDoctype(), PennantConstants.DOC_TYPE_DOC)
					|| StringUtils.equals(detail.getDoctype(), PennantConstants.DOC_TYPE_DOCX)
					|| StringUtils.equals(detail.getDoctype(), PennantConstants.DOC_TYPE_IMAGE)
					|| StringUtils.equals(detail.getDoctype(), "JPG") || StringUtils.equals(detail.getDoctype(), "JPEG")
					|| StringUtils.equals(detail.getDoctype(), "jpg") || StringUtils.equals(detail.getDoctype(), "PNG")
					|| StringUtils.equals(detail.getDoctype(), PennantConstants.DOC_TYPE_ZIP)
					|| StringUtils.equals(detail.getDoctype(), PennantConstants.DOC_TYPE_7Z)
					|| StringUtils.equals(detail.getDoctype(), PennantConstants.DOC_TYPE_RAR))) {
				String[] valueParm = new String[1];
				valueParm[0] = detail.getDoctype();
				auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetail("90122", "", valueParm), "EN"));
				return auditDetail;
			}
		}

		// docName is mandatory in XML Level
		if (StringUtils.isNotBlank(detail.getDocName()) && StringUtils.isNotBlank(detail.getDoctype())) {
			String docName = detail.getDocName().toLowerCase();
			boolean isImage = false;
			if (StringUtils.equals(detail.getDoctype(), PennantConstants.DOC_TYPE_IMAGE)) {
				isImage = true;
				if (!docName.endsWith(".jpg") && !docName.endsWith(".jpeg") && !docName.endsWith(".png")) {
					String[] valueParm = new String[2];
					valueParm[0] = "document type: " + docName;
					valueParm[1] = detail.getDoctype();
					auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetail("90289", "", valueParm), "EN"));
					return auditDetail;
				}
			}

			// if docName has no extension.
			if (!docName.contains(".")) {
				String[] valueParm = new String[1];
				valueParm[0] = "docName: " + docName;
				auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetail("90291", "", valueParm), "EN"));
				return auditDetail;

			} else {
				// document name is only extension
				String docNameExtension = docName.substring(docName.lastIndexOf("."));
				if (StringUtils.equalsIgnoreCase(docName, docNameExtension)) {
					String[] valueParm = new String[1];
					valueParm[0] = "docName: ";
					auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetail("90407", "", valueParm), "EN"));
					return auditDetail;
				}
			}
			String docExtension = docName.substring(docName.lastIndexOf(".") + 1);
			// if doc type and doc Extension are invalid
			if (!isImage) {
				if (!StringUtils.equalsIgnoreCase(detail.getDoctype(), docExtension)) {
					String[] valueParm = new String[2];
					valueParm[0] = "document type: " + docName;
					valueParm[1] = detail.getDoctype();
					auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetail("90289", "", valueParm), "EN"));
					return auditDetail;
				}
			}
		}

		// validate finance documents
		if (!(DocumentCategories.FINANCE.getKey().equals(docType.getCategoryCode())) && docType.isDocIsMandatory()) {
			if (StringUtils.isBlank(detail.getDocUri())) {
				if (detail.getDocImage() == null || detail.getDocImage().length <= 0) {
					String[] valueParm = new String[2];
					valueParm[0] = "docContent";
					valueParm[1] = "docRefId";
					auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetail("90123", valueParm)));
					return auditDetail;
				}
			}
			if (detail.getDocImage() != null || StringUtils.isNotBlank(detail.getDocUri())) {
				if (StringUtils.isBlank(detail.getDocName())) {
					String[] valueParm = new String[1];
					valueParm[0] = "docName";
					auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetail("90502", valueParm)));
					return auditDetail;
				}
			}
			if (StringUtils.isBlank(detail.getDoctype())) {
				String[] valueParm = new String[1];
				valueParm[0] = "docFormat";
				auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetail("90502", valueParm)));
				return auditDetail;
			}

		}
		logger.debug(Literal.LEAVING);

		return auditDetail;

	}

	private int resetDocumentStatus(long docId) {
		DocumentStatusDetail ds = documentDao.getDocumentStatusByDocId(docId, "");

		if (ds == null || ds.getProcessed() == 0 || !ds.getStatus().equals("R")) {
			return 0;
		}

		DocumentStatusDetail befImage = new DocumentStatusDetail();

		BeanUtils.copyProperties(ds, befImage);
		ds.setBefImage(befImage);

		AuditDetail auditDetail = new AuditDetail(PennantConstants.TRAN_UPD, 1, ds.getBefImage(), ds);
		AuditHeader ah = new AuditHeader(String.valueOf(ds.getId()), null, null, null, auditDetail, null,
				new HashMap<>());

		ds.setStatus("");
		ds.setRemarks("");
		ds.setProcessed(0);
		int count = documentDao.update(ds, TableType.MAIN_TAB);

		auditHeaderDAO.addAudit(ah);
		return count;
	}

	@Override
	public DocumentType getApprovedDocumentTypeById(String docType) {
		return documentTypeService.getApprovedDocumentTypeById(docType);
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

	public void setDocumentDao(DocumentDao documentDao) {
		this.documentDao = documentDao;
	}

}
