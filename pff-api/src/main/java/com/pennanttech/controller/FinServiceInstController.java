package com.pennanttech.controller;

import java.lang.reflect.InvocationTargetException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.security.auth.login.AccountNotFoundException;

import org.apache.commons.lang.StringUtils;
import org.apache.cxf.phase.PhaseInterceptorChain;
import org.apache.log4j.Logger;
import org.jaxen.JaxenException;

import com.amazonaws.util.CollectionUtils;
import com.pennant.app.constants.AccountConstants;
import com.pennant.app.constants.AccountEventConstants;
import com.pennant.app.constants.CalculationConstants;
import com.pennant.app.util.APIHeader;
import com.pennant.app.util.CalculationUtil;
import com.pennant.app.util.DateUtility;
import com.pennant.app.util.ErrorUtil;
import com.pennant.app.util.FeeScheduleCalculator;
import com.pennant.app.util.ReceiptCalculator;
import com.pennant.app.util.RepayCalculator;
import com.pennant.app.util.ScheduleGenerator;
import com.pennant.app.util.SessionUserDetails;
import com.pennant.app.util.SysParamUtil;
import com.pennant.backend.dao.collateral.ExtendedFieldRenderDAO;
import com.pennant.backend.dao.finance.FinODDetailsDAO;
import com.pennant.backend.dao.finance.FinODPenaltyRateDAO;
import com.pennant.backend.dao.finance.FinServiceInstrutionDAO;
import com.pennant.backend.dao.finance.FinanceProfitDetailDAO;
import com.pennant.backend.dao.finance.ReceiptResponseDetailDAO;
import com.pennant.backend.dao.finance.ReceiptUploadDetailDAO;
import com.pennant.backend.dao.partnerbank.PartnerBankDAO;
import com.pennant.backend.dao.receipts.FinReceiptDetailDAO;
import com.pennant.backend.dao.receipts.FinReceiptHeaderDAO;
import com.pennant.backend.dao.rmtmasters.FinanceTypeDAO;
import com.pennant.backend.dao.staticparms.ExtendedFieldHeaderDAO;
import com.pennant.backend.financeservice.AddDisbursementService;
import com.pennant.backend.financeservice.AddRepaymentService;
import com.pennant.backend.financeservice.CancelDisbursementService;
import com.pennant.backend.financeservice.ChangeFrequencyService;
import com.pennant.backend.financeservice.ChangeProfitService;
import com.pennant.backend.financeservice.ChangeScheduleMethodService;
import com.pennant.backend.financeservice.PostponementService;
import com.pennant.backend.financeservice.RateChangeService;
import com.pennant.backend.financeservice.ReScheduleService;
import com.pennant.backend.financeservice.RecalculateService;
import com.pennant.backend.financeservice.RemoveTermsService;
import com.pennant.backend.model.WSReturnStatus;
import com.pennant.backend.model.WorkFlowDetails;
import com.pennant.backend.model.Repayments.FinanceRepayments;
import com.pennant.backend.model.applicationmaster.BankDetail;
import com.pennant.backend.model.applicationmaster.LoanPendingData;
import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.model.bmtmasters.BankBranch;
import com.pennant.backend.model.collateral.CollateralAssignment;
import com.pennant.backend.model.configuration.VASRecording;
import com.pennant.backend.model.extendedfield.ExtendedField;
import com.pennant.backend.model.extendedfield.ExtendedFieldData;
import com.pennant.backend.model.extendedfield.ExtendedFieldHeader;
import com.pennant.backend.model.extendedfield.ExtendedFieldRender;
import com.pennant.backend.model.finance.DisbursementServiceReq;
import com.pennant.backend.model.finance.FinAdvancePayments;
import com.pennant.backend.model.finance.FinAssetTypes;
import com.pennant.backend.model.finance.FinCollaterals;
import com.pennant.backend.model.finance.FinFeeDetail;
import com.pennant.backend.model.finance.FinODDetails;
import com.pennant.backend.model.finance.FinODPenaltyRate;
import com.pennant.backend.model.finance.FinReceiptData;
import com.pennant.backend.model.finance.FinReceiptDetail;
import com.pennant.backend.model.finance.FinReceiptHeader;
import com.pennant.backend.model.finance.FinRepayHeader;
import com.pennant.backend.model.finance.FinScheduleData;
import com.pennant.backend.model.finance.FinServiceInstruction;
import com.pennant.backend.model.finance.FinTaxDetails;
import com.pennant.backend.model.finance.FinanceDetail;
import com.pennant.backend.model.finance.FinanceDeviations;
import com.pennant.backend.model.finance.FinanceDisbursement;
import com.pennant.backend.model.finance.FinanceEligibilityDetail;
import com.pennant.backend.model.finance.FinanceMain;
import com.pennant.backend.model.finance.FinanceProfitDetail;
import com.pennant.backend.model.finance.FinanceScheduleDetail;
import com.pennant.backend.model.finance.FinanceStepPolicyDetail;
import com.pennant.backend.model.finance.FinanceSummary;
import com.pennant.backend.model.finance.GuarantorDetail;
import com.pennant.backend.model.finance.Insurance;
import com.pennant.backend.model.finance.JointAccountDetail;
import com.pennant.backend.model.finance.RepayData;
import com.pennant.backend.model.finance.RepayScheduleDetail;
import com.pennant.backend.model.finance.ZIPCodeDetails;
import com.pennant.backend.model.finance.contractor.ContractorAssetDetail;
import com.pennant.backend.model.lmtmasters.FinanceCheckListReference;
import com.pennant.backend.model.lmtmasters.FinanceReferenceDetail;
import com.pennant.backend.model.lmtmasters.FinanceWorkFlow;
import com.pennant.backend.model.partnerbank.PartnerBank;
import com.pennant.backend.model.rmtmasters.FinTypeFees;
import com.pennant.backend.model.rmtmasters.FinanceType;
import com.pennant.backend.model.rulefactory.FeeRule;
import com.pennant.backend.model.rulefactory.ReturnDataSet;
import com.pennant.backend.service.applicationmaster.BankDetailService;
import com.pennant.backend.service.bmtmasters.BankBranchService;
import com.pennant.backend.service.extendedfields.ExtendedFieldDetailsService;
import com.pennant.backend.service.fees.FeeDetailService;
import com.pennant.backend.service.finance.FeeReceiptService;
import com.pennant.backend.service.finance.FinAdvancePaymentsService;
import com.pennant.backend.service.finance.FinFeeDetailService;
import com.pennant.backend.service.finance.FinanceDetailService;
import com.pennant.backend.service.finance.FinanceMainService;
import com.pennant.backend.service.finance.ManualPaymentService;
import com.pennant.backend.service.finance.ReceiptService;
import com.pennant.backend.service.finance.ZIPCodeDetailsService;
import com.pennant.backend.service.lmtmasters.FinanceWorkFlowService;
import com.pennant.backend.service.rmtmasters.FinTypePartnerBankService;
import com.pennant.backend.util.DisbursementConstants;
import com.pennant.backend.util.ExtendedFieldConstants;
import com.pennant.backend.util.FinanceConstants;
import com.pennant.backend.util.PennantApplicationUtil;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.RepayConstants;
import com.pennant.backend.util.WorkFlowUtil;
import com.pennanttech.pennapps.core.AppException;
import com.pennanttech.pennapps.core.InterfaceException;
import com.pennanttech.pennapps.core.engine.workflow.WorkflowEngine;
import com.pennanttech.pennapps.core.model.ErrorDetail;
import com.pennanttech.pennapps.core.model.LoggedInUser;
import com.pennanttech.pennapps.web.util.MessageUtil;
import com.pennanttech.util.APIConstants;
import com.pennanttech.ws.service.APIErrorHandlerService;
import com.rits.cloning.Cloner;

public class FinServiceInstController extends SummaryDetailService {

	private static final Logger logger = Logger.getLogger(FinServiceInstController.class);

	private FinanceDetailService financeDetailService;
	private RateChangeService rateChangeService;
	private AddRepaymentService addRepaymentService;
	private RecalculateService recalService;
	private ChangeProfitService changeProfitService;
	private AddDisbursementService addDisbursementService;
	private CancelDisbursementService cancelDisbursementService;
	private ZIPCodeDetailsService zIPCodeDetailsService;
	private ChangeFrequencyService changeFrequencyService;
	private ReScheduleService reScheduleService;
	private PostponementService postponementService;
	private RemoveTermsService rmvTermsService;
	private FinanceMainService financeMainService;
	private FinODPenaltyRateDAO finODPenaltyRateDAO;
	private FeeDetailService feeDetailService;
	private FinFeeDetailService finFeeDetailService;
	private BankBranchService bankBranchService;
	private FinAdvancePaymentsService finAdvancePaymentsService;
	private BankDetailService bankDetailService;
	private ReceiptService receiptService;
	private FinTypePartnerBankService finTypePartnerBankService;
	private ReceiptCalculator receiptCalculator;
	private PartnerBankDAO partnerBankDAO;
	private ManualPaymentService manualPaymentService;
	private RepayCalculator repayCalculator;
	private FinanceProfitDetailDAO profitDetailsDAO;
	private ChangeScheduleMethodService changeScheduleMethodService;
	private FinanceTypeDAO financeTypeDAO;
	private FeeReceiptService feeReceiptService;
	private FinReceiptHeaderDAO finReceiptHeaderDAO;
	private FinServiceInstrutionDAO finServiceInstructionDAO;
	private ExtendedFieldHeaderDAO extendedFieldHeaderDAO;
	private ExtendedFieldDetailsService extendedFieldDetailsService;
	private ExtendedFieldRenderDAO extendedFieldRenderDAO;
	
	//### 18-07-2018 Ticket ID : 124998,Receipt upload
	private ReceiptUploadDetailDAO receiptUploadDetailDAO;
	private ReceiptResponseDetailDAO receiptResponseDetailDAO;
	private FinReceiptDetailDAO finReceiptDetailDAO;
	private FinanceWorkFlowService financeWorkFlowService;
	protected transient WorkflowEngine workFlow = null;
	
	/**
	 * Method for fetch DisbursementInquiryDetails by FinReference and linkedTranId
	 * 
	 * @param FinReference
	 * @param serviceReqNo
	 * 
	 * @return DisbursementInquiryDetails
	 */

	public DisbursementServiceReq doDisbursementInquiry(DisbursementServiceReq disbursementServiceReq) {
		logger.debug("Entering");

		DisbursementServiceReq inquiryDetails = null;
		List<FinAdvancePayments> finAdvancePayments = null;
		FinanceMain financeMain = null;
		List<ReturnDataSet> postingList;
		List<Long> tranIdList = new ArrayList<>();

		financeMain = financeMainService.getFinanceMainByFinRef(disbursementServiceReq.getFinReference());
		if (financeMain == null) {
			DisbursementServiceReq error = new DisbursementServiceReq();
			String valueParam[] = new String[1];
			valueParam[0] = disbursementServiceReq.getFinReference();
			error.setReturnStatus(APIErrorHandlerService.getFailedStatus("90201", valueParam));
			return error;
		}
		List<FinServiceInstruction> finServiceInstruction = finServiceInstructionDAO
				.getFinServiceInstDetailsByServiceReqNo(disbursementServiceReq.getFinReference(),
						disbursementServiceReq.getServiceReqNo());

		if (CollectionUtils.isNullOrEmpty(finServiceInstruction)) {
			inquiryDetails = new DisbursementServiceReq();
			String valueParam[] = new String[1];
			valueParam[0] = disbursementServiceReq.getFinReference();
			inquiryDetails.setReturnStatus(APIErrorHandlerService.getFailedStatus("90201", valueParam));
			return inquiryDetails;
		}
		inquiryDetails = new DisbursementServiceReq();
		inquiryDetails.setFinCurrAssetValue(financeMain.getFinCurrAssetValue());
		inquiryDetails.setMaturityDate(financeMain.getMaturityDate());
		inquiryDetails.setFinReference(financeMain.getFinReference());
		inquiryDetails.setFinStartDate(financeMain.getFinStartDate());

		finAdvancePayments = finAdvancePaymentsService.getFinAdvancePaymentByFinRef(
				disbursementServiceReq.getFinReference(), finServiceInstruction.get(0).getFromDate());
		if (CollectionUtils.isNullOrEmpty(finAdvancePayments)) {
			inquiryDetails = new DisbursementServiceReq();
			String valueParam[] = new String[1];
			valueParam[0] = disbursementServiceReq.getFinReference();
			inquiryDetails.setReturnStatus(APIErrorHandlerService.getFailedStatus("90201", valueParam));
			return inquiryDetails;
		}
		//TODO temporary FIX
		List<FinAdvancePayments> finAdvPayments = new ArrayList<>();
		finAdvPayments.add(finAdvancePayments.get(0));
		inquiryDetails.setFinAdvancePayments(finAdvPayments);

		for (FinAdvancePayments advancePayments : finAdvancePayments) {
			tranIdList.add(advancePayments.getLinkedTranId());
		}

		postingList = finAdvancePaymentsService.getPostingsByLinkedTranId(tranIdList,
				disbursementServiceReq.getFinReference());
		if (postingList == null) {
			DisbursementServiceReq error = new DisbursementServiceReq();
			String valueParam[] = new String[3];
			valueParam[0] = "finRefernce/linkedTranId";
			valueParam[1] = disbursementServiceReq.getFinReference();
			valueParam[2] = String.valueOf(disbursementServiceReq.getLinkedTranId());
			error.setReturnStatus(APIErrorHandlerService.getFailedStatus("99022", valueParam));
			return error;
		}

		inquiryDetails.setPostingList(postingList);

		logger.debug("Leaving");
		return inquiryDetails;
	}

	/**
	 * Method for fetch ZIPCode Details of corresponding pin code
	 * 
	 * @param pinCode
	 * @return ZIPCodeDetails
	 */
	public ZIPCodeDetails getZIPCodeDetails(String pinCode) {
		return zIPCodeDetailsService.getZIPCodeDetails(pinCode);
	}

	/**
	 * Method for fetch Bank Details of corresponding ifsc code
	 * 
	 * @param ifsc
	 * @return BankDetail
	 */
	public BankDetail getBankDetailsByIfsc(String ifsc) {
		return bankDetailService.getBankDetailsByIfsc(ifsc);
	}

