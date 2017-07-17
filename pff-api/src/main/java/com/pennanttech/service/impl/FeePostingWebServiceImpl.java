package com.pennanttech.service.impl;

import java.util.ArrayList;
import java.util.HashMap;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.pennant.backend.model.ErrorDetails;
import com.pennant.backend.model.WSReturnStatus;
import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.model.fees.FeePostings;
import com.pennant.backend.service.fees.feepostings.FeePostingService;
import com.pennant.backend.util.PennantConstants;
import com.pennant.validation.SaveValidationGroup;
import com.pennant.validation.ValidationUtility;
import com.pennant.ws.exception.ServiceException;
import com.pennanttech.controller.FeePostingController;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pffws.FeePostingRestService;
import com.pennanttech.pffws.FeePostingSoapService;
import com.pennanttech.ws.service.APIErrorHandlerService;

@Service
public class FeePostingWebServiceImpl implements FeePostingRestService, FeePostingSoapService {
	private final Logger			logger	= Logger.getLogger(getClass());
	private FeePostingController	feePostingController;
	private ValidationUtility		validationUtility;
	private FeePostingService		feePostingService;

	/**
	 * Method for create FeePostings in PLF system.
	 * 
	 * @param feePostings
	 * @return WSReturnStatus
	 * @throws ServiceException
	 */
	@Override
	public WSReturnStatus doFeePostings(FeePostings feePostings) throws ServiceException {
		logger.debug(Literal.ENTERING);
		// bean validations
		validationUtility.validate(feePostings, SaveValidationGroup.class);
		AuditHeader auditHeader = getAuditHeader(feePostings, PennantConstants.TRAN_WF);
		// validate Fee Posting details as per the API specification
		AuditDetail auditDetail = feePostingService.doValidations(feePostings);

		auditHeader.setAuditDetail(auditDetail);
		auditHeader.setErrorList(auditDetail.getErrorDetails());

		if (auditHeader.getErrorMessage() != null) {
			for (ErrorDetails errorDetail : auditHeader.getErrorMessage()) {
				return APIErrorHandlerService.getFailedStatus(errorDetail.getErrorCode(), errorDetail.getError());

			}
		}

		// call Fee Postings method in case of no errors
		WSReturnStatus returnStatus = feePostingController.doFeePostings(feePostings);
		logger.debug(Literal.LEAVING);
		return returnStatus;

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

	@Autowired
	public void setFeePostingController(FeePostingController feePostingController) {
		this.feePostingController = feePostingController;
	}

	@Autowired
	public void setValidationUtility(ValidationUtility validationUtility) {
		this.validationUtility = validationUtility;
	}

	@Autowired
	public void setFeePostingService(FeePostingService feePostingService) {
		this.feePostingService = feePostingService;
	}

}
