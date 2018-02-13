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

import org.apache.commons.lang.StringUtils;
import org.apache.cxf.phase.PhaseInterceptorChain;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import com.pennant.app.constants.AccountEventConstants;
import com.pennant.app.util.APIHeader;
import com.pennant.app.util.DateUtility;
import com.pennant.app.util.ErrorUtil;
import com.pennant.app.util.FeeScheduleCalculator;
import com.pennant.app.util.ReferenceGenerator;
import com.pennant.app.util.ReferenceUtil;
import com.pennant.app.util.ScheduleCalculator;
import com.pennant.app.util.ScheduleGenerator;
import com.pennant.app.util.SessionUserDetails;
import com.pennant.app.util.SysParamUtil;
import com.pennant.backend.dao.audit.AuditHeaderDAO;
import com.pennant.backend.dao.documentdetails.DocumentDetailsDAO;
import com.pennant.backend.dao.finance.FinAdvancePaymentsDAO;
import com.pennant.backend.dao.finance.FinPlanEmiHolidayDAO;
import com.pennant.backend.dao.finance.FinanceMainDAO;
import com.pennant.backend.dao.finance.FinanceScheduleDetailDAO;
import com.pennant.backend.dao.finance.ManualAdviseDAO;
import com.pennant.backend.dao.lmtmasters.FinanceReferenceDetailDAO;
import com.pennant.backend.dao.solutionfactory.StepPolicyDetailDAO;
import com.pennant.backend.dao.solutionfactory.StepPolicyHeaderDAO;
import com.pennant.backend.dao.staticparms.ExtendedFieldHeaderDAO;
import com.pennant.backend.model.ErrorDetail;
import com.pennant.backend.model.WSReturnStatus;
import com.pennant.backend.model.WorkFlowDetails;
import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.model.bmtmasters.BankBranch;
import com.pennant.backend.model.collateral.CollateralAssignment;
import com.pennant.backend.model.collateral.CollateralSetup;
import com.pennant.backend.model.configuration.VASRecording;
import com.pennant.backend.model.customermasters.Customer;
import com.pennant.backend.model.customermasters.CustomerAddres;
import com.pennant.backend.model.customermasters.CustomerDetails;
import com.pennant.backend.model.documentdetails.DocumentDetails;
import com.pennant.backend.model.extendedfield.ExtendedField;
import com.pennant.backend.model.extendedfield.ExtendedFieldData;
import com.pennant.backend.model.extendedfield.ExtendedFieldHeader;
import com.pennant.backend.model.extendedfield.ExtendedFieldRender;
import com.pennant.backend.model.finance.FinAdvancePayments;
import com.pennant.backend.model.finance.FinFeeDetail;
import com.pennant.backend.model.finance.FinODPenaltyRate;
import com.pennant.backend.model.finance.FinPlanEmiHoliday;
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
import com.pennant.backend.model.financemanagement.FinFlagsDetail;
import com.pennant.backend.model.lmtmasters.FinanceWorkFlow;
import com.pennant.backend.model.mandate.Mandate;
import com.pennant.backend.model.solutionfactory.StepPolicyDetail;
import com.pennant.backend.model.solutionfactory.StepPolicyHeader;
import com.pennant.backend.service.bmtmasters.BankBranchService;
import com.pennant.backend.service.collateral.CollateralSetupService;
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
import com.pennant.backend.util.DisbursementConstants;
import com.pennant.backend.util.ExtendedFieldConstants;
import com.pennant.backend.util.FinanceConstants;
import com.pennant.backend.util.PennantApplicationUtil;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.VASConsatnts;
import com.pennant.backend.util.WorkFlowUtil;
import com.pennanttech.pennapps.core.InterfaceException;
import com.pennanttech.pennapps.core.engine.workflow.WorkflowEngine;
import com.pennanttech.pennapps.core.model.LoggedInUser;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pff.core.TableType;
import com.pennanttech.pff.document.DocumentService;
import com.pennanttech.util.APIConstants;
import com.pennanttech.ws.model.financetype.FinInquiryDetail;
import com.pennanttech.ws.model.financetype.FinanceInquiry;
import com.pennanttech.ws.service.APIErrorHandlerService;

public class CreateFinanceController extends SummaryDetailService {

