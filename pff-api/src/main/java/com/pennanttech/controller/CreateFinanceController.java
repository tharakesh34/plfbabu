package com.pennanttech.controller;

import java.io.File;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
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
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.zkoss.util.resource.Labels;

import com.aspose.words.SaveFormat;
import com.pennant.app.constants.AccountConstants;
import com.pennant.app.constants.ImplementationConstants;
import com.pennant.app.util.APIHeader;
import com.pennant.app.util.CDScheduleCalculator;
import com.pennant.app.util.DateUtility;
import com.pennant.app.util.ErrorUtil;
import com.pennant.app.util.FeeScheduleCalculator;
import com.pennant.app.util.GSTCalculator;
import com.pennant.app.util.PathUtil;
import com.pennant.app.util.ReceiptCalculator;
import com.pennant.app.util.ReferenceGenerator;
import com.pennant.app.util.ReferenceUtil;
import com.pennant.app.util.RuleExecutionUtil;
import com.pennant.app.util.ScheduleCalculator;
import com.pennant.app.util.ScheduleGenerator;
import com.pennant.app.util.SessionUserDetails;
import com.pennant.app.util.SysParamUtil;
import com.pennant.app.util.TDSCalculator;
import com.pennant.backend.dao.amtmasters.VehicleDealerDAO;
import com.pennant.backend.dao.applicationmaster.AgreementDefinitionDAO;
import com.pennant.backend.dao.applicationmaster.BounceReasonDAO;
import com.pennant.backend.dao.audit.AuditHeaderDAO;
import com.pennant.backend.dao.customermasters.CustomerDAO;
import com.pennant.backend.dao.documentdetails.DocumentDetailsDAO;
import com.pennant.backend.dao.feetype.FeeTypeDAO;
import com.pennant.backend.dao.finance.FinAdvancePaymentsDAO;
import com.pennant.backend.dao.finance.FinFeeReceiptDAO;
import com.pennant.backend.dao.finance.FinPlanEmiHolidayDAO;
import com.pennant.backend.dao.finance.FinanceMainDAO;
import com.pennant.backend.dao.finance.FinanceProfitDetailDAO;
import com.pennant.backend.dao.finance.FinanceScheduleDetailDAO;
import com.pennant.backend.dao.finance.covenant.CovenantTypeDAO;
import com.pennant.backend.dao.lmtmasters.FinanceReferenceDetailDAO;
import com.pennant.backend.dao.mandate.MandateDAO;
import com.pennant.backend.dao.pdc.ChequeDetailDAO;
import com.pennant.backend.dao.reason.deatil.ReasonDetailDAO;
import com.pennant.backend.dao.receipts.FinReceiptHeaderDAO;
import com.pennant.backend.dao.rmtmasters.PromotionDAO;
import com.pennant.backend.dao.rulefactory.RuleDAO;
import com.pennant.backend.dao.solutionfactory.StepPolicyDetailDAO;
import com.pennant.backend.dao.solutionfactory.StepPolicyHeaderDAO;
import com.pennant.backend.dao.staticparms.ExtendedFieldHeaderDAO;
import com.pennant.backend.dao.systemmasters.DivisionDetailDAO;
import com.pennant.backend.model.Notes;
import com.pennant.backend.model.WSReturnStatus;
import com.pennant.backend.model.WorkFlowDetails;
import com.pennant.backend.model.amtmasters.VehicleDealer;
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
import com.pennant.backend.model.finance.ChequeDetail;
import com.pennant.backend.model.finance.ChequeHeader;
import com.pennant.backend.model.finance.FeeType;
import com.pennant.backend.model.finance.FinAdvancePayments;
import com.pennant.backend.model.finance.FinCovenantType;
import com.pennant.backend.model.finance.FinFeeDetail;
import com.pennant.backend.model.finance.FinFeeReceipt;
import com.pennant.backend.model.finance.FinOCRCapture;
import com.pennant.backend.model.finance.FinOCRDetail;
import com.pennant.backend.model.finance.FinOCRHeader;
import com.pennant.backend.model.finance.FinODPenaltyRate;
import com.pennant.backend.model.finance.FinPlanEmiHoliday;
import com.pennant.backend.model.finance.FinReceiptHeader;
import com.pennant.backend.model.finance.FinScheduleData;
import com.pennant.backend.model.finance.FinanceDedup;
import com.pennant.backend.model.finance.FinanceDetail;
import com.pennant.backend.model.finance.FinanceDeviations;
import com.pennant.backend.model.finance.FinanceDisbursement;
import com.pennant.backend.model.finance.FinanceMain;
import com.pennant.backend.model.finance.FinanceProfitDetail;
import com.pennant.backend.model.finance.FinanceScheduleDetail;
import com.pennant.backend.model.finance.FinanceStepPolicyDetail;
import com.pennant.backend.model.finance.FinanceSummary;
import com.pennant.backend.model.finance.GuarantorDetail;
import com.pennant.backend.model.finance.JointAccountDetail;
import com.pennant.backend.model.finance.ManualAdvise;
import com.pennant.backend.model.finance.TaxAmountSplit;
import com.pennant.backend.model.finance.Taxes;
import com.pennant.backend.model.finance.covenant.Covenant;
import com.pennant.backend.model.finance.covenant.CovenantDocument;
import com.pennant.backend.model.finance.covenant.CovenantType;
import com.pennant.backend.model.finance.financetaxdetail.FinanceTaxDetail;
import com.pennant.backend.model.finance.psl.PSLDetail;
import com.pennant.backend.model.financemanagement.FinFlagsDetail;
import com.pennant.backend.model.lmtmasters.FinanceReferenceDetail;
import com.pennant.backend.model.lmtmasters.FinanceWorkFlow;
import com.pennant.backend.model.loanauthentication.LoanAuthentication;
import com.pennant.backend.model.mandate.Mandate;
import com.pennant.backend.model.partnerbank.PartnerBank;
import com.pennant.backend.model.paymentmode.PaymentMode;
import com.pennant.backend.model.reason.details.ReasonDetails;
import com.pennant.backend.model.reason.details.ReasonHeader;
import com.pennant.backend.model.rmtmasters.FinTypePartnerBank;
import com.pennant.backend.model.rmtmasters.FinanceType;
import com.pennant.backend.model.rmtmasters.Promotion;
import com.pennant.backend.model.rulefactory.Notifications;
import com.pennant.backend.model.rulefactory.Rule;
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
import com.pennant.backend.service.finance.FinFeeDetailService;
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
import com.pennant.backend.util.PennantApplicationUtil;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.RuleConstants;
import com.pennant.backend.util.RuleReturnType;
import com.pennant.backend.util.SMTParameterConstants;
import com.pennant.backend.util.VASConsatnts;
import com.pennant.backend.util.WorkFlowUtil;
import com.pennant.pff.core.loan.util.LoanClosureCalculator;
import com.pennant.pff.mandate.ChequeSatus;
import com.pennant.pff.mandate.InstrumentType;
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
import com.pennanttech.pennapps.core.util.DateUtil;
import com.pennanttech.pff.constants.AccountingEvent;
import com.pennanttech.pff.constants.FinServiceEvent;
import com.pennanttech.pff.core.RequestSource;
import com.pennanttech.pff.core.TableType;
import com.pennanttech.pff.core.util.SchdUtil;
import com.pennanttech.pff.document.DocumentService;
import com.pennanttech.pff.notifications.service.NotificationService;
import com.pennanttech.pff.overdue.constants.ChargeType;
import com.pennanttech.service.impl.RemarksWebServiceImpl;
import com.pennanttech.util.APIConstants;
import com.pennanttech.ws.model.customer.AgreementRequest;
import com.pennanttech.ws.model.eligibility.AgreementData;
import com.pennanttech.ws.model.finance.MoveLoanStageRequest;
import com.pennanttech.ws.model.financetype.FinInquiryDetail;
import com.pennanttech.ws.model.financetype.FinanceInquiry;
import com.pennanttech.ws.service.APIErrorHandlerService;
import com.pennattech.pff.receipt.model.ReceiptDTO;

public class CreateFinanceController extends SummaryDetailService {
	private static final Logger logger = LogManager.getLogger(CreateFinanceController.class);

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
	private FeeTypeDAO feeTypeDAO;
	private VehicleDealerDAO vehicleDealerDao;
	private VehicleDealer vehicleDealer;
	private CovenantTypeDAO covenantTypeDAO;
	private PromotionDAO promotionDAO;
	private RuleDAO ruleDAO;
	private FinFeeDetailService finFeeDetailService;
	private FinanceProfitDetailDAO financeProfitDetailDAO;
	private CustomerDAO customerDAO;
	private ChequeDetailDAO chequeDetailDAO;
	private BounceReasonDAO bounceReasonDAO;
	private MandateDAO mandateDAO;

	public FinanceDetail doCreateFinance(FinanceDetail fd, boolean loanWithWIF) {
		logger.info(Literal.ENTERING);

		FinScheduleData schdData = fd.getFinScheduleData();
		FinanceMain fm = schdData.getFinanceMain();
		FinanceType financeType = schdData.getFinanceType();

		String finEvent = FinServiceEvent.ORG;
		String finType = financeType.getFinType();
		String entityCode = financeType.getLovDescEntityCode();

		fm.setFinType(finType);
		fm.setEntityCode(entityCode);
		fm.setLovDescEntityCode(entityCode);
		fm.setDefferments(financeType.getFinMaxDifferment());

		try {

			if (financeType.isFinIsGenRef()) {
				fm.setFinReference(null);
			}

			String finReference = null;
			if (StringUtils.isBlank(fm.getFinReference())) {
				finReference = ReferenceGenerator.generateFinRef(fm, financeType);
			} else {
				finReference = fm.getFinReference();
			}

			long finID = fm.getFinID();

			if (finID <= 0) {
				ReferenceGenerator.generateFinID(fm);
				finID = fm.getFinID();
			}

			fm.setFinReference(finReference);
			schdData.setFinID(finID);
			schdData.setFinReference(finReference);

			if (fm.getUserDetails() == null) {
				LoggedInUser userDetails = SessionUserDetails.getUserDetails(SessionUserDetails.getLogiedInUser());
				fm.setUserDetails(userDetails);
			}

			WorkFlowDetails workFlowDetails = null;
			String roleCode = null;
			String taskid = null;
			boolean stp = fd.isStp();
			long workFlowId = 0;

			if (fm.isQuickDisb()) {
				int finRefType = FinanceConstants.PROCEDT_LIMIT;
				String quickDisbCode = FinanceConstants.QUICK_DISBURSEMENT;
				String roles = financeReferenceDetailDAO.getAllowedRolesByCode(finType, finRefType, quickDisbCode,
						finEvent);
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

				FinanceWorkFlow financeWorkFlow = financeWorkFlowService.getApprovedFinanceWorkFlowById(fm.getFinType(),
						finEvent, PennantConstants.WORFLOW_MODULE_FINANCE);
				if (financeWorkFlow != null) {
					workFlowDetails = WorkFlowUtil.getDetailsByType(financeWorkFlow.getWorkFlowType());
					if (workFlowDetails != null) {
						WorkflowEngine workflow = new WorkflowEngine(workFlowDetails.getWorkFlowXml());
						taskid = workflow.getUserTaskId(roleCode);
						workFlowId = workFlowDetails.getWorkFlowId();
					}
				}

			}

			fm.setRecordStatus(getRecordStatus(fm.isQuickDisb(), fd.isStp()));

			if (!stp) {
				fd = nonStpProcess(fd);
				if (fd.getReturnStatus() == null) {
					taskid = fm.getTaskId();
					roleCode = fm.getRoleCode();
					workFlowId = fm.getWorkflowId();
				} else {
					return fd;
				}
			}

			fm.setRecordType(PennantConstants.RECORD_TYPE_NEW);
			fm.setWorkflowId(workFlowId);
			fm.setRoleCode(roleCode);
			fm.setNextRoleCode(roleCode);
			fm.setTaskId(taskid);
			fm.setNextTaskId(getNextTaskId(taskid, fm.isQuickDisb(), stp));
			fm.setNewRecord(true);
			fm.setLastMntOn(new Timestamp(System.currentTimeMillis()));
			fm.setLastMntBy(getLastMntBy(fm.isQuickDisb(), fm.getUserDetails()));
			fm.setRecordStatus(getRecordStatus(fm.isQuickDisb(), stp));
			fm.setFinSourceID(PennantConstants.FINSOURCE_ID_API);

			// set vancode
			if (financeType.isAlwVan() && SysParamUtil.isAllowed(SMTParameterConstants.VAN_REQUIRED)) {
				financeType.setFinTypePartnerBankList(
						finTypePartnerBankService.getPartnerBanks(fm.getFinType(), TableType.AVIEW));
				List<FinTypePartnerBank> finTypePartnerBankList = financeType.getFinTypePartnerBankList();
				for (FinTypePartnerBank finTypePartnerBank : finTypePartnerBankList) {
					if (StringUtils.equals(finTypePartnerBank.getPurpose(), AccountConstants.PARTNERSBANK_RECEIPTS)
							&& finTypePartnerBank.isVanApplicable()) {
						PartnerBank bank = partnerBankService
								.getApprovedPartnerBankById(finTypePartnerBank.getPartnerBankID());
						if (bank != null && StringUtils.isNotBlank(bank.getVanCode())) {
							if (StringUtils.isNotBlank(fm.getFinReference())) {
								fm.setVanCode((bank.getVanCode().concat(fm.getFinReference())));
								break;
							}
						}
					}
				}
			}

			doSetRequiredDetails(fd, loanWithWIF, fm.getUserDetails(), stp, false, false);

			schdData = fd.getFinScheduleData();
			fm = schdData.getFinanceMain();

			if (schdData.getExternalReference() != null && !schdData.getExternalReference().isEmpty()) {
				if (schdData.isUpfrontAuto()) {
					adjustFeesAuto(schdData);
				} else {

					adjustFees(schdData);
				}
			}

			if (schdData.getErrorDetails() != null) {
				for (ErrorDetail errorDetail : schdData.getErrorDetails()) {
					FinanceDetail response = new FinanceDetail();
					doEmptyResponseObject(response);
					response.setReturnStatus(
							APIErrorHandlerService.getFailedStatus(errorDetail.getCode(), errorDetail.getError()));
					return response;
				}
			}

			List<FinanceScheduleDetail> schedules = schdData.getFinanceScheduleDetails();
			if (!loanWithWIF) {
				// call schedule calculator
				fm.setCalculateRepay(true);
				if (StringUtils.equals(FinanceConstants.PRODUCT_ODFACILITY, fm.getProductCategory())) {
					if (schdData.getOverdraftScheduleDetails() != null) {
						schdData.getOverdraftScheduleDetails().clear();
					}
					// To Rebuild the overdraft if any fields are changed
					fm.setEventFromDate(fm.getFinStartDate());
					schdData = ScheduleCalculator.buildODSchedule(schdData);
					fd.setFinScheduleData(schdData);
					fm.setLovDescIsSchdGenerated(true);

				} else if (StringUtils.equals(FinanceConstants.PRODUCT_CD, fm.getProductCategory())) {
					doSetDueDate(fm);
					schdData = ScheduleGenerator.getNewSchd(schdData);
					if (schedules.size() != 0) {
						schdData = CDScheduleCalculator.getCalSchd(schdData);
						schdData.setSchduleGenerated(true);
						adjustCDDownpay(fd);
					}
				} else {
					schdData = ScheduleGenerator.getNewSchd(schdData);
					if (schedules.size() != 0) {
						schdData = ScheduleCalculator.getCalSchd(schdData, BigDecimal.ZERO);
						schdData.setSchduleGenerated(true);
						// process planned EMI details
						doProcessPlanEMIHDays(schdData);
					}
				}

				if (schdData.getErrorDetails() != null) {
					for (ErrorDetail ed : schdData.getErrorDetails()) {
						FinanceDetail response = new FinanceDetail();
						doEmptyResponseObject(response);
						response.setReturnStatus(APIErrorHandlerService.getFailedStatus(ed.getCode(), ed.getError()));
						return response;
					}
				}

				if (!"#".equals(fm.getAdvType()) || !"#".equals(fm.getGrcAdvType())) {
					Map<String, BigDecimal> taxPercentages = GSTCalculator.getTaxPercentages(fm.getCustID(),
							fm.getFinCcy(), null, fm.getFinBranch());
					for (FinFeeDetail finfee : schdData.getFinFeeDetailList()) {
						finFeeDetailService.calculateFees(finfee, schdData, taxPercentages);
					}
				}

				// fees calculation
				if (!schdData.getFinFeeDetailList().isEmpty()) {
					schdData = FeeScheduleCalculator.feeSchdBuild(schdData);
				}

			} else {
				fm.setCalculateRepay(true);
				schdData.setSchduleGenerated(true);
			}

			if (!fm.isAlwBPI()) {
				if (stp && !loanWithWIF) {
					schdData.getDisbursementDetails().clear();
				}
				setDisbursements(fd, loanWithWIF, false, false);
			}

			if (!schdData.getErrorDetails().isEmpty()) {
				fd.setFinScheduleData(schdData);
				return fd;
			}

			if (fm.isAlwBPI()) {
				if (stp && !loanWithWIF) {
					schdData.getDisbursementDetails().clear();
				}
				setDisbursements(fd, loanWithWIF, false, false);
			}

			if (fd.getFinScheduleData().getErrorDetails() != null) {
				for (ErrorDetail errorDetail : schdData.getErrorDetails()) {
					FinanceDetail response = new FinanceDetail();
					doEmptyResponseObject(response);
					response.setReturnStatus(
							APIErrorHandlerService.getFailedStatus(errorDetail.getCode(), errorDetail.getError()));
					return response;
				}
			}

			if (fd.getChequeHeader() != null) {
				validateChequeDetails(fd);
				List<ErrorDetail> errorList = schdData.getErrorDetails();
				if (CollectionUtils.isNotEmpty(errorList)) {
					fd = new FinanceDetail();
					doEmptyResponseObject(fd);
					for (ErrorDetail ed : errorList) {
						fd.setReturnStatus(APIErrorHandlerService.getFailedStatus(ed.getCode(), ed.getError()));
						return fd;
					}
				}
			}
			fm.setLastRepayDate(fm.getFinStartDate());
			fm.setLastRepayPftDate(fm.getFinStartDate());
			fm.setLastRepayRvwDate(fm.getFinStartDate());
			fm.setLastRepayCpzDate(fm.getFinStartDate());

			fm.setFinRemarks("SUCCESS");

			for (FinanceScheduleDetail schd : schedules) {
				schd.setLastMntOn(new Timestamp(System.currentTimeMillis()));
				schd.setRecordStatus(getRecordStatus(fm.isQuickDisb(), stp));
				schd.setWorkflowId(workFlowId);
				schd.setRoleCode(roleCode);
				schd.setNextRoleCode(roleCode);
				schd.setTaskId(taskid);
				schd.setNextTaskId(fm.getNextTaskId());
				schd.setFinID(finID);
				schd.setFinReference(finReference);

				if (StringUtils.isBlank(schd.getBaseRate())) {
					schd.setBaseRate(null);
				}
			}

			fd.setUserAction("");
			fd.setExtSource(false);
			fd.setAccountingEventCode(PennantApplicationUtil.getEventCode(fm.getFinStartDate()));
			fd.setFinID(finID);
			fd.setFinReference(finReference);

			if (!StringUtils.equals(FinanceConstants.PRODUCT_ODFACILITY, fm.getProductCategory())) {
				fd.setFinScheduleData(schdData);
			}

			AuditDetail auditDetail = new AuditDetail(PennantConstants.TRAN_WF, 1, null, fd);
			AuditHeader auditHeader = new AuditHeader(finReference, null, null, null, auditDetail, fm.getUserDetails(),
					new HashMap<>());

			APIHeader reqHeaderDetails = (APIHeader) PhaseInterceptorChain.getCurrentMessage().getExchange()
					.get(APIHeader.API_HEADER_KEY);
			auditHeader.setApiHeader(reqHeaderDetails);

			if (stp && !fm.isQuickDisb()) {
				WSReturnStatus returnStatus = prepareAgrrementDetails(auditHeader);
				if (returnStatus != null && StringUtils.isNotBlank(returnStatus.getReturnCode())) {
					fd = new FinanceDetail();
					String[] valueParm = new String[1];
					valueParm[0] = "Loan Aggrement template ";
					doEmptyResponseObject(fd);
					fd.setReturnStatus(APIErrorHandlerService.getFailedStatus("API004", valueParm));
					return fd;
				}
				auditHeader = financeDetailService.doApprove(auditHeader, false);
			} else if (fm.isQuickDisb() || !stp) {
				String usrAction = null;
				String role = null;
				if (ImplementationConstants.CLIENT_NFL) {
					usrAction = "Approve";
					fm.setRecordStatus("Approve");
					role = workFlow.firstTaskOwner();
				} else {
					usrAction = "Save";
					fm.setRecordStatus("Saved");
					role = workFlow.firstTaskOwner();
				}
				// dedup check
				if (!stp) {
					List<FinanceDedup> financeDedupList = prepareFinanceDedup(role, fd);
					if (CollectionUtils.isNotEmpty(financeDedupList)) {
						fd = new FinanceDetail();
						String[] valueParm = new String[1];
						valueParm[0] = "Loan Dedup";
						doEmptyResponseObject(fd);
						fd.setReturnStatus(APIErrorHandlerService.getFailedStatus("90343", valueParm));
						return fd;
					}
				}
				auditHeader = financeDetailService.executeWorkflowServiceTasks(auditHeader, role, usrAction, workFlow);

			}

			if (auditHeader.getOverideMessage() != null && auditHeader.getOverideMessage().size() > 0) {
				for (ErrorDetail ed : auditHeader.getOverideMessage()) {
					fd = new FinanceDetail();
					doEmptyResponseObject(fd);
					fd.setReturnStatus(APIErrorHandlerService.getFailedStatus(ed.getCode(), ed.getError()));
					return fd;
				}
			}
			if (auditHeader.getErrorMessage() != null) {
				for (ErrorDetail ed : auditHeader.getErrorMessage()) {
					fd = new FinanceDetail();
					doEmptyResponseObject(fd);
					fd.setReturnStatus(APIErrorHandlerService.getFailedStatus(ed.getCode(), ed.getError()));
					return fd;
				}
			}

			if (auditHeader.getAuditDetail().getErrorDetails() != null) {
				for (ErrorDetail ed : auditHeader.getAuditDetail().getErrorDetails()) {
					fd = new FinanceDetail();
					doEmptyResponseObject(fd);
					fd.setReturnStatus(APIErrorHandlerService.getFailedStatus(ed.getCode(), ed.getError()));
					return fd;
				}
			}

			if (StringUtils.isNotBlank(finReference)) {
				// prepare response object
				fd = getFinanceDetailResponse(auditHeader);
				fd.setStp(false);
				fd.setReturnStatus(APIErrorHandlerService.getSuccessStatus());

				logger.debug(Literal.LEAVING);
				return fd;
			}
		} catch (InterfaceException ex) {
			logger.error(Literal.EXCEPTION, ex);
			FinanceDetail response = new FinanceDetail();
			doEmptyResponseObject(response);
			response.setReturnStatus(APIErrorHandlerService.getFailedStatus("9999", ex.getMessage()));
			return response;
		} catch (AppException ex) {
			logger.error(Literal.EXCEPTION, ex);
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

	private void validateChequeDetails(FinanceDetail fd) {
		boolean date = true;
		FinScheduleData schdData = fd.getFinScheduleData();
		FinanceMain fm = schdData.getFinanceMain();
		ChequeHeader ch = fd.getChequeHeader();
		List<ChequeDetail> cheques = ch.getChequeDetailList();

		String repayMethod = fm.getFinRepayMethod();

		List<Date> chequeDates = new ArrayList<>();

		for (ChequeDetail cheque : cheques) {

			if (chequeDates.contains(cheque.getChequeDate())) {
				String[] valueParm = new String[2];
				valueParm[0] = "Cheque Dates";
				schdData.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetail("90273", valueParm)));
				break;
			}

			if (InstrumentType.isPDC(cheque.getChequeType()) && !InstrumentType.isPDC(repayMethod)) {
				String[] valueParm = new String[2];
				valueParm[0] = "Cheques";
				valueParm[1] = "finRepayMethod is " + repayMethod;

				schdData.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetail("90204", valueParm)));
			}

