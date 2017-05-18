package com.pennanttech.controller;

import java.lang.reflect.InvocationTargetException;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.cxf.phase.PhaseInterceptorChain;
import org.apache.log4j.Logger;
import org.jaxen.JaxenException;

import com.pennant.app.constants.AccountEventConstants;
import com.pennant.app.constants.CalculationConstants;
import com.pennant.app.util.APIHeader;
import com.pennant.app.util.DateUtility;
import com.pennant.app.util.ErrorUtil;
import com.pennant.app.util.FeeScheduleCalculator;
import com.pennant.app.util.RepaymentProcessUtil;
import com.pennant.app.util.SessionUserDetails;
import com.pennant.backend.dao.finance.FinODPenaltyRateDAO;
import com.pennant.backend.dao.finance.FinanceProfitDetailDAO;
import com.pennant.backend.dao.finance.FinanceScheduleDetailDAO;
import com.pennant.backend.financeservice.AddDisbursementService;
import com.pennant.backend.financeservice.AddRepaymentService;
import com.pennant.backend.financeservice.AddTermsService;
import com.pennant.backend.financeservice.ChangeFrequencyService;
import com.pennant.backend.financeservice.ChangeProfitService;
import com.pennant.backend.financeservice.PostponementService;
import com.pennant.backend.financeservice.RateChangeService;
import com.pennant.backend.financeservice.ReScheduleService;
import com.pennant.backend.financeservice.RecalculateService;
import com.pennant.backend.financeservice.RemoveTermsService;
import com.pennant.backend.model.ErrorDetails;
import com.pennant.backend.model.LoggedInUser;
import com.pennant.backend.model.WSReturnStatus;
import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.model.bmtmasters.BankBranch;
import com.pennant.backend.model.collateral.CollateralAssignment;
import com.pennant.backend.model.configuration.VASRecording;
import com.pennant.backend.model.customermasters.Customer;
import com.pennant.backend.model.finance.FinAdvancePayments;
import com.pennant.backend.model.finance.FinAssetTypes;
import com.pennant.backend.model.finance.FinCollaterals;
import com.pennant.backend.model.finance.FinFeeDetail;
import com.pennant.backend.model.finance.FinODPenaltyRate;
import com.pennant.backend.model.finance.FinReceiptData;
import com.pennant.backend.model.finance.FinReceiptDetail;
import com.pennant.backend.model.finance.FinReceiptHeader;
import com.pennant.backend.model.finance.FinScheduleData;
import com.pennant.backend.model.finance.FinServiceInstruction;
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
import com.pennant.backend.model.finance.contractor.ContractorAssetDetail;
import com.pennant.backend.model.lmtmasters.FinanceCheckListReference;
import com.pennant.backend.model.lmtmasters.FinanceReferenceDetail;
import com.pennant.backend.model.rulefactory.FeeRule;
import com.pennant.backend.model.staticparms.ExtendedFieldRender;
import com.pennant.backend.service.bmtmasters.BankBranchService;
import com.pennant.backend.service.fees.FeeDetailService;
import com.pennant.backend.service.finance.FinAdvancePaymentsService;
import com.pennant.backend.service.finance.FinanceDetailService;
import com.pennant.backend.service.finance.FinanceMainService;
import com.pennant.backend.service.finance.ReceiptService;
import com.pennant.backend.service.rmtmasters.FinTypePartnerBankService;
import com.pennant.backend.util.DisbursementConstants;
import com.pennant.backend.util.FinanceConstants;
import com.pennant.backend.util.PennantApplicationUtil;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.RepayConstants;
import com.pennant.exception.PFFInterfaceException;
import com.pennanttech.util.APIConstants;
import com.pennanttech.ws.service.APIErrorHandlerService;
import com.rits.cloning.Cloner;

public class FinServiceInstController extends SummaryDetailService {

	private final static Logger			logger	= Logger.getLogger(FinServiceInstController.class);

	private FinanceDetailService		financeDetailService;
	private RateChangeService			rateChangeService;
	private AddRepaymentService			addRepaymentService;
	private RecalculateService			recalService;
	private ChangeProfitService			changeProfitService;
	private AddDisbursementService		addDisbursementService;
	private ChangeFrequencyService		changeFrequencyService;
	private ReScheduleService			reScheduleService;
	private PostponementService			postponementService;
	private FinanceScheduleDetailDAO	financeScheduleDetailDAO;
	private AddTermsService				addTermsService;
	private RemoveTermsService			rmvTermsService;
	private FinanceMainService			financeMainService;
	private FinODPenaltyRateDAO			finODPenaltyRateDAO;
	private FinanceProfitDetailDAO		profitDetailsDAO;
	private FeeDetailService 			feeDetailService;
	private BankBranchService 			bankBranchService;
	private FinAdvancePaymentsService	finAdvancePaymentsService;
	private ReceiptService				receiptService;
	private RepaymentProcessUtil		repayProcessUtil;
	private FinTypePartnerBankService 	finTypePartnerBankService;


