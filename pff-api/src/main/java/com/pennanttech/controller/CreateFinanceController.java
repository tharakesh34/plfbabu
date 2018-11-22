package com.pennanttech.controller;

import java.lang.reflect.InvocationTargetException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.cxf.phase.PhaseInterceptorChain;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import com.pennant.app.constants.AccountEventConstants;
import com.pennant.app.constants.ImplementationConstants;
import com.pennant.app.util.APIHeader;
import com.pennant.app.util.DateUtility;
import com.pennant.app.util.ErrorUtil;
import com.pennant.app.util.FeeScheduleCalculator;
import com.pennant.app.util.ReferenceGenerator;
import com.pennant.app.util.ReferenceUtil;
import com.pennant.app.util.RuleExecutionUtil;
import com.pennant.app.util.ScheduleCalculator;
import com.pennant.app.util.ScheduleGenerator;
import com.pennant.app.util.SessionUserDetails;
import com.pennant.app.util.SysParamUtil;
import com.pennant.backend.dao.audit.AuditHeaderDAO;
import com.pennant.backend.dao.documentdetails.DocumentDetailsDAO;
import com.pennant.backend.dao.finance.FinAdvancePaymentsDAO;
import com.pennant.backend.dao.finance.FinFeeReceiptDAO;
import com.pennant.backend.dao.finance.FinPlanEmiHolidayDAO;
import com.pennant.backend.dao.finance.FinanceMainDAO;
import com.pennant.backend.dao.finance.FinanceScheduleDetailDAO;
import com.pennant.backend.dao.finance.ManualAdviseDAO;
import com.pennant.backend.dao.lmtmasters.FinanceReferenceDetailDAO;
import com.pennant.backend.dao.receipts.FinReceiptHeaderDAO;
import com.pennant.backend.dao.solutionfactory.StepPolicyDetailDAO;
import com.pennant.backend.dao.solutionfactory.StepPolicyHeaderDAO;
import com.pennant.backend.dao.staticparms.ExtendedFieldHeaderDAO;
import com.pennant.backend.dao.systemmasters.DivisionDetailDAO;
import com.pennant.backend.model.WSReturnStatus;
import com.pennant.backend.model.WorkFlowDetails;
import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.model.bmtmasters.BankBranch;
import com.pennant.backend.model.collateral.CoOwnerDetail;
import com.pennant.backend.model.collateral.CollateralAssignment;
import com.pennant.backend.model.collateral.CollateralSetup;
import com.pennant.backend.model.collateral.CollateralStructure;
import com.pennant.backend.model.collateral.CollateralThirdParty;
import com.pennant.backend.model.configuration.VASRecording;
import com.pennant.backend.model.customermasters.Customer;
import com.pennant.backend.model.customermasters.CustomerAddres;
import com.pennant.backend.model.customermasters.CustomerDetails;
import com.pennant.backend.model.documentdetails.DocumentDetails;
import com.pennant.backend.model.extendedfield.ExtendedField;
import com.pennant.backend.model.extendedfield.ExtendedFieldData;
import com.pennant.backend.model.extendedfield.ExtendedFieldHeader;
import com.pennant.backend.model.extendedfield.ExtendedFieldRender;
import com.pennant.backend.model.finance.ChequeHeader;
import com.pennant.backend.model.finance.FinAdvancePayments;
import com.pennant.backend.model.finance.FinFeeDetail;
import com.pennant.backend.model.finance.FinFeeReceipt;
import com.pennant.backend.model.finance.FinODPenaltyRate;
import com.pennant.backend.model.finance.FinPlanEmiHoliday;
import com.pennant.backend.model.finance.FinReceiptHeader;
import com.pennant.backend.model.finance.FinScheduleData;
import com.pennant.backend.model.finance.FinanceDetail;
import com.pennant.backend.model.finance.FinanceDisbursement;
import com.pennant.backend.model.finance.FinanceMain;
import com.pennant.backend.model.finance.FinanceScheduleDetail;
import com.pennant.backend.model.finance.FinanceStepPolicyDetail;
import com.pennant.backend.model.finance.FinanceSummary;
import com.pennant.backend.model.finance.GuarantorDetail;
import com.pennant.backend.model.finance.JointAccountDetail;
import com.pennant.backend.model.finance.ManualAdvise;
import com.pennant.backend.model.finance.financetaxdetail.FinanceTaxDetail;
import com.pennant.backend.model.financemanagement.FinFlagsDetail;
import com.pennant.backend.model.lmtmasters.FinanceWorkFlow;
import com.pennant.backend.model.mandate.Mandate;
import com.pennant.backend.model.rmtmasters.FinanceType;
import com.pennant.backend.model.solutionfactory.StepPolicyDetail;
import com.pennant.backend.model.solutionfactory.StepPolicyHeader;
import com.pennant.backend.service.bmtmasters.BankBranchService;
import com.pennant.backend.service.collateral.CollateralSetupService;
import com.pennant.backend.service.collateral.CollateralStructureService;
import com.pennant.backend.service.customermasters.CustomerAddresService;
import com.pennant.backend.service.customermasters.CustomerDetailsService;
import com.pennant.backend.service.extendedfields.ExtendedFieldDetailsService;
import com.pennant.backend.service.fees.FeeDetailService;
import com.pennant.backend.service.finance.FinAdvancePaymentsService;
import com.pennant.backend.service.finance.FinanceDetailService;
import com.pennant.backend.service.finance.FinanceMainService;
import com.pennant.backend.service.finance.JointAccountDetailService;
import com.pennant.backend.service.lmtmasters.FinanceWorkFlowService;
import com.pennant.backend.service.mandate.FinMandateService;
import com.pennant.backend.util.CollateralConstants;
import com.pennant.backend.util.DisbursementConstants;
import com.pennant.backend.util.ExtendedFieldConstants;
import com.pennant.backend.util.FinanceConstants;
import com.pennant.backend.util.MandateConstants;
import com.pennant.backend.util.PennantApplicationUtil;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.RuleReturnType;
import com.pennant.backend.util.VASConsatnts;
import com.pennant.backend.util.WorkFlowUtil;
import com.pennanttech.pennapps.core.AppException;
import com.pennanttech.pennapps.core.InterfaceException;
import com.pennanttech.pennapps.core.engine.workflow.WorkflowEngine;
import com.pennanttech.pennapps.core.model.ErrorDetail;
import com.pennanttech.pennapps.core.model.LoggedInUser;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pff.core.TableType;
import com.pennanttech.pff.document.DocumentService;
import com.pennanttech.util.APIConstants;
import com.pennanttech.ws.model.financetype.FinInquiryDetail;
import com.pennanttech.ws.model.financetype.FinanceInquiry;
import com.pennanttech.ws.service.APIErrorHandlerService;

public class CreateFinanceController extends SummaryDetailService {

	private static final Logger logger = Logger.getLogger(CreateFinanceController.class);

	private FinanceScheduleDetailDAO financeScheduleDetailDAO;
	private CustomerDetailsService customerDetailsService;
	private FinanceDetailService financeDetailService;
	private StepPolicyDetailDAO stepPolicyDetailDAO;
	private StepPolicyHeaderDAO stepPolicyHeaderDAO;
	private BankBranchService bankBranchService;
	private FeeDetailService feeDetailService;
	private CollateralSetupService collateralSetupService;
	private FinanceMainService financeMainService;
	private JointAccountDetailService jointAccountDetailService;
	private FinAdvancePaymentsService finAdvancePaymentsService;
	private CustomerAddresService customerAddresService;
	private ManualAdviseDAO manualAdviseDAO;
	private FinanceReferenceDetailDAO financeReferenceDetailDAO;
	private FinanceWorkFlowService financeWorkFlowService;
	private FinPlanEmiHolidayDAO finPlanEmiHolidayDAO;
	private ExtendedFieldHeaderDAO extendedFieldHeaderDAO;
	private FinMandateService finMandateService;
	private AuditHeaderDAO auditHeaderDAO;
	private FinanceMainDAO financeMainDAO;
	private ExtendedFieldDetailsService extendedFieldDetailsService;
	private FinAdvancePaymentsDAO finAdvancePaymentsDAO;
	private DocumentDetailsDAO documentDetailsDAO;
	private DocumentService documentService;
	private DivisionDetailDAO divisionDetailDAO;
	private FinReceiptHeaderDAO finReceiptHeaderDAO;
	private FinFeeReceiptDAO finFeeReceiptDAO;
	protected transient WorkflowEngine workFlow = null;
	private CollateralStructureService collateralStructureService;
	private RuleExecutionUtil ruleExecutionUtil;

	
	/**
	 * Method for process create finance request
	 * 
	 * @param financeDetail
	 * @return
	 */

