package com.pennanttech.service.impl;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import com.pennant.backend.dao.finance.FinanceMainDAO;
import com.pennant.backend.model.WSReturnStatus;
import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.model.documentdetails.DocumentDetails;
import com.pennant.backend.model.finance.FinanceDetail;
import com.pennant.backend.model.finance.FinanceMain;
import com.pennant.validation.GetFinDocumentDetailsGroup;
import com.pennant.validation.ValidationUtility;
import com.pennanttech.controller.DocumentController;
import com.pennanttech.controller.ExtendedTestClass;
import com.pennanttech.pennapps.core.model.ErrorDetail;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pff.core.TableType;
import com.pennanttech.pff.document.DocumentService;
import com.pennanttech.pffws.DocumentRestService;
import com.pennanttech.pffws.DocumentSoapService;
import com.pennanttech.ws.model.customer.DocumentList;
import com.pennanttech.ws.service.APIErrorHandlerService;

public class DocumentWebServiceImpl extends ExtendedTestClass implements DocumentRestService, DocumentSoapService {

	private static final Logger logger = LogManager.getLogger(DocumentWebServiceImpl.class);

	private ValidationUtility validationUtility;
	private DocumentController documentController;
	private DocumentService documentService;
	private FinanceMainDAO financeMainDAO;

	@Override
	public WSReturnStatus addDocument(DocumentDetails documentDetails) {
		logger.debug(Literal.ENTERING);

		AuditDetail auditDetail = null;
		AuditHeader auditHeader = null;

		if (StringUtils.isEmpty(documentDetails.getFinReference())) {
			String[] valueParm = new String[1];
			valueParm[0] = "finReference";
			return APIErrorHandlerService.getFailedStatus("90502", valueParm);
		}

		if (StringUtils.isEmpty(documentDetails.getDocCategory())) {
			String[] valueParm = new String[1];
			valueParm[0] = "docCategory";
			return APIErrorHandlerService.getFailedStatus("90502", valueParm);
		} else if (documentDetails.getDocCategory().length() > 50) {
			String[] valueParm = new String[2];
			valueParm[0] = "docCategory";
			valueParm[1] = "50";
			return APIErrorHandlerService.getFailedStatus("90300", valueParm);
		}

		if (StringUtils.isEmpty(documentDetails.getDoctype())) {
			String[] valueParm = new String[1];
			valueParm[0] = "docFormat";
			return APIErrorHandlerService.getFailedStatus("90502", valueParm);
		} else if (documentDetails.getDoctype().length() > 10) {
			String[] valueParm = new String[2];
			valueParm[0] = "docFormat";
			valueParm[1] = "10";
			return APIErrorHandlerService.getFailedStatus("90300", valueParm);
		}

		if (StringUtils.isEmpty(documentDetails.getDocName())) {
			String[] valueParm = new String[1];
			valueParm[0] = "docName";
			return APIErrorHandlerService.getFailedStatus("90502", valueParm);
		} else if (documentDetails.getDocName().length() > 300) {
			String[] valueParm = new String[2];
			valueParm[0] = "docName";
			valueParm[1] = "300";
			return APIErrorHandlerService.getFailedStatus("90300", valueParm);
		}

		// validate finReference
		if (StringUtils.isNotBlank(documentDetails.getReferenceId())) {
			Long finID = financeMainDAO.getActiveFinID(documentDetails.getReferenceId(), TableType.VIEW);
			if (finID == null) {
				String[] valueParm = new String[1];
				valueParm[0] = "finreference: " + documentDetails.getReferenceId();
				return APIErrorHandlerService.getFailedStatus("90266", valueParm);
			}
		} else {
			String[] valueParm = new String[2];
			valueParm[0] = "finreference ";
			valueParm[1] = documentDetails.getReferenceId();
			return APIErrorHandlerService.getFailedStatus("90502", valueParm);
		}

		// Document Details Validation
		auditDetail = documentService.doDocumentValidation(documentDetails);
		if (auditDetail.getErrorDetails() != null) {
			for (ErrorDetail errorDetail : auditDetail.getErrorDetails()) {
				return APIErrorHandlerService.getFailedStatus(errorDetail.getCode(), errorDetail.getError());
			}
		}

		// Process Document Details
		auditHeader = documentController.processDocumentDetails(documentDetails);
		auditDetail = auditHeader.getAuditDetail();
		if (auditDetail != null) {
			for (ErrorDetail errorDetail : auditDetail.getErrorDetails()) {
				return APIErrorHandlerService.getFailedStatus(errorDetail.getCode(), errorDetail.getError());
			}
		}
		logger.debug(Literal.LEAVING);
		return APIErrorHandlerService.getSuccessStatus();
	}

	@Override
	public DocumentDetails getFinanceDocument(DocumentDetails documentDetails) {
		logger.debug(Literal.ENTERING);
		DocumentDetails response = null;

		// Considering Approved records only.
		String type = "";

		// basic field Validation
		validationUtility.validate(documentDetails, GetFinDocumentDetailsGroup.class);

		// validate given finReference is valid or not.
		if (financeMainDAO.getActiveFinID(documentDetails.getReferenceId(), TableType.MAIN_TAB) == null) {
			response = new DocumentDetails();
			String[] valueParm = new String[1];
			valueParm[0] = documentDetails.getReferenceId();
			response.setReturnStatus(APIErrorHandlerService.getFailedStatus("90201", valueParm));
			return response;
		}

		// getDocumentDetails
		response = documentController.getFinanceDocument(documentDetails.getReferenceId(),
				documentDetails.getDocCategory(), type);

		logger.debug(Literal.LEAVING);
		return response;
	}

