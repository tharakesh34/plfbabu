package com.pennanttech.controller;

import java.lang.reflect.InvocationTargetException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.cxf.phase.PhaseInterceptorChain;
import org.apache.log4j.Logger;
import org.jaxen.JaxenException;

import com.pennant.app.constants.AccountEventConstants;
import com.pennant.app.util.APIHeader;
import com.pennant.app.util.DateUtility;
import com.pennant.app.util.FeeScheduleCalculator;
import com.pennant.app.util.ReferenceGenerator;
import com.pennant.app.util.ReferenceUtil;
import com.pennant.app.util.ScheduleCalculator;
import com.pennant.app.util.ScheduleGenerator;
import com.pennant.app.util.SessionUserDetails;
import com.pennant.backend.dao.finance.FinanceMainDAO;
import com.pennant.backend.dao.finance.FinanceScheduleDetailDAO;
import com.pennant.backend.dao.rmtmasters.FinanceTypeDAO;
import com.pennant.backend.dao.rulefactory.FinFeeChargesDAO;
import com.pennant.backend.dao.solutionfactory.StepPolicyDetailDAO;
import com.pennant.backend.dao.solutionfactory.StepPolicyHeaderDAO;
import com.pennant.backend.model.ErrorDetails;
import com.pennant.backend.model.LoggedInUser;
import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.model.configuration.VASRecording;
import com.pennant.backend.model.customermasters.CustomerDetails;
import com.pennant.backend.model.finance.FinFeeDetail;
import com.pennant.backend.model.finance.FinODDetails;
import com.pennant.backend.model.finance.FinScheduleData;
import com.pennant.backend.model.finance.FinanceDetail;
import com.pennant.backend.model.finance.FinanceDisbursement;
import com.pennant.backend.model.finance.FinanceMain;
import com.pennant.backend.model.finance.FinanceScheduleDetail;
import com.pennant.backend.model.finance.FinanceStepPolicyDetail;
import com.pennant.backend.model.solutionfactory.StepPolicyDetail;
import com.pennant.backend.model.solutionfactory.StepPolicyHeader;
import com.pennant.backend.model.staticparms.ExtendedField;
import com.pennant.backend.model.staticparms.ExtendedFieldData;
import com.pennant.backend.model.staticparms.ExtendedFieldRender;
import com.pennant.backend.service.customermasters.CustomerDetailsService;
import com.pennant.backend.service.fees.FeeDetailService;
import com.pennant.backend.service.finance.FinFeeDetailService;
import com.pennant.backend.service.finance.FinanceDetailService;
import com.pennant.backend.service.rulefactory.RuleService;
import com.pennant.backend.util.FinanceConstants;
import com.pennant.backend.util.PennantApplicationUtil;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.VASConsatnts;
import com.pennanttech.util.APIConstants;
import com.pennanttech.ws.service.APIErrorHandlerService;

public class FinanceDetailController extends SummaryDetailService {

	private static final Logger logger = Logger.getLogger(FinanceDetailController.class);

	private FinanceDetailService financeDetailService;
	private StepPolicyDetailDAO stepPolicyDetailDAO;
	private StepPolicyHeaderDAO stepPolicyHeaderDAO;
	private FinanceMainDAO financeMainDAO;
	private FinanceScheduleDetailDAO financeScheduleDetailDAO;
	private FinanceTypeDAO financeTypeDAO;
	private FinFeeChargesDAO finFeeChargesDAO;
	private RuleService ruleService;
	private CustomerDetailsService customerDetailsService;
	private FeeDetailService feeDetailService;
	private FinFeeDetailService finFeeDetailService;

