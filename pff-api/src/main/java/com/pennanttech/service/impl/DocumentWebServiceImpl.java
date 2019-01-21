package com.pennanttech.service.impl;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;

import com.pennant.backend.dao.finance.FinanceMainDAO;
import com.pennant.backend.model.WSReturnStatus;
import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.model.documentdetails.DocumentDetails;
import com.pennant.validation.DocumentDetailsGroup;
import com.pennant.validation.ValidationUtility;
import com.pennanttech.controller.DocumentController;
import com.pennanttech.pennapps.core.model.ErrorDetail;
import com.pennanttech.pff.document.DocumentService;
import com.pennanttech.pffws.DocumentRestService;
import com.pennanttech.pffws.DocumentSoapService;
import com.pennanttech.ws.service.APIErrorHandlerService;

public class DocumentWebServiceImpl implements DocumentRestService, DocumentSoapService {

	private ValidationUtility validationUtility;
	private DocumentController documentController;
	private DocumentService documentService;
	private FinanceMainDAO financeMainDAO;

	@Override
	public WSReturnStatus addDocument(DocumentDetails documentDetails) {
		AuditDetail auditDetail=null;
		AuditHeader auditHeader = null;

		//basic field Validation
		validationUtility.validate(documentDetails, DocumentDetailsGroup.class);

		//validate finReference
		if (StringUtils.isNotBlank(documentDetails.getReferenceId())) {
			int count = financeMainDAO.getFinanceCountById(documentDetails.getReferenceId(), "", false);
			if (count <= 0) {
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

		//Document Details Validation
		auditDetail = documentService.doDocumentValidation(documentDetails);
		if (auditDetail.getErrorDetails() != null) {
			for (ErrorDetail errorDetail : auditDetail.getErrorDetails()) {
				return APIErrorHandlerService.getFailedStatus(errorDetail.getCode(), errorDetail.getError());
			}
		}

		//Process Document Details
		auditHeader = documentController.processDocumentDetails(documentDetails);
		auditDetail = auditHeader.getAuditDetail();
		if (auditDetail != null) {
			for (ErrorDetail errorDetail : auditDetail.getErrorDetails()) {
				return APIErrorHandlerService.getFailedStatus(errorDetail.getCode(), errorDetail.getError());
			}
		}
		return APIErrorHandlerService.getSuccessStatus();
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
