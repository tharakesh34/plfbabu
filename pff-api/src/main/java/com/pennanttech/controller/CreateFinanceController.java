package com.pennanttech.controller;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.cxf.phase.PhaseInterceptorChain;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import com.pennant.app.util.APIHeader;
import com.pennant.app.util.DateUtility;
import com.pennant.app.util.ErrorUtil;
import com.pennant.app.util.FeeScheduleCalculator;
import com.pennant.app.util.ReferenceGenerator;
import com.pennant.app.util.ScheduleCalculator;
import com.pennant.app.util.ScheduleGenerator;
import com.pennant.app.util.SessionUserDetails;
import com.pennant.backend.dao.finance.FinanceMainDAO;
import com.pennant.backend.dao.finance.FinanceScheduleDetailDAO;
import com.pennant.backend.dao.financemanagement.FinanceStepDetailDAO;
import com.pennant.backend.dao.solutionfactory.StepPolicyDetailDAO;
import com.pennant.backend.dao.solutionfactory.StepPolicyHeaderDAO;
import com.pennant.backend.model.ErrorDetails;
import com.pennant.backend.model.LoggedInUser;
import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.model.bmtmasters.BankBranch;
import com.pennant.backend.model.collateral.CollateralAssignment;
import com.pennant.backend.model.collateral.CollateralSetup;
import com.pennant.backend.model.customermasters.Customer;
import com.pennant.backend.model.customermasters.CustomerDetails;
import com.pennant.backend.model.documentdetails.DocumentDetails;
import com.pennant.backend.model.finance.FinAdvancePayments;
import com.pennant.backend.model.finance.FinFeeDetail;
import com.pennant.backend.model.finance.FinScheduleData;
import com.pennant.backend.model.finance.FinanceDetail;
import com.pennant.backend.model.finance.FinanceDisbursement;
import com.pennant.backend.model.finance.FinanceMain;
import com.pennant.backend.model.finance.FinanceScheduleDetail;
import com.pennant.backend.model.finance.FinanceStepPolicyDetail;
import com.pennant.backend.model.finance.FinanceSummary;
import com.pennant.backend.model.finance.GuarantorDetail;
import com.pennant.backend.model.finance.JointAccountDetail;
import com.pennant.backend.model.financemanagement.FinFlagsDetail;
import com.pennant.backend.model.mandate.Mandate;
import com.pennant.backend.model.solutionfactory.StepPolicyDetail;
import com.pennant.backend.model.solutionfactory.StepPolicyHeader;
import com.pennant.backend.service.bmtmasters.BankBranchService;
import com.pennant.backend.service.collateral.CollateralSetupService;
import com.pennant.backend.service.customermasters.CustomerDetailsService;
import com.pennant.backend.service.fees.FeeDetailService;
import com.pennant.backend.service.finance.FinAdvancePaymentsService;
import com.pennant.backend.service.finance.FinFeeDetailService;
import com.pennant.backend.service.finance.FinanceDetailService;
import com.pennant.backend.service.finance.FinanceMainService;
import com.pennant.backend.service.finance.JointAccountDetailService;
import com.pennant.backend.util.DisbursementConstants;
import com.pennant.backend.util.FinanceConstants;
import com.pennant.backend.util.PennantApplicationUtil;
import com.pennant.backend.util.PennantConstants;
import com.pennanttech.util.APIConstants;
import com.pennanttech.ws.model.financetype.FinInquiryDetail;
import com.pennanttech.ws.model.financetype.FinanceInquiry;
import com.pennanttech.ws.service.APIErrorHandlerService;

public class CreateFinanceController extends SummaryDetailService {

	private final static Logger			logger	= Logger.getLogger(CreateFinanceController.class);

	private FinanceScheduleDetailDAO	financeScheduleDetailDAO;
	private CustomerDetailsService		customerDetailsService;
	private FinanceDetailService		financeDetailService;
	private FinanceStepDetailDAO		financeStepDetailDAO;
	private StepPolicyDetailDAO			stepPolicyDetailDAO;
	private StepPolicyHeaderDAO			stepPolicyHeaderDAO;
	private BankBranchService			bankBranchService;
	private FinanceMainDAO				financeMainDAO;
	private FeeDetailService			feeDetailService;
	private FinFeeDetailService			finFeeDetailService;
	private CollateralSetupService		collateralSetupService;
	private FinanceMainService			financeMainService;
	private JointAccountDetailService	jointAccountDetailService;
	private FinAdvancePaymentsService	finAdvancePaymentsService;

