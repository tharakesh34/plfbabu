package com.pennanttech.service.impl;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.jaxen.JaxenException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import com.pennant.app.constants.AccountEventConstants;
import com.pennant.app.constants.CalculationConstants;
import com.pennant.app.util.DateUtility;
import com.pennant.app.util.ReceiptCalculator;
import com.pennant.app.util.SysParamUtil;
import com.pennant.backend.dao.administration.SecurityUserDAO;
import com.pennant.backend.dao.finance.FinanceMainDAO;
import com.pennant.backend.dao.receipts.FinReceiptDetailDAO;
import com.pennant.backend.financeservice.AddDisbursementService;
import com.pennant.backend.financeservice.AddRepaymentService;
import com.pennant.backend.financeservice.AddTermsService;
import com.pennant.backend.financeservice.CancelDisbursementService;
import com.pennant.backend.financeservice.ChangeFrequencyService;
import com.pennant.backend.financeservice.ChangeProfitService;
import com.pennant.backend.financeservice.ChangeScheduleMethodService;
import com.pennant.backend.financeservice.PostponementService;
import com.pennant.backend.financeservice.RateChangeService;
import com.pennant.backend.financeservice.ReScheduleService;
import com.pennant.backend.financeservice.RecalculateService;
import com.pennant.backend.financeservice.RemoveTermsService;
import com.pennant.backend.model.ValueLabel;
import com.pennant.backend.model.WSReturnStatus;
import com.pennant.backend.model.applicationmaster.LoanPendingData;
import com.pennant.backend.model.applicationmaster.LoanPendingDetails;
import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.finance.FinAdvancePayments;
import com.pennant.backend.model.finance.FinFeeDetail;
import com.pennant.backend.model.finance.FinODPenaltyRate;
import com.pennant.backend.model.finance.FinReceiptData;
import com.pennant.backend.model.finance.FinReceiptDetail;
import com.pennant.backend.model.finance.FinScheduleData;
import com.pennant.backend.model.finance.FinServiceInstruction;
import com.pennant.backend.model.finance.FinanceDetail;
import com.pennant.backend.model.finance.FinanceMain;
import com.pennant.backend.model.finance.ReceiptAllocationDetail;
import com.pennant.backend.model.finance.financetaxdetail.FinanceTaxDetail;
import com.pennant.backend.service.fees.FeeDetailService;
import com.pennant.backend.service.finance.FinAdvancePaymentsService;
import com.pennant.backend.service.finance.FinanceTaxDetailService;
import com.pennant.backend.service.finance.ReceiptService;
import com.pennant.backend.service.finance.impl.FinanceDataValidation;
import com.pennant.backend.util.DisbursementConstants;
import com.pennant.backend.util.FinanceConstants;
import com.pennant.backend.util.PennantApplicationUtil;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantStaticListUtil;
import com.pennant.backend.util.SMTParameterConstants;
import com.pennant.backend.util.UploadConstants;
import com.pennant.validation.AddDisbursementGroup;
import com.pennant.validation.AddRateChangeGroup;
import com.pennant.validation.AddTermsGroup;
import com.pennant.validation.ChangeGestationGroup;
import com.pennant.validation.ChangeInstallmentFrequencyGroup;
import com.pennant.validation.ChangeInterestGroup;
import com.pennant.validation.ChangeRepaymentGroup;
import com.pennant.validation.DefermentsGroup;
import com.pennant.validation.EarlySettlementGroup;
import com.pennant.validation.PartialSettlementGroup;
import com.pennant.validation.ReSchedulingGroup;
import com.pennant.validation.RecalculateGroup;
import com.pennant.validation.RemoveTermsGroup;
import com.pennant.validation.ScheduleMethodGroup;
import com.pennant.validation.UpdateLoanBasicDetailsGroup;
import com.pennant.validation.UpdateLoanPenaltyDetailGroup;
import com.pennant.validation.UpfrontFeesGroup;
import com.pennant.validation.ValidationUtility;
import com.pennant.ws.exception.ServiceException;
import com.pennanttech.controller.CreateFinanceController;
import com.pennanttech.controller.FinServiceInstController;
import com.pennanttech.pennapps.core.AppException;
import com.pennanttech.pennapps.core.model.ErrorDetail;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pffws.FinServiceInstRESTService;
import com.pennanttech.pffws.FinServiceInstSOAPService;
import com.pennanttech.util.APIConstants;
import com.pennanttech.ws.model.finance.DisbRequest;
import com.pennanttech.ws.model.finance.DisbResponse;
import com.pennanttech.ws.model.finance.FinAdvPaymentDetail;
import com.pennanttech.ws.service.APIErrorHandlerService;
import com.pennanttech.ws.service.FinanceValidationService;

@Service
public class FinInstructionServiceImpl implements FinServiceInstRESTService, FinServiceInstSOAPService {
	private static final Logger logger = Logger.getLogger(FinInstructionServiceImpl.class);

	private FinServiceInstController finServiceInstController;
	private CreateFinanceController createFinanceController;
	private AddRepaymentService addRepaymentService;
	private RateChangeService rateChangeService;
	private ChangeProfitService changeProfitService;
	private AddDisbursementService addDisbursementService;
	private CancelDisbursementService cancelDisbursementService;
	private ChangeFrequencyService changeFrequencyService;
	private ReceiptService receiptService;
	private ReScheduleService reScheduleService;
	private RecalculateService recalService;
	private RemoveTermsService rmvTermsService;
	private PostponementService postponementService;
	private AddTermsService addTermsService;
	private ChangeScheduleMethodService changeScheduleMethodService;
	private FeeDetailService feeDetailService;
	private ReceiptCalculator receiptCalculator;

	private FinanceMainDAO financeMainDAO;
	private ValidationUtility validationUtility;
	private FinanceValidationService financeValidationService;
	private FinanceDataValidation financeDataValidation;
	private FinReceiptDetailDAO finReceiptDetailDAO;
	private SecurityUserDAO securityUserDAO;
	private FinanceTaxDetailService financeTaxDetailService;
	private FinAdvancePaymentsService finAdvancePaymentsService;

	/**
	 * Method for perform addRateChange operation
	 * 
	 * @param finServiceInstruction
	 */
	@Override
	public FinanceDetail addRateChange(FinServiceInstruction finServiceInstruction) {
		logger.debug(Literal.ENTERING);

		WSReturnStatus returnStatus = new WSReturnStatus();
		FinanceDetail financeDetail = null;

		if (StringUtils.equals(UploadConstants.FRR, finServiceInstruction.getReqFrom())) {
			returnStatus = validateFinReference(finServiceInstruction);
			if (StringUtils.isNotBlank(returnStatus.getReturnCode())) {
				financeDetail = new FinanceDetail();
				doEmptyResponseObject(financeDetail);
				financeDetail.setReturnStatus(returnStatus);
				return financeDetail;
			}
		}
		// bean validations
		validationUtility.validate(finServiceInstruction, AddRateChangeGroup.class);

		// for logging purpose
		APIErrorHandlerService.logReference(finServiceInstruction.getFinReference());

		// set Default date formats
		setDefaultDateFormats(finServiceInstruction);
		// Set null for Empty values 
		setDefaultForReferenceFields(finServiceInstruction);
		// validate ReqType
		returnStatus = validateReqType(finServiceInstruction.getReqType());

		if (StringUtils.isNotBlank(returnStatus.getReturnCode())) {
			financeDetail = new FinanceDetail();
			doEmptyResponseObject(financeDetail);
			financeDetail.setReturnStatus(returnStatus);

			return financeDetail;
		}

		// service level validations
		if (!(StringUtils.equals(UploadConstants.FRR, finServiceInstruction.getReqFrom()))) {
			returnStatus = validateFinReference(finServiceInstruction);

			if (StringUtils.isNotBlank(returnStatus.getReturnCode())) {
				financeDetail = new FinanceDetail();
				doEmptyResponseObject(financeDetail);
				financeDetail.setReturnStatus(returnStatus);

				return financeDetail;
			}
		}

		// validate service instruction data
		AuditDetail auditDetail = rateChangeService.doValidations(finServiceInstruction);

		if (StringUtils.equals(UploadConstants.FRR, finServiceInstruction.getReqFrom())) {
			finServiceInstruction = (FinServiceInstruction) auditDetail.getModelData();
		}

		if (auditDetail.getErrorDetails() != null) {
			for (ErrorDetail errorDetail : auditDetail.getErrorDetails()) {
				financeDetail = new FinanceDetail();
				doEmptyResponseObject(financeDetail);
				financeDetail.setReturnStatus(
						APIErrorHandlerService.getFailedStatus(errorDetail.getCode(), errorDetail.getError()));

				return financeDetail;
			}
		}

		// validate fees
		String eventCode = AccountEventConstants.ACCEVENT_RATCHG;
		List<ErrorDetail> errors = doProcessServiceFees(finServiceInstruction, eventCode);
		if (!errors.isEmpty()) {
			for (ErrorDetail errorDetails : errors) {
				financeDetail = new FinanceDetail();
				doEmptyResponseObject(financeDetail);
				financeDetail.setReturnStatus(
						APIErrorHandlerService.getFailedStatus(errorDetails.getCode(), errorDetails.getError()));
				return financeDetail;
			}
		}

		// call addRateChange service
		financeDetail = finServiceInstController.doAddRateChange(finServiceInstruction, eventCode);

		logger.debug(Literal.LEAVING);
		return financeDetail;
	}

