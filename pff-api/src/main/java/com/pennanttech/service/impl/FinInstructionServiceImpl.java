package com.pennanttech.service.impl;

import java.math.BigDecimal;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.jaxen.JaxenException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.pennant.app.constants.AccountEventConstants;
import com.pennant.app.constants.CalculationConstants;
import com.pennant.app.util.DateUtility;
import com.pennant.backend.dao.finance.FinanceMainDAO;
import com.pennant.backend.financeservice.AddDisbursementService;
import com.pennant.backend.financeservice.AddRepaymentService;
import com.pennant.backend.financeservice.ChangeFrequencyService;
import com.pennant.backend.financeservice.ChangeProfitService;
import com.pennant.backend.financeservice.PostponementService;
import com.pennant.backend.financeservice.RateChangeService;
import com.pennant.backend.financeservice.ReScheduleService;
import com.pennant.backend.financeservice.RecalculateService;
import com.pennant.backend.financeservice.RemoveTermsService;
import com.pennant.backend.model.ErrorDetails;
import com.pennant.backend.model.ValueLabel;
import com.pennant.backend.model.WSReturnStatus;
import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.finance.FinODPenaltyRate;
import com.pennant.backend.model.finance.FinServiceInstruction;
import com.pennant.backend.model.finance.FinanceDetail;
import com.pennant.backend.model.finance.FinanceMain;
import com.pennant.backend.service.finance.ManualPaymentService;
import com.pennant.backend.service.finance.impl.FinanceDataValidation;
import com.pennant.backend.util.FinanceConstants;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantStaticListUtil;
import com.pennant.exception.PFFInterfaceException;
import com.pennant.validation.AddDisbursementGroup;
import com.pennant.validation.AddRateChangeGroup;
import com.pennant.validation.AddTermsGroup;
import com.pennant.validation.ChangeInstallmentFrequencyGroup;
import com.pennant.validation.ChangeInterestGroup;
import com.pennant.validation.ChangeRepaymentGroup;
import com.pennant.validation.DefermentsGroup;
import com.pennant.validation.EarlySettlementGroup;
import com.pennant.validation.PartialSettlementGroup;
import com.pennant.validation.ReSchedulingGroup;
import com.pennant.validation.RecalculateGroup;
import com.pennant.validation.RemoveTermsGroup;
import com.pennant.validation.UpdateLoanBasicDetailsGroup;
import com.pennant.validation.UpdateLoanPenaltyDetailGroup;
import com.pennant.validation.ValidationUtility;
import com.pennant.ws.exception.ServiceException;
import com.pennanttech.controller.FinServiceInstController;
import com.pennanttech.pffws.FinServiceInstRESTService;
import com.pennanttech.pffws.FinServiceInstSOAPService;
import com.pennanttech.util.APIConstants;
import com.pennanttech.ws.service.APIErrorHandlerService;
import com.pennanttech.ws.service.FinanceValidationService;

@Service
public class FinInstructionServiceImpl implements FinServiceInstRESTService, FinServiceInstSOAPService {

	private final static Logger			logger	= Logger.getLogger(FinInstructionServiceImpl.class);

	private FinServiceInstController	finServiceInstController;
	private AddRepaymentService			addRepaymentService;
	private RateChangeService			rateChangeService;
	private ChangeProfitService			changeProfitService;
	private AddDisbursementService		addDisbursementService;
	private ChangeFrequencyService		changeFrequencyService;
	private ManualPaymentService		manualPaymentService;
	private ReScheduleService			reScheduleService;
	private RecalculateService			recalService;
	private RemoveTermsService			rmvTermsService;
	private PostponementService			postponementService;

	private FinanceMainDAO				financeMainDAO;
	private ValidationUtility			validationUtility;
	private FinanceValidationService	financeValidationService;
	private FinanceDataValidation		financeDataValidation;

	/**
	 * Method for perform addRateChange operation
	 * 
	 * @param finServiceInstruction
	 */
	@Override
	public FinanceDetail addRateChange(FinServiceInstruction finServiceInstruction) {
		logger.debug("Entering");

		// bean validations
		validationUtility.validate(finServiceInstruction, AddRateChangeGroup.class);

		FinanceDetail financeDetail = null;

		// validate ReqType
		WSReturnStatus returnStatus = validateReqType(finServiceInstruction.getReqType());

		if (StringUtils.isNotBlank(returnStatus.getReturnCode())) {
			financeDetail = new FinanceDetail();
			doEmptyResponseObject(financeDetail);
			financeDetail.setReturnStatus(returnStatus);

			return financeDetail;
		}

		// service level validations
		returnStatus = validateFinReference(finServiceInstruction);

		if (StringUtils.isNotBlank(returnStatus.getReturnCode())) {
			financeDetail = new FinanceDetail();
			doEmptyResponseObject(financeDetail);
			financeDetail.setReturnStatus(returnStatus);

			return financeDetail;
		}

		// validate service instruction data
		AuditDetail auditDetail = rateChangeService.doValidations(finServiceInstruction);

		if (auditDetail.getErrorDetails() != null) {
			for (ErrorDetails errorDetail : auditDetail.getErrorDetails()) {
				financeDetail = new FinanceDetail();
				doEmptyResponseObject(financeDetail);
				financeDetail.setReturnStatus(APIErrorHandlerService.getFailedStatus(errorDetail.getErrorCode(),
						errorDetail.getError()));

				return financeDetail;
			}
		}

		// validate fees
		String eventCode = AccountEventConstants.ACCEVENT_RATCHG;
		List<ErrorDetails> errors = financeDataValidation.doFeeValidations(PennantConstants.VLD_SRV_LOAN,
				finServiceInstruction, eventCode);
		if (!errors.isEmpty()) {
			for (ErrorDetails errorDetails : errors) {
				financeDetail = new FinanceDetail();
				doEmptyResponseObject(financeDetail);
				financeDetail.setReturnStatus(APIErrorHandlerService.getFailedStatus(errorDetails.getErrorCode(),
						errorDetails.getError()));
				return financeDetail;
			}
		}

		// call addRateChange service
		financeDetail = finServiceInstController.doAddRateChange(finServiceInstruction, eventCode);

		logger.debug("Leaving");
		return financeDetail;
	}

