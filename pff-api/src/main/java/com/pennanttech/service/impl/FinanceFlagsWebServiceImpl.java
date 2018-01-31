package com.pennanttech.service.impl;

import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.pennant.backend.dao.finance.FinFlagDetailsDAO;
import com.pennant.backend.dao.finance.FinanceMainDAO;
import com.pennant.backend.model.ErrorDetail;
import com.pennant.backend.model.WSReturnStatus;
import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.financemanagement.FinFlagsDetail;
import com.pennant.backend.model.financemanagement.FinanceFlag;
import com.pennant.backend.service.finance.FinanceFlagsService;
import com.pennant.backend.util.FinanceConstants;
import com.pennant.validation.DeleteValidationGroup;
import com.pennant.validation.SaveValidationGroup;
import com.pennant.validation.ValidationUtility;
import com.pennant.ws.exception.ServiceException;
import com.pennanttech.controller.FinanceFlagsController;
import com.pennanttech.pffws.FinanceFlagsRestService;
import com.pennanttech.pffws.FinanceFlagsSoapService;
import com.pennanttech.ws.service.APIErrorHandlerService;

@Service
public class FinanceFlagsWebServiceImpl implements FinanceFlagsSoapService,FinanceFlagsRestService {

	private static final Logger logger = Logger.getLogger(FinanceFlagsWebServiceImpl.class);

	private ValidationUtility validationUtility;
	private FinanceMainDAO financeMainDAO;
	private FinanceFlagsController financeFlagsController;
	private FinanceFlagsService financeFlagsService;
	private FinFlagDetailsDAO finFlagDetailsDAO;

	
	/**
	 * Method to process and fetch Finance Flags details
	 * 
	 * @param finReference
	 * @return
	 */
	@Override
	public FinanceFlag getLoanFlags(String finReference) throws ServiceException {
		logger.debug("Entering");

		// Mandatory validation
		if (StringUtils.isBlank(finReference)) {
			validationUtility.fieldLevelException();
		}
		FinanceFlag response = null;
		// for  logging purpose
		APIErrorHandlerService.logReference(finReference);
		// validate Reference with Origination
		int count = financeMainDAO.getFinanceCountById(finReference, "", false);
		if (count <= 0) {
			response = new FinanceFlag();
			String[] valueParm = new String[1];
			valueParm[0] = finReference;
			response.setReturnStatus(APIErrorHandlerService.getFailedStatus("90201", valueParm));
			return response;
		}
		FinanceFlag financeFlag = financeFlagsService.getApprovedFinanceFlagsById(finReference);
		if(financeFlag == null){
			response = new FinanceFlag();
			String[] valueParm = new String[1];
			valueParm[0] = finReference;
			response.setReturnStatus(APIErrorHandlerService.getFailedStatus("90218", valueParm));
			return response;
		}
		
		// call get Loan Flags controller
		response = financeFlagsController.getLoanFlags(finReference);
		
		logger.debug("Leaving");
		return response;
	}
	
	/**
	 * Method for create FinanceFlag in PLF system.
	 * 
	 * @param financeFlag
	 * @throws ServiceException
	 */
	@Override
	public WSReturnStatus addLoanFlags(FinanceFlag financeFlag) throws ServiceException {
		logger.debug("Entering");

		// bean validations
		validationUtility.validate(financeFlag, SaveValidationGroup.class);

		// for logging purpose
		APIErrorHandlerService.logReference(financeFlag.getFinReference());

		if (financeFlag.getFinFlagDetailList().isEmpty()) {
			String[] valueParm = new String[1];
			valueParm[0] = "flag";
			return APIErrorHandlerService.getFailedStatus("90502", valueParm);
		}

		int count = financeMainDAO.getFinanceCountById(financeFlag.getFinReference(), "", false);
		if (count <= 0) {
			String[] valueParm = new String[1];
			valueParm[0] = financeFlag.getFinReference();
			return APIErrorHandlerService.getFailedStatus("90201", valueParm);
		}

		// validate FlagsService details as per the API specification
		AuditDetail auditDetail = financeFlagsService.doValidations(financeFlag);

		if (auditDetail.getErrorDetails() != null) {
			for (ErrorDetail errorDetail : auditDetail.getErrorDetails()) {
				return APIErrorHandlerService.getFailedStatus(errorDetail.getCode(), errorDetail.getError());
			}
		}

		// call create Loan Flags service
		WSReturnStatus returnStatus = financeFlagsController.addLoanFlags(financeFlag);

		logger.debug("Leaving");
		return returnStatus;
	}
	
	/**
	 * Method for Delete FinanceFlag in PLF system.
	 * 
	 * @param financeFlag
	 * @throws ServiceException
	 */
	@Override
	public WSReturnStatus deleteLoanFlags(FinanceFlag financeFlag) throws ServiceException {
		logger.debug("Entering");
		
		// bean validations
		validationUtility.validate(financeFlag, DeleteValidationGroup.class);

		// for logging purpose
		APIErrorHandlerService.logReference(financeFlag.getFinReference());

		if (financeFlag.getFinFlagDetailList().isEmpty()) {
			String[] valueParm = new String[1];
			valueParm[0] = "flag";
			return APIErrorHandlerService.getFailedStatus("90502", valueParm);
		}
		// validate finReference in origination
		int count = financeMainDAO.getFinanceCountById(financeFlag.getFinReference(), "", false);
		if (count <= 0) {
			String[] valueParm = new String[1];
			valueParm[0] = financeFlag.getFinReference();
			return APIErrorHandlerService.getFailedStatus("90201", valueParm);
		}

		// validate finReference in Flags
		FinanceFlag finFlag = financeFlagsService.getApprovedFinanceFlagsById(financeFlag.getFinReference());
		if(finFlag == null){
			String[] valueParm = new String[1];
			valueParm[0] = financeFlag.getFinReference();
			return APIErrorHandlerService.getFailedStatus("90218", valueParm);
		}
		
		
		// validate FlagsService details as per the API specification
		List<FinFlagsDetail> finFlagDetailsList= financeFlag.getFinFlagDetailList();
		for (FinFlagsDetail detail : finFlagDetailsList) {
			FinFlagsDetail aFinFlag = finFlagDetailsDAO.getFinFlagsByRef(financeFlag.getFinReference(),
					detail.getFlagCode(), FinanceConstants.MODULE_NAME, "_AView");
			if (aFinFlag == null) {
				String[] valueParm = new String[2];
				valueParm[0] = financeFlag.getFinReference();
				valueParm[1] = detail.getFlagCode();
				return APIErrorHandlerService.getFailedStatus("90219", valueParm);
			}
		}
		
		// delete finance flags
		WSReturnStatus  returnStatus = financeFlagsController.deleteLoanFlags(financeFlag);
		
		logger.debug("Leaving");
		return returnStatus;
	}

	@Autowired
	public void setValidationUtility(ValidationUtility validationUtility) {
		this.validationUtility = validationUtility;
	}

	@Autowired
	public void setFinanceMainDAO(FinanceMainDAO financeMainDAO) {
		this.financeMainDAO = financeMainDAO;
	}

	@Autowired
	public void setFinanceFlagsController(FinanceFlagsController financeFlagsController) {
		this.financeFlagsController = financeFlagsController;
	}
	@Autowired
	public void setFinanceFlagsService(FinanceFlagsService financeFlagsService) {
		this.financeFlagsService = financeFlagsService;
	}
	@Autowired
	public void setFinFlagDetailsDAO(FinFlagDetailsDAO finFlagDetailsDAO) {
		this.finFlagDetailsDAO = finFlagDetailsDAO;
	}
}