	/**
	 * Method for perform changeRepaymentAmount operation
	 * 
	 * @param loanServicing
	 */
	@Override
	public FinanceDetail changeRepayAmt(FinServiceInstruction finServiceInstruction) throws ServiceException {
		logger.debug(Literal.ENTERING);

		// bean validations
		validationUtility.validate(finServiceInstruction, ChangeRepaymentGroup.class);
		FinanceDetail financeDetail = null;

		// for logging purpose
		APIErrorHandlerService.logReference(finServiceInstruction.getFinReference());

		// set Default date formats
		setDefaultDateFormats(finServiceInstruction);

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
		//restrict FLEXI Finances
		//FIXME Open when the flexi is comes in core
		/*
		 * if (restrictFlexiFinances(finServiceInstruction)) { return flexiNotAllowed("ChangeRepaymentAmount"); }
		 */
		// validate service instruction data
		AuditDetail auditDetail = addRepaymentService.doValidations(finServiceInstruction);
		if (auditDetail.getErrorDetails() != null) {
			for (ErrorDetail errorDetail : auditDetail.getErrorDetails()) {
				financeDetail = new FinanceDetail();
				doEmptyResponseObject(financeDetail);
				financeDetail.setReturnStatus(
						APIErrorHandlerService.getFailedStatus(errorDetail.getCode(), errorDetail.getError()));
				return financeDetail;
			}
		}
		// validate fees
		String eventCode = AccountEventConstants.ACCEVENT_SCDCHG;
		List<ErrorDetail> errors = doProcessServiceFees(finServiceInstruction, eventCode);
		if (!errors.isEmpty()) {
			for (ErrorDetail errorDetails : errors) {
				financeDetail = new FinanceDetail();
				doEmptyResponseObject(financeDetail);
				financeDetail.setReturnStatus(
						APIErrorHandlerService.getFailedStatus(errorDetails.getCode(), errorDetails.getError()));
				return financeDetail;
			}
		}
		// call change repay amount service
		financeDetail = finServiceInstController.doAddRepayment(finServiceInstruction, eventCode);

		logger.debug(Literal.LEAVING);
		return financeDetail;
	}

	/**
	 * Method for perform deferments operation
	 * 
	 * @param loanServicing
	 * @throws JaxenException
	 */
	@Override
	public FinanceDetail deferments(FinServiceInstruction finServiceInstruction) throws ServiceException {
		logger.debug(Literal.ENTERING);

		validationUtility.validate(finServiceInstruction, DefermentsGroup.class);
		FinanceDetail financeDetail = null;

		// for logging purpose
		APIErrorHandlerService.logReference(finServiceInstruction.getFinReference());

		// set Default date formats
		setDefaultDateFormats(finServiceInstruction);

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
		//restrict FLEXI Finances
		//FIXME Used only when flexi changes comes to core
		/*
		 * if (restrictFlexiFinances(finServiceInstruction)) { return flexiNotAllowed("Deferments"); }
		 */
		// validate service instruction data
		AuditDetail auditDetail = postponementService.doValidations(finServiceInstruction);
		if (auditDetail.getErrorDetails() != null) {
			for (ErrorDetail errorDetail : auditDetail.getErrorDetails()) {
				financeDetail = new FinanceDetail();
				doEmptyResponseObject(financeDetail);
				financeDetail.setReturnStatus(
						APIErrorHandlerService.getFailedStatus(errorDetail.getCode(), errorDetail.getError()));
				return financeDetail;
			}
		}

		// validate fees
		String eventCode = AccountEventConstants.ACCEVENT_DEFRPY;
		List<ErrorDetail> errors = doProcessServiceFees(finServiceInstruction, eventCode);
		if (!errors.isEmpty()) {
			for (ErrorDetail errorDetails : errors) {
				financeDetail = new FinanceDetail();
				doEmptyResponseObject(financeDetail);
				financeDetail.setReturnStatus(
						APIErrorHandlerService.getFailedStatus(errorDetails.getCode(), errorDetails.getError()));
				return financeDetail;
			}
		}

		// call change repay amount service
		financeDetail = finServiceInstController.doDefferment(finServiceInstruction, eventCode);

		logger.debug(Literal.LEAVING);
		return financeDetail;
	}

	/**
	 * Method for perform addTerms operation
	 * 
	 * @param loanServicing
	 * @throws JaxenException
	 */
	@Override
	public FinanceDetail addTerms(FinServiceInstruction finServiceInstruction) {
		logger.debug(Literal.ENTERING);

		// bean validations
		validationUtility.validate(finServiceInstruction, AddTermsGroup.class);
		FinanceDetail financeDetail = null;

		// for logging purpose
		APIErrorHandlerService.logReference(finServiceInstruction.getFinReference());

		// set Default date formats
		setDefaultDateFormats(finServiceInstruction);

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
		//restrict FLEXI Finances
		//FIXME Used only when flexi changes comes to core

		/*
		 * if (restrictFlexiFinances(finServiceInstruction)) { return flexiNotAllowed("AddTerms"); }
		 */
		// validate service instruction data
		AuditDetail auditDetail = addTermsService.doValidations(finServiceInstruction);
		if (auditDetail.getErrorDetails() != null) {
			for (ErrorDetail errorDetail : auditDetail.getErrorDetails()) {
				financeDetail = new FinanceDetail();
				doEmptyResponseObject(financeDetail);
				financeDetail.setReturnStatus(
						APIErrorHandlerService.getFailedStatus(errorDetail.getCode(), errorDetail.getError()));
				return financeDetail;
			}
		}

		// validate fees
		String eventCode = AccountEventConstants.ACCEVENT_SCDCHG;
		List<ErrorDetail> errors = doProcessServiceFees(finServiceInstruction, eventCode);
		if (!errors.isEmpty()) {
			for (ErrorDetail errorDetails : errors) {
				financeDetail = new FinanceDetail();
				doEmptyResponseObject(financeDetail);
				financeDetail.setReturnStatus(
						APIErrorHandlerService.getFailedStatus(errorDetails.getCode(), errorDetails.getError()));
				return financeDetail;
			}
		}

		// call addRateChange service
		financeDetail = finServiceInstController.addTerms(finServiceInstruction, eventCode);

		logger.debug(Literal.LEAVING);
		return financeDetail;
	}

	private FinanceDetail flexiNotAllowed(String service) {
		String[] valueParm = new String[2];
		valueParm[0] = "HFLEXI Loans";
		valueParm[1] = service;
		WSReturnStatus returnStatus = APIErrorHandlerService.getFailedStatus("90329", valueParm);
		FinanceDetail financeDetail = new FinanceDetail();
		doEmptyResponseObject(financeDetail);
		financeDetail.setReturnStatus(returnStatus);
		return financeDetail;
	}

	/**
	 * 
	 * @param finServiceInstruction
	 * @return
	 */
	private boolean restrictFlexiFinances(FinServiceInstruction finServiceInstruction) {

		FinanceMain financeMain = financeMainDAO.isFlexiLoan(finServiceInstruction.getFinReference());
		return financeMain.isAlwFlexi();
	}

	/**
	 * Method for perform removeTerms operation
	 * 
	 * @param loanServicing
	 * @throws JaxenException
	 */
	@Override
	public FinanceDetail removeTerms(FinServiceInstruction finServiceInstruction) {
		logger.debug(Literal.ENTERING);

		// bean validations
		validationUtility.validate(finServiceInstruction, RemoveTermsGroup.class);
		FinanceDetail financeDetail = null;

		// for logging purpose
		APIErrorHandlerService.logReference(finServiceInstruction.getFinReference());

		// set Default date formats
		setDefaultDateFormats(finServiceInstruction);

		// service level validations
		WSReturnStatus returnStatus = validateFinReference(finServiceInstruction);
		if (StringUtils.isNotBlank(returnStatus.getReturnCode())) {
			financeDetail = new FinanceDetail();
			doEmptyResponseObject(financeDetail);
			financeDetail.setReturnStatus(returnStatus);
			return financeDetail;
		}
		//restrict FLEXI Finances
		//FIXME Used only when flexi changes comes to core
		/*
		 * if (restrictFlexiFinances(finServiceInstruction)) { return flexiNotAllowed("RemoveTerms"); }
		 */
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
			for (ErrorDetail errorDetail : auditDetail.getErrorDetails()) {
				financeDetail = new FinanceDetail();
				doEmptyResponseObject(financeDetail);
				financeDetail.setReturnStatus(
						APIErrorHandlerService.getFailedStatus(errorDetail.getCode(), errorDetail.getError()));
				return financeDetail;
			}
		}
		// validate fees
		String eventCode = AccountEventConstants.ACCEVENT_SCDCHG;
		List<ErrorDetail> errors = doProcessServiceFees(finServiceInstruction, eventCode);
		if (!errors.isEmpty()) {
			for (ErrorDetail errorDetails : errors) {
				financeDetail = new FinanceDetail();
				doEmptyResponseObject(financeDetail);
				financeDetail.setReturnStatus(
						APIErrorHandlerService.getFailedStatus(errorDetails.getCode(), errorDetails.getError()));
				return financeDetail;
			}
		}
		// call removeRateChange service
		financeDetail = finServiceInstController.removeTerms(finServiceInstruction, eventCode);

