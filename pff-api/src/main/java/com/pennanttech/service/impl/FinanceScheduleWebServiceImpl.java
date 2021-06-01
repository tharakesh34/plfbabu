package com.pennanttech.service.impl;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.pennant.app.constants.CalculationConstants;
import com.pennant.backend.dao.finance.FinPlanEmiHolidayDAO;
import com.pennant.backend.dao.finance.FinanceMainDAO;
import com.pennant.backend.model.finance.FinPlanEmiHoliday;
import com.pennant.backend.model.finance.FinScheduleData;
import com.pennant.backend.model.finance.FinanceDetail;
import com.pennant.backend.model.finance.FinanceMain;
import com.pennant.backend.service.finance.impl.FinanceDataDefaulting;
import com.pennant.backend.service.finance.impl.FinanceDataValidation;
import com.pennant.backend.util.FinanceConstants;
import com.pennant.backend.util.PennantConstants;
import com.pennant.validation.SaveValidationGroup;
import com.pennant.validation.ValidationUtility;
import com.pennanttech.controller.ExtendedTestClass;
import com.pennanttech.controller.FinanceDetailController;
import com.pennanttech.pennapps.core.model.ErrorDetail;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pffws.FinanceScheduleRestService;
import com.pennanttech.pffws.FinanceScheduleSoapService;
import com.pennanttech.util.APIConstants;
import com.pennanttech.ws.model.finance.EmiResponse;
import com.pennanttech.ws.service.APIErrorHandlerService;
import com.pennant.ws.exception.ServiceException;