	/**
	 * Method for process create finance request
	 * 
	 * @param financeDetail
	 * @return
	 */
	public FinanceDetail doCreateFinance(FinanceDetail financeDetail, boolean loanWithWIF) {
		logger.debug("Enteing");

		String finReference = null;

		try {
			// financeMain details
			FinScheduleData finScheduleData = financeDetail.getFinScheduleData();
			FinanceMain financeMain = finScheduleData.getFinanceMain();
			financeMain.setFinType(finScheduleData.getFinanceType().getFinType());
			if (StringUtils.isBlank(financeMain.getFinReference())) {
				finReference = String.valueOf(String.valueOf(ReferenceGenerator.generateNewFinRef(false, financeMain)));
			} else {
				finReference = financeMain.getFinReference();

			}
			// user language
			LoggedInUser userDetails = SessionUserDetails.getUserDetails(SessionUserDetails.getLogiedInUser());
			financeMain.setUserDetails(userDetails);

			financeMain.setFinReference(finReference);
			finScheduleData.setFinReference(financeMain.getFinReference());

			financeMain.setRecordType(PennantConstants.RECORD_TYPE_NEW);
			financeMain.setWorkflowId(0);
			financeMain.setNewRecord(true);
			financeMain.setLastMntOn(new Timestamp(System.currentTimeMillis()));
			financeMain.setLastMntBy(userDetails.getLoginUsrID());
			financeMain.setRecordStatus(PennantConstants.RCD_STATUS_APPROVED);
			financeMain.setFinSourceID(PennantConstants.FINSOURCE_ID_API);

	
			finScheduleData.setFinanceMain(financeMain);

			// set required mandatory values into finance details object
			doSetRequiredDetails(financeDetail);

			if (financeDetail.getFinScheduleData().getErrorDetails() != null) {
				for (ErrorDetails errorDetail : financeDetail.getFinScheduleData().getErrorDetails()) {
					FinanceDetail response = new FinanceDetail();
					doEmptyResponseObject(response);
					response.setReturnStatus(APIErrorHandlerService.getFailedStatus(errorDetail.getErrorCode(),
							errorDetail.getError()));
					return response;
				}
			}

			if (!loanWithWIF) {
				// call schedule calculator
				finScheduleData.getFinanceMain().setCalculateRepay(true);
				finScheduleData = ScheduleGenerator.getNewSchd(finScheduleData);
				if (finScheduleData.getFinanceScheduleDetails().size() != 0) {

					finScheduleData.getFinanceMain().setCalRoundingMode(finScheduleData.getFinanceType().getRoundingMode());
					finScheduleData.getFinanceMain().setRoundingTarget(finScheduleData.getFinanceType().getRoundingTarget());

					finScheduleData = ScheduleCalculator.getCalSchd(finScheduleData, BigDecimal.ZERO);
					finScheduleData.setSchduleGenerated(true);

					// process planned EMI details
					doProcessPlanEMIHDays(finScheduleData);
					if (finScheduleData.getErrorDetails() != null) {
						for (ErrorDetails errorDetail : finScheduleData.getErrorDetails()) {
							FinanceDetail response = new FinanceDetail();
							doEmptyResponseObject(response);
							response.setReturnStatus(APIErrorHandlerService.getFailedStatus(errorDetail.getErrorCode(),
									errorDetail.getError()));
							return response;
						}
					}

					// fees calculation
					if (!finScheduleData.getFinFeeDetailList().isEmpty()) {
						finScheduleData = FeeScheduleCalculator.feeSchdBuild(finScheduleData);
					}
				}
			} else {
				finScheduleData.getFinanceMain().setCalculateRepay(true);
				finScheduleData.setSchduleGenerated(true);
			}

			if (!finScheduleData.getErrorDetails().isEmpty()) {
				financeDetail.setFinScheduleData(finScheduleData);
				return financeDetail;
			}

			// Reset Data
			finScheduleData.getFinanceMain().setEqualRepay(financeMain.isEqualRepay());
			finScheduleData.getFinanceMain().setRecalType(financeMain.getRecalType());
			finScheduleData.getFinanceMain().setLastRepayDate(financeMain.getFinStartDate());
			finScheduleData.getFinanceMain().setLastRepayPftDate(financeMain.getFinStartDate());
			finScheduleData.getFinanceMain().setLastRepayRvwDate(financeMain.getFinStartDate());
			finScheduleData.getFinanceMain().setLastRepayCpzDate(financeMain.getFinStartDate());

			finScheduleData.getFinanceMain().setFinRemarks("SUCCESS");

			// set LastMntBy , LastMntOn and status fields to schedule details
			for (FinanceScheduleDetail schdDetail : finScheduleData.getFinanceScheduleDetails()) {
				schdDetail.setLastMntOn(new Timestamp(System.currentTimeMillis()));
				schdDetail.setRecordStatus(PennantConstants.RCD_STATUS_APPROVED);
			}

			// Finance detail object
			financeDetail.setUserAction("");
			financeDetail.setExtSource(false);
			financeDetail.setAccountingEventCode("");
			financeDetail.setFinReference(financeMain.getFinReference());
			financeDetail.setFinScheduleData(finScheduleData);

			AuditDetail auditDetail = new AuditDetail(PennantConstants.TRAN_WF, 1, null, financeDetail);
			AuditHeader auditHeader = new AuditHeader(financeDetail.getFinReference(), null, null, null, auditDetail,
					financeMain.getUserDetails(), new HashMap<String, ArrayList<ErrorDetails>>());

			APIHeader reqHeaderDetails = (APIHeader) PhaseInterceptorChain.getCurrentMessage().getExchange().get(APIHeader.API_HEADER_KEY);
			auditHeader.setApiHeader(reqHeaderDetails);
			
			// save the finance details into main table
			auditHeader = financeDetailService.doApprove(auditHeader, false);

			FinanceDetail response = null;
			if (auditHeader.getOverideMessage() != null && auditHeader.getOverideMessage().size() > 0) {
				for (ErrorDetails errorDetail : auditHeader.getOverideMessage()) {
					response = new FinanceDetail();
					doEmptyResponseObject(response);
					response.setReturnStatus(APIErrorHandlerService.getFailedStatus(errorDetail.getErrorCode(),
							errorDetail.getError()));
					return response;
				}
			}
			if (auditHeader.getErrorMessage() != null) {
				for (ErrorDetails errorDetail : auditHeader.getErrorMessage()) {
					response = new FinanceDetail();
					doEmptyResponseObject(response);
					response.setReturnStatus(APIErrorHandlerService.getFailedStatus(errorDetail.getErrorCode(),
							errorDetail.getError()));
					return response;
				}
			}
			
			if (auditHeader.getAuditDetail().getErrorDetails() != null) {
				for (ErrorDetails errorDetail : auditHeader.getAuditDetail().getErrorDetails()) {
					response = new FinanceDetail();
					doEmptyResponseObject(response);
					response.setReturnStatus(APIErrorHandlerService.getFailedStatus(errorDetail.getErrorCode(),
							errorDetail.getError()));
					return response;
				}
			}
			
			if (StringUtils.isNotBlank(finReference)) {
				// prepare response object
				response = getFinanceDetailResponse(finReference);
				response.setReturnStatus(APIErrorHandlerService.getSuccessStatus());

				logger.debug("Leaving");
				return response;
			}
			
		} catch (Exception e) {
			logger.error("Exception: ", e);
			FinanceDetail response = new FinanceDetail();
			doEmptyResponseObject(response);
			response.setReturnStatus(APIErrorHandlerService.getFailedStatus());
			return response;
		}

		return null;
	}