		logger.debug(Literal.LEAVING);
		return financeDetail;
	}

	public FinanceDetail feePayment(FinServiceInstruction finServiceInstruction) throws ServiceException {
		logger.debug(Literal.ENTERING);

		// for logging purpose
		APIErrorHandlerService.logReference(finServiceInstruction.getFinReference());
		if (finServiceInstruction.getAmount() == null) {
			finServiceInstruction.setAmount(BigDecimal.ZERO);
		}
		// bean validations
		validationUtility.validate(finServiceInstruction, UpfrontFeesGroup.class);

		String moduleDefiner = FinanceConstants.FINSER_EVENT_FEEPAYMENT;
		//String eventCode = AccountEventConstants.ACCEVENT_REPAY;

		// set Default date formats
		setDefaultDateFormats(finServiceInstruction);

		FinanceDetail financeDetail = null;

		//vlidate duplicate record
		boolean dedupFound = checkUpFrontDuplicateRequest(finServiceInstruction, moduleDefiner);
		if (dedupFound) {
			return errorDetails();
		}
		// execute manual payment service
		financeDetail = finServiceInstController.doFeePayment(finServiceInstruction);

		logger.debug(Literal.LEAVING);
		return financeDetail;
	}

	private FinanceDetail errorDetails() {
		FinanceDetail financeDetail;
		financeDetail = new FinanceDetail();
		doEmptyResponseObject(financeDetail);
		String valueParm[] = new String[1];
		valueParm[0] = "transaction";
		financeDetail.setReturnStatus(APIErrorHandlerService.getFailedStatus("41014", valueParm));
		return financeDetail;
	}

	private boolean checkUpFrontDuplicateRequest(FinServiceInstruction finServiceInstruction, String moduleDefiner) {
		List<FinReceiptDetail> receiptDetails = finReceiptDetailDAO
				.getFinReceiptDetailByReference(finServiceInstruction.getFinReference());
		if (finServiceInstruction.getReceiptDetail() != null) {
			if (receiptDetails != null && !receiptDetails.isEmpty()) {
				for (FinReceiptDetail finReceiptDetail : receiptDetails) {
					if (finReceiptDetail.getAmount().compareTo(finServiceInstruction.getAmount()) == 0
							&& StringUtils.equals(finReceiptDetail.getTransactionRef(),
									finServiceInstruction.getReceiptDetail().getTransactionRef())) {
						return true;
					}
				}
			}
		}
		return false;
	}

	/**
	 * Method for validate the request receiving from API and do schedule recalculation
	 * 
	 * @param finServiceInstruction
	 * @return FinanceDetail
	 */
	@Override
	public FinanceDetail recalculate(FinServiceInstruction finServiceInstruction) {
		logger.debug(Literal.ENTERING);

		// bean validations
		validationUtility.validate(finServiceInstruction, RecalculateGroup.class);
		FinanceDetail financeDetail = null;

		// for logging purpose
		APIErrorHandlerService.logReference(finServiceInstruction.getFinReference());

		// set Default date formats
		setDefaultDateFormats(finServiceInstruction);

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
		//restrict FLEXI Finances
		//FIXME Used only when flexi changes comes to core

		/*
		 * if (restrictFlexiFinances(finServiceInstruction)) { return flexiNotAllowed("Recalculate"); }
		 */
		// validate service instruction data
		AuditDetail auditDetail = recalService.doValidations(finServiceInstruction);

		if (auditDetail.getErrorDetails() != null) {
			for (ErrorDetail errorDetail : auditDetail.getErrorDetails()) {
				financeDetail = new FinanceDetail();
				doEmptyResponseObject(financeDetail);
				financeDetail.setReturnStatus(
						APIErrorHandlerService.getFailedStatus(errorDetail.getCode(), errorDetail.getError()));

				return financeDetail;
			}
		}
		// validate fees
		String eventCode = AccountEventConstants.ACCEVENT_SCDCHG;
		List<ErrorDetail> errors = doProcessServiceFees(finServiceInstruction, eventCode);
		if (!errors.isEmpty()) {
			for (ErrorDetail errorDetails : errors) {
				financeDetail = new FinanceDetail();
				doEmptyResponseObject(financeDetail);
				financeDetail.setReturnStatus(
						APIErrorHandlerService.getFailedStatus(errorDetails.getCode(), errorDetails.getError()));
				return financeDetail;
			}
		}

		// call change repay amount service
		financeDetail = finServiceInstController.doRecalculate(finServiceInstruction, eventCode);

		logger.debug(Literal.LEAVING);
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
		logger.debug(Literal.ENTERING);

		// bean validations
		validationUtility.validate(finServiceInstruction, ChangeInterestGroup.class);
		FinanceDetail financeDetail = null;

		// for logging purpose
		APIErrorHandlerService.logReference(finServiceInstruction.getFinReference());

		// set Default date formats
		setDefaultDateFormats(finServiceInstruction);

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
		//restrict FLEXI Finances
		//FIXME Used only when flexi changes comes to core
		/*
		 * if (restrictFlexiFinances(finServiceInstruction)) { return flexiNotAllowed("ChangeInterest"); }
		 */
		// validate service instruction data
		AuditDetail auditDetail = changeProfitService.doValidations(finServiceInstruction);
		if (auditDetail.getErrorDetails() != null) {
			for (ErrorDetail errorDetail : auditDetail.getErrorDetails()) {
				financeDetail = new FinanceDetail();
				doEmptyResponseObject(financeDetail);
				financeDetail.setReturnStatus(
						APIErrorHandlerService.getFailedStatus(errorDetail.getCode(), errorDetail.getError()));
				return financeDetail;
			}
		}
		// validate fees
		String eventCode = AccountEventConstants.ACCEVENT_SCDCHG;
		List<ErrorDetail> errors = doProcessServiceFees(finServiceInstruction, eventCode);
		if (!errors.isEmpty()) {
			for (ErrorDetail errorDetails : errors) {
				financeDetail = new FinanceDetail();
				doEmptyResponseObject(financeDetail);
				financeDetail.setReturnStatus(
						APIErrorHandlerService.getFailedStatus(errorDetails.getCode(), errorDetails.getError()));
				return financeDetail;
			}
		}
		// call change repay amount service
		financeDetail = finServiceInstController.doChangeProfit(finServiceInstruction, eventCode);

		logger.debug(Literal.LEAVING);
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
		logger.debug(Literal.ENTERING);

		FinanceDetail financeDetail = null;
		validationUtility.validate(finServiceInstruction, AddDisbursementGroup.class);

		// for logging purpose
		APIErrorHandlerService.logReference(finServiceInstruction.getFinReference());

		// set Default date formats
		setDefaultDateFormats(finServiceInstruction);

		// set Default date formats
		setDefaultDateFormats(finServiceInstruction);

		// validate ReqType
		WSReturnStatus returnStatus = validateReqType(finServiceInstruction.getReqType());
		if (StringUtils.isNotBlank(returnStatus.getReturnCode())) {
			financeDetail = new FinanceDetail();
			doEmptyResponseObject(financeDetail);
			financeDetail.setReturnStatus(returnStatus);
			return financeDetail;
		}

		if (StringUtils.equals(finServiceInstruction.getReqType(), APIConstants.REQTYPE_POST)
				&& finServiceInstruction.getDisbursementDetails() == null) {
			financeDetail = new FinanceDetail();
			doEmptyResponseObject(financeDetail);
			String valueParm[] = new String[1];
			valueParm[0] = "DisbursementDetails";
			financeDetail.setReturnStatus(APIErrorHandlerService.getFailedStatus("90502", valueParm));
			return financeDetail;
		}

		returnStatus = validateFinReference(finServiceInstruction);
		if (StringUtils.isNotBlank(returnStatus.getReturnCode())) {
			financeDetail = new FinanceDetail();
			doEmptyResponseObject(financeDetail);
			financeDetail.setReturnStatus(returnStatus);
			return financeDetail;
		}
		String eventCode = AccountEventConstants.ACCEVENT_ADDDBSN;
		financeDetail = finServiceInstController.getFinanceDetails(finServiceInstruction, eventCode);

		if (StringUtils.equals(finServiceInstruction.getRecalType(), CalculationConstants.RPYCHG_TILLMDT)) {
			finServiceInstruction.setToDate(financeDetail.getFinScheduleData().getFinanceMain().getMaturityDate());
		}

		int count = validateBlockedFinances(finServiceInstruction.getFinReference());
		if (count > 0) {
			financeDetail = new FinanceDetail();
			doEmptyResponseObject(financeDetail);
			String valueParm[] = new String[2];
			valueParm[0] = "Disbursement";
			valueParm[1] = "FinReference: " + finServiceInstruction.getFinReference();
			financeDetail.setReturnStatus(APIErrorHandlerService.getFailedStatus("90204", valueParm));
			return financeDetail;
		}
		financeDetail.setAdvancePaymentsList(finServiceInstruction.getDisbursementDetails());
		AuditDetail auditDetail = addDisbursementService.doValidations(financeDetail, finServiceInstruction);
		if (auditDetail.getErrorDetails() != null) {
			for (ErrorDetail errorDetail : auditDetail.getErrorDetails()) {
				financeDetail = new FinanceDetail();
				doEmptyResponseObject(financeDetail);
				financeDetail.setReturnStatus(
						APIErrorHandlerService.getFailedStatus(errorDetail.getCode(), errorDetail.getError()));
				return financeDetail;
			}
		}
		// validate fees
		financeDetail.setAccountingEventCode(AccountEventConstants.ACCEVENT_ADDDBSN);
		List<ErrorDetail> errors = doProcessServiceFees(finServiceInstruction, eventCode);
		if (!errors.isEmpty()) {
			for (ErrorDetail errorDetails : errors) {
				financeDetail = new FinanceDetail();
				doEmptyResponseObject(financeDetail);
				financeDetail.setReturnStatus(
						APIErrorHandlerService.getFailedStatus(errorDetails.getCode(), errorDetails.getError()));
				return financeDetail;
			}
		}
		// call change repay amount service
		financeDetail = finServiceInstController.doAddDisbursement(finServiceInstruction, financeDetail, eventCode);

		logger.debug(Literal.LEAVING);
		return financeDetail;
	}

	private int validateBlockedFinances(String finReference) {
		return financeMainDAO.getCountByBlockedFinances(finReference);
	}

	/**
	 * Method for process changeInstallement frequency received from API
	 * 
	 * @param finServiceInstruction
	 * @return FinanceDetail
	 */
	@Override
	public FinanceDetail changeInstallmentFrq(FinServiceInstruction finServiceInstruction) {
		logger.debug(Literal.ENTERING);

		// bean validations
		validationUtility.validate(finServiceInstruction, ChangeInstallmentFrequencyGroup.class);
		FinanceDetail financeDetail = null;

		// for logging purpose
		APIErrorHandlerService.logReference(finServiceInstruction.getFinReference());

		// set Default date formats
		setDefaultDateFormats(finServiceInstruction);

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
			for (ErrorDetail errorDetail : auditDetail.getErrorDetails()) {
				financeDetail = new FinanceDetail();
				doEmptyResponseObject(financeDetail);
				financeDetail.setReturnStatus(
						APIErrorHandlerService.getFailedStatus(errorDetail.getCode(), errorDetail.getError()));
				return financeDetail;
			}
		}
		// validate fees
		String eventCode = AccountEventConstants.ACCEVENT_SCDCHG;
		List<ErrorDetail> errors = doProcessServiceFees(finServiceInstruction, eventCode);
		if (!errors.isEmpty()) {
			for (ErrorDetail errorDetails : errors) {
				financeDetail = new FinanceDetail();
				doEmptyResponseObject(financeDetail);
				financeDetail.setReturnStatus(
						APIErrorHandlerService.getFailedStatus(errorDetails.getCode(), errorDetails.getError()));
				return financeDetail;
			}
		}
		// call change repay amount service
		financeDetail = finServiceInstController.doChangeFrequency(finServiceInstruction, eventCode);

		logger.debug(Literal.LEAVING);
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
		logger.debug(Literal.ENTERING);

		// bean validations
		validationUtility.validate(finServiceInstruction, ReSchedulingGroup.class);
		FinanceDetail financeDetail = null;

		// for logging purpose
		APIErrorHandlerService.logReference(finServiceInstruction.getFinReference());

		// set Default date formats
		setDefaultDateFormats(finServiceInstruction);

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
		//restrict FLEXI Finances
		//FIXME Used only when flexi changes comes to core
		/*
		 * if (restrictFlexiFinances(finServiceInstruction)) { return flexiNotAllowed("ReScheduling"); }
		 */
		// validate service instruction data
		AuditDetail auditDetail = reScheduleService.doValidations(finServiceInstruction);
		if (auditDetail.getErrorDetails() != null) {
			for (ErrorDetail errorDetail : auditDetail.getErrorDetails()) {
				financeDetail = new FinanceDetail();
				doEmptyResponseObject(financeDetail);
				financeDetail.setReturnStatus(
						APIErrorHandlerService.getFailedStatus(errorDetail.getCode(), errorDetail.getError()));

				return financeDetail;
			}
		}
		// validate fees
		String eventCode = AccountEventConstants.ACCEVENT_SCDCHG;
		List<ErrorDetail> errors = doProcessServiceFees(finServiceInstruction, eventCode);
		if (!errors.isEmpty()) {
			for (ErrorDetail errorDetails : errors) {
				financeDetail = new FinanceDetail();
				doEmptyResponseObject(financeDetail);
				financeDetail.setReturnStatus(
						APIErrorHandlerService.getFailedStatus(errorDetails.getCode(), errorDetails.getError()));
				return financeDetail;
			}
		}
		// call change repay amount service
		financeDetail = finServiceInstController.doReSchedule(finServiceInstruction, eventCode);

		logger.debug(Literal.LEAVING);
		return financeDetail;
	}

	/**
	 * Method for perform updateLoanBasicDetails
	 * 
	 * @param finServiceInstruction
	 * @throws JaxenException
	 */
	@Override
	public WSReturnStatus updateLoanBasicDetails(FinServiceInstruction finServiceInstruction) {
		logger.debug(Literal.ENTERING);

		// bean validations
		validationUtility.validate(finServiceInstruction, UpdateLoanBasicDetailsGroup.class);
		WSReturnStatus returnStatus = validateFinReference(finServiceInstruction);
		if (StringUtils.isNotBlank(returnStatus.getReturnCode())) {
			return returnStatus;
		}

		// for logging purpose
		APIErrorHandlerService.logReference(finServiceInstruction.getFinReference());

		// set Default date formats
		setDefaultDateFormats(finServiceInstruction);

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

		logger.debug(Literal.LEAVING);
		return returnStatus;
	}

	/**
	 * Method for perform updateLoanPenaltyDetails
	 * 
	 * @param finServiceInstruction
	 * @throws JaxenException
	 */
	@Override
	public WSReturnStatus updateLoanPenaltyDetails(FinServiceInstruction finServiceInstruction) {
		logger.debug(Literal.ENTERING);

		// bean validations
		validationUtility.validate(finServiceInstruction, UpdateLoanPenaltyDetailGroup.class);

		// for logging purpose
		APIErrorHandlerService.logReference(finServiceInstruction.getFinReference());

		if (finServiceInstruction.getFinODPenaltyRate() != null) {
			if (finServiceInstruction.getFinODPenaltyRate().isApplyODPenalty()) {
				if (finServiceInstruction.getFinODPenaltyRate().getODChargeAmtOrPerc() == null) {
					finServiceInstruction.getFinODPenaltyRate().setODChargeAmtOrPerc(BigDecimal.ZERO);
				}
				if (finServiceInstruction.getFinODPenaltyRate().getODMaxWaiverPerc() == null) {
					finServiceInstruction.getFinODPenaltyRate().setODMaxWaiverPerc(BigDecimal.ZERO);
				}
				if (finServiceInstruction.getFinODPenaltyRate().getODGraceDays() <= 0) {
					return beanValidation("odGraceDays");
				}
				if (StringUtils.isBlank(finServiceInstruction.getFinODPenaltyRate().getODChargeType())) {
					return beanValidation("odChargeType");
				}
				if (StringUtils.isBlank(finServiceInstruction.getFinODPenaltyRate().getODChargeCalOn())
						&& StringUtils.equals(finServiceInstruction.getFinODPenaltyRate().getODChargeType(),
								FinanceConstants.PENALTYTYPE_PERC_ONETIME)
						|| StringUtils.equals(finServiceInstruction.getFinODPenaltyRate().getODChargeType(),
								FinanceConstants.PENALTYTYPE_PERC_ON_DUEDAYS)
						|| StringUtils.equals(finServiceInstruction.getFinODPenaltyRate().getODChargeType(),
								FinanceConstants.PENALTYTYPE_PERC_ON_PD_MTH)) {
					return beanValidation("odChargeCalOn");
				}
				if (finServiceInstruction.getFinODPenaltyRate().getODChargeAmtOrPerc().compareTo(BigDecimal.ZERO) < 0) {
					return beanValidation("odChargeAmtOrPerc");
				}
				if (finServiceInstruction.getFinODPenaltyRate().isODAllowWaiver()) {
					if (finServiceInstruction.getFinODPenaltyRate().getODMaxWaiverPerc()
							.compareTo(BigDecimal.ZERO) < 0) {
						return beanValidation("odMaxWaiverPerc");
					}
				}
			} else {
				if (finServiceInstruction.getFinODPenaltyRate().isODIncGrcDays()
						|| StringUtils.isNotBlank(finServiceInstruction.getFinODPenaltyRate().getODChargeType())
						|| StringUtils.isNotBlank(finServiceInstruction.getFinODPenaltyRate().getODChargeCalOn())
						|| finServiceInstruction.getFinODPenaltyRate().getODChargeAmtOrPerc()
								.compareTo(BigDecimal.ZERO) > 0
						|| finServiceInstruction.getFinODPenaltyRate().isODAllowWaiver()) {
					String[] valueParm = new String[1];
					return APIErrorHandlerService.getFailedStatus("90315", valueParm);
				}
			}
			if (StringUtils.equals(finServiceInstruction.getFinODPenaltyRate().getODChargeType(),
					FinanceConstants.PENALTYTYPE_FLAT)
					|| StringUtils.equals(finServiceInstruction.getFinODPenaltyRate().getODChargeType(),
							FinanceConstants.PENALTYTYPE_FLAT_ON_PD_MTH)) {
				finServiceInstruction.getFinODPenaltyRate().setODChargeCalOn("");
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
		finODPenaltyRate.setFinEffectDate(DateUtility.getAppDate());

		returnStatus = validatefinODPenaltyRate(finODPenaltyRate);
		if (StringUtils.isNotBlank(returnStatus.getReturnCode())) {
			return returnStatus;
		}

		// call addRateChange service
		returnStatus = finServiceInstController.updateLoanPenaltyDetails(finODPenaltyRate);

		logger.debug(Literal.LEAVING);
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
		try {
			String moduleDefiner = FinanceConstants.FINSER_EVENT_EARLYSETTLE;
			FinanceDetail financeDetail = receiptTransaction(finServiceInstruction, moduleDefiner);
			return financeDetail;
		} catch (AppException ex) {
			logger.error("AppException", ex);
			FinanceDetail response = new FinanceDetail();
			doEmptyResponseObject(response);
			response.setReturnStatus(APIErrorHandlerService.getFailedStatus("9999", ex.getMessage()));
			return response;
		} catch (Exception e) {
			logger.error(Literal.EXCEPTION, e);
			APIErrorHandlerService.logUnhandledException(e);
			FinanceDetail response = new FinanceDetail();
			doEmptyResponseObject(response);
			response.setReturnStatus(APIErrorHandlerService.getFailedStatus());
			return response;
		}

	}

	@Override
	public FinanceDetail partialSettlement(FinServiceInstruction finServiceInstruction) {
		try {
			String moduleDefiner = FinanceConstants.FINSER_EVENT_EARLYRPY;
			finServiceInstruction.setReceivedDate(finServiceInstruction.getReceiptDetail().getReceivedDate());
			FinanceDetail financeDetail = receiptTransaction(finServiceInstruction, moduleDefiner);
			return financeDetail;
		} catch (AppException ex) {
			logger.error("AppException", ex);
			FinanceDetail response = new FinanceDetail();
			doEmptyResponseObject(response);
			response.setReturnStatus(APIErrorHandlerService.getFailedStatus("9999", ex.getMessage()));
			return response;
		} catch (Exception e) {
			logger.error(Literal.EXCEPTION, e);
			APIErrorHandlerService.logUnhandledException(e);
			FinanceDetail response = new FinanceDetail();
			doEmptyResponseObject(response);
			response.setReturnStatus(APIErrorHandlerService.getFailedStatus());
			return response;
		}
	}

	@Override
	public FinanceDetail manualPayment(FinServiceInstruction finServiceInstruction) throws ServiceException {
		try {
			String moduleDefiner = FinanceConstants.FINSER_EVENT_SCHDRPY;
			FinanceDetail financeDetail = receiptTransaction(finServiceInstruction, moduleDefiner);
			return financeDetail;
		} catch (AppException ex) {
			logger.error("AppException", ex);
			FinanceDetail response = new FinanceDetail();
			doEmptyResponseObject(response);
			response.setReturnStatus(APIErrorHandlerService.getFailedStatus("9999", ex.getMessage()));
			return response;
		} catch (Exception e) {
			logger.error(Literal.EXCEPTION, e);
			APIErrorHandlerService.logUnhandledException(e);
			FinanceDetail response = new FinanceDetail();
			doEmptyResponseObject(response);
			response.setReturnStatus(APIErrorHandlerService.getFailedStatus());
			return response;
		}
	}

	public FinanceDetail receiptTransaction(FinServiceInstruction fsi, String moduleDefiner) throws ServiceException {
		logger.debug(Literal.ENTERING);

		String eventCode = null;
		if (StringUtils.equals(moduleDefiner, FinanceConstants.FINSER_EVENT_SCHDRPY)) {
			eventCode = AccountEventConstants.ACCEVENT_REPAY;
			fsi.setModuleDefiner(FinanceConstants.FINSER_EVENT_SCHDRPY);
			fsi.setReceiptPurpose(FinanceConstants.FINSER_EVENT_SCHDRPY);
		} else if (StringUtils.equals(moduleDefiner, FinanceConstants.FINSER_EVENT_EARLYRPY)) {
			eventCode = AccountEventConstants.ACCEVENT_EARLYPAY;
			fsi.setModuleDefiner(FinanceConstants.FINSER_EVENT_EARLYRPY);
			fsi.setReceiptPurpose(FinanceConstants.FINSER_EVENT_EARLYRPY);
			if (!fsi.isReceiptUpload()) {
				validationUtility.validate(fsi, PartialSettlementGroup.class);
			}
		} else if (StringUtils.equals(moduleDefiner, FinanceConstants.FINSER_EVENT_EARLYSETTLE)) {
			eventCode = AccountEventConstants.ACCEVENT_EARLYSTL;
			fsi.setModuleDefiner(FinanceConstants.FINSER_EVENT_EARLYSETTLE);
			fsi.setReceiptPurpose(FinanceConstants.FINSER_EVENT_EARLYSETTLE);
			if (!fsi.isReceiptUpload()) {
				validationUtility.validate(fsi, EarlySettlementGroup.class);
			}
		}

		// Method for validate instruction details
		FinanceDetail financeDetail = new FinanceDetail();
		FinScheduleData finScheduleData = financeDetail.getFinScheduleData();

		if (fsi.getValueDate() == null) {
			fsi.setValueDate(fsi.getReceivedDate());
			fsi.getReceiptDetail().setValueDate(fsi.getReceivedDate());
		}

		fsi.setReceivedDate(fsi.getReceiptDetail().getReceivedDate());
		finScheduleData.setFinServiceInstruction(fsi);
		financeDetail = validateInstructions(financeDetail, moduleDefiner, eventCode);
		FinReceiptData receiptData = receiptService.doReceiptValidations(financeDetail, moduleDefiner);
		financeDetail = receiptData.getFinanceDetail();
		finScheduleData = financeDetail.getFinScheduleData();

		if (finScheduleData.getErrorDetails() != null && !finScheduleData.getErrorDetails().isEmpty()) {
			logger.debug("Leaving - doReceiptValidations Error");
			return setReturnStatus(financeDetail);
		}

		receiptService.setReceiptData(receiptData);
		financeDetail = finServiceInstController.doReceiptTransaction(receiptData, eventCode);

		if (financeDetail.getFinScheduleData() != null && financeDetail.getFinScheduleData().getErrorDetails() != null
				&& !financeDetail.getFinScheduleData().getErrorDetails().isEmpty()) {
			financeDetail = setReturnStatus(financeDetail);
		}

		logger.debug(Literal.LEAVING);
		return financeDetail;
	}

	public FinanceDetail setReturnStatus(FinanceDetail financeDetail) {

		WSReturnStatus returnStatus = new WSReturnStatus();
		ErrorDetail errorDetail = financeDetail.getFinScheduleData().getErrorDetails().get(0);
		returnStatus.setReturnCode(errorDetail.getCode());
		returnStatus.setReturnText(errorDetail.getError());
		financeDetail.setFinScheduleData(null);
		financeDetail.setDocumentDetailsList(null);
		financeDetail.setJountAccountDetailList(null);
		financeDetail.setGurantorsDetailList(null);
		financeDetail.setCollateralAssignmentList(null);
		financeDetail.setReturnDataSetList(null);
		financeDetail.setInterfaceDetailList(null);
		financeDetail.setFinFlagsDetails(null);
		financeDetail.setCustomerDetails(null);
		financeDetail.setReturnStatus(returnStatus);
		return financeDetail;
	}

	/**
	 * Method for perform Schedule method Change action by taking specified instructions.
	 * 
	 * @param finServiceInstruction
	 * @return FinanceDetail
	 */
	@Override
	public FinanceDetail scheduleMethodChange(FinServiceInstruction finServiceInstruction) throws ServiceException {
		logger.debug(Literal.ENTERING);

		// bean validations
		validationUtility.validate(finServiceInstruction, ScheduleMethodGroup.class);
		FinanceDetail financeDetail = null;

		// set Default date formats
		setDefaultDateFormats(finServiceInstruction);

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
		//restrict FLEXI Finances
		//FIXME Used only when flexi changes comes to core
		/*
		 * if (restrictFlexiFinances(finServiceInstruction)) { return flexiNotAllowed("Schedule Change Method"); }
		 */
		//Step Loan not accepted FIXME
		FinanceMain finMain = financeMainDAO.getFinanceMainById(finServiceInstruction.getFinReference(), "",
				finServiceInstruction.isWif());
		if (finMain.isStepFinance()) {
			String[] valueParm = new String[2];
			valueParm[0] = "Step Loan";
			valueParm[1] = "Schedule Change Method";
			returnStatus = APIErrorHandlerService.getFailedStatus("90329", valueParm);
			financeDetail = new FinanceDetail();
			doEmptyResponseObject(financeDetail);
			financeDetail.setReturnStatus(returnStatus);
			return financeDetail;
		}
		// validate service instruction data
		AuditDetail auditDetail = changeScheduleMethodService.doValidations(finServiceInstruction);
		if (auditDetail.getErrorDetails() != null) {
			for (ErrorDetail errorDetail : auditDetail.getErrorDetails()) {
				financeDetail = new FinanceDetail();
				doEmptyResponseObject(financeDetail);
				financeDetail.setReturnStatus(
						APIErrorHandlerService.getFailedStatus(errorDetail.getCode(), errorDetail.getError()));

				return financeDetail;
			}
		}
		// validate fees
		String eventCode = AccountEventConstants.ACCEVENT_SCDCHG;
		List<ErrorDetail> errors = doProcessServiceFees(finServiceInstruction, eventCode);
		if (!errors.isEmpty()) {
			for (ErrorDetail errorDetails : errors) {
				financeDetail = new FinanceDetail();
				doEmptyResponseObject(financeDetail);
				financeDetail.setReturnStatus(
						APIErrorHandlerService.getFailedStatus(errorDetails.getCode(), errorDetails.getError()));
				return financeDetail;
			}
		}
		// call change repay amount service
		financeDetail = finServiceInstController.doChangeScheduleMethod(finServiceInstruction, eventCode);

		logger.debug(Literal.LEAVING);
		return financeDetail;
	}

	/**
	 * Method for perform changePureflexiTenure action by taking specified instructions.
	 * 
	 * @param finServiceInstruction
	 * @return FinanceDetail
	 */
	@Override
	public FinanceDetail changeGestationPeriod(FinServiceInstruction finServiceInstruction) throws ServiceException {
		logger.debug(Literal.ENTERING);

		// bean validations
		validationUtility.validate(finServiceInstruction, ChangeGestationGroup.class);
		FinanceDetail financeDetail = null;

		// set Default date formats
		setDefaultDateFormats(finServiceInstruction);

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

		// validate fees
		String eventCode = AccountEventConstants.ACCEVENT_GRACEEND;
		List<ErrorDetail> errors = doProcessServiceFees(finServiceInstruction, eventCode);
		if (!errors.isEmpty()) {
			for (ErrorDetail errorDetails : errors) {
				financeDetail = new FinanceDetail();
				doEmptyResponseObject(financeDetail);
				financeDetail.setReturnStatus(
						APIErrorHandlerService.getFailedStatus(errorDetails.getCode(), errorDetails.getError()));
				return financeDetail;
			}
		}
		// call change repay amount service
		financeDetail = finServiceInstController.doChangeGestationPeriod(finServiceInstruction, eventCode);

		logger.debug(Literal.LEAVING);
		return financeDetail;
	}

	/**
	 * Method for validate instruction details
	 * 
	 * @param finServiceInstruction
	 * @param moduleDefiner
	 * @param eventCode
	 * @return
	 */
	/**
	 * Method for validate instruction details
	 * 
	 * @param finServiceInstruction
	 * @param moduleDefiner
	 * @param eventCode
	 * @return
	 */
	private FinanceDetail validateInstructions(FinanceDetail financeDetail, String moduleDefiner, String eventCode) {
		logger.debug(Literal.ENTERING);

		FinScheduleData finScheduleData = financeDetail.getFinScheduleData();
		FinServiceInstruction finServiceInstruction = finScheduleData.getFinServiceInstruction();
		String finReference = finServiceInstruction.getFinReference();
		financeDetail.setFinReference(finReference);
		ErrorDetail errorDetail = new ErrorDetail();

		WSReturnStatus returnStatus = validateReqType(finServiceInstruction.getReqType());
		if (StringUtils.isNotBlank(returnStatus.getReturnCode())) {
			errorDetail.setCode(returnStatus.getReturnCode());
			errorDetail.setMessage(returnStatus.getReturnText());
			errorDetail.setExtendedMessage(returnStatus.getReturnText());
			finScheduleData.setErrorDetail(errorDetail);
			return financeDetail;
		}

		logger.debug(Literal.LEAVING);
		return financeDetail;
	}

	private WSReturnStatus beanValidation(String valueParam) {
		String[] valueParm = new String[1];
		valueParm[0] = valueParam;
		return APIErrorHandlerService.getFailedStatus("90502", valueParm);
	}

	private WSReturnStatus validatefinODPenaltyRate(FinODPenaltyRate finODPenaltyRate) {
		logger.debug(Literal.ENTERING);

		WSReturnStatus returnStatus = new WSReturnStatus();
		if (StringUtils.isNotBlank(finODPenaltyRate.getODChargeType())) {
			List<ValueLabel> finODChargeType = PennantStaticListUtil.getODCChargeType();
			boolean finODChargeTypeSts = false;
			for (ValueLabel value : finODChargeType) {
				if (StringUtils.equals(value.getValue(), finODPenaltyRate.getODChargeType())) {
					finODChargeTypeSts = true;
					break;
				}
			}
			if (!finODChargeTypeSts) {
				String[] valueParm = new String[2];
				valueParm[0] = finODPenaltyRate.getODChargeType();
				valueParm[1] = FinanceConstants.PENALTYTYPE_FLAT + "," + FinanceConstants.PENALTYTYPE_FLAT_ON_PD_MTH
						+ "," + FinanceConstants.PENALTYTYPE_PERC_ON_DUEDAYS + ","
						+ FinanceConstants.PENALTYTYPE_PERC_ON_PD_MTH + "," + FinanceConstants.PENALTYTYPE_PERC_ONETIME;
				return getErrorDetails("90316", valueParm);
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
			if (!odChargeCalOnSts && (StringUtils.equals(finODPenaltyRate.getODChargeType(),
					FinanceConstants.PENALTYTYPE_PERC_ONETIME)
					|| StringUtils.equals(finODPenaltyRate.getODChargeType(),
							FinanceConstants.PENALTYTYPE_PERC_ON_DUEDAYS)
					|| StringUtils.equals(finODPenaltyRate.getODChargeType(),
							FinanceConstants.PENALTYTYPE_PERC_ON_PD_MTH))) {
				String[] valueParm = new String[2];
				valueParm[0] = finODPenaltyRate.getODChargeCalOn();
				valueParm[1] = FinanceConstants.ODCALON_STOT + "," + FinanceConstants.ODCALON_SPFT + ","
						+ FinanceConstants.ODCALON_SPRI;
				return getErrorDetails("90317", valueParm);
			}
		}
		if (StringUtils.equals(finODPenaltyRate.getODChargeType(), FinanceConstants.PENALTYTYPE_PERC_ONETIME)
				|| StringUtils.equals(finODPenaltyRate.getODChargeType(), FinanceConstants.PENALTYTYPE_PERC_ON_DUEDAYS)
				|| StringUtils.equals(finODPenaltyRate.getODChargeType(),
						FinanceConstants.PENALTYTYPE_PERC_ON_PD_MTH)) {
			if (finODPenaltyRate.getODChargeAmtOrPerc().compareTo(new BigDecimal(100)) > 0) {
				String[] valueParm = new String[2];
				valueParm[0] = "ODChargeAmtOrPerc";
				valueParm[1] = "100";
				return getErrorDetails("30565", valueParm);
			}
			BigDecimal totPerc = PennantApplicationUtil.unFormateAmount(finODPenaltyRate.getODChargeAmtOrPerc(), 2);
			finODPenaltyRate.setODChargeAmtOrPerc(totPerc);
		}
		if (!(finODPenaltyRate.isApplyODPenalty() && finODPenaltyRate.isODAllowWaiver())) {
			if (finODPenaltyRate.getODMaxWaiverPerc().compareTo(BigDecimal.ZERO) > 0) {
				String[] valueParm = new String[2];
				valueParm[0] = "ODMaxWaiverPerc";
				valueParm[1] = "ODAllowWaiver is disabled";
				return getErrorDetails("90329", valueParm);
			}
		} else {
			if (finODPenaltyRate.getODMaxWaiverPerc().compareTo(BigDecimal.ZERO) <= 0) {
				String[] valueParm = new String[2];
				valueParm[0] = "ODMaxWaiverPerc";
				valueParm[1] = "Zero";
				return getErrorDetails("91121", valueParm);
			} else if (finODPenaltyRate.getODMaxWaiverPerc().compareTo(new BigDecimal(100)) > 0) {
				String[] valueParm = new String[2];
				valueParm[0] = "ODChargeAmtOrPerc";
				valueParm[1] = "100";
				return getErrorDetails("30565", valueParm);
			}
		}
		logger.debug(Literal.LEAVING);
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
		logger.debug(Literal.ENTERING);

		WSReturnStatus response = new WSReturnStatus();
		response = APIErrorHandlerService.getFailedStatus(errorCode, valueParm);

		// set default error code and description in case of Error code does not
		// exists.
		if (StringUtils.isBlank(response.getReturnCode())) {
			response = APIErrorHandlerService.getFailedStatus(APIConstants.RES_FAILED_CODE,
					APIConstants.RES_FAILED_DESC);
		}

		logger.debug(Literal.LEAVING);
		return response;
	}

	/**
	 * validate request type received from API.
	 * 
	 * @param reqType
	 * @return WSReturnStatus
	 */
	private WSReturnStatus validateReqType(String reqType) {
		logger.debug(Literal.ENTERING);
		WSReturnStatus status = new WSReturnStatus();

		if (!StringUtils.equals(reqType, APIConstants.REQTYPE_INQUIRY)
				&& !StringUtils.equals(reqType, APIConstants.REQTYPE_POST)) {
			String valueParm[] = new String[1];
			valueParm[0] = reqType;
			status = APIErrorHandlerService.getFailedStatus("91113", valueParm);
		}

		logger.debug(Literal.LEAVING);
		return status;
	}

	/**
	 * Method for validate finance reference and check existence in origination or WIF
	 * 
	 * @param finReference
	 * @return
	 */
	private WSReturnStatus validateFinReference(FinServiceInstruction serviceInst) {
		logger.debug(Literal.ENTERING);

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

		logger.debug(Literal.LEAVING);
		return returnStatus;
	}

	/**
	 * Method for process servicing fees
	 * 
	 * @param finSrvcInst
	 * @param eventCode
	 * @return
	 */
	private List<ErrorDetail> doProcessServiceFees(FinServiceInstruction finSrvcInst, String eventCode) {
		logger.debug(Literal.ENTERING);
		List<ErrorDetail> errors = new ArrayList<ErrorDetail>();
		if (StringUtils.equals(finSrvcInst.getReqType(), APIConstants.REQTYPE_INQUIRY)) {
			if (finSrvcInst.getFinFeeDetails() != null && !finSrvcInst.getFinFeeDetails().isEmpty()) {
				errors = financeDataValidation.doFeeValidations(PennantConstants.VLD_SRV_LOAN, finSrvcInst, eventCode);
			}
		} else {
			if (finSrvcInst.isReceiptUpload()) {// FIXME
				FinanceDetail financeDetail = new FinanceDetail();
				financeDetail = receiptService.getFinanceDetail(finSrvcInst, eventCode, financeDetail);
				try {
					BigDecimal dueAmount = getDueAmount(finSrvcInst, financeDetail, eventCode);
					BigDecimal extraAmount = finSrvcInst.getAmount().subtract(dueAmount);
					financeDetail.getFinScheduleData().getFinanceMain().setRepayAmount(extraAmount);
					feeDetailService.doProcessFeesForInquiryForUpload(financeDetail, eventCode, finSrvcInst, true);
					buildFinFeeForUpload(finSrvcInst);

				} catch (Exception e) {
					logger.error("Exception: " + e);
				}
			}
			errors = financeDataValidation.doFeeValidations(PennantConstants.VLD_SRV_LOAN, finSrvcInst, eventCode);
		}
		logger.debug(Literal.LEAVING);
		return errors;
	}

	/**
	 * Method for get Loan Reference,Customer CIF, Customer Name.
	 */
	@Override
	public LoanPendingDetails getLoanPendingDetailsByUserName(String userName) throws ServiceException {
		logger.debug(Literal.ENTERING);

		LoanPendingDetails custLoanDetails = new LoanPendingDetails();
		List<LoanPendingData> customerODLoanData = null;

		long userID = securityUserDAO.getUserByName(userName);

		if (userID > 0) {
			customerODLoanData = finServiceInstController.getCustomerODLoanDetails(userID);
		} else {
			LoanPendingDetails error = new LoanPendingDetails();
			String[] param = new String[2];
			param[0] = "User Name";
			param[1] = String.valueOf(userName);
			error.setReturnStatus(APIErrorHandlerService.getFailedStatus("90224", param));
			return error;
		}

		if (customerODLoanData == null || CollectionUtils.isEmpty(customerODLoanData)) {
			LoanPendingDetails error = new LoanPendingDetails();
			String[] param = new String[2];
			param[0] = "User ID";
			param[1] = String.valueOf(userID);
			error.setReturnStatus(APIErrorHandlerService.getFailedStatus("90224", param));
			return error;
		}

		if (customerODLoanData != null) {
			custLoanDetails.setCustomerODLoanDataList(customerODLoanData);
		}

		custLoanDetails.setReturnStatus(APIErrorHandlerService.getSuccessStatus());

		logger.debug(Literal.ENTERING);
		return custLoanDetails;
	}

	@Override
	public FinanceTaxDetail fetchGSTDetails(String finReference) throws ServiceException {

		logger.info(Literal.ENTERING);

		FinanceTaxDetail financeTaxDetails = null;
		int count = financeMainDAO.getFinanceCountById(finReference, " ", false);
		if (count <= 0) {
			financeTaxDetails = new FinanceTaxDetail();
			String[] valueParam = new String[1];
			valueParam[0] = finReference;

			financeTaxDetails.setReturnStatus(APIErrorHandlerService.getFailedStatus("90201", valueParam));
			return financeTaxDetails;
		} else {
			financeTaxDetails = financeTaxDetailService.getApprovedFinanceTaxDetail(finReference);
			if (null == financeTaxDetails) {
				financeTaxDetails = new FinanceTaxDetail();
				String[] valueParam = new String[1];
				valueParam[0] = finReference;

				financeTaxDetails.setReturnStatus(APIErrorHandlerService.getFailedStatus("90266", valueParam));
				return financeTaxDetails;
			}
		}

		logger.info(Literal.LEAVING);

		return financeTaxDetails;
	}

	@Override
	public WSReturnStatus addGSTDetails(final FinanceTaxDetail financeTaxDetail) throws ServiceException {

		logger.info(Literal.ENTERING);

		WSReturnStatus returnStatus = new WSReturnStatus();

		List<ErrorDetail> validationErrors = financeTaxDetailService.doGSTValidations(financeTaxDetail);
		if (CollectionUtils.isEmpty(validationErrors)) {
			List<ErrorDetail> coApplicantErrors = financeTaxDetailService.verifyCoApplicantDetails(financeTaxDetail);
			if (CollectionUtils.isEmpty(coApplicantErrors)) {
				int taxDetailRecords = financeTaxDetailService
						.getFinanceTaxDetailsByCount(financeTaxDetail.getFinReference());
				if (taxDetailRecords > 0) {
					String[] valueParm = new String[1];
					valueParm[0] = financeTaxDetail.getFinReference();

					return APIErrorHandlerService.getFailedStatus("90248", valueParm);
				}

				returnStatus = finServiceInstController.saveGSTDetails(financeTaxDetail);
			} else {
				for (ErrorDetail errorDetail : coApplicantErrors) {
					returnStatus = APIErrorHandlerService.getFailedStatus(errorDetail.getCode(),
							errorDetail.getParameters());
				}
			}
		} else {
			for (ErrorDetail errorDetail : validationErrors) {
				returnStatus = APIErrorHandlerService.getFailedStatus(errorDetail.getCode(),
						errorDetail.getParameters());
			}
		}

		logger.info(Literal.LEAVING);

		return returnStatus;
	}

	@Override
	public WSReturnStatus updateGSTDetails(final FinanceTaxDetail financeTaxDetail) throws ServiceException {

		logger.info(Literal.ENTERING);

		String finReference = financeTaxDetail.getFinReference();
		WSReturnStatus returnStatus = new WSReturnStatus();

		List<ErrorDetail> validationErrors = financeTaxDetailService.doGSTValidations(financeTaxDetail);
		if (CollectionUtils.isEmpty(validationErrors)) {
			FinanceTaxDetail currentFinanceTaxData = finServiceInstController.getFinanceTaxDetails(finReference);
			if (null != currentFinanceTaxData) {
				List<ErrorDetail> coApplicantErrors = financeTaxDetailService
						.verifyCoApplicantDetails(financeTaxDetail);
				if (CollectionUtils.isEmpty(coApplicantErrors)) {
					int taxDetailRecords = financeTaxDetailService
							.getFinanceTaxDetailsByCount(financeTaxDetail.getFinReference());
					if (taxDetailRecords > 0) {
						String[] valueParm = new String[1];
						valueParm[0] = financeTaxDetail.getFinReference();

						return APIErrorHandlerService.getFailedStatus("90248", valueParm);
					}
					returnStatus = finServiceInstController.rejuvenateGSTDetails(financeTaxDetail,
							currentFinanceTaxData.getVersion());
				} else {
					for (ErrorDetail errorDetail : coApplicantErrors) {
						returnStatus = APIErrorHandlerService.getFailedStatus(errorDetail.getCode(),
								errorDetail.getParameters());
					}
				}
			} else {
				String[] valueParm = new String[1];
				valueParm[0] = financeTaxDetail.getFinReference();
				returnStatus = APIErrorHandlerService.getFailedStatus("90266", valueParm);
				return returnStatus;
			}
		} else {
			for (ErrorDetail errorDetail : validationErrors) {
				returnStatus = APIErrorHandlerService.getFailedStatus(errorDetail.getCode(),
						errorDetail.getParameters());
			}
		}

		logger.info(Literal.LEAVING);

		return returnStatus;
	}

	@Override
	public WSReturnStatus approveDisbursementResponse(DisbRequest disbRequest) throws ServiceException {

		WSReturnStatus returnStatus = new WSReturnStatus();
		// validation
		returnStatus = validateDisbursementResponse(disbRequest);
		if (returnStatus != null) {
			return returnStatus;
		}
		int count = financeMainDAO.getFinanceCountById(disbRequest.getFinReference(), " ", false);
		if (count <= 0) {
			String[] valueParam = new String[1];
			valueParam[0] = disbRequest.getFinReference();
			return APIErrorHandlerService.getFailedStatus("90201", valueParam);
		} else {
			returnStatus = finServiceInstController.approveDisbursementResponse(disbRequest);
		}
		return returnStatus;
	}

	private WSReturnStatus validateDisbursementResponse(DisbRequest disbRequest) {
		logger.info(Literal.ENTERING);

		if (StringUtils.isBlank(disbRequest.getFinReference())) {
			String[] valueParam = new String[1];
			valueParam[0] = "FinReference";
			return APIErrorHandlerService.getFailedStatus("90502", valueParam);
		}

		if (StringUtils.isBlank(disbRequest.getType())) {
			String[] valueParam = new String[1];
			valueParam[0] = "Type";
			return APIErrorHandlerService.getFailedStatus("90502", valueParam);
		} else {
			if (!(StringUtils.equals("D", disbRequest.getType()) || StringUtils.equals("P", disbRequest.getType())
					|| StringUtils.equals("I", disbRequest.getType()))) {
				String[] valueParam = new String[2];
				valueParam[0] = "Type";
				valueParam[1] = "D," + "P," + "I";
				return APIErrorHandlerService.getFailedStatus("90337", valueParam);
			}
		}
		if (disbRequest.getPaymentId() < 0) {
			String[] valueParam = new String[2];
			valueParam[0] = "PaymentId";
			valueParam[1] = "1";
			return APIErrorHandlerService.getFailedStatus("90205", valueParam);
		}
		if (disbRequest.getClearingDate() == null) {
			String[] valueParam = new String[1];
			valueParam[0] = "ClearingDate";
			return APIErrorHandlerService.getFailedStatus("90502", valueParam);
		}
		if (StringUtils.isBlank(disbRequest.getStatus())) {
			String[] valueParam = new String[1];
			valueParam[0] = "Status";
			return APIErrorHandlerService.getFailedStatus("90502", valueParam);
		} else {
			if (!(StringUtils.equals("R", disbRequest.getStatus())
					|| StringUtils.equals("E", disbRequest.getStatus()))) {
				String[] valueParam = new String[2];
				valueParam[0] = "Status";
				valueParam[1] = "P," + "R";
				return APIErrorHandlerService.getFailedStatus("90337", valueParam);
			}
		}
		if (StringUtils.equals("R", disbRequest.getStatus()) && StringUtils.isBlank(disbRequest.getRejectReason())) {
			String[] valueParam = new String[1];
			valueParam[0] = "RejectReason";
			return APIErrorHandlerService.getFailedStatus("90502", valueParam);
		}

		if (StringUtils.isBlank(disbRequest.getDisbType())) {
			String[] valueParam = new String[1];
			valueParam[0] = "DisbType";
			return APIErrorHandlerService.getFailedStatus("90502", valueParam);
		} else {

			List<ValueLabel> paymentTypes = PennantStaticListUtil.getPaymentTypesWithIST();
			boolean paymentTypeSts = false;
			for (ValueLabel value : paymentTypes) {
				if (StringUtils.equals(value.getValue(), disbRequest.getDisbType())) {
					paymentTypeSts = true;
					break;
				}
			}
			if (!paymentTypeSts) {
				String[] valueParm = new String[1];
				valueParm[0] = disbRequest.getDisbType();
				return APIErrorHandlerService.getFailedStatus("90216", valueParm);
			}

		}
		if (StringUtils.equals(disbRequest.getDisbType(), DisbursementConstants.PAYMENT_TYPE_CHEQUE)
				|| StringUtils.equals(disbRequest.getDisbType(), DisbursementConstants.PAYMENT_TYPE_DD)) {
			if (StringUtils.isBlank(disbRequest.getChequeNo())) {
				String[] valueParam = new String[1];
				valueParam[0] = "ChequeNo";
				return APIErrorHandlerService.getFailedStatus("90502", valueParam);
			}
			if (disbRequest.getDisbDate() == null) {
				String[] valueParam = new String[1];
				valueParam[0] = "DisbDate";
				return APIErrorHandlerService.getFailedStatus("90502", valueParam);
			}
		}
		logger.info(Literal.LEAVING);
		return null;
	}

	/**
	 * Method to get DisbusmentDetails based on finReference
	 * 
	 * @param finReference
	 */
	@Override
	public FinAdvPaymentDetail getDisbursmentDetails(String finReference) throws ServiceException {
		logger.debug(Literal.ENTERING);

		List<DisbResponse> disbResponse = new ArrayList<DisbResponse>();
		// Mandatory validation
		if (StringUtils.isBlank(finReference)) {
			validationUtility.fieldLevelException();
		}
		// for logging purpose
		APIErrorHandlerService.logReference(finReference);
		FinAdvPaymentDetail response = new FinAdvPaymentDetail();
		// validation
		int count = financeMainDAO.getFinanceCountById(finReference, " ", false);
		if (count <= 0) {
			String[] valueParam = new String[1];
			valueParam[0] = finReference;
			response.setReturnStatus(APIErrorHandlerService.getFailedStatus("90201", valueParam));
		} else {
			List<FinAdvancePayments> fapList = finAdvancePaymentsService.getFinAdvancePaymentsById(finReference, " ");
			for (FinAdvancePayments fap : fapList) {
				DisbResponse detail = new DisbResponse();
				detail.setPaymentId(fap.getPaymentId());
				detail.setAccountNo(fap.getBeneficiaryAccNo());
				detail.setDisbAmount(fap.getAmtToBeReleased());
				detail.setDisbDate(fap.getLlDate());
				detail.setStatus(fap.getStatus()); //FIXME set the actual status after marking the disbursement status as AC
				disbResponse.add(detail);
			}

			if (CollectionUtils.isEmpty(disbResponse)) {
				String[] valueParam = new String[2];
				valueParam[0] = "There is no pending disbursement instructions to update the status";
				valueParam[1] = "FinReference " + finReference;
				response.setReturnStatus(APIErrorHandlerService.getFailedStatus("21005", valueParam));
				return response;
			}
			response.setDisbResponse(disbResponse);
			response.setReturnStatus(APIErrorHandlerService.getSuccessStatus());
		}
		logger.info(Literal.LEAVING);
		return response;
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
		detail.setReturnDataSetList(null);
	}

	/**
	 * Set Default date formats for calculation purpose.
	 * 
	 * @param finServInst
	 */
	private void setDefaultDateFormats(FinServiceInstruction finServInst) {
		if (finServInst.getFromDate() != null) {
			finServInst.setFromDate(DateUtility
					.getDBDate(DateUtility.format(finServInst.getFromDate(), PennantConstants.DBDateFormat)));
		}

		if (finServInst.getToDate() != null) {
			finServInst.setToDate(
					DateUtility.getDBDate(DateUtility.format(finServInst.getToDate(), PennantConstants.DBDateFormat)));
		}
		if (finServInst.getRecalFromDate() != null) {
			finServInst.setRecalFromDate(DateUtility
					.getDBDate(DateUtility.format(finServInst.getRecalFromDate(), PennantConstants.DBDateFormat)));
		}
		if (finServInst.getRecalToDate() != null) {
			finServInst.setRecalToDate(DateUtility
					.getDBDate(DateUtility.format(finServInst.getRecalToDate(), PennantConstants.DBDateFormat)));
		}
		if (finServInst.getGrcPeriodEndDate() != null) {
			finServInst.setGrcPeriodEndDate(DateUtility
					.getDBDate(DateUtility.format(finServInst.getGrcPeriodEndDate(), PennantConstants.DBDateFormat)));
		}
		if (finServInst.getNextGrcRepayDate() != null) {
			finServInst.setNextGrcRepayDate(DateUtility
					.getDBDate(DateUtility.format(finServInst.getNextGrcRepayDate(), PennantConstants.DBDateFormat)));
		}
		if (finServInst.getNextRepayDate() != null) {
			finServInst.setNextRepayDate(DateUtility
					.getDBDate(DateUtility.format(finServInst.getNextRepayDate(), PennantConstants.DBDateFormat)));
		}
	}

	private void setDefaultForReferenceFields(FinServiceInstruction finServiceInstruction) {
		if (StringUtils.isBlank(finServiceInstruction.getBaseRate())) {
			finServiceInstruction.setBaseRate(StringUtils.trimToNull(finServiceInstruction.getBaseRate()));
		}
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
	public void setReceiptService(ReceiptService receiptService) {
		this.receiptService = receiptService;
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

	@Autowired
	public void setAddTermsService(AddTermsService addTermsService) {
		this.addTermsService = addTermsService;
	}

	@Autowired
	public void setChangeScheduleMethodService(ChangeScheduleMethodService changeScheduleMethodService) {
		this.changeScheduleMethodService = changeScheduleMethodService;
	}

	@Autowired
	public void setFinReceiptDetailDAO(FinReceiptDetailDAO finReceiptDetailDAO) {
		this.finReceiptDetailDAO = finReceiptDetailDAO;
	}

	@Autowired
	public void setCancelDisbursementService(CancelDisbursementService cancelDisbursementService) {
		this.cancelDisbursementService = cancelDisbursementService;
	}

	public CreateFinanceController getCreateFinanceController() {
		return createFinanceController;
	}

	@Autowired
	public void setCreateFinanceController(CreateFinanceController createFinanceController) {
		this.createFinanceController = createFinanceController;
	}

	public SecurityUserDAO getSecurityUserDAO() {
		return securityUserDAO;
	}

	@Autowired
	public void setSecurityUserDAO(SecurityUserDAO securityUserDAO) {
		this.securityUserDAO = securityUserDAO;
	}

	public BigDecimal getDueAmount(FinServiceInstruction finSerInst, FinanceDetail financeDetail, String eventCode) {
		BigDecimal dueAmount = BigDecimal.ZERO;
		FinReceiptData receiptData = new FinReceiptData();
		receiptData.setTotReceiptAmount(finSerInst.getAmount());
		receiptData.getReceiptHeader().setValueDate(finSerInst.getValueDate());
		receiptData.getReceiptHeader().setReceiptPurpose(finSerInst.getReceiptPurpose());

		receiptData = receiptCalculator.recalAutoAllocation(receiptData, finSerInst.getValueDate(), false);
		List<ReceiptAllocationDetail> allocationList = receiptData.getReceiptHeader().getAllocations();

		for (int i = 0; i < allocationList.size(); i++) {
			ReceiptAllocationDetail allocate = allocationList.get(i);
			dueAmount = dueAmount.add(allocate.getTotalDue());
		}
		return dueAmount;

	}

	private void buildFinFeeForUpload(FinServiceInstruction finSrvcInst) {
		for (FinFeeDetail feeDtl : finSrvcInst.getFinFeeDetails()) {
			feeDtl.setFeeScheduleMethod("");
		}
	}

	@Autowired
	public void setFeeDetailService(FeeDetailService feeDetailService) {
		this.feeDetailService = feeDetailService;
	}

	@Autowired
	public void setReceiptCalculator(ReceiptCalculator receiptCalculator) {
		this.receiptCalculator = receiptCalculator;
	}

	@Autowired
	public void setFinanceTaxDetailsService(FinanceTaxDetailService financeTaxDetailService) {
		this.financeTaxDetailService = financeTaxDetailService;
	}

	@Autowired
	public void setFinAdvancePaymentsService(FinAdvancePaymentsService finAdvancePaymentsService) {
		this.finAdvancePaymentsService = finAdvancePaymentsService;
	}

}