	/**
	 * Method for perform changeRepaymentAmount operation
	 * 
	 * @param loanServicing
	 */
	@Override
	public FinanceDetail changeRepayAmt(FinServiceInstruction finServiceInstruction) throws ServiceException {
		logger.debug("Entering");

		// bean validations
		validationUtility.validate(finServiceInstruction, ChangeRepaymentGroup.class);
		FinanceDetail financeDetail = null;

		// validate ReqType
		WSReturnStatus returnStatus = validateReqType(finServiceInstruction.getReqType());
		if (StringUtils.isNotBlank(returnStatus.getReturnCode())) {
			financeDetail = new FinanceDetail();
			doEmptyResponseObject(financeDetail);
			financeDetail.setReturnStatus(returnStatus);
			return financeDetail;
		}

		returnStatus = validateFinReference(finServiceInstruction);
		if (StringUtils.isNotBlank(returnStatus.getReturnCode())) {
			financeDetail = new FinanceDetail();
			doEmptyResponseObject(financeDetail);
			financeDetail.setReturnStatus(returnStatus);
			return financeDetail;
		}

		// validate service instruction data
		AuditDetail auditDetail = addRepaymentService.doValidations(finServiceInstruction);
		if (auditDetail.getErrorDetails() != null) {
			for (ErrorDetails errorDetail : auditDetail.getErrorDetails()) {
				financeDetail = new FinanceDetail();
				doEmptyResponseObject(financeDetail);
				financeDetail.setReturnStatus(APIErrorHandlerService.getFailedStatus(errorDetail.getErrorCode(),
						errorDetail.getError()));
				return financeDetail;
			}
		}
		// validate fees
		String eventCode = AccountEventConstants.ACCEVENT_SCDCHG;
		List<ErrorDetails> errors = financeDataValidation.doFeeValidations(PennantConstants.VLD_SRV_LOAN,
				finServiceInstruction, eventCode);
		if (!errors.isEmpty()) {
			for (ErrorDetails errorDetails : errors) {
				financeDetail = new FinanceDetail();
				doEmptyResponseObject(financeDetail);
				financeDetail.setReturnStatus(APIErrorHandlerService.getFailedStatus(errorDetails.getErrorCode(),
						errorDetails.getError()));
				return financeDetail;
			}
		}
		// call change repay amount service
		financeDetail = finServiceInstController.doAddRepayment(finServiceInstruction, eventCode);

		logger.debug("Leaving");
		return financeDetail;
	}

	/**
	 * Method for perform deferments operation
	 * 
	 * @param loanServicing
	 * @throws PFFInterfaceException
	 * @throws JaxenException
	 */
	@Override
	public FinanceDetail deferments(FinServiceInstruction finServiceInstruction) throws ServiceException {
		logger.debug("Entering");
		
		validationUtility.validate(finServiceInstruction, DefermentsGroup.class);
		FinanceDetail financeDetail = null;
		
		// validate ReqType
		WSReturnStatus returnStatus = validateReqType(finServiceInstruction.getReqType());
		if (StringUtils.isNotBlank(returnStatus.getReturnCode())) {
			financeDetail = new FinanceDetail();
			doEmptyResponseObject(financeDetail);
			financeDetail.setReturnStatus(returnStatus);
			return financeDetail;
		}

		returnStatus = validateFinReference(finServiceInstruction);
		if (StringUtils.isNotBlank(returnStatus.getReturnCode())) {
			financeDetail = new FinanceDetail();
			doEmptyResponseObject(financeDetail);
			financeDetail.setReturnStatus(returnStatus);
			return financeDetail;
		}

		// validate service instruction data
		AuditDetail auditDetail = postponementService.doValidations(finServiceInstruction);
		if (auditDetail.getErrorDetails() != null) {
			for (ErrorDetails errorDetail : auditDetail.getErrorDetails()) {
				financeDetail = new FinanceDetail();
				doEmptyResponseObject(financeDetail);
				financeDetail.setReturnStatus(APIErrorHandlerService.getFailedStatus(errorDetail.getErrorCode(),
						errorDetail.getError()));
				return financeDetail;
			}
		}
		
		// validate fees
		String eventCode = AccountEventConstants.ACCEVENT_DEFRPY;
		List<ErrorDetails> errors = financeDataValidation.doFeeValidations(PennantConstants.VLD_SRV_LOAN,
				finServiceInstruction, eventCode);
		if (!errors.isEmpty()) {
			for (ErrorDetails errorDetails : errors) {
				financeDetail = new FinanceDetail();
				doEmptyResponseObject(financeDetail);
				financeDetail.setReturnStatus(APIErrorHandlerService.getFailedStatus(errorDetails.getErrorCode(),
						errorDetails.getError()));
				return financeDetail;
			}
		}
		
		// call change repay amount service
		financeDetail = finServiceInstController.doDefferment(finServiceInstruction, eventCode);

		logger.debug("Leaving");
		return financeDetail;
	}

	/**
	 * Method for perform addTerms operation
	 * 
	 * @param loanServicing
	 * @throws PFFInterfaceException
	 * @throws JaxenException
	 */
	@Override
	public FinanceDetail addTerms(FinServiceInstruction finServiceInstruction) {
		logger.debug("Entering");

		// bean validations
		validationUtility.validate(finServiceInstruction, AddTermsGroup.class);
		FinanceDetail financeDetail = null;
		
		// validate ReqType
		WSReturnStatus returnStatus = validateReqType(finServiceInstruction.getReqType());
		if (StringUtils.isNotBlank(returnStatus.getReturnCode())) {
			financeDetail = new FinanceDetail();
			doEmptyResponseObject(financeDetail);
			financeDetail.setReturnStatus(returnStatus);
			return financeDetail;
		}

		// service level validations
		returnStatus = validateFinReference(finServiceInstruction);
		if (StringUtils.isNotBlank(returnStatus.getReturnCode())) {
			financeDetail = new FinanceDetail();
			doEmptyResponseObject(financeDetail);
			financeDetail.setReturnStatus(returnStatus);
			return financeDetail;
		}

		// validate fees
		String eventCode = AccountEventConstants.ACCEVENT_SCDCHG;
		List<ErrorDetails> errors = financeDataValidation.doFeeValidations(PennantConstants.VLD_SRV_LOAN,
				finServiceInstruction, eventCode);
		if (!errors.isEmpty()) {
			for (ErrorDetails errorDetails : errors) {
				financeDetail = new FinanceDetail();
				doEmptyResponseObject(financeDetail);
				financeDetail.setReturnStatus(APIErrorHandlerService.getFailedStatus(errorDetails.getErrorCode(),
						errorDetails.getError()));
				return financeDetail;
			}
		}

		// call addRateChange service
		financeDetail = finServiceInstController.addTerms(finServiceInstruction, eventCode);

		logger.debug("Leaving");
		return financeDetail;
	}

