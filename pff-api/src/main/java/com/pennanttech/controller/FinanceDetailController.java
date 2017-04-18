package com.pennanttech.controller;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.cxf.phase.PhaseInterceptorChain;
import org.apache.log4j.Logger;
import org.jaxen.JaxenException;

import com.pennant.app.constants.AccountEventConstants;
import com.pennant.app.util.APIHeader;
import com.pennant.app.util.DateUtility;
import com.pennant.app.util.FeeScheduleCalculator;
import com.pennant.app.util.ReferenceGenerator;
import com.pennant.app.util.ScheduleCalculator;
import com.pennant.app.util.ScheduleGenerator;
import com.pennant.app.util.SessionUserDetails;
import com.pennant.backend.dao.finance.FinanceMainDAO;
import com.pennant.backend.dao.finance.FinanceScheduleDetailDAO;
import com.pennant.backend.dao.financemanagement.FinanceStepDetailDAO;
import com.pennant.backend.dao.rmtmasters.FinanceTypeDAO;
import com.pennant.backend.dao.rulefactory.FinFeeChargesDAO;
import com.pennant.backend.dao.solutionfactory.StepPolicyDetailDAO;
import com.pennant.backend.dao.solutionfactory.StepPolicyHeaderDAO;
import com.pennant.backend.model.ErrorDetails;
import com.pennant.backend.model.LoggedInUser;
import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.model.customermasters.Customer;
import com.pennant.backend.model.customermasters.CustomerDetails;
import com.pennant.backend.model.finance.FinFeeDetail;
import com.pennant.backend.model.finance.FinODDetails;
import com.pennant.backend.model.finance.FinPlanEmiHoliday;
import com.pennant.backend.model.finance.FinScheduleData;
import com.pennant.backend.model.finance.FinanceDetail;
import com.pennant.backend.model.finance.FinanceDisbursement;
import com.pennant.backend.model.finance.FinanceMain;
import com.pennant.backend.model.finance.FinanceScheduleDetail;
import com.pennant.backend.model.finance.FinanceStepPolicyDetail;
import com.pennant.backend.model.rmtmasters.FinanceType;
import com.pennant.backend.model.solutionfactory.StepPolicyDetail;
import com.pennant.backend.model.solutionfactory.StepPolicyHeader;
import com.pennant.backend.service.customermasters.CustomerDetailsService;
import com.pennant.backend.service.fees.FeeDetailService;
import com.pennant.backend.service.finance.FinFeeDetailService;
import com.pennant.backend.service.finance.FinanceDetailService;
import com.pennant.backend.service.rulefactory.RuleService;
import com.pennant.backend.util.FinanceConstants;
import com.pennant.backend.util.PennantConstants;
import com.pennant.exception.PFFInterfaceException;
import com.pennanttech.util.APIConstants;
import com.pennanttech.ws.service.APIErrorHandlerService;

public class FinanceDetailController extends SummaryDetailService {

	private final static Logger logger = Logger.getLogger(FinanceDetailController.class);

	private FinanceDetailService financeDetailService;
	private StepPolicyDetailDAO stepPolicyDetailDAO;
	private StepPolicyHeaderDAO stepPolicyHeaderDAO;
	private FinanceMainDAO financeMainDAO;
	private FinanceScheduleDetailDAO financeScheduleDetailDAO;
	private FinanceTypeDAO financeTypeDAO;
	private FinFeeChargesDAO finFeeChargesDAO;
	private RuleService ruleService;
	private CustomerDetailsService customerDetailsService;
	private FinanceStepDetailDAO financeStepDetailDAO;
	private FeeDetailService feeDetailService;
	private FinFeeDetailService finFeeDetailService;