	/**
	 * Method for process AddRateChange request and re-calculate schedule details
	 * 
	 * @param finServiceInstruction
	 * @param eventCode
	 * @return
	 * @throws JaxenException
	 */
	public FinanceDetail doAddRateChange(FinServiceInstruction finServiceInst, String eventCode) {
		logger.debug("Enteing");

		// fetch finance data
		FinanceDetail financeDetail = getFinanceDetails(finServiceInst, eventCode);
		if (financeDetail != null) {
			FinScheduleData finScheduleData = financeDetail.getFinScheduleData();
			FinanceMain financeMain = finScheduleData.getFinanceMain();
			for (FinFeeDetail fees : financeDetail.getFinScheduleData().getFinFeeDetailList()) {
				fees.setFinTaxDetails(new FinTaxDetails());
			}
			financeMain.setEventFromDate(finServiceInst.getFromDate());
			financeMain.setEventToDate(finServiceInst.getToDate());
			financeMain.setRecalFromDate(finServiceInst.getRecalFromDate());
			financeMain.setRecalType(finServiceInst.getRecalType());
			financeMain.setRecalSchdMethod(financeMain.getScheduleMethod());
			financeMain.setFinSourceID(APIConstants.FINSOURCE_ID_API);
			financeMain.setRcdMaintainSts(FinanceConstants.FINSER_EVENT_RATECHG);

			if (CalculationConstants.RPYCHG_TILLMDT.equals(finServiceInst.getRecalType())) {
				financeMain.setRecalToDate(financeMain.getMaturityDate());
			} else if (CalculationConstants.RPYCHG_TILLDATE.equals(finServiceInst.getRecalType())) {
				financeMain.setRecalToDate(finServiceInst.getRecalToDate());
			}
			if (StringUtils.isBlank(finServiceInst.getPftDaysBasis())) {
				finServiceInst.setPftDaysBasis(financeMain.getProfitDaysBasis());
			}
			//OverDraft Loan Type
			if (StringUtils.equals(FinanceConstants.PRODUCT_ODFACILITY, finServiceInst.getRecalType())) {
				finScheduleData.getFinanceMain().setRecalType(CalculationConstants.RPYCHG_ADJMDT);
			}

			finServiceInst.setModuleDefiner(FinanceConstants.FINSER_EVENT_RATECHG);
			try {
				// execute fee charges
				executeFeeCharges(financeDetail, finServiceInst, eventCode);
				if (financeDetail.getFinScheduleData().getErrorDetails() != null) {
					for (ErrorDetail errorDetail : financeDetail.getFinScheduleData().getErrorDetails()) {
						FinanceDetail response = new FinanceDetail();
						doEmptyResponseObject(response);
						response.setReturnStatus(
								APIErrorHandlerService.getFailedStatus(errorDetail.getCode(), errorDetail.getError()));
						return response;
					}
				}

				// call schedule calculator for Rate change
				finScheduleData = rateChangeService.getRateChangeDetails(finScheduleData, finServiceInst,
						FinanceConstants.FINSER_EVENT_RATECHG);

				if (finScheduleData.getErrorDetails() != null) {
					for (ErrorDetail errorDetail : finScheduleData.getErrorDetails()) {
						FinanceDetail response = new FinanceDetail();
						response.setReturnStatus(
								APIErrorHandlerService.getFailedStatus(errorDetail.getCode(), errorDetail.getError()));
						return response;
					}
				}
				financeDetail.setFinScheduleData(finScheduleData);
				// Get the response
				financeDetail = getResponse(financeDetail, finServiceInst);

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
		} else {
			financeDetail = new FinanceDetail();
			financeDetail.setReturnStatus(APIErrorHandlerService.getFailedStatus());
		}

		logger.debug("Leaving");
		return financeDetail;
	}

	private void executeFeeCharges(FinanceDetail financeDetail, FinServiceInstruction finServiceInst, String eventCode)
			throws IllegalAccessException, InvocationTargetException {

		if (finServiceInst.getFinFeeDetails() != null) {
			if (StringUtils.equals(finServiceInst.getReqType(), APIConstants.REQTYPE_INQUIRY)
					&& (finServiceInst.getFinFeeDetails() == null || finServiceInst.getFinFeeDetails().isEmpty())) {
				feeDetailService.doProcessFeesForInquiry(financeDetail, eventCode, finServiceInst, true);
			} else {
				for (FinFeeDetail finFeeDetail : finServiceInst.getFinFeeDetails()) {
					finFeeDetail.setFinEvent(eventCode);
					financeDetail.getFinScheduleData().getFinFeeDetailList().add(finFeeDetail);
					finFeeDetail.setFeeScheduleMethod(PennantConstants.List_Select);
				}
				feeDetailService.doExecuteFeeCharges(financeDetail, eventCode, finServiceInst, false);

				if (financeDetail.isStp()) {
					for (FinFeeDetail feeDetail : financeDetail.getFinScheduleData().getFinFeeDetailList()) {
						feeDetail.setWorkflowId(0);
					}
				}
			}
		}

	}

	/**
	 * Method for process add repayment request and calculate schedule
	 * 
	 * @param eventCode
	 * 
	 * @param finServiceInstruction
	 * @return
	 * @throws JaxenException
	 */
	public FinanceDetail doAddRepayment(FinServiceInstruction finServiceInst, String eventCode) {
		logger.debug("Enteing");

		// fetch finance data
		FinanceDetail financeDetail = getFinanceDetails(finServiceInst, eventCode);

		if (financeDetail != null) {
			FinScheduleData finScheduleData = financeDetail.getFinScheduleData();
			FinanceMain financeMain = finScheduleData.getFinanceMain();

			financeMain.setEventFromDate(finServiceInst.getFromDate());
			financeMain.setEventToDate(finServiceInst.getToDate());
			// financeMain.setScheduleMethod(finServiceInst.getSchdMethod());
			// financeMain.setRecalSchdMethod(finServiceInst.getSchdMethod());
			finServiceInst.setSchdMethod(financeMain.getScheduleMethod());
			financeMain.setRcdMaintainSts(FinanceConstants.FINSER_EVENT_CHGRPY);

			financeMain.setRecalType(finServiceInst.getRecalType());
			financeMain.setAdjTerms(finServiceInst.getTerms());

			if (StringUtils.equals(finServiceInst.getRecalType(), CalculationConstants.RPYCHG_TILLMDT)) {
				financeMain.setRecalFromDate(finServiceInst.getRecalFromDate());
				financeMain.setRecalToDate(financeMain.getMaturityDate());
			} else if (StringUtils.equals(finServiceInst.getRecalType(), CalculationConstants.RPYCHG_ADJMDT)
					|| StringUtils.equals(finServiceInst.getRecalType(), CalculationConstants.RPYCHG_ADDTERM)) {
				financeMain.setRecalFromDate(finServiceInst.getFromDate());
				financeMain.setRecalToDate(financeMain.getMaturityDate());
			} else if (StringUtils.equals(finServiceInst.getRecalType(), CalculationConstants.RPYCHG_TILLDATE)) {
				financeMain.setRecalFromDate(finServiceInst.getRecalFromDate());
				financeMain.setRecalToDate(finServiceInst.getRecalToDate());
			} else if (StringUtils.equals(finServiceInst.getRecalType(), CalculationConstants.RPYCHG_ADDRECAL)) {
				financeMain.setRecalFromDate(finServiceInst.getRecalFromDate());
				financeMain.setRecalToDate(financeMain.getMaturityDate());
				// financeMain.setScheduleRegenerated(true);
			}

			financeMain.setFinSourceID(APIConstants.FINSOURCE_ID_API);
			finServiceInst.setModuleDefiner(FinanceConstants.FINSER_EVENT_CHGRPY);
			try {
				// execute fee charges
				executeFeeCharges(financeDetail, finServiceInst, eventCode);
				if (financeDetail.getFinScheduleData().getErrorDetails() != null) {
					for (ErrorDetail errorDetail : financeDetail.getFinScheduleData().getErrorDetails()) {
						FinanceDetail response = new FinanceDetail();
						doEmptyResponseObject(response);
						response.setReturnStatus(
								APIErrorHandlerService.getFailedStatus(errorDetail.getCode(), errorDetail.getError()));
						return response;
					}
				}
				// Call Schedule calculator for Rate change
				finScheduleData = addRepaymentService.getAddRepaymentDetails(finScheduleData, finServiceInst);

				if (finScheduleData.getErrorDetails() != null) {
					for (ErrorDetail errorDetail : finScheduleData.getErrorDetails()) {
						FinanceDetail response = new FinanceDetail();
						response.setReturnStatus(
								APIErrorHandlerService.getFailedStatus(errorDetail.getCode(), errorDetail.getError()));
						return response;
					}
				}
				financeDetail.setFinScheduleData(finScheduleData);
				// Get the response
				financeDetail = getResponse(financeDetail, finServiceInst);

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
		} else {
			financeDetail = new FinanceDetail();
			financeDetail.setReturnStatus(APIErrorHandlerService.getFailedStatus());
		}

		logger.debug("Leaving");
		return financeDetail;
	}

	/**
	 * Method for process request object and perform deferment action
	 * 
	 * @param finServiceInstruction
	 * @param eventCode
	 * @return
	 */
	public FinanceDetail doDefferment(FinServiceInstruction finServiceInst, String eventCode) {
		logger.debug("Enteing");

		// fetch finance data
		FinanceDetail financeDetail = getFinanceDetails(finServiceInst, eventCode);

		if (financeDetail != null) {
			FinScheduleData finScheduleData = financeDetail.getFinScheduleData();
			FinanceMain financeMain = finScheduleData.getFinanceMain();

			financeMain.setEventFromDate(finServiceInst.getFromDate());
			financeMain.setEventToDate(finServiceInst.getToDate());
			financeMain.setFinSourceID(APIConstants.FINSOURCE_ID_API);
			financeMain.setRcdMaintainSts(FinanceConstants.FINSER_EVENT_UNPLANEMIH);

			if (StringUtils.equals(finServiceInst.getRecalType(), CalculationConstants.RPYCHG_TILLMDT)) {
				financeMain.setRecalFromDate(finServiceInst.getRecalFromDate());
				financeMain.setRecalToDate(finServiceInst.getToDate());
			} else if (StringUtils.equals(finServiceInst.getRecalType(), CalculationConstants.RPYCHG_ADJMDT)
					|| StringUtils.equals(finServiceInst.getRecalType(), CalculationConstants.RPYCHG_ADDTERM)) {
				financeMain.setRecalFromDate(finServiceInst.getFromDate());
				financeMain.setRecalToDate(financeMain.getMaturityDate());
			} else if (StringUtils.equals(finServiceInst.getRecalType(), CalculationConstants.RPYCHG_TILLDATE)) {
				financeMain.setRecalFromDate(finServiceInst.getRecalFromDate());
				financeMain.setRecalToDate(finServiceInst.getRecalToDate());
			} else if (StringUtils.equals(finServiceInst.getRecalType(), CalculationConstants.RPYCHG_ADDRECAL)) {
				financeMain.setRecalFromDate(finServiceInst.getFromDate());
				financeMain.setRecalToDate(financeMain.getMaturityDate());
				// financeMain.setScheduleRegenerated(true);
			}
			finServiceInst.setModuleDefiner(FinanceConstants.FINSER_EVENT_UNPLANEMIH);
			finScheduleData.setFinServiceInstruction(finServiceInst);
			try {
				// execute fee charges
				executeFeeCharges(financeDetail, finServiceInst, eventCode);
				if (financeDetail.getFinScheduleData().getErrorDetails() != null) {
					for (ErrorDetail errorDetail : financeDetail.getFinScheduleData().getErrorDetails()) {
						FinanceDetail response = new FinanceDetail();
						doEmptyResponseObject(response);
						response.setReturnStatus(
								APIErrorHandlerService.getFailedStatus(errorDetail.getCode(), errorDetail.getError()));
						return response;
					}
				}

				// call deferment service
				finScheduleData = postponementService.doUnPlannedEMIH(finScheduleData);
				if (finScheduleData.getErrorDetails() != null) {
					for (ErrorDetail errorDetail : finScheduleData.getErrorDetails()) {
						FinanceDetail response = new FinanceDetail();
						response.setReturnStatus(
								APIErrorHandlerService.getFailedStatus(errorDetail.getCode(), errorDetail.getError()));
						return response;
					}
				}
				financeDetail.setFinScheduleData(finScheduleData);
				// Get the response
				financeDetail = getResponse(financeDetail, finServiceInst);

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
		} else {
			financeDetail = new FinanceDetail();
			financeDetail.setReturnStatus(APIErrorHandlerService.getFailedStatus());
		}

		logger.debug("Leaving");
		return financeDetail;
	}

	/**
	 * Method for process add terms request and perform addTerms operations.
	 * 
	 * @param finServiceInstruction
	 * @param eventCode
	 * @return
	 */
	public FinanceDetail addTerms(FinServiceInstruction finServiceInst, String eventCode) {
		logger.debug("Enteing");

		// fetch finance data
		FinanceDetail financeDetail = getFinanceDetails(finServiceInst, eventCode);

		if (financeDetail != null) {
			FinScheduleData finScheduleData = financeDetail.getFinScheduleData();
			FinanceMain financeMain = finScheduleData.getFinanceMain();
			financeMain.setEventFromDate(finServiceInst.getRecalFromDate());
			financeMain.setRecalFromDate(finServiceInst.getRecalFromDate());
			financeMain.setRecalToDate(financeMain.getMaturityDate());
			financeMain.setEventToDate(financeMain.getMaturityDate());
			financeMain.setAdjTerms(finServiceInst.getTerms());
			financeMain.setRecalType(CalculationConstants.RPYCHG_ADDRECAL);

			financeMain.setFinSourceID(APIConstants.FINSOURCE_ID_API);
			finServiceInst.setFinEvent(FinanceConstants.FINSER_EVENT_RECALCULATE);
			financeMain.setRcdMaintainSts(FinanceConstants.FINSER_EVENT_ADDTERM);

			finServiceInst.setModuleDefiner(FinanceConstants.FINSER_EVENT_ADDTERM);

			try {
				// execute fee charges
				executeFeeCharges(financeDetail, finServiceInst, eventCode);
				if (financeDetail.getFinScheduleData().getErrorDetails() != null) {
					for (ErrorDetail errorDetail : financeDetail.getFinScheduleData().getErrorDetails()) {
						FinanceDetail response = new FinanceDetail();
						doEmptyResponseObject(response);
						response.setReturnStatus(
								APIErrorHandlerService.getFailedStatus(errorDetail.getCode(), errorDetail.getError()));
						return response;
					}
				}

				// Call Schedule calculator for Rate change
				finScheduleData = recalService.getRecalculateSchdDetails(finScheduleData);

				if (finScheduleData.getErrorDetails() != null) {
					for (ErrorDetail errorDetail : finScheduleData.getErrorDetails()) {
						FinanceDetail response = new FinanceDetail();
						doEmptyResponseObject(response);
						response.setReturnStatus(
								APIErrorHandlerService.getFailedStatus(errorDetail.getCode(), errorDetail.getError()));
						return response;
					}
				}
				financeDetail.setFinScheduleData(finScheduleData);
				// Get the response
				financeDetail = getResponse(financeDetail, finServiceInst);

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
		} else {
			financeDetail = new FinanceDetail();
			financeDetail.setReturnStatus(APIErrorHandlerService.getFailedStatus());
		}

		logger.debug("Leaving");
		return financeDetail;
	}

	/**
	 * Method for process Recalculate request and generate new schedule.
	 * 
	 * @param finServiceInstruction
	 * @param eventCode
	 * @return FinanceDetail
	 */
	public FinanceDetail doRecalculate(FinServiceInstruction finServiceInst, String eventCode) {
		logger.debug("Enteing");

		// fetch finance data
		FinanceDetail financeDetail = getFinanceDetails(finServiceInst, eventCode);

		if (financeDetail != null) {
			FinScheduleData finScheduleData = financeDetail.getFinScheduleData();
			FinanceMain financeMain = finScheduleData.getFinanceMain();

			financeMain.setAdjTerms(finServiceInst.getTerms());
			financeMain.setRecalType(finServiceInst.getRecalType());
			financeMain.setFinSourceID(APIConstants.FINSOURCE_ID_API);
			financeMain.setRcdMaintainSts(FinanceConstants.FINSER_EVENT_RECALCULATE);
			finServiceInst.setModuleDefiner(FinanceConstants.FINSER_EVENT_RECALCULATE);

			switch (finServiceInst.getRecalType()) {
			case CalculationConstants.RPYCHG_TILLMDT:
				financeMain.setRecalFromDate(finServiceInst.getRecalFromDate());
				financeMain.setRecalToDate(financeMain.getMaturityDate());
				break;
			case CalculationConstants.RPYCHG_TILLDATE:
				financeMain.setRecalFromDate(finServiceInst.getRecalFromDate());
				financeMain.setRecalToDate(finServiceInst.getRecalToDate());
				break;
			case CalculationConstants.RPYCHG_ADJMDT:
				financeMain.setRecalFromDate(finServiceInst.getRecalFromDate());
				financeMain.setRecalToDate(financeMain.getMaturityDate());
				break;
			case CalculationConstants.RPYCHG_ADDRECAL:
				financeMain.setRecalFromDate(finServiceInst.getRecalFromDate());
				financeMain.setRecalToDate(financeMain.getMaturityDate());
				financeMain.setScheduleRegenerated(true);
				break;
			default:
				break;
			}

			try {
				// execute fee charges
				executeFeeCharges(financeDetail, finServiceInst, eventCode);
				if (financeDetail.getFinScheduleData().getErrorDetails() != null) {
					for (ErrorDetail errorDetail : financeDetail.getFinScheduleData().getErrorDetails()) {
						FinanceDetail response = new FinanceDetail();
						doEmptyResponseObject(response);
						response.setReturnStatus(
								APIErrorHandlerService.getFailedStatus(errorDetail.getCode(), errorDetail.getError()));
						return response;
					}
				}
				// Call Schedule calculator for Rate change
				finScheduleData = recalService.getRecalculateSchdDetails(finScheduleData);

				if (finScheduleData.getErrorDetails() != null) {
					for (ErrorDetail errorDetail : finScheduleData.getErrorDetails()) {
						FinanceDetail response = new FinanceDetail();
						response.setReturnStatus(
								APIErrorHandlerService.getFailedStatus(errorDetail.getCode(), errorDetail.getError()));
						return response;
					}
				}
				financeDetail.setFinScheduleData(finScheduleData);
				// Get the response
				financeDetail = getResponse(financeDetail, finServiceInst);

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
		} else {
			financeDetail = new FinanceDetail();
			financeDetail.setReturnStatus(APIErrorHandlerService.getFailedStatus());
		}

		logger.debug("Leaving");
		return financeDetail;
	}

	/**
	 * Method for process change profit request and generate new schedule.
	 * 
	 * @param finServiceInstruction
	 * @param eventCode
	 * @return
	 */
	public FinanceDetail doChangeProfit(FinServiceInstruction finServiceInst, String eventCode) {
		logger.debug("Enteing");

		// fetch finance data
		FinanceDetail financeDetail = getFinanceDetails(finServiceInst, eventCode);

		if (financeDetail != null) {
			FinScheduleData finScheduleData = financeDetail.getFinScheduleData();
			FinanceMain financeMain = finScheduleData.getFinanceMain();

			financeMain.setEventFromDate(finServiceInst.getFromDate());
			financeMain.setEventToDate(finServiceInst.getToDate());
			financeMain.setPftIntact(true);
			financeMain.setFinSourceID(APIConstants.FINSOURCE_ID_API);
			financeMain.setRcdMaintainSts(FinanceConstants.FINSER_EVENT_CHGPFT);
			finServiceInst.setModuleDefiner(FinanceConstants.FINSER_EVENT_CHGPFT);
			// profit amount
			BigDecimal amount = finServiceInst.getAmount();
			try {
				// execute fee charges
				executeFeeCharges(financeDetail, finServiceInst, eventCode);
				if (financeDetail.getFinScheduleData().getErrorDetails() != null) {
					for (ErrorDetail errorDetail : financeDetail.getFinScheduleData().getErrorDetails()) {
						FinanceDetail response = new FinanceDetail();
						doEmptyResponseObject(response);
						response.setReturnStatus(
								APIErrorHandlerService.getFailedStatus(errorDetail.getCode(), errorDetail.getError()));
						return response;
					}
				}
				// Call Schedule calculator for Rate change
				finScheduleData = changeProfitService.getChangeProfitDetails(finScheduleData, amount);

				if (finScheduleData.getErrorDetails() != null) {
					for (ErrorDetail errorDetail : finScheduleData.getErrorDetails()) {
						FinanceDetail response = new FinanceDetail();
						response.setReturnStatus(
								APIErrorHandlerService.getFailedStatus(errorDetail.getCode(), errorDetail.getError()));
						return response;
					}
				}
				financeDetail.setFinScheduleData(finScheduleData);
				// Get the response
				financeDetail = getResponse(financeDetail, finServiceInst);

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
		} else {
			financeDetail = new FinanceDetail();
			financeDetail.setReturnStatus(APIErrorHandlerService.getFailedStatus());
		}

		logger.debug("Leaving");
		return financeDetail;
	}

	/**
	 * 
	 * @param finServiceInst
	 * @param eventCode
	 * @return
	 */
	public FinanceDetail doAddDisbursement(FinServiceInstruction finServiceInst, FinanceDetail financeDetail,
			String eventCode) {
		logger.debug("Entering");

		if (financeDetail != null) {
			List<ExtendedField> extendedDetailsList = finServiceInst.getExtendedDetails();
			FinScheduleData finScheduleData = financeDetail.getFinScheduleData();
			FinanceMain financeMain = finScheduleData.getFinanceMain();
			FinanceType finType = finScheduleData.getFinanceType();
			financeMain.setEventFromDate(finServiceInst.getFromDate());
			financeMain.setEventToDate(financeMain.getMaturityDate());
			financeMain.setAdjTerms(finServiceInst.getTerms());
			financeMain.setRecalSchdMethod(financeMain.getScheduleMethod());
			financeMain.setRecalType(finServiceInst.getRecalType());
			financeMain.setFinSourceID(APIConstants.FINSOURCE_ID_API);
			financeMain.setRcdMaintainSts(FinanceConstants.FINSER_EVENT_ADDDISB);

			if (StringUtils.equals(FinanceConstants.PRODUCT_ODFACILITY, financeMain.getProductCategory())) {
				financeMain.setRecalType(CalculationConstants.RPYCHG_TILLMDT);
				financeMain.setEventFromDate(finServiceInst.getFromDate());
				financeMain.setRecalFromDate(finServiceInst.getFromDate());
				financeMain.setRecalToDate(financeMain.getMaturityDate());
			}

			if (StringUtils.equals(finServiceInst.getRecalType(), CalculationConstants.RPYCHG_TILLMDT)) {
				financeMain.setRecalFromDate(finServiceInst.getRecalFromDate());
				financeMain.setRecalToDate(financeMain.getMaturityDate());
			} else if (StringUtils.equals(finServiceInst.getRecalType(), CalculationConstants.RPYCHG_ADJMDT)) {
				financeMain.setRecalFromDate(finServiceInst.getFromDate());
				financeMain.setRecalToDate(financeMain.getMaturityDate());
			} else if (StringUtils.equals(finServiceInst.getRecalType(), CalculationConstants.RPYCHG_TILLDATE)) {
				financeMain.setRecalFromDate(finServiceInst.getRecalFromDate());
				financeMain.setRecalToDate(finServiceInst.getRecalToDate());
			} else if (StringUtils.equals(finServiceInst.getRecalType(), CalculationConstants.RPYCHG_ADDTERM)) {
				financeMain.setRecalFromDate(finServiceInst.getFromDate());
				financeMain.setRecalToDate(financeMain.getMaturityDate());
			} else if (StringUtils.equals(finServiceInst.getRecalType(), CalculationConstants.RPYCHG_ADDRECAL)) {
				financeMain.setRecalFromDate(finServiceInst.getRecalFromDate());
				financeMain.setRecalToDate(financeMain.getMaturityDate());
				financeMain.setScheduleRegenerated(true);
			}

			BigDecimal amount = finServiceInst.getAmount();
			financeMain.setCurDisbursementAmt(amount);
			financeMain.setFinCurrAssetValue(financeMain.getFinCurrAssetValue().add(amount));

			/*
			 * This is not a correct logic FIX IT
			 */

			// set finAssetValue = FinCurrAssetValue when there is no
			// maxDisbCheck
			/*
			 * FinanceType finType = financeDetail.getFinScheduleData().getFinanceType();
			 * if(!finType.isAlwMaxDisbCheckReq()) { financeMain.setFinAssetValue(financeMain.getFinCurrAssetValue()); }
			 */

			finServiceInst.setModuleDefiner(FinanceConstants.FINSER_EVENT_ADDDISB);
			try {
				// execute fee charges
				executeFeeCharges(financeDetail, finServiceInst, eventCode);
				if (financeDetail.getFinScheduleData().getErrorDetails() != null) {
					for (ErrorDetail errorDetail : financeDetail.getFinScheduleData().getErrorDetails()) {
						FinanceDetail response = new FinanceDetail();
						doEmptyResponseObject(response);
						response.setReturnStatus(
								APIErrorHandlerService.getFailedStatus(errorDetail.getCode(), errorDetail.getError()));
						return response;
					}
				}
				// added new disbursement details
				int seqNo = finScheduleData.getDisbursementDetails().size() + 1;
				FinanceDisbursement disbursementDetails = new FinanceDisbursement();
				disbursementDetails.setDisbDate(finServiceInst.getFromDate());
				disbursementDetails.setDisbAmount(amount);
				disbursementDetails.setDisbSeq(seqNo);
				disbursementDetails.setDisbReqDate(DateUtility.getAppDate());
				disbursementDetails.setFeeChargeAmt(financeMain.getFeeChargeAmt());
				disbursementDetails.setInsuranceAmt(financeMain.getInsuranceAmt());
				disbursementDetails
						.setDisbAccountId(PennantApplicationUtil.unFormatAccountNumber(financeMain.getDisbAccountId()));
				List<FinanceDisbursement> list = new ArrayList<FinanceDisbursement>();
				list.add(disbursementDetails);

				if (financeDetail.getAdvancePaymentsList() != null
						&& !financeDetail.getAdvancePaymentsList().isEmpty()) {
					for (FinAdvancePayments advPayments : financeDetail.getAdvancePaymentsList()) {
						if (advPayments.getDisbSeq() == 0) {
							advPayments.setDisbSeq(seqNo);
						}
					}

					// validate disbursement instructions
					List<ErrorDetail> errors = finAdvancePaymentsService.validateFinAdvPayments(
							financeDetail.getAdvancePaymentsList(), list, finScheduleData.getFinanceMain(), true);
					for (ErrorDetail erroDetails : errors) {
						finScheduleData.setErrorDetail(ErrorUtil
								.getErrorDetail(new ErrorDetail(erroDetails.getCode(), erroDetails.getParameters())));
					}
				}

				//Overdraft schedule details should be calculated before finschduledetails is empty.
				if (StringUtils.equals(FinanceConstants.PRODUCT_ODFACILITY, financeMain.getProductCategory())
						&& (finScheduleData.getFinanceScheduleDetails() == null
								|| finScheduleData.getFinanceScheduleDetails().isEmpty())) {
					financeDetail.setFinScheduleData(ScheduleGenerator.getNewSchd(financeDetail.getFinScheduleData()));
				}

				// Call Schedule calculator for add disbursement
				if (finScheduleData.getErrorDetails() == null || finScheduleData.getErrorDetails().isEmpty()) {
					finScheduleData = addDisbursementService.getAddDisbDetails(finScheduleData, amount, BigDecimal.ZERO,
							false, FinanceConstants.FINSER_EVENT_ADDDISB);
				}

				if (finScheduleData.getErrorDetails() != null) {
					for (ErrorDetail errorDetail : finScheduleData.getErrorDetails()) {
						FinanceDetail response = new FinanceDetail();
						doEmptyResponseObject(response);
						response.setReturnStatus(
								APIErrorHandlerService.getFailedStatus(errorDetail.getCode(), errorDetail.getError()));
						return response;
					}
				}
				if (finScheduleData.getErrorDetails() != null) {
					if (extendedDetailsList != null && extendedDetailsList.size() > 0) {
						addExtendedFields(finServiceInst, finType, FinanceConstants.FINSER_EVENT_ADDDISB, "ADSB");
					}
				}
				financeDetail.setFinScheduleData(finScheduleData);

				// process disbursement details
				LoggedInUser userDetails = SessionUserDetails.getUserDetails(SessionUserDetails.getLogiedInUser());
				// financeDetail.getFinScheduleData().getFinanceMain().setRecordType(PennantConstants.RECORD_TYPE_NEW);
				List<FinAdvancePayments> advancePayments = financeDetail.getAdvancePaymentsList();
				if (advancePayments != null) {
					int seq = 1;
					for (FinAdvancePayments advPayment : advancePayments) {
						int paymentSeq = finAdvancePaymentsService
								.getCountByFinReference(financeMain.getFinReference());
						seq = seq + paymentSeq;
						advPayment.setFinReference(financeMain.getFinReference());
						advPayment.setRecordType(PennantConstants.RECORD_TYPE_NEW);
						advPayment.setNewRecord(true);
						advPayment.setLastMntBy(userDetails.getUserId());
						advPayment.setLastMntOn(new Timestamp(System.currentTimeMillis()));
						advPayment.setRecordStatus(PennantConstants.RCD_STATUS_APPROVED);
						advPayment.setUserDetails(financeMain.getUserDetails());
						advPayment.setPaymentSeq(seq);
						advPayment.setDisbCCy(financeMain.getFinCcy());
						seq++;

						if (StringUtils.equals(advPayment.getPaymentType(), DisbursementConstants.PAYMENT_TYPE_IMPS)
								|| StringUtils.equals(advPayment.getPaymentType(),
										DisbursementConstants.PAYMENT_TYPE_NEFT)
								|| StringUtils.equals(advPayment.getPaymentType(),
										DisbursementConstants.PAYMENT_TYPE_RTGS)
								|| StringUtils.equals(advPayment.getPaymentType(),
										DisbursementConstants.PAYMENT_TYPE_IFT)) {

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
				financeDetail.setFinScheduleData(finScheduleData);
				// Get the response
				financeDetail = getResponse(financeDetail, finServiceInst);

				// set Last disbursement date for Inquiry service
				if (StringUtils.equals(finServiceInst.getReqType(), APIConstants.REQTYPE_INQUIRY)) {
					financeDetail.getFinScheduleData().getFinanceSummary()
							.setLastDisbDate(disbursementDetails.getDisbDate());
				}

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
		} else {
			financeDetail = new FinanceDetail();
			financeDetail.setReturnStatus(APIErrorHandlerService.getFailedStatus());
		}

		logger.debug("Leaving");
		return financeDetail;
	}

	private void addExtendedFields(FinServiceInstruction finServiceInst, FinanceType finType, String event,
			String code) {
		ExtendedFieldHeader extendedFieldHeader = extendedFieldHeaderDAO.getExtendedFieldHeaderByModuleName(
				ExtendedFieldConstants.MODULE_LOAN, finType.getFinCategory(), event, "");
		List<ExtendedField> extendedFields = finServiceInst.getExtendedDetails();
		LoggedInUser userDetails = SessionUserDetails.getUserDetails(SessionUserDetails.getLogiedInUser());
		Map<String, Object> prvExtFieldMap = new HashMap<>();
		StringBuilder tableName = new StringBuilder();
		tableName.append(ExtendedFieldConstants.MODULE_LOAN);
		tableName.append("_");
		tableName.append(finType.getFinCategory());
		tableName.append("_");
		tableName.append(code);
		tableName.append("_ED");
		if (extendedFieldHeader != null) {
			int sequenceNo = 0;

			ExtendedFieldRender exdFieldRender = new ExtendedFieldRender();
			exdFieldRender.setReference(finServiceInst.getFinReference());
			exdFieldRender.setLastMntOn(new Timestamp(System.currentTimeMillis()));
			exdFieldRender.setRecordStatus(PennantConstants.RCD_STATUS_APPROVED);
			exdFieldRender.setLastMntBy(userDetails.getUserId());
			exdFieldRender.setSeqNo(++sequenceNo);
			exdFieldRender.setTypeCode(extendedFieldHeader.getSubModuleName());
			List<ExtendedFieldData> prvExtendedFields = new ArrayList<>(1);
			ExtendedFieldHeader curExtendedFieldHeader = extendedFieldHeaderDAO.getExtendedFieldHeaderByModuleName(
					ExtendedFieldConstants.MODULE_LOAN, finType.getFinCategory(), event, "");
			if (curExtendedFieldHeader != null) {
				ExtendedFieldRender extendedFieldRender = extendedFieldDetailsService.getExtendedFieldRender(
						ExtendedFieldConstants.MODULE_LOAN, finType.getFinCategory() + "_" + code,
						finServiceInst.getFinReference());
				if (extendedFieldRender != null && extendedFieldRender.getMapValues() != null) {
					prvExtFieldMap = extendedFieldRender.getMapValues();
					for (Map.Entry<String, Object> entry : prvExtFieldMap.entrySet()) {
						ExtendedFieldData data = new ExtendedFieldData();
						data.setFieldName(entry.getKey());
						data.setFieldValue(entry.getValue());
						prvExtendedFields.add(data);
					}
				}
				exdFieldRender.setNewRecord(false);
				boolean checkFinReference = extendedFieldRenderDAO.isExists(finServiceInst.getFinReference(), 1,
						tableName.toString());
				if (checkFinReference) {
					exdFieldRender.setRecordType(PennantConstants.RECORD_TYPE_UPD);
				} else {
					exdFieldRender.setRecordType(PennantConstants.RECORD_TYPE_NEW);
				}

				exdFieldRender.setVersion(extendedFieldRender.getVersion() + 1);
				exdFieldRender.setLastMntOn(new Timestamp(System.currentTimeMillis()));
			}
			if (extendedFields != null) {
				for (ExtendedField extendedField : extendedFields) {
					Map<String, Object> mapValues = new HashMap<String, Object>();
					mapValues.put("Reference", exdFieldRender.getReference());
					mapValues.put("SeqNo", exdFieldRender.getSeqNo());
					mapValues.put("Version", exdFieldRender.getVersion());
					mapValues.put("LastMntBy", exdFieldRender.getLastMntBy());
					mapValues.put("LastMntOn", exdFieldRender.getLastMntOn());
					mapValues.put("RecordStatus", PennantConstants.RCD_STATUS_APPROVED);
					mapValues.put("RoleCode", "");
					mapValues.put("NextRoleCode", "");
					mapValues.put("TaskId", "");
					mapValues.put("NextTaskId", "");
					mapValues.put("RecordType", "");
					mapValues.put("WorkflowId", 0);
					//mapValues.put("adfdaf", "");

					if (extendedField.getExtendedFieldDataList() != null) {
						for (ExtendedFieldData extFieldData : extendedField.getExtendedFieldDataList()) {
							mapValues.put(extFieldData.getFieldName(), extFieldData.getFieldValue());
							exdFieldRender.setMapValues(mapValues);
						}
					} else {
						Map<String, Object> map = new HashMap<String, Object>();
						exdFieldRender.setMapValues(map);
					}
				}
				if (extendedFields.isEmpty()) {
					Map<String, Object> mapValues = new HashMap<String, Object>();
					exdFieldRender.setMapValues(mapValues);
				}
			} else {
				Map<String, Object> mapValues = new HashMap<String, Object>();
				exdFieldRender.setMapValues(mapValues);
			}
			if (exdFieldRender.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)) {
				extendedFieldRenderDAO.save(exdFieldRender.getMapValues(), "", tableName.toString());
			} else {
				extendedFieldRenderDAO.update(finServiceInst.getFinReference(), 1, exdFieldRender.getMapValues(), "",
						tableName.toString());
			}

		}
	}

	public FinanceDetail doCancelDisbursement(FinServiceInstruction finServiceInst, FinanceDetail financeDetail,
			String eventCode) {
		logger.debug("Enteing");

		if (financeDetail != null) {

			FinScheduleData finScheduleData = financeDetail.getFinScheduleData();
			FinanceMain financeMain = finScheduleData.getFinanceMain();

			finServiceInst.setFinEvent(FinanceConstants.FINSER_EVENT_CANCELDISB);

			// Disbursement Details Correction
			List<FinanceDisbursement> list = finScheduleData.getDisbursementDetails();
			for (int i = 0; i < list.size(); i++) {
				FinanceDisbursement disbursement = list.get(i);
				if (disbursement.getDisbDate().compareTo(finServiceInst.getFromDate()) == 0) {
					disbursement.setDisbStatus(FinanceConstants.DISB_STATUS_CANCEL);
					break;
				}
			}

			// Schedule Data disbursement Amount Correction
			finScheduleData.setDisbursementDetails(list);
			Date eventFromDate = null;
			for (int i = 0; i < finScheduleData.getFinanceScheduleDetails().size(); i++) {
				FinanceScheduleDetail curSchd = finScheduleData.getFinanceScheduleDetails().get(i);
				if (curSchd.getSchDate().compareTo(finServiceInst.getFromDate()) == 0) {
					if (curSchd.isDisbOnSchDate()) {
						curSchd.setDisbAmount(curSchd.getDisbAmount().subtract(finServiceInst.getAmount()));
						if (curSchd.getDisbAmount().compareTo(BigDecimal.ZERO) == 0) {
							curSchd.setDisbOnSchDate(false);
						} else {
							curSchd.setDisbOnSchDate(true);
						}
						eventFromDate = finScheduleData.getFinanceScheduleDetails().get(i).getSchDate();
					}

					if (!curSchd.isDisbOnSchDate() && !curSchd.isRepayOnSchDate() && !curSchd.isPftOnSchDate()
							&& !curSchd.isRvwOnSchDate() && !curSchd.isCpzOnSchDate()) {
						eventFromDate = finScheduleData.getFinanceScheduleDetails().get(i + 1).getSchDate();
						finScheduleData.getFinanceScheduleDetails().remove(i);
						i--;
					}
					break;
				}
			}

			//subtract from FinCurrAssetValue to CurDisbursementAmt and set value to FinCurrAssetValue
			BigDecimal amount = finServiceInst.getAmount();
			financeMain.setCurDisbursementAmt(amount);
			financeMain.setFinCurrAssetValue(financeMain.getFinCurrAssetValue().subtract(amount));

			financeMain.setEventFromDate(eventFromDate);
			financeMain.setEventToDate(financeMain.getMaturityDate());
			financeMain.setRecalFromDate(finServiceInst.getFromDate());
			financeMain.setRecalToDate(financeMain.getMaturityDate());

			// Service details calling for Schedule calculation
			finScheduleData = cancelDisbursementService.getCancelDisbDetails(finScheduleData);
			finServiceInst.setPftChg(finScheduleData.getPftChg());
			finScheduleData.getFinanceMain().resetRecalculationFields();
			finScheduleData.setFinServiceInstruction(finServiceInst);

			// Show Error Details in Schedule Maintenance
			if (finScheduleData.getErrorDetails() != null && !finScheduleData.getErrorDetails().isEmpty()) {
				MessageUtil.showError(finScheduleData.getErrorDetails().get(0));
				finScheduleData.getErrorDetails().clear();
			} else {
				finScheduleData.setSchduleGenerated(true);
			}

			try {
				// Get the response and set the value for RcdMaintainsts and for ModuleDefiner 
				financeDetail.setModuleDefiner("CancelDisbursement");
				financeDetail.getFinScheduleData().getFinanceMain()
						.setRcdMaintainSts(FinanceConstants.FINSER_EVENT_CANCELDISB);
				financeDetail = getResponse(financeDetail, finServiceInst);
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
		} else

		{
			financeDetail = new FinanceDetail();
			financeDetail.setReturnStatus(APIErrorHandlerService.getFailedStatus());
		}

		logger.debug("Leaving");
		return financeDetail;
	}

	/**
	 * 
	 * 
	 * @param finServiceInst
	 * @param eventCode
	 * @return
	 */
	public FinanceDetail doChangeFrequency(FinServiceInstruction finServiceInst, String eventCode) {
		logger.debug("Enteing");

		// fetch finance data
		FinanceDetail financeDetail = getFinanceDetails(finServiceInst, eventCode);

		if (financeDetail != null) {
			FinScheduleData finScheduleData = financeDetail.getFinScheduleData();
			String repayFrq = finScheduleData.getFinanceMain().getRepayFrq();
			String frqday = String.valueOf(finServiceInst.getFrqDay());
			frqday = frqday.length() == 1 ? "0".concat(frqday) : frqday;
			finServiceInst.setRepayFrq(StringUtils.substring(repayFrq, 0, repayFrq.length() - 2).concat(frqday));

			int rpyTermsCompleted = 0;
			int adjRepayTerms = 0;
			int totRepayTerms = 0;
			boolean isFromDateFound = false;
			Date fromDate = DateUtility
					.getDBDate(DateUtility.formatDate(finServiceInst.getFromDate(), PennantConstants.DBDateFormat));
			finServiceInst.setFromDate(fromDate);
			List<FinanceScheduleDetail> financeScheduleDetails = finScheduleData.getFinanceScheduleDetails();
			if (financeScheduleDetails != null) {
				for (int i = 0; i < financeScheduleDetails.size(); i++) {
					FinanceScheduleDetail curSchd = financeScheduleDetails.get(i);
					if (curSchd.isRepayOnSchDate()
							|| (curSchd.isPftOnSchDate() && curSchd.getRepayAmount().compareTo(BigDecimal.ZERO) > 0)) {
						if (fromDate.compareTo(curSchd.getSchDate()) == 0) {
							isFromDateFound = true;
						}

						totRepayTerms = totRepayTerms + 1;
						if (!isFromDateFound) {
							if (curSchd.getSchDate()
									.compareTo(finScheduleData.getFinanceMain().getGrcPeriodEndDate()) > 0) {
								rpyTermsCompleted = rpyTermsCompleted + 1;
							}
						}
					}
				}
				adjRepayTerms = totRepayTerms - rpyTermsCompleted;
			}

			finServiceInst.setAdjRpyTerms(adjRepayTerms);
			finScheduleData.getFinanceMain().setFinSourceID(APIConstants.FINSOURCE_ID_API);
			finScheduleData.getFinanceMain().setRcdMaintainSts(FinanceConstants.FINSER_EVENT_CHGFRQ);
			finServiceInst.setModuleDefiner(FinanceConstants.FINSER_EVENT_CHGFRQ);
			try {
				// execute fee charges
				executeFeeCharges(financeDetail, finServiceInst, eventCode);
				if (financeDetail.getFinScheduleData().getErrorDetails() != null) {
					for (ErrorDetail errorDetail : financeDetail.getFinScheduleData().getErrorDetails()) {
						FinanceDetail response = new FinanceDetail();
						doEmptyResponseObject(response);
						response.setReturnStatus(
								APIErrorHandlerService.getFailedStatus(errorDetail.getCode(), errorDetail.getError()));
						return response;
					}
				}
				// call change frequency service
				finScheduleData = changeFrequencyService.doChangeFrequency(finScheduleData, finServiceInst);
				financeDetail.setFinScheduleData(finScheduleData);

				// Get the response
				financeDetail = getResponse(financeDetail, finServiceInst);

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
		} else {
			financeDetail = new FinanceDetail();
			financeDetail.setReturnStatus(APIErrorHandlerService.getFailedStatus());
		}

		logger.debug("Leaving");
		return financeDetail;
	}

	/**
	 * Method for schedule terms
	 * 
	 * @param finServiceInstruction
	 * @param eventCode
	 * @return FinanceDetail
	 */
	public FinanceDetail removeTerms(FinServiceInstruction finServiceInst, String eventCode) {
		logger.debug("Enteing");

		// fetch finance data
		FinanceDetail financeDetail = getFinanceDetails(finServiceInst, eventCode);
		if (financeDetail != null) {
			FinScheduleData finScheduleData = financeDetail.getFinScheduleData();

			FinanceMain financeMain = finScheduleData.getFinanceMain();
			financeMain.setEventFromDate(finServiceInst.getFromDate());
			financeMain.setRecalFromDate(finServiceInst.getRecalFromDate());
			financeMain.setRecalType(finServiceInst.getRecalType());
			financeMain.setRecalSchdMethod(financeMain.getScheduleMethod());
			financeMain.setFinSourceID(APIConstants.FINSOURCE_ID_API);
			financeMain.setRcdMaintainSts(FinanceConstants.FINSER_EVENT_RMVTERM);
			finServiceInst.setModuleDefiner(FinanceConstants.FINSER_EVENT_RMVTERM);

			if (StringUtils.equals(finServiceInst.getRecalType(), CalculationConstants.RPYCHG_TILLMDT)) {
				financeMain.setRecalFromDate(finServiceInst.getRecalFromDate());
			} else if (StringUtils.equals(finServiceInst.getRecalType(), CalculationConstants.RPYCHG_ADJMDT)) {
				financeMain.setRecalFromDate(finServiceInst.getFromDate());
			}

			try {
				// execute fee charges
				executeFeeCharges(financeDetail, finServiceInst, eventCode);
				if (financeDetail.getFinScheduleData().getErrorDetails() != null) {
					for (ErrorDetail errorDetail : financeDetail.getFinScheduleData().getErrorDetails()) {
						FinanceDetail response = new FinanceDetail();
						doEmptyResponseObject(response);
						response.setReturnStatus(
								APIErrorHandlerService.getFailedStatus(errorDetail.getCode(), errorDetail.getError()));
						return response;
					}
				}
				// Call Schedule calculator for Rate change
				finScheduleData = rmvTermsService.getRmvTermsDetails(finScheduleData);
				if (finScheduleData.getErrorDetails() != null) {
					for (ErrorDetail errorDetail : finScheduleData.getErrorDetails()) {
						FinanceDetail response = new FinanceDetail();
						response.setReturnStatus(
								APIErrorHandlerService.getFailedStatus(errorDetail.getCode(), errorDetail.getError()));

						return response;
					}
				}
				financeDetail.setFinScheduleData(finScheduleData);
				// Get the response
				financeDetail = getResponse(financeDetail, finServiceInst);

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
		} else {
			financeDetail = new FinanceDetail();
			financeDetail.setReturnStatus(APIErrorHandlerService.getFailedStatus());
		}

		logger.debug("Leaving");
		return financeDetail;
	}

	/**
	 * Method for process and do Reschedule action
	 * 
	 * @param finServiceInstruction
	 * @param eventCode
	 * @return FinanceDetail
	 */
	public FinanceDetail doReSchedule(FinServiceInstruction finServiceInst, String eventCode) {
		logger.debug("Enteing");

		// fetch finance data
		FinanceDetail financeDetail = getFinanceDetails(finServiceInst, eventCode);
		if (financeDetail != null) {
			FinScheduleData finScheduleData = financeDetail.getFinScheduleData();

			// tempStartDate
			List<FinanceScheduleDetail> financeScheduleDetails = null;
			financeScheduleDetails = financeDetail.getFinScheduleData().getFinanceScheduleDetails();
			if (financeScheduleDetails != null) {
				for (int i = 0; i < financeScheduleDetails.size(); i++) {
					FinanceScheduleDetail curSchd = financeScheduleDetails.get(i);
					if (curSchd.isRepayOnSchDate()
							|| (curSchd.isPftOnSchDate() && curSchd.getRepayAmount().compareTo(BigDecimal.ZERO) > 0)) {
						if (finServiceInst.getFromDate().compareTo(curSchd.getSchDate()) == 0) {
							break;
						}
					}
				}
			}

			FinanceMain financeMain = finScheduleData.getFinanceMain();
			financeMain.setRecalFromDate(finServiceInst.getFromDate());
			financeMain.setEventFromDate(finServiceInst.getFromDate());
			financeMain.setFinSourceID(APIConstants.FINSOURCE_ID_API);
			financeMain.setRcdMaintainSts(FinanceConstants.FINSER_EVENT_RESCHD);
			finServiceInst.setModuleDefiner(FinanceConstants.FINSER_EVENT_RESCHD);

			try {
				// execute fee charges
				executeFeeCharges(financeDetail, finServiceInst, eventCode);
				if (financeDetail.getFinScheduleData().getErrorDetails() != null) {
					for (ErrorDetail errorDetail : financeDetail.getFinScheduleData().getErrorDetails()) {
						FinanceDetail response = new FinanceDetail();
						doEmptyResponseObject(response);
						response.setReturnStatus(
								APIErrorHandlerService.getFailedStatus(errorDetail.getCode(), errorDetail.getError()));
						return response;
					}
				}
				// Call Schedule calculator for Rate change
				finScheduleData = reScheduleService.doReSchedule(finScheduleData, finServiceInst);
				if (finScheduleData.getErrorDetails() != null) {
					for (ErrorDetail errorDetail : finScheduleData.getErrorDetails()) {
						FinanceDetail response = new FinanceDetail();
						response.setReturnStatus(
								APIErrorHandlerService.getFailedStatus(errorDetail.getCode(), errorDetail.getError()));
						return response;
					}
				}
				financeDetail.setFinScheduleData(finScheduleData);
				// Get the response
				financeDetail = getResponse(financeDetail, finServiceInst);

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
		} else {
			financeDetail = new FinanceDetail();
			financeDetail.setReturnStatus(APIErrorHandlerService.getFailedStatus());
		}

		logger.debug("Leaving");
		return financeDetail;
	}

	/**
	 * Process early settlement request and generate new schedule
	 * 
	 * @param finServiceInstruction
	 * @param eventCode
	 * @return FinanceDetail
	 */
	public FinanceDetail doEarlySettlement(FinServiceInstruction finServiceInst, String eventCode) {
		logger.debug("Enteing");

		// fetch finance data
		FinanceDetail financeDetail = getFinanceDetails(finServiceInst, eventCode);
		FinScheduleData finScheduleData = financeDetail.getFinScheduleData();
		finServiceInst.setModuleDefiner(FinanceConstants.FINSER_EVENT_EARLYSETTLE);
		if (StringUtils.equals(finServiceInst.getReqType(), APIConstants.REQTYPE_INQUIRY)) {
			if (finServiceInst.getToDate() == null) {
				finServiceInst.setToDate(finScheduleData.getFinanceMain().getMaturityDate());
			}
		}

		FinanceDetail response = null;
		try {
			// execute fee charges
			executeFeeCharges(financeDetail, finServiceInst, eventCode);
			if (financeDetail.getFinScheduleData().getErrorDetails() != null) {
				for (ErrorDetail errorDetail : financeDetail.getFinScheduleData().getErrorDetails()) {
					response = new FinanceDetail();
					doEmptyResponseObject(response);
					response.setReturnStatus(
							APIErrorHandlerService.getFailedStatus(errorDetail.getCode(), errorDetail.getError()));
					return response;
				}
			}
			response = doProcessReceipt(financeDetail, finServiceInst, finServiceInst.getModuleDefiner());
			if (response != null && response.getFinScheduleData() != null) {
				FinanceSummary summary = response.getFinScheduleData().getFinanceSummary();
				summary.setFinStatus("M");
			}
		} catch (InterfaceException ex) {
			logger.error("InterfaceException", ex);
			response = new FinanceDetail();
			doEmptyResponseObject(response);
			response.setReturnStatus(APIErrorHandlerService.getFailedStatus("9998", ex.getMessage()));
			return response;
		} catch (AppException appEx) {
			logger.error("AppException", appEx);
			response = new FinanceDetail();
			doEmptyResponseObject(response);
			response.setReturnStatus(APIErrorHandlerService.getFailedStatus("9999", appEx.getMessage()));
			return response;
		} catch (Exception e) {
			logger.error("Exception", e);
			APIErrorHandlerService.logUnhandledException(e);
			response = new FinanceDetail();
			doEmptyResponseObject(response);
			response.setReturnStatus(APIErrorHandlerService.getFailedStatus());
			return response;
		}

		logger.debug("Leaving");
		return response;
	}

	/**
	 * Process Partial payment request and generate new schedule
	 * 
	 * @param finServiceInstruction
	 * @param eventCode
	 * @return FinanceDeail
	 */
	public FinanceDetail doPartialSettlement(FinServiceInstruction finServiceInst, String eventCode) {
		logger.debug("Enteing");

		// fetch finance data
		FinanceDetail financeDetail = getFinanceDetails(finServiceInst, eventCode);
		FinScheduleData finScheduleData = financeDetail.getFinScheduleData();
		FinanceType finType = finScheduleData.getFinanceType();
		List<ExtendedField> extendedDetailsList = finServiceInst.getExtendedDetails();

		finServiceInst.setModuleDefiner(FinanceConstants.FINSER_EVENT_EARLYRPY);
		boolean isOverDraft = false;
		FinanceDetail response = null;

		if (StringUtils.equals(FinanceConstants.PRODUCT_ODFACILITY,
				financeDetail.getFinScheduleData().getFinanceMain().getProductCategory())) {
			isOverDraft = true;
		}

		if (isOverDraft && !StringUtils.equals(CalculationConstants.EARLYPAY_ADJMUR, finServiceInst.getRecalType())) {
			response = new FinanceDetail();
			String[] valueParm = new String[2];
			valueParm[0] = "Recal type code";
			valueParm[1] = CalculationConstants.EARLYPAY_ADJMUR;
			doEmptyResponseObject(response);
			response.setReturnStatus(APIErrorHandlerService.getFailedStatus("90281", valueParm));
			return response;

		}

		if (extendedDetailsList != null && extendedDetailsList.size() > 0) {
			addExtendedFields(finServiceInst, finType, FinanceConstants.FINSER_EVENT_RECEIPT, "RCPT");
		}

		FinReceiptDetail finReceiptDetail = finServiceInst.getReceiptDetail();
		if (finReceiptDetail == null) {
			finReceiptDetail = new FinReceiptDetail();
			finReceiptDetail.setReceivedDate(DateUtility.getAppDate());
			finServiceInst.setReceiptDetail(finReceiptDetail);
		} else {
			if (finReceiptDetail.getReceivedDate() == null) {
				finReceiptDetail.setReceivedDate(DateUtility.getAppDate());
			}
		}

		try {

			// calculate PartPayment amount
			Map<String, String> responseMap = validateRepayAmount(financeDetail.getFinScheduleData(), finServiceInst,
					finServiceInst.getAmount());
			finServiceInst.setRemPartPayAmt(new BigDecimal(responseMap.get("partPaidAmt")));

			// execute fee charges
			executeFeeCharges(financeDetail, finServiceInst, eventCode);
			if (financeDetail.getFinScheduleData().getErrorDetails() != null) {
				for (ErrorDetail errorDetail : financeDetail.getFinScheduleData().getErrorDetails()) {
					response = new FinanceDetail();
					doEmptyResponseObject(response);
					response.setReturnStatus(
							APIErrorHandlerService.getFailedStatus(errorDetail.getCode(), errorDetail.getError()));
					return response;
				}
			}
			response = doProcessReceipt(financeDetail, finServiceInst, FinanceConstants.FINSER_EVENT_EARLYRPY);
		} catch (InterfaceException ex) {
			logger.error("InterfaceException", ex);
			response = new FinanceDetail();
			doEmptyResponseObject(response);
			response.setReturnStatus(APIErrorHandlerService.getFailedStatus("9998", ex.getMessage()));
			return response;
		} catch (AppException appEx) {
			logger.error("AppException", appEx);
			response = new FinanceDetail();
			doEmptyResponseObject(response);
			response.setReturnStatus(APIErrorHandlerService.getFailedStatus("9999", appEx.getMessage()));
			return response;
		} catch (Exception e) {
			logger.error("Exception", e);
			APIErrorHandlerService.logUnhandledException(e);
			response = new FinanceDetail();
			doEmptyResponseObject(response);
			response.setReturnStatus(APIErrorHandlerService.getFailedStatus());
			return response;
		}

		logger.debug("Leaving");
		return response;
	}

	/**
	 * 
	 * @param finServiceInstruction
	 * @param eventCode
	 * @return
	 */
	public FinanceDetail doManualPayment(FinServiceInstruction finServiceInst, String eventCode) {
		logger.debug("Enteing");

		// fetch finance data
		FinanceDetail financeDetail = getFinanceDetails(finServiceInst, eventCode);
		finServiceInst.setModuleDefiner(FinanceConstants.FINSER_EVENT_SCHDRPY);

		FinanceDetail response = null;
		try {
			// execute fee charges
			executeFeeCharges(financeDetail, finServiceInst, eventCode);
			if (financeDetail.getFinScheduleData().getErrorDetails() != null) {
				for (ErrorDetail errorDetail : financeDetail.getFinScheduleData().getErrorDetails()) {
					response = new FinanceDetail();
					doEmptyResponseObject(response);
					response.setReturnStatus(
							APIErrorHandlerService.getFailedStatus(errorDetail.getCode(), errorDetail.getError()));
					return response;
				}
			}
			response = doProcessReceipt(financeDetail, finServiceInst, FinanceConstants.FINSER_EVENT_SCHDRPY);

		} catch (InterfaceException ex) {
			logger.error("InterfaceException", ex);
			response = new FinanceDetail();
			doEmptyResponseObject(response);
			response.setReturnStatus(APIErrorHandlerService.getFailedStatus("9998", ex.getMessage()));
			return response;
		} catch (AppException appEx) {
			logger.error("AppException", appEx);
			response = new FinanceDetail();
			doEmptyResponseObject(response);
			response.setReturnStatus(APIErrorHandlerService.getFailedStatus("9999", appEx.getMessage()));
			return response;
		} catch (Exception e) {
			logger.error("Exception", e);
			APIErrorHandlerService.logUnhandledException(e);
			response = new FinanceDetail();
			doEmptyResponseObject(response);
			response.setReturnStatus(APIErrorHandlerService.getFailedStatus());
			return response;
		}

		logger.debug("Leaving");
		return response;
	}

	private FinanceDetail doProcessReceipt(FinanceDetail aFinanceDetail, FinServiceInstruction finServiceInst,
			String purpose) throws Exception {
		logger.debug("Entering");

		if (finServiceInst.getFromDate() == null) {
			finServiceInst.setFromDate(DateUtility.getAppDate());
		}

		LoggedInUser userDetails = SessionUserDetails.getUserDetails(SessionUserDetails.getLogiedInUser());

		Cloner cloner = new Cloner();
		FinanceDetail financeDetail = cloner.deepClone(aFinanceDetail);
		FinScheduleData finScheduleData = financeDetail.getFinScheduleData();

		String rpyHierarchy = finScheduleData.getFinanceType().getRpyHierarchy();
		finScheduleData.getFinanceType().setRpyHierarchy(rpyHierarchy);
		finScheduleData.getFinanceMain().setRcdMaintainSts(purpose);

		FinReceiptData finReceiptData = new FinReceiptData();
		FinReceiptHeader receiptHeader = new FinReceiptHeader();
		
		long receiptId=getFinReceiptHeaderDAO().generatedReceiptID(receiptHeader);
		receiptHeader.setReceiptID(receiptId);
		receiptHeader.setReference(finServiceInst.getFinReference());
		receiptHeader.setExcessAdjustTo(finServiceInst.getExcessAdjustTo());
		receiptHeader.setReceiptType(RepayConstants.RECEIPTTYPE_RECIPT);
		receiptHeader.setRecAgainst(RepayConstants.RECEIPTTO_FINANCE);
		receiptHeader.setReceiptDate(DateUtility.getAppDate());
		receiptHeader.setReceiptPurpose(purpose);
		receiptHeader.setEffectSchdMethod(finServiceInst.getRecalType());
		if (StringUtils.equals(purpose, FinanceConstants.FINSER_EVENT_SCHDRPY)
				|| StringUtils.equals(purpose, FinanceConstants.FINSER_EVENT_EARLYSETTLE)) {
			if (StringUtils.isBlank(receiptHeader.getExcessAdjustTo())) {
				receiptHeader.setExcessAdjustTo(RepayConstants.EXCESSADJUSTTO_EXCESS);
			}
		} else {
			receiptHeader.setExcessAdjustTo(PennantConstants.List_Select);
		}
		receiptHeader.setAllocationType(RepayConstants.ALLOCATIONTYPE_AUTO);
		receiptHeader.setReceiptAmount(finServiceInst.getAmount());
		receiptHeader.setReceiptMode(finServiceInst.getPaymentMode());

		receiptHeader.setRecordType(PennantConstants.RECORD_TYPE_NEW);
		receiptHeader.setNewRecord(true);
		receiptHeader.setLastMntBy(userDetails.getUserId());
		receiptHeader.setLastMntOn(new Timestamp(System.currentTimeMillis()));
		receiptHeader.setRecordStatus(PennantConstants.RCD_STATUS_APPROVED);
		receiptHeader.setUserDetails(userDetails);

		FinReceiptDetail finReceiptDetail = finServiceInst.getReceiptDetail();
		if (finReceiptDetail == null) {
			finReceiptDetail = new FinReceiptDetail();
			finReceiptDetail.setReceivedDate(DateUtility.getAppDate());
			if (StringUtils.equals(finServiceInst.getPaymentMode(), DisbursementConstants.PAYMENT_TYPE_CHEQUE)
					|| StringUtils.equals(finServiceInst.getPaymentMode(), DisbursementConstants.PAYMENT_TYPE_DD)) {
				finReceiptDetail.setDepositDate((DateUtility
						.getDBDate(DateUtility.formatDate(DateUtility.getAppDate(), PennantConstants.DBDateFormat))));
			}
			finServiceInst.setReceiptDetail(finReceiptDetail);
		} else {
			if (finReceiptDetail.getReceivedDate() == null) {
				finReceiptDetail.setReceivedDate(DateUtility.getAppDate());
			}
			if (StringUtils.equals(finServiceInst.getPaymentMode(), DisbursementConstants.PAYMENT_TYPE_CHEQUE)
					|| StringUtils.equals(finServiceInst.getPaymentMode(), DisbursementConstants.PAYMENT_TYPE_DD)) {
				finReceiptDetail.setDepositDate((DateUtility
						.getDBDate(DateUtility.formatDate(DateUtility.getAppDate(), PennantConstants.DBDateFormat))));
			}
		}
		finReceiptDetail.setReceivedDate(DateUtility
				.getDBDate(DateUtility.formatDate(finReceiptDetail.getReceivedDate(), PennantConstants.DBDateFormat)));
		receiptHeader.setReceiptDate(finReceiptDetail.getReceivedDate());
		finReceiptDetail.setReceiptType(RepayConstants.RECEIPTTYPE_RECIPT);
		finReceiptDetail.setPaymentTo(RepayConstants.RECEIPTTO_FINANCE);
		finReceiptDetail.setPaymentType(finServiceInst.getPaymentMode());
		finReceiptDetail.setAmount(finServiceInst.getAmount());
		

		receiptHeader.setRemarks(finReceiptDetail.getRemarks());
		finReceiptData.setReceiptHeader(receiptHeader);
		finReceiptData.setFinanceDetail(financeDetail);
		finReceiptData.setFinReference(finServiceInst.getFinReference());
		finReceiptData.setSourceId(PennantConstants.FINSOURCE_ID_API);

		if (finServiceInst.isReceiptUpload()) {// receipt upload details setting

			receiptHeader.setAllocationType(finServiceInst.getAllocationType());
			finReceiptDetail.setReceiptID(receiptId);
			finReceiptDetail.setFavourName(finServiceInst.getEntityDesc());
			finReceiptDetail.setFavourNumber(finServiceInst.getFavourNumber());

			// add fin service instruction field data to finreceipt details
			// as in receipt upload,we had added validation in reverse stage
			// setting value date with received date and received date with
			// value date
			// as per bajaj UD,this is written in reverse order
			// ### PSD Ticket id:124998
			finReceiptDetail.setValueDate(finServiceInst.getReceivedDate());
			finReceiptDetail.setReceivedDate(finServiceInst.getValueDate());
			finReceiptDetail.setFundingAc(finServiceInst.getFundingAc());
			finReceiptDetail.setPaymentRef(finServiceInst.getPaymentRef());
			finReceiptDetail.setBankCode(finServiceInst.getBankCode());
			finReceiptDetail.setChequeAcNo(finServiceInst.getChequeNo());
			finReceiptDetail.setTransactionRef(finServiceInst.getTransactionRef());
			finReceiptDetail.setStatus(finServiceInst.getStatus());

			if (StringUtils.equals(finReceiptDetail.getStatus(), RepayConstants.PAYSTATUS_REALIZED)) {
				receiptHeader.setRealizationDate(finServiceInst.getRealizationDate());
				receiptHeader.setReceiptModeStatus(RepayConstants.PAYSTATUS_REALIZED);
			}

			finReceiptDetail.setDepositDate(finServiceInst.getDepositDate());
			receiptHeader.setRemarks(finServiceInst.getRemarks());

		}

		receiptHeader.getReceiptDetails().add(finReceiptDetail);

		// check dedup condition
		// this check will validate given details with already exit details
		if (StringUtils.equals(finServiceInst.getReqType(), APIConstants.REQTYPE_POST)
				&& receiptService.dedupCheckRequest(receiptHeader, purpose)) {
			long receiptHeaderId = receiptService.CheckDedupSP(receiptHeader, purpose);

			FinanceDetail response = new FinanceDetail();

			this.finReceiptHeaderDAO.updateReceiptStatusAndRealizationDate(receiptHeaderId,
					RepayConstants.PAYSTATUS_REALIZED, receiptHeader.getRealizationDate());
			this.finReceiptDetailDAO.updateReceiptStatusByReceiptId(receiptHeaderId, RepayConstants.PAYSTATUS_REALIZED);
			response.setReturnStatus(APIErrorHandlerService.getSuccessStatus());

			return response;

		}

		FinanceMain financeMain = financeDetail.getFinScheduleData().getFinanceMain();
		Date receiDate = DateUtility
				.getDBDate(DateUtility.formatDate(finReceiptDetail.getReceivedDate(), PennantConstants.DBDateFormat));
		finReceiptDetail.setReceivedDate(receiDate);

		Date curBussDate = DateUtility.getAppDate();
		if ((StringUtils.equals(finServiceInst.getPaymentMode(), DisbursementConstants.PAYMENT_TYPE_CHEQUE)
				|| StringUtils.equals(finServiceInst.getPaymentMode(), DisbursementConstants.PAYMENT_TYPE_DD))
				&& StringUtils.equals(finServiceInst.getReqType(), "Post")) {
			if (DateUtility.compare(finReceiptDetail.getValueDate(), financeMain.getFinStartDate()) <= 0
					|| DateUtility.compare(finReceiptDetail.getValueDate(), curBussDate) > 0) {
				FinanceDetail response = new FinanceDetail();
				doEmptyResponseObject(response);
				String[] valueParm = new String[3];
				valueParm[0] = "Value Date " + DateUtility.formatToShortDate(finReceiptDetail.getReceivedDate());
				valueParm[1] = "Loan start Date:" + DateUtility.formatToShortDate(financeMain.getFinStartDate());
				valueParm[2] = "Application Date:" + DateUtility.formatToShortDate(DateUtility.getAppDate());
				response.setReturnStatus(APIErrorHandlerService.getFailedStatus("90350", valueParm));
				return response;
			}
		}
		if (finServiceInst.getReceiptDetail() != null) {
			if (DateUtility.compare(receiDate, financeMain.getFinStartDate()) <= 0
					|| DateUtility.compare(receiDate, curBussDate) > 0) {
				FinanceDetail response = new FinanceDetail();
				doEmptyResponseObject(response);
				String[] valueParm = new String[3];
				valueParm[0] = "Received Date " + DateUtility.formatToShortDate(finReceiptDetail.getReceivedDate());
				valueParm[1] = "Loan start Date:" + DateUtility.formatToShortDate(financeMain.getFinStartDate());
				valueParm[2] = "Application Date:" + DateUtility.formatToShortDate(DateUtility.getAppDate());
				response.setReturnStatus(APIErrorHandlerService.getFailedStatus("90350", valueParm));
				return response;
			}

		}

		if (curBussDate.compareTo(financeMain.getFinStartDate()) == 0) {
			FinanceDetail response = new FinanceDetail();
			doEmptyResponseObject(response);
			response.setReturnStatus(APIErrorHandlerService.getFailedStatus("90286"));
			return response;
		}

		// calculate total fee amount
		BigDecimal totFeeAmount = getTotalFeePaid(financeDetail.getFinScheduleData().getFinFeeDetailList());
		receiptHeader.setTotFeeAmount(totFeeAmount);
		BigDecimal totReceiptAmt = receiptHeader.getReceiptAmount().subtract(totFeeAmount);

		Map<String, BigDecimal> allocationMap = null;
		if (finServiceInst.isReceiptUpload() && finServiceInst.getAllocationType().equalsIgnoreCase("M")) {
			// calculate manual Allocations
			allocationMap = finServiceInst.getManualAllocMap();
			allocationMap.remove("E");
			allocationMap.remove("A");

			if (allocationMap.containsKey(RepayConstants.ALLOCATION_NPFT)) {

				BigDecimal calPft = BigDecimal.ZERO;

				BigDecimal allocAmt = BigDecimal.ZERO;

				if (finServiceInst.getManualWaiverMap().containsKey(RepayConstants.ALLOCATION_NPFT)) {
					allocAmt = allocationMap.get(RepayConstants.ALLOCATION_NPFT)
							.add(finServiceInst.getManualWaiverMap().get(RepayConstants.ALLOCATION_NPFT));
				} else {
					allocAmt = allocationMap.get(RepayConstants.ALLOCATION_NPFT);
				}
				calPft = receiptCalculator.getPftAmount(financeDetail.getFinScheduleData(), allocAmt);
				allocationMap.put(RepayConstants.ALLOCATION_PFT, calPft);
			}

			finReceiptData.setWaiverMap(finServiceInst.getManualWaiverMap());
		} else {
			// calculate Auto allocations
			allocationMap = receiptCalculator.recalAutoAllocation(financeDetail, totReceiptAmt,
					finReceiptDetail.getReceivedDate(), receiptHeader.getReceiptPurpose(), false);
		}
		finReceiptData.setAllocationMap(allocationMap);

		// validate repayment amount
		//### 28-09-2018,Receipt upload,if receipt details exits we are not validating repay amount
		if (!finServiceInst.isReceiptdetailExits()) {
			Map<String, String> returnMap = validateRepayAmount(finScheduleData, finServiceInst, totReceiptAmt);
			if (StringUtils.isNotBlank(returnMap.get("ReturnCode"))) {
				FinanceDetail response = new FinanceDetail();
				doEmptyResponseObject(response);
				WSReturnStatus status = new WSReturnStatus();
				status.setReturnCode(returnMap.get("ReturnCode"));
				status.setReturnText(returnMap.get("ReturnText"));
				response.setReturnStatus(status);
				return response;
			}
			if (StringUtils.equals(purpose, FinanceConstants.FINSER_EVENT_EARLYRPY)) {
				List<FinanceScheduleDetail> scheduleList = finScheduleData.getFinanceScheduleDetails();
				BigDecimal partPayment = new BigDecimal(returnMap.get("partPaidAmt"));
				BigDecimal closingBal = null;
				for (int i = 0; i < scheduleList.size(); i++) {
					FinanceScheduleDetail curSchd = scheduleList.get(i);
					if (DateUtility.compare(finReceiptDetail.getReceivedDate(), curSchd.getSchDate()) >= 0) {
						closingBal = curSchd.getClosingBalance();
						continue;
					}
					if (DateUtility.compare(finReceiptDetail.getReceivedDate(), curSchd.getSchDate()) == 0
							|| closingBal == null) {
						if (closingBal == null) {
							closingBal = BigDecimal.ZERO;
						}
						closingBal = closingBal.subtract(curSchd.getSchdPriPaid().subtract(curSchd.getSchdPftPaid()));
						break;
					}
				}

				if (closingBal != null) {
					if (partPayment.compareTo(closingBal) >= 0) {
						FinanceDetail response = new FinanceDetail();
						doEmptyResponseObject(response);
						String[] valueParm = new String[1];
						valueParm[0] = String.valueOf(closingBal);
						response.setReturnStatus(APIErrorHandlerService.getFailedStatus("91127", valueParm));
						return response;
					}
				}
			}

			if (StringUtils.equals(purpose, FinanceConstants.FINSER_EVENT_EARLYRPY)
					|| StringUtils.equals(purpose, FinanceConstants.FINSER_EVENT_EARLYSETTLE)) {
				FinScheduleData scheduleData = financeDetail.getFinScheduleData();
				Date valueDate = DateUtility.getAppDate();
				if (finReceiptDetail.getReceivedDate() != null) {
					valueDate = finReceiptDetail.getReceivedDate();
				}
				finReceiptData.setBuildProcess("I");
				finReceiptData = receiptCalculator.initiateReceipt(finReceiptData, scheduleData, valueDate, purpose,
						false);
				finReceiptData = receiptService.recalEarlypaySchdl(finReceiptData, finServiceInst, purpose,
						new BigDecimal(returnMap.get("partPaidAmt")));
			} else if (StringUtils.equals(purpose, FinanceConstants.FINSER_EVENT_SCHDRPY)) {
				finReceiptData = receiptService.calculateRepayments(finReceiptData, false);
			}

			List<ErrorDetail> errorDetails = finReceiptData.getFinanceDetail().getFinScheduleData().getErrorDetails();
			if (!errorDetails.isEmpty()) {
				for (ErrorDetail error : errorDetails) {
					FinanceDetail response = new FinanceDetail();
					doEmptyResponseObject(response);
					response.setReturnStatus(APIErrorHandlerService.getFailedStatus(error.getCode()));
					return response;
				}
			}
		}

		// set fees details into finReceiptData
		finScheduleData = finReceiptData.getFinanceDetail().getFinScheduleData();
		finScheduleData.setFinFeeDetailList(aFinanceDetail.getFinScheduleData().getFinFeeDetailList());
		if (StringUtils.equals(finServiceInst.getReqType(), APIConstants.REQTYPE_POST)) {

			long fundingAccount = 0;
			if (finServiceInst.isReceiptUpload()) {// Ticket id:124998
				fundingAccount = finServiceInst.getFundingAc();
			} else {
				fundingAccount = finReceiptDetail.getFundingAc();
			}

			int count = finTypePartnerBankService.getPartnerBankCount(financeMain.getFinType(),
					finServiceInst.getPaymentMode(), AccountConstants.PARTNERSBANK_RECEIPTS, fundingAccount);
			if (count <= 0) {
				FinanceDetail response = new FinanceDetail();
				doEmptyResponseObject(response);
				response.setReturnStatus(APIErrorHandlerService.getFailedStatus("90263"));
				return response;
			}

			// fetch partner bank details
			PartnerBank partnerBank = partnerBankDAO.getPartnerBankById(finReceiptDetail.getFundingAc(), "");
			if (partnerBank != null) {
				finReceiptDetail.setPartnerBankAc(partnerBank.getAccountNo());
				finReceiptDetail.setPartnerBankAcType(partnerBank.getAcType());
			}

			// ### PSD Ticket id:124998
			if (StringUtils.equals(purpose, FinanceConstants.FINSER_EVENT_EARLYSETTLE)
					&& !finServiceInst.isReceiptUpload()) {
				if (DateUtility.compare(finReceiptDetail.getReceivedDate(), DateUtility.getAppDate()) < 0) {
					FinanceDetail response = new FinanceDetail();
					doEmptyResponseObject(response);
					String[] valueParm = new String[2];
					valueParm[0] = "Received Date  "
							+ DateUtility.formatToShortDate(finReceiptDetail.getReceivedDate());
					valueParm[1] = "Application Date " + DateUtility.formatToShortDate(DateUtility.getAppDate());
					response.setReturnStatus(APIErrorHandlerService.getFailedStatus("65030", valueParm));
					return response;
				}
			}

			if (finScheduleData.getFinFeeDetailList() != null) {
				for (FinFeeDetail finFeeDetail : finScheduleData.getFinFeeDetailList()) {
					if (StringUtils.equals(finFeeDetail.getRecordStatus(), PennantConstants.RCD_STATUS_APPROVED)) {
						finFeeDetail.setRecordType("");
					}
				}
			}

			int version = 0;
			// Receipt upload process
			if (finServiceInst.isReceiptdetailExits()) {
				FinReceiptData receiptData = this.receiptService.getFinReceiptDataById(finServiceInst.getFinReference(),
						AccountEventConstants.ACCEVENT_REPAY, FinanceConstants.FINSER_EVENT_RECEIPT,
						"RECEIPTREALIZE_MAKER");
				finReceiptData = receiptData;

				version = finReceiptData.getFinanceDetail().getFinScheduleData().getFinanceMain().getVersion();
				finReceiptData.getFinanceDetail().getFinScheduleData().getFinanceMain()
						.setRecordStatus(PennantConstants.RCD_STATUS_APPROVED);
				finReceiptData.getFinanceDetail().getFinScheduleData().getFinanceMain().setRecordType("");
				finReceiptData.getFinanceDetail().getFinScheduleData().setSchduleGenerated(true);
				finReceiptData.getReceiptHeader().setRealizationDate(finServiceInst.getRealizationDate());
			} else {
				// Set Version value
				version = finReceiptData.getFinanceDetail().getFinScheduleData().getFinanceMain().getVersion();
				finReceiptData.getFinanceDetail().getFinScheduleData().getFinanceMain().setVersion(version + 1);
				finReceiptData.getFinanceDetail().getFinScheduleData().getFinanceMain().setRecordType("");
				finReceiptData.getFinanceDetail().getFinScheduleData().setSchduleGenerated(true);
			}

			// Save the Schedule details
			AuditHeader auditHeader = getAuditHeader(finReceiptData, PennantConstants.TRAN_WF);

			// ### ticket id:124998,
			// setting to temp table
			if (!finServiceInst.isReceiptdetailExits() && finServiceInst.isReceiptUpload()
					&& !StringUtils.equals(purpose, FinanceConstants.FINSER_EVENT_SCHDRPY)
					&& StringUtils.equals(finServiceInst.getStatus(), "A")
					&& (StringUtils.equals(finServiceInst.getPaymentMode(), "CHEQUE")
							|| StringUtils.equals(finServiceInst.getPaymentMode(), "DD"))) {

				WorkFlowDetails workFlowDetails = null;
				String roleCode = "DEPOSIT_APPROVER";// default value
				String nextRolecode = "RECEIPTREALIZE_MAKER";// defaulting role
																// codes
				String taskid = null;
				String nextTaskId = null;
				long workFlowId = 0;

				String finEvent = FinanceConstants.FINSER_EVENT_RECEIPT;
				FinanceWorkFlow financeWorkFlow = financeWorkFlowService.getApprovedFinanceWorkFlowById(
						financeMain.getFinType(), finEvent, PennantConstants.WORFLOW_MODULE_FINANCE);

				if (financeWorkFlow != null) {
					workFlowDetails = WorkFlowUtil.getDetailsByType(financeWorkFlow.getWorkFlowType());
					if (workFlowDetails != null) {
						workFlow = new WorkflowEngine(workFlowDetails.getWorkFlowXml());
						taskid = workFlow.getUserTaskId(roleCode);
						workFlowId = workFlowDetails.getWorkFlowId();
						nextTaskId = workFlow.getUserTaskId(nextRolecode);
					}

					financeMain.setWorkflowId(workFlowId);
					financeMain.setTaskId(taskid);
					financeMain.setRoleCode(roleCode);
					financeMain.setNextRoleCode(nextRolecode);
					financeMain.setRecordType(PennantConstants.RECORD_TYPE_UPD);
					financeMain.setNextTaskId(nextTaskId + ";");
					financeMain.setNewRecord(true);
					financeMain.setVersion(version + 1);
					financeMain.setRcdMaintainSts(FinanceConstants.FINSER_EVENT_RECEIPT);
					financeMain.setLastMntOn(new Timestamp(System.currentTimeMillis()));
					financeMain.setRecordStatus(PennantConstants.RCD_STATUS_SUBMITTED);

					// remove unwanted fees

					String eventCode = null;
					if (StringUtils.equals(purpose, FinanceConstants.FINSER_EVENT_EARLYSETTLE)) {
						eventCode = AccountEventConstants.ACCEVENT_EARLYSTL;
					} else {
						eventCode = AccountEventConstants.ACCEVENT_EARLYPAY;
					}

					List<FinFeeDetail> listFeeDetails = new ArrayList<>();
					if (finScheduleData.getFinFeeDetailList() != null) {
						for (FinFeeDetail finFeeDetail : finScheduleData.getFinFeeDetailList()) {
							if (finFeeDetail.isOriginationFee()) {
								continue;
							} else {
								listFeeDetails.add(finFeeDetail);
							}
						}
					}
					finReceiptData.getFinanceDetail().getFinScheduleData().setFinFeeDetailList(listFeeDetails);

				}

				finReceiptData.getFinanceDetail().getFinScheduleData().setFinanceMain(financeMain);
				APIHeader reqHeaderDetails = (APIHeader) PhaseInterceptorChain.getCurrentMessage().getExchange()
						.get(APIHeader.API_HEADER_KEY);
				auditHeader.setApiHeader(reqHeaderDetails);

				auditHeader = receiptService.saveOrUpdate(auditHeader);

			} else {

				APIHeader reqHeaderDetails = (APIHeader) PhaseInterceptorChain.getCurrentMessage().getExchange()
						.get(APIHeader.API_HEADER_KEY);
				auditHeader.setApiHeader(reqHeaderDetails);

				auditHeader = receiptService.doApprove(auditHeader);
			}

			if (auditHeader.getErrorMessage() != null) {
				for (ErrorDetail errorDetail : auditHeader.getErrorMessage()) {
					FinanceDetail response = new FinanceDetail();
					doEmptyResponseObject(response);
					response.setReturnStatus(
							APIErrorHandlerService.getFailedStatus(errorDetail.getCode(), errorDetail.getError()));
					return response;
				}
			}
			finReceiptData = (FinReceiptData) auditHeader.getAuditDetail().getModelData();
			financeDetail = getServiceInstResponse(finReceiptData.getFinanceDetail().getFinScheduleData());


			// set receipt id in data
			if (finServiceInst.isReceiptUpload() && !finServiceInst.isReceiptResponse()) {
				this.receiptUploadDetailDAO.updateReceiptId(finServiceInst.getUploadDetailId(),
						finReceiptDetail.getReceiptID());
			}

			// set receipt id response job
			if (finServiceInst.isReceiptUpload() && finServiceInst.isReceiptResponse()) {
				this.receiptResponseDetailDAO.updateReceiptResponseId(finServiceInst.getRootId(),
						finReceiptDetail.getReceiptID());
			}

		} else {
			// Schedule Updations
			List<FinanceScheduleDetail> actualSchedules = finReceiptData.getFinanceDetail().getFinScheduleData()
					.getFinanceScheduleDetails();
			// Repay Schedule Data rebuild
			List<RepayScheduleDetail> rpySchdList = new ArrayList<>();
			List<FinReceiptDetail> receiptDetailList = finReceiptData.getReceiptHeader().getReceiptDetails();
			for (int i = 0; i < receiptDetailList.size(); i++) {
				List<FinRepayHeader> repayHeaderList = receiptDetailList.get(i).getRepayHeaders();
				for (int j = 0; j < repayHeaderList.size(); j++) {
					if (repayHeaderList.get(j).getRepayScheduleDetails() != null) {
						rpySchdList.addAll(repayHeaderList.get(j).getRepayScheduleDetails());
					}
				}
			}

			Map<Date, BigDecimal> odChargePaidMap = new HashMap<>();
			Map<Date, BigDecimal> odPenaltyPaidMap = new HashMap<>();
			Map<Date, BigDecimal> odPriPaidMap = new HashMap<>();
			Map<Date, BigDecimal> odPftPaidMap = new HashMap<>();
			for (FinanceScheduleDetail actFinSchedule : actualSchedules) {
				for (RepayScheduleDetail chgdFinSchedule : rpySchdList) {
					if (DateUtility.compare(actFinSchedule.getSchDate(), chgdFinSchedule.getSchDate()) == 0) {
						actFinSchedule.setSchdPriPaid(
								actFinSchedule.getSchdPriPaid().add(chgdFinSchedule.getPrincipalSchdPayNow()));
						actFinSchedule.setSchdPftPaid(
								actFinSchedule.getSchdPftPaid().add(chgdFinSchedule.getProfitSchdPayNow()));
						actFinSchedule.setTDSPaid(actFinSchedule.getTDSPaid().add(chgdFinSchedule.getTdsSchdPayNow()));
						actFinSchedule.setSchdFeePaid(
								actFinSchedule.getSchdFeePaid().add(chgdFinSchedule.getSchdFeePayNow()));
						actFinSchedule.setSchdInsPaid(
								actFinSchedule.getSchdInsPaid().add(chgdFinSchedule.getSchdInsPayNow()));

						// Preparing penalty Amount Paid Now
						BigDecimal odChargePaid = BigDecimal.ZERO;
						if (odChargePaidMap.containsKey(chgdFinSchedule.getSchDate())) {
							odChargePaid = odChargePaidMap.get(chgdFinSchedule.getSchDate());
							odChargePaidMap.remove(chgdFinSchedule.getSchDate());
						}
						odChargePaid = odChargePaid.add(chgdFinSchedule.getPenaltyPayNow());
						odChargePaidMap.put(chgdFinSchedule.getSchDate(), odChargePaid);

						// Preparing latepay Amount Paid Now
						BigDecimal odPenaltyPaid = BigDecimal.ZERO;
						if (odPenaltyPaidMap.containsKey(chgdFinSchedule.getSchDate())) {
							odPenaltyPaid = odPenaltyPaidMap.get(chgdFinSchedule.getSchDate());
							odPenaltyPaidMap.remove(chgdFinSchedule.getSchDate());
						}
						odPenaltyPaid = odPenaltyPaid.add(chgdFinSchedule.getLatePftSchdPayNow());
						odPenaltyPaidMap.put(chgdFinSchedule.getSchDate(), odPenaltyPaid);

						// Preparing OD Principle Amount Paid Now
						BigDecimal odPriPaid = BigDecimal.ZERO;
						if (odPriPaidMap.containsKey(chgdFinSchedule.getSchDate())) {
							odPriPaid = odPriPaidMap.get(chgdFinSchedule.getSchDate());
							odPriPaidMap.remove(chgdFinSchedule.getSchDate());
						}
						odPriPaid = odPriPaid.add(chgdFinSchedule.getPrincipalSchdPayNow());
						odPriPaidMap.put(chgdFinSchedule.getSchDate(), odPriPaid);

						// Preparing OD Principle Amount Paid Now
						BigDecimal odPftPaid = BigDecimal.ZERO;
						if (odPftPaidMap.containsKey(chgdFinSchedule.getSchDate())) {
							odPftPaid = odPftPaidMap.get(chgdFinSchedule.getSchDate());
							odPftPaidMap.remove(chgdFinSchedule.getSchDate());
						}
						odPftPaid = odPftPaid.add(chgdFinSchedule.getProfitSchdPayNow());
						odPftPaidMap.put(chgdFinSchedule.getSchDate(), odPftPaid);
					}
				}
			}

			financeDetail = finReceiptData.getFinanceDetail();
			receiptHeader = finReceiptData.getReceiptHeader();
			financeDetail = getServiceInstResponse(finReceiptData.getFinanceDetail().getFinScheduleData());

			financeDetail.getFinScheduleData().setFinanceMain(financeMain);
			Date valueDate = finServiceInst.getReceiptDetail().getReceivedDate();

			financeDetail.getFinScheduleData().setFinanceScheduleDetails(actualSchedules);
			List<FinODDetails> finODDetailsList = financeDetail.getFinScheduleData().getFinODDetails();
			if (DateUtility.compare(valueDate, DateUtility.getAppDate()) != 0) {
				List<FinanceRepayments> repayments = getRepaymentDetails(aFinanceDetail.getFinScheduleData(),
						totReceiptAmt, valueDate);
				finODDetailsList = receiptService.getValueDatePenalties(financeDetail.getFinScheduleData(),
						totReceiptAmt, valueDate, repayments, true);
			}

			BigDecimal overDuePrincipal = BigDecimal.ZERO;
			BigDecimal overDueProfit = BigDecimal.ZERO;
			int size = 0;
			if (finODDetailsList != null) {
				for (FinODDetails finODDetails : finODDetailsList) {
					Date finOdDate = DateUtility.getDBDate(
							DateUtility.formatDate(finODDetails.getFinODSchdDate(), PennantConstants.DBDateFormat));
					if (odChargePaidMap.containsKey(finOdDate)) {
						BigDecimal penaltyPayNow = odChargePaidMap.get(finOdDate);
						finODDetails.setTotPenaltyPaid(finODDetails.getTotPenaltyPaid().add(penaltyPayNow));
					}
					if (odPenaltyPaidMap.containsKey(finOdDate)) {
						BigDecimal latePenaltyPayNow = odPenaltyPaidMap.get(finOdDate);
						finODDetails.setLPIPaid(finODDetails.getLPIPaid().add(latePenaltyPayNow));
					}

					if (DateUtility.compare(valueDate, DateUtility.getAppDate()) == 0) {
						if (odPriPaidMap.containsKey(finOdDate)) {
							BigDecimal priPayNow = odPriPaidMap.get(finOdDate);
							finODDetails.setFinCurODPri(finODDetails.getFinCurODPri().subtract(priPayNow));
						}

						if (odPftPaidMap.containsKey(finOdDate)) {
							BigDecimal pftPayNow = odPftPaidMap.get(finOdDate);
							finODDetails.setFinCurODPft(finODDetails.getFinCurODPft().subtract(pftPayNow));
						}
					}
					finODDetails.setFinCurODAmt(finODDetails.getFinCurODPri().add(finODDetails.getFinCurODPft()));
					overDuePrincipal = overDuePrincipal.add(finODDetails.getFinCurODPri());
					overDueProfit = overDueProfit.add(finODDetails.getFinCurODPft());
					if (finODDetails.getFinCurODAmt().compareTo(BigDecimal.ZERO) > 0) {
						size++;
					}
				}
			}

			FinanceSummary summary = financeDetail.getFinScheduleData().getFinanceSummary();
			summary.setOverDuePrincipal(overDuePrincipal);
			summary.setOverDueProfit(overDueProfit);
			summary.setTotalOverDue(overDuePrincipal.add(overDueProfit));
			summary.setFinODDetail(finODDetailsList);
			summary.setOverDueInstlments(size);
			financeDetail.getFinScheduleData().setFinanceMain(null);

		}
		logger.debug("Leaving");
		return financeDetail;
	}

	private Map<String, String> validateRepayAmount(FinScheduleData finScheduleData,
			FinServiceInstruction finServiceInst, BigDecimal totReceiptAmt) {

		Map<String, String> returnMap = new HashMap<String, String>();
		returnMap.put("ReturnCode", "");
		returnMap.put("ReturnText", "");
		returnMap.put("partPaidAmt", "0");

		// validate repayment amount
		BigDecimal pftBalance = BigDecimal.ZERO;
		BigDecimal priBalance = BigDecimal.ZERO;
		BigDecimal schFeeBal = BigDecimal.ZERO;
		BigDecimal tdsReturns = BigDecimal.ZERO;

		Date curBussniessDate = finServiceInst.getReceiptDetail().getReceivedDate();
		boolean partAccrualReq = true;
		FinanceScheduleDetail curSchd = null;
		FinanceScheduleDetail prvSchd = null;
		String finReference = finScheduleData.getFinanceMain().getFinReference();

		if (totReceiptAmt.compareTo(BigDecimal.ZERO) <= 0) {
			if (StringUtils.equals(finServiceInst.getModuleDefiner(), FinanceConstants.FINSER_EVENT_EARLYSTLENQ)
					|| StringUtils.equals(finServiceInst.getModuleDefiner(),
							FinanceConstants.FINSER_EVENT_EARLYSETTLE)) {
				WSReturnStatus status = APIErrorHandlerService.getFailedStatus("90330");
				returnMap.put("ReturnCode", status.getReturnCode());
				returnMap.put("ReturnText", status.getReturnText());
			} else if (StringUtils.equals(finServiceInst.getModuleDefiner(), FinanceConstants.FINSER_EVENT_EARLYRPY)
					|| StringUtils.equals(finServiceInst.getModuleDefiner(), FinanceConstants.FINSER_EVENT_SCHDRPY)) {
				WSReturnStatus status = APIErrorHandlerService.getFailedStatus("90331");
				returnMap.put("ReturnCode", status.getReturnCode());
				returnMap.put("ReturnText", status.getReturnText());
			}
		}

		if (!StringUtils.equals(finServiceInst.getModuleDefiner(), FinanceConstants.FINSER_EVENT_SCHDRPY)) {
			FinanceMain financeMain = finScheduleData.getFinanceMain();
			BigDecimal tdsMultiplier = BigDecimal.ONE;
			if (financeMain.isTDSApplicable()) {
				BigDecimal tdsPerc = new BigDecimal(
						SysParamUtil.getValue(CalculationConstants.TDS_PERCENTAGE).toString());
				if (tdsPerc.compareTo(BigDecimal.ZERO) > 0) {
					tdsMultiplier = (new BigDecimal(100)).divide(new BigDecimal(100).subtract(tdsPerc), 20,
							RoundingMode.HALF_DOWN);
				}
			}

			List<FinanceScheduleDetail> tempScheduleDetails = finScheduleData.getFinanceScheduleDetails();
			for (int i = 0; i < tempScheduleDetails.size(); i++) {
				curSchd = tempScheduleDetails.get(i);
				if (i != 0) {
					prvSchd = tempScheduleDetails.get(i - 1);
				}
				Date schdDate = curSchd.getSchDate();

				schFeeBal = schFeeBal.add(curSchd.getFeeSchd().subtract(curSchd.getSchdFeePaid()));

				if (DateUtility.compare(schdDate, curBussniessDate) < 0) {
					pftBalance = pftBalance.add(curSchd.getProfitSchd().subtract(curSchd.getSchdPftPaid()));
					priBalance = priBalance.add(curSchd.getPrincipalSchd().subtract(curSchd.getSchdPriPaid()));
					if (financeMain.isTDSApplicable()) {
						BigDecimal pft = curSchd.getProfitSchd().subtract(curSchd.getSchdPftPaid());
						BigDecimal actualPft = pft.divide(tdsMultiplier, 0, RoundingMode.HALF_DOWN);
						tdsReturns = tdsReturns.add(pft.subtract(actualPft));
					}
				} else if (DateUtility.compare(curBussniessDate, schdDate) == 0) {

					if (StringUtils.equals(finServiceInst.getModuleDefiner(), FinanceConstants.FINSER_EVENT_EARLYSETTLE)
							|| StringUtils.equals(finServiceInst.getModuleDefiner(),
									FinanceConstants.FINSER_EVENT_EARLYSTLENQ)) {

						BigDecimal remPft = curSchd.getProfitCalc().subtract(curSchd.getSchdPftPaid());
						pftBalance = pftBalance.add(curSchd.getProfitCalc().subtract(curSchd.getSchdPftPaid()));
						if (prvSchd != null) {
							remPft = remPft.add(prvSchd.getProfitBalance());
							pftBalance = pftBalance.add(prvSchd.getProfitBalance());
						}

						priBalance = priBalance.add(curSchd.getPrincipalSchd().add(curSchd.getClosingBalance()))
								.subtract(curSchd.getCpzAmount()).subtract(curSchd.getSchdPriPaid());

						if (financeMain.isTDSApplicable()) {
							BigDecimal actualPft = remPft.divide(tdsMultiplier, 0, RoundingMode.HALF_DOWN);
							tdsReturns = tdsReturns.add(remPft.subtract(actualPft));
						}
						partAccrualReq = false;
					} else {
						pftBalance = pftBalance.add(curSchd.getProfitSchd().subtract(curSchd.getSchdPftPaid()));
						priBalance = priBalance.add(curSchd.getPrincipalSchd().subtract(curSchd.getSchdPriPaid()));
						if (financeMain.isTDSApplicable()) {
							BigDecimal pft = curSchd.getProfitSchd().subtract(curSchd.getSchdPftPaid());
							BigDecimal actualPft = pft.divide(tdsMultiplier, 0, RoundingMode.HALF_DOWN);
							tdsReturns = tdsReturns.add(pft.subtract(actualPft));
						}
					}
				} else {
					if (StringUtils.equals(finServiceInst.getModuleDefiner(), FinanceConstants.FINSER_EVENT_EARLYSETTLE)
							|| StringUtils.equals(finServiceInst.getModuleDefiner(),
									FinanceConstants.FINSER_EVENT_EARLYSTLENQ)) {
						if (partAccrualReq && prvSchd != null) {
							partAccrualReq = false;
							BigDecimal accruedPft = CalculationUtil.calInterest(prvSchd.getSchDate(), curBussniessDate,
									curSchd.getBalanceForPftCal(), prvSchd.getPftDaysBasis(),
									prvSchd.getCalculatedRate());
							accruedPft = accruedPft.add(prvSchd.getProfitFraction());
							accruedPft = CalculationUtil.roundAmount(accruedPft, financeMain.getCalRoundingMode(),
									financeMain.getRoundingTarget());
							pftBalance = pftBalance.add(accruedPft).add(prvSchd.getProfitBalance());

							priBalance = priBalance.add(prvSchd.getClosingBalance());

							if (financeMain.isTDSApplicable()) {
								BigDecimal actualPft = (accruedPft.add(prvSchd.getProfitBalance()))
										.divide(tdsMultiplier, 0, RoundingMode.HALF_DOWN);
								tdsReturns = tdsReturns
										.add(accruedPft.add(prvSchd.getProfitBalance()).subtract(actualPft));
							}
						} else {
							priBalance = priBalance.add(curSchd.getDisbAmount());
						}
					} else {
						break;
					}
				}
			}

			// Fetching Actual Late Payments based on Value date passing
			BigDecimal latePayPftBal = BigDecimal.ZERO;
			BigDecimal penaltyBal = BigDecimal.ZERO;
			List<FinODDetails> overdueList = null;
			if (DateUtility.compare(curBussniessDate, DateUtility.getAppDate()) == 0) {
				overdueList = finODDetailsDAO.getFinODDByFinRef(finReference, null);
			} else {
				// Calculate overdue Penalties
				overdueList = receiptService.getValueDatePenalties(finScheduleData, totReceiptAmt, curBussniessDate,
						null, true);
			}

			// Calculating Actual Sum of Penalty Amount & Late Pay Interest
			if (overdueList != null && !overdueList.isEmpty()) {
				for (int i = 0; i < overdueList.size(); i++) {
					FinODDetails finODDetail = overdueList.get(i);
					if (finODDetail.getFinODSchdDate().compareTo(curBussniessDate) > 0) {
						continue;
					}
					latePayPftBal = latePayPftBal.add(finODDetail.getLPIBal());
					penaltyBal = penaltyBal.add(finODDetail.getTotPenaltyBal());
				}
			}

			BigDecimal remBal = priBalance.add(pftBalance).add(schFeeBal).add(latePayPftBal).add(penaltyBal)
					.subtract(tdsReturns);
			BigDecimal partialPaidAmt = totReceiptAmt.subtract(
					priBalance.add(pftBalance).add(schFeeBal).add(latePayPftBal).add(penaltyBal).subtract(tdsReturns));
			returnMap.put("partPaidAmt", String.valueOf(partialPaidAmt));

			// calculate Remaining balance after
			if (StringUtils.equals(finServiceInst.getModuleDefiner(), FinanceConstants.FINSER_EVENT_EARLYSTLENQ)
					|| StringUtils.equals(finServiceInst.getModuleDefiner(),
							FinanceConstants.FINSER_EVENT_EARLYSETTLE)) {
				if (totReceiptAmt.compareTo(remBal) < 0) {
					WSReturnStatus status = APIErrorHandlerService.getFailedStatus("90330");
					returnMap.put("ReturnCode", status.getReturnCode());
					returnMap.put("ReturnText", status.getReturnText());
				}
			} else if (StringUtils.equals(finServiceInst.getModuleDefiner(), FinanceConstants.FINSER_EVENT_EARLYRPY)) {
				if (totReceiptAmt.compareTo(remBal) <= 0) {
					WSReturnStatus status = APIErrorHandlerService.getFailedStatus("90332");
					returnMap.put("ReturnCode", status.getReturnCode());
					returnMap.put("ReturnText", status.getReturnText());
				}
			}
		}
		return returnMap;
	}

	private BigDecimal getTotalFeePaid(List<FinFeeDetail> finFeeDetailList) {
		BigDecimal totFeeAmount = BigDecimal.ZERO;
		if (finFeeDetailList != null) {
			for (FinFeeDetail feeDetail : finFeeDetailList) {
				if (!feeDetail.isOriginationFee()) {
					totFeeAmount = totFeeAmount.add(feeDetail.getPaidAmount());
				}
			}
		}
		return totFeeAmount;
	}

	/**
	 * Method for updateLoanBasicDetails
	 * 
	 * @param financeMain
	 * @return WSReturnStatus
	 */
	public WSReturnStatus updateLoanBasicDetails(FinanceMain financeMain) {
		logger.debug("Enteing");

		// update the Finance Basic Details
		int count = financeMainService.updateFinanceBasicDetails(financeMain);
		if (count > 0) {
			logger.debug("Leaving");
			return APIErrorHandlerService.getSuccessStatus();
		} else {
			return APIErrorHandlerService.getFailedStatus();
		}

	}

	/**
	 * Method for updateLoanPenaltyDetails
	 * 
	 * @param finODPenaltyRate
	 * @return WSReturnStatus
	 */
	public WSReturnStatus updateLoanPenaltyDetails(FinODPenaltyRate finODPenaltyRate) {
		logger.debug("Enteing");
		try {
			// save the OdPenaltyDetais
			FinODPenaltyRate oldFinODPenaltyRate = finODPenaltyRateDAO
					.getFinODPenaltyRateByRef(finODPenaltyRate.getFinReference(), "");
			finODPenaltyRateDAO.saveLog(oldFinODPenaltyRate, "_Log");
			finODPenaltyRateDAO.update(finODPenaltyRate, "");
		} catch (Exception e) {
			logger.error("Exception:" + e);
			APIErrorHandlerService.logUnhandledException(e);
			return APIErrorHandlerService.getFailedStatus();
		}
		logger.debug("Leaving");
		return APIErrorHandlerService.getSuccessStatus();

	}

	public FinanceDetail doChangeScheduleMethod(FinServiceInstruction finServiceInst, String eventCode) {

		logger.debug("Enteing");

		// fetch finance data
		FinanceDetail financeDetail = getFinanceDetails(finServiceInst, eventCode);
		if (financeDetail != null) {
			FinScheduleData finScheduleData = financeDetail.getFinScheduleData();

			/*
			 * // tempStartDate List<FinanceScheduleDetail> financeScheduleDetails = null; financeScheduleDetails =
			 * financeDetail.getFinScheduleData().getFinanceScheduleDetails(); if (financeScheduleDetails != null) { for
			 * (int i = 0; i < financeScheduleDetails.size(); i++) { FinanceScheduleDetail curSchd =
			 * financeScheduleDetails.get(i); if (curSchd.isRepayOnSchDate() || (curSchd.isPftOnSchDate() &&
			 * curSchd.getRepayAmount().compareTo(BigDecimal.ZERO) > 0)) { if
			 * (finServiceInst.getFromDate().compareTo(curSchd.getSchDate()) == 0) { break; } } } }
			 */

			FinanceMain financeMain = finScheduleData.getFinanceMain();

			finServiceInst.setFromDate(finServiceInst.getFromDate());
			financeMain.setEventFromDate(finServiceInst.getFromDate());
			financeMain.setFinSourceID(APIConstants.FINSOURCE_ID_API);
			financeMain.setRcdMaintainSts(FinanceConstants.FINSER_EVENT_CHGSCHDMETHOD);
			finServiceInst.setFinReference(financeMain.getFinReference());
			financeMain.setRecalSchdMethod(finServiceInst.getSchdMethod());
			financeMain.setDevFinCalReq(false);
			finServiceInst.setFinEvent(FinanceConstants.FINSER_EVENT_CHGSCHDMETHOD);

			try {
				// execute fee charges
				executeFeeCharges(financeDetail, finServiceInst, eventCode);
				if (financeDetail.getFinScheduleData().getErrorDetails() != null) {
					for (ErrorDetail errorDetail : financeDetail.getFinScheduleData().getErrorDetails()) {
						FinanceDetail response = new FinanceDetail();
						doEmptyResponseObject(response);
						response.setReturnStatus(
								APIErrorHandlerService.getFailedStatus(errorDetail.getCode(), errorDetail.getError()));
						return response;
					}
				}
				// Call Schedule calculator for Rate change
				finScheduleData = changeScheduleMethodService.doChangeScheduleMethod(finScheduleData, finServiceInst);
				if (finScheduleData.getErrorDetails() != null) {
					for (ErrorDetail errorDetail : finScheduleData.getErrorDetails()) {
						FinanceDetail response = new FinanceDetail();
						response.setReturnStatus(
								APIErrorHandlerService.getFailedStatus(errorDetail.getCode(), errorDetail.getError()));
						return response;
					}
				}
				financeDetail.setFinScheduleData(finScheduleData);
				// Get the response
				financeDetail = getResponse(financeDetail, finServiceInst);

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
		} else {
			financeDetail = new FinanceDetail();
			financeDetail.setReturnStatus(APIErrorHandlerService.getFailedStatus());
		}

		logger.debug("Leaving");
		return financeDetail;

	}

	/**
	 * Method for prepare finance detail response object
	 * 
	 * @param finScheduleData
	 * @return
	 */
	private FinanceDetail getServiceInstResponse(FinScheduleData finScheduleData) {
		logger.debug("Entering");

		FinanceDetail response = new FinanceDetail();
		response.setFinReference(finScheduleData.getFinReference());
		response.setFinScheduleData(finScheduleData);

		// Finance Summary details i.e Basic Calculator details
		FinanceSummary summaryDetail = getFinanceSummary(finScheduleData);
		response.getFinScheduleData().setFinanceSummary(summaryDetail);
		response.setReturnStatus(APIErrorHandlerService.getSuccessStatus());

		if (finScheduleData.getFinFeeDetailList() != null) {
			List<FinFeeDetail> srvFeeList = new ArrayList<FinFeeDetail>();
			for (FinFeeDetail feeDetail : finScheduleData.getFinFeeDetailList()) {
				if (!feeDetail.isOriginationFee()) {
					srvFeeList.add(feeDetail);
				}
			}
			finScheduleData.setFinFeeDetailList(srvFeeList);
			// summaryDetail.setFeeChargeAmt(summaryDetail.getFeeChargeAmt().add(totFeeAmount));
		}

		// Resetting Maturity Terms & Summary details rendering in case of
		// Reduce maturity cases
		resetScheduleDetail(finScheduleData);

		finScheduleData.setFinanceMain(null);
		finScheduleData.setDisbursementDetails(null);
		finScheduleData.setFinReference(null);
		finScheduleData.setRepayInstructions(null);
		finScheduleData.setRateInstruction(null);
		finScheduleData.setStepPolicyDetails(null);
		finScheduleData.setInsuranceList(null);
		finScheduleData.setFinODPenaltyRate(null);
		finScheduleData.setApiPlanEMIHDates(null);
		finScheduleData.setApiplanEMIHmonths(null);

		logger.debug("Entering");
		return response;
	}

	private FinanceSummary getFinanceSummary(FinScheduleData finScheduleData) {
		FinanceDetail financeDetail = new FinanceDetail();
		financeDetail.setFinScheduleData(finScheduleData);
		return getFinanceSummary(financeDetail);
	}

	/**
	 * Get Audit Header Details
	 */
	protected AuditHeader getAuditHeader(FinReceiptData finReceiptData, String tranType) {
		AuditDetail auditDetail = new AuditDetail(tranType, 1, null, finReceiptData);
		return new AuditHeader(finReceiptData.getFinReference(), null, null, null, auditDetail,
				finReceiptData.getReceiptHeader().getUserDetails(), new HashMap<String, ArrayList<ErrorDetail>>());
	}

	/**
	 * Get Audit Header Details
	 */
	protected AuditHeader getAuditHeader(FinanceDetail afinanceDetail, String tranType) {
		AuditDetail auditDetail = new AuditDetail(tranType, 1, afinanceDetail.getBefImage(), afinanceDetail);
		return new AuditHeader(afinanceDetail.getFinScheduleData().getFinReference(), null, null, null, auditDetail,
				afinanceDetail.getUserDetails(), new HashMap<String, ArrayList<ErrorDetail>>());
	}

	/**
	 * 
	 * @param financeDetail
	 * @param finServiceInst
	 * @return
	 * @throws JaxenException
	 */
	private FinanceDetail getResponse(FinanceDetail financeDetail, FinServiceInstruction finServiceInst)
			throws JaxenException {
		logger.debug("Entering");

		// fees calculation
		FinScheduleData finScheduleData = financeDetail.getFinScheduleData();
		if (!finScheduleData.getFinFeeDetailList().isEmpty()) {
			finScheduleData = FeeScheduleCalculator.feeSchdBuild(finScheduleData);
		}

		if (StringUtils.equals(finServiceInst.getReqType(), APIConstants.REQTYPE_POST)) {
			int version = financeDetail.getFinScheduleData().getFinanceMain().getVersion();
			financeDetail.getFinScheduleData().getFinanceMain().setVersion(version + 1);
			financeDetail.getFinScheduleData().setSchduleGenerated(true);
			List<FinServiceInstruction> finServiceInstructionList = new ArrayList<>();
			if (finServiceInst != null) {
				finServiceInstructionList.add(finServiceInst);
				financeDetail.getFinScheduleData().setFinServiceInstructions(finServiceInstructionList);
			}
			financeDetail.setUserDetails(SessionUserDetails.getUserDetails(SessionUserDetails.getLogiedInUser()));

			AuditHeader auditHeader = getAuditHeader(financeDetail, PennantConstants.TRAN_WF);
			FinanceDetail aFinanceDetail = (FinanceDetail) auditHeader.getAuditDetail().getModelData();

			aFinanceDetail = prepareInstructionObject(aFinanceDetail);

			// get the header details from the request
			APIHeader reqHeaderDetails = (APIHeader) PhaseInterceptorChain.getCurrentMessage().getExchange()
					.get(APIHeader.API_HEADER_KEY);
			auditHeader.setApiHeader(reqHeaderDetails);

			auditHeader = financeDetailService.doApprove(auditHeader, finServiceInst.isWif());
			if (auditHeader.getErrorMessage() != null) {
				for (ErrorDetail errorDetail : auditHeader.getErrorMessage()) {
					FinanceDetail response = new FinanceDetail();
					doEmptyResponseObject(response);
					response.setReturnStatus(
							APIErrorHandlerService.getFailedStatus(errorDetail.getCode(), errorDetail.getError()));
					return response;
				}
			}
			financeDetail = getServiceInstResponse(financeDetail.getFinScheduleData());
		} else {
			financeDetail = getServiceInstResponse(financeDetail.getFinScheduleData());
		}
		logger.debug("Leaving");
		return financeDetail;
	}

	/**
	 * Method for prepare fianceDetail object.<br>
	 * - Nullify the unnecessary data
	 * 
	 * @param aFinanceDetail
	 * @return
	 */
	private FinanceDetail prepareInstructionObject(FinanceDetail aFinanceDetail) {
		logger.debug("Entering");

		FinScheduleData finScheduleData = aFinanceDetail.getFinScheduleData();
		finScheduleData.getFinanceMain().setRecordType("");

		finScheduleData.setStepPolicyDetails(new ArrayList<FinanceStepPolicyDetail>(1));
		finScheduleData.setInsuranceList(new ArrayList<Insurance>());
		finScheduleData.setFinODPenaltyRate(null);
		finScheduleData.setFeeRules(new ArrayList<FeeRule>());

		aFinanceDetail.setFinContributorHeader(null);
		aFinanceDetail.setIndicativeTermDetail(null);
		aFinanceDetail.setEtihadCreditBureauDetail(null);
		aFinanceDetail.setBundledProductsDetail(null);
		aFinanceDetail.setTatDetail(null);
		aFinanceDetail.setFinAssetEvaluation(null);
		aFinanceDetail.setFinanceCheckList(new ArrayList<FinanceCheckListReference>(1));
		aFinanceDetail.setCheckList(new ArrayList<FinanceReferenceDetail>(1));
		aFinanceDetail.setAggrementList(new ArrayList<FinanceReferenceDetail>(1));
		aFinanceDetail.setEligibilityRuleList(new ArrayList<FinanceReferenceDetail>(1));
		aFinanceDetail.setFinElgRuleList(new ArrayList<FinanceEligibilityDetail>(1));
		aFinanceDetail.setGurantorsDetailList(new ArrayList<GuarantorDetail>(1));
		aFinanceDetail.setJountAccountDetailList(new ArrayList<JointAccountDetail>(1));
		aFinanceDetail.setContractorAssetDetails(new ArrayList<ContractorAssetDetail>(1));
		aFinanceDetail.setFinanceDeviations(new ArrayList<FinanceDeviations>());
		aFinanceDetail.setApprovedFinanceDeviations(new ArrayList<FinanceDeviations>());
		aFinanceDetail.setFinanceCollaterals(new ArrayList<FinCollaterals>(1));
		aFinanceDetail.setCollateralAssignmentList(new ArrayList<CollateralAssignment>(1));
		aFinanceDetail.setFinAssetTypesList(new ArrayList<FinAssetTypes>(1));
		aFinanceDetail.setExtendedFieldRenderList(new ArrayList<ExtendedFieldRender>(1));
		finScheduleData.setVasRecordingList(new ArrayList<VASRecording>(1));
		aFinanceDetail.setCovenantTypeList(null);
		aFinanceDetail.setMandate(null);
		aFinanceDetail.setFinFlagsDetails(null);

		logger.debug("Leaving");
		return aFinanceDetail;
	}

	/**
	 * prepare financeDetail object to process service request
	 * 
	 * @param finServiceInst
	 * @param acceventRatchg
	 * @return FinanceDetail
	 */
	public FinanceDetail getFinanceDetails(FinServiceInstruction finServiceInst, String eventCode) {
		logger.debug("Entering");

		FinanceDetail financeDetail = null;

		String finReference = finServiceInst.getFinReference();
		if (!finServiceInst.isWif()) {
			financeDetail = financeDetailService.getFinanceDetailById(finReference, false, "", false,
					FinanceConstants.FINSER_EVENT_ORG, "");
		} else {
			financeDetail = financeDetailService.getWIFFinance(finReference, false, null);
		}

		/*
		 * List<FinFeeDetail> finServicingFeeList = finFeeDetailService.getFinFeeDetailById(finReference, false,
		 * "_TView", eventCode); financeDetail.getFinScheduleData().setFinFeeDetailList( finServicingFeeList);
		 */

		List<FinFeeDetail> newList = new ArrayList<FinFeeDetail>();
		if (financeDetail != null) {
			if (financeDetail.getFinScheduleData().getFinFeeDetailList() != null) {
				for (FinFeeDetail feeDetail : financeDetail.getFinScheduleData().getFinFeeDetailList()) {
					if (feeDetail.isOriginationFee()) {
						feeDetail.setOriginationFee(true);
						feeDetail.setRcdVisible(false);
						feeDetail.setRecordType(PennantConstants.RCD_UPD);
						feeDetail.setRecordStatus(PennantConstants.RECORD_TYPE_UPD);
						newList.add(feeDetail);
					}
				}
			}
			financeDetail.getFinScheduleData().setFinFeeDetailList(newList);
			financeDetail.setAccountingEventCode(eventCode);
			LoggedInUser userDetails = SessionUserDetails.getUserDetails(SessionUserDetails.getLogiedInUser());
			financeDetail.getFinScheduleData().getFinanceMain().setUserDetails(userDetails);
			financeDetail.setEtihadCreditBureauDetail(null);
		}

		logger.debug("Leaving");

		return financeDetail;
	}

	/**
	 * Method for process Early settlement and partial payment requests
	 * 
	 * @param financeDetail
	 * @param finServiceInst
	 * @return
	 * @throws IllegalAccessException
	 * @throws InvocationTargetException
	 * @throws AccountNotFoundException
	 */
	public FinReceiptData doProcessPayments(FinReceiptData receiptData, FinServiceInstruction finServiceInst)
			throws IllegalAccessException, InvocationTargetException, AccountNotFoundException {
		logger.debug("Entering");

		if (finServiceInst.getFromDate() == null) {
			finServiceInst.setFromDate(DateUtility.getAppDate());
		}

		FinanceDetail financeDetail = receiptData.getFinanceDetail();
		FinScheduleData finScheduleData = financeDetail.getFinScheduleData();

		RepayData repayData = new RepayData();
		repayData.setBuildProcess("R");
		repayData.setFinanceDetail(financeDetail);
		FinanceMain financeMain = finScheduleData.getFinanceMain();
		List<FinanceScheduleDetail> financeScheduleDetails = finScheduleData.getFinanceScheduleDetails();
		Date valueDate = finServiceInst.getFromDate();

		// Initiate Repay calculations
		repayData.getRepayMain().setRepayAmountNow(finServiceInst.getAmount());
		repayData = repayCalculator.initiateRepay(repayData, financeMain, financeScheduleDetails, "", null, false,
				finServiceInst.getRecalType(), valueDate, finServiceInst.getModuleDefiner());
		repayData.setRepayMain(repayData.getRepayMain());

		String finEvent = AccountEventConstants.ACCEVENT_EARLYSTL;
		repayData.setEventCodeRef(finEvent);

		// call change frequency service
		manualPaymentService.doCalcRepayments(repayData, financeDetail, finServiceInst);

		FinScheduleData scheduleData = repayData.getFinanceDetail().getFinScheduleData();

		// Repayments Posting Process Execution
		// =====================================
		FinRepayHeader finRepayHeader = repayData.getFinRepayHeader();
		financeMain.setRepayAccountId(finRepayHeader.getRepayAccountId());
		Date valuedate = finServiceInst.getFromDate();

		FinanceProfitDetail tempPftDetail = profitDetailsDAO.getFinProfitDetailsById(financeMain.getFinReference());
		getAccrualService().calProfitDetails(financeMain, scheduleData.getFinanceScheduleDetails(), tempPftDetail,
				valuedate);

		List<RepayScheduleDetail> repaySchdList = repayData.getRepayScheduleDetails();

		for (FinReceiptDetail receiptDetail : receiptData.getReceiptHeader().getReceiptDetails()) {
			for (FinRepayHeader repayHeader : receiptDetail.getRepayHeaders()) {
				repayHeader.setRepayScheduleDetails(repaySchdList);
			}
		}
		logger.debug("Leaving");
		return receiptData;
	}

	/**
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

	public FinanceDetail doFeePayment(FinServiceInstruction finServiceInst) {
		logger.debug("Enteing");

		FinanceDetail response = null;
		try {
			List<ErrorDetail> errorDetails = upfrontFeeValidations(PennantConstants.VLD_CRT_LOAN, finServiceInst, true,
					AccountEventConstants.ACCEVENT_ADDDBSP);
			if (errorDetails.size() == 0) {
				errorDetails = feeReceiptService.processFeePayment(finServiceInst);
			}
			if (errorDetails != null) {
				for (ErrorDetail errorDetail : errorDetails) {
					response = new FinanceDetail();
					doEmptyResponseObject(response);
					response.setReturnStatus(
							APIErrorHandlerService.getFailedStatus(errorDetail.getCode(), errorDetail.getError()));
					return response;
				}
			}
			response = new FinanceDetail();
			doEmptyResponseObject(response);
			response.setReceiptId(finServiceInst.getReceiptId());
		} catch (InterfaceException ex) {
			logger.error("InterfaceException", ex);
			response = new FinanceDetail();
			doEmptyResponseObject(response);
			response.setReturnStatus(APIErrorHandlerService.getFailedStatus("9998", ex.getMessage()));
			return response;
		} catch (AppException appEx) {
			logger.error("AppException", appEx);
			response = new FinanceDetail();
			doEmptyResponseObject(response);
			response.setReturnStatus(APIErrorHandlerService.getFailedStatus("9999", appEx.getMessage()));
			return response;
		} catch (Exception e) {
			logger.error("Exception", e);
			APIErrorHandlerService.logUnhandledException(e);
			response = new FinanceDetail();
			doEmptyResponseObject(response);
			response.setReturnStatus(APIErrorHandlerService.getFailedStatus());
			return response;
		}

		logger.debug("Leaving");
		return response;
	}

	private List<ErrorDetail> upfrontFeeValidations(String vldGroup, FinServiceInstruction finServInst,
			boolean isAPICall, String eventCode) {
		List<ErrorDetail> errorDetails = new ArrayList<>();
		String finEvent = eventCode;
		boolean isOrigination = false;
		int vasFeeCount = 0;
		BigDecimal feePaidAmount = BigDecimal.ZERO;
		FinanceType finType = getFinanceTypeDAO().getFinanceTypeByFinType(finServInst.getFinType());
		List<FinFeeDetail> finFeeDetailList = new ArrayList<>();
		if (finServInst.getExternalReference() != null && !finServInst.getExternalReference().isEmpty()) {
			boolean isExtAssigned = getFinReceiptHeaderDAO().isExtRefAssigned(finServInst.getExternalReference());
			if (isExtAssigned) {
				String[] valueParm = new String[1];
				valueParm[0] = " External Reference Already Assigned to Finance ";
				errorDetails.add(ErrorUtil.getErrorDetail(new ErrorDetail("30550", valueParm)));
				return errorDetails;
			}
		}

		Date curBussDate = DateUtility.getAppDate();
		if (finServInst.getReceiptDetail() != null) {
			if (DateUtility.compare(finServInst.getReceiptDetail().getReceivedDate(), curBussDate) > 0) {
				FinanceDetail response = new FinanceDetail();
				doEmptyResponseObject(response);
				String[] valueParm = new String[2];
				valueParm[0] = "Received Date "
						+ DateUtility.formatToShortDate(finServInst.getReceiptDetail().getReceivedDate());
				valueParm[1] = "Application Date:" + DateUtility.formatToShortDate(DateUtility.getAppDate());
				errorDetails.add(ErrorUtil.getErrorDetail(new ErrorDetail("30568", valueParm)));
			}
		}

		if (finType != null) {// if given fintype is not confugured

			if (finServInst.getFinFeeDetails() != null && !finServInst.getFinFeeDetails().isEmpty()) {
				if (!StringUtils.equals(PennantConstants.VLD_SRV_LOAN, vldGroup)) {
					for (FinFeeDetail finFeeDetail : finServInst.getFinFeeDetails()) {
						if (StringUtils.equals(finFeeDetail.getFinEvent(), AccountEventConstants.ACCEVENT_VAS_FEE)) {
							vasFeeCount++;
						}
					}

					isOrigination = true;
				} else {
					for (FinFeeDetail finFeeDetail : finServInst.getFinFeeDetails()) {
						if (StringUtils.isNotBlank(finFeeDetail.getFeeScheduleMethod())) {
							String[] valueParm = new String[2];
							valueParm[0] = "Fee Schedule Method";
							valueParm[1] = finFeeDetail.getFeeTypeCode();
							errorDetails.add(ErrorUtil.getErrorDetail(new ErrorDetail("90269", valueParm)));
						}
					}
				}

				List<FinTypeFees> finTypeFeeDetail = null;
				finTypeFeeDetail = financeDetailService.getFinTypeFees(finType.getFinType(), finEvent, isOrigination,
						FinanceConstants.MODULEID_FINTYPE);
				if (finTypeFeeDetail != null) {
					finServInst.setFinTypeFeeList(finTypeFeeDetail);
					for (FinFeeDetail feeDetail : finServInst.getFinFeeDetails()) {
						BigDecimal finWaiverAmount = BigDecimal.ZERO;
						BigDecimal finPaidAMount = BigDecimal.ZERO;
						boolean isFeeCodeFound = false;
						for (FinTypeFees finTypeFee : finTypeFeeDetail) {
							if (StringUtils.equals(feeDetail.getFeeTypeCode(), finTypeFee.getFeeTypeCode())) {
								isFeeCodeFound = true;
								finPaidAMount = feeDetail.getPaidAmount();
								HashMap<String, Object> gstExecutionMap = this.finFeeDetailService
										.prepareGstMapping(finServInst.getFromBranch(), finServInst.getToBranch());
								if (finTypeFee.isTaxApplicable() && !gstExecutionMap.containsKey("fromState")) {
									String[] valueParm = new String[1];
									valueParm[0] = " GST not configured ";
									errorDetails.add(ErrorUtil.getErrorDetail(new ErrorDetail("30550", valueParm)));
									return errorDetails;
								}
								feeDetailService.setFinFeeDetails(finTypeFee, feeDetail, gstExecutionMap,
										finServInst.getCurrency());
								feePaidAmount = feePaidAmount
										.add(feeDetail.getPaidAmountOriginal().add(feeDetail.getPaidAmountGST()));
								// validate negative values
								if (feeDetail.getActualAmount().compareTo(BigDecimal.ZERO) < 0
										|| feeDetail.getPaidAmount().compareTo(BigDecimal.ZERO) < 0
										|| feeDetail.getWaivedAmount().compareTo(BigDecimal.ZERO) < 0) {
									String[] valueParm = new String[1];
									valueParm[0] = feeDetail.getFeeTypeCode();
									errorDetails.add(ErrorUtil.getErrorDetail(new ErrorDetail("90259", valueParm)));
									return errorDetails;
								}

								// validate actual amount and paid amount
								if (finPaidAMount.compareTo(feeDetail.getActualAmount()) != 0) {
									String[] valueParm = new String[1];
									valueParm[0] = feeDetail.getFeeTypeCode() + " Paid amount must be  "
											+ feeDetail.getActualAmount();
									errorDetails.add(ErrorUtil.getErrorDetail(new ErrorDetail("30550", valueParm)));
									return errorDetails;
								}

								// validate fee schedule method
								if (!finTypeFee.isAlwModifyFeeSchdMthd() && !StringUtils
										.equals(feeDetail.getFeeScheduleMethod(), finTypeFee.getFeeScheduleMethod())) {
									String[] valueParm = new String[1];
									valueParm[0] = feeDetail.getFeeTypeCode();
									errorDetails.add(ErrorUtil.getErrorDetail(new ErrorDetail("90246", valueParm)));
									return errorDetails;
								}

								// validate paid by Customer method
								if (StringUtils.equals(finTypeFee.getFeeScheduleMethod(),
										CalculationConstants.REMFEE_PAID_BY_CUSTOMER)) {
									if (feeDetail.getPaidAmount().compareTo(finTypeFee.getAmount()) != 0) {
										String[] valueParm = new String[1];
										valueParm[0] = finTypeFee.getFeeTypeCode();
										errorDetails.add(ErrorUtil.getErrorDetail(new ErrorDetail("90254", valueParm)));
										return errorDetails;
									}
								}
								// validate waived by bank method
								if (StringUtils.equals(finTypeFee.getFeeScheduleMethod(),
										CalculationConstants.REMFEE_WAIVED_BY_BANK)) {
									if (feeDetail.getWaivedAmount().compareTo(finWaiverAmount) != 0) {
										String[] valueParm = new String[3];
										valueParm[0] = "Waiver amount";
										valueParm[1] = "Actual waiver amount:" + String.valueOf(finWaiverAmount);
										valueParm[2] = feeDetail.getFeeTypeCode();
										errorDetails.add(ErrorUtil.getErrorDetail(new ErrorDetail("90258", valueParm)));
										return errorDetails;
									}

									// validate paid by Customer method
									if (StringUtils.equals(finTypeFee.getFeeScheduleMethod(),
											CalculationConstants.REMFEE_PAID_BY_CUSTOMER)) {
										if (feeDetail.getPaidAmount().compareTo(finTypeFee.getAmount()) != 0) {
											String[] valueParm = new String[1];
											valueParm[0] = finTypeFee.getFeeTypeCode();
											errorDetails
													.add(ErrorUtil.getErrorDetail(new ErrorDetail("90254", valueParm)));
											return errorDetails;
										}
									}
									// validate waived by bank method
									if (StringUtils.equals(finTypeFee.getFeeScheduleMethod(),
											CalculationConstants.REMFEE_WAIVED_BY_BANK)) {
										if (feeDetail.getWaivedAmount().compareTo(finWaiverAmount) != 0) {
											String[] valueParm = new String[3];
											valueParm[0] = "Waiver amount";
											valueParm[1] = "Actual waiver amount:" + String.valueOf(finWaiverAmount);
											valueParm[2] = feeDetail.getFeeTypeCode();
											errorDetails
													.add(ErrorUtil.getErrorDetail(new ErrorDetail("90258", valueParm)));
											return errorDetails;
										}
									}
								}
							}
						}
						if (!isFeeCodeFound) {
							String[] valueParm = new String[1];
							errorDetails.add(ErrorUtil.getErrorDetail(new ErrorDetail("90247", valueParm)));
							return errorDetails;
						}
					}

				} else {
					String[] valueParm = new String[1];
					valueParm[0] = finServInst.getFinType();
					errorDetails.add(ErrorUtil.getErrorDetail(new ErrorDetail("90245", valueParm)));
					return errorDetails;
				}
				if (feePaidAmount.compareTo(finServInst.getAmount()) != 0) {
					String[] valueParm = new String[1];
					valueParm[0] = "Amount must match with sum of  fees paid amounts";
					errorDetails.add(ErrorUtil.getErrorDetail(new ErrorDetail("30550", valueParm)));
					return errorDetails;
				}

			}
		} else {

			String[] valueParm = new String[1];
			valueParm[0] = "finType " + finServInst.getFinType() + " is invalid";
			errorDetails.add(ErrorUtil.getErrorDetail(new ErrorDetail("30550", valueParm)));
			return errorDetails;
		}
		return errorDetails;
	}

	/**
	 * Method for fetch Customer Loan Details of corresponding userID
	 * 
	 * @param userID
	 * @return CustomerODLoanDetails
	 */
	public List<LoanPendingData> getCustomerODLoanDetails(long userID) {
		return financeMainService.getCustomerODLoanDetails(userID);
	}

	public void setFinanceDetailService(FinanceDetailService financeDetailService) {
		this.financeDetailService = financeDetailService;
	}

	public void setRateChangeService(RateChangeService rateChangeService) {
		this.rateChangeService = rateChangeService;
	}

	public void setAddRepaymentService(AddRepaymentService addRepaymentService) {
		this.addRepaymentService = addRepaymentService;
	}

	public void setRmvTermsService(RemoveTermsService rmvTermsService) {
		this.rmvTermsService = rmvTermsService;
	}

	public void setRecalService(RecalculateService recalService) {
		this.recalService = recalService;
	}

	public void setChangeProfitService(ChangeProfitService changeProfitService) {
		this.changeProfitService = changeProfitService;
	}

	public void setAddDisbursementService(AddDisbursementService addDisbursementService) {
		this.addDisbursementService = addDisbursementService;
	}

	public void setChangeFrequencyService(ChangeFrequencyService changeFrequencyService) {
		this.changeFrequencyService = changeFrequencyService;
	}

	public void setReScheduleService(ReScheduleService reScheduleService) {
		this.reScheduleService = reScheduleService;
	}

	public void setFinanceMainService(FinanceMainService financeMainService) {
		this.financeMainService = financeMainService;
	}

	public void setFinODPenaltyRateDAO(FinODPenaltyRateDAO finODPenaltyRateDAO) {
		this.finODPenaltyRateDAO = finODPenaltyRateDAO;
	}

	public void setPostponementService(PostponementService postponementService) {
		this.postponementService = postponementService;
	}

	public void setFeeDetailService(FeeDetailService feeDetailService) {
		this.feeDetailService = feeDetailService;
	}

	public void setBankBranchService(BankBranchService bankBranchService) {
		this.bankBranchService = bankBranchService;
	}

	public void setFinAdvancePaymentsService(FinAdvancePaymentsService finAdvancePaymentsService) {
		this.finAdvancePaymentsService = finAdvancePaymentsService;
	}

	public void setReceiptService(ReceiptService receiptService) {
		this.receiptService = receiptService;
	}

	public void setFinTypePartnerBankService(FinTypePartnerBankService finTypePartnerBankService) {
		this.finTypePartnerBankService = finTypePartnerBankService;
	}

	public void setReceiptCalculator(ReceiptCalculator receiptCalculator) {
		this.receiptCalculator = receiptCalculator;
	}

	public void setPartnerBankDAO(PartnerBankDAO partnerBankDAO) {
		this.partnerBankDAO = partnerBankDAO;
	}

	public void setRepayCalculator(RepayCalculator repayCalculator) {
		this.repayCalculator = repayCalculator;
	}

	public void setManualPaymentService(ManualPaymentService manualPaymentService) {
		this.manualPaymentService = manualPaymentService;
	}

	public void setProfitDetailsDAO(FinanceProfitDetailDAO profitDetailsDAO) {
		this.profitDetailsDAO = profitDetailsDAO;
	}

	public void setFinODDetailsDAO(FinODDetailsDAO finODDetailsDAO) {
		this.finODDetailsDAO = finODDetailsDAO;
	}

	public FinanceTypeDAO getFinanceTypeDAO() {
		return financeTypeDAO;
	}

	public void setFinanceTypeDAO(FinanceTypeDAO financeTypeDAO) {
		this.financeTypeDAO = financeTypeDAO;
	}

	public FeeReceiptService getFeeReceiptService() {
		return feeReceiptService;
	}

	public void setFeeReceiptService(FeeReceiptService feeReceiptService) {
		this.feeReceiptService = feeReceiptService;
	}

	public FinFeeDetailService getFinFeeDetailService() {
		return finFeeDetailService;
	}

	public void setFinFeeDetailService(FinFeeDetailService finFeeDetailService) {
		this.finFeeDetailService = finFeeDetailService;
	}

	public FinReceiptHeaderDAO getFinReceiptHeaderDAO() {
		return finReceiptHeaderDAO;
	}

	public void setFinReceiptHeaderDAO(FinReceiptHeaderDAO finReceiptHeaderDAO) {
		this.finReceiptHeaderDAO = finReceiptHeaderDAO;
	}

	public CancelDisbursementService getCancelDisbursementService() {
		return cancelDisbursementService;
	}

	public void setCancelDisbursementService(CancelDisbursementService cancelDisbursementService) {
		this.cancelDisbursementService = cancelDisbursementService;
	}

	public void setBankDetailService(BankDetailService bankDetailService) {
		this.bankDetailService = bankDetailService;
	}

	public ZIPCodeDetailsService getzIPCodeDetailsService() {
		return zIPCodeDetailsService;
	}

	public void setzIPCodeDetailsService(ZIPCodeDetailsService zIPCodeDetailsService) {
		this.zIPCodeDetailsService = zIPCodeDetailsService;
	}

	public void setChangeScheduleMethodService(ChangeScheduleMethodService changeScheduleMethodService) {
		this.changeScheduleMethodService = changeScheduleMethodService;
	}

	public ExtendedFieldHeaderDAO getExtendedFieldHeaderDAO() {
		return extendedFieldHeaderDAO;
	}

	public void setExtendedFieldHeaderDAO(ExtendedFieldHeaderDAO extendedFieldHeaderDAO) {
		this.extendedFieldHeaderDAO = extendedFieldHeaderDAO;
	}

	public ExtendedFieldDetailsService getExtendedFieldDetailsService() {
		return extendedFieldDetailsService;
	}

	public void setExtendedFieldDetailsService(ExtendedFieldDetailsService extendedFieldDetailsService) {
		this.extendedFieldDetailsService = extendedFieldDetailsService;
	}

	public ExtendedFieldRenderDAO getExtendedFieldRenderDAO() {
		return extendedFieldRenderDAO;
	}

	public void setExtendedFieldRenderDAO(ExtendedFieldRenderDAO extendedFieldRenderDAO) {
		this.extendedFieldRenderDAO = extendedFieldRenderDAO;
	}

	public FinServiceInstrutionDAO getFinServiceInstructionDAO() {
		return finServiceInstructionDAO;
	}

	public void setFinServiceInstructionDAO(FinServiceInstrutionDAO finServiceInstructionDAO) {
		this.finServiceInstructionDAO = finServiceInstructionDAO;
	}

	public FinReceiptDetailDAO getFinReceiptDetailDAO() {
		return finReceiptDetailDAO;
	}

	public void setFinReceiptDetailDAO(FinReceiptDetailDAO finReceiptDetailDAO) {
		this.finReceiptDetailDAO = finReceiptDetailDAO;
	}
	
	public FinanceWorkFlowService getFinanceWorkFlowService() {
		return financeWorkFlowService;
	}
	
	protected String getTaskAssignmentMethod(String taskId) {
		return workFlow.getUserTask(taskId).getAssignmentLevel();
	}
}