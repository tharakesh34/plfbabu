package com.pennanttech.controller;

import java.sql.Timestamp;

import org.apache.commons.collections.CollectionUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.pennant.backend.model.WSReturnStatus;
import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.model.documentdetails.DocumentDetails;
import com.pennant.backend.model.loanquery.QueryDetail;
import com.pennant.backend.service.loanquery.QueryDetailService;
import com.pennant.backend.util.PennantConstants;
import com.pennanttech.pennapps.core.model.ErrorDetail;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.service.impl.FinInstructionServiceImpl;
import com.pennanttech.ws.service.APIErrorHandlerService;

public class QueryModuleController extends ExtendedTestClass {

	private static final Logger logger = LogManager.getLogger(FinInstructionServiceImpl.class);

	private QueryDetailService queryDetailService;

	public WSReturnStatus doQueryUpdate(QueryDetail queryDetail) {
		logger.debug(Literal.ENTERING);

		// do set document details
		doSetDocumentDetails(queryDetail);

		AuditHeader auditHeader = getAuditHeader(queryDetail, "");

		auditHeader = queryDetailService.queryModuleUpdate(auditHeader);

		if (auditHeader.getErrorMessage() != null) {
			for (ErrorDetail errorDetail : auditHeader.getErrorMessage()) {

				return APIErrorHandlerService.getFailedStatus(errorDetail.getCode(), errorDetail.getError());
			}
		}

		logger.debug(Literal.LEAVING);
		return APIErrorHandlerService.getSuccessStatus();
	}

	private void doSetDocumentDetails(QueryDetail queryDetail) {
		if (CollectionUtils.isNotEmpty(queryDetail.getDocumentDetailsList())) {
			for (DocumentDetails documentDetail : queryDetail.getDocumentDetailsList()) {
				documentDetail.setNewRecord(true);
				documentDetail.setRecordType(PennantConstants.RECORD_TYPE_NEW);
				documentDetail.setRecordStatus(PennantConstants.RCD_STATUS_APPROVED);
				documentDetail.setLastMntOn(new Timestamp(System.currentTimeMillis()));
				documentDetail.setVersion(1);
			}
		}
	}

	private AuditHeader getAuditHeader(QueryDetail aQueryDetail, String tranType) {
		AuditDetail auditDetail = new AuditDetail(tranType, 1, aQueryDetail.getBefImage(), aQueryDetail);
		return new AuditHeader(getReference(aQueryDetail.getId()), null, null, null, auditDetail,
				aQueryDetail.getUserDetails(), null);
	}

	protected String getReference(long id) {
		return String.valueOf(id);
	}

	public void setQueryDetailService(QueryDetailService queryDetailService) {
		this.queryDetailService = queryDetailService;
	}
}