	/**
	 * Method for perform removeTerms operation
	 * 
	 * @param loanServicing
	 * @throws PFFInterfaceException
	 * @throws JaxenException
	 */
	@Override
	public FinanceDetail removeTerms(FinServiceInstruction finServiceInstruction) {
		logger.debug("Entering");

		// bean validations
		validationUtility.validate(finServiceInstruction, RemoveTermsGroup.class);
		FinanceDetail financeDetail = null;

		// service level validations
		WSReturnStatus returnStatus = validateFinReference(finServiceInstruction);
		if (StringUtils.isNotBlank(returnStatus.getReturnCode())) {
			financeDetail = new FinanceDetail();
			doEmptyResponseObject(financeDetail);
			financeDetail.setReturnStatus(returnStatus);
			return financeDetail;
		}

		// validate RecalType
		if (StringUtils.isNotBlank(finServiceInstruction.getRecalType())) {
			if (!StringUtils.equals(finServiceInstruction.getRecalType(), CalculationConstants.RPYCHG_ADJMDT)
					&& !StringUtils.equals(finServiceInstruction.getRecalType(), CalculationConstants.RPYCHG_TILLMDT)) {
				String[] valueParm = new String[1];
				valueParm[0] = finServiceInstruction.getRecalType();
				returnStatus = APIErrorHandlerService.getFailedStatus("91104", valueParm);
			}
		}

		if (StringUtils.isNotBlank(returnStatus.getReturnCode())) {
			financeDetail = new FinanceDetail();
			doEmptyResponseObject(financeDetail);
			financeDetail.setReturnStatus(returnStatus);
			return financeDetail;
		}

		// validate service instruction data
		AuditDetail auditDetail = rmvTermsService.doValidations(finServiceInstruction);
		if (auditDetail.getErrorDetails() != null) {
			for (ErrorDetails errorDetail : auditDetail.getErrorDetails()) {
				financeDetail = new FinanceDetail();
				doEmptyResponseObject(financeDetail);
				financeDetail.setReturnStatus(APIErrorHandlerService.getFailedStatus(errorDetail.getErrorCode(),
						errorDetail.getError()));
				return financeDetail;
			}
		}
		// validate fees
		String eventCode = AccountEventConstants.ACCEVENT_SCDCHG;
		List<ErrorDetails> errors = financeDataValidation.doFeeValidations(PennantConstants.VLD_SRV_LOAN,
				finServiceInstruction, eventCode);
		if (!errors.isEmpty()) {
			for (ErrorDetails errorDetails : errors) {
				financeDetail = new FinanceDetail();
				doEmptyResponseObject(financeDetail);
				financeDetail.setReturnStatus(APIErrorHandlerService.getFailedStatus(errorDetails.getErrorCode(),
						errorDetails.getError()));
				return financeDetail;
			}
		}
		// call removeRateChange service
		financeDetail = finServiceInstController.removeTerms(finServiceInstruction, eventCode);

		logger.debug("Leaving");
		return financeDetail;
	}

	/**
	 * Method for validate the request receiving from API and do schedule recalculation
	 * 
	 * @param finServiceInstruction
	 * @return FinanceDetail
	 */
	@Override
	public FinanceDetail recalculate(FinServiceInstruction finServiceInstruction) {
		logger.debug("Entering");

		// bean validations
		validationUtility.validate(finServiceInstruction, RecalculateGroup.class);
		FinanceDetail financeDetail = null;

		// validate ReqType
		WSReturnStatus returnStatus = validateReqType(finServiceInstruction.getReqType());
		if (StringUtils.isNotBlank(returnStatus.getReturnCode())) {
			financeDetail = new FinanceDetail();
			doEmptyResponseObject(financeDetail);
			financeDetail.setReturnStatus(returnStatus);
			return financeDetail;
		}

		returnStatus = validateFinReference(finServiceInstruction);
		if (StringUtils.isNotBlank(returnStatus.getReturnCode())) {
			financeDetail = new FinanceDetail();
			doEmptyResponseObject(financeDetail);
			financeDetail.setReturnStatus(returnStatus);
			return financeDetail;
		}

		// validate service instruction data
		AuditDetail auditDetail = recalService.doValidations(finServiceInstruction);

		if (auditDetail.getErrorDetails() != null) {
			for (ErrorDetails errorDetail : auditDetail.getErrorDetails()) {
				financeDetail = new FinanceDetail();
				doEmptyResponseObject(financeDetail);
				financeDetail.setReturnStatus(APIErrorHandlerService.getFailedStatus(errorDetail.getErrorCode(),
						errorDetail.getError()));

				return financeDetail;
			}
		}
		// validate fees
		String eventCode = AccountEventConstants.ACCEVENT_SCDCHG;
		List<ErrorDetails> errors = financeDataValidation.doFeeValidations(PennantConstants.VLD_SRV_LOAN,
				finServiceInstruction, eventCode);
		if (!errors.isEmpty()) {
			for (ErrorDetails errorDetails : errors) {
				financeDetail = new FinanceDetail();
				doEmptyResponseObject(financeDetail);
				financeDetail.setReturnStatus(APIErrorHandlerService.getFailedStatus(errorDetails.getErrorCode(),
						errorDetails.getError()));
				return financeDetail;
			}
		}

		// call change repay amount service
		financeDetail = finServiceInstController.doRecalculate(finServiceInstruction, eventCode);

		logger.debug("Leaving");
		return financeDetail;
	}

