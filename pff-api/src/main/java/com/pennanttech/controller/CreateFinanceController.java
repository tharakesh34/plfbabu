package com.pennanttech.controller;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.apache.cxf.phase.PhaseInterceptorChain;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import com.aspose.words.SaveFormat;
import com.pennant.app.constants.AccountConstants;
import com.pennant.app.constants.AccountEventConstants;
import com.pennant.app.constants.ImplementationConstants;
import com.pennant.app.util.APIHeader;
import com.pennant.app.util.CDScheduleCalculator;
import com.pennant.app.util.DateUtility;
import com.pennant.app.util.ErrorUtil;
import com.pennant.app.util.FeeScheduleCalculator;
import com.pennant.app.util.PathUtil;
import com.pennant.app.util.ReferenceGenerator;
import com.pennant.app.util.ReferenceUtil;
import com.pennant.app.util.RuleExecutionUtil;
import com.pennant.app.util.ScheduleCalculator;
import com.pennant.app.util.ScheduleGenerator;
import com.pennant.app.util.SessionUserDetails;
import com.pennant.app.util.SysParamUtil;
import com.pennant.backend.dao.applicationmaster.AgreementDefinitionDAO;
import com.pennant.backend.dao.audit.AuditHeaderDAO;
import com.pennant.backend.dao.documentdetails.DocumentDetailsDAO;
import com.pennant.backend.dao.finance.FinAdvancePaymentsDAO;
import com.pennant.backend.dao.finance.FinFeeReceiptDAO;
import com.pennant.backend.dao.finance.FinPlanEmiHolidayDAO;
import com.pennant.backend.dao.finance.FinanceMainDAO;
import com.pennant.backend.dao.finance.FinanceScheduleDetailDAO;
import com.pennant.backend.dao.finance.ManualAdviseDAO;
import com.pennant.backend.dao.lmtmasters.FinanceReferenceDetailDAO;
import com.pennant.backend.dao.reason.deatil.ReasonDetailDAO;
import com.pennant.backend.dao.receipts.FinReceiptHeaderDAO;
import com.pennant.backend.dao.solutionfactory.StepPolicyDetailDAO;
import com.pennant.backend.dao.solutionfactory.StepPolicyHeaderDAO;
import com.pennant.backend.dao.staticparms.ExtendedFieldHeaderDAO;
import com.pennant.backend.dao.systemmasters.DivisionDetailDAO;
import com.pennant.backend.model.Notes;
import com.pennant.backend.model.WSReturnStatus;
import com.pennant.backend.model.WorkFlowDetails;
import com.pennant.backend.model.applicationmaster.AgreementDefinition;
import com.pennant.backend.model.applicationmaster.ReasonCode;
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
import com.pennant.backend.model.customermasters.CustomerPhoneNumber;
import com.pennant.backend.model.documentdetails.DocumentDetails;
import com.pennant.backend.model.extendedfield.ExtendedField;
import com.pennant.backend.model.extendedfield.ExtendedFieldData;
import com.pennant.backend.model.extendedfield.ExtendedFieldHeader;
import com.pennant.backend.model.extendedfield.ExtendedFieldRender;
import com.pennant.backend.model.finance.AgreementDetail;
import com.pennant.backend.model.finance.ChequeHeader;
import com.pennant.backend.model.finance.FinAdvancePayments;
import com.pennant.backend.model.finance.FinFeeDetail;
import com.pennant.backend.model.finance.FinFeeReceipt;
import com.pennant.backend.model.finance.FinODPenaltyRate;
import com.pennant.backend.model.finance.FinPlanEmiHoliday;
import com.pennant.backend.model.finance.FinReceiptHeader;
import com.pennant.backend.model.finance.FinScheduleData;
import com.pennant.backend.model.finance.FinanceDedup;
import com.pennant.backend.model.finance.FinanceDetail;
import com.pennant.backend.model.finance.FinanceDeviations;
import com.pennant.backend.model.finance.FinanceDisbursement;
import com.pennant.backend.model.finance.FinanceMain;
import com.pennant.backend.model.finance.FinanceScheduleDetail;
import com.pennant.backend.model.finance.FinanceStepPolicyDetail;
import com.pennant.backend.model.finance.FinanceSummary;
import com.pennant.backend.model.finance.GuarantorDetail;
import com.pennant.backend.model.finance.JointAccountDetail;
import com.pennant.backend.model.finance.ManualAdvise;
import com.pennant.backend.model.finance.Taxes;
import com.pennant.backend.model.finance.financetaxdetail.FinanceTaxDetail;
import com.pennant.backend.model.finance.psl.PSLDetail;
import com.pennant.backend.model.financemanagement.FinFlagsDetail;
import com.pennant.backend.model.lmtmasters.FinanceReferenceDetail;
import com.pennant.backend.model.lmtmasters.FinanceWorkFlow;
import com.pennant.backend.model.mandate.Mandate;
import com.pennant.backend.model.partnerbank.PartnerBank;
import com.pennant.backend.model.reason.details.ReasonDetails;
import com.pennant.backend.model.reason.details.ReasonHeader;
import com.pennant.backend.model.rmtmasters.FinTypePartnerBank;
import com.pennant.backend.model.rmtmasters.FinanceType;
import com.pennant.backend.model.rmtmasters.Promotion;
import com.pennant.backend.model.rulefactory.Notifications;
import com.pennant.backend.model.solutionfactory.StepPolicyDetail;
import com.pennant.backend.model.solutionfactory.StepPolicyHeader;
import com.pennant.backend.service.applicationmaster.AgreementDefinitionService;
import com.pennant.backend.service.bmtmasters.BankBranchService;
import com.pennant.backend.service.collateral.CollateralSetupService;
import com.pennant.backend.service.collateral.CollateralStructureService;
import com.pennant.backend.service.customermasters.CustomerAddresService;
import com.pennant.backend.service.customermasters.CustomerDetailsService;
import com.pennant.backend.service.dedup.DedupParmService;
import com.pennant.backend.service.extendedfields.ExtendedFieldDetailsService;
import com.pennant.backend.service.fees.FeeDetailService;
import com.pennant.backend.service.finance.FinAdvancePaymentsService;
import com.pennant.backend.service.finance.FinanceCancellationService;
import com.pennant.backend.service.finance.FinanceDetailService;
import com.pennant.backend.service.finance.FinanceDeviationsService;
import com.pennant.backend.service.finance.FinanceMainService;
import com.pennant.backend.service.finance.JointAccountDetailService;
import com.pennant.backend.service.lmtmasters.FinanceWorkFlowService;
import com.pennant.backend.service.mandate.FinMandateService;
import com.pennant.backend.service.notifications.NotificationsService;
import com.pennant.backend.service.partnerbank.PartnerBankService;
import com.pennant.backend.service.pdc.ChequeHeaderService;
import com.pennant.backend.service.rmtmasters.FinTypePartnerBankService;
import com.pennant.backend.service.rmtmasters.FinanceTypeService;
import com.pennant.backend.util.CollateralConstants;
import com.pennant.backend.util.DisbursementConstants;
import com.pennant.backend.util.ExtendedFieldConstants;
import com.pennant.backend.util.FinanceConstants;
import com.pennant.backend.util.MandateConstants;
import com.pennant.backend.util.PennantApplicationUtil;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.RuleReturnType;
import com.pennant.backend.util.SMTParameterConstants;
import com.pennant.backend.util.VASConsatnts;
import com.pennant.backend.util.WorkFlowUtil;
import com.pennant.util.AgreementEngine;
import com.pennant.util.AgreementGeneration;
import com.pennanttech.framework.security.core.User;
import com.pennanttech.pennapps.core.AppException;
import com.pennanttech.pennapps.core.InterfaceException;
import com.pennanttech.pennapps.core.engine.workflow.WorkflowEngine;
import com.pennanttech.pennapps.core.feature.ModuleUtil;
import com.pennanttech.pennapps.core.model.ErrorDetail;
import com.pennanttech.pennapps.core.model.LoggedInUser;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pff.core.TableType;
import com.pennanttech.pff.document.DocumentService;
import com.pennanttech.pff.notifications.service.NotificationService;
import com.pennanttech.service.impl.RemarksWebServiceImpl;
import com.pennanttech.util.APIConstants;
import com.pennanttech.ws.model.customer.AgreementRequest;
import com.pennanttech.ws.model.eligibility.AgreementData;
import com.pennanttech.ws.model.finance.MoveLoanStageRequest;
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
	private DedupParmService dedupParmService;
	private AgreementDefinitionService agreementDefinitionService;
	private NotificationsService notificationsService;
	private NotificationService notificationService;
	private AgreementGeneration agreementGeneration;
	private FinanceTypeService financeTypeService;
	private FinanceCancellationService financeCancellationService;
	private ChequeHeaderService chequeHeaderService;
	private RemarksWebServiceImpl remarksWebServiceImpl;
	private RemarksController remarksController;
	private PartnerBankService partnerBankService;
	private AgreementDefinitionDAO agreementDefinitionDAO;
	private ReasonDetailDAO reasonDetailDAO;
	private FinTypePartnerBankService finTypePartnerBankService;
	private FinanceDeviationsService deviationDetailsService;

	/**
	 * Method for process create finance request
	 * 
	 * @param financeDetail
	 * @return
	 */

	public FinanceDetail doCreateFinance(FinanceDetail financeDetail, boolean loanWithWIF) {
		logger.debug(Literal.ENTERING);

		String finReference = null;

		try {

			// financeMain details
			FinScheduleData finScheduleData = financeDetail.getFinScheduleData();
			FinanceMain financeMain = finScheduleData.getFinanceMain();
			FinanceType financeType = finScheduleData.getFinanceType();
			financeMain.setFinType(financeType.getFinType());

			// FIXME: PV 28AUG19. Seems FinReference already generated in
			// previous methods
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
			if (financeMain.getUserDetails() == null) {
				LoggedInUser userDetails = SessionUserDetails.getUserDetails(SessionUserDetails.getLogiedInUser());
				financeMain.setUserDetails(userDetails);
			}

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
			financeMain.setLastMntBy(getLastMntBy(financeMain.isQuickDisb(), financeMain.getUserDetails()));
			financeMain.setRecordStatus(getRecordStatus(financeMain.isQuickDisb(), stp));
			financeMain.setFinSourceID(PennantConstants.FINSOURCE_ID_API);
			// set vancode
			if (financeType.isAlwVan() && SysParamUtil.isAllowed(SMTParameterConstants.VAN_REQUIRED)) {
				financeType.setFinTypePartnerBankList(
						finTypePartnerBankService.getFinTypePartnerBanksList(financeMain.getFinType(), "_AView"));
				List<FinTypePartnerBank> finTypePartnerBankList = financeType.getFinTypePartnerBankList();
				if (CollectionUtils.isNotEmpty(finTypePartnerBankList)) {
					for (FinTypePartnerBank finTypePartnerBank : finTypePartnerBankList) {
						if (StringUtils.equals(finTypePartnerBank.getPurpose(), AccountConstants.PARTNERSBANK_RECEIPTS)
								&& finTypePartnerBank.isVanApplicable()) {
							PartnerBank bank = partnerBankService
									.getApprovedPartnerBankById(finTypePartnerBank.getPartnerBankID());
							if (bank != null && StringUtils.isNotBlank(bank.getVanCode())) {
								if (StringUtils.isNotBlank(financeMain.getFinReference())) {
									financeMain.setVanCode((bank.getVanCode().concat(financeMain.getFinReference())));
									break;
								}
							}
						}
					}
				}
			}
			// finScheduleData.setFinanceMain(financeMain);

			// set required mandatory values into finance details object

			doSetRequiredDetails(financeDetail, loanWithWIF, financeMain.getUserDetails(), stp, false, false);
			// PSD #146217 Disbursal Instruction is not getting created.
			// Disbursement Instruction is calculation fails if alwBpiTreatment is true so calling this after schedule calculation.
			if (!financeMain.isAlwBPI()) {
				if (stp) {
					finScheduleData.getDisbursementDetails().clear();
				}
				setDisbursements(financeDetail, loanWithWIF, false, false);
			}

			finScheduleData = financeDetail.getFinScheduleData();
			financeMain = finScheduleData.getFinanceMain();

			if (finScheduleData.getExternalReference() != null && !finScheduleData.getExternalReference().isEmpty()) {
				if (finScheduleData.isUpfrontAuto()) {
					adjustFeesAuto(finScheduleData);
				} else {

					adjustFees(finScheduleData);
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

			if (!loanWithWIF) {
				// call schedule calculator
				financeMain.setCalculateRepay(true);
				if (StringUtils.equals(FinanceConstants.PRODUCT_ODFACILITY, financeMain.getProductCategory())) {
					if (finScheduleData.getOverdraftScheduleDetails() != null) {
						finScheduleData.getOverdraftScheduleDetails().clear();
					}
					// To Rebuild the overdraft if any fields are changed
					financeMain.setEventFromDate(financeMain.getFinStartDate());
					finScheduleData = ScheduleCalculator.buildODSchedule(finScheduleData);
					financeDetail.setFinScheduleData(finScheduleData);
					financeMain.setLovDescIsSchdGenerated(true);

				} else if (StringUtils.equals(FinanceConstants.PRODUCT_CD, financeMain.getProductCategory())) {
					finScheduleData = ScheduleGenerator.getNewSchd(finScheduleData);
					if (finScheduleData.getFinanceScheduleDetails().size() != 0) {
						finScheduleData = CDScheduleCalculator.getCalSchd(finScheduleData);
						finScheduleData.setSchduleGenerated(true);
						adjustCDDownpay(financeDetail);
					}
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
				financeMain.setCalculateRepay(true);
				finScheduleData.setSchduleGenerated(true);
			}

			if (!finScheduleData.getErrorDetails().isEmpty()) {
				financeDetail.setFinScheduleData(finScheduleData);
				return financeDetail;
			}

			if (financeMain.isAlwBPI()) {
				if (stp) {
					finScheduleData.getDisbursementDetails().clear();
				}
				setDisbursements(financeDetail, loanWithWIF, false, false);
			}

			if (financeDetail.getFinScheduleData().getErrorDetails() != null) {
				for (ErrorDetail errorDetail : finScheduleData.getErrorDetails()) {
					FinanceDetail response = new FinanceDetail();
					doEmptyResponseObject(response);
					response.setReturnStatus(
							APIErrorHandlerService.getFailedStatus(errorDetail.getCode(), errorDetail.getError()));
					return response;
				}
			}

			// FIXME: PV 28AUG19. Why setting is required like set a = a?
			/*
			 * // Reset Data finScheduleData.getFinanceMain().setEqualRepay(financeMain. isEqualRepay());
			 * finScheduleData.getFinanceMain().setRecalType(financeMain. getRecalType());
			 * finScheduleData.getFinanceMain().setLastRepayDate(financeMain. getFinStartDate());
			 * finScheduleData.getFinanceMain().setLastRepayPftDate(financeMain. getFinStartDate());
			 * finScheduleData.getFinanceMain().setLastRepayRvwDate(financeMain. getFinStartDate());
			 * finScheduleData.getFinanceMain().setLastRepayCpzDate(financeMain. getFinStartDate());
			 */

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
				if (StringUtils.isBlank(schdDetail.getBaseRate())) {
					schdDetail.setBaseRate(null);
				}
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
				WSReturnStatus returnStatus = prepareAgrrementDetails(auditHeader);
				if (returnStatus != null && StringUtils.isNotBlank(returnStatus.getReturnCode())) {
					FinanceDetail response = new FinanceDetail();
					String[] valueParm = new String[1];
					valueParm[0] = "Loan Aggrement template ";
					doEmptyResponseObject(response);
					response.setReturnStatus(APIErrorHandlerService.getFailedStatus("API004", valueParm));
					return response;
				}
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
				// dedup check
				if (!stp) {
					List<FinanceDedup> financeDedupList = prepareFinanceDedup(role, financeDetail);
					if (CollectionUtils.isNotEmpty(financeDedupList)) {
						FinanceDetail response = new FinanceDetail();
						String[] valueParm = new String[1];
						valueParm[0] = "Loan Dedup";
						doEmptyResponseObject(response);
						response.setReturnStatus(APIErrorHandlerService.getFailedStatus("90343", valueParm));
						return response;
					}
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

				logger.debug(Literal.LEAVING);
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
			logger.error(Literal.EXCEPTION, e);
			APIErrorHandlerService.logUnhandledException(e);
			FinanceDetail response = new FinanceDetail();
			doEmptyResponseObject(response);
			response.setReturnStatus(APIErrorHandlerService.getFailedStatus());
			return response;
		}
		return null;
	}

	private WSReturnStatus prepareAgrrementDetails(AuditHeader auditHeader) {
		FinanceDetail financeDetail = (FinanceDetail) auditHeader.getAuditDetail().getModelData();

		List<Notifications> notifications = new ArrayList<>(1);
		List<FinanceReferenceDetail> aggrementList = new ArrayList<>(1);
		AgreementDefinition agreementDefinition = null;
		boolean isTemplateError = false;
		String templateValidateMsg = "";
		String accMsg = "";
		Set<String> allagrDataset = new HashSet<>();
		Map<String, AgreementDefinition> agrdefMap = new HashMap<>();
		Map<String, FinanceReferenceDetail> finRefMap = new HashMap<>();
		DocumentDetails documentDetails = null;
		List<DocumentDetails> agenDocList = new ArrayList<DocumentDetails>();

		String finType = financeDetail.getFinScheduleData().getFinanceMain().getFinType();
		List<Long> finRefIds = financeReferenceDetailDAO.getRefIdListByRefType(
				financeDetail.getFinScheduleData().getFinanceMain().getFinType(), FinanceConstants.FINSER_EVENT_ORG,
				PennantConstants.REC_ON_APPR, FinanceConstants.PROCEDT_TEMPLATE);
		if (!CollectionUtils.isEmpty(finRefIds)) {
			notifications = notificationsService.getApprovedNotificationsByRuleIdList(finRefIds);
			List<String> docCatogires = new ArrayList<>();
			String[] docTypes = null;
			for (Notifications mailNotification : notifications) {
				docTypes = notificationService.getAttchmentRuleResult(mailNotification.getRuleAttachment(),
						financeDetail);
				for (String docType : docTypes) {
					docCatogires.add(docType);
				}
			}
			List<FinanceReferenceDetail> finRefDetails = financeReferenceDetailDAO
					.getFinanceProcessEditorDetails(finType, FinanceConstants.FINSER_EVENT_ORG, "_FINVIEW");

			for (FinanceReferenceDetail financeReferenceDetail : finRefDetails) {
				if (FinanceConstants.PROCEDT_AGREEMENT == financeReferenceDetail.getFinRefType()) {
					aggrementList.add(financeReferenceDetail);
				}
			}
			for (FinanceReferenceDetail financeReferenceDetail : aggrementList) {
				long id = financeReferenceDetail.getFinRefId();
				agreementDefinition = getAgreementDefinitionService().getAgreementDefinitionById(id);
				for (String docType : docCatogires) {

					if (StringUtils.equals(agreementDefinition.getDocType(), docType)) {
						try {
							templateValidateMsg = validateTemplate(financeReferenceDetail); // If

							if ("Y".equals(templateValidateMsg)) {
								if (!isTemplateError) {
									allagrDataset.add(agreementDefinition.getAggImage());
									agrdefMap.put(agreementDefinition.getAggReportName(), agreementDefinition);
									finRefMap.put(agreementDefinition.getAggReportName(), financeReferenceDetail);
								}
							} else {
								accMsg = accMsg + "  " + templateValidateMsg;
								isTemplateError = true;
								continue;
							}

						} catch (Exception e) {
							String[] valueParm = new String[1];
							valueParm[0] = "Loan Aggrement template ";
							return APIErrorHandlerService.getFailedStatus("API004", valueParm);
						}
					}
				}
			}
			if (!agrdefMap.isEmpty()) {
				AgreementDetail agrData = getAgreementGeneration().getAggrementData(financeDetail,
						allagrDataset.toString(), SessionUserDetails.getLogiedInUser());
				for (String tempName : agrdefMap.keySet()) {

					AgreementDefinition aggdef = agrdefMap.get(tempName);
					try {
						documentDetails = autoGenerateAgreement(finRefMap.get(tempName), financeDetail, aggdef,
								financeDetail.getDocumentDetailsList(), agrData);
						if (documentDetails.getReturnStatus() != null
								&& StringUtils.isNotBlank(documentDetails.getReturnStatus().getReturnCode())) {
							return documentDetails.getReturnStatus();
						}
					} catch (Exception e) {
						e.printStackTrace();
					}
					agenDocList.add(documentDetails);

				}
				if (financeDetail.getDocumentDetailsList() == null) {
					financeDetail.setDocumentDetailsList(new ArrayList<DocumentDetails>());
				}
				financeDetail.getDocumentDetailsList().addAll(agenDocList);
				agrdefMap = null;
				finRefMap = null;
				allagrDataset = null;

			}
		}
		return null;
	}

	private DocumentDetails autoGenerateAgreement(FinanceReferenceDetail frefdata, FinanceDetail financeDetail,
			AgreementDefinition agreementDefinition, List<DocumentDetails> existingUploadDocList,
			AgreementDetail detail) throws Exception {
		logger.debug(Literal.ENTERING);
		DocumentDetails details = new DocumentDetails();

		try {
			if (financeDetail != null && financeDetail.getFinScheduleData() != null
					&& financeDetail.getFinScheduleData().getFinanceMain() != null) {
				FinanceMain lmain = financeDetail.getFinScheduleData().getFinanceMain();
				String finReference = lmain.getFinReference();
				String aggName = StringUtils.trimToEmpty(frefdata.getLovDescNamelov());
				String reportName = "";
				String aggPath = "", templateName = "";
				if (StringUtils.trimToEmpty(frefdata.getLovDescAggReportName()).contains("/")) {
					String aggRptName = StringUtils.trimToEmpty(frefdata.getLovDescAggReportName());
					templateName = aggRptName.substring(aggRptName.lastIndexOf("/") + 1, aggRptName.length());
				} else {
					templateName = frefdata.getLovDescAggReportName();
				}
				AgreementEngine engine = new AgreementEngine(aggPath);
				engine.setTemplate(templateName);
				engine.loadTemplate();
				engine.mergeFields(detail);
				getAgreementGeneration().setExtendedMasterDescription(financeDetail, engine);
				getAgreementGeneration().setFeeDetails(financeDetail, engine);

				// if (agreementDefinition.isAutoDownload()) {
				if (StringUtils.equals(agreementDefinition.getAggtype(), PennantConstants.DOC_TYPE_PDF)) {
					reportName = finReference + "_" + aggName + PennantConstants.DOC_TYPE_PDF_EXT;
					// engine.showDocument(this.window_documentDetailDialog,
					// reportName, SaveFormat.PDF);
				} else {
					reportName = finReference + "_" + aggName + PennantConstants.DOC_TYPE_WORD_EXT;
					// engine.showDocument(this.window_documentDetailDialog,
					// reportName, SaveFormat.DOCX);
				}
				// }

				DocumentDetails exstDetails = null;
				if (existingUploadDocList.size() > 0) {
					exstDetails = getExistDocDetails(existingUploadDocList, agreementDefinition, financeDetail);
				}

				if (exstDetails != null) {
					if (PennantConstants.DOC_TYPE_PDF.equals(agreementDefinition.getAggtype())) {
						exstDetails.setDocImage(engine.getDocumentInByteArray(SaveFormat.PDF));
					} else {
						exstDetails.setDocImage(engine.getDocumentInByteArray(SaveFormat.DOCX));
					}

					// since it is an existing document record has to be store
					// in document manager
					exstDetails.setDocRefId(Long.MIN_VALUE);
					return exstDetails;
				}

				details.setDocCategory(agreementDefinition.getDocType());
				if (PennantConstants.WORFLOW_MODULE_FINANCE.equals(agreementDefinition.getModuleName())) {
					details.setDocModule("Finance");
				} else {
					details.setDocModule(agreementDefinition.getModuleName());
				}
				details.setReferenceId(finReference);
				if (PennantConstants.DOC_TYPE_PDF.equals(agreementDefinition.getAggtype())) {
					details.setDocImage(engine.getDocumentInByteArray(SaveFormat.PDF));
				} else {
					details.setDocImage(engine.getDocumentInByteArray(SaveFormat.DOCX));
				}
				details.setDoctype(agreementDefinition.getAggtype());
				details.setDocName(reportName.substring(15));
				details.setDocReceivedDate(DateUtility.getTimestamp(SysParamUtil.getAppDate()));
				details.setVersion(1);
				details.setFinEvent(frefdata.getFinEvent());
				// details.setCategoryCode(agreementDefinition.getModuleName());
				details.setLastMntOn(DateUtility.getTimestamp(SysParamUtil.getAppDate()));
				details.setFinEvent(FinanceConstants.FINSER_EVENT_ORG);
				details.setRecordType(PennantConstants.RECORD_TYPE_NEW);
				details.setNewRecord(true);
				engine.close();
				engine = null;

			}
		} catch (Exception e) {
			if (e instanceof IllegalArgumentException && (e.getMessage().equals("Document site does not exist.")
					|| e.getMessage().equals("Template site does not exist.")
					|| e.getMessage().equals("Template does not exist."))) {

				String[] valueParm = new String[1];
				valueParm[0] = "Loan Aggrement template ";
				details.setReturnStatus(APIErrorHandlerService.getFailedStatus("API004", valueParm));
				return details;

			} else {
				String[] valueParm = new String[1];
				valueParm[0] = "Loan Aggrement template ";
				details.setReturnStatus(APIErrorHandlerService.getFailedStatus("API004", valueParm));
				return details;
			}
		}

		logger.debug(Literal.LEAVING);
		return details;

	}

	private DocumentDetails getExistDocDetails(List<DocumentDetails> exstDoclst,
			AgreementDefinition agreementDefinition, FinanceDetail financeDetail) {

		for (DocumentDetails docDetails : financeDetail.getDocumentDetailsList()) {
			if (agreementDefinition.getDocType().equalsIgnoreCase(docDetails.getDocCategory())) {
				// ### 25-08-2018 Ticket ID : 637
				if (PennantConstants.RECORD_TYPE_CAN.equalsIgnoreCase(docDetails.getRecordType())) {
					for (DocumentDetails existDocDetails : exstDoclst) {
						if (existDocDetails.getDocCategory().equalsIgnoreCase(agreementDefinition.getDocType())
								&& "ADD".equalsIgnoreCase(existDocDetails.getRecordType())) {
							exstDoclst.remove(existDocDetails);
							return null;
						}
					}
					return null;
				}

				// ###25-08-2018 - Ticket ID : 638 & 639
				// Document category template exists in this case user should
				// not upload same document category to document list
				// when document type is "WORD" then record replace with
				// Agreement
				if (agreementDefinition.getAggtype().equalsIgnoreCase(PennantConstants.DOC_TYPE_WORD)) {
					if (!(agreementDefinition.getAggReportName()).equalsIgnoreCase(docDetails.getDocName())) {
						if (docDetails.getRecordStatus().equalsIgnoreCase(PennantConstants.RCD_STATUS_SUBMITTED)
								|| docDetails.getRecordStatus()
										.equalsIgnoreCase(PennantConstants.RCD_STATUS_RESUBMITTED)) {
							docDetails.setDocName(agreementDefinition.getAggReportName());
							docDetails.setDoctype(agreementDefinition.getAggtype());
							return docDetails;
						}
						if (docDetails.getRecordStatus().isEmpty()) {
							exstDoclst.remove(docDetails);
							return null;
						}
						if (docDetails.getRecordStatus().equalsIgnoreCase(PennantConstants.RCD_STATUS_SAVED)) {
							docDetails.setRecordType(PennantConstants.RECORD_TYPE_CAN);
							return null;
						}
					}
					return docDetails;
				}
				// when document type is "PDF" then record replace with
				// Agreement
				if (agreementDefinition.getAggtype().equalsIgnoreCase(PennantConstants.DOC_TYPE_PDF)) {
					if (!(agreementDefinition.getAggName() + "." + agreementDefinition.getAggtype())
							.equalsIgnoreCase(docDetails.getDocName())) {
						if (docDetails.getRecordStatus().equalsIgnoreCase(PennantConstants.RCD_STATUS_SUBMITTED)
								|| docDetails.getRecordStatus()
										.equalsIgnoreCase(PennantConstants.RCD_STATUS_RESUBMITTED)) {
							docDetails.setDocName(agreementDefinition.getAggName() + "."
									+ agreementDefinition.getAggtype().toLowerCase());
							docDetails.setDoctype(PennantConstants.DOC_TYPE_PDF);
							return docDetails;
						}
						if (docDetails.getRecordStatus().isEmpty()) {
							exstDoclst.remove(docDetails);
							return null;
						}
						if (docDetails.getRecordStatus().equalsIgnoreCase(PennantConstants.RCD_STATUS_SAVED)) {
							docDetails.setRecordType(PennantConstants.RECORD_TYPE_CAN);
							return null;
						}
					}
					return docDetails;
				}
			}
		}
		return null;
	}

	private List<FinanceDedup> prepareFinanceDedup(String userRole, FinanceDetail aFinanceDetail) {
		// Data Preparation for Rule Executions
		Customer customer = aFinanceDetail.getCustomerDetails().getCustomer();
		FinanceDedup financeDedup = new FinanceDedup();
		financeDedup.setCustId(customer.getCustID());
		financeDedup.setCustCRCPR(customer.getCustCRCPR());
		financeDedup.setCustCIF(customer.getCustCIF());
		financeDedup.setCustFName(customer.getCustFName());
		financeDedup.setCustMName(customer.getCustMName());
		financeDedup.setCustLName(customer.getCustLName());
		financeDedup.setCustShrtName(customer.getCustShrtName());
		financeDedup.setCustMotherMaiden(customer.getCustMotherMaiden());
		financeDedup.setCustNationality(customer.getCustNationality());
		financeDedup.setCustParentCountry(customer.getCustParentCountry());
		financeDedup.setCustDOB(customer.getCustDOB());
		financeDedup.setMobileNumber(getCustMobileNum(aFinanceDetail));
		financeDedup.setTradeLicenceNo(customer.getCustTradeLicenceNum());

		// Check Customer is Existing or New Customer Object
		FinanceMain aFinanceMain = aFinanceDetail.getFinScheduleData().getFinanceMain();

		// finance data to set in to finance dedup
		financeDedup.setFinanceAmount(aFinanceMain.getFinAmount());
		financeDedup.setProfitAmount(aFinanceMain.getTotalGrossPft());
		financeDedup.setFinanceType(aFinanceMain.getFinType());
		financeDedup.setStartDate(aFinanceMain.getFinStartDate());
		financeDedup.setFinLimitRef(aFinanceMain.getFinLimitRef());

		financeDedup.setFinReference(aFinanceMain.getFinReference());
		financeDedup
				.setLikeCustFName(financeDedup.getCustFName() != null ? "%" + financeDedup.getCustFName() + "%" : "");
		financeDedup
				.setLikeCustMName(financeDedup.getCustMName() != null ? "%" + financeDedup.getCustMName() + "%" : "");
		financeDedup
				.setLikeCustLName(financeDedup.getCustLName() != null ? "%" + financeDedup.getCustLName() + "%" : "");

		// For Existing Customer/ New Customer
		List<FinanceDedup> loanDedup = new ArrayList<FinanceDedup>();
		List<FinanceDedup> dedupeRuleData = dedupParmService.fetchFinDedupDetails(userRole, financeDedup, null,
				aFinanceMain.getFinType());
		loanDedup.addAll(dedupeRuleData);
		return loanDedup;
	}

	// get the mobile number for Customer
	private String getCustMobileNum(FinanceDetail aFinanceDetail) {
		String custMobileNumber = "";
		if (aFinanceDetail.getCustomerDetails().getCustomerPhoneNumList() != null) {
			for (CustomerPhoneNumber custPhone : aFinanceDetail.getCustomerDetails().getCustomerPhoneNumList()) {
				if (custPhone.getPhoneTypeCode().equals(PennantConstants.PHONETYPE_MOBILE)) {
					custMobileNumber = PennantApplicationUtil.formatPhoneNumber(custPhone.getPhoneCountryCode(),
							custPhone.getPhoneAreaCode(), custPhone.getPhoneNumber());
					break;
				}
			}
		}
		return custMobileNumber;
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
		if (financeMain.isLegalRequired()) {
			String finType = financeMain.getFinType();
			int finRefType = FinanceConstants.PROCEDT_LIMIT;
			String quickDisbCode = FinanceConstants.PROCEDT_LEGAL_INIT;
			String roles = financeReferenceDetailDAO.getAllowedRolesByCode(finType, finRefType, quickDisbCode);
			boolean allowed = false;
			if (StringUtils.isNotBlank(roles)) {
				String[] roleCodes = roles.split(PennantConstants.DELIMITER_COMMA);
				for (String roleCod : roleCodes) {
					if (StringUtils.equals(financeMain.getRoleCode(), roleCod)) {
						allowed = true;
						break;
					}
				}
			}
			if (!allowed) {
				FinanceDetail response = new FinanceDetail();
				doEmptyResponseObject(response);
				String[] valueParm = new String[2];
				valueParm[1] = financeMain.getFinType();
				valueParm[0] = "LegalRequired";
				response.setReturnStatus(APIErrorHandlerService.getFailedStatus("90285", valueParm));
				return response;
			}
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
	 * @param moveLoanStage
	 * @throws InvocationTargetException
	 * @throws IllegalAccessException
	 */

	private void doSetRequiredDetails(FinanceDetail financeDetail, boolean loanWithWIF, LoggedInUser userDetails,
			boolean stp, boolean approve, boolean moveLoanStage)
			throws IllegalAccessException, InvocationTargetException {
		logger.debug(Literal.ENTERING);

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
		// FIXME: PV 28AUG19. Same data has been fetched two times. below and
		// line around 1204 with different methods. Only difference found is
		// order and
		// later method even fetching VAS details.. SO tried to make as one
		// query.
		// REF: CUST28AUG19

		if (financeMain.getCustID() > 0) {
			// customerDetails =
			// customerDetailsService.getCustomerDetailsById(financeMain.getCustID(),
			// true, "");
			customerDetails = customerDetailsService.getApprovedCustomerById(financeMain.getCustID());
			if (customerDetails != null) {
				customerDetails.setUserDetails(userDetails);
				financeDetail.setCustomerDetails(customerDetails);
			}
		}

		// FIXME: 28AUG19. Moved to post schedule creation to handle Consumer
		// Durables where default down payment calculated. METHOD.
		// setDisbursements
		/*
		 * // process disbursement details doProcessDisbInstructions(financeDetail, moveLoanStage);
		 */

		// set finAssetValue = FinCurrAssetValue when there is no maxDisbCheck
		FinanceType finType = financeDetail.getFinScheduleData().getFinanceType();
		// This is not applicable for Over Draft
		if (!financeDetail.getFinScheduleData().getFinanceMain().getProductCategory()
				.equals(FinanceConstants.PRODUCT_ODFACILITY)) {
			if (!finType.isAlwMaxDisbCheckReq()) {
				financeMain.setFinAssetValue(financeMain.getFinCurrAssetValue());
			}
		}

		// vas Details
		for (VASRecording vasRecording : finScheduleData.getVasRecordingList()) {
			vasRecording.setRecordType(PennantConstants.RECORD_TYPE_NEW);
			if (!moveLoanStage) {
				vasRecording.setNewRecord(true);
			}
			vasRecording.setVersion(1);
			vasRecording.setRecordStatus(
					moveLoanStage ? financeMain.getRecordStatus() : getRecordStatus(financeMain.isQuickDisb(), stp));
			vasRecording.setVasReference(ReferenceUtil.generateVASRef());
			vasRecording.setPostingAgainst(VASConsatnts.VASAGAINST_FINANCE);
			vasRecording.setVasStatus("N");
			// workflow related
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
				exdFieldRender.setRecordStatus(moveLoanStage ? financeMain.getRecordStatus()
						: getRecordStatus(financeMain.isQuickDisb(), stp));
				exdFieldRender.setLastMntBy(userDetails.getUserId());
				exdFieldRender.setSeqNo(++seqNo);
				if (!moveLoanStage) {
					exdFieldRender.setNewRecord(true);
				}
				exdFieldRender.setRecordType(PennantConstants.RECORD_TYPE_NEW);
				exdFieldRender.setVersion(1);
				// workflow related
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
				exdFieldRender.setRecordStatus(moveLoanStage ? financeMain.getRecordStatus()
						: getRecordStatus(financeMain.isQuickDisb(), stp));
				exdFieldRender.setLastMntBy(userDetails.getUserId());
				exdFieldRender.setSeqNo(0);
				if (!moveLoanStage) {
					exdFieldRender.setNewRecord(true);
				}
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
				if (!moveLoanStage) {
					flagDetail.setNewRecord(true);
				}
				flagDetail.setVersion(1);
				flagDetail.setLastMntBy(userDetails.getUserId());
				flagDetail.setLastMntOn(new Timestamp(System.currentTimeMillis()));
				flagDetail.setRecordStatus(moveLoanStage ? financeMain.getRecordStatus()
						: getRecordStatus(financeMain.isQuickDisb(), stp));
				flagDetail.setUserDetails(financeMain.getUserDetails());
				// workflow related
				flagDetail.setWorkflowId(financeMain.getWorkflowId());
				flagDetail.setRoleCode(financeMain.getRoleCode());
				flagDetail.setNextRoleCode(financeMain.getNextRoleCode());
				flagDetail.setTaskId(financeMain.getTaskId());
				flagDetail.setNextTaskId(financeMain.getNextTaskId());
			}
		}

		// process mandate details
		doProcessMandate(financeDetail, moveLoanStage);

		// co-applicant details
		for (JointAccountDetail jointAccDetail : financeDetail.getJountAccountDetailList()) {
			jointAccDetail.setFinReference(financeMain.getFinReference());
			jointAccDetail.setRecordType(PennantConstants.RECORD_TYPE_NEW);
			if (!moveLoanStage) {
				jointAccDetail.setNewRecord(true);
			}
			jointAccDetail.setLastMntBy(userDetails.getUserId());
			jointAccDetail.setLastMntOn(new Timestamp(System.currentTimeMillis()));
			jointAccDetail.setRecordStatus(
					moveLoanStage ? financeMain.getRecordStatus() : getRecordStatus(financeMain.isQuickDisb(), stp));
			jointAccDetail.setUserDetails(financeMain.getUserDetails());
			jointAccDetail.setVersion(1);
			// workflow
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
			if (!moveLoanStage) {
				guarantorDetail.setNewRecord(true);
			}
			guarantorDetail.setLastMntBy(userDetails.getUserId());
			guarantorDetail.setLastMntOn(new Timestamp(System.currentTimeMillis()));
			guarantorDetail.setRecordStatus(
					moveLoanStage ? financeMain.getRecordStatus() : getRecordStatus(financeMain.isQuickDisb(), stp));
			guarantorDetail.setUserDetails(financeMain.getUserDetails());
			guarantorDetail.setVersion(1);
			// workflow
			guarantorDetail.setWorkflowId(financeMain.getWorkflowId());
			guarantorDetail.setRoleCode(financeMain.getRoleCode());
			guarantorDetail.setNextRoleCode(financeMain.getNextRoleCode());
			guarantorDetail.setTaskId(financeMain.getTaskId());
			guarantorDetail.setNextTaskId(financeMain.getNextTaskId());
		}

		// document details
		for (DocumentDetails detail : financeDetail.getDocumentDetailsList()) {
			if (!moveLoanStage) {
				detail.setNewRecord(true);
			}
			detail.setRecordType(PennantConstants.RECORD_TYPE_NEW);
			detail.setDocModule(FinanceConstants.MODULE_NAME);
			detail.setUserDetails(financeMain.getUserDetails());
			detail.setVersion(1);
			detail.setRecordStatus(
					moveLoanStage ? financeMain.getRecordStatus() : getRecordStatus(financeMain.isQuickDisb(), stp));
			// workflow relates
			detail.setWorkflowId(financeMain.getWorkflowId());
			detail.setRoleCode(financeMain.getRoleCode());
			detail.setNextRoleCode(financeMain.getNextRoleCode());
			detail.setTaskId(financeMain.getTaskId());
			detail.setNextTaskId(financeMain.getNextTaskId());
			if (detail.getDocRefId() == null) {
				detail.setDocRefId(Long.MIN_VALUE);
			}
		}

		// setting required values which are not received from API
		// FIXME: PV 28AUG19. Same data has been fetched two times. below and
		// line 1204 with different methods. Only difference found is order and
		// later method even fetching VAS details.. SO tried to make as one
		// query.
		// REF: CUST28AUG19
		/*
		 * financeDetail.setFinScheduleData(finScheduleData); if (financeMain.getCustID() > 0) { CustomerDetails
		 * custDetails = customerDetailsService.getApprovedCustomerById(financeMain.getCustID( ));
		 * financeDetail.setCustomerDetails(custDetails); }
		 */

		// CollateralAssignment details
		for (CollateralAssignment detail : financeDetail.getCollateralAssignmentList()) {

			if (StringUtils.isNotBlank(detail.getCollateralRef())) {
				CollateralSetup collateralSetup = collateralSetupService
						.getApprovedCollateralSetupById(detail.getCollateralRef());
				if (collateralSetup != null) {
					detail.setCollateralValue(collateralSetup.getCollateralValue());
				}
				if (!moveLoanStage) {
					detail.setNewRecord(true);
				}
				detail.setRecordType(PennantConstants.RECORD_TYPE_NEW);
				detail.setModule(FinanceConstants.MODULE_NAME);
				detail.setUserDetails(financeMain.getUserDetails());
				detail.setLastMntBy(userDetails.getUserId());
				detail.setLastMntOn(new Timestamp(System.currentTimeMillis()));
				detail.setRecordStatus(moveLoanStage ? financeMain.getRecordStatus()
						: getRecordStatus(financeMain.isQuickDisb(), stp));
				detail.setVersion(1);
				// workflow relates
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
			Boolean flag = false;
			for (CollateralAssignment detail : financeDetail.getCollateralAssignmentList()) {
				for (CollateralSetup collsetup : financeDetail.getCollaterals()) {
					if (StringUtils.isNotBlank(detail.getAssignmentReference())
							&& StringUtils.isNotBlank(collsetup.getAssignmentReference())) {
						if (StringUtils.equals(detail.getAssignmentReference(), collsetup.getAssignmentReference())) {
							processCollateralsetupDetails(userDetails, stp, financeMain, customerDetails, detail,
									collsetup, moveLoanStage);
							flag = true;
							curAssignValue = curAssignValue.add(collsetup.getBankValuation()
									.multiply(detail.getAssignPerc() == null ? BigDecimal.ZERO : detail.getAssignPerc())
									.divide(new BigDecimal(100), 0, RoundingMode.HALF_DOWN));
							totalAvailAssignValue = totalAvailAssignValue
									.add(collsetup.getBankValuation().subtract(curAssignValue));
						}
					}
				}
			}
			if (!flag) {
				financeDetail.setCollaterals(null);
			}
			// Collateral coverage will be calculated based on the flag
			// "Partially Secured?â€� defined loan type.
			if (!financeDetail.getFinScheduleData().getFinanceType().isPartiallySecured() && flag) {
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

		if (financeDetail.getFinanceTaxDetail() != null) {
			FinanceTaxDetail financeTaxDetail = financeDetail.getFinanceTaxDetail();
			financeTaxDetail.setFinReference(financeMain.getFinReference());
			financeTaxDetail.setRecordType(PennantConstants.RECORD_TYPE_NEW);
			if (!moveLoanStage) {
				financeTaxDetail.setNewRecord(true);
			}
			financeTaxDetail.setLastMntBy(userDetails.getUserId());
			financeTaxDetail.setLastMntOn(new Timestamp(System.currentTimeMillis()));
			financeTaxDetail.setRecordStatus(
					moveLoanStage ? financeMain.getRecordStatus() : getRecordStatus(financeMain.isQuickDisb(), stp));
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
		if (!moveLoanStage) {
			executeFeeCharges(financeDetail, finEvent, enquiry);
		}

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
					// feeDetail.setAlwModifyFee(true);
					feeDetail.setAlwModifyFeeSchdMthd(true);
					feeDetail.setCalculationType(PennantConstants.FEE_CALCULATION_TYPE_FIXEDAMOUNT);
					// Fee Details set to the VasRecording
					vasRecording.setWaivedAmt(feeDetail.getWaivedAmount());
					vasRecording.setPaidAmt(feeDetail.getPaidAmount());
					feeDetail.setActualAmount(vasRecording.getFee());
					feeDetail.setVersion(1);
					feeDetail.setNewRecord(true);
					feeDetail.setRecordType(PennantConstants.RCD_ADD);
					feeDetail.setLastMntBy(userDetails.getUserId());
					feeDetail.setLastMntOn(new Timestamp(System.currentTimeMillis()));
					feeDetail.setWorkflowId(financeMain.getWorkflowId());
					feeDetail.setOriginationFee(true);
					feeDetail.setFeeTypeID(0);
					feeDetail.setFeeSeq(0);
					feeDetail.setFeeOrder(0);

				}
			}
		}

		// FIXME: 28AUG19. Moved to post schedule creation to handle Consumer
		// Durables where default down payment calculated. METHOD.
		// setDisbursements

		// FIX ME: this should be removed from SetDisbursements
		// validate disbursement instructions
		if (!loanWithWIF && !financeDetail.getFinScheduleData().getFinanceMain().getProductCategory()
				.equals(FinanceConstants.PRODUCT_ODFACILITY)) {
			if (!approve && !moveLoanStage) {
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

		//pslDetails defaults
		PSLDetail pslDetail = financeDetail.getPslDetail();
		if (pslDetail != null) {
			if (!moveLoanStage) {
				pslDetail.setNewRecord(true);
				pslDetail.setRecordType(PennantConstants.RECORD_TYPE_NEW);
				pslDetail.setVersion(1);
				pslDetail.setLastMntBy(userDetails.getUserId());
				pslDetail.setLastMntOn(new Timestamp(System.currentTimeMillis()));
				pslDetail.setRecordStatus(PennantConstants.RCD_STATUS_APPROVED);
			}

			if (!stp) {
				pslDetail.setRecordStatus(moveLoanStage ? financeMain.getRecordStatus()
						: getRecordStatus(financeMain.isQuickDisb(), financeDetail.isStp()));
				pslDetail.setUserDetails(financeMain.getUserDetails());

				pslDetail.setWorkflowId(financeMain.getWorkflowId());
				pslDetail.setRoleCode(financeMain.getRoleCode());
				pslDetail.setNextRoleCode(financeMain.getNextRoleCode());
				pslDetail.setTaskId(financeMain.getTaskId());
				pslDetail.setNextTaskId(financeMain.getNextTaskId());
				pslDetail.setFinReference(financeMain.getFinReference());
			}
		}

		// process Extended field details
		// Get the ExtendedFieldHeader for given module and subModule
		// ### 02-05-2018-Start- story #334 Extended fields for loan servicing
		ExtendedFieldHeader extendedFieldHeader = extendedFieldHeaderDAO.getExtendedFieldHeaderByModuleName(
				ExtendedFieldConstants.MODULE_LOAN, financeMain.getFinCategory(), FinanceConstants.FINSER_EVENT_ORG,
				"");
		// ### 02-05-2018-END

		financeDetail.setExtendedFieldHeader(extendedFieldHeader);

		List<ExtendedField> extendedFields = financeDetail.getExtendedDetails();
		if (extendedFieldHeader != null) {
			int seqNo = 0;
			ExtendedFieldRender exdFieldRender = new ExtendedFieldRender();
			exdFieldRender.setReference(financeMain.getFinReference());
			exdFieldRender.setLastMntOn(new Timestamp(System.currentTimeMillis()));
			if (!moveLoanStage) {
				exdFieldRender.setRecordStatus(PennantConstants.RCD_STATUS_APPROVED);
			} else {
				exdFieldRender.setRecordStatus(financeMain.getRecordStatus());
			}
			exdFieldRender.setLastMntBy(userDetails.getUserId());
			exdFieldRender.setSeqNo(++seqNo);
			if (!moveLoanStage) {
				exdFieldRender.setNewRecord(true);
			}
			exdFieldRender.setRecordType(PennantConstants.RECORD_TYPE_NEW);
			exdFieldRender.setVersion(1);
			exdFieldRender.setTypeCode(financeDetail.getExtendedFieldHeader().getSubModuleName());
			exdFieldRender.setWorkflowId(financeMain.getWorkflowId());

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

		// set's the default chequeHeader to the financeDetail if chequeCapture
		// is required.
		if (MandateConstants.TYPE_PDC.equals(financeMain.getFinRepayMethod()) || finType.isChequeCaptureReq()) {
			doSetDefaultChequeHeader(financeDetail, moveLoanStage);
		}
		logger.debug(Literal.LEAVING);
	}

	private void processCollateralsetupDetails(LoggedInUser userDetails, boolean stp, FinanceMain financeMain,
			CustomerDetails customerDetails, CollateralAssignment detail, CollateralSetup colSetup,
			boolean moveLoanStage) {
		// collateral setup defaulting
		colSetup.setUserDetails(financeMain.getUserDetails());
		colSetup.setSourceId(APIConstants.FINSOURCE_ID_API);
		colSetup.setRecordStatus(
				moveLoanStage ? financeMain.getRecordStatus() : getRecordStatus(financeMain.isQuickDisb(), stp));
		colSetup.setLastMntOn(new Timestamp(System.currentTimeMillis()));
		colSetup.setLastMntBy(userDetails.getUserId());
		colSetup.setCollateralRef(ReferenceUtil.generateCollateralRef());
		colSetup.setDepositorId(financeMain.getCustID());
		colSetup.setDepositorCif(financeMain.getCustCIF());
		if (!moveLoanStage) {
			colSetup.setNewRecord(true);
		}
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
				thirdPartyColl.setRecordStatus(moveLoanStage ? financeMain.getRecordStatus()
						: getRecordStatus(financeMain.isQuickDisb(), stp));
				thirdPartyColl.setUserDetails(userDetails);

				// fetch customer id from cif
				Customer thrdPartyCustomer = customerDetailsService.getCustomerByCIF(thirdPartyColl.getCustCIF());
				if (thrdPartyCustomer != null) {
					thirdPartyColl.setCustomerId(thrdPartyCustomer.getCustID());
				}
				thirdPartyColl.setCollateralRef(colSetup.getCollateralRef());
				if (!moveLoanStage) {
					thirdPartyColl.setNewRecord(true);
				}
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
				coOwnerDetail.setRecordStatus(moveLoanStage ? financeMain.getRecordStatus()
						: getRecordStatus(financeMain.isQuickDisb(), stp));
				coOwnerDetail.setUserDetails(userDetails);
				coOwnerDetail.setLastMntBy(userDetails.getUserId());

				Customer coOwnerCustomer = customerDetailsService.getCustomerByCIF(coOwnerDetail.getCoOwnerCIF());
				if (coOwnerCustomer != null) {
					coOwnerDetail.setCustomerId(coOwnerCustomer.getCustID());
				}

				coOwnerDetail.setCollateralRef(colSetup.getCollateralRef());
				if (!moveLoanStage) {
					coOwnerDetail.setNewRecord(true);
				}
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
			collateralStructure = collateralStructureService.getApprovedCollateralStructureByType(collateralType);
		} else if (StringUtils.isNotBlank(colSetup.getCollateralRef())) {
			CollateralSetup setup = collateralSetupService.getApprovedCollateralSetupById(colSetup.getCollateralRef());
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
				exdFieldRender.setRecordStatus(moveLoanStage ? financeMain.getRecordStatus()
						: getRecordStatus(financeMain.isQuickDisb(), stp));
				exdFieldRender.setLastMntBy(userDetails.getUserId());

				exdFieldRender.setSeqNo(++seqNo);
				if (!moveLoanStage) {
					exdFieldRender.setNewRecord(true);
				}
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

				Map<String, Object> declaredMap = colSetup.getCustomerDetails().getCustomer().getDeclaredFieldValues();
				declaredMap.put("collateralType", colSetup.getCollateralType());
				declaredMap.put("collateralCcy", colSetup.getCollateralCcy());
				try {
					ruleResult = ruleExecutionUtil.executeRule(collateralStructure.getSQLRule(), declaredMap,
							colSetup.getCollateralCcy(), RuleReturnType.DECIMAL);
				} catch (Exception e) {
					APIErrorHandlerService.logUnhandledException(e);
					logger.error(Literal.EXCEPTION, e);
					ruleResult = "0";
				}
				colSetup.setBankLTV(ruleResult == null ? BigDecimal.ZERO : new BigDecimal(ruleResult.toString()));
			}

			// calculate Bank Valuation
			BigDecimal ltvValue = colSetup.getBankLTV();
			if (colSetup.getSpecialLTV() != null && colSetup.getSpecialLTV().compareTo(BigDecimal.ZERO) > 0) {
				ltvValue = colSetup.getSpecialLTV();
			}

			BigDecimal colValue = colSetup.getCollateralValue().multiply(ltvValue).divide(new BigDecimal(100), 0,
					RoundingMode.HALF_DOWN);
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
				if (!moveLoanStage) {
					documentDetail.setNewRecord(true);
				}
				documentDetail.setRecordType(PennantConstants.RECORD_TYPE_NEW);
				documentDetail.setWorkflowId(financeMain.getWorkflowId());
				documentDetail.setRoleCode(financeMain.getRoleCode());
				documentDetail.setNextRoleCode(financeMain.getNextRoleCode());
				documentDetail.setTaskId(financeMain.getTaskId());
				documentDetail.setNextTaskId(financeMain.getNextTaskId());
			}
		}
		detail.setCollateralValue(colSetup.getCollateralValue());
		if (!moveLoanStage) {
			detail.setNewRecord(true);
		}
		detail.setRecordType(PennantConstants.RECORD_TYPE_NEW);
		detail.setModule(FinanceConstants.MODULE_NAME);
		detail.setUserDetails(financeMain.getUserDetails());
		detail.setLastMntBy(userDetails.getUserId());
		detail.setLastMntOn(new Timestamp(System.currentTimeMillis()));
		detail.setRecordStatus(
				moveLoanStage ? financeMain.getRecordStatus() : getRecordStatus(financeMain.isQuickDisb(), stp));
		detail.setVersion(1);
		// workflow relates
		detail.setWorkflowId(financeMain.getWorkflowId());
		detail.setRoleCode(financeMain.getRoleCode());
		detail.setNextRoleCode(financeMain.getNextRoleCode());
		detail.setTaskId(financeMain.getTaskId());
		detail.setNextTaskId(financeMain.getNextTaskId());
		detail.setCollateralRef(colSetup.getCollateralRef());
	}

	/**
	 * @param financeDetail
	 * @param moveLoanStage
	 * @param userDetails
	 * @param stp
	 * @param financeMain
	 */
	private void doProcessMandate(FinanceDetail financeDetail, boolean moveLoanStage) {
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
			if (!moveLoanStage) {
				financeDetail.getMandate().setNewRecord(true);
			}
			financeDetail.getMandate().setRecordType(PennantConstants.RECORD_TYPE_NEW);
			financeDetail.getMandate().setVersion(1);

			financeDetail.getMandate().setLastMntBy(userDetails.getUserId());
			financeDetail.getMandate().setLastMntOn(new Timestamp(System.currentTimeMillis()));
			financeDetail.getMandate().setRecordStatus(moveLoanStage ? financeMain.getRecordStatus()
					: getRecordStatus(financeMain.isQuickDisb(), financeDetail.isStp()));
			financeDetail.getMandate().setUserDetails(financeMain.getUserDetails());
			financeDetail.getMandate().setMandateCcy(SysParamUtil.getAppCurrency());
			financeDetail.getMandate().setEntityCode(entityCode);
			// workflow
			/*
			 * financeDetail.getMandate().setWorkflowId(financeMain. getWorkflowId());
			 * financeDetail.getMandate().setRoleCode(financeMain.getRoleCode()) ;
			 * financeDetail.getMandate().setNextRoleCode(financeMain. getNextRoleCode());
			 * financeDetail.getMandate().setTaskId(financeMain.getTaskId());
			 * financeDetail.getMandate().setNextTaskId(financeMain. getNextTaskId());
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
			financeDetail.getMandate().setInputDate(SysParamUtil.getAppDate());
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
	 * Method for Creating New Disbursement Instruction for the Transaction
	 * 
	 * Default Payment Type : IFT
	 * 
	 * @param financeDetail
	 * @param moveLoanStage
	 * @return
	 */
	private FinAdvancePayments createNewDisbInst(FinanceDetail financeDetail, boolean moveLoanStage) {

		FinScheduleData finScheduleData = financeDetail.getFinScheduleData();
		FinanceMain financeMain = finScheduleData.getFinanceMain();
		LoggedInUser userDetails = financeDetail.getUserDetails();

		FinAdvancePayments advPayment = new FinAdvancePayments();
		advPayment.setFinReference(financeMain.getFinReference());
		advPayment.setRecordType(PennantConstants.RECORD_TYPE_NEW);
		advPayment.setPaymentDetail(DisbursementConstants.PAYMENT_DETAIL_CUSTOMER);
		advPayment.setNewRecord(true);
		advPayment.setVersion(1);
		advPayment.setLastMntBy(userDetails.getUserId());
		advPayment.setLastMntOn(new Timestamp(System.currentTimeMillis()));
		if (financeDetail.isStp()) {
			advPayment.setRecordStatus(PennantConstants.RCD_STATUS_APPROVED);
		}
		if (moveLoanStage) {
			advPayment.setRecordStatus(financeMain.getRecordStatus());
		} else if (financeDetail.isStp()) {
			advPayment.setRecordStatus(PennantConstants.RCD_STATUS_APPROVED);
		}

		advPayment.setUserDetails(financeMain.getUserDetails());
		advPayment.setPaymentSeq(1);
		advPayment.setDisbSeq(1);
		advPayment.setActive(true);
		advPayment.setDisbCCy(financeMain.getFinCcy());
		Promotion promotion = financeDetail.getPromotion();
		BigDecimal dbdAmount = BigDecimal.ZERO;
		if (promotion != null && promotion.isDbd()) {
			dbdAmount = financeMain.getFinAmount().multiply(promotion.getDbdPerc()).divide(new BigDecimal(100), 0,
					RoundingMode.HALF_DOWN);
		}
		advPayment.setAmtToBeReleased(financeMain.getFinAmount().subtract(financeMain.getDeductFeeDisb())
				.subtract(financeMain.getDownPayment()).subtract(dbdAmount));
		advPayment.setPaymentType(DisbursementConstants.PAYMENT_TYPE_IST);
		advPayment.setPartnerBankID(Long.valueOf(SysParamUtil.getValueAsInt("DISB_PARTNERBANK")));// FIXME
																									// SIVA
																									// 28AUG19
																									// :
																									// PartnerBank
																									// ID
																									// To
																									// be
																									// decide
																									// for
																									// IFT
																									// Transaction
		advPayment.setLLDate(financeMain.getFinStartDate());
		// fetch partner bank details
		PartnerBank partnerBank = partnerBankService.getApprovedPartnerBankById(advPayment.getPartnerBankID());
		if (partnerBank != null) {
			advPayment.setPartnerBankAc(partnerBank.getAccountNo());
			advPayment.setPartnerBankAcType(partnerBank.getAcType());
		}

		// workflow related
		advPayment.setWorkflowId(financeMain.getWorkflowId());
		advPayment.setRoleCode(financeMain.getRoleCode());
		advPayment.setNextRoleCode(financeMain.getNextRoleCode());
		advPayment.setTaskId(financeMain.getTaskId());
		advPayment.setNextTaskId(financeMain.getNextTaskId());

		BankBranch bankBranch = new BankBranch();
		/*
		 * if (true) { //if (StringUtils.isNotBlank(advPayment.getiFSC())) { //FIXME SIVA 28AUG19 : IFSC Code To be
		 * decide for IFT Transaction bankBranch = bankBranchService.getBankBrachByIFSC("SBIN0000003"); } else if
		 * (StringUtils.isNotBlank(advPayment.getBranchBankCode()) &&
		 * StringUtils.isNotBlank(advPayment.getBranchCode())) { bankBranch =
		 * bankBranchService.getBankBrachByCode(advPayment.getBranchBankCode(), advPayment.getBranchCode()); }
		 * 
		 * if (bankBranch != null) { advPayment.setiFSC(bankBranch.getIFSC());
		 * advPayment.setBranchBankCode(bankBranch.getBankCode()); advPayment.setBranchCode(bankBranch.getBranchCode());
		 * advPayment.setBankBranchID(bankBranch.getBankBranchID()); }
		 */
		return advPayment;
	}

	/**
	 * Method for process disbursement instructions and set default values
	 * 
	 * @param financeDetail
	 * @param moveLoanStage
	 */
	private void doProcessDisbInstructions(FinanceDetail financeDetail, boolean moveLoanStage) {
		FinScheduleData finScheduleData = financeDetail.getFinScheduleData();
		FinanceMain financeMain = finScheduleData.getFinanceMain();
		LoggedInUser userDetails = financeDetail.getUserDetails();

		List<FinAdvancePayments> advancePayments = financeDetail.getAdvancePaymentsList();
		if (advancePayments != null) {
			int paymentSeq = 1;
			for (FinAdvancePayments advPayment : advancePayments) {
				advPayment.setFinReference(financeMain.getFinReference());
				advPayment.setRecordType(PennantConstants.RECORD_TYPE_NEW);
				if (!moveLoanStage) {
					advPayment.setNewRecord(true);
				}
				advPayment.setVersion(1);
				advPayment.setLastMntBy(userDetails.getUserId());
				advPayment.setLastMntOn(new Timestamp(System.currentTimeMillis()));
				if (financeDetail.isStp()) {
					advPayment.setRecordStatus(PennantConstants.RCD_STATUS_APPROVED);
				}
				if (moveLoanStage) {
					advPayment.setRecordStatus(financeMain.getRecordStatus());
				}

				advPayment.setUserDetails(financeMain.getUserDetails());
				advPayment.setPaymentSeq(paymentSeq);
				advPayment.setDisbCCy(financeMain.getFinCcy());
				paymentSeq++;

				// workflow related
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
		if (financeMain != null) {
			financeMain.setNextRepayDate(DateUtility.getTimestamp(financeMain.getFinStartDate()));
			financeMain.setNextRepayPftDate(DateUtility.getTimestamp(financeMain.getNextRepayPftDate()));
			financeMain.setNextRepayCpzDate(DateUtility.getTimestamp(financeMain.getNextRepayCpzDate()));
			financeMain.setNextRepayRvwDate(DateUtility.getTimestamp(financeMain.getNextRepayRvwDate()));
		}
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

		logger.debug(Literal.LEAVING);

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
			logger.error(Literal.EXCEPTION, e);
			APIErrorHandlerService.logUnhandledException(e);
			financeDetail = new FinanceDetail();
			financeDetail.setReturnStatus(APIErrorHandlerService.getFailedStatus("API006", "Test"));
			return financeDetail;
		}

		logger.debug(Literal.LEAVING);
		return financeDetail;
	}

	public WSReturnStatus doApproveLoan(FinanceDetail financeDetail) {
		logger.debug(Literal.ENTERING);

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
			doSetRequiredDetails(financeDetail, false, userDetails, stp, true, false);
			// Temporary FIXME
			List<DocumentDetails> documentList = documentDetailsDAO.getDocumentDetailsByRef(
					financeMain.getFinReference(), FinanceConstants.MODULE_NAME, FinanceConstants.FINSER_EVENT_ORG,
					"_View");
			financeDetail.setDocumentDetailsList(documentList);
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
			// auditHeader.setApiHeader(reqHeaderDetails);
			WSReturnStatus returnStatus = prepareAgrrementDetails(auditHeader);
			if (returnStatus != null && StringUtils.isNotBlank(returnStatus.getReturnCode())) {
				String[] valueParm = new String[1];
				valueParm[0] = "Loan Aggrement template ";
				return APIErrorHandlerService.getFailedStatus("API004", valueParm);
			}
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
			logger.error(Literal.EXCEPTION, e);
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
								if (SysParamUtil.getAppDate().compareTo(financeScheduleDetail.getSchDate()) == -1) {
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
				if (finODPenaltyRate != null && (StringUtils.equals(finODPenaltyRate.getODChargeType(),
						FinanceConstants.PENALTYTYPE_PERC_ONETIME)
						|| StringUtils.equals(finODPenaltyRate.getODChargeType(),
								FinanceConstants.PENALTYTYPE_PERC_ON_DUEDAYS)
						|| StringUtils.equals(finODPenaltyRate.getODChargeType(),
								FinanceConstants.PENALTYTYPE_PERC_ON_PD_MTH))) {
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
			logger.error(Literal.EXCEPTION, e);
			APIErrorHandlerService.logUnhandledException(e);
			financeDetail = new FinanceDetail();
			doEmptyResponseObject(financeDetail);
			financeDetail.setReturnStatus(APIErrorHandlerService.getFailedStatus());
			return financeDetail;
		}

		logger.debug(Literal.LEAVING);
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
	public FinanceInquiry getFinanceDetailsById(String reference, String serviceType, boolean isPending) {
		logger.debug(Literal.ENTERING);
		try {
			FinanceInquiry financeInquiry = new FinanceInquiry();
			List<FinanceMain> financeMainList = null;
			String[] valueParm = new String[1];

			if (StringUtils.equalsIgnoreCase(APIConstants.FINANCE_INQUIRY_CUSTOMER, serviceType)) {
				Customer customer = customerDetailsService.getCustomerByCIF(reference);
				String type = "";
				if (isPending) {
					type = "_Temp";
				}
				financeMainList = financeMainService.getFinanceByCustId(customer.getCustID(), type);
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
				if (isPending
						&& StringUtils.equals(financeMain.getRecordStatus(), PennantConstants.RCD_STATUS_APPROVED)) {
					continue;
				}
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
						if (SysParamUtil.getAppDate().compareTo(financeScheduleDetail.getSchDate()) == -1) {
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
			logger.debug(Literal.LEAVING);
			return financeInquiry;
		} catch (Exception e) {
			logger.error(Literal.EXCEPTION, e);
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
			// save or update coApplicants details
			if (financeDetail.getJountAccountDetailList() != null
					&& !financeDetail.getJountAccountDetailList().isEmpty()) {
				updatedCoApplicants(financeDetail);
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
			detail.setDocRefId(Long.MIN_VALUE);
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
			// workflow relates
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
		doProcessMandate(financeDetail, false);
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

	/**
	 * Method for Save or update Finance coApplicants.
	 * 
	 * @param financeDetail
	 */
	private void updatedCoApplicants(FinanceDetail financeDetail) {
		logger.debug(Literal.ENTERING);
		FinanceMain financeMain = financeDetail.getFinScheduleData().getFinanceMain();
		AuditHeader auditHeader = null;
		for (JointAccountDetail detail : financeDetail.getJountAccountDetailList()) {
			detail.setNewRecord(true);
			detail.setRecordType(PennantConstants.RECORD_TYPE_NEW);
			detail.setVersion(1);
			detail.setFinReference(financeMain.getFinReference());

			// set update properties if exists
			String finReference = financeMain.getFinReference();
			String type = TableType.TEMP_TAB.getSuffix();
			JointAccountDetail extDetail = jointAccountDetailService.getJountAccountDetailByRef(finReference,
					detail.getCustCIF(), type);
			if (extDetail != null) {
				detail.setJointAccountId(extDetail.getJointAccountId());
				detail.setNewRecord(false);
				detail.setRecordType(PennantConstants.RECORD_TYPE_UPD);
				detail.setVersion(extDetail.getVersion() + 1);
			}

			detail.setUserDetails(financeMain.getUserDetails());
			detail.setRecordStatus(financeMain.getRecordStatus());
			detail.setLastMntOn(new Timestamp(System.currentTimeMillis()));
			detail.setLastMntBy(financeMain.getLastMntBy());
			// workflow relates
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
			jointAccountDetailService.saveOrUpdate(auditHeader);
		}

		logger.debug(Literal.LEAVING);
	}

	private WSReturnStatus updateDisbursementInst(FinanceDetail financeDetail, String type) {
		WSReturnStatus returnStatus = new WSReturnStatus();
		// process disbursement instructions
		doProcessDisbInstructions(financeDetail, false);

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
				PennantConstants.TRAN_WF, financeDetail.isDisbStp()));

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

	private AuditHeader getAuditHeader(JointAccountDetail jointAccountDetail, String tranType) {
		AuditDetail auditDetail = new AuditDetail(tranType, 1, jointAccountDetail.getBefImage(), jointAccountDetail);
		return new AuditHeader(String.valueOf(jointAccountDetail.getJointAccountId()), null, null, null, auditDetail,
				jointAccountDetail.getUserDetails(), new HashMap<String, ArrayList<ErrorDetail>>());
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
		// ReqStage and Status
		financeDetail.getFinScheduleData().getFinanceMain()
				.setStatus(financeDetail.getFinScheduleData().getFinanceMain().getRecordStatus());
		financeDetail.getFinScheduleData().getFinanceMain()
				.setStage(financeDetail.getFinScheduleData().getFinanceMain().getNextRoleCode());
		// disbursement Dates
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
		logger.debug(Literal.ENTERING);

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
		logger.debug(Literal.LEAVING);
	}

	/**
	 * Method for set the default data to the ChequeHeader.
	 * 
	 * @param financeDetail
	 * @param moveLoanStage
	 */
	private void doSetDefaultChequeHeader(FinanceDetail financeDetail, boolean moveLoanStage) {
		logger.debug(Literal.ENTERING);
		if (financeDetail.getChequeHeader() == null && !moveLoanStage) {
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
							// Throw validation error and break the loop
						}
					}

				}
				feeDetailMap.put(feeDetail.getFeeTypeCode(), feeDetail);
			}
			Map<Long, FinReceiptHeader> receiptHeaderMap = new HashMap<>();// map
																			// to
																			// save
																			// receipt
																			// header
			List<FinReceiptHeader> receiptHeaderList = finReceiptHeaderDAO
					.getUpFrontReceiptHeaderByExtRef(detail.getExternalReference(), "");
			for (FinReceiptHeader header : receiptHeaderList) {
				receiptHeaderMap.put(header.getReceiptID(), header);
			}
			for (FinFeeDetail feeDtl : detail.getFinFeeDetailList()) {// iterating
																			// existing
																		// finfee
																		// details
																		// list
																		// from
																		// finscheduledata
				if (feeDetailMap.containsKey(feeDtl.getFeeTypeCode())) {
					FinFeeDetail finFeeDetail = feeDetailMap.get(feeDtl.getFeeTypeCode());
					if (finFeeDetail.getPaidAmount().compareTo(BigDecimal.ZERO) > 0) {
						if (finFeeDetail.getFinFeeReceipts() != null && finFeeDetail.getFinFeeReceipts().size() > 0) {
							for (FinFeeReceipt feeReceipt : finFeeDetail.getFinFeeReceipts()) {// iterating
																									// receipt
																								// details
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
			for (FinFeeDetail feeDtl : detail.getFinFeeDetailList()) {// iterating
																			// existing
																		// finfee
																		// details
																		// list
																		// from
																		// finscheduledata
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

	private String validateTemplate(FinanceReferenceDetail frefdata) throws Exception {
		String templatePath = PathUtil.getPath(PathUtil.FINANCE_AGREEMENTS);
		String templateName = "";
		String msg = "Y";
		logger.debug("Template Path:" + templatePath);
		if (StringUtils.trimToEmpty(frefdata.getLovDescAggReportName()).contains("/")) {
			String aggRptName = StringUtils.trimToEmpty(frefdata.getLovDescAggReportName());
			templateName = aggRptName.substring(aggRptName.lastIndexOf("/") + 1, aggRptName.length());
		} else {
			templateName = frefdata.getLovDescAggReportName();

		}
		File templateDirectory = new File(templatePath, templateName);
		if (!templateDirectory.exists()) {
			msg = templateName;
		}
		return msg;
	}

	public List<ErrorDetail> rejectFinanceValidations(final FinanceDetail financeDetail) {
		logger.debug(Literal.ENTERING);
		List<ErrorDetail> errorsList = new ArrayList<>();

		if (StringUtils.isBlank(financeDetail.getFinScheduleData().getFinanceMain().getCustCIF())) {
			String[] param = new String[1];
			param[0] = "cif";

			errorsList.add(ErrorUtil.getErrorDetail(new ErrorDetail("30561", param)));

			return errorsList;
		}

		Customer customer = customerDetailsService
				.getCustomerByCIF(financeDetail.getFinScheduleData().getFinanceMain().getCustCIF());
		if (null == customer) {
			String[] valueParm = new String[1];
			valueParm[0] = "cif: " + financeDetail.getFinScheduleData().getFinanceMain().getCustCIF();

			errorsList.add(ErrorUtil.getErrorDetail(new ErrorDetail("90266", valueParm)));

			return errorsList;
		}

		if (StringUtils.isBlank(financeDetail.getFinScheduleData().getFinanceMain().getFinType())) {
			String[] param = new String[1];
			param[0] = "finType";

			errorsList.add(ErrorUtil.getErrorDetail(new ErrorDetail("30561", param)));

			return errorsList;
		}

		FinanceType financeType = financeTypeService
				.getFinanceTypeByFinType(financeDetail.getFinScheduleData().getFinanceMain().getFinType());
		if (null == financeType) {
			String[] valueParm = new String[1];
			valueParm[0] = "finType: " + financeDetail.getFinScheduleData().getFinanceMain().getFinType();

			errorsList.add(ErrorUtil.getErrorDetail(new ErrorDetail("90266", valueParm)));

			return errorsList;
		}

		if (null == financeDetail.getFinScheduleData().getFinanceMain().getFinAmount()) {
			financeDetail.getFinScheduleData().getFinanceMain().setFinAmount(BigDecimal.ZERO);
		}

		if (null == financeDetail.getFinScheduleData().getFinanceMain().getFinAssetValue()) {
			financeDetail.getFinScheduleData().getFinanceMain().setFinAssetValue(BigDecimal.ZERO);
		}

		if (NumberUtils.compare(financeDetail.getFinScheduleData().getFinanceMain().getNumberOfTerms(), 0) == 0) {
			String[] param = new String[1];
			param[0] = "numberOfTerms";

			errorsList.add(ErrorUtil.getErrorDetail(new ErrorDetail("30561", param)));

			return errorsList;
		}

		logger.debug(Literal.LEAVING);
		return errorsList;
	}

	private WSReturnStatus prepareAndExecuteAuditHeader(FinanceDetail aFinanceDetail, String tranType) {
		logger.debug(Literal.ENTERING);

		WSReturnStatus returnStatus = null;

		AuditDetail auditDetail = new AuditDetail(tranType, 1, aFinanceDetail.getBefImage(), aFinanceDetail);
		AuditHeader auditHeader = new AuditHeader(aFinanceDetail.getFinScheduleData().getFinReference(), null, null,
				null, auditDetail, aFinanceDetail.getUserDetails(), new HashMap<String, ArrayList<ErrorDetail>>());
		APIHeader reqHeaderDetails = (APIHeader) PhaseInterceptorChain.getCurrentMessage().getExchange()
				.get(APIHeader.API_HEADER_KEY);
		auditHeader.setApiHeader(reqHeaderDetails);

		AuditHeader rejectAuditHeader = financeDetailService.doReject(auditHeader, false, true);
		if (rejectAuditHeader.getAuditError() != null) {
			for (ErrorDetail errorDetail : rejectAuditHeader.getErrorMessage()) {
				returnStatus = APIErrorHandlerService.getFailedStatus(errorDetail.getCode(), errorDetail.getError());
			}
		} else {
			returnStatus = APIErrorHandlerService.getSuccessStatus();
		}

		logger.debug(Literal.LEAVING);
		return returnStatus;
	}

	public WSReturnStatus processRejectFinance(FinanceDetail financeDetail, boolean finReferenceAvailable) {
		logger.debug(Literal.ENTERING);

		WSReturnStatus returnStatus = null;

		FinanceMain financeMain = financeDetail.getFinScheduleData().getFinanceMain();
		// mandatory fields
		LoggedInUser userDetails = SessionUserDetails.getUserDetails(SessionUserDetails.getLogiedInUser());
		financeMain.setUserDetails(userDetails);
		financeMain.setLastMntBy(userDetails.getUserId());
		financeMain.setFinSourceID(APIConstants.FINSOURCE_ID_API);
		financeMain.setVersion(1);
		financeMain.setRecordType(PennantConstants.RECORD_TYPE_NEW);
		financeMain.setRecordStatus(PennantConstants.RCD_STATUS_REJECTED);
		financeDetail.setModuleDefiner(FinanceConstants.FINSER_EVENT_ORG);

		// customer details
		Customer customer = customerDetailsService.getCustomerByCIF(financeMain.getCustCIF());
		CustomerDetails customerDetails = new CustomerDetails();
		customerDetails.setCustomer(customer);
		financeDetail.setCustomerDetails(customerDetails);
		String tranType = PennantConstants.TRAN_WF;

		if (!finReferenceAvailable) {
			if (StringUtils.isBlank(financeMain.getFinReference())) {
				financeMain.setFinReference(String.valueOf(String.valueOf(ReferenceGenerator.generateFinRef(financeMain,
						financeDetail.getFinScheduleData().getFinanceType()))));
			}
			financeMain.setLastMntOn(new Timestamp(System.currentTimeMillis()));
			financeMain.setCustID(customer.getCustID());
			financeMain.setEqualRepay(financeMain.isEqualRepay());
			financeMain.setRecalType(financeMain.getRecalType());
			financeMain.setLastRepayDate(financeMain.getFinStartDate());
			financeMain.setLastRepayPftDate(financeMain.getFinStartDate());
			financeMain.setLastRepayRvwDate(financeMain.getFinStartDate());
			financeMain.setLastRepayCpzDate(financeMain.getFinStartDate());
			financeDetail.getFinScheduleData().setFinanceMain(financeMain);

			returnStatus = prepareAndExecuteAuditHeader(financeDetail, tranType);
		} else {
			FinanceMain dbFinanceMain = financeDetailService
					.getFinanceMain(financeDetail.getFinScheduleData().getFinReference(), "_Temp");
			if (null != dbFinanceMain
					&& !StringUtils.equals(dbFinanceMain.getRecordStatus(), PennantConstants.RCD_STATUS_REJECTED)) {
				financeMain.setFinReference(dbFinanceMain.getFinReference());
				financeMain.setLastMntOn(dbFinanceMain.getLastMntOn());
				financeMain.setCustID(dbFinanceMain.getCustID());
				financeMain.setEqualRepay(dbFinanceMain.isEqualRepay());
				financeMain.setRecalType(dbFinanceMain.getRecalType());
				financeMain.setLastRepayDate(dbFinanceMain.getLastRepayDate());
				financeMain.setLastRepayPftDate(dbFinanceMain.getLastRepayPftDate());
				financeMain.setLastRepayRvwDate(dbFinanceMain.getLastRepayRvwDate());
				financeMain.setLastRepayCpzDate(dbFinanceMain.getLastRepayCpzDate());

				// override received fields with fetch data
				financeMain.setLovDescCustCIF(dbFinanceMain.getCustCIF());
				financeMain.setFinType(dbFinanceMain.getFinType());
				financeMain.setFinAmount(dbFinanceMain.getFinAmount());
				financeMain.setFinAssetValue(dbFinanceMain.getFinAssetValue());
				financeMain.setNumberOfTerms(dbFinanceMain.getNumberOfTerms());

				financeDetail.getFinScheduleData().setFinanceMain(financeMain);
				financeDetail.setUserDetails(userDetails);
				financeDetail.setCustomerDetails(customerDetails);
				FinanceType dbFinanceType = financeTypeService.getFinanceTypeById(dbFinanceMain.getFinType());
				financeDetail.getFinScheduleData().setFinanceType(dbFinanceType);

				CustomerDetails dbCustomer = customerDetailsService.getApprovedCustomerById(financeMain.getCustID());
				financeDetail.setCustomerDetails(dbCustomer);
				returnStatus = prepareAndExecuteAuditHeader(financeDetail, tranType);
			} else {
				// throw validation error
				String[] valueParam = new String[1];
				valueParam[0] = "finreference: " + financeDetail.getFinScheduleData().getFinReference();

				returnStatus = APIErrorHandlerService.getFailedStatus("90266", valueParam);
			}
		}
		logger.debug(Literal.LEAVING);

		return returnStatus;
	}

	public FinanceDetail processCancelFinance(FinanceDetail financeDetail) {
		logger.debug(Literal.ENTERING);

		FinanceDetail response = null;
		FinanceDetail findetail = financeDetailService.getFinanceDetailById(financeDetail.getFinReference(), false, "",
				false, FinanceConstants.FINSER_EVENT_ORG, "");
		FinanceMain financeMain = findetail.getFinScheduleData().getFinanceMain();
		LoggedInUser userDetails = SessionUserDetails.getUserDetails(SessionUserDetails.getLogiedInUser());
		findetail.getFinScheduleData().getFinanceMain()
				.setVersion(findetail.getFinScheduleData().getFinanceMain().getVersion() + 1);
		financeMain.setUserDetails(userDetails);
		List<ErrorDetail> errorDetailList = null;
		if (CollectionUtils.isNotEmpty(financeDetail.getExtendedDetails())) {
			String subModule = findetail.getFinScheduleData().getFinanceType().getFinCategory();
			errorDetailList = extendedFieldDetailsService.validateExtendedFieldDetails(
					financeDetail.getExtendedDetails(), ExtendedFieldConstants.MODULE_LOAN, subModule,
					FinanceConstants.FINSER_EVENT_CANCELFIN);

			for (ErrorDetail errorDetail : errorDetailList) {
				response = new FinanceDetail();
				doEmptyResponseObject(response);
				response.setReturnStatus(
						APIErrorHandlerService.getFailedStatus(errorDetail.getCode(), errorDetail.getError()));
				logger.debug(Literal.LEAVING);
				return response;
			}
		}
		List<FinanceScheduleDetail> schdList = findetail.getFinScheduleData().getFinanceScheduleDetails();
		FinanceScheduleDetail bpiSchedule = null;
		for (int i = 1; i < schdList.size(); i++) {
			FinanceScheduleDetail curSchd = schdList.get(i);
			if (StringUtils.equals(curSchd.getBpiOrHoliday(), FinanceConstants.FLAG_BPI)) {
				bpiSchedule = curSchd;
				continue;
			}

			if (curSchd.getSchDate().compareTo(SysParamUtil.getAppDate()) <= 0) {

				ErrorDetail errorDetails = ErrorUtil.getErrorDetail(
						new ErrorDetail(PennantConstants.KEY_FIELD, "60407", null, null), userDetails.getLanguage());

				response = new FinanceDetail();
				doEmptyResponseObject(response);
				response.setReturnStatus(APIErrorHandlerService.getFailedStatus("60407", errorDetails.getError()));
				logger.debug(Literal.LEAVING);
				return response;
			}
		}
		// process Extended field details
		// Get the ExtendedFieldHeader for given module and subModule
		ExtendedFieldHeader extendedFieldHeader = extendedFieldHeaderDAO.getExtendedFieldHeaderByModuleName(
				ExtendedFieldConstants.MODULE_LOAN, financeMain.getFinCategory(),
				FinanceConstants.FINSER_EVENT_CANCELFIN, "");
		// ### 02-05-2018-END
		findetail.setExtendedFieldHeader(extendedFieldHeader);
		List<ExtendedField> extendedFields = financeDetail.getExtendedDetails();
		if (extendedFieldHeader != null) {
			int seqNo = 0;
			ExtendedFieldRender exdFieldRender = new ExtendedFieldRender();
			exdFieldRender.setReference(financeMain.getFinReference());
			exdFieldRender.setLastMntOn(new Timestamp(System.currentTimeMillis()));
			exdFieldRender.setRecordStatus(PennantConstants.RCD_STATUS_APPROVED);
			exdFieldRender.setLastMntBy(financeMain.getUserDetails().getUserId());
			exdFieldRender.setSeqNo(++seqNo);
			exdFieldRender.setNewRecord(true);
			exdFieldRender.setRecordType(PennantConstants.RECORD_TYPE_NEW);
			exdFieldRender.setVersion(1);
			exdFieldRender.setTypeCode(findetail.getExtendedFieldHeader().getSubModuleName());

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
			findetail.setExtendedFieldRender(exdFieldRender);
		}

		ReasonHeader reasonHeader = financeDetail.getReasonHeader();
		if (reasonHeader != null) {
			if (!CollectionUtils.isEmpty(reasonHeader.getDetailsList())) {
				for (ReasonDetails reasonDetails : reasonHeader.getDetailsList()) {
					if (StringUtils.isBlank(reasonDetails.getReasonCode())) {
						String[] valueParm = new String[1];
						valueParm[0] = "reasonCode";
						ErrorDetail errorDetails = ErrorUtil.getErrorDetail(
								new ErrorDetail(PennantConstants.KEY_FIELD, "30561", valueParm, null),
								userDetails.getLanguage());

						response = new FinanceDetail();
						doEmptyResponseObject(response);
						response.setReturnStatus(
								APIErrorHandlerService.getFailedStatus("60407", errorDetails.getError()));
						logger.debug(Literal.LEAVING);
						return response;
					}
					ReasonCode details = reasonDetailDAO.getCancelReasonByCode(reasonDetails.getReasonCode(), "_AView");
					if (details != null) {
						reasonDetails.setReasonId(details.getReasonCategoryID());
					} else {
						String[] valueParm = new String[2];
						valueParm[0] = " reasonCode";
						valueParm[1] = reasonDetails.getReasonCode();
						ErrorDetail errorDetails = ErrorUtil.getErrorDetail(
								new ErrorDetail(PennantConstants.KEY_FIELD, "90224", valueParm, null),
								userDetails.getLanguage());
						response = new FinanceDetail();
						doEmptyResponseObject(response);
						response.setReturnStatus(
								APIErrorHandlerService.getFailedStatus("60407", errorDetails.getError()));
						logger.debug(Literal.LEAVING);
						return response;
					}
				}
				financeMain.setDetailsList(reasonHeader.getDetailsList());
				financeMain.setCancelRemarks(reasonHeader.getRemarks());
			}
		}
		AuditDetail auditDetail = new AuditDetail(PennantConstants.TRAN_WF, 1, null, findetail);
		AuditHeader auditHeader = new AuditHeader(findetail.getFinReference(), null, null, null, auditDetail,
				findetail.getFinScheduleData().getFinanceMain().getUserDetails(),
				new HashMap<String, ArrayList<ErrorDetail>>());

		APIHeader reqHeaderDetails = (APIHeader) PhaseInterceptorChain.getCurrentMessage().getExchange()
				.get(APIHeader.API_HEADER_KEY);
		auditHeader.setApiHeader(reqHeaderDetails);

		auditHeader = financeCancellationService.doApprove(auditHeader, true);
		if (auditHeader.getOverideMessage() != null && auditHeader.getOverideMessage().size() > 0) {
			for (ErrorDetail errorDetail : auditHeader.getOverideMessage()) {
				response = new FinanceDetail();
				doEmptyResponseObject(response);
				response.setReturnStatus(
						APIErrorHandlerService.getFailedStatus(errorDetail.getCode(), errorDetail.getError()));
				logger.debug(Literal.LEAVING);
				return response;
			}
		}
		if (auditHeader.getErrorMessage() != null) {
			for (ErrorDetail errorDetail : auditHeader.getErrorMessage()) {
				response = new FinanceDetail();
				doEmptyResponseObject(response);
				response.setReturnStatus(
						APIErrorHandlerService.getFailedStatus(errorDetail.getCode(), errorDetail.getError()));
				logger.debug(Literal.LEAVING);
				return response;
			}
		}

		if (auditHeader.getAuditDetail().getErrorDetails() != null) {
			for (ErrorDetail errorDetail : auditHeader.getAuditDetail().getErrorDetails()) {
				response = new FinanceDetail();
				doEmptyResponseObject(response);
				response.setReturnStatus(
						APIErrorHandlerService.getFailedStatus(errorDetail.getCode(), errorDetail.getError()));
				logger.debug(Literal.LEAVING);
				return response;
			}
		}
		logger.debug(Literal.LEAVING);
		response = new FinanceDetail();
		doEmptyResponseObject(response);
		FinScheduleData finScheduleData = new FinScheduleData();
		finScheduleData.setRepayInstructions(null);
		finScheduleData.setRateInstruction(null);
		finScheduleData.setFeeDues(null);
		finScheduleData.setFinFeeDetailList(null);
		finScheduleData.setInsuranceList(null);
		finScheduleData.setStepPolicyDetails(null);
		finScheduleData.setFinanceScheduleDetails(null);
		finScheduleData.setApiPlanEMIHDates(null);
		finScheduleData.setApiplanEMIHmonths(null);
		finScheduleData.setVasRecordingList(null);
		finScheduleData.setFinODDetails(null);
		finScheduleData.setFinODPenaltyRate(null);
		response.setFinScheduleData(finScheduleData);
		response.getFinScheduleData().setOldFinReference(financeDetail.getFinScheduleData().getOldFinReference());
		response.setReturnStatus(APIErrorHandlerService.getSuccessStatus());
		return response;
	}

	public FinanceDetail doReInitiateFinance(FinanceDetail financeDetail) {
		logger.debug(Literal.ENTERING);

		try {
			FinanceDetail finDetail = getFinanceDetails(financeDetail.getFinReference());

			String finReference = String.valueOf(String.valueOf(ReferenceGenerator.generateFinRef(
					finDetail.getFinScheduleData().getFinanceMain(), finDetail.getFinScheduleData().getFinanceType())));
			FinanceMain financeMain = finDetail.getFinScheduleData().getFinanceMain();
			finDetail.getFinScheduleData().getFinanceMain().setFinReference(finReference);
			finDetail.getFinScheduleData().getFinanceMain()
					.setOldFinReference(financeDetail.getFinScheduleData().getOldFinReference());
			financeMain.setExtReference(financeDetail.getFinScheduleData().getExternalReference());
			finDetail.getFinScheduleData().getFinanceMain().setFinIsActive(true);
			finDetail.getFinScheduleData().getFinanceMain().setClosingStatus("");
			LoggedInUser userDetails = SessionUserDetails.getUserDetails(SessionUserDetails.getLogiedInUser());
			finDetail.setUserDetails(userDetails);
			financeMain.setLastMntBy(userDetails.getUserId());
			financeMain.setFinSourceID(APIConstants.FINSOURCE_ID_API);
			financeMain.setVersion(1);
			financeMain.setRecordType(PennantConstants.RECORD_TYPE_NEW);
			financeMain.setRecordStatus(PennantConstants.RCD_STATUS_APPROVED);
			financeDetail.setModuleDefiner(FinanceConstants.FINSER_EVENT_ORG);
			finDetail.setModuleDefiner(FinanceConstants.FINSER_EVENT_ORG);
			finDetail.getFinScheduleData().getFinanceMain().setRecordType(PennantConstants.RECORD_TYPE_NEW);
			finDetail.getFinScheduleData().setFinReference(finReference);
			for (FinanceScheduleDetail schdDetail : finDetail.getFinScheduleData().getFinanceScheduleDetails()) {
				schdDetail.setFinReference(finReference);
			}
			for (FinFeeDetail feeDetail : finDetail.getFinScheduleData().getFinFeeDetailList()) {
				feeDetail.setFinReference(finReference);
				feeDetail.setFeeID(Long.MIN_VALUE);
				feeDetail.setRecordType(PennantConstants.RECORD_TYPE_NEW);
				feeDetail.setNewRecord(true);
				if (feeDetail.getTaxHeader() != null) {
					feeDetail.getTaxHeader().setHeaderId(Long.MIN_VALUE);
					for (Taxes tax : feeDetail.getTaxHeader().getTaxDetails()) {
						tax.setId(Long.MIN_VALUE);
					}
				}
				feeDetail.getFinTaxDetails().setFinTaxID(Long.MIN_VALUE);
			}

			for (FinAdvancePayments finAdvancePayments : finDetail.getAdvancePaymentsList()) {
				finAdvancePayments.setFinReference(finReference);
				finAdvancePayments.setpOIssued(false);
				finAdvancePayments.setRecordType(PennantConstants.RECORD_TYPE_NEW);
				finAdvancePayments.setRecordStatus(PennantConstants.RCD_STATUS_APPROVED);
				finAdvancePayments.setNewRecord(true);
				finAdvancePayments.setLastMntOn(new Timestamp(System.currentTimeMillis()));
				finAdvancePayments.setId(Long.MIN_VALUE);

			}

			//setting the values for the co_applicant
			List<JointAccountDetail> jountAccountDetailList = finDetail.getJountAccountDetailList();

			for (JointAccountDetail detail : jountAccountDetailList) {
				detail.setFinReference(finReference);
				detail.setId(Long.MIN_VALUE);
				detail.setRecordType(PennantConstants.RECORD_TYPE_NEW);
				detail.setNewRecord(true);
			}

			// process Extended field details
			// Get the ExtendedFieldHeader for given module and subModule
			ExtendedFieldHeader extendedFieldHeader = extendedFieldHeaderDAO.getExtendedFieldHeaderByModuleName(
					ExtendedFieldConstants.MODULE_LOAN, financeMain.getFinCategory(), FinanceConstants.FINSER_EVENT_ORG,
					"");
			// ### 02-05-2018-END
			finDetail.setExtendedFieldHeader(extendedFieldHeader);
			List<ExtendedField> extendedFields = finDetail.getExtendedDetails();
			if (extendedFieldHeader != null) {
				int seqNo = 0;
				ExtendedFieldRender exdFieldRender = new ExtendedFieldRender();
				exdFieldRender.setReference(financeMain.getFinReference());
				exdFieldRender.setLastMntOn(new Timestamp(System.currentTimeMillis()));
				exdFieldRender.setRecordStatus(PennantConstants.RCD_STATUS_APPROVED);
				exdFieldRender.setLastMntBy(finDetail.getUserDetails().getUserId());
				exdFieldRender.setSeqNo(++seqNo);
				exdFieldRender.setNewRecord(true);
				exdFieldRender.setRecordType(PennantConstants.RECORD_TYPE_NEW);
				exdFieldRender.setVersion(1);
				exdFieldRender.setTypeCode(finDetail.getExtendedFieldHeader().getSubModuleName());

				if (extendedFields != null) {
					for (ExtendedField extendedField : extendedFields) {
						Map<String, Object> mapValues = new HashMap<String, Object>();
						for (ExtendedFieldData extFieldData : extendedField.getExtendedFieldDataList()) {
							if (!StringUtils.equalsIgnoreCase("InstructionUID", extFieldData.getFieldName())) {
								mapValues.put(extFieldData.getFieldName(), extFieldData.getFieldValue());
							}
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

				finDetail.setExtendedFieldRender(exdFieldRender);
			}

			AuditDetail auditDetail = new AuditDetail(PennantConstants.TRAN_WF, 1, null, finDetail);
			AuditHeader auditHeader = new AuditHeader(finDetail.getFinReference(), null, null, null, auditDetail,
					finDetail.getFinScheduleData().getFinanceMain().getUserDetails(),
					new HashMap<String, ArrayList<ErrorDetail>>());

			APIHeader reqHeaderDetails = (APIHeader) PhaseInterceptorChain.getCurrentMessage().getExchange()
					.get(APIHeader.API_HEADER_KEY);
			auditHeader.setApiHeader(reqHeaderDetails);

			auditHeader = financeDetailService.doApprove(auditHeader, false);

			if (auditHeader.getOverideMessage() != null && auditHeader.getOverideMessage().size() > 0) {
				for (ErrorDetail errorDetail : auditHeader.getOverideMessage()) {
					finDetail = new FinanceDetail();
					finDetail.setReturnStatus(
							APIErrorHandlerService.getFailedStatus(errorDetail.getCode(), errorDetail.getError()));
					return finDetail;
				}
			}
			if (auditHeader.getErrorMessage() != null) {
				for (ErrorDetail errorDetail : auditHeader.getErrorMessage()) {
					finDetail = new FinanceDetail();
					finDetail.setReturnStatus(
							APIErrorHandlerService.getFailedStatus(errorDetail.getCode(), errorDetail.getError()));
					return finDetail;
				}
			}

			if (auditHeader.getAuditDetail().getErrorDetails() != null) {
				for (ErrorDetail errorDetail : auditHeader.getAuditDetail().getErrorDetails()) {
					finDetail = new FinanceDetail();
					finDetail.setReturnStatus(
							APIErrorHandlerService.getFailedStatus(errorDetail.getCode(), errorDetail.getError()));
					return finDetail;

				}
			}

			if (StringUtils.isNotBlank(finReference)) {
				// prepare response object
				finDetail = getFinanceDetailResponse(auditHeader);
				finDetail.setStp(false);
				finDetail.setReturnStatus(APIErrorHandlerService.getSuccessStatus());

				logger.debug(Literal.LEAVING);
				return finDetail;
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
			logger.error(Literal.EXCEPTION, e);
			APIErrorHandlerService.logUnhandledException(e);
			FinanceDetail response = new FinanceDetail();
			doEmptyResponseObject(response);
			response.setReturnStatus(APIErrorHandlerService.getFailedStatus());
			return response;
		}
		logger.debug(Literal.LEAVING);

		return null;
	}

	public WSReturnStatus doMoveLoanStage(FinanceDetail finDetail, MoveLoanStageRequest moveLoanStageRequest) {
		logger.debug(Literal.ENTERING);
		try {
			FinanceMain financeMain = finDetail.getFinScheduleData().getFinanceMain();
			ChequeHeader chequeHeader = chequeHeaderService.getChequeHeaderByRef(financeMain.getFinReference());
			finDetail.setChequeHeader(chequeHeader);

			LoggedInUser userDetails = SessionUserDetails.getUserDetails(SessionUserDetails.getLogiedInUser());
			String finEvent = FinanceConstants.FINSER_EVENT_ORG;
			FinanceWorkFlow financeWorkFlow = financeWorkFlowService.getApprovedFinanceWorkFlowById(
					financeMain.getFinType(), finEvent, PennantConstants.WORFLOW_MODULE_FINANCE);
			WorkFlowDetails workFlowDetails = null;
			if (financeWorkFlow != null) {
				workFlowDetails = WorkFlowUtil.getDetailsByType(financeWorkFlow.getWorkFlowType());
				workFlow = new WorkflowEngine(workFlowDetails.getWorkFlowXml());
			}

			doSetRequiredDetails(finDetail, false, userDetails, false, false, true);
			if (finDetail.getExtendedFieldRender() != null) {
				if (finDetail.getExtendedFieldRender().getMapValues() != null) {
					finDetail.getExtendedFieldRender().getMapValues().remove("instructionuid");
				}
			}
			finDetail.setNewRecord(false);
			if (CollectionUtils.isNotEmpty(moveLoanStageRequest.getRemarks())) {
				for (Notes notes : moveLoanStageRequest.getRemarks()) {
					notes.setReference(moveLoanStageRequest.getFinReference());
				}

				WSReturnStatus returnStatus = remarksWebServiceImpl.validateRemarks(moveLoanStageRequest.getRemarks());
				if (returnStatus != null) {
					return returnStatus;
				} else {
					remarksController.doAddRemarks(moveLoanStageRequest.getRemarks());
				}
			}
			financeMain.setServiceName("MoveLoanStage");

			AuditDetail auditDetail = new AuditDetail(PennantConstants.TRAN_WF, 1, null, finDetail);
			AuditHeader auditHeader = new AuditHeader(finDetail.getFinReference(), null, null, null, auditDetail,
					financeMain.getUserDetails(), new HashMap<String, ArrayList<ErrorDetail>>());

			APIHeader reqHeaderDetails = (APIHeader) PhaseInterceptorChain.getCurrentMessage().getExchange()
					.get(APIHeader.API_HEADER_KEY);
			auditHeader.setApiHeader(reqHeaderDetails);
			auditHeader = financeDetailService.executeWorkflowServiceTasks(auditHeader,
					moveLoanStageRequest.getCurrentStage(), moveLoanStageRequest.getAction(), workFlow);
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
			logger.error(Literal.EXCEPTION, e);
			return APIErrorHandlerService.getFailedStatus();
		}
		return APIErrorHandlerService.getSuccessStatus();

	}

	public void setDisbursements(FinanceDetail finDetail, boolean loanWithWIF, boolean approve, boolean moveLoanStage) {
		FinScheduleData finScheduleData = finDetail.getFinScheduleData();
		FinanceMain finMain = finScheduleData.getFinanceMain();

		doProcessDisbInstructions(finDetail, moveLoanStage);

		// validate disbursement instructions
		if (!loanWithWIF && !finDetail.getFinScheduleData().getFinanceMain().getProductCategory()
				.equals(FinanceConstants.PRODUCT_ODFACILITY)) {
			if (!approve && !moveLoanStage && finDetail.isStp()) {
				FinanceDisbursement disbursementDetails = new FinanceDisbursement();
				disbursementDetails.setDisbDate(finMain.getFinStartDate());
				disbursementDetails.setDisbAmount(finMain.getFinAmount());
				disbursementDetails.setVersion(1);
				disbursementDetails.setDisbSeq(1);
				disbursementDetails.setDisbReqDate(SysParamUtil.getAppDate());
				disbursementDetails.setFeeChargeAmt(finMain.getFeeChargeAmt());
				disbursementDetails.setInsuranceAmt(finMain.getInsuranceAmt());
				disbursementDetails
						.setDisbAccountId(PennantApplicationUtil.unFormatAccountNumber(finMain.getDisbAccountId()));
				finScheduleData.getDisbursementDetails().add(disbursementDetails);
			}
		}

		// validate Disbursement instruction total amount
		if (finDetail.getAdvancePaymentsList() != null && finDetail.getAdvancePaymentsList().size() > 0) {
			validateDisbInstAmount(finDetail);
		} else if (finDetail.getFinScheduleData().getFinanceMain().getProductCategory()
				.equals(FinanceConstants.PRODUCT_CD) && CollectionUtils.isEmpty(finDetail.getAdvancePaymentsList())) {

			List<FinAdvancePayments> advPayList = finDetail.getAdvancePaymentsList();
			if (advPayList == null) {
				advPayList = new ArrayList<>();
			}

			advPayList.add(createNewDisbInst(finDetail, moveLoanStage));
			finDetail.setAdvancePaymentsList(advPayList);
		}

	}

	public void adjustCDDownpay(FinanceDetail finDetail) {
		FinScheduleData finScheduleData = finDetail.getFinScheduleData();
		FinanceMain finMain = finScheduleData.getFinanceMain();
		BigDecimal svAmount = BigDecimal.ZERO;

		if (!StringUtils.equals(finMain.getProductCategory(), FinanceConstants.PRODUCT_CD)) {
			return;
		}

		/*
		 * if (finMain.getDownPayment().compareTo(BigDecimal.ZERO) == 0) { return; }
		 */

		if (finDetail.getPromotion() != null && finDetail.getPromotion().isOpenBalOnPV()) {
			svAmount = finMain.getSvAmount();
		}
		FinAdvancePayments fap = finDetail.getAdvancePaymentsList().get(0);
		fap.setAmtToBeReleased(fap.getAmtToBeReleased().subtract(finMain.getDownPayment()).subtract(svAmount));
	}

	public List<AgreementData> getAgreements(FinanceDetail financeDetail, AgreementRequest agrReq) {
		List<AgreementData> agreements = new ArrayList<>();

		FinScheduleData finScheduleData = financeDetail.getFinScheduleData();
		FinanceMain fm = finScheduleData.getFinanceMain();
		if (financeDetail == null || finScheduleData == null || fm == null) {
			return agreements;
		}

		Set<String> allagrDataset = new HashSet<>();
		AgreementData details = new AgreementData();

		String agreementType = agrReq.getAgreementType();
		AgreementDefinition agrementDef = agreementDefinitionDAO.getAgreementDefinitionByCode(agreementType, "");
		details.setAgreementName(agrementDef.getAggName());
		String aggtype = agrementDef.getAggtype();
		details.setAgreementType(aggtype);
		allagrDataset.add(agrementDef.getAggImage());

		try {
			User logiedInUser = SessionUserDetails.getLogiedInUser();
			AgreementDetail agrData = agreementGeneration.getAggrementData(financeDetail, allagrDataset.toString(),
					logiedInUser);

			agreementGeneration.setNetFinanceAmount(agrData, financeDetail);

			String finReference = fm.getFinReference();
			String aggName = StringUtils.trimToEmpty(agrementDef.getAggReportName());
			String reportName = "";
			String aggPath = "", templateName = "";

			templateName = agrementDef.getAggReportName();
			AgreementEngine engine = new AgreementEngine(aggPath);
			engine.setTemplate(templateName);
			engine.loadTemplate();
			engine.mergeFields(agrData);

			agreementGeneration.setExtendedMasterDescription(financeDetail, engine);
			agreementGeneration.setFeeDetails(financeDetail, engine);

			if (PennantConstants.DOC_TYPE_PDF.equals(aggtype)) {
				reportName = finReference + "_" + aggName + PennantConstants.DOC_TYPE_PDF_EXT;
				details.setReportName(reportName);
			} else {
				reportName = finReference + "_" + aggName + PennantConstants.DOC_TYPE_WORD_EXT;
				details.setReportName(reportName);
			}
			if (PennantConstants.DOC_TYPE_PDF.equals(aggtype)) {
				details.setDocContent(engine.getDocumentInByteArray(SaveFormat.PDF));
			} else {
				details.setDocContent(engine.getDocumentInByteArray(SaveFormat.DOCX));
			}

			engine.close();
			engine = null;

		} catch (Exception e) {
			if (e instanceof IllegalArgumentException && (e.getMessage().equals("Document site does not exist.")
					|| e.getMessage().equals("Template site does not exist.")
					|| e.getMessage().equals("Template does not exist."))) {

				String[] valueParm = new String[1];
				valueParm[0] = "Loan Aggrement template ";
				agreements.add(details);
				return agreements;

			} else {
				String[] valueParm = new String[1];
				valueParm[0] = "Loan Aggrement template ";
				agreements.add(details);
				return agreements;
			}
		}

		agreements.add(details);

		return agreements;
	}

	public WSReturnStatus updateDeviationStatus(FinanceDeviations financeDeviations) {
		logger.debug(Literal.ENTERING);

		WSReturnStatus response = new WSReturnStatus();
		List<FinanceDeviations> list = new ArrayList<>();

		FinanceDeviations aFinanceDeviations = deviationDetailsService.getFinanceDeviationsByIdAndFinRef(
				financeDeviations.getFinReference(), financeDeviations.getDeviationId(), "_View");
		if (aFinanceDeviations == null) {
			String[] valueParm = new String[1];
			valueParm[0] = financeDeviations.getFinReference() + " and " + financeDeviations.getDeviationId();
			response = APIErrorHandlerService.getFailedStatus("90266", valueParm);
			return response;
		} else {
			LoggedInUser userDetails = SessionUserDetails.getUserDetails(SessionUserDetails.getLogiedInUser());
			aFinanceDeviations.setFinReference(financeDeviations.getFinReference());
			aFinanceDeviations.setDeviationId(financeDeviations.getDeviationId());
			aFinanceDeviations.setApprovalStatus(financeDeviations.getApprovalStatus());
			aFinanceDeviations.setLastMntBy(userDetails.getUserId());
			aFinanceDeviations.setLastMntOn(new Timestamp(System.currentTimeMillis()));
			aFinanceDeviations.setDelegatedUserId(String.valueOf(userDetails.getUserId()));
			if (StringUtils.isNotBlank(financeDeviations.getDelegationRole())) {
				aFinanceDeviations.setDelegationRole(financeDeviations.getDelegationRole());
			}
			list.add(aFinanceDeviations);

		}
		try {
			if (StringUtils.isNotBlank(financeDeviations.getDelegationRole())) {
				deviationDetailsService.processDevaitions(financeDeviations.getFinReference(), list,
						getAuditHeader(financeDeviations.getFinReference()));
			} else {
				deviationDetailsService.processApproval(list, getAuditHeader(financeDeviations.getFinReference()),
						financeDeviations.getFinReference());
			}
			response = APIErrorHandlerService.getSuccessStatus();
		} catch (Exception e) {
			response = APIErrorHandlerService.getFailedStatus();
		}

		logger.debug(Literal.LEAVING);
		return response;
	}

	public AuditHeader getAuditHeader(String finreference) {
		LoggedInUser userDetails = SessionUserDetails.getUserDetails(SessionUserDetails.getLogiedInUser());
		AuditHeader auditHeader = new AuditHeader();
		auditHeader.setAuditModule(ModuleUtil.getTableName(FinanceDeviations.class.getSimpleName()));
		auditHeader.setAuditReference(finreference);
		auditHeader.setAuditUsrId(userDetails.getUserId());
		auditHeader.setAuditBranchCode(userDetails.getBranchCode());
		auditHeader.setAuditDeptCode(userDetails.getDepartmentCode());
		auditHeader.setAuditSystemIP(userDetails.getIpAddress());
		auditHeader.setAuditSessionID(userDetails.getSessionId());
		auditHeader.setUsrLanguage(userDetails.getLanguage());
		return auditHeader;
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

	public DedupParmService getDedupParmService() {
		return dedupParmService;
	}

	public void setDedupParmService(DedupParmService dedupParmService) {
		this.dedupParmService = dedupParmService;
	}

	public AgreementDefinitionService getAgreementDefinitionService() {
		return agreementDefinitionService;
	}

	public void setAgreementDefinitionService(AgreementDefinitionService agreementDefinitionService) {
		this.agreementDefinitionService = agreementDefinitionService;
	}

	public void setNotificationsService(NotificationsService notificationsService) {
		this.notificationsService = notificationsService;
	}

	public void setNotificationService(NotificationService notificationService) {
		this.notificationService = notificationService;
	}

	public AgreementGeneration getAgreementGeneration() {
		return agreementGeneration;
	}

	public void setAgreementGeneration(AgreementGeneration agreementGeneration) {
		this.agreementGeneration = agreementGeneration;
	}

	public void setFinanceTypeService(FinanceTypeService financeTypeService) {
		this.financeTypeService = financeTypeService;
	}

	public FinanceCancellationService getFinanceCancellationService() {
		return financeCancellationService;
	}

	public void setFinanceCancellationService(FinanceCancellationService financeCancellationService) {
		this.financeCancellationService = financeCancellationService;
	}

	public Map<String, String> getUserActions(FinanceMain finMain) {
		String finEvent = FinanceConstants.FINSER_EVENT_ORG;
		FinanceWorkFlow financeWorkFlow = financeWorkFlowService.getApprovedFinanceWorkFlowById(finMain.getFinType(),
				finEvent, PennantConstants.WORFLOW_MODULE_FINANCE);
		WorkFlowDetails workFlowDetails = null;
		if (financeWorkFlow != null) {
			workFlowDetails = WorkFlowUtil.getDetailsByType(financeWorkFlow.getWorkFlowType());
			workFlow = new WorkflowEngine(workFlowDetails.getWorkFlowXml());
		}

		String userAtions = workFlow.getUserActionsAsString(workFlow.getUserTaskId(finMain.getNextRoleCode()), null);
		HashMap<String, String> userActionMap = new HashMap<String, String>();
		String[] list = userAtions.split("/");
		for (String detail : list) {
			String[] status = detail.split("=");
			userActionMap.put(status[0], status[1]);
		}
		return userActionMap;
	}

	public void setChequeHeaderService(ChequeHeaderService chequeHeaderService) {
		this.chequeHeaderService = chequeHeaderService;
	}

	public void setRemarksWebServiceImpl(RemarksWebServiceImpl remarksWebServiceImpl) {
		this.remarksWebServiceImpl = remarksWebServiceImpl;
	}

	public void setRemarksController(RemarksController remarksController) {
		this.remarksController = remarksController;
	}

	public void setPartnerBankService(PartnerBankService partnerBankService) {
		this.partnerBankService = partnerBankService;
	}

	public AgreementDefinitionDAO getAgreementDefinitionDAO() {
		return agreementDefinitionDAO;
	}

	public void setAgreementDefinitionDAO(AgreementDefinitionDAO agreementDefinitionDAO) {
		this.agreementDefinitionDAO = agreementDefinitionDAO;
	}

	public void setReasonDetailDAO(ReasonDetailDAO reasonDetailDAO) {
		this.reasonDetailDAO = reasonDetailDAO;
	}

	public void setFinTypePartnerBankService(FinTypePartnerBankService finTypePartnerBankService) {
		this.finTypePartnerBankService = finTypePartnerBankService;
	}

	public void setDeviationDetailsService(FinanceDeviationsService deviationDetailsService) {
		this.deviationDetailsService = deviationDetailsService;
	}

}
