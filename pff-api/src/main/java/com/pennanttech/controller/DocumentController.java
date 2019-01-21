package com.pennanttech.controller;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import com.pennant.app.util.SessionUserDetails;
import com.pennant.backend.dao.documentdetails.DocumentDetailsDAO;
import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.model.documentdetails.DocumentDetails;
import com.pennant.backend.util.FinanceConstants;
import com.pennant.backend.util.PennantConstants;
import com.pennanttech.pennapps.core.model.ErrorDetail;
import com.pennanttech.pennapps.core.model.LoggedInUser;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pff.core.TableType;
import com.pennanttech.pff.document.DocumentService;

public class DocumentController {
	private Logger logger = Logger.getLogger(DocumentController.class);

	private DocumentService documentService;
	private DocumentDetailsDAO documentDetailsDAO;

	public AuditHeader processDocumentDetails(DocumentDetails detail) {
		logger.debug(Literal.ENTERING);

		AuditHeader auditHeader = new AuditHeader();
		LoggedInUser userDetails = SessionUserDetails.getUserDetails(SessionUserDetails.getLogiedInUser());

		detail.setNewRecord(true);
		detail.setLastMntBy(userDetails.getUserId());
		detail.setLastMntOn(new Timestamp(System.currentTimeMillis()));
		detail.setRecordType(PennantConstants.RECORD_TYPE_NEW);
		detail.setVersion(1);

		// set update properties if exists
		String finReference = detail.getReferenceId();
		String docCategory = detail.getDocCategory();
		String module = FinanceConstants.MODULE_NAME;
		String type = TableType.MAIN_TAB.getSuffix();
		DocumentDetails extDocDetail = documentDetailsDAO.getDocumentDetails(finReference, docCategory, module, type);
		if (extDocDetail != null) {
			detail.setDocId(extDocDetail.getDocId());
			detail.setNewRecord(false);
			detail.setRecordType(PennantConstants.RECORD_TYPE_UPD);
			detail.setVersion(extDocDetail.getVersion() + 1);
		}

		detail.setDocModule(FinanceConstants.MODULE_NAME);
		detail.setUserDetails(SessionUserDetails.getUserDetails(SessionUserDetails.getLogiedInUser()));
		detail.setRecordStatus(PennantConstants.RCD_STATUS_APPROVED);

		if (StringUtils.equals(detail.getRecordType(), PennantConstants.RECORD_TYPE_UPD)) {
			auditHeader = getAuditHeader(detail, PennantConstants.TRAN_UPD);
		} else {
			auditHeader = getAuditHeader(detail, PennantConstants.TRAN_ADD);
		}
		auditHeader = documentService.saveOrUpdate(auditHeader);

		logger.debug(Literal.LEAVING);

		return auditHeader;
	}

	private AuditHeader getAuditHeader(DocumentDetails documentDetails, String transType) {
		AuditDetail auditDetail = new AuditDetail(transType, 1, documentDetails.getBefImage(), documentDetails);
		return new AuditHeader(documentDetails.getReferenceId(), null, null, null, auditDetail,
				documentDetails.getUserDetails(), new HashMap<String, ArrayList<ErrorDetail>>());
	}

	public void setDocumentService(DocumentService documentService) {
		this.documentService = documentService;
	}

	public void setDocumentDetailsDAO(DocumentDetailsDAO documentDetailsDAO) {
		this.documentDetailsDAO = documentDetailsDAO;
	}

}