	/**
	 * Method for validate request object and do ChangeInterest action
	 * 
	 * @param finServiceInstruction
	 * @return FinanceDetail
	 */
	@Override
	public FinanceDetail changeInterest(FinServiceInstruction finServiceInstruction) {
		logger.debug("Entering");

		// bean validations
		validationUtility.validate(finServiceInstruction, ChangeInterestGroup.class);
		FinanceDetail financeDetail = null;

		// validate ReqType
		WSReturnStatus returnStatus = validateReqType(finServiceInstruction.getReqType());
		if (StringUtils.isNotBlank(returnStatus.getReturnCode())) {
			financeDetail = new FinanceDetail();
			doEmptyResponseObject(financeDetail);
			financeDetail.setReturnStatus(returnStatus);
			return financeDetail;
		}

		returnStatus = validateFinReference(finServiceInstruction);
		if (StringUtils.isNotBlank(returnStatus.getReturnCode())) {
			financeDetail = new FinanceDetail();
			doEmptyResponseObject(financeDetail);
			financeDetail.setReturnStatus(returnStatus);
			return financeDetail;
		}

		// validate service instruction data
		AuditDetail auditDetail = changeProfitService.doValidations(finServiceInstruction);
		if (auditDetail.getErrorDetails() != null) {
			for (ErrorDetails errorDetail : auditDetail.getErrorDetails()) {
				financeDetail = new FinanceDetail();
				doEmptyResponseObject(financeDetail);
				financeDetail.setReturnStatus(APIErrorHandlerService.getFailedStatus(errorDetail.getErrorCode(),
						errorDetail.getError()));
				return financeDetail;
			}
		}
		// validate fees
		String eventCode = AccountEventConstants.ACCEVENT_SCDCHG;
		List<ErrorDetails> errors = financeDataValidation.doFeeValidations(PennantConstants.VLD_SRV_LOAN,
				finServiceInstruction, eventCode);
		if (!errors.isEmpty()) {
			for (ErrorDetails errorDetails : errors) {
				financeDetail = new FinanceDetail();
				doEmptyResponseObject(financeDetail);
				financeDetail.setReturnStatus(APIErrorHandlerService.getFailedStatus(errorDetails.getErrorCode(),
						errorDetails.getError()));
				return financeDetail;
			}
		}
		// call change repay amount service
		financeDetail = finServiceInstController.doChangeProfit(finServiceInstruction, eventCode);

		logger.debug("Leaving");
		return financeDetail;
	}

	/**
	 * Method for process add disbursement request received from API.
	 * 
	 * @param finServiceInstruction
	 * @return FinanceDetail
	 * 
	 */
	@Override
	public FinanceDetail addDisbursement(FinServiceInstruction finServiceInstruction) {
		logger.debug("Entering");
		
		FinanceDetail financeDetail = null;
		if (finServiceInstruction.getDisbursementDetails() == null) {
			financeDetail = new FinanceDetail();
			doEmptyResponseObject(financeDetail);
			String valueParm[] = new String[1];
			valueParm[0] = "DisbursementDetails";
			financeDetail.setReturnStatus(APIErrorHandlerService.getFailedStatus("90502", valueParm));
			return financeDetail;
		}
		validationUtility.validate(finServiceInstruction, AddDisbursementGroup.class);
		
		// validate ReqType
		WSReturnStatus returnStatus = validateReqType(finServiceInstruction.getReqType());
		if (StringUtils.isNotBlank(returnStatus.getReturnCode())) {
			financeDetail = new FinanceDetail();
			doEmptyResponseObject(financeDetail);
			financeDetail.setReturnStatus(returnStatus);
			return financeDetail;
		}

		returnStatus = validateFinReference(finServiceInstruction);
		if (StringUtils.isNotBlank(returnStatus.getReturnCode())) {
			financeDetail = new FinanceDetail();
			doEmptyResponseObject(financeDetail);
			financeDetail.setReturnStatus(returnStatus);
			return financeDetail;
		}

		financeDetail = finServiceInstController.getFinanceDetails(finServiceInstruction,
				AccountEventConstants.ACCEVENT_SCDCHG);

		if (StringUtils.equals(finServiceInstruction.getRecalType(), CalculationConstants.RPYCHG_TILLMDT)) {
			finServiceInstruction.setToDate(financeDetail.getFinScheduleData().getFinanceMain().getMaturityDate());
		}

		// validate service instruction data
		AuditDetail auditDetail = addDisbursementService.doValidations(financeDetail, finServiceInstruction);
		if (auditDetail.getErrorDetails() != null) {
			for (ErrorDetails errorDetail : auditDetail.getErrorDetails()) {
				financeDetail = new FinanceDetail();
				doEmptyResponseObject(financeDetail);
				financeDetail.setReturnStatus(APIErrorHandlerService.getFailedStatus(errorDetail.getErrorCode(),
						errorDetail.getError()));
				return financeDetail;
			}
		}

		// validate disbursement Details
		financeDetail.setAdvancePaymentsList(finServiceInstruction.getDisbursementDetails());
/*		List<ErrorDetails> errors = financeDataValidation.disbursementValidation(financeDetail);
		if (!errors.isEmpty()) {
			for (ErrorDetails errorDetails : errors) {
				financeDetail = new FinanceDetail();
				doEmptyResponseObject(financeDetail);
				financeDetail.setReturnStatus(APIErrorHandlerService.getFailedStatus(errorDetails.getErrorCode(),
						errorDetails.getError()));
				return financeDetail;
			}
		}*/
		// validate fees
		String eventCode = AccountEventConstants.ACCEVENT_ADDDBSN;
		List<ErrorDetails> errorList = financeDataValidation.doFeeValidations(PennantConstants.VLD_SRV_LOAN,
				finServiceInstruction, eventCode);
		if (!errorList.isEmpty()) {
			for (ErrorDetails errorDetails : errorList) {
				financeDetail = new FinanceDetail();
				doEmptyResponseObject(financeDetail);
				financeDetail.setReturnStatus(APIErrorHandlerService.getFailedStatus(errorDetails.getErrorCode(),
						errorDetails.getError()));
				return financeDetail;
			}
		}
		// call change repay amount service
		financeDetail = finServiceInstController.doAddDisbursement(finServiceInstruction, financeDetail, eventCode);

		logger.debug("Leaving");
		return financeDetail;
	}