	private static final Logger			logger		= Logger.getLogger(CreateFinanceController.class);

	private FinanceScheduleDetailDAO	financeScheduleDetailDAO;
	private CustomerDetailsService		customerDetailsService;
	private FinanceDetailService		financeDetailService;
	private StepPolicyDetailDAO			stepPolicyDetailDAO;
	private StepPolicyHeaderDAO			stepPolicyHeaderDAO;
	private BankBranchService			bankBranchService;
	private FeeDetailService			feeDetailService;
	private CollateralSetupService		collateralSetupService;
	private FinanceMainService			financeMainService;
	private JointAccountDetailService	jointAccountDetailService;
	private FinAdvancePaymentsService	finAdvancePaymentsService;
	private CustomerAddresService		customerAddresService;
	private ManualAdviseDAO				manualAdviseDAO;
	private FinanceReferenceDetailDAO	financeReferenceDetailDAO;
	private FinanceWorkFlowService		financeWorkFlowService;
	private FinPlanEmiHolidayDAO		finPlanEmiHolidayDAO;
	private ExtendedFieldHeaderDAO		extendedFieldHeaderDAO;
	private FinMandateService			finMandateService;
	private AuditHeaderDAO				auditHeaderDAO;
	private FinanceMainDAO				financeMainDAO;
	private ExtendedFieldDetailsService	extendedFieldDetailsService;
	private FinAdvancePaymentsDAO		finAdvancePaymentsDAO;
	private DocumentDetailsDAO			documentDetailsDAO;
	private DocumentService				documentService;


