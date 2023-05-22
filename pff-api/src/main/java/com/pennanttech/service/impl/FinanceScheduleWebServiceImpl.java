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
import com.pennant.ws.exception.ServiceException;
import com.pennanttech.controller.ExtendedTestClass;
import com.pennanttech.controller.FinanceDetailController;
import com.pennanttech.pennapps.core.model.ErrorDetail;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pffws.FinanceScheduleRestService;
import com.pennanttech.pffws.FinanceScheduleSoapService;
import com.pennanttech.util.APIConstants;
import com.pennanttech.ws.model.finance.EmiResponse;
import com.pennanttech.ws.service.APIErrorHandlerService;

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

	@Override
	public FinScheduleData createFinanceSchedule(FinScheduleData schdData) {
		logger.debug(Literal.ENTERING);

		FinanceDetail fd = new FinanceDetail();
		fd.setFinScheduleData(schdData);

		FinScheduleData newSchdData = null;

		validationUtility.validate(schdData, SaveValidationGroup.class);
		financeDataValidation.doBasicMandatoryValidations(fd);

		try {
			FinanceMain fm = schdData.getFinanceMain();

			if (fm == null) {
				FinScheduleData response = new FinScheduleData();
				doEmptyResponseObject(response);
				String[] valueParm = new String[1];
				valueParm[0] = "financeDetail";
				response.setReturnStatus(APIErrorHandlerService.getFailedStatus("90502", valueParm));
				return response;
			}

			String[] logFields = new String[3];
			logFields[0] = fm.getCustCIF();
			logFields[1] = fm.getFinType();
			logFields[2] = String.valueOf(fm.getFinAmount());

			APIErrorHandlerService.logKeyFields(logFields);
			fm.setFinCurrAssetValue(fm.getFinAmount());

			fd = financeDataDefaulting.defaultFinance(PennantConstants.VLD_CRT_SCHD, fd);
			schdData = fd.getFinScheduleData();

			if (!schdData.getErrorDetails().isEmpty()) {
				return getErrorMessage(schdData);
			}

			// validate schedule details
			financeDataValidation.financeDataValidation(PennantConstants.VLD_CRT_SCHD, schdData, true, fd, false);
			if (!schdData.getErrorDetails().isEmpty()) {
				return getErrorMessage(schdData);
			}

			// call doCreateFinanceSchedule method after successful validations
			newSchdData = financeDetailController.doCreateFinanceSchedule(schdData);

			if (newSchdData != null && newSchdData.getErrorDetails() != null) {
				return getErrorMessage(newSchdData);
			}
		} catch (Exception e) {
			logger.error(Literal.EXCEPTION, e);
			FinScheduleData response = new FinScheduleData();
			doEmptyResponseObject(response);
			response.setReturnStatus(APIErrorHandlerService.getFailedStatus());
			return response;
		}
		// for logging purpose
		if (newSchdData != null) {
			APIErrorHandlerService.logReference(newSchdData.getFinReference());
		}
		logger.debug(Literal.LEAVING);
		return newSchdData;
	}

	private FinScheduleData getErrorMessage(FinScheduleData schdData) {
		for (ErrorDetail ed : schdData.getErrorDetails()) {
			FinScheduleData response = new FinScheduleData();
			doEmptyResponseObject(response);
			response.setReturnStatus(APIErrorHandlerService.getFailedStatus(ed.getCode(), ed.getError()));
			return response;
		}

		return schdData;
	}

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
		Long finID = financeMainDAO.getFinIDByFinReference(finReference, "", false);
		if (finID == null) {
			// checking WIF table
			finID = financeMainDAO.getFinIDByFinReference(finReference, "", true);
			type = APIConstants.FINANCE_WIF;
		}

		if (finID == null) {
			FinScheduleData schdData = new FinScheduleData();
			doEmptyResponseObject(schdData);

			String[] valueParm = new String[1];
			valueParm[0] = finReference;
			schdData.setReturnStatus(APIErrorHandlerService.getFailedStatus("90201", valueParm));
			return schdData;
		}

		logger.debug(Literal.LEAVING);
		return financeDetailController.getFinanceInquiryDetails(finID, type);
	}

	@Override
	public EmiResponse getEMIAmount(FinScheduleData schdData) throws ServiceException {
		logger.debug(Literal.ENTERING);

		EmiResponse response = new EmiResponse();

		FinanceDetail fd = new FinanceDetail();
		fd.setFinScheduleData(schdData);

		FinanceMain fm = schdData.getFinanceMain();

		if (fm == null) {
			String[] valueParm = new String[1];
			valueParm[0] = "financeDetail";
			response.setReturnStatus(APIErrorHandlerService.getFailedStatus("90502", valueParm));
			return response;
		}

		String finReference = fm.getFinReference();

		FinanceMain oldFm = financeMainDAO.getFinanceMainByRef(finReference, "_View", false);

		if (oldFm == null) {
			String[] valueParm = new String[1];
			valueParm[0] = finReference;
			response.setReturnStatus(APIErrorHandlerService.getFailedStatus("90266", valueParm));
			return response;
		}

		long finID = oldFm.getFinID();

		fm.setFinID(finID);

		fm.setFinStartDate(oldFm.getFinStartDate());
		fm.setNumberOfTerms(oldFm.getNumberOfTerms());
		fm.setFinType(oldFm.getFinType());
		fm.setFinAssetValue(oldFm.getFinAssetValue());
		fm.setFinAmount(oldFm.getFinAssetValue());

		if (oldFm.isPlanEMIHAlw()) {
			fm.setPlanEMIHAlw((oldFm.isPlanEMIHAlw()));
			fm.setPlanEMIHMethod((oldFm.getPlanEMIHMethod()));
			fm.setPlanEMIHMax(oldFm.getPlanEMIHMax());
			fm.setPlanEMIHLockPeriod(oldFm.getPlanEMIHLockPeriod());
			fm.setPlanEMIHMaxPerYear(oldFm.getPlanEMIHMaxPerYear());

			List<Integer> emiMonths = finPlanEmiHolidayDAO.getPlanEMIHMonthsByRef(finID, "_temp");
			if (CollectionUtils.isEmpty(emiMonths)) {
				emiMonths = finPlanEmiHolidayDAO.getPlanEMIHMonthsByRef(finID, "");
			}
			List<FinPlanEmiHoliday> apiMonthList = new ArrayList<>();

			if (emiMonths != null
					&& StringUtils.equals(oldFm.getPlanEMIHMethod(), FinanceConstants.PLANEMIHMETHOD_FRQ)) {
				for (Integer detail : emiMonths) {
					FinPlanEmiHoliday finPlanEmiHoliday = new FinPlanEmiHoliday();
					finPlanEmiHoliday.setPlanEMIHMonth(detail);
					apiMonthList.add(finPlanEmiHoliday);
				}
				fd.getFinScheduleData().setApiplanEMIHmonths(apiMonthList);
			}

		}

		// do Basic mandatory validations using hibernate validator
		validationUtility.validate(schdData, SaveValidationGroup.class);
		financeDataValidation.doBasicMandatoryValidations(fd);

		try {
			// for logging purpose
			String[] logFields = new String[2];
			// logFields[0] = finScheduleData.getFinanceMain().getCustCIF();
			logFields[0] = fm.getFinType();
			logFields[1] = String.valueOf(fm.getFinAmount());
			APIErrorHandlerService.logKeyFields(logFields);

			// validate and Data defaulting and resetting again becuase of some new() was used in called class
			fd = financeDataDefaulting.defaultFinance(PennantConstants.VLD_CRT_SCHD, fd);
			fm.setFinIsAlwMD(false);

			if (oldFm != null) {
				if (StringUtils.equals(fm.getRepayRateBasis(), CalculationConstants.RATE_BASIS_F)) {
					fm.setRepayProfitRate(oldFm.getRepayProfitRate());
				} else {
					fm.setRepayBaseRate(oldFm.getRepayBaseRate());
					fm.setRepaySpecialRate(oldFm.getRepaySpecialRate());
					fm.setRepayMargin(oldFm.getRepayMargin());
				}

				fm.setAlwBPI(oldFm.isAlwBPI());
				fm.setBpiTreatment(oldFm.getBpiTreatment());

				if (oldFm.isAllowGrcPeriod()) {
					fm.setAllowGrcPeriod(oldFm.isAllowGrcPeriod());
					// finDetail.getFinScheduleData().getFinanceMain().setGraceTerms(oldfinaMain.getGraceTerms());
					fm.setGrcPeriodEndDate(oldFm.getGrcPeriodEndDate());

					fm.setGrcRateBasis(oldFm.getGrcRateBasis());
					if (StringUtils.equals(fm.getGrcRateBasis(), CalculationConstants.RATE_BASIS_F)) {
						fm.setGrcPftRate(oldFm.getGrcPftRate());
					} else {
						fm.setGraceBaseRate(oldFm.getGraceBaseRate());
						fm.setGraceSpecialRate(oldFm.getGraceSpecialRate());
						fm.setGrcMargin(oldFm.getGrcMargin());
					}
					fm.setGrcProfitDaysBasis(oldFm.getGrcProfitDaysBasis());
					fm.setGrcPftFrq(oldFm.getGrcPftFrq());
					fm.setNextGrcPftDate(oldFm.getNextGrcPftDate());
					fm.setAllowGrcPftRvw(oldFm.isAllowGrcPftRvw());
					fm.setGrcPftRvwFrq(oldFm.getGrcPftRvwFrq());
					fm.setNextGrcPftRvwDate(oldFm.getNextGrcPftRvwDate());
					fm.setAllowGrcCpz(oldFm.isAllowGrcCpz());
					fm.setGrcCpzFrq(oldFm.getGrcCpzFrq());
					fm.setNextGrcCpzDate(oldFm.getNextGrcCpzDate());
					fm.setAllowGrcRepay(oldFm.isAllowGrcRepay());
					fm.setGrcSchdMthd(oldFm.getGrcSchdMthd());
					fm.setGrcMinRate(oldFm.getGrcMinRate());
					fm.setGrcMaxRate(oldFm.getGrcMaxRate());
				}
				fm.setRepayFrq(oldFm.getRepayFrq());
				fm.setNextRepayDate(oldFm.getNextRepayDate());
				fm.setRepayRvwFrq(oldFm.getRepayRvwFrq());
				fm.setNextRepayRvwDate(oldFm.getNextRepayRvwDate());
				fm.setRepayPftFrq(oldFm.getRepayPftFrq());
				fm.setNextRepayPftDate(oldFm.getNextRepayPftDate());
				fm.setEqualRepay(fd.getFinScheduleData().getFinanceType().isEqualRepayment());
			}

			schdData = fd.getFinScheduleData();

			if (!schdData.getErrorDetails().isEmpty()) {

				for (ErrorDetail ed : schdData.getErrorDetails()) {
					response = new EmiResponse();
					response.setReturnStatus(APIErrorHandlerService.getFailedStatus(ed.getCode(), ed.getError()));
					return response;
				}
			}

			// validate schedule details
			financeDataValidation.financeDataValidation(PennantConstants.VLD_CRT_SCHD, schdData, true, fd, true);
			if (!schdData.getErrorDetails().isEmpty()) {
				for (ErrorDetail ed : schdData.getErrorDetails()) {
					response = new EmiResponse();
					response.setReturnStatus(APIErrorHandlerService.getFailedStatus(ed.getCode(), ed.getError()));
					return response;
				}
			}

			// call doCreateFinanceSchedule method after successful validations
			response = financeDetailController.getEMIAmount(fd);

			// for logging purpose
			if (response != null) {
				APIErrorHandlerService.logReference(String.valueOf(response.getRepayAmount()));
			}

		} catch (Exception e) {
			logger.error(Literal.EXCEPTION, e);
			response.setReturnStatus(APIErrorHandlerService.getFailedStatus());
			return response;
		}

		logger.debug(Literal.LEAVING);

		return response;
	}

	/**
	 * Nullify the un-necessary objects to prepare response in a structured format specified in API.
	 * 
	 * @param schdData
	 */
	private void doEmptyResponseObject(FinScheduleData schdData) {
		schdData.setDisbursementDetails(null);
		schdData.setRepayInstructions(null);
		schdData.setRateInstruction(null);
		schdData.setFinFeeDetailList(null);
		schdData.setStepPolicyDetails(null);
		schdData.setFinanceScheduleDetails(null);
		schdData.setPlanEMIHDates(null);
		schdData.setPlanEMIHmonths(null);
		schdData.setFinODDetails(null);
		schdData.setApiPlanEMIHDates(null);
		schdData.setApiplanEMIHmonths(null);
		schdData.setVasRecordingList(null);
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