	/**
	 * Method for process changeInstallement frequency received from API
	 * 
	 * @param finServiceInstruction
	 * @return FinanceDetail
	 */
	@Override
	public FinanceDetail changeInstallmentFrq(FinServiceInstruction finServiceInstruction) {
		logger.debug("Entering");

		// bean validations
		validationUtility.validate(finServiceInstruction, ChangeInstallmentFrequencyGroup.class);
		FinanceDetail financeDetail = null;

		// validate ReqType
		WSReturnStatus returnStatus = validateReqType(finServiceInstruction.getReqType());
		if (StringUtils.isNotBlank(returnStatus.getReturnCode())) {
			financeDetail = new FinanceDetail();
			doEmptyResponseObject(financeDetail);
			financeDetail.setReturnStatus(returnStatus);
			return financeDetail;
		}

		returnStatus = validateFinReference(finServiceInstruction);
		if (StringUtils.isNotBlank(returnStatus.getReturnCode())) {
			financeDetail = new FinanceDetail();
			doEmptyResponseObject(financeDetail);
			financeDetail.setReturnStatus(returnStatus);
			return financeDetail;
		}

		// validate service instruction data
		AuditDetail auditDetail = changeFrequencyService.doValidations(finServiceInstruction);
		if (auditDetail.getErrorDetails() != null) {
			for (ErrorDetails errorDetail : auditDetail.getErrorDetails()) {
				financeDetail = new FinanceDetail();
				doEmptyResponseObject(financeDetail);
				financeDetail.setReturnStatus(APIErrorHandlerService.getFailedStatus(errorDetail.getErrorCode(),
						errorDetail.getError()));
				return financeDetail;
			}
		}
		// validate fees
		String eventCode = AccountEventConstants.ACCEVENT_SCDCHG;
		List<ErrorDetails> errors = financeDataValidation.doFeeValidations(PennantConstants.VLD_SRV_LOAN,
				finServiceInstruction, eventCode);
		if (!errors.isEmpty()) {
			for (ErrorDetails errorDetails : errors) {
				financeDetail = new FinanceDetail();
				doEmptyResponseObject(financeDetail);
				financeDetail.setReturnStatus(APIErrorHandlerService.getFailedStatus(errorDetails.getErrorCode(),
						errorDetails.getError()));
				return financeDetail;
			}
		}
		// call change repay amount service
		financeDetail = finServiceInstController.doChangeFrequency(finServiceInstruction, eventCode);

		logger.debug("Leaving");
		return financeDetail;
	}

	/**
	 * Method for perform Re-Scheduling action with specified Instructions.
	 * 
	 * @param finServiceInstruction
	 * @return FinanceDetail
	 */
	@Override
	public FinanceDetail reScheduling(FinServiceInstruction finServiceInstruction) {
		logger.debug("Entering");

		// bean validations
		validationUtility.validate(finServiceInstruction, ReSchedulingGroup.class);
		FinanceDetail financeDetail = null;

		// validate ReqType
		WSReturnStatus returnStatus = validateReqType(finServiceInstruction.getReqType());
		if (StringUtils.isNotBlank(returnStatus.getReturnCode())) {
			financeDetail = new FinanceDetail();
			doEmptyResponseObject(financeDetail);
			financeDetail.setReturnStatus(returnStatus);
			return financeDetail;
		}

		returnStatus = validateFinReference(finServiceInstruction);
		if (StringUtils.isNotBlank(returnStatus.getReturnCode())) {
			financeDetail = new FinanceDetail();
			doEmptyResponseObject(financeDetail);
			financeDetail.setReturnStatus(returnStatus);
			return financeDetail;
		}

		// validate service instruction data
		AuditDetail auditDetail = reScheduleService.doValidations(finServiceInstruction);
		if (auditDetail.getErrorDetails() != null) {
			for (ErrorDetails errorDetail : auditDetail.getErrorDetails()) {
				financeDetail = new FinanceDetail();
				doEmptyResponseObject(financeDetail);
				financeDetail.setReturnStatus(APIErrorHandlerService.getFailedStatus(errorDetail.getErrorCode(),
						errorDetail.getError()));

				return financeDetail;
			}
		}
		// validate fees
		String eventCode = AccountEventConstants.ACCEVENT_SCDCHG;
		List<ErrorDetails> errors = financeDataValidation.doFeeValidations(PennantConstants.VLD_SRV_LOAN,
				finServiceInstruction, eventCode);
		if (!errors.isEmpty()) {
			for (ErrorDetails errorDetails : errors) {
				financeDetail = new FinanceDetail();
				doEmptyResponseObject(financeDetail);
				financeDetail.setReturnStatus(APIErrorHandlerService.getFailedStatus(errorDetails.getErrorCode(),
						errorDetails.getError()));
				return financeDetail;
			}
		}
		// call change repay amount service
		financeDetail = finServiceInstController.doReSchedule(finServiceInstruction, eventCode);

		logger.debug("Leaving");
		return financeDetail;
	}

	/**
	 * Method for perform updateLoanBasicDetails
	 * 
	 * @param finServiceInstruction
	 * @throws PFFInterfaceException
	 * @throws JaxenException
	 */
	@Override
	public WSReturnStatus updateLoanBasicDetails(FinServiceInstruction finServiceInstruction) {
		logger.debug("Entering");

		// bean validations
		validationUtility.validate(finServiceInstruction, UpdateLoanBasicDetailsGroup.class);
		WSReturnStatus returnStatus = validateFinReference(finServiceInstruction);
		if (StringUtils.isNotBlank(returnStatus.getReturnCode())) {
			return returnStatus;
		}

		// call service level validations which include business validations
		FinanceMain financeMain = new FinanceMain();
		financeMain.setFinReference(finServiceInstruction.getFinReference());
		financeMain.setDsaCode(finServiceInstruction.getDsaCode());
		financeMain.setSalesDepartment(finServiceInstruction.getSalesDepartment());
		financeMain.setDmaCode(finServiceInstruction.getDmaCode());
		financeMain.setAccountsOfficer(finServiceInstruction.getAccountsOfficer());
		financeMain.setReferralId(finServiceInstruction.getReferralId());

		returnStatus = financeValidationService.validateFinBasicDetails(financeMain);
		if (StringUtils.isNotBlank(returnStatus.getReturnCode())) {
			return returnStatus;
		}

		// call addRateChange service
		returnStatus = finServiceInstController.updateLoanBasicDetails(financeMain);

		logger.debug("Leaving");
		return returnStatus;
	}