	/**
	 * Method for create Finance/WIFFinance
	 * 
	 * @param finCalculatorRequest
	 * @return
	 * @throws JaxenException
	 * @throws PFFInterfaceException
	 */
	public FinScheduleData doCreateFinanceSchedule(FinScheduleData finScheduleData)throws JaxenException,
			PFFInterfaceException {
		logger.debug("Enteing");

		if (finScheduleData != null) {

			// financeMain details
			FinanceMain financeMain = finScheduleData.getFinanceMain();

			financeMain.setFinReference(String.valueOf(ReferenceGenerator.generateNewFinRef(true, financeMain)));
			financeMain.setRecordType(PennantConstants.RECORD_TYPE_NEW);
			financeMain.setWorkflowId(0);
			financeMain.setNewRecord(true);
			financeMain.setRecordStatus(PennantConstants.RCD_STATUS_APPROVED);
			financeMain.setFinSourceID(PennantConstants.FINSOURCE_ID_API);

			// Fetch Finance Type object
			FinanceType financeType = getFinanceTypeDAO().getFinanceTypeByID(financeMain.getFinType(), "");

			// setting financeType object
			finScheduleData.setFinanceType(financeType);

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
						finScheduleData = FeeScheduleCalculator.getFeeScheduleDetails(finScheduleData);
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
				 List<Date> planEMIHDates = new ArrayList<Date>(); 
				 List<Integer> planEMIHmonths = new ArrayList<Integer>();
				// Plan EMI Holidays Resetting after Rescheduling
				 boolean isValidSchDate = false;
				if (finScheduleData.getApiPlanEMIHDates() != null) {
					if (StringUtils.equals(finScheduleData.getFinanceMain().getPlanEMIHMethod(),
							FinanceConstants.PLANEMIHMETHOD_FRQ)) {
						for (FinPlanEmiHoliday detail : finScheduleData.getApiPlanEMIHmonths()) {
							planEMIHmonths.add(detail.getPlanEMIHMonth());
						}
					} else {
						for (FinPlanEmiHoliday detail : finScheduleData.getApiPlanEMIHDates()) {
							
							planEMIHDates.add(detail.getPlanEMIHDate());
							List<FinanceScheduleDetail> schedules = finScheduleData.getFinanceScheduleDetails();
							for (FinanceScheduleDetail schDetail : schedules) {
								if (DateUtility.compare(detail.getPlanEMIHDate(), schDetail.getSchDate()) == 0) {
									isValidSchDate = true;
									if (schDetail.getInstNumber() == 1
											|| DateUtility.compare(financeMain.getMaturityDate(),schDetail.getSchDate()) == 0
											|| DateUtility.compare(financeMain.getFinStartDate(),schDetail.getSchDate()) == 0
											|| DateUtility.compare(financeMain.getGrcPeriodEndDate(),schDetail.getSchDate()) >=0) {
										FinScheduleData response = new FinScheduleData();
										doEmptyResponseObject(response);
										String[] valueParm = new String[1];
										valueParm[0] = "holidayDate";
										response.setReturnStatus(APIErrorHandlerService.getFailedStatus("91111",
												valueParm));
										return response;
									}
								}
							}
						}
					}
				}
				if (!isValidSchDate && StringUtils.equals(finScheduleData.getFinanceMain().getPlanEMIHMethod(),
						FinanceConstants.PLANEMIHMETHOD_ADHOC) && financeMain.isPlanEMIHAlw()) {
					FinScheduleData response = new FinScheduleData();
					doEmptyResponseObject(response);
					String[] valueParm = new String[1];
					valueParm[0] = "holidayDate";
					response.setReturnStatus(APIErrorHandlerService.getFailedStatus("91111", valueParm));
					return response;
				}
				finScheduleData.setPlanEMIHDates(planEMIHDates);
				finScheduleData.setPlanEMIHmonths(planEMIHmonths);
				if(finScheduleData.getFinanceMain().isPlanEMIHAlw()){
					finScheduleData.getFinanceMain().setEventFromDate(financeMain.getFinStartDate());
					finScheduleData.getFinanceMain().setEventToDate(finScheduleData.getFinanceMain().getMaturityDate());
					finScheduleData.getFinanceMain().setRecalFromDate(financeMain.getNextRepayDate());
					finScheduleData.getFinanceMain().setRecalToDate(finScheduleData.getFinanceMain().getMaturityDate());
					finScheduleData.getFinanceMain().setRecalSchdMethod(finScheduleData.getFinanceMain().getScheduleMethod());

					if(StringUtils.equals(finScheduleData.getFinanceMain().getPlanEMIHMethod(), FinanceConstants.PLANEMIHMETHOD_FRQ)){
						finScheduleData = ScheduleCalculator.getFrqEMIHoliday(finScheduleData);
					}else{
						finScheduleData = ScheduleCalculator.getAdhocEMIHoliday(finScheduleData);
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
				AuditHeader auditHeader1 = getFinanceDetailService().doApprove(auditHeader, true);

				FinScheduleData response = null;
				if (auditHeader1.getErrorMessage() != null) {
					for (ErrorDetails errorDetail : auditHeader1.getErrorMessage()) {
						response = new FinScheduleData();
						doEmptyResponseObject(response);
						response.setReturnStatus(APIErrorHandlerService.getFailedStatus(errorDetail.getErrorCode(),
								errorDetail.getError()));
						return response;
					}
				}
				
				if (auditHeader1.getAuditDetail().getErrorDetails() != null) {
					for (ErrorDetails errorDetail : auditHeader1.getAuditDetail().getErrorDetails()) {
						response = new FinScheduleData();
						doEmptyResponseObject(response);
						response.setReturnStatus(APIErrorHandlerService.getFailedStatus(errorDetail.getErrorCode(),
								errorDetail.getError()));
						return response;
					}
				}
			} catch (Exception e) {
				logger.error("Exception", e);
				FinScheduleData response = new FinScheduleData();
				doEmptyResponseObject(response);
				response.setReturnStatus(APIErrorHandlerService.getFailedStatus());
				return response;
			}

			FinScheduleData response = getFinanceScheduleResponse(financeMain.getFinReference());
			response.setReturnStatus(APIErrorHandlerService.getSuccessStatus());

			logger.debug("Leaving");

			return response;
		}
		return null;
	}

	private void doSetRequiredData(FinScheduleData finScheduleData) {
		logger.debug("Entering");
		
		FinanceMain financeMain = finScheduleData.getFinanceMain();
		
		// user details
		LoggedInUser userDetails = SessionUserDetails.getUserDetails(SessionUserDetails.getLogiedInUser());
		financeMain.setUserDetails(userDetails);
		financeMain.setFinIsActive(true);
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
		
		// setting required values which are not received from API
		if (StringUtils.isNotBlank(financeMain.getLovDescCustCIF()) && financeMain.getCustID() == 0) {
			Customer customer = customerDetailsService.getCustomerByCIF(financeMain.getLovDescCustCIF());
			financeMain.setCustID(customer.getCustID());
		}
		
		// Fee Details
		FinanceDetail financeDetail = new FinanceDetail();
		financeDetail.setFinScheduleData(finScheduleData);
		if(financeMain.getCustID()>0){
			CustomerDetails custDetails=customerDetailsService.getApprovedCustomerById(financeMain.getCustID());
			financeDetail.setCustomerDetails(custDetails);
		}
		
		// fetch finType fees details
		String finEvent = "";
		if (financeMain.getFinStartDate().after(DateUtility.getAppDate())) {
			if (AccountEventConstants.ACCEVENT_ADDDBSF_REQ) {
				finEvent = AccountEventConstants.ACCEVENT_ADDDBSF;
			} else {
				finEvent = AccountEventConstants.ACCEVENT_ADDDBSP;
			}
		} else {
			finEvent = AccountEventConstants.ACCEVENT_ADDDBSP;
		}
		financeDetail.getFinScheduleData().setFeeEvent(finEvent);
		financeDetail.setFinTypeFeesList(getFinanceDetailService().getFinTypeFees(financeMain.getFinType(),finEvent, true, FinanceConstants.MODULEID_FINTYPE));
		
		feeDetailService.doExecuteFeeCharges(true, financeDetail);
		
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
				
				// method for prepare step installments
				prepareStepInstallements(finStepDetails, financeMain.getNumberOfTerms());
			}
		}

