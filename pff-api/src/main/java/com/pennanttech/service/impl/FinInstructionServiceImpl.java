package com.pennanttech.service.impl;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jaxen.JaxenException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.pennant.app.constants.AccountEventConstants;
import com.pennant.app.constants.CalculationConstants;
import com.pennant.app.util.DateUtility;
import com.pennant.app.util.ErrorUtil;
import com.pennant.app.util.ReceiptCalculator;
import com.pennant.app.util.SessionUserDetails;
import com.pennant.backend.dao.administration.SecurityUserDAO;
import com.pennant.backend.dao.applicationmaster.BranchDAO;
import com.pennant.backend.dao.configuration.VASConfigurationDAO;
import com.pennant.backend.dao.configuration.VASRecordingDAO;
import com.pennant.backend.dao.finance.FinAdvancePaymentsDAO;
import com.pennant.backend.dao.finance.FinanceMainDAO;
import com.pennant.backend.dao.finance.FinanceScheduleDetailDAO;
import com.pennant.backend.dao.insurance.InsuranceDetailDAO;
import com.pennant.backend.dao.pdc.ChequeDetailDAO;
import com.pennant.backend.dao.pdc.ChequeHeaderDAO;
import com.pennant.backend.dao.receipts.FinReceiptDetailDAO;
import com.pennant.backend.dao.systemmasters.VASProviderAccDetailDAO;
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
import com.pennant.backend.model.applicationmaster.Branch;
import com.pennant.backend.model.applicationmaster.LoanPendingData;
import com.pennant.backend.model.applicationmaster.LoanPendingDetails;
import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.configuration.VASConfiguration;
import com.pennant.backend.model.configuration.VASRecording;
import com.pennant.backend.model.customermasters.Customer;
import com.pennant.backend.model.finance.ChequeDetail;
import com.pennant.backend.model.finance.ChequeHeader;
import com.pennant.backend.model.finance.FinAdvancePayments;
import com.pennant.backend.model.finance.FinFeeDetail;
import com.pennant.backend.model.finance.FinODPenaltyRate;
import com.pennant.backend.model.finance.FinReceiptData;
import com.pennant.backend.model.finance.FinReceiptDetail;
import com.pennant.backend.model.finance.FinScheduleData;
import com.pennant.backend.model.finance.FinServiceInstruction;
import com.pennant.backend.model.finance.FinanceDetail;
import com.pennant.backend.model.finance.FinanceMain;
import com.pennant.backend.model.finance.FinanceScheduleDetail;
import com.pennant.backend.model.finance.ReceiptAllocationDetail;
import com.pennant.backend.model.finance.covenant.Covenant;
import com.pennant.backend.model.finance.financetaxdetail.FinanceTaxDetail;
import com.pennant.backend.model.insurance.InsurancePaymentInstructions;
import com.pennant.backend.model.systemmasters.VASProviderAccDetail;
import com.pennant.backend.service.customermasters.CustomerDetailsService;
import com.pennant.backend.service.fees.FeeDetailService;
import com.pennant.backend.service.finance.FinAdvancePaymentsService;
import com.pennant.backend.service.finance.FinanceTaxDetailService;
import com.pennant.backend.service.finance.ReceiptService;
import com.pennant.backend.service.finance.impl.FinanceDataValidation;
import com.pennant.backend.service.pdc.ChequeHeaderService;
import com.pennant.backend.util.DisbursementConstants;
import com.pennant.backend.util.FinanceConstants;
import com.pennant.backend.util.PennantApplicationUtil;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantStaticListUtil;
import com.pennant.backend.util.RepayConstants;
import com.pennant.backend.util.UploadConstants;
import com.pennant.pff.core.schd.service.PartCancellationService;
import com.pennant.validation.AddDisbursementGroup;
import com.pennant.validation.AddRateChangeGroup;
import com.pennant.validation.AddTermsGroup;
import com.pennant.validation.ChangeGestationGroup;
import com.pennant.validation.ChangeInstallmentFrequencyGroup;
import com.pennant.validation.ChangeInterestGroup;
import com.pennant.validation.ChangeRepaymentGroup;
import com.pennant.validation.DefermentsGroup;
import com.pennant.validation.EarlySettlementGroup;
import com.pennant.validation.PartCancellationGroup;
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
import com.pennanttech.pennapps.core.InterfaceException;
import com.pennanttech.pennapps.core.model.ErrorDetail;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pennapps.core.util.DateUtil;
import com.pennanttech.pff.core.TableType;
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
	private static final Logger logger = LogManager.getLogger(FinInstructionServiceImpl.class);

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
	private CustomerDetailsService customerDetailsService;
	private BranchDAO branchDAO;
	private FinanceScheduleDetailDAO financeScheduleDetailDAO;
	private ChequeHeaderService chequeHeaderService;
	private ChequeHeaderDAO chequeHeaderDAO;
	private ChequeDetailDAO chequeDetailDAO;
	private FinAdvancePaymentsDAO finAdvancePaymentsDAO;
	private VASRecordingDAO vASRecordingDAO;
	private InsuranceDetailDAO insuranceDetailDAO;
	private VASConfigurationDAO vASConfigurationDAO;
	private VASProviderAccDetailDAO vASProviderAccDetailDAO;
	private PartCancellationService partCancellationService;

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

		// bean validations
		validationUtility.validate(finServiceInstruction, UpfrontFeesGroup.class);

		if (StringUtils.isBlank(finServiceInstruction.getFinReference())
				&& StringUtils.isBlank(finServiceInstruction.getExternalReference())) {
			String[] valueParm = new String[2];
			valueParm[0] = "finReference";
			valueParm[1] = "externalReference";
			return errorDetails("90123", valueParm);
		} else if (StringUtils.isNotBlank(finServiceInstruction.getFinReference())
				&& StringUtils.isNotBlank(finServiceInstruction.getExternalReference())) {
			String[] valueParm = new String[2];
			valueParm[0] = "finReference";
			valueParm[1] = "externalReference";
			return errorDetails("30511", valueParm);
		}

		if (StringUtils.isNotBlank(finServiceInstruction.getFinReference())) {
			finServiceInstruction.setFromBranch("");
			finServiceInstruction.setToBranch("");
			finServiceInstruction.setFinType("");
			finServiceInstruction.setCustCIF("");
		} else {
			// for logging purpose
			APIErrorHandlerService.logReference(finServiceInstruction.getExternalReference());

			if (StringUtils.isBlank(finServiceInstruction.getFromBranch())) {
				String valueParm[] = new String[1];
				valueParm[0] = "fromBranch";
				return errorDetails("90502", valueParm);
			}

			if (StringUtils.isBlank(finServiceInstruction.getFinType())) {
				String valueParm[] = new String[1];
				valueParm[0] = "finType";
				return errorDetails("90502", valueParm);
			}

			if (StringUtils.isBlank(finServiceInstruction.getCustCIF())) {
				String valueParm[] = new String[1];
				valueParm[0] = "cif";
				return errorDetails("90502", valueParm);
			}

			Branch fromBranch = branchDAO.getBranchById(finServiceInstruction.getFromBranch(), "");
			if (fromBranch == null) {
				String[] valueParm = new String[1];
				valueParm[0] = finServiceInstruction.getFromBranch();
				return errorDetails("90129", valueParm);
			}

			Customer customer = customerDetailsService.checkCustomerByCIF(finServiceInstruction.getCustCIF(),
					TableType.MAIN_TAB.getSuffix());
			if (customer == null) {
				String valueParm[] = new String[1];
				valueParm[0] = finServiceInstruction.getCustCIF();
				return errorDetails("90101", valueParm);
			}
			finServiceInstruction.setCustID(customer.getCustID());

			int count = receiptService.geFeeReceiptCountByExtReference(Objects.toString(customer.getCustID(), ""),
					FinanceConstants.FINSER_EVENT_FEEPAYMENT, finServiceInstruction.getExternalReference());
			if (count > 0) {
				String valueParm[] = new String[3];
				valueParm[0] = "Invalid CIF";
				valueParm[1] = finServiceInstruction.getCustCIF();
				valueParm[2] = "externalreference is already assigned to another customer.";
				return errorDetails("30550", valueParm);
			}
		}

		if (finServiceInstruction.getAmount() == null) {
			finServiceInstruction.setAmount(BigDecimal.ZERO);
		}

		String moduleDefiner = FinanceConstants.FINSER_EVENT_FEEPAYMENT;

		// set Default date formats
		setDefaultDateFormats(finServiceInstruction);

		FinanceDetail financeDetail = null;

		//vlidate duplicate record
		boolean dedupFound = checkUpFrontDuplicateRequest(finServiceInstruction, moduleDefiner);
		if (dedupFound) {
			String valueParm[] = new String[1];
			valueParm[0] = "transaction";
			return errorDetails("41014", valueParm);
		}
		// execute manual payment service
		financeDetail = finServiceInstController.doFeePayment(finServiceInstruction);

		logger.debug(Literal.LEAVING);
		return financeDetail;
	}

	private FinanceDetail errorDetails(String errorCode, String parameter[]) {
		FinanceDetail financeDetail;
		financeDetail = new FinanceDetail();
		doEmptyResponseObject(financeDetail);
		financeDetail.setReturnStatus(APIErrorHandlerService.getFailedStatus(errorCode, parameter));
		return financeDetail;
	}

	private boolean checkUpFrontDuplicateRequest(FinServiceInstruction finServiceInstruction, String moduleDefiner) {

		List<FinReceiptDetail> receiptDetails = null;
		if (StringUtils.isNotBlank(finServiceInstruction.getFinReference())) {
			receiptDetails = finReceiptDetailDAO
					.getFinReceiptDetailByReference(finServiceInstruction.getFinReference());
		} else {
			receiptDetails = finReceiptDetailDAO
					.getFinReceiptDetailByReference(Objects.toString(finServiceInstruction.getCustID(), ""));
		}
		String paymentMode = finServiceInstruction.getPaymentMode();
		if (paymentMode.equals(RepayConstants.RECEIPTMODE_RTGS) || paymentMode.equals(RepayConstants.RECEIPTMODE_NEFT)
				|| paymentMode.equals(RepayConstants.RECEIPTMODE_IMPS)
				|| paymentMode.equals(RepayConstants.RECEIPTMODE_ESCROW)) {
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
		List<FinFeeDetail> feeDetailList = financeDetail.getFinScheduleData().getFinFeeDetailList();

		List<FinFeeDetail> newList = new ArrayList<>();
		for (FinFeeDetail fd : feeDetailList) {
			if (!fd.isOriginationFee()) {
				newList.add(fd);
			}
		}
		financeDetail.getFinScheduleData().setFinFeeDetailList(newList);

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
		} catch (ServiceException e) {
			logger.error(Literal.EXCEPTION, e);
			throw new ServiceException(e.getFaultDetails());
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
			fsi.setValueDate(fsi.getReceiptDetail().getReceivedDate());
			fsi.getReceiptDetail().setValueDate(fsi.getReceiptDetail().getReceivedDate());
		}

		fsi.setReceivedDate(fsi.getReceiptDetail().getReceivedDate());
		finScheduleData.setFinServiceInstruction(fsi);
		financeDetail = validateInstructions(financeDetail, moduleDefiner, eventCode);
		fsi.setAmount(fsi.getAmount().add(fsi.getTdsAmount()));
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
				valueParam[1] = "E," + "R";
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

	@Override
	public FinAdvPaymentDetail getDisbursmentDetails(String finReference) throws ServiceException {
		logger.info("Identifying Disb/Vas instructions for the specified FinReference>> {}", finReference);

		List<DisbResponse> disbResponse = new ArrayList<>();

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

			logger.info("The specified FinReference>> {} is not exists or in-active", finReference);
			return response;
		}

		/* Fetching Disbursement instructions */
		disbResponse.addAll(getDisbInstructions(finReference));

		/* Fetching VAS instructions */
		disbResponse.addAll(getVasInstructions(finReference));

		int size = disbResponse.size();

		if (size == 0) {
			String[] valueParam = new String[2];
			valueParam[0] = "There is no pending Disb/Vas instructions to update the status";
			valueParam[1] = "FinReference " + finReference;
			response.setReturnStatus(APIErrorHandlerService.getFailedStatus("21005", valueParam));

			logger.info("There is no pending Disb/Vas instructions for the specified FinReference>> {}", finReference);
			return response;
		}

		response.setDisbResponse(disbResponse);
		response.setReturnStatus(APIErrorHandlerService.getSuccessStatus());

		logger.info("Total {} Disb/Vas instructions found the specified FinReference>> {}", size, finReference);

		return response;
	}

	private List<DisbResponse> getVasInstructions(String finReference) {
		List<DisbResponse> vasInstructions = new ArrayList<>();
		List<VASRecording> vrList = vASRecordingDAO.getVASRecordingsByLinkRef(finReference, "");

		VASProviderAccDetail vpad = null;
		VASConfiguration vc = null;

		for (VASRecording vr : vrList) {
			long paymentInsId = vr.getPaymentInsId();
			InsurancePaymentInstructions ipi = insuranceDetailDAO.getInsurancePaymentInstructionStatus(paymentInsId);

			if (ipi == null) {
				continue;
			}

			vc = vASConfigurationDAO.getVASConfigurationByCode(vr.getProductCode(), "");

			if (vc != null) {
				long manufacturerId = vc.getManufacturerId();
				String entityCode = vr.getEntityCode();
				vpad = vASProviderAccDetailDAO.getVASProviderAccDetByPRoviderId(manufacturerId, entityCode, "_view");
			}

			DisbResponse detail = new DisbResponse();
			detail.setPaymentId(paymentInsId);

			if (vpad != null) {
				detail.setAccountNo(vpad.getAccountNumber());
			}

			detail.setDisbAmount(vr.getFee());
			// detail.setDisbDate(fap.getLlDate());
			detail.setStatus(ipi.getStatus());
			detail.setType(DisbursementConstants.CHANNEL_INSURANCE);
			vasInstructions.add(detail);
		}

		return vasInstructions;
	}

	private List<DisbResponse> getDisbInstructions(String finReference) {
		List<DisbResponse> disbInstructions = new ArrayList<>();
		List<FinAdvancePayments> fapList = finAdvancePaymentsService.getFinAdvancePaymentsById(finReference, " ");
		for (FinAdvancePayments fap : fapList) {
			DisbResponse detail = new DisbResponse();
			detail.setPaymentId(fap.getPaymentId());
			detail.setAccountNo(fap.getBeneficiaryAccNo());
			detail.setDisbAmount(fap.getAmtToBeReleased());
			detail.setDisbDate(fap.getLlDate());
			detail.setStatus(fap.getStatus());
			detail.setType(DisbursementConstants.CHANNEL_DISBURSEMENT);
			disbInstructions.add(detail);
		}
		return disbInstructions;
	}

	@Override
	public WSReturnStatus updateCovenants(FinanceDetail financeDetail) throws ServiceException {
		logger.debug(Literal.ENTERING);
		WSReturnStatus returnStatus = new WSReturnStatus();
		List<ErrorDetail> errorDetails;
		try {

			FinanceMain financeMain = null;
			String finReference = financeDetail.getFinReference();
			List<Covenant> covenantsList = financeDetail.getCovenants();

			//validating the covenants if same covenant type and same category name

			if (finReference == null) {
				String[] valueParm = new String[1];
				valueParm[0] = "FinReference";
				return returnStatus = APIErrorHandlerService.getFailedStatus("90502", valueParm);
			} else {
				financeMain = financeMainDAO.getFinanceMain(finReference, new String[] { "FinIsActive", "FinStartDate",
						"MaturityDate, WorkFlowId, CustId, FinReference" }, "");
				if (financeMain == null || !financeMain.isFinIsActive()) {
					String[] valueParm = new String[1];
					valueParm[0] = finReference;
					return returnStatus = APIErrorHandlerService.getFailedStatus("90201", valueParm);
				}
			}

			financeMain.setUserDetails(SessionUserDetails.getUserDetails(SessionUserDetails.getLogiedInUser()));

			if (covenantsList == null) {
				String[] valueParm = new String[1];
				valueParm[0] = "CovenatDetails ";
				return returnStatus = APIErrorHandlerService.getFailedStatus("90502", valueParm);
			}

			// for logging purpose
			APIErrorHandlerService.logReference(finReference);

			errorDetails = financeDataValidation.covenantValidation(financeMain, covenantsList, null);

			for (ErrorDetail errorDetail : errorDetails) {
				returnStatus = APIErrorHandlerService.getFailedStatus(errorDetail.getCode(),
						errorDetail.getParameters());
				return returnStatus;
			}

			returnStatus = finServiceInstController.processCovenants(financeMain, covenantsList);
		} catch (Exception e) {
			logger.error(Literal.EXCEPTION + e);
			return APIErrorHandlerService.getFailedStatus();
		}
		logger.debug(Literal.LEAVING);
		return returnStatus;
	}

	@Override
	public WSReturnStatus saveChequeDetails(FinanceDetail financeDetail) throws ServiceException {
		logger.debug(Literal.ENTERING);
		WSReturnStatus returnStatus = new WSReturnStatus();
		List<ErrorDetail> errorDetails;
		try {
			FinanceMain financeMain = null;
			String finReference = financeDetail.getFinReference();
			ChequeHeader chequeHeader = financeDetail.getChequeHeader();

			if (finReference == null) {
				String[] valueParm = new String[1];
				valueParm[0] = "FinReference";
				return returnStatus = APIErrorHandlerService.getFailedStatus("90502", valueParm);
			} else {
				financeMain = financeMainDAO.getFinanceMainById(finReference, "", false);
				if (financeMain == null || !financeMain.isFinIsActive()) {
					String[] valueParm = new String[1];
					valueParm[0] = finReference;
					return returnStatus = APIErrorHandlerService.getFailedStatus("90201", valueParm);
				} else {
					financeDetail.getFinScheduleData().setFinanceMain(financeMain);
				}
			}

			if (chequeHeader == null) {
				String[] valueParm = new String[1];
				valueParm[0] = "Cheque Details ";
				return returnStatus = APIErrorHandlerService.getFailedStatus("90502", valueParm);
			}

			// for logging purpose
			APIErrorHandlerService.logReference(finReference);
			errorDetails = chequeHeaderService.chequeValidationForUpdate(financeDetail, PennantConstants.method_save,
					"");
			for (ErrorDetail errorDetail : errorDetails) {
				returnStatus = APIErrorHandlerService.getFailedStatus(errorDetail.getCode(),
						errorDetail.getParameters());
				return returnStatus;
			}

			List<FinanceScheduleDetail> finScheduleDetails = financeScheduleDetailDAO
					.getFinScheduleDetails(financeMain.getFinReference(), "", false);
			financeDetail.getFinScheduleData().setFinanceScheduleDetails(finScheduleDetails);
			FinScheduleData finSchdData = validateChequeDetails(financeDetail);
			if (CollectionUtils.isNotEmpty(finSchdData.getErrorDetails())) {
				for (ErrorDetail errorDetail : finSchdData.getErrorDetails()) {
					return APIErrorHandlerService.getFailedStatus(errorDetail.getCode(), errorDetail.getError());
				}
			}
			returnStatus = finServiceInstController.processChequeDetail(financeDetail, "");

		} catch (Exception e) {
			logger.error(Literal.EXCEPTION + e);
			return APIErrorHandlerService.getFailedStatus();
		}
		logger.debug(Literal.LEAVING);
		return returnStatus;
	}

	//cheque Validations for schedule
	private FinScheduleData validateChequeDetails(FinanceDetail financeDetail) {
		boolean date = true;
		boolean amount = true;
		FinScheduleData finScheduleData = financeDetail.getFinScheduleData();
		ChequeHeader chequeHeader = financeDetail.getChequeHeader();
		List<ChequeDetail> chequeDetailsList = chequeHeader.getChequeDetailList();
		for (ChequeDetail chequeDetail : chequeDetailsList) {
			//schedules validation
			if (StringUtils.equals(FinanceConstants.REPAYMTH_PDC, chequeDetail.getChequeType())) {
				List<FinanceScheduleDetail> schedules = financeDetail.getFinScheduleData().getFinanceScheduleDetails();
				for (FinanceScheduleDetail fsd : schedules) {
					if (DateUtil.compare(fsd.getSchDate(), chequeDetail.getChequeDate()) == 0) {
						date = true;
						chequeDetail.seteMIRefNo(fsd.getInstNumber());
						if (fsd.getRepayAmount().compareTo(chequeDetail.getAmount()) != 0) {
							amount = false;
							//{0} Should be equal To {1}
							String[] valueParm = new String[2];
							valueParm[0] = new SimpleDateFormat("yyyy-MM-dd").format(fsd.getSchDate());
							valueParm[1] = String.valueOf(fsd.getRepayAmount() + "INR");
							finScheduleData
									.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetail("30570", valueParm)));
							return finScheduleData;

						} else {
							break;
						}

					} else {
						date = false;
					}
				}
				if (date == false) {
					String[] valueParm = new String[2];
					valueParm[0] = "Cheque Date";
					valueParm[1] = "ScheduleDates";
					finScheduleData.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetail("30570", valueParm)));
					return finScheduleData;

				}
			}

		}

		return finScheduleData;
	}

	@Override
	public WSReturnStatus createChequeDetails(FinanceDetail financeDetail) throws ServiceException {
		logger.debug(Literal.ENTERING);
		WSReturnStatus returnStatus = new WSReturnStatus();
		List<ErrorDetail> errorDetails;
		String tableType = "_Temp";
		try {
			FinanceMain financeMain = null;
			String finReference = financeDetail.getFinReference();
			ChequeHeader chequeHeader = financeDetail.getChequeHeader();

			if (finReference == null) {
				String[] valueParm = new String[1];
				valueParm[0] = "FinReference";
				return returnStatus = APIErrorHandlerService.getFailedStatus("90502", valueParm);
			} else {
				financeMain = financeMainDAO.getFinanceMainById(finReference, tableType, false);
				if (financeMain == null || !financeMain.isFinIsActive()) {
					String[] valueParm = new String[1];
					valueParm[0] = finReference;
					return returnStatus = APIErrorHandlerService.getFailedStatus("90201", valueParm);
				} else {
					financeDetail.getFinScheduleData().setFinanceMain(financeMain);
				}
			}

			if (chequeHeader == null) {
				String[] valueParm = new String[1];
				valueParm[0] = "Cheque Details ";
				return returnStatus = APIErrorHandlerService.getFailedStatus("90502", valueParm);
			}

			// for logging purpose
			APIErrorHandlerService.logReference(finReference);
			errorDetails = chequeHeaderService.chequeValidation(financeDetail, PennantConstants.method_save, tableType);
			for (ErrorDetail errorDetail : errorDetails) {
				returnStatus = APIErrorHandlerService.getFailedStatus(errorDetail.getCode(),
						errorDetail.getParameters());
				return returnStatus;
			}

			List<FinanceScheduleDetail> finScheduleDetails = financeScheduleDetailDAO
					.getFinScheduleDetails(financeMain.getFinReference(), tableType, false);
			financeDetail.getFinScheduleData().setFinanceScheduleDetails(finScheduleDetails);
			FinScheduleData finSchdData = validateChequeDetails(financeDetail);
			if (CollectionUtils.isNotEmpty(finSchdData.getErrorDetails())) {
				for (ErrorDetail errorDetail : finSchdData.getErrorDetails()) {
					return APIErrorHandlerService.getFailedStatus(errorDetail.getCode(), errorDetail.getError());
				}
			}
			returnStatus = finServiceInstController.processChequeDetail(financeDetail, tableType);

		} catch (Exception e) {
			logger.error(Literal.EXCEPTION + e);
			return APIErrorHandlerService.getFailedStatus();
		}
		logger.debug(Literal.LEAVING);
		return returnStatus;
	}

	@Override
	public WSReturnStatus updateChequeDetailsInMaintainence(FinanceDetail financeDetail) throws ServiceException {
		logger.debug(Literal.ENTERING);
		WSReturnStatus returnStatus = new WSReturnStatus();
		List<ErrorDetail> errorDetails;
		try {
			FinanceMain financeMain = null;
			String finReference = financeDetail.getFinReference();
			ChequeHeader chequeHeader = financeDetail.getChequeHeader();

			// for logging purpose
			APIErrorHandlerService.logReference(finReference);
			errorDetails = chequeHeaderService.chequeValidationInMaintainence(financeDetail,
					PennantConstants.method_Update, "");
			for (ErrorDetail errorDetail : errorDetails) {
				returnStatus = APIErrorHandlerService.getFailedStatus(errorDetail.getCode(),
						errorDetail.getParameters());
				return returnStatus;
			}

			if (chequeHeader.getChequeDetailList() == null) {
				String[] valueParm = new String[1];
				valueParm[0] = "Cheque Details ";
				return returnStatus = APIErrorHandlerService.getFailedStatus("90502", valueParm);
			}
			List<ChequeDetail> chequeDetails = chequeHeader.getChequeDetailList();
			for (ChequeDetail chequeDetail : chequeDetails) {
				if (chequeDetail.getChequeDetailsID() == 0) {
					String[] valueParm = new String[1];
					valueParm[0] = "ChequeDetails Id ";
					return returnStatus = APIErrorHandlerService.getFailedStatus("90502", valueParm);
				}

			}

			List<FinanceScheduleDetail> finScheduleDetails = financeScheduleDetailDAO
					.getFinScheduleDetails(finReference, "", false);
			financeDetail.getFinScheduleData().setFinanceScheduleDetails(finScheduleDetails);
			financeDetail.getFinScheduleData()
					.setFinanceMain(financeMainDAO.getFinanceMainById(finReference, "", false));

			FinScheduleData finSchdData = validateChequeDetails(financeDetail);
			if (CollectionUtils.isNotEmpty(finSchdData.getErrorDetails())) {
				for (ErrorDetail errorDetail : finSchdData.getErrorDetails()) {
					return APIErrorHandlerService.getFailedStatus(errorDetail.getCode(), errorDetail.getError());
				}
			}
			returnStatus = finServiceInstController.updateChequeDetailsinMaintainence(financeDetail, "");

		} catch (Exception e) {
			logger.error(Literal.EXCEPTION + e);
			return APIErrorHandlerService.getFailedStatus();
		}

		return returnStatus;
	}

	@Override
	public WSReturnStatus updateChequeDetails(FinanceDetail financeDetail) throws ServiceException {
		logger.debug(Literal.ENTERING);
		WSReturnStatus returnStatus = new WSReturnStatus();
		List<ErrorDetail> errorDetails;
		String tableType = "_Temp";
		try {
			FinanceMain financeMain = null;
			String finReference = financeDetail.getFinReference();
			ChequeHeader chequeHeader = financeDetail.getChequeHeader();

			// for logging purpose
			APIErrorHandlerService.logReference(finReference);
			errorDetails = chequeHeaderService.chequeValidationForUpdate(financeDetail, PennantConstants.method_Update,
					tableType);
			for (ErrorDetail errorDetail : errorDetails) {
				returnStatus = APIErrorHandlerService.getFailedStatus(errorDetail.getCode(),
						errorDetail.getParameters());
				return returnStatus;
			}

			if (chequeHeader.getChequeDetailList() == null) {
				String[] valueParm = new String[1];
				valueParm[0] = "Cheque Details ";
				return returnStatus = APIErrorHandlerService.getFailedStatus("90502", valueParm);
			}

			List<FinanceScheduleDetail> finScheduleDetails = financeScheduleDetailDAO
					.getFinScheduleDetails(finReference, tableType, false);
			financeDetail.getFinScheduleData().setFinanceScheduleDetails(finScheduleDetails);
			financeDetail.getFinScheduleData()
					.setFinanceMain(financeMainDAO.getFinanceMainById(finReference, tableType, false));
			FinScheduleData finSchdData = validateChequeDetails(financeDetail);
			if (CollectionUtils.isNotEmpty(finSchdData.getErrorDetails())) {
				for (ErrorDetail errorDetail : finSchdData.getErrorDetails()) {
					return APIErrorHandlerService.getFailedStatus(errorDetail.getCode(), errorDetail.getError());
				}
			}
			returnStatus = finServiceInstController.updateCheque(financeDetail, tableType);

		} catch (Exception e) {
			logger.error(Literal.EXCEPTION + e);
			return APIErrorHandlerService.getFailedStatus();
		}

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

	@Override
	public ChequeHeader getChequeDetails(String finReference) throws ServiceException {
		logger.debug(Literal.ENTERING);

		ChequeHeader response = new ChequeHeader();
		// Mandatory validation
		if (StringUtils.isBlank(finReference)) {
			validationUtility.fieldLevelException();
		}

		// for logging purpose
		APIErrorHandlerService.logReference(finReference);

		if (StringUtils.isBlank(finReference)) {
			String[] valueParm = new String[1];
			valueParm[0] = "finReference";
			response.setReturnStatus(APIErrorHandlerService.getFailedStatus("90502", valueParm));
			return response;
		} else {
			int count = financeMainDAO.getFinanceCountById(finReference, "_View", false);
			if (count <= 0) {
				String[] valueParm = new String[1];
				valueParm[0] = finReference;
				response.setReturnStatus(APIErrorHandlerService.getFailedStatus("90201", valueParm));
				return response;
			}
		}
		try {
			response = chequeHeaderDAO.getChequeHeaderByRef(finReference, "_View");
			if (response == null) {
				String[] valueParm = new String[1];
				valueParm[0] = "No Cheque Details";
				response.setReturnStatus(APIErrorHandlerService.getFailedStatus("90201", valueParm));
				return response;
			}

			if (response != null) {
				List<ChequeDetail> chequeDetailList = chequeDetailDAO.getChequeDetailList(response.getHeaderID(),
						"_View");
				response.setChequeDetailList(chequeDetailList);
			}

		} catch (Exception e) {
			logger.error(Literal.EXCEPTION + e);
			response.setReturnStatus(APIErrorHandlerService.getFailedStatus());
			return response;
		}
		return response;
	}

	@Override
	public WSReturnStatus cancelDisbursementInstructions(FinServiceInstruction finServiceInstRequest)
			throws ServiceException {
		logger.debug(Literal.ENTERING);

		WSReturnStatus response = new WSReturnStatus();

		// for logging purpose
		String finReference = finServiceInstRequest.getFinReference();
		APIErrorHandlerService.logReference(finReference);

		// set Default date formats
		setDefaultDateFormats(finServiceInstRequest);

		int countRef = financeMainDAO.getFinanceCountById(finReference, "", false);
		if (countRef < 0) {
			String[] valueParm = new String[1];
			valueParm[0] = finReference;
			return APIErrorHandlerService.getFailedStatus("90248", valueParm);
		}

		int count = validateBlockedFinances(finReference);
		if (count > 0) {
			String valueParm[] = new String[2];
			valueParm[0] = "Disbursement";
			valueParm[1] = "FinReference: " + finReference;
			return APIErrorHandlerService.getFailedStatus("90204", valueParm);
		}

		long paymentId = finServiceInstRequest.getPaymentId();
		if (paymentId == Long.MIN_VALUE && paymentId <= 0) {
			String valueParm[] = new String[1];
			valueParm[0] = "PaymentId";
			return APIErrorHandlerService.getFailedStatus("90502", valueParm);
		}

		int paymentIdCount = finAdvancePaymentsService.getCountByPaymentId(finReference, paymentId);
		if (paymentIdCount <= 0) {
			String[] valueParam = new String[1];
			valueParam[0] = "PaymentId";
			return APIErrorHandlerService.getFailedStatus("90405", valueParam);
		}

		FinAdvancePayments finAdvancePayments = new FinAdvancePayments();
		finAdvancePayments.setPaymentId(paymentId);
		FinAdvancePayments finAdv = finAdvancePaymentsDAO.getFinAdvancePaymentsById(finAdvancePayments, "");

		if (DisbursementConstants.STATUS_PAID.equals(finAdv.getStatus())) {
			List<FinAdvancePayments> disbursementDetailsList = finServiceInstRequest.getDisbursementDetails();
			if (CollectionUtils.isEmpty(disbursementDetailsList)) {
				String valueParm[] = new String[1];
				valueParm[0] = "DisbursementDetails";
				return APIErrorHandlerService.getFailedStatus("90502", valueParm);
			}

			String eventCode = AccountEventConstants.ACCEVENT_ADDDBSN;
			FinanceDetail financeDetail = finServiceInstController.getFinanceDetails(finServiceInstRequest, eventCode);
			financeDetail.setAdvancePaymentsList(disbursementDetailsList);
			AuditDetail auditDetail = addDisbursementService.doCancelDisbValidations(financeDetail);

			if (CollectionUtils.isNotEmpty(auditDetail.getErrorDetails())) {
				for (ErrorDetail errorDetail : auditDetail.getErrorDetails()) {
					return APIErrorHandlerService.getFailedStatus(errorDetail.getCode(), errorDetail.getError());
				}
			}

			financeDetail.getAdvancePaymentsList().add(finAdv);
			response = finServiceInstController.doCancelDisbursementInstructions(financeDetail);
		} else {
			String[] valueParam = new String[2];
			valueParam[0] = "Cancel Disbursement Instructions";
			valueParam[1] = finAdv.getStatus();
			return APIErrorHandlerService.getFailedStatus("90329", valueParam);
		}

		logger.debug(Literal.LEAVING);
		return response;
	}

	private void buildFinFeeForUpload(FinServiceInstruction finSrvcInst) {
		for (FinFeeDetail feeDtl : finSrvcInst.getFinFeeDetails()) {
			feeDtl.setFeeScheduleMethod("");
		}
	}

	@Override
	public FinanceDetail partCancellation(FinServiceInstruction finServiceInstruction) {
		logger.debug(Literal.ENTERING);
		FinanceDetail financeDetail = null;

		try {

			validationUtility.validate(finServiceInstruction, PartCancellationGroup.class);

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

			String eventCode = AccountEventConstants.PART_CANCELATION;
			financeDetail = finServiceInstController.getFinanceDetails(finServiceInstruction, eventCode);

			// validate service instruction data
			AuditDetail auditDetail = partCancellationService.validateRequest(finServiceInstruction, financeDetail);
			if (auditDetail.getErrorDetails() != null) {
				for (ErrorDetail errorDetail : auditDetail.getErrorDetails()) {
					financeDetail = new FinanceDetail();
					doEmptyResponseObject(financeDetail);
					financeDetail.setReturnStatus(
							APIErrorHandlerService.getFailedStatus(errorDetail.getCode(), errorDetail.getError()));
					return financeDetail;
				}
			}

			// call part cancellation service
			financeDetail = partCancellationService.doPartCancellation(finServiceInstruction, financeDetail);

			if (financeDetail.getFinScheduleData().getErrorDetails() != null) {
				for (ErrorDetail errorDetail : financeDetail.getFinScheduleData().getErrorDetails()) {
					FinanceDetail response = new FinanceDetail();
					doEmptyResponseObject(response);
					response.setReturnStatus(
							APIErrorHandlerService.getFailedStatus(errorDetail.getCode(), errorDetail.getError()));
					return response;
				}
			}

			// Get the response
			financeDetail = finServiceInstController.getResponse(financeDetail, finServiceInstruction);

		} catch (InterfaceException ex) {
			logger.error("InterfaceException", ex);
			FinanceDetail response = new FinanceDetail();
			doEmptyResponseObject(response);
			response.setReturnStatus(APIErrorHandlerService.getFailedStatus("9998", ex.getMessage()));
			return response;
		} catch (Exception e) {
			logger.error("Exception", e);
			APIErrorHandlerService.logUnhandledException(e);
			FinanceDetail response = new FinanceDetail();
			doEmptyResponseObject(response);
			response.setReturnStatus(APIErrorHandlerService.getFailedStatus());
			return response;
		}

		logger.debug(Literal.LEAVING);
		return financeDetail;
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

	@Autowired
	public void setCustomerDetailsService(CustomerDetailsService customerDetailsService) {
		this.customerDetailsService = customerDetailsService;
	}

	@Autowired
	public void setBranchDAO(BranchDAO branchDAO) {
		this.branchDAO = branchDAO;
	}

	@Autowired
	public void setFinanceScheduleDetailDAO(FinanceScheduleDetailDAO financeScheduleDetailDAO) {
		this.financeScheduleDetailDAO = financeScheduleDetailDAO;
	}

	@Autowired
	public void setChequeHeaderService(ChequeHeaderService chequeHeaderService) {
		this.chequeHeaderService = chequeHeaderService;
	}

	@Autowired
	public void setChequeHeaderDAO(ChequeHeaderDAO chequeHeaderDAO) {
		this.chequeHeaderDAO = chequeHeaderDAO;
	}

	@Autowired
	public void setChequeDetailDAO(ChequeDetailDAO chequeDetailDAO) {
		this.chequeDetailDAO = chequeDetailDAO;
	}

	@Autowired
	public void setFinAdvancePaymentsDAO(FinAdvancePaymentsDAO finAdvancePaymentsDAO) {
		this.finAdvancePaymentsDAO = finAdvancePaymentsDAO;
	}

	@Autowired
	public void setvASRecordingDAO(VASRecordingDAO vASRecordingDAO) {
		this.vASRecordingDAO = vASRecordingDAO;
	}

	@Autowired
	public void setInsuranceDetailDAOImpl(InsuranceDetailDAO insuranceDetailDAO) {
		this.insuranceDetailDAO = insuranceDetailDAO;
	}

	@Autowired
	public void setInsuranceDetailDAO(InsuranceDetailDAO insuranceDetailDAO) {
		this.insuranceDetailDAO = insuranceDetailDAO;
	}

	@Autowired
	public void setvASConfigurationDAO(VASConfigurationDAO vASConfigurationDAO) {
		this.vASConfigurationDAO = vASConfigurationDAO;
	}

	@Autowired
	public void setvASProviderAccDetailDAO(VASProviderAccDetailDAO vASProviderAccDetailDAO) {
		this.vASProviderAccDetailDAO = vASProviderAccDetailDAO;
	}

	@Autowired
	public void setPartCancellationService(PartCancellationService partCancellationService) {
		this.partCancellationService = partCancellationService;
	}

}
