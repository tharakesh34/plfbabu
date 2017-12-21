package com.pennanttech.controller;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;

import org.apache.log4j.Logger;

import com.pennant.app.util.DateUtility;
import com.pennant.app.util.SessionUserDetails;
import com.pennant.backend.model.ErrorDetails;
import com.pennant.backend.model.LoggedInUser;
import com.pennant.backend.model.WSReturnStatus;
import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.model.fees.FeePostings;
import com.pennant.backend.service.fees.feepostings.FeePostingService;
import com.pennant.backend.util.PennantConstants;
import com.pennant.ws.exception.ServiceException;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.util.APIConstants;
import com.pennanttech.ws.service.APIErrorHandlerService;

public class FeePostingController {
	private final Logger		logger	= Logger.getLogger(getClass());
	private FeePostingService	feePostingService;

	/**
	 * Method for create FeePostings in PLF system.
	 * 
	 * @param feePostings
	 * @return WSReturnStatus
	 * @throws ServiceException
	 */
	public WSReturnStatus doFeePostings(FeePostings feePostings) {
		logger.debug(Literal.ENTERING);
		try {
			doSetPrepareData(feePostings);
			AuditHeader auditHeader = getAuditHeader(feePostings, PennantConstants.TRAN_WF);

			auditHeader = feePostingService.doApprove(auditHeader);

			if (auditHeader.getErrorMessage() != null) {
				for (ErrorDetails errorDetail : auditHeader.getErrorMessage()) {
					return APIErrorHandlerService.getFailedStatus(errorDetail.getErrorCode(), errorDetail.getError());
				}
			} else {
				return APIErrorHandlerService.getSuccessStatus();
			}
		} catch (Exception e) {
			logger.error("Exception", e);
			APIErrorHandlerService.logUnhandledException(e);
			return APIErrorHandlerService.getFailedStatus();
		}
		logger.debug(Literal.LEAVING);
		return APIErrorHandlerService.getSuccessStatus();
	}

	/**
	 * Get Audit Header Details
	 * 
	 * @param aFeePostings
	 * @param tranType
	 * @return AuditHeader
	 */
	private AuditHeader getAuditHeader(FeePostings aFeePostings, String tranType) {
		AuditDetail auditDetail = new AuditDetail(tranType, 1, aFeePostings.getBefImage(), aFeePostings);
		return new AuditHeader(String.valueOf(aFeePostings.getId()), String.valueOf(aFeePostings.getId()), null, null,
				auditDetail, aFeePostings.getUserDetails(), new HashMap<String, ArrayList<ErrorDetails>>());
	}

	private void doSetPrepareData(FeePostings feePostings) {
		LoggedInUser userDetails = SessionUserDetails.getUserDetails(SessionUserDetails.getLogiedInUser());
		feePostings.setUserDetails(userDetails);
		feePostings.setRecordType(PennantConstants.RECORD_TYPE_NEW);
		feePostings.setNewRecord(true);
		feePostings.setVersion(1);
		feePostings.setPostDate(DateUtility.getAppDate());
		feePostings.setRecordStatus(PennantConstants.RCD_STATUS_APPROVED);
		feePostings.setLastMntBy(userDetails.getUserId());
		feePostings.setLastMntOn(new Timestamp(System.currentTimeMillis()));
		feePostings.setSourceId(APIConstants.FINSOURCE_ID_API);
	}

	public void setFeePostingService(FeePostingService feePostingService) {
		this.feePostingService = feePostingService;
	}
}
