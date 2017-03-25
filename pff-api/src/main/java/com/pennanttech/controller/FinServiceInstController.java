package com.pennanttech.controller;

import java.lang.reflect.InvocationTargetException;
import java.math.BigDecimal;
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

import com.pennant.app.constants.AccountEventConstants;
import com.pennant.app.constants.CalculationConstants;
import com.pennant.app.util.APIHeader;
import com.pennant.app.util.DateUtility;
import com.pennant.app.util.OverDueRecoveryPostingsUtil;
import com.pennant.app.util.RepayCalculator;
import com.pennant.app.util.RepaymentPostingsUtil;
import com.pennant.app.util.SessionUserDetails;
import com.pennant.backend.dao.finance.FinODPenaltyRateDAO;
import com.pennant.backend.dao.finance.FinanceProfitDetailDAO;
import com.pennant.backend.dao.finance.FinanceRepayPriorityDAO;
import com.pennant.backend.dao.finance.FinanceScheduleDetailDAO;
import com.pennant.backend.dao.rmtmasters.FinanceTypeDAO;
import com.pennant.backend.financeservice.AddDisbursementService;
import com.pennant.backend.financeservice.AddRepaymentService;
import com.pennant.backend.financeservice.AddTermsService;
import com.pennant.backend.financeservice.ChangeFrequencyService;
import com.pennant.backend.financeservice.ChangeProfitService;
import com.pennant.backend.financeservice.RateChangeService;
import com.pennant.backend.financeservice.ReScheduleService;
import com.pennant.backend.financeservice.RecalculateService;
import com.pennant.backend.financeservice.RemoveTermsService;
import com.pennant.backend.model.ErrorDetails;
import com.pennant.backend.model.LoggedInUser;
import com.pennant.backend.model.WSReturnStatus;
import com.pennant.backend.model.FinRepayQueue.FinRepayQueue;
import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.model.collateral.CollateralAssignment;
import com.pennant.backend.model.configuration.VASRecording;
import com.pennant.backend.model.finance.FinAssetTypes;
import com.pennant.backend.model.finance.FinCollaterals;
import com.pennant.backend.model.finance.FinFeeDetail;
import com.pennant.backend.model.finance.FinLogEntryDetail;
import com.pennant.backend.model.finance.FinODPenaltyRate;
import com.pennant.backend.model.finance.FinRepayHeader;
import com.pennant.backend.model.finance.FinScheduleData;
import com.pennant.backend.model.finance.FinServiceInstruction;
import com.pennant.backend.model.finance.FinanceDetail;
import com.pennant.backend.model.finance.FinanceDeviations;
import com.pennant.backend.model.finance.FinanceEligibilityDetail;
import com.pennant.backend.model.finance.FinanceMain;
import com.pennant.backend.model.finance.FinanceProfitDetail;
import com.pennant.backend.model.finance.FinanceRepayPriority;
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
import com.pennant.backend.model.rmtmasters.FinanceType;
import com.pennant.backend.model.rulefactory.AEAmountCodes;
import com.pennant.backend.model.rulefactory.FeeRule;
import com.pennant.backend.model.staticparms.ExtendedFieldRender;
import com.pennant.backend.service.finance.FinanceDetailService;
import com.pennant.backend.service.finance.FinanceMainService;
import com.pennant.backend.service.finance.ManualPaymentService;
import com.pennant.backend.util.FinanceConstants;
import com.pennant.backend.util.PennantConstants;
import com.pennant.exception.PFFInterfaceException;
import com.pennanttech.util.APIConstants;
import com.pennanttech.ws.service.APIErrorHandlerService;
import com.rits.cloning.Cloner;

public class FinServiceInstController {

	private final static Logger logger = Logger.getLogger(FinServiceInstController.class);

	private FinanceDetailService 		financeDetailService;
	private RateChangeService 			rateChangeService;
	private AddRepaymentService 		addRepaymentService;
	private RecalculateService 			recalService;
	private ChangeProfitService 		changeProfitService;
	private AddDisbursementService 		addDisbursementService;
	private ChangeFrequencyService 		changeFrequencyService;
	private ReScheduleService 			reScheduleService;
	private ManualPaymentService 		manualPaymentService;
	private RepayCalculator			    repayCalculator;
	private FinanceScheduleDetailDAO 	financeScheduleDetailDAO;