@Service
public class FinanceScheduleWebServiceImpl extends ExtendedTestClass
		implements FinanceScheduleRestService, FinanceScheduleSoapService {
	private static final Logger logger = LogManager.getLogger(FinanceScheduleWebServiceImpl.class);

	private FinanceDetailController financeDetailController;
	private ValidationUtility validationUtility;
	private FinanceMainDAO financeMainDAO;

	private FinanceDataDefaulting financeDataDefaulting;
	private FinanceDataValidation financeDataValidation;
	private FinPlanEmiHolidayDAO finPlanEmiHolidayDAO;

	/**
	 * Create finance schedule (WIF) by receiving the request from interface.<br>
	 * The request object may include basic details, fees, insurance and stepping details.<br>
	 * 
	 * @param finScheduleData
	 */
	@Override
	public FinScheduleData createFinanceSchedule(FinScheduleData finScheduleData) {
		logger.debug(Literal.ENTERING);

		FinanceDetail finDetail = new FinanceDetail();
		finDetail.setFinScheduleData(finScheduleData);

		FinScheduleData finScheduleDataNew = null;

		// do Basic mandatory validations using hibernate validator
		validationUtility.validate(finScheduleData, SaveValidationGroup.class);

		try {
			FinanceMain financeMain = finScheduleData.getFinanceMain();
			if (financeMain == null) {
				FinScheduleData response = new FinScheduleData();
				doEmptyResponseObject(response);
				String[] valueParm = new String[1];
				valueParm[0] = "financeDetail";
				response.setReturnStatus(APIErrorHandlerService.getFailedStatus("90502", valueParm));
				return response;
			}

			// for logging purpose
			String[] logFields = new String[3];
			logFields[0] = financeMain.getCustCIF();
			logFields[1] = financeMain.getFinType();
			logFields[2] = String.valueOf(financeMain.getFinAmount());
			APIErrorHandlerService.logKeyFields(logFields);
			//setting finAmount to FinCurAssetValue
			financeMain.setFinCurrAssetValue(financeMain.getFinAmount());

			// validate and Data defaulting and resetting again becuase of some new() was used in called class
			finDetail = financeDataDefaulting.defaultFinance(PennantConstants.VLD_CRT_SCHD, finDetail);
			finScheduleData = finDetail.getFinScheduleData();

			if (!finScheduleData.getErrorDetails().isEmpty()) {
				return getErrorMessage(finScheduleData);
			}

			// FIXME: PV 28AUG19 to be removed as already handled in financedatadefaulting.defaultFinance()
			// validate finance data
			/*
			 * FinanceDetail financeDetail = null; if
			 * (!StringUtils.isBlank(finScheduleData.getFinanceMain().getLovDescCustCIF())) { financeDetail = new
			 * FinanceDetail(); financeDetail.setFinScheduleData(finScheduleData); CustomerDetails customerDetails = new
			 * CustomerDetails(); customerDetails.setCustomer(null); financeDetail.setCustomerDetails(customerDetails);
			 * }
			 */

			// validate schedule details
			financeDataValidation.financeDataValidation(PennantConstants.VLD_CRT_SCHD, finScheduleData, true, finDetail,
					false);
			if (!finScheduleData.getErrorDetails().isEmpty()) {
				return getErrorMessage(finScheduleData);
			}

			// call doCreateFinanceSchedule method after successful validations
			finScheduleDataNew = financeDetailController.doCreateFinanceSchedule(finScheduleData);

			if (finScheduleDataNew != null && finScheduleDataNew.getErrorDetails() != null) {
				return getErrorMessage(finScheduleDataNew);
			}
		} catch (Exception e) {
			logger.error("Exception", e);
			FinScheduleData response = new FinScheduleData();
			doEmptyResponseObject(response);
			response.setReturnStatus(APIErrorHandlerService.getFailedStatus());
			return response;
		}
		// for logging purpose
		if (finScheduleDataNew != null) {
			APIErrorHandlerService.logReference(finScheduleDataNew.getFinReference());
		}
		logger.debug(Literal.LEAVING);
		return finScheduleDataNew;
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
		logger.debug(Literal.ENTERING);

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

		logger.debug(Literal.LEAVING);
		return financeDetailController.getFinanceInquiryDetails(finReference, type);
	}

	@Override
	public EmiResponse getEMIAmount(FinScheduleData finScheduleData) throws ServiceException {
		logger.debug(Literal.ENTERING);

		EmiResponse response = null;

		FinanceDetail finDetail = new FinanceDetail();
		finDetail.setFinScheduleData(finScheduleData);
		FinanceMain oldfinaMain = null;
		if (finScheduleData.getFinanceMain() != null
				&& StringUtils.isNotBlank((finScheduleData.getFinanceMain().getFinReference()))) {
			oldfinaMain = financeMainDAO.getFinanceMainById(finScheduleData.getFinanceMain().getFinReference(), "_View",
					false);
			if (oldfinaMain == null) {
				response = new EmiResponse();
				String[] valueParm = new String[1];
				valueParm[0] = finScheduleData.getFinanceMain().getFinReference();
				response.setReturnStatus(APIErrorHandlerService.getFailedStatus("90266", valueParm));
				return response;
			}
			finDetail.getFinScheduleData().getFinanceMain().setFinStartDate(oldfinaMain.getFinStartDate());
			finDetail.getFinScheduleData().getFinanceMain().setNumberOfTerms(oldfinaMain.getNumberOfTerms());
			finDetail.getFinScheduleData().getFinanceMain().setFinType(oldfinaMain.getFinType());
			finDetail.getFinScheduleData().getFinanceMain().setFinAssetValue(oldfinaMain.getFinAssetValue());
			finDetail.getFinScheduleData().getFinanceMain().setFinAmount(oldfinaMain.getFinAssetValue());

			if (oldfinaMain.isPlanEMIHAlw()) {
				finDetail.getFinScheduleData().getFinanceMain().setPlanEMIHAlw((oldfinaMain.isPlanEMIHAlw()));
				finDetail.getFinScheduleData().getFinanceMain().setPlanEMIHMethod((oldfinaMain.getPlanEMIHMethod()));
				finDetail.getFinScheduleData().getFinanceMain().setPlanEMIHMax(oldfinaMain.getPlanEMIHMax());
				finDetail.getFinScheduleData().getFinanceMain()
						.setPlanEMIHLockPeriod(oldfinaMain.getPlanEMIHLockPeriod());
				finDetail.getFinScheduleData().getFinanceMain()
						.setPlanEMIHMaxPerYear(oldfinaMain.getPlanEMIHMaxPerYear());

				List<Integer> emiMonths = finPlanEmiHolidayDAO
						.getPlanEMIHMonthsByRef(finScheduleData.getFinanceMain().getFinReference(), "_temp");
				if (CollectionUtils.isEmpty(emiMonths)) {
					emiMonths = finPlanEmiHolidayDAO
							.getPlanEMIHMonthsByRef(finScheduleData.getFinanceMain().getFinReference(), "");
				}
				List<FinPlanEmiHoliday> apiMonthList = new ArrayList<>();

				if (emiMonths != null
						&& StringUtils.equals(oldfinaMain.getPlanEMIHMethod(), FinanceConstants.PLANEMIHMETHOD_FRQ)) {
					for (Integer detail : emiMonths) {
						FinPlanEmiHoliday finPlanEmiHoliday = new FinPlanEmiHoliday();
						finPlanEmiHoliday.setPlanEMIHMonth(detail);
						apiMonthList.add(finPlanEmiHoliday);
					}
					finDetail.getFinScheduleData().setApiplanEMIHmonths(apiMonthList);
				}

			}
		}

		// do Basic mandatory validations using hibernate validator
		validationUtility.validate(finScheduleData, SaveValidationGroup.class);

		try {
			if (finScheduleData.getFinanceMain() == null) {
				response = new EmiResponse();
				String[] valueParm = new String[1];
				valueParm[0] = "financeDetail";
				response.setReturnStatus(APIErrorHandlerService.getFailedStatus("90502", valueParm));
				return response;
			}

			// for logging purpose
			String[] logFields = new String[2];
			//logFields[0] = finScheduleData.getFinanceMain().getCustCIF();
			logFields[0] = finScheduleData.getFinanceMain().getFinType();
			logFields[1] = String.valueOf(finScheduleData.getFinanceMain().getFinAmount());
			APIErrorHandlerService.logKeyFields(logFields);

			// validate and Data defaulting and resetting again becuase of some new() was used in called class
			finDetail = financeDataDefaulting.defaultFinance(PennantConstants.VLD_CRT_SCHD, finDetail);
			finDetail.getFinScheduleData().getFinanceMain().setFinIsAlwMD(false);
			if (oldfinaMain != null) {

				if (StringUtils.equals(finDetail.getFinScheduleData().getFinanceMain().getRepayRateBasis(),
						CalculationConstants.RATE_BASIS_F)) {
					finDetail.getFinScheduleData().getFinanceMain()
							.setRepayProfitRate(oldfinaMain.getRepayProfitRate());
				} else {
					finDetail.getFinScheduleData().getFinanceMain().setRepayBaseRate(oldfinaMain.getRepayBaseRate());
					finDetail.getFinScheduleData().getFinanceMain()
							.setRepaySpecialRate(oldfinaMain.getRepaySpecialRate());
					finDetail.getFinScheduleData().getFinanceMain().setRepayMargin(oldfinaMain.getRepayMargin());
				}

				finDetail.getFinScheduleData().getFinanceMain().setAlwBPI(oldfinaMain.isAlwBPI());
				finDetail.getFinScheduleData().getFinanceMain().setBpiTreatment(oldfinaMain.getBpiTreatment());

				if (oldfinaMain.isAllowGrcPeriod()) {
					finDetail.getFinScheduleData().getFinanceMain().setAllowGrcPeriod(oldfinaMain.isAllowGrcPeriod());
					//finDetail.getFinScheduleData().getFinanceMain().setGraceTerms(oldfinaMain.getGraceTerms());
					finDetail.getFinScheduleData().getFinanceMain()
							.setGrcPeriodEndDate(oldfinaMain.getGrcPeriodEndDate());

					finDetail.getFinScheduleData().getFinanceMain().setGrcRateBasis(oldfinaMain.getGrcRateBasis());
					if (StringUtils.equals(finDetail.getFinScheduleData().getFinanceMain().getGrcRateBasis(),
							CalculationConstants.RATE_BASIS_F)) {
						finDetail.getFinScheduleData().getFinanceMain().setGrcPftRate(oldfinaMain.getGrcPftRate());
					} else {
						finDetail.getFinScheduleData().getFinanceMain()
								.setGraceBaseRate(oldfinaMain.getGraceBaseRate());
						finDetail.getFinScheduleData().getFinanceMain()
								.setGraceSpecialRate(oldfinaMain.getGraceSpecialRate());
						finDetail.getFinScheduleData().getFinanceMain().setGrcMargin(oldfinaMain.getGrcMargin());
					}
					finDetail.getFinScheduleData().getFinanceMain()
							.setGrcProfitDaysBasis(oldfinaMain.getGrcProfitDaysBasis());
					finDetail.getFinScheduleData().getFinanceMain().setGrcPftFrq(oldfinaMain.getGrcPftFrq());
					finDetail.getFinScheduleData().getFinanceMain().setNextGrcPftDate(oldfinaMain.getNextGrcPftDate());
					finDetail.getFinScheduleData().getFinanceMain().setAllowGrcPftRvw(oldfinaMain.isAllowGrcPftRvw());
					finDetail.getFinScheduleData().getFinanceMain().setGrcPftRvwFrq(oldfinaMain.getGrcPftRvwFrq());
					finDetail.getFinScheduleData().getFinanceMain()
							.setNextGrcPftRvwDate(oldfinaMain.getNextGrcPftRvwDate());
					finDetail.getFinScheduleData().getFinanceMain().setAllowGrcCpz(oldfinaMain.isAllowGrcCpz());
					finDetail.getFinScheduleData().getFinanceMain().setGrcCpzFrq(oldfinaMain.getGrcCpzFrq());
					finDetail.getFinScheduleData().getFinanceMain().setNextGrcCpzDate(oldfinaMain.getNextGrcCpzDate());
					finDetail.getFinScheduleData().getFinanceMain().setAllowGrcRepay(oldfinaMain.isAllowGrcRepay());
					finDetail.getFinScheduleData().getFinanceMain().setGrcSchdMthd(oldfinaMain.getGrcSchdMthd());
					finDetail.getFinScheduleData().getFinanceMain().setGrcMinRate(oldfinaMain.getGrcMinRate());
					finDetail.getFinScheduleData().getFinanceMain().setGrcMaxRate(oldfinaMain.getGrcMaxRate());
				}
				finDetail.getFinScheduleData().getFinanceMain().setRepayFrq(oldfinaMain.getRepayFrq());
				finDetail.getFinScheduleData().getFinanceMain().setNextRepayDate(oldfinaMain.getNextRepayDate());
				finDetail.getFinScheduleData().getFinanceMain().setRepayRvwFrq(oldfinaMain.getRepayRvwFrq());
				finDetail.getFinScheduleData().getFinanceMain().setNextRepayRvwDate(oldfinaMain.getNextRepayRvwDate());
				finDetail.getFinScheduleData().getFinanceMain().setRepayPftFrq(oldfinaMain.getRepayPftFrq());
				finDetail.getFinScheduleData().getFinanceMain().setNextRepayPftDate(oldfinaMain.getNextRepayPftDate());
				finDetail.getFinScheduleData().getFinanceMain()
						.setEqualRepay(finDetail.getFinScheduleData().getFinanceType().isEqualRepayment());
			}

			finScheduleData = finDetail.getFinScheduleData();

			if (!finScheduleData.getErrorDetails().isEmpty()) {

				for (ErrorDetail erroDetail : finScheduleData.getErrorDetails()) {
					response = new EmiResponse();
					response.setReturnStatus(
							APIErrorHandlerService.getFailedStatus(erroDetail.getCode(), erroDetail.getError()));
					return response;
				}
			}

			// validate schedule details
			financeDataValidation.financeDataValidation(PennantConstants.VLD_CRT_SCHD, finScheduleData, true, finDetail,
					true);
			if (!finScheduleData.getErrorDetails().isEmpty()) {
				for (ErrorDetail erroDetail : finScheduleData.getErrorDetails()) {
					response = new EmiResponse();
					response.setReturnStatus(
							APIErrorHandlerService.getFailedStatus(erroDetail.getCode(), erroDetail.getError()));
					return response;
				}
			}

			// call doCreateFinanceSchedule method after successful validations
			response = financeDetailController.getEMIAmount(finDetail);

			// for  logging purpose
			if (response != null) {
				APIErrorHandlerService.logReference(String.valueOf(response.getRepayAmount()));
			}

		} catch (Exception e) {
			logger.error("Exception", e);
			response = new EmiResponse();
			response.setReturnStatus(APIErrorHandlerService.getFailedStatus());
			return response;
		}
		logger.debug(Literal.LEAVING);
		return response;
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

	@Autowired
	public void setFinPlanEmiHolidayDAO(FinPlanEmiHolidayDAO finPlanEmiHolidayDAO) {
		this.finPlanEmiHolidayDAO = finPlanEmiHolidayDAO;
	}
}