	/**
	 * Method for create Finance/WIFFinance
	 * 
	 * @param finCalculatorRequest
	 * @return
	 * @throws JaxenException
	 * @throws InvocationTargetException 
	 * @throws IllegalAccessException 
	 */
	public FinScheduleData doCreateFinanceSchedule(FinScheduleData finScheduleData)throws JaxenException,
			IllegalAccessException, InvocationTargetException {
		logger.debug("Enteing");

		if (finScheduleData != null) {

			// financeMain details
			FinanceMain financeMain = finScheduleData.getFinanceMain();
			financeMain.setFinType(finScheduleData.getFinanceType().getFinType());

			financeMain.setFinReference(String.valueOf(ReferenceGenerator.generateNewFinRef(true, financeMain)));
			financeMain.setRecordType(PennantConstants.RECORD_TYPE_NEW);
			financeMain.setWorkflowId(0);
			financeMain.setNewRecord(true);
			financeMain.setRecordStatus(PennantConstants.RCD_STATUS_APPROVED);
			financeMain.setFinSourceID(PennantConstants.FINSOURCE_ID_API);
			// prepare required data
			doSetRequiredData(finScheduleData);
			
			try {
				// call schedule calculator
				finScheduleData = ScheduleGenerator.getNewSchd(finScheduleData);
				if (finScheduleData.getFinanceScheduleDetails().size() != 0) {
					
					finScheduleData = ScheduleCalculator.getCalSchd(finScheduleData, BigDecimal.ZERO);
					finScheduleData.setSchduleGenerated(true);
					if(!finScheduleData.getFinanceMain().isAllowGrcPeriod()){
						finScheduleData.getFinanceMain().setGrcSchdMthd(null);
					}
					// fees calculation
					if(!finScheduleData.getFinFeeDetailList().isEmpty()) {
						finScheduleData = FeeScheduleCalculator.feeSchdBuild(finScheduleData);
					}
				}

				if (!finScheduleData.getErrorDetails().isEmpty()) {
					return finScheduleData;
				}

				// set LastMntBy , LastMntOn and status fields to schedule details
				for (FinanceScheduleDetail schdDetail : finScheduleData.getFinanceScheduleDetails()) {
					schdDetail.setLastMntOn(new Timestamp(System.currentTimeMillis()));
					schdDetail.setRecordStatus(PennantConstants.RCD_STATUS_APPROVED);
				}

				// Reset Data
				finScheduleData.getFinanceMain().setEqualRepay(financeMain.isEqualRepay());
				finScheduleData.getFinanceMain().setRecalType(financeMain.getRecalType());
				finScheduleData.getFinanceMain().setLastRepayDate(financeMain.getFinStartDate());
				finScheduleData.getFinanceMain().setLastRepayPftDate(financeMain.getFinStartDate());
				finScheduleData.getFinanceMain().setLastRepayRvwDate(financeMain.getFinStartDate());
				finScheduleData.getFinanceMain().setLastRepayCpzDate(financeMain.getFinStartDate());

				finScheduleData.getFinanceMain().setFinRemarks("SUCCESS");

				// Finance detail object
				FinanceDetail afinanceDetail = new FinanceDetail();
				afinanceDetail.setUserAction("");
				afinanceDetail.setExtSource(false);
				afinanceDetail.setModuleDefiner(FinanceConstants.FINSER_EVENT_ORG);

				finScheduleData.setFinReference(financeMain.getFinReference());
				doProcessPlanEMIHDays(finScheduleData);
				
				if (finScheduleData.getErrorDetails() != null) {
					for (ErrorDetails errorDetail : finScheduleData.getErrorDetails()) {
						FinScheduleData response = new FinScheduleData();
						doEmptyResponseObject(response);
						response.setReturnStatus(APIErrorHandlerService.getFailedStatus(errorDetail.getErrorCode(),
								errorDetail.getError()));
						return response;
					}
				}
				afinanceDetail.setFinScheduleData(finScheduleData);

				AuditDetail auditDetail = new AuditDetail(PennantConstants.TRAN_WF, 1, null, afinanceDetail);
				AuditHeader auditHeader = new AuditHeader(afinanceDetail.getFinScheduleData().getFinReference(), null,
						null, null, auditDetail, financeMain.getUserDetails(), new HashMap<String, ArrayList<ErrorDetails>>());
				//get the header details from the request
				APIHeader reqHeaderDetails = (APIHeader) PhaseInterceptorChain.getCurrentMessage().getExchange().get(APIHeader.API_HEADER_KEY);
				//set the headerDetails to AuditHeader
				auditHeader.setApiHeader(reqHeaderDetails);
				// save the finance details into main table
				auditHeader = getFinanceDetailService().doApprove(auditHeader, true);

				FinScheduleData response = null;
				if (auditHeader.getOverideMessage() != null && auditHeader.getOverideMessage().size() > 0) {
					for (ErrorDetails errorDetail : auditHeader.getOverideMessage()) {
						response = new FinScheduleData();
						doEmptyResponseObject(response);
						response.setReturnStatus(APIErrorHandlerService.getFailedStatus(errorDetail.getErrorCode(),
								errorDetail.getError()));
						return response;
					}
				}
				if (auditHeader.getErrorMessage() != null) {
					for (ErrorDetails errorDetail : auditHeader.getErrorMessage()) {
						response = new FinScheduleData();
						doEmptyResponseObject(response);
						response.setReturnStatus(APIErrorHandlerService.getFailedStatus(errorDetail.getErrorCode(),
								errorDetail.getError()));
						return response;
					}
				}
				
				if (auditHeader.getAuditDetail().getErrorDetails() != null) {
					for (ErrorDetails errorDetail : auditHeader.getAuditDetail().getErrorDetails()) {
						response = new FinScheduleData();
						doEmptyResponseObject(response);
						response.setReturnStatus(APIErrorHandlerService.getFailedStatus(errorDetail.getErrorCode(),
								errorDetail.getError()));
						return response;
					}
				}

				response = getFinanceScheduleResponse(auditHeader);
				response.setReturnStatus(APIErrorHandlerService.getSuccessStatus());
				
				logger.debug("Leaving");
				return response;
			} catch (Exception e) {
				logger.error("Exception", e);
				FinScheduleData response = new FinScheduleData();
				doEmptyResponseObject(response);
				response.setReturnStatus(APIErrorHandlerService.getFailedStatus());
				return response;
			}
		}
		return null;
	}

	
	private void doSetRequiredData(FinScheduleData finScheduleData) throws IllegalAccessException, InvocationTargetException {
		logger.debug("Entering");
		
		LoggedInUser userDetails = SessionUserDetails.getUserDetails(SessionUserDetails.getLogiedInUser());
		for(VASRecording vasRecording:finScheduleData.getVasRecordingList()){
			vasRecording.setRecordType(PennantConstants.RECORD_TYPE_NEW);
			vasRecording.setNewRecord(true);
			vasRecording.setRecordStatus(PennantConstants.RCD_STATUS_APPROVED);
			vasRecording.setVasReference(ReferenceUtil.generateVASRef());
			vasRecording.setPostingAgainst(VASConsatnts.VASAGAINST_FINANCE);
			vasRecording.setVasStatus("N");
			List<ExtendedField> extendedFields = vasRecording.getExtendedDetails();
			if (extendedFields != null) {
				int seqNo = 0;
				ExtendedFieldRender exdFieldRender = new ExtendedFieldRender();
				exdFieldRender.setReference(vasRecording.getVasReference());
				exdFieldRender.setLastMntOn(new Timestamp(System.currentTimeMillis()));
				exdFieldRender.setRecordStatus(PennantConstants.RCD_STATUS_APPROVED);
				exdFieldRender.setLastMntBy(userDetails.getLoginUsrID());
				exdFieldRender.setSeqNo(++seqNo);
				exdFieldRender.setNewRecord(true);
				exdFieldRender.setRecordType(PennantConstants.RECORD_TYPE_NEW);
				exdFieldRender.setVersion(1);
				for (ExtendedField extendedField : extendedFields) {
					Map<String, Object> mapValues = new HashMap<String, Object>();
					for (ExtendedFieldData extFieldData : extendedField.getExtendedFieldDataList()) {
						mapValues.put(extFieldData.getFieldName(), extFieldData.getFieldValue());
						exdFieldRender.setMapValues(mapValues);
					}
				}
				if(extendedFields.size()<=0){
					Map<String, Object> mapValues = new HashMap<String, Object>();
					exdFieldRender.setMapValues(mapValues);
				}

				vasRecording.setExtendedFieldRender(exdFieldRender);
			}else {
				ExtendedFieldRender exdFieldRender = new ExtendedFieldRender();
				exdFieldRender.setReference(vasRecording.getVasReference());
				exdFieldRender.setLastMntOn(new Timestamp(System.currentTimeMillis()));
				exdFieldRender.setRecordStatus(PennantConstants.RCD_STATUS_APPROVED);
				exdFieldRender.setLastMntBy(userDetails.getLoginUsrID());
				exdFieldRender.setSeqNo(0);
				exdFieldRender.setNewRecord(true);
				exdFieldRender.setRecordType(PennantConstants.RECORD_TYPE_NEW);
				exdFieldRender.setVersion(1);
				Map<String, Object> mapValues = new HashMap<String, Object>();
				exdFieldRender.setMapValues(mapValues);
				vasRecording.setExtendedFieldRender(exdFieldRender);
			}
		}
		FinanceMain financeMain = finScheduleData.getFinanceMain();
		
		// user details
		
		financeMain.setUserDetails(userDetails);
		financeMain.setFinIsActive(true);
		financeMain.setVersion(1);
		financeMain.setLastMntBy(userDetails.getLoginUsrID());
		financeMain.setLastMntOn(new Timestamp(System.currentTimeMillis()));
		financeMain.setFinStatus(financeDetailService.getCustStatusByMinDueDays());
		
		financeMain.setMaturityDate(financeMain.getCalMaturity());
		financeMain.setNumberOfTerms(financeMain.getCalTerms());
		financeMain.setGrcPeriodEndDate(financeMain.getCalGrcEndDate());
		financeMain.setGraceTerms(financeMain.getCalGrcTerms());
		
		// set Head branch
		if(StringUtils.isBlank(financeMain.getFinBranch())) {
			financeMain.setFinBranch(userDetails.getBranchCode());
		}
		
		FinanceDetail financeDetail = new FinanceDetail();
		financeDetail.setFinScheduleData(finScheduleData);
		if (financeMain.getCustID() > 0) {
			CustomerDetails custDetails = customerDetailsService.getApprovedCustomerById(financeMain.getCustID());
			financeDetail.setCustomerDetails(custDetails);
		}
		
		// Set VAS reference as feeCode for VAS related fees
		for(FinFeeDetail feeDetail:finScheduleData.getFinFeeDetailList()) {
			for(VASRecording vasRecording:finScheduleData.getVasRecordingList()) {
				if(StringUtils.equals(feeDetail.getFinEvent(), AccountEventConstants.ACCEVENT_VAS_FEE) &&
					StringUtils.contains(feeDetail.getFeeTypeCode(), vasRecording.getProductCode())) {
					feeDetail.setFeeTypeCode(vasRecording.getVasReference());
					feeDetail.setVasReference(vasRecording.getVasReference());
					feeDetail.setCalculatedAmount(vasRecording.getFee());
					feeDetail.setFixedAmount(vasRecording.getFee());
					feeDetail.setAlwDeviation(true);
					feeDetail.setMaxWaiverPerc(BigDecimal.valueOf(100));
					//feeDetail.setAlwModifyFee(true);
					feeDetail.setAlwModifyFeeSchdMthd(true);
					feeDetail.setCalculationType(PennantConstants.FEE_CALCULATION_TYPE_FIXEDAMOUNT);
				}
			}
		}
		// fetch finType fees details
		String finEvent = "";
		feeDetailService.doExecuteFeeCharges(financeDetail, finEvent);
		
		// Step Policy Details
		if(financeMain.isStepFinance()) {
			String stepPolicyCode = financeMain.getStepPolicy();
			if (StringUtils.isNotBlank(stepPolicyCode)) {
				List<StepPolicyDetail> stepPolicyList = getStepPolicyDetailDAO().getStepPolicyDetailListByID(
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
				Collections.sort(finStepDetails, new Comparator<FinanceStepPolicyDetail>() {
					@Override
					public int compare(FinanceStepPolicyDetail b1, FinanceStepPolicyDetail b2) {
						return (new Integer(b1.getStepNo()).compareTo(new Integer(b2.getStepNo())));
					}
				});
				// method for prepare step installments
				prepareStepInstallements(finStepDetails, financeMain.getNumberOfTerms());
			}
		}

		finScheduleData.getFinanceMain().setCalculateRepay(true);//FIXME: why this field

		// Disbursement details
		FinanceDisbursement disbursementDetails = new FinanceDisbursement();
		disbursementDetails.setDisbDate(financeMain.getFinStartDate());
		disbursementDetails.setDisbAmount(financeMain.getFinAmount());
		disbursementDetails.setDisbSeq(1);
		disbursementDetails.setVersion(1);
		disbursementDetails.setDisbReqDate(DateUtility.getAppDate());
		disbursementDetails.setFeeChargeAmt(financeMain.getFeeChargeAmt());
		disbursementDetails.setInsuranceAmt(financeMain.getInsuranceAmt());
		disbursementDetails.setDisbAccountId(PennantApplicationUtil.unFormatAccountNumber(financeMain.getDisbAccountId()));
		finScheduleData.getDisbursementDetails().add(disbursementDetails);

		logger.debug("Leaving");
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

	/**
	 * Method for prepare API response object
	 * 
	 * @param finReference
	 * @return
	 */
	private FinScheduleData getFinanceScheduleResponse(AuditHeader auditHeader) {
		logger.debug("Enteing");

		FinanceDetail financeDetail = (FinanceDetail) auditHeader.getAuditDetail().getModelData();
		
		// fetch finance basic details
		FinanceMain financeMain = financeDetail.getFinScheduleData().getFinanceMain();
		FinScheduleData finScheduleData = new FinScheduleData();

		if(financeMain != null) {
			finScheduleData.setFinReference(financeMain.getFinReference());
			finScheduleData.setFinReference(financeMain.getFinReference());
			finScheduleData.setFinFeeDetailList(financeDetail.getFinScheduleData().getFinFeeDetailList());
			finScheduleData.setStepPolicyDetails(financeDetail.getFinScheduleData().getStepPolicyDetails());
			finScheduleData.setFinanceScheduleDetails(financeDetail.getFinScheduleData().getFinanceScheduleDetails());
			
			// set fee paid amounts based on schedule method
			finScheduleData.setFinFeeDetailList(getUpdatedFees(finScheduleData.getFinFeeDetailList()));
			
			//summary
			FinanceDetail response = new FinanceDetail();
			//used for AEAMOUNTS class 
			response.setFinReference(financeMain.getFinReference());
			financeMain.setRecordType(PennantConstants.RECORD_TYPE_NEW);;
			response.getFinScheduleData().setFinanceMain(financeMain);
			response.getFinScheduleData().setFinanceScheduleDetails(financeDetail.getFinScheduleData().getFinanceScheduleDetails());
			finScheduleData.setFinanceSummary(getFinanceSummary(response));
		}
		// to remove un-necessary objects from response make them as null
		finScheduleData.setDisbursementDetails(null);
		finScheduleData.setRepayInstructions(null);
		finScheduleData.setRateInstruction(null);
		finScheduleData.setFinODPenaltyRate(null);

		logger.debug("Leaving");

		return finScheduleData;
	}

	private void doEmptyResponseObject(FinScheduleData response) {
		response.setDisbursementDetails(null);
		response.setRepayInstructions(null);
		response.setRateInstruction(null);
		response.setFinFeeDetailList(null);
		response.setInsuranceList(null);
		response.setStepPolicyDetails(null);
		response.setFinanceScheduleDetails(null);
		response.setPlanEMIHDates(null);
		response.setPlanEMIHmonths(null);
		response.setApiPlanEMIHDates(null);
		response.setApiplanEMIHmonths(null);
		response.setFinODDetails(null);
	}
	
	/**
	 * Method to process and fetch Finance Inquiry details
	 * 
	 * @param finReference
	 * @return
	 */
	public FinScheduleData getFinanceInquiryDetails(String finReference, String type) {
		logger.debug("Enteing");

		FinScheduleData response = new FinScheduleData();

		try {
			FinanceDetail financeDetail = null;
			if(StringUtils.equals(type, APIConstants.FINANCE_ORIGINATION)) {
				financeDetail = getFinanceDetailService().getFinanceDetailById(finReference, false, "", 
						false, FinanceConstants.FINSER_EVENT_ORG, "");
			} else {
				financeDetail = getFinanceDetailService().getWIFFinance(finReference, false, null);
			}

			if (financeDetail != null) {
				FinanceMain financeMain = financeDetail.getFinScheduleData().getFinanceMain();
				//setting Disb first and lastDates
				List<FinanceDisbursement> disbList = financeDetail.getFinScheduleData().getDisbursementDetails();
				Collections.sort(disbList, new Comparator<FinanceDisbursement>() {
					@Override
					public int compare(FinanceDisbursement b1, FinanceDisbursement b2) {
						return (new Integer(b1.getDisbSeq()).compareTo(new Integer(b2.getDisbSeq())));
					}
				});

				if (disbList != null && disbList.size() > 0) {
					if (disbList.size() == 1) {
						financeMain.setFirstDisbDate(disbList.get(0).getDisbDate());
						financeMain.setLastDisbDate(disbList.get(0).getDisbDate());
					} else {
						financeMain.setFirstDisbDate(disbList.get(0).getDisbDate());
						financeMain.setLastDisbDate(disbList.get(disbList.size() - 1).getDisbDate());
					}
				}

				// Avoid Grace Period details into the marshaling in case of Allow grace is false
				if (!financeMain.isAllowGrcPeriod()) {
					financeMain.setGrcPeriodEndDate(null);
					financeMain.setGrcRateBasis(null);
					financeMain.setGrcPftRate(null);
					financeMain.setGraceBaseRate(null);
					financeMain.setGraceSpecialRate(null);
					financeMain.setGrcMargin(null);
					financeMain.setGrcProfitDaysBasis(null);
					financeMain.setGrcPftFrq(null);
					financeMain.setNextGrcPftDate(null);
					financeMain.setGrcPftRvwFrq(null);
					financeMain.setNextGrcPftRvwDate(null);
					financeMain.setGrcCpzFrq(null);
					financeMain.setNextGrcCpzDate(null);
					financeMain.setAllowGrcRepay(false);
					financeMain.setGrcSchdMthd(null);
					financeMain.setGrcMinRate(null);
					financeMain.setGrcMaxRate(null);
					financeMain.setGrcAdvPftRate(null);
					financeMain.setGrcAdvBaseRate(null);
					financeMain.setGrcAdvMargin(null);
				}

				// Summary details
				response = financeDetail.getFinScheduleData();
				// set fee paid amounts based on schedule method
				response.setFinFeeDetailList(getUpdatedFees(response.getFinFeeDetailList()));
				response.setFinanceSummary(getFinanceSummary(financeDetail));
				response.setReturnStatus(APIErrorHandlerService.getSuccessStatus());
				//get FinODDetails
				List<FinODDetails> finODDetailsList = finODDetailsDAO.getFinODDByFinRef(finReference, null);
				response.setFinODDetails(finODDetailsList);
				// to remove un-necessary objects from response make them as null
				response.setDisbursementDetails(null);
				response.setRepayInstructions(null);
				response.setRateInstruction(null);
				response.setFinODPenaltyRate(null);
			} else {
				response.setReturnStatus(APIErrorHandlerService.getFailedStatus());
			}

		} catch(Exception e) {
			logger.debug("Exception: ", e);
			response.setReturnStatus(APIErrorHandlerService.getFailedStatus());
		}

		logger.debug("Leaving");
		return response;
	}

	public FinanceDetailService getFinanceDetailService() {
		return financeDetailService;
	}

	public void setFinanceDetailService(FinanceDetailService financeDetailService) {
		this.financeDetailService = financeDetailService;
	}

	public StepPolicyDetailDAO getStepPolicyDetailDAO() {
		return stepPolicyDetailDAO;
	}

	public void setStepPolicyDetailDAO(StepPolicyDetailDAO stepPolicyDetailDAO) {
		this.stepPolicyDetailDAO = stepPolicyDetailDAO;
	}

	public FinanceMainDAO getFinanceMainDAO() {
		return financeMainDAO;
	}

	public void setFinanceMainDAO(FinanceMainDAO financeMainDAO) {
		this.financeMainDAO = financeMainDAO;
	}

	public FinanceScheduleDetailDAO getFinanceScheduleDetailDAO() {
		return financeScheduleDetailDAO;
	}

	public void setFinanceScheduleDetailDAO(
			FinanceScheduleDetailDAO financeScheduleDetailDAO) {
		this.financeScheduleDetailDAO = financeScheduleDetailDAO;
	}

	public FinanceTypeDAO getFinanceTypeDAO() {
		return financeTypeDAO;
	}

	public void setFinanceTypeDAO(FinanceTypeDAO financeTypeDAO) {
		this.financeTypeDAO = financeTypeDAO;
	}

	public FinFeeChargesDAO getFinFeeChargesDAO() {
		return finFeeChargesDAO;
	}

	public void setFinFeeChargesDAO(FinFeeChargesDAO finFeeChargesDAO) {
		this.finFeeChargesDAO = finFeeChargesDAO;
	}

	public RuleService getRuleService() {
		return ruleService;
	}

	public void setRuleService(RuleService ruleService) {
		this.ruleService = ruleService;
	}

	public void setCustomerDetailsService(CustomerDetailsService customerDetailsService) {
		this.customerDetailsService = customerDetailsService;
	}
	
	public void setStepPolicyHeaderDAO(StepPolicyHeaderDAO stepPolicyHeaderDAO) {
		this.stepPolicyHeaderDAO = stepPolicyHeaderDAO;
	}

	public FeeDetailService getFeeDetailService() {
		return feeDetailService;
	}

	public void setFeeDetailService(FeeDetailService feeDetailService) {
		this.feeDetailService = feeDetailService;
	}

	public FinFeeDetailService getFinFeeDetailService() {
		return finFeeDetailService;
	}

	public void setFinFeeDetailService(FinFeeDetailService finFeeDetailService) {
		this.finFeeDetailService = finFeeDetailService;
	}
}