	/**
	 * Method for perform updateLoanPenaltyDetails
	 * 
	 * @param finServiceInstruction
	 * @throws PFFInterfaceException
	 * @throws JaxenException
	 */
	@Override
	public WSReturnStatus updateLoanPenaltyDetails(FinServiceInstruction finServiceInstruction) {
		logger.debug("Entering");

		// bean validations
		validationUtility.validate(finServiceInstruction, UpdateLoanPenaltyDetailGroup.class);
		
		if (finServiceInstruction.getFinODPenaltyRate() != null) {
			if (finServiceInstruction.getFinODPenaltyRate().isApplyODPenalty()) {
				if (finServiceInstruction.getFinODPenaltyRate().getODGraceDays() <= 0) {
					return beanValidation("odGraceDays");
				}
				if (StringUtils.isBlank(finServiceInstruction.getFinODPenaltyRate().getODChargeType())) {
					return beanValidation("odChargeType");
				}
				if (StringUtils.isBlank(finServiceInstruction.getFinODPenaltyRate().getODChargeCalOn())) {
					return beanValidation("odChargeCalOn");
				}
				if (finServiceInstruction.getFinODPenaltyRate().getODChargeAmtOrPerc().compareTo(BigDecimal.ZERO) < 0) {
					return beanValidation("odChargeAmtOrPerc");
				}
				if (finServiceInstruction.getFinODPenaltyRate().isODAllowWaiver()) {
					if (finServiceInstruction.getFinODPenaltyRate().getODMaxWaiverPerc().compareTo(BigDecimal.ZERO) < 0) {
						return beanValidation("odMaxWaiverPerc");
					}
				}
			} else {
				if (finServiceInstruction.getFinODPenaltyRate().getODGraceDays() > 0
						|| StringUtils.isNotBlank(finServiceInstruction.getFinODPenaltyRate().getODChargeType())
						|| StringUtils.isNotBlank(finServiceInstruction.getFinODPenaltyRate().getODChargeCalOn())
						|| finServiceInstruction.getFinODPenaltyRate().getODChargeAmtOrPerc().compareTo(BigDecimal.ZERO) > 0) {
					return APIErrorHandlerService.getFailedStatus("9999", "Overdue details only applicable for applyODPenalty.");
				}
			}
		} else {
			return beanValidation("overdue");
		}

		WSReturnStatus returnStatus = validateFinReference(finServiceInstruction);
		if (StringUtils.isNotBlank(returnStatus.getReturnCode())) {
			return returnStatus;
		}

		// call service level validations which include business validations
		FinODPenaltyRate finODPenaltyRate = new FinODPenaltyRate();
		finODPenaltyRate.setFinReference(finServiceInstruction.getFinReference());
		finODPenaltyRate.setApplyODPenalty(finServiceInstruction.getFinODPenaltyRate().isApplyODPenalty());
		finODPenaltyRate.setODIncGrcDays(finServiceInstruction.getFinODPenaltyRate().isODIncGrcDays());
		finODPenaltyRate.setODGraceDays(finServiceInstruction.getFinODPenaltyRate().getODGraceDays());
		finODPenaltyRate.setODChargeType(finServiceInstruction.getFinODPenaltyRate().getODChargeType());
		finODPenaltyRate.setODChargeCalOn(finServiceInstruction.getFinODPenaltyRate().getODChargeCalOn());
		finODPenaltyRate.setODChargeAmtOrPerc(finServiceInstruction.getFinODPenaltyRate().getODChargeAmtOrPerc());
		finODPenaltyRate.setODAllowWaiver(finServiceInstruction.getFinODPenaltyRate().isODAllowWaiver());
		finODPenaltyRate.setODMaxWaiverPerc(finServiceInstruction.getFinODPenaltyRate().getODMaxWaiverPerc());
		finODPenaltyRate.setFinEffectDate(DateUtility.getSysDate());

		returnStatus = validatefinODPenaltyRate(finODPenaltyRate);
		if (StringUtils.isNotBlank(returnStatus.getReturnCode())) {
			return returnStatus;
		}

		// call addRateChange service
		returnStatus = finServiceInstController.updateLoanPenaltyDetails(finODPenaltyRate);

		logger.debug("Leaving");
		return returnStatus;
	}

	/**
	 * Method for process Early settlement request received from API.
	 * 
	 * @param finServiceInstruction
	 * @return FinanceDetail
	 */
	@Override
	public FinanceDetail earlySettlement(FinServiceInstruction finServiceInstruction) {
		logger.debug("Entering");

		// bean validations
		validationUtility.validate(finServiceInstruction, EarlySettlementGroup.class);
		FinanceDetail financeDetail = null;

		// validate ReqType
		WSReturnStatus returnStatus = validateReqType(finServiceInstruction.getReqType());
		if (StringUtils.isNotBlank(returnStatus.getReturnCode())) {
			financeDetail = new FinanceDetail();
			doEmptyResponseObject(financeDetail);
			financeDetail.setReturnStatus(returnStatus);
			return financeDetail;
		}

		// service level validations
		int count = financeMainDAO.getFinanceCountById(finServiceInstruction.getFinReference(), "", false);
		if (count <= 0) {
			String[] valueParm = new String[1];
			valueParm[0] = finServiceInstruction.getFinReference();
			returnStatus = APIErrorHandlerService.getFailedStatus("90201", valueParm);
		}

		if (StringUtils.isNotBlank(returnStatus.getReturnCode())) {
			financeDetail = new FinanceDetail();
			doEmptyResponseObject(financeDetail);
			financeDetail.setReturnStatus(returnStatus);
			return financeDetail;
		}

		// validate service instruction data
		String moduleDefiner = FinanceConstants.FINSER_EVENT_EARLYSETTLE;
		if (StringUtils.equals(finServiceInstruction.getReqType(), APIConstants.REQTYPE_INQUIRY)) {
			moduleDefiner = FinanceConstants.FINSER_EVENT_EARLYSTLENQ;
		}
		AuditDetail auditDetail = manualPaymentService.doValidations(finServiceInstruction, moduleDefiner);
		if (auditDetail.getErrorDetails() != null) {
			for (ErrorDetails errorDetail : auditDetail.getErrorDetails()) {
				financeDetail = new FinanceDetail();
				doEmptyResponseObject(financeDetail);
				financeDetail.setReturnStatus(APIErrorHandlerService.getFailedStatus(errorDetail.getErrorCode(),
						errorDetail.getError()));
				return financeDetail;
			}
		}
		// validate fees
		String eventCode = AccountEventConstants.ACCEVENT_EARLYSTL;
		List<ErrorDetails> errors = financeDataValidation.doFeeValidations(PennantConstants.VLD_SRV_LOAN,
				finServiceInstruction, eventCode);
		if (!errors.isEmpty()) {
			for (ErrorDetails errorDetails : errors) {
				financeDetail = new FinanceDetail();
				doEmptyResponseObject(financeDetail);
				financeDetail.setReturnStatus(APIErrorHandlerService.getFailedStatus(errorDetails.getErrorCode(),
						errorDetails.getError()));
				return financeDetail;
			}
		}
		// call change repay amount service
		financeDetail = finServiceInstController.doEarlySettlement(finServiceInstruction, eventCode);

		logger.debug("Leaving");
		return financeDetail;
	}