	/**
	 * prepare finance detail object with required data to process finance origination.<br>
	 * 
	 * @param financeDetail
	 */
	private void doSetRequiredDetails(FinanceDetail financeDetail) {
		logger.debug("Entering");

		LoggedInUser userDetails = SessionUserDetails.getUserDetails(SessionUserDetails.getLogiedInUser());

		financeDetail.setModuleDefiner(FinanceConstants.FINSER_EVENT_ORG);
		financeDetail.setUserDetails(userDetails);
		financeDetail.setNewRecord(true);
		FinScheduleData finScheduleData = financeDetail.getFinScheduleData();
		FinanceMain financeMain = finScheduleData.getFinanceMain();
		financeMain.setFinIsActive(true);
		financeMain.setFinStatus(financeDetailService.getCustStatusByMinDueDays());

		financeMain.setMaturityDate(financeMain.getCalMaturity());
		financeMain.setNumberOfTerms(financeMain.getCalTerms());
		financeMain.setGrcPeriodEndDate(financeMain.getCalGrcEndDate());
		financeMain.setGraceTerms(financeMain.getCalGrcTerms());
		financeMain.setFinCurrAssetValue(financeMain.getFinAmount());
		
		// set Head branch
		if (StringUtils.isBlank(financeMain.getFinBranch())) {
			financeMain.setFinBranch(userDetails.getBranchCode());
		}

		// setting required values which are not received from API
		if (financeMain.getCustID() > 0) {
			CustomerDetails customerDetails = customerDetailsService.getCustomerDetailsById(financeMain.getCustID(), true, "");
			if (customerDetails != null) {
				customerDetails.setUserDetails(userDetails);
				financeDetail.setCustomerDetails(customerDetails);
			}
		}
		
		// process disbursement details
		List<FinAdvancePayments> advancePayments = financeDetail.getAdvancePaymentsList();
		if (advancePayments != null) {
			int paymentSeq = 1;
			for (FinAdvancePayments advPayment : advancePayments) {
				advPayment.setFinReference(financeMain.getFinReference());
				advPayment.setRecordType(PennantConstants.RECORD_TYPE_NEW);
				advPayment.setNewRecord(true);
				advPayment.setLastMntBy(userDetails.getLoginUsrID());
				advPayment.setLastMntOn(new Timestamp(System.currentTimeMillis()));
				advPayment.setRecordStatus(PennantConstants.RCD_STATUS_APPROVED);
				advPayment.setUserDetails(financeMain.getUserDetails());
				advPayment.setPaymentSeq(paymentSeq);
				advPayment.setLLDate(null);
				paymentSeq++;

				if (StringUtils.equals(advPayment.getPaymentType(), DisbursementConstants.PAYMENT_TYPE_IMPS)
						|| StringUtils.equals(advPayment.getPaymentType(), DisbursementConstants.PAYMENT_TYPE_NEFT)
						|| StringUtils.equals(advPayment.getPaymentType(), DisbursementConstants.PAYMENT_TYPE_RTGS)) {

					BankBranch bankBranch = new BankBranch();
					if (StringUtils.isNotBlank(advPayment.getiFSC())) {
						bankBranch = bankBranchService.getBankBrachByIFSC(advPayment.getiFSC());
					} else if (StringUtils.isNotBlank(advPayment.getBranchBankCode())
							&& StringUtils.isNotBlank(advPayment.getBranchCode())) {
						bankBranch = bankBranchService.getBankBrachByCode(advPayment.getBranchBankCode(),
								advPayment.getBranchCode());
					}

					if (bankBranch != null) {
						advPayment.setiFSC(bankBranch.getIFSC());
						advPayment.setBranchBankCode(bankBranch.getBankCode());
						advPayment.setBranchCode(bankBranch.getBranchCode());
						advPayment.setBankBranchID(bankBranch.getBankBranchID());
					}
				}
			}
		}

		// process finance flags
		List<FinFlagsDetail> finFlagsDetails = financeDetail.getFinFlagsDetails();
		if (finFlagsDetails != null) {
			for (FinFlagsDetail flagDetail : finFlagsDetails) {
				flagDetail.setReference(financeMain.getFinReference());
				flagDetail.setRecordType(PennantConstants.RECORD_TYPE_NEW);
				flagDetail.setModuleName(FinanceConstants.MODULE_NAME);
				flagDetail.setNewRecord(true);
				flagDetail.setLastMntBy(userDetails.getLoginUsrID());
				flagDetail.setLastMntOn(new Timestamp(System.currentTimeMillis()));
				flagDetail.setRecordStatus(PennantConstants.RCD_STATUS_APPROVED);
				flagDetail.setUserDetails(financeMain.getUserDetails());
			}
		}

		// process mandate details
		Mandate mandate = financeDetail.getMandate();
		if (mandate != null) {
			BankBranch bankBranch = new BankBranch();
			if (StringUtils.isNotBlank(mandate.getIFSC())) {
				bankBranch = bankBranchService.getBankBrachByIFSC(mandate.getIFSC());
			} else if (StringUtils.isNotBlank(mandate.getBankCode()) && StringUtils.isNotBlank(mandate.getBranchCode())) {
				bankBranch = bankBranchService.getBankBrachByCode(mandate.getBankCode(), mandate.getBranchCode());
			}

			// mandate details
			financeDetail.getMandate().setCustCIF(financeMain.getLovDescCustCIF());
			financeDetail.getMandate().setCustID(financeMain.getCustID());
			financeDetail.getMandate().setBankCode(bankBranch.getBankCode());
			financeDetail.getMandate().setBranchCode(bankBranch.getBranchCode());
			financeDetail.getMandate().setBankBranchID(bankBranch.getBankBranchID());
			financeDetail.getMandate().setIFSC(bankBranch.getIFSC());
			financeDetail.getMandate().setBankBranchID(bankBranch.getBankBranchID());
		}

		// co-applicant details
		for (JointAccountDetail jointAccDetail : financeDetail.getJountAccountDetailList()) {
			jointAccDetail.setFinReference(financeMain.getFinReference());
			jointAccDetail.setRecordType(PennantConstants.RECORD_TYPE_NEW);
			jointAccDetail.setNewRecord(true);
			jointAccDetail.setLastMntBy(userDetails.getLoginUsrID());
			jointAccDetail.setLastMntOn(new Timestamp(System.currentTimeMillis()));
			jointAccDetail.setRecordStatus(PennantConstants.RCD_STATUS_APPROVED);
			jointAccDetail.setUserDetails(financeMain.getUserDetails());
		}

		// guarantor details
		for (GuarantorDetail guarantorDetail : financeDetail.getGurantorsDetailList()) {
			guarantorDetail.setFinReference(financeMain.getFinReference());
			guarantorDetail.setRecordType(PennantConstants.RECORD_TYPE_NEW);
			guarantorDetail.setNewRecord(true);
			guarantorDetail.setLastMntBy(userDetails.getLoginUsrID());
			guarantorDetail.setLastMntOn(new Timestamp(System.currentTimeMillis()));
			guarantorDetail.setRecordStatus(PennantConstants.RCD_STATUS_APPROVED);
			guarantorDetail.setUserDetails(financeMain.getUserDetails());
		}

		// document details
		for (DocumentDetails detail : financeDetail.getDocumentDetailsList()) {
			detail.setNewRecord(true);
			detail.setRecordType(PennantConstants.RECORD_TYPE_NEW);
			detail.setDocModule(FinanceConstants.MODULE_NAME);
			detail.setUserDetails(financeMain.getUserDetails());
			detail.setDocImage(PennantApplicationUtil.decode(detail.getDocImage()));
		}

		financeDetail.setFinScheduleData(finScheduleData);
		if (financeMain.getCustID() > 0) {
			CustomerDetails custDetails = customerDetailsService.getApprovedCustomerById(financeMain.getCustID());
			financeDetail.setCustomerDetails(custDetails);
		}

		// CollateralAssignment details
		for (CollateralAssignment detail : financeDetail.getCollateralAssignmentList()) {
			CollateralSetup collateralSetup = collateralSetupService.getApprovedCollateralSetupById(detail.getCollateralRef());
			if (collateralSetup != null) {
				detail.setCollateralValue(collateralSetup.getCollateralValue());
			}
			detail.setNewRecord(true);
			detail.setRecordType(PennantConstants.RECORD_TYPE_NEW);
			detail.setModule(FinanceConstants.MODULE_NAME);
			detail.setUserDetails(financeMain.getUserDetails());
			detail.setLastMntBy(userDetails.getLoginUsrID());
			detail.setLastMntOn(new Timestamp(System.currentTimeMillis()));
			detail.setRecordStatus(PennantConstants.RCD_STATUS_APPROVED);
		}

		// execute fee charges
		String finEvent = "";
		feeDetailService.doExecuteFeeCharges(financeDetail, finEvent);

		// validate disbursement instructions
		FinanceDisbursement disbursementDetails = new FinanceDisbursement();
		disbursementDetails.setDisbDate(financeMain.getFinStartDate());
		disbursementDetails.setDisbAmount(financeMain.getFinAmount());
		disbursementDetails.setDisbSeq(1);
		disbursementDetails.setDisbReqDate(DateUtility.getAppDate());
		disbursementDetails.setFeeChargeAmt(financeMain.getFeeChargeAmt());
		disbursementDetails.setInsuranceAmt(financeMain.getInsuranceAmt());
		disbursementDetails.setDisbAccountId(PennantApplicationUtil.unFormatAccountNumber(financeMain.getDisbAccountId()));
		finScheduleData.getDisbursementDetails().add(disbursementDetails);
		
		for(FinAdvancePayments advPayments: financeDetail.getAdvancePaymentsList()) {
			advPayments.setDisbSeq(finScheduleData.getDisbursementDetails().size());
		}
		List<ErrorDetails> errors = finAdvancePaymentsService.validateFinAdvPayments(financeDetail.getAdvancePaymentsList(),
				finScheduleData.getDisbursementDetails(), finScheduleData.getFinanceMain(), true);
		for (ErrorDetails erroDetails : errors) {
			finScheduleData.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetails(erroDetails.getErrorCode(),
					erroDetails.getErrorParameters())));
		}
		
		// Step Policy Details
		if(financeMain.isStepFinance()) {
			String stepPolicyCode = financeMain.getStepPolicy();
			if (StringUtils.isNotBlank(stepPolicyCode)) {
				List<StepPolicyDetail> stepPolicyList = stepPolicyDetailDAO.getStepPolicyDetailListByID(
						stepPolicyCode, "_AView");
				
				// reset step policy details
				finScheduleData.resetStepPolicyDetails(stepPolicyList);
				
				finScheduleData.getFinanceMain().setStepFinance(true);
				finScheduleData.getFinanceMain().setStepPolicy(stepPolicyCode);
				
				// fetch stepHeader details
				StepPolicyHeader header = stepPolicyHeaderDAO.getStepPolicyHeaderByID(stepPolicyCode, "");
				if(header != null) {
					finScheduleData.getFinanceMain().setStepType(header.getStepType());
				}
				
				List<FinanceStepPolicyDetail> finStepDetails = finScheduleData.getStepPolicyDetails();
				
				// method for prepare step installments
				prepareStepInstallements(finStepDetails, financeMain.getNumberOfTerms());

			} else {
				List<FinanceStepPolicyDetail> finStepDetails = finScheduleData.getStepPolicyDetails();
				
				// method for prepare step installments
				prepareStepInstallements(finStepDetails, financeMain.getNumberOfTerms());
			}
		}

		logger.debug("Leaving");

	}

	/**
	 * Method for prepare API response object
	 * 
	 * @param finReference
	 * @return
	 */
	private FinanceDetail getFinanceDetailResponse(String finReference) {
		logger.debug("Enteing");

		// fetch finance basic details
		FinanceMain financeMain = financeMainDAO.getFinanceMainById(finReference, "", false);
		FinanceDetail financeDetail = new FinanceDetail();
		FinScheduleData finScheduleData = new FinScheduleData();
		
		finScheduleData.setFinanceMain(financeMain);
		
		List<FinFeeDetail> finTypeFeeDetails = finFeeDetailService.getFinFeeDetailById(finReference, false, "_View");
		if (finTypeFeeDetails != null) {
			finScheduleData.setFinFeeDetailList(finTypeFeeDetails);
		}
		
		// fetch step details if exists
		if (financeMain != null && financeMain.isStepFinance()) {
			List<FinanceStepPolicyDetail> stepPolicyDetails = financeStepDetailDAO.getFinStepDetailListByFinRef(
					finReference, "", false);
			finScheduleData.setStepPolicyDetails(stepPolicyDetails);
		}
		
		// fetch finance schedule details
		List<FinanceScheduleDetail> finSchduleList = financeScheduleDetailDAO.getFinScheduleDetails(finReference,
				"", false);
		if(finSchduleList != null) {
			finScheduleData.setFinanceScheduleDetails(finSchduleList);
		}
		financeDetail.setFinScheduleData(finScheduleData);
		
		// Fetch summary details
		FinanceSummary summary = getFinanceSummary(financeDetail);
		financeDetail.getFinScheduleData().setFinanceSummary(summary);

		// nullify the unnecessary object
		finScheduleData.setDisbursementDetails(null);
		finScheduleData.setRepayInstructions(null);
		finScheduleData.setRateInstruction(null);

		financeDetail.setFinScheduleData(finScheduleData);

		financeDetail.setJountAccountDetailList(null);
		financeDetail.setGurantorsDetailList(null);
		financeDetail.setDocumentDetailsList(null);
		financeDetail.setFinanceCollaterals(null);

		logger.debug("Leaving");

		return financeDetail;
	}

	public FinanceDetail getFinanceDetails(String finReference) {
		logger.debug("Enetring");

		FinanceDetail financeDetail = null;
		try {
			financeDetail = financeDetailService.getFinanceDetailById(finReference, false, "", 
					false, FinanceConstants.FINSER_EVENT_ORG, "");

			if(financeDetail != null) {
				financeDetail.setReturnStatus(APIErrorHandlerService.getSuccessStatus());
			} else {
				financeDetail = new FinanceDetail();
				financeDetail.setReturnStatus(APIErrorHandlerService.getFailedStatus());
			}
		} catch(Exception e) {
			financeDetail = new FinanceDetail();
			financeDetail.setReturnStatus(APIErrorHandlerService.getFailedStatus("API006", "Test"));
			return financeDetail;
		}

		logger.debug("Leaving");
		return financeDetail;
	}
	
	/**
	 * get the Finance Details by the given finReference.
	 * 
	 * @param finReference
	 * @return FinanceDetail
	 */
	public FinanceDetail getFinInquiryDetails(String finReference) {
		logger.debug("Enetring");

		FinanceDetail financeDetail = null;
		try {
			financeDetail = financeDetailService.getFinanceDetailById(finReference, false, "", false,
					FinanceConstants.FINSER_EVENT_ORG, "");
			if (financeDetail != null) {
				Mandate mandate = financeDetail.getMandate();
				if (mandate != null) {
					long mandateId = mandate.getMandateID();
					List<String> financeRefeList = financeMainService.getFinReferencesByMandateId(mandateId);
					BigDecimal totEMIAmount = BigDecimal.ZERO;
					for (String detail : financeRefeList) {
						List<FinanceScheduleDetail> finSchduleList = financeScheduleDetailDAO.getFinScheduleDetails(detail,"",false);
						if (finSchduleList != null) {
							for (FinanceScheduleDetail financeScheduleDetail : finSchduleList) {
								if (DateUtility.getAppDate().compareTo(financeScheduleDetail.getSchDate()) == -1) {
									if (!(financeScheduleDetail.getRepayAmount().compareTo(BigDecimal.ZERO) == 0)) {
										totEMIAmount = totEMIAmount.add(financeScheduleDetail.getRepayAmount());
										break;
									}
								}
							}
						}
					}
					mandate.setTotEMIAmount(totEMIAmount);
				}
			}
			if (financeDetail != null) {
				prepareResponse(financeDetail);
				financeDetail.setReturnStatus(APIErrorHandlerService.getSuccessStatus());
			} else {
				financeDetail = new FinanceDetail();
				doEmptyResponseObject(financeDetail);
				financeDetail.setReturnStatus(APIErrorHandlerService.getFailedStatus());
			}
		} catch (Exception e) {
			financeDetail = new FinanceDetail();
			doEmptyResponseObject(financeDetail);
			financeDetail.setReturnStatus(APIErrorHandlerService.getFailedStatus());
			return financeDetail;
		}

		logger.debug("Leaving");
		return financeDetail;
	}

	/**
	 * get the Finance Details by the given CustCif.
	 * 
	 * @param ID
	 * @return FinanceDetail
	 */
	public FinanceInquiry getFinanceDetailsById(String reference, String serviceType) {
		logger.debug("Entering");
		try {
			FinanceInquiry financeInquiry = new FinanceInquiry();
			List<FinanceMain> financeMainList = null;
			String[] valueParm = new String[1];

			if (StringUtils.equalsIgnoreCase(APIConstants.FINANCE_INQUIRY_CUSTOMER, serviceType)) {
				Customer customer = customerDetailsService.getCustomerByCIF(reference);
				financeMainList = financeMainService.getFinanceByCustId(customer.getCustID());
				valueParm[0] = "CIF :" + reference;
			} else {
				financeMainList = financeMainService.getFinanceByCollateralRef(reference);
				valueParm[0] = "CollateralRef :" + reference;
			}

			if (financeMainList.size() == 0) {
				financeInquiry.setReturnStatus(APIErrorHandlerService.getFailedStatus("90260", valueParm));
				return financeInquiry;
			}

			List<FinInquiryDetail> finance = new ArrayList<FinInquiryDetail>();
			for (FinanceMain financeMain : financeMainList) {
				FinInquiryDetail finInquiryDetail = new FinInquiryDetail();
				BigDecimal paidTotal = BigDecimal.ZERO;
				BigDecimal schdFeePaid = BigDecimal.ZERO;
				BigDecimal schdInsPaid = BigDecimal.ZERO;
				BigDecimal schdPftPaid = BigDecimal.ZERO;
				BigDecimal schdPriPaid = BigDecimal.ZERO;
				BigDecimal principalSchd = BigDecimal.ZERO;
				BigDecimal profitSchd = BigDecimal.ZERO;
				int futureInst = 0;
				List<FinanceScheduleDetail> finSchduleList = financeScheduleDetailDAO.getFinScheduleDetails(
						financeMain.getFinReference(), "", false);
				boolean isnextRepayAmount = true;
				if (finSchduleList != null) {
					for (FinanceScheduleDetail financeScheduleDetail : finSchduleList) {
						schdFeePaid = schdFeePaid.add(financeScheduleDetail.getSchdFeePaid());
						schdInsPaid = schdInsPaid.add(financeScheduleDetail.getSchdInsPaid());
						schdPftPaid = schdPftPaid.add(financeScheduleDetail.getSchdPftPaid());
						schdPriPaid = schdPriPaid.add(financeScheduleDetail.getSchdPriPaid());
						principalSchd = principalSchd.add(financeScheduleDetail.getPrincipalSchd());
						profitSchd = profitSchd.add(financeScheduleDetail.getProfitSchd());
						if (DateUtility.getAppDate().compareTo(financeScheduleDetail.getSchDate()) == -1) {
							if (!(financeScheduleDetail.getRepayAmount().compareTo(BigDecimal.ZERO) == 0) && isnextRepayAmount) {
								finInquiryDetail.setNextRepayAmount(financeScheduleDetail.getRepayAmount());
								isnextRepayAmount = false;
							}
							futureInst++;
						}
					}
				}
				finInquiryDetail.setFinReference(financeMain.getFinReference());
				finInquiryDetail.setFinType(financeMain.getFinType());
				finInquiryDetail.setProduct(financeMain.getLovDescFinProduct());
				finInquiryDetail.setFinCcy(financeMain.getFinCcy());
				finInquiryDetail.setFinAmount(financeMain.getFinAmount());
				finInquiryDetail.setFinAssetValue(financeMain.getFinAssetValue());
				finInquiryDetail.setNumberOfTerms(financeMain.getNumberOfTerms());
				finInquiryDetail.setFirstEmiAmount(financeMain.getFirstRepay());
				finInquiryDetail.setLoanTenor(DateUtility.getMonthsBetween(financeMain.getFinStartDate(),
						financeMain.getMaturityDate(), true));
				finInquiryDetail.setMaturityDate(financeMain.getMaturityDate());
				paidTotal = schdPriPaid.add(schdPftPaid).add(schdFeePaid).add(schdInsPaid);
				finInquiryDetail.setPaidTotal(paidTotal);
				finInquiryDetail.setPaidPri(schdPriPaid);
				finInquiryDetail.setPaidPft(schdPftPaid);
				BigDecimal outstandingPri = principalSchd.subtract(schdPriPaid);
				BigDecimal outstandingPft = profitSchd.subtract(schdPftPaid);
				finInquiryDetail.setOutstandingTotal(outstandingPri.add(outstandingPft));
				finInquiryDetail.setOutstandingPri(outstandingPri);
				finInquiryDetail.setOutstandingPft(outstandingPft);
				finInquiryDetail.setFutureInst(futureInst);
				
				// set Finance closing status
				if (StringUtils.isBlank(financeMain.getClosingStatus())) {
					finInquiryDetail.setFinStatus(APIConstants.CLOSE_STATUS_ACTIVE);
				} else {
					finInquiryDetail.setFinStatus(financeMain.getClosingStatus());
				}

				// fetch co-applicant details
				List<JointAccountDetail> jountAccountDetailList = jointAccountDetailService.getJoinAccountDetail(
						financeMain.getFinReference(), "_View");
				finInquiryDetail.setJountAccountDetailList(jountAccountDetailList);
				
				// fetch disbursement details
				List<FinanceDisbursement> disbList = getFinanceDisbursementDAO().getFinanceDisbursementDetails(
						financeMain.getFinReference(), "", false);
				BigDecimal totDisbAmt = BigDecimal.ZERO;
				BigDecimal totfeeChrgAmt = BigDecimal.ZERO;
				for(FinanceDisbursement finDisb: disbList) {
					totDisbAmt = totDisbAmt.add(finDisb.getDisbAmount());
					totfeeChrgAmt = totfeeChrgAmt.add(finDisb.getFeeChargeAmt());
				}
				BigDecimal assetValue = financeMain.getFinAssetValue() == null?BigDecimal.ZERO:financeMain.getFinAssetValue();
				if(assetValue.compareTo(totDisbAmt) == 0) {
					finInquiryDetail.setDisbStatus(APIConstants.FIN_DISB_FULLY);
				} else {
					finInquiryDetail.setDisbStatus(APIConstants.FIN_DISB_PARTIAL);
				}
				
				finance.add(finInquiryDetail);
			}
			financeInquiry.setFinance(finance);
			financeInquiry.setReturnStatus(APIErrorHandlerService.getSuccessStatus());
			logger.debug("Leaving");
			return financeInquiry;
		} catch(Exception e) {
			FinanceInquiry financeInquiry= new FinanceInquiry();
			financeInquiry.setReturnStatus(APIErrorHandlerService.getFailedStatus());
			return financeInquiry;
		}

	}

	private void prepareResponse(FinanceDetail financeDetail) {
		financeDetail.setFinReference(financeDetail.getFinScheduleData().getFinReference());
		financeDetail.getFinScheduleData().setFinReference(null);
		financeDetail.getFinScheduleData().setInsuranceList(null);
		financeDetail.getFinScheduleData().setStepPolicyDetails(null);
		financeDetail.getFinScheduleData().setPlanEMIHDates(null);
		financeDetail.getFinScheduleData().setPlanEMIHmonths(null);
		financeDetail.setFinFlagsDetails(null);
		financeDetail.setCovenantTypeList(null);
		//disbursement Dates
		List<FinanceDisbursement> disbList = financeDetail.getFinScheduleData().getDisbursementDetails();
		Collections.sort(disbList, new Comparator<FinanceDisbursement>() {
			@Override
			public int compare(FinanceDisbursement b1, FinanceDisbursement b2) {
				return (new Integer(b1.getDisbSeq()).compareTo(new Integer(b2.getDisbSeq())));
			}
		});

		if (disbList != null && disbList.size() > 0) {
			if (disbList.size() == 1) {
				financeDetail.getFinScheduleData().getFinanceMain().setFirstDisbDate(disbList.get(0).getDisbDate());
				financeDetail.getFinScheduleData().getFinanceMain().setLastDisbDate(disbList.get(0).getDisbDate());
			} else {
				financeDetail.getFinScheduleData().getFinanceMain().setFirstDisbDate(disbList.get(0).getDisbDate());
				financeDetail.getFinScheduleData().getFinanceMain().setLastDisbDate(disbList.get(disbList.size() - 1).getDisbDate());
			}
		}

		List<FinFeeDetail> finFeeDetail = financeDetail.getFinScheduleData().getFinFeeDetailList();
		financeDetail.setFinFeeDetails(finFeeDetail);

		// Fetch summary details
		FinanceSummary summary = getFinanceSummary(financeDetail);
		financeDetail.getFinScheduleData().setFinanceSummary(summary);

		// customer details
		CustomerDetails customerDetail = financeDetail.getCustomerDetails();
		customerDetail.setAddressList(null);
		customerDetail.setCustCIF(customerDetail.getCustomer().getCustCIF());
		customerDetail.setCustCoreBank(customerDetail.getCustomer().getCustCoreBank());
		customerDetail.setCustCtgCode(customerDetail.getCustomer().getCustCtgCode());
		customerDetail.setCustDftBranch(customerDetail.getCustomer().getCustDftBranch());
		customerDetail.setCustBaseCcy(customerDetail.getCustomer().getCustBaseCcy());
		customerDetail.setPrimaryRelationOfficer(customerDetail.getCustomer().getCustRO1());
		customerDetail.setCustomer(customerDetail.getCustomer());
		customerDetail.setCustomerPhoneNumList(null);
		customerDetail.setCustEmployeeDetail(null);
		customerDetail.setCustomerEMailList(null);
		customerDetail.setCustomerExtLiabilityList(null);
		customerDetail.setCustomerIncomeList(null);
		customerDetail.setCustomerDocumentsList(null);
		customerDetail.setCustomerBankInfoList(null);
		customerDetail.setEmploymentDetailsList(null);
		customerDetail.setCustomerChequeInfoList(null);
	}

	/**
	 * Method for prepare step installments
	 * 
	 * @param finStepDetails
	 * @param totalTerms
	 */
	private void prepareStepInstallements(List<FinanceStepPolicyDetail> finStepDetails, int totalTerms) {
		logger.debug("Entering");

		int sumInstallments = 0;

		for (int i = 0; i < finStepDetails.size(); i++) {
			FinanceStepPolicyDetail detail = finStepDetails.get(i);
			BigDecimal terms = detail.getTenorSplitPerc().multiply(new BigDecimal(totalTerms))
					.divide(new BigDecimal(100), 0, RoundingMode.HALF_DOWN);
			sumInstallments = sumInstallments + Integer.parseInt(terms.toString());
			detail.setInstallments(Integer.parseInt(terms.toString()));
			if (i == (finStepDetails.size() - 1)) {
				if (sumInstallments != totalTerms) {
					detail.setInstallments(detail.getInstallments() + totalTerms - sumInstallments);
				}
			}
		}
		logger.debug("Leaving");
	}

	private void doEmptyResponseObject(FinanceDetail detail) {
		detail.setFinScheduleData(null);
		detail.setDocumentDetailsList(null);
		detail.setJountAccountDetailList(null);
		detail.setGurantorsDetailList(null);
		detail.setCollateralAssignmentList(null);
		detail.setFinFlagsDetails(null);
	}

	public void setFinanceDetailService(FinanceDetailService financeDetailService) {
		this.financeDetailService = financeDetailService;
	}


	public void setStepPolicyDetailDAO(StepPolicyDetailDAO stepPolicyDetailDAO) {
		this.stepPolicyDetailDAO = stepPolicyDetailDAO;
	}

	public void setFinanceMainDAO(FinanceMainDAO financeMainDAO) {
		this.financeMainDAO = financeMainDAO;
	}

	public void setFinanceScheduleDetailDAO(FinanceScheduleDetailDAO financeScheduleDetailDAO) {
		this.financeScheduleDetailDAO = financeScheduleDetailDAO;
	}

	public void setCustomerDetailsService(CustomerDetailsService customerDetailsService) {
		this.customerDetailsService = customerDetailsService;
	}

	@Autowired
	public void setBankBranchService(BankBranchService bankBranchService) {
		this.bankBranchService = bankBranchService;
	}
	
	public void setStepPolicyHeaderDAO(StepPolicyHeaderDAO stepPolicyHeaderDAO) {
		this.stepPolicyHeaderDAO = stepPolicyHeaderDAO;
	}
	
	public void setFinanceStepDetailDAO(FinanceStepDetailDAO financeStepDetailDAO) {
		this.financeStepDetailDAO = financeStepDetailDAO;
	}
	
	public void setFeeDetailService(FeeDetailService feeDetailService) {
		this.feeDetailService = feeDetailService;
	}
	
	public void setFinFeeDetailService(FinFeeDetailService finFeeDetailService) {
		this.finFeeDetailService = finFeeDetailService;
	}
	public void setCollateralSetupService(CollateralSetupService collateralSetupService) {
		this.collateralSetupService = collateralSetupService;
	}
	public void setFinanceMainService(FinanceMainService financeMainService) {
		this.financeMainService = financeMainService;
	}

	public JointAccountDetailService getJointAccountDetailService() {
		return jointAccountDetailService;
	}

	public void setJointAccountDetailService(JointAccountDetailService jointAccountDetailService) {
		this.jointAccountDetailService = jointAccountDetailService;
	}

	public void setFinAdvancePaymentsService(FinAdvancePaymentsService finAdvancePaymentsService) {
		this.finAdvancePaymentsService = finAdvancePaymentsService;
	}
}
