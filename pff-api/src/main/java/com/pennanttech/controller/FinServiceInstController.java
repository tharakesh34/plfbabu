package com.pennanttech.controller;

import java.lang.reflect.InvocationTargetException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import javax.security.auth.login.AccountNotFoundException;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.cxf.phase.PhaseInterceptorChain;
import org.apache.log4j.Logger;
import org.jaxen.JaxenException;
import org.springframework.beans.factory.annotation.Autowired;

import com.pennant.app.constants.AccountConstants;
import com.pennant.app.constants.AccountEventConstants;
import com.pennant.app.constants.CalculationConstants;
import com.pennant.app.constants.HolidayHandlerTypes;
import com.pennant.app.constants.ImplementationConstants;
import com.pennant.app.util.APIHeader;
import com.pennant.app.util.CalculationUtil;
import com.pennant.app.util.CurrencyUtil;
import com.pennant.app.util.DateUtility;
import com.pennant.app.util.ErrorUtil;
import com.pennant.app.util.FeeScheduleCalculator;
import com.pennant.app.util.FrequencyUtil;
import com.pennant.app.util.GSTCalculator;
import com.pennant.app.util.PostingsPreparationUtil;
import com.pennant.app.util.ReceiptCalculator;
import com.pennant.app.util.RepayCalculator;
import com.pennant.app.util.ScheduleCalculator;
import com.pennant.app.util.SessionUserDetails;
import com.pennant.app.util.SysParamUtil;
import com.pennant.backend.dao.applicationmaster.BankDetailDAO;
import com.pennant.backend.dao.documentdetails.DocumentDetailsDAO;
import com.pennant.backend.dao.finance.FinAdvancePaymentsDAO;
import com.pennant.backend.dao.finance.FinODDetailsDAO;
import com.pennant.backend.dao.finance.FinODPenaltyRateDAO;
import com.pennant.backend.dao.finance.FinanceMainDAO;
import com.pennant.backend.dao.finance.FinanceProfitDetailDAO;
import com.pennant.backend.dao.finance.ManualAdviseDAO;
import com.pennant.backend.dao.finance.ReceiptResponseDetailDAO;
import com.pennant.backend.dao.finance.ReceiptUploadDetailDAO;
import com.pennant.backend.dao.finance.covenant.CovenantsDAO;
import com.pennant.backend.dao.partnerbank.PartnerBankDAO;
import com.pennant.backend.dao.receipts.FinReceiptDetailDAO;
import com.pennant.backend.dao.receipts.FinReceiptHeaderDAO;
import com.pennant.backend.dao.rmtmasters.FinanceTypeDAO;
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
import com.pennant.backend.model.WorkFlowDetails;
import com.pennant.backend.model.applicationmaster.BankDetail;
import com.pennant.backend.model.applicationmaster.LoanPendingData;
import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.model.bmtmasters.BankBranch;
import com.pennant.backend.model.collateral.CollateralAssignment;
import com.pennant.backend.model.configuration.VASRecording;
import com.pennant.backend.model.documentdetails.DocumentDetails;
import com.pennant.backend.model.extendedfield.ExtendedFieldRender;
import com.pennant.backend.model.finance.FinAdvancePayments;
import com.pennant.backend.model.finance.FinAssetTypes;
import com.pennant.backend.model.finance.FinCollaterals;
import com.pennant.backend.model.finance.FinFeeDetail;
import com.pennant.backend.model.finance.FinFeeReceipt;
import com.pennant.backend.model.finance.FinODDetails;
import com.pennant.backend.model.finance.FinODPenaltyRate;
import com.pennant.backend.model.finance.FinReceiptData;
import com.pennant.backend.model.finance.FinReceiptDetail;
import com.pennant.backend.model.finance.FinReceiptHeader;
import com.pennant.backend.model.finance.FinRepayHeader;
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
import com.pennant.backend.model.finance.ManualAdvise;
import com.pennant.backend.model.finance.ReceiptAllocationDetail;
import com.pennant.backend.model.finance.RepayData;
import com.pennant.backend.model.finance.RepayScheduleDetail;
import com.pennant.backend.model.finance.contractor.ContractorAssetDetail;
import com.pennant.backend.model.finance.covenant.Covenant;
import com.pennant.backend.model.finance.covenant.CovenantDocument;
import com.pennant.backend.model.finance.financetaxdetail.FinanceTaxDetail;
import com.pennant.backend.model.lmtmasters.FinanceCheckListReference;
import com.pennant.backend.model.lmtmasters.FinanceReferenceDetail;
import com.pennant.backend.model.lmtmasters.FinanceWorkFlow;
import com.pennant.backend.model.partnerbank.PartnerBank;
import com.pennant.backend.model.rmtmasters.FinTypeFees;
import com.pennant.backend.model.rmtmasters.FinanceType;
import com.pennant.backend.model.rulefactory.FeeRule;
import com.pennant.backend.service.applicationmaster.BankDetailService;
import com.pennant.backend.service.bmtmasters.BankBranchService;
import com.pennant.backend.service.fees.FeeDetailService;
import com.pennant.backend.service.finance.FeeReceiptService;
import com.pennant.backend.service.finance.FinAdvancePaymentsService;
import com.pennant.backend.service.finance.FinFeeDetailService;
import com.pennant.backend.service.finance.FinanceDetailService;
import com.pennant.backend.service.finance.FinanceMainService;
import com.pennant.backend.service.finance.FinanceTaxDetailService;
import com.pennant.backend.service.finance.ManualPaymentService;
import com.pennant.backend.service.finance.ReceiptService;
import com.pennant.backend.service.finance.covenant.CovenantsService;
import com.pennant.backend.service.lmtmasters.FinanceWorkFlowService;
import com.pennant.backend.service.payorderissue.impl.DisbursementPostings;
import com.pennant.backend.service.rmtmasters.FinTypePartnerBankService;
import com.pennant.backend.util.DisbursementConstants;
import com.pennant.backend.util.FinanceConstants;
import com.pennant.backend.util.PennantApplicationUtil;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantRegularExpressions;
import com.pennant.backend.util.RepayConstants;
import com.pennant.backend.util.SMTParameterConstants;
import com.pennant.backend.util.WorkFlowUtil;
import com.pennanttech.pennapps.core.AppException;
import com.pennanttech.pennapps.core.InterfaceException;
import com.pennanttech.pennapps.core.engine.workflow.WorkflowEngine;
import com.pennanttech.pennapps.core.model.ErrorDetail;
import com.pennanttech.pennapps.core.model.LoggedInUser;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pff.core.TableType;
import com.pennanttech.util.APIConstants;
import com.pennanttech.ws.model.finance.DisbRequest;
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
	private FinFeeDetailService finFeeDetailService;
	private BankBranchService bankBranchService;
	private FinAdvancePaymentsService finAdvancePaymentsService;
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
	private FinanceMainDAO financeMainDAO;

	// ### 18-07-2018 Ticket ID : 124998,Receipt upload
	private ReceiptUploadDetailDAO receiptUploadDetailDAO;
	private ReceiptResponseDetailDAO receiptResponseDetailDAO;
	private FinReceiptDetailDAO finReceiptDetailDAO;
	private FinanceWorkFlowService financeWorkFlowService;
	protected transient WorkflowEngine workFlow = null;
	private FinanceTaxDetailService financeTaxDetailService;
	private FinAdvancePaymentsDAO finAdvancePaymensDAO;
	private PostingsPreparationUtil postingsPreparationUtil;
	private ManualAdviseDAO manualAdviseDAO;
	private BankDetailDAO bankDetailDAO;
	private DisbursementPostings disbursementPostings;
	private BankDetailService bankDetailService;
	private CovenantsService covenantsService;
	private CovenantsDAO covenantsDAO;
	private DocumentDetailsDAO documentDetailsDAO;

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
				finScheduleData = addRepaymentService.getAddRepaymentDetails(finScheduleData, finServiceInst,
						FinanceConstants.FINSER_EVENT_CHGRPY);

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
				finScheduleData = recalService.getRecalculateSchdDetails(finScheduleData, "");

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
				finScheduleData = recalService.getRecalculateSchdDetails(finScheduleData, "");

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

			if (finServiceInst.isFlexiDisb()) {
				finScheduleData.setFlexiDisb(true);
			} else {
				financeMain.setFlexiAmount(financeMain.getFlexiAmount().add(amount));
			}

			// Removed because it is wrong convention
			// set finAssetValue = FinCurrAssetValue when there is no
			// maxDisbCheck
			/*
			 * FinanceType finType = financeDetail.getFinScheduleData().getFinanceType();
			 * if(!finType.isAlwMaxDisbCheckReq()) { financeMain.setFinAssetValue(financeMain.getFinAmount()); }
			 */

			finServiceInst.setModuleDefiner(FinanceConstants.FINSER_EVENT_ADDDISB);
			financeDetail.setModuleDefiner(FinanceConstants.FINSER_EVENT_ADDDISB);
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
					.getDBDate(DateUtility.format(finServiceInst.getFromDate(), PennantConstants.DBDateFormat));
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

	public FinanceDetail doReceiptTransaction(FinReceiptData receiptData, String eventCode) {
		logger.debug("Enteing");

		BigDecimal bounce = BigDecimal.ZERO;
		FinanceDetail financeDetail = receiptData.getFinanceDetail();
		FinScheduleData finScheduleData = financeDetail.getFinScheduleData();
		if (financeDetail.getFinScheduleData().getErrorDetails() != null
				&& !financeDetail.getFinScheduleData().getErrorDetails().isEmpty()) {
			logger.debug("Leaving - doReceiptTransaction");
			return financeDetail;
		}
		FinServiceInstruction finServiceInstruction = finScheduleData.getFinServiceInstruction();
		if (StringUtils.equals(financeDetail.getFinScheduleData().getFinServiceInstruction().getModuleDefiner(),
				FinanceConstants.FINSER_EVENT_EARLYSETTLE)) {
			receiptData.getReceiptHeader().setReasonCode(finServiceInstruction.getEarlySettlementReason());
		}
		if (StringUtils.equals(finServiceInstruction.getReqType(), APIConstants.REQTYPE_INQUIRY)) {
			if (finServiceInstruction.getToDate() == null) {
				finServiceInstruction.setToDate(finScheduleData.getFinanceMain().getMaturityDate());
			}

		}

		String receiptPurpose = financeDetail.getFinScheduleData().getFinServiceInstruction().getModuleDefiner();

		if (!RepayConstants.ALLOCATIONTYPE_MANUAL.equals(finServiceInstruction.getAllocationType())) {
			financeDetail = validateFees(financeDetail);
		}

		if (finScheduleData.getErrorDetails() != null && !finScheduleData.getErrorDetails().isEmpty()) {
			logger.debug("Leaving");
			return financeDetail;
		}

		try {
			financeDetail = doProcessReceipt(receiptData, receiptPurpose);

			if (StringUtils.equals(receiptPurpose, FinanceConstants.FINSER_EVENT_EARLYSETTLE)) {
				if (finScheduleData.getErrorDetails() == null || !finScheduleData.getErrorDetails().isEmpty()) {
					FinanceSummary summary = financeDetail.getFinScheduleData().getFinanceSummary();
					// summary.setFinStatus("M");
				}
				if (APIConstants.REQTYPE_INQUIRY.equals(finServiceInstruction.getReqType())) {
					List<ReceiptAllocationDetail> receiptAllocationDetails = financeDetail.getFinScheduleData()
							.getReceiptAllocationList();
					List<ReceiptAllocationDetail> newreceiptAllocationDetails = new ArrayList<ReceiptAllocationDetail>();
					ReceiptAllocationDetail receiptAllocation = null;
					for (ReceiptAllocationDetail receiptallocation : receiptAllocationDetails) {
						if (StringUtils.equals(RepayConstants.ALLOCATION_BOUNCE,
								receiptallocation.getAllocationType())) {
							bounce = bounce.add(receiptallocation.getTotalDue());
							receiptallocation.setDueAmount(bounce);
							receiptAllocation = receiptallocation;
						} else {
							newreceiptAllocationDetails.add(receiptallocation);
						}
					}
					newreceiptAllocationDetails.add(receiptAllocation);
					financeDetail.getFinScheduleData().setReceiptAllocationList(newreceiptAllocationDetails);
				}
			}

		} catch (InterfaceException ex) {
			logger.error("InterfaceException", ex);
			finScheduleData = receiptService.setErrorToFSD(finScheduleData, "9998", ex.getMessage());
			return financeDetail;
		} catch (AppException appEx) {
			logger.error("AppException", appEx);
			finScheduleData = receiptService.setErrorToFSD(finScheduleData, "9999", appEx.getMessage());
			return financeDetail;
		} catch (Exception e) {
			logger.error("Exception", e);
			WSReturnStatus returnStatus = APIErrorHandlerService.getFailedStatus();
			finScheduleData = receiptService.setErrorToFSD(finScheduleData, returnStatus.getReturnCode(),
					returnStatus.getReturnText());
			return financeDetail;
		}

		logger.debug("Leaving");
		return financeDetail;
	}

	public FinanceDetail validateFees(FinanceDetail financeDetail) {
		FinScheduleData finScheduleData = financeDetail.getFinScheduleData();
		FinServiceInstruction finServiceInst = finScheduleData.getFinServiceInstruction();
		String roundingMode = finScheduleData.getFinanceMain().getCalRoundingMode();
		int roundingTarget = finScheduleData.getFinanceMain().getRoundingTarget();

		// FIXME: PV AS OF NOW, PNLY ONE FEE IS HANDLED. AFTER FIRST RELEASE
		// MULTI FEES TO BE DEVELOPED.
		// GST to be tested
		boolean isAPIFeeRequested = false;
		boolean isEventFeeRequired = false;
		String apiFeeCode = null;
		BigDecimal apiActualFee = BigDecimal.ZERO;
		BigDecimal apiPaidFee = BigDecimal.ZERO;
		BigDecimal apiWaived = BigDecimal.ZERO;
		String eventFeeCode = null;
		BigDecimal eventActualFee = BigDecimal.ZERO;
		BigDecimal maxWaiver = BigDecimal.ZERO;
		BigDecimal maxWaiverAllowed = BigDecimal.ZERO;

		// Validate Fees
		if (finScheduleData.getFinFeeDetailList() != null && !finScheduleData.getFinFeeDetailList().isEmpty()) {
			isEventFeeRequired = true;
			eventFeeCode = finScheduleData.getFinFeeDetailList().get(0).getFeeTypeCode();
			eventActualFee = finScheduleData.getFinFeeDetailList().get(0).getActualAmount();
			maxWaiver = finScheduleData.getFinFeeDetailList().get(0).getMaxWaiverPerc();

			if (maxWaiver.compareTo(BigDecimal.valueOf(100)) == 0) {
				maxWaiverAllowed = eventActualFee;
			} else if (maxWaiver.compareTo(BigDecimal.ZERO) > 0) {
				maxWaiverAllowed = eventActualFee.multiply(maxWaiver).divide(BigDecimal.valueOf(100), 0,
						RoundingMode.HALF_DOWN);
				maxWaiverAllowed = CalculationUtil.roundAmount(maxWaiverAllowed, roundingMode, roundingTarget);
			}
		}

		if (StringUtils.equals(finServiceInst.getReqType(), APIConstants.REQTYPE_INQUIRY)) {
			return financeDetail;
		}

		if (finServiceInst.getFinFeeDetails() != null && !finServiceInst.getFinFeeDetails().isEmpty()) {
			isAPIFeeRequested = true;
			apiFeeCode = finServiceInst.getFinFeeDetails().get(0).getFeeTypeCode().toUpperCase();
			apiActualFee = finServiceInst.getFinFeeDetails().get(0).getActualAmount();
			apiPaidFee = finServiceInst.getFinFeeDetails().get(0).getPaidAmount();
			apiWaived = finServiceInst.getFinFeeDetails().get(0).getWaivedAmount();
		}

		// Event fees not applicable and API not requested.
		if (!isAPIFeeRequested && !isEventFeeRequired) {
			return financeDetail;
		}

		// Fee is Mandatory but API does not requested
		if (!isAPIFeeRequested && isEventFeeRequired) {
			finScheduleData = receiptService.setErrorToFSD(finScheduleData, "65019", eventFeeCode);
			return financeDetail;
		}

		// Mismatch in the Fees requirement.
		if (isAPIFeeRequested && !isEventFeeRequired) {
			finScheduleData = receiptService.setErrorToFSD(finScheduleData, "90245", null);
			return financeDetail;
		}

		// Mismatch in the Fees requirement.
		if (!StringUtils.equalsIgnoreCase(apiFeeCode, eventFeeCode)) {
			finScheduleData = receiptService.setErrorToFSD(finScheduleData, "90247", null);
			return financeDetail;
		}

		// Negative Amounts
		if (apiActualFee.compareTo(BigDecimal.ZERO) < 0 || apiPaidFee.compareTo(BigDecimal.ZERO) < 0
				|| apiWaived.compareTo(BigDecimal.ZERO) < 0) {
			finScheduleData = receiptService.setErrorToFSD(finScheduleData, "90259", apiFeeCode);
			return financeDetail;
		}

		String parm0 = null;
		String parm1 = null;
		int formatter = CurrencyUtil.getFormat(finScheduleData.getFinanceMain().getFinCcy());

		// Waiver Exceeds the limit
		if (apiWaived.compareTo(maxWaiverAllowed) > 0) {
			parm0 = "Fee Waiver";
			parm1 = PennantApplicationUtil.amountFormate(maxWaiverAllowed, formatter);
			finScheduleData = receiptService.setErrorToFSD(finScheduleData, "90257", parm0, parm1, apiFeeCode);
			return financeDetail;
		}

		// API Actual Amount <> EVENT Actual Amount
		if ((apiActualFee.compareTo(eventActualFee) != 0)) {
			parm0 = "Fee Amount";
			parm1 = PennantApplicationUtil.amountFormate(eventActualFee, formatter);
			finScheduleData = receiptService.setErrorToFSD(finScheduleData, "90258", parm0, parm1, apiFeeCode);
			return financeDetail;
		}

		// Actual Amount - Paid - Waived <> 0
		if ((apiActualFee.subtract(apiPaidFee).subtract(apiWaived)).compareTo(BigDecimal.ZERO) != 0) {
			parm0 = "Fee Amount - Fee Waived";
			parm1 = "Fee Paid";
			finScheduleData = receiptService.setErrorToFSD(finScheduleData, "90258", parm0, parm1, apiFeeCode);
			return financeDetail;
		}

		return financeDetail;
	}

	private FinanceDetail doProcessReceipt(FinReceiptData receiptData, String receiptPurpose) throws Exception {
		logger.debug("Entering");
		try {
			// FinReceiptData receiptData = setReceiptData(financeDetail,
			// receiptPurpose);
			FinanceDetail financeDetail = receiptData.getFinanceDetail();
			FinReceiptHeader rch = receiptData.getReceiptHeader();
			FinReceiptDetail rcd = rch.getReceiptDetails().get(0);
			FinScheduleData finScheduleData = financeDetail.getFinScheduleData();
			FinServiceInstruction finServiceInst = finScheduleData.getFinServiceInstruction();
			FinanceMain financeMain = finScheduleData.getFinanceMain();

			int receiptPurposeCtg = receiptCalculator.setReceiptCategory(receiptPurpose);
			receiptData.setTotalPastDues(receiptCalculator.getTotalNetPastDue(receiptData));
			if (receiptPurposeCtg == 2) {
				rch.getReceiptDetails().clear();
				receiptService.createXcessRCD(receiptData);
			}
			BigDecimal amount = rch.getReceiptAmount().subtract(receiptData.getExcessAvailable());
			if (receiptData.getTotalPastDues().compareTo(amount) >= 0) {
				rcd.setDueAmount(amount);
				receiptData.setTotalPastDues(receiptData.getTotalPastDues().subtract(amount));
			} else {
				rcd.setDueAmount(receiptData.getTotalPastDues());
				receiptData.setTotalPastDues(BigDecimal.ZERO);
			}
			if (receiptPurposeCtg == 2) {
				rch.getReceiptDetails().add(rcd);
			}

			if (finServiceInst.isReceiptUpload()
					&& StringUtils.equals(finServiceInst.getReqType(), APIConstants.REQTYPE_POST)
					&& receiptService.dedupCheckRequest(rch, receiptPurpose)) {
				long rchID = receiptService.CheckDedupSP(rch, receiptPurpose);

				if (rchID != 0) {
					finReceiptHeaderDAO.updateReceiptStatusAndRealizationDate(rchID, RepayConstants.PAYSTATUS_REALIZED,
							rch.getRealizationDate());
					finReceiptDetailDAO.updateReceiptStatusByReceiptId(rchID, RepayConstants.PAYSTATUS_REALIZED);
					WSReturnStatus returnStatus = APIErrorHandlerService.getSuccessStatus();
					receiptService.setErrorToFSD(finScheduleData, returnStatus.getReturnCode(),
							returnStatus.getReturnText());
					return financeDetail;
				}
			}

			if (StringUtils.equalsIgnoreCase(receiptData.getSourceId(), APIConstants.FINSOURCE_ID_API)) {
				if (CollectionUtils.isNotEmpty(rch.getAllocations())) {
					receiptData.getFinanceDetail().getFinScheduleData().setReceiptAllocationList(rch.getAllocations());
				}
			}

			if (StringUtils.equals(finServiceInst.getReqType(), APIConstants.REQTYPE_POST)) {
				// FIXME: PV. IS IT REQUIRED HERE? VALIDATION AL;READY DONE IN
				// RECEIPT SERVICE.
				String receiptMode = finServiceInst.getPaymentMode();

				if (StringUtils.equals(receiptMode, RepayConstants.RECEIPTMODE_ONLINE)) {
					receiptMode = finServiceInst.getSubReceiptMode();
				}
				if (!StringUtils.equals(receiptMode, RepayConstants.RECEIPTMODE_CASH)
						&& !StringUtils.equals(receiptMode, RepayConstants.RECEIPTMODE_CHEQUE)) {
					long fundingAccount = finServiceInst.getReceiptDetail().getFundingAc();
					finServiceInst.setFundingAc(fundingAccount);
					int count = finTypePartnerBankService.getPartnerBankCount(financeMain.getFinType(), receiptMode,
							AccountConstants.PARTNERSBANK_RECEIPTS, fundingAccount);
					if (count <= 0) {
						finScheduleData = receiptService.setErrorToFSD(finScheduleData, "90263", null);
						return financeDetail;
					}

					// fetch partner bank details
					PartnerBank partnerBank = partnerBankDAO.getPartnerBankById(rcd.getFundingAc(), "");
					if (partnerBank != null) {
						rcd.setPartnerBankAc(partnerBank.getAccountNo());
						rcd.setPartnerBankAcType(partnerBank.getAcType());
					}
				}
				int version = 0;
				// Receipt upload process
				if (finServiceInst.isReceiptdetailExits()) {
					FinReceiptData oldReceiptData = this.receiptService.getFinReceiptDataById(
							finServiceInst.getFinReference(), AccountEventConstants.ACCEVENT_REPAY,
							FinanceConstants.FINSER_EVENT_RECEIPT, FinanceConstants.REALIZATION_MAKER);
					receiptData = oldReceiptData;

					version = receiptData.getFinanceDetail().getFinScheduleData().getFinanceMain().getVersion();
					receiptData.getFinanceDetail().getFinScheduleData().getFinanceMain()
							.setRecordStatus(PennantConstants.RCD_STATUS_APPROVED);
					receiptData.getFinanceDetail().getFinScheduleData().getFinanceMain().setRecordType("");
					receiptData.getFinanceDetail().getFinScheduleData().setSchduleGenerated(true);
					receiptData.getReceiptHeader().setRealizationDate(finServiceInst.getRealizationDate());
				} else {
					// Set Version value
					version = receiptData.getFinanceDetail().getFinScheduleData().getFinanceMain().getVersion();
					receiptData.getFinanceDetail().getFinScheduleData().getFinanceMain().setVersion(version + 1);
					receiptData.getFinanceDetail().getFinScheduleData().getFinanceMain().setRecordType("");
					receiptData.getFinanceDetail().getFinScheduleData().setSchduleGenerated(true);
				}

				// Save the Schedule details
				AuditHeader auditHeader = getAuditHeader(receiptData, PennantConstants.TRAN_WF);

				// ### ticket id:124998,
				// setting to temp table
				if (finServiceInst.isReceiptUpload() && receiptPurposeCtg != 0
						&& StringUtils.equals(finServiceInst.getStatus(), "A")
						&& (StringUtils.equals(finServiceInst.getPaymentMode(), "CHEQUE")
								|| StringUtils.equals(finServiceInst.getPaymentMode(), "DD"))) {

					WorkFlowDetails workFlowDetails = null;
					String roleCode = FinanceConstants.DEPOSIT_APPROVER;// default
																		// value
					String nextRolecode = FinanceConstants.REALIZATION_MAKER;// defaulting
																				// role
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
						if (receiptPurposeCtg == 2) {
							eventCode = AccountEventConstants.ACCEVENT_EARLYSTL;
						} else {
							eventCode = AccountEventConstants.ACCEVENT_EARLYPAY;
						}
					}

					receiptData.getFinanceDetail().getFinScheduleData().setFinanceMain(financeMain);
					APIHeader reqHeaderDetails = (APIHeader) PhaseInterceptorChain.getCurrentMessage().getExchange()
							.get(APIHeader.API_HEADER_KEY);
					auditHeader.setApiHeader(reqHeaderDetails);

					auditHeader = receiptService.saveOrUpdate(auditHeader);

				} else {

					APIHeader reqHeaderDetails = (APIHeader) PhaseInterceptorChain.getCurrentMessage().getExchange()
							.get(APIHeader.API_HEADER_KEY);
					auditHeader.setApiHeader(reqHeaderDetails);
					BigDecimal earlyPayAmount = receiptData.getRemBal();
					String recalType = rch.getEffectSchdMethod();
					finScheduleData.getFinanceMain().setReceiptPurpose(receiptPurpose);
					if (receiptPurposeCtg == 1) {
						finScheduleData = ScheduleCalculator.recalEarlyPaySchedule(finScheduleData, rch.getValueDate(),
								null, earlyPayAmount, recalType);
						receiptData = receiptCalculator.addPartPaymentAlloc(receiptData);
					}
					Cloner cloner = new Cloner();
					FinReceiptData tempReceiptData = cloner.deepClone(receiptData);
					receiptData.getFinanceDetail().setFinScheduleData(finScheduleData);
					receiptData.getReceiptHeader().setValueDate(rch.getValueDate());

					receiptData.setDueAdjusted(true);
					if (receiptPurposeCtg == 2) {
						boolean duesAdjusted = receiptService
								.checkDueAdjusted(receiptData.getReceiptHeader().getAllocations(), receiptData);
						if (!duesAdjusted) {
							receiptData = receiptService.adjustToExcess(receiptData);
							receiptData.setDueAdjusted(false);
						}
					}
					if (receiptData.isDueAdjusted()) {
						for (ReceiptAllocationDetail allocate : receiptData.getReceiptHeader().getAllocations()) {
							allocate.setPaidAvailable(allocate.getPaidAmount());
							allocate.setWaivedAvailable(allocate.getWaivedAmount());
							allocate.setPaidAmount(BigDecimal.ZERO);
							allocate.setPaidGST(BigDecimal.ZERO);
							allocate.setTotalPaid(BigDecimal.ZERO);
							allocate.setBalance(allocate.getTotalDue());
							allocate.setWaivedAmount(BigDecimal.ZERO);
							allocate.setWaivedGST(BigDecimal.ZERO);
						}

						receiptData.setBuildProcess("R");
						receiptData = receiptCalculator.initiateReceipt(receiptData, false);

					}
					receiptData.getFinanceDetail().getFinScheduleData().setFinanceScheduleDetails(
							tempReceiptData.getFinanceDetail().getFinScheduleData().getFinanceScheduleDetails());

					if (finServiceInst.isNonStp()) {
						auditHeader = receiptService.doApprove(auditHeader);
					} else {

						WorkFlowDetails workFlowDetails = null;
						String roleCode = finServiceInst.getProcessStage();
						String nextRolecode = finServiceInst.getProcessStage();

						String taskid = null;
						String nextTaskId = null;
						long workFlowId = 0;
						String finEvent = FinanceConstants.FINSER_EVENT_RECEIPT;

						FinanceWorkFlow financeWorkFlow = financeWorkFlowService.getApprovedFinanceWorkFlowById(
								financeMain.getFinType(), finEvent, PennantConstants.WORFLOW_MODULE_FINANCE);

						if (financeWorkFlow == null) {
							FinanceDetail response = new FinanceDetail();
							doEmptyResponseObject(response);
							response.setReturnStatus(APIErrorHandlerService.getFailedStatus("90339"));
							return response;
						}

						workFlowDetails = WorkFlowUtil.getDetailsByType(financeWorkFlow.getWorkFlowType());
						String[] workFlowRoles = workFlowDetails.getWorkFlowRoles().split(";");

						if (StringUtils.isBlank(finServiceInst.getProcessStage())) {
							roleCode = workFlowDetails.getFirstTaskOwner();
							nextRolecode = roleCode;
						}
						boolean roleNotFound = false;
						for (String workFlowRole : workFlowRoles) {
							if (StringUtils.equals(workFlowRole, roleCode)) {
								roleNotFound = true;
								break;
							}
						}

						if (!roleNotFound) {
							FinanceDetail response = new FinanceDetail();
							doEmptyResponseObject(response);
							String[] valueParm = new String[1];
							valueParm[0] = roleCode;
							response.setReturnStatus(APIErrorHandlerService.getFailedStatus("API004", valueParm));
							return response;
						}

						if (finServiceInst.getPaymentMode().equals("CASH")) {
							if (!roleCode.equals("RECEIPT_MAKER") && !roleCode.equals("REALIZATION_APPROVER")) {
								FinanceDetail response = new FinanceDetail();
								doEmptyResponseObject(response);
								String[] valueParm = new String[1];
								valueParm[0] = "CASH PAYMENT MODE";
								response.setReturnStatus(APIErrorHandlerService.getFailedStatus("30556", valueParm));
								return response;
							}
						}

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
						financeMain.setRecordType(PennantConstants.RECORD_TYPE_NEW);
						financeMain.setNextTaskId(nextTaskId + ";");
						financeMain.setNewRecord(true);
						financeMain.setVersion(1);
						financeMain.setRcdMaintainSts(FinanceConstants.FINSER_EVENT_RECEIPT);
						financeMain.setLastMntOn(new Timestamp(System.currentTimeMillis()));
						financeMain.setRecordStatus(PennantConstants.RCD_STATUS_SAVED);
						receiptData.getReceiptHeader().setTaskId(taskid);
						receiptData.getReceiptHeader().setNextTaskId(nextTaskId + ";");
						receiptData.getReceiptHeader().setRoleCode(roleCode);
						receiptData.getReceiptHeader().setNextRoleCode(nextRolecode);
						receiptData.getReceiptHeader().setWorkflowId(workFlowId);
						receiptData.getReceiptHeader().setRecordStatus(PennantConstants.RCD_STATUS_SAVED);
						receiptData.getReceiptHeader().setFinType(financeMain.getFinType());

						auditHeader = receiptService.saveOrUpdate(auditHeader);
					}
				}

				if (auditHeader.getErrorMessage() != null) {
					for (ErrorDetail auditErrorDetail : auditHeader.getErrorMessage()) {
						receiptService.setErrorToFSD(finScheduleData, auditErrorDetail.getCode(),
								auditErrorDetail.getError());
						return financeDetail;
					}
				}

				receiptData = (FinReceiptData) auditHeader.getAuditDetail().getModelData();
				// FIXME: PV re-look at it
				financeDetail = getServiceInstResponse(receiptData.getFinanceDetail().getFinScheduleData());
				rch = receiptData.getReceiptHeader();
				financeDetail.getFinScheduleData().getFinServiceInstruction().setReceiptId(rch.getReceiptID());

				List<FinServiceInstruction> finServInstList = new ArrayList<>();
				for (FinReceiptDetail recDtl : rch.getReceiptDetails()) {
					for (FinRepayHeader rpyHeader : recDtl.getRepayHeaders()) {
						FinServiceInstruction finServInst = new FinServiceInstruction();
						finServInst.setFinReference(financeMain.getFinReference());
						finServInst.setFinEvent(rpyHeader.getFinEvent());
						finServInst.setAmount(rpyHeader.getRepayAmount());
						finServInst.setAppDate(DateUtility.getAppDate());
						finServInst.setSystemDate(DateUtility.getSysDate());
						finServInst.setMaker(auditHeader.getAuditUsrId());
						finServInst.setMakerAppDate(DateUtility.getAppDate());
						finServInst.setMakerSysDate(DateUtility.getSysDate());
						finServInst.setChecker(auditHeader.getAuditUsrId());
						finServInst.setCheckerAppDate(DateUtility.getAppDate());
						finServInst.setCheckerSysDate(DateUtility.getSysDate());
						finServInst.setReference(String.valueOf(rch.getReceiptID()));
						finServInstList.add(finServInst);
					}
				}

				// set receipt id in data
				if (finServiceInst.isReceiptUpload() && !finServiceInst.isReceiptResponse()) {
					this.receiptUploadDetailDAO.updateReceiptId(finServiceInst.getUploadDetailId(), rcd.getReceiptID());
				}

				// set receipt id response job
				if (finServiceInst.isReceiptUpload() && finServiceInst.isReceiptResponse()) {
					this.receiptResponseDetailDAO.updateReceiptResponseId(finServiceInst.getRootId(),
							rcd.getReceiptID());
				}

			} else {

				BigDecimal earlyPayAmount = receiptData.getRemBal();
				String recalType = rch.getEffectSchdMethod();
				finScheduleData.getFinanceMain().setReceiptPurpose(receiptPurpose);
				if (receiptPurposeCtg == 1) {
					finScheduleData = ScheduleCalculator.recalEarlyPaySchedule(finScheduleData, rch.getValueDate(),
							null, earlyPayAmount, recalType);
					receiptData = receiptCalculator.addPartPaymentAlloc(receiptData);
				}
				receiptData.getFinanceDetail().setFinScheduleData(finScheduleData);

				for (ReceiptAllocationDetail allocate : receiptData.getReceiptHeader().getAllocations()) {
					allocate.setPaidAvailable(allocate.getPaidAmount());
					allocate.setWaivedAvailable(allocate.getWaivedAmount());
					allocate.setPaidAmount(BigDecimal.ZERO);
					allocate.setPaidGST(BigDecimal.ZERO);
					allocate.setTotalPaid(BigDecimal.ZERO);
					allocate.setBalance(allocate.getTotalDue());
					allocate.setWaivedAmount(BigDecimal.ZERO);
					allocate.setWaivedGST(BigDecimal.ZERO);
				}
				receiptData.setBuildProcess("R");
				receiptData = receiptCalculator.initiateReceipt(receiptData, false);
				financeDetail = getServiceInstResponse(receiptData.getFinanceDetail().getFinScheduleData());
				FinanceSummary summary = financeDetail.getFinScheduleData().getFinanceSummary();
				summary.setFinODDetail(rch.getFinODDetails());
				financeDetail.getFinScheduleData().setFinODDetails(rch.getFinODDetails());
			}

			logger.debug("Leaving");
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
			if (StringUtils.equals(finServiceInst.getModuleDefiner(), FinanceConstants.FINSER_EVENT_EARLYRPY)) {
				boolean isInSubvention = receiptService.isInSubVention(finScheduleData.getFinanceMain(),
						curBussniessDate);
				if (isInSubvention) {
					String[] valueParm = new String[1];
					valueParm[0] = "Not allowed to do Partial Settlement in Subvention Period.";
					WSReturnStatus status = APIErrorHandlerService.getFailedStatus("30550", valueParm);
					returnMap.put("ReturnCode", status.getReturnCode());
					returnMap.put("ReturnText", status.getReturnText());
				}
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

				if (curSchd.isTDSApplicable()) {
					BigDecimal tdsPerc = new BigDecimal(
							SysParamUtil.getValue(CalculationConstants.TDS_PERCENTAGE).toString());
					if (tdsPerc.compareTo(BigDecimal.ZERO) > 0) {
						tdsMultiplier = (new BigDecimal(100)).divide(new BigDecimal(100).subtract(tdsPerc), 20,
								RoundingMode.HALF_DOWN);
					}
				}

				schFeeBal = schFeeBal.add(curSchd.getFeeSchd().subtract(curSchd.getSchdFeePaid()));

				if (DateUtility.compare(schdDate, curBussniessDate) < 0) {
					pftBalance = pftBalance.add(curSchd.getProfitSchd().subtract(curSchd.getSchdPftPaid()));
					priBalance = priBalance.add(curSchd.getPrincipalSchd().subtract(curSchd.getSchdPriPaid()));
					if (curSchd.isTDSApplicable()) {
						BigDecimal pft = curSchd.getProfitSchd().subtract(curSchd.getSchdPftPaid());
						BigDecimal actualPft = pft.divide(tdsMultiplier, 0, RoundingMode.HALF_DOWN);
						tdsReturns = tdsReturns.add(pft.subtract(actualPft));
						tdsReturns = CalculationUtil.roundAmount(tdsReturns, financeMain.getCalRoundingMode(),
								financeMain.getRoundingTarget());
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

						if (curSchd.isTDSApplicable()) {
							BigDecimal actualPft = remPft.divide(tdsMultiplier, 0, RoundingMode.HALF_DOWN);
							tdsReturns = tdsReturns.add(remPft.subtract(actualPft));
							tdsReturns = CalculationUtil.roundAmount(tdsReturns, financeMain.getCalRoundingMode(),
									financeMain.getRoundingTarget());
						}
						partAccrualReq = false;
					} else {
						pftBalance = pftBalance.add(curSchd.getProfitSchd().subtract(curSchd.getSchdPftPaid()));
						priBalance = priBalance.add(curSchd.getPrincipalSchd().subtract(curSchd.getSchdPriPaid()));
						if (curSchd.isTDSApplicable()) {
							BigDecimal pft = curSchd.getProfitSchd().subtract(curSchd.getSchdPftPaid());
							BigDecimal actualPft = pft.divide(tdsMultiplier, 0, RoundingMode.HALF_DOWN);
							tdsReturns = tdsReturns.add(pft.subtract(actualPft));
							tdsReturns = CalculationUtil.roundAmount(tdsReturns, financeMain.getCalRoundingMode(),
									financeMain.getRoundingTarget());
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

							if (curSchd.isTDSApplicable()) {
								BigDecimal actualPft = (accruedPft.add(prvSchd.getProfitBalance()))
										.divide(tdsMultiplier, 0, RoundingMode.HALF_DOWN);
								tdsReturns = tdsReturns
										.add(accruedPft.add(prvSchd.getProfitBalance()).subtract(actualPft));
								tdsReturns = CalculationUtil.roundAmount(tdsReturns, financeMain.getCalRoundingMode(),
										financeMain.getRoundingTarget());
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

			List<ManualAdvise> manualAdviseFees = manualAdviseDAO.getManualAdviseByRef(finReference,
					FinanceConstants.MANUAL_ADVISE_RECEIVABLE, " ");
			BigDecimal bounceCharge = BigDecimal.ZERO;
			if (manualAdviseFees != null && !manualAdviseFees.isEmpty()) {
				for (ManualAdvise advisedFees : manualAdviseFees) {
					bounceCharge = bounceCharge
							.add(advisedFees.getAdviseAmount().subtract(advisedFees.getPaidAmount()));
				}
			}
			BigDecimal remBal = priBalance.add(pftBalance).add(schFeeBal).add(latePayPftBal).add(penaltyBal)
					.add(bounceCharge).subtract(tdsReturns);
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

	public FinanceDetail doChangeGestationPeriod(FinServiceInstruction finServiceInst, String eventCode) {

		logger.debug("Enteing");

		// fetch finance data
		FinanceDetail financeDetail = getFinanceDetails(finServiceInst, eventCode);
		// validate terms
		AuditDetail auditDetail = doChangeGestationValidations(financeDetail, finServiceInst);
		if (auditDetail.getErrorDetails() != null) {
			for (ErrorDetail errorDetail : auditDetail.getErrorDetails()) {
				financeDetail = new FinanceDetail();
				doEmptyResponseObject(financeDetail);
				financeDetail.setReturnStatus(
						APIErrorHandlerService.getFailedStatus(errorDetail.getCode(), errorDetail.getError()));

				return financeDetail;
			}
		}
		if (financeDetail != null) {

			FinScheduleData finScheduleData = financeDetail.getFinScheduleData();
			FinanceMain financeMain = finScheduleData.getFinanceMain();
			FinanceType financeType = finScheduleData.getFinanceType();

			int fddLockPeriod = financeType.getFddLockPeriod();
			if (financeMain.isAllowGrcPeriod() && !ImplementationConstants.APPLY_FDDLOCKPERIOD_AFTERGRACE) {
				fddLockPeriod = 0;
			}

			if (financeMain.isAlwFlexi()) {
				int oldGrcTerms = financeMain.getGraceTerms();
				int newGrcTerms = finServiceInst.getGrcTerms();
				int numberOfTerms = newGrcTerms - oldGrcTerms;
				financeMain.setNumberOfTerms(financeMain.getNumberOfTerms() - numberOfTerms);
			}
			financeMain.setFinSourceID(APIConstants.FINSOURCE_ID_API);
			financeMain.setRcdMaintainSts(FinanceConstants.FINSER_EVENT_CHGGRCEND);
			finServiceInst.setFinReference(financeMain.getFinReference());
			financeMain.setGraceTerms(finServiceInst.getGrcTerms());

			// GraceEndDate Calculation
			financeMain.setCalGrcTerms(financeMain.getGraceTerms());
			List<Calendar> scheduleDateList = FrequencyUtil
					.getNextDate(financeMain.getGrcPftFrq(), financeMain.getGraceTerms(), financeMain.getFinStartDate(),
							HolidayHandlerTypes.MOVE_NONE, false, financeType.getFddLockPeriod())
					.getScheduleList();

			Date geDate = null;
			if (scheduleDateList != null) {
				Calendar calendar = scheduleDateList.get(scheduleDateList.size() - 1);
				geDate = DateUtility.getDBDate(DateUtility.format(calendar.getTime(), PennantConstants.DBDateFormat));
			}
			Date curBussDate = DateUtility.getAppDate();
			if (geDate.before(DateUtility.addDays(curBussDate, 1))) {
				String[] valueParm = new String[2];
				valueParm[0] = "CalGrcEndDate: " + geDate;
				valueParm[1] = "AppDate";
				auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetail("90205", valueParm)));
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
			financeMain.setEventFromDate(finScheduleData.getFinanceMain().getGrcPeriodEndDate());
			financeMain.setGrcPeriodEndDate(geDate);

			financeMain.setNextRepayDate(FrequencyUtil.getNextDate(finScheduleData.getFinanceMain().getRepayFrq(), 1,
					financeMain.getGrcPeriodEndDate(), HolidayHandlerTypes.MOVE_NONE, false, fddLockPeriod)
					.getNextFrequencyDate());

			if ((fddLockPeriod != 0) && !ImplementationConstants.ALLOW_FDD_ON_RVW_DATE) {
				financeMain.setNextRepayRvwDate(FrequencyUtil
						.getNextDate(finScheduleData.getFinanceMain().getRepayRvwFrq(), 1,
								financeMain.getGrcPeriodEndDate(), HolidayHandlerTypes.MOVE_NONE, false, 0)
						.getNextFrequencyDate());
			} else {
				financeMain.setNextRepayRvwDate(FrequencyUtil
						.getNextDate(finScheduleData.getFinanceMain().getRepayRvwFrq(), 1,
								financeMain.getGrcPeriodEndDate(), HolidayHandlerTypes.MOVE_NONE, false, fddLockPeriod)
						.getNextFrequencyDate());
			}

			financeMain.setNextRepayPftDate(FrequencyUtil
					.getNextDate(finScheduleData.getFinanceMain().getRepayPftFrq(), 1,
							financeMain.getGrcPeriodEndDate(), HolidayHandlerTypes.MOVE_NONE, false, fddLockPeriod)
					.getNextFrequencyDate());

			if (!finScheduleData.getFinanceMain().isAlwFlexi()) {
				List<Calendar> dateList = null;
				dateList = FrequencyUtil.getNextDate(finScheduleData.getFinanceMain().getRepayFrq(),
						finScheduleData.getFinanceMain().getNumberOfTerms(),
						finScheduleData.getFinanceMain().getNextRepayDate(), HolidayHandlerTypes.MOVE_NONE, true, 0)
						.getScheduleList();
				if (dateList != null) {
					Calendar calendar = dateList.get(dateList.size() - 1);
					financeMain.setMaturityDate(calendar.getTime());
				}
			}
			financeMain.setDevFinCalReq(false);
			finServiceInst.setFinEvent(FinanceConstants.FINSER_EVENT_CHGGRCEND);

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

				// For HybridFlexi
				financeDetail.getFinScheduleData().getFinanceMain().setChgDropLineSchd(true);

				// Call Schedule calculator for graceEndDate
				financeDetail.setFinScheduleData(ScheduleCalculator.changeGraceEnd(financeDetail.getFinScheduleData()));
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

	private AuditDetail doChangeGestationValidations(FinanceDetail financeDetail,
			FinServiceInstruction finServiceInstruction) {

		FinanceType financeType = financeDetail.getFinScheduleData().getFinanceType();
		AuditDetail auditDetail = new AuditDetail();

		String param = "Grace";
		boolean alwFlexi = false;
		if (financeDetail.getFinScheduleData().getFinanceMain().isAlwFlexi()) {
			alwFlexi = true;
			param = "PureFlexi";
		}

		if (!alwFlexi) {
			String[] valueParm = new String[2];
			valueParm[0] = "Change Gestation";
			valueParm[1] = "LoanType: " + financeType.getFinType();
			auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetail("90329", valueParm)));
			return auditDetail;
		}

		if (financeType.getMinGrcTerms() > 0 && financeType.getMaxGrcTerms() > 0) {
			if (finServiceInstruction.getGrcTerms() < financeType.getMinGrcTerms()
					|| finServiceInstruction.getGrcTerms() > financeType.getMaxGrcTerms()) {
				String[] valueParm = new String[3];
				valueParm[0] = param + "Terms";
				valueParm[1] = String.valueOf(financeType.getMinGrcTerms());
				valueParm[2] = String.valueOf(financeType.getMaxGrcTerms());
				auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetail("90272", valueParm)));
				return auditDetail;
			}
		}
		Date curBussDate = DateUtility.getAppDate();
		if (financeDetail.getFinScheduleData().getFinanceMain().getGrcPeriodEndDate()
				.before(DateUtility.addDays(curBussDate, 1))) {
			String[] valueParm = new String[2];
			valueParm[0] = param + "EndDate: "
					+ financeDetail.getFinScheduleData().getFinanceMain().getGrcPeriodEndDate();
			valueParm[1] = "AppDate";
			auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetail("90205", valueParm)));
		}
		if (financeDetail.getFinScheduleData().getFinanceMain().getGrcPeriodEndDate()
				.before(DateUtility.addDays(curBussDate, 1))) {
			String[] valueParm = new String[2];
			valueParm[0] = param + "EndDate: "
					+ financeDetail.getFinScheduleData().getFinanceMain().getGrcPeriodEndDate();
			valueParm[1] = "AppDate";
			auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetail("90205", valueParm)));
		}
		Date validFrom = financeDetail.getFinScheduleData().getFinanceMain().getFinStartDate();
		List<FinanceScheduleDetail> scheduelist = financeDetail.getFinScheduleData().getFinanceScheduleDetails();
		for (int i = 1; i < scheduelist.size(); i++) {

			FinanceScheduleDetail curSchd = scheduelist.get(i);
			if (curSchd.getSchDate().compareTo(DateUtility.getAppDate()) < 0) {
				validFrom = DateUtility.getAppDate();
				continue;
			}
			if (StringUtils.equals(FinanceConstants.FLAG_BPI, curSchd.getBpiOrHoliday())) {
				validFrom = curSchd.getSchDate();
				continue;
			}

			if (curSchd.getSchdPftPaid().compareTo(BigDecimal.ZERO) > 0
					|| curSchd.getSchdPriPaid().compareTo(BigDecimal.ZERO) > 0
					|| curSchd.getSchdFeePaid().compareTo(BigDecimal.ZERO) > 0
					|| curSchd.getSchdInsPaid().compareTo(BigDecimal.ZERO) > 0
					|| curSchd.getSuplRentPaid().compareTo(BigDecimal.ZERO) > 0
					|| curSchd.getIncrCostPaid().compareTo(BigDecimal.ZERO) > 0) {

				validFrom = curSchd.getSchDate();
				continue;
			}
		}
		if (financeDetail.getFinScheduleData().getFinanceMain().getGrcPeriodEndDate().compareTo(validFrom) <= 0) {
			auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetail("41019", null)));
		}

		return auditDetail;

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
	 * @throws InterfaceException
	 * @throws JaxenException
	 */
	private FinanceDetail getResponse(FinanceDetail financeDetail, FinServiceInstruction finServiceInst)
			throws JaxenException, InterfaceException {
		logger.debug("Entering");

		// fees calculation
		FinScheduleData finScheduleData = financeDetail.getFinScheduleData();
		List<FinServiceInstruction> finInstList = new ArrayList<>();
		finInstList.add(finServiceInst);
		finScheduleData.setFinServiceInstructions(finInstList);
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

	public FinanceDetail getFinanceDetail(FinServiceInstruction finServiceInst, String eventCode) {
		logger.debug("Entering");

		FinanceDetail financeDetail = null;

		String finReference = finServiceInst.getFinReference();
		String finSerEvent = "";
		if ("SP".equalsIgnoreCase(finServiceInst.getReceiptPurpose())) {
			finSerEvent = FinanceConstants.FINSER_EVENT_SCHDRPY;
		} else if ("EP".equalsIgnoreCase(finServiceInst.getReceiptPurpose())) {
			finSerEvent = FinanceConstants.FINSER_EVENT_EARLYRPY;
		} else {
			finSerEvent = FinanceConstants.FINSER_EVENT_EARLYSETTLE;
		}
		if (!finServiceInst.isWif()) {
			financeDetail = financeDetailService.getFinanceDetailById(finReference, false, "", false, finSerEvent, "");
		} else {
			financeDetail = financeDetailService.getWIFFinance(finReference, false, null);
		}

		List<FinFeeDetail> newList = new ArrayList<FinFeeDetail>();
		if (financeDetail != null) {
			if (financeDetail.getFinScheduleData().getFinFeeDetailList() != null) {
				for (FinFeeDetail feeDetail : financeDetail.getFinScheduleData().getFinFeeDetailList()) {
					if (finSerEvent.equalsIgnoreCase(feeDetail.getFinEvent())) {
						if (feeDetail.isOriginationFee()) {
							feeDetail.setOriginationFee(true);
							feeDetail.setRcdVisible(false);
							feeDetail.setRecordType(PennantConstants.RCD_UPD);
							feeDetail.setRecordStatus(PennantConstants.RECORD_TYPE_UPD);
							newList.add(feeDetail);
						}
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

	/**
	 * Sort Schedule Details
	 * 
	 * @param financeScheduleDetail
	 * @return
	 */
	public List<FinanceScheduleDetail> sortSchdDetails(List<FinanceScheduleDetail> financeScheduleDetail) {

		if (financeScheduleDetail != null && financeScheduleDetail.size() > 0) {
			Collections.sort(financeScheduleDetail, new Comparator<FinanceScheduleDetail>() {
				@Override
				public int compare(FinanceScheduleDetail detail1, FinanceScheduleDetail detail2) {
					return DateUtility.compare(detail1.getSchDate(), detail2.getSchDate());
				}
			});
		}

		return financeScheduleDetail;
	}

	/**
	 * get first Installment Date
	 * 
	 * @param financeScheduleDetail
	 * @return
	 */
	Date getFirstInstDate(List<FinanceScheduleDetail> financeScheduleDetail) {

		// Finding First Installment Date
		Date firstInstDate = null;
		for (FinanceScheduleDetail scheduleDetail : financeScheduleDetail) {

			BigDecimal repayAmt = scheduleDetail.getProfitSchd().add(scheduleDetail.getPrincipalSchd())
					.subtract(scheduleDetail.getPartialPaidAmt());

			// InstNumber issue with Partial Settlement before first installment
			if (repayAmt.compareTo(BigDecimal.ZERO) > 0) {
				firstInstDate = scheduleDetail.getSchDate();
				break;
			}
		}
		return firstInstDate;
	}

	public FinanceDetail doFeePayment(FinServiceInstruction finServiceInst) {
		logger.debug(Literal.ENTERING);

		FinanceDetail response = null;

		// Validate given Request Receipt Data is valid or not.
		WSReturnStatus returnStatus = validateReceiptData(finServiceInst);
		if (returnStatus != null) {
			response = new FinanceDetail();
			doEmptyResponseObject(response);
			response.setReturnStatus(returnStatus);
			return response;
		}
		//Fee validations
		List<ErrorDetail> errorDetails = upfrontFeeValidations(finServiceInst);
		if (errorDetails != null) {
			for (ErrorDetail errorDetail : errorDetails) {
				response = new FinanceDetail();
				doEmptyResponseObject(response);
				response.setReturnStatus(
						APIErrorHandlerService.getFailedStatus(errorDetail.getCode(), errorDetail.getError()));
				return response;
			}
		}

		try {
			ErrorDetail errorDetail = feeReceiptService.processFeePayment(finServiceInst);
			if (errorDetail != null) {
				response = new FinanceDetail();
				doEmptyResponseObject(response);
				response.setReturnStatus(
						APIErrorHandlerService.getFailedStatus(errorDetail.getCode(), errorDetail.getError()));
				return response;
			}
			response = new FinanceDetail();
			doEmptyResponseObject(response);
			response.setReturnStatus(APIErrorHandlerService.getSuccessStatus());
			response.setReceiptId(finServiceInst.getReceiptId());
		} catch (InterfaceException ex) {
			logger.error("InterfaceException", ex);
			response = new FinanceDetail();
			doEmptyResponseObject(response);
			APIErrorHandlerService.logUnhandledException(ex);
			response.setReturnStatus(APIErrorHandlerService.getFailedStatus("9998", ex.getMessage()));
			return response;
		} catch (AppException appEx) {
			logger.error("AppException", appEx);
			response = new FinanceDetail();
			doEmptyResponseObject(response);
			response.setReturnStatus(APIErrorHandlerService.getFailedStatus("9999", appEx.getMessage()));
			APIErrorHandlerService.logUnhandledException(appEx);
			return response;
		} catch (Exception e) {
			logger.error("Exception", e);
			APIErrorHandlerService.logUnhandledException(e);
			response = new FinanceDetail();
			doEmptyResponseObject(response);
			response.setReturnStatus(APIErrorHandlerService.getFailedStatus());
			return response;
		}

		logger.debug(Literal.LEAVING);
		return response;
	}

	private List<ErrorDetail> upfrontFeeValidations(FinServiceInstruction fsi) {
		logger.debug(Literal.ENTERING);
		List<ErrorDetail> errorDetails = new ArrayList<>();
		if (StringUtils.isBlank(fsi.getExternalReference())) {
			return errorDetails;
		}
		FinanceType finType = getFinanceTypeDAO().getFinanceTypeByFinType(fsi.getFinType());
		if (finType == null) {
			String[] valueParm = new String[1];
			valueParm[0] = "finType " + fsi.getFinType() + " is invalid";
			errorDetails.add(ErrorUtil.getErrorDetail(new ErrorDetail("30550", valueParm)));
			return errorDetails;
		}

		BigDecimal feePaidAmount = BigDecimal.ZERO;
		List<String> processedFees = new ArrayList<>(fsi.getFinFeeDetails().size());
		for (FinFeeDetail finFeeDetail : fsi.getFinFeeDetails()) {
			//In case of req contain duplicate fees.
			String feeCode = StringUtils.trimToEmpty(finFeeDetail.getFeeTypeCode());
			if (processedFees.contains(feeCode.toLowerCase())) {
				String[] valueParm = new String[1];
				valueParm[0] = "Fees : " + feeCode;
				errorDetails.add(ErrorUtil.getErrorDetail(new ErrorDetail("90273", valueParm)));
			}
			processedFees.add(feeCode.toLowerCase());
			if (StringUtils.isNotBlank(finFeeDetail.getFeeScheduleMethod())) {
				String[] valueParm = new String[2];
				valueParm[0] = "Fee Schedule Method";
				valueParm[1] = finFeeDetail.getFeeTypeCode();
				errorDetails.add(ErrorUtil.getErrorDetail(new ErrorDetail("90269", valueParm)));
			}
			if (finFeeDetail.getActualAmount() == null) {
				finFeeDetail.setActualAmount(BigDecimal.ZERO);
			}
			if (finFeeDetail.getWaivedAmount() == null) {
				finFeeDetail.setWaivedAmount(BigDecimal.ZERO);
			}
			if (finFeeDetail.getPaidAmount() == null) {
				finFeeDetail.setPaidAmount(BigDecimal.ZERO);
			}

			// validate negative values
			if (finFeeDetail.getActualAmount().compareTo(BigDecimal.ZERO) < 0
					|| finFeeDetail.getPaidAmount().compareTo(BigDecimal.ZERO) < 0
					|| finFeeDetail.getWaivedAmount().compareTo(BigDecimal.ZERO) < 0) {
				String[] valueParm = new String[1];
				valueParm[0] = finFeeDetail.getFeeTypeCode();
				errorDetails.add(ErrorUtil.getErrorDetail(new ErrorDetail("90259", valueParm)));
			}
			// validate actual amount and paid amount
			BigDecimal amount = finFeeDetail.getActualAmount().subtract(finFeeDetail.getWaivedAmount());
			if (finFeeDetail.getPaidAmount().compareTo(amount) != 0) {
				String[] valueParm = new String[1];
				valueParm[0] = finFeeDetail.getFeeTypeCode() + " Paid amount must be  " + amount;
				errorDetails.add(ErrorUtil.getErrorDetail(new ErrorDetail("30550", valueParm)));
			}

			feePaidAmount = feePaidAmount.add(finFeeDetail.getPaidAmount());
		}
		if (errorDetails.size() > 0) {
			return errorDetails;
		}

		if (fsi.getAmount().compareTo(feePaidAmount) < 0) {
			String valueParm[] = new String[2];
			valueParm[0] = "amount : " + fsi.getAmount();
			valueParm[1] = "total fees paid : " + feePaidAmount;
			errorDetails.add(ErrorUtil.getErrorDetail(new ErrorDetail("90205", valueParm)));
			return errorDetails;
		}

		boolean isOrigination = true;
		List<FinTypeFees> finTypeFeeDetail = financeDetailService.getFinTypeFees(finType.getFinType(),
				AccountEventConstants.ACCEVENT_ADDDBSP, isOrigination, FinanceConstants.MODULEID_FINTYPE);
		if (CollectionUtils.isEmpty(finTypeFeeDetail)) {
			String[] valueParm = new String[1];
			valueParm[0] = fsi.getFinType();
			errorDetails.add(ErrorUtil.getErrorDetail(new ErrorDetail("90245", valueParm)));
			return errorDetails;
		}
		fsi.setFinTypeFeeList(finTypeFeeDetail);

		List<FinFeeDetail> prvsFees = finFeeDetailService.getFinFeeDetailsByTran(fsi.getExternalReference(), false,
				TableType.MAIN_TAB.getSuffix());
		List<FinFeeDetail> feelist = new ArrayList<>();
		LoggedInUser userDetails = SessionUserDetails.getUserDetails(SessionUserDetails.getLogiedInUser());
		for (FinFeeDetail reqFee : fsi.getFinFeeDetails()) {
			boolean isFeeCodeFound = false;
			for (FinTypeFees finTypeFee : finTypeFeeDetail) {
				if (StringUtils.equals(reqFee.getFeeTypeCode(), finTypeFee.getFeeTypeCode())) {
					isFeeCodeFound = true;
					FinFeeDetail fee = getFeeByFeeType(prvsFees, finTypeFee.getFeeTypeID());
					if (fee == null) {
						//setting req data
						fee = new FinFeeDetail();
						fee.setNewRecord(true);
						fee.setFeeTypeID(finTypeFee.getFeeTypeID());
						fee.setOriginationFee(finTypeFee.isOriginationFee());
						fee.setFinEvent(finTypeFee.getFinEvent());
						fee.setFinEventDesc(finTypeFee.getFinEventDesc());
						fee.setFeeOrder(finTypeFee.getFeeOrder());
						fee.setAlwPreIncomization(finTypeFee.isAlwPreIncomization());
						fee.setFeeScheduleMethod(finTypeFee.getFeeScheduleMethod());
						fee.setCalculationType(finTypeFee.getCalculationType());
						fee.setRuleCode(finTypeFee.getRuleCode());
						fee.setFixedAmount(finTypeFee.getAmount());
						fee.setPercentage(finTypeFee.getPercentage());
						fee.setCalculateOn(finTypeFee.getCalculateOn());
						fee.setAlwDeviation(finTypeFee.isAlwDeviation());
						fee.setMaxWaiverPerc(finTypeFee.getMaxWaiverPerc());
						fee.setAlwModifyFee(finTypeFee.isAlwModifyFee());
						fee.setAlwModifyFeeSchdMthd(finTypeFee.isAlwModifyFeeSchdMthd());
						fee.setCalculatedAmount(finTypeFee.getAmount());
						fee.setTaxApplicable(finTypeFee.isTaxApplicable());
						fee.setTaxComponent(finTypeFee.getTaxComponent());
						fee.setActualAmountOriginal(reqFee.getActualAmount());
						fee.setNetAmount(reqFee.getActualAmount());
						fee.setWaivedAmount(reqFee.getWaivedAmount());
						fee.setVersion(1);
						fee.setFinReference(fsi.getExternalReference());
						fee.setLastMntOn(new Timestamp(System.currentTimeMillis()));
						fee.setLastMntBy(userDetails.getUserId());
						fee.setTransactionId(fee.getFinReference());
					} else {
						fee.setNetAmount(fee.getActualAmount().add(reqFee.getActualAmount()));
						fee.setWaivedAmount(fee.getWaivedAmount().add(reqFee.getWaivedAmount()));
						fee.setTransactionId(fee.getFinReference());
					}
					fee.setFeeTypeCode(finTypeFee.getFeeTypeCode());
					fee.setFeeTypeDesc(finTypeFee.getFeeTypeDesc());
					FinFeeReceipt finFeeReceipt = new FinFeeReceipt();
					finFeeReceipt.setFeeID(fee.getFeeID());
					finFeeReceipt.setPaidAmount(reqFee.getPaidAmount());
					//fee.setPaidAmount(BigDecimal.ZERO);
					fee.getFinFeeReceipts().add(finFeeReceipt);
					feelist.add(fee);
				}
			}
			if (!isFeeCodeFound) {
				String[] valueParm = new String[1];
				errorDetails.add(ErrorUtil.getErrorDetail(new ErrorDetail("90247", valueParm)));
				return errorDetails;
			}
		}
		if (errorDetails.size() == 0) {
			fsi.setFinFeeDetails(feelist);
		}
		logger.debug(Literal.LEAVING);
		return errorDetails;
	}

	private FinFeeDetail getFeeByFeeType(List<FinFeeDetail> prvsFees, Long feeTypeID) {
		logger.debug(Literal.ENTERING);
		for (FinFeeDetail finFeeDetail : prvsFees) {
			if (finFeeDetail.getFeeTypeID() == feeTypeID) {
				return finFeeDetail;
			}
		}
		logger.debug(Literal.LEAVING);
		return null;
	}

	private List<ErrorDetail> upfrontFeeValidations(String vldGroup, FinServiceInstruction finServInst,
			boolean isAPICall, String eventCode) {
		List<ErrorDetail> errorDetails = new ArrayList<>();
		String finEvent = eventCode;
		boolean isOrigination = false;
		int vasFeeCount = 0;
		BigDecimal feePaidAmount = BigDecimal.ZERO;
		// finType validation
		if (StringUtils.isNotBlank(finServInst.getFinReference())) {
			String loanType = financeTypeDAO.getFinTypeByReference(finServInst.getFinReference());
			if (!StringUtils.equals(finServInst.getFinType(), loanType)) {
				String[] valueParm = new String[1];
				valueParm[0] = "invalid finType";
				errorDetails.add(ErrorUtil.getErrorDetail(new ErrorDetail("30550", valueParm)));
				return errorDetails;
			}
		}

		// Payment mode validation
		if (!StringUtils.equals(finServInst.getPaymentMode(), RepayConstants.RECEIPTMODE_CASH)
				&& !StringUtils.equals(finServInst.getPaymentMode(), RepayConstants.RECEIPTMODE_CHEQUE)
				&& !StringUtils.equals(finServInst.getPaymentMode(), RepayConstants.RECEIPTMODE_DD)
				&& !StringUtils.equals(finServInst.getPaymentMode(), RepayConstants.RECEIPTMODE_CHEQUE)
				&& !StringUtils.equals(finServInst.getPaymentMode(), RepayConstants.RECEIPTMODE_NEFT)
				&& !StringUtils.equals(finServInst.getPaymentMode(), RepayConstants.RECEIPTMODE_RTGS)
				&& !StringUtils.equals(finServInst.getPaymentMode(), RepayConstants.RECEIPTMODE_IMPS)
				&& !StringUtils.equals(finServInst.getPaymentMode(), RepayConstants.RECEIPTMODE_ESCROW)) {

			String[] valueParm = new String[1];
			valueParm[0] = "Invalid Payment Mode ";
			errorDetails.add(ErrorUtil.getErrorDetail(new ErrorDetail("30550", valueParm)));
			return errorDetails;

		}

		if (finServInst.getPaymentMode().equals(RepayConstants.RECEIPTMODE_CHEQUE)
				|| finServInst.getPaymentMode().equals(RepayConstants.RECEIPTMODE_DD)) {

			if (StringUtils.isBlank(finServInst.getReceiptDetail().getFavourNumber())) {
				String[] valueParm = new String[1];
				valueParm[0] = "Please Enter ChequeNumber(favourNumber)";
				errorDetails.add(ErrorUtil.getErrorDetail(new ErrorDetail("30550", valueParm)));
				return errorDetails;
			} else {
				if (StringUtils.length(finServInst.getReceiptDetail().getFavourNumber()) > 6) {
					String[] valueParm = new String[1];
					valueParm[0] = "ChequeNumber(favourNumber) should be less than 6 didgit";
					errorDetails.add(ErrorUtil.getErrorDetail(new ErrorDetail("30550", valueParm)));
					return errorDetails;
				}
			}

			if (StringUtils.isBlank(finServInst.getReceiptDetail().getFavourName())) {
				String[] valueParm = new String[1];
				valueParm[0] = "Please Enter AccountHolderName(favourName)";
				errorDetails.add(ErrorUtil.getErrorDetail(new ErrorDetail("30550", valueParm)));
				return errorDetails;
			}
			if (StringUtils.isBlank(finServInst.getReceiptDetail().getBankCode())) {
				String[] valueParm = new String[1];
				valueParm[0] = "Please Enter BankCode";
				errorDetails.add(ErrorUtil.getErrorDetail(new ErrorDetail("30550", valueParm)));
				return errorDetails;
			} else {
				BankDetail bankDetail = bankDetailDAO.getBankDetailById(finServInst.getReceiptDetail().getBankCode(),
						"_AView");
				if (bankDetail == null) {
					String[] valueParm = new String[1];
					valueParm[0] = "BankCode: " + finServInst.getReceiptDetail().getBankCode();
					errorDetails.add(ErrorUtil.getErrorDetail(new ErrorDetail("90501", valueParm)));
					return errorDetails;
				}
			}

			if (finServInst.getReceiptDetail().getFundingAc() < 0) {
				String[] valueParm = new String[1];
				valueParm[0] = "Please Enter deposite bank(fundingAc)";
				errorDetails.add(ErrorUtil.getErrorDetail(new ErrorDetail("30550", valueParm)));
				return errorDetails;
			}

		}

		if (StringUtils.equals(finServInst.getPaymentMode(), RepayConstants.RECEIPTMODE_NEFT)
				|| StringUtils.equals(finServInst.getPaymentMode(), RepayConstants.RECEIPTMODE_RTGS)
				|| StringUtils.equals(finServInst.getPaymentMode(), RepayConstants.RECEIPTMODE_IMPS)
				|| StringUtils.equals(finServInst.getPaymentMode(), RepayConstants.RECEIPTMODE_ESCROW)) {

			if (StringUtils.isBlank(finServInst.getReceiptDetail().getTransactionRef())) {
				String[] valueParm = new String[1];
				valueParm[0] = "Please Enter Transaction Ref";
				errorDetails.add(ErrorUtil.getErrorDetail(new ErrorDetail("30550", valueParm)));
				return errorDetails;
			}
			if (finServInst.getReceiptDetail().getFundingAc() < 0) {
				String[] valueParm = new String[1];
				valueParm[0] = "Please Enter deposite bank(fundingAc)";
				errorDetails.add(ErrorUtil.getErrorDetail(new ErrorDetail("30550", valueParm)));
				return errorDetails;
			}
		}
		FinanceType finType = getFinanceTypeDAO().getFinanceTypeByFinType(finServInst.getFinType());
		List<FinFeeDetail> finFeeDetailList = new ArrayList<>();
		if (finServInst.getExternalReference() != null && !finServInst.getExternalReference().isEmpty()) {
			boolean isExtAssigned = getFinReceiptHeaderDAO().isExtRefAssigned(finServInst.getExternalReference());
			/*
			 * if (isExtAssigned) { String[] valueParm = new String[1]; valueParm[0] =
			 * " External Reference Already Assigned to Finance "; errorDetails.add(ErrorUtil.getErrorDetail(new
			 * ErrorDetail("30550", valueParm))); return errorDetails; }
			 */
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

					// FIXME MURTHY NEED to VALIDATE
					Map<String, BigDecimal> taxPercentages = GSTCalculator
							.getTaxPercentages(finServInst.getFinReference());

					for (FinFeeDetail feeDetail : finServInst.getFinFeeDetails()) {
						BigDecimal finWaiverAmount = BigDecimal.ZERO;
						BigDecimal finPaidAMount = BigDecimal.ZERO;
						BigDecimal actualAmount = BigDecimal.ZERO;
						boolean isFeeCodeFound = false;
						for (FinTypeFees finTypeFee : finTypeFeeDetail) {
							if (StringUtils.equals(feeDetail.getFeeTypeCode(), finTypeFee.getFeeTypeCode())) {
								isFeeCodeFound = true;
								finPaidAMount = feeDetail.getPaidAmount();
								actualAmount = feeDetail.getActualAmount();
								Map<String, Object> gstExecutionMap = GSTCalculator.getGSTDataMap(
										finServInst.getFromBranch(), finServInst.getToBranch(), null, null, null, null);
								if (finTypeFee.isTaxApplicable() && !gstExecutionMap.containsKey("fromState")) {
									String[] valueParm = new String[1];
									valueParm[0] = " GST not configured ";
									errorDetails.add(ErrorUtil.getErrorDetail(new ErrorDetail("30550", valueParm)));
									return errorDetails;
								}
								feeDetailService.setFinFeeDetails(finTypeFee, feeDetail, taxPercentages,
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
								if (finPaidAMount.compareTo(actualAmount) != 0) {
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
	 * Method for validate the Receipt related data.
	 * 
	 * 
	 * @param fsi
	 * @return
	 */
	private WSReturnStatus validateReceiptData(FinServiceInstruction fsi) {
		logger.debug(Literal.ENTERING);
		String finReference = fsi.getFinReference();
		String[] valueParm = null;

		// validate FinReference
		if (StringUtils.isNotBlank(finReference)) {
			boolean isValidRef = financeMainDAO.isFinReferenceExists(finReference, TableType.TEMP_TAB.getSuffix(),
					false);
			if (!isValidRef) {
				valueParm = new String[1];
				valueParm[0] = fsi.getFinReference();
				return APIErrorHandlerService.getFailedStatus("90201", valueParm);
			}
		}

		// better to check any unpaid fess us there or not

		// Valid Receipt Mode
		String receiptMode = fsi.getPaymentMode();
		if (!StringUtils.equals(receiptMode, RepayConstants.RECEIPTMODE_CASH)
				&& !StringUtils.equals(receiptMode, RepayConstants.RECEIPTMODE_CHEQUE)
				&& !StringUtils.equals(receiptMode, RepayConstants.RECEIPTMODE_DD)
				&& !StringUtils.equals(receiptMode, RepayConstants.RECEIPTMODE_NEFT)
				&& !StringUtils.equals(receiptMode, RepayConstants.RECEIPTMODE_RTGS)
				&& !StringUtils.equals(receiptMode, RepayConstants.RECEIPTMODE_IMPS)
				&& !StringUtils.equals(receiptMode, RepayConstants.RECEIPTMODE_ESCROW)
				&& !StringUtils.equals(receiptMode, RepayConstants.RECEIPTMODE_ONLINE)) {

			valueParm = new String[2];
			valueParm[0] = "Receipt mode";
			valueParm[1] = RepayConstants.RECEIPTMODE_CASH + "," + RepayConstants.RECEIPTMODE_CHEQUE + ","
					+ RepayConstants.RECEIPTMODE_DD + "," + RepayConstants.RECEIPTMODE_NEFT + ","
					+ RepayConstants.RECEIPTMODE_RTGS + "," + RepayConstants.RECEIPTMODE_IMPS + ","
					+ RepayConstants.RECEIPTMODE_ESCROW + RepayConstants.RECEIPTMODE_ONLINE;
			return APIErrorHandlerService.getFailedStatus("90281", valueParm);
		}
		FinReceiptDetail finReceiptDetail = fsi.getReceiptDetail();

		if (finReceiptDetail.getFundingAc() <= 0) {
			valueParm = new String[1];
			valueParm[0] = "fundingAccount";
			return APIErrorHandlerService.getFailedStatus("90502", valueParm);
		} else {
			PartnerBank partnerBank = partnerBankDAO.getPartnerBankById(finReceiptDetail.getFundingAc(), "");
			if (partnerBank != null) {
				fsi.getReceiptDetail().setPartnerBankAc(partnerBank.getAccountNo());
				fsi.getReceiptDetail().setPartnerBankAcType(partnerBank.getAcType());
			} else {
				valueParm = new String[2];
				valueParm[0] = "fundingAccount";
				valueParm[1] = String.valueOf(finReceiptDetail.getFundingAc());
				return APIErrorHandlerService.getFailedStatus("90224", valueParm);
			}
		}

		if (finReceiptDetail.getReceivedDate() == null) {
			valueParm = new String[1];
			valueParm[0] = "receivedDate";
			return APIErrorHandlerService.getFailedStatus("90502", valueParm);
		} else {
			Date appDate = DateUtility.getAppDate();
			if (DateUtility.compare(fsi.getReceiptDetail().getReceivedDate(), appDate) > 0) {
				valueParm = new String[1];
				valueParm[0] = DateUtility.formatToLongDate(appDate);
				return APIErrorHandlerService.getFailedStatus("RU0006", valueParm);
			}
		}

		if (StringUtils.isNotBlank(finReceiptDetail.getPaymentRef())) {
			Pattern pattern = Pattern.compile(
					PennantRegularExpressions.getRegexMapper(PennantRegularExpressions.REGEX_UPP_BOX_ALPHANUM));
			Matcher matcher = pattern.matcher(finReceiptDetail.getPaymentRef());
			if (matcher.matches() == false) {
				valueParm = new String[1];
				valueParm[0] = "paymentRef";
				return APIErrorHandlerService.getFailedStatus("90347", valueParm);
			}
		}

		if (RepayConstants.RECEIPTMODE_CHEQUE.equals(receiptMode)
				|| RepayConstants.RECEIPTMODE_DD.equals(receiptMode)) {

			if (StringUtils.isBlank(finReceiptDetail.getFavourName())) {
				valueParm = new String[1];
				valueParm[0] = "favourName";
				return APIErrorHandlerService.getFailedStatus("90502", valueParm);
			}

			// CHEQUE / DD number
			if (StringUtils.isBlank(finReceiptDetail.getFavourNumber())) {
				valueParm = new String[1];
				valueParm[0] = "favourNumber";
				return APIErrorHandlerService.getFailedStatus("90502", valueParm);
			}
			if (!StringUtils.isNumeric(finReceiptDetail.getFavourNumber())) {
				valueParm = new String[1];
				valueParm[0] = "favourNumber";
				return APIErrorHandlerService.getFailedStatus("90242", valueParm);
			}
			if (finReceiptDetail.getFavourNumber().length() != 6) {
				valueParm = new String[2];
				valueParm[0] = "favourNumber size";
				valueParm[1] = "six";
				return APIErrorHandlerService.getFailedStatus("90277", valueParm);
			}
			// Cheque Acc No {0} is lessthan or equals to {1} .
			if (StringUtils.length(finReceiptDetail.getChequeAcNo()) > 50) {
				valueParm = new String[2];
				valueParm[0] = "chequeAcNo";
				valueParm[1] = "50";
				return APIErrorHandlerService.getFailedStatus("90220", valueParm);
			}
			// value Date
			Date appDate = DateUtility.getAppDate();
			if (finReceiptDetail.getValueDate() == null) {
				valueParm = new String[1];
				valueParm[0] = "valueDate";
				return APIErrorHandlerService.getFailedStatus("90502", valueParm);
			} else {
				if (DateUtility.compare(finReceiptDetail.getValueDate(), appDate) > 0) {
					valueParm = new String[1];
					valueParm[0] = DateUtility.formatToLongDate(appDate);
					return APIErrorHandlerService.getFailedStatus("RU0007", valueParm);
				}
			}
			if (fsi.isNonStp()) {
				if (fsi.getRealizationDate() == null) {
					valueParm = new String[1];
					valueParm[0] = "realizationDate";
					return APIErrorHandlerService.getFailedStatus("90502", valueParm);
				} else {
					if (DateUtility.compare(fsi.getRealizationDate(), finReceiptDetail.getValueDate()) < 0) {
						valueParm = new String[1];
						valueParm[0] = DateUtility.formatToLongDate(finReceiptDetail.getValueDate());
						return APIErrorHandlerService.getFailedStatus("RU0019", valueParm);
					}
					if (DateUtility.compare(fsi.getRealizationDate(), appDate) > 0) {
						valueParm = new String[2];
						valueParm[0] = "realizationDate";
						valueParm[1] = DateUtility.formatToLongDate(appDate);
						return APIErrorHandlerService.getFailedStatus("30568", valueParm);
					}
				}
			}

			if (StringUtils.isBlank(finReceiptDetail.getBankCode())) {
				valueParm = new String[1];
				valueParm[0] = "bankCode";
				return APIErrorHandlerService.getFailedStatus("90502", valueParm);
			} else {
				// Bank Details should be configured
				BankDetail bankDetail = bankDetailService.getBankDetailById(finReceiptDetail.getBankCode());
				if (bankDetail == null) {
					valueParm = new String[2];
					valueParm[0] = "bankCode";
					valueParm[1] = finReceiptDetail.getBankCode();
					return APIErrorHandlerService.getFailedStatus("90224", valueParm);
				}
			}
			//IFSC code (bank branch) validation
			if (!StringUtils.isBlank(finReceiptDetail.getBankCode())
					&& !StringUtils.isBlank(finReceiptDetail.getiFSC())) {
				BankBranch bankBranch = null;
				bankBranch = bankBranchService.getBankBrachByIFSC(finReceiptDetail.getiFSC());
				if (bankBranch == null) {
					valueParm = new String[2];
					valueParm[0] = "ifsc";
					valueParm[1] = finReceiptDetail.getiFSC();
					return APIErrorHandlerService.getFailedStatus("90224", valueParm);
				} else if (!StringUtils.equals(finReceiptDetail.getBankCode(), bankBranch.getBankCode())) {
					valueParm = new String[1];
					valueParm[0] = finReceiptDetail.getiFSC();
					return APIErrorHandlerService.getFailedStatus("99020", valueParm);
				}
				//setting the bank branch ID for cheque and DD
				finReceiptDetail.setBankBranchID(bankBranch.getBankBranchID());
			}
		} else {
			// need to empty the data wich is not req
			fsi.setRealizationDate(null);
			finReceiptDetail.setFavourName("");
			finReceiptDetail.setFavourNumber("");
			finReceiptDetail.setChequeAcNo("");
		}
		// In Case of online mode transactionRef is mandatory
		if (StringUtils.equals(receiptMode, RepayConstants.RECEIPTMODE_NEFT)
				|| StringUtils.equals(receiptMode, RepayConstants.RECEIPTMODE_RTGS)
				|| StringUtils.equals(receiptMode, RepayConstants.RECEIPTMODE_IMPS)
				|| StringUtils.equals(receiptMode, RepayConstants.RECEIPTMODE_ESCROW)) {
			if (StringUtils.isBlank(finReceiptDetail.getTransactionRef())) {
				valueParm = new String[1];
				valueParm[0] = "transactionRef";
				return APIErrorHandlerService.getFailedStatus("90502", valueParm);
			}
			Pattern pattern = Pattern.compile(
					PennantRegularExpressions.getRegexMapper(PennantRegularExpressions.REGEX_UPP_BOX_ALPHANUM));
			Matcher matcher = pattern.matcher(finReceiptDetail.getTransactionRef());
			if (matcher.matches() == false) {
				valueParm = new String[1];
				valueParm[0] = "transactionRef";
				return APIErrorHandlerService.getFailedStatus("90347", valueParm);
			}
		}

		if (CollectionUtils.isEmpty(fsi.getFinFeeDetails())) {
			valueParm = new String[1];
			valueParm[0] = "fees";
			return APIErrorHandlerService.getFailedStatus("90502", valueParm);
		}

		// check any UpFront is in process
		String reference = finReference;
		if (StringUtils.isNotBlank(fsi.getExternalReference())) {
			reference = Objects.toString(fsi.getCustID(), "");
		}
		boolean isInProgRec = finReceiptHeaderDAO.isReceiptsInProcess(reference,
				FinanceConstants.FINSER_EVENT_FEEPAYMENT, Long.MIN_VALUE, "_Temp");
		if (isInProgRec) {
			valueParm = new String[1];
			valueParm[0] = FinanceConstants.FINSER_EVENT_FEEPAYMENT;
			return APIErrorHandlerService.getFailedStatus("IMD002", valueParm);
		}

		logger.debug(Literal.LEAVING);
		return null;
	}

	/**
	 * Method for enabling the DepositProcess<br>
	 * Based on the workflow and roleCode checks weather deposit process required or not.
	 * 
	 * @param userID
	 * @return CustomerODLoanDetails
	 */
	public List<LoanPendingData> getCustomerODLoanDetails(long userID) {
		return financeMainService.getCustomerODLoanDetails(userID);
	}

	/**
	 * Method <b>getFinanceTaxDetails(finReference)</b> - Retrieves Finance Tax Details for finReference
	 * 
	 * @param finReference
	 *            - {@link String}
	 * @return {@link WSReturnStatus}
	 */
	public FinanceTaxDetail getFinanceTaxDetails(String finReference) {

		logger.info(Literal.ENTERING);

		FinanceTaxDetail financeTaxDetail = financeTaxDetailService.getApprovedFinanceTaxDetail(finReference);

		logger.info(Literal.LEAVING);

		return financeTaxDetail;
	}

	private AuditHeader prepareAuditHeader(final FinanceTaxDetail financeTaxDetail, String tranType) {

		logger.info(Literal.ENTERING);

		AuditDetail auditDetail = new AuditDetail(tranType, 1, financeTaxDetail.getBefImage(), financeTaxDetail);
		AuditHeader auditHeader = new AuditHeader(String.valueOf(financeTaxDetail.getTaxCustId()),
				String.valueOf(financeTaxDetail.getTaxCustId()), null, null, auditDetail,
				financeTaxDetail.getUserDetails(), new HashMap<String, ArrayList<ErrorDetail>>());

		APIHeader reqHeaderDetails = (APIHeader) PhaseInterceptorChain.getCurrentMessage().getExchange()
				.get(APIHeader.API_HEADER_KEY);
		auditHeader.setApiHeader(reqHeaderDetails);

		logger.info(Literal.LEAVING);

		return auditHeader;
	}

	/**
	 * Method <b>saveGSTDetails(FinanceTaxDetail)</b> - Saves GST Details for a finReference
	 * 
	 * @param financeTaxDetail
	 *            - {@link FinanceTaxDetail}
	 * @return {@link AuditHeader}
	 */
	public WSReturnStatus saveGSTDetails(final FinanceTaxDetail financeTaxDetail) {

		WSReturnStatus returnStatus = new WSReturnStatus();

		logger.info(Literal.ENTERING);

		financeTaxDetail.setRecordType(PennantConstants.RECORD_TYPE_NEW);
		financeTaxDetail.setNewRecord(true);
		financeTaxDetail.setVersion(1);
		LoggedInUser userDetails = SessionUserDetails.getUserDetails(SessionUserDetails.getLogiedInUser());
		financeTaxDetail.setUserDetails(userDetails);
		financeTaxDetail.setLastMntBy(userDetails.getUserId());
		financeTaxDetail.setLastMntOn(new Timestamp(System.currentTimeMillis()));
		financeTaxDetail.setRecordStatus(PennantConstants.RCD_STATUS_APPROVED);
		financeTaxDetail.setSourceId(PennantConstants.FINSOURCE_ID_API);

		AuditHeader auditHeader = prepareAuditHeader(financeTaxDetail, PennantConstants.TRAN_WF);
		AuditHeader savedGSTDetails = financeTaxDetailService.doApprove(auditHeader);

		if (savedGSTDetails.getAuditError() != null) {
			for (ErrorDetail errorDetail : savedGSTDetails.getErrorMessage()) {
				returnStatus = APIErrorHandlerService.getFailedStatus(errorDetail.getCode(), errorDetail.getError());
			}
		} else
			returnStatus = APIErrorHandlerService.getSuccessStatus();

		logger.info(Literal.LEAVING);

		return returnStatus;
	}

	/**
	 * Method <b>rejuvenateGSTDetails(FinanceTaxDetail)</b> - updates GST Details for a finReference
	 * 
	 * @param financeTaxDetail
	 *            - {@link FinanceTaxDetail}
	 * @param i
	 * @return {@link AuditHeader}
	 */
	public WSReturnStatus rejuvenateGSTDetails(FinanceTaxDetail financeTaxDetail, int version) {

		WSReturnStatus returnStatus = new WSReturnStatus();

		logger.info(Literal.ENTERING);

		financeTaxDetail.setRecordType(PennantConstants.RECORD_TYPE_UPD);
		financeTaxDetail.setNewRecord(false);
		financeTaxDetail.setVersion(version + 1);

		LoggedInUser userDetails = SessionUserDetails.getUserDetails(SessionUserDetails.getLogiedInUser());
		financeTaxDetail.setUserDetails(userDetails);
		financeTaxDetail.setLastMntBy(userDetails.getLoginLogId());
		financeTaxDetail.setLastMntOn(new Timestamp(System.currentTimeMillis()));
		financeTaxDetail.setRecordStatus(PennantConstants.RCD_STATUS_APPROVED);
		financeTaxDetail.setSourceId(PennantConstants.FINSOURCE_ID_API);

		AuditHeader auditHeader = prepareAuditHeader(financeTaxDetail, PennantConstants.TRAN_WF);
		AuditHeader revisedGSTDetails = financeTaxDetailService.doApprove(auditHeader);

		if (revisedGSTDetails.getAuditError() != null) {
			for (ErrorDetail errorDetail : revisedGSTDetails.getErrorMessage()) {
				returnStatus = APIErrorHandlerService.getFailedStatus(errorDetail.getCode(), errorDetail.getError());
			}
		} else
			returnStatus = APIErrorHandlerService.getSuccessStatus();

		logger.info(Literal.LEAVING);

		return returnStatus;
	}

	public WSReturnStatus approveDisbursementResponse(DisbRequest disbRequest) {
		logger.info(Literal.ENTERING);

		FinAdvancePayments finAdvancePayments = new FinAdvancePayments();
		finAdvancePayments.setPaymentId(disbRequest.getPaymentId());
		int count = finAdvancePaymentsService.getCountByPaymentId(disbRequest.getFinReference(),
				disbRequest.getPaymentId());
		if (count <= 0) {
			String[] valueParam = new String[2];
			valueParam[0] = "PaymentId";
			return APIErrorHandlerService.getFailedStatus("90405", valueParam);
		} else {
			FinAdvancePayments finAdv = finAdvancePaymentsService.getFinAdvancePaymentsById(finAdvancePayments, "");
			if (finAdv == null) {
				String[] valueParam = new String[2];
				valueParam[0] = "PaymentId";
				return APIErrorHandlerService.getFailedStatus("90405", valueParam);
			} else {
				if (StringUtils.equals(finAdv.getStatus(), DisbursementConstants.STATUS_AWAITCON)) {
					if (DisbursementConstants.STATUS_REJECTED.equals(finAdv.getStatus())
							|| DisbursementConstants.STATUS_PAID.equals(finAdv.getStatus())) {
						String[] valueParam = new String[2];
						valueParam[0] = "PaymentId";
						return APIErrorHandlerService.getFailedStatus("90405", valueParam);
					}

					if (SysParamUtil.isAllowed(SMTParameterConstants.HOLD_DISB_INST_POST)) {
						FinanceMain finMain = financeMainDAO.getFinanceMainById(disbRequest.getFinReference(), "",
								false);
						finAdv.setStatus("AC");
						finMain.setLovDescEntityCode(
								financeMainDAO.getLovDescEntityCode(finMain.getFinReference(), "_View"));
						FinanceDetail financeDetail = new FinanceDetail();
						List<FinAdvancePayments> finAdvList = new ArrayList<FinAdvancePayments>();

						finAdvList.add(finAdv);
						financeDetail.setAdvancePaymentsList(finAdvList);

						Map<Integer, Long> finAdvanceMap = disbursementPostings.prepareDisbPostingApproval(
								financeDetail.getAdvancePaymentsList(), finMain, finMain.getFinBranch());

						List<FinAdvancePayments> advPayList = financeDetail.getAdvancePaymentsList();

						// loop through the disbursements.
						if (CollectionUtils.isNotEmpty(advPayList)) {
							for (int i = 0; i < advPayList.size(); i++) {
								FinAdvancePayments advPayment = advPayList.get(i);
								if (finAdvanceMap.containsKey(advPayment.getPaymentSeq())) {
									advPayment.setLinkedTranId(finAdvanceMap.get(advPayment.getPaymentSeq()));
									finAdvancePaymensDAO.updateLinkedTranId(advPayment);
								}
							}
						}
					}

					finAdvancePayments.setPaymentId(disbRequest.getPaymentId());
					finAdvancePayments.setStatus(disbRequest.getStatus());
					finAdvancePayments.setFinReference(disbRequest.getFinReference());
					finAdvancePayments.setClearingDate(disbRequest.getClearingDate());
					if (DisbursementConstants.PAYMENT_TYPE_CHEQUE.equals(disbRequest.getDisbType())
							|| DisbursementConstants.PAYMENT_TYPE_DD.equals(disbRequest.getDisbType())) {
						finAdvancePayments.setClearingDate(disbRequest.getDisbDate());
						finAdvancePayments.setLLReferenceNo(disbRequest.getChequeNo());
					}
					finAdvancePayments.setRejectReason(disbRequest.getRejectReason());
					finAdvancePayments.setTransactionRef(disbRequest.getTransactionRef());

					if (StringUtils.equals("R", disbRequest.getStatus())
							&& !PennantConstants.YES.equalsIgnoreCase(SMTParameterConstants.HOLD_DISB_INST_POST)) {
						postingsPreparationUtil.postReversalsByLinkedTranID(finAdv.getLinkedTranId());
						finAdvancePayments.setStatus(DisbursementConstants.STATUS_REJECTED);
					} else {
						finAdvancePayments.setStatus(DisbursementConstants.STATUS_PAID);
					}

					finAdvancePaymensDAO.updateDisbursmentStatus(finAdvancePayments);
				} else {
					String[] valueParam = new String[2];
					valueParam[0] = "Disbursement status already updated";
					return APIErrorHandlerService.getFailedStatus("21005", valueParam);
				}
			}
		}
		logger.info(Literal.LEAVING);
		return APIErrorHandlerService.getSuccessStatus();
	}

	public WSReturnStatus processCovenants(FinanceMain financeMain, List<Covenant> covenantsList) {
		logger.debug(Literal.ENTERING);
		String finReference = financeMain.getFinReference();
		Date appDate = SysParamUtil.getAppDate();
		List<Covenant> aCovenants = covenantsDAO.getCovenants(finReference, APIConstants.COVENANT_MODULE_NAME,
				TableType.MAIN_TAB);

		Map<Long, Covenant> covenantsMap = aCovenants.stream()
				.collect(Collectors.toMap(Covenant::getCovenantTypeId, covenant -> covenant));

		for (Covenant covenant : covenantsList) {
			Map<String, DocumentDetails> doctypeMap = new HashMap<String, DocumentDetails>();
			covenant.setKeyReference(finReference);
			covenant.setModule(APIConstants.COVENANT_MODULE_NAME);

			LoggedInUser userDetails = financeMain.getUserDetails();
			if (covenantsMap.containsKey(covenant.getCovenantTypeId())) {
				//validating Documnets
				long covenantId = covenantsMap.get(covenant.getCovenantTypeId()).getId();
				List<CovenantDocument> covenantDocuments = covenantsDAO.getCovenantDocuments(covenantId,
						TableType.MAIN_TAB);

				for (CovenantDocument covenantDocument : covenantDocuments) {
					DocumentDetails docDetails = documentDetailsDAO
							.getDocumentDetailsById(covenantDocument.getDocumentId(), "");
					doctypeMap.put(docDetails.getDoctype(), docDetails);
				}

				for (CovenantDocument covenantDocument : covenant.getCovenantDocuments()) {

					if (doctypeMap.containsKey(covenantDocument.getDoctype())) {
						DocumentDetails existingDoc = doctypeMap.get(covenantDocument.getDoctype());
						existingDoc.setLastMntOn(new Timestamp(System.currentTimeMillis()));
						existingDoc.setVersion(1);
						existingDoc.setRecordType(PennantConstants.RECORD_TYPE_UPD);
						existingDoc.setRecordStatus(PennantConstants.RCD_STATUS_APPROVED);
						existingDoc.setLastMntBy(userDetails.getUserId());
						existingDoc.setNewRecord(false);
						existingDoc.setCustId(financeMain.getCustID());
						existingDoc.setDocCategory(covenant.getCovenantType());

						covenantDocument.setRecordType(PennantConstants.RECORD_TYPE_UPD);
						covenantDocument.setNewRecord(false);
						covenantDocument.setLastMntOn(new Timestamp(System.currentTimeMillis()));
						covenantDocument.setRecordStatus(PennantConstants.RCD_STATUS_APPROVED);
						covenantDocument.setLastMntBy(userDetails.getUserId());
						covenantDocument.setDocumentDetail(existingDoc);
					} else {
						DocumentDetails documentDetails = new DocumentDetails();
						documentDetails.setDocCategory(covenant.getCovenantType());
						setDocumentProperties(financeMain, appDate, documentDetails, covenantDocument);

						covenantDocument.setRecordType(PennantConstants.RECORD_TYPE_NEW);
						covenantDocument.setDocumentDetail(documentDetails);
						covenantDocument.setRecordStatus(PennantConstants.RCD_STATUS_APPROVED);
						covenantDocument.setLastMntBy(userDetails.getUserId());
						covenantDocument.setLastMntOn(new Timestamp(System.currentTimeMillis()));
						covenantDocument.setDocumentReceivedDate(appDate);
					}
					covenantDocument.setCovenantType(covenant.getCovenantType());
					covenant.getDocumentDetails().add(covenantDocument.getDocumentDetail());
				}

				covenant.setId(covenantId);
				covenant.setRecordType(PennantConstants.RECORD_TYPE_UPD);
				covenant.setNewRecord(false);
			} else {
				for (CovenantDocument covenantDocument : covenant.getCovenantDocuments()) {
					DocumentDetails documentDetails = new DocumentDetails();
					documentDetails.setDocCategory(covenant.getCovenantType());
					setDocumentProperties(financeMain, appDate, documentDetails, covenantDocument);

					covenantDocument.setRecordType(PennantConstants.RECORD_TYPE_NEW);
					covenantDocument.setCovenantType(covenant.getCovenantType());
					covenantDocument.setRecordStatus(PennantConstants.RCD_STATUS_APPROVED);
					covenantDocument.setLastMntBy(userDetails.getUserId());
					covenantDocument.setLastMntOn(new Timestamp(System.currentTimeMillis()));
					covenantDocument.setDocumentDetail(documentDetails);

					covenant.getDocumentDetails().add(covenantDocument.getDocumentDetail());
				}

				covenant.setRecordType(PennantConstants.RECORD_TYPE_NEW);
			}

			covenant.setLastMntOn(new Timestamp(System.currentTimeMillis()));
			covenant.setVersion(1);
			covenant.setLastMntBy(userDetails.getUserId());
			covenant.setRecordStatus(PennantConstants.RCD_STATUS_APPROVED);
		}

		if (SysParamUtil.isAllowed(SMTParameterConstants.NEW_COVENANT_MODULE)
				&& !CollectionUtils.isEmpty(covenantsList)) {
			covenantsService.doApprove(covenantsList, TableType.MAIN_TAB, PennantConstants.TRAN_WF, 0);
		}

		logger.debug(Literal.LEAVING);
		return APIErrorHandlerService.getSuccessStatus();
	}

	protected AuditHeader getAuditHeader(FinanceMain financeMain, String tranType) {
		AuditDetail auditDetail = new AuditDetail(tranType, 1, null, financeMain);
		return new AuditHeader(financeMain.getFinReference(), null, null, null, auditDetail,
				financeMain.getUserDetails(), new HashMap<String, ArrayList<ErrorDetail>>());
	}

	private void setDocumentProperties(FinanceMain financeMain, Date appDate, DocumentDetails documentDetails,
			CovenantDocument covenantDocument) {
		documentDetails.setLastMntOn(new Timestamp(System.currentTimeMillis()));
		documentDetails.setVersion(1);
		documentDetails.setDocModule(FinanceConstants.MODULE_NAME);
		documentDetails.setDocName(covenantDocument.getDocName());
		documentDetails.setReferenceId(financeMain.getFinReference());
		documentDetails.setDocReceived(true);
		covenantDocument.setDocumentReceivedDate(appDate);
		documentDetails.setFinReference(financeMain.getFinReference());
		documentDetails.setCustId(financeMain.getCustID());
		documentDetails.setRecordType(PennantConstants.RECORD_TYPE_NEW);
		documentDetails.setNewRecord(true);
		documentDetails.setDoctype(covenantDocument.getDoctype());
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

	@Override
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

	public void setManualAdviseDAO(ManualAdviseDAO manualAdviseDAO) {
		this.manualAdviseDAO = manualAdviseDAO;
	}

	public FinReceiptHeaderDAO getFinReceiptHeaderDAO() {
		return finReceiptHeaderDAO;
	}

	@Autowired
	public void setFinReceiptHeaderDAO(FinReceiptHeaderDAO finReceiptHeaderDAO) {
		this.finReceiptHeaderDAO = finReceiptHeaderDAO;
	}

	public ReceiptUploadDetailDAO getReceiptUploadDetailDAO() {
		return receiptUploadDetailDAO;
	}

	@Autowired
	public void setReceiptUploadDetailDAO(ReceiptUploadDetailDAO receiptUploadDetailDAO) {
		this.receiptUploadDetailDAO = receiptUploadDetailDAO;
	}

	@Autowired
	public void setFinanceTaxDetailService(FinanceTaxDetailService financeTaxDetailService) {
		this.financeTaxDetailService = financeTaxDetailService;
	}

	@Autowired
	public void setFinanceWorkFlowService(FinanceWorkFlowService financeWorkFlowService) {
		this.financeWorkFlowService = financeWorkFlowService;
	}

	@Autowired
	public void setFinAdvancePaymensDAO(FinAdvancePaymentsDAO finAdvancePaymensDAO) {
		this.finAdvancePaymensDAO = finAdvancePaymensDAO;
	}

	public PostingsPreparationUtil getPostingsPreparationUtil() {
		return postingsPreparationUtil;
	}

	public void setPostingsPreparationUtil(PostingsPreparationUtil postingsPreparationUtil) {
		this.postingsPreparationUtil = postingsPreparationUtil;
	}

	@Autowired
	public void setChangeScheduleMethodService(ChangeScheduleMethodService changeScheduleMethodService) {
		this.changeScheduleMethodService = changeScheduleMethodService;
	}

	public void setBankDetailDAO(BankDetailDAO bankDetailDAO) {
		this.bankDetailDAO = bankDetailDAO;
	}

	public FinanceMainDAO getFinanceMainDAO() {
		return financeMainDAO;
	}

	public void setFinanceMainDAO(FinanceMainDAO financeMainDAO) {
		this.financeMainDAO = financeMainDAO;
	}

	public DisbursementPostings getDisbursementPostings() {
		return disbursementPostings;
	}

	public void setDisbursementPostings(DisbursementPostings disbursementPostings) {
		this.disbursementPostings = disbursementPostings;
	}

	public void setBankDetailService(BankDetailService bankDetailService) {
		this.bankDetailService = bankDetailService;
	}

	public void setCovenantsService(CovenantsService covenantsService) {
		this.covenantsService = covenantsService;
	}

	public void setCovenantsDAO(CovenantsDAO covenantsDAO) {
		this.covenantsDAO = covenantsDAO;
	}

	public void setDocumentDetailsDAO(DocumentDetailsDAO documentDetailsDAO) {
		this.documentDetailsDAO = documentDetailsDAO;
	}

}