	@Override
	public FinanceDetail partialSettlement(FinServiceInstruction finServiceInstruction) {
		logger.debug("Entering");

		// bean validations
		validationUtility.validate(finServiceInstruction, PartialSettlementGroup.class);
		FinanceDetail financeDetail = null;

		// validate ReqType
		WSReturnStatus returnStatus = validateReqType(finServiceInstruction.getReqType());
		if (StringUtils.isNotBlank(returnStatus.getReturnCode())) {
			financeDetail = new FinanceDetail();
			doEmptyResponseObject(financeDetail);
			financeDetail.setReturnStatus(returnStatus);
			return financeDetail;
		}

		// service level validations
		int count = financeMainDAO.getFinanceCountById(finServiceInstruction.getFinReference(), "", false);
		if (count <= 0) {
			String[] valueParm = new String[1];
			valueParm[0] = finServiceInstruction.getFinReference();
			returnStatus = APIErrorHandlerService.getFailedStatus("90201", valueParm);
		}

		if (StringUtils.isNotBlank(returnStatus.getReturnCode())) {
			financeDetail = new FinanceDetail();
			doEmptyResponseObject(financeDetail);
			financeDetail.setReturnStatus(returnStatus);
			return financeDetail;
		}

		// validate service instruction data
		String moduleDefiner = FinanceConstants.FINSER_EVENT_EARLYRPY;
		AuditDetail auditDetail = manualPaymentService.doValidations(finServiceInstruction, moduleDefiner);
		if (auditDetail.getErrorDetails() != null) {
			for (ErrorDetails errorDetail : auditDetail.getErrorDetails()) {
				financeDetail = new FinanceDetail();
				doEmptyResponseObject(financeDetail);
				financeDetail.setReturnStatus(APIErrorHandlerService.getFailedStatus(errorDetail.getErrorCode(),
						errorDetail.getError()));
				return financeDetail;
			}
		}
		// validate fees
		String eventCode = AccountEventConstants.ACCEVENT_REPAY;
		List<ErrorDetails> errors = financeDataValidation.doFeeValidations(PennantConstants.VLD_SRV_LOAN,
				finServiceInstruction, eventCode);
		if (!errors.isEmpty()) {
			for (ErrorDetails errorDetails : errors) {
				financeDetail = new FinanceDetail();
				doEmptyResponseObject(financeDetail);
				financeDetail.setReturnStatus(APIErrorHandlerService.getFailedStatus(errorDetails.getErrorCode(),
						errorDetails.getError()));
				return financeDetail;
			}
		}
		// call change repay amount service
		financeDetail = finServiceInstController.doPartialSettlement(finServiceInstruction, eventCode);

		logger.debug("Leaving");
		return financeDetail;
	}

	/**
	 * Method for perform manual payment action by taking specified instructions. 
	 * 
	 * @param finServiceInstruction
	 * @return FinanceDetail
	 */
	@Override
	public FinanceDetail manualPayment(FinServiceInstruction finServiceInstruction) throws ServiceException {
		logger.debug("Entering");

		// validate ReqType
		FinanceDetail financeDetail = null;
		WSReturnStatus returnStatus = validateReqType(finServiceInstruction.getReqType());
		if (StringUtils.isNotBlank(returnStatus.getReturnCode())) {
			financeDetail = new FinanceDetail();
			doEmptyResponseObject(financeDetail);
			financeDetail.setReturnStatus(returnStatus);
			return financeDetail;
		}

		int count = financeMainDAO.getFinanceCountById(finServiceInstruction.getFinReference(), "", false);
		if (count <= 0) {
			financeDetail = new FinanceDetail();
			doEmptyResponseObject(financeDetail);
			String[] valueParm = new String[1];
			valueParm[0] = finServiceInstruction.getFinReference();
			financeDetail.setReturnStatus(APIErrorHandlerService.getFailedStatus("90201", valueParm));
			return financeDetail;
		}

		// validate service instruction data
		String moduleDefiner = FinanceConstants.FINSER_EVENT_SCHDRPY;
		AuditDetail auditDetail = manualPaymentService.doValidations(finServiceInstruction, moduleDefiner);
		if (auditDetail.getErrorDetails() != null) {
			for (ErrorDetails errorDetail : auditDetail.getErrorDetails()) {
				financeDetail = new FinanceDetail();
				doEmptyResponseObject(financeDetail);
				financeDetail.setReturnStatus(APIErrorHandlerService.getFailedStatus(errorDetail.getErrorCode(),
						errorDetail.getError()));
				return financeDetail;
			}
		}
		// validate fees
		String eventCode = AccountEventConstants.ACCEVENT_REPAY;
		List<ErrorDetails> errors = financeDataValidation.doFeeValidations(PennantConstants.VLD_SRV_LOAN,
				finServiceInstruction, eventCode);
		if (!errors.isEmpty()) {
			for (ErrorDetails errorDetails : errors) {
				financeDetail = new FinanceDetail();
				doEmptyResponseObject(financeDetail);
				financeDetail.setReturnStatus(APIErrorHandlerService.getFailedStatus(errorDetails.getErrorCode(),
						errorDetails.getError()));
				return financeDetail;
			}
		}
		// call change repay amount service
		financeDetail = finServiceInstController.doManualPayment(finServiceInstruction, eventCode);

		logger.debug("Leaving");
		return financeDetail;
	}

	private WSReturnStatus beanValidation(String valueParam) {
		String[] valueParm = new String[1];
		valueParm[0] = valueParam;
		return APIErrorHandlerService.getFailedStatus("90502", valueParm);
	}

