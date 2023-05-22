package com.pennanttech.service.impl;

import java.util.HashMap;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.pennant.app.util.ErrorUtil;
import com.pennant.backend.model.WSReturnStatus;
import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.model.fees.FeePostings;
import com.pennant.backend.model.finance.ManualAdvise;
import com.pennant.backend.service.fees.feepostings.FeePostingService;
import com.pennant.backend.service.finance.FinanceDetailService;
import com.pennant.backend.util.PennantConstants;
import com.pennant.validation.SaveValidationGroup;
import com.pennant.validation.ValidationUtility;
import com.pennant.ws.exception.ServiceException;
import com.pennant.ws.exception.ServiceExceptionDetails;
import com.pennanttech.controller.ExtendedTestClass;
import com.pennanttech.controller.FeePostingController;
import com.pennanttech.pennapps.core.model.ErrorDetail;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pffws.FeePostingRestService;
import com.pennanttech.pffws.FeePostingSoapService;
import com.pennanttech.ws.model.manualAdvice.ManualAdviseResponse;
import com.pennanttech.ws.service.APIErrorHandlerService;

@Service
public class FeePostingWebServiceImpl extends ExtendedTestClass
		implements FeePostingRestService, FeePostingSoapService {
	private final Logger logger = LogManager.getLogger(getClass());
	private FeePostingController feePostingController;
	private ValidationUtility validationUtility;
	private FeePostingService feePostingService;
	private FinanceDetailService financeDetailService;

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
		doBasicMandatoryValidations(feePostings);
		AuditHeader auditHeader = getAuditHeader(feePostings, PennantConstants.TRAN_WF);
		// validate Fee Posting details as per the API specification
		AuditDetail auditDetail = feePostingService.doValidations(feePostings);

		auditHeader.setAuditDetail(auditDetail);
		auditHeader.setErrorList(auditDetail.getErrorDetails());

		if (auditHeader.getErrorMessage() != null) {
			for (ErrorDetail errorDetail : auditHeader.getErrorMessage()) {
				return APIErrorHandlerService.getFailedStatus(errorDetail.getCode(), errorDetail.getError());

			}
		}

		// call Fee Postings method in case of no errors
		WSReturnStatus returnStatus = feePostingController.doFeePostings(feePostings);
		logger.debug(Literal.LEAVING);
		return returnStatus;

	}

	/**
	 * Create Payable/Receivable Advise
	 */
	@Override
	public ManualAdviseResponse createAdvise(ManualAdvise advise) throws ServiceException {
		logger.debug(Literal.ENTERING);

		ManualAdviseResponse response = null;

		// Validate FinReference
		if (StringUtils.isBlank(advise.getFinReference())) {
			response = new ManualAdviseResponse();
			String[] param = new String[1];
			param[0] = "FinReference ";
			response.setReturnStatus(APIErrorHandlerService.getFailedStatus("90502", param));
			return response;
		}

		Long finID = financeDetailService.getFinID(advise.getFinReference());
		if (finID == null) {
			response = new ManualAdviseResponse();
			String[] param = new String[1];
			param[0] = advise.getFinReference();
			response.setReturnStatus(APIErrorHandlerService.getFailedStatus("90266", param));
			return response;
		}
		advise.setFinID(finID);
		// validate Manual Advise Detail
		response = feePostingController.validateAdviseDetail(advise);
		if (response != null && response.getReturnStatus() != null) {
			return response;
		}

		// proceed Manual Advise Detail
		response = feePostingController.doCreateAdvise(advise);
		if (response != null && response.getReturnStatus() != null) {
			return response;
		}

		// If there is no error set Success Return Status to response
		response.setReturnStatus(APIErrorHandlerService.getSuccessStatus());

		logger.debug(Literal.LEAVING);
		return response;
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
				auditDetail, aFeePostings.getUserDetails(), new HashMap<String, List<ErrorDetail>>());
	}

	private void doBasicMandatoryValidations(FeePostings feePosting) {
		if (feePosting.getPostingAmount() == null) {
			String[] valueParm = new String[1];
			valueParm[0] = "amount";

			ErrorDetail error = ErrorUtil.getErrorDetail(new ErrorDetail("90242", valueParm));

			ServiceExceptionDetails exceptions[] = new ServiceExceptionDetails[1];

			ServiceExceptionDetails exception = new ServiceExceptionDetails();

			exception.setFaultCode(error.getCode());
			exception.setFaultMessage(error.getError());

			exceptions[0] = exception;

			throw new ServiceException(exceptions);
		}
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

	@Autowired
	public void setFinanceDetailService(FinanceDetailService financeDetailService) {
		this.financeDetailService = financeDetailService;
	}

}
