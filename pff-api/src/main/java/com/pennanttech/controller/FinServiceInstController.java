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
import com.pennant.app.util.SessionUserDetails;
import com.pennant.app.util.SysParamUtil;
import com.pennant.backend.dao.finance.FinODDetailsDAO;
import com.pennant.backend.dao.finance.FinODPenaltyRateDAO;
import com.pennant.backend.dao.finance.FinanceProfitDetailDAO;
import com.pennant.backend.dao.partnerbank.PartnerBankDAO;
import com.pennant.backend.financeservice.AddDisbursementService;
import com.pennant.backend.financeservice.AddRepaymentService;
import com.pennant.backend.financeservice.ChangeFrequencyService;
import com.pennant.backend.financeservice.ChangeProfitService;
import com.pennant.backend.financeservice.ChangeScheduleMethodService;
import com.pennant.backend.financeservice.PostponementService;
import com.pennant.backend.financeservice.RateChangeService;
import com.pennant.backend.financeservice.ReScheduleService;
import com.pennant.backend.financeservice.RecalculateService;
import com.pennant.backend.financeservice.RemoveTermsService;
import com.pennant.backend.model.WSReturnStatus;
import com.pennant.backend.model.Repayments.FinanceRepayments;
import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.model.bmtmasters.BankBranch;
import com.pennant.backend.model.collateral.CollateralAssignment;
import com.pennant.backend.model.configuration.VASRecording;
import com.pennant.backend.model.extendedfield.ExtendedFieldRender;
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
import com.pennant.backend.model.finance.contractor.ContractorAssetDetail;
import com.pennant.backend.model.lmtmasters.FinanceCheckListReference;
import com.pennant.backend.model.lmtmasters.FinanceReferenceDetail;
import com.pennant.backend.model.partnerbank.PartnerBank;
import com.pennant.backend.model.rmtmasters.FinanceType;
import com.pennant.backend.model.rulefactory.FeeRule;
import com.pennant.backend.service.bmtmasters.BankBranchService;
import com.pennant.backend.service.fees.FeeDetailService;
import com.pennant.backend.service.finance.FinAdvancePaymentsService;
import com.pennant.backend.service.finance.FinanceDetailService;
import com.pennant.backend.service.finance.FinanceMainService;
import com.pennant.backend.service.finance.ManualPaymentService;
import com.pennant.backend.service.finance.ReceiptService;
import com.pennant.backend.service.rmtmasters.FinTypePartnerBankService;
import com.pennant.backend.util.DisbursementConstants;
import com.pennant.backend.util.FinanceConstants;
import com.pennant.backend.util.PennantApplicationUtil;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.RepayConstants;
import com.pennanttech.pennapps.core.AppException;
import com.pennanttech.pennapps.core.InterfaceException;
import com.pennanttech.pennapps.core.model.ErrorDetail;
import com.pennanttech.pennapps.core.model.LoggedInUser;
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
	private ChangeFrequencyService changeFrequencyService;
	private ReScheduleService reScheduleService;
	private PostponementService postponementService;
	private RemoveTermsService rmvTermsService;
	private FinanceMainService financeMainService;
	private FinODPenaltyRateDAO finODPenaltyRateDAO;
	private FeeDetailService feeDetailService;
	private BankBranchService bankBranchService;
	private FinAdvancePaymentsService finAdvancePaymentsService;
	private ReceiptService receiptService;
	private FinTypePartnerBankService finTypePartnerBankService;
	private ReceiptCalculator receiptCalculator;
	private PartnerBankDAO partnerBankDAO;
	private ManualPaymentService manualPaymentService;
	private RepayCalculator repayCalculator;
	private FinanceProfitDetailDAO profitDetailsDAO;
	private ChangeScheduleMethodService	changeScheduleMethodService;

	public void setChangeScheduleMethodService(ChangeScheduleMethodService changeScheduleMethodService) {
		this.changeScheduleMethodService = changeScheduleMethodService;
	}

	/**
	 * Method for process AddRateChange request and re-calculate schedule
	 * details
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

			finServiceInst.setModuleDefiner(FinanceConstants.FINSER_EVENT_RATECHG);
			try {
				// execute fee charges
				executeFeeCharges(financeDetail, finServiceInst, eventCode);
				if (financeDetail.getFinScheduleData().getErrorDetails() != null) {
					for (ErrorDetail errorDetail : financeDetail.getFinScheduleData().getErrorDetails()) {
						FinanceDetail response = new FinanceDetail();
						doEmptyResponseObject(response);
						response.setReturnStatus(APIErrorHandlerService.getFailedStatus(errorDetail.getCode(),
								errorDetail.getError()));
						return response;
					}
				}

				// call schedule calculator for Rate change
				finScheduleData = rateChangeService.getRateChangeDetails(finScheduleData, finServiceInst,
						FinanceConstants.FINSER_EVENT_RATECHG);

				if (finScheduleData.getErrorDetails() != null) {
					for (ErrorDetail errorDetail : finScheduleData.getErrorDetails()) {
						FinanceDetail response = new FinanceDetail();
						response.setReturnStatus(APIErrorHandlerService.getFailedStatus(errorDetail.getCode(),
								errorDetail.getError()));
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
				feeDetailService.doProcessFeesForInquiry(financeDetail, eventCode, finServiceInst);
			} else {
				for (FinFeeDetail finFeeDetail : finServiceInst.getFinFeeDetails()) {
					finFeeDetail.setFinEvent(eventCode);
					financeDetail.getFinScheduleData().getFinFeeDetailList().add(finFeeDetail);
					finFeeDetail.setFeeScheduleMethod(PennantConstants.List_Select);
				}
				feeDetailService.doExecuteFeeCharges(financeDetail, eventCode, finServiceInst);

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
						response.setReturnStatus(APIErrorHandlerService.getFailedStatus(errorDetail.getCode(),
								errorDetail.getError()));
						return response;
					}
				}
				// Call Schedule calculator for Rate change
				finScheduleData = addRepaymentService.getAddRepaymentDetails(finScheduleData, finServiceInst);

				if (finScheduleData.getErrorDetails() != null) {
					for (ErrorDetail errorDetail : finScheduleData.getErrorDetails()) {
						FinanceDetail response = new FinanceDetail();
						response.setReturnStatus(APIErrorHandlerService.getFailedStatus(errorDetail.getCode(),
								errorDetail.getError()));
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
						response.setReturnStatus(APIErrorHandlerService.getFailedStatus(errorDetail.getCode(),
								errorDetail.getError()));
						return response;
					}
				}

				// call deferment service
				finScheduleData = postponementService.doUnPlannedEMIH(finScheduleData);
				if (finScheduleData.getErrorDetails() != null) {
					for (ErrorDetail errorDetail : finScheduleData.getErrorDetails()) {
						FinanceDetail response = new FinanceDetail();
						response.setReturnStatus(APIErrorHandlerService.getFailedStatus(errorDetail.getCode(),
								errorDetail.getError()));
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
						response.setReturnStatus(APIErrorHandlerService.getFailedStatus(errorDetail.getCode(),
								errorDetail.getError()));
						return response;
					}
				}

				// Call Schedule calculator for Rate change
				finScheduleData = recalService.getRecalculateSchdDetails(finScheduleData);

				if (finScheduleData.getErrorDetails() != null) {
					for (ErrorDetail errorDetail : finScheduleData.getErrorDetails()) {
						FinanceDetail response = new FinanceDetail();
						doEmptyResponseObject(response);
						response.setReturnStatus(APIErrorHandlerService.getFailedStatus(errorDetail.getCode(),
								errorDetail.getError()));
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
						response.setReturnStatus(APIErrorHandlerService.getFailedStatus(errorDetail.getCode(),
								errorDetail.getError()));
						return response;
					}
				}
				// Call Schedule calculator for Rate change
				finScheduleData = recalService.getRecalculateSchdDetails(finScheduleData);

				if (finScheduleData.getErrorDetails() != null) {
					for (ErrorDetail errorDetail : finScheduleData.getErrorDetails()) {
						FinanceDetail response = new FinanceDetail();
						response.setReturnStatus(APIErrorHandlerService.getFailedStatus(errorDetail.getCode(),
								errorDetail.getError()));
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
						response.setReturnStatus(APIErrorHandlerService.getFailedStatus(errorDetail.getCode(),
								errorDetail.getError()));
						return response;
					}
				}
				// Call Schedule calculator for Rate change
				finScheduleData = changeProfitService.getChangeProfitDetails(finScheduleData, amount);

				if (finScheduleData.getErrorDetails() != null) {
					for (ErrorDetail errorDetail : finScheduleData.getErrorDetails()) {
						FinanceDetail response = new FinanceDetail();
						response.setReturnStatus(APIErrorHandlerService.getFailedStatus(errorDetail.getCode(),
								errorDetail.getError()));
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
		logger.debug("Enteing");

		if (financeDetail != null) {
			FinScheduleData finScheduleData = financeDetail.getFinScheduleData();
			FinanceMain financeMain = finScheduleData.getFinanceMain();

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
			
			// set finAssetValue = FinCurrAssetValue when there is no maxDisbCheck
			FinanceType finType = financeDetail.getFinScheduleData().getFinanceType();
			if(!finType.isAlwMaxDisbCheckReq()) {
				financeMain.setFinAssetValue(financeMain.getFinCurrAssetValue());
			}
			
			finServiceInst.setModuleDefiner(FinanceConstants.FINSER_EVENT_ADDDISB);
			try {
				// execute fee charges
				executeFeeCharges(financeDetail, finServiceInst, eventCode);
				if (financeDetail.getFinScheduleData().getErrorDetails() != null) {
					for (ErrorDetail errorDetail : financeDetail.getFinScheduleData().getErrorDetails()) {
						FinanceDetail response = new FinanceDetail();
						doEmptyResponseObject(response);
						response.setReturnStatus(APIErrorHandlerService.getFailedStatus(errorDetail.getCode(),
								errorDetail.getError()));
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
						finScheduleData.setErrorDetail(ErrorUtil.getErrorDetail(
								new ErrorDetail(erroDetails.getCode(), erroDetails.getParameters())));
					}
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
						response.setReturnStatus(APIErrorHandlerService.getFailedStatus(errorDetail.getCode(),
								errorDetail.getError()));
						return response;
					}
				}
				financeDetail.setFinScheduleData(finScheduleData);

				// process disbursement details
				LoggedInUser userDetails = SessionUserDetails.getUserDetails(SessionUserDetails.getLogiedInUser());
				// financeDetail.getFinScheduleData().getFinanceMain().setRecordType(PennantConstants.RECORD_TYPE_NEW);
				List<FinAdvancePayments> advancePayments = financeDetail.getAdvancePaymentsList();
				if (advancePayments != null) {
					for (FinAdvancePayments advPayment : advancePayments) {
						int paymentSeq = finAdvancePaymentsService
								.getCountByFinReference(financeMain.getFinReference());
						advPayment.setFinReference(financeMain.getFinReference());
						advPayment.setRecordType(PennantConstants.RECORD_TYPE_NEW);
						advPayment.setNewRecord(true);
						advPayment.setLastMntBy(userDetails.getUserId());
						advPayment.setLastMntOn(new Timestamp(System.currentTimeMillis()));
						advPayment.setRecordStatus(PennantConstants.RCD_STATUS_APPROVED);
						advPayment.setUserDetails(financeMain.getUserDetails());
						advPayment.setPaymentSeq(paymentSeq + 1);
						advPayment.setDisbCCy(financeMain.getFinCcy());

						if (StringUtils.equals(advPayment.getPaymentType(), DisbursementConstants.PAYMENT_TYPE_IMPS)
								|| StringUtils.equals(advPayment.getPaymentType(),
										DisbursementConstants.PAYMENT_TYPE_NEFT)
								|| StringUtils.equals(advPayment.getPaymentType(),
										DisbursementConstants.PAYMENT_TYPE_RTGS)) {

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
						response.setReturnStatus(APIErrorHandlerService.getFailedStatus(errorDetail.getCode(),
								errorDetail.getError()));
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
						response.setReturnStatus(APIErrorHandlerService.getFailedStatus(errorDetail.getCode(),
								errorDetail.getError()));
						return response;
					}
				}
				// Call Schedule calculator for Rate change
				finScheduleData = rmvTermsService.getRmvTermsDetails(finScheduleData);
				if (finScheduleData.getErrorDetails() != null) {
					for (ErrorDetail errorDetail : finScheduleData.getErrorDetails()) {
						FinanceDetail response = new FinanceDetail();
						response.setReturnStatus(APIErrorHandlerService.getFailedStatus(errorDetail.getCode(),
								errorDetail.getError()));

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
						response.setReturnStatus(APIErrorHandlerService.getFailedStatus(errorDetail.getCode(),
								errorDetail.getError()));
						return response;
					}
				}
				// Call Schedule calculator for Rate change
				finScheduleData = reScheduleService.doReSchedule(finScheduleData, finServiceInst);
				if (finScheduleData.getErrorDetails() != null) {
					for (ErrorDetail errorDetail : finScheduleData.getErrorDetails()) {
						FinanceDetail response = new FinanceDetail();
						response.setReturnStatus(APIErrorHandlerService.getFailedStatus(errorDetail.getCode(),
								errorDetail.getError()));
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
		finServiceInst.setModuleDefiner(FinanceConstants.FINSER_EVENT_EARLYRPY);

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

		FinanceDetail response = null;
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
			String purpose)
			throws Exception {
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
			finServiceInst.setReceiptDetail(finReceiptDetail);
		} else {
			if (finReceiptDetail.getReceivedDate() == null) {
				finReceiptDetail.setReceivedDate(DateUtility.getAppDate());
			}
		}
		finReceiptDetail.setReceivedDate(DateUtility
				.getDBDate(DateUtility.formatDate(finReceiptDetail.getReceivedDate(), PennantConstants.DBDateFormat)));
		receiptHeader.setReceiptDate(finReceiptDetail.getReceivedDate());
		finReceiptDetail.setReceiptType(RepayConstants.RECEIPTTYPE_RECIPT);
		finReceiptDetail.setPaymentTo(RepayConstants.RECEIPTTO_FINANCE);
		finReceiptDetail.setPaymentType(finServiceInst.getPaymentMode());
		finReceiptDetail.setAmount(finServiceInst.getAmount());
		receiptHeader.getReceiptDetails().add(finReceiptDetail);

		receiptHeader.setRemarks(finReceiptDetail.getRemarks());
		finReceiptData.setReceiptHeader(receiptHeader);
		finReceiptData.setFinanceDetail(financeDetail);
		finReceiptData.setFinReference(finServiceInst.getFinReference());
		finReceiptData.setSourceId(PennantConstants.FINSOURCE_ID_API);

		FinanceMain financeMain = financeDetail.getFinScheduleData().getFinanceMain();
		Date receiDate = DateUtility
				.getDBDate(DateUtility.formatDate(finReceiptDetail.getReceivedDate(), PennantConstants.DBDateFormat));
		finReceiptDetail.setReceivedDate(receiDate);

		Date curBussDate = DateUtility.getAppDate();
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

		// calculate allocations
		Map<String, BigDecimal> allocationMap = receiptCalculator.recalAutoAllocation(
				financeDetail.getFinScheduleData(), totReceiptAmt, finReceiptDetail.getReceivedDate(),
				receiptHeader.getReceiptPurpose(), false);
		finReceiptData.setAllocationMap(allocationMap);

		// validate repayment amount
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
			finReceiptData = receiptCalculator.initiateReceipt(finReceiptData, scheduleData, valueDate, purpose, false);
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

		// set fees details into finReceiptData
		finScheduleData = finReceiptData.getFinanceDetail().getFinScheduleData();
		finScheduleData.setFinFeeDetailList(aFinanceDetail.getFinScheduleData().getFinFeeDetailList());
		if (StringUtils.equals(finServiceInst.getReqType(), APIConstants.REQTYPE_POST)) {
			int count = finTypePartnerBankService.getPartnerBankCount(financeMain.getFinType(),
					finServiceInst.getPaymentMode(), AccountConstants.PARTNERSBANK_RECEIPTS,
					finReceiptDetail.getFundingAc());
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

			if (StringUtils.equals(purpose, FinanceConstants.FINSER_EVENT_EARLYSETTLE)) {
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
			// Set Version value
			int version = finReceiptData.getFinanceDetail().getFinScheduleData().getFinanceMain().getVersion();
			finReceiptData.getFinanceDetail().getFinScheduleData().getFinanceMain().setVersion(version + 1);
			finReceiptData.getFinanceDetail().getFinScheduleData().getFinanceMain().setRecordType("");
			finReceiptData.getFinanceDetail().getFinScheduleData().setSchduleGenerated(true);

			// Save the Schedule details
			AuditHeader auditHeader = getAuditHeader(finReceiptData, PennantConstants.TRAN_WF);

			// Get the header details from the request
			APIHeader reqHeaderDetails = (APIHeader) PhaseInterceptorChain.getCurrentMessage().getExchange()
					.get(APIHeader.API_HEADER_KEY);
			// set the headerDetails to AuditHeader
			auditHeader.setApiHeader(reqHeaderDetails);

			auditHeader = receiptService.doApprove(auditHeader);

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

		/*	// tempStartDate
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
			}*/

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
						response.setReturnStatus(APIErrorHandlerService.getFailedStatus(errorDetail.getCode(),
								errorDetail.getError()));
						return response;
					}
				}
				// Call Schedule calculator for Rate change
				finScheduleData = changeScheduleMethodService.doChangeScheduleMethod(finScheduleData, finServiceInst);
				if (finScheduleData.getErrorDetails() != null) {
					for (ErrorDetail errorDetail : finScheduleData.getErrorDetails()) {
						FinanceDetail response = new FinanceDetail();
						response.setReturnStatus(APIErrorHandlerService.getFailedStatus(errorDetail.getCode(),
								errorDetail.getError()));
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
			//summaryDetail.setFeeChargeAmt(summaryDetail.getFeeChargeAmt().add(totFeeAmount));
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
	 * @throws InterfaceException
	 * @throws JaxenException
	 */
	private FinanceDetail getResponse(FinanceDetail financeDetail, FinServiceInstruction finServiceInst)
			throws JaxenException, InterfaceException {
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
		 * List<FinFeeDetail> finServicingFeeList =
		 * finFeeDetailService.getFinFeeDetailById(finReference, false,
		 * "_TView", eventCode);
		 * financeDetail.getFinScheduleData().setFinFeeDetailList(
		 * finServicingFeeList);
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
}