	public FinanceDetail doCreateFinance(FinanceDetail financeDetail, boolean loanWithWIF) {
		logger.debug("Entering");

		String finReference = null;

		try {
			// financeMain details
			FinScheduleData finScheduleData = financeDetail.getFinScheduleData();
			FinanceMain financeMain = finScheduleData.getFinanceMain();
			FinanceType financeType = finScheduleData.getFinanceType();
			financeMain.setFinType(finScheduleData.getFinanceType().getFinType());
			if (financeType.isFinIsGenRef()) {
				financeMain.setFinReference(null);
			}
			if (StringUtils.isBlank(financeMain.getFinReference())) {
				finReference = String.valueOf(String
						.valueOf(ReferenceGenerator.generateFinRef(financeMain, finScheduleData.getFinanceType())));
			} else {
				finReference = financeMain.getFinReference();

			}
			// user language
			LoggedInUser userDetails = SessionUserDetails.getUserDetails(SessionUserDetails.getLogiedInUser());
			financeMain.setUserDetails(userDetails);

			financeMain.setFinReference(finReference);
			finScheduleData.setFinReference(financeMain.getFinReference());
			WorkFlowDetails workFlowDetails = null;
			String roleCode = null;
			String taskid = null;
			boolean stp = financeDetail.isStp();
			long workFlowId = 0;
			if (financeMain.isQuickDisb()) {
				String finType = financeMain.getFinType();
				int finRefType = FinanceConstants.PROCEDT_LIMIT;
				String quickDisbCode = FinanceConstants.QUICK_DISBURSEMENT;
				String roles = financeReferenceDetailDAO.getAllowedRolesByCode(finType, finRefType, quickDisbCode);
				if (StringUtils.isBlank(roles)) {
					FinanceDetail response = new FinanceDetail();
					doEmptyResponseObject(response);
					response.setReturnStatus(APIErrorHandlerService.getFailedStatus("90344"));
					return response;
				}
				roleCode = null;
				String[] role = roles.split(PennantConstants.DELIMITER_COMMA);
				for (String roleCod : role) {
					roleCode = roleCod;
					break;
				}
				String finEvent = FinanceConstants.FINSER_EVENT_ORG;

				FinanceWorkFlow financeWorkFlow = financeWorkFlowService.getApprovedFinanceWorkFlowById(
						financeMain.getFinType(), finEvent, PennantConstants.WORFLOW_MODULE_FINANCE);
				if (financeWorkFlow != null) {
					workFlowDetails = WorkFlowUtil.getDetailsByType(financeWorkFlow.getWorkFlowType());
					if (workFlowDetails != null) {
						WorkflowEngine workflow = new WorkflowEngine(workFlowDetails.getWorkFlowXml());
						taskid = workflow.getUserTaskId(roleCode);
						workFlowId = workFlowDetails.getWorkFlowId();
					}
				}

			}
			financeMain.setRecordStatus(getRecordStatus(financeMain.isQuickDisb(), financeDetail.isStp()));
			if (!stp) {
				financeDetail = nonStpProcess(financeDetail);
				if (financeDetail.getReturnStatus() == null) {
					taskid = financeMain.getTaskId();
					roleCode = financeMain.getRoleCode();
					workFlowId = financeMain.getWorkflowId();
				} else {
					return financeDetail;
				}
			}

			financeMain.setRecordType(PennantConstants.RECORD_TYPE_NEW);
			financeMain.setWorkflowId(workFlowId);
			financeMain.setRoleCode(roleCode);
			financeMain.setNextRoleCode(roleCode);
			financeMain.setTaskId(taskid);
			financeMain.setNextTaskId(getNextTaskId(taskid, financeMain.isQuickDisb(), stp));
			financeMain.setNewRecord(true);
			financeMain.setLastMntOn(new Timestamp(System.currentTimeMillis()));
			financeMain.setLastMntBy(getLastMntBy(financeMain.isQuickDisb(), userDetails));
			financeMain.setRecordStatus(getRecordStatus(financeMain.isQuickDisb(), stp));
			financeMain.setFinSourceID(PennantConstants.FINSOURCE_ID_API);

			finScheduleData.setFinanceMain(financeMain);

			// set required mandatory values into finance details object

			doSetRequiredDetails(financeDetail, loanWithWIF, userDetails, stp, false);

			if (financeDetail.getFinScheduleData().getExternalReference() != null
					&& !financeDetail.getFinScheduleData().getExternalReference().isEmpty()) {
				if (financeDetail.getFinScheduleData().isUpfrontAuto()) {
					adjustFeesAuto(finScheduleData);
				} else {

					adjustFees(finScheduleData);
				}
			}

			if (financeDetail.getFinScheduleData().getErrorDetails() != null) {
				for (ErrorDetail errorDetail : financeDetail.getFinScheduleData().getErrorDetails()) {
					FinanceDetail response = new FinanceDetail();
					doEmptyResponseObject(response);
					response.setReturnStatus(
							APIErrorHandlerService.getFailedStatus(errorDetail.getCode(), errorDetail.getError()));
					return response;
				}
			}

			if (!loanWithWIF) {
				// call schedule calculator
				finScheduleData.getFinanceMain().setCalculateRepay(true);
				if (StringUtils.equals(FinanceConstants.PRODUCT_ODFACILITY,
						financeDetail.getFinScheduleData().getFinanceMain().getProductCategory())) {
					if (financeDetail.getFinScheduleData().getOverdraftScheduleDetails() != null) {
						financeDetail.getFinScheduleData().getOverdraftScheduleDetails().clear();
					}
					//To Rebuild the overdraft if any fields are changed
					financeDetail.getFinScheduleData().getFinanceMain()
							.setEventFromDate(financeDetail.getFinScheduleData().getFinanceMain().getFinStartDate());
					finScheduleData = ScheduleCalculator.buildODSchedule(financeDetail.getFinScheduleData());
					financeDetail.setFinScheduleData(finScheduleData);
					financeDetail.getFinScheduleData().getFinanceMain().setLovDescIsSchdGenerated(true);

				} else {
					finScheduleData = ScheduleGenerator.getNewSchd(finScheduleData);
					if (finScheduleData.getFinanceScheduleDetails().size() != 0) {
						finScheduleData = ScheduleCalculator.getCalSchd(finScheduleData, BigDecimal.ZERO);
						finScheduleData.setSchduleGenerated(true);
						// process planned EMI details
						doProcessPlanEMIHDays(finScheduleData);
					}
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

				// fees calculation
				if (!finScheduleData.getFinFeeDetailList().isEmpty()) {
					finScheduleData = FeeScheduleCalculator.feeSchdBuild(finScheduleData);
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
				schdDetail.setRecordStatus(getRecordStatus(financeMain.isQuickDisb(), stp));
				schdDetail.setWorkflowId(workFlowId);
				schdDetail.setRoleCode(roleCode);
				schdDetail.setNextRoleCode(roleCode);
				schdDetail.setTaskId(taskid);
				schdDetail.setNextTaskId(financeMain.getNextTaskId());
			}

			// Finance detail object
			financeDetail.setUserAction("");
			financeDetail.setExtSource(false);
			financeDetail.setAccountingEventCode(PennantApplicationUtil.getEventCode(financeMain.getFinStartDate()));
			financeDetail.setFinReference(financeMain.getFinReference());
			if (!StringUtils.equals(FinanceConstants.PRODUCT_ODFACILITY,
					financeDetail.getFinScheduleData().getFinanceMain().getProductCategory())) {
				financeDetail.setFinScheduleData(finScheduleData);
			}

			AuditDetail auditDetail = new AuditDetail(PennantConstants.TRAN_WF, 1, null, financeDetail);
			AuditHeader auditHeader = new AuditHeader(financeDetail.getFinReference(), null, null, null, auditDetail,
					financeMain.getUserDetails(), new HashMap<String, ArrayList<ErrorDetail>>());

			APIHeader reqHeaderDetails = (APIHeader) PhaseInterceptorChain.getCurrentMessage().getExchange()
					.get(APIHeader.API_HEADER_KEY);
			auditHeader.setApiHeader(reqHeaderDetails);

			// save the finance details into main table
			if (stp && !financeMain.isQuickDisb()) {
				auditHeader = financeDetailService.doApprove(auditHeader, false);
			} else if (financeMain.isQuickDisb() || !stp) {
				String usrAction = null;
				String role = null;
				if (ImplementationConstants.CLIENT_NFL) {
					usrAction = "Approve";
					financeMain.setRecordStatus("Approve");
					role = workFlow.firstTaskOwner();
				} else {
					usrAction = "Save";
					financeMain.setRecordStatus("Saved");
					role = workFlow.firstTaskOwner();
				}

				auditHeader = financeDetailService.executeWorkflowServiceTasks(auditHeader, role, usrAction, workFlow);

			}

			FinanceDetail response = null;
			if (auditHeader.getOverideMessage() != null && auditHeader.getOverideMessage().size() > 0) {
				for (ErrorDetail errorDetail : auditHeader.getOverideMessage()) {
					response = new FinanceDetail();
					doEmptyResponseObject(response);
					response.setReturnStatus(
							APIErrorHandlerService.getFailedStatus(errorDetail.getCode(), errorDetail.getError()));
					return response;
				}
			}
			if (auditHeader.getErrorMessage() != null) {
				for (ErrorDetail errorDetail : auditHeader.getErrorMessage()) {
					response = new FinanceDetail();
					doEmptyResponseObject(response);
					response.setReturnStatus(
							APIErrorHandlerService.getFailedStatus(errorDetail.getCode(), errorDetail.getError()));
					return response;
				}
			}

			if (auditHeader.getAuditDetail().getErrorDetails() != null) {
				for (ErrorDetail errorDetail : auditHeader.getAuditDetail().getErrorDetails()) {
					response = new FinanceDetail();
					doEmptyResponseObject(response);
					response.setReturnStatus(
							APIErrorHandlerService.getFailedStatus(errorDetail.getCode(), errorDetail.getError()));
					return response;
				}
			}

			if (StringUtils.isNotBlank(finReference)) {
				// prepare response object
				response = getFinanceDetailResponse(auditHeader);
				response.setStp(false);
				response.setReturnStatus(APIErrorHandlerService.getSuccessStatus());

				logger.debug("Leaving");
				return response;
			}
		} catch (InterfaceException ex) {
			logger.error("InterfaceException", ex);
			FinanceDetail response = new FinanceDetail();
			doEmptyResponseObject(response);
			response.setReturnStatus(APIErrorHandlerService.getFailedStatus("9999", ex.getMessage()));
			return response;
		} catch (AppException ex) {
			logger.error("AppException", ex);
			FinanceDetail response = new FinanceDetail();
			doEmptyResponseObject(response);
			response.setReturnStatus(APIErrorHandlerService.getFailedStatus("9999", ex.getMessage()));
			return response;
		} catch (Exception e) {
			logger.error("Exception: ", e);
			APIErrorHandlerService.logUnhandledException(e);
			FinanceDetail response = new FinanceDetail();
			doEmptyResponseObject(response);
			response.setReturnStatus(APIErrorHandlerService.getFailedStatus());
			return response;
		}
		return null;
	}

	private FinanceDetail nonStpProcess(FinanceDetail financeDetail) {
		logger.debug(Literal.ENTERING);
		FinanceMain financeMain = financeDetail.getFinScheduleData().getFinanceMain();
		String finEvent = FinanceConstants.FINSER_EVENT_ORG;
		FinanceWorkFlow financeWorkFlow = financeWorkFlowService.getApprovedFinanceWorkFlowById(
				financeMain.getFinType(), finEvent, PennantConstants.WORFLOW_MODULE_FINANCE);
		WorkFlowDetails workFlowDetails = null;
		String processStage = financeDetail.getProcessStage();
		financeDetail.setActionSave(true);
		if (financeWorkFlow != null) {
			workFlowDetails = WorkFlowUtil.getDetailsByType(financeWorkFlow.getWorkFlowType());
			workFlow = new WorkflowEngine(workFlowDetails.getWorkFlowXml());
		} else {
			FinanceDetail response = new FinanceDetail();
			doEmptyResponseObject(response);
			String[] valueParm = new String[2];
			valueParm[0] = financeMain.getFinType();
			valueParm[1] = "workflow";
			response.setReturnStatus(APIErrorHandlerService.getFailedStatus("90339", valueParm));
			return response;
		}

		if (StringUtils.isBlank(processStage)) {
			processStage = workFlow.firstTaskOwner();
		}

		if (workFlowDetails != null) {
			String taskId = workFlow.getUserTaskId(processStage);
			long workflowId = workFlowDetails.getWorkFlowId();
			String roleCode = processStage;
			financeMain.setTaskId(taskId);
			financeMain.setRoleCode(roleCode);
			financeMain.setWorkflowId(workflowId);
		}

		logger.debug(Literal.LEAVING);
		return financeDetail;
	}

	private long getLastMntBy(boolean quickDisb, LoggedInUser userDetails) {
		if (!quickDisb) {
			return userDetails.getUserId();
		} else {
			return 0;
		}
	}

	private String getRecordStatus(boolean quickDisb, boolean stp) {
		if (stp && !quickDisb) {
			return PennantConstants.RCD_STATUS_APPROVED;
		} else {
			if (ImplementationConstants.CLIENT_NFL) {
				return PennantConstants.RCD_STATUS_SUBMITTED;
			} else {
				return PennantConstants.RCD_STATUS_SAVED;
			}
		}
	}

	/**
	 * prepare finance detail object with required data to process finance origination.<br>
	 * 
	 * @param financeDetail
	 * @param loanWithWIF
	 * @throws InvocationTargetException
	 * @throws IllegalAccessException
	 */

	private void doSetRequiredDetails(FinanceDetail financeDetail, boolean loanWithWIF, LoggedInUser userDetails,
			boolean stp, boolean approve) throws IllegalAccessException, InvocationTargetException {
		logger.debug("Entering");

		financeDetail.setModuleDefiner(FinanceConstants.FINSER_EVENT_ORG);
		financeDetail.setUserDetails(userDetails);
		financeDetail.setNewRecord(true);
		FinScheduleData finScheduleData = financeDetail.getFinScheduleData();
		FinanceMain financeMain = finScheduleData.getFinanceMain();
		financeMain.setVersion(1);
		financeMain.setFinIsActive(true);
		financeMain.setFinStatus(financeDetailService.getCustStatusByMinDueDays());

		if (financeMain.getMaturityDate() == null) {
			financeMain.setMaturityDate(financeMain.getCalMaturity());
		}
		if (financeMain.getNumberOfTerms() <= 0) {
			financeMain.setNumberOfTerms(financeMain.getCalTerms());
		}
		if (financeMain.getGrcPeriodEndDate() == null) {
			financeMain.setGrcPeriodEndDate(financeMain.getCalGrcEndDate());
		}
		if (financeMain.getGraceTerms() <= 0) {
			financeMain.setGraceTerms(financeMain.getCalGrcTerms());
		}
		financeMain.setFinCurrAssetValue(financeMain.getFinAmount());

		// set Head branch
		if (StringUtils.isBlank(financeMain.getFinBranch())) {
			financeMain.setFinBranch(userDetails.getBranchCode());
		}
		CustomerDetails customerDetails = null;
		// setting required values which are not received from API
		if (financeMain.getCustID() > 0) {
			customerDetails = customerDetailsService.getCustomerDetailsById(financeMain.getCustID(), true, "");
			if (customerDetails != null) {
				customerDetails.setUserDetails(userDetails);
				financeDetail.setCustomerDetails(customerDetails);
			}
		}

		// process disbursement details
		doProcessDisbInstructions(financeDetail);

		// set finAssetValue = FinCurrAssetValue when there is no maxDisbCheck
		FinanceType finType = financeDetail.getFinScheduleData().getFinanceType();
		// This is not applicable for Over Draft
		if (!financeDetail.getFinScheduleData().getFinanceMain().getProductCategory()
				.equals(FinanceConstants.PRODUCT_ODFACILITY)) {
			if (!finType.isAlwMaxDisbCheckReq()) {
				financeMain.setFinAssetValue(financeMain.getFinCurrAssetValue());//FIXME: override actual finAsset value
			}
		}

		//vas Details
		for (VASRecording vasRecording : finScheduleData.getVasRecordingList()) {
			vasRecording.setRecordType(PennantConstants.RECORD_TYPE_NEW);
			vasRecording.setNewRecord(true);
			vasRecording.setVersion(1);
			vasRecording.setRecordStatus(getRecordStatus(financeMain.isQuickDisb(), stp));
			vasRecording.setVasReference(ReferenceUtil.generateVASRef());
			vasRecording.setPostingAgainst(VASConsatnts.VASAGAINST_FINANCE);
			vasRecording.setVasStatus("N");
			//workflow related
			vasRecording.setWorkflowId(financeMain.getWorkflowId());
			vasRecording.setRoleCode(financeMain.getRoleCode());
			vasRecording.setNextRoleCode(financeMain.getNextRoleCode());
			vasRecording.setTaskId(financeMain.getTaskId());
			vasRecording.setNextTaskId(financeMain.getNextTaskId());
			// process Extended field details
			List<ExtendedField> extendedFields = vasRecording.getExtendedDetails();
			if (extendedFields != null) {
				int seqNo = 0;
				ExtendedFieldRender exdFieldRender = new ExtendedFieldRender();
				exdFieldRender.setReference(vasRecording.getVasReference());
				exdFieldRender.setLastMntOn(new Timestamp(System.currentTimeMillis()));
				exdFieldRender.setRecordStatus(getRecordStatus(financeMain.isQuickDisb(), stp));
				exdFieldRender.setLastMntBy(userDetails.getUserId());
				exdFieldRender.setSeqNo(++seqNo);
				exdFieldRender.setNewRecord(true);
				exdFieldRender.setRecordType(PennantConstants.RECORD_TYPE_NEW);
				exdFieldRender.setVersion(1);
				//workflow related
				exdFieldRender.setWorkflowId(financeMain.getWorkflowId());
				exdFieldRender.setRoleCode(financeMain.getRoleCode());
				exdFieldRender.setNextRoleCode(financeMain.getNextRoleCode());
				exdFieldRender.setTaskId(financeMain.getTaskId());
				exdFieldRender.setNextTaskId(financeMain.getNextTaskId());
				for (ExtendedField extendedField : extendedFields) {
					Map<String, Object> mapValues = new HashMap<String, Object>();
					for (ExtendedFieldData extFieldData : extendedField.getExtendedFieldDataList()) {
						mapValues.put(extFieldData.getFieldName(), extFieldData.getFieldValue());
						exdFieldRender.setMapValues(mapValues);
					}

				}
				if (extendedFields.size() <= 0) {
					Map<String, Object> mapValues = new HashMap<String, Object>();
					exdFieldRender.setMapValues(mapValues);
				}

				vasRecording.setExtendedFieldRender(exdFieldRender);
			} else {
				ExtendedFieldRender exdFieldRender = new ExtendedFieldRender();
				exdFieldRender.setReference(vasRecording.getVasReference());
				exdFieldRender.setLastMntOn(new Timestamp(System.currentTimeMillis()));
				exdFieldRender.setRecordStatus(getRecordStatus(financeMain.isQuickDisb(), stp));
				exdFieldRender.setLastMntBy(userDetails.getUserId());
				exdFieldRender.setSeqNo(0);
				exdFieldRender.setNewRecord(true);
				exdFieldRender.setRecordType(PennantConstants.RECORD_TYPE_NEW);
				exdFieldRender.setVersion(1);
				Map<String, Object> mapValues = new HashMap<String, Object>();
				exdFieldRender.setMapValues(mapValues);
				vasRecording.setExtendedFieldRender(exdFieldRender);
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
				flagDetail.setVersion(1);
				flagDetail.setLastMntBy(userDetails.getUserId());
				flagDetail.setLastMntOn(new Timestamp(System.currentTimeMillis()));
				flagDetail.setRecordStatus(getRecordStatus(financeMain.isQuickDisb(), stp));
				flagDetail.setUserDetails(financeMain.getUserDetails());
				//workflow related
				flagDetail.setWorkflowId(financeMain.getWorkflowId());
				flagDetail.setRoleCode(financeMain.getRoleCode());
				flagDetail.setNextRoleCode(financeMain.getNextRoleCode());
				flagDetail.setTaskId(financeMain.getTaskId());
				flagDetail.setNextTaskId(financeMain.getNextTaskId());
			}
		}

		// process mandate details
		doProcessMandate(financeDetail);

		// co-applicant details
		for (JointAccountDetail jointAccDetail : financeDetail.getJountAccountDetailList()) {
			jointAccDetail.setFinReference(financeMain.getFinReference());
			jointAccDetail.setRecordType(PennantConstants.RECORD_TYPE_NEW);
			jointAccDetail.setNewRecord(true);
			jointAccDetail.setLastMntBy(userDetails.getUserId());
			jointAccDetail.setLastMntOn(new Timestamp(System.currentTimeMillis()));
			jointAccDetail.setRecordStatus(getRecordStatus(financeMain.isQuickDisb(), stp));
			jointAccDetail.setUserDetails(financeMain.getUserDetails());
			jointAccDetail.setVersion(1);
			//workflow
			jointAccDetail.setWorkflowId(financeMain.getWorkflowId());
			jointAccDetail.setRoleCode(financeMain.getRoleCode());
			jointAccDetail.setNextRoleCode(financeMain.getNextRoleCode());
			jointAccDetail.setTaskId(financeMain.getTaskId());
			jointAccDetail.setNextTaskId(financeMain.getNextTaskId());

			Customer coApplicant = customerDetailsService.getCustomerByCIF(jointAccDetail.getCustCIF());
			if (coApplicant != null) {
				jointAccDetail.setCustID(coApplicant.getCustID());
			}
		}

		// guarantor details
		for (GuarantorDetail guarantorDetail : financeDetail.getGurantorsDetailList()) {
			if (guarantorDetail.isBankCustomer()) {
				List<CustomerAddres> address = customerAddresService
						.getApprovedCustomerAddresById(guarantorDetail.getCustID());
				if (address != null && !address.isEmpty()) {
					CustomerAddres customerAddress = address.get(0);
					guarantorDetail.setAddrCity(customerAddress.getCustAddrCity());
					guarantorDetail.setAddrCountry(customerAddress.getCustAddrCountry());
					guarantorDetail.setAddrHNbr(customerAddress.getCustAddrHNbr());
					guarantorDetail.setAddrLine1(customerAddress.getCustAddrLine1());
					guarantorDetail.setAddrLine2(customerAddress.getCustAddrLine2());
					guarantorDetail.setAddrProvince(customerAddress.getCustAddrProvince());
					guarantorDetail.setAddrStreet(customerAddress.getCustAddrStreet());
					guarantorDetail.setAddrZIP(customerAddress.getCustAddrZIP());
					guarantorDetail.setPOBox(customerAddress.getCustPOBox());
					guarantorDetail.setFlatNbr(customerAddress.getCustFlatNbr());
				}
			}
			guarantorDetail.setFinReference(financeMain.getFinReference());
			guarantorDetail.setRecordType(PennantConstants.RECORD_TYPE_NEW);
			guarantorDetail.setNewRecord(true);
			guarantorDetail.setLastMntBy(userDetails.getUserId());
			guarantorDetail.setLastMntOn(new Timestamp(System.currentTimeMillis()));
			guarantorDetail.setRecordStatus(getRecordStatus(financeMain.isQuickDisb(), stp));
			guarantorDetail.setUserDetails(financeMain.getUserDetails());
			guarantorDetail.setVersion(1);
			//workflow
			guarantorDetail.setWorkflowId(financeMain.getWorkflowId());
			guarantorDetail.setRoleCode(financeMain.getRoleCode());
			guarantorDetail.setNextRoleCode(financeMain.getNextRoleCode());
			guarantorDetail.setTaskId(financeMain.getTaskId());
			guarantorDetail.setNextTaskId(financeMain.getNextTaskId());
		}

		// document details
		for (DocumentDetails detail : financeDetail.getDocumentDetailsList()) {
			detail.setNewRecord(true);
			detail.setRecordType(PennantConstants.RECORD_TYPE_NEW);
			detail.setDocModule(FinanceConstants.MODULE_NAME);
			detail.setUserDetails(financeMain.getUserDetails());
			detail.setVersion(1);
			detail.setRecordStatus(getRecordStatus(financeMain.isQuickDisb(), stp));
			//workflow relates
			detail.setWorkflowId(financeMain.getWorkflowId());
			detail.setRoleCode(financeMain.getRoleCode());
			detail.setNextRoleCode(financeMain.getNextRoleCode());
			detail.setTaskId(financeMain.getTaskId());
			detail.setNextTaskId(financeMain.getNextTaskId());
		}

		financeDetail.setFinScheduleData(finScheduleData);
		if (financeMain.getCustID() > 0) {
			CustomerDetails custDetails = customerDetailsService.getApprovedCustomerById(financeMain.getCustID());
			financeDetail.setCustomerDetails(custDetails);
		}

		// CollateralAssignment details
		for (CollateralAssignment detail : financeDetail.getCollateralAssignmentList()) {

			if (StringUtils.isNotBlank(detail.getCollateralRef())) {
				CollateralSetup collateralSetup = collateralSetupService
						.getApprovedCollateralSetupById(detail.getCollateralRef());
				if (collateralSetup != null) {
					detail.setCollateralValue(collateralSetup.getCollateralValue());
				}
				detail.setNewRecord(true);
				detail.setRecordType(PennantConstants.RECORD_TYPE_NEW);
				detail.setModule(FinanceConstants.MODULE_NAME);
				detail.setUserDetails(financeMain.getUserDetails());
				detail.setLastMntBy(userDetails.getUserId());
				detail.setLastMntOn(new Timestamp(System.currentTimeMillis()));
				detail.setRecordStatus(getRecordStatus(financeMain.isQuickDisb(), stp));
				detail.setVersion(1);
				//workflow relates
				detail.setWorkflowId(financeMain.getWorkflowId());
				detail.setRoleCode(financeMain.getRoleCode());
				detail.setNextRoleCode(financeMain.getNextRoleCode());
				detail.setTaskId(financeMain.getTaskId());
				detail.setNextTaskId(financeMain.getNextTaskId());
			} 
			
		}
		if (CollectionUtils.isNotEmpty(financeDetail.getCollaterals())) {
			BigDecimal curAssignValue = BigDecimal.ZERO;
			BigDecimal totalAvailAssignValue = BigDecimal.ZERO;

			for (CollateralAssignment detail : financeDetail.getCollateralAssignmentList()) {
				for (CollateralSetup collsetup : financeDetail.getCollaterals()) {
					if (StringUtils.equals(detail.getAssignmentReference(), collsetup.getAssignmentReference())) {
						processCollateralsetupDetails(userDetails, stp, financeMain, customerDetails, detail,
								collsetup);
						curAssignValue = curAssignValue.add(collsetup.getBankValuation()
								.multiply(detail.getAssignPerc() == null ? BigDecimal.ZERO : detail.getAssignPerc())
								.divide(new BigDecimal(100), 0, RoundingMode.HALF_DOWN));
						totalAvailAssignValue = totalAvailAssignValue
								.add(collsetup.getBankValuation().subtract(curAssignValue));
					}
				}
			}

			//Collateral coverage will be calculated based on the flag "Partially Secured?” defined loan type.
			if (!financeDetail.getFinScheduleData().getFinanceType().isPartiallySecured()) {
				if (curAssignValue.compareTo(financeMain.getFinAmount()) < 0) {
					String[] valueParm = new String[2];
					valueParm[0] = "Collateral available assign value(" + String.valueOf(curAssignValue) + ")";
					valueParm[1] = "current assign value(" + financeMain.getFinAmount() + ")";
					finScheduleData.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetail("65012", valueParm)));
				}

				if (PennantConstants.COLLATERAL_LTV_CHECK_FINAMT
						.equals(financeDetail.getFinScheduleData().getFinanceType().getFinLTVCheck())) {
					if (totalAvailAssignValue.compareTo(financeMain.getFinAssetValue()) < 0) {
						String[] valueParm = new String[2];
						valueParm[0] = "Available assign value(" + String.valueOf(totalAvailAssignValue) + ")";
						valueParm[1] = "loan amount(" + String.valueOf(financeMain.getFinAssetValue()) + ")";
						finScheduleData.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetail("65012", valueParm)));
					}
				} else {
					if (totalAvailAssignValue.compareTo(financeMain.getFinAmount()) < 0) {
						String[] valueParm = new String[2];
						valueParm[0] = "Available assign value(" + String.valueOf(totalAvailAssignValue) + ")";
						valueParm[1] = "loan amount(" + String.valueOf(financeMain.getFinAmount()) + ")";
						finScheduleData.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetail("65012", valueParm)));
					}
				}
			}
		}
		

		// Set VAS reference as feeCode for VAS related fees
		for (FinFeeDetail feeDetail : finScheduleData.getFinFeeDetailList()) {
			for (VASRecording vasRecording : finScheduleData.getVasRecordingList()) {
				if (StringUtils.equals(feeDetail.getFinEvent(), AccountEventConstants.ACCEVENT_VAS_FEE)
						&& StringUtils.contains(feeDetail.getFeeTypeCode(), vasRecording.getProductCode())) {
					feeDetail.setFeeTypeCode(vasRecording.getVasReference());
					feeDetail.setVasReference(vasRecording.getVasReference());
					feeDetail.setCalculatedAmount(vasRecording.getFee());
					feeDetail.setFixedAmount(vasRecording.getFee());
					feeDetail.setAlwDeviation(true);
					feeDetail.setMaxWaiverPerc(BigDecimal.valueOf(100));
					//feeDetail.setAlwModifyFee(true);
					feeDetail.setAlwModifyFeeSchdMthd(true);
					feeDetail.setCalculationType(PennantConstants.FEE_CALCULATION_TYPE_FIXEDAMOUNT);
					//Fee Details set to the VasRecording
					vasRecording.setWaivedAmt(feeDetail.getWaivedAmount());
					vasRecording.setPaidAmt(feeDetail.getPaidAmount());
				}
			}
		}

		if (financeDetail.getFinanceTaxDetail() != null) {
			FinanceTaxDetail financeTaxDetail = financeDetail.getFinanceTaxDetail();
			financeTaxDetail.setFinReference(financeMain.getFinReference());
			financeTaxDetail.setRecordType(PennantConstants.RECORD_TYPE_NEW);
			financeTaxDetail.setNewRecord(true);
			financeTaxDetail.setLastMntBy(userDetails.getUserId());
			financeTaxDetail.setLastMntOn(new Timestamp(System.currentTimeMillis()));
			financeTaxDetail.setRecordStatus(getRecordStatus(financeMain.isQuickDisb(), stp));
			financeTaxDetail.setUserDetails(financeMain.getUserDetails());
			financeTaxDetail.setVersion(1);
			financeTaxDetail.setTaskId(financeMain.getTaskId());
			financeTaxDetail.setNextTaskId(financeMain.getNextTaskId());
			financeTaxDetail.setRoleCode(financeMain.getRoleCode());
			financeTaxDetail.setNextRoleCode(financeMain.getNextRoleCode());
			financeTaxDetail.setWorkflowId(financeMain.getWorkflowId());
		}

		// execute fee charges
		String finEvent = "";
		boolean enquiry = true;
		if (financeDetail.getFinScheduleData() != null
				&& CollectionUtils.isNotEmpty(financeDetail.getFinScheduleData().getFinFeeDetailList())) {
			enquiry = false;
		}
		executeFeeCharges(financeDetail, finEvent, enquiry);

		if (financeDetail.getFinScheduleData().getFinFeeDetailList() != null) {
			for (FinFeeDetail feeDetail : financeDetail.getFinScheduleData().getFinFeeDetailList()) {
				feeDetail.setRecordType(PennantConstants.RECORD_TYPE_NEW);
				if (!stp || financeMain.isQuickDisb())
					feeDetail.setRecordStatus("");
				feeDetail.setRcdVisible(false);
				feeDetail.setVersion(1);
				feeDetail.setWorkflowId(financeMain.getWorkflowId());
			}
		}

		// validate disbursement instructions
		if (!loanWithWIF && !financeDetail.getFinScheduleData().getFinanceMain().getProductCategory()
				.equals(FinanceConstants.PRODUCT_ODFACILITY)) {
			if (!approve) {
				FinanceDisbursement disbursementDetails = new FinanceDisbursement();
				disbursementDetails.setDisbDate(financeMain.getFinStartDate());
				disbursementDetails.setDisbAmount(financeMain.getFinAmount());
				disbursementDetails.setVersion(1);
				disbursementDetails.setDisbSeq(1);
				disbursementDetails.setDisbReqDate(DateUtility.getAppDate());
				disbursementDetails.setFeeChargeAmt(financeMain.getFeeChargeAmt());
				disbursementDetails.setInsuranceAmt(financeMain.getInsuranceAmt());
				disbursementDetails
						.setDisbAccountId(PennantApplicationUtil.unFormatAccountNumber(financeMain.getDisbAccountId()));
				finScheduleData.getDisbursementDetails().add(disbursementDetails);
			}
		}

		// validate Disbursement instruction total amount
		if (financeDetail.getAdvancePaymentsList() != null && financeDetail.getAdvancePaymentsList().size() > 0) {
			validateDisbInstAmount(financeDetail);
		}

		// Step Policy Details
		if (financeMain.isStepFinance()) {
			String stepPolicyCode = financeMain.getStepPolicy();
			if (StringUtils.isNotBlank(stepPolicyCode)) {
				List<StepPolicyDetail> stepPolicyList = stepPolicyDetailDAO.getStepPolicyDetailListByID(stepPolicyCode,
						"_AView");

				// reset step policy details
				finScheduleData.resetStepPolicyDetails(stepPolicyList);

				finScheduleData.getFinanceMain().setStepFinance(true);
				finScheduleData.getFinanceMain().setStepPolicy(stepPolicyCode);

				// fetch stepHeader details
				StepPolicyHeader header = stepPolicyHeaderDAO.getStepPolicyHeaderByID(stepPolicyCode, "");
				if (header != null) {
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

		// process Extended field details
		// Get the ExtendedFieldHeader for given module and subModule
		//### 02-05-2018-Start- story #334 Extended fields for loan servicing
		ExtendedFieldHeader extendedFieldHeader = extendedFieldHeaderDAO.getExtendedFieldHeaderByModuleName(
				ExtendedFieldConstants.MODULE_LOAN, financeMain.getFinCategory(), FinanceConstants.FINSER_EVENT_ORG,
				"");
		//### 02-05-2018-END

		financeDetail.setExtendedFieldHeader(extendedFieldHeader);

		List<ExtendedField> extendedFields = financeDetail.getExtendedDetails();
		if (extendedFieldHeader != null) {
			int seqNo = 0;
			ExtendedFieldRender exdFieldRender = new ExtendedFieldRender();
			exdFieldRender.setReference(financeMain.getFinReference());
			exdFieldRender.setLastMntOn(new Timestamp(System.currentTimeMillis()));
			exdFieldRender.setRecordStatus(PennantConstants.RCD_STATUS_APPROVED);
			exdFieldRender.setLastMntBy(userDetails.getUserId());
			exdFieldRender.setSeqNo(++seqNo);
			exdFieldRender.setNewRecord(true);
			exdFieldRender.setRecordType(PennantConstants.RECORD_TYPE_NEW);
			exdFieldRender.setVersion(1);
			exdFieldRender.setTypeCode(financeDetail.getExtendedFieldHeader().getSubModuleName());

			if (extendedFields != null) {
				for (ExtendedField extendedField : extendedFields) {
					Map<String, Object> mapValues = new HashMap<String, Object>();
					for (ExtendedFieldData extFieldData : extendedField.getExtendedFieldDataList()) {
						mapValues.put(extFieldData.getFieldName(), extFieldData.getFieldValue());
						exdFieldRender.setMapValues(mapValues);
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

			financeDetail.setExtendedFieldRender(exdFieldRender);
		}

		//set's the default chequeHeader to the financeDetail if chequeCapture is required.
		if (MandateConstants.TYPE_PDC.equals(financeMain.getFinRepayMethod()) || finType.isChequeCaptureReq()) {
			doSetDefaultChequeHeader(financeDetail);
		}
		logger.debug("Leaving");
	}

	private void processCollateralsetupDetails(LoggedInUser userDetails, boolean stp, FinanceMain financeMain,
			CustomerDetails customerDetails, CollateralAssignment detail,CollateralSetup colSetup) {
		//collateral setup defaulting
		colSetup.setUserDetails(financeMain.getUserDetails());
		colSetup.setSourceId(APIConstants.FINSOURCE_ID_API);
		colSetup.setRecordStatus(getRecordStatus(financeMain.isQuickDisb(), stp));
		colSetup.setLastMntOn(new Timestamp(System.currentTimeMillis()));
		colSetup.setLastMntBy(userDetails.getUserId());
		colSetup.setCollateralRef(ReferenceUtil.generateCollateralRef());
		colSetup.setDepositorId(financeMain.getCustID());
		colSetup.setDepositorCif(financeMain.getCustCIF());
		colSetup.setNewRecord(true);
		colSetup.setRecordType(PennantConstants.RECORD_TYPE_NEW);
		colSetup.setWorkflowId(financeMain.getWorkflowId());
		colSetup.setRoleCode(financeMain.getRoleCode());
		colSetup.setNextRoleCode(financeMain.getNextRoleCode());
		colSetup.setTaskId(financeMain.getTaskId());
		colSetup.setNextTaskId(financeMain.getNextTaskId());	
		List<CollateralThirdParty> thirdPartyCollaterals = colSetup.getCollateralThirdPartyList();
		if (thirdPartyCollaterals != null) {
			for (CollateralThirdParty thirdPartyColl : thirdPartyCollaterals) {
				thirdPartyColl.setLastMntOn(new Timestamp(System.currentTimeMillis()));
				thirdPartyColl.setLastMntBy(userDetails.getUserId());
				thirdPartyColl.setRecordStatus(getRecordStatus(financeMain.isQuickDisb(), stp));
				thirdPartyColl.setUserDetails(userDetails);

				// fetch customer id from cif
				Customer thrdPartyCustomer = customerDetailsService
						.getCustomerByCIF(thirdPartyColl.getCustCIF());
				if (thrdPartyCustomer != null) {
					thirdPartyColl.setCustomerId(thrdPartyCustomer.getCustID());
				}
				thirdPartyColl.setCollateralRef(colSetup.getCollateralRef());
				thirdPartyColl.setNewRecord(true);
				thirdPartyColl.setRecordType(PennantConstants.RECORD_TYPE_NEW);
				thirdPartyColl.setWorkflowId(financeMain.getWorkflowId());
				thirdPartyColl.setRoleCode(financeMain.getRoleCode());
				thirdPartyColl.setNextRoleCode(financeMain.getNextRoleCode());
				thirdPartyColl.setTaskId(financeMain.getTaskId());
				thirdPartyColl.setNextTaskId(financeMain.getNextTaskId());	
			}
		}

		// process co-owner details
		List<CoOwnerDetail> coOwnerDetails = colSetup.getCoOwnerDetailList();
		if (coOwnerDetails != null) {
			int seqNo = 0;
			for (CoOwnerDetail coOwnerDetail : coOwnerDetails) {
				coOwnerDetail.setCollateralRef(colSetup.getCollateralRef());
				coOwnerDetail.setLastMntOn(new Timestamp(System.currentTimeMillis()));
				coOwnerDetail.setRecordStatus(getRecordStatus(financeMain.isQuickDisb(), stp));
				coOwnerDetail.setUserDetails(userDetails);
				coOwnerDetail.setLastMntBy(userDetails.getUserId());

				Customer coOwnerCustomer = customerDetailsService
						.getCustomerByCIF(coOwnerDetail.getCoOwnerCIF());
				if (coOwnerCustomer != null) {
					coOwnerDetail.setCustomerId(coOwnerCustomer.getCustID());
				}

				coOwnerDetail.setCollateralRef(colSetup.getCollateralRef());
				coOwnerDetail.setNewRecord(true);
				coOwnerDetail.setRecordType(PennantConstants.RECORD_TYPE_NEW);
				coOwnerDetail.setCoOwnerId(++seqNo);
				coOwnerDetail.setWorkflowId(financeMain.getWorkflowId());
				coOwnerDetail.setRoleCode(financeMain.getRoleCode());
				coOwnerDetail.setNextRoleCode(financeMain.getNextRoleCode());
				coOwnerDetail.setTaskId(financeMain.getTaskId());
				coOwnerDetail.setNextTaskId(financeMain.getNextTaskId());	

			}

		}
		// get Collateral structure details
		CollateralStructure collateralStructure = null;
		String collateralType = colSetup.getCollateralType();
		if (StringUtils.isNotBlank(collateralType)) {
			collateralStructure = collateralStructureService
					.getApprovedCollateralStructureByType(collateralType);
		} else if (StringUtils.isNotBlank(colSetup.getCollateralRef())) {
			CollateralSetup setup = collateralSetupService
					.getApprovedCollateralSetupById(colSetup.getCollateralRef());
			if (setup != null) {
				collateralStructure = collateralStructureService
						.getApprovedCollateralStructureByType(setup.getCollateralType());
			}
		}
		colSetup.setCollateralStructure(collateralStructure);
		// process Extended field details
		int totalUnits = 0;
		BigDecimal totalValue = BigDecimal.ZERO;
		List<ExtendedField> extendedFields = colSetup.getExtendedDetails();
		if (extendedFields != null) {
			List<ExtendedFieldRender> extendedFieldRenderList = new ArrayList<ExtendedFieldRender>();
			int seqNo = 0;
			for (ExtendedField extendedField : extendedFields) {
				ExtendedFieldRender exdFieldRender = new ExtendedFieldRender();
				exdFieldRender.setReference(colSetup.getCollateralRef());
				exdFieldRender.setLastMntOn(new Timestamp(System.currentTimeMillis()));
				exdFieldRender.setRecordStatus(getRecordStatus(financeMain.isQuickDisb(), stp));
				exdFieldRender.setLastMntBy(userDetails.getUserId());

				exdFieldRender.setSeqNo(++seqNo);
				exdFieldRender.setNewRecord(true);
				exdFieldRender.setRecordType(PennantConstants.RECORD_TYPE_NEW);
				
				exdFieldRender.setWorkflowId(financeMain.getWorkflowId());
				exdFieldRender.setRoleCode(financeMain.getRoleCode());
				exdFieldRender.setNextRoleCode(financeMain.getNextRoleCode());
				exdFieldRender.setTaskId(financeMain.getTaskId());
				exdFieldRender.setNextTaskId(financeMain.getNextTaskId());	

				Map<String, Object> mapValues = new HashMap<String, Object>();
				int noOfUnits = 0;
				BigDecimal curValue = BigDecimal.ZERO;
				for (ExtendedFieldData extFieldData : extendedField.getExtendedFieldDataList()) {
					mapValues.put(extFieldData.getFieldName(), extFieldData.getFieldValue());
				}

				try {
					// Setting Number of units
					if (mapValues.containsKey("NOOFUNITS")) {
						noOfUnits = Integer.parseInt(mapValues.get("NOOFUNITS").toString());
						totalUnits = totalUnits + noOfUnits;
					}

					// Setting Total Value
					if (mapValues.containsKey("UNITPRICE")) {
						curValue = new BigDecimal(mapValues.get("UNITPRICE").toString());
						totalValue = totalValue.add(curValue.multiply(new BigDecimal(noOfUnits)));
					}
				} catch (NumberFormatException nfe) {
					APIErrorHandlerService.logUnhandledException(nfe);
					logger.error("Exception", nfe);
					throw nfe;
				}
				exdFieldRender.setMapValues(mapValues);
				extendedFieldRenderList.add(exdFieldRender);

			}
			colSetup.setCollateralValue(totalValue);
			colSetup.setExtendedFieldRenderList(extendedFieldRenderList);
		}

		if (collateralStructure != null) {

			// calculate BankLTV
			if (StringUtils.equals(collateralStructure.getLtvType(), CollateralConstants.FIXED_LTV)) {
				colSetup.setBankLTV(collateralStructure.getLtvPercentage());
			} else if (StringUtils.equals(collateralStructure.getLtvType(), CollateralConstants.VARIABLE_LTV)) {
				Object ruleResult = null;
				CustomerDetails custDetails = customerDetailsService.getCustomerById(colSetup.getDepositorId());
				if (custDetails != null) {
					colSetup.setCustomerDetails(customerDetails);
				}
				HashMap<String, Object> declaredMap = colSetup.getCustomerDetails().getCustomer()
						.getDeclaredFieldValues();
				declaredMap.put("collateralType", colSetup.getCollateralType());
				declaredMap.put("collateralCcy", colSetup.getCollateralCcy());
				try {
					ruleResult = ruleExecutionUtil.executeRule(collateralStructure.getSQLRule(), declaredMap,
							colSetup.getCollateralCcy(), RuleReturnType.DECIMAL);
				} catch (Exception e) {
					APIErrorHandlerService.logUnhandledException(e);
					logger.error("Exception: ", e);
					ruleResult = "0";
				}
				colSetup.setBankLTV(
						ruleResult == null ? BigDecimal.ZERO : new BigDecimal(ruleResult.toString()));
			}

			// calculate Bank Valuation
			BigDecimal ltvValue = colSetup.getBankLTV();
			if (colSetup.getSpecialLTV() != null && colSetup.getSpecialLTV().compareTo(BigDecimal.ZERO) > 0) {
				ltvValue = colSetup.getSpecialLTV();
			}

			BigDecimal colValue = colSetup.getCollateralValue().multiply(ltvValue).divide(new BigDecimal(100),
					0, RoundingMode.HALF_DOWN);
			if (colSetup.getMaxCollateralValue().compareTo(BigDecimal.ZERO) > 0
					&& colValue.compareTo(colSetup.getMaxCollateralValue()) > 0) {
				colValue = colSetup.getMaxCollateralValue();
			}
			colSetup.setBankValuation(colValue);
			colSetup.setCollateralStructure(collateralStructure);
		}

		// process document details
		List<DocumentDetails> documentDetails = colSetup.getDocuments();
		if (documentDetails != null) {
			for (DocumentDetails documentDetail : documentDetails) {
				documentDetail.setDocModule(CollateralConstants.MODULE_NAME);
				documentDetail.setUserDetails(colSetup.getUserDetails());
				documentDetail.setReferenceId(colSetup.getCollateralRef());

				documentDetail.setNewRecord(true);
				documentDetail.setRecordType(PennantConstants.RECORD_TYPE_NEW);
				documentDetail.setWorkflowId(financeMain.getWorkflowId());
				documentDetail.setRoleCode(financeMain.getRoleCode());
				documentDetail.setNextRoleCode(financeMain.getNextRoleCode());
				documentDetail.setTaskId(financeMain.getTaskId());
				documentDetail.setNextTaskId(financeMain.getNextTaskId());	
			}
		}
detail.setCollateralValue(colSetup.getCollateralValue());
detail.setNewRecord(true);
detail.setRecordType(PennantConstants.RECORD_TYPE_NEW);
detail.setModule(FinanceConstants.MODULE_NAME);
detail.setUserDetails(financeMain.getUserDetails());
detail.setLastMntBy(userDetails.getUserId());
detail.setLastMntOn(new Timestamp(System.currentTimeMillis()));
detail.setRecordStatus(getRecordStatus(financeMain.isQuickDisb(), stp));
detail.setVersion(1);
//workflow relates
detail.setWorkflowId(financeMain.getWorkflowId());
detail.setRoleCode(financeMain.getRoleCode());
detail.setNextRoleCode(financeMain.getNextRoleCode());
detail.setTaskId(financeMain.getTaskId());
detail.setNextTaskId(financeMain.getNextTaskId());
detail.setCollateralRef(colSetup.getCollateralRef());
	}

	/**
	 * @param financeDetail
	 * @param userDetails
	 * @param stp
	 * @param financeMain
	 */
	private void doProcessMandate(FinanceDetail financeDetail) {
		FinScheduleData finScheduleData = financeDetail.getFinScheduleData();
		FinanceMain financeMain = finScheduleData.getFinanceMain();
		FinanceType finType = finScheduleData.getFinanceType();
		LoggedInUser userDetails = financeDetail.getUserDetails();
		String entityCode = null;
		if (finType != null) {
			entityCode = divisionDetailDAO.getEntityCodeByDivision(finType.getFinDivision(), "");
		}
		Mandate mandate = financeDetail.getMandate();
		if (mandate != null) {
			BankBranch bankBranch = new BankBranch();
			if (StringUtils.isNotBlank(mandate.getIFSC())) {
				bankBranch = bankBranchService.getBankBrachByIFSC(mandate.getIFSC());
			} else if (StringUtils.isNotBlank(mandate.getBankCode())
					&& StringUtils.isNotBlank(mandate.getBranchCode())) {
				bankBranch = bankBranchService.getBankBrachByCode(mandate.getBankCode(), mandate.getBranchCode());
			}
			financeDetail.getMandate().setNewRecord(true);
			financeDetail.getMandate().setRecordType(PennantConstants.RECORD_TYPE_NEW);
			financeDetail.getMandate().setVersion(1);

			financeDetail.getMandate().setLastMntBy(userDetails.getUserId());
			financeDetail.getMandate().setLastMntOn(new Timestamp(System.currentTimeMillis()));
			financeDetail.getMandate()
					.setRecordStatus(getRecordStatus(financeMain.isQuickDisb(), financeDetail.isStp()));
			financeDetail.getMandate().setUserDetails(financeMain.getUserDetails());
			financeDetail.getMandate().setMandateCcy(SysParamUtil.getAppCurrency());
			financeDetail.getMandate().setEntityCode(entityCode);
			//workflow
			/*
			 * financeDetail.getMandate().setWorkflowId(financeMain.getWorkflowId());
			 * financeDetail.getMandate().setRoleCode(financeMain.getRoleCode());
			 * financeDetail.getMandate().setNextRoleCode(financeMain.getNextRoleCode());
			 * financeDetail.getMandate().setTaskId(financeMain.getTaskId());
			 * financeDetail.getMandate().setNextTaskId(financeMain.getNextTaskId());
			 */

			// mandate details
			financeDetail.getMandate().setCustCIF(financeMain.getLovDescCustCIF());
			financeDetail.getMandate().setCustID(financeMain.getCustID());
			financeDetail.getMandate().setBankCode(bankBranch.getBankCode());
			financeDetail.getMandate().setBranchCode(bankBranch.getBranchCode());
			financeDetail.getMandate().setBankBranchID(bankBranch.getBankBranchID());
			financeDetail.getMandate().setIFSC(bankBranch.getIFSC());
			financeDetail.getMandate().setBankBranchID(bankBranch.getBankBranchID());
			financeDetail.getMandate().setActive(true);
			financeDetail.getMandate().setInputDate(DateUtility.getAppDate());
		}
	}

	/**
	 * @param financeDetail
	 * @param finScheduleData
	 */
	private void validateDisbInstAmount(FinanceDetail financeDetail) {
		FinScheduleData finScheduleData = financeDetail.getFinScheduleData();
		if (financeDetail.getAdvancePaymentsList() != null) {
			for (FinAdvancePayments advPayments : financeDetail.getAdvancePaymentsList()) {
				advPayments.setDisbSeq(finScheduleData.getDisbursementDetails().size());
			}
			List<ErrorDetail> errors = finAdvancePaymentsService.validateFinAdvPayments(
					financeDetail.getAdvancePaymentsList(), finScheduleData.getDisbursementDetails(),
					finScheduleData.getFinanceMain(), true);
			for (ErrorDetail erroDetails : errors) {
				finScheduleData.setErrorDetail(
						ErrorUtil.getErrorDetail(new ErrorDetail(erroDetails.getCode(), erroDetails.getParameters())));
			}
		}
	}

	/**
	 * Method for process disbursement instructions and set default values
	 * 
	 * @param financeDetail
	 */
	private void doProcessDisbInstructions(FinanceDetail financeDetail) {
		FinScheduleData finScheduleData = financeDetail.getFinScheduleData();
		FinanceMain financeMain = finScheduleData.getFinanceMain();
		LoggedInUser userDetails = financeDetail.getUserDetails();

		List<FinAdvancePayments> advancePayments = financeDetail.getAdvancePaymentsList();
		if (advancePayments != null) {
			int paymentSeq = 1;
			for (FinAdvancePayments advPayment : advancePayments) {
				advPayment.setFinReference(financeMain.getFinReference());
				advPayment.setRecordType(PennantConstants.RECORD_TYPE_NEW);
				advPayment.setNewRecord(true);
				advPayment.setVersion(1);
				advPayment.setLastMntBy(userDetails.getUserId());
				advPayment.setLastMntOn(new Timestamp(System.currentTimeMillis()));
				if (financeDetail.isStp()) {
					advPayment.setRecordStatus(PennantConstants.RCD_STATUS_APPROVED);
				}

				advPayment.setUserDetails(financeMain.getUserDetails());
				advPayment.setPaymentSeq(paymentSeq);
				advPayment.setDisbCCy(financeMain.getFinCcy());
				paymentSeq++;

				//workflow related
				advPayment.setWorkflowId(financeMain.getWorkflowId());
				advPayment.setRoleCode(financeMain.getRoleCode());
				advPayment.setNextRoleCode(financeMain.getNextRoleCode());
				advPayment.setTaskId(financeMain.getTaskId());
				advPayment.setNextTaskId(financeMain.getNextTaskId());

				if (StringUtils.equals(advPayment.getPaymentType(), DisbursementConstants.PAYMENT_TYPE_IMPS)
						|| StringUtils.equals(advPayment.getPaymentType(), DisbursementConstants.PAYMENT_TYPE_NEFT)
						|| StringUtils.equals(advPayment.getPaymentType(), DisbursementConstants.PAYMENT_TYPE_RTGS)
						|| StringUtils.equals(advPayment.getPaymentType(), DisbursementConstants.PAYMENT_TYPE_IFT)) {

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
	}

	private void executeFeeCharges(FinanceDetail financeDetail, String eventCode, boolean enquiry)
			throws IllegalAccessException, InvocationTargetException {
		FinScheduleData schData = financeDetail.getFinScheduleData();
		if (CollectionUtils.isEmpty(schData.getFinFeeDetailList())) {
			if (StringUtils.isBlank(eventCode)) {
				eventCode = PennantApplicationUtil.getEventCode(schData.getFinanceMain().getFinStartDate());
			}
			feeDetailService.doProcessFeesForInquiry(financeDetail, eventCode, null, enquiry);
		} else {
			feeDetailService.doExecuteFeeCharges(financeDetail, eventCode, null, enquiry);
		}
		if (financeDetail.isStp()) {
			for (FinFeeDetail feeDetail : schData.getFinFeeDetailList()) {
				feeDetail.setWorkflowId(0);
			}
		}
	}

	private String getNextTaskId(String taksId, boolean qdp, boolean stp) {
		if (stp && !qdp) {
			return null;
		} else {
			return taksId + ";";
		}

	}

	/**
	 * Method for prepare API response object
	 * 
	 * @param finReference
	 * @return
	 */
	private FinanceDetail getFinanceDetailResponse(AuditHeader auditHeader) {
		logger.debug("Enteing");

		FinanceDetail financeDetail = (FinanceDetail) auditHeader.getAuditDetail().getModelData();

		FinanceMain financeMain = financeDetail.getFinScheduleData().getFinanceMain();
		FinanceDetail response = new FinanceDetail();
		FinScheduleData finScheduleData = new FinScheduleData();

		response.setFinReference(financeMain.getFinReference());
		finScheduleData.setFinanceMain(financeMain);
		finScheduleData.setFinFeeDetailList(financeDetail.getFinScheduleData().getFinFeeDetailList());
		finScheduleData.setStepPolicyDetails(financeDetail.getFinScheduleData().getStepPolicyDetails());
		if (!financeDetail.getFinScheduleData().getFinanceMain().getProductCategory()
				.equals(FinanceConstants.PRODUCT_ODFACILITY)) {
			finScheduleData.setFinanceScheduleDetails(financeDetail.getFinScheduleData().getFinanceScheduleDetails());
			response.setFinScheduleData(finScheduleData);
			// set fee paid amounts based on schedule method
			finScheduleData.setFinFeeDetailList(getUpdatedFees(finScheduleData.getFinFeeDetailList()));
			// Fetch summary details
			FinanceSummary summary = getFinanceSummary(financeDetail);
			response.getFinScheduleData().setFinanceSummary(summary);
		} else {
			finScheduleData
					.setOverdraftScheduleDetails(financeDetail.getFinScheduleData().getOverdraftScheduleDetails());
			finScheduleData.setFinanceScheduleDetails(null);
			response.getFinScheduleData().setFinanceSummary(null);
		}

		// nullify the unnecessary object
		finScheduleData.setDisbursementDetails(null);
		finScheduleData.setRepayInstructions(null);
		finScheduleData.setRateInstruction(null);

		response.setFinScheduleData(finScheduleData);

		response.setJountAccountDetailList(null);
		response.setGurantorsDetailList(null);
		response.setDocumentDetailsList(null);
		response.setFinanceCollaterals(null);

		logger.debug("Leaving");

		return response;
	}

	public FinanceDetail getFinanceDetails(String finReference) {
		logger.debug("Enetring");
		FinanceDetail financeDetail = null;
		try {
			financeDetail = financeDetailService.getFinanceDetailById(finReference, false, "", false,
					FinanceConstants.FINSER_EVENT_ORG, "");

			if (financeDetail != null) {
				List<ExtendedField> extData = extendedFieldDetailsService.getExtndedFieldDetails(
						ExtendedFieldConstants.MODULE_LOAN,
						financeDetail.getFinScheduleData().getFinanceMain().getFinCategory(),
						FinanceConstants.FINSER_EVENT_ORG, finReference);
				financeDetail.setExtendedDetails(extData);
				financeDetail.setReturnStatus(APIErrorHandlerService.getSuccessStatus());
			} else {
				financeDetail = new FinanceDetail();
				financeDetail.setReturnStatus(APIErrorHandlerService.getFailedStatus());
			}
		} catch (Exception e) {
			logger.error("Exception: ", e);
			APIErrorHandlerService.logUnhandledException(e);
			financeDetail = new FinanceDetail();
			financeDetail.setReturnStatus(APIErrorHandlerService.getFailedStatus("API006", "Test"));
			return financeDetail;
		}

		logger.debug("Leaving");
		return financeDetail;
	}

	public WSReturnStatus doApproveLoan(FinanceDetail financeDetail) {
		logger.debug("Entering");

		try {
			// financeMain details
			FinScheduleData finScheduleData = financeDetail.getFinScheduleData();
			FinanceMain financeMain = finScheduleData.getFinanceMain();

			// user language
			LoggedInUser userDetails = SessionUserDetails.getUserDetails(SessionUserDetails.getLogiedInUser());
			financeMain.setUserDetails(userDetails);

			String roleCode = null;
			String taskid = null;
			boolean stp = financeDetail.isStp();
			long workFlowId = 0;

			financeMain.setRecordStatus(getRecordStatus(financeMain.isQuickDisb(), financeDetail.isStp()));

			financeMain.setRecordType(PennantConstants.RECORD_TYPE_NEW);
			financeMain.setWorkflowId(0);
			financeMain.setRoleCode(roleCode);
			financeMain.setNextRoleCode(roleCode);
			financeMain.setTaskId(taskid);
			financeMain.setNextTaskId(getNextTaskId(taskid, financeMain.isQuickDisb(), stp));
			financeMain.setNewRecord(true);
			financeMain.setLastMntOn(new Timestamp(System.currentTimeMillis()));
			financeMain.setLastMntBy(getLastMntBy(financeMain.isQuickDisb(), userDetails));
			financeMain.setRecordStatus(getRecordStatus(financeMain.isQuickDisb(), stp));

			finScheduleData.setFinanceMain(financeMain);

			// set required mandatory values into finance details object
			doSetRequiredDetails(financeDetail, false, userDetails, stp, true);
		
			finScheduleData.getFinanceMain().setFinRemarks("SUCCESS");
			financeDetail.setStp(false);
			// set LastMntBy , LastMntOn and status fields to schedule details
			for (FinanceScheduleDetail schdDetail : finScheduleData.getFinanceScheduleDetails()) {
				schdDetail.setLastMntOn(new Timestamp(System.currentTimeMillis()));
				schdDetail.setRecordStatus(getRecordStatus(financeMain.isQuickDisb(), stp));
				schdDetail.setWorkflowId(workFlowId);
				schdDetail.setRoleCode(roleCode);
				schdDetail.setNextRoleCode(roleCode);
				schdDetail.setTaskId(taskid);
				schdDetail.setNextTaskId(financeMain.getNextTaskId());
			}

			// Finance detail object
			financeDetail.setUserAction("");
			financeDetail.setExtSource(false);
			financeDetail.setAccountingEventCode(PennantApplicationUtil.getEventCode(financeMain.getFinStartDate()));
			financeDetail.setFinReference(financeMain.getFinReference());
			if (!StringUtils.equals(FinanceConstants.PRODUCT_ODFACILITY,
					financeDetail.getFinScheduleData().getFinanceMain().getProductCategory())) {
				financeDetail.setFinScheduleData(finScheduleData);
			}

			AuditDetail auditDetail = new AuditDetail(PennantConstants.TRAN_WF, 1, null, financeDetail);
			AuditHeader auditHeader = new AuditHeader(financeDetail.getFinReference(), null, null, null, auditDetail,
					financeMain.getUserDetails(), new HashMap<String, ArrayList<ErrorDetail>>());

			APIHeader reqHeaderDetails = (APIHeader) PhaseInterceptorChain.getCurrentMessage().getExchange()
					.get(APIHeader.API_HEADER_KEY);
			//auditHeader.setApiHeader(reqHeaderDetails);

			auditHeader = financeDetailService.doApprove(auditHeader, false);

			if (auditHeader.getOverideMessage() != null && auditHeader.getOverideMessage().size() > 0) {
				for (ErrorDetail errorDetail : auditHeader.getOverideMessage()) {
					return APIErrorHandlerService.getFailedStatus(errorDetail.getCode(), errorDetail.getError());
				}
			}
			if (auditHeader.getErrorMessage() != null) {
				for (ErrorDetail errorDetail : auditHeader.getErrorMessage()) {
					return APIErrorHandlerService.getFailedStatus(errorDetail.getCode(), errorDetail.getError());
				}
			}

			if (auditHeader.getAuditDetail().getErrorDetails() != null) {
				for (ErrorDetail errorDetail : auditHeader.getAuditDetail().getErrorDetails()) {
					return APIErrorHandlerService.getFailedStatus(errorDetail.getCode(), errorDetail.getError());
				}
			}

		} catch (InterfaceException ex) {
			logger.error("InterfaceException", ex);
			return APIErrorHandlerService.getFailedStatus("9999", ex.getMessage());
		} catch (AppException ex) {
			logger.error("AppException", ex);
			return APIErrorHandlerService.getFailedStatus("9999", ex.getMessage());
		} catch (Exception e) {
			logger.error("Exception: ", e);
			return APIErrorHandlerService.getFailedStatus();
		}
		return APIErrorHandlerService.getSuccessStatus();

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
						List<FinanceScheduleDetail> finSchduleList = financeScheduleDetailDAO
								.getFinScheduleDetails(detail, "", false);
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
					if (financeDetail.getFinScheduleData().getFinanceMain().isPlanEMIHAlw()) {
						processPlanEmiDays(finReference, financeDetail);
					}
				}
			}
			if (financeDetail != null) {
				FinODPenaltyRate finODPenaltyRate = financeDetail.getFinScheduleData().getFinODPenaltyRate();
				if (finODPenaltyRate != null
						&& StringUtils.equals(finODPenaltyRate.getODChargeType(),
								FinanceConstants.PENALTYTYPE_PERC_ONETIME)
						|| StringUtils.equals(finODPenaltyRate.getODChargeType(),
								FinanceConstants.PENALTYTYPE_PERC_ON_DUEDAYS)
						|| StringUtils.equals(finODPenaltyRate.getODChargeType(),
								FinanceConstants.PENALTYTYPE_PERC_ON_PD_MTH)) {
					BigDecimal totPerc = PennantApplicationUtil.formateAmount(finODPenaltyRate.getODChargeAmtOrPerc(),
							2);
					finODPenaltyRate.setODChargeAmtOrPerc(totPerc);
				}
				financeDetail.getFinScheduleData().setFinODPenaltyRate(finODPenaltyRate);
				prepareResponse(financeDetail);
				List<ExtendedField> extData = extendedFieldDetailsService.getExtndedFieldDetails(
						ExtendedFieldConstants.MODULE_LOAN,
						financeDetail.getFinScheduleData().getFinanceMain().getFinCategory(),
						FinanceConstants.FINSER_EVENT_ORG, finReference);
				financeDetail.setExtendedDetails(extData);
				financeDetail.setReturnStatus(APIErrorHandlerService.getSuccessStatus());
			} else {
				financeDetail = new FinanceDetail();
				doEmptyResponseObject(financeDetail);
				financeDetail.setReturnStatus(APIErrorHandlerService.getFailedStatus());
			}
		} catch (Exception e) {
			logger.error("Exception: ", e);
			APIErrorHandlerService.logUnhandledException(e);
			financeDetail = new FinanceDetail();
			doEmptyResponseObject(financeDetail);
			financeDetail.setReturnStatus(APIErrorHandlerService.getFailedStatus());
			return financeDetail;
		}

		logger.debug("Leaving");
		return financeDetail;
	}

	private void processPlanEmiDays(String finReference, FinanceDetail financeDetail) {
		List<FinPlanEmiHoliday> apiPlanEMImonths = new ArrayList<FinPlanEmiHoliday>();
		FinanceMain finMain = financeDetail.getFinScheduleData().getFinanceMain();
		if (StringUtils.equals(finMain.getPlanEMIHMethod(), FinanceConstants.PLANEMIHMETHOD_FRQ)) {
			financeDetail.getFinScheduleData()
					.setPlanEMIHmonths(getFinPlanEmiHolidayDAO().getPlanEMIHMonthsByRef(finReference, ""));
			if (financeDetail.getFinScheduleData().getPlanEMIHmonths() != null) {
				for (Integer detail : financeDetail.getFinScheduleData().getPlanEMIHmonths()) {
					FinPlanEmiHoliday finPlanEmiHoliday = new FinPlanEmiHoliday();
					finPlanEmiHoliday.setPlanEMIHMonth(detail);
					apiPlanEMImonths.add(finPlanEmiHoliday);
				}
			}
			financeDetail.getFinScheduleData().setApiplanEMIHmonths(apiPlanEMImonths);
		} else if (StringUtils.equals(finMain.getPlanEMIHMethod(), FinanceConstants.PLANEMIHMETHOD_ADHOC)) {
			financeDetail.getFinScheduleData()
					.setPlanEMIHDates(getFinPlanEmiHolidayDAO().getPlanEMIHDatesByRef(finReference, ""));
			if (financeDetail.getFinScheduleData().getPlanEMIHDates() != null) {
				for (Date detail : financeDetail.getFinScheduleData().getPlanEMIHDates()) {
					FinPlanEmiHoliday finPlanEmiHoliday = new FinPlanEmiHoliday();
					finPlanEmiHoliday.setPlanEMIHDate(detail);
					apiPlanEMImonths.add(finPlanEmiHoliday);
				}
				financeDetail.getFinScheduleData().setApiPlanEMIHDates(apiPlanEMImonths);
			}
		}
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
				List<FinanceScheduleDetail> finSchduleList = financeScheduleDetailDAO
						.getFinScheduleDetails(financeMain.getFinReference(), "", false);
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
							if (!(financeScheduleDetail.getRepayAmount().compareTo(BigDecimal.ZERO) == 0)
									&& isnextRepayAmount) {
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
				List<JointAccountDetail> jountAccountDetailList = jointAccountDetailService
						.getJoinAccountDetail(financeMain.getFinReference(), "_View");
				finInquiryDetail.setJountAccountDetailList(jountAccountDetailList);

				// fetch disbursement details
				List<FinanceDisbursement> disbList = getFinanceDisbursementDAO()
						.getFinanceDisbursementDetails(financeMain.getFinReference(), "", false);
				BigDecimal totDisbAmt = BigDecimal.ZERO;
				BigDecimal totfeeChrgAmt = BigDecimal.ZERO;
				for (FinanceDisbursement finDisb : disbList) {
					totDisbAmt = totDisbAmt.add(finDisb.getDisbAmount());
					totfeeChrgAmt = totfeeChrgAmt.add(finDisb.getFeeChargeAmt());
				}
				BigDecimal assetValue = financeMain.getFinAssetValue() == null ? BigDecimal.ZERO
						: financeMain.getFinAssetValue();
				if (assetValue.compareTo(totDisbAmt) == 0) {
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
		} catch (Exception e) {
			logger.error("Exception: ", e);
			APIErrorHandlerService.logUnhandledException(e);
			FinanceInquiry financeInquiry = new FinanceInquiry();
			financeInquiry.setReturnStatus(APIErrorHandlerService.getFailedStatus());
			return financeInquiry;
		}

	}

	/**
	 * Method for update finance details
	 * 
	 * @param financeDetail
	 * @return
	 */
	public WSReturnStatus updateFinance(FinanceDetail financeDetail) {
		logger.debug(Literal.ENTERING);
		FinanceMain finMain = financeDetail.getFinScheduleData().getFinanceMain();
		TableType tableType = TableType.MAIN_TAB;
		if (finMain.isWorkflow()) {
			tableType = TableType.TEMP_TAB;
		}
		try {
			LoggedInUser userDetails = SessionUserDetails.getUserDetails(SessionUserDetails.getLogiedInUser());
			financeDetail.setUserDetails(userDetails);
			financeDetail.getFinScheduleData().getFinanceMain().setUserDetails(userDetails);

			if (financeDetail.getAdvancePaymentsList() != null && !financeDetail.getAdvancePaymentsList().isEmpty()) {
				WSReturnStatus status = updateDisbursementInst(financeDetail, tableType.getSuffix());
				if (StringUtils.isNotBlank(status.getReturnCode())) {
					return status;
				}
			}

			// Save or Update mandate details
			if (financeDetail.getMandate() != null) {
				updateFinMandateDetails(financeDetail, tableType.getSuffix());
			}

			// update Extended field details
			if (financeDetail.getExtendedDetails() != null && !financeDetail.getExtendedDetails().isEmpty()) {
				extendedFieldDetailsService.updateFinExtendedDetails(financeDetail, tableType.getSuffix());
			}

			// save or update document details
			if (financeDetail.getDocumentDetailsList() != null && !financeDetail.getDocumentDetailsList().isEmpty()) {
				updatedFinanceDocuments(financeDetail);
			}
		} catch (Exception e) {
			logger.error("Exception", e);
			return APIErrorHandlerService.getFailedStatus();
		}
		logger.debug(Literal.LEAVING);
		return APIErrorHandlerService.getSuccessStatus();
	}

	/**
	 * Method for Save or update Finance documents.
	 * 
	 * @param financeDetail
	 */
	private void updatedFinanceDocuments(FinanceDetail financeDetail) {
		logger.debug(Literal.ENTERING);
		FinanceMain financeMain = financeDetail.getFinScheduleData().getFinanceMain();
		AuditHeader auditHeader = null;
		for (DocumentDetails detail : financeDetail.getDocumentDetailsList()) {
			detail.setNewRecord(true);
			detail.setRecordType(PennantConstants.RECORD_TYPE_NEW);
			detail.setVersion(1);
			detail.setReferenceId(financeMain.getFinReference());

			// set update properties if exists
			String finReference = financeMain.getFinReference();
			String docCategory = detail.getDocCategory();
			String module = FinanceConstants.MODULE_NAME;
			String type = TableType.TEMP_TAB.getSuffix();
			DocumentDetails extDocDetail = documentDetailsDAO.getDocumentDetails(finReference, docCategory, module,
					type);
			if (extDocDetail != null) {
				detail.setDocId(extDocDetail.getDocId());
				detail.setNewRecord(false);
				detail.setRecordType(PennantConstants.RECORD_TYPE_UPD);
				detail.setVersion(extDocDetail.getVersion() + 1);
			}

			detail.setDocModule(FinanceConstants.MODULE_NAME);
			detail.setUserDetails(financeMain.getUserDetails());
			detail.setRecordStatus(financeMain.getRecordStatus());
			//workflow relates
			detail.setWorkflowId(financeMain.getWorkflowId());
			detail.setRoleCode(financeMain.getRoleCode());
			detail.setNextRoleCode(financeMain.getNextRoleCode());
			detail.setTaskId(financeMain.getTaskId());
			detail.setNextTaskId(financeMain.getNextTaskId());

			if (StringUtils.equals(detail.getRecordType(), PennantConstants.RECORD_TYPE_UPD)) {
				auditHeader = getAuditHeader(detail, PennantConstants.TRAN_UPD);
			} else {
				auditHeader = getAuditHeader(detail, PennantConstants.TRAN_ADD);
			}
			documentService.saveOrUpdate(auditHeader);
		}

		logger.debug(Literal.LEAVING);
	}

	private void updateFinMandateDetails(FinanceDetail financeDetail, String type) {
		logger.debug(Literal.ENTERING);
		// process mandate details
		doProcessMandate(financeDetail);
		AuditHeader auditHeader = null;

		// Update mandate details if exists
		String finReference = financeDetail.getFinScheduleData().getFinanceMain().getFinReference();
		long extMandateId = financeMainDAO.getMandateIdByRef(finReference, TableType.TEMP_TAB.getSuffix());
		if (extMandateId != Long.MIN_VALUE && extMandateId != 0) {
			financeDetail.getMandate().setNewRecord(false);
			financeDetail.getMandate().setRecordType(PennantConstants.RECORD_TYPE_UPD);
			financeDetail.getMandate().setVersion(1);
			auditHeader = getAuditHeader(financeDetail.getMandate(), PennantConstants.TRAN_UPD);
		} else {
			auditHeader = getAuditHeader(financeDetail.getMandate(), PennantConstants.TRAN_ADD);
		}
		finMandateService.saveOrUpdate(financeDetail, auditHeader, type);

		if (extMandateId == Long.MIN_VALUE || extMandateId == 0) {
			// update FinanceMain table
			long mandateId = financeDetail.getFinScheduleData().getFinanceMain().getMandateID();
			financeMainDAO.updateFinMandateId(mandateId, financeDetail.getFinReference(), type);
		}
		logger.debug(Literal.LEAVING);
	}

	private WSReturnStatus updateDisbursementInst(FinanceDetail financeDetail, String type) {
		WSReturnStatus returnStatus = new WSReturnStatus();
		// process disbursement instructions
		doProcessDisbInstructions(financeDetail);

		// validate total disbursement amount
		validateDisbInstAmount(financeDetail);
		if (financeDetail.getFinScheduleData().getErrorDetails() != null) {
			for (ErrorDetail errorDetail : financeDetail.getFinScheduleData().getErrorDetails()) {
				return APIErrorHandlerService.getFailedStatus(errorDetail.getCode(), errorDetail.getError());
			}
		}

		// Delete Disbursement details if exists
		String finReference = financeDetail.getFinScheduleData().getFinanceMain().getFinReference();
		List<FinAdvancePayments> extAdvPayments = finAdvancePaymentsDAO.getFinAdvancePaymentsByFinRef(finReference,
				TableType.TEMP_TAB.getSuffix());
		if (extAdvPayments != null && !extAdvPayments.isEmpty()) {
			finAdvancePaymentsDAO.deleteByFinRef(finReference, TableType.TEMP_TAB.getSuffix());
		}

		List<AuditDetail> auditDetails = new ArrayList<AuditDetail>();
		auditDetails.addAll(finAdvancePaymentsService.saveOrUpdate(financeDetail.getAdvancePaymentsList(), type,
				PennantConstants.TRAN_WF));

		AuditHeader auditHeader = getAuditHeader(financeDetail.getFinScheduleData().getFinanceMain(),
				PennantConstants.TRAN_WF);
		auditHeader.setAuditDetails(auditDetails);
		auditHeaderDAO.addAudit(auditHeader);

		return returnStatus;
	}

	private AuditHeader getAuditHeader(FinanceMain finMain, String tranType) {
		AuditDetail auditDetail = new AuditDetail(tranType, 1, finMain.getBefImage(), finMain);
		return new AuditHeader(finMain.getFinReference(), null, null, null, auditDetail, finMain.getUserDetails(),
				new HashMap<String, ArrayList<ErrorDetail>>());
	}

	private AuditHeader getAuditHeader(DocumentDetails documentDetails, String transType) {
		AuditDetail auditDetail = new AuditDetail(transType, 1, documentDetails.getBefImage(), documentDetails);
		return new AuditHeader(documentDetails.getReferenceId(), null, null, null, auditDetail,
				documentDetails.getUserDetails(), new HashMap<String, ArrayList<ErrorDetail>>());
	}

	private AuditHeader getAuditHeader(Mandate aMandate, String tranType) {
		AuditDetail auditDetail = new AuditDetail(tranType, 1, aMandate.getBefImage(), aMandate);
		return new AuditHeader(String.valueOf(aMandate.getMandateID()), null, null, null, auditDetail,
				aMandate.getUserDetails(), new HashMap<String, ArrayList<ErrorDetail>>());
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
		//ReqStage and Status
		financeDetail.getFinScheduleData().getFinanceMain()
				.setStatus(financeDetail.getFinScheduleData().getFinanceMain().getRecordStatus());
		financeDetail.getFinScheduleData().getFinanceMain()
				.setStage(financeDetail.getFinScheduleData().getFinanceMain().getNextRoleCode());
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
				financeDetail.getFinScheduleData().getFinanceMain()
						.setLastDisbDate(disbList.get(disbList.size() - 1).getDisbDate());
			}
		}

		List<FinFeeDetail> finFeeDetail = financeDetail.getFinScheduleData().getFinFeeDetailList();
		financeDetail.setFinFeeDetails(getUpdatedFees(finFeeDetail));

		// Bounce and manual advice fees if applicable
		String finReference = financeDetail.getFinScheduleData().getFinanceMain().getFinReference();
		List<ManualAdvise> manualAdviseFees = manualAdviseDAO.getManualAdviseByRef(finReference,
				FinanceConstants.MANUAL_ADVISE_RECEIVABLE, "_View");
		if (manualAdviseFees != null && !manualAdviseFees.isEmpty()) {
			for (ManualAdvise advisedFees : manualAdviseFees) {
				FinFeeDetail feeDetail = new FinFeeDetail();
				if (advisedFees.getBounceID() > 0) {
					feeDetail.setFeeCategory(FinanceConstants.FEES_AGAINST_BOUNCE);
					feeDetail.setSchdDate(getBounceDueDate(advisedFees.getReceiptID()));
				} else {
					feeDetail.setFeeCategory(FinanceConstants.FEES_AGAINST_ADVISE);
				}
				feeDetail.setFeeTypeCode(advisedFees.getFeeTypeCode());
				feeDetail.setActualAmount(advisedFees.getAdviseAmount());
				feeDetail.setPaidAmount(advisedFees.getPaidAmount());
				feeDetail.setRemainingFee(advisedFees.getBalanceAmt());

				financeDetail.getFinFeeDetails().add(feeDetail);
			}
		}

		// Fetch summary details
		FinanceSummary summary = getFinanceSummary(financeDetail);
		summary.setAdvPaymentAmount(getTotalAdvAmount(finReference));
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

	/**
	 * Method for set the default data to the ChequeHeader.
	 * 
	 * @param financeDetail
	 */
	private void doSetDefaultChequeHeader(FinanceDetail financeDetail) {
		logger.debug(Literal.ENTERING);
		if (financeDetail.getChequeHeader() == null) {
			FinanceMain financeMain = financeDetail.getFinScheduleData().getFinanceMain();
			ChequeHeader chequeHeader = new ChequeHeader();
			chequeHeader.setNewRecord(true);
			chequeHeader.setRoleCode(financeMain.getNextRoleCode());
			chequeHeader.setNextRoleCode(financeMain.getNextRoleCode());
			chequeHeader.setTaskId(financeMain.getTaskId());
			chequeHeader.setNextTaskId(financeMain.getNextTaskId());
			chequeHeader.setVersion(1);
			chequeHeader.setLastMntBy(financeMain.getLastMntBy());
			chequeHeader.setLastMntOn(new Timestamp(System.currentTimeMillis()));
			chequeHeader.setRecordStatus(financeMain.getRecordStatus());
			chequeHeader.setRecordType(PennantConstants.RECORD_TYPE_NEW);
			chequeHeader.setWorkflowId(financeMain.getWorkflowId());

			chequeHeader.setFinReference(financeMain.getFinReference());
			chequeHeader.setNoOfCheques(0);
			chequeHeader.setTotalAmount(BigDecimal.ZERO);
			chequeHeader.setActive(true);
			financeDetail.setChequeHeader(chequeHeader);
		}
		logger.debug(Literal.LEAVING);
	}

	/**
	 * Method for fetch Schedule Date against the presentment bounce charge
	 * 
	 * @param receiptId
	 * @return
	 */
	private Date getBounceDueDate(long receiptId) {
		Date schdDate = manualAdviseDAO.getPresentmentBounceDueDate(receiptId);
		return schdDate;
	}

	private void doEmptyResponseObject(FinanceDetail detail) {
		detail.setFinScheduleData(null);
		detail.setDocumentDetailsList(null);
		detail.setJountAccountDetailList(null);
		detail.setGurantorsDetailList(null);
		detail.setCollateralAssignmentList(null);
		detail.setFinFlagsDetails(null);
	}

	private List<ErrorDetail> adjustFees(FinScheduleData detail) {
		List<ErrorDetail> errorDetails = detail.getErrorDetails();
		LoggedInUser userDetails = SessionUserDetails.getUserDetails(SessionUserDetails.getLogiedInUser());
		if (detail.getFinFeeDetailList() != null && detail.getFinFeeDetailList().size() > 0) {
			Map<String, FinFeeDetail> feeDetailMap = new HashMap<>();
			List<Long> receiptList = new ArrayList<>();
			for (FinFeeDetail feeDetail : detail.getFinFeeDetailList()) {
				if (feeDetail.getPaidAmount().compareTo(BigDecimal.ZERO) > 0) {

					if (feeDetail.getFinFeeReceipts() != null && feeDetail.getFinFeeReceipts().size() > 0) {
						BigDecimal feeAmountPaid = BigDecimal.ZERO;
						for (FinFeeReceipt feeReceipt : feeDetail.getFinFeeReceipts()) {
							feeAmountPaid = feeAmountPaid.add(feeReceipt.getPaidAmount());
							if (!receiptList.contains(feeReceipt.getReceiptID())) {
								receiptList.add(feeReceipt.getReceiptID());
							}
						}
						if (feeAmountPaid.compareTo(BigDecimal.ZERO) <= 0) {
							//Throw validation error and break the loop
						}
					}

				}
				feeDetailMap.put(feeDetail.getFeeTypeCode(), feeDetail);
			}
			Map<Long, FinReceiptHeader> receiptHeaderMap = new HashMap<>();// map to save receipt header
			List<FinReceiptHeader> receiptHeaderList = finReceiptHeaderDAO
					.getUpFrontReceiptHeaderByExtRef(detail.getExternalReference(), "");
			for (FinReceiptHeader header : receiptHeaderList) {
				receiptHeaderMap.put(header.getReceiptID(), header);
			}
			for (FinFeeDetail feeDtl : detail.getFinFeeDetailList()) {// iterating existing finfee details list from finscheduledata
				if (feeDetailMap.containsKey(feeDtl.getFeeTypeCode())) {
					FinFeeDetail finFeeDetail = feeDetailMap.get(feeDtl.getFeeTypeCode());
					if (finFeeDetail.getPaidAmount().compareTo(BigDecimal.ZERO) > 0) {
						if (finFeeDetail.getFinFeeReceipts() != null && finFeeDetail.getFinFeeReceipts().size() > 0) {
							for (FinFeeReceipt feeReceipt : finFeeDetail.getFinFeeReceipts()) {// iterating receipt details
								if (receiptHeaderMap.containsKey(feeReceipt.getReceiptID())) {

									FinReceiptHeader header = receiptHeaderMap.get(feeReceipt.getReceiptID());
									if (header.getReceiptAmount().compareTo(feeReceipt.getPaidAmount()) >= 0) {
										header.setReceiptAmount(
												header.getReceiptAmount().subtract(feeReceipt.getPaidAmount()));
									} else {
										String[] valueParm = new String[1];
										valueParm[0] = "Insufficient funds to adjust fees";
										errorDetails.add(ErrorUtil.getErrorDetail(new ErrorDetail("30550", valueParm)));
										return errorDetails;
									}
								} else {
									String[] valueParm = new String[1];
									valueParm[0] = "Invalid receiptId " + feeReceipt.getReceiptID();
									errorDetails.add(ErrorUtil.getErrorDetail(new ErrorDetail("30550", valueParm)));
									return errorDetails;
								}
								feeReceipt.setFeeTypeCode(feeDtl.getFeeTypeCode());
								feeReceipt.setFeeTypeId(feeDtl.getFeeTypeID());
								feeReceipt.setRecordType(PennantConstants.RCD_ADD);
								feeReceipt.setNewRecord(true);
								feeReceipt.setFeeTypeDesc(feeDtl.getFeeTypeDesc());
								feeReceipt.setLastMntBy(getLastMntBy(false, userDetails));
								detail.getFinFeeReceipts().add(feeReceipt);
							}
						}
					}
				} /*
					 * else{//Throw error if fee code does not exist in request and break the loop
					 * 
					 * }
					 */

			}
		}
		return errorDetails;
	}

	private List<ErrorDetail> validatePaidFinFeeDetail(FinScheduleData detail, List<FinReceiptHeader> receiptHeaderList,
			Map<String, FinFeeDetail> paidFeeDetailMap) {
		List<ErrorDetail> errorDetails = detail.getErrorDetails();

		for (FinFeeDetail feeDetail : detail.getFinFeeDetailList()) {
			if (paidFeeDetailMap.containsKey(feeDetail.getFeeTypeCode())) {
				feeDetail.setTransactionId(detail.getExternalReference());
				FinFeeDetail paidFee = paidFeeDetailMap.get(feeDetail.getFeeTypeCode());
				if (feeDetail.getPaidAmount().compareTo(paidFee.getPaidAmount()) != 0) {
					String[] valueParm = new String[1];
					valueParm[0] = feeDetail.getFeeTypeCode() + " Paid Amount must match with already paid amount ";
					errorDetails.add(ErrorUtil.getErrorDetail(new ErrorDetail("30550", valueParm)));
					return errorDetails;
				}
				List<FinFeeReceipt> finFeeReceiptList = finFeeReceiptDAO.getFinFeeReceiptByFeeId(paidFee.getFeeID(),
						"_View");

				if (feeDetail.isAlwPreIncomization()) {
					if (finFeeReceiptList == null || finFeeReceiptList.size() == 0) {
						String[] valueParm = new String[1];
						valueParm[0] = feeDetail.getFeeTypeCode() + " Paid Amount must match with already paid amount ";
						errorDetails.add(ErrorUtil.getErrorDetail(new ErrorDetail("30550", valueParm)));
						return errorDetails;
					}

				}
				for (FinFeeReceipt finFeeReceipt : finFeeReceiptList) {
					finFeeReceipt.setRecordType("");
					for (FinReceiptHeader receiptHeader : receiptHeaderList) {
						if (receiptHeader.getReceiptID() == finFeeReceipt.getReceiptID()) {
							receiptHeader.setReceiptAmount(
									receiptHeader.getReceiptAmount().subtract(finFeeReceipt.getPaidAmount()));
						}
					}
				}
				detail.getFinFeeReceipts().addAll(finFeeReceiptList);
			}
		}

		return errorDetails;

	}

	private Map<String, FinFeeDetail> getFeeDetailMap(List<FinFeeDetail> finFeeDetails) {
		Map<String, FinFeeDetail> feeDetailMap = new HashMap<>();
		for (FinFeeDetail feeDetail : finFeeDetails) {
			feeDetailMap.put(feeDetail.getFeeTypeCode(), feeDetail);
		}
		return feeDetailMap;
	}

	private List<ErrorDetail> adjustFeesAuto(FinScheduleData detail) {
		List<ErrorDetail> errorDetails = detail.getErrorDetails();
		List<FinReceiptHeader> receiptHeaderList = finReceiptHeaderDAO
				.getUpFrontReceiptHeaderByExtRef(detail.getExternalReference(), "");
		List<FinFeeDetail> finFeeDetailsPaid = finFeeDetailDAO.getFinFeeDetailsByTran(detail.getExternalReference(),
				false, "_View");
		Map<String, FinFeeDetail> paidFeeDetailMap = getFeeDetailMap(finFeeDetailsPaid);
		LoggedInUser userDetails = SessionUserDetails.getUserDetails(SessionUserDetails.getLogiedInUser());
		if (finFeeDetailsPaid != null && finFeeDetailsPaid.size() > 0) {
			errorDetails = validatePaidFinFeeDetail(detail, receiptHeaderList, paidFeeDetailMap);

		}

		if (detail.getFinFeeDetailList() != null && detail.getFinFeeDetailList().size() > 0 && errorDetails.isEmpty()) {
			for (FinFeeDetail feeDtl : detail.getFinFeeDetailList()) {// iterating existing finfee details list from finscheduledata
				if (!paidFeeDetailMap.containsKey(feeDtl.getFeeTypeCode())) {
					if (feeDtl.getPaidAmount().compareTo(BigDecimal.ZERO) > 0) {
						BigDecimal feePaidAmount = feeDtl.getPaidAmount();
						FinFeeReceipt feeReceipt = new FinFeeReceipt();

						for (FinReceiptHeader header : receiptHeaderList) {
							if (header.getReceiptAmount().compareTo(BigDecimal.ZERO) > 0) {
								if (feePaidAmount.compareTo(header.getReceiptAmount()) <= 0) {
									header.setReceiptAmount(header.getReceiptAmount().subtract(feePaidAmount));
									feeReceipt.setFeeTypeCode(feeDtl.getFeeTypeCode());
									feeReceipt.setFeeTypeId(feeDtl.getFeeTypeID());
									feeReceipt.setRecordType(PennantConstants.RCD_ADD);
									feeReceipt.setNewRecord(true);
									feeReceipt.setFeeTypeDesc(feeDtl.getFeeTypeDesc());
									feeReceipt.setLastMntBy(getLastMntBy(false, userDetails));
									feeReceipt.setReceiptID(header.getReceiptID());
									feeReceipt.setPaidAmount(feePaidAmount);
									feePaidAmount = BigDecimal.ZERO;

									break;
								} else {
									feePaidAmount = feePaidAmount.subtract(header.getReceiptAmount());
									feeReceipt.setFeeTypeCode(feeDtl.getFeeTypeCode());
									feeReceipt.setFeeTypeId(feeDtl.getFeeTypeID());
									feeReceipt.setRecordType(PennantConstants.RCD_ADD);
									feeReceipt.setNewRecord(true);
									feeReceipt.setFeeTypeDesc(feeDtl.getFeeTypeDesc());
									feeReceipt.setLastMntBy(getLastMntBy(false, userDetails));
									feeReceipt.setReceiptID(header.getReceiptID());
									feeReceipt.setPaidAmount(header.getReceiptAmount());
									header.setReceiptAmount(BigDecimal.ZERO);
								}
							}

						}
						if (feePaidAmount.compareTo(BigDecimal.ZERO) > 0) {
							String[] valueParm = new String[1];
							valueParm[0] = "Insufficient funds to adjust fees";
							errorDetails.add(ErrorUtil.getErrorDetail(new ErrorDetail("30550", valueParm)));
							return errorDetails;
						}
						detail.getFinFeeReceipts().add(feeReceipt);

					}
				}

			}
		}

		return errorDetails;
	}

	protected String getTaskAssignmentMethod(String taskId) {
		return workFlow.getUserTask(taskId).getAssignmentLevel();
	}

	public void setFinanceDetailService(FinanceDetailService financeDetailService) {
		this.financeDetailService = financeDetailService;
	}

	public void setStepPolicyDetailDAO(StepPolicyDetailDAO stepPolicyDetailDAO) {
		this.stepPolicyDetailDAO = stepPolicyDetailDAO;
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

	public void setFeeDetailService(FeeDetailService feeDetailService) {
		this.feeDetailService = feeDetailService;
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

	public void setCustomerAddresService(CustomerAddresService customerAddresService) {
		this.customerAddresService = customerAddresService;
	}

	public void setManualAdviseDAO(ManualAdviseDAO manualAdviseDAO) {
		this.manualAdviseDAO = manualAdviseDAO;
	}

	public void setFinanceReferenceDetailDAO(FinanceReferenceDetailDAO financeReferenceDetailDAO) {
		this.financeReferenceDetailDAO = financeReferenceDetailDAO;
	}

	public void setFinanceWorkFlowService(FinanceWorkFlowService financeWorkFlowService) {
		this.financeWorkFlowService = financeWorkFlowService;
	}

	public FinPlanEmiHolidayDAO getFinPlanEmiHolidayDAO() {
		return finPlanEmiHolidayDAO;
	}

	public void setFinPlanEmiHolidayDAO(FinPlanEmiHolidayDAO finPlanEmiHolidayDAO) {
		this.finPlanEmiHolidayDAO = finPlanEmiHolidayDAO;
	}

	public void setExtendedFieldHeaderDAO(ExtendedFieldHeaderDAO extendedFieldHeaderDAO) {
		this.extendedFieldHeaderDAO = extendedFieldHeaderDAO;
	}

	public void setFinMandateService(FinMandateService finMandateService) {
		this.finMandateService = finMandateService;
	}

	public void setAuditHeaderDAO(AuditHeaderDAO auditHeaderDAO) {
		this.auditHeaderDAO = auditHeaderDAO;
	}

	public void setFinanceMainDAO(FinanceMainDAO financeMainDAO) {
		this.financeMainDAO = financeMainDAO;
	}

	public void setExtendedFieldDetailsService(ExtendedFieldDetailsService extendedFieldDetailsService) {
		this.extendedFieldDetailsService = extendedFieldDetailsService;
	}

	public void setFinAdvancePaymentsDAO(FinAdvancePaymentsDAO finAdvancePaymentsDAO) {
		this.finAdvancePaymentsDAO = finAdvancePaymentsDAO;
	}

	public void setDocumentDetailsDAO(DocumentDetailsDAO documentDetailsDAO) {
		this.documentDetailsDAO = documentDetailsDAO;
	}

	public void setDocumentService(DocumentService documentService) {
		this.documentService = documentService;
	}

	public void setDivisionDetailDAO(DivisionDetailDAO divisionDetailDAO) {
		this.divisionDetailDAO = divisionDetailDAO;
	}

	public FinReceiptHeaderDAO getFinReceiptHeaderDAO() {
		return finReceiptHeaderDAO;
	}

	public void setFinReceiptHeaderDAO(FinReceiptHeaderDAO finReceiptHeaderDAO) {
		this.finReceiptHeaderDAO = finReceiptHeaderDAO;
	}

	public FinFeeReceiptDAO getFinFeeReceiptDAO() {
		return finFeeReceiptDAO;
	}

	public void setFinFeeReceiptDAO(FinFeeReceiptDAO finFeeReceiptDAO) {
		this.finFeeReceiptDAO = finFeeReceiptDAO;
	}
	
	public void setCollateralStructureService(CollateralStructureService collateralStructureService) {
		this.collateralStructureService = collateralStructureService;
	}

	public void setRuleExecutionUtil(RuleExecutionUtil ruleExecutionUtil) {
		this.ruleExecutionUtil = ruleExecutionUtil;
	}
}