	protected transient WorkflowEngine	workFlow	= null;


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
			WorkFlowDetails workFlowDetails = null;
			String roleCode = null;
			String taskid = null;
			boolean stp= financeDetail.isStp();
			long  workFlowId = 0;
			if (financeMain.isQuickDisb()) {
				String finType = financeMain.getFinType();
				int finRefType = FinanceConstants.PROCEDT_LIMIT;
				String quickDisbCode = FinanceConstants.QUICK_DISBURSEMENT;
				String roles = financeReferenceDetailDAO.getAllowedRolesForQuickDisb(finType, finRefType,
						quickDisbCode);
				if(StringUtils.isBlank(roles)){
					FinanceDetail response = new FinanceDetail();
					doEmptyResponseObject(response);
					response.setReturnStatus(APIErrorHandlerService.getFailedStatus("90344"));
					return response;
				}
				roleCode = null;
				String[] role = roles.split(PennantConstants.DELIMITER_COMMA);
				for(String roleCod:role){
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
						workFlowId=workFlowDetails.getWorkFlowId();
					}
				}

			}
			financeMain.setRecordStatus(getRecordStatus(financeMain.isQuickDisb(),financeDetail.isStp()));
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
			financeMain.setNextTaskId(getNextTaskId(taskid,financeMain.isQuickDisb(),stp));
			financeMain.setNewRecord(true);
			financeMain.setLastMntOn(new Timestamp(System.currentTimeMillis()));
			financeMain.setLastMntBy(getLastMntBy(financeMain.isQuickDisb(),userDetails));
			financeMain.setRecordStatus(getRecordStatus(financeMain.isQuickDisb(),stp));
			financeMain.setFinSourceID(PennantConstants.FINSOURCE_ID_API);


			finScheduleData.setFinanceMain(financeMain);

			// set required mandatory values into finance details object
			doSetRequiredDetails(financeDetail, loanWithWIF,userDetails,stp);

			if (financeDetail.getFinScheduleData().getErrorDetails() != null) {
				for (ErrorDetail errorDetail : financeDetail.getFinScheduleData().getErrorDetails()) {
					FinanceDetail response = new FinanceDetail();
					doEmptyResponseObject(response);
					response.setReturnStatus(APIErrorHandlerService.getFailedStatus(errorDetail.getCode(),
							errorDetail.getError()));
					return response;
				}
			}

			if (!loanWithWIF) {
				// call schedule calculator
				finScheduleData.getFinanceMain().setCalculateRepay(true);
				finScheduleData = ScheduleGenerator.getNewSchd(finScheduleData);
				if (finScheduleData.getFinanceScheduleDetails().size() != 0) {

					finScheduleData = ScheduleCalculator.getCalSchd(finScheduleData, BigDecimal.ZERO);
					finScheduleData.setSchduleGenerated(true);

					// process planned EMI details
					doProcessPlanEMIHDays(finScheduleData);
					if (finScheduleData.getErrorDetails() != null) {
						for (ErrorDetail errorDetail : finScheduleData.getErrorDetails()) {
							FinanceDetail response = new FinanceDetail();
							doEmptyResponseObject(response);
							response.setReturnStatus(APIErrorHandlerService.getFailedStatus(errorDetail.getCode(),
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
				schdDetail.setRecordStatus(getRecordStatus(financeMain.isQuickDisb(),stp));
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
			financeDetail.setFinScheduleData(finScheduleData);

			AuditDetail auditDetail = new AuditDetail(PennantConstants.TRAN_WF, 1, null, financeDetail);
			AuditHeader auditHeader = new AuditHeader(financeDetail.getFinReference(), null, null, null, auditDetail,
					financeMain.getUserDetails(), new HashMap<String, ArrayList<ErrorDetail>>());

			APIHeader reqHeaderDetails = (APIHeader) PhaseInterceptorChain.getCurrentMessage().getExchange().get(APIHeader.API_HEADER_KEY);
			auditHeader.setApiHeader(reqHeaderDetails);

			// save the finance details into main table
			if(stp && !financeMain.isQuickDisb()){
				auditHeader = financeDetailService.doApprove(auditHeader, false);
			} else if(financeMain.isQuickDisb() || !stp) {
				String usrAction = "Approve";
				financeMain.setRecordStatus("Approve");
				String role = workFlow.firstTaskOwner();
				auditHeader = financeDetailService.executeWorkflowServiceTasks(auditHeader, role, usrAction, workFlow);
				//auditHeader = financeDetailService.saveOrUpdate(auditHeader, false);
			}

			FinanceDetail response = null;
			if (auditHeader.getOverideMessage() != null && auditHeader.getOverideMessage().size() > 0) {
				for (ErrorDetail errorDetail : auditHeader.getOverideMessage()) {
					response = new FinanceDetail();
					doEmptyResponseObject(response);
					response.setReturnStatus(APIErrorHandlerService.getFailedStatus(errorDetail.getCode(),
							errorDetail.getError()));
					return response;
				}
			}
			if (auditHeader.getErrorMessage() != null) {
				for (ErrorDetail errorDetail : auditHeader.getErrorMessage()) {
					response = new FinanceDetail();
					doEmptyResponseObject(response);
					response.setReturnStatus(APIErrorHandlerService.getFailedStatus(errorDetail.getCode(),
							errorDetail.getError()));
					return response;
				}
			}

			if (auditHeader.getAuditDetail().getErrorDetails() != null) {
				for (ErrorDetail errorDetail : auditHeader.getAuditDetail().getErrorDetails()) {
					response = new FinanceDetail();
					doEmptyResponseObject(response);
					response.setReturnStatus(APIErrorHandlerService.getFailedStatus(errorDetail.getCode(),
							errorDetail.getError()));
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

		} catch (InterfaceException intfcexp) {
			logger.error("Exception: ", intfcexp);
			APIErrorHandlerService.logUnhandledException(intfcexp);
			FinanceDetail response = new FinanceDetail();
			doEmptyResponseObject(response);
			response.setReturnStatus(
					APIErrorHandlerService.getFailedStatus(intfcexp.getErrorCode(), intfcexp.getErrorMessage()));
			return response;
		}

		catch (Exception e) {
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
	private String getRecordStatus(boolean quickDisb,boolean stp) {
		if(stp && !quickDisb) {
			return PennantConstants.RCD_STATUS_APPROVED;
		} else {
			return PennantConstants.RCD_STATUS_SUBMITTED;
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
	private void doSetRequiredDetails(FinanceDetail financeDetail, boolean loanWithWIF,LoggedInUser userDetails,boolean stp) 
			throws IllegalAccessException, InvocationTargetException {
		logger.debug("Entering");

		financeDetail.setModuleDefiner(FinanceConstants.FINSER_EVENT_ORG);
		financeDetail.setUserDetails(userDetails);
		financeDetail.setNewRecord(true);
		FinScheduleData finScheduleData = financeDetail.getFinScheduleData();
		FinanceMain financeMain = finScheduleData.getFinanceMain();
		financeMain.setVersion(1);
		financeMain.setFinIsActive(true);
		financeMain.setFinStatus(financeDetailService.getCustStatusByMinDueDays());

		if(financeMain.getMaturityDate() == null) {
			financeMain.setMaturityDate(financeMain.getCalMaturity());
		}
		if(financeMain.getNumberOfTerms() <= 0) {
			financeMain.setNumberOfTerms(financeMain.getCalTerms());
		}
		if(financeMain.getGrcPeriodEndDate() == null) {
			financeMain.setGrcPeriodEndDate(financeMain.getCalGrcEndDate());
		} 
		if(financeMain.getGraceTerms() <= 0) {
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
				
				// logging customer CIF as reference for create loan failure cases
				APIErrorHandlerService.logReference(customerDetails.getCustomer().getCustCIF());
			}
		}

		// process disbursement details
		doProcessDisbInstructions(financeDetail);

		//vas Details
		for(VASRecording vasRecording:finScheduleData.getVasRecordingList()){
			vasRecording.setRecordType(PennantConstants.RECORD_TYPE_NEW);
			vasRecording.setNewRecord(true);
			vasRecording.setVersion(1);
			vasRecording.setRecordStatus(getRecordStatus(financeMain.isQuickDisb(),stp));
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
				exdFieldRender.setRecordStatus(getRecordStatus(financeMain.isQuickDisb(),stp));
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
				if(extendedFields.size()<=0){
					Map<String, Object> mapValues = new HashMap<String, Object>();
					exdFieldRender.setMapValues(mapValues);
				}

				vasRecording.setExtendedFieldRender(exdFieldRender);
			}else {
				ExtendedFieldRender exdFieldRender = new ExtendedFieldRender();
				exdFieldRender.setReference(vasRecording.getVasReference());
				exdFieldRender.setLastMntOn(new Timestamp(System.currentTimeMillis()));
				exdFieldRender.setRecordStatus(getRecordStatus(financeMain.isQuickDisb(),stp));
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
				flagDetail.setRecordStatus(getRecordStatus(financeMain.isQuickDisb(),stp));
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
			jointAccDetail.setRecordStatus(getRecordStatus(financeMain.isQuickDisb(),stp));
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
				List<CustomerAddres> address = customerAddresService.getApprovedCustomerAddresById(guarantorDetail.getCustID());
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
			guarantorDetail.setRecordStatus(getRecordStatus(financeMain.isQuickDisb(),stp));
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
			detail.setRecordStatus(getRecordStatus(financeMain.isQuickDisb(),stp));
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
			CollateralSetup collateralSetup = collateralSetupService.getApprovedCollateralSetupById(detail.getCollateralRef());
			if (collateralSetup != null) {
				detail.setCollateralValue(collateralSetup.getCollateralValue());
			}
			detail.setNewRecord(true);
			detail.setRecordType(PennantConstants.RECORD_TYPE_NEW);
			detail.setModule(FinanceConstants.MODULE_NAME);
			detail.setUserDetails(financeMain.getUserDetails());
			detail.setLastMntBy(userDetails.getUserId());
			detail.setLastMntOn(new Timestamp(System.currentTimeMillis()));
			detail.setRecordStatus(getRecordStatus(financeMain.isQuickDisb(),stp));
			detail.setVersion(1);
			//workflow relates
			detail.setWorkflowId(financeMain.getWorkflowId());
			detail.setRoleCode(financeMain.getRoleCode());
			detail.setNextRoleCode(financeMain.getNextRoleCode());
			detail.setTaskId(financeMain.getTaskId());
			detail.setNextTaskId(financeMain.getNextTaskId());
		}

		// Set VAS reference as feeCode for VAS related fees
		for(FinFeeDetail feeDetail:finScheduleData.getFinFeeDetailList()) {
			for(VASRecording vasRecording:finScheduleData.getVasRecordingList()) {
				if (StringUtils.equals(feeDetail.getFinEvent(), AccountEventConstants.ACCEVENT_VAS_FEE) &&
						StringUtils.contains(feeDetail.getFeeTypeCode(), vasRecording.getProductCode())	) {
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
		// execute fee charges
		String finEvent = "";
		executeFeeCharges(financeDetail, finEvent);

		if(financeDetail.getFinScheduleData().getFinFeeDetailList() != null) {
			for(FinFeeDetail feeDetail: financeDetail.getFinScheduleData().getFinFeeDetailList()) {
				feeDetail.setRecordType(PennantConstants.RECORD_TYPE_NEW);
				if(!stp || financeMain.isQuickDisb())
					feeDetail.setRecordStatus("");
				feeDetail.setRcdVisible(false);
				feeDetail.setVersion(1);
				feeDetail.setWorkflowId(financeMain.getWorkflowId());
			}
		}

		// validate disbursement instructions
		if(!loanWithWIF) {
			FinanceDisbursement disbursementDetails = new FinanceDisbursement();
			disbursementDetails.setDisbDate(financeMain.getFinStartDate());
			disbursementDetails.setDisbAmount(financeMain.getFinAmount());
			disbursementDetails.setVersion(1);
			disbursementDetails.setDisbSeq(1);
			disbursementDetails.setDisbReqDate(DateUtility.getAppDate());
			disbursementDetails.setFeeChargeAmt(financeMain.getFeeChargeAmt());
			disbursementDetails.setInsuranceAmt(financeMain.getInsuranceAmt());
			disbursementDetails.setDisbAccountId(PennantApplicationUtil.unFormatAccountNumber(financeMain.getDisbAccountId()));
			finScheduleData.getDisbursementDetails().add(disbursementDetails);
		}

		// validate Disbursement instruction total amount
		validateDisbInstAmount(financeDetail);

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
		ExtendedFieldHeader extendedFieldHeader = extendedFieldHeaderDAO.getExtendedFieldHeaderByModuleName(
				ExtendedFieldConstants.MODULE_LOAN, financeMain.getFinCategory(), "");
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
		logger.debug("Leaving");
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
		LoggedInUser userDetails = financeDetail.getUserDetails();
		Mandate mandate = financeDetail.getMandate();
		if (mandate != null) {
			BankBranch bankBranch = new BankBranch();
			if (StringUtils.isNotBlank(mandate.getIFSC())) {
				bankBranch = bankBranchService.getBankBrachByIFSC(mandate.getIFSC());
			} else if (StringUtils.isNotBlank(mandate.getBankCode()) && StringUtils.isNotBlank(mandate.getBranchCode())) {
				bankBranch = bankBranchService.getBankBrachByCode(mandate.getBankCode(), mandate.getBranchCode());
			}
			financeDetail.getMandate().setNewRecord(true);
			financeDetail.getMandate().setRecordType(PennantConstants.RECORD_TYPE_NEW);
			financeDetail.getMandate().setVersion(1);

			financeDetail.getMandate().setLastMntBy(userDetails.getUserId());
			financeDetail.getMandate().setLastMntOn(new Timestamp(System.currentTimeMillis()));
			financeDetail.getMandate().setRecordStatus(getRecordStatus(financeMain.isQuickDisb(), financeDetail.isStp()));
			financeDetail.getMandate().setUserDetails(financeMain.getUserDetails());
			financeDetail.getMandate().setMandateCcy(SysParamUtil.getAppCurrency());

			//workflow
			financeDetail.getMandate().setWorkflowId(financeMain.getWorkflowId());
			financeDetail.getMandate().setRoleCode(financeMain.getRoleCode());
			financeDetail.getMandate().setNextRoleCode(financeMain.getNextRoleCode());
			financeDetail.getMandate().setTaskId(financeMain.getTaskId());
			financeDetail.getMandate().setNextTaskId(financeMain.getNextTaskId());

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
				finScheduleData.setErrorDetail(ErrorUtil.getErrorDetail(
						new ErrorDetail(erroDetails.getCode(), erroDetails.getParameters())));
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
	}

	private void executeFeeCharges(FinanceDetail financeDetail, String eventCode)
			throws IllegalAccessException, InvocationTargetException {
		FinScheduleData schData = financeDetail.getFinScheduleData();
		if (schData.getFinFeeDetailList() == null || schData.getFinFeeDetailList().isEmpty()) {
			if (StringUtils.isBlank(eventCode)) {
				eventCode = PennantApplicationUtil.getEventCode(schData.getFinanceMain().getFinStartDate());
			}
			feeDetailService.doProcessFeesForInquiry(financeDetail, eventCode, null);
		} else {
			feeDetailService.doExecuteFeeCharges(financeDetail, eventCode, null);
		}
		if (financeDetail.isStp()) {
			for (FinFeeDetail feeDetail : schData.getFinFeeDetailList()) {
				feeDetail.setWorkflowId(0);
			}
		}
	}
	private String getNextTaskId(String taksId,boolean qdp,boolean stp) {
		if(stp && !qdp){
			return null;
		} else  {
			return taksId+";";
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
		finScheduleData.setFinanceScheduleDetails(financeDetail.getFinScheduleData().getFinanceScheduleDetails());
		response.setFinScheduleData(finScheduleData);

		// set fee paid amounts based on schedule method
		finScheduleData.setFinFeeDetailList(getUpdatedFees(finScheduleData.getFinFeeDetailList()));

		// Fetch summary details
		FinanceSummary summary = getFinanceSummary(financeDetail);
		response.getFinScheduleData().setFinanceSummary(summary);

		// nullify the unnecessary object
		finScheduleData.setDisbursementDetails(null);
		finScheduleData.setRepayInstructions(null);
		finScheduleData.setRateInstruction(null);

		response.setFinScheduleData(finScheduleData);

		response.setJountAccountDetailList(null);
		response.setGurantorsDetailList(null);
		response.setDocumentDetailsList(null);
		response.setFinanceCollaterals(null);

		// for logging purpose
		APIErrorHandlerService.logReference(financeMain.getFinReference());
		
		logger.debug("Leaving");

		return response;
	}

	public FinanceDetail getFinanceDetails(String finReference) {
		logger.debug("Enetring");
		// for logging purpose
		APIErrorHandlerService.logReference(finReference);
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
			logger.error("Exception: ", e);
			APIErrorHandlerService.logUnhandledException(e);
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
		// for logging purpose
		APIErrorHandlerService.logReference(finReference);
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
					if (financeDetail.getFinScheduleData().getFinanceMain().isPlanEMIHAlw()) {
						processPlanEmiDays(finReference, financeDetail);
					}
				}
			}
			if (financeDetail != null) {
				FinODPenaltyRate finODPenaltyRate = financeDetail.getFinScheduleData().getFinODPenaltyRate();
				if(finODPenaltyRate!=null && StringUtils.equals(finODPenaltyRate.getODChargeType(), FinanceConstants.PENALTYTYPE_PERC_ONETIME)||
						StringUtils.equals(finODPenaltyRate.getODChargeType(), FinanceConstants.PENALTYTYPE_PERC_ON_DUEDAYS)
						|| StringUtils.equals(finODPenaltyRate.getODChargeType(),FinanceConstants.PENALTYTYPE_PERC_ON_PD_MTH)){
					BigDecimal totPerc = PennantApplicationUtil.formateAmount(finODPenaltyRate.getODChargeAmtOrPerc(), 2);
					finODPenaltyRate.setODChargeAmtOrPerc(totPerc);
				}
				financeDetail.getFinScheduleData().setFinODPenaltyRate(finODPenaltyRate);
				prepareResponse(financeDetail);
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
			if(financeDetail.getFinScheduleData().getPlanEMIHDates() != null){
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
		// for logging purpose
		APIErrorHandlerService.logReference(reference);
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
			logger.error("Exception: ", e);
			APIErrorHandlerService.logUnhandledException(e);
			FinanceInquiry financeInquiry= new FinanceInquiry();
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
		// for logging purpose
		APIErrorHandlerService.logReference(financeDetail.getFinReference());
		FinanceMain finMain = financeDetail.getFinScheduleData().getFinanceMain();
		TableType tableType = TableType.MAIN_TAB;
		if (finMain.isWorkflow()) {
			tableType = TableType.TEMP_TAB;
		}
		try {
			LoggedInUser userDetails = SessionUserDetails.getUserDetails(SessionUserDetails.getLogiedInUser());
			financeDetail.setUserDetails(userDetails);
			financeDetail.getFinScheduleData().getFinanceMain().setUserDetails(userDetails);

			if(financeDetail.getAdvancePaymentsList() != null && !financeDetail.getAdvancePaymentsList().isEmpty()) {
				WSReturnStatus status = updateDisbursementInst(financeDetail, tableType.getSuffix());
				if(StringUtils.isNotBlank(status.getReturnCode())) {
					return status;
				}
			}

			// Save or Update mandate details
			if(financeDetail.getMandate() != null) {
				updateFinMandateDetails(financeDetail, tableType.getSuffix());
			}

			// update Extended field details
			if(financeDetail.getExtendedDetails() != null && !financeDetail.getExtendedDetails().isEmpty()) {
				extendedFieldDetailsService.updateFinExtendedDetails(financeDetail, tableType.getSuffix());
			}
			
			// save or update document details
			if(financeDetail.getDocumentDetailsList() != null && !financeDetail.getDocumentDetailsList().isEmpty()) {
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
		if(extMandateId != Long.MIN_VALUE && extMandateId != 0) {
			financeDetail.getMandate().setNewRecord(false);
			financeDetail.getMandate().setRecordType(PennantConstants.RECORD_TYPE_UPD);
			financeDetail.getMandate().setVersion(1);
			auditHeader = getAuditHeader(financeDetail.getMandate(), PennantConstants.TRAN_UPD);
		} else {
			auditHeader = getAuditHeader(financeDetail.getMandate(), PennantConstants.TRAN_ADD);
		}
		finMandateService.saveOrUpdate(financeDetail, auditHeader, type);

		if(extMandateId == Long.MIN_VALUE || extMandateId == 0) {
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
		if(extAdvPayments != null && !extAdvPayments.isEmpty()) {
			finAdvancePaymentsDAO.deleteByFinRef(finReference, TableType.TEMP_TAB.getSuffix());
		}

		List<AuditDetail> auditDetails = new ArrayList<AuditDetail>();
		auditDetails.addAll(finAdvancePaymentsService.saveOrUpdate(financeDetail.getAdvancePaymentsList(),
				type, PennantConstants.TRAN_WF));

		AuditHeader auditHeader = getAuditHeader(financeDetail.getFinScheduleData().getFinanceMain(), PennantConstants.TRAN_WF);
		auditHeader.setAuditDetails(auditDetails); 
		auditHeaderDAO.addAudit(auditHeader);

		return returnStatus;
	}

	private AuditHeader getAuditHeader(FinanceMain finMain, String tranType) {
		AuditDetail auditDetail = new AuditDetail(tranType, 1, finMain.getBefImage(), finMain);
		return new AuditHeader(finMain.getFinReference(), null, null, null, auditDetail,
				finMain.getUserDetails(),  new HashMap<String, ArrayList<ErrorDetail>>());
	}
	
	private AuditHeader getAuditHeader(DocumentDetails documentDetails, String transType) {
		AuditDetail auditDetail = new AuditDetail(transType, 1, documentDetails.getBefImage(), documentDetails);
		return new AuditHeader(documentDetails.getReferenceId(), null, null, null, auditDetail,
				documentDetails.getUserDetails(),  new HashMap<String, ArrayList<ErrorDetail>>());
	}

	private AuditHeader getAuditHeader(Mandate aMandate, String tranType) {
		AuditDetail auditDetail = new AuditDetail(tranType, 1, aMandate.getBefImage(), aMandate);
		return new AuditHeader(String.valueOf(aMandate.getMandateID()), null, null, null, auditDetail,
				aMandate.getUserDetails(),  new HashMap<String, ArrayList<ErrorDetail>>());
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
		financeDetail.setFinFeeDetails(getUpdatedFees(finFeeDetail));

		// Bounce and manual advice fees if applicable
		String finReference = financeDetail.getFinScheduleData().getFinanceMain().getFinReference();
		List<ManualAdvise> manualAdviseFees = manualAdviseDAO.getManualAdviseByRef(finReference, 
				FinanceConstants.MANUAL_ADVISE_RECEIVABLE, "_View");
		if(manualAdviseFees != null && !manualAdviseFees.isEmpty()) {
			for(ManualAdvise advisedFees: manualAdviseFees) {
				FinFeeDetail feeDetail = new FinFeeDetail();
				if(advisedFees.getBounceID() > 0) {
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
}