	@Override
	public DocumentList getDocuments(String finReferance) {
		logger.debug(Literal.ENTERING);

		DocumentList response = new DocumentList();

		// Mandatory validation
		if (StringUtils.isBlank(finReferance)) {
			String[] valueParm = new String[1];
			valueParm[0] = "FinReference";
			response.setReturnStatus(APIErrorHandlerService.getFailedStatus("90502", valueParm));
			return response;
		}

		FinanceMain fm = financeMainDAO.getFinanceMain(finReferance);
		if (fm == null) {
			String[] valueParm = new String[1];
			valueParm[0] = finReferance;
			response.setReturnStatus(APIErrorHandlerService.getFailedStatus("90201", valueParm));
			return response;
		}

		response = documentController.getCustAndLoanDocuments(finReferance, fm.getCustID());

		logger.debug(Literal.LEAVING);
		return response;
	}

	private WSReturnStatus validateFinDocument(DocumentDetails documentDetails) {
		logger.debug(Literal.ENTERING);

		AuditDetail auditDetail = null;

		if (StringUtils.isEmpty(documentDetails.getFinReference())) {
			String[] valueParm = new String[1];
			valueParm[0] = "finReference";
			return APIErrorHandlerService.getFailedStatus("90502", valueParm);
		}

		if (StringUtils.isEmpty(documentDetails.getDocCategory())) {
			String[] valueParm = new String[1];
			valueParm[0] = "docCategory";
			return APIErrorHandlerService.getFailedStatus("90502", valueParm);
		} else if (documentDetails.getDocCategory().length() > 50) {
			String[] valueParm = new String[2];
			valueParm[0] = "docCategory";
			valueParm[1] = "50";
			return APIErrorHandlerService.getFailedStatus("90300", valueParm);
		}

		if (StringUtils.isEmpty(documentDetails.getDoctype())) {
			String[] valueParm = new String[1];
			valueParm[0] = "docFormat";
			return APIErrorHandlerService.getFailedStatus("90502", valueParm);
		} else if (documentDetails.getDoctype().length() > 10) {
			String[] valueParm = new String[2];
			valueParm[0] = "docFormat";
			valueParm[1] = "10";
			return APIErrorHandlerService.getFailedStatus("90300", valueParm);
		}

		if (StringUtils.isEmpty(documentDetails.getDocName())) {
			String[] valueParm = new String[1];
			valueParm[0] = "docName";
			return APIErrorHandlerService.getFailedStatus("90502", valueParm);
		} else if (documentDetails.getDocName().length() > 300) {
			String[] valueParm = new String[2];
			valueParm[0] = "docName";
			valueParm[1] = "300";
			return APIErrorHandlerService.getFailedStatus("90300", valueParm);
		}

		// validate finReference
		if (StringUtils.isNotBlank(documentDetails.getReferenceId())) {
			Long finID = financeMainDAO.getActiveFinID(documentDetails.getReferenceId(), TableType.VIEW);
			if (finID == null) {
				String[] valueParm = new String[1];
				valueParm[0] = "finreference: " + documentDetails.getReferenceId();
				return APIErrorHandlerService.getFailedStatus("90266", valueParm);
			}
		} else {
			String[] valueParm = new String[2];
			valueParm[0] = "finreference ";
			valueParm[1] = documentDetails.getReferenceId();
			return APIErrorHandlerService.getFailedStatus("90502", valueParm);
		}

		// Document Details Validation
		auditDetail = documentService.doDocumentValidation(documentDetails);
		if (auditDetail.getErrorDetails() != null) {
			for (ErrorDetail errorDetail : auditDetail.getErrorDetails()) {
				return APIErrorHandlerService.getFailedStatus(errorDetail.getCode(), errorDetail.getError());
			}
		}
		logger.debug(Literal.LEAVING);
		return null;
	}

	@Override
	public WSReturnStatus addDocuments(DocumentDetails documentDetails) {
		logger.debug(Literal.ENTERING);

		if (StringUtils.isEmpty(documentDetails.getReferenceId())) {
			String[] valueParm = new String[1];
			valueParm[0] = "finReference";
			return APIErrorHandlerService.getFailedStatus("90502", valueParm);
		}

		if (CollectionUtils.isEmpty(documentDetails.getDocuments())) {
			String[] valueParm = new String[1];
			valueParm[0] = "Atleast 1 Document";
			return APIErrorHandlerService.getFailedStatus("90502", valueParm);
		}

		FinanceDetail financeDetail = new FinanceDetail();
		financeDetail.setFinReference(documentDetails.getReferenceId());

		for (DocumentDetails doc : documentDetails.getDocuments()) {
			doc.setFinReference(documentDetails.getReferenceId());
			doc.setReferenceId(documentDetails.getReferenceId());
			WSReturnStatus status = validateFinDocument(doc);
			if (status != null) {
				return status;
			}
		}

		financeDetail.setDocumentDetailsList(documentDetails.getDocuments());
		WSReturnStatus status = documentController.processFinDocumentDetails(financeDetail);

		logger.debug(Literal.LEAVING);
		return status;
	}

	@Autowired
	public void setValidationUtility(ValidationUtility validationUtility) {
		this.validationUtility = validationUtility;
	}

	@Autowired
	public void setDocumentController(DocumentController documentController) {
		this.documentController = documentController;
	}

	@Autowired
	public void setDocumentService(DocumentService documentService) {
		this.documentService = documentService;
	}

	@Autowired
	public void setFinanceMainDAO(FinanceMainDAO financeMainDAO) {
		this.financeMainDAO = financeMainDAO;
	}

}