	/**
	 * Method for process AddRateChange request and re-calculate schedule details
	 * 
	 * @param finServiceInstruction
	 * @param eventCode 
	 * @return
	 * @throws PFFInterfaceException
	 * @throws JaxenException
	 */
	public FinanceDetail doAddRateChange(FinServiceInstruction finServiceInst, String eventCode) {
		logger.debug("Enteing");

		// fetch finance data
		FinanceDetail financeDetail = getFinanceDetails(finServiceInst, eventCode);

		if (financeDetail != null) {
			FinScheduleData finScheduleData = financeDetail.getFinScheduleData();
			FinanceMain financeMain = finScheduleData.getFinanceMain();

			financeMain.setEventFromDate(finServiceInst.getFromDate());
			financeMain.setEventToDate(finServiceInst.getToDate());
			financeMain.setRecalFromDate(finServiceInst.getRecalFromDate());
			financeMain.setRecalType(finServiceInst.getRecalType());
			financeMain.setRecalSchdMethod(financeMain.getScheduleMethod());
			financeMain.setFinSourceID(APIConstants.FINSOURCE_ID_API);

			if (CalculationConstants.RPYCHG_TILLMDT.equals(finServiceInst.getRecalType())) {
				financeMain.setRecalToDate(financeMain.getMaturityDate());
			} else if (CalculationConstants.RPYCHG_TILLDATE.equals(finServiceInst.getRecalType())) {
				financeMain.setRecalToDate(finServiceInst.getRecalToDate());
			}

			try {
				// execute fee charges
				finScheduleData.setFinFeeDetailList(finServiceInst.getFinFeeDetails());
				feeDetailService.doExecuteFeeCharges(financeDetail, eventCode);
				if (financeDetail.getFinScheduleData().getErrorDetails() != null) {
					for (ErrorDetails errorDetail : financeDetail.getFinScheduleData().getErrorDetails()) {
						FinanceDetail response = new FinanceDetail();
						doEmptyResponseObject(response);
						response.setReturnStatus(APIErrorHandlerService.getFailedStatus(errorDetail.getErrorCode(),
								errorDetail.getError()));
						return response;
					}
				}
				
				// call schedule calculator for Rate change
				finScheduleData = rateChangeService.getRateChangeDetails(finScheduleData, finServiceInst);

				if (finScheduleData.getErrorDetails() != null) {
					for (ErrorDetails errorDetail : finScheduleData.getErrorDetails()) {
						FinanceDetail response = new FinanceDetail();
						response.setReturnStatus(APIErrorHandlerService.getFailedStatus(errorDetail.getErrorCode(),
								errorDetail.getError()));
						return response;
					}
				}
				financeDetail.setFinScheduleData(finScheduleData);
				// Get the response
				financeDetail = getResponse(financeDetail, finServiceInst);

			} catch (Exception e) {
				logger.error("Exception", e);
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
	 * Method for process add repayment request and calculate schedule
	 * @param eventCode 
	 * 
	 * @param finServiceInstruction
	 * @return
	 * @throws PFFInterfaceException
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
			//financeMain.setScheduleMethod(finServiceInst.getSchdMethod());
			financeMain.setRecalSchdMethod(finServiceInst.getSchdMethod());

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
				financeMain.setRecalFromDate(finServiceInst.getFromDate());
				financeMain.setRecalToDate(financeMain.getMaturityDate());
				//financeMain.setScheduleRegenerated(true);
			}

			financeMain.setFinSourceID(APIConstants.FINSOURCE_ID_API);

			try {
				// execute fee charges
				finScheduleData.setFinFeeDetailList(finServiceInst.getFinFeeDetails());
				feeDetailService.doExecuteFeeCharges(financeDetail, eventCode);
				if (financeDetail.getFinScheduleData().getErrorDetails() != null) {
					for (ErrorDetails errorDetail : financeDetail.getFinScheduleData().getErrorDetails()) {
						FinanceDetail response = new FinanceDetail();
						doEmptyResponseObject(response);
						response.setReturnStatus(APIErrorHandlerService.getFailedStatus(errorDetail.getErrorCode(),
								errorDetail.getError()));
						return response;
					}
				}
				// Call Schedule calculator for Rate change
				finScheduleData = addRepaymentService.getAddRepaymentDetails(finScheduleData, finServiceInst);

				if (finScheduleData.getErrorDetails() != null) {
					for (ErrorDetails errorDetail : finScheduleData.getErrorDetails()) {
						FinanceDetail response = new FinanceDetail();
						response.setReturnStatus(APIErrorHandlerService.getFailedStatus(errorDetail.getErrorCode(),
								errorDetail.getError()));
						return response;
					}
				}
				financeDetail.setFinScheduleData(finScheduleData);
				// Get the response
				financeDetail = getResponse(financeDetail, finServiceInst);

			} catch (Exception e) {
				logger.error("Exception", e);
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
		FinanceDetail financeDetail = getFinanceDetails(finServiceInst, AccountEventConstants.ACCEVENT_DEFRPY);

		if (financeDetail != null) {
			FinScheduleData finScheduleData = financeDetail.getFinScheduleData();
			FinanceMain financeMain = finScheduleData.getFinanceMain();

			financeMain.setEventFromDate(finServiceInst.getFromDate());
			financeMain.setEventToDate(finServiceInst.getToDate());
			financeMain.setFinSourceID(APIConstants.FINSOURCE_ID_API);

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
				//financeMain.setScheduleRegenerated(true);
			}

			finScheduleData.setFinServiceInstruction(finServiceInst);
			try {
				// execute fee charges
				finScheduleData.setFinFeeDetailList(finServiceInst.getFinFeeDetails());
				feeDetailService.doExecuteFeeCharges(financeDetail, eventCode);
				if (financeDetail.getFinScheduleData().getErrorDetails() != null) {
					for (ErrorDetails errorDetail : financeDetail.getFinScheduleData().getErrorDetails()) {
						FinanceDetail response = new FinanceDetail();
						doEmptyResponseObject(response);
						response.setReturnStatus(APIErrorHandlerService.getFailedStatus(errorDetail.getErrorCode(),
								errorDetail.getError()));
						return response;
					}
				}
				
				// call deferment service
				finScheduleData = postponementService.doUnPlannedEMIH(finScheduleData);
				if (finScheduleData.getErrorDetails() != null) {
					for (ErrorDetails errorDetail : finScheduleData.getErrorDetails()) {
						FinanceDetail response = new FinanceDetail();
						response.setReturnStatus(APIErrorHandlerService.getFailedStatus(errorDetail.getErrorCode(),
								errorDetail.getError()));
						return response;
					}
				}
				financeDetail.setFinScheduleData(finScheduleData);
				// Get the response
				financeDetail = getResponse(financeDetail, finServiceInst);

			} catch (Exception e) {
				logger.error("Exception", e);
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
		FinanceDetail financeDetail = getFinanceDetails(finServiceInst, FinanceConstants.FINSER_EVENT_ADDTERM);

		if (financeDetail != null) {
			FinScheduleData finScheduleData = financeDetail.getFinScheduleData();
			FinanceMain financeMain = finScheduleData.getFinanceMain();
			financeMain.setFinSourceID(APIConstants.FINSOURCE_ID_API);

			try {
				// execute fee charges
				if(finServiceInst.getFinFeeDetails() != null && !finServiceInst.getFinFeeDetails().isEmpty()) {
					finScheduleData.setFinFeeDetailList(finServiceInst.getFinFeeDetails());
					feeDetailService.doExecuteFeeCharges(financeDetail, eventCode);
					if (financeDetail.getFinScheduleData().getErrorDetails() != null) {
						for (ErrorDetails errorDetail : financeDetail.getFinScheduleData().getErrorDetails()) {
							FinanceDetail response = new FinanceDetail();
							doEmptyResponseObject(response);
							response.setReturnStatus(APIErrorHandlerService.getFailedStatus(errorDetail.getErrorCode(),
									errorDetail.getError()));
							return response;
						}
					}
				}
				
				// Call Schedule calculator for Rate change
				finScheduleData = addTermsService.getAddTermsDetails(finScheduleData, finServiceInst);

				if (finScheduleData.getErrorDetails() != null) {
					for (ErrorDetails errorDetail : finScheduleData.getErrorDetails()) {
						FinanceDetail response = new FinanceDetail();
						doEmptyResponseObject(response);
						response.setReturnStatus(APIErrorHandlerService.getFailedStatus(errorDetail.getErrorCode(),
								errorDetail.getError()));
						return response;
					}
				}
				financeDetail.setFinScheduleData(finScheduleData);
				// Get the response
				financeDetail = getResponse(financeDetail, finServiceInst);

			} catch (Exception e) {
				logger.error("Exception", e);
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
		FinanceDetail financeDetail = getFinanceDetails(finServiceInst, AccountEventConstants.ACCEVENT_SCDCHG);

		if (financeDetail != null) {
			FinScheduleData finScheduleData = financeDetail.getFinScheduleData();
			FinanceMain financeMain = finScheduleData.getFinanceMain();

			financeMain.setAdjTerms(finServiceInst.getTerms());
			financeMain.setRecalType(finServiceInst.getRecalType());

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

			financeMain.setFinSourceID(APIConstants.FINSOURCE_ID_API);

			try {
				// execute fee charges
				if(finServiceInst.getFinFeeDetails() != null && !finServiceInst.getFinFeeDetails().isEmpty()) {
					finScheduleData.setFinFeeDetailList(finServiceInst.getFinFeeDetails());
					feeDetailService.doExecuteFeeCharges(financeDetail, eventCode);
					if (financeDetail.getFinScheduleData().getErrorDetails() != null) {
						for (ErrorDetails errorDetail : financeDetail.getFinScheduleData().getErrorDetails()) {
							FinanceDetail response = new FinanceDetail();
							doEmptyResponseObject(response);
							response.setReturnStatus(APIErrorHandlerService.getFailedStatus(errorDetail.getErrorCode(),
									errorDetail.getError()));
							return response;
						}
					}
				}
				// Call Schedule calculator for Rate change
				finScheduleData = recalService.getRecalculateSchdDetails(finScheduleData);

				if (finScheduleData.getErrorDetails() != null) {
					for (ErrorDetails errorDetail : finScheduleData.getErrorDetails()) {
						FinanceDetail response = new FinanceDetail();
						response.setReturnStatus(APIErrorHandlerService.getFailedStatus(errorDetail.getErrorCode(),
								errorDetail.getError()));
						return response;
					}
				}
				financeDetail.setFinScheduleData(finScheduleData);
				// Get the response
				financeDetail = getResponse(financeDetail, finServiceInst);

			} catch (Exception e) {
				logger.error("Exception", e);
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
		FinanceDetail financeDetail = getFinanceDetails(finServiceInst, AccountEventConstants.ACCEVENT_SCDCHG);

		if (financeDetail != null) {
			FinScheduleData finScheduleData = financeDetail.getFinScheduleData();
			FinanceMain financeMain = finScheduleData.getFinanceMain();

			financeMain.setEventFromDate(finServiceInst.getFromDate());
			financeMain.setEventToDate(finServiceInst.getToDate());
			financeMain.setPftIntact(true);

			financeMain.setFinSourceID(APIConstants.FINSOURCE_ID_API);

			// profit amount
			BigDecimal amount = finServiceInst.getAmount();
			try {
				// execute fee charges
				if(finServiceInst.getFinFeeDetails() != null && !finServiceInst.getFinFeeDetails().isEmpty()) {
					finScheduleData.setFinFeeDetailList(finServiceInst.getFinFeeDetails());
					feeDetailService.doExecuteFeeCharges(financeDetail, eventCode);
					if (financeDetail.getFinScheduleData().getErrorDetails() != null) {
						for (ErrorDetails errorDetail : financeDetail.getFinScheduleData().getErrorDetails()) {
							FinanceDetail response = new FinanceDetail();
							doEmptyResponseObject(response);
							response.setReturnStatus(APIErrorHandlerService.getFailedStatus(errorDetail.getErrorCode(),
									errorDetail.getError()));
							return response;
						}
					}
				}
				// Call Schedule calculator for Rate change
				finScheduleData = changeProfitService.getChangeProfitDetails(finScheduleData, amount);

				if (finScheduleData.getErrorDetails() != null) {
					for (ErrorDetails errorDetail : finScheduleData.getErrorDetails()) {
						FinanceDetail response = new FinanceDetail();
						response.setReturnStatus(APIErrorHandlerService.getFailedStatus(errorDetail.getErrorCode(),
								errorDetail.getError()));
						return response;
					}
				}
				financeDetail.setFinScheduleData(finScheduleData);
				// Get the response
				financeDetail = getResponse(financeDetail, finServiceInst);

			} catch (Exception e) {
				logger.error("Exception", e);
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
	public FinanceDetail doAddDisbursement(FinServiceInstruction finServiceInst, FinanceDetail financeDetail, String eventCode) {
		logger.debug("Enteing");

		if (financeDetail != null) {
			FinScheduleData finScheduleData = financeDetail.getFinScheduleData();
			FinanceMain financeMain = finScheduleData.getFinanceMain();

			financeMain.setEventFromDate(finServiceInst.getFromDate());
			financeMain.setEventToDate(financeMain.getMaturityDate());
			financeMain.setAdjTerms(finServiceInst.getTerms());
			financeMain.setRecalSchdMethod(financeMain.getScheduleMethod());
			financeMain.setRecalType(finServiceInst.getRecalType());

			if (StringUtils.equals(FinanceConstants.PRODUCT_ODFACILITY, financeMain.getProductCategory())) {
				financeMain.setRecalType(CalculationConstants.RPYCHG_TILLMDT);
				financeMain.setEventFromDate(finServiceInst.getFromDate());
				financeMain.setRecalFromDate(finServiceInst.getFromDate());
				financeMain.setRecalToDate(financeMain.getMaturityDate());
			}

			financeMain.setFinSourceID(APIConstants.FINSOURCE_ID_API);

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
			try {
				// execute fee charges
				if(finServiceInst.getFinFeeDetails() != null && !finServiceInst.getFinFeeDetails().isEmpty()) {
					finScheduleData.setFinFeeDetailList(finServiceInst.getFinFeeDetails());
					feeDetailService.doExecuteFeeCharges(financeDetail, eventCode);
					if (financeDetail.getFinScheduleData().getErrorDetails() != null) {
						for (ErrorDetails errorDetail : financeDetail.getFinScheduleData().getErrorDetails()) {
							FinanceDetail response = new FinanceDetail();
							doEmptyResponseObject(response);
							response.setReturnStatus(APIErrorHandlerService.getFailedStatus(errorDetail.getErrorCode(),
									errorDetail.getError()));
							return response;
						}
					}
				}
				// added new disbursement details
				int seqNo = finScheduleData.getDisbursementDetails().size() + 1;
				FinanceDisbursement disbursementDetails = new FinanceDisbursement();
				disbursementDetails.setDisbDate(finServiceInst.getFromDate());
				disbursementDetails.setDisbAmount(finServiceInst.getAmount());
				disbursementDetails.setDisbSeq(seqNo);
				disbursementDetails.setDisbReqDate(DateUtility.getAppDate());
				disbursementDetails.setFeeChargeAmt(financeMain.getFeeChargeAmt());
				disbursementDetails.setInsuranceAmt(financeMain.getInsuranceAmt());
				disbursementDetails.setDisbAccountId(PennantApplicationUtil.unFormatAccountNumber(financeMain.getDisbAccountId()));
				List<FinanceDisbursement> list = new ArrayList<FinanceDisbursement>();
				list.add(disbursementDetails);
				//finScheduleData.getDisbursementDetails().add(disbursementDetails);
				
				for(FinAdvancePayments advPayments: financeDetail.getAdvancePaymentsList()) {
					if(advPayments.getDisbSeq() == 0) {
						advPayments.setDisbSeq(seqNo);
					} 
				}
				
				// validate disbursement instructions
				List<ErrorDetails> errors = finAdvancePaymentsService.validateFinAdvPayments(financeDetail.getAdvancePaymentsList(), list,
						finScheduleData.getFinanceMain(), true);
				for (ErrorDetails erroDetails : errors) {
					finScheduleData.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetails(erroDetails.getErrorCode(),
							erroDetails.getErrorParameters())));
				}

				// Call Schedule calculator for add disbursement
				if(finScheduleData.getErrorDetails() == null || finScheduleData.getErrorDetails().isEmpty()) {
					finScheduleData = addDisbursementService.getAddDisbDetails(finScheduleData, amount, BigDecimal.ZERO, false);
				}

				if (finScheduleData.getErrorDetails() != null) {
					for (ErrorDetails errorDetail : finScheduleData.getErrorDetails()) {
						FinanceDetail response = new FinanceDetail();
						doEmptyResponseObject(response);
						response.setReturnStatus(APIErrorHandlerService.getFailedStatus(errorDetail.getErrorCode(),
								errorDetail.getError()));
						return response;
					}
				}
				financeDetail.setFinScheduleData(finScheduleData);
				
				// process disbursement details
				LoggedInUser userDetails = SessionUserDetails.getUserDetails(SessionUserDetails.getLogiedInUser());
				//financeDetail.getFinScheduleData().getFinanceMain().setRecordType(PennantConstants.RECORD_TYPE_NEW);
				List<FinAdvancePayments> advancePayments = financeDetail.getAdvancePaymentsList();
				if (advancePayments != null) {
					for (FinAdvancePayments advPayment : advancePayments) {
						int paymentSeq = finAdvancePaymentsService.getCountByFinReference(financeMain.getFinReference());
						advPayment.setFinReference(financeMain.getFinReference());
						advPayment.setRecordType(PennantConstants.RECORD_TYPE_NEW);
						advPayment.setNewRecord(true);
						advPayment.setLastMntBy(userDetails.getLoginUsrID());
						advPayment.setLastMntOn(new Timestamp(System.currentTimeMillis()));
						advPayment.setRecordStatus(PennantConstants.RCD_STATUS_APPROVED);
						advPayment.setUserDetails(financeMain.getUserDetails());
						advPayment.setPaymentSeq(paymentSeq+1);
						advPayment.setLLDate(null);
						

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
				financeDetail.setFinScheduleData(finScheduleData);
				// Get the response
				financeDetail = getResponse(financeDetail, finServiceInst);

			} catch (Exception e) {
				logger.error("Exception", e);
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
		FinanceDetail financeDetail = getFinanceDetails(finServiceInst, AccountEventConstants.ACCEVENT_SCDCHG);

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
			Date fromDate = finServiceInst.getFromDate();

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
							if (curSchd.getSchDate().compareTo(finScheduleData.getFinanceMain().getGrcPeriodEndDate()) > 0) {
								rpyTermsCompleted = rpyTermsCompleted + 1;
							}
						}
					}
				}
				adjRepayTerms = totRepayTerms - rpyTermsCompleted;
			}

			finServiceInst.setAdjRpyTerms(adjRepayTerms);
			finScheduleData.getFinanceMain().setFinSourceID(APIConstants.FINSOURCE_ID_API);
			try {
				// execute fee charges
				if(finServiceInst.getFinFeeDetails() != null && !finServiceInst.getFinFeeDetails().isEmpty()) {
					finScheduleData.setFinFeeDetailList(finServiceInst.getFinFeeDetails());
					feeDetailService.doExecuteFeeCharges(financeDetail, eventCode);
					if (financeDetail.getFinScheduleData().getErrorDetails() != null) {
						for (ErrorDetails errorDetail : financeDetail.getFinScheduleData().getErrorDetails()) {
							FinanceDetail response = new FinanceDetail();
							doEmptyResponseObject(response);
							response.setReturnStatus(APIErrorHandlerService.getFailedStatus(errorDetail.getErrorCode(),
									errorDetail.getError()));
							return response;
						}
					}
				}
				// call change frequency service
				finScheduleData = changeFrequencyService.doChangeFrequency(finScheduleData, finServiceInst);
				financeDetail.setFinScheduleData(finScheduleData);

				// Get the response
				financeDetail = getResponse(financeDetail, finServiceInst);

			} catch (Exception e) {
				logger.error("Exception", e);
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
		FinanceDetail financeDetail = getFinanceDetails(finServiceInst, FinanceConstants.FINSER_EVENT_RMVTERM);
		if (financeDetail != null) {
			FinScheduleData finScheduleData = financeDetail.getFinScheduleData();

			FinanceMain financeMain = finScheduleData.getFinanceMain();
			financeMain.setEventFromDate(finServiceInst.getFromDate());
			financeMain.setRecalFromDate(finServiceInst.getRecalFromDate());
			financeMain.setRecalType(finServiceInst.getRecalType());
			financeMain.setFinSourceID(APIConstants.FINSOURCE_ID_API);
			financeMain.setRecalSchdMethod(financeMain.getScheduleMethod());

			if (StringUtils.equals(finServiceInst.getRecalType(), CalculationConstants.RPYCHG_TILLMDT)) {
				financeMain.setRecalFromDate(finServiceInst.getRecalFromDate());
			} else if (StringUtils.equals(finServiceInst.getRecalType(), CalculationConstants.RPYCHG_ADJMDT)) {
				financeMain.setRecalFromDate(finServiceInst.getFromDate());
			}

			try {
				// execute fee charges
				if(finServiceInst.getFinFeeDetails() != null && !finServiceInst.getFinFeeDetails().isEmpty()) {
					finScheduleData.setFinFeeDetailList(finServiceInst.getFinFeeDetails());
					feeDetailService.doExecuteFeeCharges(financeDetail, eventCode);
					if (financeDetail.getFinScheduleData().getErrorDetails() != null) {
						for (ErrorDetails errorDetail : financeDetail.getFinScheduleData().getErrorDetails()) {
							FinanceDetail response = new FinanceDetail();
							doEmptyResponseObject(response);
							response.setReturnStatus(APIErrorHandlerService.getFailedStatus(errorDetail.getErrorCode(),
									errorDetail.getError()));
							return response;
						}
					}
				}
				// Call Schedule calculator for Rate change
				finScheduleData = rmvTermsService.getRmvTermsDetails(finScheduleData);
				if (finScheduleData.getErrorDetails() != null) {
					for (ErrorDetails errorDetail : finScheduleData.getErrorDetails()) {
						FinanceDetail response = new FinanceDetail();
						response.setReturnStatus(APIErrorHandlerService.getFailedStatus(errorDetail.getErrorCode(),
								errorDetail.getError()));

						return response;
					}
				}
				financeDetail.setFinScheduleData(finScheduleData);
				// Get the response
				financeDetail = getResponse(financeDetail, finServiceInst);
				
			} catch (Exception e) {
				logger.error("Exception", e);
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
		FinanceDetail financeDetail = getFinanceDetails(finServiceInst, AccountEventConstants.ACCEVENT_SCDCHG);
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

			try {
				// execute fee charges
				if(finServiceInst.getFinFeeDetails() != null && !finServiceInst.getFinFeeDetails().isEmpty()) {
					finScheduleData.setFinFeeDetailList(finServiceInst.getFinFeeDetails());
					feeDetailService.doExecuteFeeCharges(financeDetail, eventCode);
					if (financeDetail.getFinScheduleData().getErrorDetails() != null) {
						for (ErrorDetails errorDetail : financeDetail.getFinScheduleData().getErrorDetails()) {
							FinanceDetail response = new FinanceDetail();
							doEmptyResponseObject(response);
							response.setReturnStatus(APIErrorHandlerService.getFailedStatus(errorDetail.getErrorCode(),
									errorDetail.getError()));
							return response;
						}
					}
				}
				// Call Schedule calculator for Rate change
				finScheduleData = reScheduleService.doReSchedule(finScheduleData, finServiceInst);
				if (finScheduleData.getErrorDetails() != null) {
					for (ErrorDetails errorDetail : finScheduleData.getErrorDetails()) {
						FinanceDetail response = new FinanceDetail();
						response.setReturnStatus(APIErrorHandlerService.getFailedStatus(errorDetail.getErrorCode(),
								errorDetail.getError()));
						return response;
					}
				}
				financeDetail.setFinScheduleData(finScheduleData);
				// Get the response
				financeDetail = getResponse(financeDetail, finServiceInst);
				
			} catch (Exception e) {
				logger.error("Exception", e);
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
		FinanceDetail financeDetail = getFinanceDetails(finServiceInst, AccountEventConstants.ACCEVENT_EARLYSTL);
		FinScheduleData finScheduleData = financeDetail.getFinScheduleData();

		//Fetch Total Repayment Amount till Maturity date for Early Settlement
		BigDecimal repayAmt = financeScheduleDetailDAO.getTotalRepayAmount(finServiceInst.getFinReference());
		if(finServiceInst.getAmount().compareTo(repayAmt) < 0) {
			FinanceDetail response = new FinanceDetail();
			doEmptyResponseObject(response);
			String[] valueParm = new String[2];
			valueParm[0] = "Amount:"+finServiceInst.getAmount();
			valueParm[1] = "Total outstandin amount:"+repayAmt;
			response.setReturnStatus(APIErrorHandlerService.getFailedStatus("90205", valueParm));
			return response;
		}
		finServiceInst.setModuleDefiner(FinanceConstants.FINSER_EVENT_EARLYSETTLE);
		finServiceInst.setAmount(repayAmt);

		if (StringUtils.equals(finServiceInst.getReqType(), APIConstants.REQTYPE_INQUIRY)) {
			finServiceInst.setModuleDefiner(FinanceConstants.FINSER_EVENT_EARLYSTLENQ);
			if (finServiceInst.getToDate() == null) {
				finServiceInst.setToDate(finScheduleData.getFinanceMain().getMaturityDate());
			}
		}

		FinanceDetail response = null;
		try {
			// execute fee charges
			if(finServiceInst.getFinFeeDetails() != null && !finServiceInst.getFinFeeDetails().isEmpty()) {
				finScheduleData.setFinFeeDetailList(finServiceInst.getFinFeeDetails());
				feeDetailService.doExecuteFeeCharges(financeDetail, eventCode);
				if (financeDetail.getFinScheduleData().getErrorDetails() != null) {
					for (ErrorDetails errorDetail : financeDetail.getFinScheduleData().getErrorDetails()) {
						response = new FinanceDetail();
						doEmptyResponseObject(response);
						response.setReturnStatus(APIErrorHandlerService.getFailedStatus(errorDetail.getErrorCode(),
								errorDetail.getError()));
						return response;
					}
				}
			}
			response = doProcessReceipt(financeDetail, finServiceInst, FinanceConstants.FINSER_EVENT_EARLYSETTLE);
		} catch (Exception e) {
			logger.error("Exception", e);
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
		FinanceDetail financeDetail = getFinanceDetails(finServiceInst, AccountEventConstants.ACCEVENT_EARLYPAY);
		finServiceInst.setModuleDefiner(FinanceConstants.FINSER_EVENT_EARLYRPY);

		FinanceDetail response = null;
		try {
			// execute fee charges
			if(finServiceInst.getFinFeeDetails() != null && !finServiceInst.getFinFeeDetails().isEmpty()) {
				financeDetail.getFinScheduleData().setFinFeeDetailList(finServiceInst.getFinFeeDetails());
				feeDetailService.doExecuteFeeCharges(financeDetail, eventCode);
				if (financeDetail.getFinScheduleData().getErrorDetails() != null) {
					for (ErrorDetails errorDetail : financeDetail.getFinScheduleData().getErrorDetails()) {
						response = new FinanceDetail();
						doEmptyResponseObject(response);
						response.setReturnStatus(APIErrorHandlerService.getFailedStatus(errorDetail.getErrorCode(),
								errorDetail.getError()));
						return response;
					}
				}
			}
			response = doProcessReceipt(financeDetail, finServiceInst, FinanceConstants.FINSER_EVENT_EARLYRPY);
		} catch (Exception e) {
			logger.error("Exception", e);
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
		FinanceDetail financeDetail = getFinanceDetails(finServiceInst, AccountEventConstants.ACCEVENT_REPAY);
		finServiceInst.setModuleDefiner(FinanceConstants.FINSER_EVENT_SCHDRPY);

		FinanceDetail response = null;
		try {
			// execute fee charges
			if(finServiceInst.getFinFeeDetails() != null && !finServiceInst.getFinFeeDetails().isEmpty()) {
				financeDetail.getFinScheduleData().setFinFeeDetailList(finServiceInst.getFinFeeDetails());
				feeDetailService.doExecuteFeeCharges(financeDetail, eventCode);
				if (financeDetail.getFinScheduleData().getErrorDetails() != null) {
					for (ErrorDetails errorDetail : financeDetail.getFinScheduleData().getErrorDetails()) {
						response = new FinanceDetail();
						doEmptyResponseObject(response);
						response.setReturnStatus(APIErrorHandlerService.getFailedStatus(errorDetail.getErrorCode(),
								errorDetail.getError()));
						return response;
					}
				}
			}
			response = doProcessReceipt(financeDetail, finServiceInst, FinanceConstants.FINSER_EVENT_SCHDRPY);
			
		} catch (Exception e) {
			logger.error("Exception", e);
			response = new FinanceDetail();
			doEmptyResponseObject(response);
			response.setReturnStatus(APIErrorHandlerService.getFailedStatus());
			return response;
		}

		logger.debug("Leaving");
		return response;
	}

	private FinanceDetail doProcessReceipt(FinanceDetail aFinanceDetail, FinServiceInstruction finServiceInst,
			String purpose) throws IllegalAccessException, InvocationTargetException, PFFInterfaceException {
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

		FinReceiptData finReceiptData = new FinReceiptData();
		FinReceiptHeader finReceiptHeader = new FinReceiptHeader();

		FinReceiptDetail finReceiptDetail = finServiceInst.getReceiptDetail();
		if (finReceiptDetail == null) {
			finReceiptDetail = new FinReceiptDetail();
			finReceiptDetail.setReceivedDate(DateUtility.getAppDate());
		}
		finReceiptDetail.setReceiptType(RepayConstants.RECEIPTTYPE_RECIPT);
		finReceiptDetail.setPaymentTo(RepayConstants.RECEIPTTO_FINANCE);
		finReceiptDetail.setPaymentType("");
		finReceiptDetail.setAmount(finServiceInst.getAmount());

		List<FinReceiptDetail> receiptDetails = new ArrayList<FinReceiptDetail>();
		receiptDetails.add(finReceiptDetail);
		finReceiptHeader.setReceiptDetails(receiptDetails);

		finReceiptHeader.setReceiptAmount(finServiceInst.getAmount());
		finReceiptHeader.setReceiptMode(finServiceInst.getPaymentMode());

		finReceiptHeader.setReference(finServiceInst.getFinReference());
		finReceiptHeader.setReceiptType(RepayConstants.RECEIPTTYPE_RECIPT);
		finReceiptHeader.setRecAgainst(RepayConstants.RECEIPTTO_FINANCE);
		finReceiptHeader.setReceiptDate(DateUtility.getAppDate());
		finReceiptHeader.setReceiptPurpose(purpose);
		if (StringUtils.isBlank(finReceiptHeader.getExcessAdjustTo())) {
			finReceiptHeader.setExcessAdjustTo(RepayConstants.EXCESSADJUSTTO_EXCESS);
		}
		finReceiptHeader.setAllocationType(RepayConstants.ALLOCATIONTYPE_AUTO);

		finReceiptHeader.setRecordType(PennantConstants.RECORD_TYPE_NEW);
		finReceiptHeader.setNewRecord(true);
		finReceiptHeader.setLastMntBy(userDetails.getLoginUsrID());
		finReceiptHeader.setLastMntOn(new Timestamp(System.currentTimeMillis()));
		finReceiptHeader.setRecordStatus(PennantConstants.RCD_STATUS_APPROVED);
		finReceiptHeader.setUserDetails(userDetails);

		finReceiptData.setReceiptHeader(finReceiptHeader);
		finReceiptData.setFinanceDetail(financeDetail);
		finReceiptData.setFinReference(finServiceInst.getFinReference());
		finReceiptData.setSourceId(PennantConstants.FINSOURCE_ID_API);

		FinanceMain financeMain = financeDetail.getFinScheduleData().getFinanceMain();
		Customer customer = financeDetail.getCustomerDetails().getCustomer();
		List<FinanceScheduleDetail> scheduleDetails = financeDetail.getFinScheduleData().getFinanceScheduleDetails();
		FinanceProfitDetail profitDetail = new FinanceProfitDetail();
		profitDetail.setFinStartDate(financeMain.getFinStartDate());
		profitDetail.setFinReference(financeMain.getFinReference());
		Date receiDate = DateUtility.getDBDate(DateUtility.formatDate(finReceiptDetail.getReceivedDate(),
				PennantConstants.DBDateFormat));

		if (StringUtils.equals(purpose, FinanceConstants.FINSER_EVENT_EARLYRPY)) {
			finReceiptData = receiptService.setEarlyRepayEffectOnSchedule(finReceiptData, finServiceInst);
		} else {
			finReceiptData = receiptService.calculateRepayments(finReceiptData);
			finReceiptHeader = repayProcessUtil.processReceiptPayments(financeMain, customer, scheduleDetails,
					profitDetail, finReceiptHeader, rpyHierarchy, receiDate);
		}

		List<ErrorDetails> errorDetails = finReceiptData.getFinanceDetail().getFinScheduleData().getErrorDetails();
		if(!errorDetails.isEmpty()) {
			for(ErrorDetails error:errorDetails) {
				FinanceDetail response = new FinanceDetail();
				doEmptyResponseObject(response);
				response.setReturnStatus(APIErrorHandlerService.getFailedStatus(error.getErrorCode()));
				return response;
			}
		}
		if (StringUtils.equals(finServiceInst.getReqType(), APIConstants.REQTYPE_POST)) {
			int count = finTypePartnerBankService.getPartnerBankCount(financeMain.getFinType(),
					finServiceInst.getPaymentMode(), finReceiptDetail.getFundingAc());
			if (count <= 0) {
				FinanceDetail response = new FinanceDetail();
				doEmptyResponseObject(response);
				response.setReturnStatus(APIErrorHandlerService.getFailedStatus("90263"));
				return response;
			}

			if (finReceiptDetail.getReceivedDate().compareTo(financeMain.getFinStartDate()) < 0
					|| finReceiptDetail.getReceivedDate().compareTo(DateUtility.getAppDate()) > 0) {
				FinanceDetail response = new FinanceDetail();
				doEmptyResponseObject(response);
				String[] valueParm = new String[3];
				valueParm[0] = "Received Date " + DateUtility.formatToShortDate(finReceiptDetail.getReceivedDate());
				valueParm[1] = DateUtility.formatToShortDate(financeMain.getFinStartDate());
				valueParm[2] = DateUtility.formatToShortDate(DateUtility.getAppDate());
				response.setReturnStatus(APIErrorHandlerService.getFailedStatus("90282", valueParm));
				return response;
			}

			// Set Version value
			int version = financeDetail.getFinScheduleData().getFinanceMain().getVersion();
			financeDetail.getFinScheduleData().getFinanceMain().setVersion(version + 1);
			financeDetail.getFinScheduleData().getFinanceMain().setRecordType("");
			finScheduleData.setSchduleGenerated(true);

			// Save the Schedule details
			AuditHeader auditHeader = getAuditHeader(finReceiptData, PennantConstants.TRAN_WF);

			// Get the header details from the request
			APIHeader reqHeaderDetails = (APIHeader) PhaseInterceptorChain.getCurrentMessage().getExchange()
					.get(APIHeader.API_HEADER_KEY);
			//set the headerDetails to AuditHeader
			auditHeader.setApiHeader(reqHeaderDetails);

			auditHeader = receiptService.doApprove(auditHeader);

			if (auditHeader.getErrorMessage() != null) {
				for (ErrorDetails errorDetail : auditHeader.getErrorMessage()) {
					FinanceDetail response = new FinanceDetail();
					doEmptyResponseObject(response);
					response.setReturnStatus(APIErrorHandlerService.getFailedStatus(errorDetail.getErrorCode(),
							errorDetail.getError()));
					return response;
				}
			}
			finReceiptData = (FinReceiptData) auditHeader.getAuditDetail().getModelData();
			financeDetail = getServiceInstResponse(finReceiptData.getFinanceDetail().getFinScheduleData());
		} else {
			financeDetail = finReceiptData.getFinanceDetail();
			FinReceiptHeader receiptHeader = finReceiptData.getReceiptHeader();
			profitDetail = profitDetailsDAO.getFinProfitDetailsById(finReceiptData.getFinReference());
			List<FinanceScheduleDetail> schedDetails = financeDetail.getFinScheduleData().getFinanceScheduleDetails();
			receiptHeader.setInquiryReq(true);
			schedDetails = repayProcessUtil.doProcessReceipts(financeMain, schedDetails, profitDetail, receiptHeader,
					financeDetail.getFinScheduleData(), DateUtility.getAppDate());
			financeDetail = getServiceInstResponse(finReceiptData.getFinanceDetail().getFinScheduleData());
		}
		logger.debug("Leaving");
		return financeDetail;
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
		String finReference = null;
		try {
			// save the OdPenaltyDetais
			finReference = finODPenaltyRateDAO.save(finODPenaltyRate, "");
		} catch (Exception e) {
			logger.error("Exception:" + e);
			return APIErrorHandlerService.getFailedStatus();
		}

		logger.debug("Leaving");
		if (StringUtils.equals(finODPenaltyRate.getFinReference(), finReference)) {
			return APIErrorHandlerService.getSuccessStatus();
		} else {
			return APIErrorHandlerService.getFailedStatus();
		}

	}

	/**
	 * Method for prepare finance detail response object
	 * 
	 * @param finScheduleData
	 * @return
	 */
	private FinanceDetail getServiceInstResponse(FinScheduleData finScheduleData) {
		logger.debug("Entering");

		FinanceMain financeMain = finScheduleData.getFinanceMain();

		FinanceDetail response = new FinanceDetail();
		response.setFinReference(finScheduleData.getFinReference());
		response.setFinScheduleData(finScheduleData);

		// Finance Summary details i.e Basic Calculator details
		FinanceSummary summaryDetail = getFinanceSummary(finScheduleData);
		summaryDetail.setEffectiveRateOfReturn(financeMain.getEffectiveRateOfReturn());
		summaryDetail.setTotalGracePft(financeMain.getTotalGracePft());
		summaryDetail.setTotalGraceCpz(financeMain.getTotalGraceCpz());
		summaryDetail.setTotalGrossGrcPft(financeMain.getTotalGrossGrcPft());
		summaryDetail.setTotalCpz(financeMain.getTotalCpz());
		summaryDetail.setTotalProfit(financeMain.getTotalProfit());
		summaryDetail.setTotalRepayAmt(financeMain.getTotalRepayAmt());
		summaryDetail.setFeeChargeAmt(financeMain.getFeeChargeAmt());
		summaryDetail.setNumberOfTerms(financeMain.getNumberOfTerms());
		summaryDetail.setMaturityDate(financeMain.getMaturityDate());

		response.getFinScheduleData().setFinanceSummary(summaryDetail);
		response.setReturnStatus(APIErrorHandlerService.getSuccessStatus());

		finScheduleData.setFinanceMain(null);
		finScheduleData.setDisbursementDetails(null);
		finScheduleData.setFinReference(null);
		finScheduleData.setRepayInstructions(null);
		finScheduleData.setRateInstruction(null);
		finScheduleData.setStepPolicyDetails(null);
		finScheduleData.setInsuranceList(null);
		finScheduleData.setFinODDetails(null);

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
		return new AuditHeader(finReceiptData.getFinReference(), null, null, null, auditDetail, finReceiptData.getFinanceDetail()
				.getFinScheduleData().getFinanceMain().getUserDetails(), new HashMap<String, ArrayList<ErrorDetails>>());
	}

	/**
	 * Get Audit Header Details
	 */
	protected AuditHeader getAuditHeader(FinanceDetail afinanceDetail, String tranType) {
		AuditDetail auditDetail = new AuditDetail(tranType, 1, afinanceDetail.getBefImage(), afinanceDetail);
		return new AuditHeader(afinanceDetail.getFinScheduleData().getFinReference(), null, null, null, auditDetail,
				afinanceDetail.getUserDetails(), new HashMap<String, ArrayList<ErrorDetails>>());
	}

	/**
	 * 
	 * @param financeDetail
	 * @param finServiceInst
	 * @return
	 * @throws PFFInterfaceException
	 * @throws JaxenException
	 */
	private FinanceDetail getResponse(FinanceDetail financeDetail, FinServiceInstruction finServiceInst)
			throws JaxenException, PFFInterfaceException {
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

			AuditHeader auditHeader = getAuditHeader(financeDetail, PennantConstants.TRAN_WF);
			FinanceDetail aFinanceDetail = (FinanceDetail) auditHeader.getAuditDetail().getModelData();

			aFinanceDetail = prepareInstructionObject(aFinanceDetail);

			//get the header details from the request
			APIHeader reqHeaderDetails = (APIHeader) PhaseInterceptorChain.getCurrentMessage().getExchange()
					.get(APIHeader.API_HEADER_KEY);
			auditHeader.setApiHeader(reqHeaderDetails);

			auditHeader = financeDetailService.doApprove(auditHeader, finServiceInst.isWif());
			if (auditHeader.getErrorMessage() != null) {
				for (ErrorDetails errorDetail : auditHeader.getErrorMessage()) {
					FinanceDetail response = new FinanceDetail();
					doEmptyResponseObject(response);
					response.setReturnStatus(APIErrorHandlerService.getFailedStatus(errorDetail.getErrorCode(),
							errorDetail.getError()));
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
		finScheduleData.setFinFeeDetailList(new ArrayList<FinFeeDetail>());
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

		if (financeDetail != null) {
			financeDetail.setAccountingEventCode(eventCode);
			LoggedInUser userDetails = SessionUserDetails.getUserDetails(SessionUserDetails.getLogiedInUser());
			financeDetail.getFinScheduleData().getFinanceMain().setUserDetails(userDetails);
			financeDetail.setEtihadCreditBureauDetail(null);
		}

		logger.debug("Leaving");

		return financeDetail;
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

	public void setAddTermsService(AddTermsService addTermsService) {
		this.addTermsService = addTermsService;
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

	public void setFinanceScheduleDetailDAO(FinanceScheduleDetailDAO financeScheduleDetailDAO) {
		this.financeScheduleDetailDAO = financeScheduleDetailDAO;
	}

	public void setProfitDetailsDAO(FinanceProfitDetailDAO profitDetailsDAO) {
		this.profitDetailsDAO = profitDetailsDAO;
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

	public void setRepayProcessUtil(RepaymentProcessUtil repayProcessUtil) {
		this.repayProcessUtil = repayProcessUtil;
	}

	public void setFinTypePartnerBankService(FinTypePartnerBankService finTypePartnerBankService) {
		this.finTypePartnerBankService = finTypePartnerBankService;
	}
}