		finScheduleData.getFinanceMain().setCalculateRepay(true);//FIXME: why this field

		// Disbursement details
		FinanceDisbursement disbursementDetails = new FinanceDisbursement();
		disbursementDetails.setDisbDate(financeMain.getFinStartDate());
		disbursementDetails.setDisbAmount(financeMain.getFinAmount());
		disbursementDetails.setDisbReqDate(DateUtility.getAppDate());
		disbursementDetails.setFeeChargeAmt(financeMain.getFeeChargeAmt());
		disbursementDetails.setInsuranceAmt(financeMain.getInsuranceAmt());
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
	private FinScheduleData getFinanceScheduleResponse(String finReference) {
		logger.debug("Enteing");

		// fetch finance basic details
		FinanceMain financeMain = getFinanceMainDAO().getFinanceMainById(finReference, "", true);
		FinScheduleData finScheduleData = new FinScheduleData();

		if(financeMain != null) {
			finScheduleData.setFinReference(finReference);

			// Fetch FeeRules
			List<FinFeeDetail> feeRules = getFinFeeDetailService().getFinFeeDetailById(finReference, true, "");

			if(feeRules != null) {
				finScheduleData.setFinFeeDetailList(feeRules);
			}

			// fetch step details if exists
			if (financeMain.isStepFinance()) {
				List<FinanceStepPolicyDetail> stepPolicyDetails = financeStepDetailDAO.getFinStepDetailListByFinRef(
						finReference, "", true);
				finScheduleData.setStepPolicyDetails(stepPolicyDetails);
			}
			
			// fetch finance schedule details
			List<FinanceScheduleDetail> finSchduleList = getFinanceScheduleDetailDAO().getFinScheduleDetails(finReference, "", true);
			if(finSchduleList != null && !finSchduleList.isEmpty()) {
				finScheduleData.setFinanceScheduleDetails(finSchduleList);
			}
			//summary
			FinanceDetail financeDetail = new FinanceDetail();
			financeDetail.getFinScheduleData().setFinanceMain(financeMain);
			financeDetail.getFinScheduleData().setFinanceScheduleDetails(finSchduleList);
			finScheduleData.setFinanceSummary(getFinanceSummary(financeDetail));
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
				response.setFinanceSummary(getFinanceSummary(financeDetail));
				response.setReturnStatus(APIErrorHandlerService.getSuccessStatus());
				//get FinODDetails
				List<FinODDetails> finODDetailsList = finODDetailsDAO.getFinODDetailsByFinReference(finReference, "");
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
	
	public void setFinanceStepDetailDAO(FinanceStepDetailDAO financeStepDetailDAO) {
		this.financeStepDetailDAO = financeStepDetailDAO;
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