			if (!InstrumentType.isPDC(cheque.getChequeType())) {
				continue;
			}

			List<FinanceScheduleDetail> schedules = fd.getFinScheduleData().getFinanceScheduleDetails();
			for (FinanceScheduleDetail fsd : schedules) {
				if (DateUtil.compare(fsd.getSchDate(), cheque.getChequeDate()) != 0) {
					date = false;
					continue;
				}

				date = true;
				cheque.seteMIRefNo(fsd.getInstNumber());
				chequeDates.add(cheque.getChequeDate());

				if (fsd.getRepayAmount().compareTo(cheque.getAmount()) == 0) {
					break;
				}

				String[] valueParm = new String[2];
				valueParm[0] = "Cheque Date " + new SimpleDateFormat("yyyy-MM-dd").format(fsd.getSchDate());
				valueParm[1] = String.valueOf("Amount :" + fsd.getRepayAmount() + "INR");
				schdData.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetail("30570", valueParm)));
				return;
			}

			if (date) {
				continue;
			}

			String[] valueParm = new String[2];
			valueParm[0] = "Cheque Date";
			valueParm[1] = "ScheduleDates";
			schdData.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetail("30570", valueParm)));
			break;
		}

		String[] valueParm = new String[2];
		valueParm[0] = Labels.getLabel("label_ChequeDetailDialog_NoOfCheques.value");

		if (InstrumentType.isPDC(fm.getFinRepayMethod())) {
			List<FinanceScheduleDetail> fsd = schdData.getFinanceScheduleDetails();

			int noOfSchedules = fsd.size() - 1;
			int noOfPDCCheques = SysParamUtil.getValueAsInt(SMTParameterConstants.NUMBEROF_PDC_CHEQUES);

			int number = noOfSchedules;
			if (noOfSchedules >= noOfPDCCheques) {
				number = noOfPDCCheques;
			}

			if (ch.getNoOfCheques() < number) {
				valueParm[1] = String.valueOf(number);
				schdData.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetail("65012", valueParm)));
				return;
			}
		}
	}

	private WSReturnStatus prepareAgrrementDetails(AuditHeader auditHeader) {
		FinanceDetail fd = (FinanceDetail) auditHeader.getAuditDetail().getModelData();

		FinScheduleData schdData = fd.getFinScheduleData();
		FinanceMain fm = schdData.getFinanceMain();
		String finType = fm.getFinType();

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

		List<Long> finRefIds = financeReferenceDetailDAO.getRefIdListByRefType(finType, FinServiceEvent.ORG,
				PennantConstants.REC_ON_APPR, FinanceConstants.PROCEDT_TEMPLATE);

		if (CollectionUtils.isEmpty(finRefIds)) {
			return null;
		}

		List<Notifications> notifications = notificationsService.getApprovedNotificationsByRuleIdList(finRefIds);
		List<String> docCatogires = new ArrayList<>();
		String[] docTypes = null;

		for (Notifications mailNotification : notifications) {
			docTypes = notificationService.getAttchmentRuleResult(mailNotification.getRuleAttachment(), fd);
			for (String docType : docTypes) {
				docCatogires.add(docType);
			}
		}

		List<FinanceReferenceDetail> finRefDetails = financeReferenceDetailDAO.getFinanceProcessEditorDetails(finType,
				FinServiceEvent.ORG, "_FINVIEW");

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
			AgreementDetail agrData = getAgreementGeneration().getAggrementData(fd, allagrDataset.toString(),
					SessionUserDetails.getLogiedInUser());
			for (String tempName : agrdefMap.keySet()) {

				AgreementDefinition aggdef = agrdefMap.get(tempName);
				try {
					documentDetails = autoGenerateAgreement(finRefMap.get(tempName), fd, aggdef,
							fd.getDocumentDetailsList(), agrData);
					if (documentDetails.getReturnStatus() != null
							&& StringUtils.isNotBlank(documentDetails.getReturnStatus().getReturnCode())) {
						return documentDetails.getReturnStatus();
					}
				} catch (Exception e) {
					logger.error(Literal.EXCEPTION, e);
				}
				agenDocList.add(documentDetails);

			}
			if (fd.getDocumentDetailsList() == null) {
				fd.setDocumentDetailsList(new ArrayList<DocumentDetails>());
			}
			fd.getDocumentDetailsList().addAll(agenDocList);
			agrdefMap = null;
			finRefMap = null;
			allagrDataset = null;

		}
		return null;
	}

	private DocumentDetails autoGenerateAgreement(FinanceReferenceDetail frefdata, FinanceDetail financeDetail,
			AgreementDefinition agreementDefinition, List<DocumentDetails> existingUploadDocList,
			AgreementDetail detail) {
		logger.debug(Literal.ENTERING);
		DocumentDetails details = new DocumentDetails();

		try {
			if (financeDetail != null && financeDetail.getFinScheduleData() != null
					&& financeDetail.getFinScheduleData().getFinanceMain() != null) {
				FinanceMain lmain = financeDetail.getFinScheduleData().getFinanceMain();
				String finReference = lmain.getFinReference();
				String aggName = StringUtils.trimToEmpty(frefdata.getLovDescNamelov());
				String reportName = "";

				String templateName = null;
				if (StringUtils.trimToEmpty(frefdata.getLovDescAggReportName()).contains("/")) {
					String aggRptName = StringUtils.trimToEmpty(frefdata.getLovDescAggReportName());
					templateName = aggRptName.substring(aggRptName.lastIndexOf("/") + 1, aggRptName.length());
				} else {
					templateName = frefdata.getLovDescAggReportName();
				}

				AgreementEngine engine = new AgreementEngine();
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
						// Protect the agreement as per the configuration.
						exstDetails.setDocImage(engine.getDocumentInByteArrayWithPwd(reportName,
								agreementDefinition.isPwdProtected(), financeDetail));
					} else {
						exstDetails.setDocImage(engine.getDocumentInByteArray(SaveFormat.DOCX));
					}

					// since it is an existing document record has to be store
					// in document manager
					exstDetails.setDocRefId(null);
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
					// Protect the agreement as per the configuration.
					details.setDocImage(engine.getDocumentInByteArrayWithPwd(reportName,
							agreementDefinition.isPwdProtected(), financeDetail));
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
				details.setFinEvent(FinServiceEvent.ORG);
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

	private List<FinanceDedup> prepareFinanceDedup(String userRole, FinanceDetail fd) {
		// Data Preparation for Rule Executions
		Customer customer = fd.getCustomerDetails().getCustomer();
		FinanceDedup dedup = new FinanceDedup();
		dedup.setCustId(customer.getCustID());
		dedup.setCustCRCPR(customer.getCustCRCPR());
		dedup.setCustCIF(customer.getCustCIF());
		dedup.setCustFName(customer.getCustFName());
		dedup.setCustMName(customer.getCustMName());
		dedup.setCustLName(customer.getCustLName());
		dedup.setCustShrtName(customer.getCustShrtName());
		dedup.setCustMotherMaiden(customer.getCustMotherMaiden());
		dedup.setCustNationality(customer.getCustNationality());
		dedup.setCustParentCountry(customer.getCustParentCountry());
		dedup.setCustDOB(customer.getCustDOB());
		dedup.setMobileNumber(getCustMobileNum(fd));
		dedup.setTradeLicenceNo(customer.getCustTradeLicenceNum());

		// Check Customer is Existing or New Customer Object
		FinanceMain aFinanceMain = fd.getFinScheduleData().getFinanceMain();

		// finance data to set in to finance dedup
		dedup.setFinanceAmount(aFinanceMain.getFinAmount());
		dedup.setProfitAmount(aFinanceMain.getTotalGrossPft());
		dedup.setFinanceType(aFinanceMain.getFinType());
		dedup.setStartDate(aFinanceMain.getFinStartDate());
		dedup.setFinLimitRef(aFinanceMain.getFinLimitRef());

		dedup.setFinReference(aFinanceMain.getFinReference());
		dedup.setLikeCustFName(dedup.getCustFName() != null ? "%" + dedup.getCustFName() + "%" : "");
		dedup.setLikeCustMName(dedup.getCustMName() != null ? "%" + dedup.getCustMName() + "%" : "");
		dedup.setLikeCustLName(dedup.getCustLName() != null ? "%" + dedup.getCustLName() + "%" : "");

		// For Existing Customer/ New Customer
		List<FinanceDedup> loanDedup = new ArrayList<FinanceDedup>();
		List<FinanceDedup> dedupeRuleData = dedupParmService.fetchFinDedupDetails(userRole, dedup, null,
				aFinanceMain.getFinType());
		loanDedup.addAll(dedupeRuleData);
		return loanDedup;
	}

	// get the mobile number for Customer
	private String getCustMobileNum(FinanceDetail fd) {
		String custMobileNumber = "";
		if (fd.getCustomerDetails().getCustomerPhoneNumList() != null) {
			for (CustomerPhoneNumber custPhone : fd.getCustomerDetails().getCustomerPhoneNumList()) {
				if (custPhone.getPhoneTypeCode().equals(PennantConstants.PHONETYPE_MOBILE)) {
					custMobileNumber = PennantApplicationUtil.formatPhoneNumber(custPhone.getPhoneCountryCode(),
							custPhone.getPhoneAreaCode(), custPhone.getPhoneNumber());
					break;
				}
			}
		}
		return custMobileNumber;
	}

	private FinanceDetail nonStpProcess(FinanceDetail fd) {
		logger.debug(Literal.ENTERING);

		FinanceMain fm = fd.getFinScheduleData().getFinanceMain();
		String finEvent = FinServiceEvent.ORG;
		FinanceWorkFlow financeWorkFlow = financeWorkFlowService.getApprovedFinanceWorkFlowById(fm.getFinType(),
				finEvent, PennantConstants.WORFLOW_MODULE_FINANCE);
		WorkFlowDetails workFlowDetails = null;
		String processStage = fd.getProcessStage();
		fd.setActionSave(true);

		if (financeWorkFlow != null) {
			workFlowDetails = WorkFlowUtil.getDetailsByType(financeWorkFlow.getWorkFlowType());
			workFlow = new WorkflowEngine(workFlowDetails.getWorkFlowXml());
		} else {
			FinanceDetail response = new FinanceDetail();
			doEmptyResponseObject(response);
			String[] valueParm = new String[2];
			valueParm[0] = fm.getFinType();
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
			fm.setTaskId(taskId);
			fm.setRoleCode(roleCode);
			fm.setWorkflowId(workflowId);
		}
		if (fm.isLegalRequired()) {
			String finType = fm.getFinType();
			int finRefType = FinanceConstants.PROCEDT_LIMIT;
			String quickDisbCode = FinanceConstants.PROCEDT_LEGAL_INIT;
			String roles = financeReferenceDetailDAO.getAllowedRolesByCode(finType, finRefType, quickDisbCode,
					finEvent);
			boolean allowed = false;
			if (StringUtils.isNotBlank(roles)) {
				String[] roleCodes = roles.split(PennantConstants.DELIMITER_COMMA);
				for (String roleCod : roleCodes) {
					if (StringUtils.equals(fm.getRoleCode(), roleCod)) {
						allowed = true;
						break;
					}
				}
			}
			if (!allowed) {
				FinanceDetail response = new FinanceDetail();
				doEmptyResponseObject(response);
				String[] valueParm = new String[2];
				valueParm[1] = fm.getFinType();
				valueParm[0] = "LegalRequired";
				response.setReturnStatus(APIErrorHandlerService.getFailedStatus("90285", valueParm));
				return response;
			}
		}
		logger.debug(Literal.LEAVING);
		return fd;
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

	private void doSetRequiredDetails(FinanceDetail fd, boolean loanWithWIF, LoggedInUser userDetails, boolean stp,
			boolean approve, boolean moveLoanStage) throws AppException {
		logger.debug(Literal.ENTERING);

		fd.setModuleDefiner(FinServiceEvent.ORG);
		fd.setUserDetails(userDetails);
		fd.setNewRecord(true);

		FinScheduleData schdData = fd.getFinScheduleData();
		FinanceMain fm = schdData.getFinanceMain();

		long custID = fm.getCustID();

		fm.setVersion(1);
		fm.setFinIsActive(true);
		fm.setFinStatus(financeDetailService.getCustStatusByMinDueDays());

		if (fm.getMaturityDate() == null) {
			fm.setMaturityDate(fm.getCalMaturity());
		}
		if (fm.getNumberOfTerms() <= 0) {
			fm.setNumberOfTerms(fm.getCalTerms());
		}
		if (fm.getGrcPeriodEndDate() == null) {
			fm.setGrcPeriodEndDate(fm.getCalGrcEndDate());
		}
		if (fm.getGraceTerms() <= 0) {
			fm.setGraceTerms(fm.getCalGrcTerms());
		}
		fm.setFinCurrAssetValue(fm.getFinAmount());

		// set Head branch
		if (StringUtils.isBlank(fm.getFinBranch())) {
			fm.setFinBranch(userDetails.getBranchCode());
		}

		CustomerDetails customerDetails = null;

		if (custID > 0) {
			customerDetails = customerDetailsService.getApprovedCustomerById(custID);
			if (customerDetails != null) {
				customerDetails.setUserDetails(userDetails);
				fd.setCustomerDetails(customerDetails);
			}
		}

		// set finAssetValue = FinCurrAssetValue when there is no maxDisbCheck
		FinanceType finType = schdData.getFinanceType();
		// This is not applicable for Over Draft
		if (!schdData.getFinanceMain().getProductCategory().equals(FinanceConstants.PRODUCT_ODFACILITY)) {
			if (!finType.isAlwMaxDisbCheckReq()) {
				fm.setFinAssetValue(fm.getFinCurrAssetValue());
			}
		}

		// Setting Default TDS Type
		if (StringUtils.isBlank(fm.getTdsType()) && !PennantConstants.TDS_USER_SELECTION.equals(finType.getTdsType())) {
			fm.setTdsType(finType.getTdsType());
		}

		// VAS Details
		String entityCode = divisionDetailDAO.getEntityCodeByDivision(finType.getFinDivision(), "");
		String recordStatus = getRecordStatus(stp, moveLoanStage, fm);

		for (VASRecording vasRecording : schdData.getVasRecordingList()) {
			vasRecording.setRecordType(PennantConstants.RECORD_TYPE_NEW);
			if (!moveLoanStage) {
				vasRecording.setNewRecord(true);
			}
			vasRecording.setVersion(1);
			vasRecording.setRecordStatus(recordStatus);
			vasRecording.setVasReference(ReferenceUtil.generateVASRef());
			vasRecording.setPostingAgainst(VASConsatnts.VASAGAINST_FINANCE);
			vasRecording.setVasStatus("N");
			// workflow related
			vasRecording.setWorkflowId(fm.getWorkflowId());
			vasRecording.setRoleCode(fm.getRoleCode());
			vasRecording.setNextRoleCode(fm.getNextRoleCode());
			vasRecording.setTaskId(fm.getTaskId());
			vasRecording.setNextTaskId(fm.getNextTaskId());
			vasRecording.setEntityCode(entityCode);
			// process Extended field details
			List<ExtendedField> extendedFields = vasRecording.getExtendedDetails();
			if (extendedFields != null) {
				int seqNo = 0;
				ExtendedFieldRender exdFieldRender = new ExtendedFieldRender();
				exdFieldRender.setReference(vasRecording.getVasReference());
				exdFieldRender.setLastMntOn(new Timestamp(System.currentTimeMillis()));
				exdFieldRender.setRecordStatus(recordStatus);
				exdFieldRender.setLastMntBy(userDetails.getUserId());
				exdFieldRender.setSeqNo(++seqNo);
				if (!moveLoanStage) {
					exdFieldRender.setNewRecord(true);
				}
				exdFieldRender.setRecordType(PennantConstants.RECORD_TYPE_NEW);
				exdFieldRender.setVersion(1);
				// workflow related
				exdFieldRender.setWorkflowId(fm.getWorkflowId());
				exdFieldRender.setRoleCode(fm.getRoleCode());
				exdFieldRender.setNextRoleCode(fm.getNextRoleCode());
				exdFieldRender.setTaskId(fm.getTaskId());
				exdFieldRender.setNextTaskId(fm.getNextTaskId());
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
				exdFieldRender.setRecordStatus(recordStatus);
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
		List<FinFlagsDetail> finFlagsDetails = fd.getFinFlagsDetails();
		for (FinFlagsDetail flagDetail : finFlagsDetails) {
			flagDetail.setReference(fm.getFinReference());
			flagDetail.setRecordType(PennantConstants.RECORD_TYPE_NEW);
			flagDetail.setModuleName(FinanceConstants.MODULE_NAME);
			if (!moveLoanStage) {
				flagDetail.setNewRecord(true);
			}
			flagDetail.setVersion(1);
			flagDetail.setLastMntBy(userDetails.getUserId());
			flagDetail.setLastMntOn(new Timestamp(System.currentTimeMillis()));
			flagDetail.setRecordStatus(recordStatus);
			flagDetail.setUserDetails(fm.getUserDetails());
			// workflow related
			flagDetail.setWorkflowId(fm.getWorkflowId());
			flagDetail.setRoleCode(fm.getRoleCode());
			flagDetail.setNextRoleCode(fm.getNextRoleCode());
			flagDetail.setTaskId(fm.getTaskId());
			flagDetail.setNextTaskId(fm.getNextTaskId());
		}

		// process mandate details
		doProcessMandate(fd, moveLoanStage);

		// process covenant details
		List<FinCovenantType> covenantTypeList = fd.getCovenantTypeList();

		for (FinCovenantType finCovenantType : covenantTypeList) {
			finCovenantType.setFinReference(fm.getFinReference());
			finCovenantType.setRecordType(PennantConstants.RECORD_TYPE_NEW);
			if (!moveLoanStage) {
				finCovenantType.setNewRecord(true);
			}
			finCovenantType.setLastMntBy(fm.getLastMntBy());
			finCovenantType.setLastMntOn(fm.getLastMntOn());
			finCovenantType.setRecordStatus(recordStatus);
			finCovenantType.setRoleCode(fm.getRoleCode());
			finCovenantType.setNextRoleCode(fm.getNextRoleCode());
			finCovenantType.setTaskId(fm.getTaskId());
			finCovenantType.setNextTaskId(fm.getNextTaskId());
			finCovenantType.setUserDetails(userDetails);
			finCovenantType.setVersion(1);
		}

		// co-applicant details
		for (JointAccountDetail jad : fd.getJointAccountDetailList()) {
			jad.setFinID(fm.getFinID());
			jad.setFinReference(fm.getFinReference());
			jad.setRecordType(PennantConstants.RECORD_TYPE_NEW);
			if (!moveLoanStage) {
				jad.setNewRecord(true);
			}
			jad.setLastMntBy(userDetails.getUserId());
			jad.setLastMntOn(new Timestamp(System.currentTimeMillis()));
			jad.setRecordStatus(recordStatus);
			jad.setUserDetails(fm.getUserDetails());
			jad.setVersion(1);
			// workflow
			jad.setWorkflowId(fm.getWorkflowId());
			jad.setRoleCode(fm.getRoleCode());
			jad.setNextRoleCode(fm.getNextRoleCode());
			jad.setTaskId(fm.getTaskId());
			jad.setNextTaskId(fm.getNextTaskId());

			Customer coApplicant = customerDetailsService.getCustomerByCIF(jad.getCustCIF());
			if (coApplicant != null) {
				jad.setCustID(coApplicant.getCustID());
			}
		}

		// guarantor details
		for (GuarantorDetail grd : fd.getGurantorsDetailList()) {
			if (grd.isBankCustomer()) {
				List<CustomerAddres> address = customerAddresService.getApprovedCustomerAddresById(grd.getCustID());
				if (address != null && !address.isEmpty()) {
					CustomerAddres customerAddress = address.get(0);
					grd.setAddrCity(customerAddress.getCustAddrCity());
					grd.setAddrCountry(customerAddress.getCustAddrCountry());
					grd.setAddrHNbr(customerAddress.getCustAddrHNbr());
					grd.setAddrLine1(customerAddress.getCustAddrLine1());
					grd.setAddrLine2(customerAddress.getCustAddrLine2());
					grd.setAddrProvince(customerAddress.getCustAddrProvince());
					grd.setAddrStreet(customerAddress.getCustAddrStreet());
					grd.setAddrZIP(customerAddress.getCustAddrZIP());
					grd.setPOBox(customerAddress.getCustPOBox());
					grd.setFlatNbr(customerAddress.getCustFlatNbr());
				}
			}
			grd.setFinReference(fm.getFinReference());
			grd.setRecordType(PennantConstants.RECORD_TYPE_NEW);

			if (!moveLoanStage) {
				grd.setNewRecord(true);
			}

			grd.setLastMntBy(userDetails.getUserId());
			grd.setLastMntOn(new Timestamp(System.currentTimeMillis()));
			grd.setRecordStatus(recordStatus);
			grd.setUserDetails(fm.getUserDetails());
			grd.setVersion(1);
			// workflow
			grd.setWorkflowId(fm.getWorkflowId());
			grd.setRoleCode(fm.getRoleCode());
			grd.setNextRoleCode(fm.getNextRoleCode());
			grd.setTaskId(fm.getTaskId());
			grd.setNextTaskId(fm.getNextTaskId());
		}

		// document details
		for (DocumentDetails document : fd.getDocumentDetailsList()) {
			if (!moveLoanStage) {
				document.setNewRecord(true);
			}
			document.setRecordType(PennantConstants.RECORD_TYPE_NEW);
			document.setDocModule(FinanceConstants.MODULE_NAME);
			document.setUserDetails(fm.getUserDetails());
			document.setVersion(1);
			document.setRecordStatus(recordStatus);
			// workflow relates
			document.setWorkflowId(fm.getWorkflowId());
			document.setRoleCode(fm.getRoleCode());
			document.setNextRoleCode(fm.getNextRoleCode());
			document.setTaskId(fm.getTaskId());
			document.setNextTaskId(fm.getNextTaskId());

		}

		// CollateralAssignment details
		for (CollateralAssignment ca : fd.getCollateralAssignmentList()) {

			if (StringUtils.isNotBlank(ca.getCollateralRef())) {
				CollateralSetup collateralSetup = collateralSetupService
						.getApprovedCollateralSetupById(ca.getCollateralRef());
				if (collateralSetup != null) {
					ca.setCollateralValue(collateralSetup.getCollateralValue());
				}
				if (!moveLoanStage) {
					ca.setNewRecord(true);
				}
				ca.setRecordType(PennantConstants.RECORD_TYPE_NEW);
				ca.setModule(FinanceConstants.MODULE_NAME);
				ca.setUserDetails(fm.getUserDetails());
				ca.setLastMntBy(userDetails.getUserId());
				ca.setLastMntOn(new Timestamp(System.currentTimeMillis()));
				ca.setRecordStatus(recordStatus);
				ca.setVersion(1);
				// workflow relates
				ca.setWorkflowId(fm.getWorkflowId());
				ca.setRoleCode(fm.getRoleCode());
				ca.setNextRoleCode(fm.getNextRoleCode());
				ca.setTaskId(fm.getTaskId());
				ca.setNextTaskId(fm.getNextTaskId());
			}

		}
		if (CollectionUtils.isNotEmpty(fd.getCollaterals())) {
			BigDecimal curAssignValue = BigDecimal.ZERO;
			BigDecimal totalAvailAssignValue = BigDecimal.ZERO;
			Boolean flag = false;
			for (CollateralAssignment detail : fd.getCollateralAssignmentList()) {
				for (CollateralSetup collsetup : fd.getCollaterals()) {
					if (StringUtils.isNotBlank(detail.getAssignmentReference())
							&& StringUtils.isNotBlank(collsetup.getAssignmentReference())) {
						if (StringUtils.equals(detail.getAssignmentReference(), collsetup.getAssignmentReference())) {
							processCollateralsetupDetails(userDetails, stp, fm, customerDetails, detail, collsetup,
									moveLoanStage);
							flag = true;
							curAssignValue = curAssignValue.add(collsetup.getBankValuation()
									.multiply(detail.getAssignPerc() == null ? BigDecimal.ZERO : detail.getAssignPerc())
									.divide(new BigDecimal(100), 0, RoundingMode.HALF_DOWN));
							totalAvailAssignValue = totalAvailAssignValue.add(collsetup.getBankValuation());
						}
					}
				}
			}
			if (!flag) {
				fd.setCollaterals(null);
			}
			// Collateral coverage will be calculated based on the flag
			// "Partially Secured?â€� defined loan type.
			if (!schdData.getFinanceType().isPartiallySecured() && flag) {
				if (curAssignValue.compareTo(fm.getFinAmount()) < 0) {
					String[] valueParm = new String[2];
					valueParm[0] = "Collateral available assign value(" + String.valueOf(curAssignValue) + ")";
					valueParm[1] = "current assign value(" + fm.getFinAmount() + ")";
					schdData.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetail("65012", valueParm)));
				}

				if (PennantConstants.COLLATERAL_LTV_CHECK_FINAMT.equals(schdData.getFinanceType().getFinLTVCheck())) {
					if (totalAvailAssignValue.compareTo(fm.getFinAssetValue()) < 0) {
						String[] valueParm = new String[2];
						valueParm[0] = "Available assign value(" + String.valueOf(totalAvailAssignValue) + ")";
						valueParm[1] = "loan amount(" + String.valueOf(fm.getFinAssetValue()) + ")";
						schdData.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetail("65012", valueParm)));
					}
				} else {
					if (totalAvailAssignValue.compareTo(fm.getFinAmount()) < 0) {
						String[] valueParm = new String[2];
						valueParm[0] = "Available assign value(" + String.valueOf(totalAvailAssignValue) + ")";
						valueParm[1] = "loan amount(" + String.valueOf(fm.getFinAmount()) + ")";
						schdData.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetail("65012", valueParm)));
					}
				}
			}
		}

		if (fd.getFinanceTaxDetail() != null) {
			FinanceTaxDetail ftd = fd.getFinanceTaxDetail();
			ftd.setFinID(fm.getFinID());
			ftd.setFinReference(fm.getFinReference());
			ftd.setRecordType(PennantConstants.RECORD_TYPE_NEW);
			if (!moveLoanStage) {
				ftd.setNewRecord(true);
			}
			ftd.setLastMntBy(userDetails.getUserId());
			ftd.setLastMntOn(new Timestamp(System.currentTimeMillis()));
			ftd.setRecordStatus(recordStatus);
			ftd.setUserDetails(fm.getUserDetails());
			ftd.setVersion(1);
			ftd.setTaskId(fm.getTaskId());
			ftd.setNextTaskId(fm.getNextTaskId());
			ftd.setRoleCode(fm.getRoleCode());
			ftd.setNextRoleCode(fm.getNextRoleCode());
			ftd.setWorkflowId(fm.getWorkflowId());
		}

		// execute fee charges
		String finEvent = "";
		boolean enquiry = true;
		if (schdData != null && CollectionUtils.isNotEmpty(schdData.getFinFeeDetailList())) {
			enquiry = false;
		}
		if (!moveLoanStage) {
			executeFeeCharges(fd, finEvent, enquiry);
		}

		if (schdData.getFinFeeDetailList() != null) {
			for (FinFeeDetail feeDetail : schdData.getFinFeeDetailList()) {
				feeDetail.setRecordType(PennantConstants.RECORD_TYPE_NEW);
				if (!stp || fm.isQuickDisb())
					feeDetail.setRecordStatus("");
				feeDetail.setRcdVisible(false);
				feeDetail.setVersion(1);
				feeDetail.setWorkflowId(fm.getWorkflowId());
			}
		}

		// Set VAS reference as feeCode for VAS related fees
		for (FinFeeDetail feeDetail : schdData.getFinFeeDetailList()) {
			for (VASRecording vasRecording : schdData.getVasRecordingList()) {
				String feeTypeCode = feeDetail.getFeeTypeCode();
				String productCode = vasRecording.getProductCode();
				// Extracting feetypecode and productcode by excluding('{' and '}')
				feeTypeCode = extractFeeCode(feeTypeCode);
				productCode = extractFeeCode(productCode);

				if (AccountingEvent.VAS_FEE.equals(feeDetail.getFinEvent())
						&& StringUtils.equals(feeTypeCode, productCode)) {
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
					feeDetail.setWorkflowId(fm.getWorkflowId());
					feeDetail.setOriginationFee(true);
					feeDetail.setFeeTypeID(0);
					feeDetail.setFeeSeq(0);
					feeDetail.setFeeOrder(0);

				}
			}
		}

		if (!loanWithWIF
				&& !schdData.getFinanceMain().getProductCategory().equals(FinanceConstants.PRODUCT_ODFACILITY)) {
			if (!approve && !moveLoanStage) {
				FinanceDisbursement disbursementDetails = new FinanceDisbursement();
				disbursementDetails.setDisbDate(fm.getFinStartDate());
				disbursementDetails.setDisbAmount(fm.getFinAmount());
				disbursementDetails.setVersion(1);
				disbursementDetails.setDisbSeq(1);
				disbursementDetails.setDisbReqDate(SysParamUtil.getAppDate());
				disbursementDetails.setFeeChargeAmt(fm.getFeeChargeAmt());
				schdData.getDisbursementDetails().add(disbursementDetails);
			}
		}
		// Step Policy Details
		if (fm.isStepFinance() && PennantConstants.STEPPING_CALC_PERC.equals(fm.getCalcOfSteps())) {
			fm.setRpyStps(true);
			String stepPolicyCode = fm.getStepPolicy();
			if (StringUtils.isNotBlank(stepPolicyCode)) {
				List<StepPolicyDetail> stepPolicyList = stepPolicyDetailDAO.getStepPolicyDetailListByID(stepPolicyCode,
						"_AView");

				// reset step policy details
				schdData.resetStepPolicyDetails(stepPolicyList);

				schdData.getFinanceMain().setStepFinance(true);
				schdData.getFinanceMain().setStepPolicy(stepPolicyCode);

				// fetch stepHeader details
				StepPolicyHeader header = stepPolicyHeaderDAO.getStepPolicyHeaderByID(stepPolicyCode, "");
				if (header != null) {
					schdData.getFinanceMain().setStepType(header.getStepType());
				}

				List<FinanceStepPolicyDetail> finStepDetails = schdData.getStepPolicyDetails();

				// method for prepare step installments
				prepareStepInstallements(finStepDetails, fm.getNumberOfTerms());

			} else {
				List<FinanceStepPolicyDetail> finStepDetails = schdData.getStepPolicyDetails();
				Collections.sort(finStepDetails, new Comparator<FinanceStepPolicyDetail>() {
					@Override
					public int compare(FinanceStepPolicyDetail b1, FinanceStepPolicyDetail b2) {
						return (new Integer(b1.getStepNo()).compareTo(new Integer(b2.getStepNo())));
					}
				});

				// method for prepare step installments
				prepareStepInstallements(finStepDetails, fm.getNumberOfTerms());
			}
		} else if (fm.isStepFinance() && PennantConstants.STEPPING_CALC_AMT.equals(finType.getCalcOfSteps())) {

			List<FinanceStepPolicyDetail> finStepPoliciesList = schdData.getStepPolicyDetails();
			List<FinanceStepPolicyDetail> graceSpdList = new ArrayList<>();
			List<FinanceStepPolicyDetail> rpySpdList = new ArrayList<>();
			fm.setRpyStps(false);
			fm.setGrcStps(false);
			for (FinanceStepPolicyDetail spd : finStepPoliciesList) {
				if (PennantConstants.STEP_SPECIFIER_REG_EMI.equals(spd.getStepSpecifier())) {
					rpySpdList.add(spd);
					fm.setRpyStps(true);
				} else if (PennantConstants.STEP_SPECIFIER_GRACE.equals(spd.getStepSpecifier())) {
					graceSpdList.add(spd);
					fm.setGrcStps(true);
				}
			}
			prepareStepInstallements(rpySpdList, fm.getNumberOfTerms());
			prepareStepInstallements(graceSpdList, fm.getGraceTerms());
			schdData.setStepPolicyDetails(finStepPoliciesList, true);
		}

		// pslDetails defaults
		PSLDetail pslDetail = fd.getPslDetail();
		if (pslDetail != null) {
			if (!moveLoanStage) {
				pslDetail.setNewRecord(true);
				pslDetail.setRecordType(PennantConstants.RECORD_TYPE_NEW);
				pslDetail.setVersion(1);
				pslDetail.setLastMntBy(userDetails.getUserId());
				pslDetail.setLastMntOn(new Timestamp(System.currentTimeMillis()));
				pslDetail.setRecordStatus(PennantConstants.RCD_STATUS_APPROVED);
				pslDetail.setFinID(fm.getFinID());
				pslDetail.setFinReference(fm.getFinReference());
			}

			if (!stp) {
				pslDetail.setRecordStatus(
						moveLoanStage ? fm.getRecordStatus() : getRecordStatus(fm.isQuickDisb(), fd.isStp()));
				pslDetail.setUserDetails(fm.getUserDetails());

				pslDetail.setWorkflowId(fm.getWorkflowId());
				pslDetail.setRoleCode(fm.getRoleCode());
				pslDetail.setNextRoleCode(fm.getNextRoleCode());
				pslDetail.setTaskId(fm.getTaskId());
				pslDetail.setNextTaskId(fm.getNextTaskId());
				pslDetail.setFinID(fm.getFinID());
				pslDetail.setFinReference(fm.getFinReference());
			}
		}

		// process Extended field details
		// Get the ExtendedFieldHeader for given module and subModule
		// ### 02-05-2018-Start- story #334 Extended fields for loan servicing
		ExtendedFieldHeader extendedFieldHeader = extendedFieldHeaderDAO.getExtendedFieldHeaderByModuleName(
				ExtendedFieldConstants.MODULE_LOAN, fm.getFinCategory(), FinServiceEvent.ORG, "");
		// ### 02-05-2018-END

		fd.setExtendedFieldHeader(extendedFieldHeader);

		List<ExtendedField> extendedFields = fd.getExtendedDetails();
		if (extendedFieldHeader != null) {
			int seqNo = 0;
			ExtendedFieldRender exdFieldRender = new ExtendedFieldRender();
			exdFieldRender.setReference(fm.getFinReference());
			exdFieldRender.setLastMntOn(new Timestamp(System.currentTimeMillis()));
			if (!moveLoanStage) {
				exdFieldRender.setRecordStatus(PennantConstants.RCD_STATUS_APPROVED);
			} else {
				exdFieldRender.setRecordStatus(fm.getRecordStatus());
			}
			exdFieldRender.setLastMntBy(userDetails.getUserId());
			exdFieldRender.setSeqNo(++seqNo);
			if (!moveLoanStage) {
				exdFieldRender.setNewRecord(true);
			}
			exdFieldRender.setRecordType(PennantConstants.RECORD_TYPE_NEW);
			exdFieldRender.setVersion(1);
			exdFieldRender.setTypeCode(fd.getExtendedFieldHeader().getSubModuleName());
			exdFieldRender.setWorkflowId(fm.getWorkflowId());

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

			fd.setExtendedFieldRender(exdFieldRender);
		}

		// set's the default chequeHeader to the financeDetail if chequeCapture
		// is required.
		if (InstrumentType.isPDC(fm.getFinRepayMethod()) || finType.isChequeCaptureReq()) {
			doSetDefaultChequeHeader(fd, moveLoanStage);
		}

		if (fd.getChequeHeader() != null) {
			prepareCheques(fd, userDetails, stp, moveLoanStage);
		}

		doProcessOCRDetails(fd, userDetails);

		// Covenants
		if (CollectionUtils.isNotEmpty(fd.getCovenants())) {
			Date appDate = SysParamUtil.getAppDate();
			for (Covenant detail : fd.getCovenants()) {
				detail.setRecordType(PennantConstants.RECORD_TYPE_NEW);
				if (!moveLoanStage) {
					detail.setNewRecord(true);
				}
				detail.setWorkflowId(fm.getWorkflowId());
				detail.setRoleCode(fm.getRoleCode());
				detail.setNextRoleCode(fm.getNextRoleCode());
				detail.setTaskId(fm.getTaskId());
				detail.setNextTaskId(fm.getNextTaskId());
				detail.setLastMntBy(userDetails.getUserId());
				detail.setRecordStatus(recordStatus);
				detail.setUserDetails(fm.getUserDetails());
				detail.setVersion(1);
				detail.setModule(APIConstants.COVENANT_MODULE_NAME);
				// setting the CovenantTypeId based on the category and the Code
				CovenantType covenantType = covenantTypeDAO.getCovenantType(detail.getCovenantTypeId(), "");
				{
					detail.setCovenantTypeId(covenantType.getId());
				}

				if (CollectionUtils.isNotEmpty(detail.getCovenantDocuments())) {
					detail.setDocumentReceivedDate(appDate);
					for (CovenantDocument covenantDocument : detail.getCovenantDocuments()) {
						DocumentDetails documentDetails = new DocumentDetails(FinanceConstants.MODULE_NAME, "",
								covenantDocument.getDoctype(), covenantDocument.getDocName(),
								covenantDocument.getDocImage());

						documentDetails.setRecordType(PennantConstants.RECORD_TYPE_NEW);
						covenantDocument.setRecordType(PennantConstants.RECORD_TYPE_NEW);

						if (!moveLoanStage) {
							covenantDocument.setNewRecord(true);
							documentDetails.setNewRecord(true);
						}
						// DocumentType docType =
						// documentTypeService.getDocumentTypeById(covenantDocument.getDoctype());
						covenantDocument.setLastMntBy(userDetails.getUserId());
						covenantDocument.setLastMntOn(new Timestamp(System.currentTimeMillis()));
						covenantDocument.setRecordStatus(recordStatus);
						covenantDocument.setUserDetails(fm.getUserDetails());
						covenantDocument.setVersion(1);
						covenantDocument.setTaskId(fm.getTaskId());
						covenantDocument.setNextTaskId(fm.getNextTaskId());
						covenantDocument.setRoleCode(fm.getRoleCode());
						covenantDocument.setNextRoleCode(fm.getNextRoleCode());
						covenantDocument.setWorkflowId(fm.getWorkflowId());
						covenantDocument.setDocName(covenantDocument.getDocName());
						covenantDocument.setCustId(custID);
						covenantDocument.setDocumentReceivedDate(appDate);
						documentDetails.setDocReceivedDate(appDate);
						documentDetails.setLastMntBy(userDetails.getUserId());
						documentDetails.setLastMntOn(new Timestamp(System.currentTimeMillis()));
						documentDetails.setRecordStatus(recordStatus);
						documentDetails.setUserDetails(fm.getUserDetails());
						documentDetails.setVersion(1);
						documentDetails.setTaskId(fm.getTaskId());
						documentDetails.setNextTaskId(fm.getNextTaskId());
						documentDetails.setRoleCode(fm.getRoleCode());
						documentDetails.setNextRoleCode(fm.getNextRoleCode());
						documentDetails.setWorkflowId(fm.getWorkflowId());
						documentDetails.setDocModule(FinanceConstants.MODULE_NAME);
						documentDetails.setDocName(covenantDocument.getDocName());
						documentDetails.setReferenceId(fm.getFinReference());
						documentDetails.setDocReceived(true);
						documentDetails.setFinReference(fm.getFinReference());
						documentDetails.setCustId(custID);
						covenantDocument.setCovenantType(detail.getCovenantType());
						covenantDocument.setDocumentDetail(documentDetails);
						detail.getDocumentDetails().add(documentDetails);
					}
				}
			}
		}
		logger.debug(Literal.LEAVING);
	}

	private String getRecordStatus(boolean stp, boolean moveLoanStage, FinanceMain fm) {
		return moveLoanStage ? fm.getRecordStatus() : getRecordStatus(fm.isQuickDisb(), stp);
	}

	private String extractFeeCode(String feeTypeCode) {
		if (StringUtils.startsWith(feeTypeCode, "{") && StringUtils.endsWith(feeTypeCode, "}")) {
			feeTypeCode = feeTypeCode.replace("{", "");
			feeTypeCode = feeTypeCode.replace("}", "");
		}

		return feeTypeCode;
	}

	private void doProcessOCRDetails(FinanceDetail financeDetail, LoggedInUser userDetails) {
		if (financeDetail != null) {
			FinOCRHeader finOCRHeader = financeDetail.getFinOCRHeader();
			FinanceMain financeMain = financeDetail.getFinScheduleData().getFinanceMain();
			if (finOCRHeader != null && financeMain != null) {
				// do set workflow details
				finOCRHeader.setFinID(financeMain.getFinID());
				finOCRHeader.setFinReference(financeMain.getFinReference());
				finOCRHeader.setLastMntBy(userDetails.getUserId());
				finOCRHeader.setLastMntOn(new Timestamp(System.currentTimeMillis()));
				finOCRHeader.setUserDetails(userDetails);
				finOCRHeader.setWorkflowId(financeMain.getWorkflowId());
				finOCRHeader.setRoleCode(financeMain.getRoleCode());
				finOCRHeader.setNextRoleCode(financeMain.getNextRoleCode());
				finOCRHeader.setTaskId(financeMain.getTaskId());
				finOCRHeader.setNextTaskId(financeMain.getNextTaskId());
				finOCRHeader.setNewRecord(true);
				finOCRHeader.setRecordType(PennantConstants.RECORD_TYPE_NEW);
				finOCRHeader.setVersion(finOCRHeader.getVersion() + 1);
				financeDetail.setFinOCRHeader(finOCRHeader);
				// OCR Definition
				if (CollectionUtils.isNotEmpty(finOCRHeader.getOcrDetailList())) {
					for (FinOCRDetail finOCRDetail : finOCRHeader.getOcrDetailList()) {
						finOCRDetail.setNewRecord(true);
						finOCRDetail.setRecordType(PennantConstants.RCD_ADD);
						finOCRDetail.setLastMntBy(userDetails.getUserId());
						finOCRDetail.setLastMntOn(new Timestamp(System.currentTimeMillis()));
						finOCRDetail.setUserDetails(userDetails);
						finOCRDetail.setWorkflowId(financeMain.getWorkflowId());
						finOCRDetail.setRoleCode(financeMain.getRoleCode());
						finOCRDetail.setNextRoleCode(financeMain.getNextRoleCode());
						finOCRDetail.setTaskId(financeMain.getTaskId());
						finOCRDetail.setNextTaskId(financeMain.getNextTaskId());
						finOCRDetail.setVersion(finOCRDetail.getVersion() + 1);
					}
				}
				// OCR Capture Details
				if (CollectionUtils.isNotEmpty(finOCRHeader.getFinOCRCapturesList())) {
					for (FinOCRCapture finOCRCapture : finOCRHeader.getFinOCRCapturesList()) {
						finOCRCapture.setNewRecord(true);
						finOCRCapture.setRecordType(PennantConstants.RCD_ADD);
						finOCRCapture.setFinID(financeMain.getFinID());
						finOCRCapture.setFinReference(financeMain.getFinReference());
						finOCRCapture.setLastMntBy(userDetails.getUserId());
						finOCRCapture.setLastMntOn(new Timestamp(System.currentTimeMillis()));
						finOCRCapture.setUserDetails(userDetails);
						finOCRCapture.setWorkflowId(financeMain.getWorkflowId());
						finOCRCapture.setRoleCode(financeMain.getRoleCode());
						finOCRCapture.setNextRoleCode(financeMain.getNextRoleCode());
						finOCRCapture.setTaskId(financeMain.getTaskId());
						finOCRCapture.setNextTaskId(financeMain.getNextTaskId());
						finOCRCapture.setVersion(finOCRCapture.getVersion() + 1);
					}
				}
			}
		}
	}

	private void processCollateralsetupDetails(LoggedInUser userDetails, boolean stp, FinanceMain financeMain,
			CustomerDetails customerDetails, CollateralAssignment detail, CollateralSetup colSetup,
			boolean moveLoanStage) {
		// collateral setup defaulting
		colSetup.setUserDetails(financeMain.getUserDetails());
		colSetup.setSourceId(APIConstants.FINSOURCE_ID_API);
		colSetup.setRecordStatus(getRecordStatus(stp, moveLoanStage, financeMain));
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
				thirdPartyColl.setRecordStatus(getRecordStatus(stp, moveLoanStage, financeMain));
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
				coOwnerDetail.setRecordStatus(getRecordStatus(stp, moveLoanStage, financeMain));
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
				exdFieldRender.setRecordStatus(getRecordStatus(stp, moveLoanStage, financeMain));
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
					ruleResult = RuleExecutionUtil.executeRule(collateralStructure.getSQLRule(), declaredMap,
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
		detail.setRecordStatus(getRecordStatus(stp, moveLoanStage, financeMain));
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

		List<Mandate> mdt = new ArrayList<>();

		Mandate normalMandate = financeDetail.getMandate();
		if (normalMandate != null) {
			normalMandate.setSecurityMandate(false);
			mdt.add(normalMandate);
		}

		Mandate securityMandate = financeDetail.getSecurityMandate();
		if (securityMandate != null) {
			securityMandate.setSecurityMandate(true);
			mdt.add(securityMandate);
		}

		for (Mandate mandate : mdt) {
			String mandateType = mandate.getMandateType();

			switch (InstrumentType.valueOf(mandateType)) {
			case ECS:
			case DD:
			case NACH:
			case EMANDATE:
			case SI:
				String ifsc = mandate.getIFSC();
				String micr = mandate.getMICR();
				String bankCode = mandate.getBankCode();
				String branchCode = mandate.getBranchCode();

				BankBranch bankBranch = bankBranchService.getBankBranch(ifsc, micr, bankCode, branchCode);

				if (bankBranch.getError() != null) {
					financeDetail.getFinScheduleData().getErrorDetails().add(bankBranch.getError());
				}

				mandate.setBankCode(bankBranch.getBankCode());
				mandate.setBranchCode(bankBranch.getBranchCode());
				mandate.setBankBranchID(bankBranch.getBankBranchID());
				mandate.setIFSC(bankBranch.getIFSC());
				mandate.setBankBranchID(bankBranch.getBankBranchID());
				break;
			case DAS:
				break;
			default:
				break;

			}

			if (!moveLoanStage) {
				mandate.setNewRecord(true);
			}
			mandate.setRecordType(PennantConstants.RECORD_TYPE_NEW);
			mandate.setVersion(1);

			mandate.setLastMntBy(userDetails.getUserId());
			mandate.setLastMntOn(new Timestamp(System.currentTimeMillis()));
			mandate.setRecordStatus(moveLoanStage ? financeMain.getRecordStatus()
					: getRecordStatus(financeMain.isQuickDisb(), financeDetail.isStp()));
			mandate.setUserDetails(financeMain.getUserDetails());
			mandate.setMandateCcy(SysParamUtil.getAppCurrency());
			mandate.setEntityCode(entityCode);

			mandate.setCustCIF(financeMain.getLovDescCustCIF());
			mandate.setCustID(financeMain.getCustID());
			mandate.setActive(true);
			mandate.setInputDate(SysParamUtil.getAppDate());
		}
	}

	/**
	 * @param fd
	 * @param finScheduleData
	 */
	private void validateDisbInstAmount(FinanceDetail fd) {
		FinScheduleData schdData = fd.getFinScheduleData();

		if (fd.getAdvancePaymentsList() == null) {
			return;
		}

		for (FinAdvancePayments advPayments : fd.getAdvancePaymentsList()) {
			advPayments.setDisbSeq(schdData.getDisbursementDetails().size());
		}

		List<ErrorDetail> errors = finAdvancePaymentsService.validateFinAdvPayments(fd, true);
		for (ErrorDetail error : errors) {
			schdData.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetail(error.getCode(), error.getParameters())));
		}
	}

	/**
	 * Method for Creating New Disbursement Instruction for the Transaction
	 * 
	 * Default Payment Type : IFT
	 * 
	 * @param fd
	 * @param moveLoanStage
	 * @return
	 */
	private FinAdvancePayments createNewDisbInst(FinanceDetail fd, boolean moveLoanStage) {
		FinScheduleData schdData = fd.getFinScheduleData();
		FinanceMain fm = schdData.getFinanceMain();
		LoggedInUser userDetails = fd.getUserDetails();

		long finID = fm.getFinID();
		String finReference = fm.getFinReference();

		FinAdvancePayments advPayment = new FinAdvancePayments();

		advPayment.setFinID(finID);
		advPayment.setFinReference(finReference);
		advPayment.setRecordType(PennantConstants.RECORD_TYPE_NEW);
		advPayment.setPaymentDetail(DisbursementConstants.PAYMENT_DETAIL_CUSTOMER);
		advPayment.setNewRecord(true);
		advPayment.setVersion(1);
		advPayment.setLastMntBy(userDetails.getUserId());
		advPayment.setLastMntOn(new Timestamp(System.currentTimeMillis()));

		if (fd.isStp()) {
			advPayment.setRecordStatus(PennantConstants.RCD_STATUS_APPROVED);
		}
		if (moveLoanStage) {
			advPayment.setRecordStatus(fm.getRecordStatus());
		} else if (fd.isStp()) {
			advPayment.setRecordStatus(PennantConstants.RCD_STATUS_APPROVED);
		}

		advPayment.setUserDetails(fm.getUserDetails());
		advPayment.setPaymentSeq(1);
		advPayment.setDisbSeq(1);
		advPayment.setActive(true);
		advPayment.setDisbCCy(fm.getFinCcy());
		Promotion promotion = fd.getPromotion();
		BigDecimal dbdAmount = BigDecimal.ZERO;

		if (promotion != null && promotion.isDbd()) {
			dbdAmount = fm.getFinAmount().multiply(promotion.getDbdPerc()).divide(new BigDecimal(100), 0,
					RoundingMode.HALF_DOWN);
		}

		advPayment.setAmtToBeReleased(
				fm.getFinAmount().subtract(fm.getDeductFeeDisb()).subtract(fm.getDownPayment()).subtract(dbdAmount));
		advPayment.setPaymentType(DisbursementConstants.PAYMENT_TYPE_IST);
		advPayment.setPartnerBankID(Long.valueOf(SysParamUtil.getValueAsInt("DISB_PARTNERBANK")));
		advPayment.setLLDate(fm.getFinStartDate());
		// fetch partner bank details
		PartnerBank partnerBank = partnerBankService.getApprovedPartnerBankById(advPayment.getPartnerBankID());
		if (partnerBank != null) {
			advPayment.setPartnerBankAc(partnerBank.getAccountNo());
			advPayment.setPartnerBankAcType(partnerBank.getAcType());
		}

		// workflow related
		advPayment.setWorkflowId(fm.getWorkflowId());
		advPayment.setRoleCode(fm.getRoleCode());
		advPayment.setNextRoleCode(fm.getNextRoleCode());
		advPayment.setTaskId(fm.getTaskId());
		advPayment.setNextTaskId(fm.getNextTaskId());

		return advPayment;
	}

	/**
	 * Method for process disbursement instructions and set default values
	 * 
	 * @param fd
	 * @param moveLoanStage
	 */
	private void doProcessDisbInstructions(FinanceDetail fd, boolean moveLoanStage) {
		FinScheduleData schdData = fd.getFinScheduleData();
		FinanceMain fm = schdData.getFinanceMain();
		LoggedInUser userDetails = fd.getUserDetails();
		String entityCode = fm.getEntityCode();

		List<FinAdvancePayments> advancePayments = fd.getAdvancePaymentsList();
		List<VASRecording> vasRecordingList = schdData.getVasRecordingList();

		if (ImplementationConstants.VAS_INST_ON_DISB) {
			finAdvancePaymentsService.processVasInstructions(vasRecordingList, advancePayments, entityCode);
		}

		long finID = fm.getFinID();
		String finReference = fm.getFinReference();

		int paymentSeq = 1;
		for (FinAdvancePayments advPayment : advancePayments) {

			advPayment.setFinID(finID);
			advPayment.setFinReference(finReference);
			advPayment.setRecordType(PennantConstants.RECORD_TYPE_NEW);

			if (!moveLoanStage) {
				advPayment.setNewRecord(true);
			}

			advPayment.setVersion(1);
			advPayment.setLastMntBy(userDetails.getUserId());
			advPayment.setLastMntOn(new Timestamp(System.currentTimeMillis()));

			if (fd.isStp()) {
				advPayment.setRecordStatus(PennantConstants.RCD_STATUS_APPROVED);
			}
			if (moveLoanStage) {
				advPayment.setRecordStatus(fm.getRecordStatus());
			}

			advPayment.setUserDetails(fm.getUserDetails());
			advPayment.setPaymentSeq(paymentSeq);
			advPayment.setDisbCCy(fm.getFinCcy());
			paymentSeq++;

			// workflow related
			advPayment.setWorkflowId(fm.getWorkflowId());
			advPayment.setRoleCode(fm.getRoleCode());
			advPayment.setNextRoleCode(fm.getNextRoleCode());
			advPayment.setTaskId(fm.getTaskId());
			advPayment.setNextTaskId(fm.getNextTaskId());

			String paymentType = advPayment.getPaymentType();
			if (DisbursementConstants.PAYMENT_TYPE_IMPS.equals(paymentType)
					|| DisbursementConstants.PAYMENT_TYPE_NEFT.equals(paymentType)
					|| DisbursementConstants.PAYMENT_TYPE_RTGS.equals(paymentType)
					|| DisbursementConstants.PAYMENT_TYPE_IFT.equals(paymentType)) {

				BankBranch bankBranch = new BankBranch();
				String ifsc = advPayment.getiFSC();
				String micr = advPayment.getMicr();
				if (StringUtils.isNotBlank(advPayment.getBranchBankCode())
						&& StringUtils.isNotBlank(advPayment.getBranchCode())) {
					bankBranch = bankBranchService.getBankBrachByCode(advPayment.getBranchBankCode(),
							advPayment.getBranchCode());
				} else if (ifsc != null && !ifsc.isEmpty() && micr != null && !micr.isEmpty()) {
					bankBranch = bankBranchService.getBankBranchByIFSCMICR(ifsc, micr);
					if (bankBranch == null) {
						String[] valueParm = new String[2];
						valueParm[0] = ifsc;
						valueParm[1] = micr;
						schdData.getErrorDetails().add(ErrorUtil.getErrorDetail(new ErrorDetail("90703", valueParm)));
					}
				} else if (StringUtils.isNotBlank(ifsc)) {
					if (bankBranchService.getBankBranchCountByIFSC(ifsc, "") > 1) {
						String[] valueParm = new String[1];
						valueParm[0] = ifsc;
						schdData.getErrorDetails().add(ErrorUtil.getErrorDetail(new ErrorDetail("90702", valueParm)));
					} else {
						bankBranch = bankBranchService.getBankBranchByIFSC(ifsc);
					}
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

	private void executeFeeCharges(FinanceDetail fd, String eventCode, boolean enquiry) throws AppException {
		FinScheduleData schData = fd.getFinScheduleData();
		if (CollectionUtils.isEmpty(schData.getFinFeeDetailList())) {
			if (StringUtils.isBlank(eventCode)) {
				eventCode = PennantApplicationUtil.getEventCode(schData.getFinanceMain().getFinStartDate());
			}
			feeDetailService.doProcessFeesForInquiry(fd, eventCode, null, enquiry);
		} else {
			feeDetailService.doExecuteFeeCharges(fd, eventCode, null, enquiry);
		}
		if (fd.isStp()) {
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

		response.setJointAccountDetailList(null);
		response.setGurantorsDetailList(null);
		response.setDocumentDetailsList(null);
		response.setFinanceCollaterals(null);

		logger.debug(Literal.LEAVING);

		return response;
	}

	public FinanceDetail getFinanceDetails(long finID) {
		logger.debug(Literal.ENTERING);

		FinanceDetail fd = null;

		try {
			fd = financeDetailService.getFinanceDetailById(finID, false, "", false, FinServiceEvent.ORG, "");

			if (fd != null) {
				FinScheduleData schdData = fd.getFinScheduleData();
				FinanceMain fm = schdData.getFinanceMain();
				String finReference = fm.getFinReference();
				String finCategory = fm.getFinCategory();

				List<ExtendedField> extData = extendedFieldDetailsService.getExtndedFieldDetails(
						ExtendedFieldConstants.MODULE_LOAN, finCategory, FinServiceEvent.ORG, finReference);
				fd.setExtendedDetails(extData);
				fd.setReturnStatus(APIErrorHandlerService.getSuccessStatus());
			} else {
				fd = new FinanceDetail();
				fd.setReturnStatus(APIErrorHandlerService.getFailedStatus());
			}
		} catch (Exception e) {
			logger.error(Literal.EXCEPTION, e);
			APIErrorHandlerService.logUnhandledException(e);
			fd = new FinanceDetail();
			fd.setReturnStatus(APIErrorHandlerService.getFailedStatus("API006", "Test"));
			return fd;
		}

		logger.debug(Literal.LEAVING);
		return fd;
	}

	public WSReturnStatus doApproveLoan(FinanceDetail fd) {
		logger.debug(Literal.ENTERING);

		try {
			FinScheduleData schdData = fd.getFinScheduleData();
			FinanceMain fm = schdData.getFinanceMain();

			// user language
			LoggedInUser userDetails = SessionUserDetails.getUserDetails(SessionUserDetails.getLogiedInUser());
			fm.setUserDetails(userDetails);

			String roleCode = null;
			String taskid = null;
			boolean stp = fd.isStp();
			long workFlowId = 0;

			fm.setRecordStatus(getRecordStatus(fm.isQuickDisb(), fd.isStp()));

			fm.setRecordType(PennantConstants.RECORD_TYPE_NEW);
			fm.setWorkflowId(0);
			fm.setRoleCode(roleCode);
			fm.setNextRoleCode(roleCode);
			fm.setTaskId(taskid);
			fm.setNextTaskId(getNextTaskId(taskid, fm.isQuickDisb(), stp));
			fm.setNewRecord(true);
			fm.setLastMntOn(new Timestamp(System.currentTimeMillis()));
			fm.setLastMntBy(getLastMntBy(fm.isQuickDisb(), userDetails));
			fm.setRecordStatus(getRecordStatus(fm.isQuickDisb(), stp));

			// set required mandatory values into finance details object
			doSetRequiredDetails(fd, false, userDetails, stp, true, false);
			List<DocumentDetails> documentList = documentDetailsDAO.getDocumentDetailsByRef(fm.getFinReference(),
					FinanceConstants.MODULE_NAME, FinServiceEvent.ORG, "_View");
			fd.setDocumentDetailsList(documentList);
			schdData.getFinanceMain().setFinRemarks("SUCCESS");
			fd.setStp(false);
			// set LastMntBy , LastMntOn and status fields to schedule details
			for (FinanceScheduleDetail schd : schdData.getFinanceScheduleDetails()) {
				schd.setLastMntOn(new Timestamp(System.currentTimeMillis()));
				schd.setRecordStatus(getRecordStatus(fm.isQuickDisb(), stp));
				schd.setWorkflowId(workFlowId);
				schd.setRoleCode(roleCode);
				schd.setNextRoleCode(roleCode);
				schd.setTaskId(taskid);
				schd.setNextTaskId(fm.getNextTaskId());
			}

			// Finance detail object
			fd.setUserAction("");
			fd.setExtSource(false);
			fd.setAccountingEventCode(PennantApplicationUtil.getEventCode(fm.getFinStartDate()));
			fd.setFinReference(fm.getFinReference());
			if (!StringUtils.equals(FinanceConstants.PRODUCT_ODFACILITY,
					fd.getFinScheduleData().getFinanceMain().getProductCategory())) {
				fd.setFinScheduleData(schdData);
			}

			AuditDetail auditDetail = new AuditDetail(PennantConstants.TRAN_WF, 1, null, fd);
			AuditHeader auditHeader = new AuditHeader(fd.getFinReference(), null, null, null, auditDetail,
					fm.getUserDetails(), new HashMap<String, List<ErrorDetail>>());

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

	public FinanceDetail getFinInquiryDetails(long finID) {
		logger.debug(Literal.ENTERING);

		FinanceDetail fd = null;
		Date appDate = SysParamUtil.getAppDate();
		try {

			fd = financeDetailService.getFinanceDetailById(finID, false, "", false, FinServiceEvent.ORG, "");

			if (fd == null) {
				fd = new FinanceDetail();
				doEmptyResponseObject(fd);
				fd.setReturnStatus(APIErrorHandlerService.getFailedStatus());

				return fd;
			}

			FinScheduleData schdData = fd.getFinScheduleData();
			FinanceMain fm = schdData.getFinanceMain();
			List<FinanceScheduleDetail> fsdl = schdData.getFinanceScheduleDetails();

			FinanceProfitDetail fpd = financeProfitDetailDAO.getFinProfitDetailsById(fm.getFinID());

			if (fpd == null) {
				fpd = new FinanceProfitDetail();
			}

			schdData.setFinPftDeatil(fpd);

			if (!fm.isFinIsActive()) {
				fm.setClosedDate(financeMainService.getFinClosedDate(finID));
			}

			String finReference = fm.getFinReference();
			String finCategory = fm.getFinCategory();

			Mandate mandate = fd.getMandate();
			if (mandate != null) {
				long mandateId = mandate.getMandateID();
				List<Long> financeRefeList = financeMainService.getFinReferencesByMandateId(mandateId);
				BigDecimal totEMIAmount = BigDecimal.ZERO;

				List<FinanceScheduleDetail> schedules = null;
				for (long detail : financeRefeList) {
					schedules = financeScheduleDetailDAO.getFinScheduleDetails(detail, "", false);
					if (schedules != null) {
						for (FinanceScheduleDetail schd : schedules) {
							if (appDate.compareTo(schd.getSchDate()) == -1) {
								if (!(schd.getRepayAmount().compareTo(BigDecimal.ZERO) == 0)) {
									totEMIAmount = totEMIAmount.add(schd.getRepayAmount());
									break;
								}
							}
						}
					}
				}
				mandate.setTotEMIAmount(totEMIAmount);
				if (fm.isPlanEMIHAlw()) {
					processPlanEmiDays(finID, fd);
				}
			}

			FinODPenaltyRate odPenaltyRate = schdData.getFinODPenaltyRate();
			String odChargeType = odPenaltyRate.getODChargeType();

			if (odPenaltyRate != null && (ChargeType.PERC_ONE_TIME.equals(odChargeType)
					|| ChargeType.PERC_ON_DUE_DAYS.equals(odChargeType)
					|| ChargeType.PERC_ON_EFF_DUE_DAYS.equals(odChargeType)
					|| ChargeType.PERC_ON_PD_MTH.equals(odChargeType))) {
				BigDecimal totPerc = PennantApplicationUtil.formateAmount(odPenaltyRate.getODChargeAmtOrPerc(), 2);
				odPenaltyRate.setODChargeAmtOrPerc(totPerc);
			}

			schdData.setFinODPenaltyRate(odPenaltyRate);

			prepareResponse(fd);

			FinanceSummary financeSummary = schdData.getFinanceSummary();
			Date businessDate = appDate;
			if (appDate.compareTo(fm.getMaturityDate()) >= 0) {
				businessDate = DateUtil.addDays(fm.getMaturityDate(), -1);
			}

			financeSummary.setLoanEMI(SchdUtil.getNextEMI(businessDate, fsdl));
			financeSummary.setForeClosureAmount(getForeClosureAmount(fd));
			financeSummary.setFutureInst(SchdUtil.getFutureInstalments(appDate, fsdl));

			if (fpd != null) {
				financeSummary.setFinCurODDays(fpd.getCurODDays());
			}

			List<ExtendedField> extData = extendedFieldDetailsService.getExtndedFieldDetails(
					ExtendedFieldConstants.MODULE_LOAN, finCategory, FinServiceEvent.ORG, finReference);
			fd.setExtendedDetails(extData);
			fd.setReturnStatus(APIErrorHandlerService.getSuccessStatus());
		} catch (Exception e) {
			logger.error(Literal.EXCEPTION, e);
			APIErrorHandlerService.logUnhandledException(e);
			fd = new FinanceDetail();
			doEmptyResponseObject(fd);
			fd.setReturnStatus(APIErrorHandlerService.getFailedStatus());
			return fd;
		}

		logger.debug(Literal.LEAVING);
		return fd;
	}

	private BigDecimal getForeClosureAmount(FinanceDetail fd) {
		ReceiptDTO receiptDTO = financeDetailService.prepareReceiptDTO(fd);

		return LoanClosureCalculator.computeClosureAmount(receiptDTO, true);
	}

	private void processPlanEmiDays(long finid, FinanceDetail fd) {
		List<FinPlanEmiHoliday> apiPlanEMImonths = new ArrayList<FinPlanEmiHoliday>();
		FinScheduleData schdData = fd.getFinScheduleData();
		FinanceMain fm = schdData.getFinanceMain();

		if (FinanceConstants.PLANEMIHMETHOD_FRQ.equals(fm.getPlanEMIHMethod())) {
			schdData.setPlanEMIHmonths(getFinPlanEmiHolidayDAO().getPlanEMIHMonthsByRef(finid, ""));
			if (schdData.getPlanEMIHmonths() != null) {
				for (Integer detail : schdData.getPlanEMIHmonths()) {
					FinPlanEmiHoliday finPlanEmiHoliday = new FinPlanEmiHoliday();
					finPlanEmiHoliday.setPlanEMIHMonth(detail);
					apiPlanEMImonths.add(finPlanEmiHoliday);
				}
			}
			schdData.setApiplanEMIHmonths(apiPlanEMImonths);
		} else if (StringUtils.equals(fm.getPlanEMIHMethod(), FinanceConstants.PLANEMIHMETHOD_ADHOC)) {
			schdData.setPlanEMIHDates(getFinPlanEmiHolidayDAO().getPlanEMIHDatesByRef(finid, ""));
			if (schdData.getPlanEMIHDates() != null) {
				for (Date detail : schdData.getPlanEMIHDates()) {
					FinPlanEmiHoliday finPlanEmiHoliday = new FinPlanEmiHoliday();
					finPlanEmiHoliday.setPlanEMIHDate(detail);
					apiPlanEMImonths.add(finPlanEmiHoliday);
				}
				schdData.setApiPlanEMIHDates(apiPlanEMImonths);
			}
		}
	}

	public FinanceInquiry getFinanceDetailsById(String reference, String serviceType, boolean isPending) {
		logger.debug(Literal.ENTERING);

		try {
			FinanceInquiry financeInquiry = new FinanceInquiry();
			List<FinanceMain> fmList = null;
			String[] valueParm = new String[1];

			if (StringUtils.equalsIgnoreCase(APIConstants.FINANCE_INQUIRY_CUSTOMER, serviceType)) {
				Customer customer = customerDetailsService.getCustomerByCIF(reference);
				String type = "";
				if (isPending) {
					type = "_Temp";
				}
				fmList = financeMainService.getFinanceByCustId(customer.getCustID(), type);
				valueParm[0] = "CIF :" + reference;
			} else {
				fmList = financeMainService.getFinanceByCollateralRef(reference);
				valueParm[0] = "CollateralRef :" + reference;
			}

			if (fmList.size() == 0) {
				financeInquiry.setReturnStatus(APIErrorHandlerService.getFailedStatus("90260", valueParm));
				return financeInquiry;
			}

			List<FinInquiryDetail> finance = new ArrayList<FinInquiryDetail>();

			List<FinanceScheduleDetail> schedules = null;

			for (FinanceMain fm : fmList) {
				long finID = fm.getFinID();

				if (isPending && StringUtils.equals(fm.getRecordStatus(), PennantConstants.RCD_STATUS_APPROVED)) {
					continue;
				}

				FinInquiryDetail finInquiryDetail = new FinInquiryDetail();
				BigDecimal paidTotal = BigDecimal.ZERO;
				BigDecimal schdFeePaid = BigDecimal.ZERO;
				BigDecimal schdPftPaid = BigDecimal.ZERO;
				BigDecimal schdPriPaid = BigDecimal.ZERO;
				BigDecimal principalSchd = BigDecimal.ZERO;
				BigDecimal profitSchd = BigDecimal.ZERO;
				int futureInst = 0;

				schedules = financeScheduleDetailDAO.getFinScheduleDetails(finID, "", false);
				boolean isnextRepayAmount = true;

				for (FinanceScheduleDetail schd : schedules) {
					schdFeePaid = schdFeePaid.add(schd.getSchdFeePaid());
					schdPftPaid = schdPftPaid.add(schd.getSchdPftPaid());
					schdPriPaid = schdPriPaid.add(schd.getSchdPriPaid());
					principalSchd = principalSchd.add(schd.getPrincipalSchd());
					profitSchd = profitSchd.add(schd.getProfitSchd());
					if (SysParamUtil.getAppDate().compareTo(schd.getSchDate()) == -1) {
						if (!(schd.getRepayAmount().compareTo(BigDecimal.ZERO) == 0) && isnextRepayAmount) {
							finInquiryDetail.setNextRepayAmount(schd.getRepayAmount());
							isnextRepayAmount = false;
						}
						futureInst++;
					}
				}

				finInquiryDetail.setFinReference(fm.getFinReference());
				finInquiryDetail.setFinType(fm.getFinType());
				finInquiryDetail.setProduct(fm.getLovDescFinProduct());
				finInquiryDetail.setFinCcy(fm.getFinCcy());
				finInquiryDetail.setFinAmount(fm.getFinAmount());
				finInquiryDetail.setFinAssetValue(fm.getFinAssetValue());
				finInquiryDetail.setNumberOfTerms(fm.getNumberOfTerms());
				finInquiryDetail.setFirstEmiAmount(fm.getFirstRepay());
				finInquiryDetail
						.setLoanTenor(DateUtility.getMonthsBetween(fm.getFinStartDate(), fm.getMaturityDate(), true));
				finInquiryDetail.setMaturityDate(fm.getMaturityDate());
				paidTotal = schdPriPaid.add(schdPftPaid).add(schdFeePaid);
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
				if (StringUtils.isBlank(fm.getClosingStatus())) {
					finInquiryDetail.setFinStatus(APIConstants.CLOSE_STATUS_ACTIVE);
				} else {
					finInquiryDetail.setFinStatus(fm.getClosingStatus());
				}

				// fetch co-applicant details
				List<JointAccountDetail> jointAccountDetailList = jointAccountDetailService.getJoinAccountDetail(finID,
						"_View");
				finInquiryDetail.setJointAccountDetailList(jointAccountDetailList);

				// fetch disbursement details
				List<FinanceDisbursement> disbList = financeDisbursementDAO.getFinanceDisbursementDetails(finID, "",
						false);
				BigDecimal totDisbAmt = BigDecimal.ZERO;
				BigDecimal totfeeChrgAmt = BigDecimal.ZERO;
				for (FinanceDisbursement finDisb : disbList) {
					totDisbAmt = totDisbAmt.add(finDisb.getDisbAmount());
					totfeeChrgAmt = totfeeChrgAmt.add(finDisb.getFeeChargeAmt());
				}
				BigDecimal assetValue = fm.getFinAssetValue() == null ? BigDecimal.ZERO : fm.getFinAssetValue();
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

	public WSReturnStatus updateFinance(FinanceDetail fd) {
		logger.debug(Literal.ENTERING);

		FinScheduleData schdData = fd.getFinScheduleData();
		FinanceMain fm = schdData.getFinanceMain();

		TableType tableType = TableType.MAIN_TAB;
		if (fm.isWorkflow()) {
			tableType = TableType.TEMP_TAB;
		}
		try {
			LoggedInUser userDetails = SessionUserDetails.getUserDetails(SessionUserDetails.getLogiedInUser());
			fd.setUserDetails(userDetails);
			fm.setUserDetails(userDetails);

			if (fd.getAdvancePaymentsList() != null && !fd.getAdvancePaymentsList().isEmpty()) {
				WSReturnStatus status = updateDisbursementInst(fd, tableType.getSuffix());
				if (StringUtils.isNotBlank(status.getReturnCode())) {
					return status;
				}
			}

			// Save or Update mandate details
			if (fd.getMandate() != null) {
				updateFinMandateDetails(fd, tableType.getSuffix());
			}

			// update Extended field details
			if (fd.getExtendedDetails() != null && !fd.getExtendedDetails().isEmpty()) {
				extendedFieldDetailsService.updateFinExtendedDetails(fd, tableType.getSuffix());
			}

			// save or update document details
			if (fd.getDocumentDetailsList() != null && !fd.getDocumentDetailsList().isEmpty()) {
				updatedFinanceDocuments(fd);
			}
			// save or update coApplicants details
			if (fd.getJointAccountDetailList() != null && !fd.getJointAccountDetailList().isEmpty()) {
				updatedCoApplicants(fd);
			}
		} catch (Exception e) {
			logger.error(Literal.EXCEPTION, e);
			return APIErrorHandlerService.getFailedStatus();
		}

		logger.debug(Literal.LEAVING);
		return APIErrorHandlerService.getSuccessStatus();
	}

	private void updatedFinanceDocuments(FinanceDetail fd) {
		logger.debug(Literal.ENTERING);

		FinScheduleData schdData = fd.getFinScheduleData();
		FinanceMain fm = schdData.getFinanceMain();
		String finReference = fm.getFinReference();

		DocumentDetails extDocDocument = null;
		for (DocumentDetails document : fd.getDocumentDetailsList()) {
			document.setNewRecord(true);
			document.setRecordType(PennantConstants.RECORD_TYPE_NEW);
			document.setVersion(1);
			document.setReferenceId(fm.getFinReference());
			document.setDocRefId(null);
			// set update properties if exists
			String docCategory = document.getDocCategory();
			String module = FinanceConstants.MODULE_NAME;
			String type = TableType.TEMP_TAB.getSuffix();

			extDocDocument = documentDetailsDAO.getDocumentDetails(finReference, docCategory, module, type);

			if (extDocDocument != null) {
				document.setDocId(extDocDocument.getDocId());
				document.setNewRecord(false);
				if (!PennantConstants.RECORD_TYPE_NEW.equals(extDocDocument.getRecordType())) {
					document.setRecordType(PennantConstants.RECORD_TYPE_UPD);
				}
				document.setVersion(extDocDocument.getVersion() + 1);
			}

			document.setDocModule(FinanceConstants.MODULE_NAME);
			document.setUserDetails(fm.getUserDetails());
			document.setRecordStatus(fm.getRecordStatus());
			// workflow relates
			document.setWorkflowId(fm.getWorkflowId());
			document.setRoleCode(fm.getRoleCode());
			document.setNextRoleCode(fm.getNextRoleCode());
			document.setTaskId(fm.getTaskId());
			document.setNextTaskId(fm.getNextTaskId());

			AuditHeader auditHeader = null;
			if (StringUtils.equals(document.getRecordType(), PennantConstants.RECORD_TYPE_UPD)) {
				auditHeader = getAuditHeader(document, PennantConstants.TRAN_UPD);
			} else {
				auditHeader = getAuditHeader(document, PennantConstants.TRAN_ADD);
			}
			documentService.saveOrUpdate(auditHeader);
		}

		logger.debug(Literal.LEAVING);
	}

	private void updateFinMandateDetails(FinanceDetail fd, String type) {
		logger.debug(Literal.ENTERING);

		doProcessMandate(fd, false);

		// Update mandate details if exists
		FinScheduleData schdData = fd.getFinScheduleData();
		FinanceMain fm = schdData.getFinanceMain();

		long finID = fm.getFinID();

		AuditHeader auditHeader = null;

		long extMandateId = financeMainDAO.getMandateIdByRef(finID, TableType.TEMP_TAB.getSuffix());
		if (extMandateId != Long.MIN_VALUE && extMandateId != 0) {
			fd.getMandate().setNewRecord(false);
			fd.getMandate().setRecordType(PennantConstants.RECORD_TYPE_UPD);
			fd.getMandate().setVersion(1);
			auditHeader = getAuditHeader(fd.getMandate(), PennantConstants.TRAN_UPD);
		} else {
			auditHeader = getAuditHeader(fd.getMandate(), PennantConstants.TRAN_ADD);
		}

		if (fd.getMandate() != null) {
			Mandate mandate = fd.getMandate();
			finMandateService.saveOrUpdate(fm, mandate, auditHeader, type);
		}

		if (fd.getSecurityMandate() != null) {
			Mandate mandate = fd.getSecurityMandate();
			finMandateService.saveOrUpdate(fm, mandate, auditHeader, type);
		}

		if (extMandateId == Long.MIN_VALUE || extMandateId == 0) {
			Long mandateId = fm.getMandateID();
			financeMainDAO.updateFinMandateId(mandateId, finID, type);
		}
		logger.debug(Literal.LEAVING);
	}

	private void updatedCoApplicants(FinanceDetail fd) {
		logger.debug(Literal.ENTERING);

		FinScheduleData schdData = fd.getFinScheduleData();
		FinanceMain fm = schdData.getFinanceMain();

		long finID = fm.getFinID();
		String finReference = fm.getFinReference();

		JointAccountDetail extJad = null;
		String type = TableType.TEMP_TAB.getSuffix();
		for (JointAccountDetail jad : fd.getJointAccountDetailList()) {
			String custCIF = jad.getCustCIF();

			jad.setNewRecord(true);
			jad.setRecordType(PennantConstants.RECORD_TYPE_NEW);
			jad.setVersion(1);
			jad.setFinID(finID);
			jad.setFinReference(finReference);

			extJad = jointAccountDetailService.getJointAccountDetailByRef(finID, custCIF, type);
			if (extJad != null) {
				jad.setJointAccountId(extJad.getJointAccountId());
				jad.setNewRecord(false);
				jad.setRecordType(PennantConstants.RECORD_TYPE_UPD);
				jad.setVersion(extJad.getVersion() + 1);
			}

			jad.setUserDetails(fm.getUserDetails());
			jad.setRecordStatus(fm.getRecordStatus());
			jad.setLastMntOn(new Timestamp(System.currentTimeMillis()));
			jad.setLastMntBy(fm.getLastMntBy());
			// workflow relates
			jad.setWorkflowId(fm.getWorkflowId());
			jad.setRoleCode(fm.getRoleCode());
			jad.setNextRoleCode(fm.getNextRoleCode());
			jad.setTaskId(fm.getTaskId());
			jad.setNextTaskId(fm.getNextTaskId());

			AuditHeader auditHeader = null;

			if (PennantConstants.RECORD_TYPE_UPD.equals(jad.getRecordType())) {
				auditHeader = getAuditHeader(jad, PennantConstants.TRAN_UPD);
			} else {
				auditHeader = getAuditHeader(jad, PennantConstants.TRAN_ADD);
			}

			jointAccountDetailService.saveOrUpdate(auditHeader);
		}

		logger.debug(Literal.LEAVING);
	}

	private WSReturnStatus updateDisbursementInst(FinanceDetail fd, String type) {
		WSReturnStatus returnStatus = new WSReturnStatus();

		doProcessDisbInstructions(fd, false);

		validateDisbInstAmount(fd);

		FinScheduleData schdData = fd.getFinScheduleData();

		if (schdData.getErrorDetails() != null) {
			for (ErrorDetail errorDetail : schdData.getErrorDetails()) {
				return APIErrorHandlerService.getFailedStatus(errorDetail.getCode(), errorDetail.getError());
			}
		}

		FinanceMain fm = schdData.getFinanceMain();
		long finID = fm.getFinID();

		List<FinAdvancePayments> extAdvPayments = finAdvancePaymentsDAO.getFinAdvancePaymentsByFinRef(finID,
				TableType.TEMP_TAB.getSuffix());
		if (extAdvPayments != null && !extAdvPayments.isEmpty()) {
			finAdvancePaymentsDAO.deleteByFinRef(finID, TableType.TEMP_TAB.getSuffix());
		}

		List<AuditDetail> auditDetails = new ArrayList<AuditDetail>();
		auditDetails.addAll(finAdvancePaymentsService.saveOrUpdate(fd.getAdvancePaymentsList(), type,
				PennantConstants.TRAN_WF, fd.isDisbStp()));

		AuditHeader auditHeader = getAuditHeader(fm, PennantConstants.TRAN_WF);
		auditHeader.setAuditDetails(auditDetails);
		auditHeaderDAO.addAudit(auditHeader);

		return returnStatus;
	}

	private AuditHeader getAuditHeader(FinanceMain fm, String tranType) {
		AuditDetail auditDetail = new AuditDetail(tranType, 1, fm.getBefImage(), fm);
		return new AuditHeader(fm.getFinReference(), null, null, null, auditDetail, fm.getUserDetails(),
				new HashMap<>());
	}

	private AuditHeader getAuditHeader(DocumentDetails document, String transType) {
		AuditDetail auditDetail = new AuditDetail(transType, 1, document.getBefImage(), document);
		return new AuditHeader(document.getReferenceId(), null, null, null, auditDetail, document.getUserDetails(),
				new HashMap<String, List<ErrorDetail>>());
	}

	private AuditHeader getAuditHeader(Mandate mandate, String tranType) {
		AuditDetail auditDetail = new AuditDetail(tranType, 1, mandate.getBefImage(), mandate);
		return new AuditHeader(String.valueOf(mandate.getMandateID()), null, null, null, auditDetail,
				mandate.getUserDetails(), new HashMap<String, List<ErrorDetail>>());
	}

	private AuditHeader getAuditHeader(JointAccountDetail jad, String tranType) {
		AuditDetail auditDetail = new AuditDetail(tranType, 1, jad.getBefImage(), jad);
		return new AuditHeader(String.valueOf(jad.getJointAccountId()), null, null, null, auditDetail,
				jad.getUserDetails(), new HashMap<String, List<ErrorDetail>>());
	}

	private void prepareResponse(FinanceDetail fd) {
		FinScheduleData schdData = fd.getFinScheduleData();
		fd.setFinReference(schdData.getFinReference());

		schdData.setFinReference(null);
		schdData.setPlanEMIHDates(null);
		schdData.setPlanEMIHmonths(null);

		fd.setFinFlagsDetails(null);
		fd.setCovenantTypeList(null);
		// ReqStage and Status
		FinanceMain fm = schdData.getFinanceMain();

		long finID = fm.getFinID();

		fm.setStatus(fm.getRecordStatus());
		fm.setStage(fm.getNextRoleCode());

		if (StringUtils.isNotEmpty(fm.getDmaCode())) {
			vehicleDealer = vehicleDealerDao.getVehicleDealerById(Long.valueOf((fm.getDmaCode())), "");
			fm.setDmaCodeReference(vehicleDealer.getCode());
		}

		if (fm.getAccountsOfficer() > 0) {
			vehicleDealer = vehicleDealerDao.getVehicleDealerById(fm.getAccountsOfficer(), "");
			fm.setAccountsOfficerReference(String.valueOf(vehicleDealer.getDealerId()));
		}

		if (StringUtils.isNotEmpty(fm.getDsaCode())) {
			vehicleDealer = vehicleDealerDao.getVehicleDealerById(Long.valueOf((fm.getDsaCode())), "");
			fm.setDsaCodeReference(vehicleDealer.getCode());
		}

		if (fm.getConnector() > 0) {
			vehicleDealer = vehicleDealerDao.getVehicleDealerById(((fm.getConnector())), "");
			fm.setConnectorReference(vehicleDealer.getCode());
		}
		// disbursement Dates
		List<FinanceDisbursement> disbList = schdData.getDisbursementDetails();
		Collections.sort(disbList, new Comparator<FinanceDisbursement>() {
			@Override
			public int compare(FinanceDisbursement b1, FinanceDisbursement b2) {
				return (new Integer(b1.getDisbSeq()).compareTo(new Integer(b2.getDisbSeq())));
			}
		});

		if (disbList != null && disbList.size() > 0) {
			if (disbList.size() == 1) {
				fm.setFirstDisbDate(disbList.get(0).getDisbDate());
				fm.setLastDisbDate(disbList.get(0).getDisbDate());
			} else {
				fm.setFirstDisbDate(disbList.get(0).getDisbDate());
				fm.setLastDisbDate(disbList.get(disbList.size() - 1).getDisbDate());
			}
		}

		if (fm.isAllowGrcPeriod()) {
			fm.setGrcStartDate(fm.getFinStartDate());
		}

		List<FinFeeDetail> finFeeDetail = schdData.getFinFeeDetailList();
		fd.setFinFeeDetails(getUpdatedFees(finFeeDetail));

		for (FinAdvancePayments fap : fd.getAdvancePaymentsList()) {
			if (fap.getPaymentSeq() == 1) {
				fap.setNetDisbAmt(fm.getFinAmount().subtract(fm.getDeductFeeDisb().add(fm.getBpiAmount())));
			} else {
				fap.setNetDisbAmt(null);
			}
		}

		// Bounce and manual advice fees if applicable
		List<ManualAdvise> manualAdviseFees = manualAdviseDAO.getReceivableAdvises(finID, "_View");
		Map<String, BigDecimal> taxPercentages = GSTCalculator.getTaxPercentages(fm);
		FeeType feeType = feeTypeDAO.getApprovedFeeTypeByFeeCode("BOUNCE");
		TaxAmountSplit taxSplit;
		TaxAmountSplit taxSplit2;
		BigDecimal totalAmount = BigDecimal.ZERO;
		BigDecimal totalDue = BigDecimal.ZERO;
		if (manualAdviseFees != null && !manualAdviseFees.isEmpty()) {
			for (ManualAdvise advisedFees : manualAdviseFees) {
				FinFeeDetail feeDetail = new FinFeeDetail();
				if (advisedFees.getBounceID() > 0) {
					feeDetail.setFeeCategory(FinanceConstants.FEES_AGAINST_BOUNCE);
					feeDetail.setSchdDate(getBounceDueDate(advisedFees.getReceiptID()));
					advisedFees.setTaxComponent(feeType.getTaxComponent());
				} else {
					feeDetail.setFeeCategory(FinanceConstants.FEES_AGAINST_ADVISE);
				}
				feeDetail.setFeeTypeCode(advisedFees.getFeeTypeCode());
				feeDetail.setActualAmount(advisedFees.getAdviseAmount());
				feeDetail.setPaidAmount(advisedFees.getPaidAmount());
				feeDetail.setRemainingFee(advisedFees.getBalanceAmt());

				if (advisedFees.getWorkflowId() == 0) {
					totalAmount = feeDetail.getActualAmount().subtract(feeDetail.getPaidAmount())
							.subtract(advisedFees.getWaivedAmount());

					if (FinanceConstants.FEE_TAXCOMPONENT_EXCLUSIVE.equals(advisedFees.getTaxComponent())) {
						taxSplit = GSTCalculator.getExclusiveGST(totalAmount, taxPercentages);
						totalDue = totalDue.add(taxSplit.gettGST()).add(totalAmount);
					} else {
						totalDue = totalDue.add(totalAmount);
					}
					BigDecimal tdsAmount = BigDecimal.ZERO;

					// if tds applicable
					if (TDSCalculator.isTDSApplicable(fm, advisedFees.isTdsReq())) {
						BigDecimal taxableAmount = BigDecimal.ZERO;

						if (StringUtils.isNotEmpty(advisedFees.getTaxComponent())
								&& FinanceConstants.FEE_TAXCOMPONENT_INCLUSIVE.equals(advisedFees.getTaxComponent())) {
							taxSplit2 = GSTCalculator.getInclusiveGST(totalAmount, taxPercentages);
							taxableAmount = totalAmount.subtract(taxSplit2.gettGST());
						} else {
							taxableAmount = totalAmount;
						}

						tdsAmount = receiptCalculator.getTDSAmount(fm, taxableAmount);
						totalDue = totalDue.subtract(tdsAmount);
					}
				}
				fd.getFinFeeDetails().add(feeDetail);
			}
		}

		// Fetch summary details
		Date appDate = SysParamUtil.getAppDate();
		List<FinanceScheduleDetail> schedules = fd.getFinScheduleData().getFinanceScheduleDetails();
		FinanceSummary summary = getFinanceSummary(fd);

		summary.setOverDueAmount(totalDue.add(summary.getOverDueAmount()));
		summary.setTotalOverDueIncCharges(summary.getOverDueAmount());
		summary.setDueCharges(totalDue.add(summary.getDueCharges()));
		summary.setAdvPaymentAmount(getTotalAdvAmount(fm));
		summary.setOverDueEMI(SchdUtil.getOverDueEMI(appDate, schedules));

		schdData.setFinanceSummary(summary);

		// customer details
		CustomerDetails customerDetail = fd.getCustomerDetails();
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

	private void prepareStepInstallements(List<FinanceStepPolicyDetail> finStepDetails, int totalTerms) {
		logger.debug(Literal.ENTERING);

		int sumInstallments = 0;
		BigDecimal tenurePerc = BigDecimal.ZERO;
		BigDecimal sumTenurePerc = BigDecimal.ZERO;

		for (int i = 0; i < finStepDetails.size(); i++) {
			FinanceStepPolicyDetail detail = finStepDetails.get(i);
			if (detail.getInstallments() > 0) {
				tenurePerc = (new BigDecimal(detail.getInstallments()).multiply(new BigDecimal(100)))
						.divide(new BigDecimal(totalTerms), 2, RoundingMode.HALF_DOWN);
				detail.setTenorSplitPerc(tenurePerc);
				sumTenurePerc = sumTenurePerc.add(tenurePerc);
				sumInstallments = sumInstallments + detail.getInstallments();
				if (i == (finStepDetails.size() - 1) && sumInstallments == totalTerms) {
					if (sumTenurePerc.compareTo(new BigDecimal(100)) != 0) {
						detail.setTenorSplitPerc(
								detail.getTenorSplitPerc().add(new BigDecimal(100)).subtract(sumTenurePerc));
					}
				}
			} else {
				BigDecimal terms = detail.getTenorSplitPerc().multiply(new BigDecimal(totalTerms))
						.divide(new BigDecimal(100), 0, RoundingMode.HALF_DOWN);
				sumInstallments = sumInstallments + Integer.parseInt(terms.toString());
				detail.setInstallments(Integer.parseInt(terms.toString()));
				detail.setStepSpecifier(PennantConstants.STEP_SPECIFIER_REG_EMI);
				if (i == (finStepDetails.size() - 1)) {
					if (sumInstallments != totalTerms) {
						detail.setInstallments(detail.getInstallments() + totalTerms - sumInstallments);
					}
				}
			}
		}
		logger.debug(Literal.LEAVING);
	}

	private void doSetDefaultChequeHeader(FinanceDetail fd, boolean moveLoanStage) {
		logger.debug(Literal.ENTERING);

		if (fd.getChequeHeader() == null && !moveLoanStage) {
			FinanceMain fm = fd.getFinScheduleData().getFinanceMain();
			ChequeHeader ch = new ChequeHeader();
			ch.setNewRecord(true);
			ch.setRoleCode(fm.getNextRoleCode());
			ch.setNextRoleCode(fm.getNextRoleCode());
			ch.setTaskId(fm.getTaskId());
			ch.setNextTaskId(fm.getNextTaskId());
			ch.setVersion(1);
			ch.setLastMntBy(fm.getLastMntBy());
			ch.setLastMntOn(new Timestamp(System.currentTimeMillis()));
			ch.setRecordStatus(fm.getRecordStatus());
			ch.setRecordType(PennantConstants.RECORD_TYPE_NEW);
			ch.setWorkflowId(fm.getWorkflowId());
			ch.setFinID(fm.getFinID());
			ch.setFinReference(fm.getFinReference());
			ch.setNoOfCheques(0);
			ch.setTotalAmount(BigDecimal.ZERO);
			ch.setActive(true);
			ch.setSourceId(RequestSource.API.name());
			fd.setChequeHeader(ch);
		}

		logger.debug(Literal.LEAVING);
	}

	private Date getBounceDueDate(long receiptId) {
		Date schdDate = manualAdviseDAO.getPresentmentBounceDueDate(receiptId);
		return schdDate;
	}

	private void doEmptyResponseObject(FinanceDetail detail) {
		detail.setFinScheduleData(null);
		detail.setDocumentDetailsList(null);
		detail.setJointAccountDetailList(null);
		detail.setGurantorsDetailList(null);
		detail.setCollateralAssignmentList(null);
		detail.setFinFlagsDetails(null);
	}

	private List<ErrorDetail> adjustFees(FinScheduleData schdData) {
		List<ErrorDetail> errorDetails = schdData.getErrorDetails();
		LoggedInUser userDetails = SessionUserDetails.getUserDetails(SessionUserDetails.getLogiedInUser());

		Map<String, FinFeeDetail> feeDetailMap = new HashMap<>();
		List<Long> receiptList = new ArrayList<>();

		for (FinFeeDetail feeDetail : schdData.getFinFeeDetailList()) {
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
				.getUpFrontReceiptHeaderByExtRef(schdData.getExternalReference(), "");
		for (FinReceiptHeader header : receiptHeaderList) {
			receiptHeaderMap.put(header.getReceiptID(), header);
		}
		for (FinFeeDetail feeDtl : schdData.getFinFeeDetailList()) {// iterating
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
							schdData.getFinFeeReceipts().add(feeReceipt);
						}
					}
				}
			} /*
				 * else{//Throw error if fee code does not exist in request and break the loop
				 * 
				 * }
				 */

		}
		return errorDetails;
	}

	private List<ErrorDetail> validatePaidFinFeeDetail(FinScheduleData detail, List<FinReceiptHeader> receiptHeaderList,
			List<FinFeeDetail> fees) {
		List<ErrorDetail> errorDetails = detail.getErrorDetails();

		for (FinFeeDetail feeDetail : detail.getFinFeeDetailList()) {
			BigDecimal paidAmount = BigDecimal.ZERO;

			for (FinFeeDetail paidFee : fees) {
				if (paidFee.getFeeTypeCode().equals(feeDetail.getFeeTypeCode())) {
					paidAmount = paidAmount.add(paidFee.getPaidAmount());
				}

				feeDetail.setTransactionId(detail.getExternalReference());

				List<FinFeeReceipt> finFeeReceiptList = finFeeReceiptDAO.getFinFeeReceiptByFeeId(paidFee.getFeeID(),
						"_View");

				if (feeDetail.isAlwPreIncomization() && finFeeReceiptList.size() == 0) {
					String[] valueParm = new String[1];
					valueParm[0] = feeDetail.getFeeTypeCode() + " Paid Amount must match with already paid amount ";
					errorDetails.add(ErrorUtil.getErrorDetail(new ErrorDetail("30550", valueParm)));
					return errorDetails;
				}

				for (FinFeeReceipt finFeeReceipt : finFeeReceiptList) {
					finFeeReceipt.setRecordType("");
					for (FinReceiptHeader rh : receiptHeaderList) {
						if (rh.getReceiptID() == finFeeReceipt.getReceiptID()) {
							rh.setReceiptAmount(rh.getReceiptAmount().subtract(finFeeReceipt.getPaidAmount()));
						}
					}
				}

				detail.getFinFeeReceipts().addAll(finFeeReceiptList);
			}

			if (feeDetail.getPaidAmount().compareTo(paidAmount) != 0) {
				String[] valueParm = new String[1];
				valueParm[0] = feeDetail.getFeeTypeCode() + " Paid Amount must match with already paid amount ";
				errorDetails.add(ErrorUtil.getErrorDetail(new ErrorDetail("30550", valueParm)));
				return errorDetails;
			}
		}

		return errorDetails;
	}

	private List<ErrorDetail> adjustFeesAuto(FinScheduleData schdData) {
		List<ErrorDetail> errors = schdData.getErrorDetails();

		String extReference = schdData.getExternalReference();

		List<FinReceiptHeader> rchList = finReceiptHeaderDAO.getUpFrontReceiptHeaderByExtRef(extReference, "");
		List<FinFeeDetail> fees = finFeeDetailDAO.getTotalPaidFees(extReference, "_View");

		LoggedInUser userDetails = SessionUserDetails.getUserDetails(SessionUserDetails.getLogiedInUser());

		if (fees.size() > 0) {
			errors = validatePaidFinFeeDetail(schdData, rchList, fees);
		}

		if (CollectionUtils.isNotEmpty(errors) || CollectionUtils.isEmpty(schdData.getFinFeeDetailList())) {
			return errors;
		}

		for (FinFeeDetail feeDtl : schdData.getFinFeeDetailList()) {
			boolean flag = false;
			for (FinFeeDetail fee : fees) {
				if (fee.getFeeTypeCode().equals(feeDtl.getFeeTypeCode())) {
					flag = true;
					break;
				}
			}

			if (flag || feeDtl.getPaidAmount().compareTo(BigDecimal.ZERO) <= 0) {
				continue;
			}

			BigDecimal paidAmount = feeDtl.getPaidAmount();
			FinFeeReceipt feeReceipt = new FinFeeReceipt();

			for (FinReceiptHeader header : rchList) {
				if (header.getReceiptAmount().compareTo(BigDecimal.ZERO) <= 0) {
					continue;
				}

				if (paidAmount.compareTo(header.getReceiptAmount()) <= 0) {
					header.setReceiptAmount(header.getReceiptAmount().subtract(paidAmount));
					feeReceipt.setFeeTypeCode(feeDtl.getFeeTypeCode());
					feeReceipt.setFeeTypeId(feeDtl.getFeeTypeID());
					feeReceipt.setRecordType(PennantConstants.RCD_ADD);
					feeReceipt.setNewRecord(true);
					feeReceipt.setFeeTypeDesc(feeDtl.getFeeTypeDesc());
					feeReceipt.setLastMntBy(getLastMntBy(false, userDetails));
					feeReceipt.setReceiptID(header.getReceiptID());
					feeReceipt.setPaidAmount(paidAmount);
					paidAmount = BigDecimal.ZERO;
					break;
				} else {
					paidAmount = paidAmount.subtract(header.getReceiptAmount());
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

			if (paidAmount.compareTo(BigDecimal.ZERO) > 0) {
				String[] valueParm = new String[1];
				valueParm[0] = "Insufficient funds to adjust fees";
				errors.add(ErrorUtil.getErrorDetail(new ErrorDetail("30550", valueParm)));
				return errors;
			}

			schdData.getFinFeeReceipts().add(feeReceipt);
		}

		return errors;
	}

	private String validateTemplate(FinanceReferenceDetail frefdata) {
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
				null, auditDetail, aFinanceDetail.getUserDetails(), new HashMap<String, List<ErrorDetail>>());
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

	public WSReturnStatus processRejectFinance(FinanceDetail fd, boolean finReferenceAvailable) {
		logger.debug(Literal.ENTERING);

		WSReturnStatus returnStatus = null;

		FinanceMain fm = fd.getFinScheduleData().getFinanceMain();
		// mandatory fields
		LoggedInUser userDetails = SessionUserDetails.getUserDetails(SessionUserDetails.getLogiedInUser());
		fm.setUserDetails(userDetails);
		fm.setLastMntBy(userDetails.getUserId());
		fm.setFinSourceID(APIConstants.FINSOURCE_ID_API);
		fm.setVersion(1);
		fm.setRecordType(PennantConstants.RECORD_TYPE_NEW);
		fm.setRecordStatus(PennantConstants.RCD_STATUS_REJECTED);
		fd.setModuleDefiner(FinServiceEvent.ORG);

		// customer details
		Customer customer = customerDetailsService.getCustomerByCIF(fm.getCustCIF());
		CustomerDetails customerDetails = new CustomerDetails();
		customerDetails.setCustomer(customer);
		fd.setCustomerDetails(customerDetails);
		String tranType = PennantConstants.TRAN_WF;

		if (!finReferenceAvailable) {
			if (StringUtils.isBlank(fm.getFinReference())) {
				fm.setFinReference(String.valueOf(String
						.valueOf(ReferenceGenerator.generateFinRef(fm, fd.getFinScheduleData().getFinanceType()))));
			}
			fm.setLastMntOn(new Timestamp(System.currentTimeMillis()));
			fm.setCustID(customer.getCustID());
			fm.setEqualRepay(fm.isEqualRepay());
			fm.setRecalType(fm.getRecalType());
			fm.setLastRepayDate(fm.getFinStartDate());
			fm.setLastRepayPftDate(fm.getFinStartDate());
			fm.setLastRepayRvwDate(fm.getFinStartDate());
			fm.setLastRepayCpzDate(fm.getFinStartDate());
			fd.getFinScheduleData().setFinanceMain(fm);

			returnStatus = prepareAndExecuteAuditHeader(fd, tranType);
		} else {
			FinanceMain dbFinanceMain = financeDetailService.getFinanceMain(fm.getFinID(), "_Temp");
			if (null != dbFinanceMain
					&& !StringUtils.equals(dbFinanceMain.getRecordStatus(), PennantConstants.RCD_STATUS_REJECTED)) {
				fm.setFinReference(dbFinanceMain.getFinReference());
				fm.setLastMntOn(dbFinanceMain.getLastMntOn());
				fm.setCustID(dbFinanceMain.getCustID());
				fm.setEqualRepay(dbFinanceMain.isEqualRepay());
				fm.setRecalType(dbFinanceMain.getRecalType());
				fm.setLastRepayDate(dbFinanceMain.getLastRepayDate());
				fm.setLastRepayPftDate(dbFinanceMain.getLastRepayPftDate());
				fm.setLastRepayRvwDate(dbFinanceMain.getLastRepayRvwDate());
				fm.setLastRepayCpzDate(dbFinanceMain.getLastRepayCpzDate());

				// override received fields with fetch data
				fm.setLovDescCustCIF(dbFinanceMain.getCustCIF());
				fm.setFinType(dbFinanceMain.getFinType());
				fm.setFinAmount(dbFinanceMain.getFinAmount());
				fm.setFinAssetValue(dbFinanceMain.getFinAssetValue());
				fm.setNumberOfTerms(dbFinanceMain.getNumberOfTerms());

				fd.getFinScheduleData().setFinanceMain(fm);
				fd.setUserDetails(userDetails);
				fd.setCustomerDetails(customerDetails);
				FinanceType dbFinanceType = financeTypeService.getFinanceTypeById(dbFinanceMain.getFinType());
				fd.getFinScheduleData().setFinanceType(dbFinanceType);

				CustomerDetails dbCustomer = customerDetailsService.getApprovedCustomerById(fm.getCustID());
				fd.setCustomerDetails(dbCustomer);
				returnStatus = prepareAndExecuteAuditHeader(fd, tranType);
			} else {
				// throw validation error
				String[] valueParam = new String[1];
				valueParam[0] = "finreference: " + fd.getFinScheduleData().getFinReference();

				returnStatus = APIErrorHandlerService.getFailedStatus("90266", valueParam);
			}
		}
		logger.debug(Literal.LEAVING);

		return returnStatus;
	}

	public FinanceDetail processCancelFinance(FinanceDetail fd) {
		logger.debug(Literal.ENTERING);

		long finID = fd.getFinID();

		FinanceDetail findetail = financeDetailService.getFinanceDetailById(finID, false, "", false,
				FinServiceEvent.ORG, "");
		FinanceMain financeMain = findetail.getFinScheduleData().getFinanceMain();
		LoggedInUser userDetails = SessionUserDetails.getUserDetails(SessionUserDetails.getLogiedInUser());
		findetail.getFinScheduleData().getFinanceMain()
				.setVersion(findetail.getFinScheduleData().getFinanceMain().getVersion() + 1);
		financeMain.setUserDetails(userDetails);
		List<ErrorDetail> errorDetailList = null;
		if (CollectionUtils.isNotEmpty(fd.getExtendedDetails())) {
			String subModule = findetail.getFinScheduleData().getFinanceType().getFinCategory();
			errorDetailList = extendedFieldDetailsService.validateExtendedFieldDetails(fd.getExtendedDetails(),
					ExtendedFieldConstants.MODULE_LOAN, subModule, FinServiceEvent.CANCELFIN);

			for (ErrorDetail ed : errorDetailList) {
				fd = new FinanceDetail();
				doEmptyResponseObject(fd);
				fd.setReturnStatus(APIErrorHandlerService.getFailedStatus(ed.getCode(), ed.getError()));
				logger.debug(Literal.LEAVING);
				return fd;
			}
		}
		List<FinanceScheduleDetail> schdList = findetail.getFinScheduleData().getFinanceScheduleDetails();
		Date appDate = SysParamUtil.getAppDate();

		for (FinanceScheduleDetail curSchd : schdList) {
			if (curSchd.getSchDate().compareTo(appDate) <= 0) {
				ErrorDetail ed = ErrorUtil.getErrorDetail(
						new ErrorDetail(PennantConstants.KEY_FIELD, "60407", null, null), userDetails.getLanguage());

				fd = new FinanceDetail();
				doEmptyResponseObject(fd);
				fd.setReturnStatus(APIErrorHandlerService.getFailedStatus("60407", ed.getError()));
				logger.debug(Literal.LEAVING);
				return fd;
			}
		}
		// process Extended field details
		// Get the ExtendedFieldHeader for given module and subModule
		ExtendedFieldHeader extendedFieldHeader = extendedFieldHeaderDAO.getExtendedFieldHeaderByModuleName(
				ExtendedFieldConstants.MODULE_LOAN, financeMain.getFinCategory(), FinServiceEvent.CANCELFIN, "");
		// ### 02-05-2018-END
		findetail.setExtendedFieldHeader(extendedFieldHeader);
		List<ExtendedField> extendedFields = fd.getExtendedDetails();
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

		ReasonHeader reasonHeader = fd.getReasonHeader();
		if (reasonHeader != null) {
			if (!CollectionUtils.isEmpty(reasonHeader.getDetailsList())) {
				for (ReasonDetails reasonDetails : reasonHeader.getDetailsList()) {
					if (StringUtils.isBlank(reasonDetails.getReasonCode())) {
						String[] valueParm = new String[1];
						valueParm[0] = "reasonCode";
						ErrorDetail ed = ErrorUtil.getErrorDetail(
								new ErrorDetail(PennantConstants.KEY_FIELD, "30561", valueParm, null),
								userDetails.getLanguage());

						fd = new FinanceDetail();
						doEmptyResponseObject(fd);
						fd.setReturnStatus(APIErrorHandlerService.getFailedStatus("60407", ed.getError()));
						logger.debug(Literal.LEAVING);
						return fd;
					}
					ReasonCode details = reasonDetailDAO.getCancelReasonByCode(reasonDetails.getReasonCode(), "_AView");
					if (details != null) {
						reasonDetails.setReasonId(details.getReasonCategoryID());
					} else {
						String[] valueParm = new String[2];
						valueParm[0] = " reasonCode";
						valueParm[1] = reasonDetails.getReasonCode();
						ErrorDetail ed = ErrorUtil.getErrorDetail(
								new ErrorDetail(PennantConstants.KEY_FIELD, "90224", valueParm, null),
								userDetails.getLanguage());
						fd = new FinanceDetail();
						doEmptyResponseObject(fd);
						fd.setReturnStatus(APIErrorHandlerService.getFailedStatus("60407", ed.getError()));
						logger.debug(Literal.LEAVING);
						return fd;
					}
				}
				financeMain.setDetailsList(reasonHeader.getDetailsList());
				financeMain.setCancelRemarks(reasonHeader.getRemarks());
			}
		}
		AuditDetail auditDetail = new AuditDetail(PennantConstants.TRAN_WF, 1, null, findetail);
		AuditHeader auditHeader = new AuditHeader(findetail.getFinReference(), null, null, null, auditDetail,
				findetail.getFinScheduleData().getFinanceMain().getUserDetails(),
				new HashMap<String, List<ErrorDetail>>());

		APIHeader reqHeaderDetails = (APIHeader) PhaseInterceptorChain.getCurrentMessage().getExchange()
				.get(APIHeader.API_HEADER_KEY);
		auditHeader.setApiHeader(reqHeaderDetails);

		auditHeader = financeCancellationService.doApprove(auditHeader, true);
		if (auditHeader.getOverideMessage() != null && auditHeader.getOverideMessage().size() > 0) {
			for (ErrorDetail ed : auditHeader.getOverideMessage()) {
				fd = new FinanceDetail();
				doEmptyResponseObject(fd);
				fd.setReturnStatus(APIErrorHandlerService.getFailedStatus(ed.getCode(), ed.getError()));
				logger.debug(Literal.LEAVING);
				return fd;
			}
		}

		if (auditHeader.getErrorMessage() != null) {
			for (ErrorDetail ed : auditHeader.getErrorMessage()) {
				fd = new FinanceDetail();
				doEmptyResponseObject(fd);
				fd.setReturnStatus(APIErrorHandlerService.getFailedStatus(ed.getCode(), ed.getError()));
				logger.debug(Literal.LEAVING);
				return fd;
			}
		}

		if (auditHeader.getAuditDetail().getErrorDetails() != null) {
			for (ErrorDetail ed : auditHeader.getAuditDetail().getErrorDetails()) {
				fd = new FinanceDetail();
				doEmptyResponseObject(fd);
				fd.setReturnStatus(APIErrorHandlerService.getFailedStatus(ed.getCode(), ed.getError()));
				logger.debug(Literal.LEAVING);
				return fd;
			}
		}
		logger.debug(Literal.LEAVING);

		fd = new FinanceDetail();
		doEmptyResponseObject(fd);
		FinScheduleData finScheduleData = new FinScheduleData();
		finScheduleData.setRepayInstructions(null);
		finScheduleData.setRateInstruction(null);
		finScheduleData.setFeeDues(null);
		finScheduleData.setFinFeeDetailList(null);
		finScheduleData.setStepPolicyDetails(null);
		finScheduleData.setFinanceScheduleDetails(null);
		finScheduleData.setApiPlanEMIHDates(null);
		finScheduleData.setApiplanEMIHmonths(null);
		finScheduleData.setVasRecordingList(null);
		finScheduleData.setFinODDetails(null);
		finScheduleData.setFinODPenaltyRate(null);
		fd.setFinScheduleData(finScheduleData);
		fd.getFinScheduleData().setOldFinReference(fd.getFinScheduleData().getOldFinReference());
		fd.setReturnStatus(APIErrorHandlerService.getSuccessStatus());
		return fd;
	}

	public FinanceDetail doReInitiateFinance(FinanceDetail afd) {
		logger.debug(Literal.ENTERING);

		try {
			FinanceDetail fd = getFinanceDetails(afd.getFinID());

			FinScheduleData schdData = fd.getFinScheduleData();
			FinanceMain fm = schdData.getFinanceMain();

			String finReference = ReferenceGenerator.generateFinRef(fm, schdData.getFinanceType());

			long finID = fm.getFinID();

			fm.setFinReference(finReference);

			fm.setOldFinReference(afd.getFinScheduleData().getOldFinReference());
			fm.setExtReference(afd.getFinScheduleData().getExternalReference());
			fm.setFinIsActive(true);
			fm.setClosingStatus("");
			LoggedInUser userDetails = SessionUserDetails.getUserDetails(SessionUserDetails.getLogiedInUser());
			fd.setUserDetails(userDetails);
			fm.setLastMntBy(userDetails.getUserId());
			fm.setFinSourceID(APIConstants.FINSOURCE_ID_API);
			fm.setVersion(1);
			fm.setRecordType(PennantConstants.RECORD_TYPE_NEW);
			fm.setRecordStatus(PennantConstants.RCD_STATUS_APPROVED);
			afd.setModuleDefiner(FinServiceEvent.ORG);
			fd.setModuleDefiner(FinServiceEvent.ORG);
			fm.setRecordType(PennantConstants.RECORD_TYPE_NEW);

			schdData.setFinID(finID);
			schdData.setFinReference(finReference);

			for (FinanceScheduleDetail schd : schdData.getFinanceScheduleDetails()) {
				schd.setFinID(finID);
				schd.setFinReference(finReference);
			}

			for (FinFeeDetail fee : schdData.getFinFeeDetailList()) {
				fee.setFinID(finID);
				fee.setFinReference(finReference);
				fee.setFeeID(Long.MIN_VALUE);
				fee.setRecordType(PennantConstants.RECORD_TYPE_NEW);
				fee.setNewRecord(true);
				if (fee.getTaxHeader() != null) {
					fee.getTaxHeader().setHeaderId(Long.MIN_VALUE);
					for (Taxes tax : fee.getTaxHeader().getTaxDetails()) {
						tax.setId(Long.MIN_VALUE);
					}
				}
			}

			for (FinAdvancePayments fap : fd.getAdvancePaymentsList()) {
				fap.setFinID(finID);
				fap.setFinReference(finReference);
				fap.setpOIssued(false);
				fap.setRecordType(PennantConstants.RECORD_TYPE_NEW);
				fap.setRecordStatus(PennantConstants.RCD_STATUS_APPROVED);
				fap.setNewRecord(true);
				fap.setLastMntOn(new Timestamp(System.currentTimeMillis()));
				fap.setPaymentId(Long.MIN_VALUE);

			}

			// setting the values for the co_applicant
			List<JointAccountDetail> jointAccountDetailList = fd.getJointAccountDetailList();

			for (JointAccountDetail jad : jointAccountDetailList) {
				jad.setFinID(finID);
				jad.setFinReference(finReference);
				jad.setId(Long.MIN_VALUE);
				jad.setRecordType(PennantConstants.RECORD_TYPE_NEW);
				jad.setNewRecord(true);
			}

			// process Extended field details
			// Get the ExtendedFieldHeader for given module and subModule
			ExtendedFieldHeader extendedFieldHeader = extendedFieldHeaderDAO.getExtendedFieldHeaderByModuleName(
					ExtendedFieldConstants.MODULE_LOAN, fm.getFinCategory(), FinServiceEvent.ORG, "");
			// ### 02-05-2018-END
			fd.setExtendedFieldHeader(extendedFieldHeader);
			List<ExtendedField> extendedFields = fd.getExtendedDetails();
			if (extendedFieldHeader != null) {
				int seqNo = 0;
				ExtendedFieldRender exdFieldRender = new ExtendedFieldRender();
				exdFieldRender.setReference(fm.getFinReference());
				exdFieldRender.setLastMntOn(new Timestamp(System.currentTimeMillis()));
				exdFieldRender.setRecordStatus(PennantConstants.RCD_STATUS_APPROVED);
				exdFieldRender.setLastMntBy(fd.getUserDetails().getUserId());
				exdFieldRender.setSeqNo(++seqNo);
				exdFieldRender.setNewRecord(true);
				exdFieldRender.setRecordType(PennantConstants.RECORD_TYPE_NEW);
				exdFieldRender.setVersion(1);
				exdFieldRender.setTypeCode(fd.getExtendedFieldHeader().getSubModuleName());

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

				fd.setExtendedFieldRender(exdFieldRender);
			}

			fd.setPromotion(promotionDAO.getPromotionByReferenceId(fm.getPromotionSeqId(), "_AView"));
			AuditDetail auditDetail = new AuditDetail(PennantConstants.TRAN_WF, 1, null, fd);
			AuditHeader auditHeader = new AuditHeader(fd.getFinReference(), null, null, null, auditDetail,
					schdData.getFinanceMain().getUserDetails(), new HashMap<String, List<ErrorDetail>>());

			APIHeader reqHeaderDetails = (APIHeader) PhaseInterceptorChain.getCurrentMessage().getExchange()
					.get(APIHeader.API_HEADER_KEY);
			auditHeader.setApiHeader(reqHeaderDetails);

			auditHeader = financeDetailService.doApprove(auditHeader, false);

			if (auditHeader.getOverideMessage() != null && auditHeader.getOverideMessage().size() > 0) {
				for (ErrorDetail errorDetail : auditHeader.getOverideMessage()) {
					fd = new FinanceDetail();
					fd.setReturnStatus(
							APIErrorHandlerService.getFailedStatus(errorDetail.getCode(), errorDetail.getError()));
					return fd;
				}
			}
			if (auditHeader.getErrorMessage() != null) {
				for (ErrorDetail errorDetail : auditHeader.getErrorMessage()) {
					fd = new FinanceDetail();
					fd.setReturnStatus(
							APIErrorHandlerService.getFailedStatus(errorDetail.getCode(), errorDetail.getError()));
					return fd;
				}
			}

			if (auditHeader.getAuditDetail().getErrorDetails() != null) {
				for (ErrorDetail errorDetail : auditHeader.getAuditDetail().getErrorDetails()) {
					fd = new FinanceDetail();
					fd.setReturnStatus(
							APIErrorHandlerService.getFailedStatus(errorDetail.getCode(), errorDetail.getError()));
					return fd;

				}
			}

			if (StringUtils.isNotBlank(finReference)) {
				// prepare response object
				fd = getFinanceDetailResponse(auditHeader);
				fd.setStp(false);
				fd.setReturnStatus(APIErrorHandlerService.getSuccessStatus());

				logger.debug(Literal.LEAVING);
				return fd;
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

	public WSReturnStatus doMoveLoanStage(FinanceDetail fd, MoveLoanStageRequest moveLoanStageRequest) {
		logger.debug(Literal.ENTERING);

		try {
			FinScheduleData schdData = fd.getFinScheduleData();
			FinanceMain fm = schdData.getFinanceMain();
			long finID = fm.getFinID();

			ChequeHeader chequeHeader = chequeHeaderService.getChequeHeaderByRef(finID);
			chequeHeader.setSourceId(RequestSource.API.name());
			fd.setChequeHeader(chequeHeader);

			LoggedInUser userDetails = SessionUserDetails.getUserDetails(SessionUserDetails.getLogiedInUser());
			String finEvent = FinServiceEvent.ORG;
			FinanceWorkFlow financeWorkFlow = financeWorkFlowService.getApprovedFinanceWorkFlowById(fm.getFinType(),
					finEvent, PennantConstants.WORFLOW_MODULE_FINANCE);
			WorkFlowDetails workFlowDetails = null;
			if (financeWorkFlow != null) {
				workFlowDetails = WorkFlowUtil.getDetailsByType(financeWorkFlow.getWorkFlowType());
				workFlow = new WorkflowEngine(workFlowDetails.getWorkFlowXml());
			}

			doSetRequiredDetails(fd, false, userDetails, false, false, true);
			if (fd.getExtendedFieldRender() != null) {
				if (fd.getExtendedFieldRender().getMapValues() != null) {
					fd.getExtendedFieldRender().getMapValues().remove("instructionuid");
				}
			}
			fd.setNewRecord(false);
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
			fm.setServiceName("MoveLoanStage");

			AuditDetail auditDetail = new AuditDetail(PennantConstants.TRAN_WF, 1, null, fd);
			AuditHeader auditHeader = new AuditHeader(fd.getFinReference(), null, null, null, auditDetail,
					fm.getUserDetails(), new HashMap<String, List<ErrorDetail>>());

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

	@SuppressWarnings("deprecation")
	private void doSetDueDate(FinanceMain financeMain) {
		logger.debug(Literal.ENTERING);

		Map<String, Object> declaredMap = financeMain.getDeclaredFieldValues();
		declaredMap.put("fm_finStartDate", new SimpleDateFormat("dd-MM-yyyy").format(financeMain.getFinStartDate()));
		declaredMap.put("fm_finType ", financeMain.getFinType());

		int result = 0;
		try {
			Rule rule = ruleDAO.getRuleByID(RuleConstants.MODULE_DUEDATERULE, RuleConstants.MODULE_DUEDATERULE,
					RuleConstants.EVENT_DUEDATERULE, "");
			if (rule != null) {
				result = (Integer) RuleExecutionUtil.executeRule(rule.getSQLRule(), declaredMap,
						financeMain.getFinCcy(), RuleReturnType.INTEGER);

				Date nextRepayDate = financeMain.getFinStartDate();
				nextRepayDate = DateUtility.addMonths(nextRepayDate, 1);
				Date maturityDate = financeMain.getMaturityDate();

				if (result != 0) {
					nextRepayDate.setDate(result);
					financeMain.setNextRepayDate(nextRepayDate);
					financeMain.setNextRepayPftDate(nextRepayDate);
					maturityDate.setDate(result);
					financeMain.setMaturityDate(maturityDate);
					String frq = String.valueOf(result);
					if (frq.length() > 1) {
						frq = "M00" + frq;
					} else {
						frq = "M000" + frq;
					}
					financeMain.setRepayPftFrq(frq);
					financeMain.setRepayFrq(frq);
				}

				/*
				 * if (result == 2) { nextRepayDate.setDate(result); financeMain.setNextRepayDate(nextRepayDate);
				 * financeMain.setNextRepayPftDate(nextRepayDate); financeMain.setRepayPftFrq("M0002");
				 * financeMain.setRepayFrq("M0002"); } else if (result == 15) { nextRepayDate.setDate(result);
				 * financeMain.setNextRepayDate(nextRepayDate); financeMain.setNextRepayPftDate(nextRepayDate);
				 * financeMain.setRepayPftFrq("M0015"); financeMain.setRepayFrq("M0015"); }
				 */
			}
		} catch (Exception e) {
			APIErrorHandlerService.logUnhandledException(e);
			logger.error(Literal.EXCEPTION, e);
			result = 0;
		}
		logger.debug(Literal.LEAVING);

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

		if (agrementDef == null) {
			String[] valueParm = new String[1];
			valueParm[0] = agreementType;
			details.setReturnStatus((APIErrorHandlerService.getFailedStatus("RU0040", valueParm)));
			agreements.add(details);
			return agreements;
		}

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
			String templateName = "";

			templateName = agrementDef.getAggReportName();
			AgreementEngine engine = new AgreementEngine();
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

	public WSReturnStatus updateDeviationStatus(FinanceDeviations deviation) {
		logger.debug(Literal.ENTERING);

		long finID = deviation.getFinID();
		String finReference = deviation.getFinReference();
		long deviationId = deviation.getDeviationId();

		WSReturnStatus response = new WSReturnStatus();
		List<FinanceDeviations> list = new ArrayList<>();

		FinanceDeviations aDeviation = deviationDetailsService.getFinanceDeviationsByIdAndFinRef(finID, deviationId,
				"_View");

		if (aDeviation == null) {
			String[] valueParm = new String[1];
			valueParm[0] = finReference + " and " + deviationId;
			return APIErrorHandlerService.getFailedStatus("90266", valueParm);

		}

		LoggedInUser userDetails = SessionUserDetails.getUserDetails(SessionUserDetails.getLogiedInUser());
		aDeviation.setFinReference(finReference);
		aDeviation.setDeviationId(deviationId);
		aDeviation.setApprovalStatus(deviation.getApprovalStatus());
		aDeviation.setLastMntBy(userDetails.getUserId());
		aDeviation.setLastMntOn(new Timestamp(System.currentTimeMillis()));
		aDeviation.setDelegatedUserId(String.valueOf(userDetails.getUserId()));

		if (StringUtils.isNotBlank(deviation.getDelegationRole())) {
			aDeviation.setDelegationRole(deviation.getDelegationRole());
		}

		list.add(aDeviation);

		try {
			if (StringUtils.isNotBlank(deviation.getDelegationRole())) {
				deviationDetailsService.processDevaitions(finID, list, getAuditHeader(finReference));
			} else {
				deviationDetailsService.processApproval(list, getAuditHeader(finReference), finID);
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

	private void prepareCheques(FinanceDetail fd, LoggedInUser loggedInUser, boolean stp, boolean moveLoanStage) {
		ChequeHeader ch = fd.getChequeHeader();

		FinScheduleData schdData = fd.getFinScheduleData();
		FinanceMain fm = schdData.getFinanceMain();

		ch.setTotalAmount(BigDecimal.ZERO);
		ch.setFinID(fm.getFinID());
		ch.setFinReference(fm.getFinReference());
		ch.setActive(true);
		ch.setRecordType(PennantConstants.RECORD_TYPE_NEW);
		ch.setLastMntBy(loggedInUser.getUserId());
		ch.setRecordStatus(moveLoanStage ? fm.getRecordStatus() : getRecordStatus(fm.isQuickDisb(), stp));
		ch.setVersion(1);
		ch.setLastMntOn(new Timestamp(System.currentTimeMillis()));
		ch.setTaskId(fm.getTaskId());
		ch.setNextTaskId(fm.getNextTaskId());
		ch.setRoleCode(fm.getRoleCode());
		ch.setNextRoleCode(fm.getNextRoleCode());
		ch.setWorkflowId(fm.getWorkflowId());
		ch.setNewRecord(true);

		BigDecimal totalChequeAmount = BigDecimal.ZERO;
		int serialNum = Integer.valueOf(ch.getChequeSerialNumber());

		List<ChequeDetail> cheques = ch.getChequeDetailList();

		String ccy = SysParamUtil.getValueAsString(PennantConstants.LOCAL_CCY);

		for (ChequeDetail cheque : cheques) {
			serialNum = serialNum + 1;
			cheque.setChequeSerialNumber(StringUtils.leftPad("" + serialNum, 6, "0"));
			cheque.setBankBranchID(ch.getBankBranchID());
			cheque.setAccHolderName(ch.getAccHolderName());
			cheque.setAccountNo(ch.getAccountNo());
			cheque.setStatus(ChequeSatus.NEW);
			cheque.setChequeStatus(ChequeSatus.NEW);
			cheque.setChequeCcy(ccy);
			cheque.setActive(true);
			cheque.setRecordType(PennantConstants.RECORD_TYPE_NEW);
			cheque.setLastMntBy(loggedInUser.getUserId());
			cheque.setRecordStatus(moveLoanStage ? fm.getRecordStatus() : getRecordStatus(fm.isQuickDisb(), stp));
			cheque.setVersion(2);
			cheque.setLastMntOn(new Timestamp(System.currentTimeMillis()));
			cheque.setTaskId(fm.getTaskId());
			cheque.setNextTaskId(fm.getNextTaskId());
			cheque.setRoleCode(fm.getRoleCode());
			cheque.setNextRoleCode(fm.getNextRoleCode());
			cheque.setWorkflowId(fm.getWorkflowId());
			cheque.setNewRecord(true);

			totalChequeAmount = totalChequeAmount.add(cheque.getAmount());
		}

		ch.setTotalAmount(totalChequeAmount);
	}

	public LoanAuthentication getAuthenticationDetails(LoanAuthentication reqLa) {
		logger.debug(Literal.ENTERING);

		LoanAuthentication response = new LoanAuthentication();

		String finReference = reqLa.getFinReference();
		FinanceMain fm = financeMainDAO.getBasicDetails(finReference, TableType.BOTH_TAB);

		if (fm == null) {
			response.setReturnStatus(getFailedStatus("90260", "FinReference"));
			return response;
		}

		Date custDOb = customerDAO.getCustomerDOBByCustID(fm.getCustID());

		if (DateUtil.compare(DateUtil.getDatePart(custDOb), DateUtil.getDatePart(reqLa.getDateOfBirth())) != 0) {
			response.setValidFlag(PennantConstants.NO);
			logger.debug(Literal.LEAVING);
			return response;
		}

		Date businessDate = reqLa.getAppDate();
		if (businessDate.compareTo(fm.getMaturityDate()) >= 0) {
			businessDate = DateUtil.addDays(fm.getMaturityDate(), -1);
		}

		List<FinanceScheduleDetail> schedules = financeScheduleDetailDAO.getFinSchedules(fm.getFinID(),
				TableType.MAIN_TAB);

		BigDecimal loanEMI = SchdUtil.getNextEMI(businessDate, schedules);

		if (loanEMI.compareTo(reqLa.getLoanEMI()) != 0) {
			response.setValidFlag(PennantConstants.NO);
			logger.debug(Literal.LEAVING);
			return response;
		}

		response.setMobileEmailId("");
		response.setValidFlag(PennantConstants.YES);

		logger.debug(Literal.LEAVING);

		return response;

	}

	public List<PaymentMode> getPDCEnquiry(FinanceMain fm) {
		logger.debug(Literal.ENTERING);

		long finID = fm.getFinID();
		String bounceReason = null;
		WSReturnStatus returnStatus;
		Long mandateID = fm.getMandateID();
		Long secMandateID = fm.getSecurityMandateID();
		PaymentMode response = new PaymentMode();
		Date appDate = SysParamUtil.getAppDate();
		List<PaymentMode> paymentModeList = new ArrayList<>();

		List<FinanceScheduleDetail> fsdList = SchdUtil
				.sort(financeScheduleDetailDAO.getFinScheduleDetails(finID, "", false));
		List<ChequeDetail> chequeDetailList = chequeDetailDAO.getChequeDetailsByFinReference(fm.getFinReference(),
				"_AView");
		boolean isEmpty = CollectionUtils.isEmpty(chequeDetailList);

		Mandate mandate = null;
		if (mandateID != null) {
			mandate = mandateDAO.getMandateById(mandateID, "_AView");
		}

		for (FinanceScheduleDetail fsd : fsdList) {
			if (isEmpty && mandate == null) {
				returnStatus = APIErrorHandlerService.getFailedStatus("MAND100",
						"Mandate and PDC does not exist for the given LAN");
				response.setReturnStatus(returnStatus);
				paymentModeList.add(response);
				break;
			}

			if (!fsd.isRepayOnSchDate()) {
				continue;
			}

			if (!CollectionUtils.isEmpty(chequeDetailList)) {
				for (ChequeDetail cd : chequeDetailList) {
					if (cd.geteMIRefNo() != fsd.getInstNumber()) {
						continue;
					}

					cd.setSchdDate(fsd.getSchDate());
					String chequeSerialNo = Integer.toString(cd.getChequeSerialNo());
					Long receiptId = finReceiptHeaderDAO.getReceiptIdByChequeSerialNo(chequeSerialNo);
					if (receiptId != null) {
						bounceReason = bounceReasonDAO.getReasonByReceiptId(receiptId);
						cd.setChequeBounceReason(bounceReason);
					}
					response = preparePaymentMode(cd);
					paymentModeList.add(response);
					continue;
				}
			}

			if (mandate == null) {
				continue;
			}

			if (mandate.isSwapIsActive() && fsd.getSchDate().compareTo(mandate.getSwapEffectiveDate()) >= 0
					&& mandate.getMandateID() == mandateID) {
				List<Mandate> mandatesForAutoSwap = mandateDAO.getMandatesForAutoSwap(fm.getCustID(), appDate);
				if (!CollectionUtils.isEmpty(mandatesForAutoSwap)) {
					mandate = mandatesForAutoSwap.get(0);
				}

			}
			mandate.setSchdDate(fsd.getSchDate());
			mandate.setInstalmentNo(fsd.getInstNumber());
			response = preparePaymentMode(mandate);
			paymentModeList.add(response);
		}

		if (secMandateID != null) {
			Mandate secMandate = mandateDAO.getMandateById(secMandateID, "_AView");
			response = preparePaymentMode(secMandate);
			paymentModeList.add(response);
		}

		logger.debug(Literal.LEAVING);
		return paymentModeList;
	}

	public List<PaymentMode> getPDCDetails(FinanceMain fm) {
		logger.debug(Literal.ENTERING);

		WSReturnStatus returnStatus;
		long finID = fm.getFinID();
		Long mandateID = fm.getMandateID();
		PaymentMode response = new PaymentMode();
		Date appDate = SysParamUtil.getAppDate();
		List<PaymentMode> paymentModeList = new ArrayList<>();

		List<FinanceScheduleDetail> fsdList = SchdUtil
				.sort(financeScheduleDetailDAO.getFinScheduleDetails(finID, "", false));
		List<ChequeDetail> chequeDetailList = chequeDetailDAO.getChequeDetailsByFinReference(fm.getFinReference(),
				"_AView");
		boolean isEmpty = CollectionUtils.isEmpty(chequeDetailList);

		Mandate mandate = null;
		if (mandateID != null) {
			mandate = mandateDAO.getMandateById(mandateID, "_AView");
		}

		for (FinanceScheduleDetail fsd : fsdList) {
			if (isEmpty && mandate == null) {
				returnStatus = APIErrorHandlerService.getFailedStatus("MAND100",
						"Mandate and PDC does not exist for the given LAN");
				response.setReturnStatus(returnStatus);
				paymentModeList.add(response);
				break;
			}
			if (appDate.compareTo(fsd.getSchDate()) <= 0 && fsd.isRepayOnSchDate()) {
				if (isEmpty) {
					for (ChequeDetail cd : chequeDetailList) {
						if (cd.geteMIRefNo() != fsd.getInstNumber()) {
							continue;
						}
						cd.setSchdDate(fsd.getSchDate());
						response = preparePaymentMode(cd);
						paymentModeList.add(response);
						continue;
					}
				}

				if (mandate == null) {
					continue;
				}

				if (mandate.isSwapIsActive() && fsd.getSchDate().compareTo(mandate.getSwapEffectiveDate()) >= 0
						&& mandate.getMandateID() == mandateID) {
					List<Mandate> mandatesForAutoSwap = mandateDAO.getMandatesForAutoSwap(fm.getCustID(), appDate);
					if (!CollectionUtils.isEmpty(mandatesForAutoSwap)) {
						mandate = mandatesForAutoSwap.get(0);
					}

				}
				mandate.setSchdDate(fsd.getSchDate());
				mandate.setInstalmentNo(fsd.getInstNumber());
				response = preparePaymentMode(mandate);
				paymentModeList.add(response);
			}
		}
		logger.debug(Literal.LEAVING);
		return paymentModeList;
	}

	private PaymentMode preparePaymentMode(ChequeDetail cd) {
		PaymentMode response = new PaymentMode();

		response.setLoanInstrumentMode(cd.getChequeType());
		response.setLoanDueDate(cd.getSchdDate());
		response.setBankName(cd.getBankName());
		response.setBankCityName(cd.getCity());
		response.setMicr(cd.getMicr());
		response.setBankBranchName(cd.getBankName());
		response.setAccountNo(cd.getAccountNo());
		response.setAccountHolderName(cd.getAccHolderName());
		response.setAccountType(cd.getAccountType());
		response.setInstallmentNo(cd.geteMIRefNo());
		response.setPdcType(InstrumentType.isPDC(cd.getChequeType()) ? "Normal" : "Security");
		response.setChqDate(cd.getChequeDate());
		response.setChqNo(cd.getChequeSerialNumber());
		response.setChqStatus(cd.getChequeStatus());
		response.setBounceReason(cd.getChequeBounceReason());

		return response;

	}

	private PaymentMode preparePaymentMode(Mandate mndt) {
		PaymentMode response = new PaymentMode();

		response.setLoanInstrumentMode(mndt.getMandateType());
		response.setLoanDueDate(mndt.getSchdDate());
		response.setBankName(mndt.getBankName());
		response.setBankCityName(mndt.getCity());
		response.setMicr(mndt.getMICR());
		response.setBankBranchName(mndt.getBankName());
		response.setAccountNo(mndt.getAccNumber());
		response.setAccountHolderName(mndt.getAccHolderName());
		response.setAccountType(mndt.getAccType());
		response.setInstallmentNo(mndt.getInstalmentNo());

		return response;

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

	public Map<String, String> getUserActions(FinanceMain fm) {
		Map<String, String> userActionMap = new HashMap<String, String>();

		String finEvent = FinServiceEvent.ORG;
		FinanceWorkFlow financeWorkFlow = financeWorkFlowService.getApprovedFinanceWorkFlowById(fm.getFinType(),
				finEvent, PennantConstants.WORFLOW_MODULE_FINANCE);
		WorkFlowDetails workFlowDetails = null;
		if (financeWorkFlow != null) {
			workFlowDetails = WorkFlowUtil.getDetailsByType(financeWorkFlow.getWorkFlowType());
			workFlow = new WorkflowEngine(workFlowDetails.getWorkFlowXml());
		}

		String userAtions = workFlow.getUserActionsAsString(workFlow.getUserTaskId(fm.getNextRoleCode()), null);

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

	public void setReceiptCalculator(ReceiptCalculator receiptCalculator) {
		this.receiptCalculator = receiptCalculator;
	}

	public void setFeeTypeDAO(FeeTypeDAO feeTypeDAO) {
		this.feeTypeDAO = feeTypeDAO;
	}

	@Autowired
	public void setVehicleDealerDao(VehicleDealerDAO vehicleDealerDao) {
		this.vehicleDealerDao = vehicleDealerDao;
	}

	public void setPromotionDAO(PromotionDAO promotionDAO) {
		this.promotionDAO = promotionDAO;
	}

	public void setCovenantTypeDAO(CovenantTypeDAO covenantTypeDAO) {
		this.covenantTypeDAO = covenantTypeDAO;
	}

	public void setRuleDAO(RuleDAO ruleDAO) {
		this.ruleDAO = ruleDAO;
	}

	@Autowired
	public void setFinFeeDetailService(FinFeeDetailService finFeeDetailService) {
		this.finFeeDetailService = finFeeDetailService;
	}

	@Autowired
	public void setFinanceProfitDetailDAO(FinanceProfitDetailDAO financeProfitDetailDAO) {
		this.financeProfitDetailDAO = financeProfitDetailDAO;
	}

	@Autowired
	public void setCustomerDAO(CustomerDAO customerDAO) {
		this.customerDAO = customerDAO;
	}

	@Autowired
	public void setChequeDetailDAO(ChequeDetailDAO chequeDetailDAO) {
		this.chequeDetailDAO = chequeDetailDAO;
	}

	@Autowired
	public void setBounceReasonDAO(BounceReasonDAO bounceReasonDAO) {
		this.bounceReasonDAO = bounceReasonDAO;
	}

	@Autowired
	public void setMandateDAO(MandateDAO mandateDAO) {
		this.mandateDAO = mandateDAO;
	}

}