	private FinanceTypeDAO 				financeTypeDAO;
	private AddTermsService 			addTermsService;
	private RemoveTermsService 			rmvTermsService;
	private FinanceMainService 			financeMainService;
	private FinODPenaltyRateDAO 		finODPenaltyRateDAO;
	private FinanceProfitDetailDAO 		profitDetailsDAO;
	private RepaymentPostingsUtil		repayPostingUtil;
	private OverDueRecoveryPostingsUtil recoveryPostingsUtil;
	private FinanceRepayPriorityDAO			financeRepayPriorityDAO;

	
	/**
	 * Method for process AddRateChange request and re-calculate schedule details
	 * 
	 * @param finServiceInstruction
	 * @return
	 * @throws PFFInterfaceException
	 * @throws JaxenException
	 */
	public FinanceDetail doAddRateChange(FinServiceInstruction finServiceInst)  {
		logger.debug("Enteing");

		// fetch finance data
		FinanceDetail financeDetail = getFinanceDetails(finServiceInst, AccountEventConstants.ACCEVENT_RATCHG);

		if (financeDetail != null) {
			FinScheduleData finScheduleData = financeDetail.getFinScheduleData();
			FinanceMain financeMain = finScheduleData.getFinanceMain();

			financeMain.setEventFromDate(finServiceInst.getFromDate());
			financeMain.setEventToDate(finServiceInst.getToDate());
			financeMain.setRecalFromDate(finServiceInst.getRecalFromDate());
			financeMain.setRecalType(finServiceInst.getRecalType());
			financeMain.setRecalSchdMethod(financeMain.getScheduleMethod());

			if (CalculationConstants.RPYCHG_TILLMDT.equals(finServiceInst.getRecalType())) {
				financeMain.setRecalToDate(financeMain.getMaturityDate());
			} else if(CalculationConstants.RPYCHG_TILLDATE.equals(finServiceInst.getRecalType())) {
				financeMain.setRecalToDate(finServiceInst.getRecalToDate());
			}

			if(StringUtils.isBlank(finServiceInst.getBaseRate())) {
				finServiceInst.setBaseRate(null);
			}
			
			if(StringUtils.isBlank(finServiceInst.getSplRate())) {
				finServiceInst.setSplRate(null);
			}
			
			financeMain.setFinSourceID(APIConstants.FINSOURCE_ID_API);

			try {
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

				if (StringUtils.equals(finServiceInst.getReqType(), APIConstants.REQTYPE_POST)) {
					// Set Version value
					int version = financeDetail.getFinScheduleData().getFinanceMain().getVersion();
					financeDetail.getFinScheduleData().getFinanceMain().setVersion(version + 1);
					finScheduleData.setSchduleGenerated(true);
					financeMain.setScheduleRegenerated(true);

					// set generated schedule details to financeDetails
					financeDetail.setFinScheduleData(finScheduleData);

					// Save the Schedule details
					AuditHeader auditHeader = getAuditHeader(financeDetail, PennantConstants.TRAN_WF);
					FinanceDetail aFinanceDetail = (FinanceDetail) auditHeader.getAuditDetail().getModelData();
					
					aFinanceDetail = prepareInstructionObject(aFinanceDetail);
					//get the header details from the request
					APIHeader reqHeaderDetails = (APIHeader) PhaseInterceptorChain.getCurrentMessage().getExchange().get(APIHeader.API_HEADER_KEY);
					//set the headerDetails to AuditHeader
					auditHeader.setApiHeader(reqHeaderDetails);
					financeDetailService.doApprove(auditHeader, finServiceInst.isWif());

					financeDetail = getServiceInstResponse(finScheduleData);
				} else {
					financeDetail = getServiceInstResponse(finScheduleData);
				}
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
	 * 
	 * @param finServiceInstruction
	 * @return
	 * @throws PFFInterfaceException
	 * @throws JaxenException
	 */
	public FinanceDetail doAddRepayment(FinServiceInstruction finServiceInst)  {
		logger.debug("Enteing");

		// fetch finance data
		FinanceDetail financeDetail = getFinanceDetails(finServiceInst, AccountEventConstants.ACCEVENT_SCDCHG);

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

				if (StringUtils.equals(finServiceInst.getReqType(), APIConstants.REQTYPE_POST)) {
					// Set Version value
					int version = financeDetail.getFinScheduleData().getFinanceMain().getVersion();
					financeDetail.getFinScheduleData().getFinanceMain().setVersion(version + 1);
					finScheduleData.setSchduleGenerated(true);

					// Save the Schedule details
					AuditHeader auditHeader = getAuditHeader(financeDetail, PennantConstants.TRAN_WF);
					FinanceDetail aFinanceDetail = (FinanceDetail) auditHeader.getAuditDetail().getModelData();
					aFinanceDetail.getFinScheduleData().getFinanceMain().setVersion(version + 1);
					
					aFinanceDetail = prepareInstructionObject(aFinanceDetail);
					
					//get the header details from the request
					APIHeader reqHeaderDetails = (APIHeader) PhaseInterceptorChain.getCurrentMessage().getExchange().get(APIHeader.API_HEADER_KEY);
					//set the headerDetails to AuditHeader
					auditHeader.setApiHeader(reqHeaderDetails);
					
					financeDetailService.doApprove(auditHeader, finServiceInst.isWif());
					
					financeDetail = getServiceInstResponse(finScheduleData);
				} else {
					financeDetail = getServiceInstResponse(finScheduleData);
				}
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
	 * @return
	 */
	public FinanceDetail doDefferment(FinServiceInstruction finServiceInst) {
		logger.debug("Enteing");

		// fetch finance data
		FinanceDetail financeDetail = getFinanceDetails(finServiceInst, AccountEventConstants.ACCEVENT_DEFRPY);

		if (financeDetail != null) {
			FinScheduleData finScheduleData = financeDetail.getFinScheduleData();
			FinanceMain financeMain = finScheduleData.getFinanceMain();

			financeMain.setEventFromDate(finServiceInst.getFromDate());
			financeMain.setEventToDate(finServiceInst.getToDate());
			financeMain.setRecalType(finServiceInst.getRecalType());
			financeMain.setRecalFromDate(finServiceInst.getRecalFromDate());
			financeMain.setRecalToDate(finServiceInst.getRecalToDate());
			financeMain.setAvailedDefRpyChange(financeMain.getAvailedDefRpyChange() + 1);

			financeMain.setFinSourceID(APIConstants.FINSOURCE_ID_API);

			try {
				// Call Schedule calculator for Rate change
				//TODO add after the Deferment service is added 
			//	finScheduleData = deffermentService.getAddDefferments(finScheduleData);

				if (finScheduleData.getErrorDetails() != null) {
					for (ErrorDetails errorDetail : finScheduleData.getErrorDetails()) {
						FinanceDetail response = new FinanceDetail();
						response.setReturnStatus(APIErrorHandlerService.getFailedStatus(errorDetail.getErrorCode(),
								errorDetail.getError()));
						return response;
					}
				}

				if (StringUtils.equals(finServiceInst.getReqType(), APIConstants.REQTYPE_POST)) {
					// Set Version value
					int version = financeDetail.getFinScheduleData().getFinanceMain().getVersion();
					financeDetail.getFinScheduleData().getFinanceMain().setVersion(version + 1);
					finScheduleData.setSchduleGenerated(true);

					// Save the Schedule details
					AuditHeader auditHeader = getAuditHeader(financeDetail, PennantConstants.TRAN_WF);
					FinanceDetail aFinanceDetail = (FinanceDetail) auditHeader.getAuditDetail().getModelData();
					aFinanceDetail.getFinScheduleData().getFinanceMain().setVersion(version + 1);
					
					aFinanceDetail = prepareInstructionObject(aFinanceDetail);
					//get the header details from the request
					APIHeader reqHeaderDetails = (APIHeader) PhaseInterceptorChain.getCurrentMessage().getExchange().get(APIHeader.API_HEADER_KEY);
					//set the headerDetails to AuditHeader
					auditHeader.setApiHeader(reqHeaderDetails);
					
					financeDetailService.doApprove(auditHeader, finServiceInst.isWif());

					financeDetail = getServiceInstResponse(finScheduleData);
				} else {
					financeDetail = getServiceInstResponse(finScheduleData);
				}
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
	 * @return
	 */
	public FinanceDetail addTerms(FinServiceInstruction finServiceInst) {
		logger.debug("Enteing");

		// fetch finance data
		FinanceDetail financeDetail = getFinanceDetails(finServiceInst, FinanceConstants.FINSER_EVENT_ADDTERM);

		if (financeDetail != null) {
			FinScheduleData finScheduleData = financeDetail.getFinScheduleData();
			FinanceMain financeMain = finScheduleData.getFinanceMain();
			financeMain.setFinSourceID(APIConstants.FINSOURCE_ID_API);

			// validate number of terms
/*			int tenor = financeMain.getNumberOfTerms()+financeMain.getGraceTerms()+finServiceInst.getTerms();
			if(tenor > finScheduleData.getFinanceType().getFinMaxTerm()) {
				FinanceDetail response = new FinanceDetail();
				String[] valueParm = new String[2];
				valueParm[0] = "Terms:"+finServiceInst.getTerms();
				valueParm[1] = "Allowed max loan terms:"+finScheduleData.getFinanceType().getFinMaxTerm();
				response.setReturnStatus(APIErrorHandlerService.getFailedStatus("30551", valueParm));
				doEmptyResponseObject(financeDetail);
				return response;
			}*/
			try {
				// Call Schedule calculator for Rate change
				finScheduleData = addTermsService.getAddTermsDetails(finScheduleData, finServiceInst);

				if (finScheduleData.getErrorDetails() != null) {
					for (ErrorDetails errorDetail : finScheduleData.getErrorDetails()) {
						FinanceDetail response = new FinanceDetail();
						response.setReturnStatus(APIErrorHandlerService.getFailedStatus(errorDetail.getErrorCode(),
								errorDetail.getError()));
						return response;
					}
				}

				if (StringUtils.equals(finServiceInst.getReqType(), APIConstants.REQTYPE_POST)) {
					// Set Version value
					int version = financeDetail.getFinScheduleData().getFinanceMain().getVersion();
					financeDetail.getFinScheduleData().getFinanceMain().setVersion(version + 1);
					finScheduleData.setSchduleGenerated(true);

					// set generated schedule to financeDetails
					financeDetail.setFinScheduleData(finScheduleData);

					// Save the Schedule details
					AuditHeader auditHeader = getAuditHeader(financeDetail, PennantConstants.TRAN_WF);
					FinanceDetail aFinanceDetail = (FinanceDetail) auditHeader.getAuditDetail().getModelData();
					aFinanceDetail.getFinScheduleData().getFinanceMain().setVersion(version + 1);

					aFinanceDetail = prepareInstructionObject(aFinanceDetail);

					//get the header details from the request
					APIHeader reqHeaderDetails = (APIHeader) PhaseInterceptorChain.getCurrentMessage().getExchange().get(APIHeader.API_HEADER_KEY);
					//set the headerDetails to AuditHeader
					auditHeader.setApiHeader(reqHeaderDetails);

					financeDetailService.doApprove(auditHeader, finServiceInst.isWif());

					financeDetail = getServiceInstResponse(finScheduleData);
				} else {
					financeDetail = getServiceInstResponse(finScheduleData);
				}
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
	 * Method for prepare fianceDetail object.<br>
	 * 	- Nullify the unnecessary data
	 * 
	 * @param aFinanceDetail
	 * @return
	 */
	private FinanceDetail prepareInstructionObject(FinanceDetail aFinanceDetail) {
		logger.debug("Entering");
		
		FinanceDetail financeDetail = new FinanceDetail();
		FinScheduleData finScheduleData = aFinanceDetail.getFinScheduleData();
		finScheduleData.getFinanceMain().setRecordType("");
		
		finScheduleData.setStepPolicyDetails(new ArrayList<FinanceStepPolicyDetail>(1));
		finScheduleData.setInsuranceList(new ArrayList<Insurance>());
		finScheduleData.setFinODPenaltyRate(null);
		finScheduleData.setFinFeeDetailList(new ArrayList<FinFeeDetail>());
		finScheduleData.setFeeRules(new ArrayList<FeeRule>());
		financeDetail.setFinScheduleData(finScheduleData);
		
		aFinanceDetail.setAdvancePaymentsList(null);
		aFinanceDetail.setFinContributorHeader(null);
		aFinanceDetail.setIndicativeTermDetail(null);
		aFinanceDetail.setPremiumDetail(null);
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
		aFinanceDetail.setVasRecordingList(new ArrayList<VASRecording>(1));
		aFinanceDetail.setCovenantTypeList(null);
		aFinanceDetail.setMandate(null);
		aFinanceDetail.setFinFlagsDetails(null);
		aFinanceDetail.setCustomerDetails(null);
		logger.debug("Leaving");
		return financeDetail;
	}

	/**
	 * Method for process Recalculate request and generate new schedule.
	 * 
	 * @param finServiceInstruction
	 * @return FinanceDetail
	 */
	public FinanceDetail doRecalculate(FinServiceInstruction finServiceInst) {
		logger.debug("Enteing");

		// fetch finance data
		FinanceDetail financeDetail = getFinanceDetails(finServiceInst, AccountEventConstants.ACCEVENT_SCDCHG);

		if (financeDetail != null) {
			FinScheduleData finScheduleData = financeDetail.getFinScheduleData();
			FinanceMain financeMain = finScheduleData.getFinanceMain();

			financeMain.setEventFromDate(finServiceInst.getFromDate());
			financeMain.setEventToDate(financeMain.getMaturityDate());
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
				financeMain.setRecalFromDate(financeMain.getMaturityDate());
				financeMain.setRecalToDate(financeMain.getMaturityDate());
				break;
			case CalculationConstants.RPYCHG_ADDRECAL:
				financeMain.setRecalFromDate(finServiceInst.getFromDate());
				financeMain.setRecalToDate(financeMain.getMaturityDate());
				financeMain.setScheduleRegenerated(true);
				break;

			default:
				break;
			}

			financeMain.setFinSourceID(APIConstants.FINSOURCE_ID_API);
			financeMain.setVersion(financeMain.getVersion() + 1);

			try {
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

				if (StringUtils.equals(finServiceInst.getReqType(), APIConstants.REQTYPE_POST)) {
					finScheduleData.setSchduleGenerated(true);

					// set generated schedule details to financeDetails
					financeDetail.setFinScheduleData(finScheduleData);

					// Save the Schedule details
					AuditHeader auditHeader = getAuditHeader(financeDetail, PennantConstants.TRAN_WF);
					FinanceDetail aFinanceDetail = (FinanceDetail) auditHeader.getAuditDetail().getModelData();

					aFinanceDetail = prepareInstructionObject(aFinanceDetail);

					//get the header details from the request
					APIHeader reqHeaderDetails = (APIHeader) PhaseInterceptorChain.getCurrentMessage().getExchange().get(APIHeader.API_HEADER_KEY);
					//set the headerDetails to AuditHeader
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

					financeDetail = getServiceInstResponse(finScheduleData);
				} else {
					financeDetail = getServiceInstResponse(finScheduleData);
				}
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
	 * @return
	 */
	public FinanceDetail doChangeProfit(FinServiceInstruction finServiceInst) {
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

				if (StringUtils.equals(finServiceInst.getReqType(), APIConstants.REQTYPE_POST)) {
					// Set Version value
					int version = financeDetail.getFinScheduleData().getFinanceMain().getVersion();
					financeDetail.getFinScheduleData().getFinanceMain().setVersion(version + 1);
					finScheduleData.setSchduleGenerated(true);

					// set generated schedule details to financeDetails
					financeDetail.setFinScheduleData(finScheduleData);
					
					// Save the Schedule details
					AuditHeader auditHeader = getAuditHeader(financeDetail, PennantConstants.TRAN_WF);
					FinanceDetail aFinanceDetail = (FinanceDetail) auditHeader.getAuditDetail().getModelData();
					aFinanceDetail.getFinScheduleData().getFinanceMain().setVersion(version + 1);
					
					aFinanceDetail = prepareInstructionObject(aFinanceDetail);
					
					//get the header details from the request
					APIHeader reqHeaderDetails = (APIHeader) PhaseInterceptorChain.getCurrentMessage().getExchange().get(APIHeader.API_HEADER_KEY);
					//set the headerDetails to AuditHeader
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

					financeDetail = getServiceInstResponse(finScheduleData);
				} else {
					financeDetail = getServiceInstResponse(finScheduleData);
				}
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
	 * @return
	 */
	public FinanceDetail doAddDisbursement(FinServiceInstruction finServiceInst) {
		logger.debug("Enteing");

		// fetch finance data
		FinanceDetail financeDetail = getFinanceDetails(finServiceInst, "");

		if (financeDetail != null) {
			FinScheduleData finScheduleData = financeDetail.getFinScheduleData();
			FinanceMain financeMain = finScheduleData.getFinanceMain();

			financeMain.setEventFromDate(finServiceInst.getFromDate());
			financeMain.setEventToDate(financeMain.getMaturityDate());
			financeMain.setAdjTerms(finServiceInst.getTerms());
			financeMain.setRecalSchdMethod(financeMain.getScheduleMethod());
			financeMain.setRecalType(finServiceInst.getRecalType());

			if(StringUtils.equals(FinanceConstants.PRODUCT_ODFACILITY,financeMain.getProductCategory())){
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
				financeMain.setRecalFromDate(finServiceInst.getFromDate());
				financeMain.setRecalToDate(financeMain.getMaturityDate());
				financeMain.setScheduleRegenerated(true);
			}
			
			BigDecimal amount = finServiceInst.getAmount();
			financeMain.setCurDisbursementAmt(amount);

			try {
				// Call Schedule calculator for add disbursement
				finScheduleData = addDisbursementService.getAddDisbDetails(finScheduleData, amount, BigDecimal.ZERO,
						false);

				if (finScheduleData.getErrorDetails() != null) {
					for (ErrorDetails errorDetail : finScheduleData.getErrorDetails()) {
						FinanceDetail response = new FinanceDetail();
						response.setReturnStatus(APIErrorHandlerService.getFailedStatus(errorDetail.getErrorCode(),
								errorDetail.getError()));
						return response;
					}
				}

				if (StringUtils.equals(finServiceInst.getReqType(), APIConstants.REQTYPE_POST)) {
					// Set Version value
					int version = financeDetail.getFinScheduleData().getFinanceMain().getVersion();
					financeDetail.getFinScheduleData().getFinanceMain().setVersion(version + 1);
					finScheduleData.setSchduleGenerated(true);
					
					// set generated schedule details to financeDetails
					financeDetail.setFinScheduleData(finScheduleData);
					
					// Save the Schedule details
					AuditHeader auditHeader = getAuditHeader(financeDetail, PennantConstants.TRAN_WF);
					FinanceDetail aFinanceDetail = (FinanceDetail) auditHeader.getAuditDetail().getModelData();
					aFinanceDetail.getFinScheduleData().getFinanceMain().setVersion(version + 1);
					
					aFinanceDetail = prepareInstructionObject(aFinanceDetail);
					
					//get the header details from the request
					APIHeader reqHeaderDetails = (APIHeader) PhaseInterceptorChain.getCurrentMessage().getExchange().get(APIHeader.API_HEADER_KEY);
					//set the headerDetails to AuditHeader
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

					financeDetail = getServiceInstResponse(finScheduleData);
				} else {
					financeDetail = getServiceInstResponse(finScheduleData);
				}
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
	 * @return
	 */
	public FinanceDetail doChangeFrequency(FinServiceInstruction finServiceInst) {
		logger.debug("Enteing");

		// fetch finance data
		FinanceDetail financeDetail = getFinanceDetails(finServiceInst, AccountEventConstants.ACCEVENT_SCDCHG);

		if (financeDetail != null) {
			FinScheduleData finScheduleData = financeDetail.getFinScheduleData();
			String repayFrq = finScheduleData.getFinanceMain().getRepayFrq();
			String frqday = String.valueOf(finServiceInst.getFrqDay());
			frqday = frqday.length() == 1?"0".concat(frqday):frqday;
			finServiceInst.setRepayFrq(StringUtils.substring(repayFrq, 0, repayFrq.length()-2).concat(frqday));

			int rpyTermsCompleted = 0;
			int adjRepayTerms = 0;
			int totRepayTerms = 0;
			boolean isFromDateFound = false;
			Date fromDate = finServiceInst.getFromDate();

			List<FinanceScheduleDetail> financeScheduleDetails = finScheduleData.getFinanceScheduleDetails();
			if (financeScheduleDetails != null) {
				for (int i = 0; i < financeScheduleDetails.size(); i++) {
					FinanceScheduleDetail curSchd = financeScheduleDetails.get(i);
					if (curSchd.isRepayOnSchDate() || 
							(curSchd.isPftOnSchDate() && curSchd.getRepayAmount().compareTo(BigDecimal.ZERO) > 0)) {
						if (fromDate.compareTo(curSchd.getSchDate()) == 0) {
							isFromDateFound = true;
						}
						
						totRepayTerms = totRepayTerms + 1;
						if(!isFromDateFound){
							if (curSchd.getSchDate().compareTo(finScheduleData.getFinanceMain().getGrcPeriodEndDate()) > 0) {
								rpyTermsCompleted = rpyTermsCompleted + 1;
							}
						}
					}
				}
				adjRepayTerms = totRepayTerms - rpyTermsCompleted;
			}
			
			finServiceInst.setAdjRpyTerms(adjRepayTerms);

			try {
				// call change frequency service
				finScheduleData = changeFrequencyService.doChangeFrequency(finScheduleData, finServiceInst);
				financeDetail.setFinScheduleData(finScheduleData);

				if (StringUtils.equals(finServiceInst.getReqType(), APIConstants.REQTYPE_POST)) {
					// Set Version value
					int version = financeDetail.getFinScheduleData().getFinanceMain().getVersion();
					financeDetail.getFinScheduleData().getFinanceMain().setVersion(version + 1);
					finScheduleData.setSchduleGenerated(true);

					// Save the Schedule details
					AuditHeader auditHeader = getAuditHeader(financeDetail, PennantConstants.TRAN_WF);
					FinanceDetail aFinanceDetail = (FinanceDetail) auditHeader.getAuditDetail().getModelData();
					aFinanceDetail.getFinScheduleData().getFinanceMain().setVersion(version + 1);

					aFinanceDetail = prepareInstructionObject(aFinanceDetail);

					//get the header details from the request
					APIHeader reqHeaderDetails = (APIHeader) PhaseInterceptorChain.getCurrentMessage().getExchange().get(APIHeader.API_HEADER_KEY);
					//set the headerDetails to AuditHeader
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
					financeDetail = getServiceInstResponse(finScheduleData);
				} else {
					financeDetail = getServiceInstResponse(finScheduleData);
				}
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
	 * @return FinanceDetail
	 */
	public FinanceDetail removeTerms(FinServiceInstruction finServiceInst) {
		logger.debug("Enteing");

		// fetch finance data
		FinanceDetail financeDetail = getFinanceDetails(finServiceInst, FinanceConstants.FINSER_EVENT_RMVTERM);

		if (financeDetail != null) {
			FinScheduleData finScheduleData = financeDetail.getFinScheduleData();

			FinanceMain financeMain = finScheduleData.getFinanceMain();
			financeMain.setEventFromDate(finServiceInst.getFromDate());
			financeMain.setRecalType(finServiceInst.getRecalType());
			financeMain.setFinSourceID(APIConstants.FINSOURCE_ID_API);

			if (StringUtils.equals(finServiceInst.getRecalType(), CalculationConstants.RPYCHG_TILLMDT)) {
				financeMain.setRecalFromDate(finServiceInst.getFromDate());
				financeMain.setRecalToDate(financeMain.getMaturityDate());
			} else if (StringUtils.equals(finServiceInst.getRecalType(), CalculationConstants.RPYCHG_ADJMDT)) {
				financeMain.setRecalFromDate(finServiceInst.getFromDate());
				financeMain.setRecalToDate(financeMain.getMaturityDate());
			}
			
			try {
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

				if (StringUtils.equals(finServiceInst.getReqType(), APIConstants.REQTYPE_POST)) {
					// Set Version value
					int version = financeDetail.getFinScheduleData().getFinanceMain().getVersion();
					financeDetail.getFinScheduleData().getFinanceMain().setVersion(version + 1);
					finScheduleData.setSchduleGenerated(true);

					// set generated schedule details to financeDetails
					financeDetail.setFinScheduleData(finScheduleData);
					
					// Save the Schedule details
					AuditHeader auditHeader = getAuditHeader(financeDetail, PennantConstants.TRAN_WF);
					FinanceDetail aFinanceDetail = (FinanceDetail) auditHeader.getAuditDetail().getModelData();
					aFinanceDetail.getFinScheduleData().getFinanceMain().setVersion(version + 1);
					
					aFinanceDetail = prepareInstructionObject(aFinanceDetail);
					
					//get the header details from the request
					APIHeader reqHeaderDetails = (APIHeader) PhaseInterceptorChain.getCurrentMessage().getExchange().get(APIHeader.API_HEADER_KEY);
					//set the headerDetails to AuditHeader
					auditHeader.setApiHeader(reqHeaderDetails);
					
					financeDetailService.doApprove(auditHeader, finServiceInst.isWif());
					
					financeDetail = getServiceInstResponse(finScheduleData);
				} else {
					financeDetail = getServiceInstResponse(finScheduleData);
				}
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
	 * @return FinanceDetail
	 */
	public FinanceDetail doReSchedule(FinServiceInstruction finServiceInst) {
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

				if (StringUtils.equals(finServiceInst.getReqType(), APIConstants.REQTYPE_POST)) {
					// Set Version value
					int version = financeDetail.getFinScheduleData().getFinanceMain().getVersion();
					financeDetail.getFinScheduleData().getFinanceMain().setVersion(version + 1);
					finScheduleData.setSchduleGenerated(true);

					// set generated schedule details to financeDetails
					financeDetail.setFinScheduleData(finScheduleData);

					// Save the Schedule details
					AuditHeader auditHeader = getAuditHeader(financeDetail, PennantConstants.TRAN_WF);
					FinanceDetail aFinanceDetail = (FinanceDetail) auditHeader.getAuditDetail().getModelData();
					aFinanceDetail.getFinScheduleData().getFinanceMain().setVersion(version + 1);

					aFinanceDetail = prepareInstructionObject(aFinanceDetail);
					
					//get the header details from the request
					APIHeader reqHeaderDetails = (APIHeader) PhaseInterceptorChain.getCurrentMessage().getExchange().get(APIHeader.API_HEADER_KEY);
					//set the headerDetails to AuditHeader
					auditHeader.setApiHeader(reqHeaderDetails);

					financeDetailService.doApprove(auditHeader, finServiceInst.isWif());

					financeDetail = getServiceInstResponse(finScheduleData);
				} else {
					financeDetail = getServiceInstResponse(finScheduleData);
				}
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
	 * @return FinanceDetail
	 */
	public FinanceDetail doEarlySettlement(FinServiceInstruction finServiceInst) {
		logger.debug("Enteing");

		// fetch finance data
		FinanceDetail financeDetail = getFinanceDetails(finServiceInst, AccountEventConstants.ACCEVENT_EARLYSTL);
		FinScheduleData finScheduleData = financeDetail.getFinScheduleData();

		//Fetch Total Repayment Amount till Maturity date for Early Settlement
		BigDecimal repayAmt = financeScheduleDetailDAO.getTotalRepayAmount(finServiceInst.getFinReference());
		finServiceInst.setModuleDefiner(FinanceConstants.FINSER_EVENT_EARLYSETTLE);
		finServiceInst.setAmount(repayAmt);
		
		if(StringUtils.equals(finServiceInst.getReqType(), APIConstants.REQTYPE_INQUIRY)) {
			finServiceInst.setModuleDefiner(FinanceConstants.FINSER_EVENT_EARLYSTLENQ);
			if(finServiceInst.getToDate() == null) {
				finServiceInst.setToDate(finScheduleData.getFinanceMain().getMaturityDate());
			}
		}

		FinanceDetail response = null;
		try {
			response = doProcessPayments(financeDetail, finServiceInst);
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
	 * @return FinanceDeail
	 */
	public FinanceDetail doPartialSettlement(FinServiceInstruction finServiceInst) {
		logger.debug("Enteing");

		// fetch finance data
		FinanceDetail financeDetail = getFinanceDetails(finServiceInst, AccountEventConstants.ACCEVENT_EARLYPAY);
		finServiceInst.setModuleDefiner(FinanceConstants.FINSER_EVENT_ADVRPY);

		FinanceDetail response = null;
		try {
			response = doProcessPayments(financeDetail, finServiceInst);
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
	 * Method for process Early settlement and partial payment requests
	 * 
	 * @param financeDetail
	 * @param finServiceInst
	 * @return
	 * @throws IllegalAccessException
	 * @throws InvocationTargetException
	 * @throws PFFInterfaceException
	 */
	@SuppressWarnings("unchecked")
	private FinanceDetail doProcessPayments(FinanceDetail aFinanceDetail, FinServiceInstruction finServiceInst) throws 
	IllegalAccessException, InvocationTargetException, PFFInterfaceException {
		logger.debug("Entering");
		
		if(finServiceInst.getFromDate() == null) {
			finServiceInst.setFromDate(DateUtility.getAppDate());
		}
		
		Cloner cloner = new Cloner();
		FinanceDetail financeDetail = cloner.deepClone(aFinanceDetail);
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
				null, valueDate, null);
		repayData.setRepayMain(repayData.getRepayMain());

		String finEvent = AccountEventConstants.ACCEVENT_EARLYSTL;
		if(StringUtils.equals(finServiceInst.getModuleDefiner(), FinanceConstants.FINSER_EVENT_ADVRPY)) {
			finEvent = AccountEventConstants.ACCEVENT_REPAY;
			if(!StringUtils.equals(finServiceInst.getRecalType(), CalculationConstants.EARLYPAY_NOEFCT)) {
				repayData = manualPaymentService.setEarlyRepayEffectOnSchedule(repayData, finServiceInst);
			}
			aFinanceDetail = financeDetail;
		}
		
		// call change frequency service
		manualPaymentService.doCalcRepayments(repayData, aFinanceDetail, finServiceInst);

		if (StringUtils.equals(finServiceInst.getReqType(), APIConstants.REQTYPE_POST)) {
			// Set Version value
			int version = financeDetail.getFinScheduleData().getFinanceMain().getVersion();
			financeDetail.getFinScheduleData().getFinanceMain().setVersion(version + 1);
			finScheduleData.setSchduleGenerated(true);
			repayData.setSourceId(APIConstants.FINSOURCE_ID_API);
			repayData.setFinReference(finScheduleData.getFinReference());

			FinRepayHeader finRepayHeader = new FinRepayHeader();
			finRepayHeader.setFinReference(repayData.getFinReference());
			finRepayHeader.setFinEvent(finEvent);
			finRepayHeader.setValueDate(finServiceInst.getFromDate());
			finRepayHeader.setRepayAmount(repayData.getRepayMain().getRepayAmountNow());
			finRepayHeader.setPriAmount(repayData.getRepayMain().getPrincipalPayNow());
			finRepayHeader.setPftAmount(repayData.getRepayMain().getProfitPayNow());
			finRepayHeader.setTotalRefund(repayData.getRepayMain().getRefundNow());
			finRepayHeader.setTotalWaiver(BigDecimal.ZERO);
			finRepayHeader.setInsRefund(repayData.getRepayMain().getInsRefund());
			finRepayHeader.setSchdRegenerated(true);

			repayData.setFinRepayHeader(finRepayHeader);

			// Save the Schedule details
			AuditHeader auditHeader = getAuditHeader(repayData, PennantConstants.TRAN_WF);
			RepayData aRepayData = (RepayData) auditHeader.getAuditDetail().getModelData();
			aRepayData.getFinanceDetail().getFinScheduleData().getFinanceMain().setVersion(version + 1);
			aRepayData.getFinanceDetail().getFinScheduleData().getFinanceMain().setRecordType("");
			
			//get the header details from the request
			APIHeader reqHeaderDetails = (APIHeader) PhaseInterceptorChain.getCurrentMessage().getExchange().get(APIHeader.API_HEADER_KEY);
			//set the headerDetails to AuditHeader
			auditHeader.setApiHeader(reqHeaderDetails);
			
			auditHeader = manualPaymentService.doApprove(auditHeader);

			if (auditHeader.getErrorMessage() != null) {
				for (ErrorDetails errorDetail : auditHeader.getErrorMessage()) {
					FinanceDetail response = new FinanceDetail();
					doEmptyResponseObject(response);
					response.setReturnStatus(APIErrorHandlerService.getFailedStatus(errorDetail.getErrorCode(),
							errorDetail.getError()));
					return response;
				}
			}
			aRepayData = (RepayData) auditHeader.getAuditDetail().getModelData();
			financeDetail = getServiceInstResponse(aRepayData.getFinanceDetail().getFinScheduleData());
		} else {
			FinScheduleData scheduleData = repayData.getFinanceDetail().getFinScheduleData();
		
			//Repayments Posting Process Execution
			//=====================================
			FinRepayHeader finRepayHeader = repayData.getFinRepayHeader();
			financeMain.setRepayAccountId(finRepayHeader.getRepayAccountId());
			boolean isRIAFinance = false;
			FinanceProfitDetail profitDetail = profitDetailsDAO.getFinPftDetailForBatch(financeMain.getFinReference());

			List<RepayScheduleDetail> repaySchdList = repayData.getRepayScheduleDetails();
			List<Object> returnList = processRepaymentPostings(financeMain, scheduleData.getFinanceScheduleDetails(),
					profitDetail, repaySchdList, finRepayHeader.getInsRefund(), isRIAFinance, repayData.getEventCodeRef(),
					scheduleData.getFeeRules(), scheduleData.getFinanceType().getFinDivision());

			if (!(Boolean) returnList.get(0)) {
				String errParm = (String) returnList.get(1);
				throw new PFFInterfaceException("9999", errParm);
			}

			List<FinRepayQueue> finRepayQueues = new ArrayList<FinRepayQueue>();
			long linkedTranId = (Long) returnList.get(1);
			boolean partialPay = (Boolean) returnList.get(2);
			AEAmountCodes aeAmountCodes = (AEAmountCodes) returnList.get(3);
			finRepayQueues = (List<FinRepayQueue>) returnList.get(5);

			financeMain.setRecordType("");

			//Create log entry for Action for Schedule Modification
			FinLogEntryDetail entryDetail = new FinLogEntryDetail();
			entryDetail.setFinReference(financeMain.getFinReference());
			entryDetail.setEventAction(finRepayHeader.getFinEvent());
			entryDetail.setSchdlRecal(finRepayHeader.isSchdRegenerated());
			entryDetail.setPostDate(DateUtility.getAppDate());
			entryDetail.setReversalCompleted(false);

			//Repayment Postings Details Process
			returnList = repayPostingUtil.UpdateScreenPaymentsProcess(financeMain,
					scheduleData.getFinanceScheduleDetails(), profitDetail, finRepayQueues, linkedTranId, partialPay,
					isRIAFinance, aeAmountCodes);

			// ScheduleDetails delete and save
			//=======================================
			scheduleData.setFinanceScheduleDetails((List<FinanceScheduleDetail>) returnList.get(4));
			financeDetail = getServiceInstResponse(scheduleData);
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

		/*AuditHeader auditHeader = getAuditHeader(financeMain, PennantConstants.TRAN_WF);
		//get the header details from the request
		APIHeader reqHeaderDetails = (APIHeader) PhaseInterceptorChain.getCurrentMessage().getExchange().get(APIHeader.API_HEADER_KEY);
		//set the headerDetails to AuditHeader
		auditHeader.setApiHeader(reqHeaderDetails);*/
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
		FinanceSummary summaryDetail = new FinanceSummary();
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

		logger.debug("Entering");
		return response;
	}

	/**
	 * Get Audit Header Details
	 */
	protected AuditHeader getAuditHeader(FinanceMain afinanceMain, String tranType) {
		AuditDetail auditDetail = new AuditDetail(tranType, 1, afinanceMain.getBefImage(), afinanceMain);
		return new AuditHeader(afinanceMain.getFinReference(), null, null, null, auditDetail,
				afinanceMain.getUserDetails(), new HashMap<String, ArrayList<ErrorDetails>>());
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
	 * Get Audit Header Details
	 */
	private AuditHeader getAuditHeader(RepayData repayData, String tranType) {
		AuditDetail auditDetail = new AuditDetail(tranType, 1, null, repayData);
		return new AuditHeader(repayData.getFinReference(), null, null, null, auditDetail, repayData.getFinanceDetail()
				.getFinScheduleData().getFinanceMain().getUserDetails(), new HashMap<String, ArrayList<ErrorDetails>>());
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
			FinanceType financeType = financeTypeDAO.getFinanceTypeByFinType(financeDetail.getFinScheduleData()
					.getFinanceMain().getFinType());

			financeDetail.getFinScheduleData().setFinanceType(financeType);
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
		detail.setFinanceCollaterals(null);
	}
	
	/**
	 * Method for Repayment Details Posting Process
	 * 
	 * @param financeMain
	 * @param scheduleDetails
	 * @param repaySchdList
	 * @param insRefund
	 * @return
	 * @throws IllegalAccessException
	 * @throws AccountNotFoundException
	 * @throws InvocationTargetException
	 */
	public List<Object> processRepaymentPostings(FinanceMain financeMain, List<FinanceScheduleDetail> scheduleDetails,
			FinanceProfitDetail profitDetail, List<RepayScheduleDetail> repaySchdList, BigDecimal insRefund,
			boolean isRIAFinance, String eventCodeRef, List<FeeRule> feeRuleList, String finDivision)
			throws IllegalAccessException, PFFInterfaceException, InvocationTargetException {
		logger.debug("Entering");

		List<Object> returnList = new ArrayList<Object>();
		try {

			Map<String, FeeRule> feeRuleDetailsMap = null;
			if (feeRuleList != null && !feeRuleList.isEmpty()) {

				feeRuleDetailsMap = new HashMap<String, FeeRule>();
				for (FeeRule feeRule : feeRuleList) {
					if (!feeRuleDetailsMap.containsKey(feeRule.getFeeCode())) {
						feeRuleDetailsMap.put(feeRule.getFeeCode(), feeRule);
					}
				}
			}

			// FETCH Finance type Repayment Priority
			FinanceRepayPriority repayPriority = financeRepayPriorityDAO.getFinanceRepayPriorityById(
					financeMain.getFinType(), "");

			//Check Finance is RIA Finance Type or Not
			BigDecimal totRpyPri = BigDecimal.ZERO;
			BigDecimal totRpyPft = BigDecimal.ZERO;
			BigDecimal totRefund = BigDecimal.ZERO;
			BigDecimal totSchdFee = BigDecimal.ZERO;
			BigDecimal totSchdTakaful = BigDecimal.ZERO;
			BigDecimal totSchdPptTakaful = BigDecimal.ZERO;
			BigDecimal totSchdCrIns = BigDecimal.ZERO;
			BigDecimal totSchdSuplRent = BigDecimal.ZERO;
			BigDecimal totSchdIncrCost = BigDecimal.ZERO;

			List<FinRepayQueue> finRepayQueues = new ArrayList<FinRepayQueue>();
			Map<String, BigDecimal> totalsMap = new HashMap<String, BigDecimal>();
			FinRepayQueue finRepayQueue = null;
			Date curBDay = DateUtility.getAppDate();

			for (int i = 0; i < repaySchdList.size(); i++) {

				finRepayQueue = new FinRepayQueue();
				finRepayQueue.setFinReference(financeMain.getFinReference());
				finRepayQueue.setRpyDate(repaySchdList.get(i).getSchDate());
				finRepayQueue.setFinRpyFor(repaySchdList.get(i).getSchdFor());
				finRepayQueue.setRcdNotExist(true);
				finRepayQueue = doWriteDataToBean(finRepayQueue, financeMain, repaySchdList.get(i), repayPriority);

				//Overdue Details preparation
				recoveryPostingsUtil.recoveryCalculation(finRepayQueue, financeMain.getProfitDaysBasis(), curBDay,
						false, false);

				finRepayQueue.setRefundAmount(repaySchdList.get(i).getRefundReq());
				finRepayQueue.setPenaltyPayNow(repaySchdList.get(i).getPenaltyPayNow());
				finRepayQueue.setWaivedAmount(repaySchdList.get(i).getWaivedAmt());
				finRepayQueue.setPenaltyBal(repaySchdList.get(i).getPenaltyAmt()
						.subtract(repaySchdList.get(i).getPenaltyPayNow()));
				finRepayQueue.setChargeType(repaySchdList.get(i).getChargeType());

				//Total Repayments Calculation for Principal, Profit & Refunds
				totRpyPri = totRpyPri.add(repaySchdList.get(i).getPrincipalSchdPayNow());
				totRpyPft = totRpyPft.add(repaySchdList.get(i).getProfitSchdPayNow());
				totRefund = totRefund.add(repaySchdList.get(i).getRefundReq());

				//Fee Details
				totSchdFee = totSchdFee.add(repaySchdList.get(i).getSchdFeePayNow());
				totSchdSuplRent = totSchdSuplRent.add(repaySchdList.get(i).getSchdSuplRentPayNow());
				totSchdIncrCost = totSchdIncrCost.add(repaySchdList.get(i).getSchdIncrCostPayNow());

				finRepayQueues.add(finRepayQueue);

			}

			totalsMap.put("totRpyTot", totRpyPri.add(totRpyPft));
			totalsMap.put("totRpyPri", totRpyPri);
			totalsMap.put("totRpyPft", totRpyPft);
			totalsMap.put("totRefund", totRefund);
			//Schedule Early Settlement Insurance Refund
			totalsMap.put("INSREFUND", insRefund);

			//Fee Details
			totalsMap.put("takafulPay", totSchdTakaful);
			totalsMap.put("pptTakafulPay", totSchdPptTakaful);
			totalsMap.put("crInsPay", totSchdCrIns);
			totalsMap.put("schFeePay", totSchdFee);
			totalsMap.put("suplRentPay", totSchdSuplRent);
			totalsMap.put("incrCostPay", totSchdIncrCost);

			//Repayments Process For Schedule Repay List			
			returnList = repayPostingUtil.postingsScreenRepayProcess(financeMain, scheduleDetails, profitDetail,
					finRepayQueues, totalsMap, isRIAFinance, eventCodeRef, feeRuleDetailsMap, finDivision);

			if ((Boolean) returnList.get(0)) {
				returnList.add(finRepayQueues);
			}

		} catch (PFFInterfaceException e) {
			logger.error("Exception: ", e);
			throw e;
		} catch (IllegalAccessException e) {
			logger.error("Exception: ", e);
			throw e;
		} catch (InvocationTargetException e) {
			logger.error("Exception: ", e);
			throw e;
		}

		logger.debug("Leaving");
		return returnList;
	}
	
	/**
	 * Method for prepare RepayQueue data
	 * 
	 * @param resultSet
	 * @return
	 */
	private FinRepayQueue doWriteDataToBean(FinRepayQueue finRepayQueue, FinanceMain financeMain,
			RepayScheduleDetail rsd, FinanceRepayPriority repayPriority) {
		logger.debug("Entering");

		finRepayQueue.setBranch(financeMain.getFinBranch());
		finRepayQueue.setFinType(financeMain.getFinType());
		finRepayQueue.setCustomerID(financeMain.getCustID());

		if (repayPriority != null) {
			finRepayQueue.setFinPriority(repayPriority.getFinPriority());
		} else {
			finRepayQueue.setFinPriority(9999);
		}

		finRepayQueue.setSchdPft(rsd.getProfitSchd());
		finRepayQueue.setSchdPri(rsd.getPrincipalSchd());
		finRepayQueue.setSchdPftBal(rsd.getProfitSchd().subtract(rsd.getProfitSchdPaid()));
		finRepayQueue.setSchdPriBal(rsd.getPrincipalSchd().subtract(rsd.getPrincipalSchdPaid()));
		finRepayQueue.setSchdPriPayNow(rsd.getPrincipalSchdPayNow());
		finRepayQueue.setSchdPftPayNow(rsd.getProfitSchdPayNow());
		finRepayQueue.setSchdPriPaid(rsd.getPrincipalSchdPaid());
		finRepayQueue.setSchdPftPaid(rsd.getProfitSchdPaid());

		// Fee Details
		//	1. Schedule Fee Amount
		finRepayQueue.setSchdFee(rsd.getSchdFee());
		finRepayQueue.setSchdFeeBal(rsd.getSchdFeeBal());
		finRepayQueue.setSchdFeePayNow(rsd.getSchdFeePayNow());
		finRepayQueue.setSchdFeePaid(rsd.getSchdFeePaid());

		//	5. Schedule Supplementary Rent Amount
		finRepayQueue.setSchdSuplRent(rsd.getSchdSuplRent());
		finRepayQueue.setSchdSuplRentBal(rsd.getSchdSuplRentBal());
		finRepayQueue.setSchdSuplRentPayNow(rsd.getSchdSuplRentPayNow());
		finRepayQueue.setSchdSuplRentPaid(rsd.getSchdSuplRentPaid());

		//	6. Schedule Fee Amount
		finRepayQueue.setSchdIncrCost(rsd.getSchdIncrCost());
		finRepayQueue.setSchdIncrCostBal(rsd.getSchdIncrCostBal());
		finRepayQueue.setSchdIncrCostPayNow(rsd.getSchdIncrCostPayNow());
		finRepayQueue.setSchdIncrCostPaid(rsd.getSchdIncrCostPaid());

		logger.debug("Leaving");
		return finRepayQueue;
	}
	
	public void setFinanceTypeDAO(FinanceTypeDAO financeTypeDAO) {
		this.financeTypeDAO = financeTypeDAO;
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
	
	public void setManualPaymentService(ManualPaymentService manualPaymentService) {
		this.manualPaymentService = manualPaymentService;
	}
	
	public void setRepayCalculator(RepayCalculator repayCalculator) {
		this.repayCalculator = repayCalculator;
	}
	
	public void setProfitDetailsDAO(FinanceProfitDetailDAO profitDetailsDAO) {
		this.profitDetailsDAO = profitDetailsDAO;
	}
	
	public void setRepayPostingUtil(RepaymentPostingsUtil repayPostingUtil) {
		this.repayPostingUtil = repayPostingUtil;
	}
	
	public void setRecoveryPostingsUtil(OverDueRecoveryPostingsUtil recoveryPostingsUtil) {
	    this.recoveryPostingsUtil = recoveryPostingsUtil;
    }
	
	public void setFinanceRepayPriorityDAO(FinanceRepayPriorityDAO financeRepayPriorityDAO) {
		this.financeRepayPriorityDAO = financeRepayPriorityDAO;
	}
}
