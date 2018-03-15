package com.pennanttech.service.impl;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.pennant.backend.dao.finance.FinanceMainDAO;
import com.pennant.backend.model.customermasters.CustomerDetails;
import com.pennant.backend.model.finance.FinScheduleData;
import com.pennant.backend.model.finance.FinanceDetail;
import com.pennant.backend.service.finance.impl.FinanceDataDefaulting;
import com.pennant.backend.service.finance.impl.FinanceDataValidation;
import com.pennant.backend.util.PennantConstants;
import com.pennant.validation.SaveValidationGroup;
import com.pennant.validation.ValidationUtility;
import com.pennanttech.controller.FinanceDetailController;
import com.pennanttech.pennapps.core.model.ErrorDetail;
import com.pennanttech.pffws.FinanceScheduleRestService;
import com.pennanttech.pffws.FinanceScheduleSoapService;
import com.pennanttech.util.APIConstants;
import com.pennanttech.ws.service.APIErrorHandlerService;

@Service
public class FinanceScheduleWebServiceImpl implements FinanceScheduleRestService, FinanceScheduleSoapService {
	private static final Logger				logger	= Logger.getLogger(FinanceScheduleWebServiceImpl.class);

	private FinanceDetailController			financeDetailController;
	private ValidationUtility				validationUtility;
	private FinanceMainDAO					financeMainDAO;

	private FinanceDataDefaulting			financeDataDefaulting;
	private FinanceDataValidation			financeDataValidation;

	/**
	 * Create finance schedule (WIF) by receiving the request from interface.<br>
	 * The request object may include basic details, fees, insurance and stepping details.<br>
	 * 
	 * @param finScheduleData
	 */
	@Override
	public FinScheduleData createFinanceSchedule(FinScheduleData finScheduleData) {
		logger.debug("Entering");

		// do Basic mandatory validations using hibernate validator
		validationUtility.validate(finScheduleData, SaveValidationGroup.class);

		FinScheduleData financeSchdData = null;
		try {
			if (finScheduleData.getFinanceMain() == null) {
				FinScheduleData response = new FinScheduleData();
				doEmptyResponseObject(response);
				String[] valueParm = new String[1];
				valueParm[0] = "financeDetail";
				response.setReturnStatus(APIErrorHandlerService.getFailedStatus("90502", valueParm));
				return response;
			}

			// for logging purpose
			String[] logFields = new String[3];
			logFields[0] = finScheduleData.getFinanceMain().getCustCIF();
			logFields[1] = finScheduleData.getFinanceMain().getFinType();
			logFields[2] = String.valueOf(finScheduleData.getFinanceMain().getFinAmount());
			APIErrorHandlerService.logKeyFields(logFields);

			// validate and Data defaulting
			financeDataDefaulting.defaultFinance(PennantConstants.VLD_CRT_SCHD, finScheduleData);

			if (!finScheduleData.getErrorDetails().isEmpty()) {
				return getErrorMessage(finScheduleData);
			}

			// validate finance data
			if (!StringUtils.isBlank(finScheduleData.getFinanceMain().getLovDescCustCIF())) {
				FinanceDetail financeDetail = new FinanceDetail();
				financeDetail.setFinScheduleData(finScheduleData);
				CustomerDetails customerDetails = new CustomerDetails();
				customerDetails.setCustomer(null);
				financeDetail.setCustomerDetails(customerDetails);
				financeDataValidation.setFinanceDetail(financeDetail);
			}
			// validate schedule details
			financeDataValidation.financeDataValidation(PennantConstants.VLD_CRT_SCHD, finScheduleData, true);
			if (!finScheduleData.getErrorDetails().isEmpty()) {
				return getErrorMessage(finScheduleData);
			}

			// call doCreateFinanceSchedule method after successful validations
			financeSchdData = financeDetailController.doCreateFinanceSchedule(finScheduleData);

			if (financeSchdData != null && financeSchdData.getErrorDetails() != null) {
				return getErrorMessage(financeSchdData);
			}
		} catch (Exception e) {
			logger.error("Exception", e);
			FinScheduleData response = new FinScheduleData();
			doEmptyResponseObject(response);
			response.setReturnStatus(APIErrorHandlerService.getFailedStatus());
			return response;
		} finally {
			financeDataValidation.setFinanceDetail(null);
		}
		// for  logging purpose
		APIErrorHandlerService.logReference(financeSchdData.getFinReference());
		logger.debug("Leaving");
		return financeSchdData;
	}

	private FinScheduleData getErrorMessage(FinScheduleData financeSchdData) {
		for (ErrorDetail erroDetail : financeSchdData.getErrorDetails()) {
			FinScheduleData response = new FinScheduleData();
			doEmptyResponseObject(response);
			response.setReturnStatus(
					APIErrorHandlerService.getFailedStatus(erroDetail.getCode(), erroDetail.getError()));
			return response;
		}
		return financeSchdData;
	}

	/**
	 * Fetch requested finance details from PLF system.
	 * 
	 * @param finReference
	 */
	@Override
	public FinScheduleData getFinanceInquiry(String finReference) {
		logger.debug("Entering");

		// Mandatory validation
		if (StringUtils.isBlank(finReference)) {
			validationUtility.fieldLevelException();
		}
		// for logging purpose
		APIErrorHandlerService.logReference(finReference);

		String type = APIConstants.FINANCE_ORIGINATION;

		// validate Reference with WIF and Origination
		int count = financeMainDAO.getFinanceCountById(finReference, "", false);
		if (count <= 0) {
			// checking WIF table
			count = financeMainDAO.getFinanceCountById(finReference, "", true);
			type = APIConstants.FINANCE_WIF;
		}

		if (count <= 0) {
			FinScheduleData response = new FinScheduleData();
			doEmptyResponseObject(response);

			String[] valueParm = new String[1];
			valueParm[0] = finReference;
			response.setReturnStatus(APIErrorHandlerService.getFailedStatus("90201", valueParm));
			return response;
		}

		logger.debug("Leaving");
		return financeDetailController.getFinanceInquiryDetails(finReference, type);
	}

	/**
	 * Nullify the un-necessary objects to prepare response in a structured format specified in API.
	 * 
	 * @param response
	 */
	private void doEmptyResponseObject(FinScheduleData response) {
		response.setDisbursementDetails(null);
		response.setRepayInstructions(null);
		response.setRateInstruction(null);
		response.setFinFeeDetailList(null);
		response.setInsuranceList(null);
		response.setStepPolicyDetails(null);
		response.setFinanceScheduleDetails(null);
		response.setPlanEMIHDates(null);
		response.setPlanEMIHmonths(null);
		response.setFinODDetails(null);
		response.setApiPlanEMIHDates(null);
		response.setApiplanEMIHmonths(null);
		response.setVasRecordingList(null);
	}

	@Autowired
	public void setFinanceDetailController(FinanceDetailController financeDetailController) {
		this.financeDetailController = financeDetailController;
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
	public void setFinanceDataDefaulting(FinanceDataDefaulting financeDataDefaulting) {
		this.financeDataDefaulting = financeDataDefaulting;
	}

	@Autowired
	public void setFinanceDataValidation(FinanceDataValidation financeDataValidation) {
		this.financeDataValidation = financeDataValidation;
	}
}