	private WSReturnStatus validatefinODPenaltyRate(FinODPenaltyRate finODPenaltyRate) {
		logger.debug("Entering");

		WSReturnStatus returnStatus = new WSReturnStatus();
		if (StringUtils.isNotBlank(finODPenaltyRate.getODChargeType())) {
			List<ValueLabel> odChargeType = PennantStaticListUtil.getODCChargeType();
			boolean odChargeTypeSts = false;
			for (ValueLabel value : odChargeType) {
				if (StringUtils.equals(value.getValue(), finODPenaltyRate.getODChargeType())) {
					odChargeTypeSts = true;
					break;
				}
			}
			if (!odChargeTypeSts) {
				String[] valueParm = new String[1];
				valueParm[0] = finODPenaltyRate.getODChargeType();
				return getErrorDetails("90501", valueParm);
			}
		}

		if (StringUtils.isNotBlank(finODPenaltyRate.getODChargeCalOn())) {
			List<ValueLabel> odChargeCalOn = PennantStaticListUtil.getODCCalculatedOn();
			boolean odChargeCalOnSts = false;
			for (ValueLabel value : odChargeCalOn) {
				if (StringUtils.equals(value.getValue(), finODPenaltyRate.getODChargeCalOn())) {
					odChargeCalOnSts = true;
					break;
				}
			}
			if (!odChargeCalOnSts) {
				String[] valueParm = new String[1];
				valueParm[0] = finODPenaltyRate.getODChargeCalOn();
				return getErrorDetails("90501", valueParm);
			}
		}
		logger.debug("Leaving");
		return returnStatus;

	}

	/**
	 * Method for prepare response object with errorDetails.
	 * 
	 * @param errorCode
	 * @param valueParm
	 * @return
	 */
	public WSReturnStatus getErrorDetails(String errorCode, String[] valueParm) {
		logger.debug("Entering");

		WSReturnStatus response = new WSReturnStatus();
		response = APIErrorHandlerService.getFailedStatus(errorCode, valueParm);

		// set default error code and description in case of Error code does not exists.
		if (StringUtils.isBlank(response.getReturnCode())) {
			response = APIErrorHandlerService.getFailedStatus(APIConstants.RES_FAILED_CODE,
					APIConstants.RES_FAILED_DESC);
		}

		logger.debug("Leaving");
		return response;
	}

	/**
	 * validate request type received from API.
	 * 
	 * @param reqType
	 * @return WSReturnStatus
	 */
	private WSReturnStatus validateReqType(String reqType) {
		logger.debug("Entering");
		WSReturnStatus status = new WSReturnStatus();

		if (!StringUtils.equals(reqType, APIConstants.REQTYPE_INQUIRY)
				&& !StringUtils.equals(reqType, APIConstants.REQTYPE_POST)) {
			String valueParm[] = new String[1];
			valueParm[0] = reqType;
			status = APIErrorHandlerService.getFailedStatus("91113", valueParm);
		}

		logger.debug("Leaving");
		return status;
	}

	/**
	 * Method for validate finance reference and check existence in origination or WIF
	 * 
	 * @param finReference
	 * @return
	 */
	private WSReturnStatus validateFinReference(FinServiceInstruction serviceInst) {
		logger.debug("Entering");

		WSReturnStatus returnStatus = new WSReturnStatus();

		// check records in origination and WIF
		int count = financeMainDAO.getFinanceCountById(serviceInst.getFinReference(), "", false);
		if (count > 0) {
			serviceInst.setWif(false);
		} else {
			count = financeMainDAO.getFinanceCountById(serviceInst.getFinReference(), "", true);
			if (count > 0) {
				serviceInst.setWif(true);
			} else {
				String[] valueParm = new String[1];
				valueParm[0] = serviceInst.getFinReference();
				return returnStatus = APIErrorHandlerService.getFailedStatus("90201", valueParm);
			}
		}

		// validate maintained records.
		int tempCount = financeMainDAO.getFinanceCountById(serviceInst.getFinReference(), "_Temp", serviceInst.isWif());
		if (tempCount > 0) {
			String[] valueParm = new String[1];
			valueParm[0] = serviceInst.getFinReference();
			return returnStatus = APIErrorHandlerService.getFailedStatus("90248", valueParm);
		}

		logger.debug("Leaving");
		return returnStatus;
	}

	/**
	 * Method for nullify the response object to prepare valid response message.
	 * 
	 * @param detail
	 */
	private void doEmptyResponseObject(FinanceDetail detail) {
		detail.setFinScheduleData(null);
		detail.setDocumentDetailsList(null);
		detail.setJountAccountDetailList(null);
		detail.setGurantorsDetailList(null);
		detail.setCollateralAssignmentList(null);
	}

	@Autowired
	public void setFinServiceInstController(FinServiceInstController finServiceInstController) {
		this.finServiceInstController = finServiceInstController;
	}

	@Autowired
	public void setFinanceMainDAO(FinanceMainDAO financeMainDAO) {
		this.financeMainDAO = financeMainDAO;
	}

	@Autowired
	public void setRateChangeService(RateChangeService rateChangeService) {
		this.rateChangeService = rateChangeService;
	}

	@Autowired
	public void setAddRepaymentService(AddRepaymentService addRepaymentService) {
		this.addRepaymentService = addRepaymentService;
	}

	/*
	 * @Autowired public void setDeffermentService(DeffermentService deffermentService) { this.deffermentService =
	 * deffermentService; }
	 */

	@Autowired
	public void setValidationUtility(ValidationUtility validationUtility) {
		this.validationUtility = validationUtility;
	}

	@Autowired
	public void setRecalService(RecalculateService recalService) {
		this.recalService = recalService;
	}

	@Autowired
	public void setChangeProfitService(ChangeProfitService changeProfitService) {
		this.changeProfitService = changeProfitService;
	}

	@Autowired
	public void setAddDisbursementService(AddDisbursementService addDisbursementService) {
		this.addDisbursementService = addDisbursementService;
	}

	@Autowired
	public void setChangeFrequencyService(ChangeFrequencyService changeFrequencyService) {
		this.changeFrequencyService = changeFrequencyService;
	}

	@Autowired
	public void setReScheduleService(ReScheduleService reScheduleService) {
		this.reScheduleService = reScheduleService;
	}

	@Autowired
	public void setFinanceValidationService(FinanceValidationService financeValidationService) {
		this.financeValidationService = financeValidationService;
	}

	@Autowired
	public void setManualPaymentService(ManualPaymentService manualPaymentService) {
		this.manualPaymentService = manualPaymentService;
	}

	@Autowired
	public void setRmvTermsService(RemoveTermsService rmvTermsService) {
		this.rmvTermsService = rmvTermsService;
	}

	@Autowired
	public void setPostponementService(PostponementService postponementService) {
		this.postponementService = postponementService;
	}
	
	@Autowired
	public void setFinanceDataValidation(FinanceDataValidation financeDataValidation) {
		this.financeDataValidation = financeDataValidation;
	}
}
