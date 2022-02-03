package com.pennanttech.service.impl;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jaxen.JaxenException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.pennant.app.constants.CalculationConstants;
import com.pennant.app.util.DateUtility;
import com.pennant.app.util.ErrorUtil;
import com.pennant.app.util.PathUtil;
import com.pennant.app.util.ReceiptCalculator;
import com.pennant.app.util.SessionUserDetails;
import com.pennant.app.util.SysParamUtil;
import com.pennant.backend.dao.administration.SecurityUserDAO;
import com.pennant.backend.dao.applicationmaster.BranchDAO;
import com.pennant.backend.dao.applicationmaster.EntityDAO;
import com.pennant.backend.dao.configuration.VASConfigurationDAO;
import com.pennant.backend.dao.configuration.VASRecordingDAO;
import com.pennant.backend.dao.finance.FinAdvancePaymentsDAO;
import com.pennant.backend.dao.finance.FinanceMainDAO;
import com.pennant.backend.dao.finance.FinanceScheduleDetailDAO;
import com.pennant.backend.dao.finance.FinanceWriteoffDAO;
import com.pennant.backend.dao.finance.covenant.CovenantsDAO;
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
import com.pennant.backend.financeservice.RestructureService;
import com.pennant.backend.model.ValueLabel;
import com.pennant.backend.model.WSReturnStatus;
import com.pennant.backend.model.agreement.CovenantAggrement;
import com.pennant.backend.model.applicationmaster.Branch;
import com.pennant.backend.model.applicationmaster.LoanPendingData;
import com.pennant.backend.model.applicationmaster.LoanPendingDetails;
import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.configuration.VASConfiguration;
import com.pennant.backend.model.configuration.VASRecording;
import com.pennant.backend.model.customermasters.Customer;
import com.pennant.backend.model.finance.ChequeDetail;
import com.pennant.backend.model.finance.ChequeHeader;
import com.pennant.backend.model.finance.FeeWaiverDetail;
import com.pennant.backend.model.finance.FeeWaiverHeader;
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
import com.pennant.backend.model.finance.RestructureDetail;
import com.pennant.backend.model.finance.covenant.Covenant;
import com.pennant.backend.model.finance.covenant.CovenantDocument;
import com.pennant.backend.model.finance.financetaxdetail.FinanceTaxDetail;
import com.pennant.backend.model.systemmasters.VASProviderAccDetail;
import com.pennant.backend.service.customermasters.CustomerDetailsService;
import com.pennant.backend.service.fees.FeeDetailService;
import com.pennant.backend.service.finance.FeeWaiverHeaderService;
import com.pennant.backend.service.finance.FinAdvancePaymentsService;
import com.pennant.backend.service.finance.FinanceDetailService;
import com.pennant.backend.service.finance.FinanceTaxDetailService;
import com.pennant.backend.service.finance.NonLanReceiptService;
import com.pennant.backend.service.finance.ReceiptService;
import com.pennant.backend.service.finance.impl.FinanceDataValidation;
import com.pennant.backend.service.pdc.ChequeHeaderService;
import com.pennant.backend.service.systemmasters.InterestCertificateService;
import com.pennant.backend.util.DisbursementConstants;
import com.pennant.backend.util.FinanceConstants;
import com.pennant.backend.util.PennantApplicationUtil;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantJavaUtil;
import com.pennant.backend.util.PennantStaticListUtil;
import com.pennant.backend.util.RepayConstants;
import com.pennant.backend.util.UploadConstants;
import com.pennant.pff.core.schd.service.PartCancellationService;
import com.pennant.pff.dao.subvention.SubventionUploadDAO;
import com.pennant.pff.model.subvention.Subvention;
import com.pennant.pff.model.subvention.SubventionHeader;
import com.pennant.pff.service.subvention.SubventionKnockOffService;
import com.pennant.util.AgreementGeneration;
import com.pennant.validation.AddDisbursementGroup;
import com.pennant.validation.AddRateChangeGroup;
import com.pennant.validation.AddTermsGroup;
import com.pennant.validation.ChangeGestationGroup;
import com.pennant.validation.ChangeInstallmentFrequencyGroup;
import com.pennant.validation.ChangeInterestGroup;
import com.pennant.validation.ChangeRepaymentGroup;
import com.pennant.validation.DefermentsGroup;
import com.pennant.validation.EarlySettlementGroup;
import com.pennant.validation.NonLanReceiptGroup;
import com.pennant.validation.PartCancellationGroup;
import com.pennant.validation.PartialSettlementGroup;
import com.pennant.validation.ReSchedulingGroup;
import com.pennant.validation.RecalculateGroup;
import com.pennant.validation.RemoveTermsGroup;
import com.pennant.validation.ScheduleMethodGroup;
import com.pennant.validation.SchedulePaymentGroup;
import com.pennant.validation.UpdateLoanBasicDetailsGroup;
import com.pennant.validation.UpdateLoanPenaltyDetailGroup;
import com.pennant.validation.UpfrontFeesGroup;
import com.pennant.validation.ValidationUtility;
import com.pennant.ws.exception.ServiceException;
import com.pennanttech.controller.CreateFinanceController;
import com.pennanttech.controller.ExtendedTestClass;
import com.pennanttech.controller.FinServiceInstController;
import com.pennanttech.pennapps.core.AppException;
import com.pennanttech.pennapps.core.InterfaceException;
import com.pennanttech.pennapps.core.model.ErrorDetail;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pennapps.core.util.DateUtil;
import com.pennanttech.pennapps.core.util.DateUtil.DateFormat;
import com.pennanttech.pff.constants.AccountingEvent;
import com.pennanttech.pff.constants.FinServiceEvent;
import com.pennanttech.pff.core.TableType;
import com.pennanttech.pffws.FinServiceInstRESTService;
import com.pennanttech.pffws.FinServiceInstSOAPService;
import com.pennanttech.util.APIConstants;
import com.pennanttech.ws.model.covenantStatus.CovenantStatus;
import com.pennanttech.ws.model.customer.AgreementRequest;
import com.pennanttech.ws.model.eligibility.AgreementData;
import com.pennanttech.ws.model.finance.DisbRequest;
import com.pennanttech.ws.model.finance.DisbResponse;
import com.pennanttech.ws.model.finance.FinAdvPaymentDetail;
import com.pennanttech.ws.service.APIErrorHandlerService;
import com.pennanttech.ws.service.FinanceValidationService;

