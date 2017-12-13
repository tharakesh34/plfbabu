package com.pennanttech.pff.document.external;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import com.pennant.backend.model.documentdetails.DocumentDetails;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pff.external.DocumentManagementService;

public class ExternalDocumentManager {
	private static final Logger logger = Logger.getLogger(ExternalDocumentManager.class);

	@Autowired(required = false)
	private DocumentManagementService documentManagementService;

	public DocumentDetails getExternalDocument(String docRefId) {
		logger.debug(Literal.ENTERING);
		DocumentDetails detail = documentManagementService.getExternalDocument(docRefId);
		logger.debug(Literal.LEAVING);
		return detail;
	}
}