@Service
public class FinInstructionServiceImpl extends ExtendedTestClass
		implements FinServiceInstRESTService, FinServiceInstSOAPService {
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
	private VASConfigurationDAO vASConfigurationDAO;
	private VASProviderAccDetailDAO vASProviderAccDetailDAO;
	private NonLanReceiptService nonLanReceiptService;
	private PartCancellationService partCancellationService;
	private FinanceWriteoffDAO financeWriteoffDAO;
	private SubventionKnockOffService subventionKnockOffService;
	private SubventionUploadDAO subventionUploadDAO;
	private EntityDAO entityDAO;
	private CovenantsDAO covenantsDAO;
	private InterestCertificateService interestCertificateService;
	private AgreementGeneration agreementGeneration;
	private FinanceDetailService financeDetailService;
	private FeeWaiverHeaderService feeWaiverHeaderService;
	private RestructureService restructureService;

	/**
	 * Method for perform addRateChange operation
	 * 
	 * @param fsi
	 */
	@Override
	public FinanceDetail addRateChange(FinServiceInstruction fsi) {
		logger.debug(Literal.ENTERING);

		FinanceDetail fd = null;

		String eventCode = AccountingEvent.RATCHG;

		validationUtility.validate(fsi, AddRateChangeGroup.class);

		fd = validateInstruction(fsi, eventCode);

		if (fd != null) {
			return fd;
		}

		String reqFrom = fsi.getReqFrom();

		AuditDetail auditDetail = rateChangeService.doValidations(fsi);

		if (StringUtils.equals(UploadConstants.FRR, reqFrom)) {
			fsi = (FinServiceInstruction) auditDetail.getModelData();
		}

		if (auditDetail.getErrorDetails() != null) {
			for (ErrorDetail ed : auditDetail.getErrorDetails()) {
				fd = new FinanceDetail();
				doEmptyResponseObject(fd);
				fd.setReturnStatus(APIErrorHandlerService.getFailedStatus(ed.getCode(), ed.getError()));

				return fd;
			}
		}

		List<ErrorDetail> errors = doProcessServiceFees(fsi, eventCode);
		if (!errors.isEmpty()) {
			for (ErrorDetail ed : errors) {
				fd = new FinanceDetail();
				doEmptyResponseObject(fd);
				fd.setReturnStatus(APIErrorHandlerService.getFailedStatus(ed.getCode(), ed.getError()));
				return fd;
			}
		}

		fd = finServiceInstController.doAddRateChange(fsi, eventCode);

		logger.debug(Literal.LEAVING);
		return fd;
	}

	/**
	 * Method for perform changeRepaymentAmount operation
	 * 
	 * @param loanServicing
	 */
	@Override
	public FinanceDetail changeRepayAmt(FinServiceInstruction fsi) throws ServiceException {
		logger.debug(Literal.ENTERING);

		FinanceDetail fd = null;

		String eventCode = AccountingEvent.SCDCHG;

		validationUtility.validate(fsi, ChangeRepaymentGroup.class);

		fd = validateInstruction(fsi, eventCode);

		if (fd != null) {
			return fd;
		}

		AuditDetail auditDetail = addRepaymentService.doValidations(fsi);
		if (auditDetail.getErrorDetails() != null) {
			for (ErrorDetail ed : auditDetail.getErrorDetails()) {
				fd = new FinanceDetail();
				doEmptyResponseObject(fd);
				fd.setReturnStatus(APIErrorHandlerService.getFailedStatus(ed.getCode(), ed.getError()));
				return fd;
			}
		}

		List<ErrorDetail> errors = doProcessServiceFees(fsi, eventCode);
		if (!errors.isEmpty()) {
			for (ErrorDetail errorDetails : errors) {
				fd = new FinanceDetail();
				doEmptyResponseObject(fd);
				fd.setReturnStatus(
						APIErrorHandlerService.getFailedStatus(errorDetails.getCode(), errorDetails.getError()));
				return fd;
			}
		}

		fd = finServiceInstController.doAddRepayment(fsi, eventCode);

		logger.debug(Literal.LEAVING);
		return fd;
	}

	/**
	 * Method for perform deferments operation
	 * 
	 * @param loanServicing
	 * @throws JaxenException
	 */
	@Override
	public FinanceDetail deferments(FinServiceInstruction fsi) throws ServiceException {
		logger.debug(Literal.ENTERING);

		FinanceDetail fd = null;

		String eventCode = AccountingEvent.DEFRPY;

		validationUtility.validate(fsi, DefermentsGroup.class);

		fd = validateInstruction(fsi, eventCode);

		if (fd != null) {
			return fd;
		}

		AuditDetail auditDetail = postponementService.doValidations(fsi);
		if (auditDetail.getErrorDetails() != null) {
			for (ErrorDetail ed : auditDetail.getErrorDetails()) {
				fd = new FinanceDetail();
				doEmptyResponseObject(fd);
				fd.setReturnStatus(APIErrorHandlerService.getFailedStatus(ed.getCode(), ed.getError()));
				return fd;
			}
		}

		List<ErrorDetail> errors = doProcessServiceFees(fsi, eventCode);
		if (!errors.isEmpty()) {
			for (ErrorDetail ed : errors) {
				fd = new FinanceDetail();
				doEmptyResponseObject(fd);
				fd.setReturnStatus(APIErrorHandlerService.getFailedStatus(ed.getCode(), ed.getError()));
				return fd;
			}
		}

		fd = finServiceInstController.doDefferment(fsi, eventCode);

		logger.debug(Literal.LEAVING);
		return fd;
	}

	/**
	 * Method for perform addTerms operation
	 * 
	 * @param loanServicing
	 * @throws JaxenException
	 */
	@Override
	public FinanceDetail addTerms(FinServiceInstruction fsi) {
		logger.debug(Literal.ENTERING);

		FinanceDetail fd = null;

		String eventCode = AccountingEvent.SCDCHG;

		validationUtility.validate(fsi, AddTermsGroup.class);

		fd = validateInstruction(fsi, eventCode);

		if (fd != null) {
			return fd;
		}

		AuditDetail auditDetail = addTermsService.doValidations(fsi);
		if (auditDetail.getErrorDetails() != null) {
			for (ErrorDetail ed : auditDetail.getErrorDetails()) {
				fd = new FinanceDetail();
				doEmptyResponseObject(fd);
				fd.setReturnStatus(APIErrorHandlerService.getFailedStatus(ed.getCode(), ed.getError()));
				return fd;
			}
		}

		List<ErrorDetail> errors = doProcessServiceFees(fsi, eventCode);
		if (!errors.isEmpty()) {
			for (ErrorDetail ed : errors) {
				fd = new FinanceDetail();
				doEmptyResponseObject(fd);
				fd.setReturnStatus(APIErrorHandlerService.getFailedStatus(ed.getCode(), ed.getError()));
				return fd;
			}
		}

		fd = finServiceInstController.addTerms(fsi, eventCode);

		logger.debug(Literal.LEAVING);
		return fd;
	}

	/**
	 * Method for perform removeTerms operation
	 * 
	 * @param loanServicing
	 * @throws JaxenException
	 */
	@Override
	public FinanceDetail removeTerms(FinServiceInstruction fsi) {
		logger.debug(Literal.ENTERING);

		validationUtility.validate(fsi, RemoveTermsGroup.class);

		String finReference = fsi.getFinReference();
		String recalType = fsi.getRecalType();
		String eventCode = AccountingEvent.SCDCHG;

		APIErrorHandlerService.logReference(finReference);

		setDefaultDateFormats(fsi);

		FinanceDetail fd = null;
		WSReturnStatus returnStatus = new WSReturnStatus();
		Long finID = financeMainDAO.getActiveFinID(finReference, TableType.MAIN_TAB);
		if (finID != null) {
			fsi.setWif(false);
		} else {
			finID = financeMainDAO.getActiveWIFFinID(finReference, TableType.MAIN_TAB);
			if (finID != null) {
				fsi.setWif(true);
			} else {
				String[] valueParm = new String[1];
				valueParm[0] = finReference;
				returnStatus = APIErrorHandlerService.getFailedStatus("90201", valueParm);
			}
		}

		if (StringUtils.isNotBlank(returnStatus.getReturnCode())) {
			fd = new FinanceDetail();
			doEmptyResponseObject(fd);
			fd.setReturnStatus(returnStatus);
			return fd;
		}

		String rcdMaintainSts = financeMainDAO.getFinanceMainByRcdMaintenance(finID, "_View");
		if (StringUtils.isNotEmpty(rcdMaintainSts)) {
			String[] valueParm = new String[1];
			valueParm[0] = rcdMaintainSts;
			returnStatus = APIErrorHandlerService.getFailedStatus("LMS001", valueParm);
		}

		if (StringUtils.isNotBlank(returnStatus.getReturnCode())) {
			fd = new FinanceDetail();
			doEmptyResponseObject(fd);
			fd.setReturnStatus(returnStatus);
			return fd;
		}

		boolean writeoffLoan = financeWriteoffDAO.isWriteoffLoan(finID, "");
		if (writeoffLoan) {
			String[] valueParam = new String[1];
			valueParam[0] = "";
			returnStatus = APIErrorHandlerService.getFailedStatus("FWF001", valueParam);
		}

		if (StringUtils.isNotBlank(returnStatus.getReturnCode())) {
			fd = new FinanceDetail();
			doEmptyResponseObject(fd);
			fd.setReturnStatus(returnStatus);
			return fd;
		}

		if (StringUtils.isNotBlank(recalType)) {
			if (!CalculationConstants.RPYCHG_ADJMDT.equals(recalType)
					&& !CalculationConstants.RPYCHG_TILLMDT.equals(recalType)) {
				String[] valueParm = new String[1];
				valueParm[0] = recalType;
				returnStatus = APIErrorHandlerService.getFailedStatus("91104", valueParm);
			}
		}

		if (StringUtils.isNotBlank(returnStatus.getReturnCode())) {
			fd = new FinanceDetail();
			doEmptyResponseObject(fd);
			fd.setReturnStatus(returnStatus);
			return fd;
		}

		fsi.setFinID(finID);

		AuditDetail auditDetail = rmvTermsService.doValidations(fsi);
		if (auditDetail.getErrorDetails() != null) {
			for (ErrorDetail ed : auditDetail.getErrorDetails()) {
				fd = new FinanceDetail();
				doEmptyResponseObject(fd);
				fd.setReturnStatus(APIErrorHandlerService.getFailedStatus(ed.getCode(), ed.getError()));
				return fd;
			}
		}

		List<ErrorDetail> errors = doProcessServiceFees(fsi, eventCode);
		if (!errors.isEmpty()) {
			for (ErrorDetail ed : errors) {
				fd = new FinanceDetail();
				doEmptyResponseObject(fd);
				fd.setReturnStatus(APIErrorHandlerService.getFailedStatus(ed.getCode(), ed.getError()));
				return fd;
			}
		}

		fd = finServiceInstController.removeTerms(fsi, eventCode);

		logger.debug(Literal.LEAVING);
		return fd;
	}

	public FinanceDetail feePayment(FinServiceInstruction fsi) throws ServiceException {
		logger.debug(Literal.ENTERING);

		validationUtility.validate(fsi, UpfrontFeesGroup.class);

		String finReference = fsi.getFinReference();
		String externalReference = fsi.getExternalReference();

		if (StringUtils.isBlank(finReference) && StringUtils.isBlank(externalReference)) {
			String[] valueParm = new String[2];
			valueParm[0] = "finReference";
			valueParm[1] = "externalReference";
			return errorDetails("90123", valueParm);
		}

		if (StringUtils.isNotBlank(finReference) && StringUtils.isNotBlank(externalReference)) {
			String[] valueParm = new String[2];
			valueParm[0] = "finReference";
			valueParm[1] = "externalReference";
			return errorDetails("30511", valueParm);
		}

		if (StringUtils.isNotBlank(finReference)) {
			APIErrorHandlerService.logReference(finReference);
			fsi.setFromBranch("");
			fsi.setToBranch("");
			fsi.setFinType("");
			fsi.setCustCIF("");

		} else {
			APIErrorHandlerService.logReference(externalReference);

			if (StringUtils.isBlank(fsi.getFromBranch())) {
				String valueParm[] = new String[1];
				valueParm[0] = "fromBranch";
				return errorDetails("90502", valueParm);
			}

			if (StringUtils.isBlank(fsi.getFinType())) {
				String valueParm[] = new String[1];
				valueParm[0] = "finType";
				return errorDetails("90502", valueParm);
			}

			if (StringUtils.isBlank(fsi.getCustCIF())) {
				String valueParm[] = new String[1];
				valueParm[0] = "cif";
				return errorDetails("90502", valueParm);
			}

			Branch fromBranch = branchDAO.getBranchById(fsi.getFromBranch(), "");
			if (fromBranch == null) {
				String[] valueParm = new String[1];
				valueParm[0] = fsi.getFromBranch();
				return errorDetails("90129", valueParm);
			}

			Customer customer = customerDetailsService.checkCustomerByCIF(fsi.getCustCIF(),
					TableType.MAIN_TAB.getSuffix());
			if (customer == null) {
				String valueParm[] = new String[1];
				valueParm[0] = fsi.getCustCIF();
				return errorDetails("90101", valueParm);
			}
			fsi.setCustID(customer.getCustID());

			int count = receiptService.geFeeReceiptCountByExtReference(Objects.toString(customer.getCustID(), ""),
					FinServiceEvent.FEEPAYMENT, externalReference);
			if (count > 0) {
				String valueParm[] = new String[3];
				valueParm[0] = "Invalid CIF";
				valueParm[1] = fsi.getCustCIF();
				valueParm[2] = "externalreference is already assigned to another customer.";
				return errorDetails("30550", valueParm);
			}
		}

		if (fsi.getAmount() == null) {
			fsi.setAmount(BigDecimal.ZERO);
		}

		String moduleDefiner = FinServiceEvent.FEEPAYMENT;

		setDefaultDateFormats(fsi);

		FinanceDetail fd = null;

		boolean dedupFound = checkUpFrontDuplicateRequest(fsi, moduleDefiner);
		if (dedupFound) {
			String valueParm[] = new String[1];
			valueParm[0] = "transaction";
			return errorDetails("41014", valueParm);
		}

		fd = finServiceInstController.doFeePayment(fsi);

		logger.debug(Literal.LEAVING);
		return fd;
	}

	private FinanceDetail errorDetails(String errorCode, String parameter[]) {
		FinanceDetail financeDetail;
		financeDetail = new FinanceDetail();
		doEmptyResponseObject(financeDetail);
		financeDetail.setReturnStatus(APIErrorHandlerService.getFailedStatus(errorCode, parameter));
		return financeDetail;
	}

	private boolean checkUpFrontDuplicateRequest(FinServiceInstruction fsi, String moduleDefiner) {

		List<FinReceiptDetail> receiptDetails = null;
		if (StringUtils.isNotBlank(fsi.getFinReference())) {
			receiptDetails = finReceiptDetailDAO.getFinReceiptDetailByReference(fsi.getFinReference());
		} else {
			receiptDetails = finReceiptDetailDAO.getFinReceiptDetailByReference(Objects.toString(fsi.getCustID(), ""));
		}

		String paymentMode = fsi.getPaymentMode();

		if (paymentMode.equals(RepayConstants.RECEIPTMODE_RTGS) || paymentMode.equals(RepayConstants.RECEIPTMODE_NEFT)
				|| paymentMode.equals(RepayConstants.RECEIPTMODE_IMPS)
				|| paymentMode.equals(RepayConstants.RECEIPTMODE_ESCROW)) {
			if (fsi.getReceiptDetail() != null) {
				if (receiptDetails != null && !receiptDetails.isEmpty()) {
					for (FinReceiptDetail finReceiptDetail : receiptDetails) {
						if (finReceiptDetail.getAmount().compareTo(fsi.getAmount()) == 0 && StringUtils.equals(
								finReceiptDetail.getTransactionRef(), fsi.getReceiptDetail().getTransactionRef())) {
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
	 * @param fsi
	 * @return FinanceDetail
	 */
	@Override
	public FinanceDetail recalculate(FinServiceInstruction fsi) {
		logger.debug(Literal.ENTERING);

		FinanceDetail fd = null;

		String eventCode = AccountingEvent.SCDCHG;

		validationUtility.validate(fsi, RecalculateGroup.class);

		fd = validateInstruction(fsi, eventCode);

		if (fd != null) {
			return fd;
		}

		AuditDetail auditDetail = recalService.doValidations(fsi);

		if (auditDetail.getErrorDetails() != null) {
			for (ErrorDetail ed : auditDetail.getErrorDetails()) {
				fd = new FinanceDetail();
				doEmptyResponseObject(fd);
				fd.setReturnStatus(APIErrorHandlerService.getFailedStatus(ed.getCode(), ed.getError()));

				return fd;
			}
		}

		List<ErrorDetail> errors = doProcessServiceFees(fsi, eventCode);
		if (!errors.isEmpty()) {
			for (ErrorDetail ed : errors) {
				fd = new FinanceDetail();
				doEmptyResponseObject(fd);
				fd.setReturnStatus(APIErrorHandlerService.getFailedStatus(ed.getCode(), ed.getError()));
				return fd;
			}
		}

		fd = finServiceInstController.doRecalculate(fsi, eventCode);

		logger.debug(Literal.LEAVING);
		return fd;
	}

	/**
	 * Method for validate request object and do ChangeInterest action
	 * 
	 * @param fsi
	 * @return FinanceDetail
	 */
	@Override
	public FinanceDetail changeInterest(FinServiceInstruction fsi) {
		logger.debug(Literal.ENTERING);

		FinanceDetail fd = null;

		String eventCode = AccountingEvent.SCDCHG;

		validationUtility.validate(fsi, ChangeInterestGroup.class);

		fd = validateInstruction(fsi, eventCode);

		if (fd != null) {
			return fd;
		}

		AuditDetail auditDetail = changeProfitService.doValidations(fsi);
		if (auditDetail.getErrorDetails() != null) {
			for (ErrorDetail ed : auditDetail.getErrorDetails()) {
				fd = new FinanceDetail();
				doEmptyResponseObject(fd);
				fd.setReturnStatus(APIErrorHandlerService.getFailedStatus(ed.getCode(), ed.getError()));
				return fd;
			}
		}

		List<ErrorDetail> errors = doProcessServiceFees(fsi, eventCode);
		if (!errors.isEmpty()) {
			for (ErrorDetail ed : errors) {
				fd = new FinanceDetail();
				doEmptyResponseObject(fd);
				fd.setReturnStatus(APIErrorHandlerService.getFailedStatus(ed.getCode(), ed.getError()));
				return fd;
			}
		}

		fd = finServiceInstController.doChangeProfit(fsi, eventCode);

		logger.debug(Literal.LEAVING);
		return fd;
	}

	private FinanceDetail validateInstruction(FinServiceInstruction fsi, String eventCode) {
		String finReference = fsi.getFinReference();
		String reqType = fsi.getReqType();

		APIErrorHandlerService.logReference(finReference);

		FinanceDetail fd = null;

		if (!APIConstants.REQTYPE_INQUIRY.equals(reqType) && !APIConstants.REQTYPE_POST.equals(reqType)) {
			fd = new FinanceDetail();
			doEmptyResponseObject(fd);

			String valueParm[] = new String[1];
			valueParm[0] = reqType;
			fd.setReturnStatus(APIErrorHandlerService.getFailedStatus("91113", valueParm));
			return fd;
		}

		if (AccountingEvent.ADDDBSN.equals(eventCode)) {
			if (APIConstants.REQTYPE_POST.equals(reqType) && fsi.getDisbursementDetails() == null) {
				fd = new FinanceDetail();
				doEmptyResponseObject(fd);
				String valueParm[] = new String[1];
				valueParm[0] = "DisbursementDetails";
				fd.setReturnStatus(APIErrorHandlerService.getFailedStatus("90502", valueParm));
				return fd;
			}
		}

		setDefaultDateFormats(fsi);
		setDefaultForReferenceFields(fsi);

		Long finID = financeMainDAO.getActiveFinID(finReference, TableType.MAIN_TAB);

		if (finID != null) {
			fsi.setWif(false);
		} else {
			finID = financeMainDAO.getActiveWIFFinID(finReference, TableType.MAIN_TAB);
			if (finID != null) {
				fsi.setWif(true);
			}
		}

		if (finID == null) {
			fd = new FinanceDetail();
			doEmptyResponseObject(fd);
			String valueParm[] = new String[1];
			valueParm[0] = finReference;
			fd.setReturnStatus(APIErrorHandlerService.getFailedStatus("90201", valueParm));
			return fd;
		} else {
			fsi.setFinID(finID);
		}

		String rcdMaintainSts = financeMainDAO.getFinanceMainByRcdMaintenance(finID, "_View");
		if (StringUtils.isNotEmpty(rcdMaintainSts)) {
			fd = new FinanceDetail();
			doEmptyResponseObject(fd);
			String valueParm[] = new String[1];
			valueParm[0] = rcdMaintainSts;
			fd.setReturnStatus(APIErrorHandlerService.getFailedStatus("LMS001", valueParm));
			return fd;
		}

		boolean writeoffLoan = financeWriteoffDAO.isWriteoffLoan(finID, "");
		if (writeoffLoan) {
			fd = new FinanceDetail();
			doEmptyResponseObject(fd);
			String valueParm[] = new String[1];
			valueParm[0] = "";
			fd.setReturnStatus(APIErrorHandlerService.getFailedStatus("FWF001", valueParm));
			return fd;
		}

		return fd;

	}

	@Override
	public FinanceDetail addDisbursement(FinServiceInstruction fsi) {
		logger.debug(Literal.ENTERING);

		FinanceDetail fd = null;

		String eventCode = AccountingEvent.ADDDBSN;

		validationUtility.validate(fsi, AddDisbursementGroup.class);

		fd = validateInstruction(fsi, eventCode);

		if (fd != null) {
			return fd;
		}

		long finID = fsi.getFinID();
		String finReference = fsi.getFinReference();

		fd = finServiceInstController.getFinanceDetails(fsi, eventCode);
		FinScheduleData schdData = fd.getFinScheduleData();
		FinanceMain fm = schdData.getFinanceMain();
		List<FinFeeDetail> feeList = schdData.getFinFeeDetailList();

		List<FinFeeDetail> maintFeeList = new ArrayList<>();
		for (FinFeeDetail fee : feeList) {
			if (!fee.isOriginationFee()) {
				maintFeeList.add(fee);
			}
		}

		schdData.setFinFeeDetailList(maintFeeList);

		if (CalculationConstants.RPYCHG_TILLMDT.equals(fsi.getRecalType())) {
			fsi.setToDate(fm.getMaturityDate());
		}

		int count = validateBlockedFinances(finID);
		if (count > 0) {
			fd = new FinanceDetail();
			doEmptyResponseObject(fd);
			String valueParm[] = new String[2];
			valueParm[0] = "Disbursement";
			valueParm[1] = "FinReference: " + finReference;
			fd.setReturnStatus(APIErrorHandlerService.getFailedStatus("90204", valueParm));
			return fd;
		}

		fd.setAdvancePaymentsList(fsi.getDisbursementDetails());
		AuditDetail auditDetail = addDisbursementService.doValidations(fd, fsi);

		if (auditDetail.getErrorDetails() != null) {
			for (ErrorDetail ed : auditDetail.getErrorDetails()) {
				fd = new FinanceDetail();
				doEmptyResponseObject(fd);
				fd.setReturnStatus(APIErrorHandlerService.getFailedStatus(ed.getCode(), ed.getError()));
				return fd;
			}
		}

		// validate fees
		fd.setAccountingEventCode(eventCode);
		List<ErrorDetail> errors = doProcessServiceFees(fsi, eventCode);
		if (!errors.isEmpty()) {
			for (ErrorDetail ed : errors) {
				fd = new FinanceDetail();
				doEmptyResponseObject(fd);
				fd.setReturnStatus(APIErrorHandlerService.getFailedStatus(ed.getCode(), ed.getError()));
				return fd;
			}
		}

		fd = finServiceInstController.doAddDisbursement(fsi, fd, eventCode);

		logger.debug(Literal.LEAVING);
		return fd;
	}

	private int validateBlockedFinances(long finID) {
		return financeMainDAO.getCountByBlockedFinances(finID);
	}

	/**
	 * Method for process changeInstallement frequency received from API
	 * 
	 * @param fsi
	 * @return FinanceDetail
	 */
	@Override
	public FinanceDetail changeInstallmentFrq(FinServiceInstruction fsi) {
		logger.debug(Literal.ENTERING);

		FinanceDetail fd = null;

		String eventCode = AccountingEvent.SCDCHG;

		validationUtility.validate(fsi, ChangeInstallmentFrequencyGroup.class);

		fd = validateInstruction(fsi, eventCode);

		if (fd != null) {
			return fd;
		}

		AuditDetail auditDetail = changeFrequencyService.doValidations(fsi);
		if (auditDetail.getErrorDetails() != null) {
			for (ErrorDetail ed : auditDetail.getErrorDetails()) {
				fd = new FinanceDetail();
				doEmptyResponseObject(fd);
				fd.setReturnStatus(APIErrorHandlerService.getFailedStatus(ed.getCode(), ed.getError()));
				return fd;
			}
		}

		List<ErrorDetail> errors = doProcessServiceFees(fsi, eventCode);
		if (!errors.isEmpty()) {
			for (ErrorDetail ed : errors) {
				fd = new FinanceDetail();
				doEmptyResponseObject(fd);
				fd.setReturnStatus(APIErrorHandlerService.getFailedStatus(ed.getCode(), ed.getError()));
				return fd;
			}
		}

		fd = finServiceInstController.doChangeFrequency(fsi, eventCode);

		logger.debug(Literal.LEAVING);
		return fd;
	}

	/**
	 * Method for perform Re-Scheduling action with specified Instructions.
	 * 
	 * @param fsi
	 * @return FinanceDetail
	 */
	@Override
	public FinanceDetail reScheduling(FinServiceInstruction fsi) {
		logger.debug(Literal.ENTERING);

		FinanceDetail fd = null;

		String eventCode = AccountingEvent.SCDCHG;

		validationUtility.validate(fsi, ReSchedulingGroup.class);

		fd = validateInstruction(fsi, eventCode);

		if (fd != null) {
			return fd;
		}

		validationUtility.validate(fsi, ReSchedulingGroup.class);

		AuditDetail auditDetail = reScheduleService.doValidations(fsi);
		if (auditDetail.getErrorDetails() != null) {
			for (ErrorDetail ed : auditDetail.getErrorDetails()) {
				fd = new FinanceDetail();
				doEmptyResponseObject(fd);
				fd.setReturnStatus(APIErrorHandlerService.getFailedStatus(ed.getCode(), ed.getError()));

				return fd;
			}
		}

		List<ErrorDetail> errors = doProcessServiceFees(fsi, eventCode);
		if (!errors.isEmpty()) {
			for (ErrorDetail ed : errors) {
				fd = new FinanceDetail();
				doEmptyResponseObject(fd);
				fd.setReturnStatus(APIErrorHandlerService.getFailedStatus(ed.getCode(), ed.getError()));
				return fd;
			}
		}

		fd = finServiceInstController.doReSchedule(fsi, eventCode);

		logger.debug(Literal.LEAVING);
		return fd;
	}

	/**
	 * Method for perform updateLoanBasicDetails
	 * 
	 * @param fsi
	 * @throws JaxenException
	 */
	@Override
	public WSReturnStatus updateLoanBasicDetails(FinServiceInstruction fsi) {
		logger.debug(Literal.ENTERING);

		validationUtility.validate(fsi, UpdateLoanBasicDetailsGroup.class);

		String finReference = fsi.getFinReference();

		APIErrorHandlerService.logReference(finReference);

		setDefaultDateFormats(fsi);

		WSReturnStatus returnStatus = new WSReturnStatus();
		Long finID = financeMainDAO.getActiveFinID(finReference, TableType.MAIN_TAB);
		if (finID != null) {
			fsi.setWif(false);
		} else {
			finID = financeMainDAO.getActiveWIFFinID(finReference, TableType.MAIN_TAB);
			if (finID != null) {
				fsi.setWif(true);
			} else {
				String[] valueParm = new String[1];
				valueParm[0] = finReference;
				returnStatus = APIErrorHandlerService.getFailedStatus("90201", valueParm);
			}
		}

		if (StringUtils.isNotBlank(returnStatus.getReturnCode())) {
			return returnStatus;
		}

		String rcdMaintainSts = financeMainDAO.getFinanceMainByRcdMaintenance(finID, "_View");
		if (StringUtils.isNotEmpty(rcdMaintainSts)) {
			String[] valueParm = new String[1];
			valueParm[0] = rcdMaintainSts;
			returnStatus = APIErrorHandlerService.getFailedStatus("LMS001", valueParm);
		}

		if (StringUtils.isNotBlank(returnStatus.getReturnCode())) {
			return returnStatus;
		}

		boolean writeoffLoan = financeWriteoffDAO.isWriteoffLoan(finID, "");
		if (writeoffLoan) {
			String[] valueParam = new String[1];
			valueParam[0] = "";
			returnStatus = APIErrorHandlerService.getFailedStatus("FWF001", valueParam);
		}

		if (StringUtils.isNotBlank(returnStatus.getReturnCode())) {
			return returnStatus;
		}

		// call service level validations which include business validations
		FinanceMain fm = new FinanceMain();
		fm.setFinID(finID);
		fm.setFinReference(finReference);
		fm.setDsaCode(fsi.getDsaCode());
		fm.setSalesDepartment(fsi.getSalesDepartment());
		fm.setDmaCode(fsi.getDmaCode());
		fm.setAccountsOfficer(fsi.getAccountsOfficer());
		fm.setReferralId(fsi.getReferralId());

		returnStatus = financeValidationService.validateFinBasicDetails(fm);
		if (StringUtils.isNotBlank(returnStatus.getReturnCode())) {
			return returnStatus;
		}

		returnStatus = finServiceInstController.updateLoanBasicDetails(fm);

		logger.debug(Literal.LEAVING);
		return returnStatus;
	}

	/**
	 * Method for perform updateLoanPenaltyDetails
	 * 
	 * @param fsi
	 * @throws JaxenException
	 */
	@Override
	public WSReturnStatus updateLoanPenaltyDetails(FinServiceInstruction fsi) {
		logger.debug(Literal.ENTERING);

		validationUtility.validate(fsi, UpdateLoanPenaltyDetailGroup.class);

		String finReference = fsi.getFinReference();

		APIErrorHandlerService.logReference(finReference);

		FinODPenaltyRate odPenaltyRate = fsi.getFinODPenaltyRate();
		if (odPenaltyRate != null) {
			if (odPenaltyRate.isApplyODPenalty()) {
				if (odPenaltyRate.getODChargeAmtOrPerc() == null) {
					odPenaltyRate.setODChargeAmtOrPerc(BigDecimal.ZERO);
				}
				if (odPenaltyRate.getODMaxWaiverPerc() == null) {
					odPenaltyRate.setODMaxWaiverPerc(BigDecimal.ZERO);
				}
				if (odPenaltyRate.getODGraceDays() <= 0) {
					return beanValidation("odGraceDays");
				}
				if (StringUtils.isBlank(odPenaltyRate.getODChargeType())) {
					return beanValidation("odChargeType");
				}
				if (StringUtils.isBlank(odPenaltyRate.getODChargeCalOn())
						&& StringUtils.equals(odPenaltyRate.getODChargeType(),
								FinanceConstants.PENALTYTYPE_PERC_ONETIME)
						|| StringUtils.equals(odPenaltyRate.getODChargeType(),
								FinanceConstants.PENALTYTYPE_PERC_ON_DUEDAYS)
						|| StringUtils.equals(odPenaltyRate.getODChargeType(),
								FinanceConstants.PENALTYTYPE_PERC_ON_PD_MTH)) {
					return beanValidation("odChargeCalOn");
				}
				if (odPenaltyRate.getODChargeAmtOrPerc().compareTo(BigDecimal.ZERO) < 0) {
					return beanValidation("odChargeAmtOrPerc");
				}
				if (odPenaltyRate.isODAllowWaiver()) {
					if (odPenaltyRate.getODMaxWaiverPerc().compareTo(BigDecimal.ZERO) < 0) {
						return beanValidation("odMaxWaiverPerc");
					}
				}
			} else {
				if (odPenaltyRate.isODIncGrcDays() || StringUtils.isNotBlank(odPenaltyRate.getODChargeType())
						|| StringUtils.isNotBlank(odPenaltyRate.getODChargeCalOn())
						|| odPenaltyRate.getODChargeAmtOrPerc().compareTo(BigDecimal.ZERO) > 0
						|| odPenaltyRate.isODAllowWaiver()) {
					String[] valueParm = new String[1];
					return APIErrorHandlerService.getFailedStatus("90315", valueParm);
				}
			}
			if (StringUtils.equals(odPenaltyRate.getODChargeType(), FinanceConstants.PENALTYTYPE_FLAT) || StringUtils
					.equals(odPenaltyRate.getODChargeType(), FinanceConstants.PENALTYTYPE_FLAT_ON_PD_MTH)) {
				odPenaltyRate.setODChargeCalOn("");
			}
		} else {
			return beanValidation("overdue");
		}

		WSReturnStatus returnStatus = new WSReturnStatus();
		Long finID = financeMainDAO.getActiveFinID(finReference, TableType.MAIN_TAB);
		if (finID != null) {
			fsi.setWif(false);
		} else {
			finID = financeMainDAO.getActiveWIFFinID(finReference, TableType.MAIN_TAB);
			if (finID != null) {
				fsi.setWif(true);
			} else {
				String[] valueParm = new String[1];
				valueParm[0] = finReference;
				returnStatus = APIErrorHandlerService.getFailedStatus("90201", valueParm);
			}
		}

		if (StringUtils.isNotBlank(returnStatus.getReturnCode())) {
			return returnStatus;
		}

		String rcdMaintainSts = financeMainDAO.getFinanceMainByRcdMaintenance(finID, "_View");
		if (StringUtils.isNotEmpty(rcdMaintainSts)) {
			String[] valueParm = new String[1];
			valueParm[0] = rcdMaintainSts;
			returnStatus = APIErrorHandlerService.getFailedStatus("LMS001", valueParm);
		}

		if (StringUtils.isNotBlank(returnStatus.getReturnCode())) {
			return returnStatus;
		}

		boolean writeoffLoan = financeWriteoffDAO.isWriteoffLoan(finID, "");
		if (writeoffLoan) {
			String[] valueParam = new String[1];
			valueParam[0] = "";
			returnStatus = APIErrorHandlerService.getFailedStatus("FWF001", valueParam);
		}

		if (StringUtils.isNotBlank(returnStatus.getReturnCode())) {
			return returnStatus;
		}

		// call service level validations which include business validations
		FinODPenaltyRate finODPenaltyRate = new FinODPenaltyRate();
		finODPenaltyRate.setFinID(finID);
		finODPenaltyRate.setFinReference(finReference);
		finODPenaltyRate.setApplyODPenalty(odPenaltyRate.isApplyODPenalty());
		finODPenaltyRate.setODIncGrcDays(odPenaltyRate.isODIncGrcDays());
		finODPenaltyRate.setODGraceDays(odPenaltyRate.getODGraceDays());
		finODPenaltyRate.setODChargeType(odPenaltyRate.getODChargeType());
		finODPenaltyRate.setODChargeCalOn(odPenaltyRate.getODChargeCalOn());
		finODPenaltyRate.setODChargeAmtOrPerc(odPenaltyRate.getODChargeAmtOrPerc());
		finODPenaltyRate.setODAllowWaiver(odPenaltyRate.isODAllowWaiver());
		finODPenaltyRate.setODMaxWaiverPerc(odPenaltyRate.getODMaxWaiverPerc());
		finODPenaltyRate.setFinEffectDate(SysParamUtil.getAppDate());

		returnStatus = validatefinODPenaltyRate(finODPenaltyRate);
		if (StringUtils.isNotBlank(returnStatus.getReturnCode())) {
			return returnStatus;
		}

		returnStatus = finServiceInstController.updateLoanPenaltyDetails(finODPenaltyRate);

		logger.debug(Literal.LEAVING);
		return returnStatus;
	}

	/**
	 * Method for process Early settlement request received from API.
	 * 
	 * @param fsi
	 * @return FinanceDetail
	 */
	@Override
	public FinanceDetail earlySettlement(FinServiceInstruction fsi) {
		try {
			String moduleDefiner = FinServiceEvent.EARLYSETTLE;
			FinanceDetail fd = receiptTransaction(fsi, moduleDefiner);
			return fd;
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
	public FinanceDetail partialSettlement(FinServiceInstruction fsi) {
		try {
			String moduleDefiner = FinServiceEvent.EARLYRPY;
			fsi.setReceivedDate(fsi.getReceiptDetail().getReceivedDate());
			FinanceDetail fd = receiptTransaction(fsi, moduleDefiner);
			return fd;
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
	public FinanceDetail manualPayment(FinServiceInstruction fsi) throws ServiceException {
		try {
			String moduleDefiner = FinServiceEvent.SCHDRPY;
			FinanceDetail fd = receiptTransaction(fsi, moduleDefiner);
			return fd;
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

	public FinanceDetail receiptTransaction(FinServiceInstruction fsi, String moduleDefiner) throws ServiceException {
		logger.debug(Literal.ENTERING);

		String eventCode = null;
		if (StringUtils.equals(moduleDefiner, FinServiceEvent.SCHDRPY)) {
			eventCode = AccountingEvent.REPAY;
			fsi.setModuleDefiner(FinServiceEvent.SCHDRPY);
			fsi.setReceiptPurpose(FinServiceEvent.SCHDRPY);
			if (!fsi.isReceiptUpload()) {
				validationUtility.validate(fsi, SchedulePaymentGroup.class);
			}
		} else if (StringUtils.equals(moduleDefiner, FinServiceEvent.EARLYRPY)) {
			eventCode = AccountingEvent.EARLYPAY;
			fsi.setModuleDefiner(FinServiceEvent.EARLYRPY);
			fsi.setReceiptPurpose(FinServiceEvent.EARLYRPY);
			if (!fsi.isReceiptUpload()) {
				validationUtility.validate(fsi, PartialSettlementGroup.class);
			}
		} else if (StringUtils.equals(moduleDefiner, FinServiceEvent.EARLYSETTLE)) {
			eventCode = AccountingEvent.EARLYSTL;
			fsi.setModuleDefiner(FinServiceEvent.EARLYSETTLE);
			fsi.setReceiptPurpose(FinServiceEvent.EARLYSETTLE);
			if (!fsi.isReceiptUpload()) {
				validationUtility.validate(fsi, EarlySettlementGroup.class);
			}
		}

		// Method for validate instruction details
		FinanceDetail fd = new FinanceDetail();
		FinScheduleData schdData = fd.getFinScheduleData();

		if (fsi.getValueDate() == null) {
			fsi.setValueDate(fsi.getReceiptDetail().getReceivedDate());
			fsi.getReceiptDetail().setValueDate(fsi.getReceiptDetail().getReceivedDate());
		}

		fsi.setReceivedDate(fsi.getReceiptDetail().getReceivedDate());
		schdData.setFinServiceInstruction(fsi);
		fd = validateInstructions(fd, moduleDefiner, eventCode);
		fsi.setAmount(fsi.getAmount().add(fsi.getTdsAmount()));
		FinReceiptData receiptData = receiptService.doReceiptValidations(fd, moduleDefiner);
		fd = receiptData.getFinanceDetail();
		schdData = fd.getFinScheduleData();

		if (schdData.getErrorDetails() != null && !schdData.getErrorDetails().isEmpty()) {
			logger.debug("Leaving - doReceiptValidations Error");
			return setReturnStatus(fd);
		}

		receiptService.setReceiptData(receiptData);
		fd = finServiceInstController.doReceiptTransaction(receiptData, eventCode);

		if (fd.getFinScheduleData() != null && fd.getFinScheduleData().getErrorDetails() != null
				&& !fd.getFinScheduleData().getErrorDetails().isEmpty()) {
			fd = setReturnStatus(fd);
		}

		logger.debug(Literal.LEAVING);
		return fd;
	}

	public FinanceDetail setReturnStatus(FinanceDetail fd) {
		WSReturnStatus returnStatus = new WSReturnStatus();

		FinScheduleData schdData = fd.getFinScheduleData();
		ErrorDetail errorDetail = schdData.getErrorDetails().get(0);

		returnStatus.setReturnCode(errorDetail.getCode());
		returnStatus.setReturnText(errorDetail.getError());

		fd.setFinScheduleData(null);
		fd.setDocumentDetailsList(null);
		fd.setJointAccountDetailList(null);
		fd.setGurantorsDetailList(null);
		fd.setCollateralAssignmentList(null);
		fd.setReturnDataSetList(null);
		fd.setInterfaceDetailList(null);
		fd.setFinFlagsDetails(null);
		fd.setCustomerDetails(null);
		fd.setReturnStatus(returnStatus);

		return fd;
	}

	/**
	 * Method for perform Schedule method Change action by taking specified instructions.
	 * 
	 * @param fsi
	 * @return FinanceDetail
	 */
	@Override
	public FinanceDetail scheduleMethodChange(FinServiceInstruction fsi) throws ServiceException {
		logger.debug(Literal.ENTERING);

		FinanceDetail fd = null;

		String eventCode = AccountingEvent.SCDCHG;

		validationUtility.validate(fsi, ScheduleMethodGroup.class);

		fd = validateInstruction(fsi, eventCode);

		if (fd != null) {
			return fd;
		}

		long finID = fsi.getFinID();

		FinanceMain fm = financeMainDAO.getFinanceMainById(finID, "", fsi.isWif());
		if (fm.isStepFinance()) {
			String[] valueParm = new String[2];
			valueParm[0] = "Step Loan";
			valueParm[1] = "Schedule Change Method";
			fd = new FinanceDetail();
			doEmptyResponseObject(fd);
			fd.setReturnStatus(APIErrorHandlerService.getFailedStatus("90329", valueParm));
			return fd;
		}

		AuditDetail auditDetail = changeScheduleMethodService.doValidations(fsi);
		if (auditDetail.getErrorDetails() != null) {
			for (ErrorDetail ed : auditDetail.getErrorDetails()) {
				fd = new FinanceDetail();
				doEmptyResponseObject(fd);
				fd.setReturnStatus(APIErrorHandlerService.getFailedStatus(ed.getCode(), ed.getError()));
				return fd;
			}
		}

		List<ErrorDetail> errors = doProcessServiceFees(fsi, eventCode);
		if (!errors.isEmpty()) {
			for (ErrorDetail ed : errors) {
				fd = new FinanceDetail();
				doEmptyResponseObject(fd);
				fd.setReturnStatus(APIErrorHandlerService.getFailedStatus(ed.getCode(), ed.getError()));
				return fd;
			}
		}

		fd = finServiceInstController.doChangeScheduleMethod(fsi, eventCode);

		logger.debug(Literal.LEAVING);
		return fd;
	}

	/**
	 * Method for perform changePureflexiTenure action by taking specified instructions.
	 * 
	 * @param fsi
	 * @return FinanceDetail
	 */
	@Override
	public FinanceDetail changeGestationPeriod(FinServiceInstruction fsi) throws ServiceException {
		logger.debug(Literal.ENTERING);

		FinanceDetail fd = null;

		String eventCode = AccountingEvent.GRACEEND;

		validationUtility.validate(fsi, ChangeGestationGroup.class);

		fd = validateInstruction(fsi, eventCode);

		if (fd != null) {
			return fd;
		}

		List<ErrorDetail> errors = doProcessServiceFees(fsi, eventCode);
		if (!errors.isEmpty()) {
			for (ErrorDetail ed : errors) {
				fd = new FinanceDetail();
				doEmptyResponseObject(fd);
				fd.setReturnStatus(APIErrorHandlerService.getFailedStatus(ed.getCode(), ed.getError()));
				return fd;
			}
		}

		fd = finServiceInstController.doChangeGestationPeriod(fsi, eventCode);

		logger.debug(Literal.LEAVING);
		return fd;
	}

	private FinanceDetail validateInstructions(FinanceDetail fd, String moduleDefiner, String eventCode) {
		logger.debug(Literal.ENTERING);

		FinScheduleData schdData = fd.getFinScheduleData();
		FinServiceInstruction fsi = schdData.getFinServiceInstruction();
		String finReference = fsi.getFinReference();
		fd.setFinReference(finReference);
		ErrorDetail errorDetail = new ErrorDetail();

		WSReturnStatus returnStatus = validateReqType(fsi.getReqType());
		if (StringUtils.isNotBlank(returnStatus.getReturnCode())) {
			errorDetail.setCode(returnStatus.getReturnCode());
			errorDetail.setMessage(returnStatus.getReturnText());
			errorDetail.setExtendedMessage(returnStatus.getReturnText());
			schdData.setErrorDetail(errorDetail);
			return fd;
		}

		logger.debug(Literal.LEAVING);
		return fd;
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

	public WSReturnStatus getErrorDetails(String errorCode, String[] valueParm) {
		logger.debug(Literal.ENTERING);

		WSReturnStatus response = new WSReturnStatus();
		response = APIErrorHandlerService.getFailedStatus(errorCode, valueParm);

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

		if (!APIConstants.REQTYPE_INQUIRY.equals(reqType) && !APIConstants.REQTYPE_POST.equals(reqType)) {
			String valueParm[] = new String[1];
			valueParm[0] = reqType;
			return APIErrorHandlerService.getFailedStatus("91113", valueParm);
		}

		logger.debug(Literal.LEAVING);
		return new WSReturnStatus();
	}

	private List<ErrorDetail> doProcessServiceFees(FinServiceInstruction fsi, String eventCode) {
		return financeDataValidation.doFeeValidations(PennantConstants.VLD_SRV_LOAN, fsi, eventCode);
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

		FinanceTaxDetail td = null;
		Long finID = financeMainDAO.getActiveFinID(finReference);
		if (finID == null) {
			td = new FinanceTaxDetail();
			String[] valueParam = new String[1];
			valueParam[0] = finReference;
			td.setReturnStatus(APIErrorHandlerService.getFailedStatus("90201", valueParam));
			return td;
		}

		td = financeTaxDetailService.getApprovedFinanceTaxDetail(finID);
		if (null == td) {
			td = new FinanceTaxDetail();
			String[] valueParam = new String[1];
			valueParam[0] = finReference;

			td.setReturnStatus(APIErrorHandlerService.getFailedStatus("90266", valueParam));
			return td;
		}

		logger.info(Literal.LEAVING);

		return td;
	}

	@Override
	public WSReturnStatus addGSTDetails(final FinanceTaxDetail td) throws ServiceException {
		logger.info(Literal.ENTERING);
		String finReference = td.getFinReference();

		Long finID = financeMainDAO.getActiveFinID(finReference);
		td.setFinID(finID);

		WSReturnStatus returnStatus = new WSReturnStatus();

		if (finID == null) {
			String[] valueParm = new String[1];
			valueParm[0] = finReference;
			returnStatus = APIErrorHandlerService.getFailedStatus("90201", valueParm);
		}

		if (StringUtils.isNotBlank(returnStatus.getReturnCode())) {
			return returnStatus;
		}

		returnStatus = isWriteoffLoan(finID);
		if (returnStatus != null) {
			return returnStatus;
		}

		List<ErrorDetail> validationErrors = financeTaxDetailService.doGSTValidations(td);

		if (CollectionUtils.isNotEmpty(validationErrors)) {
			for (ErrorDetail ed : validationErrors) {
				returnStatus = APIErrorHandlerService.getFailedStatus(ed.getCode(), ed.getParameters());
				return returnStatus;
			}
		}

		List<ErrorDetail> coApplicantErrors = financeTaxDetailService.verifyCoApplicantDetails(td);

		if (CollectionUtils.isNotEmpty(coApplicantErrors)) {
			for (ErrorDetail ed : coApplicantErrors) {
				returnStatus = APIErrorHandlerService.getFailedStatus(ed.getCode(), ed.getParameters());
				return returnStatus;
			}
		}

		int taxDetailRecords = financeTaxDetailService.getFinanceTaxDetailsByCount(finID);
		if (taxDetailRecords > 0) {
			String[] valueParm = new String[1];
			valueParm[0] = finReference;

			return APIErrorHandlerService.getFailedStatus("90248", valueParm);
		}

		returnStatus = finServiceInstController.saveGSTDetails(td);

		logger.info(Literal.LEAVING);

		return returnStatus;
	}

	@Override
	public WSReturnStatus updateGSTDetails(final FinanceTaxDetail td) throws ServiceException {
		logger.info(Literal.ENTERING);

		String finReference = td.getFinReference();

		Long finID = financeMainDAO.getActiveFinID(finReference);
		td.setFinID(finID);

		WSReturnStatus returnStatus = new WSReturnStatus();

		if (finID == null) {
			String[] valueParm = new String[1];
			valueParm[0] = finReference;
			returnStatus = APIErrorHandlerService.getFailedStatus("90201", valueParm);
		}

		if (StringUtils.isNotBlank(returnStatus.getReturnCode())) {
			return returnStatus;
		}

		returnStatus = isWriteoffLoan(finID);
		if (returnStatus != null) {
			return returnStatus;
		}

		List<ErrorDetail> validationErrors = financeTaxDetailService.doGSTValidations(td);

		if (CollectionUtils.isNotEmpty(validationErrors)) {
			for (ErrorDetail ed : validationErrors) {
				returnStatus = APIErrorHandlerService.getFailedStatus(ed.getCode(), ed.getParameters());
				return returnStatus;
			}
		}

		FinanceTaxDetail currentFinanceTaxData = finServiceInstController.getFinanceTaxDetails(finID);

		if (currentFinanceTaxData == null) {
			String[] valueParm = new String[1];
			valueParm[0] = finReference;
			returnStatus = APIErrorHandlerService.getFailedStatus("90266", valueParm);
			return returnStatus;
		}

		List<ErrorDetail> coApplicantErrors = financeTaxDetailService.verifyCoApplicantDetails(td);

		if (CollectionUtils.isNotEmpty(coApplicantErrors)) {
			for (ErrorDetail ed : coApplicantErrors) {
				returnStatus = APIErrorHandlerService.getFailedStatus(ed.getCode(), ed.getParameters());
				return returnStatus;
			}
		}

		int taxDetailRecords = financeTaxDetailService.getFinanceTaxDetailsByCount(finID);
		if (taxDetailRecords > 0) {
			String[] valueParm = new String[1];
			valueParm[0] = finReference;

			return APIErrorHandlerService.getFailedStatus("90248", valueParm);
		}
		returnStatus = finServiceInstController.rejuvenateGSTDetails(td, currentFinanceTaxData.getVersion());

		logger.info(Literal.LEAVING);

		return returnStatus;
	}

	@Override
	public WSReturnStatus approveDisbursementResponse(DisbRequest disbRequest) throws ServiceException {
		String finReference = disbRequest.getFinReference();

		Long finID = financeMainDAO.getActiveFinID(finReference);

		WSReturnStatus returnStatus = new WSReturnStatus();

		if (finID == null) {
			String[] valueParm = new String[1];
			valueParm[0] = finReference;
			returnStatus = APIErrorHandlerService.getFailedStatus("90201", valueParm);
		}

		if (StringUtils.isNotBlank(returnStatus.getReturnCode())) {
			return returnStatus;
		}

		if (isWriteoffLoan(finID) != null) {
			return returnStatus;
		}

		returnStatus = validateDisbursementResponse(disbRequest);

		if (returnStatus != null) {
			return returnStatus;
		}

		returnStatus = finServiceInstController.approveDisbursementResponse(disbRequest);
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
		Long finID = financeMainDAO.getActiveFinID(finReference, TableType.MAIN_TAB);
		if (finID == null) {
			String[] valueParam = new String[1];
			valueParam[0] = finReference;
			response.setReturnStatus(APIErrorHandlerService.getFailedStatus("90201", valueParam));

			logger.info("The specified FinReference>> {} is not exists or in-active", finReference);
			return response;
		}

		/* Fetching Disbursement instructions */
		disbResponse.addAll(getDisbInstructions(finID));

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
			detail.setStatus(vr.getStatus());
			detail.setType(DisbursementConstants.CHANNEL_VAS);
			vasInstructions.add(detail);
		}

		return vasInstructions;
	}

	private List<DisbResponse> getDisbInstructions(long finID) {
		List<DisbResponse> disbInstructions = new ArrayList<>();
		List<FinAdvancePayments> fapList = finAdvancePaymentsService.getFinAdvancePaymentsById(finID, " ");
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
	public FinanceDetail restructuring(RestructureDetail rd) throws ServiceException {
		logger.debug(Literal.ENTERING);

		WSReturnStatus returnStatus = new WSReturnStatus();
		FinanceDetail fd = null;

		String finReference = rd.getFinReference();
		Long finID = financeMainDAO.getFinID(finReference, TableType.MAIN_TAB);
		rd.setFinID(finID);

		APIErrorHandlerService.logReference(finReference);

		returnStatus = validateReqType(rd.getReqType());

		if (StringUtils.isNotBlank(returnStatus.getReturnCode())) {
			fd = new FinanceDetail();
			doEmptyResponseObject(fd);
			fd.setReturnStatus(returnStatus);

			return fd;
		}

		FinServiceInstruction fsi = new FinServiceInstruction();
		fsi.setFinID(finID);
		fsi.setFinReference(finReference);
		returnStatus = validateFinReference(fsi);

		if (StringUtils.isNotBlank(returnStatus.getReturnCode())) {
			fd = new FinanceDetail();
			doEmptyResponseObject(fd);
			fd.setReturnStatus(returnStatus);

			return fd;
		}

		List<ErrorDetail> errors = restructureService.doValidations(rd);

		for (ErrorDetail ed : errors) {
			fd = new FinanceDetail();
			doEmptyResponseObject(fd);
			fd.setReturnStatus(APIErrorHandlerService.getFailedStatus(ed.getCode(), ed.getError()));
			return fd;
		}

		returnStatus = isWriteoffLoan(finID);
		if (StringUtils.isNotBlank(returnStatus.getReturnCode())) {
			fd = new FinanceDetail();
			doEmptyResponseObject(fd);
			fd.setReturnStatus(returnStatus);

			return fd;
		}

		returnStatus = isReceiptPending(finID);
		if (StringUtils.isNotBlank(returnStatus.getReturnCode())) {
			fd = new FinanceDetail();
			doEmptyResponseObject(fd);
			fd.setReturnStatus(returnStatus);

			return fd;
		}

		returnStatus = checkPresentmentsInQueue(finID);
		if (StringUtils.isNotBlank(returnStatus.getReturnCode())) {
			fd = new FinanceDetail();
			doEmptyResponseObject(fd);
			fd.setReturnStatus(returnStatus);

			return fd;
		}

		return finServiceInstController.doRestructuring(rd, fsi, AccountingEvent.RESTRUCTURE);
	}

	private WSReturnStatus validateFinReference(FinServiceInstruction serviceInst) {
		logger.debug(Literal.ENTERING);

		WSReturnStatus returnStatus = new WSReturnStatus();

		// check records in origination and WIF
		long finID = serviceInst.getFinID();
		String finReference = serviceInst.getFinReference();
		int count = financeMainDAO.getFinanceCountById(finID, "", false);
		if (count > 0) {
			serviceInst.setWif(false);
		} else {
			count = financeMainDAO.getFinanceCountById(finID, "", true);
			if (count > 0) {
				serviceInst.setWif(true);
			} else {
				String[] valueParm = new String[1];
				valueParm[0] = finReference;
				return returnStatus = APIErrorHandlerService.getFailedStatus("90201", valueParm);
			}
		}

		// Validate Loan is INPROGRESS in any Other Servicing Event or NOT ?
		String rcdMaintainSts = financeMainDAO.getFinanceMainByRcdMaintenance(finReference, "_View");
		if (StringUtils.isNotEmpty(rcdMaintainSts)) {
			String[] valueParm = new String[1];
			valueParm[0] = rcdMaintainSts;
			return returnStatus = APIErrorHandlerService.getFailedStatus("LMS001", valueParm);
		}

		logger.debug(Literal.LEAVING);
		return returnStatus;
	}

	private WSReturnStatus isReceiptPending(long finID) {
		if (receiptService.isReceiptsPending(finID, Long.MIN_VALUE)) {
			String[] valueParam = new String[1];
			valueParam[0] = PennantJavaUtil.getLabel("label_Receipts_Inprogress");
			return APIErrorHandlerService.getFailedStatus("92021", valueParam);
		}

		return new WSReturnStatus();
	}

	private WSReturnStatus checkPresentmentsInQueue(long finID) {
		if (receiptService.checkPresentmentsInQueue(finID)) {
			String[] valueParam = new String[1];
			valueParam[0] = PennantJavaUtil.getLabel("label_Receipts_Inprogress");
			return APIErrorHandlerService.getFailedStatus("92021", valueParam);
		}

		return new WSReturnStatus();
	}

	@Override
	public WSReturnStatus updateCovenants(FinanceDetail fd) throws ServiceException {
		logger.debug(Literal.ENTERING);
		List<ErrorDetail> errorDetails;

		try {
			FinanceMain financeMain = null;
			String finReference = fd.getFinReference();
			List<Covenant> covenantsList = fd.getCovenants();

			// validating the covenants if same covenant type and same category name

			boolean origination = fd.isOrigination();

			if (finReference == null) {
				String[] valueParm = new String[1];
				valueParm[0] = "FinReference";
				return APIErrorHandlerService.getFailedStatus("90502", valueParm);
			}

			financeMain = financeMainDAO.getFinanceMain(finReference);
			if (financeMain == null || !financeMain.isFinIsActive()) {
				String[] valueParm = new String[1];
				valueParm[0] = finReference;
				return APIErrorHandlerService.getFailedStatus("90201", valueParm);
			}

			financeMain.setUserDetails(SessionUserDetails.getUserDetails(SessionUserDetails.getLogiedInUser()));

			if (covenantsList == null) {
				String[] valueParm = new String[1];
				valueParm[0] = "CovenatDetails ";
				return APIErrorHandlerService.getFailedStatus("90502", valueParm);
			}

			FinanceMain fmMainTab = null;

			boolean mainTable = false;
			boolean tempTable = false;
			if (StringUtils.isBlank(financeMain.getRecordType())) {
				mainTable = true;
			} else {
				tempTable = true;
				fmMainTab = financeMainDAO.getFinanceMain(finReference, TableType.MAIN_TAB);
			}
			if (fmMainTab != null) {
				mainTable = true;
			}

			if (mainTable && origination == true) {
				String[] valueParm = new String[2];
				valueParm[0] = "Orgination";
				valueParm[1] = "false, Because Loan is in LMS ";
				return APIErrorHandlerService.getFailedStatus("41000", valueParm);
			}

			if (!mainTable && tempTable && !origination) {
				String[] valueParm = new String[2];
				valueParm[0] = "Orgination";
				valueParm[1] = "true, Because Loan is in LOS ";
				return APIErrorHandlerService.getFailedStatus("41000", valueParm);
			}

			// for logging purpose
			APIErrorHandlerService.logReference(finReference);
			// If Origination(true) Validations for the LOS, If not Validations For LMS
			if (origination) {
				errorDetails = financeDataValidation.covenantValidation(financeMain, covenantsList, "LOS");
			} else {
				errorDetails = financeDataValidation.covenantValidation(financeMain, covenantsList, null);
			}
			for (ErrorDetail errorDetail : errorDetails) {
				return APIErrorHandlerService.getFailedStatus(errorDetail.getCode(), errorDetail.getParameters());
			}

			return finServiceInstController.processCovenants(financeMain, covenantsList, origination);
		} catch (Exception e) {
			logger.error(Literal.EXCEPTION + e);
			return APIErrorHandlerService.getFailedStatus();
		}
	}

	@Override
	public WSReturnStatus saveChequeDetails(FinanceDetail fd) throws ServiceException {
		logger.debug(Literal.ENTERING);

		String finReference = fd.getFinReference();

		WSReturnStatus returnStatus = new WSReturnStatus();
		List<ErrorDetail> errorDetails;
		try {
			FinanceMain fm = null;

			ChequeHeader chequeHeader = fd.getChequeHeader();

			FinScheduleData schdData = fd.getFinScheduleData();
			if (finReference == null) {
				String[] valueParm = new String[1];
				valueParm[0] = "FinReference";
				return returnStatus = APIErrorHandlerService.getFailedStatus("90502", valueParm);
			} else {
				fm = financeMainDAO.getFinanceMainByRef(finReference, "", false);
				if (fm == null || !fm.isFinIsActive() || StringUtils.isNotEmpty(fm.getRcdMaintainSts())) {
					String[] valueParm = new String[1];
					valueParm[0] = finReference;
					return returnStatus = APIErrorHandlerService.getFailedStatus("90201", valueParm);
				} else {
					schdData.setFinanceMain(fm);
					fd.setFinID(fm.getFinID());
				}
				returnStatus = isWriteoffLoan(fm.getFinID());
				if (returnStatus != null) {
					return returnStatus;
				}
			}

			if (chequeHeader == null) {
				String[] valueParm = new String[1];
				valueParm[0] = "Cheque Details ";
				return returnStatus = APIErrorHandlerService.getFailedStatus("90502", valueParm);
			}

			// for logging purpose
			APIErrorHandlerService.logReference(finReference);
			errorDetails = chequeHeaderService.chequeValidationForUpdate(fd, PennantConstants.method_save, "");
			for (ErrorDetail errorDetail : errorDetails) {
				returnStatus = APIErrorHandlerService.getFailedStatus(errorDetail.getCode(),
						errorDetail.getParameters());
				return returnStatus;
			}

			List<FinanceScheduleDetail> schedules = null;
			schedules = financeScheduleDetailDAO.getFinScheduleDetails(fm.getFinID(), "", false);
			schdData.setFinanceScheduleDetails(schedules);

			validateChequeDetails(fd);

			if (CollectionUtils.isNotEmpty(schdData.getErrorDetails())) {
				for (ErrorDetail errorDetail : schdData.getErrorDetails()) {
					return APIErrorHandlerService.getFailedStatus(errorDetail.getCode(), errorDetail.getError());
				}
			}
			returnStatus = finServiceInstController.processChequeDetail(fd, "");

		} catch (Exception e) {
			logger.error(Literal.EXCEPTION + e);
			return APIErrorHandlerService.getFailedStatus();
		}
		logger.debug(Literal.LEAVING);
		return returnStatus;
	}

	private void validateChequeDetails(FinanceDetail financeDetail) {
		boolean date = true;
		FinScheduleData schdData = financeDetail.getFinScheduleData();
		ChequeHeader chequeHeader = financeDetail.getChequeHeader();
		List<ChequeDetail> chequeDetailsList = chequeHeader.getChequeDetailList();
		for (ChequeDetail chequeDetail : chequeDetailsList) {
			// schedules validation
			if (StringUtils.equals(FinanceConstants.REPAYMTH_PDC, chequeDetail.getChequeType())) {
				List<FinanceScheduleDetail> schedules = financeDetail.getFinScheduleData().getFinanceScheduleDetails();
				for (FinanceScheduleDetail fsd : schedules) {
					if (DateUtil.compare(fsd.getSchDate(), chequeDetail.getChequeDate()) == 0) {
						date = true;
						chequeDetail.seteMIRefNo(fsd.getInstNumber());
						if (fsd.getRepayAmount().subtract(fsd.getTDSAmount())
								.compareTo(chequeDetail.getAmount()) != 0) {
							// {0} Should be equal To {1}
							String[] valueParm = new String[2];
							valueParm[0] = new SimpleDateFormat("yyyy-MM-dd").format(fsd.getSchDate());
							valueParm[1] = String.valueOf(fsd.getRepayAmount() + "INR");
							schdData.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetail("30570", valueParm)));
							return;

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
					schdData.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetail("30570", valueParm)));
					return;

				}
			}

		}

		return;
	}

	@Override
	public WSReturnStatus createChequeDetails(FinanceDetail financeDetail) throws ServiceException {
		logger.debug(Literal.ENTERING);
		WSReturnStatus returnStatus = new WSReturnStatus();
		List<ErrorDetail> errorDetails;
		String tableType = "_Temp";
		try {
			FinanceMain fm = null;
			String finReference = financeDetail.getFinReference();
			ChequeHeader chequeHeader = financeDetail.getChequeHeader();

			FinScheduleData schdData = financeDetail.getFinScheduleData();
			if (finReference == null) {
				String[] valueParm = new String[1];
				valueParm[0] = "FinReference";
				return returnStatus = APIErrorHandlerService.getFailedStatus("90502", valueParm);
			} else {
				fm = financeMainDAO.getFinanceMainByRef(finReference, tableType, false);
				if (fm == null || !fm.isFinIsActive() || StringUtils.isNotEmpty(fm.getRcdMaintainSts())) {
					String[] valueParm = new String[1];
					valueParm[0] = finReference;
					return returnStatus = APIErrorHandlerService.getFailedStatus("90201", valueParm);
				} else {
					schdData.setFinanceMain(fm);
				}

				long finID = fm.getFinID();

				returnStatus = isWriteoffLoan(finID);
				if (returnStatus != null) {
					return returnStatus;
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

			List<FinanceScheduleDetail> schedules = null;
			schedules = financeScheduleDetailDAO.getFinScheduleDetails(fm.getFinID(), tableType, false);
			schdData.setFinanceScheduleDetails(schedules);
			validateChequeDetails(financeDetail);
			if (CollectionUtils.isNotEmpty(schdData.getErrorDetails())) {
				for (ErrorDetail errorDetail : schdData.getErrorDetails()) {
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

			String finReference = financeDetail.getFinReference();
			ChequeHeader chequeHeader = financeDetail.getChequeHeader();

			// for logging purpose
			APIErrorHandlerService.logReference(finReference);
			Long finID = financeMainDAO.getActiveFinID(finReference, TableType.MAIN_TAB);

			if (finID == null) {
				String[] valueParm = new String[1];
				valueParm[0] = "finReference";
				return returnStatus = APIErrorHandlerService.getFailedStatus("90201", valueParm);
			}

			financeDetail.setFinID(finID);
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

			List<FinanceScheduleDetail> schedules = null;
			FinanceMain fm = financeMainDAO.getFinanceMainByRef(finReference, "", false);
			FinScheduleData schdData = financeDetail.getFinScheduleData();
			schdData.setFinanceMain(fm);
			schedules = financeScheduleDetailDAO.getFinScheduleDetails(fm.getFinID(), "", false);
			schdData.setFinanceScheduleDetails(schedules);

			validateChequeDetails(financeDetail);
			if (CollectionUtils.isNotEmpty(schdData.getErrorDetails())) {
				for (ErrorDetail errorDetail : schdData.getErrorDetails()) {
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
			String finReference = financeDetail.getFinReference();
			ChequeHeader chequeHeader = financeDetail.getChequeHeader();

			Long finID = financeMainDAO.getFinID(finReference);

			financeDetail.setFinID(finID);

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

			List<FinanceScheduleDetail> schedules = null;
			FinanceMain fm = financeMainDAO.getFinanceMainById(finID, tableType, false);
			FinScheduleData schdData = financeDetail.getFinScheduleData();
			schdData.setFinanceMain(fm);
			schedules = financeScheduleDetailDAO.getFinScheduleDetails(finID, tableType, false);
			schdData.setFinanceScheduleDetails(schedules);

			validateChequeDetails(financeDetail);
			if (CollectionUtils.isNotEmpty(schdData.getErrorDetails())) {
				for (ErrorDetail errorDetail : schdData.getErrorDetails()) {
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
		detail.setJointAccountDetailList(null);
		detail.setGurantorsDetailList(null);
		detail.setCollateralAssignmentList(null);
		detail.setReturnDataSetList(null);
	}

	/**
	 * Set Default date formats for calculation purpose.
	 * 
	 * @param fsi
	 */
	private void setDefaultDateFormats(FinServiceInstruction fsi) {
		if (fsi.getFromDate() != null) {
			fsi.setFromDate(
					DateUtility.getDBDate(DateUtility.format(fsi.getFromDate(), PennantConstants.DBDateFormat)));
		}

		if (fsi.getToDate() != null) {
			fsi.setToDate(DateUtility.getDBDate(DateUtility.format(fsi.getToDate(), PennantConstants.DBDateFormat)));
		}
		if (fsi.getRecalFromDate() != null) {
			fsi.setRecalFromDate(
					DateUtility.getDBDate(DateUtility.format(fsi.getRecalFromDate(), PennantConstants.DBDateFormat)));
		}
		if (fsi.getRecalToDate() != null) {
			fsi.setRecalToDate(
					DateUtility.getDBDate(DateUtility.format(fsi.getRecalToDate(), PennantConstants.DBDateFormat)));
		}
		if (fsi.getGrcPeriodEndDate() != null) {
			fsi.setGrcPeriodEndDate(DateUtility
					.getDBDate(DateUtility.format(fsi.getGrcPeriodEndDate(), PennantConstants.DBDateFormat)));
		}
		if (fsi.getNextGrcRepayDate() != null) {
			fsi.setNextGrcRepayDate(DateUtility
					.getDBDate(DateUtility.format(fsi.getNextGrcRepayDate(), PennantConstants.DBDateFormat)));
		}
		if (fsi.getNextRepayDate() != null) {
			fsi.setNextRepayDate(
					DateUtility.getDBDate(DateUtility.format(fsi.getNextRepayDate(), PennantConstants.DBDateFormat)));
		}
	}

	private void setDefaultForReferenceFields(FinServiceInstruction fsi) {
		if (StringUtils.isBlank(fsi.getBaseRate())) {
			fsi.setBaseRate(StringUtils.trimToNull(fsi.getBaseRate()));
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

		receiptData = receiptCalculator.recalAutoAllocation(receiptData, false);
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
		}

		Long finID = financeMainDAO.getActiveFinID(finReference);
		if (finID == null) {
			String[] valueParm = new String[1];
			valueParm[0] = finReference;
			response.setReturnStatus(APIErrorHandlerService.getFailedStatus("90201", valueParm));
			return response;
		}

		try {
			response = chequeHeaderDAO.getChequeHeaderByRef(finID, "_View");
			if (response == null) {
				response = new ChequeHeader();
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
	public WSReturnStatus cancelDisbursementInstructions(FinServiceInstruction fsi) throws ServiceException {
		logger.debug(Literal.ENTERING);

		WSReturnStatus response = new WSReturnStatus();

		// for logging purpose
		String finReference = fsi.getFinReference();
		String eventCode = AccountingEvent.ADDDBSN;
		APIErrorHandlerService.logReference(finReference);

		// set Default date formats
		setDefaultDateFormats(fsi);

		Long finID = financeMainDAO.getFinID(finReference, TableType.MAIN_TAB);
		if (finID == null) {
			String[] valueParm = new String[1];
			valueParm[0] = finReference;
			return APIErrorHandlerService.getFailedStatus("90248", valueParm);
		}

		int count = validateBlockedFinances(finID);
		if (count > 0) {
			String valueParm[] = new String[2];
			valueParm[0] = "Disbursement";
			valueParm[1] = "FinReference: " + finReference;
			return APIErrorHandlerService.getFailedStatus("90204", valueParm);
		}

		long paymentId = fsi.getPaymentId();
		if (paymentId == Long.MIN_VALUE && paymentId <= 0) {
			String valueParm[] = new String[1];
			valueParm[0] = "PaymentId";
			return APIErrorHandlerService.getFailedStatus("90502", valueParm);
		}

		int paymentIdCount = finAdvancePaymentsService.getCountByPaymentId(finID, paymentId);
		if (paymentIdCount <= 0) {
			String[] valueParam = new String[1];
			valueParam[0] = "PaymentId";
			return APIErrorHandlerService.getFailedStatus("90405", valueParam);
		}

		FinAdvancePayments finAdvancePayments = new FinAdvancePayments();
		finAdvancePayments.setPaymentId(paymentId);
		FinAdvancePayments finAdv = finAdvancePaymentsDAO.getFinAdvancePaymentsById(finAdvancePayments, "");

		if (DisbursementConstants.STATUS_PAID.equals(finAdv.getStatus())) {
			List<FinAdvancePayments> disbursementDetailsList = fsi.getDisbursementDetails();
			if (CollectionUtils.isEmpty(disbursementDetailsList)) {
				String valueParm[] = new String[1];
				valueParm[0] = "DisbursementDetails";
				return APIErrorHandlerService.getFailedStatus("90502", valueParm);
			}

			fsi.setFinID(finID);
			FinanceDetail fd = finServiceInstController.getFinanceDetails(fsi, eventCode);
			fd.setAdvancePaymentsList(disbursementDetailsList);
			AuditDetail auditDetail = addDisbursementService.doCancelDisbValidations(fd);

			if (CollectionUtils.isNotEmpty(auditDetail.getErrorDetails())) {
				for (ErrorDetail errorDetail : auditDetail.getErrorDetails()) {
					return APIErrorHandlerService.getFailedStatus(errorDetail.getCode(), errorDetail.getError());
				}
			}

			fd.getAdvancePaymentsList().add(finAdv);

			fd.getFinScheduleData().getFinanceMain().setFinID(finID);
			response = finServiceInstController.doCancelDisbursementInstructions(fd);
		} else {
			String[] valueParam = new String[2];
			valueParam[0] = "Cancel Disbursement Instructions";
			valueParam[1] = finAdv.getStatus();
			return APIErrorHandlerService.getFailedStatus("90329", valueParam);
		}

		logger.debug(Literal.LEAVING);
		return response;
	}

	@Override
	public WSReturnStatus subventionKnockOff(SubventionHeader header) throws ServiceException {
		logger.debug(Literal.ENTERING);
		String entityCode = header.getEntityCode();
		String bRef = header.getBatchRef();

		if (StringUtils.isEmpty(bRef)) {
			String[] valueParm = new String[1];
			valueParm[0] = "Batch Reference";
			return APIErrorHandlerService.getFailedStatus("90502", valueParm);
		}

		if (StringUtils.isEmpty(entityCode)) {
			String[] valueParm = new String[1];
			valueParm[0] = "Entity Code";
			return APIErrorHandlerService.getFailedStatus("90502", valueParm);
		}

		if (entityDAO.getEntityCount(entityCode) == 0) {
			String[] valueParm = new String[4];
			valueParm[0] = "Entity";
			valueParm[1] = "Code";
			valueParm[2] = "is Invalid";
			valueParm[3] = entityCode;
			return APIErrorHandlerService.getFailedStatus("30550", valueParm);
		}

		if (subventionKnockOffService.isFileExists(bRef)) {
			String[] valueParm = new String[4];
			valueParm[0] = "Batch";
			valueParm[1] = "Reference";
			valueParm[2] = "already";
			valueParm[3] = "exists";
			return APIErrorHandlerService.getFailedStatus("30550", valueParm);
		}

		for (Subvention sub : header.getSubventions()) {
			String finReference = sub.getFinReference();
			String finType = sub.getFinType();
			String referenceCode = sub.getReferenceCode();
			BigDecimal amount = sub.getAmount();
			Date vdate = sub.getValueDate();
			Date pdate = sub.getPostDate();

			if (StringUtils.isEmpty(finReference)) {
				String[] valueParm = new String[1];
				valueParm[0] = "FinReference";
				return APIErrorHandlerService.getFailedStatus("90502", valueParm);
			}

			Long finID = financeMainDAO.getFinID(finReference, TableType.MAIN_TAB);
			if (finID == null) {
				String[] valueParm = new String[4];
				valueParm[0] = "Fin";
				valueParm[1] = "Reference";
				valueParm[2] = "not";
				valueParm[3] = "exists";
				return APIErrorHandlerService.getFailedStatus("30550", valueParm);
			}

			sub.setFinID(finID);

			if (StringUtils.isEmpty(finType)) {
				String[] valueParm = new String[1];
				valueParm[0] = "Fin type";
				return APIErrorHandlerService.getFailedStatus("90502", valueParm);
			}

			if (StringUtils.isEmpty(referenceCode)) {
				String[] valueParm = new String[1];
				valueParm[0] = "Reference Code";
				return APIErrorHandlerService.getFailedStatus("90502", valueParm);
			}

			if (amount == null || amount.compareTo(BigDecimal.ZERO) == 0) {
				String[] valueParm = new String[1];
				valueParm[0] = "Amount";
				return APIErrorHandlerService.getFailedStatus("90502", valueParm);
			}

			if (pdate == null) {
				String[] valueParm = new String[1];
				valueParm[0] = "Post Date";
				return APIErrorHandlerService.getFailedStatus("90502", valueParm);
			}
			if (vdate == null) {
				String[] valueParm = new String[1];
				valueParm[0] = "Value Date";
				return APIErrorHandlerService.getFailedStatus("90502", valueParm);
			}
		}

		header.setId(subventionUploadDAO.saveSubventionHeader(bRef, entityCode));

		header.setTotalRecords(subventionUploadDAO.saveSubvention(header.getSubventions(), header.getId()));
		List<Subvention> subventions = subventionUploadDAO.getSubventionDetails(header.getId());
		header.setSubventions(subventions);

		try {
			subventionKnockOffService.process(header);
			for (Subvention sub : subventions) {
				if (!sub.getErrorDetails().isEmpty()) {
					ErrorDetail errorDetail = sub.getErrorDetails().get(0);
					return APIErrorHandlerService.getFailedStatus(errorDetail.getCode(), errorDetail.getError());
				}
			}
		} catch (Exception e) {
			logger.debug(Literal.EXCEPTION, e);
			APIErrorHandlerService.logUnhandledException(e);
			return APIErrorHandlerService.getFailedStatus();
		}
		logger.debug(Literal.LEAVING);
		return APIErrorHandlerService.getSuccessStatus();
	}

	@Override
	public FinanceDetail partCancellation(FinServiceInstruction fsi) {
		logger.debug(Literal.ENTERING);

		FinanceDetail fd = null;

		String eventCode = AccountingEvent.PART_CANCELATION;

		validationUtility.validate(fsi, PartCancellationGroup.class);

		fd = validateInstruction(fsi, eventCode);

		if (fd != null) {
			return fd;
		}

		try {

			fd = finServiceInstController.getFinanceDetails(fsi, eventCode);

			// validate service instruction data
			AuditDetail auditDetail = partCancellationService.validateRequest(fsi, fd);
			if (auditDetail.getErrorDetails() != null) {
				for (ErrorDetail ed : auditDetail.getErrorDetails()) {
					fd = new FinanceDetail();
					doEmptyResponseObject(fd);
					fd.setReturnStatus(APIErrorHandlerService.getFailedStatus(ed.getCode(), ed.getError()));
					return fd;
				}
			}

			// call part cancellation service
			fd = partCancellationService.doPartCancellation(fsi, fd);

			if (fd.getFinScheduleData().getErrorDetails() != null) {
				for (ErrorDetail errorDetail : fd.getFinScheduleData().getErrorDetails()) {
					FinanceDetail response = new FinanceDetail();
					doEmptyResponseObject(response);
					response.setReturnStatus(
							APIErrorHandlerService.getFailedStatus(errorDetail.getCode(), errorDetail.getError()));
					return response;
				}
			}

			// Get the response
			fd = finServiceInstController.getResponse(fd, fsi);

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
		return fd;
	}

	private WSReturnStatus isWriteoffLoan(long finID) {
		boolean writeoffLoan = financeWriteoffDAO.isWriteoffLoan(finID, "");
		if (writeoffLoan) {
			String[] valueParam = new String[1];
			valueParam[0] = "";
			return APIErrorHandlerService.getFailedStatus("FWF001", valueParam);
		}
		return null;
	}

	@Override
	public List<CovenantStatus> getCovenantDocumentStatus(String finReference) throws ServiceException {
		logger.debug(Literal.ENTERING);
		CovenantStatus response = new CovenantStatus();
		List<CovenantStatus> covenantStatus = new ArrayList<>();

		try {
			if (finReference == null) {
				String[] valueParm = new String[1];
				valueParm[0] = "FinReference";
				response.setReturnStatus(APIErrorHandlerService.getFailedStatus("90502", valueParm));
				covenantStatus.add(response);
				return covenantStatus;
			}

			Long finID = financeMainDAO.getActiveFinID(finReference);
			if (finID == null) {
				String[] valueParm = new String[1];
				valueParm[0] = finReference;
				response.setReturnStatus(APIErrorHandlerService.getFailedStatus("90201", valueParm));
				covenantStatus.add(response);
				return covenantStatus;
			}

			List<Covenant> covenants = covenantsDAO.getCovenants(finReference, "LOAN", TableType.VIEW);
			if (CollectionUtils.isEmpty(covenants)) {
				String valueParm[] = new String[4];
				valueParm[0] = "Covenants Are Not";
				valueParm[1] = "Avaialable with the Finreference: " + finReference;
				valueParm[2] = "";
				valueParm[3] = "";
				response.setReturnStatus(APIErrorHandlerService.getFailedStatus("30550", valueParm));
				covenantStatus.add(response);
				return covenantStatus;
			}

			for (Covenant covenant : covenants) {
				response = new CovenantStatus();
				List<CovenantDocument> cd = covenantsDAO.getCovenantDocuments(covenant.getId(), TableType.VIEW);
				if (CollectionUtils.isNotEmpty(cd)) {
					response.setFinreference(covenant.getKeyReference());
					response.setCovenantTypeId(covenant.getCovenantTypeId());
					response.setCovenantType(covenant.getCovenantTypeCode());
					response.setCategory(covenant.getCategory());
					response.setDocStauts("RECEIVED");
					covenantStatus.add(response);
				} else {
					response = new CovenantStatus();
					response.setFinreference(covenant.getKeyReference());
					response.setCovenantTypeId(covenant.getCovenantTypeId());
					response.setCovenantType(covenant.getCovenantTypeCode());
					response.setCategory(covenant.getCategory());
					response.setDocStauts("PENDING");
					covenantStatus.add(response);
				}
			}
		} catch (Exception e) {
			logger.error(Literal.EXCEPTION + e);
			response.setReturnStatus(APIErrorHandlerService.getFailedStatus());
			covenantStatus.add(response);
			return covenantStatus;
		}
		logger.debug(Literal.LEAVING);
		response.setReturnStatus(APIErrorHandlerService.getSuccessStatus());
		return covenantStatus;
	}

	@Override
	public AgreementData getCovenantAggrement(AgreementRequest agreementRequest) throws ServiceException {
		logger.debug(Literal.ENTERING);
		AgreementData aggrementData = new AgreementData();

		String finReference = agreementRequest.getFinReference();
		if (StringUtils.isBlank(finReference)) {
			if (finReference == null) {
				String[] valueParm = new String[1];
				valueParm[0] = "FinReference";
				aggrementData.setReturnStatus(APIErrorHandlerService.getFailedStatus("90502", valueParm));
				return aggrementData;
			}
		}

		Long finID = financeMainDAO.getActiveFinID(finReference);

		if (finID == null) {
			String[] valueParm = new String[1];
			valueParm[0] = finReference;
			aggrementData.setReturnStatus(APIErrorHandlerService.getFailedStatus("90201", valueParm));
			return aggrementData;
		}

		if (StringUtils.isBlank(agreementRequest.getAgreementType())) {
			String[] valueParm = new String[1];
			valueParm[0] = "Aggrement Type";
			aggrementData.setReturnStatus(APIErrorHandlerService.getFailedStatus("90502", valueParm));
			return aggrementData;
		}

		List<CovenantAggrement> cvntAggrement = interestCertificateService.getCovenantReportStatus(finReference);
		if (CollectionUtils.isEmpty(cvntAggrement)) {
			String valueParm[] = new String[4];
			valueParm[0] = "Covenants Are Not";
			valueParm[1] = "Avaialable with the Finreference: " + finReference;
			valueParm[2] = "";
			valueParm[3] = "";
			aggrementData.setReturnStatus(APIErrorHandlerService.getFailedStatus("30550", valueParm));
			return aggrementData;
		}
		CovenantAggrement covenantAgreement = new CovenantAggrement();
		for (CovenantAggrement covenantAggrement : cvntAggrement) {
			if (covenantAggrement.getReceivableDate() == null) {
				covenantAggrement.setReceivableDate("");
			}
			if (covenantAggrement.getDocumentReceivedDate() == null) {
				covenantAggrement.setDocumentReceivedDate("");
			}
		}
		if (CollectionUtils.isNotEmpty(cvntAggrement)) {
			CovenantAggrement ca = cvntAggrement.get(0);
			String combinedString = null;
			if (ca.getCustFlatNbr() == null) {
				ca.setCustFlatNbr("");
			}
			if (ca.getCustPOBox() == null) {
				ca.setCustPOBox("");
			}
			combinedString = ca.getCustAddrHnbr() + ca.getCustFlatNbr() + "\n" + ca.getCustAddrStreet() + " "
					+ ca.getCustAddrCity() + "\n" + ca.getCustAddrProvince() + "\n" + ca.getCustAddrCountry() + "\n"
					+ ca.getCustPOBox();
			covenantAgreement.setCustAddrHnbr(combinedString);
			covenantAgreement.setFinReference(ca.getFinReference());
			covenantAgreement.setCustshrtname(ca.getCustshrtname());
			aggrementData.setFinReference(covenantAgreement.getFinReference());
		}
		Date appdate = SysParamUtil.getAppDate();
		covenantAgreement.setAppDate(DateUtil.format(appdate, DateFormat.LONG_DATE));
		covenantAgreement.setCovenantAggrementList(cvntAggrement);

		String agreement = "LOD.docx";
		String path = PathUtil.getPath(PathUtil.CovenantStatusReport);

		byte[] doc = agreementGeneration.getCovenantAgreementGeneration(covenantAgreement, path, agreement);

		aggrementData.setDocContent(doc);
		if (doc != null) {
			aggrementData.setReturnStatus(APIErrorHandlerService.getSuccessStatus());
		} else {
			aggrementData.setReturnStatus(APIErrorHandlerService.getFailedStatus());
		}
		logger.debug(Literal.LEAVING);
		return aggrementData;
	}

	@Override
	public WSReturnStatus processFeeWaiver(FeeWaiverHeader feeWaiverHeader) throws ServiceException {
		FeeWaiverHeader feeWaiver = new FeeWaiverHeader();
		List<FeeWaiverDetail> actaulfeeWaiverDetails = new ArrayList<>();

		String finReference = feeWaiverHeader.getFinReference();
		if (StringUtils.isBlank(finReference)) {
			String[] valueParm = new String[1];
			valueParm[0] = "FinReference";
			return APIErrorHandlerService.getFailedStatus("90502", valueParm);
		}

		FinanceMain fm = financeMainDAO.getFinanceMain(finReference);
		if (fm == null || !fm.isFinIsActive()) {
			String[] valueParm = new String[1];
			valueParm[0] = finReference;
			return APIErrorHandlerService.getFailedStatus("90201", valueParm);
		}

		feeWaiverHeader.setFinID(fm.getFinID());
		Date valueDate = feeWaiverHeader.getValueDate();
		if (valueDate == null) {
			String[] valueParm = new String[1];
			valueParm[0] = "ValueDate";
			return APIErrorHandlerService.getFailedStatus("90502", valueParm);
		}

		int eodProgressCount = financeDetailService.getProgressCountByCust(fm.getCustID());

		// If Customer Exists in EOD Processing, Not allowed to Maintenance till completion
		if (eodProgressCount > 0) {
			String[] valueParm = new String[1];
			return APIErrorHandlerService.getFailedStatus("60203", valueParm);
		}
		// validating with the rcdmaintainsts
		String rcdMntnSts = financeDetailService.getFinanceMainByRcdMaintenance(fm.getFinID(), "_View");

		if (StringUtils.isNotEmpty(rcdMntnSts) && !FinServiceEvent.FEEWAIVERS.equals(rcdMntnSts)) {
			String valueParm[] = new String[4];
			valueParm[0] = "Finance is";
			valueParm[1] = "Progress";
			valueParm[2] = "" + rcdMntnSts;
			valueParm[3] = "";
			return APIErrorHandlerService.getFailedStatus("30550", valueParm);
		}

		// Validating the records in temp table if Exists showing the Validation
		FeeWaiverHeader fwh = feeWaiverHeaderService.getFeeWaiverByFinRef(feeWaiverHeader);
		if (fwh != null) {
			String valueParm[] = new String[4];
			valueParm[0] = "Fee Waiver";
			valueParm[1] = "in";
			valueParm[2] = "Processing";
			valueParm[3] = "";
			return APIErrorHandlerService.getFailedStatus("30550", valueParm);
		}

		// get fee waiver details from manual advise and finoddetails to prepare the list.
		feeWaiver.setNewRecord(true);
		feeWaiver.setFinID(fm.getFinID());
		feeWaiver.setFinReference(finReference);
		feeWaiver = feeWaiverHeaderService.getFeeWaiverByFinRef(feeWaiver);

		if (!feeWaiver.isAlwtoProceed()) {
			String valueParm[] = new String[4];
			valueParm[0] = "Receipt is";
			valueParm[1] = "in";
			valueParm[2] = "Maintainance: ";
			valueParm[3] = finReference;
			return APIErrorHandlerService.getFailedStatus("30550", valueParm);
		}

		for (FeeWaiverDetail fwd : feeWaiver.getFeeWaiverDetails()) {
			if (StringUtils.isBlank(fwd.getFeeTypeCode())) {
				String[] valueParm = new String[1];
				valueParm[0] = "FeeType Code";
				return APIErrorHandlerService.getFailedStatus("90502", valueParm);
			}
			if (fwd.getCurrWaiverAmount() == null || fwd.getBalanceAmount().compareTo(BigDecimal.ZERO) < 0) {
				String[] valueParm = new String[1];
				valueParm[0] = "WaiverAmount";
				return APIErrorHandlerService.getFailedStatus("90502", valueParm);
			}

			if (fwd.getBalanceAmount() != null && fwd.getBalanceAmount().compareTo(BigDecimal.ZERO) > 0) {
				actaulfeeWaiverDetails.add(fwd);
			}
		}
		// Setting the actual feewaiver values to the feewaiver
		feeWaiver.setFeeWaiverDetails(actaulfeeWaiverDetails);

		int actualFeeTypeCode = feeWaiver.getFeeWaiverDetails().size();
		int feeTypeCode = feeWaiverHeader.getFeeWaiverDetails().size();
		if (actualFeeTypeCode != feeTypeCode) {
			String valueParm[] = new String[4];
			valueParm[0] = "FeeType Codes";
			valueParm[1] = "Should";
			valueParm[2] = "be Matched With Existing: ";
			valueParm[3] = finReference;
			return APIErrorHandlerService.getFailedStatus("30550", valueParm);
		}

		List<FeeWaiverDetail> feeWaiverDetails = feeWaiverHeader.getFeeWaiverDetails();

		// Validating The Waiver amount with the Balance

		BigDecimal totCurWaivedAmt = BigDecimal.ZERO;
		for (FeeWaiverDetail fwd : feeWaiverDetails) {
			for (FeeWaiverDetail compareWithBalance : actaulfeeWaiverDetails) {
				if (StringUtils.equals(fwd.getFeeTypeCode(), compareWithBalance.getFeeTypeCode())) {
					BigDecimal balanceAmount = compareWithBalance.getReceivableAmount();
					if (balanceAmount.compareTo(fwd.getCurrWaiverAmount()) == -1) {
						String valueParm[] = new String[4];
						valueParm[0] = "CurrentWaived Amount";
						valueParm[1] = "Should be";
						valueParm[2] = "less than";
						valueParm[3] = "or Equal to BalanceAmount with FeeTypeCode: " + fwd.getFeeTypeCode();
						return APIErrorHandlerService.getFailedStatus("30550", valueParm);
					}
				}
			}

			if (fwd.getCurrWaiverAmount().compareTo(BigDecimal.ZERO) < 0) {
				String[] valueParm = new String[2];
				valueParm[0] = "Current Waiver amount";
				valueParm[1] = "0";
				return APIErrorHandlerService.getFailedStatus("91121", valueParm);
			}

			if (fwd.getCurrWaiverAmount().compareTo(BigDecimal.ZERO) > 0) {
				totCurWaivedAmt = totCurWaivedAmt.add(fwd.getCurrWaiverAmount());
			}

			if (totCurWaivedAmt.compareTo(BigDecimal.ZERO) <= 0) {
				String[] valueParm = new String[2];
				valueParm[0] = "Total current Waived amount";
				valueParm[1] = "0";
				return APIErrorHandlerService.getFailedStatus("91121", valueParm);
			}
		}
		boolean feeCode = false;
		for (FeeWaiverDetail feeWaiverDetail : feeWaiverDetails) {
			for (FeeWaiverDetail compareWithBalance : actaulfeeWaiverDetails) {
				if (StringUtils.equals(feeWaiverDetail.getFeeTypeCode(), compareWithBalance.getFeeTypeCode())) {
					feeCode = true;
					break;
				}
			}
		}
		if (!feeCode) {
			String valueParm[] = new String[4];
			valueParm[0] = "FeeTypeCode";
			valueParm[1] = "Should be";
			valueParm[2] = "Valid";
			valueParm[3] = "";
			return APIErrorHandlerService.getFailedStatus("30550", valueParm);
		}

		return finServiceInstController.processFeeWaivers(feeWaiverHeader, feeWaiver);
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

	@Override
	public FinanceDetail nonLanReceipt(FinServiceInstruction finServiceInstruction) throws ServiceException {
		String moduleDefiner = FinServiceEvent.SCHDRPY;
		FinanceDetail financeDetail = nonLanReceiptTransaction(finServiceInstruction, moduleDefiner);
		return financeDetail;
	}

	private FinanceDetail nonLanReceiptTransaction(FinServiceInstruction fsi, String moduleDefiner) {
		logger.info(Literal.ENTERING);

		String eventCode = null;
		if (!fsi.isReceiptUpload()) {
			validationUtility.validate(fsi, NonLanReceiptGroup.class);
		}

		// Method for validate instruction details
		FinanceDetail financeDetail = new FinanceDetail();
		FinScheduleData finScheduleData = financeDetail.getFinScheduleData();
		finScheduleData.setFinServiceInstruction(fsi);
		financeDetail = validateInstructions(financeDetail, moduleDefiner, eventCode);

		if (fsi.getValueDate() == null) {
			fsi.setValueDate(fsi.getReceivedDate());
		}

		FinReceiptData receiptData = nonLanReceiptService.doReceiptValidations(financeDetail, moduleDefiner);
		financeDetail = receiptData.getFinanceDetail();
		finScheduleData = financeDetail.getFinScheduleData();

		if (finScheduleData.getErrorDetails() != null && !finScheduleData.getErrorDetails().isEmpty()) {
			logger.debug("Leaving - doReceiptValidations Error");
			return setReturnStatus(financeDetail);
		}

		receiptData = nonLanReceiptService.setReceiptData(receiptData);
		if (finScheduleData.getErrorDetails() != null && !finScheduleData.getErrorDetails().isEmpty()) {
			return setReturnStatus(financeDetail);
		}

		try {
			financeDetail = finServiceInstController.doProcessNonLanReceipt(receiptData, eventCode);
		} catch (Exception e) {
			e.printStackTrace();
			finScheduleData = nonLanReceiptService.setErrorToFSD(finScheduleData, "90502", e.getMessage());
		}
		if (finScheduleData.getErrorDetails() != null && !finScheduleData.getErrorDetails().isEmpty()) {
			return setReturnStatus(financeDetail);
		}
		if (financeDetail.getFinScheduleData().getErrorDetails() != null
				&& !financeDetail.getFinScheduleData().getErrorDetails().isEmpty()) {
			financeDetail = setReturnStatus(financeDetail);
		}

		logger.info(Literal.LEAVING);
		return financeDetail;
	}

	public NonLanReceiptService getNonLanReceiptService() {
		return nonLanReceiptService;
	}

	@Autowired
	public void setNonLanReceiptService(NonLanReceiptService nonLanReceiptService) {
		this.nonLanReceiptService = nonLanReceiptService;
	}

	@Autowired
	public void setFinanceWriteoffDAO(FinanceWriteoffDAO financeWriteoffDAO) {
		this.financeWriteoffDAO = financeWriteoffDAO;
	}

	@Autowired
	public void setSubventionUploadDAO(SubventionUploadDAO subventionUploadDAO) {
		this.subventionUploadDAO = subventionUploadDAO;
	}

	@Autowired
	public void setSubventionKnockOffService(SubventionKnockOffService subventionKnockOffService) {
		this.subventionKnockOffService = subventionKnockOffService;
	}

	@Autowired
	public void setEntityDAO(EntityDAO entityDAO) {
		this.entityDAO = entityDAO;
	}

	@Autowired
	public void setCovenantsDAO(CovenantsDAO covenantsDAO) {
		this.covenantsDAO = covenantsDAO;
	}

	@Autowired
	public void setInterestCertificateService(InterestCertificateService interestCertificateService) {
		this.interestCertificateService = interestCertificateService;
	}

	@Autowired
	public void setAgreementGeneration(AgreementGeneration agreementGeneration) {
		this.agreementGeneration = agreementGeneration;
	}

	@Autowired
	public void setFinanceDetailService(FinanceDetailService financeDetailService) {
		this.financeDetailService = financeDetailService;
	}

	@Autowired
	public void setFeeWaiverHeaderService(FeeWaiverHeaderService feeWaiverHeaderService) {
		this.feeWaiverHeaderService = feeWaiverHeaderService;
	}

	@Autowired
	public void setRestructureService(RestructureService restructureService) {
		this.restructureService = restructureService;
	}
}
